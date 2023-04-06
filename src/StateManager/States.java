package StateManager;

import Services.PostService;
import Services.UserService;
import User.User;

import java.util.Scanner;

public enum States {
    NOT_LOGGED {
        @Override
        int getTask() {
            int task;

            System.out.print("Input a task (1 - register, 2 - login, 3 - exit): ");
            task = this.sc.nextInt();
            this.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1: {
                    User user = new User();
                    user.read(this.sc);

                    this.userService.register(user);
                    return NOT_LOGGED;
                }
                case 2: {
                    System.out.print("Input email: ");
                    String email = this.sc.next();

                    System.out.print("Input password: ");
                    String password = this.sc.next();

                    this.userService.login(email, password);
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

            System.out.print("Input a task (1 - show all posts, 2 - open post, 3 - show all playlists, " +
                    "4 - open playlist, 5 - logout, 6 - delete account, 7 - exit): ");
            task = this.sc.nextInt();
            this.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1: {
                    System.out.println(this.postService);
                    return LOGGED_IN;
                }
                case 2: {
                    System.out.print("Input post ID: ");
                    int postID = this.sc.nextInt();
                    this.sc.nextLine();

                    this.postService.openPost(postID);
                    return WATCHING_POST;
                }
                case 3: {
                    System.out.println("Will implement in the future.");
                    return LOGGED_IN;
                }
                case 4: {
                    System.out.println("Will implement in the future.");
                    return WATCHING_PLAYLIST;
                }
                case 5: {
                    this.userService.logout();
                    return NOT_LOGGED;
                }
                case 6: {
                    this.userService.deleteAccount();
                    return NOT_LOGGED;
                }
                case 7: {
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
            return 0;
        }

        @Override
        States performTask(int task) {
            return null;
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

    PostService postService;
    UserService userService;
    Scanner sc;

    States() {
        this.postService = new PostService();
        this.userService = new UserService();
        this.sc = new Scanner(System.in);
    }

    abstract int getTask();
    abstract States performTask(int task);
}
