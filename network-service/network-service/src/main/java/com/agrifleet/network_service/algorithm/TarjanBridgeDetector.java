package com.agrifleet.network_service.algorithm;

import com.agrifleet.network_service.graph.Graph;
import com.agrifleet.network_service.graph.GraphEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TarjanBridgeDetector {

    private final Set<Integer> visited = new HashSet<>();
    private final List<GraphEdge> bridges = new ArrayList<>();

    public List<GraphEdge> findBridges(Graph graph) {
        visited.clear();
        bridges.clear();

        for (Integer nodeId : graph.getNodes().keySet()) {
            if (!visited.contains(nodeId)) {
                dfs(nodeId, -1, graph);
            }
        }

        return bridges;
    }

    private void dfs(int u, int parent, Graph graph) {
        visited.add(u);

        for (GraphEdge edge : graph.getNeighbors(u)) {
            int v = edge.getOtherNode(u);

            if (v == parent) {
                continue;
            }

            if (!visited.contains(v)) {
                dfs(v, u, graph);
            }
        }
    }
}
