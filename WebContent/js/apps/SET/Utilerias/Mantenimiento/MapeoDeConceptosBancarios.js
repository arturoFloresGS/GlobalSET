Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.MapeoDeConceptosBancarios');
	NS.tabContId = apps.SET.tabContainerId;
//	NS.idUsuario = apps.SET.iUserId;
	//NS.noEmpresa = apps.SET.noEmpresa;
	NS.fecHoy = apps.SET.FEC_HOY ;//BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
//	NS.tipoBanco = 0;
//	NS.idBanco = 0;
//	NS.aba = 0;
//	NS.descBanco = 0;
//	NS.modificar = false;
	NS.idSecuencia = 0;
	NS.matrizDatos = new Object();
	NS.modificar=false;
	
	
	NS.storeChequerasAsignadasG = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {idBanco : '0', secuencia : 0},
		paramOrder: ['idBanco','secuencia'],
		directFn: MapeoAction.obtenerChequerasAV,
		
		fields: [
			{name: 'referencia' },
			{name: 'descripcion'}
		],	 
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequerasAsignadasG, msg:"Cargando..."});
			} 
		}
	});
	
	NS.storeChequerasAsignadas = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {idBanco : '0', secuencia : 0},
		paramOrder: ['idBanco','secuencia'],
		directFn: MapeoAction.obtenerChequerasAV,
		
		fields: [
			{name: 'referencia' },
			{name: 'descripcion'}
		],	 
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequerasAsignadas, msg:"Cargando..."});
			} 
		}
	});
	
	
	
	
	
	NS.storeChequerasPorAsignar = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {idBanco: '0', secuencia:0},
		root: '',
		paramOrder: ['idBanco','secuencia'],
		directFn: MapeoAction.obtenerChequeras,
		idProperty: 'referencia',
		fields: [
			{name: 'referencia' },
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequerasPorAsignar, msg:"Cargando..."});
			
			}
		}
	});
	
	NS.columnaSeleccionC = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGridC = new Ext.grid.ColumnModel([
	                                            
	            {header: 'Id ', width: 100,  dataIndex: 'referencia', sortable: true},
	            {header: 'Descripcion', width:215 , dataIndex: 'descripcion', sortable: true},
	             
	             ]); 
	
NS.gridChequerasPorAsignar = new Ext.grid.GridPanel( {
		name: PF+'gridChequerasPorAsignar',
		id: PF+'gridChequerasPorAsignar',
		store : NS.storeChequerasPorAsignar,
		cm: NS.columnasGridC,
		sm: NS.columnaSeleccionC,
		height :232,
		width :318,
		x :0,
		y :0,
		stripeRows : true,
		columnLines: true,
		
	});

	
	
	
