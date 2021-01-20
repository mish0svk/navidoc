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
    int nodeId;
    String cardinalDirection;

    public NeighborNode(String uniqueId, String cardinalDirection, int distance, int nodeId)
    {
        this.uniqueId = uniqueId;
        this.cardinalDirection = cardinalDirection;
        this.distance = distance;
        this.nodeId = nodeId;
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

    public String getCardinalDirection()
    {
        return cardinalDirection;
    }

    public void setCardinalDirection(String cardinalDirection)
    {
        this.cardinalDirection = cardinalDirection;
    }

    @Override
    public String toString()
    {
        return "NeighborNode{" +
                "neighborId=" + neighborId +
                ", uniqueId='" + uniqueId + '\'' +
                ", distance=" + distance +
                ", nodeId=" + nodeId +
                ", cardinalDirection='" + cardinalDirection + '\'' +
                '}';
    }
}
