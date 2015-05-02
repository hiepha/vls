package com.vlls.web.model;

import com.vlls.jpa.domain.Picture;

/**
 * Created by hiephn on 2014/10/02.
 */
public class PicturePageResponse extends AbstractDataPageResponse<Picture, PictureResponse> {
    @Override
    public PictureResponse instantiateResponse() {
        return new PictureResponse();
    }
}
