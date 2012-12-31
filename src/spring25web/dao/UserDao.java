package spring25web.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import spring25web.dto.User;
import spring25web.util.BeanPropertyUtils;


/**
 * ユーザテーブルのDAO
 */
@Repository
public class UserDao {

	/**
	 * ログ
	 */
	private final Log log = LogFactory.getLog(getClass());

	/**
	 * JDBCテンプレート.<br>
	 */
	protected SimpleJdbcTemplate jdbcTempl;

	/**
	 * SQL定義.<br>
	 */
	@Autowired(required = true)
	@Qualifier("sqls")
	protected Properties sqls;

	/**
	 * JDBCテンプレートを構築して設定します。<br>
	 * インスタンスを構築する際にSpringによりdataSourceが渡されることでメンバ変数に
	 * JDBCテンプレートを設定するためのインジェクタ。<br>
	 * @param dataSource データソース
	 */
	@Autowired(required = true)
	public void createTemplate(@Qualifier("dataSource") DataSource dataSource) {
		this.jdbcTempl = new SimpleJdbcTemplate(dataSource);
	}

	/**
	 * USERテーブル用データベースのカラム名をクラスのプロパティ名でマッチングするマッパー.<br>
	 */
	private static ParameterizedBeanPropertyRowMapper<User> USER_ROWMAPPER =
			ParameterizedBeanPropertyRowMapper.newInstance(User.class);

	/**
	 * ユーザ情報の取得.
	 *
	 * @param loginid ログインID
	 * @return ユーザ情報、なければnull
	 */
	public User findByLoginid(String loginid) {
		String sql = sqls.getProperty("users.findByLoginid");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginid", loginid);

		if (log.isDebugEnabled()) {
			log.debug("sql=" + sql + "/param=" + params);
		}
		List<User> users = this.jdbcTempl.query(sql, USER_ROWMAPPER, params);
		if (log.isDebugEnabled()) {
			log.debug("findByLoginid(results)=" + users);
		}
		if (users.size() == 0) {
			return null;
		}

		return users.get(0);
	}

    /**
     * ユーザ情報の取得.
     *
     * @param loginid ログインID
     * @return ユーザ情報、なければnull
     */
    public List<User> selectAll() {
        String sql = sqls.getProperty("users.selectAll");

        List<User> users = this.jdbcTempl.query(sql, USER_ROWMAPPER);
        if (log.isDebugEnabled()) {
            log.debug("selectAll(results)=" + users);
        }

        return users;
    }

	/**
	 * 更新を行います.
	 * @param dto AgentMasterDto
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void update(User user) {

		if (user == null) {
			throw new IllegalArgumentException("dtoにnullは指定できません。");
		}

		String sql = sqls.getProperty("users.update");

		Map<String, Object> args = BeanPropertyUtils.getProperties(user);

		// SQL実行
		if (log.isInfoEnabled()) {
			log.info("sql=" + sql + "/params=" + args);
		}

		int numOfRows = this.jdbcTempl.update(sql, args);
		log.info("update(result)=" + numOfRows);

		if (numOfRows != 1) {
			throw new IncorrectResultSizeDataAccessException(1, numOfRows);
		}
	}

}
