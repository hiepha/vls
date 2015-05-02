package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by HoangPhi on 2015/01/14.
 */
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    @Query("select c from Conversation c where (c.userOne.id = ?1 and c.userTwo.id = ?2) or (c.userOne.id = ?2 and c.userTwo.id = ?1)")
    Conversation findByUserId(Integer userOneId, Integer userTwoId);

    Page<Conversation> findByUserOneIdOrUserTwoIdOrderByLastUpdateDesc(Integer userOneId, Integer userTwoId, Pageable pageable);
}
