package Post;

import java.util.Scanner;

public class Video extends UserPost {
    private String source;
    private double length;

    public Video() {
        super();
        this.source = "";
        this.length = 0.0;
    }

    public Video(int poster_id, String name, String thumbnail, String source, double length) {
        super(poster_id, name, thumbnail);
        this.source = source;
        this.length = length;
    }

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input video file: ");
        this.source = sc.next();

        System.out.print("Input video length: ");
        this.length = sc.nextDouble();
        sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Video file: " + this.source + "\n");
        ret += ("Video length: " + this.length + "\n");

        return ret;
    }
}
