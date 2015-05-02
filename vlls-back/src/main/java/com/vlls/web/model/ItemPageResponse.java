package com.vlls.web.model;

import com.vlls.jpa.domain.Item;

/**
 * Created by Hoang Phi on 9/28/2014.
 */
public class ItemPageResponse extends AbstractDataPageResponse<Item, ItemResponse> {
    @Override
    public ItemResponse instantiateResponse() {
        return new ItemResponse();
    }
}
