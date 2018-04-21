package com.anshulsg.mypict.content.adapter.news;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.content.base.News;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


class NewsViewHolder extends RecyclerView.ViewHolder {
    private TextView title, date;
    private ImageButton dismiss, pin, share;
    private RelativeLayout clickBar, colorBar;
    NewsViewHolder(View itemView) {
        super(itemView);
        title= itemView.findViewById(R.id.card_news_title);
        date= itemView.findViewById(R.id.card_news_date);
        dismiss= itemView.findViewById(R.id.card_news_dismiss);
        pin= itemView.findViewById(R.id.card_news_pin);
        clickBar= itemView.findViewById(R.id.card_news_title_bar);
        colorBar= itemView.findViewById(R.id.card_news_color_bar);
        share= itemView.findViewById(R.id.card_news_share);
    }
    void setData(News news){
        title.setText(news.getTitle());
        date.setText(news.getDate());
        if(news.isPinned()){
            this.itemView.animate().translationZ(10);
            pin.setRotation(180);
        }
        if(news.getSourceType().equalsIgnoreCase("PICT")){
            colorBar.setBackgroundResource(R.color.colorPICT);
        }
        else{
            colorBar.setBackgroundResource(R.color.colorSPPU);
        }
    }
    ImageButton getDismiss() {
        return dismiss;
    }
    ImageButton getPin() {
        return pin;
    }
    RelativeLayout getClickBar() {
        return clickBar;
    }

    public ImageButton getShare() {
        return share;
    }
}
