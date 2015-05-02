package com.vlls.service;

import com.vlls.exception.*;
import com.vlls.jpa.domain.*;
import com.vlls.jpa.repository.ItemRepository;
import com.vlls.jpa.repository.PictureRepository;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.ItemPageResponse;
import com.vlls.web.model.ItemResponse;
import com.vlls.web.model.PictureResponse;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Hoang Phi on 9/28/2014.
 */
@Service
public class ItemService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private LevelService levelService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private PictureRepository pictureRepo;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikedPictureService likedPictureService;
    @Autowired
    private CourseService courseService;

    public ItemPageResponse get(Integer courseId, Integer levelId, Integer id, String filterKey, int page, int pageSize)
            throws NoInstanceException {
        // case 1: get one by Id
        if (id > 0) {
            Item item = this.get0(id);
            ItemPageResponse itemPageResponse = new ItemPageResponse();
            itemPageResponse.from(item);
            return itemPageResponse;
        } else { // case 2: get list by level id
            filterKey = this.wildcardSearchKey(filterKey);
            PageRequest pageRequest = new PageRequest(page, pageSize);
            Page<Item> itemPage;
            if (levelId > 0) {
                itemPage = itemRepository.
                        findByLevelIdAndNameLikeOrMeaningLike(levelId, filterKey, filterKey, pageRequest);
            } else {
                itemPage = itemRepository.findByCourseIdAndNameLikeOrMeaningLike(courseId, filterKey, filterKey, pageRequest);
            }
            ItemPageResponse itemPageResponse = new ItemPageResponse();
            itemPageResponse.from(itemPage);
            return itemPageResponse;
        }
    }

    @Transactional
    public ItemResponse create(Integer levelId, String name, String meaning, String type, String pronun,
                               String audioName, MultipartFile audioFile)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException, ServerTechnicalException,
            DuplicatedItemException, ClientTechnicalException {
        // Get level
        Level level = levelService.get0(levelId);

        Item item = this.create0(level, name, meaning, type, pronun, audioName, audioFile);

        item.setLastUpdate(Calendar.getInstance().getTime());
        levelService.touch(level);
        courseService.touch(level.getCourse());

        // Generate response
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setData(item);
        return itemResponse;
    }

    @Transactional
    Item create0(Level level, String name, String meaning, String type, String pronun, String audioName,
                 MultipartFile audioFile)
            throws UnauthenticatedException, UnauthorizedException, DuplicatedItemException, ClientTechnicalException,
            ServerTechnicalException {
        // Validate course creator
        securityService.permitUser(level.getCourse().getCreator());

        //Validate if item name already existed or not
        /**
         * 1. Get current course of current level
         * 2. Get all level of current course
         * 3. Get all item in all level of current course
         * 4. Search for item name existence
         * 5. If true -> throw exception; If false -> continue
         *
         * TODO: optimize data acquisition
         */
        Course course = level.getCourse();
//        List<Level> levels = course.getLevels();
//        List<Item> itemCourse = new ArrayList<>();
//        for (Level currentLevel : levels) {
//            List<Item> currentLevelItems = currentLevel.getItems();
//            for (Item item : currentLevelItems) {
//                itemCourse.add(item);
//            }
//        }
//        for (Item item : itemCourse) {
//            if (item.getName().equals(name) ) {
//                throw new DuplicatedItemException("Item has already existed in course!");
//            }
//        }

        Item item = new Item();
        item.setName(name);
        item.setMeaning(meaning);
        item.setPronun(pronun);
        item.setType(type);
        item.setLevel(level);
        itemRepository.save(item);

        // Persist audio file
        if (audioFile != null && !audioFile.isEmpty() && isNotEmpty(audioName)) {
            byte[] bytes = this.retrieveBytes(audioFile);
            String itemLocation = this.vllsProperties.getProperty("file.item.audio.location");
            UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();
            String audioFilePath = String.format("%s/%s-%s-%s-%s",
                    itemLocation, item.getId(), userSessionInfo.getId(), System.currentTimeMillis(), audioName);
            this.persistFile(audioFilePath, bytes);
            String globalLocation = substring(audioFilePath, indexOf(audioFilePath, "assets"));
            item.setAudio(globalLocation);
        } else {
            String dictAudioLocation = this.vllsProperties.getProperty("file." + course.getLangTeach().getCode() + ".audio.location");
            if (isNotEmpty(dictAudioLocation)) {
                String audioFilePath = dictAudioLocation + "/" + lowerCase(item.getName()) + ".wav";
                File dictAudioFile = new File(audioFilePath);
                if (dictAudioFile.exists()) {
                    String globalLocation = substring(audioFilePath, indexOf(audioFilePath, "assets"));
                    item.setAudio(globalLocation);
                }
            }
        }

        // Generate default questions
        questionService.createDefaultQuestions0(item);

        item.setLastUpdate(null);

        return item;
    }

    @Transactional
    public ItemResponse update(Integer id, String name, String meaning, String pronun, String type,
                               Boolean isAudioRemoved, String audioName, MultipartFile audioFile)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException, ClientTechnicalException,
            ServerTechnicalException {
        Item item = get0(id);
        User creator = userService.getCreatorByItem0(item.getId());

        securityService.permitUser(creator);

        // Update default question if needed
        if (!equals(name, item.getName())
                || !equals(meaning, item.getMeaning())) {
            item.setName(name);
            item.setMeaning(meaning);
            questionService.updateDefaultQuestions0(item);
        }

        // Remove the audio file if needed
        if (isAudioRemoved) {
            this.removeItemAudio(item);
        }

        // Update audio if needed
        if (audioFile != null && !audioFile.isEmpty() && isNotEmpty(audioName)) {
            byte[] bytes = this.retrieveBytes(audioFile);
            String itemLocation = this.vllsProperties.getProperty("file.item.audio.location");

            // Remove the old file
            this.removeItemAudio(item);

            UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();
            String imageFilePath = String.format("%s/%s-%s-%s-%s",
                    itemLocation, item.getId(), userSessionInfo.getId(), System.currentTimeMillis(), audioName);
            this.persistFile(imageFilePath, bytes);
            String globalLocation = substring(imageFilePath, indexOf(imageFilePath, "assets"));
            item.setAudio(globalLocation);
        }

        item.setPronun(pronun);
        item.setType(type);

        this.touch(item);
        levelService.touch(item.getLevel());
        courseService.touch(item.getLevel().getCourse());

        // Generate response
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setData(item);
        return itemResponse;
    }

    private void removeItemAudio(Item item) {
        String itemLocation = this.vllsProperties.getProperty("file.item.audio.location");
        if (isNotEmpty(item.getAudio())) {
            String fileName = substring(item.getAudio(), lastIndexOf(item.getAudio(), '/'));
            String filePath = itemLocation + fileName;
            if (!FileUtils.deleteQuietly(new File(filePath))) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Cannot delete item audio file: " + filePath);
                }
            }
            item.setAudio(EMPTY);
        }
    }

    @Transactional
    public void deleteItems(List<Item> items) {
        itemRepository.delete(items);
    }

    @Transactional
    public ItemResponse deactivateItem(Integer id, Boolean isActive) throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        Item item = get0(id);
        User creator = userService.getCreatorByItem0(id);
        securityService.permitUser(creator);

        // Update activation status
        item.setIsActive(isActive);

        // Generate response
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setData(item);
        return itemResponse;
    }

    @Transactional
    public void delete(Integer id) throws UnauthenticatedException, UnauthorizedException, NoInstanceException {
        User creator = userService.getCreatorByItem0(id);
        securityService.permitUser(creator);
        Level level = itemRepository.findOne(id).getLevel();
        Course course = level.getCourse();
        itemRepository.delete(id);

        levelService.touch(level);
        courseService.touch(course);
    }

    void deleteByLevel0(Integer levelId) throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        User creator = userService.getCreatorByLevel0(levelId);
        securityService.permitUser(creator);
        itemRepository.deleteByLevelId(levelId);
    }

    public void touch(Item item) {
        item.setLastUpdate(null);
        item.setLastUpdate(Calendar.getInstance().getTime());
    }

    public void touch(Integer id) {
        Item item = itemRepository.findOne(id);
        item.setLastUpdate(null);
        item.setLastUpdate(Calendar.getInstance().getTime());
    }

    Item get0(Integer id) throws NoInstanceException {
        Item item = itemRepository.getOne(id);
        if (item == null) {
            throw new NoInstanceException("Item", id);
        } else {
            return item;
        }
    }

    List<Item> getList0(Integer[] ids) throws NoInstanceException {
        List<Item> items = new ArrayList<>();
        for (Integer id : ids) {
            Item item = this.get0(id);
            items.add(item);
        }
        return items;
    }

    List<Integer> getItemIdsByLevel0(Integer id) {
        return itemRepository.findIdsByLevelId(id);
    }

    List<Integer> getItemIdsByCourse0(Integer id) {
        return itemRepository.findIdsByCourseId(id);
    }

    List<Integer> getIncorrectAnswerIdsByQuestion0(Integer questionId) {
        return itemRepository.findIncorrectAnswerIdsByQuestionId(questionId);
    }

    List<String> getItemsNamesByIds0(List<Integer> ids) {
        return itemRepository.findNamesByIds(ids);
    }

    List<String> getItemsMeaningsByIds0(List<Integer> ids) {
        return itemRepository.findMeaningsByIds(ids);
    }

    List<Item> getItemsNotIn(List<Integer> ids, Integer levelId) {
        return itemRepository.findByIdNotInAndLevelId(ids, levelId);
    }

    List<Integer> removeFromListIfNameEqual(List<Integer> incorrectIds, String name) {
        return itemRepository.removeFromListIfNameEqual(incorrectIds, name);
    }

    //////////////////////////// ITEM IMAGE ////////////////////////////

    public List<PictureResponse> getImg(Integer itemId) throws NoInstanceException, UnauthenticatedException, ServerTechnicalException {
        List<Picture> pictures = this.getImg1(itemId);
        List<PictureResponse> pictureResponses = new ArrayList<PictureResponse>();
        for (Picture pic : pictures) {
            PictureResponse pictureResponse = new PictureResponse();
            pictureResponse.setData(pic);
            LinkedHashMap<String, Object> linkedHashMap = likedPictureService.checkStatus(pic.getId());
            pictureResponse.setNumOfLikes((Integer) linkedHashMap.get("numOfLikes"));
            pictureResponse.setIsLiked((Boolean) linkedHashMap.get("isLiked"));
            pictureResponses.add(pictureResponse);
        }
        return pictureResponses;
    }

    List<Picture> getImg1(Integer itemId) throws NoInstanceException {
        Item item = this.get0(itemId);
        return item.getPictures();
    }

    Picture getImg0(Integer id) throws NoInstanceException {
        Picture picture = pictureRepo.findOne(id);
        if (picture == null) {
            throw new NoInstanceException("Picture", id);
        } else {
            return picture;
        }
    }

    @Transactional
    public PictureResponse createImg(Integer itemId, MultipartFile multipartFile, String fileName, String htmlFormat)
            throws ServerTechnicalException, NoInstanceException, ClientTechnicalException, UnauthorizedException,
            UnauthenticatedException {

        // Retrieve current logged in user
        User user = securityService.retrieveCurrentUser0();

        // Retrieve item
        Item item = this.get0(itemId);

        // Save createImg to local disk
        byte[] bytes = this.retrieveBytes(multipartFile);
        String itemLocation = this.vllsProperties.getProperty("file.item.img.location");
        String imageFilePath = String.format("%s/%s-%s-%s-%s",
                itemLocation, itemId, user.getId(), System.currentTimeMillis(), fileName);
        this.persistFile(imageFilePath, bytes);
        String globalLocation = substring(imageFilePath, indexOf(imageFilePath, "assets"));

        // Save picture using cascade PERSIST of Item
        Picture picture = new Picture();
        picture.setUploader(user);
        picture.setItem(item);
        item.setLastUpdate(null);

        item.getLevel().setLastUpdate(null);
        item.getLevel().getCourse().setLastUpdate(null);
        item.setLastUpdate(Calendar.getInstance().getTime());
        item.getLevel().setLastUpdate(Calendar.getInstance().getTime());
        item.getLevel().getCourse().setLastUpdate(Calendar.getInstance().getTime());

        picture.setSource(globalLocation);
        picture.setHtmlFormat(htmlFormat);

        item.getPictures().add(picture);

        PictureResponse pictureResponse = new PictureResponse();
        pictureResponse.setData(pictureRepo.save(picture));
        return pictureResponse;
    }

    @Transactional
    public PictureResponse updateImg(Integer id, String htmlFormat)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        // Get the picture
        Picture picture = this.getImg0(id);

        // Get current logged user
        UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();

        // Validate uploader
        if (picture.getUploader().getId() != userSessionInfo.getId()) {
            throw new UnauthenticatedException("User cannot edit image uploaded by others");
        }

        // Update image
        picture.setHtmlFormat(htmlFormat);

        PictureResponse pictureResponse = new PictureResponse();
        pictureResponse.setData(picture);
        return pictureResponse;
    }

    public void deleteImg(Integer id)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        // Get the picture
        Picture picture = this.getImg0(id);

        // Get current logged user
        UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();

        // Validate uploader
        if (picture.getUploader().getId() != userSessionInfo.getId()) {
            throw new UnauthenticatedException("User cannot delete image uploaded by others");
        }

        pictureRepo.delete(picture);
    }

    //////////////////////////// ITEM AUDIO ////////////////////////////
}
