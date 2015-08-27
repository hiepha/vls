/**
 * Created by hiephn on 2014/09/29.
 */
define(['durandal/app', 'jquery', 'plugins/router', '../index', 'resource', 'notifies', 'confModals'], function (app, $, router, master, resource, notifies, modal) {
    var self = null;
    var SAVE_IMG_URL = app.conf.BACK_URL + '/course/image';
    var VM = {
        isEdited: app.koo(false),
        isAvaChanged: app.koo(false),
        course: resource.course().oTemplate(),
        languages: app.koa(),
        categories: app.koa(),
        file: app.koo(),
        status: [
            {
                'text': 'Public',
                'value': true
            },
            {
                'text': 'Private',
                'value': false
            }
        ],
        avatar: app.koo('assets/img/course-0.png'),
        canReuseForRoute: function() {
            return true;
        },
        activate: function (id) {
            self = this;
            resource.language().get(self.languages).then(function() {
                // When languages are loaded, isEdited and isAvaChanged is set to true,
                // they must be set back to false.
                self.isEdited(false);
                self.isAvaChanged(false);
            });
            resource.category().get(self.categories).then(function() {
                // When categories are loaded, isEdited and isAvaChanged is set to true,
                // they must be set back to false.
                self.isEdited(false);
                self.isAvaChanged(false);
            });
            // Load Course
            resource.course({id: id}).get(self.course).then(function() {
                if (self.course.avatar() == null || self.course.avatar() == '') {
                    self.course.avatar('assets/img/course-0.png');
                }
                // When course is loaded, isEdited and isAvaChanged is set to true,
                // they must be set back to false.
                self.isEdited(false);
                self.isAvaChanged(false);
                console.log("FALSE");
            });
        },
        submitForm: function (form) {
            if ($(form).valid() || typeof(form) == 'undefined') {
                resource.course({
                    id: self.course.id(),
                    name: self.course.name(),
                    isPublic: self.course.isPublic(),
                    categoryId: self.course.categoryId(),
                    langTeachId: self.course.langTeachId(),
                    langSpeakId: self.course.langSpeakId(),
                    description: self.course.description()
                }).put(self.course).then(function (data) {
                    notifies.success('', 'Course ' + data.name + ' has been updated.');
                    app.koMap.fromJS(data, {}, master.course);
                    self.isEdited(false);
                });
            }
        },
        readURL: function (elem) {
            if (elem.files && elem.files[0]) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    self.course.avatar(e.target.result);
                };
                reader.readAsDataURL(elem.files[0]);
                self.file(elem.files[0]);
                self.isAvaChanged(true);
            }
        },
        savePicture: function () {
            var data = new FormData();
            data.append('file', self.file());
            data.append('id', self.course.id());
            data.append('name', self.file().name);
            app.http.post({
                url: SAVE_IMG_URL,
                data: data,
                processData: false,
                contentType: false,
                success: function (data) {
                    app.koMap.fromJS(data, {}, master.course);
                    app.koMap.fromJS(data, {}, self.course);
                    self.resetVM();
                    self.isAvaChanged(false);
                    notifies.success('', 'Course ' + data.name + ' has been updated.');
                }
            });
        },
        canDeactivate: function () {
            var link = '#' + router.activeInstruction().fragment;
            if (self.isEdited() || self.isAvaChanged()) {
                modal.confirm('Do you want to save the changes you make to this course?', function () {
                    self.resetVM();
                    self.isEdited(false);
                    self.isAvaChanged(false);
                    router.navigate(link);
                });
                return false;
            }
            return true;
        },
        setEdited: function () {
            self.isEdited(true);
            console.log("TRUE");
        },
        resetVM: function () {
            self.file(null);
            self.avatar(self.course.avatar());
        }
    };
    return VM;
});