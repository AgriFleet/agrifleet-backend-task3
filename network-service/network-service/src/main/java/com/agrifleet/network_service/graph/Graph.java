package com.agrifleet.network_service.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    private final Map<Integer, GraphNode> nodes = new HashMap<>();
    private final List<GraphEdge> edges = new ArrayList<>();
    private final Map<Integer, List<GraphEdge>> adjacencyList = new HashMap<>();

    public void addNode(GraphNode node) {
        nodes.put(node.id(), node);
        adjacencyList.putIfAbsent(node.id(), new ArrayList<>());
    }

    public void addEdge(GraphEdge edge) {
        edges.add(edge);
        adjacencyList.putIfAbsent(edge.u(), new ArrayList<>());
        adjacencyList.putIfAbsent(edge.v(), new ArrayList<>());

        adjacencyList.get(edge.u()).add(edge);
        adjacencyList.get(edge.v()).add(edge);
    }

    public List<GraphEdge> getNeighbors(int nodeId) {
        return adjacencyList.getOrDefault(nodeId, new ArrayList<>());
    }

    public Map<Integer, GraphNode> getNodes() {
        return nodes;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }
}
