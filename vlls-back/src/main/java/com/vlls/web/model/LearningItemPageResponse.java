package com.vlls.web.model;

import com.vlls.jpa.domain.LearningItem;

/**
 * Created by TuanNKA on 10/4/2014.
 */
public class LearningItemPageResponse extends AbstractDataPageResponse<LearningItem, LearningItemResponse> {
    @Override
    public LearningItemResponse instantiateResponse() {
        return new LearningItemResponse();
    }
}
