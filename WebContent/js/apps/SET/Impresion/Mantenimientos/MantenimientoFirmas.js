/*
 * @author: Victor H. Tello
 * @since: 20/Mar/2012
 */

/*
 * Modificacion 06 de enero del 2015
 * Por: YEC
 */
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Impresion.Mantenimientos.MantenimientoFirmas');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.idBanco='';
	
	NS.idBancoBusq='';
	NS.idChequeraBusq='';
	NS.idPersonaBusq='';
	
	NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	verificaComponentesCreados();
	
	/***********************************************************************************
	 * 					COMPONENTES PARA LA BUSQUEDA (BANCO, CHEQUERA O NOMBRE)		   *
	 ***********************************************************************************/
	
	/********************************************
     * 				COMBO BANCO       		    *
	 ********************************************/
	
	NS.labBancoBusq = new Ext.form.Label({
		text: 'Banco:',
		x: 10
	});
	
	NS.txtBancoBusq = new Ext.form.TextField({
		id: PF + 'txtBancoBusq',
		name: PF + 'txtBancoBusq',
		x: 10,
		y: 15,
		width: 50,
		tabIndex: 0,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtBancoBusq', NS.cmbBancoBusq.getId());
						if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
							NS.llenaChequeraBusq(valueCombo);
						}else{
							NS.llenaChequeraBusq(0);
						}
					}else
						NS.llenaChequeraBusq(0);
				}
			}
		}
	});
	
	//Store banco
	NS.storeBancosBusq = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: MantenimientosAction.llenaComboBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'}, 
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records) {
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen bancos!!');
				NS.myMask.hide();
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	NS.myMask.show();
	NS.storeBancosBusq.load();
	
	//Combo banco
	NS.cmbBancoBusq = new Ext.form.ComboBox({
		store: NS.storeBancosBusq,
		id: PF + 'cmbBancoBusq',
		x: 65,
	    y: 15,
	    width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione Banco',
		triggerAction: 'all',
		value: '',
		tabIndex: 1,
		listeners: {
			select: {
				fn:function(combo, valor) {
					//Limpiar combos
					NS.storeChequerasBusq.removeAll();
					NS.cmbChequerasBusq.reset();
					
					BFwrk.Util.updateComboToTextField(PF + 'txtBancoBusq', NS.cmbBancoBusq.getId());
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != '') {
						NS.llenaChequeraBusq(combo.getValue());
						NS.idBancoBusq=combo.getValue();
						NS.idChequeraBusq='';
						if(NS.idChequeraBusq==''){
							NS.cmbPersonasBusq.reset();
							NS.txtNoFirmaBusq.setValue("");
							NS.storePersonasBusq.removeAll();
							NS.storePersonasBusq.baseParams.idBanco=NS.idBancoBusq+'';
							NS.storePersonasBusq.baseParams.cuenta='';
							NS.storePersonasBusq.baseParams.busqueda=true;
							NS.storePersonasBusq.load();
						}
					}
				}
			}
		}
	});
	
	/********************************************
     * 			COMBO CHEQUERA      		    *
	 ********************************************/
	
	NS.labChequeraBusq = new Ext.form.Label({
		text: 'Chequera:',
		x: 265
	});
	
	//storeChequeras en base al banco seleccionado
	NS.storeChequerasBusq = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['idBanco'],
		baseParams: {idBanco: 0},
		directFn: MantenimientosAction.llenaComboChequeras,
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'}, 
		],
		listeners: {
			load: function(s, records) {
				var myMaskAux = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequerasBusq, msg:"Cargando..."});		
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen chequeras asignadas para el banco');
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	
	//Combo Chequeras en base al banco
	NS.cmbChequerasBusq = new Ext.form.ComboBox({
		store: NS.storeChequerasBusq,
		id: PF + 'cmbChequerasBusq',
		x: 265,
		y: 15,
		width: 170,
		tabIndex: 2,
		typeAhead: true,
		selecOnFocus: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'idStr',
		displayField: 'idStr',
		emptyText: 'Seleccione Chequera',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn:function(combo, valor){
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != ''){
						NS.idChequeraBusq=combo.getValue();
						if(NS.idPersonaBusq==''|| NS.idChequeraBusq!=''){
							NS.cmbPersonasBusq.reset();
							NS.txtNoFirmaBusq.setValue("");
							NS.storePersonasBusq.removeAll();
							NS.storePersonasBusq.baseParams.idBanco=NS.idBancoBusq+'';
							NS.storePersonasBusq.baseParams.cuenta=NS.idChequeraBusq+'';
							NS.storePersonasBusq.baseParams.busqueda=true;
							NS.storePersonasBusq.load();
						}
					}
				}
			}
		}
	});
	
	/********************************************
     * 		    	COMBO PERSONA      		    *
	 ********************************************/
	
	NS.labNoFirmaBusq = new Ext.form.Label({
		text: 'No. Firma:',
		x: 455
	});
	
	NS.txtNoFirmaBusq = new Ext.form.TextField({
		id: PF + 'txtNoFirmaBusq',
		name: PF + 'txtNoFirmaBusq',
		x: 455,
		y: 15,
		width: 50,
		tabIndex: 3,
		listeners: {
			change: {
				fn: function(caja, valor) {
					var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoFirmaBusq', NS.cmbPersonasBusq.getId());
					if(valueCombo == null || valueCombo == undefined || valueCombo == '') {
						Ext.getCmp(PF + 'txtNoFirmaBusq').setValue('');
						NS.cmbPersonasBusq.reset();
					} 
				}
			}
		}
	});
	
	//storePersonas
	NS.storePersonasBusq = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['idBanco','cuenta','busqueda'],
		baseParams: {idBanco: '',cuenta:'', busqueda:true},
		directFn: MantenimientosAction.llenaComboPersonas,
		idProperty: 'id', 
		fields: [
			 {name: 'id'}, 
			 {name: 'descripcion'}, 
		],
		listeners: {
			load: function(s, records) {
				var myMaskAux = new Ext.LoadMask(Ext.getBody(), {store: NS.storePersonasBusq, msg:"Cargando..."});		
				//if(records.length == null || records.length <= 0)
					//Ext.Msg.alert('SET', 'No existen firmantes disponibles para esta cuenta');
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	NS.storePersonasBusq.load();
	
	
	//Combo Personas
	NS.cmbPersonasBusq = new Ext.form.ComboBox({
		store: NS.storePersonasBusq,
		id: PF + 'cmbPersonasBusq',
		x: 510,
		y: 15,
		width: 170,
		tabIndex: 4,
		typeAhead: true,
		selecOnFocus: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		emptyText: 'Seleccione Persona',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtNoFirmaBusq', NS.cmbPersonasBusq.getId());
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != '') {
						NS.idPersonaBusq=combo.getValue();
					}else{
						NS.cmbPersonasBusq.reset();
						NS.txtNoFirmaBusq.setValue("");
						NS.storePersonasBusq.removeAll();
					}
				}
			}
		}
	});

	NS.llenaChequeraBusq = function(valueCombo) {
		NS.cmbChequerasBusq.reset();
		NS.storeChequerasBusq.removeAll();
		
		if((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0) {
			if(valueCombo == 0) {
				Ext.getCmp(PF + 'txtBancoBusq').setValue('');
				NS.txtNoFirmaBusq.setValue("");
				NS.cmbBancoBusq.reset();
				NS.cmbPersonasBusq.reset();
			}else {
				NS.storeChequerasBusq.baseParams.idBanco = valueCombo;
				NS.storeChequerasBusq.load();
			}
		}
	};
	
	
	/***********************************************************************************
	 * 			FIN DE COMPONENTES PARA LA BUSQUEDA (BANCO, CHEQUERA O NOMBRE)		   *
	 ***********************************************************************************/
	
	/********************************************
     * 				COMBO BANCO       		    *
	 ********************************************/
	
	NS.labBanco = new Ext.form.Label({
		id: PF+'labBanco',
		text: 'Banco:',
		x: 10
	});
	
	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
		name: PF + 'txtBanco',
		x: 10,
		y: 15,
		width: 50,
		tabIndex: 0,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
						if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
							NS.llenaChequera(valueCombo);
						}else
							NS.llenaChequera(0);
					}else
						NS.llenaChequera(0);
				}
			}
		}
	});
	
	//Store banco
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: MantenimientosAction.llenaComboBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'}, 
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records) {
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen bancos!!');
				NS.myMask.hide();
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	NS.myMask.show();
	NS.storeBancos.load();
	
	//Combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancos,
		id: PF + 'cmbBanco',
		x: 65,
	    y: 15,
	    width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione Banco',
		triggerAction: 'all',
		value: '',
		tabIndex: 1,
		listeners: {
			select: {
				fn:function(combo, valor) {
					//Limpiar combos
					NS.cmbPersonas.reset();
					NS.txtNoFirma.setValue("");
					NS.storePersonas.removeAll();
					NS.storeChequeras.removeAll();
					NS.cmbChequeras.reset();
					
					BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBanco.getId());
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != '') {
						NS.llenaChequera(combo.getValue());
						NS.idBanco=combo.getValue();
					}
				}
			}
		}
	});
	
	/********************************************
     * 			COMBO CHEQUERA      		    *
	 ********************************************/
	
	NS.labChequera = new Ext.form.Label({
		id:PF+'labChequera',
		text: 'Chequera:',
		x: 10,
		y: 40
	});
	
	//storeChequeras en base al banco seleccionado
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['idBanco'],
		baseParams: {idBanco: 0},
		directFn: MantenimientosAction.llenaComboChequeras,
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'}, 
		],
		listeners: {
			load: function(s, records) {
				var myMaskAux = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});		
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen chequeras asignadas para el banco');
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	
	//Combo Chequeras en base al banco
	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras,
		id: PF + 'cmbChequeras',
		x: 10,
		y: 60,
		width: 170,
		tabIndex: 2,
		typeAhead: true,
		selecOnFocus: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'idStr',
		displayField: 'idStr',
		emptyText: 'Seleccione Chequera',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn:function(combo, valor){
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != ''){
						NS.cmbPersonas.reset();
						NS.txtNoFirma.setValue("");
						NS.storePersonas.removeAll();
						NS.storePersonas.baseParams.idBanco=NS.idBanco+'';
						NS.storePersonas.baseParams.cuenta=combo.getValue()+'';
						NS.storePersonas.baseParams.tipo='';
						NS.storePersonas.load();
					}
				}
			}
		}
	});
	
	/********************************************
     * 		    	COMBO PERSONA      		    *
	 ********************************************/
	
	NS.labNoFirma = new Ext.form.Label({
		id: PF+ 'labNoFirma',
		text: 'No. Firma:',
		x: 10,
		y:90
	});
	
	NS.txtNoFirma = new Ext.form.TextField({
		id: PF + 'txtNoFirma',
		name: PF + 'txtNoFirma',
		x: 10,
		y: 110,
		width: 50,
		tabIndex: 3,
		listeners: {
			change: {
				fn: function(caja, valor) {
					var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoFirma', NS.cmbPersonas.getId());
					if(valueCombo == null || valueCombo == undefined || valueCombo == '') {
						Ext.getCmp(PF + 'txtNoFirma').setValue('');
						NS.cmbPersonas.reset();
					} 
				}
			}
		}
	});
	
	//storePersonas
	NS.storePersonas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['idBanco','cuenta','busqueda'],
		baseParams: {idBanco: '',cuenta:'', busqueda:false},
		directFn: MantenimientosAction.llenaComboPersonas,
		idProperty: 'id', 
		fields: [
			 {name: 'id'}, 
			 {name: 'descripcion'}, 
		],
		listeners: {
			load: function(s, records) {
				var myMaskAux = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});		
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen firmantes disponibles para esta cuenta');
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	
	
	//Combo Personas
	NS.cmbPersonas = new Ext.form.ComboBox({
		store: NS.storePersonas,
		id: PF + 'cmbPersonas',
		x: 65,
		y: 110,
		width: 170,
		tabIndex: 4,
		typeAhead: true,
		selecOnFocus: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		emptyText: 'Seleccione Persona',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtNoFirma', NS.cmbPersonas.getId());
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != '') {
						
					}else{
						NS.cmbPersonas.reset();
						NS.txtNoFirma.setValue("");
						NS.storePersonas.removeAll();
					}
				}
			}
		}
	});
	
	/********************************************
     * 		    	GRID FIRMAS      		    *
	 ********************************************/
	
	NS.storeFirmas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idBanco:'',idChequera:'',noFirma:''},
		paramOrder: ['idBanco','idChequera','noFirma'],
		directFn: MantenimientosAction.buscaFirmas,
		fields: [
			{name: 'idBanco'},
			{name: 'noPersona'},
			{name: 'idChequera'},
			{name: 'bDeter'},
			{name: 'tipoFirma'},
			{name: 'descBanco'},
			{name: 'nombre'}
		],
		listeners: {
			load: function(s, records) {
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No Existen Firmantes!!');
				NS.myMask.hide();
			},
			exception:function(misc) {
		    	NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: true
	});
	
	NS.columnasFirmas = new Ext.grid.ColumnModel([
        NS.columnaSeleccion,
	    {header: 'No. Firma', width: 50, dataIndex: 'noPersona', sortable: true},
	    {header: 'Nombre', width: 360, dataIndex: 'nombre', sortable: true},
	    {header: 'ID Banco', width: 50, dataIndex: 'idBanco', sortable: true},
	    {header: 'Banco', width: 220, dataIndex: 'descBanco', sortable: true},
	    {header: 'Chequera', width: 200, dataIndex: 'idChequera', sortable: true}
	]);
	
	NS.gridFirmas = new Ext.grid.GridPanel({
		store: NS.storeFirmas,
		id: 'gridFirmas',
		cm: NS.columnasFirmas,
		sm: NS.columnaSeleccion,
		//width: 965,
	    height: 270,
	    stripeRows: true,
	    columnLines: true
	});

	/********************************************
     * 		    	Contenedores        		*
	 ********************************************/
	
	NS.contBusqueda = new Ext.form.FieldSet({
		y: 60,
		//width: 985,
		height: 300,
		layout: 'absolute',
		items: [
		        NS.gridFirmas
		]
	});
	
	
	
	NS.contGeneral = new Ext.form.FieldSet({
		x: 0,
		y: 0,
		//width: 1010,
		height: 440,
		layout: 'absolute',
		items: [
				NS.labBancoBusq,
				NS.txtBancoBusq,	
				NS.cmbBancoBusq,
				NS.labChequeraBusq,
				NS.cmbChequerasBusq,
				NS.labNoFirmaBusq,	
				NS.txtNoFirmaBusq,	
				NS.cmbPersonasBusq,
		        NS.contBusqueda,
		        {
		        	 xtype: 'button',
		        	 text: 'Buscar',
		        	 id: PF + 'btnBuscar',
		        	 x: 700,
		        	 y: 15,
		        	 width: 80,
		        	 listeners: {
		        	 	click: {
		        	 		fn: function(e) {
		        	 			NS.buscar();		        	 		}
		         		}
		         	 }
		         },
		         {
		        	 xtype: 'button',
		        	 text: 'Limpiar',
		        	 id: PF + 'btnLimpiar',
		        	 x: 790,
		        	 y: 15,
		        	 width: 80,
		        	 listeners: {
		        	 	click: {
		        	 		fn: function(e) {
		        	 			NS.limpiarBusq();
		        	 		}
		         		}
		         	 }
		         },
		         {
		        	 xtype: 'button',
		        	 text: 'Nuevo',
		        	 id: PF + 'btnNuevo',
		        	 x: 0,
		        	 y: 370,
		        	 width: 80,
		        	 listeners: {
		        	 	click: {
		        	 		fn: function(e) {
		        	 			//NS.limpiar();
			        	 		NS.nuevo();
			        	 		winEditar.show();
		        	 		}
		         		}
		         	 }
		         },{
		        	 xtype: 'button',
		        	 text: 'Eliminar',
		        	 id: PF + 'btnEliminar',
		        	 x: 90,
		        	 y: 370,
		        	 width: 80,
		        	 listeners: {
		        	 	click: {
		        	 		fn: function(e) {
		        	 			NS.eliminar();
		        	 		}
		         		}
		         	 }
		         },
		         {
		        	 xtype: 'button',
		        	 text: 'Excel',
		        	 id: PF + 'btnExcel',
		        	 x: 180,
		        	 y: 370,
		        	 width: 80,
		        	 listeners: {
		        	 	click: {
		        	 		fn: function(e) {
		        	 			 NS.Excel = true;
		    		    		 NS.validaDatosExcel();
		        	 		}
		         		}
		         	 }
		         }
		]
	});
	
	//Funcion que llena el combo de chequera segun el banco seleccionado
	
	NS.buscar=function(){
		NS.storeFirmas.removeAll();
		NS.storeFirmas.baseParams.idBanco = NS.idBancoBusq+'';
		NS.storeFirmas.baseParams.idChequera = NS.idChequeraBusq+'';
		NS.storeFirmas.baseParams.noFirma = NS.idPersonaBusq+'';
		NS.myMask.show();
		NS.storeFirmas.load();
		NS.gridFirmas.getView().refresh();
		
	}
	
	NS.cancelar=function(){
		 //NS.limpiar();
		 winEditar.hide();
	}
	
	NS.aceptar=function(){
		NS.guardar();
		winEditar.hide();
	}
	
	NS.llenaChequera = function(valueCombo) {
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();
		
		if((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0) {
			if(valueCombo == 0) {
				Ext.getCmp(PF + 'txtBanco').setValue('');
				NS.cmbBanco.reset();
			}else {
				NS.storeChequeras.baseParams.idBanco = valueCombo;
				NS.storeChequeras.load();
			}
		}
	};
	
	NS.limpiar = function() {
		Ext.getCmp(PF + 'txtBanco').setValue('');
		Ext.getCmp(PF + 'cmbBanco').reset();
		Ext.getCmp(PF + 'cmbChequeras').reset();
		Ext.getCmp(PF + 'txtNoFirma').setValue('');
		Ext.getCmp(PF + 'cmbPersonas').reset();
		NS.idBanco='';
		//NS.storeFirmas.removeAll();
		/*NS.myMask.show();
		NS.storeFirmas.load();*/
		//NS.gridFirmas.getView().refresh();
		NS.gridFirmas.removeAll();
		winEditar.hide();
		NS.limpiarBusq();
		
	};
	
	NS.limpiarBusq = function() {
		NS.idBancoBusq='';
		NS.idChequeraBusq='';
		NS.idPersonaBusq='';
		Ext.getCmp(PF + 'txtBancoBusq').setValue('');
		Ext.getCmp(PF + 'cmbBancoBusq').reset();
		Ext.getCmp(PF + 'cmbChequerasBusq').reset();
		Ext.getCmp(PF + 'txtNoFirmaBusq').setValue('');
		Ext.getCmp(PF + 'cmbPersonasBusq').reset();
		NS.storePersonasBusq.removeAll();
		NS.storePersonasBusq.load();
		NS.storeBancosBusq.removeAll();
		NS.storeBancosBusq.load();
		NS.storeFirmas.removeAll();
		NS.gridFirmas.removeAll();
	}
	
	NS.nuevo = function() {
		Ext.getCmp(PF + 'txtBanco').setValue('');
		Ext.getCmp(PF + 'cmbBanco').reset();
		Ext.getCmp(PF + 'cmbChequeras').reset();
		Ext.getCmp(PF + 'txtNoFirma').setValue('');
		Ext.getCmp(PF + 'cmbPersonas').reset();
		NS.idBanco='';
		//NS.storeFirmas.removeAll();
	};
	
	NS.eliminar = function() {
		var registroSeleccionado= NS.gridFirmas.getSelectionModel().getSelections();

		if(registroSeleccionado.length > 0)
		{
			NS.storeFirmas.removeAll();
			var mensaje = '¿Esta seguro de borrar la firma?';
			mensaje += '<br> Datos de la firma que se eliminara';
			mensaje+='<br> Banco: '+ registroSeleccionado[0].get('descBanco');
			mensaje += '<br> Chequera '+ registroSeleccionado[0].get('idChequera');
			mensaje += '<br> Firmante:' + registroSeleccionado[0].get('nombre');
			Ext.Msg.confirm('Información SET',mensaje,function(btn) {  
				if(btn == 'yes') {
					var vector = {};
					var matriz = new Array();
					vector.idBanco = registroSeleccionado[0].get('idBanco');
					vector.cuenta = registroSeleccionado[0].get('idChequera');
					vector.noPersona = registroSeleccionado[0].get('noPersona');
					matriz[0]=vector;
					var jSonString = Ext.util.JSON.encode(matriz);
					MantenimientosAction.eliminarFirma(jSonString, function(resultado, e) {
						if (resultado != '' && resultado != null && resultado != undefined) {
							Ext.Msg.alert('SET', resultado);
							if(resultado=='Exito'){
								//NS.gridFirmas.store.remove(registroSeleccionado[0]);
								NS.storeFirmas.removeAll();
								NS.gridFirmas.removeAll();
								//NS.myMask.show();
								//NS.storeFirmas.load();
								//NS.gridFirmas.getView().refresh();
							}
						}
					});	
				}
				//NS.limpiar();
				NS.buscar();
			});
		}
		else{
			Ext.Msg.alert('SET', 'Debe seleccionar el registro a eliminar');
		}
	};
	
	NS.guardar = function() {
		var vector = {};
		var matriz = new Array();
		var mensaje='';
		
		if(NS.idBanco!='')
			vector.idBanco = NS.idBanco;
		else
			mensaje='Error: Falta ingresar un banco';
		
		if(NS.cmbChequeras.getValue()!='')
			vector.cuenta = NS.cmbChequeras.getValue();
		else
			mensaje='Error: Falta ingresar una chequera';
		
		if(NS.txtNoFirma.getValue()!='')
			vector.noPersona = NS.txtNoFirma.getValue();
		else
			mensaje='Error: Falta ingresar el usuario';
		
		if(mensaje==''){
			var mensaje = '¿Esta seguro de guardar la siguiente firma?';
			mensaje+='<br><br>Datos nuevos';
			mensaje+='<br> Banco: '+ NS.cmbBanco.getRawValue();
			mensaje += '<br> Chequera '+ NS.cmbChequeras.getValue();
			mensaje += '<br> Firmante:' + NS.cmbPersonas .getRawValue();
			
			Ext.Msg.confirm('Información SET', mensaje, function(btn) {  
				if(btn == 'yes') {
					matriz[0]=vector;
					var jSonString = Ext.util.JSON.encode(matriz);
					MantenimientosAction.insetarFirma(jSonString, function(resultado, e) {
						if (resultado != '' && resultado != null && resultado != undefined) {
							if(resultado=='Exito'){
								NS.storeFirmas.removeAll();
								//NS.myMask.show();
								//NS.storeFirmas.load();
								//NS.gridFirmas.getView().refresh();
								NS.gridFirmas.removeAll();
								NS.buscar();
							}
							else
								NS.limpiar();
							
							Ext.Msg.alert('SET', resultado);
						}
					});	
				}
				else{
					Ext.Msg.alert('SET', 'No se ha guardado la firma');
					NS.limpiar();
				}
			});
		}
		else
			Ext.Msg.alert('SET', mensaje);
	};
	
	/**********************Generar el excel *********************************/
	 NS.validaDatosExcel = function() {		
		var registroSeleccionado =NS.gridFirmas.getSelectionModel().getSelections();
		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(registroSeleccionado.length == 0) {
			registroSeleccionado =  NS.storeFirmas.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		
		if(NS.Excel) {
			var matriz = new Array();
			for(var i=0;i<registroSeleccionado.length;i++){
				var vector = {};
				vector.idBanco=registroSeleccionado[i].get('idBanco');
				vector.noPersona=registroSeleccionado[i].get('noPersona');
				vector.idChequera=registroSeleccionado[i].get('idChequera');
				vector.descBanco=registroSeleccionado[i].get('descBanco');
				vector.nombre=registroSeleccionado[i].get('nombre');
				matriz[i] = vector;
			}
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		NS.exportaExcel(jSonString);
		NS.excel = false;
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		MantenimientosAction.exportaExcel(jsonCadena,'firmas', function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=mantenimientoFirmas';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
		/****************Fin de generar excel ***********************/
	
	/*Funcion para componentes de ventana emergente*/
	function verificaComponentesCreados(){
		var compt; 	
		
		compt = Ext.getCmp(PF + 'labBanco');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtBanco');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'cmbBanco');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'labChequera');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'cmbChequeras');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'labNoFirma');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtNoFirma');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'cmbPersonas');
		if(compt != null || compt != undefined ){ compt.destroy(); }
	}
	
	NS.contMantFirmas = new Ext.FormPanel( {
		title: 'Mantenimiento de Firmas',
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            NS.contGeneral
	    ]
	});
	
	/*Ventana para crear nuevo y modificar una firma*/
	
	var winEditar = new Ext.Window({
		title: 'Firmas',
		modal: true,
		shadow: true,
		closeAction: 'hide',
		width: 280,
	   	height: 220,
	   	layout: 'absolute',
	   	plain: true,
	   	resizable:false,
	   	draggable:false,
	   	closable:false,
	    bodyStyle: 'padding:0px;',
	    buttonAlign: 'center',
	    buttons:[
	      {
	    	text:'Aceptar',
	    	tabIndex:10,
	    	handler:function(){
	    		NS.aceptar();
	    	}
	      },
	      
	      {
		    	text:'Cancelar',
		    	tabIndex:11,
		    	handler:function(){
		    		NS.cancelar();
		    	}
		      },
	    ],
	   	items: [
	   	     NS.labBanco,
	         NS.txtBanco,	
	         NS.cmbBanco,
	         NS.labChequera,
	         NS.cmbChequeras,
	         NS.labNoFirma,	
	         NS.txtNoFirma,	
	         NS.cmbPersonas
	   	        ],
	     listeners:{
	    	 show:{
	    		 fn:function(){
	    			
	    		 }
	    	 }, 
	    	  hide:{
	    		  fn:function(){
	    			  //NS.buscar();
	    		  }
	    	  }
	     } 
	
	});
	NS.contMantFirmas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
