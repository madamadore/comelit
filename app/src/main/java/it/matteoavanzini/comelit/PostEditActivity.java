package it.matteoavanzini.comelit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import it.matteoavanzini.comelit.model.Post;

public class PostEditActivity extends AppCompatActivity {

    public static final String POST_ARG = "post";
    private Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTextToEdit(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_edit_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save_menu:
                savePost();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTextToEdit(Intent intent) {
        if (intent.getExtras().containsKey(POST_ARG)) {
            mPost = intent.getParcelableExtra(POST_ARG);
            EditText editText = findViewById(R.id.edit_post);
            editText.setText(mPost.getBody());
        }
    }

    private void savePost() {
        if (mPost != null) {
            EditText editText = findViewById(R.id.edit_post);
            String newBody = editText.getText().toString();
            mPost.setBody(newBody);

            Intent resultData = new Intent();
            resultData.putExtra(POST_ARG, mPost);
            setResult(Activity.RESULT_OK, resultData);

            finish();
        } else {
            // snackbar che indica l'errore
        }
    }

}
