package com.geta.dao.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import com.geta.dao.annotation.Entity;
import com.geta.dao.annotation.Id;

public class ConfigEntity {

    public DaoField[] getDAOFields() {
        return checkValidClass(DaoField.toDaoFields(this.getClass().getDeclaredFields(), this));
    }

    public void addValueToPk(Object value) throws Exception {
        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isAnnotationPresent(Id.class)) {
                fields[i].setAccessible(true);
                fields[i].set(this, getType(Class.forName(fields[i].getType().getCanonicalName()), value));
            }
        }
    }

    public String toColumnsList(List<String> list) {
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < list.size(); i++) {
            stringBuffer.append(list.get(i));
            if (i + 1 < list.size())
                stringBuffer.append(" , ");
        }
        return stringBuffer.toString();
    }

    public DaoField getPkField() {
        DaoField[] daoFields = getDAOFields();
        for (int i = 0; i < daoFields.length; i++) {
            if (daoFields[i] != null)
                if (daoFields[i].isId)
                    return daoFields[i];
        }
        return null;
    }

    public String getConditions() throws ClassNotFoundException {
        StringBuffer stringBuffer = new StringBuffer("");
        DaoField[] daoFields = getDAOFields();

        for (int i = 0; i < daoFields.length; i++) {
            if (daoFields[i] == null)
                continue;
            if (daoFields[i].isId)
                continue;
            String classFull = getFullNameClass(daoFields[i]);

            stringBuffer.append(daoFields[i].getName() + " = "
                    + getValueStruct(Class.forName(classFull), daoFields[i].getValue()));
            if (i + 1 < daoFields.length)
                stringBuffer.append(" AND ");
        }
        return stringBuffer.toString();
    }

    public String getColumnsAndValue() throws ClassNotFoundException {
        StringBuffer stringBuffer = new StringBuffer("");
        DaoField[] daoFields = getDAOFields();

        for (int i = 0; i < daoFields.length; i++) {
            if (daoFields[i].isId)
                continue;
            String classFull = getFullNameClass(daoFields[i]);
            stringBuffer.append(daoFields[i].getName() + " = "
                    + getValueStruct(Class.forName(classFull), daoFields[i].getValue()));
            if (i + 1 < daoFields.length)
                stringBuffer.append(" , ");
        }
        return stringBuffer.toString();
    }

    public String getColumnsParams(String param) throws ClassNotFoundException {
        StringBuffer stringBuffer = new StringBuffer("");
        DaoField[] daoFields = getDAOFields();
        for (int i = 0; i < daoFields.length; i++) {
            if (param.equals("columns")) {
                stringBuffer.append(
                        daoFields[i].getName());
            } else if (param.equals("values")) {
                String classFull = getFullNameClass(daoFields[i]);
                stringBuffer.append(
                        getValueStruct(Class.forName(classFull), daoFields[i].getValue()));
            }
            if (i + 1 < daoFields.length)
                stringBuffer.append(" ,");
        }
        return stringBuffer.toString();
    }

    public Boolean isAutoIncrement(Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            Id id = (Id) field.getAnnotation(Id.class);
            if (id.autoIcrement())
                return true;
        }
        return false;
    }

    public String getTableName() {
        if (this.getClass().isAnnotationPresent(Entity.class)) {
            String name = ((Entity) this.getClass().getAnnotation(Entity.class)).name();
            if (!name.equals(""))
                return name;
        }
        return this.getClass().getSimpleName();
    }

    public DaoField[] checkValidClass(DaoField[] daoFields) {
        Boolean hasId = false;
        for (int i = 0; i < daoFields.length; i++) {
            if (daoFields[i].isId)
                hasId = true;
        }
        if (!hasId)
            throw new RuntimeException("The Class " + this.getClass().getName() + " does not have an id property");
        return daoFields;
    }

    @SuppressWarnings("unchecked")
    public <T> T getType(Class<T> classTarget, Object value) {
        try {
            Constructor<?> constructor = classTarget.getConstructor(String.class);
            Number num = (Number) constructor.newInstance(value + "");
            return (T) num;
        } catch (Exception e) {
            T valueCasted = classTarget.cast(value);
            return valueCasted;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueStruct(Class<T> classTarget, Object value) {
        try {
            Constructor<?> constructor = classTarget.getConstructor(String.class);
            if (value != null) {
                Number num = (Number) constructor.newInstance(value.toString());
                return (T) num;
            }
            return (T) value;
        } catch (Exception e) {
            T valueCasted = classTarget.cast(value);
            if (valueCasted instanceof Boolean)
                return valueCasted;
            return (T) ("'" + valueCasted + "'");
        }
    }

    public String getFullNameClass(DaoField field) {
        String classFull = field.getNameType();
        if (field.isFk) {
            return field.getValue().getClass().getCanonicalName();
        }
        return classFull;
    }

    @SuppressWarnings("unchecked")
    public <T> T makeMe(ResultSet resultSet, List<String> columns, Connection connection) throws Exception {
        List<Object> valuesByColumn = new Vector<Object>();
        for (int i = 0; i < columns.size(); i++) {
            valuesByColumn.add(resultSet.getObject(i + 1));
        }

        Relation me = (Relation) getMyInstance();
        DaoField[] fields = me.getDAOFields();

        for (int i = 0; i < columns.size(); i++) {

            for (DaoField daoField : fields) {

                if (columns.get(i).equalsIgnoreCase(daoField.getName())) {
                    if (!daoField.isFk) {
                        daoField.getField().set(me,
                                getType(Class.forName(daoField.getNameType()), valuesByColumn.get(i)));
                    } else {
                        Relation fkInstance = (Relation) getInstanceFrom(
                                daoField.getField().getType().getCanonicalName());
                        T instance = fkInstance.selectById(connection, valuesByColumn.get(i));
                        daoField.getField().set(me, instance);
                    }
                }
            }
        }

        return (T) me;
    }

    @SuppressWarnings("unchecked")
    public <T> T getMyInstance() throws Exception {
        Constructor<?> constructor = this.getClass().getConstructor();
        return (T) constructor.newInstance();
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstanceFrom(String className) throws Exception {
        Constructor<?> constructor = Class.forName(className).getConstructor();
        return (T) constructor.newInstance();
    }

}
