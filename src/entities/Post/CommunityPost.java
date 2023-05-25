package entities.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CommunityPost extends UserPost {
    private String text;

    public CommunityPost() {
        super();
        this.text = "";
    }

    public CommunityPost(Integer posterID) {
        super(posterID);
        this.text = "";
    }

    public CommunityPost(Integer posterID, String name, String thumbnail, String text) {
        super(posterID, name, thumbnail);
        this.text = text;
    }

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input community post text: ");
        this.text = sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Content: " + this.text + "\n");

        return ret;
    }

    public String getColumns() {
        return super.getColumns() + ", text";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + ", '" + this.text + "')";
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", text = '" + this.text + "'";
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        try {
            this.text = src.getString("text");
        } catch (SQLException sqlE) {
            System.out.println("Error getting text of community post with id = " + this.id + "!");
            throw sqlE;
        }
    }
}
