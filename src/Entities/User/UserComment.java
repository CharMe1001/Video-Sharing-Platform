package Entities.User;

import Entities.BaseEntity;

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

    public UserComment(Integer userID, Integer postID, String text) {
        this.userID = userID;
        this.postID = postID;
        this.text = text;
        this.parentID = null;
    }

    public UserComment(Integer userID, Integer postID) {
        this.userID = userID;
        this.postID = postID;
    }

    public UserComment(Integer userID, Integer postID, String text, Integer parentID) {
        this.userID = userID;
        this.postID = postID;
        this.text = text;
        this.parentID = parentID;
    }

    public void read(Scanner sc) {
        System.out.print("Input comment: ");
        this.text = sc.nextLine();
    }

    public String toString() {
        String ret = "";
        ret += ("User ID: " + this.userID + "\n");
        ret += ("Post ID: " + this.postID + "\n");
        ret += ("Content: " + this.text + "\n");
        if (this.parentID != null) {
            ret += ("This is a response to the comment with id: " + this.parentID + "\n");
        }

        return ret;
    }

    public String getColumns() {
        return super.getColumns() + "userID, postID, text, parentID";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + "'" + this.userID + "', '" + this.postID + "', '" + this.text + "', " + (this.parentID == null ? "null" : "'" + this.parentID + "'") + ")";
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", userID = '" + this.userID + "', postID = '" + this.postID + "', text = '" + this.text + ", parentID = " + (this.parentID == null ? "null" : "'" + this.parentID + "'");
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        this.userID = src.getInt("userID");
        this.postID = src.getInt("postID");
        this.text = src.getString("text");
        this.parentID = src.getInt("parentID");
    }
}
