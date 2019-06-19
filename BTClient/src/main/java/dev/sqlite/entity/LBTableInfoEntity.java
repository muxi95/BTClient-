package dev.sqlite.entity;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Richx
 */
public class LBTableInfoEntity extends BaseEntity {
    private static final long serialVersionUID = 488168612576359150L;
    private String tableName = "";
    private String className = "";
    private LBPKProperyEntity pkProperyEntity = null;

    ArrayList<LBPropertyEntity> propertieArrayList = new ArrayList<LBPropertyEntity>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<LBPropertyEntity> getPropertieArrayList() {
        return propertieArrayList;
    }

    public void setPropertieArrayList(List<LBPropertyEntity> propertyList) {
        this.propertieArrayList = (ArrayList<LBPropertyEntity>) propertyList;
    }

    public LBPKProperyEntity getPkProperyEntity() {
        return pkProperyEntity;
    }

    public void setPkProperyEntity(LBPKProperyEntity pkProperyEntity) {
        this.pkProperyEntity = pkProperyEntity;
    }

}
