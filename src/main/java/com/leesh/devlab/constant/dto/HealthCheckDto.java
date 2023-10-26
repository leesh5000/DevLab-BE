package com.leesh.devlab.constant.dto;

public record HealthCheckDto(String status, String locale, String timezone, String... activeProfiles) {
}
