package entities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public abstract class BaseEntity {
    // Unique identifier.
    protected Integer id;
    // The time at which the object was created.
    protected Timestamp dateCreated;

    @Override
    public String toString() {
        String ret = "";
        ret += ("Id: " + this.id + "\n");
        ret += ("Date created: " + this.dateCreated.toString() + "\n");

        return ret;
    }

    // Returns the id field.
    public Integer getID() {
        return this.id;
    }

    // Sets the id field.
    public void setID(Integer id) {
        this.id = id;
    }

    // Returns a list of insertable columns in the database table.
    protected String getColumns() { return "objType, "; }

    // Returns the SQL Insert statement for the object.
    public String toSQLInsert(String className) {
        return "INSERT INTO " + className + "(" + this.getColumns() + ") VALUES ('" + this.getClass().getCanonicalName() + "', ";
    }

    // Returns the SQL Update statement for the object.
    public String getSQLUpdate(String className) {
        return "UPDATE " + className + " SET ";
    }

    // Returns an instance of a superclass of BaseEntity given its data in form of a ResultSet object.
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


    // Gets object data from a ResultSet object.
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

}
