package com.anshulsg.mypict.content.adapter.attendance;

import com.anshulsg.mypict.content.base.Attendance;

import java.math.BigDecimal;

/**
 * Extends Attendance. Attendance objects are converted to these before being shown in RecyclerView.
 * This is done as DisplayedAttendance has native calculator support.
 * HIGHLY INEFFICIENT.
 * TODO: Try directly obtaining this from Persistence. or Remove Attendance class Altogether(using Ignore fields).
 */

class DisplayedAttendance extends Attendance {
    private int displayed_att, displayed_tot;
    private float current;
    DisplayedAttendance(Attendance source){
        super(source);
        displayed_att= source.getAttended();
        displayed_tot= source.getTotal();
        current= source.getAttendance();
    }
    float decrementVal(int size){
        return round(current/size, 2);
    }
    float attendOne(int size){
        displayed_att++;
        displayed_tot++;
        current= (displayed_att/(float)displayed_tot)*100;
        return round(current/size, 2);
    }
    float missOne(int size){
        displayed_tot++;
        current=(displayed_att/(float)displayed_tot)*100;
        return round(current/size, 2);
    }
    float revert(int size){
        displayed_att= getAttended();
        displayed_tot= getTotal();
        current= getAttendance();
        return round(current/size, 2);
    }
    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    int getDisplayed_att() {
        return displayed_att;
    }
    int getDisplayed_tot() {
        return displayed_tot;
    }
    float getCurrent() {
        return current;
    }
}
