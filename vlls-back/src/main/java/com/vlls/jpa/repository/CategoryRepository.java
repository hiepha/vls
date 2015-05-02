package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Hoang Thong on 26/09/2014.
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Finds category by using the name as a search criteria.
     * @param name
     * @return  A category which name is an exact match with the given name.
     */
    Category findOneByNameIgnoreCase(String name);

    /**
     * Finds categories by using the name as a search criteria.
     * @param name
     * @return  A list of categories which name is an like match with the given name.
     *          If no categories is found, this method returns an empty list.
     */
    List<Category> findByNameLike(String name);
}
