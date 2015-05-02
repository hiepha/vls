package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Hoang Phi on 9/27/2014.
 */
@Entity
public class Course extends AbstractPersistable<Integer> {
    private static final long serialVersionUID = 1L;
    @Column(length = 128)
    private String name;
    @Column(length = 1024)
    private  String description;
    @Column(length = 128)
    private String avatar;
    @Column
    private Boolean isPublic;
    @Column
    private Integer numOfStudent;
    @Column
    private Integer recommended;
    @Column
    private Boolean isActive;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId")
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lang_teach_id")
    private Language langTeach;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lang_speak_id")
    private Language langSpeak;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Level> levels;
    @Column
    private Integer rating;

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public Integer getNumOfStudent() {
        return numOfStudent;
    }

    public void setNumOfStudent(Integer numOfStudent) {
        this.numOfStudent = numOfStudent;
    }

    public Integer getRecommended() {
        return recommended;
    }

    public void setRecommended(Integer recommended) {
        this.recommended = recommended;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Language getLangTeach() {
        return langTeach;
    }

    public void setLangTeach(Language langTeach) {
        this.langTeach = langTeach;
    }

    public Language getLangSpeak() {
        return langSpeak;
    }

    public void setLangSpeak(Language langSpeak) {
        this.langSpeak = langSpeak;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
