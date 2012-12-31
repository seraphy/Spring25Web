package spring25web.web.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import spring25web.dto.User;
import spring25web.service.AccountService;

/**
 * アカウント一覧コントローラ
 */
@Controller
@RequestMapping("/admin/accountlist.do")
public class AccountListController {

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
     * GET/POSTアクセス.
     */
    @RequestMapping
    public ModelAndView handleInitRequest() {
        log.debug("handleInitRequest");

        List<User> users = accountService.selectAll();
        
        ModelAndView model = new ModelAndView();
        model.addObject("users", users);

        return model;
    }
}
