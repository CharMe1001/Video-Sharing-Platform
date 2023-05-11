package Entities.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Ad extends Post {
    private String companyName;
    private String link;
    private String text;

    public Ad() {
        super();
        this.companyName = "";
        this.link = "";
        this.text = "";
    }

    public Ad(String name, String thumbnail, String companyName, String link, String text) {
        super(name, thumbnail);
        this.companyName = companyName;
        this.link = link;
        this.text = text;
    }

    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input company name: ");
        this.companyName = sc.nextLine();

        System.out.print("Input the link of the ad: ");
        this.link = sc.nextLine();

        System.out.print("Input the ad text: ");
        this.text = sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Company: " + this.companyName + "\n");
        ret += ("Link to ad: " + this.link + "\n");
        ret += ("Content: " + this.text + "\n");

        return ret;
    }

    public String getColumns() {
        return super.getColumns() + ", companyName, link, text";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + ", '" + this.companyName + "', '" + this.link + "', '" + this.text + "')";
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", companyName = '" + this.companyName + "', link = '" + this.link + "', text = '" + this.text + "'";
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        this.companyName = src.getString("companyName");
        this.link = src.getString("link");
        this.text = src.getString("text");
    }
}
