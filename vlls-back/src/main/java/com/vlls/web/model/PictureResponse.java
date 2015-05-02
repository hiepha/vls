package com.vlls.web.model;

import com.vlls.jpa.domain.Picture;

/**
 * Created by hiephn on 2014/10/01.
 */
public class PictureResponse implements DataResponse<Picture> {

    private Integer id;
    private String source;
    private String htmlFormat;
    private Integer uploaderId;
    private String uploaderName;
    private String uploaderAvatar;
    private String itemWord;
    private String itemMeaning;
    private Integer itemId;
    private Integer numOfLikes;
    private Boolean isLiked;

    @Override
    public void setData(Picture entity) {
        this.id = entity.getId();
        this.source = entity.getSource();
        this.htmlFormat = entity.getHtmlFormat();
        this.uploaderId = entity.getUploader().getId();
        this.uploaderName = entity.getUploader().getName();
        this.uploaderAvatar = entity.getUploader().getAvatar();
        this.itemWord = entity.getItem().getName();
        this.itemMeaning = entity.getItem().getMeaning();
        this.itemId = entity.getItem().getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getHtmlFormat() {
        return htmlFormat;
    }

    public void setHtmlFormat(String htmlFormat) {
        this.htmlFormat = htmlFormat;
    }

    public Integer getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Integer uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderAvatar() {
        return uploaderAvatar;
    }

    public void setUploaderAvatar(String uploaderAvatar) {
        this.uploaderAvatar = uploaderAvatar;
    }

    public String getItemWord() {
        return itemWord;
    }

    public void setItemWord(String itemWord) {
        this.itemWord = itemWord;
    }

    public String getItemMeaning() {
        return itemMeaning;
    }

    public void setItemMeaning(String itemMeaning) {
        this.itemMeaning = itemMeaning;
    }

    public Integer getNumOfLikes() {
        return numOfLikes;
    }

    public void setNumOfLikes(Integer numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
