package com.vlls.web.model;

import com.vlls.jpa.domain.Level;

/**
 * Created by thongvh on 2014/09/27.
 */
public class LevelPageResponse extends AbstractDataPageResponse<Level, LevelResponse> {
    @Override
    public LevelResponse instantiateResponse() {
        return new LevelResponse();
    }
}
