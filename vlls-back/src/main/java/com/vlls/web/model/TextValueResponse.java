package com.vlls.web.model;

/**
 * Created by hiephn on 2014/10/20.
 */
public class TextValueResponse {
    private String text;
    private String value;

    public TextValueResponse() {
    }

    public TextValueResponse(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
