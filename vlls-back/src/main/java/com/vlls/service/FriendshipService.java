package com.vlls.service;

import com.vlls.exception.DuplicatedItemException;
import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.jpa.domain.Friendship;
import com.vlls.jpa.domain.User;
import com.vlls.jpa.repository.FriendshipRepository;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.FriendshipPageResponse;
import com.vlls.web.model.FriendshipResponse;
import com.vlls.web.model.UserInfoPageResponse;
import com.vlls.web.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Hoang Phi on 10/18/2014.
 */
@Service
public class FriendshipService extends AbstractService {
    @Autowired
    private FriendshipRepository friendshipRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    enum FriendStatus {
        FRIEND, NOT_FRIEND, REQUEST_SENT, CONFIRM_WAITING
    }

    @Transactional
    public FriendshipResponse create(Integer friendTwoId) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException, DuplicatedItemException {
        User friendOne = securityService.retrieveCurrentUser0();
        User friendTwo = userService.get0(friendTwoId);

        Friendship friendshipCheck = friendshipRepo.findFriendship(friendOne.getId(), friendTwo.getId());
        if (friendshipCheck != null) {
            throw new DuplicatedItemException("Request sent already!");
        } else {
            Friendship friendship = new Friendship();
            friendship.setFriendOneId(friendOne.getId());
            friendship.setFriendOne(friendOne);
            friendship.setFriendTwoId(friendTwo.getId());
            friendship.setFriendTwo(friendTwo);
            friendship.setIsFriend(false);
            friendship.setLastUpdate(new Date());
            friendshipRepo.save(friendship);

            //Generate response
            FriendshipResponse friendshipResponse = new FriendshipResponse();
            friendshipResponse.setData(friendship);
            return friendshipResponse;
        }
    }

    /**
     * This method is created to get pending friends
     *
     * @param page
     * @param pageSize
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
     */
    public FriendshipPageResponse getPendingFriend(int page, int pageSize) throws UnauthenticatedException, ServerTechnicalException {
        // Get current user
        User friendTwo = securityService.retrieveCurrentUser0();

        // Select pendingFriend list
        Page<Friendship> friendships = friendshipRepo.findByFriendTwoIdAndIsFriendFalse(friendTwo.getId(), new PageRequest(page, pageSize));

        // Generate response
        FriendshipPageResponse friendshipPageResponse = new FriendshipPageResponse();
        friendshipPageResponse.from(friendships);
        return friendshipPageResponse;
    }

    /**
     * This method is created to get Friend list
     *
     * @param page
     * @param pageSize
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
     */
    public FriendshipPageResponse getFriendList(int page, int pageSize) throws UnauthenticatedException, ServerTechnicalException {
        // Get current user
        User user = securityService.retrieveCurrentUser0();

        // Select acceptFriend list
        Page<Friendship> friendshipPage = friendshipRepo.findAllFriendByUserId(user.getId(), new PageRequest(page, pageSize));

        // Generate response
        FriendshipPageResponse friendshipPageResponse = new FriendshipPageResponse();
        friendshipPageResponse.from(friendshipPage);
        return friendshipPageResponse;
    }

    /**
     * This method is created to display friend's info in Friend tab
     *
     * @param page
     * @param pageSize
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
     * @throws NoInstanceException
     */
    public UserInfoPageResponse getFriendInfo(String userName, int page, int pageSize) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        // Get user
        User user = userService.get0(userName);

        // Select acceptFriend list
        Page<Friendship> friendshipPage = friendshipRepo.findAllFriendByUserId(user.getId(), new PageRequest(page, pageSize));

        // Extract friend (domain)
        List<User> friends = new ArrayList<>(friendshipPage.getContent().size());
        for (Friendship friendship : friendshipPage) {
            if (!user.getId().equals(friendship.getFriendOneId())) {
                friends.add(friendship.getFriendOne());
            } else {
                friends.add(friendship.getFriendTwo());
            }
        }

        // Generate Response
        UserInfoPageResponse userInfoPageResponse = new UserInfoPageResponse();
        userInfoPageResponse.dataListFrom(friends);
        userInfoPageResponse.pageInfoFrom(friendshipPage);

