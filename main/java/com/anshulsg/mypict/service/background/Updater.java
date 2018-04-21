package com.anshulsg.mypict.service.background;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.anshulsg.mypict.content.manager.AttendanceDAO;
import com.anshulsg.mypict.content.manager.NewsDAO;
import com.anshulsg.mypict.content.manager.NewsDownLinkDAO;
import com.anshulsg.mypict.content.provider.AppDatabase;
import com.anshulsg.mypict.content.base.Attendance;
import com.anshulsg.mypict.content.base.News;
import com.anshulsg.mypict.content.base.NewsDownLink;
import com.anshulsg.mypict.service.parser.Parser;
import com.anshulsg.mypict.util.Utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
/*
* Services that serve as the intermediate between the parser utilities that fetch data and
 * database objects that store data.
 * JobScheduler's primary action is to invoke these methods with suitable parameters.*/
public class Updater extends IntentService {

    private static final String ACTION_UPDATE_ATTENDANCE = "com.anshulsg.mypict.service.background.action.updateAttendance";
    private static final String ACTION_UPDATE_NEWS = "com.anshulsg.mypict.service.background.action.updateNews";
    private static final String ACTION_SYNC_ALL="com.anshulsg.mypict.service.background.action.updateAll";

    private static final String PARAM_USERNAME = "com.anshulsg.mypict.service.background.extra.PARAM1";
    private static final String PARAM_PASSWORD = "com.anshulsg.mypict.service.background.extra.PARAM2";
    private AppDatabase database;
    public Updater() {
        super("AttendanceUpdater");
        database= AppDatabase.getDatabase(this);
    }

