package com.vlls.web.model;

import com.vlls.jpa.domain.LearningItem;
import com.vlls.service.LearningItemService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TuanNKA on 10/4/2014.
 */
public class LearningItemResponse implements DataResponse<LearningItem> {
    private static final SimpleDateFormat SDFT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int id;
    private int itemId;
    private String name;
    private String meaning;
    private String pronun;
    private Integer progress;
    private String lastUpdate;
    private String audio;
    private String type;
    private String levelName;
    private Integer reviseTimeLeft;
    private Integer pictureId;
    private Integer reviseNo;
    private int rightNo;
    private int wrongNo;
    private boolean lastLearnSessionResult;
    private double penaltyRate;

    @Override
    public void setData(LearningItem entity) {
        if (entity.getMemPicture() == null) {
            this.setPictureId(0);
        } else {
            this.setPictureId(entity.getMemPicture().getId());
        }
        this.setId(entity.getId());
        this.setItemId(entity.getItem().getId());
        this.setName(entity.getItem().getName());
        this.setMeaning(entity.getItem().getMeaning());
        this.setPronun(entity.getItem().getPronun());
        this.audio = entity.getItem().getAudio();
        this.rightNo = entity.getRightNo();
        this.wrongNo = entity.getWrongNo();
        this.lastLearnSessionResult = entity.getLastLearnSessionResult() == null ? false : entity.getLastLearnSessionResult();
        this.penaltyRate = entity.getPenaltyRate() == null ? 0 : entity.getPenaltyRate();
        if (entity.getRightNo() == null) {
            this.setProgress(0);
        } else {
            Integer currentProgress = entity.getRightNo() * 100 / LearningItemService.RIGHT_NO_TO_FINISH;
            this.setProgress(currentProgress > 100 ? 100 : currentProgress);
        }
        if (entity.getLastUpdate() != null) {
            this.lastUpdate = SDFT.format(new Date(entity.getLastUpdate().getTime()));
        }
        this.setType(entity.getItem().getType());
        this.setLevelName(entity.getItem().getLevel().getName());
        if (entity.getNextReviseTime() != null) {
            this.setReviseTimeLeft((int) (entity.getNextReviseTime().getTime() - System.currentTimeMillis()) / 1000);
        }
        this.setReviseNo(entity.getRevisedNo());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
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

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Integer getReviseTimeLeft() {
        return reviseTimeLeft;
    }

    public void setReviseTimeLeft(Integer reviseTimeLeft) {
        this.reviseTimeLeft = reviseTimeLeft;
    }

    public Integer getPictureId() {
        return pictureId;
    }

    public void setPictureId(Integer pictureId) {
        this.pictureId = pictureId;
    }

    public Integer getReviseNo() {
        return reviseNo;
    }

    public void setReviseNo(Integer reviseNo) {
        this.reviseNo = reviseNo;
    }

    public int getRightNo() {
        return rightNo;
    }

    public void setRightNo(int rightNo) {
        this.rightNo = rightNo;
    }

    public int getWrongNo() {
        return wrongNo;
    }

    public void setWrongNo(int wrongNo) {
        this.wrongNo = wrongNo;
    }

    public boolean isLastLearnSessionResult() {
        return lastLearnSessionResult;
    }

    public void setLastLearnSessionResult(boolean lastLearnSessionResult) {
        this.lastLearnSessionResult = lastLearnSessionResult;
    }

    public double getPenaltyRate() {
        return penaltyRate;
    }

    public void setPenaltyRate(double penaltyRate) {
        this.penaltyRate = penaltyRate;
    }
}
