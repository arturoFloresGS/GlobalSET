
	Ext.onReady(function() {
		
		var NS = Ext.namespace('apps.SET.Derivados.CapturaForwards');
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
		
		NS.INGRESO = "I";
		NS.EGRESO = "E";
		
		NS.idGrupo = 44;
	 	NS.idRubro = 44;
	 	NS.idGrupoI = 3;
	 	NS.idRubroI = 1;

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
		 	
		 	NS.storeDivisaVenta.removeAll();
		 	NS.storeDivisaCompra.removeAll();
		 	
		 	NS.storeBancoCargo.removeAll();
		 	NS.storeBancoAbono.removeAll(); 
		 	NS.storeChequeraCargo.removeAll();
		 	NS.storeChequeraAbono.removeAll();
		 	NS.storeChequera.removeAll();
		 	NS.storeBanco.removeAll();
		 	
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
		
		//EMPRESA
		
		
	    NS.banD= new Ext.form.TextField({
		 	
			id: PF + 'txtVersion',
	        name: PF + 'txtVersion',
	    	value:'',
	        x: 120,
	        y: 60,
	        width: 430, 
	    	
		  });
		
	     NS.g = new Ext.form.TextField({
	 	 	id: PF + 'g',
	         name: PF + 'g',
	         x: 120,
	         y: 60,
	         width: 430, 
	     
	 	  });
	     NS.sg = new Ext.form.TextField({
	 		id: PF + 'sg',
	         name: PF + 'sg',
	         x: 120,
	         y: 60,
	         width: 430, 

	 	  });
	     NS.r = new Ext.form.TextField({
	 	
	 		id: PF + 'r',
	         name: PF + 'r',
	         x: 120,
	         y: 60,
	         width: 430, 
	 

	 	  });
	     NS.sr = new Ext.form.TextField({
	 		id: PF + 'sr',
	         name: PF + 'sr',
	         x: 120,
	         y: 60,
	         width: 430, 
	   
	 	  });
		
		
		
		NS.lblEmpresa = new Ext.form.Label({
			text: 'Empresa',
			x: 30,
			y: 0
		});
		
		NS.txtEmpresa = new Ext.form.TextField({
			id: PF + 'txtEmpresa',
	        name:PF + 'txtEmpresa',
			x: 80,
	        y: 0,
	        width: 50, 
	        tabIndex: 0,
	        listeners : {
	        	change : {
	        		fn: function(caja, valor){
			
						funLimpiarPorEventoEnEmpresas();
			
						if(caja.getValue()===''){
							NS.cmbEmpresas.reset();
							return; 
						}
						
						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresas.getId()) === undefined ){
							caja.reset();
							Ext.Msg.alert('SET','Id de Empresa no valido.');
							return; 
						}					
	        				
						NS.accionarEmpresas(caja.getValue());	
						
	        		}
	        	}
	        }
		});
		
		NS.storeEmpresas = new Ext.data.DirectStore({
			paramsAsHash: false,
			baseParams:{
				idUsuario:NS.idUsuario
			},
			root: '',
			paramOrder:['idUsuario'],
			//directFn: CapturaSolicitudesPagoAction.obtenerEmpresas,
			directFn:GestionDeOperacionesDivisasAction.obtenerEmpresas,
			idProperty: 'noEmpresa', 
			fields: [
				 {name: 'noEmpresa'},
				 {name: 'nomEmpresa'}
			],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
					if(records.length==null || records.length<=0)
					{
						Ext.Msg.alert('SET','No tiene empresas asignadas');
					}
				}
			}
		}); 
		
		NS.storeEmpresas.load();
		
		function funLimpiarPorEventoEnEmpresas(){
			
			NS.txtDivisaVenta.reset();
			NS.cmbDivisaVenta.reset();
			NS.storeDivisaVenta.removeAll();
			
			NS.storeBancoCargo.removeAll();
			NS.txtBancoCargo.reset();
			NS.cmbBancoCargo.reset();
					
			NS.storeChequeraCargo.removeAll();
			NS.cmbChequeraCargo.reset();
			
			NS.txtDivisaCompra.reset();
			NS.cmbDivisaCompra.reset();
			NS.storeDivisaCompra.removeAll();
			
			NS.storeBancoAbono.removeAll();
			NS.txtBancoAbono.reset();
			NS.cmbBancoAbono.reset();
					
			NS.storeChequeraAbono.removeAll();
			NS.cmbChequeraAbono.reset();
			
		};
		
		NS.accionarEmpresas = function(valueCombo){	
			
			NS.noEmpresa = parseInt(valueCombo);
					
			NS.storeDivisaVenta.baseParams.idUsuario = NS.idUsuario
			NS.storeDivisaVenta.baseParams.noEmpresa = NS.noEmpresa		
			NS.storeDivisaVenta.load({
				callback:function(records, options, success){
					if( records.length ===1 ){
						Ext.Msg.alert('SET','Las chequeras de la empresa pertenecen a una divisa.');
						NS.storeDivisaVenta.removeAll();
					}
				}
			});
					
			NS.storeDivisaCompra.baseParams.idUsuario = NS.idUsuario
			NS.storeDivisaCompra.baseParams.noEmpresa = NS.noEmpresa		
			NS.storeDivisaCompra.load({
				callback:function(records, options, success){
					if( records.length ===1 ){				
						NS.storeDivisaCompra.removeAll();
					}
				}
			});
			

		}
		//combo Empresas
		NS.cmbEmpresas = new Ext.form.ComboBox({
			store: NS.storeEmpresas
			,name: PF+'cmbEmpresas'
			,id: PF+'cmbEmpresas'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
	        ,x: 140
	        ,y: 0
	        ,width: 300
	        ,tabIndex: 1
			,valueField:'noEmpresa'
			,displayField:'nomEmpresa'
			,autocomplete: true
			,emptyText: 'Seleccione una empresa'
			,triggerAction: 'all'
			,visible: false
			,editable:false
			,listeners:{
				select:{
					fn:function(combo, valor) {
							BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresas.getId());
							funLimpiarPorEventoEnEmpresas();
							NS.accionarEmpresas(combo.getValue());
					}
				}
			}
		});	
		
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
					
						funLimpiarPorEventoEnDivisaVenta();
			
						if(caja.getValue()===''){
							NS.cmbDivisaVenta.reset();
							return; 
						}
						
						NS.txtDivisaVenta.setValue(caja.getValue().toUpperCase());
				
						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisaVenta',NS.cmbDivisaVenta.getId()) === undefined ){
							caja.reset();
							Ext.Msg.alert('SET','Id de divisa no valido.');
							return; 
						}					
					
						NS.accionarDivisaVenta(caja.getValue());
							
	        		}
	        	}
	        }
		});
		
		function funLimpiarPorEventoEnDivisaVenta(){
			
			NS.storeBancoCargo.removeAll();
			NS.txtBancoCargo.reset();
			NS.cmbBancoCargo.reset();
					
			NS.storeChequeraCargo.removeAll();
			NS.cmbChequeraCargo.reset();
			
			NS.txtDivisaCompra.reset();
			NS.cmbDivisaCompra.reset();		
					
			NS.txtBancoAbono.reset();
			NS.cmbBancoAbono.reset();
			
			NS.cmbChequeraAbono.reset();
			
			
			NS.txtCasaCambio.reset();
	        NS.cmbCasaCambio.reset();
			
			NS.storeBanco.removeAll();
	        NS.txtBanco.reset();
	        NS.cmbBanco.reset();
	        
	        NS.storeChequera.removeAll();
	        NS.cmbChequera.reset();
	        
	        
	        NS.txtOperador.reset();
	        
			
		};

		NS.accionarDivisaVenta = function(valueCombo){
			
			NS.txtBancoCargo.reset();
			NS.cmbBancoCargo.reset();
			NS.storeBancoCargo.removeAll();
			NS.storeBancoCargo.baseParams.noEmpresa = NS.noEmpresa;
			NS.storeBancoCargo.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();				
			NS.storeBancoCargo.load();
			
		};	
		
		NS.storeDivisaVenta = new Ext.data.DirectStore({     
			paramsAsHash: false,
			root: '',
			baseParams: {},
			paramOrder:['idUsuario', 'noEmpresa'],
			directFn: ConfirmacionCargoCtaAction.obtenerDivisaVnta, 
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
			editable:false,
			listeners:{
				select:{
					fn:function(combo, valor) {
		 				BFwrk.Util.updateComboToTextField(PF+'txtDivisaVenta',NS.cmbDivisaVenta.getId());
		 				funLimpiarPorEventoEnDivisaVenta();
		 				NS.accionarDivisaVenta(combo.getValue());					
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
			
						funLimpiarPorEventoEnBancoCargo();
						
						if(caja.getValue()===''){
							NS.cmbBancoCargo.reset();
							return; 
						}
						
						NS.txtBancoCargo.setValue(caja.getValue());
				
						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoCargo',NS.cmbBancoCargo.getId()) === undefined ){
							caja.reset();
							Ext.Msg.alert('SET','Id de banco no valido.');
							return; 
						}					
					
						NS.accionarBancoCargo(caja.getValue());

	        		}
	        	}
	        }
		});
		
		function funLimpiarPorEventoEnBancoCargo(){
				
			NS.storeChequeraCargo.removeAll();
			NS.cmbChequeraCargo.reset();
			
		};
		

		NS.accionarBancoCargo = function(valueCombo){
			
			NS.cmbChequeraCargo.reset();
			NS.storeChequeraCargo.removeAll();
			
			NS.storeChequeraCargo.baseParams.noEmpresa = NS.noEmpresa;
			NS.storeChequeraCargo.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();			
			NS.storeChequeraCargo.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBancoCargo').getValue());				
			NS.storeChequeraCargo.load(); 
			
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
			editable:false,
			listeners:{
				select:{
					fn:function(combo, valor) {
		 				BFwrk.Util.updateComboToTextField(PF+'txtBancoCargo',NS.cmbBancoCargo.getId());
		 				
		 				var banD=valor.get("descBanco")	
				 		NS.banD.setValue(banD);
				 		
		 				funLimpiarPorEventoEnBancoCargo();
		 				NS.accionarBancoCargo(combo.getValue());
		 				
		 				
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
			directFn: ConfirmacionCargoCtaAction.obtenerChequeraVnta,     
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
	        width: 260,
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
					
						funLimpiarPorEventoEnDivisaCompra();
						
						if(caja.getValue()===''){
							NS.cmbDivisaCompra.reset();
							return; 
						}
						
						NS.txtDivisaCompra.setValue(caja.getValue().toUpperCase());
				
						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisaCompra',NS.cmbDivisaCompra.getId()) === undefined ){
							caja.reset();
							Ext.Msg.alert('SET','Id de divisa no valido.');
							return; 
						}					
					
						NS.accionarDivisaCompra(caja.getValue());
						
	        		}
	        	}
	        }
		});
		
		function funLimpiarPorEventoEnDivisaCompra(){
			
			NS.storeBancoAbono.removeAll();
			NS.txtBancoAbono.reset();
			NS.cmbBancoAbono.reset();
					
			NS.storeChequeraAbono.removeAll();
			NS.cmbChequeraAbono.reset();
			
		};
		
		NS.accionarDivisaCompra = function(valueCombo){

			if( Ext.getCmp(PF + 'txtDivisaVenta').getValue() === '' ){
				NS.cmbDivisaCompra.reset();
				NS.txtDivisaCompra.reset();
				Ext.Msg.alert('SET','No ha seleccionado la divisa de venta.');
				return; 
			} 
			
			
			if( Ext.getCmp(PF + 'txtDivisaVenta').getValue() === valueCombo ){
				NS.cmbDivisaCompra.reset();
				NS.txtDivisaCompra.reset();
				Ext.Msg.alert('SET','La divisa de compra no debe ser igual a la divisa de venta.');
				return; 
			} 
			
			NS.txtBancoAbono.reset();
			NS.cmbBancoAbono.reset();
			NS.storeBancoAbono.removeAll();
			
			NS.storeBancoAbono.baseParams.noEmpresa = NS.noEmpresa;
			NS.storeBancoAbono.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaCompra').getValue();    
					
			NS.storeBancoAbono.load();
		};
		
		NS.storeDivisaCompra = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			baseParams: { },
			paramOrder:['idUsuario', 'noEmpresa'],
			directFn: ConfirmacionCargoCtaAction.obtenerDivisaVnta, 
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
		
		//NS.storeDivisaCompra.load();
	 
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
						funLimpiarPorEventoEnDivisaCompra();
						NS.accionarDivisaCompra(combo.getValue());	
		 				
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
			
						funLimpiarPorEventoEnBancoAbono();
						
						if(caja.getValue()===''){
							NS.cmbBancoAbono.reset();
							return; 
						}
						
						NS.txtBancoAbono.setValue(caja.getValue());
				
						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoAbono',NS.cmbBancoAbono.getId()) === undefined ){
							caja.reset();
							Ext.Msg.alert('SET','Id de banco no valido.');
							return; 
						}					
					
						NS.accionarBancoAbono(caja.getValue());
						
	        		}
	        	}
	        }
		});
		
		function funLimpiarPorEventoEnBancoAbono(){
			
			NS.storeChequeraAbono.removeAll();
			NS.cmbChequeraAbono.reset();
			
		};
		
		NS.accionarBancoAbono = function(valueCombo){
			
			var tieneBanca = Ext.getCmp(PF + 'Radios').getValue();
			NS.iTieneBanca = parseInt(tieneBanca.getGroupValue());
			
			var icustodia = Ext.getCmp(PF + 'RadiosBtn').getValue();
			NS.iCustodia = parseInt(icustodia.getGroupValue());
			
			NS.storeChequeraAbono.removeAll();
			NS.cmbChequeraAbono.reset();
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
			paramOrder:['noEmpresa','idDivisa'],
			directFn: ConfirmacionCargoCtaAction.obtenerBancoAbono, 
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
		 				funLimpiarPorEventoEnBancoAbono();
		 				NS.accionarBancoAbono(combo.getValue());
		 				
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
			paramOrder:['noEmpresa','idDivisa','idBanco'],
			directFn: ConfirmacionCargoCtaAction.obtenerChequeraAbono, 
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
	        width: 260,
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
		          {boxLabel: 'Efectivo', name: 'opt', inputValue: 2, hidden:true},  
		          {boxLabel: 'Cargo en Cuenta Terceros', name: 'opt', inputValue: 5, hidden:true}
		     ],
		     listeners: {
					change: {
						fn: function(caja, valor) {
							
	 						var TieneBanca = Ext.getCmp(PF + 'Radios').getValue();
	 						NS.iTieneBanca = parseInt(TieneBanca.getGroupValue());      
	 						
	 						if(NS.iTieneBanca == 3){
	 							NS.txtBanco.setVisible(true);
	 							NS.cmbChequera.setVisible(true);
	 							NS.txtOperador.setVisible(true);
	 							NS.labOperador.setVisible(true);
	 							NS.labChequera.setVisible(true);
	 							NS.labBanco.setVisible(true);
	 							NS.cmbBanco.setVisible(true);

	 							NS.txtCasaCambio.setDisabled(false);
	 							NS.cmbCasaCambio.setDisabled(false);
	 						}
	 						
	 						if(NS.iTieneBanca == 5){
	 							NS.txtBanco.setVisible(false);
	 							NS.cmbChequera.setVisible(false);
	 							NS.labChequera.setVisible(false);
	 							NS.labBanco.setVisible(false);
	 							NS.cmbBanco.setVisible(false);
	 							
	 							NS.txtCasaCambio.setDisabled(true);
	 							NS.cmbCasaCambio.setDisabled(true);
	 						}
	 					}
	 				}    
	 			}
	 						     
		});
	 
	 	NS.ContFormaPago = new Ext.form.FieldSet({
			layout: 'absolute',
			title: '',
			width: 220,
		    height: 70, 
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
		    hidden:true,
			items: [
			        NS.btnRadios
			       ]
			        
		});
	 	
	 	
	 	
	 	
	 	
	 	
	 	
	 	
	 	
	 	
	 	  NS.spot = new Ext.form.TextField({
		 	 	
		 		id: PF + 'spot',
		         name: PF + 'spot',
		         x: 120,
		         y: 60,
		         width: 430, 
		 

		 	  });

		     NS.pF = new Ext.form.TextField({
		 		id: PF + 'pF',
		         name: PF + 'pF',
		         x: 120,
		         y: 60,
		         width: 430, 
		   
		 	  });
	 	
	 	
		  
		     
		     
	 	NS.labSpot = new Ext.form.Label({
	 		text: 'SPOT: ',
			x: 0,
			y: 165
	 	});
	 	
	 	NS.txtSpot = new Ext.form.TextField({
			id: PF + 'txtSpot',
	        name:PF + 'txtSpot',
			x: 0,
			enableKeyEvents: true,
			selectOnFocus: true,
			 forceSelection: false,
			y:185,
		    width: 120,
	        listeners: {
				change: {
					fn: function(caja, valor) {
						var caracter = caja.getValue();
						var alamcen=0;
						var alamcen2=[];

						if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
							
					//	alert(caracter.length+"  israe");
							for (var i = 0; i < caracter.length; i++) {
								 if(caracter[i]=='.'){
									 i=i+1;	 
								 }	
								 	 
								 if(((caracter[i] < '0') || (caracter[i]> '9')) && (caracter[i] != '\b') ){
									
							    		alert("El Spot tiene letras:   "+caracter[i]+".\n Que es invalido intenta de nuevo   ");
							    	caja.setValue("");
							    	Ext.getCmp(PF + 'txtSpot').focus(true,10);
							    	return;
							      //   caracter.consume();  // ignorar el evento de teclado
							      }else{
							    	  
							    	   var TipoCam = Ext.getCmp(PF + 'txtSpot').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtSpot').getValue()));
						
							     
							      }
								 

							}
							
							// Verificar si la tecla pulsada no es un digito
						     
						   
							
						}
					},
					keyup : {
		 				fn: function(caja,valor){
		 					 Ext.getCmp(PF + 'txtSpot').focus();
		 					var ptsForward = parseFloat(Ext.getCmp(PF + 'txtPtsForward').getValue());						
		 					Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(ptsForward + parseFloat(caja.getValue())));
		 					
		 				}
		 			}
				}
			}
		});
	 	
	 	NS.labPtsForward = new Ext.form.Label({
	 		text: 'Puntos Forward: ',
			x: 130,
			y: 165
	 	});
	 	
	 	NS.txtPtsForward = new Ext.form.TextField({
			id: PF + 'txtPtsForward',
	        name:PF + 'txtPtsForward',
			x: 130,
			y:185,
			enableKeyEvents: true,
			selectOnFocus: true,
			 forceSelection: false,
			width: 120,
	        listeners: {
				change: {
					fn: function(caja, valor) {
						
						 
						var caracter=caja.getValue();
						if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
							//var a=Ext.getCmp(PF + 'txtTipoCambio').getValue();
						/*	if(isNaN(a)){
								Ext.getCmp(PF + 'txtSpot').setValue("");
								Ext.getCmp(PF + 'txtPtsForward').setValue("");
								alert("Ingresa un valor correcto");
							}*/
							
							
							for (var i = 0; i < caracter.length; i++) {
								 if(caracter[i]=='.'){
									 i=i+1;	 
								 }	
								 	 
								 if(((caracter[i] < '0') || (caracter[i]> '9')) && (caracter[i] != '\b') ){
									
							    		alert("Valor incorrecto los Pnts forwards tiene letras:   "+caracter[i]+".\n Que es invalido intenta de nuevo   ");
							    	caja.setValue("");
							    	Ext.getCmp(PF + 'txtTipoCambio').setValue("");
							    	Ext.getCmp(PF + 'txtSpot').focus(true,10);
							    	return;
							      //   caracter.consume();  // ignorar el evento de teclado
							      }else{
							    	  
							    		var TipoCam = Ext.getCmp(PF + 'txtPtsForward').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtPtsForward').getValue()));
										var ptsForward = parseFloat(Ext.getCmp(PF + 'txtPtsForward').getValue());
										var spot = parseFloat(Ext.getCmp(PF + 'txtSpot').getValue());
										Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(spot + ptsForward));
									
							     
							      }
								 

							}
							
							
						
						}
					},
		 			keyup : {
		 				fn: function(caja,valor){
		 					var spot = parseFloat(Ext.getCmp(PF + 'txtSpot').getValue());
		 					Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(spot + parseFloat(caja.getValue())));
		 				
		 				}
		 			}
				}
			}
		});
	 	
	 	
	 	NS.labTipoCambio = new Ext.form.Label({
	 		text: 'Tipo de Cambio: ',
			x: 260,
			y: 165
	 	});
	 	
	 	NS.txtTipoCambio = new Ext.form.TextField({
			id: PF + 'txtTipoCambio',
	        name:PF + 'txtTipoCambio',
			x: 260,
			y:185,
			disabled:true,
	        width: 120,
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
//		 					alert(TipoCam);
		 					
		 				
		 				}
		 			}
				}
			}
		});
	 	
	 	
	 	NS.labImporteCompra = new Ext.form.Label({
	 		text: 'Importe de Compra: ',
	 		x: 390,
			y: 165
	 	});
	 	
	 	NS.txtImporteCompra = new Ext.form.TextField({
			id: PF + 'txtImporteCompra',
	        name:PF + 'txtImporteCompra',
	        x: 390,
	        y: 185,
			enableKeyEvents: true,
			selectOnFocus: true,
			 forceSelection: false,
	        width: 120,
	        listeners: {
				change: {
					fn: function(caja, valor) {
						var divisa = Ext.getCmp(PF + 'txtDivisaCompra').getValue();
						 var tipo = parseFloat(Ext.getCmp(PF + 'txtTipoCambio').getValue());
	 					 var caracter=caja.getValue() ;
	 					 if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	 						 
	 						 
	 						for (var i = 0; i < caracter.length; i++) {
								 if(caracter[i]=='.'){
									 i=i+1;	 
								 }	
								 	 
								 if(((caracter[i] < '0') || (caracter[i]> '9')) && (caracter[i] != '\b') ){
									
							    		alert("El importe de la compra tiene letras:   "+caracter[i]+".\n Que es invalido intenta de nuevo   ");
							    	caja.setValue("");
							    	Ext.getCmp(PF + 'txtSpot').focus(true,10);
							    	return;
							      //   caracter.consume();  // ignorar el evento de teclado
							      }else{
							    	  
							    	   var impCom = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtImporteCompra').getValue()));
				 						 Ext.getCmp(PF + 'txtImporteCompra').setValue(NS.formatNumber(impCom));
					 					 var valor1 = 0;
				 						     
							      }
								 

							}
	 						 
	 						
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
	 	
	 	NS.labImporteVenta = new Ext.form.Label({
	 		text: 'Importe de Venta: ',
	 		x: 555,
			y: 165
			
	 	});
	 	 	
	 	NS.txtImporteVenta = new Ext.form.TextField({
			id: PF + 'txtImporteVenta',
	        name:PF + 'txtImporteVenta',
	        x: 555,
	        y: 185,		
	        disabled:true,
	        width: 120,
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
	 	
	 	//CASA DE CAMBIO
	 	
	 	NS.labCasaCambio = new Ext.form.Label({
	 		text: 'Casa de Cambio: ',
			x: 0,
			y:220
	 	});
	 	
	 	NS.txtCasaCambio = new Ext.form.TextField({
			id: PF + 'txtCasaCambio',
	        name:PF + 'txtCasaCambio',
			x: 0,
			y: 245,
	        width: 70, 
	        tabIndex: 0,
	        listeners : {
	        	change : {
	        		fn: function(caja, valor){  
	 		
				 		funLimpiarPorEventoEnCasaDeCambio();
						
						if(caja.getValue()===''){
							NS.cmbCasaCambio.reset();
							return; 
						}
						
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
	 	
	 	function funLimpiarPorEventoEnCasaDeCambio(){
	 		
	 		NS.storeBanco.removeAll();
	        NS.txtBanco.reset();
	        NS.cmbBanco.reset();
	        
	        NS.storeChequera.removeAll();
	        NS.cmbChequera.reset();
	        
	        
	        NS.txtOperador.reset();
	        
	        NS.txtGrupo.reset();
	        NS.cmbGrupo.reset();
	        NS.txtRubro.reset();
	        NS.cmbRubro.reset();
	        NS.txtGrupoI.reset();
	        NS.cmbGrupoI.reset();									    
	        NS.txtRubroI.reset();
	        NS.cmbRubroI.reset();
	        
			
	 		
	 	}
	 	
	 	
	 	
	 	NS.accionarCasaCambio = function(valueCombo){
	 		
	 		if( Ext.getCmp(PF + 'txtDivisaVenta').getValue() === '' ){
				NS.cmbCasaCambio.reset();
				NS.txtCasaCambio.reset();
				Ext.Msg.alert('SET','No ha seleccionado la divisa de venta.');
				return; 
			} 
	 		
	 		NS.txtOperador.setValue( NS.storeCasaCambio.getById(valueCombo).get('contacto') );
			
	 		NS.storeBanco.removeAll();
			NS.storeBanco.baseParams.noCliente = parseInt(Ext.getCmp(PF + 'txtCasaCambio').getValue());
			NS.storeBanco.baseParams.idDivisa = Ext.getCmp(PF + 'txtDivisaVenta').getValue();
			NS.storeBanco.load();
			
			 Ext.getCmp( PF + 'txtGrupo'	    ).setValue( NS.idGrupo );
	         Ext.getCmp( PF + 'cmbGrupo'	    ).setValue( NS.idGrupo );   		                                    
			 Ext.getCmp( PF + 'txtRubro'	    ).setValue( NS.idRubro );
			 
			 NS.accionarGrupo2( NS.idGrupo, NS.idRubro ) ;
															   										    
			 Ext.getCmp( PF + 'txtGrupoI'	).setValue( NS.idGrupoI );
			 Ext.getCmp( PF + 'cmbGrupoI'	).setValue( NS.idGrupoI );   										    
			 Ext.getCmp( PF + 'txtRubroI'	).setValue( NS.idRubroI );
			 
			 NS.accionarGrupoI2( NS.idGrupoI, NS.idRubroI ) ;
				
			
			
			  
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
				 {name: 'contacto'}
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
	        y: 245,
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
				 		
				 		funLimpiarPorEventoEnCasaDeCambio();
						NS.accionarCasaCambio(combo.getValue());
						
					}
				}
			}
		});	 	
	 	
	 	
	 	NS.labBanco = new Ext.form.Label({
			text: 'Banco: ',
			x: 350,
			y: 220
		});
		
		NS.txtBanco = new Ext.form.TextField({
			id: PF + 'txtBanco',
			name:PF + 'txtBanco',
			x: 350,
			y: 245,
			width: 70,  
			tabIndex: 0,
			listeners : {
	    		change : {
	    			fn: function(caja, valor){  
			
						funLimpiarPorEventoBanco();
						
						if(caja.getValue()===''){
							NS.cmbBanco.reset();
							return; 
						}
						
						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId()) === undefined ){
							caja.reset();
							Ext.Msg.alert('SET','Id de Banco no valido.');
							return; 
						}					
							
						NS.accionarBanco(caja.getValue());
						
	    			}
	    		}
			}
		});
		
		function funLimpiarPorEventoBanco(){
			NS.storeChequera.removeAll();
			NS.cmbChequera.reset();
		}
		
		NS.accionarBanco = function(valueCombo){
			
			NS.storeChequera.removeAll();
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
						NS.cmbCasaCambio.reset();
						NS.txtCasaCambio.reset();
						Ext.Msg.alert('SET','La Casa de Cambio no cuenta con chequeras de divisa de venta.');
					}
						   		
				}
			}
		}); 
	 
		NS.cmbBanco = new Ext.form.ComboBox({
			store: NS.storeBanco,
			id: PF + 'cmbBanco',
			name: PF + 'cmbBanco',
			x: 425,
	        y: 245,
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
						funLimpiarPorEventoBanco();
						NS.accionarBanco(combo.getValue());
						

		 				
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
			text: 'Chequera:',
			x: 680,
			y: 220
		});
		 
		 NS.cmbChequera = new Ext.form.ComboBox({
				store: NS.storeChequera,
				id: PF + 'cmbChequera',
				name: PF + 'cmbChequera',
				x: 680,
		        y: 245,
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
	 	
	 	
	 	
	 	
	 	
	 	
	 	
	 	
	 	
	 	NS.labOperador = new Ext.form.Label({
	 		text: 'Operador:',
			x: 0,
			y:275

			
	 	});
	 	
	 	NS.txtOperador = new Ext.form.TextField({
			id: PF + 'txtOperador',    
	        name:PF + 'txtOperador',
			x: 0,
			y:295,
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
	 
	 	NS.labFechaValor = new Ext.form.Label({
			text: 'Fecha Valor:',
			x: 350,
			y: 275
		});
		
		
		NS.txtFechaValor = new Ext.form.DateField({
			id: PF + 'txtFechaValor',
			name: PF + 'txtFechaValor',
			x: 350,
			y: 295,
			width: 110,
			format: 'd/m/Y',
			value : apps.SET.FEC_HOY
		});
 
		NS.labFechaLiquidacion = new Ext.form.Label({
			text: 'Fecha de vencimiento:',
			x: 530,
			y: 275
		});
	 
		NS.txtFechaLiquidacion = new Ext.form.DateField({
			id: PF + 'txtFechaLiquidacion',
			name: PF + 'txtFechaLiquidacion',
			x: 530,
			y: 295,
			width: 110,
			format: 'd/m/Y',
			value : apps.SET.FEC_HOY
		});
	 	
	 	
	 
	 
	 
		//GRUPO EGRESO
	 
	 
	 	
	 	NS.labGrupo = new Ext.form.Label({
	 		text: 'Grupo Egreso:',
			x: 0,
			y: 325
	 	});
	 	   
	 	NS.txtGrupo = new Ext.form.TextField({
			id: PF + 'txtGrupo',
	        name:PF + 'txtGrupo',
			x: 0,
	        y: 345,
	        width: 70, 
	        tabIndex: 0,
	        listeners : {
	        	change : {
	        		fn: function(caja, valor){     
	 		
	 					funLimpiarPorEventoGrupo();
			
						if(caja.getValue()===''){
							NS.cmbGrupo.reset();
							return; 
						}
						
						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupo',NS.cmbGrupo.getId()) === undefined ){
							caja.reset();
							Ext.Msg.alert('SET','Id de Grupo no valido.');
							return; 
						}					
							
						NS.accionarGrupo(caja.getValue());
	        			
	        		}
	        	}
	        }
		});
	 	
	 	function funLimpiarPorEventoGrupo(){
	 		NS.storeRubro.removeAll();
	 		NS.txtRubro.reset();
	 		NS.cmbRubro.reset();
	 	}
	 	
	 	NS.accionarGrupo = function(valueCombo){		
	 		NS.storeRubro.removeAll();
			NS.storeRubro.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupo').getValue());
			NS.storeRubro.load();
			
		};
		
	 	NS.accionarGrupo2 = function(valueCombo, value2){		
	 		NS.storeRubro.removeAll();
			NS.storeRubro.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupo').getValue());
			NS.storeRubro.load({
				callback:function(records, operation, success){
					Ext.getCmp( PF + 'cmbRubro'	    ).setValue( value2 );
				}
			});
			
		};
	 	
	 	
	 	NS.storeGrupo = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			baseParams: {
	    		idTipoMovto:NS.EGRESO
			},
			paramOrder:['idTipoMovto'],
			directFn: ConfirmacionCargoCtaAction.obtenerGruposPorTipoMovto,
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
			x: 80,
	        y: 345,
	        width: 220,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
	        tabIndex: 1,
			valueField: 'idGrupo',
			displayField: 'descGrupo',
			autocomplete: true,
			emptyText: 'Seleccione un Grupo Egreso',   
			triggerAction: 'all',
			value: '',
			visible: false,
			listeners:{
				select:{
					fn:function(combo, valor) {
		 				BFwrk.Util.updateComboToTextField(PF+'txtGrupo',NS.cmbGrupo.getId());
		 				
		 				var grupo =  valor.get('idGrupo');
						NS.g.setValue(grupo);
					
		 				
		 				funLimpiarPorEventoGrupo();
		 				NS.accionarGrupo(combo.getValue());
		 				
					}
				}
			}
		});
	 
	 	
		
		
		
		NS.labRubro = new Ext.form.Label({
	 		text: 'Rubro Egreso:',
			x: 350,
			y: 325
	 	});
	 	
	 	NS.txtRubro = new Ext.form.TextField({
			id: PF + 'txtRubro',
	        name:PF + 'txtRubro',
			x: 350,
	        y: 345,
	        width: 70, 
	        tabIndex: 0,
	        listeners : {
	 			change : {
	    			fn: function(caja, valor){     
	 		
	 					if(caja.getValue()===''){
	 						NS.cmbRubro.reset();
	 						return; 
	 					}
			
	 					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtRubro',NS.cmbRubro.getId()) === undefined ){
	 						caja.reset();
	 						Ext.Msg.alert('SET','Id de Rubro no valido.');
	 						return; 
	 					}					
				
	    			}
	    		}
	 		}
	        
		});
	 	 	
	 	
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
			x: 425,
	        y: 345,
	        width: 215,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
	        tabIndex: 1,
			valueField: 'idRubro',
			displayField: 'descRubro',
			autocomplete: true,
			emptyText: 'Seleccione un Rubro Egreso',
			triggerAction: 'all',
			value: '',
			visible: false,
			listeners:{
				select:{
					fn:function(combo, valor) {
	 		
	 					BFwrk.Util.updateComboToTextField(PF+'txtRubro',NS.cmbRubro.getId());
	 					
	 					var rubro=valor.get("idRubro");
	 					NS.r.setValue(rubro);
	 									
					}
				}
			} 
		});
	 	
	 	
	 	
	 	///GRUPO INGRESO
	 
	 
	 	
	 	NS.labGrupoI = new Ext.form.Label({
	 		text: 'Grupo Ingreso:',
			x: 0,
			y: 375
	 	});
	 	   
	 	NS.txtGrupoI = new Ext.form.TextField({
			id: PF + 'txtGrupoI',
	        name:PF + 'txtGrupoI',
			x: 0,
	        y: 395,
	        width: 70, 
	        tabIndex: 0,
	        listeners : {
	        	change : {
	        		fn: function(caja, valor){     
	 		
	 					funLimpiarPorEventoGrupoI();
			
						if(caja.getValue()===''){
							NS.cmbGrupoI.reset();
							return; 
						}
						
						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupoI',NS.cmbGrupoI.gItId()) === undefined ){
							caja.reset();
							Ext.Msg.alert('SET','Id de Grupo no valido.');
							return; 
						}					
							
						NS.accionarGrupoI(caja.getValue());
	        			
	        		}
	        	}
	        }
		});
	 	
	 	function funLimpiarPorEventoGrupoI(){
	 		NS.storeRubroI.removeAll();
	 		NS.txtRubroI.reset();
	 		NS.cmbRubroI.reset();
	 	}
	 	
	 	NS.accionarGrupoI = function(valueCombo){		
	 		NS.storeRubroI.removeAll();
			NS.storeRubroI.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupoI').getValue());
			NS.storeRubroI.load();
			
		};
		
		NS.accionarGrupoI2 = function(valueCombo, value2){		
	 		NS.storeRubroI.removeAll();
			NS.storeRubroI.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupoI').getValue());
			NS.storeRubroI.load({
				callback:function(records, operation, success){
					Ext.getCmp( PF + 'cmbRubroI'	    ).setValue( value2 );
				}
			});
			
		};
	 	
	 	
	 	NS.storeGrupoI = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			baseParams: {
	    		idTipoMovto:NS.INGRESO
			},
			paramOrder:['idTipoMovto'],
			directFn: ConfirmacionCargoCtaAction.obtenerGruposPorTipoMovto, 
			idProperty: 'idGrupo',    
			fields: [
				 {name: 'idGrupo'},
				 {name: 'descGrupo'}
			],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoI, msg:"Cargando..."});
					if(records.length == null || records.length <= 0)
					{
						Ext.Msg.alert('SET','No tiene Grupos asignados');
					}
						   		
				}
			}
		}); 
	 	
	 	NS.storeGrupoI.load();
	 
	 NS.cmbGrupoI = new Ext.form.ComboBox({
			store: NS.storeGrupoI,
			id: PF + 'cmbGrupoI',
			name: PF + 'cmbGrupoI',
			x: 80,
	        y: 395,
	        width: 220,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
	        tabIndex: 1,
			valueField: 'idGrupo',
			displayField: 'descGrupo',
			autocomplete: true,
			emptyText: 'Seleccione un Grupo Ingreso',   
			triggerAction: 'all',
			value: '',
			visible: false,
			listeners:{
				select:{
					fn:function(combo, valor) {
		 				BFwrk.Util.updateComboToTextField(PF+'txtGrupoI',NS.cmbGrupoI.getId());
		 				
		 				var subG= valor.get("idGrupo");
		 				NS.sg.setValue(subG);
		 				
		 				funLimpiarPorEventoGrupoI();
		 				NS.accionarGrupoI(combo.getValue());
		 				
					}
				}
			}
		});
	 
	 	
		
		
		
		NS.labRubroI = new Ext.form.Label({
	 		text: 'Rubro Ingreso:',
			x: 350,
			y: 375
	 	});
	 	
	 	NS.txtRubroI = new Ext.form.TextField({
			id: PF + 'txtRubroI',
	        name:PF + 'txtRubroI',
			x: 350,
	        y: 395,
	        width: 70, 
	        tabIndex: 0,
	        listeners : {
	 			change : {
	    			fn: function(caja, valor){     
	 		
						if(caja.getValue()===''){
							NS.cmbRubroI.reset();
							return; 
						}
						
						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtRubroI',NS.cmbRubroI.getId()) === undefined ){
							caja.reset();
							Ext.Msg.alert('SET','Id de Rubro no valido.');
							return; 
						}	
	 					
	    			}
	    		}
	 		}
	        
		});
	 	
	 	
	 	NS.storeRubroI = new Ext.data.DirectStore({
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
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubroI, msg:"Cargando..."});
					if(records.length == null || records.length <= 0)
					{
						Ext.Msg.alert('SET','No tiene Grupos asignados');
					}
						   		
				}
			}
		}); 
	 
	 	NS.cmbRubroI = new Ext.form.ComboBox({
			store: NS.storeRubroI,
			id: PF + 'cmbRubroI',
			name: PF + 'cmbRubroI',
			x: 425,
	        y: 395,
	        width: 215,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
	        tabIndex: 1,
			valueField: 'idRubro',
			displayField: 'descRubro',
			autocomplete: true,
			emptyText: 'Seleccione un Rubro Ingreso',
			triggerAction: 'all',
			value: '',
			visible: false,
			listeners:{
				select:{
					fn:function(combo, valor) {
	 					BFwrk.Util.updateComboToTextField(PF+'txtRubroI',NS.cmbRubroI.getId());
	 					
	 					var sRubro=valor.get("idRubro");
	 					NS.sr.setValue(sRubro);
	 									
					}
				}
			} 
		});
	 	
	 	//REFERENCIA
	 	
	 	NS.labReferencia = new Ext.form.Label({
	 		text: 'Referencia:',
			x: 0,
			y: 425
	 	});
	 	
	 	NS.txtReferencia = new Ext.form.TextField({
			id: PF + 'txtReferencia',
	        name:PF + 'txtReferencia',
	        x: 0,
	        y: 445,
	        width: 300,
	        maskRe:/\w/,
	        listeners: {
				change: {
					fn: function(caja, valor) {
	 					NS.txtReferencia.setValue(caja.getValue().toUpperCase());
					}
				}
			}
		});
	 	
	 	NS.labConcepto = new Ext.form.Label({
	 		text: 'Concepto:',
			x: 350,
			y: 425
	 	});
	 	
	 	
	 	NS.txtConcepto = new Ext.form.TextField({
			id: PF + 'txtConcepto',
	        name:PF + 'txtConcepto',
	        x: 350,
	        y: 445,
	        width: 380,
	        maskRe:/\w|\s/,
	        listeners: {
				change: {
					fn: function(caja, valor) {
	 					NS.txtConcepto.setValue(caja.getValue().toUpperCase());
					}
				}
			}
		});
	 	
	 	
	 	
	 	//FIRMA 1
	 	 	
	 	NS.labFirmas = new Ext.form.Label({
	 		text: 'Firmas',
			x: 680,
			y: 325,
			hidden:true
	 	});
		 	
	 	NS.storeFirma1 = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '', 
			directFn: ConfirmacionCargoCtaAction.llenaComboFirmas,
			idProperty: 'idFirmante', 
			fields: [
				 {name: 'idFirmante'},
				 {name: 'nombreFirmante'}
			],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFirma1, msg:"Cargando..."});
					if(records.length == null || records.length <= 0)
					{
						Ext.Msg.alert('SET','No existen firmantes registrados.');
					}
						   		
				}
			}
		}); 
	 	
	 	NS.storeFirma1.load();

	 	NS.cmbFirma1 = new Ext.form.ComboBox({
			store: NS.storeFirma1,
			id: PF + 'cmbFirma1',
			name: PF + 'cmbFirma1',
			x: 680,
			y: 345,
			width: 190, 
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
			tabIndex: 1,
			valueField: 'idFirmante',
			displayField: 'nombreFirmante',
			autocomplete: true,
			emptyText: 'selecciona la firma 1',
			triggerAction: 'all',
			value: '',		
			hidden:true
		});
		 	
		 	
		 	
	 	NS.storeFirma2 = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '', 
			directFn: ConfirmacionCargoCtaAction.llenaComboFirmas,
			idProperty: 'idFirmante', 
			fields: [
				 {name: 'idFirmante'},
				 {name: 'nombreFirmante'}
			],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFirma2, msg:"Cargando..."});
					if(records.length == null || records.length <= 0)
					{
						Ext.Msg.alert('SET','No existen firmantes registrados.');
					}
						   		
				}
			}
		}); 
	 	
	 	NS.storeFirma2.load();


	 	NS.cmbFirma2 = new Ext.form.ComboBox({
			store: NS.storeFirma2,
			id: PF + 'cmbFirma2',
			name: PF + 'cmbFirma2',
			x: 680,
			y: 395,
			width: 190, 
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
			tabIndex: 1,
			valueField: 'idFirmante',
			displayField: 'nombreFirmante',
			autocomplete: true,
			emptyText: 'selecciona la firma 2',
			triggerAction: 'all',
			value: '',		
			hidden:true,
			listeners:{
				select:{
			 		fn:function(combo, valor) {
			 		
	 					if( Ext.getCmp(PF+'cmbFirma1').getValue() === ''){ 						
	 						Ext.Msg.alert('SET','No ha seleccionado el firmante 1.');
	 						combo.reset();
	 						return;  						
	 					}
	 					
	 					if( Ext.getCmp(PF+'cmbFirma1').getValue() === combo.getValue() ){ 						
	 						Ext.Msg.alert('SET','El firmante debe ser diferente al firmante 1.');
	 						combo.reset();
	 						return;  						
	 					}
		
					}
				}
				
			}
		});
		 	
	  
		NS.ContenedorGeneral = new Ext.form.FieldSet({
			layout: 'absolute',
			width: 940,
		    height: 490,      
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
			        NS.ContCustodiaFondoFijo,
			        
			        
			        NS.labSpot,
			        NS.txtSpot,
			       		        
			        NS.labPtsForward,
			        NS.txtPtsForward,
			        
			        
			        NS.labTipoCambio,
			        NS.txtTipoCambio,
			        
			        
			        NS.labImporteCompra,
			        NS.txtImporteCompra,
			        
			        NS.labImporteVenta,
			        NS.txtImporteVenta,
			        
			        NS.labCasaCambio,
			        NS.txtCasaCambio,
			        NS.cmbCasaCambio,
			        NS.labBanco,
			        NS.txtBanco,
			        NS.cmbBanco,
			        NS.labChequera,
			        NS.cmbChequera,
			        NS.labOperador,
			        NS.txtOperador,		        	        		        
			        NS.labFechaValor,		       
			        NS.txtFechaValor,
			        NS.txtFechaLiquidacion,
			        NS.labFechaLiquidacion,
			        NS.labGrupo,
			        NS.txtGrupo,
			        NS.cmbGrupo,
			        NS.labRubro,
			        NS.txtRubro,
			        NS.cmbRubro, 
			        NS.labGrupoI,
			        NS.txtGrupoI,
			        NS.cmbGrupoI,
			        NS.labRubroI,
			        NS.txtRubroI,
			        NS.cmbRubroI,
			        NS.labReferencia,
			        NS.txtReferencia,
			        NS.labConcepto,
			        NS.txtConcepto ,
			        NS.labFirmas,
			        NS.cmbFirma1,
			        NS.cmbFirma2
			       ]
		});    
		
		
		Boton = function(boton) {
			
			boton.setVisible(false);		
			var cmbCasaCambio = "";
			
			var txtDivisaVenta = new Ext.getCmp(PF + 'txtDivisaVenta').getValue();
			var cmbDivisaVenta = ( txtDivisaVenta == '' ) ? '' : NS.storeDivisaVenta.getById(txtDivisaVenta).get('descDivisa');
			
			var txtDivisaCompra = new Ext.getCmp(PF + 'txtDivisaCompra').getValue(NS.txtDivisaCompra);
			var cmbDivisaCompra = ( txtDivisaCompra == '')?'': NS.storeDivisaCompra.getById(txtDivisaCompra).get('descDivisa');
			
			var txtBancoCargo = new Ext.getCmp(PF + 'txtBancoCargo').getValue(NS.txtBancoCargo);		
			var cmbBancoCargo = (txtBancoCargo =='')? '' :NS.storeBancoCargo.getById(txtBancoCargo).get('descBanco');
			
			var txtBancoAbono = new Ext.getCmp(PF + 'txtBancoAbono').getValue(NS.txtBancoAbono);
			var cmbBancoAbono = (txtBancoAbono == '')? '' :NS.storeBancoAbono.getById(txtBancoAbono).get('descBanco');
			
			var cmbChequeraCargo = new Ext.getCmp(PF + 'cmbChequeraCargo').getValue(NS.cmbChequeraCargo);
			var cmbChequeraAbono = new Ext.getCmp(PF + 'cmbChequeraAbono').getValue(NS.cmbChequeraAbono);
			
			var txtTipoCambio = new Ext.getCmp(PF + 'txtTipoCambio').getValue(NS.txtTipoCambio);
			
			var txtCasaCambio = new Ext.getCmp(PF + 'txtCasaCambio').getValue(NS.txtCasaCambio);		
			cmbCasaCambio = (txtCasaCambio == '')? '':NS.storeCasaCambio.getById(txtCasaCambio).get('descCasaCambio');
			
			var txtOperador = Ext.getCmp(PF + 'txtOperador').getValue(NS.txtOperador);		
			
			
			var txtBanco = new Ext.getCmp(PF + 'txtBanco').getValue(NS.txtBanco);
			var cmbBanco = (txtBanco =='')?'': NS.storeBanco.getById(txtBanco).get('descBanco');
			
			var txtGrupo = new Ext.getCmp(PF + 'txtGrupo').getValue(NS.txtGrupo);
			var cmbGrupo = (txtGrupo =='')?'':NS.storeGrupo.getById(txtGrupo).get('descGrupo'); 
				
			var txtRubro = new Ext.getCmp(PF + 'txtRubro').getValue(NS.txtRubro);
			var cmbRubro = (txtRubro =='')?'':NS.storeRubro.getById(txtRubro).get('descRubro');
			
			var txtFechaValor = new Ext.getCmp(PF + 'txtFechaValor').getValue(NS.txtFechaValor);
			
			var txtImporteCompra = new Ext.getCmp(PF + 'txtImporteCompra').getValue(NS.txtImporteCompra);
			var txtImporteVenta = new Ext.getCmp(PF + 'txtImporteVenta').getValue(NS.txtImporteVenta);
			var cmbChequera = new Ext.getCmp(PF + 'cmbChequera').getValue(NS.cmbChequera);
			var txtFechaLiquidacion = Ext.getCmp(PF + 'txtFechaLiquidacion').getValue();
			var icustodia = Ext.getCmp(PF + 'Radios').getValue();
			NS.iCustodia = parseInt(icustodia.getGroupValue());  
			
			var concepto      = String( Ext.getCmp(PF + 'txtConcepto'	).getValue() );
			var referencia    = String( Ext.getCmp(PF + 'txtReferencia'	).getValue() );
			var grupoIngreso  = String( Ext.getCmp(PF + 'txtGrupoI'		).getValue() );
			var rubroIngreso  = String( Ext.getCmp(PF + 'txtRubroI'		).getValue() );
			var firma1        = String( Ext.getCmp(PF + 'cmbFirma1'		).getValue() );
			var firma2		  = String( Ext.getCmp(PF + 'cmbFirma2'		).getValue() );
			
			var empresa       = new Ext.getCmp(PF + 'txtEmpresa').getValue(NS.txtEmpresa)==''?0:parseInt(new Ext.getCmp(PF + 'txtEmpresa').getValue(NS.txtEmpresa));
			
			if(referencia != '' ){
			if(referencia.length > 30 ){
				Ext.Msg.show({
					title:'SET',
					icon:Ext.Msg.WARNING,
					msg:'La referencia debe ser de maximo 30 caracteres.',
					buttons:Ext.Msg.OK
				});
				boton.setVisible(true);
				return;
			}
			}
				
			
			ConfirmacionCargoCtaAction.crearOperaciones(NS.cliente, NS.idUsuario, 'no_folio_param', empresa, txtDivisaVenta, txtBancoCargo, 
					cmbChequeraCargo, txtDivisaCompra, txtBancoAbono,cmbChequeraAbono, txtTipoCambio, txtCasaCambio, cmbCasaCambio, txtOperador, 
					txtOperador, txtGrupo,txtBanco, txtFechaValor, txtRubro, txtImporteCompra, txtImporteVenta, cmbChequera, txtFechaLiquidacion, 
					NS.iCustodia, NS.iTieneBanca, concepto, referencia, grupoIngreso, rubroIngreso, firma1, firma2, 
					cmbDivisaVenta,	cmbDivisaCompra,cmbBancoCargo,	cmbBancoAbono,	cmbBanco,	cmbGrupo,	cmbRubro,
					function(response, e){   
				
						
						var icon = Ext.Msg.INFO; 
						
						if( ! response.resultado ){
							icon = Ext.Msg.WARNING;
						}
				
						Ext.Msg.show({
							title:'SET',
							icon:icon,
							msg:response.mensaje,
							buttons:Ext.Msg.OK
						});
						
						if(response.resultado) {
			    			
			    			limpiar();
			    			
			    			var strParams = ''; 
			    			strParams = '?nomReporte=ReporteCompraVentaDeDivisas&folioReporte=' + response.folio ;		    			
			    			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
			    			
						}
						
						boton.setVisible(true);
			    	});
		};
		   
		
		NS.ContBoton = new Ext.form.FieldSet({
			layout: 'absolute',
			width: 1010,
		    height: 550,
		    y: 30,
		    x: 15,
			items: [
			        
			        NS.ContenedorGeneral,
			        {
					    xtype: 'button',
					    text: 'Ejecutar',
					    x: 730,
					    y: 500,
					    width: 80,
					    height: 22,
					    listeners:{     
	                  		click:{     
	              			   fn:function(e) {
	              
	              				 
	              				 /////////////////parte izq///////////
	              				 
	              				 var desc_institucion = Ext.getCmp(PF + 'cmbEmpresas').getRawValue() + "";
	              				   if(NS.cmbBanco.isVisible()){
	              					
	              					 var   noEmpresa=Ext.getCmp(PF + 'txtEmpresa').getValue();
		              				   var  idDivVenta=Ext.getCmp(PF + 'txtDivisaVenta').getValue();
		              				   var bancoCargo=Ext.getCmp(PF + 'txtBancoCargo').getValue();
		              				   var   cheCargo=Ext.getCmp(PF + 'cmbChequeraCargo').getValue();
		              				  
		              				   
		              				   ////////parte dercha///////////
		              				   var  idDivCompra=Ext.getCmp(PF + 'txtDivisaCompra').getValue();
		              				   var bancoAbono=Ext.getCmp(PF + 'txtBancoAbono').getValue();
		              				   var cheAbono=Ext.getCmp(PF + 'cmbChequeraAbono').getValue();
		              				  
//		              				   var formaPago=Ext.getCmp(PF + 'Radios').getValue();
//		              				 alert(formaPago.getGrupValue());  
		              				    var formaPago=1;
		              				   var  importePago=Ext.getCmp(PF + 'txtImporteVenta').getValue();
		              				   var importeFwd=Ext.getCmp(PF + 'txtImporteCompra').getValue();
		              				   var   tipoCambio=Ext.getCmp(PF + 'txtTipoCambio').getValue();
		              				   var  fecAlta=Ext.getCmp(PF + 'txtFechaValor').getValue();
		              				   var fecVencimiento=Ext.getCmp(PF + 'txtFechaLiquidacion').getValue();
		              				   var   noInstitucion=Ext.getCmp(PF + 'txtCasaCambio').getValue();
		              				   var  nomContacto=Ext.getCmp(PF + 'txtOperador').getValue();
		              				   var idBancoBenef=Ext.getCmp(PF + 'txtBanco').getValue();
		              				
		              				   var  idCheBenef  =Ext.getCmp(PF + 'cmbChequera').getValue();
		              				   
		              				   var  rubroCarg =NS.g.getValue().toString();
		              				   var subRubroCarg=NS.sg.getValue().toString();
		              				   var   rubroAbono =NS.r.getValue().toString();
		              				   var subRubroAbono=NS.sr.getValue().toString();
		              					
		              				   var estatusMov="s";
		              				  var  estatusImporte="s";
		              				   var  firmante1="Firmante1";
		              				   var firmante2="Firmante2";
		              				   var   noDocto="NULL";
		              				   var  spot=Ext.getCmp(PF + 'txtSpot').getValue();
		              				   var pntsFwd=Ext.getCmp(PF + 'txtPtsForward').getValue();
	                  				
		              				  ForwardsAction.agregarFwds(
		              				parseInt(noEmpresa),
		  	              				     idDivVenta,
		  	              			parseInt(bancoCargo),
		  	              				     cheCargo,
		  	              				     idDivCompra,
		  	              			parseInt(bancoAbono),
		  	              					 cheAbono,
		  	              			parseInt(formaPago),
		  	              		 parseFloat(importePago),
		  	              		 parseFloat(importeFwd),
		  	              		 parseFloat(tipoCambio),
		   	              				    fecAlta,
		  	              				    fecVencimiento,
		  	              				parseInt(noInstitucion),
		  	              				     nomContacto,
		  	              				 parseInt(idBancoBenef),
		  	              				     idCheBenef,
		  	              				     rubroCarg,
		  	              				    subRubroCarg,
		  	              				    rubroAbono,
		  	              				    subRubroAbono,
		  	              				  estatusMov,
		  	              				  estatusImporte,
		  	              				   firmante1,
		  	              				    firmante2,
		  	              				   noDocto,
		  	              				  parseFloat(spot),
		  	              				parseFloat(pntsFwd),
		  	              				desc_institucion,
		  	              				NS.idUsuario,
		  	              				  
				  	              			function(response, e){
			                					if(response !== null && response !== '' ){
			                						  
			                						BFwrk.Util.msgShow('' + response, 'INFO');
			                						if(response.indexOf('correct') > 0){
			                							
													 	
			                						}
			                					}
			                				}
				                      );
		              				   
		              				NS.g.setValue("");
		              				 NS.sg.setValue("");
		              				 NS.r.setValue("");
		              				  NS.sr.setValue("");
	              					   
	              				   }else
	              					   {

	              					 var a=Ext.getCmp(PF + 'cmbBancoCargo').getValue();  
	              					 Ext.getCmp(PF + 'txtCasaCambio').setValue(a); 
	              				 	 Ext.getCmp(PF + 'cmbCasaCambio').setValue(NS.banD.getValue()); 
	              					
	              					 
	              					 
	              				 
	              					 var   noEmpresa=Ext.getCmp(PF + 'txtEmpresa').getValue();
		              				   var  idDivVenta=Ext.getCmp(PF + 'txtDivisaVenta').getValue();
		              				   var bancoCargo=Ext.getCmp(PF + 'txtBancoCargo').getValue();
		              				   var   cheCargo=Ext.getCmp(PF + 'cmbChequeraCargo').getValue();
		              				  
		              				   
		              				   ////////parte dercha///////////
		              				   var  idDivCompra=Ext.getCmp(PF + 'txtDivisaCompra').getValue();
		              				   var bancoAbono=Ext.getCmp(PF + 'txtBancoAbono').getValue();
		              				   var cheAbono=Ext.getCmp(PF + 'cmbChequeraAbono').getValue();
		              				  
//		              				   var formaPago=Ext.getCmp(PF + 'Radios').getValue();
//		              				 alert(formaPago.getGrupValue());  
		              				    var formaPago=1;
		              				   var  importePago=Ext.getCmp(PF + 'txtImporteVenta').getValue();
		              				   var importeFwd=Ext.getCmp(PF + 'txtImporteCompra').getValue();
		              				   var   tipoCambio=Ext.getCmp(PF + 'txtTipoCambio').getValue();
		              				   var  fecAlta=Ext.getCmp(PF + 'txtFechaValor').getValue();
		              				   var fecVencimiento=Ext.getCmp(PF + 'txtFechaLiquidacion').getValue();
		              				   var   noInstitucion=Ext.getCmp(PF + 'txtCasaCambio').getValue();
		              				   var  nomContacto=Ext.getCmp(PF + 'txtOperador').getValue();
		              				   
		              				   var idBancoBenef=Ext.getCmp(PF + 'txtBancoAbono').getValue();
		              				   var  idCheBenef  =Ext.getCmp(PF + 'cmbChequeraCargo').getValue();
		              				   
		              				   var  rubroCarg =NS.g.getValue().toString();
		              				   var subRubroCarg=NS.sg.getValue().toString();
		              				   var   rubroAbono =NS.r.getValue().toString();
		              				   var subRubroAbono=NS.sr.getValue().toString();
		              					
		              				   var estatusMov="A";
		              				  var  estatusImporte="P";
		              				   var firmante1="firmante1";
		              				   var firmante2="firmante2";
		              				   var   noDocto="NULL";
		              				   var  spot=Ext.getCmp(PF + 'txtSpot').getValue();
		              				   var pntsFwd=Ext.getCmp(PF + 'txtPtsForward').getValue();
		              				  ForwardsAction.agregarFwds(
		              				parseInt(noEmpresa),
		  	              				     idDivVenta,
		  	              			parseInt(bancoCargo),
		  	              				     cheCargo,
		  	              				     idDivCompra,
		  	              			parseInt(bancoAbono),
		  	              					 cheAbono,
		  	              			parseInt(formaPago),
		  	              		 parseFloat(importePago),
		  	              		 parseFloat(importeFwd),
		  	              		 parseFloat(tipoCambio),
		   	              				    fecAlta,
		  	              				    fecVencimiento,
		  	              				parseInt(noInstitucion),
		  	              				     nomContacto,
		  	              				 parseInt(idBancoBenef),
		  	              				     idCheBenef,
		  	              				     rubroCarg,
		  	              				    subRubroCarg,
		  	              				    rubroAbono,
		  	              				    subRubroAbono,
		  	              				  estatusMov,
		  	              				  estatusImporte,
		  	              				   firmante1,
		  	              				    firmante2,
		  	              				   noDocto,
		  	              				  parseFloat(spot),
		  	              				parseFloat(pntsFwd),
		  	              			desc_institucion,
	  	              				NS.idUsuario,
		  	              				  
				  	              			function(response, e){
			                					if(response !== null && response !== '' ){
			                						  
			                						BFwrk.Util.msgShow('' + response, 'INFO');
			                				
			                						if(response.indexOf('correct') > 0){
			                						
													 	
			                						}
			                					}
			                					
			                				}
				                      );
		              				limpiar();
		              				NS.g.setValue("");
		              				 NS.sg.setValue("");
		              				 NS.r.setValue("");
		              				  NS.sr.setValue("");
	              					   
	              					   
	              					   }
	              				   
	              				   
	              				   
	              				   
									
	              				   /*var   noEmpresa=911;
		              				   var  idDivVenta="DLS";
		              				   var bancoCargo=12;
		              				   var   cheCargo="1235468412";
		              				  var  idDivCompra="MN";
		              				   var bancoAbono=14;
		              				   var cheAbono="65468435351";
		              				 var formaPago=1;
		              				   var  importePago=0.05;
		              				   var importeFwd=12.0;
		              				   var   tipoCambio=24.0;
		              				   var  fecAlta="08/08/08";
		              				   var fecVencimiento="09/09/09";
		              				   var   noInstitucion=2744;
		              				   var  nomContacto="angel";
		              				   var idBancoBenef=12.0;
		              				   var  idCheBenef="654213541";
		              				   var  rubroCarg="capital";
		              				   var subRubroCarg="capital fijo";
		              				  var   rubroAbono="caja";
		              				   var  subRubroAbono="dinero";
									   var estatusMov='s';
		              				  var  estatusImporte="s";
		              				   var  firmante1="firmante1";
		              				   var firmante2="firmante2";
		              				   var   noDocto="treinta30";
		              				   var  spot=12.0;
		              				   var pntsFwd=15.0;
		  	              				       				   
	              				   
	              				   */
	              				   
	              
	              				   
	              				   
	              				   
								}
			        		}
			        	}
					},{
					    xtype: 'button',    
					    text: 'Limpiar',
					    x: 840,
					    y: 500,
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
//									alert(fecha);
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
			height: 460,
			frame: true,
			autoScroll: true,
			layout: 'absolute',
			id: PF + 'CompraVentaDivisas',
			name: PF+ 'CompraVentaDivisas',
			renderTo: NS.tabContId,
			items :[
			        NS.lblEmpresa,
			        NS.txtEmpresa,
			        NS.cmbEmpresas,
				    NS.ContBoton
				   ]
	  });
		NS.CompraTransfer.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	});