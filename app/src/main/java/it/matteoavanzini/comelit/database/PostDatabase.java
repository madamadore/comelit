package it.matteoavanzini.comelit.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import it.matteoavanzini.comelit.model.Post;

/**
 * Created by emme on 21/05/2018.
 */

@Database(entities = {Post.class}, version = 1)
public abstract class PostDatabase extends RoomDatabase {
    public abstract PostDao postDao();
}
