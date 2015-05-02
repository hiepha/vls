package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Course;
import com.vlls.jpa.domain.CourseRatingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Created by HoangPhi on 11/8/2014.
 */
public interface CourseRatingRateRepository extends JpaRepository<CourseRatingRate, Integer> {
    CourseRatingRate findOneByUserIdAndCourseId(Integer userId, Integer courseId);

    Page<CourseRatingRate> findByUserIdOrderByRatingSimilarDesc(Integer userId, Pageable pageable);

    @Query("select crr from CourseRatingRate crr where crr.user.id = ?1")
    List<CourseRatingRate> getAllCourseRatingRate(Integer userId);
}
