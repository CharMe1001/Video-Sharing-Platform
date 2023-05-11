package Entities.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Video extends UserPost {
    private String source;
    private double length;

    public Video() {
        super();
        this.source = "";
        this.length = 0.0;
    }
    
    public Video(Integer posterID) {
        super(posterID);
        this.source = "";
        this.length = 0.0;
    }

    public Video(Integer posterID, String name, String thumbnail, String source, double length) {
        super(posterID, name, thumbnail);
        this.source = source;
        this.length = length;
    }

    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input video file: ");
        this.source = sc.next();

        System.out.print("Input video length: ");
        this.length = sc.nextDouble();
        sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Video file: " + this.source + "\n");
        ret += ("Video length: " + this.length + "\n");

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
