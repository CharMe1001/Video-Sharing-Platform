package entities.post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Ad extends Post {
    // The name of the company that is advertised.
    private String companyName;

    // The link leading to the companies public page.
    private String link;

    // The descriptive text.
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

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input company name: ");
        this.companyName = sc.nextLine();

        System.out.print("Input the link of the ad: ");
        this.link = sc.nextLine();

        System.out.print("Input the ad text: ");
        this.text = sc.nextLine();
    }

    @Override
    public String toString() {
        String ret = super.toString();
        ret += ("Company: " + this.companyName + "\n");
        ret += ("Link to ad: " + this.link + "\n");
        ret += ("Content: " + this.text + "\n");

        return ret;
    }

    @Override
    public Integer getPosterID() { return null; }

    @Override
    public String getColumns() {
        return super.getColumns() + ", companyName, link, text";
    }

    @Override
    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + ", '" + this.companyName + "', '" + this.link + "', '" + this.text + "')";
    }

    @Override
    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", companyName = '" + this.companyName + "', link = '" + this.link + "', text = '" + this.text + "'";
    }

    @Override
    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        try {
            this.companyName = src.getString("companyName");
        } catch (SQLException sqlE) {
            System.out.println("Error getting the company name of ad with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.link = src.getString("link");
        } catch (SQLException sqlE) {
            System.out.println("Error getting link of ad with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.text = src.getString("text");
        } catch (SQLException sqlE) {
            System.out.println("Error getting text of ad with id = " + this.id + "!");
            throw sqlE;
        }
    }
}
