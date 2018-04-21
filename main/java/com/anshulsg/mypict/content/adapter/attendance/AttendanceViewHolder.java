package com.anshulsg.mypict.content.adapter.attendance;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.util.FragmentInteraction;

import java.util.Locale;

/**
 * Created by anshulsg on 9/11/17.
 */

class AttendanceViewHolder extends RecyclerView.ViewHolder {
    private TextView subjectTV, attendanceTV, descriptionTV;
    private DisplayedAttendance attendance;
    private ProgressBar totalBar;
    private TextView totalTV;
    AttendanceViewHolder(final View itemView, FragmentInteraction callback, TextView textView, final int max) {
        super(itemView);
        Button add, miss, revert;
        subjectTV= itemView.findViewById(R.id.attendance_card_subject);
        attendanceTV= itemView.findViewById(R.id.attendance_card_value);
        descriptionTV= itemView.findViewById(R.id.attendance_card_description);
        add= itemView.findViewById(R.id.attendance_card_attend_one);
        miss=itemView.findViewById(R.id.attendance_card_miss_one);
        revert=itemView.findViewById(R.id.attendance_card_revert);
        totalTV= textView;
        totalBar= callback.getAttendanceBar();
        final float original_view_tranz= itemView.getTranslationZ();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float total_att_val= Float.parseFloat(totalTV.getText().toString());
                total_att_val-=attendance.decrementVal(max);
                total_att_val+=attendance.attendOne(max);
                String str= "Viewing for "+attendance.getDisplayed_att()+" of "+attendance.getDisplayed_tot();
                descriptionTV.setText(str);
                String per= String.format(Locale.ENGLISH,"%.1f", attendance.getCurrent())+"%";
                attendanceTV.setText(per);
                totalTV.setText(String.format(Locale.ENGLISH, "%.2f", total_att_val));
                totalBar.setProgress((int)total_att_val);
                itemView.animate().translationZ(20);

            }
        });
        miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float total_att_val= Float.parseFloat(totalTV.getText().toString());
                total_att_val-=attendance.decrementVal(max);
                total_att_val+=attendance.missOne(max);
                String str= "Viewing for "+attendance.getDisplayed_att()+" of "+attendance.getDisplayed_tot();
                descriptionTV.setText(str);
                String per= String.format(Locale.ENGLISH,"%.1f", attendance.getCurrent())+"%";
                attendanceTV.setText(per);
                totalTV.setText(String.format(Locale.ENGLISH, "%.2f", total_att_val));
                totalBar.setProgress((int)total_att_val);
                itemView.animate().translationZ(20);
            }
        });
        revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float total_att_val= Float.parseFloat(totalTV.getText().toString());
                total_att_val-=attendance.decrementVal(max);
                total_att_val+=attendance.revert(max);
                String str= "Attended "+attendance.getDisplayed_att()+" of "+attendance.getDisplayed_tot();
                descriptionTV.setText(str);
                String per= String.format(Locale.ENGLISH,"%.1f", attendance.getCurrent())+"%";
                attendanceTV.setText(per);
                totalTV.setText(String.format(Locale.ENGLISH, "%.2f", total_att_val));
                totalBar.setProgress((int)total_att_val);
                itemView.animate().translationZ(original_view_tranz);
            }
        });
    }
    public void setData(DisplayedAttendance object){
        subjectTV.setText(object.getSubject());
        attendance= object;
        String str;
        if(object.getDisplayed_tot()==object.getTotal()){
            str= "Attended "+attendance.getDisplayed_att()+" of "+attendance.getDisplayed_tot();
        }
        else str= "Viewing for "+attendance.getDisplayed_att()+" of "+attendance.getDisplayed_tot();
        descriptionTV.setText(str);
        String per= String.format(Locale.ENGLISH,"%.1f", attendance.getCurrent())+"%";
        attendanceTV.setText(per);
    }

}
