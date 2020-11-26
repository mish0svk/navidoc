package com.example.navidoc.Databse;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {

    @PrimaryKey(autoGenerate = true)
    int History_ID;
    String date;
    String time;

    public History(int history_ID, String date, String time) {
        History_ID = history_ID;
        this.date = date;
        this.time = time;
    }

    public History(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public int getHistory_ID() {
        return History_ID;
    }

    public void setHistory_ID(int history_ID) {
        History_ID = history_ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "History{" +
                "History_ID=" + History_ID +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
