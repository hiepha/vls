package com.vlls.web.model;

/**
 * Created by hiephn on 2014/11/30.
 */
public class NotificationResponse {

    private String type;
    private String image;
    private String header;
    private String message;
    private String author;
    private int learningCourseId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getLearningCourseId() {
        return learningCourseId;
    }

    public void setLearningCourseId(int learningCourseId) {
        this.learningCourseId = learningCourseId;
    }
}
