package com.vlls.web.model;

import com.vlls.jpa.domain.CourseRatingRate;

/**
 * Created by HoangPhi on 11/8/2014.
 */
public class CourseRatingRateResponse implements DataResponse<CourseRatingRate> {
    private Integer user;
    private Integer course;
    private Double ratingSimilar;

    @Override
    public void setData(CourseRatingRate entity) {
        this.user = entity.getUser().getId();
        this.course = entity.getCourse().getId();
        this.ratingSimilar = entity.getRatingSimilar();
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public Double getRatingSimilar() {
        return ratingSimilar;
    }

    public void setRatingSimilar(Double ratingSimilar) {
        this.ratingSimilar = ratingSimilar;
    }
}
