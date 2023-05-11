package Entities.User;

import Entities.BaseEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Person extends BaseEntity {
    private String name;
    private String password;
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

    public void read(Scanner sc) {
        System.out.print("Input user name: ");
        this.name = sc.next();

        System.out.print("Input user password: ");
        this.password = sc.next();

        System.out.print("Input user email: ");
        this.email = sc.next();
    }

    public String toString() {
        String ret = "";

        ret += ("Name: " + this.name + "\n");
        ret += ("Email: " + this.email + "\n");

        return ret;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Person)) {
            return false;
        }

        Person person = (Person)object;

        return (this.email.equals(person.email) && this.password.equals(person.password));
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {return this.email;}

    public String getColumns() {
        return super.getColumns() + "name, password, email";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + "'" + this.name + "', '" + this.password + "', '" + this.email + "')";
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + ", name = '" + this.name + "', email = '" + this.email + "'";
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        this.name = src.getString("name");
        this.email = src.getString("email");
    }
}
