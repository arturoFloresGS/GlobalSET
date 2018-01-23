Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.Leyendas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var banderaEleccion=0;
	var motivoEliminar="";
	NS.idLeyenda="";
	NS.enter=0;
	NS.leyenda='';
	NS.mensajeError='';
	
	/******** Componentes **********/
	NS.myMask= new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	
	NS.lbLeyenda = new Ext.form.Label({
		text: 'Leyenda: ',
		x: 0,
		y : 5		
	});
	
	NS.txtLeyenda = new Ext.form.TextField({
		id: PF + 'txtLeyenda',
		name: PF + 'txtLeyenda',
		//x: 0,
		//y: 25,
		width:880,
		maxLength: 124,
		enableKeyEvents:true,
		listeners:{
		    keydown:{
				fn:function(caja, e) {
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 124));
					}
				}
		    },
		    keyup:{
				fn:function(caja, e) {	 				
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 124));
					}
				}
		    }
		}
	});

	/*******Funciones************/
	
	NS.buscar = function(){
		NS.storeLlenaGridLeyendas.baseParams.descLeyenda='';
		NS.storeLlenaGridLeyendas.load();
	};
	
	NS.opciones=function(op){
		switch (op) {
		case 1:
			NS.limpiarTodo();
			NS.panelMantenimiento.setTitle('Registrar Leyenda');
			NS.panelMantenimiento.show();
			banderaEleccion=0;
			NS.txtLeyenda.focus();
			break;

		case 2:
			var registroSeleccionado= NS.gridLeyendas.getSelectionModel().getSelections();
			NS.limpiarTodo();
			 if (registroSeleccionado.length > 0){
				 NS.panelMantenimiento.setTitle('Editar Leyenda');
					
				 NS.panelMantenimiento.show();;
				 banderaEleccion=1;
				// var leyendaAux = registroSeleccionado[0].get('descLeyenda').replace(new RegExp('\n'), '<br>');
				// alert(leyendaAux);
				 NS.txtLeyenda.setValue(registroSeleccionado[0].get('descLeyenda'));
				 NS.idLeyenda=registroSeleccionado[0].get('idLeyenda');
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
			 }
			break;
		case 3:
			var registroSeleccionado= NS.gridLeyendas.getSelectionModel().getSelections();
			 if (registroSeleccionado.length > 0){
				 Ext.Msg.confirm( 'SET', 'Confirma que desea eliminar el registro' , function(btn){
					if(btn=='yes'){
						NS.eliminar(registroSeleccionado[0].get('idLeyenda'));
					}else{
						NS.cancelar();
					}
				 }) 
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
			 }
			break;
	  }
	};
	
	NS.limpiarTodo=function(){
		NS.txtLeyenda.setValue("");
		NS.idLeyenda="";
	}
	
	NS.cancelar=function(){
		 NS.limpiarTodo();
		 NS.panelMantenimiento.hide();
		 NS.buscar();
	}
	
	NS.aceptar=function(){
		if(NS.mensajeError==''){
			if (banderaEleccion==0){
				NS.nuevoRegistro();
				NS.panelMantenimiento.hide();
			}else{
				NS.editarRegistro();
				NS.panelMantenimiento.hide();
			}
		}else{
			Ext.Msg.alert('SET', "Error:"+NS.mensajeError);
		}
		
	}
	
	NS.nuevoRegistro=function(){
		var vectorLeyenda = {};
		var matrizLeyenda = new Array();
		vectorLeyenda.descLeyenda=NS.txtLeyenda.getValue();
		vectorLeyenda.fecAlta=NS.fecHoy;
		matrizLeyenda[0]=vectorLeyenda;
		var jSonString = Ext.util.JSON.encode(matrizLeyenda);
	
		MantenimientoLeyendasAction.insertaMantenimientoLeyendas(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined) {
				if(resultado=='Exito'){
					Ext.Msg.alert('SET', resultado);
					NS.limpiarTodo();
					NS.buscar();
					NS.panelMantenimiento.hide();
				}else{
					Ext.Msg.alert('SET', "Error al guardar");
					NS.panelMantenimiento.show();
				}
			}else{
				Ext.Msg.alert('SET', "Error al guardar");
				NS.panelMantenimiento.show();
			}
				
		});
	}
	
	NS.editarRegistro=function(){
		var vectorLeyenda = {};
		var matrizLeyenda = new Array();
		vectorLeyenda.idLeyenda=NS.idLeyenda;
		vectorLeyenda.descLeyenda=NS.txtLeyenda.getValue();
		vectorLeyenda.fecAlta=NS.fecHoy;
		matrizLeyenda[0]=vectorLeyenda;
		var jSonString = Ext.util.JSON.encode(matrizLeyenda);
		MantenimientoLeyendasAction.updateMantenimientoLeyendas(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined && resultado!='3' && resultado!='4') {
				if (resultado != '' && resultado != null && resultado != undefined) {
					Ext.Msg.alert('SET', resultado);
					NS.limpiarTodo();
					NS.buscar();
					NS.panelMantenimiento.hide();
				}else{
					Ext.Msg.alert('SET', "Error al guardar");
					NS.panelMantenimiento.show();
				}
			}else{
				Ext.Msg.alert('SET', "Error al guardar");
				NS.panelMantenimiento.show();
			}
		});
	}
	
	NS.eliminar=function(idLeyenda){
		var registroSeleccionado= NS.gridLeyendas.getSelectionModel().getSelections();
		var vectorLeyenda = {};
		var matrizLeyenda = new Array();
		vectorLeyenda.idLeyenda=idLeyenda;
		matrizLeyenda[0]=vectorLeyenda;
		var jSonString = Ext.util.JSON.encode(matrizLeyenda);
		MantenimientoLeyendasAction.deleteMantenimientoLeyendas(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined) {
				Ext.Msg.alert('SET', resultado);
				NS.limpiarTodo();
				NS.buscar();
			}
		});	
	}
	
	/********************** Generar el excel *********************************/
	
	 NS.validaDatosExcel = function() {		
		var registroSeleccionado = NS.gridLeyendas.getSelectionModel().getSelections();
		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(registroSeleccionado.length == 0) {
			registroSeleccionado = NS.storeLlenaGridLeyendas.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		var matriz = new Array();	
		for(var i=0;i<registroSeleccionado.length;i++){
			var vector = {};
			vector.idLeyenda=registroSeleccionado[i].get('idLeyenda');
			vector.descLeyenda=registroSeleccionado[i].get('descLeyenda');
			vector.fecAlta=registroSeleccionado[i].get('fecAlta');
			vector.tipoPersona=registroSeleccionado[i].get('tipoPersona');
			matriz[i] = vector;
		}
		var jSonString = Ext.util.JSON.encode(matriz);
		NS.exportaExcel(jSonString);
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		MantenimientoLeyendasAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=excelLeyendas';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
	
	/****************Fin de generar excel ***********************/
	
	/**** Grid de leyendas *********/

	NS.storeLlenaGridLeyendas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {descLeyenda:''},
		paramOrder: ['descLeyenda'],
		directFn: MantenimientoLeyendasAction.llenaGridLeyendas,
		fields: [
		    {name: 'idLeyenda'},
		 	{name: 'descLeyenda'},
		 	{name: 'fecAlta'},
		],
		listeners: {
			load: function(s, records) {
				NS.panelMantenimiento.hide();
				NS.myMask.hide();
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No existen leyendas');}
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}	
	})
	NS.myMask.show();
	NS.storeLlenaGridLeyendas.load();
	
	NS.columnaSeleccionLeyendas= new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	NS.columnasLeyendas = new Ext.grid.ColumnModel
	([
	  NS.columnaSeleccionLeyendas,
	  {header: 'Id', width: 80, dataIndex: 'idLeyenda', sortable: true},
	  {header: 'Descripcion', width: 750, dataIndex: 'descLeyenda', sortable: true},
	  {header: 'Fecha', width: 100, dataIndex: 'fecAlta', sortable: true}
	]);
			
	NS.gridLeyendas = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridLeyendas,
		id: PF + 'gridLeyendas',
		name: PF + 'gridLeyendas',
		cm: NS.columnasLeyendas,
		sm: NS.columnaSeleccionLeyendas,
		x: 0,
		y: 0,
		width: 970,
		height: 280,
		stripRows: true,
		columnLines: true,
		autoScroll: true,
		listeners: {
			click:{
				fn:function(grid){
					 NS.panelMantenimiento.hide();
					 NS.limpiarTodo();
				}
			}
		}
	});
	
	/******** Panels ******/
	
	NS.panelComponentes = new Ext.form.FieldSet({
		title: 'Leyendas',
		x: 0,
		y: 0,
		width: 1000,
		height: 360,
		layout: 'absolute',
		autoScroll: false,
		buttonAlign: 'center',
	    buttons:[
	      {text:'Excel',handler:function(){NS.validaDatosExcel();}},
	      {text:'Crear nuevo',handler:function(){NS.opciones(1);}},
	      {text:'Modificar', handler:function(){NS.opciones(2);}},
		  {text:'Eliminar',handler:function(){NS.opciones(3);}}
	    ],
		items:[	
		  NS.gridLeyendas
		]
	});
	
	NS.panelMantenimiento = new Ext.form.FieldSet({
		title: 'Leyenda',
		x: 45,
		y: 360,
		width: 910,
		height: 100,
		layout: 'absolute',
		autoScroll: false,
		buttonAlign: 'center',
	    buttons:[
	      {text:'Aceptar',handler:function(){NS.aceptar();}},
		  {text:'Cancelar',handler:function(){NS.cancelar();}},
	    ],
		items:[	
		 	//NS.lbLeyenda,
			NS.txtLeyenda
		]
	});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 10,
		y: 0,
		width: 1060,		
		height: 490,
		layout: 'absolute',
		items:[			 	 	
		 	NS.panelComponentes,
		 	NS.panelMantenimiento
	    ]
	});

	NS.conceptos = new Ext.FormPanel ({
		title: 'Leyendas',
		width: 1085,
		height: 535,
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

	NS.panelMantenimiento.hide();
	NS.conceptos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
	
});