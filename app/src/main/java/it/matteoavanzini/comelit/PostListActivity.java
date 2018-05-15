package it.matteoavanzini.comelit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.matteoavanzini.comelit.adapter.SimplePostRecyclerViewAdapter;
import it.matteoavanzini.comelit.dummy.Post;
import it.matteoavanzini.comelit.dummy.PostContent;
import it.matteoavanzini.comelit.services.JsonPlaceHolderService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An activity representing a list of Posts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PostDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PostListActivity extends AppCompatActivity {

    private static final String TAG = PostListActivity.class.getName();
    private boolean mTwoPane;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.post_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.post_list);
        // assert recyclerView != null;
        setupRecyclerView(recyclerView);

        // executeAsyncTask();
        downloadData();
    }

    @Override
    public void onNewIntent(Intent intent) {
        // gestion
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimplePostRecyclerViewAdapter(this, PostContent.ITEMS, mTwoPane);
        recyclerView.setAdapter(mAdapter);
    }

    private void downloadData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ProgressDialog progressDialog = getProgressDialog();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        JsonPlaceHolderService service = retrofit.create(JsonPlaceHolderService.class);
        Call<List<Post>> call = service.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postList = response.body();
                PostContent.ITEMS.clear();
                PostContent.ITEMS.addAll(postList);
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void executeAsyncTask() {
        PostTask task = new PostTask();
        task.execute("http://jsonplaceholder.typicode.com/posts");
    }

    private ProgressDialog getProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(PostListActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait....");
        progressDialog.setTitle("Loading posts");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        return progressDialog;
    }

    private class PostTask extends AsyncTask<String, Integer, List<Post>> {

        private ProgressDialog progressDialog;
        private final String TAG = PostTask.class.getName();

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = getProgressDialog();
            // show it
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<Post> result) {
            PostContent.ITEMS.clear();
            PostContent.ITEMS.addAll(result);
            mAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

        @Override
        protected List<Post> doInBackground(String... params) {
            String responseStr = "";
            InputStream input = null;
            HttpURLConnection connection = null;
            List<Post> postList = new ArrayList<>();

            try {

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.connect();

                input = connection.getInputStream();

                int content = 0;
                int partialDownloaded = 0;
                while ((content = input.read()) != -1) {

                    publishProgress(partialDownloaded);
                    char readed = (char) content;
                    responseStr += readed;

                    if (readed == '}') {
                        partialDownloaded++;
                    }
                }

                postList = parseJson(responseStr);

            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            return postList;
        }

        private List<Post> parseJson(String jsonArray) {
            Type listType = new TypeToken<ArrayList<Post>>(){}.getType();
            List<Post> postList = new Gson().fromJson(jsonArray, listType);
            return postList;
        }
    }
}
