package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Hoang Thong on 26/09/2014.
 */
public interface LanguageRepository extends JpaRepository<Language, Integer> {

    /**
     * Finds language by using the name as a search criteria.
     * @param name
     * @return  A language which name is an exact match with the given name.
     */
    Language findOneByNameIgnoreCase(String name);

    /**
     * Finds languages by using the name as a search criteria.
     * @param name
     * @return  A list of languages which name is an like match with the given name.
     *          If no languages is found, this method returns an empty list.
     */
    List<Language> findByNameLike(String name);
}
