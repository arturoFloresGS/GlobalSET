Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Egresos.Divisas.CVTransPropuestasPago');	                                               
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
	
	NS.unformatNumber = function(num) {
		return num.replace(/(,)/g,''); 
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
	 	NS.CompraVentaTransferPropuestasPago.getForm().reset();
	 	NS.storeMovimientos.removeAll();	 	
	 	NS.storeDivisa.removeAll();
	 	NS.storeBanco.removeAll();
	 	NS.storeChequera.removeAll(); 
	 	NS.storeEmpresaPagadora.removeAll();
	 	NS.storeBancoPagadora.removeAll();
	 	NS.storeChequeraPagadora.removeAll();
	 	NS.storeBancoCasaCam.removeAll();
	 	NS.storeChequeraCasaCam.removeAll();	 	
	 	NS.gridMovimientos.getView().refresh();	 	
	 	NS.storeClaveControl.load();
	 	
	};
	
	NS.valida_datos = function(datos){  
		
		this.datos = NS.datos = true;
		var txtBanco =   new Ext.getCmp(PF + 'txtBanco').getValue(NS.txtBanco);
		var cmbBanco =  new Ext.getCmp(PF + 'cmbBanco').getValue(NS.cmbBanco);
		var cmbChequera = new Ext.getCmp(PF + 'cmbChequera').getValue(NS.cmbChequera);
		var txtDivisaPago = new Ext.getCmp(PF + 'txtDivisaPago').getValue(NS.txtDivisaPago);
		var cmbDivisaPago = new Ext.getCmp(PF + 'cmbDivisaPago').getValue(NS.cmbDivisaPago);
		var txtCasaCambio = new Ext.getCmp(PF + 'txtCasaCambio').getValue(NS.txtCasaCambio);
		var cmbCasaCambio = new Ext.getCmp(PF + 'cmbCasaCambio').getValue(NS.cmbCasaCambio);
		var txtBancoCasaCam = new Ext.getCmp(PF + 'txtBancoCasaCam').getValue(NS.txtBancoCasaCam);
		var cmbBancoCasaCam = new Ext.getCmp(PF + 'cmbBancoCasaCam').getValue(NS.cmbBancoCasaCam);
		var cmbChequeraCasaCam = new Ext.getCmp(PF + 'cmbChequeraCasaCam').getValue(NS.cmbChequeraCasaCam);
		var txtOperador = new Ext.getCmp(PF + 'txtOperador').getValue(NS.txtOperador);
		var cmbOperador = new Ext.getCmp(PF + 'txtOperador').getValue(NS.txtOperador);
		var txtTipoCambio = new Ext.getCmp(PF + 'txtTipoCambio').getValue(NS.txtTipoCambio);
		var txtRegistrosSeleccionados = new Ext.getCmp(PF + 'txtRegistrosSeleccionados').getValue(NS.txtRegistrosSeleccionados);
		
		if(txtBanco != "" && cmbBanco != "" && cmbChequera != "" && txtDivisaPago != "" && cmbDivisaPago != ""
			&& txtCasaCambio != "" && cmbCasaCambio != "" && cmbCasaCambio != "" && txtBancoCasaCam != "" && 
			cmbBancoCasaCam != "" && cmbChequeraCasaCam != "" && txtOperador != "" && cmbOperador != "" && txtTipoCambio != "" 
			&& txtRegistrosSeleccionados != ""){
			
			NS.datos = true;
		}
		
		if (txtTipoCambio == ""){
	        Ext.Msg.alert('SET','Debe indicar el tipo de cambio a aplicar al pago...');
	        NS.datos = false;
	        
      	}else if(txtTipoCambio <= 0 ){
            	Ext.Msg.alert('SET','Debe indicar el tipo de cambio a aplicar al pago...');
            	NS.datos = false;
      	}
		
        if (txtOperador == "" || cmbOperador == ""){
            
            Ext.Msg.alert('SET','Debe seleccionar alg�n Operador de la Casa de Cambio');
            NS.datos = false;
		}
        
		if (cmbChequeraCasaCam == ""){
        	Ext.Msg.alert('SET','Tiene que indicar la Chequera de la Casa de Cambio para la Compra de Tranfer...');
        	NS.datos = false;
        }
				
		if (txtBancoCasaCam == "" || cmbBancoCasaCam == "" ){
        	Ext.Msg.alert('SET','Tiene que indicar el Banco de la Casa de Cambio para la Compra de Tranfer...');
        	NS.datos = false;
            
        } 
		
		if (txtCasaCambio == "" || cmbCasaCambio == ""){
       		Ext.Msg.alert('SET','Tiene que indicar la Casa de Cambio para la Compra de Tranfer...');
       		if(cmbDivisaPago != "" && cmbChequera != "" && cmbBanco != ""){
       			NS.datos = false;    
       		}
        }
				  
		if (cmbChequera == ""){ 
	    	Ext.Msg.alert('SET','Tiene que indicar la Chequera de la Empresa...');
	    	NS.datos = false;
	    }
		
		
		if (txtBanco == "" || cmbBanco == ""){
	    	Ext.Msg.alert('SET','Tiene que indicar el Banco de la Empresa...');
	    	NS.datos = false;
	    }
		
		
		if (txtDivisaPago == "" || cmbDivisaPago == ""){
	    	Ext.Msg.alert('SET','Debe seleccionar la divisa para la Transfer...');   
	    	NS.datos = false;
	    	
	    }	   
		
	    if (txtRegistrosSeleccionados <= 0){
	        Ext.Msg.alert('SET','Debe seleccionar al menos un documento de la lista');
	        datos = false;  
	    }        
	            
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
	
	
	NS.labClaveControl = new Ext.form.Label({
		text: 'Clave de Control',
		x: 0,
		y: 5
	});
	
	NS.storeClaveControl = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
 		idUsuario: NS.idUsuario
		},
		paramOrder:['idUsuario'],
		directFn: ConfirmacionCargoCtaAction.claveControl, 
		idProperty: 'noClaveControl', 
		fields: [
			 {name: 'noClaveControl'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeClaveControl, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen claves de control');
				}
			}
		}
	}); 
	
	NS.storeClaveControl.load();

 	NS.cmbClaveControl = new Ext.form.ComboBox({
		store: NS.storeClaveControl,
		id: PF + 'cmbClaveControl',
		name: PF + 'cmbClaveControl',
		x: 110,
		y: 0,
		width: 190, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'noClaveControl',
		displayField: 'noClaveControl',
		autocomplete: true,
		emptyText: 'selecciona la clave',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
 					fn:function(combo, valor) {
 						
 						NS.storeMovimientos.removeAll();
 						NS.storeDivisa.removeAll();
 						
 						Ext.getCmp(PF+'txtDivisa').setValue('');
 						Ext.getCmp(PF+'cmbDivisa').setValue('');
 						
 						
 						
 						Ext.getCmp(PF+'cmbClaveControl').setValue(combo.getValue());
 						NS.storeDivisa.baseParams.noClaveControl = Ext.getCmp(PF+'cmbClaveControl').getValue();
 						NS.storeDivisa.load();
 					}
				}
			
		}
	});
 	
 	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa',
		x: 360,
		y: 5
	});
 	
 	NS.txtDivisa = new Ext.form.TextField({
		id: PF + 'txtDivisa',
        name: PF + 'txtDivisa',
		x: 400,
        y: 0,
        width: 50, 
        tabIndex: 0,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa',NS.cmbDivisa.getId());
					if(valueCombo != null && valueCombo != undefined && valueCombo != '')
						NS.cambiaDivisa(valueCombo);
        		}
        	}
        }
        
	});
 	
 	NS.cambiaDivisa = function(valueCombo){
 		if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa',NS.cmbDivisa.getId());
			
			NS.storeMovimientos.baseParams.noEmpresa = 0;
			NS.storeMovimientos.baseParams.grupo = 0;
			NS.storeMovimientos.baseParams.noClaveControl = Ext.getCmp(PF+'cmbClaveControl').getValue();
			NS.storeMovimientos.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisa').getValue();
			
			NS.storeMovimientos.load();
			 
		}
	}; 

	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false, 
		root: '',
		baseParams: {
			noClaveControl: 0
		},
		paramOrder:['noClaveControl'],
		directFn: ConfirmacionCargoCtaAction.DivisaLlenado, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen divisas');	   		
			} 
		}
	}); 
	
 	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa,
		id: PF + 'cmbDivisa',
		name: PF + 'cmbDivisa',
		x: 460,
		y: 0,
		width: 190, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'selecciona una divisa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{		 
 					fn:function(combo, valor) {  
 						BFwrk.Util.updateComboToTextField(PF+'txtDivisa',NS.cmbDivisa.getId());
 						NS.cambiaDivisa(combo.getValue());
								
						NS.storeMovimientos.baseParams.noEmpresa = 0;
						NS.storeMovimientos.baseParams.grupo = 0;
						NS.storeMovimientos.baseParams.noClaveControl = Ext.getCmp(PF+'cmbClaveControl').getValue();
						NS.storeMovimientos.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisa').getValue();
						
						NS.storeMovimientos.load();
						   
				}
			}	
		}	
		
	});
 	
 	NS.ContenedorCabecera = new Ext.form.FieldSet({
 		layout: 'absolute',
		width: 980,
	    height: 60,
	    items:[
	           NS.labClaveControl,
	           NS.cmbClaveControl,
	           NS.labDivisa,
	           NS.txtDivisa,
	           NS.cmbDivisa	           
	           ]
	           
 	});
 	
 	NS.storeMovimientos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder: ['noEmpresa', 'grupo', 'noClaveControl', 'idDivisa'],
		directFn:  ConfirmacionCargoCtaAction.llenaGridMovimientos, 
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'noProveedor'},
			{name: 'nomProveedor'},			
			{name: 'noDocto'},			
			{name: 'importe'},
			{name: 'importeMn'},
			{name: 'idDivisa'},
			{name: 'formaPago'},
			{name: 'numFormaPago'},
			{name: 'FechaHoy'},
			{name: 'descBanco'},
			{name: 'noBancoPago'},
			{name: 'bancoPago'},    
			{name: 'idChequera'},  
			{name: 'beneficiario'},  
			{name: 'noFolioDet'},  
			{name: 'concepto'},  
			{name: 'observaciones'},  
			{name: 'idBancoBenef'},  
			{name: 'divisaOriginal'},  
			{name: 'dias'},       
			{name: 'importeOriginal'},        
			{name: 'chequeraBenef'},        
			{name: 'idRubro'},
			{name: 'bancoCte'},
			{name: 'bcoPagoCruzado'},
			{name: 'cheqPagoCruzado'},  
			{name: 'divPagoCruzado'},  
			{name: 'chequeraCte'},
			{name: 'idDivisaPago'},
			{name: 'idDivisaCte'},
			{name: 'idContable'}
			
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientos, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET', 'No existen registros');
					Ext.getCmp(PF + 'txtRegistrosSeleccionados').setValue(0);
					Ext.getCmp(PF + 'txtTotal').setValue("0.0000"); 
				}
					   		
			}
		}
	}); 
 	
 	NS.seleccionMovimientos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});     
 	   
 	NS.columnasMovimientos = new Ext.grid.ColumnModel([
	    NS.seleccionMovimientos,
	    {header: 'No Empresa', width: 80, dataIndex: 'noEmpresa', sortable: true},
	    {header: 'Empresa', width: 80, dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'Proveedor', width: 90, dataIndex: 'nomProveedor', sortable: true},
	    {header: 'numProveedor', width: 90, dataIndex: 'noProveedor', sortable: true, hidden: true}, 	                                    	    
	    {header: 'No Docto', width: 90, dataIndex: 'noDocto', sortable: true},
	    {header: 'Importe', width: 70, dataIndex: 'importe', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Importe MN', width: 70, dataIndex: 'importeMn', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Divisa', width: 90, dataIndex: 'idDivisa', sortable: true},
	    {header: 'Forma de Pago', width: 130, dataIndex: 'formaPago', sortable: true},
	    {header: 'No Forma de Pago', width: 130, dataIndex: 'numFormaPago', sortable: true, hidden: true},       
	    {header: 'Fecha Propuesta', width: 100, dataIndex: 'FechaHoy', sortable: true},   
	    {header: 'Concepto', width: 100, dataIndex: 'concepto', sortable: true},   
	    {header: 'Observaciones', width: 100, dataIndex: 'observaciones', sortable: true},   
	    {header: 'Pago Cruzado', width: 100, dataIndex: 'bcoPagoCruzado', sortable: true, hidden: true},   
	    {header: 'Pago Cruzado Cheque', width: 170, dataIndex: 'cheqPagoCruzado', sortable: true, hidden: true},   
	    {header: 'Pago Cruzado 2', width: 170, dataIndex: 'divPagoCruzado', sortable: true, hidden: true},   
	    {header: 'Importe Pago Crusado', width: 170, dataIndex: 'impPagoCruzado', sortable: true, hidden: true},   
	    {header: 'Divisa Pago', width: 170, dataIndex: 'idDivisaPago', sortable: true, hidden: true},   
	    {header: 'Divisa Cliente', width: 170, dataIndex: 'idDivisaCte', sortable: true, hidden: true},   
	    {header: 'Contable', width: 170, dataIndex: 'idContable', sortable: true, hidden: true},   
	    {header: 'No Banco Benef', width: 100, dataIndex: 'idBancoBenef', sortable: true},   
	    {header: 'Banco de Pago', width: 250, dataIndex: 'bancoPago', sortable: true, hidden: true},
	    {header: 'No Banco de Pago', width: 250, dataIndex: 'noBancoPago', sortable: true, hidden: true},
	    {header: 'Chequera de Pago', width: 250, dataIndex: 'idChequera', sortable: true, hidden: true},    
	    {header: 'No Rubro', width: 250, dataIndex: 'idRubro', sortable: true, hidden: true},
	    {header: 'Beneficiario', width: 250, dataIndex: 'beneficiario', sortable: true},
	    {header: 'No Folio Det', width: 250, dataIndex: 'noFolioDet', sortable: true},
	    {header: 'Banco Cliente', width: 250, dataIndex: 'bancoCte', sortable: true, hidden: true},
	    {header: 'Chequera Cliente', width: 250, dataIndex: 'chequeraCte', sortable: true, hidden: true},
	    {header: 'Divisa Original', width: 250, dataIndex: 'divisaOriginal', sortable: true, hidden: true},
	    {header: 'Chequera Benef', width: 250, dataIndex: 'chequeraBenef', sortable: true},
	    {header: 'Dias', width: 250, dataIndex: 'dias', sortable: true},
	    {header: 'Importe Original', width: 250, dataIndex: 'importeOriginal', sortable: true, renderer: BFwrk.Util.rendererMoney}
 	                                    	   
	]);         
 	
	NS.gridMovimientos = new Ext.grid.GridPanel({
		store: NS.storeMovimientos,
		id: 'gridMovimientos',
		x: 10,
		y: 0,
		cm: NS.columnasMovimientos,
		sm: NS.seleccionMovimientos,
		width: 900,
	    height: 200,
	    stripeRows: true,
	    columnLines: true,
	    title: 'Movimientos',
		listeners: {
			click: {
				fn:function(grid) {
					      
					var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
					
					Ext.getCmp(PF + 'txtRegistrosSeleccionados').setValue(regSelec.length);
	        				NS.MuestraDatos();
	  
	        			}
	        		}
	        	}
	}); 
 	                                    	
 	                                    	
 	NS.MuestraDatos = function(){
 		
 		var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();
 		 		
 		if(regSelec.length>0){
 			var totalImporte=0;
 			
 			for(var k=0;k<regSelec.length;k=k+1){
				totalImporte = totalImporte + regSelec[k].get('importe');
				Ext.getCmp(PF + 'txtTotal').setValue(totalImporte.toFixed(4));
				var Total = Ext.getCmp(PF + 'txtTotal').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtTotal').getValue())); 
			}
 		
 		}else{

			Ext.getCmp(PF + 'txtTotal').setValue("0.00");

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
 	     tabIndex: 0,
 	     readOnly: true
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
		readOnly: true,
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
 	
 	NS.ContenedorPanel = new Ext.form.FieldSet({   
		layout: 'absolute',
		title: 'Movimientos',
		width: 980,
	    height: 300, 
	    x: 0,
	    y: 60,
	    
	   items:[
	       NS.gridMovimientos,
	       NS.txtRegistrosSeleccionados,
	       NS.labRegistrosSeleccionados,
	       NS.labTotal,
	       NS.txtTotal	           
       ]
	           
 	});
 	
 	NS.labDivisaPago = new Ext.form.Label({
 		text: 'Divisa de Pago',
	     x: 0,
	     y: 5
 	});
 	
 	NS.txtDivisaPago = new Ext.form.TextField({
	     id: PF + 'txtDivisaPago',
	     name:PF + 'txtDivisaPago',
	     x: 95,
	     y: 0, 
	     width: 50, 
	     tabIndex: 0,
	     listeners : {
 			change : {
    			fn: function(caja, valor){
    				if( BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisaPago',NS.cmbDivisaPago.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de divisa no valido.');
						return; 
					}					
				
					NS.accionarDivisaPago(caja.getValue());
    			}
    		}
 		}   
    });
 	
 	NS.accionarDivisaPago = function(valueCombo){
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisaPago',NS.cmbDivisaPago.getId());
			
			NS.storeBanco.baseParams.nomEmpresa = Ext.getCmp(PF+'cmbClaveControl').getValue();
			NS.storeBanco.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisaPago').getValue();
			NS.storeBanco.load();
		}
	};			

 	
 	NS.storeDivisaPago = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
 		idUsuario: NS.idUsuario
		},
		paramOrder:['idUsuario'],
		directFn: ConfirmacionCargoCtaAction.obtenerDivisaEmpresa, 
		idProperty: 'idDivisa',    
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisaPago, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existe divisa de pago');
				}
					   		
			}
		}
	}); 
 	
 	NS.storeDivisaPago.load();
 	

 	NS.cmbDivisaPago = new Ext.form.ComboBox({
		store: NS.storeDivisaPago,
		id: PF + 'cmbDivisaPago',
		name: PF + 'cmbDivisaPago', 
		x: 155,
		y: 0,
		width: 190, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'selecciona una divisa de pago',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
 				fn:function(combo, valor) {
 					
 					if( Ext.getCmp(PF+'cmbClaveControl').getValue() == '' ){
    					Ext.Msg.alert('SET','No ha seleccionado una propuesta');  
    					Ext.getCmp(PF+'txtDivisaPago').setValue('');
    					Ext.getCmp(PF+'cmbDivisaPago').setValue('');
    					return; 
    				}
 					
 					BFwrk.Util.updateComboToTextField(PF+'txtDivisaPago',NS.cmbDivisaPago.getId());
 					NS.accionarDivisaPago(combo.getValue());
 					
 					NS.storeBanco.baseParams.nomEmpresa = Ext.getCmp(PF+'cmbClaveControl').getValue();
 					NS.storeBanco.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisaPago').getValue();
 					NS.storeBanco.load();
 					
 				}	
			}
			
		}
	});
 	
 	NS.labBanco = new Ext.form.Label({
		text: 'Banco',
		x: 360,
		y: 5
	});
 	
 	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
        name:PF + 'txtBanco',
		x: 400,
        y: 0,
        width: 50, 
        tabIndex: 0,
        listeners:{
			change:{
				fn: function(caja,valor){
					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de banco no valido.');
						return; 
					}					
				
					NS.accionarBancoPago(caja.getValue());
                }
			}
    	}
	});
 	
 	NS.cambiaBanco = function(comboValue){
		NS.cmbBanco.reset();
		NS.storeBanco.removeAll();
				
		if(comboValue != null && comboValue != undefined && comboValue != '') {		 
			
			NS.storeBanco.baseParams.nomEmpresa = NS.noEmpresa;
			NS.storeBanco.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisaPago').getValue();
			NS.storeBanco.load();
			
		}else {
    		Ext.getCmp(PF + 'txtDivisaPago').reset(); 
    		NS.cmbBanco.reset();
		}

	}; 
 	
	NS.accionarBancoPago = function(comboValue){
		if(comboValue != null && comboValue != undefined && comboValue != '') {
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId());
			
			NS.storeChequera.baseParams.idBancoCasa = parseInt(Ext.getCmp(PF+'txtBanco').getValue()); 
			NS.storeChequera.baseParams.noEmpresa = Ext.getCmp(PF+'cmbClaveControl').getValue();
			NS.storeChequera.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisaPago').getValue();
			
		    NS.storeChequera.load();
		}
	}; 
 	
	
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
		nomEmpresa: '',
		idDivisa: Ext.getCmp(PF+'txtDivisaPago').getValue()
		},
		paramOrder:['nomEmpresa', 'idDivisa'],
		directFn: ConfirmacionCargoCtaAction.obtenerBancoEmpresa,  
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
					Ext.Msg.alert('SET','No existen bancos');
				}
					   		
			}
		}
	}); 
	

 	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 460,
		y: 0,
		width: 190, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'selecciona un Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
 				fn:function(combo, valor) {
					Ext.getCmp(PF+'cmbBanco').setValue(combo.getValue());
					BFwrk.Util.updateComboToTextField(PF+'txtBanco',NS.cmbBanco.getId());
				    NS.cmbChequera.reset();
					
				    NS.storeChequera.baseParams.idBancoCasa = parseInt(Ext.getCmp(PF+'txtBanco').getValue()); 
					NS.storeChequera.baseParams.noEmpresa = Ext.getCmp(PF+'cmbClaveControl').getValue();
					NS.storeChequera.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisaPago').getValue();
				    NS.storeChequera.load();
					
 				}	
			}
			
		}
	});
 	
 	
 	NS.labChequera = new Ext.form.Label({
		text: 'Chequera',
		x: 660,
		y: 5
	});
	
 	
 	NS.storeChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder:['idBancoCasa','noEmpresa','idDivisa'],
		directFn: ConfirmacionCargoCtaAction.obtenerChequeraEmp, 
		idProperty: 'idChequera', 
		fields: [ 
			 {name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
			 
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen chequeras');
				}
					   		
			}
		}
	}); 

 	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera,
		id: PF + 'cmbChequera',
		name: PF + 'cmbChequera',
		x: 730,
		y: 0,
		width: 190, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'idChequera',
		displayField: 'idChequera',
		autocomplete: true,
		emptyText: 'selecciona una Chequera',
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
 	
 	NS.ContenedorEmpresa = new Ext.form.FieldSet({
		layout: 'absolute',
		title: 'Empresa',
		width: 980,
	    height: 80, 
	    x: 0,
	    y: 360,
	    items:[
	           NS.labDivisaPago,
	           NS.txtDivisaPago,
	           NS.cmbDivisaPago,
	           NS.labBanco,
	           NS.txtBanco,
	           NS.cmbBanco,
	           NS.labChequera,
	           NS.cmbChequera	           
	          ]
 	});
 	
 	NS.labDivisaEmpPagadora = new Ext.form.Label({
		text: 'Divisa',
		x: 0,
		y: 5
	});
	
	NS.storeDivisaEmpPagadora = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
 		noEmpresa: NS.noEmpresa
		},
		paramOrder:['noEmpresa'],
		directFn: ConfirmacionCargoCtaAction.obtenerDivisaPago,  
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'}, 
			 {name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisaEmpPagadora, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existe divisa para la empresa');
				}
					   		
			}
		}
	}); 
	
	NS.storeDivisaEmpPagadora.load(); 
	

 	NS.cmbDivisaEmpPagadora = new Ext.form.ComboBox({
		store: NS.storeDivisaEmpPagadora,
		id: PF + 'cmbDivisaEmpPagadora',
		name: PF + 'cmbDivisaEmpPagadora',
		x: 50,
		y: 0,
		width: 150, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'selecciona una Divisa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{		 
				fn:function(combo, valor) {
	 				Ext.getCmp(PF+'cmbDivisaEmpPagadora').setValue(combo.getValue());
	 				NS.storeEmpresaPagadora.baseParams.idUsuario = NS.idUsuario;
	 				NS.storeEmpresaPagadora.load();
				}
			}	
		}	
	});
 	
 	 
 	
 	NS.labEmpresaPagadora = new Ext.form.Label({
		text: 'Empresa',
		x: 210,
		y: 5
	});
	
 	NS.storeEmpresaPagadora = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder:['idUsuario'],
		directFn: ConfirmacionCargoCtaAction.obtenerEmpresa, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresaPagadora, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen empresas pagadoras');
				}
					   		
			}
		}
	});

 	NS.cmbEmpresaPagadora = new Ext.form.ComboBox({
		store: NS.storeEmpresaPagadora,
		id: PF + 'cmbEmpresaPagadora',
		name: PF + 'cmbEmpresaPagadora',
		x: 280,
		y: 0,
		width: 150,  
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'selecciona una Empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					Ext.getCmp(PF+'cmbEmpresaPagadora').setValue(combo.getValue());
					NS.storeBancoPagadora.baseParams.noEmpresa = Ext.getCmp(PF+'cmbEmpresaPagadora').getValue();
					NS.storeBancoPagadora.baseParams.idDivisa = Ext.getCmp(PF+'cmbDivisaEmpPagadora').getValue();
					NS.storeBancoPagadora.load();
					
				}
			}
		}
	});
 	
 	
 	
 	NS.labBancoPagadora = new Ext.form.Label({
		text: 'Banco',
		x: 440,
		y: 5
	});
	
	NS.storeBancoPagadora = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder:['noEmpresa','idDivisa'],
		directFn: ConfirmacionCargoCtaAction.BancoLlenado,
		idProperty: 'idBanco', 
		fields: [
		         {name: 'idBanco'},
		         {name: 'descBanco'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoPagadora, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen bancos pagadores');
				}
					   		
			}
		}
	}); 

 	NS.cmbBancoPagadora = new Ext.form.ComboBox({
		store: NS.storeBancoPagadora, 
		id: PF + 'cmbBancoPagadora',
		name: PF + 'cmbBancoPagadora',
		x: 490,
		y: 0,
		width: 150, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'selecciona un Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
 					fn:function(combo, valor) {
 						NS.cmbChequeraPagadora.reset();
 						Ext.getCmp(PF+'cmbBancoPagadora').setValue(combo.getValue());
 						NS.storeChequeraPagadora.baseParams.noEmpresa = Ext.getCmp(PF+'cmbEmpresaPagadora').getValue();
 						NS.storeChequeraPagadora.baseParams.idDivisa = Ext.getCmp(PF+'cmbDivisaEmpPagadora').getValue();
 						NS.storeChequeraPagadora.baseParams.idBanco = Ext.getCmp(PF+'cmbBancoPagadora').getValue();
 						NS.storeChequeraPagadora.load();
					}
				
			}
			
		}
	});
 	
 	NS.labChequeraPagadora = new Ext.form.Label({
		text: 'Chequera',
		x: 650,
		y: 5
	});
	
	NS.storeChequeraPagadora = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
 		idUsuario: NS.idUsuario
		},
		paramOrder:['noEmpresa','idDivisa','idBanco'],
		directFn: ConfirmacionCargoCtaAction.ObtenerChequera, 
		idProperty: 'idChequera', 
		fields: [
			 {name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraPagadora, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen chequeras pagadoras');
				}
					   		
			}
		}
	}); 

 	NS.cmbChequeraPagadora = new Ext.form.ComboBox({
		store: NS.storeChequeraPagadora,
		id: PF + 'cmbChequeraPagadora',
		name: PF + 'cmbChequeraPagadora',
		x: 720,
		y: 0,
		width: 150, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'idChequera',
		displayField: 'idChequera',
		autocomplete: true,
		emptyText: 'selecciona una chequera',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					Ext.getCmp(PF+'cmbChequeraPagadora').setValue(combo.getValue());
				}
 			}
 		}
	});
 	
 	NS.ContenedorEmpresaPagadora = new Ext.form.FieldSet({
		layout: 'absolute',
		title: 'Empresa Pagadora',
		width: 980,
	    height: 80, 
	    hidden: true,
	    x: 0,
	    y: 60,
	    items:[
	           NS.labDivisaEmpPagadora,
	           NS.cmbDivisaEmpPagadora,
	           NS.labEmpresaPagadora,
	           NS.cmbEmpresaPagadora,
	           NS.labBancoPagadora,
	           NS.cmbBancoPagadora,
	           NS.labChequeraPagadora,
	           NS.cmbChequeraPagadora
	          ]
 	});
 	
 	NS.labCasaCambio = new Ext.form.Label({
		text: 'Casa de Cambio',
		x: 0,
		y: 0
	});
 	
 	NS.txtCasaCambio = new Ext.form.TextField({
		id: PF + 'txtCasaCambio',
        name:PF + 'txtCasaCambio',
		x: 0,
        y: 20,   
        width: 50, 
        tabIndex: 0,
        listeners:{
        	change:{
        		fn:function(caja, valor){
        			if( BFwrk.Util.updateTextFieldToCombo(PF+'txtCasaCambio',NS.cmbCasaCambio.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de Casa de Cambio no valido.');
						return; 
					}					
						
					NS.accionarCasaCambio(caja.getValue());
        			
        		}
        	}
        }
	});
 	
	
	NS.accionarCasaCambio = function(comboValue){
		if(comboValue != null && comboValue != undefined && comboValue != '') {
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtCasaCambio',NS.cmbCasaCambio.getId());
			NS.storeBancoCasaCam.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisaPago').getValue();
			NS.storeBancoCasaCam.baseParams.idCasaCambio = parseInt(Ext.getCmp(PF+'txtCasaCambio').getValue());
			NS.storeBancoCasaCam.load();
			
		}
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
			 {name: 'descCasaCambio'},
			 //{name: 'contacto'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCasaCambio, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen casas de cambio');
				}
					   		
			}
		}
	}); 
 	
 	NS.storeCasaCambio.load();

 	NS.cmbCasaCambio = new Ext.form.ComboBox({
		store: NS.storeCasaCambio,
		id: PF + 'cmbCasaCambio',
		name: PF + 'cmbCasaCambio',
		x: 60,
		y: 20,
		width: 250, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1, 
		valueField: 'idCasaCambio',
		displayField: 'descCasaCambio',
		autocomplete: true,
		emptyText: 'selecciona una casa de cambio',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					
					if( Ext.getCmp(PF+'cmbDivisaPago').getValue() == '' ){
    					Ext.Msg.alert('SET','No ha seleccionado una Divisa de Pago');  
    					Ext.getCmp(PF+'txtCasaCambio').setValue('');
    					Ext.getCmp(PF+'cmbCasaCambio').setValue('');
    					return; 
    				}
					
					//NS.txtOperador.setValue( NS.storeCasaCambio.getById(combo.getValue()).get('contacto') );
					
 					Ext.getCmp(PF+'cmbCasaCambio').setValue(combo.getValue());
					BFwrk.Util.updateComboToTextField(PF+'txtCasaCambio',NS.cmbCasaCambio.getId());
					NS.storeBancoCasaCam.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisaPago').getValue();
					NS.storeBancoCasaCam.baseParams.idCasaCambio = parseInt(Ext.getCmp(PF+'txtCasaCambio').getValue());
					NS.storeBancoCasaCam.load();
				}	 
			}
		}
	});
 	
 	NS.labBancoCasaCam = new Ext.form.Label({
		text: 'Banco',
		x: 330,
		y: 0
	});
 	
 	NS.txtBancoCasaCam = new Ext.form.TextField({
		id: PF + 'txtBancoCasaCam',
        name:PF + 'txtBancoCasaCam',
		x: 330,
        y: 20,
        width: 50, 
        tabIndex: 0,
        listeners:{
			change:{
				fn: function(caja,valor){
					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoCasaCam',NS.cmbBancoCasaCam.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de Banco no valido.');
						return; 
					}					
						
					NS.accionarBancoCasaCambio(caja.getValue());
                }
			}
    	}
	});
 	
 	NS.accionarBancoCasaCambio = function( comboValue ){
 		if(comboValue != null && comboValue != undefined && comboValue != '') {
			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoCasaCam',NS.cmbBancoCasaCam.getId()); 
			NS.storeChequeraCasaCam.baseParams.idCasaCambio = parseInt(Ext.getCmp(PF+'txtCasaCambio').getValue());
		    NS.storeChequeraCasaCam.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisaPago').getValue();
			NS.storeChequeraCasaCam.baseParams.idBancoCasa = parseInt(Ext.getCmp(PF+'txtBancoCasaCam').getValue());
			  
			NS.storeChequeraCasaCam.load();
 		}
 	};
 	
 	NS.cambiaBancoCasa = function(comboValue){
		NS.cmbBancoCasaCam.reset();
		NS.storeBancoCasaCam.removeAll();
		
		if(comboValue != null && comboValue != undefined && comboValue != '') {		 
			NS.storeBancoCasaCam.load();
		}else {
    		Ext.getCmp(PF + 'txtBancoCasaCam').reset(); 
    		NS.cmbBancoCasaCam.reset();
		}

	}; 
		
 	NS.storeBancoCasaCam = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder:['idCasaCambio','idDivisa'],
		directFn: ConfirmacionCargoCtaAction.obtenerBanco, 
		idProperty: 'idBanco', 
		fields: [
		         {name: 'idBanco'},
				 {name: 'descBanco'}
		         ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoCasaCam, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen bancos de casa de cambio');
				}
					   		
			}
		} 
	}); 

 	NS.cmbBancoCasaCam = new Ext.form.ComboBox({
		store: NS.storeBancoCasaCam,
		id: PF + 'cmbBancoCasaCam',
		name: PF + 'cmbBancoCasaCam', 
		x: 390,
		y: 20,
		width: 250, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1, 
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'selecciona un Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
 				fn:function(combo, valor) {
					Ext.getCmp(PF+'cmbBancoCasaCam').setValue(combo.getValue());
					BFwrk.Util.updateComboToTextField(PF+'txtBancoCasaCam',NS.cmbBancoCasaCam.getId());
					  
				    NS.storeChequeraCasaCam.baseParams.idCasaCambio = parseInt(Ext.getCmp(PF+'txtCasaCambio').getValue());
				    NS.storeChequeraCasaCam.baseParams.idDivisa = Ext.getCmp(PF+'txtDivisaPago').getValue();
					NS.storeChequeraCasaCam.baseParams.idBancoCasa = parseInt(Ext.getCmp(PF+'txtBancoCasaCam').getValue());
					  
					NS.storeChequeraCasaCam.load();
 				}	
			}
			
		}
	});
 	
 	NS.labChequeraCasaCam = new Ext.form.Label({
		text: 'Chequera',
		x: 650,
		y: 0
	});
 	
 	NS.storeChequeraCasaCam = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
		},
		paramOrder:['idCasaCambio','idDivisa','idBancoCasa'],
		directFn: ConfirmacionCargoCtaAction.obtenerChequeraTotal, 
		idProperty: 'idChequera', 
		fields: [
			 {name: 'idChequera'}, 
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraCasaCam, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen chequeras de casa de cambio');
				}
					   		
			}
		} 
	}); 

 	NS.cmbChequeraCasaCam = new Ext.form.ComboBox({
		store: NS.storeChequeraCasaCam,
		id: PF + 'cmbChequeraCasaCam',
		name: PF + 'cmbChequeraCasaCam', 
		x: 650,
		y: 20,
		width: 250, 
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1, 
		valueField: 'idChequera',
		displayField: 'idChequera',
		autocomplete: true,
		emptyText: 'selecciona una Chequera',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
 				fn:function(combo, valor) {
 					Ext.getCmp(PF+'cmbChequeraCasaCam').setValue(combo.getValue()); 					
 	            }
 					
 			}
		}
	});
 	
 	
 	
 	NS.labOperador = new Ext.form.Label({
		text: 'Operador',
		x: 0,
		y: 52
	});
 	
 	
 	NS.txtOperador = new Ext.form.TextField({
		id: PF + 'txtOperador',    
        name:PF + 'txtOperador',
        x: 0,
        y: 75,
        width: 300, 
        tabIndex: 0,
        maskRe:/[a-z]|[A-Z]|\s/,
        listeners : {
 		change : {
    		fn: function(caja, valor){     
 				
				if(caja.getValue()===''){					
					return; 
				}		
				
				NS.txtOperador.setValue(caja.getValue().toUpperCase());
 	}
    		}
 		}
        
	});
 	 	
 	
 	
 	
 	
 	NS.labTipoCambio = new Ext.form.Label({
		text: 'Tipo de Cambio',
		x: 330,
		y: 80
	});
 	
 	NS.txtTipoCambio = new Ext.form.TextField({
		id: PF + 'txtTipoCambio',
        name:PF + 'txtTipoCambio',
		x: 430,
        y: 75,
        width: 120, 
        tabIndex: 0,
        listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						
						var TipoCambio = Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtTipoCambio').getValue()));
						var cambio = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtTipoCambio').getValue()));
						var total = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtTotal').getValue()));    
						var totalTC = cambio * total;
						totalTC = totalTC.toFixed(4)
						
						//var Importe = Ext.getCmp(PF + 'txtImporte').setValue(NS.formatNumber(cambio * total));
						if (Ext.getCmp(PF + 'txtTotal').getValue()!=null && Ext.getCmp(PF + 'txtTotal').getValue()!='' && Ext.getCmp(PF + 'txtTotal').getValue()!='0.00')
							Ext.getCmp(PF + 'txtImporte').setValue( NS.formatNumber(totalTC) );
						else {
							Ext.getCmp(PF + 'txtImporte').setValue('');
							Ext.getCmp(PF + 'txtTipoCambio').setValue('');
						}
					}
				}
			}
		}
	});
 	
 	NS.labImporte = new Ext.form.Label({
		text: 'Importe',
		x: 660,
		y: 80
	});
 	
 	NS.txtImporte = new Ext.form.TextField({
		id: PF + 'txtImporte',
        name:PF + 'txtImporte',
		x: 720,
        y: 75,
        width: 120,    
        tabIndex: 0
 	});
 	
 	
 	NS.ContenedorCasaCambio = new Ext.form.FieldSet({
		layout: 'absolute',
		title: 'Casa de Cambio',
		width: 980,
	    height: 160,
	    hidden: false,
	    x: 0,
	    y: 450,
	    items:[
	           NS.labCasaCambio,   
	           NS.txtCasaCambio,
	           NS.cmbCasaCambio,
	           NS.labBancoCasaCam,
	           NS.txtBancoCasaCam,
	           NS.cmbBancoCasaCam,
	           NS.labChequeraCasaCam,
	           NS.cmbChequeraCasaCam,
	           NS.labOperador,
	           NS.txtOperador,
	           NS.labTipoCambio,
	           NS.txtTipoCambio,
	           NS.labImporte,
	           NS.txtImporte
	          ] 
 	}); 
 	
 	
 	NS.boton = function() {
 		
 		var psNoProveedor  = '';
 		var psNomProveedor  = '';
 		var psFolioPadre   = ''; 
 		var psFoliosHijo   = '';		
 		var pdTotalPago  = 0; 		
 	   	var plNumRegistros = 0;
 	   	var idBancoProveedor = '';
 	   	var descBancoProveedor = '';
 	   	var chequeraProveedor = ''; 
 	   	
 		var regSelec = NS.gridMovimientos.getSelectionModel().getSelections();   
 		
 		Ext.each( regSelec, function(registro){
 			
 			    psNoProveedor  = '';
				psNomProveedor = '';
				idBancoProveedor = '';
		 	   	descBancoProveedor = '';
		 	   	chequeraProveedor = ''; 
		 		psFolioPadre   = ''; 
		 		psFoliosHijo   = '';		
		 		pdTotalPago  = 0; 		
		 	   	plNumRegistros = 0;
 			
 			if( psNoProveedor == '' ){
 				
 				plNumRegistros ++;
 				psNoProveedor 		= registro.get('noProveedor');
 				psNomProveedor		= registro.get('beneficiario');
 				
 				idBancoProveedor 	= registro.get('idBancoBenef');
			 	descBancoProveedor 	= registro.get('descBanco');
			 	chequeraProveedor 	= registro.get('chequeraBenef');
			 	
 				psFolioPadre  		= registro.get('noFolioDet');
 				pdTotalPago   		= pdTotalPago + registro.get('importe'); 				
 				psFoliosHijo  		= registro.get('noFolioDet') + ",";
 				
 			}else{
 				
 				if( psNoProveedor == registro.get('noProveedor') ){
 					
 					plNumRegistros ++;
 					pdTotalPago   = pdTotalPago + registro.get('importe'); 				
 	 				psFoliosHijo  = registro.get('noFolioDet') + ",";
 					
 				}else{
 					
 					if(psFoliosHijo != ''){
 						NS.ProcesoCpaTransfer( plNumRegistros, psNoProveedor, psNomProveedor,
 											   idBancoProveedor, descBancoProveedor, chequeraProveedor,
 								              psFolioPadre, pdTotalPago, psFoliosHijo);
 					}
 					
 					
 			 	   	
 			 	   	plNumRegistros ++;
 	 				psNoProveedor = registro.get('noProveedor');
 	 				psNomProveedor= registro.get('beneficiario');
 	 				
 	 				idBancoProveedor 	= registro.get('idBancoBenef');
 				 	descBancoProveedor 	= registro.get('descBanco');
 				 	chequeraProveedor 	= registro.get('chequeraBenef');
 	 				
 	 				psFolioPadre  = registro.get('noFolioDet');
 	 				pdTotalPago   = pdTotalPago + registro.get('importe'); 				
 	 				psFoliosHijo  = registro.get('noFolioDet') + ",";
 					
 				}
 				
 			}
 			//END IF:psNoProveedor == ''
 			
 			if(psFoliosHijo != '') {
 				NS.ProcesoCpaTransfer(  plNumRegistros  , psNoProveedor		, psNomProveedor,
				 						idBancoProveedor, descBancoProveedor, chequeraProveedor,
				 						psFolioPadre    , pdTotalPago		, psFoliosHijo);  	 	      	 
 			}
 		
 		});//END FOR EACH
 		
 			
 	};//END METHOD: NS.boton
 	
 	NS.ProcesoCpaTransfer = function( plNumRegistros, psNoProveedor, psNomProveedor,
 									  idBancoProveedor, descBancoProveedor, chequeraProveedor,
 									  psFolioPadre, pdTotalPagoTrans, psFoliosHijo){
 		
 		var idCasaDeCambio 		 = Ext.getCmp(PF+'txtCasaCambio').getValue();
 		var nomCasaDeCambio      = (idCasaDeCambio == '' )? '': NS.storeCasaCambio.getById( idCasaDeCambio ).get('descCasaCambio'); 
 		var idBancoCasaDeCambio  = Ext.getCmp(PF+'txtBancoCasaCam').getValue();
 		var nomBancoCasaDeCambio = ( idBancoCasaDeCambio == '' ) ? '': NS.storeBancoCasaCam.getById( idBancoCasaDeCambio ).get('descBanco'); 				
 		var chequeraCadaDeCambio = Ext.getCmp(PF+'cmbChequeraCasaCam').getValue(); 
		var tipoDeCambio         = (Ext.getCmp(PF+'txtTipoCambio').getValue()=='')? 0 : parseFloat(NS.unformatNumber(Ext.getCmp(PF+'txtTipoCambio').getValue()));
		var idDivisaTransfer     = Ext.getCmp(PF + 'txtDivisa').getValue();
		var descDivisaTransfer   = (idDivisaTransfer=='')?'': NS.storeDivisa.getById(idDivisaTransfer).get('descDivisa');
		var idBancoPagador 		 = Ext.getCmp(PF+'cmbBanco').getValue(); 
		var nomBancoPagador      = ( idBancoPagador == '' )? '': NS.storeBanco.getById(idBancoPagador).get('descBanco');
 	    var idChequeraPagadora   = Ext.getCmp(PF + 'cmbChequera').getValue();
 	    var idDivisaDePago       = Ext.getCmp(PF+'txtDivisaPago').getValue();
 	    var descDivisaPago       = ( idDivisaDePago == '' )?'':NS.storeDivisaPago.getById(idDivisaDePago).get('descDivisa');
 	    
 	    
 	    psFolioPadre 		 = psFolioPadre.toString();
	    psFoliosHijo 		 = psFoliosHijo.toString();
 	    plNumRegistros 		 = plNumRegistros.toString(); 	    
 	    psNoProveedor 		 = psNoProveedor.toString();
 	    psNomProveedor 		 = psNomProveedor.toString();
 	    
 	   idBancoProveedor      = idBancoProveedor.toString();
 	   descBancoProveedor    = descBancoProveedor.toString();
 	   chequeraProveedor     = chequeraProveedor.toString();
 	    
 	    idDivisaTransfer     = idDivisaTransfer.toString();
		descDivisaTransfer   = descDivisaTransfer.toString();
 	    pdTotalPagoTrans 	 = pdTotalPagoTrans.toString();
 	    
 	    idCasaDeCambio 		 = idCasaDeCambio.toString();
		nomCasaDeCambio      = nomCasaDeCambio.toString();
		idBancoCasaDeCambio  = idBancoCasaDeCambio.toString();
		nomBancoCasaDeCambio = nomBancoCasaDeCambio.toString(); 				
		chequeraCadaDeCambio = chequeraCadaDeCambio.toString();
		tipoDeCambio         = tipoDeCambio.toString();
		
		idBancoPagador 		 = idBancoPagador.toString();
		nomBancoPagador      = nomBancoPagador.toString();
	    idChequeraPagadora   = idChequeraPagadora.toString();
	    idDivisaDePago       = idDivisaDePago.toString();
	    descDivisaPago       = descDivisaPago.toString();
	    /*alert(psFolioPadre);
	    alert(psFoliosHijo);
	    alert(plNumRegistros);*/
	    ConfirmacionCargoCtaAction.ejecutarCompraDeTransfer(
			psFolioPadre 		 , psFoliosHijo 		 , plNumRegistros 		 , 	    
	 	    psNoProveedor 		 , psNomProveedor 		 , idDivisaTransfer      ,
	 	    idBancoProveedor     , descBancoProveedor    , chequeraProveedor,
			descDivisaTransfer   , pdTotalPagoTrans 	 , idCasaDeCambio 		 ,
			nomCasaDeCambio      , idBancoCasaDeCambio   , nomBancoCasaDeCambio  , 				
			chequeraCadaDeCambio , tipoDeCambio          , idBancoPagador 		 ,
			nomBancoPagador      , idChequeraPagadora    , idDivisaDePago        ,
		    descDivisaPago       , NS.idUsuario          , function(response, e ){
				
				var icon = Ext.Msg.INFO; 
				
				if( ! response.resultado ){
					icon = Ext.Msg.WARNING;
				}
				
				Ext.Msg.show({ title:'SET', icon:icon, msg:response.mensaje, buttons:Ext.Msg.OK });
				
				if(response.resultado) {
					limpiar();
					var strParams = ''; 
					strParams = '?nomReporte=ReporteCompraTransfer&folioReporte=' + response.folio ;		    			
					window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
				}
		
		});
	    
		
 	};//END METHOD:NS.ProcesoCpaTransfer 
 	
 	NS.ProcesoCpaVtaDivisas = function(num, cadena, precio) {
 		var plNumRegCliente = num;
 		var psFoliosCli = cadena;
 		var pdTotalCpaVta = precio;
 		
 		 var pi_Banco = '';
 	     var plResult = '';
 	     var ps_chequera = '';
 	     var ps_folios = '';
 	     var pi_folios_rech = 0;
 	     var ps_folios_rech = '';
 	     var ps_folios_acep = '';
 	     var lcl_s_rutalogo = '';
 	     var psDoctoAgrupado = '';
 	     var psTipoEnvio = '';
 	     var plIndiceCom = '';
 	     
  		pi_Banco = Ext.getCmp(PF+'cmbBanco').getValue(); 
 	    ps_chequera = Ext.getCmp(PF+'cmbChequera').getValue(); 	    
 	    	
    	ps_folios = '';
 	    ps_folios = psFoliosCli
 	     	     
    	ConfirmacionCargoCtaAction.Update(psFoliosCli, Ext.getCmp(PF+'txtCasaCambio').getValue(), Ext.getCmp(PF+'txtBancoCasaCam').getValue(),
    			Ext.getCmp(PF+'cmbChequeraCasaCam').getValue(), Ext.getCmp(PF+'txtDivisaPago').getValue(), 
    			parseFloat(NS.unformatNumber(Ext.getCmp(PF+'txtTipoCambio').getValue())), parseFloat(NS.unformatNumber(Ext.getCmp(PF+'txtImporte').getValue())),
    			function(resultado, e){
    		
    		NS.llama_Pagador(pi_Banco, ps_chequera, ps_folios, pi_folios_rech, ps_folios_rech); 	    		
    		
    		
    	});
	};
	
	NS.llama_Pagador = function(ban, chequ, foli, i_foli_rech, s_foli_rech){
				
		pi_Banco = ban;
		var chequera = chequ;
		ps_folios = foli
		pi_folios_rech = i_foli_rech;
		ps_folios_rech = s_foli_rech;
		
	    var sBandera = '';
		var plresp = 0;
	    var ps_Sql = '';
	    var pli_error = '';
	    
	    NS.llama_Pagador = true;
	    
		sBandera = "N"; 
	    	
	    var usuario = NS.idUsuario; 
	    	    	   
	    var pri = 0;
	    var seg = 0;
	    var ter = "";
	    var cua = 1;
	     
	    
	    ConfirmacionCargoCtaAction.ejecutarPagador( sBandera, usuario.toString(), pi_Banco.toString(), 
	    		                                   chequ,pri ,ter ,seg ,ps_folios.toString() ,
	   											   pi_folios_rech.toString(), ps_folios_rech, cua,  
	   											   function(res,e){
	    	var strParams = ''; 
			strParams = '?nomReporte=ReporteCompraTransfer&folioReporte=' + foli ;		    			
			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);

    		
	    });
	    
	};//END FUNCTION: llama_Pagador
 	
 	NS.ContenedorGeneral = new Ext.form.FieldSet({
 		id: 'ContenedorGeneral',
		layout: 'absolute',
		width: 1010,
	    height: 780,
		items: [
		        NS.ContenedorCabecera,
		        NS.ContenedorPanel,
		        NS.ContenedorEmpresa,
		        NS.ContenedorEmpresaPagadora,
		        NS.ContenedorCasaCambio,
		        {
				   xtype: 'button',
				    text: 'Aceptar', 
				    x: 660,
				    y: 620,
				    width: 80,
				    height: 22,
				    listeners:{     
                  		click:{     
              			   fn:function(e){   
		        	           NS.valida_datos(NS.datos);
		        	           if(NS.datos == true){
		        	        	   NS.boton();		        	        	   
		        	           } 
		        			}
		        		}
		        	}
				},{ 
				    xtype: 'button',
				    text: 'Limpiar',
				    x: 750,
				    y: 620,
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
				    text: 'Cancelar',
				    x: 840, 
				    y: 620,
				    width: 80,
				    height: 22,
				    listeners:{
                  		click:{
              			   fn:function(e){
              			   }
           			   }   
       			   }
				} 
		       ]
		       
	});
 	
	NS.CompraVentaTransferPropuestasPago = new Ext.form.FormPanel({
	 	title: 'Compra Venta de Transfer en Propuestas de Pago',
		width: 1010,
		height: 800,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',  
		id: PF + 'CompraVentaTransferPropuestasPago',     
		name: PF+ 'CompraVentaTransferPropuestasPago',
		renderTo: NS.tabContId,
		items :[
		        NS.ContenedorGeneral
		]       
	});
	
	
	NS.CompraVentaTransferPropuestasPago.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());

 	
});