package com.vlls.web.model;

import com.vlls.jpa.domain.Item;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Hoang Phi on 9/28/2014.
 */
public class ItemResponse implements DataResponse<Item> {

    private static final SimpleDateFormat SDFT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Integer id;
    private String name;
    private String meaning;
    private String pronun;
    private String type;
    private String audio;
    private Boolean isActive;
    private Integer levelId;
    private String lastUpdate;

    @Override
    public void setData(Item entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.meaning = entity.getMeaning();
        this.pronun = entity.getPronun();
        this.type = entity.getType();
        this.audio = entity.getAudio();
        this.isActive = entity.getIsActive();
        this.levelId = entity.getLevel().getId();
        if (entity.getLastUpdate() != null) {
            this.lastUpdate = SDFT.format(new Date(entity.getLastUpdate().getTime()));
        }
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
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

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPronun() {
        return pronun;
    }

    public void setPronun(String pronun) {
        this.pronun = pronun;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
