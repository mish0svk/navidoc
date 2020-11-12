package com.example.navidoc.Databse;

public class Department {

    int ID;
    String name;
    int floor;

    public Department(int ID, String name, int floor) {
        this.ID = ID;
        this.name = name;
        this.floor = floor;
    }

    public Department(String name, int floor) {
        this.name = name;
        this.floor = floor;
    }

    public Department() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
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
