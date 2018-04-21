package com.anshulsg.mypict.content.manager;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.anshulsg.mypict.content.base.NewsDownLink;

import java.util.List;

/**
 * Created by anshulsg on 8/11/17.
 */
@Dao
public interface NewsDownLinkDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addNewsLink(NewsDownLink newsDownLink);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNewsLink(NewsDownLink newsDownLink);

    @Query("SELECT * FROM newsdownlink WHERE link_fk= :parentNews")
    List<NewsDownLink> getDownLinkFor(String parentNews);

    @Query("DELETE FROM newsdownlink")
    void deleteLinks();
}
