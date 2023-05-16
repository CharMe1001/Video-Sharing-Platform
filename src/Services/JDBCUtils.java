package Services;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
    public static Connection connection;

    public static void CreateTables() throws SQLException {
        Statement stmt = JDBCUtils.connection.createStatement();

        String sqlCreateUser = "CREATE TABLE PERSON(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "dateCreated DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "objType VARCHAR(32) NOT NULL," +
                "name VARCHAR(32) NOT NULL," +
                "password VARCHAR(32) NOT NULL," +
                "email VARCHAR(32) NOT NULL" +
                ")";
        stmt.executeUpdate(sqlCreateUser);

        String sqlCreatePost = "CREATE TABLE POST(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "dateCreated DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "objType VARCHAR(32) NOT NULL," +
                "name VARCHAR(32)," +
                "thumbnail VARCHAR(64)," +
                "companyName VARCHAR(32)," +
                "link VARCHAR(64)," +
                "text VARCHAR(200)," +
                "posterID INT," +
                "source VARCHAR(32)," +
                "length DECIMAL," +
                "FOREIGN KEY(posterID) REFERENCES PERSON(id)" +
                ")";
        stmt.executeUpdate(sqlCreatePost);

        String sqlCreateOptions = "CREATE TABLE OPTIONS(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "text VARCHAR(32) NOT NULL," +
                "pollID INT NOT NULL," +
                "FOREIGN KEY(pollID) REFERENCES POST(id)" +
                ")";
        stmt.executeUpdate(sqlCreateOptions);

        String sqlCreatePlaylist = "CREATE TABLE PLAYLIST(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "dateCreated DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "objType VARCHAR(32) NOT NULL," +
                "name VARCHAR(32) NOT NULL," +
                "ownerID INT NOT NULL," +
                "parseOrder VARCHAR(32) NOT NULL," +
                "FOREIGN KEY(ownerID) REFERENCES PERSON(id)," +
                "CONSTRAINT TYPE_ENUM CHECK(parseOrder IN ('NORMAL', 'SHUFFLE'))" +
                ")";
        stmt.executeUpdate(sqlCreatePlaylist);

        String sqlCreateComment = "CREATE TABLE USERCOMMENT(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "dateCreated DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "objType VARCHAR(32) NOT NULL," +
                "userID INT NOT NULL," +
                "postID INT NOT NULL," +
                "text VARCHAR(200) NOT NULL," +
                "parentID INT," +
                "FOREIGN KEY(userID) REFERENCES PERSON(id)," +
                "FOREIGN KEY(postID) REFERENCES  POST(id)," +
                "FOREIGN KEY(parentID) REFERENCES USERCOMMENT(id)" +
                ")";
        stmt.executeUpdate(sqlCreateComment);

        String sqlCreateSubscribedTo = "CREATE TABLE SUBSCRIBEDTO(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "dateCreated DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "subscriberID INT NOT NULL," +
                "subscribedID INT NOT NULL," +
                "FOREIGN KEY(subscriberID) REFERENCES PERSON(id)," +
                "FOREIGN KEY(subscribedID) REFERENCES PERSON(id)," +
                "CONSTRAINT NOT_SELF CHECK(subscribedID <> subscriberID)" +
                ")";
        stmt.executeUpdate(sqlCreateSubscribedTo);

        String sqlCreateEngagement = "CREATE TABLE ENGAGEMENT(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "userID INT NOT NULL," +
                "postID INT NOT NULL," +
                "liked BIT NOT NULL," +
                "disliked BIT NOT NULL," +
                "FOREIGN KEY(userID) REFERENCES PERSON(id)," +
                "FOREIGN KEY(postID) REFERENCES POST(id)" +
                ")";
        stmt.executeUpdate(sqlCreateEngagement);

        String sqlCreatePlaylistContent = "CREATE TABLE PLAYLISTCONTENT(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "postID INT NOT NULL," +
                "playlistID INT NOT NULL," +
                "FOREIGN KEY(postID) REFERENCES POST(id)," +
                "FOREIGN KEY(playlistID) REFERENCES PLAYLIST(id)" +
                ")";
        stmt.executeUpdate(sqlCreatePlaylistContent);

        String sqlCreateHistory = "CREATE TABLE HISTORY(" +
                "history_id INT IDENTITY(1, 1) PRIMARY KEY," +
                "dateAccessed DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "userID INT NOT NULL," +
                "postID INT NOT NULL," +
                "FOREIGN KEY(userID) REFERENCES PERSON(id)," +
                "FOREIGN KEY(postID) REFERENCES POST(id)" +
                ")";
        stmt.executeUpdate(sqlCreateHistory);

        String sqlCreateCommentTrigger = "CREATE TRIGGER COMMENT_DELETE ON USERCOMMENT INSTEAD OF DELETE " +
                "AS " +
                "BEGIN " +
                "WITH IDs AS (SELECT id FROM DELETED UNION ALL SELECT c.id FROM USERCOMMENT c INNER JOIN IDs i ON i.id = c.parentID) " +
                "DELETE FROM USERCOMMENT WHERE id IN (SELECT id FROM IDS) " +
                "END;";
        stmt.executeUpdate(sqlCreateCommentTrigger);

        String sqlCreatePostTrigger = "CREATE TRIGGER POST_DELETE ON POST INSTEAD OF DELETE " +
                "AS " +
                "BEGIN " +
                "DELETE FROM OPTIONS WHERE pollID IN (SELECT id FROM DELETED) " +
                "DELETE FROM USERCOMMENT WHERE postID IN (SELECT id FROM DELETED) " +
                "DELETE FROM ENGAGEMENT WHERE postID IN (SELECT id FROM DELETED) " +
                "DELETE FROM PLAYLISTCONTENT WHERE postID IN (SELECT id FROM DELETED) " +
                "DELETE FROM HISTORY WHERE postID IN (SELECT id FROM DELETED) " +
                "DELETE FROM POST WHERE id IN (SELECT id FROM DELETED) " +
                "END;";
        stmt.executeUpdate(sqlCreatePostTrigger);

        String sqlCreatePersonTrigger = "CREATE TRIGGER PERSON_DELETE ON PERSON INSTEAD OF DELETE " +
                "AS " +
                "BEGIN " +
                "DELETE FROM POST WHERE posterID IN (SELECT id FROM DELETED) " +
                "DELETE FROM USERCOMMENT WHERE userID IN (SELECT id FROM DELETED) " +
                "DELETE FROM PLAYLIST WHERE ownerID IN (SELECT id FROM DELETED) " +
                "DELETE FROM SUBSCRIBEDTO WHERE subscribedID IN (SELECT id FROM DELETED) " +
                "DELETE FROM SUBSCRIBEDTO WHERE subscriberID IN (SELECT id FROM DELETED) " +
                "DELETE FROM ENAGAGEMENT WHERE userID IN (SELECT id FROM DELETED) " +
                "DELETE FROM HISTORY WHERE userID IN (SELECT id FROM DELETED) " +
                "DELETE FROM PERSON WHERE id IN (SELECT id FROM DELETED) " +
                "END;";
        stmt.executeUpdate(sqlCreatePersonTrigger);
    }

    public static void DropEverything() throws SQLException {
        Statement stmt = JDBCUtils.connection.createStatement();

        String sqlDropPersonTrigger = "DROP TRIGGER IF EXISTS PERSON_DELETE";
        stmt.executeUpdate(sqlDropPersonTrigger);

        String sqlDropPostTrigger = "DROP TRIGGER IF EXISTS POST_DELETE";
        stmt.executeUpdate(sqlDropPostTrigger);

        String sqlDropCommentTrigger = "DROP TRIGGER IF EXISTS COMMENT_DELETE";
        stmt.executeUpdate(sqlDropCommentTrigger);

        String sqlDropHistory = "DROP TABLE IF EXISTS HISTORY";
        stmt.executeUpdate(sqlDropHistory);

        String sqlDropPlaylistContent = "DROP TABLE IF EXISTS PLAYLISTCONTENT";
        stmt.executeUpdate(sqlDropPlaylistContent);

        String sqlDropEngagement = "DROP TABLE IF EXISTS ENGAGEMENT";
        stmt.executeUpdate(sqlDropEngagement);

        String sqlDropSubscribedTo = "DROP TABLE IF EXISTS SUBSCRIBEDTO";
        stmt.executeUpdate(sqlDropSubscribedTo);

        String sqlDropOptions = "DROP TABLE IF EXISTS OPTIONS";
        stmt.executeUpdate(sqlDropOptions);

        String sqlDropPlaylist = "DROP TABLE IF EXISTS PLAYLIST";
        stmt.executeUpdate(sqlDropPlaylist);

        String sqlDropComment = "DROP TABLE IF EXISTS USERCOMMENT";
        stmt.executeUpdate(sqlDropComment);

        String sqlDropPost = "DROP TABLE IF EXISTS POST";
        stmt.executeUpdate(sqlDropPost);

        String sqlDropPerson = "DROP TABLE IF EXISTS PERSON";
        stmt.executeUpdate(sqlDropPerson);
    }

}
