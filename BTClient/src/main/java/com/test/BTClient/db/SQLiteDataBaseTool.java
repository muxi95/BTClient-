package com.test.BTClient.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.test.BTClient.model.Batch;
import com.test.BTClient.model.File;
import com.test.BTClient.model.Rizhi;
import com.test.BTClient.model.Sadd;
import com.test.BTClient.model.Sample;
import com.test.BTClient.model.UserInfo;

import dev.sqlite.LBDBHelper.OnSQLiteUpdateListener;
import dev.sqlite.LBSQLiteDatabase;
import dev.sqlite.LBSQLiteDatabase.TADBParams;
import dev.sqlite.LBSQLiteDatabasePool;
import dev.sqlite.exception.LBDBException;
import dev.sqlite.util.LBDBUtils;

/**
 * @author Zhou Xiumin
 */
public class SQLiteDataBaseTool {
    private static final String DB_NAME = "bluetooth.db";
    private static final int DB_VERSION = 10;

    private static LBSQLiteDatabasePool mSQLiteDatabasePool;

    private static LBSQLiteDatabase mDatabase;

    private static LBSQLiteDatabasePool getSQLiteDatabasePool(Context context) {
        if (mSQLiteDatabasePool == null) {
            TADBParams params = new TADBParams(DB_NAME, DB_VERSION);
            mSQLiteDatabasePool = LBSQLiteDatabasePool.getInstance(context, params, true);
            mSQLiteDatabasePool.setOnSQLiteUpdateListener(new OnSQLiteUpdateListener() {
                @Override
                public void onCreate(SQLiteDatabase db) {
                    createAllTable(db);
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    createAllTable(db);
                }
            });
            mSQLiteDatabasePool.createPool();
        }
        return mSQLiteDatabasePool;
    }

    public static LBSQLiteDatabase getSQLiteDatabase(Context context) {
        if (mDatabase == null) {
            mDatabase = getSQLiteDatabasePool(context).getSQLiteDatabase();
        }
        return mDatabase;
    }

    private static void createAllTable(SQLiteDatabase db) {
        createTable(db, UserInfo.class);
        createTable(db, Batch.class);
        createTable(db, Rizhi.class);
        createTable(db, Sample.class);
        createTable(db, File.class);
        createTable(db, Sadd.class);
    }

    private static void createTable(SQLiteDatabase db, Class<?> clazz) {
        try {
            String sql = "DROP TABLE IF EXISTS " + LBDBUtils.getTableName(clazz);
            db.execSQL(sql);
            db.execSQL(LBDBUtils.creatTableSql(clazz));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (LBDBException e) {
            e.printStackTrace();
        }
    }
}
