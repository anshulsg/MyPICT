package com.anshulsg.mypict.content.base;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/*
* Field Descriptions:
* 1.url: url of a download link, url is also the primary key
* 2.title: textual description for the link for display
* 3.parent news: the news which owns the download link-> foreign key reference*/
@Entity(foreignKeys = {
        @ForeignKey(
                entity = News.class,
                parentColumns = "title",
                childColumns = "link_fk"
        )
})
public class NewsDownLink {
    @PrimaryKey
    @NonNull private String url;
    private String title;
    @ColumnInfo(name = "link_fk", index = true)
    private String parentNews;
    public NewsDownLink(){}
    @Ignore
    public NewsDownLink(String title, String url, String parentNews) {
        this.title = title;
        this.url = url;
        this.parentNews = parentNews;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentNews() {
        return parentNews;
    }

    public void setParentNews(String parentNews) {
        this.parentNews = parentNews;
    }

    @Override
    public String toString() {
        return title+":"+url+" FROM "+parentNews;
    }
}
