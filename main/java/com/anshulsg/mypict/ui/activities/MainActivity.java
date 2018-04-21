package com.anshulsg.mypict.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anshulsg.mypict.R;
import com.anshulsg.mypict.util.AttendanceInfoObject;
import com.anshulsg.mypict.util.FragmentInteraction;
import com.anshulsg.mypict.util.SectionsPagerAdapter;
import com.anshulsg.mypict.util.Utility;


public class MainActivity extends AppCompatActivity implements FragmentInteraction {

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private AttendanceInfoObject attendanceInfo;
    private ProgressBar bar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    onAttendanceVisible();
                    if(viewPager.getCurrentItem()!=0)
                        viewPager.setCurrentItem(0);

                    return true;
                case R.id.navigation_dashboard:
                    onFeedVisible();
                    if(viewPager.getCurrentItem()!=1)
                        viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    onProfileVisible();
                    if(viewPager.getCurrentItem()!=2)
                        viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };
    private ViewPager.OnPageChangeListener mOnPageChangeListener= new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    navigation.setSelectedItemId(R.id.navigation_home);
                    return;
                case 1:
                    Log.d("Main", "Page change called");
                    RecyclerView view= (RecyclerView)findViewById(R.id.attendance_recycler_view);
                    navigation.setSelectedItemId(R.id.navigation_dashboard);
                    return;
                case 2:
                    navigation.setSelectedItemId(R.id.navigation_notifications);
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SectionsPagerAdapter adapter;
        if(getSharedPreferences(Utility.SystemSharedPreferences.FLAGS.toString(), MODE_PRIVATE)
                .getBoolean(Utility.SystemSharedPreferences.F_LOGIN.toString(), false)) {
            setContentView(R.layout.activity_main);
            navigation = (BottomNavigationView)findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            viewPager = (ViewPager)findViewById(R.id.main_view_pager);
            adapter = new SectionsPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(mOnPageChangeListener);
            bar= (ProgressBar)findViewById(R.id.snippet_progress);
            attendanceInfo= new AttendanceInfoObject(null, bar);
            navigation.setSelectedItemId(R.id.navigation_home);
        }
        else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onFeedVisible() {
        attendanceInfo.setProgress(0);
    }
    @Override
    public void onAttendanceVisible() {
        float val= getSharedPreferences(Utility.CommonValues.VALUES.toString(), MODE_PRIVATE).getFloat(Utility.CommonValues.ATTENDANCE.toString(), 0);
        attendanceInfo.setProgress(val);
    }
    @Override
    public void setAttendanceValue(float value) {
        attendanceInfo.setAttendance(value);
    }
    public void startUpdatingAttendance(){
        attendanceInfo.toggleIndeterminate(true);
    }
    public void finishUpdatingAttendance(boolean status, float newVal){
        attendanceInfo.toggleIndeterminate(false);
        if(status){
            attendanceInfo.setAttendance(newVal);
        }
    }
    public Context getContext(){
        return MainActivity.this;
    }
    @Override
    public void onProfileVisible() {
        attendanceInfo.setProgress(0);
    }
    public ProgressBar getAttendanceBar(){
        return bar;
    }
    public void signOut(View view){
        SharedPreferences.Editor editor=getSharedPreferences(Utility.SystemSharedPreferences.FLAGS.toString(),MODE_PRIVATE).edit();
        editor.putBoolean(Utility.SystemSharedPreferences.F_LOGIN.toString(), false);
        editor.apply();
        Utility.cancelAllJobs(this);
        Intent intent= new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void viewPreferences(View view){
        Intent intent= new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void privacyPolicy(View view){
        Toast.makeText(this, "Yet to Implement", Toast.LENGTH_SHORT).show();
    }
    public void credits(View view){
        Toast.makeText(this, "Yet to Implement", Toast.LENGTH_SHORT).show();
    }
    public void bugReport(View view){
        Toast.makeText(this, "Yet to Implement", Toast.LENGTH_SHORT).show();
    }
    public void changePassword(View view){
        Toast.makeText(this, "Yet to Implement", Toast.LENGTH_SHORT).show();
    }
}
