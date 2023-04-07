package Post;

import java.util.Scanner;

public class Short extends UserPost {
    private String source;
    private double length;

    public Short() {
        super();
        this.source = "";
        this.length = 0.0;
    }

    public Short(int posterID) {
        super(posterID);
        this.source = "";
        this.length = 0.0;
    }

    public Short(int posterID, String name, String thumbnail, String source, double length) {
        super(posterID, name, thumbnail);
        this.source = source;
        this.length = length;
    }

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input short file: ");
        this.source = sc.next();

        System.out.print("Input short length: ");
        this.length = sc.nextDouble();
        sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Short file: " + this.source + "\n");
        ret += ("Short length: " + this.length + "\n");

        return ret;
    }
}
