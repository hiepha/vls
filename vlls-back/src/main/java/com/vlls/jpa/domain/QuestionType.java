package com.vlls.jpa.domain;

import com.vlls.exception.InvalidRequestItemException;
import org.apache.commons.lang.StringUtils;

/**
 * Created by hiephn on 2014/10/10.
 */
public enum QuestionType {
    MULTIPLE_CHOICE("Multiple choice", 0), USER_INPUT("User input", 1);

    private String display;
    private int index;

    public String getDisplay() {
        return display;
    }

    public int getIndex() {
        return index;
    }

    QuestionType(String display, int index) {
        this.display = display;
        this.index = index;
    }

    public static QuestionType fromDisplay(String display) throws InvalidRequestItemException {
        QuestionType[] types = QuestionType.values();
        for (QuestionType type : types) {
            if (StringUtils.equalsIgnoreCase(type.getDisplay(), display)) {
                return type;
            }
        }
        throw new InvalidRequestItemException("Invalid Question Type: " + display);
    }

    public static QuestionType fromIndex(Integer index) throws InvalidRequestItemException {
        QuestionType[] types = QuestionType.values();
        for (QuestionType type : types) {
            if (index == type.index) {
                return type;
            }
        }
        throw new InvalidRequestItemException("Invalid Question Type: " + index);
    }
}
