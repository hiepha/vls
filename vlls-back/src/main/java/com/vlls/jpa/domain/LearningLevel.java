package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by hiephn on 2014/10/07.
 */
@Entity
public class LearningLevel extends AbstractPersistable<Integer> {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_course_id")
    private LearningCourse learningCourse;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "learningLevel",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH})
    private List<LearningItem> learningItems;
    @Column
    private Integer progress;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSync;

    public LearningCourse getLearningCourse() {
        return learningCourse;
    }

    public void setLearningCourse(LearningCourse learningCourse) {
        this.learningCourse = learningCourse;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<LearningItem> getLearningItems() {
        return learningItems;
    }

    public void setLearningItems(List<LearningItem> learningItems) {
        this.learningItems = learningItems;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
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
}
