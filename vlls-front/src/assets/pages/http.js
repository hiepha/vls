/**
 * Created by hiephn on 2014/08/28.
 */
define('http', ['modals', 'notifies'], function (modals, notifies) {
    function ErrorHandler(options) {
        var defaultHandler = {
            '401': function (jqXHR) {
                notifies.error('Login error.', this.getMessage(jqXHR));
            },
            '500': function (jqXHR) {
                notifies.error('Server error.', this.getMessage(jqXHR));
            },
            'other': function (jqXHR) {
                notifies.error('Error.', this.getMessage(jqXHR));
            },
            'handle': function (jqXHR) {
                if (this[jqXHR.status]) {
                    this[jqXHR.status](jqXHR);
                } else {
                    this.other(jqXHR);
                }
            },
            'getMessage': function(jqXHR) {
                if (jqXHR.responseText && jqXHR.responseText.length > 0 &&
                    jqXHR.responseText[0] == '{' && jqXHR.responseText.indexOf('message') > 0) {
                    return JSON.parse(jqXHR.responseText).message;
                } else {
                    return '';
                }
            }
        };
        return {
            handle: function (jqXHR) {
                if (options[jqXHR.status]) {
                    options[jqXHR.status].bind(options, jqXHR)();
                } else {
                    defaultHandler.handle.bind(defaultHandler, jqXHR)();
                }
            }
        }
    }

    function request(method, options) {
        options.method = method;
        options.data = options.data ? options.data : {};
        options.error = options.error ? options.error : new ErrorHandler(options).handle;
        options.xhrFields = options.xhrFields ? options.xhrFields : {withCredentials: true};
        options.dataType = options.dataType ? options.dataType : 'json';
        return $.ajax(options);
    }

    return {
        get: request.bind(null, "GET"),
        put: request.bind(null, "PUT"),
        post: request.bind(null, "POST"),
        delete: request.bind(null, "DELETE")
    }
});