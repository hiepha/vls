package com.vlls.service;

import com.vlls.jpa.domain.Course;
import com.vlls.jpa.domain.SimilarCourse;
import com.vlls.jpa.repository.SimilarCourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Hoang Phi on 11/1/2014.
 */
@Service
public class SimilarCourseService extends AbstractService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private SimilarCourseRepository similarCourseRepo;
    @Autowired
    private CourseService courseService;

    @Transactional
    public void create(Integer courseOneId, Integer courseTwoId, Double similarRate){
        SimilarCourse similarCourse = new SimilarCourse();
        similarCourse.setCourseOneId(courseOneId);
        similarCourse.setCourseTwoId(courseTwoId);
        similarCourse.setSimilarRate(similarRate);
        similarCourseRepo.save(similarCourse);
    }

    public void deleteSimilarCourse(Integer courseId){
        List<SimilarCourse> similarCourseList = similarCourseRepo.findAllSimilarCourse(courseId);
        similarCourseRepo.delete(similarCourseList);
    }

    //Asynchronous when User update course
    @Async
    @Transactional(rollbackOn = Exception.class)
    public void updateCreateSimilarRateAsync(Course course, Integer courseId){
        try {
            this.deleteSimilarCourse(courseId);
            courseService.createSimilarRateList(course);
        } catch (Exception e) {
            LOGGER.error("Cannot update similar rate", e);
            throw e;
        }
    }
}
