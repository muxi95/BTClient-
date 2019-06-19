package dev.sqlite.entity;

/**
 * 数据库的字段
 * @author Richx
 */
public class LBPropertyEntity {
    /**
     * 字段名
     */
    protected String name;

    /**
     * 字段存在数据库的中名字
     */
    protected String columnName;

    /**
     * 字段类型
     */
    protected Class<?> type;

    /**
     * 字段默认值
     */
    protected Object defaultValue;

    /**
     * 是否允许为空
     */
    protected boolean isAllowNull = true;

    /**
     * 下标
     */
    protected int index; // 暂时不写

    /**
     * 是否是主键
     */
    protected boolean primaryKey = false;

    /**
     * 是否支持自动增量
     */
    protected boolean autoIncrement = false;

    public LBPropertyEntity() {

    }

    public LBPropertyEntity(String name, Class<?> type, Object defaultValue, boolean primaryKey, boolean isAllowNull,
                            boolean autoIncrement, String columnName) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.primaryKey = primaryKey;
        this.isAllowNull = isAllowNull;
        this.autoIncrement = autoIncrement;
        this.columnName = columnName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isAllowNull() {
        return isAllowNull;
    }

    public void setAllowNull(boolean isAllowNull) {
        this.isAllowNull = isAllowNull;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
