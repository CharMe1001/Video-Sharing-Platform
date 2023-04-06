package Post;

import java.util.Scanner;

public class CommunityPost extends UserPost {
    private String text;

    public CommunityPost() {
        super();
        this.text = "";
    }

    public CommunityPost(int poster_id, String name, String thumbnail, String text) {
        super(poster_id, name, thumbnail);
        this.text = text;
    }

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input community post text: ");
        this.text = sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Content: " + this.text + "\n");

        return ret;
    }
}