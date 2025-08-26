package com.project.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "app")
public interface ApplicationConfig {

    @WithDefault("E-Commerce App")
    String name();

    @WithDefault("1.0.0")
    String version();

    @WithDefault("10")
    Integer defaultPageSize();

    @WithDefault("100")
    Integer maxPageSize();

    @WithDefault("5")
    Integer lowStockThreshold();
}