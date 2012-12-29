package spring25web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;


/**
 * ログイン関連ヘルパ.
 *
 * Spring Securityが保持しているスレッド固有情報にアクセスするためのヘルパ.
 */
public final class LoginInfoHelper {

	/**
	 * プライベートコンストラクタ
	 */
	private LoginInfoHelper() {
		super();
	}

	/**
	 * SpringSecurityからスレッドに関連づけられているログインIDを取得する 。
	 * ログインしていない場合はAnonymousとなる。
	 * @return loginid、存在しなければNULL
	 */
	public static String getCurrentLoginId() {
		String loginid = "";

		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser != null) {
			Object obj = currentUser.getPrincipal();
			if (obj instanceof UserDetails) {
				loginid = ((UserDetails) obj).getUsername();
			} else {
				loginid = obj.toString();
			}
		}
		return loginid;
	}

	/**
	 * 許可する権限を、現在のログインユーザが権限を持っているか判定する.<br>
	 * @param acceptAuthorities 許可する権限
	 * @return 許可される場合はtrue、そうでなければfalse
	 */
	public static boolean hasAuthority(String acceptAuthority) {
		return hasAuthority(new String[] {acceptAuthority});
	}

	/**
	 * 許可する権限の一覧のいずれかを、現在のログインユーザが権限を持っているか判定する.<br>
	 * 権限リストが空もしくはnullの場合は常に許可される.<br>
	 * 各権限は前後の空白をトリムして判定されます.<br>
	 * (権限リストの各要素がnullまたは空文字の場合はチェック対象外.)<br>
	 * @param acceptAuthorities 許可する権限のリスト、nullもしくは空も可。
	 * @return 許可される場合はtrue、そうでなければfalse
	 */
	public static boolean hasAuthority(String... acceptAuthorities) {
		if (acceptAuthorities == null || acceptAuthorities.length == 0) {
			return true;
		}

		Collection<String> grantedAuthorities = getPrincipalAuthorities();
		for (String acceptAuthority : acceptAuthorities) {
			if (hasAuthority(acceptAuthority, grantedAuthorities)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定した権限が権限リストに含まれているか?<br>
	 * nullまたは空文字はfalseを返します.<br>
	 * @param acceptAuthority 権限
	 * @param grantedAuthorities 権限リスト
	 * @return 権限リストに含まれる場合はtrue、そうでなければfalse
	 */
	private static boolean hasAuthority(String acceptAuthority, Collection<String> grantedAuthorities) {
		if (acceptAuthority != null) {
			acceptAuthority = acceptAuthority.trim();
		}
		if (acceptAuthority != null && acceptAuthority.length() > 0) {
			if (grantedAuthorities.contains(acceptAuthority)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * SpringSecurityからスレッドに関連づけられているログインユーザの権限一覧を取得する。
	 * 権限がないか、ログインがない場合は空のコレクションを返す.<br>
	 * @return 権限のコレクション
	 */
    public static Collection<String> getPrincipalAuthorities() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if (null == currentUser) {
            return Collections.emptyList();
        }

        if ((null == currentUser.getAuthorities()) || (currentUser.getAuthorities().length < 1)) {
            return Collections.emptyList();
        }

        HashSet<String> authorities = new HashSet<String>();

        for (GrantedAuthority grantedAuthority : currentUser.getAuthorities()) {
        	String authority = grantedAuthority.getAuthority();
        	if (authority != null && authority.length() > 0) {
        		authorities.add(authority);
        	}
        }

        return Collections.unmodifiableCollection(authorities);
    }

	/**
	 * 現在のスレッドに対して、手動でログイン状態にする.
	 * @param loginid ログインID
	 * @param roles ロールのリスト
	 */
	public static void login(String loginid, String... roles) {
		if (loginid == null) {
			throw new IllegalArgumentException();
		}

		ArrayList<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
		if (roles != null) {
			for (String role : roles) {
				if (role != null && role.length() > 0) {
					auths.add(new GrantedAuthorityImpl(role));
				}
			}
		}
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(loginid, loginid, auths
						.toArray(new GrantedAuthority[auths.size()])));
	}
}
