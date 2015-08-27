//package com.vlls.jpa.repository;
//
//import com.vlls.jpa.domain.ConversationReply;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by HoangPhi on 2015/01/14.
// */
//public interface ConversationReplyRepository extends JpaRepository<ConversationReply, Integer> {
//
//    @Query(nativeQuery = true, value = "select cr.* from conversation_reply cr where " +
//            "cr.conversation_id = ? and cr.time < ? order by cr.time desc limit ?")
//    List<ConversationReply> findByConversationId(Integer conversationId, Date from, int pageSize);
//
//}
