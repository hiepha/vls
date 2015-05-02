/**
 * Created by thongvh on 2014/10/09.
 */
define(['durandal/app','jquery','plugins/router', '../index', 'paging', 'resource', 'notifies'],function (app, $, router, master, Paging, resource, notifies) {
    var that = null;

    var VM = {
        /* INCORRECT ANSWER */
        incorrectMechanisms: app.koa([{
            text: "Rest of course items",
            value: 0
        },{
            text: "Rest of level items",
            value: 1
//        },{
//            text: "Some items from course",
//            value: 2
//        },{
//            text: "Some items from level",
//            value: 3
        },{
            text: "Input manually",
            value: 4
        }]),
        selectedIncorrectMechanismValue: app.koo(),

        /* QUESTION TYPE */
        types: app.koa([{
            text: 'Multiple choice',
            value: 0
        },{
            text: 'User input',
            value: 1
        }]),
        selectedTypeValue: app.koo(),

        /* QUESTION TYPE */
        answerTypes: app.koa(['WORD','MEANING']),

        /* USER INPUT */
        rawAnswers: app.koa([{
            answer: null
        }]),

        courseId: null,
        kou: app.ko.utils,

        activate: function(courseId) {
            that = this;
            window.question = this;
            window.ko = app.ko;
            this.courseId = courseId;

            // Load Level
            resource.level({courseId: courseId}).get(that.levels).then(function() {
                that.levels.unshift({
                    id: -1,
                    name: "All levels",
                    description: null
                });
            });
            this.loadItem();
        },
        ///////////////// LEVEL /////////////////
        levels: app.koa([]),
        ///////////////// FILTER /////////////////
        filter: app.koMap.fromJS({
            levelId: -1,
            filterKey: null,
            pageSize: 5
        }),
        resetFilter: function() {
            that.filter.levelId(-1);
            that.filter.filterKey(null);
            that.filter.pageSize(5);
            that.loadItem(0);
        },
        ///////////////// ITEM /////////////////
        items: app.koa([]),
        selectedItem: resource.item().oTemplate(),
        selectedItemPtr: null,
        itemPaging: new Paging(),
        loadItem: function(page) {
            resource.item({
                courseId: this.courseId,
                levelId: this.filter.levelId(),
                filterKey: this.filter.filterKey() ? this.filter.filterKey() : '',
                pageSize: this.filter.pageSize(),
                page: page ? page : 0
            }).get(that.items, that.addItemHelperAttr).then(function(json) {
                that.itemPaging.load(json);
            });
        },
        addItemHelperAttr: function(rawItem) {
            rawItem.lazyItem = true;
            rawItem.questions = [];
            rawItem.collapse = true;
            return rawItem;
        },
        toggle: function(item) {
            if (item.lazyItem()) {
                item.lazyItem(false);
                this.loadQuestion(item);
            }
            item.collapse(!item.collapse());
        },
        selectItem: function(item) {
            // manually select necessary attribute
            this.selectedItem.id(item.id());
            this.selectedItem.name(item.name());
            this.selectedItem.meaning(item.meaning());
            this.selectedItemPtr = item;
        },
        ///////////////// QUESTION /////////////////
        selectedQuestion: resource.question().oTemplate(),
        selectedQuestionPtr: null,
        newQuestion: resource.question().oTemplate(),
        needIncorrectAnswerInputted: app.koo(false),
        loadQuestion: function(item) {
            resource.question({itemId: item.id()}).get(item.questions, that.addQuestionHelperAttr);
        },
        addQuestionHelperAttr: function(rawQuestion) {
            rawQuestion.needIncorrectAnswerInputted = false;
            var toJson = JSON.parse(rawQuestion.incorrectAnswerRaw);
            if (!toJson) {
                rawQuestion.incorrectAnswerArray = [{answer:''}];
            } else {
                rawQuestion.incorrectAnswerArray = toJson.map(function(answer) {
                    return {answer: answer};
                });
            }
            return rawQuestion;
        },
        selectQuestion: function(item, question) {
            this.selectItem(item);

            if (question) {
                this.selectedQuestionPtr = question;
                app.koMap.fromJS(question, {}, this.selectedQuestion);
            }
        },
        selectNewQuestion: function(item) {
            this.selectItem(item);
            resource.question().oTemplate(that.newQuestion);
        },
        delQuestion: function() {
            resource.question({id: that.selectedQuestion.id()}).delete().then(function() {
                that.loadQuestion(that.selectedItemPtr);
                app.hideModals();
                notifies.success('Question deleted', that.selectedQuestion.name());
            });
        },
        addQuestion: function(form) {
            if (that.validateForm(form, that.newQuestion)) {
                var incorrectAnswerRaw = this.fromKoaToJsonString(this.newQuestion.incorrectAnswerArray);
                resource.question({
                    text: that.newQuestion.text(),
                    type: that.newQuestion.type.value(),
                    answerType: that.newQuestion.answerType(),
                    correctAnswerItemId: that.selectedItemPtr.id(),
                    incorrectAnswerType: that.newQuestion.incorrectAnswerType.value(),
                    incorrectAnswerNum: that.newQuestion.incorrectAnswerNum(),
                    incorrectAnswerRaw: incorrectAnswerRaw
//                    incorrectAnswerItemIds: ,
                }).post().then(function (json) {
                    if (!that.selectedItemPtr.lazyItem()) {
                        that.loadQuestion(that.selectedItemPtr);
                    }
                    app.hideModals();
                    notifies.success('Question created', json.text);
                });
            }
        },
        updateQuestion: function(form) {
            if (that.validateForm(form, that.selectedQuestion)) {
                var incorrectAnswerRaw = this.fromKoaToJsonString(this.selectedQuestion.incorrectAnswerArray);
                resource.question({
                    id: that.selectedQuestion.id(),
                    text: that.selectedQuestion.text(),
                    type: that.selectedQuestion.type.value(),
                    answerType: that.selectedQuestion.answerType(),
                    correctAnswerItemId: that.selectedItemPtr.id(),
                    incorrectAnswerType: that.selectedQuestion.incorrectAnswerType.value(),
                    incorrectAnswerNum: that.selectedQuestion.incorrectAnswerNum(),
                    incorrectAnswerRaw: incorrectAnswerRaw
//                    incorrectAnswerItemIds: ,
                }).put(that.selectedQuestionPtr, that.addQuestionHelperAttr).then(function (json) {
                    app.hideModals();
                    notifies.success('Question updated', json.text);
                });
            }
        },
        validateForm: function(form, question) {
            if ($(form).valid()) {
                if (question.incorrectAnswerType.value() == 4) {
                    // Incorrect answer inputted manually
                    if (that.extractInputtedIncorrectAnswers(question.incorrectAnswerArray).length > 0) {
                        question.needIncorrectAnswerInputted(false);
                        return true;
                    } else {
                        question.needIncorrectAnswerInputted(true);
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        },
        fromKoaToJsonString: function(incorrectAnswerArray) {
            var incorrectAnswers = that.extractInputtedIncorrectAnswers(incorrectAnswerArray);
            return JSON.stringify(incorrectAnswers);
        },
        extractInputtedIncorrectAnswers: function(incorrectAnswerArray) { // [{answer:'abc'},{answer:'def'}].map ['abc','def']
            var pureJsIncorrectAnswerArray = app.koMap.toJS(incorrectAnswerArray);
            return pureJsIncorrectAnswerArray.filter(function(incorrectAnswer) {
                return incorrectAnswer.answer && incorrectAnswer.answer.length > 0;
            }).map(function(incorrectAnswer) {
                return incorrectAnswer.answer;
            });
        },
        addRawAnswer: function(incorrectAnswerArray) {
            incorrectAnswerArray.push(app.koMap.fromJS({answer:''}));
        },
        removeRawAnswer: function(incorrectAnswerArray, answer) {
            incorrectAnswerArray.remove(answer);
        }
    };
    return VM;
});