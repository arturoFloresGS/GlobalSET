Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Egresos.CompraVentaDivisas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);  
	Ext.QuickTips.init();
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.cliente = 0;
	NS.iTieneBanca = 0;
	NS.iCustodia = 0;
	NS.datos = true;
	var fecha = "";

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
	
	
	function limpiar(){
	 	NS.CompraTransfer.getForm().reset();
	 	NS.storeBancoCargo.removeAll();
	 	NS.storeBancoAbono.removeAll(); 
	 	NS.storeChequeraCargo.removeAll();
	 	NS.storeChequeraAbono.removeAll();
	 	NS.storeChequera.removeAll();
	 	NS.storeBanco.removeAll();
	 	NS.storeOperador.removeAll();   
	 	NS.storeRubro.removeAll();
	};
	
	function cambiarFecha(fecha) {
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		
		for(var i=0;i<12;i=i+1) {
			if(mesArreglo[i]===mesDate) {
			var mes=i+1;
				if(mes<10)
				mes = '0' + mes;
			}
		}
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	};
	
	NS.labDivisaVenta = new Ext.form.Label({
		text: 'Divisa de Venta',
		x: 0,
		y: 0
	});
	   
	NS.txtDivisaVenta = new Ext.form.TextField({
		id: PF + 'txtDivisaVenta',
        name:PF + 'txtDivisaVenta',
		x: 0,
        y: 20,
        width: 50, 
        tabIndex: 0,
        listeners : {
        	change : {
        		fn: function(caja, valor){
					NS.txtDivisaVenta.setValue(caja.getValue().toUpperCase());		
        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisaVenta',NS.cmbDivisaVenta.getId());
					if(valueCombo != null && valueCombo != undefined && valueCombo != '')
						NS.accionarDivisaVenta(valueCombo);
        		}
        	}
        }
	});

	NS.accionarDivisaVenta = function(valueCombo){
	
			if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisaVenta',NS.cmbDivisaVenta.getId());
				
				NS.storeBancoCargo.baseParams.noEmpresa = NS.noEmpresa;
				NS.storeBancoCargo.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();
				
				NS.storeBancoCargo.load();
		}
	};	
	
	NS.storeDivisaVenta = new Ext.data.DirectStore({     
		paramsAsHash: false,
		root: '',
		baseParams: {
		idUsuario: NS.idUsuario
    	},
		paramOrder:['idUsuario'],
		directFn: ConfirmacionCargoCtaAction.obtenerDivisaVta, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
			 	],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisaVenta, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene divisas asignadas');
				}
					   		
			}
		}
	}); 
	
	NS.storeDivisaVenta.load();
 
 NS.cmbDivisaVenta = new Ext.form.ComboBox({
		store: NS.storeDivisaVenta,
		id: PF + 'cmbDivisaVenta',
		name: PF + 'cmbDivisaVenta',
		x: 60,
        y: 20,
        width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccione una Divisa ',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				BFwrk.Util.updateComboToTextField(PF+'txtDivisaVenta',NS.cmbDivisaVenta.getId());
	 				NS.accionarDivisaVenta(combo.getValue());
					NS.storeBancoCargo.baseParams.noEmpresa = NS.noEmpresa;
					NS.storeBancoCargo.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();
					
					NS.storeBancoCargo.load();					
				}
			}
		}   
	});
	
	NS.labBancoCargo = new Ext.form.Label({
		text: 'Banco de Cargo',
		x: 0,
		y: 55
	});
	
	NS.txtBancoCargo = new Ext.form.TextField({
		id: PF + 'txtBancoCargo',
        name:PF + 'txtBancoCargo',
		x: 0,
        y: 75,
        width: 50, 
        tabIndex: 0,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoCargo',NS.cmbBancoCargo.getId());
					if(valueCombo != null && valueCombo != undefined && valueCombo != '')
						NS.accionarBancoCargo(valueCombo);
        		}
        	}
        }
	});
	

	NS.accionarBancoCargo = function(valueCombo){
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoCargo',NS.cmbBancoCargo.getId());
			
			NS.storeChequeraCargo.baseParams.noEmpresa = NS.noEmpresa;
			NS.storeChequeraCargo.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();
			NS.storeChequeraCargo.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBancoCargo').getValue());
				
			NS.storeChequeraCargo.load(); 
		}
	};
	
	NS.storeBancoCargo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder:['noEmpresa','idDivisa'],
		directFn: ConfirmacionCargoCtaAction.obtenerBancoVta, 
		idProperty: 'idBanco', 
		fields: [
			 {name: 'idBanco'},
			 {name: 'descBanco'}
		],
		listeners: {     						 	 	 	
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoCargo, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene bancos asignados');
				}
					   		
			}
		}
	}); 
 
 NS.cmbBancoCargo = new Ext.form.ComboBox({
		store: NS.storeBancoCargo,
		id: PF + 'cmbBancoCargo',
		name: PF + 'cmbBancoCargo',
		x: 60,
        y: 75,
        width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Seleccione un Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				BFwrk.Util.updateComboToTextField(PF+'txtBancoCargo',NS.cmbBancoCargo.getId());
	 				NS.accionarBancoCargo(combo.getValue());
	 				
	 				
	 				NS.storeChequeraCargo.baseParams.noEmpresa = NS.noEmpresa;
	 				NS.storeChequeraCargo.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();
	 				NS.storeChequeraCargo.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBancoCargo').getValue());
	 				
	 				
	 				NS.storeChequeraCargo.load();   
	 				
	 				
				}
			}
		}
	});
 
 
	
	NS.labChequeraCargo = new Ext.form.Label({
		text: 'Chequera de Cargo',
		x: 0,
		y: 110
	});
	
	NS.storeChequeraCargo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder:['noEmpresa','idDivisa','idBanco'],
		directFn: ConfirmacionCargoCtaAction.obtenerChequeraVta,     
		idProperty: 'idChequera', 
		fields: [
			 {name: 'idChequera'},
			 {name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraCargo, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene chequeras asignadas');
				}
					   		
			}
		}
	}); 
 
 NS.cmbChequeraCargo = new Ext.form.ComboBox({
		store: NS.storeChequeraCargo,
		id: PF + 'cmbChequeraCargo',
		name: PF + 'cmbChequeraCargo',
		x: 0,
        y: 130,
        width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idChequera',
		displayField: 'idChequera',
		autocomplete: true,
		emptyText: 'Seleccione un Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				
					Ext.getCmp(PF+'cmbChequeraCargo').setValue(combo.getValue());
				}
			}
		}
	});
	
	NS.labDivisaCompra = new Ext.form.Label({ 
		text: 'Divisa de Compra',
		x: 380,
		y: 0
	});
	
	NS.txtDivisaCompra = new Ext.form.TextField({
		id: PF + 'txtDivisaCompra',   
        name:PF + 'txtDivisaCompra',
		x: 380,
        y: 20,
        width: 50, 
        tabIndex: 0,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
					NS.txtDivisaCompra.setValue(caja.getValue().toUpperCase());
        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisaCompra',NS.cmbDivisaCompra.getId());
					if(valueCombo != null && valueCombo != undefined && valueCombo != '')
						NS.accionarDivisaCompra(valueCombo);
        		}
        	}
        }
	});
	
	NS.accionarDivisaCompra = function(valueCombo){
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') 
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisaCompra',NS.cmbDivisaCompra.getId());
			
		var tieneBanca = Ext.getCmp(PF + 'Radios').getValue();
		NS.iTieneBanca = parseInt(tieneBanca.getGroupValue());
			
		var icustodia = Ext.getCmp(PF + 'RadiosBtn').getValue();
		NS.iCustodia = parseInt(icustodia.getGroupValue());
			
		NS.storeBancoAbono.baseParams.noEmpresa = NS.noEmpresa;
		NS.storeBancoAbono.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaCompra').getValue();    
		NS.storeBancoAbono.baseParams.radio = NS.iTieneBanca;    
		NS.storeBancoAbono.baseParams.custodia = NS.iCustodia;    
				
		NS.storeBancoAbono.load();
	};
	
	NS.storeDivisaCompra = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {    
    		idUsuario: NS.idUsuario
		},
		paramOrder:['idUsuario'],
		directFn: ConfirmacionCargoCtaAction.obtenerDivisaVta, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisaCompra, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene chequeras asignadas');
				}
					   		
			}
		}
	});
	
	NS.storeDivisaCompra.load();
 
	NS.cmbDivisaCompra = new Ext.form.ComboBox({
		store: NS.storeDivisaCompra,
		id: PF + 'cmbDivisaCompra',
		name: PF + 'cmbDivisaCompra',
		x: 440,
        y: 20,
        width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{      
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtDivisaCompra',NS.cmbDivisaCompra.getId());
					NS.accionarDivisaCompra(combo.getValue());
			
	 				var tieneBanca = Ext.getCmp(PF + 'Radios').getValue();
    				NS.iTieneBanca = parseInt(tieneBanca.getGroupValue());
    				
    				var icustodia = Ext.getCmp(PF + 'RadiosBtn').getValue();
    				NS.iCustodia = parseInt(icustodia.getGroupValue());
	 			
	 				NS.storeBancoAbono.baseParams.noEmpresa = NS.noEmpresa;
	 				NS.storeBancoAbono.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaCompra').getValue();    
	 				NS.storeBancoAbono.baseParams.radio = NS.iTieneBanca;    
	 				NS.storeBancoAbono.baseParams.custodia = NS.iCustodia;    
	 				
	 				NS.storeBancoAbono.load();
	 				
				}
			}
		}
	});
	
	NS.labBancoAbono = new Ext.form.Label({
		text: 'Banco de Abono',
		x: 380,
		y: 55
	});
	
	NS.txtBancoAbono = new Ext.form.TextField({
		id: PF + 'txtBancoAbono',
        name:PF + 'txtBancoAbono',
		x: 380,
        y: 75,
        width: 50, 
        tabIndex: 0,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoAbono',NS.cmbBancoAbono.getId());
					if(valueCombo != null && valueCombo != undefined && valueCombo != '')
						NS.accionarBancoAbono(valueCombo);
        		}
        	}
        }
	});
	
	NS.accionarBancoAbono = function(valueCombo){
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') 
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisaCompra',NS.cmbDivisaCompra.getId());
		
		var tieneBanca = Ext.getCmp(PF + 'Radios').getValue();
		NS.iTieneBanca = parseInt(tieneBanca.getGroupValue());
		
		var icustodia = Ext.getCmp(PF + 'RadiosBtn').getValue();
		NS.iCustodia = parseInt(icustodia.getGroupValue());
			
		NS.storeChequeraAbono.baseParams.custodia = NS.iCustodia; 
		NS.storeChequeraAbono.baseParams.radio = NS.iTieneBanca; 
		NS.storeChequeraAbono.baseParams.noEmpresa = NS.noEmpresa;
		NS.storeChequeraAbono.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaCompra').getValue();
		NS.storeChequeraAbono.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBancoAbono').getValue());
		       
		NS.storeChequeraAbono.load();  
	};
	
	NS.storeBancoAbono = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
		},
		paramOrder:['noEmpresa','idDivisa','radio','custodia'],
		directFn: ConfirmacionCargoCtaAction.obtenerBancoAbo, 
		idProperty: 'idBanco', 
		fields: [
			 {name: 'idBanco'},
			 {name: 'descBanco'},
			 {name: ''}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoAbono, msg:"Cargando..."});
 				
 				
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene bancos asignados');  
				}
					   		
			}
		}
	}); 
 
 NS.cmbBancoAbono = new Ext.form.ComboBox({ 
		store: NS.storeBancoAbono,
		id: PF + 'cmbBancoAbono',
		name: PF + 'cmbBancoAbono',
		x: 440,
        y: 75,
        width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,     
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Seleccione un Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {					
	 				BFwrk.Util.updateComboToTextField(PF+'txtBancoAbono',NS.cmbBancoAbono.getId());
	 				NS.accionarBancoAbono(combo.getValue());

	 				
	 				var tieneBanca = Ext.getCmp(PF + 'Radios').getValue();
    				NS.iTieneBanca = parseInt(tieneBanca.getGroupValue());
    				
    				var icustodia = Ext.getCmp(PF + 'RadiosBtn').getValue();
    				NS.iCustodia = parseInt(icustodia.getGroupValue());
    					
    				NS.storeChequeraAbono.baseParams.custodia = NS.iCustodia; 
	 				NS.storeChequeraAbono.baseParams.radio = NS.iTieneBanca; 
	 				NS.storeChequeraAbono.baseParams.noEmpresa = NS.noEmpresa;
	 				NS.storeChequeraAbono.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaCompra').getValue();
	 				NS.storeChequeraAbono.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBancoAbono').getValue());
	 				       
	 				NS.storeChequeraAbono.load();
				}
			}
		}
	});	
	
	NS.labChequeraAbono = new Ext.form.Label({
		text: 'Chequera de Abono',
		x: 380,
		y: 110
	});
	
	NS.storeChequeraAbono = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
		},
		paramOrder:['custodia','radio','noEmpresa','idDivisa','idBanco'],
		directFn: ConfirmacionCargoCtaAction.obtenerChequeraAbo, 
		idProperty: 'idChequera', 
		fields: [
			 {name: 'idChequera'},
			 {name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraAbono, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene chequeras asignadas');
				}
					   		
			}
		}
	}); 
 			
 NS.cmbChequeraAbono = new Ext.form.ComboBox({
		store: NS.storeChequeraAbono,
		id: PF + 'cmbChequeraAbono',
		name: PF + 'cmbChequeraAbono',
		x: 380,
        y: 130,
        width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idChequera',
		displayField: 'idChequera',
		autocomplete: true,
		emptyText: 'Seleccione una Chequera' ,
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				Ext.getCmp(PF+'cmbChequeraAbono').setValue(combo.getValue());
				}
			}
		}
	});	
 
 	NS.labReferencia = new Ext.form.Label({
 		text: 'Referencia CIE:',
		x: 0,
		y: 180
 	});    
 	
 	NS.optRadios = new Ext.form.RadioGroup({
		id: PF + 'Radios',
		name: PF + 'Radios',
		x: 25,
		y: 0,
		columns: 1, //muestra los radiobuttons en una columna
		items: [
	          {boxLabel: 'Trasferencia', name: 'opt', inputValue: 3, checked: true },  
	          {boxLabel: 'Cargo en Cuenta', name: 'opt', inputValue: 5},  
	          {boxLabel: 'Efectivo', name: 'opt', inputValue: 2},  
	          {boxLabel: 'Cargo en Cuenta Terceros', name: 'opt', inputValue: 5}
	     ],
	     listeners: {
				change: {
					fn: function(caja, valor) {
 						var TieneBanca = Ext.getCmp(PF + 'Radios').getValue();
 						NS.iTieneBanca = parseInt(TieneBanca.getGroupValue());      
 						
 						if(NS.iTieneBanca == 2){
 							NS.labBancoAbono.setText("Caja");
 							NS.labChequeraAbono.setText("Valor");
 							/*
 							 * Action.metodo(parametros,function(res,e){
 							 * if(res == true)
 							 * 		alert("imprime resultado");
 							 * })
 							 * 
 							 * */
 						}else{
 							NS.labBancoAbono.setText("Banco de Cargo");       
 							NS.labChequeraAbono.setText("Chequera de Abono");
 						}
 					}
 				}    
 			}
 						     
	});
 
 	NS.ContFormaPago = new Ext.form.FieldSet({
		layout: 'absolute',
		title: '',
		width: 220,
	    height: 110, 
	    x: 680,
	    y: 0,	    
		items: [
		        NS.optRadios
		       ]
		        
	});
 	
 	
 	NS.btnRadios = new Ext.form.RadioGroup({
		id: PF + 'RadiosBtn',
		name: PF + 'RadiosBtn',
		x: 25,
		y: 0,
		columns: 1, //muestra los radiobuttons en una columna
		items: [
		        {boxLabel: 'Custodia', name: 'btn', inputValue: 1, checked: true},     
		        {boxLabel: 'Fondo Fijo', name: 'btn', inputValue: 2 }  
	           ]
	});
 	NS.ContCustodiaFondoFijo = new Ext.form.FieldSet({
		layout: 'absolute',
		title: '',
		width: 150,
	    height: 70, 
	    x: 680,
	    y: 120,	    
		items: [
		        NS.btnRadios
		       ]
		        
	});
 	
 	NS.labTipoCambio = new Ext.form.Label({
 		text: 'Tipo de Cambio: ',
		x: 0,
		y: 220
 	});
 	
 	NS.txtTipoCambio = new Ext.form.TextField({
		id: PF + 'txtTipoCambio',
        name:PF + 'txtTipoCambio',
		x: 105,
        y: 215,
        width: 120, 
        tabIndex: 0,
        listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var TipoCam = Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtTipoCambio').getValue()));
						
					}
				}
			}
		}
	});
 	
 	NS.labImporteVenta = new Ext.form.Label({
 		text: 'Importe de Venta: ',
		x: 280,
		y: 220
 	});
 	
 	NS.txtImporteVenta = new Ext.form.TextField({
		id: PF + 'txtImporteVenta',
        name:PF + 'txtImporteVenta',
		x: 400,
        y: 215,
        width: 120,  
        tabIndex: 0,   
        listeners: {
			change: {
				fn: function(caja, valor) {
 					var divisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();
 					var tipo = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtTipoCambio').getValue()));
 					var total = 0;
 					
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
 						var impVta = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtImporteVenta').getValue()));
 						Ext.getCmp(PF + 'txtImporteVenta').setValue(NS.formatNumber(impVta));
						
						if(tipo != "") {
							if (divisa == "MN") {
								total = parseFloat(impVta / tipo);
								Ext.getCmp(PF + 'txtImporteCompra').setValue(NS.formatNumber(total));
							}else {
								total = parseFloat(impVta * tipo);
								Ext.getCmp(PF + 'txtImporteCompra').setValue(NS.formatNumber(total));
							}
						}
 					}
				}
			}
		}
	});
 	
 	NS.labImporteCompra = new Ext.form.Label({
 		text: 'Importe de Compra: ',
		x: 565,
		y: 220
 	});
 	
 	NS.txtImporteCompra = new Ext.form.TextField({
		id: PF + 'txtImporteCompra',
        name:PF + 'txtImporteCompra',
		x: 695,
        y: 215,
        width: 120, 
        tabIndex: 0,
        listeners: {
			change: {
				fn: function(caja, valor) {
					 var divisa = Ext.getCmp(PF + 'txtDivisaCompra').getValue();
					 var tipo = parseFloat(Ext.getCmp(PF + 'txtTipoCambio').getValue());
 					 
 					 if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
 						 var impCom = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtImporteCompra').getValue()));
 						 Ext.getCmp(PF + 'txtImporteCompra').setValue(NS.formatNumber(impCom));
	 					 var valor1 = 0;
 						
						 if(tipo != "") {
							 if(divisa == "MN") {
								 valor1 = parseFloat(impCom / tipo);
								 Ext.getCmp(PF + 'txtImporteVenta').setValue(NS.formatNumber(valor1));
							 }else {
								 valor1 = parseFloat(impCom * tipo);
								 Ext.getCmp(PF + 'txtImporteVenta').setValue(NS.formatNumber(valor1));
							 }
	 					 }   
 					}
				}
			}
		}
	});
 	
 	NS.labCasaCambio = new Ext.form.Label({
 		text: 'Casa de Cambio: ',
		x: 0,
		y: 255
 	});
 	
 	NS.txtCasaCambio = new Ext.form.TextField({
		id: PF + 'txtCasaCambio',
        name:PF + 'txtCasaCambio',
		x: 0,
        y: 275,
        width: 70, 
        tabIndex: 0,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtCasaCambio',NS.cmbCasaCambio.getId());
					if(valueCombo != null && valueCombo != undefined && valueCombo != '')
						NS.accionarCasaCambio(valueCombo);
        		}
        	}
        }
	});
 	
 	
 	NS.accionarCasaCambio = function(valueCombo){
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') 
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtCasaCambio',NS.cmbCasaCambio.getId());
		
		NS.storeBanco.baseParams.noCliente = parseInt(Ext.getCmp(PF + 'txtCasaCambio').getValue());
		NS.storeBanco.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();
		NS.storeBanco.load();
			
		NS.storeOperador.baseParams.noCliente = parseInt(Ext.getCmp(PF + 'txtCasaCambio').getValue());			 
		NS.storeOperador.load();
		  
	};
 	
 	
 	
 	NS.storeCasaCambio = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
    		idUsuario: NS.idUsuario
		},
		paramOrder:['idUsuario'],
		directFn: ConfirmacionCargoCtaAction.obtenerCasaCambioVta, 
		idProperty: 'idCasaCambio', 
		fields: [
			 {name: 'idCasaCambio'},
			 {name: 'descCasaCambio'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCasaCambio, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene casas de cambio asignadas');
				}
					   		
			}
		}
	}); 
 
 	NS.storeCasaCambio.load();
 	
 	NS.cmbCasaCambio = new Ext.form.ComboBox({
		store: NS.storeCasaCambio,
		id: PF + 'cmbCasaCambio',   
		name: PF + 'cmbCasaCambio',
		x: 80,
        y: 275,
        width: 220,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idCasaCambio',
		displayField: 'descCasaCambio',
		autocomplete: true,
		emptyText: 'Seleccione una casa de cambio',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
 					BFwrk.Util.updateComboToTextField(PF+'txtCasaCambio',NS.cmbCasaCambio.getId());
 					NS.accionarCasaCambio(combo.getValue());

	 				NS.storeBanco.baseParams.noCliente = parseInt(Ext.getCmp(PF + 'txtCasaCambio').getValue());
	 				NS.storeBanco.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();
	 				NS.storeBanco.load();
	 				
	 				NS.storeOperador.baseParams.noCliente = parseInt(Ext.getCmp(PF + 'txtCasaCambio').getValue());			 
	 				NS.storeOperador.load(); 
	 				
				}
			}
		}
	});	 	
 	
 	NS.labOperador = new Ext.form.Label({
 		text: 'Operador:',
		x: 0,
		y: 305
 	});
 	
 	NS.txtOperador = new Ext.form.TextField({
		id: PF + 'txtOperador',    
        name:PF + 'txtOperador',
		x: 0,
        y: 325,
        width: 70, 
        tabIndex: 0,
        listeners : {
 		change : {
    		fn: function(caja, valor){     
    			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
					var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtOperador',NS.cmbOperador.getId());
				if(valueCombo != null && valueCombo != undefined && valueCombo != '')
					NS.accionarOperador(valueCombo);
    			}
    		}
 		}
        
	});
 	
 	
 	NS.accionarOperador = function(valueCombo){
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') 
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtOperador',NS.cmbOperador.getId());
			  
	};

 	
 	NS.storeOperador = new Ext.data.DirectStore({
		paramsAsHash: false,     	
		root: '',
		baseParams: {     
    		
		},
		paramOrder:['noCliente'],
		directFn: ConfirmacionCargoCtaAction.obtenerOperadorVta, 
		idProperty: 'idOperador', 
		fields: [
			 {name: 'idOperador'},
			 {name: 'nomOperador'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeOperador, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene operadores asignados');
				}
					   		
			}
		}    
	}); 
 
 NS.cmbOperador = new Ext.form.ComboBox({
		store: NS.storeOperador,
		id: PF + 'cmbOperador',
		name: PF + 'cmbOperador',
		x: 80,
        y: 325,
        width: 220,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idOperador',
		displayField: 'nomOperador',
		autocomplete: true,
		emptyText: 'Seleccione un Operador',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				BFwrk.Util.updateComboToTextField(PF+'txtOperador',NS.cmbOperador.getId());
					NS.accionarCasaCambio(combo.getValue());
				}
			}
		}
	});	 
 	
 	NS.labGrupo = new Ext.form.Label({
 		text: 'Grupo:',
		x: 0,
		y: 355
 	});
 	   
 	NS.txtGrupo = new Ext.form.TextField({
		id: PF + 'txtGrupo',
        name:PF + 'txtGrupo',
		x: 0,
        y: 375,
        width: 60, 
        tabIndex: 0,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupo',NS.cmbGrupo.getId());
					if(valueCombo != null && valueCombo != undefined && valueCombo != '')
						NS.accionarGrupo(valueCombo);
        		}
        	}
        }
	});
 	
 	NS.accionarGrupo = function(valueCombo){
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') 
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupo',NS.cmbGrupo.getId());
		
		NS.storeRubro.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupo').getValue());
		NS.storeRubro.load();
		
	};
 	
 	
 	NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
    		idUsuario: NS.idUsuario
		},
		paramOrder:['idUsuario'],
		directFn: ConfirmacionCargoCtaAction.obtenerGrupoVta, 
		idProperty: 'idGrupo',    
		fields: [
			 {name: 'idGrupo'},
			 {name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupo, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 	
 	NS.storeGrupo.load();
 
 NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo,
		id: PF + 'cmbGrupo',
		name: PF + 'cmbGrupo',
		x: 70,
        y: 375,
        width: 190,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Seleccione un Grupo',   
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				BFwrk.Util.updateComboToTextField(PF+'txtGrupo',NS.cmbGrupo.getId());
	 				NS.accionarGrupo(combo.getValue());


	 				NS.storeRubro.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupo').getValue());
	 				
	 				NS.storeRubro.load();
	 				
				}
			}
		}
	});
 
 	NS.labBanco = new Ext.form.Label({
		text: 'Banco: ',
		x: 350,
		y: 255
	});
	
	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
		name:PF + 'txtBanco',
		x: 350,
		y: 275,
		width: 70,  
		tabIndex: 0,
		listeners : {
    		change : {
    			fn: function(caja, valor){     
    				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
    					var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId());
    				if(valueCombo != null && valueCombo != undefined && valueCombo != '')
    					NS.accionarBanco(valueCombo);
    				}
    			}
    		}
	});
	
	NS.accionarBanco = function(valueCombo){
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') 
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId());
		
		NS.storeChequera.baseParams.noCliente = parseInt(Ext.getCmp(PF + 'txtCasaCambio').getValue());
		NS.storeChequera.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();    
		NS.storeChequera.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBanco').getValue());
		NS.storeChequera.load();
			  
	};

	
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
    		
		},
		paramOrder:['noCliente','idDivisa'],
		directFn: ConfirmacionCargoCtaAction.obtenerBanco, 
		idProperty: 'idBanco', 
		fields: [
			 {name: 'idBanco'},
			 {name: 'descBanco'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Bancos y Chequeras asignada');
				}
					   		
			}
		}
	}); 
 
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 425,
        y: 275,
        width: 215,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true, 
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Seleccione un Banco ',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtBanco',NS.cmbBanco.getId());
					NS.accionarBanco(combo.getValue());


	 				NS.storeChequera.baseParams.noCliente = parseInt(Ext.getCmp(PF + 'txtCasaCambio').getValue());
	 				NS.storeChequera.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();
	 				NS.storeChequera.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBanco').getValue());
	 				
	 				NS.storeChequera.load();
	 				
				}
			}
		}
	});
	
	NS.labFechaValor = new Ext.form.Label({
		text: 'Fecha Valor',
		x: 400,
		y: 305
	});
	
	
	NS.txtFechaValor = new Ext.form.DateField({
		id: PF + 'txtFechaValor',
		name: PF + 'txtFechaValor',
		x: 400,
		y: 325,
		width: 110,
		format: 'd/m/Y',
		value : apps.SET.FEC_HOY
	});
	
	NS.labRubro = new Ext.form.Label({
 		text: 'Rubro',
		x: 320,
		y: 355
 	});
 	
 	NS.txtRubro = new Ext.form.TextField({
		id: PF + 'txtRubro',
        name:PF + 'txtRubro',
		x: 320,
        y: 375,
        width: 60, 
        tabIndex: 0,
        listeners : {
 			change : {
    			fn: function(caja, valor){     
    				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
    					var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtRubro',NS.cmbRubro.getId());
    				if(valueCombo != null && valueCombo != undefined && valueCombo != '')
    					NS.accionarRubro(valueCombo);
    			}
    		}
 		}
        
	});
 	
 	
 	NS.accionarRubro = function(valueCombo){
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') 
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtRubro',NS.cmbRubro.getId());
	};	
 	
 	
 	NS.storeRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
		},
		paramOrder:['idGrupo'],
		directFn: ConfirmacionCargoCtaAction.obtenerRubroVta, 
		idProperty: 'idRubro', 
		fields: [
			 {name: 'idRubro'},
			 {name: 'descRubro'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubro, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 
 	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeRubro,
		id: PF + 'cmbRubro',
		name: PF + 'cmbRubro',
		x: 400,
        y: 375,
        width: 290,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Seleccione un Rubro',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
 		
 					BFwrk.Util.updateComboToTextField(PF+'txtRubro',NS.cmbRubro.getId());
 					NS.accionarRubro(combo.getValue());					
				}
			}
		} 
	});
 	
 	 NS.storeChequera = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			baseParams: {
	    		
			},
			paramOrder:['noCliente','idDivisa','idBanco'],
			directFn: ConfirmacionCargoCtaAction.obtenerChequeraTotal,    
			idProperty: 'idChequera', 
			fields: [
				 {name: 'idChequera'},
				 {name: 'idChequera'}
			],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg:"Cargando..."});
					if(records.length == null || records.length <= 0)
					{
						Ext.Msg.alert('SET','No tiene Chequeras asignadas');
					}
						   		
				}
			}
		}); 
 	 
 	NS.labChequera = new Ext.form.Label({
 		text: 'Chequera',
		x: 680,
		y: 255
 	});
	 
	 NS.cmbChequera = new Ext.form.ComboBox({
			store: NS.storeChequera,
			id: PF + 'cmbChequera',
			name: PF + 'cmbChequera',
			x: 680,
	        y: 275,
	        width: 230,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
	        tabIndex: 1,
			valueField: 'idChequera',
			displayField: 'idChequera',
			autocomplete: true,
			emptyText: 'Seleccione una Chequera ',
			triggerAction: 'all',
			value: '',
			visible: false,
			listeners:{
				select:{
		  			fn:function(combo, valor) {
						Ext.getCmp(PF+'cmbChequera').setValue(combo.getValue());
					}	
				}
			}
		});
	 
	 	NS.txtFechaLiquidacion = new Ext.form.DateField({
			id: PF + 'txtFechaLiquidacion',
			name: PF + 'txtFechaLiquidacion',
			x: 730,
			y: 325,
			width: 110,
			format: 'd/m/Y',
			value : apps.SET.FEC_HOY
		});
	 	
	 	NS.labFechaLiquidacion = new Ext.form.Label({
			text: 'Fecha de Liquidacion ',
			x: 580,
			y: 305
		});
	 	
	 	NS.labFirmas = new Ext.form.Label({
	 		text: 'Firmas',
			x: 580,
			y: 355
	 	});
	 	
	 	NS.storeFirma1 = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			/*baseParams: {
	 		idUsuario: NS.idUsuario
			},*/
			//paramOrder:['idUsuario'],
			//directFn: ConfirmacionTransferenciasAction.obtenerEmpresas, 
			idProperty: 'noFirma1', 
			fields: [
				 {name: 'noFirma1'},
				 {name: 'nomFirma1'}
			],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFirma1, msg:"Cargando..."});
					if(records.length == null || records.length <= 0)
					{
						Ext.Msg.alert('SET','No existe firma 1');
					}
						   		
				}
			}
		}); 

	 	NS.cmbFirma1 = new Ext.form.ComboBox({
			store: NS.storeFirma1,
			id: PF + 'cmbFirma1',
			name: PF + 'cmbFirma1',
			x: 40,
			y: 0,
			width: 190, 
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
			tabIndex: 1,
			valueField: 'noFirma1',
			displayField: 'nomFirma1',
			autocomplete: true,
			emptyText: 'selecciona la firma 1',
			triggerAction: 'all',
			value: '',
			visible: false,
			/*listeners:{
				select:{
					
					}
				
			}*/
		});
	 	
	 	NS.labFirma1 = new Ext.form.Label({
			text: '1.-',
			x: 0,
			y: 5
		});
	 	
	 	NS.storeFirma2 = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			/*baseParams: {
	 		idUsuario: NS.idUsuario
			},*/
			//paramOrder:['idUsuario'],
			//directFn: ConfirmacionTransferenciasAction.obtenerEmpresas, 
			idProperty: 'noFirma2', 
			fields: [
				 {name: 'noFirma2'},
				 {name: 'nomFirma2'}
			],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFirma2, msg:"Cargando..."});
					if(records.length == null || records.length <= 0)
					{
						Ext.Msg.alert('SET','No existe firma 2');
					}
						   		
				}
			}
		}); 

	 	NS.cmbFirma2 = new Ext.form.ComboBox({
			store: NS.storeFirma2,
			id: PF + 'cmbFirma2',
			name: PF + 'cmbFirma2',
			x: 40,
			y: 40,
			width: 190, 
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
			tabIndex: 1,
			valueField: 'noFirma2',
			displayField: 'nomFirma2',
			autocomplete: true,
			emptyText: 'selecciona la firma 2',
			triggerAction: 'all',
			value: '',
			visible: false
			/*listeners:{
				select:{
					
					}
				
			}*/
		});
	 	
	 	NS.labFirma2 = new Ext.form.Label({
			text: '2.-',
			x: 0,
			y: 40
		});
	 	
	 	
	 	NS.ContenedorFirmas = new Ext.form.FieldSet({    
			layout: 'absolute',
			width: 260,
		    height: 90,        
		    x: 700,
		    y: 360,
			items: [
			        NS.labFirma1, 
			        NS.cmbFirma1,   
			        NS.labFirma2,
			        NS.cmbFirma2  
			        ]
		});      
  
	NS.ContenedorGeneral = new Ext.form.FieldSet({
		layout: 'absolute',
		width: 940,
	    height: 430,      
		items: [
		        NS.labDivisaVenta,
		        NS.txtDivisaVenta,
		        NS.cmbDivisaVenta,
		        NS.labBancoCargo,
		        NS.txtBancoCargo,
		        NS.cmbBancoCargo,
		        NS.labChequeraCargo,
		        NS.cmbChequeraCargo,
		        NS.labDivisaCompra,
		        NS.txtDivisaCompra,
		        NS.cmbDivisaCompra,
		        NS.labBancoAbono,
		        NS.txtBancoAbono,
		        NS.cmbBancoAbono,
		        NS.labChequeraAbono,
		        NS.cmbChequeraAbono,
		        NS.ContFormaPago,
		        NS.labReferencia,
		        NS.ContCustodiaFondoFijo,
		        NS.labTipoCambio,
		        NS.txtTipoCambio,
		        NS.labImporteVenta,
		        NS.txtImporteVenta,
		        NS.labImporteCompra,
		        NS.txtImporteCompra,
		        NS.labCasaCambio,
		        NS.txtCasaCambio,
		        NS.cmbCasaCambio,
		        NS.labOperador,
		        NS.txtOperador,
		        NS.cmbOperador,
		        NS.labGrupo,
		        NS.txtGrupo,
		        NS.cmbGrupo,
		        NS.labBanco,
		        NS.txtBanco,
		        NS.cmbBanco,
		        NS.labFechaValor,		       
		        NS.txtFechaValor,
		        NS.labRubro,
		        NS.txtRubro,
		        NS.cmbRubro,
		        NS.labChequera,
		        NS.cmbChequera,    
		        NS.txtFechaLiquidacion,
		        NS.labFechaLiquidacion
		       ]
	});    
	
	
	Boton = function() {
		var cmbCasaCambio = "";
		var cmbOperador = "";
		var txtDivisaVenta = new Ext.getCmp(PF + 'txtDivisaVenta').getValue();
		var txtBancoCargo = new Ext.getCmp(PF + 'txtBancoCargo').getValue(NS.txtBancoCargo);
		var cmbChequeraCargo = new Ext.getCmp(PF + 'cmbChequeraCargo').getValue(NS.cmbChequeraCargo);
		var txtDivisaCompra = new Ext.getCmp(PF + 'txtDivisaCompra').getValue(NS.txtDivisaCompra);
		var txtBancoAbono = new Ext.getCmp(PF + 'txtBancoAbono').getValue(NS.txtBancoAbono);
		var cmbChequeraAbono = new Ext.getCmp(PF + 'cmbChequeraAbono').getValue(NS.cmbChequeraAbono);
		var txtTipoCambio = new Ext.getCmp(PF + 'txtTipoCambio').getValue(NS.txtTipoCambio);
		var txtCasaCambio = new Ext.getCmp(PF + 'txtCasaCambio').getValue(NS.txtCasaCambio);
		
		if(txtCasaCambio == '')
			cmbCasaCambio = '';
		else
			cmbCasaCambio = NS.storeCasaCambio.getById(txtCasaCambio).get('descCasaCambio');
		//var cmbCasaCambio = new Ext.getCmp(PF + 'cmbCasaCambio').getText();
		var txtOperador = Ext.getCmp(PF + 'txtOperador').getValue(NS.txtOperador);
		
		if(txtOperador == '')
			cmbOperador = '';   
		else
			cmbOperador = '' + NS.storeOperador.getById(txtOperador).get('nomOperador');
		var txtGrupo = new Ext.getCmp(PF + 'txtGrupo').getValue(NS.txtGrupo);
		var txtBanco = new Ext.getCmp(PF + 'txtBanco').getValue(NS.txtBanco);
		var txtFechaValor = new Ext.getCmp(PF + 'txtFechaValor').getValue(NS.txtFechaValor);
		var txtRubro = new Ext.getCmp(PF + 'txtRubro').getValue(NS.txtRubro);
		var txtImporteCompra = new Ext.getCmp(PF + 'txtImporteCompra').getValue(NS.txtImporteCompra);
		var txtImporteVenta = new Ext.getCmp(PF + 'txtImporteVenta').getValue(NS.txtImporteVenta);
		var cmbChequera = new Ext.getCmp(PF + 'cmbChequera').getValue(NS.cmbChequera);
		var txtFechaLiquidacion = Ext.getCmp(PF + 'txtFechaLiquidacion').getValue();
		var icustodia = Ext.getCmp(PF + 'Radios').getValue();
		NS.iCustodia = parseInt(icustodia.getGroupValue());        
		
		ConfirmacionCargoCtaAction.validaCampos(NS.cliente, NS.idUsuario, 'no_folio_param', NS.noEmpresa, txtDivisaVenta, txtBancoCargo, 
				cmbChequeraCargo, txtDivisaCompra, txtBancoAbono,cmbChequeraAbono, txtTipoCambio, txtCasaCambio, cmbCasaCambio, txtOperador, 
				cmbOperador, txtGrupo,txtBanco, txtFechaValor, txtRubro, txtImporteCompra, txtImporteVenta, cmbChequera, txtFechaLiquidacion, 
				NS.iCustodia, NS.iTieneBanca, function(respuesta, e){   
    		   
			if(respuesta != '') {
    			Ext.Msg.alert('SET', respuesta + '!!');
    		
    			if(respuesta == 'Se realizo correctamente la compra/venta de divisas') limpiar();
			}
    	});
	};
	   
	
	NS.ContBoton = new Ext.form.FieldSet({
		layout: 'absolute',
		width: 1010,
	    height: 490,
		items: [		
		        NS.ContenedorGeneral,
		        {
				    xtype: 'button',
				    text: 'Ejecutar',
				    x: 730,
				    y: 440,
				    width: 80,
				    height: 22,
				    listeners:{     
                  		click:{     
              			   fn:function(e) {
					        	Boton();   
							}
		        		}
		        	}
				},{
				    xtype: 'button',    
				    text: 'Limpiar',
				    x: 840,
				    y: 440,
				    width: 80, 
				    height: 22,     
				    listeners:{
                  		click:{     
              			   fn:function(e){
              			  	limpiar();
              			   }
           			   }
       			   }
				},{
				    xtype: 'button',
				    text: 'Cerrar',
				    x: 620,
				    y: 440,
				    width: 80,
				    height: 22,
				    hidden: true,
				    listeners:{
                  		click:{        
              			   fn:function(e){
								var fecha = new Ext.getCmp(PF + 'txtFechaLiquidacion').getValue(NS.txtFechaLiquidacion);   
								alert(fecha);
								var cam = cambiarFecha(fecha);
								//alert(fecha);
              			   }
           			   }
       			   }
				    
				}
		       ]
	});
	
	
	NS.CompraTransfer = new Ext.form.FormPanel({
	 	title: 'Compra Venta de Divisas',
		width: 1000,
		height: 470,
		frame: true,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'CompraVentaDivisas',
		name: PF+ 'CompraVentaDivisas',
		renderTo: NS.tabContId,
		//html : '<img src="img/formas/Fondo_Form.png">',
		items :[
			    NS.ContBoton
			   ]
  });
	NS.CompraTransfer.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});