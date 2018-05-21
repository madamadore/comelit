package it.matteoavanzini.comelit;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import it.matteoavanzini.comelit.adapter.CommentRecyclerViewAdapter;
import it.matteoavanzini.comelit.adapter.RecyclerViewAdapter;
import it.matteoavanzini.comelit.database.CommentDatabase;
import it.matteoavanzini.comelit.database.PostDatabase;
import it.matteoavanzini.comelit.fragment.PostDetailFragment;
import it.matteoavanzini.comelit.model.Comment;
import it.matteoavanzini.comelit.model.Post;


/**
 * An activity representing a single Post detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PostListActivity}.
 */
public class PostDetailActivity extends AppCompatActivity
    implements RecyclerViewAdapter.OnSimpleItemRecyclerListener {

    private static final int EDIT_POST_CODE = 42;
    private Post mPost;
    PostDatabase postDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postDb = Room.databaseBuilder(this,
                PostDatabase.class, "database-post").build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Post post = getVisiblePost();
                editPost(mPost);
            }
        });

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            mPost = getIntent().getParcelableExtra(PostDetailFragment.ARG_ITEM_ID);
            PostDetailFragment fragment = getPostDetailFragment(mPost);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.post_detail_container, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.post_list);
        setupRecyclerView(recyclerView);
    }

    private PostDetailFragment getPostDetailFragment(Post post) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(PostDetailFragment.ARG_ITEM_ID, post);
        PostDetailFragment fragment = new PostDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    private List<Comment> getComments(int postId) {
        CommentDatabase db = new CommentDatabase(this);
        List<Comment> comments = db.getComments(postId);
        return comments;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        List<Comment> items = getComments(mPost.getId());

        CommentRecyclerViewAdapter adapter =
                new CommentRecyclerViewAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }

    public void editPost(Post post) {
        Intent intent = new Intent(this, PostEditActivity.class);
        intent.putExtra(PostEditActivity.POST_ARG, post);

        startActivityForResult(intent, EDIT_POST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == EDIT_POST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {

                Post post = resultData.getParcelableExtra(PostEditActivity.POST_ARG);
                postDb.postDao().update(post);

                PostDetailFragment fragment = getPostDetailFragment(post);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.post_detail_container, fragment)
                        .commitAllowingStateLoss();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home_menu:
                navigateUpTo(new Intent(this, PostListActivity.class));
                return true;

            case R.id.share_menu:
                // TODO: Il post deve essere letto dal Fragment corrente e non
                // dal metodo getIntent()
                Post post = getVisiblePost();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, post.getBody());
                intent.setType("text/plain");

                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_detail_menu, menu);
        return true;
    }

    @Override
    public void onSimpleRecyclerItemSelected(Parcelable post) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        // mettere a video AlertDialog
    }

    @Deprecated
    private Post getVisiblePost() {
        int numberOfFragmentInStack = getSupportFragmentManager()
                .getBackStackEntryCount();
        Fragment lastFragment = getSupportFragmentManager()
                .getFragments()
                .get(numberOfFragmentInStack);
        Post post = lastFragment.getArguments()
                .getParcelable(PostDetailFragment.ARG_ITEM_ID);
        return post;
    }

}
