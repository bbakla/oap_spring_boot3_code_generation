package com.example.petstore.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {

        var local = new Server();
        local.setUrl("http://localhost:8080");
        local.setDescription("Local development of petstore");

        var server = new Server();
        server.setUrl("http://petstore.swagger.io/v1");
        server.setDescription("Server URL of petstore");


        Contact contact = new Contact();
        contact.setEmail("your-email@email.com");
        contact.setName("your name");
        contact.setUrl("https://www.your email.com");

        License mitLicense = new License().name("Licence type").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Petstore Management API")
                .version("1.0")
                .contact(contact)
                .description("Open API code generation and swagger ui of petstore")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(server, local));
    }
}
