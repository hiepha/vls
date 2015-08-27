/**
 * Created by hiephn on 2014/10/02.
 */
define(['durandal/app','jquery','plugins/router', 'chartjs', '../master', 'resource'],function (app, $, router, Chart, master, resource) {
    var LEARNING_COURSE_STAT = app.conf.BACK_URL + "/learning-course/statistic",
        statisticTemplate = {
            "learntItems":0,
            "learningItems":0,
            "remainingItems":0,
            "points":0,
            "pointsToGo":0,
            "difficultWords":{
                "Item1":0.0
            }
        }, that = null,
        ChartJs = Chart.noConflict();

    var VM = {
        itemChart: null,
        qualityChart: null,
        difficultChart: null,
        learningCourseId: null,
        courseId: null,
        activate: function(courseId) {
            that = this;
            window.statistic = this;
            this.courseId = courseId;
        },
        attached: function() {
            console.log('statistic attached');
            resource.learningCourse({courseId: that.courseId}).get().then(function(json) {
                that.learningCourseId = json.dataList[0].id;
                var $itemChart = $('#itemChart'),
                    $qualityChart = $('#qualityChart'),
                    $difficultChart = $('#difficultChart');

                if ($itemChart[0]) {
                    that.itemChart = new ChartJs($itemChart[0].getContext('2d'))
                        .Pie([], {});
                    that.qualityChart = new ChartJs($qualityChart[0].getContext('2d'))
                        .Pie([], {});
                    that.difficultChart = new ChartJs($difficultChart[0].getContext('2d'))
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
                    that.loadStatistic();
                }
            });
        },
        detached: function() {
        },
        loadStatistic: function() {
            app.http.get({
                url: LEARNING_COURSE_STAT,
                data: {
                    id: that.learningCourseId
                }, success: function (json) {
                    // Update Item chart
                    that.itemChart.removeData();
                    that.itemChart.addData({
                        value: json.learntItems,
                        color: "#5cb85c",
                        highlight: "#419641",
                        label: "Learned items"
                    });
                    that.itemChart.addData({
                        value: json.learningItems,
                        color: "#428bca",
                        highlight: "#2d6ca2",
                        label: "Learning items"
                    });
                    that.itemChart.addData({
                        value: json.remainingItems,
                        color: "#f0ad4e",
                        highlight: "#eb9316",
                        label: "Remaining items"
                    });

                    // Update quality chart
                    that.qualityChart.removeData();
                    that.qualityChart.addData({
                        value: json.points,
                        color: "#5cb85c",
                        highlight: "#419641",
                        label: "Points"
                    });
                    that.qualityChart.addData({
                        value: json.pointsToGo,
                        color: "#f0ad4e",
                        highlight: "#eb9316",
                        label: "Points to go"
                    });

                    // Update difficult chart
                    for (var i = 0; i < that.difficultChart.datasets[0].bars.length; ++i) {
                        that.difficultChart.removeData();
                    }
                    that.difficultChart.addData([10], 'dummy');
                    json.difficultWords.forEach(function(difficultWord) {
                        that.difficultChart.addData([difficultWord.value], difficultWord.text);
                    });
                    that.difficultChart.removeData();
                    that.difficultChart.datasets[0].bars.sort(function (a, b) {
                        return a.value < b.value;
                    });
                }
            });
        }
    };

    return VM;
});
