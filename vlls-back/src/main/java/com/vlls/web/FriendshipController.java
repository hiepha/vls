package com.vlls.web;

import com.vlls.exception.DuplicatedItemException;
import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.service.FriendshipService;
import com.vlls.web.model.DataResponse;
import com.vlls.web.model.FriendshipPageResponse;
import com.vlls.web.model.FriendshipResponse;
import com.vlls.web.model.UserInfoPageResponse;
import com.vlls.web.model.UserInfoResponse;
import com.vlls.web.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Hoang Phi on 10/18/2014.
 */
@Controller
@RequestMapping("/user/friend")
public class FriendshipController implements ControllerConstant {
    @Autowired
    private FriendshipService friendshipService;

    /**
     * This API is created to send FriendRequest to another user
     *
     * @param friendTwoId
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
     * @throws NoInstanceException
     * @throws DuplicatedItemException
     */
    @RequestMapping(method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FriendshipResponse create(
            @RequestParam(value = "friendTwoId") Integer friendTwoId
    ) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException, DuplicatedItemException {
        FriendshipResponse friendshipResponse = friendshipService.create(friendTwoId);
        return friendshipResponse;
    }

    /**
     * This API is created to get FriendRequest list
     *
     * @param page
     * @param pageSize
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
     */
    @RequestMapping(value = "/pending",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FriendshipPageResponse getPendingFriend(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws UnauthenticatedException, ServerTechnicalException {
        FriendshipPageResponse friendshipPageResponse = friendshipService.getPendingFriend(page, pageSize);
        return friendshipPageResponse;
    }

    /**
     * This API is created to get Friend list
     *
     * @param page
     * @param pageSize
     * @return
     * @throws UnauthenticatedException
     * @throws ServerTechnicalException
     */
    @RequestMapping(value = "/accepted",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FriendshipPageResponse getFriendList(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws UnauthenticatedException, ServerTechnicalException {
        FriendshipPageResponse friendshipPageResponse = friendshipService.getFriendList(page, pageSize);
        return friendshipPageResponse;
    }

    /**
     * This API is created to accept FriendRequest
     *
     * @param friendOneId
     * @param isFriend
     * @return
     * @throws ServerTechnicalException
     * @throws NoInstanceException
     * @throws UnauthenticatedException
     */
    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FriendshipResponse acceptFriendRequest(
            @RequestParam(value = "friendOneId", required = true) Integer friendOneId,
            @RequestParam(value = "isFriend", required = true) Boolean isFriend
    ) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        FriendshipResponse friendshipResponse = friendshipService.acceptFriendRequest(friendOneId, isFriend);

        return friendshipResponse;
    }

    @RequestMapping(value = "/friendlist",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Integer getNumberOfFriend(
            @RequestParam(value = "userId") Integer userId
    ) throws NoInstanceException {
        Integer numberOfFriend = friendshipService.getNumberOfFriend(userId);
        return numberOfFriend;
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String delete(
            @RequestParam(value = "friendId") Integer friendId) throws UnauthenticatedException {
        friendshipService.delete(friendId);
        return OK_STATUS;
    }

    @RequestMapping(value = "/friend-list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserInfoPageResponse getFriendInfo(
            //@RequestParam(value = "friendOneId", required = false) Integer friendOneId,
            //@RequestParam(value = "friendTwoId", required = false) Integer friendTwoId,
            @RequestParam(value = "userName", required = true) String userName,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws ServerTechnicalException, NoInstanceException, UnauthenticatedException {
        UserInfoPageResponse userInfoPageResponse = friendshipService.getFriendInfo(userName, page, pageSize);
        return userInfoPageResponse;
    }

    @RequestMapping(value = "/name", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<Integer, String> searchFriend(
            @RequestParam("name") String name,
            @RequestParam(value = "size", defaultValue = "10") int size)
            throws UnauthenticatedException {
        return friendshipService.searchByName(name, size);
    }

    @RequestMapping(value = "/{friendId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponse getFriend(@PathVariable("friendId") Integer friendId)
            throws UnauthenticatedException, NoInstanceException {
        return friendshipService.getFriendById(friendId);
    }
}
