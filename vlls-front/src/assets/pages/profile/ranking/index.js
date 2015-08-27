/**
 * Created by Dell on 10/13/2014.
 */
define(['durandal/app', 'jquery', 'plugins/router', '../index'], function (app, $, router, master) {
    var USER_URL = app.conf.BACK_URL + '/user/'
    var self = null;
    var VM = {
        ranking: app.koa(),
        type: app.koo('ranking'),
        username: app.koo(),
        isCurUser: app.koo(master.isCurUser()),
        activate: function (username) {
            self = this;
            self.username(username);
            this.getRanking('ranking');
        },
        attached: function () {
        },
        compositionComplete: function () {
        },
        detached: function () {
        },
        getRanking: function (type) {
            self.ranking.removeAll();
            self.type(type);
            app.http.get({
                url: USER_URL + type,
                data: {
                    pageSize: 50
                },
                success: function (json) {
                    if (type === 'friendRanking') {
                        app.koMap.fromJS(json, {}, self.ranking);
                    } else {
                        app.koMap.fromJS(json.dataList, {}, self.ranking);
                    }
                }
            });
        }
    };
    return VM;
});
