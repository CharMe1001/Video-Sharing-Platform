package services;

import entities.BaseEntity;
import entities.Post.Post;
import entities.User.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class UserService extends Service<Person> {
    private static UserService instance = null;
    public static UserService getInstance() {
        if (UserService.instance == null) {
            UserService.instance = new UserService();
        }

        return UserService.instance;
    }

    private Person logged_user;

    private UserService() {
        this.logged_user = null;
    }

    public Integer getLoggedUserID() {
        return this.logged_user == null ? null : this.logged_user.getID();
    }

    public void register(Person newPerson) {
        String sqlCheck = "SELECT DISTINCT 1 FROM PERSON WHERE email = '" + newPerson.getEmail() + "'";
        ResultSet res;

        try {
            Statement stmt = Service.connection.createStatement();
            res = stmt.executeQuery(sqlCheck);

            if (res.next()) {
                System.out.println("User with this email already exists.");
                return;
            }
        } catch (SQLException sqlE) {
            System.out.println("Error checking if user already exists!");
            System.out.println("Select statement: " + sqlCheck);
            System.out.println(sqlE.getMessage());

            return;
        }

        try {
            this.add(newPerson);
            AuditService.getInstance().writeAction("Created new user with id " + newPerson.getID() + ".");
        } catch (Exception e) {
            System.out.println("Error adding new user into database!");
            System.out.println(e.getMessage());
        }

    }

    public void login(String email, String password) {
        String sqlLogin = "SELECT * FROM PERSON WHERE email = '" + email + "' AND password = '" + password + "'";
        ResultSet res;

        try {
            Statement stmt = Service.connection.createStatement();
            res = stmt.executeQuery(sqlLogin);
        } catch (SQLException sqlE) {
            System.out.println("Error retrieving user details from database!");
            System.out.println(sqlE.getMessage());
            return;
        }

        try {
            if (res.next()) {
                Person person = (Person)BaseEntity.getFromSelect(res);
                this.logged_user = person;

                AuditService.getInstance().writeAction("User with id " + this.getLoggedUserID() + " has logged in.");
                System.out.println("Welcome, " + person.getName() + "!");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error building Person object from database information");
            return;
        }

        System.out.println("There is no user with these credentials.");
    }

    private Person getCurrentUser() {
        return this.logged_user;
    }

    public void logout() {
        Person person = this.getCurrentUser();

        if (person == null) {
            System.out.println("No user is logged in.");
            return;
        }

        this.logged_user = null;
        System.out.println("Goodbye, " + person.getName() + "!");
        AuditService.getInstance().writeAction("User with id " + this.getLoggedUserID() + " has logged out of their account.");
    }

    public void deleteAccount() {
        if (this.logged_user == null) {
            System.out.println("No user is logged in.");
            return;
        }

        String name = this.logged_user.getName();

        this.remove(this.logged_user.getID());
        AuditService.getInstance().writeAction("User with id " + this.getLoggedUserID() + " has deleted their account.");
        this.logged_user = null;
        System.out.println("Goodbye forever, " + name + "!");
    }

    public void subscribeTo(Integer subscribedID) {
        if (Objects.equals(subscribedID, this.getLoggedUserID())) {
            System.out.println("You cannot subscribe to yourself!");
            return;
        }

        Statement stmt;

        try {
            stmt = Service.connection.createStatement();
        } catch (SQLException sqlE) {
            System.out.println("Error creating statement object.");
            System.out.println(sqlE.getMessage());
            return;
        }

        String sqlGet = "SELECT id FROM SUBSCRIBEDTO WHERE subscriberID = " + this.getLoggedUserID() + " AND subscribedID = " + subscribedID;
        ResultSet resultSet;

        try {
            resultSet = stmt.executeQuery(sqlGet);
        } catch (SQLException sqlE) {
            System.out.println("Error getting subscription status for user with id = " + this.getLoggedUserID() + " to user with id = " + subscribedID + "!");
            System.out.println("Select statement: " + sqlGet);
            System.out.println(sqlE.getMessage());

            return;
        }

        try {
            if (resultSet.next()) {
                String sqlDelete = "DELETE FROM SUBSCRIBEDTO WHERE id = " + resultSet.getInt("id");
                stmt.executeUpdate(sqlDelete);

                AuditService.getInstance().writeAction("User with id " + this.getLoggedUserID() + " has unsubscribed from user with id " + subscribedID + ".");
                System.out.println("Successfully unsubscribed from user with id = " + subscribedID);
            } else {
                String sqlInsert = "INSERT INTO SUBSCRIBEDTO(subscriberID, subscribedID) VALUES(" + this.getLoggedUserID() + ", " + subscribedID + ")";
                stmt.executeUpdate(sqlInsert);

                AuditService.getInstance().writeAction("User with id " + this.getLoggedUserID() + " has subscribed to user with id " + subscribedID + ".");
                System.out.println("Successfully subscribed to user with id = " + subscribedID);
            }
        } catch (SQLException sqlE) {
            System.out.println("Error updating subscription status of user with id = " + this.getLoggedUserID() + " to user with id = " + subscribedID + "!");
            System.out.println(sqlE.getMessage());
        }
    }

    public Integer getNumberOfSubscribers() {
        String sqlGet = "SELECT COUNT(*) AS cnt FROM SUBSCRIBEDTO WHERE subscribedID = " + this.getLoggedUserID();
        ResultSet res;

        try {
            Statement getStmt = Service.connection.createStatement();
            res = getStmt.executeQuery(sqlGet);
        } catch (SQLException sqlE) {
            System.out.println("Error getting number of subscribers for user with id = " + this.getLoggedUserID() + "!");
            System.out.println("Get statement = " + sqlGet);
            System.out.println(sqlE.getMessage());
            return null;
        }

        try {
            if (res.next()) {
                return res.getInt("cnt");
            } else {
                throw new SQLException("Error retrieving number of subscribers of user with id = " + this.getLoggedUserID() + " from ResultSet object!");
            }
        } catch (SQLException sqlE) {
            System.out.println("Error getting number of subscribers for user with id = " + this.getLoggedUserID() + "!");
            System.out.println(sqlE.getMessage());
            return null;
        }
    }

    public List<Person> getPeopleSubscribedTo() {
        String sqlGet = "SELECT * FROM SUBSCRIBEDTO WHERE subscriberID = " + this.getLoggedUserID();
        ResultSet res;

        try {
            Statement getStmt = Service.connection.createStatement();
            res = getStmt.executeQuery(sqlGet);
        } catch (SQLException sqlE) {
            System.out.println("Error getting people user with id = " + this.getLoggedUserID() + " is subscribed to!");
            System.out.println("Get statement: " + sqlGet);
            System.out.println(sqlE.getMessage());

            return null;
        }

        try {
            List<Person> people = new ArrayList<>();

            while (true) {
                try {
                    if (!res.next()) {
                        break;
                    }
                } catch (SQLException sqlE) {
                    System.out.println("Error retrieving next user that user with id = " + this.getLoggedUserID() + " is subscribed to!");
                    throw sqlE;
                }

                people.add((Person)BaseEntity.getFromSelect(res));
            }
            return people;
        } catch (Exception e) {
            System.out.println("Error getting list of users that user with id = " + this.getLoggedUserID() + " is subscribed to!");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Post> getOwnPosts() {
        String sqlGet = "SELECT * FROM POST WHERE posterID = " + this.getLoggedUserID();
        ResultSet res;

        try {
            Statement getStmt = Service.connection.createStatement();
            res = getStmt.executeQuery(sqlGet);
        } catch (SQLException sqlE) {
            System.out.println("Error getting list of posts that user with id = " + this.getLoggedUserID() + " owns!");
            System.out.println("Get statement: " + sqlGet);
            System.out.println(sqlE.getMessage());
            return null;
        }

        try {
            List<Post> posts = new ArrayList<>();

            while (true) {
                try {
                    if (!res.next()) {
                        break;
                    }
                } catch (SQLException sqlE) {
                    System.out.println("Error retrieving next post that user with id = " + this.getLoggedUserID() + " owns!");
                    throw sqlE;
                }

                posts.add((Post)BaseEntity.getFromSelect(res));
            }

            return posts;
        } catch (Exception e) {
            System.out.println("Error getting list of posts that user with id = " + this.getLoggedUserID() + " owns!");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Post> getHistory() {
        String sqlGet = "SELECT * FROM HISTORY h JOIN POST p ON p.id = h.postID WHERE userID = " + this.getLoggedUserID() + " ORDER BY dateAccessed DESC";
        ResultSet res;

        try {
            Statement getStmt = Service.connection.createStatement();
            res = getStmt.executeQuery(sqlGet);
        } catch (SQLException sqlE) {
            System.out.println("Error getting post history of user with id = " + this.getLoggedUserID() + "!");
            System.out.println("Get statement: " + sqlGet);
            System.out.println(sqlE.getMessage());
            return null;
        }

        try {
            List<Post> history = new ArrayList<>();
            while (true) {
                try {
                    if (!res.next()) {
                        break;
                    }
                } catch (SQLException sqlE) {
                    System.out.println("Error retrieving next post from the history of user with id = " + this.getLoggedUserID() + "!");
                    throw sqlE;
                }

                history.add((Post)BaseEntity.getFromSelect(res));
            }

            return history;
        } catch (Exception e) {
            System.out.println("Error getting post history of user with id = " + this.getLoggedUserID() + "!");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void updateDetails(Scanner sc) {
        System.out.println("Input the requested changes or nothing if you want to keep it:");
        System.out.print("Input new name: ");
        String newName = sc.nextLine();
        System.out.println("Input new password: ");
        String newPassword = sc.nextLine();
        System.out.println("Input new email: ");
        String newEmail = sc.nextLine();

        if (Objects.equals(newName, "")) {
            newName = this.logged_user.getName();
        }

        if (Objects.equals(newEmail, "")) {
            newEmail = this.logged_user.getEmail();
        }

        Person newPerson = new Person(newName, "", newEmail);
        newPerson.setID(this.getLoggedUserID());

        if (this.set(newPerson)) {
            this.logged_user = newPerson;
            AuditService.getInstance().writeAction("User with id " + this.getLoggedUserID() + " has updated their account details.");
        }

        if (!Objects.equals(newPassword, "")) {
            String sqlUpdate = "UPDATE PERSON SET password = " + newPassword + " WHERE id = " + this.getLoggedUserID();
            int rowsUpdated;

            try {
                Statement updateStmt = Service.connection.createStatement();
                rowsUpdated = updateStmt.executeUpdate(sqlUpdate);
                if (rowsUpdated == 0) {
                    throw new SQLException("Password of user = " + this.getLoggedUserID() + " has not been updated!");
                } else {
                    AuditService.getInstance().writeAction("User with id " + this.getLoggedUserID() + " has updated their password.");
                }
            } catch (SQLException sqlE) {
                System.out.println("Error updating password of user with id = " + this.getLoggedUserID() + "!");
                System.out.println("Update statement: " + sqlUpdate);
                System.out.println(sqlE.getMessage());
            }

        }

    }
}
