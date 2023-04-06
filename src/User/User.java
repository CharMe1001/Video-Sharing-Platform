package User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    private String name;
    private String password;
    private String email;

    private int cnt_subscribers;
    private List<Integer> subscribed_to;

    private List<Integer> comments;

    private List<Integer> history;

    private List<Integer> posts;

    private List<Integer> playlists;

    public User() {
        this.name = "";
        this.password = "";
        this.email = "";
        this.cnt_subscribers = 0;

        this.subscribed_to = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.history = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.playlists = new ArrayList<>();
    }

    public User(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.cnt_subscribers = 0;

        this.subscribed_to = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.history = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.playlists = new ArrayList<>();
    }

    public void read(Scanner sc) {
        System.out.print("Input user name: ");
        this.name = sc.next();

        System.out.print("Input user password: ");
        this.password = sc.next();

        System.out.print("Input user email: ");
        this.email = sc.next();

        this.cnt_subscribers = 0;

        this.subscribed_to = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.history = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.playlists = new ArrayList<>();
    }

    public String toString() {
        String ret = "";

        ret += ("Name: " + this.name + "\n");
        ret += ("Email: " + this.email + "\n");
        ret += ("Number of subscribers: " + this.cnt_subscribers + "\n");

        return ret;
    }

    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }

        User user = (User)object;

        return (this.email.equals(user.email) && this.password.equals(user.password));
    }

    public String getName() {
        return this.name;
    }

    public void addComment(int commentID) {
        this.comments.add(commentID);
    }
}
