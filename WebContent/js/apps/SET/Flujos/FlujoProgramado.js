//************************INIT*******************************/
//@autor   :  JUAN RAMIRO BARRERA MARTINEZ
//@Method  :  OnReady
//@Objetive:  En el momento que el DOM este cargado, construir
//            la pantalla de seleccion de parametros, para la
//            definicion del reporte a consultar
//************************INIT*******************************/
Ext.onReady(function() {

			var NS = Ext.namespace('apps.SET.Flujos.FlujoProgramado');
			NS.tabContId = apps.SET.tabContainerId;
			var PF = apps.SET.tabID + '.';
			NS.idUsuario = apps.SET.iUserId;
			Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
			Ext.QuickTips.init();

			// ************************INIT************************************/
			// DECLARACION DE OBJETOS Ext.form.Label DEL FORMULARCION PRINCIPAL
			// ************************INIT************************************/

		
			NS.lblGrupoEmpresas = new Ext.form.Label({
				text : 'Grupo de empresa:',
				x : 30,
				y : 30,
				width : 130
			});

			NS.lblEmpresa = new Ext.form.Label({
				text : 'Empresa:',
				x : 30,
				y : 70
			});

			NS.lblFechaInicial = new Ext.form.Label({
				text : 'Fecha inicial',
				x : 30,
				y : 110
			});

			NS.lblFechaFinal = new Ext.form.Label({
				text : 'Fecha final:',
				x : 30,
				y : 150
			});

			NS.lblReporte = new Ext.form.Label({
				text : 'Reporte',
				x : 30,
				y : 190
			});

			// ***************************END*********************************/
			// DECLARACION DE OBJETOS Ext.form.Label DEL FORMULARCION PRINCIPAL
			// ***************************END*********************************/

			// ***************************INIT*************************************/
			// DECLARACION DE OBJETOS Ext.form.NumberField DEL FORMULARIO
			// PRINCIPAL
			// ***************************INIT*************************************/

			NS.txtNoGrupoEmpresas = new Ext.form.NumberField({
				name : PF + 'txtNoGrupoEmpresas',
				id : PF + 'txtNoGrupoEmpresas',
				x : 170,
				y : 30,
				width : 50,
				listeners : {
					change : {
						fn : function() {
							if (Ext.getCmp(PF + 'txtNoGrupoEmpresas').getValue() !== "") {

								var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+ 'txtNoGrupoEmpresas',NS.cmbGrupoEmpresas.getId());
								PF + 'txtNoGrupoEmpresas'.focus();

							} else {

								Ext.getCmp(NS.cmbGrupoEmpresas.getId())
										.setValue('');
								Ext.getCmp(NS.txtNoEmpresa.getId())
										.setValue('');
								Ext.getCmp(NS.cmbEmpresa.getId()).setValue('');
								NS.storeEmpresa.removeAll();

							}// End if

						}// End fn

					}
				// End change

				}
			// End listeners

			});// NS.txtNoGrupoEmpresas

			NS.txtNoEmpresa = new Ext.form.NumberField(
					{
						name : PF + 'txtNoEmpresa',
						id : PF + 'txtNoEmpresa',
						x : 170,
						y : 70,
						width : 50,
						listeners : {
							change : {
								fn : function() {

									if (Ext.getCmp(PF + 'txtNoEmpresa') .getValue() !== "") {
										// alert(NS.cmbEmpresa.getId());
										var valueCombo = BFwrk.Util .updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
										PF + 'txtNoEmpresa'.focus();

									} else {
										// alert("En ELSE");
										Ext.getCmp(NS.cmbEmpresa.getId()) .setValue('');

									}// End if

								}// End fn

							}
						// End change
						}
					// End listeners
					});// End NS.txtNoEmpresa

			// ***************************END**************************************/
			// DECLARACION DE OBJETOS Ext.form.NumberField DEL FORMULARIO
			// PRINCIPAL
			// ****************************END*************************************/

			// ***************************INIT***********************************/
			// DEFINICION DE OBJETOS Ext.form.DateField DEL FORMULARIO PRINCIPAL
			// ***************************INIT***********************************/

			NS.txdFechaInicial = new Ext.form.DateField({
				x : 170,
				y : 110,
				width : 130,
				value : apps.SET.FEC_HOY,
				format : 'd/m/Y',
				name : PF + 'txdFechaInicial',
				id : PF + 'txdFechaInicial'
			});

			NS.txdFechaFinal = new Ext.form.DateField({
				x : 170,
				y : 150,
				width : 130,
				value : apps.SET.FEC_HOY,
				format : 'd/m/Y',
				name : PF + 'txdFechaFinal',
				id : PF + 'txdFechaFinal'
			});

			// ***************************END***********************************/
			// DEFINICION DE OBJETOS Ext.form.DateField DEL FORMULARIO PRINCIPAL
			// ***************************END***********************************/

			// ***************************INIT**********************************/
			// DEFINICION DE OBJETOS Ext.data.DirectStore y Ext.form.ComboBox
			// DEL FORMULARIO PRINCIPAL
			// ***************************INIT**********************************/
			NS.storeGrupoEmpresas = new Ext.data.DirectStore({
				paramsAsHash : false,
				directFn : CashFlowActionImplements.getGrupoEmpresasService,
				root : '',
				idProperty : 'id_super_grupo',
				fields : [ {
					name : 'id_super_grupo'
				}, {
					name : 'desc_super_grupo'
				} ]
			});

			NS.storeGrupoEmpresas.load();

			NS.cmbGrupoEmpresas = new Ext.form.ComboBox(
					{
						name : PF + 'cmbGrupoEmpresas',
						id : PF + 'cmbGrupoEmpresas',
						store : NS.storeGrupoEmpresas,
						mode : 'local',
						valueField : 'id_super_grupo',
						displayField : 'desc_super_grupo',
						emptyText : 'Seleccione un Grupo',
						triggerAction : 'all',
						x : 230,
						y : 30,
						width : 310,
						listeners : {
							select : {
								fn : function() {
									BFwrk.Util.updateComboToTextField(PF + 'txtNoGrupoEmpresas',NS.cmbGrupoEmpresas.getId());
									Ext.getCmp(NS.txtNoEmpresa.getId()).setValue('');
									Ext.getCmp(NS.cmbEmpresa.getId()).setValue('');
									NS.storeEmpresa.removeAll();
									NS.storeEmpresa.baseParams.idGrupo = NS.txtNoGrupoEmpresas.getValue();
									NS.storeEmpresa.baseParams.noUsuario = NS.idUsuario
									NS.storeEmpresa.sort('NO_EMPRESA', 'ASC');
									Ext.getCmp(NS.cmbEmpresa.getId()).setValue('');
									NS.storeEmpresa.load();
								}// end function

							},// end select
							change : {
								fn : function(combo, newValue, oldValue) {

									if (newValue == "") {
										// alert("change de Grupo");
										Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).setValue('');
										Ext.getCmp(NS.txtNoEmpresa.getId()).setValue('');
										Ext.getCmp(NS.cmbEmpresa.getId()).setValue('');
										NS.storeEmpresa.removeAll();

									}// end if

								}// end function

							}
						// end change

						}
					// end listeners

					});// NS.cmbGrupoEmpresas

			NS.storeEmpresa = new Ext.data.DirectStore({
				paramsAsHash : false,
				baseParams : {
					idGrupo : 0,
					noUsuario : 2
				},
				root : '',
				paramOrder : [ 'idGrupo', 'noUsuario' ],
				directFn : CashFlowActionImplements.getEmpresasService,
				idProperty : 'noEmpresa',
				fields : [ {
					name : 'noEmpresa'
				}, {
					name : 'nomEmpresa'
				} ],
				listeners : {
					load : function(s, records) {
						var myMask = new Ext.LoadMask(Ext.getBody(), {
							store : NS.storeEmpresa,
							msg : "Cargando..."
						});
					}// end function
				}
			// end listeners

			});

			NS.cmbEmpresa = new Ext.form.ComboBox({
				name : PF + 'cmbEmpresa',
				id : PF + 'cmbEmpresa',
				store : NS.storeEmpresa,
				mode : 'local',
				valueField : 'noEmpresa',
				displayField : 'nomEmpresa',
				emptyText : 'Seleccione una empresa',
				triggerAction : 'all',
				x : 230,
				y : 70,
				width : 310,
				listeners : {
					select : {
						fn : function() {
							BFwrk.Util.updateComboToTextField(PF+ 'txtNoEmpresa', NS.cmbEmpresa.getId());
						}// end function
					}
				/*
				 * ,//end select
				 * 
				 * change:{ fn:function(combo, newValue, oldValue) {
				 * 
				 * if( newValue == "" ){
				 * 
				 * Ext.getCmp(NS.txtNoEmpresa.getId()).setValue(''); }//end if
				 * 
				 * }//end function
				 *  }
				 */// end change
				}
			// end listeners
			});

			NS.storeReportesFlujo = new Ext.data.DirectStore({
				paramsAsHash : false,
				directFn : CashFlowActionImplements.getReportesFlujoService,
				root : '',
				idProperty : 'id',
				fields : [ {
					name : 'id'
				}, {
					name : 'descripcion'
				} ]
			});

			NS.storeReportesFlujo.load();

			NS.cmbReporte = new Ext.form.ComboBox({
				store : NS.storeReportesFlujo,
				mode : 'local',
				valueField : 'id',
				displayField : 'descripcion',
				emptyText : 'Seleccione un reporte',
				triggerAction : 'all',
				x : 170,
				y : 190,
				width : 310,
				name : PF + 'cmbReporte',
				id : PF + 'cmbReporte'
			});
			// ***************************END**********************************/
			// DEFINICION DE OBJETOS Ext.data.DirectStore y Ext.form.ComboBox
			// DEL FORMULARIO PRINCIPAL
			// ***************************END**********************************/

			// ***************************INIT**********************************/
			// DEFINICION DE OBJETOS Ext.Button DEL FORMULARIO PRINCIPAL
			// ***************************INIT**********************************/

			// @Objetive: Limpiar los campos del formulario principal

			NS.cmdLimpiar = new Ext.Button({
				text : 'Limpiar',
				x : 410,
				y : 240,
				width : 80,
				listeners : {
					click : {
						fn : function() {
							Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).reset();
							Ext.getCmp(NS.cmbGrupoEmpresas.getId()).reset();
							Ext.getCmp(NS.txtNoEmpresa.getId()).reset();
							Ext.getCmp(NS.cmbEmpresa.getId()).reset();
							Ext.getCmp(NS.cmbReporte.getId()).reset();
							NS.txdFechaInicial.setValue(apps.SET.FEC_HOY);
							NS.txdFechaFinal.setValue(apps.SET.FEC_HOY);
							NS.txtNoGrupoEmpresas.focus();
						}// end function

					}
				// end click

				}
			// end listeners

			});// end NS.cmdLimpiar

			// @Objetive: VALIDAR LOS CAMPOS DEL FORMULARIO PRINCIPAL

			NS.validar = function() {

				this.val = false;

				var pr = new NS.paramsReporte();
				// alert("empresa "+pr.noEmpresa + " grupo "+pr.grupoEmpresa +"
				// reporte "+pr.reporte);
				// alert("EMPRESA "+pr.noEmpresa);
				if (pr.grupoEmpresa == '' || pr.reporte == '') {
					this.val = true;
					Ext.Msg.alert('WEBSET','NO SE HAN SELECCIONADO TODOS LOS CAMPOS.');
				}
				/*
				 * if ( pr.fechaFinal < pr.fechaInicial ){ this.val = true;
				 * Ext.Msg.alert('WEBSET','LA FECHA FINAL NO DEBE SER MENOR QUE
				 * LA INICIAL.'); }
				 */
				if (pr.fechaFinal == pr.fechaInicial) {
					this.val = true;
					Ext.Msg.alert('WEBSET', 'SELECCIONE UN RANGO DE FECHAS.');
				}

				return this.val;

			};// END NS.validar

			// @Objetive: OBTENER LOS PARAMETROS SELECCIONADOS EL FORMULARIO
			// PRINCIPAL

			NS.paramsReporte = function() {

				this.grupoEmpresa = Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).getValue();
				this.noEmpresa = Ext.getCmp(NS.txtNoEmpresa.getId()).getValue();
				this.fechaInicial = cambiarFecha(''+ Ext.getCmp(NS.txdFechaInicial.getId()).getValue());
				this.fechaFinal = cambiarFecha(''+ Ext.getCmp(NS.txdFechaFinal.getId()).getValue());
				this.reporte = Ext.getCmp(NS.cmbReporte.getId()).getValue();
				// Ext.Msg.alert('datos ',this.grupoEmpresa + this.noEmpresa +
				// this.fechaInicial + this.fechaFinal +this.reporte );
			};// END NS.paramsReporte

			// Funcion para graficar los Flujos de Efectivo reales. ingresos -
			// egresos
			NS.graficaFlujoEfectivoReal = function() {

				if (Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).getValue() == ''|| Ext.getCmp(NS.cmbReporte.getId()).getValue() == '') {
					BFwrk.Util.msgShow('Para graficar se requiere la empresa, el rango de fechas y tipo de reporte.','INFO');
				} else {
					NS.storeGraficaFlujoEfectivoReal.baseParams.noEmpresa = parseInt(Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).getValue());
					NS.storeGraficaFlujoEfectivoReal.baseParams.fechaInicial = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF + 'txdFechaInicial').getValue());
					NS.storeGraficaFlujoEfectivoReal.baseParams.fechaFinal = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF + 'txdFechaFinal').getValue());
					NS.storeGraficaFlujoEfectivoReal.baseParams.idReporte = parseInt(Ext.getCmp(PF + 'cmbReporte').getValue());
					NS.storeGraficaFlujoEfectivoReal.load();
				}
			};
			// Funcion para graficar la comparacion Ingresos/Egresos de flujo de
			// efectivo real.
			NS.graficaFEComparativoReal = function() {
				if (Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).getValue() == ''|| Ext.getCmp(NS.cmbReporte.getId()).getValue() == '') {
					BFwrk.Util.msgShow('Para graficar se requiere la empresa, el rango de fechas y tipo de reporte.','INFO');
				} else {
					NS.storeGraficaFEComparativoReal.baseParams.noEmpresa = parseInt(Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).getValue());
					NS.storeGraficaFEComparativoReal.baseParams.fechaInicial = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF + 'txdFechaInicial').getValue());
					NS.storeGraficaFEComparativoReal.baseParams.fechaFinal = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF + 'txdFechaFinal').getValue());
					NS.storeGraficaFEComparativoReal.baseParams.idReporte = parseInt(Ext.getCmp(PF + 'cmbReporte').getValue());
					NS.storeGraficaFEComparativoReal.load();
				}
			};
			// Store para grafica flujo efectivo real. Ingresos - Egresos.
			NS.storeGraficaFlujoEfectivoReal = new Ext.data.DirectStore(
					{
						paramsAsHash : false,
						baseParams : {
							noEmpresa : 0,
							fechaInicial : '',
							fechaFinal : '',
							idReporte : 0,
							iUserId : NS.idUsuario
						},
						root : '',
						paramOrder : [ 'noEmpresa', 'fechaInicial',
								'fechaFinal', 'idReporte', 'iUserId' ],
						directFn : CashFlowActionImplements.graficarFlujoEfectivo,
						fields : [],
						listeners : {
							load : function(s, records) {
								var myMask = new Ext.LoadMask(Ext.getBody(), {
									store : NS.storeGraficaFlujoEfectivo,
									msg : "Graficando..."
								});

								if (records == null || records.length == 0) {
									Ext.Msg
											.alert('Información  SET',
													'No se encontraron datos a graficar con los parametros de búsqueda');
									return;
								} else {
									var sAux1 = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF+ 'txdFechaInicial').getValue()).replace("/", "");
									sAux1 = sAux1.replace("/", "");
									var sAux2 = BFwrk.Util.changeDateToString(+ Ext.getCmp(PF+ 'txdFechaFinal').getValue()).replace("/", "");
									sAux2 = sAux2.replace("/", "");

									sName = Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).getValue()+ ""+ sAux1+ sAux2+ Ext.getCmp(PF + 'cmbReporte').getValue();

									Ext.getCmp('panel3').show();
									Ext.getCmp('panel2').show();
									Ext.getCmp('panel1').show();
									Ext.getCmp('panel1').update(' '); // <img
																		// src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'FlujoEfectivo'+sName+'PC.jpg"
																		// border="0"/>
									Ext.getCmp('panel2').update('<img src="'+ window.location.protocol+ '//'+ window.location.host+ '/graficaSet/'+ apps.SET.iUserId+ 'FlujoEfectivo' + sName+ 'BG.jpg" border="0"/>');
									Ext.getCmp('panel3').update('<img src="'+ window.location.protocol+ '//'+ window.location.host+ '/graficaSet/'+ apps.SET.iUserId+ 'FlujoEfectivo' + sName+ 'LG.jpg" border="0"/>');
									Ext.Msg.alert('SET', 'Graficas listas');
								}
							}
						}
					});
			// Store para grafica flujo efectivo real. Ingresos - Egresos.
			NS.storeGraficaFEComparativoReal = new Ext.data.DirectStore(
					{
						paramsAsHash : false,
						baseParams : {
							noEmpresa : 0,
							fechaInicial : '',
							fechaFinal : '',
							idReporte : 0,
							iUserId : NS.idUsuario
						},
						root : '',
						paramOrder : [ 'noEmpresa', 'fechaInicial',
								'fechaFinal', 'idReporte', 'iUserId' ],
						directFn : CashFlowActionImplements.graficarFEComparativoReal,
						fields : [],
						listeners : {
							load : function(s, records) {
								var myMask = new Ext.LoadMask(Ext.getBody(), {
									store : NS.storeGraficaFEComparativoReal,
									msg : "Graficando..."
								});

								if (records == null || records.length == 0) {
									Ext.Msg.alert('Información  SET','No se encontraron datos a graficar con los parametros de búsqueda');
									return;
								} else {
									var sAux1 = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF+ 'txdFechaInicial').getValue()).replace("/", "");
									sAux1 = sAux1.replace("/", "");
									var sAux2 = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF+ 'txdFechaFinal').getValue()).replace("/", "");
									sAux2 = sAux2.replace("/", "");

									sName = Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).getValue()+ ""+ sAux1+ sAux2+ Ext.getCmp(PF + 'cmbReporte').getValue();

									Ext.getCmp('panel3').show();
									Ext.getCmp('panel2').show();
									Ext.getCmp('panel1').show();
									Ext.getCmp('panel1').update(' '); // <img
																		// src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'FlujoEfectivo'+sName+'PC.jpg"
																		// border="0"/>
									Ext.getCmp('panel2').update('<img src="'+ window.location.protocol+ '//'+ window.location.host+ '/graficaSet/'+ apps.SET.iUserId + 'FECompReal' + sName+ 'BG.jpg" border="0"/>');
									Ext.getCmp('panel3').update('<img src="'+ window.location.protocol+ '//'+ window.location.host+ '/graficaSet/'+ apps.SET.iUserId+ 'FECompReal' + sName+ 'LG.jpg" border="0"/>');
									Ext.Msg.alert('SET', 'Graficas listas');
								}
							}
						}
					});

			// @Objetive: MANDAR LLAMAR LA VALIDACION DE CAMPOS Y EN CASO DE QUE
			// PASE LA VALIDACION
			// MANDAR LLAMAR EL METODO DE EMISION DEL REPORTE

			NS.cmdEnviar = new Ext.Button({
				text : 'Consultar',
				x : 310,
				y : 240,
				id : 'CONSULTAR',
				width : 80,
				listeners : {
					click : {
						fn : function() {

							var e = new NS.validar();

							if (e.val) {
								return;
							}

							var p = new NS.paramsReporte();
							
							displayTheWindow(p.grupoEmpresa, p.noEmpresa,
									p.fechaInicial, p.fechaFinal, p.reporte);

						}// end function

					}
				// end click
				}
			// end listeners

			});// end NS.cmdEnviar

			// Boton para graficar
			NS.cmdGraficar = new Ext.Button({
				text : 'Graficar',
				x : 120,
				y : 240,
				name : PF + 'cmdGrafica',
				id : PF + 'cmdGrafica',
				width : 80,
				listeners : {
					click : {
						fn : function(e) {
							// NS.graficaFlujoEfectivoReal();
							NS.graficaFEComparativoReal();
						}
					}
				}
			});// end NS.cmdGraficar
			NS.cmdExcel = new Ext.Button(
					{
						text : 'Excel',
						x : 210,
						y : 240,
						name : PF + 'cmdExcel',
						id : PF + 'cmdExcel',
						width : 80,

						listeners : {
							click : {
								fn : function() {
									var e = new NS.validar();
						 
 										 if (e.val) {
 											 return;}
 											 var p = new NS.paramsReporte();
 											displayTheWindow2(p.grupoEmpresa, p.noEmpresa,
 											p.fechaInicial, p.fechaFinal, p.reporte);
 					
 											  
 											if(p.reporte == 7 || p.reporte == 6){
 												setTimeout(NS.excel,30000); 
 											}else{
 												setTimeout(NS.excel,10000);
 											}			 

								}// end function

							}
						// end click

						}
					// end listeners

					});
			// ***************************END**********************************/
			// DEFINICION DE OBJETOS Ext.Button DEL FORMULARIO PRINCIPAL
			// ***************************END**********************************/

			NS.FrmFlujoEfectivo = new Ext.FormPanel({
				title : 'Flujo de Efectivo',
				width : 587,
				height : 308,
				frame : true,
				layout : 'absolute',
				style : 'font-family:"Verdana";font-size:0.85em;',
				renderTo : NS.tabContId,
				items : [ NS.lblGrupoEmpresas, NS.lblFechaInicial,
						NS.lblEmpresa, NS.lblFechaFinal, NS.lblReporte,
						NS.txtNoGrupoEmpresas, NS.txtNoEmpresa,
						NS.txdFechaInicial, NS.txdFechaFinal,
						NS.cmbGrupoEmpresas, NS.cmbEmpresa, NS.cmbReporte
						/*NS.cmdGraficar*/, NS.cmdEnviar, NS.cmdExcel,
						NS.cmdLimpiar ]
			});// END NS.FrmFlujoEfectivo

			/* UTILERIAS */      
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
			/* UTILERIAS */

			// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
			// NOTA: LAS FUNCIONES DEFINIDAS A CONTINUACION, SE ENCUENTRAN FUERA
			// DEL METODO ONREADY, DEBIDO A QUE ESTA FUNCIONALIDAD NO SE PUEDE
			// EJECUTAR
			// DENTRO DE LA DECLARACION DEL CUERPO DE DICHA FUNCION.
			// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
			// ************************INIT***********************************/
			// @autor : JUAN RAMIRO BARRERA MARTINEZ
			// @Method : displayTheWindow
			// @Objetive : Desplegar una ventana, llamar metodos de negocio
			// y presentar un grid con la informacion de flujo
			// ************************INIT**********************************/
			function displayTheWindow(grupoEmpresa, noEmpresa, fechaInicial,
					fechaFinal, reporte) {

				var columnasInhabiles = new Array();

				// @Objetive: MANDAR LLAMAR EL METODO DE NEGOCIO,
				// CashFlowActionImplements.getBodyReportService PARA EXTRAER LA
				// INFORMACION QUE
				// CORRESPONDERA A LA INFORMACION DEL REPORTE
// @NOTE : 1.- EN LA PROPIEDAD DE CONFIGURACION FIELS, SE MANDA LLAMAR UNA
// FUNCION QUE DETERMINA MEDIANTE UNA LLAMADA A UN METODO
// DEL SERVIDOR LA ESTRUCTURA QUE DEFINE LA PROPIEDAD FIELS
// @NOTE : 2.-

storeReporte = new Ext.data.DirectStore({
							paramsAsHash : false,
							root : "row",
							baseParams : {
								idGrupo : 1,
								noEmpresa : 1,
								fechaInicial : apps.SET.FEC_HOY,
								fechaFinal : apps.SET.FEC_HOY,
								idReporte : 0
							},
							fields : makeStructure(grupoEmpresa, noEmpresa,
									fechaInicial, fechaFinal, reporte),
							paramOrder : [ 'idGrupo', 'noEmpresa',
									'fechaInicial', 'fechaFinal', 'idReporte'],
							directFn : CashFlowActionImplements.getBodyReportService,
						 
							listeners : {

								load : function(store, records, options) {

									var fieldValue;
									var iContInhabil = 0;
									var iContVec = 0;
									var iIndexRecord = 0;
									var iIndexInhabil = 0;

									// ALMACENA EN UN VECTOR LAS COLUMANS CON
									// BANDERA
									// DE DIA INHABIL, CON EL OBJETO DE ELIMINAR
									// OCULTAR

									storeReporte.each(function(record) {
										
												iIndexRecord = iIndexRecord + 1;

												if (reporte == 1) // FLUJO
																	// DIARIO
																	// REAL
																	// AJUSTADO
												{ 
													if (record.get('col1') == 'FEC_NO_DIA') {
														iIndexInhabil = iIndexRecord;

														record.fields .each(function( field) { 
																	iContInhabil = iContInhabil + 1;
																	fieldValue = record .get(field.name);
																	if (fieldValue != '1000' && fieldValue != '10000' && fieldValue != 'FEC_NO_DIA' && fieldValue != 'NO DIA') { 
																		columnasInhabiles[iContVec] = iContInhabil;  
																		iContVec = iContVec + 1;
																	}// end
																		// if

																});// end
																	// record.fields.each

													}// end if

												}// end if reporte == 1

												if (reporte == 2 || reporte == 3 || reporte == 4 || reporte == 5) // FLUJO
																			// MENSUAL
																			// AJUSTADO
												{

													if (record.get('col1') == 'FEC_NO_DIA') {
														iIndexInhabil = iIndexRecord;

														record.fields.each(function(
																		field) {

																	iContInhabil = iContInhabil + 1;
																	fieldValue = record
																			.get(field.name);
																	if (fieldValue != '1000'&& fieldValue != '10000'&& fieldValue != 'FEC_NO_DIA'&& fieldValue != 'NO DIA') {
																		columnasInhabiles[iContVec] = iContInhabil;
																		iContVec = iContVec + 1;
																	}// end
																		// if

																});// end
																	// record.fields.each

													}// end if

												}// end if reporte == 2

												if (reporte == 6) // FLUJO
																	// MENSUAL
																	// AJUSTADO
												{

													if (record.get('col1') == 'FEC_NO_DIA') {
														iIndexInhabil = iIndexRecord;

														record.fields.each(function(
																		field) {

																	iContInhabil = iContInhabil + 1;
																	fieldValue = record.get(field.name);
																	if (fieldValue != '1000'&& fieldValue != 'FEC_NO_DIA'&& fieldValue != 'NO DIA') {
																		columnasInhabiles[iContVec] = iContInhabil;
																		iContVec = iContVec + 1;
																	}// end
																		// if

																});// end
																	// record.fields.each

													}// end if

												}// end if reporte == 2

												if (reporte == 7 || reporte == 8) // FLUJO
																	// MENSUAL
																	// AJUSTADO
												{

													if (record.get('col1') == 'FEC_NO_DIA') {
														iIndexInhabil = iIndexRecord;

														record.fields .each(function( field) {
																	iContInhabil = iContInhabil + 1;
																	fieldValue = record.get(field.name);
																	if (fieldValue != '100' && fieldValue != '1000' && fieldValue != '10000' && fieldValue != '10000000' && fieldValue != 'FEC_NO_DIA'&& fieldValue != 'NO DIA') {
																		columnasInhabiles[iContVec] = iContInhabil;
																		iContVec = iContVec + 1;
																	}// end
																		// if

																});// end
																	// record.fields.each

													}// end if

												}// end if reporte == 7

											});// end function each

									// ELIMINA LA FILA DONDE SE ENCUENTRAN
									// REGISTRADAS LAS BANDERAS DE FEC INHABIL

									if (iIndexInhabil > 0) {
										storeReporte.removeAt(iIndexInhabil - 1);
									}

									var cm = gridDatos.getColumnModel();
									 
									
									// EN BASE AL REGISTRO REALIZADO EN EL
									// VECTOR
									// SE OCULTAN LAS COLUMNAS REGISTRADAS

									for (var x = 0; x < (columnasInhabiles.length); x = x + 1) { 
								 	cm.setHidden(columnasInhabiles[x] - 1, true); 
								 	
									}

									// SE OCULTA LA COLUMNA CERO DONDE SE
									// MANEJAN LOS IDS
									// DE LAS FILAS
							 	cm.setHidden(0, true);

									// SE ESTABLECE UN ANCHO PARA LAS COLUMAS
									// for( var y=0;y < cm.getColumnCount(); y =
									// y + 1)
									// {
									if(reporte==1 || reporte==2 || reporte==3|| reporte==4 || reporte==5 ||reporte==6 ){
										cm.setColumnWidth(1, 255);
										 
									}else if(reporte==7 || reporte == 8){ 
										cm.setColumnWidth(1, 275);
									}
									// }

									// SE ESTABLECE LA PROPIEDAD DE AUTO
									// APARICION DEL SCROLL
									gridDatos.setAutoScroll(false);
									

								}// END LOAD

							}
						// END LISTENERS

						});// END storeReporte = new Ext.data.DirectStore({



// @Objetive: PRESENTAR LA INFORMACION RETORNADA DE LOS METODOS DE NEGOCIO DEL
// SERVIDOR Y CONTENIDA EN EL STORE storeReporte
// @NOTE : 1.- EN LA PROPIEDAD DE CONFIGURACION COLUMS, SE MANDA LLAMAR UNA
// FUNCION QUE DETERMINA MEDIANTE UNA LLAMADA A UN METODO
// DEL SERVIDOR LA ESTRUCTURA QUE DEFINE LA PROPIEDAD COLUMS, ES LA MISMA
// FUNCION QUE SE MANDA LLAMAR EN LA PROPIEDAD FIELS
// DEL STORE, DEBIDO A QUE DEBEN TENER LA MISMA DEFINICION DE CAMPOS Y COLUMAS
//creamos el texfield antes de crear el grid

gridDatos = new Ext.grid.GridPanel({
					store : storeReporte,
					height : 220,
					width : 380, 
					x : 5,
					y : 20,
					title : '',  
					columns : makeStructure(grupoEmpresa, noEmpresa,
							fechaInicial, fechaFinal, reporte),
					
					viewConfig : {

						// CONFIGURA LAS OPCIONES DE COLORES DE REGISTROS EN
						// BASE A VALORES DETERMINADOS
						// EN SU COLUMNA 1 DE ID

						getRowClass : function(record, index) {
							var c = record.get('col1');
							if (c == 'R-S' || c == 'TI' || c == 'TE'|| c == 'SOA' || c == 'SD') {
								return 'price-fall';
							} else if (c.indexOf('TC', 0) > -1) {
								return 'price-1';
							} else if (c.indexOf('SUB', 0) > -1) {
								return 'price-2';
							}  else {
								return 'price-rise';
							}// end if

						}// end getRowClass

					}
				// end viewConfig

				}// end gridDatos = new Ext.grid.GridPanel({

				);

				storeReporte.baseParams.idGrupo = grupoEmpresa;
				storeReporte.baseParams.noEmpresa = noEmpresa;
				storeReporte.baseParams.fechaInicial = fechaInicial;
				storeReporte.baseParams.fechaFinal = fechaFinal;
				storeReporte.baseParams.idReporte = reporte;
				storeReporte.load();

				// @Objetive: VENTANA EN LA CUAL SE PRESENTARA EL GRID
				// QUE CONTENDRA LA DEFINICION DEL CUERPO DEL
				// REPORTE SOLICITADO

				// alert(Ext.getBody().getHeight());

				var altura = Ext.getBody().getHeight();

				var winLogin = new Ext.Window({
					/*
					 * title: 'Posición Bancaria' ,
					 */modal : true,
					shadow : true,
					height : altura,
					
					width : '100%',
					resizable : false,
					layout : 'fit',
					plain : true,
					bodyStyle : 'padding:10px;',
					buttonAlign : 'center',
					items : [ gridDatos ],
					 
				});
				winLogin.setTitle(Ext.getCmp(NS.cmbReporte.getId()).getRawValue());
				winLogin.show();
				 
				// PAEE Control que mantiene columna inicial dija solo con
				// scroll vertical,
				// La columna debe esta identificada con la clase .x-grid3-td-1
				// El contenedor se identifica como .x-grid3-scroller
				// Para empleo generico solo se requeire cambio de estas clases
				$('.x-grid3-scroller').scroll( function() {
									if (reporte == 1 || reporte == 7) {
										$('.x-grid3-td-1').attr('style','width: 248px;');
										$('.x-grid3-td-1').each( function(index, el) {
															if ($('.x-grid3-scroller').offset().top < $(el).offset().top) {
																var style = $(el).attr('style')+ 'position:fixed;';
																for (i = index; i <= index + 19; i++) {
																	var lista = $('.x-grid3-td-1');
																	var vTop = $(lista[i]).offset().top;
																	var vLeft = $(lista[i]).offset().left;
																	$(lista[i]).attr('style',style);
																	if ((index == 1 && (index + 19) < 24)||((index + 19) == lista.lenght && index > (lista.length - 20)))
																		$(lista[i]).offset({top : vTop});
																	else
																		$(lista[i]).offset({top : vTop});
																}
																return false;
															}
														})
									}
								})
							 

			}

			function displayTheWindow2(grupoEmpresa, noEmpresa, fechaInicial,
					fechaFinal, reporte) {

				var columnasInhabiles = new Array();

				// @Objetive: MANDAR LLAMAR EL METODO DE NEGOCIO,
				// CashFlowActionImplements.getBodyReportService PARA EXTRAER LA
				// INFORMACION QUE
				// CORRESPONDERA A LA INFORMACION DEL REPORTE
				// @NOTE : 1.- EN LA PROPIEDAD DE CONFIGURACION FIELS, SE MANDA
				// LLAMAR UNA FUNCION QUE DETERMINA MEDIANTE UNA LLAMADA A UN
				// METODO
				// DEL SERVIDOR LA ESTRUCTURA QUE DEFINE LA PROPIEDAD FIELS
				// @NOTE : 2.-

				storeReporte = new Ext.data.DirectStore(
						{
							paramsAsHash : false,
							root : "row",
							hidden : true,
							baseParams : {
								idGrupo : 1,
								noEmpresa : 1,
								fechaInicial : apps.SET.FEC_HOY,
								fechaFinal : apps.SET.FEC_HOY,
								idReporte : 0
							},
							fields : makeStructure(grupoEmpresa, noEmpresa,
									fechaInicial, fechaFinal, reporte),
							paramOrder : [ 'idGrupo', 'noEmpresa',
									'fechaInicial', 'fechaFinal', 'idReporte' ],
							directFn : CashFlowActionImplements.getBodyReportService2,
							hidden : true,
							listeners : {

								load : function(store, records, options) {
									// alert(records[0].get());
									var fieldValue;
									var iContInhabil = 0;
									var iContVec = 0;
									var iIndexRecord = 0;

									var iIndexInhabil = 0;

									// ALMACENA EN UN VECTOR LAS COLUMANS CON
									// BANDERA
									// DE DIA INHABIL, CON EL OBJETO DE ELIMINAR
									// OCULTAR

									storeReporte
											.each(function(record) {

												iIndexRecord = iIndexRecord + 1;

												if (reporte == 1) // FLUJO
																	// DIARIO
																	// REAL
																	// AJUSTADO
												{
													/*
													 * if( record.get('col1') ==
													 * 'FEC_HABIL' ){
													 * iIndexInhabil =
													 * iIndexRecord;
													 * 
													 * record.fields.each(function(field) {
													 * iContInhabil =
													 * iContInhabil + 1;
													 * fieldValue =
													 * record.get(field.name);
													 * if(fieldValue == 'N'){
													 * columnasInhabiles[iContVec] =
													 * iContInhabil; iContVec =
													 * iContVec + 1; }//end if
													 * });//end
													 * record.fields.each
													 * 
													 * }//end if
													 */
													if (record.get('col1') == 'FEC_NO_DIA') {
														iIndexInhabil = iIndexRecord;

														record.fields
																.each(function(
																		field) {

																	iContInhabil = iContInhabil + 1;
																	fieldValue = record.get(field.name);
																	if (fieldValue != '1000'&& fieldValue != '10000'&& fieldValue != 'FEC_NO_DIA'&& fieldValue != 'NO DIA') {
																		columnasInhabiles[iContVec] = iContInhabil;
																		record.remove(iContInhabil);
																		iContVec = iContVec + 1;
																	}// end
																		// if

																});// end
																	// record.fields.each

													}// end if

												}// end if reporte == 1

												if (reporte == 2|| reporte == 3|| reporte == 4|| reporte == 5 || reporte == 8) // FLUJO
																			// MENSUAL// AJUSTADO
												{

													if (record.get('col1') == 'FEC_NO_DIA') {
														iIndexInhabil = iIndexRecord;

														record.fields
																.each(function(
																		field) {

																	iContInhabil = iContInhabil + 1;
																	fieldValue = record.get(field.name);
																	if (fieldValue != '1000'&& fieldValue != '10000'&& fieldValue != 'FEC_NO_DIA'&& fieldValue != 'NO DIA') {
																		columnasInhabiles[iContVec] = iContInhabil;
																		record.remove(iContInhabil);
																		iContVec = iContVec + 1;
																	}// end
																		// if

																});// end
																	// record.fields.each

													}// end if

												}// end if reporte == 2

												if (reporte == 6) // FLUJO
																	// MENSUAL
																	// AJUSTADO
												{

													if (record.get('col1') == 'FEC_NO_DIA') {
														iIndexInhabil = iIndexRecord;

														record.fields
																.each(function(
																		field) {
																	iContInhabil = iContInhabil + 1;
																	fieldValue = record.get(field.name);
																	if (fieldValue != '1000'&& fieldValue != 'FEC_NO_DIA'&& fieldValue != 'NO DIA') {columnasInhabiles[iContVec] = iContInhabil;
																		record.remove(iContInhabil);
																		iContVec = iContVec + 1;
																	}// end
																		// if

																});// end
																	// record.fields.each

													}// end if

												}// end if reporte == 2

												if (reporte == 7) // FLUJO
																	// MENSUAL
																	// AJUSTADO
												{

													if (record.get('col1') == 'FEC_NO_DIA') {
														iIndexInhabil = iIndexRecord;

														record.fields
																.each(function(
																		field) {

																	iContInhabil = iContInhabil + 1;
																	fieldValue = record.get(field.name);
																	if (fieldValue != '100'&& fieldValue != '1000'&& fieldValue != '10000'&& fieldValue != 'FEC_NO_DIA'&& fieldValue != 'NO DIA') {
																		columnasInhabiles[iContVec] = iContInhabil;
																		record.remove(iContInhabil);
																		remove(fieldValue);
																		iContVec = iContVec + 1;
																	}// end
																		// if

																});// end
																	// record.fields.each

													}// end if

												}// end if reporte == 7

											});// end function each

									// ELIMINA LA FILA DONDE SE ENCUENTRAN
									// REGISTRADAS LAS BANDERAS DE FEC INHABIL
									NS.exportaExcel(record, columnasInhabiles);
									if (iIndexInhabil > 0) {
										// storeReporte.removeAt(iIndexInhabil -
										// 1);
									}

									var cm = gridDatos.getColumnModel().HTMLStyleElement;

									// EN BASE AL REGISTRO REALIZADO EN EL
									// VECTOR
									// SE OCULTAN LAS COLUMNAS REGISTRADAS

									for (var x = 0; x < (columnasInhabiles.length); x = x + 1) {
										cm.setHidden(columnasInhabiles[x] - 1, true);
									}
									// NS.exportaExcel(cm);
									// SE OCULTA LA COLUMNA CERO DONDE SE
									// MANEJAN LOS IDS
									// DE LAS FILAS
									cm.setHidden(0, true);
									// SE ESTABLECE UN ANCHO PARA LAS COLUMAS
									// for( var y=0;y < cm.getColumnCount(); y =
									// y + 1)
									// {
									cm.setColumnWidth(1, 270);
									// }

									// SE ESTABLECE LA PROPIEDAD DE AUTO
									// APARICION DEL SCROLL
									gridDatos.setAutoScroll(false);

								}// END LOAD

							}
						// END LISTENERS

						});// END storeReporte = new Ext.data.DirectStore({

				// @Objetive: PRESENTAR LA INFORMACION RETORNADA DE LOS METODOS
				// DE NEGOCIO DEL SERVIDOR Y CONTENIDA EN EL STORE storeReporte
				// @NOTE : 1.- EN LA PROPIEDAD DE CONFIGURACION COLUMS, SE MANDA
				// LLAMAR UNA FUNCION QUE DETERMINA MEDIANTE UNA LLAMADA A UN
				// METODO
				// DEL SERVIDOR LA ESTRUCTURA QUE DEFINE LA PROPIEDAD COLUMS, ES
				// LA MISMA FUNCION QUE SE MANDA LLAMAR EN LA PROPIEDAD FIELS
				// DEL STORE, DEBIDO A QUE DEBEN TENER LA MISMA DEFINICION DE
				// CAMPOS Y COLUMAS

				gridDatos = new Ext.grid.GridPanel({
					store : storeReporte,
					height : 220,
					width : 380,
					x : 5,
					y : 20,
					title : '',
					Rowclick: function () {
			             Alerta ('esto funciona');
			            },
					columns : makeStructure(grupoEmpresa, noEmpresa,
							fechaInicial, fechaFinal, reporte),
					viewConfig : {

						// CONFIGURA LAS OPCIONES DE COLORES DE REGISTROS EN
						// BASE A VALORES DETERMINADOS
						// EN SU COLUMNA 1 DE ID

						getRowClass : function(record, index) {
							var c = record.get('col1');
							if (c == 'R-S' || c == 'TI' || c == 'TE' || c == 'SOA' || c == 'SD') {
								return 'price-fall';
							} else if (c.indexOf('TC', 0) > -1) {
								return 'price-1';
							} else if (c.indexOf('SUB', 0) > -1) {
								return 'price-2';
							} else {
								return 'price-rise';
							}// end if

						}// end getRowClass

					}
				// end viewConfig

				}// end gridDatos = new Ext.grid.GridPanel({

				);

				storeReporte.baseParams.idGrupo = grupoEmpresa;
				storeReporte.baseParams.noEmpresa = noEmpresa;
				storeReporte.baseParams.fechaInicial = fechaInicial;
				storeReporte.baseParams.fechaFinal = fechaFinal;
				storeReporte.baseParams.idReporte = reporte;
				storeReporte.load();

				// @Objetive: VENTANA EN LA CUAL SE PRESENTARA EL GRID
				// QUE CONTENDRA LA DEFINICION DEL CUERPO DEL
				// REPORTE SOLICITADO

				// alert(Ext.getBody().getHeight());

				var altura = Ext.getBody().getHeight();

				var winLogin = new Ext.Window({
					/*
					 * title: 'Posición Bancaria' ,
					 */modal : true,
					shadow : true,
					height : altura,
					hidden : true,
					width : '100%',
					resizable : false,
					layout : 'fit',
					plain : true,
					bodyStyle : 'padding:10px;'

					,
					buttonAlign : 'center',
					items : [ gridDatos ]
				});
				winLogin.setTitle(Ext.getCmp(NS.cmbReporte.getId())
						.getRawValue());
				// winLogin.show();
				// PAEE Control que mantiene columna inicial dija solo con
				// scroll vertical,
				// La columna debe esta identificada con la clase .x-grid3-td-1
				// El contenedor se identifica como .x-grid3-scroller
				// Para empleo generico solo se requeire cambio de estas clases
				$('.x-grid3-scroller')
						.scroll(
								function() {
									if (reporte == 1 || reporte == 7) {
										$('.x-grid3-td-1').attr('style',
												'width: 248px;');
										$('.x-grid3-td-1')
												.each(
														function(index, el) {
															if ($('.x-grid3-scroller').offset().top < $(el).offset().top) {
																var style = $(el).attr('style')+ 'position:fixed;';
																for (i = index; i <= index + 19; i++) {
																	var lista = $('.x-grid3-td-1');
																	var vTop = $(lista[i]).offset().top;
																	var vLeft = $(lista[i]).offset().left;
																	$(lista[i]).attr('style',style);
																	if ((index == 1 && (index + 19) < 24)|| ((index + 19) == lista.lenght && index > (lista.length - 20)))
																		$(lista[i]).offset({top : vTop});
																	else
																		$(lista[i]).offset({top : vTop	});
																}
																return false;
															}
														})
									}
								})

			}

			// ************************END***********************************/
			// @autor : JUAN RAMIRO BARRERA MARTINEZ
			// @Method : displayTheWindow
			// @Objetive : Desplegar una ventana, llamar metodos de negocio
			// y presentar un grid con la informacion de flujo
			// ************************END***********************************/

			// ************************INIT***********************************/
			// @autor : JUAN RAMIRO BARRERA MARTINEZ
			// @Method : makeStructure
			// @Objetive : llama un metodo del servidor para determinar la
			// estructura que define los campos para el store,
			// asi como las columans para el grid.
			// @Return : retorna toda la estructura en formato json
			// ************************INIT***********************************/
			var countMake = 1;
			function clik() {
				$('#CONSULTAR tr td button').click();

			}

			function makeStructure(grupoEmpresa, noEmpresa, fechaInicial,
					fechaFinal, reporte) {

				CashFlowActionImplements.getStructureReportService(
						grupoEmpresa, noEmpresa, fechaInicial, fechaFinal, reporte, function(
								result, e) { 
							this.estructura = result; 
						});
				var undefinied;
				if (this.estructura == undefinied) {
					// Ext.MessageBox.wait("Procesando...", 'Por favor
					// espere...');
					if (countMake == 2)
						// setTimeout('$("#CONSULTAR tr td
						// button").click();',2000);
						countMake++;
				} else {
					Ext.Msg.hide();
				}

				// alert(this.estructura === undefinied)

				return eval(this.estructura);
			}
			// ************************END***********************************/
			// @autor : JUAN RAMIRO BARRERA MARTINEZ
			// @Method : makeStructure
			// @Objetive : llama un metodo del servidor para determinar la
			// estructura que define los campos para el store,
			// asi como las columans para el grid.
			// ************************END***********************************/

			// ************************INIT***********************************/
			// @autor : JUAN RAMIRO BARRERA MARTINEZ
			// @Method : makeBody
			// @Objetive : llama un metodo del servidor para determinar la
			// estructura del cuerpo del reporte
			// ************************INIT***********************************/

			function makeBody(grupoEmpresa, noEmpresa, fechaInicial,
					fechaFinal, reporte) {

				CashFlowActionImplements.getBodyReportService(grupoEmpresa, noEmpresa, fechaInicial, fechaFinal, reporte, function(
								result, e) { this.bodyWin = result;
						});

				return eval(this.bodyWin);

			}
			NS.excel = function() {

	        	parametros = '?nomReporte=excelflujo2';
				parametros += '&nomParam1=grupo';
				parametros += '&valParam1='+ Ext.getCmp(PF + 'cmbGrupoEmpresas').getRawValue();
				parametros += '&nomParam2=Noempresa';
				parametros += '&valParam2='+ Ext.getCmp(NS.txtNoEmpresa.getId()).getValue();
				parametros += '&nomParam3=empresa';
				parametros += '&valParam3='+ Ext.getCmp(PF + 'cmbEmpresa').getRawValue();
				parametros += '&nomParam4=fechaIni';
				parametros += '&valParam4='+ cambiarFecha(''+ Ext.getCmp(NS.txdFechaInicial.getId()).getValue());
				parametros += '&nomParam5=fechaFin';
				parametros += '&valParam5='+ cambiarFecha(''+ Ext.getCmp(NS.txdFechaFinal.getId()).getValue());
				parametros += '&nomParam6=reporte';
				parametros += '&valParam6='+ Ext.getCmp(PF + 'cmbReporte').getRawValue();
				parametros += '&nomParam7=Nogrupo';
				parametros += '&valParam7='+ Ext.getCmp(NS.txtNoGrupoEmpresas.getId()).getValue();

				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+ parametros);
			}
			 
			// ************************END***********************************/
			// @autor : JUAN RAMIRO BARRERA MARTINEZ
			// @Method : makeBody
			// @Objetive : llama un metodo del servidor para determinar la
			// estructura del cuerpo del reporte
			// ************************END***********************************/

		});
// ************************END*******************************/
// @autor : JUAN RAMIRO BARRERA MARTINEZ
// @Method : OnReady
// ************************END*******************************/
