package it.matteoavanzini.comelit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import it.matteoavanzini.comelit.dummy.Post;

public class PostEditActivity extends AppCompatActivity {

    public static final String POST_ARG = "post";
    private Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePost();
            }
        });

        setTextToEdit(getIntent());
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
