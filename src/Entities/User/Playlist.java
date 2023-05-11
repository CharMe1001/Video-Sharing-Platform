package Entities.User;

import Entities.BaseEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Playlist extends BaseEntity {
    private String name;
    private Integer ownerID;
    private enum Order{NORMAL, SHUFFLE};
    private Order parseOrder;

    public Playlist() {
        this.name = "";
        this.ownerID = null;
        this.parseOrder = Order.NORMAL;
    }

    public Playlist(String name, Integer ownerID) {
        this.name = name;
        this.ownerID = ownerID;
        this.parseOrder = Order.NORMAL;
    }

    public void read(Scanner sc) {
        System.out.print("Input playlist name: ");
        this.name = sc.nextLine();
    }

    public String toString() {
        String ret = "Playlist + \"" + this.name + "\":\n";
        return ret;
    }

    public String getColumns() {
        return super.getColumns() + "name, ownerID, parseOrder";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + "'" + this.name + "', " + this.ownerID + ", '" + this.parseOrder.name() + "')";
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", name = '" + this.name + "', ownerID = " + this.ownerID + ", parseOrder = '" + this.parseOrder.name() + "'";
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        this.name = src.getString("name");
        this.ownerID = src.getInt("ownerID");
        this.parseOrder = Order.valueOf(src.getString("parseOrder"));
    }
}
