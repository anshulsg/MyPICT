package com.anshulsg.mypict.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.util.FragmentInteraction;
import com.anshulsg.mypict.content.adapter.news.NewsAdapter;
import com.anshulsg.mypict.content.provider.AppDatabase;
import com.anshulsg.mypict.content.base.News;

import java.util.List;


public class NewsFragment extends Fragment {
    List<News> newses;
    private FragmentInteraction callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback= (FragmentInteraction) context;
    }
    public NewsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_news, container, false);
        RecyclerView newsContainer=view.findViewById(R.id.news_recycler_view);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        newsContainer.setLayoutManager(layoutManager);
        newses= AppDatabase.getDatabase(getActivity()).newsDAO().getNews();
        NewsAdapter adapter= new NewsAdapter(newses, getContext());
        newsContainer.setAdapter(adapter);
        return view;
    }

}
