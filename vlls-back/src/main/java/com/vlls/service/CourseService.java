package com.vlls.service;

import com.vlls.exception.*;
import com.vlls.jpa.domain.*;
import com.vlls.jpa.repository.CourseRepository;
import com.vlls.jpa.repository.LearningCourseRepository;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.CoursePageResponse;
import com.vlls.web.model.CourseResponse;
import com.vlls.web.model.LearningCoursePageResponse;
import com.vlls.web.model.LearningCourseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.PropertyResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class CourseService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);
    private static final String DEFAULT_COURSE_AVATAR = "assets/img/avatar/course-0.png";
    @Resource(name = "vllsPropertyResolver")
    protected PropertyResolver vllsProperties;
    @Autowired
    private SimilarCourseService similarCourseService;
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    @Lazy
    private LearningCourseService learningCourseService;
    @Autowired
    private LearningCourseRepository learningCourseRepo;

    @Transactional
    public CourseResponse create(
            String name,
            Integer categoryId,
            Integer langTeachId,
            Integer langSpeakId,
            // OPTIONAL
            String description
    ) throws NoInstanceException, ServerTechnicalException, UnauthenticatedException {
        Course course = new Course();
        course.setName(name);
        course.setDescription(description);
        course.setIsPublic(false);
        course.setNumOfStudent(0);
        course.setRecommended(0);

        // Get Language Teach
        Language langTeach = languageService.get0(langTeachId);
        course.setLangTeach(langTeach);

        // Get Language Speak
        Language langSpeak = languageService.get0(langSpeakId);
        course.setLangSpeak(langSpeak);

        // Get Category
        Category category = categoryService.get0(categoryId);
        course.setCategory(category);

        // Get creator
        User creator = securityService.retrieveCurrentUser0();
        course.setCreator(creator);

        // avatar
        course.setAvatar(DEFAULT_COURSE_AVATAR);

        course.setLastUpdate(Calendar.getInstance().getTime());
        course.setCreatedDate(Calendar.getInstance().getTime());
        courseRepo.save(course);

        this.createSimilarRateList(course);
        //Generate response
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setData(course);
        return courseResponse;
    }

    @Transactional
    public CourseResponse update(
            Integer id,
            String name,
            Integer categoryId,
            Integer langTeachId,
            Integer langSpeakId,
            String description,
            Boolean isPublic
    ) throws NoInstanceException, UnauthenticatedException, ServerTechnicalException, UnauthorizedException {

        // Get course by ID. If not found, throw NoInstanceException
        Course course = get0(id);

        //check if current user is creator of this course or not
        securityService.permitUser(course.getCreator());

        course.setName(name);
        course.setDescription(description);
        course.setIsPublic(isPublic);

        // Get Language Teach
        Language langTeach = languageService.get0(langTeachId);
        course.setLangTeach(langTeach);

        // Get Language Speak
        Language langSpeak = languageService.get0(langSpeakId);
        course.setLangSpeak(langSpeak);

        // Get Category
        Category category = categoryService.get0(categoryId);
        course.setCategory(category);

        //Set last update time
        this.touch(course);

        // Asynchronous
        similarCourseService.updateCreateSimilarRateAsync(course, id);

        // Generate response
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setData(course);
        return courseResponse;
    }

    @Transactional
    public CourseResponse deactivateCourse(Integer id, Boolean isActive) throws
            NoInstanceException, UnauthenticatedException, UnauthorizedException {
        Course course = get0(id);
        securityService.permitUser(course.getCreator());

        // Update activation status
        course.setIsActive(isActive);

        //Generate response
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setData(course);
        return courseResponse;
    }

    @Transactional
    public CoursePageResponse get(
            Integer id,
            String name,
            Integer categoryId,
            Integer langTeachId,
            Integer langSpeakId,
            int page,
            int pageSize,
            String filter,
            Boolean withLearning
    ) throws UnauthenticatedException, UnauthorizedException, NoInstanceException, InvalidRequestItemException {

        name = this.wildcardSearchKey(name);
        if (id > 0) { // case 1: id present, return only one
            Course course = courseRepo.findOne(id);
            CoursePageResponse coursePageResponse = new CoursePageResponse();
            coursePageResponse.from(course);

            // If is user mode and user is learning this course
            // inject some information
            if (securityService.isUser() && withLearning) {
                UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSessionQuietly();
                LearningCourse learningCourse = learningCourseService.getQuietly0(userSessionInfo.getId(), course.getId());
                if (learningCourse != null) {
                    // transaction conflict with a call of GET learning course
                    // learningCourseService.synchronize(learningCourse);
                    LearningCourseResponse learningCourseResponse = new LearningCourseResponse(true);
                    coursePageResponse.getDataList().get(0).setLearningCourse(learningCourseResponse);
                    coursePageResponse.getDataList().get(0).getLearningCourse().setData(learningCourse);
                }
            }

            return coursePageResponse;
        } else {
            PageRequest pageRequest;
            Page<Course> coursePage;
            if (isEmpty(filter)) {
                pageRequest = new PageRequest(page, pageSize);
                coursePage = courseRepo.findByCategoryAndLangTeachAndLangSpeakAndName(
                        categoryId, langTeachId, langSpeakId, name, pageRequest);
            } else if (equalsIgnoreCase(filter, "popular")) {
                pageRequest = new PageRequest(page, pageSize, Sort.Direction.DESC, "numOfStudent");
                coursePage = courseRepo.findByCategoryAndLangTeachAndLangSpeakAndName(
                        categoryId, langTeachId, langSpeakId, name, pageRequest);
            } else if (equalsIgnoreCase(filter, "latest")) {
                pageRequest = new PageRequest(page, pageSize, Sort.Direction.DESC, "createdDate");
                coursePage = courseRepo.findByCategoryAndLangTeachAndLangSpeakAndName(
                        categoryId, langTeachId, langSpeakId, name, pageRequest);
            } else if (equalsIgnoreCase(filter, "rated")) {
                pageRequest = new PageRequest(page, pageSize, Sort.Direction.DESC, "rating", "numOfStudent");
                coursePage = courseRepo.findByCategoryAndLangTeachAndLangSpeakAndName(
                        categoryId, langTeachId, langSpeakId, name, pageRequest);
            } else if (equalsIgnoreCase(filter, "recommend")) {
                UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();
                pageRequest = new PageRequest(page, pageSize);
                coursePage = courseRepo.findByRatingRateAndCategoryAndLangTeachAndLangSpeakAndName(
                        userSessionInfo.getId(), categoryId, langTeachId, langSpeakId, name, pageRequest);
            } else {
                throw new InvalidRequestItemException("Request parameter invalid 'filter': " + filter);
            }


            // Generate response
            CoursePageResponse coursePageResponse = new CoursePageResponse();
            coursePageResponse.from(coursePage);

            return coursePageResponse;
        }
    }

    Course get0(Integer id) throws NoInstanceException {
        Course course = courseRepo.findOne(id);
        if (course == null) {
            throw new NoInstanceException("Course", id);
        } else {
            return course;
        }
    }

    List<Course> getList0(List<Integer> ids) {
        return courseRepo.findAll(ids);
    }

    @Transactional
    public void delete(Integer id) throws UnauthenticatedException, ServerTechnicalException, UnauthorizedException {
        User user = securityService.retrieveCurrentUser0();
        securityService.permitUser(user);
        courseRepo.delete(id);
    }

    public CoursePageResponse getTeachingCourse(String username, int page, int pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Course> coursePage = courseRepo.findByCreatorAndIsPublicTrue(username, pageRequest);
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(coursePage);
        return coursePageResponse;
    }

    public CoursePageResponse getLearningCourse(String username, int page, int pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Course> coursePage = courseRepo.getLearningCourses(username, pageRequest);
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(coursePage);
        return coursePageResponse;
    }

    @Transactional
    public List<LinkedHashMap<String, Object>> getLearningCourseRankingList(Integer courseId, int page, int pageSize)
            throws NoInstanceException {
        LearningCoursePageResponse learningCoursePageResponse = learningCourseService.createRankingList(courseId, page, pageSize);
        List<LinkedHashMap<String, Object>> ranking = new ArrayList<LinkedHashMap<String, Object>>();
        for (int i = 0; i < learningCoursePageResponse.getDataList().size(); i++) {
            LearningCourseResponse courseResponse = learningCoursePageResponse.getDataList().get(i);
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("username", courseResponse.getUserName());
            data.put("point", courseResponse.getPoint());
            User user = userService.get0(courseResponse.getUserName());
            data.put("avatar", user.getAvatar());
            String subfix = (i % 10 == 0) ? "st" : ((i - 1) % 10 == 0) ? "nd" : ((i - 2) % 10 == 0) ? "rd" : "th";
            data.put("rank", (i + 1) + subfix);
            ranking.add(data);
        }
        return ranking;
    }

    public Integer getNumOfItems(Integer courseId) {
        return courseRepo.getNumofCourseItems(courseId);
    }

    @Transactional
    public void touch(Course course) {
        course.setLastUpdate(Calendar.getInstance().getTime());
    }

    public void touch(Integer id) {
        Course course = courseRepo.findOne(id);
        course.setLastUpdate(null);
        course.setLastUpdate(Calendar.getInstance().getTime());
    }

    @Transactional
    public CoursePageResponse getPopularCourse(int page, int pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize,
                new Sort(Sort.Direction.ASC));
        Page<Course> coursePage = courseRepo.getPopularCourses(pageRequest);
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(coursePage);
        return coursePageResponse;
    }

    @Transactional
    public CoursePageResponse getPopularCourseByCategory(String category, int page, int pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Course> coursePage = courseRepo.getPopularCoursesByCategory(category, pageRequest);
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(coursePage);
        return coursePageResponse;
    }

    @Transactional
    public CoursePageResponse getLatestCourse(int page, int pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Course> coursePage = courseRepo.getLatestCourses(pageRequest);
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(coursePage);
        return coursePageResponse;
    }

    @Transactional
    public CoursePageResponse getLatestCourseByCategory(String category, int page, int pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Course> coursePage = courseRepo.getLatestCoursesByCategory(category, pageRequest);
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(coursePage);
        return coursePageResponse;
    }

    @Transactional
    public CourseResponse createAvatar(MultipartFile multipartFile, String fileName, String htmlFormat, Integer courseId)
            throws ServerTechnicalException, ClientTechnicalException, UnauthenticatedException,
            UnauthorizedException, NoInstanceException {

        // Get course by ID. If not found, throw NoInstanceException
        Course course = get0(courseId);

        //check if current user is creator of this course or not
        securityService.permitUser(course.getCreator());

        byte[] bytes;
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException e) {
            throw new ClientTechnicalException("Upload has been error");
        }
        if (bytes.length == 0) {
            throw new ClientTechnicalException("File is empty");
        }
        String avatarLocation = this.vllsProperties.getProperty("file.avatar.img.location");
        String imageFilePath = String.format("%s/%s-%s-%s",
                avatarLocation, course.getId(), System.currentTimeMillis(), fileName);
        this.persistFile(imageFilePath, bytes);
        String globalLocation = substring(imageFilePath, indexOf(imageFilePath, "assets"));

        // Save picture using cascade PERSIST of Course
        course.setAvatar(globalLocation);
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setData(course);
        return courseResponse;
    }

    /**
     * This method is created to list all similar course with viewing course
     *
     * @param courseId
     * @return
     * @throws NoInstanceException
     */
    public List<CourseResponse> getSimilarCourses(Integer courseId) throws NoInstanceException {
        // Get course Id
        Course course = get0(courseId);

        // Get list of similar course
        List<Course> courses = courseRepo.getSimilarCourse(course.getId());
        List<CourseResponse> responses = new ArrayList<CourseResponse>();
        for (Course course1 : courses) {
            CourseResponse courseResponse = new CourseResponse();
            courseResponse.setData(course1);
            responses.add(courseResponse);
        }
        // Generate response
        return responses;
    }

    // Get List of similar course return Page Response
    public CoursePageResponse findAllSimilarCourses(Integer courseId, int page, int pageSize)
            throws NoInstanceException {
        // Get course Id
        Course course = get0(courseId);

        // Select similar course
        Page<SimilarCourse> similarCoursePage = courseRepo.findAllSimilarCourse(course.getId(),
                new PageRequest(page, pageSize));

        // Extract similar course domain
        List<Course> courses = new ArrayList<Course>(similarCoursePage.getContent().size());
        for (SimilarCourse similarCourse : similarCoursePage) {
            if (!course.getId().equals(similarCourse.getCourseOneId())) {
                courses.add(similarCourse.getCourseOne());
            } else {
                courses.add(similarCourse.getCourseTwo());
            }
        }
        // Generate response
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.dataListFrom(courses);
        coursePageResponse.pageInfoFrom(similarCoursePage);
        return coursePageResponse;
    }


    // Get List of similar course order by rating rate
    public CoursePageResponse getSimilarCourseByRatingRate(int page, int pageSize) throws
            NoInstanceException, UnauthenticatedException, ServerTechnicalException {
        // Get current user
        UserSessionInfo user = securityService.retrieveCurrentUserSession();

        // Get list of similar course
        Page<Course> courses = courseRepo.getSimilarCourseByRatingRate(user.getId(), new PageRequest(page, pageSize));

        // Generate response
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(courses);
        return coursePageResponse;
    }

    @Transactional
    public CourseResponse saveCourseRating(Integer courseId, Integer rating) throws
            NoInstanceException, UnauthenticatedException, ServerTechnicalException {
        UserSessionInfo user = securityService.retrieveCurrentUserSession();
        Boolean isRated = false;
        LearningCourse learningCourse = learningCourseService.get0(courseId, user.getId());
        if (learningCourse.getRating() != null) {
            isRated = true;
        }
        Course course = this.get0(courseId);
        if (course.getRating() == null) {
            course.setRating(0);
        }
        Integer courseRating = 0;
        if (isRated) {
            Integer totalRatingValues = 0;
            List<LearningCourse> learningCourseList = learningCourseService.getRatedLearningCourse(courseId);
            for (LearningCourse learningCourse1 : learningCourseList) {
                if (learningCourse.getId().equals(learningCourse1.getId())) {
                    totalRatingValues += rating;
                } else {
                    totalRatingValues += learningCourse1.getRating();
                }
            }
            courseRating = Math.round(totalRatingValues / learningCourseList.size());
        } else {
            Integer numOfRatings = courseRepo.getNumOfRatingsOfCourse(course.getId());
            courseRating = Math.round((course.getRating() * numOfRatings + rating) / (numOfRatings + 1));
        }
        learningCourse.setRating(rating);
        course.setRating((courseRating > 5) ? 5 : courseRating);

        // Check number of rating of current user to calculate rating rate
        Integer numberOfRating = learningCourseRepo.getNumberRatingOfUser(user.getId());
        if (numberOfRating > 2) {
            learningCourseService.updateCourseRatingAsync(user.getId());
        }
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setData(course);
        return courseResponse;
    }


    @Transactional
    public Double calculateSimilar(Course course1, Course course2) {
        int x = 1;
        int y = 1;
        int z = 1;
        int w = 1;
        double result;

        // Comparison
        if (!course1.getLangTeach().isEqual(course2.getLangTeach())) {
            x = 0;
        }
        if (!course1.getLangSpeak().isEqual(course2.getLangSpeak())) {
            y = 0;
        }
        if (!course1.getCreator().isEqual(course2.getCreator())) {
            z = 0;
        }
        if (!course1.getCategory().isEqual(course2.getCategory())) {
            w = 0;
        }

        //Check num = 0
        Double checkZero = Math.sqrt(x * x + y * y + z * z + w * w) * 2;
        if (checkZero == 0) {
            result = 0.01d;
        } else {
            result = (x + y + z + w) / checkZero;
        }

        // Insert to DB
        similarCourseService.create(course1.getId(), course2.getId(), result);

        return result;
    }

    //Create a similar rate list
    @Transactional
    public void createSimilarRateList(Course course1) {
        List<Course> remainCourseList = courseRepo.getRemainCourseId(course1.getId());

        for (Course course : remainCourseList) {
            this.calculateSimilar(course1, course);
        }
    }

    public List<CourseResponse> getLearnedCourseUserByCourseId(Integer courseId) throws
            UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        Set<Integer> learnedCourseIds = learningCourseService.getLearnedUserByCourseId(courseId);
        List<CourseResponse> result = new ArrayList<CourseResponse>();
        for (Integer learnedCourseId : learnedCourseIds) {
            Course course = courseRepo.findOne(learnedCourseId);
            CourseResponse courseResponse = new CourseResponse();
            courseResponse.setData(course);
            result.add(courseResponse);
        }
        return result;
    }

    public CoursePageResponse getLearnedCourseUserByCourseIdPage(Integer courseId, int page, int pageSize)
            throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        Set<Integer> learnedCourseIds = learningCourseService.getLearnedUserByCourseId(courseId);
        List<Course> result = new ArrayList<Course>();
        for (Integer learnedCourseId : learnedCourseIds) {
            Course course = courseRepo.findOne(learnedCourseId);
            result.add(course);
        }
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(result);
        return coursePageResponse;
    }

    /**
     * Creator recommendation course for current user is creator
     *
     * @param userId
     * @param courseId
     * @param isSave   true: add or update recommended course, false: remove recommendation
     * @return
     */
    public Boolean saveCreatorRecommendation(Integer userId, Integer courseId, Boolean isSave) {
        try {
            UserSessionInfo creator = securityService.retrieveCurrentUserSessionQuietly();
            String recommendFile = vllsProperties.getProperty("file.recommendation.location");
            recommendFile += "recommendation-" + creator.getName() + ".txt";
            File file = new File(recommendFile);
            String newText = "";
            Boolean isExist = false;
            if (!file.exists()) {
                file.createNewFile();
            } else {
                String text = new String(Files.readAllBytes(Paths.get(recommendFile)), StandardCharsets.UTF_8);
                if (!"".equals(text.trim())) {
                    String[] recommendInfo = text.split(";");
                    for (int i = 0; i < recommendInfo.length; i++) {
                        String info = recommendInfo[i];
                        if (Integer.parseInt(info.split(":")[0]) == userId) {
                            if (isSave) {
                                newText += userId + ":" + courseId;
                            }
                            isExist = true;
                        } else {
                            newText += info;
                        }

                        if (i < (recommendInfo.length - 1)) {
                            newText += ";";
                        }
                    }
                }
                if (newText.endsWith(";")) {
                    newText = newText.substring(0, newText.length() - 1);
                }
            }
            if (!isExist) {
                if (!newText.trim().equals("")) {
                    newText += ";";
                }
                newText += userId + ":" + courseId;
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(newText);
            bw.flush();
            bw.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Creator recommendation for user is a learner
     *
     * @param courseId
     * @return
     */
    public Boolean saveCreatorRecommendation(Integer courseId) {
        try {
            UserSessionInfo user = securityService.retrieveCurrentUserSessionQuietly();
            Course course = courseRepo.getOne(courseId);
            String recommendFile = vllsProperties.getProperty("file.recommendation.location");
            recommendFile += "recommendation-" + course.getCreator().getName() + ".txt";
            File file = new File(recommendFile);
            if (file.exists()) {
                String newText = "";
                String text = new String(Files.readAllBytes(Paths.get(recommendFile)), StandardCharsets.UTF_8);
                if (!"".equals(text.trim())) {
                    String[] recommendInfo = text.split(";");
                    for (int i = 0; i < recommendInfo.length; i++) {
                        String info = recommendInfo[i];
                        if (Integer.parseInt(info.split(":")[0]) != user.getId()) {
                            newText += info;
                        }

                        if (i < (recommendInfo.length - 1)) {
                            newText += ";";
                        }
                    }
                    if (newText.endsWith(";")) {
                        newText = newText.substring(0, newText.length() - 1);
                    }
                }
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(newText);
                bw.flush();
                bw.close();
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Integer getCreatorRecommendationCourse(Integer userId) throws IOException {
        UserSessionInfo creator = securityService.retrieveCurrentUserSessionQuietly();
        String recommendFile = vllsProperties.getProperty("file.recommendation.location");
        recommendFile += "recommendation-" + creator.getName() + ".txt";
        File file = new File(recommendFile);
        if (file.exists()) {
            String text = new String(Files.readAllBytes(Paths.get(recommendFile)), StandardCharsets.UTF_8);
            if (!"".equals(text.trim())) {
                String[] recommendInfo = text.split(";");
                for (int i = 0; i < recommendInfo.length; i++) {
                    String info = recommendInfo[i];
                    if (Integer.parseInt(info.split(":")[0]) == userId) {
                        return Integer.parseInt(info.split(":")[1]);
                    }
                }
            }
        }
        return -1;
    }

    public CoursePageResponse getTeachingCourseForRecommendsation(String learner, int page, int pageSize) {
        String creator = securityService.retrieveCurrentUserSessionQuietly().getName();
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Course> coursePage = courseRepo.findByCreatorAndIsPublicTrueNolearningCourses(creator, learner, pageRequest);
        CoursePageResponse coursePageResponse = new CoursePageResponse();
        coursePageResponse.from(coursePage);
        return coursePageResponse;
    }
}
