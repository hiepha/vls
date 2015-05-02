package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Level;
import com.vlls.web.model.LevelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Hoang Thong on 26/09/2014.
 */
public interface LevelRepository extends JpaRepository<Level, Integer> {

    /**
     * Finds a list of Level in page by using the name as a search criteria.
     * @param name
     * @return  A set of levels which name is a like match with the given name.
     */
    Page<Level> findByNameLike(String name, Pageable pageable);

    /**
     * Finds level by using the name as a search criteria.
     * @param name
     * @return  A level which name is an exact match with the given name.
     */
     Level findOneByNameIgnoreCase(String name);

    /*
        Get all level by course ID
     */
    List<Level> findByCourseId(Integer courseId);

    //Level findByCourseId(Integer courseId);

    /**
     * Finds a course by using the id as a search criteria.
     * @param courseID
     * @return  A course which id is an match with the given course_id.
     */
    /*@Query("select c from Course c where c.id = ?")
    Course findCourse(Integer courseID);*/

    @Query("select l from Level l where l.course.id = (select c.id from Course c, Level l2 where c.id = l2.course.id and l2.id = :id) and l.id < :id order by l.id desc")
    List<Level> getPrevLevel(@Param(value = "id") int levelId);

    @Query("select l from Level l where l.course.id = (select c.id from Course c, Level l2 where c.id = l2.course.id and l2.id = :id) and l.id > :id order by l.id asc")
    List<Level> getNextLevel(@Param(value = "id") int levelId);

    @Query("select count(i.id) from Item i, Level l where i.level = l.id and l.id = ?1")
    Integer getNumOfLevelItems(Integer levelId);

    List<Level> findByIdNotInAndCourseId(List<Integer> ids, Integer courseId);
}
