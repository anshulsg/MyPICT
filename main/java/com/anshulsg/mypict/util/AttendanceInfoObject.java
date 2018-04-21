package com.anshulsg.mypict.util;

import android.animation.ObjectAnimator;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Locale;

public class AttendanceInfoObject {
    private ProgressBar bar;
    private TextView view;
    public AttendanceInfoObject() {

    }
    public AttendanceInfoObject(TextView v, ProgressBar b){
        bar=b;
        view=v;
    }
    public void setBar(ProgressBar bar) {
        this.bar = bar;
    }
    public void setView(TextView view) {
        this.view = view;
    }
    public void setAttendance(float percent){
        if(bar!=null){
            ObjectAnimator.ofInt(bar, "progress", (int)percent).setDuration(500).start();
        }
        if(view!=null){
            view.setText(String.format(Locale.ENGLISH, "%.2f", percent));
        }
    }
    public void setText(float percent){
        if(view!=null){
            view.setText(String.format(Locale.ENGLISH, "%.2f", percent));
        }
    }
    public void setProgress(float percent){
        if(bar!=null){
            ObjectAnimator.ofInt(bar, "progress", (int)percent).setDuration(500).start();
        }
    }
    public float getAttendance(){
        if(view!=null){
            try{
                return Float.parseFloat(view.getText().toString());
            }
            catch (NumberFormatException exc){
                return 0;
            }
        }
        return 0;
    }
    public void toggleIndeterminate(boolean to){
        if(bar!=null){
            bar.setIndeterminate(to);
        }
    }
}
