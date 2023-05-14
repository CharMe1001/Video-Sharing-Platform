package Services;

import Entities.BaseEntity;
import Entities.User.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

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
    }

    public void deleteAccount() {
        if (this.logged_user == null) {
            System.out.println("No user is logged in.");
            return;
        }

        String name = this.logged_user.getName();

        this.remove(this.logged_user.getID());
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

                System.out.println("Successfully unsubscribed from user with id = " + subscribedID);
            } else {
                String sqlInsert = "INSERT INTO SUBSCRIBEDTO(subscriberID, subscribedID) VALUES(" + this.getLoggedUserID() + ", " + subscribedID + ")";
                stmt.executeUpdate(sqlInsert);

                System.out.println("Successfully subscribed to user with id = " + subscribedID);
            }
        } catch (SQLException sqlE) {
            System.out.println("Error updating subscription status of user with id = " + this.getLoggedUserID() + " to user with id = " + subscribedID + "!");
            System.out.println(sqlE.getMessage());
        }
    }
}
