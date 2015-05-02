package com.vlls.web;

import com.vlls.exception.*;
import com.vlls.service.CourseService;
import com.vlls.service.LearningCourseService;
import com.vlls.web.model.CoursePageResponse;
import com.vlls.web.model.CourseResponse;
import com.vlls.web.model.UserPageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by hiephn on 2014/09/25.
 */
@Controller
@RequestMapping("/course")
public class CourseController implements ControllerConstant {

    @Autowired
    private CourseService courseService;
    @Autowired
    private LearningCourseService learningCourseService;

    /**
     * This API takes course name, categoryId, langTeachId, langSpeakId as parameters.
     * Before using this API, user must log in. If not, UnauthorizedException is thrown.
     *
     * @param name        name
     * @param categoryId  categoryId
     * @param langTeachId langTeachId
     * @param langSpeakId langSpeakId
     * @param description description
     * @return created course
     */
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CourseResponse create(
            // MANDATORY
            @RequestParam("name") String name,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("langTeachId") Integer langTeachId,
            @RequestParam("langSpeakId") Integer langSpeakId,
            // OPTIONAL
            @RequestParam(value = "description", required = false, defaultValue = EMPTY) String description)
            throws NoInstanceException, UnauthorizedException, ServerTechnicalException, UnauthenticatedException {

        CourseResponse courseResponse = courseService.create(name, categoryId, langTeachId, langSpeakId, description);

        return courseResponse;
    }

    /**
     * This API takes course id, course name, langTeachId, langSpeakId as parameters.
     * User must be the owner of the course to be able to update its information.
     * If not, UnauthenticatedException is thrown.
     * <p>
     * If the course is not found, NoInstanceException is thrown.
     *
     * @param id          id
     * @param name        name
     * @param categoryId  categoryId
     * @param langTeachId langTeachId
     * @param langSpeakId langSpeakId
     * @param description description
     * @return updated course
     */
    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CourseResponse update(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("isPublic") Boolean isPublic,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("langTeachId") Integer langTeachId,
            @RequestParam("langSpeakId") Integer langSpeakId,
            @RequestParam("description") String description) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException, UnauthorizedException {

        CourseResponse courseResponse = courseService.update(id, name, categoryId, langTeachId, langSpeakId, description, isPublic);
        return courseResponse;
    }

    /**
     * This API is created to deactivate course
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
    public CourseResponse deactivateCourse(
            @RequestParam("id") Integer id,
            @RequestParam("isActive") Boolean isActive
    ) throws NoInstanceException, UnauthorizedException, UnauthenticatedException {
        CourseResponse courseResponse = courseService.deactivateCourse(id, isActive);
        return courseResponse;
    }

    /**
     * This API publicly gets course(s) based on the following information:
     * id, name, categoryId, langTeachId, langSpeakId
     * <p>
     * There are many cases to handle:
     * 1/ Search by Id: id is present, don't care other params. Return only one course
     * <p>
     * 2/ If one of categoryId/langTeachId/langSpeakIder presents, returned result must satisfy it.
     * If name presents, returned result's name must contains it (not equal)
     * <p>
     * PAGING: follow page & pageSize
     *
     * @param id          id
     * @param name        name
     * @param categoryId  categoryId
     * @param langTeachId langTeachId
     * @param langSpeakId langSpeakId
     * @return courses
     */
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse get(
            @RequestParam(value = "id", required = false, defaultValue = NEGATIVE) Integer id,
            @RequestParam(value = "name", required = false, defaultValue = EMPTY) String name,
            @RequestParam(value = "categoryId", required = false, defaultValue = NEGATIVE) Integer categoryId,
            @RequestParam(value = "langTeachId", required = false, defaultValue = NEGATIVE) Integer langTeachId,
            @RequestParam(value = "langSpeakId", required = false, defaultValue = NEGATIVE) Integer langSpeakId,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "withLearning", required = false, defaultValue = FALSE) Boolean withLearning,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
            throws UnauthenticatedException, UnauthorizedException, NoInstanceException, InvalidRequestItemException {

        CoursePageResponse coursePageResponse = courseService.get(id, name, categoryId, langTeachId, langSpeakId, page, pageSize, filter, withLearning);

        return coursePageResponse;
    }

