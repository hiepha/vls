/**
 * Created by hiephn on 2014/10/02.
 */
define(['durandal/app', 'jquery', 'plugins/router', '../master', 'resource', 'confModals', 'knob'], function (app, $, router, master, resource, modal) {
    var LEARNING_COURSE_STAT = app.conf.BACK_URL + "/learning-course/status";
    var LEARNING_COURSE_QUIT = app.conf.BACK_URL + "/learning-course/quit",
        RANKING_URL = app.conf.BACK_URL + "/course/ranking",
        courseStat = {
            numOfItems: 0,
            numOfLearntItem: 0,
            progress: 0
        },
        that = null;

    var VM = {
        ranking: app.koa([]),
        courseStatus: app.koMap.fromJS(courseStat),
        levels: app.koa([]),
        user: master.user,
        courseId: null,
        learningCourse: resource.learningCourse().oTemplate(),
        learningLevels: app.koa([]),
        cacheViews: false,

        canReuseForRoute: function () {
            return true;
        },
        activate: function (courseId) {
            that = this;
            window.details = this;
            this.courseId = courseId;

            master.enrollThen('levelCallback1', this.loadLearningCourse.bind(this, courseId));
            master.enrollThen('levelCallback2', this.getRankingData.bind(this));

            this.initData(courseId);
        },
        initData: function (courseId) {
            // Load Level
            resource.level({courseId: courseId}).get(that.levels);

            // Load Learning Course
            that.loadLearningCourse(courseId);

            // Load ranking data
            that.getRankingData();
        },
        deactivate: function () {
            if (router.activeItem().__moduleId__.indexOf('course-details') < 0) {
                that.ranking.removeAll();
                app.koMap.fromJS(courseStat, {}, that.courseStatus);
                that.levels.removeAll();
                that.courseId = null;
                resource.learningCourse().oTemplate(that.learningCourse);
                that.learningLevels.removeAll();
            }
        },
        loadLearningCourse: function (courseId) {
            resource.learningCourse().oTemplate(that.learningCourse);
            that.learningLevels.removeAll();
            if (this.user.isLogin()) {
                resource.learningCourse({courseId: courseId}, {
                    '401': function () {
                        // user does not log in, don't throw error
                    }, '404': function () {
                        // user does not enroll, don't throw error
                    }
                }).get().then(function (json) {
                    app.koMap.fromJS(json.dataList[0], {}, that.learningCourse);
                    that.getCourseStatus();
                    that.loadLearningLevels(json.dataList[0]);
                });
            }
        },
        loadLearningLevels: function (learningCourse) {
            resource.learningLevel({learningCourseId: learningCourse.id}, {
                '401': function () {
                    // hide this error, user is not login
                }
            }).get(that.learningLevels);
        },
        getCourseStatus: function () {
            app.http.get({
                url: LEARNING_COURSE_STAT,
                data: {
                    courseId: that.courseId
                },
                success: function (data) {
                    app.koMap.fromJS(data, {}, that.courseStatus);
                    var perc = that.courseStatus.numOfLearntItem() / that.courseStatus.numOfItems() * 100;
                    that.courseStatus.progress(perc);
                },
                '401': function () {
                    // user does not log in, don't throw error
                }
            })
        },
        getRankingData: function () {
            app.http.get({
                url: RANKING_URL,
                data: {
                    courseId: that.courseId
                },
                success: function (data) {
                    app.koMap.fromJS(data, {}, that.ranking);
                }
            })
        },
        openLearnPopup: function (html) {
            var learnPopup = window.open(html, 'pagename', 'resizable,height=650,width=1000');
            learnPopup.onclose = function () {
                that.loadLearningCourse(that.courseId);
            };
        },
        quitCourse: function (id) {
            modal.confirm('Are you sure you would like to quit?', function () {
                resource.learningCourse({
                    learningCourseId: id
                }, {
                    url: LEARNING_COURSE_QUIT
                }).get().then(function () {
                    location.reload();
                });
            });
        },
        restartCourse: function (id) {
            modal.confirm('Are you sure you would like to restart?', function () {
                resource.learningCourse({
                    learningCourseId: id
                }, {
                    url: LEARNING_COURSE_QUIT
                }).get().then(function () {
                    resource.learningCourse({
                        courseId: master.course.id()
                    }).post(that.learningCourse).then(function (json) {
                        location.reload();
                    });
                })
            });
        }
    };
    return VM;
});