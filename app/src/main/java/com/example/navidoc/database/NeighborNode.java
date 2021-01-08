package com.example.navidoc.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@SuppressWarnings("unused")
@Entity
public class NeighborNode
{
    @PrimaryKey(autoGenerate = true)
    int neighborId;
    String uniqueId;
    int distance;
    boolean active;

    public NeighborNode(String uniqueId, int distance, boolean active)
    {
        this.uniqueId = uniqueId;
        this.distance = distance;
        this.active = active;
    }

    public int getNeighborId()
    {
        return neighborId;
    }

    public void setNeighborId(int neighborId)
    {
        this.neighborId = neighborId;
    }

    public String getUniqueId()
    {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
    }

    public int getDistance()
    {
        return distance;
    }

    public void setDistance(int distance)
    {
        this.distance = distance;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    @Override
    public String toString()
    {
        return "NeighborNode{" +
                "id=" + neighborId +
                ", mac='" + uniqueId + '\'' +
                ", distance=" + distance +
                ", active=" + active +
                '}';
    }
}
