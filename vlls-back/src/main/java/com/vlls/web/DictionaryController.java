//package com.vlls.web;
//
//import com.vlls.exception.NoInstanceException;
//import com.vlls.exception.ServerTechnicalException;
//import com.vlls.service.EnViService;
//import com.vlls.service.ViEnService;
//import com.vlls.web.model.DictItemResponse;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by hiephn on 2014/10/04.
// */
//@RequestMapping("/dictionary")
//@Controller
//public class DictionaryController implements ControllerConstant {
//
//    @Autowired
//    private EnViService enViService;
//    @Autowired
//    private ViEnService viEnService;
//
//    @RequestMapping(value = "{dictName}/list",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public List<Map<Integer, String>> list(
//            @PathVariable("dictName") String dictName,
//            @RequestParam("key") String key,
//            @RequestParam(value = "size", defaultValue = "10") int size)
//            throws NoInstanceException {
//        if (StringUtils.equalsIgnoreCase(dictName, "en-vi")) {
//            return enViService.getWordList(key, size);
//        } else if(StringUtils.equalsIgnoreCase(dictName, "vi-en")) {
//            return viEnService.getWordList(key, size);
//        } else {
//            throw new NoInstanceException("Dictionary", dictName);
//        }
//    }
//
//    @RequestMapping(value = "{dictName}",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public List<DictItemResponse> get(
//            @PathVariable("dictName") String dictName,
//            @RequestParam("id") Integer id)
//            throws ServerTechnicalException, NoInstanceException {
//        if (StringUtils.equalsIgnoreCase(dictName, "en-vi")) {
//            return enViService.get(id);
//        } else if (StringUtils.equalsIgnoreCase(dictName, "vi-en")) {
//            return viEnService.get(id);
//        } else {
//            throw new NoInstanceException("Dictionary", dictName);
//        }
//
//    }
//}
