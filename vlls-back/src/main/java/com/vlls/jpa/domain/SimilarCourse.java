package com.vlls.jpa.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Hoang Phi on 11/1/2014.
 */
@Entity
@IdClass(SimilarCourseKey.class)
public class SimilarCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "course_one_id")
    private Integer courseOneId;

    @Id
    @Column(name = "course_two_id")
    private Integer courseTwoId;

    @Column
    private Double similarRate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_one_id", insertable = false, updatable = false)
    private Course courseOne;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_two_id", insertable = false, updatable = false)
    private Course courseTwo;

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

    public Double getSimilarRate() {
        return similarRate;
    }

    public void setSimilarRate(Double similarRate) {
        this.similarRate = similarRate;
    }

    public Course getCourseOne() {
        return courseOne;
    }

    public void setCourseOne(Course courseOne) {
        this.courseOne = courseOne;
    }

    public Course getCourseTwo() {
        return courseTwo;
    }

    public void setCourseTwo(Course courseTwo) {
        this.courseTwo = courseTwo;
    }
}
