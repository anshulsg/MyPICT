package com.anshulsg.mypict.content.provider;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.anshulsg.mypict.content.manager.AttendanceDAO;
import com.anshulsg.mypict.content.manager.NewsDAO;
import com.anshulsg.mypict.content.manager.NewsDownLinkDAO;
import com.anshulsg.mypict.content.base.Attendance;
import com.anshulsg.mypict.content.base.News;
import com.anshulsg.mypict.content.base.NewsDownLink;


@Database(entities = {Attendance.class, News.class, NewsDownLink.class}, exportSchema = false, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public abstract AttendanceDAO attendanceDAO();
    public abstract NewsDAO newsDAO();
    public abstract NewsDownLinkDAO downLinkDAO();

    public static AppDatabase getDatabase(Context context){
        if(INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context, AppDatabase.class, "userdatabase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    public static void destroyInstance(){
        INSTANCE= null;
    }
}
