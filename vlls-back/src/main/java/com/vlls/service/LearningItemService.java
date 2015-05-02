package com.vlls.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.exception.UnauthorizedException;
import com.vlls.jpa.domain.*;
import com.vlls.jpa.repository.LearningItemRepository;
import com.vlls.jpa.repository.PictureRepository;
import com.vlls.service.model.ItemResult;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.LearnQuizItemResponse;
import com.vlls.web.model.LearningItemPageResponse;
import com.vlls.web.model.PictureResponse;
import com.vlls.web.model.QuizQuestionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;


/**
 * Created by Dell on 10/4/2014.
 */
@Service
public class LearningItemService extends AbstractService {

    private final Logger LOGGER = LoggerFactory.getLogger(LearningItemService.class);
    public static final int RIGHT_NO_TO_FINISH = 6;
    public static final int MAX_LEARN_TURN_ITEM = 5;
    //    public static final int[] REVISE_TIME = {2,2,2,2,2,2};
    public static final int[] REVISE_TIME = {10, 60, 60 * 5/*5 hours*/, 60 * 24/*1 day*/, 60 * 24 * 5/*5 days*/, 60 * 24 * 25/*25 days*/};
    //    public static final int ITEM_LEARNING_POINT = 10;
    public static final int[] ITEM_LEARNING_POINTS = {10, 15, 20, 25, 30, 35};
    //    public static final int ITEM_REVISING_POINT = 10;
    public static final int[] ITEM_REVISING_POINTS = {50, 100, 200, 350, 550, 800};

    @Autowired
    private LearningItemRepository learningItemRepository;
    @Autowired
    private LearningLevelService learningLevelService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private LearningCourseService learningCourseService;
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private PictureRepository pictureRepo;
    @Autowired
    private LikedPictureService likedPictureService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    LearningItem create0(Integer learningLevelId, Integer itemId)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        // Get learning level
        LearningLevel learningLevel = learningLevelService.getAndValidateLearner0(learningLevelId);
        // Get item
        Item item = itemService.get0(itemId);

