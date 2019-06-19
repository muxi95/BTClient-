/*
 * 文 件 名:  InsertSqlBuilder.java
 * 版    权:  jiang yu feng
 * 描    述:  <描述>
 * 修 改 人:  江钰锋
 * 修改时间:  2013-12-18
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package dev.sqlite.util.sql;

import org.apache.http.NameValuePair;

import java.lang.reflect.Field;

import dev.sqlite.annotation.PrimaryKey;
import dev.sqlite.entity.LBArrayList;
import dev.sqlite.exception.LBDBException;
import dev.sqlite.util.DBUtils;
import dev.sqlite.util.LBDBUtils;

/**
 * 插入sql语句构建器类
 *
 * @author 江钰锋
 * @version [版本号, 2013-12-18]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class LBInsertSqlBuilder extends LBSqlBuilder {
    @Override
    public void onPreGetStatement()
            throws LBDBException, IllegalArgumentException, IllegalAccessException {
        // TODO Auto-generated method stub
        if (getUpdateFields() == null) {
            setUpdateFields(getFieldsAndValue(entity));
        }
        super.onPreGetStatement();
    }

    @Override
    public String buildSql()
            throws LBDBException, IllegalArgumentException {
        // TODO Auto-generated method stub
        StringBuilder columns = new StringBuilder(256);
        StringBuilder values = new StringBuilder(256);
        columns.append("INSERT INTO ");
        columns.append(tableName).append(" (");
        values.append("(");
        LBArrayList updateFields = getUpdateFields();
        if (updateFields != null) {
            for (int i = 0; i < updateFields.size(); i++) {
                NameValuePair nameValuePair = updateFields.get(i);
                columns.append(nameValuePair.getName());
                values.append(DBUtils.isNumeric(nameValuePair.getValue() != null ? nameValuePair.getValue() : "")
                        ? nameValuePair.getValue() : "'" + nameValuePair.getValue() + "'");
                if (i + 1 < updateFields.size()) {
                    columns.append(", ");
                    values.append(", ");
                }
            }
        } else {
            throw new LBDBException("插入数据有误！");
        }
        columns.append(") values ");
        values.append(")");
        columns.append(values);
        return columns.toString();
    }

    /**
     * 从实体加载,更新的数据
     *
     * @return
     * @throws LBDBException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static LBArrayList getFieldsAndValue(Object entity)
            throws LBDBException, IllegalArgumentException, IllegalAccessException {
        // TODO Auto-generated method stub
        LBArrayList arrayList = new LBArrayList();
        if (entity == null) {
            throw new LBDBException("没有加载实体类！");
        }
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            if (!LBDBUtils.isTransient(field)) {
                if (LBDBUtils.isBaseDateType(field)) {
                    PrimaryKey annotation = field.getAnnotation(PrimaryKey.class);
                    if (annotation != null && annotation.autoIncrement()) {

                    } else {
                        String columnName = LBDBUtils.getColumnByField(field);
                        field.setAccessible(true);
                        arrayList.add((columnName != null && !"".equals(columnName)) ? columnName : field.getName(),
                                field.get(entity) == null ? null : field.get(entity).toString());
                    }

                }
            }
        }
        return arrayList;
    }
}
