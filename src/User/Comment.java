package User;

import java.util.Scanner;

public class Comment {
    private int userID;
    private int postID;
    private String text;
    private Integer parentID;

    public Comment() {
        this.userID = 0;
        this.postID = 0;
        this.text = "";
        this.parentID = null;
    }

    public Comment(int userID, int postID, String text) {
        this.userID = userID;
        this.postID = postID;
        this.text = text;
        this.parentID = null;
    }

    public Comment(int userID, int postID) {
        this.userID = userID;
        this.postID = postID;
    }

    public Comment(int userID, int postID, String text, int parentID) {
        this.userID = userID;
        this.postID = postID;
        this.text = text;
        this.parentID = parentID;
    }

    public Comment(int userID, int postID, int parentID) {
        this.userID = userID;
        this.postID = postID;
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
}
