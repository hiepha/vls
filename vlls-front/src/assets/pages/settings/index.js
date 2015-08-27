/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app','jquery', 'shell','plugins/router'],function (app, $, shell, router) {
    var childRouter = router.createChildRouter()
        .makeRelative({
            moduleId:'settings',
            fromParent:true
        }).map([
            { route: ['', 'profile'], moduleId: 'profile/index', title: 'Profile', type: 'intro', nav: true, icon: 'fa fa-user', hashId: 'profile' },
            { route: 'password', moduleId: 'password/index', title: 'Password', type: 'intro', nav: true, icon: 'fa fa-lock', hashId: 'password' },
            { route: 'mail', moduleId: 'mail/index', title: 'Email', type: 'intro', nav: true, icon: 'fa fa-envelope-o', hashId: 'mail' },
            { route: 'deactivate', moduleId: 'deactivate/index', title: 'Deactivate', type: 'intro', nav: true, icon: 'fa fa-times-circle-o', hashId: 'deactive' }
        ]).buildNavigationModel();

    var VM = {
        router: childRouter,
        module: app.koo(),
        activate: function() {
            console.log('activating');
        },
        deactivate: function() {
            console.log('deactivating');
        },
        attached: function() {
        },
        compositionComplete: function() {
        },
        detached: function() {
        }
    };
    return VM;
});
