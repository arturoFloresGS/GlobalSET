Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.ProcesosGenerales');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecha = NS.fecHoy;
	/*
	 * *******Hace falta esta facultad en el boton de ejecutar
	 *  If Not gobjSeguridad.ValidaFacultad("CIERRE", sMensajeFac) Then
        lblFin.Caption = "No tiene la facultad necesaria " & "No tiene facultades para realizar esta operación" & "No tiene facultades para realizar esta operación"
        imgValida.Visible = True
        lblFin.Visible = True
        cmdAceptar.Enabled = False
        MsgBox "No tiene facultades para realizar esta operación.", vbExclamation, "SET"
        Exit Sub
    End If
	 */
	
	//LABEL
	NS.lblFecha = new Ext.form.Label({
		text: 'Fecha de Cierre',
		x: 0,
		y: 0	
	});
	
	//TEXTFIELD
	NS.txtFecha = new Ext.form.TextField({
		id: PF + 'txtFecha',
		name: PF + 'txtFecha',
		x: 0,
		y: 15,
		width: 130
	});
	
	ProcesosGeneralesAction.obtieneFecha(function(resultado, e){
		if (resultado != 0 && resultado != "" && resultado != undefined)
			Ext.getCmp(PF + 'txtFecha').setValue(resultado);		
		else
			Ext.getCmp(PF + 'txtFecha').setValue(NS.fecHoy);
			
	});
	
	
	//FUNCOINES
	NS.limpiar = function(){
		NS.gridConsulta.store.removeAll();
		NS.gridConsulta.getView().refresh();
	};
	
	NS.ejecutar = function(){		
		//Valida el estatus_sist que tiene la tabla de fechas
		ProcesosGeneralesAction.validaEstatus(function(resultado, e){
			if (resultado != 0 && resultado != '' && resultado != undefined){				
				switch (resultado){
				case 2:					
					Ext.Msg.alert('SET', 'Necesita dar de alta los tipos de cambio antes de realizar el cierre');					
					break;
				case 4://Si entra a este caso se actualiza el Estatus
					//Obtiene el estatus_sist2
					ProcesosGeneralesAction.actualizaEstatusSist2(function(resultado, e){						
					});
					break;
				default:
					if (resultado < 2){
						Ext.Msg.confirm('SET', '¿Ya imprimió TODOS los reportes? Presione NO para cancelar', function(btn){
							if (btn === 'yes'){
								//Valida que no existan usuarios conectados en el sistema
								ProcesosGeneralesAction.validaUsuariosConectados(function(resultado, e){
									if (resultado != 0 && resultado != '' && resultado != undefined){
										Ext.Msg.alert('SET', 'Hay usuarios conectados al Sistema');									
									}
									else if (resultado == 0){
										Ext.Msg.confirm('SET', 'Desea continuar con el cierre?', function(btn){
											if (btn === 'yes'){
												//Se revisa si el configura tiene la opcion para hacer respando de la Base
												ProcesosGeneralesAction.respaldoBD(130, function (resultado, e){													
														if (resultado == 'SI'){
															ProcesosGeneralesAction.generaRespaldoBD(Ext.getCmp(PF + 'txtFecha').getValue(), function(resultado, e){
																if (resultado != 5)
																	ProcesosGeneralesAction.correCierre(NS.idUsuario, Ext.getCmp(PF + 'txtFecha').getValue(), function(resultado, e){
																		Ext.Msg.alert('SET', resultado);
																	});
															});
														}
														else{
															//Ext.Msg.confirm('SET', 'No existe registro en el configura Set para el respaldo, ¿Desea continuar?con el cierre?', function(btn){
																ProcesosGeneralesAction.correCierre(NS.idUsuario, Ext.getCmp(PF + 'txtFecha').getValue(), function(resultado, e){
																	Ext.Msg.alert('SET', resultado);
																});
															//});
														}
												});
												
											}
										});
									}
										
								});
							}
						});
					}
					else if (resultado >= 4){
						ProcesosGeneralesAction.respaldoBD(130, function (resultado, e){											
							if (resultado == 'SI')
								ProcesosGeneralesAction.generaRespaldoBD(Ext.getCmp(PF + 'txtFecha').getValue(), function(resultado, e){
									if (resultado != 5){
										ProcesosGeneralesAction.correCierre(NS.idUsuario, Ext.getCmp(PF + 'txtFecha').getValue(), function(resultado, e){
											Ext.Msg.alert('SET', resultado);
										});
									}
									else{
										Ext.Msg.confirm('SET', 'La ruta especificada para el respaldo no es correcta, ¿Desea continuar?con el cierre?', function(btn){
											if (btn === 'yes'){
												ProcesosGeneralesAction.correCierre(NS.idUsuario, Ext.getCmp(PF + 'txtFecha').getValue(), function(resultado, e){
													Ext.Msg.alert('SET', resultado);
												});
											}												
										});
									}							
								});
							else
								Ext.Msg.confirm('SET', 'No existe registro en el configura Set, ¿Desea continuar?con el cierre?', function(btn){
									ProcesosGeneralesAction.correCierre(NS.idUsuario, Ext.getCmp(PF + 'txtFecha').getValue(), function(resultado, e){
										Ext.Msg.alert('SET', resultado);
									});
								});						
							});
						
					}
					break;
						
				}
			}
		});
		
	};
	
	//STORE
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ProcesosGeneralesAction.llenaGrid,
		fields:
		[
		 	{name: 'usuario'},
		 	{name: 'usuarioNT'},
		 	{name: 'dominio'},
		 	{name: 'hostName'},
		 	{name: 'programa'},
		 	{name: 'ultimaConsulta'},
		 	{name: 'idProceso'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando información..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Usuarios Conectados');				
			}
		}		
	});
	
	//GRID
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGrid = new Ext.grid.ColumnModel([
	    {header: 'Usuario', width: 200, dataIndex: 'usuario', sortable: true},
	    {header: 'Usuario NT', width: 150, dataIndex: 'usuarioNT', sortable: true},
	    {header: 'Dominio', width: 200, dataIndex: 'dominio', sortable: true},
	    {header: 'Host Name', width: 100, dataIndex: 'hostName', sortable: true},
	    {header: 'Programa', width: 100, dataIndex: 'programa', sortable: true},
	    {header: 'Ultima Consulta', width: 100, dataIndex: 'ultimaConsulta', sortable: true},
	    {header: 'Id Proceso', width: 100, dataIndex: 'idProceso', sortable: true}	    
	]);
	
	NS.gridConsulta = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'gridConsulta',
		name: PF + 'gridConsulta',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		x: 0,
		y: 0,
		width: 960,
		height: 300,
		stripRows: true,
		columnLines: true,
		listeners:
		{
			click:
			{
				fn: function(grid)
				{}
			}
		}
	});
	
	NS.panelGrid = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 50,
		width: 985,
		height: 350,
		layout: 'absolute',
		items:
		[
		 	NS.gridConsulta
		]
	});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1010,		
		height: 490,
		layout: 'absolute',
		items:
		[				 	
		 	NS.lblFecha,
		 	NS.txtFecha,
		 	NS.panelGrid,
		 	{
		 		xtype: 'button',
		 		text: 'Ejecutar',
		 		x: 650,
		 		y: 430,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.ejecutar();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Usuarios Conectados',
		 		x: 750,
		 		y: 430,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.storeLlenaGrid.load();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Limpiar',
		 		x: 880,
		 		y: 430,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					NS.limpiar();
		 				}
		 			}
		 		}
		 	}
	    ]
	});
	
	NS.procesosGenerales = new Ext.FormPanel ({
		title: 'Procesos Generales',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'procesosGenerales',
		name: PF + 'procesosGenerales',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	NS.procesosGenerales.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});
 