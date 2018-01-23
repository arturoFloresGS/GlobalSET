/*
@author: Geraldine Xareni González Mora
@since: 01/02/2017
 */
Ext
		.onReady(function() {

			Ext.QuickTips.init();
			Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
			var NS = Ext.namespace('apps.SET.Conciliacion.ConsultaIngresos');
			NS.tabContId = apps.SET.tabContainerId;
			var PF = apps.SET.tabID + '.';
			NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
			NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
			NS.NOMBRE_HOST = apps.SET.HOST_NAME_LOCAL;
			NS.idUsuario = apps.SET.iUserId;
			NS.iIdBanco = 0;
			NS.sDivisa = '';
			NS.sDivision = '';
			NS.sFecIni = '';
			NS.sFecFin = '';
			NS.sEstatus = '';
			NS.uMontoIni = 0;
			NS.uMontoFin = 0;
			NS.iFormaPago = 0;
			NS.cTodas = 0;
			NS.iNoCliente = '';
			NS.iNoEmpresa = '';
			NS.parametroBus = '';
			NS.iNoProveedor = '';

			var jsonStringRegistros = '';
			var jsonStringCriterios = '';
			var textField = new Ext.form.TextField();

			// Formato de un número a monetario
			NS.formatNumber = function(num, prefix) {
				num = num.toString();
				if (num.indexOf('.') > -1) {
					if (num.substring(num.indexOf('.')).length < 3) {
						num = num + '0';
					}
				} else {
					num = num + '.00';
				}
				prefix = prefix || '';
				var splitStr = num.split('.');
				var splitLeft = splitStr[0];
				var splitRight = splitStr.length > 1 ? '.' + splitStr[1] : '';
				var regx = /(\d+)(\d{3})/;
				while (regx.test(splitLeft)) {
					splitLeft = splitLeft.replace(regx, '$1' + ',' + '$2');
				}
				return prefix + splitLeft + splitRight;
			};
			// Quitar formato a las cantidades
			NS.unformatNumber = function(num) {
				return num.replace(/(,)/g, '');
			};
			// **********************************************BÚSQUEDA************************************************
			// Empresa
			NS.lblEmpresa = new Ext.form.Label({
				text : 'Empresa:',
				x : 60,
				y : 0
			});
			NS.txtEmpresa = new Ext.form.TextField({
				id : PF + 'txtEmpresa',
				name : PF + 'txtEmpresa',
				value : NS.GI_ID_EMPRESA,
				x : 115,
				y : 0,
				width : 50,
				listeners : {
					change : {
						fn : function(caja, valor) {
							var comboValue = BFwrk.Util.updateTextFieldToCombo(
									PF + 'txtEmpresa', NS.cmbEmpresa.getId());
							NS.accionarCmbEmpresa(comboValue);
						}
					}
				}
			});
			NS.storeCmbEmpresa = new Ext.data.DirectStore({
				paramsAsHash : false,
				root : '',
				baseParams : {},
				paramOrder : [],
				directFn : GlobalAction.llenarComboEmpresasUsuario,
				idProperty : 'id',
				fields : [ {
					name : 'id'
				}, {
					name : 'descripcion'
				} ],
				listeners : {
					load : function(s, records) {
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
				x : 166,
				y : 0,
				width : 280,
				valueField : 'id',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione una empresa',
				triggerAction : 'all',
				value : NS.GI_NOM_EMPRESA,
				listeners : {
					select : {
						fn : function(combo, valor) {
							BFwrk.Util.updateComboToTextField(
									PF + 'txtEmpresa', NS.cmbEmpresa.getId());
							NS.accionarCmbEmpresa(combo.getValue());
						}
					}
				}
			});
			// CheckBox
			NS.lblTodas = new Ext.form.Label({
				text : 'Todas:',
				x : 485,
				y : 4
			});
			NS.chkTodas = new Ext.form.Checkbox({
				name : PF + 'chkTodas',
				id : 'chkTodas',
				x : 520,
				y : 0,
				listeners : {
					check : {
						fn : function() {
							var me = this;
							var res = me.getValue();

							if (res == true) {
								NS.storeSolicitudes.baseParams.cTodas = 1;
								NS.cTodas = 1;
							} else {
								NS.storeSolicitudes.baseParams.cTodas = 0;
								NS.cTodas = 0;
							}
						}
					}
				}
			});

			// Botón Buscar
			NS.cmdBuscar = new Ext.Button({
				xtype : 'button',
				id : PF + 'cmdBuscar',
				name : PF + 'cmdBuscar',
				text : 'Buscar',
				x : 800,
				y : 165,
				width : 80,
				listeners : {
					click : {
						fn : function(e) {
							NS.verificaCampos2();
							NS.buscar();

						}
					}
				}
			});
			// ***************************************CRITERIOS DE
			// BÚSQUEDA*****************************
			// Etiqueta valor
			NS.lblValor = new Ext.form.Label({
				xtype : 'label',
				text : 'Valor:',
				x : 269,
				y : 78
			});
			// Banco
			// Store banco
			NS.storeCmbBanco = new Ext.data.DirectStore({
				paramsAsHash : false,
				root : '',
				baseParams : {
					empresa : NS.GI_ID_EMPRESA
				},
				paramOrder : [ 'empresa' ],
				directFn : ModificacionPorBloqueAction.obtenerBancos,
				idProperty : 'id',
				fields : [ {
					name : 'id'
				}, {
					name : 'descripcion'
				} ],
				listeners : {
					load : function(s, records) {
					}
				}
			});
			// Combo banco
			NS.cmbBanco = new Ext.form.ComboBox({
				store : NS.storeCmbBanco,
				name : PF + 'cmbBanco',
				id : PF + 'cmbBanco',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 269,
				y : 92,
				width : 178,
				valueField : 'id',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione un banco',
				triggerAction : 'all',
				hidden : true,
				listeners : {
					select : {
						fn : function(combo, valor) {
							NS.agregarCriterioValor('BANCO', combo.getId(),
									'valor');

						}
					}
				}
			});

			// Status
			NS.dataEstatus = [ [ '0', 'CAPTURADO' ], [ '1', 'FILIAL' ],
					[ '2', 'IMPORTADO' ] ];
			// Store criterio
			NS.storeEstatus = new Ext.data.SimpleStore({
				idProperty : 'idEstatus',
				fields : [ {
					name : 'idEstatus'
				}, {
					name : 'descripcion'
				} ]
			});
			NS.storeEstatus.loadData(NS.dataEstatus);

			// Combo
			NS.cmbEstatus = new Ext.form.ComboBox({
				store : NS.storeEstatus,
				name : PF + 'cmbEstatus',
				id : PF + 'cmbEstatus',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 269,
				y : 92,
				width : 178,
				valueField : 'idEstatus',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione un Estatus',
				triggerAction : 'all',
				value : '',
				hidden : true,
				listeners : {
					select : {
						fn : function(combo, valor) {
							NS.agregarCriterioValor('ESTATUS', combo.getId(),
									'valor');
						}
					}
				}

			});

			// Forma de pago
			// Store Forma de pago
			NS.storeFormaPago = new Ext.data.DirectStore({
				paramsAsHash : false,
				baseParams : {
					campoUno : 'id_forma_pago',
					campoDos : 'desc_forma_pago',
					tabla : 'cat_forma_pago',
					condicion : '',
					orden : '2'
				},
				root : '',
				paramOrder : [ 'campoUno', 'campoDos', 'tabla', 'condicion',
						'orden' ],
				directFn : GlobalAction.llenarComboGeneral,
				idProperty : 'id',
				fields : [ {
					name : 'id'
				}, {
					name : 'descripcion'
				} ],
				listeners : {
					load : function(s, records) {

					}
				}
			});
			// Combo Forma de pago
			NS.cmbFormaPago = new Ext.form.ComboBox({
				store : NS.storeFormaPago,
				name : PF + 'cmbFormaPago',
				id : PF + 'cmbFormaPago',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 269,
				y : 92,
				width : 178,
				valueField : 'id',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione una forma de pago',
				triggerAction : 'all',
				hidden : true,
				listeners : {
					select : {
						fn : function(combo, valor) {
							NS.agregarCriterioValor('FORMA DE PAGO', combo
									.getId(), 'valor');
						}
					}
				}
			});
			// Fecha
			// Calendario Fecha inicial
			NS.txtFechaIni = new Ext.form.DateField({
				id : PF + 'txtFechaIni',
				name : PF + 'txtFechaIni',
				format : 'd/m/Y',
				hidden : true,
				x : 269,
				y : 92,
				width : 100,
				listeners : {
					change : {
						fn : function(caja, valor) {
							NS.agregarCriterioValor('FECHA', caja.getId(),
									'valor');
						}
					}
				}
			});
			// Calendario Fecha Final
			NS.txtFechaFin = new Ext.form.DateField({
				id : PF + 'txtFechaFin',
				name : PF + 'txtFechaFin',
				format : 'd/m/Y',
				hidden : true,
				x : 372,
				y : 92,
				width : 100,
				listeners : {
					change : {
						fn : function(caja, valor) {
							var fechaIni = Ext.getCmp(PF + 'txtFechaIni')
									.getValue();
							if (fechaIni > caja.getValue()) {
								BFwrk.Util.msgShow(
										'La fecha inicial no puede ser mayor',
										'ERROR');
								Ext.getCmp(PF + 'txtFechaIni')
										.setDisabled(true);
								Ext.getCmp(PF + 'txtFechaFin')
										.setDisabled(true);
								return;
							}
							NS.agregarCriterioValor('FECHA', caja.getId(),
									'valor');
						}
					}
				}
			});
			// Monto
			// Caja de texto Monto Inicial
			NS.txtMontoIni = new Ext.form.TextField({
				id : PF + 'txtMontoIni',
				name : PF + 'txtMontoIni',
				hidden : true,
				x : 269,
				y : 92,
				width : 100,
				listeners : {
					change : {
						fn : function(caja, valor) {
							Ext.getCmp(PF + 'txtMontoIni').setValue(
									BFwrk.Util.formatNumber(caja.getValue()));
							NS.agregarCriterioValor('MONTOS', caja.getId(),
									'valor');
						}
					}
				}
			});
			// Caja de texto Monto final
			NS.txtMontoFin = new Ext.form.TextField(
					{
						id : PF + 'txtMontoFin',
						name : PF + 'txtMontoFin',
						hidden : true,
						x : 372,
						y : 92,
						width : 100,
						listeners : {
							change : {
								fn : function(caja, valor) {
									var valorUno = BFwrk.Util.formatNumber(Ext
											.getCmp(PF + 'txtMontoIni')
											.getValue());
									Ext.getCmp(PF + 'txtMontoFin').setValue(
											BFwrk.Util.formatNumber(caja
													.getValue()));
									if (parseInt(valorUno) > parseInt(valor)) {
										BFwrk.Util
												.msgShow(
														'El importe dos debe ser mayor que el importe uno',
														'ERROR');
										Ext.getCmp(PF + 'txtMontoIni')
												.setValue('');
										Ext.getCmp(PF + 'txtMontoIni')
												.setDisabled(true);
										Ext.getCmp(PF + 'txtMontoFin')
												.setDisabled(true);
										Ext.getCmp(PF + 'txtMontoFin')
												.setValue('');
										return;
									}
									NS.agregarCriterioValor('MONTOS', caja
											.getId(), 'valor');
								}
							}
						}
					});

			// Divisa
			// Store
			NS.storeCmbDivisa = new Ext.data.DirectStore({
				paramsAsHash : false,
				root : '',
				directFn : ModificacionPorBloqueAction.obtenerDivisas,
				idProperty : 'idDivisa',
				fields : [ {
					name : 'idDivisa'
				}, {
					name : 'descDivisa'
				}, {
					name : 'idDivisaSoin'
				}, {
					name : 'clasificacion'
				} ],
				listeners : {
					load : function(s, records) {
						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeCmbDivisa,
							msg : "Cargando..."
						});
						if (records.length == null || records.length <= 0) {
							Ext.Msg.alert('SET', 'No Existen divisas');
						}
					}
				}
			});
			NS.storeCmbDivisa.load();

			NS.cmbDivisa = new Ext.form.ComboBox({
				store : NS.storeCmbDivisa,
				name : PF + 'cmbDivisa',
				id : PF + 'cmbDivisa',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 269,
				y : 92,
				width : 178,
				hidden : true,
				tabIndex : 10,
				valueField : 'idDivisa',
				displayField : 'descDivisa',
				autocomplete : true,
				emptyText : 'Seleccione una divisa',
				triggerAction : 'all',
				visible : false,
				listeners : {
					select : {

						fn : function(combo, valor) {
							NS.agregarCriterioValor('DIVISA', combo.getId(),
									'valor');

						}
					}
				}
			});

			// Division
			// Store
			NS.storeCmbDivision = new Ext.data.DirectStore(
					{
						paramsAsHash : false,
						root : '',
						baseParams : {
							empresa : NS.GI_ID_EMPRESA
						},
						paramOrder : [ 'empresa' ],
						directFn : ModificacionPorBloqueAction.obtenerDivision,
						idProperty : 'campoUno',
						fields : [ {
							name : 'campoUno'
						}, {
							name : 'descripcion'
						} ],
						listeners : {
							load : function(s, records) {
								if (records.length == null
										|| records.length <= 0) {
									Ext.Msg
											.alert('SET',
													'No hay División disponible. Elija otro criterio.');

								}
							}
						}
					});
			// Combo
			NS.cmbDivision = new Ext.form.ComboBox({
				store : NS.storeCmbDivision,
				name : PF + 'cmbDivision',
				id : PF + 'cmbDivision',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 269,
				y : 92,
				width : 178,
				valueField : 'campoUno',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione división',
				triggerAction : 'all',
				hidden : true,
				listeners : {
					select : {
						fn : function(combo, valor) {
							NS.agregarCriterioValor('DIVISION', combo.getId(),
									'valor');
							// NS.storeCmbChequera.baseParams.banco = combo
							// .getValue();
							// NS.storeCmbChequera.baseParams.opcion = NS
							// .opcionSel();
						}
					}
				}
			});

			// Criterios de búsqueda
			NS.dataCriterio = [ [ '0', 'BANCO RECEPTOR' ], [ '1', 'DIVISA' ],
					[ '2', 'DIVISION' ], [ '3', 'ESTATUS' ], [ '4', 'FECHAS' ],
					[ '5', 'FORMA DE PAGO' ], [ '6', 'MONTOS' ] ];
			// Store criterio
			NS.storeCriterio = new Ext.data.SimpleStore({
				idProperty : 'idCriterio',
				fields : [ {
					name : 'idCriterio'
				}, {
					name : 'descripcion'
				} ]
			});
			NS.storeCriterio.loadData(NS.dataCriterio);
			// Etiqueta
			NS.lblCriterio = new Ext.form.Label({
				xtype : 'label',
				text : 'Criterio:',
				x : 269,
				y : 35
			});
			// Combo Criterio
			NS.cmbCriterio = new Ext.form.ComboBox(
					{
						store : NS.storeCriterio,
						name : PF + 'cmbCriterio',
						id : PF + 'cmbCriterio',
						typeAhead : true,
						mode : 'local',
						minChars : 0,
						selecOnFocus : true,
						forceSelection : true,
						x : 269,
						y : 50,
						width : 178,
						valueField : 'idCriterio',
						displayField : 'descripcion',
						autocomplete : true,
						emptyText : 'Seleccione un criterio',
						triggerAction : 'all',
						value : '',
						listeners : {
							select : {
								fn : function(combo, value) {
									combo.setDisabled(true);

									if (combo.getValue() == 0) {
										// BANCO
										NS.ocultarComponentes();
										Ext.getCmp(PF + 'cmbBanco').reset();
										NS.storeCmbBanco.load();
										Ext.getCmp(PF + 'cmbBanco')
												.setDisabled(false);
										Ext.getCmp(PF + 'cmbBanco').setVisible(
												true);
									

										NS.agregarCriterioValor('BANCO', Ext
												.getCmp(PF + 'cmbBanco')
												.getId(), 'criterio');
									} else if (combo.getValue() == 1) {
										// DIVISA
										NS.ocultarComponentes();
										Ext.getCmp(PF + 'cmbDivisa').reset();
										NS.storeCmbDivisa.load();
										Ext.getCmp(PF + 'cmbDivisa')
												.setDisabled(false);
										Ext.getCmp(PF + 'cmbDivisa')
												.setVisible(true);

										NS.agregarCriterioValor('DIVISA', Ext
												.getCmp(PF + 'cmbDivisa')
												.getId(), 'criterio');

									} else if (combo.getValue() == 2) {
										// DIVISION
										NS.ocultarComponentes();
										Ext.getCmp(PF + 'cmbDivision').reset();
										NS.storeCmbDivision.load();
										Ext.getCmp(PF + 'cmbDivision')
												.setDisabled(false);
										Ext.getCmp(PF + 'cmbDivision')
												.setVisible(true);

										NS.agregarCriterioValor('DIVISION', Ext
												.getCmp(PF + 'cmbDivision')
												.getId(), 'criterio');
									} else if (combo.getValue() == 3) {
										// ESTATUS
										NS.ocultarComponentes();
										Ext.getCmp(PF + 'cmbEstatus').reset();
										Ext.getCmp(PF + 'cmbEstatus')
												.setDisabled(false);
										Ext.getCmp(PF + 'cmbEstatus')
												.setVisible(true);

										NS.agregarCriterioValor('ESTATUS', Ext
												.getCmp(PF + 'cmbEstatus')
												.getId(), 'criterio');

									} else if (combo.getValue() == 4) {
										// FECHA
										NS.ocultarComponentes();
										Ext.getCmp(PF + 'txtFechaIni').reset();
										Ext.getCmp(PF + 'txtFechaFin').reset();
										Ext.getCmp(PF + 'txtFechaIni')
												.setDisabled(false);
										Ext.getCmp(PF + 'txtFechaIni')
												.setVisible(true);
										Ext.getCmp(PF + 'txtFechaFin')
												.setDisabled(false);
										Ext.getCmp(PF + 'txtFechaFin')
												.setVisible(true);

										NS.agregarCriterioValor('FECHA', Ext
												.getCmp(PF + 'txtFechaIni')
												.getId(), 'criterio');

									} else if (combo.getValue() == 5) {
										// FORMA DE PAGO
										Ext.getCmp(PF + 'cmbFormaPago').reset();
										NS.storeFormaPago.load();
										NS.ocultarComponentes();
										Ext.getCmp(PF + 'cmbFormaPago')
												.setDisabled(false);
										Ext.getCmp(PF + 'cmbFormaPago')
												.setVisible(true);

										NS.agregarCriterioValor(
												'FORMA DE PAGO', Ext.getCmp(
														PF + 'cmbFormaPago')
														.getId(), 'criterio');

									} else if (combo.getValue() == 6) {
										// MONTOS
										NS.ocultarComponentes();
										Ext.getCmp(PF + 'txtMontoIni').reset();
										Ext.getCmp(PF + 'txtMontoFin').reset();
										Ext.getCmp(PF + 'txtMontoIni')
												.setDisabled(false);
										Ext.getCmp(PF + 'txtMontoIni')
												.setVisible(true);
										Ext.getCmp(PF + 'txtMontoFin')
												.setDisabled(false);
										Ext.getCmp(PF + 'txtMontoFin')
												.setVisible(true);

										NS.agregarCriterioValor('MONTOS', Ext
												.getCmp(PF + 'txtMontoIni')
												.getId(), 'criterio');
									}
								}
							}
						}
					});
			// Store del grid Criterios
			NS.storeDatos = new Ext.data.DirectStore({
				paramsAsHash : false,
				root : '',
				idProperty : 'criterio',
				fields : [ {
					name : 'id'
				}, {
					name : 'criterio'
				}, {
					name : 'valor'
				} ]
			});
			// Grid Criterios
			NS.gridDatos = new Ext.grid.GridPanel(
					{
						store : NS.storeDatos,
						id : PF + 'gridDatos',
						name : PF + 'gridDatos',
						width : 290,
						height : 130,
						x : 490,
						y : 35,
						frame : true,
						title : '',

						columns : [ {
							id : 'id',
							header : 'Id',
							width : 10,
							dataIndex : 'id',
							hidden : true,
							hideable : false
						}, {
							header : 'Criterio',
							width : 120,
							dataIndex : 'criterio'
						}, {
							header : 'Valor',
							width : 135,
							dataIndex : 'valor'
						} ],
						listeners : {
							dblclick : {
								fn : function(grid) {
									var records = NS.gridDatos
											.getSelectionModel()
											.getSelections();
									for (var i = 0; i < records.length; i = i + 1) {
										NS.gridDatos.store.remove(records[i]);
										NS.gridDatos.getView().refresh();
										Ext.getCmp(PF + 'cmbCriterio')
												.setDisabled(false);

										if (records[i].get('criterio') == 'BANCO') {
											NS.cmbBanco.reset();
											NS.storeSolicitudes.baseParams.iIdBanco = 0;
											NS.iIdBanco = 0;
										}
										if (records[i].get('criterio') == 'DIVISA') {
											NS.cmbDivisa.reset();
											NS.storeSolicitudes.baseParams.sDivisa = '';
											NS.sDivisa = '';
										}
										if (records[i].get('criterio') == 'DIVISION') {
											NS.cmbDivision.reset();
											NS.storeSolicitudes.baseParams.sDivision = '';
											NS.sDivision = '';
										}
										if (records[i].get('criterio') == 'ESTATUS') {
											NS.cmbEstatus.reset();
											NS.storeSolicitudes.baseParams.sEstatus = '';
											NS.sEstatus = '';
										}
										if (records[i].get('criterio') == 'FORMA DE PAGO') {
											NS.cmbFormaPago.reset();
											NS.storeSolicitudes.baseParams.iFormaPago = 0;
											NS.iFormaPago = 0;

										}
										if (records[i].get('criterio') == 'FECHA') {
											NS.txtFechaIni.setValue('');
											NS.txtFechaFin.setValue('');
											NS.storeSolicitudes.baseParams.sFecIni = '';
											NS.storeSolicitudes.baseParams.sFecFin = '';
											NS.sFecIni = '';
											NS.sFecFin = '';

										}
										if (records[i].get('criterio') == 'MONTOS') {

											NS.txtMontoIni.setValue('');
											NS.txtMontoFin.setValue('');
											NS.storeSolicitudes.baseParams.uMontoIni = '';
											NS.storeSolicitudes.baseParams.uMontoFin = '';
											NS.uMontoIni = 0;
											NS.uMontoFin = 0;
										}
									}
								}
							}
						}

					});

			NS.smBan = new Ext.grid.CheckboxSelectionModel({
				checkOnly : false,
				singleSelect : false
			});

			NS.storeSolicitudes = new Ext.data.DirectStore({
				paramsAsHash : false,
				root : '',
				baseParams : {
					iIdBanco : 0,
					iNoEmpresa : '',
					sEstatus : '',
					sDivisa : '',
					sDivision : '',
					iFormaPago : 0,
					cTodas : 0,
					sFecFin : '',
					sFecIni : '',
					uMontoIni : '',
					uMontoFin : '',
					iNoProveedor : ''

				},
				paramOrder : [ 'iIdBanco', 'iNoEmpresa', 'sEstatus', 'sDivisa',
						'sDivision', 'iFormaPago', 'cTodas', 'sFecFin',
						'sFecIni', 'uMontoFin', 'uMontoIni', 'iNoProveedor' ],
				directFn : ModificacionPorBloqueAction.obtenerSolicitudes,

				fields : [ {
					name : 'nomEmpresa'
				}, {
					name : 'noFolioDet'
				}, {
					name : 'tipoCambio'
				}, {
					name : 'noCuenta'
				}, {
					name : 'noDocto'
				}, {
					name : 'idEstatusMov'
				}, {
					name : 'noFolioMov'
				}, {
					name : 'importe'
				}, {
					name : 'idDivisa'
				}, {
					name : 'formaPago'
				}, {
					name : 'idBancoBenef'
				}, {
					name : 'idChequeraBenef'
				}, {
					name : 'beneficiario'
				}, {
					name : 'idChequera'
				}, {
					name : 'noCheque'
				}, {
					name : 'folioRef'
				}, {
					name : 'valorTasa'
				}, {
					name : 'idTipoDocto'
				}, {
					name : 'fecValor'
				}, {
					name : 'concepto'
				}, {
					name : 'idCaja'
				}, {
					name : 'idLeyenda'
				}, {
					name : 'fecRecalculo'
				}, {
					name : 'noCliente'
				}, {
					name : 'idBanco'
				}, {
					name : 'descBanco'
				}, {
					name : 'descTipoOperacion'
				}, {
					name : 'idTipoOperacion'
				}, {
					name : 'idCveOperacion'
				}, {
					name : 'descCveOperacion'
				}, {
					name : 'descFormaPago'
				}, {
					name : 'descStatus'
				}, {
					name : 'division'
				}, {
					name : 'codBloqueo'
				}

				],
				listeners : {
					load : function(s, records) {
						if (records.length == null || records.length <= 0) {
							Ext.Msg.alert('SET', 'No hay datos');
							Ext.getCmp(PF + 'cmdEjecutar').setDisabled(true);

						} else
							NS.gridSolicitudes.getSelectionModel().selectAll();
						Ext.getCmp(PF + 'cmdEjecutar').setDisabled(false);
					}
				}
			});

			NS.smEmp = new Ext.grid.CheckboxSelectionModel({
				forceFit : false,
				fixed : true,
				checkOnly : false,
				singleSelect : true
			});

			NS.accionarCmbEmpresa = function(comboValor) {

				NS.storeCmbBanco.baseParams.empresa = comboValor;
				NS.storeDatos.removeAll();
				NS.gridDatos.getView().refresh();
				Ext.getCmp(PF + 'cmbCriterio').setDisabled(false);
			}
			// Oculta criterios
			NS.ocultarComponentes = function() {
				Ext.getCmp(PF + 'cmbBanco').setVisible(false);
				Ext.getCmp(PF + 'cmbDivisa').setVisible(false);
				Ext.getCmp(PF + 'cmbDivision').setVisible(false);
				Ext.getCmp(PF + 'cmbEstatus').setVisible(false);
				Ext.getCmp(PF + 'cmbFormaPago').setVisible(false);
				Ext.getCmp(PF + 'txtFechaIni').setVisible(false);
				Ext.getCmp(PF + 'txtFechaFin').setVisible(false);
				Ext.getCmp(PF + 'txtMontoIni').setVisible(false);
				Ext.getCmp(PF + 'txtMontoFin').setVisible(false);

			}
			// Agrega criterio y valor
			NS.agregarCriterioValor = function(sValor, oIdElemento,
					sTipoAgregado) {
				var indice = 0;
				var i = 0;
				var recordsDatGrid = NS.storeDatos.data.items;
				var sValorAnterior = '';
				for (i = 0; i < recordsDatGrid.length; i = i + 1) {
					if (recordsDatGrid[i].get('criterio') == sValor) {
						indice = i;
						sValorAnterior = recordsDatGrid[i].get('valor');
					}
				}
				if (sTipoAgregado == 'criterio') {
					if (recordsDatGrid.length > 0) {
						if ((indice > 0 || (recordsDatGrid[0].get('criterio') == sValor))
								&& sValorAnterior !== '') {
							BFwrk.Util
									.msgShow(
											'Ya indicó el criterio, necesita borrarlo antes',
											'WARNING');
							Ext.getCmp(oIdElemento).setDisabled(true);
							return;
						} else
							NS.agregarValorGridDatos(sValor, oIdElemento, 0,
									sTipoAgregado);
					} else
						NS.agregarValorGridDatos(sValor, oIdElemento, 0,
								sTipoAgregado);
				} else if (sTipoAgregado == 'valor') {
					NS.storeDatos.remove(recordsDatGrid[indice]);
					NS.agregarValorGridDatos(sValor, oIdElemento, indice,
							sTipoAgregado, sValorAnterior);
				}
			};
			// Agrega valor a criterios
			NS.agregarValorGridDatos = function(sValor, oIdElemento, indice,
					sTipoAgregado, sValorAnterior) {
				var valorCombo = Ext.getCmp(oIdElemento).getValue();
				var tamGrid = indice <= 0 ? (NS.storeDatos.data.items).length
						: indice;
				var datosClase = NS.gridDatos.getStore().recordType;

				if (sTipoAgregado == 'valor') {
					Ext.getCmp(PF + 'cmbCriterio').setDisabled(false);
					Ext.getCmp(oIdElemento).setDisabled(true);
				}
				var datos = new datosClase({
					id : valorCombo,
					criterio : sValor,
					valor : (sValorAnterior !== undefined
							&& sValorAnterior !== '' ? sValorAnterior + ' a '
							: '')
							+ (sTipoAgregado == 'criterio' ? '' : $(
									'input[id*="' + oIdElemento + '"]').val())// sCadenaValor
				});
				NS.gridDatos.stopEditing();
				NS.storeDatos.insert(tamGrid, datos);
				NS.gridDatos.getView().refresh();
			};
			// Opción seleccionada
			NS.opcionSel = function() {
				// valida opcion seleccionada
				var value = Ext.getCmp(PF + 'movimiento').getValue();
				var valor = parseInt(value.getGroupValue());
				return valor;
			}
			NS.limpiar = function() {

				Ext.getCmp(PF + 'cmbBanco').setVisible(false);
				Ext.getCmp(PF + 'cmbDivisa').setVisible(false);
				Ext.getCmp(PF + 'cmbDivision').setVisible(false);
				Ext.getCmp(PF + 'cmbEstatus').setVisible(false);
				Ext.getCmp(PF + 'cmbFormaPago').setVisible(false);
				Ext.getCmp(PF + 'txtFechaIni').setVisible(false);
				Ext.getCmp(PF + 'txtFechaFin').setVisible(false);
				Ext.getCmp(PF + 'txtMontoIni').setVisible(false);
				Ext.getCmp(PF + 'txtMontoFin').setVisible(false);

				NS.chkTodas.setValue(false);

				NS.cmbCriterio.reset();
				NS.cmbCriterio.setDisabled(false);
				NS.cmbBanco.reset();
				NS.storeSolicitudes.baseParams.iIdBanco = 0;
				NS.iIdBanco = 0;

				NS.cmbDivisa.reset();
				NS.storeSolicitudes.baseParams.sDivisa = '';
				NS.sDivisa = '';

				NS.cmbDivision.reset();
				NS.storeSolicitudes.baseParams.sDivision = '';
				NS.sDivision = '';

				NS.cmbEstatus.reset();
				NS.storeSolicitudes.baseParams.sEstatus = '';
				NS.sEstatus = '';

				NS.cmbFormaPago.reset();
				NS.storeSolicitudes.baseParams.iFormaPago = 0;
				NS.iFormaPago = 0;

				NS.txtFechaIni.setValue('');
				NS.txtFechaFin.setValue('');
				NS.storeSolicitudes.baseParams.sFecIni = '';
				NS.storeSolicitudes.baseParams.sFecFin = '';
				NS.sFecIni = '';
				NS.sFecFin = '';

				NS.txtMontoIni.setValue('');
				NS.txtMontoFin.setValue('');
				NS.storeSolicitudes.baseParams.uMontoIni = '';
				NS.storeSolicitudes.baseParams.uMontoFin = '';
				NS.uMontoIni = 0;
				NS.uMontoFin = 0;

				NS.gridDatos.store.removeAll();
				NS.gridSolicitudes.store.removeAll();

				NS.gridSolicitudes.getView().refresh();
				NS.gridDatos.getView().refresh();

				NS.cmbProveedor.reset();
				NS.txtProveedor.setValue('');
				NS.storeProveedor.removeAll();
				NS.storeUnicoBeneficiario.removeAll();
				NS.iNoProveedor = '';
				NS.storeSolicitudes.baseParams.iNoProveedor = '';
				NS.parametroBus = '';

				NS.txtFecha.setValue('');
				NS.txtFormPag.setValue('');
				NS.txtConcepto.setValue('');
				NS.cmbFormPag.reset();

				Ext.getCmp(PF + 'cmdEjecutar').setDisabled(true);

			}
			// Buscar
			NS.buscar = function() {

				// var banco = false;
				var datosBusqueda = NS.storeDatos.data.items;
				NS.storeSolicitudes.baseParams.iNoEmpresa = parseInt(Ext
						.getCmp(PF + 'txtEmpresa').getValue());
				NS.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa')
						.getValue());

				for (var i = 0; i < datosBusqueda.length; i = i + 1) {
					if (datosBusqueda[i].get('criterio') == 'BANCO') {
						NS.storeSolicitudes.baseParams.iIdBanco = parseInt(datosBusqueda[i]
								.get('id'));
						NS.iIdBanco = parseInt(datosBusqueda[i].get('id'));
					} else if (datosBusqueda[i].get('criterio') == 'FECHA') {
						var valorFechas = datosBusqueda[i].get('valor');
						var ini = NS.obtenerValIni(valorFechas);
						var fin = NS.obtenerValFin(valorFechas);
					
						NS.storeSolicitudes.baseParams.sFecIni = ini;
						NS.storeSolicitudes.baseParams.sFecFin = fin;
						NS.sFecIni = BFwrk.Util.changeDateToString(''
								+ Ext.getCmp(PF + 'txtFechaIni').getValue());
						
						NS.sFecFin = BFwrk.Util.changeDateToString(''
								+ Ext.getCmp(PF + 'txtFechaFin').getValue());
					} else if (datosBusqueda[i].get('criterio') == 'MONTOS') {
						var valorImporte = datosBusqueda[i].get('valor');
						var ini = NS.obtenerValIni(valorImporte);
						var fin = NS.obtenerValFin(valorImporte);
						NS.storeSolicitudes.baseParams.uMontoFin = BFwrk.Util
								.unformatNumber('' + fin);
						NS.storeSolicitudes.baseParams.uMontoIni = BFwrk.Util
								.unformatNumber('' + ini);
						NS.uMontoIni = BFwrk.Util.unformatNumber(''
								+ Ext.getCmp(PF + 'txtMontoIni').getValue());
						NS.uMontoFin = BFwrk.Util.unformatNumber(''
								+ Ext.getCmp(PF + 'txtMontoFin').getValue());
					} else if (datosBusqueda[i].get('criterio') == 'ESTATUS') {
						NS.storeSolicitudes.baseParams.sEstatus = datosBusqueda[i]
								.get('id');
						NS.sEstatus = datosBusqueda[i].get('id');
					} else if (datosBusqueda[i].get('criterio') == 'DIVISA') {
						NS.storeSolicitudes.baseParams.sDivisa = datosBusqueda[i]
								.get('id');
						NS.sDivisa = datosBusqueda[i].get('id');
					} else if (datosBusqueda[i].get('criterio') == 'DIVISION') {
						NS.storeSolicitudes.baseParams.sDivision = datosBusqueda[i]
								.get('id');
						NS.sDivision = datosBusqueda[i].get('id');
					} else if (datosBusqueda[i].get('criterio') == 'FORMA DE PAGO') {
						NS.storeSolicitudes.baseParams.iFormaPago = parseInt(datosBusqueda[i]
								.get('id'));
						NS.iFormaPago = parseInt(datosBusqueda[i].get('id'));
					}
				}
				if (!Ext.getCmp(PF + 'txtProveedor').getValue() == "") {
					NS.storeSolicitudes.baseParams.iNoProveedor = Ext.getCmp(
							PF + 'txtProveedor').getValue();
					NS.iNoProveedor = Ext.getCmp(PF + 'txtProveedor')
							.getValue();
				}

				else {

					NS.cmbProveedor.reset();
					NS.txtProveedor.setValue('');
					NS.storeProveedor.removeAll();
					NS.storeUnicoBeneficiario.removeAll();
					NS.iNoProveedor = '';
					NS.storeSolicitudes.baseParams.iNoProveedor = '';
					NS.parametroBus = '';
				}
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeSolicitudes,
					msg : "Buscando..."
				});
				NS.storeSolicitudes.load();
				NS.gridSolicitudes.getView().refresh();
			}
			// Obtener valor inicial
			NS.obtenerValIni = function(valor) {
				var i = 0;
				var valIni = '';
				while (valor.charAt(i) != 'a') {
					valIni += valor.charAt(i);
					i++;
				}
				valIni.replace(",", "");
				return valIni;
			}
			// Obtener valor final
			NS.obtenerValFin = function(valor) {
				var i = 0;
				var valFin = '';
				while (i < valor.length) {
					if (valor.charAt(i) == 'a') {
						valFin = valor.substring(i + 1, valor.length);
					}
					i++;
				}
				valFin.replace(",", "");
				return valFin;
			}

			NS.ejecutar = function() {

				if (!Ext.getCmp(PF + 'txtProveedor').getValue() == "") {

					NS.storeSolicitudes.baseParams.iNoProveedor = Ext.getCmp(
							PF + 'txtProveedor').getValue();
					NS.iNoProveedor = Ext.getCmp(PF + 'txtProveedor')
							.getValue();
				}
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeSolicitudes,
					msg : "Buscando..."
				});
				NS.storeSolicitudes.load();
				NS.gridSolicitudes.getView().refresh();
			}
			// ************************************************************************************************************
			// ******************************CRITERIOS PARA
			// MODIFICAR**********************************
			// Forma de pago
			// Etiqueta
			NS.lblFormPag = new Ext.form.Label({
				text : 'Forma de pago:',
				x : 60,
				y : 0
			});
			// Caja de texto
			NS.txtFormPag = new Ext.form.TextField({
				id : PF + 'txtFormPag',
				name : PF + 'txtFormPag',

				x : 60,
				y : 17,
				width : 50,
				listeners : {
					change : {
						fn : function(caja, valor) {
							BFwrk.Util.updateTextFieldToCombo(
									PF + 'txtFormPag', NS.cmbFormPag.getId());
						}
					}
				}
			});
			// Store combo
			NS.storeFrmPago = new Ext.data.DirectStore({
				paramsAsHash : false,
				baseParams : {
					campoUno : 'id_forma_pago',
					campoDos : 'desc_forma_pago',
					tabla : 'cat_forma_pago',
					condicion : '',
					orden : '2'
				},
				root : '',
				paramOrder : [ 'campoUno', 'campoDos', 'tabla', 'condicion',
						'orden' ],
				directFn : GlobalAction.llenarComboGeneral,
				idProperty : 'id',
				fields : [ {
					name : 'id'
				}, {
					name : 'descripcion'
				} ],
				listeners : {
					load : function(s, records) {

					}
				}
			});

			NS.seleccionaFormaDePago = function(formaPago) {
				Ext.getCmp(PF + 'txtFormPag').setValue(formaPago);

			};

			NS.storeFrmPago.load();

			// Combo
			NS.cmbFormPag = new Ext.form.ComboBox({
				store : NS.storeFrmPago,
				name : PF + 'cmbFormPag',
				id : PF + 'cmbFormPag',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 112,
				y : 17,
				width : 280,
				valueField : 'id',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione una forma de pago',
				triggerAction : 'all',

				listeners : {
					select : {
						fn : function(combo, valor) {
							NS.seleccionaFormaDePago(combo.getValue());
						}
					}
				}
			});

			// *********************************************************
			NS.storeProveedor = new Ext.data.DirectStore({
				paramsAsHash : false,
				root : '',
				baseParams : {
					texto : '',
					iNoEmpresa : ''
				},
				paramOrder : [ 'texto', 'iNoEmpresa' ],
				directFn : ModificacionPorBloqueAction.obtenerProveedores,
				idProperty : 'idStr',
				fields : [ {
					name : 'idStr'
				}, {
					name : 'descripcion'
				} ],
				listeners : {
					load : function(s, records) {

						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeProveedor,
							msg : "Cargando..."
						});
						if (records.length == null || records.length <= 0) {
							Ext.Msg.alert('SET',
									'No Existen proveedores con ese nombre');
						}
					}
				}
			});
			NS.storeProveedor.sort([ {
				field : 'descripcion',
				direction : 'ASC'
			} ], 'ASC');

			NS.storeUnicoBeneficiario = new Ext.data.DirectStore({
				paramsAsHash : false,
				baseParams : {
					iNoEmpresa : '',
					texto : ''
				},
				root : '',
				paramOrder : [ 'iNoEmpresa', 'texto' ],
				root : '',
				directFn : ModificacionPorBloqueAction.obtenerBeneficiario,
				idProperty : 'id',
				fields : [ {
					name : 'id'
				}, {
					name : 'descripcion'
				}, ],
				listeners : {
					load : function(s, records) {
						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeUnicoBeneficiario,
							msg : "Cargando..."
						});

						if (records.length == null || records.length <= 0) {

							Ext.Msg.alert('SET',
									'No Existe el beneficiario con esa clave');
							NS.cmbProveedor.reset();
							NS.txtProveedor.setValue('');
							NS.storeProveedor.removeAll();
							NS.storeUnicoBeneficiario.removeAll();
							NS.iNoProveedor = '';
							NS.storeSolicitudes.baseParams.iNoProveedor = '';
							NS.parametroBus = '';

							return;
						} else {
							var reg = NS.storeUnicoBeneficiario.data.items;
							Ext.getCmp(NS.cmbProveedor.getId()).setValue(
									reg[0].get('descripcion'));
							NS.accionarUnicoBeneficiario(reg[0].get('id'));
						}
					}
				}
			});

			NS.lblProveedor = new Ext.form.Label({
				text : 'Proveedor:',
				x : 60,
				y : 150
			});
			NS.txtProveedor = new Ext.form.TextField(
					{
						id : PF + 'txtProveedor',
						name : PF + 'txtProveedor',
						x : 60,
						y : 165,
						width : 100,
						listeners : {
							change : {
								fn : function(caja, valor) {
									
									
									if (caja.getValue() == "") {
										NS.cmbProveedor.reset();
										NS.txtProveedor.setValue('');
										NS.storeProveedor.removeAll();
										NS.storeUnicoBeneficiario.removeAll();
										NS.iNoProveedor = '';
										NS.storeSolicitudes.baseParams.iNoProveedor = '';
										NS.parametroBus = '';

									}
									if (caja.getValue() != null
											&& caja.getValue() != undefined
											&& caja.getValue() != '') {
										NS.storeUnicoBeneficiario.baseParams.iNoEmpresa = parseInt(Ext
												.getCmp(PF + 'txtEmpresa')
												.getValue());
										NS.storeUnicoBeneficiario.baseParams.texto = ''
												+ caja.getValue();
										var myMask = new Ext.LoadMask(Ext
												.getBody(), {
											store : NS.storeUnicoBeneficiario,
											msg : "Cargando Proveedor..."
										});
										NS.storeUnicoBeneficiario.load();
									} else {
										NS.cmbProveedor.reset();
										NS.storeUnicoBeneficiario.removeAll();
									}
								}
							}
						}
					});

			NS.cmbProveedor = new Ext.form.ComboBox({
				store : NS.storeProveedor,
				name : PF + 'cmbProveedor',
				id : PF + 'cmbProveedor',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 165,
				y : 165,
				width : 280,
				valueField : 'idStr',
				displayField : 'descripcion',

				autocomplete : true,
				emptyText : 'Seleccione un proveedor',
				triggerAction : 'all',
				// value:'',
				visible : false,
				listeners : {
					select : {
						fn : function(combo, valor) {
							// linea cambia combo
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtProveedor', NS.cmbProveedor.getId());
							var nuevo = NS.txtProveedor.getValue();
							NS.txtProveedor.setValue(nuevo.replace(/ /g, ""));
						}
					},
					beforequery : function(qe) {
						NS.parametroBus = qe.combo.getRawValue();
					}
				}
			});
			NS.cmdProveedor = new Ext.Button(
					{
						id : PF + 'cmdProveedor',
						name : PF + 'cmdProveedor',
						text : 'B',
						x : 450,
						y : 165,
						width : 18,
						height : 10,
						listeners : {
							click : {
								fn : function(e) {

									NS.storeProveedor.baseParams.texto = NS.parametroBus;
									NS.storeProveedor.baseParams.iNoEmpresa = parseInt(Ext
											.getCmp(PF + 'txtEmpresa')
											.getValue());
									var myMask = new Ext.LoadMask(
											Ext.getBody(), {
												store : NS.storeProveedor,
												msg : "Cargando Proveedores..."
											});
									NS.storeProveedor.load();
								}
							}
						}
					});

			NS.accionarUnicoBeneficiario = function(valueCombo) {
				NS.idPersona = valueCombo;
				NS.nomPersona = NS.storeUnicoBeneficiario.getById(valueCombo)
						.get('descripcion');
			}

			NS.accionarBeneficiario = function(valueCombo) {
				NS.idPersona = valueCombo;
				NS.nomPersona = NS.storeProveedor.getById(valueCombo).get(
						'descripcion');
			}

			// ***************************************************************************
			// Fecha
			// Etiqueta
			NS.lblFecha = new Ext.form.Label({
				text : 'Fecha:',
				x : 410,
				y : 0
			});
			// Calendario
			NS.txtFecha = new Ext.form.DateField({
				id : PF + 'txtFecha',
				name : PF + 'txtFecha',
				format : 'd/m/Y',
				x : 410,
				y : 17,
				width : 100,
				listeners : {
					change : {
						fn : function(caja, valor) {
						}
					}
				}
			});

			// Concepto
			// Etiqueta
			NS.lblConcepto = new Ext.form.Label({
				text : 'Concepto:',
				x : 525,
				y : 0
			});
			// Caja de texto
			NS.txtConcepto = new Ext.form.TextField({
				id : PF + 'txtConcepto',
				name : PF + 'txtConcepto',
				x : 525,
				y : 17,
				width : 300,
				listeners : {
					change : {
						fn : function(caja, valor) {

						}
					}
				}
			});

			// Botones inferiores
			// Botón Bloqueo
			NS.cmdBloquear = apps.SET
					.btnFacultativo(new Ext.Button(
							{
								id : 'cmdBloquear',
								name : 'cmdBloquear',
								text : 'Bloqueo',
								x : 611,
								y : 525,
								width : 80,

								listeners : {
									click : {
										fn : function(e) {
											var records = NS.storeSolicitudes.data.items;
											if (records.length <= 0) {
												BFwrk.Util
														.msgShow(
																'Es necesario seleccionar algún registro.',
																'WARNING');
												return;
											}
											NS.enviarParams2();

										}

									}
								}
							}));

			// Botón Ejecutar
			NS.cmdEjecutar = new Ext.Button({
				xtype : 'button',
				id : PF + 'cmdEjecutar',
				name : PF + 'cmdEjecutar',
				text : 'Ejecutar',
				x : 711,
				y : 525,
				disabled : true,
				width : 80,
				listeners : {
					click : {
						fn : function(e) {
							var records = NS.storeSolicitudes.data.items;
							if (records.length <= 0) {
								BFwrk.Util.msgShow(
										'No hay registros para modificar.',
										'WARNING');
								return;
							}

							if (NS.verificaCampos() == true) {
								NS.fechaMod = Ext.getCmp(PF + 'txtFecha')
										.getValue();
								NS.formaPagoMod = Ext.getCmp(PF + 'txtFormPag')
										.getValue();
								NS.conceptoMod = Ext.getCmp(PF + 'txtConcepto')
										.getValue();

								NS.enviarParams(NS.fechaMod, NS.formaPagoMod,
										NS.conceptoMod);

							}

						}
					}
				}
			});
			NS.verificaCampos = function() {
				var registros = NS.gridSolicitudes.getSelectionModel()
						.getSelections();
				var f = true;
				if (Ext.getCmp(PF + 'txtEmpresa').getValue() == '') {
					BFwrk.Util.msgShow(
							'Debe seleccionar una empresa concentradora',
							'WARNING');
					return false;
				}

				if (registros.length <= 0) {
					BFwrk.Util.msgShow(
							'Es necesario seleccionar algún registro. ',
							'WARNING');
					return false;
				}
				if (Ext.getCmp(PF + 'txtFecha').getValue() == ''
						&& Ext.getCmp(PF + 'txtFormPag').getValue() == ''
						&& Ext.getCmp(PF + 'txtConcepto').getValue() == '') {
					BFwrk.Util
							.msgShow(
									'Debe seleccionar una forma de pago, fecha o un concepto.',
									'WARNING');
					return false;
				}

				return true;
			}
			NS.verificaCampos2 = function() {
				if (Ext.getCmp(PF + 'txtEmpresa').getValue() == '') {
					BFwrk.Util.msgShow('Debe seleccionar una empresa o todas.',
							'WARNING');
					return false;
				}
				return true;
			}

			NS.enviarParams = function(fechaMod, formaPagoMod, conceptoMod) {

				BFwrk.Util.msgWait('Modificando solicitudes...', true);
				var registros = NS.gridSolicitudes.getSelectionModel()
						.getSelections();
				var matrizSolicitudes = new Array();
				for (var i = 0; i < registros.length; i++) {
					var regSol = {};

					regSol.nomEmpresa = registros[i].get('nomEmpresa');
					regSol.noCliente = registros[i].get('noCliente');
					regSol.idLeyenda = registros[i].get('idLeyenda');
					regSol.noFolioDet = registros[i].get('noFolioDet');
					regSol.idDivisa = registros[i].get('idDivisa');

					matrizSolicitudes[i] = regSol;
				}
				NS.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa')
						.getValue());
				if (fechaMod != null && fechaMod != '') {
					NS.fecha = BFwrk.Util.changeDateToString('' + fechaMod);
				} else {
					NS.fecha = '';
				}

				if (formaPagoMod != null && formaPagoMod != '') {
					NS.formaPago = parseInt(formaPagoMod);
				} else {
					NS.formaPago = 0;
				}
				var jsonStringS = Ext.util.JSON.encode(matrizSolicitudes);
				var terminado = false;
				ModificacionPorBloqueAction
						.ejecutar(
								jsonStringS,
								NS.noEmpresa,
								NS.fecha,
								NS.formaPago,
								conceptoMod,

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

			NS.enviarParams2 = function() {

				BFwrk.Util.msgWait('Procesando...', true);
				var registros = NS.gridSolicitudes.getSelectionModel()
						.getSelections();
				if (registros != 0) {
					var matrizSolicitudes = new Array();
					for (var i = 0; i < registros.length; i++) {
						var regSol = {};
						regSol.codBloqueo = registros[i].get('codBloqueo');
						regSol.noFolioDet = registros[i].get('noFolioDet');
						matrizSolicitudes[i] = regSol;
					}
					NS.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa')
							.getValue());
					var jsonStringS = Ext.util.JSON.encode(matrizSolicitudes);
					var terminado = false;
					ModificacionPorBloqueAction
							.bloquear(
									jsonStringS,
									NS.noEmpresa,

									function(mapResult, e) {

										BFwrk.Util.msgWait('Terminado...',
												false);

										if (mapResult.msgUsuario !== null
												&& mapResult.msgUsuario !== ''
												&& mapResult.msgUsuario != undefined) {
											for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {

												Ext.Msg
														.alert(
																'Información SET',
																''
																		+ mapResult.msgUsuario[msg]);
											}
											NS.buscar();
										}

									});
				} else {

					BFwrk.Util.msgShow(
							'Es necesario seleccionar algún registro. ',
							'WARNING');
					return false;
				}

			}

			// Botón Limpiar
			NS.cmdLimpiar = new Ext.Button({
				xtype : 'button',
				id : PF + 'cmdLimpiar',
				name : PF + 'cmdLimpiar',
				text : 'Limpiar',
				x : 811,
				y : 525,
				width : 80,
				listeners : {
					click : {
						fn : function(e) {

							NS.limpiar();
						}
					}
				}
			});
			NS.smE = new Ext.grid.CheckboxSelectionModel({
				forceFit : false
			});

			NS.gridSolicitudes = new Ext.grid.GridPanel({
				store : NS.storeSolicitudes,
				id : PF + 'gridSolicitudes',
				name : PF + 'gridSolicitudes',
				title : 'Solicitudes',
				frame : true,
				width : 975,
				height : 190,
				x : 0,
				y : 235,
				cm : new Ext.grid.ColumnModel({
					defaults : {
						width : 120,
						value : true,
						sortable : true
					},

					columns : [ NS.smE, {

						id : 'nomEmpresa',
						header : 'Empresa',
						width : 200,
						dataIndex : 'nomEmpresa',
						direction : 'ASC'
					}, {
						header : 'Estatus',
						width : 100,
						dataIndex : 'descStatus',

					}, {
						header : 'No. Docto.',
						width : 100,
						dataIndex : 'noDocto',

					}, {
						header : 'Fec. Operación',
						width : 100,
						dataIndex : 'fecValor',

					}, {
						header : 'Clave Operación',
						width : 150,
						dataIndex : 'descCveOperacion',

					}, {
						header : 'Importe',
						width : 100,
						dataIndex : 'importe',

					}, {
						header : 'Divisa',
						width : 50,
						dataIndex : 'idDivisa',

					}, {
						header : 'Forma de pago',
						width : 150,
						dataIndex : 'descFormaPago',

					}, {
						header : 'Banco Benef.',
						width : 150,
						dataIndex : 'descBanco',

					}, {
						header : 'Chequera Benef.',
						width : 100,
						dataIndex : 'idChequeraBenef',

					}, {
						header : 'Beneficiario',
						width : 250,
						dataIndex : 'beneficiario',

					}, {
						header : 'Concepto',
						width : 250,
						dataIndex : 'concepto',

					}, {
						header : 'Tipo cambio',
						width : 100,
						dataIndex : 'tipoCambio',

					}, {
						header : 'Cod. Bloqueo',
						width : 100,
						dataIndex : 'codBloqueo',

					},
					// Pertenecen a la consulta
					{
						header : 'noFolioDet',
						width : 0,
						dataIndex : 'noFolioDet',
						hidden : true,
						hideable : false,
					}, {
						header : 'No. Cuenta',
						width : 100,
						dataIndex : 'noCuenta',
						hidden : true,
						hideable : false
					}, {
						header : 'Id Estatus Mov',
						width : 100,
						dataIndex : 'idEstatusMov',
						hidden : true,
						hideable : false
					}, {
						header : 'noFolioMov',
						width : 100,
						dataIndex : 'noFolioMov',
						hidden : true,
						hideable : false
					}, {
						header : 'formaPago',
						width : 100,
						dataIndex : 'formaPago',
						hidden : true,
						hideable : false
					}, {
						header : 'idBancoBenef',
						width : 100,
						dataIndex : 'idBancoBenef',
						hidden : true,
						hideable : false
					}, {
						header : 'idChequera',
						width : 100,
						dataIndex : 'idChequera',
						hidden : true,
						hideable : false
					}, {
						header : 'noCheque',
						width : 100,
						dataIndex : 'noCheque',
						hidden : true,
						hideable : false
					}, {
						header : 'folioRef',
						width : 100,
						dataIndex : 'folioRef',
						hidden : true,
						hideable : false
					}, {
						header : 'valorTasa',
						width : 100,
						dataIndex : 'valorTasa',
						hidden : true,
						hideable : false
					}, {
						header : 'idTipoDocto',
						width : 100,
						dataIndex : 'idTipoDocto',
						hidden : true,
						hideable : false
					}, {
						header : 'idCaja',
						width : 100,
						dataIndex : 'idCaja',
						hidden : true,
						hideable : false
					}, {
						header : 'idLeyenda',
						width : 100,
						dataIndex : 'idLeyenda'

					}, {
						header : 'fecRecalculo',
						width : 100,
						dataIndex : 'fecRecalculo',
						hidden : true,
						hideable : false
					}, {
						header : 'noCliente',
						width : 100,
						dataIndex : 'noCliente',
						hidden : true,
						hideable : false
					}, {
						header : 'idBanco',
						width : 100,
						dataIndex : 'idBanco',
						hidden : true,
						hideable : false
					}, {
						header : 'descTipoOperacion',
						width : 100,
						dataIndex : 'descTipoOperacion',
						hidden : true,
						hideable : false
					}, {
						header : 'idTipoOperacion',
						width : 100,
						dataIndex : 'idTipoOperacion',
						hidden : true,
						hideable : false
					}, {
						header : 'idCveOperacion',
						width : 100,
						dataIndex : 'idCveOperacion',
						hidden : true,
						hideable : false
					}, {
						header : 'division',
						width : 100,
						dataIndex : 'division',
						hidden : true,
						hideable : false
					} ]
				}),
				viewConfig : {
					getRowClass : function(record, index) {
					}
				},
				sm : NS.smE,
				listeners : {
					click : {
						fn : function(grid) {
						}
					}
				}
			});

			// Contenedor Modificaciones de solicitudes de pago
			NS.contenedorModifSolPago = new Ext.FormPanel({
				title : 'Modificación de solicitudes de pago',
				width : 803,
				height : 700,
				padding : 10,
				layout : 'absolute',
				frame : true,
				autoScroll : true,
				renderTo : NS.tabContId,
				items : [ {
					xtype : 'fieldset',
					title : '',
					x : 0,
					y : 0,
					layout : 'absolute',
					height : 580,
					width : 1000,
					items : [
							{
								xtype : 'fieldset',
								title : 'Búsqueda',
								x : 0,
								y : 0,
								width : 975,
								height : 225,
								layout : 'absolute',
								items : [ NS.lblEmpresa, NS.txtEmpresa,
										NS.cmbEmpresa, NS.lblProveedor,
										NS.txtProveedor, NS.cmbProveedor,
										NS.cmdProveedor, NS.lblTodas,
										NS.chkTodas, NS.lblCriterio,
										NS.cmbCriterio, NS.lblValor,
										NS.cmbBanco, NS.cmbDivisa,
										NS.cmbDivision, NS.cmbEstatus,
										NS.cmbFormaPago, NS.txtFechaIni,
										NS.txtFechaFin, NS.txtMontoIni,
										NS.txtMontoFin, NS.cmdBuscar,
										NS.gridDatos ]
							},
							NS.gridSolicitudes,
							{
								xtype : 'fieldset',
								title : '',
								x : 0,
								y : 438,
								width : 975,
								height : 65,
								layout : 'absolute',
								items : [

								NS.lblFormPag, NS.txtFormPag, NS.cmbFormPag,
										NS.lblFecha, NS.txtFecha,
										NS.lblConcepto, NS.txtConcepto ]
							}, NS.cmdBloquear, NS.cmdEjecutar, NS.cmdLimpiar ]
				} ]
			});

			NS.contenedorModifSolPago.setSize(Ext.get(NS.tabContId).getWidth(),
					Ext.get(NS.tabContId).getHeight());
		});