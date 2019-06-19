package dev.sqlite.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import dev.sqlite.entity.LBPKProperyEntity;
import dev.sqlite.entity.LBPropertyEntity;
import dev.sqlite.entity.LBTableInfoEntity;
import dev.sqlite.exception.LBDBException;

/**
 * 数据库表工厂类
 * @author Richx
 */
public class LBTableInfofactory {
    /**
     * 表名为键，表信息为值的HashMap
     */
    private static final HashMap<String, LBTableInfoEntity> tableInfoEntityMap = new HashMap<String, LBTableInfoEntity>();

    private LBTableInfofactory() {

    }

    private static LBTableInfofactory instance;

    /**
     * 获得数据库表工厂
     *
     * @return 数据库表工厂
     */
    public static LBTableInfofactory getInstance() {
        if (instance == null) {
            instance = new LBTableInfofactory();
        }
        return instance;
    }

    /**
     * 获得表信息
     *
     * @param clazz 实体类型
     * @return 表信息
     * @throws LBDBException
     */
    public LBTableInfoEntity getTableInfoEntity(Class<?> clazz) throws LBDBException {
        if (clazz == null) {
            throw new LBDBException("表信息获取失败，应为class为null");
        }
        LBTableInfoEntity tableInfoEntity = tableInfoEntityMap.get(clazz.getName());
        if (tableInfoEntity == null) {
            tableInfoEntity = new LBTableInfoEntity();
            tableInfoEntity.setTableName(LBDBUtils.getTableName(clazz));
            tableInfoEntity.setClassName(clazz.getName());
            Field idField = LBDBUtils.getPrimaryKeyField(clazz);
            if (idField != null) {
                LBPKProperyEntity pkProperyEntity = new LBPKProperyEntity();
                pkProperyEntity.setColumnName(LBDBUtils.getColumnByField(idField));
                pkProperyEntity.setName(idField.getName());
                pkProperyEntity.setType(idField.getType());
                pkProperyEntity.setAutoIncrement(LBDBUtils.isAutoIncrement(idField));
                tableInfoEntity.setPkProperyEntity(pkProperyEntity);
            } else {
                tableInfoEntity.setPkProperyEntity(null);
            }
            List<LBPropertyEntity> propertyList = LBDBUtils.getPropertyList(clazz);
            if (propertyList != null) {
                tableInfoEntity.setPropertieArrayList(propertyList);
            }

            tableInfoEntityMap.put(clazz.getName(), tableInfoEntity);
        }
        if (tableInfoEntity == null || tableInfoEntity.getPropertieArrayList() == null || tableInfoEntity.getPropertieArrayList().size() == 0) {
            throw new LBDBException("不能创建+" + clazz + "的表信息");
        }
        return tableInfoEntity;
    }
}
