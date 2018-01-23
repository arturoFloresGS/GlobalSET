//GGonzález

Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Financiamiento.ProvisionInteresC');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var vbAceptar;
	NS.fecHoy = apps.SET.FEC_HOY;
	stringToDate=function(fecha){
		var fec = fecha.split("/");
		var dt = new Date(parseInt(fec[2], 10),
				parseInt(fec[1], 10) - 1, parseInt(fec[0], 10));
		return dt;
	}
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash : false,
		baseParams:{
			piNoUsuario:0,
			pbMismaEmpresa:false,
			plEmpresa:0
		},
		paramOrder : ['piNoUsuario','pbMismaEmpresa','plEmpresa'],
		root : '',
		directFn : AvalesGarantiasFCAction.llenarCmbEmpresa,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var recordsStoreEmpresa = NS.storeCmbEmpresa.recordType;	
				var todos = new recordsStoreEmpresa({
					id: '0',
					descripcion: '****TODAS****'
				});
				NS.storeCmbEmpresa.insert(0,todos);
				NS.cmbEmpresa.setValue('0');
				NS.cmbEmpresa.setRawValue('****TODAS****');

			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbEmpresa,
		msg : "Cargando..."
	});
	NS.storeCmbEmpresa.baseParams.piNoUsuario=parseInt(NS.idUsuario);
	NS.storeCmbEmpresa.load();
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa,
		name: PF + 'cmbEmpresa',
		id: PF + 'cmbEmpresa',
		emptyText: 'Seleccione una empresa',
		value:'****TODAS****',
		triggerAction: 'all',
		x: 50,
		y: 20,
		width: 300,
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
					if(NS.cmbEmpresa.getValue()==''){
						NS.txtEmpresa.setValue(0);
						NS.cmbEmpresa.setValue(0);
						NS.cmbEmpresa.setRawValue('****TODAS****');
					}
				}
			}
		}
	}); 
	var funciones = '[{"id": "1", "descripcion": "Generación de la Provisión Mensual"}, {"id": "2", "descripcion": "Provisiones Acumuladas al Día de Hoy"}, {"id": "3", "descripcion": "Provisiones del Mes al Día de Hoy"}, {"id": "4", "descripcion": "Provisiones Históricas"}]';
	NS.storeFunciones = new Ext.data.JsonStore({
		fields: [
		         {type: 'int', name: 'id'},
		         {type: 'string', name: 'descripcion'}
		         ]
	});
	NS.storeFunciones.loadData(Ext.decode(funciones));
	NS.cmbFunciones = new Ext.form.ComboBox({
		store: NS.storeFunciones,
		name: PF + 'cmbFunciones',
		id: PF + 'cmbFunciones',
		x: 1,
		y: 80,
		width: 350,
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
		emptyText : 'Seleccione una función',
		triggerAction : 'all',
		listeners:{
			select:{
				fn:function(combo, valor){
					Ext.getCmp(PF +'txtTotalProvision').setValue("0.00");
					Ext.getCmp(PF +'txtDivisa').setValue("0");
					Ext.getCmp(PF +'cmbDivisa').reset();
					Ext.getCmp(PF +'txtDivisa').setValue("0");
					if(NS.cmbFunciones.getValue()==4||NS.cmbFunciones.getRawValue()=='"Provisiones Acumuladas Futuras'){
						Ext.getCmp(PF +'cmbMes').setDisabled(false);
						Ext.getCmp(PF +'txtAnio').setDisabled(false);
					}else{
						Ext.getCmp(PF +'cmbMes').reset();
						Ext.getCmp(PF +'cmbMes').setDisabled(true);
						Ext.getCmp(PF +'txtAnio').setDisabled(true);
						Ext.getCmp(PF +'txtAnio').setValue("");
					}
				}
			}
		}
	}); 
	var meses = '[{"id": "1", "descripcion": "ENERO"}, {"id": "2", "descripcion": "FEBRERO"}, {"id": "3", "descripcion": "MARZO"}, {"id": "4", "descripcion": "ABRIL"}, {"id": "5", "descripcion": "MAYO"}, {"id": "6", "descripcion": "JUNIO"}, {"id": "7", "descripcion": "JULIO"}, {"id": "8", "descripcion": "AGOSTO"}, {"id": "9", "descripcion": "SEPTIEMBRE"}, {"id": "10", "descripcion": "OCTUBRE"}, {"id": "11", "descripcion": "NOVIEMBRE"}, {"id": "12", "descripcion": "DICIEMBRE"}]';
	NS.storeMeses = new Ext.data.JsonStore({
		fields: [
		         {type: 'int', name: 'id'},
		         {type: 'string', name: 'descripcion'}
		         ]
	});
	NS.storeMeses.loadData(Ext.decode(meses));
	NS.cmbMes= new Ext.form.ComboBox({
		store: NS.storeMeses,
		name: PF + 'cmbMes',
		id: PF + 'cmbMes',
		x: 400,
		disabled:true,
		emptyText : 'Seleccione un mes',
		triggerAction : 'all',
		y: 80,
		width: 190,
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
	}); 
	NS.storeCmbDivisa = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			bRestringido : ''
		},
		paramOrder : [ 'bRestringido' ],
		directFn : AltaFinanciamientoAction.obtenerDivisas,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var recordsStoreDivisa = NS.storeCmbDivisa.recordType;	
				var todos = new recordsStoreDivisa({
					id: '0',
					descripcion: '****TODAS****'
				});
				NS.storeCmbDivisa.insert(0,todos);
				Ext.getCmp(PF + 'cmbDivisa').setValue('0');
				NS.cmbDivisa.setRawValue('****TODAS****');
			}
		}
	});
	NS.storeCmbDivisa.baseParams.bRestringido = false;
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbDivisa,
		msg : "Cargando..."
	});
	NS.storeCmbDivisa.load();
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeCmbDivisa,
		name: PF + 'cmbDivisa',
		id: PF + 'cmbDivisa',
		x: 810,
		y: 80,
		width: 180,
		displayField: 'descripcion',
		valueField: 'idStr',
		mode: 'local',
		emptyText : 'Seleccione una divisa',
		triggerAction : 'all',
		value : '****TODAS****',
		listeners:{
			select:{
				fn:function(combo, valor){
					BFwrk.Util.updateComboToTextField(PF + 'txtDivisa',
							NS.cmbDivisa.getId());
					if(NS.cmbDivisa.getValue()==''){
						Ext.getCmp(PF + 'txtDivisa').setValue('0');
						NS.cmbDivisa.setValue(0);
						NS.cmbDivisa.setRawValue('****TODAS****');
					}
				}
			}
		}
	}); 
	validarDatos=function(){
		var band=false;
		if(Ext.getCmp(PF + 'txtEmpresa').getValue() == ""||Ext.getCmp(PF + 'txtEmpresa').getValue() ==0){
			Ext.Msg.alert("SET","Seleccione una Empresa");
			Ext.getCmp(PF + 'txtEmpresa').focus();
			return;
		}
		else if (Ext.getCmp(PF + 'cmbEmpresa').getValue() == "") {
			Ext.Msg.alert("SET","Seleccione una línea.");
			Ext.getCmp(PF + 'cmbEmpresa').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'cmbTipo').getValue() == ""){
			Ext.Msg.alert("SET","Seleccione un Tipo de Registro");
			Ext.getCmp(PF + 'cmbTipo').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'txtClave').getValue() == ""){
			Ext.Msg.alert("SET","Capture una Clave");
			Ext.getCmp(PF + 'txtClave').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'txtDescripcion').getValue() == ""){
			Ext.Msg.alert("SET","Capture la descripción");
			Ext.getCmp(PF + 'txtDescripcion').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'txtValor').getValue() == ""){
			Ext.Msg.alert("SET","Capture el importe");
			Ext.getCmp(PF + 'txtValor').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'txtIdDivisa').getValue() == ""){
			Ext.Msg.alert("SET","Capture una divisa");
			Ext.getCmp(PF + 'txtIdDivisa').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'cmbDivisa').getValue() == ""){
			Ext.Msg.alert("SET","Seleccione una divisa");
			Ext.getCmp(PF + 'cmbDivisa').focus();
			return;
		}
		band=true;
		return band;
	}
	historicaFutura=function(vsTipoFuncion){
		var vsFecha,vsFechaIni,mes,anio;
		if(Ext.getCmp(PF + 'cmbMes').getValue() == ""||Ext.getCmp(PF + 'txtAnio').getValue() == ""){
			Ext.Msg.alert("SET","Seleccione el mes y año");
			return;
		}
		vsFecha = new Date(parseInt(Ext.getCmp(PF + 'txtAnio').getValue(), 10), parseInt(
				Ext.getCmp(PF + 'cmbMes').getValue(), 10) - 1, parseInt(01, 10));
		vsFecha.setMonth(vsFecha.getMonth() + 1);
		vsFecha.setDate(vsFecha.getDate() -1 );
		mes=vsFecha.getMonth()+1;
		if(mes<10)
			mes="0"+mes;
		vsFechaIni='01/'+mes+"/"+vsFecha.getFullYear();
		vsFecha=BFwrk.Util.changeDateToString(''+ vsFecha);
		NS.storeProvisiones.baseParams.psFecha=vsFecha;
		NS.storeProvisiones.baseParams.plEmpresa=parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		NS.storeProvisiones.baseParams.psFechaIni=vsFechaIni;
		NS.storeProvisiones.baseParams.psTipoFuncion=vsTipoFuncion;
		NS.storeProvisiones.baseParams.psDivisa=Ext.getCmp(PF + 'txtDivisa').getValue();
		NS.storeProvisiones.baseParams.tipo=4;
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeProvisiones,
			msg : "Cargando..."
		});
		NS.storeProvisiones.load();
	}
	acumuladoHoy=function(vsTipoFuncion){
		var vsFecha,vsFechaIni,mes,anio,fechaHoy;
		fechaHoy=stringToDate(NS.fecHoy);
		mes=fechaHoy.getMonth()+1;
		anio=fechaHoy.getFullYear();
		vsFecha =stringToDate("01/"+mes+"/"+anio);
		vsFecha.setMonth(vsFecha.getMonth() + 1);
		vsFecha.setDate(vsFecha.getDate() -1 );
		vsFecha=BFwrk.Util.changeDateToString(''+ vsFecha);
		if(mes<10)
			mes="0"+mes;
		vsFechaIni="01/"+mes+"/"+anio;
		NS.storeProvisiones.baseParams.psFecha=vsFecha;
		NS.storeProvisiones.baseParams.plEmpresa=parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		NS.storeProvisiones.baseParams.psFechaIni=vsFechaIni;
		NS.storeProvisiones.baseParams.psTipoFuncion=vsTipoFuncion;
		NS.storeProvisiones.baseParams.psDivisa=Ext.getCmp(PF + 'txtDivisa').getValue();
		NS.storeProvisiones.baseParams.tipo=2;
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeProvisiones,
			msg : "Cargando..."
		});
		NS.storeProvisiones.load();
	}
	mesHoy=function(){
		var vsFecha,vsFechaIni,mes,anio,fechaHoy;
		fechaHoy=stringToDate(NS.fecHoy);
		mes=fechaHoy.getMonth()+1;
		anio=fechaHoy.getFullYear();
		vsFecha =stringToDate("01/"+mes+"/"+anio);
		vsFecha.setMonth(vsFecha.getMonth() + 1);
		vsFecha.setDate(vsFecha.getDate() -1 );
		vsFecha=BFwrk.Util.changeDateToString(''+ vsFecha);
		if(mes<10)
			mes="0"+mes;
		vsFechaIni="01/"+mes+"/"+anio;
		vsFecIniOri = vsFechaIni;
		NS.storeProvisiones.baseParams.psFecha=vsFecha;
		NS.storeProvisiones.baseParams.plEmpresa=parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		NS.storeProvisiones.baseParams.psFechaIni=vsFechaIni;
		NS.storeProvisiones.baseParams.psDivisa=Ext.getCmp(PF + 'txtDivisa').getValue();
		NS.storeProvisiones.baseParams.tipo=3;
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeProvisiones,
			msg : "Cargando..."
		});
		NS.storeProvisiones.load();
	}
	generaProvision=function(){
		var vsFecha,vsFechaIni,mes,anio,fechaHoy;
		fechaHoy=stringToDate(NS.fecHoy);
		mes=fechaHoy.getMonth()+1;
		anio=fechaHoy.getFullYear();
		vsFecha =stringToDate("01/"+mes+"/"+anio);
		vsFecha.setMonth(vsFecha.getMonth() + 1);
		vsFecha.setDate(vsFecha.getDate() -1 );
		vsFecha=BFwrk.Util.changeDateToString(''+ vsFecha);
		NS.storeProvisiones.baseParams.psFecha=vsFecha;
		NS.storeProvisiones.baseParams.plEmpresa=parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		NS.storeProvisiones.baseParams.psDivisa=Ext.getCmp(PF + 'txtDivisa').getValue();
		NS.storeProvisiones.baseParams.tipo=1;
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeProvisiones,
			msg : "Cargando..."
		});
		NS.storeProvisiones.load();
	}
	NS.buscar=function(){
		Ext.getCmp(PF +'btnAceptar').setDisabled(true);
		vbAceptar = false;
		//1 = "Generación de la Provisión Mensual"
		//2 = "Provisiones Acumuladas al Día de Hoy"
		//3 = "Provisiones del Mes al Día de Hoy"
		//4 = "Provisiones Historicas"
		if(Ext.getCmp(PF + 'cmbFunciones').getValue() == ""){
			Ext.Msg.alert("SET","Seleccione una opción de Funciones");
			Ext.getCmp(PF + 'cmbFunciones').focus();
			return;
		}
		if(Ext.getCmp(PF + 'cmbDivisa').getValue() == ""&&Ext.getCmp(PF + 'cmbDivisa').getRawValue() != "****TODAS****"||Ext.getCmp(PF + 'txtDivisa').getValue() == ""){
			Ext.getCmp(PF + 'cmbDivisa').reset();
			Ext.getCmp(PF + 'txtDivisa').setValue('0');
			Ext.Msg.alert("SET","Seleccione una Divisa.");
			Ext.getCmp(PF + 'cmbDivisa').focus();
			return;
		}
		if(Ext.getCmp(PF + 'cmbFunciones').getValue() == 4)
			historicaFutura("H");
		else if(Ext.getCmp(PF + 'cmbFunciones').getValue() == 2)
			acumuladoHoy("F");
		else if(Ext.getCmp(PF + 'cmbFunciones').getValue() == 3)
			mesHoy();
		else if(Ext.getCmp(PF + 'cmbFunciones').getValue() == 1){
			generaProvision();
		}         
	}
	diaHabilReg=function(vsFecMes){
		var diaHabil,fechaHabil;
		var diaSemana=vsFecMes.getDay();
		var plWeekend=0;
		if(diaSemana==6)
			plWeekend = -1;
		if(diaSemana==0)
			plWeekend = -2;
		vsFecMes=BFwrk.Util.changeDateToString(''+ vsFecMes);
		ProvisionInteresesCAction.diaHabilReg(vsFecMes,function(mapResult, e) {
			BFwrk.Util.msgWait('Terminado...', false);
			if (mapResult.fecha !== null&& mapResult.fecha !== ''&& mapResult.fecha != undefined) {
				fechaHabil=mapResult.fecha;
			}
			if(stringToDate(fechaHabil).getTime()==stringToDate(NS.fecHoy).getTime()){
				NS.btnAceptar.setDisabled(false);
			}
		});
	}
	NS.limpiar=function(){
		Ext.getCmp(PF + 'txtEmpresa').setValue(0);
		Ext.getCmp(PF + 'cmbEmpresa').reset();
		Ext.getCmp(PF + 'cmbFunciones').reset();
		Ext.getCmp(PF + 'cmbMes').reset();
		Ext.getCmp(PF + 'txtAnio').setValue("");
		NS.storeProvisiones.removeAll();
		Ext.getCmp(PF + 'txtTotalProvision').setValue("0.00");
		Ext.getCmp(PF + 'txtTotalReg').setValue("0");
		Ext.getCmp(PF + 'txtDivisa').setValue("0")
		Ext.getCmp(PF + 'cmbDivisa').reset();
	}
	obtenerDatosGrid=function(){
		var provisiones = NS.gridProvisiones
		.getSelectionModel()
		.getSelections();
		if(provisiones.length<=0){
			Ext.Msg.alert("SET","Seleccione uno o más registros.")
			return;
		}else{
			var matrizProvisiones = new Array();
			for (var i = 0; i < provisiones.length; i++) {
				var registrosProvisiones = {};
				registrosProvisiones.idFinanciamiento = provisiones[i]
				.get('idFinanciamiento');
				registrosProvisiones.idDisposicion = provisiones[i]
				.get('idDisposicion');
				registrosProvisiones.fecInicial = provisiones[i]
				.get('fecInicial');
				registrosProvisiones.fecFinal = provisiones[i]
				.get('fecFinal');
				matrizProvisiones[i] = registrosProvisiones;
			}
			var jsonStringProvisiones = Ext.util.JSON
			.encode(matrizProvisiones);
			return jsonStringProvisiones;

		}
	}
	NS.eliminar=function(){
		var hoy=stringToDate(NS.fecHoy);
		var pdFechaVal;
		json=obtenerDatosGrid();
		if(json!==undefined){
			if(hoy.getDate()<10)
				pdFechaVal=BFwrk.Util.changeDateToString(''+ hoy.setDate(hoy.getDate()-11));
			else
				pdFechaVal=NS.fecHoy;
			ProvisionInteresesCAction
			.updateProvisionX(json,pdFechaVal,
					function(mapResult, e) {
				BFwrk.Util.msgWait('Terminado...', false);
				if (mapResult.msgUsuario !== null
						&& mapResult.msgUsuario !== ''
							&& mapResult.msgUsuario != undefined) {
					for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
						Ext.Msg.alert('Información SET', ''+ mapResult.msgUsuario[msg], function(btn, text){
							if (btn == 'ok'){
								NS.buscar();
							}
						});
					}
				}
			});
		}
	}
	NS.aceptar=function(){
		json=obtenerDatosGrid();
		if(json!==undefined){
			ProvisionInteresesCAction
			.updateProvisionEstatus(json,
					function(mapResult, e) {
				BFwrk.Util.msgWait('Terminado...', false);
				if (mapResult.msgUsuario !== null
						&& mapResult.msgUsuario !== ''
							&& mapResult.msgUsuario != undefined) {
					for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
						Ext.Msg.alert('Información SET', ''+ mapResult.msgUsuario[msg], function(btn, text){
							if (btn == 'ok'){
								NS.buscar();
							}
						});
					}
				}

			});
		}
	}
	/*Grid inicio*/
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	NS.columnasProvi = new Ext.grid.ColumnModel([
	                                             NS.columnaSeleccion,
	                                             {header: '', width: 0, dataIndex: 'noEmpresa', sortable: true,hidden : true,
	                                            	 hideable : false,},
	                                            	 {header: 'Empresa', width: 250, dataIndex: 'descEmpresa', sortable: true},
	                                            	 {header: '', width: 0, dataIndex: 'idBanco', sortable: true,hidden : true,
	                                            		 hideable : false,},
	                                            		 {header: 'Banco Acreedor', width: 200, dataIndex: 'descBanco', sortable: true},
	                                            		 {header: 'Financiamiento', width:120 , dataIndex: 'idFinanciamiento', sortable: true},
	                                            		 {header: 'Disposición', width: 120, dataIndex: 'idDisposicion', sortable: true},
	                                            		 {header: '0', width:0 , dataIndex: 'iDivisa', sortable: true, hidden : true,
	                                            			 hideable : false,},
	                                            			 {header: 'Divisa', width:120 , dataIndex: 'descDivisa', sortable: true},
	                                            			 {header: 'Fecha inicial', width: 120, dataIndex: 'fecInicial', sortable: true},
	                                            			 {header: 'Fecha Final', width:120 , dataIndex: 'fecFinal', sortable: true},
	                                            			 {header: 'Días', width: 120, dataIndex: 'dias', sortable: true},
	                                            			 {header: 'Monto Capital', width:120, dataIndex: 'capital', sortable: true,
	                                            				 renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00')}},
	                                            				 {header: 'Monto Provisión', width: 120, dataIndex: 'montoProvision', sortable: true,
	                                            					 renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00')}},
	                                            					 {header: 'Valor Tasa', width:120, dataIndex: 'valorTasa', sortable: true,
	                                            						 renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00000')}},			 

	                                            						 ]);
	NS.storeProvisiones = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			psFecha:'',plEmpresa:0,psFechaIni:'',psTipoFuncion:'',psDivisa:'',tipo:0
		},
		root: '',
		paramOrder: ['psFecha','plEmpresa','psFechaIni','psTipoFuncion','psDivisa','tipo'],
		directFn: ProvisionInteresesCAction.llenarGridProvisiones, 
		fields: [
		         {name: 'noEmpresa'},
		         {name: 'descEmpresa'},
		         {name: 'idBanco'},
		         {name: 'descBanco'},
		         {name: 'idFinanciamiento'},
		         {name: 'idDisposicion'},
		         {name: 'iDivisa'},
		         {name: 'descDivisa'},
		         {name: 'fecInicial'},
		         {name: 'fecFinal'},
		         {name: 'dias'},
		         {name: 'capital'},
		         {name: 'montoProvision'},
		         {name: 'valorTasa'},
		         {name: 'valor'}
		         ],
		         listeners: {
		        	 load: function(s, records) {
		        		 var vdTotProvision=0;
		        		 var vsFecMes,mes,anio,fechaHoy;
		        		 if(records.length == null || records.length <= 0)
		        			 Ext.Msg.alert('SET', 'No existen registros con esos parámetros.');

		        		 else{

		        			 for(var i=0;i<records.length;i++){
		        				 if(Ext.getCmp(PF +'txtDivisa').getValue()!="MN"){
		        					 vdTotProvision = vdTotProvision+parseFloat(records[i].get('montoProvision'));
		        				 }
		        				 else{
		        					 if(records[i].get('iDivisa'!="MN"))
		        						 vdTotProvision+=parseFloat(records[i].get('montoProvision')) * parseFloat(records[i].get('valor'));
		        					 else
		        						 vdTotProvision+= parseFloat(records[i].get('montoProvision'));
		        				 }
		        			 }
		        		 }
		        		 Ext.getCmp(PF + 'txtTotalProvision').setValue(BFwrk.Util.formatNumber(vdTotProvision.toFixed(2)));
		        		 Ext.getCmp(PF + 'txtTotalReg').setValue(records.length);
		        		 if(!vbAceptar){
		        			 fechaHoy=stringToDate(NS.fecHoy);
		        			 mes=fechaHoy.getMonth()+1;
		        			 anio=fechaHoy.getFullYear();
		        			 vsFecMes =stringToDate("01/"+mes+"/"+anio);
		        			 vsFecMes.setMonth(vsFecMes.getMonth() + 1);
		        			 vsFecMes.setDate(vsFecMes.getDate() -1 );
		        			 diaHabilReg(vsFecMes);
		        		 }
		        	 }
		         },
		         exception:function(misc) {
		        	 Ext.Msg.alert('SET', 'Error al cargar los datos en el grid, <br> Verificar la conexión');
		         }
	});
	NS.gridProvisiones = new Ext.grid.GridPanel({
		store: NS.storeProvisiones,
		id: PF+'gridProvisiones',
		cm: NS.columnasProvi,
		autowidth: true,
		frame: true,
		height: 180,
		x: 0,
		y: 00,
		stripeRows: true,
		columnLines: true,
		cm: NS.columnasProvi,
		sm: NS.columnaSeleccion,
	});
	/*Grid final*/
	NS.provisionesC= new Ext.form.FormPanel({
		title: 'Provisiones de interes C',
		autowidth: true,
		height: 900,
		padding: 10,
		layout: 'absolute',
		id: PF + 'provisionesC',
		name: PF + 'provisionesC',
		renderTo: NS.tabContId,
		frame: true,
		autoScroll: true,
		items : [
		         { 
		        	 xtype: 'fieldset',
		        	 title: 'Búsqueda',
		        	 autowidth: true,
		        	 height: 140,
		        	 x: 0,
		        	 y: 0,
		        	 layout: 'absolute',
		        	 id: 'fSetPrincipal',
		        	 items: [
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Empresa:',
		        	        	 x: 1,
		        	        	 y: 0
		        	         },{
		        	        	 xtype: 'numberfield',
		        	        	 x: 1,
		        	        	 y: 20,
		        	        	 width: 45,
		        	        	 value:0,
		        	        	 name: PF + 'txtEmpresa',
		        	        	 id: PF + 'txtEmpresa',
		        	        	 listeners:{
		        	        		 change: {
		        	        			 fn: function(box, value) {
		        	        				 var value = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
		        	        				 if(Ext.getCmp(PF +'txtEmpresa').getValue()==''){
		        	        					 Ext.Msg.alert("SET","Empresa inexistente.");
		        	        					 Ext.getCmp(PF +'txtEmpresa').setValue(0);
		        	        					 NS.cmbEmpresa.setValue(0);
		        	        					 NS.cmbEmpresa.setRawValue('****TODAS****');
		        	        				 }
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         NS.cmbEmpresa,
		        	         { 
		        	        	 xtype: 'label',
		        	        	 text: 'Funciones:',
		        	        	 x: 1,
		        	        	 y: 60
		        	         },
		        	         NS.cmbFunciones,
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Buscar',
		        	        	 x: 870,
		        	        	 y: 15,
		        	        	 width: 90,
		        	        	 id: PF + 'btnBuscar',
		        	        	 name: PF + 'btnBuscar',
		        	        	 disabled: false,
		        	        	 listeners:{
		        	        		 click:{
		        	        			 fn: function(btn){
		        	        				 NS.buscar();
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         { 
		        	        	 xtype: 'label',
		        	        	 text: 'Mes:',
		        	        	 x: 400,
		        	        	 y: 60
		        	         },
		        	         NS.cmbMes,
		        	         { 
		        	        	 xtype: 'label',
		        	        	 text: 'Año:',
		        	        	 x:620,
		        	        	 y: 60
		        	         },
		        	         {
		        	        	 xtype: 'numberfield',
		        	        	 x: 620,
		        	        	 y: 80,
		        	        	 disabled:true,
		        	        	 width: 100,
		        	        	 name: PF + 'txtAnio',
		        	        	 id: PF + 'txtAnio',
		        	         },
		        	         { 
		        	        	 xtype: 'label',
		        	        	 text: 'Divisa:',
		        	        	 x: 760,
		        	        	 y: 60
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 760,
		        	        	 y: 80,
		        	        	 width: 45,
		        	        	 value:0,
		        	        	 name: PF + 'txtDivisa',
		        	        	 id: PF + 'txtDivisa',
		        	        	 listeners:{
		        	        		 change:{
		        	        			 fn: function(box, value){
		        	        				 var comboValue = BFwrk.Util.updateTextFieldToCombo(
		        	        						 PF + 'txtDivisa', NS.cmbDivisa.getId());
		        	        				 if(Ext.getCmp(PF +'txtDivisa').getValue()==''){
		        	        					 Ext.Msg.alert("SET","Divisa inexistente.");
		        	        					 Ext.getCmp(PF +'txtDivisa').setValue(0);
		        	        					 NS.cmbDivisa.setValue(0);
		        	        					 NS.cmbDivisa.setRawValue('****TODAS****');
		        	        				 }
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         NS.cmbDivisa
		        	         ]
		         },
		         { 
		        	 xtype: 'fieldset',
		        	 title: 'Disposiciones',
		        	 autowidth: true,
		        	 height: 250,
		        	 x: 0,
		        	 y:150,
		        	 layout: 'absolute',
		        	 id: 'fSetSec',
		        	 items: [

		        	         NS.gridProvisiones,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Total registros:',
		        	        	 x: 60,
		        	        	 y: 190
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 150,
		        	        	 disabled:true,
		        	        	 y: 190,
		        	        	 value:"0",
		        	        	 width: 120,
		        	        	 name: PF + 'txtTotalReg',
		        	        	 id: PF + 'txtTotalReg',
		        	         },
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Total Provisión:',
		        	        	 x: 750,
		        	        	 y: 190
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 840,
		        	        	 y: 190,
		        	        	 disabled:true,
		        	        	 width: 120,
		        	        	 name: PF + 'txtTotalProvision',
		        	        	 id: PF + 'txtTotalProvision',

		        	         }
		        	         ]
		         },
		         {
		        	 xtype: 'button',
		        	 text: 'Aceptar',
		        	 x: 700,
		        	 y: 420,
		        	 width: 90,
		        	 id: PF + 'btnAceptar',
		        	 name: PF + 'btnAceptar',
		        	 disabled: true,
		        	 listeners: {
		        		 click:{
		        			 fn: function(btn) {
		        				 NS.aceptar();
		        			 }
		        		 }
		        	 }
		         },
		         {
		        	 xtype: 'button',
		        	 text: 'Eliminar',
		        	 x: 800,
		        	 y: 420,
		        	 width: 90,
		        	 id: PF + 'btnEliminar',
		        	 name: PF + 'btnEliminar',
		        	 disabled: false,
		        	 listeners:{
		        		 click:{
		        			 fn: function(btn) {
		        				 NS.eliminar();
		        			 }
		        		 }
		        	 }
		         },
		         {
		        	 xtype: 'button',
		        	 text: 'Limpiar',
		        	 x: 900,
		        	 y: 420,
		        	 width: 90,
		        	 id: PF + 'btnLimpiar',
		        	 name: PF + 'btnLimpiar',
		        	 disabled: false,
		        	 listeners:{
		        		 click:{
		        			 fn: function(btn) {
		        				 NS.limpiar();
		        			 }
		        		 }
		        	 }
		         } ,
		         ]
	});
	NS.provisionesC.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});