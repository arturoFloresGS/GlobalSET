Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.GrupoEmpresas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecha = NS.fecHoy;
	NS.configuraSET = false;
	
	/*FALTANTES**********************************************************************************************************
	/Hace falta agregar esta validacion de facultad en el load
	 * 
		 If Trim(gobjVarGlobal.valor_Configura_set(240)) = "SI" Then
            If gobjSeguridad.ValidaFacultad("NIVELAUT", sMensaje) Then
                .Enabled = True
              Else
                .Enabled = False
            End If
            .AddItem "Sin Autorizar", 0
            .AddItem "Nivel 1", 1
            .AddItem "Nivel 2", 2
            .AddItem "Nivel 3", 3
          Else
            .Visible = False
        End If
        
     *****Falta reporte en boton imprimir
	 */
	
	//LABEL
	NS.lblGrupo = new Ext.form.Label({
		text: 'Grupo',
		x: 0,
		y: 0
	});
	
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 50
	});
	
	NS.lblNivelAutorizacion = new Ext.form.Label({
		text: 'Nivel Autorización',
		x: 320,
		y: 0
	});
	
	NS.lblCorreoEmpresa = new Ext.form.Label({
		text: 'Correo Empresa',
		x: 320,
		y: 50
	});
	
	NS.lblRemitenteCorreo = new Ext.form.Label({
		text: 'Remitente Correo',
		x: 550,
		y: 0
	});
	
	//TEXTFIELD
	NS.txtCorreoEmpresa = new Ext.form.TextField({
		id: PF + 'txtCorreoEmpresa',
		name: PF + 'txtCorreoEmpresa',
		x: 320,
		y: 65,
		width: 200,
		disabled: true
	});
	
	NS.txtRemitenteCorreo = new Ext.form.TextField({
		id: PF + 'txtRemitenteCorreo',
		name: PF + 'txtRemitenteCorreo',
		x: 550,
		y: 15,
		width: 200,
		disabled: true
	});
	
	//FUNCIONES
	NS.buscar = function(){		
		if (Ext.getCmp(PF + 'chkTodas').getValue() == true)
			NS.todo = 1;
		else
			NS.todo = 0;
		
		NS.idGrupo = Ext.getCmp(PF + 'cmbGrupo').getValue();		
		NS.noEmpresa = Ext.getCmp(PF + 'cmbEmpresa').getValue();		
				
		//Se valida que seleccione al menos la empresa o el grupo cuando no su usa el checkbox
		if (NS.todo == 0 && NS.noEmpresa == '' && NS.idGrupo == '')
			Ext.Msg.alert('SET', 'Se debe seleccionar la Empresa o el Grupo');
		else if (NS.todo == 1){			
			NS.storeLlenaGrid.baseParams.noEmpresa = 0;
			NS.storeLlenaGrid.baseParams.idGrupo = 0;
			NS.storeLlenaGrid.baseParams.todo = parseInt(NS.todo);
			NS.storeLlenaGrid.load();
		}
		else{					
			
			if (parseInt(NS.noEmpresa))
				NS.noEmpresa = parseInt(NS.noEmpresa) 
			else
				NS.noEmpresa = 0;
			
			if(parseInt(NS.idGrupo))
				NS.idGrupo = parseInt(NS.idGrupo);
			else
				NS.idGrupo = 0;
			
			NS.storeLlenaGrid.baseParams.noEmpresa = NS.noEmpresa;
			NS.storeLlenaGrid.baseParams.idGrupo = NS.idGrupo;
			NS.storeLlenaGrid.baseParams.todo = NS.todo;
			NS.storeLlenaGrid.load();
		}
	};
	
	NS.limpiar = function(todo){
		NS.cmbGrupo.reset();
		NS.cmbNivelAutorizacion.reset();
		NS.cmbEmpresa.reset();
		Ext.getCmp(PF + 'txtCorreoEmpresa').setValue('');
		Ext.getCmp(PF + 'txtRemitenteCorreo').setValue('');
		Ext.getCmp(PF + 'chkTodas').setValue(false);
		
		if (todo = 1){
			NS.gridConsulta.store.removeAll();
			NS.gridConsulta.getView().refresh();
		}	
	};
	
	NS.insertaRegistro = function(){		
		var vector = {};
		var matriz = new Array();
		
		vector.idGrupo = Ext.getCmp(PF + 'cmbGrupo').getValue();
		vector.noEmpresa = Ext.getCmp(PF + 'cmbEmpresa').getValue();
		
		matriz[0] = vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		
		GrupoEmpresasAction.insertaRegistro(jSonString, function(resultado, e){
			if (resultado != 0 && resultado != '' && resultado != undefined){
				Ext.Msg.alert('SET', resultado);
				NS.buscar();
			}				
		});		
	};
	
	NS.eliminar = function(){	
		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		var idGrupo = 0;
		var noEmpresa = 0;
		
		Ext.Msg.confirm('SET', '¿Esta seguro de eliminar el registro?', function(btn){
			if (btn === 'yes')
			{
				idGrupo = registroSeleccionado[0].get('idGrupo');
				noEmpresa = registroSeleccionado[0].get('noEmpresa');
				
				GrupoEmpresasAction.eliminaRegistro(idGrupo, noEmpresa, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined){
						Ext.Msg.alert('SET', 'Registro Eliminado');
						NS.buscar();
					}
					else
						Ext.Msg.alert('SET', 'Ocurrion un error durante el proceso');
				});
			}
		});
	};
	
	NS.obtieneCorreo = function(){		
		idGrupo = Ext.getCmp(PF + 'cmbGrupo').getValue();
		GrupoEmpresasAction.obtieneCorreo(idGrupo, function(resultado, e){
			if (resultado != 0 && resultado != '' && resultado != undefined)
			{
				Ext.getCmp(PF + 'txtCorreoEmpresa').setValue(resultado.correoEmpresa);
				Ext.getCmp(PF + 'txtRemitenteCorreo').setValue(resultado.remitenteCorreo);
				
				if (NS.configuraSet){
					Ext.getCmp(PF + 'cmbNivelAutorizacion').setDisabled(false);
					Ext.getCmp(PF + 'cmbNivelAutorizacion').setValue(resultado.nivelAutorizacion);
				}
			}
		});
	};
	
	NS.modificaNivel = function(){
		Ext.Msg.confirm('SET', 'Ha modificado el nivel de autorización para el grupo de empresas actualmente seleccionado, ¿Esta seguro(a) de cambiarlo?, el cambiar este nivel de autorización puede provocar que propuestas de pago actualmente autorizadas, queden desautorizadas por el cambio de nivel o bien propuestas con alto nivel de autorización queden autorizadas y puedan pagarse. \n Se le recomienda que primero verifique las propuestas actuales. \n Presione aceptar para realizar el cambio.', function(btn){
			if (btn === 'yes')
			{
				GrupoEmpresasAction.cambiaNivel(parseInt(Ext.getCmp(PF + 'cmbGrupo').getValue()), 
												parseInt(Ext.getCmp(PF + 'cmbNivelAutorizacion').getValue()), function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado != undefined)
						Ext.Msg.alert('SET', resultado);
				});
			}
		});		
	};
	
	
	//STORE
	NS.storeLlenaComboGrupo = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: GrupoEmpresasAction.llenaComboGrupo,
		fields:
		[
		 	{name: 'idGrupo'},
		 	{name: 'descGrupo'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboGrupo, msg: "Cargando información..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Grupos dados de alta');				
			}
		}
	});
	NS.storeLlenaComboGrupo.load();
	
	GrupoEmpresasAction.configuraSet(240, function (resultado, e){
		//Aqui hace falta la validacion de facultad
		if (resultado == 'SI'){
			NS.configuraSet = true;
			Ext.getCmp(PF + 'cmbNivelAutorizacion').setDisabled(false);			
			NS.storeNivelAutorizacion.loadData(NS.datosCombo);					
		}
		else{
			NS.configuraSet = false;
			Ext.getCmp(PF + 'cmbNivelAutorizacion').setDisabled(true);
		}
			
	});
	
	//Datos fijos para el combo de Nivel de Autorizacion
	NS.datosCombo = [
	    ['0', 'Sin Autorizar'],
	    ['1', 'Nivel 1'],
	    ['2', 'Nivel 2'],
	    ['3', 'Nivel 3']		 
	];	      		
	
	NS.storeNivelAutorizacion = new Ext.data.SimpleStore({
		idProperty: 'idNivel',
		fields:
		[
		 	{name: 'idNivel'},
		 	{name: 'descNivel'}
		]
	});
	
	
	NS.storeLlenaComboEmpresa = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: GrupoEmpresasAction.llenaComboEmpresa,
		fields:
		[
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboEmpresa, msg: "Cargando empresas..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Empresas dadas de alta');
			}
		}
	});
	NS.storeLlenaComboEmpresa.load();
	
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {noEmpresa: NS.noEmpresa, idGrupo: NS.idGrupo, todo: NS.todo},
		paramOrder: ['noEmpresa', 'idGrupo', 'todo'],
		directFn: GrupoEmpresasAction.llenaGrid,
		fields:
		[
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'},
		 	{name: 'nuevo'},
		 	{name: 'nuevoGrupo'},
		 	{name: 'idGrupo'},
		 	{name: 'descGrupo'},
		 	{name: 'correoEmpresa'},
		 	{name: 'remitenteCorreo'}
		],
		listeners:
		{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Buscando información..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existe información con estos parametros');
			}	
		}
	});
	
	//COMBOBOX
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeLlenaComboGrupo,
		id: PF + 'cmbGrupo',
		name: PF + 'cmbGrupo',
		hiddenName: PF + 'oculto',
		width: 300,
		x: 0,
		y: 15,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Seleccione un grupo',
		triggerAction: 'all',
		value: '',
		visible: false,		
		listeners:
		{
			select:
			{
				fn: function(combo, valor){					
					NS.obtieneCorreo();
				}
			}
		}		
	});
	
	//Este combo se llena de forma manual (datos fijos)
	NS.cmbNivelAutorizacion = new Ext.form.ComboBox({
		store: NS.storeNivelAutorizacion,
		id: PF + 'cmbNivelAutorizacion',
		name: PF + 'cmbNivelAutorizacion',
		x: 320,
		y: 15,
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		valueField: 'idNivel',
		displayField: 'descNivel',
		autocomplete: true,
		emptyText: 'Nivel Autorización',
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled: true,
		listeners: {
			select: {
				fn: function(combo, valor) {
					NS.modificaNivel();					
				}
			}
		}		
	});
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeLlenaComboEmpresa,
		id: PF + 'cmbEmpresa',
		name: PF + 'cmbEmpresa',
		x: 0,
		y: 65,
		width: 300,
		typeAhead: true,
		mode: 'local',
		selecOnfocus: true,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
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
	
	
	//GRID
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGrid = new Ext.grid.ColumnModel
	([
	  	{header: 'No. Empresa', width: 60, dataIndex: 'noEmpresa', sortable: true},
	  	{header: 'Empresa', width: 300, dataIndex: 'nomEmpresa', sortable: true},
	  	{header: 'Nuevo', width: 50, dataIndex: 'nuevo', sortable: true, hidden: true},
	  	{header: 'Grupo Nuevo', width: 50, dataIndex: 'nuevoGrupo', sortable: true, hidden: true},
	  	{header: 'Id Grupo', width: 60, dataIndex: 'idGrupo', sortable: true},
	  	{header: 'Grupo', width: 120, dataIndex: 'descGrupo', sortable: true},
	  	{header: 'Correo Empresa', width: 200, dataIndex: 'correoEmpresa', sortable: true},
	  	{header: 'Remitente Correo', width: 200, dataIndex: 'remitenteCorreo', sortable: true}	  	
	]);
	
	NS.gridConsulta = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'gridConsulta',
		name: PF + 'gridConsulta',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		x: 0,
		y: 0,
		width: 960,
		height: 260,
		stripRows: true,
		columnLines: true,
		listeners:
		{
			click:
			{
				fn: function(grid)
				{
					
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
		height: 130,
		layout: 'absolute',
		items:
		[
		 	NS.lblGrupo,
		 	NS.lblEmpresa,
		 	NS.lblNivelAutorizacion,
		 	NS.lblCorreoEmpresa,
		 	NS.lblRemitenteCorreo,
		 	NS.txtCorreoEmpresa,
		 	NS.txtRemitenteCorreo,
		 	NS.cmbGrupo,
		 	NS.cmbNivelAutorizacion,
		 	NS.cmbEmpresa,
		 	{
		 		xtype: 'checkbox',		 		
		 		id: PF + 'chkTodas',
		 		name: PF + 'chkTodas',
		 		x: 600,
		 		y: 65,
		 		boxLabel: 'Todas',
		 		listeners: {
		 			check: {
		 				fn: function(checkbox, valor) {
		 		
		 				}
		 			}
		 		}	
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 850,
		 		y: 30,
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
	
	NS.panelGrid = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 140,
		width: 985,
		height: 280,
		layout: 'absolute',
		items:
		[
		 	NS.gridConsulta
		]
	});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1010,		
		height: 490,
		layout: 'absolute',
		items:
		[				 	
		 	NS.panelBusqueda,
		 	NS.panelGrid,
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		hidden: true,
		 		x: 400,
		 		y: 440,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Ejecutar',
		 		id: PF + 'cmbEjecutar',
		 		name: PF + 'cmbEjecutar',
		 		x: 500,
		 		y: 440,
		 		width: 80,
		 		height: 22,
		 		disabled: true,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.insertaRegistro();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 600,
		 		y: 440,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					Ext.getCmp(PF + 'cmbEjecutar').setDisabled(false);
		 					NS.modificar = false;
		 					NS.limpiar(0);
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 700,
		 		y: 440,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		 					
		 					if (registroSeleccionado.length > 0)
		 						NS.eliminar();
		 					else
		 						Ext.Msg.alert('SET', 'Debe seleccionar un registro');
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Limpiar',
		 		x: 800,
		 		y: 440,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiar(1);
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Excel',
		 		x: 900,
		 		y: 440,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					parametros='?nomReporte=excelGrupoEmpresas';
 							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);	
		 				}
		 			}
		 		}
		 	},
	    ]
	});
	
	NS.grupoEmpresas = new Ext.FormPanel ({
		title: 'Grupo de Empresas',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'grupoEmpresas',
		name: PF + 'grupoEmpresas',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	NS.grupoEmpresas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});
 