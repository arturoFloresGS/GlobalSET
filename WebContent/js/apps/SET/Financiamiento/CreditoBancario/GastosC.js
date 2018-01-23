//GGonzález
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Finaciamiento.CreditoBancario');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecHoy = apps.SET.FEC_HOY;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	var  vbExiste=false,existenGastos=false;
	//variable global cargada desde la pantalla de créditos bancarios
	if(NS.noEmpresa==undefined)
		NS.noEmpresa=NS.GI_ID_EMPRESA;
	if(NS.nomEmpresa ==undefined)
		NS.nomEmpresa=NS.GI_NOM_EMPRESA;
	
	//
	
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		directFn : VencimientoFinanciamientoCAction.obtenerEmpresas,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbEmpresa,
		msg : "Cargando..."
	});
	NS.storeCmbEmpresa.load();
	
	NS.storecmbLinea = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {psTipoMenu:'',iBanco:0, noEmpresa:0},
		paramOrder : ['psTipoMenu','iBanco','noEmpresa'],
		directFn : GastosFinanciamientoCAction.obtenerContratos,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ]
	});
	
	NS.cmbEmpresa= new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa,
		name: PF + 'cmbEmpresa',
		id: PF + 'cmbEmpresa',
		x: 50,
		y: 15,
		width:230,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		displayField: 'descripcion',
		valueField: 'id',
		value:NS.nomEmpresa,
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
					NS.cmbLinea.reset();
					NS.storecmbLinea.removeAll();
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storecmbLinea,
						msg : "Cargando..."
					});
					NS.storecmbLinea.baseParams.noEmpresa=parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
					NS.storecmbLinea.load();
				}
			}
		}
	});
	//
	

	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storecmbLinea,
		msg : "Cargando..."
	});
	NS.storecmbLinea.baseParams.noEmpresa=parseInt(NS.noEmpresa);
	NS.storecmbLinea.load();
	NS.cmbLinea = new Ext.form.ComboBox({
		store: NS.storecmbLinea,
		name: PF + 'cmbLinea',
		id: PF + 'cmbLinea',
		x: 300,
		y: 15,
		width: 200,
		typeAhead: true,
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		autocomplete : true,
		emptyText: 'Seleccione la línea',
		triggerAction: 'all',
		displayField: 'idStr',
		valueField: 'descripcion',
		mode: 'local',
		tabIndex: 1,
		listeners:{
			select:{
				fn:function(combo, valor){
					NS.cmbDisp.reset();
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
		directFn : GastosFinanciamientoCAction.obtenerDisposiciones,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
	});
	NS.cmbDisp= new Ext.form.ComboBox({
		store: NS.storeDisposiciones, 
		name: PF + 'cmbDisp',
		id: PF + 'cmbDisp',
		x: 550,
		y: 15,
		width: 170,
		triggerAction: 'all',
		displayField: 'descripcion',
		autocomplete : true,
		valueField: 'id',
		mode: 'local',
		emptyText: 'Seleccione la disposición',
		listeners:{
			select:{
				fn:function(combo, valor) { 
					NS.storeGridGastos.baseParams.psFinan = NS.cmbLinea
					.getValue();
					NS.storeGridGastos.baseParams.piDisp = parseInt(NS.cmbDisp
					.getValue());
					var myMask = new Ext.LoadMask(
							Ext.getBody(), {
								store : NS.storeGridGastos,
								msg : "Cargando..."
							});
					NS.storeGridGastos.load({
						callback:function(records){
							if(records.length<=0){
								Ext.Msg.alert("SET","No Existen Gastos Registrados.");
								existenGastos=false;
								return;
							}
							else{
								existenGastos=true;
							}
						}
					});
				}
			}
		}
	});
	NS.storeSelectInhabil = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			valorFecha : ''
		},
		paramOrder : [ 'valorFecha' ],
		directFn : AltaFinanciamientoAction.selectInhabil,
		idProperty : 'fecInhabil',
		fields : [ {
			name : 'fecInhabil'
		}, ]
	});
	NS.txtFechaPago = new Ext.form.DateField({
		id: PF+'txtFechaPago',
		name: PF+'txtFechaPago',
		format: 'd/m/Y',
		x: 750,
		y: 15,
		width: 120,
		listeners:{
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			blur:function(caja){
				if (caja.getValue() == null) {
					Ext.getCmp(PF + 'txtFechaPago')
					.setValue(NS.fecHoy);
				}
			},
			change:function(caja){
				diaSemana = caja.getValue()
				.getDay();
				if (diaSemana == "0") {
					BFwrk.Util
					.msgShow(
							'La fecha de pago es día inhábil.',
					'ERROR');
					Ext.getCmp(PF + 'txtFechaPago')
					.focus();
					Ext.getCmp(PF + 'txtFechaPago')
					.setValue(NS.fecHoy);

				} else if (diaSemana == "6") {
					BFwrk.Util
					.msgShow(
							'La fecha de pago es día inhábil.',
					'ERROR');
					Ext.getCmp(PF + 'txtFechaPago')
					.focus();
					Ext.getCmp(PF + 'txtFechaPago')
					.setValue(NS.fecHoy);
				} else {
					NS.storeSelectInhabil
					.load({
						params : {
							valorFecha : caja
							.getValue()
						},
						callback : function(
								records,
								operation,
								success) {
							if (records.length > 0) {
								BFwrk.Util
								.msgShow('La fecha de pago es día inhábil.','ERROR');
								NS.storeSelectInhabil.valorFecha = '';
								NS.valorFecha = '';
								Ext.getCmp(PF+ 'txtFechaPago').focus();
								Ext.getCmp(PF+ 'txtFechaPago').setValue(NS.fecHoy);
							}
							NS.storeSelectInhabil.valorFecha = '';
						}

					});
				}
			}
		}	
	});
	NS.storeCmbGastos = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {},
		paramOrder : [],
		directFn : GastosFinanciamientoCAction.obtenerGastos,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load : function(s, records) {
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbGastos,
		msg : "Cargando..."
	});
	NS.storeCmbGastos.load();
	NS.cmbGasto= new Ext.form.ComboBox({
		store: NS.storeCmbGastos,
		name: PF + 'cmbGasto',
		id: PF + 'cmbGasto',
		x: 65,
		triggerAction:'all',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		autocomplete : true,
		y: 70,
		width: 200,  
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
		emptyText: 'Seleccione el gasto',
		listeners:{
			select:{
				fn:function(combo, valor) { 
					 var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtGasto', NS.cmbGasto.getId());
				}
			}
		}
	});
	NS.eliminar=function(){
		var gastos = NS.gridGastos.getSelectionModel()
		.getSelections();
		if(gastos.length<=0){
			Ext.Msg.alert("SET","Seleccione un registro.");
			return;
		}
		NS.MB = Ext.MessageBox;
		Ext.apply(NS.MB, {
			YES : {
				text : 'Si',
				itemId : 'yes'
			},
			NO : {
				text : 'No',
				itemId : 'no'
			}
		});
		NS.MB
		.confirm(
				'SET',
				'Se van a eliminar los gastos seleccionados. ¿Desea continuar?',
				function(btn) {
					if (btn === 'yes') {
						BFwrk.Util.msgWait('Eliminando Gastos...', true);
						var matrizGastos = new Array();
						var vdGasto=0,pdComision=0,viBancoC=0;
						for (var i = 0; i < gastos.length; i++) {
							var registrosGastos= {};
							registrosGastos.idContrato = gastos[i].get('idContrato');
							registrosGastos.idDisposicion = gastos[i].get('idDisposicion');
							registrosGastos.idAmortizacion = gastos[i].get('idAmortizacion');
							matrizGastos[i] = registrosGastos;
							var jsonStringGastos = Ext.util.JSON.encode(matrizGastos);
							GastosFinanciamientoCAction.eliminarGastos(jsonStringGastos,	
									function(mapResult,e) {
								BFwrk.Util.msgWait('Terminado...',false);
								if (mapResult.msgUsuario !== null
										&& mapResult.msgUsuario !== ''
											&& mapResult.msgUsuario != undefined) {
									for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
										Ext.Msg
										.alert(
												'Información SET',
												''
												+ mapResult.msgUsuario[msg]);
										NS.storeGridGastos.load();
									}
								}
							});
						}    
					}
					else{
						return;
					}
				});
	}
	NS.limpia=function(){
		Ext.getCmp(PF + 'txtGasto').setValue("");
		Ext.getCmp(PF + 'cmbGasto').reset();
	    Ext.getCmp(PF + 'txtImporte').setValue("0.00");
	    Ext.getCmp(PF + 'txtPorcentaje').setValue("0.00");
	    Ext.getCmp(PF + 'txtIva').setValue("0.00");
	    Ext.getCmp(PF + 'txtTotal').setValue("0.00");
	    NS.chkPagar.setValue(0);
	}
	NS.limpiar=function(){
		 NS.cmbLinea.reset();
		 NS.cmbDisp.reset();
		 NS.gridGastos.removeAll(); 
		 NS.storeDisposiciones.removeAll();
		 NS.storeGridGastos.removeAll();
		 NS.limpia();
		 NS.cmbEmpresa.reset();
		 Ext.getCmp(PF + 'txtNoEmpresa').setValue(NS.noEmpresa);
	}
	NS.validaDatosGasto=function(){
		var band=false;
		if (Ext.getCmp(PF + 'cmbLinea').getValue() == "") {
			Ext.Msg.alert("SET","Seleccione una línea.");
			Ext.getCmp(PF + 'cmbLinea').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'cmbDisp').getValue() == ""){
			Ext.Msg.alert("SET","Seleccione una disposición");
			Ext.getCmp(PF + 'cmbDisp').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'txtGasto').getValue() == ""){
			Ext.Msg.alert("SET","Seleccione un gasto");
			Ext.getCmp(PF + 'txtGasto').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'cmbGasto').getValue() == ""){
			Ext.Msg.alert("SET","Seleccione un gasto");
			Ext.getCmp(PF + 'cmbGasto').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'txtImporte').getValue() == ""||Ext.getCmp(PF + 'txtImporte').getValue() == "0.00"){
			Ext.Msg.alert("SET","Escriba el importe");
			Ext.getCmp(PF + 'txtImporte').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'txtPorcentaje').getValue() == ""||Ext.getCmp(PF + 'txtPorcentaje').getValue() == "0.00"){
			Ext.Msg.alert("SET","Escriba el porcentaje");
			Ext.getCmp(PF + 'txtPorcentaje').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'txtIva').getValue() == ""){

			Ext.getCmp(PF + 'txtIva').setValue("0.00");
			return;
		}
		/*If vsTipoMenu = "B" Then
	        If cmbBanco.ListIndex = -1 Then
	            MsgBox "Seleccione el Banco Comision!!", vbExclamation, "SET"
	            cmbBanco.SetFocus: Exit Function
	        ElseIf txtClabeBan.Text = "" Then
	            MsgBox "Escriba la clabe Bancaria!!", vbExclamation, "SET"
	            txtClabeBan.SetFocus: Exit Function
	        End If
    	End If*/
		band = true;
		return band;
	}
	NS.validaDatos=function(){
		var band= false;
		if (NS.storeGridGastos.getCount()<1) {
			Ext.Msg.alert("SET","Debe agregar al menos un gasto");
			Ext.getCmp(PF + 'cmbGasto').focus();
			band=false;
			return;
		}
		band=true;
		return band;
	}
	NS.agregarGridGastos = function(idContrato, idDisposicion,tipoPago,tipoGasto, descripcion, gasto, comision, iva, bancoGastcom,descBanco, clabeBancariaGastcom, total, pagar,fecPago) {
		var indice = 0;
		var recordsDatGrid = NS.storeGridGastos.data.items;
		var tamGrid = indice <= 0 ? (NS.storeGridGastos.data.items).length
				: indice;
		var datosClase = NS.gridGastos.getStore().recordType;
		var datos = new datosClase({
			idContrato:idContrato,
			idDisposicion:idDisposicion,
			tipoPago:tipoPago,
			tipoGasto:tipoGasto,
			descripcion:descripcion,
			gasto:gasto,
			comision:comision,
			iva:iva,
			bancoGastcom:bancoGastcom,
			descBanco:descBanco,
			clabeBancariaGastcom:clabeBancariaGastcom,
			total:total,
			pagar:pagar,
			fecPago:fecPago,
			idAmortizacion:0
		});
		NS.gridGastos.stopEditing();
		NS.storeGridGastos.insert(tamGrid, datos);
	};
	NS.agregar=function(){
		 var vsTipoPago,pagar;
		if(existenGastos){
			 NS.storeGridGastos.removeAll();
			 existenGastos=false;
		}
	    if(!NS.validaDatosGasto())
	    	return;
	    if(vbExiste)
	        vbExiste = false;
	    var cadena=Ext.getCmp(PF + 'cmbGasto').getRawValue();
	    var coincidencia=-1;
	    coincidencia=cadena.indexOf("COMISION");
	    if(coincidencia>-1)
	    	vsTipoPago = "C";
	    coincidencia=cadena.indexOf("NOTA");
	    if(coincidencia>-1)
	    	vsTipoPago = "C";
	    coincidencia=cadena.indexOf("GASTO");
	    if(coincidencia>-1)
	        vsTipoPago = "G";
	    if(NS.chkPagar.getValue())
	    	pagar="S";
	    else
	    	pagar="N";
	    NS.agregarGridGastos(NS.cmbLinea.getValue(),NS.cmbDisp.getValue(),vsTipoPago,Ext.getCmp(PF + 'txtGasto').getValue(),
	    		NS.cmbGasto.getRawValue(),BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImporte').getValue()),BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtPorcentaje').getValue()),
	    		BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtIva').getValue()),
	    		'','','',BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtTotal').getValue()),pagar,BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFechaPago').getValue()));
	    NS.gridGastos.getSelectionModel().selectAll();
	    NS.limpia();
	}
	NS.quitar=function(){
		var records = NS.gridGastos.getSelectionModel().getSelections();
		if(records.length<=0){
			Ext.Msg.alert("SET","Seleccione un registro.");
			return;
		}
		cantidad=NS.storeGridGastos.getCount();
		for(var i=0;i<records.length;i++){
			NS.gridGastos.store.remove(records[i]);
			NS.gridGastos.getView().refresh();
		}
	}
	//Grid
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	NS.columnasGastos = new Ext.grid.ColumnModel([
	                                              NS.columnaSeleccion,
	                                              {header: 'Línea', width: 120, dataIndex: 'idContrato', sortable: true},
	                                              {header: 'Crédito', width: 80, dataIndex: 'idDisposicion', sortable: true},
	                                              {header: '', width:0 , dataIndex: 'tipoPago',hidden : true, hideable : false,},
	                                              {header: '', width:0 , dataIndex: 'tipoGasto',hidden : true, hideable : false,},
	                                              {header: 'Descripción del gasto', width:200 , dataIndex: 'descripcion', sortable: true},
	                                              {header: 'Importe', width:120 , dataIndex: 'gasto', sortable: true,renderer  : function(value) {
		                              					return Ext.util.Format.number(value, '0,0.00');
		                              				}},
	                                              {header: 'Porcentaje', width: 120, dataIndex: 'comision', sortable: true,renderer  : function(value) {
	                              						return Ext.util.Format.number(value, '0,0.00000');}},
	                                              {header: 'IVA', width:120, dataIndex: 'iva', sortable: true,renderer  : function(value) {
		                              					return Ext.util.Format.number(value, '0,0.00');}},
	                                              {header: '', width: 0, dataIndex: 'bancoGastcom', hidden : true, hideable : false},
	                                              {header: '', width:120, dataIndex: 'descBanco',hidden : true, hideable : false},
	                                              {header: '', width: 120, dataIndex: 'clabeBancariaGastcom', hidden : true, hideable : false},
	                                              {header: 'Total', width: 120, dataIndex: 'total', sortable: true,renderer  : function(value) {
		                              					return Ext.util.Format.number(value, '0,0.00');}},
	                                              {header: 'Pagar', width: 60, dataIndex: 'pagar', sortable: true},
	                                              {header: 'Fecha Pago', width: 120, dataIndex: 'fecPago', sortable: true},
	                                              {header: '', width:0 , dataIndex: 'idAmortizacion',hidden : true, hideable : false,},
	                                              ]);
	NS.storeGridGastos = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psFinan:'',
			piDisp:0
		},
		paramOrder : ['psFinan','piDisp'],
		directFn : GastosFinanciamientoCAction.selectGastos,
		fields : [ {
			name : 'idContrato'
		}, {
			name : 'idDisposicion'
		},{
			name : 'tipoPago'
		},{
			name : 'tipoGasto'
		},{
			name : 'descripcion'
		},{
			name : 'gasto'
		},{
			name : 'comision'
		},{
			name : 'iva'
		},{
			name : 'bancoGastcom'
		},{
			name : 'descBanco'
		},{
			name : 'clabeBancariaGastcom'
		},{
			name : 'total'
		},{
			name : 'pagar'
		},{
			name : 'fecPago'
		},{
			name : 'idAmortizacion'
		},
		],
		listeners : {
			load : function(s, records) {
			}
		}
	});
	NS.gridGastos = new Ext.grid.GridPanel({
		store: NS.storeGridGastos,
		id: 'gridGastos',
		cm: NS.columnasGastos,
		width: 1025,
		height: 180,
		frame : true,
		x: 0,
		y: 160,
		stripeRows: true,
		columnLines: true,
		cm: NS.columnasGastos,
		sm: NS.columnaSeleccion,
	});
	//Fin Grid
	NS.chkPagar = new Ext.form.Checkbox({
		name : PF + 'chkPagar',
		id : 'chkPagar',
		x : 0,
		y : 110,
		boxLabel: 'Pagar Gasto/Comisión'
	});
	NS.gastosC = new Ext.form.FormPanel({
		title: 'Gastos/Comisiones (Créditos Bancarios)',
		autowidth: true,
		height: 900,
		padding: 10,
		layout: 'absolute',
		id: PF + 'GastosC',
		name: PF + 'GastosC',
		renderTo: NS.tabContId,
		frame: true,
		autoScroll: true,
		items : [
		         { 
		        	 xtype: 'fieldset',
		        	 title: '',
		        	 autowidth: true,
		        	 height: 415,
		        	 x: 0,
		        	 y: 0,
		        	 layout: 'absolute',
		        	 id: 'fSetPrincipal',
		        	 items: [
{
	xtype: 'label',
	text: 'Empresa:',
	x: 0,
	y: 0
}, 
NS.cmbEmpresa,
{
	xtype: 'numberfield',
	x: 0,
	y: 15,
	width: 40,
	value:NS.noEmpresa,
	name: PF + 'txtNoEmpresa',
	id: PF + 'txtNoEmpresa',
	listeners: {
		change: {
			fn: function(box, value){
				var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
				if(Ext.getCmp(PF + 'cmbEmpresa').getValue()==''){
					Ext.Msg.alert("SET","Empresa inexistente.");
					Ext.getCmp(PF + 'txtNoEmpresa').setValue(NS.GI_ID_EMPRESA);
					NS.cmbEmpresa.setValue(NS.GI_ID_EMPRESA);
					NS.cmbEmpresa.setRawValue(NS.GI_NOM_EMPRESA);
				}
			}
		}
	}
},
{
	xtype: 'label',
	text: 'Línea:',
	x: 300,
	y: 0
},   
NS.cmbLinea,
{
	xtype: 'label',
	text: 'Disposición:',
	x: 550,
	y: 0
},
NS.cmbDisp,
{
	xtype: 'label',
	text: 'Fecha de pago:',
	x: 750,
	y: 0
},
NS.txtFechaPago,
{
	xtype: 'label',
	text: 'Gasto:',
	x:0 ,
	y: 55
},
NS.cmbGasto,
{
	xtype: 'numberfield',
	x: 0,
	y: 70,
	width: 60,
	name: PF + 'txtGasto',
	id: PF + 'txtGasto',
	listeners:{
		change:{
			fn: function(box, value){
				var comboValue = BFwrk.Util.updateTextFieldToCombo(
						PF + 'txtGasto', NS.cmbGasto.getId());
			}
		}
	}
},{
	xtype: 'label',
	text: 'Importe:',
	x: 350,
	y: 55
},
{
	xtype: 'textfield',
	x: 350,
	y:70,
	value:"0.00",
	blankText : 'Este campo es requerido',
	width: 120,
	maskRe: /[0-9.,]/,
	name: PF + 'txtImporte',
	id: PF + 'txtImporte',
	listeners:{
		change: {
			fn: function(caja, value) {	
				var pdImp=0;
				if(Ext.getCmp(PF + 'txtImporte').getValue()== ""){
					Ext.getCmp(PF + 'txtImporte').setValue("0.00");
					return;
				}
				pdImp =Ext.getCmp(PF + 'txtImporte').getValue();
				Ext.getCmp(PF + 'txtImporte').setValue(BFwrk.Util.formatNumber(pdImp));
				if(pdImp <= 0)
					return;
				Ext.getCmp(PF + 'txtTotal').setValue(Ext.getCmp(PF + 'txtImporte').getValue());
				Ext.getCmp(PF +'txtImporte').setValue(BFwrk.Util.formatNumber(caja.getValue()));
			}
		}, 
	}
},
{
	xtype: 'label',
	text: 'Porcentaje:',
	x: 515,
	y: 55
},
{
	xtype: 'textfield',
	x: 515,
	y: 70,
	value:'0.00',
	width: 120,
	maskRe: /[0-9.,]/,
	name: PF + 'txtPorcentaje',
	id: PF + 'txtPorcentaje',
	listeners: {
		change: {
			fn: function(caja, value){
				var pdImp=0;
				var pdPor=0;
				var pdDisp=0;
				if(Ext.getCmp(PF + 'txtPorcentaje').getValue()==''){
					Ext.getCmp(PF + 'txtPorcentaje').setValue("0.000000");
					return;
				}
				pdPor = Ext.getCmp(PF + 'txtPorcentaje').getValue();
				if(pdPor > 100){
					Ext.Msg.alert("SET","El porcentaje no debe ser mayor al 100%");
					Ext.getCmp(PF + 'txtPorcentaje').setValue("0.000000");
					Ext.getCmp(PF + 'txtPorcentaje').focus();
					return;
				}
				Ext.getCmp(PF + 'txtPorcentaje').setValue(pdPor);
				if(pdPor <= 0){
					Ext.getCmp(PF + 'txtIva').setValue("0.00");
					Ext.getCmp(PF + 'txtTotal').setValue(Ext.getCmp(PF + 'txtImporte').getValue());
					return;
				}
				pdImp =parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImporte').getValue())) * (pdPor / 100);        				    
				Ext.getCmp(PF + 'txtIva').setValue(BFwrk.Util.formatNumber(pdImp.toFixed(2)));
				Ext.getCmp(PF + 'txtTotal').setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImporte').getValue()))+parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtIva').getValue()))));
				Ext.getCmp(PF +'txtPorcentaje').setValue(Ext.util.Format.number(caja.getValue(), '0,0.00000'));
			}
		}
	}
},

