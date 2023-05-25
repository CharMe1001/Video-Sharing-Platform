package entities.User;

import entities.BaseEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserComment extends BaseEntity {
    private Integer userID;
    private Integer postID;
    private String text;
    private Integer parentID;

    public UserComment() {
        this.userID = null;
        this.postID = null;
        this.text = "";
        this.parentID = null;
    }

    public UserComment(Integer userID, Integer postID, String text, Integer parentID) {
        this.userID = userID;
        this.postID = postID;
        this.text = text;
        this.parentID = parentID;
    }

    public UserComment(Integer userID, Integer postID, String text) {
        this.userID = userID;
        this.postID = postID;
        this.text = text;
        this.parentID = null;
    }

    public UserComment(Integer userID, Integer postID) {
        this.userID = userID;
        this.postID = postID;
        this.text = "";
        this.parentID = null;
    }

    public UserComment(Integer userID, Integer postID, Integer parentID) {
        this.userID = userID;
        this.postID = postID;
        this.text = "";
        this.parentID = parentID;
    }

    public void read(Scanner sc) {
        System.out.print("Input comment: ");
        this.text = sc.nextLine();
    }

    public String toString() {
        String ret = "";
        ret += ("Comment ID: " + this.id + "\n");
        ret += ("User ID: " + this.userID + "\n");
        ret += ("Post ID: " + this.postID + "\n");
        ret += ("Content: " + this.text + "\n");
        if (this.parentID != null) {
            ret += ("This is a response to the comment with id: " + this.parentID + "\n");
        }

        return ret;
    }

    public Integer getParentID() {
        return this.parentID;
    }

    public String getColumns() {
        return super.getColumns() + "userID, postID, text, parentID";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + this.userID + ", " + this.postID + ", '" + this.text + "', " + (this.parentID == null ? "null" : this.parentID) + ")";
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + "userID = " + this.userID + ", postID = " + this.postID + ", text = '" + this.text + "', parentID = " + (this.parentID == null ? "null" : this.parentID);
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        try {
            this.userID = src.getInt("userID");
        } catch (SQLException sqlE) {
            System.out.println("Error getting id of user for comment with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.postID = src.getInt("postID");
        } catch (SQLException sqlE) {
            System.out.println("Error getting id of post for comment with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.text = src.getString("text");
        } catch (SQLException sqlE) {
            System.out.println("Error getting text of comment with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.parentID = src.getInt("parentID");

            if (this.parentID == 0) {
                this.parentID = null;
            }
        } catch (SQLException sqlE) {
            System.out.println("Error getting id of parent for comment with id = " + this.id + "!");
            throw sqlE;
        }
    }
}
