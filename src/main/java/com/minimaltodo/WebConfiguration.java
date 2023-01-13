package com.minimaltodo;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(
                Arrays.asList("http://192.168.0.2:3000", "http://localhost:3000", "https://minimaltodo.com"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    // @Bean
    // public Jackson2ObjectMapperBuilder configureObjectMapper() {
    // return new Jackson2ObjectMapperBuilder()
    // .modulesToInstall(Hibernate4Module.class);
    // }

    // @Bean
    // public ConfigurableServletWebServerFactory webServerFactory() {
    //     JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
    //     factory.setPort(8080);
    //     factory.setContextPath("");
    //     factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"));
    //     return factory;
    // }

}