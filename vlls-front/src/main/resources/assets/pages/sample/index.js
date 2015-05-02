/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app','jquery', 'shell','plugins/router'],function (app, $, shell, router) {
    var IMAGE_URL = app.conf.BACK_URL + "/item/image";
    var VM = {
        uploadUrl: IMAGE_URL,
        fileName: app.koo(),
        ajaxFormOption: {
            xhrFields: {
                withCredentials: true
            },
            success: function(json) {
                alert(json.source);
            },
            complete: function() {
                alert("complete");
            },
            error: function(jqHXR) {

            }
        },
        activate: function() {
        },
        attached: function() {
        },
        compositionComplete: function() {
        },
        detached: function() {
        },
        login: function() {
        },
        submitForm: function($element) {
            app.http.post({
                url: IMAGE_URL,
                data: new FormData($element),
                processData: false,
                contentType: false
            });
        }
    };
    return VM;
});
