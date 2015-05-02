/**
 * Created by hiephn on 2014/08/26.
 */
define(['durandal/app','jquery','plugins/router','resource', 'validation'],function (app, $, router, resource) {
    var that = null;

    var childRouter = router.createChildRouter()
        .makeRelative({
            moduleId:'edit-course',
            fromParent: true,
            dynamicHash: ':id'
        }).map([
            { route: ['', 'details'], moduleId: 'details/index', title: 'Details', type: 'intro', nav: true, icon: 'fa fa-th', hash: '#details', hashId: 'details' },
            { route: ['levels'], moduleId: 'levels/index', title: 'Levels', type: 'intro', nav: true, icon: 'fa fa-list-alt', hash: '#levels', hashId: 'levels' },
            { route: 'question', moduleId: 'question/index', title: 'Questions', type: 'intro', nav: true, icon: 'fa fa-question-circle', hash: '#question', hashId: 'question' }
        ]).buildNavigationModel();

    var VM = {
        router: childRouter,
        course: resource.course().oTemplate(),

        resetVM: function() {
            resource.course().oTemplate(this.course);
        },

        activate: function(id) {
            that = this;
            resource.course({id: id}).get(that.course);
        },
        deactivate: function() {
        },
        ///////////////// PAGE ALERT /////////////////
        info: app.koo(),
        dismissInfo: function() {
            that.info(null);
        }
    };
    return VM;
});
