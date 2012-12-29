package spring25web.util;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;


/**
 * Beanのプロパティを操作するためのユーテリティ
 */
public final class BeanPropertyUtils {

	/**
	 * ロガー
	 */
    private static final Logger log = LoggerFactory.getLogger(BeanPropertyUtils.class);
    
    /**
     * プライベートコンストラクタ
     */
    private BeanPropertyUtils() {
        super();
    }

	/**
	 * プロパティをコピーします.<br>
	 * プロパティはgetterは引数をとらず、setterは1つだけ引数をとる形式であり、
	 * 且つ、getterの戻り値とsetterの引数が同じ型でなければなりません.<br>
	 * getterとsetterがペアでないものはコピーされません.<br>
	 * getterが返すオブジェクトがColneableである場合はCloneされた結果がsetterに渡されます.<br>
	 *
	 * @param src コピー元
	 * @param dest コピー先
	 */
	public static void copyProperties(Object src, Object dest) {
		copyProperties(src, dest, null);
	}

	/**
	 * プロパティをコピーします.<br>
	 * propertyNamestが空でない場合、該当する名前のgetterプロパティがあれば取得されます.<br>
	 * setterがあるかどうかは判断されません.<br>
	 * propertyNamesが空であれば、gettersとsetterのペアであるものが取得されます.<br>
	 * プロパティはgetterは引数をとらず、setterは1つだけ引数をとる形式であり、
	 * 且つ、getterの戻り値とsetterの引数が同じ型でなければなりません.<br>
	 * getterが返すオブジェクトがColneableである場合はCloneされた結果がsetterに渡されます.<br>
	 *
	 * @param src コピー元
	 * @param dest コピー先
	 * @param propertyNames コピーするプロパティ名のセット、空ならばsetterのあるすべてのプロパティが対象となる.
	 */
	public static void copyProperties(Object src, Object dest, Collection<String> propertyNames) {
		setProperties(dest, getProperties(src, propertyNames));
	}

	/**
	 * プロパティをMapにセットして返します.<br>
	 * プロパティはgetterは引数をとらず、setterは1つだけ引数をとる形式であり、
	 * 且つ、getterの戻り値とsetterの引数が同じ型でなければなりません.<br>
	 * getterとsetterがペアでないものは取得されません.<br>
	 * 取得されたオブジェクトは参照のコピーです.<br>
	 *
	 * @param o オブジェクト
	 * @return プロパティを格納したマップ、キーがプロパティ名、値がプロパティ値となります
	 */
	public static Map<String, Object> getProperties(Object o) {
		return getProperties(o, (Collection<String>) null);
	}

	/**
	 * プロパティをMapにセットして返します.<br>
	 * propertyNamestが空でない場合、該当する名前のgetterプロパティがあれば取得されます.<br>
	 * setterがあるかどうかは判断されません.<br>
	 * propertyNamesが空であれば、gettersとsetterのペアであるものが取得されます.<br>
	 * プロパティはgetterは引数をとらず、setterは1つだけ引数をとる形式であり、
	 * 且つ、getterの戻り値とsetterの引数が同じ型でなければなりません.<br>
	 * 取得されたオブジェクトは参照のコピーです.<br>
	 *
	 * @param o オブジェクト
	 * @param propertyNames コピーするプロパティ名のセット、空ならばsetterのあるすべてのプロパティが対象となる.
	 * @return プロパティを格納したマップ、キーがプロパティ名、値がプロパティ値となります
	 */
	public static Map<String, Object> getProperties(Object o, Collection<String> propertyNames) {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		getProperties(o, properties, propertyNames);
		return properties;
	}

	/**
	 * プロパティをMapにセットします。すでにマップにあるものは上書きされます.<br>
	 * gettersとsetterのペアであるものが取得されます.<br>
	 * プロパティはgetterは引数をとらず、setterは1つだけ引数をとる形式であり、
	 * 且つ、getterの戻り値とsetterの引数が同じ型でなければなりません.<br>
	 * 取得されたオブジェクトは参照のコピーです.<br>
	 *
	 * @param o オブジェクト
	 * @param properties プロパティを格納するマップ。キーがプロパティ名、値がプロパティ値となります
	 */
	public static void getProperties(Object o, Map<String, Object> properties) {
		getProperties(o, properties, null);
	}

