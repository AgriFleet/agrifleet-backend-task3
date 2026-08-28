package com.agrifleet.network_service.dto;

import java.util.List;

public record NetworkAnalysisResponse(int regionId,
                                      int nodeCount,
                                      int edgeCount,
                                      List<BridgeResponse> bridges,
                                      MstResponse mst) {


    public record BridgeResponse(
            int u,
            int v,
            double weight,
            double maxWeightTonnes
    ) {
    }

    public record MstResponse(
            double totalCost,
            boolean spanningTree,
            List<MstEdgeResponse> edges
    ) {
    }

    public record MstEdgeResponse(
            int u,
            int v,
            double weight
    ) {
    }
}
