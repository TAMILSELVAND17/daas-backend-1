package com.daascmputers.website.security;

import com.daascmputers.website.dto.AccessoryDTO;
import com.daascmputers.website.entities.Accessory;
import com.daascmputers.website.entities.Customer;
import com.daascmputers.website.entities.User;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // ✅ Security Filter Chain with JWT Stateless Auth
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/api/product/**", "/api/customers/**").authenticated()
                                .requestMatchers("/api/product/user*/add-accessories").authenticated()
                        .anyRequest().permitAll()                      // Allow all endpoints without authentication
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults())
                .build();
    }

    // ✅ Thread Pool Executor for @Async methods (email/SMS/DB tasks)
    @Primary
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }

    // ✅ Virtual Thread Executor (for highly concurrent short tasks)
    @Bean(name = "virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor(){
//        return Executors.newVirtualThreadPerTaskExecutor();
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("vthread-accessory-", 0).factory()
        );
    }

    // ✅ Spring Cache (used by @Cacheable)
    @Bean(name = "cacheManager")
    public org.springframework.cache.CacheManager springCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "customer", "users", "accessory", "accessories", "accessory_dto"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10_000)
                .expireAfterAccess(10, TimeUnit.MINUTES));

        cacheManager.setAsyncCacheMode(true);
        return cacheManager;
    }

    // ✅ Hibernate 2nd Level Cache JSR-107 Manager
    @Bean(name = "hibernateCacheManager")
    public javax.cache.CacheManager hibernateCacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        return cachingProvider.getCacheManager();
    }

    // ✅ JCache Customizer for Hibernate Entities
    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cacheManager -> {
            cacheManager.createCache(Accessory.class.getName(),
                    new MutableConfiguration<>()
                            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR))
                            .setStoreByValue(false)
                            .setStatisticsEnabled(true));
            cacheManager.createCache(Customer.class.getName(),
                    new MutableConfiguration<>()
                            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR))
                            .setStoreByValue(false)
                            .setStatisticsEnabled(true));
            cacheManager.createCache(User.class.getName(),
                    new MutableConfiguration<>()
                            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR))
                            .setStoreByValue(false)
                            .setStatisticsEnabled(true));
            cacheManager.createCache(AccessoryDTO.class.getName(),
                    new MutableConfiguration<>()
                            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR))
                            .setStoreByValue(false)
                            .setStatisticsEnabled(true));
        };
    }

    //✅To disable auto generated password
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    //    @Bean(name = "blockingExecutor")
//    public ExecutorService taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);           // Increased core threads
//        executor.setMaxPoolSize(50);           // Increased max threads
//        executor.setQueueCapacity(100);         // Increased queue capacity
//        executor.setKeepAliveSeconds(60);       // Optional: idle timeout
//        executor.setThreadNamePrefix("AccessoryAsync-");
//        executor.setWaitForTasksToCompleteOnShutdown(true); // Optional: graceful shutdown
//        executor.initialize();
//        return executor.getThreadPoolExecutor();
//    }

    //    @Bean
//    public CacheManager cacheManager() {
//        return new ConcurrentMapCacheManager("default","accessories");
//    }

//    @Bean(name = "cacheManager")
//    public CacheManager jCacheManager() {
//        CachingProvider cachingProvider = Caching.getCachingProvider();
//        return cachingProvider.getCacheManager();
//    }

}
