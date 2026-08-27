package com.agrifleet.network_service.service;

import com.agrifleet.network_service.algorithm.kruskal.KruskalMst;
import com.agrifleet.network_service.dto.NetworkAnalysisResponse;
import com.agrifleet.network_service.dto.WeightCheckResponse;
import com.agrifleet.network_service.repository.NetworkResultRepository;
import com.agrifleet.network_service.repository.RoadNetworkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NetworkAnalysisService {

    private final RoadNetworkRepository roadNetworkRepository;
    private final NetworkResultRepository networkResultRepository;
    private final TarjanBridgeDetector tarjanBridgeDetector;
    private final KruskalMst kruskalMst;

    public NetworkAnalysisService(
            RoadNetworkRepository roadNetworkRepository,
            NetworkResultRepository networkResultRepository,
            TarjanBridgeDetector tarjanBridgeDetector,
            KruskalMst kruskalMst
    ) {
        this.roadNetworkRepository = roadNetworkRepository;
        this.networkResultRepository = networkResultRepository;
        this.tarjanBridgeDetector = tarjanBridgeDetector;
        this.kruskalMst = kruskalMst;
    }

    public NetworkAnalysisResponse analyzeNetwork(int regionId) {

        Graph graph = roadNetworkRepository.loadGraph();

        List<GraphEdge> bridges =
                tarjanBridgeDetector.findBridges(graph);

        KruskalMst.MstResult mst =
                kruskalMst.findMst(graph);

        networkResultRepository.clearAnalysisResults(regionId);

        for (GraphEdge bridge : bridges) {
            networkResultRepository.saveBridge(regionId, bridge);
        }

        networkResultRepository.saveMst(regionId, mst);

        List<NetworkAnalysisResponse.BridgeResponse> bridgeResponses =
                bridges.stream()
                        .map(edge ->
                                new NetworkAnalysisResponse.BridgeResponse(
                                        edge.getU(),
                                        edge.getV(),
                                        edge.getWeight(),
                                        edge.getMaxWeightTonnes()
                                )
                        )
                        .toList();

        List<NetworkAnalysisResponse.MstEdgeResponse> mstEdges =
                mst.selectedEdges()
                        .stream()
                        .map(edge ->
                                new NetworkAnalysisResponse.MstEdgeResponse(
                                        edge.getU(),
                                        edge.getV(),
                                        edge.getWeight()
                                )
                        )
                        .toList();

        NetworkAnalysisResponse.MstResponse mstResponse =
                new NetworkAnalysisResponse.MstResponse(
                        mst.totalCost(),
                        mst.spanningTree(),
                        mstEdges
                );

        return new NetworkAnalysisResponse(
                regionId,
                graph.getNodeCount(),
                graph.getEdges().size(),
                bridgeResponses,
                mstResponse
        );
    }

    /**
     * Checks whether a machine can safely cross a road.
     */
    public WeightCheckResponse checkVehicleWeight(
            int uNode,
            int vNode,
            double vehicleWeightTonnes
    ) {

        Optional<Double> limit =
                roadNetworkRepository.findRoadWeightLimit(
                        uNode,
                        vNode
                );

        if (limit.isEmpty()) {
            return new WeightCheckResponse(
                    uNode,
                    vNode,
                    vehicleWeightTonnes,
                    0.0,
                    false,
                    "Road was not found."
            );
        }

        double roadLimit = limit.get();

        boolean allowed =
                vehicleWeightTonnes <= roadLimit;

        String message = allowed
                ? "Vehicle is allowed to use this road."
                : "Vehicle exceeds the road weight restriction.";

        return new WeightCheckResponse(
                uNode,
                vNode,
                vehicleWeightTonnes,
                roadLimit,
                allowed,
                message
        );
    }

}
