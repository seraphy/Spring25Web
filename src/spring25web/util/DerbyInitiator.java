package spring25web.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.apache.derby.tools.ij;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;


/**
 * Apache Derbyの初期化とシャットダウンを行う.
 */
public class DerbyInitiator implements ServletContextAware, InitializingBean, DisposableBean {

	/**
	 * ログ
	 */
    private static final Logger log = LoggerFactory.getLogger(DerbyInitiator.class);

	/**
	 * サーブレットコンテキスト
	 */
	private ServletContext ctx;

	/**
	 * Apache Derby データベースのホーム位置
	 */
	private String databaseHome;

	/**
	 * データベースの位置
	 */
	private String databaseLocation;

	/**
	 * 必要テーブル名一覧、カンマ区切り
	 */
	private String requiredTableNames;

	/**
	 * データベース初期化スクリプト
	 */
	private String initScript;
	
	/**
	 * データベース初期化スクリプトの文字コード
	 */
	private String initScriptEncoding = "MS932";
	
	/**
	 * 追加のプロパティ
	 */
	private Properties properties;

	/**
	 * コネクションプールデータソース
	 */
	private SimpleConnectionPoolDataSourceImpl _ds;


	public void setDatabaseHome(String databaseHome) {
		this.databaseHome = databaseHome;
	}

	public String getDatabaseHome() {
		return databaseHome;
	}

	public void setDatabaseLocation(String databaseLocation) {
		this.databaseLocation = databaseLocation;
	}

	public String getDatabaseLocation() {
		return databaseLocation;
	}

	public void setRequiredTableNames(String requiredTableNames) {
		this.requiredTableNames = requiredTableNames;
	}

	public String getRequiredTableNames() {
		return requiredTableNames;
	}

	public void setInitScript(String initScript) {
		this.initScript = initScript;
	}

	public String getInitScript() {
		return initScript;
	}
	
	public String getInitScriptEncoding() {
        return initScriptEncoding;
    }
	
	public void setInitScriptEncoding(String initScriptEncoding) {
	    if (initScriptEncoding == null || initScriptEncoding.length() == 0) {
	        throw new IllegalArgumentException();
	    }
        this.initScriptEncoding = initScriptEncoding;
    }
	
	public void setProperties(Properties properties) {
        this.properties = properties;
    }
	
	public Properties getProperties() {
        return properties;
    }

	@Override
	public void setServletContext(ServletContext ctx) {
		this.ctx = ctx;
	}

    public static PrintWriter createLogWriter() {
        return LogHelper.createLogWriter(log);
    }
    
	public DataSource getDataSource() {
		if (_ds == null) {
			throw new IllegalStateException("datasource is not initiaized.");
		}
		return _ds;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("DerbyInitiator#init()");

		initDataSource();
		ensureSchemaIsReady();
	}

	@Override
	public void destroy() throws Exception {
		log.info("DerbyInitiator#dispose()");
		try {
			if (_ds != null) {
				// プールをすべて解放する.
				_ds.close();
			}
		} catch (Exception ex) {
			log.info("datasource close failed. " + ex, ex);
		}

		try {
			// Derbyデータベースシステム全体をシャットダウンする.
			DriverManager.getConnection("jdbc:derby:;shutdown=true").close();
			log.warn("Failed to shutdown the Derby gracefully.");

		} catch (Exception ex) {
			log.info("shutdown report: " + ex);
		}
	}

	protected String getDatabaseName() {
		if (databaseLocation == null || databaseLocation.length() == 0) {
			throw new RuntimeException("missing databaseLocation property.");
		}

		String realPath;
		if (databaseLocation.startsWith("memory:")) {
			realPath = databaseLocation;
		} else {
			realPath = ctx.getRealPath(databaseLocation);
		}
		return realPath;
	}

