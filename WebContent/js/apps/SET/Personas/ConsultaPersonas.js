Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Personas.ConsultaPersonas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fisicaMoral = "";
	NS.equivalePersona = "";
	NS.modificar = false;
	NS.tipoPersona = "";
	NS.manejaDivision = false;
	NS.sexo = "M";
	NS.exporta = "N";
	NS.concentradora = "N";
	NS.noPersona = "";
	NS.estatus = "";
	NS.noBenef = "";
	NS.descBenef = "";
	NS.empre1="";
	var vectorCuentas = {};
	var matrizCuentas = new Array();
	NS.modificarCuentas = false;
	NS.paramCmbPersonas = '';
	NS.modificar = false;	
	
	
	//STORE
	NS.storeGridCuentasProveedor = new Ext.data.DirectStore({
		paramAsHash: false,
		root: '',
		baseParams: {noPersona: 0,
					 noEmpresa: 0,
					 tipoPersona: ''},
		paramOrder: ['noPersona', 'noEmpresa', 'tipoPersona'],		
		directFn: ConsultaPersonasAction.consultaCuentasProveedor,
		fields:
		[
		 	{name: 'idBanco'},
		 	{name: 'descBanco'},
		 	{name: 'chequera'},
		 	{name: 'descChequera'},
		 	{name: 'divisa'},
		 	{name: 'clabe'},
		 	/*{name: 'grabado'},
		 	{name: 'sucursal'},
		 	{name: 'plaza'},
		 	
		 	{name: 'chequeraAnt'},
		 	{name: 'idBancoAnt'},
		 	{name: 'usuarioModif'},
		 	{name: 'fecModif'},
		 	{name: 'noEmpresa'},
		 	{name: 'noPersona'},*/
		 	{name: 'chequeraBenef'},
		 	{name: 'swift'},
		 	{name: 'aba'},
		 	{name: 'bankTrue'},
		 	{name: 'bankCorresponding'},
		 	//{name: 'nacionalidad'},
		 	{name: 'chequeraTrue'},
		 	{name: 'bancoAnterior'},
		 	{name: 'chequeraAnterior'},
		 	{name: 'referencia'},
		 	{name: 'bancoI'},
		 	{name: 'especial'},
		 	/*
		 	{name: 'abaInter'},
		 	{name: 'swiftInter'},
		 	{name: 'abaCorresp'},
		 	{name: 'swiftCorresp'},
		 	{name: 'actualizaChequeraProv'}*/
		 	
		 	
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridCuentasProveedor, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Cuentas dadas de Alta para esta Persona');
			}
		}		
	});
	
	NS.storeLlenaComboGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaComboGrupo,
		fields:
		[
		 	{name: 'idGrupo'},
		 	{name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboGrupo, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Grupos dados de alta');
			}
		}
	});
	
	NS.storePais = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaPais,
		fields:
		[
		 	{name: 'idPais'},
		 	{name: 'descPais'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePais, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos para el Catalogo de Direccion');
			}
		}
	});
	
	
	NS.storeEstado = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaEstado,
		fields:
		[
		 	{name: 'idEstado'},
		 	{name: 'descEstado'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEstado, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos para el Catalogo de Direccion');
			}
		}
	});
	
	NS.storeTipoDireccion = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaDireccion,
		fields:
		[
		 	{name: 'idDireccion'},
		 	{name: 'descDireccion'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTipoDireccion, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos para el Catalogo de Direccion');
			}
		}
	});
	
	NS.storeTipoDireccion.load();
	
	NS.storeComboGiro = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaGiro,
		fields:
		[
		 	{name: 'idGiro'},
		 	{name: 'descGiro'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboGiro, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos para el Catalogo de Giro');
			}
		}
	});
	
	NS.storeComboCaja = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaCaja,
		fields:
		[
		 	{name: 'idCaja'},
		 	{name: 'descCaja'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboCaja, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos en el Catalogo de Cajas');
			}
		}
	});
	
	NS.storeActividadEconomica = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaActividadEconomica,
		fields:
		[
		 	{name: 'idActividadEconomica'},
		 	{name: 'descActividadEconomica'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeActividadEconomica, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Actividades Económicas dadas de alta');
			}
		}
	});
	
	NS.storeActividadGenerica = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaActividadGenerica,
		fields:
		[
		 	{name: 'idActividadGenerica'},
		 	{name: 'descActividadGenerica'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeActividadGenerica, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Actividades Genericas dadas de alta');
			}
		}
	});
	
	NS.storeCalidad = new Ext.data.DirectStore({

		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.comboCalidad,
		fields:
		[
		 	{name: 'idCalidad'},
		 	{name: 'descCalidad'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCalidad, msg: "Cargando Calidad..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos en el Catalogo de Calidad');
					
			}
		}
	});
	

	NS.storeInmueble = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.comboTipoInmueble,
		fields:
		[
		 	{name: 'idInmueble'},
		 	{name: 'descInmueble'}
		],
		listeners:
		{
			load: function(s, records)
			{
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Tipos de Inmueble dados de alta');
			}
		}
	});
	
	NS.storeFPPM = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.comboFormaPago,
		fields:
		[
		 	{name: 'idFormaPagoProv'},
		 	{name: 'descFormaPagoProv'}
		],
		listeners:
		{
			load: function(s, records)
			{
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Tipos de Inmueble dados de alta');
			}
		}
	});
	
	NS.storeFormaPagoProv = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {noPersona: NS.noPersona, noEmpresa: NS.noEmpresa},
		paramOrder: ['noPersona', 'noEmpresa'],
		directFn: ConsultaPersonasAction.comboFormaPagoProv,
		fields:
		[
		 	{name: 'idFormaPagoProv'},
		 	{name: 'descFormaPagoProv'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFormaPagoProv, msg: "Cargando Forma de Pago..."});
				/*if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Formas de Pago dadas de alta');*/
			}
		}
	});
	
	NS.storeFormaPagoProv.load();
	
	NS.storeLlenaGridMedios = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['noPersona'],
		directFn: ConsultaPersonasAction.llenaGridMedios,
		fields:
		[
		 	{name: 'descMedio'},
		 	{name: 'contactoMedio'},
		 	
		 		
		],
		listeners: {
			load: function (s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridMedios, msg: "Cargando ..."});
				
				
			}
		}
	});
	

	NS.columnaSeleccionMedios = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columnas de grid
	NS.columnasGridMedios = new Ext.grid.ColumnModel([	                                            
	    {header: 'Descripción', width: 150,  dataIndex: 'descMedio', sortable: true},
	    {header: 'Contacto', width: 300, dataIndex: 'contactoMedio', sortable: true},
	]);



NS.gridConsultaMedios = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridMedios,
		id: PF + 'gridConsultaMedios',
		name: PF + 'gridConsultaMedios',
		cm: NS.columnasGridMedios,
		sm: NS.columnaSeleccionMedios,
		width: 450,
		height: 100,

		stripeRows : true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid)
				{
					NS.txtCorreoMedios.setDisabled(true);
				}
			}	
		}
	});
NS.txtCorreoMedios = new Ext.form.TextField({
	id: PF + 'txtCorreoMedios',
	name: PF + 'txtCorreoMedios',
	x: 180,
	y: 105,
	focus: true,
	width: 220,
	listeners: {
		change: {
			fn: function(caja, valor) {
				
			}
		}
	}
});

	
	///////////////////////////////Campos Medios de Contacto/////////////////////////////////
	
	NS.lblNoPersonaMC = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 10
	});
	
	NS.lblRazonSocialMC = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 10,
	});


	NS.lblContactoPrincipalMC = new Ext.form.Label({
		text: 'Contacto Pricipal',
		x: 0,
		y: 60,
	});
	
	NS.lblFechaMC = new Ext.form.Label({
		text: 'Fecha',
		x: 310,
		y: 60,
	});
	
	NS.lblTipoMC = new Ext.form.Label({
		text: 'Tipo',
		x: 0,
		y: 110,
	});
	
	NS.lblContactoMC = new Ext.form.Label({
		text: 'Contacto',
		x: 310,
		y: 110,
	});


	NS.txtNoPersonaMC = new Ext.form.TextField({
		id: PF + 'txtNoPersonaMC',
		name: PF + 'txtNoPersonaMC',
		x: 0, 
		y: 30,
		width: 70
	});
		
	NS.txtRazonSocialMC ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialMC',
		name: PF + 'txtRazonSocialMC',
		x: 80,
		y: 30,
		width: 450
	};

	NS.txtContactoPrincipalMC ={
		xtype: 'uppertextfield',
		id: PF + 'txtContactoPrincipalMC',
		name: PF + 'txtContactoPrincipalMC',
		x: 0,
		y: 80,
		width: 300
	};

	
	NS.txtFechaIngresoMC = new Ext.form.DateField({
		id: PF + 'txtFechaIngresoMC',
		name: PF + 'txtFechaIngresoMC',
		x: 310,
		y: 80,
		width: 100,	
		format: 'd-m-Y'		
	});
		
	Ext.getCmp(PF + 'txtFechaIngresoMC').setValue(apps.SET.FEC_HOY);
	
	NS.txtTipoMC1 ={
			xtype: 'uppertextfield',
			id: PF + 'txtTipoMC1',
			name: PF + 'txtTipoMC1',
			x: 0,
			y: 130,
			width: 60
		};
	
	NS.txtTipoMC2 ={
			xtype: 'uppertextfield',
			id: PF + 'txtTipoMC2',
			name: PF + 'txtTipoMC2',
			x: 0,
			y: 160,
			width: 60
		};
	NS.txtTipoMC3 ={
			xtype: 'uppertextfield',
			id: PF + 'txtTipoMC3',
			name: PF + 'txtTipoMC3',
			x: 0,
			y: 190,
			width: 60
		};
	
	NS.txtTipoMC4 ={
			xtype: 'uppertextfield',
			id: PF + 'txtTipoMC4',
			name: PF + 'txtTipoMC4',
			x: 0,
			y: 220,
			width: 60
		};
	
	NS.txtTipoMC5 ={
			xtype: 'uppertextfield',
			id: PF + 'txtTipoMC5',
			name: PF + 'txtTipoMC5',
			x: 0,
			y: 250,
			width: 60
		};
	
	NS.txtTipoMC6 ={
			xtype: 'uppertextfield',
			id: PF + 'txtTipoMC6',
			name: PF + 'txtTipoMC6',
			x: 0,
			y: 280,
			width: 60
		};
	
	NS.txtTipoMC7 ={
			xtype: 'uppertextfield',
			id: PF + 'txtTipoMC7',
			name: PF + 'txtTipoMC7',
			x: 0,
			y: 310,
			width: 60
		};
	
	NS.txtContactoMC1 ={
			xtype: 'uppertextfield',
			id: PF + 'txtContactoMC1',
			name: PF + 'txtContactoMC1',
			x: 310,
			y: 130,
			width: 450
		};
	
	NS.txtContactoMC2 ={
			xtype: 'uppertextfield',
			id: PF + 'txtContactoMC2',
			name: PF + 'txtContactoMC2',
			x: 310,
			y: 160,
			width: 450
		};
	
	NS.txtContactoMC3 ={
			xtype: 'uppertextfield',
			id: PF + 'txtContactoMC3',
			name: PF + 'txtContactoMC3',
			x: 310,
			y: 190,
			width: 450
		};
	
	NS.txtContactoMC4 ={
			xtype: 'uppertextfield',
			id: PF + 'txtContactoMC4',
			name: PF + 'txtContactoMC4',
			x: 310,
			y: 220,
			width: 450
		};
	
	NS.txtContactoMC5 ={
			xtype: 'uppertextfield',
			id: PF + 'txtContactoMC5',
			name: PF + 'txtContactoMC5',
			x: 310,
			y: 250,
			width: 450
		};
	
	NS.txtContactoMC6 ={
			xtype: 'uppertextfield',
			id: PF + 'txtContactoMC6',
			name: PF + 'txtContactoMC6',
			x: 310,
			y: 280,
			width: 450
		};
	
	NS.txtContactoMC7 ={
			xtype: 'uppertextfield',
			id: PF + 'txtContactoMC7',
			name: PF + 'txtContactoMC7',
			x: 310,
			y: 310,
			width: 450
		};
	
	NS.cmbTipoMC1 = new Ext.form.ComboBox({
		id: PF + 'cmbTipoMC1',
		name: PF + 'cmbTipoMC1',
		x: 70,
		y: 130,
		width: 230,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaE', NS.cmbCajaE.getId());
				}
			}
		}
		
	});
	
	NS.cmbTipoMC2 = new Ext.form.ComboBox({
		id: PF + 'cmbTipoMC2',
		name: PF + 'cmbTipoMC2',
		x: 70,
		y: 160,
		width: 230,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaE', NS.cmbCajaE.getId());
				}
			}
		}
		
	});
	
	NS.cmbTipoMC3 = new Ext.form.ComboBox({
		id: PF + 'cmbTipoMC3',
		name: PF + 'cmbTipoMC3',
		x: 70,
		y: 190,
		width: 230,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaE', NS.cmbCajaE.getId());
				}
			}
		}
		
	});
	
	NS.cmbTipoMC4 = new Ext.form.ComboBox({
		id: PF + 'cmbTipoMC4',
		name: PF + 'cmbTipoMC4',
		x: 70,
		y: 220,
		width: 230,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaE', NS.cmbCajaE.getId());
				}
			}
		}
		
	});
	
	NS.cmbTipoMC5 = new Ext.form.ComboBox({
		id: PF + 'cmbTipoMC5',
		name: PF + 'cmbTipoMC5',
		x: 70,
		y: 250,
		width: 230,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaE', NS.cmbCajaE.getId());
				}
			}
		}
		
	});
	
	NS.cmbTipoMC6 = new Ext.form.ComboBox({
		id: PF + 'cmbTipoMC6',
		name: PF + 'cmbTipoMC6',
		x: 70,
		y: 280,
		width: 230,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaE', NS.cmbCajaE.getId());
				}
			}
		}
		
	});
	
	NS.cmbTipoMC7 = new Ext.form.ComboBox({
		id: PF + 'cmbTipoMC7',
		name: PF + 'cmbTipoMC7',
		x: 70,
		y: 310,
		width: 230,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaE', NS.cmbCajaE.getId());
				}
			}
		}
		
	});
	
	////////////////////////////Termina Campos MEdios de Contacto////////////////////////////////
	
/////////////////////////////////Direcciones/////////////////////////////

	NS.lblNoPersonaD = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 10
	});
	

	NS.lblRazonSocialD = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 10,
	});

	NS.lblTipoOperacion = new Ext.form.Label({
		text: 'Tipo de Dirección',
		x: 0,
		y: 100
	});

	NS.lblCalle = new Ext.form.Label({
		text: 'Calle y No',
		x: 215,
		y: 100
	});

	NS.lblColonia = new Ext.form.Label({
		text: 'Colonia',
		x: 0,
		y: 150
	});

	NS.lblCp = new Ext.form.Label({
		text: 'C.P.',
		x: 310,
		y: 150
	});

	NS.lblDelegacion = new Ext.form.Label({
		text: 'Delegación o Municipio',
		x: 380,
		y: 150
	});

	NS.lblCiudad = new Ext.form.Label({
		text: 'Ciudad',
		x: 0,
		y: 200
	});

	NS.lblEstado = new Ext.form.Label({
		text: 'Estado',
		x: 310,
		y: 200
	});

	NS.lblPais = new Ext.form.Label({
		text: 'Pais',
		x: 540,
		y: 200
	});

///////////////////TextField///////////////////////////////////

	NS.txtNoPersonaD = new Ext.form.TextField({
		id: PF + 'txtNoPersonaD',
		name: PF + 'txtNoPersonaD',
		x: 0, 
		y: 30,
		width: 70
	});
		
	NS.txtRazonSocialD ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialD',
		name: PF + 'txtRazonSocialD',
		x: 80,
		y: 30,
		width: 350
	};

	NS.txtIdTipoD = new Ext.form.TextField({
		id: PF + 'txtIdTipoD',
		name: PF + 'txtIdTipoD',
		x: 0, 
		y: 120,
		width: 50,
		listeners: {
			
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdTipoD', NS.cmbTipoDireccion.getId());
//					else
//						NS.cmbTipoDireccion.reset();
					}
				}
			}
		

	});

	NS.txtCalle= {
		xtype: 'uppertextfield',
		id: PF + 'txtCalle',
		name: PF + 'txtCalle',
		x: 215, 
		y: 120,
		width: 350
	};

	NS.txtColonia = {
		xtype: 'uppertextfield',
		id: PF + 'txtColonia',
		name: PF + 'txtColonia',
		x: 0, 
		y: 170,
		width: 300
	};

	NS.txtCp = {
		xtype: 'uppertextfield',
		id: PF + 'txtCp',
		name: PF + 'txtCp',
		x: 310, 
		y: 170,
		width: 60,
		maxLength: 5,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				if(e.keyCode ==4){ 
	 					//NS.buscar();
	 				}
	 			}
	 		},
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 5));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 5));
	 				}
	 				
	 			}
	 		},
		}
		
	};

	NS.txtDelegacion ={
		xtype: 'uppertextfield',
		id: PF + 'txtDelegacion',
		name: PF + 'txtDelegacion',
		x: 380, 
		y: 170,
		width: 300
	};
	
	NS.txtCiudad ={
		xtype: 'uppertextfield',
		id: PF + 'txtCiudad',
		name: PF + 'txtCiudad',
		x: 0, 
		y: 220,
		width: 300
	};

	NS.txtIdEstado = new Ext.form.TextField({
		id: PF + 'txtEstado',
		name: PF + 'txtEstado',
		x: 310, 
		y: 220,
		width: 60,
		listeners:
		{
		change:
		{
			fn: function(caja, valor)
			{
				if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtEstado', NS.cmbEstado.getId());
				else
					NS.cmbEstado.reset();
			}
		}
		}
	});

	
	
	NS.txtPais = new Ext.form.TextField({
		id: PF + 'txtPais',
		name: PF + 'txtPais',
		x: 540, 
		y: 220,
		width: 60,
		listeners:
		{
		change:
		{
			fn: function(caja, valor)
			{
				if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtPais', NS.cmbPais.getId());
				else
					NS.cmbPais.reset();
			}
		}
		}
		
	});

	
///////////////////////Combos//////////////////////////////



		NS.cmbTipoDireccion = new Ext.form.ComboBox({
		store: NS.storeTipoDireccion,	
		id: PF + 'cmbTipoDireccion',
		name: PF + 'cmbTipoDireccion',
		x: 55,
		y: 120,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDireccion',
		displayField: 'descDireccion',
		autocomplete: true,
		emptyText: 'Tipo de Dirección',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdTipoD', NS.cmbTipoDireccion.getId());
				}
			}
		}
	});


	NS.cmbEstado = new Ext.form.ComboBox({
		store: NS.storeEstado,
		id: PF + 'cmbEstado',
		name: PF + 'cmbEstado',
		x: 380,
		y: 220,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idEstado',
		displayField: 'descEstado',
		autocomplete: true,
		emptyText: 'Estado',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtEstado', NS.cmbEstado.getId());
				}
			}

		}
	});

	NS.storeEstado.load();

	NS.cmbPais = new Ext.form.ComboBox({
		store: NS.storePais,
		id: PF + 'cmbPais',
		name: PF + 'cmbPais',
		x: 610,
		y: 220,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idPais',
		displayField: 'descPais',
		autocomplete: true,
		emptyText: 'Pais',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtPais', NS.cmbPais.getId());
				}
			}

		}
	});
	
	NS.storePais.load();

	
//////////////////////////////Componentes Filial//////////////////////////////
	NS.lblEmpresaFI = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 0
	});
	
	NS.lblNoPersonaFI = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 50
	});
	

	NS.lblRazonSocialFI = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 50,
	});
		
	NS.lblNombreCortoFI = new Ext.form.Label({
		text: 'Nombre Corto',
		x: 450,
		y: 50
	});
		
	NS.lblRFCFI = new Ext.form.Label({
		text: 'RFC',
		x: 630,
		y: 50
	});

	NS.lblFechaIngresoFI = new Ext.form.Label({
		text: 'Fecha Ingreso',
		x: 810,
		y: 50		
	});	


	NS.lblGrupoFlujoFI = new Ext.form.Label({
		text: 'Grupo de Flujo',
		x: 0, 
		y: 100
	});
	
	NS.lblActividadEconomicaFI = new Ext.form.Label({
		text: 'Actividad Económica',
		x: 235,
		y: 100
	});
	
	NS.lblActividadGenericaFI = new Ext.form.Label({
		text: 'Actividad Genérica',
		x: 465,
		y: 100
	});
	
	
	NS.lblGiroFI = new Ext.form.Label({
		text: 'Giro',
		x: 695,
		y: 100
	});
	
	NS.lblCajaFI = new Ext.form.Label({
		text: 'Caja',
		x: 0,
		y: 150,
	});
	
	
	NS.lblCalidadFI = new Ext.form.Label({
		text: 'Calidad',
		x: 465,
		y: 150
	});	
	
	NS.lblTipoInmuebleFI = new Ext.form.Label({
		text: 'Tipo de Inmueble',
		x: 695,
		y: 150
	});
	
	NS.lblVentasAnualesFI = new Ext.form.Label({
		text: 'Ventas Anuales',
		x: 0,
		y: 200
	});

	NS.lblObjetoSocialFI = new Ext.form.Label({
		text: 'Objeto Social',
		x: 180,
		y: 200
	});
	
	//LABEL DEL PANEL DE TEF
	NS.lblContratoTefFI = new Ext.form.Label({
		text: 'Contrato Banamex TEF',		
		x: 0, 
		y: 0
	});
	
	NS.lblContratoPaymentFI = new Ext.form.Label({
		text: 'Contrato Banamex MassPayment',		
		x: 250,
		y: 0
	});
	
	NS.lblBeneficiarioFI = new Ext.form.Label({
		text: 'Beneficiario Financiamiento',		
		x: 500,
		y: 0
	});
	
	NS.lblGrupoRubroFI = new Ext.form.Label({
		text: 'Grupo Rubro',
		x: 0,
		y: 0
	});
	
	NS.lblRubroFI = new Ext.form.Label({
		text: 'Rubro',
		x: 250,
		y: 0
	});
 	
	//TEXTFIELD PARA CAMPOS DE INFORMACION GENERAL
	NS.txtEmpresaFI = new Ext.form.TextField({
		id: PF + 'txtEmpresaFI',
		name: PF + 'txtEmpresaFI',
		x: 0,
		y: 15,
		width: 300
	});
	
	NS.txtNoPersonaFI = new Ext.form.TextField({
		id: PF + 'txtNoPersonaFI',
		name: PF + 'txtNoPersonaFI',
		x: 0, 
		y: 65,
		
		width: 70
	});
	
	
	NS.txtRazonSocialFI ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialFI',
		name: PF + 'txtRazonSocialFI',
		x: 80,
		y: 65,
		width: 350
	};
	
	
	
	NS.txtNombreCortoFI ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCortoFI',
		name: PF + 'txtNombreCortoFI',
		x: 450,
		y: 65,
		width: 150
	};
	
	NS.txtRFCFI = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCFI',
		name: PF + 'txtRFCFI',
		x: 630,
		y: 65,
		width: 150
	};
	
	NS.txtFechaIngresoFI = new Ext.form.DateField({
		id: PF + 'txtFechaIngresoFI',
		name: PF + 'txtFechaIngresoFI',
		x: 810,
		y: 65,
		width: 100,	
		format: 'd-m-Y'		
	});
		
	Ext.getCmp(PF + 'txtFechaIngresoFI').setValue(apps.SET.FEC_HOY);


	NS.txtIdGrupoFI = new Ext.form.TextField({
		id: PF + 'txtIdGrupoFI',
		name: PF + 'txtIdGrupoFI',
		x: 0,
		y: 115,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdGrupoFI', NS.cmbGrupoFI.getId());
					else
						NS.cmbGrupoFI.reset();
				}				
			}
		}
	});
	
	NS.txtIdActividadEconomicaFI = new Ext.form.TextField({
		id: PF + 'txtIdActividadEconomicaFI',
		name: PF + 'txtIdActividadEconomicaFI',
		x: 235,
		y: 115,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdActividadEconomicaFI', NS.cmbActividadEconomicaFI.getId());
					else
						NS.cmbActividadEconomicaFI.reset();
				}
			}
		}
	});
	
	NS.txtIdActividadGenericaFI = new Ext.form.TextField({
		id: PF + 'txtIdActividadGenericaFI',
		name: PF + 'txtIdActividadGenericaFI',		
		x: 465,
		y: 115,
		width: 50,		
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdActividadGenericaFI', NS.cmbActividadGenericaFI.getId());
					else
						NS.cmbActividadGenericaFI.reset();
				}
			}
		}
	});
	
	NS.txtIdGiroFI = new Ext.form.TextField({
		id: PF + 'txtIdGiroFI',
		name: PF + 'txtIdGiroFI',
		x: 695,
		y: 115,
		width: 50,		
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdGiroFI', NS.cmbGiroFI.getId());
					else
						NS.cmbGiroFI.reset();
				}
			}
		}
	});
	
	NS.txtIdCajaFI = new Ext.form.TextField({
		id: PF + 'txtIdCajaFI',
		name: PF + 'txtIdCajaFI',
		x: 0,
		y: 165,
		width: 50,		
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdCajaFI', NS.cmbCajaFI.getId());
					else
						NS.cmbCajaFI.reset();
				}
			}
		}
	});
	
	NS.txtIdCalidadFI = new Ext.form.TextField({
		id: PF + 'txtIdCalidadFI',
		name: PF + 'txtIdCalidadFI',
		x: 465,
		y: 165,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdCalidadFI', NS.cmbCalidadFI.getId());
					else
						NS.cmbCalidadFI.reset();
				}
			}
		}
	});
	
	NS.txtIdInmuebleFI = new Ext.form.TextField({
		id: PF + 'txtIdInmuebleFI',
		name: PF + 'txtIdInmuebleFI',
		x: 695,
		y: 165,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdInmuebleFI', NS.cmbInmuebleFI.getId());
					else
						NS.cmbInmuebleFI.reset();
				}
 			}
		}
	});
	
	NS.txtVentasAnualesFI ={
		xtype: 'uppertextfield',
		id: PF + 'txtVentasAnualesFI',
		name: PF + 'txtVentasAnualesFI',
		x: 0,
		y: 215,
		width: 150		
	};
	
	NS.txtObjetoSocialFI ={
		xtype: 'uppertextfield',
		id: PF + 'txtObjetoSocialFI',
		name: PF + 'txtObjetoSocialFI',
		x: 180,
		y: 215,
		width: 150
	};
	
	
	NS.txtNoBenefFI ={
		xtype: 'uppertextfield',
		id: PF + 'txtNoBenefFI',
		name: PF + 'txtNoBenefFI',
		x: 500,
		y: 20,		
		width: 60,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoBenefFI', NS.cmbBenefFI.getId());
					else
						NS.cmbBenefFI.reset();
				}
			}
		}
	};
	
	NS.txtGrupoRubroFI = new Ext.form.TextField({
		id: PF + 'txtGrupoRubroFI',
		name: PF + 'txtGrupoRubroFI',
		x: 0,
		y: 20,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined) {
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtGrupoRubroFI', NS.cmbGrupoRubroFI.getId());
					
						if(comboValue == null || comboValue == undefined || comboValue == '')
							Ext.getCmp(PF + 'txtGrupoRubroFI').setValue('');
						else {
							Ext.getCmp(NS.txtRubroFI.getId()).setValue('');
							Ext.getCmp(NS.cmbRubroFI.getId()).setValue('');
							NS.storeRubroEgreso.removeAll();
							NS.storeRubroEgreso.baseParams.idGrupo = parseInt(NS.txtGrupoRubroFI.getValue());									
							NS.storeRubroEgreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresaFI').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF + 'txtNoEmpresaFI').getValue());
							NS.storeRubroEgreso.load();
						}
					}else {
						NS.cmbGrupoRubroFI.reset();
						NS.cmbRubroFI.reset();
						Ext.getCmp(PF + 'txtRubroFI').setValue('');
						Ext.getCmp(PF + 'txtGrupoRubroE').setValue('');
					}
				}
			}
		}
	});
	
	NS.txtRubroFI = new Ext.form.TextField({
		id: PF + 'txtRubroFI',
		name: PF + 'txtRubroFI',
		x: 250,
		y: 20,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined) {
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtRubroFI', NS.cmbRubroFI.getId());

						if(comboValue == null || comboValue == undefined || comboValue == '')
							Ext.getCmp(PF + 'txtRubroFI').setValue('');
					}else
						NS.cmbRubroFI.reset();
				}
			}
		}
	});
////////////////////////////////Terminan los TextFields//////////////////////////////////

//////Combosss/////////////////////////////////////////
	
	NS.cmbGrupoFI = new Ext.form.ComboBox({
		//store: NS.storeLlenaComboGrupo,
		id: PF + 'cmbGrupoFI',
		name: PF + 'cmbGrupoFI',
		x: 55,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idGrupo',
		//displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo de Flujo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGrupoFI', NS.cmbGrupoFI.getId());
				}
			}
		}
	});
	
	NS.cmbActividadEconomicaFI = new Ext.form.ComboBox({
		//store: NS.storeActividadEconomica,
		id: PF + 'cmbActividadEconomicaFI',
		name: PF + 'cmbActividadEconomicaFI',
		x: 295,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idActividadEconomica',
		//displayField: 'descActividadEconomica',
		autocomplete: true,
		emptyText: 'Actividad Económica',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdActividadEconomicaFI', NS.cmbActividadEconomicaFI.getId());					
				}
			}
		}
	});
	
	NS.cmbActividadGenericaFI = new Ext.form.ComboBox({
		//store: NS.storeActividadGenerica,	
		id: PF + 'cmbActividadGenericaFI',
		name: PF + 'cmbActividadGenericaFI',
		x: 525,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idActividadGenerica',
		//displayField: 'descActividadGenerica',
		autocomplete: true,
		emptyText: 'Actividad Generica',
		triggerAction: 'all',
		value: '',
		visible: false,		
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdActividadGenericaFI', NS.cmbActividadGenericaFI.getId());
				}
			}
		}
	});
	
	
	
	NS.cmbGiroFI = new Ext.form.ComboBox({
		//store: NS.storeComboGiro,
		id: PF + 'cmbGiroFI',
		name: PF + 'cmbGiroFI',
		x: 755,
		y: 115,		
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idGiro',
		//displayField: 'descGiro',
		autocomplete: true,
		emptyText: 'Giro',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGiroFI', NS.cmbGiroFI.getId());
				}
			}
		}
	});
	
	NS.cmbCajaFI = new Ext.form.ComboBox({
		//store: NS.storeComboCaja,
		id: PF + 'cmbCajaFI',
		name: PF + 'cmbCajaFI',
		x: 60,
		y: 165,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idCaja',
		//displayField: 'descCaja',
		autocomplete: true,
		emptyText: 'Caja',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaFI', NS.cmbCajaFI.getId());
				}
			}
		}
		
	});
		
	
	
	
	
	NS.cmbCalidadFI = new Ext.form.ComboBox({
		//store: NS.storeCalidad,
		id: PF + 'cmbCalidadFI',
		name: PF + 'cmbCalidadFI',
		x: 525,
		y: 165,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idCalidad',
		//displayField: 'descCalidad',
		autocomplete: true,
		emptyText:  'Calidad de Empresa',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdCalidadFI', NS.cmbCalidadFI.getId());
				}
			}
		}
	});
	
	NS.cmbInmuebleFI = new Ext.form.ComboBox({
		//store: NS.storeInmueble,
		id: PF + 'cmbInmuebleFI',
		name: PF + 'cmbInmuebleFI',
		x: 755,
		y: 165,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idInmueble',
		//displayField: 'descInmueble',
		autocomplete: true,
		emptyText: 'Tipo de Inmueble',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdInmuebleFI', NS.cmbInmuebleFI.getId());
				}
			}
		}
	});

	
	NS.cmbContratoTefFI = new Ext.form.ComboBox({
		//store: NS.storeComboTef,
		id: PF + 'cmbContratoTefFI',
		name: PF + 'cmbContratoTefFI',
		x: 0,
		y: 20,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idContrato',
		//displayField: 'descContrato',
		autocomplete: true,
		emptyText: 'Contrato TEF',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{}
			}
		}
	});
	
	NS.cmbContratoPaymentFI = new Ext.form.ComboBox({
		///store: NS.storeComboPayment,
		id: PF + 'cmbContratoPaymentFI',
		name: PF + 'cmbContratoPaymentFI',
		x: 250,
		y: 20,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idContratoPayment',
		//displayField: 'descContratoPayment',
		autocomplete: true,
		emptyText: 'Contrato MassPayment',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{}
			}
		}
	});
	
	NS.cmbBenefFI = new Ext.form.ComboBox({
		store: NS.storeComboBenef,
		id: PF + 'cmbBenefFI',
		name: PF + 'cmbBenefFI',
		x: 570,
		y: 20,		
		width: 250,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'noBenef',
		displayField: 'descBenef',
		autocomplete: true,
		emptyText: 'Parte del Beneficiario a Buscar',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtNoBenefFI', NS.cmbBenefFI.getId());
				}
			}
		}
	});
	
	NS.cmbGrupoRubroFI = new Ext.form.ComboBox({
		store: NS.storeGrupoEgreso,
		id: PF + 'cmbGrupoRubroFI',
		name: PF + 'cmbGrupoRubroFI',
		x: 55,
		y: 20,		
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtGrupoRubroFI', NS.cmbGrupoRubroFI.getId());
					Ext.getCmp(NS.txtRubroFI.getId()).setValue('');
					Ext.getCmp(NS.cmbRubroFI.getId()).setValue('');
					NS.storeRubroEgreso.removeAll();
					NS.storeRubroEgreso.baseParams.idGrupo = parseInt(NS.txtGrupoRubroFI.getValue());									
					NS.storeRubroEgreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresaFI').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF + 'txtNoEmpresaFI').getValue());
					NS.storeRubroEgreso.load();
				}
			},
			change:{
			    fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtGrupoRubroFI' ).setValue('');
         				Ext.getCmp(NS.txtRubroFI.getId()).setValue('');
						Ext.getCmp(NS.cmbRubroFI.getId()).setValue('');
						NS.storeRubroEgreso.removeAll();       				
         			}else{
         				Ext.getCmp( PF + 'txtRubroFI' ).forceSelection= true; 
         			}	
		    	}//end function
			}//end change
		}
	});
	
	NS.cmbRubroFI= new Ext.form.ComboBox({
		store: NS.storeRubroEgreso,
		id: PF + 'cmbRubroFI',
		name: PF + 'cmbRubroFI',
		x: 305,
		y: 20,
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtRubroFI', NS.cmbRubroE.getId());
				}
			},
			change:{
				fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtRubroFI' ).setValue('');
					}else{
						Ext.getCmp( PF + 'txtRubroFI' ).forceSelection= true;
					}
				}//end function
			}//end change
		}
	});

	NS.optExportaFI = new Ext.form.RadioGroup({
		id: PF + 'optExportaFI',
		name: PF + 'optExportaFI',
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'SI', name: 'optExportaSel', inputValue: 0, checked: true,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.exporta = 'S'
		 					}
		 				}
		 			}
 				}
		 	},
		 	{boxLabel: 'NO', name: 'optExportaSel', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.exporta = 'N';
		 					}
		 				}
		 			}
		 		}
		 	}
		]
	});

	NS.optConcentradoraFI = new Ext.form.RadioGroup({
		id: PF + 'optConcentradoraFI',
		name: PF + 'optConcentradoraFI',
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'SI', name: 'optConcentradoraSel', inputValue: 0, checked: true,
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							NS.concentradora = 'S'
		 						}
		 					}
		 				}
		 			}
		 	},
		 	{boxLabel: 'NO', name: 'optConcentradoraSel', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.concentradora = 'N'
		 					}
		 				}
		 	
		 			}
		 		}
		 	}
		]
	});
	/////////////////////////Termina componentes Filial/////////////////////////////
	
	///////////////////////////Componentes Casa de Cambio//////////////////////////////////////////
	NS.lblEmpresaK = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 0
	});
	
	NS.lblNoPersonaK = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 50
	});
	

	NS.lblRazonSocialK = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 50,
	});
		
	NS.lblNombreCortoK = new Ext.form.Label({
		text: 'Nombre Corto',
		x: 450,
		y: 50
	});
		
	NS.lblRFCK = new Ext.form.Label({
		text: 'RFC',
		x: 630,
		y: 50
	});

	NS.lblFechaIngresoK = new Ext.form.Label({
		text: 'Fecha Ingreso',
		x: 810,
		y: 50		
	});
	//TEXTFIELD PARA CAMPOS DE INFORMACION GENERAL
	NS.txtEmpresaK = new Ext.form.TextField({
		id: PF + 'txtEmpresaK',
		name: PF + 'txtEmpresaK',
		x: 0,
		y: 15,
		width: 300
	});
	
	NS.txtNoPersonaK = new Ext.form.TextField({
		id: PF + 'txtNoPersonaK',
		name: PF + 'txtNoPersonaK',
		x: 0, 
		y: 65,
		readOnly: true,
		width: 70
	});
	
	
	NS.txtRazonSocialK ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialK',
		name: PF + 'txtRazonSocialK',
		x: 80,
		y: 65,
		width: 350
	};

	NS.txtNombreCortoK ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCortoK',
		name: PF + 'txtNombreCortoK',
		x: 450,
		y: 65,
		width: 150,
		maxLength: 20,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				if(e.keyCode ==18){ 
	 					NS.buscar();
	 				}
	 			}
	 		},
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 				
	 			}
	 		},
	 		
		}
	};
	
	NS.txtRFCK = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCK',
		name: PF + 'txtRFCK',
		regex: /^(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))/,
		regexText: 'Ingrese un rfc valido',
		x: 630,
		y: 65,
		width: 150
	};
	
	NS.txtFechaIngresoK = new Ext.form.DateField({
		id: PF + 'txtFechaIngresoK',
		name: PF + 'txtFechaIngresoK',
		x: 810,
		y: 65,
		width: 100,	
		format: 'd-m-Y'		
	});
		
	Ext.getCmp(PF + 'txtFechaIngresoK').setValue(apps.SET.FEC_HOY);
	//////////////////////////Fin componentes Casa de Cambio////////////////////////////////////////
	
///////////////////////////Componentes Casa de Cambio Cuenta de Proveedores//////////////////////////////////////////
	NS.lblEmpresaCP = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 10
	});
//	
	NS.lblNoPersonaCP = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 50
	});

	NS.lblRazonSocialCP = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 50,
	});
	
//	//TEXTFIELD PARA CAMPOS DE INFORMACION GENERAL
	NS.txtEmpresaCP = new Ext.form.TextField({
		id: PF + 'txtEmpresaCP',
		name: PF + 'txtEmpresaCP',
		x: 80,
		y: 10,
		width: 400
	});

	NS.txtNoPersonaCP = new Ext.form.TextField({
		id: PF + 'txtNoPersonaCP',
		name: PF + 'txtNoPersonaCP',
		x: 0, 
		y: 65,
		readOnly: true,
		width: 70
	});

	
	NS.txtRazonSocialCP ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialCP',
		name: PF + 'txtRazonSocialCP',
		x: 80,
		y: 65,
		width: 400
	};
	////Cuenta proveedores panel 2
	
	NS.chkBancoNacional = new Ext.form.Checkbox({
		xtype: 'checkbox',
 		id: PF + 'chkBancoNacional',
 		name: PF + 'chkBancoNacional',
 		x:0,
 		y: 0,
 		boxLabel: 'Banco Nacional',
 		listeners: {
 			check: {
 				fn: function(checkBox, valor) {
 					if(NS.chkBancoExtranjero.getValue()==true){
 						NS.chkBancoExtranjero.setValue(false);
 					}	
 				}
 			}
 		}
	});
	NS.chkBancoExtranjero = new Ext.form.Checkbox({
		xtype: 'checkbox',
 		id: PF + 'chkBancoExtranjero',
 		name: PF + 'chkBancoExtranjero',
 		x: 100,
 		y: 0,
 		boxLabel: 'Banco Extanjero',
 		listeners: {
 			check: {
 				fn: function(checkBox, valor) {
 					if(NS.chkBancoNacional.getValue()==true){
 						NS.chkBancoNacional.setValue(false);
 					}
 				}
 			}
 		}
	});
	NS.lblListaCP1 = new Ext.form.Label({
		text: 'Escriba Sobre este listado',
		x:40,
		y: 30
	});
	
	NS.lblBancoCP1 = new Ext.form.Label({
		text: 'Banco',
		x: 0,
		y: 30
	});
//	
	NS.lblDivisaCP1 = new Ext.form.Label({
		text: 'Divisa',
		x: 200,
		y: 30
	});
	NS.lblChequeraCP1 = new Ext.form.Label({
		text: 'Chequera',
		x: 270,
		y: 30
	});
	
	NS.lblDescChequeraCP1 = new Ext.form.Label({
		text: 'Descripcion Chequera',
		x: 480,
		y: 30
	});


	NS.lblSucursalCP1 = new Ext.form.Label({
		text: 'Sucursal',
		x: 0,
		y: 80,
	});
	
	
	NS.lblPlazaCP1 = new Ext.form.Label({
		text: 'Plaza',
		x: 70,
		y: 80,
	});
	
	NS.lblClabeCP1 = new Ext.form.Label({
		text: 'CLABE',
		x: 155,
		y: 80,
	});
	
	
	NS.lblBancoAntCP1 = new Ext.form.Label({
		text: 'Banco Anterior',
		x: 270,
		y: 80
	});
	
	NS.lblChequeraAntCP1 = new Ext.form.Label({
		text: 'Chequera Anterior',
		x: 480,
		y: 80
	});
	//CP12
	NS.lblTipoCambioCP12 = new Ext.form.Label({
		text: 'Tipo Cambio',
		x: 0,
		y: 50
	});
	
	
//	//TEXTFIELD PARA CAMPOS DE INFORMACION GENERAL CP
	//stores>>>>>>>>>>>>>>>>>>>
	NS.storeLlenaComboDivisaCP = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaComboDivisaCP,
		idProperty: 'divisa',
		fields: [
			{name: 'divisa'},
			{name: 'idDivisa'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboDivisaCP, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'Error al cargar las divisas');
			}
		}
	});
	NS.storeLlenaComboDivisaCP.load();
	//fin de Stores CP
	
	NS.txtNoBancoCP1 = new Ext.form.TextField({
		id: PF + 'txtNoBancoCP1',
		name: PF + 'txtNoBancoCP1',
		x: 0,
		y: 50,
		width: 30
	});
	NS.cmbBancoCP = new Ext.form.ComboBox({
		store: NS.storeLlenaComboTipoPersona,
		id: PF + 'cmbBancoCP',
		name: PF + 'cmbBancoCP',
		x: 40,
		y: 50,		
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: '',
		displayField: 'descTipoPersona',
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					//Fwrk.Util.updateComboToTextField(PF + 'txtNoBancoCP1', NS.cmbDivisaCP.getId());
				}
			}
		}
	});
	NS.cmbDivisaCP = new Ext.form.ComboBox({
		store: NS.storeLlenaComboDivisaCP,
		id: PF + 'cmbDivisaCP',
		name: PF + 'cmbDivisaCP',
		x: 200,
		y: 50,		
		width: 50,	
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'divisa',
		displayField: 'idDivisa',
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
//					BFwrk.Util.updateComboToTextField(PF + 'txtTipoPersona', NS.cmbTipoPersona.getId());
//					if (Ext.getCmp(PF + 'txtTipoPersona').getValue()=='P') {
//						
//						Ext.getCmp(PF + 'excel').setDisabled(true);
//						Ext.getCmp(PF + 'crearN').setDisabled(true);
//						Ext.getCmp(PF + 'eliminar').setDisabled(true);
//						
//					} else {
//						Ext.getCmp(PF + 'excel').setDisabled(false);
//						Ext.getCmp(PF + 'crearN').setDisabled(false);
//						Ext.getCmp(PF + 'eliminar').setDisabled(false);
//					}
				}
			}
		}
	});


	NS.txtChequeraCP1 = new Ext.form.TextField({
		id: PF + 'txtChequeraCP1',
		name: PF + 'txtChequeraCP1',
		x: 270, 
		y: 50,
		width: 200
	});

	
	NS.txtDescChequeraCP1 ={
		xtype: 'uppertextfield',
		id: PF + 'txtDescChequeraCP1',
		name: PF + 'txtDescChequeraCP1',
		x: 480,
		y: 50,
		width: 200
	};
	
	
	NS.txtSucursalCP1 ={
		xtype: 'uppertextfield',
		id: PF + 'txtSucursalCP1',
		name: PF + 'txtSucursalCP1',
		x: 0,
		y: 100,
		width: 40
	};
	NS.txtPlazaCP1 ={
		xtype: 'uppertextfield',
		id: PF + 'txtPlazaCP1',
		name: PF + 'txtPlazaCP1',
		x: 65,
		y: 100,
		width: 40
	};
	
	NS.txtClabeCP1 ={
		xtype: 'uppertextfield',
		id: PF + 'txtClabeCP1',
		name: PF + 'txtClabeCP1',
		x: 130,
		y: 100,
		width: 100
	};

	
	
	NS.txtBancoAntCP1 = new Ext.form.TextField({
		id: PF + 'txtBancoAntCP1',
		name: PF + 'txtBancoAntCP1',
		x: 270, 
		y: 100,
		width: 200
	});

	
	NS.txtChequeraAntCP1 ={
		xtype: 'uppertextfield',
		id: PF + 'txtChequeraAntCP1',
		name: PF + 'txtChequeraAntCP1',
		x: 480,
		y: 100,
		width: 200
	};
	//CP12
	NS.txtAbaSwiftCP12 ={
			xtype: 'uppertextfield',
			id: PF + 'txtAbaSwiftCP12',
			name: PF + 'txtAbaSwiftCP12',
			x: 105,
			y: 0,
			width: 120
	};
	NS.chkSwift = new Ext.form.Checkbox({
		xtype: 'checkbox',
 		id: PF + 'chkSwift',
 		name: PF + 'chkSwift',
 		x: 50,
 		y: 0,
 		boxLabel: 'SWIFT',
 		listeners: {
 			check: {
 				fn: function(checkBox, valor) {
 						
 				}
 			}
 		}
	});
	NS.chkAba = new Ext.form.Checkbox({
		xtype: 'checkbox',
 		id: PF + 'chkAba',
 		name: PF + 'chkAba',
 		x:0,
 		y: 0,
 		boxLabel: 'ABA',
 		listeners: {
 			check: {
 				fn: function(checkBox, valor) {
 						
 				}
 			}
 		}
	});
	

	
	NS.cmbTipoCambioCP12 = new Ext.form.ComboBox({
//		store: ,
		id: PF + 'cmbTipoCambioCP12',
		name: PF + 'cmbTipoCambioCP12',
		x: 75,
		y: 50,		
		width: 150,	
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: '',
		displayField: '',
		autocomplete: true,
		emptyText: '',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {				
				}
			}
		}
	});
	
	//////////////////////////Fin componentes Casa de Cambio Cuenta de Proveedores////////////////////////////////////////
	
	
	
	
	///////////////////////////Componentes Proveedores Morales////////////////////////////////////////////
	
	NS.lblNoPersonaPM = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 50
	});
	
	NS.lblEquivalePerPM = new Ext.form.Label({
		text: 'Equivale Persona',
		x: 0,
		y: 0
	});
	
	NS.lblReferenciaPM = new Ext.form.Label({
		text: 'Referencia de pago',
		x: 140,
		y: 0
	});
	
	NS.lblEmpresaPM = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 50
	});
	

	NS.lblRazonSocialPM = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 50,
	});
		
	NS.lblNombreCortoPM = new Ext.form.Label({
		text: 'Nombre Corto',
		x: 450,
		y: 50
	});
		
	NS.lblRFCPM = new Ext.form.Label({
		text: 'RFC',
		x: 630,
		y: 50
	});

	NS.lblFechaIngresoPM = new Ext.form.Label({
		text: 'Fecha Ingreso',
		x: 810,
		y: 50		
	});
	
	NS.lblFormaPagoProvPM = new Ext.form.Label({
		text: 'Forma de Pago Prov',
		x: 350,
		y: 100
		
	});
	
	NS.lblFormaPagoProvPM = new Ext.form.Label({
		text: 'Forma de Pago Prov',
		x: 350,
		y: 100
		
	});
	
	NS.lblFax = new Ext.form.Label({
		text: 'Fax',
		x: 0,
		y: 100
		
	});
	
	NS.txtFax = new Ext.form.TextField({
		id: PF + 'txtFax',
		name: PF + 'txtFax',
		x: 0,
		y: 120,
		width: 130
	});
	
	NS.lblTelefono = new Ext.form.Label({
		text: 'Telefono',
		x: 160,
		y: 100
		
	});
	
	NS.txtTelefono = new Ext.form.NumberField({
		id: PF + 'txtTelefono',
		name: PF + 'txtTelefono',
		x: 160,
		y: 120,
		width: 130
	});
	
	
	NS.lblCorreo = new Ext.form.Label({
		text: 'Correo',
		x: 330,
		y: 100
		
	});
	
	NS.txtCorreo = new Ext.form.NumberField({
		id: PF + 'txtCorreo',
		name: PF + 'txtCorreo',
		x: 330,
		y: 120,
		width: 330
	});
	
	//Grid Consulta Medios>>>>>>>>>>>>>>>>
	
	
	
	//Fin Grid Consulta Medios<<<<<<<<<<<<<<



	//TEXTFIELD PARA CAMPOS DE INFORMACION GENERAL
	NS.txtEmpresaPM = new Ext.form.TextField({
		id: PF + 'txtEmpresaPM',
		name: PF + 'txtEmpresaPM',
		x: 0,
		y: 65,
		width: 70
	});
	
	NS.txtEquivalePerPM = new Ext.form.TextField({
		id: PF + 'txtEquivalePerPM',
		name: PF + 'txtEquivalePerPM',
		x: 0, 
		y: 20,
		width: 120
	});
	
	NS.txtReferenciaPM = new Ext.form.TextField({
		id: PF + 'txtReferenciaPM',
		name: PF + 'txtReferenciaPM',
		x: 140, 
		y: 20,
		width: 140
	});
		
	NS.txtRazonSocialPM ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialPM',
		name: PF + 'txtRazonSocialPM',
		x: 80,
		y: 65,
		width: 350
	};
	
	
	NS.txtNombreCortoPM ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCortoPM',
		name: PF + 'txtNombreCortoPM',
		x: 450,
		y: 65,
		width: 150
	};
	
	NS.txtRFCPM = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCPM',
		name: PF + 'txtRFCPM',
		x: 630,
		y: 65,
		width: 150
	};
	
	NS.txtFechaIngresoPM = new Ext.form.DateField({
		id: PF + 'txtFechaIngresoPM',
		name: PF + 'txtFechaIngresoPM',
		x: 810,
		y: 65,
		width: 100,	
		format: 'd-m-Y'		
	});
		
	Ext.getCmp(PF + 'txtFechaIngresoPM').setValue(apps.SET.FEC_HOY);
	
	NS.txtIdFormaPagoProvPM = new Ext.form.TextField({
		id: PF + 'txtIdFormaPagoProvPM',
		name: PF + 'txtIdFormaPagoProvPM',
		x: 350,
		y: 120,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdFormaPagoProvPM', NS.cmbFormaPagoProvPM.getId());
					else
						NS.cmbFormaPagoProvPM.reset();
				}
			}
		}
	});
	
	NS.cmbFormaPagoProvPM = new Ext.form.ComboBox({
		store: NS.storeFPPM,
		id: PF + 'cmbFormaPagoProvPM',
		name: PF + 'cmbFormaPagoProvPM',
		x: 410,
		y: 120,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idFormaPagoProv',
		displayField: 'descFormaPagoProv',
		autocomplete: true,
		emptyText: 'Forma de Pago',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdFormaPagoProvPM', NS.cmbFormaPagoProvPM.getId());
				}
			}
		}
	});
	
	NS.storeFPPM.load();
	///////////////////////////Fin componentes Proveedores Morales//////////////////////////////////////////
	
	//////////////////////////Componentes Proveedores Fisicos//////////////////////////////////////////////
	NS.lblEmpresaPF = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 50
	});
	
	NS.lblNoPersonaPF = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 50
	});
	
	NS.lblPaternoPF = new Ext.form.Label({
		text: 'Paterno',
		x: 100,
		y: 50
	});

	
	
	NS.lblMaternoPF = new Ext.form.Label({
		text: 'Materno',
		x: 210,
		y: 50
	});

	NS.lblNombrePF = new Ext.form.Label({
		text: 'Nombre',
		x: 320,
		y: 50
	});
		
	NS.lblNombreCortoPF = new Ext.form.Label({
		text: 'Nombre Corto',
		x: 450,
		y: 50
	});
		
	NS.lblRFCPF = new Ext.form.Label({
		text: 'RFC',
		x: 630,
		y: 50
	});
	
	NS.lblFormaPagoProvF = new Ext.form.Label({
		text: 'Forma de Pago Prov',
		x: 640,
		y: 100
		
	});

	//TEXTFIELD PARA CAMPOS DE INFORMACION GENERAL
	NS.txtEmpresaPF = new Ext.form.TextField({
		id: PF + 'txtEmpresaPF',
		name: PF + 'txtEmpresaPF',
		x: 0, 
		y: 65,
		width: 70
	});
	
	NS.txtNoPersonaPF = new Ext.form.TextField({
		id: PF + 'txtNoPersonaPF',
		name: PF + 'txtNoPersonaPF',
		x: 0, 
		y: 65,
		width: 70
	});
	
	NS.txtPaternoPF = {
		xtype: 'uppertextfield',
		id: PF + 'txtPaternoPF',
		name: PF + 'txtPaternoPF',
		x: 100,
		y: 65,
		width: 100
	};
	
	NS.txtMaternoPF = {
		xtype: 'uppertextfield',
		id: PF + 'txtMaternoPF',
		name: PF + 'txtMaternoPF',
		x: 210,
		y: 65,
		width: 100
	};
	
	NS.txtNombrePF = {
		xtype: 'uppertextfield',
		id: PF + 'txtNombrePF',
		name: PF + 'txtNombrePF',
		x: 320,
		y: 65,
		width: 100
	};
	
	NS.txtNombreCortoPF ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCortoPF',
		name: PF + 'txtNombreCortoPF',
		x: 450,
		y: 65,
		width: 150
	};
	
	NS.txtRFCPF = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCPF',
		name: PF + 'txtRFCPF',
		x: 630,
		y: 65,
		width: 150
	};
	
	NS.txtIdFormaPagoProvF = new Ext.form.TextField({
		id: PF + 'txtIdFormaPagoProvF',
		name: PF + 'txtIdFormaPagoProvF',
		x: 640,
		y: 120,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdFormaPagoProvF', NS.cmbFormaPagoProvF.getId());
					else
						NS.cmbFormaPagoProvF.reset();
				}
			}
		}
	});	
		

	NS.cmbGrupoPF = new Ext.form.ComboBox({
		store: NS.storeLlenaComboGrupo,
		id: PF + 'cmbGrupoPF',
		name: PF + 'cmbGrupoPF',
		x: 55,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo de Flujo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGrupoPF', NS.cmbGrupoPF.getId());
				}
			}
		}
	});

NS.cmbGrupoRubroPF = new Ext.form.ComboBox({
		store: NS.storeGrupoEgreso,
		id: PF + 'cmbGrupoRubroPF',
		name: PF + 'cmbGrupoRubroPF',
		x: 55,
		y: 20,		
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtGrupoRubroPF', NS.cmbGrupoRubroPF.getId());
					Ext.getCmp(NS.txtRubroPF.getId()).setValue('');
					Ext.getCmp(NS.cmbRubroPF.getId()).setValue('');
					NS.storeRubroEgreso.removeAll();
					NS.storeRubroEgreso.baseParams.idGrupo = parseInt(NS.txtGrupoRubroPF.getValue());									
					NS.storeRubroEgreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresaPF').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF + 'txtNoEmpresa').getValue());
					NS.storeRubroEgreso.load();
				}
			},
			change:{
			    fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtGrupoRubroPF' ).setValue('');
         				Ext.getCmp(NS.txtRubroPF.getId()).setValue('');
						Ext.getCmp(NS.cmbRubroPF.getId()).setValue('');
						NS.storeRubroEgreso.removeAll();       				
         			}else{
         				Ext.getCmp( PF + 'txtRubroPF' ).forceSelection= true; 
         			}	
		    	}//end function
			}//end change
		}
	});
	
	NS.cmbRubroPF = new Ext.form.ComboBox({
		store: NS.storeRubroEgreso,
		id: PF + 'cmbRubroPF',
		name: PF + 'cmbRubroPF',
		x: 305,
		y: 20,
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtRubroPF', NS.cmbRubroPF.getId());
				}
			},
			change:{
				fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtRubroPF' ).setValue('');
					}else{
						Ext.getCmp( PF + 'txtRubroPF' ).forceSelection= true;
					}
				}//end function
			}//end change
		}
	});

NS.optRadioSexoPF = new Ext.form.RadioGroup({
		id: PF + 'optRadioSexoPF',
		name: PF + 'optRadioSexoPF',		
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Masculino', name: 'optSeleccion', inputValue: 0, checked: true,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.sexo = 'M'
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{boxLabel: 'Femenino', name: 'optSeleccion', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.sexo = 'F'
		 					}
		 				}
		 			}
	 			}
		 	}
		]
	});

	NS.panelSexoPF = new Ext.form.FieldSet({
		id: PF + 'panelSexoPF',
		name: PF + 'panelSexoPF',
		title: 'Sexo:',
		x: 0,
		y: 100,
		width: 200,
		height: 40,
		layout: 'absolute',		
		items:
		[
		 	NS.optRadioSexoPF
		]
	});

	NS.cmbFormaPagoProvF = new Ext.form.ComboBox({
		//store: NS.storeFormaPagoProv,
		id: PF + 'cmbFormaPagoProvF',
		name: PF + 'cmbFormaPagoProvF',
		x: 700,
		y: 120,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
//		valueField: 'idFormaPagoProvF',
//		displayField: 'descFormaPagoProvF',
		autocomplete: true,
		emptyText: 'Forma de Pago',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdFormaPagoProvF', NS.cmbFormaPagoProvF.getId());
				}
			}
		}
	});
	
	NS.panelBotonesMOK = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMOK',
		name: PF + 'panelBotonesMOK',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 
		{
				xtype: 'button',
				text: 'Cuenta de Proveedores',
				x: 450,
				y: 5,
				width: 80,
				height: 22,
				listeners: {
					click: {
						fn: function(e) {
							NS.panelCamposK.setVisible(false);
							NS.panelBotonesMOK.setVisible(false);
							NS.panelCamposCP.setVisible(true);
							NS.panelCamposCP1.setVisible(true);
							NS.panelCamposGrid.setVisible(true);
							NS.chkBancoExtranjero.setDisabled(true);
							NS.chkBancoNacional.setDisabled(true);
							NS.chkBancoNacional.setValue(true);
							Ext.getCmp(PF+'txtNoPersonaCP').setValue(Ext.getCmp(PF + 'txtNoPersonaK').getValue());
							Ext.getCmp(PF+'txtRazonSocialCP').setValue(Ext.getCmp(PF + 'txtRazonSocialK').getValue());
							Ext.getCmp(PF+'txtEmpresaCP').setValue(apps.SET.NOM_EMPRESA);
				
							Ext.getCmp(PF + 'cmbBancoCP').setDisabled(true);
							Ext.getCmp(PF + 'cmbDivisaCP').setDisabled(true);
							Ext.getCmp(PF + 'txtNoBancoCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtChequeraCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtDescChequeraCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtSucursalCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtPlazaCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtClabeCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtBancoAntCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtChequeraAntCP1').setDisabled(true);
						
							
							
							
							
							NS.storeLlenaGridCP.removeAll();
							NS.storeLlenaGridCP.baseParams.tipoPersona =  Ext.getCmp(PF + 'txtTipoPersona').getValue();
							NS.storeLlenaGridCP.baseParams.noPersona =  Ext.getCmp(PF + 'txtNoPersonaK').getValue();
							NS.storeLlenaGridCP.load();
						}
					}
				}
			},
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		x: 590,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificar = true;
		 					if (NS.validaVaciosK()) {
		 						NS.insertarActualizarK();
							}



		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Direcciones',
		 		x: 680,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					
		 					NS.panelCamposK.setVisible(false);
		 					NS.panelBotonesMOK.setVisible(false);
		 					NS.panelCamposDI.setVisible(true);
		 					//NS.panelBotonesDIM.setVisible(true);
		 					//NS.DesabilitarCamposDI();
		 					NS.llenaCamposDIK();
		 					NS.llenaDatosDI();
		 					
		 					//NS.llenaCamposDI();
		 					NS.storeTipoDireccion.load();
		 					NS.storeEstado.load();
		 					NS.storePais.load();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 770,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarK();
		 					NS.cierraVentana();
		 					NS.panelCamposK.setVisible(false);
		 					NS.panelBotonesMOK.setVisible(false);
		 					NS.panelCamposCP.setVisible(false);
		 					NS.panelCamposGrid.setVisible(false)
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesK = new Ext.form.FieldSet({
		id: PF + 'panelBotonesK',
		name: PF + 'panelBotonesK',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 680,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificar = false;
		 					if (NS.validaVaciosK()) {
		 						NS.insertarActualizarK();
							}

		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 770,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarK();
		 					NS.cierraVentana();
		 					NS.panelCamposK.setVisible(false);
		 					NS.panelBotonesK.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesCBM = new Ext.form.FieldSet({
		id: PF + 'panelBotonesCBM',
		name: PF + 'panelBotonesCBM',
		x: 500,
		y: 430,
		width: 480,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	/*{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		id: PF + 'aceptar',
		 		name: PF + 'aceptar',
		 		x: 10,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
					 		NS.llenaVector();
					 		
							var jSonStringCuentas = Ext.util.JSON.encode(matrizCuentas);
		 					ConsultaPersonasAction.validaDatosCuentas(jSonStringCuentas, function(resultado, e){
		 						if (resultado == "" && resultado != undefined && resultado != null)
		 							NS.insertaModificaCuentas();
		 						else
		 							
		 							Ext.Msg.alert('SET', resultado);
		 						
		 						NS.storeGridCuentasProveedor.removeAll();
		 						NS.storeGridCuentasProveedor.baseParams.noPersona = NS.noPersona,
			 					NS.storeGridCuentasProveedor.baseParams.noEmpresa = NS.noEmpresa,
			 					NS.storeGridCuentasProveedor.baseParams.tipoPersona = NS.tipoPersona,
			 					NS.storeGridCuentasProveedor.load();
		 						NS.limpiaCuentasProveedor();
		 						
		 						
		 					
		 					});	 					
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		id: PF + 'cancelar',
		 		name: PF + 'cancelar',
		 		x: 100,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiaCuentasProveedor();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		id: PF + 'imprimir',
		 		name: PF + 'imprimir',
		 		x: 190,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Exportar',
		 		id: PF + 'exportar',
		 		name: PF + 'exportar',
		 		x: 280,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 			
		 	},*/
		 	{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		id: PF + 'regresar',
		 		name: PF + 'regresar',
		 		x: 370,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiaCuentasProveedor();
		 					NS.cierraCuentasProveedor();
		 					NS.panelCuentasBancarias.setVisible(false);
		 					NS.panelBotonesCBM.setVisible(false);
		 					//NS.panelGlobal.setVisible(true);
		 					NS.panelCamposPM.setVisible(true);
		 					NS.panelBotonesPM.setVisible(true);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesCB = new Ext.form.FieldSet({
		id: PF + 'panelBotonesCB',
		name: PF + 'panelBotonesCB',
		x: 500,
		y: 430,
		width: 480,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		id: PF + 'aceptar',
		 		name: PF + 'aceptar',
		 		x: 10,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
					 		NS.llenaVector();
					 		
							var jSonStringCuentas = Ext.util.JSON.encode(matrizCuentas);
		 					ConsultaPersonasAction.validaDatosCuentas(jSonStringCuentas, function(resultado, e){
		 						if (resultado == "" && resultado != undefined && resultado != null)
		 							NS.insertaModificaCuentas();
		 						else
		 							Ext.Msg.alert('SET', resultado);
		 						
		 						NS.storeGridCuentasProveedor.removeAll();
		 						NS.storeGridCuentasProveedor.baseParams.noPersona = NS.noPersona,
			 					NS.storeGridCuentasProveedor.baseParams.noEmpresa = NS.noEmpresa,
			 					NS.storeGridCuentasProveedor.baseParams.tipoPersona = NS.tipoPersona,
			 					NS.storeGridCuentasProveedor.load();
		 						NS.limpiaCuentasProveedor();
		 					});	 					
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		id: PF + 'cancelar',
		 		name: PF + 'cancelar',
		 		x: 100,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiaCuentasProveedor();
		 					NS.panelCuentasBancarias.setVisible(false);
		 					NS.panelBotonesCB.setVisible(false);
		 					NS.panelBusqueda.setVisible(true);
		 					NS.panelGrid.setVisible(true);
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		id: PF + 'imprimir',
		 		name: PF + 'imprimir',
		 		x: 190,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Exportar',
		 		id: PF + 'exportar',
		 		name: PF + 'exportar',
		 		x: 280,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 			
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		id: PF + 'regresar',
		 		name: PF + 'regresar',
		 		x: 370,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiaCuentasProveedor();
		 					NS.cierraCuentasProveedor();
		 					NS.panelCuentasBancarias.setVisible(false);
		 					NS.panelBotonesCB.setVisible(false);
		 					NS.panelCamposPF.setVisible(true);
		 					NS.panelBotonesPF.setVisible(true);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesDIFI = new Ext.form.FieldSet({
		id: PF + 'panelBotonesDIFI',
		name: PF + 'panelBotonesDIFI',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		id: PF + 'Aceptar',
		 		name: PF + 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();

		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 180,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},

			{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 250,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.cierraVentanaDireccionesFI();
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 					NS.panelCamposDI.setVisible(false);
		 					NS.panelBotonesDI.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesDI = new Ext.form.FieldSet({
		id: PF + 'panelBotonesDI',
		name: PF + 'panelBotonesDI',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 700,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificar = false;
		 					if (NS.validaVaciosDI()) {
		 						NS.insertarDi();
							}
		 					



		 				}
		 			}
		 		}
		 	},
		 	
		 	/*{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 210,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},

			{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 370,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		x: 530,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.cierraVentanaDireccionesE();
		 				}
		 			}
		 		}
		 	},*/
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 790,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarDI();
		 					NS.limpiarE();
		 					NS.limpiarIB();
		 					NS.limpiarK();
		 					NS.cierraVentana();
		 					NS.panelCamposDI.setVisible(false);
		 					NS.panelBotonesDI.setVisible(false);
		 					NS.buscar();
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	
	
	NS.panelBotonesDIM = new Ext.form.FieldSet({
		id: PF + 'panelBotonesDIM',
		name: PF + 'panelBotonesDIM',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		id: PF + 'modificarDIM',
				name: PF + 'modificarDIM',
		 		x: 700,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificar = true;
		 					if (NS.validaVaciosDI()) {
		 						NS.insertarDi();
							}
		 				



		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		id: PF + 'cancelarDIM',
				name: PF + 'cancelarDIM',
		 		x: 790,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.habEmpresa();
		 					NS.limpiarDI();
		 					NS.limpiarE();
		 					NS.limpiarIB();
//		 					NS.cierraVentana();
		 					if (Ext.getCmp(PF + 'txtTipoPersona').getValue()=='P') {
		 						NS.panelCamposDI.setVisible(false);
			 					NS.panelBotonesDI.setVisible(false);
			 					NS.panelBotonesDIM.setVisible(false);
			 					NS.panelCamposPM.setVisible(true);
			 					NS.panelBotonesPM.setVisible(true);
							} else{
								NS.cierraVentana();
								NS.panelBotonesDIM.setVisible(false);
								NS.panelCamposDI.setVisible(false);
							}
		 					
		 					
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesMCK = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMCK',
		name: PF + 'panelBotonesMCK',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();



		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 180,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},

			{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 250,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.cierraVentanaMediosContactoK();
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 					NS.panelCamposDI.setVisible(false);
		 					NS.panelBotonesDI.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesMCPM = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMCPM',
		name: PF + 'panelBotonesMCPM',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();



		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 180,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},

			{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 250,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.cierraVentanaMediosContactoPM();
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 					NS.panelCamposDI.setVisible(false);
		 					NS.panelBotonesDI.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesMCPF = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMCPF',
		name: PF + 'panelBotonesMCPF',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();



		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 180,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},

			{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 250,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.cierraVentanaMediosContactoPF();
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 					NS.panelCamposDI.setVisible(false);
		 					NS.panelBotonesDI.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesMCFI = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMCFI',
		name: PF + 'panelBotonesMCFI',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();



		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 180,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},

			{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 250,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.cierraVentanaMediosContactoFI();
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 					NS.panelCamposDI.setVisible(false);
		 					NS.panelBotonesDI.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesMCIB = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMCIB',
		name: PF + 'panelBotonesMCIB',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();



		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 180,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},

			{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 250,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.cierraVentanaMediosContactoIB();
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 					NS.panelCamposDI.setVisible(false);
		 					NS.panelBotonesDI.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesMCE = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMCE',
		name: PF + 'panelBotonesMCE',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();



		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 180,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},

			{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 250,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.cierraVentanaMediosContactoE();
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 					NS.panelCamposDI.setVisible(false);
		 					NS.panelBotonesDI.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesMOF = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMOF',
		name: PF + 'panelBotonesMOF',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		x: 680,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificar = true;
		 					if (NS.validaVaciosF()) {
		 						NS.insertarActualizarF();
							}



		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Direcciones',
		 		x: 770,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					
		 					NS.panelCamposF.setVisible(false);
		 					NS.panelBotonesMOF.setVisible(false);
		 					NS.panelCamposDI.setVisible(true);
		 					//NS.panelBotonesDIM.setVisible(true);
		 					//NS.DesabilitarCamposDI();
		 					NS.llenaCamposDIF();
		 					NS.llenaDatosDI();
		 					
		 					//NS.llenaCamposDI();
		 					NS.storeTipoDireccion.load();
		 					NS.storeEstado.load();
		 					NS.storePais.load();
		 				}
		 			}
		 		}
		 	},
		 /*	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 400,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},*/
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarF();
		 					NS.cierraVentana();
		 					NS.panelCamposF.setVisible(false);
		 					NS.panelBotonesMOF.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesF = new Ext.form.FieldSet({
		id: PF + 'panelBotonesF',
		name: PF + 'panelBotonesF',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 770,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					if (NS.validaVaciosF()) {
		 						NS.insertarActualizarF();
							}
		 					
		 					



		 				}
		 			}
		 		}
		 	},
		 /*	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 400,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},*/
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarF();
		 					NS.cierraVentana();
		 					NS.panelCamposF.setVisible(false);
		 					NS.panelBotonesF.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.limpiarF = function(){
		Ext.getCmp(PF + 'txtNoPersonaF').setValue('');
		Ext.getCmp(PF + 'txtPaternoF').setValue('');
		Ext.getCmp(PF + 'txtMaternoF').setValue('');
		Ext.getCmp(PF + 'txtNombreF').setValue('');
		Ext.getCmp(PF + 'txtNombreCortoF').setValue('');
		Ext.getCmp(PF + 'txtRFCF').setValue('');
		
	};
	
	NS.limpiarIB = function(){
		Ext.getCmp(PF + 'txtNoPersonaIB').setValue('');
		Ext.getCmp(PF + 'txtRazonSocialIB').setValue('');
		Ext.getCmp(PF + 'txtNombreCortoIB').setValue('');
		Ext.getCmp(PF + 'txtRFCIB').setValue('');
		Ext.getCmp(PF + 'txtFechaIngresoIB').setValue('');
	};
	
	NS.limpiarK = function(){
		Ext.getCmp(PF + 'txtEmpresaK').setValue('');
		Ext.getCmp(PF + 'txtRazonSocialK').setValue('');
		Ext.getCmp(PF + 'txtNombreCortoK').setValue('');
		Ext.getCmp(PF + 'txtRFCK').setValue('');
		Ext.getCmp(PF + 'txtFechaIngresoK').setValue('');
	};
	
	NS.limpiarPM = function(){
		Ext.getCmp(PF + 'txtEmpresaPM').setValue('');
		Ext.getCmp(PF + 'txtRazonSocialPM').setValue('');
		Ext.getCmp(PF + 'txtEquivalePerPM').setValue('');
		Ext.getCmp(PF + 'txtReferenciaPM').setValue('');
		Ext.getCmp(PF + 'txtNombreCortoPM').setValue('');
		Ext.getCmp(PF + 'txtRFCPM').setValue('');
		Ext.getCmp(PF + 'txtFechaIngresoPM').setValue('');
//		Ext.getCmp(PF + 'txtIdFormaPagoProPM').setValue('');
//		NS.cmbFormaPagoProv.reset();
		Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setValue(false);

	};

	NS.datosPagoReferenciado = [
	      [''],
	      ['no_factura'],
	      ['no_docto'],
	      ['concepto'],
	      ['concepto y no_factura']
	];
	                     	
	 NS.storePagoReferenciado = new Ext.data.SimpleStore({
	     idProperty: 'descPagoReferenciado',
	     fields: [
	         {name: 'descPagoReferenciado'} 		 	
	     ]
	 });
	 NS.storePagoReferenciado.loadData(NS.datosPagoReferenciado);
	                    	 	   	
	 NS.cmbTipoReferencia = new Ext.form.ComboBox ({
	     store: NS.storePagoReferenciado,
	     id: PF + 'cmbTipoReferencia',
	     name: PF + 'cmbTipoReferencia',
	     x: 50,
	     y: 30,
	     width: 180,
	     typeAhead: true,
	     mode: 'local',
	     selecOnFocus: true,
	     //forceSelection: true,
	     valueField: 'descPagoReferenciado',
	     displayField: 'descPagoReferenciado',
	     autocomplete: true,
	     emptyText: 'Tipo de Referencia',
	     triggerAction: 'all',
	     value: '',
	     visible: false,
	     listeners: {
	    	select: {
	    		fn: function(combo, valor) {
	    				
	    		}
	    	}
	    }
	});	
	
	NS.panelReferencias = new Ext.form.FieldSet ({
		title: 'Tipo Referencia',
		x: 15,
		y: 0,
		width: 300,
		height: 120,
		layout: 'absolute',
		items: [
		        NS.cmbTipoReferencia 
		],
	});
	
	var winReferencias = new Ext.Window({
		title: 'Cambio de Referencia',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 350,
	   	height: 200,
	   	layout: 'absolute',
	   	plain: true,
	   	resizable:false,
	   	draggable:false,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    buttons:[
			      {
			    	text:'Aceptar',
			    	handler:function(){
			    		var tipoReferencia = ''+NS.cmbTipoReferencia.getValue();
			    		var noPersona = parseInt(NS.txtEmpresaPM.getValue());
			    		var tipoPersona = ''+Ext.getCmp(PF + 'txtTipoPersona').getValue();
			    		ConsultaPersonasAction.modificarReferencia(tipoReferencia, noPersona, tipoPersona, function(resultado, e) {
	        					if(e.message=="Unable to connect to the server."){
	        						Ext.Msg.alert('SET','Error de conexión al servidor');
	        						return;
	        					}
	        				if (resultado != '')
								Ext.Msg.alert('SET', resultado);
	        				
	        				NS.cmbTipoReferencia.reset();
	        				winReferencias.hide();
	        				NS.llenaDatosP();
						});
			    	}
			      },
			      
			      {
				    	text:'Cerrar',
				    	handler:function(){
				    		winReferencias.hide();
				    	}
				      },		      
	    ],
	   	items: [
	   	     NS.panelReferencias
	   	     ],
   	        	listeners:{
   	        		show:{
   	        			fn:function(){
   	        				NS.cmbTipoReferencia.reset();
   	        			}
   	        		}
   	        	}	
		});

	
	NS.panelBotonesPM = new Ext.form.FieldSet({
		id: PF + 'panelBotonesPM',
		name: PF + 'panelBotonesPM',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Referencias',
		 		x: 560,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					winReferencias.show();
		 				}
		 			}
		 		}
		 	},		 	
		 	{
		 		xtype: 'button',
		 		text: 'Direcciones',
		 		x: 650,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelCamposPM.setVisible(false);
		 					NS.panelBotonesPM.setVisible(false);
		 					NS.panelCamposDI.setVisible(true);
		 					NS.DesabilitarCamposDI();
		 					NS.panelBotonesDIM.setVisible(false);
		 						if (Ext.getCmp(PF + 'txtTipoPersona').getValue()=='P') {
		 							Ext.getCmp(PF + 'modificarDIM').setVisible(false);
								} else{
									Ext.getCmp(PF + 'modificarDIM').setVisible(true);
								}
		 					NS.llenaCamposDIPM();
		 					NS.llenaDatosDI();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Datos Bancarios',
		 		id: PF + 'ctasBanco',
		 		name: PF + 'ctasBanco',
		 		x: 740, 
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					
		 					NS.ocultasCuentas();
		 					NS.panelCamposPM.setVisible(false);
		 					NS.panelBotonesPM.setVisible(false);
		 					NS.panelCuentasBancarias.setVisible(true);
		 					NS.panelBotonesCBM.setVisible(true);
		 					NS.llenaCamposCP();
		 					Ext.getCmp( PF + 'regresar').setVisible(true);
		 					//NS.storeGridCuentasProveedor.removeAll();
		 				
		 					NS.storeComboDivisaCuentas.load();
		 					//NS.storeGridCuentasProveedor.load();
		 					NS.storeGridCuentasProveedor.baseParams.noPersona = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noPersona');
		 					NS.storeGridCuentasProveedor.baseParams.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
		 					NS.storeGridCuentasProveedor.baseParams.tipoPersona = NS.gridConsulta.getSelectionModel().getSelections()[0].get('idTipoPersona');
		 					
		 					NS.storeGridCuentasProveedor.load();
		 				
		 					NS.gridConsultaCuentas.getView().refresh();
		 					 
		 								
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		x: 840,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarPM();
		 					NS.cierraVentana();
		 					NS.panelCamposPM.setVisible(false);
		 					NS.panelBotonesPM.setVisible(false);
		 					
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesPF = new Ext.form.FieldSet({
		id: PF + 'panelBotonesPF',
		name: PF + 'panelBotonesPF',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 520,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizarPF();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Direcciones',
		 		x: 610,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelCamposPF.setVisible(false);
		 					NS.panelBotonesPF.setVisible(false);
		 					NS.panelCamposDI.setVisible(true);
//		 					NS.panelBotonesDIM.setVisible(true);
		 					NS.llenaCamposDIPF();
		 					NS.llenaDatosDI();
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cuentas de Proveedores',
		 		id: PF + 'ctasBancoF',
		 		name: PF + 'ctasBancoF',
		 		x: 700, 
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 				
		 					/*if (NS.fisicaMoral == 'F'){		 						
		 						Ext.getCmp(PF + 'txtRazonSocialCuentas').setValue(Ext.getCmp(PF + 'txtPaternoGral').getValue() + " " + 
		 																		  Ext.getCmp(PF + 'txtMaternoGral').getValue() + " " + 
		 																		  Ext.getCmp(PF + 'txtNombreGral').getValue());		 							 						
		 					}
		 					else{
		 						Ext.getCmp(PF + 'txtRazonSocialCuentas').setValue(Ext.getCmp(PF + 'txtRazonSocialGral').getValue());		 						
		 					}
		 					
		 					Ext.getCmp(PF + 'txtNoPersonaCuentas').setValue(Ext.getCmp(PF + 'txtNoPersonaGral').getValue());
		 					*/
		 					
		 					NS.panelCamposPF.setVisible(false);
		 					NS.panelBotonesPF.setVisible(false);
		 					NS.panelCuentasBancarias.setVisible(true);
		 					NS.panelBotonesCB.setVisible(true);
		 					NS.storeGridCuentasProveedor.removeAll();
		 				
		 					NS.storeComboDivisaCuentas.load();
		 					NS.storeGridCuentasProveedor.load();
		 					NS.storeGridCuentasProveedor.baseParams.noPersona = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noPersona');
		 					NS.storeGridCuentasProveedor.baseParams.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
		 					NS.storeGridCuentasProveedor.baseParams.tipoPersona = NS.gridConsulta.getSelectionModel().getSelections()[0].get('idTipoPersona');
		 			
//		 					NS.storeGridCuentasProveedor.load();
		 					
		 					NS.gridConsultaCuentas.getView().refresh();
		 					
		 					/*if(Ext.getCmp(PF + 'txtNoEmpresa').getValue() != '' && NS.tipoPersona == 'P')
		 						Ext.getCmp(PF + 'txtEmpresaCuentas').setValue(NS.storeLlenaComboEmpresa.getById(parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue())).get('nomEmpresa'));
		 					else
		 						Ext.getCmp(PF + 'txtEmpresaCuentas').setValue(apps.SET.NOM_EMPRESA);
		 						
					 		NS.panelCampos.setVisible(false);
							NS.panelBotones.setVisible(false);
		 					NS.panelCuentasBancarias.setVisible(true);
		 					Ext.getCmp(PF + 'aceptar').setVisible(true);
		 					Ext.getCmp(PF + 'cancelar').setVisible(true);
		 					Ext.getCmp(PF + 'imprimir').setVisible(true);
		 					Ext.getCmp(PF + 'exportar').setVisible(true);
		 					Ext.getCmp(PF + 'regresar').setVisible(true);
		 					NS.panelBotonesCuentasBancarias.setVisible(true);
		 					
		 							 					
		 					NS.storeGridCuentasProveedor.baseParams.noPersona = NS.noPersona,
		 					NS.storeGridCuentasProveedor.baseParams.noEmpresa = NS.noEmpresa,
		 					NS.storeGridCuentasProveedor.baseParams.tipoPersona = NS.tipoPersona,
		 					NS.storeGridCuentasProveedor.load();
		 					
		 					NS.storeComboDivisaCuentas.load();		*/ 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 		
		 					NS.cierraVentana();
		 					NS.panelBotonesPF.setVisible(false);
		 					NS.panelCamposPF.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	/////////////////////////Termina PF////////////////////////////////////////////////////////////////
	
	//////////////////////////////Componentes Empresa//////////////////////////////
	
	
	//PAO
	NS.lblMensajeCaja = new Ext.form.Label({
		text: '* IMPORTANTE: \n La caja debe coincidir con la empresa  debido a que se relacionará con  impresión de cheques, si no se ha dado de alta la caja \n ir a Utilerías -> Mantenimiento-> Catálogos',
		x: 210,
		y: 110,
		width:190,
		style: {
	       color: 'red'
	    }
	});
	
	
	NS.lblEmpresaE = new Ext.form.Label({
		text: 'No. Empresa',
		x: 0,
		y: 25
	});
	
	NS.lblNoPersonaE = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 25
	});
	

	NS.lblRazonSocialE = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 25
	});
		
	NS.lblNombreCortoE = new Ext.form.Label({
		text: 'Nombre Corto',
		x: 450,
		y: 25
	});
		
	NS.lblRFCE = new Ext.form.Label({
		text: 'RFC',
		x: 630,
		y: 25
	});

	NS.lblFechaIngresoE = new Ext.form.Label({
		text: 'Fecha Ingreso',
		x: 810,
		y: 25		
	});	


	NS.lblGrupoFlujoE = new Ext.form.Label({
		text: 'Grupo de Flujo',
		x: 0, 
		y: 100
	});
	
	NS.lblActividadEconomicaE = new Ext.form.Label({
		text: 'Actividad Económica',
		x: 235,
		y: 100
	});
	
	NS.lblActividadGenericaE = new Ext.form.Label({
		text: 'Actividad Genérica',
		x: 465,
		y: 100
	});
	
	
	NS.lblGiroE = new Ext.form.Label({
		text: 'Giro',
		x: 695,
		y: 100
	});
	
	NS.lblCajaE = new Ext.form.Label({
		text: 'Caja',
		x: 0,
		y: 100
	});
	
	
	NS.lblCalidadE = new Ext.form.Label({
		text: 'Calidad',
		x: 465,
		y: 100
	});	
	
	NS.lblTipoInmuebleE = new Ext.form.Label({
		text: 'Tipo de Inmueble',
		x: 695,
		y: 100
	});
	
	NS.lblVentasAnualesE = new Ext.form.Label({
		text: 'Ventas Anuales',
		x: 0,
		y: 200
	});

	NS.lblObjetoSocialE = new Ext.form.Label({
		text: 'Objeto Social',
		x: 180,
		y: 200
	});
	
	//LABEL DEL PANEL DE TEF
	NS.lblContratoTefE = new Ext.form.Label({
		text: 'Contrato Banamex TEF',		
		x: 0, 
		y: 0
	});
	
	NS.lblContratoPaymentE = new Ext.form.Label({
		text: 'Contrato Banamex MassPayment',		
		x: 250,
		y: 0
	});
	
	NS.lblBeneficiarioE = new Ext.form.Label({
		text: 'Beneficiario Financiamiento',		
		x: 500,
		y: 0
	});
	
	NS.lblGrupoRubroE = new Ext.form.Label({
		text: 'Grupo Rubro',
		x: 0,
		y: 0
	});
	
	NS.lblRubroE = new Ext.form.Label({
		text: 'Rubro',
		x: 250,
		y: 0
	});
 	
	//TEXTFIELD PARA CAMPOS DE INFORMACION GENERAL

	
	NS.txtEmpresaE = new  Ext.form.NumberField({
		id: PF + 'txtEmpresaE',
		name: PF + 'txtEmpresaE',
		x: 0,
		y: 40,
		width: 70
	});
	
	NS.txtNoPersonaE = new Ext.form.TextField({
		id: PF + 'txtNoPersonaE',
		name: PF + 'txtNoPersonaE',
		x: 0, 
		y: 40,
		width: 70
	});
	
	
	NS.txtRazonSocialE ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialE',
		name: PF + 'txtRazonSocialE',
		x: 80,
		y: 40,
		width: 350
		
	};
	NS.txtNombreCortoE ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCortoE',
		name: PF + 'txtNombreCortoE',
		x: 450,
		y: 40,
		width: 150,
		maxLength: 20,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				if(e.keyCode ==18){ 
	 					NS.buscar();
	 				}
	 			}
	 		},
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 				
	 			}
	 		},
		}
	};
	
	NS.txtRFCE = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCE',
		name: PF + 'txtRFCE',
		regex: /^(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))/,
		regexText: 'Ingrese un rfc valido',
		x: 630,
		y: 40,
		width: 150
	};
	
	NS.txtFechaIngresoE = new Ext.form.DateField({
		id: PF + 'txtFechaIngresoE',
		name: PF + 'txtFechaIngresoE',
		x: 810,
		y: 40,
		width: 100,	
		format: 'd-m-Y'		
	});
		
	Ext.getCmp(PF + 'txtFechaIngresoE').setValue(apps.SET.FEC_HOY);


	NS.txtIdGrupoE = new Ext.form.TextField({
		id: PF + 'txtIdGrupoE',
		name: PF + 'txtIdGrupoE',
		x: 0,
		y: 115,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdGrupoE', NS.cmbGrupoE.getId());
					else
						NS.cmbGrupo.reset();
				}				
			}
		}
	});
	
	NS.txtIdActividadEconomicaE = new Ext.form.TextField({
		id: PF + 'txtIdActividadEconomicaE',
		name: PF + 'txtIdActividadEconomicaE',
		x: 235,
		y: 115,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdActividadEconomica', NS.cmbActividadEconomica.getId());
					else
						NS.cmbActividadEconomica.reset();
				}
			}
		}
	});
	
	NS.txtIdActividadGenericaE = new Ext.form.TextField({
		id: PF + 'txtIdActividadGenericaE',
		name: PF + 'txtIdActividadGenericaE',		
		x: 465,
		y: 115,
		width: 50,		
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdActividadGenerica', NS.cmbActividadGenerica.getId());
					else
						NS.cmbActividadGenerica.reset();
				}
			}
		}
	});
	
	NS.txtIdGiroE = new Ext.form.TextField({
		id: PF + 'txtIdGiroE',
		name: PF + 'txtIdGiroE',
		x: 695,
		y: 115,
		width: 50,		
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdGiroE', NS.cmbGiroE.getId());
					else
						NS.cmbGiroE.reset();
				}
			}
		}
	});
	
	NS.txtIdCajaE = new Ext.form.TextField({
		id: PF + 'txtIdCajaE',
		name: PF + 'txtIdCajaE',
		x: 0,
		y: 115,
		width: 50,		
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdCajaE', NS.cmbCajaE.getId());
					else
						NS.cmbCajaE.reset();
				}
			}
		}
	});
	
	NS.txtIdCalidadE = new Ext.form.TextField({
		id: PF + 'txtIdCalidadE',
		name: PF + 'txtIdCalidadE',
		x: 465,
		y: 115,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdCalidadE', NS.cmbCalidadE.getId());
					else
						NS.cmbCalidadE.reset();
				}
			}
		}
	});
	
	NS.txtIdInmuebleE = new Ext.form.TextField({
		id: PF + 'txtIdInmuebleE',
		name: PF + 'txtIdInmuebleE',
		x: 695,
		y: 115,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdInmuebleE', NS.cmbInmuebleE.getId());
					else
						NS.cmbInmuebleE.reset();
				}
 			}
		}
	});
	
	NS.txtVentasAnualesE ={
		xtype: 'uppertextfield',
		id: PF + 'txtVentasAnualesE',
		name: PF + 'txtVentasAnualesE',
		x: 0,
		y: 215,
		width: 150		
	};
	
	NS.txtObjetoSocialE ={
		xtype: 'uppertextfield',
		id: PF + 'txtObjetoSocialE',
		name: PF + 'txtObjetoSocialE',
		x: 180,
		y: 215,
		width: 150
	};
	
	
	NS.txtNoBenefE ={
		xtype: 'uppertextfield',
		id: PF + 'txtNoBenefE',
		name: PF + 'txtNoBenefE',
		x: 500,
		y: 20,		
		width: 60,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoBenefE', NS.cmbBenefE.getId());
					else
						NS.cmbBenefE.reset();
				}
			}
		}
	};
	
	NS.txtGrupoRubroE = new Ext.form.TextField({
		id: PF + 'txtGrupoRubroE',
		name: PF + 'txtGrupoRubroE',
		x: 0,
		y: 20,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined) {
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtGrupoRubroE', NS.cmbGrupoRubroE.getId());
					
						if(comboValue == null || comboValue == undefined || comboValue == '')
							Ext.getCmp(PF + 'txtGrupoRubroE').setValue('');
						else {
							Ext.getCmp(NS.txtRubroE.getId()).setValue('');
							Ext.getCmp(NS.cmbRubroE.getId()).setValue('');
							NS.storeRubroEgreso.removeAll();
							NS.storeRubroEgreso.baseParams.idGrupo = parseInt(NS.txtGrupoRubroE.getValue());									
							NS.storeRubroEgreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresaE').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF + 'txtNoEmpresaE').getValue());
							NS.storeRubroEgreso.load();
						}
					}else {
						NS.cmbGrupoRubroE.reset();
						NS.cmbRubroE.reset();
						Ext.getCmp(PF + 'txtRubroE').setValue('');
						Ext.getCmp(PF + 'txtGrupoRubroE').setValue('');
					}
				}
			}
		}
	});
	
	NS.txtRubroE = new Ext.form.TextField({
		id: PF + 'txtRubroE',
		name: PF + 'txtRubroE',
		x: 250,
		y: 20,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined) {
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtRubroE', NS.cmbRubroE.getId());

						if(comboValue == null || comboValue == undefined || comboValue == '')
							Ext.getCmp(PF + 'txtRubroE').setValue('');
					}else
						NS.cmbRubroE.reset();
				}
			}
		}
	});
////Terminan los TextFields//////////////////////////////////

//////Combosss/////////////////////////////////////////
	
	NS.cmbGrupoE = new Ext.form.ComboBox({
		store: NS.storeLlenaComboGrupo,
		id: PF + 'cmbGrupoE',
		name: PF + 'cmbGrupoE',
		x: 55,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo de Flujo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGrupoE', NS.cmbGrupoE.getId());
				}
			}
		}
	});
	
	NS.cmbActividadEconomicaE = new Ext.form.ComboBox({
		store: NS.storeActividadEconomica,
		id: PF + 'cmbActividadEconomicaE',
		name: PF + 'cmbActividadEconomicaE',
		x: 295,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idActividadEconomica',
		displayField: 'descActividadEconomica',
		autocomplete: true,
		emptyText: 'Actividad Económica',
		triggerAction: 'all',
		value: '',
		visible: true,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdActividadEconomicaE', NS.cmbActividadEconomicaE.getId());					
				}
			}
		}
	});
	
	NS.cmbActividadGenericaE = new Ext.form.ComboBox({
		store: NS.storeActividadGenerica,	
		id: PF + 'cmbActividadGenericaE',
		name: PF + 'cmbActividadGenericaE',
		x: 525,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idActividadGenerica',
		displayField: 'descActividadGenerica',
		autocomplete: true,
		emptyText: 'Actividad Generica',
		triggerAction: 'all',
		value: '',
		visible: true,		
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdActividadGenericaE', NS.cmbActividadGenericaE.getId());
				}
			}
		}
	});
	
	
	
	NS.cmbGiroE = new Ext.form.ComboBox({
		store: NS.storeComboGiro,
		id: PF + 'cmbGiroE',
		name: PF + 'cmbGiroE',
		x: 755,
		y: 115,		
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGiro',
		displayField: 'descGiro',
		autocomplete: true,
		emptyText: 'Giro',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGiroE', NS.cmbGiroE.getId());
				}
			}
		}
	});
	
	NS.cmbCajaE = new Ext.form.ComboBox({
		store: NS.storeComboCaja,
		id: PF + 'cmbCajaE',
		name: PF + 'cmbCajaE',
		x: 55,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		focus:false,
		valueField: 'idCaja',
		displayField: 'descCaja',
		autocomplete: true,
		emptyText: 'Caja',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					//NS.storeComboCaja.load();//AQUI PAO
					//console.log("Entro actualizar");
					BFwrk.Util.updateComboToTextField(PF + 'txtIdCajaE', NS.cmbCajaE.getId());
				}
	
			},
			focus:{
	 			fn: function(e){
	 					NS.storeComboCaja.load();
	 					//console.log("Aqui entra");
	 			}
	 		}

		}
		
	});
	NS.cmbCalidadE = new Ext.form.ComboBox({
		store: NS.storeCalidad,
		id: PF + 'cmbCalidadE',
		name: PF + 'cmbCalidadE',
		x: 525,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idCalidad',
		displayField: 'descCalidad',
		autocomplete: true,
		emptyText:  'Calidad de Empresa',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdCalidadE', NS.cmbCalidadE.getId());
				}
			}
		}
	});
	
	NS.cmbInmuebleE = new Ext.form.ComboBox({
		store: NS.storeInmueble,
		id: PF + 'cmbInmuebleE',
		name: PF + 'cmbInmuebleE',
		x: 755,
		y: 115,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idInmueble',
		displayField: 'descInmueble',
		autocomplete: true,
		emptyText: 'Tipo de Inmueble',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdInmuebleE', NS.cmbInmuebleE.getId());
				}
			}
		}
	});
	NS.cmbContratoTefE = new Ext.form.ComboBox({
		//store: NS.storeComboTef,
		id: PF + 'cmbContratoTefE',
		name: PF + 'cmbContratoTefE',
		x: 0,
		y: 20,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idContrato',
		//displayField: 'descContrato',
		autocomplete: true,
		emptyText: 'Contrato TEF',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{}
			}
		}
	});
	
	NS.cmbContratoPaymentE = new Ext.form.ComboBox({
		///store: NS.storeComboPayment,
		id: PF + 'cmbContratoPaymentE',
		name: PF + 'cmbContratoPaymentE',
		x: 250,
		y: 20,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		//valueField: 'idContratoPayment',
		//displayField: 'descContratoPayment',
		autocomplete: true,
		emptyText: 'Contrato MassPayment',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{}
			}
		}
	});
	
	NS.cmbBenefE = new Ext.form.ComboBox({
		store: NS.storeComboBenef,
		id: PF + 'cmbBenefE',
		name: PF + 'cmbBenefE',
		x: 570,
		y: 20,		
		width: 250,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'noBenef',
		displayField: 'descBenef',
		autocomplete: true,
		emptyText: 'Parte del Beneficiario a Buscar',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtNoBenefE', NS.cmbBenefE.getId());
				}
			}
		}
	});
	
	NS.cmbGrupoRubroE = new Ext.form.ComboBox({
		store: NS.storeGrupoEgreso,
		id: PF + 'cmbGrupoRubroE',
		name: PF + 'cmbGrupoRubroE',
		x: 55,
		y: 20,		
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtGrupoRubroE', NS.cmbGrupoRubroE.getId());
					Ext.getCmp(NS.txtRubroE.getId()).setValue('');
					Ext.getCmp(NS.cmbRubroE.getId()).setValue('');
					NS.storeRubroEgreso.removeAll();
					NS.storeRubroEgreso.baseParams.idGrupo = parseInt(NS.txtGrupoRubroE.getValue());									
					NS.storeRubroEgreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresaE').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF + 'txtNoEmpresaE').getValue());
					NS.storeRubroEgreso.load();
				}
			},
			change:{
			    fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtGrupoRubroE' ).setValue('');
         				Ext.getCmp(NS.txtRubroE.getId()).setValue('');
						Ext.getCmp(NS.cmbRubroE.getId()).setValue('');
						NS.storeRubroEgreso.removeAll();       				
         			}else{
         				Ext.getCmp( PF + 'txtRubroE' ).forceSelection= true; 
         			}	
		    	}//end function
			}//end change
		}
	});
	
	NS.cmbRubroE = new Ext.form.ComboBox({
		store: NS.storeRubroEgreso,
		id: PF + 'cmbRubroE',
		name: PF + 'cmbRubroE',
		x: 305,
		y: 20,
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtRubroE', NS.cmbRubroE.getId());
				}
			},
			change:{
				fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtRubroE' ).setValue('');
					}else{
						Ext.getCmp( PF + 'txtRubroE' ).forceSelection= true;
					}
				}//end function
			}//end change
		}
	});

	NS.optExportaE = new Ext.form.RadioGroup({
		id: PF + 'optExportaE',
		name: PF + 'optExportaE',
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'SI', name: 'optExportaSel', inputValue: 0, checked: true,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.exporta = 'S'
		 					}
		 				}
		 			}
 				}
		 	},
		 	{boxLabel: 'NO', name: 'optExportaSel', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.exporta = 'N';
		 					}
		 				}
		 			}
		 		}
		 	}
		]
	});

	NS.optConcentradoraE = new Ext.form.RadioGroup({
		id: PF + 'optConcentradoraE',
		name: PF + 'optConcentradoraE',
		x: 0,
		y: 0,
		width: 200,
		height: 30,
		columns: 2,
		items:
		[
		 	{boxLabel: 'SI', name: 'optConcentradoraSel', inputValue: 0, checked: true, width: 100, height: 30, 
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							NS.concentradora = 'S'
		 						}
		 					}
		 				}
		 			}
		 	},
		 	{boxLabel: 'NO', name: 'optConcentradoraSel', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.concentradora = 'N'
		 					}
		 				}
		 			}
		 		}
		 	}
		]
	});
	/////////////////////////Termina componentes Empresa/////////////////////////////
	NS.panelExportaFI = new Ext.form.FieldSet({
		id: PF + 'panelExportaFI',
		name: PF + 'panelExportaFI', 
		title: 'Exporta',
		x: 350,
		y: 200,
		width: 150,
		height: 40,
		layout: 'absolute',
		items:
		[
		 	NS.optExportaFI
		]
	});
	
NS.panelConcentradoraFI = new Ext.form.FieldSet({
		id: PF + 'panelConcentradoraFI',
		name: PF + 'panelConcentradoraFI',
		title: 'Concentradora de Inversion',
		x: 510,
		y: 200,
		width: 200,
		height: 40,
		layout: 'absolute',
		items:
		[
		 	NS.optConcentradoraFI
		]
	});
//////////////////////////////////fisica///////////////////////////////////////
	NS.lblEmpresaF = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 0
	});
	
	NS.lblNoPersonaF = new Ext.form.Label({
		text: 'No. Persona',
	
		x: 0,
		y: 50
	});
	
	NS.lblPaternoF = new Ext.form.Label({
		text: 'Paterno',
		x: 100,
		y: 50
	});

	
	
	NS.lblMaternoF = new Ext.form.Label({
		text: 'Materno',
		x: 250,
		y: 50
	});

	NS.lblNombreF = new Ext.form.Label({
		text: 'Nombre',
		x: 400,
		y: 50
	});
		
	NS.lblNombreCortoF = new Ext.form.Label({
		text: 'Nombre Corto',
		x: 550,
		y: 50
	});
		
	NS.lblRFCF = new Ext.form.Label({
		text: 'RFC',
		x: 710,
		y: 50
	});	

	NS.txtEmpresaF = new Ext.form.TextField({
		id: PF + 'txtEmpresaF',
		name: PF + 'txtEmpresaF',
		x: 0,
		y: 15,
		width: 300
	});
	
	NS.txtNoPersonaF = new Ext.form.TextField({
		id: PF + 'txtNoPersonaF',
		name: PF + 'txtNoPersonaF',
		x: 0, 
		y: 65,
		readOnly: true,
		width: 70
	});
	
	NS.txtRazonSocialF ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialF',
		name: PF + 'txtRazonSocialF',
		x: 80,
		y: 65,
		width: 350
	};
	
	NS.txtPaternoF = {
		xtype: 'uppertextfield',
		id: PF + 'txtPaternoF',
		name: PF + 'txtPaternoF',
		x: 100,
		y: 65,
		width: 140
	};
	
	NS.txtMaternoF = {
		xtype: 'uppertextfield',
		id: PF + 'txtMaternoF',
		name: PF + 'txtMaternoF',
		x: 250,
		y: 65,
		width: 140
	};
	
	NS.txtNombreF = {
		xtype: 'uppertextfield',
		id: PF + 'txtNombreF',
		name: PF + 'txtNombreF',
		x: 400,
		y: 65,
		width: 140
	};
	
	NS.txtNombreCortoF ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCortoF',
		name: PF + 'txtNombreCortoF',
		x: 550,
		y: 65,
		width: 150,
		maxLength: 20,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				if(e.keyCode ==18){ 
	 					NS.buscar();
	 				}
	 			}
	 		},
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 				
	 			}
	 		},
	 		/*change:{
	 			fn:function(caja, e) {	
	 				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
        				NS.storeUnicoBeneficiario.baseParams.registroUnico=true;
        				NS.storeUnicoBeneficiario.baseParams.condicion=''+caja.getValue();
        				NS.storeUnicoBeneficiario.load();
       				}else {
       					NS.cmbBeneficiario.reset();
       					NS.storeUnicoBeneficiario.removeAll();
       				}
	 			}
	 		}*/
		}
	};
	
	NS.txtRFCF = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCF',
		name: PF + 'txtRFCF',
		regex: /^(([A-Z]|[a-z]|\s){1})(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))/,
		regexText: 'Ingrese un rfc valido',
		x: 710,
		y: 65,
		width: 150
	};

//////////////////////////////////////////////////////////////////////////////
	
	//////////////////PRUEBA INSTITUCIONES BANCARIAS//////////////////////////////////////////////////////

	NS.lblEmpresaIB = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 0
	});
	
	NS.lblNoPersonaIB = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 50
	});
	

	NS.lblRazonSocialIB = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 50,
	});
		
	NS.lblNombreCortoIB = new Ext.form.Label({
		text: 'Nombre Corto',
		x: 450,
		y: 50
	});
		
	NS.lblRFCIB = new Ext.form.Label({
		text: 'RFC',
		x: 630,
		y: 50
	});

	NS.lblFechaIngresoIB = new Ext.form.Label({
		text: 'Fecha Ingreso',
		x: 810,
		y: 50		
	});
	
	
	
	

	//TEXTFIELD PARA CAMPOS DE INFORMACION GENERAL
	NS.txtEmpresaIB = new Ext.form.TextField({
		id: PF + 'txtEmpresaIB',
		name: PF + 'txtEmpresaIB',
		x: 0,
		y: 15,
		width: 300
	});
	
	NS.txtNoPersonaIB = new Ext.form.TextField({
		id: PF + 'txtNoPersonaIB',
		name: PF + 'txtNoPersonaIB',
		x: 0, 
		y: 65,
		readOnly: true,
		width: 70
	});
	

	
	NS.txtRazonSocialIB ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialIB',
		name: PF + 'txtRazonSocialIB',
		x: 80,
		y: 65,
		width: 350
	};
	
	
	
	NS.txtNombreCortoIB ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCortoIB',
		name: PF + 'txtNombreCortoIB',
		x: 450,
		y: 65,
		width: 150,
		maxLength: 20,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				if(e.keyCode ==18){ 
	 					NS.buscar();
	 				}
	 			}
	 		},
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 20));
	 				}
	 				
	 			}
	 		},
		}
	};
	
	NS.txtRFCIB = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCIB',
		name: PF + 'txtRFCIB',
		regex: /^(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))/,
		regexText: 'Ingrese un rfc valido',
		x: 630,
		y: 65,
		width: 150
	};
	
	NS.txtFechaIngresoIB = new Ext.form.DateField({
		id: PF + 'txtFechaIngresoIB',
		name: PF + 'txtFechaIngresoIB',
		x: 810,
		y: 65,
		width: 100,	
		format: 'd-m-Y'		
	});
		
	Ext.getCmp(PF + 'txtFechaIngresoIB').setValue(apps.SET.FEC_HOY);
	/////////////////////////////TERMINA INSTITUCIONES BANCARIAS//////////////////////////////////////////
	
	
	////////////////////////////Empieza Clientes Fisicos////////////////////////////////////////////
	NS.lblEmpresaCF = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 0
	});
	
	NS.lblNoPersonaCF = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 50
	});
	
	NS.lblPaternoCF = new Ext.form.Label({
		text: 'Paterno',
		x: 100,
		y: 50
	});

	
	
	NS.lblMaternoCF = new Ext.form.Label({
		text: 'Materno',
		x: 210,
		y: 50
	});

	NS.lblNombreCF = new Ext.form.Label({
		text: 'Nombre',
		x: 320,
		y: 50
	});
		
	NS.lblNombreCortoCF = new Ext.form.Label({
		text: 'Nombre Corto',
		x: 450,
		y: 50
	});
		
	NS.lblRFCCF = new Ext.form.Label({
		text: 'RFC',
		x: 630,
		y: 50
	});
	
NS.txtEmpresaCF = new Ext.form.TextField({
		id: PF + 'txtEmpresaGralCF',
		name: PF + 'txtEmpresaGralCF',
		x: 0,
		y: 15,
		
		width: 300
	});
	
	NS.txtNoPersonaCF = new Ext.form.TextField({
		id: PF + 'txtNoPersonaGralCF',
		name: PF + 'txtNoPersonaGralCF',
		x: 0, 
		y: 65,
		width: 70
	});
	

	
	NS.txtPaternoCF = {
		xtype: 'uppertextfield',
		id: PF + 'txtPaternoGralCF',
		name: PF + 'txtPaternoGralCF',
		x: 100,
		y: 65,
		width: 100
	};
	
	NS.txtMaternoCF = {
		xtype: 'uppertextfield',
		id: PF + 'txtMaternoGralCF',
		name: PF + 'txtMaternoGralCF',
		x: 210,
		y: 65,
		width: 100
	};
	
	NS.txtNombreCF = {
		xtype: 'uppertextfield',
		id: PF + 'txtNombreGralCF',
		name: PF + 'txtNombreGralCF',
		x: 320,
		y: 65,
		width: 100
	};
	
	NS.txtNombreCortoCF ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCortoCF',
		name: PF + 'txtNombreCortoCF',
		x: 450,
		y: 65,
		width: 150
	};
	
	NS.txtRFCCF = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCGralCF',
		name: PF + 'txtRFCGralCF',
		x: 630,
		y: 65,
		width: 150
	};
	
	//RadioButton
	NS.optRadioSexoCF = new Ext.form.RadioGroup({
		id: PF + 'optRadioSexoCF',
		name: PF + 'optRadioSexoCF',		
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Masculino', name: 'optSeleccion', inputValue: 0, checked: true,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.sexo = 'M'
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{boxLabel: 'Femenino', name: 'optSeleccion', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.sexo = 'F'
		 					}
		 				}
		 			}
	 			}
		 	}
		]
	});

	NS.panelSexoCF = new Ext.form.FieldSet({
		id: PF + 'panelSexoCF',
		name: PF + 'panelSexoCF',
		title: 'Sexo:',
		x: 0,
		y: 100,
		width: 200,
		height: 40,
		layout: 'absolute',		
		items:
		[
		 	NS.optRadioSexoCF
		]
	});
	
	
	///////////////////////////Termina Clientes Fisicos/////////////////////////////////////////////
	
	/////////////////////////Empieza Clientes Morales/////////////////////////////////////////////
	NS.lblEmpresaCM = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 0
	});
	
	NS.lblNoPersonaCM = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 50
	});
	

	NS.lblRazonSocialCM = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 50,
	});
		
	NS.lblNombreCortoCM = new Ext.form.Label({
		text: 'Nombre Corto',
		x: 450,
		y: 50
	});
		
	NS.lblRFCCM = new Ext.form.Label({
		text: 'RFC',
		x: 630,
		y: 50
	});

	NS.lblFechaIngresoCM = new Ext.form.Label({
		text: 'Fecha Ingreso',
		x: 810,
		y: 50		
	});

	NS.txtEmpresaCM = new Ext.form.TextField({
		id: PF + 'txtEmpresaGralCM',
		name: PF + 'txtEmpresaGralCM',
		x: 0,
		y: 15,
		width: 300
	});
	
	NS.txtNoPersonaCM = new Ext.form.TextField({
		id: PF + 'txtNoPersonaGralCM',
		name: PF + 'txtNoPersonaGralCM',
		x: 0, 
		y: 65,
		width: 70
	});
	

	
	NS.txtRazonSocialCM ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialGralCM',
		name: PF + 'txtRazonSocialGralCM',
		x: 80,
		y: 65,
		width: 350
	};
	
	
	
	NS.txtNombreCortoCM ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCortoCM',
		name: PF + 'txtNombreCortoCM',
		x: 450,
		y: 65,
		width: 150
	};
	
	NS.txtRFCCM = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCCM',
		name: PF + 'txtRFCCM',
		x: 630,
		y: 65,
		width: 150
	};

	NS.txtFechaIngresoCM = new Ext.form.DateField({
		id: PF + 'txtFechaIngresoCM',
		name: PF + 'txtFechaIngresoCM',
		x: 810,
		y: 65,
		width: 100,	
		format: 'd-m-Y'		
	});
	
	////////////////////////Termina Clientes Morales //////////////////////////////////////////////////
	
	NS.lblTipoPersona = new Ext.form.Label({
		text: 'Tipo Persona',
		x: 0,
		y: 5

	});
	
	NS.lblNoPersona = new Ext.form.Label({
		text: 'No. Persona',
		x: 0,
		y: 60

	});
	
	NS.lblRazonSocial = new Ext.form.Label({
		text: 'Razón Social',
		x: 80,
		y: 60

	});
	
	NS.lblO = new Ext.form.Label({
		text: 'Ó',
		x: 410,
		y: 70

	});
	
	NS.lblPaterno = new Ext.form.Label({
		text: 'Paterno',
		x: 440,
		y: 60

	});
	
	NS.lblMaterno = new Ext.form.Label({
		text: 'Materno',
		x: 570,
		y: 60

	});
	
	NS.lblNombre = new Ext.form.Label({
		text: 'Nombre',
		x: 700,
		y: 60

	});
	
	NS.lblFecha = new Ext.form.Label({
		text: 'Fecha Alta',
		x: 200,
		y: 150
	});
	
	//LABEL PARA CAMPOS DE INFORMACION GENERAL
	NS.lblEmpresaGral = new Ext.form.Label({
		text: 'Empresa',
		hidden: true,
		x: 0,
		y: 0
	});
	
	NS.lblNoPersonaGral = new Ext.form.Label({
		text: 'No. Persona',
		hidden: true,
		x: 0,
		y: 50
	});
	
	NS.lblPaternoGral = new Ext.form.Label({
		text: 'Paterno',
		hidden: true,
		x: 100,
		y: 50
	});

	NS.lblRazonSocialGral = new Ext.form.Label({
		text: 'Razón Social',
		hidden: true,
		x: 80,
		y: 50,
		hidden: true
	});
	
	NS.lblMaternoGral = new Ext.form.Label({
		text: 'Materno',
		hidden: true,
		x: 210,
		y: 50
	});

	NS.lblNombreGral = new Ext.form.Label({
		text: 'Nombre',
		hidden: true,
		x: 320,
		y: 50
	});
		
	NS.lblNombreCorto = new Ext.form.Label({
		text: 'Nombre Corto',
		hidden: true,
		x: 450,
		y: 50
	});
		
	NS.lblRFCGral = new Ext.form.Label({
		text: 'RFC',
		hidden: true,
		x: 630,
		y: 50
	});
	
	NS.lblFechaIngreso = new Ext.form.Label({
		text: 'Fecha Ingreso',
		hidden: true,
		x: 810,
		y: 50		
	});
	
	NS.lblPuesto = new Ext.form.Label({
		text: 'Puesto',
		x: 810,
		y: 50,
		hidden: true
	});
	
	NS.lblGrupoFlujo = new Ext.form.Label({
		text: 'Grupo de Flujo',
		hidden: true,
		x: 0, 
		y: 100
	});
	
	NS.lblEstadoCivil = new Ext.form.Label({
		text: 'Estado Civil',
		hidden: true,
		x: 465,
		y: 100
	});
	
	NS.lblRiesgo = new Ext.form.Label({
		text: 'Riesgo',
		hidden: true,
		x: 0,
		y: 150
	});
	
	NS.lblCambioPersona = new Ext.form.Label({
		text: 'Cambio Persona',
		hidden: true,
		x: 0, 
		y: 150
	});
	
	NS.lblTamano = new Ext.form.Label({
		text: 'Tamaño',
		hidden: true,
		x: 235,
		y: 150
	});

	NS.lblObjetoSocial = new Ext.form.Label({
		text: 'Objeto Social',
		hidden: true,
		x: 180,
		y: 200
	});
	
	NS.lblNoEmpleados = new Ext.form.Label({
		text: 'No. Empleados',
		hidden: true,
		x: 360,
		y: 200
	});
	
	NS.lblDiaLimite = new Ext.form.Label({
		text: 'día Límite de Pago',
		hidden: true,
		x: 490,
		y: 200
	});
	
	NS.lblDiaRecepcion = new Ext.form.Label({
		text: 'día Recepción Facturas',
		hidden: true,
		x: 620,
		y: 200
	});
	
	NS.lblFormaPagoProv = new Ext.form.Label({
		text: 'Forma de Pago Prov',
		hidden: true,
		x: 250,
		y: 100
	});
		
	NS.lblPagoReferenciado = new Ext.form.Label({
		text: 'Valor para Pago Referenciado',
		hidden: true,
		x: 0,
		y: 250
	});
	
	NS.lblAccionesA = new Ext.form.Label({
		text: 'Acciones Serie A',
		hidden: true,
		x: 180, 
		y: 250
	});
	
	NS.lblAccionesB = new Ext.form.Label({
		text: 'Acciones Serie B',
		hidden: true,
		x: 310,
		y: 250
	});
	
	NS.lblAccionesC = new Ext.form.Label({
		text: 'Acciones Serie C',
		hidden: true,
		x: 440,
		y: 250
	});
	
	NS.lblEstatus = new Ext.form.Label({
		id: PF + 'lblEstatus',
		name: PF + 'lblEstatus',
		text: '',
		x: 850,
		y: 5
	});
	
	
	//LABEL DEL PANEL DE TEF
	NS.lblContratoTef = new Ext.form.Label({
		text: 'Contrato Banamex TEF',		
		x: 0, 
		y: 0
	});
	
	NS.lblContratoPayment = new Ext.form.Label({
		text: 'Contrato Banamex MassPayment',		
		x: 250,
		y: 0
	});
	
	NS.lblBeneficiario = new Ext.form.Label({
		text: 'Beneficiario Financiamiento',		
		x: 500,
		y: 0
	});
	
	NS.lblGrupoRubro = new Ext.form.Label({
		text: 'Grupo Rubro',
		x: 0,
		y: 0
	});
	
	NS.lblRubro = new Ext.form.Label({
		text: 'Rubro',
		x: 250,
		y: 0
	});
 	
	NS.txtNoPersona = new Ext.form.TextField({
		id: PF + 'txtNoPersona',
		name: PF + 'txtNoPersona',
		x: 0,
		y: 75,		
		width: 70,
		maxLength: 11,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				if(e.keyCode ==10){ 
	 					//NS.buscar();
	 				}
	 			}
	 		},
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 11));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 11));
	 				}
	 				
	 			}
	 		},
		}
	});
		
	NS.txtRazonSocial = {
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocial',
		name: PF + 'txtRazonSocial',
		x: 80,
		y: 75,
		width: 300
	};
	
	NS.txtPaterno = {
		xtype: 'uppertextfield',
		id: PF + 'txtPaterno',
		name: PF + 'txtPaterno',
		x: 440,
		y: 75,		

		width: 100
	};
	
	NS.txtMaterno = {
		xtype: 'uppertextfield',
		id: PF + 'txtMaterno',
		name: PF + 'txtMaterno',
		x: 570,
		y: 75,
		width: 100
	};
	
	NS.txtNombre = {
		xtype: 'uppertextfield',
		id: PF + 'txtNombre',
		name: PF + 'txtNombre',
		x: 700,
		y: 75,
		width: 100
	};

	//TEXTFIELD PARA CAMPOS DE INFORMACION GENERAL
	NS.txtEmpresaGral = new Ext.form.TextField({
		id: PF + 'txtEmpresaGral',
		name: PF + 'txtEmpresaGral',
		x: 0,
		y: 15,
		hidden: true,
		width: 300
	});
	
	NS.txtNoPersonaGral = new Ext.form.TextField({
		id: PF + 'txtNoPersonaGral',
		name: PF + 'txtNoPersonaGral',
		x: 0, 
		y: 65,
		hidden: true,
		width: 70
	});
	

	NS.txtEquivalePersona = new Ext.form.TextField({
		id: PF + 'txtEquivalePersona',
		name: PF + 'txtEquivalePersona',
		x: 0, 
		y: 65,
		hidden: true,
		width: 70
	});
	
	NS.txtRazonSocialGral ={
		xtype: 'uppertextfield',
		id: PF + 'txtRazonSocialGral',
		name: PF + 'txtRazonSocialGral',
		x: 80,
		y: 65,
		hidden: true,
		width: 350
	};
	
	NS.txtPaternoGral = {
		xtype: 'uppertextfield',
		id: PF + 'txtPaternoGral',
		name: PF + 'txtPaternoGral',
		x: 100,
		y: 65,
		hidden: true,
		width: 100
	};
	
	NS.txtMaternoGral = {
		xtype: 'uppertextfield',
		id: PF + 'txtMaternoGral',
		name: PF + 'txtMaternoGral',
		x: 210,
		y: 65,
		hidden: true,
		width: 100
	};
	
	NS.txtNombreGral = {
		xtype: 'uppertextfield',
		id: PF + 'txtNombreGral',
		name: PF + 'txtNombreGral',
		x: 320,
		y: 65,
		hidden: true,
		width: 100
	};
	
	NS.txtNombreCorto ={
		xtype: 'uppertextfield',	
		id: PF + 'txtNombreCorto',
		name: PF + 'txtNombreCorto',
		x: 450,
		y: 65,
		hidden: true,
		width: 150
	};
	
	NS.txtRFCGral = {
		xtype: 'uppertextfield',
		id: PF + 'txtRFCGral',
		name: PF + 'txtRFCGral',
		x: 630,
		y: 65,
		hidden: true,
		width: 150
	};
	
	NS.txtFechaIngreso = new Ext.form.DateField({
		id: PF + 'txtFechaIngreso',
		name: PF + 'txtFechaIngreso',
		x: 810,
		y: 65,
		width: 100,	
		hidden: true,
		format: 'd-m-Y'		
	});
		
	Ext.getCmp(PF + 'txtFechaIngreso').setValue(apps.SET.FEC_HOY);
	NS.txtPuesto = new Ext.form.TextField({
		id: PF + 'txtPuesto',
		name: PF + 'txtPuesto',
		x: 810,
		y: 65,
		hidden: true,
		width: 150		
	});

	NS.txtIdGrupo = new Ext.form.TextField({
		id: PF + 'txtIdGrupo',
		name: PF + 'txtIdGrupo',
		x: 0,
		y: 115,
		hidden: true,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdGrupo', NS.cmbGrupo.getId());
					else
						NS.cmbGrupo.reset();
				}				
			}
		}
	});
	
	NS.txtIdActividadEconomica = new Ext.form.TextField({
		id: PF + 'txtIdActividadEconomica',
		name: PF + 'txtIdActividadEconomica',
		x: 235,
		y: 115,
		hidden: true,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdActividadEconomica', NS.cmbActividadEconomica.getId());
					else
						NS.cmbActividadEconomica.reset();
				}
			}
		}
	});
	
	NS.txtIdActividadGenerica = new Ext.form.TextField({
		id: PF + 'txtIdActividadGenerica',
		name: PF + 'txtIdActividadGenerica',		
		x: 465,
		y: 115,
		hidden: true,
		width: 50,		
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdActividadGenerica', NS.cmbActividadGenerica.getId());
					else
						NS.cmbActividadGenerica.reset();
				}
			}
		}
	});
	
	NS.txtIdGiro = new Ext.form.TextField({
		id: PF + 'txtIdGiro',
		name: PF + 'txtIdGiro',
		x: 695,
		y: 115,
		hidden: true,
		width: 50,		
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdGiro', NS.cmbGiro.getId());
					else
						NS.cmbGiro.reset();
				}
			}
		}
	});
	
	NS.txtIdCaja = new Ext.form.TextField({
		id: PF + 'txtIdCaja',
		name: PF + 'txtIdCaja',
		x: 0,
		y: 165,
		hidden: true,
		width: 50,		
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdCaja', NS.cmbCaja.getId());
					else
						NS.cmbCaja.reset();
				}
			}
		}
	});
	
	NS.txtIdCambioPersona = new Ext.form.TextField({
		id: PF + 'txtIdCambioPersona',
		name: PF + 'txtIdCambioPersona',
		x: 0,
		y: 165,
		hidden: true,
		width: 50,		
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdCambioPersona', NS.cmbCambioPersona.getId());
					else
						NS.cmbCambioPersona.reset();
				}
			}
		}
	});
	
	NS.txtIdRiesgo = new Ext.form.TextField({
		id: PF + 'txtIdRiesgo',
		name: PF + 'txtIdRiesgo',
		x: 0,
		y: 165,
		hidden: true,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdRiesgo', NS.cmbRiesgo.getId());
					else
						NS.cmbRiesgo.reset();
				}
			}
		}		
	});
	
	NS.txtIdTamano = new Ext.form.TextField({
		id: PF + 'txtIdTamano',
		name: PF + 'txtIdTamano',
		x: 235,
		y: 165,
		hidden: true,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdTamano', NS.cmbTamano.getId());
					else 
						NS.cmbTamano.reset();
				} 
			}
		}
	});
	
	NS.txtIdCalidad = new Ext.form.TextField({
		id: PF + 'txtIdCalidad',
		name: PF + 'txtIdCalidad',
		x: 465,
		y: 165,
		hidden: true,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdCalidad', NS.cmbCalidad.getId());
					else
						NS.cmbCalidad.reset();
				}
			}
		}
	});
	
	NS.txtIdInmueble = new Ext.form.TextField({
		id: PF + 'txtIdInmueble',
		name: PF + 'txtIdInmueble',
		x: 695,
		y: 165,
		hidden: true,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdInmueble', NS.cmbInmueble.getId());
					else
						NS.cmbInmueble.reset();
				}
 			}
		}
	});
	
	NS.txtVentasAnuales ={
		xtype: 'uppertextfield',
		id: PF + 'txtVentasAnuales',
		name: PF + 'txtVentasAnuales',
		x: 0,
		y: 215,
		hidden: true,
		width: 150		
	};
	
	NS.txtObjetoSocial ={
		xtype: 'uppertextfield',
		id: PF + 'txtObjetoSocial',
		name: PF + 'txtObjetoSocial',
		x: 180,
		y: 215,
		hidden: true,
		width: 150
	};
	
	NS.txtNoEmpleados = {
		xtype: 'uppertextfield',
		id: PF + 'txtNoEmpleados',
		name: PF + 'txtNoEmpleados',
		x: 360,
		y: 215,
		hidden: true,
		width: 100
	};
	
	NS.txtDiaLimite = new Ext.form.TextField({
		id: PF + 'txtDiaLimite',
		name: PF + 'txtDiaLimite',
		x: 490,
		y: 215,
		hidden: true,
		width: 100
	});
	
	NS.txtDiaRecepcion = new Ext.form.TextField({
		id: PF + 'txtDiaRecepcion',
		name: PF + 'txtDiaRecepcion',
		x: 620,
		y: 215,
		hidden: true,
		width: 100
	});
	
	NS.txtIdFormaPagoProv = new Ext.form.TextField({
		id: PF + 'txtIdFormaPagoProv',
		name: PF + 'txtIdFormaPagoProv',
		x: 250,
		y: 130,


		hidden: true,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdFormaPagoProv', NS.cmbFormaPagoProv.getId());
					else
						NS.cmbFormaPagoProv.reset();
				}
			}
		}
	});	
	
	NS.txtAccionesA = {
		xtype: 'uppertextfield',
		id: PF + 'txtAccionesA',
		name: PF + 'txtAccionesA',		
		x: 180,
		y: 265,
		hidden: true,
		width: 100		
	};
	
	NS.txtAccionesB ={
		xtype: 'uppertextfield',
		id: PF + 'txtAccionesB',
		name: PF + 'txtAccionesB',
		x: 310,
		y: 265,
		hidden: true,
		width: 100
	};
	
	NS.txtAccionesC = {
		xtype: 'uppertextfield',
		id: PF + 'txtAccionesC',
		name: PF + 'txtAccionesC',
		x: 440,
		y: 265,
		hidden: true,
		width: 100
	};
	
	NS.txtNoBenef ={
		xtype: 'uppertextfield',
		id: PF + 'txtNoBenef',
		name: PF + 'txtNoBenef',
		x: 500,
		y: 20,		
		width: 60,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoBenef', NS.cmbBenef.getId());
					else
						NS.cmbBenef.reset();
				}
			}
		}
	};
	
	NS.txtGrupoRubro = new Ext.form.TextField({
		id: PF + 'txtGrupoRubro',
		name: PF + 'txtGrupoRubro',
		x: 0,
		y: 20,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined) {
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtGrupoRubro', NS.cmbGrupoRubro.getId());
					
						if(comboValue == null || comboValue == undefined || comboValue == '')
							Ext.getCmp(PF + 'txtGrupoRubro').setValue('');
						else {
							Ext.getCmp(NS.txtRubro.getId()).setValue('');
							Ext.getCmp(NS.cmbRubro.getId()).setValue('');
							NS.storeRubroEgreso.removeAll();
							NS.storeRubroEgreso.baseParams.idGrupo = parseInt(NS.txtGrupoRubro.getValue());									
							NS.storeRubroEgreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF + 'txtNoEmpresa').getValue());
							NS.storeRubroEgreso.load();
						}
					}else {
						NS.cmbGrupoRubro.reset();
						NS.cmbRubro.reset();
						Ext.getCmp(PF + 'txtRubro').setValue('');
						Ext.getCmp(PF + 'txtGrupoRubro').setValue('');
					}
				}
			}
		}
	});
	
	NS.txtRubro = new Ext.form.TextField({
		id: PF + 'txtRubro',
		name: PF + 'txtRubro',
		x: 250,
		y: 20,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined) {
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtRubro', NS.cmbRubro.getId());

						if(comboValue == null || comboValue == undefined || comboValue == '')
							Ext.getCmp(PF + 'txtRubro').setValue('');
					}else
						NS.cmbRubro.reset();
				}
			}
		}
	});
	
	//FUNCIONES
	
	NS.fechaE = function(){
		Ext.getCmp(PF + 'txtFechaIngresoE').setValue(apps.SET.FEC_HOY);
	};
	
	
	NS.limpiarDI = function(){
		
		Ext.getCmp(PF + 'txtNoPersonaD').setValue('');
		Ext.getCmp(PF + 'txtRazonSocialD').setValue('');
		Ext.getCmp(PF + 'txtIdTipoD').setValue('');
		Ext.getCmp(PF + 'txtCalle').setValue('');
		Ext.getCmp(PF + 'txtColonia').setValue('');
		Ext.getCmp(PF + 'txtCp').setValue('');
		Ext.getCmp(PF + 'txtDelegacion').setValue('');
		Ext.getCmp(PF + 'txtCiudad').setValue('');
		//Ext.getCmp(PF + 'txtIdEstado').setValue('');
		NS.txtIdEstado.setValue('');
		Ext.getCmp(PF + 'txtPais').setValue('');
		Ext.getCmp(PF + 'cmbTipoDireccion').reset();
		Ext.getCmp(PF + 'cmbEstado').reset();
		Ext.getCmp(PF + 'cmbPais').reset();
		
	};
	
	
	
	NS.limpiarE = function(){
		Ext.getCmp(PF + 'txtEmpresaE').setValue('');
		Ext.getCmp(PF + 'txtRazonSocialE').setValue('');
		Ext.getCmp(PF + 'txtNombreCortoE').setValue('');
		Ext.getCmp(PF + 'txtRFCE').setValue('');
		Ext.getCmp(PF + 'txtFechaIngresoE').setValue('');
		Ext.getCmp(PF + 'txtIdGrupoE').setValue('');
		Ext.getCmp(PF + 'txtIdCajaE').setValue('');
		Ext.getCmp(PF + 'txtIdCalidadE').setValue('');
		Ext.getCmp(PF + 'txtIdInmuebleE').setValue('');
		NS.cmbGrupoE.reset();
		NS.cmbActividadEconomicaE.reset();
		NS.cmbActividadGenericaE.reset();
		NS.cmbGiroE.reset();
		NS.cmbCajaE.reset();
		NS.cmbCalidadE.reset();
		NS.cmbInmuebleE.reset();
	};
	
	
	NS.limpiarCamposNuevos = function(){
		Ext.getCmp(PF + 'txtEmpresaGral').setValue('');
		Ext.getCmp(PF + 'txtNoPersonaGral').setValue('');
		Ext.getCmp(PF + 'txtEquivalePersona').setValue('');
		Ext.getCmp(PF + 'txtRazonSocialGral').setValue('');
		Ext.getCmp(PF + 'txtPaternoGral').setValue('');
		Ext.getCmp(PF + 'txtMaternoGral').setValue('');
		Ext.getCmp(PF + 'txtNombreGral').setValue('');
		Ext.getCmp(PF + 'txtNombreCorto').setValue('');
		Ext.getCmp(PF + 'txtRFCGral').setValue('');
		Ext.getCmp(PF + 'txtFechaIngreso').setValue('');
		Ext.getCmp(PF + 'txtPuesto').setValue('');
		Ext.getCmp(PF + 'txtIdGrupo').setValue('');
		Ext.getCmp(PF + 'txtGrupoRubro').setValue('');
		Ext.getCmp(PF + 'txtRubro').setValue('');
		Ext.getCmp(PF + 'cmbGrupoRubro').reset();
		Ext.getCmp(PF + 'cmbRubro').reset();
		Ext.getCmp(PF + 'txtIdActividadEconomica').setValue('');
		Ext.getCmp(PF + 'txtIdActividadGenerica').setValue('');
		Ext.getCmp(PF + 'txtIdEstadoCivil').setValue('');
		Ext.getCmp(PF + 'txtIdGiro').setValue('');
		Ext.getCmp(PF + 'txtIdCaja').setValue('');
		Ext.getCmp(PF + 'txtIdCambioPersona').setValue('');
		Ext.getCmp(PF + 'txtIdRiesgo').setValue('');
		Ext.getCmp(PF + 'txtIdTamano').setValue('');
		Ext.getCmp(PF + 'txtIdCalidad').setValue('');
		Ext.getCmp(PF + 'txtIdInmueble').setValue('');
		Ext.getCmp(PF + 'txtVentasAnuales').setValue('');
		Ext.getCmp(PF + 'txtObjetoSocial').setValue('');
		Ext.getCmp(PF + 'txtNoEmpleados').setValue('');
		Ext.getCmp(PF + 'txtDiaLimite').setValue('');
		Ext.getCmp(PF + 'txtDiaRecepcion').setValue('');
		Ext.getCmp(PF + 'txtIdFormaPagoProv').setValue('');
		Ext.getCmp(PF + 'txtAccionesA').setValue('');
		Ext.getCmp(PF + 'txtAccionesB').setValue('');
		Ext.getCmp(PF + 'txtAccionesC').setValue('');
		NS.cmbGrupo.reset();
		NS.cmbActividadEconomica.reset();
		NS.cmbActividadGenerica.reset();
		NS.cmbEstadoCivil.reset();
		NS.cmbGiro.reset();
		NS.cmbCaja.reset();
		NS.cmbCambioPersona.reset();
		NS.cmbRiesgo.reset();
		NS.cmbTamano.reset();
		NS.cmbCalidad.reset();
		NS.cmbInmueble.reset();
		NS.cmbFormaPagoProv.reset();
		NS.cmbPagoReferenciado.reset();
		Ext.getCmp(PF + 'chkDivision').setValue(false);
		Ext.getCmp(PF + 'chkPagosCruzados').setValue(false);
		Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setValue(false);
		Ext.getCmp(PF + 'chkProveedor').setValue(false);
		Ext.getCmp(PF + 'chkAsociacion').setValue(false);
		Ext.getCmp(PF + 'chkContratoInversion').setValue(false);		
	};
	
	
	/*NS.ocultaTodosCampos = function(){
		NS.lblEmpresaGral.setVisible(false);
		NS.lblNoPersonaGral.setVisible(false);
		NS.lblPaternoGral.setVisible(false);
		NS.lblMaternoGral.setVisible(false);
		NS.lblNombreGral.setVisible(false);
		NS.lblRazonSocialGral.setVisible(false);
		NS.lblNombreCorto.setVisible(false);
		NS.lblRFCGral.setVisible(false);
		NS.lblFechaIngreso.setVisible(false);
		NS.lblPuesto.setVisible(false);
		//NS.lblGrupoFlujo.setVisible(false);
		//NS.lblActividadEconomica.setVisible(false);
		//NS.lblActividadGenerica.setVisible(false);
		NS.lblEstadoCivil.setVisible(false);
		//NS.lblGiro.setVisible(false);
		NS.lblRiesgo.setVisible(false);
		NS.lblCambioPersona.setVisible(false);
		//NS.lblCaja.setVisible(false);
		NS.lblTamano.setVisible(false);
		//NS.lblCalidad.setVisible(false);
		//NS.lblTipoInmueble.setVisible(false);
		//NS.lblVentasAnuales.setVisible(false);
		NS.lblObjetoSocial.setVisible(false);
		NS.lblNoEmpleados.setVisible(false);
		NS.lblDiaLimite.setVisible(false);
		NS.lblDiaRecepcion.setVisible(false);
		NS.lblFormaPagoProv.setVisible(false);
		NS.lblPagoReferenciado.setVisible(false);
		NS.lblAccionesA.setVisible(false);
		NS.lblAccionesB.setVisible(false);
		NS.lblAccionesC.setVisible(false);
				
		Ext.getCmp(PF + 'txtEmpresaGral').setVisible(false);
		Ext.getCmp(PF + 'txtNoPersonaGral').setVisible(false);
		Ext.getCmp(PF + 'txtEquivalePersona').setVisible(false);
		Ext.getCmp(PF + 'txtRazonSocialGral').setVisible(false);
		Ext.getCmp(PF + 'txtPaternoGral').setVisible(false);
		Ext.getCmp(PF + 'txtMaternoGral').setVisible(false);
		Ext.getCmp(PF + 'txtNombreGral').setVisible(false);
		Ext.getCmp(PF + 'txtNombreCorto').setVisible(false);
		Ext.getCmp(PF + 'txtRFCGral').setVisible(false);
		Ext.getCmp(PF + 'txtFechaIngreso').setVisible(false);
		Ext.getCmp(PF + 'txtPuesto').setVisible(false);
		Ext.getCmp(PF + 'txtIdGrupo').setVisible(false);
		//Ext.getCmp(PF + 'txtIdActividadEconomica').setVisible(false);
		//Ext.getCmp(PF + 'txtIdActividadGenerica').setVisible(false);
		//Ext.getCmp(PF + 'txtIdGiro').setVisible(false);
		//Ext.getCmp(PF + 'txtIdCaja').setVisible(false);
		Ext.getCmp(PF + 'txtIdCambioPersona').setVisible(false);
		Ext.getCmp(PF + 'txtIdRiesgo').setVisible(false);
		Ext.getCmp(PF + 'txtIdTamano').setVisible(false);
		//Ext.getCmp(PF + 'txtIdCalidad').setVisible(false);
		//Ext.getCmp(PF + 'txtIdInmueble').setVisible(false);
		//Ext.getCmp(PF + 'txtVentasAnuales').setVisible(false);
		//Ext.getCmp(PF + 'txtObjetoSocial').setVisible(false);
		Ext.getCmp(PF + 'txtNoEmpleados').setVisible(false);
		Ext.getCmp(PF + 'txtDiaLimite').setVisible(false);
		Ext.getCmp(PF + 'txtDiaRecepcion').setVisible(false);
		Ext.getCmp(PF + 'txtIdFormaPagoProv').setVisible(false);
		Ext.getCmp(PF + 'txtAccionesA').setVisible(false);
		Ext.getCmp(PF + 'txtAccionesB').setVisible(false);
		Ext.getCmp(PF + 'txtAccionesC').setVisible(false);		
		//Ext.getCmp(PF + 'txtIdActividadGenerica').setVisible(false);
		//Ext.getCmp(PF + 'txtIdActividadGenerica').setVisible(false);
		//Ext.getCmp(PF + 'txtIdActividadGenerica').setVisible(false);
		//NS.cmbEmpresa.setVisible(false);
		//NS.cmbTipoPersona.setVisible(false);
		NS.cmbGrupo.setVisible(false);
		//NS.cmbActividadEconomica.setVisible(false);
		//NS.cmbActividadGenerica.setVisible(false);
		NS.cmbEstadoCivil.setVisible(false);
		//NS.cmbGiro.setVisible(false);
		//NS.cmbCaja.setVisible(false);
		NS.cmbCambioPersona.setVisible(false);
		NS.cmbRiesgo.setVisible(false);
		NS.cmbTamano.setVisible(false);
		//NS.cmbCalidad.setVisible(false);
		//NS.cmbInmueble.setVisible(false);
		NS.cmbFormaPagoProv.setVisible(false);
		NS.cmbPagoReferenciado.setVisible(false);
		NS.panelSexo.setVisible(false);
	};
	*/
	
	ConsultaPersonasAction.configuraSet(243, function(resultado, e){		
		if (resultado != '' && resultado != null && resultado != undefined){
			if (resultado == 'SI')
				NS.manejaDivision = true;
			else
				NS.manejaDivision = false;
		}
		else
			NS.manejaDivision = false;
			
	});
	
	NS.buscar = function(){
		if(Ext.getCmp(PF + 'txtTipoPersona').getValue()=='P'){
			if (Ext.getCmp(PF + 'txtNoPersona').getValue()=='' && 
					Ext.getCmp(PF + 'txtRazonSocial').getValue()=='') {
				Ext.Msg.alert('SET', 'Debe de indicar el número de persona o razon social');
				Ext.getCmp(PF + 'buscar').setDisabled(false);
			}else{
				if(Ext.getCmp(PF + 'txtRazonSocial').getValue()!=''){
				///////////////////////////////
				if(Ext.getCmp(PF + 'txtRazonSocial').getValue().length < 4){
					Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
					
				}else{
					ConsultaPersonasAction.validaDatos(Ext.getCmp(PF + 'txtTipoPersona').getValue(), function(resultado, e){
						if (resultado != '' && resultado != 0 && resultado != undefined)
							Ext.Msg.alert('SET', resultado);
						else if (resultado == ''){				
							NS.storeLlenaGrid.baseParams.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
							NS.storeLlenaGrid.baseParams.equivalePersona = Ext.getCmp(PF + 'txtNoPersona').getValue();
							NS.storeLlenaGrid.baseParams.razonSocial = Ext.getCmp(PF + 'txtRazonSocial').getValue();
							NS.storeLlenaGrid.baseParams.paterno = Ext.getCmp(PF + 'txtPaterno').getValue();
							NS.storeLlenaGrid.baseParams.materno = Ext.getCmp(PF + 'txtMaterno').getValue();
							NS.storeLlenaGrid.baseParams.nombre = Ext.getCmp(PF + 'txtNombre').getValue();
							NS.storeLlenaGrid.baseParams.inactivas = Ext.getCmp(PF + 'chkInactivas').getValue();
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando ..."});
							NS.storeLlenaGrid.load();	
						}
						});
					
				}
				
				////////////////////
				
				}else{
					ConsultaPersonasAction.validaDatos(Ext.getCmp(PF + 'txtTipoPersona').getValue(), function(resultado, e){
						if (resultado != '' && resultado != 0 && resultado != undefined)
							Ext.Msg.alert('SET', resultado);
						else if (resultado == ''){				
							NS.storeLlenaGrid.baseParams.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
							NS.storeLlenaGrid.baseParams.equivalePersona = Ext.getCmp(PF + 'txtNoPersona').getValue();
							NS.storeLlenaGrid.baseParams.razonSocial = Ext.getCmp(PF + 'txtRazonSocial').getValue();
							NS.storeLlenaGrid.baseParams.paterno = Ext.getCmp(PF + 'txtPaterno').getValue();
							NS.storeLlenaGrid.baseParams.materno = Ext.getCmp(PF + 'txtMaterno').getValue();
							NS.storeLlenaGrid.baseParams.nombre = Ext.getCmp(PF + 'txtNombre').getValue();
							NS.storeLlenaGrid.baseParams.inactivas = Ext.getCmp(PF + 'chkInactivas').getValue();
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando ..."});
							NS.storeLlenaGrid.load();	
						}
						});
				}
			
			}
		}else {
				
				ConsultaPersonasAction.validaDatos(Ext.getCmp(PF + 'txtTipoPersona').getValue(), function(resultado, e){
				if (resultado != '' && resultado != 0 && resultado != undefined)
					Ext.Msg.alert('SET', resultado);
				else if (resultado == ''){				
					NS.storeLlenaGrid.baseParams.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
					NS.storeLlenaGrid.baseParams.equivalePersona = Ext.getCmp(PF + 'txtNoPersona').getValue();
					NS.storeLlenaGrid.baseParams.razonSocial = Ext.getCmp(PF + 'txtRazonSocial').getValue();
					NS.storeLlenaGrid.baseParams.paterno = Ext.getCmp(PF + 'txtPaterno').getValue();
					NS.storeLlenaGrid.baseParams.materno = Ext.getCmp(PF + 'txtMaterno').getValue();
					NS.storeLlenaGrid.baseParams.nombre = Ext.getCmp(PF + 'txtNombre').getValue();
					NS.storeLlenaGrid.baseParams.inactivas = Ext.getCmp(PF + 'chkInactivas').getValue();
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando ..."});
					NS.storeLlenaGrid.load();	
				}
				
				});
			}
//		Ext.MessageBox.hide();
	};
	
	NS.cambiarFecha = function (fecha)
	{
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		var mes;
		
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
			mes = i+1;
				if(mes<10)
					mes='0'+mes;
			}
		}		
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	};
	
	NS.limpiar = function(){
		//Ext.getCmp(PF + 'txtNoEmpresa').setValue('');
		//Ext.getCmp(PF + 'cmbEmpresa').reset();
		Ext.getCmp(PF + 'chkInactivas').setValue(false);
		Ext.getCmp(PF + 'txtTipoPersona').setValue('');
		Ext.getCmp(PF + 'cmbTipoPersona').reset();
		Ext.getCmp(PF + 'txtNoPersona').setValue('');
		Ext.getCmp(PF + 'txtRazonSocial').setValue('');
		Ext.getCmp(PF + 'txtPaterno').setValue('');
		Ext.getCmp(PF + 'txtMaterno').setValue('');
		Ext.getCmp(PF + 'txtNombre').setValue('');
		//Ext.getCmp(PF + 'txtRFC').setValue('');
		//Ext.getCmp(PF + 'txtFechaInicio').setValue('');
		//Ext.getCmp(PF + 'txtFechaFin').setValue('');
		//Ext.getCmp(PF + 'txtGrupoRubro').setValue('');
		//Ext.getCmp(PF + 'txtRubro').setValue('');
		//Ext.getCmp(PF + 'cmbGrupoRubro').reset();
		//Ext.getCmp(PF + 'cmbRubro').reset();
		NS.gridConsulta.store.removeAll();
		NS.gridConsulta.getView().refresh();	
	};
	
	
	NS.llenaCamposIB = function(registro){
		Ext.getCmp(PF + 'txtNoPersonaIB').setValue(registro[0].noPersona);
		Ext.getCmp(PF + 'txtRazonSocialIB').setValue(registro[0].razonSocial);
		Ext.getCmp(PF + 'txtNombreCortoIB').setValue(registro[0].nombreCorto);
		Ext.getCmp(PF + 'txtRFCIB').setValue(registro[0].rfc);
		Ext.getCmp(PF + 'txtFechaIngresoIB').setValue(registro[0].fechaIngreso);
	};
		
	NS.llenaCamposFisica = function(registro){		
		Ext.getCmp(PF + 'txtNoPersonaF').setValue(registro[0].noPersona);
		Ext.getCmp(PF + 'txtPaternoF').setValue(registro[0].paterno);
		Ext.getCmp(PF + 'txtMaternoF').setValue(registro[0].materno);
		Ext.getCmp(PF + 'txtNombreF').setValue(registro[0].nombre);
		Ext.getCmp(PF + 'txtNombreCortoF').setValue(registro[0].nombreCorto);
		Ext.getCmp(PF + 'txtRFCF').setValue(registro[0].rfc);
		
		if (registro[0].sexo == 'F'){			
			Ext.getCmp(PF + 'optRadioSexoF').setValue(1);
			NS.sexo = "F"
			}
		else{			
			Ext.getCmp(PF + 'optRadioSexoF').setValue(0);
			NS.sexo = "M";
		}
	};
	
	NS.llenaCamposMoral = function(registro){	
		Ext.getCmp(PF + 'txtNoPersonaGral').setVisible(false);
		Ext.getCmp(PF + 'txtEquivalePersona').setVisible(true);
		Ext.getCmp(PF + 'txtEquivalePersona').setDisabled(true);
		Ext.getCmp(PF + 'txtNoPersonaGral').setValue(registro[0].noPersona);
		Ext.getCmp(PF + 'txtEquivalePersona').setValue(registro[0].noPersona);
		Ext.getCmp(PF + 'txtRazonSocialGral').setValue(registro[0].razonSocial);
		Ext.getCmp(PF + 'txtNombreCorto').setValue(registro[0].nombreCorto);
		Ext.getCmp(PF + 'txtRFCGral').setValue(registro[0].rfc);		
		Ext.getCmp(PF + 'txtIdActividadEconomica').setValue(registro[0].idActividadEconomica);
		Ext.getCmp(PF + 'cmbActividadEconomica').setValue(registro[0].descActividadEconomica);		
		Ext.getCmp(PF + 'txtIdActividadGenerica').setValue(registro[0].idActividadGenerica);
		Ext.getCmp(PF + 'cmbActividadGenerica').setValue(registro[0].descActividadGenerica);		
		Ext.getCmp(PF + 'txtIdGiro').setValue(registro[0].idGiro);
		Ext.getCmp(PF + 'cmbGiro').setValue(registro[0].descGiro);			
		Ext.getCmp(PF + 'txtIdRiesgo').setValue(registro[0].idRiesgo);
		Ext.getCmp(PF + 'cmbRiesgo').setValue(registro[0].descRiesgo);		
		Ext.getCmp(PF + 'txtIdCalidad').setValue(registro[0].idCalidad);
		Ext.getCmp(PF + 'cmbCalidad').setValue(registro[0].descCalidad);		
		Ext.getCmp(PF + 'txtIdTamano').setValue(registro[0].idTamano);
		Ext.getCmp(PF + 'cmbTamano').setValue(registro[0].descTamano);		
		Ext.getCmp(PF + 'txtIdInmueble').setValue(registro[0].idInmueble);
		Ext.getCmp(PF + 'cmbInmueble').setValue(registro[0].descInmueble);
		Ext.getCmp(PF + 'txtObjetoSocial').setValue(registro[0].objetoSocial);
		Ext.getCmp(PF + 'txtVentasAnuales').setValue(registro[0].ventasAnuales);
		Ext.getCmp(PF + 'txtNoEmpleados').setValue(registro[0].noEmpleados);
		Ext.getCmp(PF + 'txtDiaLimite').setValue(registro[0].diaLimite);
		Ext.getCmp(PF + 'txtDiaRecepcion').setValue(registro[0].diaRecepcion);
		Ext.getCmp(PF + 'txtAccionesA').setValue(registro[0].accionA);
		Ext.getCmp(PF + 'txtAccionesB').setValue(registro[0].accionB);
		Ext.getCmp(PF + 'txtAccionesC').setValue(registro[0].accionC);
		
		//Ext.getCmp(PF + 'txtGrupoRubro').setValue(registro[0].sGrupoRubro);
		//Ext.getCmp(PF + 'cmbGrupoRubro').setValue(registro[0].sDescGrupoRubro);
		Ext.getCmp(PF + 'txtRubro').setValue(registro[0].sRubro);
		Ext.getCmp(PF + 'cmbRubro').setValue(registro[0].sDescRubro);
		
		if (registro[0].bProveedor == 'S')
			Ext.getCmp(PF + 'chkProveedor').setValue(true);
		else
			Ext.getCmp(PF + 'chkProveedor').setValue(false);
		
		if (registro[0].bAsociacion == 'S')
			Ext.getCmp(PF + 'chkAsociacion').setValue(true);
		else
			Ext.getCmp(PF + 'chkAsociacion').setValue(false);
		
		if (registro[0].bContratoInversion == 'S')
			Ext.getCmp(PF + 'chkContratoInversion').setValue(true);
		else
			Ext.getCmp(PF + 'chkContratoInversion').setValue(false);		
	};
	
	NS.llenaCamposDI= function(){
	Ext.getCmp(PF + 'txtNoPersonaD').setValue(Ext.getCmp(PF + 'txtEmpresaE').getValue());
	Ext.getCmp(PF + 'txtRazonSocialD').setValue(Ext.getCmp(PF + 'txtRazonSocialE').getValue())
	};
	
	NS.llenaCamposDIIB= function(){
		Ext.getCmp(PF + 'txtNoPersonaD').setValue(Ext.getCmp(PF + 'txtNoPersonaIB').getValue());
		Ext.getCmp(PF + 'txtRazonSocialD').setValue(Ext.getCmp(PF + 'txtRazonSocialIB').getValue());
		
		};
		
		NS.llenaCamposDIPF= function(){
			Ext.getCmp(PF + 'txtNoPersonaD').setValue(Ext.getCmp(PF + 'txtNoPersonaPF').getValue());
			Ext.getCmp(PF + 'txtRazonSocialD').setValue(Ext.getCmp(PF + 'txtNombrePF').getValue()
					+ " "+ Ext.getCmp(PF + 'txtPaternoPF').getValue()
					+ " "+ Ext.getCmp(PF + 'txtMaternoPF').getValue());
						
						};
		
	NS.llenaCamposDIK= function(){
		Ext.getCmp(PF + 'txtNoPersonaD').setValue(Ext.getCmp(PF + 'txtNoPersonaK').getValue());
		Ext.getCmp(PF + 'txtRazonSocialD').setValue(Ext.getCmp(PF + 'txtRazonSocialK').getValue());
			
			};
			
	NS.llenaCamposDIPM= function(){
		Ext.getCmp(PF + 'txtNoPersonaD').setValue(Ext.getCmp(PF + 'txtEmpresaPM').getValue());
		Ext.getCmp(PF + 'txtRazonSocialD').setValue(Ext.getCmp(PF + 'txtRazonSocialPM').getValue());
		
		
	};		
			
			
	NS.llenaCamposDIF= function(){
		Ext.getCmp(PF + 'txtNoPersonaD').setValue(Ext.getCmp(PF + 'txtNoPersonaF').getValue());
		Ext.getCmp(PF + 'txtRazonSocialD').setValue(Ext.getCmp(PF + 'txtNombreF').getValue()
				+ " "+ Ext.getCmp(PF + 'txtPaternoF').getValue()
				+ " "+ Ext.getCmp(PF + 'txtMaternoF').getValue());
					
					};
	
	NS.llenaCamposCP= function(){
		Ext.getCmp(PF + 'txtEmpresaCuentas').setValue(Ext.getCmp(PF + 'txtEmpresaPM').getValue());
		Ext.getCmp(PF + 'txtRazonSocialCuentas').setValue(Ext.getCmp(PF + 'txtRazonSocialPM').getValue())
		};
	
	NS.llenaCamposK = function(registro){	
		//Ext.getCmp(PF + 'txtEmpresaK').setValue(registro[0].noEmpresa);
		Ext.getCmp(PF + 'txtNoPersonaK').setValue(registro[0].noPersona);
		Ext.getCmp(PF + 'txtRazonSocialK').setValue(registro[0].razonSocial);
		Ext.getCmp(PF + 'txtNombreCortoK').setValue(registro[0].nombreCorto);
		Ext.getCmp(PF + 'txtRFCK').setValue(registro[0].rfc);				
		Ext.getCmp(PF + 'txtFechaIngresoK').setValue(registro[0].fechaIngreso);
	
	};
	
	NS.llenaCamposPM = function(registro){	
		Ext.getCmp(PF + 'txtEmpresaPM').setValue(registro[0].noPersona);
		Ext.getCmp(PF + 'txtEquivalePerPM').setValue(registro[0].equivalePersona);
		Ext.getCmp(PF + 'txtReferenciaPM').setValue(registro[0].referencia);
		Ext.getCmp(PF + 'txtRazonSocialPM').setValue(registro[0].razonSocial);
		Ext.getCmp(PF + 'txtNombreCortoPM').setValue(registro[0].nombreCorto);
		Ext.getCmp(PF + 'txtRFCPM').setValue(registro[0].rfc);				
//		console.log(registro[0].fechaIngreso);
		Ext.getCmp(PF + 'txtFechaIngresoPM').setValue(registro[0].fechaIngreso);
		Ext.getCmp(PF + 'txtIdFormaPagoProvPM').setValue(registro[0].idFormaPago);
		Ext.getCmp(PF + 'cmbFormaPagoProvPM').setValue(registro[0].descFormaPago);	
		NS.empre1=registro[0].noEmpresa;
	};
	
	NS.llenaCamposEmpresa = function(registro){	
		Ext.getCmp(PF + 'txtEmpresaE').setValue(registro[0].noEmpresa);
		Ext.getCmp(PF + 'txtRazonSocialE').setValue(registro[0].razonSocial);
		Ext.getCmp(PF + 'txtNombreCortoE').setValue(registro[0].nombreCorto);
		Ext.getCmp(PF + 'txtRFCE').setValue(registro[0].rfc);				
		Ext.getCmp(PF + 'txtFechaIngresoE').setValue(registro[0].fechaIngreso);
		Ext.getCmp(PF + 'txtIdGrupoE').setValue(registro[0].idGrupo);
		Ext.getCmp(PF + 'cmbGrupoE').setValue( registro[0].descGrupo);
		Ext.getCmp(PF + 'txtIdCalidadE').setValue(registro[0].idCalidad);
		Ext.getCmp(PF + 'cmbCalidadE').setValue(registro[0].descCalidad);		
		Ext.getCmp(PF + 'txtIdInmuebleE').setValue(registro[0].idInmueble);
		Ext.getCmp(PF + 'cmbInmuebleE').setValue(registro[0].descInmueble);	
		

		if (registro[0].concentradora == 'S'){			
			Ext.getCmp(PF + 'optConcentradoraE').setValue(0);
			NS.concentradora = "S";
			}
		else{			
			Ext.getCmp(PF + 'optConcentradoraE').setValue(1);
			NS.concentradora = "N";
		}
	};
	
	NS.HabilitarCamposDI = function(){
		Ext.getCmp(PF + 'txtNoPersonaD').setDisabled(false);
		Ext.getCmp(PF + 'txtRazonSocialD').setDisabled(false);	
		Ext.getCmp(PF + 'txtIdTipoD').setDisabled(false);
		Ext.getCmp(PF + 'txtCalle').setDisabled(false);				
		Ext.getCmp(PF + 'txtColonia').setDisabled(false);
		Ext.getCmp(PF + 'txtCp').setDisabled(false);
		Ext.getCmp(PF + 'txtDelegacion').setDisabled(false);
		Ext.getCmp(PF + 'txtCiudad').setDisabled(false);
		Ext.getCmp(PF + 'txtEstado').setDisabled(false);
		Ext.getCmp(PF + 'txtPais').setDisabled(false);
		Ext.getCmp(PF + 'cmbTipoDireccion').setDisabled(false);
		Ext.getCmp(PF + 'cmbEstado').setDisabled(false);
		Ext.getCmp(PF + 'cmbPais').setDisabled(false);
	};
	
	NS.DCD = function(){
		Ext.getCmp(PF + 'txtNoPersonaD').setDisabled(true);
		Ext.getCmp(PF + 'txtRazonSocialD').setDisabled(true);	
	};
	
	NS.desEmpresa = function(){
		Ext.getCmp(PF + 'txtEmpresaE').setDisabled(true);
	};
	
	NS.habEmpresa = function(){
		Ext.getCmp(PF + 'txtEmpresaE').setDisabled(false);
	};
	
	
	NS.DesabilitarCamposDI = function(){
		Ext.getCmp(PF + 'txtNoPersonaD').setDisabled(true);
		Ext.getCmp(PF + 'txtRazonSocialD').setDisabled(true);	
		Ext.getCmp(PF + 'txtIdTipoD').setDisabled(true);
		Ext.getCmp(PF + 'txtCalle').setDisabled(true);				
		Ext.getCmp(PF + 'txtColonia').setDisabled(true);
		Ext.getCmp(PF + 'txtCp').setDisabled(true);
		Ext.getCmp(PF + 'txtDelegacion').setDisabled(true);
		Ext.getCmp(PF + 'txtCiudad').setDisabled(true);
		Ext.getCmp(PF + 'txtEstado').setDisabled(true);
		Ext.getCmp(PF + 'txtPais').setDisabled(true);
		Ext.getCmp(PF + 'cmbTipoDireccion').setDisabled(true);
		Ext.getCmp(PF + 'cmbEstado').setDisabled(true);
		Ext.getCmp(PF + 'cmbPais').setDisabled(true);
	};
	
	NS.llenaCamposDIR = function(registro){	
		Ext.getCmp(PF + 'txtIdTipoD').setValue(registro[0].idTipoD);
		Ext.getCmp(PF + 'txtCalle').setValue(registro[0].calle);				
		Ext.getCmp(PF + 'txtColonia').setValue(registro[0].colonia);
		Ext.getCmp(PF + 'txtCp').setValue(registro[0].cp);
		Ext.getCmp(PF + 'txtDelegacion').setValue(registro[0].delegacion);
		Ext.getCmp(PF + 'txtCiudad').setValue(registro[0].ciudad);
		Ext.getCmp(PF + 'txtEstado').setValue(registro[0].estado);
		Ext.getCmp(PF + 'txtPais').setValue(registro[0].pais);
		Ext.getCmp(PF + 'cmbTipoDireccion').setValue(registro[0].idTipoD);
		Ext.getCmp(PF + 'cmbEstado').setValue(registro[0].estado);
		Ext.getCmp(PF + 'cmbPais').setValue(registro[0].pais);
	};
	
/////////////LLena datos proveedor///////////////////
	NS.llenaDatosP = function(){
		if (NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){			
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
//			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noPersona');
		}
		if (NS.tipoPersona != 'I' && NS.tipoPersona != 'E' && NS.tipoPersona != 'B' && NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
//			if (Ext.getCmp(PF + 'txtEmpresa').getValue() !== ''){
//				NS.noPersona = Ext.getCmp(PF + 'txtEmpresaF').getValue();
//			}
		}
		if (NS.tipoPersona == 'P' ){
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
			
			NS.storeLlenaGridMedios.baseParams.noPersona = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noPersona')+'';
			NS.storeLlenaGridMedios.load();
			
			
		}
		if(NS.tipoPersona == 'B' ){
			//CambioEdgar
			NS.noPersona = Ext.getCmp(PF + 'txtEmpresa').getValue();
			
		}	
		ConsultaPersonasAction.obtieneDatos(parseInt(NS.noEmpresa), NS.equivalePersona + "", NS.tipoPersona, function(resultado, e){
		
			
			
			if (resultado !== null && resultado !== '' && resultado !== undefined)
			{
				if(NS.tipoPersona == 'B' || NS.tipoPersona == 'K')
					Ext.getCmp(PF + 'txtEmpresaGral').setValue(apps.SET.NOM_EMPRESA);
				else
					Ext.getCmp(PF + 'txtEmpresaPM').setValue(resultado[0].nomEmpresa);
				
				Ext.getCmp(PF + 'txtFechaIngreso').setValue(resultado[0].fechaIngreso);
				/*Ext.getCmp(PF + 'txtFechaIngreso').getValue('11-11-2011');
				Ext.getCmp(PF + 'txtFechaIngreso').setValue(apps.SET.FEC_HOY);*/			
				//Llena Datos de Institucion Bancaria
				if (NS.tipoPersona == 'S' || NS.tipoPersona == 'B'){
					if (NS.fisicaMoral == 'F'){						
						NS.llenaCamposFisica(resultado);
						
						//Checkbox asociacion
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
						
						//checkbox proveedor
						if (resultado[0].bProveedor == 'S')
							Ext.getCmp(PF + 'chkProveedor').setValue(true);
						else
							Ext.getCmp(PF + 'chkProveedor').setValue(false);
						
						//Checkbox contrato de inversion
						if (resultado[0].bContratoInversion == 'S')
							Ext.getCmp(PF + 'chkContratoInversion').setValue(true);
						else
							Ext.getCmp(PF + 'chkContratoInversion').setValue(false);
						
					}
					else{			
						NS.llenaCamposMoral(resultado);
					}		
				}//Termina el if de instituciones bancarias
				else if (NS.tipoPersona == 'P' || NS.tipoPersona == 'K'){
					ConsultaPersonasAction.proveedorBasico(parseInt(resultado[0].noEmpresa), parseInt(resultado[0].noPersona), function (resProvBasico, e){
						if (resProvBasico !== null && resProvBasico !== '' && resProvBasico !== undefined && resProvBasico == 'S')									
							Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setValue(true);
						else
							Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setValue(false);
					});
					
					if (NS.fisicaMoral == 'F'){	//Proveedor o casa de cambio Fisicas					
						NS.llenaCamposFisica(resultado);
						
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
					}
					else{ //Proveedor o casa de cambio morales					
					
						NS.llenaCamposPM(resultado);						
					}
				}
				else if (NS.tipoPersona == 'C'){
					if (NS.fisicaMoral == 'F'){
						NS.llenaCamposFisica(resultado);
						
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
					}
					else{
						NS.llenaCamposMoral(resultado);	
					}
				}else if (NS.tipoPersona == 'E' || NS.tipoPersona == 'I'){
					ConsultaPersonasAction.obtieneCaja(parseInt(resultado[0].noEmpresa), function(resCaja, e){
						
						if (resCaja !== null && resCaja !== '' && resCaja !== undefined){
							Ext.getCmp(PF + 'txtIdCajaE').setValue(resCaja[0].idCaja);
							Ext.getCmp(PF + 'cmbCajaE').setValue(resCaja[0].descCaja);
						}
						else{
							Ext.getCmp(PF + 'txtIdCajaE').setValue('');
							Ext.getCmp(PF + 'cmbCajaE').reset();
						}							
					});	
					
					if (NS.fisicaMoral == 'F')
						Ext.Msg.alert('SET', 'Persona Juridica Erronea');
					else{				
						NS.llenaCamposEmpresa(resultado);
					}							
				}
				else if (NS.tipoPersona == 'F' || NS.tipoPersona == 'R'){
					NS.llenaCamposFisica(resultado);
					
					if (resultado[0].bAsociacion == 'S')
						Ext.getCmp(PF + 'chkAsociacion').setValue(true);
					else
						Ext.getCmp(PF + 'chkAsociacion').setValue(false);
				}
				
				if (NS.tipoPersona == 'E' || NS.tipoPersona == 'I') {// || NS.tipoPersona == 'B'){
					ConsultaPersonasAction.obtenerDatosEmpresa(parseInt(Ext.getCmp(PF + 'txtNoPersonaGral').getValue()), function (resEmpresa, e){
						if (resEmpresa !== '' && resEmpresa !== null && resEmpresa !== undefined){
							//Panel radio button exporta
							if (resEmpresa[0].exporta == 'S'){
								Ext.getCmp(PF + 'optExporta').setValue(0);
								NS.exporta = 'S';
							}
							else{
								Ext.getCmp(PF + 'optExporta').setValue(1);
								NS.exporta = 'N';
							}
							
							//Panel RadioButton concentradora
							if (resEmpresa[0].concentradora == 'S'){
								Ext.getCmp(PF + 'optConcentradora').setValue(0);
								NS.concentradora = 'S'							
							}							
							else{
								Ext.getCmp(PF + 'optConcentradora').setValue(1);
								NS.concentradora = 'N';
							}
								
							//Checkbox division
							if (resEmpresa[0].division == 'S'){
								//alert(" si trae division");
								Ext.getCmp(PF + 'chkDivision').setValue(true);
							}
							else{
								//alert(" no trae division");
								Ext.getCmp(PF + 'chkDivision').setValue(false);
							}
							
							//Checkbox pagos cruzados
							if (resEmpresa[0].pagoCruzado == 'S'){
								//alert(" si es pago cruzado");
								Ext.getCmp(PF + 'chkPagosCruzados').setValue(true);
							}
							else{
								//alert(" no es pago cruzado");
								Ext.getCmp(PF + 'chkPagosCruzados').setValue(false);
							}
							
							Ext.getCmp(PF + 'txtIdGrupo').setValue(resEmpresa[0].idGrupo);
							Ext.getCmp(PF + 'cmbGrupo').setValue(resEmpresa[0].descGrupo);
							
						}
					});
				}
				NS.noEmpresa = resultado[0].noEmpresa;
				NS.noPersona = resultado[0].noPersona;
				
				Ext.getCmp(PF + 'cmbPagoReferenciado').setValue(resultado[0].referencia);
				
			}
			else
				Ext.Msg.alert('SET', 'No existe información para esta persona');
		});
		
	};
	
	//////////////////////////////////////////////////
	NS.llenaDatosK = function(){
		if (NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){			
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
		}
		if (NS.tipoPersona != 'I' && NS.tipoPersona != 'E' && NS.tipoPersona != 'B' && NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
//			if (Ext.getCmp(PF + 'txtEmpresa').getValue() !== ''){
//				NS.noPersona = Ext.getCmp(PF + 'txtEmpresaF').getValue();
//			}
		}
		if (NS.tipoPersona == 'P' ){
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
//			NS.noPersona = Ext.getCmp(PF + 'txtEmpresaPM').getValue();
			
		}
		if(NS.tipoPersona == 'B' ){
			//CambioEdgar
			NS.noPersona = Ext.getCmp(PF + 'txtEmpresa').getValue();
			
		}	
		
		if(NS.tipoPersona == 'K'){
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
			
		}
		
		ConsultaPersonasAction.obtieneDatos(parseInt(NS.noEmpresa), NS.equivalePersona + "", NS.tipoPersona, function(resultado, e){
			if (resultado !== null && resultado !== '' && resultado !== undefined)
			{
				if(NS.tipoPersona == 'B' || NS.tipoPersona == 'K'){
				
					Ext.getCmp(PF + 'txtEmpresaK').setValue(apps.SET.NOM_EMPRESA);
				}else{
					
					Ext.getCmp(PF + 'txtEmpresaPM').setValue(resultado[0].nomEmpresa);
				}
				//Ext.getCmp(PF + 'txtFechaIngresoK').setValue(resultado[0].fechaIngreso);
				/*Ext.getCmp(PF + 'txtFechaIngreso').getValue('11-11-2011');
				Ext.getCmp(PF + 'txtFechaIngreso').setValue(apps.SET.FEC_HOY);*/			
				//Llena Datos de Institucion Bancaria
				if (NS.tipoPersona == 'S' || NS.tipoPersona == 'B'){
					if (NS.fisicaMoral == 'F'){						
						NS.llenaCamposFisica(resultado);
						
						//Checkbox asociacion
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
						
						//checkbox proveedor
						if (resultado[0].bProveedor == 'S')
							Ext.getCmp(PF + 'chkProveedor').setValue(true);
						else
							Ext.getCmp(PF + 'chkProveedor').setValue(false);
						
						//Checkbox contrato de inversion
						if (resultado[0].bContratoInversion == 'S')
							Ext.getCmp(PF + 'chkContratoInversion').setValue(true);
						else
							Ext.getCmp(PF + 'chkContratoInversion').setValue(false);
						
					}
					else{			
						NS.llenaCamposMoral(resultado);
					}		
				}//Termina el if de instituciones bancarias
				else if (NS.tipoPersona == 'P' || NS.tipoPersona == 'K'){

					NS.llenaCamposK(resultado);

				}
				else if (NS.tipoPersona == 'C'){
					if (NS.fisicaMoral == 'F'){
						NS.llenaCamposFisica(resultado);
						
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
					}
					else{
						NS.llenaCamposMoral(resultado);	
					}
				}else if (NS.tipoPersona == 'E' || NS.tipoPersona == 'I'){
					ConsultaPersonasAction.obtieneCaja(parseInt(resultado[0].noEmpresa), function(resCaja, e){
						
						if (resCaja !== null && resCaja !== '' && resCaja !== undefined){
							Ext.getCmp(PF + 'txtIdCajaE').setValue(resCaja[0].idCaja);
							Ext.getCmp(PF + 'cmbCajaE').setValue(resCaja[0].descCaja);
						}
						else{
							Ext.getCmp(PF + 'txtIdCajaE').setValue('');
							Ext.getCmp(PF + 'cmbCajaE').reset();
						}							
					});	
					
					if (NS.fisicaMoral == 'F')
						Ext.Msg.alert('SET', 'Persona Juridica Erronea');
					else{				
						NS.llenaCamposEmpresa(resultado);
					}							
				}
				else if (NS.tipoPersona == 'F' || NS.tipoPersona == 'R'){
					NS.llenaCamposFisica(resultado);
					
					if (resultado[0].bAsociacion == 'S')
						Ext.getCmp(PF + 'chkAsociacion').setValue(true);
					else
						Ext.getCmp(PF + 'chkAsociacion').setValue(false);
				}
				
				if (NS.tipoPersona == 'E' || NS.tipoPersona == 'I') {// || NS.tipoPersona == 'B'){
					ConsultaPersonasAction.obtenerDatosEmpresa(parseInt(Ext.getCmp(PF + 'txtNoPersonaGral').getValue()), function (resEmpresa, e){
						if (resEmpresa !== '' && resEmpresa !== null && resEmpresa !== undefined){
							//Panel radio button exporta
							if (resEmpresa[0].exporta == 'S'){
								Ext.getCmp(PF + 'optExporta').setValue(0);
								NS.exporta = 'S';
							}
							else{
								Ext.getCmp(PF + 'optExporta').setValue(1);
								NS.exporta = 'N';
							}
							
							//Panel RadioButton concentradora
							if (resEmpresa[0].concentradora == 'S'){
								Ext.getCmp(PF + 'optConcentradora').setValue(0);
								NS.concentradora = 'S'							
							}							
							else{
								Ext.getCmp(PF + 'optConcentradora').setValue(1);
								NS.concentradora = 'N';
							}
								
							//Checkbox division
							if (resEmpresa[0].division == 'S'){
								//alert(" si trae division");
								Ext.getCmp(PF + 'chkDivision').setValue(true);
							}
							else{
								//alert(" no trae division");
								Ext.getCmp(PF + 'chkDivision').setValue(false);
							}
							
							//Checkbox pagos cruzados
							if (resEmpresa[0].pagoCruzado == 'S'){
								//alert(" si es pago cruzado");
								Ext.getCmp(PF + 'chkPagosCruzados').setValue(true);
							}
							else{
								//alert(" no es pago cruzado");
								Ext.getCmp(PF + 'chkPagosCruzados').setValue(false);
							}
							
							Ext.getCmp(PF + 'txtIdGrupo').setValue(resEmpresa[0].idGrupo);
							Ext.getCmp(PF + 'cmbGrupo').setValue(resEmpresa[0].descGrupo);
							
						}
					});
				}
				NS.noEmpresa = resultado[0].noEmpresa;
				NS.noPersona = resultado[0].noPersona;
				
				Ext.getCmp(PF + 'cmbPagoReferenciado').setValue(resultado[0].referencia);
				
			}
			else
				Ext.Msg.alert('SET', 'No existe información para esta persona');
		});
		
	};
	
	NS.llenaDatosDI = function(){
		
		if (NS.tipoPersona=='P') {
			NS.equivalePersona=NS.gridConsulta.getSelectionModel().getSelections()[0].get('noPersona');
		}
		ConsultaPersonasAction.obtieneDireccion(parseInt(NS.noEmpresa), NS.equivalePersona + "", NS.tipoPersona, function(resultado, e){
			if (resultado != null && resultado != '' && resultado != undefined)
			{
				
				NS.DCD();
				NS.llenaCamposDIR(resultado);
				NS.panelBotonesDI.setVisible(false);
				
				NS.panelBotonesDIM.setVisible(true);
				
			}else{
				//NS.HabilitarCamposDI();
			    NS.DCD();
				NS.panelBotonesDIM.setVisible(false);
				NS.panelBotonesDI.setVisible(true);
				//Ext.Msg.alert('SET', 'No existe información para esta persona');
			}
		});
		
	};
	
	/////////////LLena datos empresa///////////////////
	NS.llenaDatosE = function(){
		if (NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){			
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
		}
		if (NS.tipoPersona != 'I' && NS.tipoPersona != 'E' && NS.tipoPersona != 'B' && NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){
			
			if (Ext.getCmp(PF + 'txtEmpresa').getValue() !== ''){
				NS.noPersona = Ext.getCmp(PF + 'txtEmpresaF').getValue();
			
			}
		}
		if (NS.tipoPersona == 'P' && Ext.getCmp(PF + 'txtNoEmpresa').getValue() != ''){
			NS.noPersona = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
			
		}
		if(NS.tipoPersona == 'B' ){
			//CambioEdgar
			NS.noPersona = Ext.getCmp(PF + 'txtEmpresa').getValue();
			
		}	
		ConsultaPersonasAction.obtieneDatos(parseInt(NS.noEmpresa), NS.equivalePersona + "", NS.tipoPersona, function(resultado, e){
			
			
			if (resultado !== null && resultado !== '' && resultado !== undefined)
			{
				if(NS.tipoPersona == 'B' || NS.tipoPersona == 'K')
					Ext.getCmp(PF + 'txtEmpresaGral').setValue(apps.SET.NOM_EMPRESA);
				else
					Ext.getCmp(PF + 'txtEmpresaGral').setValue(resultado[0].nomEmpresa);
				
				Ext.getCmp(PF + 'txtFechaIngresoE').setValue(resultado[0].fechaIngreso);
				/*Ext.getCmp(PF + 'txtFechaIngreso').getValue('11-11-2011');
				Ext.getCmp(PF + 'txtFechaIngreso').setValue(apps.SET.FEC_HOY);*/			
				//Llena Datos de Institucion Bancaria
				if (NS.tipoPersona == 'S' || NS.tipoPersona == 'B'){
					if (NS.fisicaMoral == 'F'){						
						NS.llenaCamposFisica(resultado);
						
						//Checkbox asociacion
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
						
						//checkbox proveedor
						if (resultado[0].bProveedor == 'S')
							Ext.getCmp(PF + 'chkProveedor').setValue(true);
						else
							Ext.getCmp(PF + 'chkProveedor').setValue(false);
						
						//Checkbox contrato de inversion
						if (resultado[0].bContratoInversion == 'S')
							Ext.getCmp(PF + 'chkContratoInversion').setValue(true);
						else
							Ext.getCmp(PF + 'chkContratoInversion').setValue(false);
						
					}
					else{			
						NS.llenaCamposMoral(resultado);
					}		
				}//Termina el if de instituciones bancarias
				else if (NS.tipoPersona == 'P' || NS.tipoPersona == 'K'){
					ConsultaPersonasAction.proveedorBasico(parseInt(resultado[0].noEmpresa), parseInt(resultado[0].noPersona), function (resProvBasico, e){
						if (resProvBasico !== null && resProvBasico !== '' && resProvBasico !== undefined && resProvBasico == 'S')									
							Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setValue(true);
						else
							Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setValue(false);
					});
					
					if (NS.fisicaMoral == 'F'){	//Proveedor o casa de cambio Fisicas					
						NS.llenaCamposFisica(resultado);
						
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
					}
					else{ //Proveedor o casa de cambio morales					
					
						NS.llenaCamposMoral(resultado);						
					}
				}
				else if (NS.tipoPersona == 'C'){
					if (NS.fisicaMoral == 'F'){
						NS.llenaCamposFisica(resultado);
						
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
					}
					else{
						NS.llenaCamposMoral(resultado);	
					}
				}else if (NS.tipoPersona == 'E' || NS.tipoPersona == 'I'){
					ConsultaPersonasAction.obtieneCaja(parseInt(resultado[0].noEmpresa), function(resCaja, e){
						
						if (resCaja !== null && resCaja !== '' && resCaja !== undefined){
							Ext.getCmp(PF + 'txtIdCajaE').setValue(resCaja[0].idCaja);
							Ext.getCmp(PF + 'cmbCajaE').setValue(resCaja[0].descCaja);
						}
						else{
							Ext.getCmp(PF + 'txtIdCajaE').setValue('');
							Ext.getCmp(PF + 'cmbCajaE').reset();
						}							
					});	
					
					
					
					
					if (NS.fisicaMoral == 'F')
						Ext.Msg.alert('SET', 'Persona Juridica Erronea');
					else{				
						NS.llenaCamposEmpresa(resultado);
					}							
				}
				else if (NS.tipoPersona == 'F' || NS.tipoPersona == 'R'){
					NS.llenaCamposFisica(resultado);
					
					if (resultado[0].bAsociacion == 'S')
						Ext.getCmp(PF + 'chkAsociacion').setValue(true);
					else
						Ext.getCmp(PF + 'chkAsociacion').setValue(false);
				}
				
				if (NS.tipoPersona == 'E' || NS.tipoPersona == 'I') {// || NS.tipoPersona == 'B'){
					ConsultaPersonasAction.obtenerDatosEmpresa(parseInt(Ext.getCmp(PF + 'txtNoPersonaGral').getValue()), function (resEmpresa, e){
						
						if (resEmpresa !== '' && resEmpresa !== null && resEmpresa !== undefined){
							
							//Panel radio button exporta
							if (resEmpresa[0].exporta == 'S'){
								Ext.getCmp(PF + 'optExporta').setValue(0);
								NS.exporta = 'S';
							}
							else{
								Ext.getCmp(PF + 'optExporta').setValue(1);
								NS.exporta = 'N';
							}
							
							//Panel RadioButton concentradora
							if (resEmpresa[0].concentradora == 'S'){
								Ext.getCmp(PF + 'optConcentradora').setValue(0);
								NS.concentradora = 'S'							
							}							
							else{
								Ext.getCmp(PF + 'optConcentradora').setValue(1);
								NS.concentradora = 'N';
							}
								
							//Checkbox division
							if (resEmpresa[0].division == 'S'){
								//alert(" si trae division");
								Ext.getCmp(PF + 'chkDivision').setValue(true);
							}
							else{
								//alert(" no trae division");
								Ext.getCmp(PF + 'chkDivision').setValue(false);
							}
							
							//Checkbox pagos cruzados
							if (resEmpresa[0].pagoCruzado == 'S'){
								//alert(" si es pago cruzado");
								Ext.getCmp(PF + 'chkPagosCruzados').setValue(true);
							}
							else{
								//alert(" no es pago cruzado");
								Ext.getCmp(PF + 'chkPagosCruzados').setValue(false);
							}
							
							Ext.getCmp(PF + 'txtIdGrupo').setValue(resEmpresa[0].idGrupo);
							Ext.getCmp(PF + 'cmbGrupo').setValue(resEmpresa[0].descGrupo);
							
						}
					});
				}
				NS.noEmpresa = resultado[0].noEmpresa;
				NS.noPersona = resultado[0].noPersona;
				
				Ext.getCmp(PF + 'cmbPagoReferenciado').setValue(resultado[0].referencia);
				
			}
			else
				Ext.Msg.alert('SET', 'No existe información para esta persona');
		});
		
	};
	
	//////////////////////////////////////////////////
	
	///////////////llenaDatosIB////////////////////
	NS.llenaDatosIB = function() {
		if (NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){			
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noEmpresa = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
		}
		if (NS.tipoPersona != 'I' && NS.tipoPersona != 'E' && NS.tipoPersona != 'B' && NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){
			
			if (Ext.getCmp(PF + 'txtEmpresa').getValue() !== ''){
				NS.noPersona = Ext.getCmp(PF + 'txtEmpresaF').getValue();
			
			}
		}
		
		if(NS.tipoPersona == 'B' ){
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noPersona = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noPersona');
			
		}	
		ConsultaPersonasAction.obtieneDatos(parseInt(NS.noPersona), NS.equivalePersona + "", NS.tipoPersona, function(resultado, e){
			
			
			if (resultado !== null && resultado !== '' && resultado !== undefined)
			{
				if(NS.tipoPersona == 'B' || NS.tipoPersona == 'K')
					Ext.getCmp(PF + 'txtEmpresaIB').setValue(apps.SET.NOM_EMPRESA);
				else
					Ext.getCmp(PF + 'txtEmpresaGral').setValue(resultado[0].nomEmpresa);
				
				//Ext.getCmp(PF + 'txtFechaIngresoIB').setValue(resultado[0].fechaIngreso);
				/*Ext.getCmp(PF + 'txtFechaIngreso').getValue('11-11-2011');
				Ext.getCmp(PF + 'txtFechaIngreso').setValue(apps.SET.FEC_HOY);*/			
				//Llena Datos de Institucion Bancaria
				if (NS.tipoPersona == 'S' || NS.tipoPersona == 'B'){
					
				NS.llenaCamposIB(resultado);
				
			}
			else
				Ext.Msg.alert('SET', 'No existe información para esta persona');
		}
			});
		
	};
	
	NS.llenaDatosF = function() {
		if(NS.tipoPersona == 'F' )
			NS.equivalePersona= NS.gridConsulta.getSelectionModel().getSelections()[0].get('equivalePersona');
			NS.noPersona = NS.gridConsulta.getSelectionModel().getSelections()[0].get('noPersona');
		
		ConsultaPersonasAction.obtienePersonaFisica(parseInt(NS.noPersona), NS.equivalePersona + "", NS.tipoPersona, function(resultado, e){
		
			if (resultado !== null && resultado !== '' && resultado !== undefined)
			{
				
				NS.llenaCamposFisica(resultado);
			}
			else
				Ext.Msg.alert('SET', 'No existe información para esta persona');
		});
	};
	
	
	/////////////////////////////////////////////
	
	NS.llenaDatos = function() {
		if (NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){			
			NS.equivalePersona = NS.noPersona;
		}
		if (NS.tipoPersona != 'I' && NS.tipoPersona != 'E' && NS.tipoPersona != 'B' && NS.tipoPersona != 'P' && NS.tipoPersona != 'K'){
			///Pruebaedgar
			if (Ext.getCmp(PF + 'txtEmpresa').getValue() !== '')
				NS.noPersona = Ext.getCmp(PF + 'txtEmpresaF').getValue();
		}
		//if (NS.tipoPersona == 'P' && Ext.getCmp(PF + 'txtNoEmpresa').getValue() != '')
			//NS.noPersona = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
		
		if(NS.tipoPersona == 'B' )
			//CambioEdgar
			NS.noPersona = Ext.getCmp(PF + 'txtEmpresa').getValue();
		
		ConsultaPersonasAction.obtieneDatos(parseInt(NS.noPersona), NS.equivalePersona + "", NS.tipoPersona, function(resultado, e){
		
			if (resultado !== null && resultado !== '' && resultado !== undefined)
			{
				if(NS.tipoPersona == 'B' || NS.tipoPersona == 'K')
					Ext.getCmp(PF + 'txtEmpresaGral').setValue(apps.SET.NOM_EMPRESA);
				else
					Ext.getCmp(PF + 'txtEmpresaGral').setValue(resultado[0].nomEmpresa);
				
				Ext.getCmp(PF + 'txtFechaIngreso').setValue(resultado[0].fechaIngreso);
				/*Ext.getCmp(PF + 'txtFechaIngreso').getValue('11-11-2011');
				Ext.getCmp(PF + 'txtFechaIngreso').setValue(apps.SET.FEC_HOY);*/			
				//Llena Datos de Institucion Bancaria
				if (NS.tipoPersona == 'S' || NS.tipoPersona == 'B'){
					if (NS.fisicaMoral == 'F'){						
						NS.llenaCamposFisica(resultado);
						
						//Checkbox asociacion
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
						
						//checkbox proveedor
						if (resultado[0].bProveedor == 'S')
							Ext.getCmp(PF + 'chkProveedor').setValue(true);
						else
							Ext.getCmp(PF + 'chkProveedor').setValue(false);
						
						//Checkbox contrato de inversion
						if (resultado[0].bContratoInversion == 'S')
							Ext.getCmp(PF + 'chkContratoInversion').setValue(true);
						else
							Ext.getCmp(PF + 'chkContratoInversion').setValue(false);
						
					}
					else{			
						NS.llenaCamposMoral(resultado);
					}		
				}//Termina el if de instituciones bancarias
				else if (NS.tipoPersona == 'P' || NS.tipoPersona == 'K'){
					ConsultaPersonasAction.proveedorBasico(parseInt(resultado[0].noEmpresa), parseInt(resultado[0].noPersona), function (resProvBasico, e){
						if (resProvBasico !== null && resProvBasico !== '' && resProvBasico !== undefined && resProvBasico == 'S')									
							Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setValue(true);
						else
							Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setValue(false);
					});
					
					if (NS.fisicaMoral == 'F'){	//Proveedor o casa de cambio Fisicas					
						NS.llenaCamposFisica(resultado);
						
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
					}
					else{ //Proveedor o casa de cambio morales					
					
						NS.llenaCamposMoral(resultado);						
					}
				}
				else if (NS.tipoPersona == 'C'){
					if (NS.fisicaMoral == 'F'){
						NS.llenaCamposFisica(resultado);
						
						if (resultado[0].bAsociacion == 'S')
							Ext.getCmp(PF + 'chkAsociacion').setValue(true);
						else
							Ext.getCmp(PF + 'chkAsociacion').setValue(false);
					}
					else{
						NS.llenaCamposMoral(resultado);	
					}
				}else if (NS.tipoPersona == 'E' || NS.tipoPersona == 'I'){
					ConsultaPersonasAction.obtieneCaja(parseInt(resultado[0].noEmpresa), function(resCaja, e){
						if (resCaja !== null && resCaja !== '' && resCaja !== undefined){
							Ext.getCmp(PF + 'txtIdCaja').setValue(resCaja[0].idCaja);
							Ext.getCmp(PF + 'cmbCaja').setValue(resCaja[0].descCaja);
						}
						else{
							Ext.getCmp(PF + 'txtIdCaja').setValue('');
							Ext.getCmp(PF + 'cmbCaja').reset();
						}							
					});	
					
					if (NS.fisicaMoral == 'F')
						Ext.Msg.alert('SET', 'Persona Juridica Erronea');
					else{								
						NS.llenaCamposMoral(resultado);
					}							
				}
				else if (NS.tipoPersona == 'F' || NS.tipoPersona == 'R'){
					NS.llenaCamposFisica(resultado);
					
					if (resultado[0].bAsociacion == 'S')
						Ext.getCmp(PF + 'chkAsociacion').setValue(true);
					else
						Ext.getCmp(PF + 'chkAsociacion').setValue(false);
				}
				
				if (NS.tipoPersona == 'E' || NS.tipoPersona == 'I') {// || NS.tipoPersona == 'B'){
					ConsultaPersonasAction.obtenerDatosEmpresa(parseInt(Ext.getCmp(PF + 'NS.txtEmpresaE').getValue()), function (resEmpresa, e){
						if (resEmpresa !== '' && resEmpresa !== null && resEmpresa !== undefined){
							//Panel radio button exporta
							if (resEmpresa[0].exporta == 'S'){
								Ext.getCmp(PF + 'optExporta').setValue(0);
								NS.exporta = 'S';
							}
							else{
								Ext.getCmp(PF + 'optExporta').setValue(1);
								NS.exporta = 'N';
							}
							
							//Panel RadioButton concentradora
							if (resEmpresa[0].concentradora == 'S'){
								Ext.getCmp(PF + 'optConcentradora').setValue(0);
								NS.concentradora = 'S'							
							}							
							else{
								Ext.getCmp(PF + 'optConcentradora').setValue(1);
								NS.concentradora = 'N';
							}
								
							//Checkbox division
							if (resEmpresa[0].division == 'S'){
								//alert(" si trae division");
								Ext.getCmp(PF + 'chkDivision').setValue(true);
							}
							else{
								//alert(" no trae division");
								Ext.getCmp(PF + 'chkDivision').setValue(false);
							}
							
							//Checkbox pagos cruzados
							if (resEmpresa[0].pagoCruzado == 'S'){
								//alert(" si es pago cruzado");
								Ext.getCmp(PF + 'chkPagosCruzados').setValue(true);
							}
							else{
								//alert(" no es pago cruzado");
								Ext.getCmp(PF + 'chkPagosCruzados').setValue(false);
							}
							
							Ext.getCmp(PF + 'txtIdGrupo').setValue(resEmpresa[0].idGrupo);
							Ext.getCmp(PF + 'cmbGrupo').setValue(resEmpresa[0].descGrupo);
							
						}
					});
				}
				NS.noEmpresa = resultado[0].noEmpresa;
				NS.noPersona = resultado[0].noPersona;
				
				Ext.getCmp(PF + 'cmbPagoReferenciado').setValue(resultado[0].referencia);
				
			}
			else
				Ext.Msg.alert('SET', 'No existe información para esta persona');
		});
	};
	
	NS.folioF= function(){
		ConsultaPersonasAction.obtieneFolio("no_persona", function(resultado, e){
			if (resultado != '' && resultado != null && resultado != undefined){
				Ext.getCmp(PF + 'txtNoPersonaF').setValue(resultado);
				Ext.getCmp(PF + 'txtEquivalePersona').setValue(resultado);
//				ConsultaPersonasAction.actualizaFolio("no_persona", function(resultado, e){
//				});
			}
	});


};
	
	NS.folioB= function(){
		
				ConsultaPersonasAction.obtieneFolio("no_persona", function(resultado, e){
					if (resultado != '' && resultado != null && resultado != undefined){
						Ext.getCmp(PF + 'txtNoPersonaIB').setValue(resultado);
						Ext.getCmp(PF + 'txtEquivalePersona').setValue(resultado);
//						ConsultaPersonasAction.actualizaFolio("no_persona", function(resultado, e){
//						});
					}
			});
	
		
	};
	
	NS.folioK= function(){
		
		ConsultaPersonasAction.obtieneFolio("no_persona", function(resultado, e){
			if (resultado != '' && resultado != null && resultado != undefined){
				Ext.getCmp(PF + 'txtNoPersonaK').setValue(resultado);
				Ext.getCmp(PF + 'txtEquivalePersona').setValue(resultado);
//				ConsultaPersonasAction.actualizaFolio("no_persona", function(resultado, e){
//				});
			}
	});


};
	
	//Muestra los campos en pantalla dependiendo el tipo de persona que seleccionen
	NS.muestraCampos = function(){
		//NS.ocultaTodosCampos();
		NS.limpiarCamposNuevos();
		NS.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		
		Ext.getCmp(PF + 'btnImprimir').setVisible(false);
		Ext.getCmp(PF + 'btnLimpiar').setVisible(false);
		NS.panelCampos.setVisible(true);
		//NS.panelBotones.setVisible(true);
		//NS.panelBotonesPr.setVisible(true);
		NS.panelBusqueda.setVisible(false);
		NS.panelGrid.setVisible(false);
		
		if (NS.modificar){
			NS.noEmpresa = registroSeleccionado[0].get('noEmpresa');
			NS.noPersona = registroSeleccionado[0].get('noPersona');
			NS.equivalePersona = registroSeleccionado[0].get('equivalePersona');
			NS.estatus = registroSeleccionado[0].get('idEstatus');
		}
		else{
			NS.estatus = "A";
			
			if(NS.tipoPersona != 'E') {
				ConsultaPersonasAction.obtieneFolio("no_persona", function(resultado, e){
						if (resultado != '' && resultado != null && resultado != undefined){
							Ext.getCmp(PF + 'txtNoPersonaGral').setValue(resultado);
							Ext.getCmp(PF + 'txtEquivalePersona').setValue(resultado);
							ConsultaPersonasAction.actualizaFolio("no_persona", function(resultado, e){
							});
						}
				});
			}
		}
		
		NS.lblEmpresaGral.setVisible(true);
		Ext.getCmp(PF + 'txtEmpresaGral').setVisible(true);
		Ext.getCmp(PF + 'txtEmpresaGral').setDisabled(true);
		
		Ext.getCmp(PF + 'txtEmpresaGral').setValue(apps.SET.NOM_EMPRESA);
		
		if(NS.tipoPersona == 'S' || NS.tipoPersona == 'B') { //Instituciones Bancarias y 
			//NS.lblGrupoFlujo.setVisible(true);
			//NS.lblGrupoFlujo.setDisabled(true);
			//Ext.getCmp(PF + 'txtIdGrupo').setVisible(true);
			//Ext.getCmp(PF + 'cmbGrupo').setVisible(true);
			//Ext.getCmp(PF + 'txtIdGrupo').setDisabled(true);
			//Ext.getCmp(PF + 'cmbGrupo').setDisabled(true);
			//NS.panelBotonesIB.setVisible(true);	
			NS.panelCamposIB.show();
			
			if (NS.fisicaMoral == 'F') {//Instituciones Bancarias fisicas
							

				NS.lblFormaPagoProv.setVisible(false);			
				Ext.getCmp(PF + 'txtIdFormaPagoProv').setVisible(false);
				Ext.getCmp(PF + 'cmbFormaPagoProv').setVisible(false);
				Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setVisible(false);
				NS.panelPAC.setVisible(true);
				NS.panelConcentradora.setVisible(true);
				NS.lblCambioPersona.setVisible(false);			
				Ext.getCmp(PF + 'txtIdCambioPersona').setVisible(false);
				Ext.getCmp(PF + 'cmbCambioPersona').setVisible(false);
				NS.lblAccionesA.setVisible(true);
				NS.lblAccionesB.setVisible(true);
				NS.lblAccionesC.setVisible(true);
				Ext.getCmp(PF + 'txtAccionesA').setVisible(true);
				Ext.getCmp(PF + 'txtAccionesB').setVisible(true);
				Ext.getCmp(PF + 'txtAccionesC').setVisible(true);
			}
			else{ //Instituciones Bancarias Morales				
				NS.lblEmpresaGral.setDisabled(true);
				NS.lblNoPersonaGral.setVisible(true);
				NS.lblNoPersonaGral.setDisabled(true);
				Ext.getCmp(PF + 'txtNoPersonaGral').setVisible(true);		
				NS.lblRazonSocialGral.setVisible(true);				
				Ext.getCmp(PF + 'txtRazonSocialGral').setVisible(true);				
				NS.lblNombreCorto.setVisible(true);
				Ext.getCmp(PF + 'txtNombreCorto').setVisible(true);
				NS.lblRFCGral.setVisible(true);
				Ext.getCmp(PF + 'txtRFCGral').setVisible(true);
				NS.lblFechaIngreso.setVisible(true);
				Ext.getCmp(PF + 'txtFechaIngreso').setVisible(true);
				//NS.lblActividadEconomica.setVisible(true);
				//Ext.getCmp(PF + 'txtIdActividadEconomica').setVisible(true);
				//Ext.getCmp(PF + 'cmbActividadEconomica').setVisible(true);
				NS.storeActividadEconomica.load();
				//NS.lblActividadGenerica.setVisible(true);
				Ext.getCmp(PF + 'txtIdActividadGenerica').setVisible(true);
				Ext.getCmp(PF + 'cmbActividadGenerica').setVisible(true);
				NS.storeActividadGenerica.load();
				NS.lblGiro.setVisible(true);
				Ext.getCmp(PF + 'txtIdGiro').setVisible(true);
				Ext.getCmp(PF + 'cmbGiro').setVisible(true);
				NS.storeComboGiro.load();
				NS.lblRiesgo.setVisible(true);
				NS.lblRiesgo.setDisabled(true);
				Ext.getCmp(PF + 'txtIdRiesgo').setVisible(true);
				Ext.getCmp(PF + 'txtIdRiesgo').setDisabled(true);
				Ext.getCmp(PF + 'cmbRiesgo').setVisible(true);
				Ext.getCmp(PF + 'cmbRiesgo').setDisabled(true);
				NS.storeRiesgo.load();
				NS.lblTamano.setVisible(false);
				Ext.getCmp(PF + 'txtIdTamano').setVisible(false);
				Ext.getCmp(PF + 'cmbTamano').setVisible(false);
				NS.storeTamano.load();
			}		
		}
		else if (NS.tipoPersona == 'P' || NS.tipoPersona == 'K'){ //Proveedor y Casa de Cambio
			//NS.lblPagoReferenciado.setVisible(true);
			
			Ext.getCmp(PF + 'cmbPagoReferenciado').setVisible(true);
			
			if (NS.tipoPersona == 'P'){
				

				NS.lblPagoReferenciado.setDisabled(false);
				Ext.getCmp(PF + 'cmbPagoReferenciado').setDisabled(false);
			}
			else{
				NS.lblPagoReferenciado.setDisabled(true);
				Ext.getCmp(PF + 'cmbPagoReferenciado').setDisabled(true);
			}
			

			NS.panelGpoRubro.setVisible(true);
			NS.panelTef.setVisible(false);
			NS.panelTef.setVisible(false);
			NS.panelExporta.setVisible(false);
			NS.lblFormaPagoProv.setVisible(true);
			Ext.getCmp(PF + 'txtIdFormaPagoProv').setVisible(true);
			Ext.getCmp(PF + 'cmbFormaPagoProv').setVisible(true);
			if (NS.noPersona == '')
				NS.noPersona = 0;
			
			if (NS.noEmpresa == '')
				NS.noEmpresa = 0;
			
			NS.storeFormaPagoProv.baseParams.noPersona = parseInt(NS.noPersona);
			NS.storeFormaPagoProv.baseParams.noEmpresa = parseInt(NS.noEmpresa);
			NS.storeFormaPagoProv.load();
			Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setVisible(true);
	
			if (NS.fisicaMoral == 'M'){ //Proveedor o casa de Cambio morales
				NS.camposMoral();
				NS.panelConcentradora.setVisible(false);
				NS.storeActividadEconomica.load();
				NS.storeActividadGenerica.load();
				NS.storeComboGiro.load();
				NS.storeRiesgo.load();
				NS.storeTamano.load();
				NS.storeCalidad.load();
				NS.storeInmueble.load();
			}
			else{ //Proveedor o casa de cambio fisicos
				NS.camposFisica();
				NS.lblVentasAnuales.setVisible(true);
				NS.lblVentasAnuales.setDisabled(true);
				//Ext.getCmp(PF + 'txtVentasAnuales').setVisible(true);
				//Ext.getCmp(PF + 'txtVentasAnuales').setDisabled(true);
				NS.lblObjetoSocial.setVisible(true);
				NS.lblObjetoSocial.setDisabled(true);
				Ext.getCmp(PF + 'txtObjetoSocial').setVisible(true);
				Ext.getCmp(PF + 'txtObjetoSocial').setDisabled(true);
				NS.lblNoEmpleados.setVisible(true);
				NS.lblNoEmpleados.setDisabled(true);
				Ext.getCmp(PF + 'txtNoEmpleados').setVisible(true);
				Ext.getCmp(PF + 'txtNoEmpleados').setDisabled(true);
				NS.lblDiaLimite.setVisible(true);
				NS.lblDiaLimite.setDisabled(true);
				Ext.getCmp(PF + 'txtDiaLimite').setVisible(true);
				Ext.getCmp(PF + 'txtDiaLimite').setDisabled(true);
				NS.lblDiaRecepcion.setVisible(true);
				NS.lblDiaRecepcion.setDisabled(true);
				Ext.getCmp(PF + 'txtDiaRecepcion').setVisible(true);
				Ext.getCmp(PF + 'txtDiaRecepcion').setDisabled(true);
				NS.lblAccionesA.setVisible(true);
				NS.lblAccionesB.setVisible(true);
				NS.lblAccionesC.setVisible(true);
				NS.lblAccionesA.setDisabled(true);
				NS.lblAccionesB.setDisabled(true);
				NS.lblAccionesC.setDisabled(true);
				Ext.getCmp(PF + 'txtAccionesA').setVisible(true);
				Ext.getCmp(PF + 'txtAccionesB').setVisible(true);
				Ext.getCmp(PF + 'txtAccionesC').setVisible(true);
				Ext.getCmp(PF + 'txtAccionesA').setDisabled(true);
				Ext.getCmp(PF + 'txtAccionesB').setDisabled(true);
				Ext.getCmp(PF + 'txtAccionesC').setDisabled(true);
				NS.storeActividadEconomica.load();
				NS.storeEstadoCivil.load();
				NS.panelPAC.setVisible(true);
				NS.panelPAC.setDisabled(true);
				NS.panelConcentradora.setVisible(false);
				NS.lblCambioPersona.setVisible(true);			
				Ext.getCmp(PF + 'txtIdCambioPersona').setVisible(true);
				Ext.getCmp(PF + 'cmbCambioPersona').setVisible(true);
			}
				
		}
		else if (NS.tipoPersona == 'C'){ //Campos para los Clientes
			
			NS.lblEmpresaGral.setVisible(true);
			NS.lblEmpresaGral.setDisabled(true);
			Ext.getCmp(PF + 'txtEmpresaGral').setVisible(true);
			Ext.getCmp(PF + 'txtEmpresaGral').setDisabled(true);
						
			if (NS.fisicaMoral == 'M'){ //Clientes morales
				NS.camposMoral();
				NS.panelConcentradora.setVisible(false);
				NS.storeActividadEconomica.load();
				NS.storeActividadGenerica.load();
				NS.storeComboGiro.load();
				NS.storeRiesgo.load();
				NS.storeTamano.load();
				NS.storeCalidad.load();
				NS.storeInmueble.load();
				Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setVisible(true);
				Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setDisabled(true);
				NS.lblFormaPagoProv.setVisible(true);
				NS.lblFormaPagoProv.setDisabled(true);			
				Ext.getCmp(PF + 'txtIdFormaPagoProv').setVisible(true);
				Ext.getCmp(PF + 'txtIdFormaPagoProv').setDisabled(true);
				Ext.getCmp(PF + 'cmbFormaPagoProv').setVisible(true);
				Ext.getCmp(PF + 'cmbFormaPagoProv').setDisabled(true);
				NS.lblPagoReferenciado.setVisible(true);
				NS.lblPagoReferenciado.setDisabled(true);
				Ext.getCmp(PF + 'cmbPagoReferenciado').setVisible(true);
				Ext.getCmp(PF + 'cmbPagoReferenciado').setDisabled(true);
			}
			else{ //Clientes fisicos
				NS.camposFisica();
				NS.storeActividadEconomica.load();
				NS.storeEstadoCivil.load();
				NS.panelConcentradora.setVisible(false);
				NS.lblCambioPersona.setVisible(true);			
				Ext.getCmp(PF + 'txtIdCambioPersona').setVisible(true);
				Ext.getCmp(PF + 'cmbCambioPersona').setVisible(true);
				NS.panelPAC.setVisible(false);
			}
		}
		else if(NS.tipoPersona == 'E' || NS.tipoPersona == 'I'){ //Empresas y Filiales
			
			if (NS.manejaDivision){
				Ext.getCmp(PF + 'chkDivision').setVisible(true);
			}
			
			if (NS.tipoPersona == 'E')
				Ext.getCmp(PF + 'chkPagosCruzados').setVisible(true);
			
			//NS.lblCaja.setVisible(true);
			Ext.getCmp(PF + 'txtIdCaja').setVisible(true);
			Ext.getCmp(PF + 'cmbCaja').setVisible(true);
			NS.storeComboCaja.load();
			NS.panelExporta.setVisible(true);
			NS.lblFormaPagoProv.setVisible(false);
			Ext.getCmp(PF + 'chkProveedorServiciosBasicos').setVisible(false);
			Ext.getCmp(PF + 'chkReactivarPersona').setVisible(false);
			
			NS.camposMoral();
			NS.panelPAC.setVisible(false);
			NS.panelConcentradora.setVisible(true);
			NS.lblGrupoFlujo.setVisible(true);
			Ext.getCmp(PF + 'txtIdGrupo').setVisible(true);
			Ext.getCmp(PF + 'cmbGrupo').setVisible(true);
			NS.storeLlenaComboGrupo.load();
			NS.storeComboPayment.load();
			NS.storeComboTef.load();
			NS.panelTef.setVisible(true);
			NS.panelGpoRubro.setVisible(false);
			
			NS.lblRiesgo.setVisible(false);
			Ext.getCmp(PF + 'txtIdRiesgo').setVisible(false);
			Ext.getCmp(PF + 'cmbRiesgo').setVisible(false);
			NS.lblNoEmpleados.setVisible(false);
			NS.lblNoEmpleados.setDisabled(false);
			Ext.getCmp(PF + 'txtNoEmpleados').setVisible(false);
			NS.lblDiaLimite.setVisible(false);
			Ext.getCmp(PF + 'txtDiaLimite').setVisible(false);			
			NS.lblDiaRecepcion.setVisible(false);			
			Ext.getCmp(PF + 'txtDiaRecepcion').setVisible(false);			
			NS.lblAccionesA.setVisible(false);
			NS.lblAccionesB.setVisible(false);
			NS.lblAccionesC.setVisible(false);
			Ext.getCmp(PF + 'txtAccionesA').setVisible(false);
			Ext.getCmp(PF + 'txtAccionesB').setVisible(false);
			Ext.getCmp(PF + 'txtAccionesC').setVisible(false);
						
			if (NS.fisicaMoral == 'M'){ //Empresas y Filiales morales
				NS.storeActividadEconomica.load();
				NS.storeActividadGenerica.load();
				NS.storeComboGiro.load();			
				NS.storeTamano.load();
				NS.storeCalidad.load();
				NS.storeInmueble.load();				
			}
		}
		else if(NS.tipoPersona == 'F' || NS.tipoPersona == 'R'){ //Personas Fisica y Prestadores de Servicio			
			//NS.lblGrupoFlujo.setVisible(true);
			//NS.lblGrupoFlujo.setDisabled(true);
			Ext.getCmp(PF + 'txtIdGrupo').setVisible(true);
			Ext.getCmp(PF + 'cmbGrupo').setVisible(true);
			Ext.getCmp(PF + 'txtIdGrupo').setDisabled(true);
			Ext.getCmp(PF + 'cmbGrupo').setDisabled(true);
			
			NS.camposFisica();			
			NS.storeActividadEconomica.load();
			NS.storeEstadoCivil.load();			
			NS.panelConcentradora.setVisible(false);
			NS.lblCambioPersona.setVisible(true);			
			Ext.getCmp(PF + 'txtIdCambioPersona').setVisible(true);
			Ext.getCmp(PF + 'cmbCambioPersona').setVisible(true);
		}
		 		
	};		
		
	
	NS.cierraVentanaDireccionesE = function(){
			NS.panelBusqueda.setVisible(false);
			NS.panelGrid.setVisible(false);
			NS.panelCamposDI.setVisible(false);
			NS.panelBotonesDI.setVisible(false);
			NS.panelCamposE.setVisible(true);
			NS.panelBotonesE.setVisible(true);
	};
	
	NS.cierraVentanaMediosContactoPM = function(){
		NS.panelBusqueda.setVisible(false);
		NS.panelGrid.setVisible(false);
		NS.panelCamposMC.setVisible(false);
		NS.panelBotonesMCPM.setVisible(false);
		NS.panelCamposPM.setVisible(true);
		NS.panelBotonesPM.setVisible(true);
};
	
	NS.cierraVentanaMediosContactoPF = function(){
		NS.panelBusqueda.setVisible(false);
		NS.panelGrid.setVisible(false);
		NS.panelCamposMC.setVisible(false);
		NS.panelBotonesMCPF.setVisible(false);
		NS.panelCamposPF.setVisible(true);
		NS.panelBotonesPF.setVisible(true);
};
	
	NS.cierraVentanaMediosContactoK = function(){
		NS.panelBusqueda.setVisible(false);
		NS.panelGrid.setVisible(false);
		NS.panelCamposMC.setVisible(false);
		NS.panelBotonesMCK.setVisible(false);
		NS.panelCamposK.setVisible(true);
		NS.panelBotonesK.setVisible(true);
};
	
	NS.cierraVentanaMediosContactoFI = function(){
		NS.panelBusqueda.setVisible(false);
		NS.panelGrid.setVisible(false);
		NS.panelCamposMC.setVisible(false);
		NS.panelBotonesMCFI.setVisible(false);
		NS.panelCamposFI.setVisible(true);
		NS.panelBotonesFI.setVisible(true);
};
	
	NS.cierraVentanaMediosContactoIB = function(){
		NS.panelBusqueda.setVisible(false);
		NS.panelGrid.setVisible(false);
		NS.panelCamposMC.setVisible(false);
		NS.panelBotonesMCIB.setVisible(false);
		NS.panelCamposIB.setVisible(true);
		NS.panelBotonesIB.setVisible(true);
};
	
	NS.cierraVentanaMediosContactoE = function(){
		NS.panelBusqueda.setVisible(false);
		NS.panelGrid.setVisible(false);
		NS.panelCamposMC.setVisible(false);
		NS.panelBotonesMCE.setVisible(false);
		NS.panelCamposE.setVisible(true);
		NS.panelBotonesE.setVisible(true);
};
	
	NS.cierraVentanaDireccionesFI = function(){
		NS.panelBusqueda.setVisible(false);
		NS.panelGrid.setVisible(false);
		NS.panelCamposDI.setVisible(false);
		NS.panelBotonesDIFI.setVisible(false);
		NS.panelCamposFI.setVisible(true);
		NS.panelBotonesFI.setVisible(true);
};
	
	NS.cierraVentana = function(){
		NS.panelCampos.setVisible(false);
		NS.panelBotones.setVisible(false);
		//NS.panelBotonesPr.SetVisible(false);
		NS.panelBusqueda.setVisible(true);
		NS.panelGrid.setVisible(true);
		
	};
	
	NS.insertarActualizarIB = function(){
		var vector = {};
		var matriz = new Array();

		vector.noPersona = NS.noPersona;
		vector.fechaHoy=NS.fecHoy;
		vector.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		vector.fechaIngreso = NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFechaIngresoIB').getValue());
		vector.rfc = Ext.getCmp(PF + 'txtRFCIB').getValue();
		vector.razonSocial = Ext.getCmp(PF + 'txtRazonSocialIB').getValue();
		vector.nombreCorto = Ext.getCmp(PF + 'txtNombreCortoIB').getValue();
		
		
		if (NS.modificarCuentas || NS.modificar)
			vector.tipoOperacion = "MODIFICAR";
		else
			vector.tipoOperacion= "INSERTAR";
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
				
		//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
		  ConsultaPersonasAction.validaDatosInsertUpdate(jSonString, function(resultadoValida, e){			  
			if (resultadoValida == ''){
				ConsultaPersonasAction.guardarIB(jSonString, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
					{
						if(resultado == 'Ocurrio un error al almacenar el registro'){
							Ext.Msg.alert('SET', resultado);
						}
						Ext.Msg.alert('SET', resultado);
						//NS.limpiarIB();
	 					//NS.cierraVentana();
	 					NS.panelBotonesIB.setVisible(false);
	 					NS.panelCamposIB.setVisible(false);
	 					NS.panelBotonesMIB.setVisible(false);
	 					NS.panelCamposDI.setVisible(true);
	 					NS.panelBotonesDI.setVisible(true);
	 					NS.llenaCamposDIIB();
	 					
	 					//NS.buscar();
					}					
				});					
			}
			else if (resultadoValida !== '' && resultadoValida !== undefined && resultadoValida !== null)
				Ext.Msg.alert('SET', resultadoValida);
		  });
		
			
 	};
 	
 	NS.insertarDi = function(){
 		Ext.getCmp(PF + 'Aceptar').setVisible(false);
		var vector = {};
		var matriz = new Array();
		vector.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		vector.persona = Ext.getCmp(PF + 'txtNoPersonaD').getValue();
		vector.razonSocialDI = Ext.getCmp(PF + 'txtRazonSocialD').getValue();
		vector.idTipoD = Ext.getCmp(PF + 'txtIdTipoD').getValue();
		vector.calle = Ext.getCmp(PF + 'txtCalle').getValue();
		vector.colonia = Ext.getCmp(PF + 'txtColonia').getValue();
		vector.cp = Ext.getCmp(PF + 'txtCp').getValue();
		vector.delegacion = Ext.getCmp(PF + 'txtDelegacion').getValue();
		vector.ciudad = Ext.getCmp(PF + 'txtCiudad').getValue();
		vector.estado = Ext.getCmp(PF + 'txtEstado').getValue();
		vector.pais = Ext.getCmp(PF + 'txtPais').getValue();
	

		if ( NS.modificar==true)
			vector.tipoOperacion = "MODIFICAR";
		else
			vector.tipoOperacion= "INSERTAR";
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
				
		//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
		  ConsultaPersonasAction.validaDatosInsertUpdate(jSonString, function(resultadoValida, e){			  
			if (resultadoValida == ''){
				ConsultaPersonasAction.guardarDireccion(jSonString, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
					{
						if(resultado == 'Ocurrio un error al almacenar el registro'){
							Ext.Msg.alert('SET', resultado);
							
						}
	 					Ext.Msg.alert('SET', resultado);
						NS.limpiarE();
	 					NS.panelCamposDI.setVisible(false);
	 					NS.panelBotonesDI.setVisible(false);
	 					NS.panelBotonesDIM.setVisible(false);
	 					NS.panelBusqueda.setVisible(true);
	 					NS.panelGrid.setVisible(true);
	 					NS.limpiarDI();
	 					NS.buscar();
					}					
				});					
			}
			else if (resultadoValida !== '' && resultadoValida !== undefined && resultadoValida !== null)
				Ext.Msg.alert('SET', resultadoValida);
		  });
		
			
 	};
 	
 	NS.insertarActualizarPM = function(){
 		var vector = {};
		var matriz = new Array();
		
		
		vector.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		vector.noEmpresa = Ext.getCmp(PF + 'txtEmpresaPM').getValue();
		vector.noPersona = Ext.getCmp(PF + 'txtNoPersonaPM').getValue();
		vector.rfc = Ext.getCmp(PF + 'txtRFCPM').getValue();
		vector.nombreCorto = Ext.getCmp(PF + 'txtNombreCortoPM').getValue();
		vector.idFormaPagoProv = Ext.getCmp(PF + 'txtIdFormaPagoProvPM').getValue();
		vector.proveedorServiciosBasicos = Ext.getCmp(PF + 'chkProveedorServiciosBasicosPM').getValue();
		vector.reactivarPersona = Ext.getCmp(PF + 'chkReactivarPersonaPM').getValue();
		vector.fechaIngreso = NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFechaIngresoPM').getValue());
		vector.razonSocial = Ext.getCmp(PF + 'txtRazonSocialPM').getValue();
		vector.fechaHoy=NS.fecHoy;
		
		
		if (NS.modificarCuentas || NS.modificar)
			vector.tipoOperacion = "MODIFICAR";
		else
			vector.tipoOperacion= "INSERTAR";
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
				
		//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
		  ConsultaPersonasAction.validaDatosInsertUpdate(jSonString, function(resultadoValida, e){			  
			if (resultadoValida == ''){
				ConsultaPersonasAction.guardarPM(jSonString, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
					{
						if(resultado == 'Ocurrio un error al almacenar el registro'){
							Ext.Msg.alert('SET', resultado);
						}
						Ext.Msg.alert('SET', resultado);
//						NS.limpiarCamposNuevos();
//	 					NS.cierraVentana();
//	 					NS.buscar();
						
						NS.panelBotonesPM.setVisible(false);
	 					NS.panelCamposPM.setVisible(false);
	 					NS.panelCamposDI.setVisible(true);
	 					NS.panelBotonesDI.setVisible(true);
	 					NS.llenaCamposDIPM();
					}					
				});					
			}
			else if (resultadoValida !== '' && resultadoValida !== undefined && resultadoValida !== null)
				Ext.Msg.alert('SET', resultadoValida);
		  });
 		
 	};
 	
 	NS.insertarActualizarF = function(){
 		var vector = {};
		var matriz = new Array();
		
		vector.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		vector.noPersona = Ext.getCmp(PF + 'txtNoPersonaF').getValue();
		vector.rfc = Ext.getCmp(PF + 'txtRFCF').getValue();
		vector.nombreCorto = Ext.getCmp(PF + 'txtNombreCortoF').getValue();
		vector.paterno = Ext.getCmp(PF + 'txtPaternoF').getValue();
		vector.materno = Ext.getCmp(PF + 'txtMaternoF').getValue();
		vector.nombre = Ext.getCmp(PF + 'txtNombreF').getValue();
		vector.fechaHoy=NS.fecHoy;
		vector.sexo = NS.sexo;
		

		if (NS.modificarCuentas || NS.modificar)
			vector.tipoOperacion = "MODIFICAR";
		else
			vector.tipoOperacion= "INSERTAR";
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
				
		//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
		  ConsultaPersonasAction.validaDatosInsertUpdate(jSonString, function(resultadoValida, e){			  
			if (resultadoValida == ''){
				ConsultaPersonasAction.guardarF(jSonString, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
					{
						if(resultado == 'Ocurrio un error al almacenar el registro'){
							Ext.Msg.alert('SET', resultado);
						}
						Ext.Msg.alert('SET', resultado);
						//NS.limpiarF();
	 					//NS.cierraVentana();
	 					NS.panelCamposF.setVisible(false);
	 					NS.panelBotonesMOF.setVisible(false);
	 					NS.panelBotonesF.setVisible(false);
//	 					NS.panelCamposDI.setVisible(true);
//	 					NS.panelBotonesDI.setVisible(true);
	 					NS.llenaCamposDIF();
	 					NS.llenaDatosDI();
	 					
	 				
					}					
				});					
			}
			else if (resultadoValida !== '' && resultadoValida !== undefined && resultadoValida !== null)
				Ext.Msg.alert('SET', resultadoValida);
		  });
 		
 	};
 	
 	NS.insertarActualizarPF = function(){
 		var vector = {};
		var matriz = new Array();
		
		vector.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		vector.noEmpresa = Ext.getCmp(PF + 'txtEmpresaPF').getValue();
		vector.noPersona = Ext.getCmp(PF + 'txtNoPersonaPF').getValue();
		vector.rfc = Ext.getCmp(PF + 'txtRFCPF').getValue();
		vector.nombreCorto = Ext.getCmp(PF + 'txtNombreCortoPF').getValue();
		vector.paterno = Ext.getCmp(PF + 'txtPaternoPF').getValue();
		vector.materno = Ext.getCmp(PF + 'txtMaternoPF').getValue();
		vector.nombre = Ext.getCmp(PF + 'txtNombrePF').getValue();
		vector.sexo = NS.sexo;
		vector.idFormaPagoProv = Ext.getCmp(PF + 'txtIdFormaPagoProvF').getValue();
		vector.proveedorServiciosBasicos = Ext.getCmp(PF + 'chkProveedorServiciosBasicosPF').getValue();
		vector.reactivarPersona = Ext.getCmp(PF + 'chkReactivarPersonaPF').getValue();
		vector.fechaHoy=NS.fecHoy;

		if (NS.modificarCuentas || NS.modificar)
			vector.tipoOperacion = "MODIFICAR";
		else
			vector.tipoOperacion= "INSERTAR";
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
				
		//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
		  ConsultaPersonasAction.validaDatosInsertUpdate(jSonString, function(resultadoValida, e){			  
			if (resultadoValida == ''){
				ConsultaPersonasAction.guardarPF(jSonString, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
					{
						if(resultado == 'Ocurrio un error al almacenar el registro'){
							Ext.Msg.alert('SET', resultado);
						}
						Ext.Msg.alert('SET', resultado);
						//NS.limpiarCamposNuevos();
	 					//NS.cierraVentana();
	 					//NS.buscar();
	 					
	 					NS.panelBotonesPF.setVisible(false);
	 					NS.panelCamposPF.setVisible(false);
	 					NS.panelCamposDI.setVisible(true);
	 					NS.panelBotonesDI.setVisible(true);
	 					NS.llenaCamposDIPF();
					}					
				});					
			}
			else if (resultadoValida !== '' && resultadoValida !== undefined && resultadoValida !== null)
				Ext.Msg.alert('SET', resultadoValida);
		  });
 		
 	};
 	
 	NS.insertarActualizarK = function(){
 		var vector = {};
		var matriz = new Array();
		
		vector.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
//		vector.noEmpresa = Ext.getCmp(PF + 'txtEmpresaK').getValue();
		vector.fechaHoy=NS.fecHoy;
		vector.noPersona = Ext.getCmp(PF + 'txtNoPersonaK').getValue();
		vector.fechaIngreso = NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFechaIngresoK').getValue());
		vector.rfc = Ext.getCmp(PF + 'txtRFCK').getValue();
		vector.razonSocial = Ext.getCmp(PF + 'txtRazonSocialK').getValue();
		vector.nombreCorto = Ext.getCmp(PF + 'txtNombreCortoK').getValue();
		
		if(NS.modificar){
			vector.tipoOperacion = "MODIFICAR";
		}else{
			vector.tipoOperacion= "INSERTAR";
		}
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
				
		//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
		  ConsultaPersonasAction.validaDatosInsertUpdate(jSonString, function(resultadoValida, e){			  
			if (resultadoValida == ''){
				ConsultaPersonasAction.guardarCasa(jSonString, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
					{
						if(resultado == 'Ocurrio un error al almacenar el registro'){
							Ext.Msg.alert('SET', resultado);
						}
						Ext.Msg.alert('SET', resultado);
						//NS.limpiarK();
	 					NS.panelCamposK.setVisible(false);
	 					NS.panelBotonesK.setVisible(false);
	 					NS.panelBotonesMOK.setVisible(false);
	 					NS.panelCamposDI.setVisible(true);
	 					NS.panelBotonesDI.setVisible(true);
	 					NS.llenaCamposDIK();
	 					NS.buscar();
					}					
				});					
			}
			else if (resultadoValida !== '' && resultadoValida !== undefined && resultadoValida !== null)
				Ext.Msg.alert('SET', resultadoValida);
		  });
 		
 	};
 	
 	NS.insertarActualizarE = function(){
 		var vector = {};
		var matriz = new Array();
		
		vector.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		vector.noEmpresa = Ext.getCmp(PF + 'txtEmpresaE').getValue();
		vector.fechaHoy=NS.fecHoy;
		vector.noPersona = Ext.getCmp(PF + 'txtNoPersonaE').getValue();
		vector.fechaIngreso = NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFechaIngresoE').getValue());
		vector.rfc = Ext.getCmp(PF + 'txtRFCE').getValue();
		vector.razonSocial = Ext.getCmp(PF + 'txtRazonSocialE').getValue();
		vector.nombreCorto = Ext.getCmp(PF + 'txtNombreCortoE').getValue();
		vector.idGrupo = Ext.getCmp(PF + 'txtIdGrupoE').getValue();
		vector.idCalidad = Ext.getCmp(PF + 'txtIdCalidadE').getValue();
		vector.idInmueble = Ext.getCmp(PF + 'txtIdInmuebleE').getValue();
		vector.idCaja = Ext.getCmp(PF + 'txtIdCajaE').getValue();
		vector.concentradora = NS.concentradora;
		
		if(NS.modificar){
			vector.tipoOperacion = "MODIFICAR";
		}else{
			vector.tipoOperacion= "INSERTAR";
		}
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
				
		//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
		  ConsultaPersonasAction.validaDatosInsertUpdate(jSonString, function(resultadoValida, e){			  
			if (resultadoValida == ''){
				ConsultaPersonasAction.aceptar(jSonString, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
					{
						if(resultado == 'Ocurrio un error al almacenar el registro'){
							Ext.Msg.alert('SET', resultado);
							NS.cierraVentana();
		 					NS.limpiarE();
		 					NS.limpiarDI();
		 					NS.buscar();
		 					NS.panelCamposDI.setVisible(false);
		 					NS.panelBotonesDI.setVisible(false);
		 					NS.panelBotonesDIM.setVisible(false);
		 					NS.panelCamposE.setVisible(false);
		 					NS.panelBotonesE.setVisible(false);
		 					NS.panelBotonesMOE.setVisible(false);
		 					NS.panelBusqueda.setVisible(true);
		 					NS.panelGrid.setVisible(true);
		 					
						}else{
						Ext.Msg.alert('SET', resultado);
						//NS.limpiarE();
	 					NS.panelCamposE.setVisible(true);
	 					NS.panelBotonesE.setVisible(true);
	 					NS.panelBotonesMOE.setVisible(true);
	 					//NS.panelCamposDI.setVisible(true);
	 					//NS.panelBotonesDI.setVisible(true);
	 					//NS.llenaCamposDI();
//	 					NS.panelBusqueda.setVisible(true);
//	 					NS.panelGrid.setVisible(true);
//	 					NS.buscar();
						}
					}					
				});					
			}
			else if (resultadoValida !== '' && resultadoValida !== undefined && resultadoValida !== null)
				Ext.Msg.alert('SET', resultadoValida);
		  });
 		
 	};
 	
 	//////////////////////////////////Termina InsertarActualizarEmpresa///////////////////////////////
	
	NS.insertarActualizar = function(){
		var vector = {};
		var matriz = new Array();
		
		if (Ext.getCmp(PF + 'txtEmpresaE').getValue() != '')
			NS.noEmpresa = Ext.getCmp(PF + 'txtEmpresaE').getValue();
		
		vector.noEmpresa = NS.noEmpresa;
		vector.noPersona = NS.noPersona;
		vector.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		vector.diaLimite = Ext.getCmp(PF + 'txtDiaLimite').getValue() == "" ? 0 : Ext.getCmp(PF + 'txtDiaLimite').getValue();
		vector.diaRecepcion = Ext.getCmp(PF + 'txtDiaRecepcion').getValue() == "" ? 0 : Ext.getCmp(PF + 'txtDiaRecepcion').getValue();
		vector.idActividadGenerica = Ext.getCmp(PF + 'txtIdActividadGenerica').getValue();
		vector.idActividadEconomica = Ext.getCmp(PF + 'txtIdActividadEconomica').getValue();
		vector.idGiro = Ext.getCmp(PF + 'txtIdGiro').getValue();
		vector.idRiesgo = Ext.getCmp(PF + 'txtIdRiesgo').getValue();
		vector.noEmpleados = Ext.getCmp(PF + 'txtNoEmpleados').getValue();
		vector.accionesA = Ext.getCmp(PF + 'txtAccionesA').getValue();
		vector.accionesB = Ext.getCmp(PF + 'txtAccionesB').getValue();
		vector.accionesC = Ext.getCmp(PF + 'txtAccionesC').getValue();
		vector.fechaIngreso = Ext.getCmp(PF + 'txtFechaIngreso').getValue();
		vector.fechaHoy = NS.fecHoy;
		//vector.ventasAnuales = Ext.getCmp(PF + 'txtVentasAnuales').getValue() == "" ? 0 : Ext.getCmp(PF + 'txtVentasAnuales').getValue();
		vector.rfc = Ext.getCmp(PF + 'txtRFCGral').getValue();
		vector.razonSocial = Ext.getCmp(PF + 'txtRazonSocialGral').getValue();
		vector.paterno = Ext.getCmp(PF + 'txtPaternoGral').getValue();
		vector.materno = Ext.getCmp(PF + 'txtMaternoGral').getValue();
		vector.nombre = Ext.getCmp(PF + 'txtNombreGral').getValue();
		vector.nombreCorto = Ext.getCmp(PF + 'txtNombreCorto').getValue();
//		vector.objetoSocial = Ext.getCmp(PF + 'txtObjetoSocial').getValue();
//		vector.puesto = Ext.getCmp(PF + 'txtPuesto').getValue();
//		vector.idEstatus = NS.estatus;
//		vector.idTamano = Ext.getCmp(PF + 'txtIdTamano').getValue();
//		vector.idCalidad = Ext.getCmp(PF + 'txtIdCalidad').getValue();
//		vector.idInmueble = Ext.getCmp(PF + 'txtIdInmueble').getValue();
//		vector.fisicaMoral = NS.fisicaMoral;
//		vector.idEstadoCivil = Ext.getCmp(PF + 'txtIdEstadoCivil').getValue();
		vector.sexo = NS.sexo;
		vector.proveedor = Ext.getCmp(PF + 'chkProveedor').getValue();
		vector.asociacion = Ext.getCmp(PF + 'chkAsociacion').getValue();
		vector.contratoInversion = Ext.getCmp(PF + 'chkContratoInversion').getValue();
		vector.idRubro = '0'; //Ext.getCmp(PF + 'txtRubro').getValue();
		vector.equivalePersona = Ext.getCmp(PF + 'txtEquivalePersona').getValue();
		vector.pagoReferenciado = Ext.getCmp(PF + 'cmbPagoReferenciado').getValue() == "" ||  Ext.getCmp(PF + 'cmbPagoReferenciado').getValue() == null ? '' :  Ext.getCmp(PF + 'cmbPagoReferenciado').getValue();		
		vector.idCaja = Ext.getCmp(PF + 'txtIdCaja').getValue();
		vector.idCambioPersona = Ext.getCmp(PF + 'txtIdCambioPersona').getValue();
		vector.idFormaPagoProv = Ext.getCmp(PF + 'txtIdFormaPagoProv').getValue();
		vector.division = Ext.getCmp(PF + 'chkDivision').getValue();
		vector.pagosCruzados = Ext.getCmp(PF + 'chkPagosCruzados').getValue();
		vector.proveedorServiciosBasicos = Ext.getCmp(PF + 'chkProveedorServiciosBasicos').getValue();
		vector.exporta = NS.exporta;
		vector.concentradora = NS.concentradora;
		vector.contratoTEF = Ext.getCmp(PF + 'cmbContratoTef').getValue();
		vector.contratoPayment = Ext.getCmp(PF + 'cmbContratoPayment').getValue();
		vector.manejaDivision = NS.manejaDivision;
		vector.tipoPersona = NS.tipoPersona;		
		vector.usuarioModif = NS.idUsuario;
		vector.noBenef = Ext.getCmp(PF + 'txtNoBenef').getValue();
		vector.descBenef = Ext.getCmp(PF + 'cmbBenef').getValue();
		vector.grupoRubro = '0'; //Ext.getCmp(PF + 'txtGrupoRubro').getValue();
		
		if (NS.modificarCuentas || NS.modificar)
			vector.tipoOperacion = "MODIFICAR";
		else
			vector.tipoOperacion= "INSERTAR";
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
				
		//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
		  ConsultaPersonasAction.validaDatosInsertUpdate(jSonString, function(resultadoValida, e){			  
			if (resultadoValida == ''){
				ConsultaPersonasAction.aceptar(jSonString, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
					{
						if(resultado == 'Ocurrio un error al almacenar el registro'){
							Ext.Msg.alert('SET', resultado);
						}
						Ext.Msg.alert('SET', resultado);
						NS.limpiarCamposNuevos();
	 					NS.cierraVentana();
	 					NS.buscar();
					}					
				});					
			}
			else if (resultadoValida !== '' && resultadoValida !== undefined && resultadoValida !== null)
				Ext.Msg.alert('SET', resultadoValida);
		  });
		
			
 	};
 	
	NS.validaVaciosDI = function(){
		
		
		 if(Ext.getCmp(PF + 'txtIdTipoD').getValue()==""){
			Ext.Msg.alert('SET',"Seleccione el tipo de direccion");
			return false;
		}else if(Ext.getCmp(PF + 'txtCalle').getValue()==""){
			Ext.Msg.alert('SET',"Inserte la calle de la direccion");
			return false;
		}else if(Ext.getCmp(PF + 'txtColonia').getValue()==""){
			Ext.Msg.alert('SET',"Inserte la colonia de la direccion");
			return false;	
		}else if(Ext.getCmp(PF + 'txtCp').getValue()==""){
			Ext.Msg.alert('SET',"Inserte el codigo postal");
			return false;
		}else if(Ext.getCmp(PF + 'txtDelegacion').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa la Delegacion");
			return false;
		}else if(Ext.getCmp(PF + 'txtCiudad').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa la Ciudad");
			return false;
		}else if(Ext.getCmp(PF + 'txtEstado').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa el estado");
			return false;
		}else if(Ext.getCmp(PF + 'txtPais').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa el pais");
			return false;	
		}else{
			return true;
		}
	};
 	
 	NS.validaVaciosF = function(){
		 if(Ext.getCmp(PF + 'txtPaternoF').getValue()==""){
			Ext.Msg.alert('SET',"El apellido paterno de la persona");
			return false;
		}else if(Ext.getCmp(PF + 'txtMaternoF').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa el apellido materno de la persona");
			return false;
		}else if(Ext.getCmp(PF + 'txtNombreF').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa el nombre de la persona");
			return false;
		}else if(Ext.getCmp(PF + 'txtNombreCortoF').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa el nombre corto de la persona");
			return false;
		}else if(Ext.getCmp(PF + 'txtRFCF').getValue()=="" ||
				(Ext.getCmp(PF + 'txtRFCF').getValue()+'').match(
						'^(([A-Z]|[a-z]|\s){1})(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))')==null){
			Ext.Msg.alert('SET',"Ingresa un rfc valido");
			return false;
		}
		else{
			return true;
		}
	};
	
	NS.validaVaciosIB = function(){
		 if(Ext.getCmp(PF + 'txtRazonSocialIB').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa la razon social de la empresa");
			return false;
		}else if(Ext.getCmp(PF + 'txtNombreCortoIB').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa el nombre de la Casa de Cambio");
			return false;
		}else if(Ext.getCmp(PF + 'txtRFCIB').getValue()=="" ||
				(Ext.getCmp(PF + 'txtRFCIB').getValue()+'').match(
						'(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))')==null){
			//'^(([A-Z]|[a-z]|\s){1})(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))'
			Ext.Msg.alert('SET',"Ingresa un rfc valido");
		    return false;
		}else if(Ext.getCmp(PF + 'txtFechaIngresoIB').getValue()==""){
			Ext.Msg.alert('SET',"Ingresa la fecha de ingreso");
			return false;
		}
		else{
			return true;
		}
	};
 	
 	NS.validaVaciosK = function(){
 		 if(Ext.getCmp(PF + 'txtRazonSocialK').getValue()==""){
 			Ext.Msg.alert('SET',"Ingresa la razon social de la empresa");
 			return false;
 		}else if(Ext.getCmp(PF + 'txtNombreCortoK').getValue()==""){
 			Ext.Msg.alert('SET',"Ingresa el nombre de la Casa de Cambio");
 			return false;
 		}else if(Ext.getCmp(PF + 'txtRFCK').getValue()=="" ||
 				(Ext.getCmp(PF + 'txtRFCK').getValue()+'').match(
 						'(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))')==null){
 			//'^(([A-Z]|[a-z]|\s){1})(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))'
 			Ext.Msg.alert('SET',"Ingresa un rfc valido");
 			return false;
 		}else if(Ext.getCmp(PF + 'txtFechaIngresoK').getValue()==""){
 			Ext.Msg.alert('SET',"Ingresa la fecha de ingreso");
 			return false;
 		}
 		else{
 			return true;
 		}
 	};
 	
 	NS.validaVaciosE = function(){
 		if (Ext.getCmp(PF + 'txtEmpresaE').getValue()=="") {
 			Ext.Msg.alert('SET',"Ingresa el numero de empresa");
 			return false;
 		}else if(Ext.getCmp(PF + 'txtRazonSocialE').getValue()==""){
 			Ext.Msg.alert('SET',"Ingresa la razon social de la empresa");
 			return false;
 		}else if(Ext.getCmp(PF + 'txtNombreCortoE').getValue()==""){
 			Ext.Msg.alert('SET',"Ingresa el nombre de la empresa");
 			return false;
 		}else if(Ext.getCmp(PF + 'txtRFCE').getValue()=="" ||
 				(Ext.getCmp(PF + 'txtRFCE').getValue()+'').match(
 						'(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))')==null){
 			//'^(([A-Z]|[a-z]|\s){1})(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))'
 			Ext.Msg.alert('SET',"Ingresa un rfc valido");
 			return false;
 		}else if(Ext.getCmp(PF + 'txtFechaIngresoE').getValue()==""){
 			Ext.Msg.alert('SET',"Ingresa la fecha de ingreso");
 			return false;
 		}else if(Ext.getCmp(PF + 'txtIdCajaE').getValue()==""){
 			Ext.Msg.alert('SET',"Selecciona el tipo de caja");
 			return false;
 		}
 		else{
 			return true;
 		}
 	};
 	
 	
	
 	NS.deshabilitaPersona = function(){
 		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
 		
 		if (registroSeleccionado.length <= 0)
 			Ext.Msg.alert('SET', 'Debe de seleccionar algun registro');
 		else{
 			ConsultaPersonasAction.verificaRegistro(registroSeleccionado[0].get('noPersona'), registroSeleccionado[0].get('idTipoPersona'), function(resultado, e){
 				if (resultado != 0 && resultado != undefined && resultado != null){
 					Ext.Msg.confirm('SET', '¿Esta seguro de inhabilitar a esta persona?', function(btn){
 						if (btn == 'yes') {
 							ConsultaPersonasAction.inhabilitaPersona(registroSeleccionado[0].get('noPersona'), registroSeleccionado[0].get('idTipoPersona'), function(resultado, e){
 								if (resultado != '' && resultado != undefined && resultado != null)
 									Ext.Msg.alert('SET', resultado);
 							});
 						}
 					});
 				} 					
 			});
 		} 		
 	};
	
	//STORE
	NS.storeLlenaComboEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaComboEmpresas,
		idProperty: 'noEmpresa',
		fields: [
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboEmpresa, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen empresa en el catalogo');					
			}
		}
	});
	NS.storeLlenaComboEmpresa.load();
	
	NS.storeLlenaComboTipoPersona = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaComboTipoPersona,
		idProperty: 'idTipoPersona',
		fields: [
		 	{name: 'idTipoPersona'},
		 	{name: 'descTipoPersona'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboTipoPersona, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Tipos de Personas dados de alta');
			}
		}
	});
	NS.storeLlenaComboTipoPersona.load();
//	
//	NS.storeLlenaComboBancoCP = new Ext.data.DirectStore({
//		paramsAsHash: false,
//		root: '',
//		baseParams: {},
//		paramOrder: [],
//		directFn: ConsultaPersonasAction.llenaComboBancoCP,
//		idProperty: 'idTipoPersona',
//		fields: [
//		 	{name: 'idTipoPersona'},
//		 	{name: 'descTipoPersona'}
//		],
//		listeners: {
//			load: function(s, records) {
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboBancoCP, msg: "Cargando..."});
//				if (records.length == null || records.length <= 0)
//					Ext.Msg.alert('SET', 'No hay bancos para esta operacion');
//			}
//		}
//	});
//	//NS.storeLlenaComboBancoCP.load();
//	

	
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['tipoPersona', 'equivalePersona', 'razonSocial', 'paterno', 'materno', 'nombre',  'inactivas'],
		directFn: ConsultaPersonasAction.llenaGrid,
		fields:
		[
		 	{name: 'equivalePersona',type:'string'},
		 	{name: 'nombre'},
		 	{name: 'noEmpresa'},
		 	{name: 'idTipoPersona'},
		 	{name: 'noPersona'},
		 	{name: 'fisicaMoral'},
		 	{name: 'idEstatus'},
		 	{name: 'estatus'}
		 	
		],
		listeners: {
			load: function (s, records) {
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existen datos para estos parametros de busqueda');
				}
				Ext.getCmp(PF + 'buscar').setDisabled(false);
			}
		}
	});
	
	NS.storeLlenaGridCP = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['tipoPersona', 'noPersona'],
		directFn: ConsultaPersonasAction.llenaGridCP,
		fields:
		[
		 	{name: 'bancoI',type:'string'}, 
			{name:'chequera'},
		 	{name:'divisa'},
		 	{name:'descChequera'},
		 	{name:'sucursal'},
		 	{name:'plaza'},	
		 	{name:'clabe'},
		 	{name:'swift'},
		 	{name:'aba'},
		 	{name:'bankTrue'},
		 	{name:'bankCorresponding'},
		 	{name:'hequeraTrue'},
		 	{name:'bancoAnterior'},
		 	{name:'chequeraAnterior'},
		 	{name:'especial'},
		 	{name:'idBanco'}
		 	
		],
		listeners: {
			load: function (s, records) {
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existe Cuenta de Proveedores con estos parametros de busqueda');
				}
				Ext.getCmp(PF + 'buscar').setDisabled(false);
			}
		}
	});
	
	NS.storeEstadoCivil = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaEstadoCivil,
		fields:
		[
		 	{name: 'idEstadoCivil'},
		 	{name: 'descEstadoCivil'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEstadoCivil, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos en el Catalogo de Estado Civil');
			}
		}
	});
	
	
	            	
	NS.datosCombo = [
	 	['1', 'Per. Física'],
	 	['2', 'Socio']
	]
	
	NS.storeCambioPersona = new Ext.data.SimpleStore({
		idProperty: 'idCambioPersona',
		fields: [
		 	{name: 'idCambioPersona'},
		 	{name: 'descCambioPersona'}
		]
	});
	NS.storeCambioPersona.loadData(NS.datosCombo);
	
	NS.storeRiesgo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaRiesgo,
		fields:
		[
		 	{name: 'idRiesgo'},
		 	{name: 'descRiesgo'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCambioPersona, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen causas de Riesgos en el catalogo');				
			}
		}
	});
	
	NS.storeTamano = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.comboTamano,
		fields:
		[
			{name: 'idTamano'},
			{name: 'descTamano'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTamano, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos en el Catalogo de Tamaños');
			}
		}
	});
	
	
	
	NS.datosPagoReferenciado = [
 	 	['no_folio_det'],
 	 	['no_factura'],
 	 	['no_docto'],
 	 	['referencia'] 	 	 
 	]
 	
 	NS.storePagoReferenciado = new Ext.data.SimpleStore({
 		idProperty: 'descPagoReferenciado',
 		fields: [
 		 	{name: 'descPagoReferenciado'} 		 	
 		]
 	});
 	NS.storePagoReferenciado.loadData(NS.datosPagoReferenciado);
	 	
 	NS.storeComboTef = new Ext.data.DirectStore({
 		paramsAsHash: false,
 		root: '',
 		baseParams: {},
 		paramOrder: [],
 		directFn: ConsultaPersonasAction.comboTEF,
 		fields:
 		[
 		 	{name: 'idContrato'},
 		 	{name: 'descContrato'}
 		],
 		listeners:
 		{
 			load: function(s, records)
 			{
 				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeContratoTef, msg: "Cargando Contratos TEF... "}); 				
 				if (records.length == null || records.length <= 0)
 					Ext.Msg.alert('SET', 'No existen Contratos de TEF registrados');
 			}
 		}
 	});
 	
 	NS.storeComboPayment = new Ext.data.DirectStore({
 		paramsAsHash: false,
 		root: '',
 		baseParams: {},
 		paramOrder: [],
 		directFn: ConsultaPersonasAction.comboPayment,
 		fields:
 		[
 		 	{name: 'idContratoPayment'},
 		 	{name: 'descContratoPayment'}
 		],
 		listeners:
 		{
 			load: function(s, records)
 			{
 				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboPayment, msg: "Cargando MassPayment"});
 				if (records.length == null || records.length <= 0)
 					Ext.Msg.alert('SET', 'No existen Contratos de MassPayment registrados');
 			}
 		}
 	});
 	
 	NS.storeComboBenef = new Ext.data.DirectStore({
 		paramsAsHash: false,
 		root: '',
 		baseParams: {},
 		paramOrder: ['noEmpresa', 'noBenef', 'descBenef'],
 		directFn: ConsultaPersonasAction.comboBenef,
 		fields:
 		[
 		 	{name: 'noBenef'},
 		 	{name: 'descBenef'}
 		],
 		listeners:
 		{
 			load: function(s, records)
 			{
 				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboBenef, msg: "Cargando Beneficiario"});
 				if (records.length == null || records.length <= 0)
 					Ext.Msg.alert('SET', 'No existen Beneficiarios con estos criterios de busqueda');
 			}
 		}
 	});
 	
 	NS.storeGrupoEgreso = new Ext.data.DirectStore( {
		paramOrder:['idTipoMovto'],
		paramsAsHash: false,
		baseParams:{
		idTipoMovto:'E'			
		},		
		root: '',
		directFn: TraspasosAction.llenarComboGrupo,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		]	
	}); 
 	NS.storeGrupoEgreso.load();
 	
 	NS.storeRubroEgreso = new Ext.data.DirectStore( {	
 		root: '',
 		paramOrder:['idGrupo', 'noEmpresa'],	            
 			paramsAsHash: false,
 			directFn: TraspasosAction.llenarComboRubros,
 			idProperty: 'idRubro',  		
 			fields: [
 				{name: 'idRubro' },
 				{name: 'descRubro'}
 			]	
 	});
 	
	//RadioButton
	NS.optRadioSexo = new Ext.form.RadioGroup({
		id: PF + 'optRadioSexo',
		name: PF + 'optRadioSexo',		
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Masculino', name: 'optSeleccion', inputValue: 0, checked: true,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.sexo = 'M'
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{boxLabel: 'Femenino', name: 'optSeleccion', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.sexo = 'F'
		 					}
		 				}
		 			}
	 			}
		 	}
		]
	});
	
	NS.optExporta = new Ext.form.RadioGroup({
		id: PF + 'optExporta',
		name: PF + 'optExporta',
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'SI', name: 'optExportaSel', inputValue: 0, checked: true,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.exporta = 'S'
		 					}
		 				}
		 			}
 				}
		 	},
		 	{boxLabel: 'NO', name: 'optExportaSel', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.exporta = 'N';
		 					}
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.optConcentradora = new Ext.form.RadioGroup({
		id: PF + 'optConcentradora',
		name: PF + 'optConcentradora',
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'SI', name: 'optConcentradoraSel', inputValue: 0, checked: true,
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							NS.concentradora = 'S'
		 						}
		 					}
		 				}
		 			}
		 	},
		 	{boxLabel: 'NO', name: 'optConcentradoraSel', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.concentradora = 'N'
		 					}
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.cmbTipoPersona = new Ext.form.ComboBox({
		store: NS.storeLlenaComboTipoPersona,
		id: PF + 'cmbTipoPersona',
		name: PF + 'cmbTipoPersona',
		x: 75,
		y: 20,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idTipoPersona',
		displayField: 'descTipoPersona',
		autocomplete: true,
		emptyText: 'Tipo Persona',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtTipoPersona', NS.cmbTipoPersona.getId());
					if (Ext.getCmp(PF + 'txtTipoPersona').getValue()=='P') {
						
						Ext.getCmp(PF + 'excel').setDisabled(true);
						Ext.getCmp(PF + 'crearN').setDisabled(true);
						Ext.getCmp(PF + 'eliminar').setDisabled(true);
						
					} else {
						Ext.getCmp(PF + 'excel').setDisabled(false);
						Ext.getCmp(PF + 'crearN').setDisabled(false);
						Ext.getCmp(PF + 'eliminar').setDisabled(false);
					}
				}
			}
		}
	});
	
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeLlenaComboGrupo,
		id: PF + 'cmbGrupo',
		name: PF + 'cmbGrupo',
		x: 55,
		y: 115,
		//hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo de Flujo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGrupo', NS.cmbGrupo.getId());
				}
			}
		}
	});
	
	NS.cmbActividadEconomica = new Ext.form.ComboBox({
		store: NS.storeActividadEconomica,
		id: PF + 'cmbActividadEconomica',
		name: PF + 'cmbActividadEconomica',
		x: 295,
		y: 115,
		hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idActividadEconomica',
		displayField: 'descActividadEconomica',
		autocomplete: true,
		emptyText: 'Actividad Económica',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdActividadEconomica', NS.cmbActividadEconomica.getId());					
				}
			}
		}
	});
	
	NS.cmbActividadGenerica = new Ext.form.ComboBox({
		store: NS.storeActividadGenerica,	
		id: PF + 'cmbActividadGenerica',
		name: PF + 'cmbActividadGenerica',
		x: 525,
		y: 115,
		hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idActividadGenerica',
		displayField: 'descActividadGenerica',
		autocomplete: true,
		emptyText: 'Actividad Generica',
		triggerAction: 'all',
		value: '',
		visible: false,		
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdActividadGenerica', NS.cmbActividadGenerica.getId());
				}
			}
		}
	});
	
	
	NS.cmbEstadoCivil = new Ext.form.ComboBox({
		store: NS.storeEstadoCivil,
		id: PF + 'cmbEstadoCivil',
		name: PF + 'cmbEstadoCivil',
		x: 525,
		y: 115,
		hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idEstadoCivil',
		displayField: 'descEstadoCivil',
		autocomplete: true,
		emptyText: 'Estado Civil',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdEstadoCivil', NS.cmbEstadoCivil.getId());
				}
			}
		}
	});
	
	NS.cmbGiro = new Ext.form.ComboBox({
		store: NS.storeComboGiro,
		id: PF + 'cmbGiro',
		name: PF + 'cmbGiro',
		x: 755,
		y: 115,		
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGiro',
		displayField: 'descGiro',
		autocomplete: true,
		emptyText: 'Giro',
		triggerAction: 'all',
		value: '',
		hidden: true,
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGiro', NS.cmbGiro.getId());
				}
			}
		}
	});
	
	NS.cmbCaja = new Ext.form.ComboBox({
		store: NS.storeComboCaja,
		id: PF + 'cmbCaja',
		name: PF + 'cmbCaja',
		x: 60,
		y: 165,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idCaja',
		displayField: 'descCaja',
		autocomplete: true,
		emptyText: 'Caja',
		triggerAction: 'all',
		value: '',
		hidden: true,
		listeners: {
			select: {
				fn: function(combo, valor) {
					
					BFwrk.Util.updateComboToTextField(PF + 'txtIdCaja', NS.cmbCaja.getId());
				}
			}
		}
		
	});
		
	NS.cmbCambioPersona = new Ext.form.ComboBox({
		store: NS.storeCambioPersona,
		id: PF + 'cmbCambioPersona',
		name: PF + 'cmbCambioPersona',
		x: 60,
		y: 165,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idCambioPersona',
		displayField: 'descCambioPersona',
		autocomplete: true,
		emptyText: 'Cambio Persona',
		triggerAction: 'all',
		value: '',
		hidden: true,
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtIdCambioPersona', NS.cmbCambioPersona.getId());
					Ext.Msg.confirm('SET', '¿Esta seguro de cambiar el tipo de persona?', function(btn){
						if (btn == 'yes'){
							ConsultaPersonasAction.cambiaTipoPersona(NS.noPersona, function(resCambioPersona, e){
								if (resCambioPersona != '' && resCambioPersona != null && resCambioPersona != undefined)
									Ext.Msg.alert('SET', resCambioPersona);
							});
						}
					});
				}
			}
		}
	});
	
	NS.cmbRiesgo = new Ext.form.ComboBox({
		store: NS.storeRiesgo,
		id: PF + 'cmbRiesgo',
		name: PF + 'cmbRiesgo',
		x: 60,
		y: 165,
		hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idRiesgo',
		displayField: 'descRiesgo',
		autocomplete: true,
		emptyText: 'Riesgo',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdRiesgo', NS.cmbRiesgo.getId());
				}
			}
		}
	});
	
	NS.cmbTamano = new Ext.form.ComboBox({
		store: NS.storeTamano,
		id: PF + 'cmbTamano',
		name: PF + 'cmbTamano',
		x: 295,
		y: 165,
		hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idTamano',
		displayField: 'descTamano',
		autocomplete: true,
		emptyText: 'Tamaño',
		triggerAction: 'all',
		value: '',		
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdTamano', NS.cmbTamano.getId());
				}
			}
		}
	});
	
	NS.cmbCalidad = new Ext.form.ComboBox({
		store: NS.storeCalidad,
		id: PF + 'cmbCalidad',
		name: PF + 'cmbCalidad',
		x: 525,
		y: 165,
		hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idCalidad',
		displayField: 'descCalidad',
		autocomplete: true,
		emptyText:  'Calidad de Empresa',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdCalidad', NS.cmbCalidad.getId());
				}
			}
		}
	});
	
	NS.cmbInmueble = new Ext.form.ComboBox({
		store: NS.storeInmueble,
		id: PF + 'cmbInmueble',
		name: PF + 'cmbInmueble',
		x: 755,
		y: 165,
		hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idInmueble',
		displayField: 'descInmueble',
		autocomplete: true,
		emptyText: 'Tipo de Inmueble',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdInmueble', NS.cmbInmueble.getId());
				}
			}
		}
	});
	
	NS.cmbFormaPagoProv = new Ext.form.ComboBox({
		//store: NS.storeFormaPagoProv,
		id: PF + 'cmbFormaPagoProv',
		name: PF + 'cmbFormaPagoProv',
		x: 310,
		y: 130,
		hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
//		valueField: 'idFormaPagoProv',
//		displayField: 'descFormaPagoProv',
		autocomplete: true,
		emptyText: 'Forma de Pago',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdFormaPagoProv', NS.cmbFormaPagoProv.getId());
				}
			}
		}
	});
		
	NS.cmbPagoReferenciado = new Ext.form.ComboBox({
		store: NS.storePagoReferenciado,
		id: PF + 'cmbPagoReferenciado',
		name: PF + 'cmbPagoReferenciado',		
		x: 0,
		y: 265,
		//hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: false,
		valueField: 'descPagoReferenciado',
		displayField: 'descPagoReferenciado',
		autocomplete: true,
		editable: true,
		emptyText: 'Referencia Persona',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					
				}
			}
		}
	});
		
	
	NS.cmbContratoTef = new Ext.form.ComboBox({
		store: NS.storeComboTef,
		id: PF + 'cmbContratoTef',
		name: PF + 'cmbContratoTef',
		x: 0,
		y: 20,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idContrato',
		displayField: 'descContrato',
		autocomplete: true,
		emptyText: 'Contrato TEF',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{}
			}
		}
	});
	
	NS.cmbContratoPayment = new Ext.form.ComboBox({
		store: NS.storeComboPayment,
		id: PF + 'cmbContratoPayment',
		name: PF + 'cmbContratoPayment',
		x: 250,
		y: 20,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idContratoPayment',
		displayField: 'descContratoPayment',
		autocomplete: true,
		emptyText: 'Contrato MassPayment',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{}
			}
		}
	});
	
	NS.cmbBenef = new Ext.form.ComboBox({
		store: NS.storeComboBenef,
		id: PF + 'cmbBenef',
		name: PF + 'cmbBenef',
		x: 570,
		y: 20,		
		width: 250,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'noBenef',
		displayField: 'descBenef',
		autocomplete: true,
		emptyText: 'Parte del Beneficiario a Buscar',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtNoBenef', NS.cmbBenef.getId());
				}
			}
		}
	});
	
	NS.cmbGrupoRubro = new Ext.form.ComboBox({
		store: NS.storeGrupoEgreso,
		id: PF + 'cmbGrupoRubro',
		name: PF + 'cmbGrupoRubro',
		x: 55,
		y: 20,		
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtGrupoRubro', NS.cmbGrupoRubro.getId());
					Ext.getCmp(NS.txtRubro.getId()).setValue('');
					Ext.getCmp(NS.cmbRubro.getId()).setValue('');
					NS.storeRubroEgreso.removeAll();
					NS.storeRubroEgreso.baseParams.idGrupo = parseInt(NS.txtGrupoRubro.getValue());									
					NS.storeRubroEgreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF + 'txtNoEmpresa').getValue());
					NS.storeRubroEgreso.load();
				}
			},
			change:{
			    fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtGrupoRubro' ).setValue('');
         				Ext.getCmp(NS.txtRubro.getId()).setValue('');
						Ext.getCmp(NS.cmbRubro.getId()).setValue('');
						NS.storeRubroEgreso.removeAll();       				
         			}else{
         				Ext.getCmp( PF + 'txtRubro' ).forceSelection= true; 
         			}	
		    	}//end function
			}//end change
		}
	});
	
	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeRubroEgreso,
		id: PF + 'cmbRubro',
		name: PF + 'cmbRubro',
		x: 305,
		y: 20,
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtRubro', NS.cmbRubro.getId());
				}
			},
			change:{
				fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtRubro' ).setValue('');
					}else{
						Ext.getCmp( PF + 'txtRubro' ).forceSelection= true;
					}
				}//end function
			}//end change
		}
	});
	
	//GRID CONSULTA
	//Columna de seleccion en el grid
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columnas de grid
	NS.columnasGrid = new Ext.grid.ColumnModel([	                                            
	    {header: 'Equivale Persona', width: 100,  dataIndex: 'equivalePersona', sortable: true},
	    {header: 'Nombre', width: 300, dataIndex: 'nombre', sortable: true},
	    {header: 'No Empresa', width: 60, dataIndex: 'noEmpresa', sortable: true},
	    {header: 'Id Persona', width: 60, dataIndex: 'idTipoPersona', sortable: true},
	    {header: 'No Persona', width: 70, dataIndex: 'noPersona', sortable: true},
	    {header: 'Fisica/Moral', width: 70, dataIndex: 'fisicaMoral', sortable: true},
	    {header: 'Id Estatus', width: 30, dataIndex: 'idEstatus', sortable: true, hidden: true},
	    {header: 'Estatus', width: 60, dataIndex: 'estatus', sortable: true, hidden: true}	    
	]);
	
	NS.gridConsulta = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'gridConsulta',
		name: PF + 'gridConsulta',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 960,
		height: 200,

		stripeRows : true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid)
				{
					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
				}
			}
		}
	});
	
	
	//Columna de seleccion en el gridCP
	NS.columnaSeleccionCP = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columnas de gridCP
	NS.columnasGridCP = new Ext.grid.ColumnModel([	                                            
	    {header: 'Banco', width: 100,  dataIndex: 'bancoI', sortable: true},
	    {header: 'Chequera', width: 100, dataIndex: 'chequera', sortable: true},
	    {header: 'Divisa', width: 70, dataIndex: 'divisa', sortable: true},
	    {header: 'Descripcion Chequera', width: 150, dataIndex: 'descChequera', sortable: true},
	    {header: 'Sucursal', width: 100, dataIndex: 'sucursal', sortable: true},
	    {header: 'Plaza', width: 70, dataIndex: 'plaza', sortable: true},
	    {header: 'Clabe', width: 70, dataIndex: 'clabe', sortable: true},
	    {header: 'Swift', width: 100, dataIndex: 'swift', sortable: true },
	    {header: 'Aba', width: 50,  dataIndex: 'aba', sortable: true},
	    {header: 'Bank True', width: 100, dataIndex: 'bankTrue', sortable: true},
	    {header: 'Bank Corresponding', width: 100, dataIndex: 'bankCorresponding', sortable: true},
	    {header: 'Chequera Bank True', width: 150, dataIndex: 'chequeraTrue', sortable: true},
	    {header: 'Banco Anterior', width: 100, dataIndex: 'bancoAnterior', sortable: true},
	    {header: 'Chequera Anterior', width: 150, dataIndex: 'chequeraAnterior', sortable: true},
	    {header: 'Especial', width: 50, dataIndex: 'especial', sortable: true},
	    {header: 'Id Banco', width: 50, dataIndex: 'idBanco', sortable: true, hidden:true}
	    
	]);
	
	NS.gridConsultaCP = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridCP,
		id: PF + 'gridConsultaCP',
		name: PF + 'gridConsultaCP',
		cm: NS.columnasGridCP,
		sm: NS.columnaSeleccionCP,
		width: 960,
		height: 100,

		stripeRows : true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid)
				{
					var registroSeleccionado = NS.gridConsultaCP.getSelectionModel().getSelections();
					Ext.getCmp(PF + 'cmbBancoCP').setValue(registroSeleccionado[0].get("bancoI"));
					Ext.getCmp(PF + 'txtNoBancoCP1').setValue(registroSeleccionado[0].get("idBanco"));
					Ext.getCmp(PF + 'cmbDivisaCP').setValue(registroSeleccionado[0].get("divisa"));
					Ext.getCmp(PF + 'txtChequeraCP1').setValue(registroSeleccionado[0].get("chequera"));
					Ext.getCmp(PF + 'txtDescChequeraCP1').setValue(registroSeleccionado[0].get("descChequera"));
					Ext.getCmp(PF + 'txtSucursalCP1').setValue(registroSeleccionado[0].get("sucursal"));
					Ext.getCmp(PF + 'txtPlazaCP1').setValue(registroSeleccionado[0].get("plaza"));
					Ext.getCmp(PF + 'txtClabeCP1').setValue(registroSeleccionado[0].get("clabe"));
					Ext.getCmp(PF + 'txtBancoAntCP1').setValue(registroSeleccionado[0].get("bancoAnterior"));
					Ext.getCmp(PF + 'txtChequeraAntCP1').setValue(registroSeleccionado[0].get("chequeraAnterior"));
					NS.panelCamposCP12.setVisible(true);
					var a=registroSeleccionado[0].get("aba");
					var b=registroSeleccionado[0].get("swift");
					Ext.getCmp(PF + 'txtAbaSwiftCP12').setDisabled(true);
					Ext.getCmp(PF + 'cmbTipoCambioCP12').setDisabled(true);
					NS.chkSwift.setDisabled(true);
					NS.chkAba.setDisabled(true);
					Ext.getCmp(PF + 'chkBancoExtranjero').setDisabled(true);
					if(a!=null && a!=undefined && a!=""){
						Ext.getCmp(PF + 'txtAbaSwiftCP12').setValue(registroSeleccionado[0].get("aba"));
						NS.chkAba.setValue(true);
					}else{
						if(b!=null && b!=undefined && b!=""){
							Ext.getCmp(PF + 'txtAbaSwiftCP12').setValue(registroSeleccionado[0].get("swift"));
							NS.chkSwift.setValue(true);
						}
					}
					
//					NS.chkDetalle = new Ext.form.Checkbox({
//						boxLabel: 'Detalle',
//				        id: PF+'chkDetalle',
//				        name: PF+'chkDetalle',
//				        hidden: true,
//				        value: false,
//				        /*x: 10,
//				        y: 340,*/
//				        x: 0,
//				    	y:84
//					});
				}
			}
		}
	});
	
	
	//PANEL
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: 'Busqueda',
		x: 0,
		y: 0,
		width: 985,
		height: 160,
		layout: 'absolute',
		items: 
		[
		 	NS.lblEstatus,
		 	NS.lblTipoPersona,
		 	NS.lblNoPersona,
		 	NS.lblRazonSocial,
		 	NS.lblPaterno,
		 	NS.lblMaterno,
		 	NS.lblNombre,
		 	NS.lblO,	
		 	NS.txtNoPersona,
		 	NS.txtRazonSocial,
		 	NS.txtPaterno,
		 	NS.txtMaterno,
		 	NS.txtNombre,
		 	NS.cmbTipoPersona,
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkInactivas',
		 		name: PF + 'chkInactivas',
		 		x: 400,

		 		y: 15,
		 		boxLabel: 'Personas o Empresas inactivas',
		 		listeners: {
		 			check: {
		 				fn: function(checkBox, valor) {
		 						
		 				}
		 			}
		 		}
		 	},		 	
		 	{
		 		xtype: 'uppertextfield',
		 		id: PF + 'txtTipoPersona',
		 		name: PF + 'txtTipoPersona',
		 		x: 0,
		 		y: 20,

		 		width: 65,
		 		listeners: {
		 			change: {
		 				fn: function(caja, valor){
		 					if (caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
		 						BFwrk.Util.updateTextFieldToCombo(PF + 'txtTipoPersona', NS.cmbTipoPersona.getId());
		 				}
		 			}
		 		}	 	
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		id: PF + 'buscar',
		 		name: PF + 'buscar',
		 		x: 700,
		 		y: 15,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {			
		 						NS.buscar();
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelPAC = new Ext.form.FieldSet({
		id: PF + 'panelPAC',
		name: PF + 'panelPAC',
		title: '',
		x: 0,
		y: 300,
		hidden: true,
		width: 500,
		height: 40,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'checkbox',
		 		boxLabel: 'Proveedor',
		 		id: PF + 'chkProveedor',
		 		name: PF + 'chkProveedor',
		 		x: 0,
		 		y: 0,
		 		width: 80,
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'checkbox',
		 		boxLabel: 'Asociación',
		 		id: PF + 'chkAsociacion',
		 		name: PF + 'chkAsociacion',
		 		x: 150,
		 		y: 0,
		 		width: 80,
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'checkbox',
		 		boxLabel: 'Contrato de Inversion',
		 		id: PF + 'chkContratoInversion',
		 		name: PF + 'chkContratoInversion',
		 		x: 300,
		 		y: 0,
		 		width: 150,
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelSexo = new Ext.form.FieldSet({
		id: PF + 'panelSexo',
		name: PF + 'panelSexo',
		title: 'Sexo:',
		x: 0,
		y: 100,
		hidden: true,
		width: 200,
		height: 40,
		layout: 'absolute',		
		items:
		[
		 	NS.optRadioSexo
		]
	});
	
	NS.panelExporta = new Ext.form.FieldSet({
		id: PF + 'panelExporta',
		name: PF + 'panelExporta', 
		title: 'Exporta',
		x: 350,
		y: 200,
		hidden: true,
		width: 150,
		height: 40,
		layout: 'absolute',
		items:
		[
		 	NS.optExporta
		]
	});
	
	NS.panelConcentradora = new Ext.form.FieldSet({
		id: PF + 'panelConcentradora',
		name: PF + 'panelConcentradora',
		title: 'Concentradora de Inversion',
		x: 510,
		y: 200,
		hidden: true,
		width: 200,
		height: 40,
		layout: 'absolute',
		items:
		[
		 	NS.optConcentradora
		]
	});
	
	NS.panelTef = new Ext.form.FieldSet({
		id: PF + 'panelTef',
		name: PF + 'panelTef',
		title: '',
		x: 0,
		y: 265,
		width: 950,
		height: 90,
		hidden: true,
		layout: 'absolute',		
		items:
		[				
		 	NS.lblContratoTef,
		 	NS.lblContratoPayment,
		 	NS.lblBeneficiario,
		 	NS.cmbContratoTef,
		 	NS.cmbContratoPayment,		 	
		 	NS.txtNoBenef,
		 	{
		 		xtype: 'button',
		 		text: 'Buscaar',
		 		x: 845,
		 		y: 20,
		 		width: 60,
		 		height: 10,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					if (Ext.getCmp(PF + 'txtNoEmpresa').getValue() == '')
		 						NS.noEmpresa = 552;
		 					else
		 						NS.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
		 					
		 					if (Ext.getCmp(PF + 'txtNoBenef').getValue() == '' && Ext.getCmp(PF + 'cmbBenef').getValue() == '')
		 						Ext.Msg.alert('SET', 'Debe de escribir al menos el Número de persona o una parte de la Descripcion');		 					
		 					else{
		 						if (Ext.getCmp(PF + 'txtNoBenef').getValue() != '')
		 							NS.noBenef = Ext.getCmp(PF + 'txtNoBenef').getValue();
		 						
		 						if (Ext.getCmp(PF + 'cmbBenef').getValue() != '')
		 							NS.descBenef = Ext.getCmp(PF + 'cmbBenef').getValue();
		 						
		 						NS.storeComboBenef.baseParams.noEmpresa = parseInt(NS.noEmpresa); 
		 						NS.storeComboBenef.baseParams.noBenef = NS.noBenef;
		 						NS.storeComboBenef.baseParams.descBenef = NS.descBenef;
		 						NS.storeComboBenef.load()
		 					}
		 				}
		 			}
		 		}
		 	},
		 	NS.cmbBenef
		]
	});

	NS.panelGpoRubro = new Ext.form.FieldSet({
		id: PF + 'panelGpoRubro',
		name: PF + 'panelGpoRubro',
		title: '',
		x: 0,
		y: 360,
		width: 510,
		height: 70,
		hidden: true,
		layout: 'absolute',
		items:
		[
		 	NS.lblGrupoRubro,
		 	NS.lblRubro,
		 	NS.txtGrupoRubro,
		 	NS.txtRubro,
		 	NS.cmbGrupo,
		 	NS.cmbGrupoRubro,
		 	NS.cmbRubro,
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkDivision',
		 		name: PF + 'chkDivision',
		 		x: 750,
		 		y: 200,
		 		hidden: true,
		 		boxLabel: 'División Empresarial',
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		}
		 	}
		]
	});
	NS.CuentasProvedor=apps.SET.btnFacultativo(new Ext.Button({
		text: 'Cuentas de Proveedores',
 		id: PF + 'ctasBanco',
 		name: PF + 'ctasBanco',
 		x: 480, 
 		y: 5,
 		width: 80,
 		height: 22,
 		listeners:
 		{
 			click:
 			{
 				fn: function(e)
 				{
 					if (NS.fisicaMoral == 'F'){		 						
 						Ext.getCmp(PF + 'txtRazonSocialCuentas').setValue(Ext.getCmp(PF + 'txtPaternoGral').getValue() + " " + 
 																		  Ext.getCmp(PF + 'txtMaternoGral').getValue() + " " + 
 																		  Ext.getCmp(PF + 'txtNombreGral').getValue());		 							 						
 					}
 					else{
 						Ext.getCmp(PF + 'txtRazonSocialCuentas').setValue(Ext.getCmp(PF + 'txtRazonSocialGral').getValue());		 						
 					}
 					
 					Ext.getCmp(PF + 'txtNoPersonaCuentas').setValue(Ext.getCmp(PF + 'txtNoPersonaGral').getValue());
 					
 					if(Ext.getCmp(PF + 'txtNoEmpresa').getValue() != '' && NS.tipoPersona == 'P')
 						Ext.getCmp(PF + 'txtEmpresaCuentas').setValue(NS.storeLlenaComboEmpresa.getById(parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue())).get('nomEmpresa'));
 					else
 						Ext.getCmp(PF + 'txtEmpresaCuentas').setValue(apps.SET.NOM_EMPRESA);
 						
			 		NS.panelCampos.setVisible(false);
					NS.panelBotones.setVisible(false);
 					NS.panelCuentasBancarias.setVisible(true);
 					Ext.getCmp(PF + 'aceptar').setVisible(true);
 					Ext.getCmp(PF + 'cancelar').setVisible(true);
 					Ext.getCmp(PF + 'imprimir').setVisible(true);
 					Ext.getCmp(PF + 'exportar').setVisible(true);
 					Ext.getCmp(PF + 'regresar').setVisible(true);				
 					NS.storeGridCuentasProveedor.baseParams.noPersona = NS.noPersona,
 					NS.storeGridCuentasProveedor.baseParams.noEmpresa = NS.noEmpresa,
 					NS.storeGridCuentasProveedor.baseParams.tipoPersona = NS.tipoPersona,
 					NS.storeGridCuentasProveedor.load();
 					NS.storeComboDivisaCuentas.load();		 					
 				}
 			}
 		}
	}));
	
	NS.panelBotonesFI = new Ext.form.FieldSet({
		id: PF + 'panelBotonesFI',
		name: PF + 'panelBotonesFI',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();
		 				}
		 			}
		 		}
		 	},
		 	
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Medios de Contacto',
		 		x: 355,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelCamposFI.setVisible(false);
		 					NS.panelBotonesFI.setVisible(false);
		 					NS.panelCamposMC.setVisible(true);
		 					NS.panelBotonesMCFI.setVisible(true);
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Direcciones',
		 		x: 730,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelCamposFI.setVisible(false);
		 					NS.panelBotonesFI.setVisible(false);
		 					NS.panelCamposDI.setVisible(true);
		 					NS.panelBotonesDIFI.setVisible(true);
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
							NS.panelCamposFI.setVisible(false);
		 					NS.panelBotonesFI.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesMOE = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMOE',
		name: PF + 'panelBotonesMOE',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		x: 680,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					
		 					NS.modificar = true;
		 					if (NS.validaVaciosE()) {
		 						
		 						NS.insertarActualizarE();
							}
		 					
//		 					
		 					
		 				}
		 			}
		 		}
		 	},
		 	
		 	
		/* 	{
		 		xtype: 'button',
		 		text: 'Medios de Contacto',
		 		x: 240,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					
		 					NS.panelBusqueda.setVisible(false);
		 					NS.panelGrid.setVisible(false);
		 					NS.panelCamposE.setVisible(false);
		 					NS.panelBotonesE.setVisible(false);
		 					NS.panelCamposMC.setVisible(true);
		 					NS.panelBotonesMCE.setVisible(true);
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 400,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},*/
		 	{
		 		xtype: 'button',
		 		text: 'Direcciones',
		 		x: 770,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelCamposE.setVisible(false);
		 					NS.panelBotonesMOE.setVisible(false);
		 					NS.panelBotonesE.setVisible(false);
		 					//NS.panelCamposDI.setVisible(true);
		 					NS.panelCamposDI.setVisible(true);
		 					NS.panelBotonesDIM.setVisible(true);
		 					NS.llenaCamposDI();
		 					NS.llenaDatosDI();
		 					
		 					//NS.llenaCamposDI();
		 					NS.storeTipoDireccion.load();
		 					NS.storeEstado.load();
		 					NS.storePais.load();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarE();
		 					NS.cierraVentana();
							NS.panelCamposE.setVisible(false);
		 					NS.panelBotonesMOE.setVisible(false);
		 					
		 					
		 					NS.panelBotonesE.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	
	
	NS.panelBotonesE = new Ext.form.FieldSet({
		id: PF + 'panelBotonesE',
		name: PF + 'panelBotonesE',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 770,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					
		 					NS.modificar == false;
		 					if (NS.validaVaciosE()) {
		 						NS.insertarActualizarE();
							}
		 					
//		 					
		 					
		 				}
		 			}
		 		}
		 	},
		 	
		 	
		 	/*{
		 		xtype: 'button',
		 		text: 'Medios de Contacto',
		 		x: 240,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					
		 					NS.panelBusqueda.setVisible(false);
		 					NS.panelGrid.setVisible(false);
		 					NS.panelCamposE.setVisible(false);
		 					NS.panelBotonesE.setVisible(false);
		 					NS.panelCamposMC.setVisible(true);
		 					NS.panelBotonesMCE.setVisible(true);
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 400,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},*/
		 /*	{
		 		xtype: 'button',
		 		text: 'Direcciones',
		 		x: 770,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelCamposE.setVisible(false);
		 					NS.panelBotonesE.setVisible(false);
		 					NS.panelCamposDI.setVisible(true);
		 					NS.HabilitarCamposDI();
		 					NS.panelBotonesDI.setVisible(true);
		 					NS.storeTipoDireccion.load();
		 					NS.storeEstado.load();
		 					NS.storePais.load();
		 				}
		 			}
		 		}
		 	},*/
		 	{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarE();
		 					NS.cierraVentana();
							NS.panelCamposE.setVisible(false);
		 					NS.panelBotonesE.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesIB = new Ext.form.FieldSet({
		id: PF + 'panelBotonesIB',
		name: PF + 'panelBotonesIB',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 680,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					if (NS.validaVaciosIB()) {
		 						NS.insertarActualizarIB();
							}
		 					
		 					
		 					//NS.insertarActualizar();
		 				}
		 			}
		 		}
		 	},
		 	
		 /*	{
		 		xtype: 'button',
		 		text: 'Medios de Contacto',
		 		x: 327,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelCamposIB.setVisible(false);
		 					NS.panelBotonesIB.setVisible(false);
		 					NS.panelCamposMC.setVisible(true);
		 					NS.panelBotonesMCIB.setVisible(true);
		 					NS.panelBotonesMCIB
		 				}
		 				}
		 			}
		 		
		 	},*/
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 770,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				
		 				fn: function(e)
		 				{
		 					NS.limpiarIB();
		 					NS.cierraVentana();
		 					NS.panelCamposIB.setVisible(false);
		 					NS.panelBotonesIB.setVisible(false);
		 					NS.panelBusqueda.setVisible(true);
		 					NS.panelGrid.setVisible(true);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	
	NS.panelBotonesMIB = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMIB',
		name: PF + 'panelBotonesMIB',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		x: 590,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificar = true;
		 					if (NS.validaVaciosIB()) {
		 						NS.insertarActualizarIB();
		 					}
		 					//NS.insertarActualizar();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Direcciones',
		 		x: 680,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					
		 					NS.panelCamposIB.setVisible(false);
		 					NS.panelBotonesMIB.setVisible(false);
		 					NS.panelCamposDI.setVisible(true);
		 					//NS.panelBotonesDIM.setVisible(true);
		 					//NS.DesabilitarCamposDI();
		 					NS.llenaCamposDIIB();
		 					NS.llenaDatosDI();
		 					
		 					//NS.llenaCamposDI();
		 					NS.storeTipoDireccion.load();
		 					NS.storeEstado.load();
		 					NS.storePais.load();
		 				}
		 			}
		 		}
		 	},
		 /*	{
		 		xtype: 'button',
		 		text: 'Medios de Contacto',
		 		x: 327,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelCamposIB.setVisible(false);
		 					NS.panelBotonesIB.setVisible(false);
		 					NS.panelCamposMC.setVisible(true);
		 					NS.panelBotonesMCIB.setVisible(true);
		 					NS.panelBotonesMCIB
		 				}
		 				}
		 			}
		 		
		 	},*/
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 770,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				
		 				fn: function(e)
		 				{
		 					NS.limpiarIB();
		 					NS.cierraVentana();
		 					NS.panelCamposIB.setVisible(false);
		 					NS.panelBotonesMIB.setVisible(false);
		 					NS.panelBusqueda.setVisible(true);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesCF = new Ext.form.FieldSet({
		id: PF + 'panelBotonesCF',
		name: PF + 'panelBotonesCF',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		hidden: true,
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizarCF();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 					NS.panelCamposCF.setVisible(false);
		 					NS.panelBotonesCF.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelBotonesCM = new Ext.form.FieldSet({
		id: PF + 'panelBotonesCM',
		name: PF + 'panelBotonesCM',
		x: 10,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();



		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 					NS.panelCamposCM.setVisible(false);
		 					NS.panelBotonesCM.setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	//Termina Panel Botones PRoveedores 
	NS.panelBotones = new Ext.form.FieldSet({
		id: PF + 'panelBotones',
		name: PF + 'panelBotones',
		x: 0,
		y: 370,
		width: 950,
		height: 55,
		layout: 'absolute',
		hidden: true,
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 30,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificarCuentas == false;
		 					NS.insertarActualizar();

		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Valores Empresa',
		 		x: 130,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Actualizar Partida',
		 		x: 240,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Medios de Contacto',
		 		x: 355,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{}
		 			}
		 		}
		 	},
		 	NS.CuentasProvedor,
		 	{
		 		xtype: 'button',
		 		text: 'Relaciones',
		 		x: 630,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
				 			NS.funBuscar();
				 			NS.global.setVisible(false);
		 					NS.fieldRelaciones.setVisible(true);
					 	}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Direcciones',
		 		x: 730,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 830,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiarCamposNuevos();
		 					NS.cierraVentana();
		 				}
		 			}
		 		}
		 	}
		]
	});
	//RadioButton
	NS.optRadioSexoF = new Ext.form.RadioGroup({
		id: PF + 'optRadioSexoF',
		name: PF + 'optRadioSexoF',		
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Masculino', name: 'optSeleccion', inputValue: 0, checked: true,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.sexo = 'M'
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{boxLabel: 'Femenino', name: 'optSeleccion', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.sexo = 'F'
		 					}
		 				}
		 			}
	 			}
		 	}
		]
	});

	NS.panelSexoF = new Ext.form.FieldSet({
		id: PF + 'panelSexoF',
		name: PF + 'panelSexoF',
		title: 'Sexo:',
		x: 0,
		y: 100,
		width: 200,
		height: 40,
		layout: 'absolute',		
		items:
		[
		 	NS.optRadioSexoF
		]
	});
	
	NS.panelExportaE = new Ext.form.FieldSet({
		id: PF + 'panelExportaE',
		name: PF + 'panelExportaE', 
		title: 'Exporta',
		x: 350,
		y: 200,
		width: 150,
		height: 40,
		layout: 'absolute',
		items:
		[
		 	NS.optExportaE
		]
	});

	NS.panelConcentradoraE = new Ext.form.FieldSet({
		id: PF + 'panelConcentradoraE',
		name: PF + 'panelConcentradoraE',
		title: 'Concentradora de Inversion',
		x: 410,//PAO
		y: 103,
		width: 200,
		height: 40,
		layout: 'absolute',
		items:
		[
		 	NS.optConcentradoraE
		]
	});
	
	NS.panelPACE = new Ext.form.FieldSet({
		id: PF + 'panelPACE',
		name: PF + 'panelPACE',
		title: '',
		x: 0,
		y: 300,
		width: 500,
		height: 40,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'checkbox',
		 		boxLabel: 'Proveedor',
		 		id: PF + 'chkProveedor',
		 		name: PF + 'chkProveedor',
		 		x: 0,
		 		y: 0,
		 		width: 80,
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'checkbox',
		 		boxLabel: 'Asociación',
		 		id: PF + 'chkAsociacion',
		 		name: PF + 'chkAsociacion',
		 		x: 150,
		 		y: 0,
		 		width: 80,
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'checkbox',
		 		boxLabel: 'Contrato de Inversion',
		 		id: PF + 'chkContratoInversion',
		 		name: PF + 'chkContratoInversion',
		 		x: 300,
		 		y: 0,
		 		width: 150,
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		}
		 	}
		]
	});

	
	NS.panelGpoRubroE = new Ext.form.FieldSet({
		id: PF + 'panelGpoRubroE',
		name: PF + 'panelGpoRubroE',
		title: '',
		x: 0,
		y: 360,
		width: 510,
		height: 70,
		layout: 'absolute',
		items:
		[
		 	NS.lblGrupoRubroE,
		 	NS.lblRubroE,
		 	NS.txtGrupoRubroE,
		 	NS.txtRubroE,
		 	NS.cmbGrupoE,
		 	NS.cmbGrupoRubroE,
		 	NS.cmbRubroE,
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkDivision',
		 		name: PF + 'chkDivision',
		 		x: 750,
		 		y: 200,
		 		hidden: true,
		 		boxLabel: 'División Empresarial',
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelTefE = new Ext.form.FieldSet({
		id: PF + 'panelTefE',
		name: PF + 'panelTefE',
		title: '',
		x: 0,
		y: 265,
		width: 950,
		height: 90,
		layout: 'absolute',		
		items:
		[				
		 	NS.lblContratoTefE,
		 	NS.lblContratoPaymentE,
		 	NS.lblBeneficiarioE,
		 	NS.cmbContratoTefE,
		 	NS.cmbContratoPaymentE,		 	
		 	NS.txtNoBenefE,
		 	{
		 		xtype: 'button',
		 		text: 'Buscaar',
		 		x: 845,
		 		y: 20,
		 		width: 60,
		 		height: 10,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					if (Ext.getCmp(PF + 'txtNoEmpresaE').getValue() == '')
		 						NS.noEmpresa = 552;
		 					else
		 						NS.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresaE').getValue();
		 					
		 					if (Ext.getCmp(PF + 'txtNoBenefE').getValue() == '' && Ext.getCmp(PF + 'cmbBenefE').getValue() == '')
		 						Ext.Msg.alert('SET', 'Debe de escribir al menos el Número de persona o una parte de la Descripcion');		 					
		 					else{
		 						if (Ext.getCmp(PF + 'txtNoBenefE').getValue() != '')
		 							NS.noBenef = Ext.getCmp(PF + 'txtNoBenefE').getValue();
		 						
		 						if (Ext.getCmp(PF + 'cmbBenefE').getValue() != '')
		 							NS.descBenef = Ext.getCmp(PF + 'cmbBenefE').getValue();
		 					}
		 				}
		 			}
		 		}
		 	},

		NS.cmbBenefE
		]
	});
	NS.panelGridMedios = new Ext.form.FieldSet({
		title: '',
		x: 250,
		y: 120,
		width: 485,
		height: 150,

		layout: 'absolute',
		items:
		[
		 	
		 	NS.gridConsultaMedios,
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		id: PF + 'Eliminar',
		 		name: PF + 'Eliminar',
		 		x: 0,
		 		y: 105,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					var registroSeleccionado = NS.gridConsultaMedios.getSelectionModel().getSelections();
		 					if (registroSeleccionado.length <= 0)
		 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
		 					else{
		 						var matrizElimi= new Array();
	                			var recordsEliminar=NS.gridConsultaMedios.getSelectionModel().getSelections();
	                			var regElimi={};
	                			if(recordsEliminar.length>1){
	                				Ext.Msg.alert('Información SET','Solo puede eliminar un registro a la vez');
	                			}else{
	                				Ext.Msg.confirm('Información SET','¿Esta seguro de eliminar este medio de contacto?',function(btn){  
		                				if(btn === 'yes'){
		                					var correo=NS.gridConsultaMedios.getSelectionModel().getSelections()[0].get('contactoMedio');
		                					var em=Ext.getCmp(PF + 'txtEmpresaPM').getValue();
		                					ConsultaPersonasAction.eliminaMedioContacto(em,correo, function(result, e){
												if(result===0){
													Ext.Msg.alert('Información SET','No se eliminaron los medios de contacto');
												}else{
													Ext.Msg.alert('Información SET',result);	
													for (var c=0; c<recordsEliminar.length;c=c+1){
						                				NS.gridConsultaMedios.store.remove(recordsEliminar[c]);
						                				NS.storeLlenaGridMedios.load();
						                				NS.gridConsultaMedios.getView().refresh();
						                			}
												}
		                					});
		                				}
									})
									
	                			}
	                		}
		 				}
		 			}
		 			
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Nuevo',
		 		id: PF + 'nuevo',
		 		name: PF + 'nuevo',
		 		x: 90,
		 		y: 105,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.txtCorreoMedios.setDisabled(false);
		 				}
		 			}
		 		}
		 	},
		 	NS.txtCorreoMedios,
		 	{
		 		xtype: 'button',
		 		text: 'OK.',
		 		id: PF + 'ok',
		 		name: PF + 'ok',
		 		x: 410,
		 		y: 105,
		 		width: 30,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					if(Ext.getCmp(PF + 'txtCorreoMedios').getValue()!="" && Ext.getCmp(PF + 'txtCorreoMedios').getValue()!=undefined && Ext.getCmp(PF + 'txtCorreoMedios').getValue()!=null  ){
		 						ConsultaPersonasAction.guardaNuevoMedioContacto(Ext.getCmp(PF + 'txtEmpresaPM').getValue(),Ext.getCmp(PF+'txtCorreoMedios').getValue(),NS.empre1, function(resultado, e){
		 							if (resultado != '' && resultado != 0 && resultado != undefined)
		 								Ext.Msg.alert('SET', resultado);
		 							else if (resultado == ''){
		 								NS.storeLlenaGridMedios.load();
		 								NS.gridConsultaMedios.getView().refresh();
		 								Ext.getCmp(PF+'txtCorreoMedios').setValue("");
		 								NS.txtCorreoMedios.setDisabled(true);
		 			 					
		 							}
		 						});
		 					}else{
		 						Ext.Msg.alert('SET', 'Ingrese un correo en la caja de texto.');
		 					}
		 				}
		 			}
		 		}
		 	}
		 	
		 	
		 	
		]
	});
	//PanelCampos Instituciones Bancarias////////////
	NS.panelCamposPM = new Ext.form.FieldSet({
			id: PF + 'panelCamposPM',
			name: PF + 'panelCamposPM',
			title: 'Proveedores Morales',
			x: 0,
			y: 0,
			width: 985,
			height: 370,
			layout: 'absolute',
			items:
			[
			 	//NS.lblEmpresaPM,
			 	NS.lblReferenciaPM,
			 	NS.lblEquivalePerPM,
			 	NS.lblNoPersonaPM,
			 	NS.lblRazonSocialPM,
			 	NS.lblNombreCortoPM,
			 	NS.lblRFCPM,
			 	NS.lblFechaIngresoPM,
//			 	NS.lblFormaPagoProvPM,
				NS.txtEmpresaPM,
				NS.txtEquivalePerPM,
				NS.txtReferenciaPM,
//			 	NS.txtNoPersonaPM,
			 	NS.txtRazonSocialPM,
			 	NS.txtNombreCortoPM,		 	
			 	NS.txtRFCPM,
			 	NS.txtFechaIngresoPM,
			 	NS.panelGridMedios,
//			 	NS.txtIdFormaPagoProvPM,
//			 	NS.cmbFormaPagoProvPM,

				/*{
			 		xtype: 'checkbox',
			 		boxLabel: 'Proveedor de Servicios Básicos',
			 		id: PF + 'chkProveedorServiciosBasicosPM',
			 		name: PF + 'chkProveedorServiciosBasicosPM',
			 		x: 0,
			 		y: 130,
			 		listeners:
			 		{
			 			check:
			 			{
			 				fn: function(checkbox, valor)
			 				{}
			 			}
			 		} 		
			 	},
			 	{
			 		xtype: 'checkbox',
			 		boxLabel: 'Reactivar Persona',
			 		id: PF + 'chkReactivarPersonaPM',
			 		name: PF + 'chkReactivarPersonaPM',
			 		x: 200,
			 		y: 130,
			 		listeners:
			 		{
			 			check:
			 			{
			 				fn: function(checkbox, valor)
			 				{
			 					if (valor == true)
			 						NS.estatus = "A";
			 				}
			 			}
			 		}
			 	},*/
			 	
			]
	
	});
	
	////////////////////////////fin panel proveedores morales //////////////////////////////////

	//////////////////////////////Panel Campos Casa de Cambio//////////////////////////////////
	//PanelCampos Casa de Cambio////////////
	NS.panelCamposK = new Ext.form.FieldSet({
			id: PF + 'panelCamposK',
			name: PF + 'panelCamposK',
			title: 'Casa de Cambio',
			x: 0,
			y: 0,
			width: 985,
			height: 370,
			layout: 'absolute',
			items:
			[
			 	//NS.lblEmpresaK,
			 	NS.lblNoPersonaK,
			 	NS.lblRazonSocialK,
			 	NS.lblNombreCortoK,
			 	NS.lblRFCK,
			 	NS.lblFechaIngresoK,
				//NS.txtEmpresaK,
			 	NS.txtNoPersonaK,
			 	NS.txtRazonSocialK,
			 	NS.txtNombreCortoK,		 	
			 	NS.txtRFCK,
			 	NS.txtFechaIngresoK,
			 	
			]
	});
	
	//PanelCampos Casa de Cambio Cuenta de Proveedores////////////
	NS.panelCamposCP = new Ext.form.FieldSet({
			id: PF + 'panelCamposCP',
			name: PF + 'panelCamposCP',
			title: 'Cuenta de Proveedores',
			x: 0,
			y: 0,
			width: 985,
			height: 130,
			layout: 'absolute',
			items:
			[
			 	NS.lblEmpresaCP,
			 	NS.lblNoPersonaCP,
			 	NS.lblRazonSocialCP,
			 
				NS.txtEmpresaCP,
			 	NS.txtNoPersonaCP,
			 	NS.txtRazonSocialCP,
//			
			 	
			]
	});
	
	NS.panelCamposGrid = new Ext.form.FieldSet({
		id: PF + 'panelCamposGrid',
		name: PF + 'panelCamposGrid',
		title: '',
		x: 0,
		y: 140,
		width: 985,
		height: 150,
		layout: 'absolute',
		items:
		[
		 	NS.gridConsultaCP,
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		x: 680,
		 		y: 105,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					var registroSeleccionado = NS.gridConsultaCP.getSelectionModel().getSelections();
		 					
		 					if (registroSeleccionado.length <= 0)
		 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
		 					else{
		 						if(Ext.getCmp(PF + 'txtNoBancoCP1').getValue()!=""){
			 						Ext.getCmp(PF + 'cmbBancoCP').setDisabled(false);
									Ext.getCmp(PF + 'cmbDivisaCP').setDisabled(false);
									Ext.getCmp(PF + 'txtNoBancoCP1').setDisabled(false);
									Ext.getCmp(PF + 'txtChequeraCP1').setDisabled(false);
									Ext.getCmp(PF + 'txtDescChequeraCP1').setDisabled(false);
									Ext.getCmp(PF + 'txtSucursalCP1').setDisabled(false);
									Ext.getCmp(PF + 'txtPlazaCP1').setDisabled(false);
									Ext.getCmp(PF + 'txtClabeCP1').setDisabled(false);
									Ext.getCmp(PF + 'txtBancoAntCP1').setDisabled(false);
									Ext.getCmp(PF + 'txtChequeraAntCP1').setDisabled(false);
									NS.chkSwift.setDisabled(false);
									NS.chkAba.setDisabled(false);
									Ext.getCmp(PF + 'txtAbaSwiftCP12').setDisabled(false);
									Ext.getCmp(PF + 'cmbTipoCambioCP12').setDisabled(false);
									NS.chkBancoNacional.setDisabled(false);
									NS.chkBancoExtranjero.setDisabled(false);
		 						}
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 780,
		 		y: 105,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 880,
		 		y: 105,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 				}
		 			}
		 		}
		 	}
 	
		]
	});
	NS.panelCamposCP12 = new Ext.form.FieldSet({
		id: PF + 'panelCamposCP12',
		name: PF + 'panelCamposCP12',
		title: 'Beneficiario',
		x: 700,
		y: 0,
		width: 250,
		height: 120,
		layout: 'absolute',
		items:
		[
		 	NS.txtAbaSwiftCP12,
		 	NS.lblTipoCambioCP12,
		 	NS.cmbTipoCambioCP12,
			NS.chkSwift,
			NS.chkAba
		
		 
			
		 		
		 		
		 	
		 	
 	
		]
	});
	NS.panelCamposCP1 = new Ext.form.FieldSet({
		id: PF + 'panelCamposCP1',
		name: PF + 'panelCamposCP1',
		title: '',
		x: 0,
		y: 300,
		width: 985,
		height: 300,
		layout: 'absolute',
		items:
		[
		 	NS.chkBancoExtranjero,
		 	NS.chkBancoNacional,
		 	NS.lblListaCP1,
		 	NS.lblBancoCP1,
		 	NS.txtNoBancoCP1,
		 	NS.cmbBancoCP,
		 	NS.lblDivisaCP1,
		 	NS.cmbDivisaCP,
		 	NS.lblChequeraCP1,
		 	NS.txtChequeraCP1,
		 	NS.lblDescChequeraCP1,
		 	NS.txtDescChequeraCP1,
		 	NS.txtSucursalCP1,
		 	NS.lblSucursalCP1,
		 	NS.lblPlazaCP1,
		 	NS.txtPlazaCP1,
		 	NS.lblClabeCP1,
		 	NS.txtClabeCP1,
		 	NS.lblBancoAntCP1,
		 	NS.txtBancoAntCP1,
		 	NS.lblChequeraAntCP1,
		 	NS.txtChequeraAntCP1,
		 	NS.panelCamposCP12.setVisible(false),
		 	{
		 		xtype: 'button',
		 		text: 'Asignar Cuentas',
		 		x: 0,
		 		y: 150,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 				
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 480,
		 		y: 150,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 				
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 580,
		 		y: 150,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					Ext.getCmp(PF + 'cmbBancoCP').setValue("");
							Ext.getCmp(PF + 'cmbDivisaCP').setValue("");
							Ext.getCmp(PF + 'txtNoBancoCP1').setValue("");
							Ext.getCmp(PF + 'txtChequeraCP1').setValue("");
							Ext.getCmp(PF + 'txtDescChequeraCP1').setValue("");
							Ext.getCmp(PF + 'txtSucursalCP1').setValue("");
							Ext.getCmp(PF + 'txtPlazaCP1').setValue("");
							Ext.getCmp(PF + 'txtClabeCP1').setValue("");
							Ext.getCmp(PF + 'txtBancoAntCP1').setValue("");
							Ext.getCmp(PF + 'txtChequeraAntCP1').setValue("");
		 					
		 					Ext.getCmp(PF + 'cmbBancoCP').setDisabled(true);
							Ext.getCmp(PF + 'cmbDivisaCP').setDisabled(true);
							Ext.getCmp(PF + 'txtNoBancoCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtChequeraCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtDescChequeraCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtSucursalCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtPlazaCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtClabeCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtBancoAntCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtChequeraAntCP1').setDisabled(true);
							NS.panelCamposCP12.setVisible(false);
							NS.chkBancoNacional.setDisabled(true);
							NS.chkBancoExtranjero.setDisabled(true);
		 				
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 680,
		 		y: 150,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 				
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Exportar',
		 		x: 780,
		 		y: 150,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		x: 880,
		 		y: 150,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiarK();
		 					NS.cierraVentana();
		 					NS.panelCamposCP.setVisible(false);
		 					NS.panelCamposCP1.setVisible(false);
		 					NS.panelCamposGrid.setVisible(false);
		 					Ext.getCmp(PF + 'cmbBancoCP').setValue("");
							Ext.getCmp(PF + 'cmbDivisaCP').setValue("");
							Ext.getCmp(PF + 'txtNoBancoCP1').setValue("");
							Ext.getCmp(PF + 'txtChequeraCP1').setValue("");
							Ext.getCmp(PF + 'txtDescChequeraCP1').setValue("");
							Ext.getCmp(PF + 'txtSucursalCP1').setValue("");
							Ext.getCmp(PF + 'txtPlazaCP1').setValue("");
							Ext.getCmp(PF + 'txtClabeCP1').setValue("");
							Ext.getCmp(PF + 'txtBancoAntCP1').setValue("");
							Ext.getCmp(PF + 'txtChequeraAntCP1').setValue("");
		 					
		 					Ext.getCmp(PF + 'cmbBancoCP').setDisabled(true);
							Ext.getCmp(PF + 'cmbDivisaCP').setDisabled(true);
							Ext.getCmp(PF + 'txtNoBancoCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtChequeraCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtDescChequeraCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtSucursalCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtPlazaCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtClabeCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtBancoAntCP1').setDisabled(true);
							Ext.getCmp(PF + 'txtChequeraAntCP1').setDisabled(true);
							NS.panelCamposCP12.setVisible(false);
		 				}
		 			}
		 		}
		 	}
 	
		]
	});
	


	////////////////////////////Fin panel Casa de cambio/////////////////////////////

//PanelCampos provedores fisicos////////////
NS.panelCamposPF = new Ext.form.FieldSet({
		id: PF + 'panelCamposPF',
		name: PF + 'panelCamposPF',
		title: 'Proveedores Fisicos',
		x: 0,
		y: 0,
		width: 985,
		height: 600,
		layout: 'absolute',
		items:
		[
		 	NS.lblEmpresaPF,
		 	//NS.lblNoPersonaPF,
		 	NS.lblPaternoPF,
		 	NS.lblMaternoPF,
		 	NS.lblNombrePF,
		 	NS.lblNombreCortoPF,
		 	NS.lblRFCPF,
		 	NS.lblFormaPagoProvF,
		 	NS.txtEmpresaPF,
		 	//NS.txtNoPersonaPF,
		 	NS.txtPaternoPF,
		 	NS.txtMaternoPF,
		 	NS.txtNombrePF,
		 	NS.txtNombreCortoPF,		 	
		 	NS.txtRFCPF,
		 	NS.txtIdFormaPagoProvF,
		 	NS.panelSexoPF,
//		 	NS.panelGpoRubroPF,
		
			{
		 		xtype: 'checkbox',
		 		boxLabel: 'Proveedor de Servicios Básicos',
		 		id: PF + 'chkProveedorServiciosBasicosPF',
		 		name: PF + 'chkProveedorServiciosBasicosPF',
		 		x: 250,
		 		y: 125,
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		} 		
		 	},
		 	{
		 		xtype: 'checkbox',
		 		boxLabel: 'Reactivar Persona',
		 		id: PF + 'chkReactivarPersonaPF',
		 		name: PF + 'chkReactivarPersonaPF',
		 		x: 480,
		 		y: 125,
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{
		 					if (valor == true)
		 						NS.estatus = "A";
		 				}
		 			}
		 		}
		 	},

		 	NS.cmbFormaPagoProvF,
		 	
		]
});
	//////////////////////////Termina panel Proveedores Fisicos///////////////////////////////////////////

//Panel campos fisicos ////////////////////////////
	NS.panelCamposF = new Ext.form.FieldSet({
		id: PF + 'panelCamposF',
		name: PF + 'panelCamposF',
		title: 'Persona Fisica',
		x: 0,
		y: 0,
		width: 985,
		height: 370,
		layout: 'absolute',
		items:
		[
		 	NS.lblNombreCortoF,
		 //	NS.lblEmpresaF,
		 	
		 	NS.lblNoPersonaF,
		 	NS.lblPaternoF,
		 	NS.lblMaternoF,
		 	NS.lblNombreF,
		 	NS.lblRFCF,
		 //	NS.txtEmpresaF,
		 	NS.txtNoPersonaF,
		 	NS.txtPaternoF,
		 	NS.txtMaternoF,
		 	NS.txtNombreF,
		 	NS.txtNombreCortoF,		 	
		 	NS.txtRFCF,
		 	NS.panelSexoF
		]
});
	

////////////////////////////////Panel Filial///////////////////////////


NS.panelCamposFI = new Ext.form.FieldSet({
id: PF + 'panelCamposFI',
name: PF + 'panelCamposFI',
title: 'Filial',
x: 0,
y: 0, 
width: 985,
height: 600,
layout: 'absolute',
items:
[
NS.lblEmpresaFI,
NS.lblNoPersonaFI,
NS.lblRazonSocialFI,
NS.lblNombreCortoFI,
NS.lblRFCFI,
NS.lblFechaIngresoFI,
NS.lblGrupoFlujoFI,
NS.lblActividadEconomicaFI,
NS.lblActividadGenericaFI,
NS.lblGiroFI,
NS.lblCajaFI,
NS.lblCalidadFI,
NS.lblTipoInmuebleFI,
NS.lblVentasAnualesFI,
NS.lblObjetoSocialFI,
NS.txtEmpresaFI,
NS.txtNoPersonaFI,
NS.txtRazonSocialFI,
NS.txtNombreCortoFI,
NS.txtRFCFI,		
NS.txtFechaIngresoFI,
NS.txtIdGrupoFI,
NS.txtIdActividadEconomicaFI,		
NS.txtIdActividadGenericaFI,	
NS.txtIdGiroFI,					
NS.txtIdCalidadFI,			
NS.txtIdInmuebleFI,
NS.txtIdCajaFI,
NS.txtObjetoSocialFI,
NS.txtVentasAnualesFI,
NS.cmbGrupoFI,
NS.cmbActividadEconomicaFI,
NS.cmbActividadGenericaFI,
NS.cmbGiroFI,
NS.cmbCajaFI,
NS.cmbCalidadFI,
NS.cmbInmuebleFI,
//NS.panelTefFI,
//NS.panelGpoRubroFI,
NS.panelConcentradoraFI,
NS.panelExportaFI

]
});


//////////////////////////termina empresa ///////////////////////

	///////////////////Direcciones//////////////////////////////7
NS.panelCamposDI = new Ext.form.FieldSet({
	id: PF + 'panelCamposDI',
	name: PF + 'panelCamposDI',
	title: 'Direcciones',
	x: 0,
	y: 0, 
	width: 985,
	height: 600,
	layout: 'absolute',
	items:
	[
	 		NS.lblNoPersonaD,
			NS.lblRazonSocialD,
			NS.lblTipoOperacion,
			NS.lblCalle,
			NS.lblColonia,
			NS.lblCp,
			NS.lblDelegacion,
			NS.lblCiudad,
			NS.lblEstado,
			NS.lblPais,
			NS.txtNoPersonaD,
			NS.txtRazonSocialD,
			NS.txtIdTipoD,
			NS.txtCalle,
			NS.txtColonia,
			NS.txtCp,
			NS.txtDelegacion,
			NS.txtCiudad,
			NS.txtIdEstado,
			NS.txtPais,
			NS.cmbTipoDireccion,
			NS.cmbEstado,
			NS.cmbPais	
	 	
	]
});
/////////////////////////Termina Direcciones//////////////////////

///////////////////////////Medios de Contacto/////////////////////////////
NS.panelCamposMC = new Ext.form.FieldSet({
	id: PF + 'panelCamposMC',
	name: PF + 'panelCamposMC',
	title: 'Medios de Contacto',
	x: 0,
	y: 0, 
	width: 985,
	height: 600,
	layout: 'absolute',
	items:
	[
	 		NS.lblNoPersonaMC,
			NS.lblRazonSocialMC,
			NS.lblContactoPrincipalMC,
			NS.lblFechaMC,
			NS.lblTipoMC,
			NS.lblContactoMC,
			NS.txtNoPersonaMC,
			NS.txtRazonSocialMC,
			NS.txtContactoPrincipalMC,
			NS.txtFechaIngresoMC,
			NS.txtTipoMC1,
			NS.txtTipoMC2,
			NS.txtTipoMC3,
			NS.txtTipoMC4,
			NS.txtTipoMC5,
			NS.txtTipoMC6,
			NS.txtTipoMC7,
			NS.cmbTipoMC1,
			NS.cmbTipoMC2,
			NS.cmbTipoMC3,
			NS.cmbTipoMC4,
			NS.cmbTipoMC5,
			NS.cmbTipoMC6,
			NS.cmbTipoMC7,
			NS.txtContactoMC1,
			NS.txtContactoMC2,
			NS.txtContactoMC3,
			NS.txtContactoMC4,
			NS.txtContactoMC5,
			NS.txtContactoMC6,
			NS.txtContactoMC7
	]
});

////////////////////////Termina Medios Contactos///////////////////////////////

	//////////////////////////panel campos Empresa//////////////////////////////
	NS.panelCamposE = new Ext.form.FieldSet({
		id: PF + 'panelCamposE',
		name: PF + 'panelCamposE',
		title: 'Empresa',
		x: 0,
		y: 0, 
		width: 985,
		height: 370,
		layout: 'absolute',
		items:
		[
		 
		    //PAO
		    NS.lblMensajeCaja,
		 	NS.lblEmpresaE,
			NS.lblRazonSocialE,
			NS.lblNombreCortoE,
			NS.lblRFCE,
			NS.lblFechaIngresoE,
//			NS.lblGrupoFlujoE,
			NS.lblCajaE,
//			NS.lblCalidadE,
//			NS.lblTipoInmuebleE,
			NS.txtEmpresaE,
			NS.txtRazonSocialE,
			NS.txtNombreCortoE,
			NS.txtRFCE,		
			NS.txtFechaIngresoE,
//			NS.txtIdGrupoE,					
//			NS.txtIdCalidadE,			
//			NS.txtIdInmuebleE,
			NS.txtIdCajaE,
//			NS.cmbGrupoE,
			NS.cmbCajaE,
//			NS.cmbCalidadE,
//			NS.cmbInmuebleE,
			NS.panelConcentradoraE,
			NS.storeLlenaComboGrupo.load(),
			NS.storeComboGiro.load(),
			NS.storeActividadGenerica.load(),
			NS.storeActividadEconomica.load(),
			NS.storeComboCaja.load(),
			NS.storeCalidad.load(),
			NS.storeInmueble.load(),
		]	
});
	//////////////////////////termina empresa ////////////////////////////////////////

	//Panel campos clientes morales////////////////////////////
	NS.panelCamposCM = new Ext.form.FieldSet({
		id: PF + 'panelCamposCM',
		name: PF + 'panelCamposCM',
		title: 'información General',
		x: 0,
		y: 0,
		width: 985,
		height: 600,
		layout: 'absolute',
		hidden: true,
		items:
		[
		 	NS.lblEmpresaCM,
		 	NS.lblNoPersonaCM,
		 	NS.lblRazonSocialCM,
		 	NS.lblNombreCortoCM,
		 	NS.lblRFCCM,
			NS.lblFechaIngresoCM,
			NS.txtEmpresaCM,
		 	NS.txtNoPersonaCM,
		 	NS.txtRazonSocialCM,
		 	NS.txtNombreCortoCM,		 	
		 	NS.txtRFCCM,
			NS.txtFechaIngresoCM,
		 	
		]
});
	//////////////////////////////Termina Clientes Morales//////////////////////////////////////
	
	////////////////////////////////Panel Clientes Fisicos///////////////////////////////////////
	NS.panelCamposCF = new Ext.form.FieldSet({
		id: PF + 'panelCamposCF',
		name: PF + 'panelCamposCF',
		title: 'Clientes Fisicos',
		x: 0,
		y: 0,
		width: 985,
		height: 600,
		layout: 'absolute',
		items:
		[
		 	NS.lblEmpresaCF,
		 	NS.lblNoPersonaCF,
		 	NS.lblPaternoCF,
		 	NS.lblMaternoCF,
		 	NS.lblNombreCF,
		 	NS.lblNombreCortoCF,
		 	NS.lblRFCCF,
			NS.txtEmpresaCF,
		 	NS.txtNoPersonaCF,
		 	NS.txtPaternoCF,
		 	NS.txtMaternoCF,
		 	NS.txtNombreCF,
		 	NS.txtNombreCortoCF,		 	
		 	NS.txtRFCCF,
		 	NS.panelSexoCF
		]
});
	
	
	///////////////////////////////Termina PanelCamposCF///////////////////////////////////////
	
	/////////////////
	//PanelCampos Instituciones Bancarias////////////
	NS.panelCamposIB = new Ext.form.FieldSet({
			id: PF + 'panelCamposIB',
			name: PF + 'panelCamposIB',
			title: 'INSTITUCIONES BANCARIAS',
			x: 0,
			y: 0,
			width: 985,
			height: 370,
			layout: 'absolute',
			items:
			[
			 	//NS.lblEmpresaIB,
				NS.lblNoPersonaIB,
				NS.lblRazonSocialIB,
				NS.lblNombreCortoIB,
			 	NS.lblRFCIB,
			 	NS.lblFechaIngresoIB,
				//NS.txtEmpresaIB,
			 	NS.txtNoPersonaIB,
			 	NS.txtRazonSocialIB,
			 	NS.txtNombreCortoIB,		 	
			 	NS.txtRFCIB,
			 	NS.txtFechaIngresoIB,
			 	
			]
	});

	////////////////////////TERMINA PANEL INSTITUCIONES BANCARIAS/////////////////////////
	
	NS.panelCampos = new Ext.form.FieldSet({
		id: PF + 'panelCampos',
		name: PF + 'panelCampos',
		title: 'Información General',
		x: 0,
		y: 0,
		width: 985,
		height: 300,
		layout: 'absolute',
		hidden: true,
		items:
		[
		 
		 	NS.lblEmpresaGral,
		 	NS.lblNoPersonaGral,
		 	NS.lblPaternoGral,
		 	NS.lblMaternoGral,
		 	NS.lblNombreGral,
		 	NS.lblRazonSocialGral,
		 	NS.lblNombreCorto,
		 	NS.lblRFCGral,
		 	NS.lblFechaIngreso,
		 	NS.lblPuesto,
		 	NS.lblEstadoCivil,
		 	//NS.lblGrupoFlujo,
		 	//NS.lblActividadEconomica,
		 	//NS.lblActividadGenerica,
		 	//NS.lblGiro,
		 	//NS.lblCaja,
		 	NS.lblCambioPersona,
		 	NS.lblRiesgo,
		 	//NS.lblTamano,
		 	//NS.lblCalidad,		 	
		 	//NS.lblTipoInmueble,
		 	//NS.lblVentasAnuales,
		 	//NS.lblObjetoSocial,
		 	NS.lblNoEmpleados,
		 	NS.lblDiaLimite,
		 	NS.lblDiaRecepcion,
		 	NS.lblFormaPagoProv,
		 	NS.lblPagoReferenciado,
		 	NS.lblAccionesA,
		 	NS.lblAccionesB,
		 	NS.lblAccionesC,
		 	NS.txtEmpresaGral,
		 	NS.txtNoPersonaGral,
		 	NS.txtEquivalePersona,
		 	NS.txtRazonSocialGral,
		 	NS.txtPaternoGral,
		 	NS.txtMaternoGral,
		 	NS.txtNombreGral,
		 	NS.txtNombreCorto,		 	
		 	NS.txtRFCGral,
		 	NS.txtFechaIngreso,
		 	NS.txtPuesto,
		 	NS.txtIdGrupo,
		 	//NS.txtIdActividadEconomica,
		 	//NS.txtIdActividadGenerica,
		 	{
		 		xtype: 'uppertextfield',
		 		id: PF + 'txtIdEstadoCivil',
		 		name: PF + 'txtIdEstadoCivil',
		 		x: 465,
		 		y: 115,
		 		hidden: true,
		 		width: 50,
		 		listeners: {
		 			change: {
		 				
		 				fn: function(caja, valor){
		 					if (caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
		 						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdEstadoCivil', NS.cmbEstadoCivil.getId());
		 					else
		 						NS.cmbEstadoCivil.reset();
		 				}
		 			}
		 		}	 	
		 	},		 	
		 	//NS.txtIdGiro,
		 	//NS.txtIdCaja,
		 	NS.txtIdCambioPersona,
		 	NS.txtIdRiesgo,
		 	//NS.txtIdTamano,
		 	//NS.txtIdCalidad,
		 	//NS.txtIdInmueble,
		 	//NS.txtVentasAnuales,
		 	//NS.txtObjetoSocial,
		 	NS.txtNoEmpleados,
		 	NS.txtDiaLimite,
		 	NS.txtDiaRecepcion,
		 	NS.txtIdFormaPagoProv,
		 	NS.txtAccionesA,
		 	NS.txtAccionesB,
		 	NS.txtAccionesC,
		 	//NS.txtGrupoRubro,
		 	//NS.txtRubro,
		 	NS.cmbGrupo,
		 	NS.cmbActividadEconomica,
		 	NS.cmbActividadGenerica,
		 	NS.cmbEstadoCivil,
		 	NS.cmbGiro,	
		 	NS.cmbCaja,
		 	NS.cmbCambioPersona,
		 	NS.cmbRiesgo,
		 	//NS.cmbTamano,
		 	NS.cmbCalidad,
		 	NS.cmbInmueble,
		 	NS.cmbFormaPagoProv,
		 	NS.cmbPagoReferenciado,
		 	/*NS.cmbGrupoRubro,
		 	NS.cmbRubro,
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkDivision',
		 		name: PF + 'chkDivision',
		 		x: 750,
		 		y: 200,
		 		hidden: true,
		 		boxLabel: 'División Empresarial',
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		}
		 	},*/
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkPagosCruzados',
		 		x: 750,
		 		y: 235,
		 		hidden: true,
		 		boxLabel: 'Pagos Cruzados Aut.',
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'checkbox',
		 		boxLabel: 'Proveedor de Servicios Básicos',
		 		id: PF + 'chkProveedorServiciosBasicos',
		 		name: PF + 'chkProveedorServiciosBasicos',
		 		x: 550,
		 		y: 300,
		 		hidden: true,
		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{}
		 			}
		 		} 		
		 	},
		 	{
		 		xtype: 'checkbox',
		 		boxLabel: 'Reactivar Persona',
		 		id: PF + 'chkReactivarPersona',
		 		name: PF + 'chkReactivarPersona',
		 		x: 810,
		 		y: 120,


		 		listeners:
		 		{
		 			check:
		 			{
		 				fn: function(checkbox, valor)
		 				{
		 					if (valor == true)
		 						NS.estatus = "A";
		 				}
		 			}
		 		}
		 	},
		 	NS.panelPAC,
		 	NS.panelSexo,
		 	NS.panelExporta,
		 	NS.panelConcentradora,
		 	NS.panelTef,
		 	NS.panelGpoRubro
		]
	});
	
	NS.panelGrid = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 180,

		width: 985,
		height: 250,

		layout: 'absolute',
		items:
		[
		 	NS.gridConsulta,
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Excel',
		 		x: 540,
		 		y: 205,
		 		id: PF + 'excel',
		 		name: PF + 'excel',
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		 					
		 					if(NS.tipoPersona !=""){
		 						parametros='?nomReporte=excelPersonas';
	 							parametros+='&nomParam1=tipoPersona';
	 							parametros+='&valParam1='+NS.tipoPersona;
	 							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
		 					}else{
		 						Ext.Msg.alert('SET'," Debe seleccionar un tipo de persona");
		 						
		 					}
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Consultar',
		 		x: 650,
		 		y: 205,
		 		id: PF + 'modificar',
		 		name: PF + 'modificar',
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		 					if(NS.tipoPersona=='E'){
		 						var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
			 					
			 					if (registroSeleccionado.length <= 0)
			 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			 					else
			 					{
			 						NS.panelBusqueda.setVisible(false);
			 						NS.panelGrid.setVisible(false);
			 						NS.panelCamposE.setVisible(true);
			 						NS.panelBotonesMOE.setVisible(true);
				 					NS.llenaDatosE();
				 					NS.desEmpresa();
		 					}
		 					}
		 					
		 					if(NS.tipoPersona=='B'){
		 						var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
			 					
			 					if (registroSeleccionado.length <= 0)
			 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			 					else
			 					{
			 						NS.panelBusqueda.setVisible(false);
			 						NS.panelGrid.setVisible(false);
			 						NS.panelCamposIB.setVisible(true);
			 						NS.panelBotonesMIB.setVisible(true);
			 						NS.llenaDatosIB();
				 					
		 					}
		 					}
		 					
		 					if(NS.tipoPersona=='C'){
		 						var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
			 					if (registroSeleccionado.length <= 0)
			 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			 					else
			 					{
			 						if(NS.gridConsulta.getSelectionModel().getSelections()[0].get('fisicaMoral')=='F'){
			 							NS.panelBusqueda.setVisible(false);
				 						NS.panelGrid.setVisible(false);
				 						NS.panelCamposCF.setVisible(true);
				 						NS.panelBotonesCF.setVisible(true);
					 					NS.llenaDatos();
									} else {
										NS.panelBusqueda.setVisible(false);
				 						NS.panelGrid.setVisible(false);
				 						NS.panelCamposCM.setVisible(true);
				 						NS.panelBotonesCM.setVisible(true);
					 					NS.llenaDatos();
									}
			 						;	
			 						
		 					}
		 					}
		 					
		 					if(NS.tipoPersona=='F'){
		 						var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
			 					
			 					if (registroSeleccionado.length <= 0)
			 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			 					else
			 					{
			 						NS.panelBusqueda.setVisible(false);
			 						NS.panelGrid.setVisible(false);
			 						NS.panelCamposF.setVisible(true);
			 						NS.panelBotonesMOF.setVisible(true);
				 					NS.llenaDatosF();
		 					}
		 					}
		 					
		 					if(NS.tipoPersona=='I'){
		 						var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
			 					
			 					if (registroSeleccionado.length <= 0)
			 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			 					else
			 					{
			 						NS.panelBusqueda.setVisible(false);
			 						NS.panelGrid.setVisible(false);
			 						NS.panelCamposFI.setVisible(true);
			 						NS.panelBotonesFI.setVisible(true);
				 					NS.llenaDatos();
		 					}
		 					}
		 					if(NS.tipoPersona=='K'){
		 						var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
			 					
			 					if (registroSeleccionado.length <= 0)
			 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			 					else
			 					{
			 						NS.panelBusqueda.setVisible(false);
			 						NS.panelGrid.setVisible(false);
			 						NS.panelCamposK.setVisible(true);
			 						NS.panelBotonesMOK.setVisible(true);
				 					NS.llenaDatosK();
		 					}
		 					}
		 					
		 					if(NS.tipoPersona=='P'){
		 						var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
			 					if (registroSeleccionado.length <= 0)
			 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			 					else
			 					{
//			 						if(NS.gridConsulta.getSelectionModel().getSelections()[0].get('fisicaMoral')=='F'){
//			 							NS.panelBusqueda.setVisible(false);
//				 						NS.panelGrid.setVisible(false);
//				 						NS.panelCamposPF.setVisible(true);
//				 						NS.panelBotonesPF.setVisible(true);
//					 					NS.llenaDatosP();
//									} else {
										NS.panelBusqueda.setVisible(false);
				 						NS.panelGrid.setVisible(false);
				 						NS.panelCamposPM.setVisible(true);
				 						NS.panelBotonesPM.setVisible(true);
				 						NS.desabilitaCPM();
					 					NS.llenaDatosP();
					 					//NS.empre1=NS.gridConsulta.getSelectionModel().getSelections()[0].get('noEmpresa');
					 					NS.txtCorreoMedios.setDisabled(true);
					 					
//									}
			 						};	
			 						
		 					}
		 					}	
		 				}
		 			}
		 		
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 750,
		 		y: 205,
		 		id: PF + 'crearN',
		 		name: PF + 'crearN',
		 		width: 80,
		 		height: 22,
		 		focus:true,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					if (Ext.getCmp(PF + 'txtTipoPersona').getValue() == "") {
		 						Ext.MessageBox.alert('SET','Seleccione el Tipo de Persona');
							} else {
		 					NS.modificar = false;
		 					NS.tipoPersona = Ext.getCmp(PF + 'txtTipoPersona').getValue();
		 						if (Ext.getCmp(PF + 'txtTipoPersona').getValue() != ''){
			 						if (NS.tipoPersona == 'B'){
			 							//NS.panelGlobal.setVisible(false);
			 							NS.panelBusqueda.setVisible(false);
			 							NS.panelGrid.setVisible(false);
			 							NS.panelCamposIB.setVisible(true);
			 							NS.panelBotonesIB.setVisible(true);
			 							NS.limpiarIB();
			 							NS.folioB();
			 						}
			 						
			 						if (NS.tipoPersona == 'P'){
			 							Ext.Msg.confirm('SET', '¿El Proveedor es persona Fisica?', function(btn){
			 								if (btn === 'yes'){
			 									NS.panelBusqueda.setVisible(false);
			 									NS.panelGrid.setVisible(false);
			 									NS.panelBotonesPM.setVisible(false);
			 									NS.panelCamposPF.setVisible(true);
			 									NS.panelBotonesPF.setVisible(true);
			 									NS.storeFormaPagoProv.load();	
			 								}
			 								else{
			 									NS.panelBusqueda.setVisible(false);
			 									NS.panelGrid.setVisible(false);
			 									NS.panelCamposPM.setVisible(true);
			 									NS.panelBotonesPM.setVisible(true);
			 									NS.storeFormaPagoProv.load();
			 								}
			 							});		 						
			 						}
			 						
			 						if (NS.tipoPersona == 'C'){
			 							Ext.Msg.confirm('SET', '¿El Cliente es persona Fisica?', function(btn){
			 								if (btn === 'yes'){
			 									NS.panelBusqueda.setVisible(false);
			 									NS.panelGrid.setVisible(false);
			 									NS.panelCamposCF.show();
			 									NS.panelBotonesCF.show();
			 										
			 								}
			 								else{
			 									NS.panelBusqueda.setVisible(false);
			 									NS.panelGrid.setVisible(false);
			 									NS.panelCamposCM.show();
			 									NS.panelBotonesCM.show();
			 								}
			 							});		 						
			 						}
			 						
			 						if (NS.tipoPersona == 'F'){
			 							NS.panelBusqueda.setVisible(false);
	 									NS.panelGrid.setVisible(false);
	 									NS.panelCamposF.show();
	 									NS.panelBotonesF.show();
	 									NS.folioF();
			 						}
			 						
			 						if (NS.tipoPersona == 'R'){
			 							NS.fisicaMoral = "F";
			 							NS.muestraCampos();
			 						}
			 						
			 						if (NS.tipoPersona == 'I'){
			 							NS.panelBusqueda.setVisible(false);
	 									NS.panelGrid.setVisible(false);
	 									NS.panelCamposFI.show();
	 									NS.panelBotonesFI.show();
			 						}
			 						
			 						if (NS.tipoPersona == 'K'){
			 							NS.panelBusqueda.setVisible(false);
			 							NS.panelGrid.setVisible(false);
			 							NS.panelCamposK.setVisible(true);
			 							NS.panelBotonesK.setVisible(true);
			 							NS.folioK();
			 						}
			 						
			 						if (NS.tipoPersona == 'E'){
			 							NS.panelBusqueda.setVisible(false);
			 							NS.panelGrid.setVisible(false);
			 							NS.panelCamposE.setVisible(true);
			 							NS.panelBotonesE.setVisible(true);
			 							NS.habEmpresa();
			 							NS.fechaE();
			 							
			 						}
		 						}
							}
		 				}
		 			}
		 		}
		 	},
							
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 850,
		 		y: 205,
		 		id: PF + 'eliminar',
		 		name: PF + 'eliminar',
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.modificar = false;
		 					NS.deshabilitaPersona();
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.nacionalidad = "N";
	
	
	//LABEL
	NS.lblEmpresaCuentas = new Ext.form.Label({
		x:0,
		y: 0,
		text: 'No Empresa'		
	});
	
	NS.lblNoPersonaCuentas = new Ext.form.Label({
		x: 440, 
		y: 0,
		text: 'No Persona'
	});
	
	NS.lblRazonSocialCuentas = new Ext.form.Label({
		x: 80,
		y: 0,
		text: 'Razón Social / Nombre'
	});
	
	NS.lblDivisaCuentas = new Ext.form.Label({
		x: 270,
		y: 205,
		text: 'Divisa'
	});
	
	NS.lblChequeraCuentas = new Ext.form.Label({
		x: 345,
		y: 205,
		text: 'Chequera'
	});
	
	NS.lblDescChequeraCuentas = new Ext.form.Label({
		x: 510,
		y: 205,
		text: 'Descripción Chequera'
	});
	
	NS.lblSucursalCuentas = new Ext.form.Label({
		x: 725,
		y: 205,
		text: 'Sucursal'
	});
	
	NS.lblPlazaCuentas = new Ext.form.Label({
		x: 840,
		y: 205,
		text: 'Plaza'
	});
	
	NS.lblClabeCuentas = new Ext.form.Label({
		x: 270,
		y: 250,
		text: 'Clabe'
	});	
	
	NS.lblBancoAnteriorCuentas = new Ext.form.Label({
		x: 440,
		y: 250,
		text: 'Banco Anterior'
	});	
	NS.lblChequeraAnteriorCuentas = new Ext.form.Label({
		x: 535,
		y: 250,
		text: 'Chequera Anterior'
	});
	
	NS.lblBancoIntermediarioCuentas = new Ext.form.Label({
		x: 0,
		y: 0,
		text: 'Banco Intermediario'
	});
	
	NS.lblChequeraIntermediarioCuentas = new Ext.form.Label({
		x: 235,
		y: 0,
		text: 'Chequera Intermediario'
	});
	
	NS.lblBancoCorresponsalCuentas = new Ext.form.Label({
		x: 410,
		y: 0,
		text: 'Banco Corresponsal'
	});
		
	//TEXTFIELD
	NS.txtEmpresaCuentas = new Ext.form.TextField({
		id: PF + 'txtEmpresaCuentas',
		name: PF + 'txtEmpresaCuentas',
		x: 0,
		y: 15,
		width: 70,
		disabled: true
	});
	
	NS.txtNoPersonaCuentas = new Ext.form.TextField({
		id: PF + 'txtNoPersonaCuentas',
		name: PF + 'txtNoPersonaCuentas',
		x: 440,
		y: 15,
		width: 70,
		disabled: true
	});
	
	NS.txtRazonSocialCuentas = new Ext.form.TextField({
		id: PF + 'txtRazonSocialCuentas',
		name: PF + 'txtRazonSocialCuentas',
		x: 80,
		y: 15,
		width: 350,
		disabled: true
	});
	
	NS.txtIdBancoCuentas = new Ext.form.TextField({
		id: PF + 'txtIdBancoCuentas',
		name: PF + 'txtIdBancoCuentas',
		x: 0,
		y: 25,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined )
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBancoCuentas', NS.cmbBancoCuentas.getId());
					else
						NS.cmbBancoCuentas.reset();			
				}
			}
		}
	});
	
	NS.txtIdChequeraCuentas = new Ext.form.TextField({
		id: PF + 'txtIdChequeraCuentas',
		name: PF + 'txtIdChequeraCuentas',
		x: 345,
		y: 220,
		width: 150
	});
	
	NS.txtDescChequeraCuentas = new Ext.form.TextField({
		id: PF + 'txtDescChequeraCuentas',
		name: PF + 'txtDescChequeraCuentas',
		x: 510,
		y: 220,
		width: 200
	});
	
	NS.txtSucursalCuentas = new Ext.form.TextField({
		id: PF + 'txtSucursalCuentas',
		name: PF + 'txtSucursalCuentas',
		x: 725,
		y: 220,
		width: 100
	});
	
	NS.txtPlazaCuentas = new Ext.form.TextField({
		id: PF + 'txtPlazaCuentas',
		name: PF + 'txtPlazaCuentas',
		x: 840, 
		y: 220,
		width: 100
	});
	
	NS.txtClabeCuentas = new Ext.form.TextField({
		id: PF + 'txtClabeCuentas',
		name: PF + 'txtClabeCuentas',
		x: 270,
		y: 265,
		width: 150
	});
	
	NS.txtBancoAnteriorCuentas = new Ext.form.TextField({
		id: PF + 'txtBancoAnteriorCuentas',
		name: PF + 'txtBancoAnteriorCuentas',
		x: 440,
		y: 265,
		width: 80
	});
	
	NS.txtChequeraAnteriorCuentas = new Ext.form.TextField({
		id: PF + 'txtChequeraAnteriorCuentas',
		name: PF + 'txtChequeraAnteriorCuentas',
		x: 535,
		y: 265,
		width: 150
	});
	
	NS.txtAbaSwiftCuentas = new Ext.form.TextField({
		id: PF + 'txtAbaSwiftCuentas',
		name: PF + 'txtAbaSwiftCuentas',
		x: 110,
		y: 0,
		width: 100
	});
	
	NS.txtIdBancoIntermediarioCuentas = new Ext.form.TextField({
		id: PF + 'txtIdBancoIntermediarioCuentas',
		name: PF + 'txtIdBancoIntermediarioCuentas',
		disabled: true,
		x: 0,
		y: 15,
		width: 50,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined )
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBancoIntermediarioCuentas', NS.cmbBancoTrueIntermediario.getId());
					else
						NS.cmbBancoTrueIntermediario.reset();
				}
			}
		}
	});
	
	NS.txtChequeraIntermediarioCuentas = new Ext.form.TextField({
		id: PF + 'txtChequeraIntermediarioCuentas',
		name: PF + 'txtChequeraIntermediarioCuentas',
		disabled: true,
		x: 235,
		y: 15,
		width: 150
	});
	
	NS.txtIdBancoCorresponsalCuentas = new Ext.form.TextField({
		id: PF + 'txtIdBancoCorresponsalCuentas',
		name: PF + 'txtIdBancoCorresponsalCuentas',
		disabled: true,
		x: 410,
		y: 15,
		width: 50,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined )
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBancoCorresponsalCuentas', NS.cmbBancoCorresponsal.getId());
					else
						NS.cmbBancoCorresponsal.reset();
				}
			}
		}
	});
	
	NS.txtAbaSwiftIntermediario = new Ext.form.TextField({
		id: PF + 'txtAbaSwiftIntermediario',
		name: PF + 'txtAbaSwiftIntermediario',
		disabled: true,
		x: 110,
		y: 0,
		width: 100
	});
	
	NS.txtAbaSwiftCorresponsal = new Ext.form.TextField({
		id: PF + 'txtAbaSwiftCorresponsal',
		name: PF + 'txtAbaSwiftCorresponsal',
		disabled: true,
		x: 110,
		y: 0,
		width: 100
	});
	
	//FUNCIONES
	///////////////////////Vector prueba////////////////////////
	NS.llenaVectorPF = function(){
		var registroSeleccionado = NS.gridConsultaCuentas.getSelectionModel().getSelections();
		//var matrizCuentas = new Array();
		
		vectorCuentas.idBanco = Ext.getCmp(PF + 'txtIdBancoCuentas').getValue();
		//alert(Ext.getCmp(PF + 'txtIdBancoCuentas').getValue() + " idBanco en vector");
		//vectorCuentas.descBanco = registroSeleccionado[0].get("descBanco");
		vectorCuentas.chequera = Ext.getCmp(PF + 'txtIdChequeraCuentas').getValue();
		vectorCuentas.descChequera = Ext.getCmp(PF + 'txtDescChequeraCuentas').getValue();
		vectorCuentas.divisa = Ext.getCmp(PF + 'cmbDivisaCuentas').getValue();
		vectorCuentas.sucursal = Ext.getCmp(PF + 'txtSucursalCuentas').getValue();
		vectorCuentas.plaza = Ext.getCmp(PF + 'txtPlazaCuentas').getValue();
		vectorCuentas.clabe = Ext.getCmp(PF + 'txtClabeCuentas').getValue();
		vectorCuentas.chequeraAnt = Ext.getCmp(PF + 'txtChequeraAnteriorCuentas').getValue();
		vectorCuentas.idBancoAnt = Ext.getCmp(PF + 'txtBancoAnteriorCuentas').getValue();
		vectorCuentas.usuarioModif = NS.idUsuario;
		vectorCuentas.fecModif = NS.fecHoy;
		vectorCuentas.noEmpresa = NS.noEmpresa;
		vectorCuentas.noPersona = Ext.getCmp(PF + 'txtNoPersonaCuentas').getValue();
		vectorCuentas.chequeraBenef = Ext.getCmp(PF + 'txtIdChequeraCuentas').getValue();
		
		
		if (Ext.getCmp(PF + 'optAbaSwiftCuentas').getValue() == 1){
			vectorCuentas.swift = Ext.getCmp(PF + 'txtAbaSwiftCuentas').getValue();
			vectorCuentas.aba = '';
		}
		else{
			vectorCuentas.aba = Ext.getCmp(PF + 'txtAbaSwiftCuentas').getValue();
			vectorCuentas.swift = '';
		}
				
		vectorCuentas.bankTrue = Ext.getCmp(PF + 'txtIdBancoIntermediarioCuentas').getValue();
		vectorCuentas.bankCorresponding = Ext.getCmp(PF + 'txtIdBancoCorresponsalCuentas').getValue();
		
		if (Ext.getCmp(PF + 'optRadioCuentas').getValue() == 0)
			vectorCuentas.nacionalidad = "N";
		else
			vectorCuentas.nacionalidad = "E";
		
		vectorCuentas.chequeraTrue = Ext.getCmp(PF + 'txtChequeraIntermediarioCuentas').getValue();
		vectorCuentas.bancoAnterior = Ext.getCmp(PF + 'txtBancoAnteriorCuentas').getValue();
		vectorCuentas.chequeraAnterior = Ext.getCmp(PF + 'txtChequeraAnteriorCuentas').getValue();
				
		if (Ext.getCmp(PF + 'optRadioIntermediario').getValue() == 1){
			vectorCuentas.swiftInter = Ext.getCmp(PF + 'txtAbaSwiftIntermediario').getValue();
			vectorCuentas.abaInter = '';
		}
		else{
			vectorCuentas.abaInter = Ext.getCmp(PF + 'txtAbaSwiftIntermediario').getValue();
			vectorCuentas.swiftInter = '';
		}
				
		if (Ext.getCmp(PF + 'optRadioCorresponsal').getValue() == 1){
			vectorCuentas.swiftCorresp = Ext.getCmp(PF + 'txtAbaSwiftCorresponsal').getValue();
			vectorCuentas.abaCorresp = '';
		}
		else{
			vectorCuentas.abaCorresp = Ext.getCmp(PF + 'txtAbaSwiftCorresponsal').getValue();
			vectorCuentas.swiftCorresp = '';			
		}
				
		vectorCuentas.tipoPersona = NS.tipoPersona;
		vectorCuentas.fecHoy = NS.fecHoy;
		vectorCuentas.usuarioAlta = NS.idUsuario;
		
		if (NS.modificarCuentas == true)
			vectorCuentas.tipoOperacion = "MODIFICAR";
		else
			vectorCuentas.tipoOperacion = "INSERTAR";
		
		vectorCuentas.actualizaChequeraProv = 'S';
		
		matrizCuentas[0] = vectorCuentas;		
	};
	
	///////////////////////////////////////////////////////////
	
	
	NS.llenaVector = function(){
		var registroSeleccionado = NS.gridConsultaCuentas.getSelectionModel().getSelections();
		//var matrizCuentas = new Array();
		
		vectorCuentas.idBanco = Ext.getCmp(PF + 'txtIdBancoCuentas').getValue();
		//alert(Ext.getCmp(PF + 'txtIdBancoCuentas').getValue() + " idBanco en vector");
		//vectorCuentas.descBanco = registroSeleccionado[0].get("descBanco");
		vectorCuentas.chequera = Ext.getCmp(PF + 'txtIdChequeraCuentas').getValue();
		vectorCuentas.descChequera = Ext.getCmp(PF + 'txtDescChequeraCuentas').getValue();
		vectorCuentas.divisa = Ext.getCmp(PF + 'cmbDivisaCuentas').getValue();
		vectorCuentas.sucursal = Ext.getCmp(PF + 'txtSucursalCuentas').getValue();
		vectorCuentas.plaza = Ext.getCmp(PF + 'txtPlazaCuentas').getValue();
		vectorCuentas.clabe = Ext.getCmp(PF + 'txtClabeCuentas').getValue();
		vectorCuentas.chequeraAnt = Ext.getCmp(PF + 'txtChequeraAnteriorCuentas').getValue();
		vectorCuentas.idBancoAnt = Ext.getCmp(PF + 'txtBancoAnteriorCuentas').getValue();
		vectorCuentas.usuarioModif = NS.idUsuario;
		vectorCuentas.fecModif = NS.fecHoy;
		vectorCuentas.noEmpresa = NS.noEmpresa;
		vectorCuentas.noPersona = Ext.getCmp(PF + 'txtNoPersonaCuentas').getValue();
		vectorCuentas.chequeraBenef = Ext.getCmp(PF + 'txtIdChequeraCuentas').getValue();
		
		if (Ext.getCmp(PF + 'optAbaSwiftCuentas').getValue() == 1){
			vectorCuentas.swift = Ext.getCmp(PF + 'txtAbaSwiftCuentas').getValue();
			vectorCuentas.aba = '';
		}
		else{
			vectorCuentas.aba = Ext.getCmp(PF + 'txtAbaSwiftCuentas').getValue();
			vectorCuentas.swift = '';
		}
				
		vectorCuentas.bankTrue = Ext.getCmp(PF + 'txtIdBancoIntermediarioCuentas').getValue();
		vectorCuentas.bankCorresponding = Ext.getCmp(PF + 'txtIdBancoCorresponsalCuentas').getValue();
		
		if (Ext.getCmp(PF + 'optRadioCuentas').getValue() == 0)
			vectorCuentas.nacionalidad = "N";
		else
			vectorCuentas.nacionalidad = "E";
		
		vectorCuentas.chequeraTrue = Ext.getCmp(PF + 'txtChequeraIntermediarioCuentas').getValue();
		vectorCuentas.bancoAnterior = Ext.getCmp(PF + 'txtBancoAnteriorCuentas').getValue();
		vectorCuentas.chequeraAnterior = Ext.getCmp(PF + 'txtChequeraAnteriorCuentas').getValue();
				
		if (Ext.getCmp(PF + 'optRadioIntermediario').getValue() == 1){
			vectorCuentas.swiftInter = Ext.getCmp(PF + 'txtAbaSwiftIntermediario').getValue();
			vectorCuentas.abaInter = '';
		}
		else{
			vectorCuentas.abaInter = Ext.getCmp(PF + 'txtAbaSwiftIntermediario').getValue();
			vectorCuentas.swiftInter = '';
		}
				
		if (Ext.getCmp(PF + 'optRadioCorresponsal').getValue() == 1){
			vectorCuentas.swiftCorresp = Ext.getCmp(PF + 'txtAbaSwiftCorresponsal').getValue();
			vectorCuentas.abaCorresp = '';
		}
		else{
			vectorCuentas.abaCorresp = Ext.getCmp(PF + 'txtAbaSwiftCorresponsal').getValue();
			vectorCuentas.swiftCorresp = '';			
		}
				
		vectorCuentas.tipoPersona = NS.tipoPersona;
		vectorCuentas.fecHoy = NS.fecHoy;
		vectorCuentas.usuarioAlta = NS.idUsuario;
		
		if (NS.modificarCuentas == true)
			vectorCuentas.tipoOperacion = "MODIFICAR";
		else
			vectorCuentas.tipoOperacion = "INSERTAR";
		
		vectorCuentas.actualizaChequeraProv = 'S';
		
		matrizCuentas[0] = vectorCuentas;		
	};
	
	NS.desabilitaCPM = function(){
		NS.txtCorreo.setDisabled(true);
		NS.txtTelefono.setDisabled(true);
		NS.txtFax.setDisabled(true);
		NS.txtEmpresaPM.setDisabled(true);
		NS.txtEquivalePerPM.setDisabled(true);
		NS.txtReferenciaPM.setDisabled(true);
	 	Ext.getCmp(PF + 'txtRazonSocialPM').setDisabled(true);
	 	Ext.getCmp(PF + 'txtNombreCortoPM').setDisabled(true);
	 	Ext.getCmp(PF + 'txtRFCPM').setDisabled(true);
	 	NS.txtFechaIngresoPM.setDisabled(true);
	 	NS.txtIdFormaPagoProvPM.setDisabled(true);
	 	NS.cmbFormaPagoProvPM.setDisabled(true);

	};
	
	NS.deshabilitaCamposProveedor = function(){
		//NS.txtEmpresaCuentas.setDisabled(true);
		NS.txtNoPersonaCuentas.setDisabled(true);
		NS.txtRazonSocialCuentas.setDisabled(true);
		NS.txtIdBancoCuentas.setDisabled(true);
		NS.txtIdChequeraCuentas.setDisabled(true);
		NS.txtDescChequeraCuentas.setDisabled(true);
		NS.txtSucursalCuentas.setDisabled(true);
		NS.txtPlazaCuentas.setDisabled(true);
		NS.txtClabeCuentas.setDisabled(true);
		NS.txtBancoAnteriorCuentas.setDisabled(true);
		NS.txtChequeraAnteriorCuentas.setDisabled(true);
		NS.txtAbaSwiftCuentas.setDisabled(true);
		NS.txtIdBancoIntermediarioCuentas.setDisabled(true);
		NS.txtChequeraIntermediarioCuentas.setDisabled(true);
		NS.txtIdBancoCorresponsalCuentas.setDisabled(true);
		NS.txtAbaSwiftIntermediario.setDisabled(true);
		NS.txtAbaSwiftCorresponsal.setDisabled(true);
		NS.cmbDivisaCuentas.setDisabled(true);
		NS.cmbBancoCuentas.setDisabled(true);
		NS.cmbBancoTrueIntermediario.setDisabled(true);
		NS.cmbBancoCorresponsal.setDisabled(true);
	};
	
	NS.habilitaCamposProveedor = function(){
		NS.txtEmpresaCuentas.setDisabled(false);
		NS.txtNoPersonaCuentas.setDisabled(false);
		NS.txtRazonSocialCuentas.setDisabled(false);
		NS.txtIdBancoCuentas.setDisabled(false);
		NS.txtIdChequeraCuentas.setDisabled(false);
		NS.txtDescChequeraCuentas.setDisabled(false);
		NS.txtSucursalCuentas.setDisabled(false);
		NS.txtPlazaCuentas.setDisabled(false);
		NS.txtClabeCuentas.setDisabled(false);
		NS.txtBancoAnteriorCuentas.setDisabled(false);
		NS.txtChequeraAnteriorCuentas.setDisabled(false);
		NS.txtAbaSwiftCuentas.setDisabled(false);
		NS.txtIdBancoIntermediarioCuentas.setDisabled(false);
		NS.txtChequeraIntermediarioCuentas.setDisabled(false);
		NS.txtIdBancoCorresponsalCuentas.setDisabled(false);
		NS.txtAbaSwiftIntermediario.setDisabled(false);
		NS.txtAbaSwiftCorresponsal.setDisabled(false);
		NS.cmbDivisaCuentas.setDisabled(false);
		NS.cmbBancoCuentas.setDisabled(false);
		NS.cmbBancoTrueIntermediario.setDisabled(false);
		NS.cmbBancoCorresponsal.setDisabled(false);
	};
	
	NS.limpiaCuentasProveedor = function(){
		//NS.txtEmpresaCuentas.setValue('');
		//NS.txtNoPersonaCuentas.setValue('');
		//NS.txtRazonSocialCuentas.setValue('');
		NS.txtIdBancoCuentas.setValue('');
		NS.txtIdChequeraCuentas.setValue('');
		NS.txtDescChequeraCuentas.setValue('');
		NS.txtSucursalCuentas.setValue('');
		NS.txtPlazaCuentas.setValue('');
		NS.txtClabeCuentas.setValue('');
		NS.txtBancoAnteriorCuentas.setValue('');
		NS.txtChequeraAnteriorCuentas.setValue('');
		NS.txtAbaSwiftCuentas.setValue('');
		NS.txtIdBancoIntermediarioCuentas.setValue('');
		NS.txtChequeraIntermediarioCuentas.setValue('');
		NS.txtIdBancoCorresponsalCuentas.setValue('');
		NS.txtAbaSwiftIntermediario.setValue('');
		NS.txtAbaSwiftCorresponsal.setValue('');
		NS.cmbDivisaCuentas.reset();
		NS.cmbBancoCuentas.reset();
		NS.cmbBancoTrueIntermediario.reset();
		NS.cmbBancoCorresponsal.reset();
		NS.gridConsultaCuentas.store.removeAll();
		NS.gridConsultaCuentas.getView().refresh();		
	};
	
	NS.validaMovimientos = function(chequera){		
		ConsultaPersonasAction.validaMovimientos(parseInt(Ext.getCmp(PF + 'txtNoPersonaCuentas').getValue()), chequera, function(resultado, e){
			if (resultado != '' && resultado != undefined && resultado != null)
				Ext.Msg.alert('SET', 'No se puede modificar ni eliminar esta Chequera por que tiene pagos no confirmados');
			else
				NS.habilitaCamposProveedor();
		});		
			
	};
	
	NS.eliminaRegistroProveedor = function(){
		var registroSeleccionado = NS.gridConsultaCuentas.getSelectionModel().getSelections();
		
		NS.llenaVector();
		var jSonStringCuentas = Ext.util.JSON.encode(matrizCuentas);
		
		ConsultaPersonasAction.validaMovimientos(Ext.getCmp(PF + 'txtNoPersonaCuentas').getValue(), Ext.getCmp(PF + 'txtIdChequeraCuentas').getValue(), function(resultado, e){
			if (resultado != '' && resultado != undefined && resultado != null)
				Ext.Msg.alert('SET', 'No se puede eliminar el registro por que tiene pagos.');
			else{
				ConsultaPersonasAction.validaTransferencias(jSonStringCuentas, function(resultadoTrans, e){
					if (resultadoTrans != '' && resultadoTrans != undefined && resultadoTrans != null)
						Ext.Msg.alert('SET', 'No se puede eliminar la chequera, ya que tiene Transferencias pendientes de enviar');
					else{						
						Ext.Msg.confirm('SET', '¿Esta seguro de eliminar la cuenta seleccionada?', function(btn){
							if (btn == 'yes')
							{	
								//Hace el Delete de la Cuenta
								ConsultaPersonasAction.eliminaCuentaProveedor(jSonStringCuentas, function(resultadoElimina, e){
									if (resultadoElimina != '' && resultadoElimina != undefined && resultadoElimina != null)
										Ext.Msg.alert('SET', resultadoElimina);									
								});
															
								//Inserta el registro de la cuenta en el historico
								ConsultaPersonasAction.insertaCuentaHistorico(jSonStringCuentas, function(resultadoInsertaHist, e){
									/*if (resultadoInsertaHist != '' && resultadoInsertaHist != undefined && resultadoInsertaHist != null)
										Ext.Msg.alert('SET', resultadoInsertaHist);*/
								});
								NS.limpiaCuentasProveedor();
								
								NS.storeGridCuentasProveedor.removeAll();
		 						NS.storeGridCuentasProveedor.baseParams.noPersona = NS.noPersona,
			 					NS.storeGridCuentasProveedor.baseParams.noEmpresa = NS.noEmpresa,
			 					NS.storeGridCuentasProveedor.baseParams.tipoPersona = NS.tipoPersona,
			 					NS.storeGridCuentasProveedor.load();
							}
						});
					}
				});				
			}				
				
		});
		
	};
	
	NS.cierraCuentasProveedor = function(){
		NS.panelCampos.setVisible(true);
		NS.panelBotones.setVisible(true);
		//NS.panelBotonesPr.setVisible(true);
		NS.panelCuentasBancarias.setVisible(false);	
		//NS.panelBotonesCuentasBancarias.setVisible(false);
		Ext.getCmp(PF + 'aceptar').setVisible(false);
		Ext.getCmp(PF + 'cancelar').setVisible(false);
		Ext.getCmp(PF + 'imprimir').setVisible(false);
		Ext.getCmp(PF + 'exportar').setVisible(false);
		Ext.getCmp(PF + 'regresar').setVisible(false);
		
			
	};
	
	NS.llenaCamposCuentas = function(){		
		var registroSeleccionado = NS.gridConsultaCuentas.getSelectionModel().getSelections();
				
		//Dependiendo la divisa se llenan los bancos intermediario y corresponsal
		if (registroSeleccionado[0].get('nacionalidad') == 'E'){
			Ext.getCmp(PF + 'optRadioCuentas').setValue(1);
			Ext.getCmp(PF + 'txtIdBancoIntermediarioCuentas').setValue(registroSeleccionado[0].get('bankTrue'));
			
			if (registroSeleccionado[0].get('bankTrue') != 0 && registroSeleccionado[0].get('bankTrue') != ''){
				ConsultaPersonasAction.buscaDescripcionBanco(parseInt(registroSeleccionado[0].get('bankTrue')), function(resultado, e){
					if (resultado != '' && resultado != undefined)
						Ext.getCmp(PF + 'cmbBancoTrueIntermediario').setValue(resultado);
				});
			}
			Ext.getCmp(PF + 'txtIdBancoCorresponsalCuentas').setValue(registroSeleccionado[0].get('bankCorresponding'));
			if (registroSeleccionado[0].get('bankCorresponding') != 0 && registroSeleccionado[0].get('bankCorresponding') != ''){
				ConsultaPersonasAction.buscaDescripcionBanco(parseInt(registroSeleccionado[0].get('bankCorresponding')), function(resultado, e){
					if (resultado != '' && resultado != undefined)
						Ext.getCmp(PF + 'cmbBancoCorresponsal').setValue(resultado);
				});
			}
		}
		else{			
			Ext.getCmp(PF + 'optRadioCuentas').setValue(0);
			NS.storeComboBancoCuentas.baseParams.nacionalidad = "N";
			NS.storeComboBancoCuentas.load();
			
			NS.panelAbaSwiftCuentas.setVisible(false);
			
			if (registroSeleccionado[0].get('divisa') == "DLS"){
				NS.panelAbaSwiftCuentas.setVisible(true);
				Ext.getCmp(PF + 'optAbaSwiftCuentas').setValue(1);				
			}			
		}	
		
		Ext.getCmp(PF + 'txtIdBancoCuentas').setValue(registroSeleccionado[0].get('idBanco'));
		Ext.getCmp(PF + 'cmbBancoCuentas').setValue(registroSeleccionado[0].get('descBanco'));
		Ext.getCmp(PF + 'txtBancoAnteriorCuentas').setValue(registroSeleccionado[0].get('idBancoAnt'));
		Ext.getCmp(PF + 'txtChequeraAnteriorCuentas').setValue(registroSeleccionado[0].get('chequeraAnt'));
		Ext.getCmp(PF + 'cmbDivisaCuentas').setValue(registroSeleccionado[0].get('divisa'));
		Ext.getCmp(PF + 'txtPlazaCuentas').setValue(registroSeleccionado[0].get("plaza"));
		Ext.getCmp(PF + 'txtSucursalCuentas').setValue(registroSeleccionado[0].get("sucursal"));
		Ext.getCmp(PF + 'txtClabeCuentas').setValue(registroSeleccionado[0].get("clabe"));
		Ext.getCmp(PF + 'txtDescChequeraCuentas').setValue(registroSeleccionado[0].get("descChequera"));
		Ext.getCmp(PF + 'txtIdChequeraCuentas').setValue(registroSeleccionado[0].get("chequera"));
		Ext.getCmp(PF + 'txtChequeraIntermediarioCuentas').setValue(registroSeleccionado[0].get("chequeraTrue"));
		
		//Aba y swift normal
		if (registroSeleccionado[0].get("swift") != ""){
			Ext.getCmp(PF + 'optAbaSwiftCuentas').setValue(1);
			Ext.getCmp(PF + 'txtAbaSwiftCuentas').setValue(registroSeleccionado[0].get("swift"))
		}
		else{
			Ext.getCmp(PF + 'optAbaSwiftCuentas').setValue(0);
			Ext.getCmp(PF + 'txtAbaSwiftCuentas').setValue(registroSeleccionado[0].get("aba"));
		}
		
		//Aba y Swift intermediario
		if (registroSeleccionado[0].get("swiftInter") != "") {
			Ext.getCmp(PF + 'optRadioIntermediario').setValue(1);
			Ext.getCmp(PF + 'txtAbaSwiftIntermediario').setValue(registroSeleccionado[0].get("swiftInter"));
		}
		else{
			Ext.getCmp(PF + 'optRadioIntermediario').setValue(0);
			Ext.getCmp(PF + 'txtAbaSwiftIntermediario').setValue(registroSeleccionado[0].get("abaInter"));
		}
		
		//Aba y swift corresponsal
		if (registroSeleccionado[0].get("swiftCorresp") != ""){
			Ext.getCmp(PF + 'optRadioCorresponsal').setValue(1);
			Ext.getCmp(PF + 'txtAbaSwiftCorresponsal').setValue(registroSeleccionado[0].get("swiftCorresp"));
		}
		else{
			Ext.getCmp(PF + 'optRadioCorresponsal').setValue(0);
			Ext.getCmp(PF + 'txtAbaSwiftCorresponsal').setValue(registroSeleccionado[0].get("abaCorresp"));
		}			
	};
	
	NS.ocultasCuentas = function(){
			 	
	 	NS.lblDivisaCuentas.setVisible(false),
	 	NS.lblChequeraCuentas.setVisible(false),
	 	NS.lblDescChequeraCuentas.setVisible(false),
	 	NS.lblSucursalCuentas.setVisible(false),
	 	NS.lblPlazaCuentas.setVisible(false),
	 	NS.lblClabeCuentas.setVisible(false),
	 	NS.lblBancoAnteriorCuentas.setVisible(false),
	 	NS.lblChequeraAnteriorCuentas.setVisible(false),
	 	NS.txtIdChequeraCuentas.setVisible(false),
	 	NS.txtDescChequeraCuentas.setVisible(false),
	 	NS.txtSucursalCuentas.setVisible(false),
	 	NS.txtPlazaCuentas.setVisible(false),
	 	NS.txtClabeCuentas.setVisible(false),
	 	NS.txtBancoAnteriorCuentas.setVisible(false),
	 	NS.txtChequeraAnteriorCuentas.setVisible(false),
	 	NS.cmbDivisaCuentas.setVisible(false),
	 	NS.panelRadioCuentas.setVisible(false),		 	
	 	NS.panelIntermediarioCorresponsalCuentas.setVisible(false),
	 	NS.panelAbaSwiftCuentas.setVisible(false)
	};
	
	NS.insertaModificaCuentas = function(){		
		var jSonStringCuentas = Ext.util.JSON.encode(matrizCuentas);				
		
		ConsultaPersonasAction.insertaActualizaCuentasProveedor(jSonStringCuentas, function(resultadoAceptar, e){
			if (resultadoAceptar != '' && resultadoAceptar != undefined && resultadoAceptar != null)
				Ext.Msg.alert('SET', resultadoAceptar);
		});
		NS.limpiaCuentasProveedor();
		NS.cierraCuentasProveedor();
	};
	

	
	NS.storeComboDivisaCuentas = new Ext.data.DirectStore({
		paramAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.comboDivisasCuentas,
		fields:
		[
		 	{name: 'idDivisa'}	 	
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboDivisaCuentas, msg: "Cargando..."});
				if (records.length == null || records <= 0)
					Ext.Msg.alert('SET', 'No hay datos en el catalogo de Divisas');
			}
		}
	});
	
	NS.storeComboBancoCuentas = new Ext.data.DirectStore({
		paramAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['nacionalidad'],
		directFn: ConsultaPersonasAction.comboBancoCuentas,
		fields:
		[
		 	{name: 'idBanco'},
		 	{name: 'descBanco'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboBancoCuentas, msg: "Cargando..."});
				if (records.length == null || records <= 0)
					Ext.Msg.alert ('SET', 'No existen Bancos de esta Nacionalidad');
			}
		}
			
	});
	
	NS.nacionalidad = "N";
	NS.storeComboBancoCuentas.baseParams.nacionalidad = NS.nacionalidad;
	NS.storeComboBancoCuentas.load();
	
	NS.storeComboBancoTrue = new Ext.data.DirectStore({
		paramAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['nacionalidad'],
		directFn: ConsultaPersonasAction.comboBancoCuentas,
		fields:
		[
		 	{name: 'idBanco'},
		 	{name: 'descBanco'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboBancoTrue, msg: "Cargando..."});
				if (records.length == null || records <= 0)
					Ext.Msg.alert ('SET', 'No existen Bancos de esta Nacionalidad');
			}
		} 			
	});
	
	NS.storeComboBancoCorresponsal = new Ext.data.DirectStore({
		paramAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['nacionalidad'],
		directFn: ConsultaPersonasAction.comboBancoCuentas,
		fields:
		[
		 	{name: 'idBanco'},
		 	{name: 'descBanco'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboBancoCorresponsal, msg: "Cargando..."});
				if (records.length == null || records <= 0)
					Ext.Msg.alert ('SET', 'No existen Bancos de esta Nacionalidad');
			}
		} 			
	});
	
	//COMBOBOX
	NS.cmbDivisaCuentas = new Ext.form.ComboBox({
		store: NS.storeComboDivisaCuentas,
		id: PF + 'cmbDivisaCuentas',
		name: PF + 'cmbDivisaCuentas',
		x: 270,
		y: 220,
		width: 60,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDivisa',
		displayField: 'idDivisa',
		autocomplete: true,
		emptyText: 'Divisa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor){
					
				}	
			}
		}
	});
	
	NS.cmbBancoCuentas = new Ext.form.ComboBox({
		store: NS.storeComboBancoCuentas,
		id: PF + 'cmbBancoCuentas',
		name: PF + 'cmbBancoCuentas',
		x: 60,
		y: 25,
		width: 160,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Bancos',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdBancoCuentas', NS.cmbBancoCuentas.getId());
				}
			}
		}
			
	});
	
	NS.cmbBancoTrueIntermediario = new Ext.form.ComboBox({
		store: NS.storeComboBancoTrue,
		id: PF + 'cmbBancoTrueIntermediario',
		name: PF + 'cmbBancoTrueIntermediario',
		x: 60,
		y: 15,
		width: 160,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Banco Intermediario',
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled: true,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdBancoIntermediarioCuentas', NS.cmbBancoTrueIntermediario.getId());
				}
			}
		}
	});
	
	NS.cmbBancoCorresponsal = new Ext.form.ComboBox({
		store: NS.storeComboBancoCorresponsal,
		id: PF + 'cmbBancoCorresponsal',
		name: PF + 'cmbBancoCorresponsal',
		x: 470,
		y: 15,
		width: 160,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Banco Corresponsal',
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled: true,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdBancoCorresponsalCuentas', NS.cmbBancoCorresponsal.getId());
				}
			}
		}
			
	});
	
	//GRID
	NS.columnaSeleccionCuentas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columnas de grid
	NS.columnasGridCuentas = new Ext.grid.ColumnModel([
	    {header: 'Id Banco', width: 60, dataIndex: 'idBanco', sortable: true},
	    {header: 'Desc Banco', width: 100, dataIndex: 'descBanco', sortable: true},
	    {header: 'Chequera', width: 100, dataIndex: 'chequera', sortable: true},
	    {header: 'Desc Chequera', width: 100, dataIndex: 'descChequera', sortable: true},
	    {header: 'Divisa', width: 40, dataIndex: 'divisa', sortable: true},
	    //{header: 'Grabado', width: 50, dataIndex: 'grabado', sortable: true, hidden: true},
	    //{header: 'Sucursal', width: 40, dataIndex: 'sucursal', sortable: true},
	    //{header: 'Plaza', width: 40, dataIndex: 'plaza', sortable: true},
	    {header: 'Clabe', width: 120, dataIndex: 'clabe', sortable: true},
	    //{header: 'Chequera Ant', width: 100, dataIndex: 'chequeraAnt', sortable: true, hidden: true},
	    //{header: 'Id Banco Ant', width: 60, dataIndex: 'idBancoAnt', sortable: true, hidden: true},
	    //{header: 'Usuario Modif', width: 30, dataIndex: 'usuarioModif', sortable: true, hidden: true},
	    //{header: 'Fec. Modif', width: 60, dataIndex: 'fecModif', sortable: true, hidden: true},
	    //{header: 'Empresa', width: 40, dataIndex: 'noEmpresa', sortable: true, hidden: true},
	    //{header: 'No Persona', width: 60, dataIndex: 'noPersona', sortable: true, hidden: true},    
	    {header: 'Chequera Benef', width: 100, dataIndex: 'chequeraBenef', sortable: true},
	    {header: 'SWIFT', width: 80, dataIndex: 'swift', sortable: true},
	    {header: 'ABA', width: 80, dataIndex: 'aba', sortable: true},
	    {header: 'Bank True', width: 60, dataIndex: 'bankTrue', sortable: true},
	    {header: 'Bank Corresponding', width: 60, dataIndex: 'bankCorresponding', sortable: true},
	   // {header: 'Nacionalidad', width: 40, dataIndex: 'nacionalidad', sortable: true, hidden: true},
	    {header: 'Chequera Bank True', width: 100, dataIndex: 'chequeraTrue', sortable: true},
	    {header: 'Banco Ant', width: 60, dataIndex: 'bancoAnterior', sortable: true},
	    {header: 'Chequera Ant', width: 100, dataIndex: 'chequeraAnterior', sortable: true},
	    {header: 'Referencia', width: 100, dataIndex: 'referencia', sortable: true},
	    {header: 'Banco Interlocutor', width: 100, dataIndex: 'bancoI', sortable: true},
	    {header: 'Especiales', width: 100, dataIndex: 'especial', sortable: true},
	   /* {header: 'Otro Banco', width: 30, dataIndex: 'otroBanco', sortable: true, hidden: true},
	    {header: 'Otra Chequera', width: 80, dataIndex: 'otraChequera', sortable: true, hidden: true},*/
	    //{header: 'Aba Inter', width: 80, dataIndex: 'abaInter', sortable: true, hidden: true},
	    //{header: 'Swift Inter', width: 80, dataIndex: 'swiftInter', sortable: true, hidden: true},
	    //{header: 'Aba Corresp', width: 80, dataIndex: 'abaCorresp', sortable: true, hidden: true},
	    //{header: 'Swift Corresp', width: 80, dataIndex: 'swiftCorresp', sortable: true, hidden: true},
	    //{header: 'Actualiza Chequera', width: 50, dataIndex: 'actualizaChequeraProv', sortable: true, hidden: true}
	]);
	
	NS.gridConsultaCuentas = new Ext.grid.GridPanel({
		store: NS.storeGridCuentasProveedor,
		id: PF + 'gridConsultaCuentas',
		name: PF + 'gridConsultaCuentas',
		cm: NS.columnasGridCuentas,
		sm: NS.columnaSeccionCuentas,
		width: 960,
		height: 100,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid){	
					var registroSeleccionado = NS.gridConsultaCuentas.getSelectionModel().getSelections();
					if (registroSeleccionado.length > 0){						
						NS.deshabilitaCamposProveedor();					
						NS.llenaCamposCuentas();			
					}
				}
			}
		}
	});
		
	//Radio Button
	NS.optAbaSwiftCuentas = new Ext.form.RadioGroup({
		id: PF + 'optAbaSwiftCuentas',
		name: PF + 'optAbaSwiftCuentas',
		x: 0,
		y: 0,
		height: 20,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Aba', name: 'optSeleccionAbaSwift', inputValue: 0, checked: true, width: 50},
		 	{boxLabel: 'Swift', name: 'optSeleccionAbaSwift', inputValue: 1, width: 50}
		]
	});
	
	NS.optRadioCuentas = new Ext.form.RadioGroup({
		id: PF + 'optRadioCuentas',
		name: PF + 'optRadioCuentas',
		x: 0, 
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Nacional', name: 'optSeleccionBanco', inputValue: 0, checked: true, width: 80,
		 		listeners: {
		 			check: {
		 				fn: function (opt, valor) {
		 					if (valor == true) {
		 						NS.nacionalidad = "N";
		 						
		 						NS.storeComboBancoCuentas.baseParams.nacionalidad = NS.nacionalidad;
			 					NS.storeComboBancoCuentas.load();			 
			 					NS.txtIdBancoIntermediarioCuentas.setDisabled(true),
			 				 	NS.txtChequeraIntermediarioCuentas.setDisabled(true),
			 				 	NS.txtIdBancoCorresponsalCuentas.setDisabled(true),
			 				 	NS.cmbBancoTrueIntermediario.setDisabled(true),
			 				 	NS.cmbBancoCorresponsal.setDisabled(true),
			 				 	NS.panelIntermediario.setDisabled(true),
			 				 	NS.panelCorresponsal.setDisabled(true)
			 				 	NS.txtIdBancoCuentas.setValue('');
			 					NS.cmbBancoCuentas.setValue('');
			 					//NS.panelIntermediarioCorresponsalCuentas.setDisabled(true);
			 					/*
			 					NS.storeComboBancoTrue.baseParams.nacionalidad = NS.nacionalidad;
			 					NS.storeComboBancoTrue.load();
			 					
			 					NS.storeComboBancoCorresponsal.baseParams.nacionalidad = NS.nacionalidad;
			 					NS.storeComboBancoCorresponsal.load();*/			 					
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{boxLabel: 'Extranjero', name: 'optSeleccionBanco', inputValue: 1, width: 80,
		 		listeners:
		 		{
					check: {
		 				fn: function (opt, valor) {
		 					if (valor == true) {
		 						NS.nacionalidad = "E";
		 						NS.storeComboBancoCuentas.baseParams.nacionalidad = NS.nacionalidad;
			 					NS.storeComboBancoCuentas.load();
			 					
			 					NS.storeComboBancoTrue.baseParams.nacionalidad = NS.nacionalidad;
			 					NS.storeComboBancoTrue.load();
			 					
			 					NS.storeComboBancoCorresponsal.baseParams.nacionalidad = NS.nacionalidad;
			 					NS.storeComboBancoCorresponsal.load();
			 					
			 					//NS.panelIntermediarioCorresponsalCuentas.setDisabled(false);
			 					NS.txtIdBancoIntermediarioCuentas.setDisabled(false),
			 				 	NS.txtChequeraIntermediarioCuentas.setDisabled(false),
			 				 	NS.txtIdBancoCorresponsalCuentas.setDisabled(false),
			 				 	NS.cmbBancoTrueIntermediario.setDisabled(false),
			 				 	NS.cmbBancoCorresponsal.setDisabled(false),
			 				 	NS.panelIntermediario.setDisabled(false),
			 				 	NS.panelCorresponsal.setDisabled(false)
			 				 	NS.txtIdBancoCuentas.setValue('');
			 					NS.cmbBancoCuentas.setValue('');
		 					}
		 				}
		 			}
		 		}
		 	}
		]
	});	
	
	NS.optRadioIntermediario = new Ext.form.RadioGroup({
		id: PF + 'optRadioIntermediario',
		name: PF + 'optRadioIntermediario',
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Aba', name: 'optAbaSwiftIntermediario', inputValue: 0, checked: true, width: 50},
		 	{boxLabel: 'Swift', name: 'optAbaSwiftIntermediario', inputValue: 1, width: 50}
		]
		
	});
	
	NS.optRadioCorresponsal = new Ext.form.RadioGroup({
		id: PF + 'optRadioCorresponsal',
		name: PF + 'optRadioCorresponsal',
		x: 0,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Aba', name: 'optAbaSwiftCorresponsal', inputValue: 0, checked: true, width: 50},
		 	{boxLabel: 'Swift', name: 'optAbaSwiftCorresponsal', inputValue: 1, width: 50}
		]
		
	});

	//PANEL
	NS.panelIntermediario = new Ext.form.FieldSet({
		title: 'Intermediario',
		x: 0,
		y: 40,
		width: 250,
		height: 45,
		layout: 'absolute',
		disabled: true,
		items:
		[
		 	NS.optRadioIntermediario,
		 	NS.txtAbaSwiftIntermediario
		]			
	});
	
	NS.panelCorresponsal = new Ext.form.FieldSet({
		title: 'Corresponsal',
		x: 410,
		y: 40,
		width: 250,
		height: 45,
		layout: 'absolute',
		disabled: true,
		items:
		[
		 	NS.optRadioCorresponsal,
		 	NS.txtAbaSwiftCorresponsal
		]
	});
	
	NS.panelIntermediarioCorresponsalCuentas = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 290,
		width: 960,
		height: 120,		
		layout: 'absolute',
		border: false,
		items:
		[
		 	NS.lblBancoIntermediarioCuentas,
		 	NS.lblChequeraIntermediarioCuentas,
		 	NS.lblBancoCorresponsalCuentas,
		 	NS.txtIdBancoIntermediarioCuentas,
		 	NS.txtChequeraIntermediarioCuentas,
		 	NS.txtIdBancoCorresponsalCuentas,
		 	NS.cmbBancoTrueIntermediario,
		 	NS.cmbBancoCorresponsal,
		 	NS.panelIntermediario,
		 	NS.panelCorresponsal
		]			
	});
	
	NS.panelAbaSwiftCuentas = new Ext.form.FieldSet({
		title: 'Beneficiario',
		x: 700,
		y: 250,
		width: 250,
		height: 43,		
		layout: 'absolute',
		items:
		[
		 	NS.optAbaSwiftCuentas,
		 	NS.txtAbaSwiftCuentas		 	
		]
	});
	
	NS.panelRadioCuentas = new Ext.form.FieldSet({
		title: 'Banco',
		x: 0,
		y: 200,
		width: 250,
		height: 75,
		layout: 'absolute',		
		items:
		[
		 	NS.optRadioCuentas,
		 	NS.txtIdBancoCuentas,		 	
		 	NS.cmbBancoCuentas
		]
	});
	
	NS.panelGridCuentas = new Ext.form.FieldSet({
		x: 0,
		y: 45,
		width: 960,
		height: 155,
		layout: 'absolute',
		items:
		[
		 	NS.gridConsultaCuentas,
		 	
		 	/*{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		x: 650,
		 		y: 110,
		 		width: 80, 
		 		height: 22,
		 		listeners:
		 		{
		 			click: function(e)
		 			{
		 				var registroSeleccionado = NS.gridConsultaCuentas.getSelectionModel().getSelections();
		 				
		 				if (registroSeleccionado.length <= 0)
		 					Ext.Msg.alert('SET', 'Debe de elegir un registro para modificar');		 					
		 				else{		 					
		 					NS.validaMovimientos(registroSeleccionado[0].get('chequera'));
		 					NS.modificarCuentas = true;
		 				}
		 					
		 					
		 			}
		 		}	 	
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 750,
		 		y: 110,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click: function(e)
		 			{
		 				NS.limpiaCuentasProveedor();
		 				NS.habilitaCamposProveedor();
		 				NS.modificarCuentas = false;
		 			}		 				
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 850,
		 		y: 110,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: function(e) {
		 				var registroSeleccionado = NS.gridConsultaCuentas.getSelectionModel().getSelections();
		 				
		 				if (registroSeleccionado.lenght <= 0)
		 					Ext.getCmp('SET', 'Debe de elegir un registro para eliminar');
		 				else
		 					NS.eliminaRegistroProveedor();
		 			}		 			
		 		}
		 	}*/
		]
	
	
	});
	
	NS.panelCuentasBancarias = new Ext.form.FieldSet({
		title: 'Mantenimiento De Cuentas Bancarias',
		x: 0,
		y: 0,
		width: 985,
		height: 425,
		layout: 'absolute',
		hidden: true,
		items:
		[
		 	//NS.lblBancoCuentas,
		 	NS.lblEmpresaCuentas,
		 	//NS.lblNoPersonaCuentas,
		 	NS.lblRazonSocialCuentas,		 	
		 	NS.lblDivisaCuentas,
		 	NS.lblChequeraCuentas,
		 	NS.lblDescChequeraCuentas,
		 	NS.lblSucursalCuentas,
		 	NS.lblPlazaCuentas,
		 	NS.lblClabeCuentas,
		 	NS.lblBancoAnteriorCuentas,
		 	NS.lblChequeraAnteriorCuentas,
		 	NS.txtEmpresaCuentas,
		 	//NS.txtNoPersonaCuentas,
		 	NS.txtRazonSocialCuentas,
		 	NS.txtIdChequeraCuentas,
		 	NS.txtDescChequeraCuentas,
		 	NS.txtSucursalCuentas,
		 	NS.txtPlazaCuentas,
		 	NS.txtClabeCuentas,
		 	NS.txtBancoAnteriorCuentas,
		 	NS.txtChequeraAnteriorCuentas,
		 	NS.cmbDivisaCuentas,
		 	NS.panelGridCuentas,
		 	NS.panelRadioCuentas,		 	
		 	NS.panelIntermediarioCorresponsalCuentas,
		 	NS.panelAbaSwiftCuentas
		 	
		 	
		]
	});
	
/*	///////////EDGARESTRADA////////////////////////////////////////////
	NS.panelBotonesCuentasBancarias = new Ext.form.FieldSet({
		x: 0,
		y: 425,
		width: 985,
		height: 43,
		layout: 'absolute',
		hidden: true,
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',		 		
		 		x: 400,
		 		y: 0,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
					 		NS.llenaVector();
							var jSonStringCuentas = Ext.util.JSON.encode(matrizCuentas);
		 					ConsultaPersonasAction.validaDatosCuentas(jSonStringCuentas, function(resultado, e){
		 						if (resultado = "" && resultado != undefined && resultado != null)
		 							NS.insertaModificaCuentas();
		 						else
		 							Ext.Msg.alert('SET', resultado);
		 					});		 					
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 500,
		 		y: 0,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 600,
		 		y: 0,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Exportar',
		 		x: 700,
		 		y: 0,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 			
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		x: 800,
		 		y: 0,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiaCuentasProveedor();
		 					NS.cierraCuentasProveedor();
		 				}
		 			}
		 		}
		 	}
		 	
		]
	});*/
	
	//******************************************************************************************************************************************
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1010,
		height: 520,
		layout: 'absolute',
		items:
		[
		 	NS.panelBusqueda,
		 	NS.panelGrid,
		 	/*{
		 		id: PF + 'btnImprimir',
		 		name: PF + 'btnImprimir',
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 760,
		 		y: 525,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{}
		 			}
		 		}
		 	},
		 	{
		 		id: PF + 'btnLimpiar',
		 		name: PF + 'btnLimpiar',
		 		xtype: 'button',
		 		text: 'Limpiar',
		 		x: 860,
		 		y: 525,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.limpiar();
		 				}
		 			}
		 		}
		 	},*/
		 	NS.panelCamposIB.setVisible(false),
		 	NS.panelBotonesIB.setVisible(false),
		 	NS.panelCamposCF.setVisible(false),
		 	NS.panelBotonesCF.setVisible(false),
		 	NS.panelCamposCM.setVisible(false),
		 	NS.panelBotonesCM.setVisible(false),
		 	NS.panelCamposF.setVisible(false),
		 	NS.panelBotonesF.setVisible(false),
			NS.panelCamposE.setVisible(false),
			NS.panelBotonesE.setVisible(false),
			NS.panelCamposPF.setVisible(false),
			NS.panelBotonesPF.setVisible(false),
			NS.panelCamposPM.setVisible(false),
			NS.panelBotonesPM.setVisible(false),
			NS.panelCamposK.setVisible(false),
			NS.panelCamposCP.setVisible(false),
			NS.panelCamposCP1.setVisible(false),
			NS.panelCamposGrid.setVisible(false),
			NS.panelBotonesK.setVisible(false),
			NS.panelCamposFI.setVisible(false),
			NS.panelBotonesFI.setVisible(false),
			NS.panelCamposDI.setVisible(false),
		 	NS.panelBotonesDI.setVisible(false),
		 	NS.panelCamposMC.setVisible(false),
		 	NS.panelBotonesMCE.setVisible(false),
		 	NS.panelBotonesMCIB.setVisible(false),
		 	NS.panelBotonesMCFI.setVisible(false),
		 	NS.panelBotonesDIFI.setVisible(false),
		 	NS.panelBotonesMCK.setVisible(false),
		 	NS.panelBotonesMCPF.setVisible(false),
		 	NS.panelBotonesMCPM.setVisible(false),
		 	NS.panelBotonesCB.setVisible(false),
		 	NS.panelBotonesCBM.setVisible(false),
		 	NS.panelBotonesMOE.setVisible(false),
		 	NS.panelBotonesDIM.setVisible(false),
		 	NS.panelBotonesMOK.setVisible(false),
		 	NS.panelBotonesMIB.setVisible(false),
		 	NS.panelBotonesMOF.setVisible(false),
			//****************************************** PANTALLA MANTENIMIENTO DE CUENTAS BANCARIAS ***************************************************
		 	NS.panelCuentasBancarias.setVisible(false),
		 	
		 	/*{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		id: PF + 'aceptar',
		 		name: PF + 'aceptar',
		 		hidden: true,
		 		x: 400,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
					 		NS.llenaVector();
					 		
							var jSonStringCuentas = Ext.util.JSON.encode(matrizCuentas);
		 					ConsultaPersonasAction.validaDatosCuentas(jSonStringCuentas, function(resultado, e){
		 						if (resultado == "" && resultado != undefined && resultado != null)
		 							NS.insertaModificaCuentas();
		 						else
		 							Ext.Msg.alert('SET', resultado);
		 						
		 						NS.storeGridCuentasProveedor.removeAll();
		 						NS.storeGridCuentasProveedor.baseParams.noPersona = NS.noPersona,
			 					NS.storeGridCuentasProveedor.baseParams.noEmpresa = NS.noEmpresa,
			 					NS.storeGridCuentasProveedor.baseParams.tipoPersona = NS.tipoPersona,
			 					NS.storeGridCuentasProveedor.load();
		 						NS.limpiaCuentasProveedor();
		 					});	 					
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		id: PF + 'cancelar',
		 		name: PF + 'cancelar',
		 		hidden: true,
		 		x: 500,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiaCuentasProveedor();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		id: PF + 'imprimir',
		 		name: PF + 'imprimir',
		 		hidden: true,
		 		x: 600,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Exportar',
		 		id: PF + 'exportar',
		 		name: PF + 'exportar',
		 		hidden: true,
		 		x: 700,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 			
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		id: PF + 'regresar',
		 		name: PF + 'regresar',
		 		hidden: true,
		 		x: 800,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiaCuentasProveedor();
		 					NS.cierraCuentasProveedor();
		 				}
		 			}
		 		}
		 	}*/
		 ]
	});
	
	 /*************************************** INICIO DE PANTALLA DE RELACIONES ***************************************
	 * 
	 * ***************************************************************************************************************/
	
	NS.insertarActualizarR = function(){
 		var vector = {};
		var matriz = new Array();
		if(NS.tipoPersona == 'B'){
		vector.noPersona = Ext.getCmp(PF + 'txtNoPersonaIB').getValue();
		}else{
		vector.noPersona = Ext.getCmp(PF + 'txtNoPersonaK').getValue();
		}
		vector.fechaHoy=NS.fecHoy;
		vector.noPersonaRel = Ext.getCmp(PF + 'txtPersonas').getValue();
		vector.tipoRelacion = Ext.getCmp(PF + 'cmbRelacion').getValue();
		vector.fecha = NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFecha').getValue());
		vector.cuenta = Ext.getCmp(PF + 'txtCuenta').getValue();
	//	vector.cuenta = Ext.getCmp(PF + 'txtCuenta').get Value() == '' ? '0' : Ext.getCmp(PF + 'txtCuenta').getValue();
		vector.usuario = apps.SET.iUserId;
		
	
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
				
					ConsultaPersonasAction.guardarRelacion(jSonString, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
					{
						Ext.Msg.alert('SET', resultado);
						
						if(resultado == 'Ocurrio un error al almacenar el registro'){
							Ext.Msg.alert('SET', resultado);
						}
						NS.funCancelar();
						NS.funBuscar();
						NS.fieldAgregarDatos.setDisabled(true);
					}			
				});					
 	};
 
	
	NS.funAgregar = function() {
		alert("Entra a funcion agregar");
		var vec = new Array();
		var datos = {};
		
//		datos.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF + 'txtNoEmpresa').getValue();
//		alert("1");
//		datos.noPersona = Ext.getCmp(PF + 'txtNoPersonaGral').getValue();
		datos.noPersonaRel = Ext.getCmp(PF + 'txtPersonas').getValue();
		datos.tipoRelacion = Ext.getCmp(PF + 'cmbRelacion').getValue();
		datos.fecha = Ext.getCmp(PF + 'txtFecha').getValue();
		datos.cuenta = Ext.getCmp(PF + 'txtCuenta').getValue() == '' ? '0' : Ext.getCmp(PF + 'txtCuenta').getValue();
		datos.usuario = apps.SET.iUserId;
		vec[0] = datos;
		
		var cadJson = Ext.util.JSON.encode(vec);
		
		ConsultaPersonasAction.altaRelaciones(cadJson, function(res, e) {
			Ext.Msg.alert('SET', res);
			NS.funCancelar();
			NS.funBuscar();
			NS.fieldAgregarDatos.setDisabled(true);
		});
	};
	
	NS.funCancelar = function() {
		NS.txtPersonas.reset();
		NS.cmbPersonas.reset();
		NS.cmbRelacion.reset();
		NS.txtFecha.setValue(apps.SET.FEC_HOY);
		NS.txtCuenta.reset();
	};
	
	NS.funBuscar = function() {
		NS.storeDatos.baseParams.noPersona = parseInt(Ext.getCmp(PF + 'txtNoPersonaIB').getValue());
		NS.storeDatos.baseParams.noEmpresa = Ext.getCmp(PF + 'txtEmpresaIB').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF + 'txtEmpresaIB').getValue();
		NS.storeDatos.load();
	};
	
	NS.funEliminar = function() {
		var recordsEliminar = NS.gridDatos.getSelectionModel().getSelections();
		var matDatos = new Array();
		
		Ext.Msg.confirm('SET','¿Esta seguro de eliminar '+recordsEliminar.length+' registro(s)?',function(btn) {  
			if(btn === 'yes') {
				for(var i=0; i<recordsEliminar.length; i++) {
					var datos = {};
					datos.noEmpresa = recordsEliminar[i].get('noEmpresa');
					datos.noPersona = recordsEliminar[i].get('noPersona');
					datos.noPersonaRel = recordsEliminar[i].get('noPersonaRel');
					matDatos[i] = datos;
				}
				var cadJson = Ext.util.JSON.encode(matDatos);
				
				ConsultaPersonasAction.eliminarRelaciones(cadJson, function(res, e){
					Ext.Msg.alert('SET', res);
					NS.gridDatos.store.removeAll();
					NS.gridDatos.getView().refresh();
					NS.funBuscar();
				});
			}
		});
	};
	
	//Store Datos donde se muestran los valores seleccionados
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {noPersona: '0', noEmpresa: '0'},
		root: '',
		paramOrder: ['noPersona', 'noEmpresa'],
		directFn: ConsultaPersonasAction.consultarRelaciones,
		fields: [
			{name: 'noEmpresa'},
			{name: 'noPersona'},
			{name: 'noPersonaRel'},
			{name: 'paterno'},
			{name: 'materno'},
			{name: 'nombre'},
			{name: 'descTipoRelacion'},
			{name: 'fechaIngreso'},
			{name: 'cuenta'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
			}
		}
	});
	
	//Para indicar el modo de seleccion de los registros en el Grid 
	NS.tipoSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columnaSelec = new Ext.grid.ColumnModel([
	    NS.tipoSeleccion,
	    {header: 'No. Persona', width: 100, dataIndex: 'noPersonaRel', sortable: true},
	    {header: 'Paterno', width: 160, dataIndex: 'paterno', sortable: true},
	    {header: 'Materno', width: 160, dataIndex: 'materno', sortable: true},
	    {header: 'Nombre', width: 160, dataIndex: 'nombre', sortable: true},
	    {header: 'Tipo Relación', width: 110, dataIndex: 'descTipoRelacion', sortable: true},
	    {header: 'Fecha', width: 110, dataIndex: 'fechaIngreso', sortable: true},
	    {header: 'Cuenta', width: 100, dataIndex: 'cuenta', sortable: true}
	]);
	
	//Grid para mostrar los datos seleccionados
	NS.gridDatos = new Ext.grid.GridPanel({
		store: NS.storeDatos,
		id: PF + 'gridDatos',
		cm: NS.columnaSelec,
		sm: NS.tipoSeleccion,
		x: 10,
		y: 20,
		width: 970,
	    height: 250,
	    stripeRows: true,
	    columnLines: true,
	    frame:true,
		listeners: {
			dblclick: {
				fn:function(grid) {
					NS.funElimiar();
				}
			}
		}
	});
	
	//Etiqueta Personas combo
	NS.labPersonas = new Ext.form.Label({
		text: 'Personas',
        x: 10,
        y: 0
	});
	
    //Caja numero de persona
	NS.txtPersonas = new Ext.form.TextField({
		id: PF + 'txtPersonas',
        name:PF + 'txtPersonas',
		x: 10,
        y: 15,
        width: 50,
        tabIndex: 0,
        listeners:{
			change:{
				fn: function(caja,valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtPersonas', NS.cmbPersonas.getId());
                }
			}
    	}
    });
	
	//Store personas
    NS.storePersonas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: { condicion: '' ,todos: true},
		paramOrder:['condicion','todos'],
		directFn: ConsultaPersonasAction.obtenerPersonas, 
		idProperty: 'noPersona', 
		fields: [
			 {name: 'noPersona'},
			 {name: 'nombre'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePersonas, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen personas dadas de alta para relacionar');
			}
		}
	});
//	NS.storePersonas.load();
	
	//Combo Personas
    NS.cmbPersonas = new Ext.form.ComboBox({
    	store: NS.storePersonas,
		name: PF + 'cmbPersonas',
		id: PF + 'cmbPersonas',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		pageSize: 10,
      	x: 65,
        y: 15,
        width: 250,
        tabIndex: 4,
		valueField: 'noPersona',
		displayField: 'nombre',
		autocomplete: true,
		emptyText: 'Capture al menos una letra para la busqueda',
		triggerAction: 'all',
		value: '',
		visible: false,
		editable: true,
		listeners: {
			select: {
				fn:function(combo, valor) {
    				BFwrk.Util.updateComboToTextField(PF + 'txtPersonas', NS.cmbPersonas.getId());
				}
			},
			beforequery: function(qe) {
				NS.paramCmbPersonas = qe.combo.getRawValue();
			}
		}
	});
	
    //Etiqueta relaciones combo
	NS.labRelacion = new Ext.form.Label({
		text: 'Tipo de Relaciones',
        x: 350,
        y: 0
	});
	
    NS.storeRelacion = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultaPersonasAction.obtenerRelaciones, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRelacion, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen relaciones dadas de alta');
			}
		}
	});
    NS.storeRelacion.load();
    
	//combo Relacion
	NS.cmbRelacion = new Ext.form.ComboBox({
		store: NS.storeRelacion,
		name: PF + 'cmbRelacion',
		id: PF + 'cmbRelacion',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		x: 350,
		y: 15,
		width :150,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Tipos de Relaciones',
		triggerAction: 'all'
	});
	
	//Etiqueta fecha
	NS.labFecha = new Ext.form.Label({
		text: 'Fecha',
        x: 520,
        y: 0
	});
	
	NS.txtFecha = new Ext.form.DateField({
		id: PF + 'txtFecha',
		name: PF + 'txtFecha',
		x: 520,
		y: 15,
		width: 100,
		format: 'd-m-Y',
		value: apps.SET.FEC_HOY
	});
	
	//Etiqueta cuenta
	NS.labCuenta = new Ext.form.Label({
		text: 'Cuenta',
        x: 640,
        y: 0
	});
	
	//Caja cuenta
	NS.txtCuenta = new Ext.form.TextField({
		id: PF + 'txtCuenta',
        name:PF + 'txtCuenta',
		x: 640,
        y: 15,
        width: 70,
        tabIndex: 0
    });
	
	NS.fieldAgregarDatos = new Ext.form.FieldSet ({
		title: '',
		x: 10,
		y: 330,
		width: 970,
		height: 80,
		layout: 'absolute',
		disabled: true,
		items:
			[
			 NS.labPersonas,
			 NS.txtPersonas,
			 NS.cmbPersonas,
			 NS.labRelacion,
			 NS.cmbRelacion,
			 NS.labFecha,
			 NS.txtFecha,
			 NS.labCuenta,
			 NS.txtCuenta,
			 {
				 xtype: 'button',
                 text: '...',
                 x: 320,
                 y: 15,
                 width: 20,
                 height: 22,
                 id: PF + 'btnCmbPersonas',
                 name: PF + 'btnCmbPersonas',
                 listeners: {
                 	click: {
                 		fn:function(e) {
				 			if(NS.paramCmbPersonas == '')
				 				Ext.Msg.alert('SET','Es necesario agregar una letra o nombre');
				 			else {
				 				NS.storePersonas.baseParams.condicion = NS.paramCmbPersonas;
				 				NS.storePersonas.baseParams.todos = false;
	                 			NS.storePersonas.load();
	                 		}
                		}
            		}
        		}
    		},{
		 		xtype: 'button',
		 		text: 'Agregar',
		 		id: PF + 'agregar',
		 		name: PF + 'agregar',
		 		x: 760,
		 		y: 15,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.insertarActualizarR();
		 				}
		 			}
		 		}
		 	},{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		id: PF + 'cancelara',
		 		name: PF + 'cancelara',
		 		x: 850,
		 		y: 15,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.funCancelar();
		 					NS.fieldAgregarDatos.setDisabled(true);
		 				}
		 			}
		 		}
		 	}
		 	]
	});
	
	NS.fieldRelaciones = new Ext.form.FieldSet ({
		title: 'RELACIONES',
		x: 20,
		width: 1010,
		height: 490,
		layout: 'absolute',
		hidden: true,
		items:
		[
		 	NS.gridDatos,
		 	{
		 		xtype: 'button',
		 		text: 'Nuevo',
		 		id: PF + 'btnNuevo',
		 		name: PF + 'btnNuevo',
		 		x: 690,
		 		y: 290,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.fieldAgregarDatos.setDisabled(false);
					 	}
		 			}
		 		}
		 	},{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		id: PF + 'btnEliminar',
		 		name: PF + 'btnEliminar',
		 		x: 780,
		 		y: 290,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.funEliminar();
		 				}
		 			}
		 		}
		 	},{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		id: PF + 'btnRegresar',
		 		name: PF + 'btnRegresar',
		 		x: 870,
		 		y: 290,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.global.setVisible(true);
		 					NS.fieldRelaciones.setVisible(false);
		 				}
		 			}
		 		}
		 	},NS.fieldAgregarDatos
	    ]
	});

	
	/**************************************** FIN DE PANTALLA DE RELACIONES ******************************************
	 * 
	 * ***************************************************************************************************************/
	
	
	NS.consultaPersonas = new Ext.FormPanel ({
		title: 'Consulta De Personas',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'consultaPersonas',
		name: PF + 'consultaPersonas',
		renderTo: NS.tabContId,
		items: [
		 	NS.global,
		 	NS.fieldRelaciones
		]
	});
	
	NS.consultaPersonas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
	NS.lblNoPersonaGral = new Ext.form.Label({
		text: 'No. Persona',
		hidden: true,
		x: 0,
		y: 50
	});

	/*NS.institucionesBancarias = new Ext.FormPanel ({
		title: 'Instituciones Bancarias',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'institucionesBancarias',
		name: PF + 'institucionesBancarias',
		renderTo: NS.tabContId,
		items: [
{
		xtype: 'button',
		text: 'Nuevo',
		id: PF + 'btnNuevo',
		name: PF + 'btnNuevo',
		x: 780,
		y: 290,
		width: 80,
		height: 22,
		listeners: {
			click: {
				fn: function(e) {
					console.log("no sirve para nada");
		 	}
			}
		}
	},
	NS.lblNoPersonaGral,
		]
	});*/


});
 