        return create0(learningLevel, item);
    }

    @Transactional
    void createList0(LearningLevel managedLearningLevel) {
        List<Item> items = managedLearningLevel.getLevel().getItems();
        List<LearningItem> learningItems = new ArrayList<>();
        items.forEach(item -> {
            LearningItem learningItem = this.create0(managedLearningLevel, item);
            learningItems.add(learningItem);
        });
        managedLearningLevel.setLearningItems(learningItems);
    }

    @Transactional
    LearningItem create0(LearningLevel managedLearningLevel, Item managedItem) {
        LearningItem learningItem = new LearningItem();
        learningItem.setLearningLevel(managedLearningLevel);
        learningItem.setItem(managedItem);
        learningItem.setLastUpdate(Calendar.getInstance().getTime());
        learningItem.setRightNo(0);
        learningItem.setWrongNo(0);
        learningItemRepository.save(learningItem);
        managedLearningLevel.getLearningItems().add(learningItem);
        return learningItem;
    }

    public LearningItemPageResponse get(Integer id, Integer learningLevelId, int page, int pageSize)
            throws NoInstanceException {
        if (id >= 0) {
            LearningItem learningItem = this.get0(id);
            LearningItemPageResponse learningItemPageResponse = new LearningItemPageResponse();
            learningItemPageResponse.from(learningItem);
            return learningItemPageResponse;
        } else {
            Page<LearningItem> learningItems = learningItemRepository
                    .findByLearningLevelId(learningLevelId, new PageRequest(page, pageSize));
            LearningItemPageResponse learningItemPageResponse = new LearningItemPageResponse();
            learningItemPageResponse.from(learningItems);
            return learningItemPageResponse;
        }
    }

    LearningItem get0(Integer id) throws NoInstanceException {
        LearningItem learningItem = learningItemRepository.findOne(id);
        if (learningItem == null) {
            throw new NoInstanceException("Learning Item not found or deleted by creator", id);
        } else {
            return learningItem;
        }
    }

    public List<LearnQuizItemResponse> getLearningLevelTurn(Integer learningLevelId)
            throws NoInstanceException, UnauthorizedException, UnauthenticatedException, ServerTechnicalException {
        LearningLevel learningLevel = learningLevelService.getAndValidateLearner0(learningLevelId);

        List<LearningItem> learningItems = learningItemRepository.
                findByLearningLevelIdAndRightNoLessThan(learningLevelId, RIGHT_NO_TO_FINISH, MAX_LEARN_TURN_ITEM);

        List<LearnQuizItemResponse> learnQuizItemResponses = new ArrayList<>(learningItems.size());
        for (LearningItem learningItem : learningItems) {
            LearnQuizItemResponse learnQuizItemResponse = this.toLearnQuiz(
                    learningItem,
                    learningLevel.getLevel().getCourse().getId(),
                    learningLevel.getLevel().getId());
            learnQuizItemResponses.add(learnQuizItemResponse);
        }

        return learnQuizItemResponses;
    }

    public List<LearnQuizItemResponse> getLearningCourseTurn(Integer learningCourseId)
            throws NoInstanceException, UnauthorizedException, UnauthenticatedException, ServerTechnicalException {
        LearningCourse learningCourse = learningCourseService.getAndValidateLearner0(learningCourseId);

        List<LearningItem> learningItems = learningItemRepository.
                findByLearningCourseIdAndRightNoLessThan(learningCourseId, RIGHT_NO_TO_FINISH, MAX_LEARN_TURN_ITEM);

        List<LearnQuizItemResponse> learnQuizItemResponses = new ArrayList<>(learningItems.size());
        for (LearningItem learningItem : learningItems) {
            LearnQuizItemResponse learnQuizItemResponse = this.toLearnQuiz(
                    learningItem,
                    learningCourse.getCourse().getId(),
                    learningItem.getLearningLevel().getLevel().getId());
            learnQuizItemResponses.add(learnQuizItemResponse);
        }

        return learnQuizItemResponses;
    }

    public List<LearnQuizItemResponse> getLearningLevelReviseTurn(Integer learningLevelId)
            throws NoInstanceException, UnauthorizedException, UnauthenticatedException, ServerTechnicalException {
        LearningLevel learningLevel = learningLevelService.getAndValidateLearner0(learningLevelId);

        List<LearningItem> learningItems = learningItemRepository.
                findRevisedItemsByLearningLevelId(learningLevelId, RIGHT_NO_TO_FINISH, REVISE_TIME.length);

        List<LearnQuizItemResponse> learnQuizItemResponses = new ArrayList<>(learningItems.size());
        for (LearningItem learningItem : learningItems) {
            LearnQuizItemResponse learnQuizItemResponse = this.toReviseQuiz(
                    learningItem,
                    learningLevel.getLevel().getCourse().getId(),
                    learningLevel.getLevel().getId());
            learnQuizItemResponses.add(learnQuizItemResponse);
        }

        return learnQuizItemResponses;
    }

    public List<LearnQuizItemResponse> getLearningCourseReviseTurn(Integer learningCourseId)
            throws NoInstanceException, UnauthorizedException, UnauthenticatedException, ServerTechnicalException {

        List<LearningItem> learningItems = learningItemRepository.
                findRevisedItemsByLearningCourseId(learningCourseId, RIGHT_NO_TO_FINISH, REVISE_TIME.length);

        List<LearnQuizItemResponse> learnQuizItemResponses = new ArrayList<>(learningItems.size());
        for (LearningItem learningItem : learningItems) {
            LearningLevel learningLevel = learningItem.getLearningLevel();
            LearnQuizItemResponse learnQuizItemResponse = this.toReviseQuiz(
                    learningItem,
                    learningLevel.getLevel().getCourse().getId(),
                    learningLevel.getLevel().getId());
            learnQuizItemResponses.add(learnQuizItemResponse);
        }

        return learnQuizItemResponses;
    }

    LearnQuizItemResponse toLearnQuiz(LearningItem learningItem, Integer courseId, Integer levelId)
            throws ServerTechnicalException {
        LearnQuizItemResponse learnQuizItemResponse = new LearnQuizItemResponse();
        learnQuizItemResponse.setData(learningItem);

        List<QuizQuestionResponse> quizQuestionResponses = questionService.toQuizList0(learningItem, courseId, levelId);
        learnQuizItemResponse.setQuestions(quizQuestionResponses);

        return learnQuizItemResponse;
    }

    LearnQuizItemResponse toReviseQuiz(LearningItem learningItem, Integer courseId, Integer levelId)
            throws ServerTechnicalException {
        LearnQuizItemResponse learnQuizItemResponse = new LearnQuizItemResponse();
        learnQuizItemResponse.setData(learningItem);

        List<QuizQuestionResponse> quizQuestionResponses = questionService.toReviseQuizList0(learningItem, courseId, levelId);
        learnQuizItemResponse.setQuestions(quizQuestionResponses);

        return learnQuizItemResponse;
    }

    /**
     * Submit result of a learning turn.
     *
     * @param itemResult
     * @throws NoInstanceException
     * @throws UnauthorizedException
     * @throws UnauthenticatedException
     */
    @Transactional
    public void submitTurn(String itemResult)
            throws UnauthorizedException, UnauthenticatedException, ServerTechnicalException, NoInstanceException {

        boolean itemNotFound = false;
        boolean levelNotFound = false;
        boolean courseNotFound = false;

        UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();

        Set<Integer> relatedLearningLevelIds = new HashSet<>();
//        Set<Integer> ralatedLearningCourseIds = new HashSet<>();
        Map<Integer, Long> learningCourseReviseTimeMap = new HashMap<>();

        // Update learning item right_no, wrong_no
        int totalPoint = 0;
        List<ItemResult> itemResults;
        try {
            itemResults = objectMapper.readValue(itemResult,
                    TypeFactory.defaultInstance().constructCollectionType(List.class, ItemResult.class));
        } catch (IOException e) {
            throw new ServerTechnicalException("Error while processing request", e);
        }

        for (ItemResult result : itemResults) {
            if (result.getRight() + result.getWrong() > 0) {
                try {
                    LearningItem learningItem = this.get0(result.getId());
                    // Set related learning level & learning course
                    relatedLearningLevelIds.add(learningItem.getLearningLevel().getId());
//                ralatedLearningCourseIds.add(learningItem.getLearningLevel().getLearningCourse().getId());
                    learningCourseReviseTimeMap.put(learningItem.getLearningLevel().getLearningCourse().getId(), null);
                    // Get right & wrong no
                    Integer curRightNo = learningItem.getRightNo() == null ? 0 : learningItem.getRightNo();
                    Integer curWrongNo = learningItem.getWrongNo() == null ? 0 : learningItem.getWrongNo();
                    // Set right & wrong no
                    learningItem.setRightNo(curRightNo + result.getRight());
                    learningItem.setWrongNo(curWrongNo + result.getWrong());
                    // Calculate item point & revise no
                    if (curRightNo < RIGHT_NO_TO_FINISH) {
                        // learning turn
//                    totalPoint += result.getRight() * ITEM_LEARNING_POINT;
                        // simplify learn result map
                        while (contains(result.getLearnResultMap(), "00")) {
                            result.setLearnResultMap(replace(result.getLearnResultMap(), "00", "0"));
                        }
                        // calculate point
                        totalPoint += this.calculateLearningPoint(
                                learningItem.getLastLearnSessionResult(), curRightNo, result.getLearnResultMap());
                        // persist the last answer
                        learningItem.setLastLearnSessionResult(
                                result.getLearnResultMap().length() == 0
                                        || endsWith(result.getLearnResultMap(), "1"));
                        // start of revision
                        if (learningItem.getRightNo() >= RIGHT_NO_TO_FINISH) {
                            learningItem.setRevisedNo(0);
                        }
                    } else {
                        // revise turn
//                    totalPoint += result.getRight() + ITEM_REVISING_POINT;
                        // calculate revise no
                        if (result.getRight() > 0) {
                            int originPoint = ITEM_REVISING_POINTS[learningItem.getRevisedNo()];
                            double bonusRate = Math.max(0, 20 - (5 * Math.floor((System.currentTimeMillis() - learningItem.getNextReviseTime().getTime()) / (30 * 60000)))) / 100;
                            double bonusPoint = originPoint * bonusRate;
                            double penaltyPoint = originPoint * (learningItem.getPenaltyRate() == null ? 0 : learningItem.getPenaltyRate());
                            totalPoint += originPoint + bonusPoint - penaltyPoint;

                            // decrease penalty
                            if (learningItem.getPenaltyRate() != null) {
                                learningItem.setPenaltyRate(this.decreasePenalty(learningItem.getPenaltyRate()));
                            }
                            learningItem.setRevisedNo(learningItem.getRevisedNo() + 1);
                        } else {
                            // increase penalty
                            if (learningItem.getPenaltyRate() == null) {
                                learningItem.setPenaltyRate(0d);
                            }
                            if (learningItem.getPenaltyRate() == 0d && learningItem.getRevisedNo() > 0) {
                                // begin of continuous re-answer, user is back to previous milestone
                                // so penalty must be increase twice
                                learningItem.setPenaltyRate(this.increasePenalty(learningItem.getPenaltyRate()));
                            }
                            if (learningItem.getPenaltyRate() > 0d && learningItem.getRevisedNo() == 0) {
                                // do nothing
                                // continuous re-answering ends here, at milestone 0
                            } else {
                                learningItem.setPenaltyRate(this.increasePenalty(learningItem.getPenaltyRate()));
                            }
                            learningItem.setRevisedNo((learningItem.getRevisedNo() == 0) ?
                                    0 : learningItem.getRevisedNo() - 1);
                        }
                    }
                    // Calculate revise time
                    if (learningItem.getRevisedNo() != null) {
                        this.calculateReviseTime(learningItem);
                        // update learning course next revise time
                        Long currentLCReviseTime = learningCourseReviseTimeMap.get(learningItem.getLearningLevel().getLearningCourse().getId());
                        if (currentLCReviseTime == null) {
                            learningCourseReviseTimeMap.put(
                                    learningItem.getLearningLevel().getLearningCourse().getId(),
                                    learningItem.getNextReviseTime().getTime());
                        } else if (currentLCReviseTime > learningItem.getNextReviseTime().getTime()) {
                            learningCourseReviseTimeMap.put(
                                    learningItem.getLearningLevel().getLearningCourse().getId(),
                                    learningItem.getNextReviseTime().getTime());
                        }
                    }
                    this.touch(learningItem);
                } catch (NoInstanceException e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Learning item submitting not found", e);
                    }
                    itemNotFound = true;
                }
            }
        }

        // Update learning level progress
        for (Integer learningLevelId : relatedLearningLevelIds) {
            try {
                learningLevelService.updateLevelProgress(learningLevelId);
            } catch (NoInstanceException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Learning level submitting not found", e);
                }
                levelNotFound = true;
            }
        }
        // Update learning course progress
        for (Map.Entry<Integer, Long> learningCourseReviseTime : learningCourseReviseTimeMap.entrySet()) {
            try {
                // TODO: different learning courses must have different point. At the moment, system supports only 1 course
                learningCourseService.calculatePoint(
                        learningCourseReviseTime.getKey(), totalPoint, learningCourseReviseTime.getValue());
            } catch (NoInstanceException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Learning course submitting not found", e);
                }
                courseNotFound = true;
            }
        }
        // Update user point
        userService.calculatePoint(userSessionInfo.getId(), totalPoint);

        if (courseNotFound) {
            throw new NoInstanceException("Your learning course not found. Maybe creator deleted the course. Sorry for inconvenience");
        }
        if (levelNotFound) {
            throw new NoInstanceException("Your learning level not found. Maybe creator deleted. Sorry for inconvenience");
        }
        if (itemNotFound) {
            throw new NoInstanceException("Your learning progress is saved but some of your learning item(s) not found. Maybe creator deleted. Sorry for inconvenience");
        }
    }

    @Transactional
    void touch(LearningItem learningItem) {
        learningItem.setLastUpdate(new Date());
    }

    private int calculateLearningPoint(Boolean lastLearnSessionResult, int curRightNo, String learnResultMap) {
        int newPoint = 0;
        int offset = 0;
        // last answer of last turn was wrong, count it as first answer of this turn
        if (lastLearnSessionResult != null && !lastLearnSessionResult
                && learnResultMap.charAt(0) == '1') {
            learnResultMap = '0' + learnResultMap;
        }
        for (int i = curRightNo; (i - offset) < ITEM_LEARNING_POINTS.length && learnResultMap.length() > (i - curRightNo); ++i) {
            if (learnResultMap.charAt(i - curRightNo) == '1') { // right answer
                if (i - curRightNo > 0 && learnResultMap.charAt(i - curRightNo - 1) == '0'
                        && (i - offset - 1) >= 0) { // last answer is wrong
                    newPoint += ITEM_LEARNING_POINTS[i - offset - 1];
                } else { // last answer is right
                    newPoint += ITEM_LEARNING_POINTS[i - offset];
                }
            } else { // wrong answer
                ++offset;
            }
        }
        System.out.println(newPoint);
        return newPoint;
    }

    public double increasePenalty(double currentPenalty) {
        return Math.round((1 + currentPenalty) / 2 * 10000) / 10000d;
    }

    public double decreasePenalty(double currentPenalty) {
        return Math.round(((2 * currentPenalty) - 1) * 10000) / 10000d;
    }

    public Integer getNumOfLearntItems(Integer courseId, Integer userId) {
        return learningItemRepository.getNumOfLearntItems(RIGHT_NO_TO_FINISH, courseId, userId);
    }

    public Integer getNumOfLearnedItemInLevel(Integer learningLevelId) {
        return learningItemRepository.countLearntByLearningLevelId(
                learningLevelId, RIGHT_NO_TO_FINISH);
    }

