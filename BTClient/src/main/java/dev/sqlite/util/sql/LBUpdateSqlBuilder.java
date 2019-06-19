/*
 * 文 件 名:  UpdateSqlBuilder.java
 * 版    权:  jiang yu feng
 * 描    述:  <描述>
 * 修 改 人:  江钰锋
 * 修改时间:  2013-12-18
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package dev.sqlite.util.sql;

import android.text.TextUtils;

import org.apache.http.NameValuePair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import dev.sqlite.annotation.PrimaryKey;
import dev.sqlite.entity.LBArrayList;
import dev.sqlite.exception.LBDBException;
import dev.sqlite.util.DBUtils;
import dev.sqlite.util.LBDBUtils;

/**
 * 更新sql语句构建器类
 * @author Richx
 */
public class LBUpdateSqlBuilder extends LBSqlBuilder {

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
            throws LBDBException, IllegalArgumentException, IllegalAccessException {
        // TODO Auto-generated method stub
        StringBuilder stringBuilder = new StringBuilder(256);
        stringBuilder.append("UPDATE ");
        stringBuilder.append(tableName).append(" SET ");

        LBArrayList needUpdate = getUpdateFields();
        for (int i = 0; i < needUpdate.size(); i++) {
            NameValuePair nameValuePair = needUpdate.get(i);
            stringBuilder.append(nameValuePair.getName())
                    .append(" = ")
                    .append(DBUtils.isNumeric(nameValuePair.getValue().toString()) ? nameValuePair.getValue() : "'"
                            + nameValuePair.getValue() + "'");
            if (i + 1 < needUpdate.size()) {
                stringBuilder.append(", ");
            }
        }
        if (!TextUtils.isEmpty(this.where)) {
            stringBuilder.append(buildConditionString());
        } else {
            stringBuilder.append(buildWhere(buildWhere(this.entity)));
        }
        return stringBuilder.toString();
    }

    /**
     * 创建Where语句
     *
     * @param entity
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws LBDBException
     */
    public LBArrayList buildWhere(Object entity)
            throws IllegalArgumentException, IllegalAccessException, LBDBException {
        Class<?> clazz = entity.getClass();
        LBArrayList whereArrayList = new LBArrayList();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!LBDBUtils.isTransient(field)) {
                if (LBDBUtils.isBaseDateType(field)) {
                    Annotation annotation = field.getAnnotation(PrimaryKey.class);
                    if (annotation != null) {
                        String columnName = LBDBUtils.getColumnByField(field);
                        whereArrayList.add((columnName != null && !"".equals(columnName)) ? columnName
                                        : field.getName(),
                                field.get(entity).toString());
                    }

                }
            }
        }
        if (whereArrayList.isEmpty()) {
            throw new LBDBException("不能创建Where条件，语句");
        }
        return whereArrayList;
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
                    if (annotation == null || !annotation.autoIncrement()) {
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
