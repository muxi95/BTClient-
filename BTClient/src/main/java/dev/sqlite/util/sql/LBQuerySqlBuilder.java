/*
 * 文 件 名:  QuerySqlBuilder.java
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

import java.util.regex.Pattern;

import dev.sqlite.exception.LBDBException;

/**
 * 查询sql语句构建器类
 * @author Richx
 */
public class LBQuerySqlBuilder extends LBSqlBuilder {
    protected Pattern sLimitPattern = Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");

    @Override
    public String buildSql() throws LBDBException, IllegalArgumentException, IllegalAccessException {
        // TODO Auto-generated method stub
        return buildQueryString();
    }

    /**
     * 创建查询的字段
     *
     * @param distinct      限制重复，如过为true则限制,false则不用管
     * @param table         表名
     * @param columns       需要查询的列
     * @param selection     格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
     * @param selectionArgs
     * @param groupBy       groupBy语句
     * @param having        having语句
     * @param orderBy       orderBy语句
     * @param limit         limit语句
     * @return
     */
    public String buildQueryString() {
        if (TextUtils.isEmpty(groupBy) && !TextUtils.isEmpty(having)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        if (!TextUtils.isEmpty(limit) && !sLimitPattern.matcher(limit).matches()) {
            throw new IllegalArgumentException("invalid LIMIT clauses:" + limit);
        }
        StringBuilder query = new StringBuilder(120);
        query.append("SELECT ");
        if (distinct) {
            query.append("DISTINCT ");
        }
        query.append("* ");
        query.append("FROM ");
        query.append(tableName);
        appendClause(query, " WHERE ", where);
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);
        appendClause(query, " ORDER BY ", orderBy);
        appendClause(query, " LIMIT ", limit);
        return query.toString();
    }
}
