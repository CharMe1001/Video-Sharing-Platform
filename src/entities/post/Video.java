package entities.post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Video extends UserPost {
    // The name of the file that holds the video.
    private String source;

    // The length of the video.
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

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input video file: ");
        this.source = sc.next();

        System.out.print("Input video length: ");
        this.length = sc.nextDouble();
        sc.nextLine();
    }

    @Override
    public String toString() {
        String ret = super.toString();
        ret += ("Video file: " + this.source + "\n");
        ret += ("Video length: " + this.length + "\n");

        return ret;
    }

    @Override
    public String getColumns() {
        return super.getColumns() + ", source, length";
    }

    @Override
    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + ", '" + this.source + "', '" + this.length + "')";
    }

    @Override
    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", source = '" + this.source + "', length = " + this.length;
    }

    @Override
    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        try {
            this.source = src.getString("source");
        } catch (SQLException sqlE) {
            System.out.println("Error getting source of video with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.length = src.getDouble("length");
        } catch (SQLException sqlE) {
            System.out.println("Error getting length of video with id = " + this.id + "!");
            throw sqlE;
        }
    }
}
