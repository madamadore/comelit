package it.matteoavanzini.comelit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import it.matteoavanzini.comelit.adapter.PostItemRecyclerViewAdapter;
import it.matteoavanzini.comelit.dummy.Post;

/**
 * An activity representing a list of Posts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PostDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PostListActivity extends AppCompatActivity {

    private boolean mTwoPane;

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

        FetchTask asyncTask = new FetchTask();
        asyncTask.execute("https://jsonplaceholder.typicode.com/posts");
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Post> posts) {
        RecyclerView.Adapter myAdapter = new PostItemRecyclerViewAdapter(this,
                posts, mTwoPane);
        recyclerView.setAdapter(myAdapter);
    }

    private class FetchTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String responseStr = "";
            InputStream input = null;
            HttpURLConnection connection = null;

            try {
                for (String sUrl : params) {
                    URL url = new URL(sUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.connect();

                    input = connection.getInputStream();

                    int content = 0;
                    while ((content = input.read()) != -1) {
                        responseStr += (char) content;
                    }
                }
            } catch (UnsupportedEncodingException e) {

            } catch (IOException e) {

            }
            return responseStr;
        }

        @Override
        protected void onPostExecute(String jsonArray) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Post>>(){}.getType();
            List<Post> postList = new Gson().fromJson(jsonArray, listType);

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.post_list);
            // assert recyclerView != null;
            setupRecyclerView(recyclerView, postList);
        }
    }

}
