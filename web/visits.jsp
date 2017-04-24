<%--
  Created by IntelliJ IDEA.
  User: Student Nathan
  Date: 4/8/2017
  Time: 4:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:default-template>
	<jsp:attribute name="defaultTitle">
		Visits
  	</jsp:attribute>
	<jsp:body>

		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active">
				<a id="tabLinkCurrentVisits" href="#tabCurrentVisits" aria-controls="currentVisits" role="tab" data-toggle="tab">
					Current Visits
				</a>
			</li>
			<li role="presentation">
				<a id="tabLinkPendingVisits" href="#tabPendingVisits" aria-controls="profile" role="tab" data-toggle="tab">
					Pending Visits
				</a>
			</li>
		</ul>
		<br/>

		<!-- Tab panes -->
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="tabCurrentVisits">
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<table id="tblCurrentVisits" class="table table-striped table-bordered table-hover table-condensed">
							</table>
						</div>
					</div>
				</div>
			</div>
			<div role="tabpanel" class="tab-pane" id="tabPendingVisits">
				<div class="row">
					<div class="col-md-9">
						<div class="form-group">
							<table id="tblQueuedVisits" style="width:100%" class="table table-striped table-bordered table-hover table-condensed">
							</table>
						</div>
					</div>
					<div class="divOwner col-md-3 btn-column">
						<button type="button" class="btn btn-primary disabled" id="btnConfirmVisits">Confirm</button>
						<button type="button" class="btn btn-default disabled" id="btnAddVisit">Add</button>
						<button type="button" class="btn btn-default disabled btnSingleItem" id="btnRemoveVisit">Remove</button>
					</div>
				</div>
			</div>
		</div>

		<div class="modal fade" id="modalAddVisit" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">New Available Period</h4>
					</div>
					<div class="modal-body">

						<div class="row">
							<div class="col-md-12">
								<div class="form-group">
									<label for="inpTemporaryHousing">Housing</label>
									<select class="form-control" style="width: 100%" id="inpTemporaryHousing">
									</select>
								</div>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label for="tblAvailablePeriods">Available Periods</label>
									<table id="tblAvailablePeriods" class="table table-hover table-condensed">
									</table>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="inpPeriodFrom">From</label>
									<input type="date" class="form-control" id="inpPeriodFrom" placeholder="From">
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="inpPeriodTo">To</label>
									<input type="date" class="form-control" id="inpPeriodTo" placeholder="To">
								</div>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label for="inpCost">Cost</label>
									<input type="number" class="form-control" id="inpCost" placeholder="140">
								</div>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label for="inpPartySize">Party Size</label>
									<input type="number" class="form-control" id="inpPartySize" placeholder="12">
								</div>
							</div>
						</div>

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" id="btnCreatePendingVisit">Add</button>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->

		<script>
			var currentUserId = ${pageContext.session.getAttribute("CurrentUser").getId()};
			var currentVisitsDataTable = $("#tblCurrentVisits");
			var pendingVisitsDataTable = $("#tblQueuedVisits");
			var ReservationsDataTable = $("#tblAvailablePeriods");
			var currentVisitsTable;
			var pendingVisitsTable;
			var ReservationsTable;

			var temporaryHousingCollection;

			var pendingVisits = new Db.Uotel.Collection.BaseCollection();
			var selectedPendingVisit;

			var btnConfirmVisit = $("#btnConfirmVisits");
			var btnShowAddVisitModal = $("#btnAddVisit");
			var btnAddVisit = $("#btnCreatePendingVisit");
			var btnRemoveVisit = $("#btnRemoveVisit");

			var modalInit = false;


			init();


			function init() {
				initTabEventHandlers();
				getTemporaryHousingThatHaveReservationsAfterToday();
				initCurrentVisitsList();
				initPendingVisitsList();
			}

			function getTemporaryHousingThatHaveReservationsAfterToday() {
				var now = new Date();
				var sorters = [
					new Db.Uotel.Entities.Sorter("Id", "ASC"),
					new Db.Uotel.Entities.Sorter("Reservations.Period.To", "DESC")
				];
				var filters = [
					new Db.Uotel.Entities.Filter("Reservations.Period.To", now.format("Y-m-d"), "GREATER_EQUAL"),
					new Db.Uotel.Entities.Filter("Reservations.UserId", currentUserId)
				];

				Db.Uotel.Api.TemporaryHousing.getTemporaryHousing(1, 0, sorters, filters, []).setHandlers({
					success: function (data) {
						temporaryHousingCollection = data.collection;
						temporaryHousingCollection.packOptions("Id","Name", $("#inpTemporaryHousing"));
						btnShowAddVisitModal.removeClass("disabled");
						btnShowAddVisitModal.click(showAddVisitModal);
						$("#inpTemporaryHousing").change(handleTempHousingSelected);
						initReservationsList();
					},
					error: function (error) {
						// Do nothing for now
					}
				});
			}

			function initTabEventHandlers() {
				btnConfirmVisit.click(handleConfirmVisitsButtonClicked);
				btnAddVisit.click(handleAddPendingVisit);
				btnRemoveVisit.click(handleRemovePendingVisit);
			}

			function populateHousingInfo() {
				$(".infoName").val(theTemporaryHousing.Name).text(theTemporaryHousing.Name);
				$(".infoCategory").val(theTemporaryHousing.Category).text(theTemporaryHousing.Category);
				$(".infoAddress").val(theTemporaryHousing.Address).text(theTemporaryHousing.Address);
				$(".infoUrl").val(theTemporaryHousing.URL).text(theTemporaryHousing.URL);
				$(".infoPhoneNumber").val(theTemporaryHousing.PhoneNumber).text(theTemporaryHousing.PhoneNumber);
				$(".infoExpectedPrice").val(theTemporaryHousing.ExpectedPrice).text(theTemporaryHousing.ExpectedPrice);
				$(".infoYearBuilt").val(theTemporaryHousing.YearBuilt).text(theTemporaryHousing.YearBuilt);

				if (currentUserIsOwner()) {
					$(".divNonOwner").addClass("hidden");
					$(".divOwner").removeClass("hidden");
				} else {
					$(".infoAddress").text(theTemporaryHousing.Address.replace("\n", "<br\>"));
					$(".divOwner").addClass("hidden");
					$(".divNonOwner").removeClass("hidden");
				}
			}

			function initCurrentVisitsList() {

				currentVisitsTable = currentVisitsDataTable.DataTable({
					"sDom": 'tr' +
					'<"footer-flex"' +
					'	<l>' +
					'	<"hidden-xs hidden-sm"i>' +
					'	<p>' +
					'>',
					"searching": false,
					"order": [[ 0, "desc" ]],
					"aLengthMenu": [[25, 50, 100, 1000], [25, 50, 100, 1000]],
					"scrollY": "400px",
					"processing": true,
					"serverSide": true,
					"ajax": function (data, callback, settings) {

						var now = new Date();
						var filters = [
							new Db.Uotel.Entities.Filter("UserId", currentUserId),
							new Db.Uotel.Entities.Filter("Period.To", now.format("Y-m-d"), "GREATER_EQUAL")
						];
						var sorters = Db.Uotel.Util.parseSortersFromDataTableData(data);
						var relationsToPopulate = ["TemporaryHousing", "Period"];
						var page = Db.Uotel.Util.calculatePage(data.start, data.length);
						var perPage = data.length;
						var draw = data.draw;

						Db.Uotel.Api.Visits.getVisits(page, perPage, sorters, filters, relationsToPopulate, draw).setHandlers({
							success: function (data) {
								callback(data.toDTData([
									['Period.From', formatDate],
									['Period.To', formatDate],
									'TemporaryHousing.Name',
									'Cost',
									'PartySize'
								]));
							},
							error: function (error) {
								// Do nothing for now
							}
						});
					},
					"columnDefs": getCurrentVisitsColumnDefinitions()
				});

			}

			function reloadCurrentVisits() {
				currentVisitsTable.ajax.reload();
			}

			function initPendingVisitsList() {

				pendingVisitsTable = pendingVisitsDataTable.DataTable({
					"sDom": 'tr' +
					'<"footer-flex"' +
					'	<l>' +
					'	<"hidden-xs hidden-sm"i>' +
					'	<p>' +
					'>',
					"searching": false,
					"orderable": false,
					"select": "single",
					"aLengthMenu": [[25, 50, 100, 1000], [25, 50, 100, 1000]],
					"ajax": function (data, callback, settings) {
						callback(pendingVisits.toListResult().toDTData([
							['Period.From', formatDate],
							['Period.To', formatDate],
							'TemporaryHousing.Name',
							'Cost',
							'PartySize'
						]));
					},
					"columnDefs": getPendingVisitsColumnDefinitions()
				});

				$("#tabLinkPendingVisits").on("show.bs.tab", redrawPendingVisitsList);

				pendingVisitsTable.on( 'select', function ( e, dt, type, index ) {
					selectedPendingVisit = pendingVisits.getItemAt(index);
					handlePendingVisitRowClicked();
				}).on( 'deselect', function ( e, dt, type, index ) {
					selectedPendingVisit = null;
					handlePendingVisitRowClicked();
				});

			}

			function initReservationsList() {
				ReservationsTable = ReservationsDataTable.DataTable({
					"searching": false,
					"paging":   false,
					"ordering": false,
					"info":     false,
					"order": [],
					"select": "single",
					"ajax": function (data, callback, settings) {
						var tempHousing = temporaryHousingCollection.getFirstByProperty("Id", $("#inpTemporaryHousing").val());
						console.log(tempHousing);
						var dtData =  tempHousing.Reservations.toListResult().toDTData([
							['Period.From', formatDate],
							['Period.To', formatDate],
							'Cost'
						]);
						callback(dtData);
					},
					"columnDefs": getReservationsColumnDefinitions()
				});

				$("#modalAddVisit").on("shown.bs.modal", reloadReservationsList);

				ReservationsTable.on( 'select', function (e, dt, type, index ) {
					var ReservationsCollection = temporaryHousingCollection.getFirstByProperty("Id", $("#inpTemporaryHousing").val()).Reservations;
					var selectedReservation = ReservationsCollection.getItemAt(index);
					var selectedPeriod = selectedReservation.Period;
					$("#inpPeriodFrom").val(Db.Uotel.Util.parseDate(selectedPeriod.From).formatUtc("Y-m-d"));
					$("#inpPeriodTo").val(Db.Uotel.Util.parseDate(selectedPeriod.To).formatUtc("Y-m-d"));
					$("#inpCost").val(selectedReservation.Cost);
					ReservationsTable.row(index).deselect();
				})

			}

			function handleTempHousingSelected(){
				reloadReservationsList();
			}

			function reloadReservationsList() {
				ReservationsTable.ajax.reload();
			}

			function reloadPendingVisitsList() {
				selectedPendingVisit = null;
				handlePendingVisitRowClicked();
				console.log(pendingVisits.getCount());
				if(pendingVisits.getCount() > 0){
					btnConfirmVisit.removeClass("disabled");
				}else {
					btnConfirmVisit.addClass("disabled");
				}
				redrawPendingVisitsList();
			}

			function redrawPendingVisitsList() {
				pendingVisitsTable.ajax.reload();
			}

			function formatDate(value, entity) {
				return (value && value !== "null") ?((value instanceof Date) ? value.formatUtc('n/j/Y') : new Date(value).formatUtc('n/j/Y')) : "\u2014";
			}

			function getCurrentVisitsColumnDefinitions() {
				var aVisit = new Db.Uotel.Entities.Visit();
				var columns = [
					"Period.From",
					'Period.To',
					'TemporaryHousing.Name',
					"Cost",
					"PartySize"
				];

				return aVisit.getColumnDefinitions(columns);
			}

			function getPendingVisitsColumnDefinitions() {
				var aVisit = new Db.Uotel.Entities.Visit();
				var columns = [
					"Period.From",
					'Period.To',
					'TemporaryHousing.Name',
					"Cost",
					"PartySize"
				];

				return aVisit.getColumnDefinitions(columns);
			}

			function getReservationsColumnDefinitions() {
				var aReservation = new Db.Uotel.Entities.Reservation();
				var columns = [
					"Period.From",
					'Period.To',
					"Cost"
				];

				return aReservation.getColumnDefinitions(columns);
			}

			function handlePendingVisitRowClicked() {
				if (selectedPendingVisit === null) {
					btnRemoveVisit.addClass("disabled");
					return;
				}
				btnRemoveVisit.removeClass("disabled");
			}

			function handleConfirmVisitsButtonClicked() {
				if(pendingVisits.getCount() === 0){
					alert("There Are No Visits To Confirm");
					return
				}
				btnConfirmVisit.addClass("disabled");
				btnConfirmVisit.text("Confirming");
				Db.Uotel.Api.Visits.createVisits(pendingVisits).setHandlers({
					success: function (updatedTempHousing) {
						pendingVisits = new Db.Uotel.Collection.BaseCollection();
						$("#tabLinkCurrentVisits").tab("show");
						reloadPendingVisitsList()
						reloadCurrentVisits();
						btnConfirmVisit.text("Confirm");
					},
					error: function (error) {
						alert(error.message);
						btnConfirmVisit.removeClass("disabled");
						btnConfirmVisit.text("Confirm");
					}
				});
			}

			function showAddVisitModal() {
				if(!modalInit){
					$("#modalAddVisit").modal();
					modalInit = true
				}else {
					$("#modalAddVisit").modal("show");
				}
			}

			function getInputNewVisit() {
				var newPeriod = new Db.Uotel.Entities.Period();
				newPeriod.From = $("#inpPeriodFrom").val();
				newPeriod.To = $("#inpPeriodTo").val();

				var newVisit = new Db.Uotel.Entities.Visit();
				newVisit.UserId = currentUserId;
				newVisit.TemporaryHousingId = $("#inpTemporaryHousing").val();
				newVisit.TemporaryHousing = temporaryHousingCollection.getFirstByProperty("Id", newVisit.TemporaryHousingId);
				newVisit.Period = newPeriod;
				newVisit.Cost = $("#inpCost").val();
				newVisit.PartySize = $("#inpPartySize").val();

				return newVisit;
			}

			function handleAddPendingVisit() {
				var newVisit = getInputNewVisit();
				pendingVisits.addItem(newVisit);
				reloadPendingVisitsList();
				$("#modalAddVisit").modal('hide');
				$("#inpPeriodFrom").val("");
				$("#inpPeriodTo").val("");
				$("#inpCost").val("");
				$("#inpPartySize").val("");
			}

			function handleRemovePendingVisit() {
				if(selectedPendingVisit === null){
					return;
				}

				pendingVisits.removeItem(selectedPendingVisit);
				reloadPendingVisitsList()
			}

			function showAddAvailablePeriodModal() {
				$("#modalNewAvailablePeriod").modal();
			}

		</script>
	</jsp:body>
</t:default-template>