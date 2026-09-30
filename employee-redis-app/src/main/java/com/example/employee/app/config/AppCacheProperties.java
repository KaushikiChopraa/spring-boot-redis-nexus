package com.example.employee.app.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.cache")
public record AppCacheProperties(
        @DefaultValue("60s") Duration defaultTtl,
        @DefaultValue("30s") Duration employeeTtl,
        @DefaultValue("20s") Duration employeeListTtl,
        @DefaultValue("20s") Duration departmentTtl) {
}
