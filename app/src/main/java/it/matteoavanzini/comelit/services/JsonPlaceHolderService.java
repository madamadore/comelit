package it.matteoavanzini.comelit.services;

import java.util.List;

import it.matteoavanzini.comelit.dummy.Post;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by emme on 15/05/2018.
 */
public interface JsonPlaceHolderService {
    @GET("/posts")
    Call<List<Post>> getPosts();
}
