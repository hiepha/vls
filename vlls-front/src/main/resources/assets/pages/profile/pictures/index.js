/**
 * Created by Dell on 10/13/2014.
 */
define(['durandal/app', 'jquery', 'plugins/router', 'resource', 'paging', 'user', 'notifies', 'jplayer'], function (app, $, router, resource, Paging, user, notifies) {
    var PICTURE_URL = app.conf.BACK_URL + '/picture/',
        LIKED_PICTURES_URL = app.conf.BACK_URL + '/liked_picture';
    var self = null;
    var VM = {
        pictures: app.koa(),
        type: app.koo(),
        username: app.koo(),
        isCurUser: app.koo(false),
        user: user,
        paging: new Paging(),
        item: resource.item().oTemplate(),
        activate: function (username) {
            self = this;
            self.isCurUser(user.name() === username);
            self.username(username);
            this.loadPictures('created', 0);
        },
        attached: function () {
        },
        compositionComplete: function () {
        },
        detached: function () {
        },
        loadPictures: function (type, page) {
            self.pictures.removeAll();
            self.type(type);
            resource.picture({
                username: self.username(),
                pageSize: 5,
                page: page
            }, {
                url: PICTURE_URL + type
            }).get().then(function (data) {
                self.paging.load(data);
                data.dataList.forEach(function (picture) {
                    if (picture.htmlFormat != '') {
                        picture.htmlFormat = $.parseJSON(picture.htmlFormat);
                    } else {
                        picture.htmlFormat = {
                            text: '',
                            align: 'left'
                        };
                    }
                });
                app.koMap.fromJS(data.dataList, {}, self.pictures);
            });
        },
        likePicture: function ($data) {
            app.http.post({
                url: LIKED_PICTURES_URL,
                data: {
                    pictureId: $data.id
                },
                success: function (data) {
                    $data.numOfLikes($data.numOfLikes() + 1);
                    $data.isLiked(true)
                }
            })
        },
        setItem: function (id) {
            resource.item({
                id: id
            }).get().then(function (data) {
                app.koMap.fromJS(data.dataList[0], {}, self.item);
                return true;
            });
        },
        deletePicture: function (pictureId, item) {
            resource.picture({
                id: pictureId
            }).delete().then(function () {
                self.loadPictures(self.type(), self.paging.pageNumber());
                notifies.warning('', 'A picture for "' + item + '" had been deleted.');
            });
        }
    };
    return VM;
});
