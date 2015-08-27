/**
 * Created by hiephn on 2014/08/26.
 */
define('shell', ['durandal/app', 'knockout', 'plugins/router', 'user', 'resource'], function (app, ko, router, user, resource) {
    var that = null;
    var UPDATE_RECOMMENDED_COURSE_URL = app.conf.BACK_URL + '/course/learner/updateRecommendation';
    var VM = {
        user: user,
        hasHeader: app.koo(true),
        hasFooter: app.koo(true),
        notifications: app.koa([]),
        notices: app.koa([]),
        recommendedCourses: app.koa(),
        lastNotified: 0,

        activate: function () {
            that = this;
            window.router = router;
            window.shell = this;
            user.revise().then(function () {
                that.pollNotification();
            });

            user.afterLogin('pollNotification', that.pollNotification);
            user.afterLogout('resetNotification', that.resetNotification);

            router.map([
                { route: ['', 'home'], title: 'Home', moduleId: 'home/index', nav: true },
                { route: 'login', title: 'Login', moduleId: 'login/index', nav: true },
                { route: 'register', title: 'Register', moduleId: 'register/index', nav: true },
                { route: 'create-course', title: 'Create New Course', moduleId: 'create-course/index', nav: true },
                { route: 'edit-course/:id*details', title: 'Edit Course', moduleId: 'edit-course/index', nav: true, hash: '#edit-course/:id' },
                { route: 'settings*details', title: 'Settings', moduleId: 'settings/index', nav: true },
                { route: 'course-details/:courseId*details', title: 'Course Detail', moduleId: 'course-details/master', nav: true, hash: '#course-details/:id' },
                { route: 'course', title: 'Course', moduleId: 'course/index', nav: true },
                { route: 'profile/:username*details', title: 'Profile', moduleId: 'profile/index', nav: true },
                { route: 'learning-details/:courseId/:levelId', title: 'Learning Details', moduleId: 'learning-details/index', nav: true, hash: "#learning-details/:courseId/:levelId"},
                { route: 'learning/:levelId', title: 'Learning', moduleId: 'learning/index', nav: false, hash: "#learning/:levelId"}
            ]).buildNavigationModel();

            return router.activate();
        },
        attached: function () {
        },
        logout: function () {
            user.logout().then(function () {
                router.navigate('#course');
            });
        },
        pollNotification: function () {
            if (user.isLogin()) {
                resource.notification({from: that.lastNotified}, {
                    '401': function () {
                        // user logout
                    }
                }).get().then(function (dataList) {
                    if (dataList.length == 1 && dataList[0].type == 'NO_UPDATE') {
                        // Nothing to do
                    } else {
                        var notices = [];
                        var notifications = [];
                        var recommendedCourse = [];
                        dataList.forEach(function (data) {
                            if (data.type != 'NO_UPDATE') {
                                if (data.type != 'REVISION' && data.type != 'FRIEND_PENDING') {
                                    if (data.type == 'RECOMMENDED_COURSE') {
                                        recommendedCourse.push(data);
                                    } else {
                                        notices.push(data);
                                    }
                                } else {
                                    notifications.push(data);
                                }
                            }
                        });
                        app.koMap.fromJS(notifications, {}, that.notifications);
                        app.koMap.fromJS(notices, {}, that.notices);
                        app.koMap.fromJS(recommendedCourse, {}, that.recommendedCourses);
                    }
                }).done(function () {
                    that.lastNotified = new Date().getTime();
                    that.pollNotification();
                });
            }
        },
        resetNotification: function () {
            that.notifications.removeAll();
            that.lastNotified = 0;
        },
        navigateNotification: function (html) {
            if (html.indexOf('learn.html') == 0) {
                var learnPopup = window.open(html, 'pagename', 'resizable,height=650,width=1000');
                learnPopup.onclose = function () {
//                self.activate(self.learningCourse.course.id(), self.curLearningLevel.level.id());
                    window.location.reload();
                };
            } else {
                router.navigate(html);
            }
        },
        viewRecommendedCourse: function (courseId) {
            app.http.get({
                url: UPDATE_RECOMMENDED_COURSE_URL,
                data: {
                    courseId: courseId
                },
                success: function (isSuccess) {
                    if (isSuccess) {
                        router.navigate('/#course-details/' + courseId);
                        window.location.reload();
                    }
                }
            })
        },
        clearNotices: function () {
            if (user.isLogin()) {
                resource.notification({
                    username: user.name
                }, {
                    url: app.conf.BACK_URL + '/notification/clearLog'
                }).get().then(function (data) {
                    if (data) {
                        that.notices.removeAll();
                    }
                });
            }
        }
    };

    return VM;
});
