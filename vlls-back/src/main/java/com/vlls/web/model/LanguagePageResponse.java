package com.vlls.web.model;

import com.vlls.jpa.domain.Language;

/**
 * Created by hiephn on 2014/09/27.
 */
public class LanguagePageResponse extends AbstractDataPageResponse<Language, LanguageResponse> {
    @Override
    public LanguageResponse instantiateResponse() {
        return new LanguageResponse();
    }
}
