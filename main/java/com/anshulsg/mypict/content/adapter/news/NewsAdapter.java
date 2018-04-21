package com.anshulsg.mypict.content.adapter.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.content.manager.NewsDAO;
import com.anshulsg.mypict.content.provider.AppDatabase;
import com.anshulsg.mypict.ui.activities.FeedItemView;
import com.anshulsg.mypict.content.base.News;
import com.anshulsg.mypict.util.Utility;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    private static final int PINNED_VIEW=24, UN_PINNED_VIEW=42;
    private List<News> newses;
    private Context context;
    public NewsAdapter(List<News> list, Context context){
        super();
        Collections.sort(list);
        newses= list;
        this.context=context;
    }
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) {
        final News news= newses.get(position);
        holder.setData(news);

        holder.getClickBar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                News n= newses.get(holder.getAdapterPosition());
                Intent intent= new Intent(context, FeedItemView.class);
                intent.putExtra(Utility.NEWS_ID, n.getTitle());
                context.startActivity(intent);
            }
        });
        holder.getDismiss().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDAO dao = AppDatabase.getDatabase(NewsAdapter.this.context).newsDAO();
                int pos=holder.getAdapterPosition();
                News n= newses.get(pos);
                n.setDismissed(true);
                newses.remove(pos);
                notifyItemRemoved(pos);
                dao.updateNews(n);
            }
        });
        holder.getPin().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDAO dao= AppDatabase.getDatabase(NewsAdapter.this.context).newsDAO();
                int position= holder.getAdapterPosition();
                int z_translate=2;
                News n= newses.get(position);
                newses.remove(position);
                n.setPinned(!n.isPinned());
                int new_pos;
                if(n.isPinned()){
                    new_pos= reposition(n, 0);
                    z_translate= 10;
                }
                else new_pos= reposition(n, position);
                newses.add(new_pos, n);
                if(new_pos!=position) notifyItemMoved(position, new_pos);

                dao.updateNews(n);
                holder.itemView.animate().translationZ(z_translate);
                holder.getPin().animate().rotationBy(180);
            }
        });
        holder.getShare().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,
                        "Take a look at this:\n"+news.getTitle()+"\nAvailable here:\n"+news.getSource()+"\nSent from the MyPICT App.");
                intent.setType("text/plain");
                context.startActivity(intent);
            }
        });
    }
    private int reposition(News news, int curr_index){
        if(curr_index==newses.size()) return curr_index;
        int i=newses.get(curr_index).compareTo(news);
        if(i<0){
            return reposition(news, curr_index+1);
        }
        else return curr_index;
    }
    @Override
    public int getItemViewType(int position) {
        News item= newses.get(position);
        if(item.isPinned()){
            return PINNED_VIEW;
        }
        else return UN_PINNED_VIEW;
    }

    @Override
    public int getItemCount() {
        return newses.size();
    }
}
