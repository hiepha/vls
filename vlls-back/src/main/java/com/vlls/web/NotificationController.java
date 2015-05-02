package com.vlls.web;

import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.service.NotificationService;
import com.vlls.web.model.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by hiephn on 2014/11/30.
 */
@Controller
@RequestMapping("/notification")
public class NotificationController implements ControllerConstant {

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<NotificationResponse> get(
            @RequestParam(value = "from", defaultValue = "0") long from)
            throws UnauthenticatedException, ServerTechnicalException {
        return notificationService.get(from);
    }

    @RequestMapping(value = "clearLog", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean clearLog(@RequestParam(value = "username") String user) throws UnauthenticatedException, ServerTechnicalException {
        return notificationService.clearLog(user);
    }
}
