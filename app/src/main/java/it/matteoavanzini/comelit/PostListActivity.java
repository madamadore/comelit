package it.matteoavanzini.comelit;


import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import it.matteoavanzini.comelit.adapter.RecyclerViewAdapter;
import it.matteoavanzini.comelit.adapter.SimplePostRecyclerViewAdapter;
import it.matteoavanzini.comelit.database.PostDatabase;
import it.matteoavanzini.comelit.fragment.PostDetailFragment;
import it.matteoavanzini.comelit.model.Post;
import it.matteoavanzini.comelit.provider.PostProvider;
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
public class PostListActivity extends AppCompatActivity
        implements RecyclerViewAdapter.OnSimpleItemRecyclerListener {

    private static final String PREF_NAME = "post-preferences";
    private static final String KEY_ALARM_SET = "alarm";
    private static final String TAG = PostListActivity.class.getName();
    private boolean mTwoPane;
    private boolean isPickActivity;
    private SimplePostRecyclerViewAdapter mAdapter;
    List<Post> mPost = new ArrayList<>();
    PostDatabase postDb;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PostDownloadService.ACTION_POST_DOWNLOADED)) {
                loadDataFromDatabaseAsync();
            }
        }
    };

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
        setIsPickActivity(getIntent());

        startDownloadService();

        postDb = Room.databaseBuilder(this,
                PostDatabase.class, "database-post").build();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.post_list);
        // assert recyclerView != null;
        setupRecyclerView(recyclerView);

        loadDataFromDatabaseAsync();

        setAlarmDownloadPost();
    }

    private void setIsPickActivity(Intent intent) {
        isPickActivity = false;
        if (intent.getAction().equals(Intent.ACTION_PICK)) {
            isPickActivity = true;
        }
    }

    private void setAlarmDownloadPost() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean alarmSet = preferences.getBoolean(KEY_ALARM_SET, false);
        if (!alarmSet) {
            Intent setAlarmIntent = new Intent("it.comelit.receiver.SET_ALARM");
            sendBroadcast(setAlarmIntent);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_ALARM_SET, true);
            editor.commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PostDownloadService.ACTION_POST_DOWNLOADED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private void startDownloadService() {
        Intent downloadPost = new Intent(this, PostDownloadService.class);
        downloadPost.setAction(PostDownloadService.ACTION_DOWNLOAD_POST);
        startService(downloadPost);

        Intent downloadComment = new Intent(this, PostDownloadService.class);
        downloadComment.setAction(PostDownloadService.ACTION_DOWNLOAD_COMMENT);
        startService(downloadComment);
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIsPickActivity(intent);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimplePostRecyclerViewAdapter(this, mPost);
        recyclerView.setAdapter(mAdapter);
    }

    private void loadDataFromDatabaseAsync() {
        LoadPostTask task = new LoadPostTask();
        task.execute();
    }

    private void onActionPickEnd(Post item) {
        Intent resultData = new Intent();
        int postId = item.getId();
        Uri uri = PostProvider.CONTENT_URI
                .buildUpon()
                .appendPath("posts")
                .appendPath(Integer.toString(postId))
                .build();
        resultData.setData(uri);
        setResult(RESULT_OK, resultData);
        finish();
    }

    private void onLoadFragmentDetail(Post item) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(PostDetailFragment.ARG_ITEM_ID, item);
        PostDetailFragment fragment = new PostDetailFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.post_detail_container, fragment)
                .commit();
    }

    private void onStartDetailActivity(Post item) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(PostDetailFragment.ARG_ITEM_ID, item);
        startActivity(intent);
    }

    @Override
    public void onSimpleRecyclerItemSelected(Parcelable item) {
        if (isPickActivity) {
            onActionPickEnd((Post) item);
        } else {
            if (mTwoPane) {
                onLoadFragmentDetail((Post) item);
            } else {
                onStartDetailActivity((Post) item);
            }
        }
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
