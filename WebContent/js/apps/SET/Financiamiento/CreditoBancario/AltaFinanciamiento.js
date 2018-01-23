/*
@author: GGonzález
Cuando se migre la funcionalidad de Créditos bursátiles, arrendamientos y factoraje, programar los comentarios de las condiciones de vsTipoMenu
Los componentes faltantes están ocultos de acuerdo al tipo de menú
vsTipoMenu="B" BURSÁTILES
vsTipoMenu="A" ARRENDAMIENTOS
vsTipoMenu="F" FACTORAJE
 */
Ext
.onReady(function() {
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext
	.namespace('apps.SET.Finaciamiento.CreditoBancario');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.NOMBRE_HOST = apps.SET.HOST_NAME_LOCAL;
	NS.USR = apps.SET.NOMBRE + '  ' + apps.SET.APELLIDO_PATERNO + '  '
	+ apps.SET.APELLIDO_MATERNO;
	NS.fecHoy = apps.SET.FEC_HOY;
	NS.idUsuario = apps.SET.iUserId;
	NS.iIdBanco = 0;
	NS.sDivisa = '';
	NS.pvTasaBase='';
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
	NS.oD = '1';
	NS.pdArrInteres = new Array();
	NS.pdArrPor = new Array();
	NS.pdArrIdBanco = new Array();
	NS.soloCapital = false;
	var jsonStringRegistros = '';
	var jsonStringCriterios = '';
	var textField = new Ext.form.TextField();
	NS.pbModificar = false;
	NS.pbModificarDisp = false;
	NS.pbConsult = false;
	NS.psTipoFinan = '';
	NS.psNacionalidad = '';
	NS.psDivisa = '';
	NS.bRestringido = false;
	NS.pbTipoContrato = '';
	NS.idFin = '';
	NS.montoDis = 0;
	NS.vdMontoDis = 0;
	NS.idDivisa = '';
	NS.psIdContrato = '';
	NS.pbEstatus = '';
	NS.bancoLin = 0;
	NS.vsMenu = '';
	NS.vsTipoLin = '';
	NS.vsConsecutivo = '';
	NS.val = false;
	NS.vsLinea = '';
	NS.band = false;
	NS.diaInhabil = false;
	NS.valorFecha = '';
	NS.calculoInt = '';
	NS.pagoInt = '';
	NS.pbReestructura = false;
	NS.idCaja = apps.SET.ID_CAJA;
	NS.vsTasaBase = '';
	NS.oPeriodC = '';
	NS.oPeriodI = '';
	NS.vdSaldoInsoluto = 0;
	NS.pbCons = false;
	NS.pdTasa = 0;
	NS.vbExistAmort = false;
	NS.pbBitLinea = false;
	//Variable global para utilizarse en todas las pantallas de vencimientos que indican el cambio de empresa del combo
	NS.noEmpresa = apps.SET.ID_EMPRESA;
	NS.noMEmpresa = apps.SET.NOM_EMPRESA;
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
	NS.sumarDias = function(fecha, dias) {
		var tiempo = fecha.getTime();
		var milisegundos = parseInt(dias * 24 * 60 * 60 * 1000);
		var total = fecha.setTime(tiempo + milisegundos);
		var day = fecha.getDate();
		var month = fecha.getMonth() + 1;
		var year = fecha.getFullYear();
		return (day + "/" + month + "/" + year);
	};
	// carga de store
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
	// load de la pantalla
	// carga de store
	NS.storeConfiguraSetTodos.load({
		callback : function(records) {
			// If vsTipoMenu = "B" Then Call Bursatil
			// If vsTipoMenu = "F" Then Call Factoraje
			// If vsTipoMenu = "A" Then Call Arrendamientos
			NS.proyecto = records[0].get('valorConfiguraSet');
			if (NS.proyecto == 'CIE') {
				NS.cmdRenovacion.setVisible(false);
				NS.lblBanco.setVisible(false);
				NS.txtBanco.setVisible(false);
				NS.cmbBanco.setVisible(false);
				NS.lblNoCuenta.setVisible(false);
				NS.cmbNoCuenta.setVisible(false);
				Ext.getCmp('fraCalculoInt').hide();
				Ext.getCmp('fraPagoInt').hide();
				NS.optCalculoInt.setDisabled(true);
				// If vsTipoMenu <> "A" Then
				NS.lblDivisaD.setText("Divisa Disposición");
				// End If
				// If vsTipoMenu <> "F" And vsTipoMenu <> "A" Then
				NS.lblDivisaD.setVisible(false);
				// End If
				// If vsTipoMenu <> "F" And vsTipoMenu <> "A" Then
				NS.lblDivisaD.setVisible(false);
				NS.txtDivisaD.setVisible(false);
				NS.cmbDivisaD.setVisible(false);
				// End If
				// If vsTipoMenu <> "A" Then
				NS.lblMontoMora.setVisible(true);
				NS.txtMontoMora.setVisible(true);
				NS.lblTasaMora.setVisible(true);
				NS.txtTasaMora.setVisible(true);
				// End If
				// If vsTipoMenu = "" Or vsTipoMenu = "B" Then
				NS.lblReestructura.setVisible(true);
				NS.chkReestructura.setVisible(true);
				// End If
				// If vsTipoMenu = "F" Or vsTipoMenu = "A" Then Call
				// FactoArre
				// Llamada a LlenaCombosCred()
				// If vsTipoMenu = "B" Then Call
				// gobjVarGlobal.LlenaComboRst(cmbComun,
				// gobjSQL.FunSQLSelectArrendadoras(vsTipoMenu),
				// matComun)
			}
		}
	});
	NS.storeComboClabe = new Ext.data.DirectStore({
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
	NS.storeSelectInhabil = new Ext.data.DirectStore({
		paramsAsHash : false,
		storeId : 'store1',
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
	NS.storeFunSelectExisteAmort = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psLinea : '',
			psCredito : ''
		},
		paramOrder : [ 'psLinea', 'psCredito' ],
		directFn : AltaFinanciamientoAction.selectExisteAmort,
		idProperty : 'idAmortizacion',
		fields : [ {
			name : 'idAmortizacion'
		}, {
			name : 'idContrato'
		}, {
			name : 'idDisposicion'
		} ]
	});
	// Empresa
	NS.lblEmpresa = new Ext.form.Label({
		text : 'Empresa:',
		x : 70,
		y : 3
	});
	NS.txtEmpresa = new Ext.form.TextField({
		id : PF + 'txtEmpresa',
		name : PF + 'txtEmpresa',
		value : NS.GI_ID_EMPRESA,
		x : 130,
		y : 0,
		width : 70,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util
					.updateTextFieldToCombo(PF+ 'txtEmpresa',NS.cmbEmpresa.getId());
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
		directFn : AltaFinanciamientoAction.obtenerEmpresas,
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
				x : 205,
				y : 0,
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
							NS.cmbContratos.reset();
							NS.storeCmbContratos.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
							NS.storeCmbContratos.load();

							NS.storeCmbBanco.removeAll();
							NS.cmbBanco.reset();
							var myMask = new Ext.LoadMask(Ext.getBody(), {
								store : NS.storeCmbBanco,
								msg : "Cargando..."
							});
							NS.storeCmbBanco.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
							NS.storeCmbBanco.load();

							NS.storeCmbFideicomiso.removeAll();
							NS.cmbFideicomiso.reset();
							var myMask = new Ext.LoadMask(Ext.getBody(), {
								store : NS.storeCmbFideicomiso,
								msg : "Cargando..."
							});
							NS.storeCmbFideicomiso.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
							NS.storeCmbFideicomiso.load();
							NS.noEmpresa = NS.txtEmpresa.getValue();
						}
					}
				}
			});
	// Usuario
	NS.lblUsuario = new Ext.form.Label({
		text : 'Usuario:',
		x : 540,
		y : 3
	});
	NS.txtUsuario = new Ext.form.TextField({
		id : PF + 'txtUsuario',
		name : PF + 'txtUsuario',
		value : NS.USR,
		x : 590,
		y : 0,
		readOnly : true,
		width : 300
	});
	// Línea
	NS.lblLinea = new Ext.form.Label({
		text : 'Línea:',
		x : 0,
		y : -1
	});
	NS.txtLinea = new Ext.form.TextField(
			{
				id : PF + 'txtLinea',
				name : PF + 'txtLinea',
				value : '',
				x : 0,
				y : 13,
				width : 140,
				disabled : true,
				listeners : {
					change : {
						fn : function(caja, valor) {
							caja.setValue(valor.toUpperCase());
						}
					}
				},
				regex : /^[A-Za-z]{4}[0-9]{5}$/,
				regexText : 'Formato de la clave del contrato incorrecto. Verifíque por favor XXXX00000.',
			});
	// chkLargoPlazo
	NS.chkLargoPlazo = new Ext.form.Checkbox({
		name : PF + 'chkLargoPlazo',
		id : 'chkLargoPlazo',
		x : 160,
		y : 13,
		disabled : true
	});
	NS.lblLargoPlazo1 = new Ext.form.Label({
		text : 'Largo',
		x : 175,
		y : 11
	});
	NS.lblLargoPlazo2 = new Ext.form.Label({
		text : ' Plazo',
		x : 175,
		y : 21
	});
	NS.muestraNuevosCampos = function(valor) {
		NS.lblFideicomiso.setVisible(valor);
		NS.txtFideicomiso.setVisible(valor);
		NS.cmbFideicomiso.setVisible(valor);
		NS.lblAgente.setVisible(valor);
		NS.txtAgente.setVisible(valor);
		NS.cmbAgente.setVisible(valor);
	};
	// chkReestructura
	NS.chkReestructura = new Ext.form.Checkbox({
		name : PF + 'chkReestructura',
		id : 'chkReestructura',
		x : 210,
		y : 13,
		disabled : true,
		hidden : true,
		value : false,
		listeners : {
			inputValue : 'true',
			uncheckedValue : 'false',
			check : {
				fn : function(ch, checked) {
					if (checked) {
						if (NS.proyecto == 'CIE') {// And (vsTipoMenu =
							// "B" Or vsTipoMenu
							// = "") And
							// chkReestructura.Value
							// = 1 Then
							NS.muestraNuevosCampos(true);
							NS.txtFideicomiso.setValue("");
							NS.cmbFideicomiso.reset();
							NS.txtAgente.setValue("");
							NS.cmbAgente.reset();
							var myMask = new Ext.LoadMask(
									Ext.getBody(), {
										store : NS.storeCmbFideicomiso,
										msg : "Cargando..."
									});
							NS.storeCmbFideicomiso.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
							NS.storeCmbFideicomiso.load();
							var myMask = new Ext.LoadMask(
									Ext.getBody(), {
										store : NS.storeCmbBancoAgente,
										msg : "Cargando..."
									});
							NS.storeCmbBancoAgente.load();
						} else {
							NS.muestraNuevosCampos(false);
							NS.txtFideicomiso.setValue("");
							NS.cmbFideicomiso.reset();
							NS.txtAgente.setValue("");
							NS.cmbAgente.reset();
						}
					} else {
						NS.txtFideicomiso.setValue("");
						NS.txtAgente.setValue("");
						NS.cmbFideicomiso.reset();
						NS.cmbAgente.reset();
						NS.muestraNuevosCampos(false)
					}
				}
			}
		}
	});
	NS.lblReestructura = new Ext.form.Label({
		text : 'Reestructura',
		x : 224,
		hidden : true,
		y : 16
	});
	NS.storeCmbEquivalencia = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psDesBanco : '',
			piBanco : 0
		},
		paramOrder : [ 'psDesBanco', 'piBanco' ],
		directFn : AltaFinanciamientoAction.obtenerEquivalencia,
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
	NS.storeTasa = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psTasa : ''
		},
		paramOrder : [ 'psTasa' ],
		directFn : AltaFinanciamientoAction.funSQLTasa,
		idProperty : 'valor',
		fields : [ {
			name : 'valor'
		}, ]
	});
	// combo linea
	NS.storeTipoCambio = new Ext.data.DirectStore(
			{
				paramsAsHash : false,
				root : '',
				baseParams : {
					idDivisa : ''
				},
				paramOrder : [ 'idDivisa' ],
				directFn : AltaFinanciamientoAction.obtenerTipoCambio,
				idProperty : 'idDivisa',
				fields : [ {
					name : 'idDivisa'
				}, {
					name : 'fecDivisa'
				}, {
					name : 'valor'
				} ],
				listeners : {
					load : function(s, records) {
						if (records.length == null
								|| records.length <= 0) {
							Ext.Msg
							.alert('SET',
							'El valor de la divisa no está alimentado.');
							NS.txtValorizacion.setValue("0.00");
						} else {
							if (records[0].get('valor') < 1) {
								if (records[0].get('valor') == 0) {
									NS.txtValorizacion.setValue("0.00");
								} else {
									NS.txtValorizacion
									.setValue(BFwrk.Util
											.formatNumber(parseFloat(BFwrk.Util
													.unformatNumber(NS.txtMontoDisponible
															.getValue()))
															/ parseFloat(records[0]
															.get('valor'))));
								}
							}
							if (records[0].get('valor') >= 1) {
								NS.txtValorizacion
								.setValue(BFwrk.Util
										.formatNumber(parseFloat(BFwrk.Util
												.unformatNumber(NS.txtMontoDisponible
														.getValue()))
														* parseFloat(records[0]
														.get('valor'))));
							}
						}
					}
				}
			});
	NS.lblLinea2 = new Ext.form.Label({
		text : 'Línea:',
		x : 700,
		y : -1
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
		} ]
	});
	NS.storeCmbEmpresa.load();
	NS.storeCmbContratos = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {noEmpresa:''},
		paramOrder : ['noEmpresa'],
		directFn : AltaFinanciamientoAction.obtenerContratos,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ]
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbContratos,
		msg : "Cargando..."
	});
	NS.storeCmbContratos.baseParams.noEmpresa=parseInt(NS.GI_ID_EMPRESA);
	NS.storeCmbContratos.load();
	NS.storeDisposiciones = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psIdContrato : '',
			pbEstatus : ''
		},
		paramOrder : [ 'psIdContrato', 'pbEstatus' ],
		directFn : AltaFinanciamientoAction.obtenerDisposiciones,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
	});
	NS.storeSelectDisp = new Ext.data.DirectStore(
			{
				paramsAsHash : false,
				root : '',
				baseParams : {
					psIdContrato : '',
					psIdDisp : 0
				},
				paramOrder : [ 'psIdContrato', 'psIdDisp' ],
				directFn : AltaFinanciamientoAction.selectDisp,
				fields : [ {
					name : 'idFinanciamiento'
				}, {
					name : 'idDisposicion'
				}, {
					name : 'emision'
				}, {
					name : 'idDivisa'
				}, {
					name : 'montoDisposicion'
				}, {
					name : 'fecDisposicion'
				}, {
					name : 'fecVencimiento'
				}, {
					name : 'sobreTasa'
				}, {
					name : 'aforoPorciento'
				}, {
					name : 'aforoImporte'
				}, {
					name : 'largoPlazo'
				}, {
					name : 'calificadoraSp'
				}, {
					name : 'calificadoraMoody'
				}, {
					name : 'calificadoraFitch'
				}, {
					name : 'tipoTasa'
				}, {
					name : 'tasaBase'
				}, {
					name : 'valorTasa'
				}, {
					name : 'puntos'
				}, {
					name : 'tasaPonderada'
				}, {
					name : 'montoPostura'
				}, {
					name : 'sobreVenta'
				}, {
					name : 'sobreTasacb'
				}, {
					name : 'formaPago'
				}, {
					name : 'estatus'
				}, {
					name : 'idBancoDisp'
				}, {
					name : 'idChequeraDisp'
				}, {
					name : 'idTipoFinanciamiento'
				}, {
					name : 'equivalente'
				}, {
					name : 'renta'
				}, {
					name : 'idDisposicionRef'
				}, {
					name : 'anexo'
				}, {
					name : 'comentarios'
				}, {
					name : 'periodoRenta'
				}, {
					name : 'whTax'
				}, {
					name : 'rentaDep'
				}, {
					name : 'comisionApertura'
				}, {
					name : 'montoMoratorio'
				}, {
					name : 'tasaMoratoria'
				}, {
					name : 'valorFacturas'
				} ],
				listeners : {
					load : function(s, records) {
						if (records.length > 0) {
							NS.pbConsult = true;
							NS.editarDisposicion(false);
							NS.cmdDisposicion.setDisabled(true);
							NS.cmdAmortizaciones.setDisabled(false);
							NS.cmdRentas.setDisabled(false);
							NS.cmdAvalGarantia.setDisabled(false);
							NS.cmdBitacoraDisp.setDisabled(false);
							NS.cmdEliminar1.setDisabled(false);
							NS.cmdImprimir1.setDisabled(false);
							if (NS.proyecto == 'CIE') {
								NS.txtTipoFinanDis.focus();
								NS.cmdRenovacion.setDisabled(false);
							}
							NS.cmdAceptarDis.setDisabled(true);
							NS.cmdDetalle.setDisabled(false);
							NS.cmbEquivalencia.reset();
							var myMask = new Ext.LoadMask(
									Ext.getBody(),
									{
										store : NS.storeCmbEquivalencia,
										msg : "Cargando..."
									});
							NS.storeCmbEquivalencia
							.load({
								params : {
									piBanco : NS.txtBancoLin
									.getValue()
								},
								callback : function() {
									NS.cmbFinanciamiento
									.setValue(records[0]
									.get('idFinanciamiento'));
									NS.storeCmbTipoFinanDis.load();
									NS.txtTipoFinanDis
									.setValue(records[0]
									.get('idTipoFinanciamiento'));
									NS.cmbTipoFinanDis
									.setValue(records[0]
									.get('idTipoFinanciamiento'));
									NS.txtEmision
									.setValue(records[0]
									.get('emision'));
									NS.txtDivisaD
									.setValue(records[0]
									.get('idDivisa'));
									NS.cmbDivisaD
									.setValue(records[0]
									.get('idDivisa'));
									NS.txtMontoDisposicion
									.setValue(BFwrk.Util
											.formatNumber(records[0]
											.get('montoDisposicion')));
									NS.dMontoDispOrig = records[0]
									.get('montoDisposicion');
									NS.txtFecDisp
									.setValue(records[0]
									.get('fecDisposicion'));
									NS.txtDias
									.setValue(NS
											.restaFechas2(
													records[0]
													.get('fecDisposicion'),
													records[0]
													.get('fecVencimiento')));
									NS.txtFecVenDisp
									.setValue(records[0]
									.get('fecVencimiento'));
									NS.txtSobreTasa
									.setValue(records[0]
									.get('sobreTasa'));
									NS.txtAforoPorciento
									.setValue(records[0]
									.get('aforoPorciento'));
									NS.txtAforoImporte
									.setValue(records[0]
									.get('aforoImporte'));
									NS.txtAnexo
									.setValue(records[0]
									.get(
											'anexo')
											.trim());
									NS.txtComentarios
									.setValue(records[0]
									.get(
											'comentarios')
											.trim());
									NS.txtWhtax
									.setValue(records[0]
									.get('whTax'));
									NS.txtMontoMora
									.setValue(records[0]
									.get('montoMoratorio'));
									NS.txtTasaMora
									.setValue(records[0]
									.get('tasaMoratoria'));
									NS.cmbPeriodoRenta
									.setValue(records[0]
									.get('periodoRenta'));
									NS.txtValorFacturas
									.setValue(records[0]
									.get('valorFacturas'));
									if (records[0]
									.get('formaPago') == '3')
										NS.optFormaPago
										.setValue(0);
									if (records[0]
									.get('formaPago') == '5')
										NS.optFormaPago
										.setValue(1);
									NS.cmbEquivalencia
									.setValue(records[0]
									.get('equivalente'));
									BFwrk.Util
									.updateComboToTextField(
											PF
											+ 'txtEquivalencia',
											NS.cmbEquivalencia
											.getId());
									NS.cmbSP
									.setValue(records[0]
									.get('calificadoraSp'));
									NS.cmbMoody
									.setValue(records[0]
									.get('calificadoraMoody'));
									NS.cmbFitch
									.setValue(records[0]
									.get('calificadoraFitch'));
									if (records[0]
									.get('tipoTasa') == 'F') {
										NS.optTasa.setValue(0);
										NS.txtTasaFija
										.setValue(BFwrk.Util
												.formatNumber(records[0]
												.get('valorTasa')));
										NS.cmbTasaBase.reset();
										NS.txtBase
										.setValue("0.000000");
										NS.txtPuntos
										.setValue(BFwrk.Util
												.formatNumber(parseFloat(records[0]
												.get('puntos')).toFixed(6)));
										NS.txtTasaVigente
										.setValue(BFwrk.Util
												.formatNumber(parseFloat(records[0]
										.get('puntos')+parseFloat(records[0]
										.get('valorTasa'))).toFixed(6)));
										NS.txtPuntos.setDisabled(true);
										NS.txtTasaFija.setDisabled(true);
									} else {
										NS.optTasa.setValue(1);
										NS.cmbTasaBase
										.setValue(records[0]
										.get('tasaBase'));
										NS.pvTasaBase = records[0]
										.get('tasaBase');
										NS.txtBase
										.setValue(BFwrk.Util
												.formatNumber((parseFloat(records[0]
												.get('valorTasa'))
												- parseFloat(records[0]
												.get('puntos'))).toFixed(5)));
										NS.txtTasaVigente
										.setValue(BFwrk.Util
												.formatNumber(parseFloat(BFwrk.Util
														.unformatNumber(NS.txtBase
																.getValue()))
																+ parseFloat(records[0]
																.get('puntos'))));
										NS.txtTasaFija
										.setValue("0.000000");
										NS.storeCmbTasa
										.load({
											callback : function(
													records,
													operation,
													success) {
												for (var i = 0; i <= records.length - 1; i++) {
													if (NS.cmbTasa
															.getValue() == records[i]
													.get('idStr')) {
														NS.cmbTasaBase
														.setValue(records[i]
														.get('idStr'));
														NS.cmbTasaBase
														.setRawValue(records[i]
														.get('descripcion'))
													}
												}
												NS.storeTasa.load({
													params : {
														psTasa : NS.cmbTasaBase.getValue()
													},
													callback : function(records,
															operation, success) {
														if (records.length > 0) {
															NS.txtBase.setValue(BFwrk.Util.formatNumber(records[0].get('valor')));
															NS.txtTasaVigente.setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(NS.txtPuntos.getValue()))+ (parseFloat(records[0].get('valor')))));
														} else {
															NS.txtBase.setValue("0.00000");
															NS.txtTasaVigente.setValue("0.000000");
															Ext.Msg.alert('SET','La tasa no ha sido alimentada.');
														}
													}
												});
											}
										});
									}
									NS.txtPuntos
									.setValue(BFwrk.Util
											.formatNumber(records[0]
											.get('puntos')));
									NS.txtTasaPonderada
									.setValue(records[0]
									.get('tasaPonderada'));
									NS.txtTasaPostura
									.setValue(records[0]
									.get('montoPostura'));
									NS.txtSobreVenta
									.setValue(records[0]
									.get('sobreVenta'));
									NS.txtSobreTasaCB
									.setValue(records[0]
									.get('sobreTasacb'));
									NS.txtRenta
									.setValue(records[0]
									.get('renta'));
									NS.txtRentaDep
									.setValue(records[0]
									.get('rentaDep'));
									NS.txtBancoDis
									.setValue(records[0]
									.get('idBancoDisp'));
									NS.cmbBancoDis
									.setValue(records[0]
									.get('idBancoDisp'));
									NS.cmbChequeraDis
									.setValue(records[0]
									.get('idChequeraDisp'));
									NS.cmbDivisaD
									.setValue(NS.cmbDivisa
											.getValue());
									NS.txtDivisaD
									.setValue(NS.cmbDivisa
											.getValue());
									if (records[0]
									.get('comisionApertura') == '0')
										NS.chkComision
										.setValue(false);
									if (records[0]
									.get('comisionApertura') == '1') {
										NS.chkComision
										.setValue(true);
										NS.storeBuscaComisiones
										.load({
											params : {
												psLinea : NS.txtLinea
												.getValue(),
												piDisp : parseInt(NS.cmbDisp
														.getValue())
											},
											callback : function(
													records,
													operation,
													success) {
												if (records.length <= 0)
													Ext.Msg
													.alert(
															'SET',
															'No existen comisiones registradas para esta disposición.');
												NS
												.editarDisposicion(true);
												NS.cmdModificar1
												.setDisabled(false);
												NS.cmbTasaBase
												.setDisabled(true);
												NS.txtPuntos
												.setDisabled(true);
											}
										});
									}
								}
							});
						}
						// //end with
						NS.editarDisposicion(true);
						NS.cmdModificar1.setDisabled(false);
						NS.cmbTasaBase.setDisabled(true);
						NS.txtPuntos.setDisabled(true);
					}
				}
			});
	NS.storeDatos = new Ext.data.DirectStore(
			{
				paramsAsHash : false,
				root : '',
				baseParams : {
					clave : ''
				},
				paramOrder : [ 'clave' ],
				directFn : AltaFinanciamientoAction.obtenerContratoCredito,

				fields : [ {
					name : 'idFinanciamiento'
				}, {
					name : 'idPais'
				}, {
					name : 'idBanco'
				}, {
					name : 'idClabe'
				}, {
					name : 'montoDisposicion'
				}, {
					name : 'capital'
				}, {
					name : 'revolvencia'
				}, {
					name : 'idBancoPrestamo'
				}, {
					name : 'idtipoFinanciamiento'
				}, {
					name : 'idRepresentante'
				}, {
					name : 'fecInicio'
				}, {
					name : 'fecVencimiento'
				}, {
					name : 'idDivisa'
				}, {
					name : 'montoAutorizado'
				}, {
					name : 'tasaLinea'
				}, {
					name : 'spreed'
				}, {
					name : 'reestructura'
				}, {
					name : 'largoPlazo'
				}, {
					name : 'fechaAntpost'
				}, {
					name : 'recfechaAntpost'
				}, {
					name : 'idBancoFideicomiso'
				}, {
					name : 'agente'
				} ],
				listeners : {
					load : function(s, records) {
						if (records.length <= 0) {
							Ext.Msg.alert('SET',
									'El Contrato No Existe.');
						} else {
							NS.vdMontoDis = 0;
							for (var i = 0; i <= records.length - 1; i++) {
								var capital = records[i].get('capital');
								var montoDisposicion = records[i]
								.get('montoDisposicion');
								if (parseFloat(capital) > parseFloat(montoDisposicion)) {
									NS.vdMontoDis = NS.vdMontoDis + 0;
								} else {
									NS.vdMontoDis = NS.vdMontoDis
									+ (records[i]
									.get('montoDisposicion') - records[i]
									.get('capital'));
								}
							}
							NS.cancelar();
							NS.limpiaCredito();
							NS.pbModificar = false;
							if (records[0].get('revolvencia') == "S") {
								NS.chkRevolvencia.setValue(1);
							}
							if (records[0].get('revolvencia') == "N") {
								NS.chkRevolvencia.setValue(0);
							}
							if (NS.chkRevolvencia.getValue() == false) {
								NS.storeNoDisp.baseParams.idFin = records[0]
								.get('idFinanciamiento');
								var myMask = new Ext.LoadMask(Ext
										.getBody(), {
									store : NS.storeNoDisp,
									msg : "Cargando..."
								});
								NS.storeNoDisp.load();
								NS.vdMontoDis = BFwrk.Util
								.unformatNumber(NS.txtMontoDispuesto
										.getValue());
							}
							NS.txtLinea.setValue(records[0]
							.get('idFinanciamiento'));
							NS.cmbPais.setValue(records[0]
							.get('idPais'));
							if (NS.proyecto != 'CIE') {
								NS.txtBanco.setValue(records[0]
								.get('idBanco'));
								NS.cmbBanco.setValue(records[0]
								.get('idBanco'));
								NS.cmbNoCuenta.setValue(records[0]
								.get('idClabe'));
							}
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtNoPais', NS.cmbPais.getId());
							NS.cmbBancoLin.setValue(records[0]
							.get('idBancoPrestamo'));
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtBancoLin', NS.cmbBancoLin
									.getId());
							NS.cmbTipoContrato.setValue(records[0]
							.get('idtipoFinanciamiento'));
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtTipoContrato',
									NS.cmbTipoContrato.getId());
							NS.cmbComun.setValue(records[0]
							.get('idRepresentante'));
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtComun', NS.cmbComun.getId());
							Ext.getCmp(PF + 'txtFechaIni').setValue(
									records[0].get('fecInicio'));
							var fecha = records[0].get('fecVencimiento');
							var varfec = fecha.split("-");
							var fecVencimiento = new Date(parseInt(varfec[0], 10), parseInt(
									varfec[1], 10) - 1, parseInt(varfec[2], 10));

							Ext.getCmp(PF + 'txtFechaFin').setValue(fecVencimiento);
							NS.cmbDivisa.setValue(records[0]
							.get('idDivisa'));
							BFwrk.Util
							.updateComboToTextField(PF
									+ 'txtDivisa', NS.cmbDivisa
									.getId());
							NS.txtMontoAutorizado.setValue(BFwrk.Util
									.formatNumber(records[0]
									.get('montoAutorizado')));
							NS.txtMontoDispuesto.setValue(BFwrk.Util
									.formatNumber(NS.vdMontoDis));
							NS.txtMontoDisponible
							.setValue(BFwrk.Util
									.formatNumber(parseFloat(records[0]
									.get('montoAutorizado'))
									- parseFloat(NS.vdMontoDis)));
							if (NS.txtDivisa.getValue() != "MN") {
								NS.storeTipoCambio.baseParams.idDivisa = NS.txtDivisa
								.getValue();
								var myMask = new Ext.LoadMask(Ext
										.getBody(), {
									store : NS.storeTipoCambio,
									msg : "Cargando..."
								});
								NS.storeTipoCambio.load();
							} else {
								NS.txtValorizacion
								.setValue(NS.txtMontoDisponible
										.getValue());
							}
							if (records[0].get('tasaLinea') != "") {
								var tasaLinea = records[0]
								.get('tasaLinea');
								NS.storeCmbTasa
								.load({
									callback : function(
											records, operation,
											success) {
										for (var i = 0; i <= records.length - 1; i++) {
											if (tasaLinea
													.trim() == records[i]
											.get('idStr')) {
												NS.cmbTasa
												.setValue(records[i]
												.get('idStr'));
												NS.cmbTasa
												.setRawValue(records[i]
												.get('descripcion'))
											}
										}
									}
								});
							}
							NS.txtSpreed.setValue(BFwrk.Util
									.formatNumber(parseFloat(records[0]
									.get('spreed'))));
							NS.chkReestructura.setValue(records[0]
							.get('reestructura'));
							Ext.getCmp('fraCalculoInt').show();
							Ext.getCmp('fraPagoInt').show();
							if (NS.chkReestructura.getValue() == true) {
								var myMask = new Ext.LoadMask(Ext
										.getBody(), {
									store : NS.storeCmbFideicomiso,
									msg : "Cargando..."
								});
								NS.storeCmbFideicomiso.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
								NS.storeCmbFideicomiso.load();
								var myMask = new Ext.LoadMask(Ext
										.getBody(), {
									store : NS.storeCmbBancoAgente,
									msg : "Cargando..."
								});
								NS.storeCmbBancoAgente.load();
								NS.cmbFideicomiso.setValue(records[0]
								.get('idBancoFideicomiso'));
								BFwrk.Util.updateComboToTextField(PF
										+ 'txtFideicomiso',
										NS.cmbFideicomiso.getId());
								NS.cmbAgente.setValue(records[0]
								.get('agente'));
								BFwrk.Util.updateComboToTextField(PF
										+ 'txtAgente', NS.cmbAgente
										.getId());
							}
							NS.chkLargoPlazo.setValue(0);
							if (records[0].get('largoPlazo') == "S")
								NS.chkLargoPlazo.setValue(1);
							if (records[0].get('fechaAntpost') == "E")
								NS.optCalculoInt.setValue(0);
							if (records[0].get('fechaAntpost') == "A")
								NS.optCalculoInt.setValue(1);
							if (records[0].get('fechaAntpost') == "P")
								NS.optCalculoInt.setValue(2);
							if (records[0].get('recfechaAntpost') == "A")
								NS.optPagoInt.setValue(0);
							if (records[0].get('recfechaAntpost') == "P")
								NS.optPagoInt.setValue(1);
							NS.cmdBitacora.setDisabled(false);
							NS.cmdCrearNuevo.setDisabled(true);
							NS.cmbDisp.setDisabled(false);
							NS.cmdDisposicion.setDisabled(false);
							NS.cmdHacer.setDisabled(false);
							NS.cmdImprimir.setDisabled(false);
							NS.cmdModificar.setDisabled(false);
							NS.cmdCreditoSindicado.setDisabled(false);
							NS.cmdEliminar.setDisabled(false);
							NS.cmbDisp.reset();
							NS.storeDisposiciones.baseParams.psIdContrato = NS.txtLinea
							.getValue();
							NS.storeDisposiciones.baseParams.pbEstatus = true;
							var myMask = new Ext.LoadMask(
									Ext.getBody(), {
										store : NS.storeDisposiciones,
										msg : "Cargando..."
									});
							NS.storeDisposiciones.load();
							NS.editarAltaContrato(true);
						}
					}
				}
			});
	NS.storeNoDisp = new Ext.data.DirectStore(
			{
				paramsAsHash : false,
				root : '',
				baseParams : {
					idFin : ''
				},
				paramOrder : [ 'idFin' ],
				directFn : AltaFinanciamientoAction.obtenerNoDisp,
				fields : [ {
					name : 'idDisposicion'
				}, {
					name : 'montoDisposicion'
				} ],
				listeners : {
					load : function(s, records) {
						if (records.lenght < 0) {
							NS.montoDis = records[0]
							.get('montoDisposicion');
							// Ext.getCmp(PF +
							// 'txtMontoDispuesto').setValue(
							// NS.fecHoy);
							NS.txtMontoDisponible
							.setValue(BFwrk.Util
									.formatNumber(1000 - parseFloat(NS.montoDis)));
							NS.txtMontoDispuesto
							.setValue(BFwrk.Util
									.formatNumber(parseFloat(NS.montoDis)));
						}
					}
				}
			});
	NS.cmbContratos = new Ext.form.ComboBox({
		store : NS.storeCmbContratos,
		name : PF + 'cmbContratos',
		id : PF + 'cmbContratos',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 700,
		y : 13,
		width : 140,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una línea',
		triggerAction : 'all',
		listeners : {
			select : {
				fn : function(combo, valor) {
					NS.optCalculoInt.setDisabled(true);
					if (combo.getSize == 0) {
						return;
					}
					NS.storeDatos.baseParams.clave = combo.getValue();
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storeDatos,
						msg : "Cargando datos..."
					});
					NS.storeDatos.load();
				}
			}
		}
	});
	NS.limpiaCredito = function() {
		NS.txtLinea.setValue('')
		Ext.getCmp(PF + 'txtFechaIni').setValue(NS.fecHoy);
		Ext.getCmp(PF + 'txtFechaFin').setValue(NS.fecHoy);
		NS.txtMontoAutorizado.setValue("0.00");
		NS.txtMontoDispuesto.setValue("0.00");
		NS.txtMontoDisponible.setValue("0.00");
		NS.txtValorizacion.setValue("0.00");
		NS.txtSpreed.setValue("0.00");
		NS.txtComun.setValue('');
		NS.txtBanco.setValue('');
		NS.txtBancoLin.setValue('');
		NS.txtNoPais.setValue('');
		NS.txtDivisa.setValue('');
		NS.cmbComun.reset();
		NS.cmbPais.reset();
		NS.cmbBanco.reset();
		NS.cmbTipoContrato.reset();
		NS.cmbDivisa.reset();
		NS.cmbTasa.reset();
		NS.cmbBancoLin.reset();
		NS.chkRevolvencia.setValue(1);
		NS.chkLargoPlazo.setValue(0);
		Ext.getCmp('chkReestructura').setValue(false);
		Ext.getCmp('fraCalculoInt').show();
		NS.optCalculoInt.setVisible(true);
		Ext.getCmp('fraPagoInt').show();
		NS.optPagoInt.setVisible(true);
		NS.optCalculoInt.setDisabled(true);
		/*
		 * If vsTipoMenu = "F" Then optConRec.Value = True
		 * optSinRec.Value = False optAnticipado.Value = False End If
		 */
		if (NS.proyecto == "CIE") {
			NS.txtFideicomiso.setValue('');
			NS.cmbFideicomiso.reset();
			NS.txtAgente.setValue('');
			NS.cmbAgente.reset();
		}
	};
	NS.editarAltaContrato = function(valor) {
		NS.cmbContratos.setDisabled(!valor);
		NS.cmdCrearNuevo.setDisabled(!valor);
		NS.cmdDisposicion.setDisabled(!valor);
		NS.cmdAceptar.setDisabled(valor);
		NS.txtNoPais.setDisabled(valor);
		NS.txtBanco.setDisabled(valor);
		NS.txtBancoLin.setDisabled(valor);
		NS.txtTipoContrato.setDisabled(valor);
		if (NS.proyecto == 'CIE')
			NS.txtLinea.setDisabled(valor);
		// If vsTipoMenu = "B" Then
		// NS.txtComun.setDisabled(valor);
		// NS.cmbComun.setDisabled(valor);
		// End If
		NS.txtFechaIni.setDisabled(valor);
		NS.txtFechaFin.setDisabled(valor);
		NS.txtDivisa.setDisabled(valor);
		NS.txtSpreed.setDisabled(valor);
		NS.txtMontoAutorizado.setDisabled(valor);
		NS.cmbPais.setDisabled(valor);
		NS.txtMontoDispuesto.setDisabled(valor);
		NS.cmbBanco.setDisabled(valor);
		NS.cmbBancoLin.setDisabled(valor);
		NS.cmbNoCuenta.setDisabled(valor);
		NS.cmbTipoContrato.setDisabled(valor);
		NS.cmbDivisa.setDisabled(valor);
		NS.cmbTasa.setDisabled(valor);
		NS.chkRevolvencia.setDisabled(valor);
		NS.chkLargoPlazo.setDisabled(valor);
		NS.optCalculoInt.setDisabled(valor);
		NS.optPagoInt.setDisabled(valor);
		NS.chkReestructura.setDisabled(valor);
		/*
		 * If vsTipoMenu = "F" Then optConRec.Enabled = pbEditar
		 * optSinRec.Enabled = pbEditar optAnticipado.Enabled = pbEditar
		 * End If
		 */
		if (NS.proyecto == 'CIE') {// (&&vsTipoMenu = "" Or vsTipoMenu
			// = "B") Then
			NS.txtFideicomiso.setDisabled(valor);
			NS.cmbFideicomiso.setDisabled(valor);
			NS.txtAgente.setDisabled(valor);
			NS.cmbAgente.setDisabled(valor);
		}
		if (NS.proyecto == 'CIE' && NS.pbModificar) {
			NS.txtBancoLin.setDisabled(false);
			NS.cmbBancoLin.setDisabled(false);
			NS.chkLargoPlazo.setDisabled(false);
			NS.chkReestructura.setDisabled(false);
		}
	};
	NS.limpiaDisposicion = function() {
		NS.pbConsult = false;
		NS.txtTipoFinanDis.setValue('');
		NS.cmbTipoFinanDis.reset();
		NS.cmbDisp.reset();
		NS.cmbFinanciamiento.reset();
		NS.txtEmision.setValue('');
		NS.cmbDivisaD.reset();
		NS.txtMontoDisposicion.setValue("0.00");
		Ext.getCmp(PF + 'txtFecDisp').setValue(NS.fecHoy);
		Ext.getCmp(PF + 'txtFecVenDisp').setValue(NS.fecHoy);
		NS.txtSobreTasa.setValue("0.00000");
		NS.txtAforoPorciento.setValue("100.00");
		NS.txtAforoImporte.setValue("0.00000");
		// se debe seleccionar el segundo
		Ext.getCmp(PF + 'optFormaPago').setValue(1);
		NS.oFormaPago = '1';
		// NS.optFormaPago.setChecked(true);
		// no se selecciona ninguno
		Ext.getCmp(PF + 'optTasa').reset();
		NS.cmbTasaBase.reset();
		NS.txtBase.setValue("0.00000");
		NS.txtPuntos.setValue("0.00000");
		NS.txtTasaVigente.setValue("0.00000");
		NS.txtTasaFija.setValue("0.00000");
		NS.cmbSP.reset();
		NS.cmbMoody.reset();
		NS.cmbFitch.reset();
		NS.txtTasaPonderada.setValue("0.00000");
		NS.txtTasaPostura.setValue("0.00");
		NS.txtSobreVenta.setValue("0.00");
		NS.txtSobreTasaCB.setValue("0.00");
		NS.cmbBancoDis.reset();
		NS.txtBancoDis.setValue('');
		NS.cmbChequeraDis.reset();
		NS.txtEquivalencia.setValue('');
		NS.cmbEquivalencia.reset();
		NS.txtRenta.setValue("0.00");
		NS.txtAnexo.setValue('');
		NS.txtComentarios.setValue('');
		NS.cmbPeriodoRenta.reset();
		NS.txtRentaDep.setValue("0.00");
		NS.chkComision.setValue(0);
		NS.txtWhtax.setValue("0.0000");
		NS.txtMontoMora.setValue("0.00");
		NS.txtTasaMora.setValue("0.0000");
		NS.txtDias.setValue('');
		NS.txtValorFacturas.setValue("0.00");
	}
	NS.editarDisposicion = function(valor) {
		// If vsTipoMenu = "" Then
		NS.txtWhtax.setDisabled(valor);
		// End If
		/*
		 * If vsTipoMenu = "B" Then txtEmision.Enabled = pbEditar
		 * txtSobreTasa.Enabled = pbEditar ' txtSP.Enabled = pbEditar
		 * cmbSP.Enabled = pbEditar ' txtMoody.Enabled = pbEditar
		 * cmbMoody.Enabled = pbEditar ' txtFitch.Enabled = pbEditar
		 * cmbFitch.Enabled = pbEditar txtTasaPonderada.Enabled =
		 * pbEditar txtTasaPostura.Enabled = pbEditar
		 * txtSobreVenta.Enabled = pbEditar txtSobreTasaCB.Enabled =
		 * pbEditar End If
		 */
		NS.txtTipoFinanDis.setDisabled(valor);
		NS.cmbTipoFinanDis.setDisabled(valor);
		/*
		 * If gobjVarGlobal.valor_configura_set(1) = "CIE" And
		 * (vsTipoMenu = "A" Or vsTipoMenu = "F") Then
		 * txtTipoFinanDis.Enabled = False cmbTipoFinanDis.Enabled =
		 * False End If
		 */
		NS.txtDivisaD.setDisabled(valor);
		NS.cmbDivisaD.setDisabled(valor);
		NS.txtMontoDisposicion.setDisabled(valor);
		NS.txtFecDisp.setDisabled(valor);
		NS.txtFecVenDisp.setDisabled(valor);
		/*
		 * If vsTipoMenu = "F" Then txtAforoPorciento.Enabled = pbEditar
		 * txtAforoImporte.Enabled = pbEditar cmdFacturas.Enabled =
		 * pbEditar
		 * 
		 * If gobjVarGlobal.valor_configura_set(1) = "CIE" Then
		 * txtDivisaD.Enabled = False cmbDivisaD.Enabled = False
		 * txtValorFacturas.Enabled = pbEditar End If End If If
		 * vsTipoMenu = "A" Then txtRenta.Enabled = pbEditar
		 * txtAnexo.Enabled = pbEditar cmbPeriodoRenta.Enabled =
		 * pbEditar txtRentaDep.Enabled = pbEditar chkComision.Enabled =
		 * pbEditar End If
		 */
		NS.optFormaPago.setDisabled(valor);
		NS.optTasa.setDisabled(valor);
		NS.cmbTasaBase.setDisabled(valor);
		NS.txtPuntos.setDisabled(valor);
		if (valor) {
			NS.cmbTasaBase.setDisabled(valor);
			NS.txtPuntos.setDisabled(valor);
			NS.txtTasaVigente.setDisabled(valor);
			NS.txtTasaFija.setDisabled(valor);
		}
		NS.txtBancoDis.setDisabled(valor);
		NS.cmbBancoDis.setDisabled(valor);
		NS.cmbChequeraDis.setDisabled(valor);
		NS.txtEquivalencia.setDisabled(valor);
		NS.cmbEquivalencia.setDisabled(valor);
		NS.lblComentarios.setDisabled(valor);
		NS.txtComentarios.setDisabled(valor);
		NS.txtMontoMora.setDisabled(valor);
		NS.txtTasaMora.setDisabled(valor);
		NS.txtDias.setDisabled(valor);
		NS.cmdDisposicion.setDisabled(!valor);
		NS.cmbDisp.setDisabled(!valor);
		NS.cmdAceptarDis.setDisabled(valor);
		NS.cmdModificar1.setDisabled(valor);
	}
	NS.cancelar = function() {
		NS.limpiaDisposicion();
		NS.editarDisposicion(true);
		NS.cmdAmortizaciones.setDisabled(true);
		NS.cmdRentas.setDisabled(true);
		NS.cmdFacturas.setDisabled(true);
		NS.cmdBitacoraDisp.setDisabled(true);
		NS.cmdAvalGarantia.setDisabled(true);
		NS.txtDivisaD.setValue("");
		// NS.cmdEliminar.setDisabled(true);
		NS.cmdEliminar1.setDisabled(true);
		NS.cmdImprimir1.setDisabled(true);
		NS.pbModificarDisp = false;
		NS.cmdContabiliza.setDisabled(true);
		NS.cmdDetalle.setDisabled(true);
		NS.optPagoInt.setDisabled(true);
	}
	NS.limpiar = function() {
		NS.cmbContratos.reset();
		NS.cmbNoCuenta.reset();
		NS.cmbDisp.setDisabled(true);
		NS.cmbDisp.reset();
		NS.limpiaCredito();
		NS.editarAltaContrato(true)
		NS.cmdDisposicion.setDisabled(true);
		NS.cmdBitacora.setDisabled(true);
		NS.cmdBitacoraDisp.setDisabled(true);
		NS.cmdHacer.setDisabled(true);
		NS.cmdModificar.setDisabled(true);
		// NS.cmdModificar1.disabled = true;
		NS.cmdImprimir.setDisabled(true);
		NS.cmdCreditoSindicado.setDisabled(true);
		NS.cmdAvalGarantia.setDisabled(true);
		NS.pbModificar = false
		NS.cmdEliminar.setDisabled(true);
		NS.optPagoInt.setDisabled(true);
		NS.cancelar();
		NS.cmdDisposicion.setDisabled(true);
		NS.cmdRenovacion.setDisabled(true);
		NS.cmbDisp.reset();
		NS.storeDisposiciones.baseParams.psIdContrato = '';
		NS.storeDisposiciones.baseParams.pbEstatus = '';
		NS.optPagoInt.setValue('');
		NS.optPagoInt.reset();
		NS.optCalculoInt.setValue('');
		NS.optCalculoInt.reset();
		NS.storeDisposiciones.removeAll();
		NS.psIdContrato = '';
		NS.pbEstatus = '';
		NS.txtTipoContrato.setValue('');
	};
	NS.crearNuevo = function() {
		NS.cmbContratos.reset();
		NS.limpiar();
		NS.editarAltaContrato(false);
		if (NS.proyecto != 'CIE')
			NS.txtLinea.setDisabled(false);
		NS.cmbPais.setValue("MX");
		BFwrk.Util.updateComboToTextField(PF + 'txtNoPais', NS.cmbPais
				.getId());
		NS.cmdHacer.setDisabled(true);
		NS.pbModificar = false;
		if (NS.proyecto != 'CIE')
			Ext.getCmp(PF + 'txtLinea').focus();
	};
	// cmdCrearNuevo
	NS.cmdCrearNuevo = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCrearNuevo',
		name : PF + 'cmdCrearNuevo',
		text : 'Crear Nuevo',
		x : 870,
		y : 13,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.crearNuevo();
				}
			}
		}
	});
	// pais
	NS.storeCmbPais = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {},
		paramOrder : [],
		directFn : AltaFinanciamientoAction.obtenerPais,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ]
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbPais,
		msg : "Cargando..."
	});
	NS.storeCmbPais.load();
	NS.lblPais = new Ext.form.Label({
		text : 'País:',
		x : 0,
		y : 38
	});
	NS.txtNoPais = new Ext.form.TextField({
		id : PF + 'txtNoPais',
		name : PF + 'txtNoPais',
		value : '',
		x : 0,
		y : 53,
		width : 35,
		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util.updateTextFieldToCombo(
							PF + 'txtNoPais', NS.cmbPais.getId());
				}
			}
		}
	});
	NS.cmbPais = new Ext.form.ComboBox({
		store : NS.storeCmbPais,
		name : PF + 'cmbPais',
		id : PF + 'cmbPais',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 40,
		y : 53,
		width : 160,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione un país',
		triggerAction : 'all',
		disabled : true,
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtNoPais',
							NS.cmbPais.getId());
				}
			}
		}
	});
	// fideicomiso
	NS.storeCmbFideicomiso = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psNacionalidad : '',
			psDivisa : '',
			noEmpresa:''
		},
		paramOrder : [ 'psNacionalidad', 'psDivisa' ,'noEmpresa'],
		directFn : AltaFinanciamientoAction.obtenerBancos,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ],
		listeners : {
			load : function(s, records) {
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbFideicomiso,
		msg : "Cargando..."
	});
	NS.storeCmbFideicomiso.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
	NS.storeCmbFideicomiso.load();
	NS.lblFideicomiso = new Ext.form.Label({
		text : 'Fideicomiso:',
		x : 250,
		y : 38,
		hidden : true,
	});
	NS.txtFideicomiso = new Ext.form.TextField({
		id : PF + 'txtFideicomiso',
		name : PF + 'txtFideicomiso',
		value : '',
		x : 250,
		y : 53,
		width : 35,
		readOnly : true,
		hidden : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util.updateTextFieldToCombo(
							PF + 'txtFideicomiso', NS.cmbFideicomiso
							.getId());
				}
			}
		}
	});
	NS.cmbFideicomiso = new Ext.form.ComboBox({
		store : NS.storeCmbFideicomiso,
		name : PF + 'cmbFideicomiso',
		id : PF + 'cmbFideicomiso',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 290,
		y : 53,
		width : 160,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione Fideicomiso',
		triggerAction : 'all',
		value : '',
		hidden : true,
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF
							+ 'txtFideicomiso', NS.cmbFideicomiso
							.getId());
				}
			}
		}
	});
	// banco liquidador
	NS.storeCmbBanco = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psNacionalidad : '',
			psDivisa : '',
			noEmpresa:''
		},
		paramOrder : [ 'psNacionalidad', 'psDivisa' ,'noEmpresa'],
		directFn : AltaFinanciamientoAction.obtenerBancos,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ],
		listeners : {
			load : function(s, records) {
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbBanco,
		msg : "Cargando..."
	});
	NS.storeCmbBanco.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
	NS.storeCmbBanco.load();
	NS.lblBanco = new Ext.form.Label({
		text : 'Banco Liquidador:',
		x : 260,
		y : 38,
	});

	NS.txtBanco = new Ext.form.NumberField({
		id : PF + 'txtBanco',
		name : PF + 'txtBanco',
		value : '',
		x : 260,
		disabled : true,
		y : 53,
		width : 35,
		listeners : {
			change : {
				fn : function(caja, valor) {
					BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco',
							NS.cmbBanco.getId());
				}
			}
		}
	});
	NS.cmbBanco = new Ext.form.ComboBox(
			{
				store : NS.storeCmbBanco,
				name : PF + 'cmbBanco',
				id : PF + 'cmbBanco',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 300,
				y : 53,
				disabled : true,
				width : 160,
				valueField : 'idStr',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione un banco',
				triggerAction : 'all',
				value : '',
				listeners : {
					select : {
						fn : function(combo, valor) {
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtBanco', NS.cmbBanco.getId());
							if (NS.cmbBanco.store.getCount() > 0) {
								NS.txtBanco.setValue(NS.cmbBanco
										.getValue());
								NS.cmbNoCuenta.reset();
								var myMask = new Ext.LoadMask(Ext
										.getBody(), {
									store : NS.storeComboClabe,
									msg : "Cargando..."
								});

								NS.storeComboClabe.load({
									params : {
										pvvValor2 : NS.txtBanco
										.getValue(),
										psDivisa : '',
										noEmpresa:parseInt(NS.txtEmpresa.getValue())
									}
								});
								NS.txtBancoLin.setValue(NS.cmbBanco
										.getValue());
								NS.cmbBancoLin.setValue(NS.txtBanco
										.getValue());
							} else {
								NS.txtBanco.setValue("");
								NS.cmbNoCuenta.reset();
							}
						}
					}
				}
			});
	// banco agente
	NS.storeCmbBancoAgente = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {},
		paramOrder : [],
		directFn : AltaFinanciamientoAction.obtenerArrendadoras,
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
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbBancoAgente,
		msg : "Cargando..."
	});
	NS.storeCmbBancoAgente.load();
	NS.lblAgente = new Ext.form.Label({
		text : 'Banco Agente:',
		x : 510,
		y : 38,
		hidden : true
	});
	NS.txtAgente = new Ext.form.TextField({
		id : PF + 'txtAgente',
		name : PF + 'txtAgente',
		value : '',
		x : 510,
		y : 53,
		width : 35,
		hidden : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util.updateTextFieldToCombo(
							PF + 'txtAgente', NS.cmbAgente.getId());
				}
			}
		}
	});
	NS.cmbAgente = new Ext.form.ComboBox({
		store : NS.storeCmbBancoAgente,
		name : PF + 'cmbAgente',
		id : PF + 'cmbAgente',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 550,
		y : 53,
		width : 160,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione banco agente',
		triggerAction : 'all',
		value : '',
		hidden : true,
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtAgente',
							NS.cmbAgente.getId());
				}
			}
		}
	});
	// Chequera
	NS.lblNoCuenta = new Ext.form.Label({
		text : 'Chequera:',
		x : 510,
		y : 38,
	});
	NS.cmbNoCuenta = new Ext.form.ComboBox({
		store : NS.storeComboClabe,
		name : PF + 'cmbNoCuenta',
		id : PF + 'cmbNoCuenta',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 510,
		y : 53,
		disabled : true,
		width : 160,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una chequera',
		triggerAction : 'all',
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {
				}
			}
		}
	});
	// banco otorgante
	NS.storeCmbBancoLin = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {},
		paramOrder : [],
		directFn : AltaFinanciamientoAction.obtenerArrendadoras,
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
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbBancoLin,
		msg : "Cargando..."
	});
	NS.storeCmbBancoLin.load();
	NS.lblBancoLin = new Ext.form.Label({
		text : 'Banco Otorgante:',
		x : 750,
		y : 38,
	});
	NS.txtBancoLin = new Ext.form.NumberField(
			{
				id : PF + 'txtBancoLin',
				name : PF + 'txtBancoLin',
				value : '',
				x : 750,
				y : 53,
				width : 35,
				disabled : true,
				listeners : {
					change : {
						fn : function(caja, valor) {
							var comboValue = BFwrk.Util
							.updateTextFieldToCombo(PF
									+ 'txtBancoLin',
									NS.cmbBancoLin.getId());
						}
					}
				}
			});
	NS.cmbBancoLin = new Ext.form.ComboBox({
		store : NS.storeCmbBancoLin,
		name : PF + 'cmbBancoLin',
		id : PF + 'cmbBancoLin',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 790,
		y : 53,
		width : 160,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione un banco',
		triggerAction : 'all',
		value : '',
		disabled : true,
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF
							+ 'txtBancoLin', NS.cmbBancoLin.getId());
				}
			}
		}
	});
	// Tipo Línea
	NS.storeCmbTipoContratos = new Ext.data.DirectStore({
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
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbTipoContratos,
		msg : "Cargando..."
	});
	NS.storeCmbTipoContratos.load();
	NS.lblTipoContrato = new Ext.form.Label({
		text : 'Tipo Línea:',
		x : 0,
		y : 80
	});
	NS.txtTipoContrato = new Ext.form.NumberField({
		id : PF + 'txtTipoContrato',
		name : PF + 'txtTipoContrato',
		value : '',
		x : 0,
		y : 95,
		width : 35,
		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util.updateTextFieldToCombo(
							PF + 'txtTipoContrato', NS.cmbTipoContrato
							.getId());
				}
			}
		}
	});
	NS.cmbTipoContrato = new Ext.form.ComboBox({
		store : NS.storeCmbTipoContratos,
		name : PF + 'cmbTipoContrato',
		id : PF + 'cmbTipoContrato',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 40,
		y : 95,
		width : 160,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione Tipo Línea',
		triggerAction : 'all',
		disabled : true,
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF
							+ 'txtTipoContrato', NS.cmbTipoContrato
							.getId());
				}
			}
		}
	});
	// fecha inicial
	NS.lblFechaIni = new Ext.form.Label({
		text : 'Fecha Inicio:',
		x : 250,
		y : 80
	});
	NS.txtFechaIni = new Ext.form.DateField(
			{
				id : PF + 'txtFechaIni',
				name : PF + 'txtFechaIni',
				format : 'd/m/Y',
				disabled : true,
				x : 250,
				y : 95,
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
								Ext.getCmp(PF + 'txtFechaIni')
								.setValue(NS.fecHoy);
								Ext.getCmp(PF + 'txtFechaIni').focus();
							} else {
								if (fechaFin < caja.getValue()) {
									BFwrk.Util
									.msgShow(
											'La fecha de inicial no puede ser mayor a la final.',
									'ERROR');
									Ext.getCmp(PF + 'txtFechaIni')
									.focus();
									Ext.getCmp(PF + 'txtFechaIni')
									.setValue(NS.fecHoy);
								} else {
									diaSemana = caja.getValue()
									.getDay();
									if (diaSemana == "0") {
										BFwrk.Util
										.msgShow(
												'La fecha de inicio es inhábil.',
										'ERROR');
										Ext.getCmp(PF + 'txtFechaIni')
										.focus();
										Ext.getCmp(PF + 'txtFechaIni')
										.setValue(NS.fecHoy);

									} else if (diaSemana == "6") {
										BFwrk.Util
										.msgShow(
												'La fecha de inicio es inhábil.',
										'ERROR');
										Ext.getCmp(PF + 'txtFechaIni')
										.focus();
										Ext.getCmp(PF + 'txtFechaIni')
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
													.msgShow(
															'La fecha de inicio es inhábil.',
															'ERROR');
													NS.storeSelectInhabil.valorFecha = '';
													NS.valorFecha = '';
													Ext
													.getCmp(
															PF
															+ 'txtFechaIni')
															.focus();
													Ext
													.getCmp(
															PF
															+ 'txtFechaIni')
															.setValue(
																	NS.fecHoy);
												}
												NS.storeSelectInhabil.valorFecha = '';
												NS.valorFecha = '';
											}
										});
									}
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
	// fecha fin
	NS.lblFechaFin = new Ext.form.Label({
		text : 'Fecha Final:',
		x : 352,
		y : 80
	});
	NS.txtFechaFin = new Ext.form.DateField(
			{
				id : PF + 'txtFechaFin',
				name : PF + 'txtFechaFin',
				format : 'd/m/Y',
				x : 352,
				y : 95,
				disabled : true,
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
								Ext.getCmp(PF + 'txtFechaFin')
								.setValue(NS.fecHoy);
								Ext.getCmp(PF + 'txtFechaFin').focus();
							} else {
								if (fechaIni > caja.getValue()) {
									BFwrk.Util
									.msgShow(
											'La fecha de final no puede ser menor a la inicial.',
									'ERROR');
									Ext.getCmp(PF + 'txtFechaFin')
									.focus();
									Ext.getCmp(PF + 'txtFechaFin')
									.setValue(NS.fecHoy);
								} else {
									diaSemana = caja.getValue()
									.getDay();

									if (diaSemana == "0") {
										BFwrk.Util
										.msgShow(
												'La fecha de vencimiento es inhábil.',
										'ERROR');
										Ext.getCmp(PF + 'txtFechaFin')
										.focus();
										Ext.getCmp(PF + 'txtFechaFin')
										.setValue(NS.fecHoy);
									} else if (diaSemana == "6") {
										BFwrk.Util
										.msgShow(
												'La fecha de vencimiento es inhábil.',
										'ERROR');
										Ext.getCmp(PF + 'txtFechaFin')
										.focus();
										Ext.getCmp(PF + 'txtFechaFin')
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
													.msgShow(
															'La fecha de vencimiento es inhábil.',
															'ERROR');
													NS.storeSelectInhabil.valorFecha = '';
													NS.valorFecha = '';
													Ext
													.getCmp(
															PF
															+ 'txtFechaFin')
															.focus();
													Ext
													.getCmp(
															PF
															+ 'txtFechaFin')
															.setValue(
																	NS.fecHoy);
												}
												NS.storeSelectInhabil.valorFecha = '';
												NS.valorFecha = '';
											}
										});
									}
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
	// divisa
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
	NS.lblDivisa = new Ext.form.Label({
		text : 'Divisa:',
		x : 510,
		y : 80,

	});
	NS.txtDivisa = new Ext.form.TextField({
		id : PF + 'txtDivisa',
		name : PF + 'txtDivisa',
		value : '',
		x : 510,
		y : 95,
		width : 35,
		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util.updateTextFieldToCombo(
							PF + 'txtDivisa', NS.cmbDivisa.getId());
				}
			}
		}
	});
	NS.cmbDivisa = new Ext.form.ComboBox({
		store : NS.storeCmbDivisa,
		name : PF + 'cmbDivisa',
		id : PF + 'cmbDivisa',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 550,
		disabled : true,
		y : 95,
		width : 160,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una divisa',
		triggerAction : 'all',
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtDivisa',
							NS.cmbDivisa.getId());
				}
			}
		}
	});
	// tasa línea
	NS.storeCmbTasa = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {},
		paramOrder : [],
		directFn : AltaFinanciamientoAction.obtenerTasa,
		idProperty : 'idStr',
		fields : [ {
			name : 'idStr'
		}, {
			name : 'descripcion'
		} ]
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbTasa,
		msg : "Cargando..."
	});
	NS.storeCmbTasa.load();
	NS.lblTasa = new Ext.form.Label({
		text : 'Tasa Línea:',
		x : 750,
		y : 80,
	});

	NS.cmbTasa = new Ext.form.ComboBox({
		store : NS.storeCmbTasa,
		name : PF + 'cmbTasa',
		id : PF + 'cmbTasa',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 750,
		y : 95,
		width : 160,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		disabled : true,
		emptyText : 'Seleccione tasa',
		triggerAction : 'all',
		value : ''
	});
	// monto autorizado
	NS.lblMontoAutorizado = new Ext.form.Label({
		text : 'Monto Autorizado:',
		x : 0,
		y : 120,
	});
	NS.txtMontoAutorizado = new Ext.form.TextField(
			{
				id : PF + 'txtMontoAutorizado',
				name : PF + 'txtMontoAutorizado',
				disabled : true,
				x : 0,
				y : 135,
				value : "0.00",
				width : 100,
				listeners : {
					change : {
						fn : function(caja, valor) {
							Ext
							.getCmp(PF + 'txtMontoAutorizado')
							.setValue(
									BFwrk.Util
									.formatNumber(caja
											.getValue()));
						}
					},
					blur : {
						fn : function(caja, valor) {
							NS.txtMontoDisponible
							.setValue(BFwrk.Util
									.formatNumber(parseFloat(BFwrk.Util
											.unformatNumber(NS.txtMontoAutorizado
													.getValue()))
													- parseFloat(BFwrk.Util
															.unformatNumber(NS.txtMontoDispuesto
																	.getValue()))));
							NS.txtValorizacion.setValue("");
							if (NS.txtDivisa.getValue() != "MN") {
								NS.storeTipoCambio.baseParams.idDivisa = NS.txtDivisa
								.getValue();
								var myMask = new Ext.LoadMask(Ext
										.getBody(), {
									store : NS.storeTipoCambio,
									msg : "Cargando..."
								});
								NS.storeTipoCambio.load();
							} else {
								NS.txtValorizacion
								.setValue(NS.txtMontoDisponible
										.getValue());
							}
						}
					}
				}
			});
	// monto dispuesto
	NS.lblMontoDispuesto = new Ext.form.Label({
		text : 'Monto Dispuesto:',
		x : 110,
		y : 120,
	});
	NS.txtMontoDispuesto = new Ext.form.TextField({
		id : PF + 'txtMontoDispuesto',
		name : PF + 'txtMontoDispuesto',
		disabled : true,
		x : 110,
		y : 135,
		value : "0.00",
		width : 100,
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtMontoDispuesto').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	// monto disponible
	NS.lblMontoDisponible = new Ext.form.Label({
		text : 'Monto Disponible:',
		x : 250,
		y : 120,

	});
	NS.txtMontoDisponible = new Ext.form.TextField({
		id : PF + 'txtMontoDisponible',
		name : PF + 'txtMontoDisponible',
		disabled : true,
		x : 250,
		y : 135,
		value : "0.00",
		width : 100,
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtMontoDisponible').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	// valorizacion
	NS.lblValorizacion = new Ext.form.Label({
		text : 'Valorización:',
		x : 357,
		y : 120,
	});
	NS.txtValorizacion = new Ext.form.TextField({
		id : PF + 'txtValorizacion',
		name : PF + 'txtValorizacion',
		disabled : true,
		x : 357,
		y : 135,
		width : 100,
		value : "0.00",
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtValorizacion').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	// Spread
	NS.lblSpreed = new Ext.form.Label({
		text : 'Spread:',
		x : 510,
		y : 120,
		hidden:true,
	});
	NS.txtSpreed = new Ext.form.TextField({
		id : PF + 'txtSpreed',
		name : PF + 'txtSpreed',
		disabled : true,
		x : 510,
		y : 135,
		hidden:true,
		width : 80,
		value : "0.00",
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtSpreed').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	//
	NS.chkRevolvencia = new Ext.form.Checkbox({
		name : PF + 'chkRevolvencia',
		id : 'chkRevolvencia',
		x : 630,
		y : 135,
		// checked:true,
		disabled : true,
		listeners : {
			check : {
				fn : function() {
					// var me = this;
					// var res = me.getValue();
					// if (res == true) {
					// NS.storeSolicitudes.baseParams.cTodas = 1;
					// NS.cTodas = 1;
					// } else {
					// NS.storeSolicitudes.baseParams.cTodas = 0;
					// NS.cTodas = 0;
					// }
				}
			}
		}
	});
	NS.lblRevolvencia = new Ext.form.Label({
		text : 'Revolvencia',
		x : 645,
		y : 138
	});
	// inhabil calculo
	NS.optCalculoInt = new Ext.form.RadioGroup({
		id : PF + 'optCalculoInt',
		name : PF + 'optCalculoInt',
		x : 0,
		y : 0,
		columns : 1,
		items : [ {
			boxLabel : 'Día establecido',
			name : 'optCalcInt',
			inputValue : 0,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.calculoInt = '0';
							NS.oCalculoI = '0';
						}
					}
				}
			}
		}, {
			boxLabel : 'Anterior',
			name : 'optCalcInt',
			inputValue : 1,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.calculoInt = '1';
							NS.oCalculoI = '1';
						}
					}
				}
			}
		}, {
			boxLabel : 'Posterior',
			name : 'optCalcInt',
			inputValue : 2,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.calculoInt = '2';
							NS.oCalculoI = '2';
						}
					}
				}
			}
		} ]
	});
	// inhabil pago
	NS.optPagoInt = new Ext.form.RadioGroup({
		id : PF + 'optPagoInt',
		name : PF + 'optPagoInt',
		x : 0,
		y : 0,
		columns : 1,
		items : [ {
			boxLabel : 'Anterior',
			name : 'optPagInt',
			inputValue : 0,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.pagoInt = '0';
							NS.oPagoI = '0';
						}
					}
				}
			}
		}, {
			boxLabel : 'Posterior',
			name : 'optPagInt',
			inputValue : 1,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.pagoInt = '1';
							NS.oPagoI = '1';
						}
					}
				}
			}
		} ]
	});
	// Comun
	NS.lblComun = new Ext.form.Label({
		text : 'Representante Común',
		x : 0,
		y : 160
	});
	NS.txtComun = new Ext.form.TextField({
		id : PF + 'txtComun',
		name : PF + 'txtComun',
		value : '',
		x : 0,
		y : 175,
		width : 35,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.cmbComun = new Ext.form.ComboBox({
		store : NS.storeCmbEmpresa,
		name : PF + 'cmbComun',
		id : PF + 'cmbComun',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 40,
		y : 175,
		width : 160,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una empresa',
		triggerAction : 'all',
		value : NS.GI_NOM_EMPRESA,
		listeners : {
			select : {
				fn : function(combo, valor) {
					// BFwrk.Util.updateComboToTextField(
					// PF + 'txtEmpresa', NS.cmbEmpresa.getId());
					// /NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	// operacion
	NS.optOperacion = new Ext.form.RadioGroup({
		id : PF + 'optOperacion',
		name : PF + 'optOperacion',
		x : 0,
		y : 0,
		columns : 3,
		items : [ {
			boxLabel : 'Con recurso',
			name : 'optOpt',
			inputValue : 0,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.optOp = '0';
						}
					}
				}
			}
		}, {
			boxLabel : 'Sin recurso',
			name : 'optOpt',
			inputValue : 1,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.optOp = '1';
						}
					}
				}
			}
		}, {
			boxLabel : 'Anticipados/Proveedores',
			name : 'optOpt',
			inputValue : 2,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.optOp = '2';
						}
					}
				}
			}
		} ]
	});
	// cmdCreditoSindicado
	NS.cmdCreditoSindicado = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCreditoSindicado',
		name : PF + 'cmdCreditoSindicado',
		text : 'Crédito Sindicado',
		x : 765,
		y : 225,
		hidden : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					// NS.verificaCampos2();
					// NS.buscar();

				}
			}
		}
	});
	NS.lblFinanciamientoO = new Ext.form.Label({
		text : 'Financiamiento:',
		x : 0,
		y : 0,

	});
	NS.txtFinanciamientoO = new Ext.form.TextField({
		id : PF + 'txtFinanciamientoO',
		name : PF + 'txtFinanciamientoO',
		value : '',
		x : 0,
		y : 15,
		disabled : true,
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.buscar = function() {
		Ext.getCmp(PF + 'txtDescripcion').setValue("");
		if (Ext.getCmp(PF + 'txtFinanciamientoO').getValue() != "") {
			NS.storeObligaciones.baseParams.idFinanciamiento = Ext
			.getCmp(PF + 'txtFinanciamientoO').getValue();
			NS.storeObligaciones.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
			NS.storeObligaciones.load();
		} else {
			Ext.Msg.alert('SET', 'Falta el financiamiento');
			return;
		}
	}
	NS.cmdBuscar = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdBuscar',
		name : PF + 'cmdBuscar',
		text : 'Buscar',
		x : 460,
		y : 15,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.buscar();
				}
			}
		}
	});
	NS.lblDescripcion = new Ext.form.Label({
		text : 'Descripción:',
		x : 0,
		y : 0,

	});
	NS.txtDescripcion = new Ext.form.TextField({
		id : PF + 'txtDescripcion',
		name : PF + 'txtDescripcion',
		value : '',
		x : 0,
		y : 15,
		width : 400,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.cmdBuscar = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdBuscar',
		name : PF + 'cmdBuscar',
		text : 'Buscar',
		x : 460,
		y : 15,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.buscar();
				}
			}
		}
	});
	NS.aceptarObligacion = function() {
		var ultimaobligacion;
		if (Ext.getCmp(PF + 'txtDescripcion').getValue() != "") {
			NS.storeObligacionesT.baseParams.idFinanciamiento = Ext
			.getCmp(PF + 'txtFinanciamientoO').getValue();
			NS.storeObligacionesT.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
			NS.storeObligacionesT.load({
				callback : function(records, operation, success) {
					if (records.length != 0) {
						ultimaobligacion = records[records.length - 1]
						.get('idClave') + 1;
					} else {
						ultimaobligacion = 1;
					}
					AltaFinanciamientoAction.insertObligacion(Ext
							.getCmp(PF + 'txtFinanciamientoO')
							.getValue(), ultimaobligacion,
							NS.txtDescripcion.getValue(), parseInt(NS.txtEmpresa.getValue()),function(
									resultado, e) {
						BFwrk.Util.msgWait('Terminado...',
								false);
						NS.buscar();
					});
				}
			});
		} else {
			Ext.Msg.alert('SET',
			'Falta la descripcion de la obligación.');
			return;
		}
	}
	NS.cmdAceptarO = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAceptarO',
		name : PF + 'cmdAceptarO',
		text : 'Aceptar',
		x : 460,
		y : 0,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.aceptarObligacion();
				}
			}
		}
	});
	NS.cancelarO = function() {
		NS.txtDescripcion.setValue("");
		NS.txtDescripcion.setDisabled(true);
		NS.cmdAceptarO.setDisabled(true);
		NS.cmdCancelarO.setDisabled(true);
	}
	NS.cmdCancelarO = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCancelarO',
		name : PF + 'cmdCancelarO',
		text : 'Cancelar',
		x : 460,
		y : 30,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.cancelarO();
				}
			}
		}
	});
	NS.crearNuevaObligacion = function() {
		Ext.getCmp(PF + 'txtDescripcion').setDisabled(false);
		NS.cmdAceptarO.setDisabled(false);
		NS.cmdCancelarO.setDisabled(false);
		Ext.getCmp(PF + 'txtDescripcion').setValue("");
	}
	NS.cmdNuevo = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdNuevo',
		name : PF + 'cmdNuevo',
		text : 'Crear Nuevo',
		x : 380,
		y : 330,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.crearNuevaObligacion();
				}
			}
		}
	});
	NS.eliminarO = function() {
		var obligaciones = NS.gridDatos3.getSelectionModel()
		.getSelections();
		if (obligaciones.length <= 0) {
			BFwrk.Util
			.msgShow(
					'No existen registros seleccionados para eliminar ',
			'WARNING');
		} else {
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
					'Se va a eliminar el registro, ¿Desea continuar?',
					function(btn) {
						if (btn === 'yes') {
							BFwrk.Util.msgWait(
									'Ejecutando solicitud...',
									true);
							var matrizObligaciones = new Array();
							for (var i = 0; i < obligaciones.length; i++) {
								var regObl = {};
								regObl.idClave = obligaciones[i]
								.get('idClave');
								matrizObligaciones[i] = regObl;
							}
							var jsonStringO = Ext.util.JSON
							.encode(matrizObligaciones);
							AltaFinanciamientoAction
							.deleteObligacion(
									jsonStringO,
									Ext
									.getCmp(
											PF
											+ 'txtFinanciamientoO')
											.getValue(),parseInt(NS.txtEmpresa.getValue()),
											function(resultado,
													e) {
										BFwrk.Util
										.msgWait(
												'Terminado...',
												false);
										NS.buscar();
									});
						} else {
							return;
						}
					});
		}
	}
	NS.cmdEliminarO = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdEliminarO',
		name : PF + 'cmdEliminarO',
		text : 'Eliminar',
		x : 470,
		y : 330,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.eliminarO();
				}
			}
		}
	});
	NS.storeObligaciones = new Ext.data.DirectStore(
			{
				paramsAsHash : false,
				root : '',
				baseParams : {
					idFinanciamiento : '',
					noEmpresa:0
				},
				paramOrder : [ 'idFinanciamiento' ,'noEmpresa'],
				directFn : AltaFinanciamientoAction.obtenerObligaciones,
				fields : [ {
					name : 'idClave'
				}, {
					name : 'descripcion'
				}, ],
				listeners : {
					load : function(s, records) {
						if (records.length <= 0) {
							Ext.Msg
							.alert('SET',
									'No existen obligaciones para este financiamiento');
							return;
						}
					}
				}
			});
	NS.storeObligacionesT = new Ext.data.DirectStore(
			{
				paramsAsHash : false,
				root : '',
				baseParams : {
					idFinanciamiento : '',
					noEmpresa:0
				},
				paramOrder : [ 'idFinanciamiento','noEmpresa' ],
				directFn : AltaFinanciamientoAction.obtenerObligacionesTotal,
				fields : [ {
					name : 'idClave'
				}, {
					name : 'descripcion'
				}, ],
				listeners : {
					load : function(s, records) {
						if (records.length <= 0) {
							Ext.Msg
							.alert('SET',
									'No existen obligaciones para este financiamiento');
							return;
						}
					}
				}
			});
	NS.smE3 = new Ext.grid.CheckboxSelectionModel({
		singleSelect : false
	});
	NS.gridDatos3 = new Ext.grid.GridPanel({
		store : NS.storeObligaciones,
		id : PF + 'gridDatos3',
		name : PF + 'gridDatos3',
		frame : true,
		width : 550,
		height : 150,
		x : 0,
		y : 50,
		cm : new Ext.grid.ColumnModel({
			defaults : {
				width : 120,
				value : true,
				sortable : true
			},
			columns : [ NS.smE3, {
				id : 'idClave',
				header : 'Clave',
				width : 150,
				dataIndex : 'idClave',
				direction : 'ASC'
			}, {
				header : 'Descripción',
				width : 350,
				dataIndex : 'descripcion',

			}, ]
		}),
		viewConfig : {
			getRowClass : function(record, index) {
			}
		},
		sm : NS.smE3,

		listeners : {
			click : {
				fn : function(grid) {

				}
			},
			dblclick : {}
		}
	});
	NS.winObligacion = new Ext.Window(
			{
				title : 'Obligaciones (Créditos bancarios)',
				modal : true,
				shadow : true,
				closeAction : 'hide',
				width : 600,
				height : 400,
				layout : 'absolute',
				plain : true,
				bodyStyle : 'padding:10px;',
				buttonAlign : 'center',
				draggable : false,
				resizable : false,
				autoScroll : true,
				items : [
				         {
				        	 xtype : 'fieldset',
				        	 title : '',
				        	 id : PF + 'panelFinanc',
				        	 name : PF + 'panelFinanc',
				        	 x : 0,
				        	 y : 0,
				        	 width : 580,
				        	 height : 220,
				        	 layout : 'absolute',
				        	 items : [ NS.lblFinanciamientoO,
				        	           NS.txtFinanciamientoO,
				        	           NS.cmdBuscar, NS.gridDatos3 ]
				         },
				         {
				        	 xtype : 'fieldset',
				        	 title : '',
				        	 id : PF + 'panelDescr',
				        	 name : PF + 'panelDescr',
				        	 x : 0,
				        	 y : 230,
				        	 width : 580,
				        	 height : 80,
				        	 layout : 'absolute',
				        	 items : [ NS.lblDescripcion,
				        	           NS.txtDescripcion, NS.cmdAceptarO,
				        	           NS.cmdCancelarO ]
				         }, NS.cmdNuevo, NS.cmdEliminarO ],
				         listeners : {
				        	 show : {
				        		 fn : function() {
				        			 Ext.getCmp(PF + 'txtFinanciamientoO')
				        			 .setDisabled(true);
				        			 Ext.getCmp(PF + 'txtFinanciamientoO')
				        			 .setValue(NS.txtLinea.getValue());
				        			 Ext.getCmp(PF + 'txtDescripcion')
				        			 .setDisabled(true);
				        			 NS.cmdAceptarO.setDisabled(true);
				        			 NS.storeObligaciones.removeAll();
				        		 }
				        	 },
				        	 hide : {
				        		 fn : function() {
				        			 Ext.getCmp(PF + 'txtFinanciamientoO')
				        			 .setValue("");
				        			 Ext.getCmp(PF + 'txtDescripcion').setValue(
				        			 "");
				        		 }
				        	 }
				         }
			});
	NS.hacer = function() {
		if (NS.cmbContratos.getValue() == ''
			|| NS.cmbContratos.getValue() == 0) {
			Ext.Msg.alert('SET',
			'Ingrese el nombre de un Financiamiento');
			NS.txtIdContrato.focus();
			return;
		}
		NS.winObligacion.show();
	}
	NS.cmdHacer = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdHacer',
		name : PF + 'cmdHacer',
		text : 'Hacer/No Hacer',
		disabled : true,
		x : 865,
		y : 225,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.hacer();
				}
			}
		}
	});
	//
	NS.lblDisp = new Ext.form.Label({
		text : 'Disposiciones',
		x : 0,
		y : 240
	});
	NS.storeBuscaComisiones = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psLinea : '',
			piDisp : 0
		},
		paramOrder : [ 'psLinea', 'piDisp' ],
		directFn : AltaFinanciamientoAction.buscaComisiones,
		idProperty : 'idFinanciamiento',
		fields : [ {
			name : 'idFinanciamiento'
		}, {
			name : 'idDisposicion'
		} ],
		listeners : {
			load : function(s, records) {

			}
		}
	});
	NS.cmbDisp = new Ext.form.ComboBox({
		store : NS.storeDisposiciones,
		name : PF + 'cmbDisp',
		id : PF + 'cmbDisp',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		disabled : true,
		selecOnFocus : true,
		forceSelection : true,
		x : 0,
		y : 255,
		width : 160,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione disposición',
		triggerAction : 'all',
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {

					if (NS.cmbDisp.store.getCount() > 0) {
						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeSelectDisp,
							msg : "Cargando..."
						});

						NS.storeSelectDisp.load({
							params : {
								psIdContrato : NS.txtLinea.getValue(),
								psIdDisp : parseInt(NS.cmbDisp
										.getValue())
							}
						});
					}

				}
			}
		}
	});
	NS.storeBitacora = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			vsContrato : '',
			viDisp : 0,
			noEmpresa:0
		},
		paramOrder : [ 'vsContrato', 'viDisp' ,'noEmpresa'],
		directFn : AltaFinanciamientoAction.selectBitacora,
		// idProperty : 'fecAlta',
		fields : [ {
			name : 'fecAlta'
		}, {
			name : 'usuario'
		}, {
			name : 'nota'
		}, {
			name : 'idFinanciamientoHijo'
		}, {
			name : 'idDisposicionHijo'
		}, , ]
	});
	NS.winBitacoraC = new Ext.Window(
			{
				title : 'Bitácora (Créditos bancarios)',
				modal : true,
				shadow : true,
				closeAction : 'hide',
				width : 600,
				height : 300,
				layout : 'absolute',
				plain : true,
				bodyStyle : 'padding:10px;',
				buttonAlign : 'center',
				draggable : false,
				resizable : false,
				autoScroll : true,
				items : [ {
					xtype : 'fieldset',
					title : 'Informe bitácora',
					id : PF + 'panelInformeBit',
					name : PF + 'panelInformeBit',
					x : 0,
					y : 0,
					width : 570,
					height : 255,
					layout : 'absolute',
					items : [ {
						xtype : 'textarea',
						id : PF + 'txtNotaC',
						name : PF + 'txtNotaC',
						fieldLabel : 'txtNotaC',
						width : '100%',
						height : 220,
						preventScrollbars : true,
					} ]
				}, ],
				listeners : {
					show : {
						fn : function() {
							var vsContrato;
							var viDisp = 0;
							var vsInformacion = '';
							if (NS.pbBitLinea)
								vsContrato = NS.txtLinea.getValue();
							if (!NS.pbBitLinea) {
								vsContrato = NS.txtLinea.getValue();
								viDisp = parseInt(NS.cmbDisp.getValue());
							}
							NS.storeBitacora
							.load({
								params : {
									vsContrato : vsContrato,
									viDisp : viDisp,
									noEmpresa:parseInt(NS.txtEmpresa.getValue())
								},
								callback : function(records,
										operation, success) {
									if (records.length > 0) {
										for (var i = 0; i <= records.length - 1; i++) {
											vsInformacion = vsInformacion
											+ "\n"
											+ records[i]
											.get('fecAlta')
											+ "\n"
											+ records[i]
											.get('usuario')
											+ "\n"
											+ records[i]
											.get('nota')
											+ "\n"
											+ records[i]
											.get('idFinanciamientoHijo')
											+ "\n"
											+ records[i]
											.get('idDisposicionHijo')
											+ "\n\n";
										}
										Ext
										.getCmp(
												PF
												+ 'txtNotaC')
												.setValue(
														vsInformacion);
									} else {
										Ext.Msg
										.alert('SET',
										'No se ha dado de alta ninguna nota de este crédito');
									}
								}
							});
						}
					},
					hide : {
						fn : function() {
							Ext.getCmp(PF + 'txtNotaC').setValue("");
						}
					}
				}
			});
	NS.bitacoraC = function() {
		NS.winBitacoraC.show();
	}
	NS.cmdConsultaBit = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdConsultaBit',
		name : PF + 'cmdConsultaBit',
		text : 'Informe',
		x : 290,
		y : 230,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.bitacoraC();
				}
			}
		}
	});
	NS.aceptarBitacora = function() {
		var viDisposi;
		if (Ext.getCmp(PF + 'txtNota').getValue() == "") {
			Ext.Msg.alert('SET', 'Inserte una nota.');
			return;
		}
		if (NS.pbBitLinea)
			viDisposi = 0;
		if (!NS.pbBitLinea)
			viDisposi = NS.cmbDisp.getValue();
		AltaFinanciamientoAction
		.insertBitacora(
				NS.txtLinea.getValue(),
				viDisposi,
				Ext.getCmp(PF + 'txtNota').getValue(),
				Ext.getCmp(PF + 'txtFinanciamiento').getValue(),
				parseInt(NS.txtEmpresa.getValue()),
				function(mapResult, e) {
					BFwrk.Util.msgWait('Terminado...', false);
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
					}
				});
		NS.winBitacora.hide();
	}
	NS.cmdAceptarBit = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAceptarBit',
		name : PF + 'cmdAceptarBit',
		text : 'Aceptar',
		x : 380,
		y : 230,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.aceptarBitacora();
				}
			}
		}
	});
	NS.cancelarBitacora = function() {
		Ext.getCmp(PF + 'txtFinanciamiento').setValue("");
		Ext.getCmp(PF + 'txtNota').setValue("");
		NS.winBitacora.hide();
	}
	NS.cmdCancelarBit = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCancelarBit',
		name : PF + 'cmdCancelarBit',
		text : 'Cancelar',
		x : 470,
		y : 230,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.cancelarBitacora();
				}
			}
		}
	});
	NS.winBitacora = new Ext.Window({
		title : 'Bitácora (Créditos bancarios)',
		modal : true,
		shadow : true,
		closeAction : 'hide',
		width : 600,
		height : 300,
		layout : 'absolute',
		plain : true,
		bodyStyle : 'padding:10px;',
		buttonAlign : 'center',
		draggable : false,
		resizable : false,
		autoScroll : true,
		items : [ {
			xtype : 'fieldset',
			title : 'Nota',
			id : PF + 'panelNota',
			name : PF + 'panelNota',
			x : 0,

			y : 0,
			width : 580,
			height : 130,
			layout : 'absolute',
			items : [ {
				xtype : 'textarea',
				id : PF + 'txtNota',
				name : PF + 'txtNota',
				fieldLabel : 'txtNota',
				width : '100%',
				height : 100,
				preventScrollbars : true,
			} ]
		}, {
			xtype : 'fieldset',
			title : 'Línea y disposiciones',
			id : PF + 'panelLinDisp',
			name : PF + 'panelLinDisp',
			x : 0,
			y : 132,
			width : 580,
			height : 80,
			layout : 'absolute',
			items : [ {
				xtype : 'textarea',
				id : PF + 'txtFinanciamiento',
				name : PF + 'txtFinanciamiento',
				fieldLabel : 'txtFinanciamiento',
				width : '100%',
				height : 50,
				preventScrollbars : true,
			} ]
		}, NS.cmdConsultaBit, NS.cmdAceptarBit, NS.cmdCancelarBit ],
		listeners : {
			show : {
				// funcionamiento
				fn : function() {
				}
			},
			hide : {
				fn : function() {
					Ext.getCmp(PF + 'txtFinanciamiento').setValue("");
					Ext.getCmp(PF + 'txtNota').setValue("");
				}
			}

		}
	});

	NS.bitacora = function() {
		if (NS.cmbContratos.getValue() <= 0) {
			Ext.Msg.alert('SET', 'Seleccione un financiamiento');
			NS.txtLinea.focus();
			return;
		}
		NS.pbBitLinea = true;
		NS.winBitacora.show();
		Ext.getCmp(PF + 'txtNota').setValue("");
		Ext.getCmp(PF + 'txtFinanciamiento').setValue("");
	}
	NS.cmdBitacora = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdBitacora',
		name : PF + 'cmdBitacora',
		text : 'Bitácora',
		x : 330,
		y : 255,
		disabled : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.bitacora();
				}
			}
		}
	});
	NS.llenaCombosDis = function() {
		NS.cmbFinanciamiento.reset();
		NS.cmbTipoFinanDis.reset();
		NS.cmbDivisaD.reset();
		NS.cmbTasaBase.reset();
		NS.cmbBancoDis.reset();
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeCmbContratos,
			msg : "Cargando..."
		});
		NS.storeCmbContratos.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
		NS.storeCmbContratos.load();

		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeCmbTipoContratos,
			msg : "Cargando..."
		});
		NS.storeCmbTipoContratos.load();
		NS.storeCmbDivisa.baseParams.bRestringido = false;
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeCmbDivisa,
			msg : "Cargando..."
		});
		NS.storeCmbDivisa.load();
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeCmbTasa,
			msg : "Cargando..."
		});
		NS.storeCmbTasa.load();
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeCmbBanco,
			msg : "Cargando..."
		});
		NS.storeCmbBanco.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
		NS.storeCmbBanco.baseParams.psDivisa = NS.txtDivisaD.getValue();
		NS.storeCmbBanco.load();
	}
	NS.restaFechas = function(f1, f2) {
		var aFecha1 = f1.split('/');
		var aFecha2 = f2.split('/');
		var fFecha1 = Date.UTC(aFecha1[2], aFecha1[1] - 1, aFecha1[0]);
		var fFecha2 = Date.UTC(aFecha2[2], aFecha2[1] - 1, aFecha2[0]);
		var dif = fFecha2 - fFecha1;
		var dias = Math.floor(dif / (1000 * 60 * 60 * 24));
		return dias;
	}

	NS.restaFechas2 = function(f1, f2) {
		var aFecha1 = f1.split('-');
		var aFecha2 = f2.split('-');
		var fFecha1 = Date.UTC(aFecha1[0], aFecha1[1] - 1, aFecha1[2]);
		var fFecha2 = Date.UTC(aFecha2[0], aFecha2[1] - 1, aFecha2[2]);
		var dif = fFecha2 - fFecha1;
		var dias = Math.floor(dif / (1000 * 60 * 60 * 24));
		return dias;
	}



	NS.disposicion = function() {
		if (!NS.txtLinea.getValue() == "") {
			if (parseFloat(BFwrk.Util
					.unformatNumber(NS.txtMontoDisponible.getValue())) > 0) {
				NS.pbConsult = false;
				NS.cancelar();
				NS.editarDisposicion(false);
				// NS.cmdModificar.setDisabled(true);
				NS.cmdModificar1.setDisabled(true);
				NS.cmdEliminar1.setDisabled(false);
				NS.llenaCombosDis();
				NS.cmbFinanciamiento.setValue(NS.cmbContratos
						.getValue());
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeCmbTipoFinanDis,
					msg : "Cargando..."
				});
				NS.storeCmbTipoFinanDis.load();
				NS.txtTipoFinanDis.setValue('');
				NS.cmbTipoFinanDis.setValue('');
				NS.txtDivisaD.setValue(NS.txtDivisa.getValue());
				NS.cmbDivisaD.setValue(NS.cmbDivisa.getValue());
				NS.txtFecDisp.setValue(NS.txtFechaIni.getValue());
				NS.txtFecVenDisp.setValue(NS.txtFechaFin.getValue());
				NS.txtDias.setValue(NS.restaFechas(BFwrk.Util
						.changeDateToString(''
								+ NS.txtFechaIni.getValue()),
								BFwrk.Util.changeDateToString(''
										+ NS.txtFechaFin.getValue())));
				if (NS.cmbTasa.store.getCount() != 0) {
					NS.optTasa.setValue(1);
					NS.storeCmbTasa
					.load({
						callback : function(records, operation,
								success) {
							for (var i = 0; i <= records.length - 1; i++) {
								if (NS.cmbTasa.getValue() == records[i]
								.get('idStr')) {
									NS.cmbTasaBase
									.setValue(records[i]
									.get('idStr'));
									NS.cmbTasaBase
									.setRawValue(records[i]
									.get('descripcion'))
								}
							}
						}
					});
					NS.txtPuntos.setValue(NS.txtSpreed.getValue());
					NS.txtTasaVigente.setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(NS.txtBase.getValue()))
											+ parseFloat(BFwrk.Util.unformatNumber(NS.txtPuntos.getValue()))));
					NS.txtTasaFija.setValue("0.00000");
				} else {
					NS.optTasa.setValue(1);
					NS.txtPuntos.setValue("0.00000");
					NS.txtTasaVigente.setValue("0.00000");
				}
				NS.storeComboClabe.removeAll();
				NS.pvvValor2 = '';
				NS.psDivisa = '';
				if (NS.txtFideicomiso.getValue() != "") {
					NS.txtBancoDis.setValue(NS.txtFideicomiso
							.getValue());
					NS.cmbBancoDis.setValue(NS.txtFideicomiso
							.getValue());
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storeComboClabe,
						msg : "Cargando..."
					});
					NS.storeComboClabe.load({
						params : {
							pvvValor2 : NS.txtBancoDis.getValue(),
							psDivisa : NS.txtDivisaD.getValue(),
							noEmpresa:parseInt(NS.txtEmpresa.getValue())
						}
					});
				}
				NS.cmbEquivalencia.reset();
				var myMask = new Ext.LoadMask(Ext.getBody(), {
					store : NS.storeCmbEquivalencia,
					msg : "Cargando..."
				});
				NS.storeCmbEquivalencia.load({
					params : {

						piBanco : NS.txtBancoLin.getValue()
					},
					callback : function() {

					}
				});
			}
		}
	}
	// cmdDisposicion
	NS.cmdDisposicion = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdDisposicion',
		name : PF + 'cmdDisposicion',
		text : 'Disposición',
		x : 420,
		y : 255,
		disabled : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.disposicion();

				}
			}
		}
	});
	NS.ponerCeros = function(cadena, cant) {
		if (cant == 0) {
			return cadena;
		} else {
			var regla = "";
			for (var i = 0; i < cant; i++)
				regla += "0";
			fin = regla + cadena;
			return fin;
		}
	}
	NS.storeSelectDisposicionCred = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psIdContrato : '',
			piDisposicion : 0,
		},
		paramOrder : [ 'psIdContrato', 'piDisposicion' ],
		directFn : AltaFinanciamientoAction.selectDisposicionCred,
		idProperty : 'idFinanciamiento',
		fields : [ {
			name : 'idFinanciamiento'
		}, {
			name : 'montoDisposicion'
		}, {
			name : 'fecDisposicion'
		}, {
			name : 'fecVencimiento'
		}, {
			name : 'tipoTasa'
		}, {
			name : 'valorTasa'
		}

		],
		listeners : {
			load : function(s, records) {

			}
		}
	});
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
		               directFn : AltaFinanciamientoAction.selectAmortizaciones,
		               idProperty : 'idAmortizacion',
		               fields : [ {
		            	   name : 'idAmortizacion'
		               }, {
		            	   name : 'idContrato'
		               }, {
		            	   name : 'idDisposicion'
		               } ],
		               listeners : {
		            	   load : function(s, records) {
		            	   }
		               }
	});
	NS.storeSelectContratoCredito = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psIdContrato : ''
		},
		paramOrder : [ 'psIdContrato' ],
		directFn : AltaFinanciamientoAction.selectContratoCredito,
		idProperty : 'idFinanciamiento',
		fields : [ {
			name : 'idFinanciamiento'
		}, {
			name : 'idPais'
		}, {
			name : 'montoDisposicion'
		}, {
			name : 'capital'
		}, {
			name : 'revolvencia'
		}, {
			name : 'idBancoPrestamo'
		}, {
			name : 'idtipoFinanciamiento'
		}, {
			name : 'idRepresentante'
		}, {
			name : 'fecInicio'
		}, {
			name : 'fecVencimiento'
		}, {
			name : 'idDivisa'
		}, {
			name : 'montoAutorizado'
		}, {
			name : 'tasaLinea'
		}, {
			name : 'spreed'
		}, {
			name : 'reestructura'
		}, {
			name : 'largoPlazo'
		}, {
			name : 'fechaAntpost'
		}, {
			name : 'recfechaAntpost'
		}, {
			name : 'idBancoFideicomiso'
		}, {
			name : 'agente'
		} ],
		listeners : {
			load : function(s, records) {

			}
		}
	});
	NS.storeSelectConsecutivoLinea = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {},
		paramOrder : [],
		directFn : AltaFinanciamientoAction.selectConsecutivoLinea,
		idProperty : 'idFinanciamiento',
		fields : [ {
			name : 'idFinanciamiento'
		}, ],
		listeners : {
			load : function(s, records) {

			}
		}
	});
	NS.storeSelectAltaAmortizaciones = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			psIdContrato : ''
		},
		paramOrder : [ 'psIdContrato' ],
		directFn : AltaFinanciamientoAction.selectAltaAmortizaciones,
		idProperty : 'idContrato',
		fields : [ {
			name : 'idContrato'
		}, ],
		listeners : {
			load : function(s, records) {
			}
		}
	});
	NS.storeSelectPrefijo = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			bancoLin : 0,
		},
		paramOrder : [ 'bancoLin' ],
		directFn : AltaFinanciamientoAction.selectPrefijo,
		idProperty : 'prefijoBanco',
		fields : [ {
			name : 'prefijoBanco'
		}, ],
		listeners : {
			load : function(s, records) {
			}
		}
	});
	NS.creaLinea = function() {

		NS.storeSelectPrefijo.baseParams.bancoLin = NS.txtBancoLin
		.getValue();
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeSelectPrefijo,
			msg : "Cargando..."
		});
		NS.storeSelectPrefijo.load();
	}
	NS.validaDatos = function() {
		NS.band = false;
		if (NS.proyecto != 'CIE') {
			if (NS.txtLinea.getValue() == "") {
				Ext.Msg
				.alert('SET',
				'Escriba la identificación del financiamiento.');
				Ext.getCmp(PF + 'txtLinea').focus();
				NS.band = false;
			} else if (NS.txtBanco.getValue() == "") {
				Ext.Msg.alert('SET', 'Seleccione un banco');
				Ext.getCmp(PF + 'txtBanco').focus();
				NS.band = false;
			} else if (NS.cmbNoCuenta.getValue() == "") {
				Ext.Msg.alert('SET', 'Seleccione la clabe');
				Ext.getCmp(PF + 'cmbNoCuenta').focus();
				NS.band = false;
			} else {
				NS.band = true;
			}
		} else {
			NS.band = true;
		}
		if (NS.band) {
			if (NS.txtNoPais.getValue() == "") {
				Ext.Msg.alert('SET', 'Seleccione un país.');
				Ext.getCmp(PF + 'txtNoPais').focus();
				NS.band = false;
			} else if (NS.txtBancoLin.getValue() == "") {
				Ext.Msg.alert('SET', 'Seleccione un banco otorgante.');
				Ext.getCmp(PF + 'txtBancoLin').focus();
				NS.band = false;
			} else if (NS.txtTipoContrato.getValue() == "") {
				Ext.Msg.alert('SET', 'Seleccione un contrato.');
				Ext.getCmp(PF + 'txtTipoContrato').focus();
				NS.band = false;
			} else if (BFwrk.Util.changeDateToString(''
					+ NS.txtFechaIni.getValue()) == BFwrk.Util
					.changeDateToString('' + NS.txtFechaFin.getValue())) {
				BFwrk.Util
				.msgShow(
						'La fecha de vencimiento debe ser mayor a la de inicio.',
				'ERROR');
				NS.band = false;
				Ext.getCmp(PF + 'txtFechaFin').focus();
			} else if (NS.txtDivisa.getValue() == "") {
				Ext.Msg.alert('SET', 'Seleccione una divisa.');
				Ext.getCmp(PF + 'txtDivisa').focus();
				NS.band = false;
			} else if (BFwrk.Util.unformatNumber(NS.txtMontoAutorizado
					.getValue()) == "") {
				Ext.Msg.alert('SET', 'Escriba el monto autorizado.');
				Ext.getCmp(PF + 'txtMontoAutorizado').focus();
				NS.band = false;
			} else if (parseFloat(BFwrk.Util
					.unformatNumber(NS.txtMontoDispuesto.getValue()))
					+ parseFloat(BFwrk.Util
							.unformatNumber(NS.txtMontoDisponible
									.getValue())) > parseFloat(BFwrk.Util
											.unformatNumber(NS.txtMontoAutorizado.getValue()))) {
				Ext.Msg
				.alert('SET',
						'El Monto Dispuesto no debe ser mayor al autorizado.');
				NS.txtMontoDispuesto
				.setValue(BFwrk.Util
						.formatNumber(NS.txtMontoAutorizado
								.getValue()));
				Ext.getCmp(PF + 'txtMontoDispuesto').focus();
				NS.band = false;
			} else if (parseFloat(BFwrk.Util
					.unformatNumber(NS.txtMontoAutorizado.getValue())) <= 0) {
				Ext.Msg.alert('SET', 'Escriba el monto autorizado.');
				Ext.getCmp(PF + 'txtMontoAutorizado').focus();
				NS.band = false;
			} else if (NS.cmbTasa.getValue() == "") {
				Ext.Msg.alert('SET', 'Seleccione la tasa de la línea.');
				Ext.getCmp(PF + 'cmbTasa').focus();
				NS.band = false;
			} else {
				NS.band = true;
			}
		}
	}
	NS.modificarDatos = function() {
		NS.validaDatos();
		validaDatos = NS.band;
		if (validaDatos) {
			NS.storeSelectInhabil
			.load({
				params : {
					valorFecha : NS.txtFechaIni.getValue()
				},
				callback : function(records, operation, success) {
					if (records.length > 0) {
						BFwrk.Util
						.msgShow(
								'La fecha de inicio es inhábil.',
								'ERROR');
						NS.storeSelectInhabil.valorFecha = '';
						NS.valorFecha = '';
						Ext.getCmp(PF + 'txtFechaIni').focus();
						Ext.getCmp(PF + 'txtFechaIni')
						.setValue(NS.fecHoy);
					}
					NS.storeSelectInhabil.valorFecha = '';
					NS.valorFecha = '';
					NS.storeSelectInhabil
					.load({
						params : {
							valorFecha : NS.txtFechaFin
							.getValue()
						},
						callback : function(records,
								operation, success) {
							if (records.length > 0) {
								BFwrk.Util
								.msgShow(
										'La fecha de vencimiento es inhábil.',
										'ERROR');
								NS.storeSelectInhabil.valorFecha = '';
								NS.valorFecha = '';
								Ext
								.getCmp(
										PF
										+ 'txtFechaFin')
										.focus();
								Ext
								.getCmp(
										PF
										+ 'txtFechaFin')
										.setValue(
												NS.fecHoy);
							}
							NS.storeSelectInhabil.valorFecha = '';
							NS.valorFecha = '';
							NS.storeSelectContratoCredito
							.load({
								params : {
									psIdContrato : NS.txtLinea
									.getValue()
								},
								callback : function(
										records,
										operation,
										success) {
									if (records.length > 0) {
										NS.fechaAntpost = records[0]
										.get('fechaAntpost');
										NS.recFechaAntPost = records[0]
										.get('recfechaAntpost');
										NS.idPaisC = records[0]
										.get('idPais');
										NS.idDivisaC = records[0]
										.get('idDivisa');
										NS.piDisposicion = 0;

										if (records.length > 0) {
											NS.storeSelectDisposicionCred
											.load({
												params : {
													psIdContrato : NS.txtLinea
													.getValue(),
													piDisposicion : NS.piDisposicion
												},
												callback : function(
														records,
														operation,
														success) {
													NS.vsTipoOperac = '';
													if (NS.optOp == '0')
														NS.vsTipoOperac = "C";
													if (NS.optOp == '1')
														NS.vsTipoOperac = "S";
													if (NS.optOp == '2')
														NS.vsTipoOperac = "A";
													if (NS.chkRevolvencia
															.getValue() == true)
														NS.vsRevolven = "S";
													if (NS.chkRevolvencia
															.getValue() == false)
														NS.vsRevolven = "N";
													if (NS.chkLargoPlazo
															.getValue() == true)
														NS.vsLargoPlazo = "S";
													if (NS.chkLargoPlazo
															.getValue() == false)
														NS.vsLargoPlazo = "N";
													if (NS.oCalculoI == '0')
														NS.vsRecorreFecha = "E";
													if (NS.oCalculoI == '1')
														NS.vsRecorreFecha = "A";
													if (NS.oCalculoI == '2')
														NS.vsRecorreFecha = "P";
													if (NS.oPagoI == '0')
														NS.vsRecSoloFecha = "A";
													if (NS.oPagoI == '1')
														NS.vsRecSoloFecha = "P";
													NS.vsTasa = NS.cmbTasa
													.getValue();
													if (records.length > 0) {
														if ((NS.fechaAntPost != NS.recorreFecha)
																|| (NS.recFechaAntPost != NS.vsRecSoloFecha)) {
															NS.contrato = NS.txtLinea
															.getValue();
															NS.storeSelectAltaAmortizaciones
															.load({
																params : {
																	psIdContrato : NS.contrato
																},
																callback : function(
																		records,
																		operation,
																		success) {
																	if (records.length >= 0) {
																		Ext.Msg
																		.alert(
																				'SET',
																				'Línea con tabla de amortizaciones, modificación no permitida.');
																		// Limpia
																		NS
																		.limpiaCredito();
																		// cmbContratos_click
																		NS.optCalculoInt
																		.setDisabled(true);
																		if (NS.cmbContratos
																				.getSize() == 0) {
																			return;
																		}
																		NS.storeDatos.baseParams.clave = NS.cmbContratos
																		.getValue();
																		var myMask = new Ext.LoadMask(
																				Ext
																				.getBody(),
																				{
																					store : NS.storeDatos,
																					msg : "Cargando datos..."
																				});
																		NS.storeDatos
																		.load();
																		// termina
																		// cmbContratos_click
																		NS
																		.editarAltaContrato(true);
																		// termina
																		// Limpia
																	}
																	if (NS.proyecto != 'CIE'
																		&& (NS.txtNoPais
																				.getValue() != NS.idPaisC)
																				|| (NS.txtDivisa
																						.getValue() != NS.idDivisaC)
																						|| NS.proyecto == 'CIE') {
																		Ext.Msg
																		.alert(
																				'SET',
																		'Línea con disposiciones, modificación no permitida.');
																		// LIMPIA
																		NS
																		.limpiaCredito();
																		// cmbContratos_click
																		NS.optCalculoInt
																		.setDisabled(true);
																		if (NS.cmbContratos.getSize == 0) {
																			return;
																		}
																		NS.storeDatos.baseParams.clave = NS.cmbContratos
																		.getValue();
																		var myMask = new Ext.LoadMask(
																				Ext
																				.getBody(),
																				{
																					store : NS.storeDatos,
																					msg : "Cargando datos..."
																				});
																		NS.storeDatos
																		.load();
																		// termina
																		// cmbContratos_click
																		NS
																		.editarAltaContrato(true);
																		// termina
																		// Limpia
																	}
																}
															});
														}
													}
													NS.MB2 = Ext.MessageBox;
													Ext
													.apply(
															NS.MB2,
															{
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
															' ¿Desea modificar este Registro?',
															function(
																	btn) {
																if (btn === 'yes') {

																	NS.txtMontoDisponible
																	.setValue(BFwrk.Util
																			.formatNumber(parseFloat(BFwrk.Util
																					.unformatNumber(NS.txtMontoAutorizado
																							.getValue()))
																							- (parseFloat(BFwrk.Util
																									.unformatNumber(NS.txtMontoDispuesto
																											.getValue())))));
																	if (NS.chkReestructura
																			.getValue() == true
																			&& (NS.txtFideicomiso
																					.getValue() == "" || NS.txtAgente
																					.getValue() == "")) {
																		Ext.Msg
																		.alert(
																				'SET',
																		'Seleccione el fideicomiso y banco agente.');
																	}
																	if (!NS.txtBanco
																			.getValue() == ''
																				|| !NS.txtBanco
																				.getValue() == null)
																		NS.piBanco = parseInt(NS.txtBanco
																				.getValue());
																	else
																		NS.piBanco = 0;
																	if (!NS.txtTipoContrato
																			.getValue() == ''
																				|| !NS.txtTipoContrato
																				.getValue() == null)
																		NS.piTipoFinancia = NS.txtTipoContrato
																		.getValue();
																	else
																		NS.piTipoFinancia = 0;
																	if (!NS.txtComun
																			.getValue() == ''
																				|| !NS.txtComun
																				.getValue() == null)
																		NS.piComun = parseInt(NS.txtComun
																				.getValue());
																	else
																		NS.piComun = 0;
																	if (BFwrk.Util
																			.unformatNumber(NS.txtMontoAutorizado
																					.getValue()) > 0)
																		NS.pdMontoAuto = parseFloat(BFwrk.Util
																				.unformatNumber(NS.txtMontoAutorizado
																						.getValue()));
																	else
																		NS.pdMontoAuto = 0.0;
																	if (BFwrk.Util
																			.unformatNumber(NS.txtMontoDispuesto
																					.getValue()) > 0)
																		NS.pdMontoDisp = parseFloat(BFwrk.Util
																				.unformatNumber(NS.txtMontoDispuesto
																						.getValue()));
																	else
																		NS.pdMontoDisp = 0.0;
																	if (BFwrk.Util
																			.unformatNumber(NS.txtSpreed
																					.getValue()) > 0)
																		NS.pdSpreed = parseFloat(BFwrk.Util
																				.unformatNumber(NS.txtSpreed
																						.getValue()));
																	else
																		NS.pdSpreed = 0.0;
																	if (!NS.txtBancoLin
																			.getValue() == ''
																			|| !NS.txtBancoLin
																			.getValue() == null)
																		NS.piBancoLin = NS.txtBancoLin
																		.getValue();
																	else
																		NS.piBancoLin = 0;
																	if (!NS.txtFideicomiso
																			.getValue() == ''
																				|| !NS.txtFideicomiso
																				.getValue() == null)
																		NS.piFideicomiso = parseInt(NS.txtFideicomiso
																				.getValue());
																	else
																		NS.piFideicomiso = 0;
																	if (!NS.txtAgente
																			.getValue() == ''
																				|| !NS.txtAgente
																				.getValue() == null)
																		NS.piAgente = parseInt(NS.txtAgente
																				.getValue());
																	else
																		NS.piAgente = 0;
																	if (NS.chkReestructura
																			.getValue())
																		NS.piReestructura = '1';
																	else
																		NS.piReestructura = '0';
																	NS.fechaI = BFwrk.Util
																	.changeDateToString(''
																			+ Ext
																			.getCmp(
																					PF
																					+ 'txtFechaIni')
																					.getValue());
																	NS.fechaF = BFwrk.Util
																	.changeDateToString(''
																			+ Ext
																			.getCmp(
																					PF
																					+ 'txtFechaFin')
																					.getValue());

																	var terminado = false;
																	AltaFinanciamientoAction
																	.updateLinea(
																			NS.txtLinea
																			.getValue(),
																			NS.txtNoPais
																			.getValue(),
																			NS.piBanco,
																			NS.cmbNoCuenta
																			.getValue(),
																			NS.piTipoFinancia,
																			NS.piComun,
																			NS.fechaI,
																			NS.fechaF,
																			NS.txtDivisa
																			.getValue(),
																			NS.pdMontoAuto,
																			NS.pdMontoDisp,
																			NS.vsTasa,
																			NS.vsTipoOperac,
																			NS.pdSpreed,
																			NS.vsRevolven,
																			NS.piBancoLin,
																			NS.vsRecorreFecha,
																			NS.vsLargoPlazo,
																			NS.piFideicomiso,
																			NS.piAgente,
																			NS.vsRecSoloFecha,
																			NS.piReestructura,
																			function(
																					mapResult,
																					e) {
																				BFwrk.Util
																				.msgWait(
																						'Terminado...',
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
																				}
																				if (mapResult.result > 0) {
																					NS.vsLin = NS.txtLinea
																					.getValue();
																					NS.cmbContratos
																					.reset();
																					NS
																					.limpiaCredito();
																					NS.cmbContratos
																					.setValue(NS.vsLin);
																					NS
																					.editarAltaContrato(true);
																					NS.cmbContratos
																					.setValue(NS.vsLin);
																					BFwrk.Util
																					.updateComboToTextField(
																							PF
																							+ 'txtLinea',
																							NS.cmbContratos
																							.getId());
																					NS
																					.editarAltaContrato(true);
																					NS.storeDatos.baseParams.clave = NS.cmbContratos
																					.getValue();
																					var myMask = new Ext.LoadMask(
																							Ext
																							.getBody(),
																							{
																								store : NS.storeDatos,
																								msg : "Cargando..."
																							});
																					NS.storeDatos
																					.load();
																				}
																			});//
																}
															});
												}
											});
										}
									}
								}
							});

						}
					});

				}
			});
		}
	}

	NS.aceptar = function() {
		NS.validaDatos();
		validaDatos = NS.band;
		if (validaDatos) {
			NS.storeSelectInhabil
			.load({
				params : {
					valorFecha : NS.txtFechaIni.getValue()
				},
				callback : function(records, operation, success) {
					if (records.length > 0) {
						BFwrk.Util
						.msgShow(
								'La fecha de inicio es inhábil.',
								'ERROR');
						NS.storeSelectInhabil.valorFecha = '';
						NS.valorFecha = '';
						Ext.getCmp(PF + 'txtFechaIni').focus();
						Ext.getCmp(PF + 'txtFechaIni')
						.setValue(NS.fecHoy);
					}
					NS.storeSelectInhabil.valorFecha = '';
					NS.valorFecha = '';
					NS.storeSelectInhabil
					.load({
						params : {
							valorFecha : NS.txtFechaFin
							.getValue()
						},
						callback : function(records,
								operation, success) {
							if (records.length > 0) {
								BFwrk.Util
								.msgShow(
										'La fecha de vencimiento es inhábil.',
										'ERROR');
								NS.storeSelectInhabil.valorFecha = '';
								NS.valorFecha = '';
								Ext
								.getCmp(
										PF
										+ 'txtFechaFin')
										.focus();
								Ext
								.getCmp(
										PF
										+ 'txtFechaFin')
										.setValue(
												NS.fecHoy);
							}
							NS.storeSelectInhabil.valorFecha = '';
							NS.valorFecha = '';
							if (NS.proyecto == 'CIE'
								&& !NS.pbModificar) {
								// creaLinea()
								NS.storeSelectPrefijo
								.load({
									params : {
										bancoLin : NS.txtBancoLin
										.getValue()
									},
									callback : function(
											records,
											operation,
											success) {
										if (records.length <= 0) {
											Ext.Msg
											.alert(
													'SET',
													'El prefijo del banco otorgante no existe');
										} else {
											NS.vsMenu = '';
											if (NS.chkReestructura
													.getValue() == true)
												NS.vsTipoLin = "R";
											else {
												if (NS.chkLargoPlazo
														.getValue() == false)
													NS.vsTipoLin = "C";
												else
													NS.vsTipoLin = "L";
											}
											NS.vsMenu = "C";
											NS.vsPrefijo = records[0]
											.get('prefijoBanco');
											// consecutivo
											NS.storeSelectConsecutivoLinea
											.load({
												callback : function(
														records,
														operation,
														success) {
													if (parseInt(records[0]
													.get('idFinanciamiento')) > 0) {
														NS.vsConsecutivo = records[0]
														.get('idFinanciamiento');
														var cadena = parseInt(records[0]
														.get('idFinanciamiento')) + 1;
														var faltan = 5 - (cadena
																.toString().length);
														var fin = NS
														.ponerCeros(
																cadena
																.toString(),
																faltan);
														NS.vsConsecutivo = fin;
													}
													if (parseInt(records[0]
													.get('idFinanciamiento')) <= 0
													|| records[0]
													.get('idFinanciamiento') == '') {
														var con = "00001";
														NS.vsConsecutivo = con;
													}
													NS.vsLinea = NS.vsMenu
													+ NS.vsPrefijo
													+ NS.vsTipoLin
													+ NS.vsConsecutivo;
													NS.txtLinea
													.setValue(NS.vsLinea);
													NS.storeSelectContratoCredito
													.load({
														params : {
															psIdContrato : NS.txtLinea
															.getValue()
														},
														callback : function(
																records,
																operation,
																success) {
															if (records.length <= 0) {
																NS.vsTipoOpe = "";
																if (NS.chkRevolvencia
																		.getValue() == true)
																	NS.vsRevolven = "S";
																if (NS.chkRevolvencia
																		.getValue() == false)
																	NS.vsRevolven = "N";
																if (NS.chkLargoPlazo
																		.getValue() == true)
																	NS.vsPlazoLargo = "S";
																if (NS.chkLargoPlazo
																		.getValue() == false)
																	NS.vsPlazoLargo = "N";
																if (NS.calculoInt == 0)
																	NS.vsRecorreFecha = "E";
																if (NS.calculoInt == 1)
																	NS.vsRecorreFecha = "A";
																if (NS.calculoInt == 2)
																	NS.vsRecorreFecha = "P";
																if (NS.pagoInt == 0)
																	NS.vsRecSoloFecha = "A";
																if (NS.pagoInt == 1)
																	NS.vsRecSoloFecha = "P";
																NS.vsTasa = NS.cmbTasa
																.getValue();
																if (!NS.txtBanco
																		.getValue() == ''
																			|| !NS.txtBanco
																			.getValue() == null)
																	NS.piBanco = parseInt(NS.txtBanco
																			.getValue());
																else
																	NS.piBanco = 0;
																if (!NS.txtTipoContrato
																		.getValue() == ''
																			|| !NS.txtTipoContrato
																			.getValue() == null)
																	NS.piTipoFinancia = NS.txtTipoContrato
																	.getValue();
																else
																	NS.piTipoFinancia = 0;
																if (!NS.txtComun
																		.getValue() == ''
																			|| !NS.txtComun
																			.getValue() == null)
																	NS.piComun = parseInt(NS.txtComun
																			.getValue());
																else
																	NS.piComun = 0;
																if (BFwrk.Util
																		.unformatNumber(NS.txtMontoAutorizado
																				.getValue()) > 0)
																	NS.pdMontoAuto = parseFloat(BFwrk.Util
																			.unformatNumber(NS.txtMontoAutorizado
																					.getValue()));
																else
																	NS.pdMontoAuto = 0.0;
																if (BFwrk.Util
																		.unformatNumber(NS.txtMontoDispuesto
																				.getValue()) > 0)
																	NS.pdMontoDisp = parseFloat(BFwrk.Util
																			.unformatNumber(NS.txtMontoDispuesto
																					.getValue()));
																else
																	NS.pdMontoDisp = 0.0;
																if (BFwrk.Util
																		.unformatNumber(NS.txtSpreed
																				.getValue()) > 0)
																	NS.pdSpreed = parseFloat(BFwrk.Util
																			.unformatNumber(NS.txtSpreed
																					.getValue()));
																else
																	NS.pdSpreed = 0.0;
																if (!NS.txtBancoLin
																		.getValue() == ''
																		|| !NS.txtBancoLin
																		.getValue() == null)
																	NS.piBancoLin = NS.txtBancoLin
																	.getValue();
																else
																	NS.piBancoLin = 0;
																if (!NS.txtFideicomiso
																		.getValue() == ''
																			|| !NS.txtFideicomiso
																			.getValue() == null)
																	NS.piFideicomiso = parseInt(NS.txtFideicomiso
																			.getValue());
																else
																	NS.piFideicomiso = 0;
																if (!NS.txtAgente
																		.getValue() == ''
																			|| !NS.txtAgente
																			.getValue() == null)
																	NS.piAgente = parseInt(NS.txtAgente
																			.getValue());
																else
																	NS.piAgente = 0;
																if (NS.chkReestructura
																		.getValue())
																	NS.piReestructura = '1';
																else
																	NS.piReestructura = '0';
																NS.fechaI = BFwrk.Util
																.changeDateToString(''
																		+ Ext
																		.getCmp(
																				PF
																				+ 'txtFechaIni')
																				.getValue());
																NS.fechaF = BFwrk.Util
																.changeDateToString(''
																		+ Ext
																		.getCmp(
																				PF
																				+ 'txtFechaFin')
																				.getValue());
																var terminado = false;
																AltaFinanciamientoAction
																.altaContrato(
																		NS.txtLinea
																		.getValue(),
																		NS.txtNoPais
																		.getValue(),
																		NS.piBanco,
																		NS.cmbNoCuenta
																		.getValue(),
																		NS.piTipoFinancia,
																		NS.piComun,
																		NS.fechaI,
																		NS.fechaF,
																		NS.txtDivisa
																		.getValue(),
																		NS.pdMontoAuto,
																		NS.pdMontoDisp,
																		NS.vsTasa,
																		NS.vsTipoOpe,
																		"",
																		"",
																		NS.pdSpreed,
																		NS.vsRevolven,
																		"A",
																		NS.piBancoLin,
																		NS.vsRecorreFecha,
																		NS.vsPlazoLargo,
																		NS.vsRecSoloFecha,
																		NS.piFideicomiso,
																		NS.piAgente,
																		NS.piReestructura,
																		parseInt(NS.txtEmpresa.getValue()),
																		function(
																				mapResult,
																				e) {
																			BFwrk.Util
																			.msgWait(
																					'Terminado...',
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
																			}
																			if (mapResult.result > 0) {
																				NS.vsLinea = NS.txtLinea
																				.getValue();
																				NS.cmbContratos
																				.reset();
																				var myMask = new Ext.LoadMask(
																						Ext
																						.getBody(),
																						{
																							store : NS.storeCmbContratos,
																							msg : "Cargando..."
																						});
																				NS.storeCmbContratos.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
																				NS.storeCmbContratos
																				.load();
																				NS
																				.limpiaCredito();
																				NS.cmbContratos
																				.setValue(NS.vsLinea);
																				BFwrk.Util
																				.updateComboToTextField(
																						PF
																						+ 'txtLinea',
																						NS.cmbContratos
																						.getId());
																				NS
																				.editarAltaContrato(true);
																				NS.storeDatos.baseParams.clave = NS.cmbContratos
																				.getValue();
																				var myMask = new Ext.LoadMask(
																						Ext
																						.getBody(),
																						{
																							store : NS.storeDatos,
																							msg : "Cargando..."
																						});
																				NS.storeDatos
																				.load();
																			}
																		});
															} else {
																Ext.Msg
																.alert(
																		'Información SET',
																		''
																		+ 'La Línea de Crédito ya fue dada de alta.');
															}
														}
													});
												}
											});
										}
									}
								});
							} else {
								NS.storeSelectContratoCredito
								.load({
									params : {
										psIdContrato : NS.txtLinea
										.getValue()
									},
									callback : function(
											records,
											operation,
											success) {
										if (records.length <= 0) {
											NS.vsTipoOpe = "";
											if (NS.chkRevolvencia
													.getValue() == true)
												NS.vsRevolven = "S";
											if (NS.chkRevolvencia
													.getValue() == false)
												NS.vsRevolven = "N";
											if (NS.chkLargoPlazo
													.getValue() == true)
												NS.vsPlazoLargo = "S";
											if (NS.chkLargoPlazo
													.getValue() == false)
												NS.vsPlazoLargo = "N";
											if (NS.calculoInt == 0)
												NS.vsRecorreFecha = "E";
											if (NS.calculoInt == 1)
												NS.vsRecorreFecha = "A";
											if (NS.calculoInt == 2)
												NS.vsRecorreFecha = "P";
											if (NS.pagoInt == 0)
												NS.vsRecSoloFecha = "A";
											if (NS.pagoInt == 1)
												NS.vsRecSoloFecha = "P";
											NS.vsTasa = NS.cmbTasa
											.getValue();
											if (!NS.txtBanco
													.getValue() == ''
														|| !NS.txtBanco
														.getValue() == null)
												NS.piBanco = parseInt(NS.txtBanco
														.getValue());
											else
												NS.piBanco = 0;
											if (!NS.txtTipoContrato
													.getValue() == ''
														|| !NS.txtTipoContrato
														.getValue() == null)
												NS.piTipoFinancia = NS.txtTipoContrato
												.getValue();
											else
												NS.piTipoFinancia = 0;
											if (!NS.txtComun
													.getValue() == ''
														|| !NS.txtComun
														.getValue() == null)
												NS.piComun = parseInt(NS.txtComun
														.getValue());
											else
												NS.piComun = 0;
											if (BFwrk.Util
													.unformatNumber(NS.txtMontoAutorizado
															.getValue()) > 0)
												NS.pdMontoAuto = parseFloat(BFwrk.Util
														.unformatNumber(NS.txtMontoAutorizado
																.getValue()));
											else
												NS.pdMontoAuto = 0.0;
											if (BFwrk.Util
													.unformatNumber(NS.txtMontoDispuesto
															.getValue()) > 0)
												NS.pdMontoDisp = parseFloat(BFwrk.Util
														.unformatNumber(NS.txtMontoDispuesto
																.getValue()));
											else
												NS.pdMontoDisp = 0.0;
											if (BFwrk.Util
													.unformatNumber(NS.txtSpreed
															.getValue()) > 0)
												NS.pdSpreed = parseFloat(BFwrk.Util
														.unformatNumber(NS.txtSpreed
																.getValue()));
											else
												NS.pdSpreed = 0.0;
											if (!NS.txtBancoLin
													.getValue() == ''
													|| !NS.txtBancoLin
													.getValue() == null)
												NS.piBancoLin = NS.txtBancoLin
												.getValue();
											else
												NS.piBancoLin = 0;
											if (!NS.txtFideicomiso
													.getValue() == ''
														|| !NS.txtFideicomiso
														.getValue() == null)
												NS.piFideicomiso = parseInt(NS.txtFideicomiso
														.getValue());
											else
												NS.piFideicomiso = 0;
											if (!NS.txtAgente
													.getValue() == ''
														|| !NS.txtAgente
														.getValue() == null)
												NS.piAgente = parseInt(NS.txtAgente
														.getValue());
											else
												NS.piAgente = 0;
											if (NS.chkReestructura
													.getValue())
												NS.piReestructura = '1';
											else
												NS.piReestructura = '0';
											NS.fechaI = BFwrk.Util
											.changeDateToString(''
													+ Ext
													.getCmp(
															PF
															+ 'txtFechaIni')
															.getValue());
											NS.fechaF = BFwrk.Util
											.changeDateToString(''
													+ Ext
													.getCmp(
															PF
															+ 'txtFechaFin')
															.getValue());
											var terminado = false;
											AltaFinanciamientoAction
											.altaContrato(
													NS.txtLinea
													.getValue(),
													NS.txtNoPais
													.getValue(),
													NS.piBanco,
													NS.cmbNoCuenta
													.getValue(),
													NS.piTipoFinancia,
													NS.piComun,
													NS.fechaI,
													NS.fechaF,
													NS.txtDivisa
													.getValue(),
													NS.pdMontoAuto,
													NS.pdMontoDisp,
													NS.vsTasa,
													NS.vsTipoOpe,
													"",
													"",
													NS.pdSpreed,
													NS.vsRevolven,
													"A",
													NS.piBancoLin,
													NS.vsRecorreFecha,
													NS.vsPlazoLargo,
													NS.vsRecSoloFecha,
													NS.piFideicomiso,
													NS.piAgente,
													NS.piReestructura,
													parseInt(NS.txtEmpresa.getValue()),
													function(
															mapResult,
															e) {

														BFwrk.Util
														.msgWait(
																'Terminado...',
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
														}
														if (mapResult.result > 0) {
															NS.vsLinea = NS.txtLinea
															.getValue();
															NS.cmbContratos
															.reset();
															var myMask = new Ext.LoadMask(
																	Ext
																	.getBody(),
																	{
																		store : NS.storeCmbContratos,
																		msg : "Cargando..."
																	});
															NS.storeCmbContratos.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
															NS.storeCmbContratos
															.load();
															NS
															.limpiaCredito();
															NS.cmbContratos
															.setValue(NS.vsLinea);
															BFwrk.Util
															.updateComboToTextField(
																	PF
																	+ 'txtLinea',
																	NS.cmbContratos
																	.getId());
															NS
															.editarAltaContrato(true);
															NS.storeDatos.baseParams.clave = NS.cmbContratos
															.getValue();
															var myMask = new Ext.LoadMask(
																	Ext
																	.getBody(),
																	{
																		store : NS.storeDatos,
																		msg : "Cargando..."
																	});
															NS.storeDatos
															.load();
														}
													});
										} else {
											Ext.Msg
											.alert(
													'Información SET',
													''
													+ 'La Línea de Crédito ya fue dada de alta.');
										}
									}
								});
							}
						}
					});
				}
			});
		}
	}
	// cmdAceptar

	NS.cmdAceptar = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAceptar',
		name : PF + 'cmdAceptar',
		text : 'Aceptar',
		x : 510,
		disabled : true,
		y : 255,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					if (NS.pbModificar == false)
						NS.aceptar();
					if (NS.pbModificar == true)
						NS.modificarDatos();
				}
			}
		}
	});
	NS.modificar = function() {
		NS.pbModificar = true;
		NS.editarAltaContrato(false);
		NS.txtLinea.setDisabled(true);
		NS.cmbDisp.setDisabled(true);
		NS.optPagoInt.setDisabled(true);
		NS.optPagoInt.setDisabled(true);
		NS.cmbPais.setDisabled(true);
		NS.txtNoPais.setDisabled(true);
		NS.txtTipoContrato.setDisabled(true);
		NS.cmbTipoContrato.setDisabled(true);
		NS.txtFechaIni.setDisabled(true);
		NS.txtDivisa.setDisabled(true);
		NS.cmbDivisa.setDisabled(true);
		NS.txtBancoLin.setDisabled(true);
		NS.cmbBancoLin.setDisabled(true);
		NS.chkReestructura.setDisabled(true);
		NS.chkLargoPlazo.setDisabled(true);
	};
	// Modificar
	NS.cmdModificar = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdModificar',
		name : PF + 'cmdModificar',
		text : 'Modificar',
		x : 600,
		y : 255,
		width : 80,
		disabled : true,
		listeners : {
			click : {
				fn : function(e) {
					NS.modificar();

				}
			}
		}
	});
	// Eliminar
	NS.storeExisteDispAmort = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			idLinea : '',
		},
		paramOrder : [ 'idLinea' ],
		directFn : AltaFinanciamientoAction.selectExisteDispAmort,
		idProperty : 'idFinanciamiento',
		fields : [ {
			name : 'idFinanciamiento'
		}, ],
		listeners : {
			load : function(s, records) {

			}
		}
	});
	NS.eliminar = function() {
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
				'Se va a eliminar la línea. ¿Desea continuar?',
				function(btn) {
					if (btn === 'yes') {

						NS.idLinea = NS.txtLinea.getValue();
						NS.storeExisteDispAmort
						.load({
							params : {
								idLinea : NS.txtLinea
								.getValue(),
							},
							callback : function(
									records, operation,
									success) {
								if (records.length > 0) {
									Ext.Msg
									.alert(
											'SET',
											'No se puede eliminar la línea, tiene disposiciones registradas.');
								} else {
									NS.cancelar();
									NS.limpiar();
									NS.cmdRenovacion
									.setDisabled(true);
									NS.cmbContratos
									.reset();
									NS.storeCmbContratos.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
									NS.storeCmbContratos
									.load();
									var terminado = false;
									AltaFinanciamientoAction
									.deleteDispAmortizacion(
											NS.idLinea,
											function(
													mapResult,
													e) {
												BFwrk.Util
												.msgWait(
														'Terminado...',
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
													NS.storeCmbContratos.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
													NS.storeCmbContratos
													.load();
												}
											});
								}
							}
						});
					}
				});
	}
	NS.cmdEliminar = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdEliminar',
		name : PF + 'cmdEliminar',
		text : 'Eliminar',
		x : 690,
		y : 255,
		width : 80,
		disabled : true,
		listeners : {
			click : {
				fn : function(e) {
					NS.eliminar();
				}
			}
		}
	});
	// Imprimir
	NS.cmdImprimir = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdImprimir',
		name : PF + 'cmdImprimir',
		text : 'Imprimir',
		x : 780,
		y : 255,
		width : 80,
		disabled : true,
		listeners : {
			click : {
				fn : function(e) {
					if (NS.cmbContratos.getValue() == "") {
						Ext.Msg.alert('SET', 'Seleccione un contrato');
						NS.cmbContratos.focus();
						return;
					}
					var strParams = '';
					strParams = '?nomReporte=ReporteContratoCredito';
					strParams += '&' + 'idFinanciamiento='
					+ NS.cmbContratos.getValue();
					window.open("/SET/jsp/Reportes.jsp" + strParams);
				}
			}
		}
	});
	// Limpiar
	NS.cmdLimpiar = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdLimpiar',
		name : PF + 'cmdLimpiar',
		text : 'Limpiar',
		x : 870,
		y : 255,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.limpiar();
				}
			}
		}
	});
	// *************************************************************************linea
	NS.lblLinea3 = new Ext.form.Label({
		text : 'Línea:',
		x : 0,
		y : -1
	});
	NS.cmbFinanciamiento = new Ext.form.ComboBox({
		store : NS.storeCmbContratos,
		name : PF + 'cmbFinanciamiento',
		id : PF + 'cmbFinanciamiento',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 0,
		y : 13,
		width : 140,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : '',
		triggerAction : 'all',
		disabled : true,
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {
				}
			}
		}
	});
	// Destino de Recursos
	NS.storeCmbTipoFinanDis = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		directFn : AltaFinanciamientoAction.obtenerDestinoRecursos,
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
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.CmbTipoFinanDis,
		msg : "Cargando..."
	});
	NS.storeCmbTipoFinanDis.load();
	NS.lblTipoFinanDis = new Ext.form.Label({
		text : 'Destino de Recursos:',
		x : 160,
		y : -1,

	});
	NS.txtTipoFinanDis = new Ext.form.TextField({
		id : PF + 'txtTipoFinanDis',
		name : PF + 'txtTipoFinanDis',
		value : '',
		x : 160,
		y : 13,
		width : 35,
		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util.updateTextFieldToCombo(
							PF + 'txtTipoFinanDis', NS.cmbTipoFinanDis
							.getId());
				}
			}
		}
	});
	NS.cmbTipoFinanDis = new Ext.form.ComboBox({
		store : NS.storeCmbTipoFinanDis,
		name : PF + 'cmbTipoFinanDis',
		id : PF + 'cmbTipoFinanDis',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 200,
		y : 13,
		width : 140,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione destino de recursos',
		triggerAction : 'all',
		value : '',
		disabled : true,
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF
							+ 'txtTipoFinanDis', NS.cmbTipoFinanDis
							.getId());
					// /NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	// monto disposicion
	NS.lblMontoDisposicion = new Ext.form.Label({
		text : 'Monto disposición:',
		x : 550,
		y : -1,
	});
	NS.txtMontoDisposicion = new Ext.form.TextField({
		id : PF + 'txtMontoDisposicion',
		name : PF + 'txtMontoDisposicion',
		disabled : true,
		x : 550,
		y : 13,
		value : "0.00",
		width : 100,
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtMontoDisposicion').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	// divisa
	NS.lblDivisaD = new Ext.form.Label({
		text : 'Divisa Pago:',
		x : 360,
		y : -1,

	});
	NS.txtDivisaD = new Ext.form.TextField({
		id : PF + 'txtDivisaD',
		name : PF + 'txtDivisaD',
		value : '',
		x : 360,
		y : 13,
		disabled : true,
		width : 35,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util.updateTextFieldToCombo(
							PF + 'txtDivisaD', NS.cmbDivisaD.getId());
				}
			}
		}
	});
	NS.cmbDivisaD = new Ext.form.ComboBox({
		store : NS.storeCmbDivisa,
		name : PF + 'cmbDivisaD',
		id : PF + 'cmbDivisaD',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 400,
		y : 13,
		width : 140,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una divisa',
		triggerAction : 'all',
		value : '',
		disabled : true,
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(
							PF + 'txtDivisaD', NS.cmbDivisaD.getId());
				}
			}
		}
	});
	// fecha inicio
	NS.lblFecDisp = new Ext.form.Label({
		text : 'Fecha Inicio:',
		x : 665,
		y : -1
	});
	NS.txtFecDisp = new Ext.form.DateField(
			{
				id : PF + 'txtFecDisp',
				name : PF + 'txtFecDisp',
				format : 'd/m/Y',
				disabled : true,
				x : 665,
				y : 13,
				width : 100,
				vtype : 'daterange',
				listeners : {
					render : function(datefield) {
						datefield.setValue(NS.fecHoy);
					},
					change : {
						fn : function(caja, valor) {
							var fechaFin = Ext.getCmp(
									PF + 'txtFechaIni').getValue();
							if (caja.getValue() == null) {
								Ext.getCmp(PF + 'txtFecDisp').setValue(
										Ext.getCmp(PF + 'txtFechaIni')
										.getValue());
								Ext.getCmp(PF + 'txtFecDisp').focus();
							} else {
								if (fechaFin > caja.getValue()) {
									BFwrk.Util
									.msgShow(
											'La fecha de disposición no está dentro del rango de la línea.',
									'ERROR');
									Ext.getCmp(PF + 'txtFecDisp')
									.focus();
									Ext
									.getCmp(PF + 'txtFecDisp')
									.setValue(
											Ext
											.getCmp(
													PF
													+ 'txtFechaIni')
													.getValue());
								} else {
									diaSemana = caja.getValue()
									.getDay();

									if (diaSemana == "0") {
										BFwrk.Util
										.msgShow(
												'La fecha de Disposición es inhábil.',
										'ERROR');
										Ext.getCmp(PF + 'txtFecDisp')
										.focus();
										Ext
										.getCmp(
												PF
												+ 'txtFecDisp')
												.setValue(
														Ext
														.getCmp(
																PF
																+ 'txtFechaIni')
																.getValue());

									} else if (diaSemana == "6") {
										BFwrk.Util
										.msgShow(
												'La fecha de Disposición es inhábil.',
										'ERROR');
										Ext.getCmp(PF + 'txtFecDisp')
										.focus();
										Ext
										.getCmp(
												PF
												+ 'txtFecDisp')
												.setValue(
														Ext
														.getCmp(
																PF
																+ 'txtFechaIni')
																.getValue());
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
													.msgShow(
															'La fecha de Disposición es inhábil.',
															'ERROR');
													NS.storeSelectInhabil.valorFecha = '';
													NS.valorFecha = '';
													Ext
													.getCmp(
															PF
															+ 'txtFecDisp')
															.focus();
													Ext
													.getCmp(
															PF
															+ 'txtFecDisp')
															.setValue(
																	Ext
																	.getCmp(
																			PF
																			+ 'txtFechaIni')
																			.getValue());

												}
												NS.storeSelectInhabil.valorFecha = '';
												NS.valorFecha = '';
											}
										});
									}
								}
							}
						}
					},
					blur : function(datefield) {
						if (datefield.getValue() == null) {
							datefield.setValue(NS.fecHoy);
						}
						NS.txtDias.setValue(NS.restaFechas(BFwrk.Util
								.changeDateToString(''
										+ Ext.getCmp(PF + 'txtFecDisp')
										.getValue()),
										BFwrk.Util.changeDateToString(''
												+ Ext.getCmp(
														PF + 'txtFecVenDisp')
														.getValue())));

					},
				}
			});
	// dias
	NS.lblDias = new Ext.form.Label({
		text : 'Días:',
		x : 780,
		y : -1,
	});
	NS.txtDias = new Ext.form.TextField({
		id : PF + 'txtDias',
		name : PF + 'txtDias',
		disabled : true,
		x : 780,
		y : 13,
		width : 50,
		listeners : {
			change : {
				fn : function(caja, valor) {
					/*
					 * Ext.getCmp(PF + 'txtMontoIni').setValue(
					 * BFwrk.Util.formatNumber(caja.getValue()));
					 * ('MONTOS', caja.getId(), 'valor');
					 */
				}
			}
		}
	});
	// fecha fin
	NS.lblFecVenDisp = new Ext.form.Label({
		text : 'Fecha Final:',
		x : 845,
		y : -1
	});
	NS.txtFecVenDisp = new Ext.form.DateField(
			{
				id : PF + 'txtFecVenDisp',
				name : PF + 'txtFecVenDisp',
				format : 'd/m/Y',
				x : 845,
				y : 13,
				width : 100,
				disabled : true,
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
								Ext
								.getCmp(PF + 'txtFecVenDisp')
								.setValue(
										Ext
										.getCmp(
												PF
												+ 'txtFechaFin')
												.getValue());
								Ext.getCmp(PF + 'txtFecDisp').focus();
							} else {
								if (fechaFin < caja.getValue()) {
									BFwrk.Util
									.msgShow(
											'La fecha de vencimiento de la disposición no está dentro del rango de la línea.',
									'ERROR');
									Ext.getCmp(PF + 'txtFecVenDisp')
									.focus();
									Ext
									.getCmp(
											PF
											+ 'txtFecVenDisp')
											.setValue(
													Ext
													.getCmp(
															PF
															+ 'txtFechaFin')
															.getValue());
								} else {
									diaSemana = caja.getValue()
									.getDay();

									if (diaSemana == "0") {
										BFwrk.Util
										.msgShow(
												'La fecha de vencimiento de Disposición es inhábil.',
										'ERROR');
										Ext
										.getCmp(
												PF
												+ 'txtFecVenDisp')
												.focus();
										Ext
										.getCmp(
												PF
												+ 'txtFecVenDisp')
												.setValue(
														Ext
														.getCmp(
																PF
																+ 'txtFechaFin')
																.getValue());
									} else if (diaSemana == "6") {
										BFwrk.Util
										.msgShow(
												'La fecha de vencimiento de Disposición es inhábil.',
										'ERROR');
										Ext
										.getCmp(
												PF
												+ 'txtFecVenDisp')
												.focus();
										Ext
										.getCmp(
												PF
												+ 'txtFecVenDisp')
												.setValue(
														Ext
														.getCmp(
																PF
																+ 'txtFechaFin')
																.getValue());
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
													.msgShow(
															'La fecha de vencimiento de Disposición es inhábil.',
															'ERROR');
													NS.storeSelectInhabil.valorFecha = '';
													NS.valorFecha = '';
													Ext
													.getCmp(
															PF
															+ 'txtFecVenDisp')
															.focus();
													Ext
													.getCmp(
															PF
															+ 'txtFecVenDisp')
															.setValue(
																	Ext
																	.getCmp(
																			PF
																			+ 'txtFechaFin')
																			.getValue());
												}
												NS.storeSelectInhabil.valorFecha = '';
												NS.valorFecha = '';
											}
										});
									}
								}
							}
						}
					},
					blur : function(datefield) {
						if (datefield.getValue() == null) {
							datefield.setValue(NS.fecHoy);
						}
						NS.txtDias.setValue(NS.restaFechas(BFwrk.Util
								.changeDateToString(''
										+ Ext.getCmp(PF + 'txtFecDisp')
										.getValue()),
										BFwrk.Util.changeDateToString(''
												+ Ext.getCmp(
														PF + 'txtFecVenDisp')
														.getValue())));
					},
				}
			});
	//
	NS.lblPeriodoRenta = new Ext.form.Label({
		text : 'Periodicidad Renta:',
		x : 510,
		y : 100
	});
	NS.cmbPeriodoRenta = new Ext.form.ComboBox({
		store : NS.storeCmbEmpresa,
		name : PF + 'cmbPeriodoRenta ',
		id : PF + 'cmbPeriodoRenta ',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 510,
		y : 115,
		width : 140,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una empresa',
		triggerAction : 'all',
		value : NS.GI_NOM_EMPRESA,
		listeners : {
			select : {
				fn : function(combo, valor) {
					// BFwrk.Util.updateComboToTextField(
					// PF + 'txtEmpresa', NS.cmbEmpresa.getId());
					// /NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	NS.lblAforoImporte = new Ext.form.Label({
		text : 'Importe:',
		x : 510,
		y : 100
	});
	NS.txtAforoImporte = new Ext.form.TextField({
		id : PF + 'txtAforoImporte',
		name : PF + 'txtAforoImporte',
		value : '',
		x : 510,
		y : 115,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	NS.lblTasaMora = new Ext.form.Label({
		text : 'Tasa Moratoria:',
		x : 640,
		y : 100,
		hidden : true
	});
	NS.txtTasaMora = new Ext.form.TextField({
		id : PF + 'txtTasaMora',
		name : PF + 'txtTasaMora',
		value : "0.00",
		x : 640,
		y : 115,
		width : 120,
		hidden : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	NS.lblRentaDep = new Ext.form.Label({
		text : 'Renta en Depósito:',
		x : 640,
		y : 100
	});
	NS.txtRentaDep = new Ext.form.TextField({
		id : PF + 'txtRentaDep',
		name : PF + 'txtRentaDep',
		value : '',
		x : 640,
		y : 115,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	NS.lblMontoMora = new Ext.form.Label({
		text : 'Monto moratorio:',
		x : 770,
		y : 100,
		hidden : true
	});
	NS.txtMontoMora = new Ext.form.TextField({
		id : PF + 'txtMontoMora',
		name : PF + 'txtMontoMora',
		value : "0.00",
		x : 770,
		y : 115,
		hidden : true,
		width : 100,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	// renta
	NS.lblRenta = new Ext.form.Label({
		text : 'Renta:',
		x : 770,
		y : 100
	});
	NS.txtRenta = new Ext.form.TextField({
		id : PF + 'txtRenta',
		name : PF + 'txtRenta',
		value : '',
		x : 770,
		y : 115,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	// valor factura
	NS.lblValorFacturas = new Ext.form.Label({
		text : 'Valor factura:',
		x : 380,
		y : 140
	});
	NS.txtValorFacturas = new Ext.form.TextField({
		id : PF + 'txtValorFacturas',
		name : PF + 'txtValorFacturas',
		value : '',
		x : 380,
		y : 155,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	//
	NS.chkComision = new Ext.form.Checkbox({
		name : PF + 'chkComision',
		id : 'chkComision',
		x : 380,
		y : 155,
		listeners : {
			check : {
				fn : function() {
					// var me = this;
					// var res = me.getValue();

					// if (res == true) {
					// NS.storeSolicitudes.baseParams.cTodas = 1;
					// NS.cTodas = 1;
					// } else {
					// NS.storeSolicitudes.baseParams.cTodas = 0;
					// NS.cTodas = 0;
					// }
				}
			}
		}
	});
	NS.lblComision2 = new Ext.form.Label({
		text : 'Comisión',
		x : 395,
		y : 155
	});
	NS.lblComision3 = new Ext.form.Label({
		text : 'Apertura',
		x : 395,
		y : 165
	});
	// sp
	NS.lblSP = new Ext.form.Label({
		text : 'Calificadora SP:',
		x : 380,
		y : 140
	});
	var storeStates = new Ext.data.ArrayStore({
		fields : [ 'state' ],
		data : [ [ '0' ], [ '1' ], [ '2' ] ]
	});
	NS.cmbSP = new Ext.form.ComboBox({
		store : storeStates,
		name : PF + 'cmbSP',
		id : PF + 'cmbSP',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 380,
		y : 155,
		width : 140,
		valueField : 'state',
		displayField : 'state',
		autocomplete : true,
		triggerAction : 'all',
		value : 0,
		listeners : {
			select : {
				fn : function(combo, valor) {
				}
			}
		}
	});
	// moodys
	NS.lblMoody = new Ext.form.Label({
		text : 'Calificadora Moody´s:',
		x : 530,
		y : 140
	});
	NS.cmbMoody = new Ext.form.ComboBox({
		store : NS.storeCmbEmpresa,
		name : PF + 'cmbMoody',
		id : PF + 'cmbMoody',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 530,
		y : 155,
		width : 140,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una empresa',
		triggerAction : 'all',
		value : NS.GI_NOM_EMPRESA,
		listeners : {
			select : {
				fn : function(combo, valor) {
					// BFwrk.Util.updateComboToTextField(
					// PF + 'txtEmpresa', NS.cmbEmpresa.getId());
					// /NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	NS.lblFitch = new Ext.form.Label({
		text : 'Calificadora Fitch:',
		x : 680,
		y : 140
	});
	NS.cmbFitch = new Ext.form.ComboBox({
		store : NS.storeCmbEmpresa,
		name : PF + 'cmbFitch',
		id : PF + 'cmbFitch',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 680,
		y : 155,
		width : 140,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una empresa',
		triggerAction : 'all',
		value : NS.GI_NOM_EMPRESA,
		listeners : {
			select : {
				fn : function(combo, valor) {
					// BFwrk.Util.updateComboToTextField(
					// PF + 'txtEmpresa', NS.cmbEmpresa.getId());
					// /NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	// sobre tasa
	NS.lblSobreTasa = new Ext.form.Label({
		text : 'Sobre tasa:',
		x : 830,
		y : 140
	});
	NS.txtSobreTasa = new Ext.form.TextField({
		id : PF + 'txtSobreTasa',
		name : PF + 'txtSobreTasa',
		value : '',
		x : 830,
		y : 155,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	//
	NS.lblTasaPonderada = new Ext.form.Label({
		text : 'Tasa ponderada:',
		x : 380,
		y : 180
	});
	NS.txtTasaPonderada = new Ext.form.TextField({
		id : PF + 'txtTasaPonderada',
		name : PF + 'txtTasaPonderada',
		value : '',
		x : 380,
		y : 195,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.lblTasaPostura = new Ext.form.Label({
		text : 'Monto postura:',
		x : 530,
		y : 180
	});
	NS.txtTasaPostura = new Ext.form.TextField({
		id : PF + 'txtTasaPostura',
		name : PF + 'txtTasaPostura',
		value : '',
		x : 530,
		y : 195,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	NS.lblSobreVenta = new Ext.form.Label({
		text : 'Sobre Venta:',
		x : 680,
		y : 180
	});
	NS.txtSobreVenta = new Ext.form.TextField({
		id : PF + 'txtSobreVenta',
		name : PF + 'txtSobreVenta',
		value : '',
		x : 680,
		y : 195,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	NS.lblSobreTasaCB = new Ext.form.Label({
		text : 'Sobre Tasa CB:',
		x : 830,
		y : 180
	});
	NS.txtSobreTasaCB = new Ext.form.TextField({
		id : PF + 'txtSobreTasaCB',
		name : PF + 'txtSobreTasaCB',
		value : '',
		x : 830,
		y : 195,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	// Banco Receptor
	NS.lblBancoDis = new Ext.form.Label({
		text : 'Banco Receptor:',
		x : 0,
		y : 38
	});
	NS.txtBancoDis = new Ext.form.TextField(
			{
				id : PF + 'txtBancoDis',
				name : PF + 'txtBancoDis',
				value : '',
				x : 0,
				y : 53,
				disabled : true,
				width : 35,
				listeners : {
					change : {
						fn : function(caja, valor) {
							var comboValue = BFwrk.Util
							.updateTextFieldToCombo(PF
									+ 'txtBancoDis',
									NS.cmbBancoDis.getId());
						}
					}
				}
			});
	NS.storeSelectBancoNacionalidad = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			piBanco : 0
		},
		paramOrder : [ 'piBanco' ],
		directFn : AltaFinanciamientoAction.selectBancoNacionalidad,
		idProperty : 'nacExt',
		fields : [ {
			name : 'nacExt'
		}, ],
		listeners : {
			load : function(s, records) {
			}
		}
	});
	NS.cmbBancoDis = new Ext.form.ComboBox(
			{
				store : NS.storeCmbBanco,
				name : PF + 'cmbBancoDis',
				id : PF + 'cmbBancoDis',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 40,
				y : 53,
				disabled : true,
				width : 140,
				valueField : 'idStr',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione un banco',
				triggerAction : 'all',
				value : '',
				listeners : {
					select : {
						fn : function(combo, valor) {
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtBancoDis', NS.cmbBancoDis
									.getId());
							if (NS.cmbBancoDis.store.getCount() > 0) {
								NS.txtBancoDis.setValue(NS.cmbBancoDis
										.getValue());
								NS.cmbChequeraDis.reset();
								if (NS.proyecto == 'CIE') {
									var myMask = new Ext.LoadMask(Ext
											.getBody(), {
										store : NS.storeComboClabe,
										msg : "Cargando..."
									});
									NS.storeComboClabe
									.load({
										params : {
											pvvValor2 : parseInt(NS.txtBancoDis
													.getValue()),
													psDivisa : '',
													noEmpresa:parseInt(NS.txtEmpresa.getValue())
										}
									});
								} else {
									var myMask = new Ext.LoadMask(Ext
											.getBody(), {
										store : NS.storeComboClabe,
										msg : "Cargando..."
									});
									NS.storeComboClabe
									.load({
										params : {
											pvvValor2 : parseInt(NS.txtBancoDis
													.getValue()),
													psDivisa : NS.txtDivisaD
													.getValue(),
													noEmpresa:parseInt(NS.txtEmpresa.getValue())
										}
									});
									// If vsTipoMenu = "" Then
									var myMask = new Ext.LoadMask(
											Ext.getBody(),
											{
												store : NS.storeSelectBancoNacionalidad,
												msg : "Cargando..."
											});
									NS.storeSelectBancoNacionalidad
									.load({
										params : {
											piBanco : NS.txtBancoDis
											.getValue(),

										},
										callback : function(
												records,
												operation,
												success) {
											if (records.length > 0) {
												if (records[0]
												.get('nacExt') == "N"
												|| records[0]
												.get('nacExt') == "")

													Ext
													.getCmp(
															'frmWHTax')
															.hide();
												else
													Ext
													.getCmp(
															'frmWHTax')
															.show();
											}
										}
									});
									// End If
								}
							} else {
								NS.txtBancoDis.setValue("");
								NS.cmbChequeraDis.reset();
							}
						}
					}
				}
			});
	// chequera
	NS.lblChequeraDis = new Ext.form.Label({
		text : 'Chequera Receptora:',
		x : 200,
		y : 38
	});
	NS.cmbChequeraDis = new Ext.form.ComboBox({
		store : NS.storeComboClabe,
		name : PF + 'cmbChequeraDis',
		id : PF + 'cmbChequeraDis',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 200,
		y : 53,
		disabled : true,
		width : 140,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione una chequera',
		triggerAction : 'all',
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {
					// BFwrk.Util.updateComboToTextField(
					// PF + 'txtEmpresa', NS.cmbEmpresa.getId());
					// /NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	// pais
	NS.lblEquivalencia = new Ext.form.Label({
		text : 'Acreedor ERP:',
		x : 360,
		y : 38
	});
	NS.txtEquivalencia = new Ext.form.TextField({
		id : PF + 'txtEquivalencia',
		name : PF + 'txtEquivalencia',
		value : '',
		x : 360,
		y : 53,
		disabled : true,
		width : 35,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util.updateTextFieldToCombo(
							PF + 'txtEquivalencia', NS.cmbEquivalencia
							.getId());
				}
			}
		}
	});
	NS.cmbEquivalencia = new Ext.form.ComboBox({
		store : NS.storeCmbEquivalencia,
		name : PF + 'cmbEquivalencia',
		id : PF + 'cmbEquivalencia',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 400,
		y : 53,
		disabled : true,
		width : 180,
		valueField : 'idStr',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : '',
		triggerAction : 'all',
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF
							+ 'txtEquivalencia', NS.cmbEquivalencia
							.getId());
					// /NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});

	// Forma pago

	NS.optFormaPago = new Ext.form.RadioGroup({
		id : PF + 'optFormaPago',
		name : PF + 'optFormaPago',
		x : 0,
		y : 0,
		disabled : true,
		columns : 2,
		items : [ {
			boxLabel : 'Transferencia',
			name : 'optFrmPago',
			inputValue : 0,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.oFormaPago = '0';
						}
					}
				}
			}
		}, {
			boxLabel : 'Cargo Cta.',
			name : 'optFrmPago',
			inputValue : 1,
			checked : true,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.oFormaPago = '1';
						}
					}
				}
			}
		} ]
	});
	// Tasas
	NS.optTasa = new Ext.form.RadioGroup(
			{
				id : PF + 'optTasa',
				name : PF + 'optTasa',
				x : 0,
				y : 0,
				disabled : true,
				columns : 2,
				items : [
				         {
				        	 boxLabel : 'Tasa Fija',
				        	 name : 'optTsa',
				        	 inputValue : 0,
				        	 listeners : {
				        		 check : {
				        			 fn : function(opt, valor) {
				        				 if (valor == true) {
				        					 NS.oTasa = '0';
				        					 NS.cmbTasaBase.reset();
				        					 NS.cmbTasaBase
				        					 .setDisabled(true);
				        					 // cmbTasaBase.BackColor =
				        					 // &H8000000F
				        					 NS.txtBase
				        					 .setValue("0.00000");
				        					 NS.txtPuntos
				        					 .setValue("0.00000");
				        					 NS.txtTasaVigente
				        					 .setValue("0.00000");
				        					 NS.txtTasaFija
				        					 .setValue("0.00000");
				        					 NS.txtPuntos
				        					 .setDisabled(false);
				        					 NS.txtTasaFija
				        					 .setDisabled(false);
				        					 // txtPuntos.BackColor =
				        					 // &H80000005
				        					 // txtTasaFija.BackColor =
				        					 // &H80000005
				        					 if (NS.cmbTasa.store
				        							 .getCount() > 0) {
				        						 NS.vdTasa = 0;
				        						 NS.storeTasa
				        						 .load({
				        							 params : {
				        								 psTasa : NS.cmbTasa
				        								 .getValue()
				        							 },
				        							 callback : function(
				        									 records,
				        									 operation,
				        									 success) {
				        								 if (records.length > 0) {
				        									 NS.vdTasa = parseFloat(records[0]
				        									 .get('valor'))
				        									 + parseFloat(BFwrk.Util
				        											 .unformatNumber(NS.txtSpreed
				        													 .getValue()));
				        								 } else {
				        									 NS.txtBase
				        									 .setValue("0.00000");
				        									 NS.txtTasaVigente
				        									 .setValue("0.000000");
				        									 Ext.Msg
				        									 .alert(
				        											 'SET',
				        									 'La tasa no ha sido alimentada.');
				        								 }
				        							 }
				        						 });
				        					 }
				        				 }
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
				        				 if (valor == true) {
				        					 NS.oTasa = '1';
				        					 NS.txtTasaFija
				        					 .setValue("0.00000");
				        					 NS.txtTasaVigente
				        					 .setValue("0.00000");
				        					 NS.txtBase
				        					 .setValue("0.00000");
				        					 NS.txtPuntos
				        					 .setValue("0.00000");
				        					 NS.txtPuntos
				        					 .setDisabled(false);
				        					 NS.txtTasaFija
				        					 .setDisabled(true);
				        					 // txtPuntos.BackColor =
				        					 // &H80000005
				        					 // txtTasaFija.BackColor =
				        					 // &H8000000F
				        					 NS.cmbTasaBase
				        					 .setDisabled(false);
				        					 // cmbTasaBase.BackColor =
				        					 // &H80000005
				        				 }
				        			 }
				        		 }
				        	 }
				         } ]
			});
	// tasa base
	NS.lblTasaBase = new Ext.form.Label({
		text : 'Tasa base:',
		x : 0,
		y : 30
	});
	NS.cmbTasaBase = new Ext.form.ComboBox(
			{
				store : NS.storeCmbTasa,
				name : PF + 'cmbTasaBase ',
				id : PF + 'cmbTasaBase ',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 0,
				y : 45,
				width : 140,
				valueField : 'idStr',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione Tasa Base',
				triggerAction : 'all',
				disabled : true,
				value : '',
				listeners : {
					select : {
						fn : function(combo, valor) {
							NS.storeTasa.load({
								params : {
									psTasa : NS.cmbTasaBase.getValue()
								},
								callback : function(records,
										operation, success) {
									if (records.length > 0) {
										NS.txtBase.setValue(BFwrk.Util.formatNumber(records[0].get('valor')));
										NS.txtTasaVigente.setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(NS.txtPuntos.getValue()))+ (parseFloat(records[0].get('valor')))));
									} else {
										NS.txtBase.setValue("0.00000");
										NS.txtTasaVigente.setValue("0.000000");
										Ext.Msg.alert('SET','La tasa no ha sido alimentada.');
									}
								}
							});
						}
					}
				}
			});
	NS.lblBase = new Ext.form.Label({
		text : 'Valor tasa:',
		x : 170,
		y : 30
	});

	NS.txtBase = new Ext.form.TextField({
		id : PF + 'txtBase',
		name : PF + 'txtBase',
		value : '',
		x : 170,
		y : 45,
		width : 120,
		value : "0.00000",
		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtBase').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	NS.lblPuntos = new Ext.form.Label({
		text : 'Puntos:',
		x : 0,
		y : 70
	});
	NS.lblTasaVigente = new Ext.form.Label({
		text : 'Tasa vigente:',
		x : 240,
		y : 70,
		value : "0.00000",
	});
	NS.lblTasaFija = new Ext.form.Label({
		text : 'Tasa fija:',
		x : 120,
		y : 70
	});
	NS.txtPuntos = new Ext.form.TextField({
		id : PF + 'txtPuntos',
		name : PF + 'txtPuntos',
		value : '',
		x : 0,
		y : 85,
		width : 100,
		value : "0.00000",
		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
					
					Ext.getCmp(PF + 'txtPuntos').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
					if(NS.oTasa=='1'){
						NS.storeTasa.load({
							params : {
								psTasa : NS.cmbTasaBase.getValue()
							},
							callback : function(records,
									operation, success) {
								if (records.length > 0) {
									NS.txtBase.setValue(BFwrk.Util.formatNumber(records[0].get('valor')));
									NS.txtTasaVigente.setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(NS.txtPuntos.getValue()))+ (parseFloat(records[0].get('valor')))));
								} else {
									NS.txtBase.setValue("0.00000");
									NS.txtTasaVigente.setValue("0.000000");
									Ext.Msg.alert('SET','La tasa no ha sido alimentada.');
								}
							}
						});
					
					}else{
						NS.txtTasaVigente.setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(NS.txtPuntos.getValue()))+ (parseFloat(BFwrk.Util.unformatNumber(NS.txtTasaFija.getValue())))));
					}
				}
			}
		}
	});
	// WHtax23
	NS.txtWhtax = new Ext.form.TextField({
		id : PF + 'txtWhtax',
		name : PF + 'txtWhtax',
		value : '',
		x : 0,
		y : 0,
		width : 100,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.txtTasaVigente = new Ext.form.TextField({
		id : PF + 'txtTasaVigente',
		name : PF + 'txtTasaVigente',
		value : '',
		x : 240,
		y : 85,
		width : 100,
		value : "0.00000",
		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {
					
					Ext.getCmp(PF + 'txtTasaVigente').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	NS.txtTasaFija = new Ext.form.TextField({
		id : PF + 'txtTasaFija',
		name : PF + 'txtTasaFija',
		value : '',
		x : 120,
		y : 85,
		width : 100,
		disabled : true,
		value : "0.00000",
		listeners : {
			change : {
				fn : function(caja, valor) {
					if(NS.oTasa=='0'){
						NS.txtTasaVigente.setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(NS.txtPuntos.getValue()))+ (parseFloat(BFwrk.Util.unformatNumber(NS.txtTasaFija.getValue())))));
					}
					
					Ext.getCmp(PF + 'txtTasaFija').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	//
	NS.lblAnexo = new Ext.form.Label({
		text : 'No. Anexo:',
		x : 380,
		y : 100
	});
	// WHtax
	NS.txtAnexo = new Ext.form.TextField({
		id : PF + 'txtAnexo',
		name : PF + 'txtAnexo',
		value : '',
		x : 380,
		y : 115,
		width : 80,
		disabled : true,

		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.lblAforoPorciento = new Ext.form.Label({
		text : 'Aforo:',
		x : 380,
		y : 100
	});
	NS.txtAforoPorciento = new Ext.form.TextField({
		id : PF + 'txtAforoPorciento',
		name : PF + 'txtAforoPorciento',
		value : '',
		x : 380,
		y : 115,
		width : 100,
		disabled : true,

		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	NS.lblEmision = new Ext.form.Label({
		text : 'Emisión %:',
		x : 380,
		y : 100
	});
	NS.txtEmision = new Ext.form.TextField({
		id : PF + 'txtEmision',
		name : PF + 'txtEmision',
		value : '',
		x : 380,
		y : 115,
		width : 120,
		readOnly : true,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.lblTasaMora = new Ext.form.Label({
		text : 'Tasa Moratoria:',
		x : 640,
		hidden : true,
		y : 100
	});

	NS.txtTasaMora = new Ext.form.TextField({
		id : PF + 'txtTasaMora',
		name : PF + 'txtTasaMora',
		value : '',
		x : 640,
		y : 115,
		width : 120,
		hidden : true,
		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});

	NS.lblMontoMora = new Ext.form.Label({
		text : 'Monto moratorio:',
		x : 770,
		hidden : true,
		y : 100
	});

	NS.txtMontoMora = new Ext.form.TextField({
		id : PF + 'txtMontoMora',
		name : PF + 'txtMontoMora',
		value : '',
		x : 770,
		hidden : true,
		y : 115,
		width : 100,

		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});

	// comentarios
	NS.lblComentarios = new Ext.form.Label({
		text : 'Comentarios:',
		x : 0,
		y : 255
	});
		NS.txtComentarios = new Ext.form.TextArea({
		id : PF + 'txtComentarios',
		name : PF + 'txtComentarios',
		value : '',
		x : 0,
		y : 270,
		width : 250,

		disabled : true,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.validaDatosDisp = function() {

		NS.bandDis = false;
		if (NS.txtTipoFinanDis.getValue() == "") {
			Ext.Msg.alert('SET',
			'Seleccione el destino de recursos.');
			Ext.getCmp(PF + 'txtTipoFinanDis').focus();
			NS.bandDis = false;
		} else if (NS.txtDivisaD.getValue() == "") {
			Ext.Msg.alert('SET', 'Seleccione una divisa válida.');
			Ext.getCmp(PF + 'txtDivisaD').focus();
			NS.bandDis = false;
		} else if (BFwrk.Util.unformatNumber(NS.txtMontoDisposicion
				.getValue()) == ""
					|| BFwrk.Util.unformatNumber(NS.txtMontoDisposicion
							.getValue()) <= 0) {
			Ext.Msg.alert('SET', 'Escriba el monto de la disposición.');
			NS.txtMontoDisposicion.focus();
			NS.bandDis = false;
		} else if ((Ext.getCmp(PF + 'txtFecDisp').getValue() < Ext
				.getCmp(PF + 'txtFechaIni').getValue())
				|| (Ext.getCmp(PF + 'txtFecDisp').getValue() > Ext
						.getCmp(PF + 'txtFecVenDisp').getValue())) {
			BFwrk.Util
			.msgShow(
					'La fecha de disposición no está dentro del rango de la línea.',
			'ERROR');
			Ext.getCmp(PF + 'txtFecDisp').focus();
			NS.bandDis = false;
		} else if (NS.oFormaPago == '0'
			&& NS.txtEquivalencia.getValue() == "") {
			BFwrk.Util.msgShow('Seleccione el acreedor ERP.', 'ERROR');
			Ext.getCmp(PF + 'txtEquivalencia').focus();
			NS.bandDis = false;

		} else if (NS.txtEquivalencia.getValue() == "") {
			BFwrk.Util.msgShow('Seleccione el acreedor ERP.', 'ERROR');
			Ext.getCmp(PF + 'txtEquivalencia').focus();
			NS.bandDis = false;

		} else if (NS.txtBancoDis.getValue() == "") {
			Ext.Msg.alert('SET', 'Seleccione un banco receptor.');
			Ext.getCmp(PF + 'txtBancoDis').focus();
			NS.bandDis = false;
		} else if (NS.cmbChequeraDis.getValue() == "") {
			Ext.Msg.alert('SET', 'Seleccione una chequera receptora.');
			NS.cmbChequeraDis.focus();
			NS.bandDis = false;
		} else if (NS.txtEquivalencia.getValue() == "") {
			BFwrk.Util.msgShow('Seleccione el acreedor ERP.', 'ERROR');
			Ext.getCmp(PF + 'txtEquivalencia').focus();
			NS.bandDis = false;
		} else if (NS.txtMontoMora.getValue() != 0
				&& NS.txtTasaMora.getValue() != 0
				&& NS.proyecto == 'CIE') {
			BFwrk.Util
			.msgShow(
					'Solo puede insertar un dato, Tasa moratoria o Monto moratorio.',
			'ERROR');
			Ext.getCmp(PF + 'txtMontoMora').focus();
			NS.bandDis = false;
		} else {
			if (NS.oTasa == '1') {
				if (NS.proyecto != 'CIE') {
					if (NS.cmbTasaBase.getValue() <= 0) {
						BFwrk.Util.msgShow('Seleccione la tasa base.',
						'ERROR');
						NS.cmbTasaBase.focus();
						NS.bandDis = false;
					} else if (BFwrk.Util.unformatNumber(NS.txtBase
							.getValue()) == "") {
						BFwrk.Util.msgShow(
								'Ingrese el valor de la tasa base.',
						'ERROR');
						Ext.getCmp(PF + 'txtBase').focus();
						NS.bandDis = false;
					} else if (BFwrk.Util
							.unformatNumber(NS.txtTasaVigente
									.getValue()) == "") {
						BFwrk.Util
						.msgShow(
								'Ingrese el valor de la tasa variable.',
						'ERROR');
						Ext.getCmp(PF + 'txtTasaVigente').focus();
						NS.bandDis = false;
					} else {
						NS.bandDis = true;
					}
				} else {
					NS.bandDis = true;
				}
				NS.pbCredSind = false;
			} else {
				NS.bandDis = true;
			}
		}
		/*
		 * If vsTipoMenu = "A" And Val(txtRenta.Text) = 0 Then MsgBox
		 * "Ingrese el Valor de la Renta!!", vbExclamation, "SET"
		 * txtRenta.SetFocus: Exit Function End If
		 */
		/*
		 * If vsTipoMenu = "A" And gobjVarGlobal.valor_configura_set(1) =
		 * "CIE" Then Else 'Nada mas para dar de alta un caso de CIE '''
		 * If txtBancoDis.Text = "" Then ''' MsgBox "Seleccione el banco
		 * de la disposición!!", vbExclamation, "SET" '''
		 * txtBancoDis.SetFocus: Exit Function ''' ElseIf
		 * cmbChequeraDis.Text = "" Then ''' MsgBox "Seleccione la
		 * chequera Receptora!!", vbExclamation, "SET" '''
		 * cmbChequeraDis.SetFocus: Exit Function ''' End If End If
		 */
		/*
		 * If gobjVarGlobal.valor_configura_set(1) = "CIE" Then If
		 * vsTipoMenu = "A" Then If Trim(txtAnexo.Text) = "" Then MsgBox
		 * "Ingrese el Valor de Anexo!!", vbExclamation, "SET"
		 * txtAnexo.SetFocus: Exit Function ElseIf
		 * Trim(cmbPeriodoRenta.Text) = "" Then MsgBox "Ingrese el
		 * periodo de la renta!!", vbExclamation, "SET"
		 * cmbPeriodoRenta.SetFocus: Exit Function End If End If If
		 * vsTipoMenu = "F" Then If CDbl(txtAforoPorciento.Text) > 0
		 * Then vdTotal = CDbl(txtValorFacturas.Text) *
		 * (CDbl(txtAforoPorciento.Text) / 100) ElseIf
		 * CDbl(txtAforoImporte.Text) > 0 Then vdTotal =
		 * CDbl(txtValorFacturas.Text) - CDbl(txtAforoImporte.Text) End
		 * If If Val(txtValorFacturas.Text) = 0 Then MsgBox "Ingrese el
		 * valor de las Facturas!!", vbExclamation, "SET"
		 * txtValorFacturas.SetFocus: Exit Function End If If vdTotal <>
		 * CDbl(txtMontoDisposicion.Text) Then MsgBox "El monto de la
		 * disposición sera reemplazado por que no cuadra con el Aforo",
		 * vbInformation, "SET" txtMontoDisposicion.Text =
		 * Format(vdTotal, "#,##0.00") End If If pbModificarDisp Then If
		 * CDbl(txtMontoDisposicion.Text) >
		 * (CDbl(txtMontoDisponible.Text) + dMontoDispOrig) Then MsgBox
		 * "El valor de la disposición No puede ser mayor a lo
		 * disponible!!", vbExclamation, "SET"
		 * txtMontoDisposicion.SetFocus: Exit Function End If Else If
		 * CDbl(txtMontoDisposicion.Text) >
		 * CDbl(txtMontoDisponible.Text) Then MsgBox "El valor de la
		 * disposición No puede ser mayor a lo disponible!!",
		 * vbExclamation, "SET" txtMontoDisposicion.SetFocus: Exit
		 * Function End If End If End If End If
		 */
	}

	NS.storeSelectNoDisp = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			finac : ''
		},
		paramOrder : [ 'finac' ],
		directFn : AltaFinanciamientoAction.selectNoDisp,
		idProperty : 'idDisposicion',
		fields : [ {
			name : 'idDisposicion'
		}, ],
		listeners : {
			load : function(s, records) {

			}
		}
	});
	NS.modificarDatosDis = function() {
		NS.validaDatosDisp();
		var validaDatoDisp = NS.bandDis;
		if (validaDatoDisp) {
			NS.storeSelectInhabil
			.load({
				params : {
					valorFecha : NS.txtFecDisp.getValue()
				},
				callback : function(records, operation, success) {
					if (records.length > 0) {
						BFwrk.Util
						.msgShow(
								'La fecha de la disposición es inhábil.',
								'ERROR');
						NS.storeSelectInhabil.valorFecha = '';
						NS.valorFecha = '';
						Ext.getCmp(PF + 'txtFecDisp').focus();
						Ext.getCmp(PF + 'txtFecDisp').setValue(
								Ext.getCmp(PF + 'txtFechaIni')
								.getValue());
					}
					NS.storeSelectInhabil.valorFecha = '';
					NS.valorFecha = '';
					NS.storeSelectInhabil
					.load({
						params : {
							valorFecha : NS.txtFecVenDisp
							.getValue()
						},
						callback : function(records,
								operation, success) {
							if (records.length > 0) {
								BFwrk.Util
								.msgShow(
										'La fecha de vencimiento de la disposición es inhábil.',
										'ERROR');
								NS.storeSelectInhabil.valorFecha = '';
								NS.valorFecha = '';
								Ext
								.getCmp(
										PF
										+ 'txtFecVenDisp')
										.focus();
								Ext
								.getCmp(
										PF
										+ 'txtFecVenDisp')
										.setValue(
												Ext
												.getCmp(
														PF
														+ 'txtFechaFin')
														.getValue());
							}
							NS.storeSelectInhabil.valorFecha = '';
							NS.valorFecha = '';
							if (NS.cmbDisp.store
									.getCount() > 0) {
								NS.storeSelectDisposicionCred
								.load({
									params : {
										psIdContrato : NS.txtLinea
										.getValue(),
										piDisposicion : parseInt(NS.cmbDisp
												.getValue())
									},
									callback : function(
											records,
											operation,
											success) {
										if (records.length > 0) {
											NS.montD = records[0]
											.get('montoDisposicion');
											NS.fecD = records[0]
											.get('fecDisposicion');
											NS.fecV = records[0]
											.get('fecVencimiento');
											NS.tipT = records[0]
											.get('tipoTasa');
											NS.valorT = records[0]
											.get('valorTasa');
											NS.storeSelectAmortizaciones
											.load({
												params : {
													psIdContrato : NS.txtLinea
													.getValue(),
													piDisposicion : parseInt(NS.cmbDisp
															.getValue()),
															pbCambioTasa : false,
															psTipoMenu : '',
															psProyecto : '',
															piCapital : 0
												},
												callback : function(
														records,
														operation,
														success) {
													if (NS.oTasa == '0')
														NS.vsTipoTasa = "F";
													if (NS.oTasa == '1')
														NS.vsTipoTasa = "V";
													if (records.lenght > 0) {
														if (NS.proyecto == 'CIE')
															NS.vbExisteA = true;
														if ((parseFloat(BFwrk.Util
																.unformatNumber(NS.txtMontoDisposicion
																		.getValue())) != parseFloat(NS.montD))
																		|| (NS.txtFecDisp
																				.getValue() != NS.fecD)
																				|| (NS.txtFecVenDisp
																						.getValue() != NS.fecV)
																						|| (NS.vsTipoTasa
																								.trim() != NS.tipT))
															NS.vbExisteA = true;
														if (NS.tipT == "V") {
															if (parseFloat(BFwrk.Util
																	.unformatNumber(NS.txtTasaVigente
																			.getValue())) != NS.valorT)
																NS.vbExisteA = true;
														} else {
															if (parseFloat(BFwrk.Util
																	.unformatNumber(NS.txtTasaFija
																			.getValue())) != NS.valorT)
																NS.vbExisteA = true;
														}
														/*
														 * If
														 * vsTipoMenu =
														 * "A"
														 * Then
														 * If
														 * CDbl(Format(txtRenta.Text,
														 * "######.00")) <>
														 * CDbl(rstDatos!RENTA)
														 * Then
														 * vbExisteA =
														 * True
														 * ElseIf
														 * rstDatos!tipo_tasa =
														 * "V"
														 * Then
														 * If
														 * CDbl(txtTasaVigente.Text) <>
														 * CDbl(rstDatos!valor_tasa)
														 * Then
														 * vbExisteA =
														 * True
														 * Else
														 * If
														 * CDbl(txtTasaFija.Text) <>
														 * CDbl(rstDatos!valor_tasa)
														 * Then
														 * vbExisteA =
														 * True
														 * End
														 * If
														 */
														if (NS.vbExisteA == true)
															Ext.msg
															.alert(
																	'SET',
															'El registro no se puede modificar, ya contiene Amortizaciones registradas');
													}
													if (NS.oFormaPago == '0')
														NS.vsFormaPago = "3";
													if (NS.oFormaPago == '1')
														NS.vsFormaPago = "5";
													NS.vdValorTasa = 0;
													NS.vdRenta = 0;
													// If
													// vsTipoMenu
													// =
													// "A"
													// Then
													// vdRenta
													// =
													// CDbl(Format(txtRenta.Text,
													// "######.00"))
													if (NS.proyecto != 'CIE') {// Or
														// vsTipoMenu
														// <>
														// "A"
														if (NS.oTasa == '0') {
															NS.vsTipoTasa = "F";
															NS.vdValorTasa = parseFloat(BFwrk.Util
																	.unformatNumber(NS.txtTasaFija
																			.getValue()));
														} else if (NS.oTasa == '1') {
															NS.vsTipoTasa = "V"
																NS.vsTasaBase = NS.cmbTasaBase.getValue();
															NS.vdValorTasa = parseFloat(BFwrk.Util.unformatNumber(NS.txtTasaVigente
																			.getValue()));
														}
													}
													if (NS.chkLargoPlazo
															.getValue())
														NS.vsPlazoLargo = "S";
													if (!NS.chkLargoPlazo
															.getValue())
														NS.vsPlazoLargo = "N";
													if (NS.cmbPeriodoRenta
															.getValue() != "")
														NS.viPeriodoRenta = NS.cmbPeriodoRenta
														.getValue();
													else
														NS.viPeriodoRenta = 0;
													NS.MB2 = Ext.MessageBox;
													Ext
													.apply(
															NS.MB2,
															{
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
															' ¿Desea modificar este Registro?',
															function(
																	btn) {
																if (btn === 'yes') {

																	if (!NS.txtTipoFinanDis
																			.getValue() == ''
																				|| !NS.txtTipoFinanDis
																				.getValue() == null)
																		NS.tipoFinDis = parseInt(NS.txtTipoFinanDis
																				.getValue());
																	else
																		NS.tipoFinDis = 0;
																	if (!NS.cmbBancoDis
																			.getValue() == ''
																				|| !NS.cmbBancoDis
																				.getValue() == null)
																		NS.bncoDis = parseInt(NS.txtBancoDis
																				.getValue());
																	else
																		NS.bncoDis = 0;
																	if (!BFwrk.Util
																			.unformatNumber(NS.txtMontoDisposicion
																					.getValue()) == ''
																						|| !BFwrk.Util
																						.unformatNumber(NS.txtMontoDisposicion
																								.getValue()) == null)
																		NS.montoDisposicion = parseFloat(BFwrk.Util
																				.unformatNumber(NS.txtMontoDisposicion
																						.getValue()));
																	else
																		NS.montoDisposicion = 0;
																	if (!NS.txtSobreTasa
																			.getValue() == ''
																				|| !NS.txtSobreTasa
																				.getValue() == null)
																		NS.sobreTasa = parseFloat(NS.txtSobreTasa
																				.getValue());
																	else
																		NS.sobreTasa = 0;
																	if (!NS.txtAforoPorciento
																			.getValue() == ''
																				|| !NS.txtAforoPorciento
																				.getValue() == null)
																		NS.aforoPorciento = parseFloat(NS.txtAforoPorciento
																				.getValue());
																	else
																		NS.aforoPorciento = 0;
																	if (!NS.txtAforoImporte
																			.getValue() == ''
																				|| !NS.txtAforoImporte
																				.getValue() == null)
																		NS.aforoImporte = parseFloat(NS.txtAforoImporte
																				.getValue());
																	else
																		NS.aforoImporte = 0;
																	if (!BFwrk.Util
																			.unformatNumber(NS.txtPuntos
																					.getValue()) == ''
																						|| !BFwrk.Util
																						.unformatNumber(NS.txtPuntos
																								.getValue()) == null)
																		NS.puntos = parseFloat(BFwrk.Util
																				.unformatNumber(NS.txtPuntos
																						.getValue()));
																	else
																		NS.puntos = 0;
																	if (!NS.txtTasaPonderada
																			.getValue() == ''
																				|| !NS.txtTasaPonderada
																				.getValue() == null)
																		NS.tasaPonderada = parseFloat(NS.txtTasaPonderada
																				.getValue());
																	else
																		NS.tasaPonderada = 0;
																	if (!NS.txtTasaPostura
																			.getValue() == ''
																				|| !NS.txtTasaPostura
																				.getValue() == null)
																		NS.tasaPostura = parseFloat(NS.txtTasaPostura
																				.getValue());
																	else
																		NS.tasaPostura = 0;
																	if (!NS.txtSobreVenta
																			.getValue() == ''
																				|| !NS.txtSobreVenta
																				.getValue() == null)
																		NS.tasaSobreVenta = parseFloat(NS.txtSobreVenta
																				.getValue());
																	else
																		NS.tasaSobreVenta = 0;
																	if (!NS.txtSobreVenta
																			.getValue() == ''
																				|| !NS.txtSobreVenta
																				.getValue() == null)
																		NS.tasaSobreTasaSB = parseFloat(NS.txtSobreVenta
																				.getValue());
																	else
																		NS.tasaSobreTasaSB = 0;
																	if (!NS.txtWhtax
																			.getValue() == ''
																				|| !NS.txtWhtax
																				.getValue() == null)
																		NS.whtax = parseFloat(NS.txtWhtax
																				.getValue());
																	else
																		NS.whtax = 0;
																	if (!NS.txtRentaDep
																			.getValue() == ''
																				|| !NS.txtRentaDep
																				.getValue() == null)
																		NS.rentaDep = parseFloat(NS.txtRentaDep
																				.getValue());
																	else
																		NS.rentaDep = 0;
																	if (!NS.txtMontoMora
																			.getValue() == ''
																				|| !NS.txtMontoMora
																				.getValue() == null)
																		NS.montoMora = parseFloat(NS.txtMontoMora
																				.getValue());
																	else
																		NS.montoMora = 0;
																	if (!NS.txtTasaMora
																			.getValue() == ''
																				|| !NS.txtTasaMora
																				.getValue() == null)
																		NS.tasaMora = parseFloat(NS.txtTasaMora
																				.getValue());
																	else
																		NS.tasaMora = 0;
																	if (!NS.txtValorFacturas
																			.getValue() == ''
																				|| !NS.txtValorFacturas
																				.getValue() == null)
																		NS.valorFacturas = parseFloat(NS.txtValorFacturas
																				.getValue());
																	else
																		NS.valorFacturas = 0;
																	if (NS.chkComision
																			.getValue())
																		NS.comision = 1;
																	else
																		NS.comision = 0;

																	NS.fechaID = BFwrk.Util
																	.changeDateToString(''
																			+ NS.txtFecDisp
																			.getValue());
																	NS.fechaIDV = BFwrk.Util
																	.changeDateToString(''
																			+ NS.txtFecVenDisp
																			.getValue());

																	var terminado = false;

																	AltaFinanciamientoAction
																	.updateDisposicionCompleta(
																			NS.cmbContratos
																			.getValue(),
																			parseInt(NS.cmbDisp
																					.getValue()),
																					'',
																					'',
																					'',
																					// NS.cmbSP.getValue().trim(),
																					// NS.cmbMoody.getValue().trim(),
																					// NS.cmbFitch.getValue().trim()
																					NS.txtEmision
																					.getValue()
																					.trim(),
																					NS.txtDivisaD
																					.getValue(),
																					NS.montoDisposicion,
																					NS.fechaID,
																					NS.fechaIDV,
																					NS.sobreTasa,
																					NS.aforoPorciento,
																					NS.aforoImporte,
																					NS.vsPlazoLargo,
																					NS.vsFormaPago,
																					NS.vsTipoTasa,
																					NS.vsTasaBase,
																					NS.vdValorTasa,
																					NS.puntos,
																					NS.tasaPonderada,
																					NS.tasaPostura,
																					NS.tasaSobreVenta,
																					NS.tasaSobreTasaSB,
																					NS.bncoDis,
																					NS.cmbChequeraDis
																					.getValue(),
																					NS.tipoFinDis,
																					NS.txtEquivalencia
																					.getValue(),
																					NS.vdRenta,
																					NS.txtAnexo
																					.getValue(),
																					NS.txtComentarios
																					.getValue(),
																					NS.viPeriodoRenta,
																					NS.whtax,
																					NS.rentaDep,
																					NS.comision,
																					NS.montoMora,
																					NS.tasaMora,
																					NS.valorFacturas,

																					function(
																							mapResult,
																							e) {

																				BFwrk.Util
																				.msgWait(
																						'Terminado...',
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

																				}

																				if (mapResult.result > 0) {

																					NS.vsFinanci = NS.cmbContratos
																					.getValue();
																					NS.vlDisp = NS.cmbDisp
																					.getValue();
																					NS
																					.limpiar();
																					NS.cmbContratos
																					.setValue(NS.vsFinanci);
																					NS.storeDatos.baseParams.clave = NS.cmbContratos
																					.getValue();
																					var myMask = new Ext.LoadMask(
																							Ext
																							.getBody(),
																							{
																								store : NS.storeDatos,
																								msg : "Cargando..."
																							});
																					NS.storeDatos
																					.load({
																						callback : function(
																								records,
																								operation,
																								success) {
																							NS.cmbDisp
																							.reset();
																							NS.storeDisposiciones.baseParams.psIdContrato = NS.vsFinanci;
																							NS.storeDisposiciones.baseParams.pbEstatus = true;
																							var myMask = new Ext.LoadMask(
																									Ext
																									.getBody(),
																									{
																										store : NS.storeDisposiciones,
																										msg : "Cargando..."
																									});
																							NS.storeDisposiciones
																							.load({
																								callback : function(
																										records,
																										operation,
																										success) {
																									NS.cmbDisp
																									.setValue(NS.vlDisp);
																									var myMask = new Ext.LoadMask(
																											Ext
																											.getBody(),
																											{
																												store : NS.storeSelectDisp,
																												msg : "Cargando..."
																											});
																									NS.storeSelectDisp
																									.load({
																										params : {
																											psIdContrato : NS.txtLinea
																											.getValue(),
																											psIdDisp : parseInt(NS.cmbDisp
																													.getValue())
																										}
																									});

																								}
																							});

																						}
																					});

																				}

																			});//
																}

															});

												}

											});

										}

									}

								});
							}

						}
					});
				}
			});
		}
	}
	NS.storeSelectDivision = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			piBanco : 0,
			psChequera : ''
		},
		paramOrder : [ 'piBanco', 'psChequera' ],
		directFn : AltaFinanciamientoAction.selectDivision,
		idProperty : 'idDivision',
		fields : [ {
			name : 'idDivision'
		}, ],
		listeners : {
			load : function(s, records) {

			}
		}
	});
	NS.aceptarDis = function() {
		NS.validaDatosDisp();
		var validaDatoDisp = NS.bandDis;
		if (validaDatoDisp) {
			NS.storeSelectInhabil
			.load({
				params : {
					valorFecha : NS.txtFecDisp.getValue()
				},
				callback : function(records, operation, success) {
					if (records.length > 0) {
						BFwrk.Util
						.msgShow(
								'La fecha de la disposición es inhábil.',
								'ERROR');
						NS.storeSelectInhabil.valorFecha = '';
						NS.valorFecha = '';
						Ext.getCmp(PF + 'txtFecDisp').focus();
						Ext.getCmp(PF + 'txtFecDisp').setValue(
								Ext.getCmp(PF + 'txtFechaIni')
								.getValue());
					}
					NS.storeSelectInhabil.valorFecha = '';
					NS.valorFecha = '';
					NS.storeSelectInhabil
					.load({
						params : {
							valorFecha : NS.txtFecVenDisp
							.getValue()
						},
						callback : function(records,
								operation, success) {
							if (records.length > 0) {
								BFwrk.Util
								.msgShow(
										'La fecha de vencimiento de la disposición es inhábil.',
										'ERROR');
								NS.storeSelectInhabil.valorFecha = '';
								NS.valorFecha = '';
								Ext
								.getCmp(
										PF
										+ 'txtFecVenDisp')
										.focus();
								Ext
								.getCmp(
										PF
										+ 'txtFecVenDisp')
										.setValue(
												Ext
												.getCmp(
														PF
														+ 'txtFechaFin')
														.getValue());
							}
							NS.storeSelectInhabil.valorFecha = '';
							NS.valorFecha = '';
							if (NS.oTasa == '0') {
								NS.vsTipoTasa = "F"
									NS.vlValorTasa = parseFloat(BFwrk.Util
											.unformatNumber(NS.txtTasaFija
													.getValue()));
							} else if (NS.oTasa == '1') {
								NS.vsTipoTasa = "V";
								NS.vlValorTasa = parseFloat(BFwrk.Util
										.unformatNumber(NS.txtTasaVigente
												.getValue()));
								NS.vsTasaBase = NS.cmbTasaBase
								.getValue();
							}
							if (NS.oFormaPago == '0')
								NS.vsFormaPago = "3";
							if (NS.oFormaPago == '1')
								NS.vsFormaPago = "5";
							if (NS.chkLargoPlazo
									.getValue())
								NS.vsPlazoLargo = "S";
							if (!NS.chkLargoPlazo
									.getValue())
								NS.vsPlazoLargo = "N";
							NS.vsFinanc = NS.cmbFinanciamiento
							.getValue();
							NS.vdRenta = parseFloat(NS.txtRenta
									.getValue());
							if (NS.cmbPeriodoRenta
									.getValue().trim() != '')
								NS.viPeriodoRenta = parseInt(NS.cmbPeriodoRenta
										.getValue());
							else {
								NS.viPeriodoRenta = 0;
							}
							NS.storeSelectNoDisp
							.load({
								params : {
									finac : NS.vsFinanc,
								},
								callback : function(
										records,
										operation,
										success) {
									if (records.length <= 0)
										NS.vlDisp = 1;
									else
										NS.vlDisp = parseInt(records[records.length - 1]
										.get('idDisposicion')) + 1;
									if (!NS.cmbBancoDis
											.getValue() == ''
												|| !NS.cmbBancoDis
												.getValue() == null)
										NS.bncoDis = parseInt(NS.txtBancoDis
												.getValue());
									else
										NS.bncoDis = 0;
									if (!BFwrk.Util
											.unformatNumber(NS.txtMontoDisposicion
													.getValue()) == ''
														|| !BFwrk.Util
														.unformatNumber(NS.txtMontoDisposicion
																.getValue()) == null)
										NS.montoDisposicion = parseFloat(BFwrk.Util
												.unformatNumber(NS.txtMontoDisposicion
														.getValue()));
									else
										NS.montoDisposicion = 0;
									if (!NS.txtSobreTasa
											.getValue() == ''
												|| !NS.txtSobreTasa
												.getValue() == null)
										NS.sobreTasa = parseFloat(NS.txtSobreTasa
												.getValue());
									else
										NS.sobreTasa = 0;
									if (!NS.txtAforoPorciento
											.getValue() == ''
												|| !NS.txtAforoPorciento
												.getValue() == null)
										NS.aforoPorciento = parseFloat(NS.txtAforoPorciento
												.getValue());
									else
										NS.aforoPorciento = 0;
									if (!NS.txtAforoImporte
											.getValue() == ''
												|| !NS.txtAforoImporte
												.getValue() == null)
										NS.aforoImporte = parseFloat(NS.txtAforoImporte
												.getValue());
									else
										NS.aforoImporte = 0;
									if (!BFwrk.Util
											.unformatNumber(NS.txtPuntos
													.getValue()) == ''
														|| !BFwrk.Util
														.unformatNumber(NS.txtPuntos
																.getValue()) == null)
										NS.puntos = parseFloat(BFwrk.Util
												.unformatNumber(NS.txtPuntos
														.getValue()));
									else
										NS.puntos = 0;
									if (!NS.txtTasaPonderada
											.getValue() == ''
												|| !NS.txtTasaPonderada
												.getValue() == null)
										NS.tasaPonderada = parseFloat(NS.txtTasaPonderada
												.getValue());
									else
										NS.tasaPonderada = 0;
									if (!NS.txtTasaPostura
											.getValue() == ''
												|| !NS.txtTasaPostura
												.getValue() == null)
										NS.tasaPostura = parseFloat(NS.txtTasaPostura
												.getValue());
									else
										NS.tasaPostura = 0;
									if (!NS.txtSobreVenta
											.getValue() == ''
												|| !NS.txtSobreVenta
												.getValue() == null)
										NS.tasaSobreVenta = parseFloat(NS.txtSobreVenta
												.getValue());
									else
										NS.tasaSobreVenta = 0;
									if (!NS.txtSobreVenta
											.getValue() == ''
												|| !NS.txtSobreVenta
												.getValue() == null)
										NS.tasaSobreTasaSB = parseFloat(NS.txtSobreVenta
												.getValue());
									else
										NS.tasaSobreTasaSB = 0;
									if (!NS.txtWhtax
											.getValue() == ''
												|| !NS.txtWhtax
												.getValue() == null)
										NS.whtax = parseFloat(NS.txtWhtax
												.getValue());
									else
										NS.whtax = 0;
									if (!NS.txtRentaDep
											.getValue() == ''
												|| !NS.txtRentaDep
												.getValue() == null)
										NS.rentaDep = parseFloat(NS.txtRentaDep
												.getValue());
									else
										NS.rentaDep = 0;
									if (!NS.txtMontoMora
											.getValue() == ''
												|| !NS.txtMontoMora
												.getValue() == null)
										NS.montoMora = parseFloat(NS.txtMontoMora
												.getValue());
									else
										NS.montoMora = 0;
									if (!NS.txtTasaMora
											.getValue() == ''
												|| !NS.txtTasaMora
												.getValue() == null)
										NS.tasaMora = parseFloat(NS.txtTasaMora
												.getValue());
									else
										NS.tasaMora = 0;
									if (!NS.txtValorFacturas
											.getValue() == ''
												|| !NS.txtValorFacturas
												.getValue() == null)
										NS.valorFacturas = parseFloat(NS.txtValorFacturas
												.getValue());
									else
										NS.valorFacturas = 0;
									if (NS.chkComision
											.getValue())
										NS.comision = 1;
									else
										NS.comision = 0;
									NS.fechaID = BFwrk.Util
									.changeDateToString(''
											+ NS.txtFecDisp
											.getValue());
									NS.fechaIDV = BFwrk.Util
									.changeDateToString(''
											+ NS.txtFecVenDisp
											.getValue());
									var tiempo = new Date();
									var hora = tiempo
									.getHours();
									var minuto = tiempo
									.getMinutes()
									.toString();
									if (minuto.length == 1)
										minuto = "0"
										+ minuto;
									NS.horaActual = hora
									+ ":"
									+ minuto;
									if (NS.proyecto == 'CIE') {
										AltaFinanciamientoAction
										.updateLineaBancoCheq(
												NS.vsFinanc,
												NS.bncoDis,
												NS.cmbChequeraDis
												.getValue(),
												function(
														mapResult,
														e) {
													BFwrk.Util
													.msgWait(
															'Terminado...',
															false);
													if (mapResult.result !== null
															&& mapResult.result !== ''
																&& mapResult.result != undefined
																&& mapResult.result != 0) {
														// contabiliza
														NS.storeSelectDivision
														.load({
															params : {
																piBanco : NS.txtBancoDis
																.getValue(),
																psChequera : NS.cmbChequeraDis
																.getValue(),
															},
															callback : function(
																	records,
																	operation,
																	success) {
																if (records.length > 0)
																	NS.vsDivision = records[0]
																.get('idDivision');
																AltaFinanciamientoAction
																.obtieneFolio(
																		"no_folio_param",
																		function(
																				resultado,
																				e) {
																			if (resultado != ''
																				&& resultado != null
																				&& resultado != undefined) {
																				NS.plFolio = resultado;
																				NS.plDisp = NS.vlDisp;
																				NS.folioS = NS.plFolio
																				.toString();
																				NS.caja = NS.idCaja
																				.toString();
																				NS.plDispS = NS.plDisp
																				.toString();
																				AltaFinanciamientoAction
																				.inserta1(
																						NS.folioS,
																						0,
																						parseInt(NS.vsFormaPago),
																						3120,
																						parseInt(NS.plDisp),
																						NS.txtEquivalencia
																						.getValue()
																						.trim(),
																						NS.fechaID,
																						NS.fechaID,
																						NS.montoDisposicion,
																						NS.montoDisposicion,
																						NS.caja,
																						NS.txtDivisa
																						.getValue(),
																						NS.txtDivisa
																						.getValue(),
																						"CRD",
																						"",
																						"Ingreso por credito bancario "
																						+ parseInt(NS.plDisp),
																						1,
																						"A",
																						"S",
																						"P",
																						NS.txtBancoDis
																						.getValue(),
																						NS.cmbChequeraDis
																						.getValue(),
																						"",
																						"CRD",
																						"Ingreso por credito bancario"
																						+ NS.plDisp,
																						1,
																						0,
																						0,
																						NS.plFolio,
																						NS.montoDisposicion,
																						0,
																						"A",
																						"0",
																						NS.horaActual,
																						"",
																						NS.vsDivision,
																						0,
																						"",
																						NS.plDispS,
																						NS.cmbEquivalencia
																						.getValue()
																						.trim(),
																						NS.txtEquivalencia
																						.getValue()
																						.trim(),
																						NS.txtBancoDis
																						.getValue(),
																						NS.cmbChequeraDis
																						.getValue()
																						.toString()
																						.trim(),
																						"",
																						NS.txtLinea
																						.getValue(),
																						parseInt(NS.txtEmpresa.getValue()),
																						function(
																								mapResult,
																								e) {
																							BFwrk.Util
																							.msgWait(
																									'Terminado...',
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
																							}
																							if (mapResult.result > 0) {
																								if (mapResult.plresp == 0) {
																									AltaFinanciamientoAction
																									.altaDisposicion(
																											NS.cmbFinanciamiento
																											.getValue(),
																											NS.txtEmision
																											.getValue()
																											.trim(),
																											NS.txtDivisaD
																											.getValue(),
																											NS.montoDisposicion,
																											NS.fechaID,
																											NS.fechaIDV,
																											NS.sobreTasa,
																											NS.aforoPorciento,
																											NS.aforoImporte,
																											NS.vsPlazoLargo,
																											'',
																											'',
																											'',
																											// NS.cmbSP.getValue().trim(),
																											// NS.cmbMoody.getValue().trim(),
																											// NS.cmbFitch.getValue().trim(),
																											NS.vsTipoTasa,
																											NS.vsTasaBase,
																											NS.vlValorTasa,
																											NS.puntos,
																											NS.tasaPonderada,
																											NS.tasaPostura,
																											NS.tasaSobreVenta,
																											NS.tasaSobreTasaSB,
																											NS.vsFormaPago,
																											"A",
																											NS.vlDisp,
																											NS.bncoDis,
																											NS.cmbChequeraDis
																											.getValue(),
																											parseInt(NS.txtTipoFinanDis
																													.getValue()),
																													NS.txtEquivalencia
																													.getValue(),
																													NS.vdRenta,
																													NS.cmbDisp
																													.getValue()
																													.trim(),
																													NS.txtAnexo
																													.getValue(),
																													NS.txtComentarios
																													.getValue(),
																													NS.viPeriodoRenta,
																													NS.whtax,
																													NS.rentaDep,
																													NS.comision,
																													NS.montoMora,
																													NS.tasaMora,
																													NS.valorFacturas,
																													function(
																															mapResult,
																															e) {
																												BFwrk.Util
																												.msgWait(
																														'Terminado...',
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
																												}
																												if (mapResult.result > 0) {
																													if (NS.pbReestructura) {
																														AltaFinanciamientoAction
																														.updateAmortizacionReestructurada(
																																NS.cmbFinanciamiento
																																.getValue(),
																																parseInt(NS.cmbDisp
																																		.getValue()),
																																		function(
																																				mapResult,
																																				e) {
																																	BFwrk.Util
																																	.msgWait(
																																			'Terminado...',
																																			false);
																																	NS.cmbContratos
																																	.setValue(NS.vsFinanc);
																																	NS.storeDatos.baseParams.clave = NS.cmbContratos
																																	.getValue();
																																	var myMask = new Ext.LoadMask(
																																			Ext
																																			.getBody(),
																																			{
																																				store : NS.storeDatos,
																																				msg : "Cargando..."
																																			});
																																	NS.storeDatos
																																	.load({
																																		callback : function(
																																				records,
																																				operation,
																																				success) {
																																			NS
																																			.limpiar();
																																			NS
																																			.editarDisposicion(true);
																																			NS.cmdAmortizaciones
																																			.setDisabled(false);
																																			NS.cmdRentas
																																			.setDisabled(false);
																																			NS.cmbDisp
																																			.reset();
																																			NS.storeDisposiciones.baseParams.psIdContrato = NS.txtLinea
																																			.getValue();
																																			NS.storeDisposiciones.baseParams.pbEstatus = true;
																																			var myMask = new Ext.LoadMask(
																																					Ext
																																					.getBody(),
																																					{
																																						store : NS.storeDisposiciones,
																																						msg : "Cargando..."
																																					});
																																			NS.cmbDisp
																																			.setValue(NS.vlDisp);
																																		}
																																	});
																																});
																													}
																													NS
																													.limpiar();
																													NS
																													.editarDisposicion(true);
																													NS.cmbContratos
																													.setValue(NS.vsFinanc);
																													NS.storeDatos.baseParams.clave = NS.cmbContratos
																													.getValue();
																													var myMask = new Ext.LoadMask(
																															Ext
																															.getBody(),
																															{
																																store : NS.storeDatos,
																																msg : "Cargando..."
																															});
																													NS.storeDatos
																													.load({
																														callback : function(
																																records,
																																operation,
																																success) {
																															NS.cmdAmortizaciones
																															.setDisabled(false);
																															NS.cmdRentas
																															.setDisabled(false);
																															NS.cmbDisp
																															.reset();
																															NS.storeDisposiciones.baseParams.psIdContrato = NS.txtLinea
																															.getValue();
																															NS.storeDisposiciones.baseParams.pbEstatus = true;
																															var myMask = new Ext.LoadMask(
																																	Ext
																																	.getBody(),
																																	{
																																		store : NS.storeDisposiciones,
																																		msg : "Cargando..."
																																	});
																															NS.cmbDisp
																															.setValue(NS.vlDisp);
																															var myMask = new Ext.LoadMask(
																																	Ext
																																	.getBody(),
																																	{
																																		store : NS.storeSelectDisp,
																																		msg : "Cargando..."
																																	});
																															NS.storeSelectDisp
																															.load({
																																params : {
																																	psIdContrato : NS.txtLinea
																																	.getValue(),
																																	psIdDisp : parseInt(NS.cmbDisp
																																			.getValue())
																																}
																															});
																														}
																													});
																												}
																											});
																								}
																							}
																						});
																			}
																		});
															}
														});
													}
												});
									} else {
										AltaFinanciamientoAction
										.altaDisposicion(
												NS.cmbFinanciamiento
												.getValue(),
												NS.txtEmision
												.getValue()
												.trim(),
												NS.txtDivisaD
												.getValue(),
												NS.montoDisposicion,
												NS.fechaID,
												NS.fechaIDV,
												NS.sobreTasa,
												NS.aforoPorciento,
												NS.aforoImporte,
												NS.vsPlazoLargo,
												'',
												'',
												'',
												// NS.cmbSP.getValue().trim(),
												// NS.cmbMoody.getValue().trim(),
												// NS.cmbFitch.getValue().trim(),
												NS.vsTipoTasa,
												NS.vsTasaBase,
												NS.vlValorTasa,
												NS.puntos,
												NS.tasaPonderada,
												NS.tasaPostura,
												NS.tasaSobreVenta,
												NS.tasaSobreTasaSB,
												NS.vsFormaPago,
												"A",
												NS.vlDisp,
												NS.bncoDis,
												NS.cmbChequeraDis
												.getValue(),
												parseInt(NS.txtTipoFinanDis
														.getValue()),
														NS.txtEquivalencia
														.getValue(),
														NS.vdRenta,
														NS.cmbDisp
														.getValue()
														.trim(),
														NS.txtAnexo
														.getValue(),
														NS.txtComentarios
														.getValue(),
														NS.viPeriodoRenta,
														NS.whtax,
														NS.rentaDep,
														NS.comision,
														NS.montoMora,
														NS.tasaMora,
														NS.valorFacturas,
														function(
																mapResult,
																e) {
													BFwrk.Util
													.msgWait(
															'Terminado...',
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
													}
													if (mapResult.result > 0) {
														if (NS.pbReestructura) {
															/*
															 * If
															 * vsTipoMenu =
															 * "F"
															 * Then
															 * viDisp =
															 * 0
															 * vdMontoFacturas =
															 * 0
															 * End
															 * If
															 */
															AltaFinanciamientoAction
															.updateAmortizacionReestructurada(
																	NS.cmbFinanciamiento
																	.getValue(),
																	parseInt(NS.cmbDisp
																			.getValue()),
																			function(
																					mapResult,
																					e) {
																		BFwrk.Util
																		.msgWait(
																				'Terminado...',
																				false);
																		// If
																		// vsTipoMenu
																		// =
																		// "F"
																		// Then
																		// cmdFacturas.Enabled
																		// =
																		// True
																		NS.cmbContratos
																		.setValue(NS.vsFinanc);
																		NS.storeDatos.baseParams.clave = NS.cmbContratos
																		.getValue();
																		var myMask = new Ext.LoadMask(
																				Ext
																				.getBody(),
																				{
																					store : NS.storeDatos,
																					msg : "Cargando..."
																				});
																		NS.storeDatos
																		.load({
																			callback : function(
																					records,
																					operation,
																					success) {
																				NS
																				.limpiar();
																				NS
																				.editarDisposicion(true);
																				NS.cmdAmortizaciones
																				.setDisabled(false);
																				NS.cmdRentas
																				.setDisabled(false);
																				NS.cmbDisp
																				.reset();
																				NS.storeDisposiciones.baseParams.psIdContrato = NS.txtLinea
																				.getValue();
																				NS.storeDisposiciones.baseParams.pbEstatus = true;
																				var myMask = new Ext.LoadMask(
																						Ext
																						.getBody(),
																						{
																							store : NS.storeDisposiciones,
																							msg : "Cargando..."
																						});
																				NS.cmbDisp
																				.setValue(NS.vlDisp);
																			}
																		});
																	});
														}
														NS
														.limpiar();
														NS
														.editarDisposicion(true);
														NS.cmbContratos
														.setValue(NS.vsFinanc);
														NS.storeDatos.baseParams.clave = NS.cmbContratos
														.getValue();
														var myMask = new Ext.LoadMask(
																Ext
																.getBody(),
																{
																	store : NS.storeDatos,
																	msg : "Cargando..."
																});
														NS.storeDatos
														.load({
															callback : function(
																	records,
																	operation,
																	success) {
																NS.cmdAmortizaciones
																.setDisabled(false);
																NS.cmdRentas
																.setDisabled(false);
																NS.cmbDisp
																.reset();
																NS.storeDisposiciones.baseParams.psIdContrato = NS.txtLinea
																.getValue();
																NS.storeDisposiciones.baseParams.pbEstatus = true;
																var myMask = new Ext.LoadMask(
																		Ext
																		.getBody(),
																		{
																			store : NS.storeDisposiciones,
																			msg : "Cargando..."
																		});
																NS.cmbDisp
																.setValue(NS.vlDisp);
																var myMask = new Ext.LoadMask(
																		Ext
																		.getBody(),
																		{
																			store : NS.storeSelectDisp,
																			msg : "Cargando..."
																		});
																NS.storeSelectDisp
																.load({
																	params : {
																		psIdContrato : NS.txtLinea
																		.getValue(),
																		psIdDisp : parseInt(NS.cmbDisp
																				.getValue())
																	}
																});
															}
														});
													}
												});
									}
								}
							});
						}
					});
				}
			});
		}
	}
	NS.cmdAceptarDis = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAceptarDis',
		name : PF + 'cmdAceptarDis',
		text : 'Aceptar',
		disabled : true,
		x : 420,
		y : 285,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					if (!NS.pbModificarDisp)
						NS.aceptarDis();
					if (NS.pbModificarDisp)
						NS.modificarDatosDis();

				}
			}
		}
	});
	NS.cmdCancelar = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCancelar',
		name : PF + 'cmdCancelar',
		text : 'Cancelar',
		x : 510,
		y : 285,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.cancelar();
				}
			}
		}
	});
	NS.modificaDis = function() {
		NS.editarDisposicion(false);
		NS.pbModificarDisp = true;
		NS.txtPuntos.setDisabled(false);
		NS.pbConsult = false;
		if (NS.oTasa == '0')
			NS.txtTasaFija.setDisabled(false);

	};
	NS.cmdModificar1 = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdModificar1',
		name : PF + 'cmdModificar1',
		text : 'Modificar',
		disabled : true,
		x : 600,
		y : 285,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.modificaDis();
				}
			}
		}
	});
	NS.eliminarDisposicion = function() {
		if (NS.cmbDisp.getValue() > 0) {
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
					' Se va a eliminar la disposición. ¿Desea continuar??',
					function(btn) {
						if (btn === 'yes') {
							NS.storeFunSelectExisteAmort
							.load({
								params : {
									psLinea : NS.cmbContratos
									.getValue(),
									psCredito : NS.cmbDisp
									.getValue()

								},
								callback : function(
										records,
										operation,
										success) {
									if (records.length > 0) {
										Ext.sg
										.alert(
												'SET',
												'No se puede eliminar la disposición, tiene amortizaciones registradas.');
									} else {
										try {
											AltaFinanciamientoAction
											.deleteAmortizacion(
													NS.cmbContratos
													.getValue(),
													parseInt(NS.cmbDisp
															.getValue()),
															false,
															true,
															function(
																	mapResult,
																	e) {
														BFwrk.Util
														.msgWait(
																'Terminado...',
																false);

													});
											AltaFinanciamientoAction
											.deleteAGAsigLin(
													NS.cmbContratos
													.getValue(),
													parseInt(NS.cmbDisp
															.getValue()),
															function(
																	mapResult,
																	e) {
														BFwrk.Util
														.msgWait(
																'Terminado...',
																false);
													});
											AltaFinanciamientoAction
											.deleteFactFacturas(
													NS.cmbContratos
													.getValue(),
													parseInt(NS.cmbDisp
															.getValue()),
															function(
																	mapResult,
																	e) {
														BFwrk.Util
														.msgWait(
																'Terminado...',
																false);
													});
											AltaFinanciamientoAction
											.cancelaMovimiento(
													NS.cmbContratos
													.getValue(),
													parseInt(NS.cmbDisp
															.getValue()),
															function(
																	mapResult,
																	e) {
														BFwrk.Util
														.msgWait(
																'Terminado...',
																false);
													});
											Ext.Msg
											.alert(
													'SET',
											'La disposición ha sido eliminada');
											NS
											.cancelar();
											NS
											.limpiar();
										} catch (err) {
											Ext.Msg
											.alert(
													'SET',
											'Falló al eliminar la disposición');
										}
									}
								}
							});
						}
					});
		}

	}
	NS.cmdEliminar1 = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdEliminar1',
		name : PF + 'cmdEliminar1',
		text : 'Eliminar',
		disabled : true,
		x : 690,
		y : 285,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.eliminarDisposicion();

				}
			}
		}
	});
	NS.cmdImprimir1 = new Ext.Button(
			{
				xtype : 'button',
				id : PF + 'cmdImprimir1',
				name : PF + 'cmdImprimir1',
				text : 'Imprimir',
				disabled : true,
				x : 780,
				y : 285,
				width : 80,
				listeners : {
					click : {
						fn : function(e) {
							if (NS.cmbDisp.getValue() == "") {
								Ext.Msg.alert('SET',
										'Seleccione una disposición');
								NS.cmbContratos.focus();
								return;
							}
							var formaPago, tipoTasa;
							var strParams = '';
							strParams = '?nomReporte=ReporteDisposicionCredito';
							strParams += '&' + 'empresa='
							+ NS.GI_NOM_EMPRESA;
							strParams += '&' + 'idFinanciamiento='
							+ NS.cmbContratos.getValue();
							strParams += '&' + 'idDisposicion='
							+ NS.cmbDisp.getValue();
							strParams += '&'
								+ 'fechaFinal='
								+ BFwrk.Util
								.changeDateToString(''
										+ Ext
										.getCmp(
												PF
												+ 'txtFecVenDisp')
												.getValue());
							strParams += '&' + 'montoDisposicion='
							+ NS.txtMontoDisposicion.getValue();

							if (NS.oFormaPago == '0')
								formaPago = 'TRANSFERENCIA';
							else
								formaPago = 'CARGO EN CUENTA';
							strParams += '&' + 'formaPago=' + formaPago;
							strParams += '&' + 'valorTasa='
							+ NS.txtBase.getValue();
							strParams += '&' + 'tasaFija='
							+ NS.txtTasaFija.getValue();
							strParams += '&' + 'valorTasa='
							+ NS.txtBase.getValue();
							strParams += '&' + 'banco='
							+ NS.cmbBancoDis.getRawValue();
							strParams += '&' + 'divisa='
							+ NS.cmbDivisaD.getRawValue();
							if (NS.oTasa == '0')
								tipoTasa = 'VARIABLE';
							else
								tipoTasa = 'FIJA';
							strParams += '&' + 'tipoTasa=' + tipoTasa;
							strParams += '&' + 'puntos='
							+ NS.txtPuntos.getValue();
							strParams += '&' + 'clabe='
							+ NS.cmbChequeraDis.getValue();
							strParams += '&'
								+ 'fechaInicio='
								+ BFwrk.Util.changeDateToString(''
										+ NS.txtFecDisp.getValue());
							strParams += '&' + 'tasaBase='
							+ NS.cmbTasa.getRawValue();
							strParams += '&' + 'tasaVigente='
							+ NS.txtTasaVigente.getValue();
							window.open("/SET/jsp/Reportes.jsp"
									+ strParams);
						}
					}
				}
			});

	// ********************************************************TABLA DE
	// AMORTIZACIONES***************************************

	NS.editarAmortizacionInt = function(pbEditar) {
		NS.cmbDiaCorteInt.setDisabled(pbEditar);
		NS.txtComentario.setDisabled(pbEditar);
		NS.cmbPeriodoInt.setDisabled(pbEditar);
		NS.txtIva.setDisabled(pbEditar);
		// If vsTipoMenu = "B" Then chkSobreTasa.Enabled = True
		NS.txtFecVenInt.setDisabled(!pbEditar);
		NS.txtInteres.setDisabled(!pbEditar);
		// txtInteres.Locked = Not pbEditar
		NS.txtInteres.setValue("0.00");
	}
	NS.habilitaTodo = function(vbOpcion) {
		NS.optPeriodCapital.setDisabled(vbOpcion);
		NS.optPeriodIntereses.setDisabled(vbOpcion);
		NS.cmbDiaCorte.setDisabled(vbOpcion);
		NS.cmbDiaCorteInt.setDisabled(vbOpcion);
		NS.cmbPeriodo.setDisabled(vbOpcion);
		NS.cmbPeriodoInt.setDisabled(vbOpcion);
		NS.txtDiasC.setDisabled(vbOpcion);
		NS.txtDiasInt.setDisabled(vbOpcion);
		NS.txtNoAmort.setDisabled(vbOpcion);
		NS.txtNoAmortInt.setDisabled(vbOpcion);
		NS.chkPeriodoGracia.setDisabled(vbOpcion);
		NS.txtPeriodoGracia.setDisabled(vbOpcion);
		NS.txtFecVenC.setDisabled(vbOpcion);
		NS.txtFecVenInt.setDisabled(vbOpcion);
		NS.txtCapital.setDisabled(vbOpcion);
		NS.txtInteres.setDisabled(vbOpcion);
		NS.chkPrimerPagoInt.setDisabled(vbOpcion);
		NS.txtPrimerPagoInt.setDisabled(vbOpcion);
		// / If vsTipoMenu = "B" Then chkSobreTasa.Enabled = vbOpcion
		NS.txtComentario.setDisabled(vbOpcion);
		NS.txtTotalCap.setDisabled(vbOpcion);
		NS.txtTotalInt.setDisabled(vbOpcion);
		NS.txtTotal.setDisabled(vbOpcion);
		NS.txtIvaC.setDisabled(vbOpcion);
		NS.txtFactorCapital.setDisabled(vbOpcion);
		NS.txtIva.setDisabled(vbOpcion);
		NS.chkRenta.setDisabled(vbOpcion);
	}
	NS.limpiaTodo = function() {
		NS.storeCmbPeriodo.removeAll();
		NS.storeCmbPeriodo2.removeAll();
		NS.storeSolicitudes.removeAll();
		NS.storeSolicitudes2.removeAll();
		NS.optPeriodCapital.setValue('');
		NS.optPeriodIntereses.setValue('');
		NS.cmbDiaCorte.reset();
		NS.cmbDiaCorte.setValue('');
		NS.cmbDiaCorteInt.reset();
		NS.cmbDiaCorteInt.setValue('');
		NS.cmbPeriodo.reset();
		NS.cmbPeriodo.setValue('');
		NS.cmbPeriodoInt.reset();
		NS.cmbPeriodoInt.setValue('');
		NS.txtDiasC.setValue('');
		NS.txtDiasInt.setValue('');
		NS.txtNoAmort.setValue('');
		NS.txtNoAmortInt.setValue('0');
		NS.chkPeriodoGracia.setValue(0);
		NS.txtPeriodoGracia.setValue(NS.fecHoy);
		NS.txtFecVenC.setValue(NS.txtFecVenDisp.getValue());
		NS.txtFecVenInt.setValue(NS.txtFecVenDisp.getValue());
		NS.txtCapital.setValue("0.00");
		NS.txtInteres.setValue("0.00");
		NS.chkPrimerPagoInt.setValue(0);
		NS.txtPrimerPagoInt.setValue(NS.fecHoy);
		// If vsTipoMenu = "F" Then chkSobreTasa.Value = 0
		NS.optD = '0';
		NS.optDias.setValue(0);
		NS.txtFecVenC.setDisabled(false);
		NS.txtCapital.setDisabled(false);
		NS.txtComentario.setValue('');
		NS.txtTotalCap.setValue("0.00");
		NS.txtTotalInt.setValue("0.00");
		NS.txtTotal.setValue("0.00");
		NS.txtIvaC.setValue("0.00");
		NS.txtFactorCapital.setValue("0.00");
		NS.txtIva.setValue("0.00");
		NS.chkRenta.setValue(0);
	}
	NS.editarAmortizacion = function(pbEditar) {
		NS.cmbDiaCorte.setDisabled(pbEditar);
		NS.cmbPeriodo.setDisabled(pbEditar);
		NS.txtFecVenC.setDisabled(!pbEditar);
		NS.txtCapital.setDisabled(!pbEditar);
		NS.txtCapital.setValue("0.00");
		NS.lblIvaC.setDisabled(pbEditar);
		NS.txtIvaC.setDisabled(pbEditar);
		NS.lblFactorCapital.setDisabled(pbEditar);
		NS.txtFactorCapital.setDisabled(pbEditar);
		NS.chkRenta.setDisabled(pbEditar);
	}
	NS.limpiarValores = function() {
		NS.txtTotalInt.setValue("0.00");
		NS.plConsAmort = 0;
		NS.pdSaldoInsoluto = BFwrk.Util
		.unformatNumber(NS.txtMontoDisposicion.getValue());
		NS.psInteresSind = 0;
		NS.psPorSind = "";
		NS.psIdBanco = "";
		NS.txtComentario.setValue("");
		NS.txtDiasInt.setValue("");
		NS.cmbDiaCorteInt.reset();
		NS.cmbPeriodoInt.reset();
		NS.txtNoAmortInt.setValue(0);
		NS.pdArrInteres = new Array();
		NS.pdArrPor = new Array();
		NS.pdArrIdBanco = new Array();
	}
	NS.pruebaCalculaPeriodosCapNuevo = function(pbCap, i) {
		var vsFecIni, vsFecVen, vsFecVenAux, vsFecVenAux1, dFecPagoCapital, vlNoDiasMes, viDias, viDiaCorte, iC, iDiasFaltantes, band, mes, anio, fecha2, fec, dt;
		iDiasFaltantes = 0;
		var cadena = '';
		viDiaCorte = 0;
		iC = 0;
		vsFecIni = NS.txtFecDisp.getValue();
		vsFecVen = NS.txtFecVenDisp.getValue();
		vsFecVenAux = vsFecVen;
		vsArrayFechas = new Array();
		if (pbCap) {
			NS.validaDatosPeriodico();
			var validaDatosP = NS.bandP;
			if (validaDatosP) {
				viDias = parseInt(NS.txtDiasC.getValue());
				if (NS.cmbDiaCorte.getValue() != "")
					viDiaCorte = parseInt(NS.cmbDiaCorte.getValue());
				else
					viDiaCorte = 0;
				band = true;
			} else {
				band = false;
			}
		} else {
			NS.validaDatosInteres();
			var validaDatosPI = NS.bandPI;
			if (validaDatosPI) {
				viDias = parseInt(NS.txtDiasInt.getValue());
				if (NS.cmbDiaCorteInt.getValue() != "")
					viDiaCorte = parseInt(NS.cmbDiaCorteInt.getValue());
				else
					viDiaCorte = 0;
				band = true;
			} else {
				band = false;
			}
		}
		if (band) {
			if (viDias == 0) {
				i = 0;
				vsArrayFechas = new Array();
				vsArrayFechas[0] = vsFecVen;
			} else {
				if (!pbCap) {
					iC = 0;
				}
				while (vsFecIni < vsFecVen) {
					if (i > 0)
						vsFecIni = vsFecVenAux;
					if (NS.chkPeriodoGracia.getValue() == true
							&& i == 0 && pbCap) {
						vsFecVenAux = NS.txtPeriodoGracia.getValue();
					} else if (NS.chkPrimerPagoInt.getValue() == true
							&& i == 0 && !pbCap) {
						vsFecVenAux = NS.txtPrimerPagoInt.getValue();
					} else if (iDiasFaltantes != 0 && !pbCap) {
						vsFecVenAux = vsFecVenAux1;
						iDiasFaltantes = 0;
					} else {
						mes = vsFecIni.getMonth() + 1;
						anio = vsFecIni.getFullYear();
						fecha2 = "01/" + mes + "/" + anio;
						fec = fecha2.split("/");
						dt = new Date(parseInt(fec[2], 10), parseInt(
								fec[1], 10) - 1, parseInt(fec[0], 10));
						dt.setMonth(dt.getMonth() + 1);
						dt.setDate(dt.getDate() - 1);
						vlNoDiasMes = parseInt(dt.getDate());
						if (viDiaCorte > parseInt(vsFecIni.getDate())
								&& i == 0 && viDiaCorte <= vlNoDiasMes) {
							mes = vsFecIni.getMonth() + 1;
							anio = vsFecIni.getFullYear();
							fecha2 = "01/" + mes + "/" + anio;
							fec = fecha2.split("/");
							dt = new Date(parseInt(fec[2], 10),
									parseInt(fec[1], 10) - 1, parseInt(
											fec[0], 10));
							dt.setMonth(dt.getMonth() + 1);
							dt.setDate(dt.getDate() - 1);
							vlNoDiasMes = parseInt(dt.getDate());
						} else {
							if ((viDias % 30) == 0) {
								mes = vsFecIni.getMonth() + 1;
								anio = vsFecIni.getFullYear();
								fecha2 = "01/" + mes + "/" + anio;
								fec = fecha2.split("/");
								dt = new Date(parseInt(fec[2], 10),
										parseInt(fec[1], 10) - 1,
										parseInt(fec[0], 10));
								dt.setMonth(dt.getMonth()
										+ (parseInt(viDias / 30) + 1));
								dt.setDate(dt.getDate() - 1);
								vlNoDiasMes = parseInt(dt.getDate());
							} else {
								vlNoDiasMes = 0;
							}
						}
						if (viDiaCorte <= vlNoDiasMes) {
							mes = vsFecIni.getMonth() + 1;
							anio = vsFecIni.getFullYear();
							fecha2 = "01/" + mes + "/" + anio;
							fec = fecha2.split("/");
							dt = new Date(parseInt(fec[2], 10),
									parseInt(fec[1], 10) - 1, parseInt(
											fec[0], 10));
							dt.setMonth(dt.getMonth() + 1);
							dt.setDate(dt.getDate() - 1);
							vlNoDiasMes = parseInt(dt.getDate());
							if (viDiaCorte > vsFecIni.getDate()
									&& i == 0
									&& viDiaCorte <= vlNoDiasMes) {
								mes = vsFecIni.getMonth() + 1;
								anio = vsFecIni.getFullYear();
								vsFecVenAux = viDiaCorte + "/" + mes
								+ "/" + anio;
							} else {
								if ((viDias % 30) == 0) {
									dt = vsFecIni;
									dt.setMonth(dt.getMonth()
											+ (parseInt(viDias / 30)));
									vsFecVenAux = viDiaCorte + "/"
									+ (dt.getMonth() + 1) + "/"
									+ dt.getFullYear();
								} else {
									dt = vsFecIni;
									dt.setDate(dt.getDate() + viDias);
									vsFecVenAux = dt;
								}
							}
						} else {
							if ((viDias % 30) == 0) {
								dt = vsFecIni;
								dt.setMonth(dt.getMonth()
										+ (parseInt(viDias / 30)));
								vsFecVenAux = vlNoDiasMes + "/"
								+ (dt.getMonth()) + "/"
								+ dt.getFullYear();
							} else {
								dt = vsFecIni;
								dt.setDate(dt.getDate() + viDias);
								vsFecVenAux = dt;
							}
						}
					}
					if (!pbCap) {
						var data = new Array();
						NS.storeSolicitudes.each(function(records) {
							data.push(records.get('fecVen'));
						});
						var fec = data[iC].split("/");
						var dt = new Date(parseInt(fec[2], 10),
								parseInt(fec[1], 10) - 1, parseInt(
										fec[0], 10));
						dFecPagoCapital = dt;
						try {
							var fec2 = vsFecVenAux.split("/");
							var dt2 = new Date(parseInt(fec2[2], 10),
									parseInt(fec2[1], 10) - 1,
									parseInt(fec2[0], 10));
							vsFecVenAux = dt2;
						} catch (err) {
							vsFecVenAux = vsFecVenAux;
						}
						if (dFecPagoCapital < vsFecVenAux) {
							vsFecVenAux1 = vsFecVenAux;
							vsFecVenAux = dFecPagoCapital;
							iDiasFaltantes = NS.restaFechas(BFwrk.Util
									.changeDateToString(''
											+ dFecPagoCapital),
											BFwrk.Util.changeDateToString(''
													+ vsFecVenAux1));
							iC = iC + 1;
						} else {
							iDiasFaltantes = 0;
						}
					}
					try {
						fec = vsFecVenAux.split("/");
						vsFecVenAux = new Date(parseInt(fec[2], 10),
								parseInt(fec[1], 10) - 1, parseInt(
										fec[0], 10));
					} catch (err) {
						vsFecVenAux = vsFecVenAux;
					}
					if (vsFecVenAux > vsFecVen)
						vsFecVenAux = vsFecVen;
					vsFecIni = vsFecVenAux;
					vsArrayFechas[i] = vsFecVenAux;
					cadena += BFwrk.Util.changeDateToString(''
							+ vsFecVenAux)
							+ " ";
					i = i + 1;
				} 
			}
			NS.cadenaFechas = cadena;
			NS.i = i;

		}
	}
	// CAPITAL
	NS.optPeriodCapital = new Ext.form.RadioGroup(
			{
				id : PF + 'optPeriodCapital',
				name : PF + 'optPeriodCapital',
				x : 0,
				y : 0,
				columns : 2,
				items : [
				         {
				        	 boxLabel : 'No periódica',
				        	 name : 'optPC',
				        	 checked : true,
				        	 inputValue : 0,
				        	 listeners : {
				        		 check : {
				        			 fn : function(opt, valor) {
				        				 if (valor == true) {
				        					 NS.oPeriodC = '0';
				        					 NS.chkPeriodoGracia
				        					 .setDisabled(true);
				        					 NS.chkPeriodoGracia
				        					 .setValue(false);
				        					 NS.editarAmortizacion(true);
				        					 NS.limpiarValores();
				        					 NS.pdSaldoInsoluto = BFwrk.Util
				        					 .unformatNumber(NS.txtMontoDisposicion
				        							 .getValue());
				        					 NS.txtFecVenC.focus();
				        					 NS.lblIvaC
				        					 .setDisabled(false);
				        					 NS.txtIvaC
				        					 .setDisabled(false);
				        					 NS.lblFactorCapital
				        					 .setDisabled(false);
				        					 NS.txtFactorCapital
				        					 .setDisabled(false);
				        					 NS.txtDiasC
				        					 .setDisabled(true);
				        				 }
				        			 }
				        		 }
				        	 }
				         },
				         {
				        	 boxLabel : 'Periódica',
				        	 name : 'optPC',
				        	 inputValue : 1,
				        	 listeners : {
				        		 check : {
				        			 fn : function(opt, valor) {
				        				 if (valor == true) {
				        					 NS.oPeriodC = '1';
				        					 NS.chkPeriodoGracia
				        					 .setDisabled(false);
				        					 NS
				        					 .editarAmortizacion(false);
				        					 NS.limpiarValores();
				        					 NS.txtDiasC
				        					 .setDisabled(false);
				        					 var myMask = new Ext.LoadMask(
				        							 Ext.getBody(),
				        							 {
				        								 store : NS.storeCmbPeriodo,
				        								 msg : "Cargando..."
				        							 });
				        					 NS.storeCmbPeriodo.load();
				        				 }
				        			 }
				        		 }
				        	 }
				         } ]
			});
	NS.lblPeriodoC = new Ext.form.Label({
		text : 'Periodo:',
		x : 0,
		y : 60
	});
	NS.storeCmbPeriodo = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			pdAmort : true
		},
		paramOrder : [ 'pdAmort' ],
		directFn : AltaFinanciamientoAction.funSQLComboPeriodo,
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
	NS.cmbPeriodo = new Ext.form.ComboBox({
		store : NS.storeCmbPeriodo,
		name : PF + 'cmbPeriodo ',
		id : PF + 'cmbPeriodo ',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 0,
		y : 75,
		width : 140,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione un periodo',
		triggerAction : 'all',
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {
					NS.txtDiasC.setDisabled(false);
					NS.txtDiasC.focus();
				}
			}
		}
	});
	NS.storeDias = new Ext.data.SimpleStore({
		idProperty : 'id',
		fields : [ {
			name : 'id'
		} ]
	});
	NS.dataDia = [ [ '1' ], [ '2' ], [ '3' ], [ '4' ], [ '5' ],
	               [ '6' ], [ '7' ], [ '8' ], [ '9' ], [ '10' ], [ '11' ],
	               [ '12' ], [ '13' ], [ '14' ], [ '15' ], [ '16' ], [ '17' ],
	               [ '18' ], [ '19' ], [ '20' ], [ '21' ], [ '22' ], [ '23' ],
	               [ '24' ], [ '25' ], [ '26' ], [ '27' ], [ '28' ], [ '29' ],
	               [ '30' ], [ '31' ] ];
	NS.storeDias.loadData(NS.dataDia);

	NS.lblDiaCorte = new Ext.form.Label({
		text : 'Día corte:',
		x : 240,
		y : 0
	});
	NS.cmbDiaCorte = new Ext.form.ComboBox({
		store : NS.storeDias,
		name : PF + 'cmbDiaCorte ',
		id : PF + 'cmbDiaCorte ',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 240,
		y : 17,
		width : 50,
		valueField : 'id',
		displayField : 'id',
		autocomplete : true,
		emptyText : 'Día',
		triggerAction : 'all',
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {

				}
			}
		}
	});
	NS.lblDia = new Ext.form.Label({
		text : 'Días:',
		x : 240,
		y : 60
	});
	NS.txtDiasC = new Ext.form.TextField({
		id : PF + 'txtDiasC',
		name : PF + 'txtDiasC',
		disabled : true,
		x : 240,
		y : 75,
		width : 50,
		listeners : {
			change : {
				fn : function(caja, valor) {
					if (NS.oPeriodC == '1') {
						NS.i = 0;
						NS.pruebaCalculaPeriodosCapNuevo(true, NS.i);
						NS.txtNoAmort.setValue(NS.i);
					}
				}
			}
		}
	});
	NS.chkPeriodoGracia = new Ext.form.Checkbox({
		name : PF + 'chkPeriodoGracia',
		id : 'chkPeriodoGracia',
		x : 15,
		y : 0,
		listeners : {
			check : {
				fn : function(ch, checked) {
					if (checked) {
						NS.txtPeriodoGracia.setDisabled(false);
						NS.txtPeriodoGracia.focus();
						NS.txtDiasC.setValue("");
						NS.txtNoAmort.setValue("");
					} else {
						txtPeriodoGracia.setDisabled(true);
					}
				}
			}
		}
	});
	NS.txtPeriodoGracia = new Ext.form.DateField(
			{
				id : PF + 'txtPeriodoGracia',
				name : PF + 'txtPeriodoGracia',
				format : 'd/m/Y',
				disabled : true,
				x : 40,
				y : 0,
				allowBlank : false,
				width : 100,
				vtype : 'daterange',
				listeners : {
					render : function(datefield) {
						datefield.setValue(NS.fecHoy);
					},
					change : {
						fn : function(caja, valor) {
							if (caja.getValue() == ''
									|| caja.getValue() == null) {
								Ext.Msg
								.alert('SET',
								'Introduzca la fecha de primera amortización');
								NS.txtPeriodoGracia.setValue(NS.fecHoy);
								Ext.getCmp(PF + 'txtPeriodoGracia')
								.focus();
							}
						}
					},
					blur : function(caja, valor) {
						if (caja.getValue() == ''
							|| caja.getValue() == null) {
							Ext.Msg
							.alert('SET',
							'Introduzca la fecha de primera amortización');
							NS.txtPeriodoGracia.setValue(NS.fecHoy);
							Ext.getCmp(PF + 'txtPeriodoGracia').focus();
						}
					},
					dblClick : function(datefield) {
						NS.psFecha = "FECPERGRA";
					}
				}
			});
	NS.lblNoAmort = new Ext.form.Label({
		text : 'Periodos:',
		x : 330,
		y : 60
	});
	NS.txtNoAmort = new Ext.form.TextField({
		id : PF + 'txtNoAmort',
		name : PF + 'txtNoAmort',
		disabled : true,
		x : 330,
		y : 75,
		width : 50,
		listeners : {
			change : {
				fn : function(caja, valor) {
					/*
					 * Ext.getCmp(PF + 'txtMontoIni').setValue(
					 * BFwrk.Util.formatNumber(caja.getValue()));
					 * ('MONTOS', caja.getId(), 'valor');
					 */
				}
			}
		}
	});
	NS.calculaInteres = function(pdFecVen, pvDayDiff, pvRetInteres,
			psCampoFecha) {
		var pdImporteDisp;
		var pdImporteSind;
		var pdTasaSind;
		var pdInteresSind;
		var pdPorSind;
		var pdInteres;
		var pdFecIni;
		var pdDayDif;
		var pdSaldo;
		var psTmpInteresSind;
		var psTmpPorSind;
		var psTmpIdBanco;
		var band = false;
		var i;
		if (!NS.pbCons) {
			if (pvDayDiff == '') {
				if (psCampoFecha.toUpperCase() == "txtFecVenC"
						.toUpperCase()) {
					if (NS.storeSolicitudes.getCount() > 0) {
						var data = new Array();
						NS.storeSolicitudes.each(function(records) {
							data.push(records.get('fecVen'));
						});
						pdFecIni = data[data.length - 1];
					} else {
						pdFecIni = NS.txtFecDisp.getValue();
					}
				} else if (psCampoFecha.toUpperCase() == "txtFecVenInt"
					.toUpperCase()) {
					if (NS.storeSolicitudes2.getCount() > 0) {
						var data = new Array();
						NS.storeSolicitudes2.each(function(records) {
							data.push(records.get('fecVen'));
						});
						pdFecIni = data[data.length - 1];
					} else {
						pdFecIni = NS.txtFecDisp.getValue();
					}
				}
				if (BFwrk.Util.changeDateToString('' + pdFecVen).length > 10)
					NS.restaFechas(BFwrk.Util.changeDateToString(''
							+ pdFecIni), BFwrk.Util
							.changeDateToString('' + pdFecVen));
				if (pdDayDif < 0) {
					Ext.Msg.alert('SET',
							'Seleccione una fecha mayor a la fecha'
							+ pdFecIni);
					if (psCampoFecha.toUpperCase() == "txtFecVen"
						.toUpperCase()) {
						NS.txtFecVenC.setValue(pdFecIni);
						NS.txtFecVenC.focus();
						band = true;
					} else if (psCampoFecha.toUpperCase() == "txtFecVenInt"
						.toUpperCase()) {
						NS.txtFecVenInt.setValue(pdFecIni);
						NS.txtFecVenInt.focus();
						band = false;
					}
				}
			} else {
				if (pvDayDiff > 0)
					pdDayDif = pvDayDiff;
			}
			if (band) {
				var r2;
				if (NS.txtCapital.getValue() == "")
					r2 = 0;
				else
					r2 = parseFloat(BFwrk.Util.unformatNumber(NS.txtCapital.getValue()));
				pdSaldo = parseFloat(NS.pdSaldoInsoluto) - r2;
				if (NS.oD == '0')
					pdInteres = (((NS.pdTasa / 100 / 360) * pdSaldo) * pdDayDif);
				else
					pdInteres = (((NS.pdTasa / 100 / 365) * pdSaldo) * pdDayDif);
			}
		}
	};
	NS.lblVencimiento = new Ext.form.Label({
		text : 'Vencimiento:',
		x : 420,
		y : 60
	});
	NS.txtFecVenC = new Ext.form.DateField({
		id : PF + 'txtFecVenC',
		name : PF + 'txtFecVenC',
		format : 'd/m/Y',
		x : 420,
		y : 75,
		allowBlank : false,
		width : 100,
		vtype : 'daterange',
		listeners : {
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			change : {
				fn : function(caja, valor) {
					NS.calculaInteres(caja.getValue(), '', '',
							"txtFecVenC");
				}
			},
			blur : function(datefield) {
			},
		}
	});
	NS.chkDiasReales = new Ext.form.Checkbox({
		name : PF + 'chkDiasReales',
		id : 'chkDiasReales',
		x : 580,
		y : 13,
		hidden : true,
		// disabled : true,
		listeners : {
			check : {
				fn : function() {

				}
			}
		}
	});
	NS.lblDiasReales1 = new Ext.form.Label({
		text : 'Días',
		x : 600,
		y : 11,
		hidden : true
	});
	NS.lblDiasReales2 = new Ext.form.Label({
		text : ' exactos',
		x : 600,
		y : 21,
		hidden : true
	});
	NS.lblIvaC = new Ext.form.Label({
		text : 'IVA:',
		x : 670,
		y : 0,
		hidden : true
	});
	NS.txtIvaC = new Ext.form.TextField({
		id : PF + 'txtIvaC',
		name : PF + 'txtIvaC',
		hidden : true,
		// disabled : true,
		x : 670,
		y : 15,
		width : 50,
		listeners : {
			change : {
				fn : function(caja, valor) {
					/*
					 * Ext.getCmp(PF + 'txtMontoIni').setValue(
					 * BFwrk.Util.formatNumber(caja.getValue()));
					 * ('MONTOS', caja.getId(), 'valor');
					 */
				}
			}
		}
	});
	NS.lblCapital = new Ext.form.Label({
		text : 'Capital:',
		x : 580,
		y : 60
	});

	NS.txtCapital = new Ext.form.TextField({
		id : PF + 'txtCapital',
		name : PF + 'txtCapital',
		// disabled : true,
		x : 580,
		y : 75,
		value : '0.00',
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {
					 Ext.getCmp(PF + 'txtCapital').setValue(BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	NS.lblFactorCapital = new Ext.form.Label({
		text : 'Factor Capital:',
		x : 770,
		y : 0,
		hidden : true
	});
	NS.txtFactorCapital = new Ext.form.TextField({
		id : PF + 'txtFactorCapital',
		name : PF + 'txtFactorCapital',
		// disabled : true,
		hidden : true,
		x : 770,
		y : 15,
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {
					/*
					 * Ext.getCmp(PF + 'txtMontoIni').setValue(
					 * BFwrk.Util.formatNumber(caja.getValue()));
					 * ('MONTOS', caja.getId(), 'valor');
					 */
				}
			}
		}
	});
	NS.cmdCargaExcel = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCargaExcel',
		name : PF + 'cmdCargaExcel',
		text : 'Importar',
		x : 770,
		hidden : true,
		y : 15,
		// disabled : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {

				}
			}
		}
	});
	NS.chkRenta = new Ext.form.Checkbox({
		name : PF + 'chkRenta',
		id : 'chkRenta',
		x : 770,
		y : 60,
		hidden : true,
		// disabled : true,
		listeners : {
			check : {
				fn : function() {

				}
			}
		}
	});
	NS.lblRentaC = new Ext.form.Label({
		text : 'Solo renta',
		x : 790,
		y : 65,
		hidden : true
	});
	NS.storeSolicitudes = new Ext.data.DirectStore(
			{
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
				paramOrder : [ 'psIdContrato', 'piDisposicion',
				               'pbCambioTasa', 'psTipoMenu', 'psProyecto',
				               'piCapital' ],
				               directFn : AltaFinanciamientoAction.selectAmortizacionesCapital,
				               fields : [ {
				            	   name : 'noAmort'
				               }, {
				            	   name : 'fecIni'
				               }, {
				            	   name : 'fecVen'
				               }, {
				            	   name : 'fecPag'
				               }, {
				            	   name : 'dias'
				               }, {
				            	   name : 'capital'
				               }, {
				            	   name : 'saldo'
				               }, {
				            	   name : 'estatus'
				               }, {
				            	   name : 'descEstatus'
				               }, {
				            	   name : 'diaCortecap'
				               }, {
				            	   name : 'diaCorteint'
				               }, {
				            	   name : 'periodo'
				               }, {
				            	   name : 'noAmortizaciones'
				               }, {
				            	   name : 'diasPeriodo'
				               }, ],
				               listeners : {
				            	   load : function(s, records) {

				            	   }
				               }
			});
	NS.agregarCriterioValor = function(noAmort, fecIni, fecVen, fecPag,
			dias, capital, saldo, estatus, descEstatus) {
		var indice = 0;
		var recordsDatGrid = NS.storeSolicitudes.data.items;
		NS.agregarValorGridDatos(noAmort, fecIni, fecVen, fecPag, dias,
				capital, saldo, estatus, descEstatus, indice, 0);

	};
	// Agrega valor a criterios
	NS.agregarValorGridDatos = function(noAmort, fecIni, fecVen,
			fecPag, dias, capital, saldo, estatus, descEstatus, indice) {
		// var valorCombo = Ext.getCmp(oIdElemento).getValue();
		// var valorCombo = sValor;
		var tamGrid = indice <= 0 ? (NS.storeSolicitudes.data.items).length
				: indice;
		var datosClase = NS.gridDatos.getStore().recordType;
		var datos = new datosClase({
			noAmort : noAmort,
			fecIni : fecIni,
			fecVen : fecVen,
			fecPag : fecPag,
			dias : dias,
			capital : capital,
			saldo : saldo,
			estatus : estatus,
			descEstatus : descEstatus
		});
		NS.gridDatos.stopEditing();
		NS.storeSolicitudes.insert(tamGrid, datos);
		// NS.storeSolicitudes.load();
		NS.gridDatos.getView().refresh();
		NS.gridDatos.getSelectionModel().selectAll();
	};
	NS.smE = new Ext.grid.CheckboxSelectionModel({
		singleSelect : false
	});
	NS.gridDatos = new Ext.grid.GridPanel(
			{
				store : NS.storeSolicitudes,
				id : PF + 'gridDatos',
				name : PF + 'gridDatos',
				frame : true,
				width : 890,
				height : 150,
				x : 0,
				y : 120,
				cm : new Ext.grid.ColumnModel({
					defaults : {
						width : 120,
						value : true,
						sortable : true
					},
					columns : [
					           NS.smE,
					           {
					        	   id : 'noAmort',
					        	   header : 'Amortización',
					        	   width : 100,
					        	   dataIndex : 'noAmort',
					        	   direction : 'ASC'
					           },
					           {
					        	   header : 'Fecha Inicio',
					        	   width : 115,
					        	   dataIndex : 'fecIni',
					           },
					           {
					        	   header : 'Fecha Vencimiento.',
					        	   width : 115,
					        	   dataIndex : 'fecVen',
					           },
					           {
					        	   header : 'Fecha Pago',
					        	   width : 115,
					        	   dataIndex : 'fecPag',
					           },
					           {
					        	   header : 'Días',
					        	   width : 50,
					        	   dataIndex : 'dias',
					           },
					           {
					        	   header : 'Capital',
					        	   width : 120,
					        	   dataIndex : 'capital',
					        	   renderer : function(value) {
					        		   return Ext.util.Format.number(
					        				   value, '0,0.00');
					        	   },
					           },
					           {
					        	   header : 'Saldo Insoluto',
					        	   width : 120,
					        	   dataIndex : 'saldo',
					        	   renderer : function(value) {
					        		   return Ext.util.Format.number(
					        				   value, '0,0.00');
					        	   },
					           }, {
					        	   header : 'Estatus',
					        	   width : 100,
					        	   dataIndex : 'estatus',
					           }, {
					        	   header : 'descEstatus',
					        	   width : 00,
					        	   dataIndex : 'descEstatus',
					        	   hidden : true,
					        	   hideable : false,
					           }, {
					        	   header : 'diaCortecap',
					        	   width : 00,
					        	   dataIndex : 'diaCortecap',
					        	   hidden : true,
					        	   hideable : false,
					           }, {
					        	   header : 'diaCorteint',
					        	   width : 00,
					        	   dataIndex : 'diaCorteint',
					        	   hidden : true,
					        	   hideable : false,
					           }, {
					        	   header : 'periodo',
					        	   width : 00,
					        	   dataIndex : 'periodo',
					        	   hidden : true,
					        	   hideable : false,
					           }, {
					        	   header : 'noAmortizaciones',
					        	   width : 00,
					        	   dataIndex : 'noAmortizaciones',
					        	   hidden : true,
					        	   hideable : false,
					           }, {
					        	   header : 'diasPeriodo',
					        	   width : 00,
					        	   dataIndex : 'diasPeriodo',
					        	   hidden : true,
					        	   hideable : false,
					           }, ]
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
					},
					dblclick : {
						fn : function(grid) {
							if (NS.storeSolicitudes.getCount() >= 1) {
								if (NS.oPeriodC == '0') {
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
											'Se van a eliminar los registros de Capital e Intereses. ¿Desea continuar?',
											function(btn) {
												if (btn === 'yes') {
													var data = new Array();
													NS.storeSolicitudes
													.each(function(
															records) {
														data
														.push(records
																.get('capital'));
													});
													var saldo = data[data.length - 1];
													try {
														NS.pdSaldoInsoluto = parseFloat(BFwrk.Util
																.unformatNumber(NS.pdSaldoInsoluto))
																+ parseFloat(BFwrk.Util
																		.unformatNumber(saldo));
													} catch (err) {
														NS.pdSaldoInsoluto = parseFloat(NS.pdSaldoInsoluto)
														+ parseFloat(saldo);
													}
													try {
														NS.txtTotalCap
														.setValue(BFwrk.Util
																.formatNumber((parseFloat((BFwrk.Util
																		.unformatNumber(NS.txtTotalCap
																				.getValue()))
																				- parseFloat(BFwrk.Util
																						.unformatNumber(saldo))))
																						.toFixed(2)));
													} catch (err) {
														NS.txtTotalCap
														.setValue(BFwrk.Util
																.formatNumber((parseFloat((BFwrk.Util
																		.unformatNumber(NS.txtTotalCap
																				.getValue()))
																				- parseFloat(BFwrk.Util
																						.unformatNumber(saldo))))
																						.toFixed(2)));
													}
													NS.txtTotal
													.setValue(BFwrk.Util
															.formatNumber((parseFloat((BFwrk.Util
																	.unformatNumber(NS.txtTotal
																			.getValue()))
																			- parseFloat(BFwrk.Util
																					.unformatNumber(saldo))))
																					.toFixed(2)));
													NS.gridDatos
													.getSelectionModel()
													.selectAll();
													var records = NS.gridDatos
													.getSelectionModel()
													.getSelections();
													cantidad = NS.storeSolicitudes
													.getCount();
													for (var i = 0; i < records.length; i++) {
														NS.gridDatos.store
														.remove(records[cantidad - 1]);
														NS.gridDatos
														.getView()
														.refresh();
													}
												}
											});
								}
							}
						}
					}
				}
			});
	NS.validaFecha = function(pvFecha, pvSeccion) {
		var fec = pvFecha.split("/");
		var dt = new Date(parseInt(fec[2], 10),
				parseInt(fec[1], 10) - 1, parseInt(fec[0], 10));
		pvFecha = dt;

		NS.fechaValida = false;
		NS.pdFecIniDisp = NS.txtFecDisp.getValue();
		NS.pdFecVenDisp = NS.txtFecVenDisp.getValue();
		if (pvSeccion == "A") {
			if (NS.storeSolicitudes.getCount() >= 2) {
				var data = new Array();
				NS.storeSolicitudes.each(function(records) {
					data.push(records.get('fecVen'));
				});
				var fec = data[data.length - 1].split("/");
				var dt = new Date(parseInt(fec[2], 10), parseInt(
						fec[1], 10) - 1, parseInt(fec[0], 10));
				var pdFecVenAmort = dt;
				if (pvFecha.getTime() == pdFecVenAmort.getTime()) {
					Ext.Msg
					.alert('SET',
							'La fecha de vencimiento debe ser mayor a la de la ultima amortización');
					pvFecha = NS.sumarDias(pdFecVenAmort, 1);
					NS.fechaValida = false;
				}
			} else {
				pdFecVenAmort = NS.pdFecIniDisp;
			}
		} else if (pvSeccion == "I") {
			pdFecVenAmort = new Date(0000, 00, 00);
			if (NS.storeSolicitudes2.getCount() > 1) {
				var data = new Array();
				NS.storeSolicitudes.each(function(records) {
					data.push(records.get('fecVen'));
				});
				if (data[0] != '') {
					var fec = data[data.length - 1].split("/");
					var dt = new Date(parseInt(fec[2], 10), parseInt(
							fec[1], 10) - 1, parseInt(fec[0], 10));
					NS.pdFecVenInt = dt;
					pvFecha == NS.sumarDias(pdFecVenAmort, 1);
					NS.fechaValida = false;
				}
			} else {
				NS.pdFecVenInt = NS.pdFecVenDisp;
			}
		}
		if (pvFecha >= NS.pdFecIniDisp) {
			if (pvFecha <= NS.pdFecVenDisp) {
				if (pvFecha >= pdFecVenAmort
						&& pvFecha <= NS.pdFecVenDisp) {
					NS.fechaValida = true;
				} else {
					if (pvSeccion == "A") {
						Ext.Msg
						.alert('SET',
						'La fecha de vencimiento debe ser mayor a la de la última amortización');
					} else {
						Ext.Msg
						.alert('SET',
						'La fecha de vencimiento debe ser mayor a la del último interés');
					}
					pvFecha = NS.pdFecVenDisp;
					NS.fechaValida = false;
				}
			} else {
				Ext.Msg
				.alert('SET',
				'La fecha de vencimiento debe ser menor a la de vencimiento de la disposición');
				pvFecha = NS.pdFecVenDisp;
				NS.fechaValida = false;
			}
		} else {
			Ext.Msg
			.alert('SET',
			'La fecha de vencimiento debe ser mayor a la inicial de la disposición');
			pvFecha = NS.pdFecIniDisp;
			NS.fechaValida = false;
		}
	}
	NS.validaDatosNoPeriodico = function() {
		NS.bandNP = false;
		NS.validaFecha(BFwrk.Util.changeDateToString(''
				+ NS.txtFecVenC.getValue()), "A");
		var fechaVal = NS.fechaValida;
		if (!fechaVal) {
			Ext.Msg.alert('SET', 'Seleccione una fecha válida.');
			NS.txtFecVenC.focus();
			NS.bandNP = false;
		} else if (NS.pdSaldoInsoluto <= 0) {
			Ext.Msg.alert('SET', 'No se pueden agregar más periodos.');
			NS.bandNP = false;
		} else if (NS.txtCapital.getValue() == ""
			|| parseFloat(BFwrk.Util.unformatNumber(NS.txtCapital.getValue())) == 0) {
			Ext.Msg.alert('SET', 'Debe ingresar el capital.');
			Ext.getCmp(PF + 'txtCapital').focus();
			NS.bandNP = false;
		} else if (parseFloat(BFwrk.Util.unformatNumber(NS.txtCapital.getValue())) > parseFloat(BFwrk.Util
				.unformatNumber(NS.txtMontoDisposicion.getValue()))) {
			Ext.Msg
			.alert('SET',
					'El capital no puede ser mayor al monto de la disposición.');
			Ext.getCmp(PF + 'txtCapital').focus();
			NS.bandNP = false;
		} else {
			NS.bandNP = true;
		}
	}
	NS.validaDatosPeriodico = function() {
		NS.viPosicion = 0;
		NS.bandP = false;
		if (((NS.cmbPeriodo.store.getCount() <= 0) && (NS.cmbPeriodo
				.getRawValue() == ""))
				|| (NS.cmbPeriodo.getRawValue() == "")) {
			Ext.Msg.alert('SET', 'Seleccione un periodo.');
			NS.cmbPeriodo.focus();
			NS.bandP = false;
		} else if ((NS.cmbDiaCorte.getValue() == "" && NS.cmbPeriodo
				.getRawValue() != 'UNIVERSAL')) {
			Ext.Msg.alert('SET', 'Seleccione el día de corte.');
			var myMask = new Ext.LoadMask(Ext.getBody(), {
				store : NS.storeCmbPeriodo,
				msg : "Cargando..."
			});
			NS.storeCmbPeriodo.load();
			// NS.cmbPeriodo.setValue('');
			Ext.getCmp(PF + 'cmbDiaCorte').focus();
			NS.bandP = false;
		} else if (NS.txtDiasC.getValue() == ""
			|| NS.txtDiasC.getValue() <= 0) {
			Ext.Msg.alert('SET', 'Inserte el No. de días del periodo.');
			Ext.getCmp(PF + 'txtDiasC').focus();
			NS.bandP = false;
		} else if (NS.txtDiasC.getValue() != "") {
			NS.vdValor = (parseFloat(NS.txtDiasC.getValue()) / 30);

			NS.viPosicion = NS.vdValor.toString().indexOf(".");

			if (NS.viPosicion != -1) {
				NS.vdValor = (NS.vdValor.toString().substr(
						NS.viPosicion, 3));
			}
			if ((NS.vdValor == '0' || NS.viPosicion == '-1')
					&& (NS.cmbDiaCorte.getValue() == "")) {
				Ext.Msg.alert('SET', 'Seleccione el día de corte');
				NS.cmbPeriodo.reset();
				NS.txtDiasC.setValue('');
				NS.cmbDiaCorte.focus();
				NS.bandP = false;
			} else {
				NS.bandP = true;
			}
		} else if (NS.chkPeriodoGracia.getValue() == true) {
			if (NS.txtPeriodoGracia.getValue() == "") {
				Ext.Msg.alert('SET', 'Ingrese la fecha de gracia.');
				Ext.getCmp(PF + 'txtPeriodoGracia').focus();
				NS.bandP = false;
			}
		} else {
			NS.bandP = true;
		}
	}
	NS.validaDatosInteres = function() {
		NS.viPosicion = 0;
		NS.bandPI = false;

		if (((NS.cmbPeriodoInt.store.getCount() <= 0) && (NS.cmbPeriodoInt
				.getRawValue() == ""))
				|| (NS.cmbPeriodoInt.getRawValue() == "")) {
			Ext.Msg.alert('SET', 'Seleccione un periodo.');
			NS.cmbPeriodoInt.focus();
			NS.bandPI = false;
		} else if ((NS.cmbDiaCorteInt.getValue() == "" && NS.cmbPeriodoInt
				.getRawValue() != 'UNIVERSAL')) {
			Ext.Msg.alert('SET', 'Seleccione el día de corte.');
			var myMask = new Ext.LoadMask(Ext.getBody(), {
				store : NS.storeCmbPeriodo2,
				msg : "Cargando..."
			});
			NS.storeCmbPeriodo2.load();
			// NS.cmbPeriodo.setValue('');
			Ext.getCmp(PF + 'cmbDiaCorteInt').focus();
			NS.bandPI = false;
		} else if (NS.txtDiasInt.getValue() == ""
			|| NS.txtDiasInt.getValue() <= 0) {
			Ext.Msg.alert('SET', 'Inserte el No. de días del periodo.');
			Ext.getCmp(PF + 'txtDiasInt').focus();
			NS.bandPI = false;
		} else if (NS.txtDiasInt.getValue() != "") {
			NS.vdValor = (parseFloat(NS.txtDiasInt.getValue()) / 30);

			NS.viPosicion = NS.vdValor.toString().indexOf(".");

			if (NS.viPosicion != -1) {
				NS.vdValor = (NS.vdValor.toString().substr(
						NS.viPosicion, 3));

			}
			if ((NS.vdValor == '0' || NS.viPosicion == '-1')
					&& (NS.cmbDiaCorteInt.getValue() == "")) {
				Ext.Msg.alert('SET', 'Seleccione el día de corte');
				NS.cmbPeriodoInt.reset();
				NS.txtDiasInt.setValue('');
				NS.cmbDiaCorteInt.focus();
				NS.bandPI = false;
			} else {
				NS.bandPI = true;
			}
		} else {
			NS.bandPI = true;
		}
	}
	NS.agregar = function() {
		if (!NS.vbExistAmort) {
			if (NS.oPeriodC == '0') {
				var registros = NS.storeSolicitudes.getCount();
				if (registros > 0) {
					NS.vdCapital = 0;
					var data = new Array();
					NS.storeSolicitudes.each(function(records) {
						data.push(records.get('capital'));
					});
					for (var i = 0; i <= data.length - 1; i++) {
						NS.vdCapital = parseFloat(NS.vdCapital)
						+ parseFloat(data[i]);
					}
					NS.vdCapital = parseFloat(NS.vdCapital)+ parseFloat(BFwrk.Util.unformatNumber(NS.txtCapital.getValue()));
					NS.vdTotal = BFwrk.Util
					.unformatNumber(NS.txtMontoDisposicion
							.getValue());
				}

				if (parseFloat(NS.vdCapital) > parseFloat(NS.vdTotal)) {
					Ext.Msg
					.alert('SET',
					'El monto del capital debe ser igual al de la disposición.');
				} else {
					NS.validaDatosNoPeriodico(NS.pdSaldoInsoluto);
					NS.validaDatosNP = NS.bandNP;
					if (NS.validaDatosNP) {
						// /var recordsDatGrid =
						// NS.storeSolicitudes.getCount();
						// With msfDatos
						if (registros == 0) {
							NS.vsFechaIni = NS.txtFecDisp.getValue();
						} else {
							var data = new Array();
							NS.storeSolicitudes.each(function(records) {
								data.push(records.get('fecVen'));
							});
							var fec = data[data.length - 1].split("/");
							var dt = new Date(parseInt(fec[2], 10),
									parseInt(fec[1], 10) - 1, parseInt(
											fec[0], 10));
							NS.vsFechaIni = dt;
						}
						NS.vdCapital = BFwrk.Util.unformatNumber(NS.txtCapital.getValue());
						NS.plWeekend = 0;
						NS.pdFecha = NS.txtFecVenC.getValue();

						if (NS.oPagoI == '0')
							NS.psRecorre = "A";
						else
							NS.psRecorre = "P";
						if (NS.psRecorre != "E") {
							NS.plDiaInhabil = false;
							// AQUI
							NS.plWeekend = 0;
							NS.storeSelectInhabil
							.load({
								params : {
									valorFecha : NS.pdFecha
								},
								callback : function(records,
										operation, success) {
									if (records.length <= 0) {
										var diaSemana = NS.pdFecha
										.getDay();
										if (diaSemana == "6") {
											if (NS.psRecorre == "A")
												NS.plWeekend = -1;
											else
												NS.plWeekend = 2;
										}
										if (diaSemana == "0") {
											if (NS.psRecorre == "A")
												NS.plWeekend = -2;
											else
												NS.plWeekend = 1;
										} else {
											NS.diaHabil = NS.pdFecha;
										}
									} else if (records.length > 0) {
										if (NS.psRecorre == "A")
											NS.plWeekend = -1;
										else
											NS.plWeekend = 1;
									}
									if (NS.plWeekend != 0) {
										NS.pdFecha = NS
										.sumarDias(
												NS.pdFecha,
												NS.plWeekend);
										var fec = NS.pdFecha
										.split("/");
										var dt = new Date(
												parseInt(
														fec[2],
														10),
														parseInt(
																fec[1],
																10) - 1,
																parseInt(
																		fec[0],
																		10));
										NS.diaHabil = dt;
									}
									NS.txtFecVenC
									.setValue(NS.diaHabil);
									registros = NS.gridDatos
									.getSelectionModel()
									.getSelections();
									NS
									.agregarCriterioValor(
											NS.storeSolicitudes
											.getCount() + 1,
											BFwrk.Util
											.changeDateToString(''
													+ NS.vsFechaIni),
													BFwrk.Util
													.changeDateToString(''
															+ NS.txtFecVenC
															.getValue()),
															BFwrk.Util
															.changeDateToString(''
																	+ NS.txtFecVenC
																	.getValue()),
																	NS
																	.restaFechas(
																			BFwrk.Util
																			.changeDateToString(''
																					+ NS.vsFechaIni),
																					BFwrk.Util
																					.changeDateToString(''
																							+ NS.txtFecVenC
																							.getValue())),
																							NS.vdCapital,
																							NS.pdSaldoInsoluto,
																							"A",
									"ACTIVA");
									try {
										NS.pdSaldoInsoluto = BFwrk.Util
										.formatNumber(parseFloat(BFwrk.Util
												.unformatNumber(NS.pdSaldoInsoluto))
												- parseFloat(BFwrk.Util
														.unformatNumber(NS.vdCapital)));
									} catch (err) {
										NS.pdSaldoInsoluto = BFwrk.Util
										.formatNumber(parseFloat((NS.pdSaldoInsoluto)
												- parseFloat(BFwrk.Util
														.unformatNumber(NS.vdCapital))));
									}
									try {
										NS.txtTotal
										.setValue(BFwrk.Util
												.formatNumber(((parseFloat(BFwrk.Util
														.unformatNumber(NS.txtTotal
																.getValue())) + parseFloat(BFwrk.Util
																		.unformatNumber(NS.vdCapital))))
																		.toFixed(2)));
									} catch (err) {
										NS.txtTotal
										.setValue(BFwrk.Util
												.formatNumber(((parseFloat(NS.txtTotal
														.getValue()) + parseFloat(NS.vdCapital)))
														.toFixed(2)));
									}
									try {
										NS.txtTotalCap
										.setValue(BFwrk.Util
												.formatNumber(((parseFloat(BFwrk.Util
														.unformatNumber(NS.txtTotalCap
																.getValue())) + parseFloat(NS.vdCapital)))
																.toFixed(2)));
									} catch (err) {
										NS.txtTotalCap
										.setValue(BFwrk.Util
												.formatNumber(((parseFloat(BFwrk.Util
														.unformatNumber(NS.txtTotalCap
																.getValue())) + parseFloat(BFwrk.Util
																		.formatNumber(NS.vdCapital))))
																		.toFixed(2)));
									}
									NS.txtCapital.setValue("0.00");
									NS.txtFecVenC.focus();
								}
							});
						} else {
							NS.diaHabil = NS.pdFecha;
							NS.txtFecVenC.setValue(NS.diaHabil);
							registros = NS.gridDatos
							.getSelectionModel()
							.getSelections();
							NS
							.agregarCriterioValor(
									NS.storeSolicitudes
									.getCount() + 1,
									BFwrk.Util
									.changeDateToString(''
											+ NS.vsFechaIni),
											BFwrk.Util
											.changeDateToString(''
													+ NS.txtFecVenC
													.getValue()),
													BFwrk.Util
													.changeDateToString(''
															+ NS.txtFecVenC
															.getValue()),
															NS
															.restaFechas(
																	BFwrk.Util
																	.changeDateToString(''
																			+ NS.vsFechaIni),
																			BFwrk.Util
																			.changeDateToString(''
																					+ NS.txtFecVenC
																					.getValue())),
																					NS.vdCapital,
																					NS.pdSaldoInsoluto, "A",
							"ACTIVA");
							try {
								NS.pdSaldoInsoluto = BFwrk.Util
								.formatNumber((parseFloat(BFwrk.Util
										.unformatNumber(NS.pdSaldoInsoluto)) - parseFloat(BFwrk.Util
												.unformatNumber(NS.vdCapital)))
												.toFixed(2));
							} catch (err) {
								NS.pdSaldoInsoluto = BFwrk.Util
								.formatNumber((parseFloat((NS.pdSaldoInsoluto)
										- parseFloat(BFwrk.Util
												.unformatNumber(NS.vdCapital))))
												.toFixed(2));
							}
							try {
								NS.txtTotal
								.setValue(BFwrk.Util
										.formatNumber(((parseFloat(BFwrk.Util
												.unformatNumber(NS.txtTotal
														.getValue())) + parseFloat(BFwrk.Util
																.unformatNumber(NS.vdCapital))))
																.toFixed(2)));
							} catch (err) {
								NS.txtTotal
								.setValue(BFwrk.Util
										.formatNumber(((parseFloat(NS.txtTotal
												.getValue()) + parseFloat(NS.vdCapital)))
												.toFixed(2)));
							}
							try {
								NS.txtTotalCap
								.setValue(BFwrk.Util
										.formatNumber(((parseFloat(BFwrk.Util
												.unformatNumber(NS.txtTotalCap
														.getValue())) + parseFloat(NS.vdCapital)))
														.toFixed(2)));
							} catch (err) {
								NS.txtTotalCap
								.setValue(BFwrk.Util
										.formatNumber(((parseFloat(BFwrk.Util
												.unformatNumber(NS.txtTotalCap
														.getValue())) + parseFloat(BFwrk.Util
																.formatNumber(NS.vdCapital))))
																.toFixed(2)));
							}
							NS.txtCapital.setValue("0.00");
							NS.txtFecVenC.focus();
						}
					}
				}
			}
			if (NS.oPeriodC == '1') {
				BFwrk.Util.msgWait('Calculando capital...', true);
				NS.storeSolicitudes.removeAll();
				NS.vdTotCapital = 0;
				NS.validaDatosPeriodico();
				NS.validaDatosP = NS.bandP;
				if (NS.validaDatosP) {
					// msfDatos.Rows = 1
					NS.vsBisiesto = "360";
					if (NS.oD == '1')
						NS.vsBisiesto = "365";
					// If vsTipoMenu = "A" Then
					// Call PruebaArrendamientoNuevo
					// Else
					NS.vdSaldoInsoluto = parseFloat(BFwrk.Util
							.unformatNumber(NS.txtMontoDisposicion
									.getValue()));
					NS.vlNoAmort = NS.txtNoAmort.getValue();
					NS.vdCapital = parseFloat(BFwrk.Util
							.unformatNumber(NS.txtMontoDisposicion
									.getValue()))
									/ NS.vlNoAmort;
					NS.vsFecIni = BFwrk.Util.changeDateToString(''
							+ NS.txtFecDisp.getValue());
					var inhabil;
					if (NS.oPagoI == '0')
						inhabil = "A";
					else
						inhabil = "P";
					AltaFinanciamientoAction
					.obtenDiaHabil(
							NS.cadenaFechas,
							inhabil,
							function(mapResult, e) {
								BFwrk.Util.msgWait(
										'Terminado...', false);
								if (mapResult.fechasHabiles !== null
										&& mapResult.fechasHabiles !== ''
											&& mapResult.fechasHabiles != undefined) {
									for (var fecha = 0; fecha < mapResult.fechasHabiles.length - 1; fecha++) {
										NS.vsFecPag = mapResult.fechasHabiles[fecha];
									}
									for (var i = 0; i <= NS.vlNoAmort - 1; i++) {
										var fechas = NS.cadenaFechas
										.split(" ");
										NS.vsFecVen = fechas[i];
										if (NS.oCalculoI == '0')
											NS.vsFecVen = NS.vsFecPag;
										NS
										.agregarCriterioValor(
												NS.storeSolicitudes
												.getCount() + 1,
												NS.vsFecIni,
												NS.vsFecVen,
												mapResult.fechasHabiles[i],
												NS.restaFechas(
														NS.vsFecIni,
														NS.vsFecVen),
														NS.vdCapital
														.toFixed(2),
														NS.vdSaldoInsoluto
														.toFixed(2),
														"A",
														"ACTIVA");
										NS.vdSaldoInsoluto = parseFloat(NS.vdSaldoInsoluto)
										- parseFloat(NS.vdCapital);
										NS.vdTotCapital = parseFloat(NS.vdTotCapital)
										+ parseFloat(NS.vdCapital);
										NS.vsFecIni = NS.vsFecVen;
									}
									NS.txtTotalCap
									.setValue(BFwrk.Util
											.formatNumber(NS.vdTotCapital
													.toFixed(2)));
									NS.txtTotal
									.setValue(BFwrk.Util
											.formatNumber(NS.txtTotalCap
													.getValue()));
									// NS.vbExistAmort=true;
								}
							});
				}
				// End If
			}
		}
	}
	NS.cmdAgregar = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAgregar',
		name : PF + 'cmdAgregar',
		text : '<<',
		x : 895,
		y : 120,
		// disabled : true,
		width : 30,
		listeners : {
			click : {
				fn : function(e) {
					NS.agregar();
				}
			}
		}
	});
	NS.quitar2 = function() {
		var pdTotal;
		var pdCapital;
		var pdInteres;
		var pbSel;
		var i;
		var sMensaje;
		if (NS.storeSolicitudes.getCount() >= 1) {
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
					'Se van a eliminar los registros de Capital e Intereses. ¿Desea continuar?',
					function(btn) {
						if (btn === 'yes') {
							if (NS.vbPagado) {
								Ext.Msg
								.alert('SET',
								'No se pueden eliminar las amortizaciones por que ya existen pagadas.');
							} else {
								AltaFinanciamientoAction
								.deleteAmortizacion(
										NS.cmbContratos
										.getValue(),
										parseInt(NS.cmbDisp
												.getValue()),
												false,
												false,
												function(
														mapResult,
														e) {
											BFwrk.Util
											.msgWait(
													'Terminado...',
													false);

										});
								AltaFinanciamientoAction
								.funSQLDeleteProvisiones(
										NS.cmbContratos
										.getValue(),
										parseInt(NS.cmbDisp
												.getValue()),
												false,
												function(
														mapResult,
														e) {
											BFwrk.Util
											.msgWait(
													'Terminado...',
													false);

										});
								NS.habilitaTodo(false);
								NS.limpiaTodo();
								NS.habilitaTodo(true);
								NS.optPeriodCapital
								.setDisabled(false);
								NS.optPeriodIntereses
								.setDisabled(false);
								NS.vbExistAmort = false;
								NS.oPeriodC = '0';
								NS.chkPeriodoGracia
								.setDisabled(true);
								NS.chkPeriodoGracia
								.setValue(false);
								NS.editarAmortizacion(true);
								NS.limpiarValores();
								NS.pdSaldoInsoluto = BFwrk.Util
								.unformatNumber(NS.txtMontoDisposicion
										.getValue());
								NS.txtFecVenC.focus();
								NS.lblIvaC.setDisabled(false);
								NS.txtIvaC.setDisabled(false);
								NS.lblFactorCapital
								.setDisabled(false);
								NS.txtFactorCapital
								.setDisabled(false);
								NS.txtDiasC.setDisabled(true);
							}
						}
					});
		}
	}
	// Este botón es en caso de que el usuario tenga la facultad para
	// eliminar las amortizaciones de capital
	NS.cmdQuitar2 = apps.SET.btnFacultativo(new Ext.Button({
		id : 'cmdQuitar2',
		name : 'cmdQuitar2',
		text : '>>',
		x : 895,
		y : 150,
		width : 30,
		listeners : {
			click : {
				fn : function(e) {
					NS.quitar2();
				}
			}
		}
	}));
	NS.quitar = function() {
		var pdTotal;
		var pdCapital;
		var pdInteres;
		var pbSel;
		var i;
		var sMensaje;
		if (NS.storeSolicitudes.getCount() >= 1) {
			if (NS.vbExistAmort) {
				Ext.Msg
				.alert('SET',
						'No tiene facultad para eliminar Capital e Intereses');
			} else {
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
						'Se van a eliminar los registros de Capital e Intereses. ¿Desea continuar?',
						function(btn) {
							if (btn === 'yes') {
								if (NS.vbPagado) {
									Ext.Msg
									.alert('SET',
									'No se pueden eliminar las amortizaciones por que ya existen pagadas.');
								} else {
									AltaFinanciamientoAction
									.deleteAmortizacion(
											NS.cmbContratos
											.getValue(),
											parseInt(NS.cmbDisp
													.getValue()),
													false,
													false,
													function(
															mapResult,
															e) {
												BFwrk.Util
												.msgWait(
														'Terminado...',
														false);

											});
									AltaFinanciamientoAction
									.funSQLDeleteProvisiones(
											NS.cmbContratos
											.getValue(),
											parseInt(NS.cmbDisp
													.getValue()),
													false,
													function(
															mapResult,
															e) {
												BFwrk.Util
												.msgWait(
														'Terminado...',
														false);
											});
									NS.habilitaTodo(false);
									NS.limpiaTodo();
									NS.habilitaTodo(true);
									NS.optPeriodCapital
									.setDisabled(false);
									NS.optPeriodIntereses
									.setDisabled(false);
									NS.vbExistAmort = false;
									NS.oPeriodC = '0';
									NS.chkPeriodoGracia
									.setDisabled(true);
									NS.chkPeriodoGracia
									.setValue(false);
									NS.editarAmortizacion(true);
									NS.limpiarValores();
									NS.pdSaldoInsoluto = BFwrk.Util
									.unformatNumber(NS.txtMontoDisposicion
											.getValue());
									NS.txtFecVenC.focus();
									NS.lblIvaC
									.setDisabled(false);
									NS.txtIvaC
									.setDisabled(false);
									NS.lblFactorCapital
									.setDisabled(false);
									NS.txtFactorCapital
									.setDisabled(false);
									NS.txtDiasC
									.setDisabled(true);
								}
							}
						});
			}
		}
	}
	// Este botón es en caso de que el usuario no tenga la facultad para
	// eliminar las amortizaciones de capital
	NS.cmdQuitar = apps.SET.btnFacultativo(new Ext.Button({
		id : 'cmdQuitar',
		name : 'cmdQuitar',
		text : '>>',
		x : 895,
		y : 150,
		width : 30,
		listeners : {
			click : {
				fn : function(e) {
					NS.quitar();
				}
			}
		}
	}));
	// INTERESES
	// Se deshabilita la opción 'No periódica' hasta que la solicite el
	// usuario
	// se migró la funcionalidad completa, tal como en el SET de C-S de
	// CIE
	NS.optPeriodIntereses = new Ext.form.RadioGroup(
			{
				id : PF + 'optPeriodIntereses',
				name : PF + 'optPeriodIntereses',
				x : 0,
				y : 0,
				columns : 2,
				items : [
				         {
				        	 boxLabel : 'No periódica',
				        	 name : 'optPI',
				        	 hidden : true,
				        	 inputValue : 0,
				        	 listeners : {
				        		 check : {
				        			 fn : function(opt, valor) {
				        				 if (valor == true) {
				        					 NS.oPeriodI = '0';
				        					 NS.chkPrimerPagoInt
				        					 .setValue(0);
				        					 NS.chkPrimerPagoInt
				        					 .setDisabled(false);
				        					 NS
				        					 .editarAmortizacionInt(true);
				        					 NS.limpiarValores();
				        					 NS.pdSaldoInsoluto = BFwrk.Util
				        					 .unformatNumber(NS.txtMontoDisposicion
				        							 .getValue());
				        					 NS.txtIva
				        					 .setDisabled(false);
				        					 NS.txtFecVenInt
				        					 .setDisabled(false);
				        					 NS.txtFecVenInt.focus();
				        					 NS.txtInteres
				        					 .setDisabled(false);
				        					 // txtInteres.Locked = False
				        					 NS.txtInteres
				        					 .setValue("0.00");
				        					 NS.txtDiasInt
				        					 .setDisabled(true);
				        				 }
				        			 }
				        		 }
				        	 }
				         },
				         {
				        	 boxLabel : 'Periódica',
				        	 name : 'optPI',
				        	 inputValue : 1,
				        	 listeners : {
				        		 check : {
				        			 fn : function(opt, valor) {
				        				 if (valor == true) {
				        					 NS.oPeriodI = '1';
				        					 NS.chkPeriodoGracia
				        					 .setDisabled(false);
				        					 NS
				        					 .editarAmortizacionInt(false);
				        					 NS.limpiarValores();
				        					 var myMask = new Ext.LoadMask(
				        							 Ext.getBody(),
				        							 {
				        								 store : NS.storeCmbPeriodo2,
				        								 msg : "Cargando..."
				        							 });
				        					 NS.storeCmbPeriodo2.load();

				        				 }
				        			 }
				        		 }
				        	 }
				         } ]
			});
	NS.lblPeriodoInt = new Ext.form.Label({
		text : 'Periodo:',
		x : 0,
		y : 60
	});
	NS.storeCmbPeriodo2 = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			pdAmort : true
		},
		paramOrder : [ 'pdAmort' ],
		directFn : AltaFinanciamientoAction.funSQLComboPeriodo,
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
	NS.cmbPeriodoInt = new Ext.form.ComboBox({
		store : NS.storeCmbPeriodo2,
		name : PF + 'cmbPeriodoInt ',
		id : PF + 'cmbPeriodoInt ',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 0,
		disabled : true,
		y : 75,
		width : 140,
		valueField : 'id',
		displayField : 'descripcion',
		autocomplete : true,
		emptyText : 'Seleccione un periodo',
		triggerAction : 'all',
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {
					NS.txtDiasInt.setDisabled(false);
					NS.txtDiasInt.focus();
				}
			}
		}
	});
	NS.lblDiaCorteInt = new Ext.form.Label({
		text : 'Día corte:',
		x : 240,
		y : 0
	});
	NS.cmbDiaCorteInt = new Ext.form.ComboBox({
		store : NS.storeDias,
		name : PF + 'cmbDiaCorteInt ',
		id : PF + 'cmbDiaCorteInt ',
		typeAhead : true,
		mode : 'local',
		minChars : 0,
		selecOnFocus : true,
		forceSelection : true,
		x : 240,
		y : 17,
		disabled : true,
		width : 50,
		valueField : 'id',
		displayField : 'id',
		autocomplete : true,
		emptyText : 'Día',
		triggerAction : 'all',
		disabled : true,
		value : '',
		listeners : {
			select : {
				fn : function(combo, valor) {

				}
			}
		}
	});
	NS.lblDiaInt = new Ext.form.Label({
		text : 'Días:',
		x : 240,
		y : 60
	});
	NS.txtDiasInt = new Ext.form.TextField(
			{
				id : PF + 'txtDiasInt',
				name : PF + 'txtDiasInt',
				disabled : true,
				x : 240,
				y : 75,
				width : 50,
				listeners : {
					change : {
						fn : function(caja, valor) {

							if (NS.storeSolicitudes.getCount() > 0) {
								if (NS.oPeriodI == '1') {
									NS.i = 0;
									NS.pruebaCalculaPeriodosCapNuevo(
											false, NS.i);
									NS.txtNoAmortInt.setValue(NS.i);
								}
							} else {
								Ext.Msg
								.alert("SET",
								"Agregue las amortizaciones de capital.");
								caja.setValue("");
								return;
							}
						}
					}
				}
			});
	NS.chkPrimerPagoInt = new Ext.form.Checkbox({
		name : PF + 'chkPrimerPagoInt',
		id : 'chkPrimerPagoInt',
		x : 15,
		y : 0,
		disabled : true,
		listeners : {
			check : {
				fn : function() {
					NS.txtPrimerPagoInt.setDisabled(false);
				}
			}
		}
	});
	NS.txtPrimerPagoInt = new Ext.form.DateField({
		id : PF + 'txtPrimerPagoInt',
		name : PF + 'txtPrimerPagoInt',
		format : 'd/m/Y',
		disabled : true,
		x : 40,
		y : 0,
		allowBlank : false,
		width : 100,
		vtype : 'daterange',
		listeners : {
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			change : {
				fn : function(caja, valor) {
					if (caja.getValue() == ''
							|| caja.getValue() == null) {
						Ext.Msg.alert('SET',
						'Introduzca la fecha del primer pago');
						NS.txtPrimerPagoInt.setValue(NS.fecHoy);
						Ext.getCmp(PF + 'txtPrimerPagoInt').focus();
					}
				}
			},
			blur : function(datefield) {
				if (caja.getValue() == '' || caja.getValue() == null) {
					Ext.Msg.alert('SET',
					'Introduzca la fecha del primer pago');
					NS.txtPrimerPagoInt.setValue(NS.fecHoy);
					Ext.getCmp(PF + 'txtPrimerPagoInt').focus();
				}
			},
		}
	});
	NS.lblNoAmortInt = new Ext.form.Label({
		text : 'Periodos:',
		x : 330,
		y : 60
	});
	NS.txtNoAmortInt = new Ext.form.TextField({
		id : PF + 'txtNoAmortInt',
		name : PF + 'txtNoAmortInt',
		disabled : true,
		x : 330,
		y : 75,
		width : 50,
		listeners : {
			change : {
				fn : function(caja, valor) {
					/*
					 * Ext.getCmp(PF + 'txtMontoIni').setValue(
					 * BFwrk.Util.formatNumber(caja.getValue()));
					 * ('MONTOS', caja.getId(), 'valor');
					 */
				}
			}
		}
	});
	NS.lblVencimientoInt = new Ext.form.Label({
		text : 'Vencimiento:',
		x : 670,
		y : 60
	});
	NS.txtFecVenInt = new Ext.form.DateField({
		id : PF + 'txtFecVenInt',
		name : PF + 'txtFecVenInt',
		format : 'd/m/Y',
		disabled : true,
		x : 670,
		y : 75,
		allowBlank : false,
		width : 100,
		vtype : 'daterange',
		listeners : {
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			change : {
				fn : function(caja, valor) {
					NS.calculaInteres(caja.getValue(), '', '',
							"txtFecVenInt");
				}
			},
			blur : function(datefield) {
			},
		}
	});
	NS.lblInteres = new Ext.form.Label({
		text : 'Interés:',
		x : 800,
		y : 60
	});
	NS.txtInteres = new Ext.form.TextField({
		id : PF + 'txtInteres',
		name : PF + 'txtInteres',
		disabled : true,
		x : 800,
		y : 75,
		width : 100,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.chkDiasExacInt = new Ext.form.Checkbox({
		name : PF + 'chkDiasExacInt',
		id : 'chkDiasExacInt',
		x : 430,
		y : 75,
		// disabled : true,
		listeners : {
			check : {
				fn : function() {

				}
			}
		}
	});
	NS.lblDiasExacInt1 = new Ext.form.Label({
		text : 'Días',
		x : 450,
		y : 70
	});
	NS.lblDiasExacInt2 = new Ext.form.Label({
		text : ' exactos',
		x : 450,
		y : 80
	});
	NS.lblIva = new Ext.form.Label({
		text : 'IVA:',
		x : 580,
		y : 0
	});
	NS.txtIva = new Ext.form.TextField({
		id : PF + 'txtIva',
		name : PF + 'txtIva',
		disabled : true,
		x : 580,
		y : 15,
		value : '0.00',
		width : 50,
		listeners : {
			change : {
				fn : function(caja, valor) {
					if (NS.txtIva.getValue() == ''
							|| NS.txtIva.getValue() == 0)
						NS.txtIva.setValue("0.00");
					else
						caja.setValue(BFwrk.Util.formatNumber(NS.txtIva
								.getValue()));
				}
			},
			blur : {
				fn : function(caja, valor) {
					if (NS.txtIva.getValue() == ''
						|| NS.txtIva.getValue() == 0)
						NS.txtIva.setValue("0.00");
					else
						caja.setValue(BFwrk.Util.formatNumber(NS.txtIva
								.getValue()));
				}
			}
		}
	});
	NS.chkSobreTasa = new Ext.form.Checkbox(
			{
				name : PF + 'chkSobreTasa',
				id : 'chkSobreTasa',
				x : 670,
				y : 15,
				disabled : true,
				listeners : {
					check : {
						fn : function(ch, checked) {
							if (checked) {
								// If vsTipoMenu = "B" And
								// CDbl(Val(frmAltaCredito.txtSobreTasaCB))
								// <> 0 Then
								// Call EditarAmortizacionInt(True,
								// &H80000005)
								NS.optPeriodIntereses
								.setDisabled(false);
								NS.optPeriodIntereses
								.setDisabled(false);
								NS.txtComentario.setValue("");
								NS.txtDiasInt.setValue("");
								NS.cmbDiaCorteInt.setValue("");
								NS.cmbPeriodoInt.setValue("");
								NS.txtNoAmortInt.setValue("");
								NS.storeSolicitudes2.removeAll();
								NS.txtTotalInt.setValue("0.00");
								NS.txtTotal.setValue(NS.txtTotalCap
										.getValue());
							}
						}
					}
				}
			});
	NS.lblSobreTasa1 = new Ext.form.Label({
		text : 'Sobre',
		x : 685,
		y : 13
	});
	NS.lblSobreTasa2 = new Ext.form.Label({
		text : 'tasa',
		x : 685,
		y : 22
	});
	NS.lblComentario = new Ext.form.Label({
		text : 'Comentarios:',
		x : 730,
		y : 0
	});
	NS.txtComentario = new Ext.form.TextField({
		id : PF + 'txtComentario',
		name : PF + 'txtComentario',
		disabled : true,
		x : 730,
		y : 15,
		width : 190,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.optDias = new Ext.form.RadioGroup({
		id : PF + 'optDias',
		name : PF + 'optDias',
		x : 0,
		y : 0,
		columns : 2,
		items : [ {
			boxLabel : '360',
			name : 'optD',
			inputValue : 0,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.oD = '0';
						}
					}
				}
			}
		}, {
			boxLabel : '365',
			name : 'optD',
			inputValue : 1,
			checked : true,
			listeners : {
				check : {
					fn : function(opt, valor) {
						if (valor == true) {
							NS.oD = '1';
						}
					}
				}
			}
		} ]
	});
	NS.storeSolicitudes2 = new Ext.data.DirectStore(
			{
				paramsAsHash : false,
				root : '',
				baseParams : {
					psIdContrato : '',
					piDisposicion : 0,
					pbCambioTasa : false,
					psTipoMenu : '',
					psProyecto : '',
					piCapital : 0,
					dias : 0
				},
				paramOrder : [ 'psIdContrato', 'piDisposicion',
				               'pbCambioTasa', 'psTipoMenu', 'psProyecto',
				               'piCapital', 'dias' ],
				               directFn : AltaFinanciamientoAction.selectAmortizacionesInteres,
				               fields : [ {
				            	   name : 'fecInicio'
				               }, {
				            	   name : 'fecVenc'
				               }, {
				            	   name : 'fecPago'
				               }, {
				            	   name : 'dias'
				               }, {
				            	   name : 'saldo'
				               }, {
				            	   name : 'saldoIns'
				               }, {
				            	   name : 'interes'
				               }, {
				            	   name : 'iva'
				               }, {
				            	   name : 'estatus'
				               }, {
				            	   name : 'descEstatus'
				               }, {
				            	   name : 'periodo'
				               }, {
				            	   name : 'noAmortizaciones'
				               }, {
				            	   name : 'diasPeriodo'
				               }, {
				            	   name : 'comentario'
				               }, {
				            	   name : 'diaCorteint'
				               }, ],
				               listeners : {
				            	   load : function(s, records) {

				            	   }
				               }
			});
	NS.smE2 = new Ext.grid.CheckboxSelectionModel({
		forceFit : false
	});
	NS.gridDatos2 = new Ext.grid.GridPanel({
		store : NS.storeSolicitudes2,
		id : PF + 'gridDatos2',
		name : PF + 'gridDatos2',
		frame : true,
		width : 890,
		height : 150,
		x : 0,
		y : 120,
		cm : new Ext.grid.ColumnModel({
			defaults : {
				width : 120,
				value : true,
				sortable : true
			},
			columns : [ NS.smE2, {
				id : 'fecInicio',
				header : 'Fecha Inicio',
				width : 115,
				dataIndex : 'fecInicio',
				direction : 'ASC'
			}, {
				header : 'Fecha Vencimiento',
				width : 115,
				dataIndex : 'fecVenc',
			}, {
				header : 'Fecha Pago',
				width : 115,
				dataIndex : 'fecPago',
			}, {
				header : 'Días',
				width : 100,
				dataIndex : 'dias',
			}, {
				header : 'Saldo',
				width : 115,
				dataIndex : 'saldo',
				renderer : function(value) {
					return Ext.util.Format.number(value, '0,0.00');
				},
			}, {
				header : 'SaldoIns',
				width : 150,
				dataIndex : 'saldoIns',
				hidden : true,
				hideable : false
			}, {
				header : 'Interes',
				width : 115,
				dataIndex : 'interes',
				renderer : function(value) {
					return Ext.util.Format.number(value, '0,0.00');
				},

			}, {
				header : 'IVA',
				width : 115,
				dataIndex : 'iva',
				renderer : function(value) {
					return Ext.util.Format.number(value, '0,0.00');
				},
			}, {
				header : 'Estatus',
				width : 115,
				dataIndex : 'estatus',
			}, {
				header : 'Desc. Estatus',
				width : 150,
				dataIndex : 'descEstatus',
				hidden : true,
				hideable : false
			}, {
				header : 'periodo',
				width : 00,
				dataIndex : 'periodo',
				hidden : true,
				hideable : false,
			}, {
				header : 'noAmortizaciones',
				width : 00,
				dataIndex : 'noAmortizaciones',
				hidden : true,
				hideable : false,
			}, {
				header : 'diasPeriodo',
				width : 00,
				dataIndex : 'diasPeriodo',
				hidden : true,
				hideable : false,

			}, {
				header : 'comentario',
				width : 00,
				dataIndex : 'comentario',
				hidden : true,
				hideable : false,
			}, {
				header : 'diaCorteint',
				width : 00,
				dataIndex : 'diaCorteint',
				hidden : true,
				hideable : false,
			} ]
		}),
		viewConfig : {
			getRowClass : function(record, index) {
			}
		},
		sm : NS.smE2,
		listeners : {
			click : {
				fn : function(grid) {
				}
			}
		}
	});
	NS.agregarCriterioValor2 = function(fecInicio, fecVenc, fecPago,
			dias, saldo, saldoIns, interes, iva, estatus, descEstatus) {
		var indice = 0;
		var recordsDatGrid = NS.storeSolicitudes2.data.items;
		NS.agregarValorGridDatos2(fecInicio, fecVenc, fecPago, dias,
				saldo, saldoIns, interes, iva, estatus, descEstatus,
				indice, 0);
	};
	// Agrega valor a criterios
	NS.agregarValorGridDatos2 = function(fecInicio, fecVenc, fecPago,
			dias, saldo, saldoIns, interes, iva, estatus, descEstatus,
			indice) {
		// var valorCombo = Ext.getCmp(oIdElemento).getValue();
		// var valorCombo = sValor;
		var tamGrid = indice <= 0 ? (NS.storeSolicitudes2.data.items).length
				: indice;
		var datosClase = NS.gridDatos2.getStore().recordType;
		var datos = new datosClase({
			fecInicio : fecInicio,
			fecVenc : fecVenc,
			fecPago : fecPago,
			dias : dias,
			saldo : saldo,
			saldoIns : saldoIns,
			interes : interes,
			iva : iva,
			estatus : estatus,
			descEstatus : descEstatus
		});
		NS.gridDatos2.stopEditing();
		NS.storeSolicitudes2.insert(tamGrid, datos);
		// NS.storeSolicitudes2.load();
		NS.gridDatos2.getView().refresh();
		NS.gridDatos2.getSelectionModel().selectAll();
	};
	// No aplica
	/*
	 * NS.unico=function(pbInteres, vsBisiesto){ var dt; var vdFecIni =
	 * NS.txtFecDisp.getValue(); var vdSaldoInsoluto
	 * =NS.txtMontoDisposicion.getValue(); if (!pbInteres){ vlPeriodos =
	 * NS.txtNoAmort.getValue(); vlDias = NS.txtDias.getValue(); } else{
	 * vlPeriodos = NS.txtNoAmortInt.getValue(); vlDias =
	 * NS.txtDiasInt.getValue(); } if(!pbInteres) vdCapital =
	 * parseFloat(BFwrk.Util.unformatNumber(vdSaldoInsoluto)) /
	 * parseFloat(vlPeriodos);
	 * NS.txtTotalCap.setValue(BFwrk.Util.formatNumber(vdSaldoInsoluto));
	 * //If vsTipoMenu = "A" Then vdSaldoIn =
	 * CDbl(frmAltaCredito.txtMontoDisposicion.Text) var j = 1 for(var
	 * i=1;i<=vlPeriodos;i++){
	 * if(NS.cmbDiaCorteInt.getValue()!=''&&i==1){
	 * if(NS.cmbDiaCorteInt.getValue() >vdFecInI.getDay()){ vdFecFin =
	 * NS.cmbDiaCorteInt.getValue() + "/"
	 * +(vdFecIni.getMonth()+1)+"/"+vdFecIni.getFullYear(); } else{ var
	 * dt=vdFecIni; dt.setMonth(dt.getMonth()+1); vdFecFin =
	 * NS.cmbDiaCorteInt.getValue()+"/"+
	 * dt.getMonth()+"/"+dt.getFullYear(); vlDiasDif =
	 * NS.restaFechas(BFwrk.Util.changeDateToString(''+vdFecIni),
	 * BFwrk.Util.changeDateToString(vdFecFin)); if(vlDiasDif >
	 * NS.txtDiasInt.getValue()){ dt=vdFecIni; vdFecFin =
	 * dt.setDate(dt.getDate()+vlDias);
	 *  } } vdFecAuxIni = vdFecFin; }else{ dt=vdFecIni vdFecAuxIni =
	 * dt.setDate(dt.getDate()+vlDias); vdFecIni = vdFecFin; vdFecFin =
	 * vdFecAuxIni; } if (i== vlPeriodos) vdFecFin =
	 * NS.txtFecVenDisp.getValue(); vdFecPag = vdFecFin; var inhabil;
	 * if(NS.oCalculoI=='0') inhabil="E"; else if(NS.oPagoI=='0')
	 * inhabil="A"; else inhabil="P"; AltaFinanciamientoAction
	 * .obtenDiaHabil( NS.cadenaFechas, inhabil, function( mapResult, e) {
	 * BFwrk.Util .msgWait( 'Terminado...', false); if
	 * (mapResult.fechasHabiles !== null && mapResult.fechasHabiles !== '' &&
	 * mapResult.fechasHabiles != undefined) { for (var fecha = 0; fecha <
	 * mapResult.fechasHabiles.length-1; fecha++) {
	 * vdFecPag=mapResult.fechasHabiles[fecha]; } } });
	 * if(NS.oCalculoI!='1'&&NS.oCalculoI!='2') vdFecFin = vdFecPag;
	 * if(!pbInteres) vdSaldoInsoluto = vdSaldoInsoluto - vdCapital;
	 * if(pbInteres){ if(NS.storeSolicitudes.getCount()<1){
	 * Ext.Msg.alert('SET','Cargue la Tabla de Amortizaciones de
	 * Capital'); return; } if(NS.storeSolicitudes.getCount()>2){ var
	 * data=new Array(); var data2=new Array();
	 * NS.storeSolicitudes.each(function(records){
	 * data.push(records.get('fecVenC'));
	 * data2.push(records.get('capital')); }); fecha=data[j];
	 * if(vdFecFin<=fecha) vdSaldoInsoluto = vdSaldoInsoluto; else
	 * if(vdFecFin>=fecha){ vdCapital=data2[j]; if((data[j] > vdFecIni) &&
	 * (data[j] < vdFecFin)) vlDiasDif =
	 * NS.restaFechas(vdFecIni,data[j]); //If vsTipoMenu = "B" And
	 * frmAltaCredito.txtSobreTasaCB.Text <> 0 And chkSobreTasa.Value =
	 * 1 Then //vdIntProvI = (((CDbl(frmAltaCredito.txtSobreTasaCB.Text) /
	 * 100 / CInt(vsBisiesto)) * vdSaldoInsoluto) * vlDiasDif) //Else
	 * vdIntProvI = (((parseFloat(pdTasa) / 100 /
	 * parseFloat((vsBisiesto))) * parseFloat(vdSaldoInsoluto)) *
	 * parseFloat(vlDiasDif)); //End If vdSaldoInsoluto =
	 * vdSaldoInsoluto - vdCapital; vlDiasDif = NS.restaFechas(data[j],
	 * vdFecFin); //If vsTipoMenu = "B" And
	 * frmAltaCredito.txtSobreTasaCB.Text <> 0 And chkSobreTasa.Value =
	 * 1 Then //vdIntProvI2 =
	 * (((CDbl(frmAltaCredito.txtSobreTasaCB.Text) / 100 /
	 * CInt(vsBisiesto)) * vdSaldoInsoluto) * vlDiasDif) //Else
	 * vdIntProvI2 = (((parseFloat(pdTasa) / 100 /
	 * parseFloat(vsBisiesto)) * parseFloat(vdSaldoInsoluto)) *
	 * parseFloat(vlDiasDif)); vlDiasDif = NS.restFechas( vdFecIni,
	 * vdFecFin); vdInteres = vdIntProvI + vdIntProvI2; j++; //If
	 * vsTipoMenu = "B" And frmAltaCredito.txtSobreTasaCB.Text <> 0 And
	 * chkSobreTasa.Value = 1 Then //.TextMatrix(.Rows - 1,
	 * LI_I_SALDO_INS) =
	 * Format$(CDbl(frmAltaCredito.txtSobreTasaCB.Text), "#,##0.00000")
	 * //Else //.TextMatrix(.Rows - 1, LI_I_SALDO_INS) = Format$(pdTasa,
	 * "#,##0.00000") //End If
	 * NS.agregarCriterioValor2(BFwrk.Util.changeDateToString(''+vdFecIni),BFwrk.Util.changeDateToString(''+
	 * NS.vdFecFin),BFwrk.Util.changeDateToString(''+
	 * vdFecPag),vlDiasDif,BFwrk.Util.formatNumber(vdInteres),BFwrk.Util.formatNumber(parseFloat(vdInteres) *
	 * (parseFloat(BFwrk.Util.formatNumber(NS.txtIva.getValue()))) /
	 * 100),BFwrk.Util.formatNumber(vdSaldoInsoluto),BFwrk.Util.formatNumber(pdTasa),"A","ACTIVA");
	 * vdTotalInteres = vdTotalInteres + vdInteres; vdFecIni =
	 * vdFecAuxIni; NS.txtTotalInt.setValue(vdTotalInteres);
	 * NS.txtTotal.setValue(NS.txtTotalCap.getValue() + vdTotalInteres); }
	 * else{ vdSaldoInsoluto = vdSaldoInsoluto - vdCapital; } j++; } }
	 * if(!pbInteres){ //If vsTipoMenu = "A" Then //vlDiasDif =
	 * DateDiff("d", vdFecIni, vdFecFin) //vdInteres = (((pdTasa / 100 /
	 * 360) * vdSaldoIn) * vlDiasDif) //vdSaldoIn = vdSaldoIn -
	 * (CDbl(frmAltaCredito.txtRenta.Text) - vdInteres)
	 * //.TextMatrix(.Rows - 1, LI_C_CAPITAL) =
	 * Format$((CDbl(frmAltaCredito.txtRenta.Text) - vdInteres),
	 * "#,##0.00") //.TextMatrix(.Rows - 1, LI_C_INTERES) =
	 * Format$(vdInteres, "#,##0.00") //.TextMatrix(.Rows - 1,
	 * LI_C_RENTA) = Format$(CDbl(frmAltaCredito.txtRenta.Text),
	 * "#,##0.00") //.TextMatrix(.Rows - 1, LI_C_IVA) =
	 * Format$(CDbl(frmAltaCredito.txtRenta.Text) * (CDbl(txtIvaC.Text) /
	 * 100), "#,##0.00") //.TextMatrix(.Rows - 1, LI_C_SALDO) =
	 * Format$(vdSaldoIn, "#,##0.00") //Else // .TextMatrix(.Rows - 1,
	 * LI_C_CAPITAL) = vdCapital //.TextMatrix(.Rows - 1, LI_C_SALDO) =
	 * Format$(vdSaldoInsoluto, "#,##0.00") //End If
	 * NS.agregarCriterioValor(i,vdFecIni, vdFecFin,
	 * vdFecPag,BFwrk.Util.formatNumber(vdCapital.toFixed(2)),BFwrk.Util.formatNumber(vdSaldoInsoluto.toFixed(2)),"A","ACTIVA"); }
	 * else{ vlDiasDif = NS.restaFechas(vdFecIni, vdFecFin);
	 * 
	 * //If vsTipoMenu = "B" And frmAltaCredito.txtSobreTasaCB.Text <> 0
	 * And chkSobreTasa.Value = 1 Then //vdInteres =
	 * (((CDbl(frmAltaCredito.txtSobreTasaCB.Text) / 100 /
	 * CInt(vsBisiesto)) * vdSaldoInsoluto) * vlDiasDif) //Else
	 * vdInteres = (((pdTasa / 100 / CInt(vsBisiesto)) *
	 * vdSaldoInsoluto) * vlDiasDif) //End If
	 * if(NS.txtDiasInt.getValue()==0) vdFecPag = vdFecIni; } } }
	 */
	NS.testInteres = function(vlBisiesto) {
		var vdInteres=0;
		NS.storeSolicitudes2.removeAll();
		BFwrk.Util.msgWait('Calculando intereses...', true);
		vlBisiesto = parseFloat(BFwrk.Util.unformatNumber(vlBisiesto));
		vlDiaCorte = NS.cmbDiaCorteInt.getValue();
		vlDiasPeriodo = NS.txtDiasInt.getValue();
		vdSaldoInsoluto = parseFloat(BFwrk.Util
				.unformatNumber(NS.txtMontoDisposicion.getValue()));
		vlNoAmort = BFwrk.Util.unformatNumber(NS.txtNoAmortInt
				.getValue());
		vsFecIni = NS.txtFecDisp.getValue();
		var vsFecVen, vsFecCapital;
		var vsFecPag, vdTotInteres = 0;
		// If vsTipoMenu = "B" And frmAltaCredito.txtSobreTasaCB.Text <>
		// 0 And chkSobreTasa.Value = 1 Then
		// dValorTasa = CDbl(frmAltaCredito.txtSobreTasaCB.Text)
		// Else
		dValorTasa = parseFloat(BFwrk.Util.unformatNumber(NS.pdTasa));
		// End If
		// c=número de línea en el grid de capital
		c = 0;
		var data = new Array();
		var data2 = new Array();
		var data3 = new Array();
		NS.storeSolicitudes.each(function(records) {
			data.push(records.get('fecIni'));
			data2.push(records.get('fecVen'));
			data3.push(parseFloat(records.get('saldo')));
		});
		var inhabil;
		if (NS.oCalculoI == '0')
			inhabil = "E";
		else if (NS.oCalculoI == '1')
			inhabil = "A";
		else
			inhabil = "P";
		AltaFinanciamientoAction.obtenDiaHabil(
				NS.cadenaFechas,inhabil,
				function(mapResult, e) {
					BFwrk.Util.msgWait('Terminado...', false);
					if (mapResult.fechasHabiles !== null
							&& mapResult.fechasHabiles !== ''
								&& mapResult.fechasHabiles != undefined) {
						var inhabil;
						if (NS.oCalculoI == '0')
							inhabil = "E";
						else if (NS.oPagoI == '0')
							inhabil = "A";
						else
							inhabil = "P";
						AltaFinanciamientoAction
						.obtenDiaHabil(NS.cadenaFechas,inhabil,
								function(mapResult2, e) {
							BFwrk.Util.msgWait('Terminado...',false);
							if (mapResult2.fechasHabiles !== null
									&& mapResult2.fechasHabiles !== ''
										&& mapResult2.fechasHabiles != undefined) {
								for (var i = 0; i <= mapResult.fechasHabiles.length - 1; i++) {
									vsFecPag = mapResult.fechasHabiles[i];
									vsFecVen = mapResult2.fechasHabiles[i];
									vsFecIniCapital = data[c];
									vsFecCapital = data2[c];
									var fec = vsFecIniCapital
									.split("/");
									vsFecIniCapital = new Date(
											parseInt(fec[2],10),parseInt(fec[1],10) - 1,parseInt(fec[0],10));
									var fec2 = vsFecCapital.split("/");
									vsFecCapital = new Date(parseInt(fec2[2],10),parseInt(fec2[1],10) - 1,
											parseInt(fec2[0],10));
									var fec3 = vsFecVen
									.split("/");
									vsFecVen = new Date(parseInt(fec3[2],10),parseInt(fec3[1],10) - 1,parseInt(fec3[0],10));
									if ((vsFecVen > vsFecCapital)
											&& (vsFecIni >= vsFecCapital)) {
										c = c + 1;
										vsFecIniCapital = data[c];
										vsFecCapital = data2[c];
										vdSaldoInsoluto = data3[c];
										fec = vsFecIniCapital.split("/");
										vsFecIniCapital = new Date(parseInt(
												fec[2],10),parseInt(fec[1],10) - 1,
												parseInt(fec[0],10));
										fec2 = vsFecCapital.split("/");
										vsFecCapital = new Date(parseInt(fec2[2],10),parseInt(fec2[1],10) - 1,parseInt(fec2[0],10));
									}
									if ((vsFecIni <= vsFecCapital)
											&& (vsFecVen > vsFecCapital)) {
										vlDayDif = NS.restaFechas(BFwrk.Util.changeDateToString(''+ vsFecIni),
														BFwrk.Util.changeDateToString(''+ vsFecCapital));
										vdInteres = (dValorTasa / 100 / vlBisiesto)* vlDayDif* vdSaldoInsoluto;
										c = c + 1;

										try {
											fech = data2[c]
											.split("/");
											f = new Date(parseInt(fech[2],10),parseInt(fech[1],10) - 1,parseInt(fech[0],10));
										} catch (err) {
											fech = "00/00/0000".split("/");
											f = new Date(parseInt(fech[2],10),
															parseInt(fech[1],10) - 1,
																	parseInt(fech[0],10));
										}

										while (vsFecVen > f) {
											vdSaldoInsoluto = data3[c];
											vlDayDif = vlDayDif+ (NS.restaFechas(data[c],data2[c]));
											vdInteres = vdInteres+ (dValorTasa / 100 / vlBisiesto)* (NS.restaFechas(data[c],data2[c]) * vdSaldoInsoluto);
											c = c + 1;
										}
										vdSaldoInsoluto = data3[c];
										try {
											vlDayDif = vlDayDif
											+ (NS
													.restaFechas(
															data[c],
															BFwrk.Util
															.changeDateToString(''
																	+ vsFecVen)));
										} catch (err) {
											lDayDif = vlDayDif
											+ (NS.restaFechas("00/00/0000",
															BFwrk.Util.changeDateToString(''+ vsFecVen)));
										}
										vdInteres = vdInteres+ (dValorTasa / 100 / vlBisiesto)* (NS.restaFechas(data[c],BFwrk.Util.changeDateToString(''+ vsFecVen)) * vdSaldoInsoluto);
									} else {
										if (NS.chkDiasExacInt
												.getValue() == true)
											vlDayDif = vlDiasPeriodo;
										else
											vlDayDif = NS
											.restaFechas(
													BFwrk.Util
													.changeDateToString(''
															+ vsFecIni),
															BFwrk.Util
															.changeDateToString(''
																	+ vsFecVen));
										vdInteres = dValorTasa/ 100/ vlBisiesto* vlDayDif* vdSaldoInsoluto;
									}
									vsDescEstatus = "ACTIVA";
									vsEstatus = "A";
									if (vlDayDif > 0) {
										NS.agregarCriterioValor2(BFwrk.Util.changeDateToString(''+ vsFecIni),
												BFwrk.Util.changeDateToString(''+ vsFecVen),vsFecPag,vlDayDif,
												vdSaldoInsoluto,dValorTasa,vdInteres,
												vdInteres * parseFloat(BFwrk.Util.unformatNumber(NS.txtIva.getValue()) / 100),
												vsEstatus,vsDescEstatus);
										vdTotInteres = vdTotInteres+ vdInteres;
									}
									vsFecIni = vsFecVen;
								}
							}
							NS.txtTotalInt
							.setValue(BFwrk.Util
									.formatNumber(vdTotInteres
											.toFixed(2)));
							NS.txtTotal
							.setValue(BFwrk.Util
									.formatNumber((parseFloat(BFwrk.Util
											.unformatNumber(NS.txtTotalInt
													.getValue())) + parseFloat(BFwrk.Util
															.unformatNumber(NS.txtTotalCap
																	.getValue())))
																	.toFixed(2)));
							NS.txtFecVenInt
							.focus();
						});
					}
				});
	}
	NS.agregarInt = function() {
		NS.storeSolicitudes2.removeAll();
		var vdCapital = 0;
		var psRecorre;
		var data = new Array();
		NS.storeSolicitudes.each(function(records) {
			data.push(records.get('capital'));
		});
		for (var i = 0; i <= data.length - 1; i++) {
			vdCapital = vdCapital + (parseFloat(data[i]));
		}
		if (vdCapital != parseFloat(BFwrk.Util
				.unformatNumber(NS.txtMontoDisposicion.getValue()))) {
			Ext.Msg
			.alert('SET',
					'El monto del capital debe ser igual al de la disposición');
		} else {
			if (NS.soloCapital)
				NS.vbExistAmort = false;
			if (!NS.vbExistAmort && !NS.chkSobreTasa.getValue()
					&& NS.storeSolicitudes2.getCount() <= 1) {
				if (NS.storeSolicitudes.getCount() < 1) {
					Ext.Msg
					.alert('SET',
							'Debe agregar primero las Amortizaciones de Capital');
				} else {
					vdIva = NS.txtIva.getValue();
					if (NS.oD == '0')
						NS.vsBisiesto = "360";
					else
						NS.vsBisiesto = "365";

					if (NS.oPeriodI == '1') {
						// No entra a esta condición, envía errores en
						// el de créditos bancarios
						// if ((NS.cmbDiaCorteInt.getValue==
						// ""||NS.cmbDiaCorteInt.getValue<=0)&&(NS.txtDiasInt.getValue()!=0)){
						// NS.unico(true, NS.vsBisiesto);
						// }
						// else{
						NS.validaDatosInteres();
						NS.datosValidosInt = NS.bandPI;
						if (NS.datosValidosInt) {
							NS.testInteres(NS.vsBisiesto);
						}
					}
				}
			}
		}
	}
	NS.cmdAgregarInt = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAgregarInt',
		name : PF + 'cmdAgregarInt',
		text : '<<',
		x : 895,
		y : 120,
		width : 30,
		listeners : {
			click : {
				fn : function(e) {
					NS.agregarInt();
				}
			}
		}
	});
	// Este botón es en caso de que el usuario no tenga la facultad para
	// eliminar los intereses
	NS.quitarInt = function() {
		var pdTotal;
		var pdCapital;
		var pdInteres;
		var pbSel;
		var i;
		var sMensaje;
		if (NS.proyecto == 'CIE') {
			if (NS.chkSobreTasa.getValue(0))
				NS.storeSolicitudes2.removeAll();
		} else {
			if (NS.storeSolicitudes.getCount() >= 1) {
				if (NS.vbExistAmort) {
					Ext.Msg
					.alert('SET',
							'No tiene facultad para eliminar los Intereses');
				} else {
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
							'Se van a eliminar los registros de Intereses. ¿Desea continuar?',
							function(btn) {
								if (btn === 'yes') {
									if (NS.vbPagado) {
										Ext.Msg
										.alert('SET',
										'No se pueden eliminar las amortizaciones por que ya existen pagadas.');
									} else {
										AltaFinanciamientoAction
										.deleteAmortizacion(
												NS.cmbContratos
												.getValue(),
												parseInt(NS.cmbDisp
														.getValue()),
														true,
														false,
														function(
																mapResult,
																e) {
													BFwrk.Util
													.msgWait(
															'Terminado...',
															false);

												});

										AltaFinanciamientoAction
										.funSQLDeleteProvisiones(
												NS.cmbContratos
												.getValue(),
												parseInt(NS.cmbDisp
														.getValue()),
														false,
														function(
																mapResult,
																e) {
													BFwrk.Util
													.msgWait(
															'Terminado...',
															false);

												});
										NS.storeSolicitudes2
										.removeAll();
										NS.optPeriodIntereses
										.setValue(1);
										NS.oPeriodI = '1';
										NS.optPeriodIntereses
										.setDisabled(false);
										NS.cmbDiaCorteInt
										.reset();
										NS.cmbDiaCorteInt
										.setValue('');
										NS.cmbDiaCorteInt
										.setDisabled(false);
										NS.oD = '0';
										NS.optDias.setValue(0);
										NS.txtComentario
										.setValue("");
										NS.txtComentario
										.setDisabled(true);
										NS.txtDiasInt
										.setValue("");
										NS.cmbPeriodoInt
										.reset();
										NS.cmbPeriodoInt
										.setValue("");
										NS.cmbPeriodoInt
										.setDisabled(false);
										NS.chkSobreTasa
										.setValue(0);
										NS.chkSobreTasa
										.setDisabled(true);
										NS.txtInteres
										.setValue("0.00");
										NS.txtIva
										.setValue("0.00");
										NS.txtIva
										.setDisabled(true);
										NS.txtTotalInt
										.setValue("0.00");
										NS.txtTotal
										.setValue(NS.txtTotalCap
												.getValue());
										NS.txtNoAmortInt
										.setValue("0");
										NS.txtDiasInt
										.setDisabled(true);
										NS.vbExistAmort = false;
										NS.storeSolicitudes2
										.load();

									}
								}
							});
				}
			}
		}
	}
	// Este botón es en caso de que el usuario tenga la facultad para
	// eliminar los intereses
	NS.quitarInt2 = function() {
		var pdTotal;
		var pdCapital;
		var pdInteres;
		var pbSel;
		var i;
		var sMensaje;
		if (NS.proyecto == 'CIE') {
			if (NS.chkSobreTasa.getValue(0))
				NS.storeSolicitudes2.removeAll();
		} else {
			if (NS.storeSolicitudes.getCount() >= 1) {
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
						'Se van a eliminar los registros de Intereses. ¿Desea continuar?',
						function(btn) {
							if (btn === 'yes') {
								if (NS.vbPagado) {
									Ext.Msg
									.alert('SET',
									'No se pueden eliminar las amortizaciones por que ya existen pagadas.');
								} else {
									AltaFinanciamientoAction
									.deleteAmortizacion(
											NS.cmbContratos
											.getValue(),
											parseInt(NS.cmbDisp
													.getValue()),
													true,
													false,
													function(
															mapResult,
															e) {
												BFwrk.Util
												.msgWait(
														'Terminado...',
														false);

											});
									AltaFinanciamientoAction
									.funSQLDeleteProvisiones(
											NS.cmbContratos
											.getValue(),
											parseInt(NS.cmbDisp
													.getValue()),
													false,
													function(
															mapResult,
															e) {
												BFwrk.Util
												.msgWait(
														'Terminado...',
														false);

											});
									NS.storeSolicitudes2
									.removeAll();
									NS.optPeriodIntereses
									.setValue(1);
									NS.oPeriodI = '1';
									NS.optPeriodIntereses
									.setDisabled(false);
									NS.cmbDiaCorteInt.reset();
									NS.cmbDiaCorteInt
									.setValue('');
									NS.cmbDiaCorteInt
									.setDisabled(false);
									NS.oD = '0';
									NS.optDias.setValue(0);
									NS.txtComentario
									.setValue("");
									NS.txtComentario
									.setDisabled(true);
									NS.txtDiasInt.setValue("");
									NS.cmbPeriodoInt.reset();
									NS.cmbPeriodoInt
									.setValue("");
									NS.cmbPeriodoInt
									.setDisabled(false);
									NS.chkSobreTasa.setValue(0);
									NS.chkSobreTasa
									.setDisabled(true);
									NS.txtInteres
									.setValue("0.00");
									NS.txtIva.setValue("0.00");
									NS.txtIva.setDisabled(true);
									NS.txtTotalInt
									.setValue("0.00");
									NS.txtTotal
									.setValue(NS.txtTotalCap
											.getValue());
									NS.txtNoAmortInt
									.setValue("0");
									NS.txtDiasInt
									.setDisabled(true);
									NS.vbExistAmort = false;
									NS.storeSolicitudes2.load();
								}
							}
						});
			}
		}
	}
	// Este botón es en caso de que el usuario tenga la facultad para
	// eliminar los intereses
	NS.cmdQuitarInt2 = apps.SET.btnFacultativo(new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdQuitarInt2',
		name : PF + 'cmdQuitarInt2',
		text : '>>',
		x : 895,
		y : 150,
		// disabled : true,
		width : 30,
		listeners : {
			click : {
				fn : function(e) {
					NS.quitarInt2();
				}
			}
		}
	}));
	NS.cmdQuitarInt = apps.SET.btnFacultativo(new Ext.Button({
		id : 'cmdQuitarInt',
		name : 'cmdQuitarInt',
		text : '>>',
		x : 895,
		y : 150,
		// disabled : true,
		width : 30,
		listeners : {
			click : {
				fn : function(e) {
					NS.quitarInt();
				}
			}
		}

	}));
	// TOTALES
	NS.lblTotalCap = new Ext.form.Label({
		text : 'Total Capital:',
		x : 20,
		y : 620
	});

	NS.txtTotalCap = new Ext.form.TextField({
		id : PF + 'txtTotalCap',
		name : PF + 'txtTotalCap',
		disabled : true,
		x : 20,
		y : 635,
		width : 120,
		value : '0.00',
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	NS.lblTotalInt = new Ext.form.Label({
		text : 'Total Interés:',
		x : 150,
		y : 620
	});
	NS.txtTotalInt = new Ext.form.TextField({
		id : PF + 'txtTotalInt',
		name : PF + 'txtTotalInt',
		disabled : true,
		x : 150,
		y : 635,
		value : '0.00',
		width : 120,
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	NS.lblTotal = new Ext.form.Label({
		text : 'Total:',
		x : 280,
		y : 620
	});
	NS.txtTotal = new Ext.form.TextField({
		id : PF + 'txtTotal',
		name : PF + 'txtTotal',
		disabled : true,
		x : 280,
		y : 635,
		width : 120,
		value : '0.00',
		listeners : {
			change : {
				fn : function(caja, valor) {
				}
			}
		}
	});
	/**
	 * ******************** Generar el excel
	 * ********************************
	 */
	NS.excel = function() {
		if (NS.storeSolicitudes.getCount() < 1) {
			Ext.Msg
			.alert("SET",
			"Necesitan  existir datos para poder enviarlos a Excel.");
			return;
		}
		parametros = '?nomReporte=excelAmortizaciones';
		parametros += '&nomParam1=disposicion';
		parametros += '&valParam1=' + NS.cmbDisp.getValue();
		parametros += '&nomParam2=contrato';
		parametros += '&valParam2=' + NS.txtLinea.getValue();
		parametros += '&nomParam3=montoAutorizado';
		parametros += '&valParam3=' + NS.txtMontoAutorizado.getValue();
		parametros += '&nomParam4=banco';
		parametros += '&valParam4=' + NS.cmbBancoLin.getRawValue();
		parametros += '&nomParam5=montoDisposicion';
		parametros += '&valParam5=' + NS.txtMontoDisposicion.getValue();
		parametros += '&nomParam6=divisa';
		parametros += '&valParam6=' + NS.cmbDivisa.getRawValue();
		parametros += '&nomParam7=fechaInicio';
		parametros += '&valParam7='
			+ BFwrk.Util.changeDateToString(''
					+ NS.txtFecDisp.getValue());
		parametros += '&nomParam8=fechaFin';
		parametros += '&valParam8='
			+ BFwrk.Util.changeDateToString(''
					+ NS.txtFecVenDisp.getValue());
		var tasa;
		if (NS.oTasa == '1') {
			if (NS.pvTasaBase == "TIIE")
				tasa = NS.cmbTasa.getRawValue() + "+"
				+ NS.txtPuntos.getValue() + " Puntos";
			else
				tasa = NS.cmbTasa.getRawValue();
		} else {
			tasa = NS.txtTasaFija.getValue();
		}
		parametros += '&nomParam9=tasa';
		parametros += '&valParam9=' + tasa;
		window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"
				+ parametros);

	}
	/** **************Fin de generar excel ********************** */
	NS.cmdImpExcel = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdImpExcel',
		name : PF + 'cmdImpExcel',
		text : 'Exportar Excel',
		x : 840,
		y : 620,
		disabled : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					// If vsTipoMenu = "A" Then
					// Call CreaExcel
					// Else
					NS.excel();
					// End If

				}
			}
		}
	});
	NS.cmdModificarA = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdModificarA',
		name : PF + 'cmdModificarA',
		text : 'Modificar',
		x : 750,
		y : 620,
		hidden : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.pbModificar = true;
					NS.chkRenta.setVisible(true);

				}
			}
		}
	});
	NS.cmdCancelarA = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCancelarA',
		name : PF + 'cmdCancelarA',
		text : 'Cancelar',
		x : 840,
		y : 650,
		// disabled : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.plConsAmort = 0;
					NS.winAmortizacion.hide();
				}
			}
		}
	});
	NS.cmdLimpiarA = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdLimpiarA',
		name : PF + 'cmdLimpiarA',
		text : 'Limpiar',
		x : 750,
		y : 650,
		// disabled : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.limpiaTodo();
				}
			}
		}
	});
	NS.validaDatosAmort = function() {
		var capital = 0;
		capital = NS.gridDatos.getSelectionModel().getSelections();
		var intereses = 0;
		intereses = NS.gridDatos2.getSelectionModel().getSelections();
		NS.bandAmort = false;
		if (NS.storeSolicitudes.getCount() <= 1) {
			if (NS.oPeriodC != "1" && NS.oPeriodC != "0") {
				Ext.Msg.alert('SET', 'Agregue las amortizaciones.');
				Ext.getCmp(PF + 'cmdAgregarInt').focus();
				NS.bandAmort = false;
				return;
			}
			if (capital.length > 0) {
				if (capital[capital.length - 1].get('noAmort') == '') {
					if (NS.oPeriodC == "1") {
						Ext.Msg
						.alert('SET',
						'Presione el botón "<<" para agregar las amortizaciones.');
						Ext.getCmp(PF + 'cmdAgregar').focus();
						NS.bandAmort = false;
						return;
					}
					if (NS.oPeriodC == "0") {
						Ext.Msg
						.alert('SET',
						'Debe agregar al menos una amortización.');
						Ext.getCmp(PF + 'cmdAgregar').focus();
						NS.bandAmort = false;
						return;
					}
				}
			} else {
				if (NS.oPeriodC == "1") {
					Ext.Msg
					.alert('SET',
					'Presione el botón "<<" para agregar las amortizaciones.');
					Ext.getCmp(PF + 'cmdAgregar').focus();
					NS.bandAmort = false;
					return;
				}
				if (NS.oPeriodC == "0") {
					Ext.Msg.alert('SET',
					'Debe agregar al menos una amortización.');
					Ext.getCmp(PF + 'cmdAgregar').focus();
					NS.bandAmort = false;
					return;
				}
			}
		}
		if (NS.storeSolicitudes2.getCount() <= 1) {
			if (intereses.length > 0) {
				if (intereses[intereses.length - 1].get('fecVen') == '') {
					if (NS.oPeriodI == "1") {
						Ext.Msg
						.alert('SET',
						'Presione el botón "<<" para agregar los intereses.');
						Ext.getCmp(PF + 'cmdAgregarInt').focus();
						NS.bandAmort = false;
						return;
					}
					if (NS.oPeriodI == "0") {
						Ext.Msg.alert('SET',
						'Debe agregar al menos un interés.');
						Ext.getCmp(PF + 'txtInteres').focus();
						NS.bandAmort = false;
						return;
					}
				}
			}

		}
		NS.bandAmort = true;
		NS.pbCredSind = NS.pbCredSind;
	}
	NS.aceptarA = function() {
		var respSwap;
		var resp;
		var ps_tasa;
		var ps_plazo;
		var vsBisiesto;
		var vdTasaVar;
		var vdTasaFij;
		var viSobreTasaBC;
		var lFolio;
		var pbIntAnt;
		NS.pbGuardado = false;
		NS.validaDatosAmort();
		var validaDatosAmort = NS.bandAmort;
		if (!validaDatosAmort)
			return;
		// If vsTipoMenu = "A" Then
		// If pbModificar Then
		// Call ModificaArrendamiento
		// Else
		// Call InsertArrendamiento
		// End If
		// End If
		if (NS.txtDias.getValue() == "")
			NS.txtDias.setValue("0");
		if (NS.txtDiasInt.getValue() == "")
			NS.txtDiasInt.setValue("0");
		AltaFinanciamientoAction
		.selectExisteAmortizacion(
				NS.txtLinea.getValue(),
				parseInt(NS.cmbDisp.getValue()),
				0,
				function(resultado, e) {
					BFwrk.Util.msgWait('Terminado...', false);
					if (resultado > 0) {
						Ext.Msg
						.alert("SET",
						"La Tabla de amortizaciones ya fue dada de alta.");
						return;
					} else {
						BFwrk.Util.msgWait(
								'Guardando Amortizaciones...',
								true);
						if (NS.oTasa == "0")
							ps_tasa = "F";
						if (NS.oTasa == "1")
							ps_tasa = "V";
						if (NS.chkLargoPlazo.getValue() == 0)
							ps_plazo = "S";
						if (NS.chkLargoPlazo.getValue() == 1)
							ps_plazo = "N";
						/*
						 * If Val(txtDiasInt.Text) = 0 And
						 * MSFDatos2.Rows - 1 = 1 And _
						 * MSFDatos2.Rows = 2 And vsTipoMenu <>
						 * "A" And optSiInt.Value Then 'INTERES
						 * PAGADO POR ANTICIPADO Operacion =
						 * 1027 'Realiza un registro en
						 * movimiento cargo en cta. devido a que
						 * es un interes pagado por anticipado
						 * If MsgBox("Quiere pagar los interese
						 * por anticipado?", vbQuestion +
						 * vbYesNo, "SET") = vbNo Then
						 * Screen.MousePointer = 0
						 * proBar.Visible = False Exit Sub Else
						 * pbIntAnt = True End If End If
						 */
						if (NS.oD == "0")
							vsBisiesto = "360";
						if (NS.oD == "1")
							vsBisiesto = "365";
						if (NS.soloCapital)
							NS.gridDatos.getSelectionModel()
							.clearSelections();
						else
							NS.gridDatos.getSelectionModel()
							.selectAll();
						var capital = NS.gridDatos
						.getSelectionModel()
						.getSelections();
						var matrizCapital = new Array();
						for (var i = 0; i < capital.length; i++) {
							var registrosCapital = {};
							registrosCapital.noAmort = capital[i]
							.get('noAmort');
							registrosCapital.fecIni = capital[i]
							.get('fecIni');
							registrosCapital.fecVen = capital[i]
							.get('fecVen');
							registrosCapital.fecPag = capital[i]
							.get('fecPag');
							registrosCapital.dias = capital[i]
							.get('dias');
							registrosCapital.capital = capital[i]
							.get('capital');
							registrosCapital.saldo = capital[i]
							.get('saldo');
							registrosCapital.estatus = capital[i]
							.get('estatus');
							registrosCapital.descEstatus = capital[i]
							.get('descEstatus');
							matrizCapital[i] = registrosCapital;
						}
						var jsonStringCapital = Ext.util.JSON
						.encode(matrizCapital);
						if (NS.storeSolicitudes2.getCount() > 0)
							NS.gridDatos2.getSelectionModel()
							.selectAll();
						var interes = NS.gridDatos2
						.getSelectionModel()
						.getSelections();
						var matrizInteres = new Array();
						for (var i = 0; i < interes.length; i++) {
							var registrosInteres = {};
							registrosInteres.fecInicio = interes[i]
							.get('fecInicio');
							registrosInteres.fecVenc = interes[i]
							.get('fecVenc');
							registrosInteres.fecPago = interes[i]
							.get('fecPago');
							registrosInteres.dias = interes[i]
							.get('dias');
							registrosInteres.saldo = interes[i]
							.get('saldo');
							registrosInteres.saldoIns = interes[i]
							.get('saldoIns');
							registrosInteres.interes = interes[i]
							.get('interes').toFixed(2);
							registrosInteres.iva = interes[i]
							.get('iva').toFixed(2);
							registrosInteres.estatus = interes[i]
							.get('estatus');
							registrosInteres.descEstatus = interes[i]
							.get('descEstatus');
							matrizInteres[i] = registrosInteres;
						}
						var jsonStringInteres = Ext.util.JSON
						.encode(matrizInteres);
						var period;
						if (((NS.cmbPeriodo.store.getCount() <= 0) && (NS.cmbPeriodo
								.getRawValue() == ""))
								|| (NS.cmbPeriodo.getRawValue() == ""))
							period = "";
						else
							period = NS.cmbPeriodo
							.getRawValue();
						var periodInt;
						if (((NS.cmbPeriodoInt.store.getCount() <= 0) && (NS.cmbPeriodoInt
								.getRawValue() == ""))
								|| (NS.cmbPeriodoInt
										.getRawValue() == ""))
							periodInt = "";
						else
							periodInt = NS.cmbPeriodoInt
							.getRawValue();
						var noAmort;
						if (NS.txtNoAmort.getValue() == "")
							noAmort = 0;
						else
							noAmort = parseInt(NS.txtNoAmort
									.getValue());
						var diaCorte;
						if (NS.cmbDiaCorte.getValue() == '')
							diaCorte = 0;
						else
							diaCorte = parseInt(NS.cmbDiaCorte
									.getValue());
						var dias;
						if (NS.txtDias.getValue() == '')
							dias = 0;
						else
							dias = parseInt(NS.txtDias
									.getValue());
						var diasInt;
						if (NS.txtDiasInt.getValue() == '')
							diasInt = 0;
						else
							diasInt = parseInt(NS.txtDiasInt
									.getValue());
						var sobreTasa = 0;
						if (NS.txtSobreTasaCB.getValue() != '')
							sobreTasa = parseInt(NS.txtSobreTasaCB
									.getValue());
						AltaFinanciamientoAction
						.altaAmortizacion(
								jsonStringCapital,
								jsonStringInteres,
								ps_tasa,
								vsBisiesto,
								NS.txtLinea.getValue(),
								NS.cmbDisp.getValue(),
								BFwrk.Util
								.unformatNumber(NS.txtTasaVigente
										.getValue()),
										BFwrk.Util
										.unformatNumber(NS.txtTasaFija
												.getValue()),
												period,
												noAmort,
												NS.pvTasaBase,
												BFwrk.Util
												.unformatNumber(NS.txtBase
														.getValue()),
														BFwrk.Util
														.unformatNumber(NS.txtPuntos
																.getValue()),
																BFwrk.Util
																.unformatNumber(NS.txtIva
																		.getValue()),
																		diaCorte,
																		dias,
																		NS.txtComentario
																		.getValue(),
																		NS.chkDiasExacInt
																		.getValue(),
																		parseInt(NS.txtNoAmortInt
																				.getValue()),
																				periodInt,
																				sobreTasa,
																				NS.chkSobreTasa
																				.getValue(),
																				NS.cmbDiaCorteInt
																				.getValue(),
																				diasInt,
																				parseInt(NS.txtEmpresa.getValue()),
																				function(mapResult, e) {
									BFwrk.Util
									.msgWait(
											'Terminado...',
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
										NS.limpiaTodo();
										NS.winAmortizacion
										.hide();
									}
								});
					}
				});
	}
	NS.cmdAceptarA = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAceptarA',
		name : PF + 'cmdAceptarA',
		text : 'Aceptar',
		x : 660,
		y : 650,
		// disabled : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.aceptarA();
				}
			}
		}
	});

	NS.provision = function() {
		AltaFinanciamientoAction
		.provisiones(NS.txtLinea.getValue(),parseInt(NS.cmbDisp.getValue()),parseInt(NS.txtEmpresa.getValue()),function(mapResult,e) {
			BFwrk.Util.msgWait('Guardando Provisiones...',true);
			for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
				Ext.Msg.alert('Información SET',''+ mapResult.msgUsuario[msg]);
			}
		});
	}
	NS.cmdProvision = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdProvision',
		name : PF + 'cmdProvision',
		text : 'Provisiones',
		x : 570,
		y : 650,
		// disabled : true,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.provision();
				}
			}
		}
	});
	// *******************************************************************************
	NS.consultarAmortizacion = function(valor) {
		NS.optPeriodCapital.setDisabled(valor);
		NS.optPeriodIntereses.setDisabled(valor);
		// NS.txtNoAmort.setDisabled(valor);
		// NS.txtNoAmortInt.setDisabled(valor);
		NS.txtFecVenC.setDisabled(valor);
		NS.txtFecVenInt.setDisabled(valor);
		NS.txtCapital.setDisabled(valor);
		NS.txtInteres.setDisabled(valor);
		NS.cmbPeriodo.setDisabled(valor);
		NS.cmbPeriodoInt.setDisabled(valor);
		NS.cmdAgregar.setDisabled(valor);
		NS.cmdAgregarInt.setDisabled(valor);
		NS.cmdQuitar.setDisabled(valor);
		NS.cmdQuitar2.setDisabled(valor);
		NS.cmdQuitarInt.setDisabled(valor);
		NS.cmdQuitarInt2.setDisabled(valor);
	}
	NS.winAmortizacion = new Ext.Window(
			{
				title : 'Tabla de amortizaciones (Créditos bancarios)',
				modal : true,
				shadow : true,
				closeAction : 'hide',
				width : 980,
				height : 520,
				layout : 'absolute',
				plain : true,
				bodyStyle : 'padding:10px;',
				buttonAlign : 'center',
				draggable : true,
				resizable : true,
				autoScroll : true,
				items : [
				         {
				        	 xtype : 'fieldset',
				        	 title : 'Capital',
				        	 id : PF + 'panelCapital',
				        	 name : PF + 'panelCapital',
				        	 x : 0,

				        	 y : 0,
				        	 width : 950,
				        	 height : 300,
				        	 layout : 'absolute',
				        	 items : [
				        	          {
				        	        	  xtype : 'fieldset',
				        	        	  id : 'frmPeriodCapital',
				        	        	  x : 0,
				        	        	  y : 0,
				        	        	  width : 200,
				        	        	  height : 50,
				        	        	  layout : 'absolute',
				        	        	  items : [ NS.optPeriodCapital ]
				        	          },
				        	          NS.lblPeriodoC,
				        	          NS.cmbPeriodo,
				        	          NS.lblDiaCorte,
				        	          NS.cmbDiaCorte,
				        	          NS.lblDia,
				        	          NS.txtDiasC,
				        	          {
				        	        	  xtype : 'fieldset',
				        	        	  title : 'Primer Pago de Capital',
				        	        	  id : 'frmPrimerPago',
				        	        	  x : 330,
				        	        	  y : -2,
				        	        	  width : 200,
				        	        	  height : 60,
				        	        	  layout : 'absolute',
				        	        	  items : [ NS.chkPeriodoGracia,
				        	        	            NS.txtPeriodoGracia ]
				        	          }, NS.lblNoAmort, NS.txtNoAmort,
				        	          NS.txtFecVenC, NS.lblVencimiento,
				        	          NS.chkDiasReales,
				        	          NS.lblDiasReales1,
				        	          NS.lblDiasReales2, NS.txtIvaC,
				        	          NS.lblIvaC, NS.lblCapital,
				        	          NS.txtCapital, NS.lblFactorCapital,
				        	          NS.txtFactorCapital,
				        	          NS.cmdCargaExcel, NS.chkRenta,
				        	          NS.lblRentaC, NS.gridDatos,
				        	          NS.cmdAgregar, NS.cmdQuitar,
				        	          NS.cmdQuitar2, ]
				         },
				         {
				        	 xtype : 'fieldset',
				        	 title : 'Intereses',
				        	 id : PF + 'panelIntereses',
				        	 name : PF + 'panelIntereses',
				        	 x : 0,
				        	 y : 300,
				        	 width : 950,
				        	 height : 300,
				        	 layout : 'absolute',
				        	 items : [
				        	          {
				        	        	  xtype : 'fieldset',
				        	        	  id : 'frmPeriodIntereses',
				        	        	  x : 0,
				        	        	  y : 0,
				        	        	  width : 200,
				        	        	  height : 50,
				        	        	  layout : 'absolute',
				        	        	  items : [ NS.optPeriodIntereses ]
				        	          },
				        	          NS.lblPeriodoInt,
				        	          NS.cmbPeriodoInt,
				        	          NS.lblDiaCorteInt,
				        	          NS.cmbDiaCorteInt,
				        	          NS.lblDiaInt,
				        	          NS.txtDiasInt,
				        	          {
				        	        	  xtype : 'fieldset',
				        	        	  title : 'Primer Pago de Interes',
				        	        	  id : 'frmPrimerPagoI',
				        	        	  x : 330,
				        	        	  y : -2,
				        	        	  width : 200,
				        	        	  height : 60,
				        	        	  layout : 'absolute',
				        	        	  items : [ NS.chkPrimerPagoInt,
				        	        	            NS.txtPrimerPagoInt ]
				        	          }, NS.lblNoAmortInt,
				        	          NS.txtNoAmortInt, NS.txtFecVenInt,
				        	          NS.lblVencimientoInt,
				        	          NS.txtInteres, NS.lblInteres,
				        	          NS.chkDiasExacInt,
				        	          NS.lblDiasExacInt1,
				        	          NS.lblDiasExacInt2, {
				        	        	  xtype : 'fieldset',
				        	        	  title : 'Días',
				        	        	  id : 'frmDias',
				        	        	  x : 550,
				        	        	  y : 50,
				        	        	  width : 100,
				        	        	  height : 60,
				        	        	  layout : 'absolute',
				        	        	  items : [ NS.optDias ]
				        	          }, NS.txtIva, NS.lblIva,
				        	          NS.chkSobreTasa, NS.lblSobreTasa1,
				        	          NS.lblSobreTasa2,
				        	          // NS.lblCapital,NS.txtCapital,
				        	          NS.lblComentario, NS.txtComentario,
				        	          // NS.cmdCargaExcel,NS.chkRenta,NS.lblRentaC,
				        	          NS.gridDatos2, NS.cmdAgregarInt,
				        	          NS.cmdQuitarInt, NS.cmdQuitarInt2 ]
				         }, NS.lblTotalCap, NS.txtTotalCap,
				         NS.lblTotalInt, NS.txtTotalInt, NS.lblTotal,
				         NS.txtTotal, NS.cmdImpExcel, NS.cmdModificarA,
				         NS.cmdLimpiarA, NS.cmdCancelarA,
				         NS.cmdProvision, NS.cmdAceptarA ],
				         listeners : {
				        	 show : {
				        		 // funcionamiento
				        		 fn : function() {
				        			 //Si el contrato es a corto plazo la última fecha(txtPrimerPagoInt) es la del vencimiento
				        			 if(!NS.chkLargoPlazo.getValue())
				        				 NS.txtPrimerPagoInt.setValue(NS.txtFecVenDisp.getValue());
				        			 NS.optPeriodCapital.setValue(0);
				        			 NS.cmbPeriodo.reset();
				        			 NS.cmbDiaCorte.reset();
				        			 NS.txtDiasC.setValue("");
				        			 NS.txtNoAmort.setValue("");
				        			 NS.vdCapital = 0;
				        			 NS.vdTotal = '';
				        			 NS.vsFechaIni = '';
				        			 NS.plWeekend = 0;
				        			 NS.pdFecha = '';
				        			 NS.psRecorre = '';
				        			 NS.plDiaInhabil = '';
				        			 NS.pdSaldoInsoluto = '';
				        			 NS.diaHabil = '';
				        			 NS.vsBisiesto = '';
				        			 NS.vlNoAmort = '';
				        			 NS.vdSaldoInsoluto = 0;
				        			 NS.oPeriodC = '0';
				        			 //
				        			 NS.txtIva.setValue("0.00");
				        			 NS.chkPeriodoGracia.setValue(false);
				        			 NS.chkPeriodoGracia.setDisabled(true);
				        			 NS.editarAmortizacion(true);
				        			 NS.limpiarValores();
				        			 NS.pdSaldoInsoluto = BFwrk.Util
				        			 .unformatNumber(NS.txtMontoDisposicion
				        					 .getValue());
				        			 NS.txtFecVenC.focus();
				        			 NS.lblIvaC.setDisabled(false);
				        			 NS.txtIvaC.setDisabled(false);
				        			 NS.lblFactorCapital.setDisabled(false);
				        			 NS.txtFactorCapital.setDisabled(false);
				        			 //
				        			 NS.oPeriodI = '';
				        			 NS.storeDias.loadData(NS.dataDia);
				        			 NS.vbPagado = false;
				        			 NS.txtFecVenC.setValue(NS.txtFecVenDisp
				        					 .getValue());
				        			 NS.txtFecVenInt.setValue(NS.txtFecVenDisp
				        					 .getValue());
				        			 var myMask = new Ext.LoadMask(
				        					 Ext.getBody(), {
				        						 store : NS.storeCmbPeriodo,
				        						 msg : "Cargando..."
				        					 });
				        			 NS.storeCmbPeriodo.load();
				        			 if (NS.oTasa == '1')
				        				 NS.pdTasa = BFwrk.Util
				        				 .unformatNumber(NS.txtTasaVigente.getValue());
				        			 else
				        				 NS.pdTasa = BFwrk.Util
				        				 .unformatNumber(NS.txtTasaFija
				        						 .getValue());
				        			 NS.pdSaldoInsoluto = BFwrk.Util
				        			 .unformatNumber(NS.txtMontoDisposicion
				        					 .getValue());
				        			 NS.vbExistAmort = false;
				        			
				        			 var myMask = new Ext.LoadMask(
				        					 Ext.getBody(),
				        					 {
				        						 store : NS.storeSelectAmortizaciones,
				        						 msg : "Cargando..."
				        					 });
				        			 NS.storeSelectAmortizaciones
				        			 .load({
				        				 params : {
				        					 psIdContrato : NS.txtLinea
				        					 .getValue(),
				        					 piDisposicion : parseInt(NS.cmbDisp
				        							 .getValue()),
				        							 pbCambioTasa : false,
				        							 psTipoMenu : '',
				        							 psProyecto : '',
				        							 piCapital : 0
				        				 },
				        				 callback : function(records,
				        						 operation, success) {
				        					 if (records.length <= 0) {
				        						 Ext.Msg
				        						 .alert(
				        								 'SET',
				        								 'La Disposición '
				        								 + NS.cmbDisp
				        								 .getValue()
				        								 + ' no tiene Amortizaciones');
				        						 NS.psInteresSind = "";
				        						 NS.psPorSind = "";
				        						 NS.psIdBanco = "";
				        						 NS
				        						 .consultarAmortizacion(false);
				        						 NS.chkPeriodoGracia
				        						 .setDisabled(false);
				        						 NS.txtPeriodoGracia
				        						 .setDisabled(true);
				        						 NS.txtIva
				        						 .setDisabled(false);
				        						 NS.cmbDiaCorteInt
				        						 .setDisabled(false);
				        						 NS.txtIva
				        						 .setDisabled(false);
				        						 NS.txtComentario
				        						 .setDisabled(false);
				        						 NS.chkDiasExacInt
				        						 .setDisabled(false);
				        						 NS.optDias
				        						 .setDisabled(false);
				        						 NS.chkPrimerPagoInt
				        						 .setDisabled(false);
				        						 NS.txtPeriodoGracia
				        						 .setValue(NS.txtFecDisp
				        								 .getValue());
				        						 NS.cmbPeriodo
				        						 .setDisabled(true);
				        						 //Se valida si es a corto plazo(quirografario) y llena por defecto
				        						 if(NS.cmbTipoContrato.getValue().toUpperCase.indexOf('QUIROGRAFARIO')!=-1){
				        							 NS.txtCapital.setValue(BFwrk.Util.formatNumber(NS.txtMontoDisposicion.getValue()));
				        						 } 
				        					 } else {
				        						 var pdTotalCap = 0, pdTotalInt = 0;
				        						 var vbBan1, vbBan;
				        						 NS.vbExistAmort = true;
				        						 NS.storeSolicitudes
				        						 .load({
				        							 params : {
				        								 psIdContrato : NS.txtLinea
				        								 .getValue(),
				        								 piDisposicion : parseInt(NS.cmbDisp
				        										 .getValue()),
				        										 pbCambioTasa : false,
				        										 psTipoMenu : '',
				        										 psProyecto : '',
				        										 piCapital : 0,
				        							 },
				        							 callback : function(
				        									 records) {
				        								 if (records.length != null
				        										 || records.length > 0) {
				        									 NS.vbExistAmort = true;
				        									 if (vbBan1)
				        										 NS.optPeriodCapital
				        										 .setValue(1);
				        									 if (!vbBan1)
				        										 NS.optPeriodCapital
				        										 .setValue(0);
				        									 NS.cmbDiaCorte
				        									 .setValue(records[0]
				        									 .get('diaCortecap'));
				        									 NS.txtIva
				        									 .setValue(records[0]
				        									 .get('iva'));
				        									 NS.txtPrimerPagoInt
				        									 .setValue(records[0]
				        									 .get('fecVen'));
				        									 NS.cmdCargaExcel
				        									 .setDisabled(false);
				        									 NS.optPeriodCapital
				        									 .setDisabled(true);
				        									 NS.optPeriodIntereses
				        									 .setDisabled(true);
				        									 NS.txtIva
				        									 .setDisabled(true);
				        									 NS.cmdImpExcel
				        									 .setDisabled(false);
				        									 NS.txtComentario
				        									 .setDisabled(true);
				        									 NS.txtDiasInt
				        									 .setDisabled(true);
				        									 var cCount = 1;
				        									 var iCount = 1;
				        									 if (records[0]
				        									 .get('estatus') == "P")
				        										 NS.vbPagado = true;
				        									 if (records[0]
				        									 .get('periodo') != "NO")
				        										 vbBan1 = true;
				        									 NS.txtNoAmort
				        									 .setValue(records[0]
				        									 .get('noAmortizaciones'));
				        									 NS.txtDiasC
				        									 .setValue(records[0]
				        									 .get('diasPeriodo'));
				        									 if (records[0]
				        									 .get('periodo') != "NO")
				        										 NS.cmbPeriodo
				        										 .setValue(records[0]
				        										 .get('periodo'));
				        									 if (i == 0) {
				        										 NS.txtPeriodoGracia
				        										 .setValue(records[0]
				        										 .get('fecVen'));
				        										 i = i + 1;
				        									 }
				        									 NS.vbExistAmort = true;
				        									 NS.gridDatos
				        									 .getSelectionModel()
				        									 .selectAll();
				        									 if (NS.chkDiasExacInt
				        											 .getValue())
				        										 dias = parseInt(NS.txtDiasInt
				        												 .getValue());
				        									 else
				        										 dias = -1;
				        									 for (var i = 0; i <= records.length - 1; i++) {
				        										 pdTotalCap += records[i]
				        										 .get('capital');
				        									 }
				        									 NS.storeSolicitudes2
				        									 .load({
				        										 params : {
				        											 psIdContrato : NS.txtLinea
				        											 .getValue(),
				        											 piDisposicion : parseInt(NS.cmbDisp
				        													 .getValue()),
				        													 pbCambioTasa : false,
				        													 psTipoMenu : '',
				        													 psProyecto : '',
				        													 piCapital : 0,
				        													 dias : dias
				        										 },
				        										 callback : function(
				        												 records) {
				        											 NS.gridDatos2
				        											 .getSelectionModel()
				        											 .selectAll();
				        											 if (records.length > 0
				        													 && records.length != null) {
				        												 if (records[0]
				        												 .get('periodo') != "NO")
				        													 vbBan = true;
				        												 if (vbBan)
				        													 NS.optPeriodIntereses
				        													 .setValue(1);
				        												 if (!vbBan)
				        													 NS.optPeriodIntereses
				        													 .setValue(0);
				        												 vbInteres = true;
				        												 NS.cmbDiaCorteInt
				        												 .setValue(records[0]
				        												 .get('diaCorteint'));
				        												 NS.cmbPeriodoInt
				        												 .setValue(records[0]
				        												 .get('periodo'));
				        												 NS.txtComentario
				        												 .setValue(records[0]
				        												 .get('comentario'));
				        												 NS.txtNoAmortInt
				        												 .setValue(records[0]
				        												 .get('noAmortizaciones'));
				        												 NS.txtDiasInt
				        												 .setValue(records[0]
				        												 .get('diasPeriodo'));
				        												 if (records[0]
				        												 .get('dias') == 360)
				        													 NS.optDias
				        													 .setValue(0);
				        												 if (records[0]
				        												 .get('dias') == 365)
				        													 NS.optDias
				        													 .setValue(1);
				        												 for (var i = 0; i <= records.length - 1; i++) {
				        													 pdTotalInt += records[i]
				        													 .get('interes');
				        												 }
				        												 NS.pbCons = true;
				        												 NS.txtTotalCap
				        												 .setValue(BFwrk.Util
				        														 .formatNumber(pdTotalCap
				        																 .toFixed(2)));
				        												 NS.txtTotalInt
				        												 .setValue(BFwrk.Util
				        														 .formatNumber(pdTotalInt
				        																 .toFixed(2)));
				        												 NS.txtTotal
				        												 .setValue(BFwrk.Util
				        														 .formatNumber((pdTotalCap + pdTotalInt)
				        																 .toFixed(2)));
				        												 NS
				        												 .consultarAmortizacion(true);
				        												 NS.chkPeriodoGracia
				        												 .setDisabled(true);
				        												 NS.txtPeriodoGracia
				        												 .setDisabled(true);
				        												 NS.txtIva
				        												 .setDisabled(true);
				        												 NS.cmbDiaCorteInt
				        												 .setDisabled(true);
				        												 NS.txtIva
				        												 .setDisabled(true);
				        												 NS.txtComentario
				        												 .setDisabled(true);
				        												 NS.chkDiasExacInt
				        												 .setDisabled(true);
				        												 NS.optDias
				        												 .setDisabled(true);
				        												 NS.chkPrimerPagoInt
				        												 .setDisabled(true);
				        												 NS.psInteresSind = "";
				        												 NS.psPorSind = "";
				        												 NS.psIdBanco = "";
				        												 NS.cmdQuitar
				        												 .setDisabled(false);
				        												 NS.cmdQuitarInt
				        												 .setDisabled(false);
				        												 NS.cmdQuitar2
				        												 .setDisabled(false);
				        												 NS.cmdQuitarInt2
				        												 .setDisabled(false);
				        											 } else {
				        												 NS.optPeriodIntereses
				        												 .setDisabled(false);
				        												 NS.cmbDiaCorteInt
				        												 .setValue("");
				        												 NS.txtTotalCap
				        												 .setValue(BFwrk.Util
				        														 .formatNumber(pdTotalCap
				        																 .toFixed(2)));
				        												 NS.txtTotalInt
				        												 .setValue(BFwrk.Util
				        														 .formatNumber(pdTotalInt
				        																 .toFixed(2)));
				        												 NS.txtTotal
				        												 .setValue(BFwrk.Util
				        														 .formatNumber((pdTotalCap + pdTotalInt)
				        																 .toFixed(2)));
				        												 NS.txtIva
				        												 .setDisabled(false);
				        												 NS.cmbDiaCorteInt
				        												 .setDisabled(false);
				        												 NS.txtIva
				        												 .setDisabled(true);
				        												 NS.txtComentario
				        												 .setDisabled(false);
				        												 NS.chkDiasExacInt
				        												 .setDisabled(false);
				        												 NS.optDias
				        												 .setDisabled(false);
				        												 NS.chkPrimerPagoInt
				        												 .setDisabled(false);
				        												 NS.psInteresSind = "";
				        												 NS.psPorSind = "";
				        												 NS.psIdBanco = "";
				        												 NS.cmdQuitar
				        												 .setDisabled(false);
				        												 NS.cmdQuitar2
				        												 .setDisabled(false);
				        												 NS.cmdAgregar
				        												 .setDisabled(true);
				        												 NS.txtCapital.setDisabled(true);
				        												 NS.txtFecVenC
				        												 .setDisabled(true);
				        												 NS.soloCapital = true;
				        												 NS.cmdAgregarInt
				        												 .setDisabled(false);
				        												 NS.txtIva
				        												 .setValue("0.00");
				        											 }
				        										 }
				        									 });
				        								 }
				        							 }
				        						 });
				        					 }

				        				 }
				        			 });
				        			 // End If
				        		 }
				        	 },
				        	 hide : {
				        		 fn : function() {
				        			 NS.vbExistAmort = false;
				        			 NS.optPeriodCapital.setValue(0);
				        			 NS.cmbPeriodo.setValue("");
				        			 NS.cmbDiaCorte.setValue("");
				        			 NS.txtDiasC.setValue("");
				        			 NS.txtNoAmort.setValue("");
				        			 NS.vdCapital = 0;
				        			 NS.vdTotal = '';
				        			 NS.vsFechaIni = '';
				        			 NS.plWeekend = 0;
				        			 NS.pdFecha = '';
				        			 NS.psRecorre = '';
				        			 NS.plDiaInhabil = '';
				        			 NS.pdSaldoInsoluto = '';
				        			 NS.diaHabil = '';
				        			 NS.vsBisiesto = '';
				        			 NS.vlNoAmort = '';
				        			 NS.vdSaldoInsoluto = 0;
				        			 NS.oPeriodC = '0';
				        			 //
				        			 NS.chkPeriodoGracia.setValue(false);
				        			 NS.chkPeriodoGracia.setDisabled(true);
				        			 NS.editarAmortizacion(true);
				        			 NS.limpiarValores();
				        			 NS.pdSaldoInsoluto = '';
				        			 NS.txtFecVenC.focus();
				        			 NS.lblIvaC.setDisabled(false);
				        			 NS.txtIvaC.setDisabled(false);
				        			 NS.lblFactorCapital.setDisabled(false);
				        			 NS.txtFactorCapital.setDisabled(false);
				        			 //
				        			 NS.oPeriodI = '';
				        			 NS.storeDias.removeAll();
				        			 NS.vbPagado = false;
				        			 NS.txtFecVenC.setValue('');
				        			 NS.txtFecVenInt.setValue('');
				        			 NS.storeCmbPeriodo.removeAll();
				        			 NS.storeCmbPeriodo2.removeAll();
				        			 NS.pdTasa = '';
				        			 NS.pdSaldoInsoluto = '';
				        			 NS.vbExistAmort = false;
				        			 NS.storeSelectAmortizaciones.removeAll();
				        			 NS.storeSolicitudes.removeAll();
				        			 NS.storeSolicitudes2.removeAll();
				        			 NS.txtTotalCap.setValue('');
				        			 NS.txtTotalInt.setValue('');
				        			 NS.txtTotal.setValue('');
				        			 NS.txtDiasInt.setValue("");
				        			 NS.optPeriodIntereses.setValue(0);
				        			 NS.cmbPeriodoInt.setValue("");
				        			 NS.cmbDiaCorteInt.setValue("");
				        			 NS.txtNoAmortInt.setValue("");
				        			 NS.oPeriodI = '0';
				        		 }
				        	 }
				         }
			});
	NS.amortizacion = function() {

		NS.winAmortizacion.show();
	}
	NS.cmdAmortizaciones = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAmortizaciones',
		name : PF + 'cmdAmortizaciones',
		text : 'Amortización',
		disabled : true,
		x : 420,
		y : 255,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.amortizacion();

				}
			}
		}
	});
	NS.cmdRentas = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdRentas',
		name : PF + 'cmdRentas',
		text : 'Rentas',
		disabled : true,
		x : 420,
		y : 255,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
				}
			}
		}
	});
	// ////////////////////////AVALES Y GARANTÍAS
	NS.limpiaAG = function() {
		NS.txtEmpresaA.setValue("");
		NS.cmbEmpresaA.reset();
		NS.txtAvalGtia.setValue("");
		NS.cmbAvalGtia.reset();
		NS.txtDisponible.setValue("");
		NS.txtMontoAvalado.setValue(NS.txtMontoCredito.getValue());

	}
	NS.cancelarAG = function() {
		NS.limpiaAG();
		NS.editarAG(true);
		NS.cmdCancelar.setDisabled(true);
	}

	NS.lblNoLinea = new Ext.form.Label({
		text : 'No. Línea:',
		x : 0,
		y : 0,

	});
	NS.txtNoLinea = new Ext.form.TextField({
		id : PF + 'txtNoLinea',
		name : PF + 'txtNoLinea',
		value : '',
		x : 60,
		y : 0,
		disabled : true,
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.lblNoCredito = new Ext.form.Label({
		text : 'No. Crédito:',
		x : 240,
		y : 0,

	});
	NS.txtNoCredito = new Ext.form.TextField({
		id : PF + 'txtNoCredito',
		name : PF + 'txtNoCredito',
		value : '',
		x : 310,
		y : 0,
		disabled : true,
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {

				}
			}
		}
	});
	NS.lblMontoCredito = new Ext.form.Label({
		text : 'Monto Crédito:',
		x : 500,
		y : 0,

	});
	NS.txtMontoCredito = new Ext.form.TextField({
		id : PF + 'txtMontoCredito',
		name : PF + 'txtMontoCredito',
		value : '',
		x : 580,
		y : 0,
		disabled : true,
		width : 150,
		listeners : {
			change : {
				fn : function(caja, valor) {
					Ext.getCmp(PF + 'txtMontoCredito').setValue(
							BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	NS.lblTotMontoAsig = new Ext.form.Label({
		text : 'Total Monto Avalado:',
		x : 490,
		y : 350,

	});
	NS.txtTotMontoAsig = new Ext.form.TextField({
		id : PF + 'txtTotMontoAsig',
		name : PF + 'txtTotMontoAsig',
		value : '',
		x : 600,
		y : 350,

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
		text : 'Empresa Avalista:',
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
	NS.storeLlenarComboEmpresaAval = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {noEmpresa:0},
		paramOrder : ['noEmpresa'],
		directFn : AltaFinanciamientoAction.selectComboEmpresaAval,
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
	NS.cmbEmpresaA = new Ext.form.ComboBox(
			{
				store : NS.storeLlenarComboEmpresaAval,
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
				valueField : 'idStr',
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
							if (NS.cmbEmpresaA.getValue() != '') {
								NS.txtEmpresaA.setValue(NS.cmbEmpresaA
										.getValue());
								NS.txtAvalGtia.setValue();
								NS.cmbAvalGtia.reset();
								NS.storeComboAvalGtia
								.load({
									params : {
										piEmpresaAvalista : NS.txtEmpresaA
										.getValue(),
										psDivisa : NS.txtDivisa
										.getValue(),
										noEmpresa:parseInt(NS.txtEmpresa.getValue()),
									},
									callback : function(records) {

									}
								});
							} else {
								NS.txtEmpresaA.setValue("");
							}
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
			piEmpresaAvalista : '',
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
						fn : function(combo, valor) {
							var comboValue = BFwrk.Util
							.updateComboToTextField(PF
									+ 'txtAvalGtia',
									NS.cmbAvalGtia.getId());
							if (NS.cmbAvalGtia.getValue() != '') {
								AltaFinanciamientoAction
								.selectAvaladaGtia(
										parseInt(NS.txtEmpresaA
												.getValue()),
												NS.txtAvalGtia
												.getValue(),parseInt(NS.txtEmpresa.getValue()),
												function(resultado, e) {
											if (resultado != 0) {
												NS.txtDisponible
												.setValue(BFwrk.Util
														.formatNumber(resultado));
											} else {
												NS.txtDisponible
												.setValue("0.00");
											}
										});
							} else {
								NS.txtAvalGtia.setValue("");
							}
						}
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
		x : 600,
		y : 45
	});
	NS.txtMontoAvalado = new Ext.form.TextField(
			{
				id : PF + 'txtMontoAvalado',
				name : PF + 'txtMontoAvalado',
				value : '',
				x : 600,
				y : 60,
				disabled : true,
				width : 150,
				listeners : {
					change : {
						fn : function(caja, valor) {
							Ext
							.getCmp(PF + 'txtMontoAvalado')
							.setValue(
									BFwrk.Util
									.formatNumber(caja
											.getValue()));
						}
					},
					blur : {
						fn : function(caja, valor) {
							if (NS.txtMontoAvalado.getValue() == "")
								NS.txtMontoAvalado.setValue("0.00");
							if (parseFloat(BFwrk.Util
									.unformatNumber(NS.txtMontoAvalado
											.getValue())) > parseFloat(BFwrk.Util
													.unformatNumber((NS.txtDisponible
															.getValue())))) {
								Ext.Msg
								.alert(
										"SET",
										"El monto disponible de la garantía es insuficiente para cubrir el monto avalado");
								NS.txtMontoAvalado.setValue("0.00");
								NS.txtMontoAvalado.focus();
							}
							NS.txtMontoAvalado.setValue(BFwrk.Util
									.unformatNumber(NS.txtMontoAvalado
											.getValue()));
						}
					}
				}
			});
	NS.crearNuevoAG = function() {
		NS.editarAG(false);
		NS.limpiaAG();
		NS.cmdCancelarAG.setDisabled(false);
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
					NS.crearNuevoAG();

				}
			}
		}
	});
	NS.validaDatosAG = function() {
		NS.bandAG = false;
		if (NS.txtEmpresaA.getValue() == "") {
			Ext.Msg.alert('SET', 'Seleccione la empresa.');
			Ext.getCmp(PF + 'txtEmpresaA').focus();
			NS.bandAG = false;
			return;
		} else if (NS.txtAvalGtia.getValue() == "") {
			Ext.Msg.alert('SET', 'Seleccione un Aval o Garantía.');
			Ext.getCmp(PF + 'txtEmpresaA').focus();
			NS.bandAG = false;
			return;
		} else if (NS.txtMontoAvalado.getValue() == "") {
			Ext.Msg.alert('SET',
			'El monto avalado debe ser mayor que cero.');
			Ext.getCmp(PF + 'txtEmpresaA').focus();
			NS.bandAG = false;
			return;
		} else {
			NS.bandAG = true;
		}

	}
	NS.aceptarAG = function() {
		NS.validaDatosAG();
		var validaDatosAG = NS.bandAG;
		if (!validaDatosAG)
			return;
		AltaFinanciamientoAction
		.insertAvalGtiaLin(
				Ext.getCmp(PF + 'txtNoLinea').getValue(),
				parseInt(Ext.getCmp(PF + 'txtNoCredito')
						.getValue()),
						parseFloat(BFwrk.Util
								.unformatNumber(NS.txtMontoAvalado
										.getValue())),
										NS.txtEmpresaA.getValue(),
										NS.txtAvalGtia.getValue(),
										parseInt(NS.txtEmpresa.getValue()),
										function(resultado, e) {
					BFwrk.Util.msgWait('Terminado...', false);
					if (resultado > 0) {
						Ext.Msg.alert("SET",
						"Asignación Realizada");
						NS.cancelarAG();
						var myMask = new Ext.LoadMask(
								Ext.getBody(),
								{
									store : NS.storeMontoDispuestoAvalada,
									msg : "Cargando..."
								});
						NS.storeMontoDispuestoAvalada
						.load({
							params : {
								psFinanciamiento : NS.cmbContratos
								.getValue(),
								piDisposicion : parseInt(NS.cmbDisp
										.getValue()),
										noEmpresa : parseInt(NS.txtEmpresa.getValue())
							},
							callback : function(records) {
								if (records.length > 0) {
									vdValor = 0;
									for (var i = 0; i <= records.length - 1; i++) {
										vdValor = vdValor
										+ parseFloat(records[i]
										.get('montoDispuesto'));
									}
									NS.txtTotMontoAsig
									.setValue(BFwrk.Util
											.formatNumber(vdValor
													.toFixed(2)));
								} else {
									NS.txtTotMontoAsig
									.setValue("0.00");
								}
							}
						});
					} else {
						Ext.Msg
						.alert("SET",
						"Error en la Asignación consulte a sistemas.");
						return;
					}
				});
	}
	NS.cmdAceptarAG = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAceptarAG',
		name : PF + 'cmdAceptarAG',
		text : 'Aceptar',
		x : 490,

		y : 540,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.aceptarAG();
				}
			}
		}
	});
	NS.editarAG = function(valor) {
		NS.txtEmpresaA.setDisabled(valor);
		NS.cmbEmpresaA.setDisabled(valor);
		NS.txtAvalGtia.setDisabled(valor);
		NS.cmbAvalGtia.setDisabled(valor);
		NS.txtMontoAvalado.setDisabled(valor);
	}
	NS.cmdCancelarAG = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdCancelarAG',
		name : PF + 'cmdCancelarAG',
		text : 'Cancelar',
		x : 580,
		disabled : true,
		y : 540,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.cancelarAG();
				}
			}
		}
	});
	NS.eliminarAG = function() {
		if (NS.storeMontoDispuestoAvalada.getCount() < 1)
			return;
		var registros = NS.gridDatos4.getSelectionModel()
		.getSelections();
		var vdTotal = 0;
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
				'Se van a eliminar los registros seleccionados, ¿Desea continuar?',
				function(btn) {
					if (btn === 'yes') {
						var ag = NS.gridDatos4
						.getSelectionModel()
						.getSelections();
						var matrizAG = new Array();

						for (var i = 0; i < ag.length; i++) {
							var regAG = {};
							regAG.idClave = ag[i].get('clave');
							regAG.noEmpresaAvalistaAux = ag[i]
							.get('noEmpresaAvalistaAux');
							matrizAG[i] = regAG;
						}
						var jsonStringAG = Ext.util.JSON
						.encode(matrizAG);
						AltaFinanciamientoAction
						.existeAvalGtiaLinea(
								jsonStringAG,parseInt(NS.txtEmpresa.getValue()),
								function(resultado, e) {
									BFwrk.Util
									.msgWait(
											'Terminado...',
											false);
									if (resultado > 0) {
										NS.MB2
										.confirm(
												'SET',
												'Empresa con créditos vigentes, ¿desea continuar?',
												function(
														btn) {
													if (btn === 'yes') {
														var ag = NS.gridDatos4
														.getSelectionModel()
														.getSelections();
														var matrizAGEliminar = new Array();
														for (var i = 0; i < ag.length; i++) {
															var regAGEliminar = {};
															regAGEliminar.idClave = ag[i]
															.get('clave');
															regAGEliminar.noEmpresaAvalistaAux = ag[i]
															.get('noEmpresaAvalistaAux');
															matrizAGEliminar[i] = regAGEliminar;
														}
														var jsonStringAGEliminar = Ext.util.JSON
														.encode(matrizAGEliminar);
														AltaFinanciamientoAction
														.deleteLineaAvalada(
																jsonStringAGEliminar,
																NS.txtNoLinea
																.getValue(),
																parseInt(NS.txtNoCredito
																		.getValue()),parseInt(NS.txtEmpresa
																				.getValue()),
																				function(
																						resultado,
																						e) {
																	if (resultado > 0) {
																		Ext.Msg
																		.alert(
																				"SET",
																				"El registro se eliminó correctamente");
																	} else {
																		Ext.Msg
																		.alert(
																				"SET",
																		"El registro no se eliminó correctamente");
																	}
																	NS
																	.limpiaAG();
																	NS
																	.editarAG(true);
																	var myMask = new Ext.LoadMask(
																			Ext
																			.getBody(),
																			{
																				store : NS.storeMontoDispuestoAvalada,
																				msg : "Cargando..."
																			});
																	NS.storeMontoDispuestoAvalada
																	.load({
																		params : {
																			psFinanciamiento : NS.cmbContratos
																			.getValue(),
																			piDisposicion : parseInt(NS.cmbDisp
																					.getValue()),
																					noEmpresa : parseInt(NS.txtEmpresa.getValue())
																		},
																		callback : function(
																				records) {
																			if (records.length > 0) {
																				var vdValor = 0;
																				for (var i = 0; i <= records.length - 1; i++) {
																					vdValor = vdValor
																					+ parseFloat(records[i]
																					.get('montoDispuesto'));
																				}
																				NS.txtTotMontoAsig
																				.setValue(BFwrk.Util
																						.formatNumber(vdValor
																								.toFixed(2)));
																			} else {
																				NS.txtTotMontoAsig
																				.setValue("0.00");
																			}
																		}
																	});
																});
													}
													if (btn === 'no') {
														return;
													}
												});
									} else if (resultado == 0) {
										var ag = NS.gridDatos4
										.getSelectionModel()
										.getSelections();
										var matrizAGEliminar = new Array();
										for (var i = 0; i < ag.length; i++) {
											var regAGEliminar = {};
											regAGEliminar.idClave = ag[i]
											.get('clave');
											regAGEliminar.noEmpresaAvalistaAux = ag[i]
											.get('noEmpresaAvalistaAux');
											matrizAGEliminar[i] = regAGEliminar;
										}
										var jsonStringAGEliminar = Ext.util.JSON
										.encode(matrizAGEliminar);
										AltaFinanciamientoAction
										.deleteLineaAvalada(
												jsonStringAGEliminar,
												NS.txtNoLinea
												.getValue(),
												parseInt(NS.txtNoCredito
														.getValue()),
														function(
																resultado,
																e) {
													if (resultado > 0) {
														Ext.Msg
														.alert(
																"SET",
																"El registro se eliminó correctamente");
													} else {
														Ext.Msg
														.alert(
																"SET",
														"El registro no se eliminó correctamente");
													}
													NS
													.limpiaAG();
													NS
													.editarAG(true);
													var myMask = new Ext.LoadMask(
															Ext
															.getBody(),
															{
																store : NS.storeMontoDispuestoAvalada,
																msg : "Cargando..."
															});
													NS.storeMontoDispuestoAvalada
													.load({
														params : {
															psFinanciamiento : NS.cmbContratos
															.getValue(),
															piDisposicion : parseInt(NS.cmbDisp
																	.getValue()),
																	noEmpresa : parseInt(NS.txtEmpresa.getValue())
														},
														callback : function(
																records) {
															if (records.length > 0) {
																var vdValor = 0;
																for (var i = 0; i <= records.length - 1; i++) {
																	vdValor = vdValor
																	+ parseFloat(records[i]
																	.get('montoDispuesto'));
																}
																NS.txtTotMontoAsig
																.setValue(BFwrk.Util
																		.formatNumber(vdValor
																				.toFixed(2)));
															} else {
																NS.txtTotMontoAsig
																.setValue("0.00");
															}
														}
													});
												});
									} else {
										return;
									}
								});
					} else {
						return;
					}
				});
		// vdTotal+=registros[i].get('montoDispuesto');
		/*
		 * Set rsDatos = New ADODB.Recordset Set rsDatos =
		 * gobjSQL.FunSQLExisteAvalGtiaLinea(CInt(Val(.TextMatrix(i,
		 * MI_NO_EMPRESA))), Trim(.TextMatrix(i, MI_CLAVE)), _
		 * CInt(GI_ID_EMPRESA))
		 * 
		 * If Not rsDatos.EOF Then If MsgBox("Empresa con créditos
		 * vigentes, ¿desea continuar?", vbQuestion + vbYesNo, "SET") =
		 * vbNo Then Screen.MousePointer = 0 Exit Sub End If End If
		 * 
		 * vlResultado =
		 * gobjSQL.FunSQLDeleteLineaAvalada(CInt(Val(.TextMatrix(i,
		 * MI_NO_EMPRESA))), Trim(.TextMatrix(i, MI_CLAVE)), _
		 * CInt(GI_ID_EMPRESA), Trim(TxtNoLinea.Text),
		 * CInt(txtNoCredito.Text), _ GI_USUARIO, GT_FECHA_HOY)
		 * 
		 * If vlResultado < 1 Then MsgBox "El registro no se elimino
		 * correctamente", vbCritical, "SET" Screen.MousePointer = 0
		 * Exit Sub End If End If Next MsgBox "El registro se elimino
		 * correctamente!!", vbInformation, "SET" Call Limpia Call
		 * Editar(False) Call Muestra_Datos(GI_ID_EMPRESA,
		 * TxtNoLinea.Text, CInt(Val(txtNoCredito.Text))) End With
		 */
	}
	NS.cmdEliminarAG = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdEliminarAG',
		name : PF + 'cmdEliminarAG',
		text : 'Eliminar',
		x : 670,
		y : 540,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.eliminarAG();

				}
			}
		}
	});
	NS.storeMontoDispuestoAvalada = new Ext.data.DirectStore(
			{
				paramsAsHash : false,
				root : '',
				baseParams : {
					psFinanciamiento : '',
					piDisposicion : 0,
					noEmpresa:0
				},
				paramOrder : [ 'psFinanciamiento', 'piDisposicion','noEmpresa' ],
				directFn : AltaFinanciamientoAction.selectMontoDispuestoAvalada,
				fields : [ {
					name : 'clave'
				}, {
					name : 'idAvalGarantia'
				}, {
					name : 'avalGarantia'
				}, {
					name : 'noEmpresaAvalistaAux'
				}, {
					name : 'nomEmpresa'
				}, {
					name : 'descripcion'
				}, {
					name : 'fecFinal'
				}, {
					name : 'fecAlta'
				}, {
					name : 'montoDispuesto'
				}, ]

			});
	NS.smE4 = new Ext.grid.CheckboxSelectionModel({
		singleSelect : false
	});
	NS.agregarGridAval = function(clave, idAvalGarantia, fecPag,
			avalGarantia, noEmpresaAvalistaAux, nomEmpresa, descripcion,
			fecFinal, fecAlta, montoDispuesto) {
		var indice = 0;
		var recordsDatGrid = NS.storeMontoDispuestoAvalada.data.items;
		var tamGrid = indice <= 0 ? (NS.storeMontoDispuestoAvalada.data.items).length
				: indice;
		var datosClase = NS.gridDatos4.getStore().recordType;
		var datos = new datosClase({
			clave : clave,
			idAvalGarantia : idAvalGarantia,
			fecPag : fecPag,
			avalGarantia : avalGarantia,
			noEmpresaAvalistaAux : noEmpresaAvalistaAux,
			nomEmpresa : nomEmpresa,
			descripcion : descripcion,
			fecFinal : fecFinal,
			fecAlta : fecAlta,
			montoDispuesto : montoDispuesto
		});
		NS.gridDatos4.stopEditing();
		NS.storeMontoDispuestoAvalada.insert(tamGrid, datos);
	};
	NS.gridDatos4 = new Ext.grid.GridPanel({
		store : NS.storeMontoDispuestoAvalada,
		id : PF + 'gridDatos4',
		name : PF + 'gridDatos4',
		frame : true,
		autowidth : true,
		autoScroll : true,
		height : 300,
		x : 0,
		y : 40,
		cm : new Ext.grid.ColumnModel({
			defaults : {
				width : 120,
				value : true,
				sortable : true
			},
			columns : [ NS.smE4, {
				id : 'clave',
				header : 'Clave',
				width : 100,
				dataIndex : 'clave',
				direction : 'ASC'
			}, {
				id : 'idAvalGarantia',
				header : '',
				width : 0,
				dataIndex : 'idAvalGarantia',
				hidden : true,
				hideable : false,

			}, {
				id : 'avalGarantia',
				header : ' Tipo Garantía',
				width : 150,
				dataIndex : 'avalGarantia',

			}, {
				id : 'noEmpresaAvalistaAux',
				header : '',
				width : 0,
				dataIndex : 'noEmpresaAvalistaAux',
				hidden : true,
				hideable : false,

			}, {
				id : 'nomEmpresa',
				header : ' Empresa',
				width : 300,
				dataIndex : 'nomEmpresa',

			}, {
				id : 'descripcion',
				header : ' Descripción',
				width : 300,
				dataIndex : 'descripcion',

			}, {
				id : 'fecFinal',
				header : ' Vigencia Aval',
				width : 150,
				dataIndex : 'fecFinal',

			}, {
				id : 'fecAlta',
				header : ' Fecha asignación',
				width : 100,
				dataIndex : 'fecAlta',

			}, {
				id : 'montoDispuesto',
				header : ' Valor Aval/Garantía',
				width : 150,
				renderer : function(value) {
					return Ext.util.Format.number(value, '0,0.00');
				},
				dataIndex : 'montoDispuesto',

			},

			]
		}),
		viewConfig : {
			getRowClass : function(record, index) {
			}
		},
		sm : NS.smE4,
		listeners : {
			click : {
				fn : function(grid) {
					var registros = NS.gridDatos4.getSelectionModel()
					.getSelections();
					var vdTotal = 0;
					for (var i = 0; i < registros.length; i++) {
						vdTotal += registros[i].get('montoDispuesto');
					}
					NS.txtTotMontoAsig.setValue(BFwrk.Util
							.formatNumber(vdTotal.toFixed(2)));
				}
			},
		}
	});
	NS.winAvalGarantia = new Ext.Window(
			{
				title : 'Asignación de Avales y Garantías Línea (Créditos bancarios)',
				modal : true,
				shadow : true,
				closeAction : 'hide',
				width : 850,
				height : 500,
				layout : 'absolute',
				// plain : true,
				triggerAction : 'all',
				bodyStyle : 'padding:10px;',
				buttonAlign : 'center',
				draggable : false,
				resizable : false,
				autoScroll : true,
				items : [
				         {
				        	 xtype : 'fieldset',
				        	 title : '',
				        	 id : PF + 'panelGral',
				        	 name : PF + 'panelGral',
				        	 x : 0,
				        	 y : 0,
				        	 autowidth : true,
				        	 height : 590,
				        	 layout : 'absolute',
				        	 items : [
				        	          {
				        	        	  xtype : 'fieldset',
				        	        	  title : '',
				        	        	  id : PF + 'panelGridAvales',
				        	        	  name : PF + 'panelGridAvales',
				        	        	  x : 0,
				        	        	  y : 0,
				        	        	  autowidth : true,
				        	        	  height : 400,
				        	        	  layout : 'absolute',
				        	        	  items : [ NS.lblNoLinea,
				        	        	            NS.txtNoLinea,
				        	        	            NS.lblNoCredito,
				        	        	            NS.txtNoCredito,
				        	        	            NS.lblMontoCredito,
				        	        	            NS.txtMontoCredito,
				        	        	            NS.gridDatos4,
				        	        	            NS.lblTotMontoAsig,
				        	        	            NS.txtTotMontoAsig ]
				        	          },
				        	          {
				        	        	  xtype : 'fieldset',
				        	        	  title : '',
				        	        	  id : PF + 'panelAvales',
				        	        	  name : PF + 'panelAvales',
				        	        	  x : 0,
				        	        	  y : 410,
				        	        	  autowidth : true,
				        	        	  height : 110,
				        	        	  layout : 'absolute',
				        	        	  items : [ NS.lblEmpresaA,
				        	        	            NS.txtEmpresaA,
				        	        	            NS.cmbEmpresaA,
				        	        	            NS.lblAvalGtia,
				        	        	            NS.txtAvalGtia,
				        	        	            NS.cmbAvalGtia,
				        	        	            NS.lblDisponible,
				        	        	            NS.txtDisponible,
				        	        	            NS.lblMontoAvalado,
				        	        	            NS.txtMontoAvalado,
				        	        	            NS.cmdCrearNvo ]
				        	          }, NS.cmdAceptarAG,
				        	          NS.cmdCancelarAG, NS.cmdEliminarAG

				        	          ],
				         }, ],
				         listeners : {
				        	 show : {
				        		 fn : function() {
				        			 var vdValor = 0;
				        			 NS.txtNoLinea.setValue(NS.cmbContratos
				        					 .getValue());
				        			 NS.txtNoCredito.setValue(NS.cmbDisp
				        					 .getValue());
				        			 NS.txtMontoCredito
				        			 .setValue(NS.txtMontoDisposicion
				        					 .getValue());
				        			 NS.txtMontoAvalado
				        			 .setValue(NS.txtMontoDisposicion
				        					 .getValue());
				        			 var myMask = new Ext.LoadMask(
				        					 Ext.getBody(),
				        					 {
				        						 store : NS.storeLlenarComboEmpresaAval,
				        						 msg : "Cargando..."
				        					 });
				        			 NS.storeLlenarComboEmpresaAval.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
				        			 NS.storeLlenarComboEmpresaAval.load();
				        			 var myMask = new Ext.LoadMask(
				        					 Ext.getBody(),
				        					 {
				        						 store : NS.storeMontoDispuestoAvalada,
				        						 msg : "Cargando..."
				        					 });
				        			 NS.storeMontoDispuestoAvalada
				        			 .load({
				        				 params : {
				        					 psFinanciamiento : NS.cmbContratos
				        					 .getValue(),
				        					 piDisposicion : parseInt(NS.cmbDisp
				        							 .getValue()),
				        							 noEmpresa : parseInt(NS.txtEmpresa.getValue())
				        				 },
				        				 callback : function(records) {
				        					 if (records.length > 0) {
				        						 for (var i = 0; i <= records.length - 1; i++) {
				        							 vdValor = vdValor
				        							 + parseFloat(records[i]
				        							 .get('montoDispuesto'));
				        						 }
				        						 NS.txtTotMontoAsig
				        						 .setValue(BFwrk.Util
				        								 .formatNumber(vdValor
				        										 .toFixed(2)));
				        					 }
				        				 }
				        			 });
				        		 }
				        	 },
				        	 hide : {
				        		 fn : function() {
				        			 NS.txtNoLinea.setValue('');
				        			 NS.txtNoCredito.setValue('');
				        			 NS.txtMontoCredito.setValue('');
				        			 NS.txtMontoAvalado.setValue('');
				        			 NS.txtTotMontoAsig.setValue('');
				        			 NS.txtEmpresaA.setValue('');
				        			 NS.cmbEmpresaA.setValue('');
				        			 NS.txtAvalGtia.setValue('');
				        			 NS.cmbAvalGtia.setValue('');
				        			 NS.txtDisponible.setValue('');
				        			 NS.storeMontoDispuestoAvalada.removeAll();
				        		 }
				        	 }
				         }
			});
	NS.avalGarantia = function() {
		if (NS.cmbContratos.getValue() == ""
			|| NS.cmbContratos.getValue() == null)
			Ext.Msg
			.alert("SET",
			"Debe Selecionar una Línea de Crédito");
		else
			NS.winAvalGarantia.show();
	}

	NS.cmdAvalGarantia = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdAvalGarantia',
		name : PF + 'cmdAvalGarantia',
		text : 'Aval / Garantía',
		disabled : true,
		x : 510,
		y : 255,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.avalGarantia();
				}
			}
		}
	});

	NS.cmdRenovacion = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdRenovacion',
		name : PF + 'cmdRenovacion',
		text : 'Renovación',
		disabled : true,
		x : 600,
		y : 255,
		width : 80,
		listeners : {
			click : {}
		}
	});

	NS.cmdDetalle = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdDetalle',
		name : PF + 'cmdDetalle',
		text : 'Detalle',
		x : 600,
		y : 255,
		width : 80,
		listeners : {}
	});
	NS.cmdContabiliza = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdContabiliza',
		name : PF + 'cmdContabiliza',
		text : 'Contabiliza',
		disabled : true,
		hidden : true,
		x : 690,
		y : 255,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					/*
					 * En VB, tenía el código comentado: Private Sub
					 * cmdContabiliza_Click() On Error GoTo HayError
					 * 'MS: esta funcion no esta en el modulo de cie, se
					 * comenta 16/04/2015 'Call
					 * gobjSQL.FunSQLUpdateMovimiento3120 Exit Sub
					 * HayError: Screen.MousePointer = 0 BitacoraError
					 * GI_USUARIO, "NetroCreditoBanSITE", Me.Name,
					 * "cmdContabiliza_Click", Err.Number,
					 * Err.Description End Sub
					 */
				}
			}
		}
	});
	NS.bitacoraDisp = function() {
		NS.pbBitLinea = false;
		NS.winBitacora.show();
		Ext.getCmp(PF + 'txtNota').setValue("");
		Ext.getCmp(PF + 'txtFinanciamiento').setValue("");
	}
	NS.cmdBitacoraDisp = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdBitacoraDisp',
		name : PF + 'cmdBitacoraDisp',
		text : 'Bitácora',
		disabled : true,
		x : 780,
		y : 255,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					NS.bitacoraDisp();
				}
			}
		}
	});
	NS.cmdFacturas = new Ext.Button({
		xtype : 'button',
		id : PF + 'cmdFacturas',
		name : PF + 'cmdFacturas',
		text : 'Facturas',
		disabled : true,
		hidden : true,
		x : 870,
		y : 255,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
				}
			}
		}
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
	// *********************************************

	// Contenedor
	NS.contenedorAltaFinanciamiento = new Ext.FormPanel({
		title : 'Alta Financiamiento (Créditos Bancarios)',
		autowidth : true,
		height : 1500,
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
			height : 770,
			autowidth : true,
			items : [
			         {
			        	 xtype : 'fieldset',
			        	 title : '',
			        	 id : PF + 'panelEmpresa',
			        	 name : PF + 'panelEmpresa',
			        	 x : 0,
			        	 y : 0,
			        	 autowidth : true,
			        	 height : 44,
			        	 layout : 'absolute',
			        	 items : [ NS.lblEmpresa,NS.cmbEmpresa, NS.txtEmpresa,
			        	           NS.lblUsuario, NS.txtUsuario ]
			         },
			         {
			        	 xtype : 'fieldset',
			        	 title : 'Línea',
			        	 id : PF + 'panelLinea',
			        	 name : PF + 'panelLinea',
			        	 x : 0,
			        	 y : 45,
			        	 autowidth : true,
			        	 height : 320,
			        	 layout : 'absolute',
			        	 items : [ NS.lblLinea, NS.txtLinea,
			        	           NS.chkLargoPlazo, NS.lblLargoPlazo1,
			        	           NS.lblLargoPlazo2, NS.chkReestructura,
			        	           NS.lblReestructura, NS.lblLinea2,
			        	           NS.cmbContratos, NS.cmdCrearNuevo,
			        	           NS.lblPais, NS.cmbPais, NS.txtNoPais,
			        	           NS.lblTipoContrato, NS.txtTipoContrato,
			        	           NS.cmbTipoContrato, NS.lblFideicomiso,
			        	           NS.txtFideicomiso, NS.cmbFideicomiso,
			        	           NS.lblBanco, NS.txtBanco, NS.cmbBanco,
			        	           NS.lblNoCuenta, NS.cmbNoCuenta,
			        	           NS.lblAgente, NS.txtAgente,
			        	           NS.cmbAgente, NS.lblBancoLin,
			        	           NS.txtBancoLin, NS.cmbBancoLin,
			        	           NS.lblFechaIni, NS.txtFechaIni,
			        	           NS.lblFechaFin, NS.txtFechaFin,
			        	           NS.lblDivisa, NS.txtDivisa,
			        	           NS.cmbDivisa, NS.lblTasa, NS.cmbTasa,
			        	           NS.lblMontoAutorizado,
			        	           NS.txtMontoAutorizado,
			        	           NS.lblMontoDispuesto,
			        	           NS.txtMontoDispuesto,
			        	           NS.lblMontoDisponible,
			        	           NS.txtMontoDisponible,
			        	           NS.lblValorizacion, NS.txtValorizacion,
			        	           NS.lblSpreed, NS.txtSpreed,
			        	           NS.lblRevolvencia, NS.chkRevolvencia, {
			        		 xtype : 'fieldset',
			        		 title : 'Inhábil Cálc. Int.',
			        		 id : 'fraCalculoInt',
			        		 x : 715,
			        		 y : 120,
			        		 hidden : true,
			        		 width : 120,
			        		 height : 100,
			        		 layout : 'absolute',
			        		 items : [ NS.optCalculoInt ]
			        	 }, {
			        		 xtype : 'fieldset',
			        		 title : 'Inhábil Pago',
			        		 id : 'fraPagoInt',
			        		 x : 840,
			        		 y : 120,
			        		 hidden : true,
			        		 width : 110,
			        		 height : 100,
			        		 layout : 'absolute',
			        		 items : [ NS.optPagoInt ]
			        	 }, NS.cmdCreditoSindicado, NS.cmdHacer,
			        	 NS.lblDisp, NS.cmbDisp, NS.cmdBitacora,
			        	 NS.cmdDisposicion, NS.cmdAceptar,
			        	 NS.cmdEliminar, NS.cmdModificar,
			        	 NS.cmdImprimir, NS.cmdLimpiar ]
			         },
			         {
			        	 xtype : 'fieldset',
			        	 title : 'Disposición',
			        	 id : PF + 'panelDisposicion',
			        	 name : PF + 'panelDisposicion',
			        	 x : 0,
			        	 y : 365,
			        	 autowidth : true,
			        	 height : 345,
			        	 layout : 'absolute',
			        	 items : [
			        	          NS.lblLinea3,
			        	          NS.cmbFinanciamiento,
			        	          NS.lblTipoFinanDis,
			        	          NS.txtTipoFinanDis,
			        	          NS.cmbTipoFinanDis,
			        	          NS.lblDivisaD,
			        	          NS.txtDivisaD,
			        	          NS.cmbDivisaD,
			        	          NS.txtMontoDisposicion,
			        	          NS.lblMontoDisposicion,
			        	          NS.lblFecDisp,
			        	          NS.txtFecDisp,
			        	          NS.lblDias,
			        	          NS.txtDias,
			        	          NS.lblFecVenDisp,
			        	          NS.txtFecVenDisp,
			        	          NS.lblBancoDis,
			        	          NS.txtBancoDis,
			        	          NS.cmbBancoDis,
			        	          NS.lblChequeraDis,
			        	          NS.cmbChequeraDis,
			        	          NS.lblEquivalencia,
			        	          NS.txtEquivalencia,
			        	          NS.cmbEquivalencia,
			        	          {
			        	        	  xtype : 'fieldset',
			        	        	  title : 'Forma pago',
			        	        	  x : 590,
			        	        	  y : 38,
			        	        	  width : 230,
			        	        	  height : 60,
			        	        	  layout : 'absolute',
			        	        	  items : [ NS.optFormaPago ]
			        	          },
			        	          {
			        	        	  xtype : 'fieldset',
			        	        	  title : 'Tasas',
			        	        	  x : 0,
			        	        	  y : 100,
			        	        	  width : 370,
			        	        	  height : 150,
			        	        	  layout : 'absolute',
			        	        	  items : [ NS.optTasa,
			        	        	            NS.lblTasaBase,
			        	        	            NS.cmbTasaBase, NS.lblBase,
			        	        	            NS.txtBase, NS.lblPuntos,
			        	        	            NS.lblTasaVigente,
			        	        	            NS.lblTasaFija,
			        	        	            NS.txtPuntos,
			        	        	            NS.txtTasaVigente,
			        	        	            NS.txtTasaFija ]
			        	          }, {
			        	        	  xtype : 'fieldset',
			        	        	  title : 'WHTax',
			        	        	  id : 'frmWHTax',
			        	        	  x : 380,
			        	        	  y : 100,
			        	        	  width : 125,
			        	        	  height : 60,

			        	        	  hidden : true,
			        	        	  layout : 'absolute',
			        	        	  items : [ NS.txtWhtax ]
			        	          }, NS.lblTasaMora, NS.txtTasaMora,
			        	          NS.lblMontoMora, NS.txtMontoMora,
			        	          NS.lblComentarios, NS.txtComentarios,
			        	          NS.cmdAceptarDis, NS.cmdCancelar,
			        	          NS.cmdModificar1, NS.cmdEliminar1,
			        	          NS.cmdImprimir1, NS.cmdAmortizaciones,
			        	          NS.cmdAvalGarantia, NS.cmdContabiliza,
			        	          NS.cmdBitacoraDisp, NS.cmdFacturas, ]
			         }, ]
		} ]
	});
	NS.contenedorAltaFinanciamiento.setSize(Ext.get(NS.tabContId)
			.getWidth(), Ext.get(NS.tabContId).getHeight());
});