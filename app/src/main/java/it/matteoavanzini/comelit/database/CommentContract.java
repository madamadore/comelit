package it.matteoavanzini.comelit.database;

import android.provider.BaseColumns;

/**
 * Created by emme on 21/05/2018.
 */

public class CommentContract {

    private CommentContract() {}

    /* Inner class that defines the table contents */
    public static class CommentEntry implements BaseColumns {
        public static final String TABLE_NAME = "comments";
        public static final String COLUMN_NAME_POST_ID = "post_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_BODY = "body";
    }

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + CommentEntry.TABLE_NAME + " (" +
                    CommentEntry._ID + " INTEGER PRIMARY KEY," +
                    CommentEntry.COLUMN_NAME_POST_ID + " INTEGER," +
                    CommentEntry.COLUMN_NAME_NAME + " TEXT," +
                    CommentEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    CommentEntry.COLUMN_NAME_BODY+ " TEXT)";

    static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + CommentEntry.TABLE_NAME;
}
