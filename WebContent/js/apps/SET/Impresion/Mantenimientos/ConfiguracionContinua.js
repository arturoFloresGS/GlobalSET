/**
 * Luis Alfredo Serrato Montes de Oca
 * 08/12/2015
 */

Ext.onReady(function (){
	
	var NS = Ext.namespace('apps.SET.Impresion.Mantenimientos.ConfiguracionContinua');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.verificaComponentesCreados = function(){
		var compt;
		
		compt = Ext.getCmp(PF + 'cmbCampo');
		if(compt != null || compt != undefined){
			compt.destroy();
		}
		compt = Ext.getCmp(PF + 'cmbTLetra');
		if(compt != null || compt != undefined){
			compt.destroy();
		}
		compt = Ext.getCmp(PF + 'gridDatosCampos');
		if(compt != null || compt != undefined){
			compt.destroy();
		}
	}

	
	NS.verificaComponentesCreados();
	
	
	/*
	 * VARIABLE'S
	 */
	
	NS.idConfGlobal;
	
	/*
	 * FUNCTION'S
	 */
	
	NS.limpiarConfiguracion = function(){
		
		NS.configuracionTxt.setValue('');
	    NS.bancoTxt.setValue('');
	    NS.cmbBanco.setValue('');
	    NS.divisaTxt.setValue('');
	    NS.cmbDivisa.setValue('');
	    NS.cmbTipoImprecion.setValue('');
	    NS.empresaTxt.setValue('');
	    NS.cmbEmpresa.setValue('');
	    NS.cmbChequera.setValue('');
	    NS.driverTxt.setValue('');
	}
	
	NS.limpiarCampos = function(){
        NS.cmbCampo.setValue('');
        NS.cmbTLetra.setValue('');
        NS.tamanoTxt.setValue('');
        NS.formatoTxt.setValue('');
        NS.coordXTxt.setValue('');
        NS.coordYTxt.setValue('');
        
	}
		
	
	
	/*
	 * TEXTFIELD'S / LABEL'S
	 */
	
    
	NS.configuracionLbl = new Ext.form.Label({
		text: 'Configuración',
		x:0,
		y:10
	});
	
	 NS.configuracionTxt = new Ext.form.TextField({
	   	width: 210,
	   	id: PF + 'configuracionTxt',
		name: PF + 'configuracionTxt',
		x: 105,
		y: 10,
		blankText : 'Este campo es requerido',
		
	 });
   
	NS.bancoLbl = new Ext.form.Label({
		text: 'Banco',
		x:0,
		y:45
	});
	 
	 NS.bancoTxt = new Ext.form.TextField({
	   	width: 50,
	   	id: PF + 'bancoTxt',
		name: PF + 'bancoTxt',
		x: 45,
		y: 45,
		blankText : 'Este campo es requerido',
		//readOnly: true,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					BFwrk.Util.updateTextFieldToCombo(PF + 'bancoTxt', NS.cmbBanco.getId());
					if(NS.cmbEmpresa.getValue() != "" && NS.cmbEmpresa.getValue() != null && NS.cmbEmpresa.getValue() != undefined){
						NS.cmbChequera.setValue('');
						console.log(NS.cmbBanco.getValue());
						NS.storeChequeras.baseParams.idBanco = parseInt(NS.bancoTxt.getValue()); 
						NS.storeChequeras.baseParams.idEmpresa = NS.cmbEmpresa.getValue()+''; 
						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg: "Cargando Chequeras ..."});
						NS.storeChequeras.load();
					}
				}
			}
		}
		
		
	 });
	 
	 NS.divisaLbl = new Ext.form.Label({
			text: 'Divisa',
			x:0,
			y:80
	 });
		 
	NS.divisaTxt = new Ext.form.TextField({
		   width: 50,
		   id: PF + 'divisaTxt',
		   name: PF + 'divisaTxt',
		   x: 45,
		   y: 80,
		   blankText : 'Este campo es requerido',
		   listeners:
			{
				change:
				{
					fn: function(caja, valor)
					{
						BFwrk.Util.updateTextFieldToCombo(PF + 'divisaTxt', NS.cmbDivisa.getId());
					}
				}
			}
	});
		 
		NS.tipoImprecionLbl = new Ext.form.Label({
			text: 'Tipo Impresión',
			x:0,
			y:115
		});
		
		NS.empresaLbl = new Ext.form.Label({
			text: 'Empresa',
			x:360,
			y:10
		});
		 
		 NS.empresaTxt = new Ext.form.TextField({
			   	width: 50,
			   	id: PF + 'empresaTxt',
				name: PF + 'empresaTxt',
				x: 410,
				y: 10,
				blankText : 'Este campo es requerido',
				listeners:
				{
					change:
					{
						fn: function(caja, valor)
						{
							BFwrk.Util.updateTextFieldToCombo(PF + 'empresaTxt', NS.cmbEmpresa.getId());
							if(NS.cmbBanco.getValue() != "" && NS.cmbBanco.getValue() != null && NS.cmbBanco.getValue() != undefined){
								NS.cmbChequera.setValue('');
								console.log(NS.cmbBanco.getValue());
								NS.storeChequeras.baseParams.idBanco = parseInt(NS.cmbBanco.getValue());
								NS.storeChequeras.baseParams.idEmpresa = NS.empresaTxt.getValue()+'';
								var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg: "Cargando Chequeras ..."});
								NS.storeChequeras.load();
							}
							
						}
					}
				}
		});
		 
		
		 NS.chequeraLbl = new Ext.form.Label({
				text: 'Chequera',
				x:360,
				y:45
		});
	   
		 
		 NS.driverLbl = new Ext.form.Label({
				text: 'Driver',
				x:360,
				y:80
		});

		 NS.driverTxt = new Ext.form.TextField({
			   	width: 210,
			   	id: PF + 'driverTxt',
				name: PF + 'driverTxt',
				x: 465,
				y: 80,
				blankText : 'Este campo es requerido'
			 });
		 
		 NS.campoLbl = new Ext.form.Label({
				text: 'Campo',
				x:0,
				y:10
		});
	
		 NS.tLetraLbl = new Ext.form.Label({
				text: 'Tipo de Letra',
				x:0,
				y:45
		});
		 
		 NS.tamanoLbl = new Ext.form.Label({
				text: 'Tamaño de Letra',
				x:0,
				y:80
		});
	
		 NS.tamanoTxt = new Ext.form.NumberField({
			   	width: 50,
			   	id: PF + 'tamanoTxt',
				name: PF + 'tamanoTxt',
				x: 90,
				y: 80,
				blankText : 'Este campo es requerido'
		 });
		 
		 NS.formatoLbl = new Ext.form.Label({
				text: 'Leyenda Campo',
				x:150,
				y:80
		});
		 
		 NS.formatoTxt = new Ext.form.TextField({
			   	width: 100,
			   	id: PF + 'formatoTxt',
				name: PF + 'formatoTxt',
				x: 230,
				y: 80,
				blankText : 'Este campo es requerido'
		 }); 
		 
		 NS.coordenadasLbl = new Ext.form.Label({
				text: 'Coordenadas',
				x:0,
				y:115
		});
		 
		 NS.coordXTxt = new Ext.form.NumberField({
			   	width: 70,
			   	id: PF + 'coordXTxt',
				name: PF + 'coordXTxt',
				x: 90,
				y: 115,
				blankText : 'Este campo es requerido'
		 });
		 
		 NS.coordenadasXLbl = new Ext.form.Label({
				text: 'X',
				x:165,
				y:120
		});

		NS.coordYTxt = new Ext.form.NumberField({
			   	width: 70,
			   	id: PF + 'coordYTxt',
				name: PF + 'coordYTxt',
				x: 195,
				y: 115,
				blankText : 'Este campo es requerido'
		});
		
		 NS.coordenadasYLbl = new Ext.form.Label({
				text: 'Y',
				x:270,
				y:120
		});
		 
		 NS.confConultaLbl = new Ext.form.Label({
				text: '',
				x:610,
				y:285
		});
		 
		 NS.bancoConultaLbl = new Ext.form.Label({
				text: '',
				x:610,
				y:305
		});
		 
		 NS.divisaConultaLbl = new Ext.form.Label({
				text: '',
				x:610,
				y:325
		});
		 
		 NS.driverConultaLbl = new Ext.form.Label({
				text: '',
				x:610,
				y:345
		});
		 
		 NS.empresaConultaLbl = new Ext.form.Label({
				text: '',
				x:610,
				y:365
		});
		 
		 NS.chequeraConultaLbl = new Ext.form.Label({
				text: '',
				x:610,
				y:385
		});
		 
		 
		 
		 
	/*
	 * COMBO'S
	 */
	
		 
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConfiguracionContinuaAction.obtenerBancos, 
		idProperty: 'id', 
		fields: [
		         {name: 'id'},
		         {name: 'descripcion'}
		         ],
		         listeners: {
		        	 load: function(s, records){
						
						if(records.length==null || records.length <= 0)
						{
							Ext.Msg.alert('SET','No Existen bancos');
						}
					}
				}
	});	 
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
	NS.storeBancos.load();
	
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancos,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		width: 210,
		x: 105,
		y: 45,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'SELECCIONE BANCO',
		triggerAction: 'all',
		listeners:{
			select:{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'bancoTxt', NS.cmbBanco.getId());
	
					if(NS.cmbEmpresa.getValue() != "" && NS.cmbEmpresa.getValue() != null && NS.cmbEmpresa.getValue() != undefined){
						NS.cmbChequera.setValue('');
						console.log(combo.getValue());
						NS.storeChequeras.baseParams.idBanco = parseInt(combo.getValue()); 
						NS.storeChequeras.baseParams.idEmpresa = NS.cmbEmpresa.getValue()+''; 
						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg: "Cargando Chequeras ..."});
						NS.storeChequeras.load();
					}
				}
			}
			
		}
	});
	
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultaPagosPendientesAction.obtenerDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
			 {name: 'idDivisaSoin'},
			 {name: 'clasificacion'}
		],
		listeners: {
			load: function(s, records){
				
				if(records.length==null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen divisas');
				}
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
	NS.storeDivisa.load();

	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa,
		id: PF + 'cmbDivisa',
		name: PF + 'cmbDivisa',
		width: 210,
		x: 105,
		y: 80,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'SELECCIONE DIVISA',
		triggerAction: 'all',
		listeners:{
			select:{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'divisaTxt', NS.cmbDivisa.getId());
				}
			}
			
		}
	});

	
	NS.datosComboTipoImpresion = [
	  ['C', 'CONTINUA']		       			
	];	       		
	
	
	NS.storeTipoImpresion = new Ext.data.SimpleStore({
		idProperty: 'idTipoImpresion',
		fields: [
			{name: 'idTipoImpresion'},
			{name: 'descTipoImpresion'}
		]
	});
	NS.storeTipoImpresion.loadData(NS.datosComboTipoImpresion);

	
	NS.cmbTipoImprecion = new Ext.form.ComboBox({
		store: NS.storeTipoImpresion,
		id: PF + 'cmbTipoImpresion',
		name: PF + 'cmbTipoImpresion',
		width: 210,
		x: 105,
		y: 115,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idTipoImpresion',
		displayField: 'descTipoImpresion',
		autocomplete: true,
		emptyText: 'SELECCIONE TIPO',
		triggerAction: 'all',
		listeners:{
			select:{
				fn: function(combo, valor)
				{
					//BFwrk.Util.updateComboToTextField(PF + 'noEmpresasTxt', NS.cmbNoEmpresa.getId());
				}
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
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeLlenarComboEmpresa,
		id: PF + 'cmbEmpresa',
		name: PF + 'cmbEmpresa',
		width: 210,
		x: 465,
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
					if(NS.cmbBanco.getValue() != "" && NS.cmbBanco.getValue() != null && NS.cmbBanco.getValue() != undefined){
						NS.cmbChequera.setValue('');
						console.log(combo.getValue());
						NS.storeChequeras.baseParams.idBanco = parseInt(NS.cmbBanco.getValue());
						NS.storeChequeras.baseParams.idEmpresa = NS.cmbEmpresa.getValue()+'';
						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg: "Cargando Chequeras ..."});
						NS.storeChequeras.load();
					}
					
				}
			}
			
		}
	});
	
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {idBanco: 0, idEmpresa: ''},
		paramOrder: ['idBanco', 'idEmpresa'],
		directFn: ConfiguracionContinuaAction.obtenerChequeras,
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
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen chequeras.');
			}
		}

	});
	
	
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequeras,
		id: PF + 'cmbChequera',
		name: PF + 'cmbChequera',
		width: 210,
		x: 465,
		y: 45,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'id',
		displayField: 'id',
		autocomplete: true,
		emptyText: 'SELECCIONE CHEQUERA',
		triggerAction: 'all',
		listeners:{
			select:{
				fn: function(combo, valor)
				{
					
				}
			}
			
		}
	});
	
	NS.datosComboCampo = [
	  /*['CAJA', 'CAJA'], ['NUMERO DE SOLICITUD', 'NUMERO DE SOLICITUD'], ['IMPORTE', 'IMPORTE'], 
	  ['BENEFICIARIO', 'BENEFICIARIO'], ['BANCO', 'BANCO'], ['CHEQUERA', 'CHEQUERA'],
	  ['CONCEPTO', 'CONCEPTO'], ['CENTRO DE COSTOS', 'CENTRO DE COSTOS'], ['NUM. DE BANCO', 'NUM. DE BANCO'], 
	  ['IMPORTE ORIGINAL', 'IMPORTE ORIGINAL'], ['IMPORTE EN LETRA', 'IMPORTE EN LETRA'], ['NUM. CONTRARECIBO', 'NUM. CONTRARECIBO'], 
	  ['RFC BENEFICIARIO', 'RFC BENEFICIARIO'], ['MONEDA', 'MONEDA'], ['LEYENDA PROTECCION', 'LEYENDA PROTECCION'], 
	  ['PLAZA', 'PLAZA'], ['SUCURSAL', 'SUCURSAL'], ['DIRECCION EMPRESA', 'DIRECCION EMPRESA'], ['2a DIRECCION EMPRESA', '2a DIRECCION EMPRESA'],
	  ['RFC DE LA EMPRESA', 'RFC DE LA EMPRESA'], ['FECHA DEL CHEQUE', 'FECHA DEL CHEQUE'], ['NUMERO DE CHEQUE', 'NUMERO DE CHEQUE'], ['FIRMANTE 1', 'FIRMANTE 1'],
	  ['FIRMANTE 2', 'FIRMANTE 2'], ['ID. RUBRO', 'ID. RUBRO'], ['NUM. DE PEDIDO', 'NUM. DE PEDIDO'], ['EMPRESA EQUIVALENTE', 'EMPRESA EQUIVALENTE'],
	  ['DIVISA ORIGINAL', 'DIVISA ORIGINAL'], ['TIPO DE CAMBIO', 'TIPO DE CAMBIO']*/
	  
	  ['IMPORTE', 'IMPORTE'],
	  ['IMPORTE 2', 'IMPORTE 2'],
	  ['IMPORTE 3', 'IMPORTE 3'],
	  ['IMPORTE 4', 'IMPORTE 4'],
	  ['IMPORTE EN LETRA', 'IMPORTE EN LETRA'], 
	  ['BENEFICIARIO', 'BENEFICIARIO'],
	  ['BENEFICIARIO 2', 'BENEFICIARIO 2'],
	  ['NUMERO ACREEDOR', 'NUMERO ACREEDOR'], 
	  ['BANCO PAGADOR', 'BANCO PAGADOR'], 
	  ['CHEQUERA BENEFICIARIA', 'CHEQUERA BENEFICIARIA'],
	  ['CHEQUERA PAGADORA', 'CHEQUERA PAGADORA'],
	  ['NUMERO DE CHEQUE', 'NUMERO DE CHEQUE'],
	  ['NUMERO DE CHEQUE 2', 'NUMERO DE CHEQUE 2'],
	  ['FECHA DEL CHEQUE', 'FECHA DEL CHEQUE'],
	  ['FECHA DEL CHEQUE 2', 'FECHA DEL CHEQUE 2'],
	  ['DIVISA ORIGINAL', 'DIVISA ORIGINAL'],
	  ['DOCUMENTO COMPENSACION', 'DOCUMENTO COMPENSACION'], //PO_HEADERS
	  ['CUENTA CONTABLE', 'CUENTA CONTABLE'],
	  ['CONCEPTO', 'CONCEPTO'],
	  ['CONCEPTO 2', 'CONCEPTO 2'],
	  ['LEYENDA', 'LEYENDA'],
	  ['EMPRESA PAGADORA', 'EMPRESA PAGADORA'],
	  ['NOMBRE EMPRESA', 'NOMBRE EMPRESA'],
	  ['NUMERO DE DOCUMENTO', 'NUMERO DE DOCUMENTO'],
	  ['NUMERO DE DOCUMENTO 2', 'NUMERO DE DOCUMENTO 2'],
	  ['FACTURA', 'FACTURA']
	];	       		

	NS.storeCampos = new Ext.data.SimpleStore({
		idProperty: 'idCampo',
		fields: [
		         {name: 'idCampo'},
		         {name: 'descCampo'}
		         ]
	});
	NS.storeCampos.loadData(NS.datosComboCampo);

	                        	
	NS.cmbCampo = new Ext.form.ComboBox({
		store: NS.storeCampos,
		id: PF + 'cmbCampo',
		name: PF + 'cmbCampo',
		width: 210,
		x: 90,
		y: 10,
		typeAhead: true,
	    mode: 'local',
	    selecOnFocus: true,
	    forceSelection: true,
	    valueField: 'idCampo',
	    displayField: 'descCampo',
	    autocomplete: true,
	    emptyText: 'SELECCIONE CAMPO',
	    triggerAction: 'all',
	    listeners:{
	    	select:{
	    		fn: function(combo, valor)
	    		{
	    		}
	    	}
	    }
	});
	
	
	
	
	
	
	
	
	NS.storeLetras = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: ConfiguracionContinuaAction.obtenerFuentes,
		fields:
		[
         {name: 'descripcion'}
		],
		idProperty: 'descripcion',
		listeners:
		{
		load: function(s, records)
			{
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen pagos pendientes.');
			}
		}

	});
	
	
	NS.cmbTLetra = new Ext.form.ComboBox({
		store: NS.storeLetras,
		id: PF + 'cmbTLetra',
		name: PF + 'cmbTLetra',
		width: 210,
		x: 90,
		y: 45,
		typeAhead: true,
	    mode: 'local',
	    selecOnFocus: true,
	    forceSelection: true,
	    valueField: 'descripcion',
	    displayField: 'descripcion',
	    autocomplete: true,
	    emptyText: 'SELECCIONE LETRA',
	    triggerAction: 'all',
	    listeners:{
	    	select:{
	    		fn: function(combo, valor)
	    		{
	    		}
	    	}
	    }
	});
	
	
	
	
	/*
	 * GRID'S
	 */


	NS.storeLlenarGridatosCheq = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: ConfiguracionContinuaAction.obtenerConfiguraciones,
		fields:
		[
		    {name: 'idConf'},
		    {name: 'descConf'},
		 	{name: 'descBanco'},
		 	{name: 'idBanco'},
		 	{name: 'descDivisa'},
		 	{name: 'idDivisa'},
		 	{name: 'driver'},
		 	{name: 'tipoImpresion'},
		 	{name: 'nomEmpresa'},
		 	{name: 'numEmpresa'},
		 	{name: 'idChequera'}
		 	
		],
		listeners:
		{
			load: function(s, records)
			{
				
				
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay datos que mostrar');
				}else{
										
		 			
				}
					
			}
		}

	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridatosCheq, msg: "Cargando Datos ..."});
	NS.storeLlenarGridatosCheq.load();

	
	NS.seleccionGrid = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});  
	
	
	NS.columnasDatosCheq = new Ext.grid.ColumnModel([
	  {header: 'ID', width: 70, dataIndex: 'idConf' ,sortable: true, hidden: true},
	  {header: 'CONFIGURACION', width: 200, dataIndex: 'descConf'},	
	  {header: 'BANCO', width: 150, dataIndex: 'descBanco'},                                        	
	  {header: 'ID BANCO', width: 70, dataIndex: 'idBanco' ,sortable: true, hidden: true},
	  {header: 'DIVISA', width: 100, dataIndex: 'descDivisa'},
	  {header: 'ID DIVISA', width: 70, dataIndex: 'idDivisa', hidden: true},
	  {header: 'DRIVER', width: 100, dataIndex: 'driver'},                                      	
	  {header: 'TIPO', width: 50, dataIndex: 'tipoImpresion'},
	  {header: 'EMPRESA', width: 200, dataIndex: 'nomEmpresa'},
	  {header: 'NUMERO EMPRESA', width: 70, dataIndex: 'numEmpresa' ,sortable: true, hidden: true},
	  {header: 'CHEQUERA', width: 150, dataIndex: 'idChequera' ,sortable: true},
	]);
	                                         	
	
	
	NS.gridDatosCheq = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridatosCheq,
		id: PF + 'gridDatosCheq',
		x: 0,
		y: 0,
		cm: NS.columnasDatosCheq,
		sm: NS.seleccionGrid,
		width: 960,
		height: 250,
		border: false,
		stripeRows: true,
		listeners:{
			rowdblClick:{
				fn:function(grid){
					var idConf = NS.gridDatosCheq.getSelectionModel().getSelections();
					NS.idConfGlobal = idConf[0].get('idConf');
					NS.storeLlenarGridatosCampos.baseParams.idConf = idConf[0].get('idConf');
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridatosCampos, msg: "Cargando Datos ..."});
					NS.storeLlenarGridatosCampos.load();
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLetras, msg: "Cargando Chequeras ..."});
					NS.storeLetras.load();
					NS.confConultaLbl.setText("Descripcion: " + idConf[0].get('descConf'));
					NS.bancoConultaLbl.setText("Banco: " + idConf[0].get('descBanco'));
					NS.divisaConultaLbl.setText("Divisa: " + idConf[0].get('descDivisa'));
					NS.driverConultaLbl.setText("Driver: " + idConf[0].get('driver'));
					NS.empresaConultaLbl.setText("Empresa: " + idConf[0].get('nomEmpresa'));
					NS.chequeraConultaLbl.setText("Chequera: " + idConf[0].get('idChequera'));
					NS.venCampos.show();
				}
			}
		}
      
	});
	
	
	NS.storeLlenarGridatosCampos = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {idConf: 0},
		paramOrder: ['idConf'],
		root: '',
		directFn: ConfiguracionContinuaAction.obtenerCampos,
		fields:
		[
		 	{name: 'idCampo'},
		    {name: 'campo'},
		    {name: 'idConf'},
		 	{name: 'tipoLetra'},
		 	{name: 'tamanoLetra'},
		 	{name: 'coordX'},
		 	{name: 'coordY'},
		 	{name: 'formato'}
		],
		listeners:
		{
			load: function(s, records)
			{
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay datos que mostrar');
				}	
			}
		}
	});
	

	NS.seleccionGridCampos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});  
	
	
	NS.columnasDatosCampos = new Ext.grid.ColumnModel([
      {header: 'ID', width: 70, dataIndex: 'idCampo', hidden: true},                                                 
	  {header: 'CAMPO', width: 200, dataIndex: 'campo'},
	  {header: 'ID_CONF', width: 100, dataIndex: 'idConf'},	
	  {header: 'TIPO_LETRA', width: 150, dataIndex: 'tipoLetra'},
	  {header: 'TAMANO_LETRA', width: 105, dataIndex: 'tamanoLetra'},
	  {header: 'CORDENADA X', width: 105, dataIndex: 'coordX'},
	  {header: 'CORDENADA Y', width: 105, dataIndex: 'coordY'},                                      	
	  {header: 'FORMATO', width: 155, dataIndex: 'formato'}
	  
	]);
	
	NS.gridDatosCampos = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridatosCampos,
		id: PF + 'gridDatosCampos',
		x: 10,
		y: 25,
		cm: NS.columnasDatosCampos,
		sm: NS.seleccionGridCampos,
		width: 960,
		height: 170,
		border: false,
		stripeRows: true,
		listeners:{
			rowClick:{
				fn:function(grid){
					
				}
			}
		}
      
	});
	
	
	/*
	 * WINDOW'S
	 */
	
	NS.venCampos = new Ext.Window({
		title: 'CAMPOS',
		modal: true,
		shadow: true,
		width: 1025,
		height: 500,
		layout: 'absolute',
		plain: true,
		bodyStyle: 'padding:10px;background-color:#000000',
		closeAction: 'hide',
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		listeners: {
 		   hide:{
 			   fn:function(e){
 				   NS.limpiarCampos();
 			   }
 		   }
 	   },
 	   items:[
 	          NS.confConultaLbl,
 	          NS.bancoConultaLbl,
 	          NS.divisaConultaLbl,
	          NS.driverConultaLbl,
	          NS.empresaConultaLbl,
	          NS.chequeraConultaLbl,
 	          {
		    	   xtype: 'fieldset',
		    	   id: PF + 'PanelContenedorCampos',
		    	   layout: 'absolute',
		    	   x: 5,
		    	   y: 0,
		    	   width: 990,
		    	   height: 260,
		    	   items:[
		    	          NS.gridDatosCampos
		    	   ],
		    	   buttons:[
		    	            {
		    	            	text: 'Eliminar',
		    	            	handler: function(){
		    	            		var seleccion = NS.gridDatosCampos.getSelectionModel().getSelections();
		    	            		if(seleccion.length > 0){
		    	            			
		    	            			ConfiguracionContinuaAction.eliminarCampo(seleccion[0].get('idCampo'), function(resultado, e){
		    	            				if(resultado != null && resultado != '' && resultado != undefined){
  											  Ext.Msg.alert('SET', resultado);
  											  NS.storeLlenarGridatosCampos.reload();
  										  }
		    	            			});
		    	            			
		    	            		}else{
		    	            			Ext.Msg.alert('SET', 'Debe seleccionar un registro');
		    	            		}
		    	            		
		    	            	}
		    	            }
		    	  ]      
		       },
		       {
		    	   xtype: 'fieldset',
		    	   title: 'DATOS NUEVO',
		    	   id: PF + 'PanelContenedorCamposRegistro',
		    	   layout: 'absolute',
		    	   x: 5,
		    	   y: 275,
		    	   width: 595,
		    	   height: 170,
		    	   items:[
		    	          NS.campoLbl,
		    	          NS.cmbCampo,
		    	          NS.tLetraLbl,
		    	          NS.cmbTLetra,
		    	          NS.tamanoLbl,
		    	          NS.tamanoTxt,
		    	          NS.formatoLbl,
		    	          NS.formatoTxt,
		    	          NS.coordenadasLbl,
		    	          NS.coordXTxt,
		    	          NS.coordenadasXLbl,
		    	          NS.coordYTxt,
		    	          NS.coordenadasYLbl,
		    	          {
		    	        	  xtype: 'button',
		    	        	  text: 'Aceptar',
		    	        	  x: 390,
		    	        	  y: 115,
		    	        	  width: 80,
		    	        	  listeners: {
		    	        		  click:{
		    	        			  fn:function(e){
		    	        				  NS.gridDatosCampos
		    	        				  var registrosGrid = NS.gridDatosCampos.store.data.items;
		    	                    		 for(var i = 0; i < registrosGrid.length; i++){
		    	                    			 NS.campo=NS.gridDatosCampos.store.data.items[i].get("campo");
		    	                    			 if(NS.campo==NS.cmbCampo.getValue()){
		    	                    				 Ext.Msg.alert('SET', 'Este campo ya se ha agregado');
		    	                    				 return;
		    	                    			 }
		    	                    	  }
		    	        				  var jsonCamp= '[{';
		    	        				  if(NS.cmbCampo.getValue() != ''){
		    	        					  jsonCamp = jsonCamp + '"campo":"'+NS.cmbCampo.getValue()+'",';
		    	        					  
		    	        					  if(NS.cmbTLetra.getValue() != ''){
		    	        						  jsonCamp = jsonCamp + '"fuente":"' +NS.cmbTLetra.getValue()+ '",';
		    	        						  
		    	        						  if(NS.tamanoTxt.getValue() != ''){
		    	        							  jsonCamp = jsonCamp + '"tamano":"' + NS.tamanoTxt.getValue() + '",';
		    	        							  
		    	        							  if(NS.coordXTxt.getValue() != ''){
		    	        								  jsonCamp = jsonCamp + '"x":"' + NS.coordXTxt.getValue() + '",';
		    	        								  
		    	        								  if(NS.coordYTxt.getValue() != ''){
		    	        									  jsonCamp = jsonCamp + '"y":"' + NS.coordYTxt.getValue() + '",';
		    	        									  
		    	        									  if(NS.formatoTxt.getValue() != ''){
		    	        										  jsonCamp = jsonCamp + '"formato":"' + NS.formatoTxt.getValue() + '",';
		    	        									  }else{
		    	        										  jsonCamp = jsonCamp + '"formato":"",';
		    	        									  }
		    	        									  
		    	        									  jsonCamp = jsonCamp + '"idConf":"' + NS.idConfGlobal + '"}]'; 
		    	        									  
		    	        									  ConfiguracionContinuaAction.insertarCampos(jsonCamp, function(resultado, e){
		    	        										  
		    	        										  if(resultado != null && resultado != '' && resultado != undefined){
		    	        											  Ext.Msg.alert('SET', resultado);
		    	        											  NS.storeLlenarGridatosCampos.reload();
		    	        											  NS.limpiarCampos();
		    	        										  }
		    	        						    			  
		    	        									  });
		    	        									  
		    	        								  }else{
		    	        									  Ext.Msg.alert('SET', 'La cordenada Y es obligatoria');
		    	        								  }
		    	        							  }else{
		    	        								  Ext.Msg.alert('SET', 'La cordenada X es obligatoria');
		    	        							  }
		    	        						  }else{
		    	        							  Ext.Msg.alert('SET', 'El tamano de letra es obligatorio');
		    	        						  }
		    	        						  
		    	        					  }else{
		    	        						  Ext.Msg.alert('SET', 'El tipo de letra es obligatorio');
		    	        					  }
		    	        				  }else{
		    	        					  Ext.Msg.alert('SET', 'El campo es obligatorio');
		    	        				  }
		    	        			  }
		    	        		  }
		    	        	  }
		    	          },
		    	          {
		    	        	  xtype: 'button',
		    	        	  text: 'Cancelar',
		    	        	  x: 495,
		    	        	  y: 115,
		    	        	  width: 80,
		    	        	  listeners: {
		    	        		  click:{
		    	        			  fn:function(e){
		    	        				  NS.limpiarCampos();
		    	        			  }
		    	        		  }
		    	        	  }
		    	          }
		  				
		    	   ]
		       }
		] 
	});

	
	
	/*
	 * PANEL'S
	 */
	
	
	NS.PanelContenedorDatosConsulta = new Ext.form.FieldSet({
		title: 'DATOS CONFIGURACIÓN',
		id: PF + 'PanelContenedorDatosConsulta',
		layout: 'absolute',
		width: 978,
		height: 325,
		x: 0,
		y: 10,
		items:[
		       NS.gridDatosCheq
		],
		buttons:[
		       {
		    	   text:'Campos',
		    	   handler: function(){
		    		   var idConf = NS.gridDatosCheq.getSelectionModel().getSelections();
		    		   if(idConf.length > 0){
							NS.idConfGlobal = idConf[0].get('idConf');
							NS.storeLlenarGridatosCampos.baseParams.idConf = idConf[0].get('idConf');
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridatosCampos, msg: "Cargando Datos ..."});
							NS.storeLlenarGridatosCampos.load();
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLetras, msg: "Cargando Chequeras ..."});
							NS.storeLetras.load();
							NS.confConultaLbl.setText("Descripcion: " + idConf[0].get('descConf'));
							NS.bancoConultaLbl.setText("Banco: " + idConf[0].get('descBanco'));
							NS.divisaConultaLbl.setText("Divisa: " + idConf[0].get('descDivisa'));
							NS.driverConultaLbl.setText("Driver: " + idConf[0].get('driver'));
							NS.empresaConultaLbl.setText("Empresa: " + idConf[0].get('nomEmpresa'));
							NS.chequeraConultaLbl.setText("Chequera: " + idConf[0].get('idChequera'));
							NS.venCampos.show();
		    		   }else{
		    			   Ext.Msg.alert('SET', 'No se han seleccionado registros');
		    		   }
		    	   }
		    	   
		       },
