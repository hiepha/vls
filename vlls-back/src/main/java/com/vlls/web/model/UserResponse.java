package com.vlls.web.model;

import com.vlls.jpa.domain.Friendship;
import com.vlls.jpa.domain.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hiephn on 2014/09/24.
 */
public class UserResponse implements DataResponse<User> {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat SDFT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Integer id;
    private String name;
    private String firstName;
    private String lastName;
    private Integer point;
    private String birthday;
    private String bio;
    private String avatar;
    private String phone;
    private String fbId;
    private String fbAccessToken;
    private String email;
    private String gender;
    private String role;
    private Friendship numberOfFriend;
    private String lastLogin;
    private String lastUpdate;
    private String friendStatus;
    private float learningProgress;

    @Override
    public void setData(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.point = entity.getPoint();
        this.bio = entity.getBio();
        this.avatar = entity.getAvatar();
        this.phone = entity.getPhone();
        this.fbId = entity.getFbId();
        this.fbAccessToken = entity.getFbAccessToken();
        this.email = entity.getEmail();
        this.role = entity.getRole().getName();
        if (entity.getGender() != null) {
            this.gender = entity.getGender().toString();
        }
        if (entity.getBirthday() != null) {
            this.birthday = SDF.format(new Date(entity.getBirthday().getTime()));
        }
        if (entity.getLastLogin() != null) {
            this.lastLogin = SDFT.format(new Date(entity.getLastLogin().getTime()));
        }
        if (entity.getLastUpdate() != null) {
            this.lastUpdate = SDFT.format(new Date(entity.getLastUpdate().getTime()));
        }
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }

    public float getLearningProgress() {
        return learningProgress;
    }

    public void setLearningProgress(float learningProgress) {
        this.learningProgress = learningProgress;
    }
}
