package spring25web.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 指定された元ConnectionPoolDataSourceから物理コネクションをプールし、
 * コネクションが要求された場合にプールから物理コネクションを返すデータソース.
 * データベースのJDBCドライバと、アプリケーションが使用するデータソースとの中間層となる.
 */
public class SimpleConnectionPoolDataSourceImpl implements DataSource {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 物理コネクションを保持するホルダークラス. 保持した時刻から期限切れか判定する.
	 */
	protected static class PooledConnectionCache {

		/**
		 * 物理コネクション
		 */
		public final PooledConnection poolConn;

		/**
		 * 保持を開始した時刻
		 */
		public final long lastUse;

		public PooledConnectionCache(PooledConnection poolConn, long lastUse) {
			this.poolConn = poolConn;
			this.lastUse = lastUse;
		}

		/**
		 * 指定した時刻より以前 = 期限切れであるか判定する.
		 *
		 * @param limit
		 *            期限時刻
		 * @return 期限切れであればtrue
		 */
		public boolean isExpired(long limit) {
			return lastUse <= limit;
		}
	}

	/**
	 * コネクションプールデータソース (元データソース)
	 */
	private ConnectionPoolDataSource ds;

	/**
	 * 物理コネクションのプール
	 */
	private final ConcurrentLinkedQueue<PooledConnectionCache> caches = new ConcurrentLinkedQueue<PooledConnectionCache>();

	/**
	 * クローズ済みか?
	 */
	private volatile boolean closed = false;

	/**
	 * キャッシュタイム
	 */
	private long cacheLifeTime = 60 * 1000;

	/**
	 * 最大キャッシュ数
	 */
	private int cacheLimit = 10;

	/**
	 * 期限切れキャッシュを掃除するタイマー
	 */
	private Timer purgeScheduler;

	/**
	 * 期限切れキャッシュを掃除する間隔
	 */
	static long purgeInterval = 10 * 1000;

	/**
	 * コンストラクタ
	 *
	 * @param ds
	 *            元データソース
	 */
	public SimpleConnectionPoolDataSourceImpl(ConnectionPoolDataSource ds) {
		if (ds == null) {
			throw new IllegalArgumentException();
		}
		this.ds = ds;

		this.purgeScheduler = new Timer(true);
		this.purgeScheduler.schedule(new TimerTask() {
			@Override
			public void run() {
				purge();
			}
		}, purgeInterval, purgeInterval);
	}

	/**
	 * 元データソースに関連づけられたログライタを取得する. ただし、ログライタがnullの場合はSystem.errをログライタとして使用する.
	 *
	 * @return ログライタ、非nullとなる.
	 */
	protected PrintWriter getSafeLogWriter() {
		PrintWriter logWriter;
		try {
			logWriter = getLogWriter();
		} catch (SQLException ex) {
			logWriter = null;
		}
		if (logWriter == null) {
			logWriter = new PrintWriter(System.err);
		}
		return logWriter;
	}

	/**
	 * プールされている全ての物理コネクションをクローズし、 プールを空にする. ただし、現在使用中のコネクションは解放されない.
	 *
	 * @throws Exception
	 */
	public void close() {
		PrintWriter logWriter = getSafeLogWriter();

		// クローズ済みをマーク
		closed = true;

		// タイマーを停止
		purgeScheduler.cancel();

		// 現在のコネクションをすべて解放する.
		PooledConnectionCache conn;
		while ((conn = caches.poll()) != null) {
			try {
				close(conn.poolConn);

			} catch (Exception ex) {
				ex.printStackTrace(logWriter);
			}
		}
		assert caches.isEmpty();
	}

