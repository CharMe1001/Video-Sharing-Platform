package Services;

import Post.*;
import Post.Short;

import java.util.List;
import java.util.Scanner;

public class PostService extends Service<Post> {
    private int currentPost;

    public PostService() {
        super();
    }

    public int getCurrentPostID() {
        return this.currentPost;
    }

    private Post getCurrentPost() {
        if (this.currentPost == -1) {
            return null;
        }

        return this.get(this.currentPost);
    }

    public String getPostName(int id) {
        Post post = this.get(id);
        if (post == null) {
            return "Post with id " + id + " doesn't exist.";
        }

        return post.getName();
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("Posts:\n");

        for (int id: this.itemHashMap.keySet()) {
            ret.append("---------------").append(id).append("---------------\n").append(this.getPostName(id));
        }

        return ret.toString();
    }

    public int read(Scanner sc, int userID) {
        Post post;

        int type;
        while (true) {
            System.out.print("Input type of post (1 - Ad, 2 - Poll, 3 - Community Post, 4 - Short, 5 - Video): ");
            type = sc.nextInt();
            sc.nextLine();

            if (type < 1 || type > 5) {
                System.out.println("Wrong type. Try again.");
                continue;
            }

            break;
        }

        switch (type) {
            case 1: {
                post = new Ad();
                break;
            }
            case 2: {
                post = new Poll(userID);
                break;
            }
            case 3: {
                post = new CommunityPost(userID);
                break;
            }
            case 4: {
                post = new Short(userID);
                break;
            }
            default: {
                post = new Video(userID);
            }
        }

        post.read(sc);

        return this.add(post);
    }

    public void showPosts(List<Integer> postIDs) {
        if (postIDs == null) {
            return;
        }

        for (int postID:postIDs) {
            Post post = this.get(postID);

            System.out.println("--------------" + postID + "--------------\n" + post.getName());
        }
    }

    public boolean openPost(int postID) {
        Post post = this.get(postID);
        if (post == null) {
            System.out.println("The post with id " + postID + " doesn't exist.");
            return false;
        }

        System.out.println("-------------" + postID + "-------------");
        System.out.println(post);
        this.currentPost = postID;

        return true;
    }

    public void closePost() {
        if (this.currentPost != -1) {
            System.out.println("Closed post with id " + this.currentPost + ".");
        }
        this.currentPost = -1;
    }

    public void addComment(int commentID) {
        Post post = this.getCurrentPost();
        if (!(post instanceof UserPost)) {
            System.out.println("No post that you can comment on is currently opened.");
            return;
        }

        ((UserPost) post).addComment(commentID);
    }

    public List<Integer> getCommentIDs() {
        Post post = this.getCurrentPost();
        if (!(post instanceof  UserPost)) {
            return null;
        }

        return ((UserPost) post).getComments();
    }

    public int getPosterID(int postID) {
        Post post = this.get(postID);
        if (!(post instanceof UserPost)) {
            System.out.println("No post that you can comment on is currently opened.");
            return -1;
        }

        return ((UserPost) post).getPosterID();
    }

    public void addLike(int userID) {
        Post post = this.getCurrentPost();
        if (!(post instanceof UserPost)) {
            System.out.println("No post that you can like is currently opened.");
            return;
        }

        ((UserPost) post).addLike(userID);
    }

    public void addDislike(int userID) {
        Post post = this.getCurrentPost();
        if (!(post instanceof UserPost)) {
            System.out.println("No post that you can dislike is currently opened.");
            return;
        }

        ((UserPost) post).addDislike(userID);
    }
}
