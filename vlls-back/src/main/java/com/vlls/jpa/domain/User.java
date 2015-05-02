package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

/**
 * Created by hiephn on 2014/06/28.
 */
@Entity
public class User extends AbstractPersistable<Integer> {
    private static final long serialVersionUID = 1L;

    @Column(length = 128)
    protected String name;
    @Column(length = 128)
    private String password;
    @Column(length = 50)
    private String firstName;
    @Column(length = 50)
    private String lastName;
    @Column
    private Integer point;
    @Column
    private Date birthday;
    @Column
    private String bio;
    @Column(length = 128)
    private String avatar;
    @Column(length = 20)
    private String phone;
    @Column(length = 128)
    private String fbId;
    @Column(length = 256)
    private String fbAccessToken;
    @Column(length = 64)
    private String email;
    @Column
    private Gender gender;
    @Column
    private Boolean isActive;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date lastLogin;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date lastUpdate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator")
    private List<Course> createdCourses;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Setting> settings;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<LearningCourse> learningCourses;

    public List<LearningCourse> getLearningCourses() {
        return learningCourses;
    }

    public void setLearningCourses(List<LearningCourse> learningCourses) {
        this.learningCourses = learningCourses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getFbAccessToken() {
        return fbAccessToken;
    }

    public void setFbAccessToken(String fbAccessToken) {
        this.fbAccessToken = fbAccessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Course> getCreatedCourses() {
        return createdCourses;
    }

    public void setCreatedCourses(List<Course> createdCourses) {
        this.createdCourses = createdCourses;
    }

    public java.util.Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(java.util.Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public java.util.Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(java.util.Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isEqual(User user){
        boolean isEqual = true;
        if (this.name != user.name){
            isEqual = false;
        }
        return isEqual;
    }
}