//	NS.storeChequerasPorAsignarM = new Ext.data.DirectStore({
//		paramsAsHash: false,
//		baseParams: {idBanco:0, bandera:0},
//		root: '',
//		paramOrder: ['idBanco','bandera'],
//		directFn: MapeoAction.obtenerChequerasM,
//		idProperty: 'referencia',
//		fields: [
//			{name: 'referencia' },
//			{name: 'descripcion'}
//		],
//		listeners: {
//			load: function(s, records){
//			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequerasPorAsignarM, msg:"Cargando..."});
//			
//			}
//		}
//	});
//	
	
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: MapeoAction.llenaBanco,
		fields:
		[
		 	{name: 'idBanco'},
		 	{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de banco');
			}
		}
	});
	
	NS.storeRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {poliza:'0'},
		paramOrder: ['poliza'],
		directFn: MapeoAction.llenaRubro,
		idProperty: 'idRubro',
		fields:
		[
		 	{name: 'idRubro'},
		 	{name: 'descRubro'},
		 	{name: 'idGrupo'},
		 	{name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubro, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de rubro');
			}
		}
	});
	
	NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {poliza: '0'},
		paramOrder: ['poliza'],
		directFn: MapeoAction.llenaGrupo,
		fields:
		[
		 	{name: 'idGrupo'},
		 	{name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupo, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de grupo rubro');
			}
		}
	});
	
	NS.storePoliza = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: MapeoAction.llenaPoliza,
		fields:
		[
		 	{name: 'idPoliza'},
		 	{name: 'descPoliza'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePoliza, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de poliza');
				
			}
		
		}
	});
	
	NS.cambiaGRubro = function(valueCombo){
		Ext.getCmp(PF + 'txtGRubro').reset();
		NS.cmbGRubro.reset();
		NS.storeGrupo.removeAll();
		
		if ((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0){
			if (valueCombo == 0)
				Ext.getCmp(PF + 'txtIdPoliza').setValue(valueCombo);
			
			NS.storeGrupo.baseParams.noPoliza = '' +(valueCombo);
			NS.storeGrupo.load();
			
		}
	};
	
	
	
	
	
	
	////////////funciones
	NS.continuar = function(){
		//NS.matrizDatos = new Object();
//		NS.storeChequerasPorAsignar.removeAll();
//		NS.gridChequerasPorAsignar.getView().refresh();
		NS.storeChequerasAsignadas.removeAll();
		NS.gridChequerasAsignadas.getView().refresh();
		NS.storeChequerasPorAsignar.load();
		NS.storeChequerasAsignadas.load();
		NS.porAsignar.setVisible(true);
		NS.Asignadas.setVisible(true);
		NS.lblReferencia.setVisible(true);
		NS.txtReferencia.setVisible(true);
		NS.lblConcepto.setVisible(true);
		NS.txtConcepto.setVisible(true);
		NS.lblDescripcion.setVisible(true);
		NS.txtDescripcion.setVisible(true);
		NS.txtObservacion.setVisible(true);
		NS.lblObservacion.setVisible(true);
		NS.lblTipo.setVisible(true);
		NS.cmbTipo.setVisible(true);
		NS.lblEspecial.setVisible(true);
		NS.txtEspecial.setVisible(true);
		NS.lblActivo.setVisible(true);
		NS.cmbActivo.setVisible(true);
		Ext.getCmp(PF + 'Modificar').setVisible(false);
		Ext.getCmp(PF + 'Aceptar').setVisible(true);
		Ext.getCmp(PF + 'Cancelar').setVisible(true);
		Ext.getCmp(PF + 'Agregar').setVisible(true);
		Ext.getCmp(PF + 'Quitar').setVisible(true);
		Ext.getCmp(PF + 'AgregarTodos').setVisible(true);
		Ext.getCmp(PF + 'QuitarTodos').setVisible(true);
		Ext.getCmp(PF + 'Continuar').setVisible(false);
	};
	
	NS.modificarM = function(){
		NS.porAsignar.setVisible(true);
		NS.Asignadas.setVisible(true);
		NS.lblReferencia.setVisible(true);
		NS.txtReferencia.setVisible(true);
		NS.lblConcepto.setVisible(true);
		NS.txtConcepto.setVisible(true);
		NS.lblDescripcion.setVisible(true);
		NS.txtDescripcion.setVisible(true);
		NS.txtObservacion.setVisible(true);
		NS.lblObservacion.setVisible(true);
		NS.lblTipo.setVisible(true);
		NS.cmbTipo.setVisible(true);
		NS.lblEspecial.setVisible(true);
		NS.txtEspecial.setVisible(true);
		NS.lblActivo.setVisible(true);
		NS.cmbActivo.setVisible(true);
			Ext.getCmp(PF + 'Modificar').setVisible(true);
			Ext.getCmp(PF + 'Aceptar').setVisible(false);
			Ext.getCmp(PF + 'Cancelar').setVisible(true);
			Ext.getCmp(PF + 'Agregar').setVisible(true);
			Ext.getCmp(PF + 'Quitar').setVisible(true);
			Ext.getCmp(PF + 'AgregarTodos').setVisible(true);
			Ext.getCmp(PF + 'QuitarTodos').setVisible(true);
			Ext.getCmp(PF + 'Continuar').setVisible(false);
	};
	
	NS.nuevo = function(){
		NS.porAsignar.setVisible(false);
		NS.Asignadas.setVisible(false);
		NS.lblReferencia.setVisible(false);
		NS.txtReferencia.setVisible(false);
		NS.lblConcepto.setVisible(false);
		NS.txtConcepto.setVisible(false);
		NS.lblDescripcion.setVisible(false);
		NS.txtDescripcion.setVisible(false);
		NS.txtObservacion.setVisible(false);
		NS.lblObservacion.setVisible(false);
		NS.lblTipo.setVisible(false);
		NS.cmbTipo.setVisible(false);
		NS.lblEspecial.setVisible(false);
		NS.txtEspecial.setVisible(false);
		NS.lblActivo.setVisible(false);
		NS.cmbActivo.setVisible(false);
			Ext.getCmp(PF + 'Modificar').setVisible(false);
			Ext.getCmp(PF + 'Aceptar').setVisible(false);
			Ext.getCmp(PF + 'Cancelar').setVisible(false);
			Ext.getCmp(PF + 'Agregar').setVisible(false);
			Ext.getCmp(PF + 'Quitar').setVisible(false);
			Ext.getCmp(PF + 'AgregarTodos').setVisible(false);
			Ext.getCmp(PF + 'QuitarTodos').setVisible(false);
			Ext.getCmp(PF + 'Continuar').setVisible(true);
	};
	
	NS.limpiar = function(){
//		NS.storeChequerasPorAsignar.removeAll();
//		NS.gridChequerasPorAsignar.getView().refresh();
		NS.storeChequerasAsignadas.removeAll();
		NS.gridChequerasAsignadas.getView().refresh();
		NS.txtReferencia.setValue('');
		NS.txtConcepto.setValue('');
		NS.txtDescripcion.setValue('');
		NS.txtObservacion.setValue('');
		NS.txtDGrupo.setValue('');
		NS.cmbTipo.reset();
		NS.txtIdPoliza.setValue('');
		NS.cmbPoliza.reset();
		NS.txtEspecial.setValue('');
		NS.cmbActivo.reset();
		NS.txtGRubro.setValue('');
//		NS.cmbGRubro.reset();
		NS.txtRubro.setValue('');
		NS.cmbRubro.reset();
		NS.txtBanco.setValue('');
		NS.cmbBanco.reset();
	};
	
	
	
	
	NS.quitarModificar = function(){
		var seleccion =  NS.gridChequerasAsignadas.getSelectionModel().getSelections();
		var tamano = NS.storeChequerasPorAsignar.data.items.length;
		if (seleccion.length > 0) {
			
			var datosClase = NS.gridChequerasPorAsignar.getStore().recordType;
			var datos = new datosClase({
				referencia: seleccion[0].get("referencia"),
				descripcion: seleccion[0].get("descripcion")
			});
			NS.storeChequerasPorAsignar.insert(tamano, datos);
			NS.storeChequerasAsignadas.remove(seleccion[0]);
			NS.gridChequerasPorAsignar.getView().refresh();
			NS.gridChequerasPorAsignar.getView().refresh();
			MapeoAction.eliminarChequerasAV(parseInt(NS.idSecuencia),(seleccion[0].get("referencia")),''+(NS.txtBanco.getValue()),
					function(resultado , e){
				
			});
			

		} else {
			Ext.Msg.alert('SET',"Debe seleccionar una chequera");
		}
	};
	
	
	
	NS.agregarModificar = function(){
		var seleccion =  NS.gridChequerasPorAsignar.getSelectionModel().getSelections();
		var tamano = NS.storeChequerasAsignadas.data.items.length;
		if (seleccion.length > 0) {
			
			var datosClase = NS.gridChequerasAsignadas.getStore().recordType;
			var datos = new datosClase({
				referencia: seleccion[0].get("referencia"),
				descripcion: seleccion[0].get("descripcion")
			});
			NS.storeChequerasAsignadas.insert(tamano, datos);
			NS.storeChequerasPorAsignar.remove(seleccion[0]);
			NS.gridChequerasPorAsignar.getView().refresh();
			NS.gridChequerasAsignadas.getView().refresh();
			MapeoAction.agregarChequerasAV(parseInt(NS.idSecuencia),''+(seleccion[0].get("referencia")),''+(NS.txtBanco.getValue()),
					function(resultado , e){
				
			});
			

		} else {
			Ext.Msg.alert('SET',"Debe seleccionar una chequera");
		}
	};
	
	
	NS.quitarNuevo = function(){
		var seleccion =  NS.gridChequerasAsignadas.getSelectionModel().getSelections();
		var tamano = NS.storeChequerasPorAsignar.data.items.length;
		if (seleccion.length > 0) {
			if (NS.matrizDatos[''+seleccion[0].get("referencia")]!= undefined) {
				delete NS.matrizDatos[''+seleccion[0].get("referencia")];
			}
			console.log(NS.matrizDatos);
			
			var datosClase = NS.gridChequerasPorAsignar.getStore().recordType;
			var datos = new datosClase({
				referencia: seleccion[0].get("referencia"),
				descripcion: seleccion[0].get("descripcion")
			});
			NS.storeChequerasPorAsignar.insert(tamano, datos);
			NS.storeChequerasAsignadas.remove(seleccion[0]);
			NS.gridChequerasPorAsignar.getView().refresh();
			NS.gridChequerasAsignadas.getView().refresh();
			

		} else {
			Ext.Msg.alert('SET',"Debe seleccionar una chequera");
		}
	};
	
	NS.agregarTodosM = function(){
		NS.storeChequerasPorAsignar.baseParams.idBanco= ''+ NS.gridConsulta.getSelectionModel().getSelections()[0].get('idBanco');
		NS.storeChequerasPorAsignar.baseParams.secuencia=NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
		NS.storeChequerasAsignadas.baseParams.idBanco= ''+ NS.gridConsulta.getSelectionModel().getSelections()[0].get('idBanco');
		NS.storeChequerasAsignadas.baseParams.secuencia=NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
	
		MapeoAction.agregarTodasM(''+ (NS.gridConsulta.getSelectionModel().getSelections()[0].get('idBanco')),
		NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia')+'', function(resultado, e){
							  
			NS.storeChequerasPorAsignar.load();
			NS.storeChequerasAsignadas.load();
		  });
	};
	
	NS.agregarNuevo = function(){
		var seleccion =  NS.gridChequerasPorAsignar.getSelectionModel().getSelections();
		var tamano = NS.storeChequerasAsignadas.data.items.length;
		if (seleccion.length > 0) {
			var vectorDatos = {};
			vectorDatos.referencia= seleccion[0].get("referencia");
			vectorDatos.descripcion = seleccion[0].get("descripcion");
			NS.matrizDatos[''+seleccion[0].get("referencia")]= vectorDatos;
			console.log(NS.matrizDatos);
			var datosClase = NS.gridChequerasAsignadas.getStore().recordType;
			var datos = new datosClase({
				referencia: seleccion[0].get("referencia"),
				descripcion: seleccion[0].get("descripcion")
			});
			NS.storeChequerasAsignadas.insert(tamano, datos);
			NS.storeChequerasPorAsignar.remove(seleccion[0]);
			NS.gridChequerasPorAsignar.getView().refresh();
			NS.gridChequerasAsignadas.getView().refresh();
			

		} else {
			Ext.Msg.alert('SET',"Debe seleccionar una chequera");
		}
	};
	
	NS.quitarTodos = function(){
		var tamano = NS.storeChequerasPorAsignar.data.items;
		var seleccion = NS.storeChequerasAsignadas.data.items;
		var tam = seleccion.length;
//		if (seleccion.length > 0) {
			for(var i=0; i<tam; i++){
				var vectorDatos = {};
				vectorDatos.referencia= seleccion[0].get("referencia");
				vectorDatos.descripcion = seleccion[0].get("descripcion");
				NS.matrizDatos[''+seleccion[0].get("referencia")]= vectorDatos;
				console.log(NS.matrizDatos);
				var datosClase = NS.gridChequerasPorAsignar.getStore().recordType;
				var datos = new datosClase({
					referencia: seleccion[0].get("referencia"),
					descripcion: seleccion[0].get("descripcion")
				});
				NS.storeChequerasPorAsignar.insert(tamano.length, datos);
				NS.storeChequerasAsignadas.remove(seleccion[0]);
			}
			NS.gridChequerasPorAsignar.getView().refresh();
			NS.gridChequerasAsignadas.getView().refresh();
	};
	
	NS.agregarTodos = function(){		
		var tamano = NS.storeChequerasAsignadas.data.items;
		var seleccion = NS.storeChequerasPorAsignar.data.items;
		var tam = seleccion.length;
//		if (seleccion.length > 0) {
			for(var i=0; i<tam; i++){
				var vectorDatos = {};
				vectorDatos.referencia= seleccion[0].get("referencia");
				vectorDatos.descripcion = seleccion[0].get("descripcion");
				NS.matrizDatos[''+seleccion[0].get("referencia")]= vectorDatos;
				console.log(NS.matrizDatos);
				var datosClase = NS.gridChequerasAsignadas.getStore().recordType;
				var datos = new datosClase({
					referencia: seleccion[0].get("referencia"),
					descripcion: seleccion[0].get("descripcion")
				});
				NS.storeChequerasAsignadas.insert(tamano.length, datos);
				NS.storeChequerasPorAsignar.remove(seleccion[0]);
			}
			NS.gridChequerasPorAsignar.getView().refresh();
			NS.gridChequerasAsignadas.getView().refresh();
			

			//	} else {
//			Ext.Msg.alert('SET',"No existen chequeras para su asignacion");
//		}
	};
	
	NS.llenaDatosMapeo = function(){
		NS.storeChequerasPorAsignar.baseParams.idBanco= '' + NS.gridConsulta.getSelectionModel().getSelections()[0].get('idBanco');
		NS.storeChequerasPorAsignar.baseParams.secuencia=NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
		NS.storeChequerasPorAsignar.load();
		
		NS.storeChequerasAsignadas.baseParams.idBanco= '' + NS.gridConsulta.getSelectionModel().getSelections()[0].get('idBanco');
		NS.storeChequerasAsignadas.baseParams.secuencia=NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
		NS.storeChequerasAsignadas.load();
		
		NS.idPoliza= NS.gridConsulta.getSelectionModel().getSelections()[0].get('idPoliza');
		NS.idGrupo = NS.gridConsulta.getSelectionModel().getSelections()[0].get('idGrupo');
		NS.idRubro= NS.gridConsulta.getSelectionModel().getSelections()[0].get('idRubro');
		NS.idBanco = NS.gridConsulta.getSelectionModel().getSelections()[0].get('idBanco');
		NS.idSecuencia = NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
		
		MapeoAction.obtieneDatos(''+NS.idPoliza,''+ NS.idGrupo, ''+NS.idRubro,''+NS.idBanco, function(resultado, e){
			
			
			if (resultado !== null && resultado !== '' && resultado !== undefined)
			{
				NS.llenaCamposMapeo(resultado);
			}
			else
				Ext.Msg.alert('SET', 'No existe información para este mapeo');
		});
		
	};
	
	
	NS.actualizarMapeo = function(){
		
//		var matrizAux = $.map(NS.matrizDatos,function(value, index){
//			return [value];
//		});
//		alert(matrizAux);
//		if (matrizAux.length > 0) {
			
			var vector = {};
			var matriz = new Array();
			
			vector.idPoliza =NS.txtIdPoliza.getValue();
			vector.descPoliza = NS.cmbPoliza.getValue();
			vector.idGrupo = Ext.getCmp(PF + 'txtGRubro').getValue();
			vector.descGrupo = Ext.getCmp(PF + 'txtDGrupo').getValue();
			vector.idRubro = Ext.getCmp(PF + 'txtRubro').getValue();
			vector.descRubro = Ext.getCmp(PF + 'cmbRubro').getValue();
			vector.idBanco = Ext.getCmp(PF + 'txtBanco').getValue();
			vector.descBanco = Ext.getCmp(PF + 'cmbBanco').getValue();
			vector.referencia = Ext.getCmp(PF + 'txtReferencia').getValue();
			vector.concepto = Ext.getCmp(PF + 'txtConcepto').getValue();
			vector.descripcion = Ext.getCmp(PF + 'txtDescripcion').getValue();
			vector.observacion = Ext.getCmp(PF + 'txtObservacion').getValue();
			vector.tipo = Ext.getCmp(PF + 'cmbTipo').getValue();
			vector.especial = Ext.getCmp(PF + 'txtEspecial').getValue();
			vector.activo = Ext.getCmp(PF + 'cmbActivo').getValue();
			
			
			
//			if(NS.modificar){
//				vector.tipoOperacion = "MODIFICAR";
//			}else{
//				vector.tipoOperacion= "INSERTAR";
//			}
			
		
			matriz[0] = vector;
			var jSonString = Ext.util.JSON.encode(matriz);
			console.log(jSonString);
				
//			var jSonChequeras = Ext.util.JSON.encode(matrizAux);
			//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
			// MapeoAction.actualizaMapeo(jSonString, /*jSonChequeras,*/ secuencia, function(resultado, e){	
			  MapeoAction.actualizaMapeo(jSonString, ''+NS.idSecuencia, function(resultado, e){			  
				if (resultado == ''){
					Ext.Msg.alert('SET', resultado);		
				}
				else if (resultado !== '' && resultado !== undefined && resultado !== null)
				  	Ext.Msg.alert('SET', resultado);
					NS.limpiar();
					NS.panelContenedor.setVisible(false);
					NS.storeLlenaGrid.load();
					NS.storeChequerasAsignadas.removeAll();
					NS.gridChequerasAsignadas.getView().refresh();
					NS.matrizDatos = new Object();
			  });
			
//		} else {
//				Ext.Msg.alert("SET","No se han asignado chequeras");
//		}
		
		
 		
 		
 	};
	
	
	NS.insertarActualizarMapeo = function(){
		
		var matrizAux = $.map(NS.matrizDatos,function(value, index){
			return [value];
		});
		
		console.log(matrizAux);
		if (matrizAux.length > 0) {
			
			var vector = {};
			var matriz = new Array();
			
			
			vector.idPoliza = Ext.getCmp(PF + 'txtIdPoliza').getValue();
			vector.idGrupo = Ext.getCmp(PF + 'txtGRubro').getValue();		
			vector.idRubro = Ext.getCmp(PF + 'txtRubro').getValue();
			vector.idBanco = Ext.getCmp(PF + 'txtBanco').getValue();
			vector.referencia = Ext.getCmp(PF + 'txtReferencia').getValue();
			vector.concepto = Ext.getCmp(PF + 'txtConcepto').getValue();
			vector.descripcion = Ext.getCmp(PF + 'txtDescripcion').getValue();
			vector.observacion = Ext.getCmp(PF + 'txtObservacion').getValue();
			vector.tipo = Ext.getCmp(PF + 'cmbTipo').getValue();
			vector.especial = Ext.getCmp(PF + 'txtEspecial').getValue();
			vector.activo = Ext.getCmp(PF + 'cmbActivo').getValue();
			
//			if(NS.modificar){
//				vector.tipoOperacion = "MODIFICAR";
//			}else{
//				vector.tipoOperacion= "INSERTAR";
//			}
			
		
			matriz[0] = vector;
			var jSonString = Ext.util.JSON.encode(matriz);
				
			var jSonChequeras = Ext.util.JSON.encode(matrizAux);
			//Se validan los datos si no manda mensaje de error se inserta o actualiza el registro
			  MapeoAction.insertaMapeo(jSonString, jSonChequeras,NS.idSecuencia, function(resultado, e){			  
				if (resultado == ''){
										
				}
				else if (resultado !== '' && resultado !== undefined && resultado !== null)
				  	Ext.Msg.alert('SET', resultado);
					NS.limpiar();
					NS.panelContenedor.setVisible(false);
					NS.storeLlenaGrid.load();
					NS.storeChequerasAsignadas.removeAll();
					NS.gridChequerasAsignadas.getView().refresh();
					NS.matrizDatos = new Object();
					
					
			  });
			
			
		} else {
				Ext.Msg.alert("SET","No se han asignado chequeras");
		}
		
		
 		
 		
 	};
 	

	NS.lblPoliza = new Ext.form.Label({
		text: 'Poliza',
		x: 30,
		y: 20		
	});
	
	NS.txtIdPoliza = new Ext.form.TextField({
		id: PF + 'txtIdPoliza',
		name: PF + 'txtIdPoliza',
		x: 30,
		y: 40,
		width: 50,		
			listeners: {
	 			change: {
	 				
	 				fn: function(caja, valor) {
	 					
	 				if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
 						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdPoliza', NS.cmbPoliza.getId());
	 					NS.storeRubro.baseParams.poliza = ''+(valor);
	 					NS.storeRubro.load();
//	 					NS.txtIdPoliza = comboValor;
// 					else
// 						NS.cmbPoliza.reset();
 				}
	 			}
	 		}	 	
	});
	
	
	


	NS.cmbPoliza = new Ext.form.ComboBox({
		store: NS.storePoliza,
		id: PF + 'cmbPoliza',
		name: PF + 'cmbPoliza',
		x: 90,
		y: 40,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idPoliza',
		displayField: 'descPoliza',
		autocomplete: true,
		emptyText: 'Poliza',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdPoliza', NS.cmbPoliza.getId());
			
					NS.storeRubro.baseParams.poliza=''+NS.cmbPoliza.getValue();
					NS.storeRubro.load();
					NS.txtRubro.setValue("");
					NS.cmbRubro.reset();
					NS.txtGRubro.setValue("");
					NS.txtDGrupo.setValue("");
					
					
					//NS.cambiaGRubro(combo.getValue());
					
					

				}
			}
			
		}
		
	});
	
	 NS.storePoliza.load();

	NS.lblGRubro = new Ext.form.Label({
		text: 'Grupo Rubro',
		x: 490,
		y: 20		
	});
	
	NS.txtGRubro = new Ext.form.TextField({
		readOnly: true,
		id: PF + 'txtGRubro',
		name: PF + 'txtGRubro',
		x: 490,
		y: 40,
		width: 50,		
		listeners:
		{
			change:
			{
				
				fn: function(caja, valor){
 					if (caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
 						BFwrk.Util.updateTextFieldToCombo(PF + 'txtGRubro',PF + 'txtDGrupo'.getId());
 				}
			}
		}
	});
	
	NS.txtDGrupo = new Ext.form.TextField({
		readOnly: true,
		id: PF + 'txtDGrupo',
		name: PF + 'txtDGrupo',
		x: 550,
		y: 40,
		width: 150,		
		listeners:
		{
			change:
			{
				fn: function(){
 					if (caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
 						BFwrk.Util.updateTextFieldToCombo(PF + 'txtGRubro', PF + 'txtDGrupo'.getId());
 				}
			}
		}
	});




	NS.lblRubro = new Ext.form.Label({
		text: 'Rubro',
		x: 260,
		y: 20		
	});
	
	NS.txtRubro = new Ext.form.TextField({
		id: PF + 'txtRubro',
		name: PF + 'txtRubro',
		x: 260,
		y: 40,
		width: 50,		
		listeners:
		{
			change:
			{
				
					fn: function(caja, valor)
	 				{
	 					if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined){
	 						BFwrk.Util.updateTextFieldToCombo(PF + 'txtRubro', NS.cmbRubro.getId());
	 						NS.txtGRubro.setValue(NS.storeRubro.getById((caja.getValue())).get('idGrupo'));
	 						NS.txtDGrupo.setValue(NS.storeRubro.getById((caja.getValue())).get('descGrupo'));
	 						
	 					}
	 						else
	 						NS.cmbGrupo.reset();
	 				}
				
			}
		}
	});


	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeRubro,
		id: PF + 'cmbRubro',
		name: PF + 'cmbRubro',
		x: 320,
		y: 40,
		width: 150,
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
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtRubro', NS.cmbRubro.getId());
					NS.txtGRubro.setValue(NS.storeRubro.getById((combo.getValue())).get('idGrupo'));
					NS.txtDGrupo.setValue(NS.storeRubro.getById((combo.getValue())).get('descGrupo'));
				}
			}
		}
		
	});
	
