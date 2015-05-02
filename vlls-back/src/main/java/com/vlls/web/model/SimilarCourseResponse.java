package com.vlls.web.model;

import com.vlls.jpa.domain.SimilarCourse;

/**
 * Created by Hoang Phi on 11/1/2014.
 */
public class SimilarCourseResponse implements DataResponse<SimilarCourse> {
    private Integer courseOne;
    private Integer courseTwo;
    private Double similarRate;

    @Override
    public void setData(SimilarCourse entity) {
        if (entity.getCourseOne() != null) {
            this.courseOne = entity.getCourseOne().getId();
        }
        if (entity.getCourseTwo() != null) {
            this.courseTwo = entity.getCourseTwo().getId();
        }
        if (entity.getSimilarRate() != null){
            this.similarRate = entity.getSimilarRate();
        }
    }

    public Integer getCourseOne() {
        return courseOne;
    }

    public void setCourseOne(Integer courseOne) {
        this.courseOne = courseOne;
    }

    public Integer getCourseTwo() {
        return courseTwo;
    }

    public void setCourseTwo(Integer courseTwo) {
        this.courseTwo = courseTwo;
    }

    public Double getSimilarRate() {
        return similarRate;
    }

    public void setSimilarRate(Double similarRate) {
        this.similarRate = similarRate;
    }
}
