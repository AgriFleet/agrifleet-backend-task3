package com.agrifleet.network_service.graph;

public record GraphNode(
        int id,
        String name,
        double latitude,
        double longitude
) {
}
