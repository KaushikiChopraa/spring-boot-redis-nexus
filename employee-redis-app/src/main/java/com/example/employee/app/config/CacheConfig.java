package com.example.employee.app.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
@EnableConfigurationProperties(AppCacheProperties.class)
public class CacheConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                          AppCacheProperties properties) {
        RedisCacheConfiguration defaults = RedisCacheConfiguration
                .defaultCacheConfig(getClass().getClassLoader())
                .entryTtl(properties.defaultTtl())
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> perCache = new HashMap<>();
        perCache.put(CacheNames.EMPLOYEE, defaults.entryTtl(properties.employeeTtl()));
        perCache.put(CacheNames.EMPLOYEE_LIST, defaults.entryTtl(properties.employeeListTtl()));
        perCache.put(CacheNames.EMPLOYEES_BY_DEPARTMENT, defaults.entryTtl(properties.departmentTtl()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(perCache)
                .build();
    }
}
