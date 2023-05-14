package Entities.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class UserPost extends Post {
    protected Integer posterID;

    public UserPost() {
        super();
        this.posterID = null;
    }

    public UserPost(Integer posterID) {
        super();
        this.posterID = posterID;
    }

    public UserPost(Integer posterID, String name, String thumbnail) {
        super(name, thumbnail);
        this.posterID = posterID;
    }

    public void read(Scanner sc) {
        super.read(sc);
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Poster id: " + this.posterID + "\n");

        return ret;
    }

    public Integer getPosterID() {
        return this.posterID;
    }

    public String getColumns() {
        return super.getColumns() + ", posterID";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + ", " + this.posterID;
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", posterID = " + this.posterID;
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        try {
            this.posterID = src.getInt("posterID");
        } catch (SQLException sqlE) {
            System.out.println("Error getting id of poster of post with id = " + this.id + "!");
            throw sqlE;
        }
    }
}
