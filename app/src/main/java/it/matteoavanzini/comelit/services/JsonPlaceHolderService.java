package it.matteoavanzini.comelit.services;

import java.util.List;

import it.matteoavanzini.comelit.dummy.Post;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonPlaceHolderService {
    @GET("posts")
    Call<List<Post>> listPosts();

    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") String id);
}