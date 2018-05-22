package it.matteoavanzini.comelit.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

import it.matteoavanzini.comelit.model.Post;

@Dao
public interface PostDao {
    @Query("SELECT * FROM post")
    List<Post> getAll();

    @Query("SELECT * FROM post WHERE id IN (:ids)")
    List<Post> loadAllByIds(int[] ids);

    @Query("SELECT * FROM post WHERE user_id = :userId LIMIT 1")
    Post findByUserId(int userId);

    @Insert
    void insertAll(Post... posts);

    @Update
    void update(Post post);

    @Delete
    void delete(Post post);

    @Query("DELETE FROM post")
    void truncate();

    @Query("SELECT * FROM post")
    Cursor getCursorAllPost();

    @Query("SELECT * FROM post WHERE id = :id")
    Cursor loadPostById(int id);
}
