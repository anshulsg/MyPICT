package com.anshulsg.mypict.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;

import com.anshulsg.mypict.service.background.Updater;
import com.anshulsg.mypict.util.Utility;
/*
* JobService that is to be periodically called for updating attendance.
* All it does is gather parameters and invoke a suitable intent-service from 'Updater'*/
public class UpdateAttendance extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        SharedPreferences userPref, sysPref;
        userPref= getSharedPreferences(Utility.UserSharedPreferences.USER.toString(), MODE_PRIVATE);
        sysPref= getSharedPreferences(Utility.SystemSharedPreferences.FLAGS.toString(), MODE_PRIVATE);
        if(sysPref.getBoolean(Utility.SystemSharedPreferences.F_LOGIN.toString(), false)){
            String user,pass;
            user= userPref.getString(Utility.UserSharedPreferences.U_ID.toString(), "");
            pass= userPref.getString(Utility.UserSharedPreferences.U_PASSWORD.toString(), "");
            Updater.startUpdatingAttendance(this, user, pass);
            return true;
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
