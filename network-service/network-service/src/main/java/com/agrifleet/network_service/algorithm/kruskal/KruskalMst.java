package com.agrifleet.network_service.algorithm.kruskal;

import com.agrifleet.network_service.graph.Graph;
import com.agrifleet.network_service.graph.GraphEdge;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class KruskalMst {

    public record MstResult(
            List<GraphEdge> selectedEdges,
            double totalCost,
            boolean spanningTree
    ) {}

    public MstResult findMst(Graph graph) {

        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }

        List<GraphEdge> edges =
                new ArrayList<>(graph.getEdges());

        if (graph.getNodeCount() <= 1) {
            return new MstResult(
                    List.of(),
                    0.0,
                    true
            );
        }

        if (edges.isEmpty()) {
            return new MstResult(
                    List.of(),
                    0.0,
                    false
            );
        }

        // Cheapest roads first.
        edges.sort(
                Comparator.comparingDouble(GraphEdge::weight)
        );

        int maxNodeId = graph.getNodes()
                .keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        DisjointSet dsu =
                new DisjointSet(maxNodeId);

        List<GraphEdge> mstEdges =
                new ArrayList<>();

        double totalCost = 0.0;

        for (GraphEdge edge : edges) {

            int u = edge.u();
            int v = edge.v();

            /*
             * union() returns true only when u and v
             * belong to different sets.
             *
             * Therefore this edge does not create a cycle.
             */
            if (dsu.union(u, v)) {

                mstEdges.add(edge);
                totalCost += edge.weight();

                if (mstEdges.size()
                        == graph.getNodeCount() - 1) {
                    break;
                }
            }
        }

        boolean spanningTree =
                mstEdges.size()
                        == graph.getNodeCount() - 1;

        return new MstResult(
                List.copyOf(mstEdges),
                totalCost,
                spanningTree
        );
    }
}
