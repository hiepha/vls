/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app','jquery', 'shell','plugins/router', 'user', 'validation'],function (app, $, shell, router, user) {
    var VM = {
        username: app.koo(),
        password: app.koo(),
        hashRedirect: null,

        activate: function() {
            if (router.activeInstruction().queryParams) {
                this.hashRedirect = router.activeInstruction().queryParams.hr;
            }
        },
        deactivate: function() {
            this.username(null);
            this.password(null);
            this.hashRedirect = null;
        },
        attached: function() {
        },
        compositionComplete: function() {
        },
        detached: function() {
        },
        login: function() {
            var that = this;
            user.login(this.username(), this.password()).then(function() {
                if (that.hashRedirect) {
                    router.navigate(that.hashRedirect);
                } else {
                    router.navigate('course');
                }
            });
        }
    };
    return VM;
});
