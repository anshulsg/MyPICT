package com.anshulsg.mypict.content.base;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
* Field Description:
* 1.id: ~~removed from design~~
* 2.title: title to be displayed on card
* 3.date: timestamp
* 4.source: link to original article
* 5.sourceType: PICT or UniPune for the card display
* 6.dismissed: flag that stores card dismissal status
* 7.pin: flag that stores card pinning status
* 8.links: one or more links to downloadable documents*/
@Entity
public class News implements Comparable<News>{
    @PrimaryKey
    private @NonNull String title;
    private String date;
    private String source;
    @Ignore
    private String sourceType;
    private boolean dismissed;
    private boolean pinned;
    @Ignore
    private List<NewsDownLink> links;
    @Ignore
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy", Locale.US);
    @Ignore
    public News(String title){
        this.title = title;
        dismissed= false;
        pinned=false;
        Date d= new Date();
        date= sdf.format(d);
    }
    public News(){
        dismissed=false;
        pinned=false;
        Date d= new Date();
        date= sdf.format(d);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NewsDownLink> getLinks() {
        if(links==null)
            return new ArrayList<>();
        else return links;
    }

    public void setLinks(List<NewsDownLink> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "/"+title+":"+source;
    }
    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

    public boolean isDismissed() {
        return dismissed;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSourceType() {
        if(sourceType!=null)
            return sourceType;
        if(source!=null){
            if(source.contains("pict.edu")) return "PICT";
            else return "UniPune";
        }
        return null;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
        if(source.contains("pict.edu")) sourceType="PICT";
        else sourceType="UniPune";
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public Date getDateObject(){
        try {
            return sdf.parse(date);
        }
        catch (ParseException exc){
            return new Date();
        }
    }
    public void setDateObject(Date dateObject){
        date= sdf.format(dateObject);
    }
    /**
     * first pinned news sorted by date, then unpinned news sorted by date
    */
    @Override
    public int compareTo(@NonNull News news) {
        if((this.isPinned() && news.isPinned())||(!this.isPinned() && !news.isPinned())){
            return this.getDateObject().compareTo(news.getDateObject());
        }
        if(this.isPinned()) return -1;
        else return 1;
    }
}
