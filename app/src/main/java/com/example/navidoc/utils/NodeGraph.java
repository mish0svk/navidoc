package com.example.navidoc.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NodeGraph
{
    private String name;

    private List<NodeGraph> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    Map<NodeGraph, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(NodeGraph destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public NodeGraph(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NodeGraph> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<NodeGraph> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Map<NodeGraph, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<NodeGraph, Integer> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeGraph nodeGraph = (NodeGraph) o;
        return Objects.equals(name, nodeGraph.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
