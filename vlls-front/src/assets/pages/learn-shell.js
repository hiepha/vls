/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app', 'knockout', 'plugins/router', 'user'], function (app, ko, router, user) {
    var USER_INFO_URL = app.conf.BACK_URL + '/security/user';

    var VM = {
        user: user,

        activate: function() {
            // Revise user session
            app.http.get({
                url: USER_INFO_URL,
                error: function() {
                    // Do nothing
                },
                success: function(json) {
                    user.fromJson(json);
                }
            });

            router.map([
                { route: [':lcId(/:llId)','revise/:lcId(/:llId)'], title:'Learn', moduleId: 'learning/index', nav: true }
            ]).buildNavigationModel();

            return router.activate();
        },
        attached: function() {
        }
    };

    return VM;
});
