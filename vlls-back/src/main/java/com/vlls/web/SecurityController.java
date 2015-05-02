package com.vlls.web;

import com.vlls.exception.*;
import com.vlls.service.SecurityService;
import com.vlls.web.model.UserResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hiephn on 2014/09/25.
 */
@Controller
@RequestMapping("/security")
public class SecurityController implements ControllerConstant {

    @Autowired
    private SecurityService securityService;

    /**
     * Takes Authorization header (Basic Auth) and authorize user.
     *
     * @return user information
     */
    @RequestMapping(value = "/auth",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponse authorize(
            @RequestHeader(value = "Authorization", required = false, defaultValue = EMPTY) String authorization)
            throws MissingServletRequestParameterException, NoInstanceException, ServerTechnicalException,
            InvalidRequestItemException, UnauthenticatedException {

        if (StringUtils.isEmpty(authorization)) {
            // Hide the Authorization header from being known by unauthorized accesses
            // Make them believe there is a required parameter named 'credentials'
            throw new MissingServletRequestParameterException("credentials", "String");
        }

        UserResponse userResponse = securityService.authenticate(authorization);
        return userResponse;
    }

    /**
     * Logout current user
     *
     * @return API status
     */
    @RequestMapping(value = "/logout",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String logout() {
        securityService.logout();
        return OK_STATUS;
    }

    /**
     * Get current logged in user information
     *
     * @return current logged in user information
     */
    @RequestMapping(value = "/user",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponse user() throws NoInstanceException, UnauthenticatedException, ServerTechnicalException {
        UserResponse userResponse = securityService.retrieveCurrentUser();
        return userResponse;
    }
}
