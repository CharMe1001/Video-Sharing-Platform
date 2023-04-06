package Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserPost extends Post {
    protected int poster_id;
    protected int likes;
    protected List<Integer> comments;

    public UserPost() {
        super();
        this.poster_id = 0;
        this.likes = 0;
        this.comments = new ArrayList<>();
    }

    public UserPost(int poster_id, String name, String thumbnail) {
        super(name, thumbnail);
        this.poster_id = poster_id;
        this.likes = 0;
        this.comments = new ArrayList<>();
    }

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input the id of the poster: ");
        this.poster_id = sc.nextInt();
        sc.nextLine();

        this.likes = 0;
        this.comments = new ArrayList<>();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Poster id: " + this.poster_id + "\n");
        ret += ("Number of likes: " + this.likes + "\n");

        return ret;
    }

    public void addComment(int commentID) {
        this.comments.add(commentID);
    }
}
