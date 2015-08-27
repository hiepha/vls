/**
 * Created by Dell on 10/13/2014.
 */
define(['durandal/app', 'conf', 'jquery', 'plugins/router', 'resource', 'paging', 'user', 'notifies', 'sockjs', 'stomp', 'select2'], function (app, conf, $, router, resource, Paging, user, notifies) {
    var that = null,
        CONVERSATION_SOCKET_URL = conf.BACK_URL + "/conversation-socket",
        SUBSCRIBE_PATH = "/topic/conversation-socket/",
        SEND_PATH = "/app/conversation-socket/",
        FRIEND_NAME_URL = conf.BACK_URL + "/user/friend/name",
        USER_INFO_URL = conf.BACK_URL + "/user/info";

    var VM = {
        conversations: app.koa([]),
        selectedConversation: resource.conversation().oTemplate(),
        selectedConversationPtr: null,
        conversationReplies: app.koa([]),
        user: user,
        stompClient: null,
        $chatMessages: null,
        messageInput: app.koo(),
        friendSearchId: app.koo(),
        passedUserId: null,
        
        activate: function (username, userId) {
            that = this;
            window.message = this;
            that.passedUserId = userId;
        },
        deactivate: function() {
            that.disconnect();
            that.passedUserId = null;
            that.conversationReplies.removeAll();
        },
        attached: function () {
            that.$chatMessages = $('.chat-messages');
            resource.conversation().get(this.conversations, function(data) {
                if (user.id() == data.userTwoId) {
                    data.userId = data.userOneId;
                    data.userName = data.userOneName;
                    data.userAvatar = data.userOneAvatar;
                } else {
                    data.userId = data.userTwoId;
                    data.userName = data.userTwoName;
                    data.userAvatar = data.userTwoAvatar;
                }
                return data;
            }).then(function() {
                var conversation;
                if (that.conversations().length > 0) {
                    if (that.passedUserId) {
                        var targetConversation = that.conversations().filter(function(conversation) {
                            return conversation.userId() == that.passedUserId;
                        });
                        if (targetConversation.length > 0) {
                            conversation = targetConversation[0];
                        } else {
                            /* Do nothing */
                        }
                    } else {
                        //that.select(that.conversations()[0]);
                        conversation = that.conversations()[0];
                    }
                    if (conversation) {
                        /* Conversation already established */
                        app.koMap.fromJS(conversation, {}, that.selectedConversation);
                        that.selectedConversationPtr = conversation;
                        resource.conversationReply({
                            from: new Date().getTime()
                        }, {
                            url: resource.conversationReply().url + '/' + conversation.userId()
                        }).get().then(function (data) {
                            data = data.map(that.incomeOutcomeMarkerMapper);
                            data.reverse();
                            app.koMap.fromJS(data, {}, that.conversationReplies);
                            that.connect();
                            that.scrollToLastMessage();
                        });
                    } else {
                        /* New conversation */
                        resource.user({id: that.passedUserId}, {url: USER_INFO_URL}).get().then(function(data) {
                            var publicUserInfo = data.dataList[0];
                            conversation = resource.conversation().oTemplate();
                            conversation.userId(publicUserInfo.id);
                            conversation.userName(publicUserInfo.name);
                            conversation.userAvatar(publicUserInfo.avatar);
                            conversation.userOneId(user.id());
                            conversation.userOneName(user.name());
                            conversation.userOneName(user.avatar());
                            conversation.userTwoId(publicUserInfo.id);
                            conversation.userTwoName(publicUserInfo.name);
                            conversation.userTwoAvatar(publicUserInfo.avatar);
                            that.selectedConversationPtr = conversation;
                            app.koMap.fromJS(conversation, {}, that.selectedConversation);
                            that.conversations.unshift(conversation);
                        });
                    }
                }

                that.scrollToMessageInput();
            });
        },
        compositionComplete: function () {
        },
        detached: function () {
        },
        select: function(userId) {
            that.disconnect();
            router.navigate("#profile/" + user.name() + "/message/" + userId);
        },
        incomeOutcomeMarkerMapper: function(rawConversationReply) {
            rawConversationReply.income = rawConversationReply.userId != user.id();
            return rawConversationReply;
        },
        connect: function() {
            var socket = new SockJS(CONVERSATION_SOCKET_URL);
            that.stompClient = Stomp.over(socket);
            that.stompClient.connect({}, function(frame) {
                console.log('Connected: ' + frame);
                that.stompClient.subscribe(SUBSCRIBE_PATH + that.selectedConversation.id(), function(message) {
                    console.log(message);
                    var reply = JSON.parse(message.body);
                    that.receive(reply);
                });
            });
        },
        disconnect: function() {
            if (that.stompClient && that.stompClient.connected) {
                that.stompClient.disconnect();
            }
        },
        send: function() {
            var msg = that.messageInput();
            if (that.selectedConversation.id() != null) {
                /* Conversation already established */
                that.stompClient.send(SEND_PATH + that.selectedConversation.id() + "/" + user.id(), {}, JSON.stringify({
                    "userId": user.id(),
                    "userName": user.name(),
                    "reply": msg
                }));
            } else {
                /* New conversation */
                var conUrl = resource.conversation().url + "/" + that.selectedConversation.userTwoId();
                resource.conversation({reply: msg}, {url: conUrl}).post().then(function(conversationReply) {
                    that.selectedConversation.id(conversationReply.conversationId);
                    that.receive(conversationReply);
                    that.connect();
                });
            }
            that.messageInput('');
        },
        receive: function(conversationReply) {
            var reply = that.incomeOutcomeMarkerMapper(conversationReply);
            var observableReply = app.koMap.fromJS(reply);
            that.conversationReplies.push(observableReply);
            that.scrollToLastMessage();

            var lastUpdate = new Date();
            that.selectedConversation.lastUpdate(lastUpdate.getTime());
            that.selectedConversationPtr.lastUpdate(lastUpdate.getTime());
            that.conversations.sort(function(a, b){
                return a.lastUpdate() < b.lastUpdate();
            });
        },
        scrollToLastMessage: function() {
            var $messages = $('.chat-message'),
                $lastMessageElem = $($messages[$messages.length - 1]);
            that.$chatMessages.animate({
                scrollTop: $lastMessageElem.offset().top
            }, 1000);
        },
        scrollToMessageInput: function() {
            $('body').animate({
                scrollTop: $('#message-input').offset().top
            }, 500, 'linear', function() {
                $('#message-input').focus();
            });
        },
        friendSearchSelect2Options: {
            placeholder: "Search friend...",
            allowClear: true,
            ajax: {
                url: FRIEND_NAME_URL,
                quietMillis: 500,
                dataType: 'json',
                params: {
                    xhrFields: {withCredentials: true}
                },
                data: function (term, page) { // page is the one-based page number tracked by Select2
                    return {
                        name: term, //search term
                        size: 10 // page size
                    };
                },
                results: function (data) {
                    var more = false; // do not support paging
                    var resultDatas = [];
                    for (var i in data) {
                        resultDatas.push({
                            id: i,
                            text: data[i]
                        })
                    }
                    return {results: resultDatas, more: more};
                }
            }
        },
        friendSelect: function() {
            if (that.friendSearchId()) {
                that.select(that.friendSearchId());
            }
        }
    };
    return VM;
});
