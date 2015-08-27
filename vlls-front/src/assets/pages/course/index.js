/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app', 'jquery', 'shell', 'plugins/router', 'resource', 'paging', 'user', 'select2'], function (app, $, shell, router, resource, Paging, user) {
    var that = null;

    var VM = {
        user: user,
        category: resource.category().oTemplate(),
        courses: app.koa(),
        categories: app.koa(),

        languages: app.koa([]),
        langSpeakId: app.koo(),
        langSpeakSubscribed: false,
        langLearnId: app.koo(),
        langLearnSubscribed: false,
        searchKeyDisplay: app.koo(),
        searchKeySubmitted: app.koo(),

        paging: new Paging(),

        filter: app.koo('Popular'),

        activate: function () {
            window.course = this;
            that = this;
            that.loadCourse();
            that.loadLang();
            that.loadCate();
        },
        attached: function () {
        },
        compositionComplete: function () {
        },
        detached: function () {
        },
        cate: function (item) {
            app.koMap.fromJS(item, {}, this.category);
            this.loadCourse();
        },
        clearCate: function () {
            resource.category().oTemplate(this.category);
            this.loadCourse();
        },
        loadCourse: function (page) {
            resource.course({
                langTeachId: (this.langLearnId() && this.langLearnId() != '') ? this.langLearnId() : -1,
                langSpeakId: (this.langSpeakId() && this.langSpeakId() != '') ? this.langSpeakId() : -1,
                categoryId: this.category.id(),
                name: this.searchKeySubmitted(),
                page: page,
                pageSize: 10,
                filter: this.filter()
            }).get(that.courses, function (rawData) {
                rawData.avatar = (rawData.avatar == null || rawData.avatar == '')
                    ? 'assets/img/course-0.png'
                    : rawData.avatar;
                return rawData;
            }).then(function (data) {
                that.paging.load(data);
            });
        },
        loadLang: function () {
            resource.language().get().then(function (data) {
                that.languages.removeAll();
                data.dataList.forEach(function (lang) {
                    that.languages.push({
                        id: lang.id,
                        text: lang.name
                    });
                });
            });
        },
        loadCate: function () {
            resource.category().get(that.categories).then(function () {
                that.categories.unshift({
                    id: app.koo(-1),
                    name: app.koo('All')
                })
            });
        },
        formatFlag: function (state) {
            if (!state.id) {
                return state.text; // optgroup
            } else {
                return "<img class='flag-circle' src='assets/img/lang-flag/" + state.text + ".png'/>" + state.text;
            }
        },
        search: function () {
            this.searchKeySubmitted(this.searchKeyDisplay());
            this.loadCourse();
        },
        selectFilter: function (filterType) {
            that.filter(filterType);
            that.loadCourse();
        }
    };
    return VM;
});
