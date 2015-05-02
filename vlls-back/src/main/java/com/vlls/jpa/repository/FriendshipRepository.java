package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Friendship;
import com.vlls.jpa.domain.FriendshipKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


/**
 * Created by Hoang Phi on 10/18/2014.
 */
public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipKey> {
    Friendship findOneByFriendOneIdAndFriendTwoIdAndIsFriendFalse (Integer friendOneId, Integer friendTwoId);

    Friendship findOneByFriendOneIdAndFriendTwoIdAndIsFriendTrue (Integer friendOneId, Integer friendTwoId);

    @Query("select f from Friendship f where (f.friendOneId = ?1 or f.friendTwoId = ?1) and f.isFriend = 1")
    Page<Friendship> findAllFriendByUserId(Integer user, Pageable pageable);

//    @Query("(select f.friendOneId from Friendship f where f.friendTwoId = ?1 and f.isFriend = 1) union (select f.friendTwoId from Friendship f where f.friendOneId = ?1 and f.isFriend = 1)")
//    Page<Friendship> findFriendTab(Integer user, Pageable pageable);

    @Query("select count (f) from Friendship f where (f.friendOneId = ?1 or f.friendTwoId = ?1) and f.isFriend = 1")
            Integer getNumberOfFriend(Integer userId);

    // Query for friend ranking
    @Query("select distinct u.avatar, u.name, u.point from User u, Friendship f where ((f.friendOneId = ?1) and (f.friendTwoId = u.id)) or ((f.friendTwoId = ?1) and (f.friendOneId = u.id)) and f.isFriend = 1 or u.id = ?1 order by u.point desc")
    Page<Friendship> getFriendRanking(Integer userId, Pageable pageable);

    Page<Friendship> findByFriendTwoIdAndIsFriendFalse (Integer friendTwoId, Pageable pageable);

    @Query("select f from Friendship f where ((f.friendOneId = ?1) and (f.friendTwoId = ?2)) or ((f.friendTwoId = ?1) and (f.friendOneId = ?2))")
    Friendship findFriendship(Integer friendOneId, Integer friendTwoId);

    Long countByFriendTwoIdAndIsFriendFalseAndLastUpdateAfter(Integer userId, Date after);

    @Query("select f from Friendship f where f.friendTwoId = ?1 and f.isFriend = 0")
    List<Friendship> findAllPendingFriend(Integer userId);

    Long countByFriendTwoIdAndLastUpdateAfter(Integer userId, Date after);

    @Query(nativeQuery = true, value = "select f.* from friendship f, `user` u " +
            "where case " +
            "when f.friend_one_id = u.id then (f.friend_two_id = ? and u.name like ?) " +
            "when f.friend_two_id = u.id then (f.friend_one_id = ? and u.name like ?) end " +
            "limit ?")
    List<Friendship> findByName(Integer userId, String name, Integer userId2, String name2, int size);
}
