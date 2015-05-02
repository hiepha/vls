package com.vlls.service;

import com.vlls.exception.*;
import com.vlls.exception.UnsupportedOperationException;
import com.vlls.jpa.domain.Gender;
import com.vlls.jpa.domain.Role;
import com.vlls.jpa.domain.User;
import com.vlls.jpa.repository.RoleRepository;
import com.vlls.jpa.repository.UserRepository;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.UserInfoPageResponse;
import com.vlls.web.model.UserInfoResponse;
import com.vlls.web.model.UserPageResponse;
import com.vlls.web.model.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by hiephn on 2014/07/12.
 */
@Service
public class UserService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String DEFAULT_AVATAR = "assets/img/avatar/unknown_user.png";

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private LearningItemService learningItemService;

    private SimpleDateFormat fbBirthDayDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private SimpleDateFormat birthDayFormat = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * Find user. There is 3 cases:
     * 1/ Find by name (unique). First/Last names are empty
     * => return page with only 1 user
     * <p>
     * 2/ Find all (RESTRICTED USED). All names are empty
     * => return all users
     * <p>
     * 3/ Filter by name, first/last name
     * => return page of users whose name, first/last name contains the corresponding name
     *
     * @param name      unique username
     * @param firstName first name
     * @param lastName  last name
     * @param page      page number (start from 0)
     * @param pageSize  page size
     * @return user page
     */
    public UserPageResponse get(String name, String firstName, String lastName, int page, int pageSize) {
        if (isEmpty(firstName) && isEmpty(lastName)) {
            if (isNotEmpty(name)) { // Case 1
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Find only one user by name: " + name);
                }
                User user = userRepo.findOneByNameIgnoreCase(name);
                UserPageResponse userPageResponse = new UserPageResponse();
                userPageResponse.from(user);
                if (securityService.isUser()) {
                    UserSessionInfo currentUser = securityService.retrieveCurrentUserSessionQuietly();
                    if (!user.getId().equals(currentUser.getId())) {
                        // Get friend status
                        FriendshipService.FriendStatus friendStatus = friendshipService.getRelationship(
                                currentUser.getId(), user.getId());
                        userPageResponse.getDataList().get(0).setFriendStatus(friendStatus.toString());
                    }
                }
                return userPageResponse;
            } else { // Case 2
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Find all user");
                }
                Page<User> userPage = userRepo.findAll(new PageRequest(page, pageSize));
                UserPageResponse userPageResponse = new UserPageResponse();
                userPageResponse.from(userPage);
                return userPageResponse;
            }
        } else { // Case 3
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Filter user by name: [%s], first name: [%s], last name: [%s]",
                        name, firstName, lastName));
            }
            Page<User> userPage = userRepo.findByNameLikeOrFirstNameLikeOrLastNameLike(
                    wildcardSearchKey(name),
                    wildcardSearchKey(firstName),
                    wildcardSearchKey(lastName),
                    new PageRequest(page, pageSize)
            );
            UserPageResponse userPageResponse = new UserPageResponse();
            userPageResponse.from(userPage);
            return userPageResponse;
        }
    }

    /**
     * Get User's info:
     * 1/ Get one by userName
     * <p>
     * 2/ Get one by userId
     *
     * @param name
     * @param page
     * @param pageSize
     * @return
     */
    public UserInfoPageResponse getUserInfo(String name, int page, int pageSize) throws NoInstanceException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Find only one user by name: " + name);
        }
        User user = userRepo.findOneByNameIgnoreCase(name);
        UserInfoPageResponse userInfoPageResponse = new UserInfoPageResponse();
        userInfoPageResponse.from(user);
        // Set number of Friend
        Integer userId = user.getId();
        userInfoPageResponse.getDataList().get(0).setNumberF(friendshipService.getNumberOfFriend(userId));
        //userInfoPageResponse.getDataList().get(0).setNumberLI(learningItemService.getNumOfLearnedItems(userId));
        return userInfoPageResponse;
    }

    public UserInfoPageResponse getUserInfo(int id) throws NoInstanceException {
        User user =this.get0(id);
        UserInfoPageResponse userInfoPageResponse = new UserInfoPageResponse();
        userInfoPageResponse.from(user);
        return userInfoPageResponse;
    }

    /**
     * Get user entity by name & password
     *
     * @param name     name
     * @param password password
     * @return user entity
     * @throws NoInstanceException
     */
    User get0(String name, String password) throws NoInstanceException, ServerTechnicalException {
        User user = userRepo.findByNameIgnoreCaseAndPassword(name, this.hashSha(password));
        if (user == null) {
            throw new NoInstanceException("Username or Password is incorrect");
        } else {
            return user;
        }
    }

    User get0(String name) throws NoInstanceException {
        User user = userRepo.findOneByNameIgnoreCase(name);
        if (user == null) {
            throw new NoInstanceException("User", name);
        } else {
            return user;
        }
    }

    User get0(Integer id) throws NoInstanceException {
        User user = userRepo.findOne(id);
        if (user == null) {
            throw new NoInstanceException("User", id);
        } else {
            return user;
        }
    }

    User getCreatorByQuestion0(Integer questionId) throws NoInstanceException {
        User user = userRepo.findOneByCreatedCoursesLevelsItemsQuestionsId(questionId);
        if (user == null) {
            throw new NoInstanceException("Cannot find creator of question: " + questionId);
        } else {
            return user;
        }
    }

    User getCreatorByItem0(Integer itemId) throws NoInstanceException {
        User user = userRepo.findOneByCreatedCoursesLevelsItemsId(itemId);
        if (user == null) {
            throw new NoInstanceException("Cannot find creator of item: " + itemId);
        } else {
            return user;
        }
    }

    User getCreatorByLevel0(Integer levelId) throws NoInstanceException {
        User user = userRepo.findOneByCreatedCoursesLevelsId(levelId);
        if (user == null) {
            throw new NoInstanceException("Cannot find creator of level: " + levelId);
        } else {
            return user;
        }
    }

    User getCreatorByCourse0(Integer courseId) throws NoInstanceException {
        User user = userRepo.findOneByCreatedCourses(courseId);
        if (user == null) {
            throw new NoInstanceException("Cannot find creator of course: " + courseId);
        } else {
            return user;
        }
    }

    /**
     * Create user
     *
     * @param name      name1
     * @param password  password
     * @param firstName firstName
     * @param lastName  lastName
     * @param point     point
     * @param birthday  birthday
     * @param bio       bio
     * @param avatar    avatar
     * @param phone     phone
     * @param email     email
     * @param gender    gender
     * @param role      role
     * @return created user
     * @throws DuplicatedItemException
     * @throws ServerTechnicalException
     */
    @Transactional
    public UserResponse create(String name, String password, String firstName,
                               String lastName, Integer point, String birthday,
                               String bio, String avatar, String phone,
                               String email, String gender, String role)
            throws DuplicatedItemException, ServerTechnicalException, InvalidEmailException {
        // Check duplicate user name
        User user = userRepo.findOneByNameIgnoreCase(name);
        if (user != null) {
            throw new DuplicatedItemException("User name already exist");
        } else {
            // Check duplicate email
            user = userRepo.findOneByEmailIgnoreCase(email);
            if (user != null) {
                throw new DuplicatedItemException("Email already exist");
            } else {
                user = new User();

                // Setting role
                if (isEmpty(role)) {
                    Role userRole = roleRepo.findMemRole();
                    user.setRole(userRole);
                } else {
                    Role roleEntity = roleRepo.findOneByNameIgnoreCase(role);
                    if (roleEntity == null) {
                        LOGGER.warn(String.format("Role not found [%s]. Save as user.", role));
                        roleEntity = roleRepo.findMemRole();
                    }
                    user.setRole(roleEntity);
                }

                // Setting birthday
                if (isNotEmpty(birthday)) {
                    try {
                        Date date = new Date(birthDayFormat.parse(birthday).getTime());
                        user.setBirthday(date);
                    } catch (ParseException e) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn(String.format("Invalid birthday [%s]. Save as null.", birthday), e);
                        }
                    }
                }

                // Setting email
                this.validateEmailFormat(email);
                user.setEmail(email);

                // Setting other information
                user.setName(name);
                user.setPassword(this.hashSha(password));
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPoint(point);
                user.setBio(bio);
                if (isEmpty(avatar)) {
                    user.setAvatar(DEFAULT_AVATAR);
                } else {
                    user.setAvatar(avatar);
                }
                user.setPhone(phone);
                if (isEmpty(gender)) {
                    user.setGender(Gender.UNDEFINED);
                } else {
                    try {
                        user.setGender(Gender.valueOf(gender));
                    } catch (IllegalArgumentException e) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn(String.format("Cannot find gender [%s]. Save as UNDEFINED.", gender), e);
                        }
                        user.setGender(Gender.UNDEFINED);
                    }
                }
                userRepo.save(user);

                // Generating response
                UserResponse userResponse = new UserResponse();
                userResponse.setData(user);
                return userResponse;
            }
        }
    }

    /**
     * Update user
     *
     * @param name      name1
     * @param firstName firstName
     * @param lastName  lastName
     * @param birthday  birthday
     * @param bio       bio
     * @param phone     phone
     * @param email     email
     * @param gender    gender
     * @return updated user
     * @throws NoInstanceException
     * @throws ServerTechnicalException
     */
    @Transactional
    public UserResponse update(String name, String firstName,
                               String lastName, String birthday, Integer point,
                               String bio, String phone,
                               String email, String gender)
            throws ServerTechnicalException, NoInstanceException, InvalidEmailException, UnauthenticatedException {
        User user = userRepo.findOneByNameIgnoreCase(name);

        if (user == null) {
            throw new NoInstanceException("User", name);
        } else {
            user = securityService.retrieveCurrentUser0();

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setBio(bio);
            user.setPoint(point);
            user.setPhone(phone);
            this.validateEmailFormat(email);
            user.setEmail(email);
            user.setGender(Gender.valueOf(gender));
        }

        // Generate response
        UserResponse userResponse = new UserResponse();
        userResponse.setData(user);
        return userResponse;
    }

    /**
     * Because of security concern. Current password needs provided.
     *
     * @param name        name
     * @param password    current password
     * @param newPassword new password
     * @return updated user
     * @throws NoInstanceException
     * @throws ServerTechnicalException
     */
    @Transactional
    public UserResponse updatePassword(String name, String password, String newPassword)
            throws NoInstanceException, ServerTechnicalException {
        User user = this.get0(name, password);
        user.setPassword(newPassword);

        // Generate response
        UserResponse userResponse = new UserResponse();
        userResponse.setData(user);
        return userResponse;
    }

    @Transactional
    public UserResponse updateAvatar(String name, String avatar) throws NoInstanceException {
        User user = this.get0(name);
        user.setAvatar(avatar);

        // Generate response
        UserResponse userResponse = new UserResponse();
        userResponse.setData(user);
        return userResponse;
    }

    @Transactional
    public UserResponse updateRole(String name, Integer roleId) throws NoInstanceException,
            UnauthorizedException, ServerTechnicalException, UnauthenticatedException {
        securityService.checkAdmin();
        User user = this.get0(name);
        Role roleEntity = roleRepo.findOne(roleId);
        if (roleEntity == null) {
            throw new NoInstanceException("Role", roleId);
        } else {
            user.setRole(roleEntity);
        }

        // Generate response
        UserResponse userResponse = new UserResponse();
        userResponse.setData(user);
        return userResponse;
    }

    @Transactional
    public void delete(String name) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot delete user");
    }

    @Transactional
    public void deactivate(String name) throws UnauthorizedException, ServerTechnicalException, UnauthenticatedException {
        securityService.checkAdmin();
        // TODO: add column to user table
    }

    @Transactional
    public UserResponse createAvatar(MultipartFile multipartFile, String fileName, String htmlFormat) throws UnauthenticatedException, ServerTechnicalException, ClientTechnicalException {

        //Retrieve current logged in user
        User user = securityService.retrieveCurrentUser0();
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
                avatarLocation, user.getId(), System.currentTimeMillis(), fileName);
        this.persistFile(imageFilePath, bytes);
        String globalLocation = substring(imageFilePath, indexOf(imageFilePath, "assets"));

        // Save picture using cascade PERSIST of User
        user.setAvatar(globalLocation);
        UserResponse userResponse = new UserResponse();
        userResponse.setData(user);
        return userResponse;
    }

    void calculatePoint(Integer userId, Integer point) {
        User user = userRepo.findOne(userId);
        user.setPoint(
                user.getPoint() == null ? 0 : user.getPoint()
                        + point);
    }

    public UserPageResponse getRanking(int page, int pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<User> userPage = userRepo.rankingUser(pageRequest);
        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.from(userPage);
        return userPageResponse;
    }

    /**
     * This method is created to get friend ranking
     *
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
     */
    public List<UserResponse> getFriendRanking() throws UnauthenticatedException, ServerTechnicalException {
        // Get current user
        User user = securityService.retrieveCurrentUser0();

        // Get friend ranking
        List<Object[]> users = userRepo.friendRanking(user.getId());
        List<UserResponse> responses = new ArrayList<>();
        for (Object[] properties : users) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId((Integer) properties[0]);
            userResponse.setName((String) properties[1]);
            userResponse.setAvatar((String) properties[2]);
            userResponse.setPoint((Integer) (properties[3]));
            responses.add(userResponse);
        }
        // Generate response
        return responses;
    }

    public LinkedHashMap<String, String> getUserProfileStat(String username) throws NoInstanceException {
        Long learnts = userRepo.getUserTotalLearntItems(username, 6);
        Long friends = userRepo.getUserTotalFriends(username);
        LinkedHashMap<String, String> stat = new LinkedHashMap<String, String>();
        stat.put("totalItems", String.valueOf(learnts));
        stat.put("totalFriends", String.valueOf(friends));
        return stat;
    }

    public List<Object[]> getLastLogin() throws NoInstanceException {
        return userRepo.getUsersNeedCall();
    }

    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }

    public User getLearningCourseCreator(Integer learningCourseId) {
        return userRepo.getLearningCourseCreator(learningCourseId);
    }

    public List<User> getLearningCoursesCreatorByLearnerId(Integer userId) {
        return userRepo.getLearningCoursesCreatorByLearnerId(userId);
    }
}