        return userInfoPageResponse;
    }

    public Integer getNumberOfFriend(Integer userId) throws NoInstanceException {
        Integer numberF = friendshipRepo.getNumberOfFriend(userService.get0(userId).getId());
        return numberF;
    }


    /**
     * Accept friend request
     *
     * @param friendOneId
     * @param isFriend
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
     * @throws NoInstanceException
     */
    @Transactional
    public FriendshipResponse acceptFriendRequest(Integer friendOneId, Boolean isFriend) throws
            UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        //Get current user
        User friendTwo = securityService.retrieveCurrentUser0();

        // Get friend
        User friendOne = userService.get0(friendOneId);

        // get pending friendship
        Friendship friendship = friendshipRepo.findOneByFriendOneIdAndFriendTwoIdAndIsFriendFalse(
                friendOne.getId(),friendTwo.getId());
        if (friendship == null) {
            throw new NoInstanceException("Is not friend");
        } else {
            // update isFriend to true
            friendship.setFriendOneId(friendOne.getId());
            friendship.setFriendOne(friendOne);
            friendship.setFriendTwoId(friendTwo.getId());
            friendship.setFriendTwo(friendTwo);
            friendship.setIsFriend(isFriend);
            friendship.setLastUpdate(new Date());

            // Generate response
            FriendshipResponse friendshipResponse = new FriendshipResponse();
            friendshipResponse.setData(friendship);
            return friendshipResponse;
        }
    }

    @Transactional
    public void delete(Integer friendId) throws UnauthenticatedException {
        Integer userId = securityService.retrieveCurrentUserSession().getId();
        Friendship friendship = friendshipRepo.findFriendship(userId, friendId);
        if (friendship != null) {
            friendshipRepo.delete(friendship);
        }
    }

    void touch(Friendship friendship) {
        friendship.setLastUpdate(new Date());
    }

    FriendStatus getRelationship(Integer currentUserId, Integer otherId) {
        Friendship friendship = friendshipRepo.findFriendship(currentUserId, otherId);
        if (friendship == null) {
            return FriendStatus.NOT_FRIEND;
        } else if (friendship.getIsFriend()) {
            return FriendStatus.FRIEND;
        } else if (friendship.getFriendOneId().equals(currentUserId)) {
            return FriendStatus.REQUEST_SENT;
        } else {
            return FriendStatus.CONFIRM_WAITING;
        }
    }

    long countIfLastUpdateAfter(Date date) throws UnauthenticatedException {
        Long counted = friendshipRepo.countByFriendTwoIdAndLastUpdateAfter(
                securityService.retrieveCurrentUserSession().getId(), date);
        return counted == null ? 0 : counted;
    }

    long countPendingFriendAfter(Date date) throws UnauthenticatedException {
        Long counted = friendshipRepo.countByFriendTwoIdAndIsFriendFalseAndLastUpdateAfter(
                securityService.retrieveCurrentUserSession().getId(), date);
        return counted == null ? 0 : counted;
    }

    List<Friendship> getAllPendingFriend() throws UnauthenticatedException {
        return friendshipRepo.findAllPendingFriend(securityService.retrieveCurrentUserSession().getId());
    }

    public Map<Integer, String> searchByName(String name, int size) throws UnauthenticatedException {
        UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();
        Integer currentUserId = userSessionInfo.getId();
        String wildcardName = this.wildcardSearchKey(name);
        List<Friendship> friendships = friendshipRepo.findByName(currentUserId, wildcardName, currentUserId, wildcardName, size);
        Map<Integer, String> friendshipMap = new HashMap<>(friendships.size());
        friendships.forEach(friendship -> {
            if (Objects.equals(friendship.getFriendOneId(), currentUserId)) {
                friendshipMap.put(friendship.getFriendTwoId(), friendship.getFriendTwo().getName());
            } else {
                friendshipMap.put(friendship.getFriendOneId(), friendship.getFriendOne().getName());
            }
        });
        return friendshipMap;
    }

    public UserResponse getFriendById(Integer friendId)
            throws UnauthenticatedException, NoInstanceException {
        UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();
        Friendship friendship = friendshipRepo.findFriendship(userSessionInfo.getId(), friendId);
        if (friendship == null || !friendship.getIsFriend()) {
            throw new NoInstanceException("Friend", friendId);
        } else {
            UserResponse userResponse = new UserResponse();
            if (Objects.equals(friendship.getFriendOneId(), userSessionInfo.getId())) {
                userResponse.setData(friendship.getFriendTwo());
            } else {
                userResponse.setData(friendship.getFriendOne());
            }
            return userResponse;
        }
    }
}
