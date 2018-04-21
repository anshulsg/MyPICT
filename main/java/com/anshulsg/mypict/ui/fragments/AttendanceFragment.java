package com.anshulsg.mypict.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.service.parser.Parser;
import com.anshulsg.mypict.util.FragmentInteraction;
import com.anshulsg.mypict.content.adapter.attendance.AttendanceAdapter;
import com.anshulsg.mypict.content.provider.AppDatabase;
import com.anshulsg.mypict.content.base.Attendance;
import com.anshulsg.mypict.util.Utility;

import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AttendanceFragment extends Fragment {
    private List<Attendance> vals;
    private RecyclerView contents;
    private AttendanceAdapter adapter;
    private TextView attendanceValue;
    private FragmentInteraction callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback= (FragmentInteraction) context;
    }

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUserVisibleHint(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        contents = view.findViewById(R.id.attendance_recycler_view);
        attendanceValue= view.findViewById(R.id.attendance_value);
        vals= AppDatabase.getDatabase(getActivity()).attendanceDAO().getAttendence();
        adapter = new AttendanceAdapter(vals,
                callback, attendanceValue);
        contents.setHasFixedSize(true);
        contents.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        contents.setLayoutManager(layoutManager);
        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && getView()!=null){
            float value=getActivity().getSharedPreferences(Utility.CommonValues.VALUES.toString(), MODE_PRIVATE).getFloat(Utility.CommonValues.ATTENDANCE.toString(), 0);
            callback.setAttendanceValue(value);
            if(attendanceValue!=null){
                attendanceValue.setText(String.format(Locale.ENGLISH,"%.2f",value));
            }
            /*CardView forRefresh= getActivity().findViewById(R.id.information_snippet);
            forRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences preferences= getActivity().getSharedPreferences(Utility.UserSharedPreferences.USER.toString(), MODE_PRIVATE);
                    new UpdateInBackground().execute(preferences.getString(Utility.UserSharedPreferences.U_ID.toString(), ""), preferences.getString(Utility.UserSharedPreferences.U_PASSWORD.toString(), ""));
                }
            });*/
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    public class UpdateInBackground extends AsyncTask<String, Void, List<Attendance>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.startUpdatingAttendance();
        }

        @Override
        protected List<Attendance> doInBackground(String... vals) {
            return Parser.parseAttendanceFor(vals[0], vals[1]);
        }
        @Override
        protected void onPostExecute(List<Attendance> attendances) {
            super.onPostExecute(attendances);
            AppDatabase database= AppDatabase.getDatabase(getActivity());
            if (attendances.size() != 0) {
                float sum = 0;
                for (Attendance val : attendances) {
                    database.attendanceDAO().addAttendance(val);
                    sum += val.getAttendance();
                }
                sum /= attendances.size();
                SharedPreferences.Editor editor= callback.getContext()
                        .getSharedPreferences(Utility.CommonValues.VALUES.toString(), MODE_PRIVATE)
                        .edit();
                editor.putFloat(Utility.CommonValues.ATTENDANCE.toString(), sum);
                editor.apply();
                callback.finishUpdatingAttendance(true, sum);
                if(attendanceValue!=null){
                    attendanceValue.setText(String.format(Locale.ENGLISH,"%.2f",sum));
                }
                adapter.update(attendances);
                Snackbar.make(contents, "Updated Attendance Successfully", Snackbar.LENGTH_SHORT).show();
            }
            else {
                callback.finishUpdatingAttendance(false, 0);
                Snackbar.make(contents, "Failed To Update Attendance. Try Again Later :/", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
