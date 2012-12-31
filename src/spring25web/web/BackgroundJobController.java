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

import spring25web.service.AccountService;
import spring25web.util.LoginInfoHelper;
import spring25web.util.Roles;


@Controller
@RequestMapping("/backgroundJob.do")
public class BackgroundJobController {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * アカウントサービス
     */
    @Autowired(required = true)
    @Qualifier("accountService")
    private AccountService accountService;

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
        
        Runnable job = new Runnable() {
            @Override
            public void run() {
                try {
                    // このスレッドをログイン状態に関連づける.
                    LoginInfoHelper.login("admin", Roles.ROLE_ADMINISTRATOR);
                    
                    // 時間のかかる処理のエミュレート
                    for (int idx = 0; idx < mx; idx++) {
                        log.info("background " + idx + "/" + mx);
                        Thread.sleep(100);
                    }
                    
                    // ROLE_ADMINISTRATRORが必要な処理のテスト
                    log.info("users=" + accountService.selectAll());
                    
                    // 完了
                    log.info("background done.");

                } catch (Exception ex) {
                    log.info("error in bgJob: " + ex, ex);
                }
            }
        };
        
        taskExecutor.execute(job);
        
        model.addObject("message", "accepted.");
        return model;
    }
}
