package spring25web.util;

import java.util.Enumeration;
import java.util.Properties;

/**
 * 複数プロパティオブジェクトを受け取って1つに纏めるPOJOオブジェクト.
 * 
 * これをSpringのapplication-context.xmlによって明示的に生成して扱うデモ用. 
 */
public class MultiProperties extends Properties {

	private static final long serialVersionUID = 2559164514967606097L;

	/**
	 * コンストラクタ
	 * @param props 0個以上のプロパティズオブジェクトをもつ配列
	 */
	public MultiProperties(Properties[] props) {
		if (props != null) {
			for (Properties prop : props) {
				@SuppressWarnings("unchecked")
				Enumeration<String> names = (Enumeration<String>)prop.propertyNames();
				while (names.hasMoreElements()) {
					String key = names.nextElement();
					String value = prop.getProperty(key);
					this.setProperty(key, value);
				}
			}
		}
	}
}
