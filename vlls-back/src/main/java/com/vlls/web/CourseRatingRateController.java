package com.vlls.web;

import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.service.CourseRatingRateService;
import com.vlls.web.model.CourseRatingRatePageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by HoangPhi on 11/8/2014.
 */
@Controller
@RequestMapping("/course-rating-rate")
public class CourseRatingRateController implements ControllerConstant {
    @Autowired
    private CourseRatingRateService courseRatingRateService;

    // Get recommended courses by userId
    @RequestMapping(value = "/recommend", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CourseRatingRatePageResponse get(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws UnauthenticatedException, ServerTechnicalException {
        CourseRatingRatePageResponse courseRatingRatePageResponse = courseRatingRateService.getAllByUserId(page, pageSize);
        return courseRatingRatePageResponse;
    }
}
