package com.example.navidoc.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DoctorsHistory {

    @Embedded
    public History history;
    @Relation(parentColumn = "History_ID", entityColumn = "history_id")
    public List<Doctor> doctors;
}
