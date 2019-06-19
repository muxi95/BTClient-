package dev.sqlite.entity;

import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * NameValuePair数组 NameValuePair数组
 * @author Richx
 */
public class LBArrayList extends ArrayList<NameValuePair> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(NameValuePair nameValuePair) {
        if (!TextUtils.isEmpty(nameValuePair.getValue())) {
            return super.add(nameValuePair);
        } else {
            return false;
        }
    }

    /**
     * 添加数据
     *
     * @param key
     * @param value
     * @return
     */
    public boolean add(String key, String value) {
        return add(new BasicNameValuePair(key, value));
    }
}
