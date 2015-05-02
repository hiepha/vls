package com.vlls.service;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.exception.UnauthorizedException;
import com.vlls.jpa.domain.Item;
import com.vlls.jpa.domain.LearningCourse;
import com.vlls.jpa.domain.LearningLevel;
import com.vlls.jpa.domain.Level;
import com.vlls.jpa.repository.LearningLevelRepository;
import com.vlls.web.model.LearningLevelPageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hiephn on 2014/10/07.
 */
@Service
public class LearningLevelService extends AbstractService {

    @Autowired
    private LearningLevelRepository learningLevelRepository;
    @Autowired
    private LevelService levelService;
    @Autowired
    private LearningCourseService learningCourseService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private LearningItemService learningItemService;
    @Autowired
    private ItemService itemService;

    /**
     * @param managedLearningCourse
     */
    @Transactional
    void createList0(LearningCourse managedLearningCourse) {
        List<Level> levels = managedLearningCourse.getCourse().getLevels();
        List<LearningLevel> learningLevels = new ArrayList<>();
        levels.forEach(level -> {
            LearningLevel learningLevel = this.create0(managedLearningCourse, level);
            learningLevels.add(learningLevel);
        });

        managedLearningCourse.setLearningLevels(learningLevels);
    }

    @Transactional
    LearningLevel create0(Integer learningCourseId, Integer levelId) throws NoInstanceException {
        // Get Course
        LearningCourse learningCourse = learningCourseService.get0(learningCourseId);
        // Get level
        Level level = levelService.get0(levelId);
        return create0(learningCourse, level);
    }

    @Transactional
    LearningLevel create0(LearningCourse managedLearningCourse, Level managedLevel) {

        LearningLevel learningLevel = new LearningLevel();
        learningLevel.setLearningCourse(managedLearningCourse);
        learningLevel.setLevel(managedLevel);
        learningLevel.setLastUpdate(new Date());
        learningLevel.setLastSync(new Date());
        learningLevel.setLearningItems(new ArrayList<>());
        learningLevelRepository.save(learningLevel);
        managedLearningCourse.getLearningLevels().add(learningLevel);

        // Create list of Learning Item
        learningItemService.createList0(learningLevel);

        return learningLevel;
    }

    LearningLevel getAndValidateLearner0(Integer learningLevelId) throws NoInstanceException,
            UnauthenticatedException, UnauthorizedException {
        // Get learning level
        LearningLevel learningLevel = this.get0(learningLevelId);

        // Validate if this learning level is actually belongs to current user
        securityService.permitUser(learningLevel.getLearningCourse().getUser());

        return learningLevel;
    }

    LearningLevel getAndValidateLearner0(Integer learningCourseId, Integer levelId)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        // Get learning level
        LearningLevel learningLevel = this.get0(learningCourseId, levelId);

        // Validate if this learning level is actually belongs to current user
        securityService.permitUser(learningLevel.getLearningCourse().getUser());

        return learningLevel;
    }

    /**
     * Prevent other services to call this method.
     * They must use {@link #getAndValidateLearner0(Integer)} instead.
     *
     * @param learningLevelId
     * @return
     * @throws NoInstanceException
     */
    private LearningLevel get0(Integer learningLevelId) throws NoInstanceException {
        LearningLevel learningLevel = learningLevelRepository.findOne(learningLevelId);
        if (learningLevel == null) {
            throw new NoInstanceException("Learning Level", learningLevelId);
        } else {
            return learningLevel;
        }
    }

    private LearningLevel get0(Integer learningCourseId, Integer levelId) throws NoInstanceException {
        LearningLevel learningLevel = learningLevelRepository.
                findByLearningCourseIdAndLevelId(learningCourseId, levelId);
        if (learningLevel == null) {
            throw new NoInstanceException("Learning Level not found or deleted by creator");
        } else {
            return learningLevel;
        }
    }

    public LearningLevelPageResponse get(Integer learningLevelId, Integer learningCourseId,
                                         Integer levelId, int page, int pageSize)
            throws NoInstanceException {
        if (learningLevelId > 0) {
            LearningLevel learningLevel = this.get0(learningLevelId);
            LearningLevelPageResponse learningLevelPageResponse = new LearningLevelPageResponse();
            learningLevelPageResponse.from(learningLevel);
            return learningLevelPageResponse;
        } else {
            if (levelId > 0) {
                LearningLevel learningLevel = this.get0(learningCourseId, levelId);
                LearningLevelPageResponse learningLevelPageResponse = new LearningLevelPageResponse();
                learningLevelPageResponse.from(learningLevel);
                return learningLevelPageResponse;
            } else {
                Page<LearningLevel> learningLevels = learningLevelRepository.
                        findByLearningCourseId(learningCourseId, new PageRequest(page, pageSize));
                LearningLevelPageResponse learningLevelPageResponse = new LearningLevelPageResponse();
                learningLevelPageResponse.from(learningLevels);
                return learningLevelPageResponse;
            }
        }
    }

