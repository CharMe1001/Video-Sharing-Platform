package Entities.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Poll extends UserPost {
    private String text;
    private List<String> options;

    public Poll() {
        super(null, "", null);
        this.text = "";
        this.options = new ArrayList<>();
    }

    public Poll(Integer posterID) {
        super(posterID);
        this.text = "";
        this.options = new ArrayList<>();
    }

    public Poll(Integer posterID, String name, String text, List<String> options) {
        super(posterID, name, null);
        this.text = text;
        this.options = options;
    }

    @Override
    public void read(Scanner sc) {
        System.out.print("Input name: ");
        this.name = sc.nextLine();

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

    public String getColumns() {
        return super.getColumns() + ", text";
    }

    public String toSQLInsertOptions() {
        return "INSERT INTO OPTIONS(text, pollID) VALUES" + this.options.stream().<String>map(option -> "('" + option + "', " + this.id + ")").collect(Collectors.joining(", "));
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + ", '" + this.text + "')";
    }

    public String getSQLUpdate(String className) {


        return super.getSQLUpdate(className) + ", text = '" + this.text + "', options = OPT_TAB(" + this.options.stream().<String>map(option -> "'" + option + "'").collect(Collectors.joining(", ")) + ")";
    }

    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        try {
            this.text = src.getString("text");
        } catch (SQLException sqlE) {
            System.out.println("Error getting text from poll with id = " + this.id + "!");
            throw sqlE;
        }
    }

    public void getOptionsFromResult(ResultSet options) throws SQLException {
        while (true) {
            try {
                if (!options.next()) {
                    break;
                }
            } catch (SQLException sqlE) {
                System.out.println("Error retrieving next poll option for poll with id = " + this.id + "!");
                throw sqlE;
            }

            try {
                this.options.add(options.getString("text"));
            } catch (SQLException sqlE) {
                System.out.println("Error getting text for poll with id = " + this.id + "!");
                throw sqlE;
            }
        }
    }
}