    /**
     * This API is created to upload Course's avatar
     *
     * @param multipartFile
     * @param fileName
     * @param htmlFormat
     * @return
     * @throws ServerTechnicalException
     * @throws com.vlls.exception.ClientTechnicalException
     * @throws UnauthenticatedException
     */
    @RequestMapping(value = "/image",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CourseResponse createAvatar(
            @RequestParam("id") Integer courseId,
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("name") String fileName,
            @RequestParam(value = "htmlFormat", required = false, defaultValue = EMPTY) String htmlFormat) throws
            ServerTechnicalException, ClientTechnicalException, NoInstanceException,
            UnauthorizedException, UnauthenticatedException {
        CourseResponse courseResponse = courseService.createAvatar(multipartFile, fileName, htmlFormat, courseId);
        return courseResponse;
    }

    /**
     * Delete the course. Take id as parameter.
     * User must be the owner of the course to be able to delete it.
     * If not, UnauthenticatedException is thrown.
     *
     * @param id id
     * @return operation status
     */
    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String del(@RequestParam("id") Integer id) throws ServerTechnicalException,
            UnauthorizedException, UnauthenticatedException {
        courseService.delete(id);
        return OK_STATUS;
    }

    @RequestMapping(value = "learning", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getLearningCourses(@RequestParam(value = "username") String username,
                                                 @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        CoursePageResponse coursePageResponse = courseService.getLearningCourse(username, page, pageSize);
        return coursePageResponse;
    }

    @RequestMapping(value = "teaching", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getTeachingCourses(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        CoursePageResponse coursePageResponse = courseService.getTeachingCourse(username, page, pageSize);
        return coursePageResponse;
    }

    @RequestMapping(value = "ranking", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LinkedHashMap<String, Object>> getRankingCourseList(
            @RequestParam(value = "courseId") Integer courseId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) throws NoInstanceException {
        List<LinkedHashMap<String, Object>> response = courseService.getLearningCourseRankingList(courseId, page, pageSize);
        return response;
    }

    /**
     * This API is created to get List of similar course
     *
     * @param courseId
     * @return
     * @throws NoInstanceException
     */
    @RequestMapping(value = "/recommend", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CourseResponse> getSimilarCourses(
            @RequestParam(value = "courseId", required = true) Integer courseId
    ) throws NoInstanceException {
        List<CourseResponse> responses = courseService.getSimilarCourses(courseId);
        return responses;
    }

    /**
     * This API is clone of method getSimilarCourses, but return page response
     *
     * @param courseId
     * @return
     * @throws NoInstanceException
     */
    @RequestMapping(value = "/recommend-similar", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse findAllSimilarCourses(
            @RequestParam(value = "courseId", required = true) Integer courseId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws NoInstanceException {
        CoursePageResponse coursePageResponse = courseService.findAllSimilarCourses(courseId, page, pageSize);
        return coursePageResponse;
    }

    @RequestMapping(value = "/popular", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getPopularCourse(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        CoursePageResponse coursePageResponse;
        if (category == null) {
            coursePageResponse = courseService.getPopularCourse(page, pageSize);
        } else {
            coursePageResponse = courseService.getPopularCourseByCategory(category, page, pageSize);
        }
        return coursePageResponse;
    }

    @RequestMapping(value = "/latest", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getLatestCourse(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        CoursePageResponse coursePageResponse;
        if (category == null) {
            coursePageResponse = courseService.getLatestCourse(page, pageSize);
        } else {
            coursePageResponse = courseService.getLatestCourseByCategory(category, page, pageSize);
        }
        return coursePageResponse;
    }

    @RequestMapping(value = "/rating", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CourseResponse rateCourse(Integer courseId, Integer rating)
            throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        return courseService.saveCourseRating(courseId, rating);
    }

/*    @RequestMapping(value = "/similar", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Double cosinSimilar(
            @RequestParam(value = "course1Id")Integer course1Id,
            @RequestParam(value = "course2Id")Integer course2Id
    ) throws NoInstanceException {
        Course course1 = courseService.get0(course1Id);
        Course course2 = courseService.get0(course2Id);

        Double result = courseService.calculateSimilar(course1, course2);
        return result;
    }*/

    /**
     * This API is created to create similarity rate of newest course with others
     *
     * @param course1Id
     * @throws NoInstanceException
     */
/*    @RequestMapping(value = "/createSimilar", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void createSimilarRateList(
            @RequestParam(value = "course1Id")Integer course1Id
    ) throws NoInstanceException {
        Course course = courseService.get0(course1Id);
        courseService.createSimilarRateList(course);
    }*/

    /**
     * This API is created to get list of recommended courses by user rating rate
     *
     * @param page
     * @param pageSize
     * @return
     * @throws ServerTechnicalException
     * @throws NoInstanceException
     * @throws UnauthenticatedException
     */
    @RequestMapping(value = "/recommend/rating-rate", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getSimilarCourseByRatingRate(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        CoursePageResponse responses = courseService.getSimilarCourseByRatingRate(page, pageSize);
        return responses;
    }

    @RequestMapping(value = "/recommend/byUsers", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CourseResponse> getRecommendCoursesByUsers(Integer courseId
    ) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        List<CourseResponse> responses = courseService.getLearnedCourseUserByCourseId(courseId);
        return responses;
    }

    @RequestMapping(value = "/recommend/by-user", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getRecommendCoursesByUsersPage(
            @RequestParam(value = "courseId") Integer courseId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        CoursePageResponse responses = courseService.getLearnedCourseUserByCourseIdPage(courseId, page, pageSize);
        return responses;
    }

    // Get learned courses by userId to recommend
    @RequestMapping(value = "/recommend/also-learn", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getCourseAlsoBeLearned(
            @RequestParam(value = "courseId") Integer courseId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws ServerTechnicalException, NoInstanceException {
        CoursePageResponse coursePageResponse = learningCourseService.getRecommendCoursesByCourseId(courseId, page, pageSize);
        return coursePageResponse;
    }

    @RequestMapping(value = "/recommend/similar", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse findSimilarCourses(
            @RequestParam(value = "courseId") Integer courseId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        CoursePageResponse coursePageResponse = courseService.findAllSimilarCourses(courseId, page, pageSize);
        return coursePageResponse;
    }

    // Get learner by courseId
    @RequestMapping(value = "/learner", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserPageResponse getLearnerInCourse(
            @RequestParam(value = "courseId", required = true) Integer courseId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        UserPageResponse userPageResponse = learningCourseService.getLearnerInCourse(courseId, page, pageSize);
        return userPageResponse;
    }

    @RequestMapping(value = "/creator/currentRecommended")
    @ResponseBody
    public Integer currentRecommended(@RequestParam(value = "userId", required = true) Integer userId) throws IOException {
        return courseService.getCreatorRecommendationCourse(userId);
    }

    @RequestMapping(value = "/creator/updateRecommendation", method = RequestMethod.GET)
    @ResponseBody
    public Boolean updateRecommendation(
            @RequestParam(value = "userId", required = true) Integer userId,
            @RequestParam(value = "courseId", required = true) Integer courseId,
            @RequestParam(value = "isSave", defaultValue = "true") Boolean isSave) {
        return courseService.saveCreatorRecommendation(userId, courseId, isSave);
    }

    @RequestMapping(value = "/learner/updateRecommendation", method = RequestMethod.GET)
    @ResponseBody
    public Boolean updateRecommendationLearner(@RequestParam(value = "courseId", required = true) Integer courseId) {
        return courseService.saveCreatorRecommendation(courseId);
    }

    @RequestMapping(value = "teachingForRecommend", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CoursePageResponse getTeachingCoursesForRecommendation(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        CoursePageResponse coursePageResponse = courseService.getTeachingCourseForRecommendsation(username, page, pageSize);
        return coursePageResponse;
    }
}
