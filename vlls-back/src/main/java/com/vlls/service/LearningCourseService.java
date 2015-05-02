package com.vlls.service;

import com.vlls.exception.*;
import com.vlls.jpa.domain.*;
import com.vlls.jpa.repository.CourseRepository;
import com.vlls.jpa.repository.LearningCourseRepository;
import com.vlls.jpa.repository.LearningItemRepository;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Hoang Phi on 10/1/2014.
 */
@Service
public class LearningCourseService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LearningCourseService.class);

    private static final int MAX_DIFFICULT_WORD_STATISTIC = 10;
    private static int ITEM_POINT_TO_REMEMBER;

    static {
        int maxLearnPoint = 0;
        for (int i = 0; i < LearningItemService.ITEM_LEARNING_POINTS.length; ++i) {
            maxLearnPoint += LearningItemService.ITEM_LEARNING_POINTS[i];
        }

        int maxRevisionPoint = 0;
        for (int i = 0; i < LearningItemService.ITEM_REVISING_POINTS.length; ++i) {
            maxRevisionPoint += LearningItemService.ITEM_REVISING_POINTS[i];
        }
        maxRevisionPoint += maxRevisionPoint * 0.2;
        ITEM_POINT_TO_REMEMBER = maxLearnPoint + maxRevisionPoint;
    }

    @Autowired
    private LearningCourseRepository learningCourseRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    @Lazy
    private CourseService courseService;
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private LearningLevelService learningLevelService;
    @Autowired
    private LearningItemService learningItemService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private CourseRatingRateService courseRatingRateService;
    @Autowired
    private LearningItemRepository learningItemRepo;

    public LearningCoursePageResponse get(Integer id, Integer courseId, Integer userId, Boolean withCourse, int page, int pageSize)
            throws UnauthenticatedException, NoInstanceException, UnauthorizedException {
        Page<LearningCourse> learningCoursePage;
        LearningCoursePageResponse learningClassPageResponse = new LearningCoursePageResponse(!withCourse);
        if (userId > 0 && courseId > 0) {
            // select * from learningClass where userId = ? and courseId =?
            LearningCourse learningCourse = this.get0(courseId, userId);
            this.synchronize(learningCourse);
            learningClassPageResponse.from(learningCourse);
            learningClassPageResponse.getDataList().get(0).setNumOfReviseItem(
                    learningItemRepo.countRevisedItemsByLearningCourseId(learningCourse.getId(),
                            LearningItemService.RIGHT_NO_TO_FINISH, LearningItemService.REVISE_TIME.length));
        } else {
            UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();
            if (id > 0) {
                LearningCourse learningCourse = this.get0(id);
                this.synchronize(learningCourse);
                learningClassPageResponse.from(learningCourse);
            } else if (courseId > 0) {
                // select * from learningClass where userId = ? and courseId =?
                LearningCourse learningCourse = this.get0(courseId, userSessionInfo.getId());
                this.synchronize(learningCourse);
                learningClassPageResponse.from(learningCourse);
                learningClassPageResponse.getDataList().get(0).setNumOfReviseItem(
                        learningItemRepo.countRevisedItemsByLearningCourseId(learningCourse.getId(),
                                LearningItemService.RIGHT_NO_TO_FINISH, LearningItemService.REVISE_TIME.length));
            } else { // Get all learning course
                // select * from learningClass where userId = ?
                learningCoursePage = learningCourseRepository
                        .findByUserId(userSessionInfo.getId(), new PageRequest(page, pageSize));
                for (LearningCourse learningCourse : learningCoursePage) {
                    this.synchronize(learningCourse);
                }
                learningClassPageResponse.from(learningCoursePage);
            }
        }
        return learningClassPageResponse;
    }

    LearningCourse get0(Integer id) throws NoInstanceException {
        LearningCourse learningCourse = learningCourseRepository.findOne(id);
        if (learningCourse == null) {
            throw new NoInstanceException("Learning Course not found or deleted by creator", id);
        } else {
            return learningCourse;
        }
    }

    LearningCourse get0(Integer courseId, Integer userId) throws NoInstanceException {
        LearningCourse learningCourse = learningCourseRepository
                .findOneByUserIdAndCourseId(userId, courseId);
        if (learningCourse == null) {
            throw new NoInstanceException("User does not yet enroll");
        } else {
            return learningCourse;
        }
    }

    LearningCourse getQuietly0(Integer userId, Integer courseId) {
        LearningCourse learningCourse = learningCourseRepository.findOneByUserIdAndCourseId(userId, courseId);
        return learningCourse;
    }

    LearningCourse getAndValidateLearner0(Integer learningLevelId) throws NoInstanceException,
            UnauthenticatedException, UnauthorizedException {
        // Get learning course
        LearningCourse learningCourse = this.get0(learningLevelId);

        // Validate if this learning level is actually belongs to current user
        securityService.permitUser(learningCourse.getUser());

        return learningCourse;
    }

    @Transactional
    public LearningCourseResponse create(Integer courseId, Boolean pinStatus)
            throws UnauthorizedException, ServerTechnicalException, NoInstanceException, DuplicatedItemException,
            UnauthenticatedException {
        //Get current user
        User user = securityService.retrieveCurrentUser0();

        //Get current course
        Course course = courseService.get0(courseId);

        // Validate duplication
        LearningCourse learningCourseCheck = learningCourseRepository.
                findOneByUserIdAndCourseId(user.getId(), course.getId());
        if (learningCourseCheck != null) { // duplicated
            throw new DuplicatedItemException("User already enrolled");
        } else { // create new

            // Create Learning Course
            LearningCourse learningCourse = new LearningCourse();
            learningCourse.setUser(user);
            learningCourse.setCourse(course);
            learningCourse.setPinStatus(pinStatus);
            learningCourse.setLastUpdate(Calendar.getInstance().getTime());
            learningCourse.setLastSync(Calendar.getInstance().getTime());
            learningCourse.setLearningLevels(new ArrayList<>());
            learningCourseRepository.save(learningCourse);
            if (course.getNumOfStudent() == null) {
                course.setNumOfStudent(1);
            } else {
                course.setNumOfStudent(learningCourseRepository.getNumberOfLearnerByCourseId(courseId));
            }

            // Create list of Learning Level
            learningLevelService.createList0(learningCourse);

            //Generate response
            LearningCourseResponse learningClassResponse = new LearningCourseResponse(true);
            learningClassResponse.setData(learningCourse);
            return learningClassResponse;
        }
    }

    public LearningCourseResponse update(Integer courseId, Boolean pinStatus)
            throws UnauthorizedException, ServerTechnicalException, NoInstanceException, UnauthenticatedException {

        //Get current user
        UserSessionInfo user = securityService.retrieveCurrentUserSession();

        //Get current course
        Course course = courseService.get0(courseId);

        // Validate noInstance
        LearningCourse learningCourse = learningCourseRepository.
                findOneByUserIdAndCourseId(user.getId(), course.getId());
        if (learningCourse == null) { // Not yet enroll
            throw new NoInstanceException("User has not enrolled");
        } else { // update

            learningCourse.setCourse(course);
            learningCourse.setPinStatus(pinStatus);
            learningCourse.setLastUpdate(Calendar.getInstance().getTime());


            //Generate response
            LearningCourseResponse learningClassResponse = new LearningCourseResponse(true);
            learningClassResponse.setData(learningCourse);
            return learningClassResponse;
        }
    }

    public void delete(Integer id) {
        learningCourseRepository.delete(id);
    }

    @Transactional
    void calculatePoint(Integer learningCourseId, Integer point, Long nextReviseTime) throws NoInstanceException {
        LearningCourse learningCourse = this.get0(learningCourseId);
        learningCourse.setPoint(
                (learningCourse.getPoint() == null ? 0 : learningCourse.getPoint()) + point);
        if (nextReviseTime != null && (learningCourse.getNextReviseTime() == null
                || learningCourse.getNextReviseTime().getTime() > nextReviseTime
                || learningCourse.getNextReviseTime().getTime() < System.currentTimeMillis())) {
            learningCourse.setNextReviseTime(new Date(nextReviseTime));
        }
        this.touch(learningCourse);
    }

    @Transactional
    void touch(LearningCourse learningCourse) {
        learningCourse.setLastUpdate(new Date());
    }

    public LearningCoursePageResponse createRankingList(Integer courseId, int page, int pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<LearningCourse> learningCoursePage;
        learningCoursePage = learningCourseRepository.rankingInCourse(courseId, pageRequest);
        LearningCoursePageResponse learningCoursePageResponse = new LearningCoursePageResponse();
        learningCoursePageResponse.from(learningCoursePage);
        return learningCoursePageResponse;
    }

    public LinkedHashMap<String, Integer> getLearningCourseStatus(Integer courseId)
            throws UnauthenticatedException, ServerTechnicalException {
        Integer userId = securityService.retrieveCurrentUser0().getId();
        Integer numOfItems = courseService.getNumOfItems(courseId);
        Integer numOfLearntItem = learningItemService.getNumOfLearntItems(courseId, userId);
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("numOfItems", numOfItems);
        map.put("numOfLearntItem", numOfLearntItem);
        return map;
    }

    @Transactional
    public void synchronize(LearningCourse learningCourse)
            throws NoInstanceException, UnauthorizedException, UnauthenticatedException {

        Date learningCourseLastSync = learningCourse.getLastSync();
        Date courseLastUpdate = learningCourseRepository.findCourseLastUpdate(learningCourse.getId());

        if (courseLastUpdate.after(learningCourseLastSync)) {
            // Sync
            // DELETE: Do nothing

            // UPDATE: Do nothing

            // ADD:

            // Sync current learning levels
            for (LearningLevel learningLevel : learningCourse.getLearningLevels()) {
                learningLevelService.synchronize(learningLevel);
            }

            //Get all level id belong to learning course
            List<Integer> ids = learningCourseRepository.findLevelId(learningCourse);

            //Get all level of course that currently not in learning course
            List<Level> toBeAddedLevels;
            if (ids.size() > 0) {
                toBeAddedLevels = levelService.getLevelsNotIn(ids, learningCourse.getCourse().getId());
            } else {
                toBeAddedLevels = learningCourse.getCourse().getLevels();
            }

            //Add all new levels to learning course
            for (Level level : toBeAddedLevels) {
                learningLevelService.create0(learningCourse, level);
            }

            //Update last_sync
            learningCourse.setLastSync(Calendar.getInstance().getTime());
            this.touch(learningCourse);
        }
    }

    /**
     * Update number of learning levels in learning course that match with master data
     */
    @Transactional
    public void synchronize(Integer id) throws UnauthenticatedException, NoInstanceException, UnauthorizedException {
        LearningCourse learningCourse = this.get0(id);
        this.synchronize(learningCourse);
    }

    @Transactional
    public List getDifficultWords(Integer learningCourseId) throws UnauthenticatedException, ServerTechnicalException {
        LearningCourse learningCourse = learningCourseRepository.findOne(learningCourseId);
        List<Object> obj = learningCourseRepository.getDifficultWords(learningCourse);
        List<LearningItemResponse> itemResponses = new ArrayList<>();
        for (Object item : obj) {
            Object[] ob = (Object[]) item;
            LearningItemResponse itemResponse = new LearningItemResponse();
            itemResponse.setName((String) ob[0]);
            itemResponse.setMeaning((String) ob[1]);
            itemResponse.setPronun((String) ob[2]);
            itemResponse.setType((String) ob[3]);
            itemResponse.setLevelName((String) ob[4]);
            itemResponses.add(itemResponse);
        }
        return itemResponses;
    }

    public LearningCourseStatisticResponse getStatistic(Integer id) throws NoInstanceException {
        LearningCourse learningCourse = this.get0(id);
        Integer totalLearningItems = learningItemService.countByLearningCourseId(id);
        Integer learntItems = learningItemService.countLearntByLearningCourseId(id);
        Integer learningItem = learningItemService.countLearningByLearningCourseId(id);
        Integer pointsToRememberAll = ITEM_POINT_TO_REMEMBER * totalLearningItems;
        List<TextValueResponse> difficultWords = this.getDifficultWordsStatistic2(id);

        LearningCourseStatisticResponse statisticResponse = new LearningCourseStatisticResponse();
        statisticResponse.setLearntItems(learntItems);
        statisticResponse.setLearningItems(learningItem);
        statisticResponse.setRemainingItems(totalLearningItems - learntItems - learningItem);
        statisticResponse.setPoints(learningCourse.getPoint());
        statisticResponse.setPointsToGo(pointsToRememberAll -
                (learningCourse.getPoint() == null ? 0 : learningCourse.getPoint()));
        statisticResponse.setDifficultWords(difficultWords);
        return statisticResponse;
    }

    List<TextValueResponse> getDifficultWordsStatistic2(Integer id) {
        List<Object[]> difficultWordsStatistic = learningCourseRepository.
                getDifficultWordsStatistic(id, MAX_DIFFICULT_WORD_STATISTIC);

        return difficultWordsStatistic.stream().map(
                objects -> new TextValueResponse((String) objects[0],
                        String.valueOf((double) Math.round(((BigDecimal) objects[1]).doubleValue() * 100) / 100))
        ).collect(Collectors.toList());
    }

    // Method to test remain courses return Course page
    public CoursePageResponse getRemainCourseIdByUserId(int page, int pageSize) throws
            UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        UserSessionInfo user = securityService.retrieveCurrentUserSession();

        List<Integer> learnedCourses = learningCourseRepository.getLearnedCourseIdByUserId(user.getId());
        Page<Course> remainCourses = learningCourseRepository.getRemainingCourseId(learnedCourses,
                new PageRequest(page, pageSize));

        //Generate response
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(remainCourses);

        this.calculateRatingSimilar(user.getId());

        return coursePageResponse;
    }

    // Method to test remain courses return Course Id
    public void getRemainCourseByUserId() throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        UserSessionInfo user = securityService.retrieveCurrentUserSession();

        List<Course> learnedCourses = learningCourseRepository.getLearnedCourseByUserId(user.getId());
        List<Course> remainCourses = learningCourseRepository.getRemainingCourse(learnedCourses);
        System.out.println("Course learning: ");
        for (Course course : learnedCourses) {
            System.out.print(course.getId());
        }
        System.out.println("Course remaining: ");
        for (Course course : remainCourses) {
            System.out.print(course.getId());
        }
    }

    // Method to calculate R x SimilarCourseRate, put it in rating function
    @Transactional
    public void calculateRatingSimilar(Integer userId) throws ServerTechnicalException, NoInstanceException {
        Integer rating;
        Double simRate;
        Double ratingSimCourse;

        List<Course> learnedCourses = learningCourseRepository.getLearnedCourseByUserId(userId);
        List<Course> remainCourses = learningCourseRepository.getRemainingCourse(learnedCourses);

        for (Course rCourse : remainCourses) {
            // init total rating
            Double totalRatingSim = 0.d;
            Double totalSimRate = 0.d;
            Double ratingRate = 0.d;
            for (Course lCourse : learnedCourses) {
                simRate = courseRepo.getSimilarRate(lCourse.getId(), rCourse.getId(), lCourse.getId(), rCourse.getId());
                rating = learningCourseRepository.getRatingByUser(lCourse.getId(), userId);
                if (simRate != null && rating != null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Simrate: " + simRate + ". Rating: " + rating);
                    }
                    ratingSimCourse = simRate * rating;
                    // add to total rating
                    totalRatingSim += ratingSimCourse;
                    totalSimRate += simRate;
                    ratingRate = totalRatingSim / totalSimRate;
                }
            }
            // persist to db
            courseRatingRateService.createRatingRate(userId, rCourse.getId(), ratingRate);
        }
    }

    // Method to get student by Course Id
    public Set<Integer> getLearnedUserByCourseId(Integer currentCourseId) throws
            UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        // Get current user
        UserSessionInfo user = securityService.retrieveCurrentUserSession();

        LearningCourse course = get0(currentCourseId);

        if (course.getId() < 0) {
            throw new NoInstanceException("There isn't any user learned this course before");
        } else {
            // Get list of student in course
            List<Integer> learnedUser = learningCourseRepository.getLearnedUserNotIn(course.getId(), user.getId());

            // Initiate Set of remaining course of student
            Set<Integer> allRemainCourse = new HashSet<>(10);
            if (learnedUser.size() > 0) {
                // List other courses that learned by other users in List of learned user
                List<Integer> otherCourse = learningCourseRepository.
                        getOtherLearnedCourseByUserIdIn(learnedUser, course.getId());
                for (Integer courseId : otherCourse) {
                    // Add other courses of other user that current user has not learned
                    allRemainCourse.add(courseId);
                }
            }
            System.out.println(allRemainCourse.size());
            for (Integer remain : allRemainCourse) {
                System.out.print(remain);
            }

            // Get list of courses that current user has learned
            List<Integer> learnedCourses = learningCourseRepository.getLearnedCourseIdByUserId(user.getId());

            // Remove courses that current user has learned to get list of recommendation
            allRemainCourse.removeAll(learnedCourses);
            for (Integer allRemain : allRemainCourse) {
                System.out.print(allRemain);
            }
            return allRemainCourse;
        }
    }

    /**
     * Get user also learned courses.
     *
     * @param currentCourseId
     * @param page
     * @param pageSize
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
     */
    public CoursePageResponse getRecommendCoursesByCourseId(Integer currentCourseId, int page, int pageSize)
            throws ServerTechnicalException, NoInstanceException {
        // Get list of student in course
        List<Integer> learnedUser = learningCourseRepository.getLearnedUser(currentCourseId);

        if (learnedUser.size() == 0) {
            CoursePageResponse coursePageResponse = new CoursePageResponse();
            coursePageResponse.from(new ArrayList<>(0));
            return coursePageResponse;
        } else {
            // user login
            List<Integer> learnedCourses = null;
            if (securityService.isUser()) {
                // Get list of courses that current user has learned
                learnedCourses = learningCourseRepository.getLearnedCourseIdByUserId(
                        securityService.retrieveCurrentUserSessionQuietly().getId());
            }

            Page<Object[]> courseIdsAndFrequent;
            if (learnedCourses != null && learnedCourses.size() > 0) {
                learnedCourses.add(currentCourseId);
            } else {
                learnedCourses = new ArrayList<>(1);
                learnedCourses.add(currentCourseId);
            }
            courseIdsAndFrequent = learningCourseRepository.getOtherLearnedCourseByUserIdInAndCourseIdNotIn(
                    learnedUser, learnedCourses, new PageRequest(page, pageSize));

            // Extract course id list
            List<Integer> recoCourseIds = courseIdsAndFrequent.getContent().stream().
                    map(objects -> (Integer) objects[0]).collect(Collectors.toList());
            recoCourseIds.remove(currentCourseId);

            // Get courses
            List<Course> courses = new ArrayList<>(recoCourseIds.size());
            for (Integer courseId : recoCourseIds) {
                Course course = courseService.get0(courseId);
                courses.add(course);
            }

            // Generate response
            CoursePageResponse coursePageResponse = new CoursePageResponse();
            coursePageResponse.pageInfoFrom(courseIdsAndFrequent);
            coursePageResponse.dataListFrom(courses);

            return coursePageResponse;
        }
    }

    public List<LearningCourse> getRatedLearningCourse(Integer courseId) {
        return learningCourseRepository.getRatedLearningCoursesByCourseId(courseId);
    }


    public UserPageResponse getLearnerInCourse(Integer courseId, int page, int pageSize) throws
            UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        Page<User> learners = learningCourseRepository.getLearnerInCourse(courseId, new PageRequest(page, pageSize));
        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.from(learners);
        return userPageResponse;
    }

    public LearningCoursePageResponse getLearningCourseOfCurrentUser(int page, int pageSize) throws
            UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        UserSessionInfo user = securityService.retrieveCurrentUserSession();
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<LearningCourse> learningCoursePage = learningCourseRepository.
                getLearningCoursesOfCurUsers(user.getName(), pageRequest);
        LearningCoursePageResponse learningCoursePageResponse = new LearningCoursePageResponse();
        learningCoursePageResponse.from(learningCoursePage);
        for (LearningCourseResponse learningCourseResponse : learningCoursePageResponse.getDataList()) {
            LinkedHashMap<String, Integer> status = getLearningCourseStatus(learningCourseResponse.getCourse().getId());
            learningCourseResponse.setTotalItems(status.get("numOfItems"));
            learningCourseResponse.setTotalLearntItems(status.get("numOfLearntItem"));
        }
        return learningCoursePageResponse;
    }

    // Asynchronous when User rating
    @Async
    @Transactional(rollbackOn = Exception.class)
    public void updateCourseRatingAsync(Integer userId) throws ServerTechnicalException, NoInstanceException {
        try {
            courseRatingRateService.deleteRatingRate(userId);
            this.calculateRatingSimilar(userId);
        } catch (Exception e) {
            LOGGER.error("Cannot update course rating rate", e);
            throw e;
        }
    }

    long countIfLastUpdateAfter(Date date) throws UnauthenticatedException {
        Long counted = learningCourseRepository.countByUserIdAndLastUpdateAfterAndReviseTimeAfter(
                securityService.retrieveCurrentUserSession().getId(), date);
        return counted == null ? 0 : counted;
    }

    public void quitCourse(Integer learningCourseId) throws NoInstanceException {
//        LearningCourse learningCourse = this.get0(learningCourseId);
//        for (LearningLevel learningLevel : learningCourse.getLearningLevels()) {
//            for (LearningItem learningItem : learningLevel.getLearningItems()) {
//                learningItemService.delete(learningItem.getId());
//            }
//            learningLevelService.delete(learningLevel.getId());
//        }
        this.delete(learningCourseId);
    }

    public LinkedHashMap<String, Integer> getLearnerLearningCourseStatus(Integer courseId, Integer userId) {
        Integer numOfItems = courseService.getNumOfItems(courseId);
        Integer numOfLearntItem = learningItemService.getNumOfLearntItems(courseId, userId);
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("numOfItems", numOfItems);
        map.put("numOfLearntItem", numOfLearntItem);
        return map;
    }
}
