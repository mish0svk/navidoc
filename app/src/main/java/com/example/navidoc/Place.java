package com.example.navidoc;

public class Place
{
    private String ambulance;
    private String doctorsName;
    private String department;

    public Place(String ambulance, String doctorsName, String department)
    {
        this.ambulance = ambulance;
        this.doctorsName = doctorsName;
        this.department = department;
    }

    public String getAmbulance()
    {
        return ambulance;
    }

    public String getDoctorsName()
    {
        return doctorsName;
    }

    public String getDepartment()
    {
        return department;
    }
}
