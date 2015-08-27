/**
 * Created by hiephn on 2014/10/02.
 */
define(['durandal/app', 'jquery', 'shell', 'plugins/router', 'user', 'starRating', 'resource'], function (app, $, shell, router, user, rating, resource) {
    var that = null;
    var COURSE_URL = app.conf.BACK_URL + '/course';
    var childRouter = router.createChildRouter()
        .makeRelative({
            moduleId: 'course-details',
            fromParent: true,
            dynamicHash: ':id'
        }).map([
            { route: ['', 'level'], moduleId: 'level/index', title: 'Level', type: 'intro', nav: true, icon: 'fa fa-list-alt', hashId: 'level', login: false, creator: false },
            { route: 'statistics', moduleId: 'statistics/index', title: 'Statistic', type: 'intro', nav: true, icon: 'fa fa-bar-chart-o', hashId: 'statistics', login: true, creator: false },
            { route: 'difficult', moduleId: 'difficult/index', title: 'Difficult', type: 'intro', nav: true, icon: 'fa fa-history', hashId: 'difficult', login: true, creator: false },
            { route: 'recommend', moduleId: 'recommend/index', title: 'Recommendation', type: 'intro', nav: true, icon: 'fa fa-star', hashId: 'recommend', login: false, creator: false },
            { route: 'learner', moduleId: 'learner/index', title: 'Learner', type: 'intro', nav: true, icon: 'fa fa-group', hashId: 'learner', login: true, creator: true }
        ]).buildNavigationModel();

    var VM = {
        course: resource.course().oTemplate(),
        learningCourse: resource.learningCourse().oTemplate(),
        router: childRouter,
        user: user,
        enrollCallbacks: {},

        resetVM: function () {
            resource.course().oTemplate(this.course);
            resource.learningCourse().oTemplate(this.learningCourse);
        },

        activate: function (courseId) {
            window.details = this;
            that = this;
            this.course.id(courseId);
            this.loadCourse();
        },
        deactivate: function () {
        },
        detached: function () {
            that.resetVM();
        },
        loadCourse: function () {
            resource.course({id: that.course.id(), withLearning: true}).get(that.course).then(function (json) {
                app.koMap.fromJS(json.dataList[0], {}, that.course);
                if (json.dataList[0].learningCourse) {
                    app.koMap.fromJS(json.dataList[0].learningCourse, {}, that.learningCourse);
                } else {
                    resource.learningCourse().oTemplate(that.learningCourse);
                }
            });
        },
        enroll: function () {
            resource.learningCourse({courseId: this.course.id()}).post(that.learningCourse).then(function (json) {
                for (var i in that.enrollCallbacks) {
                    that.enrollCallbacks[i](json);
                }
            });
        },
        enrollThen: function (key, func) {
            that.enrollCallbacks[key] = func;
        },
        saveRating: function (element) {
            var rating = parseInt($(element).val());
            resource.course({
                courseId: that.course.id(),
                rating: rating
            }, {
                url: COURSE_URL + '/rating'
            }).post().then(function (data) {
                app.koMap.fromJS(data, {}, that.course);
            });
        }
    };
    return VM;
});
