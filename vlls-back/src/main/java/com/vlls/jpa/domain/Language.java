package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Hoang Thong on 26/09/2014.
 */
@Entity
public class Language extends AbstractPersistable<Integer> {

    @Column(unique = true, nullable = false, length = 128)
    private String name;
    @Column(nullable = true, length = 256)
    private String description;
    @Column(length = 5)
    private String code;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isEqual(Language language){
        boolean isEqual = true;
        if (this.name != language.name){
            isEqual = false;
        }
        return isEqual;
    }
}
