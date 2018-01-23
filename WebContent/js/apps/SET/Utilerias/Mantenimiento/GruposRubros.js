Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.GruposRubros');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.seleccionIdRubro;

	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {idGrupo: 0},
		paramOrder: ['idGrupo'],
		directFn: CodigosAction.getRubros,
		fields:
		[
		 	{name: 'idGrupo'},
		 	{name: 'descGrupo'},
		 	{name: 'idRubro'},
		 	{name: 'descRubro'},
		 	{name: 'ingresoEgreso'}
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

	
	
	NS.optionsBusqueda = new Ext.form.RadioGroup({
		id: PF + 'optionsBusqueda',
		name: PF + 'optionsBusqueda',
		x: 10,
		y: 0,
		columns: 1, 
		items: [  
		      {boxLabel: 'Polizas', name:'optionsBusqueda', inputValue: 4, checked: true}, 
	          {boxLabel: 'Grupos', name: 'optionsBusqueda', inputValue: 1},
	          {boxLabel: 'Rubros', name: 'optionsBusqueda', inputValue: 2},  
	          {boxLabel: 'Rubros-Cuentas Contables', name: 'optionsBusqueda', inputValue: 3, hidden: true}
	          ],
	    listeners:{
			change:{
				fn:function(){
		
					var opcion = Ext.getCmp(PF + 'optionsBusqueda').getValue().getGroupValue();

					switch( parseInt( opcion ) )
					{
					
						case 1:
							
							NS.panelGridPolzas.setVisible(false);
							NS.panelAltaPolizas.setVisible(false);
							
							NS.panelBusqueda.setVisible(false);
						 	NS.panelGrid.setVisible(false);
						 	NS.panelAlta.setVisible(false);
						 	NS.panelAltaGrupos.setVisible(true);
							NS.panelGridGrupos.setVisible(true);
							NS.panelBusquedaRubrosCuentas.setVisible(false);
						 	NS.panelGridRubrosCuentas.setVisible(false);
						 	NS.panelAltaRubrosCuentas.setVisible(false);
						 	NS.storeLlenaGridGrupos.load();
						 	NS.storeCmbPolizas.load();
							break;
							
						case 2:
							
							NS.panelGridPolzas.setVisible(false);
							NS.panelAltaPolizas.setVisible(false);
							
							NS.panelBusquedaRubrosCuentas.setVisible(false);
						 	NS.panelGridRubrosCuentas.setVisible(false);
						 	NS.panelAltaRubrosCuentas.setVisible(false);
							
							NS.panelBusqueda.setVisible(true);
						 	NS.panelGrid.setVisible(true);
						 	NS.panelAlta.setVisible(true);
						 	NS.panelAltaGrupos.setVisible(false);
							NS.panelGridGrupos.setVisible(false);
							
							
						 	NS.storeLlenaGrid.load();
						 	NS.storeLlenaComboGrupoShr.load();
						 	
						 	
							break;
						case 3:	
							
							NS.gridConsultaRubrosCuentas.setVisible(true);
							NS.panelBusqueda.setVisible(false);
						 	NS.panelGrid.setVisible(false);
						 	NS.panelAlta.setVisible(false);
						 	NS.panelAltaGrupos.setVisible(false);
							NS.panelGridGrupos.setVisible(false);
							NS.panelBusquedaRubrosCuentas.setVisible(true);
						 	NS.panelGridRubrosCuentas.setVisible(true);
						 	NS.panelAltaRubrosCuentas.setVisible(true);
						 	NS.storeLlenaGridRubrosCuentas.load();
							break;
							
						case 4:
							
							NS.panelBusqueda.setVisible(false);
						 	NS.panelGrid.setVisible(false);
						 	NS.panelAlta.setVisible(false);
						 	NS.panelAltaGrupos.setVisible(false);
							NS.panelGridGrupos.setVisible(false);
							
							NS.panelGridPolzas.setVisible(true);
							NS.panelAltaPolizas.setVisible(true);
							
							NS.storeLlenaGridPolizas.load();
							break;
							
					}//End switch
					
				}//End function
		
			} //End change
		
		} // End listeners	
	});
	
	
	
	
	
	//FUNCIONES
	NS.buscar = function(){
		NS.gridConsulta.store.removeAll();
		NS.gridConsulta.getView().refresh();
		
		NS.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
		
		NS.storeLlenaGrid.baseParams.noEmpresa = parseInt(NS.noEmpresa);		
		NS.storeLlenaGrid.load();		
	};
	
	NS.limpiar = function(todo){

		NS.panelAlta.setDisabled(true);
		Ext.getCmp(PF + 'txtIdCodigo').setValue('');
		Ext.getCmp(PF + 'txtDescCodigo').setValue('');
		Ext.getCmp(PF + 'txtGrupoShr').setValue('');
		NS.storeLlenaComboGrupoSave.removeAll();
		NS.cmbGrupoShr.reset();
		
		if (todo == 1){ //Limpia toda la pantalla
			Ext.getCmp(PF + 'txtGrupoShr').setValue('');
			NS.cmbGrupoShr.reset();
			NS.gridConsulta.store.removeAll();
			NS.gridConsulta.getView().refresh();
		}
	};
		
	NS.eliminar = function(seleccion, seleccion2 ,opcion){
		//Ext.Msg.alert('SET', seleccion[0].get('idGrupo'));
		console.log(seleccion + ' ' + seleccion2 + ' ' + opcion);
		Ext.Msg.confirm('SET', 'Esta seguro que desea eliminar el ' + opcion + ' seleccionado', function (btn){
			if (btn === 'yes'){
				CodigosAction.eliminaCodigo(seleccion, seleccion2, opcion, function(resultado, e){
					if (resultado != 0 && resultado != '' && resultado && undefined){
						Ext.Msg.alert('SET', resultado);
						
					}
					switch(opcion){
					case 'grupo':
						NS.storeLlenaGridGrupos.load();
						break;
					case 'rubro':
						NS.storeLlenaGrid.load();
						break;
				}
				});
			}
			
		});
				
		
	}
	
//	NS.eliminar = function(){
//		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
//		var idCodigo = 0;
//		var noEmpresa = 0;
//		
//		Ext.Msg.confirm('SET', '¿Esta seguro de eliminar el Código seleccionado?', function (btn){
//			if (btn === 'yes'){
//				idCodigo = registroSeleccionado[0].get('idCodigo');
//				noEmpresa = parseInt(registroSeleccionado[0].get('noEmpresa'));
//				
//				CodigosAction.eliminaCodigo(idCodigo, noEmpresa, function(resultado, e){
//					if (resultado != 0 && resultado != '' && resultado && undefined){
//						Ext.Msg.alert('SET', resultado);						
//						NS.buscar();
//					}
//					else{
//						Ext.Msg.alert('SET', resultado);
//						NS.buscar();
//					}
//				});
//			}
//		});
//	};
//	
	
	
	/*******************************************************
	 *             BUSQUEDA DE RUBRO POR GRUPO 
	 */
	
	NS.storeLlenaComboGrupoShr = new Ext.data.DirectStore({
		paramOrder:['idTipoMovto'],
		paramsAsHash: false,
		baseParams:{
		idTipoMovto:'N'			
		},		
		root: '',
		directFn: TraspasosAction.llenarComboGrupo,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridGrupos, msg: "Cargando información..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existe información para estos parametros');
			}
		}
	});
	
	//COMBO
	NS.cmbGrupoShr = new Ext.form.ComboBox({
		store: NS.storeLlenaComboGrupoShr,
		id: PF + 'cmbGrupoShr',
		name: PF + 'cmbGrupoShr',
		width: 300,
		x: 60,
		y: 15,
		typeAhead: true,
		mode: 'local',	
		selecOnFocus: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Seleccione el grupo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtGrupoShr', NS.cmbGrupoShr.getId());
				}
			}
		}		
	});
	
	//TEXFIELD
	NS.txtGrupoShr = new Ext.form.TextField({
		id: PF + 'txtGrupoShr',
		name: PF + 'txtGrupoShr',
		x: 0,
		y: 15,
		width: 50,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtGrupoShr', NS.cmbGrupoShr.getId());
				}
			}
		}
	});
	
	NS.lblGrupoShr = new Ext.form.Label({
		text: 'Grupo',
		x: 0,
		y: 0
	});

	
	/*******************************************************
	 *             BUSQUEDA DE RUBRO POR GRUPO 
	 */	
	
	
		
	//GRID
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	NS.columnasGrid = new Ext.grid.ColumnModel([
	    {header: 'Id Grupo', width: 80, dataIndex: 'idGrupo', sortable: true},
	    {header: 'Descripcion', width: 200, dataIndex: 'descGrupo', sortable: true},
	    {header: 'id Rubro', width: 80, dataIndex: 'idRubro', sortable: true},
	    {header: 'Descripcion', width: 200, dataIndex: 'descRubro', sortable: true},
	    {header: 'I/E', width: 50, dataIndex: 'ingresoEgreso', sortable: true}
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
		height: 200,
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
	
	NS.storeLlenaGridPolizasSinAsignar = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {idRubro: 0},
		paramOrder:['idRubro'],
		root: '',
		directFn: CodigosAction.obtenerPolizasSinAsignar,
		fields:
		[
		 	{name: 'idCodigo'},
		 	{name: 'descCodigo'},
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridPolizasSinAsignar, msg: "Cargando informacion..."});
				if(records.length == null || records.length <= 0){
					//Ext.Msg.alert('SET', 'No existe información para mostrar');
				}
			}
		}
	});	

	
	NS.columnaSeleccionPolizasSinAsignar = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGridPolizasSinAsignar = new Ext.grid.ColumnModel([
        NS.columnaSeleccionPolizasSinAsignar,
	    {header: 'Id Poliza', width: 80, dataIndex: 'idCodigo', sortable: true},
	    {header: 'Descripcion', width: 300, dataIndex: 'descCodigo', sortable: true},
	]);
	
	NS.gridConsultaPolizasSinAsignar = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridPolizasSinAsignar,
		id: PF + 'gridConsultaPolizasSinAsignar',
		name: PF + 'gridConsultaPolizasSinAsignar',
		title: 'Sin Asignar',
		cm: NS.columnasGridPolizasSinAsignar,
		sm: NS.columnaSeleccionPolizasSinAsignar,
		x: 0,
		y: 0,
		width: 400,
		height: 200,
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


	NS.storeLlenaGridPolizasAsignadas = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {idRubro: 0},
		paramOrder:['idRubro'],
		root: '',
		directFn: CodigosAction.obtenerPolizasAsignadas,
		fields:
		[
		 	{name: 'idCodigo'},
		 	{name: 'descCodigo'},
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridPolizasAsignadas, msg: "Cargando informacion..."});
				if(records.length == null || records.length <= 0){
					//Ext.Msg.alert('SET', 'No existe información para mostrar');
				}
			}
		}
	});	

	
	NS.columnaSeleccionPolizasAsignadas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGridPolizasAsignadas = new Ext.grid.ColumnModel([
        NS.columnaSeleccionPolizasAsignadas,
	    {header: 'Id Poliza', width: 80, dataIndex: 'idCodigo', sortable: true},
	    {header: 'Descripcion', width: 300, dataIndex: 'descCodigo', sortable: true},
	]);
	
	NS.gridConsultaPolizasAsignadas = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridPolizasAsignadas,
		id: PF + 'gridConsultaPolizasAsignadas',
		name: PF + 'gridConsultaPolizasAsignadas',
		title: 'Asignadas',
		cm: NS.columnasGridPolizasAsignadas,
		sm: NS.columnaSeleccionPolizasAsignadas,
		x: 450,
		y: 0,
		width: 400,
		height: 200,
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
		
	

	NS.venPolizas = new Ext.Window({
		title: 'POLIZAS',
		modal: true,
		shadow: true,
		width: 885,
		height: 350,
		layout: 'absolute',
		plain: true,
		bodyStyle: 'padding:10px;background-color:#000000',
		closeAction: 'hide',
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items:[
		       NS.gridConsultaPolizasSinAsignar,
		       NS.gridConsultaPolizasAsignadas,
		       {
		    	   	xtype: 'button',  
		    	   	text: 'Asignar',
		    	   	x: 0,
		    	   	y: 215,
		    	   	width: 80,
			 		height: 22,
                	handler: function(){
                		 var seleccionados = NS.gridConsultaPolizasSinAsignar.getSelectionModel().getSelections();
                		 if(seleccionados.length > 0){
                			 NS.matDatos = new Array();
                			 
                			 for(var i = 0; i < seleccionados.length; i++){
                				 var vecDatos = {};
                				 vecDatos.idRubro = seleccionados[i].get('idCodigo');
                				 vecDatos.desRubro = seleccionados[i].get('descCodigo');
                				 NS.matDatos[i] = vecDatos; 
                			 }
                			 
                			 var json = Ext.util.JSON.encode(NS.matDatos);
                			 CodigosAction.asignarPolizas(json, NS.seleccionIdRubro, function(resultado, e){
                				 NS.storeLlenaGridPolizasSinAsignar.reload();
                				 NS.storeLlenaGridPolizasAsignadas.reload();
                			 });
                			 
                		 }else{
                			 Ext.Msg.alert('SET', 'No se han seleccionado polizas');
                		 }
                	 }
                 },
                 {
                	xtype: 'button',  
 		    	   	text: 'Eliminar',
 		    	   	x: 770,
 		    	   	y: 215,
 		    	   	width: 80,
 			 		height: 22,
                 	handler: function(){
                 		var seleccionadosEliminaar = NS.gridConsultaPolizasAsignadas.getSelectionModel().getSelections();
                 		if(seleccionadosEliminaar.length > 0){
                 			NS.matDatosE = new Array();
               			 
               			 for(var i = 0; i < seleccionadosEliminaar.length; i++){
               				 var vecDatos = {};
               				 vecDatos.idRubro = seleccionadosEliminaar[i].get('idCodigo');
               				 vecDatos.desRubro = seleccionadosEliminaar[i].get('descCodigo');
               				 NS.matDatosE[i] = vecDatos; 
               			 }
               			 
               			 var json = Ext.util.JSON.encode(NS.matDatosE);
               			 CodigosAction.eliminarPolizas(json, NS.seleccionIdRubro, function(resultado, e){
               				 NS.storeLlenaGridPolizasSinAsignar.reload();
               				 NS.storeLlenaGridPolizasAsignadas.reload();
               			 });
               			 
               			 
                 		}else{
                 			Ext.Msg.alert('SET', 'No se han seleccionado polizas');
                 		}
                 	}
                 }
        ]
	});
	
	
	//PANEL
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: 'Busqueda de Rubros',
		x: 185, 
		y: 0,
		width: 800,
		height: 80,
		layout: 'absolute',
		hidden:true,
		items:
		[
		 	NS.lblGrupoShr,
		 	NS.txtGrupoShr,
		 	NS.cmbGrupoShr,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 650,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners:{ 
		 			click: {
		 				fn: function(e) {
		 					NS.buscarRubros();
		 				}
		 			}
		 		}	
		 	}
		]
	});
	
	//FUNCIONES
	
		
	NS.buscarRubros = function(){
		NS.gridConsulta.store.removeAll();
		NS.gridConsulta.getView().refresh();
		
		NS.idGrupo = Ext.getCmp(PF + 'txtGrupoShr').getValue();
		
		if (NS.idGrupo == 0)
			NS.idGrupo = 0;
		
		NS.storeLlenaGrid.baseParams.idGrupo = parseInt(NS.idGrupo);		
		NS.storeLlenaGrid.load();		
	};
	
	NS.panelGrid = new Ext.form.FieldSet({
		title: 'Resultados Rubros',
		x: 0,
		y: 90,
		width: 985,
		height: 280,
		layout: 'absolute',
		hidden:true,
		items:
		[		 	
		 	NS.gridConsulta,
		 	{
		 		xtype: 'button',
		 		text: 'Excel',
		 		x: 850,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					parametros='?nomReporte=excelGrupoRubros';
 							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);	
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 550,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.storeLlenaComboGrupoSave.load();
		 					NS.panelAlta.setDisabled(false);
		 				}
		 			}
		 		}
		 	},
		 	{
		 	xtype: 'button',
        	text: 'Modificar',
        	x: 450,
        	y: 220,
        	width: 80,
        	height: 22,
        	//hidden:true,
        	listeners:
        	{
        		click:
        		{
        			fn: function(e)
        			{
        				if(NS.gridConsulta.getSelectionModel().getSelections().length > 0){
        					var seleccion = NS.gridConsulta.getSelectionModel().getSelections();
        					//BFwrk.Util.updateComboToTextField(PF + 'txtGrupoSave', NS.cmbGrupoSave.getId());
        					NS.storeLlenaComboGrupoSave.load();
		 					NS.panelAlta.setDisabled(false);
		 					NS.txtIdCodigo.setDisabled(true);
		 					NS.txtIdCodigo.setValue(seleccion[0].get('idRubro'));
		 					NS.cmbGrupoSave.setValue(seleccion[0].get('descGrupo'));
		 					NS.txtDescCodigo.setValue(seleccion[0].get('descRubro'));
		 					NS.txtGrupoSave.setValue(seleccion[0].get('idGrupo'));
		 					
        				}
        				else{
        					Ext.Msg.alert('SET', 'Debe seleccionar un registro');
        				}	
        			}
        		}
        	}
        },

		 	
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar R',
		 		x: 650,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		//hidden:true,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		 					
		 	
		 					if (registroSeleccionado.length > 0){
		 						NS.eliminar(registroSeleccionado[0].get('idGrupo'), registroSeleccionado[0].get('idRubro'), 'rubro');
		 					}
		 					else{
		 						Ext.Msg.alert('SET', 'Debe de seleccionar algun registro');
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 650,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		hidden:true,
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
		 		x: 750,
		 		y: 220,
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
		 	},
		 	{
			 	xtype: 'button',
	        	text: 'Asignar',
	        	x: 350,
	        	y: 220,
	        	width: 80,
	        	height: 22,
	        	
	        	listeners:
	        	{
	        		click:
	        		{
	        			fn: function(e)
	        			{
	        				if(NS.gridConsulta.getSelectionModel().getSelections().length > 0){
	        					NS.seleccionIdRubro = NS.gridConsulta.getSelectionModel().getSelections()[0].get('idRubro');
	        					NS.storeLlenaGridPolizasSinAsignar.baseParams.idRubro = NS.seleccionIdRubro + '';
	        					NS.storeLlenaGridPolizasSinAsignar.load();
	        					NS.storeLlenaGridPolizasAsignadas.baseParams.idRubro = NS.seleccionIdRubro + '';
	        					NS.storeLlenaGridPolizasAsignadas.load();
	        					NS.venPolizas.show();
	        				}
	        				else{
	        					Ext.Msg.alert('SET', 'Debe seleccionar un registro');
	        				}	
	        			}
	        		}
	        	}
	        }
		]			
	});
	
	
	/******************************************************************
	 *                       PANEL ALTA RUBROS
	 */
	
	
	NS.storeLlenaComboGrupoSave = new Ext.data.DirectStore({
		paramOrder:['idTipoMovto'],
		paramsAsHash: false,
		baseParams:{
		idTipoMovto:'N'			
		},		
		root: '',
		directFn: TraspasosAction.llenarComboGrupo,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboGrupoSave, msg: "Cargando información..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existe información para estos parametros');
			}
		}
	});
	
	
	//COMBO
	NS.cmbGrupoSave = new Ext.form.ComboBox({
		store: NS.storeLlenaComboGrupoSave,
		id: PF + 'cmbGrupoSave',
		name: PF + 'cmbGrupoSave',
		width: 220,
		x: 60,
		y: 15,
		typeAhead: true,
		mode: 'local',	
		selecOnFocus: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Seleccione el grupo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtGrupoSave', NS.cmbGrupoSave.getId());
				}
			}
		}		
	});
	
	//TEXFIELD
	NS.txtGrupoSave = new Ext.form.TextField({
		id: PF + 'txtGrupoSave',
		name: PF + 'txtGrupoSave',
		x: 0,
		y: 15,
		width: 50,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtGrupoSave', NS.cmbGrupoSave.getId());
				}
			}
		}
	});
	
	NS.lblGrupoSave = new Ext.form.Label({
		text: 'Grupo',
		x: 0,
		y: 0
	});

	

	//LABEL DE ALTA DE CODIGO
	NS.lblRubroIdSave = new Ext.form.Label({
		text: 'Id Rubro',
		x: 300,
		y: 0
	});
	
	//LABEL DE ALTA DE CODIGO
	NS.lblCodigo = new Ext.form.Label({
		text: 'Descripción del Rubro',
		x: 360,
		y: 0
	});
	
	//TEXTFIELD PARA ALTA
	NS.txtIdCodigo = new Ext.form.TextField({
		id: PF + 'txtIdCodigo',
		name: PF + 'txtIdCodigo',
		x: 300,
		y: 15,
		width: 50		
	});
	
	NS.txtDescCodigo = new Ext.form.TextField({
		id: PF + 'txtDescCodigo',
		name: PF + 'txtDescCodigo',
		x: 360,
		y: 15,
		width: 300
	});
	
	
	NS.optionsIngresoEgreso = new Ext.form.RadioGroup({
		id: PF + 'optionsIngresoEgreso',
		name: PF + 'optionsIngresoEgreso',
		x: 680,
		y: 0,
		columns: 1, 
		items: [  
	          {boxLabel: 'Ingreso', name: 'optionsIngresoEgreso', inputValue: 1, checked: true},
	          {boxLabel: 'Egreso', name: 'optionsIngresoEgreso', inputValue: 2}
	          ],
	    listeners:{
			change:{
				fn:function(){
		
					var opcion = Ext.getCmp(PF + 'optionsIngresoEgreso').getValue().getGroupValue();

					switch( parseInt( opcion ) )
					{
					
						case 1:
							break;
							
						case 2:
							break;
						case 3:							
							break;
							
					}//End switch
					
				}//End function
		
			} //End change
		
		} // End listeners	
	});

	

	NS.panelAlta = new Ext.form.FieldSet({
		title: 'Alta De Rubros',
		x: 0,
		y: 380,
		width: 985,
		height: 80,
		layout: 'absolute',
		disabled: true,
		hidden:true,
		items:
		[
		 	NS.optionsIngresoEgreso,
		 	NS.lblRubroIdSave,
		 	NS.lblGrupoSave,
		 	NS.txtGrupoSave,
		 	NS.cmbGrupoSave,
		 	NS.lblCodigo,
		 	NS.txtIdCodigo,
		 	NS.txtDescCodigo,
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 780, 
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
		 		x: 880,
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
		 					NS.cmbGrupoSave.reset();
		 					NS.txtGrupoSave.setValue('');
		 					NS.panelAlta.setDisabled(true);
		 					
		 				}
		 			}
		 		}
		 	}
		]
	});
	

	
	NS.insertaCodigo = function(){
		
		var idGrupo = "";
		var idRubro = "";
		var descRubro = "";
		var ingresoEgreso = 0;
		var ingresoEgresoStr = "";
		
		idGrupo = Ext.getCmp(PF + 'txtGrupoSave').getValue();
		idRubro = Ext.getCmp(PF + 'txtIdCodigo').getValue();
		descRubro = Ext.getCmp(PF + 'txtDescCodigo').getValue();
		ingresoEgreso = Ext.getCmp(PF + 'optionsIngresoEgreso').getValue().getGroupValue();
		
		if( ingresoEgreso == 1 ){
			ingresoEgresoStr = 'I';
		}
		
		if( ingresoEgreso == 2 ){
			ingresoEgresoStr = 'E';
		}
		
		
		if (idGrupo != '' && idRubro != '' && descRubro != '' && ingresoEgresoStr != '' ){			
			CodigosAction.insertaRubro(idGrupo, idRubro, descRubro, ingresoEgresoStr, function(resultado, e){
				if (resultado != 0 && resultado != '' && resultado != undefined){
					Ext.Msg.alert('SET', resultado);
					NS.buscar();
					
					if ( resultado != 'El rubro ya existe') {
						NS.limpiar(0);
						NS.cmbGrupoSave.reset();
	 					NS.txtGrupoSave.setValue('');
						NS.panelAlta.setDisabled(true);
					}
					
				}
				else
					Ext.Msg.alert('SET', resultado);
			});
		}
		else{
			Ext.Msg.alert('SET', 'Los datos no estan completos');
		}
		
	};
	
	/******************************************************************
	 *                       PANEL ALTA RUBROS
	 */
	
	
	/************************************************************************************
	 * 										GRUPOS 
	 */
	
	NS.storeLlenaGridGrupos = new Ext.data.DirectStore({
		paramOrder:['idTipoMovto'],
		paramsAsHash: false,
		baseParams:{
		idTipoMovto:'N'			
		},		
		root: '',
		directFn: CodigosAction.llenarComboGrupo,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
			//{name: 'idPoliza'},
			//{name: 'decPoliza'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridGrupos, msg: "Cargando información..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existe información para estos parametros');
			}
		}
	});
	
	//NS.storeLlenaGridGrupos.load();

	NS.columnaSeleccionGrupos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	NS.columnasGridGrupos = new Ext.grid.ColumnModel([
	    {header: 'Id Grupo', width: 80, dataIndex: 'idGrupo', sortable: true},
	    {header: 'Descripcion', width: 300, dataIndex: 'descGrupo', sortable: true}
	    //{header: 'Id Poliza', width: 80, dataIndex: 'idPoliza', sortable: true},
	    //{header: 'Descripcion Poliza', width: 300, dataIndex: 'decPoliza', sortable: true},
	    
	    
	]);
	
	NS.gridConsultaGrupos = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridGrupos,
		id: PF + 'gridConsulta',
		name: PF + 'gridConsulta',
		cm: NS.columnasGridGrupos,
		sm: NS.columnaSeleccionGrupos,
		x: 0,
		y: 0,
		width: 960,
		height: 200,
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
	
	
	NS.storeCmbPolizas = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: CodigosAction.getPolizas,
		fields:
		[
		 	{name: 'idCodigo'},
		 	{name: 'descCodigo'}
		 	
		],
		idProperty: 'idCodigo',
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCmbPolizas, msg: "Cargando Bancos..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'NO HAY BANCOS RELACIONADOS CON LA EMPRESA PAGADORA');
				}
				else{
				}
			}
		}
	});
	
	var tplB = new Ext.XTemplate('<tpl for="."><div class="x-combo-list-item">{idCodigo}-{descCodigo}</div></tpl>');
	NS.cmbPolizas = new Ext.form.ComboBox({
		store: NS.storeCmbPolizas,
		id: PF + 'cmbPolizas',
		name: PF + 'cmbPolizas',
		width: 300,
		x: 400,
		y: 15,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idCodigo',
		displayField: 'descCodigo',
		tpl:tplB,
		autocomplete: true,
		emptyText: 'SELECCIONE LA POLIZA',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor){
					
					
				}	
			}
		}
	});
	

	
	
	NS.panelGridGrupos = new Ext.form.FieldSet({
		title: 'Resultados Grupos',
		x: 0,
		y: 90,
		width: 985,
		height: 280,
		layout: 'absolute',
		hidden: true,
		items:
		[		 
		 
		 	
		 	
		 	NS.gridConsultaGrupos,	
		 	{
		 		xtype: 'button',
		 		text: 'Excel',
		 		x: 850,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					parametros='?nomReporte=excelGrupos';
 							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);	
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
	        	text: 'Crear Nuevo',
	        	x: 750,
	        	y: 220,
	        	width: 80,
	        	height: 22,
	        	listeners:
	        	{
	        		click:
	        		{
	        			fn: function(e)
	        			{
	        				NS.panelAltaGrupos.setDisabled(false);
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'button',
	        	text: 'Eliminar',
	        	x: 650,
	        	y: 220,
	        	width: 80,
	        	height: 22,
	        	//hidden:true,
	        	listeners:
	        	{
	        		click:
	        		{
	        			fn: function(e)
	        			{
	        				if(NS.gridConsultaGrupos.getSelectionModel().getSelections().length > 0){
	        					NS.eliminar(NS.gridConsultaGrupos.getSelectionModel().getSelections()[0].get('idGrupo'), 
	        							0,'grupo');
	        				}
	        				else{
	        					Ext.Msg.alert('SET', 'Debe seleccionar un registro');
	        				}	
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'button',
	        	text: 'Modificar',
	        	x: 550,
	        	y: 220,
	        	width: 80,
	        	height: 22,
	        	//hidden:true,
	        	listeners:
	        	{
	        		click:
	        		{
	        			fn: function(e)
	        			{
	        				if(NS.gridConsultaGrupos.getSelectionModel().getSelections().length > 0){
	        					NS.panelAltaGrupos.setDisabled(false);
	        					Ext.getCmp(PF + 'txtIdGrupo').setValue(NS.gridConsultaGrupos.getSelectionModel().getSelections()[0].get('idGrupo'));
	        					Ext.getCmp(PF + 'txtDescGrupo').setValue(NS.gridConsultaGrupos.getSelectionModel().getSelections()[0].get('descGrupo'));
	        					//NS.cmbPolizas.setValue(NS.gridConsultaGrupos.getSelectionModel().getSelections()[0].get('idPoliza'));
	        				}
	        				else{
	        					Ext.Msg.alert('SET', 'Debe seleccionar un registro');
	        				}	
	        			}
	        		}
	        	}
	        },

		 	
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 650,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		hidden:true,
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
		 		x: 750,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		hidden:true,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 				
		 				}
		 			}
		 		}
		 	}
		 	
		]
		
		
	});
	
	
	NS.lblDescPoliza = new Ext.form.Label({
		text: 'Descripcion de la poliza',
		x: 405,
		y: 0
	});
	
	//LABEL
	NS.lblIdGrupo = new Ext.form.Label({
		text: 'Id Grupo',
		x: 0,
		y: 0
	});
	
	//LABEL DE ALTA DE CODIGO
	NS.lblDescGrupo = new Ext.form.Label({
		text: 'Descripción del Grupo',
		x: 65,
		y: 0
	});
	
	
	
	//TEXTFIELD PARA ALTA
	NS.txtIdGrupo = new Ext.form.TextField({
		id: PF + 'txtIdGrupo',
		name: PF + 'txtIdGrupo',
		x: 0,
		y: 15,
		width: 50		
	});
	
	NS.txtDescGrupo = new Ext.form.TextField({
		id: PF + 'txtDescGrupo',
		name: PF + 'txtDescGrupo',
		x: 60,
		y: 15,
		width: 300
	});
	
	NS.panelAltaGrupos = new Ext.form.FieldSet({
		title: 'Alta De Grupos',
		x: 0,
		y: 380,
		width: 985,
		height: 80,
		layout: 'absolute',
		disabled: true,
		hidden: true,
		items:
		[
		 	NS.lblIdGrupo,
		 	NS.lblDescGrupo,
		 	NS.txtIdGrupo,
		 	NS.txtDescGrupo,
		 	//NS.cmbPolizas,
		 	//NS.lblDescPoliza,
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
		 					NS.insertaGrupo();
		 					
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
		 					NS.limpiarGrupos(0);
		 					NS.panelAltaGrupos.setDisabled(true);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.limpiarGrupos = function(todo){	
		
		NS.panelAltaGrupos.setDisabled(true);
		
		Ext.getCmp(PF + 'txtIdGrupo').setValue('');
		Ext.getCmp(PF + 'txtDescGrupo').setValue('');
		
		
		if (todo == 1){ //Limpia toda la pantalla
			
			NS.gridConsultaGrupos.store.removeAll();
			NS.gridConsultaGrupos.getView().refresh();
		}
	};
	
	//FUNCIONES
	NS.buscarGrupo = function(){
		NS.gridConsultaGrupos.store.removeAll();
		NS.gridConsultaGrupos.getView().refresh();
		
		NS.storeLlenaGridGrupos.load();		
	};

	
	NS.insertaGrupo = function(){
		
		var idGrupo = "";
		var descGrupo = "";
		//var idPoliza = "";
		
		idGrupo = Ext.getCmp(PF + 'txtIdGrupo').getValue();
		descGrupo = Ext.getCmp(PF + 'txtDescGrupo').getValue();
		//idPoliza = NS.cmbPolizas.getValue();
		
		if (idGrupo != '' && descGrupo != ''){
			//console.log(idGrupo + " " + descGrupo + " " + idPoliza);
			
			CodigosAction.insertaGrupo(idGrupo, descGrupo, function(resultado, e){
				if (resultado != 0 && resultado != '' && resultado != undefined){
					Ext.Msg.alert('SET', resultado);
					NS.buscarGrupo();
					
					if (resultado != 'El Grupo ya existe') {
						NS.limpiarGrupos(0);
						NS.panelAltaGrupos.setDisabled(true);
					}

				}else{
					Ext.Msg.alert('SET', resultado);
				}
			});
			
		}
		
		else{
			
			Ext.Msg.alert('SET', 'Los datos No estan completos');
			
		}
		
	};


	
	/************************************************************************************
	 * 										GRUPOS 
	 */
	
	
	
	/************************************************************************************
	 * 										POLIZAS 
	 */
	
	
	
	NS.lblPoliza = new Ext.form.Label({
		text: 'Id Poliza',
		x: 0,
		y: 0
	});
	

	NS.txtPoliza = new Ext.form.TextField({
		id: PF + 'txtPoliza',
		name: PF + 'txtPoliza',
		x: 0,
		y: 15,
		width: 80
	});
	
	NS.lblDescPoliza = new Ext.form.Label({
		text: 'Nombre Poliza',
		x: 100,
		y: 0
	});
	
	NS.txtDescPoliza = new Ext.form.TextField({
		id: PF + 'txtDescPoliza',
		name: PF + 'txtDescPoliza',
		x: 100,
		y: 15,
		width: 300
	});


	
	NS.storeLlenaGridPolizas = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: CodigosAction.getPolizas,
		fields:
		[
		 	{name: 'idCodigo'},
		 	{name: 'descCodigo'},
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridPolizas, msg: "Cargando informacion..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existe información para mostrar');
			}
		}
	});	

	
	NS.columnaSeleccionPolizas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	NS.columnasGridPolizas = new Ext.grid.ColumnModel([
	    {header: 'Id Poliza', width: 80, dataIndex: 'idCodigo', sortable: true},
	    {header: 'Descripcion', width: 300, dataIndex: 'descCodigo', sortable: true},
	]);
	
	NS.gridConsultaPolizas = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridPolizas,
		id: PF + 'gridConsultaPolizas',
		name: PF + 'gridConsultaPolizas',
		cm: NS.columnasGridPolizas,
		sm: NS.columnaSeleccionPolizas,
		x: 0,
		y: 0,
		width: 960,
		height: 200,
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

	
	NS.panelGridPolzas = new Ext.form.FieldSet({
		title: 'Resultados Polizas',
		x: 0,
		y: 90,
		width: 985,
		height: 280,
		layout: 'absolute',
		items:
		[
		 	NS.gridConsultaPolizas,
		 	{
		 		xtype: 'button',
		 		text: 'Excel',
		 		x: 850,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					parametros='?nomReporte=excelGrupoPolizas';
 							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);	
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
	        	text: 'Crear Nuevo',
	        	x: 750,
	        	y: 220,
	        	width: 80,
	        	height: 22,
	        	listeners:
	        	{
	        		click:
	        		{
	        			fn: function(e)
	        			{
	        				NS.panelAltaPolizas.setDisabled(false);
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'button',
	        	text: 'Eliminar',
	        	x: 650,
	        	y: 220,
	        	width: 80,
	        	height: 22,
	        	listeners:
	        	{
	        		click:
	        		{
	        			fn: function(e)
	        			{
	        				
	        				var seleccion = NS.gridConsultaPolizas.getSelectionModel().getSelections();
	        				if(seleccion.length > 0){
	        					Ext.Msg.confirm('SET', 'Esta seguro que desea eliminar la poliza seleccionada?', function (btn){
	        						if (btn === 'yes'){
	        							CodigosAction.eliminarPoliza(parseInt(seleccion[0].get('idCodigo')), function(resultado, e){
	    	        						NS.storeLlenaGridPolizas.reload();
	    	        					});
	        						}
	        						
	        					});	
	        				}
	        					
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'button',
	        	text: 'Modificar',
	        	x: 550,
	        	y: 220,
	        	width: 80,
	        	height: 22,
	        	listeners:
	        	{
	        		click:
	        		{
	        			fn: function(e)
	        			{
	        				var seleccion = NS.gridConsultaPolizas.getSelectionModel().getSelections();
	        				if(seleccion.length > 0){
	        					NS.panelAltaPolizas.setDisabled(false);
	        					NS.txtPoliza.setValue(seleccion[0].get('idCodigo'));
	        					NS.txtDescPoliza.setValue(seleccion[0].get('descCodigo'));
	        					
	        				}else{
	        					Ext.Msg.alert('SET', 'Debe Seleccionar una poliza');
	        				}
	        			}
	        		}
	        	}
	        }
		 ]
	});	 
	
		 
	NS.panelAltaPolizas = new Ext.form.FieldSet({
		title: 'Alta De Polizas',
		x: 0,
		y: 380,
		width: 985,
		height: 80,
		layout: 'absolute',
		disabled: true,
		items:
		[
		 NS.lblPoliza,
		 NS.txtPoliza,
		 NS.lblDescPoliza,
		 NS.txtDescPoliza,
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
		 					if(NS.txtPoliza.getValue() != '' && NS.txtDescPoliza.getValue() != ''){
		 						CodigosAction.agregarPoliza(parseInt(NS.txtPoliza.getValue()), NS.txtDescPoliza.getValue(), function(resultado, e){
		 							NS.storeLlenaGridPolizas.reload();
		 							Ext.Msg.alert('SET' ,resultado);
		 						});
		 						NS.panelAltaPolizas.setDisabled(true);
	        					NS.txtPoliza.setValue('');
	        					NS.txtDescPoliza.setValue('');
		 						
		 					}else{
		 						Ext.Msg.alert('SET', 'No puede haber campos vacios para polizas');
		 					}
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
		 					NS.txtPoliza.setValue('');
		 					NS.txtDescPoliza.setValue('');
		 					NS.panelAltaPolizas.setDisabled(true);
		 				}
		 			}
		 		}
		 	}
		 
		 ]
	});
	
	
	NS.storeLlenaGridPolizas.load();
	
	
	
	
	/************************************************************************************
	 * 										RUBROS - CUENTAS CONTABLES
	 */
	
	
	
	/*****************************************************************************************
	 *****************************************************************************************
	 *    						BUSQUEDA RUBROS CUENTAS CONTABLES
	 *****************************************************************************************
	 *****************************************************************************************
	 */
	
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
	
	//LABEL
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
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
	
	//PANEL
	NS.panelBusquedaRubrosCuentas = new Ext.form.FieldSet({
		title: 'Busqueda de Rubros Cuentas Contables',
		x: 185, 
		y: 0,
		width: 800,
		height: 80,
		hidden:true,
		layout: 'absolute',
		items:
		[
		 
		 	NS.lblEmpresa,
		 	NS.txtEmpresa,
		 	NS.cmbEmpresa,	
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 650,
		 		y: 10,
		 		width: 80,
		 		height: 22,
		 		listeners:{ 
		 			click: {
		 				fn: function(e) {
		 					NS.buscarGuiasContables();
		 				}
		 			}
		 		}	
		 	}
		]
	});
	
	//FUNCIONES
	NS.buscarGuiasContables = function(){
		NS.gridConsultaRubrosCuentas.store.removeAll();
		NS.gridConsultaRubrosCuentas.getView().refresh();
		
		NS.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
		
		if (NS.noEmpresa == 0)
			NS.noEmpresa = 0;
		
		NS.storeLlenaGridRubrosCuentas.baseParams.noEmpresa = parseInt(NS.noEmpresa);		
		NS.storeLlenaGridRubrosCuentas.load();		
	};


	/*****************************************************************************************
	 *****************************************************************************************
	 *    						BUSQUEDA RUBROS CUENTAS CONTABLES
	 *****************************************************************************************
	 *****************************************************************************************
	 */
	
	
	
	NS.storeLlenaGridRubrosCuentas = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {noEmpresa: 0},
		paramOrder: ['noEmpresa'],
		directFn: CodigosAction.getGuiasContables,
		fields:
		[
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'},
		 	{name: 'idGrupo'},
		 	{name: 'descGrupo'},
		 	{name: 'idRubro'},
		 	{name: 'descRubro'},
		 	{name: 'cuentaContable'}		 	
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridRubrosCuentas, msg: "Cargando información..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existe información para estos parametros');
			}
		}
	});	

	NS.columnaSeleccionRubrosCuentas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGridRubrosCuentas = new Ext.grid.ColumnModel([
	    {header: 'No Empresa', width: 80, dataIndex: 'noEmpresa', sortable: true},
	    {header: 'Empresa', width: 200, dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'Id Grupo', width: 80, dataIndex: 'idGrupo', sortable: true},
	    {header: 'Descripción', width: 200, dataIndex: 'descGrupo', sortable: true},
	    {header: 'Id Rubro', width: 80, dataIndex: 'idRubro', sortable: true},
	    {header: 'Descripción', width: 200, dataIndex: 'descRubro', sortable: true},
	    {header: 'Cuenta Contable', width: 80, dataIndex: 'cuentaContable', sortable: true}
	    
	]);
	
	
	NS.gridConsultaRubrosCuentas = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridRubrosCuentas,
		id: PF + 'gridConsulta',
		name: PF + 'gridConsulta',
		cm: NS.columnasGridRubrosCuentas,
		sm: NS.columnaSeleccionRubrosCuentas,
		x: 0,
		y: 0,
		width: 960,
		height: 200,
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

	
	
		

	
	NS.panelGridRubrosCuentas = new Ext.form.FieldSet({
		title: 'Resultados Rubros Cuentas Contables',
		x: 0,
		y: 90,
		width: 985,
		height: 280,
		layout: 'absolute',
		hidden:true,
		items:
		[		 	
		 	NS.gridConsultaRubrosCuentas,
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 750,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.panelAltaRubrosCuentas.setDisabled(false);
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 750,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		//hidden:true,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		 					
		 	
		 					if (registroSeleccionado.length > 0){
		 						alert('ESCUCHANDO');
		 					}
		 					else{
		 						Ext.Msg.alert('SET', 'Debe de seleccionar algun registro');
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 750,
		 		y: 220,
		 		width: 80,
		 		height: 22,
		 		hidden:true,
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
		 		y: 220,
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
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ PANEL ALTA RUBROS CUENTAS CONTABLES
	 * 
	 * 
	 */
	
	//STORE
	NS.storeLlenaComboEmpresaSaveGC = new Ext.data.DirectStore({
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
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboEmpresaSaveGC, msg: "Cargando empresas..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Empresas dadas de alta');			
			}
		}
	});
	NS.storeLlenaComboEmpresaSaveGC.load();
	
	
	
	//COMBO
	NS.cmbEmpresaSaveGC = new Ext.form.ComboBox({
		store: NS.storeLlenaComboEmpresaSaveGC,
		id: PF + 'cmbEmpresaSaveGC',
		name: PF + 'cmbEmpresaSaveGC',
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
					BFwrk.Util.updateComboToTextField(PF + 'txtEmpresaSaveGC', NS.cmbEmpresaSaveGC.getId());
				}
			}
		}		
	});
	
	//LABEL
	NS.lblEmpresaSaveGC = new Ext.form.Label({
		text: 'Empresa',
		x: 0,
		y: 0
	});
	
	
	
	//TEXFIELD
	NS.txtEmpresaSaveGC = new Ext.form.TextField({
		id: PF + 'txtEmpresaSaveGC',
		name: PF + 'txtEmpresaSaveGC',
		x: 0,
		y: 15,
		width: 50,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresaSaveGC', NS.cmbEmpresaSaveGC.getId());
				}
			}
		}
	});
	
	NS.storeLlenaComboGrupoSaveGC = new Ext.data.DirectStore({
		paramOrder:['idTipoMovto'],
		paramsAsHash: false,
		baseParams:{
		idTipoMovto:'N'			
		},		
		root: '',
		directFn: TraspasosAction.llenarComboGrupo,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboGrupoSaveGC, msg: "Cargando información..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existe información para estos parametros');
				
			}
		}
	});
	
	NS.storeLlenaComboGrupoSaveGC.load();
	
	
	//COMBO
	NS.cmbGrupoSaveGC = new Ext.form.ComboBox({
		store: NS.storeLlenaComboGrupoSaveGC,
		id: PF + 'cmbGrupoSaveGC',
		name: PF + 'cmbGrupoSaveGC',
		width: 300,
		x: 60,
		y: 70,
		typeAhead: true,
		mode: 'local',	
		selecOnFocus: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Seleccione el grupo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtGrupoSaveGC', NS.cmbGrupoSaveGC.getId());
					Ext.getCmp(NS.txtIdRubroSaveGC.getId()).setValue('');
					Ext.getCmp(NS.cmbRubroSaveGC.getId()).setValue('');
					NS.storeRubroSaveGC.removeAll();
					NS.storeRubroSaveGC.baseParams.idGrupo = parseInt(NS.txtGrupoSaveGC.getValue());									
					NS.storeRubroSaveGC.load();
					
					
				}
			}
		}		
	});
	
	
	
	
	
	
	
	//TEXFIELD
	NS.txtGrupoSaveGC = new Ext.form.TextField({
		id: PF + 'txtGrupoSaveGC',
		name: PF + 'txtGrupoSaveGC',
		x: 0,
		y: 70,
		width: 50,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtGrupoSaveGC', NS.cmbGrupoSaveGC.getId());
				}
			}
		}
	});
	
	NS.lblGrupoSaveGC = new Ext.form.Label({
		text: 'Grupo',
		x: 0,
		y: 50
	});
	
	NS.storeRubroSaveGC = new Ext.data.DirectStore( {	
		root: '',
		paramOrder:['idGrupo'],	            
			paramsAsHash: false,
			directFn: TraspasosAction.llenarComboRubros,
			idProperty: 'idRubro',  		
			fields: [
				{name: 'idRubro' },
				{name: 'descRubro'}
			]	
		});
			
		NS.lblRubroSaveGC = new Ext.form.Label({                
			text: 'Rubro:',
			x: 400,
			y: 50,
			width: 120
		});
		
	
		
		NS.txtIdRubroSaveGC = new Ext.form.NumberField({
	        name  : PF + 'txtIdRubroSaveGC',
			id    : PF + 'txtIdRubroSaveGC',    
	        x     : 400,
	        y     : 70,
	        width : 50        
	        ,listeners:{
		    	change:{
		   			fn:function(){
		   		
		   				if( Ext.getCmp( PF + 'txtIdRubroSaveGC' ).getValue() !== "" ){
		   			
		   					var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdRubroSaveGC', NS.cmbRubroSaveGC.getId());
		  					
		  				
		   				}else{
		  						              			
		  					Ext.getCmp(NS.cmbRubroSaveGC.getId()).setValue('');
		  					Ext.getCmp(NS.txtIdRubroSaveGC.getId()).setValue('');
						
		  				}//End if
		  			
		        	}//End fn
		        
				}//End change
		
			}//End listeners

	    });
		
		
		NS.cmbRubroSaveGC = new Ext.form.ComboBox({
			 store: NS.storeRubroSaveGC	
			,name: PF+'cmbRubroSaveGC'
			,id: PF+'cmbRubroSaveGC'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
	       ,hidden: false
			,x :460
			,y :70
			,width :240
			,valueField:'idRubro'
			,displayField:'descRubro'
			,autocomplete: true
			,emptyText: 'Seleccione el rubro'
			,triggerAction: 'all'
			,value: ''
			,listeners:{
				select:{
					    	fn:function() {
									
									
									BFwrk.Util.updateComboToTextField(PF+'txtIdRubroSaveGC', NS.cmbRubroSaveGC.getId());
									
					    	}//end function
					    							    	
						},//end select
				change:{
					    	fn:function(combo, newValue, oldValue) {						    		
				    		  
								if( newValue == '' ){		
		             					              			
									//Ext.getCmp( PF + 'txtIdRubroIngreso' ).setValue('');
		             				         				
		             			}else{
		             				
		             				//Ext.getCmp( PF + 'txtIdRubroIngreso' ).forceSelection= true; 
		             				
		             			}	
	             															
				    		}//end function

					    	
						   }//end change
		
				}//end listeners

				});
		
		//LABEL DE ALTA DE CODIGO
		NS.lblDescSaveGC = new Ext.form.Label({
			text: 'Cuenta Contable',
			x: 400,
			y: 0
		});
		
		//TEXTFIELD PARA ALTA
		NS.txtDescSaveGC = new Ext.form.TextField({
			id: PF + 'txtDescSaveGC',
			name: PF + 'txtDescSaveGC',
			x: 400,
			y: 15,
			width: 200		
		});

		

		

		
		//CONTROLES RUBRO INGRESO


	
	
	
	NS.panelAltaRubrosCuentas = new Ext.form.FieldSet({
		title: 'Alta Rubros Cuentas Contables',
		x: 0,
		y: 380,
		width: 985,
		height: 300,
		layout: 'absolute',
		disabled: true,
		hidden:true,
		items:
		[
		 	NS.lblEmpresaSaveGC,
		 	NS.txtEmpresaSaveGC,
		 	NS.cmbEmpresaSaveGC,
		 	NS.lblGrupoSaveGC,
			NS.txtGrupoSaveGC,
			NS.cmbGrupoSaveGC,
			NS.txtIdRubroSaveGC,
			NS.cmbRubroSaveGC,
			NS.lblRubroSaveGC,
			NS.lblDescSaveGC,
			NS.txtDescSaveGC,
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
		 		NS.insertaGuiaContable();
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
							NS.limpiarGuiaContable(0);
							NS.panelAltaRubrosCuentas.setDisabled(true);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	

	
	NS.insertaGuiaContable = function(){
		
		var noEmpresa = "";
		var idGrupo = "";
		var idRubro = "";
		var cuentaContable = "";
		
		noEmpresa = Ext.getCmp(PF + 'txtEmpresaSaveGC').getValue();
		idGrupo = Ext.getCmp(PF + 'txtGrupoSaveGC').getValue();
		idRubro = Ext.getCmp(PF + 'txtIdRubroSaveGC').getValue() + '';
		cuentaContable = Ext.getCmp(PF + 'txtDescSaveGC').getValue();
		
			
		if (noEmpresa != '' && idGrupo != '' && idRubro != '' && cuentaContable != ''){
			
			CodigosAction.insertaGuiaContable(noEmpresa,idGrupo, idRubro, cuentaContable,function(resultado, e){
				if (resultado != 0 && resultado != '' && resultado != undefined){
					
					Ext.Msg.alert('SET', resultado);
					NS.buscarGC();
					
					if ( resultado != 'La guia contable ya existe') {
						NS.limpiarGuiaContable(0);
						NS.panelAltaRubrosCuentas.setDisabled(true);
					}

				}
				else{
					Ext.Msg.alert('SET', resultado);
				}
			});
			
		}
		
		else{
			
			Ext.Msg.alert('SET', 'Los datos no estan completos');
			
		}
		
	};
	
	NS.buscarGC = function(){
		NS.gridConsultaRubrosCuentas.store.removeAll();
		NS.gridConsultaRubrosCuentas.getView().refresh();
		
		NS.storeLlenaGridRubrosCuentas.load();		
	};
	

	NS.limpiarGuiaContable = function(todo){	
		
		NS.panelAltaRubrosCuentas.setDisabled(true);
		
		Ext.getCmp(PF + 'txtEmpresaSaveGC').setValue('');
		Ext.getCmp(PF + 'txtGrupoSaveGC').setValue('');
		Ext.getCmp(PF + 'txtIdRubroSaveGC').setValue('');
		Ext.getCmp(PF + 'txtDescSaveGC').setValue('');
		
		Ext.getCmp(PF + 'cmbEmpresaSaveGC').setValue('');
		Ext.getCmp(PF + 'cmbGrupoSaveGC').setValue('');
		Ext.getCmp(PF + 'cmbRubroSaveGC').setValue('');
		
		if (todo == 1){ //Limpia toda la pantalla
			
			NS.gridConsultaRubrosCuentas.store.removeAll();
			NS.gridConsultaRubrosCuentas.getView().refresh();
		}
	};

	
	/************************************************************************************
	 * 									RUBROS - CUENTAS CONTABLES 
	 */
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1010,		
		height: 550,
		layout: 'absolute',
		items:
		[				 
		 	NS.optionsBusqueda,
		 	NS.panelBusqueda,
		 	NS.panelGrid,
		 	NS.panelAlta,
		 	NS.panelAltaGrupos,
			NS.panelGridGrupos,
			NS.panelBusquedaRubrosCuentas,
		 	NS.panelGridRubrosCuentas,
		 	NS.panelAltaRubrosCuentas,
		 	NS.panelGridPolzas,
		 	NS.panelAltaPolizas
			
	    ]
	});
	
	NS.codigos = new Ext.FormPanel ({
		title: 'Mantenimiento de Grupos y Rubros',
		width: 1300,
		height: 550,
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
 