    public static void startUpdatingAttendance(Context context, String username, String password){
        Intent intent= new Intent(context, Updater.class);
        intent.setAction(ACTION_UPDATE_ATTENDANCE);
        intent.putExtra(PARAM_USERNAME, username);
        intent.putExtra(PARAM_PASSWORD, password);
        context.startService(intent);
    }
    public static void startUpdatingNewsFeed(Context context){
        Intent intent= new Intent(context, Updater.class);
        intent.setAction(ACTION_UPDATE_NEWS);
        Log.d("Updater", "received news feed update request");
        context.startService(intent);
    }
    public static void startSync(Context context, String username, String password){
        Intent intent= new Intent(context, Updater.class);
        intent.setAction(Intent.ACTION_SYNC);
        Log.d("Updater","received sync request");
        intent.putExtra(PARAM_USERNAME, username);
        intent.putExtra(PARAM_PASSWORD, password);
        context.startService(intent);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_ATTENDANCE.equals(action)) {
                final String param_user = intent.getStringExtra(PARAM_USERNAME);
                final String param_pwd = intent.getStringExtra(PARAM_PASSWORD);
                handleUpdatingAttendance(param_user, param_pwd);
            }
            else {
                if (ACTION_UPDATE_NEWS.equals(action)) {
                    Log.d("Updater", "Starting news feed update op");
                    handleUpdatingNews();
                }
                else {
                    Log.d("Updater", "Starting sync op");
                    final String param_user = intent.getStringExtra(PARAM_USERNAME);
                    final String param_pwd = intent.getStringExtra(PARAM_PASSWORD);
                    handleSync(param_user, param_pwd);
                }

            }
        }
    }
    private void handleSync(String user, String password){
        try{
            handleUpdatingNews();

            final String obj = "loginid=" + user + "&password=" + password + "&dbConnVar=PICT&service_id=";
            SharedPreferences uPref= getSharedPreferences(Utility.UserSharedPreferences.USER.toString(), MODE_PRIVATE);
            SharedPreferences.Editor uEdit= uPref.edit();
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

            HttpURLConnection auth = (HttpURLConnection) Utility.getAuthURL().openConnection();
            auth.setRequestMethod("POST");
            auth.setDoOutput(true);
            new DataOutputStream(auth.getOutputStream()).writeBytes(obj);
            readFrom(auth.getInputStream());

            HttpURLConnection source = (HttpURLConnection) Utility.getAttendanceURL().openConnection();
            source.setRequestMethod("GET");
            String text = readFrom(source.getInputStream());
            Document content= Jsoup.parse(text);

            AttendanceDAO data= database.attendanceDAO();
            List<Attendance> updates= Parser.parseAttendanceAt(content);
            float average=0;
            for(Attendance a: updates){
                data.addAttendance(a);
                average+=a.getAttendance();
            }
            average=average/updates.size();
            SharedPreferences.Editor editor= getSharedPreferences(Utility.CommonValues.VALUES.toString(), MODE_PRIVATE).edit();
            editor.putFloat(Utility.CommonValues.ATTENDANCE.toString(),average);
            editor.apply();

            Element table = content.selectFirst("[id=table5]");
            //5th and 9th <tr>
            Elements rows= table.select("tr");
            Element row2= rows.get(2-1);
            Element image= row2.selectFirst("img");
            String imageURL= image.attr("src");
            InputStream is= new java.net.URL(imageURL).openStream();
            Bitmap img= BitmapFactory.decodeStream(is);
            uEdit.putString(Utility.UserSharedPreferences.U_IMAGE.toString(), Utility.encodeBitmap(img));

            Element row5= rows.get(5-1);
            Elements nameCells= row5.select("td");
            String name= nameCells.get(1).text().trim();
            Log.d("Updater", "Found name: "+name);
            uEdit.putString(Utility.UserSharedPreferences.U_NAME.toString(), name);

            Element row9= rows.get(9-1);
            Elements divisionCells= row9.select("td");
            String division= divisionCells.get(1).text().trim().substring(0,2);
            division+="-"+divisionCells.get(3).text().trim();
            Log.d("Updater", "Found div: "+division);
            uEdit.putString(Utility.UserSharedPreferences.U_DIVISION.toString(), division);
            uEdit.apply();
        }
        catch (IOException exc){
            Object[] A=exc.getStackTrace();
            String content="{";
            for(Object a:A ){
                content+=a;
            }
            content+="}";
            Log.e("Updater", "IO Error in Sync"+exc.getLocalizedMessage()+","+content);
        }
    }
    public static String readFrom(InputStream is){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String content="";
            while (true) {
                String temp = br.readLine();
                if(temp==null) return content;
                content= content.concat(temp.trim());
            }
        }
        catch (IOException exc){
            Log.d("Updater", "IO Read Error.");
            return null;
        }
    }

    private void handleUpdatingNews(){
        final String[] newsLinks= Utility.SOURCE_NEWS;
        NewsDAO newsDAO= database.newsDAO();
        NewsDownLinkDAO linkDAO= database.downLinkDAO();
        List<News> newses= new ArrayList<>();
        Log.d("Updater", "Starting update");
        for(String news:newsLinks){
            Log.d("Updater", "For: "+news);
            if(news.contains("pict.edu/news")){
                newses.addAll(Parser.parseHTMLAt(news));
            }
            else{
                if(news.startsWith("http://collegecirculars")){
                    newses.addAll(Parser.parseRSSAt(news));
                }
            }
        }
        Log.d("Updater", "Obtained All Feed");
        for(News toUpsert: newses){
            Log.d("Updater", "Upserting : "+ toUpsert);
            try {
                newsDAO.addIfNotPresent(toUpsert);
            }
            catch (Exception e){
                //ignore
            }
            List<NewsDownLink> children= toUpsert.getLinks();
            for(NewsDownLink child:children){
                Log.d("Updater", "Adding Link : "+child);
                linkDAO.addNewsLink(child);
            }
        }
    }
    private void handleUpdatingAttendance(String user, String password){
        try {
            AttendanceDAO data= database.attendanceDAO();
            List<Attendance> updates= Parser.parseAttendanceFor(user, password);
            float average=0;
            for(Attendance a: updates){
                data.addAttendance(a);
                average+=a.getAttendance();
            }
            average=average/updates.size();
            SharedPreferences.Editor editor= getSharedPreferences(Utility.CommonValues.VALUES.toString(), MODE_PRIVATE).edit();
            editor.putFloat(Utility.CommonValues.ATTENDANCE.toString(),average);
            editor.apply();
        }
        catch (Exception exc){
            Log.e("Updater",exc.toString());
        }
    }

}
