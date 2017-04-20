Db = {};
Db.Uotel = {};
Db.Uotel.Collection = {};
Db.Uotel.Entities = {};
Db.Uotel.Api = {};
Db.Uotel.Util = {};
Db.Uotel.Remote = {};


Db.Uotel.Util.UrlParam = function (name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results == null) {
        return null;
    }
    else {
        return results[1] || 0;
    }
}

Db.Uotel.Util.Promise = function () {
    var self = this;

    this.handlers = {};
    this.code = 0;
    this.data = null;
    this.jqXHR = null;

    this.setHandlers = function (handlers) {
        self.handlers = handlers;

        if (self.code !== 0) {
            self.handleResult(self.code, self.data);
        }

        return self;
    };

    this.handleResult = function (code, data) {

        if (self.handlers.hasOwnProperty(code)) {
            self.handlers[code](data);
        } else if (self.handlers.hasOwnProperty('_default')) {
            self.handlers._default(code, data);
        }

        self.code = code;
        self.data = data;
    };

    this.cancel = function () {
        self.jqXHR.abort();
    }
};

Db.Uotel.Remote = {
    call: function (action, url, data) {
        var promise = new Db.Uotel.Util.Promise();

        promise.jqXHR = $.ajax({
            type: action,
            dataType: "json",
            url: url,
            data: data,
            cache: false,

            error: function (jqXHR, textStatus, errorThrown) {
                try {
                    promise.handleResult(jqXHR.status, jQuery.parseJSON(jqXHR.responseText));
                } catch (e) {
                    promise.handleResult(jqXHR.status, jqXHR.responseText);
                }
            },

            success: function (data, textStatus, jqXHR) {
                promise.handleResult(jqXHR.status, jQuery.parseJSON(jqXHR.responseText));
            }
        });

        return promise;
    },

    doDelete: function (url, data) {
        return Db.Uotel.Remote.call('DELETE', url, data);
    },

    doPost: function (url, data) {
        return Db.Uotel.Remote.call('POST', url, data);
    },

    doPut: function (url, data) {
        return Db.Uotel.Remote.call('PUT', url, data);
    },

    doGet: function (url, data) {
        return Db.Uotel.Remote.call('GET', url, data);
    }
};
