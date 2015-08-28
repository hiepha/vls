package com.vlls.web.config;

import com.vlls.web.filter.HttpDeleteFormContentFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.Assert;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.AppCacheManifestTransformer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolver;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;

/**
 * Created by hiephn on 2014/07/23.
 */
@Configuration
@ConditionalOnWebApplication
public class WebConfig extends WebMvcConfigurerAdapter implements EnvironmentAware {

    private Environment environment;

    @Value("${resources.projectroot:}")
    private String projectRoot;

    @Value("${vlls.version:}")
    private String appVersion;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public PropertyResolver vllsPropertyResolver() {
        return new RelaxedPropertyResolver(environment, "vlls.");
    }

    @Bean
    public PropertyResolver vllsMessagesPropertyResolver() {
        return new RelaxedPropertyResolver(environment, "vlls.messages.");
    }

    @Bean
    public PropertyResolver springPropertyResolver() {
        return new RelaxedPropertyResolver(environment, "spring.");
    }

    @Bean
    public Filter httpPutFormContentFilter() {
        return new HttpPutFormContentFilter();
    }

    @Bean
    public Filter httpDeleteFormContentFilter() {
        return new HttpDeleteFormContentFilter();
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Bean
    public FilterRegistrationBean httpPutFormContentFilterRegistrationBean () {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(httpPutFormContentFilter());
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean httpDeleteFormContentFilterRegistrationBean () {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(httpDeleteFormContentFilter());
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean characterEncodingFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(characterEncodingFilter());
        return registrationBean;
    }

//    @Bean
//    public CommonsFileUploadSupport commonsMultipartResolver() {
//        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
//        commonsMultipartResolver.setMaxUploadSize(10000000l);
//        return commonsMultipartResolver;
//    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("10MB");
        factory.setMaxRequestSize("10MB");
        return factory.createMultipartConfig();
    }

    private String getProjectRootRequired() {
        Assert.state(this.projectRoot != null, "Please set \"resources.projectRoot\" in application.yml");
        return this.projectRoot;
    }

    protected String getApplicationVersion() {
        return this.environment.acceptsProfiles("dev") ? "dev" : this.appVersion;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        boolean devMode = true;
//        boolean devMode = this.environment.acceptsProfiles("dev");

        String location = "file://" + getProjectRootRequired();
        Integer cachePeriod = devMode ? 0 : null;
        boolean useResourceCache = !devMode;

        AppCacheManifestTransformer appCacheTransformer = new AppCacheManifestTransformer();
        ResourceResolver resourceResolver = new PathResourceResolver();

        registry.addResourceHandler("/**")
                .addResourceLocations(location)
                .setCachePeriod(cachePeriod)
                .resourceChain(useResourceCache)
                .addResolver(resourceResolver)
                .addTransformer(appCacheTransformer);
    }
}
