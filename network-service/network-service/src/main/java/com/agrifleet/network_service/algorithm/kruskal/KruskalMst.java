package com.agrifleet.network_service.algorithm.kruskal;

import java.util.List;

public class KruskalMst {

    public record MstResult(
            List<GraphEdge> selectedEdges,
            double totalCost,
            boolean spanningTree
    ) {
    }

    public MstResult findMst(Graph graph) {

        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }

        List<GraphEdge> edges = new ArrayList<>(graph.getEdges());

        if (edges.isEmpty()) {
            return new MstResult(
                    List.of(),
                    0.0,
                    graph.getNodeCount() <= 1
            );
        }


        edges.sort(
                Comparator.comparingDouble(GraphEdge::getWeight)
        );

        int maxNodeId = graph.getNodes()
                .stream()
                .mapToInt(node -> node.getId())
                .max()
                .orElse(0);

        DisjointSet dsu = new DisjointSet(maxNodeId);

        List<GraphEdge> mstEdges = new ArrayList<>();
        double totalCost = 0.0;

        for (GraphEdge edge : edges) {

            int u = edge.getU();
            int v = edge.getV();

            if (dsu.union(u, v)) {
                mstEdges.add(edge);
                totalCost += edge.getWeight();
                
                if (mstEdges.size() == graph.getNodeCount() - 1) {
                    break;
                }
            }
        }

        boolean isSpanningTree =
                graph.getNodeCount() <= 1 ||
                        mstEdges.size() == graph.getNodeCount() - 1;

        return new MstResult(
                List.copyOf(mstEdges),
                totalCost,
                isSpanningTree
        );
    }

}
