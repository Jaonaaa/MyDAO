package com.geta.dao.entity;

import java.lang.reflect.Field;

import com.geta.dao.annotation.Column;
import com.geta.dao.annotation.Fk;
import com.geta.dao.annotation.Id;
import com.geta.dao.annotation.IgnoreField;

public class DaoField {

    String name;

    String type;

    Object value;

    Boolean isId;

    Class<?> foreignClass;

    Boolean isFk;

    Object origin;

    Field field;

    String nameType;

    public DaoField(Field field, Object origin) {
        this.origin = origin;
        acces(field);
        setName(field);
        setType(field);
        setValue(field);
        setIsId(field);
        setForeignClass(field);
        setIsFk(field);
        setField(field);
    }

    public void acces(Field field) {
        try {
            field.setAccessible(true);
        } catch (Exception e) {
        }
    }

    public static DaoField[] toDaoFields(Field[] field, Object origin) {
        int count = 0;
        for (int i = 0; i < field.length; i++) {
            if (!field[i].isAnnotationPresent(IgnoreField.class))
                count++;
        }
        DaoField[] daoFields = new DaoField[count];
        for (int i = 0; i < field.length; i++) {
            if (!field[i].isAnnotationPresent(IgnoreField.class))
                daoFields[i] = new DaoField(field[i], origin);
        }
        return daoFields;
    }

    public String getName() {
        return name;
    }

    public void setName(Field field) {
        if (field.isAnnotationPresent(Fk.class)) {
            Fk fk = (Fk) field.getAnnotation(Fk.class);
            this.name = fk.name().equals(null) ? field.getName() : fk.name();
        } else if (field.isAnnotationPresent(Column.class)) {
            Column column = (Column) field.getAnnotation(Column.class);
            this.name = column.name().equals(null) ? field.getName() : column.name();
        } else
            this.name = field.getName();

    }

    public Object getValue() {

        if (this.foreignClass != null) {
            if (this.value != null) {
                if (!this.value.getClass().getPackageName().contains("java.lang")) {
                    DaoField[] daoFields = toDaoFields(this.value.getClass().getDeclaredFields(), this.value);
                    DaoField pkField = getPkField(daoFields);
                    return pkField.getValue();
                }
            }
        }
        return value;
    }

    public DaoField getPkField(DaoField[] daoFields) {
        for (int i = 0; i < daoFields.length; i++) {
            if (daoFields[i] != null)
                if (daoFields[i].isId)
                    return daoFields[i];
        }
        return null;
    }

    public void setValue(Field field) {
        try {
            this.value = field.get(this.origin);
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        }
    }

    public String getNameType() {
        return nameType;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }

    public Boolean getIsId() {
        return isId;
    }

    public void setIsId(Field field) {
        if (field.isAnnotationPresent(Id.class)) {
            this.isId = true;
        } else
            this.isId = false;
    }

    public String getType() {
        return type;
    }

    public void setType(Field field) {
        this.type = field.getType().getSimpleName();
    }

    public Class<?> getForeignClass() {
        return foreignClass;
    }

    public void setForeignClass(Field field) {
        this.nameType = field.getType().getName();
        if (field.isAnnotationPresent(Fk.class)) {
            this.foreignClass = this.origin.getClass();
        } else
            try {
                if (!this.value.getClass().getPackageName().contains("java.lang")) {
                    this.foreignClass = Class.forName(field.getType().getName());
                }

            } catch (Exception e) {
            }
    }

    public Boolean getIsFk() {
        return isFk;
    }

    public void setIsFk(Field field) {
        if (field.isAnnotationPresent(Fk.class)) {
            this.isFk = true;
        } else
            this.isFk = false;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

}