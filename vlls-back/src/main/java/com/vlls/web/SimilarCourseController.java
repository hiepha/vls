package com.vlls.web;

import com.vlls.service.SimilarCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Hoang Phi on 11/1/2014.
 */
@Controller
@RequestMapping("/similar-course")
public class SimilarCourseController implements ControllerConstant {
    @Autowired
    private SimilarCourseService similarCourseService;

    public Object get() {

        return null;
    }

    public Object create() {

        return null;
    }

    public Object update() {

        return null;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void delete(
            @RequestParam(value = "courseId", required = true) Integer courseId
    ) {
            similarCourseService.deleteSimilarCourse(courseId);
    }

}
