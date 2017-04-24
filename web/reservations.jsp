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
		Reservations
  	</jsp:attribute>
	<jsp:body>

		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active">
				<a id="tabLinkCurrentReservations" href="#tabCurrentReservations" aria-controls="currentReservations" role="tab" data-toggle="tab">
					Current Reservations
				</a>
			</li>
			<li role="presentation">
				<a id="tabLinkPendingReservations" href="#tabPendingReservations" aria-controls="profile" role="tab" data-toggle="tab">
					Pending Reservations
				</a>
			</li>
		</ul>
		<br/>

		<!-- Tab panes -->
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="tabCurrentReservations">
				<div class="row">
					<div class="col-md-12">
						<div class="form-group">
							<table id="tblCurrentReservations" class="table table-striped table-bordered table-hover table-condensed">
							</table>
						</div>
					</div>
				</div>
			</div>
			<div role="tabpanel" class="tab-pane" id="tabPendingReservations">
				<div class="row">
					<div class="col-md-9">
						<div class="form-group">
							<table id="tblQueuedReservations" style="width:100%" class="table table-striped table-bordered table-hover table-condensed">
							</table>
						</div>
					</div>
					<div class="divOwner col-md-3 btn-column">
						<button type="button" class="btn btn-primary disabled" id="btnConfirmReservations">Confirm</button>
						<button type="button" class="btn btn-default disabled" id="btnAddReservation">Add</button>
						<button type="button" class="btn btn-default disabled btnSingleItem" id="btnRemoveReservation">Remove</button>
					</div>
				</div>
			</div>
		</div>

		<div class="modal fade" id="modalAddReservation" tabindex="-1" role="dialog">
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
						</div>

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" id="btnCreatePendingReservation">Add</button>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->

		<script>
			var currentUserId = ${pageContext.session.getAttribute("CurrentUser").getId()};
			var currentReservationsDataTable = $("#tblCurrentReservations");
			var pendingReservationsDataTable = $("#tblQueuedReservations");
			var AvailablePeriodsDataTable = $("#tblAvailablePeriods");
			var currentReservationsTable;
			var pendingReservationsTable;
			var AvailablePeriodsTable;

			var temporaryHousingCollection;

			var pendingReservations = new Db.Uotel.Collection.BaseCollection();
			var selectedPendingReservation;

			var btnConfirmReservation = $("#btnConfirmReservations");
			var btnShowAddReservationModal = $("#btnAddReservation");
			var btnAddReservation = $("#btnCreatePendingReservation");
			var btnRemoveReservation = $("#btnRemoveReservation");

			var modalInit = false;


			init();


			function init() {
				initTabEventHandlers();
				getTemporaryHousingThatHasAvailablePeriodsAfterToday();
				initCurrentReservationsList();
				initPendingReservationsList();
			}

			function getTemporaryHousingThatHasAvailablePeriodsAfterToday() {
				var now = new Date();
				var sorters = [
					new Db.Uotel.Entities.Sorter("Id", "ASC"),
					new Db.Uotel.Entities.Sorter("AvailablePeriods.Period.To", "DESC")
				];
				var filters = [
					new Db.Uotel.Entities.Filter("AvailablePeriods.Period.To", now.format("Y-m-d"), "GREATER_EQUAL")
				];

				Db.Uotel.Api.TemporaryHousing.getTemporaryHousing(1, 0, sorters, filters, ["AvailablePeriods.Period"]).setHandlers({
					success: function (data) {
						temporaryHousingCollection = data.collection;
						temporaryHousingCollection.packOptions("Id","Name", $("#inpTemporaryHousing"));
						btnShowAddReservationModal.removeClass("disabled");
						btnShowAddReservationModal.click(showAddReservationModal);
						$("#inpTemporaryHousing").change(handleTempHousingSelected);
						initAvailablePeriodsList();
					},
					error: function (error) {
						// Do nothing for now
					}
				});
			}

			function initTabEventHandlers() {
				btnConfirmReservation.click(handleConfirmReservationsButtonClicked);
				btnAddReservation.click(handleAddPendingReservation);
				btnRemoveReservation.click(handleRemovePendingReservation);
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

			function initCurrentReservationsList() {

				currentReservationsTable = currentReservationsDataTable.DataTable({
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

						Db.Uotel.Api.Reservations.getReservations(page, perPage, sorters, filters, relationsToPopulate, draw).setHandlers({
							success: function (data) {
								callback(data.toDTData([
									['Period.From', formatDate],
									['Period.To', formatDate],
									'TemporaryHousing.Name',
									'Cost'
								]));
							},
							error: function (error) {
								// Do nothing for now
							}
						});
					},
					"columnDefs": getCurrentReservationsColumnDefinitions()
				});

			}

			function reloadCurrentReservations() {
				currentReservationsTable.ajax.reload();
			}

			function initPendingReservationsList() {

				pendingReservationsTable = pendingReservationsDataTable.DataTable({
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
						callback(pendingReservations.toListResult().toDTData([
							['Period.From', formatDate],
							['Period.To', formatDate],
							'TemporaryHousing.Name',
							'Cost'
						]));
					},
					"columnDefs": getPendingReservationsColumnDefinitions()
				});

				$("#tabLinkPendingReservations").on("show.bs.tab", redrawPendingReservationsList);

				pendingReservationsTable.on( 'select', function ( e, dt, type, index ) {
					selectedPendingReservation = pendingReservations.getItemAt(index);
					handlePendingReservationRowClicked();
				}).on( 'deselect', function ( e, dt, type, index ) {
					selectedPendingReservation = null;
					handlePendingReservationRowClicked();
				});

			}

			function initAvailablePeriodsList() {
				AvailablePeriodsTable = AvailablePeriodsDataTable.DataTable({
					"searching": false,
					"paging":   false,
					"ordering": false,
					"info":     false,
					"order": [],
					"select": "single",
					"ajax": function (data, callback, settings) {
						var tempHousing = temporaryHousingCollection.getFirstByProperty("Id", $("#inpTemporaryHousing").val());
						var dtData =  tempHousing.AvailablePeriods.toListResult().toDTData([
							['Period.From', formatDate],
							['Period.To', formatDate],
							'PricePerNight'
						]);
						callback(dtData);
					},
					"columnDefs": getAvailablePeriodsColumnDefinitions()
				});

				$("#modalAddReservation").on("shown.bs.modal", reloadAvailablePeriodsList);

				AvailablePeriodsTable.on( 'select', function ( e, dt, type, index ) {
					var AvailablePeriodsCollection = temporaryHousingCollection.getFirstByProperty("Id", $("#inpTemporaryHousing").val()).AvailablePeriods;
					var selectedPeriod = AvailablePeriodsCollection.getItemAt(index).Period;
					$("#inpPeriodFrom").val(Db.Uotel.Util.parseDate(selectedPeriod.From).formatUtc("Y-m-d"));
					$("#inpPeriodTo").val(Db.Uotel.Util.parseDate(selectedPeriod.To).formatUtc("Y-m-d"));
					AvailablePeriodsTable.row(index).deselect();
				})

			}

			function handleTempHousingSelected(){
				reloadAvailablePeriodsList();
			}

			function reloadAvailablePeriodsList() {
				AvailablePeriodsTable.ajax.reload();
			}

			function reloadPendingReservationsList() {
				selectedPendingReservation = null;
				handlePendingReservationRowClicked();
				console.log(pendingReservations.getCount());
				if(pendingReservations.getCount() > 0){
					btnConfirmReservation.removeClass("disabled");
				}else {
					btnConfirmReservation.addClass("disabled");
				}
				redrawPendingReservationsList();
			}

			function redrawPendingReservationsList() {
				pendingReservationsTable.ajax.reload();
			}

			function formatDate(value, entity) {
				return (value && value !== "null") ?((value instanceof Date) ? value.formatUtc('n/j/Y') : new Date(value).formatUtc('n/j/Y')) : "\u2014";
			}

			function getCurrentReservationsColumnDefinitions() {
				var aReservation = new Db.Uotel.Entities.Reservation();
				var columns = [
					"Period.From",
					'Period.To',
					'TemporaryHousing.Name',
					"Cost"
				];

				return aReservation.getColumnDefinitions(columns);
			}

			function getPendingReservationsColumnDefinitions() {
				var aReservation = new Db.Uotel.Entities.Reservation();
				var columns = [
					"Period.From",
					'Period.To',
					'TemporaryHousing.Name',
					"Cost"
				];

				return aReservation.getColumnDefinitions(columns);
			}

			function getAvailablePeriodsColumnDefinitions() {
				var aReservation = new Db.Uotel.Entities.AvailablePeriod();
				var columns = [
					"Period.From",
					'Period.To',
					"PricePerNight"
				];

				return aReservation.getColumnDefinitions(columns);
			}

			function handlePendingReservationRowClicked() {
				if (selectedPendingReservation === null) {
					btnRemoveReservation.addClass("disabled");
					return;
				}
				btnRemoveReservation.removeClass("disabled");
			}

			function handleConfirmReservationsButtonClicked() {
				if(pendingReservations.getCount() === 0){
					alert("There Are No Reservations To Confirm");
					return
				}
				btnConfirmReservation.addClass("disabled");
				btnConfirmReservation.text("Confirming");
				Db.Uotel.Api.Reservations.createReservations(pendingReservations).setHandlers({
					success: function (updatedTempHousing) {
						pendingReservations = new Db.Uotel.Collection.BaseCollection();
						$("#tabLinkCurrentReservations").tab("show");
						reloadPendingReservationsList()
						reloadCurrentReservations();
						btnConfirmReservation.text("Confirm");
					},
					error: function (error) {
						alert(error.message);
						btnConfirmReservation.removeClass("disabled");
						btnConfirmReservation.text("Confirm");
					}
				});
			}

			function showAddReservationModal() {
				if(!modalInit){
					$("#modalAddReservation").modal();
					modalInit = true
				}else {
					$("#modalAddReservation").modal("show");
				}
			}

			function getInputNewReservation() {
				var newPeriod = new Db.Uotel.Entities.Period();
				newPeriod.From = $("#inpPeriodFrom").val();
				newPeriod.To = $("#inpPeriodTo").val();

				var newReservation = new Db.Uotel.Entities.Reservation();
				newReservation.UserId = currentUserId;
				newReservation.TemporaryHousingId = $("#inpTemporaryHousing").val();
				newReservation.TemporaryHousing = temporaryHousingCollection.getFirstByProperty("Id", newReservation.TemporaryHousingId);
				newReservation.Period = newPeriod;
				newReservation.Cost = $("#inpCost").val()

				return newReservation;
			}

			function handleAddPendingReservation() {
				var newReservation = getInputNewReservation();
				pendingReservations.addItem(newReservation);
				reloadPendingReservationsList();
				$("#modalAddReservation").modal('hide');
				$("#inpPeriodFrom").val("");
				$("#inpPeriodTo").val("");
				$("#inpCost").val("");
			}

			function handleRemovePendingReservation() {
				if(selectedPendingReservation === null){
					return;
				}

				pendingReservations.removeItem(selectedPendingReservation);
				reloadPendingReservationsList()
			}

			function showAddAvailablePeriodModal() {
				$("#modalNewAvailablePeriod").modal();
			}

		</script>
	</jsp:body>
</t:default-template>