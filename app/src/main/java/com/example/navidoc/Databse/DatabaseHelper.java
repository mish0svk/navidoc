package com.example.navidoc.Databse;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Department.class, Doctor.class, History.class}, version = 1, exportSchema = false)
public abstract class DatabaseHelper extends RoomDatabase
{
    private static DatabaseHelper instance;

    public abstract DAO dao();

    public static synchronized DatabaseHelper getInstance(Context context)
    {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), DatabaseHelper.class,
                    "HospitalDatabase").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }

        return instance;
    }


}

