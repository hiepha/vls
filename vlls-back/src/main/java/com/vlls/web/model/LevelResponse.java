package com.vlls.web.model;

import com.vlls.jpa.domain.Level;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by thongvh on 2014/09/27.
 */
public class LevelResponse implements DataResponse<Level> {

    private static final SimpleDateFormat SDFT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Integer id;
    private String name;
    private String description;
    private Boolean isActive;
    private String lastUpdate;

    @Override
    public void setData(Level entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.isActive = entity.getIsActive();
        if (entity.getLastUpdate() != null) {
            this.lastUpdate = SDFT.format(new Date(entity.getLastUpdate().getTime()));
        }
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
