package com.vlls.service;

import com.vlls.exception.*;
import com.vlls.exception.UnsupportedOperationException;
import com.vlls.jpa.domain.Course;
import com.vlls.jpa.domain.Item;
import com.vlls.jpa.domain.Level;
import com.vlls.jpa.domain.User;
import com.vlls.jpa.repository.ItemRepository;
import com.vlls.jpa.repository.LevelRepository;
import com.vlls.web.model.LevelPageResponse;
import com.vlls.web.model.LevelResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class LevelService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private LevelRepository levelRepo;
    @Autowired
    private CourseService courseService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepo;
    @Autowired
    private UserService userService;

    @Transactional
    public LevelResponse create(String name, String description, int courseID) throws
            InvalidRequestItemException, NoInstanceException, UnauthenticatedException, ServerTechnicalException,
            UnauthorizedException {

        //check if current user is creator of this course or not
        securityService.permitUser(courseService.get0(courseID).getCreator());
        Level level = new Level();
        Course entity = courseService.get0(courseID);
        level.setName(name);
        level.setDescription(description);
        level.setCourse(entity);
        level.setLastUpdate(Calendar.getInstance().getTime());
        courseService.touch(entity);
        levelRepo.save(level);
        //Generating response
        LevelResponse levelResponse = new LevelResponse();
        levelResponse.setData(level);
        return levelResponse;
    }

    @Transactional
    public LevelResponse update(int levelID, String name, String description)
            throws InvalidRequestItemException, NoInstanceException, UnauthenticatedException, ServerTechnicalException,
            UnauthorizedException {

        //check if current user is creator of this course or not
        securityService.permitUser(levelRepo.findOne(levelID).getCourse().getCreator());
        Level level = levelRepo.findOne(levelID);
        if (level == null) {
            throw new NoInstanceException("Level", level);
        } else {
            level.setName(name);
            level.setDescription(description);
            this.touch(level);
            courseService.touch(level.getCourse());
        }
        //Generating response
        LevelResponse levelResponse = new LevelResponse();
        levelResponse.setData(level);
        return levelResponse;
    }

    public LevelResponse update(Integer id, String itemsJson) {
        return null;
    }

    public LevelPageResponse get(String name, int page, int pageSize) {

        Page<Level> levelPage = levelRepo.findByNameLike(name, new PageRequest(page, pageSize));
        LevelPageResponse levelPageResponse = new LevelPageResponse();
        levelPageResponse.from(levelPage);
        return levelPageResponse;
    }

    public LevelPageResponse getLevel(Integer id, Integer courseId) {

        LevelPageResponse levelPageResponse = new LevelPageResponse();
        // case 1: find only one by id
        if (id > 0) {
            Level level = levelRepo.findOne(id);
            levelPageResponse.from(level);
        } else {
            // case 2: find all levels by course id
            List<Level> levels = levelRepo.findByCourseId(courseId);
            levelPageResponse.from(levels);
        }
        return levelPageResponse;
    }

    @Transactional
    public LevelResponse deactivateLevel(Integer id, Integer itemId, Boolean isActive) throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        Level level = get0(id);
        User creator = userService.getCreatorByLevel0(id);
        List<Integer> items = itemService.getItemIdsByLevel0(itemId);
        securityService.permitUser(creator);

        // Update activation status
        level.setIsActive(isActive);
        for (Integer item : items) {
            itemService.deactivateItem(item, level.getIsActive());
        }
        // Generate response
        LevelResponse levelResponse = new LevelResponse();
        levelResponse.setData(level);
        return levelResponse;
    }

    @Transactional
    public boolean delete(int levelID)
            throws UnsupportedOperationException, UnauthenticatedException,
            ServerTechnicalException, UnauthorizedException {

        //check if current user is creator of this course or not
        securityService.permitUser(levelRepo.findOne(levelID).getCourse().getCreator());
        Level level = levelRepo.findOne(levelID);
        if (level == null) {
            throw new UnsupportedOperationException("Cannot delete level!");
        } else {
            Course course = level.getCourse();
            levelRepo.delete(level);
            courseService.touch(course);
            return true;
        }
    }

    Level get0(Integer id) throws NoInstanceException {
        Level level = levelRepo.findOne(id);
        if (level == null) {
            throw new NoInstanceException("Level", id);
        } else {
            return level;
        }
    }

    List<Level> getLevelsNotIn(List<Integer> ids, Integer courseId) {
        List<Level> notInLevels = levelRepo.findByIdNotInAndCourseId(ids, courseId);
        return notInLevels;
    }

    @Transactional
    public String importItems(MultipartFile csvFile, Integer id) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException, UnauthorizedException, ClientTechnicalException, DuplicatedItemException {

        List<String> lines;
        // Get level
        Level level = levelRepo.findOne(id);
        if (level == null) {
            throw new NoInstanceException("Level not exist");
        }
        // Read lines of string from csvFile
        try {
            lines = IOUtils.readLines(csvFile.getInputStream(), "UTF-8");
            lines.remove(0); // remove title
        } catch (IOException e) {
            throw new ServerTechnicalException("Error while processing file", e);
        }

        // Delete old items
        level.setItems(new ArrayList<>());
        itemService.deleteByLevel0(level.getId());

        // Parse into itemList
        for (String line : lines) {
            String[] stk = line.split(",");
            if (stk.length <= 4) {
                String itemName = stk[0];
                String itemMeaning = stk[1];
                String itemPronun = stk[2];
                String itemType = stk[3];
                Item item = itemService.create0(level, itemName, itemMeaning, itemType, itemPronun, "", null);
                level.getItems().add(item);
                this.touch(level);
                courseService.touch(level.getCourse());
            } else {
                throw new ServerTechnicalException("Please follow these format: name,meaning,pronun,type!! \n" +
                        "If any column has multiple content, please seperate with '-'");
            }
        }

        return String.format("{\"imported\": %d}", level.getItems().size());
    }

    public File exportItems(Integer id) throws NoInstanceException, ServerTechnicalException {
        // Get level
        Level level = levelRepo.findOne(id);
        if (level == null) {
            throw new NoInstanceException("Level not exist");
        }
        // Get itemList
        List<Item> items = level.getItems();
        // Transform into list of string with csv format
        List<String> lines = new ArrayList<>(items.size() + 1);
        lines.add("name,meaning,pronun,type");
        items.forEach(item -> {
            String line = item.getName() + "," + item.getMeaning() + ","
                    + item.getPronun() + "," + item.getType();
            lines.add(line);
        });
        // Persist to file
        try {
            File file = File.createTempFile("items", ".csv");
            FileUtils.writeLines(file, lines);

            // return the file
            return file;
        } catch (IOException e) {
            throw new ServerTechnicalException("Error while exporting file", e);
        }
    }

    //    public Integer getNumberOfItem(Integer levelId) {
//        return levelRepo.getNumOfLevelItems(levelId);
//    }
    public void touch(Level level) {
        level.setLastUpdate(null);
        level.setLastUpdate(Calendar.getInstance().getTime());
    }

    public void touch(Integer id) {
        Level level = levelRepo.findOne(id);
        level.setLastUpdate(null);
        level.setLastUpdate(Calendar.getInstance().getTime());
    }

    public Integer getNumOfLevelItem(Integer levelId) {
        return levelRepo.getNumOfLevelItems(levelId);
    }

}
