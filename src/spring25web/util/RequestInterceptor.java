package spring25web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * リクエストをインターセプトする。
 */
@Component("requestInterceptor")
public class RequestInterceptor extends HandlerInterceptorAdapter {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * リクエストを処理する前にインターセプトする
	 * @param request リクエスト
	 * @param response レスポンス
	 * @param handler ハンドラ
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		// ログ
		log.info("[req] access from " + request.getRemoteAddr() +
				" /uri=" + request.getRequestURI());

		// 処理を継続する
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		// ログ
		log.info("[res] access from " + request.getRemoteAddr() +
				" /uri=" + request.getRequestURI());

		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		// ログ
		log.info("[afterCompletion] access from " + request.getRemoteAddr() +
				" /uri=" + request.getRequestURI());

		// 処理を継続する
		super.afterCompletion(request, response, handler, ex);
	}
}
