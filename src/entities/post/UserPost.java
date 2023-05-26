package entities.post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class UserPost extends Post {
    // The id of the user who posted this post.
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

    @Override
    public void read(Scanner sc) {
        super.read(sc);
    }

    @Override
    public String toString() {
        String ret = super.toString();
        ret += ("Poster id: " + this.posterID + "\n");

        return ret;
    }

    @Override
    public Integer getPosterID() {
        return this.posterID;
    }

    @Override
    public String getColumns() {
        return super.getColumns() + ", posterID";
    }

    @Override
    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + ", " + this.posterID;
    }

    @Override
    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", posterID = " + this.posterID;
    }

    @Override
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
