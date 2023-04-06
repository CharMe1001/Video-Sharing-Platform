package Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Poll extends UserPost {
    private String text;
    private List<String> options;

    public Poll() {
        super(0, "", null);
        this.text = "";
        this.options = new ArrayList<>();
    }

    public Poll(int poster_id, String name, String text, List<String> options) {
        super(poster_id, name, null);
        this.text = text;
        this.options = options;
    }

    @Override
    public void read(Scanner sc) {
        System.out.print("Input name: ");
        this.name = sc.nextLine();

        System.out.print("Input the id of the poster: ");
        this.poster_id = sc.nextInt();
        sc.nextLine();

        this.likes = 0;
        this.comments = new ArrayList<>();

        System.out.print("Input poll text: ");
        this.text = sc.nextLine();

        int cntOptions;
        System.out.print("Input number of poll options: ");
        cntOptions = sc.nextInt();
        sc.nextLine();
        this.options = new ArrayList<>();

        for (int i = 1; i <= cntOptions; ++i) {
            System.out.print("Input option number " + i + ": ");
            this.options.add(sc.nextLine());
        }
    }

    public String toString() {
        StringBuilder ret = new StringBuilder(super.toString());
        ret.append("Content: ").append(this.text).append("\n");
        ret.append("Options:\n");
        for (int i = 1; i <= this.options.size(); ++i) {
            ret.append(i).append(". ").append(this.options.get(i - 1)).append("\n");
        }

        return ret.toString();
    }
}
