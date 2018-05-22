package it.matteoavanzini.comelit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import it.matteoavanzini.comelit.model.Comment;

/**
 * Created by emme on 21/05/2018.
 */

public class CommentDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PostComments.db";

    public CommentDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CommentContract.SQL_CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(CommentContract.SQL_DELETE_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public List<Comment> getComments(int postId) {
        List<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(CommentContract.CommentEntry.TABLE_NAME, null,
                CommentContract.CommentEntry.COLUMN_NAME_POST_ID + "=?",
                new String[] { Integer.toString(postId) },
                null, null, null);

        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex(CommentContract.CommentEntry._ID));
            String name = c.getString(c.getColumnIndex(CommentContract.CommentEntry.COLUMN_NAME_NAME));
            String body = c.getString(c.getColumnIndex(CommentContract.CommentEntry.COLUMN_NAME_BODY));
            String email = c.getString(c.getColumnIndex(CommentContract.CommentEntry.COLUMN_NAME_EMAIL));

            Comment comment = new Comment();
            comment.setId(id);
            comment.setPostId(postId);
            comment.setBody(body);
            comment.setEmail(email);
            comment.setName(name);

            comments.add(comment);
        }
        db.close();
        return comments;
    }

    public void insert(Comment... comments) {
        List<Comment> commentList = Arrays.asList(comments);
        insert(commentList);
    }

    public void insert(List<Comment> comments) {
        SQLiteDatabase db = getWritableDatabase();
        Iterator<Comment> commentIterator = comments.iterator();
        while (commentIterator.hasNext()) {
            Comment comment = commentIterator.next();

            ContentValues values = new ContentValues();
            values.put(CommentContract.CommentEntry.COLUMN_NAME_POST_ID, comment.getPostId());
            values.put(CommentContract.CommentEntry.COLUMN_NAME_NAME, comment.getName());
            values.put(CommentContract.CommentEntry.COLUMN_NAME_BODY, comment.getBody());
            values.put(CommentContract.CommentEntry.COLUMN_NAME_EMAIL, comment.getEmail());

            db.insert(CommentContract.CommentEntry.TABLE_NAME, null, values);
        }
        db.close();
    }

    public void update(Comment comment) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CommentContract.CommentEntry.COLUMN_NAME_POST_ID, comment.getPostId());
        values.put(CommentContract.CommentEntry.COLUMN_NAME_NAME, comment.getName());
        values.put(CommentContract.CommentEntry.COLUMN_NAME_BODY, comment.getBody());
        values.put(CommentContract.CommentEntry.COLUMN_NAME_EMAIL, comment.getEmail());

        db.update(CommentContract.CommentEntry.TABLE_NAME, values,
                CommentContract.CommentEntry._ID+"=?",
                new String[] { Integer.toString(comment.getId()) });

        db.close();
    }

    public void truncate() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CommentContract.CommentEntry.TABLE_NAME, null, null);
        db.close();
    }
}
