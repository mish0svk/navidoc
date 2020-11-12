package com.example.navidoc.Databse;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DepartmentWithDoctors
{
    @Embedded
    public Department department;
    @Relation(parentColumn = "ID", entityColumn = "department_id")
    public List<Doctor> doctors;
}
