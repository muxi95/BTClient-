
package dev.sqlite.util;

import android.database.Cursor;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import dev.sqlite.annotation.Column;
import dev.sqlite.annotation.PrimaryKey;
import dev.sqlite.annotation.TableName;
import dev.sqlite.annotation.Transient;
import dev.sqlite.entity.LBHashMap;
import dev.sqlite.entity.LBPKProperyEntity;
import dev.sqlite.entity.LBPropertyEntity;
import dev.sqlite.entity.LBTableInfoEntity;
import dev.sqlite.exception.LBDBException;

/**
 * 数据库的一些工具
 * @author Richx
 */
public class LBDBUtils {
    /**
     * 通过Cursor获取一个实体数组
     *
     * @param clazz  实体类型
     * @param cursor 数据集合
     * @return 相应实体List数组
     */
    public static <T> List<T> getListEntity(Class<T> clazz, Cursor cursor) {
        List<T> queryList = LBEntityBuilder.buildQueryList(clazz, cursor);
        return queryList;
    }

    /**
     * 通过Cursor获取一个实体
     *
     * @param clazz  实体类型
     * @param cursor 数据集合
     * @return
     */
    public static <T> T getEntity(Class<T> clazz, Cursor cursor) {
        if (cursor.moveToFirst()) {
            return LBEntityBuilder.buildQueryOneEntity(clazz, cursor);
        }
        return null;
    }

