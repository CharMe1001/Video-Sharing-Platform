package services;

import entities.BaseEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Service<T extends BaseEntity> {
    // Returns the generic name of the T class.
    protected String getGenericName() {
        String[] name = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName().split("\\.", 0);
        return name[name.length - 1].toUpperCase();
    }

    // The connection to the MSSQLServer database.
    static private Connection connection;

    // Returns the database connection.
    static public Connection getConnection() {
        return Service.connection;
    }

    // Sets up the database connection.
    static public void setupConnection(String database) throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        Service.connection = DriverManager.getConnection("jdbc:sqlserver://localhost;database=" + database + ";encrypt=false;integratedSecurity=true");
    }

    // Inserts the given item into the database.
    public T add(T item) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String insertQuery = item.toSQLInsert(this.getGenericName());
        PreparedStatement insertStmt;

        try {
            insertStmt = Service.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            insertStmt.executeUpdate();
        } catch (SQLException sqlE) {
            System.out.println("Error adding new " + this.getGenericName() + "!");
            System.out.println("Insert statement: " + insertQuery);
            System.out.println(this.getGenericName() + ":");
            System.out.println(item);

            throw sqlE;
        }

        try (ResultSet resID = insertStmt.getGeneratedKeys()) {
            if (resID.next()) {
                String getItemQuery = "SELECT * FROM " + this.getGenericName() + " WHERE id = " + resID.getInt(1);
                Statement getStmt = Service.getConnection().createStatement();

                ResultSet resItem = getStmt.executeQuery(getItemQuery);
                if (resItem.next()) {
                    item.setID(BaseEntity.getFromSelect(resItem).getID());
                    System.out.println("Created new " + this.getGenericName() + " with id = " + item.getID());
                    return item;
                } else {
                    throw new SQLException("No " + this.getGenericName() + " object found after creating it.\n" + getItemQuery);
                }
            } else {
                throw new SQLException("Error inserting new " + this.getGenericName() + ".\n" + insertStmt);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving new inserted " + this.getGenericName() + " from database!");
            throw e;
        }
    }

    // Removes the item with the given id from the database.
    public void remove(Integer id) {
        String deleteQuery = "DELETE FROM " + this.getGenericName() + " WHERE id = " + id;
        int cntDeleted;

        try {
            Statement deleteStmt = Service.getConnection().createStatement();
            cntDeleted = deleteStmt.executeUpdate(deleteQuery);
        } catch (SQLException sqlE) {
            System.out.println("Error removing " + this.getGenericName() + " from the database!");
            System.out.println("Delete statement: " + deleteQuery);

            System.out.println(sqlE.getMessage());
            return;
        }

        if (cntDeleted == 0) {
            System.out.println("There were no " + this.getGenericName() + "s with id = " + id + "!");
        } else {
            System.out.println("Successfully deleted " + this.getGenericName() + " with id = " + id + ".");
        }
    }

    // Updates the data of the given item into the database.
    public boolean set(T item) {
        String updateQuery = item.getSQLUpdate(this.getGenericName()) + " WHERE id = " + item.getID();
        int cntUpdated;

        try {
            Statement updateStmt = Service.getConnection().createStatement();
            cntUpdated = updateStmt.executeUpdate(updateQuery);
        }  catch (SQLException sqlE) {
            System.out.println("Error updating " + this.getGenericName() + " in database!");
            System.out.println("Update statement: " + updateQuery);
            System.out.println(this.getGenericName() + ":");
            System.out.println(item);

            System.out.println(sqlE.getMessage());
            return false;
        }

        if (cntUpdated == 0) {
            System.out.println("There were no " + this.getGenericName() + "s with id = " + item.getID() + "!");
        } else {
            System.out.println("Successfully updated " + this.getGenericName() + " with id = " + item.getID() + ".");
        }

        return true;
    }

    // Gets the data of the item with the given id from the database.
    public T get(Integer id) {
        String getQuery = "SELECT * FROM " + this.getGenericName() + " WHERE id = " + id;
        ResultSet res;

        try {
            Statement getStmt = Service.getConnection().createStatement();
            res = getStmt.executeQuery(getQuery);
        } catch (SQLException sqlE) {
            System.out.println("Error retrieving " + this.getGenericName() + " with id = " + id + "!");
            System.out.println("Select statement: " + getQuery);
            System.out.println(sqlE.getMessage());
            return null;
        }

        try {
            if (res.next()) {
                return (T)BaseEntity.getFromSelect(res);
            } else {
                throw new SQLException("No " + this.getGenericName() + " found with id = " + id + "!");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving " + this.getGenericName() + " with id = " + id + "!");
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Gets all items from the database.
    public List<T> getAll() {
        String getQuery = "SELECT * FROM " + this.getGenericName();
        ResultSet res;

        try {
            Statement getStmt = Service.getConnection().createStatement();
            res = getStmt.executeQuery(getQuery);
        } catch (SQLException sqlE) {
            System.out.println("Error retrieving all objects of type " + this.getGenericName() + "!");
            System.out.println(sqlE.getMessage());
            return null;
        }

        List<T> ret = new ArrayList<>();

        while (true) {
            try {
                if (!res.next()) {
                    break;
                }
            } catch (SQLException sqlE) {
                System.out.println("Error retrieving next " + this.getGenericName() + "!");
                System.out.println(sqlE.getMessage());
            }

            try {
                ret.add((T)BaseEntity.getFromSelect(res));
            } catch (Exception e) {
                System.out.println("Error reading " + this.getGenericName() + " from database!");
                System.out.println(e.getMessage());
            }
        }

        return ret;
    }

}
