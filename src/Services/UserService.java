package Services;

import User.User;

import java.util.Map;

public class UserService extends Service<User> {
    private int current_user;

    public UserService() {
        super();
        this.current_user = -1;
    }

    public void register(User newUser) {
        for (User user: this.itemHashMap.values()) {
            if (newUser.equals(user)) {
                System.out.println("User with these credentials already exists.");
                return;
            }
        }

        this.add(newUser);
    }

    public void login(String email, String password) {
        User attempt = new User("", password, email);

        for (Map.Entry<Integer, User> entry: this.itemHashMap.entrySet()) {
            int id = entry.getKey();
            User user = entry.getValue();

            if (user.equals(attempt)) {
                this.current_user = id;
                System.out.println("Welcome, " + user.getName() + "!");
                return;
            }
        }

        System.out.println("There is no user with these credentials.");
    }

    public void logout() {
        if (this.current_user == -1) {
            System.out.println("No user is logged in.");
            return;
        }

        User user = this.get(this.current_user);
        System.out.println("Goodbye, " + user.getName() + "!");
    }

}
