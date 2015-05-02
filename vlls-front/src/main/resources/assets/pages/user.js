
define(['durandal/app', 'knockout'],function(app, ko) {
    var USER_INFO_URL = app.conf.BACK_URL + '/security/user',
        USER_LOGOUT_URL = app.conf.BACK_URL + '/security/logout',
        AUTH_URL = app.conf.BACK_URL + '/security/auth';

    var koo = ko.observable,
        user = {
            id: koo(),
            name: koo(),
            firstName: koo(),
            lastName: koo(),
            point: koo(),
            birthday: koo(),
            bio: koo(),
            avatar: koo(),
            email: koo(),
            gender: koo(),
            logoutCallbacks: {},
            loginCallbacks: {},

            fromJson: function(json) {
                user.id(json.id);
                user.name(json.name);
                user.firstName(json.firstName);
                user.lastName(json.lastName);
                user.point(json.point);
                user.birthday(json.birthday);
                user.bio(json.bio);
                user.avatar(json.avatar);
                user.email(json.email);
                user.gender(json.gender);
            },
            reset: function() {
                user.id(null);
                user.name(null);
                user.firstName(null);
                user.lastName(null);
                user.point(null);
                user.birthday(null);
                user.bio(null);
                user.avatar(null);
                user.email(null);
                user.gender(null);
            },
            isLogin: function() {
                return user.id() != null;
            },
            login: function(username, password) {
                var encrypted = btoa(username + ':' + password),
                    authHeader = 'Basic ' + encrypted;
                return app.http.post({
                    url: AUTH_URL,
                    headers: {
                        Authorization: authHeader
                    }
                }).then(function(json) {
                    user.fromJson(json);
                    for (var i in user.loginCallbacks) {
                        user.loginCallbacks[i]();
                    }
                });
            },
            logout: function() {
                return app.http.post({
                    url: USER_LOGOUT_URL
                }).then(function() {
                    user.reset();
                    for (var i in user.logoutCallbacks) {
                        user.logoutCallbacks[i]();
                    }
                });
            },
            revise: function() {
                return app.http.get({
                    url: USER_INFO_URL,
                    error: function() {
                        // Do nothing
                    }
                }).then(function(json) {
                    user.fromJson(json);
                });
            },
            afterLogout: function(key, callback) {
                if (typeof callback == 'function') {
                    user.logoutCallbacks[key] = callback;
                }
            },
            afterLogin: function(key, callback) {
                if (typeof callback == 'function') {
                    user.loginCallbacks[key] = callback;
                }
            }
        };

    return user;
});