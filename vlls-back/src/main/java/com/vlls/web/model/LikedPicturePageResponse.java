package com.vlls.web.model;

import com.vlls.jpa.domain.LikedPicture;

/**
 * Created by HoangPhi on 10/13/2014.
 */
public class LikedPicturePageResponse extends AbstractDataPageResponse<LikedPicture, LikedPictureResponse> {
    @Override
    public LikedPictureResponse instantiateResponse() {
        return new LikedPictureResponse();
    }
}
