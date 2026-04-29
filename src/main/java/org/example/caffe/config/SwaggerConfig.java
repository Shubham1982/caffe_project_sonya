package org.example.caffe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    // Set SWAGGER_SERVER_URL env variable when running behind ngrok, e.g.:
    // SWAGGER_SERVER_URL=https://xxxx.ngrok-free.app
    // Falls back to localhost for local development.
    @Value("${swagger.server-url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(serverUrl);
        server.setDescription("Active Server");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Caffe Application API")
                        .version("1.0.0")
                        .description("Detailed API Documentation for the Caffe application."));
    }
}
