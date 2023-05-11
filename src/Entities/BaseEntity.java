package Entities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class BaseEntity {
    protected Integer id;
    protected Timestamp dateCreated;

    public String toString() {
        String ret = "";
        ret += ("Id: " + this.id + "\n");
        ret += ("Date created: " + this.dateCreated.toString() + "\n");

        return ret;
    }

    protected String getColumns() { return "objType, "; }

    public String toSQLInsert(String className) {
        return "INSERT INTO " + className + "(" + this.getColumns() + ") VALUES ('" + this.getClass().getCanonicalName() + "', ";
    }

    public String getSQLUpdate(String className) {
        return "UPDATE " + className + " SET id = " + this.id;
    }

    public static BaseEntity getFromSelect(ResultSet src) throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(src.getString("objType"));
        Constructor<?> ctor = clazz.getConstructor();
        BaseEntity item = (BaseEntity) ctor.newInstance();

        item.getDataFromSelect(src);
        return item;
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        this.id = src.getInt("id");
        this.dateCreated = src.getTimestamp("dateCreated");
    }

    public Integer getID() {
        return this.id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

}
