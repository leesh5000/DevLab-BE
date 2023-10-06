package com.leesh.devlab.controller;

import com.leesh.devlab.dto.HealthCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.TimeZone;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final Environment environment;

    @GetMapping(value = "/api/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HealthCheck> healthCheck() {

        HealthCheck response = new HealthCheck("ok", Locale.getDefault().toString(), TimeZone.getDefault().getID(), environment.getActiveProfiles());

        return ResponseEntity.ok(response);
    }

}
