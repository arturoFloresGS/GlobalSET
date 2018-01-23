Ext
		.onReady(function() {
			Ext.QuickTips.init();
			Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
			var NS = Ext.namespace('apps.SET.BarridosFondeos.MtoEmpresas');
			NS.tabContId = apps.SET.tabContainerId;
			var PF = apps.SET.tabID + '.';
			NS.GI_USUARIO = apps.SET.iUserId;
			NS.GI_ID_CAJA = apps.SET.ID_CAJA;
			NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
			NS.idUsuario = apps.SET.iUserId;
			var mascara = new Ext.LoadMask(Ext.getBody(), {
				msg : "Cargando..."
			});
			// store empresa
			NS.storeCmbEmpresa = new Ext.data.DirectStore({
				paramsAsHash : false,
				root : '',
				baseParams : {
					idUsuario : NS.idUsuario,
					mantenimiento : true
				},
				paramOrder : [ 'idUsuario', 'mantenimiento' ],
				directFn : BarridosFondeosAction.obtenerEmpresasConcent,
				idProperty : 'noEmpresa',
				fields : [ {
					name : 'noEmpresa'
				}, {
					name : 'nomEmpresa'
				} ],
				listeners : {
					load : function(s, records) {
					}
				}
			});

			// combo Empresas coinversoras
			NS.storeCmbEmpresa.load();
			NS.cmbEmpresa = new Ext.form.ComboBox(
					{
						store : NS.storeCmbEmpresa,
						name : PF + 'cmbEmpresa',
						id : PF + 'cmbEmpresa',
						typeAhead : true,
						mode : 'local',
						minChars : 0,
						selecOnFocus : true,
						forceSelection : true,
						x : 150,
						y : 0,
						width : 320,
						valueField : 'noEmpresa',
						displayField : 'nomEmpresa',
						autocomplete : true,
						emptyText : 'Seleccione una empresa',
						triggerAction : 'all',
						value : '',
						visible : false,
						listeners : {
							select : {
								fn : function(combo, valor) {
									BFwrk.Util.updateComboToTextField(PF
											+ 'txtEmpresa', NS.cmbEmpresa
											.getId());
									NS.storeEmpresas.baseParams.empConcentradora = combo
											.getValue();
								}
							}
						}
					});

			NS.smE = new Ext.grid.CheckboxSelectionModel({
				forceFit : false,
				singleSelect : true
			});

			// store del grid empresas
			NS.storeEmpresas = new Ext.data.DirectStore({
				paramsAsHash : false,
				root : '',
				baseParams : {
					noEmpresa : 0,
					idUsuario : NS.idUsuario
				},
				paramOrder : [ 'noEmpresa', 'idUsuario' ],

				directFn : BarridosFondeosAction.obtenerFiliales,
				idProperty : 'noEmpresa',
				fields : [ {
					name : 'noEmpresa'
				}, {
					name : 'nombreEmpresa'
				}, {
					name : 'noCuentaEmp'
				}, {
					name : 'tipoPersona'
				}, {
					name : 'noCuenta'
				}, {
					name : 'noControladora'
				} ],
				listeners : {
					load : function(s, records) {
						mascara.hide();
					}
				}
			});

			// grid empresas coinversionistas
			NS.gridEmpresas = new Ext.grid.GridPanel(
					{
						store : NS.storeEmpresas,
						id : PF + 'gridEmpresas',
						name : PF + 'gridEmpresas',
						title : 'Empresas Filiales',
						frame : true,
						width : 470,
						height : 340,
						x : 10,
						y : 50,
						cm : new Ext.grid.ColumnModel({
							defaults : {
								width : 120,
								value : true,
								sortable : true
							},

							columns : [ NS.smE, {
								id : 'noEmpresa',
								header : 'No. Empresa',
								width : 90,
								dataIndex : 'noEmpresa',
								direction : 'ASC',

							}, {
								header : 'Descripción',
								width : 375,
								dataIndex : 'nombreEmpresa'
							}, {
								header : 'noCuentaEmp',
								width : 0,
								dataIndex : 'noCuentaEmp',
								hidden : true
							}, {
								header : 'idTipoPersona',
								width : 0,
								dataIndex : 'tipoPersona',
								hidden : true
							}, {
								header : 'noCuenta',
								width : 0,
								dataIndex : 'noCuenta',
								hidden : true
							}, {
								header : 'noControladora',
								width : 0,
								dataIndex : 'noControladora',
								hidden : true
							} ]
						}),

						viewConfig : {
							getRowClass : function(record, index) {
								if (record.get('noControladora') == null
										|| record.get('noControladora') == 0) {
									return 'row-background-color-blue';
								}
							}
						},
						sm : NS.smE,
						listeners : {
							click : {
								fn : function(grid) {
									NS.gridDivisas
											.setTitle('Divisas Encontradas');
									var regSelec = NS.gridEmpresas
											.getSelectionModel()
											.getSelections();
									if (regSelec.length > 0) {
										mascara.show()
										NS.storeDivisas.baseParams.empCoinversionista = regSelec[0]
												.get('noEmpresa');
										NS.storeDivisas.baseParams.elemento = 'grid';
										NS.storeDivisas.load();
										Ext.getCmp(PF + 'btnEliminar')
												.setDisabled(false);
									}
								}
							}
						}
					});

			NS.sm = new Ext.grid.CheckboxSelectionModel({});

			// store del grid divisas
			NS.storeDivisas = new Ext.data.DirectStore({
				paramsAsHash : false,
				root : '',
				baseParams : {
					empCoinversionista : 0,
					elemento : ''
				},
				paramOrder : [ 'empCoinversionista', 'elemento' ],
				directFn : CoinversionAction.llenarGridDivisas,
				idProperty : 'idDivisa',
				fields : [ {
					name : 'idDivisa'
				}, {
					name : 'descDivisa'
				} ],
				listeners : {
					load : function(s, records) {
						mascara.hide();
						cantidad = records.length;
					}
				}
			});

			// grid divisas
			NS.gridDivisas = new Ext.grid.GridPanel({
				store : NS.storeDivisas,
				id : PF + 'gridDivisas',
				name : PF + 'gridDivisas',
				title : 'Divisas Encontradas',
				frame : true,
				width : 350,
				height : 340,
				x : 520,
				y : 50,
				cm : new Ext.grid.ColumnModel({
					defaults : {
						width : 120,
						value : true,
						sortable : true
					},
					columns : [ NS.sm, {
						id : 'idDivisa',
						header : 'Divisa',
						width : 80,
						dataIndex : 'idDivisa'
					}, {
						header : 'Descripción',
						width : 260,
						dataIndex : 'descDivisa'
					} ]
				}),
				sm : NS.sm,
				listeners : {
					dblclick : {
						fn : function(grid) {

						}
					}
				}
			});

			// funciones

			NS.verificaCampos = function(mensaje) {
				var divisas = NS.gridDivisas.getSelectionModel()
						.getSelections();
				if (Ext.getCmp(PF + 'txtEmpresa').getValue() == '') {
					BFwrk.Util.msgShow(
							'Debe seleccionar una empresa concentradora',
							'WARNING');
					return false;
				}

				if (divisas.length <= 0) {
					BFwrk.Util.msgShow(
							'Debe marcar al menos un registro de Divisas para '
									+ mensaje, 'WARNING');
					return false;
				}
				return true;
			}

			NS.enviarParams = function(operacion) {
				BFwrk.Util.msgWait('Ejecutando solictud...', true);
				var registrosDiv = NS.gridDivisas.getSelectionModel()
						.getSelections();
				var registrosEmp = NS.gridEmpresas.getSelectionModel()
						.getSelections();
				var matrizDivisas = new Array();
				var matrizEmpresas = new Array();
				for (var i = 0; i < registrosDiv.length; i++) {
					var regDivisa = {};
					regDivisa.idDivisa = registrosDiv[i].get('idDivisa');
					regDivisa.descDivisa = registrosDiv[i].get('descDivisa');

					matrizDivisas[i] = regDivisa;
				}

				for (var j = 0; j < registrosEmp.length; j++) {
					var regEmpresa = {};
					regEmpresa.noEmpresa = registrosEmp[j].get('noEmpresa');
					regEmpresa.nomEmpresa = registrosEmp[j]
							.get('nombreEmpresa');
					regEmpresa.noCuentaEmp = registrosEmp[j].get('noCuentaEmp');
					regEmpresa.idTipoPersona = registrosEmp[j]
							.get('idTipoPersona');
					regEmpresa.noCuenta = registrosEmp[j].get('noCuenta');
					regEmpresa.noControladora = registrosEmp[j]
							.get('noControladora');
					matrizEmpresas[j] = regEmpresa;
				}
				NS.concentradora = parseInt(Ext.getCmp(PF + 'txtEmpresa')
						.getValue());

				var jsonStringD = Ext.util.JSON.encode(matrizDivisas);
				var jsonStringE = Ext.util.JSON.encode(matrizEmpresas);
				var terminado = false;

				CoinversionAction
						.operacionesFilial(
								jsonStringD,
								jsonStringE,
								NS.concentradora,
								operacion,
								function(mapResult, e) {

									BFwrk.Util.msgWait('Terminado...', false);

									if (mapResult.msgUsuario !== null
											&& mapResult.msgUsuario !== ''
											&& mapResult.msgUsuario != undefined) {
										for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
											// BFwrk.Util.msgShow(''+mapResult.msgUsuario[msg],
											// 'INFO');
											Ext.Msg
													.alert(
															'Información SET',
															''
																	+ mapResult.msgUsuario[msg]);
										}
										NS.buscar();
									}
								});
			}

			NS.buscar = function() {
				NS.storeDivisas.removeAll();
				NS.gridDivisas.getView().refresh();
				if (Ext.getCmp(PF + 'txtEmpresa').getValue() == '') {
					BFwrk.Util.msgShow(
							'Debe seleccionar una empresa concentradora',
							'WARNING');
					return;
				}
				Ext.getCmp(PF + 'btnNuevo').setDisabled(false);
				Ext.getCmp(PF + 'btnEliminar').setDisabled(false);
				NS.storeEmpresas.baseParams.noEmpresa = Ext.getCmp(
						PF + 'txtEmpresa').getValue();
				try {
					mascara.show();
					NS.storeEmpresas.load();
					NS.storeEmpresas.setDefaultSort("noEmpresa");
				} catch (err) {
					alert(err.message);
				}
				NS.gridEmpresas.getView().refresh();
			}

			NS.contenedorCoinversionistas = new Ext.FormPanel(
					{
						title : 'Mantenimiento de Empresas para Barridos y Fondeos',
						width : 990,
						height : 502,
						padding : 10,
						layout : 'absolute',
						frame : true,
						autoScroll : true,
						renderTo : NS.tabContId,
						items : [ {
							xtype : 'fieldset',
							title : '',
							x : 9,
							y : 10,
							width : 930,
							height : 455,
							layout : 'absolute',
							items : [
									{
										xtype : 'label',
										text : 'Empresa:',
										x : 20,
										y : 5
									},
									NS.cmbEmpresa,
									{
										xtype : 'textfield',
										id : PF + 'txtEmpresa',
										name : PF + 'txtEmpresa',
										x : 80,
										y : 0,
										width : 50,
										listeners : {
											change : {
												fn : function(caja, valor) {
													var comboValue = BFwrk.Util
															.updateTextFieldToCombo(
																	PF
																			+ 'txtEmpresa',
																	NS.cmbEmpresa
																			.getId());
													NS.storeEmpresas.baseParams.empConcentradora = comboValue;
													NS.storeEmpresas.baseParams.divisa = '';
												}
											}
										}
									},
									{
										xtype : 'button',
										text : 'Buscar',
										id : PF + 'btnBuscar',
										name : PF + 'btnBuscar',
										x : 480,
										y : 0,
										width : 70,
										listeners : {
											click : {
												fn : function(e) {
													NS.buscar();
												}
											}
										}
									},
									NS.gridEmpresas,
									NS.gridDivisas,

									{
										xtype : 'button',
										text : 'Imprimir',
										id : PF + 'btnImprimir',
										name : PF + 'btnImprimir',
										x : 500,
										y : 410,
										width : 80,
										height : 22,
										listeners : {
											click : {
												fn : function(e) {
													if (Ext.getCmp(
															PF + 'txtEmpresa')
															.getValue() == '') {
														BFwrk.Util
																.msgShow(
																		'Debe seleccionar una empresa concentradora',
																		'WARNING');
														return;
													}
													// MsgBox para elegir tipo
													// de reporte
													Ext.Msg
															.show({
																title : 'Reporte de empresas filiales',
																msg : 'Elija el tipo de reporte a imprimir',
																width : 300,
																closable : false,
																buttons : {
																	yes : "Simple",
																	handler : function() {
																		Ext.MessageBox
																				.hide();
																	},
																	no : "Árbol de empresas",
																	handler : function() {
																		Ext.MessageBox
																				.hide();

																	},
																	cancel : "Cancelar",
																	handler : function() {
																		Ext.MessageBox
																				.hide();

																	}
																},
																multiline : false,
																fn : function(
																		buttonValue,
																		inputText,
																		showConfig) {
																	if (buttonValue == 'yes') {
																		// Reporte
																		// Lineal
																		var strParams = '';
																		strParams = '?nomReporte=ReporteFiliales';
																		strParams += '&'
																				+ 'noEmpresa='
																				+ Ext
																						.getCmp(
																								PF
																										+ 'txtEmpresa')
																						.getValue();
																		strParams += '&'
																				+ 'idUsuario='
																				+ NS.idUsuario;
																		window
																				.open("/SET/jsp/Reportes.jsp"
																						+ strParams);
																	}
																	if (buttonValue == 'no') {
																		// Reporte
																		// árbol
																		// de
																		// empresas
																		CoinversionAction
																				.obtenerReporteCoinversion(
																						parseInt(Ext
																								.getCmp(
																										PF
																												+ 'txtEmpresa')
																								.getValue()),
																						NS.GI_USUARIO,
																						function(
																								mapResult,
																								e) {
																							if (mapResult !== null
																									&& mapResult !== ''
																									&& mapResult != undefined) {
																								if (mapResult <= 0) {
																									BFwrk.Util
																											.msgShow(
																													'No hay datos para el reporte',
																													'INFO');
																									return;
																								} else {
																									strParams = '?nomReporte=ReporteMttoConiv';
																									strParams += '&'
																											+ 'nomParam1=usuario';
																									strParams += '&'
																											+ 'valParam1='
																											+ NS.GI_USUARIO;
																									strParams += '&'
																											+ 'nomParam2=empresa';
																									strParams += '&'
																											+ 'valParam2='
																											+ Ext
																													.getCmp(
																															PF
																																	+ 'txtEmpresa')
																													.getValue();
																									window
																											.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"
																													+ strParams);
																								}
																							}

																						});
																	}
																	if (buttonValue == 'cancel') {
																		Ext.Msg
																				.hide();
																	}
																},
																icon : Ext.Msg.QUESTION
															});
												}
											}
										}
									},
									{
										xtype : 'button',
										text : 'Crear Nuevo',
										id : PF + 'btnNuevo',
										name : PF + 'btnNuevo',
										disabled : true,
										x : 590,
										y : 410,
										width : 80,
										listeners : {
											click : {
												fn : function(e) {
													var empSel = NS.gridEmpresas
															.getSelectionModel()
															.getSelections();
													if (empSel.length <= 0) {
														BFwrk.Util
																.msgShow(
																		'Debe seleccionar una empresa coinversionista',
																		'WARNING');
														return;
													}
													NS.storeDivisas.removeAll();
													NS.gridDivisas.getView()
															.refresh();
													NS.gridDivisas
															.setTitle('Divisas por Agregar');
													var regSelec = NS.gridEmpresas
															.getSelectionModel()
															.getSelections();
													if (regSelec.length > 0) {
														NS.storeDivisas.baseParams.empCoinversionista = regSelec[0]
																.get('noEmpresa');
														NS.storeDivisas.baseParams.elemento = 'boton';
														NS.storeDivisas.load();
														Ext
																.getCmp(
																		PF
																				+ 'btnEliminar')
																.setDisabled(
																		true);
														Ext
																.getCmp(
																		PF
																				+ 'btnEjecutar')
																.setDisabled(
																		false);

													}
												}
											}
										}
									},
									{
										xtype : 'button',
										text : 'Ejecutar',
										id : PF + 'btnEjecutar',
										name : PF + 'btnEjecutar',
										disabled : true,
										x : 680,
										y : 410,
										width : 80,
										listeners : {
											click : {
												fn : function(e) {
													var recordsDivisas = NS.storeDivisas.data.items;
													if (recordsDivisas.length <= 0) {
														BFwrk.Util
																.msgShow(
																		'No existen divisas adicionales que almacenar en la empresa',
																		'WARNING');
														return;
													}

													if (NS
															.verificaCampos('dar de alta') == true) {

														NS.enviarParams('alta')

													}

												}
											}
										}
									},
									{
										xtype : 'button',
										text : 'Eliminar',
										id : PF + 'btnEliminar',
										name : PF + 'btnEliminar',
										disabled : true,
										x : 770,
										y : 410,
										width : 80,
										listeners : {
											click : {
												fn : function(e) {
													if (NS
															.verificaCampos('eliminar') == true) {
														NS.enviarParams('baja')
													}
												}
											}
										}
									} ]
						} ]
					});
			NS.contenedorCoinversionistas.setSize(Ext.get(NS.tabContId)
					.getWidth(), Ext.get(NS.tabContId).getHeight());
		});