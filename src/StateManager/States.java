package StateManager;

import Services.CommentService;
import Services.PostService;
import Services.UserService;
import User.User;
import User.Comment;

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
        States performTask(int task) {
            switch (task) {
                case 1: { // Register new account
                    User user = new User();
                    user.read(States.sc);

                    States.userService.register(user);
                    return this;
                }
                case 2: { // Login
                    System.out.print("Input email: ");
                    String email = States.sc.next();

                    System.out.print("Input password: ");
                    String password = States.sc.next();

                    States.userService.login(email, password);
                    return LOGGED_IN;
                }
                case 3: { // Exit program
                    return EXITED;
                }
                default: {
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

            System.out.println("Input a task:\n\t1. Show all posts;\n\t2. Show all of your posts;\n\t3. Create a new post;\n\t" +
                    "4. Open a post;\n\t5. Delete a post;\n\t6. Show all of your playlists;\n\t7. Create a new playlist;\n\t" +
                    "8. Open a playlist;\n\t9. Delete a playlist;\n\t10. Show history.\n\t11. Show user stats.\n\t12. Logout;\n\t" +
                    "13. Delete your account;\n\t14. Exit the program;");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1: { // Show all posts
                    System.out.println(States.postService);
                    return this;
                }
                case 2: { // Show all of your posts
                    System.out.println("Your posts:");
                    States.postService.showPosts(States.userService.getPosts());
                    return this;
                }
                case 3: { // Create a new post
                    int userID = States.userService.getCurrentUserID();
                    if (userID == -1) {
                        System.out.println("No user logged in.");
                        return NOT_LOGGED;
                    }

                    States.userService.addToPosts(States.postService.read(States.sc, userID));
                    return this;
                }
                case 4: { // Open a post
                    System.out.print("Input post ID: ");
                    int postID = States.sc.nextInt();
                    States.sc.nextLine();

                    if (States.postService.openPost(postID)) {
                        States.userService.addToHistory(postID);
                    }
                    return WATCHING_POST;
                }
                case 5: { // Delete a post
                    System.out.print("Input post ID: ");
                    int postID = States.sc.nextInt();
                    States.sc.nextLine();

                    int userID = States.userService.getCurrentUserID();
                    int posterID = States.postService.getPosterID(postID);
                    if (userID == -1 || userID != posterID) {
                        System.out.println("You cannot delete this post.");
                        return this;
                    }

                    States.postService.remove(postID);
                    return this;
                }
                case 6: { // Show all of your playlists
                    System.out.println("Will implement print all playlists in the future.");
                    return this;
                }
                case 7: { // Create a new playlist
                    System.out.println("Will implement create playlist in the future.");
                    return this;
                }
                case 8: { // Open a playlist
                    System.out.println("Will implement open playlist in the future.");
                    return WATCHING_PLAYLIST;
                }
                case 9: { // Delete a playlist
                    System.out.println("Will implement delete playlist in the future.");
                    return this;
                }
                case 10: { // Show history
                    System.out.println("History:");
                    States.postService.showPosts(States.userService.getHistory());
                    return this;
                }
                case 11: { //Show user stats
                    States.userService.showData();
                    return this;
                }
                case 12: { // Logout
                    States.userService.logout();
                    return NOT_LOGGED;
                }
                case 13: { // Delete your account
                    States.userService.deleteAccount();
                    return NOT_LOGGED;
                }
                case 14: { // Exit the program
                    return EXITED;
                }
                default: {
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

            System.out.println("Input a task:\n\t1. Subscribe/Unsubscribe from user.\n\t2. Like/Un-like the current post;\n\t" +
                    "3. Dislike/Un-dislike the current post;\n\t4. Add to a playlist;\n\t5. Close the current post;\n\t" +
                    "6. Show the comments;\n\t7. Add a comment;\n\t8. Reply to a comment;\n\t9. Exit the program;");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1: { // Subscribe/Unsubscribe from user
                    int posterID = States.postService.getPosterID(States.postService.getCurrentPostID());

                    States.userService.subscribe(posterID);
                    return this;
                }
                case 2: { // Like the current post
                    int userID = States.userService.getCurrentUserID();
                    if (userID == -1) {
                        System.out.println("There is no user logged in.");
                        return NOT_LOGGED;
                    }

                    States.postService.addLike(States.userService.getCurrentUserID());
                    States.postService.openPost(States.postService.getCurrentPostID());
                    return this;
                }
                case 3: { // Dislike the current post
                    int userID = States.userService.getCurrentUserID();
                    if (userID == -1) {
                        System.out.println("There is no user logged in.");
                        return NOT_LOGGED;
                    }

                    States.postService.addDislike(States.userService.getCurrentUserID());
                    States.postService.openPost(States.postService.getCurrentPostID());
                    return this;
                }
                case 4: { // Add to a playlist
                    System.out.println("Will implement add to playlist later.");
                    return this;
                }
                case 5: { // Close the current post
                    States.postService.closePost();
                    return LOGGED_IN;
                }
                case 6: { // Show the comments
                    List<Integer> commentIDS = States.postService.getCommentIDs();
                    if (commentIDS != null) {
                        System.out.println(States.commentService.getComments(commentIDS));
                    }
                    return this;
                }
                case 7: { // Add a comment
                    int userID = States.userService.getCurrentUserID();
                    int postID = States.postService.getCurrentPostID();
                    if (userID == -1 || postID == -1) {
                        System.out.println("Cannot write a comment.");
                        return this;
                    }

                    System.out.println("Input contents of comment:");
                    String text = States.sc.nextLine();

                    int commentID = States.commentService.add(new Comment(userID, postID, text));
                    States.userService.sendComment(commentID);
                    States.postService.addComment(commentID);
                    return this;
                }
                case 8: { // Reply to a comment
                    int userID = States.userService.getCurrentUserID();
                    int postID = States.postService.getCurrentPostID();
                    if (userID == -1 || postID == -1) {
                        System.out.println("Cannot write a comment.");
                        return this;
                    }

                    System.out.print("Input id of comment replied to: ");
                    int parentID = States.sc.nextInt();
                    States.sc.nextLine();

                    System.out.println("Input contents of comment:");
                    String text = States.sc.nextLine();

                    int commentID = States.commentService.add(new Comment(userID, postID, text, parentID));
                    States.userService.sendComment(commentID);
                    States.postService.addComment(commentID);
                    return this;
                }
                case 9: { // Exit the program
                    return EXITED;
                }
                default: {
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
    abstract States performTask(int task);

    static {
        States.postService = new PostService();
        States.userService = new UserService();
        States.commentService = new CommentService();
        States.sc = new Scanner(System.in);
    }
}
