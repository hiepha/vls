package com.vlls.web.model;

import com.vlls.jpa.domain.LearningCourse;

/**
 * Created by Hoang Phi on 10/1/2014.
 */
public class LearningCoursePageResponse extends AbstractDataPageResponse<LearningCourse, LearningCourseResponse> {
    private Boolean simpleResponse;

    public LearningCoursePageResponse(Boolean simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    public LearningCoursePageResponse() {
        this.simpleResponse = false;
    }

    @Override
    public LearningCourseResponse instantiateResponse() {
        return new LearningCourseResponse(this.simpleResponse);
    }
}
