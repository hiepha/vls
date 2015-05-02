package com.vlls.jpa.repository;

import com.vlls.jpa.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hiephn on 2014/06/28.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    Page<User> findByNameLikeOrFirstNameLikeOrLastNameLike(
            String name, String firstName, String lastName, Pageable pageable);

    User findByNameIgnoreCaseAndPassword(String name, String password);

    User findOneByEmailIgnoreCase(String email);

    User findOneByNameIgnoreCase(String name);

    User findOneByCreatedCoursesLevelsItemsQuestionsId(Integer questionId);

    User findOneByCreatedCoursesLevelsItemsId(Integer itemId);

    User findOneByCreatedCoursesLevelsId(Integer levelId);

    User findOneByCreatedCourses(Integer courseId);

    @Query("select u from User u where u.role.id <> 1 order by u.point desc")
    Page<User> rankingUser(Pageable pageable);

    // Query for friend ranking
    @Query("select distinct u.id, u.name, u.avatar, u.point from User u, Friendship f " +
            "where ((f.friendOneId = ?1) and (f.friendTwoId = u.id)) or ((f.friendTwoId = ?1) " +
            "and (f.friendOneId = u.id)) and f.isFriend = 1 or u.id = ?1 order by u.point desc")
    List<Object[]> friendRanking(Integer userId);

    @Query("select count(li.id) from LearningItem li where li.learningLevel.learningCourse.user.name = ?1 and li.rightNo >= ?2")
    Long getUserTotalLearntItems(String username, Integer rightNoToFinish);

    @Query("select count(f.isFriend) from Friendship f where f.friendOne.name = ?1 or f.friendTwo.name = ?1")
    Long getUserTotalFriends(String username);

    @Query("select u.name, u.email from User u where u.lastLogin < (current_timestamp - 2592000000)")
    List<Object[]> getUsersNeedCall();

    @Query("select u from User u")
    List<User> getAllUsers();

    @Query("select lc.course.creator from LearningCourse lc where lc.id =?1")
    User getLearningCourseCreator(Integer learningCourseId);

    @Query("select lc.course.creator from LearningCourse lc where lc.user.id = ?1 group by lc.course.creator.id")
    List<User> getLearningCoursesCreatorByLearnerId(Integer userId);
}
