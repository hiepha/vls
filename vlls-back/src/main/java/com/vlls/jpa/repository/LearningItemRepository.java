package com.vlls.jpa.repository;

import com.vlls.jpa.domain.LearningItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Dell on 10/4/2014.
 */
public interface LearningItemRepository extends JpaRepository<LearningItem, Integer> {

    //    @Query("select li from LearningItem li where li.item.level.id = :levelId and li.item.level.course.id = :courseId and li.user.id = :userId")
//    Page<LearningItem> findByLevelIdAndCourseIdAndUserId(@Param(value = "levelId") final int levelId,
//                                                   @Param(value = "courseId") final int courseId,
//                                                   @Param(value = "userId") final int userId,
//                                                   Pageable pageable);
//
    @Query("select li from LearningItem li where li.item.level.id = :levelId")
    Page<LearningItem> findByLevelId(@Param(value = "levelId") final int levelId, Pageable pageable);

    Page<LearningItem> findByLearningLevelId(Integer learningLevelId, Pageable pageable);

    @Query(value = "select li.* from learning_item li where li.learning_level_id = ? and li.right_no < ? limit ?",
            nativeQuery = true)
    List<LearningItem> findByLearningLevelIdAndRightNoLessThan(Integer learningLevelId, Integer rightNo, Integer limit);

    @Query(value = "select li.* from learning_item li left join learning_level ll on li.learning_level_id = ll.id " +
            "where ll.learning_course_id = ? and li.right_no < ? limit ?",
            nativeQuery = true)
    List<LearningItem> findByLearningCourseIdAndRightNoLessThan(Integer learningCourseId, Integer rightNo, Integer limit);

    @Query("select count(li.id) from LearningItem li, LearningLevel lv, LearningCourse lc where " +
            "li.rightNo >= :rightNoToFinish and li.learningLevel.id = lv.id and lv.learningCourse.id = lc.id and " +
            "lc.course.id = :courseId and lc.user.id = :userId")
    Integer getNumOfLearntItems(@Param(value = "rightNoToFinish") Integer rightNoToFinish,
                                @Param(value = "courseId") Integer courseId,
                                @Param(value = "userId") Integer userId);

//    @Query("select count(li.id) from LearningItem li, LearningLevel lv, LearningCourse lc where li.rightNo >= ?4 and " +
//            "li.learningLevel = lv.id and lv.id = ?1 and lc.id = ?2 and lc.user.id = ?3")
//    Integer getNumOfLearnedItemInLevel(Integer learningLevelId, Integer learningCourseId, Integer userId,
//                                       Integer rightNoToFinish);

    @Query("select count(li.id) from LearningItem li where li.learningLevel.id = ?1")
    Integer countByLearningLevelId(Integer learningLevelId);

    @Query("select count(li.id) from LearningItem li where li.learningLevel.id = ?1 and li.rightNo >= ?2")
    Integer countLearntByLearningLevelId(Integer learningLevelId, Integer rightNoToFinish);

    @Query("select count(li.id) from LearningItem li join li.learningLevel ll join ll.learningCourse lc where lc.id = ?1")
    Integer countByLearningCourseId(Integer learningCourseId);

    @Query("select count(li.id) from LearningItem li join li.learningLevel ll join ll.learningCourse lc where " +
            "lc.id = ?1 and li.rightNo >= ?2")
    Integer countLearntByLearningCourseId(Integer learningCourseId, Integer rightNoToFinish);

    @Query("select count(li.id) from LearningItem li join li.learningLevel ll join ll.learningCourse lc where " +
            "lc.id = ?1 and li.rightNo < ?2 and li.rightNo > 0")
    Integer countLearningByLearningCourseId(Integer learningCourseId, Integer rightNoToFinish);

    @Query("select li from LearningItem li where " +
            "li.learningLevel.id = ?1 and li.rightNo >= ?2 and " +
            "li.revisedNo < ?3 and li.nextReviseTime <= current_timestamp")
    List<LearningItem> findRevisedItemsByLearningLevelId(Integer learningLevelId, Integer rightNo, Integer reviseNo);

    @Query("select li from LearningItem li where li.memPicture.id = ?1")
    List<LearningItem> findLearningItemByPictureId(Integer id);

    @Query("select li from LearningItem li where " +
            "li.learningLevel.learningCourse.id = ?1 and li.rightNo >= ?2 and " +
            "li.revisedNo < ?3 and li.nextReviseTime <= current_timestamp")
    List<LearningItem> findRevisedItemsByLearningCourseId(Integer learningCourseId, Integer rightNo, Integer reviseNo);

    @Query("select count (li.id) from LearningItem li where " +
            "li.learningLevel.learningCourse.id = ?1 and " +
            "li.rightNo >= ?2 and li.revisedNo < ?3 and " +
            "li.nextReviseTime <= current_timestamp")
    Integer countRevisedItemsByLearningCourseId(Integer learningCourseId, Integer rightNo, Integer reviseNo);

    @Query("select lc.id, c.name, c.avatar, count(lc.id) as reviseNo from LearningItem li join li.learningLevel ll " +
            "join ll.learningCourse lc join lc.course c where " +
            "lc.user.id = ?1 and li.rightNo >= ?2 and li.revisedNo < ?3 and " +
            "li.nextReviseTime <= current_timestamp " +
            "group by lc.id")
    List<Object[]> findRevisedItemsNumberByUserId(
            Integer userId, Integer rightNo, Integer reviseNo);

    @Query("select count(lc.id) from LearningItem li join li.learningLevel ll " +
            "join ll.learningCourse lc where " +
            "lc.user.id = ?1 and li.rightNo >= ?2 and li.revisedNo < ?3 and " +
            "li.nextReviseTime <= current_timestamp and " +
            "li.nextReviseTime > ?4 group by lc.id")
    Long countRevisedItemsNumberByUserIdAfter(
            Integer userId, Integer rightNo, Integer reviseNo, Date after);

    @Query("select li.item.name, li.item.meaning, c.name from LearningItem li join li.learningLevel ll " +
            "join ll.learningCourse lc join lc.course c where " +
            "lc.user.id = ?1 and li.rightNo >= ?2 and li.revisedNo < ?3 and " +
            "li.nextReviseTime <= current_timestamp and " + "li.nextReviseTime > (current_timestamp - 900000) " +
            "group by li.id")
    List<Object[]> getReviseItemDetail(Integer userId, Integer rightNo, Integer reviseNo);
}