	/**
	 * コネクションプールから期限切れのものを除去する.
	 *
	 * @return アクティブなコネクション数
	 */
	public int purge() {
		// 有効なコネクション保持数
		int cnt = 0;

		// 有効期限切れの時刻を取得
		long limit = System.currentTimeMillis() - cacheLifeTime;

		PrintWriter logWriter = getSafeLogWriter();

		// 期限切れのものを除去
		for (Iterator<PooledConnectionCache> ite = caches.iterator(); ite
				.hasNext();) {
			PooledConnectionCache conn = ite.next();
			if (conn.isExpired(limit)) {
				// 期限切れのコネクションをクローズする
				try {
					close(conn.poolConn);

				} catch (SQLException ex) {
					ex.printStackTrace(logWriter);
				}
				// クローズの成否にかかわらず除去する.
				ite.remove();

			} else {
				// 有効なコネクション保持数をカウント
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * 物理接続に対して論理接続が閉じられた場合にプールに返却するようにリスナを設定する.
	 *
	 * @param conn
	 *            物理接続
	 * @param pool
	 *            返却先となるプール
	 */
	protected void setupRecycleHandler(PooledConnection conn) {
		assert conn != null;
		conn.addConnectionEventListener(new ConnectionEventListener() {
			@Override
			public void connectionClosed(ConnectionEvent event) {
				// 使用済みコネクションはプールに戻す.
				boolean nocache = true;
				int activeCnt = purge();
				if (!closed && cacheLifeTime > 0 && activeCnt < cacheLimit) {
					PooledConnection me = (PooledConnection) event.getSource();
					long lastUse = System.currentTimeMillis();
					caches.offer(new PooledConnectionCache(me, lastUse));
					nocache = false;
				}
				if (nocache) {
					// このプールがクローズ済みであるか、即時期限切れであるか、
					// プールが制限値を超えている場合は、プールに入れずに閉じる.
					logAndClose(event);
				}
			}

			@Override
			public void connectionErrorOccurred(ConnectionEvent event) {
				// エラーが発生していればログに記録し
				// 物理コネクションを閉じてプールには戻さない
				logAndClose(event);
			}

			protected void logAndClose(ConnectionEvent event) {
				PooledConnection me = (PooledConnection) event.getSource();
				PrintWriter logWriter = getSafeLogWriter();

				// エラーが発生していればログライタに記録する.
				SQLException exception = event.getSQLException();
				if (exception != null) {
					exception.printStackTrace(logWriter);
				}
				// プールには戻さずにクローズする.
				try {
					close(me);
				} catch (SQLException ex) {
					ex.printStackTrace(logWriter);
				}
			}
		});
	}

	/**
	 * キャッシュの有効期限を取得する. 0以下はキャッシュしないことを示す.
	 *
	 * @return キャッシュの有効期間(mSec)
	 */
	public long getCacheLifeTime() {
		return cacheLifeTime;
	}

	/**
	 * キャッシュの有効期限を設定する. 0以下はキャッシュしないことを示す.
	 *
	 * @param cacheLifeTime
	 *            キャッシュの有効期間(mSec)
	 */
	public void setCacheLifeTime(long cacheLifeTime) {
		this.cacheLifeTime = cacheLifeTime;
	}

	/**
	 * キャッシュの有効期限を設定する. 0以下はキャッシュしないことを示す.
	 *
	 * @param cacheLifeTime
	 *            キャッシュの有効期間
	 * @param timeUnit
	 *            時間の単位
	 */
	public void setCacheLifeTime(long cacheLifeTime, TimeUnit timeUnit) {
		if (timeUnit == null) {
			throw new IllegalArgumentException();
		}
		setCacheLifeTime(TimeUnit.MILLISECONDS.convert(cacheLifeTime, timeUnit));
	}

	/**
	 * プールできる最大数を取得する. 0以下はプールしないことを示す.
	 *
	 * @return プールできる最大数
	 */
	public int getCacheLimit() {
		return cacheLimit;
	}

	/**
	 * プールできる最大数を設定する.
	 *
	 * @param cacheLimit
	 *            プールできる最大数
	 */
	public void setCacheLimit(int cacheLimit) {
		this.cacheLimit = cacheLimit;
	}

	/**
	 * 物理コネクションを閉じる.
	 *
	 * @param conn
	 *            物理コネクション
	 * @throws SQLException
	 *             失敗
	 */
	protected void close(PooledConnection conn) throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}

	/**
	 * プールされたコネクションからコネクションを取得する.
	 *
	 * @param poolConn
	 *            プールされたコネクション
	 * @return コネクション
	 * @throws SQLException
	 */
	protected Connection getConnection(PooledConnection poolConn)
			throws SQLException {
		return poolConn.getConnection();
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (closed) {
			throw new SQLException("connection pool is closed.");
		}
		SQLException oex = null;
		for (int retry = 0; retry < 10; retry++) {
			try {
				PooledConnectionCache cache = caches.poll();
				PooledConnection poolConn;
				if (cache == null) {
					// プールが空の場合は物理接続を新規に作成する.
					poolConn = ds.getPooledConnection();
					setupRecycleHandler(poolConn);

				} else {
					poolConn = cache.poolConn;
				}

				// 物理コネクションからの取得
				return getConnection(poolConn);

			} catch (SQLException ex) {
				// コネクション取得に失敗したらリトライする.
				oex = ex;
				log.error("getConnectionに失敗。retry=" + retry, ex);
				try {
					Thread.sleep(300);

				} catch (InterruptedException iex) {
					log.warn("InterruptedException", iex);
					break;
				}
			}
		}
		if (oex == null) {
			oex = new SQLException("コネクションプールの取得に失敗");
		}
		throw oex;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		throw new SQLException("unsupported operation.");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if (iface != null && iface.isAssignableFrom(ds.getClass())) {
			// 元データソースへのアクセスは可
			return true;
		}
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (iface != null && iface.isAssignableFrom(ds.getClass())) {
			// 元データソースへのアクセスは可
			@SuppressWarnings("unchecked")
			T ds2 = (T) ds;
			return ds2;
		}
		throw new SQLException("not wrapped. " + iface);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		ds.setLoginTimeout(seconds);
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		ds.setLogWriter(out);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return ds.getLoginTimeout();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return ds.getLogWriter();
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(getClass().getName());
		buf.append("@");
		buf.append(Integer.toHexString(System.identityHashCode(this)));
		buf.append(" {poolMap: {");
		buf.append(caches);
		buf.append("}, Wrapped ConnectionPoolDataSource = ");
		buf.append(ds);
		return buf.toString();
	}
}
