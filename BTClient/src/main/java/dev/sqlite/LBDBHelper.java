
package dev.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 管理数据库的创建和版本更新
 * @author Richx
 */
public class LBDBHelper extends SQLiteOpenHelper {
    /**
     * Interface 数据库更新回调
     */
    public interface OnSQLiteUpdateListener {
        void onCreate(SQLiteDatabase db);

        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

    /**
     * 数据库更新监听器
     */
    private OnSQLiteUpdateListener mUpdateListener;

    /**
     * 构造函数
     *
     * @param context            上下文
     * @param name               数据库名字
     * @param factory            可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标
     * @param version            数据库版本
     * @param updateListener 数据库更新监听器
     */
    public LBDBHelper(Context context, String name, CursorFactory factory, int version, OnSQLiteUpdateListener updateListener) {
        super(context, name, factory, version);
        this.mUpdateListener = updateListener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mUpdateListener != null) {
            mUpdateListener.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (mUpdateListener != null) {
            mUpdateListener.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