    /**
     * 返回数据表中一行的数据
     *
     * @param cursor 数据集合
     * @return TAHashMap类型数据
     */
    public static LBHashMap<String> getRowData(Cursor cursor) {
        if (cursor != null && cursor.getColumnCount() > 0) {
            LBHashMap<String> hashMap = new LBHashMap<String>();
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                hashMap.put(cursor.getColumnName(i), cursor.getString(i));
            }
            return hashMap;
        }
        return null;
    }

    /**
     * 根据实体类 获得 实体类对应的表名
     *
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz) {
        TableName table = clazz.getAnnotation(TableName.class);
        if (table == null || TextUtils.isEmpty(table.name())) {
            return clazz.getSimpleName();
        }
        return table.name();

    }

    /**
     * 返回主键字段
     *
     * @param clazz 实体类型
     * @return
     */
    public static Field getPrimaryKeyField(Class<?> clazz) {
        Field primaryKeyField = null;
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null) {

            for (Field field : fields) { // 获取ID注解
                if (field.getAnnotation(PrimaryKey.class) != null) {
                    primaryKeyField = field;
                    break;
                }
            }
            if (primaryKeyField == null) { // 没有ID注解
                for (Field field : fields) {
                    if ("_id".equals(field.getName())) {
                        primaryKeyField = field;
                        break;
                    }
                }
                if (primaryKeyField == null) { // 如果没有_id的字段
                    for (Field field : fields) {
                        if ("id".equals(field.getName())) {
                            primaryKeyField = field;
                            break;
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException("this model[" + clazz + "] has no field");
        }
        return primaryKeyField;
    }

    /**
     * 返回主键名
     *
     * @param clazz 实体类型
     * @return
     */
    public static String getPrimaryKeyFieldName(Class<?> clazz) {
        Field f = getPrimaryKeyField(clazz);
        return f == null ? "id" : f.getName();
    }

    /**
     * 返回数据库字段数组
     *
     * @param clazz 实体类型
     * @return 数据库的字段数组
     */
    public static List<LBPropertyEntity> getPropertyList(Class<?> clazz) {

        List<LBPropertyEntity> plist = new ArrayList();
        try {
            Field[] fields = clazz.getDeclaredFields();
            String primaryKeyFieldName = getPrimaryKeyFieldName(clazz);
            for (Field field : fields) {
                if (!LBDBUtils.isTransient(field)) {
                    if (LBDBUtils.isBaseDateType(field)) {

                        if (field.getName().equals(primaryKeyFieldName)) // 过滤主键
                        {
                            continue;
                        }

                        LBPKProperyEntity property = new LBPKProperyEntity();

                        property.setColumnName(LBDBUtils.getColumnByField(field));
                        property.setName(field.getName());
                        property.setType(field.getType());
                        property.setDefaultValue(LBDBUtils.getPropertyDefaultValue(field));
                        plist.add(property);
                    }
                }
            }
            return plist;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 构建创建表的sql语句
     *
     * @param clazz 实体类型
     * @return 创建表的sql语句
     * @throws LBDBException
     */
    public static String creatTableSql(Class<?> clazz) throws LBDBException {
        LBTableInfoEntity tableInfoEntity = LBTableInfofactory.getInstance().getTableInfoEntity(clazz);

        LBPKProperyEntity pkProperyEntity = null;
        pkProperyEntity = tableInfoEntity.getPkProperyEntity();
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("CREATE TABLE IF NOT EXISTS ");
        strSQL.append(tableInfoEntity.getTableName());
        strSQL.append(" ( ");

        if (pkProperyEntity != null) {
            Class<?> primaryClazz = pkProperyEntity.getType();
            if (primaryClazz == int.class || primaryClazz == Integer.class) {
                if (pkProperyEntity.isAutoIncrement()) {
                    strSQL.append("\"").append(pkProperyEntity.getColumnName()).append("\"    ").append("INTEGER PRIMARY KEY AUTOINCREMENT,");
                } else {
                    strSQL.append("\"").append(pkProperyEntity.getColumnName()).append("\"    ").append("INTEGER PRIMARY KEY,");
                }
            } else {
                strSQL.append("\"").append(pkProperyEntity.getColumnName()).append("\"    ").append("TEXT PRIMARY KEY,");
            }
        } else {
            strSQL.append("\"").append("id").append("\"    ").append("INTEGER PRIMARY KEY AUTOINCREMENT,");
        }

        Collection<LBPropertyEntity> propertys = tableInfoEntity.getPropertieArrayList();
        for (LBPropertyEntity property : propertys) {
            strSQL.append("\"").append(property.getColumnName());
            strSQL.append("\",");
        }
        strSQL.deleteCharAt(strSQL.length() - 1);
        strSQL.append(" )");
        return strSQL.toString();
    }

    /**
     * 检测 字段是否已经被标注为 非数据库字段
     *
     * @param field
     * @return
     */
    public static boolean isTransient(Field field) {
        return field.getAnnotation(Transient.class) != null;
    }

    /**
     * 检查是否是主键
     *
     * @param field
     * @return
     */
    public static boolean isPrimaryKey(Field field) {
        return field.getAnnotation(PrimaryKey.class) != null;
    }

    /**
     * 检查是否自增
     *
     * @param field
     * @return
     */
    public static boolean isAutoIncrement(Field field) {
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        if (null != primaryKey) {
            return primaryKey.autoIncrement();
        }
        return false;
    }

    /**
     * 是否为基本的数据类型
     *
     * @param field
     * @return
     */
    public static boolean isBaseDateType(Field field) {
        Class<?> clazz = field.getType();
        return clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class) || clazz.equals(Long.class)
                || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Character.class) || clazz.equals(Short.class)
                || clazz.equals(Boolean.class) || clazz.equals(Date.class) || clazz.equals(Date.class) || clazz.equals(java.sql.Date.class)
                || clazz.isPrimitive();
    }

    /**
     * 获取某个列
     *
     * @param field
     * @return
     */
    public static String getColumnByField(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && column.name().trim().length() != 0) {
            return column.name();
        }
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        if (primaryKey != null && primaryKey.name().trim().length() != 0) {
            return primaryKey.name();
        }

        return field.getName();
    }

    /**
     * 获得默认值
     *
     * @param field
     * @return
     */
    public static String getPropertyDefaultValue(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && column.defaultValue().trim().length() != 0) {
            return column.defaultValue();
        }
        return null;
    }
}
