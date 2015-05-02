package com.vlls.web;

import com.vlls.exception.*;
import com.vlls.service.LevelService;
import com.vlls.web.model.LevelPageResponse;
import com.vlls.web.model.LevelResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/level")
public class LevelController implements ControllerConstant {

    @Autowired
    private LevelService levelService;

    /**
     * Only admin can create level.
     *
     * @param name        name
     * @param description description
     * @return create level
     */
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LevelResponse create(
            // MANDATORY
            @RequestParam("name") String name,
            @RequestParam("courseId") Integer courseId,
            // OPTIONAL
            @RequestParam(value = "description", required = false, defaultValue = EMPTY) String description)
            throws NoInstanceException, InvalidRequestItemException, ServerTechnicalException,
            UnauthorizedException, UnauthenticatedException {
        LevelResponse levelResponse = levelService.create(name, description, courseId);

        return levelResponse;
    }

    /**
     * Only member can update Level.
     *
     * @param id          id
     * @param name        name
     * @param description description
     * @return updated level
     */
    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LevelResponse update(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description)
            throws NoInstanceException, InvalidRequestItemException, ServerTechnicalException,
            UnauthorizedException, UnauthenticatedException {

        LevelResponse levelResponse = levelService.update(id, name, description);
        return levelResponse;
    }

    /**
     * This API is created to deactivate level
     *
     * @param id
     * @param isActive
     * @return
     * @throws NoInstanceException
     * @throws UnauthorizedException
     * @throws UnauthenticatedException
     */
    @RequestMapping(value = "/status",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LevelResponse deactivateLevel(
            @RequestParam("id") Integer id,
            @RequestParam("isActive") Boolean isActive,
            @RequestParam(value = "itemId", required = false) Integer itemId
    ) throws NoInstanceException, UnauthorizedException, UnauthenticatedException {
        LevelResponse levelResponse = levelService.deactivateLevel(id, itemId, isActive);
        return levelResponse;
    }

    /**
     * Public API.
     * <p>
     * 1/ If id is present. Return page with only 1 level
     * <p>
     * 2/ If only name is present. Return page of level that have name in equal.
     * <p>
     * 3/ If none of parameter is present. Return page of all level.
     *
     * @param id id
     * @return level
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LevelPageResponse get(
            @RequestParam(value = "id", required = false, defaultValue = NEGATIVE) Integer id,
            @RequestParam(value = "courseId", required = false, defaultValue = EMPTY) Integer courseId)
            throws MissingServletRequestParameterException {

        if (id < 0 && courseId < 0) {
            throw new MissingServletRequestParameterException("courseId", "Integer");
        }

        LevelPageResponse levelPageResponse = levelService.getLevel(id, courseId);
        return levelPageResponse;
    }

    /**
     * Only member can delete level
     *
     * @param id id
     * @return operation status
     */
    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String del(@RequestParam("id") Integer id) throws com.vlls.exception.UnsupportedOperationException,
            ServerTechnicalException, UnauthorizedException, UnauthenticatedException {
        boolean success = levelService.delete(id);
        if (success) return OK_STATUS;
        return NOT_OK_STATUS;
    }

    /**
     * Receive item csv file from creator and import it to level.
     *
     * @param itemsFile
     * @param id
     * @return
     */
    @RequestMapping(value = "/csv",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, headers = "content-type=multipart/*")
    @ResponseBody
    public String importItems(
            @RequestParam("file") MultipartFile itemsFile,
            @RequestParam("id") Integer id
    ) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException, UnauthorizedException, ClientTechnicalException, DuplicatedItemException {
        return levelService.importItems(itemsFile, id);
    }

    /**
     * Export items in level
     *
     * @param id
     * @return exported file
     */
    @RequestMapping(value = "/csv",
            method = RequestMethod.GET,
            produces = "application/csv")
    @ResponseBody
    public Object exportItems(
            @RequestParam("id") Integer id,
            HttpServletResponse response
    ) throws NoInstanceException, ServerTechnicalException, IOException {
        File file = levelService.exportItems(id);

        String header = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"Level-%d_items.csv\"", id);
        response.setHeader(header, headerValue);

        FileUtils.copyFile(file, response.getOutputStream());
        return null;
    }

    /**
     * This API is created to test number of items in level
     *
     * @param levelId
     * @return
     */
    @RequestMapping(value = "/totalLvItem", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Integer getNumOfLevelItem(
            @RequestParam(value = "levelId") Integer levelId
    ) {
        return levelService.getNumOfLevelItem(levelId);
    }
}
