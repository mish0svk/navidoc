package com.example.navidoc;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable
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

    protected Place(Parcel in)
    {
        ambulance = in.readString();
        doctorsName = in.readString();
        department = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>()
    {
        @Override
        public Place createFromParcel(Parcel in)
        {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size)
        {
            return new Place[size];
        }
    };

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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(ambulance);
        dest.writeString(doctorsName);
        dest.writeString(department);
    }
}
