package com.example.navidoc.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

@SuppressWarnings("unused")
public class NodeWithNeighborNodes
{
    @Embedded
    public Node node;
    @Relation(parentColumn = "id", entityColumn = "neighborId")
    public List<NeighborNode> neighborNodes;
}
