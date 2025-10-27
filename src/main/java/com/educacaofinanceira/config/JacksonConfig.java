package com.educacaofinanceira.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuração do Jackson para serialização JSON
 * Previne LazyInitializationException ao serializar entidades Hibernate
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // Registrar módulo Hibernate para lidar com proxies lazy
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();

        // Configurar para NÃO tentar carregar relacionamentos lazy
        // Se encontrar um proxy lazy não inicializado, retorna null ao invés de lançar exceção
        hibernateModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, false);
        hibernateModule.configure(Hibernate5JakartaModule.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);

        objectMapper.registerModule(hibernateModule);

        return objectMapper;
    }
}
