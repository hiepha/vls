/**
 * Created by Dell on 10/13/2014.
 */
define(['durandal/app', 'jquery', 'plugins/router', '../index'], function (app, $, router, master) {
    var FRIEND_URL = app.conf.BACK_URL + '/user/friend/friend-list';
    var self = null;
    var VM = {
        friends: app.koa(),
        isCurUser: app.koo(master.isCurUser()),
        username: app.koo(),
        activate: function (username) {
            self = this;
            this.username(username);
            this.loadFriends();
        },
        attached: function () {
        },
        compositionComplete: function () {
        },
        detached: function () {
        },
        loadFriends: function () {
            self.friends.removeAll();
            app.http.get({
                url: FRIEND_URL,
                data: {
                    userName: self.username()
                },
                success: function (json) {
                    app.koMap.fromJS(json.dataList, {}, self.friends);
                }
            });
        }
    };
    return VM;
});
