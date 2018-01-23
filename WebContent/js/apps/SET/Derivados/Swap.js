/**
 * Eric Cesar Guzmán Malvaez
 */

Ext.onReady(function() {
			var NS = Ext.namespace('apps.SET.Derivados.Swap');
			NS.tabContId = apps.SET.tabContainerId;
			var PF = apps.SET.tabID + '.';

			NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
			NS.empresa = NS.GI_ID_EMPRESA;

			Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
			Ext.QuickTips.init();
			NS.idUsuario = apps.SET.iUserId;
			NS.obj;
			NS.fecHoy = apps.SET.FEC_HOY;
			NS.fechaIni = '';
			NS.fechaFin = NS.fecHoy;
			
//			NS.cargosMn = [];
//			NS.cargosEur = [];
//			NS.cargosDls = [];
//			
//			NS.abonosMn = [];
//			NS.abonosEur = [];
//			NS.abonosDls = [];
			NS.data = [];

			/*******************************************************************
			 * COMPONENTES *
			 ******************************************************************/
			// NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Procesando
			// ..."})
			NS.lbFecInicio = new Ext.form.Label({
				id : PF + 'lbFecInicio',
				text : 'Inicio: ',
				x : 230,
				y : 0
			});

			NS.lbFecFin = new Ext.form.Label({
				id : PF + 'lbFecFin',
				text : 'Fin: ',
				x : 360,
				y : 0
			});

			NS.txtFechaIni = new Ext.form.DateField(
					{
						name : PF + 'txtFechaIni',
						id : PF + 'txtFechaIni',
						x : 230,
						y : 20,
						width : 95,
						value : NS.fecHoy,
						format : 'd/m/Y',
						enableKeyEvents : true,
						listeners : {
							change : {
								fn : function(caja, e) {
									NS.fechaIni = cambiarFecha(caja.getValue()
											+ '')
											+ '';
									var fechaFin = Ext.getCmp(
											PF + 'txtFechaFin').getValue();
									if (fechaFin < caja.getValue()
											&& NS.fechaFin != '') {
										Ext.Msg
												.alert('SET',
														'La fecha inicial no puede ser mayor a la fecha final!!');
										Ext.getCmp(PF + 'txtFechaFin')
												.setValue('');
									}
								}
							}
						}
					});

			NS.txtFechaFin = new Ext.form.DateField(
					{
						format : 'd/m/Y',
						id : PF + 'txtFechaFin',
						name : PF + 'txtFechaFin',
						value : NS.fecHoy,
						x : 360,
						y : 20,
						width : 95,
						enableKeyEvents : true,
						listeners : {
							change : {
								fn : function(caja, e) {
									NS.fechaFin = cambiarFecha(caja.getValue()
											+ '')
											+ '';
									var fechaIni = Ext.getCmp(
											PF + 'txtFechaIni').getValue();
									if (fechaIni > caja.getValue()
											&& NS.fechaIni != '') {
										Ext.Msg
												.alert('SET',
														'La fecha inicial no puede ser mayor a la fecha final!!');
										Ext.getCmp(PF + 'txtFechaIni')
												.setValue('');
									}
								}
							}
						}
					});

			NS.txtEmpresa = new Ext.form.TextField({
				id : PF + 'txtEmpresa',
				name : PF + 'txtEmpresa',
				x : 90,
				y : 0,
				width : 260,

				value : apps.SET.NOM_EMPRESA
			});

			NS.tipo = new Ext.form.TextField({

				id : PF + 'tipo ',
				name : PF + 'tipo ',
				value : '',
				x : 120,
				y : 60,
				width : 430,
				emptyText : '',
				value : 1

			});

			NS.optFechas = new Ext.form.RadioGroup({
				id : PF + 'optFechas',
				name : PF + 'optFechas',
				columns : 2,
				items : [ {
					boxLabel : 'Compra',
					name : 'optSeleccion',
					inputValue : 0,
					checked : true,
					tabIndex : 3,
					listeners : {
						check : {
							fn : function(opt, valor) {
								if (valor == true) {
									parseInt(NS.tipo.setValue(1));
									// alert(NS.tipo.getValue());

								}
							}
						}
					}
				}, {
					boxLabel : 'Vencimiento',
					name : 'optSeleccion',
					inputValue : 1,
					tabIndex : 4,
					listeners : {
						check : {
							fn : function(opt, valor) {
								if (valor == true) {
									parseInt(NS.tipo.setValue(2));
									// alert(NS.tipo.getValue());

								}
							}
						}
					}
				} ]
			});
			
			
		
	       

					NS.storeLlenarNeteo = new Ext.data.DirectStore({
						paramsAsHash : true,
						root : '',
						baseParams : {
							json : ''
						},
						paramOrder : [ 'json' ],
						fields : [
						{
							name : 'cargosDls'
						}, {
							name : 'abonosDls'
						}, {
							name : 'cargosMn'
						}, {
							name : 'abonosMn'
						}, {
							name : 'cargosEur'
						}, {
							name : 'abonosEur'
						},{
							name : 'fechaVto'
						},{
							name : 'bancoDls'
						},{
							name : 'bancoMn'
						},{
							name : 'bancoEur'
						},{
							name : 'neteo'
						}
						],

						listeners : {
							load : function(s, records) {
								
							}
						}

					});
					NS.columnasNeteo = new Ext.grid.ColumnModel([
			           
			           {
			        	   header : 'Dólares_Cargo',
			        	   width : 100,
			        	   dataIndex : 'cargosDls',
			        	   sortable : true,
//			        	   
			        	   align:'right'
			           }, {
			        	   header : 'Dólares_Abono',
			        	   width : 100,
			        	   dataIndex : 'abonosDls',
			        	   sortable : true,
			        	   
			        	   align:'right'
			           }, {
			        	   header : 'Pesos_Cargo',
			        	   width : 100,
			        	   dataIndex : 'cargosMn',
			        	   sortable : true,
			        	   
			        	   align:'right'
			           }, {
			        	   header : 'Pesos_Abonos',
			        	   width : 100,
			        	   dataIndex : 'abonosMn',
			        	   sortable : true,
			        	   
			        	   align:'right'
			           }, {
			        	   header : 'Euros_Cargos',
			        	   width : 100,
			        	   dataIndex : 'cargosEur',
			        	   sortable : true,
			        	   
			        	   align:'right'
			           }, {
			        	   header : 'Euros_Abonos',
			        	   width : 100,
			        	   dataIndex : 'abonosEur',
			        	   sortable : true,
			        	   align:'right'
			           },{
			        	   header : 'BancoDls',
			        	   width : 100,
			        	   dataIndex : 'bancoDls',
			        	   sortable : true,
			        	   align:'right'
			           },{
			        	   header : 'BancoMn',
			        	   width : 100,
			        	   dataIndex : 'bancoMn',
			        	   sortable : true,
			        	   align:'right'
			           },{
			        	   header : 'BancoEur',
			        	   width : 100,
			        	   dataIndex : 'bancoEur',
			        	   sortable : true,
			        	   align:'right'
			           },{
			        	   header : 'Fecha Vencimiento',
			        	   width : 100,
			        	   dataIndex : 'fechaVto',
			        	   sortable : true,
			        	   align:'right'
			           },{
			        	   header : 'Neteo',
			        	   width : 120,
			        	   dataIndex : 'neteo',
			        	   sortable : true,
			        	   align:'right'
			           }
			           
			           ]);                                                   

					NS.gridNeteo = new Ext.grid.GridPanel({
							store : NS.storeLlenarNeteo,
							id : 'gridNeteo',
							width: 500,
							
							height : 300,
							stripeRows : true,
//							hidden: true,
							columnLines : true,
							cm : NS.columnasNeteo,
							listeners : {
								rowclick : {
									fn : function(grid, record, e) {

										var records = NS.gridNeteo.getSelectionModel().getSelections();

										var id_bancoDls = '';
										var id_bancoMn = '';
										var id_bancoEur = '';
										
										var cargosDls = 0;
										var abonosDls = 0;
										var cargosMn = 0;
										var abonosMn = 0;
										var cargosEur = 0;
										var abonosEur = 0;
										
										var neteo = '';
										var fechaVto = '';
										for (var i = 0; i < records.length; i++) {
											
											id_bancoDls = records[i].get('bancoDls');
											id_bancoMn = records[i].get('bancoMn');
											id_bancoEur = records[i].get('bancoEur');
											
											cargosDls = records[i].get('cargosDls');
											abonosDls = records[i].get('abonosDls');
											
											cargosMn = records[i].get('cargosMn');
											abonosMn = records[i].get('abonosMn');
											
											cargosEur = records[i].get('cargosEur');
											abonosEur = records[i].get('abonosEur');
											
											
											neteo = records[i].get('neteo');
											fechaVto = records[i].get('fechaVto');
										}
										
										
										ForwardsAction.consultaBanco(id_bancoDls,function(result,e){
											Ext.getCmp(PF + 'txtBancoDls').setValue(result);
										});
										
										
										ForwardsAction.consultaBanco(id_bancoMn,function(result,e){
											Ext.getCmp(PF + 'txtBancoMn').setValue(result);
										});
										
										ForwardsAction.consultaBanco(id_bancoEur,function(result,e){
											Ext.getCmp(PF + 'txtBancoEur').setValue(result);
										});
										
//										alert(id_bancoMn + "  -Eric");
									//	llenar caja dls
										if (cargosDls == 0) 
											Ext.getCmp(PF + 'txtPagaDls').setValue(abonosDls);
										else
											Ext.getCmp(PF + 'txtPagaDls').setValue(cargosDls);
										
										//llenar caja Mn	
										if (cargosMn == 0) 
											Ext.getCmp(PF + 'txtPagaMn').setValue(abonosMn);
										else
											Ext.getCmp(PF + 'txtPagaMn').setValue(cargosMn);

										//llenar caja Eur
										if (cargosEur == 0) 
											Ext.getCmp(PF + 'txtPagaEur').setValue(abonosEur);
										else
											Ext.getCmp(PF + 'txtPagaEur').setValue(cargosEur);
										
										Ext.getCmp(PF + 'txtNeteo').setValue(neteo);
										Ext.getCmp(PF + 'txtFechaVto').setValue(fechaVto);
										

									}
								}
							
							}
						});

			
			
			
			
			
			
			
			
			
			
			
			NS.columnaSeleccionDer2 = new Ext.grid.CheckboxSelectionModel({
				singleSelect : false
			});
			NS.columnaSeleccionDer = new Ext.grid.CheckboxSelectionModel({
				singleSelect : false
			});

			NS.columnasDerivados = new Ext.grid.ColumnModel([
					NS.columnaSeleccionDer, {
						header : 'FOLIO',
						width : 50,
						dataIndex : 'NO_FOLIO',
						sortable : true
					}, {
						header : 'No. Empresa',
						width : 100,
						dataIndex : 'NO_EMPRESA',
						sortable : true
					}, {
						header : 'Divisa Venta',
						width : 130,
						dataIndex : 'ID_DIVISA_VENTA',
						sortable : true
					}, {
						header : 'Banco Cargo',
						width : 120,
						dataIndex : 'ID_BANCO_CARGO',
						sortable : true
					}, {
						header : 'Chequera Cargo',
						width : 120,
						dataIndex : 'ID_CHEQUERA_CARGO',
						sortable : true
					}, {
						header : 'Divisa Compra',
						width : 120,
						dataIndex : 'ID_DIVISA_COMPRA',
						sortable : true
					}, {
						header : 'Banco Abono',
						width : 120,
						dataIndex : 'ID_BANCO_ABONO',
						sortable : true
					}, {
						header : 'Chequera Abono',
						width : 120,
						dataIndex : 'ID_CHEQUERA_ABONO',
						sortable : true
					}, {
						header : 'Forma Pago',
						width : 120,
						dataIndex : 'ID_FORMA_PAGO',
						sortable : true
					}, {
						header : 'Importe Pago',
						width : 120,
						dataIndex : 'IMPORTE_PAGO',
						sortable : true
					}, {
						header : 'Importe Compra',
						width : 120,
						dataIndex : 'IMPORTE_FWS',
						sortable : true
					}, {
						header : 'Tipo Cambio',
						width : 120,
						dataIndex : 'TIPO_CAMBIO',
						sortable : true
					}, {
						header : 'Fecha Alta',
						width : 120,
						dataIndex : 'FEC_ALTA',
						sortable : true
					}, {
						header : 'Fecha Vencimiento',
						width : 120,
						dataIndex : 'FEC_VENC',
						sortable : true
					}, {
						header : 'Institucion',
						width : 120,
						dataIndex : 'NO_INSTITUCION',
						sortable : true
					}, {
						header : 'Nombre Contacto',
						width : 120,
						dataIndex : 'NOM_CONTACTO',
						sortable : true
					}, {
						header : 'Banco Beneficiario',
						width : 120,
						dataIndex : 'ID_BANCO_BENEF',
						sortable : true
					}, {
						header : 'Chequera Beneficiaria',
						width : 120,
						dataIndex : 'ID_CHEQUERA_BENEF',
						sortable : true
					}, {
						header : 'Rubro Cargo',
						width : 120,
						dataIndex : 'RUBRO_CGO',
						sortable : true
					}, {
						header : 'SubRubro Cargo',
						width : 120,
						dataIndex : 'SUBRUBRO_CGO',
						sortable : true
					}, {
						header : 'Rubro Abono',
						width : 120,
						dataIndex : 'RUBRO_ABN',
						sortable : true
					}, {
						header : 'SubRubro Abono',
						width : 120,
						dataIndex : 'SUBRUBRO_ABN',
						sortable : true
					}, {
						header : 'Status Movimiento',
						width : 120,
						dataIndex : 'ESTATUS_MOV',
						sortable : false
					}, {
						header : 'Status_Importe',
						width : 120,
						dataIndex : 'ESTATUS_IMP',
						sortable : false
					}, {
						header : 'Firmanete 1',
						width : 120,
						dataIndex : 'FIRMANTE_1',
						sortable : true
					}, {
						header : 'Firmante 2',
						width : 120,
						dataIndex : 'FIRMANTE_2',
						sortable : true
					}, {
						header : 'No. Documento',
						width : 120,
						dataIndex : 'NO_DOCTO',
						sortable : true
					}, {
						header : 'SPOT',
						width : 120,
						dataIndex : 'SPOT',
						sortable : true
					}, {
						header : 'Puntos_Forward',
						width : 120,
						dataIndex : 'PUNTOS_FORWARD',
						sortable : true
					},
					{
						header : 'Referencia',
						width : 120,
						dataIndex : 'REFERENCIA',
						sortable : true
					},
					{
						header : 'Concepto',
						width : 120,
						dataIndex : 'CONCEPTO',
						sortable : true
					},{
						header : 'ESTATUS_SWAP',
						width : 120,
						dataIndex : 'ESTATUS_SWAP',
						sortable : true
					},
					{
						header : 'ID_NETEO',
						width : 120,
						dataIndex : 'ID_NETEO',
						sortable : true
					}


			]);

			NS.storeForwards = new Ext.data.DirectStore(
					{
						paramsAsHash : false,
						baseParams : {
							foco : false,
							tipo : 1,
							fec_A : "",
							fec_V : "",
							noEm : 0
						},
						root : '',
						paramOrder : [ 'foco', 'tipo', 'fec_A', 'fec_V', 'noEm' ],
						// directFn: MantenimientosAction.buscaFirmantes,
						directFn : ForwardsAction.llenarGrid,
						fields : [ {
							name : 'NO_FOLIO'
						}, {
							name : 'NO_EMPRESA'
						}, {
							name : 'ID_DIVISA_VENTA'
						}, {
							name : 'ID_BANCO_CARGO'
						}, {
							name : 'ID_CHEQUERA_CARGO'
						}, {
							name : 'ID_DIVISA_COMPRA'
						}, {
							name : 'ID_BANCO_ABONO'
						}, {
							name : 'ID_CHEQUERA_ABONO'
						}, {
							name : 'ID_FORMA_PAGO'
						}, {
							name : 'IMPORTE_PAGO'
						}, {
							name : 'IMPORTE_FWS'
						}, {
							name : 'TIPO_CAMBIO'
						}, {
							name : 'FEC_ALTA'
						}, {
							name : 'FEC_VENC'
						}, {
							name : 'NO_INSTITUCION'
						}, {
							name : 'NOM_CONTACTO'
						}, {
							name : 'ID_BANCO_BENEF'
						}, {
							name : 'ID_CHEQUERA_BENEF'
						}, {
							name : 'RUBRO_CGO'
						}, {
							name : 'SUBRUBRO_CGO'
						}, {
							name : 'RUBRO_ABN'
						}, {
							name : 'SUBRUBRO_ABN'
						}, {
							name : 'ESTATUS_MOV'
						}, {
							name : 'ESTATUS_IMP'
						}, {
							name : 'FIRMANTE_1'
						}, {
							name : 'FIRMANTE_2'
						}, {
							name : 'NO_DOCTO'
						}, {
							name : 'SPOT'
						}, {
							name : 'PUNTOS_FORWARD'
						},
						{
							name : 'REFERENCIA'
						},
						{
							name : 'CONCEPTO'
						},
						{
							name : 'ESTATUS_SWAP'
						},{
							name : 'ID_NETEO'
						}

						],
						listeners : {
							load : function(s, records) {
//								alert("cargando records4");
//								NS.storeLlenarGridatosExcel.baseParams.json = Ext.util.JSON.encode(records);
								
//								alert();
								
								var myMask = new Ext.LoadMask(Ext.getBody(), {
									store : NS.storeForwards,
									msg : "Cargando..."
								});
								
								
								
								
								if (records.length == null|| records.length <= 0) {
									
									Ext.Msg.alert('SET','No existen Datos para esta operacion');
									myMask.hide();
								}
								
							}
						},
						exception : function(misc) {
							// NS.myMask.hide();
							Ext.Msg
									.alert('SET',
											'Error al cargar los datos en el grid, <br> Verificar la conexión');
						}
					});
			// NS.myMask.show();
			// NS.storeForwards.load();

			NS.gridDerivados = new Ext.grid.GridPanel({
				store : NS.storeForwards,
				id : 'gridDerivados',
				// width: 1025,
				height : 213,
				stripeRows : true,
				columnLines : true,
				cm : NS.columnasDerivados,
				sm : NS.columnaSeleccionDer,
			});

			NS.storeLlenarGriDatosImp = new Ext.data.DirectStore({
				paramsAsHash : true,
				root : '',
				baseParams : {
					json : ''
				},
				paramOrder : [ 'json' ],
				fields : [
				{
					name : 'NO_FOLIO'
				}, {
					name : 'NO_EMPRESA'
				}, {
					name : 'ID_DIVISA_VENTA'
				}, {
					name : 'ID_BANCO_CARGO'
				}, {
					name : 'ID_CHEQUERA_CARGO'
				}, {
					name : 'ID_DIVISA_COMPRA'
				}, {
					name : 'ID_BANCO_ABONO'
				}, {
					name : 'ID_CHEQUERA_ABONO'
				}, {
					name : 'ID_FORMA_PAGO'
				}, {
					name : 'IMPORTE_PAGO'
				}, {
					name : 'IMPORTE_FWS'
				}, {
					name : 'TIPO_CAMBIO'
				}, {
					name : 'FEC_ALTA'
				}, {
					name : 'FEC_VENC'
				}, {
					name : 'NO_INSTITUCION'
				}, {
					name : 'NOM_CONTACTO'
				}, {
					name : 'ID_BANCO_BENEF'
				}, {
					name : 'ID_CHEQUERA_BENEF'
				}, {
					name : 'RUBRO_CGO'
				}, {
					name : 'SUBRUBRO_CGO'
				}, {
					name : 'RUBRO_ABN'
				}, {
					name : 'SUBRUBRO_ABN'
				}, {
					name : 'ESTATUS_MOV'
				}, {
					name : 'ESTATUS_IMP'
				}, {
					name : 'FIRMANTE_1'
				}, {
					name : 'FIRMANTE_2'
				}, {
					name : 'NO_DOCTO'
				}, {
					name : 'SPOT'
				}, {
					name : 'PUNTOS_FORWARD'
				},{
					name : 'REFERENCIA'
				},{
					name : 'CONCEPTO'
				},{
					name : 'ESTATUS_SWAP'
				},{
					name : 'ID_NETEO'
				} ],
				listeners : {
					load : function(s, records) {
						
					}
				}

			});

			NS.columnasCobImportadas = new Ext.grid.ColumnModel([
					NS.columnaSeleccionDer2, {
						header : 'FOLIO',
						width : 50,
						dataIndex : 'NO_FOLIO',
						sortable : true
					}, {
						header : 'No. Empresa',
						width : 100,
						dataIndex : 'NO_EMPRESA',
						sortable : true
					}, {
						header : 'Divisa Venta',
						width : 130,
						dataIndex : 'ID_DIVISA_VENTA',
						sortable : true
					}, {
						header : 'Banco Cargo',
						width : 120,
						dataIndex : 'ID_BANCO_CARGO',
						sortable : true
					}, {
						header : 'Chequera Cargo',
						width : 120,
						dataIndex : 'ID_CHEQUERA_CARGO',
						sortable : true
					}, {
						header : 'Divisa Compra',
						width : 120,
						dataIndex : 'ID_DIVISA_COMPRA',
						sortable : true
					}, {
						header : 'Banco Abono',
						width : 120,
						dataIndex : 'ID_BANCO_ABONO',
						sortable : true
					}, {
						header : 'Chequera Abono',
						width : 120,
						dataIndex : 'ID_CHEQUERA_ABONO',
						sortable : true
					}, {
						header : 'Forma Pago',
						width : 120,
						dataIndex : 'ID_FORMA_PAGO',
						sortable : true
					}, {
						header : 'Importe Pago',
						width : 120,
						dataIndex : 'IMPORTE_PAGO',
						sortable : true
					}, {
						header : 'Importe FWS',
						width : 120,
						dataIndex : 'IMPORTE_FWS',
						sortable : true
					}, {
						header : 'Tipo Cambio',
						width : 120,
						dataIndex : 'TIPO_CAMBIO',
						sortable : true
					}, {
						header : 'Fecha Alta',
						width : 120,
						dataIndex : 'FEC_ALTA',
						sortable : true
					}, {
						header : 'Fecha Vencimiento',
						width : 120,
						dataIndex : 'FEC_VENC',
						sortable : true
					}, {
						header : 'Institucion',
						width : 120,
						dataIndex : 'NO_INSTITUCION',
						sortable : true
					}, {
						header : 'Nombre Contacto',
						width : 120,
						dataIndex : 'NOM_CONTACTO',
						sortable : true
					}, {
						header : 'Banco Beneficiario',
						width : 120,
						dataIndex : 'ID_BANCO_BENEF',
						sortable : true
					}, {
						header : 'Chequera Beneficiaria',
						width : 120,
						dataIndex : 'ID_CHEQUERA_BENEF',
						sortable : true
					}, {
						header : 'Rubro Cargo',
						width : 120,
						dataIndex : 'RUBRO_CGO',
						sortable : true
					}, {
						header : 'SubRubro Cargo',
						width : 120,
						dataIndex : 'SUBRUBRO_CGO',
						sortable : true
					}, {
						header : 'Rubro Abono',
						width : 120,
						dataIndex : 'RUBRO_ABN',
						sortable : true
					}, {
						header : 'SubRubro Abono',
						width : 120,
						dataIndex : 'SUBRUBRO_ABN',
						sortable : true
					}, {
						header : 'Status Movimiento',
						width : 120,
						dataIndex : 'ESTATUS_MOV',
						sortable : false
					}, {
						header : 'Status_Importe',
						width : 120,
						dataIndex : 'ESTATUS_IMP',
						sortable : false
					}, {
						header : 'Firmanete 1',
						width : 120,
						dataIndex : 'FIRMANTE_1',
						sortable : true
					}, {
						header : 'Firmante 2',
						width : 120,
						dataIndex : 'FIRMANTE_2',
						sortable : true
					}, {
						header : 'No. Documento',
						width : 120,
						dataIndex : 'NO_DOCTO',
						sortable : true
					}, {
						header : 'SPOT',
						width : 120,
						dataIndex : 'SPOT',
						sortable : true
					}, {
						header : 'Puntos_Forward',
						width : 120,
						dataIndex : 'PUNTOS_FORWARD',
						sortable : true
					},
					{
						header : 'Referencia',
						width : 120,
						dataIndex : 'REFERENCIA',
						sortable : true
					},
					{
						header : 'Concepto',
						width : 120,
						dataIndex : 'CONCEPTO',
						sortable : true
					},{
						header : 'ESTATUS_SWAP',
						width : 120,
						dataIndex : 'ESTATUS_SWAP',
						sortable : true
					},{
						header : 'ID_NETEO',
						width : 120,
						dataIndex : 'ID_NETEO',
						sortable : true
					}
			// new Ext.grid.RowNumberer(),
			// {header: 'FOLIO', width: 80, dataIndex: 'folio' ,sortable: true},
			// {header: 'UNIDAD_DE_NEGOCIO', width: 120, dataIndex:
			// 'unidad_negocio' ,sortable: true},
			// {header: 'CHEQUERA_CARGO', width: 120, dataIndex:
			// 'chequera_cargo' ,sortable: true},
			// {header: 'CHEQUERA_ABONO', width: 120, dataIndex:
			// 'chequera_abono' ,sortable: true},
			// {header: 'FORMA_PAGO', width: 180, dataIndex: 'forma_pago'
			// ,sortable: true},
			// {header: 'IMPORTE PAGO', width: 120, dataIndex: 'importe_pago'
			// ,sortable: true, renderer: BFwrk.Util.rendererMoney},
			// {header: 'IMPORTE COMPRA', width: 120, dataIndex:
			// 'importe_compra' ,sortable: true, renderer:
			// BFwrk.Util.rendererMoney},
			// {header: 'T_C', width: 80, dataIndex: 'tc' ,sortable: true,
			// renderer: BFwrk.Util.rendererMoney},
			// {header: 'FEC_VTO', width: 100, dataIndex: 'fec_vto' ,sortable:
			// true},
			// {header: 'INSTITUCION', width: 100, dataIndex: 'institucion'
			// ,sortable: true},
			// {header: 'RUBRO_ABONO', width: 120, dataIndex: 'rubro_abono'
			// ,sortable: true},
			// // {header: 'RUBRO_ABONO', width: 120, dataIndex: 'rubro_abono'
			// ,sortable: true, renderer: BFwrk.Util.rendererMoney,
			// align:'right'},
			// {header: 'SUBRUBRO_ABONO', width: 120, dataIndex:
			// 'subrubro_abono' ,sortable: true},
			// {header: 'RUBRO_CARGO' , width: 120 , dataIndex: 'rubro_cargo' ,
			// sortable:true},
			// {header: 'SUBRUBRO_CARGO' , width: 120 , dataIndex:
			// 'subrubro_cargo' , sortable:true},
			// {header: 'FEC_COMPRA' , width: 100 , dataIndex: 'fec_compra' ,
			// sortable:true},
			// {header: 'SPOT' , width: 120 , dataIndex: 'spot' , sortable:true,
			// renderer: BFwrk.Util.rendererMoney},
			// {header: 'PUNTOS_FORWARD' , width: 120 , dataIndex:
			// 'puntos_forward' , sortable:true, renderer:
			// BFwrk.Util.rendererMoney},

			]);

			NS.gridDerivados2 = new Ext.grid.GridPanel({
				store : NS.storeLlenarGriDatosImp,
				id : 'gridDerivados2',
				// width: 1025,
				height : 213,
				stripeRows : true,
				columnLines : true,
				cm : NS.columnasCobImportadas,
				sm : NS.columnaSeleccionDer2,
			});

			/*******************************************************************
			 * FUNCIONES
			 */

			function cambiarFecha(fecha) {
				var mesArreglo = new Array(11);
				mesArreglo[0] = "Jan";
				mesArreglo[1] = "Feb";
				mesArreglo[2] = "Mar";
				mesArreglo[3] = "Apr";
				mesArreglo[4] = "May";
				mesArreglo[5] = "Jun";
				mesArreglo[6] = "Jul";
				mesArreglo[7] = "Aug";
				mesArreglo[8] = "Sep";
				mesArreglo[9] = "Oct";
				mesArreglo[10] = "Nov";
				mesArreglo[11] = "Dec";
				var mesDate = fecha.substring(4, 7);
				var dia = fecha.substring(8, 10);
				var anio = fecha.substring(11, 15);
				for (var i = 0; i < 12; i = i + 1) {
					if (mesArreglo[i] === mesDate) {
						var mes = i + 1;
						if (mes < 10)
							var mes = '0' + mes;
					}
				}
				var fechaString = '' + dia + '/' + mes + '/' + anio;
				return fechaString;
			}

			NS.opciones = function(op) {
				switch (op) {
				case 1:

					break;

				case 2:

					break;
				case 3:

					break;
				}
			};

			NS.limpiarTodo = function() {

				NS.storeForwards.removeAll();
				NS.gridDerivados.getView().refresh();
				banderaEleccion = 0;
				NS.txtNoEmpresa.setValue('');
				NS.txtPath.setValue('');
				NS.txtEmpresa.reset();

				NS.txtEmpresa.setDisabled(false);
				NS.txtNoEmpresa.setDisabled(false);
				NS.fechaIni = '';
				NS.fechaFin = NS.fecHoy;

				// NS.myMask.show();
				// NS.storeForwards.load();

			}

			NS.cancelar = function() {

			}

			NS.aceptar = function() {
				if (banderaEleccion == 0) {

				} else {

				}
			}

			/*******************************************************************
			 * PANEL'S *
			 ******************************************************************/

			NS.storeEmpresas = new Ext.data.DirectStore(
					{
						paramsAsHash : false,
						baseParams : {
							idUsuario : 2
						},
						root : '',
						paramOrder : [ 'idUsuario' ],
						// directFn:
						// CapturaSolicitudesPagoAction.obtenerEmpresas,
						directFn : GestionDeOperacionesDivisasAction.obtenerEmpresas,
						idProperty : 'noEmpresa',
						fields : [ {
							name : 'noEmpresa'
						}, {
							name : 'nomEmpresa'
						} ],
						listeners : {
							load : function(s, records) {
								var myMask = new Ext.LoadMask(Ext.getBody(), {
									store : NS.storeEmpresas,
									msg : "Cargando..."
								});
								if (records.length == null
										|| records.length <= 0) {
									Ext.Msg.alert('SET',
											'No tiene empresas asignadas');
								}
							}
						}
					});

			NS.storeEmpresas.load();

			NS.cmbEmpresas = new Ext.form.ComboBox({
				store : NS.storeEmpresas,
				name : PF + 'cmbEmpresas',
				id : PF + 'cmbEmpresas',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 90,
				y : 0,
				width : 280,
				tabIndex : 1,
				valueField : 'noEmpresa',
				displayField : 'nomEmpresa',
				autocomplete : true,
				emptyText : 'Seleccione una empresa',
				triggerAction : 'all',
				visible : false,
				editable : false,
				listeners : {
					select : {
						fn : function(combo, valor) {
							BFwrk.Util.updateComboToTextField(PF
									+ 'txtNoEmpresa', NS.cmbEmpresas.getId());
							// funLimpiarPorEventoEnEmpresas();
							// NS.accionarEmpresas(combo.getValue());
						}
					}
				}
			});

			NS.panelDerivados2 = new Ext.form.FieldSet({
				title : 'Consulta de Derivados',
				x : 0,
				y : 170,
				width : 1025,
				height : 250,
				hidden : true,
				layout : 'absolute',
				buttonAlign : 'right',
				items : [ NS.gridDerivados2, ],
			});
			NS.panelDerivados = new Ext.form.FieldSet({
				title : 'Consulta de Derivados',
				x : 0,
				y : 170,
				width : 1025,
				height : 250,
				hidden : false,
				layout : 'absolute',
				buttonAlign : 'right',
				items : [ NS.gridDerivados,

				],
			});

			NS.panelEmpresa = new Ext.form.FieldSet(
					{
						title : 'Empresa',
						x : 0,
						y : 0,
						width : 400,
						height : 60,
						layout : 'absolute',
						items : [
								{
									xtype : 'textfield',
									x : 20,
									y : 0,

									width : 60,
									name : PF + 'txtNoEmpresa',
									id : PF + 'txtNoEmpresa',
									value : '',
									listeners : {
										change : {
											fn : function(caja, valor) {
												// alert("");
												if (caja.getValue() === '') {
													NS.cmbEmpresas.reset();
													return;
												}

												if (BFwrk.Util
														.updateTextFieldToCombo(
																PF
																		+ 'txtNoEmpresa',
																NS.cmbEmpresas
																		.getId()) === undefined) {
													caja.reset();
													Ext.Msg
															.alert('SET',
																	'Id de Empresa no valido.');
													return;
												}

												// //linea cambia combo
												// BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
												// NS.accionarCmbEmpresa();
											}
										}
									}
								}, NS.cmbEmpresas,
						// NS.txtEmpresa,
						],
					});

			NS.panelFechas = new Ext.form.FieldSet({
				title : 'Fechas',
				x : 0,
				y : 70,
				width : 480,
				height : 90,
				layout : 'absolute',
				items : [

				{
					xtype : 'fieldset',
					// title: 'Clase',
					x : 0,
					y : 0,
					width : 190,
					height : 60,
					layout : 'absolute',
					id : PF + 'opcFechas',
					name : PF + 'opcFechas',
					items : [ NS.optFechas, ]
				},

				NS.lbFecInicio, NS.lbFecFin, NS.txtFechaIni, NS.txtFechaFin, ],
			});

			NS.foco = new Ext.form.TextField({

				id : PF + 'foco',
				name : PF + 'foco',
				value : '',
				x : 120,
				y : 60,
				width : 430,
				emptyText : '',
				value : false

			});

			NS.panelTodas = new Ext.form.FieldSet({
				title : 'Todas',
				x : 420,
				y : 0,
				width : 60,
				height : 60,
				layout : 'absolute',
				items : [ {
					xtype : 'checkbox',
					// boxLabel: 'Todas',
					x : 13,
					y : 0,
					id : PF + 'chkTodas',
					name : PF + 'chkTodas',
					listeners : {
						check : {
							fn : function(box) {
								if (box.getValue() == true) {

									NS.foco.setValue(box.getValue());
									// alert(NS.foco.getValue());
								} else {
									NS.foco.setValue(box.getValue());
									// alert(NS.foco.getValue());
								}
							}
						}
					}

				}, ],
			});

			NS.global = new Ext.form.FieldSet(
					{
						title : '',
						x : 0,
						y : 5,
						width : 1050,
						height : 480,
						layout : 'absolute',
						// autoScroll: true,
						items : [
								{
									xtype : 'button',
									text : 'Buscar',
									x : 500,
									y : 85,
									width : 90,
									height : 30,
									listeners : {
										click : {
											fn : function(e) {
												NS.panelDerivados2.setVisible(false);
												NS.panelDerivados.setVisible(true);
												Ext.getCmp(PF + 'swapear').setVisible(true);
												Ext.getCmp(PF + 'netear').setVisible(false);
												var foco = NS.foco.getValue();
												var tipo = NS.tipo.getValue();
												var fecA = Ext.getCmp(PF + 'txtFechaIni').getValue();
												var fecV = Ext.getCmp(PF + 'txtFechaFin').getValue();
												var noEmp = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
												parseInt(noEmp);
												// alert(noEmp );
												if (noEmp == '') {
													var noEmp = 1;
													parseInt(noEmp);

												}// alert(noEmp);

												if (foco == false) {

													// alert("aqui el foco esta
													// en false si esta en false
													// ver que tipo de consulta
													// quiere si 1 o 2");
													if (tipo == 1) {

														NS.storeForwards.baseParams.foco = false;
														NS.storeForwards.baseParams.tipo = parseInt(1);
														NS.storeForwards.baseParams.fec_A = fecA;
														NS.storeForwards.baseParams.fec_V = fecV;
														NS.storeForwards.baseParams.noEm = parseInt(noEmp);

														var myMask = new Ext.LoadMask(
																Ext.getBody(),
																{
																	store : NS.storeForwards,
																	msg : "Buscando..."
																});
														NS.storeForwards.load();
														// alert("hacer la
														// consulta de compra
														// con la fecha inicio
														// fin");
													} else {

														NS.storeForwards.baseParams.foco = false;
														NS.storeForwards.baseParams.tipo = parseInt(2);
														NS.storeForwards.baseParams.fec_A = fecA;
														NS.storeForwards.baseParams.fec_V = fecV;
														NS.storeForwards.baseParams.noEm = parseInt(noEmp);

														var myMask = new Ext.LoadMask(
																Ext.getBody(),
																{
																	store : NS.storeForwards,
																	msg : "Buscando..."
																});
														NS.storeForwards.load();

														// alert("hacer la
														// consulta con la fec
														// vencimiento inicio
														// fin");
													}

												} else {
													var tipo = NS.tipo
															.getValue();
													if (tipo == 1) {

														NS.storeForwards.baseParams.foco = true;
														NS.storeForwards.baseParams.tipo = parseInt(1);
														NS.storeForwards.baseParams.fec_A = fecA;
														NS.storeForwards.baseParams.fec_V = fecV;
														NS.storeForwards.baseParams.noEm = parseInt(noEmp);

														var myMask = new Ext.LoadMask(
																Ext.getBody(),
																{
																	store : NS.storeForwards,
																	msg : "Buscando..."
																});
														NS.storeForwards.load();

														// alert("si tiene el
														// foco
														// positivo cnsulta de
														// todo");

													} else {

														NS.storeForwards.baseParams.foco = true;
														NS.storeForwards.baseParams.tipo = parseInt(2);
														NS.storeForwards.baseParams.fec_A = fecA;
														NS.storeForwards.baseParams.fec_V = fecV;
														NS.storeForwards.baseParams.noEm = parseInt(noEmp);

														var myMask = new Ext.LoadMask(
																Ext.getBody(),
																{
																	store : NS.storeForwards,
																	msg : "Buscando..."
																});
														NS.storeForwards.load();

														// alert("si tiene el
														// foco
														// positivo cnsulta de
														// todo");

													}
												}

											}
										}
									}
								},
								NS.panelEmpresa,
								NS.panelDerivados2,
								NS.panelDerivados,

								NS.panelFechas,
								NS.panelTodas,
								{
									xtype : 'button',
									text : 'SWAPEAR',
									id : PF + 'swapear',
									name : PF + 'swapear',
									x : 850,
									y : 420,
									width : 80,
									height : 22,
									listeners : {
										click : {
											fn : function(e) {
												var registroSeleccionado = NS.gridDerivados.getSelectionModel().getSelections();
												if ((registroSeleccionado.length > 0) && (registroSeleccionado.length % 2 == 0)) {
													

													for(var i = 0; i < registroSeleccionado.length; i ++){
														var matriz = new Array();
														var vector = {};
														 vector.folio = registroSeleccionado[i].get('NO_FOLIO');
														 vector.unidad_negocio = registroSeleccionado[i].get('NO_EMPRESA');
														 vector.chequera_cargo = registroSeleccionado[i].get('ID_CHEQUERA_CARGO');
														 vector.chequera_abono = registroSeleccionado[i].get('ID_CHEQUERA_ABONO');
														 vector.importe_pago = registroSeleccionado[i].get('IMPORTE_PAGO');
														 vector.importe_compra = registroSeleccionado[i].get('IMPORTE_FWS');
														 vector.fec_vto = registroSeleccionado[i].get('FEC_VENC');
														 vector.fec_compra=registroSeleccionado[i].get('FEC_ALTA');
														 vector.spot = registroSeleccionado[i].get('SPOT');
														 vector.puntos_forward = registroSeleccionado[i].get('PUNTOS_FORWARD');
														 matriz[i] = vector;
														 
														 var jSonString = Ext.util.JSON.encode(matriz);
															// cambiar el status del Forward en la columna Swap a S
															
															ForwardsAction.swapForward(jSonString,NS.idUsuario, function(result, e){
//										    					 Ext.Msg.alert('SET', result);
										    					 NS.panelDerivados.setVisible(false);
																	NS.storeLlenarGriDatosImp.removeAll();
																	NS.gridDerivados2.store.add(registroSeleccionado);
																	NS.panelDerivados2.setVisible(true);
																	Ext.getCmp(PF + 'netear').setVisible(true);
																	Ext.getCmp(PF + 'swapear').setVisible(false);
																	
										    				});

															

													}
													
													
													
													
													
													
													// Ext.Msg.alert('SET',
													// 'Verifique que los datos
													// sean corecctos antes de
													// netear ');
													// var matriz = new Array();
													// for(var i = 0; i <
													// registroSeleccionado.length;
													// i ++){
													// var vector = {};
													//															
													// vector.folio =
													// registroSeleccionado[i].get('NO_FOLIO');
													// vector.unidad_negocio =
													// registroSeleccionado[i].get('NO_EMPRESA');
													// vector.chequera_cargo =
													// registroSeleccionado[i].get('ID_CHEQUERA_CARGO');
													// vector.chequera_abono =
													// registroSeleccionado[i].get('ID_CHEQUERA_ABONO');
													// vector.forma_pago =
													// registroSeleccionado[i].get('ID_FORMA_PAGO');
													// vector.importe_pago =
													// regist0roSeleccionado[i].get('IMPORTE_PAGO');
													// vector.importe_compra =
													// registroSeleccionado[i].get('IMPORTE_FWS');
													// vector.tc =
													// registroSeleccionado[i].get('TIPO_CAMBIO');
													// vector.fec_vto =
													// registroSeleccionado[i].get('FEC_VENC');
													// vector.institucion =
													// registroSeleccionado[i].get('NO_INSTITUCION');
													// vector.rubro_abono =
													// registroSeleccionado[i].get('RUBRO_ABN');
													// vector.subrubro_abono =
													// registroSeleccionado[i].get('SUBRUBRO_ABN');
													// vector.rubro_cargo =
													// registroSeleccionado[i].get('RUBRO_CGO');
													// vector.subrubro_cargo =
													// registroSeleccionado[i].get('SUBRUBRO_CGO');
													// vector.fec_compra=registroSeleccionado[i].get('FEC_ALTA');
													// vector.spot=
													// registroSeleccionado[i].get('SPOT');
													// vector.puntos_forward=
													// registroSeleccionado[i].get('PUNTOS_FORWARD');
													//															
													//															
													// matriz[i] = vector;
													// }
													
													

													

												} else {
													//													
													Ext.Msg.alert('SET','Seleccione filas pares para Swapear');

												}

											}
										}
									}
								}, {

									xtype : 'button',
									text : 'Netear',
									id : PF + 'netear',
									name : PF + 'netear',
									x : 850,
									y : 420,
									width : 80,
									height : 22,
									hidden : true,
									listeners : {
										click : {
											fn : function(e) {
//												NS.gridNeteo.getStore().removeAll();	
//												NS.storeLlenarNeteo.removeAll();
//												
												
												var registroSeleccionado = NS.gridDerivados2.getSelectionModel().getSelections();
											
												var datosClase = NS.gridNeteo.getStore().recordType;


												var cargoDls = 0.0;
												var abonoDls = 0.0;
												
												var cargoEur = 0.0;
												var abonoEur = 0.0;
												
												var cargoMn = 0.0;
												var abonoMn = 0.0;
												
												
												NS.cargosMn = 0.0;
												NS.cargosEur = 0.0;
												NS.cargosDls = 0.0;
												
												NS.abonosMn = 0.0;
												NS.abonosEur = 0.0;
												NS.abonosDls = 0.0;
												
												var contAux = 0;
												
												var bancoDls = '';
												var bancoMn = '';
												var bancoEur = '';
												var neteo = '';
												
												
												for (var i = 0; i < NS.gridDerivados2.store.getTotalCount(); i++) {
													
													var divisaCargo = NS.gridDerivados2.store.getAt(i).get("ID_DIVISA_VENTA");
													var divisaAbono = NS.gridDerivados2.store.getAt(i).get("ID_DIVISA_COMPRA");
													
													var fechaVto = NS.gridDerivados2.store.getAt(i).get("FEC_VENC");
													
													
													
//													var bancoCargo = '';
//													var bancoAbono = '';
													
//													var empresa = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_CARGO");
													
//													var banco = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_ABONO");
													
													
													if(NS.gridDerivados2.getSelectionModel().isSelected(i)){
														
														if(divisaCargo === 'DLS'){
															cargoDls += parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_PAGO"));
															bancoDls = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_CARGO");
														}else if(divisaCargo === 'MN'){
															cargoMn += parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_PAGO"));
															bancoMn = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_CARGO");
														}else if(divisaCargo === 'EUR'){
															cargoEur += parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_PAGO"));
															bancoEur = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_CARGO");
														}
														
														if(divisaAbono === 'DLS'){
															abonoDls += parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_FWS"));
															bancoDls = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_ABONO");
														}else if(divisaAbono === 'MN'){
															abonoMn += parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_FWS"));
															bancoMn = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_ABONO");
														}else if(divisaAbono === 'EUR'){
															abonoEur += parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_FWS"));
															bancoEur = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_ABONO");
														}
														
														
													}else{
														neteo = 'NO';
														
														if(divisaCargo === 'DLS'){
															bancoDls = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_CARGO");
															NS.cargosDls = parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_PAGO"));
															
														}else if(divisaCargo === 'MN'){
															bancoMn = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_CARGO");
															NS.cargosMn = parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_PAGO"));
															
														}else if(divisaCargo === 'EUR'){
															bancoEur = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_CARGO");
															NS.cargosEur = parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_PAGO"));
															
														}
														
														if(divisaAbono === 'DLS'){
															bancoDls = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_ABONO");
															NS.abonosDls = parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_FWS"));
															
														}else if(divisaAbono === 'MN'){
															bancoMn = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_ABONO");
															NS.abonosMn = parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_FWS"));
															
														}else if(divisaAbono === 'EUR'){
															bancoEur = NS.gridDerivados2.store.getAt(i).get("ID_BANCO_ABONO");
															NS.abonosEur = parseFloat(NS.gridDerivados2.store.getAt(i).get("IMPORTE_FWS"));
															
														}
														
//														alert(contAux);
														
														var datos2 = new datosClase({
															cargosDls : NS.cargosDls,
															abonosDls : NS.abonosDls,
															cargosMn  : NS.cargosMn,
															abonosMn  : NS.abonosMn,
															cargosEur : NS.cargosEur,
															abonosEur : NS.abonosEur,
															fechaVto  : fechaVto,
															bancoDls  : bancoDls,
															bancoMn   : bancoMn,
															bancoEur  : bancoEur,
															neteo     : neteo
															
														});
//														alert("antes122");
														NS.storeLlenarNeteo.insert(contAux, datos2);
														contAux++;
														
													}
													
													NS.cargosMn = 0.0;
													NS.cargosEur = 0.0;
													NS.cargosDls = 0.0;
													
													NS.abonosMn = 0.0;
													NS.abonosEur = 0.0;
													NS.abonosDls = 0.0;
													
												}
												
												
//												alert(cargoDls + "");
//												alert(abonoDls + "");
//												alert((cargoDls - abonoDls) + " Total Dls");
//												alert((cargoEur - abonoEur) + " Total Eur");
//												alert((cargoMn - abonoMn) + " Total Mn");
//												
												
												
												
												sumDlsNet = cargoDls - abonoDls;
												sumEurNet = cargoEur - abonoEur;
												sumMnNet = cargoMn - abonoMn;
													
												
												
												
												
												if(sumDlsNet>0){
													NS.cargosDls = sumDlsNet;
												}else{
													NS.abonosDls = Math.abs(sumDlsNet);
												}
												
												if(sumEurNet>0){
													NS.cargosEur = sumEurNet;
												}else{
													NS.abonosEur = Math.abs(sumEurNet);
												}
												
												if(sumMnNet>0){
													NS.cargosMn = sumMnNet;
												}else{
													NS.abonosMn = Math.abs(sumMnNet);
												}
												
												
												if (NS.cargosDls > 0 ||
														NS.abonosDls > 0 ||
														NS.cargosMn > 0 ||
														NS.abonosMn > 0 ||
														NS.cargosEur> 0 ||
														NS.abonosEur) {
													neteo = 'SI';
													var datos = null;
													datos = new datosClase({
														cargosDls : NS.cargosDls,
														abonosDls : NS.abonosDls,
														cargosMn  : NS.cargosMn,
														abonosMn  : NS.abonosMn,
														cargosEur : NS.cargosEur,
														abonosEur : NS.abonosEur,
														fechaVto  : fechaVto,
														bancoDls  : bancoDls,
														bancoMn   : bancoMn,
														bancoEur  : bancoEur,
														neteo     : neteo
													});
													NS.storeLlenarNeteo.insert(contAux, datos);
													contAux++;
												}
												
//												NS.cargosDls = 0; 
//												NS.abonosDls = 0; 
//												NS.cargosMn  = 0; 
//												NS.abonosMn  = 0; 
//												NS.cargosEur = 0; 
//												NS.abonosEur = 0; 
												
												
//												alert("Cargos Dolares: "+ NS.cargosDls);
//												alert("Abonos Dolares: "+ NS.abonosDls);
//												
//												alert("Cargos Euros: "+ NS.cargosEur);
//												alert("Abonos Euros: "+ NS.abonosEur);
//												
//												alert("Cargos Mn: "+ NS.cargosMn);
//												alert("Abonos Mn: "+ NS.abonosMn);
												
												 
												
												
												
												
//												alert("mostrando");
												NS.winParcializar.show();
												NS.gridNeteo.stopEditing();
												NS.gridNeteo.getStore().load();
//												NS.gridNeteo.getView().refresh();
												
												
												
												
												
												
											}
										}
									}

								} ]
					});
			
			
			
			
			
	
		
				

			NS.winParcializar = new Ext.Window({
					title: 'Bank Preview',
					modal: true,
					shadow: true,
					//closable: false,
					closeAction: 'hide',
					width: 1000,
				   	height: 550,
				   	layout: 'absolute',
				   	plain: true,
				    bodyStyle: 'padding:10px;',
				    buttonAlign: 'center',
				    draggable: false,
				    resizable: false,
				   	items: [
							NS.gridNeteo,
							//va panel
							
							{
								xtype : 'fieldset',
								title : 'Detalle Forward',
								x : 510,
								y : 0,
								width : 450,
								height : 300,
								layout : 'absolute',
								items : [								         
								         {
								        	xtype : 'label',
											text : 'Es neteo:',
											x : 5,
											y : 20 
								         },
								         {
									        xtype : 'label',
											text : 'Fecha Vencimiento:',
											x : 5,
											y : 55 
									     },
									     {
										    xtype : 'label',
											text : '___________________________________________________________________',
											x : 5,
											y : 80 
										 },
								         {
											xtype : 'label',
											text : 'Paga:',
											x : 170,
											y : 110
										},
										{
											xtype : 'label',
											text : 'Paga:',
											x : 170,
											y : 150
										},
										{
											xtype : 'label',
											text : 'Paga:',
											x : 170,
											y : 190
										},
										{
											xtype : 'label',
											text : 'DLS',
											x : 350,
											y : 110
										},
										{
											xtype : 'label',
											text : 'MN',
											x : 350,
											y : 150
										},
										{
											xtype : 'label',
											text : 'EUR',
											x : 350,
											y : 190
										},
										{
											xtype : 'textfield',
											x : 60,
											y : 15,
											width : 100,
											name : PF + 'txtNeteo',
											disabled: true,
											id : PF + 'txtNeteo',
											value : '',
											
										},{
											xtype : 'textfield',
											x : 110,
											y : 50,
											width : 100,
											name : PF + 'txtFechaVto',
											disabled: true,
											id : PF + 'txtFechaVto',
											value : '',
										},
										{
											xtype : 'textfield',
											x : 230,
											y : 105,
											width : 100,
											name : PF + 'txtPagaDls',
											disabled: true,
											id : PF + 'txtPagaDls',
											value : '',
										},{
											xtype : 'textfield',
											x : 230,
											y : 145,
											width : 100,
											name : PF + 'txtPagaMn',
											disabled: true,
											id : PF + 'txtPagaMn',
											value : '',
										},
										{
											xtype : 'textfield',
											x : 230,
											y : 185,
											width : 100,
											name : PF + 'txtPagaEur',
											disabled: true,
											id : PF + 'txtPagaEur',
											value : '',
										},
										{
											xtype : 'textfield',
											x : 50,
											y : 105,
											width : 100,
											name : PF + 'txtBancoDls',
											disabled: true,
											id : PF + 'txtBancoDls',
											value : '',
										},{
											xtype : 'textfield',
											x : 50,
											y : 145,
											width : 100,
											name : PF + 'txtBancoMn',
											disabled: true,
											id : PF + 'txtBancoMn',
											value : '',
										},
										{
											xtype : 'textfield',
											x : 50,
											y : 185,
											width : 100,
											name : PF + 'txtBancoEur',
											disabled: true,
											id : PF + 'txtBancoEur',
											value : '',
										},
										
								]
							},{
		                        xtype: 'button',
		                        id: PF+'cmdImprimir',
		                        name: PF+'cmdImprimir',
		                        text: 'Imprimir',
		                        x: 200,
		                        y: 480,
		                        width: 80,
		                        listeners:{
			               			click:{
			           			   		fn:function(e){
			           			   			alert("Imprmir");
			           			   			
//			           			   			NS.winParcializar.hide();
//			           			   			NS.panelDerivados2.setVisible(false);
//											NS.panelDerivados.setVisible(true);
//											Ext.getCmp(PF + 'swapear').setVisible(true);
//											Ext.getCmp(PF + 'netear').setVisible(false);
//			           			   			NS.gridNeteo.getView().refresh();
			           			   			NS.gridNeteo.getView().refresh();
			           			   			NS.gridDerivados2.getView().refresh();
			           			   		}
			        			   	}
			    			   	}
		                    
							},
		                    {
		                        xtype: 'button',
		                        id: PF+'cmdAceptar',
		                        name: PF+'cmdAceptar',
		                        text: 'Aceptar',
		                        x: 300,
		                        y: 480,
		                        width: 80,
		                        listeners:{
			               			click:{
			           			   		fn:function(e){
			           			   			NS.storeLlenarNeteo.removeAll();
			           			   			NS.winParcializar.hide();
			           			   			NS.panelDerivados2.setVisible(false);
			           			   			NS.panelDerivados.setVisible(true);
			           			   			Ext.getCmp(PF + 'swapear').setVisible(true);
			           			   			Ext.getCmp(PF + 'netear').setVisible(false);
			           			   			NS.storeForwards.load();
			           			   			NS.panelDerivados.getView().refresh();
			           			   			
			           			   		}
			        			   	}
			    			   	}
		                    },
							{
		                        xtype: 'button',
		                        id: PF+'cmdCerrar',
		                        name: PF+'cmdCerrar',
		                        text: 'Cerrar',
		                        x: 400,
		                        y: 480,
		                        width: 80,
		                        listeners:{
			               			click:{
			           			   		fn:function(e){
			           			   			NS.storeLlenarNeteo.removeAll();
			           			   			NS.winParcializar.hide();
			           			   			NS.panelDerivados2.setVisible(false);
			           			   			NS.panelDerivados.setVisible(true);
			           			   			Ext.getCmp(PF + 'swapear').setVisible(true);
			           			   			Ext.getCmp(PF + 'netear').setVisible(false);
			           			   			NS.storeForwards.load();
			           			   			NS.panelDerivados.getView().refresh();
			           			   		}
			        			   	}
			    			   	}
		                    },
//					        
				   	        ],
				   	        listeners:{
				   	        	show:{		   	        		
				   	        		fn:function(){
//				   	        			NS.gridNeteo.setVisible(true);
				   	        			NS.gridNeteo.getView().refresh();
				   	        			
				   	        		}
				   	        	},
				   	        	hide:{		   	        		
				   	        		fn:function(){
//				   	        			NS.gridNeteo.getView().refresh();
//				   	        		
				   	        			
				   	        		}
				   	        	}
				   	        }	
					});

			

			NS.derivados = new Ext.FormPanel({
				title : 'Consulta de Derivados',
				// width: 1300,
				// height: 800,
				frame : true,
				padding : 10,
				autoScroll : true,
				layout : 'absolute',
				id : PF + 'derivados',
				name : PF + 'derivados',
				renderTo : NS.tabContId,
				items : [ NS.global ]
			});

			NS.derivados.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(
					NS.tabContId).getHeight());
		});