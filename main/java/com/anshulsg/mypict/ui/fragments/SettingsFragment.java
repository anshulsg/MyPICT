package com.anshulsg.mypict.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.util.FragmentInteraction;
import com.anshulsg.mypict.util.Utility;


public class SettingsFragment extends Fragment{
    private FragmentInteraction callback;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback= (FragmentInteraction) context;
    }
    public SettingsFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        SharedPreferences uPref= getActivity().getSharedPreferences(Utility.UserSharedPreferences.USER.toString(), Context.MODE_PRIVATE);
        String name_val, div_val, img_val;
        name_val= uPref.getString(Utility.UserSharedPreferences.U_NAME.toString(), "Batman");
        div_val= uPref.getString(Utility.UserSharedPreferences.U_DIVISION.toString(), "Gotham");
        img_val= uPref.getString(Utility.UserSharedPreferences.U_IMAGE.toString(), null);
        ImageView img= view.findViewById(R.id.settings_user_image);
        if(img_val!=null){
            img.setImageBitmap(Utility.decodeBitmap(img_val));
        }
        TextView name, division;
        name= view.findViewById(R.id.settings_user_name);
        division= view.findViewById(R.id.settings_user_extra);
        name.setText(name_val);
        division.setText(div_val);
        return view;
    }
}