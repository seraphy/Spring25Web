package spring25web.util;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/**
 * Spring連携のフィルタ実装デモ
 */
@Component("mySpringFilter")
public class MySpringFilter implements Filter {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());


	/**
	 * コンストラクタ
	 */
	public MySpringFilter() {
		log.info("ctor");
	}

	/**
	 * プロパティ
	 */
	@Autowired(required = true)
	@Qualifier("props")
	private Properties props;

	@Override
	public void init(FilterConfig config) throws ServletException {
		log.info("init-filter: data1=" + props.getProperty("data1"));
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException,
			ServletException {
		log.info("do-filter: data2=" + props.getProperty("data2"));
		filterChain.doFilter(req, res);
	}

	@Override
	public void destroy() {
		log.info("destroy-filter: data3=" + props.getProperty("data3"));
	}
}
