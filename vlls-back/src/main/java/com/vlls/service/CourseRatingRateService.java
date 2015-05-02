package com.vlls.service;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.jpa.domain.CourseRatingRate;
import com.vlls.jpa.repository.CourseRatingRateRepository;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.CourseRatingRatePageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by HoangPhi on 11/8/2014.
 */
@Service
public class CourseRatingRateService extends AbstractService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRatingRateRepository courseRatingRateRepo;
    @Autowired
    private SecurityService securityService;

    @Transactional
    public void createRatingRate(Integer userId, Integer courseId, Double ratingSimilar)
            throws ServerTechnicalException, NoInstanceException {
        // Get current user
        //User user = securityService.retrieveCurrentUser0();
        // Check course existed or not
        //Course course = courseService.get0(courseId);

        CourseRatingRate courseRatingRate = new CourseRatingRate();
        courseRatingRate.setUserId(userId);
        courseRatingRate.setCourseId(courseId);
        courseRatingRate.setRatingSimilar(ratingSimilar);
        courseRatingRateRepo.save(courseRatingRate);
    }

    public CourseRatingRatePageResponse getAllByUserId(int page, int pageSize)
            throws UnauthenticatedException, ServerTechnicalException {
        // Get current user
        UserSessionInfo user = securityService.retrieveCurrentUserSession();

        Page<CourseRatingRate> courseRatingRates;
        CourseRatingRatePageResponse courseRatingRatePageResponse = new CourseRatingRatePageResponse();
        courseRatingRates = courseRatingRateRepo.findByUserIdOrderByRatingSimilarDesc(user.getId(),
                new PageRequest(page, pageSize));

        // Generate response
        courseRatingRatePageResponse.from(courseRatingRates);
        return courseRatingRatePageResponse;
    }

    public void deleteRatingRate(Integer userId) {
        List<CourseRatingRate> courseRatingRates = courseRatingRateRepo.getAllCourseRatingRate(userId);
        courseRatingRateRepo.delete(courseRatingRates);
    }
}
