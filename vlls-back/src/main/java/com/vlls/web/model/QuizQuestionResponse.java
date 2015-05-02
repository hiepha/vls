package com.vlls.web.model;

import java.util.List;

/**
 * Created by hiephn on 2014/10/11.
 */
public class QuizQuestionResponse {
    private String text;
    private int type;
    private List<String> options;
    private String answer;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    /**
     * MULTIPLE_CHOICE = 0, USER_INPUT = 1 defined at {@link com.vlls.jpa.domain.QuestionType}
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
