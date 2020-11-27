package com.example.navidoc.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Doctor
{
    @PrimaryKey (autoGenerate = true)
    int Doctor_ID;
    String name;
    String ambulance_name;
    String phone_number;
    String email;
    int door_number;
    String web_site;
    String start_time;
    String end_time;
    int isFavorite;
    int department_id;
    int history_id;

    public Doctor(String name, String ambulance_name, String phone_number, String email, int door_number, String web_site, String start_time, String end_time, int isFavorite, int department_id, int history_id) {
        this.name = name;
        this.ambulance_name = ambulance_name;
        this.phone_number = phone_number;
        this.email = email;
        this.door_number = door_number;
        this.web_site = web_site;
        this.start_time = start_time;
        this.end_time = end_time;
        this.isFavorite = isFavorite;
        this.department_id = department_id;
        this.history_id = history_id;
    }

    public void setDoctor_ID(int doctor_ID)
    {
        Doctor_ID = doctor_ID;
    }

    public int getDoctor_ID()
    {
        return Doctor_ID;
    }

    public int getDepartment_id()
    {
        return department_id;
    }

    public String getName()
    {
        return name;
    }

    public String getAmbulance_name()
    {
        return ambulance_name;
    }

    public String getPhone_number()
    {
        return phone_number;
    }

    public String getEmail()
    {
        return email;
    }

    public int getDoor_number()
    {
        return door_number;
    }

    public String getWeb_site()
    {
        return web_site;
    }

    public String getStart_time()
    {
        return start_time;
    }

    public String getEnd_time()
    {
        return end_time;
    }

    public void setIsFavorite(int isFavorite) { this.isFavorite = isFavorite; }

    public int getIsFavorite()
    {
        return isFavorite;
    }

    public int getHistory_id() {
        return history_id;
    }

    public void setHistory_id(int history_id) {
        this.history_id = history_id;
    }

    @Override
    public String toString()
    {
        return "Doctor{" +
                "Doctor_ID=" + Doctor_ID +
                ", name='" + name + '\'' +
                ", ambulance_name='" + ambulance_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", door_number=" + door_number +
                ", web_site='" + web_site + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Doctor_ID == doctor.Doctor_ID &&
                door_number == doctor.door_number &&
                isFavorite == doctor.isFavorite &&
                department_id == doctor.department_id &&
                Objects.equals(name, doctor.name) &&
                Objects.equals(ambulance_name, doctor.ambulance_name) &&
                Objects.equals(phone_number, doctor.phone_number) &&
                Objects.equals(email, doctor.email) &&
                Objects.equals(web_site, doctor.web_site) &&
                Objects.equals(start_time, doctor.start_time) &&
                Objects.equals(end_time, doctor.end_time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Doctor_ID, name, ambulance_name, phone_number, email, door_number, web_site, start_time, end_time, isFavorite, department_id);
    }
}
