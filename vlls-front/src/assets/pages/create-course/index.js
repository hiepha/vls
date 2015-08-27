/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app','jquery', 'shell','plugins/router', 'user', 'resource', 'validation'],function (app, $, shell, router, user, resource) {
    var that = null;

    var VM = {
        languages: app.koa([]),
        langTeach: app.koo(-1),
        langSpeak: app.koo(-1),

        categories: app.koa([]),
        category: app.koo(-1),

        courseName: app.koo(null),

        activate: function() {
            that = this;
            this.loadLang();
            this.loadCate();
        },
        deactivate: function() {
            this.langTeach(-1);
            this.langSpeak(-1);
            this.category(-1);
            this.languages.removeAll();
            this.categories.removeAll();
            this.courseName(null);
        },
        loadLang: function() {
            resource.language().get(that.languages).then(function() {
                var firstOption = resource.language().oTemplate();
                firstOption.id(-1);
                firstOption.name('Choose');
                that.languages.unshift(firstOption);
                that.langTeach(-1);
                that.langSpeak(-1);
            });
        },
        loadCate: function() {
            resource.category().get(that.categories).then(function() {
                var firstOption = resource.language().oTemplate();
                firstOption.id(-1);
                firstOption.name('Choose');
                that.categories.unshift(firstOption);
                that.category(-1);
            });
        },
        create: function() {
            resource.course({
                name: this.courseName(),
                categoryId: this.category(),
                langTeachId: this.langTeach(),
                langSpeakId: this.langSpeak()
            }).post().then(function(json) {
                router.navigate('#edit-course/' + json.id);
            });
        }
    };
    return VM;
});
