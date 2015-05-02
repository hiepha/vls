package com.vlls.web.model;

/**
 * Created by hiephn on 2014/09/24.
 */
public interface DataResponse<E> {

    /**
     * Extract data from entity to fill data response
     * @param entity entity
     */
    void setData(E entity);
}
