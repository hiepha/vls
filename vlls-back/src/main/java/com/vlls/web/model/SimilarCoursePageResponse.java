package com.vlls.web.model;

import com.vlls.jpa.domain.SimilarCourse;

/**
 * Created by Hoang Phi on 11/1/2014.
 */
public class SimilarCoursePageResponse extends AbstractDataPageResponse<SimilarCourse, SimilarCourseResponse> {
    @Override
    public SimilarCourseResponse instantiateResponse() {
        return new SimilarCourseResponse();
    }
}
