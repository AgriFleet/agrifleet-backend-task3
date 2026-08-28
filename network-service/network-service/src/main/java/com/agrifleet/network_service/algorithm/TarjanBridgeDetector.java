package com.agrifleet.network_service.algorithm;

import com.agrifleet.network_service.graph.Graph;
import com.agrifleet.network_service.graph.GraphEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <h1>Tarjan's Bridge Detection Algorithm</h1>
 * <p>
 * Implements linear-time bridge (cut-edge) detection on undirected rural road networks
 * using Depth-First Search (DFS) traversal with entry discovery times (tin) and
 * low-link values (low).
 * </p>
 *
 * <h2>Learning Outcome Mapping:</h2>
 * <ul>
 *   <li><b>LO1:</b> Complexity reasoning:
 *     <ul>
 *       <li>Time Complexity: Upper Bound {@code O(V + E)}, Tight Bound {@code Θ(V + E)}, Lower Bound {@code Ω(V + E)}.</li>
 *       <li>Space Complexity: Auxiliary {@code O(V)} for recursion stack, discovery arrays, and visited tracking.</li>
 *     </ul>
 *   </li>
 *   <li><b>LO2:</b> Data structure integration with custom Adjacency List graph representation.</li>
 * </ul>
 *
 * <h2>Bridge Condition:</h2>
 * <p>
 * For a forward tree-edge {@code (u, v)}, if {@code low[v] > tin[u]}, no back-edge exists from
 * the subtree rooted at {@code v} to {@code u} or any ancestor of {@code u}. Therefore, removing
 * {@code (u, v)} increases the number of connected components, confirming {@code (u, v)} is a critical bridge.
 * </p>
 *
 * @author Uvisha (AgriFleet Task 3 Network Analysis Team)
 */
public class TarjanBridgeDetector {

    private final Set<Integer> visited = new HashSet<>();
    private final Map<Integer, Integer> tin = new HashMap<>();
    private final Map<Integer, Integer> low = new HashMap<>();
    private final List<GraphEdge> bridges = new ArrayList<>();
    private int timer = 0;

    /**
     * Finds all critical bridges in the given road network graph.
     * Supports both connected topologies and multi-cluster disconnected networks.
     *
     * @param graph The agricultural road network graph
     * @return List of critical road bridges whose failure disconnects farm clusters
     */
    public List<GraphEdge> findBridges(Graph graph) {
        visited.clear();
        tin.clear();
        low.clear();
        bridges.clear();
        timer = 0;

        // Traverse all nodes to support both single and multi-cluster disconnected graphs
        for (Integer nodeId : graph.getNodes().keySet()) {
            if (!visited.contains(nodeId)) {
                dfs(nodeId, -1, graph);
            }
        }

        return bridges;
    }

    /**
     * Recursive DFS traversal maintaining discovery time and lowest reachable ancestor.
     *
     * @param u            Current node being visited
     * @param parentEdgeId ID of the road used to enter node u (prevents immediate backtrack)
     * @param graph        The road network graph
     */
    private void dfs(int u, int parentEdgeId, Graph graph) {
        visited.add(u);
        timer++;
        tin.put(u, timer);
        low.put(u, timer);

        for (GraphEdge edge : graph.getNeighbors(u)) {
            // Avoid immediate backtrack through the exact same physical road
            if (edge.id() == parentEdgeId) {
                continue;
            }

            int v = edge.getOtherNode(u);

            if (visited.contains(v)) {
                // Back-edge: neighbor v was discovered earlier in the DFS traversal tree
                low.put(u, Math.min(low.get(u), tin.get(v)));
            } else {
                // Tree-edge: recursively visit unvisited child node v
                dfs(v, edge.id(), graph);

                // Update low-link value of u with child's lowest reachable ancestor
                low.put(u, Math.min(low.get(u), low.get(v)));

                // Evaluate Tarjan's bridge condition
                if (low.get(v) > tin.get(u)) {
                    bridges.add(edge);
                }
            }
        }
    }
}
