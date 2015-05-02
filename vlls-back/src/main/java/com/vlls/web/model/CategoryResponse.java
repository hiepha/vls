package com.vlls.web.model;

import com.vlls.jpa.domain.Category;

/**
 * Created by thongvh on 2014/09/27.
 */
public class CategoryResponse implements DataResponse<Category> {

    private Integer id;
    private String name;
    private String description;

    @Override
    public void setData(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
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
}
