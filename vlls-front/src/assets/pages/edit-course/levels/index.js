/**
 * Created by hiephn on 2014/09/29.
 */
define(['durandal/app', 'jquery', 'plugins/router', '../index', 'resource', 'notifies', 'select2'], function (app, $, router, master, resource, notifies) {
    var DICT_URL = app.conf.BACK_URL + "/dictionary/",
        that = null,
        file = null;

    var VM = {
        activate: function (courseId) {
            window.edit = this;
            window.resource = resource;
            that = this;
            // Load course
            resource.course({id: courseId}).get(that.course).then(function () {
                // Load Levels
                resource.level({courseId: courseId}).
                    get(that.levels, that.addLevelHelperAttr).then(function () {
                        // Set dictionary
                        var dictType = that.course.langTeachCode() + '-' + that.course.langSpeakCode();
                        that.checkAndSetDict(dictType);
                    });
            });
        },
        ///////////////// COURSE /////////////////
        course: resource.course().oTemplate(),
        ///////////////// LEVEL /////////////////
        levels: app.koa([]),
        selectedLevel: resource.level().oTemplate(),
        selectedLevelPtr: null,
        addLevelHelperAttr: function (rawLevel) {
            rawLevel.lazyItem = true;
            rawLevel.items = [];
            rawLevel.recommendItems = [];
            rawLevel.collapse = true;
            rawLevel.newItem = resource.item().template();
            rawLevel.isSubmitting = false; // use for validate form input
            rawLevel.dictWordId = null;
            rawLevel.dictOptions = null;
            return rawLevel;
        },
        toggle: function (level) {
            if (level.lazyItem()) {
                level.lazyItem(false);
                this.loadItem(level);
            }
            level.collapse(!level.collapse());
        },
        selectLevel: function (level) {
            if (level) {
                this.selectedLevelPtr = level;
                app.koMap.fromJS(level, {}, this.selectedLevel);
            } else {
                resource.level().oTemplate(this.selectedLevel);
            }
        },
        saveLevel: function () {
            if (this.selectedLevel.id() < 0) { // create new
                resource.level({
                    courseId: this.course.id(),
                    name: this.selectedLevel.name(),
                    description: this.selectedLevel.description()
                }).post().then(function (json) {
                    notifies.success('Level created', json.name);
                    app.hideModals();
                    that.addLevelHelperAttr(json);
                    var newLevel = app.koMap.fromJS(json);
                    that.levels.push(newLevel);
                    that.selectedLevelPtr = null;
                    resource.level().oTemplate(that.selectedLevel);
                });
            } else { // update
                resource.level({
                    id: this.selectedLevel.id(),
                    name: this.selectedLevel.name(),
                    description: this.selectedLevel.description()
                }).put().then(function (json) {
                    notifies.success('Level updated', json.name);
                    app.hideModals();
                    resource.level().oTemplate(that.selectedLevel);
                    if (that.selectedLevelPtr) {
                        that.selectedLevelPtr.name(json.name);
                        that.selectedLevelPtr.description(json.description);
                        that.selectedLevelPtr = null;
                    }
                });
            }
        },
        delLevel: function () {
            resource.level({id: that.selectedLevel.id()}).delete().then(function () {
                // TODO: use soft notification instead of modal
                notifies.success('Level deleted', that.selectedLevel.name());
                app.hideModals();
                that.levels.remove(that.selectedLevelPtr);
                that.selectedLevelPtr = null;
            });
        },
        ///////////////// ITEM /////////////////
        loadItem: function (level) {
            resource.item({levelId: level.id()}).get(level.items, that.addItemHelperAttr);
        },
        addItemHelperAttr: function (rawItem) {
            rawItem.isEditing = false;
            rawItem.isSubmitting = false; // use for form validation
            rawItem.audioName = null;
            rawItem.hasAudio = false;
            return rawItem;
        },
        addItem: function (curLevel) {
            curLevel.isSubmitting(true);
            if (curLevel.newItem.name()
                && curLevel.newItem.meaning()) {
                // Construct a form data
                var formData = new FormData();
                formData.append('levelId', curLevel.id());
                formData.append('name', curLevel.newItem.name());
                formData.append('meaning', curLevel.newItem.meaning());
                formData.append('audioFile', (curLevel.newItem.audioFile == undefined) ? null : curLevel.newItem.audioFile);
                formData.append('audioName', curLevel.newItem.audioName() ? curLevel.newItem.audioName() : '');
                formData.append('pronun', curLevel.newItem.pronun() ? curLevel.newItem.pronun() : '');
                formData.append('type', curLevel.newItem.type() ? curLevel.newItem.type() : '');

                resource.item(null, {
                    data: formData,
                    processData: false,
                    contentType: false
                }).post().then(function () {
                    curLevel.isSubmitting(false);
                    curLevel.newItem.name('');
                    curLevel.newItem.meaning('');
                    curLevel.newItem.audio('');
                    curLevel.newItem.audioName('');
                    curLevel.newItem.pronun('');
                    curLevel.newItem.type('');
                    curLevel.newItem.hasAudio(false);
                    curLevel.newItem.isSubmitting(false);
                    // Release the file if needed
                    curLevel.newItem.audioFile = null;

                    that.loadItem(curLevel);
                });
            }
        },
        updateItem: function (curlLevel, item) {
            item.isSubmitting(true);
            if (item.name() && item.meaning()) {
                // Construct a form data
                var formData = new FormData();
                formData.append('id', item.id());
                formData.append('name', item.name());
                formData.append('meaning', item.meaning());
                formData.append('isAudioRemoved', (item.isAudioRemoved == undefined) ? false : item.isAudioRemoved);
                formData.append('audioFile', (item.audioFile == undefined) ? null : item.audioFile);
                formData.append('audioName', item.audioName() ? item.audioName() : '');
                formData.append('pronun', item.pronun() ? item.pronun() : '');
                formData.append('type', item.type() ? item.type() : '');

                resource.item(null, {
                    url: resource.item().url + '/update',
                    data: formData,
                    processData: false,
                    contentType: false
                }).post().then(function (json) {
                    that.addItemHelperAttr(json);
                    app.koMap.fromJS(json, {}, item);
                    // Release the file if held
                    item.audioFile = null;
                });
            }
        },
        delItem: function (item, curLevel) {
            resource.item({id: item.id()}).delete().then(function () {
                curLevel.items.remove(item);
            });
        },
        resetItem: function (item) {
            // reset observable
            app.koMap.fromJS(resource.item().template(), {}, item);
            // release the file if any
            item.audioFile = null;
        },
        startEditItem: function (item) {
            item.isEditing(true);
            if (!item.origin) {
                item.origin = resource.item().template();
            }
            // Store origin data
            item.origin.id = item.id();
            item.origin.name = item.name();
            item.origin.meaning = item.meaning();
            item.origin.type = item.type();
            item.origin.audio = item.audio();
            item.origin.pronun = item.pronun();
            item.origin.isAudioRemoved = item.isAudioRemoved;
        },
        cancelEditItem: function (item) {
            // Restore origin data
            item.id(item.origin.id);
            item.name(item.origin.name);
            item.meaning(item.origin.meaning);
            item.type(item.origin.type);
            item.audio(item.origin.audio);
            item.pronun(item.origin.pronun);
            item.isAudioRemoved = item.origin.isAudioRemoved;

            item.isEditing(false);
        },
        ///////////////// DICTIONARY /////////////////
        dictType: app.koo(null),
        checkAndSetDict: function (dictType) {
            app.http.get({
                url: DICT_URL + dictType,
                data: {
                    id: 1
                }, success: function () {
                    that.dictType(dictType);
                }, '404': function () {
                    // Dictionary is not available
                }
            })
        },
        dictOptions: function () {
            return {
                placeholder: "Search from Dictionary",
                minimumInputLength: 1,
                allowClear: true,
                ajax: {
                    url: DICT_URL + that.dictType() + '/list',
                    quietMillis: 500,
                    dataType: 'json',
                    data: function (term, page) { // page is the one-based page number tracked by Select2
                        return {
                            key: term, //search term
                            pageSize: 10, // page size
                            page: page - 1 // page number
                        };
                    },
                    results: function (data) {
                        var more = false; // do not support paging
                        var resultDatas = [];
                        data.forEach(function (data) {
                            var resultData = {};
                            data.forEach(function (item) {
                                if (!resultData.id) {
                                    resultData.id = item
                                } else {
                                    resultData.text = item;
                                }
                            });
                            resultDatas.push(resultData);
                        });
                        return {results: resultDatas, more: more};
                    }
                },
                //Allow manually entered text in drop down.
                createSearchChoice: function (term, list) {
                    for (var i in list) {
                        if (list[i].text.toLowerCase() == term.toLowerCase()) {
                            return null;
                        }
                    }
                    return {id: term, text: term};
                }
            }
        },
        chooseRecomItem: function (recomItem, level) {
            app.koMap.fromJS(recomItem, {}, level.newItem);
        },
        resetRecomItems: function (level) {
            level.recommendItems.removeAll();
        },
        loadRecomItems: function (level) {
            if (level.dictWordId() && level.dictWordId().length > 0 && !isNaN(level.dictWordId())) {
                app.http.get({
                    url: DICT_URL + that.dictType(),
                    data: {
                        id: level.dictWordId()
                    },
                    success: function (json) {
                        app.koMap.fromJS(json, {}, level.recommendItems);
                    }
                });
            } else {
                level.recommendItems.removeAll();
                // custom select2 search choice
                if (level.dictWordId().length > 0) {
                    level.newItem.name(level.dictWordId());
                }
            }
        },
        ///////////////// ITEM AUDIO /////////////////
        audioChosen: function (item, $element) {
            if ($element.files[0]) {
                item.audioFile = $element.files[0];
                item.audioName(item.audioFile.name);
            }
        },
        deleteAudio: function (item) {
            item.audioFile = null;
            item.audio(null);
            item.isAudioRemoved = true;
        },
        setupAudio: function (id) {
            console.log(id);
        },

        // CSV
        csvHeaders: app.koa(),
        csvData: app.koa(),
        csvLevel: app.koMap.fromJS(resource.level().template()),
        csvURL: app.koo(resource.level().url + '/csv'),
        showCSVModal: function (level) {
            app.koMap.fromJS(level, {}, that.csvLevel);
            return true;
        },
        chooseCSVFile: function (fileInput) {
            that.csvHeaders.removeAll();
            that.csvData.removeAll();
            var data = null;
            file = fileInput.files[0];
            var reader = new FileReader();
            reader.readAsText(file);
            reader.onload = function (event) {
                var csvData = event.target.result;
                that.processData(csvData);
                $('#uploadFile').val(file.name);
                $('#items-preview').css('display', 'block');
            };
        },
        importCSV: function () {
            var data = new FormData();
            data.append("file", file);
//            data.append("id", that.csvLevel.id());
            data.append("id", that.csvLevel.id());
            app.http.post({
                url: that.csvURL(),
                data: data,
                processData: false,
                contentType: false,
                success: function (data) {
                    for (var i = 0; i < that.levels().length; i++) {
                        if (that.levels()[i].id() == that.csvLevel.id()) {
                            that.loadItem(that.levels()[i]);
                            i = that.levels().length;
                        }
                    }
                    that.csvData.removeAll();
                    that.csvHeaders.removeAll();
                    $('#uploadFile').val('');
                    $('#items-preview').css('display', 'none');
                    $('#csvPreviewModal').modal('hide');
                }
            });
        },
        processData: function (allText) {
            var allTextLines = allText.split(/\r\n|\n/);
            var headers = allTextLines[0].split(',');
            headers.forEach(function (header) {
                that.csvHeaders.push(header);
            });
            for (var i = 1; i < allTextLines.length; i++) {
                var data = allTextLines[i].split(',');
                if (data.length == headers.length) {
                    var tarr = [];
                    for (var j = 0; j < headers.length; j++) {
                        tarr.push(data[j]);
                    }
                    that.csvData.push(tarr);
                }
            }
        },
        resetCSV: function () {
            that.csvData.removeAll();
            that.csvHeaders.removeAll();
            $('#uploadFile').val('');
            $('#items-preview').css('display', 'none');
        }
    };
    return VM;
});