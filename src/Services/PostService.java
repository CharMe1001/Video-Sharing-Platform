package Services;

import Entities.BaseEntity;
import Entities.Post.*;
import Entities.Post.Short;
import Entities.User.UserComment;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
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

    private Post openPost;

    private PostService() {
        this.openPost = null;
    }

    public void openPost(Integer id) {
        this.openPost = this.get(id);
    }

    public void closePost() {
        this.openPost = null;
    }

    public Integer getCurrentPostID() {
        return this.openPost == null ? null : this.openPost.getID();
    }

    public Post getCurrentPost() {
        return this.openPost;
    }

    public String getPostName(Integer id) {
        Post post = this.get(id);
        if (post == null) {
            return "Post with id " + id + " doesn't exist.";
        }

        return post.getName();
    }

    private void getPollOptions(Poll poll) {
        String getOptions = "SELECT * FROM OPTIONS WHERE pollID = " + poll.getID();
        ResultSet options;

        try {
            Statement stmt = Service.connection.createStatement();
            options = stmt.executeQuery(getOptions);

            poll.getOptionsFromResult(options);
        } catch (SQLException sqlE) {
            System.out.println("Error getting options for poll with id = " + poll.getID() + "!");
            System.out.println(sqlE.getMessage());
        }
    }

    private void selectComments(Post post) {
        String getComments = "SELECT * FROM USERCOMMENT WHERE postID = " + post.getID();
        ResultSet comments;

        try {
            Statement stmt = Service.connection.createStatement();
            comments = stmt.executeQuery(getComments);

            post.getCommentsFromSelect(comments);
        } catch (Exception e) {
            System.out.println("Error getting comments for post with id = " + post.getID() + "!");
            System.out.println(e.getMessage());
        }

    }

    public Post get(Integer id) {
        Post post = super.get(id);
        if (post == null) {
            return null;
        }

        if (post instanceof Poll) {
            this.getPollOptions((Poll) post);
        }

        this.selectComments(post);
        return post;
    }

    public List<Post> getAll() {
        List<Post> posts = super.getAll();
        for (Post post: posts) {
            if (post instanceof Poll) {
                this.getPollOptions((Poll) post);
            }

            this.selectComments(post);
        }

        return posts;
    }

    public List<Post> getAllFromUser(Integer posterID) {
        String sqlGet = "SELECT * FROM POST WHERE posterID = " + posterID;
        ResultSet res;

        try {
            Statement userStmt = Service.connection.createStatement();
            res = userStmt.executeQuery(sqlGet);
        } catch (SQLException sqlE) {
            System.out.println("Error getting all posts belonging to user with id = " + posterID + "!");
            System.out.println(sqlE.getMessage());
            return null;
        }

        List<Post> posts = new ArrayList<>();

        try {
            while (true) {
                try {
                    if (!res.next()) {
                        break;
                    }
                } catch (SQLException sqlE) {
                    System.out.println("Error retrieving next post belonging to user with id = " + posterID + "!");
                    throw sqlE;
                }

                Post post = (Post)BaseEntity.getFromSelect(res);

                if (post instanceof Poll) {
                    this.getPollOptions((Poll) post);
                }

                this.selectComments(post);

                posts.add(post);
            }
        } catch (Exception e) {
            System.out.println("Error getting list of posts belonging to user with id = " + posterID + "!");
            System.out.println(e.getMessage());
            return null;
        }

        return posts;
    }

    public int add(Scanner sc, int userID) {
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

        try {
            post = super.add(post);

            if (post instanceof Poll) {
                String sqlOptions = ((Poll) post).toSQLInsertOptions();

                Statement stmt = Service.connection.createStatement();
                stmt.executeUpdate(sqlOptions);
            }
        } catch (Exception e) {
            System.out.println("Error adding new post to database!");
            System.out.println(e.getMessage());
            return 1;
        }

        return 0;
    }

    public void like(Integer userID) {
        Statement stmt;
        ResultSet resultSet;
        String sqlGet = "SELECT liked, disliked FROM ENGAGEMENT WHERE userID = " + userID + " AND postID = " + this.getCurrentPostID();

        try {
            stmt = Service.connection.createStatement();
            resultSet = stmt.executeQuery(sqlGet);
        } catch (SQLException sqlE) {
            System.out.println("Error getting like/dislike data for user with id = " + userID + " on post with id = " + this.getCurrentPostID() + "!");
            System.out.println(sqlE.getMessage());
            return;
        }

        try {
            if (resultSet.next()) {
                int liked = resultSet.getInt("liked");
                int disliked = resultSet.getInt("disliked");

                if (disliked != 0) {
                    disliked = 0;
                }

                liked = 1 - liked;

                String sqlUpdate = "UPDATE ENGAGEMENT SET liked = " + liked + ", disliked = " + disliked + " WHERE userID = " + userID + " AND postID = " + this.getCurrentPostID();
                stmt.executeUpdate(sqlUpdate);

                if (liked == 1) {
                    System.out.println("Successfully liked post with id = " + this.getCurrentPostID() + ".");
                } else {
                    System.out.println("Successfully unliked post with id = " + this.getCurrentPostID() + ".");
                }
            } else {
                String sqlInsert = "INSERT INTO ENGAGEMENT(userID, postID, liked, disliked) VALUES (" + userID + ", " + this.getCurrentPostID() + ", 1, 0)";
                stmt.executeUpdate(sqlInsert);

                System.out.println("Successfully liked post with id = " + this.getCurrentPostID() + ".");
            }
        } catch (SQLException sqlE) {
            System.out.println("Error updating like/dislike data for user with id = " + userID + " on post with id = " + this.getCurrentPostID() + "!");
            System.out.println(sqlE.getMessage());
        }
    }

    public void dislike(Integer userID) {
        String sqlGet = "SELECT liked, disliked FROM ENGAGEMENT WHERE userID = " + userID + " AND postID = " + this.getCurrentPostID();
        Statement stmt;
        ResultSet resultSet;

        try {
            stmt = Service.connection.createStatement();
            resultSet = stmt.executeQuery(sqlGet);
        } catch (SQLException sqlE) {
            System.out.println("Error getting like/dislike data for user with id = " + userID + " on post with id = " + this.getCurrentPostID() + "!");
            System.out.println(sqlE.getMessage());
            return;
        }

        try {
            if (resultSet.next()) {
                int liked = resultSet.getInt("liked");
                int disliked = resultSet.getInt("disliked");

                if (liked != 0) {
                    liked = 0;
                }

                disliked = 1 - disliked;

                String sqlUpdate = "UPDATE ENGAGEMENT SET liked = " + liked + ", disliked = " + disliked + " WHERE userID = " + userID + " AND postID = " + this.getCurrentPostID();
                stmt.executeUpdate(sqlUpdate);

                if (disliked == 1) {
                    System.out.println("Successfully disliked post with id = " + this.getCurrentPostID() + ".");
                } else {
                    System.out.println("Successfully undisliked post with id = " + this.getCurrentPostID() + ".");
                }
            } else {
                String sqlInsert = "INSERT INTO ENGAGEMENT(userID, postID, liked, disliked) VALUES (" + userID + ", " + this.getCurrentPostID() + ", 0, 1)";
                stmt.executeUpdate(sqlInsert);

                System.out.println("Successfully disliked post with id = " + this.getCurrentPostID() + ".");
            }
        } catch (SQLException sqlE) {
            System.out.println("Error getting like/dislike data for user with id = " + userID + " on post with id = " + this.getCurrentPostID() + "!");
            System.out.println(sqlE.getMessage());
            return;
        }
    }

    public void addToPlaylist(Integer playlistID) {
        String sqlInsert = "INSERT INTO PLAYLISTCONTENT(postID, playlistID) VALUES(" + this.getCurrentPostID() + ", " + playlistID + ")";

        try {
            Statement insertStmt = Service.connection.createStatement();
            insertStmt.executeUpdate(sqlInsert);
        } catch (SQLException sqlE) {
            System.out.println("Error inserting video with id = " + this.getCurrentPostID() + " to playlist with id = " + playlistID + "!");
            System.out.println(sqlE.getMessage());
            return;
        }

        System.out.println("Successfully added this post to playlist with id = " + playlistID + ".");
    }

    private void insertComment(UserComment comment) {
        String sqlInsert = comment.toSQLInsert(comment.getClass().getSimpleName().toUpperCase());
        PreparedStatement commentStmt;

        try {
            commentStmt = Service.connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            commentStmt.executeUpdate();
        } catch (SQLException sqlE) {
            System.out.println("Error adding comment to database!");
            System.out.println(sqlE.getMessage());
            return;
        }

        try (ResultSet resID = commentStmt.getGeneratedKeys()) {
            if (resID.next()) {
                comment.setID(resID.getInt(1));
            } else {
                throw new SQLException("Error inserting new comment!");
            }
        } catch (SQLException sqlE) {
            System.out.println("Error getting id of newly inserted comment!");
        }

    }

    public void addComment(Scanner sc, Integer userID) {
        UserComment comment = new UserComment(userID, this.getCurrentPostID());
        comment.read(sc);
        this.getCurrentPost().addComment(comment);

        this.insertComment(comment);

        System.out.println("Successfully replied to comment with id = " + comment.getParentID() + ".");
    }

    public void addComment(Scanner sc, Integer userID, Integer parentID) {
        UserComment comment = new UserComment(userID, this.getCurrentPostID(), parentID);
        comment.read(sc);

        if (!this.getCurrentPost().addComment(comment)) {
            System.out.println("Could not reply to comment with id = " + comment.getParentID() + ". Comment does not exist on this post.");
            return;
        }

        this.insertComment(comment);

        System.out.println("Successfully replied to comment with id = " + comment.getParentID() + ".");

    }
}
