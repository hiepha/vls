package com.vlls.jpa.domain;

import com.vlls.exception.InvalidRequestItemException;
import org.apache.commons.lang.StringUtils;

/**
 * Created by hiephn on 2014/10/10.
 */
public enum  QuestionIncorrectAnswerType {
    REST_OF_COURSE("Rest of course items", 0),
    REST_OF_LEVEL("Rest of level items", 1),
    SOME_FROM_COURSE("Some items from course", 2),
    SOME_FROM_LEVEL("Some items from level", 3),
    INPUT_MANUALLY("Input manually", 4);

    private String display;
    private int index;

    public String getDisplay() {
        return display;
    }

    public int getIndex() {
        return index;
    }

    QuestionIncorrectAnswerType(String display, int index) {
        this.display = display;
        this.index = index;
    }

    public static QuestionIncorrectAnswerType fromDisplay(String display) throws InvalidRequestItemException {
        QuestionIncorrectAnswerType[] types = QuestionIncorrectAnswerType.values();
        for (QuestionIncorrectAnswerType type : types) {
            if (StringUtils.equalsIgnoreCase(type.getDisplay(), display)) {
                return type;
            }
        }
        throw new InvalidRequestItemException("Invalid Question Incorrect Answer Type: " + display);
    }

    public static QuestionIncorrectAnswerType fromIndex(Integer index) throws InvalidRequestItemException {
        QuestionIncorrectAnswerType[] types = QuestionIncorrectAnswerType.values();
        for (QuestionIncorrectAnswerType type : types) {
            if (index == type.index) {
                return type;
            }
        }
        throw new InvalidRequestItemException("Invalid Question Incorrect Answer Type: " + index);
    }
}
