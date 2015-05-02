package com.vlls.web.model;

import com.vlls.jpa.domain.Question;

/**
 * Created by hiephn on 2014/10/10.
 */
public class QuestionPageResponse extends AbstractDataPageResponse<Question, QuestionResponse> {
    @Override
    public QuestionResponse instantiateResponse() {
        return new QuestionResponse();
    }
}
