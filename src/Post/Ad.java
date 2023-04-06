package Post;

import java.util.Scanner;

public class Ad extends Post {
    private String companyName;
    private String link;
    private String text;

    public Ad() {
        super();
        this.companyName = "";
        this.link = "";
        this.text = "";
    }

    public Ad(String name, String thumbnail, String companyName, String link, String text) {
        super(name, thumbnail);
        this.companyName = companyName;
        this.link = link;
        this.text = text;
    }

    @Override
    public void read(Scanner sc) {
        super.read(sc);

        System.out.print("Input company name: ");
        this.companyName = sc.nextLine();

        System.out.print("Input the link of the ad: ");
        this.link = sc.next();

        System.out.print("Input the ad text: ");
        this.text = sc.nextLine();
    }

    public String toString() {
        String ret = super.toString();
        ret += ("Company: " + this.companyName + "\n");
        ret += ("Link to ad: " + this.link + "\n");
        ret += ("Content: " + this.text + "\n");

        return ret;
    }
}
