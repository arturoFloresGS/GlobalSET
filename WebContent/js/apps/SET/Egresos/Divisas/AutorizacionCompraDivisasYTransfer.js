Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Egresos.Divisas.AutorizacionCompraDivisasYTransfer');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.letra = "S";
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.datos = false;  
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.noClaveControl = 0;
	NS.idDivisa = 0;
	NS.Grupo = 0;
	
	NS.foliosCVDGlobal = '';	
	
	NS.unformatNumber = function(num) {
		return num.replace(/(,)/g,''); 
	};
	
	
	NS.hide_messagee=function() {
	    Ext.defer(function() {
	    	Ext.MessageBox.hide();
	    }, 180000);
	}
	
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
	
	//ContenedorPanelDivisas--------------------------------------------------------INIT
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
	
	//Ventana para autorizar divisas
	var winLogin = new Ext.Window({
		title: 'Confirmación de contraseña'
		,modal:true
		,shadow:true
		,width: 200
	   	,height:110
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,closable: false
	   	,resizable: false
	   	,draggable: false
	   	,items: [
	   	
	   		{
	       		inputType: 'password',
				xtype: 'textfield',
	       		id:PF+'txtPsw',
	       		name:PF+'txtPsw',
	       		x:20,
	       		y:10,
	       		width:100,
	       		hidden:false	       		
	   		}
		]
        
	   	,buttons: [
		   	{
			 	text: 'Aceptar',
				handler: function(e){
					
					var credencial = Ext.getCmp(PF+'txtPsw').getValue();
					
					if( credencial == '' ){
						Ext.Msg.show({ title:'SET', icon:Ext.Msg.WARNING, msg:'No ha capturado la contraseña.', buttons:Ext.Msg.OK });
						return;	
					}
					
					if( NS.foliosCVDGlobal == '' ){
						Ext.Msg.show({ title:'SET', icon:Ext.Msg.WARNING, msg:'No ha seleccionado ningun registro.', buttons:Ext.Msg.OK });
						return;
					} 
        	   		
					mascara.show();
        	   		
        	   		GestionDeOperacionesDivisasAction.autorizaOperacionesDivisas(credencial, NS.idUsuario, NS.foliosCVDGlobal, function(response, e){
        	   			
        	   			NS.storeMovimientos.load();
        	   			
        	   			var icon = Ext.Msg.INFO; 
        				
        				if( ! response.resultado ){
        					icon = Ext.Msg.WARNING;
        				}
        				
        				Ext.Msg.show({ title:'SET', icon:icon, msg:response.mensaje, buttons:Ext.Msg.OK });
        										
						mascara.hide();						
						winLogin.hide();
        	   			
    		    	});
        	   		
        	   		 estatusSeleccionado = "NINGUNO";
        	   		 NS.foliosCVDGlobal = '';
        	   		 
						
					
				 	
				 }
		     },{
			 	text: 'Cancelar',
				handler: function(e) { 
				 	winLogin.hide();
				}
		     }
	 	]
	});
	
 	NS.storeMovimientos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder: ['noEmpresa', 'grupo', 'noClaveControl', 'idDivisa'],
		directFn:GestionDeOperacionesDivisasAction.traerOperacionesCompraVentaDeDivisasParaPago, 
		fields: [
			{name: 'nomEmpresa'},
			{name: 'descDivisaVenta'},
			{name: 'idBancoCargo'},
			{name: 'chequeraCargo'},			
			{name: 'descBancoCargo'},
			{name: 'chequeraCargo'},
			{name: 'importeVenta'},
			{name: 'descDivisaCompra'},
			{name: 'descBancoAbono'},
			{name: 'chequeraAbono'},
			{name: 'tipoDeCambio'},
			{name: 'importeCompra'},
			{name: 'nomCasaDeCambio'}, 
			{name: 'estatusAutorizacion'},
			{name: 'folio'}
		],
		autoLoad:true, 
		listeners: {
			load: function(s, records){				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientos, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.getCmp(PF + 'txtRegistrosSeleccionados').setValue(0);
					Ext.getCmp(PF + 'txtTotal').setValue("0.00"); 
				}
					   		
			}
		}
	}); 
 	
 	NS.seleccionMovimientos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});     
 	   
 	NS.columnasMovimientos = new Ext.grid.ColumnModel([
 	                                                   
	    NS.seleccionMovimientos,
	    {header: 'Empresa'			, width: 180, dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'Autorizacion'		, width: 180, dataIndex: 'estatusAutorizacion', sortable: true, hidden:true,
	    	renderer:function(value){ return value=='P'?'PENDIENTE':'AUTORIZADA';}
	    },
	    {header: 'Divisa Cargo'		, width: 120, dataIndex: 'descDivisaVenta', sortable: true},
	    {header: 'Banco Cargo'		, width: 120, dataIndex: 'descBancoCargo', sortable: true},
	    {header: 'Chequera Cargo'	, width: 120, dataIndex: 'chequeraCargo', sortable: true},
	    {header: 'Importe Venta'	, width: 120, dataIndex: 'importeVenta', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Divisa Abono'		, width: 120, dataIndex: 'descDivisaCompra', sortable: true},
	    {header: 'Banco Abono'		, width: 120, dataIndex: 'descBancoAbono', sortable: true},
	    {header: 'Chequera Abono'	, width: 120, dataIndex: 'chequeraAbono', sortable: true},
	    {header: 'Tipo Cambio'		, width: 120, dataIndex: 'tipoDeCambio', sortable: true},
	    {header: 'Casa de Cambio'	, width: 120, dataIndex: 'nomCasaDeCambio', sortable: true},
	    {header: 'Importe Compra'	, width: 120, dataIndex: 'importeCompra', sortable: true, renderer: BFwrk.Util.rendererMoney}
	   
	]);         
 	
	NS.gridMovimientos = new Ext.grid.GridPanel({
		store: NS.storeMovimientos,
		id: 'gridMovimientos',		
		cm: NS.columnasMovimientos,
		sm: NS.seleccionMovimientos,		
		height: 200,
		stripeRows: true,
		columnLines: true,
		title: 'Compra-Venta de Divisas',
		tools:[{
		    id:'refresh',
		    qtip: 'Refrescar Información',		   
		    handler: function(event, toolEl, panel){
		    	NS.gridMovimientos.getSelectionModel().clearSelections();		    	
		    	var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();				
				Ext.getCmp(PF + 'txtRegistrosSeleccionados').setValue(regSelec.length);
        		NS.MuestraDatos();        		
		    	NS.storeMovimientos.load();
		    }
		}],
		listeners: {
			rowclick: {
				fn:function(grid, i, e) {
					      
					var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
					Ext.getCmp(PF + 'txtRegistrosSeleccionados').setValue(regSelec.length);					
					NS.MuestraDatos();
					NS.SeleccionaSoloEstatus(i);
				}
			},
			click: {
				fn:function(grid) {
					      
					var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
					
					Ext.getCmp(PF + 'txtRegistrosSeleccionados').setValue(regSelec.length);
	        				NS.MuestraDatos();
	  
	               }
	        }
	   }			
		
	}); 
	
	var estatusSeleccionado = "NINGUNO";
	
	NS.SeleccionaSoloEstatus=function(i){
		var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
 		 		
 		if(regSelec.length>0){
 			
 			for(var k=0;k<regSelec.length;k=k+1){
 				
	 			 if( estatusSeleccionado == "NINGUNO" ){
	 				 
	 				estatusSeleccionado = regSelec[k].get('estatusAutorizacion')
	 				
	 			 }else{
	 				 
	 				if(estatusSeleccionado != regSelec[k].get('estatusAutorizacion') ){
	 					NS.gridMovimientos.getSelectionModel().deselectRow(i);
	 					Ext.Msg.alert('SET', 'Debe Seleccionar registros del mismo estatus.');
    	   				return;	
	 				}
	 			 }
 				
			 			
				
			}
 		
 		}else{

 			 estatusSeleccionado = "NINGUNO";

 		}
		
	};
 	                                    	
 	                                    	
 	NS.MuestraDatos = function(){
 		
 		var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
 		
 		
 		if(regSelec.length>0){
 			var totalImporte=0;
 			
 			for(var k=0;k<regSelec.length;k=k+1){
 				
				totalImporte = totalImporte + regSelec[k].get('importeVenta');
				Ext.getCmp(PF + 'txtTotal').setValue(totalImporte.toFixed(4));
				var Total = Ext.getCmp(PF + 'txtTotal').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtTotal').getValue()));
				
			}
 		
 		}else{

			Ext.getCmp(PF + 'txtTotal').setValue("0.0000");

 		}
 	};
 	                                    	
 	
 	NS.labRegistrosSeleccionados = new Ext.form.Label({
 	     text: 'Registros Seleccionados',
 	     x: 260,
 	     y: 215
 	});
 	                                    	
 	NS.txtRegistrosSeleccionados = new Ext.form.TextField({
 	     id: PF + 'txtRegistrosSeleccionados',
 	     name:PF + 'txtRegistrosSeleccionados',
 	     x: 410,
 	     y: 212,
 	     width: 180, 
 	     tabIndex: 0 
 	});
 	
 	NS.labTotal = new Ext.form.Label({
	     text: 'Total',
	     x: 660,
	     y: 215
	});  
	                                    	
	NS.txtTotal = new Ext.form.TextField({
	     id: PF + 'txtTotal',
	     name:PF + 'txtTotal',
	     x: 695, 
	     y: 212, 
	     width: 180, 
	     tabIndex: 0,
	     listeners: {
				change: {
					fn: function(caja, valor) {
						if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
							var Total = Ext.getCmp(PF + 'txtTotal').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtTotal').getValue()));
							
						}
					}
				}
			}
	     
	});
 	
 	NS.ContenedorPanelDivisas = new Ext.form.FieldSet({   
		layout: 'absolute',
		title: 'Movimientos',
	    height: 310,
		margin:10,		
	    items:[
	           NS.gridMovimientos,
	           NS.txtRegistrosSeleccionados,
	           NS.labRegistrosSeleccionados,
	           NS.labTotal,
	           NS.txtTotal	,
	           apps.SET.btnFacultativo(new Ext.Button(
		        {
		           id:'btnAutorizarDivisas', name:'btnAutorizarDivisas', text: 'Autorizar',  x: 1500, y: 250,  width: 80, height: 22, hidden:true,
				   listeners:{ click:{ fn:function(e){ 
					   
					   Ext.getCmp(PF+'txtPsw').setValue('');
					   
					   var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
        	   			
	        	   		if( regSelec.length == 0 ){
        	   				Ext.Msg.alert('SET', 'No ha seleccionado ningun registro.');
        	   				return;
        	   			}
	        	   		
	        	   		var folios = ''; 
	        	   		
	        	   		Ext.each( regSelec, function(registro, indice, array){
	        	   			
	        	   			if( registro.get('estatusAutorizacion')=== 'P' ){
	        	   				
	        	   				folios = folios + registro.get('folio') + ",";
	        	   				
	        	   			}
	        	   			
	        	   		});
	        	   		
	        	   		if( folios === '' ){
	        	   			Ext.Msg.alert('SET', 'No ha seleccionado ningun registro pendiente de Autorizar.');
        	   				return;
	        	   		}
	        	   		
	        	   		NS.foliosCVDGlobal = folios;
	        	   		
	        	   		winLogin.show();
	        	   		
				   
	            } } }
				})),
				apps.SET.btnFacultativo(new Ext.Button({ 
				 id:'btnEjecutarDivisas', name:'btnEjecutarDivisas',text: 'Ejecutar',  x: 800, y: 250, width: 80, height: 22,
				   listeners:{ click:{ fn:function(e){
					   
					   e.setVisible(false);
					   
						var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
						var folios = '';
	    	   			
	        	   		if( regSelec.length == 0 ){
	    	   				Ext.Msg.alert('SET', 'No ha seleccionado ningun registro.');
	    	   				e.setVisible(true);
	    	   				return;
	    	   			} 
	        	   			
        	   			Ext.each( regSelec, function(registro, indice, array){
	        	   			
	        	   			if( registro.get('estatusAutorizacion')=== 'A' ){
	        	   				
	        	   				folios = folios + registro.get('folio') + ",";
	        	   				
	        	   			}else{
	        	   				NS.gridMovimientos.getSelectionModel().deselectRow(indice);
	        	   			}
	        	   			
	        	   		});
        	   			
        	   			if( folios == ''){
        	   				Ext.Msg.alert('SET', 'No ha seleccionado ningun registro Autorizado.');
        	   				e.setVisible(true);
	    	   				return;
        	   			}
        	   			
        	   			regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
        	   			
        	   			var registros = new Array ();
	        	   			        	   	
	        	   		for( var i = 0; i < regSelec.length; i++ ){
	        	   			
	        	   			var registro={};
	        	   			
	        	   			registro.folio           = regSelec[i].get('folio'); 
	        	   			registro.bandera         = 'N';
	        	   			registro.noUsuario       = NS.idUsuario;
	        	   			registro.idBancoCargo    = regSelec[i].get('idBancoCargo');
	        	   			registro.chequeraCargo   = regSelec[i].get('chequeraCargo');
	        	   			
	        	   			registros[i] = registro;
	        	   			
	        	   		}
	        	   		
	        	   		var jsonRegistros = Ext.util.JSON.encode(registros);
	        	   			        	   		
	        	   		GestionDeOperacionesDivisasAction.pagarOperacionesDivisas( jsonRegistros, function(response, f){
	        	   			
	        	   			
	        	   			
	        	   			NS.storeMovimientos.load();
	        	   			
	        	   			var icon = Ext.Msg.INFO; 
	        				
	        				if( ! response.resultado ){
	        					icon = Ext.Msg.WARNING;
	        				}
	        				
	        				Ext.Msg.show({ title:'SET', icon:icon, msg:response.mensaje, buttons:Ext.Msg.OK });
	        				
		        	   		var strParams = ''; 
			    			strParams = '?nomReporte=ReporteAutCompraVentaDeDivisas&foliosReporte=' + folios ;
			    			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
			    			
			    			
			    			if(e){
	        	   				e.setVisible(true);
	        	   			}
			    			
	        	   			
        		    	});
	        	   		

	        	   		
		    			
				   
					} } }
				} ) ) ,
				
		           apps.SET.btnFacultativo(new Ext.Button(
			        {
			        	id:'btnEliminarDivisas', name:'btnEliminarDivisas', text: 'Eliminar',  x: 700, y: 250,  width: 80, height: 22,
					   listeners:{ click:{ fn:function(e){ 

		        	   		var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
	        	   			
		        	   		if( regSelec.length == 0 ){
	        	   				Ext.Msg.alert('SET', 'No ha seleccionado ningun registro.');
	        	   				return;
	        	   			}
		        	   		
		        	   		var folios = ''; 
		        	   		
		        	   		Ext.each( regSelec, function(registro, indice, array){
		        	   				
		        	   			folios = folios + registro.get('folio') + ",";
		        	   				
		        	   		});
		        	   		
		        	   		GestionDeOperacionesDivisasAction.eliminarOperacionesDivisas(NS.idUsuario, folios, function(response, e){
		        	   			
		        	   			NS.storeMovimientos.load();
		        	   			
		        	   			var icon = Ext.Msg.INFO; 
		        				
		        				if( ! response.resultado ){
		        					icon = Ext.Msg.WARNING;
		        				}
		        				
		        				Ext.Msg.show({ title:'SET', icon:icon, msg:response.mensaje, buttons:Ext.Msg.OK });
		        	   			
	        		    	});
		        	   		
		        	   		 estatusSeleccionado = "NINGUNO";
					   
		            } } }
					}))
	           ]
	           
 	});
 	
	//ContenedorPanelDivisas--------------------------------------------------------END
 	
 	//ContenedorPanelTransfer--------------------------------------------------------INIT
 	
 	NS.foliosCVDGlobalTransfer = '';
 	
	//Ventana para autorizar divisas
	var winLoginTransfer = new Ext.Window({
		title: 'Confirmación de contraseña'
		,modal:true
		,shadow:true
		,width: 200
	   	,height:110
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,closable: false
	   	,resizable: false
	   	,draggable: false
	   	,items: [
	   	
	   		{
	       		inputType: 'password',
				xtype: 'textfield',
	       		id:PF+'txtPswTransfer',
	       		name:PF+'txtPswTransfer',
	       		x:20,
	       		y:10,
	       		width:100,
	       		hidden:false	       		
	   		}
		]
        
	   	,buttons: [
		   	{
			 	text: 'Aceptar',
				handler: function(e){
					
					var credencial = Ext.getCmp(PF+'txtPswTransfer').getValue();
					
					if( credencial == '' ){
						Ext.Msg.show({ title:'SET', icon:Ext.Msg.WARNING, msg:'No ha capturado la contraseña.', buttons:Ext.Msg.OK });
						return;	
					}
					
					if( NS.foliosCVDGlobalTransfer == '' ){
						Ext.Msg.show({ title:'SET', icon:Ext.Msg.WARNING, msg:'No ha seleccionado ningun registro.', buttons:Ext.Msg.OK });
						return;
					} 
        	   		
					mascara.show();
        	   		
        	   		GestionDeOperacionesDivisasAction.autorizaOperacionesTransfer(credencial, NS.idUsuario, NS.foliosCVDGlobalTransfer, function(response, e){
        	   			
        	   			NS.storeMovimientosTransfer.load();
        	   			
        	   			var icon = Ext.Msg.INFO; 
        				
        				if( ! response.resultado ){
        					icon = Ext.Msg.WARNING;
        				}
        				
        				Ext.Msg.show({ title:'SET', icon:icon, msg:response.mensaje, buttons:Ext.Msg.OK });
        										
						mascara.hide();						
						winLoginTransfer.hide();
        	   			
    		    	});
        	   		
        	   		 estatusSeleccionadoTransfer = "NINGUNO";
        	   		 NS.foliosCVDGlobalTransfer = '';
				 	
				 }
		     },{
			 	text: 'Cancelar',
				handler: function(e) { 
				 	winLoginTransfer.hide();
				}
		     }
	 	]
	});

 	
 	NS.storeMovimientosTransfer = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder: ['noEmpresa', 'grupo', 'noClaveControl', 'idDivisa'],
		directFn:GestionDeOperacionesDivisasAction.traerOperacionesCompraDeTransferParaPago, 
		fields: [
			{name: 'nomEmpresa'},
			{name: 'descDivisaVenta'},
			{name: 'idBancoCargo'},
			{name: 'descBancoCargo'},			
			{name: 'chequeraCargo'},
			{name: 'importeVenta'},
			{name: 'descDivisaCompra'},
			{name: 'descBancoAbono'},
			{name: 'chequeraAbono'},
			{name: 'tipoDeCambio'},
			{name: 'importeCompra'},
			{name: 'nomCasaDeCambio'},
			{name: 'estatusAutorizacion'},
			{name: 'folio'},
			{name: 'cveControl'},
			{name: 'fecPropuesta'}
			
		],
		//autoLoad:true, 
		listeners: {
			load: function(s, records){				
				
				if(records.length == null || records.length <= 0)
				{					
					Ext.getCmp(PF + 'txtRegistrosSeleccionadosTransfer').setValue(0);
					Ext.getCmp(PF + 'txtTotalTransfer').setValue("0.00"); 
				}
					   		
			}
		}
	}); 
 	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosTransfer, msg:"Cargando..."});
 	NS.storeMovimientosTransfer.load();
 	 	
 	NS.seleccionMovimientosTransfer = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});  
 	
 	NS.columnasMovimientosTransfer = new Ext.grid.ColumnModel([
 	    	                                                   
  	    NS.seleccionMovimientosTransfer,
  	    {header: 'Empresa'			, width: 180, dataIndex: 'nomEmpresa', sortable: true},
  	    {header: 'Autorizacion'		, width: 180, dataIndex: 'estatusAutorizacion', sortable: true,
	    	renderer:function(value){ return value=='P'?'PENDIENTE':'AUTORIZADA';}
	    },	    
	    {header: 'Divisa Cargo'		, width: 120, dataIndex: 'descDivisaVenta', sortable: true},
	    {header: 'Banco Cargo'		, width: 120, dataIndex: 'descBancoCargo', sortable: true},
	    {header: 'Chequera Cargo'	, width: 120, dataIndex: 'chequeraCargo', sortable: true},
	    {header: 'Importe Venta'	, width: 120, dataIndex: 'importeVenta', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Divisa Abono'		, width: 120, dataIndex: 'descDivisaCompra', sortable: true},
	    {header: 'Banco Abono'		, width: 120, dataIndex: 'descBancoAbono', sortable: true},
	    {header: 'Chequera Abono'	, width: 120, dataIndex: 'chequeraAbono', sortable: true},
	    {header: 'Tipo Cambio'		, width: 120, dataIndex: 'tipoDeCambio', sortable: true},
	    {header: 'Casa de Cambio'	, width: 120, dataIndex: 'nomCasaDeCambio', sortable: true},
	    {header: 'Importe Compra'	, width: 120, dataIndex: 'importeCompra', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Fecha Propuesta'		, width: 120, dataIndex: 'fecPropuesta', sortable: true},
  	   
  	]); 

 	NS.gridMovimientosTransfer = new Ext.grid.GridPanel({
		store: NS.storeMovimientosTransfer,
		id: 'gridMovimientosTransfer',		
		cm: NS.columnasMovimientosTransfer,
		sm: NS.seleccionMovimientosTransfer,		
		height: 200,
		stripeRows: true,
		columnLines: true,
		title: 'Compra de Transfer',
		tools:[{
		    id:'refresh',
		    qtip: 'Refrescar Información',		   
		    handler: function(event, toolEl, panel){
		    	NS.gridMovimientosTransfer.getSelectionModel().clearSelections();
		    	var regSelec = NS.gridMovimientosTransfer.getSelectionModel().getSelections();				
				Ext.getCmp(PF + 'txtRegistrosSeleccionadosTransfer').setValue(regSelec.length);
        		NS.MuestraDatosTransfer();
        		NS.storeMovimientosTransfer.load();
		    	
		    }
		}],
		listeners: {
			rowclick:{
				fn:function(grid, i, e) {
					      
					var regSelec = NS.gridMovimientosTransfer.getSelectionModel().getSelections();
					Ext.getCmp(PF + 'txtRegistrosSeleccionadosTransfer').setValue(regSelec.length);					
					NS.MuestraDatosTransfer();
					NS.SeleccionaSoloEstatusTransfer(i);
				}
			},			
			click: {
				fn:function(grid) {
					      
					var regSelec = NS.gridMovimientosTransfer.getSelectionModel().getSelections();
					
					Ext.getCmp(PF + 'txtRegistrosSeleccionadosTransfer').setValue(regSelec.length);
	        				NS.MuestraDatosTransfer();
	  
	               }
	        }
			
		}
	});
 	
    NS.MuestraDatosTransfer = function(){
 		
 		var regSelec = NS.gridMovimientosTransfer.getSelectionModel().getSelections();
 		
 		
 		if(regSelec.length>0){
 			var totalImporte=0;
 			
 			for(var k=0;k<regSelec.length;k=k+1){
 				
				totalImporte = totalImporte + regSelec[k].get('importeVenta');
				Ext.getCmp(PF + 'txtTotalTransfer').setValue(totalImporte.toFixed(4));
				var Total = Ext.getCmp(PF + 'txtTotalTransfer').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtTotalTransfer').getValue()));
				
			}
 		
 		}else{

			Ext.getCmp(PF + 'txtTotalTransfer').setValue("0.0000");

 		}
 	};
 	
    var estatusSeleccionadoTransfer = "NINGUNO";
	
	NS.SeleccionaSoloEstatusTransfer=function(i){
		var regSelec = NS.gridMovimientosTransfer.getSelectionModel().getSelections();
 		 		
 		if(regSelec.length>0){
 			
 			for(var k=0;k<regSelec.length;k=k+1){
 				
	 			 if( estatusSeleccionadoTransfer == "NINGUNO" ){
	 				 
	 				estatusSeleccionadoTransfer = regSelec[k].get('estatusAutorizacion')
	 				
	 			 }else{
	 				 
	 				if(estatusSeleccionadoTransfer != regSelec[k].get('estatusAutorizacion') ){
	 					NS.gridMovimientosTransfer.getSelectionModel().deselectRow(i);
	 					Ext.Msg.alert('SET', 'Debe Seleccionar registros del mismo estatus.');
    	   				return;	
	 				}
	 			 }
 				
			 			
				
			}
 		
 		}else{

 			estatusSeleccionadoTransfer = "NINGUNO";

 		}
		
	};
 	
 	
 	NS.labRegistrosSeleccionadosTransfer = new Ext.form.Label({
	     text: 'Registros Seleccionados',
	     x: 260,
	     y: 215
	});
	                                    	
	NS.txtRegistrosSeleccionadosTransfer = new Ext.form.TextField({
	     id: PF + 'txtRegistrosSeleccionadosTransfer',
	     name:PF + 'txtRegistrosSeleccionadosTransfer',
	     x: 410,
	     y: 212,
	     width: 180, 
	     tabIndex: 0 
	});
	
	NS.labTotalTransfer = new Ext.form.Label({
	     text: 'Total',
	     x: 660,
	     y: 215
	});  
	                                    	
	NS.txtTotalTransfer = new Ext.form.TextField({
	     id: PF + 'txtTotalTransfer',
	     name:PF + 'txtTotalTransfer',
	     x: 695, 
	     y: 212, 
	     width: 180, 
	     tabIndex: 0,
	     listeners: {
				change: {
					fn: function(caja, valor) {
						if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
							var Total = Ext.getCmp(PF + 'txtTotalTransfer').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtTotalTransfer').getValue()));
							
						}
					}
				}
			}
	     
	});
	
	NS.ContenedorPanelTransferTransfer = new Ext.form.FieldSet({   
		layout: 'absolute',
		title: 'Movimientos',
	    height: 310,
		margin:10,		
	    items:[
	           NS.gridMovimientosTransfer,
	           NS.txtRegistrosSeleccionadosTransfer,
	           NS.labRegistrosSeleccionadosTransfer,
	           NS.labTotalTransfer,
	           NS.txtTotalTransfer	,	           
	           apps.SET.btnFacultativo(new Ext.Button(
		        {
		           id:'btnAutorizarTransfer', name:'btnAutorizarTransfer', text: 'Autorizar',  x: 1500, y: 250,  width: 80, height: 22, hidden:true,
				   listeners:{ click:{ fn:function(e){ 
					   
					   Ext.getCmp(PF+'txtPswTransfer').setValue('');
					   
					   var regSelec = NS.gridMovimientosTransfer.getSelectionModel().getSelections();
        	   			
	        	   		if( regSelec.length == 0 ){
        	   				Ext.Msg.alert('SET', 'No ha seleccionado ningun registro.');
        	   				return;
        	   			}
	        	   		
	        	   		var folios = ''; 
	        	   		
	        	   		Ext.each( regSelec, function(registro, indice, array){
	        	   			
	        	   			if( registro.get('estatusAutorizacion')=== 'P' ){
	        	   				
	        	   				folios = folios + registro.get('folio') + ",";
	        	   				
	        	   			}
	        	   			
	        	   		});
	        	   		
	        	   		if( folios === '' ){
	        	   			Ext.Msg.alert('SET', 'No ha seleccionado ningun registro pendiente de Autorizar.');
        	   				return;
	        	   		}
	        	   		
	        	   		NS.foliosCVDGlobalTransfer = folios;
	        	   		
	        	   		winLoginTransfer.show();
	        	   		
				   
	            } } }
				})),
				apps.SET.btnFacultativo(new Ext.Button({ 
					 id:'btnEjecutarTransfer', name:'btnEjecutarTransfer',text: 'Ejecutar', x: 800, y: 250, width: 80, height: 22,
					   listeners:{ click:{ fn:function(e){
						   
						   e.setVisible(false);
						   
							var regSelec = NS.gridMovimientosTransfer.getSelectionModel().getSelections();
							var folios = '';
		    	   			
		        	   		if( regSelec.length == 0 ){
		    	   				Ext.Msg.alert('SET', 'No ha seleccionado ningun registro.');
		    	   				e.setVisible(true);
		    	   				return;
		    	   			}
		        	   		
		        	   		Ext.MessageBox.show({
							       title : 'Información SET',
							       msg : 'Pagando operaciones, espere por favor...',
							       width : 300,
							       wait : true,
							       progress:true,
							       waitConfig : {interval:200}					
					   		});
		        	   			
	        	   			Ext.each( regSelec, function(registro, indice, array){
		        	   			
		        	   			if( registro.get('estatusAutorizacion')=== 'A' ){
		        	   				
		        	   				folios = folios + registro.get('folio') + ",";
		        	   				
		        	   			}else{
		        	   				NS.gridMovimientosTransfer.getSelectionModel().deselectRow(indice);
		        	   			}
		        	   			
		        	   		});
	        	   			
	        	   			if( folios == ''){
	        	   				Ext.Msg.alert('SET', 'No ha seleccionado ningun registro Autorizado.');
	        	   				e.setVisible(true);
		    	   				return;
	        	   			}
	        	   			
	        	   			regSelec = NS.gridMovimientosTransfer.getSelectionModel().getSelections();
	        	   			
	        	   			var registros = new Array ();
		        	   			        	   	
		        	   		for( var i = 0; i < regSelec.length; i++ ){
		        	   			
		        	   			var registro={};
		        	   					        	   			
		        	   			registro.folio           = regSelec[i].get('folio');
		        	   			registro.cveControl      = regSelec[i].get('cveControl');
		        	   			registro.fecPropuesta    = regSelec[i].get('fecPropuesta');
		        	   			
		        	   			registros[i] = registro;		        	   			
		        	   		}
		        	   		
		        	   		var jsonRegistros = Ext.util.JSON.encode(registros);		        	   		  
		        	   		NS.hide_messagee();	        	   		
		        	   		GestionDeOperacionesDivisasAction.pagarOperacionesTransfer( jsonRegistros, function(result, f){
		        	   			
            					if(result!==null && result!==undefined && result!==''){
            						
            						if(result.estatus==true){
            							if(result.excel !== ''){
            								
            								parametros='?nomReporte=excelMensajePagoPropuesta';
            								parametros+='&nomParam1=nombre';
            								parametros+='&valParam1=' + result.excel;
            								
            								window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);            								
            								Ext.Msg.alert('Información SET', 'Consulte el archivo Excel para ver el detalle de los pagos.', function(btn){
            								
            								
	                						    if (btn == 'ok'){
	                						    	if(result.reporteCVD==true){
		                						    	NS.storeMovimientosTransfer.load();
		                						    	var strParams = ''; 
		                				    			strParams = '?nomReporte=ReporteAutCompraVentaDeTransfer&foliosReporte=' + folios ;
		                				    			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		                				    			
		                				    			if(e){	                				    				
		                				    				e.setVisible(true);	                				    				
		                				    			}
	                						    	}
	                						    }
	                						});
            								
            							}else{
            								Ext.Msg.alert('Información SET', 'No se pudo generar la respuesta.', function(btn){
	                						    if (btn == 'ok'){
	                						    	NS.storeMovimientosTransfer.load();
	                						    }
	                						});

            							}
            						
            						}else{
            							Ext.Msg.alert('Información SET', ''+result.msgError, function(btn){
                						    if (btn == 'ok'){
                						    	NS.storeMovimientosTransfer.load();
                						    }
                						});
            						}
            					}
            					
            					
		        	   			
		        	   			//////////////
		        	   			
		        	   			//NS.storeMovimientosTransfer.load();
		        	   			
		        	   			/*var icon = Ext.Msg.INFO; 
		        				
		        				if( ! response.resultado ){
		        					icon = Ext.Msg.WARNING;
		        				}
		        				
		        				Ext.Msg.show({ title:'SET', icon:icon, msg:response.mensaje, buttons:Ext.Msg.OK });*/
		        	   			
	        		    	});
					   
						} } }
					} ) )    ,
					
		           apps.SET.btnFacultativo(new Ext.Button(
			        {
			        	id:'btnEliminarTransfer', name:'btnEliminarTransfer', text: 'Eliminar',  x: 700, y: 250,  width: 80, height: 22,
					   listeners:{ click:{ fn:function(e){ 

		        	   		var regSelec = NS.gridMovimientosTransfer.getSelectionModel().getSelections();
	        	   			
		        	   		if( regSelec.length == 0 ){
	        	   				Ext.Msg.alert('SET', 'No ha seleccionado ningun registro.');
	        	   				return;
	        	   			}
		        	   		
		        	   		var folios = ''; 
		        	   		
		        	   		Ext.each( regSelec, function(registro, indice, array){
		        	   				
		        	   			folios = folios + registro.get('folio') + ",";
		        	   				
		        	   		});
		        	   		
		        	   		GestionDeOperacionesDivisasAction.eliminarOperacionesTransfer(NS.idUsuario, folios, function(response, e){
		        	   			
		        	   			NS.storeMovimientosTransfer.load();
		        	   			
		        	   			var icon = Ext.Msg.INFO; 
		        				
		        				if( ! response.resultado ){
		        					icon = Ext.Msg.WARNING;
		        				}
		        				
		        				Ext.Msg.show({ title:'SET', icon:icon, msg:response.mensaje, buttons:Ext.Msg.OK });
		        	   			
	        		    	});
		        	   		
		        	   		 estatusSeleccionadoTransfer = "NINGUNO";
					   
		            } } }
					}))         
	           ]
	           
 	});
 	//ContenedorPanelTransfer--------------------------------------------------------END
 	
	NS.AutorizacionCVTCVD = new Ext.form.FormPanel({
		title: 'Autorizci�n de Compra-Venta de Divisas y Compra de Transfer',		
		frame: true,		
		autoScroll: true,
		padding:5,
		id: PF + 'AutorizacionCVTCVD',     
		name: PF+ 'AutorizacionCVTCVD',
		renderTo: NS.tabContId,
		items :[
		    NS.ContenedorPanelDivisas,
		    NS.ContenedorPanelTransferTransfer

		]       
	});
	
	
	
	NS.AutorizacionCVTCVD.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
 	
});