//	NS.storeRubro.load();
	
	NS.lblBanco = new Ext.form.Label({
		text: 'Banco',
		x: 720,
		y: 20		
	});
	
	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
		name: PF + 'txtBanco',
		x: 720,
		y: 40,
		width: 50,		
		listeners:
		{
			change:
			{
				
					fn: function(caja, valor)
	 				{
	 					if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
	 						BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
	 					else
	 						NS.cmbBanco.reset();
	 				}
				
			}
		}
	});


	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 780,
		y: 40,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBanco.getId());
					NS.storeChequerasPorAsignar.baseParams.idBanco='' + NS.cmbBanco.getValue();
					NS.storeChequerasPorAsignar.baseParams.secuencia=0;
					NS.storeChequerasPorAsignar.load();
				}
			}
		}
		
	});
	
	NS.storeBanco.load();
	
	
	
	
	
	
	
	NS.lblReferencia = new Ext.form.Label({
	text: 'Referencia',
	x: 45,
	y: 350,		
	});

	NS.txtReferencia = new Ext.form.TextField({
	id: PF + 'txtReferencia',
	name: PF + 'txtReferencia',
	x: 105,
	y: 350,
	width: 200,		
	listeners:
	{
		change:
		{
			fn: function()
			{
				
			}
		}
	}
	});
	
	NS.lblConcepto = new Ext.form.Label({
		text: 'Concepto',
		x: 45,
		y: 380,		
		});

		NS.txtConcepto = new Ext.form.TextField({
		id: PF + 'txtConcepto',
		name: PF + 'txtConcepto',
		x: 105,
		y: 380,
		width: 200,		
		listeners:
		{
			change:
			{
				fn: function()
				{
					
				}
			}
		}
		});
		
			NS.lblDescripcion = new Ext.form.Label({
			text: 'Descripcion',
			x: 45,
			y: 410,		
			});
			

			NS.txtDescripcion = new Ext.form.TextField({
			id: PF + 'txtDescripcion',
			name: PF + 'txtDescripcion',
			x: 105,
			y: 410,
			width: 200,		
			listeners:
			{
				change:
				{
					fn: function()
					{
						
					}
				}
			}
			});
			
			NS.lblObservacion = new Ext.form.Label({
				text: 'Observacion',
				x: 45,
				y: 440,		
				});
				

				NS.txtObservacion = new Ext.form.TextField({
				id: PF + 'txtObservacion',
				name: PF + 'txtObservacion',
				x: 105,
				y: 440,
				width: 200,		
				listeners:
				{
					change:
					{
						fn: function()
						{
							
						}
					}
				}
				});
				
				NS.lblTipo = new Ext.form.Label({
					text: 'Tipo',
					x: 45,
					y: 470		
				});
				
				NS.datosComboTipo = [
				                    	   ['E', 'Egresos'],
				                    	   ['I', 'Ingresos']		       			
				                    	];	       		
				                    	                   	 
				                    	NS.storeTipo = new Ext.data.SimpleStore({
				                    		idProperty: 'idTipo',
				                    	    fields: [
				                    	             {name: 'idTipo'},
				                    	             {name: 'descTipo'}
				                    	    ]
				                    	});
				                    	NS.storeTipo.loadData(NS.datosComboTipo);
				

				NS.cmbTipo = new Ext.form.ComboBox({
					store: NS.storeTipo,
					id: PF + 'cmbTipo',
					name: PF + 'cmbTipo',
					x: 105,
					y: 470,
					width: 150,
					typeAhead: true,
					mode: 'local',
					selecOnFocus: true,
					forceSelection: true,
					valueField: 'idTipo',
					displayField: 'descTipo',
					autocomplete: true,
					emptyText: 'Tipo',
					triggerAction: 'all',
					value: '',
					listeners: {
						select: {
							fn: function() {
								
							}
						}
					}
					
				});
				
				NS.lblEspecial = new Ext.form.Label({
					text: 'Especial',
					x: 30,
					y: 470,		
					});
					

					NS.txtEspecial = new Ext.form.TextField({
					id: PF + 'txtEspecial',
					name: PF + 'txtEspecial',
					x: 90,
					y: 470,
					width: 200,		
					listeners:
					{
						change:
						{
							fn: function()
							{
								
							}
						}
					}
					});
					
					NS.lblActivo = new Ext.form.Label({
						text: 'Activo',
						x: 45,
						y: 500,		
					});
					
					NS.datosComboActivo = [
				                    	   ['S', 'Si'],
				                    	   ['N', 'No']		       			
				                    	];	       		
				                    	                   	 
				                    	NS.storeActivo = new Ext.data.SimpleStore({
				                    		idProperty: 'idActivo',
				                    	    fields: [
				                    	             {name: 'idActivo'},
				                    	             {name: 'descActivo'}
				                    	    ]
				                    	});
				                    	NS.storeActivo.loadData(NS.datosComboActivo);
					

					NS.cmbActivo = new Ext.form.ComboBox({
						store: NS.storeActivo,
						id: PF + 'cmbActivo',
						name: PF + 'cmbActivo',
						x: 105,
						y: 500,
						width: 150,
						typeAhead: true,
						mode: 'local',
						selecOnFocus: true,
						forceSelection: true,
						valueField: 'idActivo',
						displayField: 'descActivo',
						autocomplete: true,
						emptyText: 'Activo',
						triggerAction: 'all',
						value: '',
						listeners: {
							select: {
								fn: function() {
									
								}
							}
						}
						
					});
	
					NS.llenaCamposMapeo = function(registro){	
						Ext.getCmp(PF + 'txtIdPoliza').setValue(registro[0].idPoliza);
//						alert(registro[0].descPoliza+","+registro[0].descGrupo+","+registro[0].descRubro+","+registro[0].descBanco);
						Ext.getCmp(PF + 'cmbPoliza').setValue(registro[0].descPoliza);
						Ext.getCmp(PF + 'txtGRubro').setValue(registro[0].idGrupo);
						Ext.getCmp(PF + 'txtDGrupo').setValue(registro[0].descGrupo);
						Ext.getCmp(PF + 'txtRubro').setValue(registro[0].idRubro);
						Ext.getCmp(PF + 'cmbRubro').setValue(registro[0].descRubro);
						Ext.getCmp(PF + 'txtBanco').setValue(registro[0].idBanco);
						Ext.getCmp(PF + 'cmbBanco').setValue(registro[0].descBanco);
						
						Ext.getCmp(PF + 'txtReferencia').setValue(registro[0].referencia);
						Ext.getCmp(PF + 'txtConcepto').setValue(registro[0].concepto);
						Ext.getCmp(PF + 'txtDescripcion').setValue(registro[0].descripcion);				
						Ext.getCmp(PF + 'txtObservacion').setValue(registro[0].observacion);
						
						Ext.getCmp(PF + 'txtEspecial').setValue(registro[0].especial);
						Ext.getCmp(PF + 'cmbTipo').setValue(registro[0].tipo);		
						Ext.getCmp(PF + 'cmbActivo').setValue(registro[0].activo);	
							
					
					};
	
					//Columna de seleccion en el grid
					NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
						singleSelect: false
					});
					
					//Columnas de grid
					NS.columnasGrid = new Ext.grid.ColumnModel([
					    {header: 'Secuencia', width: 100,  dataIndex: 'secuencia', sortable: true, hidden:true},                                        
					    {header: 'Poliza', width: 100,  dataIndex: 'idPoliza', sortable: true},
					    {header: 'Grupo Rubros', width: 100, dataIndex: 'idGrupo', sortable: true},
					    {header: 'Rubro', width: 100, dataIndex: 'idRubro', sortable: true},
					    {header: 'Banco', width: 100, dataIndex: 'idBanco', sortable: true},
					    {header: 'Referencia', width: 70, dataIndex: 'referencia', sortable: true},
					    {header: 'Concepto', width: 70, dataIndex: 'concepto', sortable: true},
					    {header: 'Descripcion', width: 100, dataIndex: 'descripcion', sortable: true},
					    {header: 'Observacion', width: 100, dataIndex: 'observacion', sortable: true},	
					    {header: 'Tipo', width: 60, dataIndex: 'tipo', sortable: true},	
					    {header: 'Especial', width: 60, dataIndex: 'especial', sortable: true, hidden:true},	    
					    {header: 'Activo', width: 60, dataIndex: 'activo', sortable: true},	    

					]); 
	
	NS.storeLlenaGrid = new Ext.data.DirectStore({
	paramsAsHash: false,
	root: '',
	directFn: MapeoAction.llenaGrid,
	fields:
	[
	 	{name: 'secuencia'},
	 	{name: 'idPoliza'}, //,type:'number'
	 	{name: 'idGrupo'},
	 	{name: 'idRubro'},
	 	{name: 'idBanco'},
	 	{name: 'referencia'},
	 	{name: 'concepto'},
	 	{name: 'descripcion'},
	 	{name: 'observacion'},
	 	{name: 'tipo'},
	 	{name: 'especial'},
	 	{name: 'activo'}	
	],
	listeners: {
		load: function (s, records) {
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando ..."});
			if(records.length == null || records.length <= 0)
				Ext.Msg.alert('SET', 'No existen datos para estos parametros de busqueda');
		}
	}
});
	
	NS.storeLlenaGrid.load();
	
	NS.eliminaMapeo = function(){
 		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
 		
 		if (registroSeleccionado.length <= 0)
 			Ext.Msg.alert('SET', 'Debe de seleccionar algun registro');
 		else{
 			MapeoAction.verificaRegistro(registroSeleccionado[0].get('secuencia'), function(resultado, e){
 				if (resultado != 0 && resultado != undefined && resultado != null){
 					Ext.Msg.confirm('SET', '¿Esta seguro de eliminar a este mapeo?', function(btn){
 						if (btn == 'yes') {
 							MapeoAction.inhabilitaMapeo(registroSeleccionado[0].get('secuencia'), function(resultado, e){
 								if (resultado != '' && resultado != undefined && resultado != null)
 									MapeoAction.eliminaChequera(registroSeleccionado[0].get('secuencia'), function(resultado, e){
 		 								if (resultado != '' && resultado != undefined && resultado != null)
 		 									Ext.Msg.alert('SET', resultado);
 		 								NS.storeLlenaGrid.load();
 		 							});
 									
 							});
 						}
 					});
 				} 					
 			});
 		} 		
 	};
 	
	NS.validaCampos = function(){
		 if(Ext.getCmp(PF + 'txtReferencia').getValue()=="" && Ext.getCmp(PF + 'txtConcepto').getValue()=="" &&
				 Ext.getCmp(PF + 'txtDescripcion').getValue()=="" && Ext.getCmp(PF + 'txtObservacion').getValue()==""){
			Ext.Msg.alert('SET',"Al menos debe ingresar una opcion de las cuatro disponibles");
			return false;
		}else if(Ext.getCmp(PF + 'cmbTipo').getValue()==""){
			Ext.Msg.alert('SET',"Seleccione el tipo");
			return false;
		}else if(Ext.getCmp(PF + 'cmbActivo').getValue()==""){
			Ext.Msg.alert('SET',"Selecciones la opcion Activo");
			return false;
		}
		else{
			return true;
		}
	};
	
	
	NS.validaMapeo = function(){
		 if(Ext.getCmp(PF + 'txtIdPoliza').getValue()==""){
			Ext.Msg.alert('SET',"Selecciona una poliza");
			return false;
		}else if(Ext.getCmp(PF + 'txtGRubro').getValue()==""){
			Ext.Msg.alert('SET',"Seleccione un grupo");
			return false;
		}else if(Ext.getCmp(PF + 'txtRubro').getValue()=="" ){
			Ext.Msg.alert('SET',"Seleccione un Rubro");
			return false;
		}else if(Ext.getCmp(PF + 'txtBanco').getValue()==""){
			Ext.Msg.alert('SET',"Selecciones un banco");
			return false;
		}
		else{
			return true;
		}
	};
	
	
	NS.columnaSeleccionCA = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGridCA = new Ext.grid.ColumnModel([
	                                            
	            {header: 'Id ', width: 100,  dataIndex: 'referencia', sortable: true},
	            {header: 'Descripcion', width:215 , dataIndex: 'descripcion', sortable: true},
	             
	             ]); 
	
	NS.columnaSeleccionCAG = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGridCAG = new Ext.grid.ColumnModel([
	                                            
	            {header: 'Id ', width: 100,  dataIndex: 'referencia', sortable: true},
	            {header: 'Descripcion', width:215 , dataIndex: 'descripcion', sortable: true},
	             
	             ]); 
	

