package com.agrifleet.network_service.service;

import com.agrifleet.network_service.algorithm.KruskalMST;
import com.agrifleet.network_service.algorithm.TarjanBridgeDetector;
import com.agrifleet.network_service.entity.RoadEdgeEntity;
import com.agrifleet.network_service.repository.MstBackboneRepository;
import com.agrifleet.network_service.repository.NetworkBridgeRepository;
import com.agrifleet.network_service.repository.RoadEdgeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NetworkService {

    private final NetworkBridgeRepository bridgeRepository;
    private final MstBackboneRepository mstRepository;
    private final RoadEdgeRepository roadEdgeRepository;

    public NetworkService(NetworkBridgeRepository bridgeRepository,
                          MstBackboneRepository mstRepository,
                          RoadEdgeRepository roadEdgeRepository) {
        this.bridgeRepository = bridgeRepository;
        this.mstRepository = mstRepository;
        this.roadEdgeRepository = roadEdgeRepository;
    }

    public Map<String, Object> analyzeRegion(Long regionId) {
        // 1. Fetch live edges dynamically from the SQLite 'road_edges' table
        List<RoadEdgeEntity> dbEdges = roadEdgeRepository.findAll();
        List<TarjanBridgeDetector.Edge> edges = new ArrayList<>();

        int maxNodeId = 10;
        for (RoadEdgeEntity dbEdge : dbEdges) {
            edges.add(new TarjanBridgeDetector.Edge(
                    dbEdge.getUNode().intValue(),
                    dbEdge.getVNode().intValue(),
                    dbEdge.getComputedWeight()
            ));
            maxNodeId = Math.max(maxNodeId, Math.max(dbEdge.getUNode().intValue(), dbEdge.getVNode().intValue()));
        }

        // 2. Run Tarjan's Bridge Detection on live DB records
        TarjanBridgeDetector detector = new TarjanBridgeDetector();
        List<TarjanBridgeDetector.Edge> bridges = detector.findBridges(maxNodeId, edges);

        // 3. Run Kruskal's MST on live DB records
        KruskalMST.MSTResult mstResult = KruskalMST.calculateMST(maxNodeId, edges);

        Map<String, Object> response = new HashMap<>();
        response.put("regionId", regionId);
        response.put("criticalBridges", bridges);
        response.put("mstBackboneEdges", mstResult.mstEdges);
        response.put("totalBackboneCost", mstResult.totalCost);

        return response;
    }

    public Map<String, Object> checkWeightLimit(Long uNode, Long vNode, Double vehicleWeightTonnes) {
        // Dynamically find the weight tolerance for this specific edge from the database
        List<RoadEdgeEntity> allEdges = roadEdgeRepository.findAll();
        double bridgeLimit = 40.0; // Default safety threshold

        Optional<RoadEdgeEntity> matchedEdge = allEdges.stream()
                .filter(e -> (e.getUNode().equals(uNode) && e.getVNode().equals(vNode)) ||
                        (e.getUNode().equals(vNode) && e.getVNode().equals(uNode)))
                .findFirst();

        if (matchedEdge.isPresent() && matchedEdge.get().getMaxWeightTonnes() != null) {
            bridgeLimit = matchedEdge.get().getMaxWeightTonnes();
        }

        boolean isSafe = vehicleWeightTonnes <= bridgeLimit;

        Map<String, Object> result = new HashMap<>();
        result.put("uNode", uNode);
        result.put("vNode", vNode);
        result.put("vehicleWeightTonnes", vehicleWeightTonnes);
        result.put("bridgeLimitTonnes", bridgeLimit);
        result.put("isAllowed", isSafe);
        result.put("warning", isSafe ? "Weight limit acceptable." : "WARNING: Vehicle tonnage exceeds structural bridge tolerance! Rerouting recommended.");

        return result;
    }
}