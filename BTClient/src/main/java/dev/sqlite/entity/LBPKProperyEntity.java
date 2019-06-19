package dev.sqlite.entity;

/**
 * @author Richx
 */
public class LBPKProperyEntity extends LBPropertyEntity {

    public LBPKProperyEntity() {

    }

    public LBPKProperyEntity(String name, Class<?> type, Object defaultValue, boolean primaryKey, boolean isAllowNull,
                             boolean autoIncrement, String columnName) {
        super(name, type, defaultValue, primaryKey, isAllowNull, autoIncrement, columnName);
        // TODO Auto-generated constructor stub
    }

}