//		       {
//	    	   
//		    	   text:'Crear Nuevo',
//		    	   handler: function(){
//	    		   
//		    	   }
//		       },
		       {
		    	   text:'Eliminar',
		    	   handler: function(){
		    		   var seleccion = NS.gridDatosCheq.getSelectionModel().getSelections();
		    		   if(seleccion.length > 0){
		    			   ConfiguracionContinuaAction.eliminarConfiguracion(seleccion[0].get('idConf'), function(resultado, e){
		    				   if(resultado != null && resultado != '' && resultado != undefined){
									Ext.Msg.alert('SET', resultado);
									NS.storeLlenarGridatosCheq.reload();
								}
		    			   });
		    			   
		    			   
		    		   }else{
		    			   Ext.Msg.alert('SET', 'Debe seleccionar un registro');
		    		   }
		    	   }
		       }
		]
	
	});
	
	
	NS.PanelContenedorDatosNuevo = new Ext.form.FieldSet({
		title: 'DATOS NUEVO',
		id: PF + 'PanelContenedorDatosNuevo',
		layout: 'absolute',
		width: 700,
		height: 220,
		x: 0,
		y: 350,
		items:[
		       NS.configuracionTxt,
		       NS.configuracionLbl,
		       NS.bancoTxt,
		       NS.bancoLbl,
		       NS.cmbBanco,
		       NS.divisaLbl,
		       NS.divisaTxt,
		       NS.cmbDivisa,
		       NS.tipoImprecionLbl,
		       NS.cmbTipoImprecion,
		       NS.empresaLbl,
		       NS.empresaTxt,
		       NS.cmbEmpresa,
		       NS.chequeraLbl,
		       NS.cmbChequera,
		       NS.driverLbl,
		       NS.driverTxt
		       
		       
		       
		       
		],
		buttons:[
		
		        {
		        	text: 'Aceptar',
		        	handler: function (){
		        		
		        		
		        		var jsonConf = '[{';
		        		if(NS.configuracionTxt.getValue() != ''){
		        			jsonConf = jsonConf + '"configuracion":"' + NS.configuracionTxt.getValue() + '",';
		        			
		        			if(NS.bancoTxt.getValue() != ''){
		        				jsonConf = jsonConf + '"banco":"' + NS.bancoTxt.getValue() + '",';
		        				
		        				if(NS.cmbDivisa.getValue() != ''){
		        					jsonConf = jsonConf + '"divisa":"' + NS.cmbDivisa.getValue() + '",';
		        					
		        					if(NS.cmbTipoImprecion.getValue() != ''){
		        						jsonConf = jsonConf + '"tipo":"' + NS.cmbTipoImprecion.getValue() + '",';
		        						
		        						if(NS.cmbEmpresa.getValue() != ''){
		        							jsonConf = jsonConf + '"empresa":"' + NS.cmbEmpresa.getValue() + '",';
		        							
		        							if(NS.cmbChequera.getValue() != ''){
		        								jsonConf = jsonConf + '"chequera":"' + NS.cmbChequera.getValue() + '",';
		        								
		        								if(NS.driverTxt.getValue() != ''){
		        									jsonConf = jsonConf + '"driver":"' + NS.driverTxt.getValue() + '"';
		        									jsonConf = jsonConf + "}]";

		        									ConfiguracionContinuaAction.insertarConfiguracion(jsonConf, function(resultado, e){
		        										if(resultado != null && resultado != '' && resultado != undefined){
		        											Ext.Msg.alert('SET', resultado);
		        											NS.storeLlenarGridatosCheq.reload();
		        											NS.limpiarConfiguracion();
		        										}
		        										
		        									});
		        									
		        								}else{
		        									Ext.Msg.alert('SET', 'El campo driver es obligatorio');
		        								}
		        							}else{
		        								Ext.Msg.alert('SET', 'El campo chequera es obligatorio');
		        							}
		        						}else{
		        							Ext.Msg.alert('SET', 'El campo empresa es obligatorio');
		        						}
		        					}else{
		        						Ext.Msg.alert('SET', 'El campo tipo impresión es obligatorio');
		        					}
		        				}else{
		        					Ext.Msg.alert('SET', 'El campo divisa es obligatorio');
		        				}
		        			}else{
		        				Ext.Msg.alert('SET', 'El campo banco es obligatorio');
		        			}
		        		}else{
		        			Ext.Msg.alert('SET', 'El campo configuración es obligatorio'); 
		        		}	
		        		
		        	}
		        },
		        
		        {
		        	text: 'Cancelar',
		        	handler: function (){
		        		NS.limpiarConfiguracion();
		        	}
		        
		        }
		        
		]
	
	});
		
	
	
	NS.PanelContenedorGeneral = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorGeneral',
		layout: 'absolute',
		width: 1010,
		height: 650,
		items: [
		        NS.PanelContenedorDatosConsulta,
		        NS.PanelContenedorDatosNuevo
		]
	});
	
	NS.CContinua = new Ext.form.FormPanel({
		title: 'Configuración de Cheque Continuo',
		width: 1010,
		height: 800,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'CContinua',
		name: PF + 'CContinua',
		renderTo: NS.tabContId,
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items: [
		        	NS.PanelContenedorGeneral
		   
		        ]
	});
	
	NS.CContinua.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
	
	
	
	
	
	
});