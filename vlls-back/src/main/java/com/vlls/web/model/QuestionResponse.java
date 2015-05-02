package com.vlls.web.model;

import com.vlls.jpa.domain.Question;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hiephn on 2014/10/10.
 */
public class QuestionResponse implements DataResponse<Question> {

    private Integer id;
    private String text;
    private TextValueResponse type;
    private String answerType;
    private TextValueResponse incorrectAnswerType;
    private Integer incorrectAnswerNum;
    private String incorrectAnswerRaw;
    private ItemResponse correctAnswerItem;
//    private List<ItemResponse> incorrectAnswerItems;

    @Override
    public void setData(Question entity) {
        this.id = entity.getId();
        this.text = entity.getText();
        this.type = entity.getType() != null
                ? new TextValueResponse(entity.getType().getDisplay(), String.valueOf(entity.getType().getIndex()))
                : new TextValueResponse();
        this.answerType = entity.getAnswerType() != null ? entity.getAnswerType().toString() : null;
        this.incorrectAnswerType = entity.getIncorrectAnswerType() != null
                ? new TextValueResponse(entity.getIncorrectAnswerType().getDisplay(),
                    String.valueOf(entity.getIncorrectAnswerType().getIndex()))
                : new TextValueResponse();
        this.incorrectAnswerNum = entity.getIncorrectAnswerNum();
        this.incorrectAnswerRaw = entity.getIncorrectAnswerRaw();
        /* Question list will be grouped by correct item,
            so this attribute is considered to be redundant
        this.correctAnswerItem = new ItemResponse();
        this.correctAnswerItem.setData(entity.getCorrectAnswerItem());
        */
//        this.incorrectAnswerItems = entity.getIncorrectAnswerItems() != null ?
//                entity.getIncorrectAnswerItems().stream().map(item -> {
//                    ItemResponse itemResponse = new ItemResponse();
//                    itemResponse.setData(item);
//                    return itemResponse;
//                }).collect(Collectors.toList()) :
//                null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextValueResponse getType() {
        return type;
    }

    public void setType(TextValueResponse type) {
        this.type = type;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public TextValueResponse getIncorrectAnswerType() {
        return incorrectAnswerType;
    }

    public void setIncorrectAnswerType(TextValueResponse incorrectAnswerType) {
        this.incorrectAnswerType = incorrectAnswerType;
    }

    public Integer getIncorrectAnswerNum() {
        return incorrectAnswerNum;
    }

    public void setIncorrectAnswerNum(Integer incorrectAnswerNum) {
        this.incorrectAnswerNum = incorrectAnswerNum;
    }

    public String getIncorrectAnswerRaw() {
        return incorrectAnswerRaw;
    }

    public void setIncorrectAnswerRaw(String incorrectAnswerRaw) {
        this.incorrectAnswerRaw = incorrectAnswerRaw;
    }

    public ItemResponse getCorrectAnswerItem() {
        return correctAnswerItem;
    }

    public void setCorrectAnswerItem(ItemResponse correctAnswerItem) {
        this.correctAnswerItem = correctAnswerItem;
    }

//    public List<ItemResponse> getIncorrectAnswerItems() {
//        return incorrectAnswerItems;
//    }
//
//    public void setIncorrectAnswerItems(List<ItemResponse> incorrectAnswerItems) {
//        this.incorrectAnswerItems = incorrectAnswerItems;
//    }
}
