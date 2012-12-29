package spring25web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.DispatcherServlet;


/**
 * Springのメッセージソースを任意の時点で取得するためのヘルパ.
 *
 * 静的メソッドのRequestContextHolder#currentRequestAttributes()を通じて、
 * web.xmlのRequestContextListenerでバインドされている情報から、
 * SpringのApplicationContextを取得し、メッセージソースを解決する.
 */
public final class MessageHelper {

	/**
	 * プライベートコンストラクタ
	 */
	private MessageHelper() {
		super();
	}

	/**
	 * メッセージソースを取得する。
	 * @param id メッセージID
	 * @return メッセージ
	 */
	public static String getMessage(String id) {
		return getMessage(id, new Object[0]);
	}

	/**
	 * メッセージソースを取得する。
	 * @param id メッセージID
	 * @param args 引数
	 * @return メッセージ
	 */
	public static String getMessage(String id, Object[] args) {

		final Log log = LogFactory.getLog(MessageHelper.class);
		try {
			// SpringFrameworkのアプリケーションコンテキスト
			WebApplicationContext context = getApplicationContext();

			// ロケールコンテキストを取得する
			LocaleContext loc = LocaleContextHolder.getLocaleContext();

			// メッセージを取得する。
			String message = context.getMessage(id, args, loc.getLocale());

			return message;

		} catch (RuntimeException e) {
			log.warn(e, e);
			return "";
		}
	}

	/**
	 * SpringFrameworkのアプリケーションコンテキストを取得する.
	 * @return アプリケーションコンテキスト
	 */
	public static WebApplicationContext getApplicationContext() {
		// リクエストコンテキストを取得
		// (RequestContextListenerによりバインドされる)
		RequestAttributes attrs = RequestContextHolder.currentRequestAttributes();

		// リクエストコンテストからアプリケーションコンテキストを取得する。
		WebApplicationContext context = (WebApplicationContext) attrs
				.getAttribute(
						DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE,
						RequestAttributes.SCOPE_REQUEST);
		return context;
	}
}
