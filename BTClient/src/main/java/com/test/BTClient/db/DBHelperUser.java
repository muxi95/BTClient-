package com.test.BTClient.db;

import android.content.Context;

import com.test.BTClient.model.UserInfo;

import java.util.ArrayList;

import dev.sqlite.LBSQLiteDatabase;
import dev.sqlite.entity.LBHashMap;

/**
 * @author Richx
 */
public class DBHelperUser {
    //添加
    public static Boolean addUserInfo(Context context, UserInfo userInfo) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + userInfo.getId() + "'";
        UserInfo uiOld = database.queryEntity(UserInfo.class, false, where, null, null, null, null);
        if (uiOld == null) {
            return database.insert(userInfo);
        } else {
            return false;
        }
    }

    //删除
    public static Boolean delUserInfo(Context context, UserInfo userInfo) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + userInfo.getId() + "'";
        UserInfo uiOld = database.queryEntity(UserInfo.class, false, where, null, null, null, null);
        if (uiOld != null) {
            return database.delete(userInfo);
        } else {
            return false;
        }
    }

    //获得id
    public static UserInfo getUserInfo(Context context, String id) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "id = '" + id + "'";
        UserInfo userInfo = database.queryEntity(UserInfo.class, false, where, null, null, null, null);
        return userInfo;
    }

    //获得userid
    public static UserInfo getUserid(Context context, String userid) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        String where = "userid = '" + userid + "'";
        UserInfo user= database.queryEntity(UserInfo.class, false, where, null, null, null, null);
        return user;
    }

    //获得最大id
    public static ArrayList<LBHashMap<String>> getMaxUserInfoid(Context context) {
        LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(context);
        ArrayList<LBHashMap<String>> ret = database.query("SELECT MAX(id) FROM UserInfo", null);
        return ret;
    }
}
