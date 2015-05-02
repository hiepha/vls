package com.vlls.web.model;

import com.vlls.jpa.domain.LearningLevel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hiephn on 2014/10/17.
 */
public class LearningLevelResponse implements DataResponse<LearningLevel> {

    private static final SimpleDateFormat SDFT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Integer id;
    private Integer progress;
    private LevelResponse level;
    private String lastUpdate;
    private String lastSync;

    @Override
    public void setData(LearningLevel entity) {
        this.id = entity.getId();
        this.progress = entity.getProgress();
        this.level = new LevelResponse();
        this.level.setData(entity.getLevel());
        if (entity.getLastUpdate() != null) {
            this.lastUpdate = SDFT.format(new Date(entity.getLastUpdate().getTime()));
        }
        if (entity.getLastSync() != null) {
            this.lastSync = SDFT.format(new Date(entity.getLastSync().getTime()));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public LevelResponse getLevel() {
        return level;
    }

    public void setLevel(LevelResponse level) {
        this.level = level;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }
}
