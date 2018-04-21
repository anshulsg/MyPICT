package com.anshulsg.mypict.content.manager;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.anshulsg.mypict.content.base.Attendance;

import java.util.List;

/**
 * Created by anshulsg on 8/11/17.
 */
@Dao
public interface AttendanceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAttendance(Attendance attendance);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAttendance(Attendance attendance);

    @Query("SELECT * FROM attendance")
    List<Attendance> getAttendence();

    @Query("SELECT * FROM attendance WHERE subject = :subjectName")
    List<Attendance> getAttendanceFor(String subjectName);

    @Query("DELETE FROM attendance")
    void deleteAttendance();

}
