package com.anshulsg.mypict.content.base;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Attendance {
    @PrimaryKey
    @NonNull private String subject;
    private int attended;
    private int total;
    public Attendance(){}
    @Ignore
    public Attendance(String subject, int attended, int total) {
        this.subject = subject;
        this.attended = attended;
        this.total = total;
    }
    @Ignore
    public Attendance(Attendance obj){
        this.subject= obj.getSubject();
        this.attended= obj.getAttended();
        this.total= obj.getTotal();
    }
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getAttended() {
        return attended;
    }

    public void setAttended(int attended) {
        this.attended = attended;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public float getAttendance()
    {
        return (attended/(float)total)*100;
    }

    @Override
    public String toString() {
        return subject+":"+ getAttendance();
    }
}
