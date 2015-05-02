package com.vlls.web.model;

import com.vlls.jpa.domain.Setting;

/**
 * Created by HoangPhi on 10/11/2014.
 */
public class SettingPageResponse extends AbstractDataPageResponse<Setting, SettingResponse> {
    @Override
    public SettingResponse instantiateResponse() {
        return new SettingResponse();
    }
}
