package com.vlls.web.model;

import com.vlls.jpa.domain.Course;

/**
 * Created by HoangPhi on 9/27/2014.
 */
public class CoursePageResponse extends AbstractDataPageResponse<Course, CourseResponse> {
    @Override
    public CourseResponse instantiateResponse() {
        return new CourseResponse();
    }
}
