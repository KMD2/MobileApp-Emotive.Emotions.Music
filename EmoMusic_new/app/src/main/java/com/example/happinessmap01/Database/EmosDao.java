package com.example.happinessmap01.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Dao
public interface EmosDao {


    @Query("SELECT *  FROM emotions_table")
    List<Emos> getAll();

    @Insert
    void insert(Emos emos);

    @Insert
    void insertAll(Emos... emos);

    @Query("DELETE FROM emotions_table")
    void deleteAll();

    //@Query("select recodate from emotions_table where recodate>=datetime('now', 'now', '-30 day')")
    //LiveData<List<Emos>> fetchDateByDuration(String duration);

   // @Query("select interest from emotions_table where recodate>=datetime('now', 'now', '-30 day')")
   // LiveData<List<Emos>> fetchInterestByDuration(String duration);
}
