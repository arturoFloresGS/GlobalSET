Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.SolicitantesFirmantes');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var banderaEleccion=0; 
	NS.combo1='';
	NS.combo2='';
	NS.Excel= false;
	
	//Componentes para ventana emergente
	verificaComponentesCreados();
	
	/************************************************************************
	 * 							COMBO NO FIRMANTES					     	*
	 ************************************************************************/
	/*NS.storePersonas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		root: '',
		directFn: MantenimientoSolicitantesFirmantesAction.llenarComboPersonas, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'}, 
			 {name: 'descripcion'}, 
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay personas disponibles');
				}
			}
		}
	});
	NS.storePersonas.load();

	NS.txtIdPersona = new Ext.form.TextField({
		id: PF + 'txtIdPersona',
		name: PF + 'txtIdPersona',
        x: 90,
		y: 5,
		width: 300,
		disabled:true,
		listeners: {
			change: {
				fn: function(caja,valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
							valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdPersona', NS.cmbPersonas.getId());
					else {
						Ext.getCmp(PF + 'txtIdPersona').setValue('');
						NS.cmbPersonas.reset();
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
		,x: 90
		,y: 30
		,width: 300
		,valueField:'id'
		,displayField:'descripcion'
		,emptyText: 'Seleccione un nombre'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,autocomplete:true
		,disabled:true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtIdPersona',NS.cmbPersonas.getId());
				}
			},
			blur:{
				fn:function(combo){
					NS.txtIdPersona.setValue('');
					BFwrk.Util.updateComboToTextField(PF+'txtIdPersona',NS.cmbPersonas.getId());
				}
			}
		}
	});*/
	/****Fin combo personas **********/
	NS.lbIdPersona = new Ext.form.Label({
		text: 'lbIdPersona',
		text: 'Identificación: ',
		x: 10,
		y : 8		
	});
	
	NS.txtIdPersona = new Ext.form.TextField({
		id: PF + 'txtIdPersona',
		name: PF + 'txtIdPersona',
		x: 90,
		y: 5,
		width: 300,
	});
	
	NS.lbNombre = new Ext.form.Label({
		id: PF + 'lbNombre',
		text: 'Nombre: ',
		x: 10,
		y : 30
		
		
	});
	
	NS.txtNombre= new Ext.form.TextField({
		id: PF + 'txtNombre',
		name: PF + 'txtNombre',
		x: 90,
		y: 30,
		width: 300,
	});
	
	NS.lbPuesto = new Ext.form.Label({
		id: PF + 'lbPuesto',
		text: 'Puesto: ',
		x: 10,
		y : 60		
	});
	
	NS.txtPuesto = new Ext.form.TextField({
		id: PF + 'txtPuesto',
		name: PF + 'txtPuesto',
		x: 90,
		y: 60 ,
		width: 300,
	});
	
	NS.lbTipoPersona = new Ext.form.Label({
		id: PF + 'lbTipoPersona',
		text: 'Tipo Persona: ',
		x: 10,
		y : 90		
	});
	
	//Datos del combo
	NS.datosCmbTipoPersona = [['S', 'SOLICITANTE'],['L', 'FIRMANTE 1'],	['F', 'FIRMANTE 2']];	
	
	//store para el combo
	NS.storeCmbTipoPersona = new Ext.data.SimpleStore({
   		idProperty: 'idPersona',
   		fields: [
   					{name: 'idPersona'},
   					{name: 'nombrePersona'}
   				]
   	});
   	NS.storeCmbTipoPersona.loadData(NS.datosCmbTipoPersona);
	//x 90 y 60
	NS.cmbTipoPersona = new Ext.form.ComboBox ({
		store: NS.storeCmbTipoPersona,
		id: PF + 'cmbTipoPersona',
		name: PF + 'cmbTipoPersona',
		x: 90,
		y: 90,
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idPersona',
		displayField: 'nombrePersona',
		autocomplete: true,
		emptyText: 'Seleccione Persona',
		triggerAction: 'all',
		value: '',
		editable:false,
		hideTrigger :false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					NS.combo1=combo.getValue();
				}
			}
		}
	});
	
	
	//fin de componentes emergentes
	
	
	NS.lblTipoPersona = new Ext.form.Label({
		text: 'Tipo Persona',
		x: 0, 
		y: 0,
	});
	
   	//Combo de busqueda
	NS.datosCmbTipoPersonaB = [['', 'TODOS'],['S', 'SOLICITANTE'],['L', 'FIRMANTE 1'],	['F', 'FIRMANTE 2']];	
	
	//store para el combo
	NS.storeCmbTipoPersonaB = new Ext.data.SimpleStore({
   		idProperty: 'idPersona',
   		fields: [
   					{name: 'idPersona'},
   					{name: 'nombrePersona'}
   				]
   	});
   	NS.storeCmbTipoPersonaB.loadData(NS.datosCmbTipoPersonaB);
   	
	NS.cmbTipoPersonaBuscar= new Ext.form.ComboBox ({
		store: NS.storeCmbTipoPersonaB,
		id: PF + 'cmbTipoPersonaBuscar ',
		name: PF + 'cmbTipoPersonaBuscar ',
		x: 90,
		y: 0,
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idPersona',
		displayField: 'nombrePersona',
		autocomplete: true,
		emptyText: 'Seleccione Persona',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					NS.combo2=combo.getValue();
					NS.buscar();
				}
			}
		}
	});
	//Fin del combo tipo persona
	
	//GRID DE CONSULTA
	//Columna de seleccion en el grid
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {tipoPersona:''},
		paramOrder: ['tipoPersona'],
		directFn: MantenimientoSolicitantesFirmantesAction.llenaGridSolicitantesFirmantes,
		fields: [
		    {name: 'idPersona'},
		 	{name: 'nombre'},
		 	{name: 'puesto'},
		 	{name: 'tipoPersona'},
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridSolicitantesFirmantes, msg: "Buscando..."});
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No existen solicitantes o firmantes');}
			}
		}	
	})
	
	NS.storeLlenaGrid.load();
	
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: true
	});

	//Columnas del grid
	NS.columnasGrid = new Ext.grid.ColumnModel ([
	    NS.columnaSeleccion,
	    {header: 'Identificación', width: 150, dataIndex: 'idPersona', sortable: true},
	  	{header: 'Nombre', width: 250, dataIndex: 'nombre', sortable: true},
	  	{header: 'Puesto', width: 150, dataIndex: 'puesto', sortable: true},
	  	{header: 'Tipo', width: 80, dataIndex: 'tipoPersona', sortable: true},
	]); 
	
	//Se agregan componentes (columnas) a grid
	NS.gridDatos = new Ext.grid.GridPanel ({
		store: NS.storeLlenaGrid,
		id: PF + 'gridDatos',
		name: PF + 'gridDatos',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 805,
		height: 300,
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
		NS.storeLlenaGrid.baseParams.tipoPersona = NS.combo2;
		NS.storeLlenaGrid.load();
	};
	
	NS.opciones=function(op){
		switch (op) {
		case 1:
			// NS.limpiarTodo();
			 winEditarSF.show();
			 winEditarSF.setTitle("Nuevo registro");
			 banderaEleccion=0;
			 NS.txtIdPersona.enable();
			break;

		case 2:
			 var registroSeleccionado= NS.gridDatos.getSelectionModel().getSelections();
			 if (registroSeleccionado.length > 0){
				 winEditarSF.show();
				 winEditarSF.setTitle("Editar registro");
				 NS.txtIdPersona.disable();
				// NS.cmbPersonas.disable();
				 banderaEleccion=1;
				 NS.txtIdPersona.setValue(registroSeleccionado[0].get('idPersona'));
				 NS.txtNombre.setValue(registroSeleccionado[0].get('nombre'));
				 NS.txtPuesto.setValue(registroSeleccionado[0].get('puesto'));
				 var tipoPer=registroSeleccionado[0].get('tipoPersona');
				 NS.cmbTipoPersona.setValue(tipoPer);
				 if(tipoPer=='SOLICITANTE'){
					 NS.combo1='S';
				 }
				 
				 if(tipoPer=='FIRMANTE 1'){
					 NS.combo1='L'
				 }
				 
				 if(tipoPer=='FIRMANTE 2'){
					 NS.combo1='F'
				 }
				 //falta el combo
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
							NS.eliminar(registroSeleccionado[0].get('idPersona'));
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
		NS.txtIdPersona.setValue("");
		NS.txtPuesto.setValue("");
		NS.cmbTipoPersonaBuscar.reset();
		NS.storeLlenaGrid.removeAll();
		NS.gridDatos.getView().refresh();
		banderaEleccion=0;
		NS.combo1='';
		NS.combo2='';
		NS.txtNombre.setValue("");
		//NS.storeCmbTipoPersona.removeAll();
		NS.cmbTipoPersona.reset();
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
		vector.idPersona=idPersona;
		matriz[0]=vector;
		
		var jSonString = Ext.util.JSON.encode(matriz);
		MantenimientoSolicitantesFirmantesAction.deleteMantenimientoSolicitantesFirmantes(jSonString, function(resultado, e) {
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
		vector.idPersona=NS.txtIdPersona.getValue();
		vector.nombre=NS.txtNombre.getValue();
		vector.puesto=NS.txtPuesto.getValue();
		vector.tipo=NS.combo1;
		vector.usuarioAlta=NS.idUsuario;
		vector.fecAlta=NS.fecHoy;
		matriz[0]=vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		MantenimientoSolicitantesFirmantesAction.insertaMantenimientoSolicitantesFirmantes(jSonString, function(resultado, e) {
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
		vector.idPersona=NS.txtIdPersona.getValue();
		vector.nombre=NS.txtNombre.getValue();
		vector.puesto=NS.txtPuesto.getValue();
		vector.tipo=NS.combo1;
		matriz[0]=vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		MantenimientoSolicitantesFirmantesAction.updateMantenimientoSolicitantesFirmantes(jSonString, function(resultado, e) {
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
		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(registroSeleccionado.length == 0) {
			registroSeleccionado = NS.storeLlenaGrid.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		
		if(NS.Excel) {
			
			var matriz = new Array();
			
			for(var i=0;i<registroSeleccionado.length;i++){
				var vector = {};
				vector.idPersona=registroSeleccionado[i].get('idPersona');
				vector.nombre=registroSeleccionado[i].get('nombre');
				vector.puesto=registroSeleccionado[i].get('puesto');
				vector.tipoPersona=registroSeleccionado[i].get('tipoPersona');
				matriz[i] = vector;
	 		
			}
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		NS.exportaExcel(jSonString);
		NS.excel = false;
	
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		MantenimientoSolicitantesFirmantesAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=solicitantesFirmantes';
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
		compt = Ext.getCmp(PF + 'lbIdPersona');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtIdPersona');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbNombre');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		//compt = Ext.getCmp(PF + 'cmbPersonas');
		//if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'NS.txtNombre');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbPuesto');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtPuesto');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'lbTipo');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'cmbTipoPersona');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		/**************************/
	}
	
	//panel
	NS.panelBusqueda = new Ext.form.FieldSet ({
		title: '',
		x: 200,
		y: 0,
		width: 500,
		height: 50,
		layout: 'absolute',
		items: [
		 	NS.lblTipoPersona,
		 	NS.cmbTipoPersonaBuscar,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 300,
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
		y: 60,
		width: 700,
		height: 400,
		layout: 'absolute',
		buttonAlign:'center',
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
			    		 NS.Excel = true;
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
			width: 725,		
			height: 530,
			layout: 'absolute',
			items:
			[
			 	NS.panelBusqueda,
			 	NS.panelGrid,
		    ]
		});
	 NS.conceptos = new Ext.FormPanel ({
			title: 'Solicitantes/Firmantes',
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
		var winEditarSF = new Ext.Window({
			title: 'Solicitantes Firmantes',
			modal: true,
			shadow: true,
			closeAction: 'hide',
			width: 420,
		   	height: 180,
		   	layout: 'absolute',
		   	plain: true,
		   	resizable:false,
		   	draggable:false,
		   	closable:false,
		    bodyStyle: 'padding:10px;',
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
						NS.lbIdPersona,
						NS.txtIdPersona,
						NS.lbNombre,
						//NS.cmbPersonas,
						NS.txtNombre,
						NS.lbPuesto,
						NS.txtPuesto,
						NS.lbTipoPersona,
						NS.cmbTipoPersona,
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
 