/**
 * Created by hiephn on 2014/08/28.
 */
define('modals', ['jquery'], function($) {
    function hideModals() {
        var $visibleDialogs = $('.modal:visible');
        if ($visibleDialogs.size() > 0) {
            $visibleDialogs.modal('hide');
        }
    }
    function info(html) {
        hideModals();
        $('#info-modal .modal-body').html(html);
        $('#info-modal').modal('show');
    }
    return {
        info: info,
        hideModals: hideModals
    }
});
