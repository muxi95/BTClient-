/*
 * 文 件 名:  SqlBuilder.java
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

import dev.sqlite.entity.LBArrayList;
import dev.sqlite.exception.LBDBException;
import dev.sqlite.util.DBUtils;
import dev.sqlite.util.LBDBUtils;

/**
 * sql语句构建器基类
 * @author Richx
 */
public abstract class LBSqlBuilder {
    protected Boolean distinct;
    protected String where;
    protected String groupBy;
    protected String having;
    protected String orderBy;
    protected String limit;
    protected Class<?> clazz = null;
    protected String tableName = null;
    protected Object entity;
    protected LBArrayList updateFields;

    public LBSqlBuilder(Object entity) {
        this.entity = entity;
        setClazz(entity.getClass());
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
        setClazz(entity.getClass());
    }

    public void setCondition(boolean distinct, String where, String groupBy, String having, String orderBy, String limit) {
        this.distinct = distinct;
        this.where = where;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
        this.limit = limit;
    }

    public LBArrayList getUpdateFields() {
        return updateFields;
    }

    public void setUpdateFields(LBArrayList updateFields) {
        this.updateFields = updateFields;
    }

    public LBSqlBuilder() {
    }

    public LBSqlBuilder(Class<?> clazz) {
        setTableName(clazz);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setTableName(Class<?> clazz) {
        this.tableName = LBDBUtils.getTableName(clazz);
    }

    public String getTableName() {
        return tableName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        setTableName(clazz);
        this.clazz = clazz;
    }

    /**
     * 获取sql语句
     *
     * @return
     * @throws LBDBException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public String getSqlStatement() throws LBDBException, IllegalArgumentException, IllegalAccessException {
        onPreGetStatement();
        return buildSql();
    }

    /**
     * 构建sql语句前执行方法
     *
     * @return
     * @throws LBDBException
     */
    public void onPreGetStatement() throws LBDBException, IllegalArgumentException, IllegalAccessException {

    }

    /**
     * 构建sql语句
     *
     * @return
     * @throws LBDBException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public abstract String buildSql() throws LBDBException, IllegalArgumentException, IllegalAccessException;

    /**
     * 创建条件字句
     *
     * @return 返回条件Sql
     */
    protected String buildConditionString() {
        StringBuilder query = new StringBuilder(120);
        appendClause(query, " WHERE ", where);
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);
        appendClause(query, " ORDER BY ", orderBy);
        appendClause(query, " LIMIT ", limit);
        return query.toString();
    }

    protected void appendClause(StringBuilder s, String name, String clause) {
        if (!TextUtils.isEmpty(clause)) {
            s.append(name);
            s.append(clause);
        }
    }

    /**
     * 构建where子句
     *
     * @param conditions TAArrayList类型的where数据
     * @return 返回where子句
     */
    public String buildWhere(LBArrayList conditions) {
        StringBuilder stringBuilder = new StringBuilder(256);
        if (conditions != null) {
            stringBuilder.append(" WHERE ");
            for (int i = 0; i < conditions.size(); i++) {
                NameValuePair nameValuePair = conditions.get(i);
                stringBuilder
                        .append(nameValuePair.getName())
                        .append(" = ")
                        .append(DBUtils.isNumeric(nameValuePair.getValue())
                                ? nameValuePair.getValue() : "'" + nameValuePair.getValue()
                                + "'");
                if (i + 1 < conditions.size()) {
                    stringBuilder.append(" AND ");
                }
            }
        }
        return stringBuilder.toString();
    }
}
