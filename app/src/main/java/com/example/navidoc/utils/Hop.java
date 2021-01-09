package com.example.navidoc.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Hop implements Parcelable, Serializable
{
    private final String sourceUniqueId;
    private final String destinationUniqueId;
    private final int distance;

    public Hop(String sourceUniqueId, String destinationUniqueId, int distance)
    {
        this.sourceUniqueId = sourceUniqueId;
        this.destinationUniqueId = destinationUniqueId;
        this.distance = distance;
    }

    protected Hop(Parcel in)
    {
        sourceUniqueId = in.readString();
        destinationUniqueId = in.readString();
        distance = in.readInt();
    }

    public static final Creator<Hop> CREATOR = new Creator<Hop>()
    {
        @Override
        public Hop createFromParcel(Parcel in)
        {
            return new Hop(in);
        }

        @Override
        public Hop[] newArray(int size)
        {
            return new Hop[size];
        }
    };

    public String getDestinationUniqueId()
    {
        return destinationUniqueId;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(sourceUniqueId);
        dest.writeString(destinationUniqueId);
        dest.writeInt(distance);
    }
}
