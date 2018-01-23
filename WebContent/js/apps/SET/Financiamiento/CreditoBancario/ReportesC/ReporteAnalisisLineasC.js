//GGonzález

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Financiamiento');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	var vbAceptar;
	var vsTipoMenu='';
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.USR = apps.SET.NOMBRE + '  ' + apps.SET.APELLIDO_PATERNO + '  '
	+ apps.SET.APELLIDO_MATERNO;
	NS.fecHoy = apps.SET.FEC_HOY;
	stringToDate=function(fecha){
		var fec = fecha.split("/");
		var dt = new Date(parseInt(fec[2], 10),
				parseInt(fec[1], 10) - 1, parseInt(fec[0], 10));
		return dt;
	}
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

	NS.lblGpoEmpresas = new Ext.form.Label({
		name: PF + 'lblGpoEmpresas',
		id: PF + 'lblGpoEmpresas',
		text: 'Grupo de Empresas:',
		x: 100,
		y: 0,
		hidden:true,
	});
	NS.txtGpoEmpresas = new Ext.form.NumberField({
		name: PF + 'txtGpoEmpresas',
		id: PF + 'txtGpoEmpresas',
		x: 100,
		y: 15,
		hidden:true,
		width: 45,
		listeners:{
			blur:{
				fn:function(field){
					if(NS.txtGpoEmpresas.getValue()==''){
						NS.txtGpoEmpresas.setValue('');
						NS.cmbGpoEmpresa.reset();
					}else{
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtGpoEmpresas', NS.cmbGpoEmpresa.getId());
					}
				}
			}
		}
	});
	//Grupo de empresas
	NS.storeCmbGpoEmpresa = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		directFn : ReporteAnalisisLineasCAction.obtenerGruposEmpresa,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeCmbGpoEmpresa,
					msg : "Cargando..."
				});
				NS.storeCmbEmpresa.removeAll();
			}
		}
	});

	NS.cmbGpoEmpresa = new Ext.form.ComboBox({
		store: NS.storeCmbGpoEmpresa,
		name: PF + 'cmbGpoEmpresa',
		id: PF + 'cmbGpoEmpresa',
		emptyText: 'Seleccione un grupo de empresas',
		triggerAction: 'all',
		x: 150,
		y: 15,
		hidden:true,
		width: 300,
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					if(NS.cmbGpoEmpresa.getValue()==''){
						NS.txtGpoEmpresas.setValue('');
						NS.cmbGpoEmpresa.reset();
					}else{
						var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtGpoEmpresas', NS.cmbGpoEmpresa.getId());
						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeCmbEmpresa,
							msg : "Cargando..."
						});
						NS.storeCmbEmpresa.baseParams.grupo=true;
						NS.storeCmbEmpresa.baseParams.noGrupo=parseInt(NS.cmbGpoEmpresa.getValue());
						NS.storeCmbEmpresa.baseParams.piNoUsuario=parseInt(NS.idUsuario);
						NS.storeCmbEmpresa.load();
					}
				}
			},
			blur:{
				fn:function(combo, valor){
					if(NS.cmbGpoEmpresa.getValue()==''){
						NS.txtGpoEmpresas.setValue('');
						NS.cmbGpoEmpresa.reset();
					}else{
						var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtGpoEmpresas', NS.cmbGpoEmpresa.getId());
					}
				}
			}
		}
	}); 
	NS.lblEmpresas = new Ext.form.Label({
		name: PF + 'lblEmpresas',
		id: PF + 'lblEmpresas',
		text: 'Empresas:',
		x: 550,
		y: 0,

	});
	NS.txtEmpresas = new Ext.form.NumberField({
		name: PF + 'txtEmpresas',
		id: PF + 'txtEmpresas',
		x: 550,
		y: 15,
		width: 45,
		value:0,
		listeners:{
			blur:{
				fn:function(field){
					if(field.getValue()==''){
						NS.txtEmpresas.setValue(0);
						NS.cmbEmpresas.setValue('****TODOS****');
					}
					else{
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresas', NS.cmbEmpresas.getId());
					}
				}
			}
		}
	});
	//Empresas
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash : false,
		baseParams:{
			piNoUsuario:0,
			grupo:false,
			noGrupo:0
		},
		paramOrder : ['piNoUsuario','grupo','noGrupo'],
		root : '',
		directFn : ReporteAnalisisLineasCAction.obtenerEmpresas,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeCmbEmpresa,
					msg : "Cargando..."
				});
				var recordsStore = NS.storeCmbEmpresa.recordType;	
				var todos = new recordsStore({
					id: 0,
					descripcion: '****TODOS****'
				});
				NS.storeCmbEmpresa.insert(0,todos);
				NS.cmbEmpresas.setValue(0);
				NS.cmbEmpresas.setRawValue('****TODOS****');
			}
		}
	});
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa,
		name: PF + 'cmbEmpresas',
		id: PF + 'cmbEmpresas',
		emptyText: 'Seleccione una empresa',
		value:'****TODOS****',
		triggerAction: 'all',
		x: 600,
		y: 15,
		width: 300,
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					if(NS.cmbEmpresas.getValue()==''){
						NS.txtEmpresas.setValue(0);
						NS.cmbEmpresas.setValue(0);
						NS.cmbEmpresas.setRawValue('****TODOS****');
					}else{
						var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtEmpresas', NS.cmbEmpresas.getId());
					}
				}
			},
			blur:{
				fn:function(combo, valor){
					if(NS.cmbEmpresas.getValue()==''){
						NS.txtEmpresas.setValue(0);
						NS.cmbEmpresas.setValue(0);
						NS.cmbEmpresas.setRawValue('****TODOS****');
					}else{
						var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtEmpresas', NS.cmbEmpresas.getId());
					}
				}
			},
		}
	}); 
	//Fechas
	NS.lblFechaIni = new Ext.form.Label({
		text : 'Fecha Inicio:',
		x : 100,
		y : 65
	});
	NS.txtFechaIni = new Ext.form.DateField({
		id : PF + 'txtFechaIni',
		name : PF + 'txtFechaIni',
		format : 'd/m/Y',
		x : 170,
		y : 60,
		allowBlank : false,
		width : 100,
		vtype : 'daterange',
		listeners : {
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			change : {
				fn : function(caja, valor) {
					var fechaFin = Ext.getCmp(
							PF + 'txtFechaFin').getValue();
					if (caja.getValue() == null) {
						Ext.getCmp(PF + 'txtFechaIni').setValue(NS.fecHoy);
						Ext.getCmp(PF + 'txtFechaIni').focus();
					} else {
						if (fechaFin < caja.getValue()) {
							BFwrk.Util.msgShow('La fecha de inicial no puede ser mayor a la final.','ERROR');
							Ext.getCmp(PF + 'txtFechaIni').focus();
							Ext.getCmp(PF + 'txtFechaIni').setValue(NS.fecHoy);
							return;
						} 
					}
				}
			},
			blur : function(datefield) {
				if (datefield.getValue() == null) {
					datefield.setValue(NS.fecHoy);
				}
			},
		}
	});
	NS.lblFechaFin = new Ext.form.Label({
		text : 'Fecha Final:',
		x : 300,
		y : 65
	});
	NS.txtFechaFin = new Ext.form.DateField(
			{
				id : PF + 'txtFechaFin',
				name : PF + 'txtFechaFin',
				format : 'd/m/Y',
				x : 370,
				y : 60,
				width : 100,
				altFormats : 'd-m-Y',
				vtype : 'daterange',
				listeners : {
					render : function(datefield) {
						datefield.setValue(NS.fecHoy);
					},
					change : {
						fn : function(caja, valor) {
							var fechaIni = Ext.getCmp(
									PF + 'txtFechaIni').getValue();
							if (caja.getValue() == null) {
								Ext.getCmp(PF + 'txtFechaFin').setValue(NS.fecHoy);
								Ext.getCmp(PF + 'txtFechaFin').focus();
							} else {
								if (fechaIni > caja.getValue()) {
									BFwrk.Util.msgShow('La fecha de final no puede ser menor a la inicial.','ERROR');
									Ext.getCmp(PF + 'txtFechaFin').focus();
									Ext.getCmp(PF + 'txtFechaFin').setValue(NS.fecHoy);
									return;
								} 
							}
						}
					},
					blur : function(datefield) {
						if (datefield.getValue() == null) {
							datefield.setValue(NS.fecHoy);
						}
					},
				}
			});
	//Tipo financiamiento 

	NS.lblFinanciamiento = new Ext.form.Label({
		xtype: 'label',
		text: 'Tipo Financiamiento:',
		x: 500,
		y: 65,
		id : PF + 'lblFinanciamiento',
		name : PF + 'lblFinanciamiento',
	});
	NS.txtTipoFin = new Ext.form.NumberField({
		name: PF + 'txtTipoFin',
		id: PF + 'txtTipoFin',
		x: 610,
		y: 60,
		width: 45,
		value:0,
		listeners:{
			blur:{
				fn:function(field){
					if(NS.txtTipoFin.getValue()==''){
						NS.txtTipoFin.setValue(0);
						NS.cmbFinanciamiento.setRawValue('****TODOS****');
						NS.cmbFinanciamiento.setValue(0);
					}
					else{
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtTipoFin', NS.cmbFinanciamiento.getId());
					}
				}
			}
		}

	});
	//Empresas
	NS.storeCmbFinanciamiento = new Ext.data.DirectStore({
		paramsAsHash : false,
		baseParams:{vsTipoMenu:vsTipoMenu},
		paramOrder : ['vsTipoMenu'],
		root : '',
		directFn : ReporteAnalisisLineasCAction.obtenerTipoFinanciamiento,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners:{
			load:function(records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeCmbFinanciamiento,
					msg : "Cargando..."
				});
				var recordsStore = NS.storeCmbFinanciamiento.recordType;	
				var todos = new recordsStore({
					id: 0,
					descripcion: '****TODOS****'
				});
				NS.storeCmbFinanciamiento.insert(0,todos);
				NS.cmbFinanciamiento.setValue(0);
				NS.cmbFinanciamiento.setRawValue('****TODOS****');
			}
		}
	});
	NS.storeCmbFinanciamiento.load();
	NS.cmbFinanciamiento = new Ext.form.ComboBox({
		store: NS.storeCmbFinanciamiento,
		name: PF + 'cmbFinanciamiento',
		id: PF + 'cmbFinanciamiento',
		emptyText: 'Seleccione un tipo',
		value:'',
		triggerAction: 'all',
		x: 660,
		y: 60,
		width: 180,
		displayField: 'descripcion',
		valueField: 'id',
		mode: 'local',
		listeners:{
			select:{
				fn:function(combo, valor){
					if(NS.cmbFinanciamiento.getValue()==''){
						NS.txtTipoFin.setValue(0);
						NS.cmbFinanciamiento.setRawValue('****TODOS****');
						NS.cmbFinanciamiento.setValue(0);
					}else{
						var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtTipoFin', NS.cmbFinanciamiento.getId());
					}
				}
			},
			blur:{
				fn:function(combo, valor){
					if(NS.cmbFinanciamiento.getValue()==''){
						NS.txtTipoFin.setValue(0);
						NS.cmbFinanciamiento.setRawValue('****TODOS****');
						NS.cmbFinanciamiento.setValue(0);
					}else{
						var comboValue = BFwrk.Util.updateComboToTextField(PF + 'txtTipoFin', NS.cmbFinanciamiento.getId());
					}
				}
			}
		}
	}); 
	NS.buscar=function(){
		var vbTipoCambio=false;
		var  vsMenu = vsTipoMenu;
		if(NS.proyecto=='CIE'&&NS.cmbGpoEmpresas.getValue()==1&&(vsTipoMenu==""||vsTipoMenu=="B"))
			vsMenu = "', 'B";
		ReporteAnalisisLineasCAction.obtenerValoresDivisa(function(resultado,e) {
			if(resultado==1){
				Ext.Msg.alert("SET","No están dados de alta todos los Tipos de Cambio.");
				return;
			}
			else{
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeDatos,
					msg : "Cargando..."
				});
				NS.storeDatos.baseParams.empresa=parseInt(NS.txtEmpresas.getValue());
				NS.storeDatos.baseParams.tipoFinanciamiento=parseInt(NS.txtTipoFin.getValue());
				NS.storeDatos.baseParams.vbTipoCambio=vbTipoCambio;
				NS.storeDatos.baseParams.vsMenu=vsMenu;
				NS.storeDatos.baseParams.fechaInicio=BFwrk.Util.changeDateToString(''+NS.txtFechaIni.getValue());
				NS.storeDatos.baseParams.fechaFin=BFwrk.Util.changeDateToString(''+NS.txtFechaFin.getValue());
				NS.storeDatos.load();
			}
		});
	}
	//Botón buscar
	NS.btnBuscar = new Ext.Button({
		xtype : 'button',
		id : PF + 'btnBuscar',
		name : PF + 'btnBuscar',
		text : 'Buscar',
		x : 900,
		y : 60,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.buscar();
				}
			}
		}
	});
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			empresa:0,
			tipoFinanciamiento:0,
			vbTipoCambio:false,
			vsMenu:'',
			fechaInicio:'',
			fechaFin:''
		},
		paramOrder : ['empresa','tipoFinanciamiento','vbTipoCambio','vsMenu','fechaInicio','fechaFin'],
		directFn : ReporteAnalisisLineasCAction.obtenerAnalisisLineas,
		fields : [ {
			name : 'grupoEmpresa'
		}, {
			name : 'noEmpresa'
		},  {
			name : 'nomEmpresa'
		}, {
			name : 'tipoCredito'
		},{
			name : 'idFinanciamiento'
		}, {
			name : 'noBanco'
		}, {
			name : 'descBanco'
		}, {
			name : 'linea'
		}, {
			name : 'pasivo'
		}, {
			name : 'tasa'
		}, {
			name : 'fecVencimiento'
		}, {
			name : 'cartas'
		}, {
			name : 'factoraje'
		}, {
			name : 'anticipagos'
		}, {
			name : 'namig'
		},{
			name : 'totalLinea'
		},{
			name : 'totalLineaDisp'
		},{
			name : 'garantiasAvales'
		},{
			name : 'color'
		}
		],
		listeners : {
			load : function(s, records) {
				if(records.length<=0){
					Ext.Msg.alert('SET','No existen datos con los criterios de búsqueda seleccionados.');
					return;
				}
			}
		}
	});

	NS.gridDatos = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		id : PF + 'gridDatos',
		name : PF + 'gridDatos',
		frame : true,
		autoScroll : true,
		autowidth : true,
		height : 340,
		x : 0,
		y : 0,
		cm : new Ext.grid.ColumnModel({
			defaults : {
				width : 120,
				value : true,
			},
			columns : [
			           {
			        	   id : 'grupoEmpresa',
			        	   header : 'Gpo. Empresa',
			        	   width :100 ,
			        	   dataIndex : 'grupoEmpresa',
			        	   hidden : true,
			        	   hideable : false
			           },{
			        	   id : 'noEmpresa',
			        	   width : 0,
			        	   dataIndex : 'noEmpresa',
			        	   hidden : true,
			        	   hideable : false,
			           },
			           {
			        	   id : 'nomEmpresa',
			        	   header :'Empresa',
			        	   width : 250,
			        	   dataIndex : 'nomEmpresa',
			           },
			           {
			        	   header : 'Tipo Crédito',
			        	   width : 250,
			        	   dataIndex : 'tipoCredito',
			           },
			           {
			        	   width : 0,
			        	   dataIndex : 'idFinanciamiento',
			        	   hidden : true,
			        	   hideable : false
			           },
			           {
			        	   header : '',
			        	   width : 0,
			        	   dataIndex : 'noBanco',
			           },
			           {
			        	   header : 'Banco / Arrendadora',
			        	   width : 250,
			        	   dataIndex : 'descBanco',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   id : 'linea',
			        	   header : 'Línea Autorizada',
			        	   width : 130,
			        	   align :'right',
			        	   dataIndex : 'linea',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : 'Crédito dispuesto',
			        	   width : 130,
			        	   dataIndex : 'pasivo',
			        	   align :'right',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : 'Tasa',
			        	   width : 100,
			        	   dataIndex : 'tasa',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : 'Fec. Vencimiento',
			        	   width : 130,
			        	   align :'right',
			        	   dataIndex : 'fecVencimiento',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : '',
			        	   width : 0,
			        	   dataIndex : 'carta',
			        	   hidden : true,
			        	   hideable : false
			           },
			           {
			        	   header : 'Factoraje',
			        	   width : 130,
			        	   align :'right',
			        	   dataIndex : 'factoraje',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : 'Anticipagos',
			        	   width : 130,
			        	   align :'right',
			        	   dataIndex : 'anticipagos',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           }, {
			        	   header : '',
			        	   width : 0,
			        	   dataIndex : 'namig',
			        	   hidden : true,
			        	   hideable : false
			           },
			           {
			        	   header : 'Tot. Línea Dispuesta',
			        	   width : 130,
			        	   align :'right',
			        	   dataIndex : 'totalLinea',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : 'Tot. Línea Disponible',
			        	   width : 130,
			        	   align :'right',
			        	   dataIndex : 'totalLineaDisp',renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : '',
			        	   width : 0,
			        	   dataIndex : 'garantiasAvales',
			        	   hidden : true,
			        	   hideable : false
			           },
			           ]
		}),
	});
	NS.limpiar=function(){
		NS.txtGpoEmpresas.setValue("");
		NS.txtEmpresas.setValue(0);
		NS.txtTipoFin.setValue(0);
		NS.cmbEmpresas.setValue('****TODOS****');
		NS.cmbGpoEmpresa.reset();
		NS.cmbFinanciamiento.setValue('****TODOS****');
		NS.storeDatos.removeAll()
		NS.txtFechaIni.reset();
		NS.txtFechaFin.reset();
	}
	NS.obtenerDatosReporte=function(excel){
		if(NS.storeDatos.getCount()<=0){
			Ext.Msg.alert("SET","No hay datos para exportar.");
			return;
		}
		else{	
			var matriz = new Array();
			for (var i = 0; i < NS.storeDatos.data.length; i++) {
				var registro=NS.storeDatos.getAt(i);
				var registroGrid= {};
				registroGrid.empresa = NS.GI_NOM_EMPRESA;
				registroGrid.nomEmpresa = registro.data.nomEmpresa;
				registroGrid.tipoCredito = registro.data.tipoCredito;
				registroGrid.descBanco = registro.data.descBanco;
				registroGrid.linea = registro.data.linea;
				registroGrid.pasivo = registro.data.pasivo;
				registroGrid.tasa = registro.data.tasa;
				registroGrid.fecVencimiento = registro.data.fecVencimiento;
				registroGrid.factoraje = registro.data.factoraje;
				registroGrid.anticipagos = registro.data.anticipagos;
				registroGrid.totalLinea = registro.data.totalLinea;
				registroGrid.totalLineaDisp = registro.data.totalLineaDisp;
				registroGrid.color = registro.data.color;
				matriz[i] = registroGrid;
			}
			var jsonString= Ext.util.JSON.encode(matriz);
			if(excel){
				Ext.getCmp('hdJasonExcel').setValue(jsonString);
				var forma = NS.reporteAnalisisLineasAux.getForm();
				forma.url='/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp?nomReporte=excelAnalisisLineas';
			}
			else{
				Ext.getCmp('hdJasonImprimir').setValue(jsonString);
				var forma = NS.reporteAnalisisLineasC.getForm();
				forma.url = '/SET/jsp/Reportes.jsp?nomReporte=ReporteAnalisisLineas';
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
	//WinResumen
	NS.obtenerDatosResumen=function(excel){
		if(NS.storeResumen.getCount()<=0){
			Ext.Msg.alert("SET","No hay datos para exportar.");
			return;
		}
		else{	
			var matriz = new Array();
			for (var i = 0; i < NS.storeResumen.data.length; i++) {
				var registro=NS.storeResumen.getAt(i);
				var registroResumen= {};
				registroResumen.empresa = NS.GI_NOM_EMPRESA;
				registroResumen.descripcion = registro.data.descripcion;
				registroResumen.lineasAut = registro.data.lineasAut;
				registroResumen.dispuestas = registro.data.dispuestas;
				registroResumen.disponibles = registro.data.disponibles;
				registroResumen.color = registro.data.color;
				matriz[i] = registroResumen;
			}
			var jsonString= Ext.util.JSON.encode(matriz);
			if(excel){
				Ext.getCmp('hdJasonRExcel').setValue(jsonString);
				var forma = NS.formResumen.getForm();
				forma.url='/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp?nomReporte=excelResumenAnalisisLineas';
			}
			else{
				Ext.getCmp('hdJasonRImprimir').setValue(jsonString);
				var forma = NS.formResumen2.getForm();
				forma.url = '/SET/jsp/Reportes.jsp?nomReporte=ReporteAnalisisLineasResumen';
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
	NS.formResumen = new Ext.form.FormPanel({
		title : '',
		autowidth : true,
		height : 550,
		hidden:true,
		layout : 'absolute',
		id : PF + 'formResumen',
		name : PF + 'formResumen',
		autoScroll : true,
		items : [
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonRExcel',
		        	 id:'hdJasonRExcel',
		        	 value: ''
		         },
		         ],
	});
	NS.formResumen2 = new Ext.form.FormPanel({
		title : '',
		autowidth : true,
		height : 550,
		hidden:true,
		layout : 'absolute',
		id : PF + 'formResumen2',
		name : PF + 'formResumen2',
		autoScroll : true,
		items : [
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonRImprimir',
		        	 id:'hdJasonRImprimir',
		        	 value: ''
		         },
		         ],
	});
	NS.formReporte = new Ext.form.FormPanel({
		title : '',
		autowidth : true,
		height : 0,
		hidden:true,
		layout : 'absolute',
		id : PF + 'formReporte',
		name : PF + 'formReporte',
		autoScroll : true,
		items : [
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonExcel',
		        	 id:'hdJasonExcel',
		        	 value: ''
		         },
		         ],
	});
	NS.formReporte2 = new Ext.form.FormPanel({
		title : '',
		autowidth : true,
		height : 0,
		hidden:true,
		layout : 'absolute',
		id : PF + 'formReporte2',
		name : PF + 'formReporte2',
		autoScroll : true,
		items : [
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonImprimir',
		        	 id:'hdJasonImprimir',
		        	 value: ''
		         },
		         ],

	});
	NS.storeResumen = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			empresa:0,
			tipoFinanciamiento:0,
			vsMenu:''
		},
		paramOrder : ['empresa','tipoFinanciamiento','vsMenu'],
		directFn : ReporteAnalisisLineasCAction.obtenerResumen,
		fields : [ {
			name : 'idGrupo'
		}, {
			name : 'descripcion'
		},  {
			name : 'lineasAut'
		}, {
			name : 'dispuestas'
		}, {
			name : 'disponibles'
		}, {
			name : 'color'
		}, 
		],
		listeners : {
			load : function(s, records) {
				if(records.length<=0){
					Ext.Msg.alert('SET','No existen datos.');
					return;
				}
				else{
					NS.winResumen.show();
				}
			}
		}
	});
	NS.gridDatosResumen = new Ext.grid.GridPanel({
		store : NS.storeResumen,
		id : PF + 'gridDatosResumen',
		name : PF + 'gridDatosResumen',
		frame : true,
		//autoScroll : true,
		autowidth : true,
		height : 510,
		x : 0,
		y : 30,
		cm : new Ext.grid.ColumnModel({
			defaults : {
				width : 120,
				value : true,
				sortable : true
			},
			columns : [
			           {
			        	   id : 'idGrupo',
			        	   header : 'ID Grupo',
			        	   width : 150,
			        	   dataIndex : 'idGrupo',
			        	   direction : 'ASC',
			        	   hidden : true,
			        	   hideable : false
			           },
			           {
			        	   id : 'descripcion',
			        	   header : 'Descripción',
			        	   width : 250,
			        	   dataIndex : 'descripcion',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   id : 'lineasAut',
			        	   header : 'Líneas Autorizadas',
			        	   width : 150,
			        	   align :'right',
			        	   dataIndex : 'lineasAut',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },{
			        	   id : 'dispuestas',
			        	   header : 'Dispuestas',
			        	   width : 150,
			        	   align :'right',
			        	   dataIndex : 'dispuestas',
			        	   direction : 'ASC',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   id : 'disponibles',
			        	   header : 'Disponibles',
			        	   width : 150,
			        	   dataIndex : 'disponibles',
			        	   align :'right',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           ]
		})
	});
	NS.lblUsuario = new Ext.form.Label({
		text : 'Usuario:',
		x : 20,
		y : 5
	});
	NS.txtUsuario = new Ext.form.TextField({
		id : PF + 'txtUsuario',
		name : PF + 'txtUsuario',
		value : NS.USR,
		x : 70,
		y : 5,
		readOnly : true,
		width : 300
	});
	NS.lblEmpresaR = new Ext.form.Label({
		text : 'Empresa:',
		x : 410,
		y : 5
	});
	NS.txtEmpresaR = new Ext.form.TextField({
		id : PF + 'txtEmpresaR',
		name : PF + 'txtEmpresaR',
		value : NS.cmbEmpresas.getRawValue(),
		x : 460,
		y : 5,
		readOnly : true,
		width : 300
	});
	NS.winResumen = new Ext.Window(
			{
				title : 'Pasivos Financieros (Créditos bancarios)',
				modal : true,
				shadow : true,
				closeAction : 'hide',
				width : 800,
				height : 600,
				layout : 'absolute',
				plain : true,
				bodyStyle : 'padding:10px;',
				buttonAlign : 'center',
				draggable : true,
				resizable : true,
				buttons:[ {
					text: 'Exportar Excel',
					handler: function(){
						NS.obtenerDatosResumen(true);
					}
				},
				{
					text: 'Imprimir',
					handler: function(){
						NS.obtenerDatosResumen(false);
					}
				},
				{
					text: 'Regresar',
					handler: function(){
						NS.winResumen.hide();
					}
				}
				],
				items : [NS.formResumen,NS.formResumen2,NS.lblUsuario,NS.txtUsuario,NS.lblEmpresaR,NS.txtEmpresaR,NS.gridDatosResumen],
			});
	NS.reporteAnalisisLineasAux= new Ext.form.FormPanel({
		hidden:true,
		layout: 'absolute',
		id: PF + 'reporteAnalisisLineasAux',
		name: PF + 'reporteAnalisisLineasAux',
		renderTo: NS.tabContId,
		frame: true,
		autoScroll: true,
		items : [{
			xtype: 'textfield',
			x:0,
			y:0,
			hidden:true,
			name:'hdJasonExcel',
			id:'hdJasonExcel',
			value: ''
		},]});         
	NS.reporteAnalisisLineasC= new Ext.form.FormPanel({
		title: 'Universo de Líneas de Crédito',
		autowidth: true,
		height: 900,
		padding: 10,
		layout: 'absolute',
		id: PF + 'reporteAnalisisLineasC',
		name: PF + 'reporteAnalisisLineasC',
		renderTo: NS.tabContId,
		frame: true,
		autoScroll: true,
		items : [
		         {
		        	 xtype: 'fieldset',
		        	 title: '',
		        	 autowidth: true,
		        	 height: 110,
		        	 x: 0,
		        	 y: 0,
		        	 layout: 'absolute',
		        	 id: 'fSetBusqueda',
		        	 items: [
		        	         NS.lblGpoEmpresas,
		        	         NS.txtGpoEmpresas,
		        	         NS.cmbGpoEmpresa,
		        	         NS.lblEmpresas,
		        	         NS.txtEmpresas,
		        	         NS.cmbEmpresas,
		        	         NS.lblFechaIni,NS.txtFechaIni,
		        	         NS.lblFechaFin,NS.txtFechaFin,
		        	         NS.lblFinanciamiento,
		        	         NS.txtTipoFin,
		        	         NS.cmbFinanciamiento,
		        	         NS.btnBuscar
		        	         ]
		         },{
		        	 xtype: 'fieldset',
		        	 title: '',
		        	 autowidth: true,
		        	 height: 400,
		        	 x: 0,
		        	 y: 120,
		        	 frame:true,
		        	 layout: 'absolute',
		        	 id: 'fSetGrid',
		        	 items: [NS.gridDatos],
		        	 buttons:[
		        	          {
		        	        	  text: 'Resumen',
		        	        	  handler: function(){
		        	        		  if(NS.storeDatos.getCount()>0){
		        	        			  var  vsMenu = vsTipoMenu;
		        	        			  if(NS.proyecto=='CIE')
		        	        				  if(NS.cmbGpoEmpresas.getValue()==1&&(vsTipoMenu==""||vsTipoMenu=="B"))
		        	        					  vsMenu = "', 'B";
		        	        			  var myMask = new Ext.LoadMask(Ext.getBody(), {
		        	        				  store : NS.storeResumen,
		        	        				  msg : "Cargando..."
		        	        			  });
		        	        			  NS.storeResumen.baseParams.empresa=parseInt(NS.txtEmpresas.getValue());
		        	        			  NS.storeResumen.baseParams.tipoFinanciamiento=parseInt(NS.txtTipoFin.getValue());
		        	        			  NS.storeResumen.baseParams.vsMenu=vsMenu;
		        	        			  NS.storeResumen.load();}
		        	        		  else{
		        	        			  Ext.Msg.alert("SET","No existe información para realizar un resumen.");
		        	        		  }
		        	        	  }
		        	          },{
		        	        	  text: 'Exportar Excel',

		        	        	  handler: function(){
		        	        		  NS.obtenerDatosReporte(true);
		        	        	  }
		        	          },
		        	          {
		        	        	  text: 'Imprimir',
		        	        	  handler: function(){
		        	        		  NS.obtenerDatosReporte(false);
		        	        	  }
		        	          },
		        	          {
		        	        	  text: 'Limpiar',
		        	        	  handler: function(){
		        	        		  NS.limpiar();
		        	        	  }
		        	          }
		        	          ],
		         },
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonImprimir',
		        	 id:'hdJasonImprimir',
		        	 value: ''
		         },

		         ]
	});
	NS.storeConfiguraSetTodos.load({
		callback : function(records) {
			NS.proyecto = records[0].get('valorConfiguraSet');
			if (NS.proyecto == 'CIE') {
				NS.reporteAnalisisLineasC.setTitle("Análisis de Líneas de Crédito");
				NS.lblFinanciamiento.setVisible(false);
				NS.txtTipoFin.setVisible(false);
				NS.cmbFinanciamiento.setVisible(false);
				if (vsTipoMenu == ""|| vsTipoMenu =="B"){
					NS.lblGpoEmpresas.setVisible(true);
					NS.cmbGpoEmpresa.setVisible(true);
					NS.txtGpoEmpresas.setVisible(true);
					NS.cmbEmpresas.setPosition(600,15);
					NS.txtEmpresas.setPosition(550,15);
					NS.lblEmpresas.setPosition(550,0);
					NS.storeCmbGpoEmpresa.load();
				}	
			}else{
				NS.cmbEmpresas.setPosition(150,15);
				NS.txtEmpresas.setPosition(100,15);
				NS.lblEmpresas.setPosition(100,0);
				NS.storeCmbEmpresa.baseParams.grupo=false;
				NS.storeCmbEmpresa.baseParams.piNoUsuario=parseInt(NS.idUsuario);
				NS.storeCmbEmpresa.load();
			}
		}
	});
	NS.reporteAnalisisLineasC.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});