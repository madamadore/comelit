package it.matteoavanzini.comelit.services;

import java.util.List;

import it.matteoavanzini.comelit.model.Comment;
import it.matteoavanzini.comelit.model.Post;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Created by emme on 15/05/2018.
 */
public interface JsonPlaceHolderService {
    @GET("/posts")
    Call<List<Post>> getPosts();

    @GET("/comments")
    Call<List<Comment>> getAllComments();

    @GET("/comments?postId={postId}")
    Call<List<Comment>> getComments(@Path("postId") int postId);
}

