package Services;

import Post.*;
import Post.Short;

import java.util.Scanner;

public class PostService extends Service<Post> {
    public PostService() {
        super();
    }

    public String getPostData(int id) {
        Post post = this.get(id);
        if (post == null) {
            return "Post with id " + id + " doesn't exist.";
        }

        return post.toString();
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("Posts:\n");

        for (int id: this.itemHashMap.keySet()) {
            ret.append("---------------").append(id).append("---------------\n").append(this.getPostData(id));
        }

        return ret.toString();
    }

    public void read(Scanner sc) {
        Post post;

        int type = 6;
        while (true) {
            System.out.print("Input type of post (1 - Ad, 2 - Poll, 3 - Community Post, 4 - Short, 5 - Video): ");
            type = sc.nextInt();
            sc.nextLine();

            if (type < 1 || type > 5) {
                System.out.println("Wrong type. Try again.");
                continue;
            }

            break;
        }

        switch (type) {
            case 1: {
                post = new Ad();
                break;
            }
            case 2: {
                post = new Poll();
                break;
            }
            case 3: {
                post = new CommunityPost();
                break;
            }
            case 4: {
                post = new Short();
                break;
            }
            default: {
                post = new Video();
            }
        }

        post.read(sc);

        this.add(post);
    }
}
