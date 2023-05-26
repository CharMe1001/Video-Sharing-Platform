package entities.post;

import entities.BaseEntity;
import entities.user.UserComment;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class Post extends BaseEntity {
    // The name of a post.
    protected String name;

    // The thumbnail of a post.
    protected String thumbnail;

    // The comment hierarchy of a post.
    private Map<Integer, List<UserComment>> commentTree;

    protected Post() {
        this.name = "";
        this.thumbnail = "";
    }

    protected Post(String name, String thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    // Reads the data of a post.
    public void read(Scanner sc) {
        System.out.print("Input name: ");
        this.name = sc.nextLine();

        System.out.print("Input thumbnail file: ");
        this.thumbnail = sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Name: " + this.name + "\n");
        if (thumbnail != null) {
            ret += ("Thumbnail: " + this.thumbnail + "\n");
        }

        return ret;
    }

    // Returns the name of the post.
    public String getName() {
        return this.name;
    }

    // Sets the name of the post.
    public void setName(String name) {
        this.name = name;
    }

    // Returns the id of the poster if the post is not an ad.
    public abstract Integer getPosterID();

    @Override
    protected String getColumns() {
        return super.getColumns() + "name, thumbnail";
    }

    @Override
    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + "'" + this.name + "', '" + this.thumbnail + "'";
    }

    @Override
    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + "name = '" + this.name + "', thumbnail = '" + this.thumbnail + "'";
    }

    @Override
    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        try {
            this.name = src.getString("name");
        } catch (SQLException sqlE) {
            System.out.println("Error getting name of post with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.thumbnail = src.getString("thumbnail");
        } catch (SQLException sqlE) {
            System.out.println("Error getting thumbnail of post with id = " + this.id + "!");
            throw sqlE;
        }
    }

    // Gets the comment hierarchy from a ResultSet object.
    public void getCommentsFromSelect(ResultSet src) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.commentTree = new HashMap<>();

        while (true) {
            try {
                if (!src.next()) {
                    break;
                }
            } catch (SQLException sqlE) {
                System.out.println("Error retrieving next comment!");
                throw sqlE;
            }

            UserComment comment;

            try {
                comment = (UserComment)BaseEntity.getFromSelect(src);
            } catch (Exception e) {
                System.out.println("Error reading comments for post with id = " + this.id + "!");
                throw e;
            }

            if (!this.commentTree.containsKey(comment.getParentID() == null ? 0 : comment.getParentID())) {
                this.commentTree.put(comment.getParentID() == null ? 0 : comment.getParentID(), new ArrayList<>());
            }

            this.commentTree.get(comment.getParentID() == null ? 0 : comment.getParentID()).add(comment);

            if (!this.commentTree.containsKey(comment.getID())) {
                this.commentTree.put(comment.getID(), new ArrayList<>());
            }
        }
    }

    // Turns the comment tree into a List object for display purposes.
    private void treeToList(List<UserComment> ret, Integer id) {
        if (this.commentTree.containsKey(id == null ? 0 : id)) {
            for (UserComment child: this.commentTree.get(id == null ? 0 : id)) {
                ret.add(child);

                treeToList(ret, child.getID());
            }
        }
    }

    // Returns the comments arranged as a List.
    public List<UserComment> getComments() {
        List<UserComment> comments = new ArrayList<>();

        this.treeToList(comments, null);
        return comments;
    }

    // Puts new comment into the comment hierarchy.
    public void putInCommentTree(Integer k) {
        this.commentTree.put(k, new ArrayList<>());
    }

    // Adds a comment into the hierarchy.
    public boolean addComment(UserComment comment) {
        if (comment.getParentID() == null || this.commentTree.containsKey(comment.getParentID())) {
            if (comment.getParentID() == null) {
                this.commentTree.put(0, new ArrayList<>());
            }

            this.commentTree.get(comment.getParentID() == null ? 0 : comment.getParentID()).add(comment);
            return true;
        }

        return false;
    }

}