/*    public LinkedHashMap<String, Integer> getLearningLevelStatus(Integer levelId, Integer learningLevelId, Integer learningCourseId) throws UnauthenticatedException, ServerTechnicalException {
        Integer userId = securityService.retrieveCurrentUser0().getId();
        Integer totalLevelItem = levelService.getNumOfLevelItem(levelId);
        Integer numOfLearnedItem = learningItemService.getNumOfLearnedItemInLevel(learningLevelId, learningCourseId);
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("totalLevelItem", totalLevelItem);
        map.put("numOfLearnedItem", numOfLearnedItem);
        return map;
    }*/

/*    // Create a method to test level status
    public Integer getLevelStatus(Integer learningLevelId) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        LearningLevel learningLevel = get0(learningLevelId);
        Integer totalLearningLevelItem = learningItemService.countByLearningLevelId(learningLevelId);
        Integer numOfLearnedItem = learningItemService.getNumOfLearnedItemInLevel(learningLevelId);
        Integer levelStatus = (int)((numOfLearnedItem * 100.0f) / totalLearningLevelItem);
        learningLevel.setProgress(levelStatus);
        learningLevelRepository.save(learningLevel);
        return levelStatus;
    }*/

    // Create a method to test level status DEMO
    public Integer getLevelStatus(Integer learningLevelId) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        LearningLevel learningLevel = get0(learningLevelId);
        Integer totalLevelItem = levelService.getNumOfLevelItem(learningLevelId);
        Integer numOfLearnedItem = learningItemService.getNumOfLearnedItemInLevel(learningLevelId);
        Integer levelStatus = (int) ((numOfLearnedItem * 100.0f) / totalLevelItem);
        learningLevel.setProgress(levelStatus);
        learningLevelRepository.save(learningLevel);
        return levelStatus;
    }

    @Transactional
    public void updateLevelProgress(Integer learningLevelId) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        LearningLevel learningLevel = get0(learningLevelId);
        Integer totalLearningLevelItem = learningItemService.countByLearningLevelId(learningLevelId);
        Integer numOfLearnedItem = learningItemService.getNumOfLearnedItemInLevel(learningLevelId);
        Integer levelStatus = (int) ((numOfLearnedItem * 100.0f) / totalLearningLevelItem);
        learningLevel.setProgress(levelStatus);
        this.touch(learningLevel);
    }

    @Transactional
    void touch(LearningLevel learningLevel) {
        learningLevel.setLastUpdate(new Date());
    }

    /**
     * Get number of learning item need to be revised in learning level
     *
     * @param learningLevelId
     * @return
     */
    public Integer getNumOfReviseItems(Integer learningLevelId) {
        return learningLevelRepository.getNumOfReviseItems(learningLevelId);
    }

    public void synchronize(Integer id) throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        LearningLevel learningLevel = this.get0(id);
        this.synchronize(learningLevel);
    }

    /**
     * Update number of learning items in learning levels that match with master data
     */
    @Transactional
    public void synchronize(LearningLevel learningLevel) throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        Level level = learningLevel.getLevel();

        Date levelLastUpdate = level.getLastUpdate();
        //Get last sync of that learning level
        Date learningLevelLastSync = learningLevel.getLastSync();

        if (levelLastUpdate.after(learningLevelLastSync)) {
            // Need to sync

            // ITEM is deleted: Do nothing

            // item is updated: Do nothing

            // item is added:

            //Get all item id belong to learning level
            List<Integer> ids = learningLevelRepository.findItemId(learningLevel);

            //Get all items of level that currently not in learning level
            List<Item> toBeAddedItems;
            if (ids.size() > 0) {
                toBeAddedItems = itemService.getItemsNotIn(ids, learningLevel.getLevel().getId());
            } else {
                toBeAddedItems = learningLevel.getLevel().getItems();
            }

            //Add all new items to learning level
            for (Item item : toBeAddedItems) {
                learningItemService.create0(learningLevel, item);
            }

            //Update last_sync
            learningLevel.setLastSync(Calendar.getInstance().getTime());
            this.touch(learningLevel);
        }
    }

    void delete(Integer id) {
        learningLevelRepository.delete(id);
    }
}
