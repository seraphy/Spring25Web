package spring25web.util;

/**
 * Roleの一覧
 */
public final class Roles {

    private Roles() {
        super();
    }

    /**
     * 管理者
     */
    public static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    
    /**
     * 一般ユーザ
     */
    public static final String ROLE_USER = "ROLE_USER";

    /**
     * ゲスト
     */
    public static final String ROLE_GUEST = "ROLE_GUEST";

}
