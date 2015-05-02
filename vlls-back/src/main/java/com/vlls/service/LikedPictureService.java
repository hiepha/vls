package com.vlls.service;

import com.vlls.exception.*;
import com.vlls.jpa.domain.LikedPicture;
import com.vlls.jpa.domain.Picture;
import com.vlls.jpa.domain.User;
import com.vlls.jpa.repository.LikedPictureRepository;
import com.vlls.jpa.repository.PictureRepository;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.LikedPicturePageResponse;
import com.vlls.web.model.LikedPictureResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

/**
 * Created by HoangPhi on 10/13/2014.
 */
@Service
public class LikedPictureService extends AbstractService {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private LikedPictureRepository likedPictureRepo;
    @Autowired
    private PictureRepository pictureRepo;
    @Autowired
    private PictureService pictureService;

    public LikedPicturePageResponse get(Integer pictureId, int page, int pageSize) throws UnauthenticatedException {
        UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSession();
        Page<LikedPicture> likedPicturePage = null;
        LikedPicturePageResponse likedPicturePageResponse = new LikedPicturePageResponse();
        if (pictureId > 0) {
            LikedPicture likedPicture = likedPictureRepo.findOneByPictureIdAndUserId(pictureId, userSessionInfo.getId());
            likedPicturePageResponse.from(likedPicture);
        } else if (pictureId < 0) {
            likedPicturePage = likedPictureRepo.findByUserId(userSessionInfo.getId(), new PageRequest(page, pageSize));
            likedPicturePageResponse.from(likedPicturePage);
        }
        likedPicturePageResponse.from(likedPicturePage);
        return likedPicturePageResponse;
    }

    @Transactional
    public LikedPictureResponse create(Integer pictureId) throws UnauthenticatedException, ServerTechnicalException, NoInstanceException, DuplicatedItemException {
        User user = securityService.retrieveCurrentUser0();
        Picture picture = pictureService.getPicture(pictureId);

        LikedPicture likedPictureCheck = likedPictureRepo.findOneByPictureIdAndUserId(picture.getId(), user.getId());
        if (likedPictureCheck != null) {
            throw new DuplicatedItemException("User already liked");
        } else {
            // create new
            LikedPicture likedPicture = new LikedPicture();
            likedPicture.setUserId(user.getId());
            likedPicture.setUser(user);
            likedPicture.setPictureId(picture.getId());
            likedPicture.setPicture(picture);
            likedPictureRepo.save(likedPicture);

            //Generate response
            LikedPictureResponse likedPictureResponse = new LikedPictureResponse();
            likedPictureResponse.setData(likedPicture);
            return likedPictureResponse;
        }
    }

    public Object update() {

        return null;
    }

    public void delete(Integer id) throws UnauthenticatedException, ServerTechnicalException, UnauthorizedException {
        Picture picture = pictureRepo.findOne(id);
        User user = securityService.retrieveCurrentUser0();
        securityService.permitUser(user.getId());
        likedPictureRepo.delete(id);
    }

    LikedPicture get0(Integer id) throws NoInstanceException {
        LikedPicture likedPicture = likedPictureRepo.findOne(id);
        if (likedPicture == null) {
            throw new NoInstanceException("Liked Picture", id);
        } else {
            return likedPicture;
        }
    }

    LikedPicture get0(Integer pictureId, Integer userId) {
        LikedPicture likedPicture = likedPictureRepo.findOneByPictureIdAndUserId(pictureId, userId);
        return likedPicture;
    }

    public LinkedHashMap<String, Object> checkStatus(Integer pictureId) throws ServerTechnicalException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("numOfLikes", likedPictureRepo.getLikeCount(pictureId));
        if (securityService.retrieveCurrentUserSessionQuietly() != null) {
            Integer userId = securityService.retrieveCurrentUserSessionQuietly().getId();
            map.put("isLiked", (likedPictureRepo.findOneByPictureIdAndUserId(pictureId, userId) != null));
        } else {
            map.put("isLiked", false);
        }
        return map;
    }
}
