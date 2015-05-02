package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Course;
import com.vlls.jpa.domain.SimilarCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Hoang Phi on 11/1/2014.
 */
public interface SimilarCourseRepository extends JpaRepository<SimilarCourse, Integer> {

    @Query("select s from SimilarCourse s where s.courseOneId = ?1 or s.courseTwoId = ?1")
    List<SimilarCourse> findAllSimilarCourse(Integer courseId);

}