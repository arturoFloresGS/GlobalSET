/**
 * Autor: Luis Alfredo Serrato Montes de Oca
 * Fecha: 02/10/2015
 */

Ext.onReady(function(){
	
	var NS = Ext.namespace('apps.SET.Egresos.Cupos.Rentas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.obj;
	NS.contador;
	NS.totalAprovado;
	NS.matrizDatos = new Array();
	
	NS.mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Validando ..."});
	
	/*****************
	 * Etiquetas nuevas
	 */
	
	NS.limpiarEtiquetas= function(){
		NS.lbImporteTotalMNExcel.setText('$ 0.00 MN');
		NS.lbImporteTotalDLSExcel.setText('0.00 Otras divisas');
		NS.lbMovimientosLeidosExcel.setText('0');
		
		NS.lbImporteTotalMN.setText('$ 0.00 MN');
		NS.lbImporteTotalDLS.setText('0.00 Otras divisas');
		NS.lbMovimientosLeidos.setText('0');
		NS.lbContMovValidos.setText('0');
		Ext.getCmp(PF + 'btnExcel').hide();
		
		NS.lbImporteTotalMNExcel.hide();
		NS.lbImporteTotalDLSExcel.hide();
		NS.lbMovimientosLeidosExcel.hide();
		
		NS.lbImporteTotalMN.hide();
		NS.lbImporteTotalDLS.hide();
		NS.lbMovimientosLeidos.hide();
		NS.lbContMovValidos.hide();
		
		NS.lbMoviLeidosExcel.hide();
		NS.lbImporteTotalExcel.hide();
		NS.lbMoviLeidos.hide();
		NS.lbMovValidos.hide();
		NS.lbImporteTotal.hide();
		
	}
	
	NS.lbMoviLeidosExcel = new Ext.form.Label({
		text: 'Movimientos leidos: ',
		x: 250,
		y : 40	,
		renderTo: Ext.getBody(),
		hidden:true,
		labelStyle: 'font-weight:bold;'
	});
	
	NS.lbMovimientosLeidosExcel = new Ext.form.Label({
		text: '0',
		x: 350,
		y : 40	,
		hidden:true,
	});
	
	NS.lbImporteTotalExcel = new Ext.form.Label({
		text: 'Importe total: ',
		x: 400,
		y : 40,
		hidden:true,
	});
	
	NS.lbImporteTotalMNExcel = new Ext.form.Label({
		text: '$0.00 MN',
		x: 500,
		y : 40,
		hidden:true,
	});
	
	NS.lbImporteTotalDLSExcel = new Ext.form.Label({
		text: '0.00 Otras divisas',
		x: 620,
		y : 40,
		hidden:true,
	});
	
 //------------------------
	NS.lbMoviLeidos = new Ext.form.Label({
		text: 'Movimientos leidos: ',
		x: 50,
		y : 330,
		hidden:true,
	});
	
	NS.lbMovimientosLeidos = new Ext.form.Label({
		text: '0',
		x: 150,
		y : 330,
		hidden:true,
	});
	
	NS.lbMovValidos = new Ext.form.Label({
		text: 'Movimientos validos: ',
		x: 220,
		y : 330,
		hidden:true,
	});
	
	NS.lbContMovValidos = new Ext.form.Label({
		text: '0',
		x: 330,
		y : 330,
		hidden:true,
	});
	
	NS.lbImporteTotal = new Ext.form.Label({
		text: 'Importe total: ',
		x: 400,
		y : 330,
		hidden:true,
	});
	
	NS.lbImporteTotalMN = new Ext.form.Label({
		text: '$0.00 MN',
		x: 500,
		y : 330,
		hidden:true,
	});
	
	NS.lbImporteTotalDLS = new Ext.form.Label({
		text: '0.00 Otras divisas',
		x: 620,
		y : 330,	
		hidden:true,
	});
		
	/************************************************************************************************************/
	/******************************************CREACION DE FUNCIONES*********************************************/
	/************************************************************************************************************/
	
	NS.insertarValor = function(renglones){
		NS.datosClase = NS.gridDatosExcel.getStore().recordType;
		NS.contador = 0;
		NS.totalAprovado = 0.00;
		NS.totalAprovadoDls = 0.00;
		for(i=0; i<renglones.length; i++){
			var datos = new NS.datosClase({
				ccid: renglones[i].ccid,
				sociedad: renglones[i].sociedad,
				numeroAcredor: renglones[i].numeroAcredor,
				nombreAcredor: renglones[i].nombreAcredor,
				docSap: renglones[i].docSap,
				viaPago: renglones[i].viaPago,
				banco: renglones[i].banco, 
				cuenta: renglones[i].cuenta,
			 	moneda: renglones[i].moneda,
			 	fechaContabilizacion: renglones[i].fechaContabilizacion + '',
			 	total: renglones[i].total,
			 	observacion: renglones[i].observacion,
			 	referencia: renglones[i].referencia
        	});

			NS.gridDatosExcel.stopEditing();
			NS.storeLlenarGridatosExcel.insert(i, datos);
			NS.gridDatosExcel.getView().refresh();
		}		
	};
	
	/********************** Generar el excel *********************************/
	
	 NS.validaDatosExcel = function() {		
		var registroSeleccionado = NS.storeLlenarGridatosExcel.data.items;
		if(registroSeleccionado.length == 0) {
			Ext.Msg.alert('SET','No hay registros para el reporte !!');
		    return;
		}
		
		var matriz = new Array();	
		for(var i=0;i<registroSeleccionado.length;i++){
			var vector = {};
			vector.ccid=registroSeleccionado[i].get('ccid');
			vector.sociedad=registroSeleccionado[i].get('sociedad');
			vector.numeroAcredor=registroSeleccionado[i].get('numeroAcredor');
			vector.nombreAcredor=registroSeleccionado[i].get('nombreAcredor');
			
			vector.docSap=registroSeleccionado[i].get('docSap');
			vector.viaPago=registroSeleccionado[i].get('viaPago');
			vector.banco=registroSeleccionado[i].get('banco');
			vector.cuenta=registroSeleccionado[i].get('cuenta');
			
			vector.moneda=registroSeleccionado[i].get('moneda');
			vector.fechaContabilizacion=registroSeleccionado[i].get('fechaContabilizacion');
			vector.total=registroSeleccionado[i].get('total');
			vector.observacion=registroSeleccionado[i].get('observacion');
			vector.referencia=registroSeleccionado[i].get('referencia');
			matriz[i] = vector;
		}
		var jSonString = Ext.util.JSON.encode(matriz);
		NS.exportaExcel(jSonString);
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		RentasAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=excelRentas';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
	
	/****************Fin de generar excel ***********************/
	
	NS.cambiarFormato = function(num,prefix){
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
	
/************************************************************************************************************/
/******************************************CREACION DE GRIDS*******************************************/
/************************************************************************************************************/
	
	
	NS.storeLlenarGridatosExcel = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {json: ''},
		paramOrder: ['json'],
		directFn: RentasAction.validarDatosExcel,
		fields:
		[		    
		    {name: 'ccid'},
		 	{name: 'sociedad'},
		 	{name: 'numeroAcredor'},
		 	{name: 'nombreAcredor'},
		 	{name: 'docSap'},
		 	{name: 'viaPago'},
		 	{name: 'banco'},
		 	{name: 'cuenta'},
		 	{name: 'moneda'},
		 	//{name: 'fechaContabilizacion' , type: 'date', dateFormat: 'd/m/Y'},
		 	{name: 'fechaContabilizacion'},
		 	{name: 'total'},
		 	{name: 'observacion'},
		 	{name: 'noFolioDet'},
		 	{name: 'bandera'},
		 	{name: 'referencia'}
		],
		listeners:
		{
			load: function(s, records){
				
				
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridatosExcel, msg: "Cargando Datos ..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay datos que mostrar');
				}else{
					Ext.getCmp(PF + 'btnExcel').show();
					NS.contador = 0;
					NS.totalAprovado = 0.00;
					NS.totalAprovadoDls = 0.00;
					NS.datos = NS.storeLlenarGridatosExcel.data.items;
					
					var valid =0;
					var invalid=0;
					
					for(var i = 0; i<NS.datos.length; i++ ){
						if(NS.datos[i].get('observacion') == ''){
							valid++;
							NS.gridDatosExcel.getView().getRow(i).style.backgroundColor  = '#4BD844'; //verde
							if(NS.datos[i].get('moneda') == 'MN'){
								NS.totalAprovado = NS.totalAprovado + NS.datos[i].get('total');
							}else{
								NS.totalAprovadoDls = NS.totalAprovadoDls + NS.datos[i].get('total');
							}
							NS.contador ++;
						}else{
							invalid++;
							NS.gridDatosExcel.getView().getRow(i).style.backgroundColor  = '#FF8536'; //naranja
						}
						
						
						
					}
					//NS.lbImporteTotalMN.setText('$ ' +NS.totalAprovado+ ' MN');
					//NS.lbImporteTotalDLS.setText(''+NS.totalAprovadoDls + ' Otras divisas');
					NS.lbImporteTotalMN.setText('' +NS.cambiarFormato(NS.totalAprovado.toFixed(2), '$')+ ' MN');
					NS.lbImporteTotalDLS.setText(''+NS.cambiarFormato(NS.totalAprovadoDls.toFixed(2), ' ') + ' Otras divisas');
					
					NS.lbMovimientosLeidos.setText((valid+invalid)+'');
					NS.lbContMovValidos.setText(valid+'');
						
					
					if(NS.contador == NS.datos.length){
						Ext.getCmp(PF + 'btnPropuesta').setDisabled(false);
					}
					
				}
				NS.mascara.hide();
					
			}
		}

	});
	
	
	NS.seleccionGrid = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	}); 
	
	
	
	NS.columnasDatosExcel = new Ext.grid.ColumnModel([
	   new Ext.grid.RowNumberer(),                                            
	   {header: 'CCID', width: 100, dataIndex: 'ccid' ,sortable: true},	
	   {header: 'SOCIEDAD', width: 100, dataIndex: 'sociedad' ,sortable: true},                                        	
	   {header: 'NUMERO ACREEDOR', width: 120, dataIndex: 'numeroAcredor' ,sortable: true},
	   {header: 'NOMBRE ACREEDOR', width: 270, dataIndex: 'nombreAcredor' ,sortable: true},
	   {header: 'DOC SAP', width: 100, dataIndex: 'docSap' ,sortable: true},
	   {header: 'VIA DE PAGO', width: 150, dataIndex: 'viaPago' ,sortable: true},                                      	
	   {header: 'BANCO', width: 120, dataIndex: 'banco' ,sortable: true},
	   {header: 'CUENTA', width: 180, dataIndex: 'cuenta' ,sortable: true},
	   {header: 'MONEDA', width: 100, dataIndex: 'moneda' ,sortable: true},
	   /*{header: 'FECHA CONTABILIZACION', width: 150, dataIndex: 'fechaContabilizacion' ,sortable: true , xtype:'datecolumn', format: 'd/m/y',
		   editor:{
			   xtype: 'datefield',
			   format: 'd/m/Y',
			   submitFormat: 'c'
		   }
	    },*/
	   {header: 'FECHA CONTABILIZACION', width: 100, dataIndex: 'fechaContabilizacion' ,sortable: true},
	   {header: 'TOTAL', width: 180, dataIndex: 'total' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right'},
	   {header: 'OBSERVACION', width: 170, dataIndex: 'observacion' ,sortable: true},
	   {header: 'REFERENCIA' , width: 170 , dataIndex: 'referencia' , sortable:true}
	]);
	
    
	NS.gridDatosExcel = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridatosExcel,
		id: PF + 'gridDatosExcel',
		x: 0,
		y: 0,
		cm: NS.columnasDatosExcel,
		//sm: NS.seleccionGrid,
		width: 960,
		height: 320,
		border: false,
		stripeRows: true,
		listeners:{
			rowClick:{
				fn:function(grid){
						
				}
			}
		}
      
	});
	
	
