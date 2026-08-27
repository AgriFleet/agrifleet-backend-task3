package com.agrifleet.network_service.repository;

import com.agrifleet.network_service.algorithm.kruskal.KruskalMst;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

@Repository
public class NetworkResultRepository {

    private final JdbcTemplate jdbcTemplate;

    public NetworkResultRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clearAnalysisResults(int regionId) {

        jdbcTemplate.update(
                """
                DELETE FROM network_bridges_and_cuts
                WHERE region_id = ?
                """,
                regionId
        );

        jdbcTemplate.update(
                """
                DELETE FROM mst_logistics_backbone
                WHERE region_id = ?
                """,
                regionId
        );
    }

    public void saveBridge(
            int regionId,
            GraphEdge edge
    ) {

        jdbcTemplate.update(
                """
                INSERT INTO network_bridges_and_cuts
                (
                    region_id,
                    u_node,
                    v_node,
                    is_bridge,
                    is_articulation_point,
                    max_tonnage_limit,
                    is_severed,
                    isolated_subgraph_nodes
                )
                VALUES (?, ?, ?, 1, 0, ?, 0, NULL)
                """,
                regionId,
                edge.getU(),
                edge.getV(),
                edge.getMaxWeightTonnes()
        );
    }

    public long saveMst(
            int regionId,
            KruskalMst.MstResult mstResult
    ) {

        String edgeJson = mstResult.selectedEdges()
                .stream()
                .map(this::edgeToJson)
                .collect(Collectors.joining(",", "[", "]"));

        return jdbcTemplate.update(
                """
                INSERT INTO mst_logistics_backbone
                (
                    region_id,
                    mst_edge_list,
                    total_backbone_cost
                )
                VALUES (?, ?, ?)
                """,
                regionId,
                edgeJson,
                mstResult.totalCost()
        );
    }

    private String edgeToJson(GraphEdge edge) {

        return String.format(
                Locale.US,
                "{\"u\":%d,\"v\":%d,\"w\":%.3f}",
                edge.getU(),
                edge.getV(),
                edge.getWeight()
        );
    }

}
