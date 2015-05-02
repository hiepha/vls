package com.vlls.web.model;

import com.vlls.jpa.domain.LearningCourse;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hoang Phi on 10/1/2014.
 */
public class LearningCourseResponse implements DataResponse<LearningCourse> {

    private static final SimpleDateFormat SDFT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Integer id;
    private Boolean pinStatus;
    private String userName;
    private CourseResponse course;
    private Integer point;
    private String lastUpdate;
    private String lastSync;
    private Integer rating;
    private Integer numOfReviseItem;
    private Integer totalItems;
    private Integer totalLearntItems;

    private boolean simpleResponse;

    public LearningCourseResponse() {
    }

    public LearningCourseResponse(boolean simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    @Override
    public void setData(LearningCourse entity) {
        this.id = entity.getId();
        if (entity.getUser() != null) {
            this.userName = entity.getUser().getName();
        }
        this.pinStatus = entity.getPinStatus();
        this.point = entity.getPoint();
        if (!this.simpleResponse) {
            this.course = new CourseResponse(true);
            this.course.setData(entity.getCourse());
        }
        if (entity.getLastUpdate() != null) {
            this.lastUpdate = SDFT.format(new Date(entity.getLastUpdate().getTime()));
        }
        if (entity.getLastSync() != null) {
            this.lastSync = SDFT.format(new Date(entity.getLastSync().getTime()));
        }
        if (entity.getRating() != null) {
            this.setRating(entity.getRating());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getPinStatus() {
        return pinStatus;
    }

    public void setPinStatus(Boolean pinStatus) {
        this.pinStatus = pinStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public CourseResponse getCourse() {
        return course;
    }

    public void setCourse(CourseResponse course) {
        this.course = course;
    }


    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getNumOfReviseItem() {
        return numOfReviseItem;
    }

    public void setNumOfReviseItem(Integer numOfReviseItem) {
        this.numOfReviseItem = numOfReviseItem;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getTotalLearntItems() {
        return totalLearntItems;
    }

    public void setTotalLearntItems(Integer totalLearntItems) {
        this.totalLearntItems = totalLearntItems;
    }
}
