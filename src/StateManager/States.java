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

            System.out.print("Input a task (1 - register, 2 - login, 3 - exit): ");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1: {
                    User user = new User();
                    user.read(States.sc);

                    States.userService.register(user);
                    return NOT_LOGGED;
                }
                case 2: {
                    System.out.print("Input email: ");
                    String email = States.sc.next();

                    System.out.print("Input password: ");
                    String password = States.sc.next();

                    States.userService.login(email, password);
                    return LOGGED_IN;
                }
                case 3: {
                    return EXITED;
                }
                default: {
                    System.out.println("Wrong task. Try again.");
                    return NOT_LOGGED;
                }
            }
        }
    },
    LOGGED_IN {
        @Override
        int getTask() {
            int task;

            System.out.print("Input a task (1 - show all posts, 2 - create post,  3 - open post, 4 - delete post, " +
                    "5 - show all playlists, 6 - create playlist, 7 - open playlist, 8 - delete playlist, " +
                    "9 - logout, 10 - delete account, 11 - exit): ");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1: {
                    System.out.println(States.postService);
                    return LOGGED_IN;
                }
                case 2: {
                    int userID = States.userService.getCurrentUserID();
                    if (userID == -1) {
                        System.out.println("No user logged in.");
                        return NOT_LOGGED;
                    }

                    States.postService.read(States.sc, userID);
                    return LOGGED_IN;
                }
                case 3: {
                    System.out.print("Input post ID: ");
                    int postID = States.sc.nextInt();
                    States.sc.nextLine();

                    States.postService.openPost(postID);
                    return WATCHING_POST;
                }
                case 4: {
                    System.out.print("Input post ID: ");
                    int postID = States.sc.nextInt();
                    States.sc.nextLine();

                    States.postService.remove(postID);
                    return LOGGED_IN;
                }
                case 5: {
                    System.out.println("Will implement print all playlists in the future.");
                    return LOGGED_IN;
                }
                case 6: {
                    System.out.println("Will implement create playlist in the future.");
                    return LOGGED_IN;
                }
                case 7: {
                    System.out.println("Will implement open playlist in the future.");
                    return WATCHING_PLAYLIST;
                }
                case 8: {
                    System.out.println("Will implement delete playlist in the future.");
                    return LOGGED_IN;
                }
                case 9: {
                    States.userService.logout();
                    return NOT_LOGGED;
                }
                case 10: {
                    States.userService.deleteAccount();
                    return NOT_LOGGED;
                }
                case 11: {
                    return EXITED;
                }
                default: {
                    System.out.println("Wrong task. Try again.");
                    return LOGGED_IN;
                }
            }
        }
    },
    WATCHING_POST {
        @Override
        int getTask() {
            int task;

            System.out.print("Input a task (1 - like post, 2 - dislike post, 3 - add to playlist, " +
                    "4 - close post, 5 - show comments, 6 - add comment, 7 - add reply, 8 - exit): ");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1: {
                    int userID = States.userService.getCurrentUserID();
                    if (userID == -1) {
                        System.out.println("There is no user logged in.");
                        return NOT_LOGGED;
                    }

                    States.postService.addLike(States.userService.getCurrentUserID());
                    return WATCHING_POST;
                }
                case 2: {
                    int userID = States.userService.getCurrentUserID();
                    if (userID == -1) {
                        System.out.println("There is no user logged in.");
                        return NOT_LOGGED;
                    }

                    States.postService.addDislike(States.userService.getCurrentUserID());
                    return WATCHING_POST;
                }
                case 3: {
                    System.out.println("Will implement add to playlist later.");
                    return WATCHING_POST;
                }
                case 4: {
                    States.postService.closePost();
                    return LOGGED_IN;
                }
                case 5: {
                    List<Integer> commentIDS = States.postService.getCommentIDs();
                    if (commentIDS != null) {
                        System.out.println(States.commentService.getComments(commentIDS));
                    }
                    return WATCHING_POST;
                }
                case 6: {
                    int userID = States.userService.getCurrentUserID();
                    int postID = States.postService.getCurrentPostID();
                    if (userID == -1 || postID == -1) {
                        System.out.println("Cannot write a comment.");
                        return WATCHING_POST;
                    }

                    System.out.println("Input contents of comment:");
                    String text = States.sc.nextLine();

                    int commentID = States.commentService.add(new Comment(userID, postID, text));
                    States.userService.sendComment(commentID);
                    States.postService.addComment(commentID);
                    return WATCHING_POST;
                }
                case 7: {
                    int userID = States.userService.getCurrentUserID();
                    int postID = States.postService.getCurrentPostID();
                    if (userID == -1 || postID == -1) {
                        System.out.println("Cannot write a comment.");
                        return WATCHING_POST;
                    }

                    System.out.print("Input id of comment replied to: ");
                    int parentID = States.sc.nextInt();
                    States.sc.nextLine();

                    System.out.println("Input contents of comment:");
                    String text = States.sc.nextLine();

                    int commentID = States.commentService.add(new Comment(userID, postID, text, parentID));
                    States.userService.sendComment(commentID);
                    States.postService.addComment(commentID);
                    return WATCHING_POST;
                }
                case 8: {
                    return EXITED;
                }
                default: {
                    System.out.println("Wrong task. Try again.");
                    return WATCHING_POST;
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
            return EXITED;
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
