package com.vlls.web.model;

/**
 * Created by Dell on 10/6/2014.
 */
public class LearningItemsResponse {
    private int courseId;
    private String courseName;
    private int levelId;
    private String levelName;
    private int prevLevelId;
    private String prevLevelName;
    private int nextLevelId;
    private String nextLevelName;
    private LearningItemPageResponse pageResponse;

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getPrevLevelId() {
        return prevLevelId;
    }

    public void setPrevLevelId(int prevLevelId) {
        this.prevLevelId = prevLevelId;
    }

    public String getPrevLevelName() {
        return prevLevelName;
    }

    public void setPrevLevelName(String prevLevelName) {
        this.prevLevelName = prevLevelName;
    }

    public int getNextLevelId() {
        return nextLevelId;
    }

    public void setNextLevelId(int nextLevelId) {
        this.nextLevelId = nextLevelId;
    }

    public String getNextLevelName() {
        return nextLevelName;
    }

    public void setNextLevelName(String nextLevelName) {
        this.nextLevelName = nextLevelName;
    }

    public LearningItemPageResponse getPageResponse() {
        return pageResponse;
    }

    public void setPageResponse(LearningItemPageResponse pageResponse) {
        this.pageResponse = pageResponse;
    }

    public LearningItemsResponse(CourseResponse course, LevelResponse currLevel,
                                 LevelResponse prevLevel, LevelResponse nextLevel,
                                 LearningItemPageResponse learningItem) {
        this.setCourseId(course.getId());
        this.setCourseName(course.getName());
        this.setLevelId(currLevel.getId());
        this.setLevelName(currLevel.getName());
        if (prevLevel != null) {
            this.setPrevLevelId(prevLevel.getId());
            this.setPrevLevelName(prevLevel.getName());
        } else {
            this.setPrevLevelId(0);
            this.setPrevLevelName("");
        }
        if (nextLevel != null) {
            this.setNextLevelId(nextLevel.getId());
            this.setNextLevelName(nextLevel.getName());
        } else {
            this.setNextLevelId(0);
            this.setNextLevelName("");
        }
        this.setPageResponse(learningItem);
    }
}
