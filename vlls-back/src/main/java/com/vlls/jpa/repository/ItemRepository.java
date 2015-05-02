package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Hoang Phi on 9/28/2014.
 */
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select i from Item i where i.level.id = ?1 and (i.name like ?2 or i.meaning like ?3)")
    Page<Item> findByLevelIdAndNameLikeOrMeaningLike(Integer levelId, String name, String meaning, Pageable pageable);

    @Query("select i from Item i where i.level.course.id = ?1 and (i.name like ?2 or i.meaning like ?3)")
    Page<Item> findByCourseIdAndNameLikeOrMeaningLike(Integer courseId, String name, String meaning, Pageable pageable);

//    @Query("select i from Item i, Level l " +
//            "where i.level.id = l.id " +
//            "and l.course.id = (select c.id from  where )")
//    List<Item> findAllItemsByUserId(@Param(value = "id") int userId);

    @Query("select i from Item i, Level l where i.level.id = l.id " +
            "and l.course.id = :id")
    List<Item> findAllItemsByCourseId(@Param(value = "id") int courseId);

    @Query("select i.id from Item i where i.level.course.id = ?1")
    List<Integer> findIdsByCourseId(Integer courseId);

    @Query("select i.id from Item i where i.level.id = ?1")
    List<Integer> findIdsByLevelId(Integer levelId);

    @Query(value = "select i.id from item i left join incorrect_answer ia on i.id = ia.item_id" +
            " left join question q on ia.question_id = q.id" +
            " where q.id = ?1", nativeQuery = true)
    List<Integer> findIncorrectAnswerIdsByQuestionId(Integer questionId);

    @Query("select i.name from Item i where i.id in ?1")
    List<String> findNamesByIds(List<Integer> ids);

    @Query("select i.meaning from Item i where i.id in ?1")
    List<String> findMeaningsByIds(List<Integer> ids);

    void deleteByLevelId(Integer levelId);

    Item findByName(String name);

    List<Item> findByIdNotInAndLevelId(List<Integer> ids, Integer levelId);

    @Query("select i.id from Item i where i.id in ?1 and i.name not like ?2")
    List<Integer> removeFromListIfNameEqual(List<Integer> incorrectIds, String name);
}