NS.gridChequerasAsignadasG = new Ext.grid.GridPanel( {
	store : NS.storeChequerasAsignadasG,
	name: PF+'gridChequerasAsignadas',
	id: PF+'gridChequerasAsignadas',
	cm: NS.columnasGridCAG,
	sm: NS.columnaSeleccionCAG,
	height :232,
	width :318,
	x :0,
	y :0,
	stripeRows : true,
	columnLines: true,

});


NS.gridChequerasAsignadas = new Ext.grid.GridPanel( {
	name: PF+'gridChequerasAsignadas',
	id: PF+'gridChequerasAsignadas',
	store : NS.storeChequerasAsignadas,
	cm: NS.columnasGridCA,
	sm: NS.columnaSeleccionCA,
	height :232,
	width :318,
	x :0,
	y :0,
	stripeRows : true,
	columnLines: true,

});

NS.storeChequerasAsignadas.load();


	NS.Asignadas = new Ext.form.FieldSet ({
		title: 'Chequeras Asignadas',
		x: 600,
		y: 80,
		width: 340,
		height: 220,
		layout: 'absolute',
		items:
			[
//	 	
		NS.gridChequerasAsignadas,
	]
	});
	
	NS.porAsignar = new Ext.form.FieldSet ({
		  title: 'Chequeras Por Asignar',
              x: 30,
		      y: 80,
              width: 340,
              height: 220,
              layout: 'absolute',
              id: PF+'frameResgistros',
              items:[
                    NS.gridChequerasPorAsignar,
                     ]	
		
	
	});
	


	
	NS.gridConsulta = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'gridConsulta',
		name: PF + 'gridConsulta',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 960,
		height: 150,
		stripeRows : true,
		columnLines: true,
		listeners: {
			rowClick:{
			//click: {
				fn: function(e)
				{
					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
					
					NS.storeChequerasAsignadasG.baseParams.secuencia=NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
					NS.storeChequerasAsignadasG.load();
					
					venChequeras.show();
				}
			}
		}
	});
	
	NS.panelGrid = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 10,
		width: 985,
		height: 220,
		layout: 'absolute',
		items:
		[
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		x: 580,
		 		y: 175,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					
		 					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
		 					if (registroSeleccionado.length <= 0)
		 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
		 					else
		 					{
		 						NS.modificar=true;
		 						NS.panelContenedor.setVisible(true);
		 						NS.modificarM();
			 					NS.llenaDatosMapeo();
			 					NS.cmbPoliza.setDisabled(true);
			 					NS.txtIdPoliza.setDisabled(true);
			 					NS.cmbRubro.setDisabled(true);
			 					NS.txtRubro.setDisabled(true);
			 					NS.txtDGrupo.setDisabled(true);
			 					NS.txtGRubro.setDisabled(true);
			 					NS.cmbBanco.setDisabled(true);
			 					NS.txtBanco.setDisabled(true);
			 					
			 					
	 					}
		 					}	
		 				}
		 			}
		 		
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 670,
		 		y: 175,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					NS.modificar=false;
		 					NS.limpiar();
		 					NS.panelContenedor.setVisible(true);
		 					NS.nuevo();
		 					NS.cmbPoliza.setDisabled(false);
		 					NS.txtIdPoliza.setDisabled(false);
		 					NS.cmbRubro.setDisabled(false);
		 					NS.txtRubro.setDisabled(false);
		 					NS.txtDGrupo.setDisabled(false);
		 					NS.txtGRubro.setDisabled(false);
		 					NS.cmbBanco.setDisabled(false);
		 					NS.txtBanco.setDisabled(false);
		 					NS.storeChequerasAsignadas.removeAll();
							NS.gridChequerasAsignadas.getView().refresh();
		 					NS.matrizDatos = new Object();
	 						
		 				}
		 			}
		 		}
		 	},
							
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 760,
		 		y: 175,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.eliminaMapeo();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Excel',
		 		x: 850,
		 		y: 175,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					parametros='?nomReporte=excelMapeo';
 							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);	
		 				}
		 			}
		 		}
		 	},
		 	NS.gridConsulta,
		 	
		]
	});
	
	NS.panelCN = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 240,
		width: 985,
		height: 550,
		layout: 'absolute',
