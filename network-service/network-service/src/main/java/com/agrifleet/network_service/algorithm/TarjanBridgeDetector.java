package com.agrifleet.network_service.algorithm;

import com.agrifleet.network_service.graph.Graph;
import com.agrifleet.network_service.graph.GraphEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TarjanBridgeDetector {

    private final Set<Integer> visited = new HashSet<>();
    private final Map<Integer, Integer> tin = new HashMap<>();
    private final Map<Integer, Integer> low = new HashMap<>();
    private final List<GraphEdge> bridges = new ArrayList<>();
    private int timer = 0;

    public List<GraphEdge> findBridges(Graph graph) {
        visited.clear();
        tin.clear();
        low.clear();
        bridges.clear();
        timer = 0;

        for (Integer nodeId : graph.getNodes().keySet()) {
            if (!visited.contains(nodeId)) {
                dfs(nodeId, -1, graph);
            }
        }

        return bridges;
    }

    private void dfs(int u, int parent, Graph graph) {
        visited.add(u);
        timer++;
        tin.put(u, timer);
        low.put(u, timer);

        for (GraphEdge edge : graph.getNeighbors(u)) {
            int v = edge.getOtherNode(u);

            if (v == parent) {
                continue;
            }

            if (visited.contains(v)) {
                low.put(u, Math.min(low.get(u), tin.get(v)));
            } else {
                dfs(v, u, graph);

                low.put(u, Math.min(low.get(u), low.get(v)));

                if (low.get(v) > tin.get(u)) {
                    bridges.add(edge);
                }
            }
        }
    }
}
