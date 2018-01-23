Ext
		.onReady(function() {
			var NS = Ext
					.namespace('apps.SET.Utilerias.Mantenimiento.Conceptos');
			NS.tabContId = apps.SET.tabContainerId;
			NS.idUsuario = apps.SET.iUserId;
			NS.noEmpresa = apps.SET.noEmpresa;
			NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
			var PF = apps.SET.tabID + '.';
			Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
			Ext.QuickTips.init();
			NS.ingresoEgreso = "I";
			NS.idBanco = 0;
			NS.formPago = "";
			NS.conceptoBanco = "";
			NS.modificar = false;
			NS.ingresoEgresoNuevo = "I";
			var registros = 0;

			// LABELS BUSQUEDA
			NS.lblBanco = new Ext.form.Label({
				text : 'Banco',
				x : 0,
				y : 0
			});

			NS.lblConceptoBanco = new Ext.form.Label({
				text : 'Concepto Banco',
				x : 250,
				y : 0
			});

			NS.lblFormaPago = new Ext.form.Label({
				text : 'Forma De Pago',
				x : 600,
				y : 0
			});

			// LABEL NUEVO
			NS.lblBancoNuevo = new Ext.form.Label({
				text : 'Banco',
				x : 0,
				y : 0
			});

			NS.lblConceptoBancoNuevo = new Ext.form.Label({
				text : 'Concepto Banco',
				x : 240,
				y : 0
			});

			NS.lblChequeras = new Ext.form.Label({
				text : 'Tipos de Chequeras a Afectar',
				x : 410,
				y : 0
			});

			NS.lblFormaPagoNuevo = new Ext.form.Label({
				text : 'Forma de Pago',
				x : 790,
				y : 0
			});

			NS.lblClaveConcepto = new Ext.form.Label({
				text : 'Clave Concepto',
				hidden : true,
				x : 240,
				y : 45
			});

			// LABELS PARA MANTENIMIENTO DE FORMA DE PAGO
			NS.lblTipoOperacion = new Ext.form.Label({
				text : 'Tipo De operación',
				x : 200,
				y : 0
			});

			// TEXT FIELD BUSQUEDA
			NS.txtIdBancoBusqueda = new Ext.form.TextField({
				id : PF + 'txtIdBancoBusqueda',
				name : PF + 'txtIdBancoBusqueda',
				x : 0,
				y : 15,
				width : 50,
				tabIndex : 0,
				listeners : {
					change : {
						fn : function(caja) {
							if (caja.getValue() != null
									&& caja.getValue() != undefined
									&& caja.getValue() != '') {
								BFwrk.Util.updateTextFieldToCombo(PF
										+ 'txtIdBancoBusqueda',
										NS.cmbBancoBusqueda.getId());
							} else
								NS.cmbBancoBusqueda.reset();
						}
					}
				}
			});

			NS.txtIdFormaPago = new Ext.form.TextField({
				id : PF + 'txtIdFormaPago',
				name : PF + 'txtIdFormaPago',
				x : 600,
				y : 0,
				width : 50,
				hidden : true
			});

			NS.txtConceptoBanco = new Ext.form.TextField({
				id : PF + 'txtConceptoBanco',
				name : PF + 'txtConceptoBanco',
				x : 250,
				y : 15,
				width : 150,
				tabIndex : 2
			});

			NS.txtClaveConcepto = new Ext.form.TextField({
				id : PF + 'txtClaveConcepto',
				name : PF + 'txtClaveConcepto',
				hidden : true,
				x : 240,
				y : 60,
				width : 150,
				tabIndex : 2
			});

			// TEXTFIELD NUEVOS
			NS.txtIdBancoNuevo = new Ext.form.TextField({
				id : PF + 'txtIdBancoNuevo',
				name : PF + 'txtIdBancoNuevo',
				x : 0,
				y : 15,
				width : 50,
				tabIndex : 10,
				listeners : {
					change : {
						fn : function(caja) {
							if (caja.getValue() != null
									&& caja.getValue() != undefined
									&& caja.getValue() != '') {
								var banco = BFwrk.Util.updateTextFieldToCombo(
										PF + 'txtIdBancoNuevo',
										NS.cmbBancoNuevo.getId());
								NS.visibleCveConcepto(banco);
							} else {
								NS.cmbBancoNuevo.reset();
								NS.visibleCveConcepto(0);
							}
						}
					}
				}
			});

			NS.txtConceptoBancoNuevo = new Ext.form.TextField({
				id : PF + 'txtConceptoBancoNuevo',
				name : PF + 'txtConceptoBancoNuevo',
				x : 240,
				y : 15,
				width : 150,
				tabIndex : 12
			});

			// TEXTFIEL DE LA FORMA DE PAGO
			NS.txtIdTipoOperacion = new Ext.form.TextField({
				id : PF + 'txtIdTipoOperacion',
				name : PF + 'txtIdTipoOperacion',
				x : 200,
				y : 15,
				width : 50
			});

			// RADIO BUTTON
			NS.optIngresoEgreso = new Ext.form.RadioGroup(
					{
						id : PF + 'optIngresoEgreso',
						name : PF + 'optIngresoEgreso',
						columns : 2,
						items : [
								{
									boxLabel : 'Ingreso',
									name : 'optSeleccion',
									inputValue : 0,
									checked : true,
									tabIndex : 3,
									listeners : {
										check : {
											fn : function(opt, valor) {
												if (valor == true) {
													c = "I";
													NS.storeFormaPago.baseParams.ingresoEgreso = NS.ingresoEgreso;
													NS.storeFormaPago.load();
												}
											}
										}
									}
								},
								{
									boxLabel : 'Egreso',
									name : 'optSeleccion',
									inputValue : 1,
									tabIndex : 4,
									listeners : {
										check : {
											fn : function(opt, valor) {
												if (valor == true) {
													NS.ingresoEgreso = "E";
													NS.storeFormaPago.baseParams.ingresoEgreso = NS.ingresoEgreso;
													NS.storeFormaPago.load();
												}
											}
										}
									}
								} ]
					});

			NS.optIngresoEgresoNuevo = new Ext.form.RadioGroup(
					{
						id : PF + 'optIngresoEgresoNuevo',
						name : PF + 'optIngresoEgresoNuevo',
						columns : 2,
						items : [
								{
									boxLabel : 'Ingreso',
									name : 'optSeleccionNuevo',
									inputValue : 0,
									checked : true,
									tabIndex : 15,
									listeners : {
										check : {
											fn : function(opt, valor) {
												if (valor == true) {
													NS.ingresoEgresoNuevo = "I";
													NS.storeFormaPago.baseParams.ingresoEgreso = NS.ingresoEgresoNuevo;
													NS.storeFormaPago.load();
												}
											}
										}
									}
								},
								{
									boxLabel : 'Egreso',
									name : 'optSeleccionNuevo',
									inputValue : 1,
									tabIndex : 16,
									listeners : {
										check : {
											fn : function(opt, valor) {
												if (valor == true) {
													NS.ingresoEgresoNuevo = "E";
													NS.storeFormaPago.baseParams.ingresoEgreso = NS.ingresoEgresoNuevo;
													NS.storeFormaPago.load();
												}
											}
										}
									}
								} ]
					});

			NS.optIngresoEgresoFP = new Ext.form.RadioGroup({
				id : PF + 'optIngresoEgresoFP',
				name : PF + 'optIngresoEgresoFP',
				columns : 2,
				items : [ {
					boxLabel : 'Ingreso',
					name : 'optSeleccionFP',
					inputValue : 0,
					checked : true,
					listeners : {
						check : {
							fn : function(opt, valor) {
								if (valor == true) {
									/*
									 * NS.ingresoEgresoNuevo = "I";
									 * NS.storeFormaPago.baseParams.ingresoEgreso =
									 * NS.ingresoEgresoNuevo;
									 * NS.storeFormaPago.load();
									 */
								}
							}
						}
					}
				}, {
					boxLabel : 'Egreso',
					name : 'optSeleccionFP',
					inputValue : 1,
					listeners : {
						check : {
							fn : function(opt, valor) {
								if (valor == true) {/*
													 * NS.ingresoEgresoNuevo =
													 * "E";
													 * NS.storeFormaPago.baseParams.ingresoEgreso =
													 * NS.ingresoEgresoNuevo;
													 * NS.storeFormaPago.load();
													 */
								}
							}
						}
					}
				} ]
			});

			// FUNCIONES
			NS.visibleCveConcepto = function(banco) {
				if (banco == 12) {
					NS.lblClaveConcepto.setVisible(true);
					Ext.getCmp(PF + 'txtClaveConcepto').setVisible(true);
				} else {
					NS.lblClaveConcepto.setVisible(false);
					Ext.getCmp(PF + 'txtClaveConcepto').setVisible(false);
				}
			};

			NS.buscar = function() {
				NS.idBanco = Ext.getCmp(PF + 'txtIdBancoBusqueda').getValue();
				NS.formaPago = Ext.getCmp(PF + 'cmbFormaPago').getValue();
				NS.conceptoBanco = Ext.getCmp(PF + 'txtConceptoBanco')
						.getValue();

				NS.limpiaPanelNuevo(true, false);

				if (NS.idBanco == '')
					Ext.Msg.alert('SET', 'Debe de seleccionar un banco');
				else {
					NS.storeGrid.baseParams.idBanco = parseInt(NS.idBanco == '' ? 0
							: NS.idBanco);
					NS.storeGrid.baseParams.conceptoBanco = NS.conceptoBanco;
					NS.storeGrid.baseParams.idFormaPago = NS.formaPago;
					NS.storeGrid.baseParams.ingresoEgreso = NS.ingresoEgreso;
					NS.storeGrid.load();
				}
			};

			NS.llenaCampos = function() {
				var registroSeleccionado = NS.gridDatos.getSelectionModel()
						.getSelections();

				if (registroSeleccionado.length <= 0)
					Ext.Msg.alert('SET',
							'Debes de seleccionar un registro para modificar');
				else {
					Ext.getCmp(PF + 'txtIdBancoNuevo').setDisabled(true);
					Ext.getCmp(PF + 'cmbBancoNuevo').setDisabled(true);
					Ext.getCmp(PF + 'txtConceptoBancoNuevo').setDisabled(true);
					Ext.getCmp(PF + 'cmbFormaPagoNuevo').setDisabled(false);
					Ext.getCmp(PF + 'panelIngresoEgresoNuevo').setDisabled(
							false);
					Ext.getCmp(PF + 'chkDespliega').setVisible(true);
					Ext.getCmp(PF + 'chkDespliega').setDisabled(false);
					Ext.getCmp(PF + 'chkImporta').setVisible(true);
					Ext.getCmp(PF + 'chkImporta').setDisabled(false);
					Ext.getCmp(PF + 'chkCargoCuenta').setDisabled(false);
					Ext.getCmp(PF + 'txtIdBancoNuevo').setValue(
							registroSeleccionado[0].get('idBanco'));
					Ext.getCmp(PF + 'cmbBancoNuevo').setValue(
							registroSeleccionado[0].get('descBanco'));
					Ext.getCmp(PF + 'txtConceptoBancoNuevo').setValue(
							registroSeleccionado[0].get('conceptoBanco'));
					Ext.getCmp(PF + 'cmbFormaPagoNuevo').setValue(
							registroSeleccionado[0].get('conceptoSet')); 
					Ext.getCmp(PF + 'txtClaveConcepto').setValue(
							registroSeleccionado[0].get('cveConcepto'));
					NS.visibleCveConcepto(registroSeleccionado[0]
							.get('idBanco'));

					if (registroSeleccionado[0].get('cargoAbono') == 'I')
						Ext.getCmp(PF + 'optIngresoEgresoNuevo').setValue(0);
					else if (registroSeleccionado[0].get('cargoAbono') == 'E') {
						Ext.getCmp(PF + 'optIngresoEgresoNuevo').setValue(1);
						Ext.getCmp(PF + 'chkRechazado').setDisabled(true);
					}

					if (registroSeleccionado[0].get('despliega') == 'S')
						Ext.getCmp(PF + 'chkDespliega').setValue(true);
					else
						Ext.getCmp(PF + 'chkDespliega').setValue(false);

					if (registroSeleccionado[0].get('importa') == 'S')
						Ext.getCmp(PF + 'chkImporta').setValue(true);
					else
						Ext.getCmp(PF + 'chkImporta').setValue(false);

					if (registroSeleccionado[0].get('cargoCuenta') == 'S')
						Ext.getCmp(PF + 'chkCargoCuenta').setValue(true);
					else
						Ext.getCmp(PF + 'chkCargoCuenta').setValue(false);

					if (registroSeleccionado[0].get('rechazo') == 'S')
						Ext.getCmp(PF + 'chkRechazado').setValue(true);
					else
						Ext.getCmp(PF + 'chkRechazado').setValue(false);
					if (registroSeleccionado[0].get('clasChequera') == 'X') {
						Ext.getCmp(PF + 'chkConcentradora').getValue(true);
					} else {
						Ext.getCmp(PF + 'chkConcentradora').getValue(false);
					}

					if (registroSeleccionado[0].get('clasChequera').charAt(0) == 'X') {
						Ext.getCmp(PF + 'chkConcentradora').setValue(true);
					} else {
						Ext.getCmp(PF + 'chkConcentradora').setValue(false);
					}
					if (registroSeleccionado[0].get('clasChequera').charAt(1) == 'X') {
						Ext.getCmp(PF + 'chkPagadora').setValue(true);
					} else {
						Ext.getCmp(PF + 'chkPagadora').setValue(false);
					}
					if (registroSeleccionado[0].get('clasChequera').charAt(2) == 'X') {
						Ext.getCmp(PF + 'chkCoinversora').setValue(true);
					} else {
						Ext.getCmp(PF + 'chkCoinversora').setValue(false);
					}
					if (registroSeleccionado[0].get('clasChequera').charAt(3) == 'X') {
						Ext.getCmp(PF + 'chkMixta').setValue(true);
					} else {
						Ext.getCmp(PF + 'chkMixta').setValue(false);
					}

					/*
					 * if (registroSeleccionado[0].get('clasChequera').trim() ==
					 * 'P') { // Ext.getCmp(PF + 'txtIdChequera').setValue('P'); //
					 * Ext.getCmp(PF + 'cmbChequeras').setValue('PAGADORA'); }
					 * else if (registroSeleccionado[0].get('clasChequera')
					 * .trim() == 'C') { // Ext.getCmp(PF +
					 * 'txtIdChequera').setValue('C'); // Ext.getCmp(PF + //
					 * 'cmbChequeras').setValue('CONCENTRADORA'); } else if
					 * (registroSeleccionado[0].get('clasChequera') .trim() ==
					 * 'CO') { // Ext.getCmp(PF +
					 * 'txtIdChequera').setValue('CO'); // Ext.getCmp(PF + //
					 * 'cmbChequeras').setValue('COINVERSORA'); } else if
					 * (registroSeleccionado[0].get('clasChequera') .trim() ==
					 * 'M') { // Ext.getCmp(PF + 'txtIdChequera').setValue('M'); //
					 * Ext.getCmp(PF + 'cmbChequeras').setValue('MIXTA'); } else { //
					 * Ext.getCmp(PF + 'txtIdChequera').setValue(''); //
					 * Ext.getCmp(PF + 'cmbChequeras').reset(); }
					 */
					NS.panelNuevo.setDisabled(false);
				}
			};

			NS.aceptarModificar = function() {
				var vector = {};
				var matriz = new Array();

				var registroSeleccionado = NS.gridDatos.getSelectionModel()
						.getSelections();

				vector.idBanco = Ext.getCmp(PF + 'txtIdBancoNuevo').getValue();
				vector.cveConcepto = Ext.getCmp(PF + 'txtClaveConcepto')
						.getValue();
				vector.conceptoBanco = Ext.getCmp(PF + 'txtConceptoBancoNuevo')
						.getValue();
				vector.conceptoSet = Ext.getCmp(PF + 'cmbFormaPagoNuevo')
						.getValue();
				vector.ingresoEgreso = NS.ingresoEgresoNuevo;

				vector.despliega = Ext.getCmp(PF + 'chkDespliega').getValue(
						true) ? 'S' : 'N';
				vector.importa = Ext.getCmp(PF + 'chkImporta').getValue(true) ? 'S'
						: 'N';
				vector.cargoCuenta = Ext.getCmp(PF + 'chkCargoCuenta')
						.getValue(true) ? 'S' : 'N';
				vector.rechazo = Ext.getCmp(PF + 'chkRechazado').getValue(true) ? 'S'
						: 'N';
				///SE AGREGO LA FUNCIONALIDAD DE LOS CHK 
				vector.clasChequera = ''; 
				if (Ext.getCmp(PF + 'chkConcentradora').getValue(true)) 
					vector.clasChequera = vector.clasChequera + 'X';  
				else 
					vector.clasChequera = vector.clasChequera + ' ';  
				if (Ext.getCmp(PF + 'chkPagadora').getValue(true))
					vector.clasChequera = vector.clasChequera + 'X'; 
				else 
					vector.clasChequera = vector.clasChequera + ' '; 
				
				if (Ext.getCmp(PF + 'chkCoinversora').getValue(true)) 
					vector.clasChequera = vector.clasChequera + 'X';  
				else 
					vector.clasChequera = vector.clasChequera + ' ';  
				if (Ext.getCmp(PF + 'chkMixta').getValue(true))
					vector.clasChequera = vector.clasChequera + 'X'; 
				else 
					vector.clasChequera = vector.clasChequera + ' '; 
				 //FIN/////
				if (NS.modificar)
					vector.modificar = 'modificar';
				else
					vector.modificar = 'insertar';

				matriz[0] = vector;

				var jSonString = Ext.util.JSON.encode(matriz);

				if (NS.modificar) {
					Ext.Msg
							.confirm(
									'SET',
									'¿Esta seguro de aplicar los cambios al registro?',
									function(btn) {
										if (btn === 'yes') {
											MantenimientoConceptosAction
													.aceptar(
															jSonString,
															function(resultado,
																	e) {
																if (resultado != 0
																		&& resultado != ''
																		&& resultado != undefined) {
																	Ext.Msg
																			.alert(
																					'SET',
																					resultado);
																	NS.buscar();
																}
															});
										}
									});
				} else {
					MantenimientoConceptosAction.aceptar(jSonString, function(
							resultado, e) {
						if (resultado != 0 && resultado != ''
								&& resultado != undefined) {
							Ext.Msg.alert('SET', resultado);
							NS.buscar();
						}
					});
				}
				NS.limpiaPanelNuevo(true, false);
				NS.panelNuevo.setDisabled(true);
			};

			NS.limpiaPanelNuevo = function(tipo, todo) {
				NS.panelNuevo.setDisabled(tipo);
				Ext.getCmp(PF + 'txtIdBancoNuevo').setValue('');
				Ext.getCmp(PF + 'cmbBancoNuevo').reset();
				Ext.getCmp(PF + 'txtConceptoBancoNuevo').setValue('');
				Ext.getCmp(PF + 'cmbFormaPagoNuevo').reset();
				Ext.getCmp(PF + 'optIngresoEgresoNuevo').setValue(0);
				Ext.getCmp(PF + 'chkDespliega').setValue(false);
				Ext.getCmp(PF + 'chkImporta').setValue(false);
				Ext.getCmp(PF + 'chkCargoCuenta').setValue(false);
				Ext.getCmp(PF + 'chkRechazado').setValue(false);
				/* Se agregagaron H */
				Ext.getCmp(PF + 'chkConcentradora').setValue(false);
				Ext.getCmp(PF + 'chkPagadora').setValue(false);
				Ext.getCmp(PF + 'chkCoinversora').setValue(false);
				Ext.getCmp(PF + 'chkMixta').setValue(false);
				/* Se agregaron H */
				// Ext.getCmp(PF + 'txtIdChequera').setValue('');
				// Ext.getCmp(PF + 'cmbChequeras').reset();
				Ext.getCmp(PF + 'txtClaveConcepto').setValue('');

				Ext.getCmp(PF + 'txtIdBancoNuevo').setDisabled(false);
				Ext.getCmp(PF + 'cmbBancoNuevo').setDisabled(false);
				Ext.getCmp(PF + 'txtConceptoBancoNuevo').setDisabled(false);
				Ext.getCmp(PF + 'cmbFormaPagoNuevo').setDisabled(false);
				Ext.getCmp(PF + 'panelIngresoEgresoNuevo').setDisabled(false);
				Ext.getCmp(PF + 'chkDespliega').setDisabled(false);
				Ext.getCmp(PF + 'chkImporta').setDisabled(false);
				Ext.getCmp(PF + 'chkCargoCuenta').setDisabled(false);
				Ext.getCmp(PF + 'chkRechazado').setDisabled(false);
				// Ext.getCmp(PF + 'txtIdChequera').setDisabled(false);
				// Ext.getCmp(PF + 'cmbChequeras').setDisabled(false);

				if (todo) {
					Ext.getCmp(PF + 'txtIdBancoBusqueda').setValue('');
					Ext.getCmp(PF + 'cmbBancoBusqueda').reset();
					Ext.getCmp(PF + 'txtConceptoBanco').setValue('');
					Ext.getCmp(PF + 'txtIdFormaPago').setValue('');
					Ext.getCmp(PF + 'cmbFormaPago').reset();
					Ext.getCmp(PF + 'optIngresoEgreso').setValue(0);
					Ext.getCmp(PF + 'txtClaveConcepto').setValue('');
					NS.gridDatos.store.removeAll();
					NS.gridDatos.getView().refresh();
				}
				NS.visibleCveConcepto(0);
			};

			NS.eliminaConcepto = function() {
				var registroSeleccionado = NS.gridDatos.getSelectionModel()
						.getSelections();

				if (registroSeleccionado <= 0)
					Ext.Msg.alert('SET',
							'Debe de seleccionar un registro para eliminar');
				else {

					Ext.Msg
							.confirm(
									'SET',
									'¿está seguro de eliminar el concepto: '
											+ registroSeleccionado[0]
													.get('conceptoBanco') + '?',
									function(btn) {
										if (btn === 'yes') {

											// Se elimina el banco
											MantenimientoConceptosAction
													.eliminaConcepto(
															parseInt(registroSeleccionado[0]
																	.get('idBanco')),
															registroSeleccionado[0]
																	.get('conceptoBanco'),
															registroSeleccionado[0]
																	.get('cargoAbono'),
															function(resultado,
																	e) {
																if (resultado > 0) {
																	Ext.Msg
																			.alert(
																					'SET',
																					'EL concepto fue eliminado con exito');
																	NS.buscar();
																} else
																	Ext.Msg
																			.alert(
																					'SET',
																					'Ocurrio un problema durante la eliminación');
															});
										}
									});
				}
			};

			// STORE
			// STORE QUE LLENA EL COMBO BANCOS
			NS.storeBancos = new Ext.data.DirectStore({
				paramAsHash : false,
				root : '',
				baseParams : {},
				paramOrder : [],
				directFn : MantenimientoConceptosAction.llenaBancos,
				idProperty : 'idBanco',
				fields : [ {
					name : 'idBanco'
				}, {
					name : 'descBanco'
				} ],
				listeners : {
					load : function(s, records) {
						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeBancos,
							msg : "Cargando..."
						});

						if (records.length == null || records.length <= 0)
							Ext.Msg.alert('SET', 'No existen Bancos');
					}
				}
			});
			NS.storeBancos.load();

			NS.storeFormaPago = new Ext.data.DirectStore({
				paramAsHash : false,
				root : '',
				baseParams : {
					ingresoEgreso : NS.ingresoEgreso
				},
				paramOrder : [ 'ingresoEgreso' ],
				directFn : MantenimientoConceptosAction.llenaFormaPago,
				idProperty : 'descFormaPago',
				fields : [ {
					name : 'idFormaPago'
				}, {
					name : 'descFormaPago'
				} ],
				listeners : {
					load : function(s, records) {
						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeFormaPago,
							msg : "Cargando..."
						});

						if (records.length == null || records.length <= 0)
							Ext.Msg.alert('SET',
									'No existen formas de pago dadas de alta');

					}
				}
			});
			NS.storeFormaPago.load();
			 
			NS.storeGrid = new Ext.data.DirectStore(
					{
						paramAsHash : false,
						root : '',
						baseParams : {
							idBanco : NS.idBanco,
							conceptoBanco : NS.conceptoBanco,
							idFormaPago : NS.formaPago,
							ingresoEgreso : NS.ingresoEgreso
						},
						paramOrder : [ 'idBanco', 'conceptoBanco',
								'idFormaPago', 'ingresoEgreso' ],
						directFn : MantenimientoConceptosAction.llenaGrid,
						idProperty : 'idConcepto',
						fields : [ {
							name : 'idBanco'
						}, {
							name : 'descBanco'
						}, {
							name : 'cveConcepto'
						}, {
							name : 'conceptoBanco'
						}, {
							name : 'conceptoSet'
						}, {
							name : 'sbc'
						}, {
							name : 'cargoAbono'
						}, {
							name : 'despliega'
						}, {
							name : 'importa'
						}, {
							name : 'clasChequera'
						}, {
							name : 'rechazo'
						}, {
							name : 'cargoCuenta'
						}

						],
						listeners : {
							load : function(s, records) {
								var myMask = new Ext.LoadMask(Ext.getBody(), {
									store : NS.storeGrid,
									msg : "Cargando..."
								});
								if (records.length == null || records.length <= 0) {
									registros = 0;
									Ext.Msg.alert('SET', 'No existen conceptos dados de alta para estos criterios de busqueda');
								}
								else{
									registros = records.length;
								}
							}
						}
					});

			// Store para el llenado del tipo de operacion para su mantenimiento
			NS.storeTipoOperacion = new Ext.data.DirectStore(
					{
						paramAsHash : false,
						root : '',
						baseParams : {},
						paramOrder : [],
						directFn : MantenimientoConceptosAction.llenaTipoOperacion,
						idProperty : 'idTipoOperacion',
						fields : [ {
							name : 'idTipoOperacion'
						}, {
							name : 'descripcion'
						} ],
						listeners : {
							load : function(s, records) {
								var myMask = new Ext.LoadMask(Ext.getBody(), {
									store : NS.storeTipoOperacion,
									msg : "Cargando..."
								});
								if (records.length == null
										|| records.length <= 0)
									Ext.Msg
											.alert('SET',
													'No existen Tipos de Operacion dados de alta');
							}
						}
					});

			// COMBOBOX
			NS.cmbBancoBusqueda = new Ext.form.ComboBox({
				store : NS.storeBancos,
				id : PF + 'cmbBancoBusqueda',
				name : PF + 'cmbBancoBusqueda',
				x : 60,
				y : 15,
				width : 160,
				tabIndex : 1,
				typeAhead : true,
				mode : 'local',
				selectOnFocus : true,
				forceSelection : true,
				valueField : 'idBanco',
				displayField : 'descBanco',
				autocomplete : true,
				emptyText : 'Seleccione un Banco',
				triggerAction : 'all',
				value : '',
				visible : false,
				listeners : {
					select : {
						fn : function(combo, valor) {
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtIdBancoBusqueda', NS.cmbBancoBusqueda
									.getId());
						}
					}
				}
			});

			NS.cmbFormaPago = new Ext.form.ComboBox({
				store : NS.storeFormaPago,
				id : PF + 'cmbFormaPago',
				name : PF + 'cmbFormaPago',
				x : 600,
				y : 15,
				width : 160,
				tabIndex : 5,
				typeAhead : true,
				mode : 'local',
				selectOnFocus : true,
				forceSelection : true,
				valueField : 'descFormaPago',
				displayField : 'descFormaPago',
				autocomplete : true,
				emptyText : 'Forma de pago',
				triggerAction : 'all',
				value : '',
				visible : false,
				listeners : {
					select : {
						fn : function(combo, valor) {
							BFwrk.Util
									.updateComboToTextField(PF
											+ 'txtIdFormaPago', NS.cmbFormaPago
											.getId());
							NS.formaPago = Ext.getCmp(PF + 'cmbFormaPago')
									.getValue();
						}
					}
				}

			});

			// COMBOBOX NUEVO
			NS.cmbBancoNuevo = new Ext.form.ComboBox({
				store : NS.storeBancos,
				id : PF + 'cmbBancoNuevo',
				name : PF + 'cmbBancoNuevo',
				x : 60,
				y : 15,
				width : 160,
				tabIndex : 11,
				typeAhead : true,
				mode : 'local',
				selectOnFocus : true,
				forceSelection : true,
				valueField : 'idBanco',
				displayField : 'descBanco',
				autocomplete : true,
				emptyText : 'Seleccione un Banco',
				triggerAction : 'all',
				value : '',
				visible : false,
				listeners : {
					select : {
						fn : function(combo, valor) {
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtIdBancoNuevo', NS.cmbBancoNuevo
									.getId());
							NS.visibleCveConcepto(combo.getValue());
						}
					}
				}
			});

			NS.cmbFormaPagoNuevo = new Ext.form.ComboBox({
				store : NS.storeFormaPago,
				id : PF + 'cmbFormaPagoNuevo',
				name : PF + 'cmbFormaPagoNuevo',
				x : 790,
				y : 15,
				width : 160,
				tabIndex : 17,
				typeAhead : true,
				mode : 'local',
				selectOnFocus : true,
				forceSelection : true,
				valueField : 'descFormaPago',
				displayField : 'descFormaPago',
				autocomplete : true,
				emptyText : 'Forma de pago',
				triggerAction : 'all',
				value : '',
				visible : false,
				listeners : {
					select : {
						fn : function(combo, valor) {

						}
					}
				}

			});

			// Datos fijos para el combo de Chequeras a Asignar
			NS.datosCombo = [ [ 'C', 'CONCENTRADORA' ], [ 'P', 'PAGADORA' ],
					[ 'CO', 'COINVERSORA' ], [ 'M', 'MIXTA' ], [ 'T', 'TODAS' ] ];

			/*
			 * NS.storeChequeras = new Ext.data.SimpleStore({ idProperty:
			 * 'idChequera', fields: [ {name: 'idChequera'}, {name:
			 * 'descripcion'} ] });
			 */
			// NS.storeChequeras.loadData(NS.datosCombo);
			// COMBOBOX
			// Combo del tipo de Banca Electronica (Se llena de forma manual)
			/*
			 * NS.cmbChequeras = new Ext.form.ComboBox({ store:
			 * NS.storeChequeras, id: PF + 'cmbChequeras', name: PF +
			 * 'cmbChequeras', x: 470, y: 15, width: 150, tabIndex: 14,
			 * typeAhead: true, mode: 'local', selecOnFocus: true,
			 * forceSelection: true, valueField: 'idChequera', displayField:
			 * 'descripcion', autocomplete: true, emptyText: 'Seleccione Tipo
			 * Chequera', triggerAction: 'all', value: '', visible: false,
			 * listeners:{ select: { fn: function (combo, valor) {
			 * BFwrk.Util.updateComboToTextField(PF + 'txtIdChequera',
			 * NS.cmbChequeras.getId()); } } } });
			 */

			// COMBO PARA LA FORMA DE PAGO (tipo de operacion)
			NS.cmbTipoOperacion = new Ext.form.ComboBox({
				store : NS.storeTipoOperacion,
				id : PF + 'cmbTipoOperacion',
				name : PF + 'cmbTipoOperacion',
				x : 260,
				y : 15,
				width : 250,
				typeAhead : true,
				mode : 'local',
				selecOnFocus : true,
				forceSelection : true,
				valueField : 'idTipoOperacion',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione el Tipo de operación',
				triggerAction : 'all',
				value : '',
				visible : false,
				listeners : {
					select : {
						fn : function(combo, valor) {
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtIdTipoOperacion', NS.cmbTipoOperacion
									.getId());
						}
					}
				}

			});

			// GRID
			NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
				singleSelect : false
			});

			NS.columnasGrid = new Ext.grid.ColumnModel([ {
				header : 'Id Banco',
				width : 60,
				dataIndex : 'idBanco',
				sortable : true
			}, {
				header : 'Desc Banco',
				width : 100,
				dataIndex : 'descBanco',
				sortable : true
			}, {
				header : 'Clave Concepto',
				width : 80,
				dataIndex : 'cveConcepto',
				sortable : true
			}, {
				header : 'Concepto Banco',
				width : 160,
				dataIndex : 'conceptoBanco',
				sortable : true
			}, {
				header : 'Concepto Set',
				width : 160,
				dataIndex : 'conceptoSet',
				sortable : true
			}, {
				header : 'SBC',
				width : 60,
				dataIndex : 'sbc',
				sortable : true
			}, {
				header : 'Cargo / Abono',
				width : 100,
				dataIndex : 'cargoAbono',
				sortable : true
			}, {
				header : 'Despliega',
				width : 60,
				dataIndex : 'despliega',
				sortable : true
			}, {
				header : 'Importa',
				width : 60,
				dataIndex : 'importa',
				sortable : true
			}, {
				header : 'Chequeras',
				width : 70,
				dataIndex : 'clasChequera',
				sortable : true
			}, {
				header : 'Rechazado',
				width : 70,
				dataIndex : 'rechazo',
				sortable : true
			}, {
				header : 'F.P. Cargo en Cta',
				width : 100,
				dataIndex : 'cargoCuenta',
				sortable : true
			},

			]);

			NS.gridDatos = new Ext.grid.GridPanel({
				store : NS.storeGrid,
				id : PF + 'gridDatos',
				name : PF + 'gridDatos',
				cm : NS.columnasGrid,
				sm : NS.columnaSeleccion,
				width : 960,
				height : 150,
				stripeRows : true,
				columnsLine : true,
				listeners : {
					click : {
						fn : function(e) {

						}
					}
				}
			});

			// GRID PARA LA FORMA DE PAGO
			NS.columnaSeleccionFP = new Ext.grid.CheckboxSelectionModel({
				singleSelect : false
			});

			NS.columnasGridFP = new Ext.grid.ColumnModel([ {
				header : 'Ingreso/Egreso',
				width : 60,
				dataIndex : 'ingresoEgreso',
				sortable : true
			}, {
				header : 'Id Pago',
				width : 60,
				dataIndex : 'idFormaPago',
				sortable : true
			}, {
				header : 'Descripción',
				width : 70,
				dataIndex : 'descFormaPago',
				sortable : true
			}, {
				header : 'Concepto SET',
				width : 100,
				dataIndex : 'conceptoSet',
				sortable : true
			}, {
				header : 'Tipo Operacion',
				width : 60,
				dataIndex : 'tipoOperacion',
				sortable : true
			}, {
				header : 'Descripción Operacion',
				width : 100,
				dataIndex : 'descripcion',
				sortable : true
			}, {
				header : 'Concepto Personal',
				width : 100,
				dataIndex : 'conceptoPersonal',
				sortable : true
			}, {
				header : 'Salvo Buen Cobro',
				width : 60,
				dataIndex : 'sbc',
				sortable : true
			}, {
				header : 'Id Corresponde',
				width : 60,
				dataIndex : 'idCorresponde',
				sortable : true
			}, {
				header : 'Importa Banca',
				width : 60,
				dataIndex : 'importaBanca',
				sortable : true
			} ]);

			NS.gridDatosFP = new Ext.grid.GridPanel({
				store : NS.storeGridFP,
				id : PF + 'gridDatosFP',
				name : PF + 'gridDatosFP',
				cm : NS.columnasGridFP,
				sm : NS.columnaSeleccionFP,
				width : 940,
				height : 180,
				stripeRows : true,
				columnsLine : true,
				listeners : {
					click : {
						fn : function(e) {

						}
					}
				}
			});
			NS.panelIngresoEgreso = new Ext.form.FieldSet({
				title : '',
				x : 430,
				y : 0,
				width : 150,
				height : 40,
				layout : 'absolute',
				items : [ NS.optIngresoEgreso ]
			});

			NS.panelBusqueda = new Ext.form.FieldSet({
				title : 'busqueda',
				x : 0,
				y : 0,
				width : 985,
				height : 75,
				layout : 'absolute',
				items : [ NS.lblBanco, NS.lblConceptoBanco, NS.lblFormaPago,
						NS.txtIdBancoBusqueda, NS.txtConceptoBanco,
						NS.txtIdFormaPago, NS.cmbBancoBusqueda,
						NS.cmbFormaPago, NS.panelIngresoEgreso, {
							xtype : 'button',
							text : 'Buscar',
							x : 870,
							y : 10,
							width : 80,
							height : 22,
							tabIndex : 6,
							listeners : {
								click : {
									fn : function(e) {
										NS.buscar();
									}
								}
							}
						} ]
			});

			NS.panelGrid = new Ext.form.FieldSet({
				x : 0,
				y : 80,
				width : 985,
				height : 220,
				layout : 'absolute',
				items : [ NS.gridDatos, {
					xtype : 'button',
					text : 'Modificar',
					x : 670,
					y : 170,
					width : 80,
					height : 22,
					tabIndex : 7,
					listeners : {
						click : {
							fn : function(e) {
								NS.modificar = true;
								NS.llenaCampos();
								// NS.panelNuevo.setDisabled(false);
							}
						}
					}
				}, {
					xtype : 'button',
					text : 'Crear Nuevo',
					x : 770,
					y : 170,
					width : 80,
					height : 22,
					tabIndex : 8,
					listeners : {
						click : {
							fn : function(e) {
								NS.modificar = false;
								// Ext.getCmp(PF +
								// 'chkDespliega').setValue(true);
								// Ext.getCmp(PF + 'chkImporta').setValue(true);
								NS.limpiaPanelNuevo(false, false);
							}
						}
					}
				}, {
					xtype : 'button',
					text : 'Eliminar',
					x : 870,
					y : 170,
					width : 80,
					height : 22,
					tabIndex : 9,
					listeners : {
						click : {
							fn : function(e) {
								NS.eliminaConcepto();
							}
						}
					}
				} ]
			});

			NS.panelIngresoEgresoNuevo = new Ext.form.FieldSet({
				id : PF + 'panelIngresoEgresoNuevo',
				name : PF + 'panelIngresoEgresoNuevo',
				title : '',
				x : 600,
				y : 0,
				width : 140,
				height : 40,
				layout : 'absolute',
				items : [ NS.optIngresoEgresoNuevo ]
			});

			NS.panelNuevo = new Ext.form.FieldSet({
				x : 0,
				y : 310,
				width : 985,
				height : 110,
				layout : 'absolute',
				disabled : true,
				items : [ NS.lblBancoNuevo, NS.lblConceptoBancoNuevo,
						NS.lblChequeras, NS.lblFormaPagoNuevo,
						NS.lblClaveConcepto, NS.txtIdBancoNuevo,
						NS.txtConceptoBancoNuevo, NS.txtClaveConcepto/*
																		 * , {
																		 * xtype:
																		 * 'uppertextfield',
																		 * x:
																		 * 410,
																		 * y:
																		 * 15,
																		 * width:
																		 * 50,
																		 * tabIndex:
																		 * 13,
																		 * id:
																		 * PF +
																		 * 'txtIdChequera',
																		 * name:
																		 * PF +
																		 * 'txtIdChequera',
																		 * listeners:{
																		 * change:{
																		 * fn:function(caja){
																		 * if(caja.getValue() !=
																		 * null &&
																		 * caja.getValue() !=
																		 * undefined &&
																		 * caja.getValue() !=
																		 * '') {
																		 * //var
																		 * valueCombo =
																		 * BFwrk.Util.updateTextFieldToCombo(PF +
																		 * 'txtIdChequera',
																		 * NS.cmbChequeras.getId());
																		 * }else
																		 * //NS.cmbChequeras.reset(); } } } }
																		 */, NS.cmbBancoNuevo, NS.cmbFormaPagoNuevo,
						// NS.cmbChequeras,
						{
							xtype : 'checkbox',
							id : PF + 'chkConcentradora',
							name : PF + 'chkConcentradora',
							x : 425,
							y : 15,
							boxLabel : 'CONCENTRADORA',
							listeners : {
								check : {
									fn : function(checkBox, valor) {
									}
								}
							}
						}, {
							xtype : 'checkbox',
							id : PF + 'chkPagadora',
							name : PF + 'chkPagadora',
							x : 425,
							y : 35,
							boxLabel : 'PAGADORA',
							listeners : {
								check : {
									fn : function(checkBox, valor) {
									}
								}
							}
						}, {
							xtype : 'checkbox',
							id : PF + 'chkCoinversora',
							name : PF + 'chkCoinversora',
							x : 425,
							y : 55,
							boxLabel : 'COINVERSORA',

						}, {
							xtype : 'checkbox',
							id : PF + 'chkMixta',
							name : PF + 'chkMixta',
							x : 425,
							y : 71,
							boxLabel : 'MIXTA',

						}, NS.panelIngresoEgresoNuevo, {
							xtype : 'checkbox',
							id : PF + 'chkDespliega',
							name : PF + 'chkDespliega',
							x : 0,
							y : 45,
							boxLabel : 'Despliega',
							tabIndex : 18,
							listeners : {
								check : {
									fn : function(checkBox, valor) {
									}
								}
							}
						}, {
							xtype : 'checkbox',
							id : PF + 'chkImporta',
							name : PF + 'chkImporta',
							x : 0,
							y : 60,
							boxLabel : 'Importa',
							tabIndex : 19,
							listeners : {
								check : {
									fn : function(checkBox, valor) {
									}
								}
							}
						}, {
							xtype : 'checkbox',
							id : PF + 'chkRechazado',
							name : PF + 'chkRechazado',
							x : 100,
							y : 45,
							boxLabel : 'Rechazado',
							tabIndex : 20,
							listeners : {
								check : {
									fn : function(checkBox, valor) {
									}
								}
							}
						}, {
							xtype : 'checkbox',
							id : PF + 'chkCargoCuenta',
							name : PF + 'chkCargoCuenta',
							x : 100,
							y : 60,
							boxLabel : 'Forma Pago Cargo Cta.',
							tabIndex : 21,
							listeners : {
								check : {
									fn : function(checkBox, valor) {
									}
								}
							}
						}, {
							xtype : 'button',
							text : 'Aceptar',
							x : 770,
							y : 60,
							width : 80,
							height : 22,
							tabIndex : 22,
							listeners : {
								click : {
									fn : function(e) {
										NS.aceptarModificar();
										NS.limpiaPanelNuevo(false, false);
									}
								}
							}
						}, {
							xtype : 'button',
							text : 'Cancelar',
							x : 870,
							y : 60,
							width : 80,
							height : 22,
							tabIndex : 23,
							listeners : {
								click : {
									fn : function(e) {
										NS.limpiaPanelNuevo(true, false);
									}
								}
							}
						} ]
			});

			NS.panelIngresoEgresoFP = new Ext.form.FieldSet({
				title : '',
				x : 0,
				y : 0,
				width : 140,
				height : 40,
				layout : 'absolute',
				items : [ NS.optIngresoEgresoFP ]
			});

			NS.panelBusquedaFP = new Ext.form.FieldSet({
				title : '',
				x : 5,
				y : 0,
				width : 955,
				height : 75,
				layout : 'absolute',
				items : [ NS.panelIngresoEgresoFP, NS.lblTipoOperacion,
						NS.txtIdTipoOperacion, NS.cmbTipoOperacion, {
							xtype : 'button',
							text : 'Buscar',
							x : 750,
							y : 20,
							width : 80,
							height : 22,
							listeners : {
								clic : {
									fn : function(e) {

									}
								}
							}
						} ]
			});

			NS.panelGridFP = new Ext.form.FieldSet({
				title : '',
				x : 5,
				y : 85,
				width : 955,
				height : 200,
				layout : 'absolute',
				items : [ NS.gridDatosFP ]
			});

			NS.panelNuevoFP = new Ext.form.FieldSet({
				title : '',
				x : 5,
				y : 295,
				width : 955,
				height : 85,
				layout : 'absolute',
				items : []
			});

			NS.panelFormaPago = new Ext.form.FieldSet({
				title : 'Formas De Pago',
				x : 0,
				y : 0,
				width : 985,
				height : 420,
				layout : 'absolute',
				hidden : true,
				items : [ NS.panelBusquedaFP, NS.panelGridFP, NS.panelNuevoFP ]
			});

			NS.global = new Ext.form.FieldSet({
				title : '',
				x : 20,
				y : 5,
				width : 1010,
				height : 490,
				layout : 'absolute',
				items : [ NS.panelBusqueda, NS.panelGrid, NS.panelNuevo,
				// NS.panelFormaPago,
				{
					xtype : 'button',
					text : 'Formas de Pago',
					x : 670,
					y : 440,
					width : 80,
					height : 22,
					tabIndex : 24,
					hidden : true,
					listeners : {
						click : {
							fn : function(e) {
								NS.panelBusqueda.setVisible(false);
								NS.panelGrid.setVisible(false);
								NS.panelNuevo.setVisible(false);
								NS.storeTipoOperacion.load();
								NS.panelFormaPago.setDisabled(false);
								NS.panelFormaPago.setVisible(true);
							}
						}
					}
				}, {
					xtype : 'button',
					text : 'Imprimir',
					x : 780,
					y : 440,
					width : 80,
					height : 22,
					tabIndex : 25,
					listeners : {
						click : {
							fn : function(e) {
								//SE AGREGO CONDICION PARA VALIDAR SI EXISTEN REGISTROS PARA REALIZAR EL REPORTE////
								/////INICIA////
								if(registros == 0){
									Ext.Msg.alert('SET', 'No Existen Datos para el Reporte');									
								}else{
									//alert(Ext.getCmp(PF + 'cmbBancoBusqueda').getRawValue());
									strParams = '?nomReporte=ReporteConceptos';
									strParams += '&' + 'nomParam1=idBanco';
									strParams += '&' + 'valParam1=' + NS.idBanco;
									strParams += '&' + 'nomParam2=cmbBancoBusqueda';
									strParams += '&' + 'valParam2='+ Ext.getCmp(PF + 'cmbBancoBusqueda').getRawValue();
									//alert(NS.idBanco); 
									//alert(Ext.getCmp(PF + 'cmbBancoBusqueda').getRawValue());
									strParams += '&' + 'nomParam3=conceptoBanco';
									strParams += '&' + 'valParam3=' + NS.conceptoBanco;
									//alert(NS.conceptoBanco);
									strParams += '&' + 'nomParam4=ingresoEgreso';
									strParams += '&' + 'valParam4=' + NS.ingresoEgreso; // Ext.getCmp(PF + 'cmbFormaPago').getRawValue();									;
									strParams += '&' + 'nomParam5=cmbFormaPago';
									strParams += '&' + 'valParam5=' + Ext.getCmp(PF + 'cmbFormaPago').getRawValue();
									//alert(Ext.getCmp(PF + 'cmbFormaPago').getRawValue());
									
									//alert(strParams);
									
									window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);	
									 
								} 
								///FIN/////
							}
						}
					}
				}, {
					xtype : 'button',
					text : 'Limpiar',
					x : 880,
					y : 440,
					width : 80,
					height : 22,
					tabIndex : 26,
					listeners : {
						click : {
							fn : function(e) {
								NS.limpiaPanelNuevo(true, true);
							}
						}
					}
				} ]
			});

			NS.conceptos = new Ext.FormPanel({
				title : 'Mantenimiento De Conceptos De Banca Electronica',
				width : 1300,
				height : 706,
				frame : true,
				padding : 10,
				autoScroll : true,
				layout : 'absolute',
				id : PF + 'conceptos',
				name : PF + 'conceptos',
				renderTo : NS.tabContId,
				items : [ NS.global ]
			});

			NS.conceptos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(
					NS.tabContId).getHeight());
		});