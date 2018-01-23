Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Impresion.Mantenimientos.MantenimientoFirmantes');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	
	/***************
	 * COMPONENTES *
	 ***************/
	NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Procesando ..."})
	
	/*
	 * Panel de busqueda
	 */
	
	NS.labFirmaB = new Ext.form.Label({
		text: 'No. Pers:',
		x: 10
	});
	
	NS.labNombreB = new Ext.form.Label({
		text: 'Nombre:',
		x: 90
	});
	
	//Combo personas , store txt cmb
	NS.storePersonasB = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		directFn: MantenimientosAction.llenarComboNoFirmantes, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'}, //numero de persona
			 {name: 'descripcion'}, //nombre de la persona
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay personas disponibles');
				}
				NS.myMask.hide();
			}
		},
		exception:function(misc) {
	    	NS.myMask.hide();
			Ext.Msg.alert('SET', 'Error al cargar los datos para el combo personas, <br> Verificar la conexión');
		}
	});
	NS.myMask.show();
	NS.storePersonasB.load();
	
	NS.txtNoPersonaB = new Ext.form.TextField({
		id: PF+'txtNoPersonaB',
        name: PF+'txtNoPersonaB',
        x: 10,
		y: 15,
		width: 60
    });
	
	NS.cmbPersonasB = new Ext.form.ComboBox({
		store: NS.storePersonasB
		,id: PF + 'cmbPersonasB'
		,name: PF + 'cmbPersonasB'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 90
		,y: 15
		,width: 300
		,valueField:'id'
		,displayField:'descripcion'
		,emptyText: 'Seleccione un nombre'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,autocomplete:true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtNoPersonaB',NS.cmbPersonasB.getId());
				}
			},
			blur:{
				fn:function(combo){
					NS.txtNoPersona.setValue("");
					BFwrk.Util.updateComboToTextField(PF+'txtNoPersonaB',NS.cmbPersonasB.getId());
				}
			}
		}
	});
	
	/*
	 * Ventana emergente
	 */
	
	//verificaComponentesCreados();
	
	NS.lbNoPersona = new Ext.form.Label({
		id: PF + 'lbNoPersona',
		text: 'Persona: ',
		x: 10,
		y : 0		
	});
	
	NS.lbPathFirma = new Ext.form.Label({
		id: PF + 'lbPathFirma',
		text: 'Firma: ',
		x: 10,
		y : 30		
	});
	
	NS.storePersonas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		directFn: MantenimientosAction.llenarComboNoFirmantes, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'}, //numero de persona
			 {name: 'descripcion'}, //nombre de la persona
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay personas disponibles');
				}
				NS.myMask.hide();
			}
		},
		exception:function(misc) {
	    	NS.myMask.hide();
			Ext.Msg.alert('SET', 'Error al cargar los datos para el combo personas, <br> Verificar la conexión');
		}
	});
	
	NS.txtNoPersona = new Ext.form.NumberField({
		id: PF+'txtNoPersona',
        name: PF+'txtNoPersona',
        x: 70,
		y: 0 ,
        width: 50,
        listeners: {
			change: {
				fn: function(caja,valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
							valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoPersona', NS.cmbPersonas.getId());
					}
                }
			}
    	}
    });
	
	NS.cmbPersonas = new Ext.form.ComboBox({
		store: NS.storePersonas
		,id: PF + 'cmbPersonas'
		,name: PF + 'cmbPersonas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 130
		,y: 0 
        ,width: 245
		,valueField:'id'
		,displayField:'descripcion'
		,emptyText: 'Seleccione un nombre'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,autocomplete:true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtNoPersona',NS.cmbPersonas.getId());
				}
			},
			blur:{
				fn:function(combo){
					NS.txtNoPersona.setValue("");
					BFwrk.Util.updateComboToTextField(PF+'txtNoPersona',NS.cmbPersonas.getId());
				}
			}
		}
	});
	
	NS.txtPath = new Ext.form.TextField({
		id: PF+'txtPath',
        name: PF+'txtPath',
        x: 70,
		y: 30 ,
        width: 300
    });
	
	/*
	 * Grid de firmantes
	 */
	
	NS.storeFirmantes = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder: [],
		directFn: MantenimientosAction.buscaFirmantes, 
		fields: [
			{name: 'noPersona'},
			{name: 'nombre'},
			{name: 'pathFirma'}
		],
		listeners: {
			load: function(s, records) {
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No Existen Firmantes!!');
				NS.myMask.hide();
			}
		},
		exception:function(misc) {
	    	NS.myMask.hide();
			Ext.Msg.alert('SET', 'Error al cargar los datos en el grid, <br> Verificar la conexión');
		}
	});
	NS.myMask.show();
	NS.storeFirmantes.load();
	
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: true
	});
	
	NS.columnasFirmantes = new Ext.grid.ColumnModel([
	    NS.columnaSeleccion,
	    {header: 'No. Persona', width: 120, dataIndex: 'noPersona', sortable: true},
	    {header: 'Nombre', width: 250, dataIndex: 'nombre', sortable: true},
	    {header: 'Firma', width:350 , dataIndex: 'pathFirma', sortable: true}
	]);
	
	NS.gridFirmantes = new Ext.grid.GridPanel({
		store: NS.storeFirmantes,
		id: 'gridFirmantes',
		cm: NS.columnasFirmantes,
		//width: 1025,
		height: 340,
	    stripeRows: true,
	    columnLines: true,
	    cm: NS.columnasFirmantes,
	    sm: NS.columnaSeleccion,
	});
	
	/**********
	 * FUNCIONES
	 */
	
	NS.buscar = function(){
		/*NS.storeLlenaGrid.baseParams.nombre = NS.txtNombre.getValue();
		NS.myMask.show();
		NS.storeLlenaGrid.load();*/
	};
	
	NS.opciones=function(op){
		switch (op) {
		case 1:
			 NS.myMask.show();
			 NS.storePersonas.load();
			 winEditarSF.show();
			 winEditarSF.setTitle("Nuevo registro");
			 banderaEleccion=0;
			
			break;

		case 2:
			 var registroSeleccionado= NS.gridFirmantes.getSelectionModel().getSelections();
			 if (registroSeleccionado.length > 0){
				 NS.storePersonas.load();
				 winEditarSF.show();
				 winEditarSF.setTitle("Editar registro");
			     banderaEleccion=1;
				 NS.txtNoPersona.setValue(registroSeleccionado[0].get('noPersona'));
				 NS.cmbPersonas.setValue(registroSeleccionado[0].get('nombre'));
				 NS.txtPath.setValue(registroSeleccionado[0].get('pathFirma'));
				 
				 NS.cmbPersonas.setDisabled(true);
				 NS.txtNoPersona.setDisabled(true);
			
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
				 NS.limpiarTodo();
				 NS.buscar();
			 }
			break;
		case 3:
			var registroSeleccionado= NS.gridFirmantes.getSelectionModel().getSelections();
			 if (registroSeleccionado.length > 0){
				 Ext.Msg.confirm( 'SET', 'Confirma que desea eliminar el registro' , function(btn){
					// for(var i=0; i<registroSeleccionado.length; i++) {
						 if(btn=='yes'){
								NS.eliminar(registroSeleccionado[0].get('noPersona'));
							}else{
								NS.cancelar();
							}
					 //}
				 }) 
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
				 NS.limpiarTodo();
				 NS.buscar();
			 }
			break;
		}
	};
	
	NS.limpiarTodo=function(){
		
		NS.storePersonas.removeAll();
		NS.storeFirmantes.removeAll();
		NS.gridFirmantes.getView().refresh();
		banderaEleccion=0;
		NS.txtNoPersona.setValue('');
		NS.txtPath.setValue('');
		NS.cmbPersonas.reset();
		
		NS.cmbPersonas.setDisabled(false);
		NS.txtNoPersona.setDisabled(false);
		
		//panel de busqueda
		/*NS.storePersonasB.removeAll();
		NS.txtNoPersonaB.setValue('');
		NS.cmbPersonasB.reset();
		NS.cmbPersonasB.load();*/
		
		NS.myMask.show();
		NS.storeFirmantes.load();
		
		//NS.storePersonas.load();
		
	}
	
	NS.cancelar=function(){
		 NS.limpiarTodo();
		 NS.buscar();
		 winEditarSF.hide();
	}
	
	NS.aceptar=function(){
		if (banderaEleccion==0){
			NS.nuevoRegistro();
		}else{
			NS.editarRegistro();
		}
	}
	
	
	NS.eliminar=function(noPersona){
		var registroSeleccionado= NS.gridDatos.getSelectionModel().getSelections();
		var vector = {};
		var matriz = new Array();
		vector.noPersona=idPersona;
		matriz[0]=vector;
		
		var jSonString = Ext.util.JSON.encode(matriz);
		MantenimientosAction.eliminarFirmantes(jSonString, function(res, e) {		
			if(res != null && res != undefined && res != '') {
				Ext.Msg.alert('SET', '' + res.msgUsuario + '!!');
			}else{
				if(res==''){
					NS.gridFirmantes.store.remove(regSelec[i]);
					NS.gridFirmantes.getView().refresh();
					Ext.Msg.alert('SET', 'Firmante eliminado!!');
				}		
			}
			 NS.limpiarTodo();
			 NS.buscar();
		});	
	}
	
	NS.nuevoRegistro=function(){
		var vector = {};
		var matriz = new Array();
		vector.noPersona=NS.txtNoPersona.getValue();
		vector.nombre=NS.cmbPersonas.getRawValue();
		vector.pathFirma=NS.txtPath.getValue();
		matriz[0]=vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		
		MantenimientosAction.insertarFirmantes(jSonString, function(res, e) {
			if(res != null && res != undefined && res != '') {
				Ext.Msg.alert('SET', '' + res.msgUsuario + '!!');
			}
			NS.limpiarTodo();
			NS.buscar();
			winEditarSF.hide();
		});
	}
	
	NS.editarRegistro=function(){
		if(NS.txtPath.getValue()!= ''){
			var vector = {};
			var matriz = new Array();
			vector.noPersona=NS.txtNoPersona.getValue();
			vector.nombre=NS.cmbPersonas.getRawValue();
			vector.pathFirma=NS.txtPath.getValue();
			matriz[0]=vector;
			var jSonString = Ext.util.JSON.encode(matriz);
			
			MantenimientosAction.insertarFirmantes(jSonString, function(res, e) {
				if(res != null && res != undefined && res != '') {
					Ext.Msg.alert('SET', '' + res.msgUsuario + '!!');
				}
				NS.limpiarTodo();
				NS.buscar();
				winEditarSF.hide();
			});
		}else{
			Ext.Msg.alert('SET', "La firma no puede estar vacia.");
			winEditarSF.show();
		}
	}
	
	/**********************Generar el excel *********************************/
	 NS.validaDatosExcel = function() {		
		var registroSeleccionado = NS.gridFirmantes.getSelectionModel().getSelections();
		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(registroSeleccionado.length == 0) {
			registroSeleccionado =  NS.storeFirmantes.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		
		var matriz = new Array();
		for(var i=0;i<registroSeleccionado.length;i++){
			var vector = {};
			vector.noPersona=registroSeleccionado[i].get('noPersona');
			vector.nombre=registroSeleccionado[i].get('nombre');
			vector.pathFirma=registroSeleccionado[i].get('pathFirma');
			matriz[i] = vector;
 		
		}
		var jSonString = Ext.util.JSON.encode(matriz);
			
		NS.exportaExcel(jSonString);
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		MantenimientosAction.exportaExcel(jsonCadena,'firmantes', function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=mantenimientoFirmantes';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
	/****************Fin de generar excel ***********************/
	
	function verificaComponentesCreados(){
		
		compt = Ext.getCmp(PF + 'lbNoPersona');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbPathFirma');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtNoPersona');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'cmbPersonas');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtPath');
		if(compt != null || compt != undefined ){ compt.destroy(); }

	}
	
	/****************
	 *  PANEL'S     *
	 ****************/
	
	NS.panelBusqueda = new Ext.form.FieldSet ({
		title: 'Busqueda por nombre',
		x: 0,
		y: 0,
		width: 510,
		height: 90,
		layout: 'absolute',
		items: [
				NS.labFirmaB,
				NS.labNombreB,
				NS.txtNoPersonaB,
				NS.cmbPersonasB,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 400,
		 		y: 15,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click:{
		 				fn: function(e){
		 					//NS.buscar();
		 				}
		 			}
		 		}
		 	},
		]
	});
	
	
	NS.panelGrid = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 120,
		//width: 1040,
		height: 300,
		layout: 'absolute',
		buttonAlign:'left',
		//autoScroll : true,
		items: [
			 	NS.gridFirmantes,		 		 	
		],
		buttons:[
			      {
				    text:'Crear Nuevo',
				    handler:function(){
				    	NS.opciones(1);
				    }
				  },
				  {
					 text:'Modificar',
					 handler:function(){
						 NS.opciones(2);
					 }
			       },
			       {
			    	  text:'Eliminar',
					  handler:function(){
						  NS.opciones(3);
					}
			       },
			       {
			    	 text:'Excel',
			    	 handler:function(){
			    		 NS.validaDatosExcel();
			    	 }
			       }
		]
	});
	
	 NS.global = new Ext.form.FieldSet ({
			title: '',
			x: 0,
			y: 5,
			width: 1200 ,		
			height: 530,
			layout: 'absolute',
			items:
			[
			 	NS.panelBusqueda,
			 	NS.panelGrid,
		    ]
	});
	 
	 NS.conceptos = new Ext.FormPanel ({
			title: 'Firmantes',
			width: 1300,
			height: 800,
			frame: true,
			padding: 10,
			autoScroll: false,
			layout: 'absolute',
			id: PF + 'conceptos',
			name: PF + 'conceptos',
			renderTo: NS.tabContId,
			items: [
			 	NS.global
			]
		});
	 
	//Ventana emergente para editar registro o nuevo registro
	 
	 NS.panelA = new Ext.form.FieldSet ({
			title: 'Datos firmante',
			x: 15,
			y: 0,
			width: 400,
			height: 100,
			layout: 'absolute',
			items: [
					NS.lbNoPersona,
					NS.lbPathFirma,
					NS.txtNoPersona,
					NS.cmbPersonas,
					NS.txtPath
			]
	});
	 
	 var winEditarSF = new Ext.Window({
			title: 'Firmantes',
			modal: true,
			shadow: true,
			closeAction: 'hide',
			width: 450,
		   	height: 180,
		   	layout: 'absolute',
		   	plain: true,
		   	resizable:false,
		   	draggable:false,
		   	closable:false,
		    bodyStyle: 'padding:0px;',
		    buttonAlign: 'center',
		    buttons:[
		             {text:'Aceptar',handler:function(){ NS.aceptar();}},
		             {text:'Cancelar',handler:function(){NS.cancelar();} },
		    ],
		   	items: [
		   	        	NS.panelA,
		   	],
		    listeners:{
		    	 show:{
		    		 fn:function(){
		    			
		    		 }
		    	 }, 
		    	  hide:{
		    		  fn:function(){
		    			  NS.buscar();
		    		  }
		    	  }
		     } 
		});
	 
	 NS.conceptos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());

	
});