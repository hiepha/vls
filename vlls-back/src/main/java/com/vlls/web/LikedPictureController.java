package com.vlls.web;

import com.vlls.exception.*;
import com.vlls.service.LikedPictureService;
import com.vlls.web.model.LikedPicturePageResponse;
import com.vlls.web.model.LikedPictureResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;

/**
 * Created by HoangPhi on 10/13/2014.
 */
@Controller
@RequestMapping("/liked_picture")
public class LikedPictureController implements ControllerConstant{

    @Autowired
    private LikedPictureService likedPictureService;

    @RequestMapping(method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LikedPicturePageResponse get(
            @RequestParam(value = "pictureId", required = false, defaultValue = NEGATIVE) Integer pictureId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws UnauthenticatedException {
        LikedPicturePageResponse likedPicturePageResponse = likedPictureService.get(pictureId, page, pageSize);
        return likedPicturePageResponse;
    }

    @RequestMapping(method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LikedPictureResponse create(
            @RequestParam(value = "pictureId") Integer pictureId
    ) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException, DuplicatedItemException {
        LikedPictureResponse likedPictureResponse = likedPictureService.create(pictureId);
        return likedPictureResponse;
    }

    public Object update() {

        return null;
    }

    /*public Object delete(
            @RequestParam(value = "pictureId") Integer pictureId
    ) throws ServerTechnicalException, UnauthorizedException, UnauthenticatedException {
        LikedPictureResponse likedPictureResponse = likedPictureService.delete(pictureId);
    }*/
	
    @RequestMapping(value = "checkStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LinkedHashMap<String, Object> checkStatus(Integer pictureId) throws UnauthenticatedException, ServerTechnicalException {
        return likedPictureService.checkStatus(pictureId);
    }

}
