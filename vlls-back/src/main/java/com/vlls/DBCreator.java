package com.vlls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by hiephn on 2014/06/28.
 */
@Configuration
@Profile("initdb")
@ConfigurationProperties
public class DBCreator implements EnvironmentAware {

    private RelaxedPropertyResolver props;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void setEnvironment(Environment environment) {
        this.props = new RelaxedPropertyResolver(environment, "spring.datasource.");
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DataSourceAutoConfiguration.class, DBCreator.class);
        app.setAdditionalProfiles("initdb");
        app.setShowBanner(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @PostConstruct
    protected void initialize() throws IOException {
        Resource schemaResource = this.applicationContext.getResource("file:dbscripts/schema.sql");
        Resource[] dataResources = this.applicationContext.getResources("file:dbscripts/data/*.sql");
        Resource[] testResources = this.applicationContext.getResources("file:dbscripts/test/*.sql");

        Arrays.sort(dataResources, (a, b) -> a.getFilename().compareTo(b.getFilename()));
        Arrays.sort(testResources, (a, b) -> a.getFilename().compareTo(b.getFilename()));

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSqlScriptEncoding("UTF-8");
        populator.addScript(schemaResource);
        for (Resource resource : dataResources) {
            if (resource.exists()) {
                populator.addScript(resource);
            }
        }
        for (Resource resource : testResources) {
            if (resource.exists()) {
                populator.addScript(resource);
            }
        }

        DatabasePopulatorUtils.execute(populator, this.dataSource);
    }
}
