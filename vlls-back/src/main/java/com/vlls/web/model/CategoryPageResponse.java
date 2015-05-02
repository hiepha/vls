package com.vlls.web.model;

import com.vlls.jpa.domain.Category;

/**
 * Created by thongvh on 2014/09/27.
 */
public class CategoryPageResponse extends AbstractDataPageResponse<Category, CategoryResponse> {
    @Override
    public CategoryResponse instantiateResponse() {
        return new CategoryResponse();
    }
}
