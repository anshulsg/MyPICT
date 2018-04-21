package com.anshulsg.mypict.content.manager;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.anshulsg.mypict.content.base.News;
import java.util.List;

@Dao
public interface NewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addNews(News news);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void addIfNotPresent(News news);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNews(News news);

    @Query("SELECT * FROM news")
    List<News> getNews();

    @Query("SELECT * FROM news WHERE title = :title_val")
    List<News> getNewsByID(String title_val);

    @Query("DELETE FROM news")
    void deleteNews();
}
