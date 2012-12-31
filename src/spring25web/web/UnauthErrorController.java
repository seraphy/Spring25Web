package spring25web.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 権限無しの場合に呼び出されるエラーページハンドラ.
 */
@Controller
@RequestMapping("/unauthError.do")
public class UnauthErrorController {

    /**
     * GET/POSTアクセス.
     */
    @RequestMapping
    public String handleInitRequest() {

        // JSPページにフォワードするだけ.
        return "forward:/WEB-INF/jsp/err/error_unauth.jsp";
    }
}
