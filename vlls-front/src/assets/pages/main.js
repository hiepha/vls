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
        'validation': '../plugins/jquery-validation/dist/jquery.validate.min',
        'select2': '../plugins/select2/select2.min',
        'jplayer': '../plugins/jplayer/jquery.jplayer/jquery.jplayer',
        'knob': '../plugins/jquery-knob/dist/jquery.knob.min',
        'ko-ext': 'knockout-ext',
        'modals': 'modals',
        'http': 'http',
        'user': 'user',
        'conf': 'conf',
        'paging': 'paging',
        'resource': 'resource',
        'chartjs': '../plugins/chartjs/Chart.min',
        'starRating': '../plugins/bootstrap-star-rating/js/star-rating.min',
        'notify': '../plugins/metro-ui-css/js/metro-notify',
        'notifies': 'notifies',
        'rating': '../plugins/metro-ui-css/js/metro-rating',
        'confModals': 'confModals',
        'sockjs': '../plugins/sockjs-client/dist/sockjs',
        'stomp': '../plugins/stomp-websocket/lib/stomp'
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

define(['jquery', 'bootstrap', 'durandal/system', 'durandal/app', 'http', 'conf', 'knockout', 'knockout-mapping', 'modals', 'ko-ext'], function ($, bootstrap, system, app, http, conf, ko, koMap, modals) {

    system.debug(true);

    app.title = 'Visual Language Learning';

    app.configurePlugins({
        router: true
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

    app.start().then(function () {
        app.setRoot('shell');
    });
});