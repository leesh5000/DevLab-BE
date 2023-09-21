package com.leesh.devlab.api.home;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final Environment environment;

    @GetMapping(value = "/api/secret", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> healthCheck() {

        Map<String, Object> map = new HashMap<>();
        map.put("health", "ok");
        map.put("activeProfiles", environment.getActiveProfiles());
        map.put("locale", Locale.getDefault());
        map.put("timezone", TimeZone.getDefault());

        return map;
    }

}
