/**
 * Created by hiephn on 2014/08/26.
 */
require(['knockout', 'http', 'conf', 'resource'], function (ko, http, conf, resource) {
    ko.bindingHandlers.uniform = {
        init: function (el) {
            var $el = $(el);
            $el.uniform();
        }
    };
    ko.bindingHandlers.validate = {
        init: function (el, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var $elem = $(el),
                options = valueAccessor();
            $elem.validate(options);
        }
    };
    ko.bindingHandlers.select2 = {
        init: function (el, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var $elem = $(el),
                options = valueAccessor();
            $elem.select2(options);
        },
        update: function (el, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var $elem = $(el),
                options = valueAccessor();
            $elem.select2(options);
        }
    };
    ko.bindingHandlers.select2Array = {
        init: function (el, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var $elem = $(el),
                options = valueAccessor();
            options.data = ko.utils.unwrapObservable(options.data);
            $elem.select2(options);
        },
        update: function (el, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var $elem = $(el),
                options = valueAccessor();
            options.data = ko.utils.unwrapObservable(options.data);
            $elem.select2(options);
        }
    };
    ko.bindingHandlers.ajaxForm = {
        init: function (el, valueAccessor) {
            var $el = $(el);
            $el.ajaxForm(valueAccessor());
        }
    };
    ko.bindingHandlers.fadeVisible = {
        init: function (element, valueAccessor) {
            // Initially set the element to be instantly visible/hidden depending on the value
            var value = valueAccessor();
            $(element).toggle(ko.unwrap(value)); // Use "unwrapObservable" so we can handle values that may or may not be observable
        },
        update: function (element, valueAccessor) {
            // Whenever the value subsequently changes, slowly fade the element in or out
            var value = valueAccessor();
            ko.unwrap(value) ? $(element).fadeIn() : $(element).fadeOut();
        }
    };
    ko.bindingHandlers.popover = {
        init: function (element, valueAccessor) {
            var $elem = $(element),
                options = valueAccessor();
            $elem.popover();
        }
    };
    ko.bindingHandlers.knob = {
        init: function (element, valueAccessor) {
            var $elem = $(element),
                unwrappedOptions = ko.unwrap(valueAccessor());
            $elem.knob({
                'readOnly': true,
                'width': 120,
                'height': 120,
                'draw': function () {
                    $(this.i).val(this.cv + '%');
                }
            });
        },
        update: function (element, valueAccessor) {
            var $elem = $(element),
                unwrappedOptions = ko.unwrap(valueAccessor());
            console.log(unwrappedOptions);
            $elem.trigger('change');
        }
    };
    ko.bindingHandlers.rating = {
        init: function (element, valueAccessor) {
            var $elem = $(element),
                unwrappedOptions = ko.unwrap(valueAccessor());
            $elem.rating({
                'min': 0,
                'size': 'xs',
                'step': 1,
                'showCaption': false,
                'showClear': false,
                'hoverEnabled': false,
                'disabled': true
            });
        }
    };
    var COURSE_RATING_URL = conf.BACK_URL + "/course/rating";
    ko.bindingHandlers.courseRating = {
        init: function (element, valueAccessor) {
            var $elem = $(element);
            var unwrappedOptions = ko.unwrap(valueAccessor());
            var rating = unwrappedOptions.rating;
            $elem.rating({
                'min': 0,
                'size': 'xs',
                'step': 1,
                'showCaption': false,
                'showClear': false,
                'hoverEnabled': false,
                'disabled': true
            });
        },
        update: function (element, valueAccessor) {
            var $elem = $(element);
            if ($elem.val() != "") {
                var unwrappedOptions = ko.unwrap(valueAccessor());
                var rating = unwrappedOptions.rating;
                $elem.rating('update', rating);
            }
        }
    };
    ko.bindingHandlers.rating = {
        init: function (element, valueAccessor) {
            var $elem = $(element);
            var unwrappedOptions = ko.unwrap(valueAccessor());
            var rating = unwrappedOptions.rating;
            $elem.rating({
                'min': 0,
                'size': 'xs',
                'step': 1,
                'showCaption': false,
                'showClear': false,
                'hoverEnabled': true,
                'disabled': false
            });
        },
        update: function (element, valueAccessor) {
            var $elem = $(element);
            if ($elem.val() != "") {
                var unwrappedOptions = ko.unwrap(valueAccessor());
                var rating = unwrappedOptions.rating;
                $elem.rating('update', rating);
            }
        }
    };
    ko.bindingHandlers.reviseTimeFormatter = {
        init: function (element, valueAccessor) {
            var $elem = $(element);
            var unwrappedOptions = ko.unwrap(valueAccessor());
            var timeLeft = unwrappedOptions.timeLeft;
            if (timeLeft > 0) {
                if (timeLeft < 61) {
                    timeLeft = timeLeft.toString().split('.')[0] + 's';
                } else if (timeLeft < 3600) {
                    timeLeft = (timeLeft / 60).toString().split('.')[0] + 'm';
                } else if (timeLeft < (3600 * 24)) {
                    var hour = timeLeft / 3600;
                    var minutes = (timeLeft % 3600) / 60;
                    timeLeft = hour.toString().split('.')[0] + 'h';
                    if (minutes > 0) {
                        timeLeft += ' ' + minutes.toString().split('.')[0] + 'm';
                    }
                } else if (timeLeft >= (3600 * 24)) {
                    var day = timeLeft / (3600 * 24);
                    var hour = (timeLeft % (3600 * 24)) / 3600;
                    var minutes = ((timeLeft % (3600 * 24)) % 3600) / 60;
                    timeLeft = day.toString().split('.')[0] + 'd';
                    if (hour > 0) {
                        timeLeft += ' ' + hour.toString().split('.')[0] + 'h';
                        if (minutes > 0) {
                            timeLeft += ' ' + minutes.toString().split('.')[0] + 'm';
                        }
                    }
                }
                $elem.html('Revise in ' + timeLeft);
//                $elem.html('<label class="label label-success" style="font-size: 100%">Revise in ' + timeLeft + '</label>');
            } else {
                $elem.html('Revise now');
//                $elem.html('<button class="btn btn-info"><i class="glyphicon glyphicon-pencil" style="margin-right: 5px;"></i>Revise</button>');
            }
        },
        update: function (element, valueAccessor) {
            var $elem = $(element);
            var unwrappedOptions = ko.unwrap(valueAccessor());
            var timeLeft = unwrappedOptions.timeLeft;
            if (timeLeft > 0) {
                if (timeLeft < 61) {
                    timeLeft = timeLeft.toString().split('.')[0] + 's';
                } else if (timeLeft < 3600) {
                    timeLeft = (timeLeft / 60).toString().split('.')[0] + 'm';
                } else if (timeLeft < (3600 * 24)) {
                    var hour = timeLeft / 3600;
                    var minutes = (timeLeft % 3600) / 60;
                    timeLeft = hour.toString().split('.')[0] + 'h';
                    if (minutes > 0) {
                        timeLeft += ' ' + minutes.toString().split('.')[0] + 'm';
                    }
                } else if (timeLeft >= (3600 * 24)) {
                    var day = timeLeft / (3600 * 24);
                    var hour = (timeLeft % (3600 * 24)) / 3600;
                    var minutes = ((timeLeft % (3600 * 24)) % 3600) / 60;
                    timeLeft = day.toString().split('.')[0] + 'd';
                    if (hour > 0) {
                        timeLeft += ' ' + hour.toString().split('.')[0] + 'h';
                        if (minutes > 0) {
                            timeLeft += ' ' + minutes.toString().split('.')[0] + 'm';
                        }
                    }
                }
                $elem.html('Revise in ' + timeLeft);
//                $elem.html('<label class="label label-success" style="font-size: 100%">Revise in ' + timeLeft + '</label>');
            } else {
                $elem.html('Revise now');
//                $elem.html('<button class="btn btn-info"><i class="glyphicon glyphicon-pencil" style="margin-right: 5px;"></i>Revise</button>');
            }
        }
    };
    var jplayerMedia;
    ko.bindingHandlers.jplayer = {
        init: function (element, valueAccessor) {
            var $elem = $(element);
            var unwrappedOptions = ko.unwrap(valueAccessor());
            var audio = unwrappedOptions.audio;
            var $player = $($elem.find('#jplayer')[0]);
            var $play = $($elem.find('#jplayer-play')[0]);
            var $stop = $($elem.find('#jplayer-stop')[0]);
            if (audio.indexOf('.mp3') > -1) {
                jplayerMedia = {
                    mp3: audio
                }
            } else if (audio.indexOf('.m4a') > -1) {
                jplayerMedia = {
                    m4a: audio
                }
            } else if (audio.indexOf('.ogg') > -1) {
                jplayerMedia = {
                    ogg: audio
                }
            } else if (audio.indexOf('.oga') > -1) {
                jplayerMedia = {
                    oga: audio
                }
            } else if (audio.indexOf('.wav') > -1) {
                jplayerMedia = {
                    wav: audio
                }
            }
            $player.jPlayer({
                ready: function () {
                    $player.jPlayer("setMedia", jplayerMedia);
                    $player.jPlayer("play");
                },
                ended: function () {
//                    $player.jPlayer("stop");
                    $player.jPlayer("clearMedia");
                    $player.jPlayer("setMedia", jplayerMedia);
                },
                supplied: "m4a, mp3, ogg, wav",
//                swfPath: 'assets/plugins/jplayer/jquery.jplayer/Jplayer.swf',
                preload: 'auto',
                cssSelectorAncestor: "#left-controls",
                cssSelector: {
                    play: ".fa-volume-up",
                    stop: ".fa-stop"
                },
                stateClass: {
                    playing: "audio-playing"
                }
            });
            $play.click(function () {
                $player.jPlayer('play');
            });
            $stop.click(function () {
                $player.jPlayer('pause', 0);
            });
        },
        update: function (element, valueAccessor) {
            var $elem = $(element);
            var unwrappedOptions = ko.unwrap(valueAccessor());
            var audio = unwrappedOptions.audio;
            var $player = $($elem.find('#jplayer')[0]);
            if (audio.indexOf('.mp3') > -1) {
                jplayerMedia = {
                    mp3: audio
                }
            } else if (audio.indexOf('.m4a') > -1) {
                jplayerMedia = {
                    m4a: audio
                }
            } else if (audio.indexOf('.ogg') > -1) {
                jplayerMedia = {
                    ogg: audio
                }
            } else if (audio.indexOf('.oga') > -1) {
                jplayerMedia = {
                    oga: audio
                }
            } else if (audio.indexOf('.wav') > -1) {
                jplayerMedia = {
                    wav: audio
                }
            }
            $player.jPlayer("setMedia", jplayerMedia);
            $player.jPlayer("play");
        }
    };
    ko.bindingHandlers.tooltip = {
        init: function (element) {
            $(element).tooltip();
        }
    }

    ko.bindingHandlers.wordDetail = {
        init: function (element, valueAccessor) {
        },
        update: function (element, valueAccessor) {
            var $elem = $(element);
            var unwrappedOptions = ko.unwrap(valueAccessor());
            var itemId = unwrappedOptions.itemId;
            if (itemId != -1) {
                var $word = $($(element).find('#detail-word'));
                var $mean = $($(element).find('#detail-meaning'));
                var $type = $($(element).find('#detail-type'));
                var $pron = $($(element).find('#detail-pronun'));
                var $audio = $($(element).find('#detail-audio'));
                resource.item({
                    id: itemId
                }).get().then(function (data) {
                    $word.text(data.dataList[0].name);
                    $mean.text(data.dataList[0].meaning);
                    var type = data.dataList[0].type;
                    if (type != null && type != '') {
                        $type.text(type);
                        $type.closest('.modal-header').removeClass('hide');
                    } else {
                        if (!$type.closest('.modal-header').hasClass('hide')) {
                            $type.closest('.modal-header').addClass('hide');
                        }
                    }
                    var pronun = data.dataList[0].pronun;
                    if (pronun != null && pronun != '') {
                        $pron.text(pronun);
                        $pron.closest('.modal-header').removeClass('hide');
                    } else {
                        if (!$pron.closest('.modal-header').hasClass('hide')) {
                            $pron.closest('.modal-header').addClass('hide');
                        }
                    }
                    var audio = data.dataList[0].audio;
                    if (audio != null && audio != '') {
                        $audio.html('<audio controls style="width:250px;"><source type="audio/mpeg" src="' + audio + '" ></audio>')
                        $audio.removeClass('hide');
                    } else {
                        if (!$audio.hasClass('hide')) {
                            $audio.addClass('hide');
                        }
                    }
                });
            }
        }
    }
})
;