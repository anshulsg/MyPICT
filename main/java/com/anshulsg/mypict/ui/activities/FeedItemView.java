package com.anshulsg.mypict.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.content.provider.AppDatabase;
import com.anshulsg.mypict.content.base.News;
import com.anshulsg.mypict.content.base.NewsDownLink;
import com.anshulsg.mypict.util.Utility;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class FeedItemView extends AppCompatActivity {
    private News news;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_item_view);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        FloatingActionButton fab =  (FloatingActionButton)findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        TextView source = (TextView)findViewById(R.id.feed_item_source), description=(TextView)findViewById(R.id.feed_item_description);
        ListView list = (ListView)findViewById(R.id.feed_item_links);
        AppDatabase database = AppDatabase.getDatabase(this);
        String id= getIntent().getExtras().getString(Utility.NEWS_ID);
        AppBarLayout colorBar = (AppBarLayout)findViewById(R.id.app_bar);

        news = database.newsDAO().getNewsByID(id).get(0);
        final List<NewsDownLink> links = database.downLinkDAO().getDownLinkFor(id);
        Log.d("FeedItemView", "Links collected: "+ links.size());
        // Share button action.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,
                        "Take a look at this:\n"+news.getTitle()+"\nAvailable here:\n"+news.getSource()+"\nSent from the MyPICT App.");
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        this.setTitle(news.getTitle());
        if(news.getSourceType().equalsIgnoreCase("PICT")){
            colorBar.setBackgroundResource(R.color.colorPICT);
        }
        else colorBar.setBackgroundResource(R.color.colorSPPU);

        //TODO : Replace By String Resource.
        source.setText("Visit Source Website");
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse(news.getSource()));
                startActivity(intent);
            }
        });
        description.setText(news.getDate());

        //Set up the list for downloads.
        if(links.size()!=0){
            List<String> names = new ArrayList<>();
            for(NewsDownLink link : links){
                names.add(link.getTitle());
            }
            list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(links.get(i).getUrl()));
                    startActivity(intent);
                }
            });
            setListViewHeightBasedOnChildren(list);
        }
        else list.setVisibility(GONE);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, Toolbar.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
