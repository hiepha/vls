/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app', 'jquery', 'shell', 'plugins/router', 'user', 'confModals', 'notifies'], function (app, $, shell, router, user, modal, notify) {
    var USER_URL = app.conf.BACK_URL + '/user',
        that = null,
        preUser = {
            name: user.name(),
            firstName: user.firstName(),
            lastName: user.lastName(),
            email: user.email(),
            bio: user.bio()
        };
    var VM = {
        user: user,
        file: app.koo(),
        avatar: app.koo(),
//        avaFileName: app.koo(),
//        avaRealName: app.koo(''),
//        avaRealNameSubscribed: false,
        isAvaChanged: app.koo(false),
        isEdited: app.koo(false),

        resetVM: function () {
            that.file(null);
            that.avatar(user.avatar());
        },

        canReuseForRoute: function() {
            return true;
        },
        activate: function () {
            that = this;
            this.avatar(user.avatar());
//            if (!this.avaRealNameSubscribed) {
//                this.avaRealNameSubscribed = true;
//                this.avaFileName.subscribe(function () {
//                    if (that.avaFileName()) {
//                        that.avaRealName(that.avaFileName().substr(that.avaFileName().lastIndexOf('\\') + 1));
//                    } else {
//                        that.avaRealName('');
//                    }
//                })
//            }
        },
        canDeactivate: function () {
            var link = '#' + router.activeInstruction().fragment;
            var isEdited = (user.bio() != preUser.bio
                || user.name() != preUser.name
                || user.firstName() != preUser.firstName
                || user.lastName() != preUser.lastName
                || user.email() != preUser.email);
            if (isEdited || that.isAvaChanged()) {
                modal.confirm('You have not save your changes, do you want to continue?', function () {
                    user.bio(preUser.bio);
                    user.name(preUser.name);
                    user.firstName(preUser.firstName);
                    user.lastName(preUser.lastName);
                    user.email(preUser.email);
                    that.isAvaChanged(false);
                    that.resetVM();
                    router.navigate(link);
                });
                return false;
            }
            return true;
        },
        attached: function () {
        },
        compositionComplete: function () {
        },
        detached: function () {
            this.resetVM();
        },
        uploadAva: function () {
            var data = new FormData();
            data.append('file', that.file());
            data.append('name', that.file().name);
            app.http.post({
                url: USER_URL + '/image',
                data: data,
                processData: false,
                contentType: false,
                success: function (json) {
                    notify.success('', 'Your profile has been updated.');
                    user.fromJson(json);
                    that.resetVM();
                    that.isAvaChanged(false);
                }
            });
        },
        readURL: function (elem) {
            if (elem.files && elem.files[0]) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    that.avatar(e.target.result);
                }
                reader.readAsDataURL(elem.files[0]);
                that.file(elem.files[0]);
                that.isAvaChanged(true);
            }
        },
        saveProfile: function () {
            app.http.put({
                url: USER_URL,
                data: {
                    name: user.name(),
                    email: user.email(),
                    bio: user.bio(),
                    firstName: user.firstName(),
                    lastName: user.lastName(),
                    birthday: user.birthday(),
                    gender: user.gender()
                },
                success: function (json) {
                    user.fromJson(json);
                    preUser.bio = user.bio();
                    preUser.name = user.name();
                    preUser.firstName = user.firstName();
                    preUser.lastName = user.lastName();
                    preUser.email = user.email();
                    notify.success('', 'Your profile has been updated.');
                }
            });
        }
    };
    return VM;
})
;
