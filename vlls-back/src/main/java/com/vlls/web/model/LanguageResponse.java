package com.vlls.web.model;

import com.vlls.jpa.domain.Language;

/**
 * Created by hiephn on 2014/09/27.
 */
public class LanguageResponse implements DataResponse<Language> {

    private Integer id;
    private String name;
    private String description;
    private String code;

    @Override
    public void setData(Language entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.code = entity.getCode();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
}
