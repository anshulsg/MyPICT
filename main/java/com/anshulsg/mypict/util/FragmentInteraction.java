package com.anshulsg.mypict.util;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by anshulsg on 13/11/17.
 */

public interface FragmentInteraction {
    void onFeedVisible();
    void onAttendanceVisible();
    ProgressBar getAttendanceBar();
    void setAttendanceValue(float value);
    void startUpdatingAttendance();
    void finishUpdatingAttendance(boolean status, float newVal);
    void onProfileVisible();
    Context getContext();
}
