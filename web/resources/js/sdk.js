Db = {};
Db.Uotel = {};
Db.Uotel.Collection = {};
Db.Uotel.Entities = {};
Db.Uotel.Api = {};
Db.Uotel.Util = {};
Db.Uotel.Remote = {};

Db.Uotel.Util.Date = {
	weekDayNames: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
	monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
	options: {
		j: function (date) {
			return date.getDate().toString();
		},
		d: function (date) {
			var val = date.getDate();
			return val < 10 ? '0' + val : val;
		},
		w: function (date) {
			return date.getDay();
		},
		D: function (date) {
			return Db.Uotel.Util.Date.weekDayNames[date.getDay()];
		},
		l: function (date) {
			return Db.Uotel.Util.Date.weekDayNames[date.getDay()].substring(0, 3);
		},
		n: function (date) {
			return date.getMonth() + 1;
		},
		F: function (date) {
			return Db.Uotel.Util.Date.monthNames[date.getMonth()];
		},
		m: function (date) {
			var val = date.getMonth() + 1;
			return val < 10 ? '0' + val : val;
		},
		M: function (date) {
			return Db.Uotel.Util.Date.monthNames[date.getMonth()].substring(0, 3);
		},
		Y: function (date) {
			return date.getFullYear();
		},
		y: function (date) {
			return date.getFullYear().toString().substring(2);
		},
		G: function (date) {
			return date.getHours();
		},
		H: function (date) {
			var val = date.getHours();
			return val < 10 ? '0' + val : val;
		},
		a: function (date) {
			return date.getHours() <= 11 ? 'am' : 'pm';
		},
		A: function (date) {
			return date.getHours() <= 11 ? 'AM' : 'PM';
		},
		g: function (date) {
			return date.getHours() % 12 || 12;
		},
		h: function (date) {
			var val = Db.Uotel.Util.Date.options.g(date);
			return val < 10 ? '0' + val : val;
		},
		i: function (date) {
			var val = date.getMinutes();
			return val < 10 ? '0' + val : val;
		},
		s: function (date) {
			var val = date.getSeconds();
			return val < 10 ? '0' + val : val;
		},
		S: function (date) {
			var val = date.getDate();
			return val < 4 | val > 20 && (['st', 'nd', 'rd'][val % 10 - 1] || 'th');
		},
		U: function (date) {
			return Math.round(date.getTime() / 1000);
		}
	},
	optionsUTC: {
		j: function (date) {
			return date.getUTCDate().toString();
		},
		d: function (date) {
			var val = date.getUTCDate();
			return val < 10 ? '0' + val : val;
		},
		w: function (date) {
			return date.getUTCDay();
		},
		D: function (date) {
			return Db.Uotel.Util.Date.weekDayNames[date.getUTCDay()];
		},
		l: function (date) {
			return Db.Uotel.Util.Date.weekDayNames[date.getUTCDay()].substring(0, 3);
		},
		n: function (date) {
			return date.getUTCMonth() + 1;
		},
		F: function (date) {
			return Db.Uotel.Util.Date.monthNames[date.getUTCMonth()];
		},
		m: function (date) {
			var val = date.getUTCMonth() + 1;
			return val < 10 ? '0' + val : val;
		},
		M: function (date) {
			return Db.Uotel.Util.Date.monthNames[date.getUTCMonth()].substring(0, 3);
		},
		Y: function (date) {
			return date.getUTCFullYear();
		},
		y: function (date) {
			return date.getUTCFullYear().toString().substring(2);
		},
		G: function (date) {
			return date.getUTCHours();
		},
		H: function (date) {
			var val = date.getUTCHours();
			return val < 10 ? '0' + val : val;
		},
		a: function (date) {
			return date.getUTCHours() <= 11 ? 'am' : 'pm';
		},
		A: function (date) {
			return date.getUTCHours() <= 11 ? 'AM' : 'PM';
		},
		g: function (date) {
			return date.getUTCHours() % 12 || 12;
		},
		h: function (date) {
			var val = Db.Uotel.Util.Date.optionsUTC.g(date);
			return val < 10 ? '0' + val : val;
		},
		i: function (date) {
			var val = date.getUTCMinutes();
			return val < 10 ? '0' + val : val;
		},
		s: function (date) {
			var val = date.getUTCSeconds();
			return val < 10 ? '0' + val : val;
		},
		S: function (date) {
			var val = date.getUTCDate();
			return val < 4 | val > 20 && (['st', 'nd', 'rd'][val % 10 - 1] || 'th');
		},
		U: function (date) {
			return Math.round(date.getTime() / 1000);
		}
	}
};

