package com.vlls.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hiephn on 2014/09/02.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FbPictureResponse {
    private FbPictureDataResponse data;

    public FbPictureResponse() {
    }

    public FbPictureDataResponse getData() {
        return data;
    }

    public void setData(FbPictureDataResponse data) {
        this.data = data;
    }
}
