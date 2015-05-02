package com.vlls.web.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;

import java.util.List;

/**
 * Created by hiephn on 2014/09/24.
 */
public interface DataPageResponse<E, R extends DataResponse<E>> {

    R instantiateResponse();

    void from(List<E> entityList);

    void from(Page<E> entityPage);

    void from(E entity);
}
