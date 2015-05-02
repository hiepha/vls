package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by hiephn on 2014/10/10.
 */
@Entity
public class Question extends AbstractPersistable<Integer> {

    @Column(length = 256)
    private String text;
    @Column
    private QuestionType type;
    @Column
    private QuestionAnswerType answerType;
    @Column
    private QuestionIncorrectAnswerType incorrectAnswerType;
    @Column
    private Integer incorrectAnswerNum;
    @Column(length = 512)
    private String incorrectAnswerRaw;
    @Column
    private Boolean isSystemGenerated;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correct_answer_id")
    private Item correctAnswerItem;
    @ManyToMany(fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//    @JoinTable(name = "incorrect_answer",
//        joinColumns = @JoinColumn(name = "question_id"),
//        inverseJoinColumns = @JoinColumn(name = "item_id"))
//    private List<Item> incorrectAnswerItems;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public QuestionAnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(QuestionAnswerType answerType) {
        this.answerType = answerType;
    }

    public QuestionIncorrectAnswerType getIncorrectAnswerType() {
        return incorrectAnswerType;
    }

    public void setIncorrectAnswerType(QuestionIncorrectAnswerType incorrectAnswerType) {
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

    public Boolean getIsSystemGenerated() {
        return isSystemGenerated;
    }

    public void setIsSystemGenerated(Boolean isSystemGenerated) {
        this.isSystemGenerated = isSystemGenerated;
    }

    public Item getCorrectAnswerItem() {
        return correctAnswerItem;
    }

    public void setCorrectAnswerItem(Item correctAnswerItem) {
        this.correctAnswerItem = correctAnswerItem;
    }

//    public List<Item> getIncorrectAnswerItems() {
//        return incorrectAnswerItems;
//    }
//
//    public void setIncorrectAnswerItems(List<Item> incorrectAnswerItems) {
//        this.incorrectAnswerItems = incorrectAnswerItems;
//    }
}
