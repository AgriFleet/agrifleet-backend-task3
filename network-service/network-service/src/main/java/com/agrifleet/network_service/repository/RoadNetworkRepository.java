package com.agrifleet.network_service.repository;

import com.agrifleet.network_service.graph.Graph;
import com.agrifleet.network_service.graph.GraphEdge;
import com.agrifleet.network_service.graph.GraphNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoadNetworkRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoadNetworkRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Graph loadGraph() {

        Graph graph = new Graph();

        String nodeSql = """
                SELECT
                    node_id,
                    node_name,
                    lat,
                    lng
                FROM road_nodes
                ORDER BY node_id
                """;

        jdbcTemplate.query(
                nodeSql,
                (rs, rowNum) ->
                        new GraphNode(
                                rs.getInt("node_id"),
                                rs.getString("node_name"),
                                rs.getDouble("lat"),
                                rs.getDouble("lng")
                        )
        ).forEach(graph::addNode);

        /*
         * road_edges contains two rows for a
         * two-way physical road.
         *
         * Therefore use u_node < v_node to load
         * one physical edge.
         */
        String edgeSql = """
                SELECT
                    MIN(edge_id) AS edge_id,
                    u_node,
                    v_node,
                    computed_weight,
                    max_weight_tonnes
                FROM road_edges
                WHERE u_node < v_node
                GROUP BY
                    u_node,
                    v_node,
                    computed_weight,
                    max_weight_tonnes
                ORDER BY edge_id
                """;

        jdbcTemplate.query(
                edgeSql,
                (rs, rowNum) ->
                        new GraphEdge(
                                rs.getInt("edge_id"),
                                rs.getInt("u_node"),
                                rs.getInt("v_node"),
                                rs.getDouble("computed_weight"),
                                rs.getDouble("max_weight_tonnes")
                        )
        ).forEach(graph::addEdge);

        return graph;
    }

    public Optional<Double> findRoadWeightLimit(
            int uNode,
            int vNode
    ) {

        String sql = """
                SELECT max_weight_tonnes
                FROM road_edges
                WHERE
                    (u_node = ? AND v_node = ?)
                    OR
                    (u_node = ? AND v_node = ?)
                LIMIT 1
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        rs.getDouble("max_weight_tonnes"),
                uNode,
                vNode,
                vNode,
                uNode
        ).stream().findFirst();
    }
}
