package Services;

import Entities.BaseEntity;
import Entities.Post.Poll;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Service<T extends BaseEntity> {
    protected String getGenericName() {
        String[] name = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName().split("\\.", 0);
        return name[name.length - 1].toUpperCase();
    }

    static public Connection connection;

    public T add(T item) throws SQLException {
        String insertQuery = item.toSQLInsert(this.getGenericName());

        PreparedStatement insertStmt = Service.connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        System.out.println(insertQuery);
        insertStmt.executeUpdate();

        try (ResultSet resID = insertStmt.getGeneratedKeys()) {
            if (resID.next()) {
                String getItemQuery = "SELECT * FROM " + this.getGenericName() + " WHERE id = " + resID.getInt(1);
                Statement getStmt = Service.connection.createStatement();

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
        } catch (SQLException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Integer id) throws SQLException {
        String deleteQuery = "DELETE FROM " + this.getGenericName() + " WHERE id = " + id;
        Statement deleteStmt = Service.connection.createStatement();

        int cntDeleted = deleteStmt.executeUpdate(deleteQuery);
        if (cntDeleted == 0) {
            System.out.println("There were no " + this.getGenericName() + "s with id = " + id + "!");
        } else {
            System.out.println("Successfully deleted " + this.getGenericName() + " with id = " + id + ".");
        }
    }

    public void set(Integer id, T item) throws SQLException {
        String updateQuery = item.getSQLUpdate(this.getGenericName()) + " WHERE id = " + id;
        Statement updateStmt = Service.connection.createStatement();

        int cntUpdated = updateStmt.executeUpdate(updateQuery);
        if (cntUpdated == 0) {
            System.out.println("There were no " + this.getGenericName() + "s with id = " + id + "!");
        } else {
            System.out.println("Successfully updated " + this.getGenericName() + " with id = " + id + ".");
        }

    }

    public T get(Integer id) throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String getQuery = "SELECT * FROM " + this.getGenericName() + " WHERE id = " + id;
        Statement getStmt = Service.connection.createStatement();

        ResultSet res = getStmt.executeQuery(getQuery);

        if (res.next()) {
            return (T)BaseEntity.getFromSelect(res);
        } else {
            throw new SQLException("No " + this.getGenericName() + " found with id = " + id + "!");
        }
    }

    public List<T> getAll() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String getQuery = "SELECT * FROM " + this.getGenericName();
        Statement getStmt = Service.connection.createStatement();

        ResultSet res = getStmt.executeQuery(getQuery);
        List<T> ret = new ArrayList<>();

        while (res.next()) {
            ret.add((T)BaseEntity.getFromSelect(res));
        }

        return ret;
    }

}
