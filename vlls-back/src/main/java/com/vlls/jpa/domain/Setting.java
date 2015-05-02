package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by HoangPhi on 10/10/2014.
 */
@Entity
@IdClass(SettingKey.class)
public class Setting implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(nullable = false, length = 20)
    private String name;

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(length = 20)
    private String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
