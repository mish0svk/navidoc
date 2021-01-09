package com.example.navidoc.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@SuppressWarnings("unused")
@Entity
public class Node
{
    @PrimaryKey(autoGenerate = true)
    int id;
    String uniqueId;
    boolean active;
    int nodeNumber;

    public Node(String uniqueId, boolean active, int nodeNumber)
    {
        this.uniqueId = uniqueId;
        this.active = active;
        this.nodeNumber = nodeNumber;
    }

    public int getNodeNumber()
    {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber)
    {
        this.nodeNumber = nodeNumber;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUniqueId()
    {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
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
        return "Node{" +
                "id=" + id +
                ", mac='" + uniqueId + '\'' +
                ", active=" + active +
                '}';
    }
}
