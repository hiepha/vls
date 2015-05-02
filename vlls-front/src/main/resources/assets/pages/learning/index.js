/**
 * Created by Hoang Thong on 01/10/2014.
 */
define(['durandal/app', 'jquery', 'plugins/router', 'user', 'resource', 'paging', 'notifies', 'jplayer'], function (app, $, router, user, resource, Paging, notify) {
    var IMAGE_URL = app.conf.BACK_URL + "/item/image",
        LIKED_PICTURES_URL = app.conf.BACK_URL + '/liked_picture',
        PICTURES_URL = app.conf.BACK_URL + '/picture',
        LEARN_QUIZ_TURN_URL = app.conf.BACK_URL + "/learning-item/turn",
        SET_PICTURE_URL = app.conf.BACK_URL + "/learning-item/set_picture",
        that = null, self = null;
    var pictureTmp = {
        id: 0,
        source: '',
        htmlFormat: '',
        uploaderId: 0,
        uploaderName: '',
        uploaderAvatar: '',
        itemWord: '',
        itemMeaning: '',
        numOfLikes: 0,
        isLiked: false
    };
    var VM = {
        uploadImage: app.koo(''),
        repoPictures: {
            sources: app.koa(),
            nextPage: app.koo(null)
        },
        pictures: app.koa(),
        itemList: app.koa(),
        textPic: app.koo(),
        path: app.koo(),
        right: app.koo(false),
        left: app.koo(false),
        center: app.koo(false),
        paging: new Paging(),
        carouselCurIndex: app.koo(0),
        disableSubmit: app.koo(false),

        newPictureId: app.koo(-1),

        file: app.koo(),

        curItem: app.koMap.fromJS({
            id: -1,
            itemId: -1,
            word: "Loading",
            meaning: "Loading",
            pronun: "Loading",
            audio: null,
            // Helper attributes from here
            lastAnswer: null,
            isLearned: null,
            pictureId: 0
        }),
        curQuestion: app.koMap.fromJS({
            text: "Loading",
            type: 0,
            answer: null,
            options: [],
            // Helper attributes from here
            correctItemPtr: null,
            userAnswer: null,
            isSubmitted: null,
            isUserCorrect: null,
            point: null,
            bonus: null,
            penalty: null
        }),

        useImgMemHelper: app.koo(false),
        useSearchImg: app.koo(true),
        useUploadNewImg: app.koo(false),
        useOldRepoImg: app.koo(false),

        gameOver: app.koo(false),
        totalAccuracy: app.koo(),
        totalPoint: app.koo(),
        quizMode: app.koo(false),
        learnTurn: null,
        isPause: app.koo(false),
        displayAdvancedOptions: app.koo(),
        learningLevelId: null,

        activate: function (levelId) {
            that = this;
            self = this;
            this.learningLevelId = levelId;
            this.learnTurn = app.cache.learnTurn;
            window.learn = this;
        },

        attached: function (view, parent) {
            that = this;
            window.quiz = this;

            this.next();
        },
        detached: function () {
        },
        toggleImgMemHelper: function () {
            this.useImgMemHelper(!this.useImgMemHelper());
            self.loadPicturesOfItem();
        },
        pause: function () {
            this.isPause(!this.isPause());
        },
        num: 0,
        next: function () {
            self.pictures.removeAll();
            self.useImgMemHelper(false);
            app.koMap.fromJS(pictureTmp, {}, self.selectedPicture);
            if (this.learnTurn.isGameOver()) {
                this.gameOver(true);
                var result = this.learnTurn.result;
                this.totalPoint(result.point);
                var rate = result.right / (result.wrong + result.right) * 10000;
                rate = Math.round(rate) / 100;
                this.totalAccuracy(rate);
            } else {
                this.learnTurn.startLearnSessionIfNeeded(app.cache.isRevise);

                if (this.learnTurn.curLearnItems.length > 0) {
                    // In progress of a item session
                    var learnItem = this.learnTurn.curLearnItems.pop();
                    this.curItem.id(learnItem.id);
                    this.curItem.itemId(learnItem.itemId);
                    this.curItem.word(learnItem.name);
                    this.curItem.meaning(learnItem.meaning);
                    this.curItem.pronun(learnItem.pronun);
                    this.curItem.audio(learnItem.audio);
                    this.curItem.lastAnswer(learnItem.lastAnswer);
                    this.curItem.pictureId(learnItem.pictureId);
                    this.quizMode(false);
                    if (learnItem.pictureId > 0) {
                        self.loadPicture(learnItem.pictureId);
                    } else {
                        self.checkSelectedPicture(learnItem.id);
                    }
                } else {
                    // In progress of a questions session
                    this.learnTurn.curQuestionPtr = this.learnTurn.curLearnQuestions.pop();
                    console.log('---- display question ' + ++this.num);
                    app.koMap.fromJS(this.learnTurn.curQuestionPtr, {}, this.curQuestion);
                    this.quizMode(true);
                }
            }
        },
        chooseOption: function (option) {
            this.curQuestion.userAnswer(option);
            this.submitAnswer();
        },
        submitAnswer: function () {
            if (!this.curQuestion.isSubmitted()) {
                this.learnTurn.submitAnswer(this.curQuestion.userAnswer());
                this.curQuestion.isUserCorrect(this.learnTurn.curQuestionPtr.isUserCorrect);
                this.curQuestion.point(this.learnTurn.curQuestionPtr.point);
                this.curQuestion.bonus(this.learnTurn.curQuestionPtr.bonus);
                this.curQuestion.penalty(this.learnTurn.curQuestionPtr.penalty);
                this.curQuestion.isSubmitted(true);
            }
        },
        submitTurn: function () {
            var itemResults = [];
            that.learnTurn.learnItemArray.forEach(function (learnItem) {
                itemResults.push({
                    id: learnItem.id,
                    right: learnItem.right,
                    wrong: learnItem.wrong,
                    learnResultMap: learnItem.result.map
                });
            });
            app.http.post({
                url: LEARN_QUIZ_TURN_URL,
                data: {
                    itemResult: JSON.stringify(itemResults)
                },
                success: function () {
                    that.exit();
                }
            }).fail(function() {
                that.disableSubmit(true);
            });
        },

        alignRight: function () {
            this.left(false);
            this.center(false);
            this.right(!this.right());
        },
        alignLeft: function () {
            this.right(false);
            this.center(false);
            this.left(!this.left());
        },
        alignCenter: function () {
            this.left(false);
            this.right(false);
            this.center(!this.center());
        },
        exit: function () {
            if (window.history.length == 1) {
                if (window.onclose) {
                    window.onclose();
                }
                window.close();
            } else {
                window.history.back();
            }
        },
        loadPicturesOfItem: function (page, pageSize, pictureId, curIndex, element) {
            if (typeof(page) == 'undefined' || page == '') {
                page = 0;
            }
            if (typeof(pageSize) == 'undefined' || pageSize == '') {
                pageSize = 5;
            }
            if (typeof(pictureId) == 'undefined' || pictureId == '') {
                pictureId = self.newPictureId();
            }
            if (typeof(curIndex) == 'undefined' || curIndex == '') {
                curIndex = self.carouselCurIndex();
            }
            resource.picture({
                itemId: self.curItem.itemId(),
                pictureId: pictureId,
                page: page,
                pageSize: pageSize
            }, {
                url: PICTURES_URL + '/all-pictures'
            }).get().then(function (data) {
                self.paging.load(data);
                // Process raw data
                if (data.dataList.length > 0) {
                    data.dataList.forEach(function (picture) {
                        if (picture.htmlFormat != '') {
                            picture.htmlFormat = $.parseJSON(picture.htmlFormat);
                        } else {
                            picture.htmlFormat = {text: '', align: 'left'};
                        }
                    });
                }

                // Load first time
                if (page == 0) {
                    self.pictures.removeAll();
                    app.koMap.fromJS(data.dataList, {}, self.pictures);
                    self.carouselCurIndex(0);
                }
                // Click show more
                else {
                    if (pictureId > -1) {
                        data.dataList.shift();
                    }
                    var news = app.koa();
                    app.koMap.fromJS(data.dataList, {}, news);
                    news().forEach(function (newPic) {
                        self.pictures.push(newPic);
                    });
                    $(element).closest('.active').removeClass('active');
                    self.carouselCurIndex(curIndex);
                }
            });
        },
        likePicture: function (picture) {
            resource.picture({
                pictureId: picture.id
            }, {
                url: LIKED_PICTURES_URL
            }).post().then(function () {
                picture.numOfLikes(picture.numOfLikes() + 1);
                picture.isLiked(true)
            });
        },
        loadAllPictures: function (page, type) {
            if (isNaN(page)) {
                page = 0;
            }
            resource.picture({
                itemId: self.curItem.itemId(),
                page: page,
                pageSize: 30
            }, {
                url: PICTURES_URL + '/all-sources'
            }).get().then(function (data) {
                var next = data.pageNumber + 1;
                self.repoPictures.nextPage(next >= data.totalPages ? null : next);
                if (type == '' || typeof(type) == 'undefined') {
                    self.repoPictures.sources.removeAll();
                }
                data.dataList.forEach(function (picture) {
                    self.repoPictures.sources.push(picture.source);
                });
            });
        },
        cancelSetupImage: function () {
            self.uploadImage('');
            self.loadAllPictures();
            return true;
        },
        readUrl: function (fileInput) {
            if (fileInput.files && fileInput.files[0]) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    self.uploadImage(e.target.result);
                }
                reader.readAsDataURL(fileInput.files[0]);
                self.file(fileInput.files[0]);
            }
        },
        fileUpload: function () {
            var data = new FormData();
            data.append('file', self.file());
            data.append('itemId', self.curItem.itemId());
            data.append('name', self.file().name);
            var htmlFormat = {
                text: self.textPic(),
                align: (self.center) ? 'center' : (self.right()) ? 'right' : 'left'
            };
            data.append('htmlFormat', JSON.stringify(htmlFormat));
            app.http.post({
                url: IMAGE_URL,
                data: data,
                processData: false,
                contentType: false,
                success: function (data) {
                    self.newPictureId(data.id);
                    self.loadPicturesOfItem(0, 5, data.id);
                    self.loadAllPictures();
                    $('#chooseImage').modal('hide');
                    self.showSearchImg();
                }
            });
        },
        showSearchImg: function () {
            self.resetElements();
            self.loadAllPictures();
            self.useOldRepoImg(false);
            self.useUploadNewImg(false);
            setTimeout(function () {
                self.useSearchImg(true);
            }, 500);
        },
        showSkipSearch: function () {
            self.resetElements();
            self.useSearchImg(false);
            self.useOldRepoImg(false);
            setTimeout(function () {
                self.useUploadNewImg(true);
            }, 500);
        },
        showUseOldImg: function (imgSource) {
            self.resetElements();
            self.uploadImage(imgSource);
            self.useSearchImg(false);
            self.useUploadNewImg(false);
            setTimeout(function () {
                self.useOldRepoImg(true);
            }, 500);
        },
        resetElements: function () {
            self.uploadImage('');
            self.file(null);
            self.textPic('');
            self.alignLeft();
        },
        createImgFromOld: function () {
            var htmlFormat = {
                text: self.textPic(),
                align: (self.center) ? 'center' : (self.right()) ? 'right' : 'left'
            };
            resource.picture({
                source: self.uploadImage(),
                itemId: self.curItem.itemId(),
                format: JSON.stringify(htmlFormat)
            }, {
                url: PICTURES_URL
            }).post().then(function (data) {
                self.newPictureId(data.id);
                self.loadPicturesOfItem(0, 5, data.id);
                self.loadAllPictures();
                $('#chooseImage').modal('hide');
                self.showSearchImg();
            });

        },

        /**
         * SELECT PICTURE
         */
        selectedPicture: app.koMap.fromJS(pictureTmp),
        setPicture: function (pictureId, type) {
            var data;
            if (type === 'select') {
                data = {
                    pictureId: pictureId,
                    learningItemId: self.curItem.id()
                };
            } else {
                data = {
                    learningItemId: self.curItem.id()
                }
            }
            resource.picture(data, {
                url: SET_PICTURE_URL
            }).get().then(function (data) {
                if (data.id == 0) {
                    app.koMap.fromJS(pictureTmp, {}, self.selectedPicture);
                    self.curItem.pictureId(0);
                    notify.warning('', 'Your picture has been unset!');
                } else {
                    app.koMap.fromJS(data, {}, self.selectedPicture);
                    if (data.htmlFormat != '') {
                        self.selectedPicture.htmlFormat($.parseJSON(data.htmlFormat));
                    }
                    self.curItem.pictureId(data.id);
                    $('#chooseImage').modal('hide');
                    self.useImgMemHelper(false);
                    notify.success('', 'You have chosen a picture!');
                }
            });
        },
        loadPicture: function (pictureId) {
            resource.picture({
                pictureId: pictureId
            }, {
                url: PICTURES_URL + '/getOne'
            }).get().then(function (data) {
                app.koMap.fromJS(data, {}, self.selectedPicture);
                self.selectedPicture.htmlFormat($.parseJSON(data.htmlFormat));
            });
        },
        checkSelectedPicture: function (learningItemId) {
            resource.picture({
                learningItemId: learningItemId
            }, {
                url: PICTURES_URL + '/selected-picture'
            }).get().then(function (data) {
                if (data.totalElements > 0) {
                    app.koMap.fromJS(data.dataList[0], {}, self.selectedPicture);
                    self.selectedPicture.htmlFormat($.parseJSON(data.htmlFormat));
                }
            });
        }
    };
    return VM;
});