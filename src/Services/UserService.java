package Services;

import Entities.BaseEntity;
import Entities.User.Person;

import java.lang.reflect.InvocationTargetException;
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

    private Integer logged_user;

    private UserService() {
        this.logged_user = null;
    }

    public Integer getLoggedUserID() {
        return this.logged_user;
    }

    public void register(Person newPerson) throws SQLException {
        String sqlCheck = "SELECT DISTINCT 1 FROM PERSON WHERE email = '" + newPerson.getEmail() + "'";
        Statement stmt = Service.connection.createStatement();

        ResultSet res = stmt.executeQuery(sqlCheck);
        if (res.next()) {
            System.out.println("User with this email already exists.");
            return;
        }

        this.add(newPerson);
    }

    public void login(String email, String password) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String sqlLogin = "SELECT * FROM PERSON WHERE email = '" + email + "' AND password = '" + password + "'";
        Statement stmt = Service.connection.createStatement();
        ResultSet res = stmt.executeQuery(sqlLogin);

        if (res.next()) {
            Person person = (Person)BaseEntity.getFromSelect(res);
            this.logged_user = person.getID();

            System.out.println("Welcome, " + person.getName() + "!");
            return;
        }

        System.out.println("There is no user with these credentials.");
    }

    private Person getCurrentUser() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (this.logged_user == null) {
            return null;
        }

        return this.get(this.logged_user);
    }

    public void logout() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Person person = this.getCurrentUser();

        if (person == null) {
            System.out.println("No user is logged in.");
            return;
        }

        this.logged_user = null;
        System.out.println("Goodbye, " + person.getName() + "!");
    }

    public void deleteAccount() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (this.logged_user == null) {
            System.out.println("No user is logged in.");
            return;
        }

        Person person = this.getCurrentUser();

        assert person != null;
        String name = person.getName();

        this.remove(this.logged_user);
        this.logged_user = null;
        System.out.println("Goodbye forever, " + name + "!");
    }

    public void showData() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        System.out.println(this.getCurrentUser());
    }
}
