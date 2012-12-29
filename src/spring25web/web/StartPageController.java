package spring25web.web;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/index.do")
public class StartPageController {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * コンストラクタ
	 */
	public StartPageController() {
		log.info("StartPageController#ctor()");
	}

	/**
	 * プロパティ
	 */
	@Autowired(required = true)
	@Qualifier("props")
	private Properties props;

	/**
	 * GETアクセス.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView handleInitRequest() {
		log.debug("handleInitRequest");

		log.info("data1=" + props.getProperty("data1"));
		log.info("data2=" + props.getProperty("data2"));
		log.info("data3=" + props.getProperty("data3"));

		ModelAndView model = new ModelAndView();

		return model;
	}
}
