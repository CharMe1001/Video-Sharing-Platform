package Entities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public abstract class BaseEntity {
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
        BaseEntity item;

        try {
            Class<?> clazz = Class.forName(src.getString("objType"));
            Constructor<?> ctor = clazz.getConstructor();
            item = (BaseEntity) ctor.newInstance();
        } catch (Exception e) {
            System.out.println("Error getting class instance.");
            throw e;
        }

        try {
            item.getDataFromSelect(src);
        } catch (SQLException sqlE) {
            System.out.println("Error getting data from select.");
            throw sqlE;
        }
        return item;
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        try {
            this.id = src.getInt("id");
        } catch (SQLException sqlE) {
            System.out.println("Error getting id field!");
            throw sqlE;
        }

        try {
            this.dateCreated = src.getTimestamp("dateCreated");
        } catch (SQLException sqlE) {
            System.out.println("Error getting dateCreated field!");
            throw sqlE;
        }
    }

    public Integer getID() {
        return this.id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

}
