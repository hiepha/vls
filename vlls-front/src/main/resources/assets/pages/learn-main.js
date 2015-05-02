/**
 * Created by hiephn on 2014/08/26.
 */
requirejs.config({
    paths: {
        'text': '../plugins/requirejs-text/text',
        'durandal': '../plugins/durandal/js',
        'plugins': '../plugins/durandal/js/plugins',
        'transitions': '../plugins/durandal/js/transitions',
        'knockout': '../plugins/knockout.js/knockout',
        'knockout-mapping': '../plugins/knockout-mapping/build/output/knockout.mapping-latest',
        'bootstrap': '../plugins/bootstrap/dist/js/bootstrap.min',
        'jquery': '../plugins/jquery/jquery.min',
        'jquery-form': '../plugins/jquery-form/jquery.form',
        'validation': '../plugins/jquery-validation/dist/jquery.validate.min',
        'select2': '../plugins/select2/select2.min',
        'ko-ext': 'knockout-ext',
        'modals': 'modals',
        'http': 'http',
        'user': 'user',
        'conf': 'conf',
        'jplayer': '../plugins/jplayer/jquery.jplayer/jquery.jplayer',
        'knob': '../plugins/jquery-knob/js/jquery.knob',
        'learn-turn': 'learn-turn',
        'chartjs': '../plugins/chartjs/Chart.min',
        'starRating': '../plugins/bootstrap-star-rating/js/star-rating',
        'resource': 'resource',
        'notify': '../plugins/metro-ui-css/js/metro-notify',
        'notifies': 'notifies',
        'rating': '../plugins/metro-ui-css/js/metro-rating',
        'paging': 'paging',
        'confModals': 'confModals'
    },
    shim: {
        'http': 'modals',
        'bootstrap': {
            deps: ['jquery'],
            exports: 'jQuery'
        },
        'chartjs': {
            exports: 'Chart'
        },
        'starRating': {
            deps: ['bootstrap']
        },
        'ko-ext': {
            deps: ['starRating']
        },
        'rating': {
            deps: ['bootstrap']
        },
        'notify': {
            deps: ['bootstrap']
        },
        'notifies': {
            deps: ['notify']
        }
    }
});

define(['durandal/system', 'durandal/app', 'http', 'conf', 'knockout', 'jquery', 'knockout-mapping', 'modals', 'learn-turn', 'ko-ext', 'bootstrap', 'validation'], function (system, app, http, conf, ko, $, koMap, modals, LearnTurn) {

    system.debug(true);

    app.title = 'Visual Language Learning';

    app.configurePlugins({
        router: true,
        dialog: true
    });

    $.extend(app, {
        ko: ko,
        koo: ko.observable,
        koa: ko.observableArray,
        koc: ko.computed,
        kou: ko.utils,
        koMap: koMap,
        http: http,
        conf: conf,
        info: modals.info.bind(null),
        hideModals: modals.hideModals.bind(null),
        cache: {}
    });

    var LEARN_QUIZ_TURN_URL = app.conf.BACK_URL + "/learning-item/turn";
    window.app = app;

    app.start().then(function () {
        try {
            var hash = window.location.hash.substr(1);
            app.cache.isRevise = hash.indexOf('revise') >= 0;
            hash = hash.replace('revise/', '');
            var ids = hash.split('/');
            app.cache.learningCourseId = ids[0];
            if (ids.length == 2) {
                app.cache.learningLevelId = ids[1];
            } else {
                app.cache.learningLevelId = -1;
            }

            app.http.get({
                url: LEARN_QUIZ_TURN_URL,
                data: {
                    learningCourseId: app.cache.learningCourseId,
                    learningLevelId: app.cache.learningLevelId,
                    isRevise: app.cache.isRevise
                },
                success: function (json) {
                    app.cache.learnTurn = new LearnTurn(json);
                    window.learnTurn = app.cache.learnTurn;
                    // Need some time out to slow user down
                    setTimeout(function () {
                        app.setRoot('learn-shell');
                    }, 0);
                },
                error: function (jqXHR) {
                    $('.message').html("ERROR");
                    $('.fa').attr('class', 'fa fa-frown-o fa-4x');
                    $('.caption').html("Details:<br/>" + JSON.parse(jqXHR.responseText).message);
                }
            });
        } catch (err) {
            console.error(err);
            $('.message').html("INVALID REQUEST");
            $('.fa').attr('class', 'fa fa-frown-o fa-4x');
            $('.caption').html("Please go back and retry your request.");
        }
    });
});