//		items:
//		[
//
//	]
	});
	
	var venChequeras = new Ext.Window({
		title: 'CHEQUERAS ASIGNADAS',
		modal: true,
		shadow: true,
		height :240,
		width :330,
		layout: 'absolute',
		plain: true,
		bodyStyle: 'padding:10px;',
		closeAction: 'hide',
		items:[
		       	NS.gridChequerasAsignadasG,

		       	]

	});
	
	NS.cuadro = new Ext.form.FieldSet ({
		title: '',
		x: 29,
		y: 335,
		width: 350,
		height: 233,
		layout: 'absolute',
		
	});
	
	NS.panelContenedor = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 240,
		width: 985,
		height: 595,
		layout: 'absolute',
		items:
		[
			[
			
	NS.panelGrid,
	NS.porAsignar,
	NS.Asignadas,
	NS.lblReferencia,
	NS.txtReferencia,
	NS.lblConcepto,
	NS.txtConcepto,
	NS.lblDescripcion,
	NS.txtDescripcion,
	NS.txtObservacion,
	NS.lblObservacion,
	NS.lblTipo,
	NS.cmbTipo,
	NS.lblPoliza,
	NS.txtIdPoliza,
	NS.cmbPoliza,
//	NS.lblEspecial,
//	NS.txtEspecial,
	NS.lblActivo,
	NS.cmbActivo,
	NS.lblGRubro,
	NS.txtGRubro,
	NS.txtDGrupo,
	//NS.cmbGRubro,
	NS.lblRubro,
	NS.txtRubro,
	NS.cmbRubro,
	NS.lblBanco,
	NS.txtBanco,
	NS.cmbBanco,
	NS.cuadro,
	{
					xtype: 'button',
					id: PF + 'Aceptar',
					name: PF + 'Aceptar',
					text: 'Aceptar',
					x: 100,
					y: 535,
					width: 80,
					height: 22,
					listeners: {
						click: {
							
							fn: function(e)
							{
								if (NS.validaCampos()==true) {
									NS.insertarActualizarMapeo();
									
								}
								NS.storeChequerasAsignadas.removeAll();
								NS.gridChequerasAsignadas.getView().refresh();
								NS.matrizDatos = new Object();
							}
						}
					}
					},

					{
					xtype: 'button',
					text: 'Cancelar',
					id: PF + 'Cancelar',
					name: PF + 'Cancelar',
					x: 190,
					y: 535,
					width: 80,
					height: 22,
					listeners: {
						click: {
							
							fn: function(e)
							{
								NS.limpiar();
								NS.panelContenedor.setVisible(false);
								NS.storeChequerasAsignadas.removeAll();
								NS.gridChequerasAsignadas.getView().refresh();
								NS.matrizDatos = new Object();
							}
						}
					}
					},
					
					{
						xtype: 'button',
						id: PF + 'Modificar',
						name: PF + 'Modificar',
						text: 'Modificar',
						x: 100,
						y: 535,
						width: 80,
						height: 22,
						listeners: {
							click: {
								
								fn: function(e)
								{
									if (NS.validaCampos()==true) {
										NS.actualizarMapeo();
										
									}
									
									
								}
							}
						}
						},
					
					{
						xtype: 'button',
						id: PF + 'Continuar',
						name: PF + 'Continuar',
						text: 'Continuar',
						x: 450,
						y: 80,
//						x: 430,
//						y: 100,
						width: 80,
						height: 22,
						listeners: {
							click: {
								
								fn: function(e)
								{
									if (NS.validaMapeo()==true) {
										MapeoAction.retornaIdMapeo(Ext.getCmp(''+PF + 'txtIdPoliza').getValue(),
												''+Ext.getCmp(PF + 'txtGRubro').getValue(),
												''+Ext.getCmp(PF + 'txtRubro').getValue(),
												''+Ext.getCmp(PF + 'txtBanco').getValue(),
												 function(resultado, e){			  
											if (resultado != '' && resultado == "Ya existe un mapeo con esos valores"||
													resultado=="No se pudo generar secuencia"){
												Ext.Msg.alert('SET', resultado);
												 NS.idSecuencia = 0;
											}else{
												 NS.idSecuencia= resultado; 
												 NS.storeChequerasAsignadas.removeAll();
													NS.gridChequerasAsignadas.getView().refresh();
													NS.storeChequerasAsignadas.removeAll();
													NS.storeChequerasPorAsignar.baseParams.idBanco= '' + (Ext.getCmp(PF + 'txtBanco').getValue());
													NS.storeChequerasPorAsignar.baseParams.secuencia=parseInt(NS.idSecuencia);
													NS.storeChequerasAsignadas.baseParams.idBanco= '' + (Ext.getCmp(PF + 'txtBanco').getValue());
													NS.storeChequerasAsignadas.baseParams.secuencia=parseInt(NS.idSecuencia);
												NS.continuar();
												NS.cmbPoliza.setDisabled(true);
							 					NS.txtIdPoliza.setDisabled(true);
							 					NS.cmbRubro.setDisabled(true);
							 					NS.txtRubro.setDisabled(true);
							 					NS.txtDGrupo.setDisabled(true);
							 					NS.txtGRubro.setDisabled(true);
							 					NS.cmbBanco.setDisabled(true);
							 					NS.txtBanco.setDisabled(true);
											}
											
											
										  });
									} else {

									}
									NS.validaMapeo();
									
									 
								}
							}
						}
						},

					{
					xtype: 'button',
					id: PF + 'Agregar',
					name: PF + 'Agregar',
					text: '>',
					x: 470,
					y: 120,
					width: 30,
					listeners: {
					click:{
					//funcion para asignar uno o varios componentes dependiendo de la seleccion
					fn:function(e) 
					{
						if(!NS.modificar){
							NS.agregarNuevo();
						}else{
							NS.agregarModificar();
						}
						
					}
						  }
						}
					},

					{
						xtype: 'button',
						id: PF + 'Quitar',
						name: PF + 'Quitar',
						text: '<',
						x: 470,
						y: 160,
						width: 30,
						listeners: {
							click:{
							//funcion para asignar uno o varios componentes dependiendo de la seleccion
							fn:function(e) 
							{
								if(!NS.modificar){
									NS.quitarNuevo();
								}else{
									NS.quitarModificar();
								}
								
								////////////
							}
								  }
								}
							},
							
							{
								xtype: 'button',
								id: PF + 'AgregarTodos',
								name: PF + 'AgregarTodos',
								text: '>>',
								x: 470,
								y: 200,
								width: 30,
								listeners: {
									click:{
									//funcion para asignar uno o varios componentes dependiendo de la seleccion
									fn:function(e) 
									{
										
										if(!NS.modificar){
											NS.agregarTodos();
										}else{
											NS.agregarTodosM();
										}
										
										
										
										}
									}
										  }
										
									},
									
									{
										xtype: 'button',
										id: PF + 'QuitarTodos',
										name: PF + 'QuitarTodos',
										text: '<<',
										x: 470,
										y: 240,
										width: 30,
										listeners: {
											click:{
											//funcion para asignar uno o varios componentes dependiendo de la seleccion
											fn:function(e) 
											{
												if(!NS.modificar){
													NS.quitarTodos();
												}else{
													NS.storeChequerasPorAsignar.baseParams.secuencia=NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
													NS.storeChequerasAsignadas.baseParams.secuencia=NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
													
													
													
													MapeoAction.quitarTodasM(NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia')+'', function(resultado, e){
															  
														NS.storeChequerasPorAsignar.load();
														NS.storeChequerasAsignadas.load();
													  });
												}
												
												
												
											}
												  }
												}
											},


		]
	]
	});
	
	
	
	
	NS.global = new Ext.form.FieldSet ({
		title: 'Mapeo de Conceptos Bancarios',
		x: 20,
		y: 5,
		width: 1010,
		height: 880,
		layout: 'absolute',
		items:
		[
		 NS.panelGrid,
		 NS.panelContenedor.setVisible(false),
//		 NS.cuadro,
		


	]
	});
	
	NS.mapeo = new Ext.FormPanel ({
		title: 'Mapeo de conceptos Bancarios',
		width: 1300,
		height: 736,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'mapeo',
		name: PF + 'mapeo',
		renderTo: NS.tabContId,
		items: [
		 	NS.global,
		
		 
		]
	});
	NS.mapeo.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
});