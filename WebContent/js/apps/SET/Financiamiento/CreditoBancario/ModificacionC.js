/*
 * angel israel jacinto rodriguez
 * ggonzalez
 * */ 

Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Financiamiento.ModificacionC');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	NS.fecHoy = apps.SET.FEC_HOY;
	var vsTipoMenu='';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.inicializarVariables = function(){
		NS.bOptTipoMovto = true;

		NS.idFin=''; 
		NS.idDisp=0;
	};
	stringToDate=function(fecha){
		var fec = fecha.split("/");
		var dt = new Date(parseInt(fec[2], 10),
				parseInt(fec[1], 10) - 1, parseInt(fec[0], 10));
		return dt;
	}
	NS.inicializarVariables();
	NS.storeConfiguraSetTodos = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		directFn : AltaFinanciamientoAction.consultarConfiguraSetTodos,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'valorConfiguraSet'
		} ],
	});
	NS.storeConfiguraSetTodos.load({
		callback : function(records) {
			NS.proyecto = records[0].get('valorConfiguraSet');
		}
	});
	NS.chkCapital = new Ext.form.Checkbox({
		name : PF + 'chkCapital',
		id : PF+'chkCapital',
		x : 850,
		y : 20,
		hidden:true,
		boxLabel: 'Capital'
	});
	NS.habilitar=function(pbEditar){
		Ext.getCmp(PF+'cmbLinea').setDisabled(!pbEditar);
		Ext.getCmp(PF+'cmbDispo').setDisabled(!pbEditar);
		Ext.getCmp(PF+'btnBuscar').setDisabled(!pbEditar);
		Ext.getCmp(PF+'btnMod').setDisabled(!pbEditar);
		Ext.getCmp(PF+'btnAceptar').setDisabled(pbEditar);
		Ext.getCmp(PF+'btnCancelar1').setDisabled(pbEditar);
		Ext.getCmp(PF+'txtFecCor').setDisabled(pbEditar);
		Ext.getCmp(PF+'txtValTasa').setDisabled(pbEditar);
		Ext.getCmp(PF+'txtIva').setDisabled(pbEditar);
		Ext.getCmp(PF+'optTasa').setDisabled(pbEditar);
		Ext.getCmp(PF+'txtRenta').setDisabled(pbEditar);
		Ext.getCmp(PF+'txtTasaFij').setDisabled(pbEditar);
		if (vsTipoMenu == "A"){
			if(NS.proyecto== "CIE"){
				Ext.getCmp(PF+'txtValTasa').setDisabled(true);
				Ext.getCmp(PF+'cmbTasaBase').setDisabled(true);
				Ext.getCmp(PF+'txtPuntos').setDisabled(true);
				Ext.getCmp(PF+'txtTasaVig').setDisabled(true);
				Ext.getCmp(PF+'txtTasaFij').setDisabled(true);
			}
		}
	}
	NS.lblEmpresa = new Ext.form.Label({
		text : 'Empresa:',
		x : 0,
		y : 0
	});
	NS.txtEmpresa = new Ext.form.TextField({
		id : PF + 'txtEmpresa',
		name : PF + 'txtEmpresa',
		value : NS.GI_ID_EMPRESA,
		x : 0,
		y : 20,
		width : 70,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+ 'txtEmpresa',NS.cmbEmpresa.getId());
				}
			}
		}
	});
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			idUsuario : NS.idUsuario,
			mantenimiento : true
		},
		paramOrder : [ 'idUsuario', 'mantenimiento' ],
		directFn : FinanciamientoModificacionCAction.obtenerEmpresas,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCmbEmpresa, msg:"Cargando..."});
				if(records.length === null || records.length <= 0){
					Ext.Msg.alert('SET','No tiene empresas asignadas.');
					return;
				}
			}
		}
	});
	NS.storeCmbEmpresa.load();
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store : NS.storeCmbEmpresa,
		name : PF + 'cmbEmpresa',
		id : PF + 'cmbEmpresa',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 80,
		y : 20,
		width : 320,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una empresa',
		triggerAction : 'all',
		value :  NS.GI_NOM_EMPRESA,
		visible : false,
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+ 'txtEmpresa', NS.cmbEmpresa.getId());
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storeCmbContratos,
						msg : "Cargando..."
					});
					NS.limpiar();
					NS.storeCmbContratos.removeAll();
					NS.cmbLinea.reset();
					NS.storeDisposiciones.removeAll();
					NS.cmbDispo.reset();
					NS.storeCmbContratos.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
					NS.storeCmbContratos.load();
				}
			}
		}
	});

	limpia=function(){
		Ext.getCmp(PF+'optTasa').reset();
		Ext.getCmp(PF+'txtFecCor').reset();
		Ext.getCmp(PF+'txtValTasa').setValue("0.00000");
		Ext.getCmp(PF+'txtPuntos').setValue("0.00000");
		Ext.getCmp(PF+'txtTasaVig').setValue("0.00000");
		Ext.getCmp(PF+'txtTasaFij').setValue("0.00000");
		Ext.getCmp(PF+'txtRenta').setValue("0.00");
		Ext.getCmp(PF+'txtIva').setValue("0.00000");
		NS.storeSelectAmortizaciones.removeAll();
		NS.storeTasa.removeAll();
		Ext.getCmp(PF+'cmbTasaBase').reset();
	}
	NS.limpiar=function(){
		NS.habilitar(true);
		limpia();
		Ext.getCmp(PF+'btnMod').setDisabled(true);
		NS.storeSelectAmortizaciones.removeAll();
		Ext.getCmp(PF+'txtTotalReg').setValue("0");
		NS.storeDisposiciones.removeAll();
		Ext.getCmp(PF+'cmbDispo').reset();
		Ext.getCmp(PF+'cmbLinea').reset();	
	}
	NS.txtRenta = new Ext.form.TextField({
		id: PF+'txtRenta',
		name: PF+'txtRenta',
		x: 430,
		y: 20,
		hidden:true,
		disabled :true, 
		width: 150,
		value:'0',
		listeners : {
			change : function(textfield) {
				if(textfield.getValue()=="")
					textfield.setValue("0.00");
				textfield.setValue(BFwrk.Util.formatNumber(textfield.getValue()));
			}
		}
	});
	NS.txtFecCor = new Ext.form.DateField({
		id: PF+'txtFecCor',
		name: PF+'txtFecCor',
		format: 'd/m/Y',
		x: 250,
		y: 20,
		disabled :true, 
		width: 150,
		listeners : {
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			change : function(datefield) {
				var pdFecIni,pdFecIni,pdFecFin,sFecVenSig;
				var registro = NS.gridMod.getSelectionModel().getSelections();
				for(var i=0;i<registro.length;i++){
					pdFecIni=stringToDate(registro[i].get('fecInicio'));
					pdFecInt = datefield.getValue();
					pdFecFin=stringToDate(registro[i].get('fecVencimiento'));
					if((i+1)<NS.storeSelectAmortizaciones.getTotalCount())
						sFecVenSig=stringToDate(NS.gridMod.store.getAt((i+1)).data.fecVencimiento);
					else
						sFecVenSig=stringToDate(registro[i].get('fecVencimiento'));
					if(datefield.getValue()==""||datefield.getValue()==null){
						Ext.Msg.alert("SET","Digite una fecha correcta");
						datefield.setValue(pdFecFin);
						return;
					}
					if((pdFecInt<=pdFecIni)||(pdFecInt>=sFecVenSig)&&((i+1)<NS.storeSelectAmortizaciones.getTotalCount())){	
						Ext.Msg.alert("SET","La fecha debe ser mayor al "+
								BFwrk.Util.changeDateToString(''+pdFecIni)+" y menor al "+
								BFwrk.Util.changeDateToString(''+sFecVenSig)+" .La fecha se regresará a la original.");
						datefield.setValue(pdFecFin);}
				}	
			}
		}
	});
	NS.storeCmbContratos = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {noEmpresa:''},
		paramOrder : ['noEmpresa'],
		directFn : FinanciamientoModificacionCAction.obtenerContratos,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCmbContratos, msg:"Cargando..."});
				if(records.length === null || records.length <= 0){
					Ext.Msg.alert('SET','No hay contratos registrados.');
					return;
				}
			}
		}
	});
	NS.storeCmbContratos.baseParams.noEmpresa=parseInt(NS.GI_ID_EMPRESA);
	NS.storeCmbContratos.load();
	NS.cmbLinea = new Ext.form.ComboBox({
		store:NS.storeCmbContratos,
		name: PF + 'cmbLinea',
		id: PF + 'cmbLinea',	                                          
		x: 440,
		y: 20,
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		width: 150,
		emptyText: 'Seleccione línea',
		valueField:'idStr',
		displayField:'descripcion',
		mode: 'local',
		autocomplete: true,
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor) {  
					NS.cmbDispo.reset();
					NS.storeDisposiciones.removeAll();
					NS.storeDisposiciones.baseParams.psIdContrato = NS.cmbLinea
					.getValue();
					var myMask = new Ext.LoadMask(
							Ext.getBody(), {
								store : NS.storeDisposiciones,
								msg : "Cargando..."
							});
					NS.storeDisposiciones.load();
				}
			}
		}
	}); 

	NS.storeDisposiciones = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psIdContrato : '',
			pbEstatus : false
		},
		paramOrder : [ 'psIdContrato', 'pbEstatus' ],
		directFn : FinanciamientoModificacionCAction.obtenerDisposiciones,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDisposiciones, msg:"Cargando..."});
				if(records.length === null || records.length <= 0){
					Ext.Msg.alert('SET','No hay disposiciones registradas.');
					return;
				}
			}
		}
	});
	NS.cmbDispo= new Ext.form.ComboBox({
		store:NS.storeDisposiciones,
		name: PF + 'cmbDispo',
		id: PF + 'cmbDispo',	                                          
		x: 640,
		y: 20,
		whidth:150,
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		width: 150,
		emptyText: 'Seleccione la disposición',
		valueField:'id',
		displayField:'descripcion',
		mode: 'local',
		autocomplete: true,
		triggerAction: 'all',
		value: '',
	});

	NS.storeTasa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: FinanciamientoModificacionCAction.llenarCmbTasa, 
		idProperty: 'idStr',
		fields: [
		         {name: 'idStr'},
		         {name: 'descripcion'}
		         ],
		         listeners: {
		        	 load: function(s, records){
		        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTasa, msg:"Cargando..."});
		        		 if(records.length === null || records.length <= 0){
		        			 Ext.Msg.alert('SET','No hay tasas.');
		        			 return;
		        		 }
		        	 }
		         }
	}); 
	NS.optTasa = new Ext.form.RadioGroup({
		id : PF + 'optTasa',
		name : PF + 'optTasa',
		x : 0,
		y : 0,
		disabled : true,
		columns : 2,
		items : [{
			boxLabel : 'Tasa Fija',
			name : 'optTsa',
			inputValue : 0,
			listeners : {
				check : {
					fn : function(opt, valor) {
						Ext.getCmp(PF + 'cmbTasaBase').setDisabled(false);
						Ext.getCmp(PF + 'txtPuntos').setDisabled(false);
						Ext.getCmp(PF + 'txtTasaFij').setDisabled(true);
					}
				}
			}
		},
		{
			boxLabel : 'Tasa Variable',
			name : 'optTsa',
			inputValue : 1,
			listeners : {
				check : {
					fn : function(opt, valor) {
						Ext.getCmp(PF + 'cmbTasaBase').setDisabled(true);
						Ext.getCmp(PF + 'txtPuntos').setDisabled(true);
						Ext.getCmp(PF + 'txtTasaFij').setDisabled(false);
					}
				}
			}
		} ]
	});
	NS.storeTasaBase = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psTasa : ''
		},
		paramOrder : [ 'psTasa' ],
		directFn : FinanciamientoModificacionCAction.funSQLTasa,
		idProperty : 'valor',
		fields : [ {
			name : 'valor'
		}, ],
		listeners: {
			load: function(s, records){
			
				if (records.length == null || records.length <= 0)
					Ext.getCmp(PF + 'txtValTasa').setValue("0.00000");
				else{
					Ext.getCmp(PF + 'txtValTasa').setValue(BFwrk.Util.formatNumber(records[0].get('valor').toFixed(5)));
					Ext.getCmp(PF + 'txtTasaVig').setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtPuntos').getValue()))+ (parseFloat(records[0].get('valor')))));
				}
				}
		}
	});
	NS.cmbTasaBase = new Ext.form.ComboBox({
		store: NS.storeTasa,
		name: PF + 'cmbTasaBase',
		id: PF + 'cmbTasaBase',	
		x: 20,
		y: 90,
		disabled :true, 
		emptyText: 'Seleccione la Tasa Base',
		width: 150,
		valueField:'idStr',
		displayField:'descripcion',
		mode: 'local',
		autocomplete: true,
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor){
					
					NS.storeTasaBase.baseParams.psTasa=combo.getValue();
					NS.storeTasaBase.load();
				}
			}
		}
	});
	validaDatos=function(psAccion){
		var band=false;
		var dTasaFinal=0;
		if(psAccion == "BUSCAR"){
			if (Ext.getCmp(PF + 'cmbLinea').getValue() == "") {
				Ext.Msg.alert("SET","Seleccione un contrato.");
				Ext.getCmp(PF + 'cmbLinea').focus();
				return;
			}else if(Ext.getCmp(PF + 'cmbDispo').getValue() == "") {
				Ext.Msg.alert("SET","Seleccione una disposición.");
				Ext.getCmp(PF + 'cmbDispo').focus();
				return;
			}
		}
		if(psAccion == "GUARDAR"){
			if (NS.proyecto!= "CIE"||(NS.proyecto== "CIE"&&vsTipoMenu != "A")){
				if(Ext.getCmp(PF + 'optTasa').getValue().inputValue==1){
					if (Ext.getCmp(PF + 'cmbTasaBase').getValue() == "") {
						Ext.Msg.alert("SET","Seleccione una tasa.");
						Ext.getCmp(PF + 'cmbTasaBase').focus();
						return;
					}
					else if (Ext.getCmp(PF + 'txtValTasa').getValue() == ""||Ext.getCmp(PF + 'txtValTasa').getValue() == "0.00000") {
						Ext.Msg.alert("SET","No se ha alimentado el valor de la tasa.");
						Ext.getCmp(PF + 'txtValTasa').focus();
						return;
					}
					else if (Ext.getCmp(PF + 'txtTasaVig').getValue() == ""||Ext.getCmp(PF + 'txtTasaVig').getValue() == "0.00000") {
						Ext.Msg.alert("SET","Ingrese el valor de la tasa variable.");
						Ext.getCmp(PF + 'txtTasaVig').focus();
						return;
					}
				}
				else if(Ext.getCmp(PF + 'optTasa').getValue().inputValue==0){
					if (Ext.getCmp(PF + 'txtTasaFij').getValue() == ""||Ext.getCmp(PF + 'txtTasaFij').getValue() == "0.00000") {
						Ext.Msg.alert("SET","Ingrese el valor de la tasa fija.");
						Ext.getCmp(PF + 'txtTasaFij').focus();
						return;
					}
				}
				if(Ext.getCmp(PF + 'optTasa').getValue()==1)
					dTasaFinal=BFwrk.Util.unformatNumber(parseFloat(Ext.getCmp(PF + 'txtTasaVig').getValue()));
				if(Ext.getCmp(PF + 'optTasa').getValue()==0)
					dTasaFinal=BFwrk.Util.unformatNumber(parseFloat(Ext.getCmp(PF + 'txtTasaFij').getValue()));

			}
			var registroSeleccionado = NS.gridMod.getSelectionModel().getSelections();
			if (registroSeleccionado<=0) {
				Ext.Msg.alert("SET","Es necesario seleccionar un registro.");
				return;
			}else{
				if((Ext.getCmp(PF + 'cmbTasaBase').getValue()==registroSeleccionado[0].get('idTasaBase'))&&
						(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtValTasa').getValue())).toFixed(5)==parseFloat(registroSeleccionado[0].get('valorTasa')).toFixed(5))&&
						(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtPuntos').getValue())).toFixed(5)==parseFloat(registroSeleccionado[0].get('puntos')).toFixed(5))&&
						(dTasaFinal==parseFloat(registroSeleccionado[0].get('tasa')).toFixed(5))&&
						(Ext.getCmp(PF + 'txtFecCor').getValue().getTime()==stringToDate(registroSeleccionado[0].get('fecPago').getTime()))&&
						(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtRenta').getValue()))==parseFloat(registroSeleccionado[0].get('renta')))&&
						(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtIva').getValue())).toFixed(5)==parseFloat(registroSeleccionado[0].get('iva')))){
					Ext.Msg.alert("SET","No se ha realizado ningún cambio en los datos originales");
					return;
				}
			}
			if(Ext.getCmp(PF + 'txtFecCor').getValue()==""||Ext.getCmp(PF + 'txtFecCor').getValue()==null){
				Ext.Msg.alert("SET","Ingrese una fecha de corte correcta.");
				Ext.getCmp(PF + 'txtFecCor').reset();
				Ext.getCmp(PF + 'txtFecCor').focus();
				return;
			}
		}
		band=true;
		return band;
	}
	//CODIGO DEL GRID
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect:false,
		listeners:{
			rowselect:{
				fn : function(selectionModel, rowIndex, record) {
					if(rowIndex>0){
						Ext.Msg.alert("SET","No puede modificar otra amortización que no sea la primera.")
						NS.gridMod.getSelectionModel().clearSelections();
						Ext.getCmp(PF + 'btnMod').setDisabled(true);
					}
					else
						Ext.getCmp(PF + 'btnMod').setDisabled(false);
				}
			},
		}
	});
	//Store del grid
	NS.storeSelectAmortizaciones = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psIdContrato : '',
			piDisposicion : 0,
			pbCambioTasa : false,
			psTipoMenu : '',
			psProyecto : '',
			piCapital : 0
		},
		paramOrder : [ 'psIdContrato', 'piDisposicion', 'pbCambioTasa',
		               'psTipoMenu', 'psProyecto', 'piCapital' ],
		               directFn : FinanciamientoModificacionCAction.selectAmortizaciones,
		               fields: [
		                        {name: 'idContrato'},
		                        {name: 'idDisposicion'},
		                        {name: 'idAmortizacion'},
		                        {name: 'fecPago'},
		                        {name: 'fecInicio'},
		                        {name: 'fecVencimiento'},
		                        {name: 'dias'},
		                        {name: 'saldoInsoluto'},
		                        {name: 'capital'},
		                        {name: 'interes'},
		                        {name: 'renta'},
		                        {name: 'estatus'},
		                        {name: 'periodo'},
		                        {name: 'noAmortizaciones'},
		                        {name: 'tipoTasaBase'},
		                        {name: 'tasaBase'},
		                        {name: 'idTasaBase'},
		                        {name: 'valorTasa'},
		                        {name: 'puntos'},
		                        {name: 'tasaVigente'},
		                        {name: 'iva'},
		                        ],
		                        listeners: {
		                        	load: function(s, records) {
		                        		var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeSelectAmortizaciones, msg: "Cargando..."});
		                        		if (records.length == null || records.length <= 0){
		                        			Ext.Msg.alert('SET', 'La disposición no cuenta con amortizaciones.');
		                        			myMask.hide();
		                        		}
		                        		Ext.getCmp(PF + 'txtTotalReg').setValue(records.length);
		                        	}
		                        }
	});

	NS.buscar=function(){
		var vbCambioTasa=false;
		var valorCheck=0;
		if(Ext.getCmp(PF + 'chkCapital').getValue())
			valorCheck=1;
		if(validaDatos("BUSCAR")){
			vbCambioTasa = true;
			NS.storeSelectAmortizaciones.load({
				params : {
					psIdContrato : NS.cmbLinea.getValue(),
					piDisposicion : parseInt(NS.cmbDispo.getValue()),
					pbCambioTasa : vbCambioTasa,
					psTipoMenu : '',
					psProyecto : NS.proyecto,
					piCapital : valorCheck
				},
			});
		}
	}
	NS.columnasMod = new Ext.grid.ColumnModel([
	                                           NS.columnaSeleccion,
	                                           {header: '', width: 0, dataIndex: 'idContrato', sortable: true, hidden : true,
	                                        	   hideable : false,},
	                                        	   {header: '', width: 0, dataIndex: 'idDisposicion', sortable: true, hidden : true,
	                                        		   hideable : false},
	                                        		   {header: '', width: 0, dataIndex: 'idAmortizacion', sortable: true, hidden : true,
	                                        			   hideable : false},
	                                        			   {header: 'Fecha Pago', width: 120, dataIndex: 'fecPago', sortable: true},
	                                        			   {header: 'Fecha Inicial', width:120 , dataIndex: 'fecInicio', sortable: true},
	                                        			   {header: '', width: 0, dataIndex: 'fecVencimiento', sortable: true, hidden : true,hideable : false},
	                                        			   {header: '', width:0 , dataIndex: 'dias', sortable: true, hidden : true,hideable : false},
	                                        			   {header: 'Saldo Insoluto', width: 120, dataIndex: 'saldoInsoluto', sortable: true,align: 'right', sortable: true,renderer  : function(value) {return Ext.util.Format.number(value, '0,0.00');},},
	                                        			   {header: '', width:0 , dataIndex: 'capital', sortable: true,hidden : true, hideable : false, hidden : true,hideable : false},
	                                        			   {header: 'Interes', width:120 , dataIndex: 'interes', sortable: true,align: 'right', sortable: true,renderer  : function(value) {return Ext.util.Format.number(value, '0,0.00');}},
	                                        			   {header: '', width:0 , dataIndex: 'renta', sortable: true,align: 'right', sortable: true, hidden : true,hideable : false,renderer  : function(value) {return Ext.util.Format.number(value, '0,0.00');},},
	                                        			   {header: '', width: 0, dataIndex: 'estatus', sortable: true,hidden : true,hideable : false},
	                                        			   {header: 'Periodicidad', width: 80, dataIndex: 'periodo', sortable: true},
	                                        			   {header: '', width: 0, dataIndex: 'noAmortizaciones', sortable: true,hidden : true,hideable : false},
	                                        			   {header: '', width:0 , dataIndex: 'tipoTasaBase', sortable: true,hidden : true,hideable : false},
	                                        			   {header: 'Tasa Base', width:120 , dataIndex: 'tasaBase', sortable: true},
	                                        			   {header: '', width:0 , dataIndex: 'idTasaBase', sortable: true,hidden : true,hideable : false},
	                                        			   {header: 'Valor de la tasa', width: 120, dataIndex: 'valorTasa', sortable: true,align: 'right', sortable: true,renderer  : function(value) {
	                                        				   return Ext.util.Format.number(value, '0,0.00000');
	                                        			   },},
	                                        			   {header: 'Puntos', width:120 , dataIndex: 'puntos', sortable: true,align: 'right', sortable: true,renderer  : function(value) {
	                                        				   return Ext.util.Format.number(value, '0,0.00000');
	                                        			   },},
	                                        			   {header: 'Tasa Final', width: 120, dataIndex: 'tasaVigente' ,renderer  : function(value) {return Ext.util.Format.number(value, '0,0.00000');},},
	                                        			   
	                                        			   {header: 'Iva', width:120 , dataIndex: 'iva', sortable: true,align: 'right', sortable: true,renderer  : function(value) {
	                                        				   return Ext.util.Format.number(value, '0,0.00000');
	                                        			   },}
	                                        			   ]);
	NS.gridMod = new Ext.grid.GridPanel({
		store:NS.storeSelectAmortizaciones,
		id: PF+'gridMod',
		name: PF+'gridMod',
		cm: NS.columnasMod,
		autowidth: true,
		height: 180,
		x: 0,
		y: 60,
		frame:true,
		stripeRows: true,
		columnLines: true,
		cm:NS.columnasMod,
		sm: NS.columnaSeleccion,

	});

	/*Find el codigo del grid*/

	//Botón cancelar
	NS.cancelar1=function(){
		NS.habilitar(true);
		limpia();
		Ext.getCmp(PF+'txtTasaFij').setDisabled(true);		 
	}
	
	/*Panel Vista */
	NS.modificacionC = new Ext.form.FormPanel({
		title: 'Modificacion C',
		width: 1100,
		height: 1200,
		padding: 10,
		layout: 'absolute',
		id: PF + 'modificacionC',
		name: PF + 'modificacionC',
		renderTo: NS.tabContId,
		frame: true,
		autoScroll: true,
		items : [
		         /*******************************aqui empieza los elementos de arriba*****************************/            

		         { 
		        	 xtype: 'fieldset',
		        	 title: '',
		        	 width: 1050,
		        	 height: 300,
		        	 x: 1,
		        	 y: 1,
		        	 layout: 'absolute',
		        	 id: 'fSetArriba',
		        	 items: [
NS.lblEmpresa, NS.txtEmpresa,NS.cmbEmpresa,
{
	xtype: 'label',
	text: 'Línea:',
	x: 440,
	y: 0
},
NS.cmbLinea,
{
	xtype: 'label',
	text: 'Disposición:',
	x: 640,
	y: 0
},
NS.cmbDispo,
NS.chkCapital,
{
	xtype: 'button',
	text: 'Buscar',
	x: 920,
	y: 20,
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
NS.gridMod,
{
	xtype: 'label',
	text: 'Total registros:',
	x: 800,
	y: 255
},
{
	xtype: 'numberfield',
	x: 880,
	y: 255,
	width: 120,
	disabled :true, 
	name: PF + 'txtTotalReg',
	id: PF + 'txtTotalReg',
}
]},   
{ 
	xtype: 'fieldset',
	title: 'Nuevos Datos',
	width: 1050,
	height: 150,
	x: 1,
	y: 300,
	layout: 'absolute',
	id: 'fSetAbajo',
	items: [
	        {
	        	xtype : 'fieldset',
	        	title : 'Tasas',
	        	x : 10,
	        	y : 0,
	        	width : 200,
	        	height : 70,
	        	layout : 'absolute',
	        	items : [NS.optTasa
	        	         ]
	        },
	        {
	        	xtype: 'label',
	        	text: 'Fecha corte:',
	        	x: 250,
	        	y: 0
	        },
	        NS.txtFecCor,
	        {
	        	xtype: 'label',
	        	text: 'Renta:',
	        	x: 430,
	        	y: 0,
	        	hidden:true
	        },
	        NS.txtRenta,
	        {
	        	xtype: 'label',
	        	text: 'I.V.A.:',
	        	x: 600,
	        	y: 0
	        },
	        {
	        	xtype: 'textfield',
	        	x: 600,
	        	y: 20,
	        	width: 120,
	        	value:'0.00000',
	        	disabled :true, 
	        	name: PF + 'txtIva',
	        	id: PF + 'txtIva',
	        	listeners: {
	        		change: {
	        			fn: function(textfield, value){
	        				if(textfield.getValue()=="")
	        					textfield.setValue("0.00000");
	        				textfield.setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(textfield.getValue())).toFixed(5)));
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'label',
	        	text: 'Tasa Base:',
	        	x: 25,
	        	y: 70
	        },
	        NS.cmbTasaBase,
	        {
	        	xtype: 'label',
	        	text: 'Valor Tasa:',
	        	x: 200,
	        	y: 70
	        },
	        {
	        	xtype: 'textfield',
	        	x: 200,
	        	y: 90,
	        	value:'0.00000',
	        	width: 120,
	        	name: PF + 'txtValTasa',
	        	id: PF + 'txtValTasa',
	        	disabled :true, 
	        	listeners:{
	        		change: {
	        			fn: function(box, value){
	        				if(value!=""){
	        					if(Ext.getCmp(PF + 'txtPuntos').getValue()!="")
	        						Ext.getCmp(PF + 'txtTasaVig').setValue(BFwrk.Util.formatNumber((parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtValTasa').getValue()))+parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtPuntos').getValue()))).toFixed(5)));
	        					else
	        						Ext.getCmp(PF + 'txtTasaVig').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF + 'txtValTasa').getValue()));
	        				}else{
	        					if(Ext.getCmp(PF + 'txtPuntos').getValue()!="")
	        						Ext.getCmp(PF + 'txtTasaVig').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF + 'txtPuntos').getValue()));
	        					else
	        						Ext.getCmp(PF + 'txtTasaVig').setValue("0");
	        				}
	        				if(Ext.getCmp(PF + 'txtValTasa').getValue()!="")
	        					Ext.getCmp(PF + 'txtValTasa').setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtValTasa').getValue())).toFixed(5)));
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'label',
	        	text: 'Puntos:',
	        	x: 350,
	        	y: 70
	        },
	        {
	        	xtype: 'textfield',
	        	x: 350,
	        	y: 90,
	        	value:'0.00000',
	        	width: 120,
	        	name: PF + 'txtPuntos',
	        	id: PF + 'txtPuntos',
	        	disabled :true, 
	        	listeners:{
	        		change:{
	        			fn: function(box, value){
	        				if(Ext.getCmp(PF + 'txtPuntos').getValue()=="")
	        					Ext.getCmp(PF + 'txtPuntos').setValue("0.00000");
	        				if(Ext.getCmp(PF + 'txtValTasa').getValue()!="")
	        					Ext.getCmp(PF + 'txtTasaVig').setValue(BFwrk.Util.formatNumber((parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtPuntos').getValue()))+parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtValTasa').getValue()))).toFixed(5)));		     	        					
	        				else
	        					Ext.getCmp(PF + 'txtTasaVig').setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtPuntos').getValue())).toFixed(5)));
	        				Ext.getCmp(PF + 'txtPuntos').setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtPuntos').getValue())).toFixed(5)));
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'label',
	        	text: 'Tasa Vigente:',
	        	x: 500,
	        	y: 70
	        },
	        {
	        	xtype: 'textfield',
	        	x: 500,
	        	y: 90,
	        	value:'0.00000',
	        	width: 120,
	        	disabled :true, 
	        	name: PF + 'txtTasaVig',
	        	id: PF + 'txtTasaVig',
	        	listeners:{
	        		change:{
	        			fn: function(box, value){
	        				Ext.getCmp(PF + 'txtTasaVig').setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtTasaVig').getValue())).toFixed(5)));
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'label',
	        	text: 'Tasa Fija:',
	        	x: 650,
	        	y: 70
	        },
	        {
	        	xtype: 'textfield',
	        	x: 650,
	        	y: 90,
	        	value:'0.00000',
	        	width: 120,
	        	disabled :true, 
	        	name: PF + 'txtTasaFij',
	        	id: PF + 'txtTasaFij',
	        	listeners:{
	        		change:  {
	        			fn: function(box, value){
	        				if(box.getValue()=="")
	        					box.setValue("0.00000");
	        				if(parseFloat(BFwrk.Util.unformatNumber(box.getValue()))>100){
	        					Ext.Msg.alert("SET","El tasa no puede ser mayor al 100%");
	        					box.setValue("100.00000");
	        					return;
	        				}
	        				box.setValue(BFwrk.Util.unformatNumber(parseFloat(BFwrk.Util.unformatNumber(box.getValue())).toFixed(5)));

	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'button',
	        	text: 'Modificar',
	        	x: 930,
	        	y: 0,
	        	width: 90,
	        	id: PF + 'btnMod',
	        	name: PF + 'btnMod',
	        	disabled: true,
	        	listeners:{
	        		click:{
	        			fn: function(btn){ 
	        				var registroSeleccionado = NS.gridMod.getSelectionModel().getSelections();
	        				if (registroSeleccionado<=0) {
	        					Ext.Msg.alert("SET","Es necesario seleccionar un registro.");
	        					return;
	        				}else{
	        					NS.habilitar(false);
	        					Ext.getCmp(PF + 'cmbTasaBase').reset();
	        					Ext.getCmp(PF + 'txtFecCor').setValue(stringToDate(registroSeleccionado[0].get('fecVencimiento')));
	        					Ext.getCmp(PF + 'txtRenta').setValue(registroSeleccionado[0].get('renta'));
	        					Ext.getCmp(PF + 'txtIva').setValue(BFwrk.Util.formatNumber(registroSeleccionado[0].get('iva').toFixed(5)));
	        					if (vsTipoMenu == "A"&&NS.proyecto== "CIE"){
	        						Ext.getCmp(PF + 'optTasa').reset();
	        						Ext.getCmp(PF+'txtValTasa').setDisabled(true);
	        						Ext.getCmp(PF+'cmbTasaBase').setDisabled(true);
	        						Ext.getCmp(PF+'txtPuntos').setDisabled(true);
	        						Ext.getCmp(PF+'txtTasaVig').setDisabled(true);
	        						Ext.getCmp(PF+'txtTasaFij').setDisabled(true);
	        					}
	        					if(registroSeleccionado[0].get('tipoTasaBase')=="V"){
	        						Ext.getCmp(PF + 'optTasa').setValue(1);
	        						Ext.getCmp(PF + 'cmbTasaBase').setDisabled(false);
	        						Ext.getCmp(PF + 'txtPuntos').setDisabled(false);
	        						Ext.getCmp(PF + 'txtTasaFij').setDisabled(true);
	        						Ext.getCmp(PF+'txtValTasa').setValue(BFwrk.Util.formatNumber(registroSeleccionado[0].get('valorTasa').toFixed(5)));
	        						Ext.getCmp(PF + 'txtPuntos').setValue(BFwrk.Util.formatNumber(registroSeleccionado[0].get('puntos').toFixed(5)));
	        						Ext.getCmp(PF + 'txtTasaVig').setValue(BFwrk.Util.formatNumber((parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtPuntos').getValue()))+parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtValTasa').getValue()))).toFixed(5)));
	        						Ext.getCmp(PF + 'txtTasaFij').setValue("0.00000");
	        						NS.cmbTasaBase.setValue(registroSeleccionado[0].get('idTasaBase'));
	        						NS.cmbTasaBase.setRawValue(registroSeleccionado[0].get('tasaBase'));
	        						NS.storeTasa.load();
	        					}
	        					else{
	        						Ext.getCmp(PF + 'optTasa').setValue(0);
	        						Ext.getCmp(PF + 'cmbTasaBase').setDisabled(true);
	        						Ext.getCmp(PF + 'txtPuntos').setDisabled(true);
	        						Ext.getCmp(PF + 'txtTasaFij').setDisabled(false);
	        						Ext.getCmp(PF+'txtValTasa').setValue("0.00000");
	        						Ext.getCmp(PF+'txtTasaVig').setValue("0.00000");
	        						Ext.getCmp(PF + 'txtPuntos').setValue(BFwrk.Util.formatNumber(registroSeleccionado[0].get('puntos').toFixed(5)));
	        						Ext.getCmp(PF + 'txtTasaFij').setValue(BFwrk.Util.formatNumber(parseFloat(registroSeleccionado[0].get('valorTasa'))+parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtValTasa').getValue()))));
	        						NS.storeTasa.load();
	        					}
	        				}
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'button',
	        	text: 'Aceptar',
	        	x: 830,
	        	y: 90,
	        	width: 90,
	        	id: PF + 'btnAceptar',
	        	name: PF + 'btnAceptar',
	        	disabled: true,
	        	listeners:{
	        		click: {
	        			fn: function(btn){
	        				var registroSeleccionado = NS.gridMod.getSelectionModel().getSelections();
	        				if (registroSeleccionado<=0) {
	        					Ext.Msg.alert("SET","Es necesario seleccionar un registro.");
	        					return;
	        				}
	        				if(!validaDatos("GUARDAR"))
	        					return;
	        				NS.MB2 = Ext.MessageBox;
	        				Ext.apply(NS.MB2, {
	        					YES : {
	        						text : 'Si',
	        						itemId : 'yes'
	        					},
	        					NO : {
	        						text : 'No',
	        						itemId : 'no'
	        					}
	        				});
	        				NS.MB2
	        				.confirm('SET','¿Desea actualizar este registro?',
	        						function(btn) {
	        					if (btn === 'yes') {
	        						var matrizAmort = new Array();
	        						for (var i = 0; i < NS.storeSelectAmortizaciones.data.length; i++) {
	        							var registro=NS.storeSelectAmortizaciones.getAt(i);
	        							var registrosAmort= {};
	        							registrosAmort.interes = registro.data.interes;
	        							registrosAmort.saldoInsoluto = registro.data.saldoInsoluto;
	        							registrosAmort.fecVencimiento = registro.data.fecVencimiento;
	        							registrosAmort.fecInicio = registro.data.fecInicio;
	        							registrosAmort.idAmortizacion = registro.data.idAmortizacion;
	        							matrizAmort[i] = registrosAmort;
	        						}
	        						var jsonGrid= Ext.util.JSON.encode(matrizAmort);
	        						BFwrk.Util.msgWait('Ejecutando solicitud...',true);
	        						FinanciamientoModificacionCAction.modificar(
	        								Ext.getCmp(PF + 'cmbLinea').getValue(),
	        								Ext.getCmp(PF + 'cmbDispo').getValue(),
	        								Ext.getCmp(PF + 'optTasa').getValue().inputValue,
	        								Ext.getCmp(PF + 'cmbTasaBase').getValue(),
	        								BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtValTasa').getValue()),
	        								BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtPuntos').getValue()),
	        								BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtTasaVig').getValue()),
	        								BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtTasaFij').getValue()),
	        								BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecCor').getValue()),
	        								Ext.getCmp(PF + 'chkCapital').getValue(),
	        								BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtRenta').getValue()),
	        								BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtIva').getValue()),
	        								jsonGrid,function(mapResult,e) {
	        									BFwrk.Util.msgWait('Terminado...',false);
	        									for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
	        										if(mapResult.msgUsuario[msg]!="ok")
	        											Ext.Msg.alert('Información SET',''+ mapResult.msgUsuario[msg]);
	        									}
	        									if(mapResult.result==1){
	        										if (vsTipoMenu == "A"&&NS.proyecto== "CIE"){
	        											BFwrk.Util.msgWait('Ejecutando solicitud...',true);
	        											FinanciamientoModificacionCAction.modificaProvision(
	        													Ext.getCmp(PF + 'cmbLinea').getValue(),
	        													parseInt(Ext.getCmp(PF + 'cmbDispo').getValue()),
	        													parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue()),
	        													function(mapResult,e) {
	        														BFwrk.Util.msgWait('Terminado...',false);
	        														for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
	        															Ext.Msg.alert('Información SET',''+ mapResult.msgUsuario[msg]);
	        														}
	        														NS.buscar();
	        														NS.cancelar1();
	        													});
	        										}else{
	        											NS.buscar();
	        											NS.cancelar1();
	        											}
	        									}else
	        										return;
	        								});
	        					}
	        					else
	        						return;
	        				});
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'button',
	        	text: 'Cancelar',
	        	x: 930,
	        	y: 90,
	        	width: 90,
	        	id: PF + 'btnCancelar1',
	        	name: PF + 'btnCancelar1',
	        	disabled: true,
	        	listeners: {
	        		click:{
	        			fn: function(btn){
	        				NS.cancelar1();  			
	        			}
	        		}
	        	}
	        }
	        ]
}, 
{ 
	xtype: 'fieldset',
	title: '',
	width: 1050,
	height: 50,
	x: 1,
	y: 460,
	layout: 'absolute',
	id: 'fSetInferior',
	items: [
	        {
	        	xtype: 'button',
	        	text: 'Limpiar',
	        	x: 930,
	        	y:0,
	        	width: 90,
	        	id: PF + 'btnLimpiar',
	        	name: PF + 'btnLimpiar',
	        	disabled: false,
	        	listeners: {
	        		click: {
	        			fn: function(btn){
	        				NS.limpiar();
	        				Ext.getCmp(PF+'txtTasaFij').setDisabled(true);
	        			}
	        		}
	        	}
	        },
	        ]}
]
	});

	NS.modificacionC.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());



});