package entities.Post;

import entities.BaseEntity;
import entities.User.UserComment;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class Post extends BaseEntity {
    protected String name;
    protected String thumbnail;

    private Map<Integer, List<UserComment>> commentTree;

    protected Post() {
        this.name = "";
        this.thumbnail = "";
    }

    protected Post(String name, String thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

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

    public String getName() {
        return this.name;
    }

    public abstract Integer getPosterID();

    protected String getColumns() {
        return super.getColumns() + "name, thumbnail";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + "'" + this.name + "', '" + this.thumbnail + "'";
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + "name = '" + this.name + "', thumbnail = '" + this.thumbnail + "'";
    }

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

    private void treeToList(List<UserComment> ret, Integer id) {
        if (this.commentTree.containsKey(id == null ? 0 : id)) {
            for (UserComment child: this.commentTree.get(id == null ? 0 : id)) {
                ret.add(child);

                treeToList(ret, child.getID());
            }
        }
    }

    public List<UserComment> getComments() {
        List<UserComment> comments = new ArrayList<>();

        this.treeToList(comments, null);
        return comments;
    }

    public void putInCommentTree(Integer k) {
        this.commentTree.put(k, new ArrayList<>());
    }

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

    public void setName(String name) {
        this.name = name;
    }

}
