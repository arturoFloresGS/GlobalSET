Ext.onReady(function (){
	
	var NS = Ext.namespace('apps.SET.Impresion.Impresion.CartasPorEntregar');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.folio='';
	NS.opciones='I';
	NS.opcionesSol='solicitantes';
	NS.b=false;
	
	
	//Para evitar el desface de los grids
	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
	
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: CartasPorEntregarAction.llenaBanco,
		fields:   
		[
		 	{name: 'idBanco'},
		 	{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records) {
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen bancos');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg: "Cargando Bancos..."});
	NS.storeBanco.load();
	
	NS.storeLlenarGridCartas = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {folio: '', idBanco:'', tipo:'', estatus:'',fechaIni:'',fechaFin:''},
		paramOrder: ['folio','idBanco', 'tipo', 'estatus','fechaIni','fechaFin'],
		root: '',
		directFn: CartasPorEntregarAction.obtenerCartasE,
		fields:
		[
		 	{name: 'idEmision'},
		 	{name: 'idBanco'},
		    {name: 'descBanco'},
		    {name: 'nomSolicitante'},
		    {name: 'nomSolicitante2'},
		 	{name: 'fechaI'},
		 	{name: 'tipo'},
		 	{name: 'estatus'},
		 	{name: 'motCancelacion'},
		 	{name: 'fechaE'},
		 	{name: 'usuEntrega'}
		 	
		 	
		],
		listeners:
		{
			load: function(s, records)
			{
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay datos que mostrar');
				}
				NS.calcularRenglones();
			}
		}
	});
	
	
	
	NS.storeLlenarComboSolicitantes = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {tipoSol: ''+NS.opcionesSol},
		paramOrder: ['tipoSol'],
		root: '',
		directFn: CartasPorEntregarAction.obtenerSolicitantes,
		fields:
			[
			 {name: 'idPersona'},
			 {name: 'nombre'}
			 
			 ],
			 idProperty: 'idPersona',
			 listeners:
			 {
				 load: function(s, records)
				 {
					 if(records.length == null || records.length <= 0){
						 Ext.Msg.alert('SET', 'No hay solicitantes que mostrar');
					 }else{
					 }
				 }
			 }
	});
	
	
	NS.storeLlenarGrid = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {folio: '', estatus:'', tipo:''},
		paramOrder: ['folio','estatus', 'tipo'],
		root: '',
		directFn: CartasPorEntregarAction.obtenerCartas,
		fields:
		[
		 	{name: 'folioSet'},
		 	{name: 'nomEmpresa'},
		    {name: 'noProveedor'},
		    {name: 'nombreProveedor'},
		 	{name: 'nombreBanco'},
		 	{name: 'idChequera'},
		 	{name: 'noCheque'},
		 	{name: 'importe'},
		 	{name: 'divisa'},
		 	{name: 'idEmision'},
		 	{name: 'cuenta'},
		 	{name: 'claveP'},
		 	{name: 'noCheque'},
		 	//{name: 'beneficiario'},
		 	{name: 'tipoC'},
		 	{name: 'estatus'}
		],
		listeners:
		{
			load: function(s, records)
			{
				
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'Se cancelaron los cheques de esta carta');
				}
				NS.calcularRenglones();
			}
		}
	});
	
	
	
	
	
	 NS.storeLlenarComboEmpresa = new Ext.data.DirectStore({
			paramsAsHash: true,
			root: '',
			directFn: GlobalAction.llenarComboEmpresasUsuario,
			fields:
			[
			    
			    {name: 'id'},
			 	{name: 'descripcion'}
			 	
			],
			idProperty: 'id',
			listeners:
			{
				load: function(s, records)
				{
					if(records.length == null || records.length <= 0){
						Ext.Msg.alert('SET', 'No hay empresas que mostrar');
					}else{
						
					}
						
				}
			}

		});
	    var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storeLlenarComboEmpresa, msg: "Cargando Datos ..."});
		NS.storeLlenarComboEmpresa.load();
	
	
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'criterio',//identificador del store
		fields: [
			{name: 'criterio'},
			{name: 'valor'}
		]
	});
	
	
	NS.storeFolio = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {fechaIni:apps.SET.FEC_HOY,fechaFin:apps.SET.FEC_HOY},
		paramOrder: ['fechaIni','fechaFin'],
		directFn: CartasPorEntregarAction.llenaFolio,
		fields:   
		[
		 	{name: 'idEmision'}
		],
		listeners: {
			load: function(s, records) {
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen folios de cartas para las fechas');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFolio, msg: "Cargando..."});
	NS.storeFolio.load();

	
	NS.lblTipo = new Ext.form.Label({
		text: 'Tipo de Carta',
		x: 380,
		y: 40		
	});
	
	NS.datosComboTipo = [
	                    	   ['ACERT', 'CARTA CERTIFICADO'],
	                    	   ['ACHEQ', 'CARTA CHEQUE DE CAJA'],
	                    	   ['CCERT', 'CANCELACION CARTA CERTIFICADA'],
	                    	   ['CCHEQ', 'CANCELACION CARTA CHEQUE DE CAJA']
	                    	];	       		
	                    	                   	 
	                    	NS.storeTipo = new Ext.data.SimpleStore({
	                    		idProperty: 'idTipo',
	                    	    fields: [
	                    	             {name: 'idTipo'},
	                    	             {name: 'descTipo'}
	                    	    ]
	                    	});
	                    	NS.storeTipo.loadData(NS.datosComboTipo);
	

	NS.cmbTipo = new Ext.form.ComboBox({
		store: NS.storeTipo,
		id: PF + 'cmbTipo',
		name: PF + 'cmbTipo',
		x: 380,
		y: 55,
		width: 250,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idTipo',
		displayField: 'descTipo',
		autocomplete: true,
		emptyText: 'Tipo',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function() {
					
				}
			}
		}
		
	});
	

	 NS.criterioLbl = new Ext.form.Label({
		 text: 'Criterio',
		 x:0,
		 y:55
	 });
	 
	 NS.datosComboCriterio = [
	                          ['FECHAS', 'FECHAS'],
	                          ['IMPORTES', 'IMPORTES'],
	                          ['CLAVE CONTROL', 'CLAVE CONTROL'],
	                          ['NUM_CHEQUE', 'NUM_CHEQUE'],
	                          ['CHEQUERA', 'CHEQUERA'],
	                          ['BANCO', 'BANCO'],
	                          ['PROVEEDOR', 'PROVEEDOR']
	               		];	       		
	               		                        	
	               		NS.storeCriterio = new Ext.data.SimpleStore({
	               			idProperty: 'idCriterio',
	               			fields: [
	               			         {name: 'idCriterio'},
	               			         {name: 'descCriterio'}
	               			         ]
	               		});
	               		NS.storeCriterio.loadData(NS.datosComboCriterio);
	 
	 
	 NS.Criterio = new Ext.form.ComboBox({
			store: NS.storeCriterio,
			id: PF + 'cmbCriterio',
			name: PF + 'cmbTipoImpresion',
			width: 210,
			x: 105,
			y: 55,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
			valueField: 'id_emision',
			displayField: 'id_emision',
		    autocomplete: true,
		    emptyText: 'SELECCIONE CRITERIO',
		    triggerAction: 'all',
		    listeners:{
		    	select:{
		    		fn: function(combo, valor)
		    		{
		    			if(combo.getValue() == 'FECHAS'){
		    				NS.txtFechaInicial.setVisible(true);
		    				NS.txtFechaFinal.setVisible(true);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.idChequeraTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'IMPORTES'){
		    				NS.importeUno.setVisible(true);
		    			    NS.importeDos.setVisible(true);
		    			    NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.clavePropuestaTxt.setVisible(false);
		    				NS.numeroChequeTxt.setVisible(false);
		    				NS.idChequeraTxt.setVisible(false);
		    				NS.bancoTxt.setVisible(false);
		    				NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'CLAVE CONTROL'){
		    				NS.clavePropuestaTxt.setVisible(true);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.idChequeraTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'NUM_CHEQUE'){
		    				NS.numeroChequeTxt.setVisible(true);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.idChequeraTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'CHEQUERA'){
		    				NS.idChequeraTxt.setVisible(true);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'BANCO'){
		    				NS.bancoTxt.setVisible(true);
		    				NS.idChequeraTxt.setVisible(false);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'PROVEEDOR'){
		    				NS.proveedorTxt.setVisible(true);
		    				NS.bancoTxt.setVisible(false);
		    				NS.idChequeraTxt.setVisible(false);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			}
		    		}
		    	}
		    }
		});
	 
	 
	 
	 NS.txtFechaInicial = new Ext.form.DateField({
			id: PF + 'txtFechaInicial',
			name: PF + 'txtFechaInicial',
			format:'d/m/Y',
			x: 105,
			y: 100,
			width: 100,
			hidden: true
			
		});
		
		NS.txtFechaFinal = new Ext.form.DateField({
			id: PF + 'txtFechaFinal',
			name: PF + 'txtFechaFinal',
			format:'d/m/Y',
			x: 215,
			y: 100,
			width: 100,
			hidden: true
			
		});
		
		 NS.importeUno = new Ext.form.NumberField({
			   	width: 100,
			   	id: PF + 'importeUno',
				name: PF + 'importeUno',
				x: 105,
				y: 100,
				hidden: true
		});
		 
		 NS.importeDos = new Ext.form.NumberField({
			   	width: 100,
			   	id: PF + 'importeDos',
				name: PF + 'importeDos',
				x: 215,
				y: 100,
				hidden: true
		});
		 
		 
		 NS.clavePropuestaTxt = new Ext.form.TextField({
			   	width: 200,
			   	id: PF + 'clavePropuestaTxt',
				name: PF + 'clavePropuestaTxt',
				x: 105,
				y: 100,
				hidden: true
		});
		 
		 NS.numeroChequeTxt = new Ext.form.TextField({
			   	width: 70,
			   	id: PF + 'numeroChequeTxt',
				name: PF + 'numeroChequeTxt',
				x: 105,
				y: 100,
				hidden: true
		});
		 
		 NS.idChequeraTxt = new Ext.form.TextField({
			   	width: 200,
			   	id: PF + 'idChequeraTxt',
				name: PF + 'idChequeraTxt',
				x: 105,
				y: 100,
				hidden: true
		});
		 
		 NS.bancoTxt = new Ext.form.TextField({
			   	width: 200,
			   	id: PF + 'bancoTxt',
				name: PF + 'bancoTxt',
				x: 105,
				y: 100,
				hidden: true
		});
		 
		 NS.proveedorTxt = new Ext.form.TextField({
			   	width: 200,
			   	id: PF + 'proveedorTxt',
				name: PF + 'proveedorTxt',
				x: 105,
				y: 100,
				hidden: true
		});

	 
	 NS.valorLbl = new Ext.form.Label({
		 text: 'Valor',
		 x:0,
		 y:100
	 });
	 
	  NS.lblFechaIni = new Ext.form.Label({
			text: 'Fecha Inicial',
			x: 35,
			y: 3
			
		});
		
		NS.txtFechaIni = new Ext.form.DateField({
			id: PF + 'txtFechaIni',
			name: PF + 'txtFechaIni',
			format:'d/m/Y',
			x: 140,
			y: 0,
			width: 150,
			value: apps.SET.FEC_HOY,
			listeners:{
	        	change:{
	        		fn:function(caja, valor){
						var valIni='';
							if(valor!=''){
								valIni=NS.cambiarFecha(''+valor);
							}else{
								return Ext.Msg.alert('SET',"Debe seleccionar una fecha inicial"); 
							}
						var fechaDos=NS.txtFechaFin.getValue();	
						var valFin='';
							if(fechaDos!=''){
								valFin=NS.cambiarFecha(''+fechaDos);
							}else{
									return Ext.Msg.alert('SET',"Debe seleccionar una fecha final"); 
							}
							if(valor>fechaDos){
								Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
							}else{
						   //alert(valIni);
						   //alert(valFin);
						    	NS.storeFolio.baseParams.fechaIni=''+valIni;
						    	NS.storeFolio.baseParams.fechaFin=''+valFin;
						    	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFolio, msg: "Cargando..."});
						    	NS.storeFolio.load();
								NS.cmbFolio.reset()
		        			 
		        		}
						
	        		 }
	        	}
			}
		});
		
		NS.lblFechaFin = new Ext.form.Label({
			text: 'Fecha Final',
			x: 380,
			y: 3
			
		});
			
		NS.txtFechaFin = new Ext.form.DateField({
			id: PF + 'txtFechaFinal',
			name: PF + 'txtFechaFinal',
			format:'d/m/Y',
			x: 480,
			y: 0,
			width: 150,
			value: apps.SET.FEC_HOY,
			listeners:{
	        	change:{
	        		fn:function(caja, valor){
	        
						var fechaUno=NS.txtFechaIni.getValue();
						var valIni='';
						if(fechaUno!=''){
							valIni=NS.cambiarFecha(''+fechaUno);
						}else{
								return Ext.Msg.alert('SET',"Debe seleccionar una fecha inicial"); 
						}

						var valFin='';
						if(valor!=''){
							valFin=NS.cambiarFecha(''+valor);
						}else{
								return Ext.Msg.alert('SET',"Debe seleccionar una fecha inicial"); 
						}
						if(fechaUno>valor){
							Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
						}else{
					   //alert(valIni);
					   //alert(valFin);
							NS.storeFolio.baseParams.fechaIni=''+valIni;
					    	NS.storeFolio.baseParams.fechaFin=''+valFin;
					    	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFolio, msg: "Cargando..."});
					    	NS.storeFolio.load();
							NS.cmbFolio.reset();
	        			 
	        		}
					}
				}
			}
			});
	 
		NS.lblFolio = new Ext.form.Label({
			text: 'Folio',
			x:35,
			y:40
		});
		 
		 /*NS.txtFolio = new Ext.form.TextField({
			 width: 100,
			 id: PF + 'txtFolio',
			 name: PF + 'txtFolio',
			 x: 35,
			 y: 55,
			 blankText : 'Este campo es requerido',
			
		 });*/
		 
		 NS.cmbFolio = new Ext.form.ComboBox({
				store: NS.storeFolio,
				id: PF + 'cmbFolio',
				name: PF + 'cmbFolio',
				x: 35,
				y: 55,
				width: 100,
				typeAhead: true,
				mode: 'local',
				selecOnFocus: true,
				forceSelection: true,
				valueField: 'idEmision',
				displayField: 'idEmision',
				autocomplete: true,
				emptyText: 'Folios Cartas',
				triggerAction: 'all',
				value: '',
				visible: true,
				listeners: {
					select: {
						fn: function(combo, valor) {

						}
					}
				}
				
		});
			
		 
		 NS.lblBanco = new Ext.form.Label({
				text: 'Banco',
				x: 160,
		    	y: 40,		
			});
			
			NS.txtBanco = new Ext.form.TextField({
				id: PF + 'txtBanco',
				name: PF + 'txtBanco',
				x: 160,
		    	y: 55,
				width: 35,	
				listeners: {
		 			change: {
		 				
		 				fn: function(caja, valor) {
		 					
		 				if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
								BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
		 					
						}
		 			}
		 		}	 	
				
			});
		 
		 NS.cmbBanco = new Ext.form.ComboBox({
				store: NS.storeBanco,
				id: PF + 'cmbBanco',
				name: PF + 'cmbBanco',
				x: 205,
				y: 55,
				width: 150,
				typeAhead: true,
				mode: 'local',
				selecOnFocus: true,
				forceSelection: true,
				valueField: 'idBanco',
				displayField: 'descBanco',
				autocomplete: true,
				emptyText: 'Banco',
				triggerAction: 'all',
				value: '',
				visible: false,
				listeners: {
					select: {
						
						fn: function(combo, valor) {
							BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBanco.getId());
							
						}
					}
				}
				
			});
	
	NS.empresaLbl = new Ext.form.Label({
		text: 'Empresa',
		x:0,
		y:10
	});
	 
	 NS.empresaTxt = new Ext.form.TextField({
		 width: 50,
		 id: PF + 'empresaTxt',
		 name: PF + 'empresaTxt',
		 x: 50,
		 y: 10,
		 blankText : 'Este campo es requerido',
		 listeners:
		 {
			 change:
			 {
				 fn: function(caja, valor)
				 {
					 BFwrk.Util.updateTextFieldToCombo(PF + 'empresaTxt', NS.cmbEmpresa.getId());
				 }
			 }
		 }
	 });
	 
	 NS.cmbEmpresa = new Ext.form.ComboBox({
			store: NS.storeLlenarComboEmpresa,
			id: PF + 'cmbEmpresa',
			name: PF + 'cmbEmpresa',
			width: 210,
			x: 105,
			y: 10,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
			valueField: 'id',
			displayField: 'descripcion',
			autocomplete: true,
			emptyText: 'SELECCIONE EMPRESA',
			triggerAction: 'all',
			listeners:{
				select:{
					fn: function(combo, valor)
					{
						BFwrk.Util.updateComboToTextField(PF + 'empresaTxt', NS.cmbEmpresa.getId());
					}
				}
				
			}
		});
	
	 NS.solicitanteLbl = new Ext.form.Label({
		 text: 'Solicitante:',
		 x:0,
		 y:275
	 });
	 
	
	 NS.identificacionLbl = new Ext.form.Label({
		 text: 'Identificacion:',
		 x:0,
		 y:310
	 });
	
	NS.fechaEntregaLbl = new Ext.form.Label({
		 text: 'Fecha Entrega:',
		 x:0,
		 y:345
	 }); 
	
	NS.txtFechaEntrega = new Ext.form.DateField({
		id: PF + 'txtFechaEntrega',
		name: PF + 'txtFechaEntrega',
		format:'d/m/Y',
		x: 75,
		y: 345,
		width: 200,
		value: apps.SET.FEC_HOY
		
	});
	
	NS.lblFechaImp = new Ext.form.Label({
		text: 'Fecha de Impresión:',
		x: 700,
		y: 275
			
	});
		
	NS.txtFechaImp = new Ext.form.DateField({
			id: PF + 'txtFechaImp',
			name: PF + 'txtFechaImp',
			format:'d/m/Y',
			x: 800,
			y: 275,
			width: 150,
			value: apps.SET.FEC_HOY,
			listeners:{
	        	change:{
	        		fn:function(caja, valor){
	        			NS.b=true;
						NS.valImp='';
							if(valor!=''){
								NS.valImp=NS.cambiarFecha(''+valor);
							}else{
								return Ext.Msg.alert('SET',"Debe seleccionar una fecha de impresión"); 
							}
	        		 }
	        	}
			}
		});
	
	NS.identificacionTxt = new Ext.form.TextField({
	   	width: 200,
	   	id: PF + 'identificacionTxt',
		name: PF + 'identificacionTxt',
		x: 75,
		y: 310,
		readOnly: true
		
	});
	
	NS.cmbSolicitantes = new Ext.form.ComboBox({
		store: NS.storeLlenarComboSolicitantes,
		id: PF + 'cmbSolicitantes',
		name: PF + 'cmbSolicitantes',
		width: 200,
		x: 75,
		y: 275,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idPersona',
		displayField: 'nombre',
		autocomplete: true,
		emptyText: 'SELECCIONE SOLICITANTE',
		triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor){
					BFwrk.Util.updateComboToTextField(PF + 'identificacionTxt', NS.cmbSolicitantes.getId());
				}
			}
			
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarComboSolicitantes, msg: "Cargando Solicitantes ..."});
	NS.storeLlenarComboSolicitantes.load();
	
	NS.calcularRenglones = function(){
		var seleccionados =  NS.gridDatosCheques.getSelectionModel().getSelections();
		var importeMn = 0;
		var seleccionMn = 0;
		var importeDls = 0;
		var seleccionDls = 0;
		
		for(var i = 0; i < seleccionados.length; i++){
			if(seleccionados[i].get('divisa') == 'PESOS'){
				importeMn = importeMn + seleccionados[i].get('importe');
				seleccionMn++;
			}else{
				importeDls = importeDls + seleccionados[i].get('importe');
				seleccionDls++;
			}
				
		}

	}
	
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
	}
	
	
	NS.cambiarFecha = function(fecha){
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		var mes;
		
		for(var i=0;i<12;i=i+1)
		{
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
	
	
	
	NS.visualizar = function(){
		var registroSeleccionado = NS.gridDatosCheques.store.data.items;
		NS.idBanco= NS.gridDatosCartas.getSelectionModel().getSelections()[0].get('idBanco');
			if (registroSeleccionado.length <= 0)
				Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			else{
				
				NS.idEmpresa= NS.gridDatosCheques.store.data.items[0].get('idEmpresa');
				NS.descEmpresa= NS.gridDatosCheques.store.data.items[0].get('nomEmpresa');
				NS.idProveedor = NS.gridDatosCheques.store.data.items[0].get('idProveedor');
				NS.descProveedor = NS.gridDatosCheques.store.data.items[0].get('nombreProveedor');
				NS.claveP= NS.gridDatosCheques.store.data.items[0].get('claveP');
				NS.importe = NS.gridDatosCheques.store.data.items[0].get('importe'); 
				NS.cuenta = NS.gridDatosCheques.store.data.items[0].get('idChequera');
				NS.folio = NS.gridDatosCheques.store.data.items[0].get('folio');
				NS.idEmision= NS.gridDatosCheques.store.data.items[0].get('idEmision');
				NS.tipoCar=NS.gridDatosCheques.store.data.items[0].get('tipoC');
				
				CartasPorEntregarAction.obtieneDatos(''+ NS.idEmision, ''+NS.idBanco, function(resultado, e){
					
					if (resultado != null && resultado != '' && resultado != undefined)
					{
						
						var jsonDatos = '[{"solicitante":"' + resultado[0].solicitante + '","identificacion":"' + resultado[0].identificacion + 
						'","autorizacion1":"' + resultado[0].autorizacion1 +  '","autorizacion2":"' + resultado[0].autorizacion2 +
					 	'","b1":"' + resultado[0].b1 + '","b2":"' + resultado[0].b2 + '","b3":"' + resultado[0].b3 + '","b4":"' + resultado[0].b4 + 
					 	'","c1":"' + resultado[0].c1 + '","c2":"' + resultado[0].c2 + '","c3":"' + resultado[0].c3 + '","c4":"' + resultado[0].c4 + 
					 	'","c5":"' + resultado[0].c5 + '","c6":"' + resultado[0].c6 + '","proveedor":"' + NS.descProveedor + '","empresa":"' + NS.descEmpresa +
					 	'","calle":"' + resultado[0].calle + '","colonia":"' + resultado[0].colonia + '","ciudad":"' + resultado[0].ciudad + '","cuenta":"' + NS.cuenta +
					 	'","cp":"' + resultado[0].cp +  '","estado":"' + resultado[0].estado + '","emision":"' + NS.idEmision +
					 	 '","tipoCar":"' + NS.tipoCar + '","lugarFecha":"' + resultado[0].lugarFecha + '" }]' ;
						
						
						
						NS.vecBeneficiarios = new Array ();
						for (var i = 0; i < registroSeleccionado.length; i++) {
							NS.beneficiario = {};
							NS.beneficiario.importe = registroSeleccionado[i].get('importe');
							NS.beneficiario.beneficiario = registroSeleccionado[i].get('nombreProveedor');
							NS.beneficiario.folio = registroSeleccionado[i].get('folio');
							NS.beneficiario.noCheque = registroSeleccionado[i].get('noCheque');
							NS.vecBeneficiarios[i] = NS.beneficiario;
						}
						
						var beneficiarios = Ext.util.JSON.encode(NS.vecBeneficiarios);
						if(NS.b==false){
							NS.valImp=apps.SET.FEC_HOY;
							NS.b=true;
						}
						CartasPorEntregarAction.generaPDF(jsonDatos, beneficiarios,''+NS.dif, NS.tipoCar, NS.valImp, function(resultado, e){
							
							if (resultado != null && resultado != '' && resultado != undefined)
							{
								
								strParams = '?nomReporte=cartas';
								strParams += '&'+'nomParam1=rutaArchivo';
								strParams += '&'+'valParam1='+resultado;
								strParams += '&'+'nomParam2=nombreArchivo';
								strParams += '&'+'valParam2=carta';
								window.open("/SET/jsp/bfrmwrk/gnrators/abretxt.jsp"+strParams);
							}else{
								Ext.Msg.alert('SET', 'No se pudo generar la carta');
							}
							
						});
				
				
					}
					else
						Ext.Msg.alert('SET', 'No se pudo generar la carta');
				});
			
								
			}
	};
	
	
	NS.limpiarTodo = function(){
		//NS.txtFolio.setValue('');
		NS.txtFechaEntrega.setValue(apps.SET.FEC_HOY);
		NS.cmbSolicitantes.setValue('');
		NS.identificacionTxt.setValue('');
		NS.txtBanco.setValue('');
		NS.cmbBanco.setValue('');
		NS.cmbTipo.setValue('');
		NS.txtFechaIni.setValue(apps.SET.FEC_HOY);
		NS.txtFechaFin.setValue(apps.SET.FEC_HOY);
		NS.txtFechaImp.setValue(apps.SET.FEC_HOY)
	    NS.cmbFolio.reset();
		NS.storeFolio.removeAll();
		NS.storeLlenarGrid.removeAll();
		NS.gridDatosCheques.getView().refresh();
		NS.storeLlenarGridCartas.removeAll();
		NS.gridDatosCartas.getView().refresh();
	}
	

	NS.manejoDeCriterios = function(criterio){
		var datosClase = NS.gridDatos.getStore().recordType;
		var dato = NS.storeDatos.data.items; 
		
		if(criterio == 'CLAVE CONTROL'){
			for(var i=0; i < dato.length; i++){
				if(dato[i].get('criterio') == 'CLAVE CONTROL'){
					NS.storeDatos.remove(dato[i]);
				}
			}
			
			var datos = new datosClase({
            	criterio: 'CLAVE CONTROL',
				valor: NS.clavePropuestaTxt.getValue()
        	});
			NS.storeDatos.insert(dato.length, datos);
    		NS.gridDatos.getView().refresh();
    		NS.clavePropuestaTxt.setDisabled(true);
			
		}else if(criterio == 'FECHAS'){
			for(var i=0; i < dato.length; i++){
				if(dato[i].get('criterio') == 'FECHAS'){
					NS.storeDatos.remove(dato[i]);
				}
			}
			var datos = new datosClase({
            	criterio: 'FECHAS',
				valor:NS.cambiarFecha('' + NS.txtFechaInicial.getValue()) + " a " + NS.cambiarFecha('' + NS.txtFechaFinal.getValue())
        	});
			NS.storeDatos.insert(dato.length, datos);
    		NS.gridDatos.getView().refresh();
    		NS.txtFechaInicial.setDisabled(true);
    		NS.txtFechaFinal.setDisabled(true);
		}else if(criterio == 'IMPORTES'){
			for(var i=0; i < dato.length; i++){
				if(dato[i].get('criterio') == 'IMPORTES'){
					NS.storeDatos.remove(dato[i]);
				}
			}
			var datos = new datosClase({
            	criterio: 'IMPORTES',
				valor: '$' + NS.importeUno.getValue() + " >< $" + NS.importeDos.getValue()
        	});
			NS.storeDatos.insert(dato.length, datos);
    		NS.gridDatos.getView().refresh();
    		NS.importeUno.setDisabled(true);
    		NS.importeDos.setDisabled(true);
		}else if(criterio == 'NUM_CHEQUE'){
			for(var i = 0; i < dato.length; i++){
				if(dato[i].get('criterio') == 'NUM_CHEQUE'){
					NS.storeDatos.remove(dato[i]);
				}
			}
			var datos = new datosClase({
				criterio: 'NUM_CHEQUE',
				valor: NS.numeroChequeTxt.getValue()
			});
			NS.storeDatos.insert(dato.length, datos);
    		NS.gridDatos.getView().refresh();
    		NS.numeroChequeTxt.setDisabled(true);
		}else if(criterio == 'CHEQUERA'){
			for(var i = 0; i < dato.length; i++){
				if(dato[i].get('criterio') == 'CHEQUERA'){
					NS.storeDatos.remove(dato[i]);
				}
			}
			var datos = new datosClase({
				criterio: 'CHEQUERA',
				valor: NS.idChequeraTxt.getValue()
			});
			NS.storeDatos.insert(dato.length, datos);
    		NS.gridDatos.getView().refresh();
    		NS.idChequeraTxt.setDisabled(true);
		}else if(criterio == 'BANCO'){
			for(var i = 0; i < dato.length; i++){
				if(dato[i].get('criterio') == 'BANCO'){
					NS.storeDatos.remove(dato[i]);
				}
			}
			var datos = new datosClase({
				criterio: 'BANCO',
				valor: NS.bancoTxt.getValue()
			});
			NS.storeDatos.insert(dato.length, datos);
    		NS.gridDatos.getView().refresh();
			NS.bancoTxt.setDisabled(true);
		}else if(criterio == 'PROVEEDOR'){
			for(var i = 0; i < dato.length; i++){
				if(dato[i].get('criterio') == 'PROVEEDOR'){
					NS.storeDatos.remove(dato[i]);
				}
			}
			var datos = new datosClase({
				criterio: 'PROVEEDOR',
				valor: NS.proveedorTxt.getValue()
			});
			NS.storeDatos.insert(dato.length, datos);
    		NS.gridDatos.getView().refresh();
			NS.proveedorTxt.setDisabled(true);
		}
	}
	
	
	/*NS.seleccionGridCheques = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false,
		
		listeners:{
			rowselect: {
				fn:function(r, rowIndex){
					NS.calcularRenglones();
					
				}
			},
			rowdeselect: {
				fn:function(r, rowIndex){
					NS.calcularRenglones();
				}
			}
		}
	});  */
	
	
	  NS.columnasDatosCheques = new Ext.grid.ColumnModel([
      //NS.seleccionGridCheques,
      new Ext.grid.RowNumberer(),
	  {header: 'FOLIO SET', width: 100, dataIndex:'folioSet',hidden: true},
	  {header: 'EMPRESA', width: 200, dataIndex: 'nomEmpresa'},
	  {header: 'NO. PROVEEDOR', width: 100, dataIndex: 'noProveedor'},	
	  {header: 'NOM. PROVEEDOR', width: 200, dataIndex: 'nombreProveedor'},
	  {header: 'BANCO', width: 150, dataIndex: 'nombreBanco'},
	  {header: 'CHEQUERA', width: 100, dataIndex: 'idChequera'},
	  {header: 'NO. CHEQUE', width: 70, dataIndex: 'noCheque'},                                      	
	  {header: 'IMPORTE', width: 120, dataIndex: 'importe', renderer: BFwrk.Util.rendererMoney ,align:'right'},
	  {header: 'DIVISA', width: 120, dataIndex: 'divisa'},
	  {header: 'ID_EMISION', width: 120, dataIndex: 'idEmision'},
	  //{header: 'NO CUENTA', width: 120, dataIndex: 'idChequera'},
	  {header: 'CLAVE PROPUESTA', width: 120, dataIndex: 'claveP'},
	  //{header: 'NO. CHEQUE', width: 120, dataIndex: 'noCheque',hidden:true},
	  //{header: 'BENEFICIARIO', width: 120, dataIndex: 'beneficiario', hidden: true},
	  {header: 'TIPO CARTA', width: 120, dataIndex: 'tipoC'}
	  
	]);
	  
	  NS.seleccionGridCartas = new Ext.grid.CheckboxSelectionModel({
			singleSelect: true,
			listeners:{
				rowselect: {
					fn:function(r, rowIndex){
						NS.calcularRenglones();
						
					}
				},
				rowdeselect: {
					fn:function(r, rowIndex){
						NS.calcularRenglones();
					}
				}
			}
		});  
		
		
	NS.columnasDatosCartas = new Ext.grid.ColumnModel([
	      NS.seleccionGridCartas,
	      //new Ext.grid.RowNumberer(),
		  {header: 'FOLIO', width: 100, dataIndex:'idEmision'},
		  {header: 'ID BANCO', width: 100, dataIndex: 'idBanco'},
		  {header: 'BANCO', width: 100, dataIndex: 'descBanco'},	
		  {header: 'SOLICITANTE', width: 200, dataIndex: 'nomSolicitante'},
		  {header: 'SOLICITANTE ALTERNO', width: 200, dataIndex: 'nomSolicitante2', hidden: true},
		  {header: 'FECHA DE IMPRESION', width: 150, dataIndex: 'fechaI'},
		  {header: 'TIPO', width: 100, dataIndex: 'tipo'},
		  {header: 'ESTATUS', width: 100, dataIndex: 'estatus'},
		  {header: 'MOTIVO DE CANCELACION',width: 200, dataIndex: 'motCancelacion'},
		  {header: 'FECHA DE ENTREGA',width: 150, dataIndex: 'fechaE'},
		  {header: 'USUARIO ENTREGA',width: 200, dataIndex: 'usuEntrega'}
		]);

	  
	  
	  
	
	NS.gridDatosCheques = new Ext.grid.GridPanel({
		store: NS.storeLlenarGrid,
		id: PF + 'gridDatosCheques',
		x: 0,
		y: 5,
		cm: NS.columnasDatosCheques,
		//sm: NS.seleccionGridCheques,
		lockColumns: 3, 
		width: 960,
		height: 200,
		border: false,
		stripeRows: true,
		listeners:{
			rowdblClick:{
				fn:function(grid){
					
				}
			}
		}
    
	});
	
	
	
	NS.gridDatos  = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		id: PF+'gridDatos',
		name: PF+'gridDatos',
		width: 370,
       	height: 130,
		x : 380,
		y : 0,
		frame: true,
		title :'',
		columns : [ 
		{
			id :'criterio',
			header :'Criterio',
			width :170,
			dataIndex :'criterio'
		}, 
		{
			header :'Valor',
			width :185,
			dataIndex :'valor'
		} ],
		listeners:{
			rowdblClick:{
				fn:function(grid, index){
		
					var renglon = grid.getSelectionModel().getSelected();
					
					switch(renglon.get('criterio')){
						case 'CLAVE CONTROL':
						
							NS.clavePropuestaTxt.setVisible(true);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.idChequeraTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			    
		            		NS.clavePropuestaTxt.setDisabled(false);
		            		NS.clavePropuestaTxt.setValue('');
							break;
						case 'IMPORTES':
							
							NS.importeUno.setVisible(true);
		    			    NS.importeDos.setVisible(true);
		    			    NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.clavePropuestaTxt.setVisible(false);
		    				NS.numeroChequeTxt.setVisible(false);
		    				NS.idChequeraTxt.setVisible(false);
		    				NS.bancoTxt.setVisible(false);
		    				NS.proveedorTxt.setVisible(false);
							
		    				NS.importeUno.setDisabled(false);
		    				NS.importeDos.setDisabled(false);
		    				NS.importeUno.setValue('');
		    				NS.importeDos.setValue('');
							break;
						case 'FECHAS':
							NS.txtFechaInicial.setVisible(true);
		    				NS.txtFechaFinal.setVisible(true);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.idChequeraTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			    
		    			    NS.txtFechaInicial.setDisabled(false);
		    				NS.txtFechaFinal.setDisabled(false);
		    				NS.txtFechaInicial.setValue('');
		    				NS.txtFechaFinal.setValue('');
							break;
						case 'NUM_CHEQUE':
							NS.numeroChequeTxt.setVisible(true);
							NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
							NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.idChequeraTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			    
		    			    NS.numeroChequeTxt.setDisabled(false);
		    			    NS.numeroChequeTxt.setValue('');
							break; 
						case 'CHEQUERA':
							NS.idChequeraTxt.setVisible(true);
							NS.numeroChequeTxt.setVisible(false);
							NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
							NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			    
		    			    NS.idChequeraTxt.setDisabled(false);
		    			    NS.idChequeraTxt.setValue('');
							break;
						case 'BANCO':
							NS.bancoTxt.setVisible(true);
							NS.idChequeraTxt.setVisible(false);
							NS.numeroChequeTxt.setVisible(false);
							NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
							NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
							
		    			    NS.bancoTxt.setDisabled(false);
		    			    NS.bancoTxt.setValue('');
							break;
						case 'PROVEEDOR':
							NS.proveedorTxt.setVisible(true);
							NS.bancoTxt.setVisible(false);
							NS.idChequeraTxt.setVisible(false);
							NS.numeroChequeTxt.setVisible(false);
							NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
							NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    
		    			    NS.proveedorTxt.setDisabled(false);
		    			    NS.proveedorTxt.setValue('');
							break;
					
					}
					NS.cmbCriterio.setValue(renglon.get('criterio'));
					NS.storeDatos.remove(renglon);
					NS.gridDatos.getView().refresh();
					

				}
			}
		}
		           
	});
	
	NS.optOpciones = new Ext.form.RadioGroup({
		id: PF + 'optOpciones',
		name: PF + 'optOpciones',
		x: 650,
		y: 55,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Impresas', name: 'optOpcionesSel', inputValue: 0, checked: true,
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							NS.opciones = 'I'
	 								
		 						}
		 							
		 					}
		 				}
		 			}
		 	},
		 	{boxLabel: 'Entregadas', name: 'optOpcionesSel', inputValue: 1 ,
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							NS.opciones = 'E'
		 								
		 						}
		 						
		 						
		 						
		 					}
		 					
		 				}
		 			}
		 	},
		 	
		 	
		]
	});
	
	NS.optOpcionesSol = new Ext.form.RadioGroup({
		id: PF + 'optOpcionesSol',
		name: PF + 'optOpcionesSol',
		x: 0,
		y: 215,
		columns: 1,
		items:
		[
		 	{boxLabel: 'Solicitante', name: 'optOpcionesSol', inputValue: 0, checked: true,
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarComboSolicitantes, msg: "Cargando Datos ..."});
		 							NS.opcionesSol = 'solicitantes';
		 							NS.storeLlenarComboSolicitantes.baseParams.tipoSol = ''+NS.opcionesSol;
		 							NS.storeLlenarComboSolicitantes.load();
		 							NS.cmbSolicitantes.reset();
		 							NS.identificacionTxt.setValue('');
	 								
		 						}
		 							
		 					}
		 				}
		 			}
		 	},
		 	{boxLabel: 'Solicitante Alterno', name: 'optOpcionesSol', inputValue: 1 ,
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarComboSolicitantes, msg: "Cargando Datos ..."});
		 							NS.opcionesSol = 'alternos';
		 							NS.storeLlenarComboSolicitantes.baseParams.tipoSol = ''+NS.opcionesSol;
		 							NS.storeLlenarComboSolicitantes.load();
		 							NS.cmbSolicitantes.reset();
		 							NS.identificacionTxt.setValue('');
		 						}
		 						
		 						
		 						
		 					}
		 					
		 				}
		 			}
		 	},
		 	
		 	
		]
	});
	
	
	NS.PanelContenedorCriteriosBusqueda = new Ext.form.FieldSet({
		title: 'BUSQUEDA',
		id: PF + 'PanelContenedorCriteriosBusqueda',
		layout: 'absolute',
		width: 978,
		height: 155,
		x: 0,
		y: 0,
		items:[
		       NS.lblFechaIni,
		       NS.txtFechaIni,
		       NS.lblFechaFin,
		       NS.txtFechaFin,
		       //NS.txtFolio,
		       NS.cmbFolio,
		       NS.lblFolio,
		       NS.lblBanco,
		       NS.txtBanco,
		       NS.cmbBanco,
		       NS.lblTipo,
		       NS.cmbTipo,
		       NS.optOpciones,
		       {
		    	   xtype: 'button',
		    	   text: 'Buscar',
		    	   x: 820,
		    	   y: 80,
		    	   width: 80,
		    	   height: 22,
		    	   handler: function(){
		    		   var valor=NS.txtFechaIni.getValue();	
		    		   var valIni='';
						if(valor!=''){
							valIni=NS.cambiarFecha(''+valor);
						}else{
							return Ext.Msg.alert('SET',"Debe seleccionar una fecha inicial"); 
						}
						var fechaDos=NS.txtFechaFin.getValue();	
						var valFin='';
						if(fechaDos!=''){
							valFin=NS.cambiarFecha(''+fechaDos);
						}else{
								return Ext.Msg.alert('SET',"Debe seleccionar una fecha final"); 
						}
						if(valor>fechaDos){
							Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
						}else{
							   NS.storeLlenarGridCartas.baseParams.fechaIni=''+valIni;
							   NS.storeLlenarGridCartas.baseParams.fechaFin=''+valFin;
				    		   NS.storeLlenarGridCartas.baseParams.folio = ''+NS.cmbFolio.getValue();
				    		   NS.storeLlenarGridCartas.baseParams.idBanco = NS.txtBanco.getValue();
				    		   NS.storeLlenarGridCartas.baseParams.tipo = ''+NS.cmbTipo.getValue();
				    		   NS.storeLlenarGridCartas.baseParams.estatus = ''+NS.opciones;
				    		   NS.storeLlenarGrid.removeAll();
				    		   NS.gridDatosCheques.getView().refresh();
				    		   var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridCartas, msg: "Cargando Datos ..."});
				    		   NS.storeLlenarGridCartas.load();
						
						}
		    		   }
		       
		       }
		       
		],
		buttons:[

		]
	
	});
	
	NS.gridDatosCartas = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridCartas,
		id: PF + 'gridDatosCartas',
		x: 0,
		y: 5,
		cm: NS.columnasDatosCartas,
		sm: NS.seleccionGridCartas,
		lockColumns: 3, 
		width: 960,
		height: 140,
		border: false,
		stripeRows: true,
		listeners:{
			rowClick:{
				fn:function(grid){
					
					var registroSeleccionado = NS.gridDatosCartas.getSelectionModel().getSelections();
					 	NS.folio= registroSeleccionado[0].get('idEmision');
					 	NS.estatus= registroSeleccionado[0].get('estatus');
					 	NS.tipo= registroSeleccionado[0].get('tipo');
		    		   	NS.storeLlenarGrid.baseParams.folio = ''+NS.folio;
		    		   	NS.storeLlenarGrid.baseParams.estatus = ''+NS.estatus;
		    		   	NS.storeLlenarGrid.baseParams.tipo = ''+NS.tipo;
		    		   	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGrid, msg: "Cargando Datos ..."});
		    		   	NS.storeLlenarGrid.load();
				}
			}
		}
    
	});
	
	NS.PanelContenedorCartas = new Ext.form.FieldSet({
    	title: 'CARTAS',
    	id: PF + 'PanelContenedorCartas',
    	layout: 'absolute',
    	width: 978,
    	height: 190,
    	x: 0,
    	y: 170,
    	items:[
    	       NS.gridDatosCartas,
    	],
	});
	
	
	NS.PanelContenedorCheques = new Ext.form.FieldSet({
    	title: 'BENEFICIARIOS',
    	id: PF + 'PanelContenedorCheques',
    	layout: 'absolute',
    	width: 978,
    	height: 460,
    	x: 0,
    	y: 370,
    	items:[
    	       NS.gridDatosCheques,
    	  	   NS.cmbSolicitantes,
    	  	   NS.solicitanteLbl,
    	  	   NS.identificacionLbl,
    	  	   NS.identificacionTxt,
    	  	   NS.fechaEntregaLbl,
    	  	   NS.txtFechaEntrega,
    	  	   NS.lblFechaImp,
    	  	   NS.txtFechaImp,
    	  	   NS.optOpcionesSol
    	  	  
    	],
    	buttons:[

				{
					   xtype: 'button',
					   text: 'Excel',
					   handler: function(){
						   var valor=NS.txtFechaIni.getValue();	
			    		   var valIni='';
							if(valor!=''){
								valIni=NS.cambiarFecha(''+valor);
							}else{
								return Ext.Msg.alert('SET',"Debe seleccionar una fecha inicial"); 
							}
							var fechaDos=NS.txtFechaFin.getValue();	
							var valFin='';
							if(fechaDos!=''){
								valFin=NS.cambiarFecha(''+fechaDos);
							}else{
									return Ext.Msg.alert('SET',"Debe seleccionar una fecha final"); 
							}
							if(valor>fechaDos){
								Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
							}else{
							//NS.storeLlenarGridCartas.baseParams.fechaIni=''+valIni;
							//NS.storeLlenarGridCartas.baseParams.fechaFin=''+valFin;
						   	parametros='?nomReporte=excelCartasEmitidas';
						   	
							parametros+='&nomParam1=folio';
							parametros+='&valParam1='+NS.cmbFolio.getValue();
							
							parametros+='&nomParam2=idBanco';
							parametros+='&valParam2='+NS.txtBanco.getValue();
							
							parametros+='&nomParam3=tipo';
							parametros+='&valParam3='+NS.cmbTipo.getValue();
							
							parametros+='&nomParam4=estatus';
							parametros+='&valParam4='+NS.opciones;
							
							parametros+='&nomParam5=fechaIni';
							parametros+='&valParam5='+valIni;
							
							parametros+='&nomParam6=fechaFin';
							parametros+='&valParam6='+valFin;
							
							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
							}
					   }
				},
    	         {
    	        	 text: 'Ejecutar',
    	        	 handler: function(){
    	        	 NS.datos = NS.gridDatosCartas.getSelectionModel().getSelections();
    	        	 var estatus=NS.datos[0].get('estatus');
    	        	  if(estatus!='E'){
    	        		 if(NS.datos.length > 0){
    	        			 if(NS.cmbSolicitantes.getValue() != ''){
    	        				 if(NS.txtFechaEntrega.getValue() != ''){
			    	        		 NS.folios = '( ';
			    	        		 for(var i = 0; i < NS.datos.length; i ++){
			    	        			 NS.folios = NS.folios + NS.datos[i].get('idEmision');
				        				 if(i < NS.datos.length - 1){
				        					 NS.folios = NS.folios + ', ';
				        				 }
				        			 }
				        			 NS.folios = NS.folios + ' )';

				        			 var jsonDatos = '[{"folios":"' + NS.folios + '","nombre":"' + NS.cmbSolicitantes.getRawValue() + 
				        			 	'","fecha":"' + NS.cambiarFecha(''+NS.txtFechaEntrega.getValue()) + 
				        			 	'","":"' + NS.cmbSolicitantes.getRawValue() +'"}]' ;
				        			 
				        			 
				        			 
				        			 CartasPorEntregarAction.cambiarEstatus(jsonDatos, function(resultado, e){
				        				 Ext.Msg.alert('SET', resultado);
				        				 //NS.storeLlenarGrid.reload();
				        				 NS.limpiarTodo();
				        			 });
    	        				 }else{
    	        					 Ext.Msg.alert('SET', 'Debe seleccionar una fecha entrega');
    	        				 }
    	        			 }else{
    	        				 Ext.Msg.alert('SET', 'Debe seleccionar un solicitante');
    	        			 }
    	        		 }else{
    	        			 Ext.Msg.alert('SET', 'No se han seleccionado registros');
    	        		 }
    	        		}else{
    	        			Ext.Msg.alert('SET', 'La carta ya fue entregada'); 
    	        		}
    	        	 }
    	         },
    	        
    	         {
    	        	 text: 'Limpiar',
    	        	 handler: function(){
    	        		 NS.limpiarTodo();
    	        	 }
    	         },
    	         {
    	        	 text: 'Reimprimir',
    	        	 handler: function(){
    	        		 NS.visualizar();
    	        	 }
    	         },
	    ]       
    });
	
	
	
	NS.PanelContenedorGeneral = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorGeneral',
		layout: 'absolute',
		width: 1010,
		height: 860,
		items: [
		        NS.PanelContenedorCriteriosBusqueda,
		        NS.PanelContenedorCheques,
		        NS.PanelContenedorCartas
		]
	});
	
	NS.CPEntregar = new Ext.form.FormPanel({
		title: 'Cartas Por Entregar',
		width: 1010,
		height: 1000,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'CPEntregar',
		name: PF + 'CPEntregar',
		renderTo: NS.tabContId,
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items: [
		        	NS.PanelContenedorGeneral
		        	
		        ]
	});
	
	NS.CPEntregar.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
});