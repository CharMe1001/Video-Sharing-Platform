package Services;

import javax.swing.plaf.nimbus.State;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
    public static Connection connection;

    public static void CreateTables() throws SQLException {
        Statement stmt = JDBCUtils.connection.createStatement();

        String sqlCreateUser = "CREATE TABLE PERSON(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "dateCreated DATE DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "objType VARCHAR(32) NOT NULL," +
                "name VARCHAR(32) NOT NULL," +
                "password VARCHAR(32) NOT NULL," +
                "email VARCHAR(32) NOT NULL" +
                ")";
        stmt.executeUpdate(sqlCreateUser);

        String sqlCreatePost = "CREATE TABLE POST(" +
                "id INT IDENTITY(1, 1) PRIMARY KEY," +
                "dateCreated DATE DEFAULT CURRENT_TIMESTAMP NOT NULL," +
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
                "dateCreated DATE DEFAULT CURRENT_TIMESTAMP NOT NULL," +
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
                "dateCreated DATE DEFAULT CURRENT_TIMESTAMP NOT NULL," +
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
    }

    public static void DropEverything() throws SQLException {
        Statement stmt = JDBCUtils.connection.createStatement();

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
