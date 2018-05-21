package it.matteoavanzini.comelit.services;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import it.matteoavanzini.comelit.database.CommentDatabase;
import it.matteoavanzini.comelit.database.PostDatabase;
import it.matteoavanzini.comelit.model.Comment;
import it.matteoavanzini.comelit.model.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by emme on 21/05/2018.
 */

public class PostDownloadService extends IntentService {

    public final static String ACTION_DOWNLOAD_POST = "it.matteoavanzini.comelit.action.DOWNLOAD_POST";
    public final static String ACTION_DOWNLOAD_COMMENT = "it.matteoavanzini.comelit.action.DOWNLOAD_COMMENT";
    private static final String TAG = PostDownloadService.class.getName();

    private Retrofit mRetrofit;
    PostDatabase postDb;

    public PostDownloadService() {
        super("post_download");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        postDb = Room.databaseBuilder(this,
                PostDatabase.class, "database-post").build();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_DOWNLOAD_COMMENT)) {
            downloadComments();
        } else if (action.equals(ACTION_DOWNLOAD_POST)) {
            downloadPosts();
        }
    }

    private void downloadComments() {
        Log.d(TAG, "Start download comments");
        JsonPlaceHolderService service = mRetrofit.create(JsonPlaceHolderService.class);
        Call<List<Comment>> callComments = service.getAllComments();
        callComments.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                List<Comment> comments = response.body();
                CommentDatabase db = new CommentDatabase(PostDownloadService.this);
                db.truncate();
                db.insert(comments);
                Log.d(TAG, "End download comments");
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
            }
        });
    }

    private void downloadPosts() {
        Log.d(TAG, "Start download posts");
        JsonPlaceHolderService service = mRetrofit.create(JsonPlaceHolderService.class);
        Call<List<Post>> call = service.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postList = response.body();
                Post[] postArray = new Post[postList.size()];
                for (int i =0; i<postList.size(); i++) {
                    postArray[i] = postList.get(i);
                }

                DatabaseTask task = new DatabaseTask();
                task.execute(postArray);

                Log.d(TAG, "End download posts");
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
            }
        });
    }

    private class DatabaseTask extends AsyncTask<Post, Void, Void> {

        @Override
        protected Void doInBackground(Post... params) {
            postDb.postDao().truncate();
            postDb.postDao().insertAll(params);
            Log.d(TAG, "Fine inserimento Post in db");
            List<Post> posts = postDb.postDao().getAll();
            Log.d(TAG, "Nel db ci sono " + posts.size() + " post");
            return null;
        }
    }
}
