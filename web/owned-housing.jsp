<%--
  Created by IntelliJ IDEA.
  User: Student Nathan
  Date: 4/8/2017
  Time: 4:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:default-template>
  <jsp:attribute name="defaultTitle">
    Owned Housing
  </jsp:attribute>
  <jsp:body>
    <div class="row">
      <div class="col-md-9">
        <div class="form-group">
          <table id="tblOwnedHousing" class="table table-striped table-bordered table-hover table-condensed"></table>
        </div>
      </div>
      <div class="col-md-3 btn-column">
        <a class="btn btn-default" href="${pageContext.request.contextPath}/new-housing.jsp" role="button">Create</a>
        <a class="btn btn-default disabled btnSingleItem" href="${pageContext.request.contextPath}/temporary-housing.jsp" role="button">Manage</a>
        <%--<button id="btnDeleteHousing" class="btn btn-default disabled btnSingleItem" >Delete</button>--%>

      </div>
    </div>

    <script>
		var currentUserId = ${pageContext.session.getAttribute("CurrentUser").getId()};
		var dataTable = $("#tblOwnedHousing");
		var table;

		var temporaryHousingCollection;
		var selectedTemporaryHousing;

		initOwnedHousingList();

		function initOwnedHousingList() {

			table = dataTable.DataTable({
				"sDom": 'tr' +
				'<"footer-flex"' +
				'	<l>' +
				'	<"hidden-xs hidden-sm"i>' +
				'	<p>' +
				'>',
				"searching": false,
				"aLengthMenu": [[25, 50, 100, 1000], [25, 50, 100, 1000]],
				"scrollY": "400px",
				"serverSide": true,
				"select": 'single',
				"ajax": function (data, callback, settings) {
					var filters = [new Db.Uotel.Entities.Filter("OwnerId", currentUserId)];
					var sorters = Db.Uotel.Util.parseSortersFromDataTableData(data);
					var page = Db.Uotel.Util.calculatePage(data.start, data.length);
					var perPage = data.length;
					var draw = data.draw;

					Db.Uotel.Api.TemporaryHousing.getTemporaryHousing(page, perPage, sorters, filters, [], draw).setHandlers({
						success: function(data) {
							temporaryHousingCollection = data.collection;

							callback(data.toDTData([
								'Id',
								"Name",
								"Category",
								"Address",
								"URL",
								"PhoneNumber",
								"YearBuilt"
							]));
						},
						error: function(error) {
							// Do nothing for now
						}
					});
				},
				"columnDefs": getDataTableColumnDefinitions()
			});

			table.on( 'select', function ( e, dt, type, index ) {
				selectedTemporaryHousing = temporaryHousingCollection.getItemAt(index)
				handleRowClicked();
			}).on( 'deselect', function ( e, dt, type, index ) {
				selectedTemporaryHousing = null;
				handleRowClicked();
            });
		}

		function getDataTableColumnDefinitions() {
			var columnDefinitions = [];
			var tempHousing = new Db.Uotel.Entities.TemporaryHousing();
			var properties = tempHousing.getProperties();

			var columns = [
				'Id',
				"Name",
				"Category",
				"Address",
				"URL",
				"PhoneNumber",
				"YearBuilt"
			];

			columns.forEach(function(columnName, i){
				if (properties.hasOwnProperty(columnName)) {
					var property = properties[columnName];
					columnDefinitions.push({name: property.name, title: property.alias, targets: i});
				}
				else{
					columnDefinitions.push({name: columnName, title: columnName, targets: i});
				}

			});

			return columnDefinitions;
		}

		function handleRowClicked() {
			var buttonsForSingleItems = $(".btnSingleItem");
			var buttonLinksForSingleItems = $("a.btnSingleItem");
			if(selectedTemporaryHousing == null){
				buttonsForSingleItems.addClass("disabled");
				return;
			}
			buttonsForSingleItems.removeClass("disabled");
			buttonLinksForSingleItems.each(function () {
				this.search = "?Id="+selectedTemporaryHousing.Id;
			});
		}

    </script>
  </jsp:body>
</t:default-template>