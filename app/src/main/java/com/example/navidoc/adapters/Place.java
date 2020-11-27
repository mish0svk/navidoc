package com.example.navidoc.adapters;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable
{
    private final String ambulance;
    private final String department;
    private final int floor;
    private final String doctorsName;
    private final String startTime;
    private final String endTime;
    private final String phoneNumber;
    private final String websiteUrl;
    private int favourite;

    public Place(String ambulance, String department, int floor, String doctorsName, String startTime,
                 String endTime, String phoneNumber, String websiteUrl, int favourite)
    {
        this.ambulance = ambulance;
        this.department = department;
        this.floor = floor;
        this.doctorsName = doctorsName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.phoneNumber = phoneNumber;
        this.websiteUrl = websiteUrl;
        this.favourite = favourite;
    }


    protected Place(Parcel in)
    {
        ambulance = in.readString();
        department = in.readString();
        floor = in.readInt();
        doctorsName = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        phoneNumber = in.readString();
        websiteUrl = in.readString();
        favourite = in.readInt();
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

    public String getDepartment()
    {
        return department;
    }

    public int getFloor()
    {
        return floor;
    }

    public String getDoctorsName()
    {
        return doctorsName;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getWebsiteUrl()
    {
        return websiteUrl;
    }

    public int isFavourite() { return favourite; }

    public void setFavourite(int favourite)
    {
        this.favourite = favourite;
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
        dest.writeString(department);
        dest.writeInt(floor);
        dest.writeString(doctorsName);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(phoneNumber);
        dest.writeString(websiteUrl);
        dest.writeInt(favourite);
    }
}
