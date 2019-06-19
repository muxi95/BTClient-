/*
 * 文 件 名:  SqlBuilderFactory.java
 * 版    权:  jiang yu feng
 * 描    述:  <描述>
 * 修 改 人:  江钰锋
 * 修改时间:  2013-12-18
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package dev.sqlite.util;

import dev.sqlite.util.sql.LBDeleteSqlBuilder;
import dev.sqlite.util.sql.LBInsertSqlBuilder;
import dev.sqlite.util.sql.LBQuerySqlBuilder;
import dev.sqlite.util.sql.LBSqlBuilder;
import dev.sqlite.util.sql.LBUpdateSqlBuilder;

/**
 * Sql构建器工厂,生成sql语句构建器
 * @author Richx
 */
public class LBSqlBuilderFactory {
    private static LBSqlBuilderFactory instance;
    /**
     * 调用getSqlBuilder(int operate)返回插入sql语句构建器传入的参数
     */
    public static final int INSERT = 0;
    /**
     * 调用getSqlBuilder(int operate)返回查询sql语句构建器传入的参数
     */
    public static final int SELECT = 1;
    /**
     * 调用getSqlBuilder(int operate)返回删除sql语句构建器传入的参数
     */
    public static final int DELETE = 2;
    /**
     * 调用getSqlBuilder(int operate)返回更新sql语句构建器传入的参数
     */
    public static final int UPDATE = 3;

    /**
     * 单例模式获得Sql构建器工厂
     *
     * @return sql构建器
     */
    public static LBSqlBuilderFactory getInstance() {
        if (instance == null) {
            instance = new LBSqlBuilderFactory();
        }
        return instance;
    }

    /**
     * 获得sql构建器
     *
     * @param operate
     * @return 构建器
     */
    public synchronized LBSqlBuilder getSqlBuilder(int operate) {
        LBSqlBuilder sqlBuilder = null;
        switch (operate) {
            case INSERT:
                sqlBuilder = new LBInsertSqlBuilder();
                break;
            case SELECT:
                sqlBuilder = new LBQuerySqlBuilder();
                break;
            case DELETE:
                sqlBuilder = new LBDeleteSqlBuilder();
                break;
            case UPDATE:
                sqlBuilder = new LBUpdateSqlBuilder();
                break;
            default:
                break;
        }
        return sqlBuilder;
    }
}
