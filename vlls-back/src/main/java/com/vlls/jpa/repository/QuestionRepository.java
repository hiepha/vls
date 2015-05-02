package com.vlls.jpa.repository;

import com.vlls.jpa.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hiephn on 2014/10/10.
 */
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    List<Question> findByCorrectAnswerItemId(Integer itemId);
}
