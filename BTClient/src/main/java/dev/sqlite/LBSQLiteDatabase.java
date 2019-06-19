package dev.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import java.util.ArrayList;
import java.util.List;

import dev.alog.ALog;
import dev.sqlite.LBDBHelper.OnSQLiteUpdateListener;
import dev.sqlite.entity.LBArrayList;
import dev.sqlite.entity.LBDBMasterEntity;
import dev.sqlite.entity.LBHashMap;
import dev.sqlite.entity.LBMapArrayList;
import dev.sqlite.exception.LBDBException;
import dev.sqlite.exception.LBDBNotOpenException;
import dev.sqlite.util.LBDBUtils;
import dev.sqlite.util.LBSqlBuilderFactory;
import dev.sqlite.util.sql.LBSqlBuilder;

/**
 * 数据库管理类，通过此类进行数据库的操作
 * @author Richx
 */
public class LBSQLiteDatabase {

    /**
     * 当前SQL指令
     */
    private String queryStr = "";

    /**
     * 错误信息
     */
    private String error = "";

    /**
     * 是否已经连接数据库
     */
    private Boolean isConnect = false;

    /**
     * 执行oepn打开数据库时，保存返回的数据库对象
     */
    private SQLiteDatabase mSQLiteDatabase = null;

    private LBDBHelper mDatabaseHelper = null;

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param params  数据参数信息
     */
    public LBSQLiteDatabase(Context context, TADBParams params, OnSQLiteUpdateListener upgradeListener) {
        this.mDatabaseHelper = new LBDBHelper(context, params.getDbName(), null, params.getDbVersion(), upgradeListener);
    }

    /**
     * 打开数据库如果是 isWrite为true,则磁盘满时抛出错误
     *
     * @param isWrite 是否可写
     * @return
     */
    public SQLiteDatabase openDatabase(Boolean isWrite) {
        if (isWrite) {
            mSQLiteDatabase = openWritable();
        } else {
            mSQLiteDatabase = openReadable();
        }
        return mSQLiteDatabase;

    }

    /**
     * 以读写方式打开数据库，一旦数据库的磁盘空间满了，数据库就只能读而不能写,如果满了磁盘空间满了使用此方法会抛出错误。
     *
     * @param
     * @return
     */
    public SQLiteDatabase openWritable() {
        try {
            mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
            isConnect = true;
            // 注销数据库连接配置信息
            // 暂时不写
        } catch (Exception e) {
            isConnect = false;
        }
        return mSQLiteDatabase;
    }

    /**
     * 先以读写方式打开数据库，如果数据库的磁盘空间满了，就会打开失败，当打开失败后会继续尝试以只读方式打开数据库。如果该问题成功解决，
     * 则只读数据库对象就会关闭，然后返回一个可读写的数据库对象。
     *
     * @return 数据库
     */
    public SQLiteDatabase openReadable() {
        try {
            mSQLiteDatabase = mDatabaseHelper.getReadableDatabase();
            isConnect = true;
            // 注销数据库连接配置信息
            // 暂时不写
        } catch (Exception e) {
            isConnect = false;
        }
        return mSQLiteDatabase;
    }

    /**
     * 测试 TASQLiteDatabase是否可用
     *
     * @return
     */
    public Boolean testSQLiteDatabase() {
        if (isConnect) {
            return mSQLiteDatabase.isOpen();
        } else {
            return false;
        }
    }

