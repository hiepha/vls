package com.vlls.web;

import com.vlls.exception.*;
import com.vlls.service.LearningCourseService;
import com.vlls.web.model.CoursePageResponse;
import com.vlls.web.model.LearningCoursePageResponse;
import com.vlls.web.model.LearningCourseResponse;
import com.vlls.web.model.LearningCourseStatisticResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Hoang Phi on 10/2/2014.
 */
@Controller
@RequestMapping("/learning-course")
public class LearningCourseController implements ControllerConstant {

    @Autowired
    private LearningCourseService learningCourseService;

    /**
     * This API is get learningClass based on:
     * userId, courseId
     *
     * @param courseId
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LearningCoursePageResponse get(
            @RequestParam(value = "id", required = false, defaultValue = NEGATIVE) Integer id,
            @RequestParam(value = "courseId", required = false, defaultValue = NEGATIVE) Integer courseId,
            @RequestParam(value = "userId", required = false, defaultValue = NEGATIVE) Integer userId,
            @RequestParam(value = "withCourse", required = false, defaultValue = FALSE) Boolean withCourse,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
            throws UnauthenticatedException, NoInstanceException, UnauthorizedException {

        LearningCoursePageResponse learningClassPageResponse = learningCourseService.get(id, courseId, userId, withCourse,
                page, pageSize);
        return learningClassPageResponse;
    }

    /**
     * This API is created to add new learningClass
     *
     * @param courseId
     * @return
     * @throws ServerTechnicalException
     * @throws NoInstanceException
     * @throws UnauthorizedException
     * @throws DuplicatedItemException
     */
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LearningCourseResponse create(
            @RequestParam("courseId") Integer courseId,
            @RequestParam(value = "pinStatus", defaultValue = TRUE) Boolean pinStatus
    ) throws ServerTechnicalException, NoInstanceException, UnauthorizedException, DuplicatedItemException,
            UnauthenticatedException {
        LearningCourseResponse learningClassResponse = learningCourseService.create(courseId, pinStatus);
        return learningClassResponse;
    }

    /**
     * This API is created to update exist learningClass
     *
     * @param courseId
     * @param pinStatus
     * @return
     * @throws ServerTechnicalException
     * @throws NoInstanceException
     * @throws UnauthorizedException
     * @throws DuplicatedItemException
     */
    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LearningCourseResponse update(
            @RequestParam("courseId") Integer courseId,
            @RequestParam("pinStatus") Boolean pinStatus
    ) throws ServerTechnicalException, NoInstanceException, UnauthorizedException, DuplicatedItemException,
            UnauthenticatedException {
        LearningCourseResponse learningClassResponse = learningCourseService.update(courseId, pinStatus);
        return learningClassResponse;
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object delete(
            @RequestParam("id") Integer id) {
        learningCourseService.delete(id);
        return OK_STATUS;
    }

    @RequestMapping(value = "status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LinkedHashMap<String, Integer> getLearningStatus(Integer courseId) throws UnauthenticatedException, ServerTechnicalException {
        return learningCourseService.getLearningCourseStatus(courseId);
    }

    @RequestMapping(value = "learner-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LinkedHashMap<String, Integer> getLearnerLearningStatus(Integer courseId, Integer id) {
        return learningCourseService.getLearnerLearningCourseStatus(courseId, id);
    }

    @RequestMapping(value = "/difficult", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List getDifficultWords(
            @RequestParam(value = "id") int learningCourseId
    ) throws UnauthenticatedException, ServerTechnicalException {
        List response = learningCourseService.getDifficultWords(learningCourseId);
        return response;
    }

    @RequestMapping(value = "/statistic", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LearningCourseStatisticResponse getStatistic(
            @RequestParam(value = "id") int learningCourseId) throws NoInstanceException {
        LearningCourseStatisticResponse statisticResponse = learningCourseService.
                getStatistic(learningCourseId);
        return statisticResponse;
    }

    // Get learned courses by userId
    @RequestMapping(value = "/remainCourse", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getLearnedCourseByUserId(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        CoursePageResponse coursePageResponse = learningCourseService.getRemainCourseIdByUserId(page, pageSize);
        return coursePageResponse;
    }

    // Get learned courses by userId to recommend
    @RequestMapping(value = "/also-learn", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getLearnedCourseByUserId(
            @RequestParam(value = "courseId") Integer courseId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        return learningCourseService.getRecommendCoursesByCourseId(courseId, page, pageSize);
    }

    @RequestMapping(value = "/learning", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LearningCoursePageResponse getLearningCourseOfCurrentUser(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        return learningCourseService.getLearningCourseOfCurrentUser(page, pageSize);
    }

    @RequestMapping(value = "/quit", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean quitLearningCourse(@RequestParam(value = "learningCourseId") Integer id) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        learningCourseService.quitCourse(id);
        return true;
    }
}
