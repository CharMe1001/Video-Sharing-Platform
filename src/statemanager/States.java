package statemanager;

import entities.Post.Post;
import entities.Post.Video;
import entities.User.Playlist;
import entities.User.UserComment;
import services.*;
import entities.User.Person;

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

                    UserService.getInstance().register(person);
                    return this;
                }
                case 2 -> { // Login
                    System.out.print("Input email: ");
                    String email = States.sc.next();

                    System.out.print("Input password: ");
                    String password = States.sc.next();

                    UserService.getInstance().login(email, password);

                    if (UserService.getInstance().getLoggedUserID() == null) {
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
            Integer userID = UserService.getInstance().getLoggedUserID();
            if (userID == null) {
                System.out.println("No user logged in.");
                return 4;
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
                    UserService.getInstance().logout();
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
                        \t2. Create a new post;
                        \t3. Open a post;
                        \t4. Go back;
                        \t5. Exit program.""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            Integer userID = UserService.getInstance().getLoggedUserID();

            switch (task) {
                case 1 -> { // Show post feed
                    List<Post> posts = PostService.getInstance().getAll();
                    for (Post post : posts) {
                        System.out.println(post);
                    }
                    AuditService.getInstance().writeAction("User with id " + userID + " has requested the post feed.");

                    return this;
                }
                case 2 -> { // Create a new post
                    PostService.getInstance().add(States.sc, userID);

                    return this;
                }
                case 3 -> { // Open a post
                    System.out.print("Input post id to open: ");
                    Integer id = States.sc.nextInt();

                    PostService.getInstance().openPost(id);
                    if (PostService.getInstance().getCurrentPost() == null) {
                        return this;
                    }

                    PostService.getInstance().addToHistory(userID);
                    System.out.println(PostService.getInstance().getCurrentPost());
                    AuditService.getInstance().writeAction("User with id " + userID + " has opened post with id " + id + ".");
                    return WATCHING_POST;
                }
                case 4 -> { // Go back
                    return LOGGED_IN;
                }
                case 5 -> { // Exit program
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
            Integer postID = PostService.getInstance().getCurrentPostID();
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
            Integer userID = UserService.getInstance().getLoggedUserID();
            Integer postID = PostService.getInstance().getCurrentPostID();

            switch (task) {
                case 1 -> { // Subscribe/Unsubscribe from user
                    Integer posterID = PostService.getInstance().getCurrentPost().getPosterID();
                    if (posterID == null) {
                        System.out.println("This is an ad. You cannot subscribe to its poster.");
                        return this;
                    }

                    UserService.getInstance().subscribeTo(posterID);

                    return this;
                }
                case 2 -> { // Like the current post
                    PostService.getInstance().like(userID);
                    return this;
                }
                case 3 -> { // Dislike the current post
                    PostService.getInstance().dislike(userID);
                    return this;
                }
                case 4 -> { // Add to a playlist
                    Post post = PostService.getInstance().getCurrentPost();
                    if (!(post instanceof Video)) {
                        System.out.println("You cannot add a " + post.getClass().getSimpleName() +" to a playlist; it needs to be a Video!");
                        return this;
                    }

                    System.out.println("Your playlists:");
                    List<Playlist> playlists = PlaylistService.getInstance().getAllFromUser(userID);
                    for (Playlist playlist: playlists) {
                        System.out.println(playlist);
                    }

                    System.out.print("\nChoose playlist id to add this video to: ");
                    Integer playlistID = States.sc.nextInt();

                    if (playlists.stream().noneMatch(playlist -> (Objects.equals(playlist.getID(), playlistID)))) {
                        System.out.println("The requested playlist id doesn't exist!");
                        return this;
                    }

                    PlaylistService.getInstance().addVideo(postID, playlistID);
                    return this;
                }
                case 5 -> { // Close the current post
                    PostService.getInstance().closePost();
                    return POST_MENU;
                }
                case 6 -> { // Show the comments
                    System.out.println("Comments:");

                    List<UserComment> comments = PostService.getInstance().getCurrentPost().getComments();
                    Stack<Integer> hierarchy = new Stack<>();

                    for (UserComment comment: comments) {
                        while ((!hierarchy.isEmpty()) && !Objects.equals(comment.getParentID(), hierarchy.peek())) {
                            hierarchy.pop();
                        }

                        System.out.println(comment.toString().indent(hierarchy.size()));
                        System.out.println();
                        hierarchy.push(comment.getID());
                    }
                    AuditService.getInstance().writeAction("User with id " + userID + " has accessed the comments of post with id " + postID + ".");

                    return this;
                }
                case 7 -> { // Add a comment
                    PostService.getInstance().addComment(States.sc, userID);
                    return this;
                }
                case 8 -> { // Reply to a comment
                    System.out.print("Input id of comment you are replying to: ");
                    Integer parentID = States.sc.nextInt();
                    States.sc.nextLine();

                    PostService.getInstance().addComment(States.sc, userID, parentID);
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
            Integer userID = UserService.getInstance().getLoggedUserID();

            switch(task) {
                case 1 -> { // Show all of your playlists
                    List<Playlist> playlists =  PlaylistService.getInstance().getAllFromUser(userID);

                    System.out.println("Your playlists:");
                    for (Playlist playlist: playlists) {
                        System.out.println(playlist);
                    }

                    return this;
                }
                case 2 -> { // Create a new playlist
                    PlaylistService.getInstance().add(States.sc, userID);
                    return this;
                }
                case 3 -> { // Open a playlist
                    System.out.print("Input playlist id to open: ");
                    Integer playlistID = States.sc.nextInt();

                    PlaylistService.getInstance().openPlaylist(playlistID);
                    if (PlaylistService.getInstance().getOpenPlaylist() == null) {
                        return this;
                    }

                    AuditService.getInstance().writeAction("User with id " + userID + " has opened playlist with id " + playlistID + ".");
                    return WATCHING_PLAYLIST;
                }
                case 4 -> { // Delete a playlist
                    System.out.print("Input playlist id to delete: ");
                    Integer playlistID = States.sc.nextInt();

                    PlaylistService.getInstance().remove(playlistID);
                    AuditService.getInstance().writeAction("User with id " + userID + " has removed playlist with id " + playlistID + ".");
                    return this;
                }
                case 5 -> { // Go back
                    PlaylistService.getInstance().closePlaylist();
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
            Integer playlistID = PlaylistService.getInstance().getOpenPlaylistID();
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
                        \t7. Change the name of the playlist;
                        \t8. Exit playlist;
                        \t9. Exit program.""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            Integer userID = UserService.getInstance().getLoggedUserID();
            Integer playlistID = PlaylistService.getInstance().getOpenPlaylistID();

            switch (task) {
                case 1 -> { // Show all videos
                    System.out.println("Videos:");
                    List<Video> videos = PlaylistService.getInstance().getOpenPlaylist().getVideos();

                    for (Video video: videos) {
                        System.out.println(video);
                    }

                    AuditService.getInstance().writeAction("User with id " + userID + " has requested a list of the videos in playlist with id " + playlistID + ".");
                    return this;
                }
                case 2 -> { // Switch ordering method
                    PlaylistService.getInstance().switchOrdering();

                    return this;
                }
                case 3 -> { // Go to previous video
                    PlaylistService.getInstance().previousVideo();

                    return this;
                }
                case 4 -> { // Go to next video
                    PlaylistService.getInstance().nextVideo();

                    return this;
                }
                case 5 -> { // Go to video by id
                    System.out.print("Input id position of video between 1 and " + PlaylistService.getInstance().getOpenPlaylist().getVideos().size() + ": ");
                    int position = States.sc.nextInt();
                    if (position < 1 || position > PlaylistService.getInstance().getOpenPlaylist().getVideos().size()) {
                        System.out.println("Wrong position index.");
                        return this;
                    }

                    PlaylistService.getInstance().setVideo(position - 1);
                    return this;
                }
                case 6 -> { // Remove video from playlist
                    PlaylistService.getInstance().removeCurrentVideo();
                    return this;
                }
                case 7 -> { // Change the name of the playlist
                    System.out.print("Input new name for playlist: ");
                    String newName = States.sc.nextLine();

                    String oldName = PlaylistService.getInstance().getOpenPlaylist().getName();
                    PlaylistService.getInstance().getOpenPlaylist().setName(newName);
                    if (!PlaylistService.getInstance().set(PlaylistService.getInstance().getOpenPlaylist())) {
                        PlaylistService.getInstance().getOpenPlaylist().setName(oldName);
                    } else {
                        AuditService.getInstance().writeAction("User with id " + userID + " changed the name of playlist with id " + playlistID + ".");
                    }

                    return this;
                }
                case 8 -> { // Exit playlist
                    return PLAYLIST_MENU;
                }
                case 9 -> { // Exit program
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
            int task;

            System.out.println("""
                        Input a task:
                        \t1. Show number of subscribers;
                        \t2. Show users you are subscribed to;
                        \t3. Show your posts;
                        \t4. Change a posts name;
                        \t5. Delete a post;
                        \t6. Show your history;
                        \t7. Change account details;
                        \t8. Delete your account;
                        \t9. Exit profile;
                        \t10. Exit program.""");
            task = States.sc.nextInt();
            States.sc.nextLine();

            return task;
        }

        @Override
        States performTask(int task) {
            Integer userID = UserService.getInstance().getLoggedUserID();
            switch (task) {
                case 1 -> { // Show number of subscribers
                    Integer numberOfSubscribers = UserService.getInstance().getNumberOfSubscribers();
                    if (numberOfSubscribers != null) {
                        System.out.println("Congrats! You currently have " + numberOfSubscribers + " subscribers!");
                    }

                    return this;
                }
                case 2 -> { // Show users you are subscribed to
                    List<Person> people = UserService.getInstance().getPeopleSubscribedTo();
                    if (people != null) {
                        System.out.println("You are currently subscribed to:");

                        for (Person person: people) {
                            System.out.println(person);
                        }
                    }

                    return this;
                }
                case 3 -> { // Show your posts
                    List<Post> posts = UserService.getInstance().getOwnPosts();
                    if (posts != null) {
                        System.out.println("You currently own the following posts:");

                        for (Post post: posts) {
                            System.out.println(post);
                        }
                        AuditService.getInstance().writeAction("User with id " + userID + " has requested a list of their own posts.");
                    }

                    return this;
                }
                case 4 -> { // Change a posts name
                    System.out.print("Input the id of the post whose title you want to change: ");
                    Integer postID = States.sc.nextInt();
                    States.sc.nextLine();

                    Post post = PostService.getInstance().get(postID);
                    if (post == null) {
                        return this;
                    }

                    if (!Objects.equals(post.getPosterID(), UserService.getInstance().getLoggedUserID())) {
                        System.out.println("This post does not belong to you!");
                        return this;
                    }

                    System.out.println("Input the new name of this post:");
                    System.out.println(post);
                    String oldName = post.getName();
                    String newName = States.sc.nextLine();
                    post.setName(newName);
                    if (!PostService.getInstance().set(post)) {
                        post.setName(oldName);
                    } else {
                        AuditService.getInstance().writeAction("User with id " + userID + " has modified the name of post with id " + postID + ".");
                    }

                    return this;
                }
                case 5 -> { // Delete a post
                    System.out.print("Input post id to delete: ");
                    Integer postID = States.sc.nextInt();
                    States.sc.nextLine();

                    Post post = PostService.getInstance().get(postID);
                    if (post != null) {
                        if (!Objects.equals(post.getPosterID(), UserService.getInstance().getLoggedUserID())) {
                            System.out.println("This post does not belong to you!");
                            return this;
                        }

                        PostService.getInstance().remove(postID);
                        AuditService.getInstance().writeAction("User with id " + userID + " has removed post with id " + postID + ".");
                    }

                    return this;
                }
                case 6 -> { // Show your history
                    List<Post> history = UserService.getInstance().getHistory();
                    if (history != null) {
                        System.out.println("Post history:");
                        for (Post post: history) {
                            System.out.println(post);
                        }
                    }

                    return this;
                }
                case 7 -> { // Change account details
                    UserService.getInstance().updateDetails(States.sc);
                    return this;
                }
                case 8 -> { // Delete your account
                    UserService.getInstance().deleteAccount();
                    return NOT_LOGGED;
                }
                case 9 -> { // Exit profile
                    return LOGGED_IN;
                }
                case 10 -> { // Exit program
                    return EXITED;
                }
                default -> {
                    System.out.println("Wrong task. Try again.");
                    return this;
                }
            }
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
    static Scanner sc;

    States() {
    }

    abstract int getTask();
    abstract States performTask(int task);

    static {
        States.sc = new Scanner(System.in);
    }
}
