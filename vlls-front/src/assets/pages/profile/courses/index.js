/**
 * Created by Dell on 10/13/2014.
 */
define(['durandal/app', 'jquery', 'plugins/router', 'resource', 'user', 'paging', 'starRating'], function (app, $, router, resource, user, Paging) {
    var COURSE_URL = app.conf.BACK_URL + '/course/',
        LEARNING_COURSE_URL = app.conf.BACK_URL + '/learning-course/';
    var self = null;
    var VM = {
        courses: app.koa(),
        learningCourses: app.koa(),
        type: app.koo(),
        username: app.koo(),
        isCurUser: app.koo(false),
        paging: new Paging(),
        activate: function (username) {
            self = this;
            self.isCurUser(user.name() === username);
            self.learningCourses.removeAll();
            self.courses.removeAll();
            self.username(username);
            self.loadCourses('learning', 0);
        },
        attached: function () {
        },
        compositionComplete: function () {
        },
        detached: function () {
            self.learningCourses.removeAll();
            self.courses.removeAll();
        },
        loadCourses: function (type, page) {
            self.learningCourses.removeAll();
            self.courses.removeAll();
            self.type(type);
            if (self.isCurUser() && type == 'learning') {
                resource.learningCourse({
                    withCourse: true,
                    page: page,
                    pageSize: 5
                }, {
                    url: LEARNING_COURSE_URL + type
                }).get(self.learningCourses).then(function (data) {
                    self.paging.load(data);
                });
            } else {
                resource.course({
                    username: self.username,
                    page: page,
                    pageSize: 12
                }, {
                    url: COURSE_URL + type
                }).get(self.courses).then(function (data) {
                    self.paging.load(data);
                });
            }
        }
    };
    return VM;
});
