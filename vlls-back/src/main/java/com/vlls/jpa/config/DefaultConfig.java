package com.vlls.jpa.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.PropertyResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hiephn on 2014/10/05.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.vlls.jpa.repository", "com.vlls.jpa.domain"},
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
@ConditionalOnWebApplication
public class DefaultConfig {

    @Resource(name = "springPropertyResolver")
    private PropertyResolver springPropertyResolver;

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    @Primary
    public DataSource dataSource() {
        DataSourceBuilder factory = DataSourceBuilder.create();
        return factory.build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource())
                .packages("com.vlls.jpa.domain","com.vlls.jpa.repository")
                .persistenceUnit("default")
                .properties(hibernateProperties())
                .build();
    }

    @Bean
    public Map<String, String> hibernateProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.autoReconnect", springPropertyResolver.getProperty("jpa.hibernate.connection.autoReconnect"));
        properties.put("hibernate.hbm2ddl.auto", springPropertyResolver.getProperty("jpa.hibernate.ddl-auto"));
        properties.put("hibernate.ejb.naming_strategy", springPropertyResolver.getProperty("jpa.hibernate.naming-strategy"));
        properties.put("hibernate.c3p0.min_size", springPropertyResolver.getProperty("jpa.hibernate.c3p0.min_size"));
        properties.put("hibernate.c3p0.max_size", springPropertyResolver.getProperty("jpa.hibernate.c3p0.max_size"));
        properties.put("hibernate.c3p0.timeout", springPropertyResolver.getProperty("jpa.hibernate.c3p0.timeout"));
        return properties;
    }

    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    @Primary
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }
}
