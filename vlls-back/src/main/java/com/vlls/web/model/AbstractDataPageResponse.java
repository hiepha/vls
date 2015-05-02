package com.vlls.web.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hiephn on 2014/09/24.
 */
public abstract class AbstractDataPageResponse<E, R extends DataResponse<E>>
        implements DataPageResponse<E, R> {
    protected int pageNumber;
    protected int totalPages;
    protected long totalElements;
    protected int pageSize;
    protected int numberOfElements;
    protected List<R> dataList;

    @Override
    public void from(List<E> list) {
        if (list != null) {
            this.setPageNumber(0);
            this.setTotalElements(list.size());
            this.setTotalPages(1);
            this.setNumberOfElements(list.size());
            this.setPageSize(list.size());
            this.dataListFrom(list);
        }
    }

    @Override
    public void from(Page<E> page) {
        if (page != null) {
            this.pageInfoFrom(page);
            this.dataListFrom(page.getContent());
        }
    }

    public void pageInfoFrom(Page page) {
        this.pageNumber = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageSize = page.getSize();
        this.numberOfElements = page.getNumberOfElements();
    }

    @Override
    public void from(E entity) {
        this.pageNumber = 0;
        this.totalPages = 1;
        this.totalElements = 1;
        this.pageSize = 1;
        this.numberOfElements = 1;

        R response = this.instantiateResponse();
        response.setData(entity);
        this.dataList = new ArrayList<>(1);
        this.dataList.add(response);
    }

    public void dataListFrom(List<E> entityList) {
        if (entityList == null) {
            this.dataList = new ArrayList<>(0);
        } else {
            this.dataList = new ArrayList<>(entityList.size());
            entityList.forEach(entity -> {
                R response = this.instantiateResponse();
                response.setData(entity);
                this.dataList.add(response);
            });
        }
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public List<R> getDataList() {
        return dataList;
    }

    public void setDataList(List<R> dataList) {
        this.dataList = dataList;
    }
}
