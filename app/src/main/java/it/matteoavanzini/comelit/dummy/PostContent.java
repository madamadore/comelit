package it.matteoavanzini.comelit.dummy;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PostContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Post> ITEMS = new ArrayList<>();

    static {
        // Add some sample items.
        for (int i = 1; i <= 25; i++) {
            // addPost(createPost(i));
        }
    }

    private static void addPost(Post item) {
        ITEMS.add(item);
    }

    private static Post createPost(int position) {
        Post post = new Post();
        post.setId(position);
        post.setTitle("Post " + position);
        post.setBody(makeDetails(position));
        return post;
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Post: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

}
