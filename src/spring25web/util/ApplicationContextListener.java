package spring25web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.security.event.authentication.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.event.authentication.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import spring25web.service.AccountService;


/**
 * アプリケーションイベントを受け取るためのリスナクラス
 */
@Component
public class ApplicationContextListener implements ApplicationListener {

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
     * アプリケーションイベントを受け取る
     */
    @Override
    public void onApplicationEvent(ApplicationEvent evt) {
        log.info("detect applicationEvent=" + evt);

        if (evt instanceof ContextClosedEvent) {
            // アプリケーションクローズイベント
            onContextClosedEvent((ContextClosedEvent) evt);
        
        } else if (evt instanceof AuthenticationFailureBadCredentialsEvent) {
            // ログイン失敗イベント
            // (無効にされているログインIDの場合は、AuthenticationFailureDisabledEventが発生する.)
            onAuthenticationFailureBadCredentialsEvent((AuthenticationFailureBadCredentialsEvent) evt);
            
        } else if (evt instanceof AuthenticationSuccessEvent) {
            // ログイン成功イベント
            onAuthenticationSuccessEvent((AuthenticationSuccessEvent) evt);
        }
    }
    
    /**
     * アプリケーションクローズイベント
     * @param evt
     */
    protected void onContextClosedEvent(ContextClosedEvent evt) {
        log.info("ApplicationContextListener#onContextClosedEvent");
    }
    
    /**
     * ログイン成功イベント
     * @param evt
     */
    protected void onAuthenticationSuccessEvent(AuthenticationSuccessEvent evt) {
        String loginid = evt.getAuthentication().getName();
        log.info("login succeeded loginid=" + loginid);
        
        accountService.login(loginid, true);
    }

    /**
     * ログイン失敗イベント.
     * @param evt
     */
    protected void onAuthenticationFailureBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent evt) {
        String loginid = evt.getAuthentication().getName();
        log.info("login failed loginid=" + loginid);
        
        accountService.login(loginid, false);
    }
}
