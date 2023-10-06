package com.leesh.devlab.dto;

import java.util.List;

public record HealthCheck(String status, String locale, String timezone, String... activeProfiles) {
}
