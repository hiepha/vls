/**
 * Created by thongvh on 2014/11/03.
 */
define(['durandal/app', 'jquery', 'plugins/router', '../master', 'knob'], function (app, $, router, master) {
    var LEARNING_COURSE_DIFF = app.conf.BACK_URL + "/learning-course/difficult";
    var self;
    var learningItemTmp = {
        id: -1,
        itemId: -1,
        name: '',
        meaning: '',
        pronun: '',
        progress: 0,
        lastUpdate: '',
        audio: '',
        type: '',
        levelName: '',
        reviseTimeLeft: 0,
        pictureId: -1
    };
    var VM = {
        learningCourse: master.learningCourse,
        difficultWords: app.koa(),
        activate: function () {
            self = this;
            self.loadDifficultWords();
        },
        loadDifficultWords: function () {
            app.http.get({
                url: LEARNING_COURSE_DIFF,
                data: {
                    id: self.learningCourse.id()
                },
                success: function (data) {
                    self.difficultWords.removeAll();
                    app.koMap.fromJS(data, {}, self.difficultWords);
                }
            });
        }
    };

    return VM;
});
