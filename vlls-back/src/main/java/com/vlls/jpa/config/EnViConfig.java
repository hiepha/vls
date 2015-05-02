package com.vlls.jpa.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.PropertyResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hiephn on 2014/10/03.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.vlls.jpa.dict.envi"},
        entityManagerFactoryRef = "enviEntityManagerFactory",
        transactionManagerRef = "transactionManager")
@ConditionalOnWebApplication
public class EnViConfig {

    @Resource(name = "hibernateProperties")
    private Map<String, String> hibernateProperties;

    @ConfigurationProperties(prefix = "spring.enviDatasource")
    @Bean
    public DataSource enviDataSource() {
        DataSourceBuilder factory = DataSourceBuilder.create();
        return factory.build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean enviEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(enviDataSource())
                .packages("com.vlls.jpa.dict.envi")
                .persistenceUnit("envi")
                .properties(hibernateProperties)
                .build();
    }
}
