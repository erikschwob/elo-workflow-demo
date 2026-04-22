package de.erikschwob.elodemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ELO Workflow Demo API")
                .description("""
                    Prototype exploring ECM core concepts inspired by ELO Digital Office.
                    
                    Implements document management (Sord objects), controlled vocabularies
                    (KeywordLists), hierarchical folder structures, and a state-machine
                    based workflow engine with full audit trail.
                    
                    This is an independent learning prototype — not affiliated with
                    ELO Digital Office GmbH.
                    """)
                .version("0.1.0")
                .contact(new Contact().name("Erik Schwob"))
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
            );
    }
}
