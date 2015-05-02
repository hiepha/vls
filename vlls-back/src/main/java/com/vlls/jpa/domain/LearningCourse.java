package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Hoang Phi on 10/1/2014.
 */
@Entity
public class LearningCourse extends AbstractPersistable<Integer> {
    private static final long serialVersionUID = 1L;

    @Column
    private Boolean pinStatus;
    @Column
    private Integer point;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSync;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "learningCourse",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<LearningLevel> learningLevels;
    @Column
    private Integer rating;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextReviseTime;

    public Boolean getPinStatus() {
        return pinStatus;
    }

    public void setPinStatus(Boolean pinStatus) {
        this.pinStatus = pinStatus;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<LearningLevel> getLearningLevels() {
        return learningLevels;
    }

    public void setLearningLevels(List<LearningLevel> learningLevels) {
        this.learningLevels = learningLevels;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getNextReviseTime() {
        return nextReviseTime;
    }

    public void setNextReviseTime(Date nextReviseTime) {
        this.nextReviseTime = nextReviseTime;
    }
}
