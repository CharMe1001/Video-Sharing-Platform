package Entities.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Short extends UserPost {
    private String source;
    private double length;

    public Short() {
        super();
        this.source = "";
        this.length = 0.0;
    }

    public Short(Integer posterID) {
        super(posterID);
        this.source = "";
        this.length = 0.0;
    }

    public Short(Integer posterID, String name, String thumbnail, String source, double length) {
        super(posterID, name, thumbnail);
        this.source = source;
        this.length = length;
    }

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input short file: ");
        this.source = sc.next();

        System.out.print("Input short length: ");
        this.length = sc.nextDouble();
        sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Short file: " + this.source + "\n");
        ret += ("Short length: " + this.length + "\n");

        return ret;
    }

    public String getColumns() {
        return super.getColumns() + ", source, length";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + ", '" + this.source + "', '" + this.length + "')";
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", source = '" + this.source + "', length = " + this.length;
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        this.source = src.getString("source");
        this.length = src.getDouble("length");
    }
}
