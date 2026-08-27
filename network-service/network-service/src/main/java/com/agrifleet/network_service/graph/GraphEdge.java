package com.agrifleet.network_service.graph;

public record GraphEdge(
        int id,
        int u,
        int v,
        double weight,
        double maxWeightTonnes
) {
    public int getOtherNode(int current) {
        if (current == u) {
            return v;
        }
        return u;
    }
}
