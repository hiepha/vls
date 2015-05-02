package com.vlls.jpa.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by hiephn on 2014/07/12.
 */
@Entity
public class Role extends AbstractPersistable<Integer> {

    @Column(unique = true, nullable = false, length = 128)
    private String name;
    @Column(nullable = true, length = 256)
    private String description;

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
}
