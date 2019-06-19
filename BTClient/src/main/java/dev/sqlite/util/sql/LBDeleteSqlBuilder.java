/*
 * 文 件 名:  DeleteSqlBuilder.java
 * 版    权:  jiang yu feng
 * 描    述:  <描述>
 * 修 改 人:  江钰锋
 * 修改时间:  2013-12-18
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package dev.sqlite.util.sql;

import java.lang.reflect.Field;

import dev.sqlite.entity.LBArrayList;
import dev.sqlite.exception.LBDBException;
import dev.sqlite.util.LBDBUtils;

/**
 * 删除sql语句构建器类
 * @author Richx
 */
public class LBDeleteSqlBuilder extends LBSqlBuilder {
    @Override
    public String buildSql() throws IllegalArgumentException, IllegalAccessException, LBDBException {
        // TODO Auto-generated method stub
        StringBuilder stringBuilder = new StringBuilder(256);
        stringBuilder.append("DELETE FROM ");
        stringBuilder.append(tableName);
        if (entity == null) {
            stringBuilder.append(buildConditionString());
        } else {
            stringBuilder.append(buildWhere(buildWhere(this.entity)));
        }

        return stringBuilder.toString();
    }

    /**
     * 创建Where语句
     *
     * @param entity
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws LBDBException
     */
    public LBArrayList buildWhere(Object entity) throws IllegalArgumentException, IllegalAccessException, LBDBException {
        Class<?> clazz = entity.getClass();
        LBArrayList whereArrayList = new LBArrayList();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!LBDBUtils.isTransient(field)) {
                if (LBDBUtils.isBaseDateType(field)) {
                    // 如果ID不是自动增加的
                    if (!LBDBUtils.isAutoIncrement(field)) {
                        String columnName = LBDBUtils.getColumnByField(field);
                        if (null != field.get(entity) && field.get(entity).toString().length() > 0) {
                            whereArrayList.add((columnName != null && !"".equals(columnName)) ? columnName : field.getName(), field.get(entity)
                                    .toString());
                        }
                    }
                }
            }
        }
        if (whereArrayList.isEmpty()) {
            throw new LBDBException("不能创建Where条件，语句");
        }
        return whereArrayList;
    }
}
