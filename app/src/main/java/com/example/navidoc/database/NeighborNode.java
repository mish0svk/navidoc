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
    int nodeId;
    int nodeNumber;

    public NeighborNode(String uniqueId, int distance, boolean active, int nodeId, int nodeNumber)
    {
        this.uniqueId = uniqueId;
        this.distance = distance;
        this.active = active;
        this.nodeId = nodeId;
        this.nodeNumber = nodeNumber;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public int getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
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
