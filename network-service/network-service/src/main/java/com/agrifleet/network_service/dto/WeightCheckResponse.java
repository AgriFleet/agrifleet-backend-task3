package com.agrifleet.network_service.dto;

public record WeightCheckResponse(
        int uNode,
        int vNode,
        double vehicleWeightTonnes,
        double roadLimitTonnes,
        boolean allowed,
        String message
) {
}
