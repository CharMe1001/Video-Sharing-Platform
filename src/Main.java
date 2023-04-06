import Services.PostService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PostService postService = new PostService();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Input a task (1 - add post, 2 - print post by id, 3 - print all posts, 4 - remove post by id, 5 - exit): ");
            int task = sc.nextInt();
            sc.nextLine();

            switch (task) {
                case 1: {
                    postService.read(sc);
                    break;
                }
                case 2: {
                    System.out.print("Input id of post: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.println(postService.getPostData(id));
                    break;
                }
                case 3: {
                    System.out.println(postService);
                    break;
                }
                case 4: {
                    System.out.print("Input id of post: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    postService.remove(id);
                    break;
                }
                case 5: {
                    return;
                }
                default: {
                    System.out.println("Wrong option. Try again.");
                }
            }

        }
    }
}