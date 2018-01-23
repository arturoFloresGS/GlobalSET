/**
 * ERIC CESAR GUZMAN MALVAEZ
 */


Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Derivados.CargaForwards');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.idUsuario = apps.SET.iUserId;
	
	
	NS.obj;
	NS.obj2;
	NS.contador;
	NS.totalAprovado;
	
	/***************
	 * COMPONENTES *
	 ***************/

	NS.mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Validando ..."});
	
	
	NS.limpiarEtiquetas= function(){
		NS.lbMoviLeidosExcel.hide();
		NS.lbMovimientosLeidosExcel.setText('0');
		NS.lbMovimientosLeidosExcel.hide();
		
		NS.lbContMovValidos.setText('0');
		NS.lbContMovValidos.hide();
		NS.lbMovValidos.hide();
		
		NS.lbContMovValidos2.setText('0');
		NS.lbContMovValidos2.hide();
		NS.lbMovValidos2.hide();
		NS.contador = 0;
		
		
	}

	
	NS.lbMoviLeidosExcel = new Ext.form.Label({
		text: 'Movimientos leidos: ',
		x: 250,
		y : 135	,
		renderTo: Ext.getBody(),
		hidden:true,
		labelStyle: 'font-weight:bold;'
	});
	
	NS.lbMovimientosLeidosExcel = new Ext.form.Label({
		text: '0',
		x: 350,
		y : 135	,
		hidden:true,
	});

	

	
 //------------------------

	
	NS.lbMovValidos = new Ext.form.Label({
		text: 'Movimientos validos: ',
		x: 380,
		y : 135,
		hidden:true,
	});
	
	NS.lbContMovValidos = new Ext.form.Label({
		text: '0',
		x: 490,
		y : 135,
		hidden:true,
	});
	
	NS.lbMovValidos2 = new Ext.form.Label({
		text: 'Movimientos aplicados: ',
		x: 380,
		y : 135,
		hidden:true,
	});
	
	NS.lbContMovValidos2 = new Ext.form.Label({
		text: '0',
		x: 500,
		y : 135,
		hidden:true,
	});

	

