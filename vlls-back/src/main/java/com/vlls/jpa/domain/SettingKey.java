package com.vlls.jpa.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by HoangPhi on 10/11/2014.
 */
public class SettingKey implements Serializable{
    private static final long serialVersionUID = 1L;

    private String name;
    private Integer userId;

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return true;
    }

    public int hashCode(){
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.name)
                .append(this.userId)
                .toHashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
