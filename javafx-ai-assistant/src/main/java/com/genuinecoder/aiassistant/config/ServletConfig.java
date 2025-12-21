package com.genuinecoder.aiassistant.config;

import com.genuinecoder.aiassistant.servlet.ProjectServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for registering servlets in the Spring Boot application.
 * This class sets up the servlet registration beans with their respective URL mappings.
 */
@Configuration
public class ServletConfig {
    
    /**
     * Registers the ProjectServlet with the servlet container.
     * The servlet will be mapped to the "/project" URL pattern.
     *
     * @return ServletRegistrationBean for ProjectServlet
     */
    @Bean
    public ServletRegistrationBean<ProjectServlet> projectServletRegistration() {
        ServletRegistrationBean<ProjectServlet> registration = new ServletRegistrationBean<>();
        registration.setServlet(new ProjectServlet());
        registration.addUrlMappings("/project");
        registration.setLoadOnStartup(1);
        registration.setName("projectServlet");
        return registration;
    }
}