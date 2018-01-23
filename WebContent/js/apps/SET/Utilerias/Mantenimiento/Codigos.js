Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.Codigos');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	
	//LABEL
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 0
	});
	
	//LABEL DE ALTA DE CODIGO
	NS.lblCodigo = new Ext.form.Label({
		text: 'Descripción De Código',
		x: 0,
		y: 0
	});
	
	//TEXFIELD
	NS.txtEmpresa = new Ext.form.TextField({
		id: PF + 'txtEmpresa',
		name: PF + 'txtEmpresa',
		x: 0,
		y: 15,
		width: 50,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}
	});
	
	//TEXTFIELD PARA ALTA
	NS.txtIdCodigo = new Ext.form.TextField({
		id: PF + 'txtIdCodigo',
		name: PF + 'txtIdCodigo',
		x: 0,
		y: 15,
		width: 50		
	});
	
	NS.txtDescCodigo = new Ext.form.TextField({
		id: PF + 'txtDescCodigo',
		name: PF + 'txtDescCodigo',
		x: 60,
		y: 15,
		width: 300
	});
	
	//FUNCIONES
	NS.buscar = function(){
		NS.gridConsulta.store.removeAll();
		NS.gridConsulta.getView().refresh();
		
		NS.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
		
		if (NS.noEmpresa == 0)
			NS.noEmpresa = 0;
		
		NS.storeLlenaGrid.baseParams.noEmpresa = parseInt(NS.noEmpresa);		
		NS.storeLlenaGrid.load();		
	};
	
	NS.limpiar = function(todo){		
		NS.panelAlta.setDisabled(true);
		Ext.getCmp(PF + 'txtIdCodigo').setValue('');
		Ext.getCmp(PF + 'txtDescCodigo').setValue('');
		
		if (todo == 1){ //Limpia toda la pantalla
			Ext.getCmp(PF + 'txtEmpresa').setValue('');
			NS.cmbEmpresa.reset();
			NS.gridConsulta.store.removeAll();
			NS.gridConsulta.getView().refresh();
		}
	};
		
	NS.eliminar = function(){
		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		var idCodigo = 0;
		var noEmpresa = 0;
		
		Ext.Msg.confirm('SET', '¿Esta seguro de eliminar el Código seleccionado?', function (btn){
			if (btn === 'yes'){
				idCodigo = registroSeleccionado[0].get('idCodigo');
				noEmpresa = parseInt(registroSeleccionado[0].get('noEmpresa'));
				
				CodigosAction.eliminaCodigo(idCodigo, noEmpresa, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado && undefined){
						Ext.Msg.alert('SET', resultado);						
						NS.buscar();
					}
					else{
						Ext.Msg.alert('SET', resultado);
						NS.buscar();
					}
				});
			}
		});
	};
	
	NS.insertaCodigo = function(){
		var idGrupo = "";
		var descGrupo = "";
		
		idGrupo = Ext.getCmp(PF + 'txtIdCodigo').getValue();
		descGrupo = Ext.getCmp(PF + 'txtDescCodigo').getValue();
		NS.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
		if (NS.noEmpresa == '')
			NS.noEmpresa = apps.SET.NO_EMPRESA;
			
			
		if (idGrupo != '' && descGrupo != '')			
			CodigosAction.insertaCodigo(idGrupo, descGrupo, parseInt(NS.noEmpresa),function(resultado, e){
				if (resultado != 0 && resultado != '' && resultado != undefined){
					Ext.Msg.alert('SET', resultado);
					NS.buscar();
				}
				else
					Ext.Msg.alert('SET', resultado);
			});
		else
			Ext.Msg.alert('SET', 'Los datos No estan completos');
	};
	
	//STORE
	NS.storeLlenaComboEmpresa = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: CodigosAction.llenaComboEmpresas,
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
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Empresas dadas de alta');			
			}
		}
	});
	NS.storeLlenaComboEmpresa.load();
	
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {noEmpresa: NS.noEmpresa},
		paramOrder: ['noEmpresa'],
		directFn: CodigosAction.llenaGrid,
		fields:
		[
		 	{name: 'noEmpresa'},
		 	{name: 'idCodigo'},
		 	{name: 'descCodigo'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando información..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existe información para estos parametros');
			}
		}
	});	
	
	//COMBO
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeLlenaComboEmpresa,
		id: PF + 'cmbEmpresa',
		name: PF + 'cmbEmpresa',
		width: 300,
		x: 60,
		y: 15,
		typeAhead: true,
		mode: 'local',	
		selecOnFocus: true,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione Empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}		
	});
		
	//GRID
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGrid = new Ext.grid.ColumnModel([
	    {header: 'No Empresa', width: 80, dataIndex: 'noEmpresa', sortable: true},
	    {header: 'Id Codigo', width: 80, dataIndex: 'idCodigo', sortable: true},
	    {header: 'Descripción', width: 200, dataIndex: 'descCodigo', sortable: true}
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
		height: 230,
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
		height: 80,
		layout: 'absolute',
		items:
		[
		 	NS.lblEmpresa,
		 	NS.txtEmpresa,
		 	NS.cmbEmpresa,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 850,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners:{ 
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
		y: 90,
		width: 985,
		height: 280,
		layout: 'absolute',
		items:
		[
		 	NS.gridConsulta,
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 550,
		 		y: 235,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelAlta.setDisabled(false);
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 650,
		 		y: 235,
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
		 						Ext.Msg.alert('SET', 'Debe de seleccionar algun registro');
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 750,
		 		y: 235,
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
		 		text: 'Limpiar',
		 		x: 850,
		 		y: 235,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiar(1);
		 				}
		 			}
		 		}
		 	}
		]			
	});

	NS.panelAlta = new Ext.form.FieldSet({
		title: 'Alta De Código',
		x: 0,
		y: 380,
		width: 985,
		height: 80,
		layout: 'absolute',
		disabled: true,
		items:
		[
		 	NS.lblCodigo,
		 	NS.txtIdCodigo,
		 	NS.txtDescCodigo,
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 750, 
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.insertaCodigo();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 850,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiar(0);
		 					NS.panelAlta.setDisabled(true);
		 				}
		 			}
		 		}
		 	}
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
		 	NS.panelAlta
	    ]
	});
	
	NS.codigos = new Ext.FormPanel ({
		title: 'Mantenimiento de Códigos',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'codigos',
		name: PF + 'codigos',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	NS.codigos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});
 