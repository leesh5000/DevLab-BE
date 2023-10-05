package config;

import com.leesh.devlab.config.WebConfig;
import com.leesh.devlab.jwt.implementation.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({JwtService.class, WebConfig.class, RestDocsConfig.class})
public class WebMvcTestConfig {

}
