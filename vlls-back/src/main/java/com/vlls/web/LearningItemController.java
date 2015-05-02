package com.vlls.web;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.exception.UnauthorizedException;
import com.vlls.service.LearningItemService;
import com.vlls.web.model.LearnQuizItemResponse;
import com.vlls.web.model.LearningItemPageResponse;
import com.vlls.web.model.PictureResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Dell on 10/5/2014.
 */
@Controller
@RequestMapping(value = "/learning-item")
public class LearningItemController implements ControllerConstant {

    @Autowired
    private LearningItemService learningItemService;

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LearningItemPageResponse get(
            @RequestParam(value = "id", defaultValue = NEGATIVE) Integer id,
            @RequestParam(value = "learningLevelId", defaultValue = NEGATIVE) Integer learningLevelId,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
            throws MissingServletRequestParameterException, NoInstanceException {

        if (id < 0 && learningLevelId < 0) {
            throw new MissingServletRequestParameterException("learningLevelId", "Integer");
        }
        LearningItemPageResponse learningItemPageResponse = learningItemService.get(id, learningLevelId, page, pageSize);

        return learningItemPageResponse;
    }

    @RequestMapping(value = "/turn",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LearnQuizItemResponse> getTurn(
            @RequestParam(value = "learningCourseId", defaultValue = NEGATIVE) Integer learningCourseId,
            @RequestParam(value = "learningLevelId", defaultValue = NEGATIVE) Integer learningLevelId,
            @RequestParam("isRevise") Boolean isRevise)
            throws UnauthenticatedException, ServerTechnicalException, NoInstanceException, UnauthorizedException,
            MissingServletRequestParameterException {
        if (learningCourseId < 0 && learningLevelId < 0) {
            throw new MissingServletRequestParameterException("learningCourseId", "Integer");
        } else {
            List<LearnQuizItemResponse> learnQuizItemResponses;
            if (isRevise) {
                if (learningLevelId > 0) {
                    learnQuizItemResponses = learningItemService.getLearningLevelReviseTurn(learningLevelId);
                } else {
                    learnQuizItemResponses = learningItemService.getLearningCourseReviseTurn(learningCourseId);
                }
            } else {
                if (learningLevelId > 0) {
                    learnQuizItemResponses = learningItemService.getLearningLevelTurn(learningLevelId);
                } else {
                    learnQuizItemResponses = learningItemService.getLearningCourseTurn(learningCourseId);
                }
            }
            return learnQuizItemResponses;
        }
    }

    @RequestMapping(value = "/turn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String submitTurn(
            @RequestParam("itemResult") String itemResult)
            throws UnauthenticatedException, ServerTechnicalException, NoInstanceException, UnauthorizedException {
        learningItemService.submitTurn(itemResult);
        return OK_STATUS;
    }

    /*@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LearningItemResponse create()*/


//    @RequestMapping(method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public LearningItemsResponse get(
//            @RequestParam(value = "userId") Integer userId,
//            @RequestParam(value = "levelId") Integer levelId,
//            @RequestParam(value = "courseId") Integer courseId,
//            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
//            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
//            throws NoInstanceException {
//        LearningItemsResponse response = learningItemService.get(userId, courseId, levelId, page, pageSize);
//        return response;
//    }
//
//    @RequestMapping(value = "create", method = RequestMethod.GET)
//    @ResponseBody
//    public Boolean create(int userId, int courseId) {
//        return learningItemService.create(userId, courseId);
//    }
//
//    @RequestMapping(value = "{levelId}", method = RequestMethod.GET)
//    @ResponseBody
//    public LearningItemPageResponse getLearningItemsByLevel(
//            @PathVariable(value = "levelId") final int levelId,
//            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
//            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize)
//            throws NoInstanceException {
//        return learningItemService.get(levelId, page, pageSize);
//    }

    /**
     * This API is created to test number of learned items in learning level
     *
     * @param learningLevelId
     */
    @RequestMapping(value = "/testLvItem", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Integer getNumOfLearnedItemInLevel(
            @RequestParam(value = "learningLevelId", required = true) Integer learningLevelId
    ) throws UnauthenticatedException, ServerTechnicalException {
        return learningItemService.getNumOfLearnedItemInLevel(learningLevelId);
    }

    /**
     * This API is created to get next revise time of learning item
     * @param learningItemId
     * @return
     */
/*    @RequestMapping(value = "/revise",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getNextReviseTime(
            @RequestParam("Id") Integer learningItemId){
        learningItemService.calculateReviseTime(learningItemId);
        return OK_STATUS;
    }*/

    @RequestMapping(value = "/set_picture",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PictureResponse setLearningItemPicture (
            @RequestParam(value = "pictureId", defaultValue = NEGATIVE) Integer pictureId,
                                         Integer learningItemId)
            throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        return learningItemService.setLearningItemPicture(pictureId, learningItemId);
    }
}