/************************************************************************************************************/
/******************************************CREACION DE PANELES*******************************************/
/************************************************************************************************************/
	

		
	NS.PanelContenedorDatosExcel = new Ext.form.FieldSet({
		title: 'DATOS EXCEL',
		id: PF + 'PanelContenedorDatosExcel',
		layout: 'absolute',
		width: 1000,
		height: 420,
		x: 0,
		y: 130,
		items:[
		      NS.gridDatosExcel,
		      NS.lbMoviLeidos,
		  	  NS.lbImporteTotal,
		  	  NS.lbMovValidos,
		  	  NS.lbImporteTotalMN,
			  NS.lbImporteTotalDLS,
			  NS.lbMovimientosLeidos,
			  NS.lbContMovValidos,
		       ],
		buttons:[
		         {
		        	 //id: PF + 'btnMostrarDatos',
		        	 text: 'Validacion', 
		        	 x: 700,
		        	 y: 345,
		        	 width: 80,
		        	 listeners:{
		        		 click:{
		        			 fn:function(e){
		        				 var datosGrid = NS.storeLlenarGridatosExcel.data.items;
		        				 if(datosGrid.length > 0 ){
		        					 NS.storeLlenarGridatosExcel.baseParams.json = Ext.util.JSON.encode(NS.obj);
		        					 NS.mascara.show();
		        					 NS.storeLlenarGridatosExcel.load();
		        					 //NS.lbImporteTotalMNExcel.show();
			        		       	  //NS.lbImporteTotalDLSExcel.show();
			        		       		//NS.lbMovimientosLeidosExcel.show();
			        		       		
			        		       		NS.lbImporteTotalMN.show();
			        		       		NS.lbImporteTotalDLS.show();
			        		       		NS.lbMovimientosLeidos.show();
			        		       		NS.lbContMovValidos.show();
			        		       		
			        		       		//NS.lbMoviLeidosExcel.show();
			        		       		//NS.lbImporteTotalExcel.show();
			        		       		NS.lbMoviLeidos.show();
			        		       		NS.lbMovValidos.show();
			        		       		NS.lbImporteTotal.show();
		        				 }else{
		        					 Ext.Msg.alert('SET', 'No hay datos para validar');
		        				 }
		        			 }
		        		 }
		        	 }
		         },
		         {
		        	 id: PF + 'btnPropuesta',
		        	 text: 'Propuesta', 
		        	 x: 790,
		        	 y: 345,
		        	 width: 80,
		        	 disabled: true,
		        	 listeners:{
		        		 click:{
		        			 fn:function(e){
		        				 
		        				 if(NS.contador == NS.storeLlenarGridatosExcel.data.items.length){  					 
		        					var datosGrid =NS.storeLlenarGridatosExcel.data.items;
		        					 var matriz = new Array();
		        						
		        						for(var i = 0; i < datosGrid.length; i ++){
		        							var vector = {};
		        							vector.ccid = datosGrid[i].get('ccid');
		        							vector.sociedad = datosGrid[i].get('sociedad');
		        							vector.numeroAcredor = datosGrid[i].get('numeroAcredor');
		        							vector.nombreAcredor = datosGrid[i].get('nombreAcredor');
		        							vector.docSap = datosGrid[i].get('docSap');
		        							vector.viaPago = datosGrid[i].get('viaPago');
		        							vector.banco = datosGrid[i].get('banco');
		        							vector.cuenta = datosGrid[i].get('cuenta');
		        							vector.moneda = datosGrid[i].get('moneda');
		        							vector.fechaContabilizacion = datosGrid[i].get('fechaContabilizacion');
		        							vector.total = datosGrid[i].get('total');
		        							vector.observacion = datosGrid[i].get('observacion');
		        							vector.noFolioDet = datosGrid[i].get('noFolioDet');
		        							vector.referencia=datosGrid[i].get('referencia');
		        							vector.bandera= datosGrid[i].get('bandera');
		        							matriz[i]=vector;
		        						}
		        						
		        						var jSonString = Ext.util.JSON.encode(matriz);
		        						
		        					 //var jSonString = Ext.util.JSON.encode(NS.obj);
		        					 RentasAction.crearPropuesta(jSonString, NS.totalAprovado, NS.totalAprovadoDls, NS.storeLlenarGridatosExcel.data.items[0].get('sociedad'), function(result, e){
		        						 Ext.Msg.alert('SET', result);
		        						 NS.storeLlenarGridatosExcel.removeAll();
		        						 NS.gridDatosExcel.getView().refresh();
		        					 });
		        					 Ext.getCmp(PF + 'btnPropuesta').setDisabled(true);
			        				 
		        				 }else{
		        					 Ext.Msg.alert('SET', 'No se puede realizar una propuesta mientras haya observaciones');
		        				 }
		        			 }
		        		 }
		        	 }
		         },
		         {id: PF+ 'btnExcel', hidden:true,text:'Excel',handler:function(){NS.validaDatosExcel();}},
		         {
		        	 //id: PF + 'btnMostrarDatos',
		        	 text: 'Cerrar', 
		        	 x: 880,
		        	 y: 345,
		        	 width: 80,
		        	 listeners:{
		        		 click:{
		        			 fn:function(e){
		        				 NS.storeLlenarGridatosExcel.removeAll();
		        				 NS.gridDatosExcel.getView().refresh();
		        				 Ext.getCmp(PF + 'btnPropuesta').setDisabled(true);
		        				 NS.limpiarEtiquetas();
		        			 }
		        		 }
		        	 }
		         }
		    ]
	});
	
	
	NS.PanelContenedorArchivo = new Ext.FormPanel({
		fileUpload: true,
        title: 'Archivo Excel',
        width: 990,
        height: 120,
        x:0,
        y:10,
    	autoScroll: true,
        renderTo: NS.tabContId,
        layout: 'absolute',
        monitorValid: true,
        items:[
               NS.lbMoviLeidosExcel,
               NS.lbMovimientosLeidosExcel,
               NS.lbImporteTotalExcel,
               NS.lbImporteTotalMNExcel,
           	   NS.lbImporteTotalDLSExcel,
			{
			    xtype: 'fileuploadfield',
			    id: PF + 'archivoExcel',
			    emptyText: 'Archivo',
			    name: PF + 'archivoExcel',
			    x: 0,
			    y:10
			}
       ],
       
       buttons: [{
           text: 'Abrir',
           handler: function () {
        	   NS.limpiarEtiquetas();
        	   Ext.getCmp(PF + 'btnPropuesta').setDisabled(true);
        	   NS.lbImporteTotalMNExcel.show();
       		   NS.lbImporteTotalDLSExcel.show();
       		   NS.lbMovimientosLeidosExcel.show();
       		
       	      NS.lbMoviLeidosExcel.show();
       		  NS.lbImporteTotalExcel.show();
       		  var contadorMovimientos=0;
               if (NS.PanelContenedorArchivo.getForm().isValid() && Ext.getCmp(PF + 'archivoExcel').getValue() != '' ) {
            	   var strParams = 'nombreArchivoLeer=leerExcelRentas';
            	   NS.PanelContenedorArchivo.getForm().submit({
                       url: '/SET/jsp/bfrmwrk/gnrators/leerExcel.jsp?'+strParams,
                       waitMsg: 'Leyendo Registros...',
                       success: function (fp, o) {
                    	   NS.obj = JSON.parse(o.result.json)
                    	   NS.storeLlenarGridatosExcel.removeAll();
                    	   NS.gridDatosExcel.getView().refresh();
                    	   NS.insertarValor(NS.obj);
                    	   var datosGridExcel =NS.storeLlenarGridatosExcel.data.items;
                    	   var totalMN=0.0;
                    	   var totalOtras=0.0;
                    	   
                    	   for(var i = 0; i< datosGridExcel.length; i++ ){
                    		   var total=datosGridExcel[i].get('total').replace('$', '');
                    		       total=total.replace(',', '');
                    		   
       							if(datosGridExcel[i].get('moneda') == 'MN'){
       								totalMN = totalMN + parseFloat(total);
       							}else{
       								totalOtras= totalOtras + parseFloat(total);
       							}	
       					   }
       					    //NS.lbImporteTotalMNExcel.setText('$ ' +totalMN+ ' MN');
                    	   NS.lbImporteTotalMNExcel.setText('' +NS.cambiarFormato(totalMN.toFixed(2), '$')+ ' MN');
                    	   NS.lbImporteTotalDLSExcel.setText(''+NS.cambiarFormato(totalOtras.toFixed(2), ' ') + ' Otras divisas');
       					   // NS.lbImporteTotalDLSExcel.setText(''+totalOtras + ' Otras divisas');
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
       },
       {
           text: 'Cancelar',
           handler: function () {
        	   NS.PanelContenedorArchivo.getForm().reset();
        	   Ext.getCmp(PF + 'btnPropuesta').setDisabled(true);
        	   NS.limpiarEtiquetas();
           }
       }]
	});
	
	NS.PanelContenedorGeneral = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorGeneral',
		layout: 'absolute',
		width: 1030,
		height: 580,
		x: 0,
		y: 0,
		items: [
		        	NS.PanelContenedorArchivo,
		        	NS.PanelContenedorDatosExcel
		        ]
	});
	
	NS.Rentas = new Ext.form.FormPanel({
		title: 'RENTAS',
		width: 1010,
		height: 800,
		frame: true,	
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'Rentas',
		name: PF + 'Rentas',
		renderTo: NS.tabContId,
		items: [
		        	NS.PanelContenedorGeneral
		        ]
	});
	
	
	NS.Rentas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
	
});