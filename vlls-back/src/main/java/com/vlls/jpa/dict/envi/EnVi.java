package com.vlls.jpa.dict.envi;

import com.vlls.jpa.dict.DictItemPersistable;

import javax.persistence.Entity;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;

/**
 * Created by hiephn on 2014/10/03.
 */
@Entity
@Table(name = "anh_viet")
@PersistenceContext(unitName="envi")
public class EnVi extends DictItemPersistable {
    private static final long serialVersionUID = 1L;
}
