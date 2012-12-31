package spring25web.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import spring25web.dao.UserDao;
import spring25web.dto.User;


/**
 * アカウント管理サービス
 */
@Service
public class AccountService {

	/**
	 * パスワードエンコーダ
	 */
    @Autowired(required = true)
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /**
     * USERSテーブルのDAO
     */
    @Autowired(required = true)
    @Qualifier("userDao")
    private UserDao userDao;

    /**
     * パスワードを更新する.
     * 連続失敗回数はゼロにリセットされます.
     * @param loginid  ログインID
     * @param password パスワード(ハッシュ前)
     * @return 更新された場合は生成されたハッシュ値、そうでなければnull
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    public String changePassword(String loginid, String password) {
		if (loginid == null || loginid.length() == 0) {
			throw new IllegalArgumentException();
		}

		if (password == null) {
		    password = "";
		}

		User user = userDao.findByLoginid(loginid);
		if (user == null) {
			return null;
		}

		// パスワードエンコーダとソルトソースを指定してハッシュ値を計算する.
        String hash = passwordEncoder.encodePassword(password, loginid);

        user.setFailcount(0);
        user.setPassword(hash);

        userDao.update(user);

        return hash;
	}
    

    /**
     * ログインを記録する.
     * ログインに成功した場合は連続失敗カウントをリセットし、現在日時を最終ログイン時刻にする.
     * ログインに失敗した場合は連続失敗カウントを増やす.
     * @param loginid ログインID
     * @param logged ログインされた場合はtrue、ログイン不可の場合はfalse
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    public void login(String loginid, boolean logged) {
        if (loginid == null || loginid.length() == 0) {
            throw new IllegalArgumentException();
        }

        User user = userDao.findByLoginid(loginid);
        if (user == null) {
            return;
        }

        if (logged) {
            user.setLastlogin(new Timestamp(System.currentTimeMillis()));
            user.setFailcount(0);
            
        } else {
            user.setFailcount(user.getFailcount() + 1);
        }

        userDao.update(user);
    }
    
    /**
     * すべてのユーザを取得します.
     * loginid順.
     * @return すべてのユーザのリスト、なければ空
     */
    public List<User> selectAll() {
        return userDao.selectAll();
    }
}