	/**
	 * データソースを初期化する.
	 * @throws SQLException
	 */
	protected void initDataSource() throws SQLException {
	    if (_ds != null) {
	        throw new IllegalStateException("this datasource was already initialized.");
	    }
		String databaseName = getDatabaseName();
		log.info("derby databaseName=" + databaseName);

		// derbyのシステムホームを設定 (derby.iniの読み込み先)
		if (databaseHome != null && databaseHome.trim().length() > 0) {
			String realDatabaseHome = ctx.getRealPath(databaseHome);
			System.setProperty("derby.system.home", realDatabaseHome);
		}

		// 追加のプロパティの設定
		if (properties != null) {
		    Enumeration<?> enmProps = properties.propertyNames();
		    while (enmProps.hasMoreElements()) {
		        String name = (String)enmProps.nextElement();
		        if (name.startsWith("derby.")) {
		            String value = properties.getProperty(name);
                    System.setProperty(name, value);
		        }
		    }
		}

		// プールデータソースの取得
		EmbeddedConnectionPoolDataSource ds = new EmbeddedConnectionPoolDataSource();
		ds.setCreateDatabase("create");
		ds.setDatabaseName(databaseName);

		// データソースのラップ
		_ds = new SimpleConnectionPoolDataSourceImpl(ds);
		_ds.setLogWriter(createLogWriter());
	}

	/**
	 * スキーマが初期化済みであるか?
	 * @return 初期化済みであればtrue、まだスキーマが設定されていなければfalse
	 * @throws SQLException データベースのアクセスに失敗した場合(真偽不明な場合)
	 */
	protected boolean isSchemaInitialized() throws SQLException {
		if (requiredTableNames == null || requiredTableNames.length() == 0) {
			// 必要テーブル名の指定なし = チェック不要
			return true;
		}

		// テーブルが不足しているか?
		boolean missing = false;

		Connection conn = _ds.getConnection();
		try {
			// 存在するテーブル一覧
			HashSet<String> existTableName = new HashSet<String>();

			// データベースのAPPスキーマに登録されているテーブル一覧
			DatabaseMetaData dbMeta = conn.getMetaData();
			ResultSet rs = dbMeta.getTables(null,"APP","%",null);
			try {
				while (rs.next()) {
					String tableName = rs.getString("TABLE_NAME");
					existTableName.add(tableName.toUpperCase());
					log.debug("%% " + tableName);
				}

			} finally {
				rs.close();
			}

			// 必要なテーブルがそろっているか確認する.
			final String[] requiredTbls = requiredTableNames.split(",");

			for (String requiredTbl : requiredTbls) {
				requiredTbl = requiredTbl.trim().toUpperCase();
				if (!existTableName.contains(requiredTbl)) {
					// 不足あり
					missing = true;
					break;
				}
			}
		} finally {
			conn.close();
		}

		return !missing;
	}

	/**
	 * リソースに指定されているDDLをデータベースに適用し、
	 * スキーマを初期化する.
	 * @throws SQLException 失敗
	 * @throws IOException 失敗
	 */
	protected void initializeSchema() throws SQLException, IOException {
		if (initScript == null || initScript.length() == 0) {
			return;
		}

		// スクリプトファイル名の取得
		String ddlScript = ctx.getRealPath(initScript);

		InputStream inp = new BufferedInputStream(
				new FileInputStream(ddlScript));
		try {
			// ijのrunScriptを使用してスクリプトを一括ロードする.
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Connection conn = _ds.getConnection();
			try {
				int ret = ij.runScript(conn, inp, initScriptEncoding, bos, "UTF-8");

				// 結果のログへの書き込み
				String msg = new String(bos.toByteArray(), "UTF-8");
				log.info("load script(numOfFailed=" + ret + ") output=" + msg);

			} finally {
				conn.close();
			}

		} finally {
			inp.close();
		}
	}

	/**
	 * スキーマが初期化されていることを保証する.
	 * 新規データベースの場合はスキーマにDDLを適用し初期化する.
	 * @throws SQLException
	 */
	protected void ensureSchemaIsReady() throws SQLException {
		try {
			// データベースが初期化されているか?
			boolean schemaInitialized = isSchemaInitialized();
			if (!schemaInitialized) {
				// 初期化されていなければ、DDLを適用してスキーマを初期化する.
				initializeSchema();
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
}
