/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app','jquery', 'shell','plugins/router', '../index'],function (app, $, shell, router, settings) {
    var VM = {
        activate: function() {
            settings.module('email');
        },
        attached: function() {
        },
        compositionComplete: function() {
        },
        detached: function() {
        },
        login: function() {
        }
    };
    return VM;
});
