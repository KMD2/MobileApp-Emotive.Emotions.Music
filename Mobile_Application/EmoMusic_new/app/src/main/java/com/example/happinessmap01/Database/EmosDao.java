package com.example.happinessmap01.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Dao
public interface EmosDao {


    @Query("SELECT *  FROM performance_table")
    List<Emos> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Emos emos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Emos... emos);

    @Query("DELETE FROM performance_table")
    void deleteAll();

}
