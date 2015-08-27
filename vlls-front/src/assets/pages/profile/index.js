/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app', 'jquery', 'shell', 'plugins/router', 'user', 'resource'], function (app, $, shell, router, user, resource) {
    var self = null;
    var FRIEND_URL = resource.user().url + '/friend';
    var childRouter = router.createChildRouter()
        .makeRelative({
            moduleId: 'profile',
            fromParent: true,
            dynamicHash: ':username'
        }).map([
            { route: ['', 'pictures'], moduleId: 'pictures/index', title: 'Pictures', type: 'intro', nav: true, icon: 'fa fa-image', hash: '#pictures', hashId: 'pictures' },
            { route: 'courses', moduleId: 'courses/index', title: 'Courses', type: 'intro', nav: true, icon: 'fa fa-th-large', hash: '#courses', hashId: 'courses' },
            { route: 'friends', moduleId: 'friends/index', title: 'Friends', type: 'intro', nav: true, icon: 'fa fa-users', hash: '#friends', hashId: 'friends' },
            { route: 'ranking', moduleId: 'ranking/index', title: 'Leaderboard', type: 'intro', nav: true, icon: 'fa fa-trophy', hash: '#ranking', hashId: 'ranking' },
            { route: 'message(/:userId)', moduleId: 'message/index', title: 'Message', type: 'intro', nav: true, icon: 'fa fa-envelope-o', hash: '#message(/:userId)', hashId: 'message' }
        ]).buildNavigationModel();
    var VM = {
        router: childRouter,
        profile: resource.user().oTemplate(),
        isCurUser: app.koo(false),
        point: app.koo(0),
        totalItems: app.koo(0),
        totalFriends: app.koo(0),
        user: user,
        excludeRoutes: app.koa([]),
        canReuseForRoute: function() {
            return true;
        },
        activate: function (username) {
            self = this;
            this.isCurUser((user.name() === username));
            if (!this.isCurUser()) {
                this.excludeRoutes.push('Message');
            }
            this.loadProfile(username);
            this.loadProfileStat(username);
        },
        deactivate: function() {
            self.excludeRoutes.removeAll();
        },
        attached: function () {
        },
        compositionComplete: function () {
        },
        detached: function () {
        },
        loadProfile: function (username) {
            resource.user({name: username}).get(self.profile).then(function (data) {
                self.point(data.dataList[0].point);
            });
        },
        sendFriendRequest: function () {
            resource.user({friendTwoId: self.profile.id()}, {url: FRIEND_URL}).post().then(function () {
                self.loadProfile(self.profile.name());
            });
        },
        cancelFriend: function () {
            resource.user({friendId: self.profile.id()}, {url: FRIEND_URL}).delete().then(function () {
                self.loadProfile(self.profile.name());
            });
        },
        acceptFriendRequest: function () {
            resource.user({friendOneId: self.profile.id(), isFriend: true}, {url: FRIEND_URL}).put().then(function () {
                self.loadProfile(self.profile.name());
            });
        },
        loadProfileStat: function (username) {
            app.http.get({
                url: resource.user().url + '/profile/stat',
                data: {
                    username: username
                },
                success: function (data) {
                    self.totalFriends(data.totalFriends);
                    self.totalItems(data.totalItems);
                }
            })
        },
        sendMessage: function() {
            router.navigate("#profile/" + user.name() + "/message/" + self.profile.id());
        }
    };
    return VM;
});
