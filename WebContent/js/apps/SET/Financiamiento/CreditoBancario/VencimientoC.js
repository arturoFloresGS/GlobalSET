//GGonzález
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Finaciamiento.VencimientoC');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecHoy = apps.SET.FEC_HOY;
	var vsTipoMenu='',vsPais;
	var vbBanPagoAnt;
	var sel=false;
	var seleccionar=false;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	var lineas = '[{"cod_linea": "1", "nom_linea": "Linea 1"}, {"cod_linea": "2", "nom_linea": "Linea 2"}]';
	var lineasStore = new Ext.data.JsonStore({
		fields: [
		         {type: 'string', name: 'cod_linea'},
		         {type: 'string', name: 'nom_linea'}
		         ]
	});
	stringToDate=function(fecha){
		var fec = fecha.split("/");
		var dt = new Date(parseInt(fec[2], 10),
				parseInt(fec[1], 10) - 1, parseInt(fec[0], 10));
		return dt;
	}
	lineasStore.loadData(Ext.decode(lineas));
	/*if (vsTipoMenu == "A"){
		Ext.getCmp(PF + 'lblRenta').setVisible(true);
		Ext.getCmp(PF + 'labRenta').setVisible(true);
	}*/
	//variable global cargada desde la pantalla de créditos bancarios
	if(NS.noEmpresa==undefined)
		NS.noEmpresa=NS.GI_ID_EMPRESA;
	if(NS.nomEmpresa ==undefined)
		NS.nomEmpresa=NS.GI_NOM_EMPRESA;
	NS.storeSelectCapital = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams:{
			psLinea:'',
			piDisposicion:0
		},
		paramOrder : ['psLinea','piDisposicion'],
		directFn : VencimientoFinanciamientoCAction.storeSelectCapital,
		idProperty: 'capital',
		fields : [ {
			name : 'capital'
		} ],
	});
	NS.storePrimerAmortAct = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams:{
			psLinea:'',
			piDisposicion:0
		},
		paramOrder : ['psLinea','piDisposicion'],
		directFn : VencimientoFinanciamientoCAction.selectPrimerAmortAct,
		fields: [
		         {name: 'idAmortizacion'},
		         {name: 'fecInicio'},
		         ],
	});
	NS.cmdPagoAnticipado = new Ext.Button({
		xtype : 'button',
		text: 'Pago anticipado',
		x: 630,
		y: 450,
		width: 90,
		id: PF + 'cmdPagoAnticipado',
		name: PF + 'cmdPagoAnticipado',
		disabled: true,
		hidden:true,
		listeners:{
			click:{
				fn: function(btn){
					var registros = NS.gridVencimiento.getSelectionModel().getSelections();
					if(registros.length<=0){
						Ext.Msg.alert("SET","Seleccione un registro para realizar el pago anticipado.");
						return;
					}
					if(registros.length>1){
						Ext.Msg.alert("SET","Seleccione un solo registro para realizar el pago anticipado.");
						return;
					}
					NS.storeSelectCapital.load({
						params:{
							psLinea:registros[0].get('idContrato'),
							piDisposicion:parseInt(registros[0].get('idDisposicion'))
						},
						callback:function(records){
							if(records<=0){
								Ext.Msg.alert("SET","No existen amortizaciones de capital para pagar.");
								return;
							}
							else{
								if(records[0].get('capital')==0){
									Ext.Msg.alert("SET","No existen amortizaciones de capital para pagar.");
									return;
								}
								NS.winPagoAnticipado.show();
								NS.txtEmpresaPago.setValue(NS.cmbEmpresa.getValue());
								NS.txtLineaPago.setValue(registros[0].get('idContrato'));
								NS.txtDisposicionPago.setValue(registros[0].get('idDisposicion'));
								NS.txtSaldoActualPago.setValue(BFwrk.Util.formatNumber(records[0].get('capital')));
								NS.txtImportePago.setValue(BFwrk.Util.formatNumber(records[0].get('capital')));
								NS.storePrimerAmortAct.load({
									params : {
										psLinea:registros[0].get('idContrato'),
										piDisposicion:parseInt(registros[0].get('idDisposicion'))
									},
									callback : function(
											records, operation,
											success) {
										if(records.length>0){
											NS.txtFecInicio.setValue(records[0].get('fecInicio'));
											NS.txtAmortizacion.setValue(records[0].get('idAmortizacion'));
											NS.txtAmortizacion.setDisabled(true);
										}
										NS.txtFechaVto.setValue(NS.fecHoy);
										vbBanPagoAnt=true;
									}});
							}
						}
					});
				}
			}
		}
	});
	if(vsTipoMenu == ""||vsTipoMenu == "B")
		Ext.getCmp(PF + 'cmdPagoAnticipado').setVisible(true);
	NS.storeConfiguraSetTodos = new  Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: AltaFinanciamientoAction.consultarConfiguraSetTodos,
		idProperty: 'id',
		fields: [
		         {name: 'id'},
		         {name: 'valorConfiguraSet'}
		         ],
	});
	//load de la pantalla
	//carga de store
	NS.storeConfiguraSetTodos.load({
		callback:function(records){
			NS.proyecto=records[0].get('valorConfiguraSet');
			if(NS.proyecto=='CIE'){
				/*If vsTipoMenu = "A" Then
		            labFechaDocto.Visible = True
		            txtFechaDocto.Visible = True
		            labFactura.Visible = True
		            txtFactura.Visible = True
		        End If
				 */
				Ext.getCmp(PF + 'lblFinanciamiento').setVisible(false);
				Ext.getCmp(PF + 'txtFinanciamiento').setVisible(false);
				Ext.getCmp(PF + 'cmbFinanciamiento').setVisible(false);
				Ext.getCmp(PF + 'lblBanco').setVisible(false);
				Ext.getCmp(PF + 'txtBanco').setVisible(false);
				Ext.getCmp(PF + 'cmbBanco').setVisible(false);
				Ext.getCmp(PF + 'lblDivisa').setPosition(800,0);
				Ext.getCmp(PF + 'txtDivisa').setPosition(800,15);
				Ext.getCmp(PF + 'cmbDivisa').setPosition(845,15);
				Ext.getCmp(PF + 'frmPagoDivisa').setVisible(false);
				Ext.getCmp(PF + 'cmdCancelar').setVisible(false);
			}
		}
	});
	NS.txtTipoCambio= new Ext.form.TextField({
		id: PF + 'txtTipoCambio',
		name: PF + 'txtTipoCambio',
		x: 220,
		y: 20,
		width:70,
		value: '0.00',
	});
	NS.txtFecIni = new Ext.form.DateField({
		id: PF+'txtFecIni',
		name: PF+'txtFecIni',
		format: 'd/m/Y',
		x: 0,
		y: 15,
		width: 110,
		listeners:{
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			blur:function(datefield){
				if(NS.txtFecIni.getValue()=='')
					datefield.setValue(NS.fecHoy);
			}
		}
	});
	NS.storeCmbPais = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		directFn : VencimientoFinanciamientoCAction.obtenerPaisVenc,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var recordsStorePais = NS.storeCmbPais.recordType;	
				var todos = new recordsStorePais({
					idStr: '0',
					descripcion: '****TODOS****'
				});
				NS.storeCmbPais.insert(0,todos);
				NS.cmbPais.setValue('0');
				NS.cmbPais.setRawValue('****TODOS****');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbPais,
		msg : "Cargando..."
	});
	NS.storeCmbPais.load();
	NS.txtPais= new Ext.form.TextField({
		id: PF + 'txtPais',
		name: PF + 'txtPais',
		x: 220,
		y: 15,
		width: 40,
		tabIndex: 0,
		value: '0',
		listeners:{
			change:function(caja,value){
				NS.txtPais.setValue(NS.txtPais.getValue().toUpperCase());
				var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtPais', NS.cmbPais.getId());
				if(NS.cmbPais.getValue()==''){
					Ext.Msg.alert("SET","País inexistente.");
					NS.txtPais.setValue('0');
					NS.cmbPais.setValue('0');
					NS.cmbPais.setRawValue('****TODOS****');
				}
			},
			blur:function(caja,value){
				if(caja.getValue()==""){
					NS.txtPais.setValue('0');
					NS.cmbPais.setValue('0');
					NS.cmbPais.setRawValue('****TODOS****');
				}
			}
		}
	});
	NS.cmbPais= new Ext.form.ComboBox({
		store: NS.storeCmbPais,
		name: PF + 'cmbPais',
		id: PF + 'cmbPais',
		x: 265,
		y: 15,
		width:150,
		typeAhead: true,
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: 'Seleccione un pais',
		triggerAction: 'all',
		displayField: 'descripcion',
		valueField: 'idStr',
		mode: 'local',
		tabIndex: 1,
		listeners:{
			select:{
				fn:function(combo,valor){
					var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtPais', NS.cmbPais.getId());
					if(combo.getValue()=='0')
						vsPais = "N";
					else if(combo.getValue()=='MX')
						vsPais = "N";
					else
						vsPais = "E";
					NS.cmbBanco.reset();
					NS.storeCmbBanco.load({
						params:{
							psNac:'',
							psTipoMenu:vsTipoMenu,
						}
					});  
				}
			},
			blur:function(caja,value){
				if(caja.getValue()==""){
					NS.txtPais.setValue('0');
					NS.cmbPais.setValue('0');
					NS.cmbPais.setRawValue('****TODOS****');
				}
			}
		}
	});
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
	NS.cmbEmpresa= new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa,
		name: PF + 'cmbEmpresa',
		id: PF + 'cmbEmpresa',
		x: 505,
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
					NS.storeDisposiciones.removeAll();
					NS.storeCmbContratos.removeAll();
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storeCmbContratos,
						msg : "Cargando..."
					});
					NS.storeCmbContratos.load({
						params:{
							piEmpresa:parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue())
						}
					});
				}
			}
		}
	});
	NS.storeCmbDivisa = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {noEmpresa:NS.noEmpresa},
		paramOrder : ['noEmpresa'],
		directFn : VencimientoFinanciamientoCAction.obtenerDivisas,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var recordsStoreDivisa = NS.storeCmbDivisa.recordType;	
				var todas = new recordsStoreDivisa({
					idStr: '0',
					descripcion: '****TODAS****'
				});
				NS.storeCmbDivisa.insert(0,todas);
				NS.cmbDivisa.setValue('0');
				NS.cmbDivisa.setRawValue('****TODAS****');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbDivisa,
		msg : "Cargando..."
	});
	NS.storeCmbDivisa.baseParams.noEmpresa=parseInt(NS.noEmpresa);
	NS.storeCmbDivisa.load();
	NS.txtDivisa= new Ext.form.TextField({
		id: PF + 'txtDivisa',
		name: PF + 'txtDivisa',
		x: 650,
		y: 80,
		width:40,
		value: '0',
		listeners:{
			change:function(caja,valor){
				NS.txtDivisa.setValue(NS.txtDivisa.getValue().toUpperCase());
				var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtDivisa', NS.cmbDivisa.getId());
				if(NS.cmbDivisa.getValue()==''){
					Ext.Msg.alert("SET","Divisa inexistente.");
					NS.txtDivisa.setValue('0');
					NS.cmbDivisa.setValue('0');
					NS.cmbDivisa.setRawValue('****TODAS****');
				}
			}
		}
	});
	NS.cmbDivisa= new Ext.form.ComboBox({
		store: NS.storeCmbDivisa,
		name: PF + 'cmbDivisa',
		id: PF + 'cmbDivisa',
		x: 695,
		y: 80,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all',
		width:150,
		displayField: 'descripcion',
		valueField: 'idStr',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtDivisa', NS.cmbDivisa.getId());
				}
			}
		}
	});
	NS.storeCmbContratos = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {piEmpresa:NS.GI_ID_EMPRESA},
		paramOrder : ['piEmpresa'],
		directFn : VencimientoFinanciamientoCAction.obtenerContratos,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var recordsStoreLinea = NS.storeCmbContratos.recordType;	
				var todas = new recordsStoreLinea({
					idStr: '0',
					descripcion: '****TODAS****'
				});
				NS.storeCmbContratos.insert(0,todas);
				NS.cmbLinea.setValue('0');
				NS.cmbLinea.setRawValue('****TODAS****');
			}
		}

	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbContratos,
		msg : "Cargando..."
	});
	NS.storeCmbContratos.load({
		params:{
			piEmpresa:parseInt(NS.noEmpresa)
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
		listeners:{
			load:function(records){
				var recordsStoreDisp = NS.storeDisposiciones.recordType;	
				var todas = new recordsStoreDisp({
					id: 0,
					descripcion: '****TODAS****'
				});
				NS.storeDisposiciones.insert(0,todas);
				NS.cmbDispo.setValue(0);
				NS.cmbDispo.setRawValue('****TODAS****');
			}
		}
	});
	NS.cmbLinea= new Ext.form.ComboBox({
		store: NS.storeCmbContratos,
		name: PF + 'cmbLinea',
		id: PF + 'cmbLinea',
		x: 0,
		emptyText: 'Seleccione una línea',
		triggerAction: 'all',
		y: 80,
		width:150,
		displayField: 'descripcion',
		valueField: 'idStr',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					NS.cmbDispo.reset();
					NS.storeDisposiciones.removeAll();
					NS.storeDisposiciones.baseParams.psIdContrato =Ext.getCmp(PF + 'cmbLinea').getValue();
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

	NS.cmbDispo= new Ext.form.ComboBox({
		store: NS.storeDisposiciones,
		name: PF + 'cmbDispo',
		id: PF + 'cmbDispo',
		x: 200,
		y: 80,
		emptyText: 'Seleccione una disposición',
		triggerAction: 'all',
		width:150,
		value:0,
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
	});
	NS.validaDatos=function(){
		var band=false;
		if (Ext.getCmp(PF + 'txtFecIni').getValue() == "") {
			Ext.Msg.alert("SET","Seleccione una fecha.");
			Ext.getCmp(PF + 'txtFecIni').setValue(NS.fecHoy);
			Ext.getCmp(PF + 'txtFecIni').focus();
			return;
		}
		else if (Ext.getCmp(PF + 'cmbPais').getValue() == ""||Ext.getCmp(PF + 'txtPais').getValue()=='') {
			Ext.Msg.alert("SET","Seleccione un país.");
			Ext.getCmp(PF + 'txtPais').focus();
			return;
		}
		else if (Ext.getCmp(PF + 'cmbEmpresa').getValue() == ""||Ext.getCmp(PF + 'txtNoEmpresa').getValue()=='') {
			Ext.Msg.alert("SET","Seleccione una empresa.");
			Ext.getCmp(PF + 'txtNoEmpresa').focus();
			return;
		}
		else if (Ext.getCmp(PF + 'txtBanco').getValue() == "") {
			Ext.Msg.alert("SET","Seleccione un banco.");
			Ext.getCmp(PF + 'txtBanco').focus();
			return;
		}
		else if (Ext.getCmp(PF + 'txtFinanciamiento').getValue() == "") {
			Ext.Msg.alert("SET","Seleccione un tipo de financiamiento.");
			Ext.getCmp(PF + 'txtFinanciamiento').focus();
			return;
		}
		else if (Ext.getCmp(PF + 'cmbDivisa').getValue() == ""||Ext.getCmp(PF + 'txtDivisa').getValue() == "") {
			Ext.Msg.alert("SET","Seleccione una divisa.");
			Ext.getCmp(PF + 'cmbDivisa').focus();
			return;
		}
		band=true;
		return band;
	}
	NS.cancelar=function(){
		Ext.getCmp(PF + 'txtDivisaPag').setValue("");
		Ext.getCmp(PF + 'cmbDivisaPag').reset();
		Ext.getCmp(PF + 'txtTipoCambio').setValue("0.00");
		Ext.getCmp(PF + 'txtBancoPag').setValue("");
		Ext.getCmp(PF + 'cmbBancoPag').reset();
		Ext.getCmp(PF + 'cmbChequeraPag').setValue("");
		//Ext.getCmp(PF + 'txtFechaDocto').setValue("Pzo: Z01 Pago inmediato a la entrega de documentos");
		//Ext.getCmp(PF + 'txtFactura').setValue("");
	}
	NS.storeSelectTipoCambio = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psDivisa : 'DLS'
		},
		paramOrder : [ 'psDivisa'],
		directFn : AltaFinanciamientoAction.obtenerTipoCambio,
		fields : [ {
			name : 'valor'
		},  ],
	});
	NS.txtBanco= new Ext.form.TextField({
		id: PF + 'txtBanco',
		name: PF + 'txtBanco',
		x: 800,
		y: 15,
		width:40,
		listeners:{
			change:function(caja,valor){
				var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
				if(NS.cmbBanco.getValue()==''){
					Ext.Msg.alert("SET","Banco inexistente.");
					NS.txtBanco.setValue(0);
					NS.cmbBanco.setValue(0);
					NS.cmbBanco.setRawValue('****TODOS****');
				}
			}
		}
	});
	NS.storeCmbBanco = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psNac : '',
			psTipoMenu : '',
		},
		paramOrder : [ 'psNac', 'psTipoMenu' ],
		directFn : VencimientoFinanciamientoCAction.obtenerBancoVenci,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners : {
			load : function(s, records) {
				var recordsStore = NS.storeCmbBanco.recordType;	
				var todos = new recordsStore({
					id: 0,
					descripcion: '****TODOS****'
				});
				NS.storeCmbBanco.insert(0,todos);
				NS.txtBanco.setValue(0);
				NS.cmbBanco.setValue(0);
				NS.cmbBanco.setRawValue('****TODOS****');
			}
		}
	});
	NS.storeCmbBanco.load();
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbBanco,
		msg : "Cargando..."
	});
	NS.cmbBanco= new Ext.form.ComboBox({
		store: NS.storeCmbBanco,
		name: PF + 'cmbBanco',
		id: PF + 'cmbBanco',
		displayField: 'descripcion',
		valueField: 'id',
		emptyText: 'Seleccione un banco',
		x: 845,
		y: 15,
		width:150,
		listeners:{
			select:function(combo,value){
				var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBanco.getId());
			}
		}
	});
	NS.txtFinanciamiento= new Ext.form.TextField({
		id: PF + 'txtFinanciamiento',
		name: PF + 'txtFinanciamiento',
		x: 400,
		y: 80,
		width:40,
		value: '0',
		listeners:{
			change:function(caja,valor){
				var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtFinanciamiento', NS.cmbFinanciamiento.getId());
				if(NS.cmbFinanciamiento.getValue()==''){
					Ext.Msg.alert("SET","Tipo de financiamiento inexistente.");
					NS.txtFinanciamiento.setValue(0);
					NS.cmbFinanciamiento.setValue(0);
					NS.cmbFinanciamiento.setRawValue('****TODOS****');
				}
			}
		}
	});
	NS.storeCmbTipoFinan = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psTipoFinan : '',
			pbTipoContrato : false,
		},
		paramOrder : [ 'psTipoFinan', 'pbTipoContrato' ],
		directFn : AltaFinanciamientoAction.obtenerTipoContratos,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners : {
			load : function(s, records) {
				var recordsStore = NS.storeCmbTipoFinan.recordType;	
				var todos = new recordsStore({
					id: 0,
					descripcion: '****TODOS****'
				});
				NS.storeCmbTipoFinan.insert(0,todos);
				NS.cmbFinanciamiento.setValue(0);
				NS.cmbFinanciamiento.setRawValue('****TODOS****');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbTipoFinan,
		msg : "Cargando..."
	});
	NS.storeCmbTipoFinan.load();
	NS.cmbFinanciamiento= new Ext.form.ComboBox({
		store: NS.storeCmbTipoFinan,
		name: PF + 'cmbFinanciamiento',
		id: PF + 'cmbFinanciamiento',
		x: 445,
		y: 80,
		displayField: 'descripcion',
		valueField: 'id',
		width:150,
		emptyText: 'Seleccione un tipo',
		triggerAction: 'all',
		value:0,
		listeners:{
			select:function(combo, value){
				var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtFinanciamiento', NS.cmbFinanciamiento.getId());
			}
		}
	});
	//
	NS.buscar=function(){
		Ext.getCmp(PF + 'labCap').setValue('0.00');
		Ext.getCmp(PF + 'labInt').setValue('0.00');
		//Ext.getCmp(PF + 'labRenta').setValue('0.00');
		Ext.getCmp(PF + 'labIva').setValue('0.00');
		Ext.getCmp(PF + 'labPagoTotal').setValue('0.00');
		NS.cancelar();
		if(!NS.validaDatos()){
			return;
		}
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeSelectTipoCambio,
			msg : "Cargando..."
		});
		NS.storeSelectTipoCambio.load({
			callback:function(records){
				if(records.length<=0){
					Ext.Msg.alert("SET","El tipo de cambio no esta dado de alta");
					Ext.getCmp(PF + 'txtTipoCambio').setValue('0.00');
				}else{
					Ext.getCmp(PF + 'txtTipoCambio').setValue(records[0].get('valor'));
				}
				/*
				If vsTipoMenu = "A" Then
			        Call BuscaArrendamientos
			        Screen.MousePointer = 0
			        Exit Sub
			    End If
				 */
				if(vbBanPagoAnt)
					Ext.getCmp(PF + 'txtFecIni').setValue(Ext.getCmp(PF + 'txtFechaVto').getValue());
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeVencimiento,
					msg : "Cargando..."
				});
				NS.storeVencimiento.load({
					params:{
						psFecIni:BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecIni').getValue()),
						psPais:Ext.getCmp(PF + 'txtPais').getValue(),
						plEmpresa:parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue()),
						piBanco:parseInt(Ext.getCmp(PF + 'txtBanco').getValue()),
						psLinea:Ext.getCmp(PF + 'cmbLinea').getValue(),
						piTipoFinan:parseInt(Ext.getCmp(PF + 'txtFinanciamiento').getValue()),
						psDivisa:Ext.getCmp(PF + 'txtDivisa').getValue(),
						plCredito:parseInt(Ext.getCmp(PF + 'cmbDispo').getValue()),
					}
				});
			}
		});
	};
	NS.limpiar=function(){
		Ext.getCmp(PF + 'btnImprimir').setDisabled(true);
		Ext.getCmp(PF + 'btnAceptar').setDisabled(true);
		Ext.getCmp(PF + 'cmdPagoAnticipado').setDisabled(true);
		Ext.getCmp(PF +'txtFecIni').setValue(NS.fecHoy);
		Ext.getCmp(PF +'cmbEmpresa').setRawValue(NS.GI_NOM_EMPRESA);
		Ext.getCmp(PF +'cmbEmpresa').setValue(NS.GI_ID_EMPRESA);
		Ext.getCmp(PF +'txtNoEmpresa').setValue(NS.GI_ID_EMPRESA);
		Ext.getCmp(PF +'txtPais').setValue("0");
		Ext.getCmp(PF +'txtBanco').setValue("0");
		Ext.getCmp(PF +'cmbPais').setRawValue("****TODOS****");
		Ext.getCmp(PF +'cmbPais').setValue("0");
		Ext.getCmp(PF +'cmbBanco').setRawValue("****TODOS****");
		Ext.getCmp(PF +'cmbBanco').setValue("0");
		Ext.getCmp(PF +'cmbLinea').setRawValue("****TODAS****");
		Ext.getCmp(PF +'cmbLinea').setValue("0");
		NS.cmbDispo.reset();
		Ext.getCmp(PF +'txtFinanciamiento').setValue('0');
		Ext.getCmp(PF +'cmbFinanciamiento').setValue("****TODOS****");
		Ext.getCmp(PF +'txtDivisa').setValue("0");
		Ext.getCmp(PF +'cmbDivisa').setRawValue("****TODAS****");
		Ext.getCmp(PF +'cmbDivisa').setValue("0"); 
		NS.gridVencimiento.removeAll();
		NS.storeVencimiento.removeAll();
		NS.storeDisposiciones.removeAll();
		NS.cancelar();
		Ext.getCmp(PF +'labCap').setValue("0.00");    
		Ext.getCmp(PF +'labInt').setValue("0.00");  
		Ext.getCmp(PF +'labRenta').setValue("0.00"); 
		Ext.getCmp(PF +'labIva').setValue("0.00");
		Ext.getCmp(PF +'labPagoTotal').setValue("0.00");
	}
	NS.calculaTotales=function(){
		var vdTotCap=0,vdTotIva=0,vdTotPago=0,vdTotInt=0,vdTotRenta=0;
		var registros = NS.gridVencimiento.getSelectionModel().getSelections();
		for(var i=0;i<registros.length;i++){
			if(registros[i].get('valorPago')=='-'){
				vdTotCap = vdTotCap - registros[i].get('capital');
				vdTotIva = vdTotIva - registros[i].get('iva');
				vdTotPago = vdTotPago - registros[i].get('totalPago');
			}
			else{
				vdTotCap = vdTotCap +registros[i].get('capital');
				vdTotIva = vdTotIva + registros[i].get('iva');
				vdTotPago = vdTotPago + registros[i].get('totalPago');
			}
			vdTotInt = vdTotInt + registros[i].get('interes');
			vdTotRenta = vdTotRenta + registros[i].get('renta');
		}
		Ext.getCmp(PF + 'labCap').setValue(BFwrk.Util.formatNumber(vdTotCap.toFixed(2)));
		Ext.getCmp(PF + 'labInt').setValue(BFwrk.Util.formatNumber(vdTotInt.toFixed(2)));
		//NS.labRenta.setValue(BFwrk.Util.formatNumber(vdTotRenta.toFixed(2)));
		Ext.getCmp(PF + 'labIva').setValue(BFwrk.Util.formatNumber(vdTotIva.toFixed(2)));
		if(vsTipoMenu == "A")
			Ext.getCmp(PF + 'labPagoTotal').setValue(BFwrk.Util.formatNumber((vdTotRenta + vdTotIva).toFixed(2)));
		else
			Ext.getCmp(PF + 'labPagoTotal').setValue(BFwrk.Util.formatNumber((vdTotCap +vdTotInt +vdTotIva).toFixed(2)));
	}
	/*Inicio del grid*/
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false,
		listeners:{
			rowselect:{
				fn : function(selectionModel, rowIndex, record) {
					NS.calculaTotales();
					NS.cmbDivisaPag.reset()
					NS.cmbBancoPag.reset();
					NS.storeCmbDivisaPag.baseParams.bRestringido = false;
					//NS.storeCmbDivisaPag.baseParams.noEmpresa = parseInt(NS.noEmpresa);
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storeCmbDivisaPag,
						msg : "Cargando..."
					});
					NS.storeCmbDivisaPag.load();
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storeBancoPago,
						msg : "Cargando..."
					});
					NS.storeBancoPago.load({params:{
						plEmpresa:parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue())}
					});
				}
			},
			beforerowselect : {
				fn : function(selectionModel, rowIndex, record) {
					if(!this.isSelected(rowIndex)){
						if(NS.storeVencimiento.getCount()<1)
							return;
						if(rowIndex>0){
							var records=NS.storeVencimiento.getAt(rowIndex);
							var fec1,fec2;
							var fec =records.get('fecVen').split("/");
							var dt = new Date(parseInt(fec[2], 10),
									parseInt(fec[1], 10) - 1,
									parseInt(fec[0], 10));
							fec1=dt;
							var record2=NS.storeVencimiento.getAt(rowIndex-1);
							fec=record2.get('fecVen').split("/");
							dt = new Date(parseInt(fec[2], 10),
									parseInt(fec[1], 10) - 1,
									parseInt(fec[0], 10));
							fec2=dt;
							if(!this.isSelected(rowIndex-1)&&fec1>fec2){
								Ext.Msg.alert("SET","No se puede pagar una amortizacion mayor sin vencer las anteriores.");
								sel=true;
								return false;
							}
						}
					}
					else{
						return false;
					}
				}
			},
			rowdeselect : {
				fn : function(selectionModel, rowIndex, record) {
					if(NS.storeVencimiento.getCount()<1)
						return;
					if(rowIndex<NS.storeVencimiento.getCount()-1){
						var fec1,fec2;
						var fec =record.get('fecVen').split("/");
						var dt = new Date(parseInt(fec[2], 10),
								parseInt(fec[1], 10) - 1,
								parseInt(fec[0], 10));
						fec1=dt;
						var record2=NS.storeVencimiento.getAt(rowIndex+1);
						fec=record2.get('fecVen').split("/");
						dt = new Date(parseInt(fec[2], 10),
								parseInt(fec[1], 10) - 1,
								parseInt(fec[0], 10));
						fec2=dt;
						if(this.isSelected(rowIndex+1)&&fec1<fec2){
							Ext.Msg.alert("SET","Desmarque la Amorización Mayor");
							sel=true;
							return false;
						}
						NS.calculaTotales();
						NS.cmbDivisaPag.reset()
						NS.cmbBancoPag.reset();
						NS.storeCmbDivisaPag.baseParams.bRestringido = false;
						//NS.storeCmbDivisaPag.baseParams.noEmpresa = parseInt(NS.noEmpresa);
						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeCmbDivisaPag,
							msg : "Cargando..."
						});
						NS.storeCmbDivisaPag.load();
						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeBancoPago,
							msg : "Cargando..."
						});
						NS.storeBancoPago.load({params:{
							plEmpresa:parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue())}
						});
					}
				}
			}
		}
	});
	NS.columnasVencimiento = new Ext.grid.ColumnModel([
	                                                   NS.columnaSeleccion,
	                                                   {header: '', width: 0, dataIndex: 'renta', sortable: true ,hidden : true, hideable : false,renderer  : function(value) {
	                                                	   return Ext.util.Format.number(value, '0,0.00');
	                                                   },},
	                                                   {header: 'Vencimiento', width: 120, dataIndex: 'fecVen'},
	                                                   {header: 'Empresa', width: 200, dataIndex: 'descEmpresa'},
	                                                   {header: '',hidden : true, hideable : false, width: 0, dataIndex: 'descEmpresa', sortable: true},
	                                                   {header: '', width: 0, dataIndex: 'institucion', hidden : true, hideable : false},
	                                                   {header: 'Banco Otorgante', width: 160, dataIndex: 'descInstitucion'},
	                                                   {header: 'Monto Disposición', width: 120, dataIndex: 'saldo',align: 'right',renderer  : function(value) {
	                                                	   return Ext.util.Format.number(value, '0,0.00');
	                                                   },},
	                                                   {header: '', width: 0, dataIndex: 'formaPago', hidden : true, hideable : false},
	                                                   {header: '', width: 0, dataIndex: 'equivalente',hidden : true, hideable : false},
	                                                   {header: '', width: 0, dataIndex: 'gpoEmpresa', hidden : true, hideable : false},
	                                                   {header: '', width: 0, dataIndex: 'descPago', hidden : true, hideable : false},
	                                                   {header: '', width: 0, dataIndex: 'valorPago',hidden : true, hideable : false},
	                                                   {header: '', width: 0, dataIndex: 'bancoBenef',hidden : true, hideable : false},
	                                                   {header: '', width: 0, dataIndex: 'chequeraBenef',hidden : true, hideable : false},
	                                                   {header: 'Divisa', width: 70, dataIndex: 'divisa'},
	                                                   {header: 'Línea', width: 120, dataIndex: 'idContrato'},
	                                                   {header: 'Disposición', width: 80, dataIndex: 'idDisposicion'},
	                                                   {header: 'Amortización', width: 80, dataIndex: 'idAmortizacion'},
	                                                   {header: 'Capital', width: 120, dataIndex: 'capital',align: 'right',renderer  : function(value) {
	                                                	   return Ext.util.Format.number(value, '0,0.00');
	                                                   },},
	                                                   {header: 'Interes', width: 120, dataIndex: 'interes',align: 'right',renderer  : function(value) {
	                                                	   return Ext.util.Format.number(value, '0,0.00');
	                                                   },},
	                                                   {header: 'I.V.A', width: 120, dataIndex: 'iva', align: 'right',renderer  : function(value) {
	                                                	   return Ext.util.Format.number(value, '0,0.00');
	                                                   },},
	                                                   {header: 'Total pago', width: 120, dataIndex: 'totalPago', align: 'right',renderer  : function(value) {
	                                                	   return Ext.util.Format.number(value, '0,0.00')
	                                                   },},
	                                                   {header: '', width: 0, dataIndex: 'fecIni', hidden : true, hideable : false},
	                                                   ]);
	NS.storeVencimiento = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			psFecIni:'',
			psPais:'',
			plEmpresa:0,
			piBanco:0,
			psLinea:'',
			piTipoFinan:0,
			psDivisa:'',
			plCredito:0,
		},
		root: '',
		paramOrder: ['psFecIni','psPais','plEmpresa','piBanco','psLinea','piTipoFinan','psDivisa','plCredito'],
		directFn: VencimientoFinanciamientoCAction.selectMovimientoAzt, 
		fields: [
		         {name: 'idContrato'},
		         {name: 'idDisposicion'},
		         {name: 'idAmortizacion'},
		         {name: 'capital'},
		         {name: 'interes'},
		         {name: 'renta'},
		         {name: 'iva'},
		         {name: 'fecVen'},
		         {name: 'empresa'},
		         {name: 'descEmpresa'},
		         {name: 'institucion'},
		         {name: 'descInstitucion'},
		         {name: 'saldo'},
		         {name: 'formaPago'},
		         {name: 'equivalente'},
		         {name: 'gpoEmpresa'},
		         {name: 'descPago'},
		         {name: 'valorPago'},
		         {name: 'bancoBenef'},
		         {name: 'chequeraBenef'},
		         {name: 'divisa'},
		         {name: 'fecIni'},
		         {name: 'totalPago'},
		         ],
		         listeners: {
		        	 load: function(s, records) {
		        		 var vdTotIva=0,vdTotPago=0,vdCapital=0,vdTotInt=0;
		        		 if(records.length == null || records.length <= 0){
		        			 Ext.getCmp(PF + 'btnImprimir').setDisabled(true);
		        			 Ext.getCmp(PF + 'btnAceptar').setDisabled(true);
		        			 Ext.getCmp(PF + 'cmdPagoAnticipado').setDisabled(true);
		        			 Ext.Msg.alert('SET', 'No existen registros');
		        		 }
		        		 else{
		        			 Ext.getCmp(PF + 'btnImprimir').setDisabled(false);
		        			 Ext.getCmp(PF + 'btnAceptar').setDisabled(false);
		        			 Ext.getCmp(PF + 'cmdPagoAnticipado').setDisabled(false);
		        			 for(var  i=0;i<records.length;i++){
		        				 vdTotIva+=records[i].get('iva');
		        				 vdTotPago+=records[i].get('totalPago');
		        				 vdCapital+=records[i].get('capital');
		        				 vdTotInt+=records[i].get('interes');
		        			 }
		        			 Ext.getCmp(PF + 'labCap').setValue(BFwrk.Util.formatNumber(vdCapital.toFixed(2)));
		        			 Ext.getCmp(PF + 'labInt').setValue(BFwrk.Util.formatNumber(vdTotInt.toFixed(2)));
		        			 Ext.getCmp(PF + 'labIva').setValue(BFwrk.Util.formatNumber(vdTotIva.toFixed(2)));
		        			 Ext.getCmp(PF + 'labPagoTotal').setValue(BFwrk.Util.formatNumber(vdTotPago.toFixed(2)));
		        		 }
		        		 if(seleccionar){
		        			 var registros=NS.storeVencimiento.getCount();
		        			 for (var i=0;i<registros;i++){
		        				 var record=NS.storeVencimiento.getAt(i);
		        				 if(record.data.capital!=0||record.data.interes!=0){
		        					 NS.gridVencimiento.getSelectionModel().selectRow(i,true);
		        				 }
		        			 }
		        			 seleccionar=false;
		        		 }
		        	 }
		         },
		         exception:function(misc) {
		        	 Ext.Msg.alert('SET', 'Error al cargar los datos en el grid, <br> Verificar la conexión');
		         }
	});
	NS.gridVencimiento = new Ext.grid.GridPanel({
		store: NS.storeVencimiento,
		id: 'gridVencimiento',
		autowidth: true,
		height:300,
		x: 0,
		y: 0,
		frame:true,
		stripeRows: true,
		columnLines: true,
		cm: NS.columnasVencimiento,
		sm: NS.columnaSeleccion,
	});
	/*Fin del grid*/
	//Inicia NS.winPagoAnticipado
	NS.lblEmpresaPago = new Ext.form.Label({
		text : 'Empresa:',
		x : 10,
		y : 10
	});
	NS.txtEmpresaPago= new Ext.form.TextField({
		id: PF + 'txtEmpresaPago',
		name: PF + 'txtEmpresaPago',
		x: 10,
		y: 25,
		disabled:true,
		width:460,
		value: '',
	});
	NS.lblDisposicionPago = new Ext.form.Label({
		text : 'Línea:',
		x : 10,
		y : 55
	});
	NS.txtLineaPago= new Ext.form.TextField({
		id: PF + 'txtLineaPago',
		name: PF + 'txtLineaPago',
		x: 10,
		y: 70,
		disabled:true,
		width:150,
		value: '',
	});
	NS.lblLineaPago = new Ext.form.Label({
		text : 'Disposición:',
		x : 200,
		y : 55
	});
	NS.txtDisposicionPago= new Ext.form.TextField({
		id: PF + 'txtDisposicionPago',
		name: PF + 'txtDisposicionPago',
		x: 200,
		y: 70,
		disabled:true,
		width:100,
		value: '',
	});
	NS.lblSaldoActualPago = new Ext.form.Label({
		text : 'Saldo Actual:',
		x : 340,
		y : 55
	});
	NS.txtSaldoActualPago= new Ext.form.TextField({
		id: PF + 'txtSaldoActualPago',
		name: PF + 'txtSaldoActualPago',
		x: 340,
		y: 70,
		width:130,
		disabled:true,
		value: '',
	});
	NS.lblImportePago = new Ext.form.Label({
		text : 'Saldo Actual:',
		x : 10,
		y : 105
	});
	NS.txtImportePago= new Ext.form.TextField({
		id: PF + 'txtImportePago',
		name: PF + 'txtImportePago',
		x: 10,
		y: 120,
		width:130,
		value: '',
		listeners:{
			change:function(){
				var vdMonto;
				if(NS.txtImportePago.getValue()=='')
					NS.txtImportePago.setValue("0.00");
				if(parseFloat(BFwrk.Util.unformatNumber(NS.txtImportePago.getValue()))>
				parseFloat(BFwrk.Util.unformatNumber(NS.txtSaldoActualPago.getValue()))&&
				(parseFloat(BFwrk.Util.unformatNumber(NS.txtImportePago.getValue()))-
						parseFloat(BFwrk.Util.unformatNumber(NS.txtSaldoActualPago.getValue())))>10){
					Ext.Msg.alert("SET","El monto a pagar no puede ser mayor al de la deuda");
					NS.txtImportePago.setValue(NS.txtSaldoActualPago.getValue());
				}
				NS.txtImportePago.setValue(BFwrk.Util.formatNumber(NS.txtImportePago.getValue()));
			}
		}
	});
	NS.lblFechaVto = new Ext.form.Label({
		text : 'Fecha Vto:',
		x : 150,
		y : 105
	});
	NS.txtFechaVto = new Ext.form.DateField({
		id: PF+'txtFechaVto',
		name: PF+'txtFechaVto',
		format: 'd/m/Y',
		x: 150,
		y: 120,
		width: 110,
		listeners:{
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			blur:function(datefield){
				if(datefield.getValue()==''){
					datefield.setValue(NS.fecHoy);
				}	
			}
		}
	});
	NS.lblAmortizacion = new Ext.form.Label({
		text : 'No. Amortización:',
		x : 280,
		y : 105
	});
	NS.txtAmortizacion= new Ext.form.TextField({
		id: PF + 'txtAmortizacion',
		name: PF + 'txtAmortizacion',
		x: 280,
		disabled:true,
		y: 120,
		width:100,
		value: '',
	});
	NS.chkPrimeras = new Ext.form.Checkbox({
		name : PF + 'chkPrimeras',
		id : 'chkPrimeras',
		x : 410,
		y : 105,
		boxLabel: 'Primeras amortizaciones'
	});
	NS.aceptarPago=function(){
		NS.MB2 = Ext.MessageBox;
		Ext.apply(NS.MB2,{
			YES : {text : 'Si',itemId : 'yes'},
			NO : {text : 'No',itemId : 'no'}
		});
		NS.MB2.confirm('SET','¿Desea realizar el Pago Anticipado?',
				function(
						btn) {
			if (btn === 'yes') {
				if(parseFloat(BFwrk.Util.unformatNumber(NS.txtSaldoActualPago.getValue()))<
						parseFloat(BFwrk.Util.unformatNumber(NS.txtImportePago.getValue())))
					Ext.Msg.alert("SET","No se puede pagar más de lo dispuesto");
				else{	//pago anticipado de amortizaciones
					var seleccionado = NS.gridVencimiento.getSelectionModel().getSelections();
					VencimientoFinanciamientoCAction.pagoAnticipadoParcial(NS.txtLineaPago.getValue(),
							parseInt(NS.txtDisposicionPago.getValue()),BFwrk.Util.changeDateToString(''+NS.txtFecInicio.getValue()),
							BFwrk.Util.changeDateToString(''+NS.txtFechaVto.getValue()),
							parseFloat(BFwrk.Util.unformatNumber(NS.txtImportePago.getValue())),
							parseInt(NS.txtAmortizacion.getValue()),NS.chkPrimeras.getValue(),parseInt(NS.noEmpresa),
							parseFloat(BFwrk.Util.unformatNumber(NS.txtSaldoActualPago.getValue())),
							seleccionado[0].get('fecVen'),NS.fecHoy,
							function(mapResult,e) {
						BFwrk.Util.msgWait('Terminado...',false);
						if (mapResult.msgUsuario !== null
								&& mapResult.msgUsuario !== ''
									&& mapResult.msgUsuario != undefined) {
							for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
								Ext.Msg.alert('Información SET', ''+ mapResult.msgUsuario[msg], function(btn, text){
									if (btn == 'ok'){
										NS.limpiar();
										NS.buscar();
										NS.winPagoAnticipado.hide();
									}
								});
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
	NS.cmdAceptarPago = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAceptarPago',
		name : PF + 'cmdAceptarPago',
		text : 'Aceptar',
		x : 300,
		y : 170,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.aceptarPago();
				}
			}
		}
	});
	NS.cancelarPago=function(){
		NS.txtEmpresaPago.setValue("");
		NS.txtLineaPago.setValue("");
		NS.txtDisposicionPago.setValue("");
		NS.txtSaldoActualPago.setValue("0.00");
		NS.txtImportePago.setValue("0.00");
		NS.chkPrimeras.setValue(0);
		NS.winPagoAnticipado.setVisible(false);
		vbBanPagoAnt = false;
	}
	NS.chkAcumulativa = new Ext.form.Checkbox({
		name : PF + 'chkAcumulativa',
		id : 'chkAcumulativa',
		x : 800,
		y : 400,
		hidden:true,
		boxLabel: 'Acumulativa'
	});
	NS.cmdCancelarPago = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCancelarPago',
		name : PF + 'cmdCancelarPago',
		text : 'Cancelar',
		x : 400,
		y : 170,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.cancelarPago();
				}
			}
		}
	});
	NS.txtFecInicio = new Ext.form.DateField({
		id: PF+'txtFecInicio',
		name: PF+'txtFecInicio',
		format: 'd/m/Y',
		x: 130,
		y: 170,
		hidden:true,
		width: 110,
		listeners:{
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			}
		}
	});
	NS.winPagoAnticipado = new Ext.Window({
		title: 'Pagos Anticipados',
		modal: true,
		shadow: true,
		closeAction: 'hide',
		width: 500,
		height: 250,
		layout: 'absolute',
		plain: true,
		bodyStyle: 'padding:10px;',
		buttonAlign: 'center',
		draggable: false,
		resizable: false,
		autoScroll: true,
		items: [
		        NS.lblEmpresaPago,NS.txtEmpresaPago,
		        NS.lblLineaPago,NS.txtLineaPago,
		        NS.lblDisposicionPago,NS.txtDisposicionPago,
		        NS.lblSaldoActualPago,NS.txtSaldoActualPago,
		        NS.lblImportePago,NS.txtImportePago,
		        NS.lblFechaVto,NS.txtFechaVto,
		        NS.lblAmortizacion,NS.txtAmortizacion,
		        NS.chkPrimeras,
		        NS.cmdAceptarPago,NS.cmdCancelarPago,
		        NS.txtFecInicio
		        ],
		        listeners: {
		        	show: {
		        		fn: function() {}
		        	},
		        	hide: {
		        		fn: function() {
		        			NS.txtEmpresaPago.setValue("");
		        			NS.txtLineaPago.setValue("");
		        			NS.txtDisposicionPago.setValue("");
		        			NS.txtSaldoActualPago.setValue("0.00");
		        			NS.txtImportePago.setValue("0.00");
		        			NS.chkPrimeras.setValue(0);
		        			NS.winPagoAnticipado.setVisible(false);
		        			vbBanPagoAnt = false;
		        		}
		        	}
		        }	
	});
	//Fin NS.winPagoAnticipado
	NS.txtDivisaPag= new Ext.form.TextField({
		id: PF + 'txtDivisaPag',
		name: PF + 'txtDivisaPag',
		x: 0,
		y: 20,
		width:40,
		value: '0',
		listeners:{
			change:function(caja,valor){
				NS.txtDivisaPag.setValue(NS.txtDivisaPag.getValue().toUpperCase());
				var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtDivisaPag', NS.cmbDivisaPag.getId());
				if(Ext.getCmp(PF + 'cmbDivisaPag').getValue()==''){
					Ext.Msg.alert("SET","Divisa inexistente.");
					Ext.getCmp(PF + 'txtDivisaPag').setValue('');
					NS.cmbDivisaPag.reset();
				}
			}
		}
	});
	NS.storeCmbDivisaPag = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			bRestringido : '',
		},
		paramOrder : [ 'bRestringido'],
		directFn : AltaFinanciamientoAction.obtenerDivisas,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ]
	});
	NS.cmbDivisaPag= new Ext.form.ComboBox({
		store: NS.storeCmbDivisaPag,
		name: PF + 'cmbDivisaPag',
		id: PF + 'cmbDivisaPag',
		x: 45,
		y: 20,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all',
		width:150,
		displayField: 'descripcion',
		valueField: 'idStr',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtDivisaPag', NS.cmbDivisaPag.getId());
				}
			}
		}
	});
	NS.txtBancoPag= new Ext.form.TextField({
		id: PF + 'txtBancoPag',
		name: PF + 'txtBancoPag',
		x: 320,
		y: 20,
		width:40,
		value: '0',
		listeners:{
			change:function(caja,valor){
				var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtBancoPag', NS.cmbBancoPag.getId());
				if(Ext.getCmp(PF + 'cmbBancoPag').getValue()==''){
					Ext.Msg.alert("SET","Banco inexistente.");
					Ext.getCmp(PF + 'txtBancoPag').setValue('');
					NS.cmbBancoPag.reset();
				}
			}
		}
	});
	NS.storeBancoPago = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			plEmpresa :0
		},
		paramOrder : [ 'plEmpresa' ],
		directFn : VencimientoFinanciamientoCAction.obtenerBancoPago,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ]
	});
	NS.cmbBancoPag= new Ext.form.ComboBox({
		store: NS.storeBancoPago,
		name: PF + 'cmbBancoPag',
		id: PF + 'cmbBancoPag',
		x: 365,
		y: 20,
		emptyText: 'Seleccione un banco',
		triggerAction: 'all',
		width:150,
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtBancoPag', NS.cmbBancoPag.getId());
					NS.cmbChequeraPag.reset();
					NS.storeComboClabe.baseParams.noEmpresa=parseInt(NS.cmbEmpresa.getValue());
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storeComboClabe,
						msg : "Cargando..."
					});
					NS.storeComboClabe.load({
						params:{
							pvvValor2:parseInt(NS.txtBancoPag.getValue()),
							psDivisa: ''
						}
					});
				}
			}
		}
	});
	NS.storeComboClabe= new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			pvvValor2 : 0,
			psDivisa : '',
			noEmpresa:0
		},
		paramOrder : [ 'pvvValor2', 'psDivisa','noEmpresa' ],
		directFn : AltaFinanciamientoAction.funSQLComboClabe,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ]
	});
	NS.cmbChequeraPag= new Ext.form.ComboBox({
		store: NS.storeComboClabe,
		name: PF + 'cmbChequeraPag',
		id: PF + 'cmbChequeraPag',
		x: 540,
		y: 20,
		emptyText: 'Seleccione una chequera',
		triggerAction: 'all',
		width:150,
		displayField: 'descripcion',
		valueField: 'idStr',
		mode: 'local',
	});
	NS.verificaFechas=function(){
		var x=0,record,record2;
		var registros=NS.storeVencimiento.getCount();
		for(var i=0;i<registros;i++){
			if(NS.gridVencimiento.getSelectionModel().isSelected(i)){
				if (x = 0) 
					x = i;
				record=NS.storeVencimiento.getAt(i);
				record2=NS.storeVencimiento.getAt(x);
				var fecVenI=record.data.fecVen.split("/");
				var dt = new Date(parseInt(fecVenI[2], 10),
						parseInt(fecVenI[1], 10) - 1,
						parseInt(fecVenI[0], 10));
				fecVenI=dt;
				var fecVenX=record2.data.fecVen.split("/");
				var dt = new Date(parseInt(fecVenX[2], 10),
						parseInt(fecVenX[1], 10) - 1,
						parseInt(fecVenX[0], 10));
				fecVenX=dt;
				var fecHoy= NS.fecHoy.split("/");
				var dt = new Date(parseInt(fecHoy[2], 10),
						parseInt(fecHoy[1], 10) - 1,
						parseInt(fecHoy[0], 10));
				fecHoy=dt;
				if(fecVenX>fecHoy){
					if((fecVenX!=fecVenI)&&(fecVenX<fecVenI)){
						Ext.Msg.alert("SET","La amortizaciones que desea vencer son de fecha diferente");
						return false;
					}
				}
			}
		}
		return true;
	}
	NS.aceptar=function(){
		//
		var vsClave ,bAgrupado, plFolioAnt,sBanco,sChequera,record;
		var registros = NS.gridVencimiento.getSelectionModel().getSelections();
		if(registros.length<=0){
			Ext.Msg.alert("SET","Seleccione un registro para realizar el pago.");
			return;
		}
//Validación para que envíe amortizaciones de capital e interes de una misma disposición
		for (var i = 0; i < (registros.length+1); i++) {
			var record=NS.storeVencimiento.getAt(i);
			var sigRecord;
			for (var j = 0; j < registros.length; j++) {
				if(stringToDate(record.data.fecVen).getTime()==stringToDate(registros[j].get('fecVen')).getTime()){
					if(record.data.idContrato==registros[j].get('idContrato')){
						if(record.data.idDisposicion==registros[j].get('idDisposicion')){
							if(registros[j].get('idAmortizacion')==0){
								if(i==(NS.storeVencimiento.data.length-1)){
									break;
								}
								sigRecord=NS.storeVencimiento.getAt(i+1);
								if(stringToDate(registros[j].get('fecVen')).getTime()==stringToDate(sigRecord.data.fecVen).getTime()&&
										registros[j].get('idContrato')==sigRecord.data.idContrato&&
										registros[j].get('idDisposicion')==sigRecord.data.idDisposicion&&
										registros[j].get('idAmortizacion')!=sigRecord.data.idAmortizacion){
									if(!NS.gridVencimiento.getSelectionModel().isSelected(i+1)){
										Ext.Msg.alert("SET","Seleccione la amortización de capital correspondiente a la disposición "+registros[j].get('idDisposicion')+
												" del contrato "+registros[j].get('idContrato'));
										return;
									}
								}
							}
						}
					}
				}
			}
		}
		if(NS.storeVencimiento.getCount()<1)
			return;
		NS.MB2 = Ext.MessageBox;
		Ext.apply(NS.MB2,{
			YES : {text : 'Si',itemId : 'yes'},
			NO : {text : 'No',itemId : 'no'}
		});
		NS.MB2.confirm('SET','¿Desea pagar las amortizaciones seleccionadas?',
				function(
						btn) {
			if (btn === 'yes') {
				if(!NS.verificaFechas())
					return;
				//Para cuando sea menú de tipo "Arrendamientos"
				//if (vsTipoMenu == "A")
				//If Not BuscaNotasCredito(False) Then Exit Sub
				//Método para realizar pago de amortizaciones.
				var matrizAmortizaciones = new Array();
				for (var i = 0; i < NS.storeVencimiento.data.length; i++) {
					var record=NS.storeVencimiento.getAt(i);
					var registrosAmort = {};
					registrosAmort.fecVen = record.data.fecVen;
					registrosAmort.idContrato=record.data.idContrato;
					registrosAmort.idDisposicion=record.data.idDisposicion;
					registrosAmort.idAmortizacion=record.data.idAmortizacion;
					registrosAmort.capital=record.data.capital;
					registrosAmort.interes=record.data.interes;
					registrosAmort.renta=record.data.renta;
					registrosAmort.iva=record.data.iva;
					registrosAmort.fecVen=record.data.fecVen;
					registrosAmort.empresa=record.data.empresa;
					registrosAmort.descEmpresa=record.data.descEmpresa;
					registrosAmort.institucion=record.data.institucion;
					registrosAmort.descInstitucion=record.data.descInstitucion;
					registrosAmort.saldo=record.data.saldo;
					registrosAmort.formaPago=record.data.formaPago;
					registrosAmort.equivalente=record.data.equivalente;
					registrosAmort.gpoEmpresa=record.data.gpoEmpresa;
					registrosAmort.descPago=record.data.descPago;
					registrosAmort.valorPago=record.data.valorPago;
					registrosAmort.bancoBenef=record.data.bancoBenef;
					registrosAmort.chequeraBenef=record.data.chequeraBenef;
					registrosAmort.divisa=record.data.divisa;
					registrosAmort.fecIni=record.data.fecIni;
					registrosAmort.totalPago=record.data.totalPago;
					registrosAmort.checked = NS.gridVencimiento.getSelectionModel().isSelected(i);
					matrizAmortizaciones[i] = registrosAmort;
				}
				var jsonStringAmort= Ext.util.JSON.encode(matrizAmortizaciones);
				var tipoCambio,banco;
				if(NS.txtTipoCambio.getValue()==0)
					tipoCambio=0;
				else
					tipoCambio=parseFloat(NS.txtTipoCambio.getValue());
				if(NS.txtBancoPag.getValue()==0)
					banco=0;
				else
					banco=parseInt(NS.txtBancoPag.getValue());
				VencimientoFinanciamientoCAction.pagoAmortizaciones(jsonStringAmort,NS.txtDivisaPag.getValue(),banco,
						NS.cmbChequeraPag.getValue(),tipoCambio,
						function(mapResult,e) {
					BFwrk.Util.msgWait('Terminado...',false);
					if (mapResult.msgUsuario !== null
							&& mapResult.msgUsuario !== ''
								&& mapResult.msgUsuario != undefined) {
						for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
							Ext.Msg.alert('Información SET', ''+ mapResult.msgUsuario[msg], function(btn, text){
								if (btn == 'ok'){
									NS.buscar();
									NS.limpiar();
								}
							});

						}			
						//NS.buscar();
						//NS.limpiar();
					}
				});
			}
			else{
				return;
			}
		});
	}
	NS.imprimir=function(){
		if(NS.storeVencimiento.getCount()<=0){
			Ext.Msg.alert("SET","No existen registros.");
			return;
		}
		else{
			var matrizAmortizaciones = new Array();
			for (var i = 0; i < NS.storeVencimiento.data.length; i++) {
				var record=NS.storeVencimiento.getAt(i);
				var registrosAmort = {};
				registrosAmort.fecVen = record.data.fecVen;
				registrosAmort.idContrato=record.data.idContrato;
				registrosAmort.idDisposicion=record.data.idDisposicion;
				registrosAmort.idAmortizacion=record.data.idAmortizacion;
				registrosAmort.capital=record.data.capital;
				registrosAmort.interes=record.data.interes;
				registrosAmort.iva=record.data.iva;
				registrosAmort.descEmpresa=record.data.descEmpresa;
				registrosAmort.descInstitucion=record.data.descInstitucion;
				registrosAmort.totalPago=record.data.totalPago;
				matrizAmortizaciones[i] = registrosAmort;
			}
			var jsonStringAmort= Ext.util.JSON.encode(matrizAmortizaciones);
			var forma = NS.vencimientoC.getForm();
			Ext.getCmp('hdJasonV').setValue(jsonStringAmort);
			Ext.getCmp('nomReporte').setValue("ReporteVencimientosCB");
			if(NS.cmbDivisa.getRawValue()=='****TODAS****')
				Ext.getCmp('divisa').setValue("TODAS");
			else
				Ext.getCmp('divisa').setValue(Ext.getCmp('cmbDivisa').getRawValue());
			Ext.getCmp('fecha').setValue(BFwrk.Util.changeDateToString(''+NS.txtFecIni.getValue()));
			forma.url = '/SET/jsp/Reportes.jsp';
			forma.standardSubmit = true;
			forma.method = 'GET';
			forma.target = '_blank';
			var el = forma.getEl().dom;
			var target = document.createAttribute("target");
			target.nodeValue = "_blank";
			el.setAttributeNode(target);
			forma.submit({target: '_blank'});
		}
	}
	NS.correo=function(){

		VencimientoFinanciamientoCAction.correo(function(e) {
			BFwrk.Util.msgWait('Terminado...',false);
		});

	}
	NS.vencimientoC = new Ext.form.FormPanel({
		title: 'Vencimiento de Crédito (Créditos Bancarios)',
		autowidth: true,
		height: 500,
		padding: 10,
		layout: 'absolute',
		id: PF + 'vencimientoC',
		name: PF + 'vencimientoC',
		renderTo: NS.tabContId,
		frame: true,
		autoScroll: true,
		items : [
		         { 
		        	 xtype: 'fieldset',
		        	 title: '',
		        	 autowidth: true,
		        	 height: 130,
		        	 x: 0,
		        	 y: 0,
		        	 layout: 'absolute',
		        	 id: 'fSetPrincipal',
		        	 items: [
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Fecha de Vencimiento:',
		        	        	 x: 0,
		        	        	 y: 0
		        	         },
		        	         NS.txtFecIni,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'País:',
		        	        	 x: 220,
		        	        	 y: 0
		        	         },
		        	         NS.txtPais,
		        	         NS.cmbPais,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Empresa:',
		        	        	 x: 460,
		        	        	 y: 0
		        	         },
		        	         {
		        	        	 xtype: 'numberfield',
		        	        	 x: 460,
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
		        	         NS.cmbEmpresa,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Divisa:',
		        	        	 name:PF+'lblDivisa',
		        	        	 id:PF+'lblDivisa',
		        	        	 x: 650,
		        	        	 y: 60
		        	         },
		        	         NS.txtDivisa,
		        	         NS.cmbDivisa,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Banco Otorgante:',
		        	        	 name:PF+'lblBanco',
		        	        	 id:PF+'lblBanco',
		        	        	 x: 800,
		        	        	 y: 0
		        	         },
		        	         NS.txtBanco,
		        	         NS.cmbBanco ,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Tipo Financiamiento:',
		        	        	 name:PF+'lblFinanciamiento',
		        	        	 id:PF+'lblFinanciamiento',
		        	        	 x: 400,
		        	        	 y: 60
		        	         },
		        	         NS.txtFinanciamiento,
		        	         NS.cmbFinanciamiento ,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Línea:',
		        	        	 x: 0,
		        	        	 y: 60
		        	         },
		        	         NS.cmbLinea,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Disposición:',
		        	        	 x: 200,
		        	        	 y: 60
		        	         },
		        	         NS.cmbDispo,
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Buscar',
		        	        	 x: 900,
		        	        	 y: 75,
		        	        	 width: 90,
		        	        	 id: PF + 'btnBuscar',
		        	        	 name: PF + 'btnBuscar',
		        	        	 disabled: false,
		        	        	 listeners: {
		        	        		 click:{
		        	        			 fn: function(btn){
		        	        				 NS.buscar();
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
		        	 height: 500,
		        	 x: 0,
		        	 y: 140,
		        	 layout: 'absolute',
		        	 id: 'fSetSec2',
		        	 items: [
		        	         NS.gridVencimiento,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'TOTAL PAGO:',
		        	        	 x: 100,
		        	        	 y: 310
		        	         },
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Capital:',
		        	        	 x: 190,
		        	        	 y: 310
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 240,
		        	        	 y: 310,
		        	        	 disabled:true,
		        	        	 width: 110,
		        	        	 name: PF + 'labCap',
		        	        	 id: PF + 'labCap'
		        	         },
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Interes:',
		        	        	 x: 360,
		        	        	 y: 310
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 405,
		        	        	 y: 310,
		        	        	 width: 110,
		        	        	 disabled:true,
		        	        	 name: PF + 'labInt',
		        	        	 id: PF + 'labInt',
		        	         },{
		        	        	 xtype: 'label',
		        	        	 text: 'Iva:',
		        	        	 x: 690,
		        	        	 y: 310
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 720,
		        	        	 y: 310,
		        	        	 width: 110,
		        	        	 disabled:true,
		        	        	 name: PF + 'labIva',
		        	        	 id: PF + 'labIva',
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 720,
		        	        	 y: 310,
		        	        	 width: 110,
		        	        	 hidden:true,
		        	        	 value:'0.00',
		        	        	 disabled:true,
		        	        	 name: PF + 'labRenta',
		        	        	 id: PF + 'labRenta',
		        	         },
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Pago Total:',
		        	        	 x: 840,
		        	        	 y: 310
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 900,
		        	        	 y: 310,
		        	        	 disabled:true,
		        	        	 width: 110,
		        	        	 name: PF + 'labPagoTotal',
		        	        	 id: PF + 'labPagoTotal',
		        	         },{
		        	        	 xtype: 'fieldset',
		        	        	 title: '',
		        	        	 width: 750,
		        	        	 height: 80,
		        	        	 x: 0,
		        	        	 y: 340,
		        	        	 layout: 'absolute',
		        	        	 id: PF+'frmPagoDivisa',
		        	        	 name: PF+'frmPagoDivisa',
		        	        	 items: [
		        	        	         {
		        	        	        	 xtype: 'label',
		        	        	        	 text: 'Divisa Pago:',
		        	        	        	 x: 0,
		        	        	        	 y: 0
		        	        	         },
		        	        	         NS.txtDivisaPag,
		        	        	         NS.cmbDivisaPag,
		        	        	         {
		        	        	        	 xtype: 'label',
		        	        	        	 text: 'T. Cambio:',
		        	        	        	 x: 220,
		        	        	        	 y: 0
		        	        	         },
		        	        	         NS.txtTipoCambio,
		        	        	         {
		        	        	        	 xtype: 'label',
		        	        	        	 text: 'Banco Pagador:',
		        	        	        	 x: 320,
		        	        	        	 y: 0
		        	        	         },
		        	        	         NS.txtBancoPag,
		        	        	         NS.cmbBancoPag,
		        	        	         {
		        	        	        	 xtype: 'label',
		        	        	        	 text: 'Cta. Cheques:',
		        	        	        	 x: 540,
		        	        	        	 y: 0
		        	        	         },
		        	        	         NS.cmbChequeraPag

		        	        	         ]},
		        	        	         {
		        	        	        	 xtype: 'button',
		        	        	        	 text: 'Cancelar',
		        	        	        	 x: 800,
		        	        	        	 y: 350,
		        	        	        	 width: 90,
		        	        	        	 id: PF + 'btnCancelar',
		        	        	        	 name: PF + 'btnCancelar',
		        	        	        	 disabled: false,
		        	        	        	 listeners:{
		        	        	        		 click:{
		        	        	        			 fn: function(btn) {
		        	        	        				 NS.cancelar();
		        	        	        			 }
		        	        	        		 }
		        	        	        	 }
		        	        	         },
		        	        	         NS.cmdPagoAnticipado,
		        	        	         {
		        	        	        	 xtype: 'button',
		        	        	        	 text: 'Aceptar',
		        	        	        	 x: 730,
		        	        	        	 y: 450,
		        	        	        	 width: 90,
		        	        	        	 id: PF + 'btnAceptar',
		        	        	        	 name: PF + 'btnAceptar',
		        	        	        	 disabled: true,
		        	        	        	 listeners:{
		        	        	        		 click: {
		        	        	        			 fn: function(btn){
		        	        	        				 NS.aceptar();
		        	        	        			 }
		        	        	        		 }
		        	        	        	 }
		        	        	         },
		        	        	         {
		        	        	        	 xtype: 'textfield',
		        	        	        	 x:0,
		        	        	        	 y:0,
		        	        	        	 hidden:true,
		        	        	        	 name:'hdJasonV',
		        	        	        	 id:'hdJasonV',
		        	        	        	 value: ''
		        	        	         },
		        	        	         {
		        	        	        	 xtype: 'textfield',
		        	        	        	 x:0,
		        	        	        	 y:0,
		        	        	        	 hidden:true,
		        	        	        	 name:'empresa',
		        	        	        	 id:'empresa',
		        	        	        	 value: apps.SET.NOM_EMPRESA
		        	        	         },
		        	        	         {
		        	        	        	 xtype: 'textfield',
		        	        	        	 x:0,
		        	        	        	 y:0,
		        	        	        	 hidden:true,
		        	        	        	 name:'fecha',
		        	        	        	 id:'fecha',
		        	        	        	 value:''
		        	        	         },
		        	        	         {
		        	        	        	 xtype: 'textfield',
		        	        	        	 x:0,
		        	        	        	 y:0,
		        	        	        	 hidden:true,
		        	        	        	 name:'divisa',
		        	        	        	 id:'divisa',
		        	        	        	 value: '',
		        	        	         },

		        	        	         {
		        	        	        	 xtype: 'textfield',
		        	        	        	 x:0,
		        	        	        	 y:0,
		        	        	        	 hidden:true,
		        	        	        	 name: 'nomReporte',
		        	        	        	 id: 'nomReporte',
		        	        	        	 value: 'ReporteVencimientos'
		        	        	         },
		        	        	         NS.chkAcumulativa,
		        	        	         {
		        	        	        	 xtype: 'button',
		        	        	        	 text: 'Imprimir',
		        	        	        	 x: 830,
		        	        	        	 y: 450,
		        	        	        	 width: 90,
		        	        	        	 id: PF + 'btnImprimir',
		        	        	        	 name: PF + 'btnImprimir',
		        	        	        	 disabled: true,
		        	        	        	 listeners:{
		        	        	        		 click: {
		        	        	        			 fn: function(btn) {
		        	        	        				 NS.imprimir();
		        	        	        			 }
		        	        	        		 }
		        	        	        	 }
		        	        	         },
		        	        	         {
		        	        	        	 xtype: 'button',
		        	        	        	 text: 'Limpiar',
		        	        	        	 x: 930,
		        	        	        	 y: 450,
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
		        	        	         }]
		         }]
	});

	NS.vencimientoC.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());


});