package it.matteoavanzini.comelit;


import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import it.matteoavanzini.comelit.adapter.SimplePostRecyclerViewAdapter;
import it.matteoavanzini.comelit.database.PostDatabase;
import it.matteoavanzini.comelit.model.Post;
import it.matteoavanzini.comelit.services.LoadDatabaseTask;
import it.matteoavanzini.comelit.services.PostDownloadService;

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
    List<Post> mPost = new ArrayList<>();
    PostDatabase postDb;

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

        startDownloadService();

        postDb = Room.databaseBuilder(this,
                PostDatabase.class, "database-post").build();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.post_list);
        // assert recyclerView != null;
        setupRecyclerView(recyclerView);

        loadDataFromDatabaseAsync();
    }

    private void startDownloadService() {
        Intent intent = new Intent(this, PostDownloadService.class);
        intent.setAction(PostDownloadService.ACTION_DOWNLOAD_POST);
        startService(intent);

        intent.setAction(PostDownloadService.ACTION_DOWNLOAD_COMMENT);
        startService(intent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        // gestion
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimplePostRecyclerViewAdapter(this, mPost, mTwoPane);
        recyclerView.setAdapter(mAdapter);
    }

    private void loadDataFromDatabaseAsync() {
        LoadPostTask task = new LoadPostTask();
        task.execute();
    }

    private class LoadPostTask extends LoadDatabaseTask<Post> {

        LoadPostTask() {
            super(PostListActivity.this);
        }

        @Override
        protected List<Post> doInBackground(Void... params) {
            List<Post> posts = postDb.postDao().getAll();
            Log.d(TAG, "Nel db ci sono " + posts.size() + " post");
            return posts;
        }

        @Override
        public void onTaskEnd(List<Post> result) {
            mPost.addAll(result);
            mAdapter.notifyDataSetChanged();
        }
    }


}
