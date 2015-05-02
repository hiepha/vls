package com.vlls.web.model;

import java.util.List;

/**
 * Created by hiephn on 2014/10/12.
 */
public class LearnQuizItemResponse extends LearningItemResponse {
    private List<QuizQuestionResponse> questions;

    public List<QuizQuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionResponse> questions) {
        this.questions = questions;
    }
}