Db.Uotel.Util.Date.format = function (date, format) {
	return format.replace(/\\?([a-z])/gi, function (match, s) {
		return Db.Uotel.Util.Date.options[match] ? Db.Uotel.Util.Date.options[match](date) : s;
	});
};

Db.Uotel.Util.Date.formatUtc = function (date, format) {
	return format.replace(/\\?([a-z])/gi, function (match, s) {
		return Db.Uotel.Util.Date.optionsUTC[match] ? Db.Uotel.Util.Date.optionsUTC[match](date) : s;
	});
};

Db.Uotel.Util.Date.microtime = function (get_as_float) {
	var now = new Date().getTime() / 1000;
	var s = parseInt(now, 10);
	return (get_as_float) ? now : (Math.round((now - s) * 1000) / 1000) + ' ' + s;
};

Db.Uotel.Util.Date.secondsToHumanReadableDuration = function (seconds) {
	var date = new Date();
	date.setTime(seconds * 1000);
	var days = parseInt(seconds / (24 * 60 * 60));
	var daysString = (days) ? days + ' day' + ((days > 1) ? 's ' : ' ') : '';

	return daysString + date.formatUtc('H:i:s');
};

Db.Uotel.Util.Date.secondsToHoursMinutes = function (seconds) {
	var secondsLeftToFormat = seconds;
	var hours = parseInt(secondsLeftToFormat / (3600));
	secondsLeftToFormat -= hours * 3600;
	var minutes = parseInt(secondsLeftToFormat / (60));

	return hours + ":" + (minutes < 10 ? '0' + minutes : minutes);
};

Date.prototype.format = function (format) {
	return Db.Uotel.Util.Date.format(this, format);
};

Date.prototype.formatUtc = function (format) {
	return Db.Uotel.Util.Date.formatUtc(this, format);
};

Db.Uotel.Util.isArray = function (object) {
	return Object.prototype.toString.call(object) === '[object Array]';
};

Db.Uotel.Util.isFunction = function (object) {
	return Object.prototype.toString.call(object) === '[object Function]';
};

Db.Uotel.Util.parseDraw = function (draw) {
	var newDraw = parseInt(draw);

	return (newDraw) ? newDraw : 0;
};

Db.Uotel.Util.calculatePage = function (start, perPage) {
	return (perPage > 0) ? Math.floor(start / perPage) + 1 : 1;
};

Db.Uotel.Util.parseSorterFromDataTableData = function (data) {
	var sorters = Db.Uotel.Util.parseSortersFromDataTableData(data);
	return (sorters.length > 0) ? sorters[0] : null;
};
Db.Uotel.Util.parseSortersFromDataTableData = function (data) {

	var sorters = [];

	data.order.forEach(function (dataSorter) {
		var sortBy = data.columns[dataSorter.column].name;
		var sortDirection = dataSorter.dir.toUpperCase();

		sorters.push(new Db.Uotel.Entities.Sorter(sortBy, sortDirection));
	});

	return sorters;
};

Db.Uotel.Util.IsInstanceOf = function (instance, type) {
	return instance.constructor == type;
};

Db.Uotel.Util.formatTime = function (seconds) {
	var h = Math.floor(seconds / 60 / 60);
	var m = Math.floor(seconds / 60) - h * 60;
	var s = seconds - h * 60 * 60 - m * 60;

	var string = '';

	(h) ? string += h + 'h' : null;
	(m) ? string += m + 'm' : null;
	(s) ? string += s + 's' : null;

	return string;
};

Db.Uotel.Util.formatHours = function (seconds) {
	return (Math.round(seconds / 60 / 60 * 1000) / 1000).toFixed(3);
};

Db.Uotel.Util.createApiPayload = function (page, perPage, sorters, filters, foreignTablesToInclude) {
	if (foreignTablesToInclude && !Array.isArray(foreignTablesToInclude)) {
		foreignTablesToInclude = [foreignTablesToInclude];
	}

	if (sorters) {
		sorters = Db.Uotel.Util.hashSorters(sorters);
	}

	if (filters) {
		filters = Db.Uotel.Util.hashFilters(filters);
	}

	return {
		page: (page) ? page : 1,
		perPage: (perPage || perPage === 0) ? perPage : 1,
		filters: filters ? filters : [],
		sorters: sorters ? sorters : [],
		foreignTablesToInclude: foreignTablesToInclude ? foreignTablesToInclude : [],
	};
};

