package StateManager;

import Entities.Post.Post;
import Entities.Post.Video;
import Entities.User.Playlist;
import Entities.User.UserComment;
import Services.*;
import Entities.User.Person;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public enum States {
    NOT_LOGGED {
        @Override
        int getTask() {
            int task;

            System.out.println("Input a task:\n\t1. Register new account;\n\t2. Login;\n\t3. Exit program.");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
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
            Integer userID = States.userService.getLoggedUserID();
            if (userID == null) {
                System.out.println("No user logged in.");
                return 5;
            }

            int task;

            System.out.println("""
                    Input a task:
                    \t1. Go to post menu;
                    \t2. Go to playlist menu;
                    \t3. Go to profile;
                    \t4. Logout;
                    \t5. Exit the program.""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1 -> { // Go to post menu
                    return POST_MENU;
                }
                case 2 -> { // Go to playlist menu
                    return PLAYLIST_MENU;
                }
                case 3 -> { // Go to profile
                    return PROFILE;
                }
                case 4 -> { // Logout
                    States.userService.logout();
                    return NOT_LOGGED;
                }
                case 5 -> { // Exit the program
                    return EXITED;
                }
                default -> {
                    System.out.println("Wrong task. Try again.");
                    return this;
                }
            }
        }
    },
    POST_MENU {
        @Override
        int getTask() {
            int task;

                System.out.println("""
                        Input a task:
                        \t1. Show post feed;
                        \t2. Show all of your posts;
                        \t3. Create a new post;
                        \t4. Open a post;
                        \t5. Show history;
                        \t6. Go back;
                        \t7. Exit program.""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            Integer userID = States.userService.getLoggedUserID();

            switch (task) {
                case 1 -> { // Show post feed
                    List<Post> posts = States.postService.getAll();
                    for (Post post : posts) {
                        System.out.println(post);
                    }

                    return this;
                }
                case 2 -> { // Show all of your posts
                    System.out.println("Your posts:");
                    List<Post> posts = States.postService.getAllFromUser(userID);
                    for (Post post : posts) {
                        System.out.println(post);
                    }

                    return this;
                }
                case 3 -> { // Create a new post
                    States.postService.add(States.sc, userID);
                    return this;
                }
                case 4 -> { // Open a post
                    System.out.print("Input post id to open: ");
                    Integer id = States.sc.nextInt();

                    States.postService.openPost(id);
                    if (States.postService.getCurrentPost() == null) {
                        return this;
                    }

                    System.out.println(States.postService.getCurrentPost());
                    return WATCHING_POST;
                }
                case 5 -> { // Show history
                    System.out.println("Will implement showing history later.");
                    return this;
                }
                case 6 -> { // Go back
                    return LOGGED_IN;
                }
                case 7 -> { // Exit program
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
            Integer postID = States.postService.getCurrentPostID();
            if (postID == null) {
                System.out.println("There is no post opened");
                return 5;
            }

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
                    \t9. Exit the program.""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            Integer userID = States.userService.getLoggedUserID();

            switch (task) {
                case 1 -> { // Subscribe/Unsubscribe from user
                    Integer posterID = States.postService.getCurrentPost().getPosterID();
                    if (posterID == null) {
                        System.out.println("This is an ad. You cannot subscribe to its poster.");
                        return this;
                    }

                    States.userService.subscribeTo(posterID);

                    return this;
                }
                case 2 -> { // Like the current post
                    States.postService.like(userID);
                    return this;
                }
                case 3 -> { // Dislike the current post
                    States.postService.dislike(userID);
                    return this;
                }
                case 4 -> { // Add to a playlist
                    Post post = States.postService.getCurrentPost();
                    if (!(post instanceof Video)) {
                        System.out.println("You cannot add a " + post.getClass().getSimpleName() +" to a playlist; it needs to be a Video!");
                        return this;
                    }

                    System.out.println("Your playlists:");
                    List<Playlist> playlists = States.playlistService.getAllFromUser(userID);
                    for (Playlist playlist: playlists) {
                        System.out.println(playlist);
                    }

                    System.out.print("\nChoose playlist id to add this video to: ");
                    Integer playlistID = States.sc.nextInt();

                    if (playlists.stream().noneMatch(playlist -> (Objects.equals(playlist.getID(), playlistID)))) {
                        System.out.println("The requested playlist id doesn't exist!");
                        return this;
                    }

                    States.postService.addToPlaylist(playlistID);
                    return this;
                }
                case 5 -> { // Close the current post
                    States.postService.closePost();
                    return POST_MENU;
                }
                case 6 -> { // Show the comments
                    System.out.println("Comments:");

                    List<UserComment> comments = States.postService.getCurrentPost().getComments();
                    Stack<Integer> hierarchy = new Stack<>();

                    for (UserComment comment: comments) {
                        while ((!hierarchy.isEmpty()) && !Objects.equals(comment.getParentID(), hierarchy.peek())) {
                            hierarchy.pop();
                        }

                        System.out.println(comment.toString().indent(hierarchy.size()));
                        System.out.println();
                        hierarchy.push(comment.getID());
                    }

                    return this;
                }
                case 7 -> { // Add a comment
                    States.postService.addComment(States.sc, userID);
                    return this;
                }
                case 8 -> { // Reply to a comment
                    System.out.print("Input id of comment you are replying to: ");
                    Integer parentID = States.sc.nextInt();
                    States.sc.nextLine();

                    States.postService.addComment(States.sc, userID, parentID);
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
    PLAYLIST_MENU {
        @Override
        int getTask() {
            int task;

            System.out.println("""
                        Input a task:
                        \t1. Show all of your playlists;
                        \t2. Create a new playlist;
                        \t3. Open a playlist;
                        \t4. Delete a playlist;
                        \t5. Go back;
                        \t6. Exit program.""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            Integer userID = States.userService.getLoggedUserID();

            switch(task) {
                case 1 -> { // Show all of your playlists
                    List<Playlist> playlists =  States.playlistService.getAllFromUser(userID);

                    System.out.println("Your playlists:");
                    for (Playlist playlist: playlists) {
                        System.out.println(playlist);
                    }

                    return this;
                }
                case 2 -> { // Create a new playlist
                    States.playlistService.add(States.sc, userID);
                    return this;
                }
                case 3 -> { // Open a playlist
                    System.out.print("Input playlist id to open: ");
                    Integer playlistID = States.sc.nextInt();

                    States.playlistService.openPlaylist(playlistID);
                    if (States.playlistService.getOpenPlaylist() == null) {
                        return this;
                    }

                    return WATCHING_PLAYLIST;
                }
                case 4 -> { // Delete a playlist
                    System.out.print("Input playlist id to delete: ");
                    Integer playlistID = States.sc.nextInt();

                    States.playlistService.remove(playlistID);
                    return this;
                }
                case 5 -> { // Go back
                    States.playlistService.closePlaylist();
                    return LOGGED_IN;
                }
                case 6 -> { // Exit program
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
            Integer playlistID = States.playlistService.getOpenPlaylistID();
            if (playlistID == null) {
                System.out.println("There is no playlist open right now.");
                return 7;
            }

            int task;

            System.out.println("""
                        Input a task:
                        \t1. Show all videos;
                        \t2. Switch ordering method;
                        \t3. Go to previous video;
                        \t4. Go to next video;
                        \t5. Go to video by id;
                        \t6. Remove video from playlist;
                        \t7. Exit playlist;
                        \t8. Exit program.""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            switch (task) {
                case 1 -> { // Show all videos
                    System.out.println("Videos:");
                    List<Video> videos = States.playlistService.getOpenPlaylist().getVideos();

                    for (Video video: videos) {
                        System.out.println(video);
                    }

                    return this;
                }
                case 2 -> { // Switch ordering method
                    States.playlistService.switchOrdering();

                    return this;
                }
                case 3 -> { // Go to previous video
                    States.playlistService.previousVideo();

                    return this;
                }
                case 4 -> { // Go to next video
                    States.playlistService.nextVideo();

                    return this;
                }
                case 5 -> { // Go to video by id
                    System.out.print("Input id position of video between 1 and " + States.playlistService.getOpenPlaylist().getVideos().size() + ": ");
                    int position = States.sc.nextInt();
                    if (position < 1 || position > States.playlistService.getOpenPlaylist().getVideos().size()) {
                        System.out.println("Wrong position index.");
                        return this;
                    }

                    States.playlistService.setVideo(position - 1);
                    return this;
                }
                case 6 -> { // Remove video from playlist
                    States.playlistService.removeCurrentVideo();
                    return this;
                }
                case 7 -> { // Exit playlist
                    return PLAYLIST_MENU;
                }
                case 8 -> { // Exit program
                    return EXITED;
                }
                default -> {
                    System.out.println("Wrong task. Try again.");
                    return this;
                }
            }
        }
    },
    PROFILE {
        @Override
        int getTask() {
            return 0;
        }

        @Override
        States performTask(int task) {
            return this;
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
    static PlaylistService playlistService;
    static Scanner sc;

    States() {
    }

    abstract int getTask();
    abstract States performTask(int task) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    static {
        States.postService = PostService.getInstance();
        States.userService = UserService.getInstance();
        States.playlistService = PlaylistService.getInstance();
        States.sc = new Scanner(System.in);
    }
}
