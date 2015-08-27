/**
 * Created by hiephn on 2014/10/31.
 */
define(['jquery', 'durandal/app'], function($, app) {
    var pagingTemplate = {
            numberOfElements: 10,
            pageNumber: 0,
            pageSize: 10,
            totalElements: 11,
            totalPages: 2,
            dataList:[]
    },
        that = null;

    function Paging(rawPaging) {
        this.rawPaging = rawPaging;
        that = this;
        this.pageNumber = app.koo();
        this.pageSize = app.koo();
        this.totalElements = app.koo();
        this.totalPages = app.koo();
        this.range = app.koc(function() {
            return app.kou.range(1, that.totalPages);
        });
        this.nextPage = app.koc(function() {
            var next = that.pageNumber() + 1;
            return next >= that.totalPages() ?
                null :
                next;
        });
        this.prevPage = app.koc(function() {
            var prev = that.pageNumber() - 1;
            return prev < 0 ?
                null :
                prev;
        });

        this.load(rawPaging);
    }

    $.extend(Paging.prototype, {
        load: function(rawPaging) {
            if (rawPaging) {
                this.rawPaging = rawPaging;
                this.pageNumber(rawPaging.pageNumber);
                this.pageSize(rawPaging.pageSize);
                this.totalElements(rawPaging.totalElements);
                this.totalPages(rawPaging.totalPages);
            }
        }
    });

    return Paging;
});
