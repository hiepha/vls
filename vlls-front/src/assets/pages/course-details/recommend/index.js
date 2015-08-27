/**
 * Created by TuanNKA on 2014/11/10.
 */
define(['durandal/app', 'jquery', 'plugins/router', '../master', 'resource'], function (app, $, router, master, resource) {
    var COURSE_URL = app.conf.BACK_URL + "/course",
        self = null;
    var VM = {
            course: master.course,
            similarCourses: {
                pageNumber: app.koo(0),
                totalPages: app.koo(0),
                dataList: app.koa()
            },
            alsoLearnCourses: {
                pageNumber: app.koo(0),
                totalPages: app.koo(0),
                dataList: app.koa()
            },
            activate: function () {
                self = this;
                this.loadSimilarCourses(0);
                this.loadAlsoLearnCourses(0);
            },
            attached: function () {
            },
            detached: function () {
            },
            loadSimilarCourses: function (page) {
                resource.course({
                    page: page,
                    pageSize: 5,
                    courseId: self.course.id()
                }, {url: COURSE_URL + '/recommend/similar'}).get().then(function (data) {
                    app.koMap.fromJS(data.dataList, {}, self.similarCourses.dataList);
                    self.similarCourses.pageNumber(data.pageNumber);
                    self.similarCourses.totalPages(data.totalPages);
                });
            },
            loadAlsoLearnCourses: function (page) {
                resource.course({
                    page: page,
                    pageSize: 5,
                    courseId: self.course.id()
                }, {url: COURSE_URL + '/recommend/also-learn'}).get().then(function (data) {
                    app.koMap.fromJS(data.dataList, {}, self.alsoLearnCourses.dataList);
                    self.alsoLearnCourses.pageNumber(data.pageNumber);
                    self.alsoLearnCourses.totalPages(data.totalPages);
                });
            }
        }
        ;
    return VM;
})
;
