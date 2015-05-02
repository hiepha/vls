package com.vlls.web.model;

import com.vlls.jpa.domain.LearningLevel;

/**
 * Created by hiephn on 2014/10/16.
 */
public class LearningLevelPageResponse extends AbstractDataPageResponse<LearningLevel, LearningLevelResponse> {
    @Override
    public LearningLevelResponse instantiateResponse() {
        return new LearningLevelResponse();
    }
}
