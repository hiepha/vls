package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by TuanNKA on 10/3/2014.
 */
@Entity
public class LearningItem extends AbstractPersistable<Integer>{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_level_id")
    private LearningLevel learningLevel;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Column
    private Integer rightNo;
    @Column
    private Integer wrongNo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picture_id")
    private Picture memPicture;
    @Column
    private Integer revisedNo;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextReviseTime;
    @Column
    private Double penaltyRate;
    @Column
    private Boolean lastLearnSessionResult;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public LearningLevel getLearningLevel() {
        return learningLevel;
    }

    public void setLearningLevel(LearningLevel learningLevel) {
        this.learningLevel = learningLevel;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getWrongNo() {
        return wrongNo;
    }

    public void setWrongNo(Integer wrongNo) {
        this.wrongNo = wrongNo;
    }

    public Integer getRightNo() {
        return rightNo;
    }

    public void setRightNo(Integer rightNo) {
        this.rightNo = rightNo;
    }

    public Picture getMemPicture() {
        return memPicture;
    }

    public void setMemPicture(Picture memPicture) {
        this.memPicture = memPicture;
    }

    public Integer getRevisedNo() {
        return revisedNo;
    }

    public void setRevisedNo(Integer revisedNo) {
        this.revisedNo = revisedNo;
    }

    public Date getNextReviseTime() {
        return nextReviseTime;
    }

    public void setNextReviseTime(Date nextReviseTime) {
        this.nextReviseTime = nextReviseTime;
    }

    public Double getPenaltyRate() {
        return penaltyRate;
    }

    public void setPenaltyRate(Double penaltyRate) {
        this.penaltyRate = penaltyRate;
    }

    public Boolean getLastLearnSessionResult() {
        return lastLearnSessionResult;
    }

    public void setLastLearnSessionResult(Boolean lastLearnSessionResult) {
        this.lastLearnSessionResult = lastLearnSessionResult;
    }
}