	/**
	 * プロパティをMapにセットします。すでにマップにあるものは上書きされます.<br>
	 * propertyNamestが空でない場合、該当する名前のgetterプロパティがあれば取得されます.<br>
	 * setterがあるかどうかは判断されません.<br>
	 * propertyNamesが空であれば、gettersとsetterのペアであるものが取得されます.<br>
	 * プロパティはgetterは引数をとらず、setterは1つだけ引数をとる形式であり、
	 * 且つ、getterの戻り値とsetterの引数が同じ型でなければなりません.<br>
	 * 取得されたオブジェクトは参照のコピーです.<br>
	 *
	 * @param o オブジェクト
	 * @param properties プロパティを格納するマップ。キーがプロパティ名、値がプロパティ値となります
	 * @param propertyNames コピーするプロパティ名のセット、空ならばsetterのあるすべてのプロパティが対象となる.
	 */
	public static void getProperties(Object o, Map<String, Object> properties,
			Collection<String> propertyNames) {
		if (o == null || properties == null) {
			throw new IllegalArgumentException();
		}
		if (propertyNames == null) {
			propertyNames = Collections.emptySet();
		}

		Class<?> clz = o.getClass();
		while (clz != null && !clz.equals(Object.class)) {
			PropertyDescriptor[] descs = org.springframework.beans.BeanUtils
					.getPropertyDescriptors(clz);
			for (PropertyDescriptor desc : descs) {
				String name = desc.getName();
				Method getter = desc.getReadMethod();
				Method setter = desc.getWriteMethod();
				if (getter != null) {
					if (propertyNames.contains(name)
							|| (propertyNames.isEmpty() && setter != null)) {
						try {
							Object value = getter.invoke(o);
							properties.put(name, value);

						} catch (Exception e) {
							throw new RuntimeException("プロパティの読み込みに失敗しました。" + clz
									+ "." + name, e);
						}
					}
				}
			}
			clz = clz.getSuperclass();
		}
	}

	/**
	 * オブジェクトのツリーを再帰的に走査し、プロパティの一覧を取得します.<br>
	 * getterとsetterがペアであるプロパティを取得し、その値がさらにチェックし、これ以上、子が展開できなくなるまで続けます。<br>
	 * 子プロパティは、たとえば「aaa.bbb」のようにドットで親オブジェクト名と接続されます.<br>
	 * (java.*クラスは展開されません。)<br>
	 * @param obj 対象オブジェクト
	 * @return プロパティマップ
	 */
	public static Map<String, Object> getNestedProperties(Object obj) {
		Map<String, Object> props = getProperties(obj);
		firstLoop: for (;;) {
			Iterator<Map.Entry<String, Object>> ite = props.entrySet().iterator();
			while (ite.hasNext()) {
				Map.Entry<String, Object> entry = ite.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				if (value == null) {
					// nullであれば次へ
					continue;
				}
				Class<?> valueClass = value.getClass();
				if (valueClass.getName().startsWith("java")) {
					// java.*クラスであれば次へ
					continue;
				}
				Map<String, Object> nestedProps = getProperties(value);
				if (!nestedProps.isEmpty()) {
					// 子プロパティが取得できた場合、このプロパティを削除して
					// 子プロパティを展開する.
					ite.remove();
					for (Map.Entry<String, Object> nestedEntry : nestedProps.entrySet()) {
						String nestedKey = nestedEntry.getKey();
						Object nestedValue = nestedEntry.getValue();
						String newKey = key + "." + nestedKey;
						props.put(newKey, nestedValue);
					}
					// チェックのやり直し.
					continue firstLoop;
				}
			}
			break;
		}
		return props;
	}

