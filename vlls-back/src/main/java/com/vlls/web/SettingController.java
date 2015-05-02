package com.vlls.web;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.service.SettingService;
import com.vlls.web.model.SettingPageResponse;
import com.vlls.web.model.SettingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by HoangPhi on 10/11/2014.
 */
@Controller
@RequestMapping("/setting")
public class SettingController implements ControllerConstant {

    @Autowired
    private SettingService settingService;

    @RequestMapping(method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SettingPageResponse get(
    ) throws UnauthenticatedException, ServerTechnicalException {
        SettingPageResponse settingPageResponse = settingService.get();
        return settingPageResponse;
    }

    @RequestMapping(method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SettingResponse create(
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "value", required = true) String value
    ) throws UnauthenticatedException, ServerTechnicalException {
        SettingResponse settingResponse = settingService.create(name, value);
        return settingResponse;
    }

    @RequestMapping(method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SettingResponse update(
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "value", required = true) String value
    ) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        SettingResponse settingResponse = settingService.update(name, value);
        return settingResponse;
    }

    @RequestMapping(method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void delete(
            @RequestParam(value = "name", required = true) String name
    ) throws UnauthenticatedException, ServerTechnicalException {
        settingService.delete(name);
    }
}
