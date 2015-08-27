/**
 * Created by hiephn on 2014/11/09.
 */
define(['jquery', 'http', 'conf', 'durandal/app'], function ($, http, conf, app) {

    /**
     * Resource function definition
     */
    function Resource(url, template) {
        this.rawUrl = url;
        this.rawTemplate = template;
    }

    $.extend(Resource.prototype, {
        init: function (dataOptions, ajaxOptions) {
            var options = {url: this.rawUrl, data: {}};
            $.extend(options.data, dataOptions);
            $.extend(options, ajaxOptions);
            var resourceFunctions = {
                url: this.rawUrl,
                template: $.extend.bind(null, true, {}, this.rawTemplate),
                get: function (observableObject, mapper) {
                    if (!observableObject) {
                        return http.get(options);
                    } else {
                        var $get;
                        if (typeof observableObject == 'function') {
                            $get = http.get(options);
                            $get.then(function (data) {
                                var dataList;
                                if (data instanceof Array) {
                                    dataList = data;
                                } else {
                                    dataList = data.dataList;
                                }
                                if (mapper) {
                                    var rawContentList = dataList.map(mapper);
                                    app.koMap.fromJS(rawContentList, {}, observableObject);
                                } else {
                                    app.koMap.fromJS(dataList, {}, observableObject);
                                }
                            });
                            return $get;
                        } else {
                            $get = http.get(options);
                            $get.then(function (data) {
                                var dataList;
                                if (data instanceof Array) {
                                    dataList = data;
                                } else {
                                    dataList = data.dataList;
                                }
                                if (mapper) {
                                    var rawContent = mapper(dataList[0]);
                                    app.koMap.fromJS(rawContent, {}, observableObject);
                                } else {
                                    app.koMap.fromJS(dataList[0], {}, observableObject);
                                }
                            });
                            return $get;
                        }
                    }
                },
                post: function (observableObject, mapper) {
                    if (!observableObject) {
                        return http.post(options);
                    } else {
                        var $post = http.post(options);
                        $post.then(function (data) {
                            if (mapper) {
                                var rawContent = mapper(data);
                                app.koMap.fromJS(rawContent, {}, observableObject);
                            } else {
                                app.koMap.fromJS(data, {}, observableObject);
                            }
                        });
                        return $post;
                    }
                },
                put: function (observableObject, mapper) {
                    if (!observableObject) {
                        return http.put(options);
                    } else {
                        var $put = http.put(options);
                        $put.then(function (data) {
                            if (mapper) {
                                var rawContent = mapper(data);
                                app.koMap.fromJS(rawContent, {}, observableObject);
                            } else {
                                app.koMap.fromJS(data, {}, observableObject);
                            }
                        });
                        return $put;
                    }
                },
                delete: http.delete.bind(null, options),
                oTemplate: function (template) {
                    if (template) {
                        return app.koMap.fromJS(resourceFunctions.template(), {}, template);
                    } else {
                        return app.koMap.fromJS(resourceFunctions.template());
                    }
                }
            };
            return resourceFunctions;
        }
    });

    /**
     * Template definition
     */
    var COURSE_URL = conf.BACK_URL + "/course",
        COURSE_TEMPLATE = {
            "id": -1,
            "name": "Loading...",
            "description": null,
            "status": null,
            "avatar": 'assets/img/course-0.png',
            "langTeach": null,
            "langTeachId": -1,
            "langTeachCode": null,
            "langSpeak": null,
            "langSpeakId": -1,
            "langSpeakCode": null,
            "numberOfLevel": 0,
            "numberOfStudent": 0,
            "recommend": 0,
            "creatorId": null,
            "creatorName": null,
            "creatorAvatar": null,
            "categoryName": null,
            "categoryId": -1,
            "isLearning": null,
            "isActive": null,
            "isPublic": false,
            "lastUpdate": null,
            "createdDate": null,
            "rating": 0
        },
        course = new Resource(COURSE_URL, COURSE_TEMPLATE);
    var LEVEL_URL = conf.BACK_URL + "/level",
        LEVEL_TEMPLATE = {
            id: -1,
            name: null,
            description: null,
            items: []
        },
        level = new Resource(LEVEL_URL, LEVEL_TEMPLATE);
    var ITEM_URL = conf.BACK_URL + "/item",
        ITEM_TEMPLATE = {
            id: -1,
            name: null,
            meaning: null,
            audio: null,
            audioName: null,
            pronun: null,
            type: null,
            hasAudio: false,
            isEditing: false,
            isSubmitting: false
        },
        item = new Resource(ITEM_URL, ITEM_TEMPLATE);
    var QUESTION_URL = conf.BACK_URL + "/question",
        QUESTION_TEMPLATE = {
            "id": -1,
            "text": null,
            "type": {text: null, value: 0},
            "answerType": null,
            "incorrectAnswerType": {text: null, value: 0},
            "incorrectAnswerNum": null,
            "incorrectAnswerRaw": null,
            "incorrectAnswerArray": [
                {answer: null}
            ],
            "correctAnswerItem": null,
            "incorrectAnswerItems": [],
            "needIncorrectAnswerInputted": false
        },
        question = new Resource(QUESTION_URL, QUESTION_TEMPLATE);
    var LANGUAGE_URL = conf.BACK_URL + "/language",
        LANGUAGE_TEMPLATE = {
            code: "en",
            description: "English Language",
            id: 1,
            name: "English"
        },
        language = new Resource(LANGUAGE_URL, LANGUAGE_TEMPLATE);
    var CATEGORY_URL = conf.BACK_URL + "/category",
        CATEGORY_TEMPLATE = {
            description: "",
            id: -1,
            name: "All"
        },
        category = new Resource(CATEGORY_URL, CATEGORY_TEMPLATE);
    var LEARNING_COURSE_URL = conf.BACK_URL + "/learning-course",
        LEARNING_COURSE_TEMPLATE = {
            id: -1,
            pinStatus: null,
            userName: null,
            lastSync: null,
            lastUpdate: null,
            point: null,
            rating: 0,
            numOfReviseItem: 0,
            totalItems: 0,
            totalLearntItems: 0,
            course: COURSE_TEMPLATE
        },
        learningCourse = new Resource(LEARNING_COURSE_URL, LEARNING_COURSE_TEMPLATE);
    var LEARNING_LEVEL_URL = conf.BACK_URL + "/learning-level",
        LEARNING_LEVEL_TEMPLATE = {
            id: -1,
            progress: null,
            level: LEVEL_TEMPLATE
        },
        learningLevel = new Resource(LEARNING_LEVEL_URL, LEARNING_LEVEL_TEMPLATE);
    var LEARNING_ITEM_URL = conf.BACK_URL + "/learning-item",
        LEARNING_ITEM_TEMPLATE = {
            audio: null,
            id: -1,
            itemId: -1,
            lastUpdate: null,
            levelName: null,
            meaning: null,
            name: null,
            pictureId: -1,
            progress: 0,
            pronun: null,
            reviseTimeLeft: null,
            type: null,
            reviseNo: 0
        },
        learningItem = new Resource(LEARNING_ITEM_URL, LEARNING_ITEM_TEMPLATE);
    var PICTURE_URL = conf.BACK_URL + '/picture',
        PICTURE_TEMPLATE = {
            id: -1,
            source: '',
            htmlFormat: '',
            uploaderId: -1,
            uploaderName: '',
            uploaderAvatar: '',
            itemWord: '',
            itemMeaning: '',
            itemId: -1,
            numOfLikes: 0,
            isLiked: false
        },
        picture = new Resource(PICTURE_URL, PICTURE_TEMPLATE);
    var USER_URL = conf.BACK_URL + '/user',
        USER_TEMPLATE = {
            avatar: null,
            bio: null,
            birthday: null,
            email: null,
            fbAccessToken: null,
            fbId: null,
            firstName: null,
            friendStatus: null,
            gender: null,
            id: -1,
            lastLogin: null,
            lastName: null,
            lastUpdate: null,
            name: null,
            phone: null,
            point: null,
            role: null,
            learningProgress: null
        },
        user = new Resource(USER_URL, USER_TEMPLATE);
    var NOTIFICATION_URL = conf.BACK_URL + '/notification',
        NOTIFICATION_TEMPLATE = {
            "type": null,
            "image": null,
            "header": null,
            "message": null,
            "author": null,
            "learningCourseId": 0
        },
        notification = new Resource(NOTIFICATION_URL, NOTIFICATION_TEMPLATE);
    var CONVERSATION_URL = conf.BACK_URL + '/conversation',
        CONVERSATION_TEMPLATE = {
            "id": null,
            "userId": null,
            "userName": null,
            "userAvatar": null,
            "userOneId": null,
            "userOneName": null,
            "userOneAvatar": null,
            "userTwoId": null,
            "userTwoName": null,
            "userTwoAvatar": null,
            "lastUpdate": null,
            "read": null
        },
        conversation = new Resource(CONVERSATION_URL, CONVERSATION_TEMPLATE);
    var CONVERSATION_REPLY_URL = conf.BACK_URL + '/conversation',
        CONVERSATION_REPLY_TEMPLATE = {
            "id": null,
            "userId": null,
            "userName": null,
            "reply": null,
            "time": null,
            "read": null,
            "conversationId": null,
            "income": null
        },
        conversationReply = new Resource(CONVERSATION_REPLY_URL, CONVERSATION_REPLY_TEMPLATE);

    return {
        course: course.init.bind(course),
        level: level.init.bind(level),
        item: item.init.bind(item),
        question: question.init.bind(question),
        language: language.init.bind(language),
        category: category.init.bind(category),
        learningCourse: learningCourse.init.bind(learningCourse),
        learningLevel: learningLevel.init.bind(learningLevel),
        learningItem: learningItem.init.bind(learningItem),
        picture: picture.init.bind(picture),
        user: user.init.bind(user),
        notification: notification.init.bind(notification),
        conversation: conversation.init.bind(conversation),
        conversationReply: conversationReply.init.bind(conversationReply)
    };
});
