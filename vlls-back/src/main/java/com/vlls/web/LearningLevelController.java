package com.vlls.web;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.exception.UnauthorizedException;
import com.vlls.service.LearningLevelService;
import com.vlls.web.model.LearningLevelPageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hiephn on 2014/10/09.
 */
@Controller
@RequestMapping("/learning-level")
public class LearningLevelController implements ControllerConstant {

    @Autowired
    private LearningLevelService learningLevelService;

    @RequestMapping(method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LearningLevelPageResponse get(
            @RequestParam(value = "id", defaultValue = NEGATIVE) Integer id,
            @RequestParam(value = "learningCourseId", defaultValue = NEGATIVE) Integer learningCourseId,
            @RequestParam(value = "levelId", defaultValue = NEGATIVE) Integer levelId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
            throws NoInstanceException, MissingServletRequestParameterException {

        if (id < 0 && learningCourseId < 0 && levelId < 0) {
            throw new MissingServletRequestParameterException("learningCourseId", "Integer");
        }
        if (levelId > 0 && learningCourseId < 0) {
            throw new MissingServletRequestParameterException("learningCourseId", "Integer");
        }

        LearningLevelPageResponse learningLevelPageResponse = learningLevelService.
                get(id, learningCourseId, levelId, page, pageSize);
        return learningLevelPageResponse;
    }

    @RequestMapping(value = "/test",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String test(
            @RequestParam("id") Integer id) throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        learningLevelService.synchronize(id);
        return OK_STATUS;
    }

    /**
     * This API is created to test status of level
     *
     * //@param levelId
     * @param learningLevelId
     * //@param learningCourseId
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
//     */
//    @RequestMapping(value = "/testStatus",
//            method = RequestMethod.PUT,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public Integer getLevelStatus(
//            @RequestParam(value = "levelId", required = true) Integer levelId,
//            @RequestParam(value = "learningLevelId", required = true) Integer learningLevelId,
//            @RequestParam(value = "learningCourseId", required = true) Integer learningCourseId
//    ) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
//        return learningLevelService.getLevelStatus(levelId, learningLevelId, learningCourseId);
//    }

    /**
     * This API is created to get number of learning item need to be revised in learning level
     *
     * @param learningLevelId
     * @return
     */
    @RequestMapping(value = "/reviseItems",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Integer getLevelStatus(
            @RequestParam(value = "Id", required = true) Integer learningLevelId
    ) {
        return learningLevelService.getNumOfReviseItems(learningLevelId);
    }
}
