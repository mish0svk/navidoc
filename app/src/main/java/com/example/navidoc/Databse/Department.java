package com.example.navidoc.Databse;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Department
{
    @PrimaryKey (autoGenerate = true)
    int ID;
    String name;
    int floor;

    public Department(String name, int floor)
    {
        this.name = name;
        this.floor = floor;
    }

    public void setID(int ID)
    {
        this.ID = ID;
    }

    public int getID()
    {
        return ID;
    }

    public String getName()
    {
        return name;
    }

    public int getFloor()
    {
        return floor;
    }

    @Override
    public String toString() {
        return "Department{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", floor=" + floor +
                '}';
    }
}
