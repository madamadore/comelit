package it.matteoavanzini.comelit.provider;

import android.arch.persistence.room.Room;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import it.matteoavanzini.comelit.database.CommentContract;
import it.matteoavanzini.comelit.database.CommentDatabase;
import it.matteoavanzini.comelit.database.PostDao;
import it.matteoavanzini.comelit.database.PostDatabase;

/**
 * Created by emme on 21/05/2018.
 */

public class PostProvider extends ContentProvider {

    public static final String AUTHORITY = "it.comelit.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final int POST_LIST = 1;
    private static final int POST_ID = 2;
    private static final int COMMENT_LIST = 3;
    private static final int COMMENT_ID = 4;

    // Defines a handle to the Room database
    private PostDatabase postDb;
    private CommentDatabase commentDb;

    // Defines a Data Access Object to perform the database operations
    private PostDao postDao;

    // Defines the database name
    public static final String DBNAME = "database-post";

    @Override
    public boolean onCreate() {
        postDb = Room.databaseBuilder(getContext(), PostDatabase.class, DBNAME).build();
        postDao = postDb.postDao();

        commentDb = new CommentDatabase(getContext());
        return true;
    }

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(PostProvider.AUTHORITY, "posts", POST_LIST);
        uriMatcher.addURI(PostProvider.AUTHORITY, "posts/#", POST_ID);
        uriMatcher.addURI(PostProvider.AUTHORITY, "comments", COMMENT_LIST);
        uriMatcher.addURI(PostProvider.AUTHORITY, "comments/#", COMMENT_ID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case POST_LIST:
                cursor = postDao.getCursorAllPost();
                break;
            case POST_ID:
                String lastPathSegment = uri.getLastPathSegment();
                int id = Integer.parseInt(lastPathSegment);
                cursor = postDao.loadPostById(id);
                break;
            case COMMENT_LIST:
                SQLiteDatabase db = commentDb.getReadableDatabase();
                cursor = db.query(CommentContract.CommentEntry.TABLE_NAME,
                                        projection, selection,
                                        selectionArgs, sortOrder, null, null);
                break;
            case COMMENT_ID:
                lastPathSegment = uri.getLastPathSegment();
                db = commentDb.getReadableDatabase();
                cursor = db.query(CommentContract.CommentEntry.TABLE_NAME,
                        projection,
                        CommentContract.CommentEntry._ID + "=?",
                        new String[] { lastPathSegment },
                        sortOrder, null, null);
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "it.comelit.provider/item.post";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            return itemUri;
        }
        throw new SQLException("Problem while inserting into uri: " + uri);
    }
}