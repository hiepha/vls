package com.vlls.web;

import com.vlls.exception.*;
import com.vlls.service.ItemService;
import com.vlls.web.model.ItemPageResponse;
import com.vlls.web.model.ItemResponse;
import com.vlls.web.model.PicturePageResponse;
import com.vlls.web.model.PictureResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Hoang Phi on 9/28/2014.
 */

@Controller
@RequestMapping("/item")
public class ItemController implements ControllerConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;

    /**
     * Public API.
     * There are 2 cases only:
     *
     * 1/ Get by Id. Return page with only 1 item
     *
     * 2/ Get by Level Id. Return page with list of items in level
     *
     * @param levelId
     * @param id
     * @param page
     * @param pageSize
     * @return
     * @throws MissingServletRequestParameterException
     */
    @RequestMapping(method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ItemPageResponse get(
            @RequestParam(value = "courseId", required = false, defaultValue = NEGATIVE) Integer courseId,
            @RequestParam(value = "levelId", required = false, defaultValue = NEGATIVE) Integer levelId,
            @RequestParam(value = "id", required = false, defaultValue = NEGATIVE) Integer id,
            @RequestParam(value = "filterKey", required = false, defaultValue = EMPTY) String filterKey,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws MissingServletRequestParameterException, NoInstanceException {
        // Validate params
        if (courseId < 0 && levelId < 0 && id < 0) {
            throw new MissingServletRequestParameterException("levelId", "Integer");
        }

        ItemPageResponse itemPageResponse = itemService.get(courseId, levelId, id, filterKey, page, pageSize);
        return itemPageResponse;
    }

    @RequestMapping(method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ItemResponse create(
            // MANDATORY
            @RequestParam("levelId") Integer levelId,
            @RequestParam("name") String name,
            @RequestParam("meaning") String meaning,
            // OPTIONAL
            @RequestParam(value = "type", required = false, defaultValue = EMPTY) String type,
            @RequestParam(value = "pronun", required = false, defaultValue = EMPTY) String pronun,
            @RequestParam(value = "audioName", required = false, defaultValue = EMPTY) String audioName,
            @RequestParam(value = "audioFile", required = false) MultipartFile audioFile
    ) throws NoInstanceException, UnauthenticatedException, UnauthorizedException, ServerTechnicalException,
            DuplicatedItemException, MissingServletRequestParameterException, ClientTechnicalException {

        if (audioFile != null && !audioFile.isEmpty() && StringUtils.isEmpty(audioName)) {
            throw new MissingServletRequestParameterException("audioName", "String");
        }

        ItemResponse itemResponse = itemService.create(levelId, name, meaning, type, pronun, audioName, audioFile);
        return itemResponse;
    }

    /**
     * HTTP PUT does not use HTTP request body to content request data, so the form cannot be read.
     * Use HTTP POST instead.
     *
     * @param id
     * @param name
     * @param meaning
     * @param type
     * @param pronun
     * @param isAudioRemove
     * @param audioName
     * @param audioFile
     * @return
     * @throws NoInstanceException
     * @throws UnauthenticatedException
     * @throws UnauthorizedException
     * @throws MissingServletRequestParameterException
     * @throws ClientTechnicalException
     * @throws ServerTechnicalException
     */
    @RequestMapping(value = "/update",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ItemResponse update(
            // MANDATORY
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("meaning") String meaning,
            // OPTIONAL
            @RequestParam(value = "type", required = false, defaultValue = EMPTY) String type,
            @RequestParam(value = "pronun", required = false, defaultValue = EMPTY) String pronun,
            @RequestParam(value = "isAudioRemoved", required = false, defaultValue = FALSE) Boolean isAudioRemove,
            @RequestParam(value = "audioName", required = false, defaultValue = EMPTY) String audioName,
            @RequestParam(value = "audioFile", required = false) MultipartFile audioFile
    ) throws NoInstanceException, UnauthenticatedException, UnauthorizedException,
            MissingServletRequestParameterException, ClientTechnicalException, ServerTechnicalException {

        if (audioFile != null && !audioFile.isEmpty() && StringUtils.isEmpty(audioName)) {
            throw new MissingServletRequestParameterException("audioName", "String");
        }

        ItemResponse itemResponse = itemService.
                update(id, name, meaning, pronun, type, isAudioRemove, audioName, audioFile);
        return itemResponse;
    }

    @RequestMapping(value = "/status", method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ItemResponse deactivateItem(
            @RequestParam("id") Integer id,
            @RequestParam("isActive") Boolean isActive
    ) throws NoInstanceException, UnauthorizedException, UnauthenticatedException {
        ItemResponse itemResponse = itemService.deactivateItem(id, isActive);
        return itemResponse;
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String delete(@RequestParam("id") Integer id)
            throws UnauthenticatedException, UnauthorizedException, NoInstanceException {
        itemService.delete(id);
        return OK_STATUS;
    }

    ////////////////////// ITEM IMAGE ///////////////////////

    @RequestMapping(value = "/image",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PictureResponse> getImg(
            @RequestParam("itemId") Integer itemId) throws NoInstanceException, UnauthenticatedException, ServerTechnicalException {
        List<PictureResponse> pictureResponses = itemService.getImg(itemId);
        return pictureResponses;
    }

    @RequestMapping(value = "/image",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PictureResponse createImg(
            @RequestParam("itemId") Integer itemId,
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("name") String fileName,
            @RequestParam(value = "htmlFormat", required = false, defaultValue = EMPTY) String htmlFormat)
            throws ServerTechnicalException, ClientTechnicalException, NoInstanceException, UnauthorizedException,
            UnauthenticatedException {
        PictureResponse pictureResponse = itemService.createImg(itemId, multipartFile, fileName, htmlFormat);
        return pictureResponse;
    }

    @RequestMapping(value = "/image",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PictureResponse updateImg(
            @RequestParam("itemId") Integer itemId,
            @RequestParam(value = "htmlFormat", required = false, defaultValue = EMPTY) String htmlFormat)
            throws UnauthenticatedException, NoInstanceException, UnauthorizedException {
        PictureResponse pictureResponse = itemService.updateImg(itemId, htmlFormat);
        return pictureResponse;
    }

    @RequestMapping(value = "/image",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String updateImg(@RequestParam("itemId") Integer itemId)
            throws UnauthenticatedException, NoInstanceException, UnauthorizedException {
        itemService.deleteImg(itemId);
        return OK_STATUS;
    }

}
