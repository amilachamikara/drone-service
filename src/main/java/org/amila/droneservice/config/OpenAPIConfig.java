package org.amila.droneservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Drone Fleet Client API", description = "REST API that allows clients to communicate with the drones")
)
@Configuration
public class OpenAPIConfig {
}
