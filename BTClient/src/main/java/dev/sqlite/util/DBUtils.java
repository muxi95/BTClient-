package dev.sqlite.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.sqlite.entity.KeyValuePair;

/**
 * @author Richx
 */
public class DBUtils {
    /**
     * 判断是否为数字
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        if (str.length() > 1 && str.startsWith("0")) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 根据参数生成WHERE语句
     *
     * @param name 名字
     * @param value 值
     * @return
     */
    public static String getSQLWhere(String name, Object value) {
        if (value == null) {
            return name + " = null";
        }
        if (DBUtils.isNumeric(value.toString())) {
            return name + " = " + value;
        } else {
            return name + " = '" + value + "'";
        }
    }

    /**
     * 根据参数生成WHERE语句
     *
     * @param params 参数
     * @return 字符串
     */
    public static String getSQLWhere(SQLParams params) {
        List<KeyValuePair> list = params.KVList;
        if (list != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                KeyValuePair kvp = list.get(i);
                String value = kvp.Value.toString();
                sb.append(kvp.Key);
                sb.append(" = ");
                if (DBUtils.isNumeric(value)) {
                    sb.append(value);
                } else {
                    sb.append('\'');
                    sb.append(value);
                    sb.append('\'');
                }
                if (i != list.size() - 1) {
                    sb.append(" AND ");
                }
            }
            return sb.toString();
        }
        return null;
    }
}
