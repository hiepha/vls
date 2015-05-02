package com.vlls.web.model;

import com.vlls.jpa.domain.LikedPicture;

/**
 * Created by HoangPhi on 10/13/2014.
 */
public class LikedPictureResponse implements DataResponse<LikedPicture> {
    private Integer pictureId;
    private String userName;

    @Override
    public void setData(LikedPicture entity) {
        if (entity.getUser() != null) {
            this.userName = entity.getUser().getName();
        }
        if (entity.getPicture() != null) {
            this.pictureId = entity.getPicture().getId();
        }
    }

    public Integer getPictureId() {
        return pictureId;
    }

    public void setPictureId(Integer pictureId) {
        this.pictureId = pictureId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