//	¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬
//	    FUNCIONES
//	¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬
	
	NS.insertarValor = function(renglones){
		NS.datosClase = NS.gridDatosExcel.getStore().recordType;
		NS.contador = 0;
		for(i=0; i < renglones.length; i++){
			var datos = new NS.datosClase({
				folio: renglones[i].folio,
				unidad_negocio: renglones[i].unidad_negocio,				
				chequera_cargo: renglones[i].chequera_cargo,
				chequera_abono: renglones[i].chequera_abono,
				forma_pago: renglones[i].forma_pago,
				importe_pago: renglones[i].importe_pago,
				importe_compra: renglones[i].importe_compra, 
				tc: renglones[i].tc,
			 	fec_vto: renglones[i].fec_vto + '',			 	
			 	institucion: renglones[i].institucion,
			 	rubro_abono: renglones[i].rubro_abono,
			 	subrubro_abono: renglones[i].subrubro_abono,
			 	rubro_cargo: renglones[i].rubro_cargo,			 	
			 	subrubro_cargo: renglones[i].subrubro_cargo,
			 	fec_compra: renglones[i].fec_compra + '',			 	
			 	spot: renglones[i].spot,
			 	puntos_forward: renglones[i].puntos_forward,
			 	referencia: renglones[i].referencia,
			 	concepto: renglones[i].concepto,
			 	observacion: renglones[i].observacion,
        	});

			NS.gridDatosExcel.stopEditing();
			NS.storeLlenarGridatosExcel.insert(i, datos);
			NS.gridDatosExcel.getView().refresh();
		}		
	};


	
	

	
	
	
	
	NS.storeLlenarGridatosExcel = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {json: ''},
		paramOrder: ['json'],
		directFn: ForwardsAction.validarDatosExcel,
		fields:
		[		
//FOLIO	UNIDAD_DE_NEGOCIO	CHEQUERA_CARGO	CHEQUERA_ABONO	FORMA_PAGO	IMPORTE PAGO	IMPORTE COMPRA	 T_C 	FEC_VTO	INSTITUCION	RUBRO_ABONO	SUBRUBRO_ABONO	RUBRO_CARGO	SUBRUBRO_CARGO	FEC_COMPRA	SPOT	PUNTOS_FORWARD
		    {name: 'folio'},
		 	{name: 'unidad_negocio'},
		 	{name: 'chequera_cargo'},
		 	{name: 'chequera_abono'},
		 	{name: 'forma_pago'},
		 	{name: 'importe_pago'},
		 	{name: 'importe_compra'},
		 	{name: 'tc'},
		 	{name: 'fec_vto'},
		 	{name: 'institucion'},
		 	{name: 'rubro_abono'},		 			 
		 	{name: 'subrubro_abono'},
		 	{name: 'rubro_cargo'},
		 	{name: 'subrubro_cargo'},
		 	{name: 'fec_compra'},
		 	{name: 'spot'},
		 	{name: 'puntos_forward'},
		 	{name: 'referencia'},
		 	{name: 'concepto'},
		 	{name: 'observacion'},
		],
		listeners:
		{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridatosExcel, msg: "Cargando Datos ..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay datos que mostrar');
				}else{
					NS.datos = NS.storeLlenarGridatosExcel.data.items;
					NS.contador = 0;
					var valid =0;
					var invalid=0;
					
					for(var i = 0; i < NS.datos.length; i++ ){
						if(NS.datos[i].get('observacion') == ''){
							valid++;
							NS.contador ++;
							NS.gridDatosExcel.getView().getRow(i).style.backgroundColor  = '#4BD844'; //verde
						}else{
							invalid++;
							NS.gridDatosExcel.getView().getRow(i).style.backgroundColor  = '#FFA500'; //naranja
						}											
						
					}	
					NS.lbMovimientosLeidosExcel.setText((valid+invalid)+'');
					NS.lbContMovValidos.setText(valid+'');
						

//					if(NS.contador == NS.datos.length){
//						Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
//					}
					
				}
				NS.mascara.hide();
					
			}
		}

	});
	
	NS.storeLlenarGriDatosImp = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {json: ''},
		paramOrder: ['json'],
		fields:
		[	
		    {name: 'folio'},
		 	{name: 'unidad_negocio'},
		 	{name: 'chequera_cargo'},
		 	{name: 'chequera_abono'},
		 	{name: 'forma_pago'},
		 	{name: 'importe_pago'},
		 	{name: 'importe_compra'},
		 	{name: 'tc'},
		 	{name: 'fec_vto'},
		 	{name: 'institucion'},
		 	{name: 'rubro_abono'},		 			 
		 	{name: 'subrubro_abono'},
		 	{name: 'rubro_cargo'},
		 	{name: 'subrubro_cargo'},
		 	{name: 'fec_compra'},
		 	{name: 'spot'},
		 	{name: 'puntos_forward'},
		 	{name: 'referencia'},
		 	{name: 'concepto'},
		],
		listeners:
		{
			load: function(s, records){
				
			}
		}

	});
	
	NS.columnasCobImportadas = new Ext.grid.ColumnModel([
	    new Ext.grid.RowNumberer(),                                            
	   {header: 'FOLIO', width: 80, dataIndex: 'folio' ,sortable: true},	
	   {header: 'UNIDAD_DE_NEGOCIO', width: 120, dataIndex: 'unidad_negocio' ,sortable: true},                                        	
	   {header: 'CHEQUERA_CARGO', width: 120, dataIndex: 'chequera_cargo' ,sortable: true},
	   {header: 'CHEQUERA_ABONO', width: 120, dataIndex: 'chequera_abono' ,sortable: true},
	   {header: 'FORMA_PAGO', width: 180, dataIndex: 'forma_pago' ,sortable: true},
	   {header: 'IMPORTE PAGO', width: 120, dataIndex: 'importe_pago' ,sortable: true,  renderer: BFwrk.Util.rendererMoney},                                      	
	   {header: 'IMPORTE COMPRA', width: 120, dataIndex: 'importe_compra' ,sortable: true, renderer: BFwrk.Util.rendererMoney},
	   {header: 'T_C', width: 80, dataIndex: 'tc' ,sortable: true, renderer: BFwrk.Util.rendererMoney},
	   {header: 'FEC_VTO', width: 100, dataIndex: 'fec_vto' ,sortable: true},
	   {header: 'INSTITUCION', width: 100, dataIndex: 'institucion' ,sortable: true},
	   {header: 'RUBRO_ABONO', width: 120, dataIndex: 'rubro_abono' ,sortable: true},
//	   {header: 'RUBRO_ABONO', width: 120, dataIndex: 'rubro_abono' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right'},
	   {header: 'SUBRUBRO_ABONO', width: 120, dataIndex: 'subrubro_abono' ,sortable: true},
	   {header: 'RUBRO_CARGO' , width: 120 , dataIndex: 'rubro_cargo' , sortable:true},
	   {header: 'SUBRUBRO_CARGO' , width: 120 , dataIndex: 'subrubro_cargo' , sortable:true},	   
	   {header: 'FEC_COMPRA' , width: 100 , dataIndex: 'fec_compra' , sortable:true},
	   {header: 'SPOT' , width: 120 , dataIndex: 'spot' , sortable:true, renderer: BFwrk.Util.rendererMoney},
	   {header: 'PUNTOS_FORWARD' , width: 120 , dataIndex: 'puntos_forward' , sortable:true, renderer: BFwrk.Util.rendererMoney},
	   {header: 'REFERENCIA' , width: 150 , dataIndex: 'referencia' , sortable:true},
	   {header: 'CONCEPTO' , width: 150 , dataIndex: 'concepto' , sortable:true},
	]);
	
	NS.gridCobImportadas = new Ext.grid.GridPanel({
		store: NS.storeLlenarGriDatosImp,
		id: 'gridCobImportadas',
		cm: NS.columnasCobImportadas,
		//width: 1025,
		height: 150,
		x: 0,
		y: 0,
		width: 1000,
		height: 130,
	    stripeRows: true,
	    columnLines: true,
		border: false,

	});
	
	NS.seleccionGrid = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	}); 
	
	NS.columnasDatosExcel = new Ext.grid.ColumnModel([
	   NS.seleccionGrid,	                                        
//	   new Ext.grid.RowNumberer(),                                            
	   {header: 'FOLIO', width: 80, dataIndex: 'folio' ,sortable: true},	
	   {header: 'UNIDAD_DE_NEGOCIO', width: 120, dataIndex: 'unidad_negocio' ,sortable: true},                                        	
	   {header: 'CHEQUERA_CARGO', width: 120, dataIndex: 'chequera_cargo' ,sortable: true},
	   {header: 'CHEQUERA_ABONO', width: 120, dataIndex: 'chequera_abono' ,sortable: true},
	   {header: 'FORMA_PAGO', width: 180, dataIndex: 'forma_pago' ,sortable: true},
	   {header: 'IMPORTE PAGO', width: 120, dataIndex: 'importe_pago' ,sortable: true,  renderer: BFwrk.Util.rendererMoney},                                      	
	   {header: 'IMPORTE COMPRA', width: 120, dataIndex: 'importe_compra' ,sortable: true, renderer: BFwrk.Util.rendererMoney},
	   {header: 'T_C', width: 80, dataIndex: 'tc' ,sortable: true, renderer: BFwrk.Util.rendererMoney},
	   {header: 'FEC_VTO', width: 100, dataIndex: 'fec_vto' ,sortable: true},
	   {header: 'INSTITUCION', width: 100, dataIndex: 'institucion' ,sortable: true},
	   {header: 'RUBRO_ABONO', width: 120, dataIndex: 'rubro_abono' ,sortable: true},
//	   {header: 'RUBRO_ABONO', width: 120, dataIndex: 'rubro_abono' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right'},
	   {header: 'SUBRUBRO_ABONO', width: 120, dataIndex: 'subrubro_abono' ,sortable: true},
	   {header: 'RUBRO_CARGO' , width: 120 , dataIndex: 'rubro_cargo' , sortable:true},
	   {header: 'SUBRUBRO_CARGO' , width: 120 , dataIndex: 'subrubro_cargo' , sortable:true},	   
	   {header: 'FEC_COMPRA' , width: 100 , dataIndex: 'fec_compra' , sortable:true},
	   {header: 'SPOT' , width: 120 , dataIndex: 'spot' , sortable:true, renderer: BFwrk.Util.rendererMoney},
	   {header: 'PUNTOS_FORWARD' , width: 120 , dataIndex: 'puntos_forward' , sortable:true, renderer: BFwrk.Util.rendererMoney},
	   {header: 'REFERENCIA' , width: 100 , dataIndex: 'referencia' , sortable:true},
	   {header: 'CONCEPTO' , width: 100 , dataIndex: 'concepto' , sortable:true},
	   {header: 'OBSERVACIÓN' , width: 300 , dataIndex: 'observacion' , sortable:true}
	]);	
		
	
	
	NS.gridDatosExcel = new Ext.grid.GridPanel({		
		store: NS.storeLlenarGridatosExcel,
		id: PF + 'gridDatosExcel',
		x: 0,
		y: 0,
		
		width: 1000,
		height: 130,
//		border: false,
		stripeRows: true,	
		columnLines: true,
		cm: NS.columnasDatosExcel,
		sm:  NS.seleccionGrid,
	});	
	
	/**********
	 * FUNCIONES
	 */

	NS.opciones=function(op){
		switch (op) {
		
		case 1:
			//OPCIÓN PARA LANZAR LA VENTANA DONDE ABRIRÁ EL EXCEL
			 winImportarF.show();
			 winImportarF.setTitle("Importar Forwards");
			break;

		case 2: 	
			var registroSeleccionado= NS.gridDatosExcel.getSelectionModel().getSelections();
//			alert(registroSeleccionado.length); 
			var datosGrid =NS.storeLlenarGridatosExcel.data.items;
			if(registroSeleccionado.length > 0 ){
				NS.limpiarEtiquetas();
				var observaciones = 0;
				
				var matriz = new Array();
					for(var i = 0; i < registroSeleccionado.length; i ++){
						var vector = {};
						vector.folio = registroSeleccionado[i].get('folio');
						vector.unidad_negocio = registroSeleccionado[i].get('unidad_negocio');
						vector.chequera_cargo = registroSeleccionado[i].get('chequera_cargo');
						vector.chequera_abono = registroSeleccionado[i].get('chequera_abono');
						vector.forma_pago = registroSeleccionado[i].get('forma_pago');
						vector.importe_pago = registroSeleccionado[i].get('importe_pago');
						vector.importe_compra = registroSeleccionado[i].get('importe_compra');
						vector.tc = registroSeleccionado[i].get('tc');
						vector.fec_vto = registroSeleccionado[i].get('fec_vto');
						vector.institucion = registroSeleccionado[i].get('institucion');
						vector.rubro_abono = registroSeleccionado[i].get('rubro_abono');
						vector.subrubro_abono = registroSeleccionado[i].get('subrubro_abono');
						vector.rubro_cargo = registroSeleccionado[i].get('rubro_cargo');
						vector.subrubro_cargo = registroSeleccionado[i].get('subrubro_cargo');				
						vector.fec_compra=registroSeleccionado[i].get('fec_compra');
						vector.spot= registroSeleccionado[i].get('spot');
						vector.puntos_forward= registroSeleccionado[i].get('puntos_forward');
						vector.referencia= registroSeleccionado[i].get('referencia');
						vector.concepto= registroSeleccionado[i].get('concepto');
						vector.observacion = registroSeleccionado[i].get('observacion');
						if(registroSeleccionado[i].get('observacion') != ""){
							observaciones ++;
						}
						matriz[i] = vector;
					}	

					
//				alert(observaciones);
				
				
				if(observaciones > 0){
					 Ext.Msg.alert('SET', 'No se puede realizar la ejecución mientras haya observaciones');
					 return;
				}else{
					  var jSonString = Ext.util.JSON.encode(matriz);
	    				ForwardsAction.insertaForward(jSonString,NS.idUsuario, function(result, e){
	    					 Ext.Msg.alert('SET', result);
	    					
	    					 if (result === "Ejecucion realizada con éxito") {
	    						 NS.gridCobImportadas.store.add(registroSeleccionado);
	    						 NS.storeLlenarGridatosExcel.removeAll();
	    						 NS.gridDatosExcel.getView().refresh();
	    						 NS.lbMovValidos2.show();
	    				       	 NS.lbContMovValidos2.show();
	    				         NS.lbContMovValidos2.setText(registroSeleccionado.length + "");
	    				         Ext.getCmp(PF + 'btnEjecutar').setDisabled(true);
	    					}
	    				});
				}
			}else{
				 Ext.Msg.alert('SET', 'Seleccione filas para ejecutar');
			}	

			break;
		case 3:
			
			 var datosGrid = NS.storeLlenarGridatosExcel.data.items;
			 if(datosGrid.length > 0 ){
				 NS.storeLlenarGridatosExcel.baseParams.json = Ext.util.JSON.encode(NS.obj);
				 NS.mascara.show();
				 NS.storeLlenarGridatosExcel.load();
				 NS.lbMovimientosLeidosExcel.show();
				 NS.lbMovValidos.show();
		       	 NS.lbContMovValidos.show();
		       	Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
			 }else{
				 Ext.Msg.alert('SET', 'No hay datos para validar');
			 }		 
			break;
		case 4:
			var registroSeleccionado= NS.gridDatosExcel.getSelectionModel().getSelections();
			if(registroSeleccionado.length > 0 ){
				NS.gridDatosExcel.store.remove(registroSeleccionado);
//				NS.gridDatosExcel.getView().refresh();
			}else{
				Ext.Msg.alert('SET', 'Seleccione filas para eliminar');
			}	
		break;
		}
	};
	
	NS.limpiarTodo=function(){
//		NS.storeFirmantes.removeAll();
		NS.storeLlenarGridatosExcel.removeAll();
		NS.storeLlenarGriDatosImp.removeAll();
		NS.lbMoviLeidosExcel.hide();
		NS.gridDatosExcel.getView().refresh();
		NS.gridCobImportadas.getView().refresh();
		NS.lbMovimientosLeidosExcel.setText('0');
		NS.lbMovimientosLeidosExcel.hide();
		NS.lbContMovValidos.setText('0');
		NS.lbContMovValidos.hide();
		NS.lbMovValidos.hide();
		NS.lbContMovValidos2.setText('0');
		NS.lbContMovValidos2.hide();
		NS.lbMovValidos2.hide();
		NS.myMask.show();
		NS.contador = 0;
	
	}
	
	NS.cancelar=function(){
		winImportarF.hide();
		winImportarF.hide();		
		NS.limpiarTodo();		 
		 
	}
	
	NS.abrir=function(){
		NS.storeLlenarGriDatosImp.removeAll();
		NS.limpiarEtiquetas();
		NS.lbMovimientosLeidosExcel.show();
		NS.lbMoviLeidosExcel.show();
		if (NS.PanelContenedorArchivo.getForm().isValid() && Ext.getCmp(PF + 'archivoExcel').getValue() != '' ) {

			var strParams = 'nombreArchivoLeer=leerExcelForwards';
			
     	   NS.PanelContenedorArchivo.getForm().submit({
                url: '/SET/jsp/bfrmwrk/gnrators/leerExcel.jsp'+ '?' + strParams,
                waitMsg: 'Leyendo Registros...',
                success: function (fp, o) {
             	   NS.obj = JSON.parse(o.result.json);
             	   NS.storeLlenarGridatosExcel.removeAll();
             	   
             	   NS.gridDatosExcel.getView().refresh();
             	   NS.insertarValor(NS.obj);
             	   var datosGridExcel = NS.storeLlenarGridatosExcel.data.items;


             	   	NS.PanelContenedorArchivo.getForm().reset();
             	   	winImportarF.hide();   
             	   	
					NS.lbMovimientosLeidosExcel.setText((datosGridExcel.length)+'');
                },
                failure: function (fp, o) {
                    Ext.MessageBox.alert('ERROR', 'Ocurrio un error mientras se leeia el archivo');
                }
            });
     		
        }else{
     	   Ext.Msg.alert('SET', 'Seleccione un archivo');
        }
	}
		

	/**********************Generar el excel *********************************/
	 NS.validaDatosExcel = function() {		
		var datosGrid = NS.storeLlenarGriDatosImp.data.items;
//		alert(datosGrid.length);
		//Primero validar si hay datos importados
		if(datosGrid.length == 0) {
			Ext.Msg.alert('SET','No hay registros para el reporte !!');
			return;	
		}
		
		var matriz = new Array();
		for(var i=0;i<datosGrid.length;i++){
			var vector = {};
			vector.folio = datosGrid[i].get('folio');
			vector.unidad_negocio = datosGrid[i].get('unidad_negocio');
			vector.chequera_cargo = datosGrid[i].get('chequera_cargo');
			vector.chequera_abono = datosGrid[i].get('chequera_abono');
			vector.forma_pago = datosGrid[i].get('forma_pago');
			vector.importe_pago = datosGrid[i].get('importe_pago');
			vector.importe_compra = datosGrid[i].get('importe_compra');
			vector.tc = datosGrid[i].get('tc');
			vector.fec_vto = datosGrid[i].get('fec_vto');
			vector.institucion = datosGrid[i].get('institucion');
			vector.rubro_abono = datosGrid[i].get('rubro_abono');
			vector.subrubro_abono = datosGrid[i].get('subrubro_abono');
			vector.rubro_cargo = datosGrid[i].get('rubro_cargo');
			vector.subrubro_cargo = datosGrid[i].get('subrubro_cargo');				
			vector.fec_compra=datosGrid[i].get('fec_compra');
			vector.spot= datosGrid[i].get('spot');
			vector.puntos_forward= datosGrid[i].get('puntos_forward');
			vector.referencia= datosGrid[i].get('referencia');
			vector.concepto= datosGrid[i].get('concepto');
			matriz[i] = vector;
 		
		}
		var jSonString = Ext.util.JSON.encode(matriz);
			
		NS.exportaExcel(jSonString);
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		ForwardsAction.exportaExcel(jsonCadena,'forwardsImportados', function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=forwardsImportados';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
	/****************Fin de generar excel ***********************/

//	function verificaComponentesCreados(){
//		
//		compt = Ext.getCmp(PF + 'lbNoPersona');
//		if(compt != null || compt != undefined ){ compt.destroy(); }
//			
//		compt = Ext.getCmp(PF + 'lbPathFirma');
//		if(compt != null || compt != undefined ){ compt.destroy(); }
//			
//		compt = Ext.getCmp(PF + 'txtNoPersona');
//		if(compt != null || compt != undefined ){ compt.destroy(); }
//			
//		compt = Ext.getCmp(PF + 'cmbPersonas');
//		if(compt != null || compt != undefined ){ compt.destroy(); }
//			
//		compt = Ext.getCmp(PF + 'txtPath');
//		if(compt != null || compt != undefined ){ compt.destroy(); }
//
//	}
	
	/****************
	 *  PANEL'S     *
	 ****************/
	
	NS.panelCobPendientes = new Ext.form.FieldSet ({
		title: 'Forwards pendientes',		
		x: 0,
		y: 25,
		width: 1025,
		height: 220,
		layout: 'absolute',
		buttonAlign:'right',
		items: [
		        NS.gridDatosExcel,		
		        NS.lbMoviLeidosExcel,
	            NS.lbMovimientosLeidosExcel,
	            NS.lbMovValidos,
	            NS.lbContMovValidos,
	           
		],
		buttons:[
		         {
		        	 id: PF + 'btnValidar',
		        	 text:'Validacion',
		        	 handler:function(){
		        		 NS.opciones(3);
		        	 }
		         }, {
		        	 id: PF + 'btnEjecutar',
		        	 disabled: true,
		        	 text:'Ejecutar',
		        	 handler:function(){
		        		 NS.opciones(2);
		        	 }
		         }, {
		        	 id: PF + 'btnEliminar',
		        	 text:'Eliminar',
		        	 handler:function(){
		        		 NS.opciones(4);
		        	 }
		         }
		]
	});
	
	
	NS.panelCobImportadas = new Ext.form.FieldSet ({
		title: 'Forwards Importados',
		x: 0,
		y: 250,
		width: 1025,
		height: 200,
		layout: 'absolute',
		buttonAlign:'right',
		//autoScroll : true,
		items: [
			 	NS.gridCobImportadas,	
			 	NS.lbMovValidos2,
	            NS.lbContMovValidos2,
	
		],
//		buttons:[
//		         {
//			    	 text:'Imprimir',
//			    	 handler:function(){
//			    		 NS.validaDatosExcel();
//			    	 }
//			      },
//				  {
//					 text:'Limpiar',
//					 handler:function(){
//						 NS.opciones(2);
//					 }
//			       },			      
//			      
//		]
	});
	
	 NS.global = new Ext.form.FieldSet ({
			title: '',
			x: 0,
			y: 5,
			width: 1050 ,		
			height: 570,
			layout: 'absolute',
//			autoScroll: true,
			items:
			[
//			 	{
//			 		xtype: 'button',
//			 		text: 'Importar Divisas',
//			 		x: 850,
//			 		y: 0,
//			 		width: 80,
//			 		height: 22,
//			 		listeners: {
//			 			click:{
//			 				fn: function(e){
//			 					// 	NS.buscar();
//			 				}
//			 			}
//			 		}
//			 	},
			 	{
			 		xtype: 'button',
			 		text: 'Importar',
			 		x: 940,
			 		y: 0,
			 		width: 80,
			 		height: 22,
			 		listeners: {
			 			click:{
			 				fn: function(e){
			 					 winImportarF.show();
			 					 winImportarF.setTitle("Importar Forwards");
			 				}
			 			}
			 		}
			 	},
			 	NS.panelCobPendientes,
			 	NS.panelCobImportadas,
			 	{
			 		xtype: 'button',
			 		text: 'Limpiar',
			 		x: 940,
			 		y: 460,
			 		width: 80,
			 		height: 22,
			 		listeners: {
			 			click:{
			 				fn: function(e){
			 					NS.limpiarTodo();
			 				}
			 			}
			 		}
			 	},
//			 	{
//			 		xtype: 'button',
//			 		text: 'Imprimir',
//			 		x: 940,
//			 		y: 460,
//			 		width: 80,
//			 		height: 22,
//			 		listeners: {
//			 			click:{
//			 				fn: function(e){
////			 					NS.validaDatosExcel();
//			 				}
//			 			}
//			 		}
//			 	},
		    ]
	});
	 
	 NS.conceptos = new Ext.FormPanel ({
			title: 'Importacion de Forwards',
//			width: 1300,
//			height: 800,
			frame: true,
			padding: 10,
			autoScroll: true,
			layout: 'absolute',
			id: PF + 'conceptos',
			name: PF + 'conceptos',
			renderTo: NS.tabContId,
			items: [
			 	NS.global
			]
		});
	 
	//Ventana emergente para editar registro o nuevo registro
	 
	 NS.PanelContenedorArchivo = new Ext.FormPanel({
			fileUpload: true,
	        title: 'Archivo Excel',
	        width: 470,
	        height: 80,
	        x: 10,
	        y: 20,
	    	autoScroll: true,
	        renderTo: NS.tabContId,
	        layout: 'absolute',
	        monitorValid: true,
	        
			items: [
			        {
			        	xtype: 'fileuploadfield',
			        	id: PF + 'archivoExcel',
			        	emptyText: 'Archivo',
			        	name: PF + 'archivoExcel',
			        	x: 0,
			        	y:10
			        }
			]
	});
	 
	 var winImportarF = new Ext.Window({
			title: 'Abrir Excel',
			modal: true,
			shadow: true,
			closeAction: 'hide',
			width: 500,
		   	height: 170,
		   	layout: 'absolute',
		   	plain: true,
		   	resizable:false,
		   	draggable:false,
		   	closable:false,
		    bodyStyle: 'padding:0px;',
		    buttonAlign: 'center',
		    buttons:[
		             {text:'Abrir',handler:function(){ NS.abrir();}},
		             {text:'Cancelar',handler:function(){NS.cancelar();} },
		    ],
		   	items: [
		   	        	NS.PanelContenedorArchivo,
		   	],
		     
		});
	 
	 NS.conceptos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());

	
});