package Services;

import Entities.BaseEntity;
import Entities.Post.*;
import Entities.Post.Short;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PostService extends Service<Post> {
    private static PostService instance = null;
    public static PostService getInstance() {
        if (PostService.instance == null) {
            PostService.instance = new PostService();
        }

        return PostService.instance;
    }

    private Integer currentPost;

    private PostService() {
        this.currentPost = null;
    }

    public void setCurrentPostID(Integer id) {
        this.currentPost = id;
    }

    public Integer getCurrentPostID() {
        return this.currentPost;
    }

    private Post getCurrentPost() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (this.currentPost == null) {
            return null;
        }

        return this.get(this.currentPost);
    }

    public String getPostName(Integer id) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Post post = this.get(id);
        if (post == null) {
            return "Post with id " + id + " doesn't exist.";
        }

        return post.getName();
    }

    private void getPollOptions(Poll poll) throws SQLException {
        String getOptions = "SELECT * FROM OPTIONS WHERE pollID = " + poll.getID();
        Statement stmt = Service.connection.createStatement();
        ResultSet options = stmt.executeQuery(getOptions);

        poll.getOptionsFromResult(options);
    }

    public Post get(Integer id) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Post post = super.get(id);

        if (post instanceof Poll) {
            this.getPollOptions((Poll) post);
        }

        return post;
    }

    public List<Post> getAll() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Post> posts = super.getAll();
        for (Post post: posts) {
            if (post instanceof Poll) {
                this.getPollOptions((Poll) post);
            }
        }

        return posts;
    }

    public List<Post> getAllFromUser(Integer posterID) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String sqlGet = "SELECT * FROM POST WHERE posterID = " + posterID;
        Statement userStmt = Service.connection.createStatement();
        ResultSet res = userStmt.executeQuery(sqlGet);

        List<Post> posts = new ArrayList<>();

        while (res.next()) {
            Post post = (Post)BaseEntity.getFromSelect(res);

            if (post instanceof Poll) {
                this.getPollOptions((Poll) post);
            }

            posts.add(post);
        }

        return posts;
    }

    public int read(Scanner sc, int userID) throws SQLException {
        Post post;

        int type;
        while (true) {
            System.out.print("Input type of post (1 - Ad, 2 - Poll, 3 - Post, 4 - Short, 5 - Video): ");
            type = sc.nextInt();
            sc.nextLine();

            if (type < 1 || type > 5) {
                System.out.println("Wrong type. Try again.");
                continue;
            }

            break;
        }

        switch (type) {
            case 1 -> {
                post = new Ad();
            }
            case 2 -> {
                post = new Poll(userID);
            }
            case 3 -> {
                post = new CommunityPost(userID);
            }
            case 4 -> {
                post = new Short(userID);
            }
            default -> {
                post = new Video(userID);
            }
        }

        post.read(sc);

        post = this.add(post);

        if (post instanceof Poll) {
            String sqlOptions = ((Poll) post).toSQLInsertOptions();
            System.out.println(sqlOptions);
            Statement stmt = Service.connection.createStatement();
            stmt.executeUpdate(sqlOptions);
        }

        return 0;
    }
}
