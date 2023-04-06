package Services;

import User.Comment;

public class CommentService extends Service<Comment> {
    public CommentService() {
        super();
    }

    public int sendComment(Comment comment) {
        System.out.println("Added a comment.");
        return this.add(comment);
    }
}
