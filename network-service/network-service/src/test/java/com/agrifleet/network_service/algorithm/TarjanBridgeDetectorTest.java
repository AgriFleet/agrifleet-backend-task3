package com.agrifleet.network_service.algorithm;

import com.agrifleet.network_service.graph.Graph;
import com.agrifleet.network_service.graph.GraphEdge;
import com.agrifleet.network_service.graph.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TarjanBridgeDetectorTest {

    private TarjanBridgeDetector detector;

    @BeforeEach
    void setUp() {
        detector = new TarjanBridgeDetector();
    }

    @Test
    @DisplayName("Test 1: Linear path A - B - C has all roads as critical bridges")
    void testLinearPathAllBridges() {
        Graph graph = new Graph();
        graph.addNode(new GraphNode(1, "Depot", 6.9, 79.8));
        graph.addNode(new GraphNode(2, "Junction", 6.91, 79.81));
        graph.addNode(new GraphNode(3, "Farm", 6.92, 79.82));

        GraphEdge e1 = new GraphEdge(1, 1, 2, 5.0, 20.0);
        GraphEdge e2 = new GraphEdge(2, 2, 3, 4.0, 20.0);

        graph.addEdge(e1);
        graph.addEdge(e2);

        List<GraphEdge> bridges = detector.findBridges(graph);

        assertEquals(2, bridges.size());
        assertTrue(bridges.contains(e1));
        assertTrue(bridges.contains(e2));
    }

    @Test
    @DisplayName("Test 2: Square cycle A - B - C - D - A has ZERO bridges due to alternate loop")
    void testSquareCycleZeroBridges() {
        Graph graph = new Graph();
        graph.addNode(new GraphNode(1, "Node 1", 6.9, 79.8));
        graph.addNode(new GraphNode(2, "Node 2", 6.91, 79.81));
        graph.addNode(new GraphNode(3, "Node 3", 6.92, 79.82));
        graph.addNode(new GraphNode(4, "Node 4", 6.93, 79.83));

        graph.addEdge(new GraphEdge(1, 1, 2, 2.0, 25.0));
        graph.addEdge(new GraphEdge(2, 2, 3, 3.0, 25.0));
        graph.addEdge(new GraphEdge(3, 3, 4, 2.5, 25.0));
        graph.addEdge(new GraphEdge(4, 4, 1, 3.5, 25.0));

        List<GraphEdge> bridges = detector.findBridges(graph);

        assertEquals(0, bridges.size());
    }

    @Test
    @DisplayName("Test 3: Triangle cycle with a single stick edge to Farm (Lollipop shape)")
    void testLollipopGraphSingleBridge() {
        Graph graph = new Graph();
        graph.addNode(new GraphNode(1, "Hub 1", 6.9, 79.8));
        graph.addNode(new GraphNode(2, "Hub 2", 6.91, 79.81));
        graph.addNode(new GraphNode(3, "Hub 3", 6.92, 79.82));
        graph.addNode(new GraphNode(4, "Isolated Farm", 6.93, 79.83));

        // Triangle cycle (1 - 2 - 3 - 1)
        graph.addEdge(new GraphEdge(1, 1, 2, 2.0, 30.0));
        graph.addEdge(new GraphEdge(2, 2, 3, 2.0, 30.0));
        graph.addEdge(new GraphEdge(3, 3, 1, 2.0, 30.0));

        // Stick connecting Hub 3 to Isolated Farm (3 - 4)
        GraphEdge stickEdge = new GraphEdge(4, 3, 4, 5.0, 15.0);
        graph.addEdge(stickEdge);

        List<GraphEdge> bridges = detector.findBridges(graph);

        assertEquals(1, bridges.size());
        assertEquals(stickEdge.id(), bridges.get(0).id());
    }
}
