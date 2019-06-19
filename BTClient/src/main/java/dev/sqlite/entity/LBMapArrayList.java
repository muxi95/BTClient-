package dev.sqlite.entity;

import java.util.ArrayList;

/**
 * @author Richx
 */
public class LBMapArrayList<T extends Object> extends ArrayList<LBHashMap<T>> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(LBHashMap<T> taHashMap) {
        if (taHashMap != null) {
            return super.add(taHashMap);
        } else {
            return false;
        }
    }
}
