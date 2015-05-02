package com.vlls.jpa.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

/**
* Created by thongvh on 2014/10/23.
*/
@Configuration
@EnableJpaRepositories(basePackages = {"com.vlls.jpa.dict.vien"},
        entityManagerFactoryRef = "vienEntityManagerFactory",
        transactionManagerRef = "transactionManager")
@ConditionalOnWebApplication
public class ViEnConfig {

    @Resource(name = "hibernateProperties")
    private Map<String, String> hibernateProperties;

    @ConfigurationProperties(prefix = "spring.vienDatasource")
    @Bean
    public DataSource vienDataSource() {
        DataSourceBuilder factory = DataSourceBuilder.create();
        return factory.build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean vienEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(vienDataSource())
                .packages("com.vlls.jpa.dict.vien")
                .persistenceUnit("vien")
                .properties(hibernateProperties)
                .build();
    }
}