	/**
	 * ネストされたプロパティを設定する.<br>
	 * キーは「aaa.bbb.ccc」の形式でネストされているものを受け入れる.<br>
	 * マップにない項目は変更されません.<br>
	 * @param values ネスト表記のプロパティのマップ
	 * @param target 書き込み先
	 */
	public static void setNestedProperties(Object target, Map<String, Object> values) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		if (values == null || values.isEmpty()) {
			return;
		}
		BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			wrapper.setPropertyValue(key, value);
		}
	}

	/**
	 * プロパティの型をMapにセットして返します.<br>
	 * プロパティはgetterは引数をとらず、setterは1つだけ引数をとる形式であり、
	 * 且つ、getterの戻り値とsetterの引数が同じ型でなければなりません.<br>
	 * getterとsetterがペアでないものは取得されません.<br>
	 *
	 * @param c クラス
	 * @return プロパティを格納したマップ、キーがプロパティ名、値がプロパティの型となります
	 */
	public static Map<String, Class<?>> getPropertiesClass(Class<?> c) {
		return getPropertiesClass(c, (Collection<String>) null);
	}

	/**
	 * プロパティの型をMapにセットして返します.<br>
	 * propertyNamestが空でない場合、該当する名前のgetterプロパティがあれば取得されます.<br>
	 * setterがあるかどうかは判断されません.<br>
	 * propertyNamesが空であれば、gettersとsetterのペアであるものが取得されます.<br>
	 * プロパティはgetterは引数をとらず、setterは1つだけ引数をとる形式であり、
	 * 且つ、getterの戻り値とsetterの引数が同じ型でなければなりません.<br>
	 *
	 * @param c クラス
	 * @param propertyNames 対象のプロパティ名のセット、空ならばsetterのあるすべてのプロパティが対象となる.
	 * @return プロパティの型を格納したマップ、キーがプロパティ名、値がプロパティの型となります
	 */
	public static Map<String, Class<?>> getPropertiesClass(Class<?> c, Collection<String> propertyNames) {
		HashMap<String, Class<?>> propertiesClz = new HashMap<String, Class<?>>();
		getPropertiesClass(c, propertiesClz, propertyNames);
		return propertiesClz;
	}

	/**
	 * プロパティの型をMapにセットします。すでにマップにあるものは上書きされます.<br>
	 * gettersとsetterのペアであるものが取得されます.<br>
	 * プロパティはgetterは引数をとらず、setterは1つだけ引数をとる形式であり、
	 * 且つ、getterの戻り値とsetterの引数が同じ型でなければなりません.<br>
	 *
	 * @param c クラス
	 * @param propertiesClz プロパティの型を格納するマップ。キーがプロパティ名、値がプロパティの型となります
	 */
	public static void getPropertiesClass(Class<?> c, Map<String, Class<?>> propertiesClz) {
		getPropertiesClass(c, propertiesClz, null);
	}

	/**
	 * プロパティの型をマップとして返します.<br>
	 * @param c クラス
	 * @param propertiesClz プロパティの型を格納するマップ。キーがプロパティ名、値がプロパティの型となります
	 * @param propertyNames 対象となるプロパティ名のセット、空ならばsetterのあるすべてのプロパティが対象となる.
	 */
	public static void getPropertiesClass(Class<?> clz,
			Map<String, Class<?>> propertiesClz,
			Collection<String> propertyNames) {
		if (clz== null || propertiesClz == null) {
			throw new IllegalArgumentException();
		}
		if (propertyNames == null) {
			propertyNames = Collections.emptySet();
		}

		while (clz != null && !clz.equals(Object.class)) {
			PropertyDescriptor[] descs = org.springframework.beans.BeanUtils
					.getPropertyDescriptors(clz);

			for (PropertyDescriptor desc : descs) {
				String name = desc.getName();
				Method getter = desc.getReadMethod();
				Method setter = desc.getWriteMethod();
				if (getter != null) {
					if (propertyNames.contains(name)
							|| (propertyNames.isEmpty() && setter != null)) {
						try {
							propertiesClz.put(name, getter.getReturnType());

						} catch (Exception e) {
							throw new RuntimeException("プロパティの戻り方の解析に失敗しました。" + clz
									+ "." + name, e);
						}
					}
				}
			}

			clz = clz.getSuperclass();
		}
	}

	/**
	 * マップに格納されたプロパティをオブジェクトのsetterに適用します.<br>
	 * 該当するsetterがない場合は設定されません.<br>
	 * setterの引数とプロパティ値の型が一致しない場合は実行時エラーとなります.<br>
	 * プロパティ値がCloneableを実装する場合、Cloneされた内容が設定されます.<br>
	 *
	 * @param o 設定先のオブジェクト
	 * @param properties 設定するプロパティ
	 */
	public static void setProperties(Object o, Map<String, Object> properties) {
		if (o == null || properties == null) {
			throw new IllegalArgumentException();
		}
		Class<?> clz = o.getClass();
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String propertyName = entry.getKey();
			PropertyDescriptor desc = org.springframework.beans.BeanUtils
					.getPropertyDescriptor(clz, propertyName);
			Method setter = null;
			if (desc != null) {
				setter = desc.getWriteMethod();
			}
			if (setter == null) {
				if (log.isTraceEnabled()) {
					log.trace("setterがありません:" + clz + "." + propertyName);
				}
				continue;
			}

			Object value = entry.getValue();

			try {

				// クローン可能な場合はクローンする
				if (value != null && value instanceof Cloneable) {
					Method cloneMethod = value.getClass().getMethod("clone");
					value = cloneMethod.invoke(value);
				}

				setter.invoke(o, value);

			} catch (Exception e) {
				throw new RuntimeException("プロパティの書き込みに失敗しました。" + clz
						+ "." + propertyName, e);
			}
		}
	}

	/**
	 * 存在しないプロパティに対してgetアクセスすると実行時例外が発生する「必須マップ」に変換して返します.<br>
	 * @param parent 元プロパティ
	 * @return 変換されたプロパティ
	 */
	public static Map<String, Object> requiredMap(Map<String, Object> parent) {
		return new RequiredMap<String, Object>(parent);
	}

	/**
	 * オブジェクトをシリアライズしバイト配列として返します.<br>
	 * nullの場合はnullが返されます.<br>
	 * シリアライズできない場合は実行時例外が発生します.<br>
	 * @param o オブジェクト、シリアライズ可能でなければなりません.<br>
	 * @return シリアライズされたバイト配列、またはnull
	 */
	public static byte[] getSerializedData(Object o) {
		if (o == null) {
			return null;
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(o);
			oos.close(); // メモリ操作なので例外が発生しても明示的closeがなくても安全です
			bos.close();
			return bos.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("オブジェクトのシリアライズに失敗しました。:" + o, e);
		}
	}

	/**
	 * シリアライズされたバイト配列からオブジェクトを復元します.<br>
	 * nullまたは空のバイト配列はnullを返します.<br>
	 * @param data バイト配列、またはnull
	 * @return 復元されたオブジェクト、またはnull
	 */
	public static Object getObjectFromSerializedData(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bis);
			Object o = ois.readObject();
			ois.close(); // メモリ操作なので例外が発生して明示的closeがなくても安全です
			bis.close();
			return o;

		} catch (Exception e) {
			throw new RuntimeException("オブジェクトのデシリアライズに失敗しました。", e);
		}
	}

	/**
	 * ディープコピーします.<br>
	 * nullの場合はnullを返します.<br>
	 * ディープコピーのためにオブジェクトをシリアライズします.<br>
	 * オブジェクトは自身を含み、その末端の子に至るまですべてシリアライズ可能でなければなりません.<br>
	 * @param o コピーするオブジェクト、シリアライズ可能でなければなりません。
	 * @return ディープコピーされたオブジェクト、またはnull
	 */
	public static <T extends Serializable> T deepCopy(T o) {
		@SuppressWarnings("unchecked")
		T r = (T) getObjectFromSerializedData(getSerializedData(o));
		return r;
	}

	/**
	 * プロパティから指定した名前のものを除外します.<br>
	 * @param obj プロパティ
	 * @param names 除外されるプロパティ名
	 */
	public static void excludeByNames(Map<String, ?> obj, Collection<String> names) {
		if (obj == null) {
			throw new IllegalArgumentException();
		}
		if (names != null) {
			for (String name : names) {
				obj.remove(name);
			}
		}
	}

	/**
	 * プロパティから指定した名前のもの以外を除外します.<br>
	 * @param obj プロパティ
	 * @param names 残されるプロパティ名
	 */
	public static void filterByNames(Map<String, ?> obj, Collection<String> names) {
		if (obj == null) {
			throw new IllegalArgumentException();
		}

		HashSet<String> nameMap = new HashSet<String>();
		if (names != null) {
			nameMap.addAll(names);
		}

		Iterator<?> ite = obj.entrySet().iterator();
		while (ite.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, ?> entry = (Map.Entry<String, ?>) ite.next();
			String name = entry.getKey();
			if ( !nameMap.contains(name)) {
				ite.remove();
			}
		}
	}
}

/**
 * マップのgetアクセス時に存在しないキーを指定するとエラーとなるマップ.<br>
 *
 * @param <K> キーの型
 * @param <V> 値の型
 */
class RequiredMap<K, V> extends AbstractMap<K, V> implements Serializable {

	/**
	 * シリアライズバージョンID.<br>
	 */
	private static final long serialVersionUID = -6244607659900973185L;

	/**
	 * 親マップ
	 */
	private final Map<K, V> parent;

	/**
	 * 親マップを指定して構築する
	 * @param parent 親マップ
	 */
	RequiredMap(Map<K, V> parent) {
		if (parent == null) {
			throw new IllegalArgumentException();
		}
		this.parent = parent;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return parent.entrySet();
	}

	@Override
	public V get(Object key) {
		if ( !parent.containsKey(key)) {
			throw new RuntimeException("キーは存在しません:" + key + "/" + parent);
		}
		return parent.get(key);
	}

	@Override
	public V put(K key, V value) {
		return parent.put(key, value);
	};

}
