package com.anshulsg.mypict.service.parser;

import android.util.Log;
import android.util.Xml;

import com.anshulsg.mypict.content.base.Attendance;
import com.anshulsg.mypict.content.base.News;
import com.anshulsg.mypict.content.base.NewsDownLink;
import com.anshulsg.mypict.util.Utility;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    //Variables and methods for Parsing RSS Feed
    private static final String PUB_DATE= "pubdate", DESCRIPTION = "description", CHANNEL = "channel",
            LINK = "link", TITLE = "title", ITEM = "item", AUTHOR="author", GUID="guid";
    public static List<News> parseRSSAt(String url){
        Log.d("RSSParser", " Starting for "+url);
        List<News> newses= new ArrayList<>();
        XmlPullParser parser= Xml.newPullParser();
        InputStream stream;
        try{
            stream= new URL(url).openConnection().getInputStream();
            parser.setInput(stream, null);
            int eventType= parser.getEventType();
            boolean done= false;
            News item= null;
            while (eventType!=XmlPullParser.END_DOCUMENT && !done){
                String name;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name= parser.getName();
                        switch (name.toLowerCase()){
                            case PUB_DATE:
                                break;
                            case DESCRIPTION:
                                break;
                            case CHANNEL:
                                break;
                            case LINK:
                                if(item!=null){
                                    String text= replace(parser.nextText());
                                    item.setSource(text);
                                    Log.i("RSSParser", "Set Link"+ text);
                                }
                                break;
                            case TITLE:
                                if(item!=null){
                                    Log.i("RSSParser", "Set Title");
                                    item.setTitle(parser.nextText());
                                }
                                break;
                            case ITEM:
                                Log.i("RSSParser", "Made Item");
                                item= new News();
                                break;
                            case AUTHOR:
                                break;
                            case GUID:
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String tag_name= parser.getName();
                        if(tag_name.equalsIgnoreCase(ITEM)&& item!=null){
                            String download_link= null;
                            int index_form_substr= item.getSource().indexOf("/Forms/");
                            if(index_form_substr!=-1){
                                download_link= item.getSource().substring(0,index_form_substr);
                                download_link+="/"+item.getTitle()+".pdf";
                            }
                            if(download_link!=null){
                                download_link=replace(download_link);
                                NewsDownLink link= new NewsDownLink("Notice", download_link, item.getTitle());
                                ArrayList<NewsDownLink> links= new ArrayList<>();
                                links.add(link);
                                item.setLinks(links);
                            }
                            newses.add(item);
                            Log.d("RSSParser", "Parsed:"+item);
                        }
                        else if(tag_name.equalsIgnoreCase(CHANNEL)){
                            done=true;
                        }
                        break;
                }
                eventType=parser.next();
            }
        }
        catch (XmlPullParserException |IOException exc){
            Log.e("RSSParser", "Exception", exc);
        }
        return newses;
    }
    public static String replace(String str) {
        String[] words = str.split(" ");
        StringBuilder sentence = new StringBuilder(words[0]);

        for (int i = 1; i < words.length; ++i) {
            sentence.append("%20");
            sentence.append(words[i]);
        }

        return sentence.toString();
    }
    //methods for html parsing
    public static List<News> parseHTMLAt(String url){
        Log.d("HTMLParser", "Starting HTML Parse for "+url);
        List<News> newses = new ArrayList<>();
        try{
            Document resp= Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .get();
            Elements articles=resp.select("article");

            for(int i=0; i<articles.size(); i++){
                String title,source;
                Element article;
                Elements links;
                article= articles.get(i);
                links= article.select("a");
                title= links.get(1).text();
                source=links.get(1).attr("href");
                News obj= new News(title);
                obj.setSource(source);
                Log.d("HTMLParser", "Found News "+ obj +" with Links: "+links.size());
                List<NewsDownLink> link_values= new ArrayList<>();
                for(int j=4; j<links.size(); j++){
                    Element link= links.get(j);
                    String text, web_url;
                    text= link.text();
                    web_url= link.attr("href");
                    NewsDownLink downLink=new NewsDownLink(text, web_url, title);
                    Log.d("Updater", "Adding Link "+ downLink);
                    link_values.add(downLink);
                }
                obj.setLinks(link_values);
                newses.add(obj);
                Log.d("HTMLParser", "Added news to list");
            }
        }
        catch (IOException exc){
            Log.d("HTMLParser", "IOException", exc);
        }
        return newses;
    }
    //methods for attendance parsing
    public static List<Attendance> parseAttendanceFor(String user, String password){
        try {
            List<Attendance> updated = new ArrayList<>();
            final String obj = "loginid=" + user + "&password=" + password + "&dbConnVar=PICT&service_id=";
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

            HttpURLConnection auth = (HttpURLConnection) Utility.getAuthURL().openConnection();
            auth.setRequestMethod("POST");
            auth.setDoOutput(true);
            new DataOutputStream(auth.getOutputStream()).writeBytes(obj);
            readFrom(auth.getInputStream());

            HttpURLConnection source = (HttpURLConnection) Utility.getAttendanceURL().openConnection();
            source.setRequestMethod("GET");
            String text = readFrom(source.getInputStream());

            Document document = Jsoup.parse(text);
            System.out.println(document.html());
            Element table = document.selectFirst("[id=table1]");
            System.out.println(table.html());
            Elements rows = table.select(".child");

            for (int i = 1; i < rows.size() - 1; i++) {
                Element row = rows.get(i);
                Elements cells = row.select("td");
                String[] cellValues = new String[cells.size()];
                for (int j = 0; j < cells.size(); j++) {
                    cellValues[j] = cells.get(j).text().trim();
                }
                if (Integer.parseInt(cellValues[1]) != 0) {
                    Attendance attendance = new Attendance(cellValues[0], Integer.parseInt(cellValues[2]), Integer.parseInt(cellValues[1]));
                    Log.d("Updater", "Updating:" + updated.toString());
                    updated.add(attendance);
                }
            }
            return updated;
        }
        catch (IOException exc){
            Log.d("AParser", exc.toString());
            return new ArrayList<>();
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
    //alternate attendance parsing from html string
    public static List<Attendance> parseAttendanceAt(Document document){
        List<Attendance> updated = new ArrayList<>();

        System.out.println(document.html());
        Element table = document.selectFirst("[id=table1]");
        System.out.println(table.html());
        Elements rows = table.select(".child");

        for (int i = 1; i < rows.size() - 1; i++) {
            Element row = rows.get(i);
            Elements cells = row.select("td");
            String[] cellValues = new String[cells.size()];
            for (int j = 0; j < cells.size(); j++) {
                cellValues[j] = cells.get(j).text().trim();
            }
            if (Integer.parseInt(cellValues[1]) != 0) {
                Attendance attendance = new Attendance(cellValues[0], Integer.parseInt(cellValues[2]), Integer.parseInt(cellValues[1]));
                Log.d("Updater", "Updating:" + updated.toString());
                updated.add(attendance);
            }
        }
        return updated;
    }
}
