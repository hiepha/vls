//package com.vlls.jpa.dict.envi;
//
//import com.vlls.jpa.dict.envi.EnVi;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//
//import javax.persistence.PersistenceContext;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by hiephn on 2014/10/03.
// */
//@PersistenceContext(unitName="envi")
//public interface EnViRepository extends JpaRepository<EnVi, Integer> {
//
//    Page<EnVi> findByWordGreaterThanEqual(String word, Pageable pageable);
//
//    @Query("select id,word from EnVi where word >= ?1")
//    List<Map<Integer, String>> findListWordByWordGreaterThanEqual(String word, Pageable pageable);
//}
