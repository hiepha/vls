package com.vlls.web;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.service.PictureService;
import com.vlls.web.model.PicturePageResponse;
import com.vlls.web.model.PictureResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Dell on 10/13/2014.
 */
@Controller
@RequestMapping(value = "/picture")
public class PictureController implements ControllerConstant {

    @Autowired
    private PictureService pictureService;

    @RequestMapping(value = "created", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PicturePageResponse getPicturesCreatedByUser(
            @RequestParam(value = "username") String name,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) throws ServerTechnicalException {
        PicturePageResponse picturePageResponse = pictureService.getPicturesCreatedByUser(name, page, pageSize);
        return picturePageResponse;
    }

    @RequestMapping(value = "liked", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PicturePageResponse getPicturesLikedByUser(
            @RequestParam(value = "username") String name,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) throws ServerTechnicalException {
        PicturePageResponse picturePageResponse = pictureService.getPicturesLikedByUser(name, page, pageSize);
        return picturePageResponse;
    }

    @RequestMapping(value = "popular", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PicturePageResponse getPopularPicturesOfUser(
            @RequestParam(value = "username") String name,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) throws ServerTechnicalException {
        PicturePageResponse picturePageResponse = pictureService.getPopularPicturesOfUser(name, page, pageSize);
        return picturePageResponse;
    }

    @RequestMapping(value = "all-sources", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PicturePageResponse getAllPictureSources(
            @RequestParam(value = "itemId") Integer itemId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return pictureService.getAllPictureSources(itemId, page, pageSize);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PictureResponse post(String source, String format, Integer itemId) throws UnauthenticatedException, ServerTechnicalException {
        PictureResponse pictureResponse = pictureService.create(source, format, itemId);
        return pictureResponse;
    }

    @RequestMapping(value = "getOne", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PictureResponse getOne(Integer pictureId) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        return pictureService.getPictureById(pictureId);
    }

    @RequestMapping(value = "all-pictures", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PicturePageResponse getAllPicturesByItemId(
            @RequestParam(value = "itemId") Integer itemId,
            @RequestParam(value = "pictureId", required = false, defaultValue = NEGATIVE) Integer pictureId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        return pictureService.getAllPicturesByItemId(itemId, pictureId, page, pageSize);
    }

    @RequestMapping(value = "selected-picture", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PicturePageResponse getSelectedPictureByLearningItemId(
            @RequestParam(value = "learningItemId") Integer learningItemId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) throws UnauthenticatedException, ServerTechnicalException {
        return pictureService.getSelectedPictureByLearningItemId(learningItemId, page, pageSize);
    }

    /**
     * Only uploader can delete picture
     *
     * @param id id
     * @return operation status
     */
    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String del(@RequestParam("id") Integer id){
        pictureService.deletePicture(id);
        return OK_STATUS;
    }
}
