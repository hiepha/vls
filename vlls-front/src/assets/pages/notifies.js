/**
 * Created by tuannka on 2014/11/16.
 */
define('notifies', ['jquery', 'notify'], function ($) {
    function success(caption, content) {
        var style = {
            background: '#419641',
            color: 'white'
        }
        notify(caption, content, style);
    }

    function info(caption, content) {
        var style = {
            background: '#2d6ca2',
            color: 'white'
        }
        notify(caption, content, style);
    }

    function warning(caption, content) {
        var style = {
            background: '#eb9316',
            color: 'white'
        }
        notify(caption, content, style);
    }

    function error(caption, content) {
        var style = {
            background: '#c12e2a',
            color: 'white'
        }
        notify(caption, content, style);
    }

    function notify(caption, content, style) {
        var not = {};
        not.style = style;
        not.timeout = 5000;
        if (typeof(caption) != 'undefined') {
            not.caption = caption;
        }
        if (typeof(content) != 'undefined') {
            not.content = content;
        }
        $.Notify(not);
    }

    return {
        success: success,
        info: info,
        warning: warning,
        error: error
    }
});
