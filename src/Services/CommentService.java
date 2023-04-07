package Services;

import User.Comment;

import java.util.List;

public class CommentService extends Service<Comment> {
    public CommentService() {
        super();
    }

    public int sendComment(Comment comment) {
        System.out.println("Added a comment.");
        return this.add(comment);
    }

    public String getComments(List<Integer> commentIDs) {
        StringBuilder ret = new StringBuilder("Comments:\n");
        for (int comment:commentIDs) {
            ret.append("------------").append(comment).append("------------\n");
            ret.append(this.itemHashMap.get(comment).toString());
        }

        return ret.toString();
    }
}
