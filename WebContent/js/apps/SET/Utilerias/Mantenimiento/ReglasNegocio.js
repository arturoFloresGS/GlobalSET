Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.ReglasNegocio');
	NS.tabContId = apps.SET.tabContainerId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	//Ext.Ajax.timeout = 180000;
	 
	NS.fecHoy = apps.SET.FEC_HOY; //BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	var selGlobalAcreInc = '';
	var selGlobalAcreExc = '';
	var selGlobalDoctoInc = '';
	var selGlobalDoctoExc = '';
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
	var mascara2 = new Ext.LoadMask(Ext.getBody(), {msg:"Simulando regla de negocio. <br><br> Este proceso puede tardar varios minutos."});
	var mascara3 = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando propuestas simuladas..."});
	//mascara.show();
	
	NS.idCondicionPago = 0;
	NS.idContab = 0;
	NS.idDiasExcep = 0;
	NS.jsonEliminarAcre = '';
	NS.jsonEliminarClase = '';
	NS.jsonEliminarTes = '';
	NS.jsonEliminarFechas = '';
	NS.jsonEliminarDoctos = '';
	NS.eliminaDiasContabVenc = false;
	NS.eliminaDiasAdicionales = false;
	NS.eliminaDiasespecificos = false;
	NS.eliminaPlanPagos = false;
	NS.jsonStringAcreedores = ''
	NS.globalGridAcre = 0;
	NS.globalCargaAcre = false;
	NS.simulador = false;
	
	verificaComponentesCreados();
	
	//STORES
	NS.storeRelacion = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		//baseParams: {},
		//paramOrder: [],
		directFn: ReglasNegocioAction.obtenerRelacion,
		//idProperty: 'idRegla',
		fields:[
		 	{name: 'idRegla'},
		 	{name: 'reglaNegocio'}, //nombre
		 	{name: 'tipoRegla'},
		 	{name: 'fechaCaptura'},
		 	{name: 'usuarioCaptura'},
		 	{name: 'claveUsuarioCaptura'},
		 	{name: 'generaPropuestaAutomatica'},
		 	{name: 'usuarios'},
		 	{name: 'indicador'},
		 	{name: 'descuento'},
		 	{name: 'longChequera'}
		],
		listeners:{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRelacion, msg: "Cargando..."});
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No tiene reglas creadas.');
				}
				
			}
		}
	});
	NS.storeRelacion.load();
	
	NS.storeEmpresasSinAsignar = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			bExiste: false
			,idRegla: 0
		},
		root: '',
		paramOrder: ['bExiste', 'idRegla'],
		directFn: ReglasNegocioAction.obtenerEmpresas,
		idProperty: 'idEmpresas',
		fields: [
			{name: 'noEmpresa' },
			{name: 'nombreEmpresa'},
			{name: 'horaLimiteOperacion'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresasSinAsignar, msg:"Cargando..."});
			
			}
		}
	});

	NS.storeEmpresasAsignadas = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			bExiste: true
			,idRegla: 0
		},
		root: '',
		paramOrder: ['bExiste','idRegla'],
		directFn: ReglasNegocioAction.obtenerEmpresas,
		idProperty: 'noEmpresa',
		fields: [
			{name: 'noEmpresa' },
			{name: 'nombreEmpresa'},
			{name: 'horaLimiteOperacion'}//, type: 'number'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresasAsignadas, msg:"Cargando..."});
			} 
		}
	});
	
	NS.storeAcreedoresInc = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			 bTipoAcre: false
			 ,clasificacion: ''
			 ,idRegla: 0
		},
		root: '',
		paramOrder: ['bTipoAcre','idRegla','clasificacion'],
		directFn: ReglasNegocioAction.obtenerAcreedorDocumento,
		idProperty: 'idAcreDocto',
		fields: [
			{name: 'idAcreDocto' },
			{name: 'deAcre'},
			{name: 'aAcre'},
			{name: 'clasificacion'},
			{name: 'tipoAcre'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAcreedoresInc, msg:"Cargando..."});
			} 
		}
	});
	
	NS.storeAcreedoresExc = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			 bTipoAcre: false
			 ,clasificacion: ''
			,idRegla: 0
		},
		root: '',
		paramOrder: ['bTipoAcre','idRegla','clasificacion'],
		directFn: ReglasNegocioAction.obtenerAcreedorDocumento,
		idProperty: 'idAcreDocto',
		fields: [
			{name: 'idAcreDocto' },
			{name: 'deAcre'},
			{name: 'aAcre'},
			{name: 'clasificacion'},
			{name: 'tipoAcre'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAcreedoresExc, msg:"Cargando..."});
			} 
		}
	});
	
	NS.storeDocumentoInc = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			 bTipoDocto: false
			,idRegla: 0
			,clasificacion: ''
		},
		root: '',
		paramOrder: ['bTipoDocto','idRegla','clasificacion'],
		directFn: ReglasNegocioAction.obtenerAcreedorDocumento,
		idProperty: 'idAcreDocto', //Aunque hace referencia a los acreedores se utiliza de la misma forma para documentos
								  //El campo clasificacion se distingue por A(Acreedores) o D(Documentos).
		fields: [
			{name: 'idAcreDocto' },
			{name: 'deAcre'},
			{name: 'aAcre'},
			{name: 'clasificacion'},
			{name: 'tipoAcre'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDocumentoInc, msg:"Cargando..."});
			} 
		}
	});
	
	NS.storeDocumentoExc = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			 bTipoDocto: false
			 ,clasificacion: ''
			,idRegla: 0
		},
		root: '',
		paramOrder: ['bTipoDocto','idRegla','clasificacion'],
		directFn: ReglasNegocioAction.obtenerAcreedorDocumento,
		idProperty: 'idAcreDocto',
		fields: [
			{name: 'idAcreDocto' },
			{name: 'deAcre'},
			{name: 'aAcre'},
			{name: 'clasificacion'},
			{name: 'tipoAcre'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDocumentoExc, msg:"Cargando..."});
			} 
		}
	});
	
	
	NS.storeClaseDocto = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			clasificacion: ''
			,idRegla: 0
		},
		root: '',
		paramOrder: ['idRegla','clasificacion'],
		directFn: ReglasNegocioAction.obtenerDoctoTesoreria, //funciona para clase documento y grupo tesoreria
		idProperty: 'idDoctoTes',
		fields: [
			{name: 'idDoctoTes' },
			{name: 'incluir'},
			{name: 'excluir'},
			{name: 'clasificacion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeClaseDocto, msg:"Cargando..."});
			} 
		}
	});
	
	NS.storeGrupoTesoreria = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			clasificacion: ''
			,idRegla: 0
		},
		root: '',
		paramOrder: ['idRegla','clasificacion'],
		directFn: ReglasNegocioAction.obtenerDoctoTesoreria, //funciona para clase documento y grupo tesoreria
		idProperty: 'idDoctoTes',
		fields: [
			{name: 'idDoctoTes' },
			{name: 'incluir'},
			{name: 'excluir'},
			{name: 'clasificacion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoTesoreria, msg:"Cargando..."});
			} 
		}
	});
	
	NS.storePlanPagos = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			idCondicionPago: 0
		},
		root: '',
		paramOrder: ['idCondicionPago'],
		directFn: ReglasNegocioAction.obtenerPlanPago,
		idProperty: 'idPlan',
		fields: [
			{name: 'idPlan' },
			{name: 'fecha'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePlanPagos, msg:"Cargando..."});
				if(records.length > 0){
					Ext.getCmp(PF + 'chkAplica5').setValue(true);
				}else{
					Ext.getCmp(PF + 'chkAplica5').setValue(false);
				}
			} 
		}
	});

	NS.storeRubrosSinAsignar = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			bAsignados: false,
			idRegla: 0
		},
		root: '',
		paramOrder: ['bAsignados','idRegla'],
		directFn: ReglasNegocioAction.obtenerRubros,
		idProperty: 'idRubro',
		fields: [
			{name: 'idRubro' },
			{name: 'descRubro'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubrosSinAsignar, msg:"Cargando..."});
				
			}
		}
	});

	NS.storeRubrosAsignados = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			bAsignados: false,
			idRegla: 0
		},
		root: '',
		paramOrder: ['bAsignados','idRegla'],
		directFn: ReglasNegocioAction.obtenerRubros,
		idProperty: 'idRubro',
		fields: [
			{name: 'idRubro' },
			{name: 'descRubro'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubrosAsignados, msg:"Cargando..."});
			
			} 
		}
	});
	
/******** FUNCION EXPORTAR EXCEL ********/
	
	NS.exportaExcel = function(jsonCadena) {
		
		//mascara.show(); //se muestra en el boton que manda llamar esta funcion.
		
		ReglasNegocioAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
				mascara.hide();
			} else {
				strParams = '?nomReporte=ReporteReglasNegocio';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
				mascara.hide();
			}
		});
	};
	
	
