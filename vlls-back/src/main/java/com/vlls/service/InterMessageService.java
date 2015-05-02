package com.vlls.service;

import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by hiephn on 2014/10/23.
 */
@Service
public class InterMessageService extends AbstractService {

    @Resource(name = "vllsMessagesPropertyResolver")
    private PropertyResolver properties;

    private String getDefaultLocale() {
        return this.properties.getProperty("default");
    }

    private String getDefaultLocale(String key) {
        return this.properties.getProperty(this.getDefaultLocale() + "." + key);
    }

    public String getMessage(String code, String key) {
        String message = this.properties.getProperty(lowerCase(code) + '.' + key);
        if (isEmpty(message)) {
            message = "";
        }
        return message;
    }
}
