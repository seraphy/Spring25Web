package spring25web.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import spring25web.form.PersonEditForm;


@Controller
@RequestMapping("/personEdit.do")
public class PersonEditController {

    /**
     * ログ
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * コンストラクタ
     */
    public PersonEditController() {
        log.info("CalcuratorController#ctor()");
    }

    /**
     * GETアクセス.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleInitRequest() {
        log.info("handleInitRequest");

        
        ModelAndView model = new ModelAndView();

        PersonEditForm form = new PersonEditForm();
        form.setId((int)(Math.random() * 1000));
        
        model.addObject("personEditForm", form);
        
        return model;
    }
    
    /**
     * POSTアクセス.
     */
    @RequestMapping(method = RequestMethod.POST, params = {"doRegister"})
    public ModelAndView handlePostRequest(
            PersonEditForm form, BindingResult result) throws IOException {
        log.info("handlePostRequest form=" + form);
        
        ModelAndView model = new ModelAndView();
        return model;
    }
    
    /**
     * POSTアクセス(CANCEL).
     */
    @RequestMapping(method = RequestMethod.POST, params = {"doCancel"})
    public String handleCancelRequest() throws IOException {
        return "redirect:/index.do";
    }

    /**
     * フォームのバインダの構成
     * @param dataBinder バインダ
     */
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        //// 無効フィールド
        //dataBinder.setDisallowedFields(new String[] {"id"});

        // 受け入れ可能なフィールドを明示する. (セキュリティ的に、こちらが望ましい)
        dataBinder.setAllowedFields(new String[] {"name", "birthday", "height"});
         
        // 必須フィールドの指定
        dataBinder.setRequiredFields(new String[] {"name", "birthday"});
         
        // 文字列はトリムを行う
        dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
         
        // 日付を受け入れるようにエディタを設定する.(日付形式指定)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        dateFormat.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        
        // 数値型 (型チェックだけなら標準だけでも可)
        // NumberFormat numberFormat = NumberFormat.getInstance();
        // dataBinder.registerCustomEditor(Long.class, "height",
        //        new CustomNumberEditor(Long.class, numberFormat, false));
    }
}
