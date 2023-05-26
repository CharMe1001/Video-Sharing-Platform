package entities.user;

import entities.BaseEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Person extends BaseEntity {
    // The name of the user.
    private String name;

    // The password of the user.
    private String password;

    // The email address of the user.
    private String email;

    public Person() {
        this.name = "";
        this.password = "";
        this.email = "";
    }

    public Person(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    // Reads the data of the user using the scanner.
    public void read(Scanner sc) {
        System.out.print("Input user name: ");
        this.name = sc.next();

        System.out.print("Input user password: ");
        this.password = sc.next();

        System.out.print("Input user email: ");
        this.email = sc.next();
    }

    @Override
    public String toString() {
        String ret = "";

        ret += ("Name: " + this.name + "\n");
        ret += ("Email: " + this.email + "\n");

        return ret;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Person person)) {
            return false;
        }

        return (this.email.equals(person.email) && this.password.equals(person.password));
    }

    // Returns the name of the user.
    public String getName() {
        return this.name;
    }

    // Returns the email address of the user.
    public String getEmail() {return this.email;}

    @Override
    public String getColumns() {
        return super.getColumns() + "name, password, email";
    }

    @Override
    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + "'" + this.name + "', '" + this.password + "', '" + this.email + "')";
    }

    @Override
    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + "name = '" + this.name + "', email = '" + this.email + "'";
    }

    @Override
    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        try {
            this.name = src.getString("name");
        } catch (SQLException sqlE) {
            System.out.println("Error getting name of user with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.email = src.getString("email");
        } catch (SQLException sqlE) {
            System.out.println("Error getting email of user with id = " + this.id + "!");
            throw sqlE;
        }
    }
}
