package com.vlls.jpa.dict.vien;

import com.vlls.jpa.dict.vien.ViEn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

/**
 * Created by thongvh on 2014/10/23.
 */
@PersistenceContext(unitName="vien")
public interface ViEnRepository extends JpaRepository<ViEn, Integer> {

    Page<ViEn> findByWordGreaterThanEqual(String word, Pageable pageable);

    @Query("select id,word from ViEn where word >= ?1")
    List<Map<Integer, String>> findListWordByWordGreaterThanEqual(String word, Pageable pageable);
}
