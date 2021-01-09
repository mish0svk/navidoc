package com.example.navidoc.utils;

import com.example.navidoc.database.DAO;
import com.example.navidoc.database.Node;
import com.example.navidoc.database.NodeWithNeighborNodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class Dijkstra
{
    private final NodeGraph source;
    private List<NodeWithNeighborNodes> allNodes;
    private List<NodeGraph> nodes;
    private Graph graph;

    public Dijkstra(DAO dao, Node source)
    {
        this.source = new NodeGraph(source.getUniqueId());
        nodes = new ArrayList<>();
        graph = new Graph();
        nodes.add(this.source);
        allNodes = dao.getNodesWithNeighbors();
        allNodes.forEach(node ->
        {
            NodeGraph tmp = new NodeGraph(node.node.getUniqueId());

            if (nodes.stream().noneMatch(tmp::equals))
            {
                nodes.add(tmp);
            }
        });
        for (NodeWithNeighborNodes node : allNodes)
        {
            NodeGraph nodeGraph = nodes.get(getIndex(node.node));
            dao.getNeighborNodesByNodeId(node.node.getId()).forEach(neighborNode ->
            {
                NodeGraph tmp = nodes.stream().filter(nodeGraph1 -> nodeGraph1.getName().equals(neighborNode.getUniqueId())).collect(Collectors.toList()).get(0);
                nodeGraph.addDestination(tmp, neighborNode.getDistance());
            });
        }
        nodes.forEach(node -> graph.addNode(node));
    }

    private int getIndex(Node node)
    {
        int idx = 0;
        for (NodeGraph nodeGraph : nodes)
        {
            if (nodeGraph.getName().equals(node.getUniqueId()))
            {
                return idx;
            }
            idx++;
        }

        return idx;
    }

    public Graph calculateShortestPathFromSource()
    {
        source.setDistance(0);
        Set<NodeGraph> settledNodes = new HashSet<>();
        Set<NodeGraph> unSettledNodes = new HashSet<>();

        unSettledNodes.add(source);
        while (unSettledNodes.size() != 0)
        {
            NodeGraph currentNode = getLowestDistanceNode(unSettledNodes);
            unSettledNodes.remove(currentNode);
            for (Map.Entry<NodeGraph, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet())
            {
                NodeGraph adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode))
                {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unSettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }

    private void calculateMinimumDistance(NodeGraph evaluationNode, Integer edgeWeigh, NodeGraph sourceNode)
    {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance())
        {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<NodeGraph> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    private NodeGraph getLowestDistanceNode(Set<NodeGraph> unsettledNodes)
    {
        NodeGraph lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (NodeGraph node : unsettledNodes)
        {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance)
            {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }
}
