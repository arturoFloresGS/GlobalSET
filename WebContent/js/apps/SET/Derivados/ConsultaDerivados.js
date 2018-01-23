/**
 * Eric Cesar Guzmán Malvaez
 */

Ext.onReady(function() {
			var NS = Ext.namespace('apps.SET.Derivados.ConsultaDerivados');
			NS.tabContId = apps.SET.tabContainerId;
			var PF = apps.SET.tabID + '.';

			NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
			NS.empresa = NS.GI_ID_EMPRESA;
			NS.idUsuario = apps.SET.iUserId;
			Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
			Ext.QuickTips.init();

			NS.fecHoy = apps.SET.FEC_HOY;
			NS.fechaIni = '';
			NS.fechaFin = NS.fecHoy;
			NS.divisa = '';
			NS.unformatNumber = function(num) {
				return num.replace(/(,)/g,''); //num.replace(/([^0-9\.\-])/g,''*1);
			};
			
			NS.formatNumber = function(num,prefix){
				num = num.toString();
				if(num.indexOf('.')>-1){
					if(num.substring(num.indexOf('.')).length<3){
						num = num + '0';   
					}    
				}
				else{
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
			

			/*******************************************************************
			 * COMPONENTES *
			 ******************************************************************/
			// NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Procesando
			// ..."})
			
			
			NS.lbNoFolio = new Ext.form.Label({
				id: PF + 'lbNoFolio',
				text: 'Folio: ',
				x: 10,
				y : 0		
			});
			
			NS.lbNoEmpresa = new Ext.form.Label({
				id: PF + 'lbNoEmpresa',
				text: 'No. Empresa: ',
				x: 10,
				y : 30		
			});
			
			NS.lbCheCargo = new Ext.form.Label({
				id: PF + 'lbCheCargo',
				text: 'Chequera Cargo: ',
				x: 10,
				y : 60		
			});
			
			NS.lbCheAbono = new Ext.form.Label({
				id: PF + 'lbCheAbono',
				text: 'Chequera Abono: ',
				x: 10,
				y : 90		
			});
			
			NS.lbSpot = new Ext.form.Label({
				id: PF + 'lbSpot',
				text: 'Spot: ',
				x: 10,
				y : 120		
			});
			
			NS.lbPtosFws = new Ext.form.Label({
				id: PF + 'lbPtosFws',
				text: 'Ptos. Forward: ',
				x: 10,
				y : 150		
			});
			
			
			NS.lbTipoCambio = new Ext.form.Label({
				id: PF + 'lbTipoCambio',
				text: 'Tipo de cambio: ',
				x: 10,
				y : 180		
			});
			
			NS.lbImpCompra = new Ext.form.Label({
				id: PF + 'lbImpCompra',
				text: 'Importe Forward: ',
				x: 10,
				y : 210	
			});
			
			NS.lbImpVenta = new Ext.form.Label({
				id: PF + 'lbImpVenta',
				text: 'Importe Pago: ',
				x: 10,
				y : 240		
			});
			
			NS.lbFecAlta = new Ext.form.Label({
				id : PF + 'lbFecAlta',
				text : 'Fecha Alta:: ',
				x : 10,
				y : 270
			});
			
			NS.lbFecCompra = new Ext.form.Label({
				id : PF + 'lbFecCompra',
				text : 'Fecha Vencimiento: ',
				x : 10,
				y : 300
			});
			

			// TXTS
			NS.txtNoFolio = new Ext.form.NumberField({
				id: PF+'txtNoFolio',
		        name: PF+'txtNoFolio',
		        x: 120,
				y: 0,
		        width: 200,
		        disabled: true,
		    });
			
			NS.txtNoEmpresa2 = new Ext.form.NumberField({
				id: PF+'txtNoEmpresa2',
		        name: PF+'txtNoEmpresa2',
		        x: 120,
				y: 30,
		        width: 200,
		        disabled: true,
		    });
			
			NS.txtCheCargo = new Ext.form.TextField({
				id: PF+'txtCheCargo',
		        name: PF+'txtCheCargo',
		        x: 120,
				y: 60,
		        width: 200,
		        disabled: true,
		    });
			
			NS.txtCheAbono = new Ext.form.TextField({
				id: PF+'txtCheAbono',
		        name: PF+'txtCheAbono',
		        x: 120,
				y: 90,
		        width: 200,
		        disabled: true,
		    });
			
			
			// num
		
			     
		 	
		 	
		 	NS.txtSpot = new Ext.form.NumberField({
				id: PF + 'txtSpot',
		        name:PF + 'txtSpot',
				enableKeyEvents: true,
				selectOnFocus: false,
				forceSelection: false,
				decimalPrecision: 4,
				minValue :0,
				x: 120,
				y: 120,
			    width: 200,
		        listeners: {
		        	keyup : {
		 				fn: function(caja,valor){
		 					
		 					var ptosFw = parseFloat(Ext.getCmp(PF + 'txtPtsForward').getValue());
//		 					alert(NS.formatNumber(ptosFw + parseFloat(caja.getValue())));
		 					Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(ptosFw + parseFloat(caja.getValue())));
		 					
		 				}
		 			}
				}
			});
		 	
		 	
		 	
		 	NS.txtPtsForward = new Ext.form.NumberField({
				id: PF + 'txtPtsForward',
		        name:PF + 'txtPtsForward',
		        x: 120,
				y: 150,
			    width: 200, 
				enableKeyEvents: true,
				selectOnFocus: false,
				forceSelection: false,
				decimalPrecision: 4,
				minValue :0,
		        listeners: {
		        	

		        	keyup : {
		 				fn: function(caja,valor){
		 					
		 					var spot = parseFloat(Ext.getCmp(PF + 'txtSpot').getValue());
//		 					alert(NS.formatNumber(spot + parseFloat(caja.getValue())));
		 					Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(spot + parseFloat(caja.getValue())));
		 					
		 				}
		 			}
				
		        	
//					change: {
//						fn: function(caja, valor) {
//							alert("holasCHAN");
//								    		var TipoCam = Ext.getCmp(PF + 'txtPtsForward').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtPtsForward').getValue()));
//											var ptsForward = parseFloat(Ext.getCmp(PF + 'txtPtsForward').getValue());
//											var spot = parseFloat(Ext.getCmp(PF + 'txtSpot').getValue());
//											Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(spot + ptsForward));
										
//						},
//			 			keyup : {
//			 				fn: function(caja,valor){
//			 					alert("holasKEY");
//			 					var spot = parseFloat(Ext.getCmp(PF + 'txtSpot').getValue());
//			 					alert(spot);
//			 					Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(spot + parseFloat(caja.getValue())));
			 				
//			 				}
//			 			}
//					}
				}
			});
		 	
		 	
		 	
		 	
		 	NS.txtTipoCambio = new Ext.form.NumberField({
				id: PF + 'txtTipoCambio',
		        name:PF + 'txtTipoCambio',
		        x: 120,
				y: 180,
			    width: 200,
				disabled:true,
				decimalPrecision: 4,
				minValue :0,
		        listeners: {
					change: {
						fn: function(caja, valor) {
							//var spot = parseFloat(Ext.getCmp(PF + 'txtSpot').getValue());					
							//var ptsForward = parseFloat(Ext.getCmp(PF + 'txtPtsForward').getValue());
						
							if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '' ) {
								var TipoCam = Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtTipoCambio').getValue()));
							//	Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(spot + ptsForward));
								
							
							}
						},
			 			blur: {
			 				fn: function(caja, valor) {
			 					var compra = parseFloat(Ext.getCmp(PF  + 'txtImporteCompra').getValue());
			 					//Ext.getCmp(PF + 'txtPago').setValue(NS.formatNumber(parseFloat(caja.getValue()) * compra));
//			 					alert(TipoCam);
			 					
			 				
			 				}
			 			}
					}
				}
			});
		 	
		 	
		 	
		 	
		 	NS.txtImporteCompra = new Ext.form.TextField({
				id: PF + 'txtImporteCompra',
		        name:PF + 'txtImporteCompra',
		        x: 120,
				y: 210,
			    width: 200,
				enableKeyEvents: true,
				selectOnFocus: true,
				forceSelection: false,
				
		        listeners: {
					keyup: {
						fn: function(caja, valor) {
//							alert("j0a");
							 var tipo = parseFloat(Ext.getCmp(PF + 'txtTipoCambio').getValue());
//							 alert(tipo);
							 var impCom = parseFloat(caja.getValue());
//							 alert(impCom);
//							 
		 					 if(impCom != null && impCom != undefined && impCom != '') {
		 						 
								 if(tipo != "") {
									 if(NS.divisa == "MN") {
										 valor1 = parseFloat(impCom / tipo);
										 Ext.getCmp(PF + 'txtImportePago').setValue(NS.formatNumber(valor1));
									 }else {
										 valor1 = parseFloat(impCom * tipo);
										 Ext.getCmp(PF + 'txtImportePago').setValue(NS.formatNumber(valor1));
									 }
			 					 }   
		 					}
							 
						}
					}
				}
			});
		 	
		 	
		 	 	
		 	NS.txtImportePago = new Ext.form.TextField({
				id: PF + 'txtImportePago',
		        name:PF + 'txtImportePago',
		        x: 120,
				y: 240,
			    width: 200,	
		        disabled:true,
			});
		 	
		 	
		 	NS.txtFechaAlta = new Ext.form.TextField({
				name : PF + 'txtFechaAlta',
				id : PF + 'txtFechaAlta',
				x: 120,
				y: 270,
			    width: 200,
//				format : 'd/m/Y',
				disabled:true,
			});
		 	
		 	NS.txtFechaVenc = new Ext.form.TextField({
				name : PF + 'txtFechaVenc',
				id : PF + 'txtFechaVenc',
				x: 120,
				y: 300,
			    width: 200,
//				format : 'd/m/Y',
				disabled:true,
			});
			
			//___________________________________
		 	
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
			
			NS.txtFechaIni = new Ext.form.DateField({
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

			NS.columnaSeleccionDer = new Ext.grid.CheckboxSelectionModel({
				singleSelect : true
			});

			NS.columnasDerivados = new Ext.grid.ColumnModel([
					NS.columnaSeleccionDer, {
						header : 'N°FOLIO',
						width : 50,
						dataIndex : 'NO_FOLIO',
						sortable : true
					}, {
						header : 'N° Empresa',
						width : 100,
						dataIndex : 'NO_EMPRESA',
						sortable : true
					}, {
						header : 'Id_Divisa_venta',
						width : 130,
						dataIndex : 'ID_DIVISA_VENTA',
						sortable : true
					}, {
						header : 'Id_Banco_Cargo',
						width : 120,
						dataIndex : 'ID_BANCO_CARGO',
						sortable : true
					}, {
						header : 'Id_Chequera_Cargo',
						width : 120,
						dataIndex : 'ID_CHEQUERA_CARGO',
						sortable : true
					}, {
						header : 'Id_Divisa_Compra',
						width : 120,
						dataIndex : 'ID_DIVISA_COMPRA',
						sortable : true
					}, {
						header : 'Id_Banco_Abono',
						width : 120,
						dataIndex : 'ID_BANCO_ABONO',
						sortable : true
					}, {
						header : 'Id_Chequera_Abono',
						width : 120,
						dataIndex : 'ID_CHEQUERA_ABONO',
						sortable : true
					}, {
						header : 'Id_Forma_Pago',
						width : 120,
						dataIndex : 'ID_FORMA_PAGO',
						sortable : true
					}, {
						header : 'Importe_Pago',
						width : 120,
						dataIndex : 'IMPORTE_PAGO',
						sortable : true
					}, {
						header : 'Importe_FWS',
						width : 120,
						dataIndex : 'IMPORTE_FWS',
						sortable : true
					}, {
						header : 'Tipo_Cambio',
						width : 120,
						dataIndex : 'TIPO_CAMBIO',
						sortable : true
					}, {
						header : 'Fecha_Alta',
						width : 120,
						dataIndex : 'FEC_ALTA',
						sortable : true
					}, {
						header : 'Fecha_Vencimiento',
						width : 120,
						dataIndex : 'FEC_VENC',
						sortable : true
					}, {
						header : 'N°Institucion',
						width : 120,
						dataIndex : 'NO_INSTITUCION',
						sortable : true
					}, {
						header : 'Nombre_Contacto',
						width : 120,
						dataIndex : 'NOM_CONTACTO',
						sortable : true
					}, {
						header : 'Id_Banco_Beneficiario',
						width : 120,
						dataIndex : 'ID_BANCO_BENEF',
						sortable : true
					}, {
						header : 'Id_Chequera_Beneficiaria',
						width : 120,
						dataIndex : 'ID_CHEQUERA_BENEF',
						sortable : true
					}, {
						header : 'Rubro_Cargo',
						width : 120,
						dataIndex : 'RUBRO_CGO',
						sortable : true
					}, {
						header : 'Sub_Rubro_Cargo',
						width : 120,
						dataIndex : 'SUBRUBRO_CGO',
						sortable : true
					}, {
						header : 'Rubro_Abono',
						width : 120,
						dataIndex : 'RUBRO_ABN',
						sortable : true
					}, {
						header : 'SubRubro_Abono',
						width : 120,
						dataIndex : 'SUBRUBRO_ABN',
						sortable : true
					}, {
						header : 'Status_Movimiento',
						width : 120,
						dataIndex : 'ESTATUS_MOV',
						sortable : true
					}, {
						header : 'Status_Importe',
						width : 120,
						dataIndex : 'ESTATUS_IMP',
						sortable : true
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
						header : 'N° Documento',
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
					},
					{
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
						},{
							name : 'REFERENCIA'
						},{
							name : 'CONCEPTO'
						},{
							name : 'ESTATUS_SWAP'
						},{
							name : 'ID_NETEO'
						}

						],
						listeners : {
							load : function(s, records) {
								var myMask = new Ext.LoadMask(Ext.getBody(), {
									store : NS.storeForwards,
									msg : "Cargando..."
								});
								if (records.length == null
										|| records.length <= 0) {

									Ext.Msg
											.alert('SET',
													'No existen Datos para esta operacion');
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
				cm : NS.columnasDerivados,
				// width: 1025,
				height : 213,
				stripeRows : true,
				columnLines : true,
				cm : NS.columnasDerivados,
				sm : NS.columnaSeleccionDer,
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

			NS.buscar = function() {

			};

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

			NS.panelDerivados = new Ext.form.FieldSet({
				title : 'Consulta de Derivados',
				x : 0,
				y : 170,
				width : 1025,
				height : 250,
				layout : 'absolute',
				buttonAlign : 'right',
				items : [ NS.gridDerivados, ],
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
												if (caja.getValue() === '') {
													NS.cmbEmpresas.reset();
													return;
												}

												if (BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId()) === undefined) {
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
													var tipo = NS.tipo.getValue();
													if (tipo==1) {

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

														// alert("si tiene el foco
														// positivo cnsulta de
														// todo");

														
													}else{

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

														// alert("si tiene el foco
														// positivo cnsulta de
														// todo");
														
													}
												}

											}
										}
									}
								},
								NS.panelEmpresa,
								NS.panelDerivados,
								NS.panelFechas,
								NS.panelTodas,
								{
									xtype : 'button',
									text : 'Excel',
									x : 850,
									y : 420,
									width : 80,
									height : 22,
									listeners : {
										click : {
											fn : function(e) {
												// rev55
												// alert("excel");

												var foco = NS.foco.getValue();
												var tipo = NS.tipo.getValue();
												var fecA = cambiarFecha(Ext.getCmp(PF+ 'txtFechaIni').getValue()+ '')+ '';
												var fecV = cambiarFecha(Ext
														.getCmp(PF + 'txtFechaFin').getValue() + '') + '';
												var noEmp = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
												if (foco == false) {

													// alert("aqui el foco esta
													// en false si esta en false
													// ver que tipo de consulta
													// quiere si 1 o 2");
													if (tipo == 1) {
														var emp = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
														parseInt(emp);
														var foco = false;
														var tipo = 1;
														var fecA = cambiarFecha(Ext.getCmp(PF+ 'txtFechaIni').getValue()+ '')+ '';
														var fecV = cambiarFecha(Ext.getCmp(PF+ 'txtFechaFin').getValue()+ '')+ '';

														if (emp == '') {
															emp = 1;
															parseInt(noEmp);

														}

														if (emp != "") {
															// Ext.Msg.alert(emp);
															parametros = '?nomReporte=excelFws';
															parametros += '&nomParam1=tipoPersona';
															parametros += '&valParam1='+ emp;

															parametros += '&nomParam2=tipoPersona2';
															parametros += '&valParam2='
																	+ foco;
															parametros += '&nomParam3=tipoPersona3';
															parametros += '&valParam3='
																	+ tipo;
															parametros += '&nomParam4=tipoPersona4';
															parametros += '&valParam4='
																	+ fecA;
															parametros += '&nomParam5=tipoPersona5';
															parametros += '&valParam5='
																	+ fecV;

															window
																	.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"
																			+ parametros);
														} else {
															Ext.Msg
																	.alert(
																			'SET',
																			" Debe seleccionar un tipo de persona");

														}// alert("hacer la
															// consulta de
															// compra con la
															// fecha inicio
															// fin");
													} else {

														var emp = Ext
																.getCmp(
																		PF
																				+ 'txtNoEmpresa')
																.getValue();
														parseInt(emp);
														var foco = false;
														var tipo = 2;
														var fecA = cambiarFecha(Ext
																.getCmp(
																		PF
																				+ 'txtFechaIni')
																.getValue()
																+ '')
																+ '';
														var fecV = cambiarFecha(Ext
																.getCmp(
																		PF
																				+ 'txtFechaFin')
																.getValue()
																+ '')
																+ '';
														if (emp == '') {
															emp = 1;
															parseInt(noEmp);

														}
														if (emp != "") {
															// Ext.Msg.alert(emp);
															parametros = '?nomReporte=excelFws';
															parametros += '&nomParam1=tipoPersona';
															parametros += '&valParam1='
																	+ emp;

															parametros += '&nomParam2=tipoPersona2';
															parametros += '&valParam2='
																	+ foco;
															parametros += '&nomParam3=tipoPersona3';
															parametros += '&valParam3='
																	+ tipo;
															parametros += '&nomParam4=tipoPersona4';
															parametros += '&valParam4='
																	+ fecA;
															parametros += '&nomParam5=tipoPersona5';
															parametros += '&valParam5='
																	+ fecV;

															window
																	.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"
																			+ parametros);
														} else {
															Ext.Msg
																	.alert(
																			'SET',
																			" Debe seleccionar un tipo de persona");

														}
													}

												} else {
													var tipo = NS.tipo.getValue();
													if (tipo==1) {
														
														var emp = Ext.getCmp(	PF+ 'txtNoEmpresa').getValue();
														parseInt(emp);
														var foco = true;
														var tipo = 1;

														var fecA = cambiarFecha(Ext
																.getCmp(
																		PF
																				+ 'txtFechaIni')
																.getValue()
																+ '')
																+ '';
														var fecV = cambiarFecha(Ext
																.getCmp(
																		PF
																				+ 'txtFechaFin')
																.getValue()
																+ '')
																+ '';
														// var fecV =Ext.getCmp(PF +
														// 'txtFechaFin').getValue();
														if (emp == '') {
															emp = 1;
															parseInt(noEmp);

														}
														if (emp != "") {
															// Ext.Msg.alert(emp);
															parametros = '?nomReporte=excelFws';
															parametros += '&nomParam1=tipoPersona';
															parametros += '&valParam1='
																	+ emp;

															parametros += '&nomParam2=tipoPersona2';
															parametros += '&valParam2='
																	+ foco;
															parametros += '&nomParam3=tipoPersona3';
															parametros += '&valParam3='
																	+ tipo;
															parametros += '&nomParam4=tipoPersona4';
															parametros += '&valParam4='
																	+ fecA;
															parametros += '&nomParam5=tipoPersona5';
															parametros += '&valParam5='
																	+ fecV;

															window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+ parametros);
														} else {
															Ext.Msg.alert('SET'," Debe seleccionar un tipo de persona");

														}
														
													} else {

														
														var emp = Ext.getCmp(	PF+ 'txtNoEmpresa').getValue();
														parseInt(emp);
														var foco = true;
														var tipo = 2;

														var fecA = cambiarFecha(Ext.getCmp(PF + 'txtFechaIni').getValue()+ '')+ '';
														var fecV = cambiarFecha(Ext.getCmp(PF + 'txtFechaFin').getValue() + '') + '';
														// var fecV =Ext.getCmp(PF +
														// 'txtFechaFin').getValue();
														if (emp == '') {
															emp = 1;
															parseInt(noEmp);

														}
														if (emp != "") {
															// Ext.Msg.alert(emp);
															parametros = '?nomReporte=excelFws';
															parametros += '&nomParam1=tipoPersona';
															parametros += '&valParam1=' + emp;
															parametros += '&nomParam2=tipoPersona2';
															parametros += '&valParam2=' + foco;
															parametros += '&nomParam3=tipoPersona3';
															parametros += '&valParam3=' + tipo;
															parametros += '&nomParam4=tipoPersona4';
															parametros += '&valParam4=' + fecA;
															parametros += '&nomParam5=tipoPersona5';
															parametros += '&valParam5=' + fecV;

															window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+ parametros);
														} else {
															Ext.Msg.alert('SET'," Debe seleccionar un tipo de persona");
														}

														
														
													}
													
											
												}
											}
										}
									}
								},
								{
									xtype : 'button',
									text : 'Eliminar',
									x : 760,
									y : 420,
									width : 80,
									height : 22,
									listeners : {
										click : {
											fn : function(e) {
												
												var registroSeleccionado= NS.gridDerivados.getSelectionModel().getSelections();
//												alert(registroSeleccionado.length); 
												if(registroSeleccionado.length > 0 ){
													var swap = '';
													var neto = '';
													var matriz = new Array();
														for(var i = 0; i < registroSeleccionado.length; i ++){
															var vector = {};
															vector.folio = registroSeleccionado[i].get('NO_FOLIO');
															vector.unidad_negocio = registroSeleccionado[i].get('NO_EMPRESA');
															vector.chequera_cargo = registroSeleccionado[i].get('ID_CHEQUERA_CARGO');
															vector.chequera_abono = registroSeleccionado[i].get('ID_CHEQUERA_ABONO');
															vector.importe_pago = registroSeleccionado[i].get('IMPORTE_PAGO');
															vector.importe_compra = registroSeleccionado[i].get('IMPORTE_FWS');
															vector.tc = registroSeleccionado[i].get('TIPO_CAMBIO');
															vector.fec_vto = registroSeleccionado[i].get('FEC_VENC');				
															vector.fec_compra=registroSeleccionado[i].get('FEC_ALTA');
															swap = registroSeleccionado[i].get('ESTATUS_SWAP');
															neto = registroSeleccionado[i].get('ID_NETEO');
															
															matriz[i] = vector;
														}	
														
														
														if (swap === 'N' || neto === 0) {
															var jSonString = Ext.util.JSON.encode(matriz);
										    				ForwardsAction.eliminaForward(jSonString,NS.idUsuario, function(result, e){
										    					 Ext.Msg.alert('SET', result);
										    					 NS.storeForwards.load();
										    					 NS.gridDerivados.getView().refresh();
										    				});
														}else{
															Ext.Msg.alert('SET', 'No es posible eliminar Forward con Swap o Neteo');
														}
														  
										    				
										    				
												}else{
													 Ext.Msg.alert('SET', 'Seleccione filas para eliminar');
												}	

											}
										}
									}
								},
								{
									xtype : 'button',
									text : 'Modificar',
									x : 670,
									y : 420,
									width : 80,
									height : 22,
									listeners : {
										click : {
											fn : function(e) {
												
												var registroSeleccionado= NS.gridDerivados.getSelectionModel().getSelections();
//												alert(registroSeleccionado.length); 
												if(registroSeleccionado.length > 0 ){
													var swap = '';
													var neto = '';
													var matriz = new Array();
														for(var i = 0; i < registroSeleccionado.length; i ++){
															var vector = {};
															vector.folio = registroSeleccionado[i].get('NO_FOLIO');
															vector.unidad_negocio = registroSeleccionado[i].get('NO_EMPRESA');
															vector.chequera_cargo = registroSeleccionado[i].get('ID_CHEQUERA_CARGO');
															vector.chequera_abono = registroSeleccionado[i].get('ID_CHEQUERA_ABONO');
															vector.importe_pago = registroSeleccionado[i].get('IMPORTE_PAGO');
															vector.importe_compra = registroSeleccionado[i].get('IMPORTE_FWS');
															vector.tc = registroSeleccionado[i].get('TIPO_CAMBIO');
															vector.fec_vto = registroSeleccionado[i].get('FEC_VENC');				
															vector.fec_compra=registroSeleccionado[i].get('FEC_ALTA');
															swap = registroSeleccionado[i].get('ESTATUS_SWAP');
															neto = registroSeleccionado[i].get('ID_NETEO');
															NS.divisa = registroSeleccionado[i].get('ID_DIVISA_COMPRA');
															matriz[i] = vector;
														}	
														
														
														if (swap === 'N' || neto === 0) {
															
															winEditarSF.show();
															winEditarSF.setTitle("Modificar Forward");
															
															
															
															NS.txtNoFolio.setValue(registroSeleccionado[0].get('NO_FOLIO'));
															NS.txtNoEmpresa2.setValue(registroSeleccionado[0].get('NO_EMPRESA'));
															NS.txtCheCargo.setValue(registroSeleccionado[0].get('ID_CHEQUERA_CARGO'));
															NS.txtCheAbono.setValue(registroSeleccionado[0].get('ID_CHEQUERA_ABONO'));	
															NS.txtSpot.setValue(registroSeleccionado[0].get('SPOT'));
															NS.txtPtsForward.setValue(registroSeleccionado[0].get('PUNTOS_FORWARD'));
															NS.txtTipoCambio.setValue(registroSeleccionado[0].get('TIPO_CAMBIO'));
															NS.txtImporteCompra.setValue(registroSeleccionado[0].get('IMPORTE_FWS'));
															NS.txtImportePago.setValue(registroSeleccionado[0].get('IMPORTE_PAGO'));
															
															NS.txtFechaAlta.setValue(registroSeleccionado[0].get('FEC_ALTA'));
															NS.txtFechaVenc.setValue(registroSeleccionado[0].get('FEC_VENC'));
															
//																											 	
														}else{
															Ext.Msg.alert('SET', 'No es posible modificar Forward con Swap o Neteo');
														}
														  
										    				
										    				
												}else{
													 Ext.Msg.alert('SET', 'Seleccione filas para modificar');
												}	

											}
										}
									}
								}
								
							
						]
					});
			
			//Ventana emergente para editar registro o nuevo registro
			 
			 NS.panelA = new Ext.form.FieldSet ({
					title: 'Datos Forward',
					x: 15,
					y: 0,
					width: 400,
					height: 400,
					layout: 'absolute',
					items: [
							NS.lbNoFolio,
							NS.lbNoEmpresa,
							NS.lbCheCargo,
							NS.lbCheAbono,
							NS.lbSpot,
							NS.lbPtosFws,
							NS.lbTipoCambio,
							NS.lbImpCompra,
							NS.lbImpVenta,
							NS.lbFecAlta,
							NS.lbFecCompra,
							
							NS.txtNoFolio,
							NS.txtNoEmpresa2,
							NS.txtCheCargo,
							NS.txtCheAbono,
							
							NS.txtSpot,
							NS.txtPtsForward,
							NS.txtTipoCambio,
							NS.txtImporteCompra,
							NS.txtImportePago,
							NS.txtFechaVenc,
						 	NS.txtFechaAlta
						 	
					]
			});
			 
			
			var winEditarSF = new Ext.Window({
				title: 'Forward',
				modal: true,
				shadow: true,
				closeAction: 'hide',
				width: 450,
			   	height: 450,
			   	layout: 'absolute',
			   	plain: true,
			   	resizable:true,
			   	draggable:true,
			   	closable:true,
			    bodyStyle: 'padding:0px;',
			    buttonAlign: 'center',
			    buttons:[
			             {text:'Aceptar',handler:function(){
			            	 var matriz = new Array();

			            	 var vector = {};
			            	 vector.folio = Ext.getCmp(PF + 'txtNoFolio').getValue();
			            	 vector.unidad_negocio = Ext.getCmp(PF + 'txtNoEmpresa2').getValue();
			            	 vector.chequera_cargo = Ext.getCmp(PF + 'txtCheCargo').getValue();
			            	 vector.chequera_abono = Ext.getCmp(PF + 'txtCheAbono').getValue();
			            	 vector.spot = Ext.getCmp(PF + 'txtSpot').getValue();
			            	 vector.pts_forward  = Ext.getCmp(PF + 'txtPtsForward').getValue();
			            	 vector.importe_pago = NS.unformatNumber(Ext.getCmp(PF + 'txtImportePago').getValue());
			            	 vector.importe_compra = Ext.getCmp(PF + 'txtImporteCompra').getValue();
			            	 vector.tc = Ext.getCmp(PF + 'txtTipoCambio').getValue();
			            	 vector.fec_vto = Ext.getCmp(PF + 'txtFechaVenc').getValue();				
			            	 vector.fec_compra = Ext.getCmp(PF + 'txtFechaAlta').getValue();
			            	 matriz[i] = vector;




			            	 var jSonString = Ext.util.JSON.encode(matriz);
			            	 ForwardsAction.modificaForward(jSonString,NS.idUsuario, function(result, e){
			            		 Ext.Msg.alert('SET', result);
			            		 winEditarSF.hide();
			            		 NS.storeForwards.load();
			            		 NS.gridDerivados.getView().refresh();
			            	 });





			             }
			             },
			             {text:'Cancelar',handler:function(){
			            	 	winEditarSF.hide();
			            	 	NS.storeForwards.load();
			            	 	NS.gridDerivados.getView().refresh();
			            	 } 
			             },
			    ],
			   	items: [
			   	        	NS.panelA,
			   	],
			    listeners:{
			    	 show:{
			    		 fn:function(){
			    			
			    		 }
			    	 }, 
			    	  hide:{
			    		  fn:function(){
			    			  NS.storeForwards.load();
			    			  NS.gridDerivados.getView().refresh();
			    		  }
			    	  }
			     } 
			});
			
			NS.derivados = new Ext.FormPanel(
					{
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