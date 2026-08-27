package com.agrifleet.network_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record WeightCheckRequest(
        @NotNull
        Integer uNode,

        @NotNull
        Integer vNode,

        @NotNull
        @DecimalMin(
                value = "0.0",
                inclusive = true
        )
        Double vehicleWeightTonnes

        ){

}
