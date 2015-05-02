package com.vlls.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hiephn on 2014/09/02.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FbPictureDataResponse {
    private int height;
    private int width;
    private String url;

    public FbPictureDataResponse() {
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
