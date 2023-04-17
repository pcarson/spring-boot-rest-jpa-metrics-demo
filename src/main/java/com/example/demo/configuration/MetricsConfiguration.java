package com.example.demo.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${metrics.environment}")
    private String metricsEnvironment;

    @Bean
    /**
     * Add environment/application tags to all metrics
     */
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", appName, "environment", metricsEnvironment);
    }
}
