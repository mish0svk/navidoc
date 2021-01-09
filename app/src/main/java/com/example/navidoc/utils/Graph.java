package com.example.navidoc.utils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
public class Graph
{
    private List<NodeGraph> nodes = new ArrayList<>();

    public void addNode(NodeGraph node)
    {
        nodes.add(node);
    }

    public List<NodeGraph> getNodes()
    {
        return nodes;
    }
}
