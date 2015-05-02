package com.vlls.jpa.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by HoangPhi on 11/9/2014.
 */
public class CourseRatingRateKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private Integer courseId;

    public CourseRatingRateKey(){

    }

    public Integer getUserId() {
        return userId;
    }

    public CourseRatingRateKey(Integer userId, Integer courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return true;
    }

    public int hashCode(){
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.userId)
                .append(this.courseId)
                .toHashCode();
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
}
