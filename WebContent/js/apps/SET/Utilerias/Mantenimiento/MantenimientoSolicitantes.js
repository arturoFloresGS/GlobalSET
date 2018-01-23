Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.MantenimientoSolicitantes');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var banderaEleccion=0; 
	NS.nombreBusqueda='';
	NS.idSolicitante='';
	
	NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	
	/*************************************************
	 *		COMPONENTES PARA VENTANA EMERGENTE		 *
	 *************************************************/
	
	verificaComponentesCreados();
	
	/***********CONTACTO 1**********/
						  			
	NS.lbIdPersonaC1 = new Ext.form.Label({
		text: 'lbIdPersonaC1',
		text: 'Identificación: ',
		x: 10,
		y : 8		
	});
	
	NS.lbNombreC1 = new Ext.form.Label({
		id: PF + 'lbNombreC1',
		text: 'Nombre: ',
		x: 10,
		y : 30
	});
	
	NS.lbPuestoC1 = new Ext.form.Label({
		id: PF + 'lbPuestoC1',
		text: 'Puesto: ',
		x: 10,
		y : 60		
	});
	
	NS.lbTelefonoC1 = new Ext.form.Label({
		id: PF + 'lbTelefonoC1',
		text: 'Telefono: ',
		x: 10,
		y : 90		
	});
	
	NS.lbCorreoC1 = new Ext.form.Label({
		id: PF + 'lbCorreoC1',
		text: 'Correo: ',
		x: 10,
		y : 120		
	});
	
	NS.txtIdPersonaC1 = new Ext.form.TextField({
		id: PF + 'txtIdPersonaC1',
		name: PF + 'txtIdPersonaC1',
		x: 90,
		y: 5,
		width: 300,
		//maskRe: /[A-Z ÑÁÉÍÓÚ]/,
	});
	
	NS.txtNombreC1= new Ext.form.TextField({
		id: PF + 'txtNombreC1',
		name: PF + 'txtNombreC1',
		x: 90,
		y: 30,
		width: 300,
		maskRe: /[A-Za-z ÑÁÉÍÓÚñáéíóú]/,
	});
	
	NS.txtPuestoC1= new Ext.form.TextField({
		id: PF + 'txtPuestoC1',
		name: PF + 'txtPuestoC1',
		x: 90,
		y: 60,
		width: 300,
		maskRe: /[A-Za-z ÑÁÉÍÓÚñáéíóú]/,
	});
		
	NS.txtTelefonoC1 = new Ext.form.NumberField({
		id: PF + 'txtTelefonoC1',
		name: PF + 'txtTelefonoC1',
		x: 90,
		y: 90 ,
		width: 300,
	});
	
	NS.txtCorreoC1 = new Ext.form.TextField({
		id: PF + 'txtCorreoC1',
		name: PF + 'txtCorreoC1',
		x: 90,
		y: 120 ,
		width: 300,
		maskRe: /[A-Za-z0-9 @.-_]/,
	});
	
	/***********Contacto 2*************/
	
	NS.lbIdPersonaC2 = new Ext.form.Label({
		text: 'lbIdPersonaC2',
		text: 'Identificación: ',
		x: 10,
		y : 8		
	});
	
	NS.lbNombreC2 = new Ext.form.Label({
		id: PF + 'lbNombreC2',
		text: 'Nombre: ',
		x: 10,
		y : 30
	});

	NS.lbPuestoC2 = new Ext.form.Label({
		id: PF + 'lbPuestoC2',
		text: 'Puesto: ',
		x: 10,
		y : 60		
	});
	
	NS.lbTelefonoC2 = new Ext.form.Label({
		id: PF + 'lbTelefonoC2',
		text: 'Telefono: ',
		x: 10,
		y : 90		
	});
	
	NS.lbCorreoC2 = new Ext.form.Label({
		id: PF + 'lbCorreoC2',
		text: 'Correo: ',
		x: 10,
		y : 120	
	});

	NS.txtIdPersonaC2 = new Ext.form.TextField({
		id: PF + 'txtIdPersonaC2',
		name: PF + 'txtIdPersonaC2',
		x: 90,
		y: 5,
		width: 300,
	});

	NS.txtNombreC2= new Ext.form.TextField({
		id: PF + 'txtNombreC2',
		name: PF + 'txtNombreC2',
		x: 90,
		y: 30,
		width: 300,
		maskRe: /[A-Za-z ÑÁÉÍÓÚñáéíóú]/,
	});
	
	NS.txtPuestoC2= new Ext.form.TextField({
		id: PF + 'txtPuestoC2',
		name: PF + 'txtPuestoC2',
		x: 90,
		y: 60,
		width: 300,
		maskRe: /[A-Za-z ÑÁÉÍÓÚñáéíóú]/,
	});
		
	NS.txtTelefonoC2 = new Ext.form.NumberField({
		id: PF + 'txtTelefonoC2',
		name: PF + 'txtTelefonoC2',
		x: 90,
		y: 90 ,
		width: 300,
	});
	
	NS.txtCorreoC2 = new Ext.form.TextField({
		id: PF + 'txtCorreoC2',
		name: PF + 'txtCorreoC2',
		x: 90,
		y: 120 ,
		width: 300,
		maskRe: /[A-Za-z0-9 @.-_]/,
	});
	
	/***********Observacion************/
	NS.lbObservacion = new Ext.form.Label({
		id: PF + 'lbObservacion',
		text: 'Observación: ',
		x: 0,
		y : 0		
	});
	
	NS.txtObservacion = new Ext.form.TextArea({
		id: PF + 'txtObservacion',
		name: PF + 'txtObservacion',
		x: 90,
		y: 0,
		width: 300,
		height: 70,
	});
	
	
	/*********************************************
	 * 			Fin de ventana emergente         *
	 *********************************************/
	
	NS.lbNombre = new Ext.form.Label({
		id: PF + 'lbNombre',
		text: 'Nombre: ',
		x: 0,
		y : 0		
	});
	
	NS.txtNombre = new Ext.form.TextField({
		id: PF + 'txtNombre',
		name: PF + 'txtNombre',
		x: 60,
		y: 0,
		width: 200,
		maskRe: /[A-Za-z ÑÁÉÍÓÚñáéíóú]/,
	});
	
	//GRID DE CONSULTA
	//Columna de seleccion en el grid
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {nombre:''},
		paramOrder: ['nombre'],
		directFn: MantenimientoSolicitantesAction.llenaGridSolicitantes,
		fields: [
		    {name: 'identificacion1'},
		 	{name: 'nombre1'},
		 	{name: 'puesto1'},
		 	{name: 'telefono1'},
		 	{name: 'correo1'},
		 	{name: 'identificacion2'},
		 	{name: 'nombre2'},
		 	{name: 'puesto2'},
		 	{name: 'telefono2'},
		 	{name: 'correo2'},
		 	{name: 'idSolicitante'},
		 	{name: 'fecha'},
		 	{name: 'observacion'},
		],
		listeners: {
			load: function(s, records) {
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No existen solicitantes o firmantes');}
				NS.myMask.hide();
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}	
	})
	NS.myMask.show();
	NS.storeLlenaGrid.load();
	
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: true
	});

	//Columnas del grid
	NS.columnasGrid = new Ext.grid.ColumnModel ([
	    NS.columnaSeleccion,
	  	{header: 'Fecha', width: 100, dataIndex: 'fecha', sortable: true},
	  	{header: 'Observacion', width: 200, dataIndex: 'observacion', sortable: true},
	  	{header: 'Identificacion', width: 100, dataIndex: 'identificacion1', sortable: true},
	  	{header: 'Nombre', width: 100, dataIndex: 'nombre1', sortable: true},
	  	{header: 'Puesto', width: 100, dataIndex: 'puesto1', sortable: true},
	  	{header: 'Telefono', width: 100, dataIndex: 'telefono1', sortable: true},
	  	{header: 'Correo', width: 100, dataIndex: 'correo1', sortable: true},
	  	{header: 'Identificacion 2', width: 100, dataIndex: 'identificacion2', sortable: true},
	  	{header: 'Nombre 2', width: 100, dataIndex: 'nombre2', sortable: true},
	  	{header: 'Puesto 2', width: 100, dataIndex: 'puesto2', sortable: true},
	  	{header: 'Telefono 2', width: 100, dataIndex: 'telefono2', sortable: true},
	  	{header: 'Correo 2', width: 100, dataIndex: 'correo2', sortable: true},
	]); 
	
	//Se agregan componentes (columnas) a grid
	NS.gridDatos = new Ext.grid.GridPanel ({
		store: NS.storeLlenaGrid,
		id: PF + 'gridDatos',
		name: PF + 'gridDatos',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 1025,
		height: 340,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					
				}
			}
		}
	});
	
	//fin de  grid
	
   	
   	// Funciones y store's
	
	NS.buscar = function(){
		NS.storeLlenaGrid.baseParams.nombre = NS.txtNombre.getValue();
		NS.myMask.show();
		NS.storeLlenaGrid.load();
	};
	
	NS.opciones=function(op){
		switch (op) {
		case 1:
			// NS.limpiarTodo();
			 winEditarSF.show();
			 winEditarSF.setTitle("Nuevo registro");
			 banderaEleccion=0;
			break;

		case 2:
			 var registroSeleccionado= NS.gridDatos.getSelectionModel().getSelections();
			 if (registroSeleccionado.length > 0){
				 winEditarSF.show();
				 winEditarSF.setTitle("Editar registro");
			     banderaEleccion=1;
				 NS.txtIdPersonaC1.setValue(registroSeleccionado[0].get('identificacion1'));
					NS.txtNombreC1.setValue(registroSeleccionado[0].get('nombre1'));
					NS.txtTelefonoC1.setValue(registroSeleccionado[0].get('telefono1'));
					NS.txtCorreoC1.setValue(registroSeleccionado[0].get('correo1'));
					NS.txtPuestoC1.setValue(registroSeleccionado[0].get('puesto1'));
					NS.txtIdPersonaC2.setValue(registroSeleccionado[0].get('identificacion2'));
					NS.txtNombreC2.setValue(registroSeleccionado[0].get('nombre2'));
					NS.txtTelefonoC2.setValue(registroSeleccionado[0].get('telefono2'));
					NS.txtCorreoC2.setValue(registroSeleccionado[0].get('correo2'));
					NS.txtPuestoC2.setValue(registroSeleccionado[0].get('puesto2'));
					NS.txtObservacion.setValue(registroSeleccionado[0].get('observacion'));
					NS.idSolicitante=registroSeleccionado[0].get('idSolicitante');
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
				 NS.limpiarTodo();
				 NS.buscar();
			 }
			break;
		case 3:
			var registroSeleccionado= NS.gridDatos.getSelectionModel().getSelections();
			 if (registroSeleccionado.length > 0){
				 Ext.Msg.confirm( 'SET', 'Confirma que desea eliminar el registro' , function(btn){
						if(btn=='yes'){
							NS.eliminar(registroSeleccionado[0].get('idSolicitante'));
						}else{
							NS.cancelar();
						}
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
		NS.storeLlenaGrid.removeAll();
		NS.gridDatos.getView().refresh();
		banderaEleccion=0;
		NS.txtIdPersonaC1.setValue('');
		NS.txtNombreC1.setValue('');
		NS.txtTelefonoC1.setValue('');
		NS.txtCorreoC1.setValue('');
		NS.txtPuestoC1.setValue('');
		NS.txtIdPersonaC2.setValue('');
		NS.txtNombreC2.setValue('');
		NS.txtTelefonoC2.setValue('');
		NS.txtCorreoC2.setValue('');
		NS.txtPuestoC2.setValue('');
		NS.txtObservacion.setValue('');
		NS.idSolicitante='';
		NS.myMask.show();
		NS.storeLlenaGrid.load();
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
	
	NS.eliminar=function(idPersona){
		var registroSeleccionado= NS.gridDatos.getSelectionModel().getSelections();
		var vector = {};
		var matriz = new Array();
		vector.idSolicitante=idPersona;
		matriz[0]=vector;
		
		var jSonString = Ext.util.JSON.encode(matriz);
		MantenimientoSolicitantesAction.deleteSolicitante(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined) {
				if(resultado=='Exito'){
					NS.limpiarTodo();
					NS.buscar();
				}
				Ext.Msg.alert('SET', resultado);
			}
		});	
	}
	
	NS.nuevoRegistro=function(){
		var vector = {};
		var matriz = new Array();
		vector.idPersona1=NS.txtIdPersonaC1.getValue();
		vector.nombre1=NS.txtNombreC1.getValue();
		vector.puesto1=NS.txtPuestoC1.getValue();
		vector.telefono1=NS.txtTelefonoC1.getValue();
	    vector.correo1=NS.txtCorreoC1.getValue();
	    vector.idPersona2=NS.txtIdPersonaC2.getValue();
		vector.nombre2=NS.txtNombreC2.getValue();
		vector.puesto2=NS.txtPuestoC2.getValue();
		vector.telefono2=NS.txtTelefonoC2.getValue();
	    vector.correo2=NS.txtCorreoC2.getValue();
		vector.fecAlta=NS.fecHoy;
		vector.observacion=NS.txtObservacion.getValue();
		matriz[0]=vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		MantenimientoSolicitantesAction.insertSolicitante(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined ) {
				if(resultado=='Exito'){
					NS.limpiarTodo();
					NS.buscar();
					winEditarSF.hide();
				}else{
					winEditarSF.show();
				}
				Ext.Msg.alert('SET', resultado);
			}
				
		});
	}
	
	NS.editarRegistro=function(){
		var vector = {};
		var matriz = new Array();
		vector.idPersona1=NS.txtIdPersonaC1.getValue();
		vector.nombre1=NS.txtNombreC1.getValue();
		vector.puesto1=NS.txtPuestoC1.getValue();
		vector.telefono1=NS.txtTelefonoC1.getValue();
	    vector.correo1=NS.txtCorreoC1.getValue();
	    vector.idPersona2=NS.txtIdPersonaC2.getValue();
		vector.nombre2=NS.txtNombreC2.getValue();
		vector.puesto2=NS.txtPuestoC2.getValue();
		vector.telefono2=NS.txtTelefonoC2.getValue();
	    vector.correo2=NS.txtCorreoC2.getValue();
		vector.fecAlta=NS.fecHoy;
		vector.observacion=NS.txtObservacion.getValue();
		vector.idSol=NS.idSolicitante;
		matriz[0]=vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		MantenimientoSolicitantesAction.updateSolicitante(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined ) {
				if(resultado=='Exito'){
					NS.limpiarTodo();
					NS.buscar();
					winEditarSF.hide();
				}else{
					winEditarSF.show();
				}
				Ext.Msg.alert('SET', resultado);
			}
		});
	}
	
	/**********************Generar el excel *********************************/
	 NS.validaDatosExcel = function() {		
		var registroSeleccionado = NS.gridDatos.getSelectionModel().getSelections();
		if(registroSeleccionado.length == 0) {
			registroSeleccionado = NS.storeLlenaGrid.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		var matriz = new Array();
		for(var i=0;i<registroSeleccionado.length;i++){
			var vector = {};
			
			vector.idPersona1=registroSeleccionado[i].get('identificacion1');
			vector.nombre1=registroSeleccionado[i].get('nombre1');
			vector.puesto1=registroSeleccionado[i].get('puesto1');
			vector.telefono1=registroSeleccionado[i].get('telefono1');
		    vector.correo1=registroSeleccionado[i].get('correo1');
		    vector.idPersona2=registroSeleccionado[i].get('identificacion2');
			vector.nombre2=registroSeleccionado[i].get('nombre2');
			vector.puesto2=registroSeleccionado[i].get('puesto2');
			vector.telefono2=registroSeleccionado[i].get('telefono2');
		    vector.correo2=registroSeleccionado[i].get('correo2');
			vector.fecAlta=registroSeleccionado[i].get('fecha');
			vector.observacion=registroSeleccionado[i].get('observacion');
			vector.idSol=registroSeleccionado[i].get('idSolicitante');
			
			matriz[i] = vector;
	 	}
		var jSonString = Ext.util.JSON.encode(matriz);
		NS.exportaExcel(jSonString);
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		MantenimientoSolicitantesAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=solicitantes';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
		/****************Fin de generar excel ***********************/
	
	//ventana emergente
	function verificaComponentesCreados(){
		var compt; 	
		/*** Ventana nuevo/editar***/
		
		//Panel contacto 1
		
		compt = Ext.getCmp(PF + 'lbIdPersonaC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbNombreC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbTelefonoC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbCorreoC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtIdPersonaC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtNombreC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtTelefonoC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtCorreoC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtPuestoC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'lbPuestoC1');
		if(compt != null || compt != undefined ){ compt.destroy(); }

		
		//Panel contacto 2
		
		compt = Ext.getCmp(PF + 'lbIdPersonaC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbNombreC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbTelefonoC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbCorreoC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtIdPersonaC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtNombreC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtTelefonoC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtCorreoC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtPuestoC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'lbPuestoC2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		//Panel observacion
		
		compt = Ext.getCmp(PF + 'lbObservacion');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtObservacion');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		/**************************/
	}
	
	//panel
	NS.panelBusqueda = new Ext.form.FieldSet ({
		title: 'Busqueda por nombre',
		x: 600,
		y: 0,
		width: 400,
		height: 60,
		layout: 'absolute',
		items: [
				NS.lbNombre,
				NS.txtNombre,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 280,
		 		y: 0,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click:{
		 				fn: function(e){
		 					NS.buscar();
		 				}
		 			}
		 		}
		 	},
		]
	});
	
	
	NS.panelGrid = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 70,
		width: 1040,
		height: 400,
		layout: 'absolute',
		buttonAlign:'center',
		//autoScroll : true,
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
				 ],
		items: [
		 	NS.gridDatos,		 		 	
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
			title: 'Solicitantes',
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
	 
	 NS.panelContacto1 = new Ext.form.FieldSet ({
			title: 'Datos contacto 1',
			x: 15,
			y: 0,
			width: 400,
			height: 180,
			layout: 'absolute',
			items: [
					NS.lbIdPersonaC1,
					NS.lbNombreC1,
					NS.lbPuestoC1,
					NS.lbTelefonoC1,
					NS.lbCorreoC1,
					NS.txtIdPersonaC1,
					NS.txtNombreC1,
					NS.txtPuestoC1, 
					NS.txtTelefonoC1,
					NS.txtCorreoC1
			]
		});
	 
	 
	 NS.panelContacto2= new Ext.form.FieldSet ({
			title: 'Datos contacto 2',
			x: 15,
			y: 185,
			width: 400,
			height: 180,
			layout: 'absolute',
			//collapsible:true,
			//collapsed:true,
			items: [
				NS.lbIdPersonaC2,
				NS.lbNombreC2,
				NS.lbPuestoC2,
				NS.lbTelefonoC2,
				NS.lbCorreoC2,
				NS.txtIdPersonaC2,
				NS.txtNombreC2,
				NS.txtPuestoC2,
				NS.txtTelefonoC2,
				NS.txtCorreoC2
			],
		 /*listeners:{
			 collapse:{
	    		 fn:function(p){
	    			 winEditarSF.setHeight(420);
	    			 NS.panelObservacion.setPosition(0,300);
	    			 winEditarSF.setPosition(475,100);
	    		 }
	    	 },
	         expand:{
	    		 fn:function(p){
	    			 winEditarSF.setHeight(570);
	    			 NS.panelObservacion.setPosition(0,440);
	    			 winEditarSF.setPosition(475,30);
		    	 }
		    },
	     } */
	 
		});
	 
	 NS.panelObservacion= new Ext.form.FieldSet ({
			title: 'Observacion',
			x: 15,
			y: 370,
			width: 400,
			height: 80,
			layout: 'absolute',
			items: [
				NS.lbObservacion,
				NS.txtObservacion
			]
		});
	 
		var winEditarSF = new Ext.Window({
			title: 'Solicitantes',
			modal: true,
			shadow: true,
			closeAction: 'hide',
			width: 450,
		   	height: 520,
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
		   	        	NS.panelContacto1,
		   	        	NS.panelContacto2,
		   	        	NS.panelObservacion
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
 