package com.vlls.web;

import com.vlls.exception.InvalidRequestItemException;
import com.vlls.exception.NoInstanceException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.exception.UnauthorizedException;
import com.vlls.service.QuestionService;
import com.vlls.web.model.QuestionPageResponse;
import com.vlls.web.model.QuestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hiephn on 2014/10/10.
 */
@Controller
@RequestMapping("/question")
public class QuestionController implements ControllerConstant {

    @Autowired
    private QuestionService questionService;

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public QuestionPageResponse get(
            @RequestParam(value = "id", defaultValue = NEGATIVE) Integer id,
            @RequestParam(value = "itemId", defaultValue = NEGATIVE) Integer itemId)
            throws NoInstanceException, MissingServletRequestParameterException,
            UnauthenticatedException, UnauthorizedException {

        if (id < 0 && itemId < 0) {
            throw new MissingServletRequestParameterException("itemId", "Integer");
        }

        QuestionPageResponse questionPageResponse = questionService.get(itemId, id);
        return questionPageResponse;
    }

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public QuestionResponse create(
            /* MANDATORY */
            @RequestParam("text") String text,
            @RequestParam("type") Integer typeIndex,
            @RequestParam("answerType") String answerTypeString,
            @RequestParam("correctAnswerItemId") Integer correctAnswerItemId,
            /* OPTIONAL */
            @RequestParam(value = "incorrectAnswerType", required = false) Integer incorrectAnswerTypeIndex,
            @RequestParam(value = "incorrectAnswerNum", required = false) Integer incorrectAnswerNum,
            @RequestParam(value = "incorrectAnswerRaw", required = false) String incorrectAnswerRaw,
            @RequestParam(value = "incorrectAnswerItemIds[]", required = false) Integer[] incorrectAnswerItemIds)
            throws NoInstanceException, InvalidRequestItemException {
        QuestionResponse questionResponse = questionService.create(text, typeIndex, answerTypeString,
                incorrectAnswerTypeIndex, incorrectAnswerNum, incorrectAnswerRaw, correctAnswerItemId,
                incorrectAnswerItemIds, false);
        return questionResponse;
    }

    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public QuestionResponse update(
            /* MANDATORY */
            @RequestParam("id") Integer id,
            @RequestParam("text") String text,
            @RequestParam("type") Integer typeIndex,
            @RequestParam("answerType") String answerTypeString,
            @RequestParam("correctAnswerItemId") Integer correctAnswerItemId,
            /* OPTIONAL */
            @RequestParam(value = "incorrectAnswerType", required = false) Integer incorrectAnswerTypeIndex,
            @RequestParam(value = "incorrectAnswerNum", required = false) Integer incorrectAnswerNum,
            @RequestParam(value = "incorrectAnswerRaw", required = false) String incorrectAnswerRaw,
            @RequestParam(value = "incorrectAnswerItemIds[]", required = false) Integer[] incorrectAnswerItemIds)
            throws NoInstanceException, InvalidRequestItemException,
            UnauthenticatedException, UnauthorizedException {
        QuestionResponse questionResponse = questionService.update(id, text, typeIndex, answerTypeString,
                incorrectAnswerTypeIndex, incorrectAnswerNum, incorrectAnswerRaw, correctAnswerItemId,
                incorrectAnswerItemIds, false);
        return questionResponse;
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String delete(
            @RequestParam(value = "id") Integer id)
            throws NoInstanceException, UnauthorizedException, UnauthenticatedException {
        questionService.delete(id);
        return OK_STATUS;
    }
}
