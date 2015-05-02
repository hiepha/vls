package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Hoang Phi on 9/28/2014.
 */
@Entity
public class Item extends AbstractPersistable<Integer>{
    private static final long serialVersionUID = 1L;

    @Column(length = 128)
    private String name;
    @Column(length = 64)
    private String pronun;
    @Column(length = 64)
    private String type;
    @Column(length = 512)
    private String meaning;
    @Column(length = 50)
    private String audio;
    @Column
    private Boolean isActive;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Picture> pictures;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "correctAnswerItem",
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Question> questions;
//    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "incorrectAnswerItems")
//    private List<Question> incorrectQuestions;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item",
            cascade = {CascadeType.REMOVE})
    private List<LearningItem> learningItems;

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPronun() {
        return pronun;
    }

    public void setPronun(String pronun) {
        this.pronun = pronun;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

//    public List<Question> getIncorrectQuestions() {
//        return incorrectQuestions;
//    }
//
//    public void setIncorrectQuestions(List<Question> incorrectQuestions) {
//        this.incorrectQuestions = incorrectQuestions;
//    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
