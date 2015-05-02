package com.vlls.web;

import com.vlls.exception.ServerTechnicalException;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hiephn on 2014/10/20.
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {DataIntegrityViolationException.class, DataAccessException.class, DataException.class})
    @ResponseBody
    private Map<String, String> dbErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it.
        // AnnotationUtils is a Spring Framework utility class.
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;

        LOGGER.error(e.getMessage(), e);

        // Otherwise setup and send the user to a default error-view.
        Map<String, String> map = new HashMap<>();
        map.put("exception", ServerTechnicalException.class.getName());
        map.put("url", req.getRequestURL().toString());
        map.put("message", "Error while processing your request. Sorry please try again later.");
        map.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        map.put("status", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return map;
    }
}
