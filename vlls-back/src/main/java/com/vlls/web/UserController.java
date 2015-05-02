package com.vlls.web;

import com.vlls.exception.*;
import com.vlls.service.UserService;
import com.vlls.web.model.UserInfoPageResponse;
import com.vlls.web.model.UserPageResponse;
import com.vlls.web.model.UserResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by hiephn on 2014/09/24.
 */
@Controller
@RequestMapping("/user")
public class UserController implements ControllerConstant {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponse create(
            // MANDATORY
            @RequestParam("name") String name,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            // OPTIONAL
            @RequestParam(value = "firstName", required = false, defaultValue = EMPTY) String firstName,
            @RequestParam(value = "lastName", required = false, defaultValue = EMPTY) String lastName,
            @RequestParam(value = "birthday", required = false, defaultValue = EMPTY) String birthday,
            @RequestParam(value = "bio", required = false, defaultValue = EMPTY) String bio,
            @RequestParam(value = "avatar", required = false, defaultValue = EMPTY) String avatar,
            @RequestParam(value = "phone", required = false, defaultValue = EMPTY) String phone,
            @RequestParam(value = "gender", required = false, defaultValue = EMPTY) String gender,
            @RequestParam(value = "role", required = false, defaultValue = EMPTY) String role)
            throws InvalidRequestItemException, DuplicatedItemException, ServerTechnicalException {

        UserResponse userResponse = userService.create(
                name, password, firstName, lastName, null, birthday, bio, avatar, phone,
                email, gender, role);

        return userResponse;
    }

    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponse update(
            // MANDATORY
            @RequestParam(value = "name", required = true) String name,
            // OPTIONAL
            @RequestParam(value = "firstName", required = true) String firstName,
            @RequestParam(value = "lastName", required = true) String lastName,
            @RequestParam(value = "birthday", required = true) String birthday,
            @RequestParam(value = "point", required = false) Integer point,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "gender", required = true) String gender

    ) throws ServerTechnicalException, NoInstanceException, InvalidEmailException, UnauthenticatedException {
        UserResponse userResponse = userService.update(name, firstName, lastName, birthday, point, bio, phone, email, gender);
        return userResponse;
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserPageResponse get(
            @RequestParam(value = "name", defaultValue = EMPTY) String name,
            @RequestParam(value = "firstName", defaultValue = EMPTY) String firstName,
            @RequestParam(value = "lastName", defaultValue = EMPTY) String lastName,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        UserPageResponse userPageResponse = userService.get(name, firstName, lastName, page, pageSize);
        return userPageResponse;
    }

    @RequestMapping(value = "/info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserInfoPageResponse getUserInfo(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws NoInstanceException, MissingServletRequestParameterException {
        if (id != null) {
            return userService.getUserInfo(id);
        } else if (StringUtils.isNotEmpty(name)) {
            return userService.getUserInfo(name, page, pageSize);
        } else {
            throw new MissingServletRequestParameterException("id", "Integer");
        }
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String del(
            @RequestParam("name") String name) {
        return null;
    }

    /*
     * Change avatar API
     *
     * @param avatar
     * @param name
     * @return
     * @throws NoInstanceException
     *
    @RequestMapping(value = "/avatar",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponse updateAvatar(
            @RequestParam(value = "avatar", defaultValue = EMPTY) String avatar,
            @RequestParam(value = "name", defaultValue = EMPTY) String name
    ) throws NoInstanceException {
        UserResponse userResponse = userService.updateAvatar(name, avatar);
        return userResponse;
    } */

    /**
     * This API is created to upload User's avatar
     *
     * @param multipartFile
     * @param fileName
     * @param htmlFormat
     * @return
     * @throws ServerTechnicalException
     * @throws ClientTechnicalException
     * @throws UnauthenticatedException
     */
    @RequestMapping(value = "/image",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserResponse createAvatar(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("name") String fileName,
            @RequestParam(value = "htmlFormat", required = false, defaultValue = EMPTY) String htmlFormat) throws ServerTechnicalException, ClientTechnicalException, UnauthenticatedException {
        UserResponse userResponse = userService.createAvatar(multipartFile, fileName, htmlFormat);
        return userResponse;
    }

    @RequestMapping(value = "ranking", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserPageResponse getRankingList(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        UserPageResponse userPageResponse = userService.getRanking(page, pageSize);
        return userPageResponse;
    }

    @RequestMapping(value = "/friendRanking", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserResponse> getFriendRanking(
            //@RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
            //@RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
    ) throws UnauthenticatedException, ServerTechnicalException {
        List<UserResponse> response = userService.getFriendRanking();
        return response;
    }

    @RequestMapping(value = "/profile/stat", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LinkedHashMap<String, String> getProfileStat(String username) throws NoInstanceException {
        return userService.getUserProfileStat(username);
    }
}
