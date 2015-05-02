/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app','jquery', 'shell','plugins/router', 'user', 'validation'],function (app, $, shell, router, user) {
    var USER_URL = app.conf.BACK_URL + "/user";

    var VM = {
        username: app.koo(),
        email: app.koo(),
        password: app.koo(),

        activate: function() {
        },
        deactivate: function() {
            this.username(null);
            this.email(null);
            this.password(null);
        },
        attached: function() {
        },
        compositionComplete: function() {
        },
        detached: function() {
        },
        register: function() {
            app.http.post({
                url: USER_URL,
                data: {
                    name: this.username(),
                    email: this.email(),
                    password: this.password()
                },
                success: function(json) {
                    router.navigate("#login");
                }
            });
        }
    };
    return VM;
});
