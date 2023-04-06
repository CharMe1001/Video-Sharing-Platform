package Post;

import java.util.Scanner;

public class Post {
    protected String name;
    protected String thumbnail;

    public Post() {
        this.name = "";
        this.thumbnail = "";
    }

    public Post(String name, String thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public void read(Scanner sc) {
        System.out.print("Input name: ");
        this.name = sc.nextLine();

        System.out.print("Input thumbnail file: ");
        this.thumbnail = sc.next();
    }

    public String toString() {
        String ret = "";
        ret += ("Name: " + this.name + "\n");
        if (thumbnail != null) {
            ret += ("Thumbnail: " + this.thumbnail + "\n");
        }

        return ret;
    }
}
