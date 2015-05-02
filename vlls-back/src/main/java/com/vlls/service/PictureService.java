package com.vlls.service;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.jpa.domain.*;
import com.vlls.jpa.repository.ItemRepository;
import com.vlls.jpa.repository.LearningItemRepository;
import com.vlls.jpa.repository.LikedPictureRepository;
import com.vlls.jpa.repository.PictureRepository;
import com.vlls.web.model.NotificationResponse;
import com.vlls.web.model.PicturePageResponse;
import com.vlls.web.model.PictureResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Dell on 10/13/2014.
 */
@Service
public class PictureService extends AbstractService {

    @Autowired
    private PictureRepository pictureRepo;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private LikedPictureRepository likedPictureRepository;
    @Autowired
    private LikedPictureService likedPictureService;
    @Autowired
    private LearningItemRepository learningItemRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private NotificationService notificationService;

    public PicturePageResponse getPicturesCreatedByUser(String username, int page, int pageSize) throws ServerTechnicalException {
//        Integer id = userRepository.findOneByNameIgnoreCase(username).getId();
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Picture> picturePage = pictureRepo.findByUploader(username, pageRequest);
        PicturePageResponse picturePageResponse = new PicturePageResponse();
        picturePageResponse.from(picturePage);
        this.checkStatus(picturePageResponse);
        return picturePageResponse;
    }

    public PicturePageResponse getPicturesLikedByUser(String username, int page, int pageSize) throws ServerTechnicalException {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Picture> picturePage = pictureRepo.findPicturesLikedByUser(username, pageRequest);
        PicturePageResponse picturePageResponse = new PicturePageResponse();
        picturePageResponse.from(picturePage);
        this.checkStatus(picturePageResponse);
        return picturePageResponse;
    }

    public void deletePicture(Integer pictureId) {
        Picture picture = pictureRepo.findOne(pictureId);
        List<LikedPicture> likedPictures = likedPictureRepository.findByPictureId(picture);
        for (LikedPicture likedPicture : likedPictures) {
            likedPictureRepository.delete(likedPicture);
        }
        List<LearningItem> learningItems = learningItemRepository.findLearningItemByPictureId(pictureId);
        for (LearningItem learningItem : learningItems) {
            learningItem.setMemPicture(null);
            NotificationResponse note = new NotificationResponse();
            note.setType("NOTICE");
            note.setHeader("");
            note.setImage("");
            note.setAuthor("");
            note.setMessage("The picture for word " + learningItem.getItem().getName() + " had been deleted.");
            notificationService.log(learningItem.getLearningLevel().getLearningCourse().getUser().getName(), note);
        }
        pictureRepo.delete(pictureId);
    }

    public PicturePageResponse getPopularPicturesOfUser(String username, int page, int pageSize) throws ServerTechnicalException {
        PageRequest pageRequest = new PageRequest(page, pageSize);
//        Page<Picture> picturePage = pictureRepo.findByUploader(username, pageRequest);
//        PicturePageResponse picturePageResponse = new PicturePageResponse();
//        picturePageResponse.from(picturePage);
//        for (int i = 0; i < picturePageResponse.getDataList().size(); i++) {
//            PictureResponse pic = picturePageResponse.getDataList().get(i);
//            LinkedHashMap<String, Object> linkedHashMap = likedPictureService.checkStatus(pic.getId());
//            if ((Integer) linkedHashMap.get("numOfLikes") > 0) {
//                pic.setNumOfLikes((Integer) linkedHashMap.get("numOfLikes"));
//                pic.setIsLiked((Boolean) linkedHashMap.get("isLiked"));
//            } else {
//                picturePageResponse.getDataList().remove(pic);
//                i--;
//            }
//        }
//        Collections.sort(picturePageResponse.getDataList(), new Comparator<PictureResponse>() {
//            public int compare(PictureResponse p1, PictureResponse p2) {
//                return (p1.getNumOfLikes() < p2.getNumOfLikes()) ? -1 : (p1.getNumOfLikes() > p2.getNumOfLikes()) ? 1 : 0;
//            }
//        });
        Page<Object> objectPage = pictureRepo.getPopularPicture(username, pageRequest);
        List<PictureResponse> pictureResponseList = new ArrayList<PictureResponse>();
        for (Object object : objectPage.getContent()) {
            Object[] objects = (Object[]) object;
            Picture picture = (Picture) objects[0];
            PictureResponse pictureResponse = new PictureResponse();
            pictureResponse.setData(picture);
            pictureResponseList.add(pictureResponse);
        }
        PicturePageResponse picturePageResponse = new PicturePageResponse();
        picturePageResponse.setNumberOfElements(objectPage.getNumberOfElements());
        picturePageResponse.setPageNumber(objectPage.getNumber());
        picturePageResponse.setPageSize(objectPage.getSize());
        picturePageResponse.setTotalElements(objectPage.getTotalElements());
        picturePageResponse.setTotalPages(objectPage.getTotalPages());
        picturePageResponse.setDataList(pictureResponseList);
        this.checkStatus(picturePageResponse);
        return picturePageResponse;
    }

    Picture getPicture(Integer pictureId) throws NoInstanceException {
        Picture picture = pictureRepo.getOne(pictureId);
        if (picture == null) {
            throw new NoInstanceException("Picture", pictureId);
        } else {
            return picture;
        }
    }

