package com.agrifleet.network_service.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoadNetworkRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoadNetworkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Graph loadGraph() {

        Graph graph = new Graph();

        List<GraphNode> nodes = jdbcTemplate.query(
                """
                SELECT node_id,
                       node_name,
                       lat,
                       lng,
                       elevation_meters,
                       is_farm_gate,
                       is_depot
                FROM road_nodes
                ORDER BY node_id
                """,
                (rs, rowNum) -> new GraphNode(
                        rs.getInt("node_id"),
                        rs.getString("node_name"),
                        rs.getDouble("lat"),
                        rs.getDouble("lng")
                )
        );

        for (GraphNode node : nodes) {
            graph.addNode(node);
        }

        List<GraphEdge> edges = jdbcTemplate.query(
                """
                SELECT MIN(edge_id) AS edge_id,
                       u_node,
                       v_node,
                       computed_weight,
                       max_weight_tonnes
                FROM road_edges
                WHERE u_node < v_node
                GROUP BY u_node, v_node, computed_weight, max_weight_tonnes
                ORDER BY edge_id
                """,
                (rs, rowNum) -> new GraphEdge(
                        rs.getInt("edge_id"),
                        rs.getInt("u_node"),
                        rs.getInt("v_node"),
                        rs.getDouble("computed_weight"),
                        rs.getDouble("max_weight_tonnes")
                )
        );

        for (GraphEdge edge : edges) {
            graph.addEdge(edge);
        }

        return graph;
    }

    public Optional<Double> findRoadWeightLimit(int uNode, int vNode) {

        List<Double> limits = jdbcTemplate.query(
                """
                SELECT max_weight_tonnes
                FROM road_edges
                WHERE (u_node = ? AND v_node = ?)
                   OR (u_node = ? AND v_node = ?)
                LIMIT 1
                """,
                (rs, rowNum) -> rs.getDouble("max_weight_tonnes"),
                uNode,
                vNode,
                vNode,
                uNode
        );

        return limits.stream().findFirst();
    }

}