//    public static void main(String[] args) throws IOException {
//        ObjectMapper objectMapper1 = new ObjectMapper();
//        List<ItemResult> results = objectMapper1.readValue("[{\"id\":1,\"right\":3}]", List.class);
//        System.out.println("");
//    }

    public Integer countByLearningLevelId(Integer learningLevelId) {
        return learningItemRepository.countByLearningLevelId(learningLevelId);
    }

    public Integer countByLearningCourseId(Integer learningCourseId) {
        return learningItemRepository.countByLearningCourseId(learningCourseId);
    }

    public Integer countLearntByLearningCourseId(Integer learningCourseId) {
        return learningItemRepository.countLearntByLearningCourseId(learningCourseId, RIGHT_NO_TO_FINISH);
    }

    public Integer countLearningByLearningCourseId(Integer learningCourseId) {
        return learningItemRepository.countLearningByLearningCourseId(learningCourseId, RIGHT_NO_TO_FINISH);
    }

    @Transactional
    public void calculateReviseTime(LearningItem learningItem) {
        Calendar now = Calendar.getInstance();
        if (learningItem.getRevisedNo() <= 5) {
            now.add(Calendar.MINUTE, REVISE_TIME[learningItem.getRevisedNo()]);
        }
        learningItem.setNextReviseTime(now.getTime());
    }

    @Transactional
    public PictureResponse setLearningItemPicture(Integer pictureId, Integer learningItemId)
            throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        LearningItem learningItem = this.get0(learningItemId);
        PictureResponse pictureResponse = new PictureResponse();
        if (pictureId > 0) {
            Picture picture = pictureRepo.findOne(pictureId);
            learningItem.setMemPicture(picture);
            LinkedHashMap<String, Object> status = likedPictureService.checkStatus(pictureId);
            pictureResponse.setData(picture);
            pictureResponse.setNumOfLikes((Integer) status.get("numOfLikes"));
            pictureResponse.setIsLiked((Boolean) status.get("isLiked"));
        } else {
            learningItem.setMemPicture(null);
            pictureResponse.setId(0);
        }
        return pictureResponse;
    }

    List<Object[]> getReviseItemsNumber() throws UnauthenticatedException {
        return learningItemRepository.findRevisedItemsNumberByUserId(
                securityService.retrieveCurrentUserSession().getId(), RIGHT_NO_TO_FINISH, REVISE_TIME.length);
    }

    long countReviseItemsNumberAfter(Date date) throws UnauthenticatedException {
        Long counted = learningItemRepository.countRevisedItemsNumberByUserIdAfter(
                securityService.retrieveCurrentUserSession().getId(), RIGHT_NO_TO_FINISH, REVISE_TIME.length, date);
        return counted == null ? 0 : counted;
    }

    public static void main(String[] args) {
//        System.out.println(Math.max(0, System.currentTimeMillis() - 1418796486070l));
//        System.out.println(Math.max(0, Math.floor((System.currentTimeMillis() - 1418796486070l)/(30*60000))));
//        System.out.println(Math.max(0, 20 - (5 * Math.floor((System.currentTimeMillis() - 1418796486070l)/(30*60000)))));
        System.out.println(increasePenaltyS(0));
        System.out.println(increasePenaltyS(0.50));
        System.out.println(increasePenaltyS(0.75));
        System.out.println(increasePenaltyS(0.875));
        System.out.println(increasePenaltyS(0.9375));
        System.out.println(increasePenaltyS(0.96875));
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<");

        System.out.println(decreasePenaltyS(0.98438));
        System.out.println(decreasePenaltyS(0.96875));
        System.out.println(decreasePenaltyS(0.9375));
        System.out.println(decreasePenaltyS(0.875));
        System.out.println(decreasePenaltyS(0.750));
        System.out.println(decreasePenaltyS(0.500));
        System.out.println(contains(null, "00"));
//        System.out.println(calc(null,0, "01010101") == 55 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,2, "01010101") == 90 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,2, "010101") == 60 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,2, "10101") == 65 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,2, "110") == 45 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,2, "111") == 75 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,3, "111") == 90 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,4, "0101") == 55 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,4, "11") == 65 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,4, "101") == 60 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,5, "1") == 35 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,5, "01") == 30 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,5, "010") == 30 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,0, "111111") == 135 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,0, "1011") == 40 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(null,0, "10101") == 35 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(false,3, "111") == 85 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(false,3, "0111") == 85 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(true,3, "111") == 90 ? "OK" : "xxxxxFAILxxxxx");
//        System.out.println(calc(true,3, "0111") == 85 ? "OK" : "xxxxxFAILxxxxx");
    }

    public static double increasePenaltyS(double currentPenalty) {
        return Math.round((1 + currentPenalty) / 2 * 10000) / 10000d;
    }

    public static double decreasePenaltyS(double currentPenalty) {
        return Math.round(((2 * currentPenalty) - 1) * 10000) / 10000d;
    }

    public static int calc(Boolean lastLearnSessionResult, int curRightNo, String learnTurnResult) {
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(curRightNo + " - " + learnTurnResult);
        int newPoint = 0;
        int offset = 0;
        // last answer of last turn was wrong, count it as first answer of this turn
        if (lastLearnSessionResult != null && !lastLearnSessionResult
                && learnTurnResult.charAt(0) == '1') {
            learnTurnResult = '0' + learnTurnResult;
        }
        for (int i = curRightNo; (i - offset) < ITEM_LEARNING_POINTS.length && learnTurnResult.length() > (i - curRightNo); ++i) {
            if (learnTurnResult.charAt(i - curRightNo) == '1') { // right answer
                if (i - curRightNo > 0 && learnTurnResult.charAt(i - curRightNo - 1) == '0'
                        && (i - offset - 1) >= 0) { // last answer is wrong
                    newPoint += ITEM_LEARNING_POINTS[i - offset - 1];
                    System.out.println("Add " + ITEM_LEARNING_POINTS[i - offset - 1]);
                } else { // last answer is right
                    newPoint += ITEM_LEARNING_POINTS[i - offset];
                    System.out.println("Add " + ITEM_LEARNING_POINTS[i - offset]);
                }
            } else { // wrong answer
                ++offset;
            }
        }
        System.out.println(newPoint);
        return newPoint;
    }

    void delete(Integer id) {
        learningItemRepository.delete(id);
        learningItemRepository.delete(id);
    }

    List<Object[]> getReviseItem(Integer userId) {
        return learningItemRepository.getReviseItemDetail(userId, RIGHT_NO_TO_FINISH, REVISE_TIME.length);
    }
}
