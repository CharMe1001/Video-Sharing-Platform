package services;

import entities.post.*;
import entities.post.Short;
import entities.user.UserComment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    // The currently open post.
    private Post openPost;

    private PostService() {
        this.openPost = null;
    }

    // Opens the post with the given id.
    public void openPost(Integer id) {
        this.openPost = this.get(id);
    }

    // Adds the currently open post to the view history of the given user.
    public void addToHistory(Integer userID) {
        String sqlInsert = "INSERT INTO HISTORY(userID, postID) VALUES(" + userID + ", " + this.getCurrentPostID() + ")";

        try {
            Statement insertStmt = Service.getConnection().createStatement();
            insertStmt.executeUpdate(sqlInsert);
        } catch (SQLException sqlE) {
            System.out.println("Error inserting user access of post into history!");
            System.out.println(sqlE.getMessage());
        }
    }

    // Closes the currently open post.
    public void closePost() {
        this.openPost = null;
    }

    // Returns the currently open post.
    public Post getCurrentPost() {
        return this.openPost;
    }

    // Returns the id of the currently open post.
    public Integer getCurrentPostID() {
        return this.openPost == null ? null : this.openPost.getID();
    }

    // Gets the polls options from the database.
    private void getPollOptions(Poll poll) {
        String getOptions = "SELECT * FROM OPTIONS WHERE pollID = " + poll.getID();
        ResultSet options;

        try {
            Statement stmt = Service.getConnection().createStatement();
            options = stmt.executeQuery(getOptions);

            poll.getOptionsFromResult(options);
        } catch (SQLException sqlE) {
            System.out.println("Error getting options for poll with id = " + poll.getID() + "!");
            System.out.println(sqlE.getMessage());
        }
    }

    // Gets the posts comments from the database.
    private void selectComments(Post post) {
        String getComments = "SELECT * FROM USERCOMMENT WHERE postID = " + post.getID();
        ResultSet comments;

        try {
            Statement stmt = Service.getConnection().createStatement();
            comments = stmt.executeQuery(getComments);

            post.getCommentsFromSelect(comments);
        } catch (Exception e) {
            System.out.println("Error getting comments for post with id = " + post.getID() + "!");
            System.out.println(e.getMessage());
        }

    }

    @Override
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

    @Override
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

    // Inserts the newly read post into the database.
    public int add(Scanner sc, int userID) {
        Post post;

        int type;
        while (true) {
            System.out.print("Input type of post (1 - Ad, 2 - Poll, 3 - Community Post, 4 - Short, 5 - Video): ");
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

                Statement stmt = Service.getConnection().createStatement();
                stmt.executeUpdate(sqlOptions);
            }

            if (post != null) {
                AuditService.getInstance().writeAction("User with id " + userID + " has created a new post with id " + post.getID() + ".");
            }
        } catch (Exception e) {
            System.out.println("Error adding new post to database!");
            System.out.println(e.getMessage());
            return 1;
        }

        return 0;
    }

    // Likes the currently open post.
    public void like(Integer userID) {
        Statement stmt;
        ResultSet resultSet;
        String sqlGet = "SELECT liked, disliked FROM ENGAGEMENT WHERE userID = " + userID + " AND postID = " + this.getCurrentPostID();

        try {
            stmt = Service.getConnection().createStatement();
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
                    AuditService.getInstance().writeAction("User with id " + userID + " has liked post with id " + this.getCurrentPostID() + ".");
                    System.out.println("Successfully liked post with id = " + this.getCurrentPostID() + ".");
                } else {
                    AuditService.getInstance().writeAction("User with id " + userID + " has unliked post with id " + this.getCurrentPostID() + ".");
                    System.out.println("Successfully unliked post with id = " + this.getCurrentPostID() + ".");
                }
            } else {
                String sqlInsert = "INSERT INTO ENGAGEMENT(userID, postID, liked, disliked) VALUES (" + userID + ", " + this.getCurrentPostID() + ", 1, 0)";
                stmt.executeUpdate(sqlInsert);

                AuditService.getInstance().writeAction("User with id " + userID + " has liked post with id " + this.getCurrentPostID() + ".");
                System.out.println("Successfully liked post with id = " + this.getCurrentPostID() + ".");
            }
        } catch (SQLException sqlE) {
            System.out.println("Error updating like/dislike data for user with id = " + userID + " on post with id = " + this.getCurrentPostID() + "!");
            System.out.println(sqlE.getMessage());
        }
    }

    // Dislikes the currently open post.
    public void dislike(Integer userID) {
        String sqlGet = "SELECT liked, disliked FROM ENGAGEMENT WHERE userID = " + userID + " AND postID = " + this.getCurrentPostID();
        Statement stmt;
        ResultSet resultSet;

        try {
            stmt = Service.getConnection().createStatement();
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
                    AuditService.getInstance().writeAction("User with id " + userID + " has disliked post with id " + this.getCurrentPostID() + ".");
                    System.out.println("Successfully disliked post with id = " + this.getCurrentPostID() + ".");
                } else {
                    AuditService.getInstance().writeAction("User with id " + userID + " has undisliked post with id " + this.getCurrentPostID() + ".");
                    System.out.println("Successfully undisliked post with id = " + this.getCurrentPostID() + ".");
                }
            } else {
                String sqlInsert = "INSERT INTO ENGAGEMENT(userID, postID, liked, disliked) VALUES (" + userID + ", " + this.getCurrentPostID() + ", 0, 1)";
                stmt.executeUpdate(sqlInsert);

                AuditService.getInstance().writeAction("User with id " + userID + " has disliked post with id " + this.getCurrentPostID() + ".");
                System.out.println("Successfully disliked post with id = " + this.getCurrentPostID() + ".");
            }
        } catch (SQLException sqlE) {
            System.out.println("Error getting like/dislike data for user with id = " + userID + " on post with id = " + this.getCurrentPostID() + "!");
            System.out.println(sqlE.getMessage());
        }
    }

    // Inserts the given comment into the database.
    private void insertComment(UserComment comment) {
        String sqlInsert = comment.toSQLInsert(comment.getClass().getSimpleName().toUpperCase());
        PreparedStatement commentStmt;

        try {
            commentStmt = Service.getConnection().prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
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

    // Adds the newly read comment to the database.
    public void addComment(Scanner sc, Integer userID) {
        UserComment comment = new UserComment(userID, this.getCurrentPostID());
        comment.read(sc);
        this.getCurrentPost().addComment(comment);

        this.insertComment(comment);
        this.getCurrentPost().putInCommentTree(comment.getID());

        AuditService.getInstance().writeAction("User with id " + userID + " added a comment to post with id " + this.getCurrentPostID() + ".");
        System.out.println("Successfully commented on post with id = " + this.getCurrentPostID() + ".");
    }

    // Adds the newly read reply to the database.
    public void addComment(Scanner sc, Integer userID, Integer parentID) {
        UserComment comment = new UserComment(userID, this.getCurrentPostID(), parentID);
        comment.read(sc);

        if (!this.getCurrentPost().addComment(comment)) {
            System.out.println("Could not reply to comment with id = " + comment.getParentID() + ". Comment does not exist on this post.");
            return;
        }

        this.insertComment(comment);
        this.getCurrentPost().putInCommentTree(comment.getID());

        AuditService.getInstance().writeAction("User with id " + userID + " replied to comment with id " + parentID + " on post with id " + this.getCurrentPostID() + ".");
        System.out.println("Successfully replied to comment with id = " + comment.getParentID() + ".");

    }
}
