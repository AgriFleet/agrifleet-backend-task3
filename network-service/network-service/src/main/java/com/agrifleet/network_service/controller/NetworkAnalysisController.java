package com.agrifleet.network_service.controller;

import com.agrifleet.network_service.dto.NetworkAnalysisResponse;
import com.agrifleet.network_service.dto.WeightCheckRequest;
import com.agrifleet.network_service.dto.WeightCheckResponse;
import com.agrifleet.network_service.service.NetworkAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/network-analysis")
public class NetworkAnalysisController {

    private final NetworkAnalysisService networkAnalysisService;

    public NetworkAnalysisController(
            NetworkAnalysisService networkAnalysisService
    ) {
        this.networkAnalysisService = networkAnalysisService;
    }

    @GetMapping("/analyze")
    public ResponseEntity<NetworkAnalysisResponse> analyze(
            @RequestParam(defaultValue = "101")
            int regionId
    ) {

        NetworkAnalysisResponse response =
                networkAnalysisService.analyzeNetwork(regionId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/weight-check")
    public ResponseEntity<WeightCheckResponse> checkWeight(
            @Valid @RequestBody WeightCheckRequest request
    ) {

        WeightCheckResponse response =
                networkAnalysisService.checkVehicleWeight(
                        request.uNode(),
                        request.vNode(),
                        request.vehicleWeightTonnes()
                );

        return ResponseEntity.ok(response);
    }

}
