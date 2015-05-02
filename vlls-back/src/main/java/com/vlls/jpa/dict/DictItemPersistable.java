package com.vlls.jpa.dict;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by hiephn on 2014/10/03.
 */
@MappedSuperclass
public abstract class DictItemPersistable extends AbstractPersistable<Integer> {
    private static final long serialVersionUID = 1L;

    @Column
    private String word;
    @Column
    private String content;
    @Column
    private Boolean hasAudio;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(Boolean hasAudio) {
        this.hasAudio = hasAudio;
    }
}
