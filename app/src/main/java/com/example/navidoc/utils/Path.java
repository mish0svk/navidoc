package com.example.navidoc.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Path implements Parcelable
{
    private List<Hop> hops;
    private int currentHopIdx;

    public List<Hop> getHops()
    {
        return hops;
    }

    public void setHops(List<Hop> hops)
    {
        this.hops = hops;
    }

    public Path(NodeGraph node)
    {
        this.currentHopIdx = 0;
        this.hops = new ArrayList<>();
        List<NodeGraph> shortestPath = node.getShortestPath();
        for (int idx = 0; idx < shortestPath.size() - 1; idx++)
        {
            Hop hop = new Hop(shortestPath.get(idx).getName(), shortestPath.get(idx + 1).getName(), shortestPath.get(idx + 1).getDistance());
            hops.add(hop);
        }
    }

    protected Path(Parcel in)
    {
        currentHopIdx = in.readInt();
    }

    public static final Creator<Path> CREATOR = new Creator<Path>()
    {
        @Override
        public Path createFromParcel(Parcel in)
        {
            return new Path(in);
        }

        @Override
        public Path[] newArray(int size)
        {
            return new Path[size];
        }
    };

    public boolean isFinalHop()
    {
        return (currentHopIdx + 1) == hops.size();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(currentHopIdx);
    }
}
