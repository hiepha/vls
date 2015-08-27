//package com.vlls.web;
//
//import com.vlls.exception.UnauthenticatedException;
//import com.vlls.service.MailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.PropertyResolver;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.annotation.Resource;
//import javax.mail.MessagingException;
//import java.io.IOException;
//
///**
// * Created by Dell on 1/9/2015.
// */
//@Controller
//public class MailController {
//
//    @Autowired
//    private MailService mailService;
//
//    @Resource(name = "vllsPropertyResolver")
//    protected PropertyResolver vllsProperties;
//
//    @RequestMapping(value = "/sendMail")
//    public ModelAndView sendMail() throws IOException, MessagingException, UnauthenticatedException {
//        mailService.sendUserReviseItemList();
//        return null;
//    }
//}
