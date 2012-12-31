package spring25web.dto;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * USERSテーブルのレコード
 */
public class User implements Serializable {

	private static final long serialVersionUID = -2381731295297125100L;

	/**
	 * ログインID
	 */
	private String loginid;

	/**
	 * パスワード(ハッシュ済み)
	 */
	private String password;

	/**
	 * 有効フラグ
	 */
	private boolean enabled;

	/**
	 * 連続失敗数
	 */
	private int failcount;
	
	/**
	 * 最終ログイン日時
	 */
	private Timestamp lastlogin;

	
	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getFailcount() {
		return failcount;
	}

	public void setFailcount(int failcount) {
		this.failcount = failcount;
	}
	
	public Timestamp getLastlogin() {
        return lastlogin;
    }
	
	public void setLastlogin(Timestamp lastlogin) {
        this.lastlogin = lastlogin;
    }

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("(loginid=").append(loginid);
		buf.append(", password=").append(password);
		buf.append(", enabled=").append(enabled);
		buf.append(", failcount=").append(failcount);
        buf.append(", lastlogin=").append(lastlogin);
		buf.append(")");
		return buf.toString();
	}
}
