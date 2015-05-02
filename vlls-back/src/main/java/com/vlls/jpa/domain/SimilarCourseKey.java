package com.vlls.jpa.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by Hoang Phi on 11/1/2014.
 */
public class SimilarCourseKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer courseOneId;
    private Integer courseTwoId;

    public SimilarCourseKey(){

    }

    public SimilarCourseKey(Integer courseOneId, Integer courseTwoId) {
        this.courseOneId = courseOneId;
        this.courseTwoId = courseTwoId;
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
                .append(this.courseOneId)
                .append(this.courseTwoId)
                .toHashCode();
    }


    public Integer getCourseOneId() {
        return courseOneId;
    }

    public void setCourseOneId(Integer courseOneId) {
        this.courseOneId = courseOneId;
    }

    public Integer getCourseTwoId() {
        return courseTwoId;
    }

    public void setCourseTwoId(Integer courseTwoId) {
        this.courseTwoId = courseTwoId;
    }
}
