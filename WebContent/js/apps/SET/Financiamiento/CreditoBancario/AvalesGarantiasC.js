//GGonzález

Ext.onReady(function(){

	var NS = Ext.namespace('apps.SET.Financiamiento.AvalesGarantiasC');
	NS.tabContId = apps.SET.tabContainerId;
	NS.fecHoy = apps.SET.FEC_HOY;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var vbModifico;
	//Fecha en formato dd/mm/yyyy a tipo Date
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
		directFn : AvalesGarantiasFCAction.llenarCmbEmpresaAvalista,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var recordsStoreEmpresa = NS.storeCmbEmpresa.recordType;	
				var todos = new recordsStoreEmpresa({
					idStr: '0',
					descripcion: '****TODAS****'
				});
				NS.storeCmbEmpresa.insert(0,todos);
				NS.cmbEmpresaBusca.setValue('0');
				NS.cmbEmpresaBusca.setRawValue('****TODAS****');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbEmpresa,
		msg : "Cargando..."
	});
	NS.storeCmbEmpresa.baseParams.piNoUsuario=parseInt(NS.idUsuario);
	NS.storeCmbEmpresa.load();
	NS.cmbEmpresaBusca = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa,
		name: PF + 'cmbEmpresaBusca',
		id: PF + 'cmbEmpresaBusca',
		x: 75,
		y: 20,
		width:300,
		displayField: 'descripcion',
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		displayField: 'descripcion',
		valueField: 'idStr',
		mode: 'local',
		value:'****TODAS****',
		listeners:{
			select:{
				fn:function(combo, valor){
					var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtEmpresaBusca', NS.cmbEmpresaBusca.getId());
					if(NS.cmbEmpresaBusca.getValue()==''){
						NS.txtEmpresaBusca.setValue('0');
						NS.cmbEmpresaBusca.setValue(0);
						NS.cmbEmpresaBusca.setRawValue('****TODAS****');
					}
				}
			}
		}
	});
	NS.storeCmbTipoGtia = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		directFn : AvalesGarantiasFCAction.llenarCmbTipoGtia,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var recordsStore = NS.storeCmbTipoGtia.recordType;	
				var todos = new recordsStore({
					id: '0',
					descripcion: '****TODAS****'
				});
				NS.storeCmbTipoGtia.insert(0,todos);
				NS.cmbTipoGtia.setValue('0');
				NS.cmbTipoGtia.setRawValue('****TODAS****');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbTipoGtia,
		msg : "Cargando..."
	});
	NS.storeCmbTipoGtia.load();
	NS.cmbTipoGtia= new Ext.form.ComboBox({
		store: NS.storeCmbTipoGtia,
		name: PF + 'cmbTipoGtia',
		id: PF + 'cmbTipoGtia',
		x: 400,
		y: 20,
		width:150,
		displayField: 'descripcion',
		valueField: 'id',
		triggerAction: 'all',
		mode: 'local',
		value:'****TODAS****',
	});
	NS.buscar=function(){
		var vsTipoGtia = '0';
		if(NS.cmbTipoGtia.getRawValue()!='****TODAS****')
			vsTipoGtia=NS.cmbTipoGtia.getValue();
		NS.storeAvalG.baseParams.psTipo=vsTipoGtia.toString();
		NS.storeAvalG.baseParams.piEmpresa=Ext.getCmp(PF +'txtEmpresaBusca').getValue();
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeAvalG,
			msg : "Cargando..."
		});
		NS.storeAvalG.load();
	}
	/*Inicio del grid*/
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: true
	});
	NS.columnasAvalG = new Ext.grid.ColumnModel([
	                                             NS.columnaSeleccion,
	                                             {header: '', width: 0, dataIndex: 'noEmpresaS', sortable: true,hidden : true, hideable : false},
	                                             {header: 'Avalista', width: 200, dataIndex: 'nomEmpresa', sortable: true},
	                                             {header: 'Clave', width: 120, dataIndex: 'clave', sortable: true},
	                                             {header: '', width:0 , dataIndex: 'idAvalGarantia', sortable: true,hidden : true, hideable : false},
	                                             {header: 'Tipo Garantía', width: 150, dataIndex: 'avalGarantia', sortable: true},
	                                             {header: 'Aval', width:180 , dataIndex: 'descripcion', sortable: true},
	                                             {header: 'Garantía Especial', width: 120, dataIndex: 'garantiaEspecial', sortable: true},
	                                             {header: '% Gtía.', width:120 , dataIndex: 'pjeGarantia', sortable: true,
	                                            	 renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00000')}},
	                                            	 {header: 'Valor Aval/Garantía', width: 120, dataIndex: 'valorTotal', sortable: true,
	                                            		 renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00')}},
	                                            		 {header: 'Valor Dispuesto', width:120, dataIndex: 'montoDispuesto', sortable: true,
	                                            			 renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00')}},
	                                            			 {header: 'Valor Disponible', width: 120, dataIndex: 'disponible', sortable: true,
	                                            				 renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00')}},
	                                            				 {header: 'Divisa', width:120, dataIndex: 'idDivisa', sortable: true},
	                                            				 {header: 'Fecha de inicio', width:120, dataIndex: 'fecInicial', sortable: true},
	                                            				 {header: 'Fecha de vencimiento', width:120, dataIndex: 'fecFinal', sortable: true}
	                                            				 ]);
	NS.storeAvalG = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {pstipo:'',piEmpresa:''},
		root: '',
		paramOrder: ['psTipo','piEmpresa'],
		directFn: AvalesGarantiasFCAction.buscarAvalGtia, 
		fields: [
		         {name: 'noEmpresaS'},
		         {name: 'nomEmpresa'},
		         {name: 'clave'},
		         {name: 'idAvalGarantia'},
		         {name: 'avalGarantia'},
		         {name: 'descripcion'},
		         {name: 'garantiaEspecial'},
		         {name: 'pjeGarantia'},
		         {name: 'valorTotal'},
		         {name: 'montoDispuesto'},
		         {name: 'disponible'},
		         {name: 'idDivisa'},
		         {name: 'fecInicial'},
		         {name: 'fecFinal'},
		         ],
		         listeners: {
		        	 load: function(s, records) {
		        		 if(records.length == null || records.length <= 0)
		        			 Ext.Msg.alert('SET', 'No existen datos con el criterio seleccionado');
		        	 }
		         },
		         exception:function(misc) {
		        	 NS.myMask.hide();
		        	 Ext.Msg.alert('SET', 'Error al cargar los datos en el grid, <br> Verificar la conexión');
		         }
	});


	NS.gridAvalG = new Ext.grid.GridPanel({
		store: NS.storeAvalG,
		id: 'gridAvalG',
		cm: NS.columnasAvalG,
		autowidth: true,
		height:220,
		x: 0,
		y: 0,
		frame:true,
		cm: NS.columnasAvalG,
		sm: NS.columnaSeleccion,
		listeners : {
			click : {
				fn : function(grid) {
					var registro = NS.gridAvalG.getSelectionModel().getSelections();
					Ext.getCmp(PF +'txtEmpresa').setValue(registro[0].get('noEmpresaS'));
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
					Ext.getCmp(PF +'cmbTipo').setValue(registro[0].get('idAvalGarantia'));
					Ext.getCmp(PF +'txtClave').setValue(registro[0].get('clave'));
					Ext.getCmp(PF +'txtFecIni').setValue(stringToDate(registro[0].get('fecInicial')));
					Ext.getCmp(PF +'txtFecFin').setValue(stringToDate(registro[0].get('fecFinal')));
					Ext.getCmp(PF +'txtValor').setValue(Ext.util.Format.number(registro[0].get('valorTotal'), '0,0.00'));
					Ext.getCmp(PF +'txtPje').setValue(Ext.util.Format.number(registro[0].get('pjeGarantia'), '0,0.0000'));
					if(registro[0].get('garantiaEspecial')=='SI')
						Ext.getCmp(PF +'chkGtiaEspecial').setValue(1);
					else
						Ext.getCmp(PF +'chkGtiaEspecial').setValue(0);
					Ext.getCmp(PF +'txtIdDivisa').setValue(registro[0].get('idDivisa').trim());
					var comboValue = BFwrk.Util.updateTextFieldToCombo(PF +'txtIdDivisa', NS.cmbDivisa.getId());
					Ext.getCmp(PF +'txtDescripcion').setValue(registro[0].get('descripcion'));
					Ext.getCmp(PF +'btnModificar').setDisabled(false);
					Ext.getCmp(PF +'btnCancelar').setDisabled(false);
				}
			},
		}
	});
	/*Fin del grid*/

	NS.storeCmbEmpresa2 = new Ext.data.DirectStore({
		paramsAsHash : false,
		baseParams:{
			piNoUsuario:0,
			pbMismaEmpresa:false,
			plEmpresa:0
		},
		paramOrder : ['piNoUsuario','pbMismaEmpresa','plEmpresa'],
		root : '',
		directFn : AvalesGarantiasFCAction.llenarCmbEmpresaAvalista,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ,
		],
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbEmpresa2,
		msg : "Cargando..."
	});
	NS.storeCmbEmpresa2.baseParams.piNoUsuario=parseInt(NS.idUsuario);
	NS.storeCmbEmpresa2.load();
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa2,
		name: PF + 'cmbEmpresa',
		id: PF + 'cmbEmpresa',
		x: 80,
		y: 20,
		disabled:true,
		triggerAction : 'all',
		emptyText: 'Seleccione una empresa',
		width:450,
		displayField: 'descripcion',
		valueField: 'idStr',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresa.getId());


				}
			}
		}
	});	  

	NS.storeCmbTipoGtia2 = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		directFn : AvalesGarantiasFCAction.llenarCmbTipoGtia,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbTipoGtia2,
		msg : "Cargando..."
	});
	NS.storeCmbTipoGtia2.load();
	NS.cmbTipo= new Ext.form.ComboBox({
		store: NS.storeCmbTipoGtia2,
		name: PF + 'cmbTipo',
		id: PF + 'cmbTipo',
		x: 690,
		y: 20,
		disabled:true,
		triggerAction : 'all',
		width:150,
		emptyText: 'Seleccione un tipo de garantía',
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
	});

	NS.txtClave = new Ext.form.TextField({
		id: PF + 'txtClave',
		name: PF + 'txtClave',
		x: 1,
		y: 70,
		disabled:true,
		width: 100,
		value: '',
		listeners:{
			change:{
				fn:function(combo,valor){
					Ext.getCmp(PF +'txtClave').setValue(Ext.getCmp(PF +'txtClave').getValue().toUpperCase());
				}
			}
		}
	});
	NS.txtFecIni= new Ext.form.DateField({
		id: PF+'txtFecIni',
		name: PF+'txtFecIni',
		format: 'd/m/Y',
		x: 150,
		disabled:true,
		y: 70,
		width: 150,
		listeners:{
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			change: function(datefield) {
				if(datefield.getValue()=='')
					datefield.setValue(NS.fecHoy);
			}
		}
	});


	NS.txtFecFin = new Ext.form.DateField({
		id: PF+'txtFecFin',
		name: PF+'txtFecFin',
		format: 'd/m/Y',
		x: 310,
		disabled:true,
		y: 70,
		width: 150,
		listeners:{
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			change: function(datefield) {
				if(datefield.getValue()=='')
					datefield.setValue(NS.fecHoy);
			}
		}
	});

	NS.txtIdDivisa= new Ext.form.TextField({
		id: PF + 'txtIdDivisa',
		name: PF + 'txtIdDivisa',
		x: 690,
		y: 70,
		disabled:true,
		width: 45,
		value: '',
		listeners:{
			change:{
				fn:function(combo,valor){
					var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdDivisa', NS.cmbDivisa.getId());
					Ext.getCmp(PF +'txtIdDivisa').setValue(Ext.getCmp(PF +'txtIdDivisa').getValue().toUpperCase());
					if(valor=='')
						Ext.Msg.alert("SET","Divisa incorrecta");
					NS.cmbDivisa.reset();
				}
			}
		}
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
		} ]
	});
	NS.storeCmbDivisa.baseParams.bRestringido = false;
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbDivisa,
		msg : "Cargando..."
	});
	NS.storeCmbDivisa.load();
	NS.cmbDivisa= new Ext.form.ComboBox({
		store: NS.storeCmbDivisa,
		name: PF + 'cmbDivisa',
		id: PF + 'cmbDivisa',
		x: 740,
		y: 70,
		disabled:true,
		triggerAction : 'all',
		emptyText: 'Seleccione una divisa',
		width:150,
		displayField: 'descripcion',
		valueField: 'idStr',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtIdDivisa', NS.cmbDivisa.getId());
				}
			}
		}
	});
	NS.txtDescripcion= new Ext.form.TextArea({
		id: PF + 'txtDescripcion',
		name: PF + 'txtDescripcion',
		x: 1,
		y: 120,
		maxLength : 240,
		disabled:true,
		width: 550,
		height:100,
		value: '',

	});

	NS.chkGtiaEspecial= new Ext.form.Checkbox({
		id: PF + 'chkGtiaEspecial',
		name: PF + 'chkGtiaEspecial',
		x: 600,
		y: 120,
		disabled:true,
		boxLabel: 'Garantía Especial',
		listeners:{
			check:{
				fn : function(ch, checked) {
					if (checked) {
						Ext.getCmp(PF +'txtPje').setDisabled(false);
					}
					else{
						Ext.getCmp(PF +'txtPje').setDisabled(true);
					}
				}
			}
		}
	}
	);


	NS.habilitaControles=function(valor, modifica){
		Ext.getCmp(PF +'txtEmpresa').setDisabled(valor);
		Ext.getCmp(PF +'cmbEmpresa').setDisabled(valor);
		Ext.getCmp(PF +'cmbTipo').setDisabled(valor);
		Ext.getCmp(PF +'txtClave').setDisabled(valor);
		Ext.getCmp(PF +'txtDescripcion').setDisabled(valor);
		Ext.getCmp(PF +'txtValor').setDisabled(valor);
		Ext.getCmp(PF +'txtIdDivisa').setDisabled(valor);
		Ext.getCmp(PF +'cmbDivisa').setDisabled(valor);
		Ext.getCmp(PF +'btnAceptar').setDisabled(valor);
		Ext.getCmp(PF +'btnCancelar').setDisabled(valor);
		Ext.getCmp(PF +'txtFecIni').setDisabled(valor);
		Ext.getCmp(PF +'txtFecFin').setDisabled(valor);
		NS.chkGtiaEspecial.setDisabled(valor);
		if(modifica){
			Ext.getCmp(PF +'txtFecIni').setDisabled(false);
			Ext.getCmp(PF +'txtFecFin').setDisabled(false);
			Ext.getCmp(PF +'txtValor').setDisabled(false);
			Ext.getCmp(PF +'txtDescripcion').setDisabled(false);
			Ext.getCmp(PF +'txtPje').setDisabled(false);
			NS.chkGtiaEspecial.setDisabled(false);
			Ext.getCmp(PF +'btnAceptar').setDisabled(false);
			Ext.getCmp(PF +'btnCancelar').setDisabled(false);
		}
	}
	NS.deshabilitaBotones=function(valor){
		Ext.getCmp(PF +'btnBuscar').setDisabled(false);
		Ext.getCmp(PF +'btnCrearNuevo').setDisabled(false);
	}
	NS.crearNuevo=function(){
		vbModifico = false;
		NS.cancelar();
		NS.habilitaControles(false,false);
		NS.deshabilitaBotones(true);
		Ext.getCmp(PF +'btnCancelar').setDisabled(false);
	}
	NS.modificar=function(){
		NS.habilitaControles(true, true);
		vbModifico = true;

	}
	NS.aceptar=function(){
		if(!validarDatos())
			return;
		var lsTipo =parseInt(NS.cmbTipo.getValue());
		var vsEspecial;
		if(NS.chkGtiaEspecial.getValue())
			vsEspecial = 'S';
		else
			vsEspecial = 'N';
		var porcentaje=BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtPje').getValue());
		if(porcentaje!=0)
			porcentaje=parseFloat(porcentaje);
		else
			porcentaje=0.00;
		if (vbModifico){
			AvalesGarantiasFCAction.updateAvalGarantia(
					Ext.getCmp(PF + 'txtEmpresa').getValue(), lsTipo,Ext.getCmp(PF + 'txtClave').getValue(),
					Ext.getCmp(PF + 'txtDescripcion').getValue(), 
					parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtValor').getValue())) , 
					BFwrk.Util.changeDateToString(''+Ext.getCmp(PF +'txtFecIni').getValue()),
					BFwrk.Util.changeDateToString(''+Ext.getCmp(PF +'txtFecFin').getValue()),
					porcentaje,vsEspecial,
					function(mapResult,e) {
						BFwrk.Util.msgWait('Terminado...',false);
						if (mapResult.msgUsuario !== null
								&& mapResult.msgUsuario !== ''
									&& mapResult.msgUsuario != undefined) {
							for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
								Ext.Msg.alert('Información SET',''+ mapResult.msgUsuario[msg]);
							}
							NS.cancelar();
							NS.buscar();
						}
					});
		}
		else{
			AvalesGarantiasFCAction.insertaAvalGtia(Ext.getCmp(PF + 'txtEmpresa').getValue(), lsTipo,Ext.getCmp(PF + 'txtClave').getValue(),
					Ext.getCmp(PF + 'txtDescripcion').getValue(), 
					parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtValor').getValue())) ,
					Ext.getCmp(PF + 'txtIdDivisa').getValue(), 
					BFwrk.Util.changeDateToString(''+Ext.getCmp(PF +'txtFecIni').getValue()),
					BFwrk.Util.changeDateToString(''+Ext.getCmp(PF +'txtFecFin').getValue()),
					porcentaje,vsEspecial,	
					function(mapResult,e) {
				BFwrk.Util.msgWait('Terminado...',false);
				if (mapResult.msgUsuario !== null
						&& mapResult.msgUsuario !== ''
							&& mapResult.msgUsuario != undefined) {
					for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
						Ext.Msg.alert('Información SET',''+ mapResult.msgUsuario[msg]);
					}
					NS.cancelar();
					NS.buscar();
				}
			});
		}
	}
	validarDatos=function(){
		var band=false;
		if(Ext.getCmp(PF + 'txtEmpresa').getValue() == ""||Ext.getCmp(PF + 'txtEmpresa').getValue() =='0'){
			Ext.Msg.alert("SET","Seleccione una Empresa.");
			Ext.getCmp(PF + 'txtEmpresa').focus();
			return;
		}
		else if (Ext.getCmp(PF + 'cmbEmpresa').getValue() == "") {
			Ext.Msg.alert("SET","Seleccione una Empresa.");
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
	NS.cancelar=function(){
		Ext.getCmp(PF + 'txtEmpresa').setValue("");
		Ext.getCmp(PF + 'cmbEmpresa').reset();
		Ext.getCmp(PF + 'cmbTipo').setValue("");
		Ext.getCmp(PF + 'txtClave').setValue("");
		Ext.getCmp(PF + 'txtDescripcion').setValue("");
		Ext.getCmp(PF + 'txtValor').setValue("");
		Ext.getCmp(PF + 'txtIdDivisa').setValue("");
		Ext.getCmp(PF + 'cmbDivisa').setValue("");
		Ext.getCmp(PF + 'txtFecIni').setValue(NS.fecHoy);
		Ext.getCmp(PF + 'txtFecFin').setValue(NS.fecHoy);
		Ext.getCmp(PF + 'txtPje').setValue("0.0000");
		Ext.getCmp(PF + 'chkGtiaEspecial').reset();
		Ext.getCmp(PF + 'txtPje').setDisabled(true);
		vbModifico = false;
		NS.habilitaControles(true,false);
		NS.deshabilitaBotones(false);
		Ext.getCmp(PF + 'btnModificar').setDisabled(true);
		NS.buscar();
	}


	//Inicio ventana asignación 

	NS.lblEmpresaAvalista = new Ext.form.Label({
		text :'Avalista:',
		x : 0,
		y : 0,

	});
	NS.txtEmpresaAvalista = new Ext.form.TextField({
		id : PF + 'txtEmpresaAvalista',
		name : PF + 'txtEmpresaAvalista',
		value : '',
		x : 100,
		y : 0,
		disabled : true,
		width : 300,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.lblDescripcionA = new Ext.form.Label({
		text : 'Descripción garantía:',
		x : 0,
		y : 30,

	});
	NS.txtDescripcionA = new Ext.form.TextField({
		id : PF + 'txtDescripcionA',
		name : PF + 'txtDescripcionA',
		value : '',
		x : 100,
		y : 30,
		disabled : true,
		width : 300,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.lblMontoGarantia = new Ext.form.Label({
		text : 'Monto Garantía:',
		x : 500,
		y : 0,

	});
	NS.txtMontoGarantia = new Ext.form.TextField({
		id : PF + 'txtMontoGarantia',
		name : PF + 'txtMontoGarantia',
		value : '',
		x : 580,
		y : 0,
		disabled : true,
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtMontoGarantia').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});

	NS.lblDisponibleA = new Ext.form.Label({
		text : 'Disponible:',
		x : 500,
		y : 30,

	});
	NS.txtDisponibleA = new Ext.form.TextField({
		id : PF + 'txtDisponibleA',
		name : PF + 'txtDisponibleA',
		value : '0.00',
		x : 580,
		y : 30,
		disabled : true,
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtDisponible').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});


	NS.lblTotMontoAsig = new Ext.form.Label({
		text : 'Total Monto Avalado:',
		x : 490,
		y : 380,

	});
	NS.txtTotMontoAsig = new Ext.form.TextField({
		id : PF + 'txtTotMontoAsig',
		name : PF + 'txtTotMontoAsig',
		value : '',
		x : 600,
		y : 380,
		disabled : true,
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtTotMontoAsig').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	NS.lblEmpresaA = new Ext.form.Label({
		text : 'Empresa para Avalar:',
		x : 0,
		y : 0
	});
	NS.txtEmpresaA = new Ext.form.TextField(
			{
				id : PF + 'txtEmpresaA',
				name : PF + 'txtEmpresaA',
				value : '',
				x : 0,
				y : 15,
				disabled : true,
				width : 50,

				listeners : {
					change : {
						fn : function(caja, valor) {
							var comboValue = BFwrk.Util
							.updateTextFieldToCombo(PF
									+ 'txtEmpresaA',
									NS.cmbEmpresaA.getId());
						}
					}
				}
			});

	NS.storeCmbEmpresaA = new Ext.data.DirectStore({
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
			load:function(records){}
		}
	});

	NS.cmbEmpresaA = new Ext.form.ComboBox(
			{
				store : NS.storeCmbEmpresaA,
				name : PF + 'cmbEmpresaA',
				id : PF + 'cmbEmpresaA',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 55,
				y : 15,
				disabled : true,
				width : 350,
				valueField : 'id',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione una empresa',
				triggerAction : 'all',
				value : '',
				listeners : {
					select : {
						fn : function(combo, valor) {
							var comboValue = BFwrk.Util
							.updateComboToTextField(PF
									+ 'txtEmpresaA',
									NS.cmbEmpresaA.getId());
						}
					}
				}
			});
	NS.lblAvalGtia = new Ext.form.Label({
		text : 'Avales y garantías:',
		x : 0,
		y : 45
	});
	NS.txtAvalGtia = new Ext.form.TextField(
			{
				id : PF + 'txtAvalGtia',
				name : PF + 'txtAvalGtia',
				value : '',
				x : 0,
				y : 60,
				disabled : true,
				width : 70,
				listeners : {
					change : {
						fn : function(caja, valor) {
							var comboValue = BFwrk.Util
							.updateTextFieldToCombo(PF
									+ 'txtAvalGtia',
									NS.cmbAvalGtia.getId());

						}
					}
				}
			});
	NS.storeComboAvalGtia = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			piEmpresaAvalista : 0,
			psDivisa : '',
			noEmpresa:0
		},
		paramOrder : [ 'piEmpresaAvalista', 'psDivisa','noEmpresa' ],
		directFn : AltaFinanciamientoAction.selectComboAvalGtia,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		}, ],
		listeners : {
			load : function(s, records) {
			}
		}
	});
	NS.storeAvaladaGtia = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			piEmpresa : 0,
			psClave : ''
		},
		paramOrder : [ 'piEmpresa', 'psClave' ],
		directFn : AltaFinanciamientoAction.selectAvaladaGtia,
		idProperty : 'montoAvalado',
		fields : [ {
			name : 'montoAvalado'
		}, ],

	});
	NS.cmbAvalGtia = new Ext.form.ComboBox(
			{
				store : NS.storeComboAvalGtia,
				name : PF + 'cmbAvalGtia',
				id : PF + 'cmbAvalGtia',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 75,
				y : 60,
				disabled : true,
				width : 300,
				valueField : 'idStr',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione avales/garantías',
				triggerAction : 'all',
				value : '',
				listeners : {
					select : {
						fn : function(combo, valor) {}
					}
				}
			});
	NS.lblDisponible = new Ext.form.Label({
		text : 'Valor Disponible:',
		x : 400,
		y : 45
	});
	NS.txtDisponible = new Ext.form.TextField({
		id : PF + 'txtDisponible',
		name : PF + 'txtDisponible',
		value : '',
		x : 400,
		y : 60,
		disabled : true,
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtDisponible').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	NS.lblMontoAvalado = new Ext.form.Label({
		text : 'Monto Avalado:',
		x : 0,
		y : 45
	});
	NS.txtMontoAvalado = new Ext.form.TextField(
			{
				id : PF + 'txtMontoAvalado',
				name : PF + 'txtMontoAvalado',
				value : '',
				x : 0,
				y : 60,
				disabled : true,
				width : 150,
				listeners : {
					change : {
						fn : function(caja, valor) {
							Ext.getCmp(PF +'txtMontoAvalado').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF +'txtMontoAvalado').getValue()));
						}
					},
				}
			});
	NS.editar=function(valor){
		NS.txtEmpresaA.setDisabled(valor);
		NS.cmbEmpresaA.setDisabled(valor);
		NS.txtMontoAvalado.setDisabled(valor);
	}
	NS.limpia=function(){
		NS.txtEmpresaA.setValue("");
		NS.cmbEmpresaA.reset();
		NS.txtMontoAvalado.setValue("0.00");
	}
	NS.cancelarA=function(){
		NS.editar(true);
		NS.limpia()
		Ext.getCmp(PF+'cmdCancelar').setDisabled(true);
	}
	NS.crearNuevoA=function(){
		NS.editar(false);
		NS.limpia()
		Ext.getCmp(PF+'cmdCancelar').setDisabled(false);
	}
	validarDatosA=function(){
		var band=false;
		var vdTotal=0;
		vdTotal=parseFloat(BFwrk.Util.unformatNumber(NS.txtMontoAvalado.getValue()))+parseFloat(BFwrk.Util.unformatNumber(NS.txtTotMontoAsig.getValue()));
		if(vdTotal>BFwrk.Util.unformatNumber(NS.txtMontoGarantia.getValue())){
			Ext.Msg.alert("SET","El monto avalado supera al monto de la garantía");
			Ext.getCmp(PF + 'txtMontoAvalado').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'txtEmpresaA').getValue() == ""||Ext.getCmp(PF + 'txtEmpresaA').getValue() ==0){
			Ext.Msg.alert("SET","Seleccione una Empresa para Avalar");
			Ext.getCmp(PF + 'txtEmpresa').focus();
			return;
		}
		else if (Ext.getCmp(PF + 'txtMontoAvalado').getValue() <=0) {
			Ext.Msg.alert("SET","Digite el monto para avalar");
			Ext.getCmp(PF + 'txtMontoAvalado').focus();
			return;
		}
		band=true;
		return band;
	}
	NS.aceptarA=function(){
		if(!validarDatosA())
			return;
		AvalesGarantiasFCAction.insertaAsignacionEmp(Ext.getCmp(PF + 'txtEmpresa').getValue(),
				Ext.getCmp(PF + 'txtClave').getValue(),
				parseInt(Ext.getCmp(PF + 'txtEmpresaA').getValue()),
				parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtMontoAvalado').getValue())),
				function(mapResult,e) {
			BFwrk.Util.msgWait('Terminado...',false);
			if (mapResult.msgUsuario !== null
					&& mapResult.msgUsuario !== ''
						&& mapResult.msgUsuario != undefined) {
				for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
					Ext.Msg.alert('Información SET',''+ mapResult.msgUsuario[msg]);
				}
				NS.cancelarA();
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeGridAvaladas,
					msg : "Cargando..."
				});
				NS.storeGridAvaladas.baseParams.piEmpresa=Ext.getCmp(PF +'txtEmpresa').getValue();
				NS.storeGridAvaladas.baseParams.psClave=Ext.getCmp(PF +'txtClave').getValue();
				NS.storeGridAvaladas.load();
			}
		});


	}
	NS.eliminar=function(){
		var registros = NS.gridDatos2.getSelectionModel()
		.getSelections();
		if (registros.length >0) {
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
			.confirm(
					'SET',
					'Se va a eliminar el registro seleccionado, ¿Desea continuar?',
					function(btn) {
						if (btn === 'yes') {
							BFwrk.Util.msgWait(
									'Ejecutando solicitud...',
									true);
							AvalesGarantiasFCAction
							.existeAvalGtiaLinea(
									Ext.getCmp(PF +'txtEmpresa').getValue(),
									Ext.getCmp(PF +'txtClave').getValue(), 
									parseInt(registros[0].get('noEmpresa')),function(mapResult,e) {
										BFwrk.Util.msgWait('Terminado...',false);
										if (mapResult.result==1){
											NS.MB2
											.confirm(
													'SET',
													'Empresa con créditos vigentes desea continuar, ¿Desea continuar?',
													function(btn) {
														if (btn === 'yes') {
															AvalesGarantiasFCAction.deleteAvalada(
																	Ext.getCmp(PF +'txtEmpresa').getValue(),
																	Ext.getCmp(PF +'txtClave').getValue(), 
																	parseInt(registros[0].get('noEmpresa')),function(mapResult,e) {
																		BFwrk.Util.msgWait('Terminado...',false);
																		for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
																			Ext.Msg
																			.alert('Información SET',''+ mapResult.msgUsuario[msg]);
																		}

																		var myMask = new Ext.LoadMask(Ext.getBody(), {
																			store : NS.storeGridAvaladas,
																			msg : "Cargando..."
																		});
																		NS.storeGridAvaladas.baseParams.piEmpresa=Ext.getCmp(PF +'txtEmpresa').getValue();
																		NS.storeGridAvaladas.baseParams.psClave=Ext.getCmp(PF +'txtClave').getValue();
																		NS.storeGridAvaladas.load();
																		NS.cancelar();
																	});
														}
														else{
															return;
														}
													});
										}
										else{
											if (mapResult.msgUsuario !== null
													&& mapResult.msgUsuario !== ''
														&& mapResult.msgUsuario != undefined) {
												for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
													Ext.Msg.alert('Información SET',''+ mapResult.msgUsuario[msg]);
												}

												var myMask = new Ext.LoadMask(Ext.getBody(), {
													store : NS.storeGridAvaladas,
													msg : "Cargando..."
												});
												NS.storeGridAvaladas.baseParams.piEmpresa=Ext.getCmp(PF +'txtEmpresa').getValue();
												NS.storeGridAvaladas.baseParams.psClave=Ext.getCmp(PF +'txtClave').getValue();
												NS.storeGridAvaladas.load();
												NS.cancelar();
											}
										}
									});
						}
						else{
							return;
						}
					}
			);
		} else{
			BFwrk.Util.msgShow('Debe seleccionar un registro para eliminar.','WARNING');
			return;
		}
	}
	NS.cmdCrearNvo = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCrearNvo',
		name : PF + 'cmdCrearNvo',
		text : 'Crear Nuevo',
		x : 680,
		y : 0,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.crearNuevoA();
				}
			}
		}
	});

	NS.storeGridAvaladas = new Ext.data.DirectStore(
			{
				paramsAsHash : false,
				root : '',
				baseParams : {
					piEmpresa : '',
					psClave : ''
				},
				paramOrder : [ 'piEmpresa', 'psClave' ],
				directFn : AvalesGarantiasFCAction.selectAvaladas,
				fields : [ {
					name : 'noEmpresa'
				}, {
					name : 'nomEmpresa'
				}, {
					name : 'montoAvalado'
				}, {
					name : 'montoDispuesto'
				}, {
					name : 'disponible'
				}],
				listeners : {
					load : function(s, records) {
						var vdTotal=0;
						if(records.length>0){
							for(var i=0;i<records.length;i++){
								vdTotal=vdTotal+parseFloat(records[i].get('montoAvalado'));
							}
							NS.txtTotMontoAsig.setValue(BFwrk.Util.formatNumber(vdTotal));
						}else{
							NS.txtTotMontoAsig.setValue('0.00');
						}
						NS.txtDisponibleA.setValue(BFwrk.Util.formatNumber(BFwrk.Util.unformatNumber(NS.txtMontoGarantia.getValue())-BFwrk.Util.unformatNumber(NS.txtTotMontoAsig.getValue())));
					},
				}



			});
	NS.smE4 = new Ext.grid.CheckboxSelectionModel({
		singleSelect : true
	});

	NS.gridDatos2 = new Ext.grid.GridPanel({
		store : NS.storeGridAvaladas,
		id : PF + 'gridDatos2',
		name : PF + 'gridDatos2',
		frame : true,
		autowidth : true,
		autoScroll : true,
		height : 300,
		x : 0,
		y : 60,
		cm : new Ext.grid.ColumnModel({
			defaults : {
				width : 120,
				value : true,
				sortable : true
			},
			columns : [ NS.smE4, {
				id : 'noEmpresa',
				header : 'No. Empresa',
				width : 100,
				dataIndex : 'noEmpresa',
				direction : 'ASC'
			}, {
				id : 'nomEmpresa',
				header : 'Empresa',
				width : 250,
				dataIndex : 'nomEmpresa',

			}, {

				id : 'montoAvalado',
				header : 'Monto Avalado',
				width : 150,
				dataIndex : 'montoAvalado',
				renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00')},
			}, {
				id : 'montoDispuesto',
				header : 'Monto Dispuesto',
				width : 150,
				dataIndex : 'montoDispuesto',
				renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00')},
			}, {
				id : 'disponible',
				header : ' Monto Disponible',
				width : 150,
				dataIndex : 'disponible',
				renderer  : function(value) { return Ext.util.Format.number(value, '0,0.00')},
			},
			]
		}),
		viewConfig : {
			getRowClass : function(record, index) {
			}
		},
		sm : NS.smE4,

	});
	
	NS.winAsignacion = new Ext.Window(
			{
				title : 'Asignación de Avales y Garantías por Empresa (Créditos bancarios)',
				modal : true,
				shadow : true,
				closeAction : 'hide',
				width : 850,
				height : 600,
				layout : 'absolute',
				// plain : true,
				triggerAction : 'all',
				bodyStyle : 'padding:10px;',
				buttonAlign : 'center',
				draggable : true,
				resizable : false,
				autoScroll : true,
				items : [
				         {

				        	 xtype : 'fieldset',
				        	 title : '',
				        	 id : PF + 'panelGridAvales',
				        	 name : PF + 'panelGridAvales',
				        	 x : 0,
				        	 y : 0,
				        	 autowidth : true,
				        	 height : 430,
				        	 layout : 'absolute',
				        	 items : [ NS.lblEmpresaAvalista,
				        	           NS.txtEmpresaAvalista,
				        	           NS.lblDescripcionA,
				        	           NS.txtDescripcionA,
				        	           NS.lblMontoGarantia,
				        	           NS.txtMontoGarantia,
				        	           NS.lblDisponibleA,
				        	           NS.txtDisponibleA,
				        	           NS.gridDatos2,
				        	           NS.lblTotMontoAsig,
				        	           NS.txtTotMontoAsig ]
				         },
				         {
				        	 xtype : 'fieldset',
				        	 title : '',
				        	 id : PF + 'panelAvales',
				        	 name : PF + 'panelAvales',
				        	 x : 0,
				        	 y : 440,
				        	 autowidth : true,
				        	 height : 110,
				        	 layout : 'absolute',
				        	 items : [ NS.lblEmpresaA,
				        	           NS.txtEmpresaA,
				        	           NS.cmbEmpresaA,
				        	           NS.lblMontoAvalado,
				        	           NS.txtMontoAvalado,
				        	           NS.cmdCrearNvo
				        	           ]
				         }, ],
				         buttons:[
				                  {
				                	  text: 'Aceptar',
				                	  handler: function(){
				                		  NS.aceptarA();
				                	  }
				                  },{
				                	  text: 'Cancelar',
				                	  name:PF+'cmdCancelar',
				                	  disabled:true,
				                	  handler: function(){
				                		  NS.cancelarA();
				                	  }
				                  },
				                  {
				                	  text: 'Eliminar',
				                	  handler: function(){
				                		  NS.eliminar();
				                	  }
				                  },
				                  
				                  ],
				                  listeners : {
				                	  show : {
				                		  fn:function(){
				                			  NS.txtEmpresaAvalista.setValue(NS.cmbEmpresa.getValue());
				                			  NS.txtMontoGarantia.setValue(Ext.getCmp(PF +'txtValor').getValue());
				                			  NS.txtDescripcionA.setValue(Ext.getCmp(PF +'txtDescripcion').getValue());
				                			  var myMask = new Ext.LoadMask(Ext.getBody(), {
				                				  store : NS.storeCmbEmpresaA,
				                				  msg : "Cargando..."
				                			  });
				                			  NS.storeCmbEmpresaA.baseParams.piNoUsuario=parseInt(NS.idUsuario);
				                			  NS.storeCmbEmpresaA.baseParams.pbMismaEmpresa=false;
				                			  NS.storeCmbEmpresaA.load();
				                			  var myMask = new Ext.LoadMask(Ext.getBody(), {
				                				  store : NS.storeGridAvaladas,
				                				  msg : "Cargando..."
				                			  });
				                			  NS.storeGridAvaladas.baseParams.piEmpresa=Ext.getCmp(PF +'txtEmpresa').getValue();
				                			  NS.storeGridAvaladas.baseParams.psClave=Ext.getCmp(PF +'txtClave').getValue();
				                			  NS.storeGridAvaladas.load();

				                		  }
				                	  },


				                	  hide : {
				                		  fn:function(){
				                			  NS.txtEmpresaAvalista.setValue('');
				                			  NS.txtMontoGarantia.setValue('');
				                			  NS.txtDescripcionA.setValue('');
				                			  NS.storeCmbEmpresaA.removeAll();
				                			  NS.storeGridAvaladas.removeAll();
				                			  NS.txtDisponibleA.setValue('');
				                			  NS.cmbEmpresaA.reset();
				                		  }
				                	  }
				                  }

			});
	//Fin ventana asignación

	//Inicio ventana avaladas
	NS.storeAvaladas = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			vsTipoGtia:0
		},
		paramOrder : ['vsTipoGtia'],
		directFn : AvalesGarantiasFCAction.reporteAvalesGtiasAvaladas,
		fields : [ {
			name : 'noEmpresaS'
		}, {
			name : 'nomEmpresa'
		},  {
			name : 'clave'
		}, {
			name : 'descripcion'
		}, {
			name : 'garantiaAsignada'
		}, {
			name : 'montoAsignado'
		}, {
			name : 'banco'
		}, {
			name : 'descBanco'
		}, {
			name : 'idFinanciamiento'
		}, {
			name : 'credito'
		}, {
			name : 'montoAvaladoS'
		}, {
			name : 'dispuesto'
		}, {
			name : 'montoDisponible'
		}, {
			name : 'identifica'
		},{
			name : 'color'
		},
		],
		listeners : {
			load : function(s, records) {
				if(records.length<=0){
					Ext.Msg.alert('SET','No existen datos para la consulta');
					NS.winAvaladas.hide();
					return;
				}
			}
		}
	});

	NS.gridAvaladas = new Ext.grid.GridPanel({
		store : NS.storeAvaladas,
		id : PF + 'gridAvaladas',
		name : PF + 'gridAvaladas',
		frame : true,
		autoScroll : true,
		autowidth : true,
		height : 510,
		x : 0,
		y : 0,
		cm : new Ext.grid.ColumnModel({
			defaults : {
				width : 120,
				value : true,
			},
			columns : [
			           {
			        	   id : '',
			        	   header : 'No. Empresa',
			        	   width :0 ,
			        	   dataIndex : 'noEmpresaS',
			        	   direction : 'ASC',
			        	   hidden : true,
			        	   hideable : false
			           },{
			        	   id : 'nomEmpresa',
			        	   header : 'Empresa Avalada',
			        	   width : 350,
			        	   dataIndex : 'nomEmpresa',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   id : 'clave',
			        	   header :'ID Aval',
			        	   width : 100,
			        	   dataIndex : 'clave',
			        	   /*hidden : true,
			        	   hideable : false,*/
			           },
			           {
			        	   header : 'Aval/Garantía',
			        	   width : 350,
			        	   dataIndex : 'descripcion',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : '% Garantía asignada',
			        	   width : 100,
			        	   dataIndex : 'garantiaAsignada',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : 'Monto Asignado',
			        	   width : 100,
			        	   dataIndex : 'montoAsignado',
			        	   align :'right',

			           },
			           {
			        	   id : 'banco',
			        	   header : '',
			        	   width : 0,
			        	   dataIndex : 'banco',
			        	   hidden : true,
			        	   hideable : false,
			           },
			           {
			        	   header : 'Banco',
			        	   width : 120,
			        	   dataIndex : 'descBanco',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : 'Línea',
			        	   width : 100,
			        	   dataIndex : 'idFinanciamiento',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : 'Crédito',
			        	   width : 100,
			        	   dataIndex : 'credito',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : 'Monto Asignado/Avalado',
			        	   width : 100,
			        	   dataIndex : 'montoAvaladoS',
			        	   align :'right',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : 'Dispuesto',
			        	   width :100,
			        	   dataIndex : 'dispuesto',
			        	   align :'right',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : 'Disponible',
			        	   width : 100,
			        	   align :'right',
			        	   dataIndex : 'montoDisponible',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : '',
			        	   width : 0,
			        	   dataIndex : 'identifica',
			        	   hidden : true,
			        	   hideable : false,
			           }
			           ]
		}),
	});


	NS.formAvaladas = new Ext.form.FormPanel({
		title : '',
		autowidth : true,
		height : 510,
		layout : 'absolute',
		id : PF + 'formAvaladas',
		name : PF + 'formAvaladas',

		items : [
		         NS.gridAvaladas,
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonAva',
		        	 id:'hdJasonAva',
		        	 value: ''
		         },

		         ],
	});


	NS.formAvaladas2 = new Ext.form.FormPanel({
		title : '',
		autowidth : true,
		height : 550,
		hidden:true,
		layout : 'absolute',
		id : PF + 'formAvaladas2',
		name : PF + 'formAvaladas2',
		autoScroll : true,
		items : [
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonAva2',
		        	 id:'hdJasonAva2',
		        	 value: ''
		         },
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'nomEmp2',
		        	 id:'nomEmp2',
		        	 value: ''
		         },
		         ],
	});

	NS.winAvaladas = new Ext.Window(
			{
				title : 'Consulta Empresas Avaladas (Créditos bancarios)',
				modal : true,
				shadow : true,
				closeAction : 'hide',
				width : 1200,
				height : 590,
				layout : 'absolute',
				plain : true,
				bodyStyle : 'padding:10px;',
				buttonAlign : 'right',
				draggable : true,
				resizable : true,
				autoScroll : true,
				items : [NS.formAvaladas,NS.formAvaladas2],
				buttons:[
				         {
				        	 text: 'Exportar Excel',
				        	
				        	 handler: function(){
				        		 NS.generaReporteAvaladas(true);
				        	 }
				         },
				         {
				        	 text: 'Imprimir',
				        	 hidden:true,
				        	 handler: function(){
				        		 NS.generaReporteAvaladas(false);
				        	 }
				         },
				         ],
				         listeners : {
				        	 show : {
				        		 fn : function() {
				        			 var vsTipoGtia = '0';
				        			 if(NS.cmbTipoGtia.getRawValue()!='****TODAS****')
				        				 vsTipoGtia=NS.cmbTipoGtia.getValue();
				        			 NS.storeAvaladas.baseParams.vsTipoGtia=parseInt(vsTipoGtia);
				        			 var myMask = new Ext.LoadMask(Ext.getBody(), {
				        				 store : NS.storeAvaladas,
				        				 msg : "Cargando..."
				        			 });
				        			 NS.storeAvaladas.load();
				        		 }
				        	 },
				        	 hide : {
				        		 fn : function() {}
				        	 }
				         }
			});

	//Fin ventana avaladas
	//Inicio ventana avalistas
	NS.storeAvalistas = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			vsTipoGtia:0
		},
		paramOrder : ['vsTipoGtia'],
		directFn : AvalesGarantiasFCAction.reporteAvalesGtiasAvalistas,
		fields : [ {
			name : 'noEmpresaS'
		}, {
			name : 'nomEmpresa'
		},  {
			name : 'clave'
		}, {
			name : 'descripcion'
		}, {
			name : 'montoAvaladoS'
		}, {
			name : 'dispuesto'
		}, {
			name : 'dispuestoReal'
		}, {
			name : 'montoDisponible'
		}, {
			name : 'disponibleReal'
		},  {
			name : 'identifica'
		},{
			name : 'color'
		},
		],
		listeners : {
			load : function(s, records) {
				if(records.length<=0){
					Ext.Msg.alert('SET','No existen datos para la consulta');
					NS.winAvalistas.hide();
					return;
				}
			}
		}
	});

	NS.gridAvalistas = new Ext.grid.GridPanel({
		store : NS.storeAvalistas,
		id : PF + 'gridAvalistas',
		name : PF + 'gridAvalistas',
		frame : true,
		autoScroll : true,
		autowidth : true,
		height : 510,
		x : 0,
		y : 0,
		cm : new Ext.grid.ColumnModel({
			defaults : {
				width : 120,
				value : true,
			},
			columns : [
			           {
			        	   id : '',
			        	   header : 'No. Empresa',
			        	   width :0 ,
			        	   dataIndex : 'noEmpresaS',
			        	   direction : 'ASC',
			        	   hidden : true,
			        	   hideable : false
			           },{
			        	   id : 'nomEmpresa',
			        	   header : 'Avalista',
			        	   width : 350,
			        	   dataIndex : 'nomEmpresa',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   id : 'clave',
			        	   header :'ID Aval',
			        	   width : 0,
			        	   dataIndex : 'clave',
			        	   hidden : true,
			        	   hideable : false,
			           },
			           {
			        	   header : 'Aval/Garantía',
			        	   width : 350,
			        	   dataIndex : 'descripcion',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : 'Monto Avalado',
			        	   width : 100,
			        	   dataIndex : 'montoAvaladoS',
			        	   align :'right',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },

			           {
			        	   header : 'Dispuesto',
			        	   width :100,
			        	   dataIndex : 'dispuesto',
			        	   align :'right',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, 
			           {
			        	   header : 'Dispuesto Real',
			        	   width : 100,
			        	   align :'right',
			        	   dataIndex : 'dispuestoReal',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : 'Disponible',
			        	   width : 100,
			        	   align :'right',
			        	   dataIndex : 'montoDisponible',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }

			           }, 

			           {
			        	   header : 'Disponible Real',
			        	   width : 100,
			        	   // align :'right',
			        	   dataIndex : 'disponibleReal',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }

			           } ,
			           {
			        	   header : '',
			        	   width : 0,
			        	   dataIndex : 'identifica',
			        	   hidden : true,
			        	   hideable : false,
			           }
			           ]
		}),
		viewConfig : {
			getRowClass : function(record, index) {
			}
		},
		listeners : {
			click : {
				fn : function(grid) {
				}
			},
			dblclick : {
				fn : function(grid) {}
			}
		}
	});

	NS.generaReporteAvaladas=function(excel){
		if(NS.storeAvaladas.getCount()<=0){
			Ext.Msg.alert("SET","No existen registros.");
			return;
		}
		else{
			var matrizAvaladas = new Array();
			for (var i = 0; i < NS.storeAvaladas.data.length; i++) {
				var registro=NS.storeAvaladas.getAt(i);
				var registroAvaladas= {};
				registroAvaladas.color = registro.data.color;
				registroAvaladas.nomEmpresa = registro.data.nomEmpresa;
				registroAvaladas.descripcion = registro.data.descripcion;
				registroAvaladas.garantiaAsignada = registro.data.garantiaAsignada;
				registroAvaladas.montoAsignado = registro.data.montoAsignado;
				registroAvaladas.descBanco = registro.data.descBanco;
				registroAvaladas.idFinanciamiento = registro.data.idFinanciamiento;
				registroAvaladas.credito = registro.data.credito;
				registroAvaladas.montoAvaladoS = registro.data.montoAvaladoS;
				registroAvaladas.dispuesto = registro.data.dispuesto;
				registroAvaladas.montoDisponible = registro.data.montoDisponible;
				matrizAvaladas[i] = registroAvaladas;
			}
			var jsonStringAvaladas= Ext.util.JSON.encode(matrizAvaladas);
			if(excel){
				Ext.getCmp('hdJasonAva').setValue(jsonStringAvaladas);
				var forma = NS.formAvaladas.getForm();
				forma.url='/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp?nomReporte=excelAvaladas';
			}
			else{
				Ext.getCmp('hdJasonAva2').setValue(jsonStringAvaladas);
				var vsTipoGtia =NS.GI_NOM_EMPRESA ;
				if(NS.cmbEmpresaBusca.getValue()!=0)
					vsTipoGtia=NS.cmbTipoGtia.getRawValue();
				Ext.getCmp('nomEmp2').setValue(vsTipoGtia);
				var forma = NS.formAvaladas2.getForm();
				forma.url = '/SET/jsp/Reportes.jsp?nomReporte=ReporteEmpresasAvaladas';
			}
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
	NS.generarReporteAvalistas=function(excel){
		if(NS.storeAvalistas.getCount()<=0){
			Ext.Msg.alert("SET","No existen registros.");
			return;
		}
		else{
			var matrizAvalistas = new Array();
			for (var i = 0; i < NS.storeAvalistas.data.length; i++) {
				var registro=NS.storeAvalistas.getAt(i);
				var registroAvalistas= {};
				registroAvalistas.color = registro.data.color;
				registroAvalistas.nomEmpresa = registro.data.nomEmpresa;
				registroAvalistas.descripcion = registro.data.descripcion;
				registroAvalistas.montoAvaladoS = registro.data.montoAvaladoS;
				registroAvalistas.dispuesto = registro.data.dispuesto;
				registroAvalistas.dispuestoReal = registro.data.dispuestoReal;
				registroAvalistas.montoDisponible = registro.data.montoDisponible;
				registroAvalistas.disponibleReal = registro.data.disponibleReal;
				matrizAvalistas[i] = registroAvalistas;
			}
			var jsonStringAvalistas= Ext.util.JSON.encode(matrizAvalistas);
			if(excel){
				Ext.getCmp('hdJasonAv').setValue(jsonStringAvalistas);
				var forma = NS.formAvalistas.getForm();
				forma.url='/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp?nomReporte=excelAvalistas';
			}
			else{
				Ext.getCmp('hdJasonAv2').setValue(jsonStringAvalistas);
				var vsTipoGtia =NS.GI_NOM_EMPRESA ;
				if(NS.cmbEmpresaBusca.getValue()!=0)
					vsTipoGtia=NS.cmbTipoGtia.getRawValue();
				Ext.getCmp('nomEmp').setValue(vsTipoGtia);
				var forma = NS.formAvalistas2.getForm();
				forma.url = '/SET/jsp/Reportes.jsp?nomReporte=ReporteEmpresasAvalistas';
			}
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


	NS.formAvalistas = new Ext.form.FormPanel({
		title : '',
		autowidth : true,
		height : 510,
		layout : 'absolute',
		id : PF + 'formAvalistas',
		name : PF + 'formAvalistas',
		url:'',
		items : [
		         NS.gridAvalistas,
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonAv',
		        	 id:'hdJasonAv',
		        	 value: ''
		         },
		         ],

	});


	NS.formAvalistas2 = new Ext.form.FormPanel({
		title : '',
		height : 510,
		layout : 'absolute',
		id : PF + 'formAvalistas2',
		name : PF + 'formAvalistas2',
		hidden:true,
		items : [
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonAv2',
		        	 id:'hdJasonAv2',
		        	 value: ''
		         },
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'nomEmp',
		        	 id:'nomEmp',
		        	 value: ''
		         },

		         ],
	});
	NS.winAvalistas = new Ext.Window({
		title : 'Consulta Empresas Avalistas (Créditos bancarios)',
		modal : true,
		shadow : true,
		closeAction : 'hide',
		width : 1200,
		height : 590,
		layout : 'absolute',
		plain : true,
		bodyStyle : 'padding:10px;',
		buttonAlign : 'right',
		draggable : true,
		resizable : true,
		autoScroll : true,
		items : [NS.formAvalistas,
		         NS.formAvalistas2,
		         ],
		         buttons:[
		                  {
		                	  text: 'Exportar Excel',
		                	  handler: function(){
		                		  NS.generarReporteAvalistas(true);
		                	  }
		                  },
		                  {
		                	  text: 'Imprimir',
		                	  hidden:true,
		                	  handler: function(){
		                		  NS.generarReporteAvalistas(false);
		                	  }
		                  },
		                  ],
		                  listeners : {
		                	  show : {
		                		  fn : function() {
		                			  var vsTipoGtia = '0';
		                			  if(NS.cmbTipoGtia.getRawValue()!='****TODAS****')
		                				  vsTipoGtia=NS.cmbTipoGtia.getValue();
		                			  NS.storeAvalistas.baseParams.vsTipoGtia=parseInt(vsTipoGtia);
		                			  var myMask = new Ext.LoadMask(Ext.getBody(), {
		                				  store : NS.storeAvalistas,
		                				  msg : "Cargando..."
		                			  });
		                			  NS.storeAvalistas.load();
		                		  }
		                	  },
		                	  hide : {
		                		  fn : function() {}
		                	  }
		                  }
	});



	//Fin ventana avalistas

	NS.avalesGarantiasC = new Ext.form.FormPanel({
		title: 'Registro de avales y garantías',
		width: 1100,
		height: 1100,
		padding: 10,
		layout: 'absolute',
		id: PF + 'avalesGarantiasC',
		name: PF + 'avalesGarantiasC',
		renderTo: NS.tabContId,
		frame: true,
		autoScroll: true,
		items : [
		         { 
		        	 xtype: 'fieldset',
		        	 title: 'Tipo',
		        	 width: 1050,
		        	 height: 100,
		        	 x: 0,
		        	 y: 0,
		        	 layout: 'absolute',
		        	 id: 'fSetPrincipal',
		        	 items: [
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Empresa / Persona:',
		        	        	 x: 1,
		        	        	 y: 0
		        	         },{
		        	        	 xtype: 'textfield',
		        	        	 x: 1,
		        	        	 y: 20,
		        	        	 width: 60,
		        	        	 name: PF + 'txtEmpresaBusca',
		        	        	 id: PF + 'txtEmpresaBusca',
		        	        	 value:0,
		        	        	 listeners:{
		        	        		 change: {
		        	        			 fn: function(box, value) {
		        	        				 var value = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresaBusca', NS.cmbEmpresaBusca.getId());
		        	        				 if(Ext.getCmp(PF +'txtEmpresaBusca').getValue()==''){
		        	        					 Ext.Msg.alert("SET","Empresa inexistente.");
		        	        					 Ext.getCmp(PF +'txtEmpresaBusca').setValue('0');
		        	        					 NS.cmbEmpresaBusca.setValue('0');
		        	        					 NS.cmbEmpresaBusca.setRawValue('****TODAS****');
		        	        				 }
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         NS.cmbEmpresaBusca,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Tipo de garantía:',
		        	        	 x: 400,
		        	        	 y: 0
		        	         },
		        	         NS.cmbTipoGtia,
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Buscar',
		        	        	 x: 900,
		        	        	 y: 20,
		        	        	 width: 90,
		        	        	 id: PF + 'btnBuscar',
		        	        	 name: PF + 'btnBuscar',
		        	        	 listeners:{
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
		        	 width: 1050,
		        	 height: 250,
		        	 x: 0,
		        	 y: 110,
		        	 layout: 'absolute',
		        	 id: 'fSetSec2',
		        	 items: [NS.gridAvalG]
		         },
		         { 
		        	 xtype: 'fieldset',
		        	 title: '',
		        	 width: 1050,
		        	 height: 300,
		        	 x: 0,
		        	 y:370,
		        	 layout: 'absolute',
		        	 id: 'fSetSec3',
		        	 items: [

		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Avalista:',
		        	        	 x: 1,
		        	        	 y: 0
		        	         },{
		        	        	 xtype: 'textfield',
		        	        	 x: 1,
		        	        	 y: 20,
		        	        	 disabled:true,
		        	        	 width: 70,
		        	        	 name: PF + 'txtEmpresa',
		        	        	 id: PF + 'txtEmpresa',
		        	        	 listeners:{
		        	        		 change: {
		        	        			 fn: function(box, value){
		        	        				 var value = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
		        	        				 if(Ext.getCmp(PF +'txtEmpresa').getValue()==''){
		        	        					 Ext.Msg.alert("SET","Empresa inexistente.");
		        	        					 Ext.getCmp(PF +'txtEmpresa').setValue('');
		        	        					 NS.cmbEmpresa.reset();
		        	        				 }
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         NS.cmbEmpresa,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Tipo de garantía:',
		        	        	 x: 690,
		        	        	 y: 0
		        	         },
		        	         NS.cmbTipo,
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Crear Nuevo',
		        	        	 x: 920,
		        	        	 y: 0,
		        	        	 width: 90,
		        	        	 id: PF + 'btnCrearNuevo',
		        	        	 name: PF + 'btnCrearNuevo',
		        	        	 disabled: false,
		        	        	 listeners:{
		        	        		 click:{
		        	        			 fn: function(btn)
		        	        			 {
		        	        				 NS.crearNuevo();
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Clave:',
		        	        	 x: 1,
		        	        	 y: 50
		        	         },
		        	         NS.txtClave,

		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Fecha inicial:',
		        	        	 x: 150,
		        	        	 y: 50
		        	         },
		        	         NS.txtFecIni,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Fecha Final:',
		        	        	 x: 310,
		        	        	 y: 50
		        	         },
		        	         NS.txtFecFin,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Valor:',
		        	        	 x: 490,
		        	        	 y: 50
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 490,
		        	        	 y: 70,
		        	        	 width: 60,
		        	        	 disabled:true,
		        	        	 name: PF + 'txtValor',
		        	        	 id: PF + 'txtValor',
		        	        	 maxLength : 8,
		        	        	 listeners: {
		        	        		 change:{
		        	        			 fn: function(box, value){
		        	        				 Ext.getCmp(PF +'txtValor').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF +'txtValor').getValue()));

		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Divisa:',
		        	        	 x: 690,
		        	        	 y: 50
		        	         },
		        	         NS.txtIdDivisa,
		        	         NS.cmbDivisa,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: 'Aval:',
		        	        	 x:1,
		        	        	 y: 100
		        	         },
		        	         NS.txtDescripcion,
		        	         NS.chkGtiaEspecial,
		        	         {
		        	        	 xtype: 'label',
		        	        	 text: '% Garantía:',
		        	        	 x: 720,
		        	        	 y: 125
		        	         },
		        	         {
		        	        	 xtype: 'textfield',
		        	        	 x: 780,
		        	        	 y: 120,
		        	        	 width: 120,
		        	        	 disabled:true,
		        	        	 name: PF + 'txtPje',
		        	        	 id: PF + 'txtPje',
		        	        	 listeners:{
		        	        		 change:{
		        	        			 fn: function(box, value){
		        	        				 box.setValue(Ext.util.Format.number(value, '0,0.00000'));
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Modificar',
		        	        	 x: 920,
		        	        	 y: 40,
		        	        	 width: 90,
		        	        	 id: PF + 'btnModificar',
		        	        	 name: PF + 'btnModificar',
		        	        	 disabled: true,
		        	        	 listeners:{
		        	        		 click:{
		        	        			 fn: function(btn){
		        	        				 NS.modificar();
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Asignación',
		        	        	 x: 600,
		        	        	 y: 180,
		        	        	 width: 90,
		        	        	 id: PF + 'btnAsignacion',
		        	        	 name: PF + 'btnAsignacion',
		        	        	 disabled: false,
		        	        	 listeners:{
		        	        		 click:{
		        	        			 fn: function(btn) {
		        	        				 var registros = NS.gridAvalG.getSelectionModel()
		        	        				 .getSelections();
		        	        				 if (registros.length <= 0) {
		        	        					 BFwrk.Util
		        	        					 .msgShow(
		        	        							 'Debe seleccionar un registro.',
		        	        					 'WARNING');
		        	        					 return;
		        	        				 } else{
		        	        					 vbSelecciono = true;
		        	        					 NS.winAsignacion.show();
		        	        					 NS.buscar();
		        	        				 }
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Avalistas',
		        	        	 x: 700,
		        	        	 y: 180,
		        	        	 width:90,
		        	        	 id: PF + 'btnAvalistas',
		        	        	 name: PF + 'btnAvalistas',
		        	        	 disabled: false,
		        	        	 listeners:{
		        	        		 click:{
		        	        			 fn: function(btn){
		        	        				 vbModifico = true;
		        	        				 NS.winAvalistas.show();
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Avaladas',
		        	        	 x: 800,
		        	        	 y: 180,
		        	        	 width: 90,
		        	        	 id: PF + 'btnAvaladas',
		        	        	 name: PF + 'btnAvaladas',
		        	        	 disabled: false,
		        	        	 listeners:{
		        	        		 click:{
		        	        			 fn: function(btn){
		        	        				 vbModifico = false;
		        	        				 NS.winAvaladas.show()
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Aceptar',
		        	        	 x: 600,
		        	        	 y: 220,
		        	        	 width: 90,
		        	        	 disabled:true,
		        	        	 id: PF + 'btnAceptar',
		        	        	 name: PF + 'btnAceptar',
		        	        	 disabled: true,
		        	        	 listeners:{
		        	        		 click:{
		        	        			 fn: function(btn){
		        	        				 NS.aceptar();
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         {
		        	        	 xtype: 'button',
		        	        	 text: 'Cancelar',
		        	        	 x: 700,
		        	        	 y: 220,
		        	        	 width: 90,
		        	        	 id: PF + 'btnCancelar',
		        	        	 name: PF + 'btnCancelar',
		        	        	 disabled: true,
		        	        	 listeners:{
		        	        		 click: {
		        	        			 fn: function(btn){
		        	        				 NS.cancelar();
		        	        			 }
		        	        		 }
		        	        	 }
		        	         },
		        	         ]
		         }
		         ]
	});
	NS.buscar();

	NS.avalesGarantiasC.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});