package Services;

import Entities.User.UserComment;

import java.util.List;

public class CommentService extends Service<UserComment> {
    private static CommentService instance = null;
    public static CommentService getInstance() {
        if (CommentService.instance == null) {
            CommentService.instance = new CommentService();
        }

        return CommentService.instance;
    }

    private CommentService() {
        super();
    }

    public int sendComment(UserComment userComment) {
        System.out.println("Added a comment.");
        return 0;// this.add(comment);
    }

    public String getComments(List<Integer> commentIDs) {
        StringBuilder ret = new StringBuilder("Comments:\n");
        for (int comment:commentIDs) {
            ret.append("------------").append(comment).append("------------\n");
            //ret.append(this.itemHashMap.get(comment).toString());
        }

        return ret.toString();
    }
}