Db.Uotel.Util.calculateEntityType = function (endpointBlurb) {
	var lowerCamelCase = endpointBlurb.replace(/-([a-z])/gi, function (g) {
		return g[1].toUpperCase();
	});

	return lowerCamelCase.charAt(0).toUpperCase() + lowerCamelCase.slice(1, -1);

};

Db.Uotel.Util.calculateEndpointBlurb = function (apiUrl) {
	return apiUrl.replace(/^\/api\//, '');
};

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

Db.Uotel.Collection.BaseCollection = function () {
	var self = this,
		items = [];

	this.primaryKey = 'Id';
	this.itemType = null;

	this.addItem = function (item) {
		items.push(item);
	};

	this.prependItem = function (item) {
		items.unshift(item);
	};

	this.reverse = function () {
		items.reverse();
	};

	this.removeItem = function (item) {
		var index = items.indexOf(item);

		if (index >= 0) {
			items.splice(index, 1);
		}
	};

	this.removeAll = function () {
		items = [];
	};

	this.replaceItem = function (index, item) {
		var oldItem = self.getItemAt(index);

		if (oldItem) {
			items.splice(index, 1, item);
		}
	};

	this.getItemAt = function (index) {
		if (items.length > index) {
			return items[index];
		}

		return null;
	};

	this.getItemIndex = function (item) {
		return items.indexOf(item);
	};

	this.getCount = function () {
		return items.length;
	};

	this.packOptions = function (valueField, labelField, selectInput, selectedValue) {
		var options = [];

		self.each(function (item) {
			var option = jQuery("<option></option>");

			if (item.hasOwnProperty(valueField)) {
				option.attr('value', item[valueField]);
			} else {
				option.attr('value', valueField);
			}

			if (item[valueField] == selectedValue || item[labelField] == selectedValue) {
				option.attr('selected', 'selected');
			}

			if (!Db.Uotel.Util.isArray(labelField)) {
				labelField = [labelField];
			}

			option.text(item.parsePropertiesFromArray(labelField));

			if (selectInput) {
				selectInput.append(option);
			}

			options.push(option);
		});

		return options;
	};

	this.toHash = function () {
		var hashArray = new Array();

		self.each(function (item) {
			hashArray.push(item.toHash());
		});

		return hashArray;
	};



	this.toFlatHash = function (keyPrepend, hash) {
		keyPrepend = (keyPrepend) ? keyPrepend+"-" : "";
		hash = (hash) ? hash : {};

		for (var i = 0; i < items.length; i++) {
			item.toFlatHash(keyPrepend+i, hash);
		}

		hash[keyPrepend+"CollectionLength"] = items.length;

		return hash;
	};

	this.varCount = function () {
		var count = 0;

		self.each(function (item) {
			if (item.hasOwnProperty('varCount')) {
				count += item.varCount();
			} else {
				count++;
			}
		});

		return count;
	};

	this.each = function (handler) {
		for (var i = 0; i < items.length; i++) {
			if (handler(items[i])) {
				return;
			}
		}
	};

	this.getById = function (id) {
		for (var i = 0; i < items.length; i++) {
			if (!items[i].hasOwnProperty(self.primaryKey)) {
				continue;
			} else if (items[i][self.primaryKey] == id) {
				return items[i];
			}
		}

		return null;
	};

	this.setData = function (data) {
		// Allow data to be a Db.Uotel.Collections.BaseCollection, not just a hash
		if (data && data.hasOwnProperty('toHash')) {
			data = data.toHash();
		}

		for (var i in data) {
			if (self.itemType) {
				var item = new self.itemType(data[i]);
			} else {
				var item = data[i];
			}

			self.addItem(item);
		}
	};

	this.toListResult = function () {
		var listResult = new Db.Uotel.Entities.ListResult();
		listResult.page = 1;
		listResult.perPage = self.getCount();
		listResult.totalItemCount = self.getCount();
		listResult.collection = self;
	};

	this.splitByProperty = function (property) {
		var testItem = new self.itemType();
		var splitCollection = {};

		if (!testItem.hasOwnProperty(property)) {
			return self;
		}

		self.each(function (item) {
			if (!splitCollection.hasOwnProperty(item[property])) {
				splitCollection[item[property]] = new self.constructor();
			}

			splitCollection[item[property]].addItem(item);
		});

		return splitCollection;
	};

	this.getArrayOfCollectionProperty = function (property) {
		var propertyArray = [];

		self.each(function (item) {

			if (item.hasOwnProperty(property)) {
				propertyArray.push(item[property]);
			}
			else {
				propertyArray.push(property);
			}

		});

		return propertyArray;
	};
};

Db.Uotel.Entities.BaseEntity = function () {
	var self = this,
		properties = {};

	this.IsBaseEntity = true;

	this.uid = Math.random().toString(36).substr(2, 9);

	this.createProperty = function (propertyName, defaultValue, propertyAlias, entityType) {

		properties[propertyName] = {
			'name': propertyName,
			'alias': propertyAlias,
			'defaultValue': defaultValue,
			'entityType': entityType
		};

		return defaultValue;
	};

	this.setData = function (data) {
		if (!data) return;

		for (var propertyName in properties) {
			var property = properties[propertyName];

			if (data.hasOwnProperty(propertyName)) {
				var value = data[propertyName];

				if (property.entityType && value) {
					if (property.entityType === Date) {
						if (isNaN(value + 1)) {
							value = Date.parse(value);
						}
					}

					self[propertyName] = new property.entityType(value);
				} else {
					self[propertyName] = value;
				}
			}
		}
	};

	this.getColumnDefinitions = function(columns){
		var definitions = [];

		columns.forEach(function (column, i) {
			if (!Db.Uotel.Util.isArray(column)) {
				column = [column, findRelatedAlias(column)];
			}

			if(column.length == 1){
				column[1] = findRelatedAlias(column[0]);
			}

			definitions.push({name:column[0], title: column[1], targets: i});
		});

		return definitions;
	};


	function findRelatedAlias(propertyName){
		if (propertyName.indexOf('.') >= 0) {
			var trail = propertyName.split('.');
			var currentEntity = self;

			for (var i = 0; i < trail.length; i++) {
				if (currentEntity.hasOwnProperty("hasProperty") && currentEntity.hasProperty(trail[i])) {
					var property = currentEntity.getProperties()[trail[i]];
					console.log(property);
					if(property.hasOwnProperty("entityType") && property.entityType){
						currentEntity = new property.entityType();
						if(!currentEntity.hasOwnProperty("IsBaseEntity") || !currentEntity.IsBaseEntity){
							currentEntity = property;
						}
					}else {
						currentEntity = property;
					}
				} else {
					return propertyName;
				}
			}

			return (currentEntity.hasOwnProperty("alias")) ? currentEntity["alias"] : propertyName;
		} else {
			if (self.hasProperty(propertyName)) {
				return self.getProperties()[propertyName]["alias"];
			} else {
				return propertyName;
			}
		}
	}

	this.hasProperty = function (propertyName) {
		return properties.hasOwnProperty(propertyName);
	};

	this.parsePropertiesFromArray = function (input) {
		if (!Db.Uotel.Util.isArray(input)) {
			input = [input];
		}

		var result = "";

		for (var i = 0; i < input.length; i++) {
			if (Db.Uotel.Util.isFunction(input[i])) {
				result = input[i](result, self);
				continue;
			}

			// sub-children properties
			if (input[i].indexOf('.') >= 0) {
				var trail = input[i].split('.');
				var currentValue = self;

				for (var trailI = 0; trailI < trail.length; trailI++) {
					if (!currentValue) {
						currentValue = 'Undefined';
						break;
					} else if (currentValue.hasOwnProperty(trail[trailI])) {
						currentValue = currentValue[trail[trailI]];
					} else {
						currentValue = input[i];
						break;
					}
				}

				result += currentValue;
			} else {
				if (self.hasProperty(input[i])) {
					result += self[input[i]];
				} else {
					result += input[i];
				}
			}
		}

		return result;
	};

	this.reset = function () {
		for (var propertyName in properties) {
			self[propertyName] = properties[propertyName].defaultValue;
		}
	};

	this.toHash = function (propertiesList, settings, blackOrWhitelist) {
		if (propertiesList && !blackOrWhitelist) {
			return;
		}

		var hash = {};
		propertiesList = propertiesList ? propertiesList : [];
		settings = settings ? settings : {};

		for (var propertyIndex in properties) {
			var property = properties[propertyIndex];

			if (propertiesList.indexOf(property.name) >= 0 && blackOrWhitelist === 'blacklist') {
				continue;
			}

			if (!blackOrWhitelist || propertiesList.indexOf(property.name) >= 0 && blackOrWhitelist === 'whitelist') {
				if (self[property.name] && self[property.name].hasOwnProperty('toHash')) {
					hash[property.name] = self[property.name].toHash();
				} else if (self[property.name] instanceof Date) {
					if (settings.hasOwnProperty(property.name)) {
						hash[property.name] = self[property.name].format(settings[property.name]);
					} else {
						hash[property.name] = self[property.name].getTime() / 1000;
					}
				} else {
					if (typeof self[property.name] === 'boolean') {
						hash[property.name] = (self[property.name]) ? 1 : 0;
					} else {
						if(self[property.name] !== undefined && self[property.name] !== null){
							hash[property.name] = self[property.name];
						}
					}
				}
			}
		}

		return hash;
	};

	this.toFlatHash = function (propertiesList, settings, blackOrWhitelist, keyPrepend, hash) {
		if (propertiesList && !blackOrWhitelist) {
			return;
		}

		keyPrepend = (keyPrepend) ? keyPrepend+"-" : "";

		hash = (hash) ? hash : {};
		propertiesList = propertiesList ? propertiesList : [];
		settings = settings ? settings : {};

		for (var propertyIndex in properties) {
			var property = properties[propertyIndex];

			if (propertiesList.indexOf(property.name) >= 0 && blackOrWhitelist === 'blacklist') {
				continue;
			}

			if (!blackOrWhitelist || propertiesList.indexOf(property.name) >= 0 && blackOrWhitelist === 'whitelist') {
				if (self[property.name] && self[property.name].hasOwnProperty('toFlatHash')) {
					self[property.name].toFlatHash(null, null, null, keyPrepend+property.name, hash);
				} else if (self[property.name] instanceof Date) {
					if (settings.hasOwnProperty(property.name)) {
						hash[keyPrepend+property.name] = self[property.name].format(settings[property.name]);
					} else {
						hash[keyPrepend+property.name] = self[property.name].format("Y-m-d");
					}
				} else {
					if (typeof self[property.name] === 'boolean') {
						hash[keyPrepend+property.name] = (self[property.name]) ? 1 : 0;
					} else {
						if(self[property.name] !== undefined && self[property.name] !== null){
							hash[keyPrepend+property.name] = self[property.name];
						}
					}
				}
			}
		}

		return hash;
	};

	this.getCompositeKey = function (columns) {
		var key = '';
		var first = true;
		var string;

		if (columns && columns instanceof Array) {
			for (var i in columns) {
				if (columns[i] && self.hasOwnProperty(columns[i])) {
					string = (self[columns[i]]) ? String(self[columns[i]]) : '';

					if (first) {
						key = string;
						first = false;
					} else {
						key = String(key) + '-' + string;
					}
				} else {
					key = 'no value for column, or property does not exist';
					return key;
				}
			}
		}

		return key;
	};

	this.getProperties = function () {
		return properties;
	};

	this.getPkHash = function () {
		if (self.hasOwnProperty('pk')) {
			return self.toHash(self.pk, null, 'whitelist');
		}
	};

	this.getPrimaryKey = function () {
		if (self.hasOwnProperty('pk')) {
			return self.getCompositeKey(self.pk);
		}
	};

	this.getPk = this.getPrimaryKey;

	this.varCount = function () {
		var count = 0;

		for (var propertyIndex in properties) {
			var property = properties[propertyIndex];

			if (self[property.name] && self[property.name].hasOwnProperty('varCount')) {
				count += self[property.name].varCount();
			} else {
				count++;
			}
		}

		return count;
	};

	return this;
};

Db.Uotel.Entities.Error = function (code, message) {
	this.code = code;
	this.message = message;

	return this;
};

Db.Uotel.Entities.Filter = function (field, value, type) {
	// Extend Base Entity
	Db.Uotel.Entities.BaseEntity.call(this);

	// List of properties
	this.field = this.createProperty('field', null, 'Filter Field');
	this.value = this.createProperty('value', null, 'Filter Value');
	this.type = this.createProperty('type', null, 'Filter Type');

	this.setData({
		field: field,
		value: value,
		type: type ? type : 'EQUAL'
	});

	return this;
};

Db.Uotel.Entities.Sorter = function (field, direction) {
	// Extend Base Entity
	Db.Uotel.Entities.BaseEntity.call(this);

	this.field = this.createProperty('field', null, 'Sort By Field');
	this.direction = this.createProperty('direction', null, 'Sort Direction');

	this.setData({
		field: field,
		direction: (direction) ? direction : 'ASC'
	});

	return this;
};

Db.Uotel.Entities.ListResult = function () {
	// Extend Base Entity
	Db.Uotel.Entities.BaseEntity.call(this);

	// List of properties
	this.page = this.createProperty('page', null, 'Page Number');
	this.perPage = this.createProperty('per_page', null, 'Per Page');
	this.totalItemCount = this.createProperty('count', null, 'Total Count (on server)');
	this.collection = this.createProperty('collection', null, 'List Collection');

	// Methods
	var self = this;

	this.toDTData = function (cols) {
		var data = {
			draw: self.draw,
			recordsTotal: self.totalItemCount,
			recordsFiltered: self.totalItemCount,
			data: []
		};

		var count = self.collection.getCount();

		for (var i = 0; i < count; i++) {
			var item = self.collection.getItemAt(i);
			var properties = item.getProperties();
			var columnData = [];

			if (!cols) {
				cols = properties;
			}

			for (var col in cols) {
				columnData.push(item.parsePropertiesFromArray(cols[col]));
			}

			data.data.push(columnData);
		}

		return data;
	};

	return this;
};

Db.Uotel.Entities.User = function (data) {
	// Extend Base Entity
	Db.Uotel.Entities.BaseEntity.call(this);

	// List of properties
	this.Id = this.createProperty('Id', null, 'Id');
	this.Login = this.createProperty('Login', null, 'Login Name');
	this.Password = this.createProperty('Password', null, 'Password');
	this.Name = this.createProperty('Name', null, 'Real Name');
	this.PhoneNumber = this.createProperty('PhoneNumber', null, 'Phone Number');
	this.Address = this.createProperty('Address', null, 'Address');
	this.IsAdmin = this.createProperty('IsAdmin', 0, 'Is User Admin');

	// Set Data with input
	this.setData(data);

	// Methods
	var self = this;

	this.getPk = function () {
		return self.getCompositeKey(['Id']);
	}
};


Db.Uotel.Api.User = {};

Db.Uotel.Api.User.loginUser = function (login, password) {
	// Create a promise the requester can listen on
	var promise = new Db.Uotel.Util.Promise();

	// Send Call
	Db.Uotel.Remote.doPost('/api/LoginController.jsp', {Login:login, Password:password}).setHandlers({
		200: function (data) {
			promise.handleResult('success', new Db.Uotel.Entities.User(data));
		},
		_default: function (code, data) {
			promise.handleResult('error', new Db.Uotel.Entities.Error(code, data));
		}
	});

	return promise;
};

Db.Uotel.Api.User.createUser = function (item) {
	// Create a promise the requester can listen on
	var promise = new Db.Uotel.Util.Promise();

	// Send Call
	Db.Uotel.Remote.doPost('/api/UserController.jsp', item.toFlatHash()).setHandlers({
		201: function (data) {
			promise.handleResult('success', new Db.Uotel.Entities.User(data));
		},
		_default: function (code, data) {
			promise.handleResult('error', new Db.Uotel.Entities.Error(code, data));
		}
	});

	return promise;
};

Db.Uotel.Api.User.getUsers = function (page, perPage, sorters, filters) {
	// Create a promise the requester can listen on
	var promise = new Db.Uotel.Util.Promise();

	var query = {
		page: (page || page === 0) ? page : 1,
		perPage: (perPage || perPage === 0) ? perPage : 25,
		"filter-length": (filters) ? filters.length : 0,
		"sorter-length": (sorters) ? sorters.length : 0
	};

	for (var i = 0; i < sorters.length; i += 1) {
		if (Db.Uotel.Util.IsInstanceOf(sorters[i], Db.Uotel.Entities.Sorter)) {
			query["sorter-"+i] = sorters[i].toFlatHash(null, null, null, "sorter-"+i);
		}
	}

	for (var i = 0; i < filters.length; i += 1) {
		if (Db.Uotel.Util.IsInstanceOf(sorters[i], Db.Uotel.Entities.Filter)) {
			query["filter-"+i] = filters[i].toFlatHash(null, null, null, "filter-"+i);
		}
	}


	// Send Call
	Db.Uotel.Remote.doGet('/api/UserController.jsp', query).setHandlers({
		200: function (data) {
			var collection = new Db.Uotel.Collection.BaseCollection();

			for (var index in data.results) {
				collection.addItem(new Db.Uotel.Entities.User(data.results[index]))
			}

			var listResult = new Db.Uotel.Entities.ListResult();
			listResult.collection = collection;
			listResult.perPage = data.perPage;
			listResult.totalItemCount = data.count;
			listResult.page = data.page;

			promise.handleResult('success', listResult);
		},
		_default: function (code, data) {
			promise.handleResult('error', new Db.Uotel.Entities.Error(code, data));
		}
	});

	return promise;
};

Db.Uotel.Entities.TemporaryHousing = function (data) {
	// Extend Base Entity
	Db.Uotel.Entities.BaseEntity.call(this);

	// List of properties
	this.Id = this.createProperty('Id', null, 'Id');
	this.Name = this.createProperty('Name', null, 'Name');
	this.Category = this.createProperty('Category', null, 'Category');
	this.Address = this.createProperty('Address', null, 'Address');
	this.URL = this.createProperty('URL', null, 'Housing Url');
	this.PhoneNumber = this.createProperty('PhoneNumber', null, 'Phone Number');
	this.YearBuilt = this.createProperty('YearBuilt', null, 'Year Built');
	this.ExpectedPrice = this.createProperty('ExpectedPrice', null, 'Expected Price Per Night');
	this.OwnerId = this.createProperty('OwnerId', null, 'Id Of Owner');

	this.Owner = this.createProperty('Owner', null, 'Owner', Db.Uotel.Entities.User);

	// Set Data with input
	this.setData(data);

	// Methods
	var self = this;

	this.getPk = function () {
		return self.getCompositeKey(['Id']);
	}
};


Db.Uotel.Api.TemporaryHousing = {};

Db.Uotel.Api.TemporaryHousing.getTemporaryHousing = function (page, perPage, sorters, filters, draw) {
	// Create a promise the requester can listen on
	var promise = new Db.Uotel.Util.Promise();

	var draw = Db.Uotel.Util.parseDraw(draw);

	var query = {
		page: (page || page === 0) ? page : 1,
		perPage: (perPage || perPage === 0) ? perPage : 25,
		"filter-length": (filters) ? filters.length : 0,
		"sorter-length": (sorters) ? sorters.length : 0
	};

	for (var i = 0; i < sorters.length; i += 1) {
		if (Db.Uotel.Util.IsInstanceOf(sorters[i], Db.Uotel.Entities.Sorter)) {
			sorters[i].toFlatHash(null, null, null, "sorter-"+i, query);
		}
	}

	for (var i = 0; i < filters.length; i += 1) {
		if (Db.Uotel.Util.IsInstanceOf(filters[i], Db.Uotel.Entities.Filter)) {
			filters[i].toFlatHash(null, null, null, "filter-"+i, query);
		}
	}


	// Send Call
	Db.Uotel.Remote.doGet('/api/TemporaryHousingController.jsp', query).setHandlers({
		200: function (data) {
			console.log(data);
			var collection = new Db.Uotel.Collection.BaseCollection();

			for (var index in data.results) {
				collection.addItem(new Db.Uotel.Entities.TemporaryHousing(data.results[index]))
			}

			var listResult = new Db.Uotel.Entities.ListResult();
			listResult.draw = draw + 1;
			listResult.collection = collection;
			listResult.perPage = data.perPage;
			listResult.totalItemCount = data.count;
			listResult.page = data.page;

			promise.handleResult('success', listResult);
		},
		_default: function (code, data) {

			console.log(data);
			promise.handleResult('error', new Db.Uotel.Entities.Error(code, data));
		}
	});

	return promise;
};

Db.Uotel.Api.TemporaryHousing.createTemporaryHousing = function (item) {
	// Create a promise the requester can listen on
	var promise = new Db.Uotel.Util.Promise();

	// Send Call
	Db.Uotel.Remote.doPost('/api/TemporaryHousingController.jsp', item.toFlatHash()).setHandlers({
		201: function (data) {
			promise.handleResult('success', new Db.Uotel.Entities.TemporaryHousing(data));
		},
		_default: function (code, data) {
			promise.handleResult('error', new Db.Uotel.Entities.Error(code, data));
		}
	});

	return promise;
};

Db.Uotel.Api.TemporaryHousing.updateTemporaryHousing = function (item) {
	// Create a promise the requester can listen on
	var promise = new Db.Uotel.Util.Promise();

	// Send Call
	Db.Uotel.Remote.doPost('/api/UpdateTemporaryHousingController.jsp', item.toFlatHash()).setHandlers({
		200: function (data) {
			promise.handleResult('success', new Db.Uotel.Entities.TemporaryHousing(data));
		},
		_default: function (code, data) {
			promise.handleResult('error', new Db.Uotel.Entities.Error(code, data));
		}
	});

	return promise;
};

Db.Uotel.Entities.AvailableDate = function (data) {
	// Extend Base Entity
	Db.Uotel.Entities.BaseEntity.call(this);

	// List of properties
	this.TemporaryHousingId = this.createProperty('TemporaryHousingId', null, 'Temp Housing Id');
	this.PeriodId = this.createProperty('PeriodId', null, 'Period Id');
	this.Name = this.createProperty('Name', null, 'Name');
	this.PricePerNight = this.createProperty('PricePerNight', null, '$ Per Night');

	this.TemporaryHousing = this.createProperty('TemporaryHousing', null, 'Temporary Housing', Db.Uotel.Entities.TemporaryHousing);
	this.Period = this.createProperty('Period', null, 'Period', Db.Uotel.Entities.Period);

	// Set Data with input
	this.setData(data);

	// Methods
	var self = this;

	this.getPk = function () {
		return self.getCompositeKey(['Id']);
	}
};

Db.Uotel.Api.AvailableDates = {};

Db.Uotel.Api.AvailableDates.getAvailableDates = function (page, perPage, sorters, filters, relationsToPopulate,draw) {
	// Create a promise the requester can listen on
	var promise = new Db.Uotel.Util.Promise();

	var draw = Db.Uotel.Util.parseDraw(draw);

	var query = {
		page: (page || page === 0) ? page : 1,
		perPage: (perPage || perPage === 0) ? perPage : 25,
		"filter-length": (filters) ? filters.length : 0,
		"sorter-length": (sorters) ? sorters.length : 0,
		"populate-length": (relationsToPopulate) ? relationsToPopulate.length : 0
	};

	for (var i = 0; i < sorters.length; i += 1) {
		if (Db.Uotel.Util.IsInstanceOf(sorters[i], Db.Uotel.Entities.Sorter)) {
			sorters[i].toFlatHash(null, null, null, "sorter-"+i, query);
		}
	}

	for (var i = 0; i < filters.length; i += 1) {
		if (Db.Uotel.Util.IsInstanceOf(filters[i], Db.Uotel.Entities.Filter)) {
			filters[i].toFlatHash(null, null, null, "filter-"+i, query);
		}
	}

	for (var i = 0; i < relationsToPopulate.length; i += 1) {
		query["populate-"+i] = relationsToPopulate[i];
	}


	// Send Call
	Db.Uotel.Remote.doGet('/api/AvailableDatesController.jsp', query).setHandlers({
		200: function (data) {
			console.log(data);
			var collection = new Db.Uotel.Collection.BaseCollection();

			for (var index in data.results) {
				collection.addItem(new Db.Uotel.Entities.AvailableDate(data.results[index]))
			}

			var listResult = new Db.Uotel.Entities.ListResult();
			listResult.draw = draw + 1;
			listResult.collection = collection;
			listResult.perPage = data.perPage;
			listResult.totalItemCount = data.count;
			listResult.page = data.page;

			promise.handleResult('success', listResult);
		},
		_default: function (code, data) {

			console.log(data);
			promise.handleResult('error', new Db.Uotel.Entities.Error(code, data));
		}
	});

	return promise;
};

Db.Uotel.Api.AvailableDates.createAvailableDate = function (item) {
	// Create a promise the requester can listen on
	var promise = new Db.Uotel.Util.Promise();

	// Send Call
	Db.Uotel.Remote.doPost('/api/AvailableDatesController.jsp', item.toFlatHash()).setHandlers({
		201: function (data) {
			promise.handleResult('success', new Db.Uotel.Entities.AvailableDate(data));
		},
		_default: function (code, data) {
			promise.handleResult('error', new Db.Uotel.Entities.Error(code, data));
		}
	});

	return promise;
};

Db.Uotel.Entities.Period = function (data) {
	// Extend Base Entity
	Db.Uotel.Entities.BaseEntity.call(this);

	// List of properties
	this.Id = this.createProperty('TemporaryHousingId', null, 'Temp Housing Id');
	this.From = this.createProperty('From', null, 'From', Date);
	this.To = this.createProperty('To', null, 'To', Date);

	// Set Data with input
	this.setData(data);

	// Methods
	var self = this;

	this.getPk = function () {
		return self.getCompositeKey(['Id']);
	}
};