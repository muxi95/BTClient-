package dev.sqlite.util;

import java.util.ArrayList;

import dev.sqlite.entity.KeyValuePair;

/**
 * @author Richx
 */
public class SQLParams {
    public ArrayList<KeyValuePair> KVList = new ArrayList<KeyValuePair>();

    public void addParam(KeyValuePair kvp) {
        KVList.add(kvp);
    }

    public void addParam(String key, Object value) {
        KeyValuePair kvp = new KeyValuePair();
        kvp.Key = key;
        kvp.Value = value;
        KVList.add(kvp);
    }
}
