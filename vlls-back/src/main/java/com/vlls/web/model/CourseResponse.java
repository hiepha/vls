package com.vlls.web.model;

import com.vlls.jpa.domain.Course;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hoang Phi on 9/27/2014.
 */
public class CourseResponse implements DataResponse<Course> {

    private static final SimpleDateFormat SDFT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Integer id;
    private String name;
    private String description;
    private String avatar;
    private Boolean isPublic;
    private String langTeach;
    private Integer langTeachId;
    private String langTeachCode;
    private String langSpeak;
    private Integer langSpeakId;
    private String langSpeakCode;
    private Integer numberOfStudent;
    private Integer recommend;
    private String creatorName;
    private Integer creatorId;
    private String creatorAvatar;
    private String categoryName;
    private Integer categoryId;
    private Boolean isActive;
    private String lastUpdate;
    private String createdDate;
    private LearningCourseResponse learningCourse;
    private Integer rating;

    private boolean simpleResponse;

    public CourseResponse() {
    }

    public CourseResponse(boolean simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    @Override
    public void setData(Course entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.avatar = entity.getAvatar();
        this.numberOfStudent = entity.getNumOfStudent();
        this.recommend = entity.getRecommended();
        if (entity.getIsPublic() != null) {
            this.isPublic = entity.getIsPublic();
        }
        if (entity.getCreator() != null) {
            this.creatorName = entity.getCreator().getName();
            this.creatorId = entity.getCreator().getId();
            this.creatorAvatar = entity.getCreator().getAvatar();
        }
        if (!this.simpleResponse) {
            //langTeach
            if (entity.getLangTeach() != null) {
                this.langTeach = entity.getLangTeach().getName();
                this.langTeachId = entity.getLangTeach().getId();
                this.langTeachCode = entity.getLangTeach().getCode();
            }
            //langSpeak
            if (entity.getLangSpeak() != null) {
                this.langSpeak = entity.getLangSpeak().getName();
                this.langSpeakId = entity.getLangSpeak().getId();
                this.langSpeakCode = entity.getLangSpeak().getCode();
            }
            if (entity.getCategory() != null) {
                this.categoryName = entity.getCategory().getName();
                this.categoryId = entity.getCategory().getId();
            }
        }
        if (entity.getIsActive() != null) {
            this.isActive = entity.getIsActive();
        }
        if (entity.getLastUpdate() != null) {
            this.lastUpdate = SDFT.format(new Date(entity.getLastUpdate().getTime()));
        }
        if (entity.getCreatedDate() != null) {
            this.createdDate = SDFT.format(new Date(entity.getCreatedDate().getTime()));
        }
        if (entity.getRating() != null) {
            this.setRating(entity.getRating());
        } else {
            this.setRating(0);
        }
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getLangTeach() {
        return langTeach;
    }

    public void setLangTeach(String langTeach) {
        this.langTeach = langTeach;
    }

    public Integer getLangTeachId() {
        return langTeachId;
    }

    public void setLangTeachId(Integer langTeachId) {
        this.langTeachId = langTeachId;
    }

    public String getLangSpeak() {
        return langSpeak;
    }

    public void setLangSpeak(String langSpeak) {
        this.langSpeak = langSpeak;
    }

    public Integer getLangSpeakId() {
        return langSpeakId;
    }

    public void setLangSpeakId(Integer langSpeakId) {
        this.langSpeakId = langSpeakId;
    }

    public Integer getNumberOfStudent() {
        return numberOfStudent;
    }

    public void setNumberOfStudent(Integer numberOfStudent) {
        this.numberOfStudent = numberOfStudent;
    }

    public Integer getRecommend() {
        return recommend;
    }

    public void setRecommend(Integer recommend) {
        this.recommend = recommend;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorAvatar() {
        return creatorAvatar;
    }

    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public LearningCourseResponse getLearningCourse() {
        return learningCourse;
    }

    public void setLearningCourse(LearningCourseResponse learningCourse) {
        this.learningCourse = learningCourse;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLangTeachCode() {
        return langTeachCode;
    }

    public void setLangTeachCode(String langTeachCode) {
        this.langTeachCode = langTeachCode;
    }

    public String getLangSpeakCode() {
        return langSpeakCode;
    }

    public void setLangSpeakCode(String langSpeakCode) {
        this.langSpeakCode = langSpeakCode;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