/********* PANEL RELACIÓN **********/
	
	//GRID DE CONSULTA
	NS.colSelRelacion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: true
	});

	NS.columnasGridRelacion = new Ext.grid.ColumnModel ([
	  	NS.colSelRelacion,
	  	{header: 'Regla Negocio', width: 150, dataIndex: 'reglaNegocio', sortable: true},
	  	{header: 'Tipo Regla', width: 70 , dataIndex: 'tipoRegla', sortable: true },
	  	{header: 'Fecha Captura', width: 130, dataIndex: 'fechaCaptura', sortable: true},
	  	{header: 'Usuario Capturo', width: 150, dataIndex: 'claveUsuarioCaptura', sortable : true},
	  	//Columnas quedan ocultas pero son necesarias para acceder a sus datos
	  	{header: 'Id Regla', width: 50, dataIndex: 'idRegla', sortable: true, hidden: true},
	  	{header: 'Genera Propuesta Automatica', width: 50, dataIndex: 'generaPropuestaAutomatica', sortable: true, hidden: true},
	  	{header: 'Usuarios', width: 50, dataIndex: 'usuarios', sortable: true, hidden: true},
	  	{header: 'Indicador', width: 50, dataIndex: 'indicador', sortable: true, hidden: true}
	 	
	]); 
	
	//Se agregan componentes (columnas) a grid
	NS.gridRelacion = new Ext.grid.GridPanel ({
		store: NS.storeRelacion,
		id: PF + 'gridRelacion',
		name: PF + 'gridRelacion',
		cm: NS.columnasGridRelacion,
		sm: NS.colSelRelacion,
		width: 580,
		height: 220,
		frame:true,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					
					var seleccion = NS.gridRelacion.getSelectionModel().getSelections();

					//limpiaVariables();
					limpiarTodo(false);
					
					if(seleccion.length > 0){
						
						if(seleccion[0].get('tipoRegla') == 'S'){
							NS.cmbReglas.setValue('SERVICIOS')
						}else if(seleccion[0].get('tipoRegla') == 'L'){
							NS.cmbReglas.setValue('LINEAS')
						}else{
							NS.cmbReglas.setValue('');
						}
						
						NS.cmbReglas.setDisabled(true);
						
						NS.txtNombreRegla.setValue(seleccion[0].get('reglaNegocio'))

						if(seleccion[0].get('generaPropuestaAutomatica') == 'S'){
							Ext.getCmp(PF + 'chkGeneraPropAut').setValue(true);
						}else{
							Ext.getCmp(PF + 'chkGeneraPropAut').setValue(false);
						}
						
						
						if(seleccion[0].get('descuento') == 'C'){
							Ext.getCmp(PF + 'chkCPagos').setValue(true);
						}else if(seleccion[0].get('descuento') == 'D'){
							Ext.getCmp(PF + 'chkDescuento').setValue(true);
						}else{
							Ext.getCmp(PF + 'chkCPagos').setValue(false);
							Ext.getCmp(PF + 'chkDescuento').setValue(false);
						}
						
						NS.txtUsuarios.setValue(seleccion[0].get('usuarios'));
						NS.txtIndicador.setValue(seleccion[0].get('indicador'));
						NS.txtLongChequera.setValue(seleccion[0].get('longChequera'));
						
						var regla = seleccion[0].get('idRegla');
						NS.cargarGrids(regla);
						
						//Condiciones de pago
						ReglasNegocioAction.obtenerCondicionesPago(regla, function(resultado, e) {
							if (resultado != '' && resultado != null && resultado != undefined) {
								
								if(resultado.fechaBase == 'C'){
									 Ext.getCmp(PF + 'optFechaC').setValue(true);
									 Ext.getCmp(PF + 'optFechaV').setValue(false);
									 Ext.getCmp(PF + 'optFechaD').setValue(false);
								}else if(resultado.fechaBase == 'V'){
									 Ext.getCmp(PF + 'optFechaC').setValue(false);
									 Ext.getCmp(PF + 'optFechaV').setValue(true);
									 Ext.getCmp(PF + 'optFechaD').setValue(false);
								}else if(resultado.fechaBase == 'D'){
									 Ext.getCmp(PF + 'optFechaC').setValue(false);
									 Ext.getCmp(PF + 'optFechaV').setValue(false);
									 Ext.getCmp(PF + 'optFechaD').setValue(true);
								}
									
								if(resultado.claseDia == 'N'){
									 Ext.getCmp(PF + 'optNaturales').setValue(true);
									 Ext.getCmp(PF + 'optHabiles').setValue(false);
								}else if(resultado.claseDia == 'H'){
									Ext.getCmp(PF + 'optNaturales').setValue(false);
									 Ext.getCmp(PF + 'optHabiles').setValue(true);
								}
								
								if(resultado.accionDia == 'A'){
									Ext.getCmp(PF + 'optDiaAnt').setValue(true);
									Ext.getCmp(PF + 'optDiaPost').setValue(false);
								}else if(resultado.accionDia == 'P'){
									Ext.getCmp(PF + 'optDiaAnt').setValue(false);
									Ext.getCmp(PF + 'optDiaPost').setValue(true);
								}
								
								if(resultado.diasAdicionales != ''){
									Ext.getCmp(PF + 'chkAplica2').setValue(true);
									NS.txtIdDiasAdicionales.setValue(resultado.diasAdicionales);
								}else{
									Ext.getCmp(PF + 'chkAplica2').setValue(false);
									NS.txtIdDiasAdicionales.setValue('');
								}
								
								if(resultado.finMes == 'S'){
									Ext.getCmp(PF + 'chkAplica3').setValue(true);
								}else{
									Ext.getCmp(PF + 'chkAplica3').setValue(false);
								}
								
								Ext.getCmp(PF + 'txtBancoInter').setValue(resultado.bancoInterlocutor);
								
								// panel dias pago
								if(resultado.diaPago1 != '' && resultado.diaPago1 != null ){
									
									Ext.getCmp(PF + 'chkAplica').setValue(true);
									
									NS.idContab = resultado.idContab;
									
									NS.cmbDiasLunes.setValue(resultado.diaPago1);
									NS.cmbDiasMartes.setValue(resultado.diaPago2);
									NS.cmbDiasMiercoles.setValue(resultado.diaPago3);
									NS.cmbDiasJueves.setValue(resultado.diaPago4);
									NS.cmbDiasViernes.setValue(resultado.diaPago5);
									NS.cmbDiasSabado.setValue(resultado.diaPago6);
									NS.cmbDiasDomingo.setValue(resultado.diaPago7);
									
									if(resultado.semana1 == 'A'){
										NS.cmbSemanaProximaLunes.setValue("SEMANA ACTUAL");
									}else if(resultado.semana1 == 'P'){
										NS.cmbSemanaProximaLunes.setValue("PRÓXIMA SEMANA");
									}										
									
									if(resultado.semana2 == 'A'){
										NS.cmbSemanaProximaMartes.setValue("SEMANA ACTUAL");
									}else if(resultado.semana2 == 'P'){
										NS.cmbSemanaProximaMartes.setValue("PRÓXIMA SEMANA");
									}	
									
									if(resultado.semana3 == 'A'){
										NS.cmbSemanaProximaMiercoles.setValue("SEMANA ACTUAL");
									}else if(resultado.semana3 == 'P'){
										NS.cmbSemanaProximaMiercoles.setValue("PRÓXIMA SEMANA");
									}	
									
									if(resultado.semana4 == 'A'){
										NS.cmbSemanaProximaJueves.setValue("SEMANA ACTUAL");
									}else if(resultado.semana4 == 'P'){
										NS.cmbSemanaProximaJueves.setValue("PRÓXIMA SEMANA");
									}	
									
									if(resultado.semana5 == 'A'){
										NS.cmbSemanaProximaViernes.setValue("SEMANA ACTUAL");
									}else if(resultado.semana5 == 'P'){
										NS.cmbSemanaProximaViernes.setValue("PRÓXIMA SEMANA");
									}	
									
									if(resultado.semana6 == 'A'){
										NS.cmbSemanaProximaSabado.setValue("SEMANA ACTUAL");
									}else if(resultado.semana6 == 'P'){
										NS.cmbSemanaProximaSabado.setValue("PRÓXIMA SEMANA");
									}	
									
									if(resultado.semana7 == 'A'){
										NS.cmbSemanaProximaDomingo.setValue("SEMANA ACTUAL");
									}else if(resultado.semana7 == 'P'){
										NS.cmbSemanaProximaDomingo.setValue("PRÓXIMA SEMANA");
									}	
									
								}else{
									Ext.getCmp(PF + 'chkAplica').setValue(false);
								}
								
								// Panel dia especifico / excepciones 
								if(resultado.diaEspecifico1 != '' && resultado.diaEspecifico1 != null ){
									
									NS.idDiasExcep = resultado.idDiasExcep;

									NS.txtIdDiaEspecifico.setValue(resultado.diaEspecifico1);
									NS.cmbDiasExcepcion.setValue(resultado.diaExcep1);
									NS.cmbDiaPago.setValue(resultado.diaPagoExcep1);
									
									if(resultado.rangoDiaDesde1 == 0){
										NS.txtIdRangoDesde.setValue('');
									}else{
										NS.txtIdRangoDesde.setValue(resultado.rangoDiaDesde1);
									}
									
									if(resultado.rangoDiaHasta1 == 0){
										NS.txtIdRangoHasta.setValue('');
									}else{
										NS.txtIdRangoHasta.setValue(resultado.rangoDiaHasta1);
									}
									
									
									Ext.getCmp(PF + 'chkAplica4').setValue(true);
									
									if(resultado.diaEspecifico2 != ''){
										NS.txtIdDiaEspecifico2.setValue(resultado.diaEspecifico2);
										
										if(resultado.rangoDiaDesde2 == 0){
											NS.txtIdRangoDesde2.setValue('');
										}else{
											NS.txtIdRangoDesde2.setValue(resultado.rangoDiaDesde2);
										}
										
										if(resultado.rangoDiaHasta2 == 0){
											NS.txtIdRangoHasta2.setValue('');
										}else{
											NS.txtIdRangoHasta2.setValue(resultado.rangoDiaHasta2);
										}
											
										NS.cmbDiaExepc.setValue(resultado.diaExcep2);
										NS.cmbDiaPago2.setValue(resultado.diaPagoExcep2);
									}							
									
								}else{
									Ext.getCmp(PF + 'chkAplica4').setValue(false);
								}
								
								if(resultado.idCondicionPago != '' && resultado.idCondicionPago != null){
									//Guardamos el idCondicionPago que servira para realizar modificaciones a las RG.
									NS.idCondicionPago = resultado.idCondicionPago;
									
									//Carga el stored de plan pagos
									NS.storePlanPagos.baseParams.idCondicionPago = resultado.idCondicionPago;
									NS.storePlanPagos.load();
									
								}
								
							}
						});
					
						Ext.getCmp(PF + 'btnModificarArriba').setDisabled(false);
						Ext.getCmp(PF + 'btnAceptar').setText("Modificar")
						
					}else{
						limpiarTodo(true);
					}
				}
			}
		}
	});
	
	NS.panelRelacion = new Ext.form.FieldSet ({
		title: 'Relación',
		id: PF + 'panelRelacion',
		name: PF + 'panelRelacion',
		x: 0,
		y: 0,
		width: 600,
		height: 290,
		layout: 'absolute',
		items: [
		 	NS.gridRelacion,
		 	{
		 		xtype: 'button',
		 		id: PF + 'btnNuevo',
		 		name: PF + 'btnNuevo',
		 		text: 'Crear Nuevo',
		 		x: 120, 
		 		y: 230,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					
		 					limpiarTodo(true);
							NS.cargarGrids(0);
							
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		id: PF + 'btnModificarArriba',
		 		name: PF + 'btnModificarArriba',
		 		text: 'Modificar',
		 		x: 210, 
		 		y: 230,
		 		width: 80,
		 		height: 22,
		 		enabled: false,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					aceptarModificar();					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		id: PF + 'btnEliminarRegla',
		 		name: PF + 'btnEliminarRegla',
		 		text: 'Eliminar',
		 		x: 300, 
		 		y: 230,
		 		width: 80,
		 		height: 22,
		 		enabled: false,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					var seleccion = NS.gridRelacion.getSelectionModel().getSelections();
		 					var regla = 0;
		 					if(seleccion.length > 0){
		 						regla = seleccion[0].get('idRegla');
		 					}
		 					
		 					
		 					//Cambiar el status del grid
		 					Ext.Msg.confirm('SET', '¿Esta seguro de eliminar esta regla de negocio?', function(btn){
		 						if (btn === 'yes'){		
		 							
		 							mascara.show();
		 							
		 							Ext.getCmp(PF + 'btnEliminarRegla').setDisabled(true);
		 							Ext.getCmp(PF + 'btnModificarArriba').setDisabled(true);
		 							Ext.getCmp(PF + 'btnAceptar').setDisabled(true);
		 							
		 							ReglasNegocioAction.eliminarReglaNegocio(regla, function(result, e){
		 								
		 								if(result != null && result != '' && result != undefined) {								
											
				 							if(result.error != ''){
				 								Ext.Msg.alert('SET',result.error);
				 								Ext.getCmp(PF + 'btnEliminarRegla').setDisabled(false);
					 							Ext.getCmp(PF + 'btnModificarArriba').setDisabled(false);
					 							Ext.getCmp(PF + 'btnAceptar').setDisabled(false);
					 							mascara.hide();
						 						return;
				 							}else if(result.mensaje != ''){
				 								Ext.Msg.alert('SET',result.mensaje);
				 								limpiarTodo(true);
				 								Ext.getCmp(PF + 'btnEliminarRegla').setDisabled(false);
					 							Ext.getCmp(PF + 'btnModificarArriba').setDisabled(false);
					 							Ext.getCmp(PF + 'btnAceptar').setDisabled(false);
					 							mascara.hide();
						 						return;
				 							}
				 							
										}
		 								
		 							});
		 							
		 						}
		 					});
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		id: PF + 'btnExcel',
		 		name: PF + 'btnExcel',
		 		text: 'Excel',
		 		x: 390, 
		 		y: 230,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					
		 					mascara.show();
		 					
		 					var seleccion = NS.storeRelacion.data.items;
		 					
		 					var matriz = new Array();
		 					
		 					for(var i=0;i<seleccion.length;i++){
		 						var vector = {};
		 						vector.reglaNegocio = seleccion[i].get('reglaNegocio'); 
		 						vector.tipoRegla = seleccion[i].get('tipoRegla');
		 						vector.fechaCaptura = seleccion[i].get('fechaCaptura');
		 						//vector.usuarioCaptura = seleccion[i].get('usuarioCaptura');
		 						vector.claveUsuarioCaptura = seleccion[i].get('claveUsuarioCaptura');
		 						
		 						matriz[i] = vector;
		 						
		 					}
		 					
		 					var jSonString = Ext.util.JSON.encode(matriz);
		 				
		 					NS.exportaExcel(jSonString);
		 					
		 				}
		 			}
		 		}
		 	},
	 		{
		 		xtype: 'button',
		 		id: PF + 'btnLimpiar',
		 		name: PF + 'btnLimpiar',
		 		text: 'Limpiar',
		 		x: 480, 
		 		y: 230,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					limpiarTodo(true);
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	/********* FIN PANEL RELACIÓN **********/
	
	/************ PANEL EMPRESAS **********/
	
	NS.lblTipoReglas = new Ext.form.Label({
		text: 'Tipo Reglas',
		x: 0,
		y: 0
	});
   	
	NS.lblNombreRegla = new Ext.form.Label({
		text: 'Nombre de la Regla',
		x: 245,
		y: 0
	});
	
	//Esto es para llenar el combo "Tipo Reglas"
	NS.datosComboTipoReglas = [
		['S', 'SERVICIOS'],
		['L', 'LINEAS']		       			
	];	       		
	 
	NS.storeTipoReglas = new Ext.data.SimpleStore({
   		idProperty: 'idTipoRegla',
   		fields: [
   					{name: 'idTipoRegla'},
   					{name: 'tipoRegla'}
   				]
   	});
   	NS.storeTipoReglas.loadData(NS.datosComboTipoReglas);
   	
	NS.cmbReglas = new Ext.form.ComboBox ({
		store: NS.storeTipoReglas,
		id: PF + 'cmbReglas',
		name: PF + 'cmbReglas',
		x: 0,
		y: 20,
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idTipoRegla',
		displayField: 'tipoRegla',
		autocomplete: true,
		emptyText: 'Seleccione regla de negocio',
		triggerAction: 'all',
		value: '',
		tabIndex: 1,
		listeners: {
			select: {
				fn: function(combo, valor) {
					
				}
			}
		}
	});
	
	NS.txtNombreRegla = new Ext.form.TextField({
		id: PF + 'txtNombreRegla', 
		name: PF + 'txtNombreRegla',
		x: 240, 
		y: 20, 
		width: 150,
		tabIndex: 2,
	});
	
	//Grid Asignacion
	NS.selEmpresasSinAsignar = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	NS.colEmpresasSinAsignar = new Ext.grid.ColumnModel ([
 	  	NS.selEmpresasSinAsignar,
 	  	{header: 'No. Empresa', width: 40, dataIndex: 'noEmpresa', sortable: true},
 	  	{header: 'Sin Asignar', width: 100, dataIndex: 'nombreEmpresa', sortable: true},
 	  	{header: 'Hora límite operación', width: 140, dataIndex: 'horaLimiteOperacion', sortable: true, hidden: true} //, xtype: 'numbercolumn', format: '0.00'},
 	  	
 	]); 
	
	NS.gridEmpresasSinAsignar = new Ext.grid.GridPanel ({
		store: NS.storeEmpresasSinAsignar,
		id: PF + 'gridEmpresasSinAsignar', 
		name: PF + 'gridEmpresasSinAsignar',
		cm: NS.colEmpresasSinAsignar,
		sm: NS.selEmpresasSinAsignar,
		width: 232,
		height:200,
		x: 0,
		y: 50,
		//stripeRows: true,
		//columnLines: true,
		enableDragDrop   : true,
		ddGroup: PF+'secondGridDDGroup',
		listeners: {
			click: {
				fn: function(grid) {
					
				}
			}
		}
	});
	
//Grid Asignacion
	
	NS.selEmpresasAsignadas = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	var txtHoraLimite = new Ext.form.TextField({});
	
	/*new Ext.form.TimeField({
	    minValue: '9:00 AM',
	    maxValue: '6:00 PM',
	    increment: 30
	});*/
	
	
	//Este text se crea para hacer editable el grid en el campo Hora límite operación.
	NS.colEmpresasAsignadas = new Ext.grid.ColumnModel ([
 	  	NS.selEmpresasAsignadas,
 	  	{header: 'No. Empresa', width: 40, dataIndex: 'noEmpresa', sortable: true},
 	  	{header: 'Asignadas', width: 100, dataIndex: 'nombreEmpresa', sortable: true},
 	  	{header: 'Hora límite operación', width: 140, dataIndex: 'horaLimiteOperacion', sortable: true, editor: txtHoraLimite} //, xtype: 'numbercolumn', format: '0.00'},
 	]); 
	
	
	NS.gridEmpresasAsignadas = new Ext.grid.EditorGridPanel ({
		store: NS.storeEmpresasAsignadas,
		id: PF + 'gridEmpresasAsignadas', 
		name: PF + 'gridEmpresasAsignadas',
		cm: NS.colEmpresasAsignadas,
		sm: NS.selEmpresasAsignadas,
		width: 282,
		height:200,
		x: 300,
		y: 50,
		stripeRows: true,
		columnLines: true,
		enableDragDrop   : true,
		ddGroup: 'firstGridDDGroup',
		listeners: {
			click: {
				fn: function(grid) {
					
				}
			}
		}
	});
	
	NS.PanelCargaExcelEmpresas = new Ext.FormPanel({
		fileUpload: true,
        title: '',
        width: 350,
        height: 40,
        x:0,
        y:0,
    	autoScroll: true,
        renderTo: NS.tabContId,
        layout: 'absolute',
        monitorValid: true,
        items:[
			{
			    xtype: 'fileuploadfield',
			    id: PF + 'archivoExcelEmp',
			    emptyText: 'Archivo',
			    name: PF + 'archivoExcelEmp',
			    x:0,
			    y:0,
			    hidden: true
			},{
		 		xtype: 'button',
		 		text: 'Cargar Excel',
		 		x: 260,
		 		y: 0,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					 if (Ext.getCmp(PF + 'archivoExcelEmp').getValue() != '' ) {
		 		            	   //NS.PanelCargaExcelDocto.getForm().submit({
		 		            		   var strParams = 'nombreArchivoLeer=leerExcelEmpresas';
		 								
		 								NS.PanelCargaExcelEmpresas.getForm().submit({
		 		       						
		 			                       url: '/SET/jsp/bfrmwrk/gnrators/leerExcel.jsp'+ '?' + strParams,
		 			                       waitMsg: 'Leyendo Registros...',
		 			                       success: function (fp, o) {
		 			                    	  
		 			                    	  var obj = JSON.parse(o.result.json);
			         							
		 			                    	  var matriz = new Array();
		 			                    	  for(var i=0; i<obj.length; i++){
		 			                    		   var reg = {};
		 			                    		   reg.noEmpresa = obj[i].No_Empresa;
		 			                    		   reg.nombreEmpresa = obj[i].Nombre;
		 			                    		   reg.horaLimite = obj[i].Hora_limite;
		 			                    		   matriz[i] = reg;
		 			                    	   }
		 			                    	   
		 			                    	  var jsonString = Ext.util.JSON.encode(matriz);
		 			                    	  
		 			                    	  var seleccion = NS.gridRelacion.getSelectionModel().getSelections();
		 			                    	  var regla = 0;
		 			                    	  if(seleccion.length > 0){
		 			                    		  regla = seleccion[0].get('idRegla');
		 			                    	  }else{
		 			                    		//Antes que nada cargamos los grid de empresas si no hay seleccion
		 			       							NS.storeEmpresasSinAsignar.baseParams.bExiste = false;
		 			       							NS.storeEmpresasSinAsignar.baseParams.idRegla = regla;
		 			       							NS.storeEmpresasSinAsignar.load();
		 			       							
		 			       							NS.storeEmpresasAsignadas.baseParams.bExiste = true;
		 			       							NS.storeEmpresasAsignadas.baseParams.idRegla = regla;
		 			       							NS.storeEmpresasAsignadas.load();
		 			                    	  }
	 			       							
		 			                    	   ReglasNegocioAction.validarEmpresas(jsonString, regla, function(result, e){
		 			                    		   
		 			                    		  if(result != null && result != '' && result != undefined) {								
		 			             					
		 			         						if(result.error != ''){
		 			         							Ext.Msg.alert('SET',result.error);
		 			         	 						return;
		 			         						}else if(result.mensaje == 'Ok'){
		 			         							
		 			         							//Una vez validados cargamos los datos al grid
		 			         							 ReglasNegocioAction.obtenerEmpresasValidadas(jsonString, function(result, e){
		 			 			                    		   
		 			         								var datosClase = NS.storeEmpresasAsignadas.recordType;
		 			         								//var empSinAsignar = NS.storeEmpresasAsignadas.data.items;
		 			         								var exist = true;
		 			         								
		 			         								 for(var i = 0; i < result.length; i++){
		 			         									
		 			         									var datos = new datosClase({
		 			         										noEmpresa: result[i].noEmpresa,
		 			         										nombreEmpresa: result[i].nombreEmpresa,
		 			         										horaLimiteOperacion: result[i].horaLimiteOperacion
		 			         									});
		 			         									
		 			         									var empSinAsignar = NS.storeEmpresasSinAsignar.data.items;
		 			         									
		 			         									for(var j = 0; j < empSinAsignar.length; j++){
		 			         										if(empSinAsignar[j].get('noEmpresa') == result[i].noEmpresa){
		 			         											NS.storeEmpresasSinAsignar.remove(empSinAsignar[j]);
		 			         											exist = true;
		 			         											break;
		 			         										}else{
		 			         											exist = false;
		 			         										}
		 			         									}
		 			         									
		 			         									if(exist){
		 			         										NS.storeEmpresasAsignadas.add(datos);
		 			         									}else{
		 			         										Ext.Msg.alert('SET',"La empresa " +  result[i].nombreEmpresa + ", no está asignada al usuario");
		 			 			         	 						
		 				 			       							NS.storeEmpresasSinAsignar.baseParams.bExiste = false;
		 				 			       							NS.storeEmpresasSinAsignar.baseParams.idRegla = regla;
		 				 			       							NS.storeEmpresasSinAsignar.load();
		 				 			       							
		 				 			       							NS.storeEmpresasAsignadas.baseParams.bExiste = true;
		 				 			       							NS.storeEmpresasAsignadas.baseParams.idRegla = regla;
		 				 			       							NS.storeEmpresasAsignadas.load();
		 				 			       							
		 				 			       							return;
		 			         									}
	 			         										//NS.storeEmpresasAsignadas.insert(totalReg.length, datos);
		 			         									
		 			         								 }
		 			         								 
		 			         								NS.gridEmpresasAsignadas.getView().refresh();
		 			         								NS.gridEmpresasSinAsignar.getView().refresh();
		 			         								
		 			 			                    	   });
		 			         							 
		 			         						}
		 			         						
		 			         					}
		 			                    		  
		 			                    	   });
		 			                    	   
		 			                       },
		 			                       failure: function (fp, o) {
		 			                           Ext.MessageBox.alert('ERROR', 'Ocurrio un error mientras se leeia el archivo');
		 			                       }
		 		                   });
		 		               }else{
		 		            	   Ext.Msg.alert('SET', 'Seleccione un archivo');
		 		               }
		 					 
		 				}
		 			}
		 		}
		 	}
       ]
	});
	
	NS.panelEmpresas = new Ext.form.FieldSet ({
		title: 'Empresas',
		id: PF + 'panelEmpresas',
		name: PF + 'panelEmpresas',
		x: 0,
		y: 300,
		width: 600,
		height: 345,
		layout: 'absolute',		
		//disabled: true,
		items: [
		 	NS.lblTipoReglas,
		 	NS.cmbReglas,
		 	NS.lblNombreRegla,
		 	NS.gridEmpresasSinAsignar,
		 	NS.gridEmpresasAsignadas,
		 	NS.txtNombreRegla,
		 	{
				xtype: 'fieldset',
				title: '',
				width: 360,
				height: 45,
				x: 0,
				y: 260,
				layout: 'absolute',
				items: [
				        NS.PanelCargaExcelEmpresas
				       ]
		 	},
		 	{
		 		xtype: 'checkbox',
		 		id: PF + 'chkCPagos', 
		 		name: PF + 'chkCPagos',
		 		x: 410,
		 		y: 0,
		 		//tabIndex: 31,
		 		boxLabel: 'C/Pagos',
		 		checked: false,
		 		listeners:{
		 			check:{
		 				fn: function(checkBox, valor){
		 					if (valor){
		 						Ext.getCmp(PF + 'chkDescuento').setValue(false);
		 					}
		 				}
		 			}
		 		}
		 	},{
		 		xtype: 'checkbox',
		 		id: PF + 'chkDescuento', 
		 		name: PF + 'chkDescuento',
		 		x: 490,
		 		y: 0,
		 		//tabIndex: 31,
		 		boxLabel: 'Descuento',
		 		checked: false,
		 		listeners:{
		 			check:{
		 				fn: function(checkBox, valor){
		 					if(valor)
		 						Ext.getCmp(PF + 'chkCPagos').setValue(false);
		 				}
		 			}
		 		}
		 	},{
		 		xtype: 'checkbox',
		 		id: PF + 'chkGeneraPropAut', 
		 		name: PF + 'chkGeneraPropAut',
		 		x: 410,
		 		y: 20,
		 		//tabIndex: 31,
		 		boxLabel: 'Genera Propuesta Automática',
		 		checked: true
		 	},
		 	{
		 		xtype: 'button',
		 		id: PF + 'btnAsignarEmpresa',
		 		name: PF + 'btnAsignarEmpresa',
		 		text: '>',
		 		x: 250,
		 		y: 80,
		 		width: 30,
		 		listeners:{
		 			click:{
		 				fn:function(e) {	
		 					
		 					var records = NS.gridEmpresasSinAsignar.getSelectionModel().getSelections();
		 					var datosClase = NS.gridEmpresasSinAsignar.getStore().recordType;
		 					/*
		 					NS.gridEmpresasSinAsignar.store.remove(records);
		 					NS.gridEmpresasSinAsignar.getView().refresh();
		 					
		                    NS.gridEmpresasAsignadas.store.add(records);
		 					NS.gridEmpresasAsignadas.getView().refresh();
		 					*/
		 					
		 					for(var i = 0; i < records.length; i++) {
		 						noEmp = records[i].get('noEmpresa');
		 						nombreEmp = records[i].get('nombreEmpresa');

								NS.gridEmpresasSinAsignar.store.remove(records[i]);

								var datos = new datosClase({
									noEmpresa: noEmp,
									nombreEmpresa: nombreEmp,
									horaLimiteOperacion: '23:59:00'
								});
								
								//NS.gridEmpresasSinAsignar.stopEditing();
								//NS.gridEmpresasAsignadas.insert(i, datos);
								NS.gridEmpresasAsignadas.store.add(datos);
							}
		 						
		 					NS.gridEmpresasAsignadas.getView().refresh();
							NS.gridEmpresasSinAsignar.getView().refresh();
		 					NS.gridEmpresasAsignadas.store.sort('nombreEmpresa', 'ASC');
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: '<',
		 		id: PF + 'btnDesasignarEmpresa',
		 		name: PF + 'btnDesasignarEmpresa',
		 		x: 250,
		 		y: 115,
		 		width: 30,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					var records = NS.gridEmpresasAsignadas.getSelectionModel().getSelections();
		 					
		 					NS.gridEmpresasAsignadas.store.remove(records);
		 					NS.gridEmpresasAsignadas.getView().refresh();
		 					
		                    NS.gridEmpresasSinAsignar.store.add(records);
		 					NS.gridEmpresasSinAsignar.getView().refresh();
		 					
		 					NS.gridEmpresasSinAsignar.store.sort('nombreEmpresa', 'ASC');
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: '>>',
		 		id: PF + 'btnAsignarEmpresas',
		 		name: PF + 'btnAsignarEmpresas',
		 		x: 250,
		 		y: 150,
		 		width: 30,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					var records = NS.storeEmpresasSinAsignar.data.items;
		 					
		 					/*NS.gridEmpresasSinAsignar.store.removeAll();
		 					NS.gridEmpresasSinAsignar.getView().refresh();
		 					
		                    NS.gridEmpresasAsignadas.store.add(records);
		 					NS.gridEmpresasAsignadas.getView().refresh();*/
		 					
		 					var datosClase = NS.gridEmpresasSinAsignar.getStore().recordType;
		 					
		 					for(var i = 0; i < records.length; i++) {
		 						noEmp = records[i].get('noEmpresa');
		 						nombreEmp = records[i].get('nombreEmpresa');

								//NS.gridEmpresasSinAsignar.store.remove(records[i]);

								var datos = new datosClase({
									noEmpresa: noEmp,
									nombreEmpresa: nombreEmp,
									horaLimiteOperacion: '23:59:00'
								});
								
								//NS.gridEmpresasSinAsignar.stopEditing();
								//NS.gridEmpresasAsignadas.insert(i, datos);
								NS.gridEmpresasAsignadas.store.add(datos);
							}
		 						
		 					NS.gridEmpresasSinAsignar.store.removeAll();
		 					NS.gridEmpresasSinAsignar.getView().refresh();
		 					NS.gridEmpresasAsignadas.getView().refresh();
		 					NS.gridEmpresasAsignadas.store.sort('nombreEmpresa', 'ASC');
		 					
		 				}
		 			}
		 		}
		 	},
			{
		 		xtype: 'button',
		 		text: '<<',
		 		id: PF + 'btnDesasignarEmpresas',
		 		name: PF + 'btnDesasignarEmpresas',
		 		x: 250,
		 		y: 185,
		 		width: 30,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					var records = NS.storeEmpresasAsignadas.data.items
		 					
		 					NS.gridEmpresasAsignadas.store.removeAll();
		 					NS.gridEmpresasAsignadas.getView().refresh();
		 					
		                    NS.gridEmpresasSinAsignar.store.add(records);
		 					NS.gridEmpresasSinAsignar.getView().refresh();
		 					
		 					NS.gridEmpresasSinAsignar.store.sort('nombreEmpresa', 'ASC');
		 					 
		 				}
		 			}
		 		}
		 	}
		]
	});

	/************ FIN PANEL EMPRESAS **********/
	
	
	/********** PANEL ACREEDORES ***********/ 
	
	NS.lblAcreedores = new Ext.form.Label({
		text: 'Acreedores a Incluir',
		x: 0, 
		y: 0
	});
	
	NS.lblAcreedoresExcluir = new Ext.form.Label({
		text: 'Acreedores a Excluir',
		x: 325, 
		y: 0,
	});
	
	NS.optAcreedores = new Ext.form.RadioGroup({
		id: PF + 'optAcreedores',
		name: PF + 'optAcreedores',
		x: 245,
		y: 50,
		columns: 1, 
		items: [
	          {boxLabel: 'Individual', id: PF + 'optIndividualAcre', name: PF + 'optAcreedores', inputValue: 'I', checked: true },// checked: true},  
	          {boxLabel: 'Rango', id: PF + 'optRangoAcre', name: PF + 'optAcreedores', inputValue: 'R'}
	     ]
	});
	
	NS.colSelAcreedoresInc = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//textfields para hacer editables campos del grid
	//var txtDeInc = new Ext.form.TextField({
	var txtDeInc = new Ext.form.NumberField ({
		enableKeyEvents: true,
		listeners: {
			change: {
				fn: function(caja, valor) {
					
					if(valor == '' || valor == NaN || valor == null || valor == undefined ){
						return;
					}
					
					if(isNumeric(valor)){
						if(selGlobalAcreInc[0].get('aAcre')!= '' && selGlobalAcreInc[0].get('aAcre') != null && selGlobalAcreInc[0].get('aAcre') != undefined){
							if(parseInt(valor) > parseInt(selGlobalAcreInc[0].get('aAcre'))){
								Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
								caja.setValue('');
								return;
							}
						}
						
						if(Ext.getCmp(PF + 'optIndividualAcre').getValue() == true){
							selGlobalAcreInc[0].set('deAcre', valor);
							selGlobalAcreInc[0].set('aAcre', valor);
							
							NS.gridDatosAcreedoresInc.getView().refresh();
							
							// SE VALIDA QUE NO EXISTA EN OTRA REGLA DE NEGOCIOS 
							var matriz = new Array();
	                    	  
							var reg = {};
							reg.idAcreDocto = '',
							reg.deAcre = valor,
							reg.aAcre = valor,
							reg.clasificacion = 'A',
							reg.tipoAcre = 'I'
                			   
							matriz[i] = reg;
	                    	   
							var jsonString = Ext.util.JSON.encode(matriz);
							NS.jsonStringAcreedores = jsonString;
                    	 
							//Valida que no exista en otra regla de negocios.
							NS.globalCargaAcre = true;
 							NS.globalGridAcre = 1;
 									
 							if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 1){
 								
 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
			 						if (btn === 'yes'){		
			 							validarAcreedores(jsonString, "I", 1);
			 						}else{
			 							
			 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
										
										var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
										
										//Generamos un renglon en blanco
										var datos = new datosClase({
										 	idAcreDocto: '',
				 							deAcre: '',
										 	aAcre: '',
										 	clasificacion: 'A',
										 	tipoAcre: 'I'
						            	});
										
										NS.gridDatosAcreedoresInc.store.add(datos);
										NS.gridDatosAcreedoresInc.getView().refresh();
										
			 						}
			 					});
 								
 							}else if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 2){
 								
 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
			 						if (btn === 'yes'){		
			 							validarAcreedores(jsonString, "I", 1)
			 						}else{
			 							
			 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
										
										var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
										
										//Generamos un renglon en blanco
										var datos = new datosClase({
										 	idAcreDocto: '',
				 							deAcre: '',
										 	aAcre: '',
										 	clasificacion: 'A',
										 	tipoAcre: 'I'
						            	});
										
										NS.gridDatosAcreedoresInc.store.add(datos);
										NS.gridDatosAcreedoresInc.getView().refresh();
										
			 						}
			 					});
 								
 							}else{
 								validarAcreedores(jsonString, "I", 1)
 							}
 							
							//validarAcreedores(jsonString, "I", 1)
							
						}else{
						
							//Solo si el valor de "A" no esta vacio
							if(selGlobalAcreInc[0].get('aAcre')!= '' && selGlobalAcreInc[0].get('aAcre') != null && selGlobalAcreInc[0].get('aAcre') != undefined){
								// SE VALIDA QUE NO EXISTA EN OTRA REGLA DE NEGOCIOS 
								var matriz = new Array();
		                    	  
								var reg = {};
								reg.idAcreDocto = '',
								reg.deAcre = valor,
								reg.aAcre = selGlobalAcreInc[0].get('aAcre'),
								reg.clasificacion = 'A',
								reg.tipoAcre = 'I'
	                			   
								matriz[i] = reg;
		                    	   
								var jsonString = Ext.util.JSON.encode(matriz);
								NS.jsonStringAcreedores = jsonString;
	                    	 
								NS.globalCargaAcre = true;
	 							NS.globalGridAcre = 1;
	 								
	 							if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 1){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "R", 1)
				 						}else{
				 							
				 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
											
											var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'I'
							            	});
											
											NS.gridDatosAcreedoresInc.store.add(datos);
											NS.gridDatosAcreedoresInc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 2){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "R", 1)
				 						}else{
				 							
				 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
											
											var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'I'
							            	});
											
											NS.gridDatosAcreedoresInc.store.add(datos);
											NS.gridDatosAcreedoresInc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else{
	 								validarAcreedores(jsonString, "R", 1)
	 							}
	 							
								//Valida que no exista en otra regla de negocios.
								//validarAcreedores(jsonString, "R", 1)
							}
							
						}
						
					}else{
						Ext.Msg.alert('SET','El valor debe ser numérico');
						caja.setValue('');
						return;
					}

				}
			},
			specialkey:{
				fn: function(caja, e){
					
					if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
						return;
					}
					
					if (e.keyCode == Ext.EventObject.TAB) {
						
						selGlobalAcreInc = NS.gridDatosAcreedoresInc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							if(selGlobalAcreInc[0].get('aAcre')!= '' && selGlobalAcreInc[0].get('aAcre') != null && selGlobalAcreInc[0].get('aAcre') != undefined){
								if(parseInt(caja.getValue()) > parseInt(selGlobalAcreInc[0].get('aAcre'))){
									Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
									caja.setValue('');
									return;
								}
							}
						}else{
							Ext.Msg.alert('SET','El valor debe ser numérico');
							caja.setValue('');
							return;
						}
						
					}else if (e.keyCode == 13) { //Presionó Enter
						
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						selGlobalAcreInc = NS.gridDatosAcreedoresInc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							if(selGlobalAcreInc[0].get('aAcre')!= '' && selGlobalAcreInc[0].get('aAcre') != null && selGlobalAcreInc[0].get('aAcre') != undefined){
								if(parseInt(caja.getValue()) > parseInt(selGlobalAcreInc[0].get('aAcre'))){
									Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
									caja.setValue('');
									return;
								}
							}
							
							if(Ext.getCmp(PF + 'optIndividualAcre').getValue() == true){
			 					
								selGlobalAcreInc[0].set('deAcre', caja.getValue());
								selGlobalAcreInc[0].set('aAcre', caja.getValue());
						
								NS.gridDatosAcreedoresInc.getView().refresh();
								
								//VALIDACIÓN EXISTE EN REGLA NEGOCIO
								var matriz = new Array();
		                    	  
								var reg = {};
								reg.idAcreDocto = '',
								reg.deAcre = selGlobalAcreInc[0].get('deAcre'),
								reg.aAcre = selGlobalAcreInc[0].get('aAcre'),
								reg.clasificacion = 'A',
								reg.tipoAcre = 'I'
	                			   
								matriz[i] = reg;
		                    	   
								var jsonString = Ext.util.JSON.encode(matriz);
								NS.jsonStringAcreedores = jsonString;
	                    	 
								NS.globalCargaAcre = true;
	 							NS.globalGridAcre = 1;
	 							
	 							if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 1){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "I", 1)
				 						}else{
				 							
				 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
											
											var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'I'
							            	});
											
											NS.gridDatosAcreedoresInc.store.add(datos);
											NS.gridDatosAcreedoresInc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 2){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "I", 1)
				 						}else{
				 							
				 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
											
											var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'I'
							            	});
											
											NS.gridDatosAcreedoresInc.store.add(datos);
											NS.gridDatosAcreedoresInc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else{
	 								validarAcreedores(jsonString, "I", 1)
	 							}
	 							
								//Valida que no exista en otra regla de negocios.
								//validarAcreedores(jsonString, "I", 1)
								
							}else{
								//VALIDACIÓN EXISTE EN REGLA NEGOCIO
								if(selGlobalAcreInc[0].get('aAcre')!= '' && selGlobalAcreInc[0].get('aAcre') != null && selGlobalAcreInc[0].get('aAcre') != undefined){
									var matriz = new Array();
			                    	  
									var reg = {};
									reg.idAcreDocto = '',
									reg.deAcre = selGlobalAcreInc[0].get('deAcre'),
									reg.aAcre = selGlobalAcreInc[0].get('aAcre'),
									reg.clasificacion = 'A',
									reg.tipoAcre = 'I'
		                			   
									matriz[i] = reg;
			                    	   
									var jsonString = Ext.util.JSON.encode(matriz);
									NS.jsonStringAcreedores = jsonString;
		                    	 
									NS.globalCargaAcre = true;
		 							NS.globalGridAcre = 1;
		 							
if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 1){
		 								
		 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
					 						if (btn === 'yes'){		
					 							validarAcreedores(jsonString, "R", 1)
					 						}else{
					 							
					 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
												
												var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
												
												//Generamos un renglon en blanco
												var datos = new datosClase({
												 	idAcreDocto: '',
						 							deAcre: '',
												 	aAcre: '',
												 	clasificacion: 'A',
												 	tipoAcre: 'I'
								            	});
												
												NS.gridDatosAcreedoresInc.store.add(datos);
												NS.gridDatosAcreedoresInc.getView().refresh();
												
					 						}
					 					});
		 								
		 							}else if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 2){
		 								
		 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
					 						if (btn === 'yes'){		
					 							validarAcreedores(jsonString, "R", 1)
					 						}else{
					 							
					 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
												
												var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
												
												//Generamos un renglon en blanco
												var datos = new datosClase({
												 	idAcreDocto: '',
						 							deAcre: '',
												 	aAcre: '',
												 	clasificacion: 'A',
												 	tipoAcre: 'I'
								            	});
												
												NS.gridDatosAcreedoresInc.store.add(datos);
												NS.gridDatosAcreedoresInc.getView().refresh();
												
					 						}
					 					});
		 								
		 							}else{
		 								validarAcreedores(jsonString, "R", 1)
		 							}

									//Valida que no exista en otra regla de negocios.
									//validarAcreedores(jsonString, "R", 1)	
								}
							}
							
						}else{
							Ext.Msg.alert('SET','El valor debe ser numérico');
							caja.setValue('');
							return;
						}
						
					}
				}
			}
		}
			
	});
	
	var txtAInc = new Ext.form.NumberField ({
		enableKeyEvents: true,
		listeners: {
			change: {
				fn: function(caja, valor) {
					
					if(valor == '' || valor == NaN || valor == null || valor == undefined ){
						return;
					}
					
					if(isNumeric(valor)){
						if(selGlobalAcreInc[0].get('deAcre')!= '' && selGlobalAcreInc[0].get('deAcre') != null && selGlobalAcreInc[0].get('deAcre') != undefined){
							if(parseInt(valor) < parseInt(selGlobalAcreInc[0].get('deAcre'))){
								Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
								caja.setValue('');
								return;
							}else{
								//Validacion existe en otra regla negocio
								var matriz = new Array();
		                    	  
								var reg = {};
								reg.idAcreDocto = '',
								reg.deAcre = selGlobalAcreInc[0].get('deAcre'),
								reg.aAcre = valor,
								reg.clasificacion = 'A',
								reg.tipoAcre = 'I'
	                			   
								matriz[i] = reg;
		                    	   
								var jsonString = Ext.util.JSON.encode(matriz);
								NS.jsonStringAcreedores = jsonString;
	                    	 
								NS.globalCargaAcre = true;
	 							NS.globalGridAcre = 1;
	 							
	 							if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 1){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "R", 1)
				 						}else{
				 							
				 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
											
											var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'I'
							            	});
											
											NS.gridDatosAcreedoresInc.store.add(datos);
											NS.gridDatosAcreedoresInc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 2){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "R", 1)
				 						}else{
				 							
				 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
											
											var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'I'
							            	});
											
											NS.gridDatosAcreedoresInc.store.add(datos);
											NS.gridDatosAcreedoresInc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else{
	 								validarAcreedores(jsonString, "R", 1)
	 							}
	 							
								//Valida que no exista en otra regla de negocios.
								//validarAcreedores(jsonString, "R", 1)
								
							}
						}else{
							Ext.Msg.alert('SET','Digite algun valor en el campo DE');
							caja.setValue('');
							return;
						}
					}else{
						Ext.Msg.alert('SET','El valor debe ser numérico');
						caja.setValue('');
						return;
					}
					
				}
			},
			specialkey:{
				fn: function(caja, e){
					
					if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
						return;
					}
					
					if (e.keyCode == Ext.EventObject.TAB) {
						if(isNumeric(caja.getValue())){
							if(selGlobalAcreInc[0].get('deAcre')!= '' && selGlobalAcreInc[0].get('deAcre') != null && selGlobalAcreInc[0].get('deAcre') != undefined){
								if(parseInt(caja.getValue()) < parseInt(selGlobalAcreInc[0].get('deAcre'))){
									Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
									caja.setValue('');
									return;
								}
							}
						}else{
							Ext.Msg.alert('SET','Digite algun valor en el campo DE');
							caja.setValue('');
							return;
						}
					}else if (e.keyCode == 13) {
						
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						selGlobalAcreInc = NS.gridDatosAcreedoresInc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							
							if(selGlobalAcreInc[0].get('deAcre')!= '' && selGlobalAcreInc[0].get('deAcre') != null && selGlobalAcreInc[0].get('deAcre') != undefined){
								if(parseInt(caja.getValue()) < parseInt(selGlobalAcreInc[0].get('deAcre'))){
									Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
									caja.setValue('');
									return;
								}else{
									//Validacion existe en otra regla negocio
									var matriz = new Array();
			                    	  
									var reg = {};
									reg.idAcreDocto = '',
									reg.deAcre = selGlobalAcreInc[0].get('deAcre'),
									reg.aAcre = caja.getValue(),
									reg.clasificacion = 'A',
									reg.tipoAcre = 'I'
		                			   
									matriz[i] = reg;
			                    	   
									var jsonString = Ext.util.JSON.encode(matriz);
									NS.jsonStringAcreedores = jsonString;
		                    	 
									NS.globalCargaAcre = true;
		 							NS.globalGridAcre = 1;
		 							
		 							if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 1){
		 								
		 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
					 						if (btn === 'yes'){		
					 							validarAcreedores(jsonString, "R", 1)
					 						}else{
					 							
					 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
												
												var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
												
												//Generamos un renglon en blanco
												var datos = new datosClase({
												 	idAcreDocto: '',
						 							deAcre: '',
												 	aAcre: '',
												 	clasificacion: 'A',
												 	tipoAcre: 'I'
								            	});
												
												NS.gridDatosAcreedoresInc.store.add(datos);
												NS.gridDatosAcreedoresInc.getView().refresh();
												
					 						}
					 					});
		 								
		 							}else if(validaAcreedoresGrids(selGlobalAcreInc[0], 'I') == 2){
		 								
		 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
					 						if (btn === 'yes'){		
					 							validarAcreedores(jsonString, "R", 1)
					 						}else{
					 							
					 							NS.gridDatosAcreedoresInc.store.remove(selGlobalAcreInc[0]);
												
												var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
												
												//Generamos un renglon en blanco
												var datos = new datosClase({
												 	idAcreDocto: '',
						 							deAcre: '',
												 	aAcre: '',
												 	clasificacion: 'A',
												 	tipoAcre: 'I'
								            	});
												
												NS.gridDatosAcreedoresInc.store.add(datos);
												NS.gridDatosAcreedoresInc.getView().refresh();
												
					 						}
					 					});
		 								
		 							}else{
		 								validarAcreedores(jsonString, "R", 1)
		 							}
		 							
									//Valida que no exista en otra regla de negocios.
									//validarAcreedores(jsonString, "R", 1)
								}
							}else{
								Ext.Msg.alert('SET','Digite algun valor en el campo DE.');
								caja.setValue('');
								return;
							}
							
						}else{
							Ext.Msg.alert('SET','El valor debe ser numérico');
							caja.setValue('');
							return;
						}
						
					}
				}
			}
		}
	});
	
	NS.colAcreedoresInc = new Ext.grid.ColumnModel ([
 	  	NS.colSelAcreedoresInc,
 	  	{header: 'DE', width: 100, dataIndex: 'deAcre', sortable: true, editor: txtDeInc, xtype: 'numbercolumn', format: '00' }, //},
 	  	{header: 'A', width: 100, dataIndex: 'aAcre', sortable: true, editor: txtAInc,xtype: 'numbercolumn', format: '00'}, //, format: '00'},
 	  	{header: 'idAcreDocto', width: 100, dataIndex: 'idAcreDocto', sortable: true, hidden:true},
 	  	{header: 'clasificacion', width: 100, dataIndex: 'clasificacion', sortable: true, hidden:true}, 
 	  	{header: 'tipo', width: 100, dataIndex: 'tipoAcre', sortable: true, hidden:true }
 	  	
 	]); 
	
	NS.gridDatosAcreedoresInc = new Ext.grid.EditorGridPanel ({
		store: NS.storeAcreedoresInc,
		id: PF + 'gridDatosAcreedoresInc',
		name: PF + 'gridDatosAcreedoresInc',
		cm: NS.colAcreedoresInc,
		sm: NS.colSelAcreedoresInc,
		width: 232,
		height:100,
		x: 5,		 
		y: 30,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					var seleccion = NS.gridDatosAcreedoresInc.getSelectionModel().getSelections();
					//alert(seleccion[0].get("tipoAcre") + ", " + seleccion[0].get('clasificacion'));
				}
			},
			dblclick:{
				fn: function(grid) {
					selGlobalAcreInc = NS.gridDatosAcreedoresInc.getSelectionModel().getSelections();
				}
			}
		}
	});
	
	NS.colSelAcreedoresExclu = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//textfields para hacer editables campos del grid
	var txtDeExclu = new Ext.form.NumberField ({
		enableKeyEvents: true,
		listeners: {
			change: {
				fn: function(caja, valor) {
					
					if(valor == '' || valor == NaN || valor == null || valor == undefined ){
						return;
					}
					
					if(isNumeric(valor)){
						if(selGlobalAcreExc[0].get('aAcre')!= '' && selGlobalAcreExc[0].get('aAcre')!= null && selGlobalAcreExc[0].get('aAcre')!= undefined){
							if(parseInt(valor) > parseInt(selGlobalAcreExc[0].get('aAcre'))){
								Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
								caja.setValue('');
								return;
							}
						}
						
						if(Ext.getCmp(PF + 'optIndividualAcre').getValue() == true){
		 					
							selGlobalAcreExc[0].set('deAcre', valor);
							selGlobalAcreExc[0].set('aAcre', valor);

							// SE VALIDA QUE NO EXISTA EN OTRA REGLA DE NEGOCIOS 
							var matriz = new Array();
	                    	  
							var reg = {};
							reg.idAcreDocto = '',
							reg.deAcre = valor,
							reg.aAcre = valor,
							reg.clasificacion = 'A',
							reg.tipoAcre = 'E'
                			   
							matriz[i] = reg;
	                    	   
							var jsonString = Ext.util.JSON.encode(matriz);
							NS.jsonStringAcreedores = jsonString;
                    	 
							//Valida que no exista en otra regla de negocios.
							NS.globalCargaAcre = true;
 							NS.globalGridAcre = 2;
 									
 							if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 1){
 								
 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
			 						if (btn === 'yes'){		
			 							validarAcreedores(jsonString, "I", 2)
			 						}else{
										
			 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
										
										var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
										
										//Generamos un renglon en blanco
										var datos = new datosClase({
										 	idAcreDocto: '',
				 							deAcre: '',
										 	aAcre: '',
										 	clasificacion: 'A',
										 	tipoAcre: 'E'
						            	});
										
										NS.gridDatosAcreedoresExc.store.add(datos);
										NS.gridDatosAcreedoresExc.getView().refresh();
										
			 						}
			 					});
 								
 							}else if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 2){
 								
 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
			 						if (btn === 'yes'){		
			 							validarAcreedores(jsonString, "I", 2)
			 						}else{
			 							
			 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
										
										var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
										
										//Generamos un renglon en blanco
										var datos = new datosClase({
										 	idAcreDocto: '',
				 							deAcre: '',
										 	aAcre: '',
										 	clasificacion: 'A',
										 	tipoAcre: 'E'
						            	});
										
										NS.gridDatosAcreedoresExc.store.add(datos);
										NS.gridDatosAcreedoresExc.getView().refresh();
										
			 						}
			 					});
 								
 							}else{
 								validarAcreedores(jsonString, "I", 2)
 							}

 							//validarAcreedores(jsonString, "I", 2)
	                    	 
						}else{
						
							//Solo si el valor de "A" no esta vacio
							if(selGlobalAcreExc[0].get('aAcre')!= '' && selGlobalAcreExc[0].get('aAcre') != null && selGlobalAcreExc[0].get('aAcre') != undefined){
								// SE VALIDA QUE NO EXISTA EN OTRA REGLA DE NEGOCIOS 
								var matriz = new Array();
		                    	  
								var reg = {};
								reg.idAcreDocto = '',
								reg.deAcre = valor,
								reg.aAcre = selGlobalAcreExc[0].get('aAcre'),
								reg.clasificacion = 'A',
								reg.tipoAcre = 'E'
	                			   
								matriz[i] = reg;
		                    	   
								var jsonString = Ext.util.JSON.encode(matriz);
								NS.jsonStringAcreedores = jsonString;
	                    	 
								NS.globalCargaAcre = true;
	 							NS.globalGridAcre = 2;
	 								
	 							if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 1){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "I", 2)
				 						}else{
											
				 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
											
											var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'E'
							            	});
											
											NS.gridDatosAcreedoresExc.store.add(datos);
											NS.gridDatosAcreedoresExc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 2){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "I", 2)
				 						}else{
				 							
				 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
											
											var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'E'
							            	});
											
											NS.gridDatosAcreedoresExc.store.add(datos);
											NS.gridDatosAcreedoresExc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else{
	 								validarAcreedores(jsonString, "I", 2)
	 							}
	 							
								//Valida que no exista en otra regla de negocios.
								//validarAcreedores(jsonString, "R", 2)
							}
							
						}
							
					}else{
						Ext.Msg.alert('SET','El valor debe ser numérico');
						caja.setValue('');
						return;
					}
					
				}
			},
			specialkey:{
				fn: function(caja, e){
					
					if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
						return;
					}
					
					if (e.keyCode == Ext.EventObject.TAB) {
						
						selGlobalAcreExc = NS.gridDatosAcreedoresExc.getSelectionModel().getSelections();
						
						if(selGlobalAcreExc[0].get('aAcre')!= '' && selGlobalAcreExc[0].get('aAcre')!= null && selGlobalAcreExc[0].get('aAcre')!= undefined){
							if(parseInt(caja.getValue()) > parseInt(selGlobalAcreExc[0].get('aAcre'))){
								Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
								caja.setValue('');
								return;
							}
						}
					}else if (e.keyCode == 13) {
						
						selGlobalAcreExc = NS.gridDatosAcreedoresExc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							
							if(selGlobalAcreExc[0].get('aAcre')!= '' && selGlobalAcreExc[0].get('aAcre')!= null && selGlobalAcreExc[0].get('aAcre')!= undefined){
								if(parseInt(caja.getValue()) > parseInt(selGlobalAcreExc[0].get('aAcre'))){
									Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
									caja.setValue('');
									return;
								}
							}
							
							if(Ext.getCmp(PF + 'optIndividualAcre').getValue() == true){
			 					
								selGlobalAcreExc[0].set('deAcre', caja.getValue());
								selGlobalAcreExc[0].set('aAcre', caja.getValue());

								// SE VALIDA QUE NO EXISTA EN OTRA REGLA DE NEGOCIOS 
								var matriz = new Array();
		                    	  
								var reg = {};
								reg.idAcreDocto = '',
								reg.deAcre = caja.getValue(),
								reg.aAcre = caja.getValue(),
								reg.clasificacion = 'A',
								reg.tipoAcre = 'E'
	                			   
								matriz[i] = reg;
		                    	   
								var jsonString = Ext.util.JSON.encode(matriz);
								NS.jsonStringAcreedores = jsonString;
	                    	 
								//Valida que no exista en otra regla de negocios.
								NS.globalCargaAcre = true;
	 							NS.globalGridAcre = 2;
	 								
	 							if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 1){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "I", 2)
				 						}else{
											
				 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
											
											var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'E'
							            	});
											
											NS.gridDatosAcreedoresExc.store.add(datos);
											NS.gridDatosAcreedoresExc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 2){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "I", 2)
				 						}else{
				 							
				 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
											
											var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'E'
							            	});
											
											NS.gridDatosAcreedoresExc.store.add(datos);
											NS.gridDatosAcreedoresExc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else{
	 								validarAcreedores(jsonString, "I", 2)
	 							}
	 							
								//validarAcreedores(jsonString, "I", 2);
		                    	 
							}else{
							
								//Solo si el valor de "A" no esta vacio
								if(selGlobalAcreExc[0].get('aAcre')!= '' && selGlobalAcreExc[0].get('aAcre') != null && selGlobalAcreExc[0].get('aAcre') != undefined){
									// SE VALIDA QUE NO EXISTA EN OTRA REGLA DE NEGOCIOS 
									var matriz = new Array();
			                    	  
									var reg = {};
									reg.idAcreDocto = '',
									reg.deAcre = caja.getValue(),
									reg.aAcre = selGlobalAcreExc[0].get('aAcre'),
									reg.clasificacion = 'A',
									reg.tipoAcre = 'E'
		                			   
									matriz[i] = reg;
			                    	   
									var jsonString = Ext.util.JSON.encode(matriz);
									NS.jsonStringAcreedores = jsonString;
		                    	 
									NS.globalCargaAcre = true;
		 							NS.globalGridAcre = 2;
		 								
		 							if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 1){
		 								
		 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
					 						if (btn === 'yes'){		
					 							validarAcreedores(jsonString, "I", 2)
					 						}else{
												
					 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
												
												var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
												
												//Generamos un renglon en blanco
												var datos = new datosClase({
												 	idAcreDocto: '',
						 							deAcre: '',
												 	aAcre: '',
												 	clasificacion: 'A',
												 	tipoAcre: 'E'
								            	});
												
												NS.gridDatosAcreedoresExc.store.add(datos);
												NS.gridDatosAcreedoresExc.getView().refresh();
												
					 						}
					 					});
		 								
		 							}else if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 2){
		 								
		 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
					 						if (btn === 'yes'){		
					 							validarAcreedores(jsonString, "I", 2)
					 						}else{
					 							
					 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
												
												var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
												
												//Generamos un renglon en blanco
												var datos = new datosClase({
												 	idAcreDocto: '',
						 							deAcre: '',
												 	aAcre: '',
												 	clasificacion: 'A',
												 	tipoAcre: 'E'
								            	});
												
												NS.gridDatosAcreedoresExc.store.add(datos);
												NS.gridDatosAcreedoresExc.getView().refresh();
												
					 						}
					 					});
		 								
		 							}else{
		 								validarAcreedores(jsonString, "I", 2)
		 							}
		 							
									//Valida que no exista en otra regla de negocios.
									//validarAcreedores(jsonString, "R", 2)
								}
								
							}
							
						}else{
							Ext.Msg.alert('SET','El valor debe ser numérico');
							caja.setValue('');
							return;
						}
						
					}
				}
			}
		}
	});
	
	var txtAExclu = new Ext.form.NumberField ({
		enableKeyEvents: true,
		listeners: {
			change: {
				fn: function(caja, valor) {
					
					if(valor == '' || valor == NaN || valor == null || valor == undefined ){
						return;
					}
					
					if(isNumeric(valor)){
						if(selGlobalAcreExc[0].get('deAcre')!= '' && selGlobalAcreExc[0].get('deAcre')!= null && selGlobalAcreExc[0].get('deAcre')!= undefined){
							if(parseInt(valor) < parseInt(selGlobalAcreExc[0].get('deAcre'))){
								Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
								caja.setValue('');
								return;
							}else{
								
								//Validacion existe en otra regla negocio
								var matriz = new Array();
		                    	  
								var reg = {};
								reg.idAcreDocto = '',
								reg.deAcre = selGlobalAcreExc[0].get('deAcre'),
								reg.aAcre = valor,
								reg.clasificacion = 'A',
								reg.tipoAcre = 'E'
	                			   
								matriz[i] = reg;
		                    	   
								var jsonString = Ext.util.JSON.encode(matriz);
								NS.jsonStringAcreedores = jsonString;
	                    	 
								NS.globalCargaAcre = true;
	 							NS.globalGridAcre = 2;
	 							
	 							if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 1){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "I", 2)
				 						}else{
											
				 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
											
											var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'E'
							            	});
											
											NS.gridDatosAcreedoresExc.store.add(datos);
											NS.gridDatosAcreedoresExc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 2){
	 								
	 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							validarAcreedores(jsonString, "I", 2)
				 						}else{
				 							
				 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
											
											var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
											
											//Generamos un renglon en blanco
											var datos = new datosClase({
											 	idAcreDocto: '',
					 							deAcre: '',
											 	aAcre: '',
											 	clasificacion: 'A',
											 	tipoAcre: 'E'
							            	});
											
											NS.gridDatosAcreedoresExc.store.add(datos);
											NS.gridDatosAcreedoresExc.getView().refresh();
											
				 						}
				 					});
	 								
	 							}else{
	 								validarAcreedores(jsonString, "I", 2)
	 							}
	 							
								//Valida que no exista en otra regla de negocios.
								//validarAcreedores(jsonString, "R", 2)
								
							}
						}else{
							Ext.Msg.alert('SET','Digite algun valor en el campo DE');
							caja.setValue('');
							return;
						}
					}else{
						Ext.Msg.alert('SET','El valor debe ser numérico');
						caja.setValue('');
						return;
					}
					
				}
			},
			specialkey:{
				fn: function(caja, e){
					if (e.keyCode == Ext.EventObject.TAB) {
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						if(isNumeric(caja.getValue())){
							if(selGlobalAcreExc[0].get('deAcre')!= '' && selGlobalAcreExc[0].get('deAcre')!= null && selGlobalAcreExc[0].get('deAcre')!= undefined){
								if(parseInt(caja.getValue()) < parseInt(selGlobalAcreExc[0].get('deAcre'))){
									Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
									caja.setValue('');
									return;
								}
							}else{
								Ext.Msg.alert('SET','Digite algun valor en el campo DE');
								caja.setValue('');
								return;
							}
						}else{
							Ext.Msg.alert('SET','El valor debe ser numérico');
							caja.setValue('');
							return;
						}
					}else if (e.keyCode == 13) {
						
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						selGlobalAcreExc = NS.gridDatosAcreedoresExc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							
							if(selGlobalAcreExc[0].get('deAcre')!= '' && selGlobalAcreExc[0].get('deAcre') != null && selGlobalAcreExc[0].get('deAcre') != undefined){
								if(parseInt(caja.getValue()) < parseInt(selGlobalAcreExc[0].get('deAcre'))){
									Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
									caja.setValue('');
									return;
								}else{
									
									//Validacion existe en otra regla negocio
									var matriz = new Array();
			                    	  
									var reg = {};
									reg.idAcreDocto = '',
									reg.deAcre = selGlobalAcreExc[0].get('deAcre'),
									reg.aAcre = caja.getValue(),
									reg.clasificacion = 'A',
									reg.tipoAcre = 'E'
		                			   
									matriz[i] = reg;
			                    	   
									var jsonString = Ext.util.JSON.encode(matriz);
									NS.jsonStringAcreedores = jsonString;
		                    	 
									NS.globalCargaAcre = true;
		 							NS.globalGridAcre = 2;
		 							
		 							
		 							if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 1){
		 								
		 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid INCLUIR, ¿Desea continuar?', function(btn){
					 						if (btn === 'yes'){		
					 							validarAcreedores(jsonString, "I", 2)
					 						}else{
												
					 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
												
												var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
												
												//Generamos un renglon en blanco
												var datos = new datosClase({
												 	idAcreDocto: '',
						 							deAcre: '',
												 	aAcre: '',
												 	clasificacion: 'A',
												 	tipoAcre: 'E'
								            	});
												
												NS.gridDatosAcreedoresExc.store.add(datos);
												NS.gridDatosAcreedoresExc.getView().refresh();
												
					 						}
					 					});
		 								
		 							}else if(validaAcreedoresGrids(selGlobalAcreExc[0], 'E') == 2){
		 								
		 								Ext.Msg.confirm('SET', 'Se encontraron acreedores repetidos en el grid EXCLUIR, ¿Desea continuar?', function(btn){
					 						if (btn === 'yes'){		
					 							validarAcreedores(jsonString, "I", 2)
					 						}else{
					 							
					 							NS.gridDatosAcreedoresExc.store.remove(selGlobalAcreExc[0]);
												
												var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
												
												//Generamos un renglon en blanco
												var datos = new datosClase({
												 	idAcreDocto: '',
						 							deAcre: '',
												 	aAcre: '',
												 	clasificacion: 'A',
												 	tipoAcre: 'E'
								            	});
												
												NS.gridDatosAcreedoresExc.store.add(datos);
												NS.gridDatosAcreedoresExc.getView().refresh();
												
					 						}
					 					});
		 								
		 							}else{
		 								validarAcreedores(jsonString, "I", 2)
		 							}
		 							
		 							
									//Valida que no exista en otra regla de negocios.
									//validarAcreedores(jsonString, "R", 2)
									
								}
							}else{
								Ext.Msg.alert('SET','Digite algun valor en el campo DE.');
								caja.setValue('');
								return;
							}
								
						}
							
					}
				}
			}
		}
	});
	

	NS.columnasAcreedoresExclu = new Ext.grid.ColumnModel ([
		NS.colSelAcreedoresExclu,
		{header: 'DE', width: 100, dataIndex: 'deAcre', sortable: true, editor: txtDeExclu, xtype: 'numbercolumn' , format: '00'}, //, format: '00'},
 	  	{header: 'A', width: 100, dataIndex: 'aAcre', sortable: true, editor: txtAExclu, xtype: 'numbercolumn', format: '00'}, //, format: '00'},
 	  	{header: 'idAcreDocto', width: 100, dataIndex: 'idAcreDocto', sortable: true, hidden:true},
 	  	{header: 'clasificacion', width: 100, dataIndex: 'clasificacion', sortable: true, hidden:true}, 
 	  	{header: 'tipo', width: 100, dataIndex: 'tipoAcre', sortable: true, hidden:true }
 	  	
		]); 
	
	NS.gridDatosAcreedoresExc = new Ext.grid.EditorGridPanel ({
		store: NS.storeAcreedoresExc,
		id: PF + 'gridDatosAcreedoresExc', 
		name: PF + 'gridDatosAcreedoresExc',
		cm: NS.columnasAcreedoresExclu,
		sm: NS.colSelAcreedoresExclu,
		width: 232,
		height:100,
		x: 325,
		y: 30,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					var seleccion = NS.gridDatosAcreedoresExc.getSelectionModel().getSelections();
					//alert(seleccion[0].get("tipoAcre") + ", " + seleccion[0].get('clasificacion'));
				}
			},
			dblclick:{
				fn: function(grid) {
					selGlobalAcreExc = NS.gridDatosAcreedoresExc.getSelectionModel().getSelections();
				}
			}
		}
	});

	/*NS.datosStoreWinAcre = [
   		['100-100','Prueba Grid 1', '100', '100'],
   		['10-20','Prueba Grid 2', '10', '20']	
   	];*/       		
   	 
   	NS.storeWinAcre = new Ext.data.SimpleStore({
  		idProperty: '',
  		fields: [
  		         	{name: 'rangoOriginal'},
  					{name: 'reglaNegocio'},
  					{name: 'deAcre'},
  					{name: 'aAcre'}
  				],
  		listeners:{
  			load: function(s, records){
  				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeWinAcre, msg:"Cargando..."});
  					NS.gridWinAcre.getSelectionModel().selectAll();
  				}
  			/*,add: function(store, record, index){
  				NS.gridWinAcre.getSelectionModel().selectAll();
  			}*/
  		}		
  	});
  	//NS.storeWinAcre.loadData(NS.datosStoreWinAcre);
	                      	
	                      	
	NS.colSelWinAcre = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	NS.columnasWinAcre = new Ext.grid.ColumnModel ([
		NS.colSelWinAcre,
		{header: 'Rango Original', width: 90, dataIndex: 'rangoOriginal', sortable: true},
		{header: 'Regla Negocio', width: 100, dataIndex: 'reglaNegocio', sortable: true},
		{header: 'DE', width: 80, dataIndex: 'deAcre', sortable: true, editor: txtDeExclu, xtype: 'numbercolumn' , format: '00'}, //, format: '00'},
 	  	{header: 'A', width: 80, dataIndex: 'aAcre', sortable: true, editor: txtAExclu, xtype: 'numbercolumn', format: '00'} //, format: '00'},
		]); 
	
	NS.gridWinAcre = new Ext.grid.EditorGridPanel ({
		store: NS.storeWinAcre,
		id: PF + 'gridWinAcre', 
		name: PF + 'gridWinAcre',
		cm: NS.columnasWinAcre,
		sm: NS.colSelWinAcre,
		width: 410,
		height:120,
		x: 0,
		y: 50,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					//var seleccion = NS.gridWinAcre.getSelectionModel().getSelections();
					//alert(seleccion[0].get("tipoAcre") + ", " + seleccion[0].get('clasificacion'));
				}
			}
		}
	});
	
	var winAcre = new Ext.Window({
		title: 'Acreedores existentes',
		modal: true,
		shadow: true,
		//closable: true,
		closeAction: 'hide',
		width: 425,
	   	height: 240,
	   	//x: 800,
	   	//y: 100,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    buttons: [
	              {
	           text: 'Aceptar',
	           handler: function () {
	        	   
	        	   var seleccion = NS.gridWinAcre.store.data.items; //getSelectionModel();
	        	   
	        	   var matriz = new Array();
	        	   
        		   var cont = 0;
                   for(var i=0; i<seleccion.length; i++){
                	   if(!NS.colSelWinAcre.isSelected(seleccion[i])){
                		   var reg = {};
                		   reg.deAcre = seleccion[i].get('deAcre');
                		   reg.aAcre = seleccion[i].get('aAcre');
                		   matriz[cont] = reg;
                		   cont++;
	        		   }
                   }
        		   
                   var jsonStringEliminar = Ext.util.JSON.encode(matriz);
                   cargarAcreedoresValidados(NS.jsonStringAcreedores, jsonStringEliminar);
                   winAcre.hide();	
	           }
	       },
	       {
	           text: 'Cancelar',
	           handler: function () {
	        	   Ext.Msg.confirm('SET', 'No se cargará ningún registro mostrados en esta ventana, ¿Desea continuar?', function(btn){
	 						if (btn === 'yes'){	
	 							
	 							//Elimina solo un registro si fue digitado manualmente, o todos los registros si es cara de excel
	 							if(NS.globalCargaAcre){
	 								if(NS.globalGridAcre == 1){
		 								var sel = NS.gridDatosAcreedoresInc.getSelectionModel().getSelections();
		 								NS.gridDatosAcreedoresInc.store.remove(sel[0]);
		 								
		 								var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
		 								
		 								//Generamos un renglon en blanco
		 								var datos = new datosClase({
										 	idAcreDocto: '',
				 							deAcre: '',
										 	aAcre: '',
										 	clasificacion: 'A',
										 	tipoAcre: 'I'
						            	});
										
										NS.gridDatosAcreedoresInc.store.add(datos);
										NS.gridDatosAcreedoresInc.getView().refresh();
										
		 							}else if(NS.globalGridAcre == 2){
		 								var sel = NS.gridDatosAcreedoresExc.getSelectionModel().getSelections();
		 								NS.gridDatosAcreedoresExc.store.remove(sel[0]);
		 								
		 								var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
		 								
		 								//Generamos un renglon en blanco
		 								var datos = new datosClase({
										 	idAcreDocto: '',
				 							deAcre: '',
										 	aAcre: '',
										 	clasificacion: 'A',
										 	tipoAcre: 'E'
						            	});
										
										NS.gridDatosAcreedoresExc.store.add(datos);
										NS.gridDatosAcreedoresExc.getView().refresh();
										
		 							}
									
	 							}else{
	 							
	 								var seleccion = NS.gridWinAcre.store.data.items; //getSelectionModel();
	 	 			        	   
		 							var matriz = new Array();
		 			        	   
		 							var cont = 0;
		 							for(var i=0; i<seleccion.length; i++){
	 									var reg = {};
	 									reg.deAcre = seleccion[i].get('deAcre');
	 									reg.aAcre = seleccion[i].get('aAcre');
	 									matriz[cont] = reg;
	 									cont++;
		 							}
		 		        		   
		 							var jsonStringEliminar = Ext.util.JSON.encode(matriz);
		 		                   
		 							cargarAcreedoresValidados(NS.jsonStringAcreedores, jsonStringEliminar);		 							
	 							}
	 							
	 							NS.globalCargaAcre = false;
	 							NS.globalGridAcre = 0;
	 							winAcre.hide();
	 							
	 						}
						});
	           }
	       }],
	   	items: [
				{
					  xtype:'label',
					  text: 'Seleccione registros que serán agregados a la tabla.',
					  x:0,
					  y:0
				},
				{
					  xtype:'label',
					  text: '(Si no cargó un archivo excel, \"Aceptar\" cargará el registro este seleccionado o no.)',
					  x:0,
					  y:20,
					  style: {color: '#FF0000'}
				},
	   	        NS.gridWinAcre
	   	        ],
	     listeners:{
	    	 show:{
	    		 fn:function(){
	    			 //alert("cargo show");
	    			 //NS.gridWinAcre.getSelectionModel().selectAll();
	    		 }
	    	 }
	     }	
	});
	
	NS.PanelCargaExcelAcre = new Ext.FormPanel({
		fileUpload: true,
        title: '',
        width: 350,
        height: 40,
        x:0,
        y:0,
    	autoScroll: true,
        renderTo: NS.tabContId,
        layout: 'absolute',
        monitorValid: true,
        items:[
			{
			    xtype: 'fileuploadfield',
			    id: PF + 'archivoExcelAcre',
			    emptyText: 'Archivo',
			    name: PF + 'archivoExcelAcre',
			    x:0,
			    y:0,
			    hidden: true
			},{
		 		xtype: 'button',
		 		text: 'Cargar Excel',
		 		x: 260,
		 		y: 0,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					 if (Ext.getCmp(PF + 'archivoExcelAcre').getValue() != '' ) {
		 		            	  
		 		            		   var strParams = 'nombreArchivoLeer=leerExcelAcreedores';
		 								
		 								NS.PanelCargaExcelAcre.getForm().submit({
		 		       						
		 			                       url: '/SET/jsp/bfrmwrk/gnrators/leerExcel.jsp'+ '?' + strParams,
		 			                       waitMsg: 'Leyendo Registros...',
		 			                       success: function (fp, o) {
		 			                    	  
		 			                    	  var obj = JSON.parse(o.result.json);
		 			                    	  
		 			                    	  var matriz = new Array();
		 			                    	  
		 			                    	  for(var i=0; i<obj.length; i++){
		 			                    		   var reg = {};
		 			                    		   reg.deAcre = obj[i].Inicio;
		 			                    		   reg.aAcre = obj[i].Fin;
		 			                    		   reg.clave = obj[i].Clave;
		 			                    		   matriz[i] = reg;
		 			                    	   }
		 			                    	   
		 			                    	  var jsonString = Ext.util.JSON.encode(matriz);
		 			                    	  NS.jsonStringAcreedores = jsonString;
		 			                    	 
		 			                    	  //validar Acreedores
		 			                    	 validarAcreedores(jsonString, "E");
		 			                    		   
		 			                       },
		 			                       failure: function (fp, o) {
		 			                           Ext.MessageBox.alert('ERROR', 'Ocurrio un error mientras se leeía el archivo');
		 			                       }
		 		                   });
		 		               }else{
		 		            	   Ext.Msg.alert('SET', 'Seleccione un archivo');
		 		               }
		 					 
		 				}
		 			}
		 		}
		 	}
       ]
	});
	
	NS.PanelAcreedores = new Ext.form.FieldSet ({
		title: 'Acreedores',
		id: PF + 'panelAcreedores',
		name: PF + 'panelAcreedores',
		x: 0,
		y: 650,
		width: 600,		
		height: 220,
		layout: 'absolute',
		items:
		[
		 	NS.optAcreedores,
		 	NS.lblAcreedores,
		 	NS.gridDatosAcreedoresInc,
		 	NS.lblAcreedoresExcluir,
		 	NS.gridDatosAcreedoresExc,
		 	{
				xtype: 'fieldset',
				title: '',
				width: 360,
				height: 45,
				x: 0,
				y: 135,
				layout: 'absolute',
				items: [
				        NS.PanelCargaExcelAcre
				       ]
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Nuevo',
		 		x: 400,
		 		y: 140,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					// GRID ACREEDORES INC
		 					//var datosClase = NS.gridDatosAcreedoresInc.getStore().recordType;
		 					var datosClase = NS.storeAcreedoresInc.recordType;
		 					var totalReg = NS.storeAcreedoresInc.data.items;
		 					
		 					if(totalReg.length > 0){
		 						if((totalReg[totalReg.length -1 ].get('deAcre') != ''
									&& totalReg[totalReg.length -1 ].get('deAcre') > 0) && 
									(totalReg[totalReg.length -1 ].get('aAcre') != '' 
									&& totalReg[totalReg.length -1 ].get('aAcre') > 0)){
			 					
			 						var datos = new datosClase({
									 	idAcreDocto: '',
			 							deAcre: '',
									 	aAcre: '',
									 	clasificacion: 'A',
									 	tipoAcre: 'I'
					            	});
									
									NS.storeAcreedoresInc.insert(totalReg.length, datos);
									NS.gridDatosAcreedoresInc.getView().refresh();
			 					}
		 					}else{
		 						var datos = new datosClase({
								 	idAcreDocto: '',
		 							deAcre: '',
								 	aAcre: '',
								 	clasificacion: 'A',
								 	tipoAcre: 'I'
				            	});
								
								NS.storeAcreedoresInc.insert(totalReg.length, datos);
								NS.gridDatosAcreedoresInc.getView().refresh();
		 					}
	
							// GRID ACREEDORES EXC
		 					var datosClase2 = NS.storeAcreedoresExc.recordType;
							var totalReg2 = NS.storeAcreedoresExc.data.items;
							if(totalReg2.length > 0){
								//validamos el ultimo que no este vacio, si esta vacio no agrega nuevo renglon
								if((totalReg2[totalReg2.length -1 ].get('deAcre') != ''
									&& totalReg2[totalReg2.length -1 ].get('deAcre') > 0) && 
									(totalReg2[totalReg2.length -1 ].get('aAcre') != '' 
									&& totalReg2[totalReg2.length -1 ].get('aAcre') > 0)){
									
				 					var datos2 = new datosClase2({
				 						idAcreDocto: '',
									 	deAcre: '',
									 	aAcre: '',
									 	clasificacion: 'A',
									 	tipoAcre: 'E'
					            	});
									
									NS.storeAcreedoresExc.insert(totalReg2.length, datos2);
									NS.gridDatosAcreedoresExc.getView().refresh();
									
								}
							}else{
								
								var datos2 = new datosClase2({
			 						idAcreDocto: '',
								 	deAcre: '',
								 	aAcre: '',
								 	clasificacion: 'A',
								 	tipoAcre: 'E'
				            	});
								
								NS.storeAcreedoresExc.insert(totalReg2.length, datos2);
								NS.gridDatosAcreedoresExc.getView().refresh();
							}
							
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 490,
		 		y: 140,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					var selInc = NS.gridDatosAcreedoresInc.getSelectionModel().getSelections();
		 					
		 					var matriz = new Array();
		 					var count = 0;
		 					
		 					for(var i=0; i<selInc.length; i++){
		 						//Agregado para eliminaciones		 						
		 						if(selInc[i].get('idAcreDocto') != '' && selInc[i].get('idAcreDocto') > 0){
		 							var registro ={};
		 							registro.idAcreDocto = selInc[i].get('idAcreDocto');
		 							matriz[count] = registro;
		 							count++;
		 						}
		 						
		 						NS.gridDatosAcreedoresInc.store.remove(selInc[i]);
		 					}
		 					
		 					
		 					var selExc = NS.gridDatosAcreedoresExc.getSelectionModel().getSelections();
		 					
		 					for(var i=0; i<selExc.length; i++){
		 						
		 						//Agregado para eliminaciones
		 						if(selExc[i].get('idAcreDocto') != '' && selExc[i].get('idAcreDocto') > 0){
		 							var registro ={};
		 							registro.idAcreDocto = selExc[i].get('idAcreDocto');
		 							matriz[count] = registro;
		 							count++;
		 						}
		 						
		 						NS.gridDatosAcreedoresExc.store.remove(selExc[i]);
		 					}
		 					
		 					var jsonTemp = Ext.util.JSON.encode(matriz);
		 					if(NS.jsonEliminarAcre.length > 0){
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 0){
		 							NS.jsonEliminarAcre = NS.jsonEliminarAcre.substring(0, NS.jsonEliminarAcre.length -1) +"," + jsonTemp + "]";
		 						}
		 					}else{
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 2){ //se quitaron los corchetes, si es mayor a dos hay datos
		 							NS.jsonEliminarAcre = "[" + jsonTemp + "]";
		 						}
		 					}
		 					
		 				}
		 			}
		 		}
		 	}
	    ]
	});
	
	/********** FIN PANEL ACREEDORES ***********/ 
	
	
	/********** PANEL DOCUMENTO ***********/ 
	
	NS.lblDocumento = new Ext.form.Label({
		text: 'Documento a Incluir',
		x: 0, 
		y: 0,
	});
	
	NS.lblDocumentoExcluir = new Ext.form.Label({
		text: 'Documento a Excluir',
		x: 325, 
		y: 0,
	});
	
	NS.optDocumentos = new Ext.form.RadioGroup({
		id: PF + 'optDocumentos',
		name: PF + 'optDocumentos',
		x: 245,
		y: 50,
		columns: 1, 
		items: [
	          {boxLabel: 'Individual', id: PF + 'optIndividualDoc', name: PF + 'optDocumentos', inputValue: 'I', checked: true },// checked: true},  
	          {boxLabel: 'Rango', id: PF + 'optRangoDoc', name: PF + 'optDocumentos', inputValue: 'R'}
	     ]
	});
	
	NS.colSelDocumentoInc = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//textfields para hacer editables campos del grid
	var txtDeDocInc = new Ext.form.TextField({
		enableKeyEvents: true,
		listeners: {
			change: {
				fn: function(caja, valor) {
					
					if(valor == '' || valor == NaN || valor == null || valor == undefined ){
						return;
					}
					
					if(isNumeric(valor)){
						if(selGlobalDoctoInc[0].get('aAcre')!= '' && selGlobalDoctoInc[0].get('aAcre')!= null && selGlobalDoctoInc[0].get('aAcre')!= undefined){
							
							if(parseInt(valor) > parseInt(selGlobalDoctoInc[0].get('aAcre'))){
								Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
								caja.setValue('');
								return;
							}
						}
						
						if(Ext.getCmp(PF + 'optIndividualDoc').getValue() == true){
		 					
							//Concatenamos a 10 digitos
							valor = concatenaDiezDigitos(caja);
							
							if(valor == undefined){
								valor = caja.getValue();
							}
							
							selGlobalDoctoInc[0].set('deAcre', valor);
							selGlobalDoctoInc[0].set('aAcre', valor);
							
							NS.gridDocumentoInc.getView().refresh();
							
						}else{
							concatenaDiezDigitos(caja);
						}
						
					}else{
						Ext.Msg.alert('SET','El valor no es númerico');
						caja.setValue('');
						return;
					}
					
					//Concatenamos a 10 digitos
					//concatenaDiezDigitos(caja);
					
				}
			},
			specialkey:{
				fn: function(caja, e){
					if (e.keyCode == Ext.EventObject.TAB) {
						
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						selGlobalDoctoInc = NS.gridDocumentoInc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							if(selGlobalDoctoInc[0].get('aAcre')!= '' && selGlobalDoctoInc[0].get('aAcre')!= null && selGlobalDoctoInc[0].get('aAcre')!= undefined){
								
								if(parseInt(caja.getValue()) > parseInt(selGlobalDoctoInc[0].get('aAcre'))){
									Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
									caja.setValue('');
									return;
								}
							}
						}else{
							Ext.Msg.alert('SET','El valor no es númerico');
							caja.setValue('');
							return;
						}
						
						//Concatenamos a 10 digitos
						concatenaDiezDigitos(caja);
						
					}else if (e.keyCode == 13) {
						
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						selGlobalDoctoInc = NS.gridDocumentoInc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							
							if(selGlobalDoctoInc[0].get('aAcre')!= '' && selGlobalDoctoInc[0].get('aAcre')!= null && selGlobalDoctoInc[0].get('aAcre')!= undefined){
								if(parseInt(caja.getValue()) > parseInt(selGlobalDoctoInc[0].get('aAcre'))){
									Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
									caja.setValue('');
									return;
								}
							}
							
							if(Ext.getCmp(PF + 'optIndividualDoc').getValue() == true){
			 					
								//Concatenamos a 10 digitos
								concatenaDiezDigitos(caja);
								
								selGlobalDoctoInc[0].set('deAcre', caja.getValue());
								selGlobalDoctoInc[0].set('aAcre', caja.getValue());
								
								NS.gridDocumentoInc.getView().refresh();
								
							}else{ //optRango ó ninguno
								//Concatenamos a 10 digitos
								concatenaDiezDigitos(caja);
								
							}
							
						}else{
							Ext.Msg.alert('SET','El valor debe ser numérico');
							caja.setValue('');
							return;
						}
						
					}
				}
			}
			
		}
	});
	
	var txtADocInc = new Ext.form.TextField({
		enableKeyEvents: true,
		listeners: {
			change: {
				fn: function(caja, valor) {
					
					if(valor == '' || valor == NaN || valor == null || valor == undefined ){
						return;
					}
					
					if(isNumeric(valor)){
						if(selGlobalDoctoInc[0].get('deAcre')!= '' && selGlobalDoctoInc[0].get('deAcre')!= null && selGlobalDoctoInc[0].get('deAcre')!= undefined){
							if(parseInt(valor) < parseInt(selGlobalDoctoInc[0].get('deAcre'))){
								Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
								caja.setValue('');
								return;
							}
						}else{
							Ext.Msg.alert('SET','Digite un valor en el campo DE.');
							caja.setValue('');
							return;
						}
					}else{
						Ext.Msg.alert('SET','El valor no es númerico');
						caja.setValue('');
						return;
					}
					
					//Concatenamos a 10 digitos
					concatenaDiezDigitos(caja);
					
				}
			},
			specialkey:{
				fn: function(caja, e){
					
					if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
						return;
					} 
					
					if (e.keyCode == Ext.EventObject.TAB) {
						
						selGlobalDoctoInc = NS.gridDocumentoInc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							if(selGlobalDoctoInc[0].get('deAcre')!= '' && selGlobalDoctoInc[0].get('deAcre')!= null && selGlobalDoctoInc[0].get('deAcre')!= undefined){
								if(parseInt(caja.getValue()) < parseInt(selGlobalDoctoInc[0].get('deAcre'))){
									Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
									caja.setValue('');
									return;
								}
							}else{
								Ext.Msg.alert('SET','Digite un valor en el campo DE.');
								caja.setValue('');
								return;
							}
						}else{
							Ext.Msg.alert('SET','El valor no es númerico');
							caja.setValue('');
							return;
						}
						
						//Concatenamos a 10 digitos
						concatenaDiezDigitos(caja);
						
					}else if (e.keyCode == 13) {
						
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						selGlobalDoctoInc = NS.gridDocumentoInc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							
							if(selGlobalDoctoInc[0].get('deAcre')!= '' && selGlobalDoctoInc[0].get('deAcre') != null && selGlobalDoctoInc[0].get('deAcre') != undefined){
								if(parseInt(caja.getValue()) < parseInt(selGlobalDoctoInc[0].get('deAcre'))){
									Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
									caja.setValue('');
									return;
								}
							}else{
								Ext.Msg.alert('SET','Digite algun valor en el campo DE.');
								caja.setValue('');
								return;
							}
								
							//Concatenamos a 10 digitos
							concatenaDiezDigitos(caja);

						}
						
					}
				}
			}
		}
	});
	
	NS.colDocumentoInc = new Ext.grid.ColumnModel ([
		NS.colSelDocumentoInc,
		{header: 'DE', width: 100, dataIndex: 'deAcre', sortable: true, editor: txtDeDocInc }, //, xtype: 'numbercolumn', format: '00'}, //, format: '00'},
 	  	{header: 'A', width: 100, dataIndex: 'aAcre', sortable: true, editor: txtADocInc }, //, xtype: 'numbercolumn', format: '00'}, //, format: '00'},
 	  	{header: 'idAcreDocto', width: 100, dataIndex: 'idAcreDocto', sortable: true, hidden:true},
 	  	{header: 'clasificacion', width: 100, dataIndex: 'clasificacion', sortable: true, hidden:true}, 
 	  	{header: 'tipo', width: 100, dataIndex: 'tipoAcre', sortable: true, hidden:true }
 	  	
		]); 
	
	NS.gridDocumentoInc = new Ext.grid.EditorGridPanel ({
		store: NS.storeDocumentoInc,
		id: PF + 'gridDocumentoInc',
		name: PF + 'gridDocumentoInc',
		cm: NS.colDocumentoInc,
		sm: NS.colSelDocumentoInc,
		width: 232,
		height:100,
		x: 5,		 
		y: 30,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					var seleccion = NS.gridDocumentoInc.getSelectionModel().getSelections();
					//alert(seleccion[0].get("tipoAcre") + ", " + seleccion[0].get('clasificacion'));
				}
			},
			dblclick: {
				fn: function(grid) {
					selGlobalDoctoInc = NS.gridDocumentoInc.getSelectionModel().getSelections();
				}
			}
		}
	});

	NS.colSelDocumentoExc = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//textfields para hacer editables campos del grid
	var txtDeDocExclu = new Ext.form.TextField({
		enableKeyEvents: true,
		//tabIndex:10,
		listeners: {
			change: {
				fn: function(caja, valor) {
					
					if(valor == '' || valor == NaN || valor == null || valor == undefined ){
						return;
					}
					
					if(isNumeric(valor)){
						if(selGlobalDoctoExc[0].get('aAcre')!= '' && selGlobalDoctoExc[0].get('aAcre')!= null && selGlobalDoctoExc[0].get('aAcre')!= undefined){
							if(parseInt(valor) > parseInt(selGlobalDoctoExc[0].get('aAcre'))){
								Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
								caja.setValue('');
								return;
							}
						}
						
						if(Ext.getCmp(PF + 'optIndividualDoc').getValue() == true){
		 					
							//Concatenamos a 10 digitos
							valor = concatenaDiezDigitos(caja);
							
							if(valor == undefined){
								valor = caja.getValue();
							}
							
							selGlobalDoctoExc[0].set('deAcre', valor);
							selGlobalDoctoExc[0].set('aAcre', valor);
							
							NS.gridDocumentoExc.getView().refresh();
							
						}else{
							concatenaDiezDigitos(caja);
						}
						
					}else{
						Ext.Msg.alert('SET','El valor no es númerico');
						caja.setValue('');
						return;
					}
					
					//Concatenamos a 10 digitos
					//concatenaDiezDigitos(caja);
					
				}
			},
			specialkey:{
				fn: function(caja, e){
					
					if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
						return;
					}
					
					if (e.keyCode == Ext.EventObject.TAB) {
						
						selGlobalDoctoExc = NS.gridDocumentoExc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							if(selGlobalDoctoExc[0].get('aAcre')!= '' && selGlobalDoctoExc[0].get('aAcre')!= null && selGlobalDoctoExc[0].get('aAcre')!= undefined){
								if(parseInt(caja.getValue()) > parseInt(selGlobalDoctoExc[0].get('aAcre'))){
									Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
									caja.setValue('');
									return;
								}
							}
						}else{
							Ext.Msg.alert('SET','El valor no es númerico');
							caja.setValue('');
							return;
						}
						
						//Concatenamos a 10 digitos
						concatenaDiezDigitos(caja);
						
					}else if (e.keyCode == 13) {
						
						//Inicia
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						selGlobalDoctoExc = NS.gridDocumentoExc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							
							if(selGlobalDoctoExc[0].get('aAcre')!= '' && selGlobalDoctoExc[0].get('aAcre')!= null && selGlobalDoctoExc[0].get('aAcre')!= undefined){
								if(parseInt(caja.getValue()) > parseInt(selGlobalDoctoExc[0].get('aAcre'))){
									Ext.Msg.alert('SET','El valor "DE" debe ser menor al valor "A"');
									caja.setValue('');
									return;
								}
							}
							
							if(Ext.getCmp(PF + 'optIndividualDoc').getValue() == true){
			 					
								//Concatenamos a 10 digitos
								concatenaDiezDigitos(caja);
								
								selGlobalDoctoExc[0].set('deAcre', caja.getValue());
								selGlobalDoctoExc[0].set('aAcre', caja.getValue());
								
								NS.gridDocumentoExc.getView().refresh();
								
							}else{
								
								//Concatenamos a 10 digitos
								concatenaDiezDigitos(caja);
								
							}
						}else{
							Ext.Msg.alert('SET','El valor debe ser numérico');
							caja.setValue('');
							return;
						}
						
					} //Fin
				}
			}
		}
	});
	
	var txtADocExclu = new Ext.form.TextField({
		enableKeyEvents: true,
		tabIndex:11,
		listeners: {
			change: {
				fn: function(caja, valor) {
					
					if(valor == '' || valor == NaN || valor == null || valor == undefined ){
						return;
					}
					
					if(isNumeric(valor)){
						if(selGlobalDoctoExc[0].get('deAcre')!= '' && selGlobalDoctoExc[0].get('deAcre')!= null && selGlobalDoctoExc[0].get('deAcre')!= undefined){
							if(parseInt(valor) < parseInt(selGlobalDoctoExc[0].get('deAcre'))){
								Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
								caja.setValue('');
								return;
							}
						}else{
							Ext.Msg.alert('SET','Digite un valor en el campo DE.')
							caja.setValue('');
							return;
						}
					}else{
						Ext.Msg.alert('SET','El valor no es númerico');
						caja.setValue('');
						return;
					}
					
					//Concatenamos a 10 digitos
					concatenaDiezDigitos(caja);
					
				}
			},
			/*keypress:{
				fn: function(caja, e){
					alert(e.keyCode);
				}
			},*/
			specialkey:{
				fn: function(caja, e){
					if (e.keyCode == Ext.EventObject.TAB) {
						
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						selGlobalDoctoExc = NS.gridDocumentoExc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							if(selGlobalDoctoExc[0].get('deAcre')!= '' && selGlobalDoctoExc[0].get('deAcre')!= null && selGlobalDoctoExc[0].get('deAcre')!= undefined){
								if(parseInt(caja.getValue()) < parseInt(selGlobalDoctoExc[0].get('deAcre'))){
									Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
									caja.setValue('');
									return;
								}
							}else{
								Ext.Msg.alert('SET','Digite un valor en el campo DE.')
								caja.setValue('');
								return;
							}
						}else{
							Ext.Msg.alert('SET','El valor no es númerico');
							caja.setValue('');
							return;
						}
						
						//Concatenamos a 10 digitos
						concatenaDiezDigitos(caja);
						
					}else if (e.keyCode == 13) { //Enter
						
						if(caja.getValue() == '' || caja.getValue() == NaN || caja.getValue() == null || caja.getValue() == undefined ){
							return;
						}
						
						selGlobalDoctoExc = NS.gridDocumentoExc.getSelectionModel().getSelections();
						
						if(isNumeric(caja.getValue())){
							
							if(selGlobalDoctoExc[0].get('deAcre')!= '' && selGlobalDoctoExc[0].get('deAcre') != null && selGlobalDoctoExc[0].get('deAcre') != undefined){
								if(parseInt(caja.getValue()) < parseInt(selGlobalDoctoExc[0].get('deAcre'))){
									Ext.Msg.alert('SET','El valor "A" debe ser mayor al valor "DE"');
									caja.setValue('');
									return;
								}
							}else{
								Ext.Msg.alert('SET','Digite algun valor en el campo DE.');
								caja.setValue('');
								return;
							}
								
							//Concatenamos a 10 digitos
							concatenaDiezDigitos(caja);

						}
						
					}
				}
			}
			
		}
	});
	
	NS.colDocumentoExc = new Ext.grid.ColumnModel ([
		NS.colSelDocumentoExc,
		{header: 'DE', width: 100, dataIndex: 'deAcre', sortable: true, editor: txtDeDocExclu},//, xtype: 'numbercolumn', format: '00'}, //, format: '00'},
 	  	{header: 'A', width: 100, dataIndex: 'aAcre', sortable: true, editor: txtADocExclu},//, xtype: 'numbercolumn', format: '00'},
 	  	{header: 'idAcreDocto', width: 100, dataIndex: 'idAcreDocto', sortable: true, hidden:true},
 	  	{header: 'clasificacion', width: 100, dataIndex: 'clasificacion', sortable: true, hidden:true}, 
 	  	{header: 'tipo', width: 100, dataIndex: 'tipoAcre', sortable: true, hidden:true }
		]); 
	
	NS.gridDocumentoExc = new Ext.grid.EditorGridPanel ({
		store: NS.storeDocumentoExc,
		id: PF + 'gridDocumentoExc',
		name: PF + 'gridDocumentoExc',
		cm: NS.colDocumentoExc,
		sm: NS.colSelDocumentoExc,
		width: 232,
		height:100,
		x: 325,
		y: 30,
		stripeRows: true,
		columnLines: true,
		tabIndex:9,
		listeners: {
			click: {
				fn: function(grid) {
					var seleccion = NS.gridDocumentoExc.getSelectionModel().getSelections();
					//alert(seleccion[0].get("tipoAcre") + ", " + seleccion[0].get('clasificacion'));
				}
			},
			dblclick: {
				fn: function(grid) {
					selGlobalDoctoExc = NS.gridDocumentoExc.getSelectionModel().getSelections();
				}
			}
			/*keypress:{
				fn: function(grid){
					alert(grid.keyCode);
					alert(Ext.EventObject.TAB);
					if (grid.keyCode == Ext.EventObject.TAB) {
						
					}else{
						alert('otra cosa');
					}
				}
			}
			*/
		}
	});

	
	NS.PanelCargaExcelDocto = new Ext.FormPanel({
		fileUpload: true,
        title: '',
        width: 350,
        height: 45,
        x:0,
        y:0,
    	autoScroll: true,
        renderTo: NS.tabContId,
        layout: 'absolute',
        monitorValid: true,
        items:[
			{
			    xtype: 'fileuploadfield',
			    id: PF + 'archivoExcelDocto',
			    emptyText: 'Archivo',
			    name: PF + 'archivoExcelDocto',
			    x:0,
			    y:0,
			    hidden: true
			},{
		 		xtype: 'button',
		 		text: 'Cargar Excel',
		 		x: 260,
		 		y: 0,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					 if (Ext.getCmp(PF + 'archivoExcelDocto').getValue() != '' ) {
		 		            	   //NS.PanelCargaExcelDocto.getForm().submit({
		 		            		   var strParams = 'nombreArchivoLeer=leerExcelDocumento';
		 								
		 								NS.PanelCargaExcelDocto.getForm().submit({
		 		       						
		 			                       url: '/SET/jsp/bfrmwrk/gnrators/leerExcel.jsp'+ '?' + strParams,
		 			                       waitMsg: 'Leyendo Registros...',
		 			                       success: function (fp, o) {
		 			                    	  
		 			                    	  var obj = JSON.parse(o.result.json);
		 			                    	  
		 			                    	  var matriz = new Array();
		 			                    	  for(var i=0; i<obj.length; i++){
		 			                    		   var reg = {};
		 			                    		   reg.deAcre = obj[i].Inicio;
		 			                    		   reg.aAcre = obj[i].Fin;
		 			                    		   reg.clave = obj[i].Clave;
		 			                    		   matriz[i] = reg;
		 			                    	   }
		 			                    	   
		 			                    	  var jsonString = Ext.util.JSON.encode(matriz);
		 			                    	   
		 			                    	   ReglasNegocioAction.validarDocumento(jsonString, function(result, e){
		 			                    		   
		 			                    		  if(result != null && result != '' && result != undefined) {								
		 			             					
		 			         						if(result.error != ''){
		 			         							Ext.Msg.alert('SET',result.error);
		 			         	 						return;
		 			         						}else if(result.mensaje == 'Ok'){
		 			         							
		 			         							//Una vez validados cargamos los datos al grid
		 			         							 ReglasNegocioAction.obtenerDatosValidados(jsonString, 'D', function(result, e){
		 			 			                    		   
		 			         								var datosClase = NS.storeDocumentoInc.recordType;
		 			         								
		 			         								// GRID ACREEDORES EXC
	 			         				 					var datosClase2 = NS.storeDocumentoExc.recordType;
	 			         									
		 			         								 for(var i = 0; i < result.length; i++){
		 			         				 					
		 			         									 //Los totales se van incrementando, por eso se sacan en cada vuelta del ciclo
		 			         									var totalReg = NS.storeDocumentoInc.data.items;
		 			         									var totalReg2 = NS.storeDocumentoExc.data.items;
		 			         									
		 			         									 if(result[i].tipoAcre == 'I'){
		 			         										
		 			         										var datos = new datosClase({
	 			         											 	idAcreDocto: '',
	 			         					 							deAcre: result[i].deAcre,
	 			         											 	aAcre: result[i].aAcre,
	 			         											 	clasificacion: result[i].clasificacion,
	 			         											 	tipoAcre: result[i].tipoAcre
	 			         							            	});
	 			         											
	 			         											NS.storeDocumentoInc.insert(totalReg.length, datos);
	 			         											
		 			         									 }else if(result[i].tipoAcre == 'E'){
		 			         										 
		 			         										var datos2 = new datosClase2({
	 			         						 						idAcreDocto: '',
	 			         											 	deAcre: result[i].deAcre,
	 			         											 	aAcre: result[i].aAcre,
	 			         											 	clasificacion: result[i].clasificacion,
	 			         											 	tipoAcre: result[i].tipoAcre
	 			         							            	});
	 			         											
	 			         											NS.storeDocumentoExc.insert(totalReg2.length, datos2);
	 			         											
		 			         									 }
		 			         								 }
		 			         								 
		 			         								NS.gridDocumentoInc.getView().refresh();
		 			         								NS.gridDocumentoExc.getView().refresh();
		 			         								
		 			 			                    	   });
		 			         							 
		 			         						}
		 			         						
		 			         					}
		 			                    		  
		 			                    	   });
		 			                    	   
		 			                       },
		 			                       failure: function (fp, o) {
		 			                           Ext.MessageBox.alert('ERROR', 'Ocurrio un error mientras se leeía el archivo');
		 			                       }
		 		                   });
		 		               }else{
		 		            	   Ext.Msg.alert('SET', 'Seleccione un archivo');
		 		               }
		 					 
		 				}
		 			}
		 		}
		 	}
       ]
	});
	
	
	NS.PanelDocumento = new Ext.form.FieldSet ({
		//fileUpload: true,
		title: 'Documento',
		id: PF + 'panelDocumento',
		name: PF + 'panelDocumento',
		x: 0,
		y: 876,
		width: 600,		
		height: 220,
		layout: 'absolute',
		monitorValid: true,
		items:
		[
		 	NS.optDocumentos,
		 	NS.lblDocumento,
		 	NS.lblDocumentoExcluir,
		 	NS.gridDocumentoInc,
		 	NS.gridDocumentoExc,
		 	{
				xtype: 'fieldset',
				title: '',
				width: 360,
				height: 45,
				x: 0,
				y: 135,
				layout: 'absolute',
				items: [
				        NS.PanelCargaExcelDocto
				       ]
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Nuevo',
		 		x: 400,
		 		y: 135,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					//var datosClase = NS.gridDatosAcreedoresInc.getStore().recordType;
		 					var datosClase = NS.storeDocumentoInc.recordType;
		 					var totalReg = NS.storeDocumentoInc.data.items;
		 					
		 					if(totalReg.length > 0){
		 						if((totalReg[totalReg.length -1 ].get('deAcre') != ''
									&& totalReg[totalReg.length -1 ].get('deAcre') > 0) && 
									(totalReg[totalReg.length -1 ].get('aAcre') != '' 
									&& totalReg[totalReg.length -1 ].get('aAcre') > 0)){
			 					
			 						var datos = new datosClase({
									 	idAcreDocto: '',
			 							deAcre: '',
									 	aAcre: '',
									 	clasificacion: 'D',
									 	tipoAcre: 'I'
					            	});
									
									NS.storeDocumentoInc.insert(totalReg.length, datos);
									NS.gridDocumentoInc.getView().refresh();
			 					}
		 					}else{
		 						var datos = new datosClase({
								 	idAcreDocto: '',
		 							deAcre: '',
								 	aAcre: '',
								 	clasificacion: 'D',
								 	tipoAcre: 'I'
				            	});
								
								NS.storeDocumentoInc.insert(totalReg.length, datos);
								NS.gridDocumentoInc.getView().refresh();
		 					}
	
							// GRID ACREEDORES EXC
		 					var datosClase2 = NS.storeDocumentoExc.recordType;
							var totalReg2 = NS.storeDocumentoExc.data.items;
							if(totalReg2.length > 0){
								//validamos el ultimo que no este vacio, si esta vacio no agrega nuevo renglon
								if((totalReg2[totalReg2.length -1 ].get('deAcre') != ''
									&& totalReg2[totalReg2.length -1 ].get('deAcre') > 0) && 
									(totalReg2[totalReg2.length -1 ].get('aAcre') != '' 
									&& totalReg2[totalReg2.length -1 ].get('aAcre') > 0)){
									
				 					var datos2 = new datosClase2({
				 						idAcreDocto: '',
									 	deAcre: '',
									 	aAcre: '',
									 	clasificacion: 'D',
									 	tipoAcre: 'E'
					            	});
									
									NS.storeDocumentoExc.insert(totalReg2.length, datos2);
									NS.gridDocumentoExc.getView().refresh();
									
								}
							}else{
								
								var datos2 = new datosClase2({
			 						idAcreDocto: '',
								 	deAcre: '',
								 	aAcre: '',
								 	clasificacion: 'D',
								 	tipoAcre: 'E'
				            	});
								
								NS.storeDocumentoExc.insert(totalReg2.length, datos2);
								NS.gridDocumentoExc.getView().refresh();
							}
							
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 490,
		 		y: 135,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					var selInc = NS.gridDocumentoInc.getSelectionModel().getSelections();
		 					
		 					var matriz = new Array();
		 					var count = 0;
		 					
		 					for(var i=0; i<selInc.length; i++){
		 						//Agregado para eliminaciones
		 						if(selInc[i].get('idAcreDocto') != '' && selInc[i].get('idAcreDocto') > 0){
		 							var registro ={};
		 							registro.idAcreDocto = selInc[i].get('idAcreDocto');
		 							matriz[count] = registro;
		 							count++;
		 						}
		 						
		 						NS.gridDocumentoInc.store.remove(selInc[i]);
		 					}
		 					
		 					var selExc = NS.gridDocumentoExc.getSelectionModel().getSelections();
		 					
		 					for(var i=0; i<selExc.length; i++){
		 						
		 						//Agregado para eliminaciones
		 						if(selExc[i].get('idAcreDocto') != '' && selExc[i].get('idAcreDocto') > 0){
		 							var registro ={};
		 							registro.idAcreDocto = selExc[i].get('idAcreDocto');
		 							matriz[count] = registro;
		 							count++;
		 						}
		 						
		 						NS.gridDocumentoExc.store.remove(selExc[i]);
		 					}
		 					
		 					var jsonTemp = Ext.util.JSON.encode(matriz);
		 					if(NS.jsonEliminarDoctos.length > 0){
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 0){
		 							NS.jsonEliminarDoctos = NS.jsonEliminarDoctos.substring(0, NS.jsonEliminarDoctos.length -1) +"," + jsonTemp + "]";
		 						}
		 					}else{
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 2){
		 							NS.jsonEliminarDoctos = "[" + jsonTemp + "]";
		 						}
		 					}

		 				}
		 			}
		 		}
		 	}
		    ]
	});
	
	/********** FIN PANEL DOCUMENTO ***********/ 
	
	/********** PANEL CLASE DOCUMENTO ***********/ 
	
	NS.colSelClaseDocumento = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//textfields para hacer editables campos del grid
	var txtClaseDoctoInc = new Ext.form.TextField({});
	var txtClaseDoctoExc = new Ext.form.TextField({});
	
	NS.colClaseDocumento = new Ext.grid.ColumnModel ([
		NS.colSelClaseDocumento,
		{header: 'INCLUIR', width: 100, dataIndex: 'incluir', sortable: true, editor: txtClaseDoctoInc}, //, format: '00'},
 	  	{header: 'EXCLUIR', width: 100, dataIndex: 'excluir', sortable: true, editor: txtClaseDoctoExc}, //, format: '00'},
		{header: 'idDoctoTes', width: 100, dataIndex: 'idDoctoTes', sortable: true, hidden: true}, //, format: '00'},
 	  	{header: 'clasificacion', width: 100, dataIndex: 'clasificacion', sortable: true, hidden:true}
		]); 

	 NS.gridClaseDocto = new Ext.grid.EditorGridPanel ({
	 	store: NS.storeClaseDocto,
	 	id: PF + 'gridClaseDocto',
	    name: PF + 'gridClaseDocto',
	 	cm: NS.colClaseDocumento,
	 	sm: NS.colSelClaseDocumento,
	 	width: 332,
	 	height:100,
	 	x: 100,		 
	 	y: 0,
	 	stripeRows: true,
	 	columnLines: true,
	 	listeners: {
	 		click: {
	 			fn: function(grid) {
	 				var seleccion = NS.gridClaseDocto.getSelectionModel().getSelections();
					//alert(seleccion[0].get('clasificacion'));
	 			}
	 		}
	 	}
	 });

	                                                     
	NS.PanelClaseDocumento = new Ext.form.FieldSet ({
		title: 'Clase de Documento',
		id: PF + 'panelClaseDocumento',
		name: PF + 'panelClaseDocumento',
		x: 0,
		y: 1103,
		width: 600,		
		height: 165,
		layout: 'absolute',
		items:
		[
		 	NS.gridClaseDocto,
		 	{
		 		xtype: 'button',
		 		text: 'Nuevo',
		 		x: 270,
		 		y: 105,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					//var datosClase = NS.storeClaseDocto.getStore().recordType;
		 					var datosClase = NS.storeClaseDocto.recordType;
		 					var totalReg = NS.storeClaseDocto.data.items;
		 					
		 					if(totalReg.length > 0){
		 						if(totalReg[totalReg.length -1 ].get('incluir') != ''
									|| totalReg[totalReg.length -1 ].get('excluir') != '' ){
			 					
			 						var datos = new datosClase({
									 	idDoctoTes: '',
			 							incluir: '',
									 	excluir: '',
									 	clasificacion: 'D'
					            	});
									
									NS.storeClaseDocto.insert(totalReg.length, datos);
									NS.gridClaseDocto.getView().refresh();
			 					}
		 					}else{
		 						var datos = new datosClase({
		 							idDoctoTes: '',
		 							incluir: '',
								 	excluir: '',
								 	clasificacion: 'D'
				            	});
								
		 						NS.storeClaseDocto.insert(totalReg.length, datos);
								NS.gridClaseDocto.getView().refresh();
		 					}
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 350,
		 		y: 105,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					var selInc = NS.gridClaseDocto.getSelectionModel().getSelections();
		 					
		 					var matriz = new Array();
		 					var count = 0;
		 					
		 					for(var i=0; i<selInc.length; i++){
		 						//Agregado para eliminaciones
		 						var registro ={};
		 						if(selInc[i].get('idDoctoTes') != '' && selInc[i].get('idDoctoTes') > 0){
		 							registro.idDoctoTes = selInc[i].get('idDoctoTes');
		 							matriz[count] = registro;
		 							count++;
		 						}
		 						
		 						NS.gridClaseDocto.store.remove(selInc[i]);
		 					}
		 					
		 					var jsonTemp = Ext.util.JSON.encode(matriz);
		 					if(NS.jsonEliminarClase.length > 0){
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 0){
		 							NS.jsonEliminarClase = NS.jsonEliminarClase.substring(0, NS.jsonEliminarClase.length -1) +"," + jsonTemp + "]";
		 						}
		 					}else{
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 2){
		 							NS.jsonEliminarClase = "[" + jsonTemp + "]";
		 						}
		 					}
		 					
		 				}
		 			}
		 		}
		 	}
		    ]
	});	 
	
	/********** FIN PANEL CLASE DOCUMENTO ***********/ 
	
	/********** PANEL USUARIO ***********/ 

	NS.lblUsuarios = new Ext.form.Label({
		text: 'Usuario',
		x: 0, 
		y: 0
	});
	
	NS.txtUsuarios = new Ext.form.TextField({
		id: PF + 'txtUsuarios',
		name: PF + 'txtUsuarios',
		x: 50,
		y: 0,
		width: 350,
		tabIndex: 3
	});
	
	NS.PanelUsuario = new Ext.form.FieldSet ({
		title: 'Usuario',
		id: PF + 'panelUsuario',
		name: PF + 'panelUsuario',
		x: 0,
		y: 1270,
		width: 600,		
		height: 60,
		layout: 'absolute',
		items:
		[	
		 	NS.lblUsuarios,
		 	NS.txtUsuarios
		 ]
	});	 
	
	/********** FIN PANEL USUARIO ***********/ 
	
	/********** PANEL GRUPO DE TESORERIA ***********/ 
	
	NS.colSelGrupoTesoreria = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//textfields para hacer editables campos del grid
	var txtTesoreriaInc = new Ext.form.TextField({});
	var txtTesoreriaExc = new Ext.form.TextField({});
	
	NS.colGrupoTesoreria = new Ext.grid.ColumnModel ([
		NS.colSelGrupoTesoreria,
		{header: 'INCLUIR', width: 150, dataIndex: 'incluir', sortable: true, editor: txtTesoreriaInc},
 	  	{header: 'EXCLUIR', width: 150, dataIndex: 'excluir', sortable: true, editor: txtTesoreriaExc},
		{header: 'idDoctoTes', width: 100, dataIndex: 'idDoctoTes', sortable: true, hidden: true},
 	  	{header: 'clasificacion', width: 100, dataIndex: 'clasificacion', sortable: true, hidden:true}
		]); 

    NS.gridGrupoTesoreria = new Ext.grid.EditorGridPanel ({
    	store: NS.storeGrupoTesoreria,
    	id: PF + 'gridGrupoTesoreria',
        name: PF + 'gridGrupoTesoreria',
    	cm: NS.colGrupoTesoreria,
    	sm: NS.colSelGrupoTesoreria,
    	width: 332,
    	height:100,
    	x: 100,		 
    	y: 0,
    	stripeRows: true,
    	columnLines: true,
    	listeners: {
    		click: {
    			fn: function(grid) {
    				var seleccion = NS.gridGrupoTesoreria.getSelectionModel().getSelections();
					//alert(seleccion[0].get('clasificacion'));
    			}
    		}
    	}
    });
	    
	NS.PanelGrupoTesoreria = new Ext.form.FieldSet ({
		title: 'Grupo de Tesoreria',
		id: PF + 'panelGrupoTesoreria',
		name: PF + 'panelGrupoTesoreria',
		x: 0,
		y: 1335,
		width: 600,		
		height: 165,
		layout: 'absolute',
		items:
		[	
		 NS.gridGrupoTesoreria,
		 {
		 		xtype: 'button',
		 		text: 'Nuevo',
		 		x: 270,
		 		y: 105,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					var datosClase = NS.storeGrupoTesoreria.recordType;
		 					var totalReg = NS.storeGrupoTesoreria.data.items;
		 					
		 					if(totalReg.length > 0){
		 						if(totalReg[totalReg.length -1 ].get('incluir') != ''
									|| totalReg[totalReg.length -1 ].get('excluir') != '' ){
			 					
			 						var datos = new datosClase({
									 	idDoctoTes: '',
			 							incluir: '',
									 	excluir: '',
									 	clasificacion: 'T'
					            	});
									
									NS.storeGrupoTesoreria.insert(totalReg.length, datos);
									NS.gridGrupoTesoreria.getView().refresh();
			 					}
		 					}else{
		 						var datos = new datosClase({
		 							idDoctoTes: '',
		 							incluir: '',
								 	excluir: '',
								 	clasificacion: 'T'
				            	});
								
		 						NS.storeGrupoTesoreria.insert(totalReg.length, datos);
								NS.gridGrupoTesoreria.getView().refresh();
		 					}
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 350,
		 		y: 105,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					var selInc = NS.gridGrupoTesoreria.getSelectionModel().getSelections();
		 					
		 					var matriz = new Array();
		 					var count = 0;
		 					
		 					for(var i=0; i<selInc.length; i++){
		 						//Agregado para eliminaciones
		 						var registro ={};
		 						if(selInc[i].get('idDoctoTes') != '' && selInc[i].get('idDoctoTes') > 0){
		 							registro.idDoctoTes = selInc[i].get('idDoctoTes');
		 							matriz[count] = registro;
		 							count++;
		 						}
		 						
		 						NS.gridGrupoTesoreria.store.remove(selInc[i]);
		 					}
		 					
		 					var jsonTemp = Ext.util.JSON.encode(matriz);
		 					if(NS.jsonEliminarTes.length > 0){
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 0){
		 							NS.jsonEliminarTes = NS.jsonEliminarTes.substring(0, NS.jsonEliminarTes.length -1) +"," + jsonTemp + "]";
		 						}
		 					}else{
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 2){
		 							NS.jsonEliminarTes = "[" + jsonTemp + "]";
		 						}
		 					}
		 					
		 				}
		 			}
		 		}
		 	}
		 	]
	});	 
	
	/********** FIN PANEL GRUPO DE TESORERIA ***********/
	
	/********** PANEL INDICADOR ***********/
	
	NS.lblIndicador = new Ext.form.Label({
		text: 'Indicador',
		x: 0, 
		y: 0,
	});
	
	NS.txtIndicador = new Ext.form.TextField({
		id: PF + 'txtIndicador',
		name: PF + 'txtIndicador',
		x: 60,
		y: 0,
		width: 150,
		maxLength: 1,
    	maxLengthText: "El tamaño máximo de este campo es 1",
    	tabIndex: 4,
    	enableKeyEvents: true,
    	listeners:{
	 		keyup:{
	 			fn:function(caja, e) {
	 				
	 				var temp = caja.getValue();
	 			
	 				if(e.keyCode == 102 || e.keyCode == 70){
	 					if(temp.length > 1){
		 					temp = temp.substring(0, caja.getValue().length - 1);
		 				}
	 				}else{
	 					if(temp.length > 1){
		 					temp = temp.substring(0, caja.getValue().length - 1);
		 				}else if(temp == 'f' || temp == 'F'){
		 					caja.setValue(temp);
		 				}else{
		 					temp = temp.substring(0, caja.getValue().length - 1);
		 				}
	 				}
	 				
	 				caja.setValue(temp);
	 				
	 			}
	 		}
	 		
    	}    		
	});
	
	NS.PanelIndicador = new Ext.form.FieldSet ({
		title: 'Indicador',
		id: PF + 'panelIndicador',
		name: PF + 'panelIndicador',
		x: 0,
		y: 1505,
		width: 600,		
		height: 60,
		layout: 'absolute',
		items:
		[	
		 NS.lblIndicador,
		 NS.txtIndicador
		 	]
	});	
	
	/********** FIN PANEL INDICADOR ***********/
	
	/********** PANEL CONDICIONES DE PAGO ***********/
	
	/* <<<<< SUBPANEL DIA DE CONTAB VENCIMIENTO >>>>>>> */
	
	NS.lblLunes = new Ext.form.Label({ text: 'LUNES', x: 0,  y: 25 });
	NS.lblMartes = new Ext.form.Label({ text: 'MARTES', x: 0,  y: 55 });
	NS.lblMiercoles = new Ext.form.Label({ text: 'MIERCOLES', x: 0,  y: 85 });
	NS.lblJueves = new Ext.form.Label({ text: 'JUEVES', x: 0,  y: 115 });
	NS.lblViernes = new Ext.form.Label({ text: 'VIERNES', x: 0,  y: 145 });
	NS.lblSabado = new Ext.form.Label({ text: 'SABADO', x: 0,  y: 175 });
	NS.lblDomingo = new Ext.form.Label({ text: 'DOMINGO', x: 0,  y: 205 });
	
	NS.lblDiasPagos = new Ext.form.Label({ text: 'Dias de Pago', x: 100,  y: 0 });
	NS.lblSemanas = new Ext.form.Label({ text: 'Semana', x: 230,  y: 0 });
	
	//Para llenar combos de días
	NS.datosComboDiasSemana = [
		['LU', 'LUNES'],
		['MA', 'MARTES'],
		['MI', 'MIERCOLES'],
		['JU', 'JUEVES'],
		['VI', 'VIERNES'],
		['SA', 'SÁBADO'],
		['DO', 'DOMINGO']
	];	       		
	 
	NS.storeDiasSemana = new Ext.data.SimpleStore({
   		idProperty: 'idDiaSemana',
   		fields: [
   					{name: 'idDiaSemana'},
   					{name: 'diaSemana'}
   				]
   	});
   	NS.storeDiasSemana.loadData(NS.datosComboDiasSemana);
   	
	NS.cmbDiasLunes = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiasLunes',
		name: PF + 'cmbDiasLunes',
		x: 95, 
		y: 17,
		width: 80,
		typeAhead: true,
		emptyText: 'Lunes',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		value: '',
		tabIndex: 5
	});
	
	
	NS.cmbDiasMartes = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiasMartes',
		name: PF + 'cmbDiasMartes',
		x: 95, 
		y: 46,
		width: 80,
		typeAhead: true,
		emptyText: 'Martes',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		value: '',
		tabIndex: 6
		
	});
	
	NS.cmbDiasMiercoles = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiasMiercoles',
		name: PF + 'cmbDiasMiercoles',
		x: 95, 
		y: 78,
		width: 80,
		typeAhead: true,
		emptyText: 'Miercoles',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 7,
		value: ''
		
	});
	
	NS.cmbDiasJueves = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiasJueves',
		name: PF + 'cmbDiasJueves',
		x: 95, 
		y: 109,
		width: 80,
		typeAhead: true,
		emptyText: 'Jueves',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 8,
		value: ''
	});

	NS.cmbDiasViernes = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiasViernes',
		name: PF + 'cmbDiasViernes',
		x: 95, 
		y: 139,
		width: 80,
		typeAhead: true,
		emptyText: 'Viernes',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 9,
		value: ''
	});
	
	NS.cmbDiasSabado = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiasSabado',
		name: PF + 'cmbDiasSabado',
		x: 95, 
		y: 170,
		width: 80,
		typeAhead: true,
		emptyText: 'Sábado',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 10,
		value: ''
	});
	
	NS.cmbDiasDomingo = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiasDomingo',
		name: PF + 'cmbDiasDomingo',
		x: 95, 
		y: 199,
		width: 80,
		typeAhead: true,
		emptyText: 'Domingo',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 11,
		value: ''
	});

	
	//Para llenar combos de días
	NS.datosComboSemana = [
		['A', 'SEMANA ACTUAL'],
		['P', 'PRÓXIMA SEMANA']
	];	       		
	 
	NS.storeSemana = new Ext.data.SimpleStore({
   		idProperty: 'idSemana',
   		fields: [
   					{name: 'idSemana'},
   					{name: 'semana'}
   				]
   	});
   	NS.storeSemana.loadData(NS.datosComboSemana);
   	
	NS.cmbSemanaProximaLunes = new Ext.form.ComboBox ({
		store: NS.storeSemana,
		id: PF + 'cmbSemanaProximaLunes',
		name: PF + 'cmbSemanaProximaLunes',
		x: 220, 
		y: 16,
		width: 115,
		typeAhead: true,
		emptyText: 'Próxima Semana',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSemana',
		displayField: 'semana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 12,
		value: ''
	});
		
	NS.cmbSemanaProximaMartes = new Ext.form.ComboBox ({
		store: NS.storeSemana,
		id: PF + 'cmbSemanaProximaMartes',
		name: PF + 'cmbSemanaProximaMartes',
		x: 220, 
		y: 45,
		width: 115,
		typeAhead: true,
		emptyText: 'Próxima Semana',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSemana',
		displayField: 'semana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 13,
		value: ''
		
	});
		
	NS.cmbSemanaProximaMiercoles = new Ext.form.ComboBox ({
		store: NS.storeSemana,
		id: PF + 'cmbSemanaProximaMiercoles',
		name: PF + 'cmbSemanaProximaMiercoles',
		x: 220, 
		y: 78,
		width: 115,
		typeAhead: true,
		emptyText: 'Próxima Semana',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSemana',
		displayField: 'semana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 14,
		value: ''
		
	});
		
	NS.cmbSemanaProximaJueves = new Ext.form.ComboBox ({
		store: NS.storeSemana,
		id: PF + 'cmbSemanaProximaJueves',
		name: PF + 'cmbSemanaProximaJueves',
		x: 220, 
		y: 110,
		width: 115,
		typeAhead: true,
		emptyText: 'Próxima Semana',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSemana',
		displayField: 'semana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 15,
		value: ''
		
	});
	
	NS.cmbSemanaProximaViernes = new Ext.form.ComboBox ({
		store: NS.storeSemana,
		id: PF + 'cmbSemanaProximaViernes',
		name: PF + 'cmbSemanaProximaViernes',
		x: 220, 
		y: 139,
		width: 115,
		typeAhead: true,
		emptyText: 'Próxima Semana',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSemana',
		displayField: 'semana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 16,
		value: ''
		
	});
	
	NS.cmbSemanaProximaSabado = new Ext.form.ComboBox ({
		store: NS.storeSemana,
		id: PF + 'cmbSemanaProximaSabado',
		name: PF + 'cmbSemanaProximaSabado',
		x: 220, 
		y: 170,
		width: 115,
		typeAhead: true,
		emptyText: 'Próxima Semana',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSemana',
		displayField: 'semana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 17,
		value: ''
		
	});
	
	NS.cmbSemanaProximaDomingo = new Ext.form.ComboBox ({
		store: NS.storeSemana,
		id: PF + 'cmbSemanaProximaDomingo',
		name: PF + 'cmbSemanaProximaDomingo',
		x: 220, 
		y: 199,
		width: 115,
		typeAhead: true,
		emptyText: 'Próxima Semana',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSemana',
		displayField: 'semana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 18,
		value: ''
		
	});
	
	NS.PanelDiasContabi = new Ext.form.FieldSet ({
		title: 'Día de Contab Vencimiento',
		id: PF + 'panelDiasContabi',
		name: PF + 'panelDiasContabi',
		x: 67,
		y: 20,
		width: 380,		
		height: 270,
		layout: 'absolute',
		items:
		[
			NS.lblLunes,
			NS.lblMartes,
			NS.lblMiercoles,
			NS.lblJueves,
			NS.lblViernes,
			NS.lblSabado,
			NS.lblDomingo,
			NS.lblDiasPagos,
			NS.lblSemanas,
			NS.cmbDiasLunes,
			NS.cmbDiasMartes,
			NS.cmbDiasMiercoles,
			NS.cmbDiasJueves,
			NS.cmbDiasViernes,
			NS.cmbDiasSabado,
			NS.cmbDiasDomingo,
			NS.cmbSemanaProximaLunes,
			NS.cmbSemanaProximaMartes,
			NS.cmbSemanaProximaMiercoles,
			NS.cmbSemanaProximaJueves,
			NS.cmbSemanaProximaViernes,
			NS.cmbSemanaProximaSabado,
			NS.cmbSemanaProximaDomingo
		 ]
	});	
	
	/* <<<<< FIN SUBPANEL DIA DE CONTAB VENCIMIENTO >>>>>>> */
	
	/* <<<<< SUBPANEL DIA ESPECIFICO >>>>>>> */
	
	NS.lblDiaEspecifico = new Ext.form.Label({ text: 'Día Especifico (1)', x: 0,  y: 4 });
	NS.lblDiaExcepcion = new Ext.form.Label({ text: 'Día Excepción', x: 225,  y: 0 });
	NS.lblDiaPago = new Ext.form.Label({ text: 'Día Pago por Excepción', x: 335,  y: 0 });
	NS.lblRangoDias = new Ext.form.Label({ text: 'Rango de Días (1)', x: 0, y: 31 });
	NS.lblDiaPago2 = new Ext.form.Label({ text: 'Día Especifico (2)', x: 0,  y: 55 });
	NS.lblRangoDia = new Ext.form.Label({ text: 'Rango de Días (2)', x: 0,  y: 79 });
	NS.lblDiaExcepcion2 = new Ext.form.Label({ text: 'Día Exepción', x: 225,  y: 68 });
	NS.lblDiaPagoExcep = new Ext.form.Label({ text: 'Día Pago por Excepción', x: 336,  y: 68 });
	
	NS.txtIdDiaEspecifico = new Ext.form.NumberField({
		id: PF + 'txtIdDiaEspecifico',
		name: PF + 'txtIdDiaEspecifico',
		enableKeyEvents: true,
		x: 92,
		y: 0,
		width: 100,
		tabIndex: 20/*
		listeners:{
			keydown:{
	 			fn:function(caja, e) {
	 				
	 				var temp = caja.getValue();
	 				
	 				//Para que 
	 				//if(e.keyCode < 48 || e.keyCode > 57){ 
	 				//	caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				//}

	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				var temp = caja.getValue();
	 			
	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				
	 			}
	 		}
		}*/
 		
	});
	
	NS.txtIdRangoDesde = new Ext.form.NumberField({
		id: PF + 'txtIdRangoDesde',
		name: PF + 'txtIdRangoDesde',
		enableKeyEvents: true,
		x: 92,
		y: 27,
		width: 50,
		tabIndex: 21 /*,
		listeners:{
			keydown:{
	 			fn:function(caja, e) {
	 				
	 				var temp = caja.getValue();
	 				
	 				//Para que 
	 				//if(e.keyCode < 48 || e.keyCode > 57){ 
	 				//	caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				//}

	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				var temp = caja.getValue();
	 			
	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				
	 			}
	 		}
		}*/
	});
	
	NS.txtIdRangoHasta = new Ext.form.NumberField({
		id: PF + 'txtIdRangoHasta',
		name: PF + 'txtIdRangoHasta',
		enableKeyEvents: true,
		x: 142,
		y: 27,
		width: 50,
		tabIndex: 22 
		/*,
		//tabIndex: 2,
		listeners:{
			keydown:{
	 			fn:function(caja, e) {
	 				
	 				var temp = caja.getValue();
	 				
	 				//Para que 
	 				//if(e.keyCode < 48 || e.keyCode > 57){ 
	 				//	caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				//}

	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				var temp = caja.getValue();
	 			
	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				
	 			}
	 		}
		}*/
	});
	
	NS.txtIdDiaEspecifico2 = new Ext.form.NumberField({
		id: PF + 'txtIdDiaEspecifico2',
		name: PF + 'txtIdDiaEspecifico2',
		enableKeyEvents: true,
		x: 92,
		y: 53,
		width: 100,
		tabIndex: 25
		/*,
		//tabIndex: 2
		listeners:{
			keydown:{
	 			fn:function(caja, e) {
	 				
	 				var temp = caja.getValue();
	 				
	 				//Para que 
	 				//if(e.keyCode < 48 || e.keyCode > 57){ 
	 				//	caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				//}

	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				var temp = caja.getValue();
	 			
	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				
	 			}
	 		}
		}*/
	});
	
	NS.txtIdRangoDesde2 = new Ext.form.NumberField({
		id: PF + 'txtIdRangoDesde2',
		name: PF + 'txtIdRangoDesde2',
		enableKeyEvents: true,
		x: 92,
		y: 78,
		width: 50,
		tabIndex: 26
		/*,
		//tabIndex: 2
		listeners:{
			keydown:{
	 			fn:function(caja, e) {
	 				
	 				var temp = caja.getValue();
	 				
	 				//Para que 
	 				//if(e.keyCode < 48 || e.keyCode > 57){ 
	 				//	caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				//}

	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				var temp = caja.getValue();
	 			
	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				
	 			}
	 		}
		}*/
	});
	
	NS.txtIdRangoHasta2 = new Ext.form.NumberField({
		id: PF + 'txtIdRangoHasta2',
		name: PF + 'txtIdRangoHasta2',
		enableKeyEvents: true,
		x: 142,
		y: 78,
		width: 50, 
		tabIndex: 27
		/*,
		//tabIndex: 2,
		listeners:{
			keydown:{
	 			fn:function(caja, e) {
	 				
	 				var temp = caja.getValue();
	 				
	 				//Para que 
	 				//if(e.keyCode < 48 || e.keyCode > 57){ 
	 				//	caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				//}

	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				var temp = caja.getValue();
	 			
	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				
	 			}
	 		}
		}*/
	});
	
	
	NS.cmbDiasExcepcion = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiasExcepcion',
		name: PF + 'cmbDiasExcepcion',
		x: 222, 
		y: 15,
		width: 99,
		typeAhead: true,
		emptyText: 'Lunes',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 23,
		value: ''
	});
	
	NS.cmbDiaPago = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiaPago',
		name: PF + 'cmbDiaPago',
		x: 333, 
		y: 15,
		width: 120,
		typeAhead: true,
		emptyText: 'Lunes',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 24,
		value: '',
		listeners: {
			change: {
				fn: function(combo) {
					//combo.getValue() -> retorna la clave del dia.
				}
			}
		}
		
	});	
	
	NS.cmbDiaExepc = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiaExepc',
		name: PF + 'cmbDiaExepc',
		x: 222, 
		y: 90,
		width: 100,
		typeAhead: true,
		emptyText: 'Lunes',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex: 28,
		value: ''
	});
	
	NS.cmbDiaPago2 = new Ext.form.ComboBox ({
		store: NS.storeDiasSemana,
		id: PF + 'cmbDiaPago2',
		name: PF + 'cmbDiaPago2',
		x: 334, 
		y: 90,
		width: 120,
		typeAhead: true,
		emptyText: 'Lunes',
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDiaSemana',
		displayField: 'diaSemana',
		autocomplete: true,
		triggerAction: 'all',
		tabIndex:29,
		value: ''
	});
	
	NS.PanelDiaEspecifico = new Ext.form.FieldSet ({
		title: '',
		id: PF + 'panelDiaEspecifico',
		name: PF + 'panelDiaEspecifico',
		x: 67,
		y: 344,
		width: 500,		
		height: 140,
		layout: 'absolute',
		items:
		[	
		 NS.lblDiaEspecifico,
		 NS.txtIdDiaEspecifico,
		 NS.lblDiaExcepcion,
		 NS.lblDiaPago,
		 NS.cmbDiasExcepcion,
		 NS.txtIdRangoDesde,
		 NS.cmbDiaPago,
		 NS.lblRangoDias,
		 NS.txtIdRangoHasta,
		 NS.txtIdDiaEspecifico2,
		 NS.lblDiaPago2,
		 NS.lblRangoDia, //label para rango 2
		 NS.lblDiaExcepcion2, 
		 NS.lblDiaPagoExcep,
		 NS.txtIdRangoDesde2,
		 NS.txtIdRangoHasta2,
		 NS.cmbDiaExepc,
		 NS.cmbDiaPago2
		 ]
	});	
	
	/* <<<<< FIN SUBPANEL DIA ESPECIFICO >>>>>>> */
	
	/* <<<<< SUBPANEL PLAN DE PAGOS >>>>>>> */
	
	NS.colSelPlanPagos = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//textfields para hacer editables campos del grid
	var txtFechaPlanPagos= new Ext.form.DateField({});	
	
	NS.colPlanPagos = new Ext.grid.ColumnModel ([
 	  	NS.colSelPlanPagos,
 	  	{header: 'Fecha', width: 130, dataIndex: 'fecha', sortable: true, editor: txtFechaPlanPagos, xtype: 'datecolumn', format:'d/m/Y'},
 	  	{header: 'idPlan', width: 10, dataIndex: 'idPlan', sortable: true, hidden:true}
 	]); 
	
	NS.gridPlanPagos = new Ext.grid.EditorGridPanel ({
		store: NS.storePlanPagos,
		id: PF + 'gridPlanPagos',
		name: PF + 'gridPlanPagos',
		cm: NS.colPlanPagos,
		sm: NS.colSelPlanPagos,
		width: 170,
		height:110,
		x: 0,		 
		y: 0,
		stripeRows: true,
		columnLines: true,
		viewConfig: {emptyText: 'Fechas de pagos'},
		listeners: {
			click: {
				fn: function(grid) {
					
				}
			}
		}
	});
	
	NS.PanelPlanPagos = new Ext.form.FieldSet ({
		title: 'Plan de Pagos',
		id: PF + 'panelPlanPagos',
		name: PF + 'panelPlanPagos',
		x: 67,
		y: 494,
		width: 290,		
		height: 130,
		layout: 'absolute',
		items:
		[
		 	NS.gridPlanPagos,
		 	 {
		 		xtype: 'button',
		 		text: 'Nuevo',
		 		x: 190,
		 		y: 30,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					var datosClase = NS.storePlanPagos.recordType;
		 					var totalReg = NS.storePlanPagos.data.items;
		 					
		 					if(totalReg.length > 0){
		 						if(totalReg[totalReg.length -1 ].get('fecha') != ''){
			 					
			 						var datos = new datosClase({
									 	idPlan: '',
			 							fecha: ''
					            	});
									
									NS.storePlanPagos.insert(totalReg.length, datos);
									NS.gridPlanPagos.getView().refresh();
			 					}
		 					}else{
		 						var datos = new datosClase({
		 							idPlan: '',
		 							fecha: ''
				            	});
								
								NS.storePlanPagos.insert(totalReg.length, datos);
								NS.gridPlanPagos.getView().refresh();
		 					}
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 190,
		 		y: 60,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					var sel = NS.gridPlanPagos.getSelectionModel().getSelections();
		 					
		 					var matriz = new Array();
		 					var count = 0;
		 					
		 					for(var i=0; i<sel.length; i++){
		 						//Agregado para eliminaciones
		 						var registro ={};
		 						if(sel[i].get('idPlan') != '' && sel[i].get('idPlan') > 0){
		 							registro.idPlan = sel[i].get('idPlan');
		 							matriz[count] = registro;
		 							count++;
		 						}
		 						
		 						NS.gridPlanPagos.store.remove(sel[i]);
		 					}
		 					
		 					var jsonTemp = Ext.util.JSON.encode(matriz);
		 					if(NS.jsonEliminarFechas.length > 0){
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 0){
		 							NS.jsonEliminarFechas = NS.jsonEliminarFechas.substring(0, NS.jsonEliminarFechas.length -1) +"," + jsonTemp + "]";
		 						}
		 						
		 					}else{
		 						jsonTemp = jsonTemp.substring(1,jsonTemp.length-1);
		 						if(jsonTemp.length > 2){
		 							NS.jsonEliminarFechas = "[" + jsonTemp + "]";
		 						}
		 					}
		 					
		 				}
		 			}
		 		}
		 	}
		 ]
	});
	
	/* <<<<< FIN SUBPANEL PLAN DE PAGOS >>>>>>> */
	
	NS.lblFechaBase = new Ext.form.Label({ text: 'Fecha Base', x: 70,  y: 4 });
	NS.lblDiasAdi = new Ext.form.Label({ text: 'Días Adicionales', x: 70, y: 302 });
	//NS.lblDiasAdi2 = new Ext.form.Label({ text: 'Hora Limite', x: 70,  y: 327 });
	NS.lblDiasAdi3 = new Ext.form.Label({ text: 'Fin Mes', x: 70,  y: 327 });
	NS.lblClaseDia = new Ext.form.Label({ text: 'Clase de Días', x: 477,  y: 40 });
	NS.lblAccion = new Ext.form.Label({ text: 'Acción Fin de Semana/Destiempo ', x: 477,  y: 130 });
	
	NS.txtIdDiasAdicionales = new Ext.form.NumberField({
		id: PF + 'txtIdDiasAdicionales',
		name: PF + 'txtIdDiasAdicionales',
		enableKeyEvents: true,
		x: 155,
		y: 300,
		width: 150,
		tabIndex: 19 /*,
		//tabIndex: 2,
		listeners:{
			keydown:{
	 			fn:function(caja, e) {
	 				
	 				var temp = caja.getValue();
	 				
	 				//Para que 
	 				//if(e.keyCode < 48 || e.keyCode > 57){ 
	 				//	caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				//}

	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				var temp = caja.getValue();
	 			
	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				
	 			}
	 		}
		}*/
	
	});	
	
	/*NS.txtIdHoraLimite = new Ext.form.TextField({
		id: PF + 'txtIdHoraLimite',
		name: PF + 'txtIdHoraLimite',
		x: 155,
		y: 325,
		width: 150,
		tabIndex: 2,
		
	});
	*/
	
	NS.optClaseDia = new Ext.form.RadioGroup({
		id: PF + 'optClaseDia',
		name: PF + 'optClaseDia',
		x: 480,
		y: 65,
		columns: 1, 
		items: [
	          {boxLabel: 'Naturales', id: PF + 'optNaturales', name: PF + 'optClaseDia', inputValue: 'N' },// checked: true},  
	          {boxLabel: 'Hábiles', id: PF + 'optHabiles', name: PF + 'optClaseDia', inputValue: 'H'}
	     ]
	});
	
	NS.optDiaAntPost = new Ext.form.RadioGroup({
		id: PF + 'optDiaAntPost',
		name: PF + 'optDiaAntPost',
		x: 480,
		y: 165,
		columns: 1, 
		items: [
	          {boxLabel: 'Día Anterior', id: PF + 'optDiaAnt', name: PF + 'optDiaAntPost', inputValue: 'A' },// checked: true},  
	          {boxLabel: 'Día Posterior', id: PF + 'optDiaPost', name: PF + 'optDiaAntPost', inputValue: 'P'}
	     ]
	});
	
	NS.optFechaBase = new Ext.form.RadioGroup({
		id: PF + 'optFechaBase',
		name: PF + 'optFechaBase',
		x: 165,
		y: 0,
		columns: 3, 
		items: [
	          {boxLabel: 'Fecha Contabilización', id: PF + 'optFechaC', name: PF + 'optFechaBase', inputValue: 'C' },// checked: true},  
	          {boxLabel: 'Fecha Vencimiento', id: PF + 'optFechaV', name: PF + 'optFechaBase', inputValue: 'V'},
	          {boxLabel: 'Fecha Documento', id: PF + 'optFechaD', name: PF + 'optFechaBase', inputValue: 'D'}
	     ]
	});
	
	NS.PanelCondicionesPago = new Ext.form.FieldSet ({
		title: 'Condiciones de Pago',
		id: PF + 'panelCondicionesPago',
		name: PF + 'panelCondicionesPago',
		x: 0,
		y: 1575,
		width: 600,		
		height: 700,
		layout: 'absolute',
		items:
		[
		 NS.lblFechaBase,
		 NS.optFechaBase,		 
		 NS.PanelDiasContabi,
		 NS.lblDiasAdi,
		 //NS.lblDiasAdi2, //hora limite
		 NS.lblDiasAdi3,
		 NS.txtIdDiasAdicionales, 
		 //NS.txtIdHoraLimite, //hora limite
		 NS.PanelDiaEspecifico,
		 NS.PanelPlanPagos,
		 
		 NS.optDiaAntPost,
		 NS.lblAccion,
		 NS.lblClaseDia,
		 NS.optClaseDia,
		 {
	        	xtype: 'checkbox',
	        	id: PF + 'chkAplica',  //dia de contab vencimiento
	        	name: PF + 'chkAplica',
	        	x: 0,
	        	y: 26,
	        	boxLabel: 'Aplica',
	        	listeners:{
	        		check:{
	        			fn: function(chk , checked){
	        				
	        				if(checked == false){
	        					if(!vacioContabVencimiento()){
	        						Ext.Msg.confirm('SET', 'Se borrarán los datos de ésta área, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							NS.eliminaDiasContabVenc = true;
				 						}else{
				 							chk.setValue(true);
				 							NS.eliminaDiasContabVenc = false;
				 						}
				 					});
	        					}
	        				}else{
	        					NS.eliminaDiasContabVenc = false;
	        				}
	        					
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'checkbox',
	        	id: PF + 'chkAplica2', //dias adicionales
	        	name: PF + 'chkAplica2',
	        	x: 0,
	        	y: 298,
	        	//tabIndex: 20,
	        	boxLabel: 'Aplica',
	        	listeners:{
	        		check:{
	        			fn: function(chk , checked){
	        				
	        				if(checked == false){
	        					if(NS.txtIdDiasAdicionales.getValue() != ""){
	        						Ext.Msg.confirm('SET', 'Se borrarán los datos de ésta área, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							NS.eliminaDiasAdicionales = true;
				 						}else{
				 							chk.setValue(true);
				 							NS.eliminaDiasAdicionales = false;
				 						}
				 					});
	        					}
	        				}else{
	        					NS.eliminaDiasAdicionales = false;
	        				}
	        				
	        			}
	        		}
	        	}
	        	
	        },
	        {
	        	xtype: 'checkbox',
	        	id: PF + 'chkAplica3', //fin mes
	        	name: PF + 'chkAplica3',
	        	x: 0,
	        	y: 323,
	        	//tabIndex: 20,
	        	boxLabel: 'Aplica',
	        	listeners:{
	        		check:{
	        			fn: function(chk , checked){
	        				//Fin mes no se pregunta, modifica al instante 
	        			}
	        		}
	        	}
	        	
	        },
	        {
	        	xtype: 'checkbox',
	        	id: PF + 'chkAplica4', //dias especificos
	        	name: PF + 'chkAplica4',
	        	x: 0,
	        	y: 374,
	        	//tabIndex: 20,
	        	boxLabel: 'Aplica',
	        	listeners:{
	        		check:{
	        			fn: function(chk , checked){
	        				
	        				if(checked == false){
	        					if(!vacioDiasEspecificos()){
	        						Ext.Msg.confirm('SET', 'Se borrarán los datos de ésta área, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							NS.eliminaDiasespecificos = true;
				 						}else{
				 							chk.setValue(true);
				 							NS.eliminaDiasespecificos = false;
				 						}
				 					});
	        					}
	        				}else{
	        					NS.eliminaDiasespecificos = false;
	        				}
	        				
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'checkbox',
	        	id: PF + 'chkAplica5', //plan de pagos
	        	name: PF + 'chkAplica5',
	        	x: 0,
	        	y: 500,
	        	//tabIndex: 20,
	        	boxLabel: 'Aplica',
        		listeners:{
	        		check:{
	        			fn: function(chk , checked){
	        				
	        				if(checked == false){
	        					if(NS.storePlanPagos.data.items.length > 0){
	        						Ext.Msg.confirm('SET', 'Se borrarán los datos de ésta área, ¿Desea continuar?', function(btn){
				 						if (btn === 'yes'){		
				 							NS.eliminaPlanPagos = true;
				 						}else{
				 							chk.setValue(true);
				 							NS.eliminaPlanPagos = false;
				 						}
				 					});
	        					}
	        				}else{
	        					NS.eliminaPlanPagos = false;
	        				}
	        				
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'label',
	        	text: 'Banco Interlocutor',
	        	x: 67,
	        	y: 635
	        },
	        {
	        	xtype: 'textfield',
	        	id: PF + 'txtBancoInter', 
	        	name: PF + 'txtBancoInter',
	        	x: 170,
	        	y: 635,
	        	width: 100,
	        	maxLength: 4,
	        	tabIndex: 30,
	        	maxLengthText: "El tamaño máximo de este campo es 4",
	        	enableKeyEvents: true,
	        	listeners:{
	    	 		keyup:{
	    	 			fn:function(caja, e) {
	    	 				
	    	 				var temp = caja.getValue();
	    	 			
	    	 				if(temp.length > 4){
    		 					temp = temp.substring(0, caja.getValue().length - 1);
    		 				}
	    	 				
	    	 				caja.setValue(temp);
	    	 				
	    	 			}
	    	 		}
	        	}   //fin listener 		
	        
	        },
	        
	        
		 ]
	});	
	
	/********** FIN PANEL CONDICIONES DE PAGO ***********/
	
	/************ PANEL RUBROS **********/
	
	//Grid Asignacion
	NS.selRubrosSinAsignar = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	NS.colRubrosSinAsignar = new Ext.grid.ColumnModel ([
 	  	NS.selRubrosSinAsignar,
 	  	{header: 'No. Rubro', width: 40, dataIndex: 'idRubro', sortable: true},
 	  	{header: 'Sin Asignar', width: 100, dataIndex: 'descRubro', sortable: true}
 	]); 
	
	NS.gridRubrosSinAsignar = new Ext.grid.GridPanel ({
		store: NS.storeRubrosSinAsignar, //cambiar por storeRubrosSinAsignar
		id: PF + 'gridRubrosSinAsignar', 
		name: PF + 'gridRubrosSinAsignar',
		cm: NS.colRubrosSinAsignar,
		sm: NS.selRubrosSinAsignar,
		width: 232,
		height:200,
		x: 0,
		y: 0,
		//stripeRows: true,
		//columnLines: true,
		enableDragDrop   : true,
		ddGroup: PF+'secondGridDDGroupRubros',
		listeners: {
			click: {
				fn: function(grid) {
					
				}
			}
		}
	});
	
//Grid Asignacion
	
	NS.selRubrosAsignados = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//Este text se crea para hacer editable el grid en el campo Hora límite operación.
	NS.colRubrosAsignados = new Ext.grid.ColumnModel ([
 	  	NS.selRubrosAsignados,
 	  	{header: 'No. Rubro', width: 40, dataIndex: 'idRubro', sortable: true},
 	  	{header: 'Asignados', width: 100, dataIndex: 'descRubro', sortable: true}
 	]); 
	
	
	NS.gridRubrosAsignados = new Ext.grid.EditorGridPanel ({
		store: NS.storeRubrosAsignados, //storeRubrosAsignados
		id: PF + 'gridRubrosAsignados', 
		name: PF + 'gridRubrosAsignados',
		cm: NS.colRubrosAsignados,
		sm: NS.selRubrosAsignados,
		width: 282,
		height:200,
		x: 300,
		y: 0,
		stripeRows: true,
		columnLines: true,
		enableDragDrop   : true,
		ddGroup: 'firstGridDDGroupRubros',
		listeners: {
			click: {
				fn: function(grid) {
					
				}
			}
		}
	});
	
	NS.panelRubros = new Ext.form.FieldSet ({
		title: 'Rubros',
		id: PF + 'panelRubros',
		name: PF + 'panelRubros',
		x: 0,
		y: 2285,
		width: 600,
		height: 235,
		layout: 'absolute',		
		//disabled: true,
		items: [
		 	NS.gridRubrosSinAsignar,
		 	NS.gridRubrosAsignados,
		 	{
		 		xtype: 'button',
		 		id: PF + 'btnAsignarRubro',
		 		name: PF + 'btnAsignarRubro',
		 		text: '>',
		 		x: 250,
		 		y: 30,
		 		width: 30,
		 		listeners:{
		 			click:{
		 				fn:function(e) {	
		 					
		 					var records = NS.gridRubrosSinAsignar.getSelectionModel().getSelections();
		 					var datosClase = NS.gridRubrosSinAsignar.getStore().recordType;

		 					for(var i = 0; i < records.length; i++) {
		 						idRubro = records[i].get('idRubro');
		 						descRubro = records[i].get('descRubro');

								NS.gridRubrosSinAsignar.store.remove(records[i]);

								var datos = new datosClase({
									idRubro: idRubro,
									descRubro: descRubro,
								});
								
								//NS.gridEmpresasSinAsignar.stopEditing();
								//NS.gridEmpresasAsignadas.insert(i, datos);
								NS.gridRubrosAsignados.store.add(datos);
							}
		 						
		 					NS.gridRubrosAsignados.getView().refresh();
							NS.gridRubrosSinAsignar.getView().refresh();
		 					NS.gridRubrosAsignados.store.sort('descRubro', 'ASC');
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: '<',
		 		id: PF + 'btnDesasignarRubro',
		 		name: PF + 'btnDesasignarRubro',
		 		x: 250,
		 		y: 65,
		 		width: 30,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					var records = NS.gridRubrosAsignados.getSelectionModel().getSelections();
		 					
		 					NS.gridRubrosAsignados.store.remove(records);
		 					NS.gridRubrosAsignados.getView().refresh();
		 					
		                    NS.gridRubrosSinAsignar.store.add(records);
		 					NS.gridRubrosSinAsignar.getView().refresh();
		 					
		 					NS.gridRubrosSinAsignar.store.sort('descRubro', 'ASC');
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: '>>',
		 		id: PF + 'btnAsignarRubros',
		 		name: PF + 'btnAsignarRubros',
		 		x: 250,
		 		y: 100,
		 		width: 30,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					var records = NS.storeRubrosSinAsignar.data.items;
		 					
		 					NS.gridRubrosSinAsignar.store.removeAll();
			 				NS.gridRubrosSinAsignar.getView().refresh();
			 					
		 					NS.gridRubrosAsignados.store.add(records);
		 					NS.gridRubrosAsignados.getView().refresh();
		 					
		 					NS.gridRubrosAsignados.store.sort('descRubro', 'ASC');
		 					 
		 					
		 					/*var datosClase = NS.gridRubrosSinAsignar.getStore().recordType;
		 					
		 					for(var i = 0; i < records.length; i++) {
		 						idRubro = records[i].get('idRubro');
		 						descRubro = records[i].get('descRubro');

								var datos = new datosClase({
									idRubro: idRubro,
									descRubro: descRubro
								});
								
								//NS.gridEmpresasSinAsignar.stopEditing();
								//NS.gridEmpresasAsignadas.insert(i, datos);
								NS.gridRubrosAsignados.store.add(datos);
							}
		 						
		 					NS.gridRubrosSinAsignar.store.removeAll();
		 					NS.gridRubrosSinAsignar.getView().refresh();
		 					NS.gridRubrosAsignados.getView().refresh();
		 					NS.gridRubrosAsignados.store.sort('descRubro', 'ASC');
		 					*/
		 				}
		 			}
		 		}
		 	},
			{
		 		xtype: 'button',
		 		text: '<<',
		 		id: PF + 'btnDesasignarRubros',
		 		name: PF + 'btnDesasignarRubros',
		 		x: 250,
		 		y: 135,
		 		width: 30,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					var records = NS.storeRubrosAsignados.data.items
		 					
		 					NS.gridRubrosAsignados.store.removeAll();
		 					NS.gridRubrosAsignados.getView().refresh();
		 					
		                    NS.gridRubrosSinAsignar.store.add(records);
		 					NS.gridRubrosSinAsignar.getView().refresh();
		 					
		 					NS.gridRubrosSinAsignar.store.sort('descRubro', 'ASC');
		 					 
		 				}
		 			}
		 		}
		 	}
		]
	});

	/************ FIN PANEL RUBROS **********/
	
	/********** PANEL LONGITUD CHEQUERA ***********/
	
	NS.lblLongChequera = new Ext.form.Label({
		text: 'Longitud Chequera',
		x: 0, 
		y: 0,
	});
	
	NS.txtLongChequera = new Ext.form.NumberField({
		id: PF + 'txtLongChequera',
		name: PF + 'txtLongChequera',
		x: 100,
		y: 0,
		width: 150,
		maxLength: 2,
    	maxLengthText: "El tamaño máximo de este campo es 2",
    	enableKeyEvents: true,
    	listeners:{
	 		keyup:{
	 			fn:function(caja, e) {
	 				
	 			}
	 		}
	 		
    	}    		
	});
	
	NS.PanelLongitudChequera = new Ext.form.FieldSet ({
		title: 'Longitud Chequera',
		id: PF + 'panelLongChequera',
		name: PF + 'panelLongChequera',
		x: 0,
		y: 2530,
		width: 600,		
		height: 60,
		layout: 'absolute',
		items:
		[	
		 NS.lblLongChequera,
		 NS.txtLongChequera
		]
	});	
	
	/********** FIN PANEL LONGITUD CADENA ***********/
	
	 NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 5,
		width: 633,		
		height: 2650,
		layout: 'absolute',
		items:
		[
		 	NS.panelRelacion,
		 	NS.panelEmpresas,		 	
		 	NS.PanelAcreedores,
		 	NS.PanelDocumento,
		 	NS.PanelClaseDocumento,
		 	NS.PanelUsuario,
		 	NS.PanelGrupoTesoreria,
		 	NS.PanelIndicador,
		 	NS.PanelCondicionesPago,
		 	NS.panelRubros,
		 	NS.PanelLongitudChequera,
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		id: PF + 'btnAceptar',
		 		x: 425, 
		 	    y: 2600,
		 		width: 80,
		 		height: 22,
		 		listeners:{
		 			click:{
		 				fn: function(e){
		 					aceptarModificar();
		 				}		 				
		 			}
		 		}	 		
		 	}, 
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		id: PF + 'btnCancelar',
		 		x: 520,
		 		y: 2600,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					limpiarTodo(true);	
		 				}
		 			}
		 		}		 		
		 	},{
		 		xtype: 'button',
		 		text: 'Simular Regla',
		 		id: PF + 'btnSimulador',
		 		x: 310, 
		 	    y: 2600,
		 		width: 100,
		 		height: 22,
		 		enabled: false,
		 		listeners:{
		 			click:{
		 				fn: function(e){
		 					NS.simulador = true;
		 					mascara.show();
		 					ReglasNegocioAction.loginSimulador(function(result, e){
								mascara.hide();
		 						if(result.error != null && result.error != '' && result.error != undefined) {								
									Ext.Msg.alert('SET',result.error);
									NS.simulador = false;
									return;
								}else{
									
									ReglasNegocioAction.insertarLoginSimulador(function(result, e){
										if(result.error != null && result.error != '' && result.error != undefined) {								
											Ext.Msg.alert('SET',result.error);
											NS.simulador = false;
											return;
										}else{
											aceptarModificar();
										}
										
									});
								}
								
							});
		 					
		 				}		 				
		 			}
		 		}	 		
		 	}		 			 	
	    ]
	});
	
	NS.ReglasNegocios = new Ext.FormPanel ({
		title: 'Mantenimiento de Reglas de Negocios',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'mantenimientoReglasNegocios',
		name: PF + 'mantenimientoReglasNegocios',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
		
	function limpiarTodo(limpiaRelacion){
	
		//VARIABLES GLOBALES
		selGlobalAcreInc = '';
		selGlobalAcreExc = '';
		selGlobalDoctoInc = '';
		selGlobalDoctoExc = '';
		
		NS.jsonEliminarAcre = '';
		NS.jsonEliminarClase = '';
		NS.jsonEliminarTes = '';
		NS.jsonEliminarFechas = '';
		NS.jsonEliminarDoctos = '';
		
		NS.eliminaDiasContabVenc = false;
		NS.eliminaDiasAdicionales = false;
		NS.eliminaDiasespecificos = false;
		NS.eliminaPlanPagos = false;
		
		//ARCHIVOS EXCEL
		NS.PanelCargaExcelEmpresas.getForm().reset();
		NS.PanelCargaExcelAcre.getForm().reset();
		NS.PanelCargaExcelDocto.getForm().reset();
		
		
		//Vuelve a cargar el grid de consulta.
		if(limpiaRelacion){
			NS.gridRelacion.store.removeAll();
			NS.storeRelacion.load();
			NS.gridRelacion.getView().refresh();
		}
		
		//EMPRESAS
		Ext.getCmp(PF + 'chkGeneraPropAut').setValue(true);
		Ext.getCmp(PF + 'chkCPagos').setValue(false);
		Ext.getCmp(PF + 'chkDescuento').setValue(false);
		NS.gridEmpresasAsignadas.store.removeAll();
		NS.gridEmpresasAsignadas.getView().refresh();
		NS.gridEmpresasSinAsignar.store.removeAll();
		NS.gridEmpresasSinAsignar.getView().refresh();
		Ext.getCmp(PF + 'txtNombreRegla').setValue("");
		Ext.getCmp(PF + 'cmbReglas').setValue("");
		Ext.getCmp(PF + 'cmbReglas').setDisabled(false);
		
		//ACREEDORES
		NS.gridDatosAcreedoresExc.store.removeAll();
		NS.gridDatosAcreedoresExc.getView().refresh();
		NS.gridDatosAcreedoresInc.store.removeAll();
		NS.gridDatosAcreedoresInc.getView().refresh();
		
		//DOCUMENTO
		NS.gridDocumentoInc.store.removeAll();
		NS.gridDocumentoInc.getView().refresh();
		NS.gridDocumentoExc.store.removeAll();
		NS.gridDocumentoExc.getView().refresh();
		
		//CLASE DOCTO
		NS.gridClaseDocto.store.removeAll();
		NS.gridClaseDocto.getView().refresh();
		
		//USUARIO
		NS.txtUsuarios.setValue("");
		
		//TESORERIA
		NS.gridGrupoTesoreria.store.removeAll();
		NS.gridGrupoTesoreria.getView().refresh();
		
		//INDICADOR
		NS.txtIndicador.setValue("");
		
		//DIAS CONTABI
		NS.cmbDiasLunes.setValue(""); 
		NS.cmbDiasMartes.setValue(""); 
		NS.cmbDiasMiercoles.setValue("");
		NS.cmbDiasJueves.setValue("");
		NS.cmbDiasViernes.setValue("");
		NS.cmbDiasSabado.setValue("");
		NS.cmbDiasDomingo.setValue("");
		NS.cmbSemanaProximaLunes.setValue("");
		NS.cmbSemanaProximaMartes.setValue("");
		NS.cmbSemanaProximaMiercoles.setValue("");
		NS.cmbSemanaProximaJueves.setValue("");
		NS.cmbSemanaProximaViernes.setValue("");
		NS.cmbSemanaProximaSabado.setValue("");
		NS.cmbSemanaProximaDomingo.setValue("");
		
		//DIAS EXCEP
		 NS.txtIdDiaEspecifico.setValue("");
		 NS.cmbDiasExcepcion.setValue("");
		 NS.cmbDiaPago.setValue("");
		 NS.txtIdRangoDesde.setValue("");
		 NS.txtIdRangoHasta.setValue("");
		 NS.txtIdDiaEspecifico2.setValue("");
		 NS.txtIdRangoDesde2.setValue("");
		 NS.txtIdRangoHasta2.setValue("");
		 NS.cmbDiaExepc.setValue("");
		 NS.cmbDiaPago2.setValue("");
		 
		 //PlAN PAGO
		 NS.gridPlanPagos.store.removeAll();
		 NS.gridPlanPagos.getView().refresh();
		 
		 //CONDICIONES PAGO
		 NS.txtIdDiasAdicionales.setValue("");
		 Ext.getCmp(PF + 'txtBancoInter').setValue("");
		 
		 //RadioGroups
		 Ext.getCmp(PF + 'optNaturales').setValue(false);
		 Ext.getCmp(PF + 'optHabiles').setValue(false);
		 
		 Ext.getCmp(PF + 'optDiaAnt').setValue(false);
		 Ext.getCmp(PF + 'optDiaPost').setValue(false);
			
		 Ext.getCmp(PF + 'chkAplica').setValue(false);
		 Ext.getCmp(PF + 'chkAplica2').setValue(false);
		 Ext.getCmp(PF + 'chkAplica3').setValue(false);
		 Ext.getCmp(PF + 'chkAplica4').setValue(false);
		 Ext.getCmp(PF + 'chkAplica5').setValue(false);
		
		 Ext.getCmp(PF + 'optFechaC').setValue(false);
		 Ext.getCmp(PF + 'optFechaV').setValue(false);
		 Ext.getCmp(PF + 'optFechaD').setValue(false);

		 // BOTONES
		 Ext.getCmp(PF + 'btnModificarArriba').setDisabled(true);
		 Ext.getCmp(PF + 'btnAceptar').setText("Aceptar");
		
		 //RUBROS
		 NS.gridRubrosSinAsignar.store.removeAll();
		 NS.gridRubrosSinAsignar.getView().refresh();
		 NS.gridRubrosAsignados.store.removeAll();
		 NS.gridRubrosAsignados.getView().refresh();
			
		 //Longitud Chequera
		 NS.txtLongChequera.setValue('');
	}
	
	
	NS.cargarGrids = function(regla){
	
		NS.storeEmpresasSinAsignar.baseParams.bExiste = false;
		NS.storeEmpresasSinAsignar.baseParams.idRegla = regla;
		NS.storeEmpresasSinAsignar.load();
		
		NS.storeEmpresasAsignadas.baseParams.bExiste = true;
		NS.storeEmpresasAsignadas.baseParams.idRegla = regla;
		NS.storeEmpresasAsignadas.load();
		
		NS.storeAcreedoresInc.baseParams.bTipoAcre = true;
		NS.storeAcreedoresInc.baseParams.clasificacion = 'A';
		NS.storeAcreedoresInc.baseParams.idRegla = regla;
		NS.storeAcreedoresInc.load();
		
		NS.storeAcreedoresExc.baseParams.bTipoAcre = false;
		NS.storeAcreedoresExc.baseParams.clasificacion = 'A';
		NS.storeAcreedoresExc.baseParams.idRegla = regla;		
		NS.storeAcreedoresExc.load();
		
		NS.storeDocumentoInc.baseParams.bTipoDocto = true;
		NS.storeDocumentoInc.baseParams.clasificacion = 'D';
		NS.storeDocumentoInc.baseParams.idRegla = regla;
		NS.storeDocumentoInc.load();
		
		NS.storeDocumentoExc.baseParams.bTipoDocto = false;
		NS.storeDocumentoExc.baseParams.clasificacion = 'D';
		NS.storeDocumentoExc.baseParams.idRegla = regla;
		NS.storeDocumentoExc.load();
		
		NS.storeClaseDocto.baseParams.clasificacion = 'D';
		NS.storeClaseDocto.baseParams.idRegla = regla;
		NS.storeClaseDocto.load();
		
		NS.storeGrupoTesoreria.baseParams.clasificacion = 'T';
		NS.storeGrupoTesoreria.baseParams.idRegla = regla;
		NS.storeGrupoTesoreria.load();
			
		NS.storeRubrosSinAsignar.baseParams.bAsignados = false;
		NS.storeRubrosSinAsignar.baseParams.idRegla = regla;
		NS.storeRubrosSinAsignar.load();
		
		NS.storeRubrosAsignados.baseParams.bAsignados = true;
		NS.storeRubrosAsignados.baseParams.idRegla = regla;
		NS.storeRubrosAsignados.load();
		
	};  
	
	//Funciones para los eventos del grid asignar empresas
    var firstGridDropTargetEl =  NS.gridEmpresasSinAsignar.getView().scroller.dom;
    var firstGridDropTarget = new Ext.dd.DropTarget(firstGridDropTargetEl, {
            ddGroup    : 'firstGridDDGroup',
            notifyDrop : function(ddSource, e, data){
                    var records =  ddSource.dragData.selections;
                    Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                    NS.gridEmpresasSinAsignar.store.add(records);
                    NS.gridEmpresasSinAsignar.store.sort('noEmpresa', 'ASC');
                    return true
            }
    });

    var secondGridDropTargetEl = NS.gridEmpresasAsignadas.getView().scroller.dom;
    var secondGridDropTarget = new Ext.dd.DropTarget(secondGridDropTargetEl,{
            ddGroup    : PF+'secondGridDDGroup',
            notifyDrop : function(ddSource, e, data){
                    var records =  ddSource.dragData.selections;
                    
                    /*Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                    NS.gridEmpresasAsignadas.store.add(records);*/
                    
 					var datosClase = NS.gridEmpresasSinAsignar.getStore().recordType;
 					/*
 					NS.gridEmpresasSinAsignar.store.remove(records);
 					NS.gridEmpresasSinAsignar.getView().refresh();
 					
                    NS.gridEmpresasAsignadas.store.add(records);
 					NS.gridEmpresasAsignadas.getView().refresh();
 					*/
 					
 					for(var i = 0; i < records.length; i++) {
 						noEmp = records[i].get('noEmpresa');
 						nombreEmp = records[i].get('nombreEmpresa');

						NS.gridEmpresasSinAsignar.store.remove(records[i]);

						var datos = new datosClase({
							noEmpresa: noEmp,
							nombreEmpresa: nombreEmp,
							horaLimiteOperacion: '23:59:00'
						});
						
						NS.gridEmpresasAsignadas.store.add(datos);						
					}
 						
 					NS.gridEmpresasAsignadas.getView().refresh();
					NS.gridEmpresasSinAsignar.getView().refresh();
					
 					NS.gridEmpresasAsignadas.store.sort('nombreEmpresa', 'ASC');
 					
                    NS.gridEmpresasAsignadas.store.sort('noEmpresa', 'ASC');
                    return true
            }
    });
    
  //Funciones para los eventos DD Rubros
    var firstGridDropTargetElRubros =  NS.gridRubrosSinAsignar.getView().scroller.dom;
    var firstGridDropTargetRubros = new Ext.dd.DropTarget(firstGridDropTargetElRubros, {
            ddGroup    : 'firstGridDDGroupRubros',
            notifyDrop : function(ddSource, e, data){
                    var records =  ddSource.dragData.selections;
                    Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                    NS.gridRubrosSinAsignar.store.add(records);
                    NS.gridRubrosSinAsignar.store.sort('descRubro', 'ASC');
                    return true
            }
    });

    var secondGridDropTargetElRubros = NS.gridRubrosAsignados.getView().scroller.dom;
    var secondGridDropTargetRubros = new Ext.dd.DropTarget(secondGridDropTargetElRubros,{
            ddGroup    : PF+'secondGridDDGroupRubros',
            notifyDrop : function(ddSource, e, data){
                    var records =  ddSource.dragData.selections;
                   
 					var datosClase = NS.gridEmpresasSinAsignar.getStore().recordType;
 					
 					for(var i = 0; i < records.length; i++) {
 						idRubro = records[i].get('idRubro');
 						descRubro = records[i].get('descRubro');

						NS.gridRubrosSinAsignar.store.remove(records[i]);

						var datos = new datosClase({
							idRubro: idRubro,
							descRubro: descRubro,
						});
						
						NS.gridRubrosAsignados.store.add(datos);						
					}
 						
 					NS.gridRubrosAsignados.getView().refresh();
					NS.gridRubrosSinAsignar.getView().refresh();
					
 					NS.gridEmpresasAsignadas.store.sort('nombreEmpresa', 'ASC');
                    return true
            }
    });
    
    function cambiarFecha(fecha){
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		var mes;
		
		for(var i=0;i<12;i=i+1){
			if(mesArreglo[i]===mesDate)
			{
			mes = i+1;
				if(mes<10)
					mes='0'+mes;
			}
		}		
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	}
    
    function isNumeric(num){
        return !isNaN(num);
        
        /* EJEMPLOS DE isNaN
            isNaN(123)       // false
			isNaN('123')     // false
			isNaN('1e10000') // false  (number is Infinity)
			isNaN('foo')     // true
			isNaN('10px')    // true
         */
    }
    
    function concatenaDiezDigitos(caja){
    	//Concatenamos a 10 digitos
		if(caja.getValue().length < 10){
			var concat = '';
			for(var i = caja.getValue().length; i < 10; i++){
				concat = concat + '0';
			}
			
			caja.setValue(concat + caja.getValue());
			
			return caja.getValue(); //Por si se quiere capturar en alguna variable
			
		}else if(caja.getValue().length > 10){
			Ext.Msg.alert('SET','El campo DE no debe pasar de 10 dígitos');
			caja.setValue('');
			return;
		}
    }
    
    function vacioContabVencimiento(){
    	
    	if(NS.cmbDiasLunes.getValue() == "" && 
    			NS.cmbDiasMartes.getValue() == "" &&
    			NS.cmbDiasMiercoles.getValue() == "" &&
    			NS.cmbDiasJueves.getValue() == "" &&
    			NS.cmbDiasViernes.getValue() == "" &&
    			NS.cmbDiasSabado.getValue() == "" &&
    			NS.cmbDiasDomingo.getValue() == "" &&
    			NS.cmbSemanaProximaLunes.getValue() == "" &&
    			NS.cmbSemanaProximaMartes.getValue() == "" &&
    			NS.cmbSemanaProximaMiercoles.getValue() == "" &&
    			NS.cmbSemanaProximaJueves.getValue() == "" &&
    			NS.cmbSemanaProximaViernes.getValue() == "" &&
    			NS.cmbSemanaProximaSabado.getValue() == "" &&
    			NS.cmbSemanaProximaDomingo.getValue() == "" ){
    	
    		
    	return true;
    	
    	}else{
    		return false;
    	}
    }
    
    
    function vacioDiasEspecificos(){
    	
    	if(NS.txtIdDiaEspecifico.getValue() == "" &&
    			 NS.cmbDiasExcepcion.getValue() == "" &&
		  		 NS.cmbDiaPago.getValue() == "" &&
		  		 NS.txtIdRangoDesde.getValue() == "" &&
		  		 NS.txtIdRangoHasta.getValue() == "" &&
		  		 NS.txtIdDiaEspecifico2.getValue() == "" &&
		  		 NS.txtIdRangoDesde2.getValue() == "" &&
		  		 NS.txtIdRangoHasta2.getValue() == "" &&
		  		 NS.cmbDiaExepc.getValue() == "" &&
		  		 NS.cmbDiaPago2.getValue() == ""){
    	
    	return true;
    	
    	}else{
    		return false;
    	}
    }
    
    function verificaComponentesCreados(){
		
		/**
		 * AGREGAR UN IF A ESTE METODO LOS COMPONENTES QUE SE CREEN EN VENTANAS MODALES 
		 * radios, combos, botones, etc.
		 */
		
		var compt; 
		
		/*** Ventana winAcre ***/
		compt = Ext.getCmp(PF + 'gridWinAcre');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		/********************************/
	}

    
    function aceptarModificar(){
    	
    	mascara.show();
    	
    	//Recogemos los datos
			var jsonReglaNegocio, jsonEmpresas, jsonAcreDocto, jsonDoctoTes, jsonCondicionPago,
			jsonDiasContabVen, jsonDiaEsp, jsonPlanPagos, jsonRubros;
			
			var seleccion = NS.gridRelacion.getSelectionModel().getSelections();
			var regla = 0;
			if(seleccion.length > 0){
				regla = seleccion[0].get('idRegla');
			}
		
			var matriz = new Array();
			//Regla Negocio General
		var registro = {};
		registro.idRegla = regla; 
		registro.tipoRegla = Ext.getCmp(PF + 'cmbReglas').getValue();
		registro.reglaNegocio = Ext.getCmp(PF + 'txtNombreRegla').getValue();
		registro.fechaCaptura = NS.fecHoy;
		
		if(Ext.getCmp(PF + 'chkGeneraPropAut').getValue() == true){
			registro.generaPropuestaAutomatica = 'S';
		}else{
			registro.generaPropuestaAutomatica = 'N';
		}
		
		registro.usuarios = Ext.getCmp(PF + 'txtUsuarios').getValue();							
		registro.indicador = Ext.getCmp(PF + 'txtIndicador').getValue();
		// longitud chequera - 29/02/2016
		registro.longChequera = Ext.getCmp(PF + 'txtLongChequera').getValue();
		
		//Agregado 21/01/16 - Para descuentos
		if(Ext.getCmp(PF + 'chkCPagos').getValue() == false
			&& Ext.getCmp(PF + 'chkDescuento').getValue() == false){
			
			registro.descuento = 'N';
		}else if(Ext.getCmp(PF + 'chkCPagos').getValue() == true){
			registro.descuento = 'C';
		}else if(Ext.getCmp(PF + 'chkDescuento').getValue() == true){
			registro.descuento = 'D';
		}

		matriz[0] = registro;

		jsonReglaNegocio = Ext.util.JSON.encode(matriz);
		
		//EMPRESAS ASIGNADAS
		var matriz = new Array();
		var records = NS.storeEmpresasAsignadas.data.items;
		
		for(var inc = 0; inc<records.length; inc++) {
			var registro = {};
			registro.noEmpresa=records[inc].get('noEmpresa');
			registro.nombreEmpresa=records[inc].get('nombreEmpresa');
			registro.horaLimiteOperacion=records[inc].get('horaLimiteOperacion');
			
			matriz[inc]=registro;
		}
		
		jsonEmpresas = Ext.util.JSON.encode(matriz);
		
		
		//ACREEDORES Y DOCUMENTOS
		var matriz = new Array();
		var records = NS.storeAcreedoresInc.data.items;
		var cont = 0;
		
		for(var inc = 0; inc<records.length; inc++) {
			var registro = {};
			registro.idAcreDocto=records[inc].get('idAcreDocto');
			registro.deAcre=records[inc].get('deAcre');
			registro.aAcre=records[inc].get('aAcre');
			registro.clasificacion=records[inc].get('clasificacion');
			registro.tipoAcre=records[inc].get('tipoAcre');
			
			matriz[cont]=registro;
			cont++;
		}
		
		var records = NS.storeAcreedoresExc.data.items;
		
		for(var inc = 0; inc<records.length; inc++) {
			var registro = {};
			registro.idAcreDocto=records[inc].get('idAcreDocto');
			registro.deAcre=records[inc].get('deAcre');
			registro.aAcre=records[inc].get('aAcre');
			registro.clasificacion=records[inc].get('clasificacion');
			registro.tipoAcre=records[inc].get('tipoAcre');
			
			matriz[cont]=registro;
			cont++;
		}
		
		var records = NS.storeDocumentoInc.data.items;
		
		for(var inc = 0; inc<records.length; inc++) {
			var registro = {};
			registro.idAcreDocto=records[inc].get('idAcreDocto');
			registro.deAcre=records[inc].get('deAcre');
			registro.aAcre=records[inc].get('aAcre');
			registro.clasificacion=records[inc].get('clasificacion');
			registro.tipoAcre=records[inc].get('tipoAcre');
			
			matriz[cont]=registro;
			cont++;
		}
		
		var records = NS.storeDocumentoExc.data.items;
		
		for(var inc = 0; inc<records.length; inc++) {
			var registro = {};
			registro.idAcreDocto=records[inc].get('idAcreDocto');
			registro.deAcre=records[inc].get('deAcre');
			registro.aAcre=records[inc].get('aAcre');
			registro.clasificacion=records[inc].get('clasificacion');
			registro.tipoAcre=records[inc].get('tipoAcre');
			
			matriz[cont]=registro;
			cont++;
		}
		
		jsonAcreDocto = Ext.util.JSON.encode(matriz);
		
		

		//CLASE DOCUMENTO Y GRUPO TESORERIA
		var matriz = new Array();
		var records = NS.storeClaseDocto.data.items;
		var cont = 0;
		
		for(var inc = 0; inc<records.length; inc++) {
			var registro = {};
			registro.idDoctoTes=records[inc].get('idDoctoTes');
			registro.incluir=records[inc].get('incluir');
			registro.excluir=records[inc].get('excluir');
			registro.clasificacion=records[inc].get('clasificacion');
			
			matriz[cont]=registro;
			cont++;
		}
		
		var records = NS.storeGrupoTesoreria.data.items;
		
		for(var inc = 0; inc<records.length; inc++) {
			var registro = {};
			registro.idDoctoTes=records[inc].get('idDoctoTes');
			registro.incluir=records[inc].get('incluir');
			registro.excluir=records[inc].get('excluir');
			registro.clasificacion=records[inc].get('clasificacion');
			
			matriz[cont]=registro;
			cont++;
		}
		
		jsonDoctoTes = Ext.util.JSON.encode(matriz);
		
		
		//CONDICION PAGO
		
		var matriz = new Array();
		
		var registro = {};
		
		registro.idCondicionPago = NS.idCondicionPago;
		
		if(Ext.getCmp(PF + 'optFechaBase').getValue() == null || Ext.getCmp(PF + 'optFechaBase').getValue() == undefined){
			registro.fechaBase = "";
		}else{
			registro.fechaBase = Ext.getCmp(PF + 'optFechaBase').getValue().getGroupValue();
		}
		
		if(Ext.getCmp(PF + 'optClaseDia').getValue() == null || Ext.getCmp(PF + 'optClaseDia').getValue() == undefined){
			registro.claseDia = "";
		}else{
			registro.claseDia = Ext.getCmp(PF + 'optClaseDia').getValue().getGroupValue();
		}
		
		if(Ext.getCmp(PF + 'optDiaAntPost').getValue() == null || Ext.getCmp(PF + 'optDiaAntPost').getValue() == undefined){
			registro.accionDia = "";
		}else{
			registro.accionDia = Ext.getCmp(PF + 'optDiaAntPost').getValue().getGroupValue();
		}
		
		//DIAS ADICIONALES
		if(Ext.getCmp(PF + 'chkAplica2').getValue() == true ){
			registro.diasAdicionales = Ext.getCmp(PF + 'txtIdDiasAdicionales').getValue();
			registro.aplicaDiasAdicionales = true
		}else{
			registro.aplicaDiasAdicionales = false
			registro.diasAdicionales = 0;
		}
		
		if(Ext.getCmp(PF + 'chkAplica3').getValue() == true){
			registro.finMes = 'S';
		}else{
			registro.finMes = 'N';
		}
		
		registro.bancoInterlocutor = Ext.getCmp(PF + 'txtBancoInter').getValue();
		
		matriz[0] = registro;

		jsonCondicionPago = Ext.util.JSON.encode(matriz);
		
		
		//DIAS CONTAB VENCIMIENTO
		if(Ext.getCmp(PF + 'chkAplica').getValue() == true){
			
			var matriz = new Array();
			
			var registro = {};
			registro.idContab = NS.idContab;
			registro.diaPago1 = NS.cmbDiasLunes.getValue();
			registro.diaPago2 = NS.cmbDiasMartes.getValue();
			registro.diaPago3 = NS.cmbDiasMiercoles.getValue();
			registro.diaPago4 = NS.cmbDiasJueves.getValue();
			registro.diaPago5 = NS.cmbDiasViernes.getValue();
			registro.diaPago6 = NS.cmbDiasSabado.getValue();
			registro.diaPago7 = NS.cmbDiasDomingo.getValue();
			registro.semana1 = NS.cmbSemanaProximaLunes.getValue();
			registro.semana2 = NS.cmbSemanaProximaMartes.getValue();
			registro.semana3 = NS.cmbSemanaProximaMiercoles.getValue();
			registro.semana4 = NS.cmbSemanaProximaJueves.getValue();
			registro.semana5 = NS.cmbSemanaProximaViernes.getValue();
			registro.semana6 = NS.cmbSemanaProximaSabado.getValue();
			registro.semana7 = NS.cmbSemanaProximaDomingo.getValue();
			registro.aplicaContab = true;
			matriz[0] = registro;
			
			jsonDiasContabVen = Ext.util.JSON.encode(matriz);
		}else{
			var matriz = new Array();
			
			var registro = {};
			registro.aplicaContab = false;
			matriz[0] = registro;

			jsonDiasContabVen = Ext.util.JSON.encode(matriz);
		}
		
		//DIAS ESPECIFICOS
		
		if(Ext.getCmp(PF + 'chkAplica4').getValue() == true){
			
			var matriz = new Array();
			
			var registro = {};
			registro.idDiasExcep = NS.idDiasExcep;
			registro.diaEspecifico1 = NS.txtIdDiaEspecifico.getValue();
			registro.diaExcep1 = NS.cmbDiasExcepcion.getValue();
			registro.diaPagoExcep1 = NS.cmbDiaPago.getValue();
			registro.rangoDiaDesde1 = NS.txtIdRangoDesde.getValue();
			registro.rangoDiaHasta1 = NS.txtIdRangoHasta.getValue();
			registro.diaEspecifico2 = NS.txtIdDiaEspecifico2.getValue();
			registro.diaExcep2 = NS.cmbDiaExepc.getValue();
			registro.diaPagoExcep2 = NS.cmbDiaPago2.getValue();
			registro.rangoDiaDesde2 = NS.txtIdRangoDesde2.getValue();
			registro.rangoDiaHasta2 = NS.txtIdRangoHasta2.getValue();								
			registro.aplicaDiasExcep = true;
			matriz[0] = registro;

			jsonDiaEsp = Ext.util.JSON.encode(matriz);
		}else{
			var matriz = new Array();
			
			var registro = {};
			registro.aplicaDiasExcep = false;
			matriz[0] = registro;

			jsonDiaEsp = Ext.util.JSON.encode(matriz);
		}
		
		jsonPlanPagos = "";
		//PLAN DE PAGOS
			if(Ext.getCmp(PF + 'chkAplica5').getValue() == true ){
				
				var matriz = new Array();
			var records = NS.storePlanPagos.data.items;
			
			for(var inc = 0; inc<records.length; inc++) {
				var registro = {};
				registro.idPlan = records[inc].get('idPlan');
				//registro.fecha=records[inc].get('fecha');
				var dt = new Date((''+records[inc].get('fecha')));
				registro.fecha = dt.format('d/m/Y');
				
				matriz[inc]=registro;
			}
			
			jsonPlanPagos = Ext.util.JSON.encode(matriz);
			
			}
			
			//RUBROS ASIGNADPS
		var matriz = new Array();
		var records = NS.storeRubrosAsignados.data.items;
		
		for(var inc = 0; inc<records.length; inc++) {
			var registro = {};
			registro.idRubro=records[inc].get('idRubro');
			registro.descRubro=records[inc].get('descRubro');
			
			matriz[inc]=registro;
		}
		
		jsonRubros = Ext.util.JSON.encode(matriz);
		
		Ext.getCmp(PF + 'btnAceptar').setDisabled(true);
		Ext.getCmp(PF + 'btnModificarArriba').setDisabled(true);
		Ext.getCmp(PF + 'btnEliminarRegla').setDisabled(true);
		
			if(Ext.getCmp(PF + 'btnAceptar').getText() == 'Aceptar' ){
				
				ReglasNegocioAction.insertarActualizarReglaNegocio(jsonReglaNegocio, jsonEmpresas, jsonAcreDocto, jsonDoctoTes, jsonCondicionPago,
														jsonDiasContabVen, jsonDiaEsp, jsonPlanPagos, jsonRubros, 
														'I','','','','','',
														false, false, false, false,
														NS.simulador, function(result, e){
				
					if(result != null && result != '' && result != undefined) {								
						
						if(result.error != ''){
							Ext.Msg.alert('SET',result.error);
							//cancela el login del simulador en dado caso que entrara en simulador y hay error.
							
							 ReglasNegocioAction.eliminaLogueoUsuario(function(res, e){
			 	        		  
		   	        				if(e.message=="Unable to connect to the server."){
		   	        					Ext.Msg.alert('SET','Error de conexión al servidor');
		   	        					return;
		   	        				}
			 	        	  });
							
							Ext.getCmp(PF + 'btnAceptar').setDisabled(false);
							Ext.getCmp(PF + 'btnModificarArriba').setDisabled(false);
							Ext.getCmp(PF + 'btnEliminarRegla').setDisabled(false);
							
							mascara.hide();
							return;
						}else if(result.mensaje != ''){
	
							//Entra al simulador
							if(NS.simulador == true){
								mascara.hide();
								NS.simulador = false;
								winSimuladorPropuestas.show();
							}else{
								
								Ext.Msg.alert('SET',result.mensaje);
								limpiarTodo(true);
								NS.simulador = false;
								Ext.getCmp(PF + 'btnAceptar').setDisabled(false);
								Ext.getCmp(PF + 'btnModificarArriba').setDisabled(false);
								Ext.getCmp(PF + 'btnEliminarRegla').setDisabled(false);
								
								mascara.hide();
								return;
								
							}
							
						}
						
				}
			});
				
			}else if(Ext.getCmp(PF + 'btnAceptar').getText() == 'Modificar' ){
				
				//Ultimo parametro indica si se va a insertar un nuevo registro('I' - insert) o si se va a modificar ('U' - update)
				ReglasNegocioAction.insertarActualizarReglaNegocio(jsonReglaNegocio, jsonEmpresas, jsonAcreDocto, jsonDoctoTes, jsonCondicionPago,
														jsonDiasContabVen, jsonDiaEsp, jsonPlanPagos, jsonRubros, 
														'U', NS.jsonEliminarAcre, NS.jsonEliminarDoctos,
														NS.jsonEliminarClase, NS.jsonEliminarTes, NS.jsonEliminarFechas, //jsonEliminar: elimina registros que se eliminaron con botn eliminar (Grids)
														NS.eliminaDiasContabVenc, NS.eliminaDiasAdicionales, //Elimina Dias, elimina todos los campos de la regla de negocio en la que deseleccionaron algun check aplica.
														NS.eliminaDiasespecificos, NS.eliminaPlanPagos, NS.simulador,
														function(result, e){
					
					
					if(result != null && result != '' && result != undefined) {								
				
						if(result.error != ''){
							Ext.Msg.alert('SET',result.error);
							Ext.getCmp(PF + 'btnAceptar').setDisabled(false);
							Ext.getCmp(PF + 'btnModificarArriba').setDisabled(false);
							Ext.getCmp(PF + 'btnEliminarRegla').setDisabled(false);
							mascara.hide();
							NS.simulador = false;
							return;
						}else if(result.mensaje != ''){
							
							if(NS.simulador == true){
								mascara.hide();
								NS.simulador = false;
								winSimuladorPropuestas.show();
							}else{
								Ext.Msg.alert('SET',result.mensaje);
								limpiarTodo(true);
								NS.simulador = false;
								Ext.getCmp(PF + 'btnAceptar').setDisabled(false);
								Ext.getCmp(PF + 'btnModificarArriba').setDisabled(false);
								Ext.getCmp(PF + 'btnEliminarRegla').setDisabled(false);
								mascara.hide();
								return;
							}
							
						}
						
				}
			});
				
			}
    }
    
    
    function cargarAcreedoresValidados(jsonDatos, jsonDatosEliminar){
    	//
    	ReglasNegocioAction.obtenerDatosValidadosAcreedores(jsonDatos, jsonDatosEliminar, function(result, e){
    		   
			var datosClase = NS.storeAcreedoresInc.recordType;
				var datosClase2 = NS.storeAcreedoresExc.recordType;
			
			 for(var i = 0; i < result.length; i++){
					
				 //Los totales se van incrementando, por eso se sacan en cada vuelta del ciclo
				var totalReg = NS.storeAcreedoresInc.data.items;
				var totalReg2 = NS.storeAcreedoresExc.data.items;
				
				 if(result[i].tipoAcre == 'I'){
					
					var datos = new datosClase({
					 	idAcreDocto: '',
							deAcre: result[i].deAcre,
					 	aAcre: result[i].aAcre,
					 	clasificacion: result[i].clasificacion,
					 	tipoAcre: result[i].tipoAcre
	            	});
					
					NS.storeAcreedoresInc.insert(totalReg.length, datos);
					
				 }else if(result[i].tipoAcre == 'E'){
					 
					var datos2 = new datosClase2({
 						idAcreDocto: '',
 						deAcre: result[i].deAcre,
					 	aAcre: result[i].aAcre,
					 	clasificacion: result[i].clasificacion,
					 	tipoAcre: result[i].tipoAcre
	            	});
					
					NS.storeAcreedoresExc.insert(totalReg2.length, datos2);
					
				 }
			 }
			 
			NS.gridDatosAcreedoresInc.getView().refresh();
			NS.gridDatosAcreedoresExc.getView().refresh();
    	});
    	//
    }

    function validarAcreedores(jsonString, tipoOper, grid){
    	
    	 ReglasNegocioAction.validarAcreedores(jsonString, tipoOper, function(result, e){
   		   
    		  if(result != null && result != '' && result != undefined) {								
					
					if(result.error != ''){
						Ext.Msg.alert('SET',result.error);
						
						//Si hay error eliminamos la fila que este seleccionada (sera la que se esta modificando)
						if(grid == 1){
							var sel = NS.gridDatosAcreedoresInc.getSelectionModel().getSelections();
							NS.gridDatosAcreedoresInc.store.remove(sel[0]);
							
							var datosClase = NS.gridDatosAcreedoresInc.store.recordType;
							
							//Generamos un renglon en blanco
							var datos = new datosClase({
							 	idAcreDocto: '',
	 							deAcre: '',
							 	aAcre: '',
							 	clasificacion: 'A',
							 	tipoAcre: 'I'
			            	});
							
							NS.gridDatosAcreedoresInc.store.add(datos);
							NS.gridDatosAcreedoresInc.getView().refresh();
							
						}else if(grid == 2){
							var sel = NS.gridDatosAcreedoresExc.getSelectionModel().getSelections();
							NS.gridDatosAcreedoresExc.store.remove(sel[0]);
							
							var datosClase = NS.gridDatosAcreedoresExc.store.recordType;
							
							//Generamos un renglon en blanco
							var datos = new datosClase({
							 	idAcreDocto: '',
								deAcre: '',
							 	aAcre: '',
							 	clasificacion: 'A',
							 	tipoAcre: 'E'
			            	});
							
							NS.gridDatosAcreedoresExc.store.add(datos);
							NS.gridDatosAcreedoresExc.getView().refresh();
						}
						
						return; 
					}else if(result.mensaje == 'Ok'){
						//Una vez validados cargamos los datos al grid
						 ReglasNegocioAction.obtenerDatosValidados(jsonString, 'A', function(result, e){
                 		   
							var datosClase = NS.storeAcreedoresInc.recordType;
		 					var datosClase2 = NS.storeAcreedoresExc.recordType;
							
							 for(var i = 0; i < result.length; i++){
			 					
								 //Los totales se van incrementando, por eso se sacan en cada vuelta del ciclo
								var totalReg = NS.storeAcreedoresInc.data.items;
								var totalReg2 = NS.storeAcreedoresExc.data.items;
								
								 if(result[i].tipoAcre == 'I'){
									
									var datos = new datosClase({
									 	idAcreDocto: '',
			 							deAcre: result[i].deAcre,
									 	aAcre: result[i].aAcre,
									 	clasificacion: result[i].clasificacion,
									 	tipoAcre: result[i].tipoAcre
					            	});
									
									NS.storeAcreedoresInc.insert(totalReg.length, datos);
									
								 }else if(result[i].tipoAcre == 'E'){
									 
									var datos2 = new datosClase2({
				 						idAcreDocto: '',
				 						deAcre: result[i].deAcre,
									 	aAcre: result[i].aAcre,
									 	clasificacion: result[i].clasificacion,
									 	tipoAcre: result[i].tipoAcre
					            	});
									
									NS.storeAcreedoresExc.insert(totalReg2.length, datos2);
									
								 }
							 }
							 
							NS.gridDatosAcreedoresInc.getView().refresh();
							NS.gridDatosAcreedoresExc.getView().refresh();
							
                 	   });
						 
					}else if(result.mensaje == 'A2D2'){
						
						var cont = 0;
						var cad = '';
						if(result.listaOriginal.length > 0){
							
							var datosClase = NS.storeWinAcre.recordType;
							
							NS.storeWinAcre.removeAll();
							
							for(var i=0; i<result.listaOriginal.length;i++){
								for(var j=0; j < result.listaOriginal[i].idAcreDocto; j++){
									
									var datos = new datosClase({
										rangoOriginal: result.listaOriginal[i].deAcre + '-' + result.listaOriginal[i].aAcre, 
										reglaNegocio: result.existe[cont].clasificacion,
										deAcre: result.existe[cont].deAcre,
										aAcre: result.existe[cont].aAcre
									});
									
									//NS.gridWinAcre.stopEditing();
									NS.storeWinAcre.add(datos);
									cont++;
								}

							}

							//NS.gridWinAcre.getView().refresh(); //No se puede llamar este grid porque todavía no existe hasta showde winAcre.
							winAcre.show();
							
						}
					}
					
				}
    		  
    	   });
    	 
    }
    
    function validaAcreedoresGrids(renglonSel, tipoGrid){
    	
    	if(tipoGrid == 'I'){
    		var encontradoAcreRep = false;
        	var todosDatos = NS.gridDatosAcreedoresInc.store.data.items;
        	
        	for(var i=0;i<todosDatos.length;i++){
        		if((renglonSel.get('deAcre') >= todosDatos[i].get('deAcre') && renglonSel.get('deAcre') <= todosDatos[i].get('aAcre')) 
        		|| (renglonSel.get('aAcre') >= todosDatos[i].get('deAcre') && renglonSel.get('aAcre') <= todosDatos[i].get('aAcre'))){
        		
        			//Si tiene la opcion individual activada
        			if(renglonSel.get('deAcre') == renglonSel.get('aAcre') && tipoGrid == 'I'){
        				
        				if(encontradoAcreRep){
        					return 1;
        				}else{
        					//Si son diferentes es que si hay match, si no es el renglon digitado
            				if((todosDatos[i].get('deAcre') != renglonSel.get('deAcre'))
            					&& (todosDatos[i].get('aAcre') != renglonSel.get('aAcre'))){
            					
            					return 1;
            				}else{
            					encontradoAcreRep = true;
            				}
        				}
        				
        			}else{
        				return 1;
        			}
        			
        		}
        	}
    	}
    		
    	if(tipoGrid == 'E'){
    		encontradoAcreRep = false;
        	var todosDatosExc = NS.gridDatosAcreedoresExc.store.data.items;
        	
        	for(var i=0;i<todosDatosExc.length;i++){
        		if((renglonSel.get('deAcre') >= todosDatosExc[i].get('deAcre') && renglonSel.get('deAcre') <= todosDatosExc[i].get('aAcre')) 
        		|| (renglonSel.get('aAcre') >= todosDatosExc[i].get('deAcre') && renglonSel.get('aAcre') <= todosDatosExc[i].get('aAcre'))){
        		
        			//Si tiene la opcion individual activada
        			if(renglonSel.get('deAcre') == renglonSel.get('aAcre') && tipoGrid == 'E'){
        				
        				if(encontradoAcreRep){
        					return 2;
        				}else{
        					//Si son diferentes es que si hay match, si no es el renglon digitado
            				if((todosDatosExc[i].get('deAcre') != renglonSel.get('deAcre'))
            					&& (todosDatosExc[i].get('aAcre') != renglonSel.get('aAcre'))){
            					
            					return 2;
            				}else{
            					encontradoAcreRep = true;
            				}
        				}
        				
        			}else{
        				return 2;
        			}
        			
        		}
        	}
    	}
    	
    	return 0;
    }
    
   
    /**********		COMIENZA GRID DE PROPUESTAS		**********/
    
  //Label para Total de importe
	NS.lblImporteMN = new Ext.form.Label({
		text: 'MN',
		x: 10,
		y: 200
	});
	
	//TextField para importes totales
	NS.txtImporteMNProp = new Ext.form.TextField({			
		id: PF + 'txtImporteMNProp',
		name: PF + 'txtImporteMNProp',
		x: 40,
		y: 200,
		width: 100,
		value: '0.00',
        disabled: true
	});
	
	//Label para importe DLS
	NS.lblImporteDLS = new Ext.form.Label({
		text: 'DLS',
		x: 150,
		y: 200
	});
	
	//TextField para importes totales
	NS.txtImporteDLSProp = new Ext.form.TextField({			
		id: PF + 'txtImporteDLSProp',
		name: PF + 'txtImporteDLSProp',
		x: 180,
		y: 200,
		width: 100,
		value: '0.00',
        disabled: true
	});
	
	//Label para importe EUR
	NS.lblImporteEUR = new Ext.form.Label
	({
		text: 'EUR',
		x: 290,
		y: 200
	});
	
	NS.txtImporteEURProp = new Ext.form.TextField
	({			
		id: PF + 'txtImporteEURProp',
		name: PF + 'txtImporteEURProp',
		x: 320,
		y: 200,
		width: 100,
		value: '0.00',
        disabled: true
	});
	  
	//Label para importe otras divisas
	NS.lblImporteOTR = new Ext.form.Label
	({
		text: 'OTRAS DIV',
		x: 430,
		y: 200
	});
	
	NS.txtImporteOTRProp = new Ext.form.TextField
	({			
		id: PF + 'txtImporteOTRProp',
		name: PF + 'txtImporteOTRProp',
		x: 495,
		y: 200,
		width: 100,
		value: '0.00',
        disabled: true
	});
	
    
    NS.storeMaestro = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			idGrupoEmpresa:0,
			idProv:0,
			idGrupoRubro:0,
	        fecIni: apps.SET.FEC_HOY,
	        fecFin: apps.SET.FEC_HOY,
	        soloMisProp:'false',
	        propVigentes:'false',
	        todasPro: false,
	        divMN: false,
	        divDLS:false,
	        divEUR:false,
	        divOtras:false,
	        tipoRegla: 'S',
	        reglaNegocio: ''
		},
		root: '',
		paramOrder:['idGrupoEmpresa','idProv','idGrupoRubro','fecIni','fecFin','soloMisProp','propVigentes', 'todasPro','divMN','divDLS','divEUR','divOtras','tipoRegla', 'reglaNegocio'],
		directFn: ReglasNegocioAction.consultarSeleccionAutomaticaSIM,
		//idProperty: 'marca',
		fields: [
			{name: 'cveControl'}, //id propuesta
			{name: 'idGrupoFlujo'}, 
			{name: 'propuesto'},
			{name: 'cupoTotal'}, //total
			{name: 'descGrupoCupo'},
			//{name: 'fecLimiteSelecc'}, //Fecha elaboracion
			//Recibe un tipo String formato dd/mm/yyyy y lo comvierte a date de Extjs.
			{name: 'fecLimiteSelecc', type: 'date', dateFormat:'d/m/Y'}, //Fecha elaboracion
			{name: 'fechaPropuesta',  type: 'date', dateFormat:'d/m/Y'}, 
			//{name: 'fechaPropuesta'},
			{name: 'idGrupo'}, //sociedad
			{name: 'descGrupoFlujo'}, //nombre sociedad
			{name: 'divisa'}, //moneda
			{name: 'origenPropuesta'}, //origen propuesta
			{name: 'concepto'},
			{name: 'estatus'},
			{name: 'usuarioUno'},
			{name: 'usuarioDos'},
			{name: 'usuarioTres'},
			{name: 'nivelAutorizacion'},
			//{name: 'idDivision'},
			//{name: 'numIntercos'},
			//{name: 'totalIntercos'},
			{name: 'user1'},
			{name: 'user2'},
			{name: 'viaPago'},
			//{name: 'user3'},
			{name: 'color'}
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMaestro, msg:"Cargando..."});
				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No existen propuestas con los parámetros de búsqueda');
				}
				
				mascara3.hide();
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara3.hide();
			}
		}
	});
  	 
  	//Para multiple seleccion en el grid
 	NS.seleccion = new Ext.grid.CheckboxSelectionModel
 	({
 		singleSelect: false,
 	});

 	//Columnas del grid	    	
 	NS.columnas = new Ext.grid.ColumnModel
 	([	
 	  	//NS.seleccion,
 	  	{
 			header :'Concepto',
 			width :120,
 			sortable :true,
 			dataIndex :'concepto',
 			renderer: function (value, meta, record) {
 					meta.attr = 'style=' + record.get('color');
 	            return value;
 	        }
 		},{	
 			header :'Fecha Pago',
 			width :100,
 			xtype: 'datecolumn',
 			format: 'd/m/Y',
 			//dateFormat: 'd/m/Y',	
 			sortable :true,
 			dataIndex :'fechaPropuesta', //fechaPropuesta
 			editor: {
	            xtype : 'datefield',
	            //format: 'Y-m-d H:i:s',
	            format: 'd/m/Y',
	            submitFormat: 'c'
	        },
 			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
 	            return value;
 			}
 		},{	
 			header :'Grupo de propuesta', //no empresa/no Grupo
 			width :100,
 			sortable :true,
 			dataIndex :'idGrupoFlujo', 
 			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
 	            return value;
 			},
 			hidden: true
 		},{	
 			header :'Nombre de Grupo de Propuesta', //PENDIENTE
 			width :200,
 			sortable :true,
 			dataIndex :'descGrupoFlujo', 
 			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
 	            return value;
 			},
 			hidden: true
 		},{
 			header :'Total',
 			width :80,
 			sortable :true,
 			dataIndex :'cupoTotal',
 			css: 'text-align:right;',
 			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
 	            return '$ ' + NS.formatNumber(Math.round((value)*100)/100);
 	        }
 		},{
 			header :'Moneda', //divisa
 			width :80,
 			sortable :true,
 			dataIndex :'divisa',
 			css: 'text-align:right;',
 			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
 	            return value;
 	        }
 		},{
 			header :'Vía Pago',	//PENDIENTE
 			width :80,
 			sortable :true,
 			dataIndex :'viaPago',
 			renderer: function (value, meta, record) { 				
 				meta.attr = 'style=' + record.get('color');
 	            return value;
 	        }
 		},{
			header :'ID Propuesta',
			width :100,
			sortable :true,
			dataIndex :'cveControl',
			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
				return value;
			}
		},{
			header :'Origen Propuesta',
			width :100,
			sortable :true,
			dataIndex :'origenPropuesta',
			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
				return value;
			}
		},{	
 			header :'Observación', //no empresa/no Grupo
 			width :100,
 			sortable :true,
 			dataIndex :'estatus', 
 			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color'); 				
 	            return value;
 			}
 		},{
 			header :'Nivel 1', 
 			width :75, sortable :true, 
 			dataIndex :'user1', 
 			hidden: false,
 			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
 				return value;
 			}
 		},{
 			header :'Nivel 2', 
 			width :75, sortable :true, 
 			dataIndex :'user2', 
 			hidden: true,
 			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
 				return value;
 			}
 		},{
			header :'Nivel de Autorización',
			width :120,
			sortable :true,
			dataIndex :'nivelAutorizacion',
			hidden: false,
			renderer: function (value, meta, record) {				
 				meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{	
 			header :'Fecha Elaboración',
 			width :100,
 			xtype: 'datecolumn',
 			format: 'd/m/y',
 			sortable :true,
 			dataIndex :'fecLimiteSelecc',
 			//renderer: Ext.util.Format.dateRenderer('Y-m-d'),
 			editor: {
	            xtype : 'datefield',
	            //format: 'Y-m-d H:i:s',
	            format: 'd/m/Y',
	            submitFormat: 'c'  
	        },
 			renderer: function (value, meta, record) {
 				meta.attr = 'style=' + record.get('color');
  	            return value;
 			}
 		}
 	  ]);

  	 
	//grid de datos
	 NS.gridMaestroPropuestas = new Ext.grid.GridPanel({
        store : NS.storeMaestro,
        viewConfig: {emptyText: 'Resumen de propuestas'},
        x:10,
        y:0,
        cm: NS.columnas,
		sm: NS.seleccion,
        width:760,
        height:195,
        frame:true,
        listeners: {
        	click: {
        		fn:function(e) {
        			
        			mascara.show();
        			
        			var regSeleccionados = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
        			var importeMN = 0;
        			var importeDLS = 0;
        			var importeEUR = 0;
        			var importeOTR = 0;
        			
        			Ext.getCmp(PF + 'txtImporteMNProp').setValue(NS.formatNumber(Math.round((importeMN)*100)/100));
        			Ext.getCmp(PF + 'txtImporteDLSProp').setValue(NS.formatNumber(Math.round((importeDLS)*100)/100));
        			Ext.getCmp(PF + 'txtImporteEURProp').setValue(NS.formatNumber(Math.round((importeEUR)*100)/100));
        			Ext.getCmp(PF + 'txtImporteOTRProp').setValue(NS.formatNumber(Math.round((importeOTR)*100)/100 ));
        			
        			if(regSeleccionados != null && regSeleccionados != undefined && regSeleccionados != '') {
        				
        				for(var i=0; i<regSeleccionados.length; i++) {
        					
        					if(regSeleccionados[i].get('concepto') != null && regSeleccionados[i].get('concepto') != undefined && regSeleccionados[i].get('concepto') != ''){
        						
	        					if(regSeleccionados[i].get('concepto').indexOf('MN') > 0){
	        						importeMN += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
	        						Ext.getCmp(PF + 'txtImporteMNProp').setValue(NS.formatNumber(Math.round((importeMN)*100)/100 ));
	        					}else if(regSeleccionados[i].get('concepto').indexOf('DLS') > 0){
	        						importeDLS += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
	        						Ext.getCmp(PF + 'txtImporteDLSProp').setValue(NS.formatNumber(Math.round((importeDLS)*100)/100));
	        					}else if(regSeleccionados[i].get('concepto').indexOf('EUR') > 0){
	        						importeEUR += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
	        						Ext.getCmp(PF + 'txtImporteEURProp').setValue(NS.formatNumber(Math.round((importeEUR)*100)/100 ));
	        					}else{
	        						importeOTR += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
	        						Ext.getCmp(PF + 'txtImporteOTRProp').setValue(NS.formatNumber(Math.round((importeOTR)*100)/100 ));
	        					}
        					}
	        					
						}
						
        				NS.storeDetalle.baseParams.idGrupoEmpresa=regSeleccionados[0].get('idGrupoFlujo');
		        		NS.storeDetalle.baseParams.idGrupoRubro=regSeleccionados[0].get('idGrupo');
		        		NS.storeDetalle.baseParams.cveControl=regSeleccionados[0].get('cveControl');
		        		NS.storeDetalle.baseParams.idUsuario1=regSeleccionados[0].get('usuarioUno');
		        		NS.storeDetalle.baseParams.idUsuario2=regSeleccionados[0].get('usuarioDos');
		        		//NS.storeDetalle.baseParams.idUsuario3=regSeleccionados[0].get('usuarioTres');
		        		
		        		NS.storeDetalle.load();
		        			        		
        			}else {
        				NS.gridDetalle.store.removeAll();
        				NS.gridDetalle.getView().refresh();
        				Ext.getCmp(PF + 'txtImporteMNProp').setValue('0.00');
            			Ext.getCmp(PF + 'txtImporteDLSProp').setValue('0.00');
            			Ext.getCmp(PF + 'txtImporteEURProp').setValue('0.00');
            			Ext.getCmp(PF + 'txtImporteOTRProp').setValue('0.00');
            			
        				Ext.getCmp(PF+'totalMN').setValue('0.00');
              			Ext.getCmp(PF+'sumMN').setValue('0.00');
              			Ext.getCmp(PF+'totalDLS').setValue('0.00');
              			
              			mascara.hide();
        			}
        			
        		}
        	}
        }
    });
	 
	 
	/**********		EMPIEZA GRID DETALLE	**********/
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	 
	
	 NS.storeDetalle = new Ext.data.DirectStore({
	  	 	paramsAsHash: false,
	  	 	baseParams: {
				idGrupoEmpresa:0,
				idGrupoRubro:0,
				cveControl:'',
				idUsuario1:0,
				idUsuario2:0,
				idUsuario3:0
			},
			root: '',
			paramOrder:['idGrupoEmpresa','idGrupoRubro','cveControl','idUsuario1','idUsuario2','idUsuario3'],
			directFn: ReglasNegocioAction.consultarDetalle,
			fields: [
				{name: 'nomEmpresa'},//Grupo
				{name: 'noEmpresa'},//Nom. Empresa
				{name: 'noDocto'},//No. Docto
				{name: 'nomProveedor'},//Proveedor
				{name: 'importe'},//importe
				{name: 'fecValorOriginalStr', type: 'date', dateFormat:'d/m/Y'},//fecha de vencimiento
				{name: 'idDivisa'},//Divisa
				{name: 'importeMn'},//ImporteMN
				{name: 'descFormaPago'},//Forma Pago
				{name: 'fecPropuestaStr', type: 'date', dateFormat:'d/m/Y'},//Fec Prop
				{name: 'bancoPago'},//Banco de Pago
				{name: 'idChequera'},//Chequera de Pago
				{name: 'descGrupoCupo'},//Grupo de Rubros
				{name: 'idRubro'},//Rubro
				{name: 'importeOriginal'},//Importe Original
				{name: 'idDivisaOriginal'},//DivisaOriginal
				{name: 'beneficiario'},//Beneficiario
				{name: 'noFolioDet'},//No. Folio Det
				{name: 'concepto'},//Concepto
				{name: 'idBancoBenef'},//Id Banco Benef
				{name: 'idChequeraBenef'},//Id Chequera Benef
				{name: 'noFactura'},//No. Factura
				{name: 'diasInv'},//Dias Vto
				{name: 'origenMov'},//Origen
				{name: 'noCliente'},//No. Persona
				{name: 'noCheque'},//No. Cheque
				{name: 'idBanco'},//No. Banco pagador
				{name: 'usr1'},
				{name: 'usr2'},
				{name: 'color'},
				{name: 'fecContabilizacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Contabilizacion
				{name: 'fecOperacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Documento
				{name: 'fecValorStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Vencimiento
				{name: 'equivale'},
				{name: 'idContable'},	//clase docto.
				{name: 'comentario1'}, //Comentario1 : indicara la regla de negocios de la cual viene si ya estaba asignado a una propuesta.
				{name: 'comentario2'}	//comentario2: indica la propuesta de la que viene el movimiento.

			],
			listeners: {
				load: function(s, records){
					mascara.hide();
					
					var totalImporte=0;
          			var totalImporteDLS=0;
          			var regSelec=NS.gridDetalle.store.data.items;
          			
          			for(var k=0; k<regSelec.length; k++) {
          				if(regSelec[k].get('idDivisa') == 'DLS')
          					totalImporteDLS += regSelec[k].get('importe');
          				else
          					totalImporte += regSelec[k].get('importe');
          			}
          			Ext.getCmp(PF+'totalMN').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));
          			//Ext.getCmp(PF+'sumMN').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));
          			Ext.getCmp(PF+'totalDLS').setValue(NS.formatNumber(Math.round((totalImporteDLS)*100)/100));
          			
          			
				},
				exception: function(mist){
					Ext.Msg.alert('SET','Error en conexión al servidor');
					mascara.hide();
				}
			}
		}); 

	 
	 NS.gridDetalle = new Ext.grid.GridPanel({
	        store : NS.storeDetalle,
	        cm: new Ext.grid.ColumnModel({
	            defaults: {
	                width: 100,
	                value:true,
	                sortable: true
	            },
	            columns: [
	                //NS.sm,
	                {
						header :'No.Empresa',
						width :70,
						sortable :true,
						dataIndex :'noEmpresa',
						hidden: false,
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'No. Benef',
						width :80,
						sortable :true,
						dataIndex :'equivale',
						hidden: false,
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'Beneficiario',
						width :200,
						sortable :true,
						dataIndex :'beneficiario',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'No. Factura',
						width :80,
						sortable :true,
						dataIndex :'noFactura',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						
						header :'No.Docto',
						width :60,
						sortable :true,
						dataIndex :'noDocto',
						//hidden: true,
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'Clase Docto',
						width :80,
						sortable :true,
						dataIndex :'idContable',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
			        },{
						header :'Importe',
						width :90,
						sortable :true,
						dataIndex :'importe',
						css: 'text-align:right;',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return '$' + NS.formatNumber(Math.round((value)*100)/100 );
				        }
					},{
						header :'Forma Pago',
						width :120,
						sortable :true,
						dataIndex :'descFormaPago',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'Concepto',
						width :250,
						sortable :true,
						dataIndex :'concepto',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        },
				        listeners:{
				        	click:{
				        		fn:function(e){
				          			var regSelec = NS.gridDetalle.getSelectionModel().getSelections();
				          			
				          			if(regSelec.length > 0) {
				          				Ext.Msg.confirm('SET', '¿Desea ver el concepto completo?', function(btn) {
					          				if(btn == 'yes') {
					          					Ext.Msg.alert('SET','Concepto: ' + regSelec[0].get('concepto'), 'INFO');
					          				}
					          			});
				          			}
				        		}
				        	}
				        }
					},{
						header :'Fecha Prop.',
						width :100,
						sortable :true,
						dataIndex :'fecPropuestaStr',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        },
				        xtype: 'datecolumn',
			 			format: 'd/m/Y',
			 			editor: {
				            xtype : 'datefield',
				            //format: 'Y-m-d H:i:s',
				            format: 'd/m/Y',
				            submitFormat: 'c'
				        }
					},{
						header :'Fecha Venc.',
						width :100,
						sortable :true,
						dataIndex :'fecValorStr', //
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        },
				        xtype: 'datecolumn',
			 			format: 'd/m/Y',
			 			editor: {
				            xtype : 'datefield',
				            //format: 'Y-m-d H:i:s',
				            format: 'd/m/Y',
				            submitFormat: 'c'
				        }
					},{
						header :'Fecha Contabilización',
						width :100,
						sortable :true,
						dataIndex :'fecContabilizacionStr',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color'); 
				            return value;
				        },
				        xtype: 'datecolumn',
			 			format: 'd/m/Y',
			 			editor: {
				            xtype : 'datefield',
				            //format: 'Y-m-d H:i:s',
				            format: 'd/m/Y',
				            submitFormat: 'c'
				        }
					},{
						header :'Fecha Documento',
						width :100,
						sortable :true,
						dataIndex :'fecOperacionStr',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        },
				        xtype: 'datecolumn',
			 			format: 'd/m/Y',
			 			editor: {
				            xtype : 'datefield',
				            format: 'd/m/Y',
				            submitFormat: 'c'
				        }
					},{
						header :'Banco Pago',
						width :90,
						sortable :true,
						dataIndex :'bancoPago',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'Chequera de Pago',
						width :100,
						sortable :true,
						dataIndex :'idChequera',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'Id Banco Benef',
						width :90,
						sortable :true,
						dataIndex :'idBancoBenef',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'Id Chequera Benef',
						width :90,
						sortable :true,
						dataIndex :'idChequeraBenef',
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'Propuesta Anterior',
						width :70,
						sortable :true,
						dataIndex :'comentario2',
						hidden: false,
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					},{
						header :'Regla anterior',
						width :70,
						sortable :true,
						dataIndex :'comentario1',
						hidden: false,
						renderer: function (value, meta, record) {
				            meta.attr = 'style=' + record.get('color');
				            return value;
				        }
					}
	            ]
	        }),
	        columnLines: true,
	        x:10,
	        y:0,
	        width:760,
	        height:175,
	        frame:true,
	        sm: NS.sm,
	        listeners:{
	        	click:{
	        		fn:function(e){
	        			
	        			// LIMPIA COMPONENTES DEL GRID A MODIFICAR EMS 261115
	        		
	          			var totalImporte=0;
	          			var totalImporteDLS=0;
	          			var regSelec=NS.gridDetalle.getSelectionModel().getSelections();
	          			
	          			for(var k=0; k<regSelec.length; k++) {
	          				if(regSelec[k].get('idDivisa') == 'DLS')
	          					totalImporteDLS += regSelec[k].get('importe');
	          				else
	          					totalImporte += regSelec[k].get('importe');
	          			}
	          			Ext.getCmp(PF+'totalMN').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));
	          			//Ext.getCmp(PF+'sumMN').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));
	          			Ext.getCmp(PF+'totalDLS').setValue(NS.formatNumber(Math.round((totalImporteDLS)*100)/100));
	          			
	        		}
	        	}
	        }
	    });
	 
	 
    //	CREACIÓN DE VENTANA PARA EL SIMULADOR DE PROPUESTAS
	 
    var winSimuladorPropuestas = new Ext.Window({
		title: 'Simulador de propuestas',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 850,
	   	height: 600,
	   	//x: 800,
	   	//y: 100,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    draggable: false,
	    resizable: false,
	   	items: [
	   	        {
	   	        	xtype: 'fieldset',
	                title: 'Propuestas',
	                width: 800,
	                height: 260,
	                x: 10,
	                y: 10,
	                layout: 'absolute',
	                id: PF + 'fieldSimProp',
	                items: [
							NS.lblImporteMN,
							NS.txtImporteMNProp,
							NS.lblImporteDLS, 
	                        NS.txtImporteDLSProp, 
	                        NS.lblImporteEUR, 
	                        NS.txtImporteEURProp, 
	                        NS.lblImporteOTR, 
	                        NS.txtImporteOTRProp,
	                        NS.gridMaestroPropuestas
					]
	   	        },{
	   	        	xtype: 'fieldset',
	                title: 'Detallado Propuestas',
	                width: 800,
	                height: 245,
	                x: 10,
	                y: 280,
	                layout: 'absolute',
	                id: PF + 'fieldSimDetProp',
	                items: [
	                        NS.gridDetalle,
	                        {
                                xtype: 'textfield',
                                x: 60,
                                y: 180,
                                width: 100,
                                id: PF+'totalMN',
                                name: PF+'totalMN',
                                disabled: true
                                
                            },
                            {
                                xtype: 'textfield',
                                x: 240,
                                y: 180,
                                width: 100,
                                id: PF+'totalDLS',
                                name: PF+'totalDLS',
                                disabled: true
                            },
                            /*{
                            	xtype: 'textfield',
                     	        x: 430,
                     	        y: 180,
                     	        width: 100,
                     	        id: PF+'sumMN',
                     	        name: PF+'sumMN',
                     	        value: '0.00',
                     	        disabled: true
                            },*/
                            {
                                xtype: 'label',
                                text: 'Total MN:',
                                x: 10,
                                y: 185
                            },
                            {
                                xtype: 'label',
                                text: 'Total DLS:',
                                x: 180,
                                y: 185
                            },
                            /*
                            {
                                xtype: 'label',
                                text: 'Suma en MN:',
                                x: 350,
                                y: 185
                            }*/
					]
	   	        },{
			 		xtype: 'button',
			 		id: PF + 'btnSmlAceptar',
			 		name: PF + 'btnSmlAceptar',
			 		text: 'Aceptar',
			 		x: 625, 
			 		y: 535,
			 		width: 80,
			 		height: 22,
			 		listeners: {
			 			click: {
			 				fn: function(e) {
			 					
			 					var seleccion = NS.gridRelacion.getSelectionModel().getSelections();
		   	        			var regla = 0;
		   	        			
		   	        			if(seleccion.length > 0){
		   	        				regla = seleccion[0].get('idRegla');
		   	        			}
		   	        			
		   	        			mascara.show();
		   	        			
			 					ReglasNegocioAction.actualizarMovtosSimulador(regla, function(res, e){
				 	        		  
			 						mascara.hide();
			 						
		   	        				if(e.message=="Unable to connect to the server."){
		   	        					Ext.Msg.alert('SET','Error de conexión al servidor');
		   	        					return;
		   	        				}
			 	        		  
			 	        		 
			 	        		  if(res.error != null && res.error != undefined && res.error != "") {
			 	        			 Ext.Msg.alert('SET',res.error);
		   	        				 return;
			 	        		  }else if(res.msgUsuario != null && res.msgUsuario != undefined && res.msgUsuario != ""){
			 	        			 Ext.Msg.alert('SET',res.msgUsuario);
			 	        			
			 	        			limpiarTodo(true);
			 	        			winSimuladorPropuestas.hide();
			 	        			
		   	        				 return;
			 	        		  }else{
			 	        			 Ext.Msg.alert('SET','Error al actualizar los datos de la simulación');
		   	        				 return;
			 	        		  }
			 	        		  
			 	        	  });
			 					
			 				}
			 			}
			 		}
			 	},{
			 		xtype: 'button',
			 		id: PF + 'btnSmlCancelar',
			 		name: PF + 'btnSmlCancelar',
			 		text: 'Cancelar',
			 		x: 725, 
			 		y: 535,
			 		width: 80,
			 		height: 22,
			 		listeners: {
			 			click: {
			 				fn: function(e) {
			 					
			 					Ext.Msg.confirm('SET', 'No se cargará ningún registro mostrados en esta ventana, ¿Desea continuar?', function(btn){
		 	 						if (btn === 'yes'){	
		 	 							winSimuladorPropuestas.hide();		 	 							
		 	 						}
		 						});
								
			 				}
			 			}
			 		}
			 	}	   	       
	   	        ],
	   	        listeners:{
	   	        	show:{
	   	        		fn:function(){
	   	        			//	Limpiar componentes y remover stores de la ventana modal.
	   	        			mascara2.show();
	   	        			
	   	        			NS.gridMaestroPropuestas.store.removeAll();
   	        				NS.gridMaestroPropuestas.getView().refresh();
   	        				NS.gridDetalle.store.removeAll();
   	        				NS.gridDetalle.getView().refresh();
   	        				
   	        				
	   	        			var seleccion = NS.gridRelacion.getSelectionModel().getSelections();
	   	        			var regla = 0;
	   	        			
	   	        			if(seleccion.length > 0){
	   	        				regla = seleccion[0].get('idRegla');
	   	        			}
	   	        				   	        			
	   	        			ReglasNegocioAction.procesaSimulador(regla, function(res, e){
		 	        		  
	   	        				if(e.message=="Unable to connect to the server."){
	   	        					Ext.Msg.alert('SET','Error de conexión al servidor');
	   	        					return;
	   	        				}
		 	        		  
	   	        				mascara2.hide();
		 	        		  
	   	        				mascara3.show();
	   	        				NS.storeMaestro.load();
	   	        				
	   	        				NS.gridMaestroPropuestas.store.removeAll();
	   	        				NS.gridMaestroPropuestas.getView().refresh();
	   	        				NS.gridDetalle.store.removeAll();
	   	        				NS.gridDetalle.getView().refresh();
	   	     				
		 	        		  /*if(res != null && res != undefined && res == "") {
		 	        			  alert(res);
		 	        		  }*/
		 	        	  });
		 	        	   
	   	        		}
	   	        	},
	   	        	hide:{
	   	        		fn:function(){
	   	        			
	   	        			mascara2.hide();
	   	        			mascara3.hide();
	   	        			
	   	        			ReglasNegocioAction.deshacerCambiosSimulador(function(res, e){
			 	        		  
	   	        				if(e.message=="Unable to connect to the server."){
	   	        					Ext.Msg.alert('SET','Error de conexión al servidor');
	   	        					return;
	   	        				}
		 	        		  
		 	        		   if(res.error != null && res.error != undefined && res.error != "") {
		 	        			 Ext.Msg.alert('SET',res.error);
	   	        				 return;
		 	        		   }else{
		 	        			   
		 	        			  ReglasNegocioAction.eliminaLogueoUsuario(function(res, e){
				 	        		  
			   	        				if(e.message=="Unable to connect to the server."){
			   	        					Ext.Msg.alert('SET','Error de conexión al servidor');
			   	        					return;
			   	        				}
				 	        		  
				 	        		   /*if(res.error != null && res.error != undefined && res.error != "") {
				 	        			 Ext.Msg.alert('SET',res.error);
			   	        				 return;
				 	        		   }*/
				 	        	  });
			   	        			
		 	        			  
		 	        		   
		 	        		   }
		 	        		  
		 	        	  });
	   	        			
	   	        		}
	   	        	}
	   	        }	
		});
    //FIN VENTANA SIMULADOR PROPUESTAS.
    
  //Formato de un numero a monetario
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
	
	//Quitar formato a las cantidades
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,''); //num.replace(/([^0-9\.\-])/g,''*1);
	};
	
    
	NS.ReglasNegocios.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});
