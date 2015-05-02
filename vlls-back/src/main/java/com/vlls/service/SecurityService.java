package com.vlls.service;

import com.vlls.exception.*;
import com.vlls.jpa.domain.User;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

/**
 * Created by hiephn on 2014/09/25.
 */
@Service
public class SecurityService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserSessionInfo userSessionInfo;

    /**
     * This method takes authenticate header as input.
     * What it does:
     * - Clear current session
     * - Check if authenticate username:password) is valid
     * - if valid, store user information to session
     * - if invalid, throw UnauthenticatedException
     *
     * @param authenticate header sent by client, which is base64 encoded username:password
     * @return user information
     */
    @Transactional
    public UserResponse authenticate(String authenticate)
            throws UnauthenticatedException, InvalidRequestItemException,
            ServerTechnicalException, NoInstanceException {
        this.logout();
        if (!contains(authenticate, "Basic ")) {
            throw new InvalidRequestItemException("Invalid credential information");
        }
        authenticate = removeStart(authenticate, "Basic ");
        String decoded = new String(Base64.getDecoder().decode(authenticate));
        String[] items = decoded.split(":");
        if (items.length != 2) {
            throw new InvalidRequestItemException("Invalid credential information");
        }
        String username = items[0];
        String password = items[1];

        User user = userService.get0(username, password);
        if (user == null) {
            throw new UnauthenticatedException("Username or Password not correct");
        }
        user.setLastLogin(new Date());
        userSessionInfo.setSession(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setData(user);

        return userResponse;
    }

    /**
     * This method takes no parameter.
     * When triggered, current user information is wiped out from its session.
     * Cause the user logs out.
     */
    public void logout() {
        userSessionInfo.resetSession();
    }

    /**
     * Retrieve current user info from session.
     * If user does not yet log in, throw UnauthorizedException.
     *
     * @return current logged in user info
     */
    public UserResponse retrieveCurrentUser()
            throws NoInstanceException, ServerTechnicalException, UnauthenticatedException {
        User user = this.retrieveCurrentUser0();
        UserResponse userResponse = new UserResponse();
        userResponse.setData(user);
        return userResponse;
    }

    /**
     * Retrieve current logged in user session info.
     *
     * @return current logged n user session info
     * @throws NoInstanceException
     */
    public UserSessionInfo retrieveCurrentUserSession() throws UnauthenticatedException {
        if (isEmpty(this.userSessionInfo.getName())) {
            throw new UnauthenticatedException("Please login.");
        }
        return this.userSessionInfo;
    }

    /**
     * Retrieve current logged in user session info.
     * If user not logged in, no exception is thrown.
     *
     * @return current logged n user session info
     */
    public UserSessionInfo retrieveCurrentUserSessionQuietly() {
        return this.userSessionInfo;
    }

    /**
     * Retrieve current user as JPA entity.
     * This method is only visible to classes that are in the same package (com.vlls.service).
     * Controllers cannot retrieve this entity.
     * If user does not yet log in, throw Unauthenticated Exception.
     * <p>
     * NOTE: If simple user information is needed,
     * use {@link #retrieveCurrentUserSession()} instead to prevent too much query to DB
     *
     * @return current logged in user as entity
     */
    User retrieveCurrentUser0() throws UnauthenticatedException, ServerTechnicalException {
        if (isEmpty(userSessionInfo.getName())) {
            throw new UnauthenticatedException("Please login.");
        }
        try {
            User user = userService.get0(userSessionInfo.getId());
            return user;
        } catch (NoInstanceException e) {
            LOGGER.error("User exists in session but not in database: " + userSessionInfo.getId());
            throw new ServerTechnicalException("Cannot find current user information.", e);
        }
    }

    /**
     * Check if current logged in user is own the course or not.
     * If not, UnauthenticatedException is thrown.
     *
     * @param courseId courseId
     * @return course response. Note: DON'T PUBLICLY RETURN JPA ENTITY
     */
    public Object isUserOwn(int courseId) {
        return null;
    }

    /**
     * Check if current logged in user is own the course or not.
     * If not, UnauthenticatedException is thrown.
     *
     * @param courseId courseId
     * @return course entity
     */
    Object isUserOwn0(int courseId) {
        return null;
    }

    public boolean isGuess() {
        return userSessionInfo.getId() == null;
    }

    public boolean isUser() {
        return userSessionInfo.getId() != null;
    }

    public boolean isAdmin() {
        return userSessionInfo.getRole().equals("admin");
    }

    public void checkUser() throws UnauthenticatedException {
        if (!isUser()) {
            throw new UnauthenticatedException("Please login");
        }
    }

    /**
     * Check if current user is admin or not
     *
     * @throws UnauthorizedException
     * @throws ServerTechnicalException
     */
    public void checkAdmin() throws UnauthorizedException {
        if (!isAdmin()) {
            throw new UnauthorizedException("You don't have permission to do this!");
        }
    }

    public void isCreator(User user, Integer creatorId) throws UnauthorizedException {
        if (user.getId() != creatorId) {
            throw new UnauthorizedException("You are not creator of this course!");
        }
    }

    /**
     * Check if current logged in user is the same as user from parameter.
     * If not, UnauthorizedException is thrown.
     *
     * @param user user to be permitted to continue action
     * @throws UnauthenticatedException
     * @throws UnauthorizedException
     */
    public void permitUser(User user) throws UnauthenticatedException, UnauthorizedException {
        this.permitUser(user.getId());
    }

    /**
     * See {@link #permitUser(com.vlls.jpa.domain.User)}
     *
     * @param userIds
     * @throws UnauthenticatedException
     * @throws UnauthorizedException
     */
    public void permitUser(Integer... userIds) throws UnauthenticatedException, UnauthorizedException {
        boolean permit = false;
        for (Integer userId : userIds) {
            if (Objects.equals(userId, retrieveCurrentUserSession().getId())) {
                permit = true;
            }
        }
        if (!permit) {
            throw new UnauthorizedException("You do not have permission to do this action");
        }
    }
}
