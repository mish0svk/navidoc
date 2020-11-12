package com.example.navidoc.Databse;

public class Doctor {

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

    public Doctor(int doctor_ID, String name, String ambulance_name, String phone_number, String email, int door_number, String web_site, String start_time, String end_time, int isFavorite) {
        Doctor_ID = doctor_ID;
        this.name = name;
        this.ambulance_name = ambulance_name;
        this.phone_number = phone_number;
        this.email = email;
        this.door_number = door_number;
        this.web_site = web_site;
        this.start_time = start_time;
        this.end_time = end_time;
        this.isFavorite = isFavorite;
    }

    public Doctor(String name, String ambulance_name, String phone_number, String email, int door_number, String web_site, String start_time, String end_time, int isFavorite) {
        this.name = name;
        this.ambulance_name = ambulance_name;
        this.phone_number = phone_number;
        this.email = email;
        this.door_number = door_number;
        this.web_site = web_site;
        this.start_time = start_time;
        this.end_time = end_time;
        this.isFavorite = isFavorite;
    }

    public Doctor(String name, String ambulance_name) {
        this.name = name;
        this.ambulance_name = ambulance_name;
    }

    public Doctor() {
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDoctor_ID() {
        return Doctor_ID;
    }

    public void setDoctor_ID(int doctor_ID) {
        Doctor_ID = doctor_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmbulance_name() {
        return ambulance_name;
    }

    public void setAmbulance_name(String ambulance_name) {
        this.ambulance_name = ambulance_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getDoor_number() {
        return door_number;
    }

    public void setDoor_number(int door_number) {
        this.door_number = door_number;
    }

    public String getWeb_site() {
        return web_site;
    }

    public void setWeb_site(String web_site) {
        this.web_site = web_site;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    @Override
    public String toString() {
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
}
