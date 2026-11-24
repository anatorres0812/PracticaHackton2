package ana.torres.martinez.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "CASA NOBLE - Room Management API",
        version = "1.0.0",
        description = """
            REST API for managing hotel rooms.
            This API allows users to create, retrieve, update,
            delete and restore room records.
            """
    )
)
public class OpenApiConfig {
}
