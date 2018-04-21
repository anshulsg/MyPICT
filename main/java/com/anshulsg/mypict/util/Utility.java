package com.anshulsg.mypict.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.anshulsg.mypict.service.UpdateAttendance;
import com.anshulsg.mypict.service.UpdateNews;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Utility {
    public static final String NEWS_ID="NEWS_ID";
    private static final int  JOB_ATT_ID= 255;
    private static final int  JOB_NEWS_ID= 256;
    public static void scheduleChecker(Context context){
        ComponentName attJobService= new ComponentName(context, UpdateAttendance.class);
        ComponentName newsJobService= new ComponentName(context, UpdateNews.class);
        JobInfo.Builder forAttendance= new JobInfo.Builder(JOB_ATT_ID, attJobService)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresCharging(true)
                .setPersisted(true)
                .setPeriodic(75_400_000);
        JobInfo.Builder forNews= new JobInfo.Builder(JOB_NEWS_ID, newsJobService)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresCharging(true)
                .setPersisted(true)
                .setPeriodic(86_400_000);
        JobScheduler scheduler= (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(scheduler!=null){
            int check= scheduler.schedule(forAttendance.build());
            Log.d("Utility","Scheduling attendance: "+(check==JobScheduler.RESULT_SUCCESS));
            check=scheduler.schedule(forNews.build());
            Log.d("Utility","Scheduling news: "+(check==JobScheduler.RESULT_SUCCESS));
        }
    }
    public static void cancelAllJobs(Context context){
        JobScheduler scheduler= (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if(scheduler!=null)
            scheduler.cancelAll();
    }


    //1. Encoders and Decoders for Image <=> String.
    public static String encodeBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos= new ByteArrayOutputStream(); //pronounced as bawss B)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes= baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    public static Bitmap decodeBitmap(String string){
        byte[] decoded= Base64.decode(string, 0);
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
    }

    //2. SharedPreferences
    public enum UserSharedPreferences{
        USER, U_NAME, U_ID, U_PASSWORD, U_DIVISION, U_IMAGE
    }
    public enum SystemSharedPreferences{
        FLAGS, F_LOGIN
    }
    public enum CommonValues{
        VALUES, ATTENDANCE
    }

    //3. URLs, and their Getters.
    private static final String SOURCE_AUTH="http://pict.ethdigitalcampus.com:80/DCWeb/authenticate.do";
    private static final String SOURCE_ATTENDANCE= "http://pict.ethdigitalcampus.com/DCWeb/form/jsp_sms/StudentsPersonalFolder_pict.jsp?dashboard=1";
    public static final String[] SOURCE_NEWS=
            {
                    "https://pict.edu/news",
                    "http://collegecirculars.unipune.ac.in/sites/examdocs/_layouts/listfeed.aspx?List=%7B4B0EE791-5E13-4721-9C7F-E0331908FDC5%7D&Source=http%3A%2F%2Fcollegecirculars%2Eunipune%2Eac%2Ein%2Fsites%2Fexamdocs%2FExamination%2520Timetable%2520First%2520Half%25202017Theory%2520Examin%2FForms%2FActive%2520Results%2Easpx"
            };
    public static URL getAuthURL() throws MalformedURLException{
        return new URL(SOURCE_AUTH);
    }
    public static URL getAttendanceURL() throws MalformedURLException{
        return new URL(SOURCE_ATTENDANCE);
    }
}
