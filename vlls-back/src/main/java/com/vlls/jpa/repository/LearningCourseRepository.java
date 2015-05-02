package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Course;
import com.vlls.jpa.domain.LearningCourse;
import com.vlls.jpa.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Hoang Phi on 10/1/2014.
 */
public interface LearningCourseRepository extends JpaRepository<LearningCourse, Integer> {

    @Query("select lc from LearningCourse lc join fetch lc.user u join lc.course c where u.id = ?1 and c.id = ?2")
    LearningCourse findOneByUserIdAndCourseId(Integer userId, Integer courseId);

    Page<LearningCourse> findByUserId(Integer userId, Pageable pageable);

    Page<LearningCourse> findByCourseId(Integer courseId, Pageable pageable);

    Page<LearningCourse> findByUserIdAndCourseIdAndPinStatus(Integer userId, Integer courseId, Boolean pinStatus, Pageable pageable);

    Page<LearningCourse> findByCourseIdAndPinStatus(Integer courseId, Boolean pinStatus, Pageable pageable);

    @Query("select lc from LearningCourse lc WHERE lc.course.id = ?1 order by lc.point desc")
    Page<LearningCourse> rankingInCourse(Integer courseId, Pageable pageable);

    @Query("select l.id from LearningCourse lc join lc.learningLevels ll join ll.level l where lc = ?1")
    List<Integer> findLevelId(LearningCourse learningCourse);

    @Query("select i.name, i.meaning, i.pronun, i.type, i.level.name from LearningCourse lc join lc.learningLevels ll join ll.learningItems li join li.item i " +
            "where lc = ?1 and li.wrongNo - li.rightNo > 0")
    List<Object> getDifficultWords(LearningCourse learningCourse);

    @Query(nativeQuery = true, value =
            "select i.name, cast((li.wrong_no / (li.right_no + li.wrong_no) * 100) as decimal(4,2)) as wrong_ratio " +
                    "from learning_course lc " +
                    "  left join learning_level ll on lc.id = ll.learning_course_id " +
                    "  left join learning_item li on ll.id = li.learning_level_id " +
                    "  left join item i on i.id = li.item_id " +
                    "where lc.id = ? and li.right_no > 0 order by wrong_ratio desc limit ?")
    List<Object[]> getDifficultWordsStatistic(Integer learningCourseId, Integer limit);

    // Get learned courses by userId return Integer
    @Query("select lc.course.id from LearningCourse lc where lc.user.id = ?1")
    List<Integer> getLearnedCourseIdByUserId(Integer userId);

    // Get learned courses by userId return Course
    @Query("select c from LearningCourse lc join lc.course c where lc.user.id = ?1")
    List<Course> getLearnedCourseByUserId(Integer userId);

    // Get courses are not learned by Id
    @Query("select c from Course c where c.id not in ?1")
    Page<Course> getRemainingCourseId(List<Integer> remainCourses, Pageable pageable);

    // Get courses are not learned
    @Query("select c from Course c where c not in ?1")
    List<Course> getRemainingCourse(List<Course> remainCourses);

    // Get list of user by learned course
    @Query("select lc.user.id from LearningCourse lc where lc.course.id = ?1 and lc.user.id <> ?2")
    List<Integer> getLearnedUserNotIn(Integer courseId, Integer userId);

    // Get list of user by learned course
    @Query("select lc.user.id from LearningCourse lc where lc.course.id = ?1")
    List<Integer> getLearnedUser(Integer courseId);

    // Get all learner in course (Guest)
/*    @Query("select u from LearningCourse lc, User u, Course c " +
            "where c.id = lc.course.id and u.id = lc.user.id and c.id = ?1 and u.id <> ?2")
    Page<User> getLearnerInCourse(Integer courseId, Integer userId, Pageable pageable);*/
    @Query("select lc.user from LearningCourse lc where lc.course.id = ?1 and lc.user.id <> ?2")
    Page<User> getOtherLearnerInCourse(Integer courseId, Integer userId, Pageable pageable);

    // Get other users in course
    @Query("select lc.user from LearningCourse lc where lc.course.id = ?1")
    Page<User> getLearnerInCourse(Integer courseId, Pageable pageable);

    // Get other learned courses by userId
    @Query("select c.id from LearningCourse lc join lc.course c where lc.user.id in ?1 and lc.course.id <> ?2")
    List<Integer> getOtherLearnedCourseByUserIdIn(List<Integer> userIds, Integer courseId);

    // Get other learned courses by userId
    @Query("select c.id, count(c.id) as frequent from LearningCourse lc join lc.course c " +
            "where lc.user.id in ?1 and " +
            "c.id not in ?2 group by c.id order by frequent desc")
    Page<Object[]> getOtherLearnedCourseByUserIdInAndCourseIdNotIn(List<Integer> userIds, List<Integer> courseId, Pageable pageable);

    // Get other learned courses by userId
    @Query("select c.id, count(c.id) as frequent from LearningCourse lc join lc.course c " +
            "where lc.user.id in ?1 " +
            "group by c.id order by frequent desc")
    Page<Object[]> getOtherLearnedCourseByUserIdIn(List<Integer> userIds, Pageable pageable);

    // Get learned courses by userId return Integer except current course
    @Query("select lc.course.id from LearningCourse lc where lc.user.id = ?1 and lc.course.id <> ?2")
    List<Integer> getLearnedCourseIdByUserIdExceptCurrentCourse(Integer userId, Integer currentCourseId);

    @Query("select lc from LearningCourse lc, Course c where lc.rating != null and lc.course.id = c.id and c.id = ?1")
    List<LearningCourse> getRatedLearningCoursesByCourseId(Integer courseId);

    // Get Rating base on userId & courseId
    @Query("select lc.rating from LearningCourse lc where lc.course.id = ?1 and lc.user.id = ?2")
    Integer getRatingByUser(Integer courseId, Integer userId);

    @Query("select lc from Course c, LearningCourse lc where c.id = lc.course.id and lc.user.name = :username")
    Page<LearningCourse> getLearningCoursesOfCurUsers(@Param(value = "username") String username, Pageable pageable);

    @Query("select c.lastUpdate from LearningCourse lc join lc.course c where lc.id = ?1")
    Date findCourseLastUpdate(Integer id);

    @Query("select count(lc.user.id) from LearningCourse lc where lc.user.id = ?1")
    Integer getNumberRatingOfUser(Integer userId);

    @Query("select count(lc) from LearningCourse lc where lc.course.id = ?1")
    Integer getNumberOfLearnerByCourseId(Integer courseId);

    @Query("select count(lc.id) from LearningCourse lc where lc.user.id =?1 and (lc.lastUpdate > ?2 or (lc.nextReviseTime > ?2 and lc.nextReviseTime < current_timestamp))")
    Long countByUserIdAndLastUpdateAfterAndReviseTimeAfter(Integer userId, Date after);
}