    /**
     * 获得所有的查询数据集中的数据
     *
     * @return
     */
    public LBMapArrayList<String> getQueryCursorData(Cursor cursor) {
        LBMapArrayList<String> arrayList = null;
        if (cursor != null) {
            try {
                arrayList = new LBMapArrayList();
//                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    arrayList.add(LBDBUtils.getRowData(cursor));
                }
            } catch (Exception e) {
                e.printStackTrace();
                ALog.e("当前数据集获取失败！");
            }
        } else {
            ALog.w("当前数据集不存在！");
        }
        return arrayList;
    }

    /**
     * 取得数据库的表信息
     *
     * @return 数据库的表信息
     */
    public ArrayList<LBDBMasterEntity> getTables() {
        ArrayList<LBDBMasterEntity> tadbMasterArrayList = new ArrayList<LBDBMasterEntity>();
        String sql = "select * from sqlite_master where type='table' order by name";
        if (testSQLiteDatabase()) {
            if (sql != null && !"".equalsIgnoreCase(sql)) {
                this.queryStr = sql;
                Cursor queryCursor = mSQLiteDatabase.rawQuery("select * from sqlite_master where type='table' order by name", null);

                if (queryCursor != null) {
                    while (queryCursor.moveToNext()) {
                        if (queryCursor != null && queryCursor.getColumnCount() > 0) {
                            LBDBMasterEntity tadbMasterEntity = new LBDBMasterEntity();
                            tadbMasterEntity.setType(queryCursor.getString(0));
                            tadbMasterEntity.setName(queryCursor.getString(1));
                            tadbMasterEntity.setTbl_name(queryCursor.getString(2));
                            tadbMasterEntity.setRootpage(queryCursor.getInt(3));
                            tadbMasterEntity.setSql(queryCursor.getString(4));
                            tadbMasterArrayList.add(tadbMasterEntity);
                        }
                    }
                } else {
                    ALog.w("数据库未打开！");
                }
                freeCursor(queryCursor);
            }
        } else {
            ALog.w("数据库未打开！");
        }
        return tadbMasterArrayList;
    }

    /**
     * 判断是否存在某个表,为true则存在，否则不存在
     *
     * @param clazz 类
     * @return true则存在，否则不存在
     */
    public boolean hasTable(Class<?> clazz) {
        String tableName = LBDBUtils.getTableName(clazz);
        return hasTable(tableName);
    }

    /**
     * 判断是否存在某个表,为true则存在，否则不存在
     *
     * @param tableName 需要判断的表名
     * @return true则存在，否则不存在
     */
    public boolean hasTable(String tableName) {
        boolean result = false;
        if (tableName != null && !"".equalsIgnoreCase(tableName)) {
            if (testSQLiteDatabase()) {
                tableName = tableName.trim();
                String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName + "' ";
                if (sql != null && !"".equalsIgnoreCase(sql)) {
                    this.queryStr = sql;
                }
                Cursor queryCursor = mSQLiteDatabase.rawQuery(sql, null);
                if (queryCursor.moveToNext()) {
                    int count = queryCursor.getInt(0);
                    if (count > 0) {
                        result = true;
                    }
                }
                freeCursor(queryCursor);
            } else {
                ALog.w("数据库未打开！");
            }
        } else {
            ALog.w("判断数据表名不能为空！");
        }
        return result;
    }

    /**
     * 创建表
     *
     * @param clazz
     * @return 为true创建成功，为false创建失败
     */
    public Boolean creatTable(Class<?> clazz) {
        boolean isSuccess;
        if (testSQLiteDatabase()) {
            try {
                String sqlString = LBDBUtils.creatTableSql(clazz);
                execute(sqlString, null);
                isSuccess = true;
            } catch (LBDBException e) {
                isSuccess = false;
                e.printStackTrace();
                ALog.e(e.getMessage());
            } catch (LBDBNotOpenException e) {
                isSuccess = false;
                e.printStackTrace();
                ALog.e(e.getMessage());
            }
        } else {
            ALog.w("数据库未打开！");
            return false;
        }
        return isSuccess;
    }

    public Boolean dropTable(Class<?> clazz) {
        String tableName = LBDBUtils.getTableName(clazz);
        return dropTable(tableName);
    }

    /**
     * 删除表
     *
     * @param tableName 表名
     * @return 为true创建成功，为false创建失败
     */
    public Boolean dropTable(String tableName) {
        boolean isSuccess = false;
        if (tableName != null && !"".equalsIgnoreCase(tableName)) {
            if (testSQLiteDatabase()) {
                try {
                    String sqlString = "DROP TABLE " + tableName;
                    execute(sqlString, null);
                    isSuccess = true;
                } catch (Exception e) {
                    isSuccess = false;
                    e.printStackTrace();
                    ALog.e(e.getMessage());
                }
            } else {
                ALog.w("数据库未打开！");
                return false;
            }
        } else {
            ALog.w("删除数据表名不能为空！");
        }
        return isSuccess;
    }

    /**
     * 更新表用于对实体修改时，改变表 暂时不写
     *
     * @param tableName 表名
     * @return 结果
     */
    public Boolean alterTable(String tableName) {
        return false;
    }

    /**
     * 数据库错误信息 并显示当前的SQL语句
     *
     * @return 错误信息
     */
    public String error() {
        if (this.queryStr != null && !"".equalsIgnoreCase(queryStr)) {
            error = error + "\n [ SQL语句 ] : " + queryStr;
        }
        ALog.w(error);
        return error;
    }

    // [start] query操作

    /**
     * 执行查询，主要是SELECT, SHOW 等指令 返回数据集
     *
     * @param sql           sql语句
     * @param selectionArgs
     * @return 查询数据集
     */
    public ArrayList<LBHashMap<String>> query(String sql, String[] selectionArgs) {
        if (testSQLiteDatabase()) {
            if (sql != null && !"".equalsIgnoreCase(sql)) {
                this.queryStr = sql;
            }
            ArrayList<LBHashMap<String>> result = null;
            Cursor queryCursor = mSQLiteDatabase.rawQuery(sql, selectionArgs);
            if (queryCursor != null) {
                result = getQueryCursorData(queryCursor);
            } else {
                ALog.w("执行" + sql + "错误");
            }
            freeCursor(queryCursor);
            return result;
        } else {
            ALog.w("数据库未打开！");
        }
        return null;
    }

    /**
     * 执行查询，主要是SELECT, SHOW 等指令 返回数据集
     *
     * @param clazz 类
     * @param distinct 限制重复，如过为true则限制,false则不用管
     * @param where    where语句
     * @param groupBy  groupBy语句
     * @param having   having语句
     * @param orderBy  orderBy语句
     * @param limit    limit语句
     * @return 类表
     */
    public <T> List<T> queryList(Class<T> clazz, boolean distinct, String where, String groupBy, String having, String orderBy, String limit) {
        if (testSQLiteDatabase()) {
            List<T> list = null;
            LBSqlBuilder getSqlBuilder = LBSqlBuilderFactory.getInstance().getSqlBuilder(LBSqlBuilderFactory.SELECT);
            getSqlBuilder.setClazz(clazz);
            getSqlBuilder.setCondition(distinct, where, groupBy, having, orderBy, limit);
            Cursor queryCursor = null;
            try {
                String sqlString = getSqlBuilder.getSqlStatement();
                queryCursor = mSQLiteDatabase.rawQuery(sqlString, null);
                list = LBDBUtils.getListEntity(clazz, queryCursor);
            } catch (IllegalArgumentException e) {
                ALog.e(e.getMessage());
                e.printStackTrace();
            } catch (LBDBException e) {
                ALog.e(e.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                ALog.e(e.getMessage());
                e.printStackTrace();
            } finally {
                freeCursor(queryCursor);
            }
            return list;
        } else {
            return null;
        }
    }

    /**
     * 查询单个实体，主要是SELECT, SHOW 等指令
     *
     * @param clazz 类
     * @param distinct 限制重复，如过为true则限制,false则不用管
     * @param where    where语句
     * @param groupBy  groupBy语句
     * @param having   having语句
     * @param orderBy  orderBy语句
     * @param limit    limit语句
     * @return         查询结果
     */
    public <T> T queryEntity(Class<T> clazz, boolean distinct, String where, String groupBy, String having, String orderBy, String limit) {
        if (testSQLiteDatabase()) {
            T t = null;
            LBSqlBuilder getSqlBuilder = LBSqlBuilderFactory.getInstance().getSqlBuilder(LBSqlBuilderFactory.SELECT);
            getSqlBuilder.setClazz(clazz);
            getSqlBuilder.setCondition(distinct, where, groupBy, having, orderBy, limit);
            Cursor queryCursor = null;
            try {
                String sqlString = getSqlBuilder.getSqlStatement();
                ALog.i("执行" + sqlString);
                queryCursor = mSQLiteDatabase.rawQuery(sqlString, null);
                t = LBDBUtils.getEntity(clazz, queryCursor);
            } catch (IllegalArgumentException e) {
                ALog.e(e.getMessage());
                e.printStackTrace();
            } catch (LBDBException e) {
                ALog.e(e.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                ALog.e(e.getMessage());
                e.printStackTrace();
            } finally {
                freeCursor(queryCursor);
            }
            return t;
        } else {
            return null;
        }
    }

    /**
     * 查询记录
     *
     * @param table         表名
     * @param columns       需要查询的列
     * @param selection     格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
     * @param selectionArgs 参数
     * @param groupBy       groupBy语句
     * @param having        having语句
     * @param orderBy       orderBy语句
     * @return              查询结果数组
     */
    public ArrayList<LBHashMap<String>> query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                                              String having, String orderBy) {
        if (testSQLiteDatabase()) {
            ArrayList<LBHashMap<String>> result = null;
            Cursor queryCursor = mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            if (queryCursor != null) {
                result = getQueryCursorData(queryCursor);
            } else {
                ALog.w("查询" + table + "错误");
            }
            freeCursor(queryCursor);
            return result;
        } else {
            ALog.w("数据库未打开！");
        }
        return null;
    }

    /**
     * 查询记录
     *
     * @param distinct      限制重复，如过为true则限制,false则不用管
     * @param table         表名
     * @param columns       需要查询的列
     * @param selection     格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
     * @param selectionArgs 参数
     * @param groupBy       groupBy语句
     * @param having        having语句
     * @param orderBy       orderBy语句
     * @param limit         limit语句
     * @return 数组
     */
    public ArrayList<LBHashMap<String>> query(String table, boolean distinct, String[] columns, String selection, String[] selectionArgs,
                                              String groupBy, String having, String orderBy, String limit) {
        if (testSQLiteDatabase()) {
            ArrayList<LBHashMap<String>> result = null;
            Cursor queryCursor = mSQLiteDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (queryCursor != null) {
                result = getQueryCursorData(queryCursor);
            } else {
                ALog.e("查询" + table + "错误");
            }
            freeCursor(queryCursor);
            return result;
        } else {
            ALog.e("数据库未打开！");
        }
        return null;
    }

    /**
     * 查询记录
     *
     * @param table         表名
     * @param columns       需要查询的列
     * @param selection     格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
     * @param selectionArgs 参数
     * @param groupBy       groupBy语句
     * @param having        having语句
     * @param orderBy       orderBy语句
     * @param limit         limit语句
     * @return              数组
     */
    public ArrayList<LBHashMap<String>> query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                                              String having, String orderBy, String limit) {
        if (testSQLiteDatabase()) {
            ArrayList<LBHashMap<String>> result = null;
            Cursor queryCursor = mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (queryCursor != null) {
                result = getQueryCursorData(queryCursor);
            } else {
                ALog.w("查询" + table + "错误");
            }
            freeCursor(queryCursor);
            return result;
        } else {
            ALog.w("数据库未打开！");
        }
        return null;
    }

    /**
     * 查询记录
     *
     * @param cursorFactory
     * @param distinct      限制重复，如过为true则限制,false则不用管
     * @param table         表名
     * @param columns       需要查询的列
     * @param selection     格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
     * @param selectionArgs
     * @param groupBy       groupBy语句
     * @param having        having语句
     * @param orderBy       orderBy语句
     * @param limit         limit语句
     * @return              查询结果数组
     */
    public ArrayList<LBHashMap<String>> queryWithFactory(CursorFactory cursorFactory, boolean distinct, String table, String[] columns,
                                                         String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        if (testSQLiteDatabase()) {
            ArrayList<LBHashMap<String>> result = null;
            Cursor queryCursor = mSQLiteDatabase.queryWithFactory(cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having,
                    orderBy, limit);
            if (queryCursor != null) {
                result = getQueryCursorData(queryCursor);
            } else {
                ALog.w("查询" + table + "错误");
            }
            freeCursor(queryCursor);
            return result;
        } else {
            ALog.w("数据库未打开！");
        }
        return null;
    }

    // [end]

    // [start] execute操作

    /**
     * INSERT, UPDATE 以及DELETE
     *
     * @param sql      语句
     * @param bindArgs
     * @throws LBDBNotOpenException
     */
    public void execute(String sql, String[] bindArgs) throws LBDBNotOpenException {
        if (testSQLiteDatabase()) {
            if (sql != null && !"".equalsIgnoreCase(sql)) {
                this.queryStr = sql;
                if (bindArgs != null) {
                    mSQLiteDatabase.execSQL(sql, bindArgs);
                } else {
                    mSQLiteDatabase.execSQL(sql);
                }
            }
        } else {
            throw new LBDBNotOpenException("数据库未打开！");
        }
    }

    /**
     * 执行INSERT, UPDATE 以及DELETE操作
     *
     * @param getSqlBuilder Sql语句构建器
     * @return
     */
    public Boolean execute(LBSqlBuilder getSqlBuilder) {
        boolean isSuccess;
        String sqlString;
        try {
            sqlString = getSqlBuilder.getSqlStatement();
            execute(sqlString, null);
            isSuccess = true;
        } catch (IllegalArgumentException e) {
            isSuccess = false;
            e.printStackTrace();

        } catch (LBDBException e) {
            isSuccess = false;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            isSuccess = false;
            e.printStackTrace();
        } catch (LBDBNotOpenException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    // [end]

    // [start] insert操作

    /**
     * 插入记录
     *
     * @param entity 插入的实体
     * @return 结果
     */
    public Boolean insert(Object entity) {
        return insert(entity, null);
    }

    /**
     * 将一个数据集插入到相应的表内
     *
     * @param list 数据集
     */
    public void insertList(List<?> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                this.insert(list.get(i));
            }
        }
    }

    /**
     * 插入记录
     *
     * @param table          需要插入到的表
     * @param nullColumnHack 不允许为空的行
     * @param values         插入的值
     * @return  返回true执行成功，否则执行失败
     */
    public Boolean insert(String table, String nullColumnHack, ContentValues values) {
        if (testSQLiteDatabase()) {
            return mSQLiteDatabase.insert(table, nullColumnHack, values) > 0;
        } else {
            ALog.w("数据库未打开！");
            return false;
        }
    }

    /**
     * 插入记录
     *
     * @param table          需要插入到的表
     * @param nullColumnHack 不允许为空的行
     * @param values         插入的值
     * @return  返回true执行成功，否则执行失败
     */
    public Boolean insertOrThrow(String table, String nullColumnHack, ContentValues values) {
        if (testSQLiteDatabase()) {
            return mSQLiteDatabase.insertOrThrow(table, nullColumnHack, values) > 0;
        } else {
            ALog.w("数据库未打开！");
            return false;
        }
    }

    /**
     * 插入记录
     *
     * @param entity       传入数据实体
     * @param updateFields 插入到的字段,可设置为空
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean insert(Object entity, LBArrayList updateFields) {

        LBSqlBuilder getSqlBuilder = LBSqlBuilderFactory.getInstance().getSqlBuilder(LBSqlBuilderFactory.INSERT);
        getSqlBuilder.setEntity(entity);
        getSqlBuilder.setUpdateFields(updateFields);
        return execute(getSqlBuilder);
    }

    // [end]

    // [start] delete操作

    /**
     * 删除记录
     *
     * @param table       被删除的表名
     * @param whereClause 设置的WHERE子句时，删除指定的数据 ,如果null会删除所有的行。
     * @param whereArgs
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean delete(String table, String whereClause, String[] whereArgs) {
        if (testSQLiteDatabase()) {
            return mSQLiteDatabase.delete(table, whereClause, whereArgs) > 0;

        } else {
            ALog.w("数据库未打开！");
            return false;
        }
    }

    /**
     * 删除记录
     *
     * @param clazz
     * @param where where语句
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean delete(Class<?> clazz, String where) {
        if (testSQLiteDatabase()) {
            LBSqlBuilder getSqlBuilder = LBSqlBuilderFactory.getInstance().getSqlBuilder(LBSqlBuilderFactory.DELETE);
            getSqlBuilder.setClazz(clazz);
            getSqlBuilder.setCondition(false, where, null, null, null, null);
            return execute(getSqlBuilder);
        } else {
            return false;
        }

    }

    /**
     * 删除记录
     *
     * @param entity
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean delete(Object entity) {
        if (testSQLiteDatabase()) {
            LBSqlBuilder getSqlBuilder = LBSqlBuilderFactory.getInstance().getSqlBuilder(LBSqlBuilderFactory.DELETE);
            getSqlBuilder.setEntity(entity);
            return execute(getSqlBuilder);
        } else {
            return false;
        }
    }

    // [end]

    // [start] update操作

    /**
     * 更新记录
     *
     * @param table       表名字
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (testSQLiteDatabase()) {
            return mSQLiteDatabase.update(table, values, whereClause, whereArgs) > 0;
        } else {
            ALog.w("数据库未打开！");
            return false;
        }
    }

    public Boolean update(Class<?> clazz, ContentValues values, String whereClause, String[] whereArgs) {
        String table = LBDBUtils.getTableName(clazz);
        return update(table, values, whereClause, whereArgs);
    }

    /**
     * 更新记录 这种更新方式只有才主键不是自增的情况下可用
     *
     * @param entity 更新的数据
     * @return 返回true执行成功，否则执行失败
     */
    public Boolean update(Object entity) {
        return update(entity, null);
    }

    /**
     * 更新记录
     *
     * @param entity 更新的数据
     * @param where  where语句
     * @return
     */
    public Boolean update(Object entity, String where) {
        if (testSQLiteDatabase()) {
            LBSqlBuilder getSqlBuilder = LBSqlBuilderFactory.getInstance().getSqlBuilder(LBSqlBuilderFactory.UPDATE);
            getSqlBuilder.setEntity(entity);
            getSqlBuilder.setCondition(false, where, null, null, null, null);
            return execute(getSqlBuilder);
        } else {
            return false;
        }
    }

    // [end]

    /**
     * 获取最近一次查询的sql语句
     *
     * @return sql 语句
     */
    public String getLastSql() {
        return queryStr;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        mSQLiteDatabase.close();
    }

    /**
     * 释放查询结果
     */
    public void freeCursor(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 数据库配置参数
     */
    public static class TADBParams {
        private String dbName;

        private int dbVersion;

        public TADBParams(String dbName, int dbVersion) {
            this.dbName = dbName;
            this.dbVersion = dbVersion;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public int getDbVersion() {
            return dbVersion;
        }

        public void setDbVersion(int dbVersion) {
            this.dbVersion = dbVersion;
        }
    }
}
