/**
 * Created by thongvh on 2014/11/03.
 */
define(['durandal/app', 'jquery', 'plugins/router', '../master', 'resource', 'paging', 'chartjs', 'notifies', 'knob'], function (app, $, router, master, resource, Paging, Chart, Notifies) {
    var LEANER_URL = app.conf.BACK_URL + '/course/learner';
    var TEACHING_COURSE_URL = app.conf.BACK_URL + '/course/teachingForRecommend';
    var COURSE_CURRENT_RECOMMENDATION_URL = app.conf.BACK_URL + '/course/creator/currentRecommended';
    var COURSE_UPDATE_RECOMMENDATION_URL = app.conf.BACK_URL + '/course/creator/updateRecommendation';
    /* Statistic */
    var LEARNING_COURSE_STAT = app.conf.BACK_URL + "/learning-course/statistic",
        statisticTemplate = {
            "learntItems": 0,
            "learningItems": 0,
            "remainingItems": 0,
            "points": 0,
            "pointsToGo": 0,
            "difficultWords": {
                "Item1": 0.0
            }
        },
        ChartJs = Chart.noConflict();

    var self;
    var VM = {
        itemChart: null,
        qualityChart: null,
        difficultChart: null,
        showItemChart: app.koo(false),
        showQualityChart: app.koo(false),
        showDifficultChart: app.koo(false),
        learners: app.koa(),
        course: master.course,
        paging: new Paging(),
        pagingCourse: new Paging(),
        courses: app.koa(),
        user: master.user,
        learningCourse: master.learningCourse,
        enroll: master.enroll,
        displayCharts: app.koo(true),
        displayCourses: app.koo(false),
        activate: function (courseId) {
            self = this;
            self.loadLearners(0);
        },
        loadLearners: function (page) {
            resource.user({
                page: page,
                pageSize: 12,
                courseId: self.course.id()
            }, {
                url: LEANER_URL
            }).get(self.learners).then(function (data) {
                self.learners().forEach(function (user) {
                    app.http.get({
                        url: app.conf.BACK_URL + "/learning-course/learner-status",
                        data: {
                            id: user.id(),
                            courseId: self.course.id()
                        },
                        success: function (learningData) {
                            if (learningData.numOfItems && learningData.numOfLearntItem) {
                                user.learningProgress((learningData.numOfLearntItem / learningData.numOfItems) * 100);
                            }
                        }
                    });
                });
                self.paging.load(data);
            });
        },
        loadCourses: function (page) {
            resource.course({
                username: self.selectedLearnerName(),
                page: page,
                pageSize: 12
            }, {
                url: TEACHING_COURSE_URL
            }).get(self.courses).then(function (data) {
                self.pagingCourse.load(data);
            });
        },
        selectedLearnerName: app.koo(),
        selectedLearnerId: app.koo(-1),
        loadStatistic: function (id, name) {
            self.showItemChart(false);
            self.showQualityChart(false);
            self.showDifficultChart(false);
            self.selectedLearnerName(name);
            self.selectedLearnerId(id);
            self.loadCourses(0);
            resource.learningCourse({
                userId: id,
                courseId: self.course.id()
            }).get().then(function (json) {
                var $itemChart = $('#itemChart'),
                    $qualityChart = $('#qualityChart'),
                    $difficultChart = $('#difficultChart');

                if ($itemChart[0]) {
                    self.itemChart = new ChartJs($itemChart[0].getContext('2d'))
                        .Pie([], {});
                    self.qualityChart = new ChartJs($qualityChart[0].getContext('2d'))
                        .Pie([], {});
                    self.difficultChart = new ChartJs($difficultChart[0].getContext('2d'))
                        .Bar({
                            labels: [],
                            datasets: [
                                {
                                    fillColor: "rgba(217,83,79,0.5)",
                                    strokeColor: "rgba(193,46,42,1)",
                                    data: []
                                }
                            ]
                        }, {});
                    app.http.get({
                        url: LEARNING_COURSE_STAT,
                        data: {
                            id: json.dataList[0].id
                        }, success: function (json) {
                            // Update Item chart
                            if (json.learntItems > 0 || json.learningItems > 0 || json.remainingItems > 0) {
                                self.itemChart.removeData();
                                self.itemChart.addData({
                                    value: json.learntItems,
                                    color: "#5cb85c",
                                    highlight: "#419641",
                                    label: "Learned items"
                                });
                                self.itemChart.addData({
                                    value: json.learningItems,
                                    color: "#428bca",
                                    highlight: "#2d6ca2",
                                    label: "Learning items"
                                });
                                self.itemChart.addData({
                                    value: json.remainingItems,
                                    color: "#f0ad4e",
                                    highlight: "#eb9316",
                                    label: "Remaining items"
                                });
                                self.showItemChart(true);
                            }

                            // Update quality chart
                            if (json.points > 0 || json.pointsToGo > 0) {
                                self.qualityChart.removeData();
                                self.qualityChart.addData({
                                    value: json.points,
                                    color: "#5cb85c",
                                    highlight: "#419641",
                                    label: "Points"
                                });
                                self.qualityChart.addData({
                                    value: json.pointsToGo,
                                    color: "#f0ad4e",
                                    highlight: "#eb9316",
                                    label: "Points to go"
                                });
                                self.showQualityChart(false);
                            }

                            // Update difficult chart
                            if (json.difficultWords.length > 0) {
                                for (var i = 0; i < self.difficultChart.datasets[0].bars.length; ++i) {
                                    self.difficultChart.removeData();
                                }
                                self.difficultChart.addData([10], 'dummy');
                                json.difficultWords.forEach(function (difficultWord) {
                                    self.difficultChart.addData([difficultWord.value], difficultWord.text);
                                });
                                self.difficultChart.removeData();
                                self.difficultChart.datasets[0].bars.sort(function (a, b) {
                                    return a.value < b.value;
                                });
                                self.showDifficultChart(true);
                            }
                        }
                    });
                }
            });
            self.showCharts();
            self.getRecommendedCourse(id);
        },
        showCharts: function () {
            self.displayCharts(true);
            self.displayCourses(false);
        },
        showCourses: function () {
            self.displayCharts(false);
            self.displayCourses(true);
        },
        selectedCourse: app.koo(-1),
        toggleSelectedCourse: function (courseId, courseName) {
            app.http.get({
                url: COURSE_UPDATE_RECOMMENDATION_URL,
                data: {
                    userId: self.selectedLearnerId(),
                    courseId: courseId,
                    isSave: (self.selectedCourse() != courseId)
                },
                success: function (isSuccess) {
                    if (isSuccess) {
                        var $alert = $('#modalAlert');
                        if (self.selectedCourse() == courseId) {
                            self.selectedCourse(-1);
                            $alert.html("You have stopped recommending course " + courseName + " to " + self.selectedLearnerName());
                            $alert.prop('class', 'alert alert-warning');
                            $alert.fadeIn();
                            setTimeout(function () {
                                $alert.fadeOut();
                            }, 2000);
                        } else {
                            self.selectedCourse(courseId);
                            $alert.html("You have recommended course " + courseName + " to " + self.selectedLearnerName());
                            $alert.prop('class', 'alert alert-success');
                            $alert.fadeIn();
                            setTimeout(function () {
                                $alert.fadeOut();
                            }, 2000);
                        }
                    }
                }
            })
        },
        getRecommendedCourse: function (id) {
            app.http.get({
                url: COURSE_CURRENT_RECOMMENDATION_URL,
                data: {
                    userId: id
                },
                success: function (courseId) {
                    self.selectedCourse(courseId);
                }
            });
        }
    };

    return VM;
});
