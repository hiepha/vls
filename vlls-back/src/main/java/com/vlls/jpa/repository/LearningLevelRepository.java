package com.vlls.jpa.repository;

import com.vlls.jpa.domain.LearningLevel;
import com.vlls.jpa.domain.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hiephn on 2014/10/07.
 */
public interface LearningLevelRepository extends JpaRepository<LearningLevel, Integer> {

    @Query("select ll.level from LearningLevel ll where ll.learningCourse.id = ?1")
    Page<Level> findLevelByLearningCourseId(Integer learningCourseid, Pageable pageable);

    Page<LearningLevel> findByLearningCourseId(Integer learningCourseId, Pageable pageable);

    LearningLevel findByLearningCourseIdAndLevelId(Integer learningCourseId, Integer levelId);

    //@Query("select i.id from LearningItem li, LearningLevel lv, Item i, Level l where lv.id = :LearningLevelId and lv.level.id = l.id")
    @Query("select i.id from LearningLevel ll join ll.learningItems li join li.item i where ll = ?1")
    List<Integer> findItemId(LearningLevel learningLevel);

    @Query("select count(li.id) from LearningLevel ll join ll.learningItems li where ll.id = ?1 and li.revisedNo < 6")
    Integer getNumOfReviseItems(Integer learningLevelId);
}
