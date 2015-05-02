package com.vlls.jpa.domain;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by hiephn on 2014/07/12.
 */
@MappedSuperclass
public class NamePersistable implements Persistable<String> {

    @Id
    @Column(length = 128)
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public boolean isNew() {
        return null == this.getId();
    }
}
