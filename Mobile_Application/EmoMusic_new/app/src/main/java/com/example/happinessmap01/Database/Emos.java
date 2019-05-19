package com.example.happinessmap01.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity(tableName = "performance_table")
public class Emos {


    @PrimaryKey(autoGenerate = true)
    public int uid;

    @NonNull
    @ColumnInfo(name = "interest")
    public Float interest;

    @NonNull
    @ColumnInfo(name = "str")
    public Float stress;

    @NonNull
    @ColumnInfo(name = "rel")
    public Float relaxation;

    @NonNull
    @ColumnInfo(name = "exc")
    public Float excitement;

    @NonNull
    @ColumnInfo(name = "eng")
    public Float engagement;

    @NonNull
    @ColumnInfo(name = "foc")
    public Float focus;


    @ColumnInfo(name = "recodate")
    public String recoDate;



    public Emos(Float interest, Float stress, Float relaxation,
                Float excitement, Float engagement, Float focus,
                String recoDate) {

        this.interest = interest;
        this.stress = stress;
        this.relaxation = relaxation;
        this.excitement = excitement;
        this.engagement = engagement;
        this.focus = focus;
        this.recoDate = recoDate;

    }


}


