package spring25web.web.admin;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import spring25web.service.AccountService;


/**
 * アカウント制御コントローラ
 */
@Controller
@RequestMapping("/admin/changepassword.do")
public class ChangePasswordController {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * アカウント制御サービス
     */
    @Autowired(required = true)
    @Qualifier("accountService")
    private AccountService accountService;

    /**
     * GETアクセス.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleInitRequest(@RequestParam("loginid") String loginid) {
        log.debug("handleInitRequest");

        if (loginid == null || loginid.trim().length() == 0) {
            throw new IllegalArgumentException();
        }
        
        ModelAndView model = new ModelAndView();
        model.addObject("loginid", loginid);

        return model;
    }

    /**
     * POSTアクセス(Check).
     */
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView handleUploadRequest(
            @RequestParam("loginid") String loginid,
            @RequestParam("password") String password) throws IOException {
        log.debug("handleUploadRequest");

        String hash = accountService.changePassword(loginid, password);

        ModelAndView model = new ModelAndView();

        model.addObject("loginid", loginid);
        model.addObject("password", password);
        model.addObject("hash", hash);

        return model;
    }
}