    public PicturePageResponse getAllPictureSources(Integer itemId, Integer page, Integer pageSize) {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<String> sourcePage = pictureRepo.getAllPictureSources(itemId, pageRequest);
        List<PictureResponse> pictureResponses = new ArrayList<PictureResponse>();
        for (String source : sourcePage.getContent()) {
            PictureResponse pictureResponse = new PictureResponse();
            pictureResponse.setSource(source);
            pictureResponses.add(pictureResponse);
        }
        PicturePageResponse picturePageResponse = new PicturePageResponse();
        picturePageResponse.setNumberOfElements(sourcePage.getNumberOfElements());
        picturePageResponse.setPageNumber(sourcePage.getNumber());
        picturePageResponse.setPageSize(sourcePage.getSize());
        picturePageResponse.setTotalElements(sourcePage.getTotalElements());
        picturePageResponse.setTotalPages(sourcePage.getTotalPages());
        picturePageResponse.setDataList(pictureResponses);
        return picturePageResponse;
    }

    public PicturePageResponse getAllPicturesByItemId(Integer itemId, Integer pictureId, Integer page, Integer pageSize)
            throws UnauthenticatedException, ServerTechnicalException, NoInstanceException {
        Integer userId = securityService.retrieveCurrentUser0().getId();
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Object[]> objectPage = pictureRepo.getAllPicturesByItemId(itemId, pictureId, pageRequest);
        List<PictureResponse> pictureResponses = new ArrayList<PictureResponse>();

        if (pictureId > 0) {
            PictureResponse pictureResponse = getPictureById(pictureId);
            pictureResponses.add(pictureResponse);
        }

        for (Object[] objects : objectPage.getContent()) {
            Picture picture = (Picture) objects[0];
            if (picture.getId() != pictureId) {
                PictureResponse pictureResponse = new PictureResponse();
                pictureResponse.setData(picture);
                pictureResponse.setNumOfLikes(Integer.parseInt(String.valueOf((Long) objects[1])));
                pictureResponse.setIsLiked(false);
                if (objects[2] != null) {
                    if (userId == (Integer) objects[2]) {
                        pictureResponse.setIsLiked(true);
                    }
                }
                pictureResponses.add(pictureResponse);
            }
        }
        PicturePageResponse picturePageResponse = new PicturePageResponse();
        picturePageResponse.setNumberOfElements(objectPage.getNumberOfElements());
        picturePageResponse.setPageNumber(objectPage.getNumber());
        picturePageResponse.setPageSize(objectPage.getSize());
        picturePageResponse.setTotalElements(objectPage.getTotalElements());
        picturePageResponse.setTotalPages(objectPage.getTotalPages());
        picturePageResponse.setDataList(pictureResponses);
        return picturePageResponse;
    }

    public PictureResponse create(String source, String format, Integer itemId) throws
            UnauthenticatedException, ServerTechnicalException {
        Item item = itemRepository.findOne(itemId);
        Picture picture = new Picture();
        picture.setSource(source);
        picture.setItem(item);
        picture.setHtmlFormat(format);
        User user = securityService.retrieveCurrentUser0();
        picture.setUploader(user);
        pictureRepo.save(picture);
        //Generating response
        PictureResponse pictureResponse = new PictureResponse();
        pictureResponse.setData(picture);
        return pictureResponse;
    }

    void checkStatus(PicturePageResponse pageResponse) throws ServerTechnicalException {
        for (PictureResponse pictureResponse : pageResponse.getDataList()) {
            LinkedHashMap<String, Object> linkedHashMap = likedPictureService.checkStatus(pictureResponse.getId());
            pictureResponse.setNumOfLikes((Integer) linkedHashMap.get("numOfLikes"));
            pictureResponse.setIsLiked((Boolean) linkedHashMap.get("isLiked"));
        }
    }

    public PictureResponse getPictureById(Integer pictureId) throws NoInstanceException, ServerTechnicalException {
        Picture picture = this.getPicture(pictureId);
        LinkedHashMap<String, Object> map = likedPictureService.checkStatus(pictureId);
        PictureResponse pictureResponse = new PictureResponse();
        pictureResponse.setData(picture);
        pictureResponse.setIsLiked((Boolean) map.get("isLiked"));
        pictureResponse.setNumOfLikes((Integer) map.get("numOfLikes"));
        return pictureResponse;
    }

    public PicturePageResponse getSelectedPictureByLearningItemId(Integer learningItemId, Integer page, Integer pageSize) throws ServerTechnicalException {
        PageRequest pageRequest = new PageRequest(page, pageSize);
        Page<Picture> picturePage = pictureRepo.getSelectedPictureByLearningItemId(learningItemId, pageRequest);
        PicturePageResponse picturePageResponse = new PicturePageResponse();
        picturePageResponse.from(picturePage);
        if (picturePageResponse.getTotalElements() > 0) {
            LinkedHashMap<String, Object> map = likedPictureService.checkStatus(
                    picturePageResponse.getDataList().get(0).getId());
            PictureResponse pictureResponse = picturePageResponse.getDataList().get(0);
            pictureResponse.setIsLiked((Boolean) map.get("isLiked"));
            pictureResponse.setNumOfLikes((Integer) map.get("numOfLikes"));
        }
        return picturePageResponse;
    }
}
