package com.vlls.web.model;

import com.vlls.jpa.domain.Setting;

/**
 * Created by HoangPhi on 10/11/2014.
 */
public class SettingResponse implements DataResponse<Setting> {
    private String name;
    private String value;

    @Override
    public void setData(Setting entity) {
        this.name = entity.getName();
        this.value = entity.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
