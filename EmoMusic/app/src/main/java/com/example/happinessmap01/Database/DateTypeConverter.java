
package com.example.happinessmap01.Database;


import android.arch.persistence.room.TypeConverter;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(String value) {
        try {
            if (value == null)
                return null;
            else
               return  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(value);
        }

        catch (ParseException parseException){
            parseException.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String toString(Date date) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return date == null ? null : formatter.format(date);
    }
}
