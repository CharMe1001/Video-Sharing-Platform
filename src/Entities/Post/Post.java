package Entities.Post;

import Entities.BaseEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Post extends BaseEntity {
    protected String name;
    protected String thumbnail;

    protected Post() {
        this.name = "";
        this.thumbnail = "";
    }

    protected Post(String name, String thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public void read(Scanner sc) {
        System.out.print("Input name: ");
        this.name = sc.nextLine();

        System.out.print("Input thumbnail file: ");
        this.thumbnail = sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Name: " + this.name + "\n");
        if (thumbnail != null) {
            ret += ("Thumbnail: " + this.thumbnail + "\n");
        }

        return ret;
    }

    public String getName() {
        return this.name;
    }

    protected String getColumns() {
        return super.getColumns() + "name, thumbnail";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + "'" + this.name + "', '" + this.thumbnail + "'";
    }

    public String getSQLUpdate(String className) {
        return super.toSQLInsert(className) + ", name = '" + this.name + "', thumbnail = '" + this.thumbnail + "'";
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);
        this.name = src.getString("name");
        this.thumbnail = src.getString("thumbnail");
    }

}
