/**
 * Created by TuanNKA on 10/5/2014.
 */
define(['durandal/app', 'jquery', 'shell', 'plugins/router', 'user', 'resource'], function (app, $, shell, router, user, resource) {
    var self;
    var VM = {
        learningCourse: resource.learningCourse().oTemplate(),
        learningItems: app.koa([]),
        learningLevels: app.koa([]),
        prevLearningLevel: resource.learningLevel().oTemplate(),
        curLearningLevel: resource.learningLevel().oTemplate(),
        nextLearningLevel: resource.learningLevel().oTemplate(),
        learningLevelsRaw: null,

        learningCourseId: app.koo(),
        courseId: app.koo(),
        courseName: app.koo(),
        levelId: app.koo(),
        levelName: app.koo(),
        prevLevelId: app.koo(),
        prevLevelName: app.koo(),
        nextLevelId: app.koo(),
        nextLevelName: app.koo(),
        numberOfElements: app.koo(),
        numberOfReviseItems: app.koo(),
        numberOfPossibleLearningItems: app.koo(),

        activate: function (courseId, levelId) {
            self = this;
            window.ld = this;
            this.loadLearningCourseCaching(courseId, levelId);
            this.updateReviseTime(5);
        },
        attached: function () {
        },
        compositionComplete: function () {
        },
        detached: function () {
        },
        loadLearningCourseCaching: function (courseId, levelId) {
            // Check if the 1 minutes cache is expired
            var currentMillis = new Date().getTime();
            if ((this.learningCourse.timeout
                && this.learningCourse.timeout < currentMillis)
                || this.learningCourse.course.id() != courseId) {
                // Refresh cache
                resource.learningCourse({courseId: courseId, withCourse: true}).get(self.learningCourse).then(function (json) {
                    // Cache learning course data for 1 minute
                    self.learningCourse.timeout = currentMillis + (1000 * 60);
                    self.loadLearningLevels(json.dataList[0].id, levelId);
                });
            } else {
                self.updateCurLevel(levelId);
            }
        },
        loadLearningLevels: function (learningCourseId, levelId) {
            resource.learningLevel({learningCourseId: learningCourseId}).get(self.learningLevels).then(function (data) {
                self.learningLevelsRaw = data.dataList;
                self.updateCurLevel(levelId);
            });
        },
        updateCurLevel: function (levelId) {
            for (var i = 0; i < self.learningLevelsRaw.length; ++i) {
                if (self.learningLevelsRaw[i].level.id == levelId) {
                    app.koMap.fromJS(self.learningLevelsRaw[i], {}, self.curLearningLevel);
                    // Update previous level
                    if (i > 0) {
                        app.koMap.fromJS(self.learningLevelsRaw[i - 1], {}, self.prevLearningLevel);
                    } else {
                        resource.learningLevel().oTemplate(self.prevLearningLevel);
                    }
                    // Update next level
                    if (i < self.learningLevelsRaw.length - 1) {
                        app.koMap.fromJS(self.learningLevelsRaw[i + 1], {}, self.nextLearningLevel);
                    } else {
                        resource.learningLevel().oTemplate(self.nextLearningLevel);
                    }
                    self.loadLearningItems(self.curLearningLevel.id());
                }
            }
        },
        loadLearningItems: function (levelId) {
            resource.learningItem({learningLevelId: levelId}).get(self.learningItems).then(function (data) {
                self.numberOfElements(data.numberOfElements);
                self.numberOfReviseItems(data.dataList.filter(function(learningItem) {
                    return learningItem.reviseTimeLeft && learningItem.reviseTimeLeft <= 0 && learningItem.reviseNo < 6;
                }).length);
                self.numberOfPossibleLearningItems(Math.min(5, data.dataList.filter(function(learningItem) {
                    return learningItem.progress < 100;
                }).length));
            });
        },
        updateReviseTime: function (second) {
            setInterval(function () {
                if (self.learningItems().length > 0) {
                    self.learningItems().forEach(function (item) {
                        if (item.reviseTimeLeft() > 0) {
                            var time = item.reviseTimeLeft() - second;
                            item.reviseTimeLeft((time > 0) ? time : -1);
                        }
                    });
                }
            }, second * 1000);
        },
        openLearnPopup: function(html) {
            var learnPopup = window.open(html, 'pagename', 'resizable,height=650,width=1000');
            learnPopup.onclose = function() {
//                self.activate(self.learningCourse.course.id(), self.curLearningLevel.level.id());
                window.location.reload();
            };
        }
    };
    return VM;
});
