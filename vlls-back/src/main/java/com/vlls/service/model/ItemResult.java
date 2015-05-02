package com.vlls.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hiephn on 2014/10/25.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemResult {
    private Integer id;
    private Integer right;
    private Integer wrong;
    private String learnResultMap;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRight() {
        return right;
    }

    public void setRight(Integer right) {
        this.right = right;
    }

    public Integer getWrong() {
        return wrong;
    }

    public void setWrong(Integer wrong) {
        this.wrong = wrong;
    }

    public String getLearnResultMap() {
        return learnResultMap;
    }

    public void setLearnResultMap(String learnResultMap) {
        this.learnResultMap = learnResultMap;
    }
}
