Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.EquivalenciaEmpresas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecha = NS.fecHoy;
	NS.modificar = false;
	NS.codigo = "CP"
	NS.nomEmpresaAlta = "";
	
	
	//LABELS
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 0
	});
	
	//LABEL PARA CAMPOS DE ALTA
	NS.lblEmpresaAlta = new Ext.form.Label({
		text: 'Empresa SET',
		x: 0,
		y: 0
	});
	
	NS.lblEmpresaInterface = new Ext.form.Label({
		text: 'Empresa Interface',
		x: 400,
		y: 0
	});
		
	//TEXTFIELD
	NS.txtIdEmpresa = new Ext.form.TextField({
		id: PF + 'txtIdEmpresa',
		name: PF + 'txtIdEmpresa',
		width: 50,
		x: 0,
		y: 15,
		listeners:
		{
			change:
			{
				fn: function(caja, valor){
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}
	});
	
	//TEXFIELD PARA CAMPOS DE ALTA
	NS.txtIdEmpresaAlta = new Ext.form.TextField({
		id: PF + 'txtIdEmpresaAlta',
		name: PF + 'txtIdEmpresaAlta',
		width: 70,
		x: 0,
		y: 15,
		listeners:
		{
			change:
			{
				fn: function(caja, valor){
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdEmpresaAlta', NS.cmbEmpresaAlta.getId());
				}
			}
		}
	});
	
	NS.txtEmpresaInterface = new Ext.form.TextField({
		id: PF + 'txtEmpresaInterface',
		name: PF + 'txtEmpresaInterface',
		width: 70,
		x: 400,
		y: 15
	});
	
	//FUNCIONES
	NS.buscar = function(){		
		NS.nomEmpresa = Ext.getCmp(PF + 'cmbEmpresa').getValue();
		
		if (Ext.getCmp(PF + 'cmbEmpresa').getValue() != '')
			NS.nomEmpresa = NS.storeLlenaComboEmpresa.getById(NS.nomEmpresa).get('nomEmpresa');
		else
			NS.nomEmpresa = '';
		
		NS.storeLlenaGrid.baseParams.nomEmpresa = NS.nomEmpresa;
		NS.storeLlenaGrid.load();
	};
	
	
	NS.llenaDatos = function(){
		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		NS.panelCampos.setDisabled(false);
				
		Ext.getCmp(PF + 'txtIdEmpresaAlta').setValue(registroSeleccionado[0].get('empresaSet'));
		Ext.getCmp(PF + 'txtEmpresaInterface').setValue(registroSeleccionado[0].get('empresaInterface'));
		Ext.getCmp(PF + 'cmbEmpresaAlta').setValue(registroSeleccionado[0].get('descripcion'));
		NS.nomEmpresaAlta = Ext.getCmp(PF + 'cmbEmpresaAlta').getValue();
		
		if (registroSeleccionado[0].get('codigo') == 'CP')
			Ext.getCmp(PF + 'optRadios').setValue(0);
		else if(registroSeleccionado[0].get('codigo') == 'CC')
			Ext.getCmp(PF + 'optRadios').setValue(1);
		else
			Ext.getCmp(PF + 'optRadios').setValue(2);
		
		
		Ext.getCmp(PF + 'btnCrearNuevo').setDisabled(true);
		Ext.getCmp(PF + 'btnModificar').setDisabled(true);
		Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
	};
	
	NS.limpiaCampos = function(todo){
		NS.txtIdEmpresaAlta.setValue('');
		NS.cmbEmpresaAlta.reset();
		NS.txtEmpresaInterface.setValue('');
		Ext.getCmp(PF + 'optRadios').setValue(0);
		
		if (todo)
		{
			Ext.getCmp(PF + 'txtIdEmpresa').setValue('');
			Ext.getCmp(PF + 'cmbEmpresa').reset();
			NS.gridConsulta.store.removeAll();
			NS.gridConsulta.getView().refresh();
			NS.panelCampos.setDisabled(true);
			Ext.getCmp(PF + 'btnCrearNuevo').setDisabled(false);
			Ext.getCmp(PF + 'btnModificar').setDisabled(false);
			Ext.getCmp(PF + 'btnEliminar').setDisabled(false);
		}
	};
	
	NS.crearNuevo = function(){
		NS.panelCampos.setDisabled(false);
		NS.limpiaCampos(false);
		Ext.getCmp(PF + 'btnCrearNuevo').setDisabled(true);
		Ext.getCmp(PF + 'btnModificar').setDisabled(true);
		Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
	};
	
	NS.eliminar = function(registroSeleccionado){
		var codigo;
		var empresaSet;
		var empresaInterface;
		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		
		Ext.Msg.confirm('SET', '¿está seguro de eliminar el registro seleccionado?', function(btn){
			if (btn === 'yes'){
				codigo = registroSeleccionado[0].get('codigo');
				empresaSet = registroSeleccionado[0].get('empresaSet');
				empresaInterface = registroSeleccionado[0].get('empresaInterface');
			
				EquivalenciaEmpresasAction.eliminaRegistro(codigo, empresaSet, empresaInterface, function(resultado, e){
					if (resultado > 0)
					{
						Ext.Msg.alert('SET', 'Registro eliminado con éxito');
						NS.buscar();
					}
					else
						Ext.Msg.alert('SET', 'Ocurrio un problema durante la eliminación');
				});
			}
		});		
	};
	
	NS.insertaModifica = function(){
		
		var registro = NS.gridConsulta.getSelectionModel().getSelections();			
		var vector = {};
		var matriz = new Array();
		
			
		vector.empresaSet = Ext.getCmp(PF + 'txtIdEmpresaAlta').getValue();
		
		vector.descripcion = NS.cmbEmpresaAlta.getValue();
				
//		vector.descripcion = NS.nomEmpresaAlta;
					
		vector.empresaInterface = Ext.getCmp(PF + 'txtEmpresaInterface').getValue();
		
		vector.codigo = NS.codigo;
		
		if (NS.modificar){
			vector.tipoOperacion = 'modificar';
			vector.empresaSetOri = registro[0].get('empresaSet');
			vector.empresaInterfaceOri = registro[0].get('empresaInterface');
			vector.codigoOri = registro[0].get('codigo');
		}
		else
			vector.tipoOperacion = 'insertar';
		
		matriz[0] = vector;
		
		var jSonString = Ext.util.JSON.encode(matriz);
		
		//Se valida que los campos tengan informacion
		EquivalenciaEmpresasAction.validaDatos(jSonString, function(resultado, e){
			if (resultado == 0 || resultado == '' && resultado != undefined)
			{
				if (NS.modificar) //Actualiza registro
				{
					Ext.Msg.confirm('SET', '¿Esta seguro de aplicar los cambios al registro?', function(btn){
						if (btn === 'yes')
						{
							EquivalenciaEmpresasAction.insertaActualizaEmpresa(jSonString, function(resultado, e)
							{
								if (resultado != 0 && resultado != '' && resultado != undefined)
								{
									Ext.Msg.alert('SET', resultado);
									NS.buscar();
								}
							});
						}
					});
				}
				else //Inserta registro
				{
					EquivalenciaEmpresasAction.insertaActualizaEmpresa(jSonString, function(resultado, e){
					
						if (resultado != 0 && resultado != '' && resultado != undefined)
						{
							Ext.Msg.alert('SET', resultado);
							NS.buscar();
						}
					});
				}
				NS.cancelar();
			}
			else
				Ext.Msg.alert('SET', resultado);
		});
		
		
			
	
	};
	
	NS.cancelar = function(){
		NS.panelCampos.setDisabled(true);
		NS.limpiaCampos(false);
		Ext.getCmp(PF + 'btnCrearNuevo').setDisabled(false);
		Ext.getCmp(PF + 'btnModificar').setDisabled(false);
		Ext.getCmp(PF + 'btnEliminar').setDisabled(false);
	};
	
	//STORE
	NS.storeLlenaComboEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: EquivalenciaEmpresasAction.llenaComboEmpresas,
		idProperty: 'noEmpresa',
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
					Ext.Msg.alert('SET', 'No existen empresas dadas de alta');				
			}
		}		
	});	
	NS.storeLlenaComboEmpresa.load();
		
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {nomEmpresa: NS.nomEmpresa},
		paramOrder: ['nomEmpresa'],
		directFn: EquivalenciaEmpresasAction.llenaGrid,
		fields:
		[
		 	{name: 'descripcion'},
		 	{name: 'empresaSet'},
		 	{name: 'empresaInterface'},
		 	{name: 'codigo'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando información.."});
				if (records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET', 'No existen Empresas dadas de alta');
				}
			}
		}
	});
	
	NS.storeLlenaComboEmpresaAlta = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: EquivalenciaEmpresasAction.llenaComboEmpresas,
		idProperty: 'noEmpresa',
		fields:
		[
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboEmpresaAlta, msg: "Cargando empresas..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen empresas dadas de alta');				
			}
		}		
	});	
	NS.storeLlenaComboEmpresaAlta.load();
	
	//COMBOS
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
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		editable: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdEmpresa', NS.cmbEmpresa.getId());
									
					
				}
			}
		}		
	});
	
	NS.cmbEmpresaAlta = new Ext.form.ComboBox({
		store: NS.storeLlenaComboEmpresaAlta,
		id: PF + 'cmbEmpresaAlta',
		name: PF + 'cmbEmpresaAlta',
		width: 300,
		x: 80,
		y: 15,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		editable: false,
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtIdEmpresaAlta', NS.cmbEmpresaAlta.getId());
					
					NS.nomEmpresaAlta = Ext.getCmp(PF + 'cmbEmpresaAlta').getValue();
					
					if (Ext.getCmp(PF + 'cmbEmpresaAlta').getValue() != '')
						NS.nomEmpresaAlta = NS.storeLlenaComboEmpresaAlta.getById(NS.nomEmpresaAlta).get('nomEmpresa');
					else
						NS.nomEmpresaAlta = '';
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
	  {header: 'Descripción', width: 500, dataIndex: 'descripcion', sortable: true},
	  {header: 'Empresa SET', width: 100, dataIndex: 'empresaSet', sortable: true},
	  {header: 'Empresa Interface', width: 100, dataIndex: 'empresaInterface', sortable: true},
	  {header: 'Código', width: 80, dataIndex: 'codigo', sortable: true}
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
		height: 175,
		stripRows: true,
		columnLines: true,
		listeners: {
			click:
			{
				fn: function(grid)
				{
					if (NS.modificar)
						NS.llenaDatos();
				}
			}
		}
	});
	
	//RADIO BUTTON	
	NS.optRadios = new Ext.form.RadioGroup({
		id: PF + 'optRadios',
		name: PF + 'optRadios',
		x: 0,
		y: 0,
		columns: 3,
		items:
		[
		 	{boxLabel: 'Cuentas por Pagar', name: 'optSeleccion', inputValue: 0, checked: true,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true){		 					
		 						NS.codigo = "CP";	
		 					}	 					
		 				}
		 			}
		 		}		 		
		 	},
		 	{boxLabel: 'Cuentas por Cobrar', name: 'optSeleccion', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true){		 			
		 						NS.codigo = "CC";
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{boxLabel: 'Empresa Coinversora', name: 'optSeleccion', inputValue: 2,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true){
		 						NS.codigo = "CO";
		 					}
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	//PANEL
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: 'Busqueda',
		x: 0,
		y: 0,		
		width: 985,
		height: 75,
		layout: 'absolute',
		items:
		[
		 	NS.lblEmpresa,
		 	NS.txtIdEmpresa,
		 	NS.cmbEmpresa,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 870,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e){
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
		y: 85,
		width: 985,
		height: 230,
		layout: 'absolute',
		items:
		[
		 	NS.gridConsulta,
		 	{
		 		id: PF + 'btnExcel',
		 		name: PF + 'btnExcel',
		 		xtype: 'button',
		 		text: 'Excel',		 		
		 		x: 570,
		 		y: 185,
		 		width: 80,
		 		heigth: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					var registroSeleccionado = NS.storeLlenaGrid.data.items;
		 					if (registroSeleccionado.length > 0)
		 					{
		 						
		 						var matriz = new Array();
		 						
		 						for(var i=0;i<registroSeleccionado.length;i++){
		 							var vector = {};
		 							
		 							vector.descripcion = registroSeleccionado[i].get('descripcion');
		 							vector.empresaSet = registroSeleccionado[i].get('empresaSet');
		 							vector.empresaInterface = registroSeleccionado[i].get('empresaInterface');
		 							vector.codigo = registroSeleccionado[i].get('codigo');
		 							
		 							matriz[i] = vector;
		 				 		
		 						}
		 						
		 						EquivalenciaEmpresasAction.exportaExcel(Ext.util.JSON.encode(matriz), function(res, e){
		 							if (res != null && res != undefined && res == "") {
		 								Ext.Msg.alert('SET', "Error al generar el archivo");
		 							} else {
		 								strParams = '?nomReporte=equivalePersonas';
		 								strParams += '&'+'nomParam1=nomArchivo';
		 								strParams += '&'+'valParam1='+res;
		 								window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
		 							}
		 						});
		 					}
		 					else
		 						Ext.Msg.alert('SET', 'No hay información para exportar.');
		 				}
		 			}
		 		}
		 	},
		 	{
		 		id: PF + 'btnModificar',
		 		name: PF + 'btnModificar',
		 		xtype: 'button',
		 		text: 'Modificar',		 		
		 		x: 670,
		 		y: 185,
		 		width: 80,
		 		heigth: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		 					if (registroSeleccionado.length > 0)
		 					{
			 					NS.modificar = true;
			 					NS.llenaDatos();
			 					
		 					}
		 					else
		 						Ext.Msg.alert('SET', 'Se debe seleccionar algun registro');
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		id: PF + 'btnCrearNuevo',
		 		name: PF + 'btnCrearNuevo',
		 		text: 'Crear Nuevo',
		 		x: 770, 
		 		y: 185,
		 		width: 80, 
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.modificar = false;
		 					NS.crearNuevo();		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		id: PF + 'btnEliminar',
		 		name: PF + 'btnEliminar',
		 		text: 'Eliminar',
		 		x: 870,
		 		y: 185,
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
		 					{
			 					NS.eliminar();
		 					}
		 					else
		 						Ext.Msg.alert('SET', 'Se debe seleccionar algun registro');
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelRadio = new Ext.form.FieldSet({
		title: 'Código',
		x: 500,
		y: 0,
		width: 450,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	NS.optRadios
		]
	});
	
	NS.panelCampos = new Ext.form.FieldSet({
		title: 'Alta de Equivalencia',
		x: 0,
		y: 320,
		width: 985,
		height: 120,
		layout: 'absolute',
		disabled: true,
		items:
		[
		 	NS.lblEmpresaAlta,
		 	NS.lblEmpresaInterface,
		 	NS.txtIdEmpresaAlta,
		 	NS.txtEmpresaInterface,
		 	NS.cmbEmpresaAlta,
		 	NS.panelRadio,
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 770,
		 		y: 60,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.insertaModifica();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 870,
		 		y: 60, 
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.cancelar();
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
		 	NS.panelCampos,
		 	{
		 		xtype: 'button',
		 		text: 'Limpiar',
		 		x: 880,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiaCampos(true);
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 780,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		hidden: true,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{}
		 			}
		 		}
		 	}
	    ]
	});
	
	NS.equivalenciaEmpresas = new Ext.FormPanel ({
		title: 'Equivalencia de Empresas',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'equivalenciaEmpresas',
		name: PF + 'equivalenciaEmpresas',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	NS.equivalenciaEmpresas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});
 