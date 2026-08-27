package com.agrifleet.network_service.algorithm.kruskal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KruskalMstTest {

    @Test
    void shouldBuildMinimumSpanningTree() {

        Graph graph = new Graph();

        graph.addNode(
                new GraphNode(
                        1,
                        "A",
                        0.0,
                        0.0,
                        0.0,
                        false,
                        false
                )
        );

        graph.addNode(
                new GraphNode(
                        2,
                        "B",
                        0.0,
                        0.0,
                        0.0,
                        false,
                        false
                )
        );

        graph.addNode(
                new GraphNode(
                        3,
                        "C",
                        0.0,
                        0.0,
                        0.0,
                        false,
                        false
                )
        );

        graph.addEdge(
                new GraphEdge(
                        1,
                        1,
                        2,
                        1.0,
                        40.0
                )
        );

        graph.addEdge(
                new GraphEdge(
                        2,
                        2,
                        3,
                        2.0,
                        40.0
                )
        );

        graph.addEdge(
                new GraphEdge(
                        3,
                        1,
                        3,
                        10.0,
                        40.0
                )
        );

        KruskalMst kruskal = new KruskalMst();

        KruskalMst.MstResult result =
                kruskal.findMst(graph);

        assertTrue(result.spanningTree());

        assertEquals(
                3.0,
                result.totalCost(),
                0.0001
        );

        assertEquals(
                2,
                result.selectedEdges().size()
        );
    }

}
