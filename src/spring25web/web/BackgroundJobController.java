package spring25web.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/backgroundJob.do")
public class BackgroundJobController {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * コンストラクタ
     */
    public BackgroundJobController() {
        log.info("BackgroundJobController#ctor()");
    }

    /**
     * プロパティ
     */
    @Autowired(required = true)
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    /**
     * GETアクセス.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleInitRequest() {
        log.debug("handleInitRequest");
        ModelAndView model = new ModelAndView();
        model.addObject("message", "initial");
        return model;
    }
    
    /**
     * POSTアクセス.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView handlePostRequest() {
        log.debug("handlePostRequest");
        ModelAndView model = new ModelAndView();

        final int mx = 50;
        
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int idx = 0; idx < mx; idx++) {
                        log.info("background " + idx + "/" + mx);
                        Thread.sleep(100);
                    }
                    log.info("background done.");

                } catch (InterruptedException e) {
                    log.info("interrupted", e);
                }
            }
        });
        
        model.addObject("message", "accepted.");
        return model;
    }
}