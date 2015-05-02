package com.vlls.jpa.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by HoangPhi on 10/13/2014.
 */
public class LikedPictureKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer pictureId;
    private Integer userId;

    public LikedPictureKey() {

    }

    public LikedPictureKey(Integer pictureId, Integer userId) {
        this.pictureId = pictureId;
        this.userId = userId;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return true;
    }

    public int hashCode(){
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.pictureId)
                .append(this.userId)
                .toHashCode();
    }

    public Integer getPictureId() {
        return pictureId;
    }

    public void setPictureId(Integer pictureId) {
        this.pictureId = pictureId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
