package com.vlls.jpa.domain;

import com.vlls.exception.InvalidRequestItemException;
import org.apache.commons.lang.StringUtils;

/**
 * Created by hiephn on 2014/10/10.
 */
public enum QuestionAnswerType {
    WORD, MEANING;

    public static QuestionAnswerType fromDisplay(String display) throws InvalidRequestItemException {
        QuestionAnswerType[] types = QuestionAnswerType.values();
        for (QuestionAnswerType type : types) {
            if (StringUtils.equalsIgnoreCase(type.toString(), display)) {
                return type;
            }
        }
        throw new InvalidRequestItemException("Invalid Question Answer Type: " + display);
    }
}
