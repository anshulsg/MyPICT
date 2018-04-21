package com.anshulsg.mypict.content.adapter.attendance;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.util.FragmentInteraction;
import com.anshulsg.mypict.content.base.Attendance;

import java.util.ArrayList;
import java.util.List;


public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceViewHolder> {
    private List<DisplayedAttendance> attendances;
    private FragmentInteraction callback;
    private TextView attendance_val;
    public AttendanceAdapter(List<Attendance> attendances, FragmentInteraction callback, TextView display){
        super();
        attendance_val=display;
        this.attendances = new ArrayList<>();
        for(Attendance a: attendances){
            this.attendances.add(new DisplayedAttendance(a));
        }
        this.callback= callback;
    }
    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_attendance, parent, false);
        return new AttendanceViewHolder(view, callback, attendance_val, attendances.size());
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {
        holder.setData(attendances.get(position));
    }

    @Override
    public int getItemCount() {
        return attendances.size();
    }
    public void update(List<Attendance> vals){
        attendances= new ArrayList<>();
        for(Attendance val:vals){
            attendances.add(new DisplayedAttendance(val));
        }
        notifyDataSetChanged();
    }
}
