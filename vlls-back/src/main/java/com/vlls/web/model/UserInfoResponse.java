package com.vlls.web.model;

import com.vlls.jpa.domain.User;

/**
 * Created by Hoang Phi on 10/25/2014.
 */
public class UserInfoResponse implements DataResponse <User> {

    private int id;
    private String name;
    private Integer point;
    private String bio;
    private String avatar;
    private Integer numberF;
//    private Integer numberLI;

    @Override
    public void setData(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.point = entity.getPoint();
        this.bio = entity.getBio();
        this.avatar = entity.getAvatar();
    }
/*

    public Integer getNumberLI() {
        return numberLI;
    }

    public void setNumberLI(Integer numberLI) {
        this.numberLI = numberLI;
    }
*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getNumberF() {
        return numberF;
    }

    public void setNumberF(Integer numberF) {
        this.numberF = numberF;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
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
}
