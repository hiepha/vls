package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Course;
import com.vlls.jpa.domain.SimilarCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Hoang Phi on 9/27/2014.
 */
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query(value = "select c from Course c join fetch c.creator join fetch c.category " +
            "join fetch c.langTeach join fetch c.langSpeak " +
            "where (c.category.id = ?1 or ?1 < 0) " +
            "and (c.langTeach.id = ?2 or ?2 < 0) " +
            "and (c.langSpeak.id = ?3 or ?3 < 0) " +
            "and upper(c.name) like upper(?4) " +
            "and c.isPublic = true",
            countQuery = "select count(c) from Course c " +
                    "where (c.category.id = ?1 or ?1 < 0) " +
                    "and (c.langTeach.id = ?2 or ?2 < 0) " +
                    "and (c.langSpeak.id = ?3 or ?3 < 0) " +
                    "and upper(c.name) like upper(?4) " +
                    "and c.isPublic = true")
    Page<Course> findByCategoryAndLangTeachAndLangSpeakAndName(
            Integer categoryId, Integer langTeachId, Integer langSpeakId, String name, Pageable pageable);

    @Query("select distinct crr.course from Course c, CourseRatingRate crr, User u where crr.user.id = ?1 and " +
            "(crr.course.category.id = ?2 or ?2 < 0) " +
            "and (crr.course.langTeach.id = ?3 or ?3 < 0) " +
            "and (crr.course.langSpeak.id = ?4 or ?4 < 0) " +
            "and upper(crr.course.name) like upper(?5) " +
            "and crr.course.isPublic = true " +
            "order by crr.ratingSimilar desc")
    Page<Course> findByRatingRateAndCategoryAndLangTeachAndLangSpeakAndName(
            Integer userId, Integer categoryId, Integer langTeachId, Integer langSpeakId, String name, Pageable pageable);

    /**
     * By TuanNKA
     */
    @Query("select c from Course c where c.creator.name = :username")
    Page<Course> findByCreatorAndIsPublicTrue(@Param(value = "username") String creator, Pageable pageable);

    @Query("select c from Course c, LearningCourse lc where c.id = lc.course.id and lc.user.name = :username")
    Page<Course> getLearningCourses(@Param(value = "username") String username, Pageable pageable);

    @Query("select count(i.id) from Item i, Level l where i.level.id = l.id and l.course.id = :courseId")
    Integer getNumofCourseItems(@Param(value = "courseId") Integer courseId);

    @Query("select distinct crr.course from Course c, CourseRatingRate crr, User u where crr.user.id = ?1 order by crr.ratingSimilar desc")
    Page<Course> getSimilarCourseByRatingRate(Integer userId, Pageable pageable);

    @Query("select c from Course c, SimilarCourse s where ((s.courseOneId = ?1) and (s.courseTwoId = c.id)) or ((s.courseTwoId = ?1) and (s.courseOneId = c.id)) and s.similarRate > 0 order by s.similarRate desc")
    List<Course> getSimilarCourse(Integer courseId);

    // Get similar course return Page response
    @Query("select s from SimilarCourse s where (s.courseOneId = ?1 or s.courseTwoId = ?1) and s.similarRate > 0 order by s.similarRate desc")
    Page<SimilarCourse> findAllSimilarCourse(Integer courseId, Pageable pageable);

    @Query("select c from Course c where c.id <> ?1")
    List<Course> getRemainCourseId(Integer courseId);

    // Query to get list of similar courses and similar rate
    @Query("select c.id, s.similarRate from Course c, SimilarCourse s where ((s.courseOneId = ?1) and (s.courseTwoId = c.id)) or ((s.courseTwoId = ?1) and (s.courseOneId = c.id)) and s.similarRate > 0")
    List<Object[]> getSimilarCoursesAndSimilarRate(Integer courseId);

    @Query(nativeQuery = true, value = "select s.similar_rate from similar_course s where ((s.course_one_id = ?) and (s.course_two_id = ?)) or ((s.course_two_id = ?) and (s.course_one_id = ?))")
    Double getSimilarRate(Integer courseOneId, Integer courseTwoId, Integer courseOneId2, Integer courseTwoId2);

    @Query("select c from Course c where c.creator.name = ?1 and c.isPublic = true and c.id not in (select lc.course.id from LearningCourse lc where lc.user.name = ?2 and lc.course.creator.name = ?1)")
    Page<Course> findByCreatorAndIsPublicTrueNolearningCourses(String creator, String learner, Pageable pageable);

    /**
     * By ThongVH
     */
    @Query("select c from Course c order by c.numOfStudent desc")
    Page<Course> getPopularCourses(Pageable pageable);

    @Query("select c from Course c where c.category.name = ?1 order by c.numOfStudent desc")
    Page<Course> getPopularCoursesByCategory(String category, Pageable pageable);

    @Query("select c from Course c order by c.createdDate desc")
    Page<Course> getLatestCourses(Pageable pageable);

    @Query("select c from Course c where c.category.name = ?1 order by c.createdDate desc")
    Page<Course> getLatestCoursesByCategory(String category, Pageable pageable);

    @Query("select count(lc.id) from LearningCourse lc left join lc.course where lc.rating != null and lc.course.id = :id")
    Integer getNumOfRatingsOfCourse(@Param(value = "id") Integer courseId);
}
