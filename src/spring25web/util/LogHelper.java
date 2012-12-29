package spring25web.util;

import java.io.PrintWriter;
import java.io.Writer;

import org.slf4j.Logger;

/**
 * ログ用ユーテリティ
 */
public final class LogHelper {

	/**
	 * プライベートコンストラクタ
	 */
	private LogHelper() {
		super();
	}

	/**
	 * ログに転送するライターを構築して返します.
	 * @return ログに転送するライター
	 */
	public static PrintWriter createLogWriter(final Logger log) {
		if (log == null) {
			throw new IllegalArgumentException();
		}

		final StringBuilder buf = new StringBuilder();
		Writer wr = new Writer() {
			@Override
			public void close() {
				flush();
			}

			@Override
			public void flush() {
				synchronized (buf) {
					if (buf.length() > 0) {
						log.info(buf.toString());
						buf.setLength(0);
					}
				}
			}

			@Override
			public void write(char[] cbuf, int off, int len) {
				synchronized (buf) {
					for (int idx = 0; idx < len; idx++) {
						char c = cbuf[off + idx];
						if (c == '\r' || c== '\n') {
							flush();
						} else {
							buf.append(c);
						}
					}
				}
			}
		};
		return new PrintWriter(wr);
	}
}
