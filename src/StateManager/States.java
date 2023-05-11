package StateManager;

import Entities.Post.Post;
import Services.CommentService;
import Services.PostService;
import Services.UserService;
import Entities.User.Person;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public enum States {
    NOT_LOGGED {
        @Override
        int getTask() {
            int task;

            System.out.println("Input a task:\n\t1. Register new account;\n\t2. Login;\n\t3. Exit program;");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            switch (task) {
                case 1 -> { // Register new account
                    Person person = new Person();
                    person.read(States.sc);

                    States.userService.register(person);
                    return this;
                }
                case 2 -> { // Login
                    System.out.print("Input email: ");
                    String email = States.sc.next();

                    System.out.print("Input password: ");
                    String password = States.sc.next();

                    States.userService.login(email, password);

                    if (States.userService.getLoggedUserID() == null) {
                        return this;
                    }
                    return LOGGED_IN;
                }
                case 3 -> { // Exit program
                    return EXITED;
                }
                default -> {
                    System.out.println("Wrong task. Try again.");
                    return this;
                }
            }
        }
    },
    LOGGED_IN {
        @Override
        int getTask() {
            int task;

            System.out.println("""
                    Input a task:
                    \t1. Show all posts;
                    \t2. Show all of your posts;
                    \t3. Create a new post;
                    \t4. Open a post;
                    \t5. Delete a post;
                    \t6. Show all of your playlists;
                    \t7. Create a new playlist;
                    \t8. Open a playlist;
                    \t9. Delete a playlist;
                    \t10. Logout;
                    \t11. Delete your account;
                    \t12. Exit the program;""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            Integer userID = States.userService.getLoggedUserID();
            if (userID == null) {
                System.out.println("No user logged in.");
                return NOT_LOGGED;
            }

            switch (task) {
                case 1 -> { // Show all posts
                    List<Post> posts = States.postService.getAll();
                    for (Post post: posts) {
                        System.out.println(post);
                    }

                    return this;
                }
                case 2 -> { // Show all of your posts
                    System.out.println("Your posts:");
                    List<Post> posts = States.postService.getAllFromUser(userID);
                    for (Post post: posts) {
                        System.out.println(post);
                    }

                    return this;
                }
                case 3 -> { // Create a new post
                    States.postService.read(States.sc, userID);
                    return this;
                }
                case 4 -> { // Open a post
                    System.out.print("Input post id to open: ");
                    Integer id = States.sc.nextInt();

                    States.postService.setCurrentPostID(id);
                    return WATCHING_POST;
                }
                case 5 -> { // Delete a post
                    System.out.println("Input post id to delete: ");
                    Integer id = States.sc.nextInt();

                    States.postService.remove(id);
                    return this;
                }
                case 6 -> { // Show all of your playlists
                    System.out.println("Will implement print all playlists in the future.");
                    return this;
                }
                case 7 -> { // Create a new playlist
                    System.out.println("Will implement create playlist in the future.");
                    return this;
                }
                case 8 -> { // Open a playlist
                    System.out.println("Will implement open playlist in the future.");
                    return WATCHING_PLAYLIST;
                }
                case 9 -> { // Delete a playlist
                    System.out.println("Will implement delete playlist in the future.");
                    return this;
                }
                case 10 -> { // Logout
                    States.userService.logout();
                    return NOT_LOGGED;
                }
                case 11 -> { // Delete your account
                    States.userService.deleteAccount();
                    return NOT_LOGGED;
                }
                case 12 -> { // Exit the program
                    return EXITED;
                }
                default -> {
                    System.out.println("Wrong task. Try again.");
                    return this;
                }
            }
        }
    },
    WATCHING_POST {
        @Override
        int getTask() {
            int task;

            System.out.println("""
                    Input a task:
                    \t1. Subscribe/Unsubscribe from user;
                    \t2. Like/Un-like the current post;
                    \t3. Dislike/Un-dislike the current post;
                    \t4. Add to a playlist;
                    \t5. Close the current post;
                    \t6. Show the comments;
                    \t7. Add a comment;
                    \t8. Reply to a comment;
                    \t9. Exit the program;""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1 -> { // Subscribe/Unsubscribe from user
                    return this;
                }
                case 2 -> { // Like the current post
                    return this;
                }
                case 3 -> { // Dislike the current post
                    return this;
                }
                case 4 -> { // Add to a playlist
                    System.out.println("Will implement add to playlist later.");
                    return this;
                }
                case 5 -> { // Close the current post
                    return LOGGED_IN;
                }
                case 6 -> { // Show the comments
                    return this;
                }
                case 7 -> { // Add a comment
                    return this;
                }
                case 8 -> { // Reply to a comment
                    return this;
                }
                case 9 -> { // Exit the program
                    return EXITED;
                }
                default -> {
                    System.out.println("Wrong task. Try again.");
                    return this;
                }
            }
        }
    },
    WATCHING_PLAYLIST {
        @Override
        int getTask() {
            return 0;
        }

        @Override
        States performTask(int task) {
            return null;
        }
    },
    EXITED {
        @Override
        int getTask() {
            return 0;
        }

        @Override
        States performTask(int task) {
            return this;
        }
    };

    static PostService postService;
    static UserService userService;
    static CommentService commentService;
    static Scanner sc;

    States() {
    }

    abstract int getTask();
    abstract States performTask(int task) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    static {
        States.postService = PostService.getInstance();
        States.userService = UserService.getInstance();
        States.commentService = CommentService.getInstance();
        States.sc = new Scanner(System.in);
    }
}
