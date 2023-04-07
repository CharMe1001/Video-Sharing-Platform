package Post;

import java.util.*;

public class UserPost extends Post {
    protected int posterID;
    protected Set<Integer> likes;
    protected Set<Integer> dislikes;
    protected List<Integer> comments;

    public UserPost() {
        super();
        this.posterID = 0;
        this.likes = new HashSet<>();
        this.dislikes = new HashSet<>();
        this.comments = new ArrayList<>();
    }

    public UserPost(int posterID) {
        super();
        this.posterID = posterID;
        this.likes = new HashSet<>();
        this.dislikes = new HashSet<>();
        this.comments = new ArrayList<>();
    }

    public UserPost(int posterID, String name, String thumbnail) {
        super(name, thumbnail);
        this.posterID = posterID;
        this.likes = new HashSet<>();
        this.dislikes = new HashSet<>();
        this.comments = new ArrayList<>();
    }

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        this.likes = new HashSet<>();
        this.dislikes = new HashSet<>();
        this.comments = new ArrayList<>();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Poster id: " + this.posterID + "\n");
        ret += ("Number of likes: " + this.likes.size() + "\n");
        ret += ("Number of dislikes: " + this.dislikes.size() + "\n");

        return ret;
    }

    public void addComment(int commentID) {
        this.comments.add(commentID);
    }

    public void addLike(int userID) {
        this.dislikes.remove(userID);

        if (this.likes.contains(userID)) {
            this.likes.remove(userID);
        } else {
            this.likes.add(userID);
        }
    }

    public void addDislike(int userID) {
        this.likes.remove(userID);

        if (this.dislikes.contains(userID)) {
            this.dislikes.remove(userID);
        } else {
            this.dislikes.add(userID);
        }
    }

    public List<Integer> getComments() {
        return new ArrayList<>(this.comments);
    }

    public int getPosterID() {
        return this.posterID;
    }
}
