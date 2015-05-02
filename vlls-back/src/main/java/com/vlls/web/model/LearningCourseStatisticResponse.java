package com.vlls.web.model;

import java.util.List;

/**
 * Created by hiephn on 2014/11/03.
 */
public class LearningCourseStatisticResponse {
    private Integer learntItems;
    private Integer learningItems;
    private Integer remainingItems;
    private Integer points;
    private Integer pointsToGo;
    private List<TextValueResponse> difficultWords;

    public Integer getLearntItems() {
        return learntItems;
    }

    public void setLearntItems(Integer learntItems) {
        this.learntItems = learntItems;
    }

    public Integer getLearningItems() {
        return learningItems;
    }

    public void setLearningItems(Integer learningItems) {
        this.learningItems = learningItems;
    }

    public Integer getRemainingItems() {
        return remainingItems;
    }

    public void setRemainingItems(Integer remainingItems) {
        this.remainingItems = remainingItems;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getPointsToGo() {
        return pointsToGo;
    }

    public void setPointsToGo(Integer pointsToGo) {
        this.pointsToGo = pointsToGo;
    }

    public List<TextValueResponse> getDifficultWords() {
        return difficultWords;
    }

    public void setDifficultWords(List<TextValueResponse> difficultWords2) {
        this.difficultWords = difficultWords2;
    }
}