{
	xtype: 'label',
	text: 'I.V.A.:',
	x: 680,
	y: 55
},
{
	xtype: 'textfield',
	x: 680,
	y: 70,
	value:'0.00',
	width: 120,
	name: PF + 'txtIva',
	id: PF + 'txtIva',
	readOnly : true

},

{
	xtype: 'label',
	text: 'Total:',
	x: 850,
	y: 55
},
{
	xtype: 'textfield',
	x: 850,
	y: 70,
	value:'0.00',
	width: 120,
	name: PF + 'txtTotal',
	id: PF + 'txtTotal',
	readOnly : true
},
NS.chkPagar,
NS.gridGastos,
{
	xtype: 'button',
	text: 'Agregar',
	x: 20,
	y: 350,
	width: 70,
	id: PF + 'cmdAgregar',
	name: PF + 'cmdAgregar',
	disabled: false,
	listeners:{
		click:{
			fn: function(btn) {
				NS.agregar();
			}
		}
	}
},
{
	xtype: 'button',
	text: 'Quitar',
	x: 105,
	y: 350,
	width: 70,
	id: PF + 'cmdQuitar',
	name: PF + 'cmdQuitar',
	disabled: false,
	listeners:{
		click:{
			fn: function(btn){
				NS.quitar();
			}
		}
	}
},
{
	xtype: 'button',
	text: 'Aceptar',
	x: 800,
	y: 370,
	width: 90,
	heigth: 40,
	id: PF + 'cmdAceptar',
	name: PF + 'cmdAceptar',
	disabled: false,
	listeners:{
		click:{
			fn: function(btn){
				var records = NS.gridGastos.getSelectionModel().getSelections();
				if(records.length<=0){
					Ext.Msg.alert("SET","Seleccione un registro.");
					return;
				}
				if(!NS.validaDatos()){
					return;
				}
				BFwrk.Util.msgWait('Guardando Gastos...', true);
				var gastos = NS.gridGastos.getSelectionModel()
				.getSelections();
				var matrizGastos = new Array();
				var vdGasto=0,pdComision=0,viBancoC=0;
				for (var i = 0; i < gastos.length; i++) {
					var registrosGastos= {};
					if(gastos[i].get('descripcion').substr(0, 1)=="G")
						vdGasto =BFwrk.Util.unformatNumber(gastos[i].get('gasto'));
					else
						pdComision = BFwrk.Util.unformatNumber(gastos[i].get('gasto'));
					if(gastos[i].get('bancoGastcom')!="")
						viBancoC = gastos[i].get('bancoGastcom');
					registrosGastos.idContrato = gastos[i].get('idContrato');
					registrosGastos.idDisposicion = gastos[i].get('idDisposicion');
					registrosGastos.fecPago = gastos[i].get('fecPago');
					registrosGastos.gasto =vdGasto;
					registrosGastos.comision =pdComision;
					registrosGastos.bancoGastcom = viBancoC;
					registrosGastos.porcentaje =gastos[i].get('comision');
					registrosGastos.clabeBancariaGastcom = gastos[i].get('clabeBancariaGastcom');
					registrosGastos.tipoGasto =gastos[i].get('tipoGasto');
					registrosGastos.pagar = gastos[i].get('pagar');
					matrizGastos[i] = registrosGastos;
				}
				var jsonStringGastos = Ext.util.JSON.encode(matrizGastos);
				GastosFinanciamientoCAction.altaAmortizacion(jsonStringGastos,	
						function(mapResult,e) {
					BFwrk.Util.msgWait('Terminado...',false);
					if (mapResult.msgUsuario !== null
							&& mapResult.msgUsuario !== ''
								&& mapResult.msgUsuario != undefined) {
						for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
							Ext.Msg
							.alert(
									'Información SET',
									''
									+ mapResult.msgUsuario[msg]);
							if(mapResult.result==1){
								NS.limpiar();
							}
						}
					}
				});
			}
		}

	}
},
{
	xtype: 'button',
	text: 'Limpiar',
	x: 900,
	y: 370,
	width: 90,
	heigth: 40,
	id: PF + 'cmdLimpiar',
	name: PF + 'cmdLimpiar',
	disabled: false,
	listeners: {
		click: {
			fn: function(btn) {
				NS.limpiar();
			}
		}
	}
}  
]
		         },
		         { 
		        	 xtype: 'fieldset',
		        	 title: '',
		        	 autowidth: true,
		        	 height: 50,
		        	 x: 0,
		        	 y: 420,
		        	 layout: 'absolute',
		        	 id: 'fSetSec',
		        	 items: [
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Eliminar',
		        	        	 x: 700,
		        	        	 y: 0,
		        	        	 width: 90,
		        	        	 heigth: 40,
		        	        	 id: PF + 'cmdEliminar',
		        	        	 name: PF + 'cmdEliminar',
		        	        	 disabled: false,
		        	        	 listeners: {
		        	        		 click:{
		        	        			 fn: function(btn){
		        	        				 NS.eliminar();
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Imprimir',
		        	        	 x: 800,
		        	        	 y: 0,
		        	        	 width: 90,
		        	        	 heigth: 40,
		        	        	 id: PF + 'cmdImprimir',
		        	        	 name: PF + 'cmdImprimir',
		        	        	 disabled: false,
		        	        	 listeners:{
		        	        		 click:{
		        	        			 fn: function(btn){
		        	        				 if(NS.storeGridGastos.getCount()<=0)
		        	        					 return;
		        	        				 if(NS.cmbLinea.getValue()== ""||NS.cmbDisp.getValue()== ""){ 
		        	        					 Ext.Msg.alert('SET','Seleccione una línea y una disposición');
		        	        					 NS.cmbLinea.focus();
		        	        					 return;
		        	        				 }
		        	        				 var strParams = '';
		        	        				 strParams = '?nomReporte=ReporteGastosCredito';
		        	        				 strParams += '&'
		        	        					 + 'idLinea='
		        	        					 + NS.cmbLinea.getValue();
		        	        				 strParams += '&'
		        	        					 + 'idDisposicion='
		        	        					 + NS.cmbDisp.getValue();
		        	        				 window
		        	        				 .open("/SET/jsp/Reportes.jsp"
		        	        						 + strParams);

		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         ]
		         }
		         ]        
	});
	NS.gastosC.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});