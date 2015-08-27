/**
 * Created by tuannka on 2014/11/19.
 */
define('confModals', ['jquery'], function ($) {
    function hideModals() {
        var $visibleDialogs = $('.modal:visible');
        if ($visibleDialogs.size() > 0) {
            $visibleDialogs.modal('hide');
        }
    }

    var modal = {
        confirm: function (html, callback, callback2) {
            hideModals();
            var self = this;
            $('#conf-modal .modal-body .confirm').html(html);
            $btn1 = $('#conf-modal').find('#confirm-ok');
            $btn2 = $('#conf-modal').find('#confirm-no');
            if (typeof(callback) == 'function') {
                $btn1.bind('click', function () {
                    callback();
                    hideModals();
                });
                $btn1.removeClass('hide');
            } else {
                if(!$btn1.hasClass('hide')) {
                    $btn1.addClass('hide');
                }
            }
            if (typeof(callback2) == 'function') {
                $btn2.bind('click', function () {
                    callback2();
                    hideModals();
                });
                $btn2.removeClass('hide');
            } else {
                if(!$btn2.hasClass('hide')) {
                    $btn2.addClass('hide');
                }
            }
            $('#conf-modal').modal('show');
        }
    }

    return modal;
});