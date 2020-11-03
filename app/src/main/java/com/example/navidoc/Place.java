package com.example.navidoc;

public class Place
{
    private final String ambulance;
    private final String doctorsName;
    private final String department;

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
