package com.vlls.service;

import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.exception.UnauthorizedException;
import com.vlls.jpa.domain.Friendship;
import com.vlls.jpa.domain.User;
import com.vlls.service.model.UserSessionInfo;
import com.vlls.web.model.CourseResponse;
import com.vlls.web.model.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by hiephn on 2014/11/30.
 */
@Service
public class NotificationService extends AbstractService {

    private static final long TIME_WAIT = 5000;
    private static final int MAX_WAIT = 24;
    @Resource(name = "vllsPropertyResolver")
    protected PropertyResolver vllsProperties;
    @Autowired
    private LearningItemService learningItemService;
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private LearningCourseService learningCourseService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserService userService;

    public List<NotificationResponse> get(long from)
            throws UnauthenticatedException, ServerTechnicalException {
        Date revisionFromDate = new Date(from);
        Date friendshipFromDate = new Date(from);
        long learningCourseUpdate;
        long friendshipUpdate;
        if (from > 0) {
            learningCourseUpdate = learningCourseService.countIfLastUpdateAfter(revisionFromDate);
            friendshipUpdate = friendshipService.countIfLastUpdateAfter(friendshipFromDate);
        } else {
            learningCourseUpdate = 1;
            friendshipUpdate = 1;
        }

        int stillWait = MAX_WAIT;
        while ((learningCourseUpdate + friendshipUpdate) == 0
                && stillWait > 0) {
            try {
                Thread.sleep(TIME_WAIT);
                learningCourseUpdate = learningCourseService.countIfLastUpdateAfter(revisionFromDate);
                friendshipUpdate = friendshipService.countIfLastUpdateAfter(friendshipFromDate);
                --stillWait;
            } catch (InterruptedException e) {
                throw new ServerTechnicalException("Error while getting notification");
            }
        }
        List<NotificationResponse> notificationResponses;
        if ((learningCourseUpdate + friendshipUpdate) > 0) {
            List<Object[]> reviseItemsNumber = learningItemService.getReviseItemsNumber();
            List<Friendship> pendingFriends = friendshipService.getAllPendingFriend();
            notificationResponses = new ArrayList<>(reviseItemsNumber.size() + pendingFriends.size());

            reviseItemsNumber.forEach(reviseItem -> {
                NotificationResponse notificationResponse = new NotificationResponse();
                notificationResponse.setType("REVISION");
                notificationResponse.setLearningCourseId((Integer) reviseItem[0]);
                notificationResponse.setHeader((String) reviseItem[1]);
                notificationResponse.setImage((String) reviseItem[2]);
                notificationResponse.setMessage(reviseItem[3] + " items are ready to revise");
                notificationResponses.add(notificationResponse);
            });

            pendingFriends.forEach(pendingFriend -> {
                NotificationResponse notificationResponse = new NotificationResponse();
                notificationResponse.setType("FRIEND_PENDING");
                notificationResponse.setAuthor(pendingFriend.getFriendOne().getName());
                notificationResponse.setHeader(pendingFriend.getFriendOne().getName());
                notificationResponse.setMessage("sends you a friend request");
                notificationResponse.setImage(pendingFriend.getFriendOne().getAvatar());
                notificationResponses.add(notificationResponse);
            });
        } else {
            notificationResponses = new ArrayList<>(1);
            NotificationResponse notificationResponse = new NotificationResponse();
            notificationResponse.setType("NO_UPDATE");
            notificationResponses.add(notificationResponse);
        }
        List<NotificationResponse> notices = this.getLog();
        if (notices != null && notices.size() > 0) {
            notificationResponses.addAll(notices);
        }
        try {
            List<CourseResponse> courseResponses = this.getNotificationRecommendedCourse();
            for (CourseResponse courseResponse : courseResponses) {
                NotificationResponse notificationResponse = new NotificationResponse();
                notificationResponse.setType("RECOMMENDED_COURSE");
                notificationResponse.setLearningCourseId(courseResponse.getId());
                notificationResponse.setHeader(courseResponse.getName());
                notificationResponse.setImage(courseResponse.getAvatar());
                notificationResponse.setMessage("Recommended by " + courseResponse.getCreatorName());
                notificationResponses.add(notificationResponse);
            }
        } catch (NoInstanceException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notificationResponses;
    }

    public Boolean log(String user, NotificationResponse note) {
        try {
            HashMap _map = new HashMap();
            int _cols = 5;
            int _rows = 0;
            String notificationFile = vllsProperties.getProperty("file.notification.location");
            notificationFile += "notification-" + user + ".csv";
            File file = new File(notificationFile);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                //Read csv file and parse line values to _map
                Scanner scanner = new Scanner(file);
                scanner.useDelimiter(Character.toString(','));
                while (scanner.hasNextLine()) {
                    String[] values = scanner.nextLine().split(Character.toString(','));
                    int col = 0;
                    for (String value : values) {
                        HashMap point = new HashMap();
                        point.put("col", col);
                        point.put("row", _rows);
                        _map.put(point, value);
                        col++;
                    }
                    _rows++;
                }
                scanner.close();
            }
            // Create new csv line
            HashMap newCol0 = new HashMap();
            newCol0.put("col", 0);
            newCol0.put("row", _rows);
            _map.put(newCol0, note.getType());
            HashMap newCol1 = new HashMap();
            newCol1.put("col", 1);
            newCol1.put("row", _rows);
            _map.put(newCol1, note.getImage());
            HashMap newCol2 = new HashMap();
            newCol2.put("col", 2);
            newCol2.put("row", _rows);
            _map.put(newCol2, note.getHeader());
            HashMap newCol3 = new HashMap();
            newCol3.put("col", 3);
            newCol3.put("row", _rows);
            _map.put(newCol3, note.getMessage());
            HashMap newCol4 = new HashMap();
            newCol4.put("col", 4);
            newCol4.put("row", _rows);
            _map.put(newCol4, note.getAuthor());
            _rows++;
            //Save to csv file
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            for (int row = 0; row < _rows; row++) {
                for (int col = 0; col <= _cols; col++) {
                    HashMap key = new HashMap();
                    key.put("col", col);
                    key.put("row", row);
                    if (_map.containsKey(key)) {
                        bw.write((String) _map.get(key));
                    }
                    if ((col + 1) < _cols) {
                        bw.write(',');
                    }
                }
                bw.newLine();
            }
            bw.flush();
            bw.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public List<NotificationResponse> getLog() {
        List<NotificationResponse> list;
        UserSessionInfo user = securityService.retrieveCurrentUserSessionQuietly();
        if (user == null) {
            return null;
        }
        String notificationFile = vllsProperties.getProperty("file.notification.location");
        notificationFile += "notification-" + user.getName() + ".csv";
        BufferedReader br = null;
        String line = "";
        File file;
        try {
            file = new File(notificationFile);
            if (!file.exists()) {
//                System.out.println("File " + notificationFile + " does not exists.");
                return null;
            }
            list = new ArrayList<NotificationResponse>();
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                String[] detail = line.split(",");
                NotificationResponse notificationResponse = new NotificationResponse();
                notificationResponse.setType(detail[0]);
                notificationResponse.setImage(detail[1]);
                notificationResponse.setHeader(detail[2]);
                notificationResponse.setMessage(detail[3]);
                if (4 != detail.length) {
                    notificationResponse.setAuthor(detail[4]);
                } else {
                    notificationResponse.setAuthor("");
                }
                list.add(notificationResponse);
            }
            br.close();
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Boolean clearLog(String user) {
        try {
            String notificationFile = vllsProperties.getProperty("file.notification.location");
            notificationFile += "notification-" + user + ".csv";
            File file = new File(notificationFile);
            file.setWritable(true);
            if (file.delete()) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<CourseResponse> getNotificationRecommendedCourse() throws NoInstanceException, UnauthorizedException, UnauthenticatedException, IOException {
        UserSessionInfo userSessionInfo = securityService.retrieveCurrentUserSessionQuietly();
        List<CourseResponse> result = new ArrayList<CourseResponse>();
        if (userSessionInfo != null) {
            List<User> creators = userService.getLearningCoursesCreatorByLearnerId(userSessionInfo.getId());
            for (User creator : creators) {
                String recommendFile = vllsProperties.getProperty("file.recommendation.location");
                recommendFile += "recommendation-" + creator.getName() + ".txt";
                File file = new File(recommendFile);
                if (file.exists()) {
                    String text = new String(Files.readAllBytes(Paths.get(recommendFile)), StandardCharsets.UTF_8);
                    if (!"".equals(text.trim())) {
                        String[] recommendInfo = text.split(";");
                        for (int i = 0; i < recommendInfo.length; i++) {
                            String info = recommendInfo[i];
                            if (Integer.parseInt(info.split(":")[0]) == userSessionInfo.getId()) {
                                CourseResponse courseResponse = new CourseResponse();
                                courseResponse.setData(courseService.get0(Integer.parseInt(info.split(":")[1])));
                                result.add(courseResponse);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
