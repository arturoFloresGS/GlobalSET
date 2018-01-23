Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Interfases.Egresos.Reparacion'); //Nombre del archivo de trabajo
	NS.tabContId = apps.SET.tabContainerId; //Variable para definir el tamaño del panel principal
	NS.idUsuario = apps.SET.iUserId; //Como global para el id de usuario
	var PF = apps.SET.tabID + '.'; //Se coloca PF para los nombres de los componentes
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API); //para poder tener el enlace a las clases java
	Ext.QuickTips.init(); //Constructor
	NS.iTipoPago = 0;
	NS.fecHoy = apps.SET.FEC_HOY;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.estatus = 1;
	NS.totalMN = 0;
	NS.totalDLS = 0;
	NS.foliosExporta = "";
	NS.idTipoMovto = '';
	NS.tmpCtaContable = '';
	
	//LABEL
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 120,
		y: 0
	});
	
	NS.lblNoRegistros = new Ext.form.Label({
		text: 'No. Registros',
		x: 0,
		y: 190
	});
	
	NS.lblTotalMN = new Ext.form.Label({
		text: 'Total MN',
		x: 200,
		y: 190
	});
	
	NS.lblTotalDLS = new Ext.form.Label({
		text: 'Total DLS',
		x: 400,
		y: 190
	});
	
	NS.lblImporteSolicitado = new Ext.form.Label({
		text: 'Importe Solicitado',
		x: 600,
		y: 190
	});
	
	
	NS.lblFolioExportacion = new Ext.form.Label({
		text: 'Folio de Exportación',
		x: 800,
		y: 190
	});
	
	//TEXTFIELD
	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
		name: PF + 'txtNoEmpresa',
		x: 120,
		y: 15,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor){
					if (caja.getValue() !== null && caja.getValue() !== undefined && caja.getValue() !== '') {
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());						
					}
					else {						
						NS.cmbEmpresa.reset();
					}
				}
			}
		}
	});
	
	NS.txtNoRegistros = new Ext.form.TextField({
		id: PF + 'txtNoRegistros',
		name: PF + 'txtNoRegistros',
		x: 75,
		y: 190,
		width: 100
	});
	
	NS.txtTotalMN = new Ext.form.TextField({
		id: PF + 'txtTotalMN',
		name: PF + 'txtTotalMN',
		x: 250,
		y: 190,
		width: 100,
		listeners: {
			change: {
				fn: function(caja, valor) {
					Ext.getCmp(PF + 'txtTotalMN').setValue(BFwrk.Util.formatNumber(caja.getValue()));
				}
			}
		}
	});
	
	NS.txtTotalDLS = new Ext.form.TextField({
		id: PF + 'txtTotalDLS',
		name: PF + 'txtTotalDLS',
		x: 450,
		y: 190,
		width: 100
	});
	
	NS.txtImporteSolicitado = new Ext.form.TextField({
		id: PF + 'txtImporteSolicitado',
		name: PF + ' txtImporteSolicitado',
		x: 600,
		y: 200,
		width: 100
	});
	
	NS.txtFolioExportacion = new Ext.form.TextField({
		id: PF + 'txtFolioExportacion',
		name: PF + 'txtFolioExportacion',
		x: 800,
		y: 200,
		width: 100
	});
	
	//FUNCIONES
	NS.limpiaPantalla = function(){
		Ext.getCmp(PF + 'txtNoEmpresa').setValue('0');
		Ext.getCmp(PF + 'txtNoRegistros').setValue('');
		Ext.getCmp(PF + 'txtTotalMN').setValue('');
		Ext.getCmp(PF + 'txtTotalDLS').setValue('');
		Ext.getCmp(PF + 'txtImporteSolicitado').setValue('');
		Ext.getCmp(PF + 'txtFolioExportacion').setValue('');
		NS.cmbEmpresa.reset();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
		
		NS.cmbGrupo.setValue('');
	    	NS.cmbRubro.setValue('');
	    	NS.txtCuentaContable.setValue('');
	    	NS.cmbCuentaContable.setValue('');
	    	Ext.getCmp(PF+'txtGrupoBanco').setValue('');
	    	Ext.getCmp(PF+'txtRubroBanco').setValue('');
	    	NS.frameReparacion.setVisible(false);
	    	NS.panelGrid.setDisabled(false);
		
	};
	
	NS.repararRegistros = function(){
		
		var regSelec = NS.gridDatos.getSelectionModel().getSelections();
		
		if(regSelec.length <= 0)
		{			
			BFwrk.Util.msgShow('Debe seleccionar al menos un registro.', 'INFO');			
			return;
		}
		
		NS.panelGrid.setDisabled(true);
		
		NS.idTipoMovto = '';
		for(var i = 0; i < regSelec.length; i ++)
		{			
			if(NS.idTipoMovto != '' && NS.idTipoMovto != regSelec[i].get('idTipoMovto') )
			{
				BFwrk.Util.msgShow('Ha seleccionado registros de diferente naturaleza.', 'INFO');
				return;
			}
			
			NS.idTipoMovto = regSelec[i].get('idTipoMovto');			
		}
		
		
		NS.cmbGrupo.setValue('');
	    NS.cmbRubro.setValue('');
	    NS.txtCuentaContable.setValue('');
	    NS.cmbCuentaContable.setValue('');
	    Ext.getCmp(PF+'txtGrupoBanco').setValue('');
	    Ext.getCmp(PF+'txtRubroBanco').setValue('');
		NS.frameReparacion.setVisible(true);
		
		NS.storeGrupo.baseParams.idTipoMovto = NS.idTipoMovto;
		NS.storeGrupo.load();
		
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
	
	
	//STORE	
	NS.storeComboEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ExportacionAction.llenaComboEmpresa,
		idProperty: 'noEmpresa',
		fields:
		[
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboEmpresa, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen empresas dadas de alta');
				
				var recordsStoreEmpresas = NS.storeComboEmpresa.recordType;
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '***************TODAS***************'
				});
				NS.storeComboEmpresa.insert(0, todas);
			}
		}		
	});
	NS.storeComboEmpresa.load();
	
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['noEmpresa', 'fecHoy', 'estatus'],
		directFn: ExportacionAction.llenaGrid,
		fields:
		[
		 	{name: 'noEmpresa'},
		 	{name: 'noDocto'},
		 	{name: 'noCheque'},
		 	{name: 'idChequera'},
		 	{name: 'fecValor'},
		 	{name: 'importe'},
		 	{name: 'idDivisa'},
		 	{name: 'formaPago'},
		 	{name: 'idBanco'},
		 	{name: 'estatus'},
		 	{name: 'tipoCambio'},
		 	{name: 'noFolioDet'},
		 	{name: 'exporta'},		 	
		 	{name: 'concepto'},
		 	{name: 'referencia'},
		 	{name: 'ctaContablea'},
		 	{name: 'idTipoMovto'}		 	
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando..."});
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen registros para estos parametros');
				else{
					Ext.getCmp(PF + 'txtNoRegistros').setValue(records.length);
					NS.totalMN = 0;
					NS.totalDLS = 0;
					NS.foliosExporta = '';
					
					for (var i = 0; i < records.length; i++){					
						if(records[i].get('idDivisa') == 'MN'){							
							NS.totalMN = NS.totalMN + parseInt(records[i].get('importe'));							
						}
						else{							
							NS.totalDLS = NS.totalDLS + parseInt(records[i].get('importe'));							
						}
						
						if (NS.estatus == 1){						
							NS.foliosExporta = NS.foliosExporta + records[i].get('noFolioDet') + ",";
						}
					}								
					
					Ext.getCmp(PF + 'txtTotalMN').setValue(NS.formatNumber(NS.totalMN));
					Ext.getCmp(PF + 'txtTotalDLS').setValue(NS.formatNumber(NS.totalDLS));
				}
			}
		}		
	});
	
	//COMBOBOX
	NS.cmbEmpresa = new Ext.form.ComboBox ({
		store: NS.storeComboEmpresa,
		id: PF + 'cmbEmpresa',
		name: PF + 'cmbEmpresa',
		x: 180,
		y: 15,
		width: 400,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}		
	});
	
	//RADIO BUTTON	
	NS.optRadioEstatus = new Ext.form.RadioGroup
	({
		id: PF + 'optRadios',
		name: PF + 'optRadios',
		x: 0,
		y: 0,
		columns: 1,
		items:
		[
		 	{boxLabel: 'Rechazadas', name: 'optSeleccion', inputValue: 2, checked: true, 
		 	listeners: {
		 		check: {
		 			fn: function(opt, valor) {
		 				if(valor == true)
		 					NS.estatus = 2;		 				
		 			}
		 		}		 		
		 	}}	 	
		]
	});
	
	
	//COMPONENTES DE GRID
	//Columna de seleccion
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//Columnas del grid
	NS.columnasGrid = new Ext.grid.ColumnModel ([
	    NS.columnaSeleccion,
	  	{header: 'Empresa', width: 70, dataIndex: 'noEmpresa', sortable: true},
	  	{header: 'Concepto', width: 70, dataIndex: 'concepto', sortable: true},
	  	{header: 'Referencia', width: 70, dataIndex: 'referencia', sortable: true},
	  	{header: 'TipoMovto', width: 70, dataIndex: 'idTipoMovto', sortable: true},
	  	{header: 'Banco', width: 50, dataIndex: 'idBanco', sortable: true},
	  	{header: 'Chequera', width: 100, dataIndex: 'idChequera', sortable: true},
	  	{header: 'No Cheque', width: 70, dataIndex: 'noCheque', sortable: true},
	  	{header: 'Importe', width: 80, dataIndex: 'importe', sortable: true, css: 'text-align:right;'},
	  	{header: 'Fec Valor', width: 100, dataIndex: 'fecValor', sortable: true},
	  	{header: 'Divisa', width: 50, dataIndex: 'idDivisa', sortable: true},
	  	{header: 'Cta Contable', width: 50, dataIndex: 'ctaContablea', sortable: true},
	  	{header: 'Forma de Pago', width: 100, dataIndex: 'formaPago', sortable: true},
	  	{header: 'Estatus', width: 80, dataIndex: 'estatus', sortable: true},
	  	{header: 'Tipo de Cambio', width: 60, dataIndex: 'tipoCambio', sortable: true},
	  	{header: 'Folio SET', width: 60, dataIndex: 'noFolioDet', sortable: true, hidden: true},
	  	{header: 'Exporta', width: 50, dataIndex: 'exporta', sortable: true, hidden: true},
	  	{header: 'No. Docto', width: 80, dataIndex: 'noDocto', sortable: true}
	]);
	
	//GRID
	NS.gridDatos = new Ext.grid.GridPanel
	({
		store: NS.storeLlenaGrid,
		id: PF + 'gridDatos',
		name: PF + 'gridDatos',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 960,
		height: 180,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
		
				}
			}
		}
	});

	//PANEL
	NS.panelRadio = new Ext.form.FieldSet({
		title: "",
		x: 0,
		y: 0,
		width: 100,
		height: 63,
		layout: 'absolute',
		hidden:true,
		items:
		[
		 	NS.optRadioEstatus
		]
	});
	
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: "",
		x: 0,
		y: 0,
		width: 985,
		height: 85,
		layout: 'absolute',
		items: [
		    NS.panelRadio,
		    NS.lblEmpresa,
		    NS.txtNoEmpresa,
		    NS.cmbEmpresa,
		    {
		    	xtype: 'button',
		    	text: 'Buscar',
		    	x: 800,
		    	y: 15,
		    	width: 80,
		    	height: 22,
		    	listeners: {
		    		click: {
		    			fn: function(e) {
		    				if (Ext.getCmp(PF + 'txtNoEmpresa').getValue() != '')
		    					NS.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
		    						    				
		    				NS.storeLlenaGrid.baseParams.noEmpresa = parseInt(NS.noEmpresa);		    
		    				NS.storeLlenaGrid.baseParams.fecHoy = NS.fecHoy;
		    				
		    				NS.storeLlenaGrid.baseParams.estatus = 2;
		    				NS.storeLlenaGrid.load();
		    				
               		    	NS.cmbGrupo.setValue('');
               		    	NS.cmbRubro.setValue('');
               		    	NS.txtCuentaContable.setValue('');
               		    	NS.cmbCuentaContable.setValue('');
               		    	Ext.getCmp(PF+'txtGrupoBanco').setValue('');
               		    	Ext.getCmp(PF+'txtRubroBanco').setValue('');
               		    	NS.frameReparacion.setVisible(false);
               		    	NS.panelGrid.setDisabled(false);
		    			}
		    		}
		    	}
		    }
		]		
	});
	
	NS.panelGrid = new Ext.form.FieldSet({
		title: "",
		x: 0,
		y: 100,
		width: 985,
		height: 235,
		layout: 'absolute',
		
		items: [
		    NS.gridDatos,
		    NS.lblNoRegistros,
		    NS.lblTotalMN,
		    NS.lblTotalDLS,
		    //NS.lblImporteSolicitado,
		    //NS.lblFolioExportacion,
		    NS.txtNoRegistros,
		    NS.txtTotalMN,
		    NS.txtTotalDLS/*,
		    NS.txtImporteSolicitado,
		    NS.txtFolioExportacion*/		    
		]
	});
	
	/***************************************************************************
	 * FRAME REPARACION 8======================================================D
	 */
	
	NS.storeGrupo = new Ext.data.DirectStore( {
		paramOrder:['idTipoMovto'],
		paramsAsHash: false,
		baseParams:{
		idTipoMovto:'I'			
		},		
		root: '',
		directFn: TraspasosAction.llenarComboGrupo,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		]	
	});
	
	NS.storeGrupo.load();
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo
		,name: PF+'cmbGrupo'
		,id: PF+'cmbGrupo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 315
        ,y: 24
        ,width: 180
		,valueField:'idGrupo'
		,displayField:'descGrupo'
		,autocomplete: true
		,emptyText: 'Seleccione un grupo'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtGrupoBanco',NS.cmbGrupo.getId());
					Ext.getCmp(PF + 'cmbRubro').reset();
					Ext.getCmp(PF+'txtRubroBanco').reset();
					NS.storeRubro.baseParams.condicion = 'id_grupo = \'' + combo.getValue() + '\'';
					NS.storeRubro.load();
				}
			}
		}
	});
	
	NS.storeRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_rubro',
			campoDos:'desc_rubro',
			tabla:'cat_rubro',
			condicion:'',
			orden:'2'
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		directFn: GlobalAction.llenarComboGeneral, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
			
				NS.cmbRubro.setValue(NS.storeRubro.getById(Ext.getCmp(PF+'txtRubroBanco').getValue()).get('descripcion'));
			}
		}
	}); 
	
	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeRubro
		,name: PF+'cmbRubro'
		,id: PF+'cmbRubro'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 580
        ,y: 24
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un rubro'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtRubroBanco',NS.cmbRubro.getId());
					//NS.accionarCmbRubro(combo.getValue());
				}
			}
		}
	});
	
	
	/***************************************************************************
	 * CONTROLES SELECCION DE CUENTAS 8========================================D
	 * INIT
	 */
	
	NS.lblCuentaContable = new Ext.form.Label({
	    xtype: 'label',
	    text: 'Cuenta:',
	    x: 0,
	    y: 0
	});
	
	NS.txtCuentaContable = new Ext.form.TextField({
        xtype: 'textfield',
        id: PF+'txtCuentaContable',
        name: PF+'txtCuentaContable',
        x: 0,
        y: 24,
        width: 90,
        hidden:true,
        listeners:{
	   		change:{
	   			fn:function(ov, nv){
	   				
					NS.storeCuentaContable.removeAll();
	   				NS.cmbCuentaContable.reset();
	   				
	   				if( ov.getValue() != '' ){
	   						   					
	   					//NS.storeCuentaContable.removeAll();
	   					NS.storeCuentaContable.baseParams.noCuentaContable= ov.getValue() + '';	 
	   					NS.storeCuentaContable.baseParams.idTipoMovto= NS.idTipoMovto + '';
	   					
	   					
	   					NS.storeCuentaContable.load();
	   				}
				}
			}//End change
	   }//End listeners
    });
	
    NS.storeCuentaContable = new Ext.data.DirectStore({
			paramsAsHash: false,
			baseParams:{
	    	noCuentaContable:'',
	    	idTipoMovto:''
			},
			root: '',
			paramOrder:['noCuentaContable','idTipoMovto'],
			root: '',
			directFn: ExportacionAction.llenaComboCuentas, 
			idProperty: 'cuentaContable', 
			fields: [
			         	{name: 'cuentaContable'},			         	
			         	{name: 'idGuia'},
			         	{name: 'noEmpresa'},
			         	{name: 'idRubro'},
			         	{name: 'idGrupo'}
			         ],
			listeners: {
				load: function(s, records){
					
					var myMask = new Ext.LoadMask( Ext.getBody(), {store: NS.storeCuentaContable, msg:"Cargando..."});
					
					if(records.length==null || records.length<=0) {
						
						
						Ext.Msg.alert('SET','No esta registrada la cuenta contable en el catalogo de SET.\nReportarlo a Tesorería para su registro.',function(btn){
							Ext.Msg.confirm('confirmación',' Por el momento, ¿Desea utilizar la cuenta ' + NS.tmpCtaContable + ' para reparar y contabilizar el registro ?', function(btn){
								if(btn === 'yes'){  
									
									var matrizRegistros = new Array ();
    								var matrizCriterios = new Array ();
    					
    								var regSelec = NS.gridDatos.getSelectionModel().getSelections();
															
    								BFwrk.Util.msgWait('Actualizando...', true);
						
    								for(var i = 0; i < regSelec.length; i ++)
    								{
    									var registro = {};
    									registro.noFolioDet = regSelec[i].get('noFolioDet');        														  
    									matrizRegistros[i] = registro; 
    								}
						
						
    								var criterio = {};
    								criterio.idGrupo = '';
    								criterio.idRubro = '';
    								criterio.idCuentaContable = NS.tmpCtaContable;
    								matrizCriterios[i] = criterio; 
						
    								var jsonStringRegistros = Ext.util.JSON.encode(matrizRegistros);
    								var jsonStringCriterios = Ext.util.JSON.encode(matrizCriterios);
						
    								ExportacionAction.updateMovimientoSET(jsonStringRegistros, jsonStringCriterios, function(result, e){
  			   			
    									if(result.msgUsuario!==null  &&  result.msgUsuario!=='' && result.msgUsuario!= undefined)
    									{
    										BFwrk.Util.msgWait('Finalizado...', false);
    										BFwrk.Util.msgShow(''+result.msgUsuario,'INFO');
    										NS.storeLlenaGrid.load();
				               		    	NS.cmbGrupo.setValue('');
				               		    	NS.cmbRubro.setValue('');
				               		    	NS.txtCuentaContable.setValue('');
				               		    	NS.cmbCuentaContable.setValue('');
				               		    	Ext.getCmp(PF+'txtGrupoBanco').setValue('');
				               		    	Ext.getCmp(PF+'txtRubroBanco').setValue('');
				               		    	NS.frameReparacion.setVisible(false);
				               		    	NS.panelGrid.setDisabled(false);


    									}
    								});                        	   			

						        }
								
							}); 
						});   						

						Ext.getCmp(PF + 'txtCuentaContable').setValue('');
						NS.cmbCuentaContable.reset();
						
					}else {
						
						NS.cmbCuentaContable.setValue(records[0].get('cuentaContable'));   						
						Ext.getCmp(PF+'txtCuentaContable').setValue(records[0].get('cuentaContable'));
						Ext.getCmp(PF+'txtGrupoBanco').setValue(records[0].get('idGrupo'));
						NS.cmbGrupo.setValue(NS.storeGrupo.getById(records[0].get('idGrupo')).get('descGrupo'));
						
						//LLENAR GRUPO
						Ext.getCmp(PF + 'cmbRubro').reset();
						Ext.getCmp(PF+'txtRubroBanco').reset();
						NS.storeRubro.baseParams.condicion = 'id_grupo = \'' + records[0].get('idGrupo') + '\'';
						NS.storeRubro.load();
						
						Ext.getCmp(PF+'txtRubroBanco').setValue(records[0].get('idRubro'));
						 
						
					}
			}
			
		}
	});
	
 	   
    NS.cmbCuentaContable = new Ext.form.ComboBox({			
		name: PF+'cmbCuentaContable'			
		,id: PF+'cmbCuentaContable'
		,store: NS.storeCuentaContable
		,valueField:'cuentaContable'
		,displayField:'cuentaContable'
		,typeAhead: true
		,mode: 'local'   		
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,pageSize: 10
		,x: 0
		,y: 24
		,width: 220
		,autocomplete: true
		,emptyText: 'Digite Cuenta y de clic al boton'
		,triggerAction: 'all'			
		,visible: false
		,editable: true
		,autocomplete: true
		,triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor) {								
						BFwrk.Util.updateComboToTextField(PF+'txtCuentaContable',NS.cmbCuentaContable.getId());
					
				}//End function
	
			},//End select				
			beforequery: function(qe){				
				NS.textCuentaContable = '';
				NS.textCuentaContable= qe.combo.getRawValue();
			},
			change:{
				fn:function(nv,ov){
				
					if(ov == '' ){	
						
							NS.storeRubro.removeAll();
							Ext.getCmp(PF+'txtCuentaContable').setValue('');
               		    	NS.cmbGrupo.setValue('');
               		    	NS.cmbRubro.setValue('');
               		    	Ext.getCmp(PF+'txtGrupoBanco').setValue('');
               		    	Ext.getCmp(PF+'txtRubroBanco').setValue('');

					}
					
				}
			}
	
		}//End listeners
		
	});
	
	NS.btnSearchCuentaContable = new Ext.Button({
		id: PF+'btnSearchCuentaContable',
        name: PF+'btnSearchCuentaContable',
		text: '...',
        x: 220,
        y: 24,
        width: 20,
        height: 22,
        listeners:{
			click:{
					fn:function(){
		
						
						NS.storeCuentaContable.removeAll();
						var textCuentaContable = NS.textCuentaContable;
						var numCuentaContable =  parseInt( Ext.getCmp(PF + 'txtCuentaContable').getValue() ) ;
						NS.tmpCtaContable = '';
						NS.tmpCtaContable = textCuentaContable;
						
						
						if( textCuentaContable == '' && isNaN(numCuentaContable) )
						{
                			Ext.Msg.alert('Información SET','El texto y caja de selección estan vacios.');
                			return;
						}
														
						if ( isNaN(numCuentaContable) ){
							
                			NS.storeCuentaContable.baseParams.noCuentaContable = textCuentaContable;
                			NS.storeCuentaContable.baseParams.idTipoMovto= NS.idTipoMovto + '';
                			
						}
							
						if(textCuentaContable == ''){
							
							NS.storeCuentaContable.baseParams.noCuentaContable= numCuentaContable;
							NS.storeCuentaContable.baseParams.idTipoMovto= NS.idTipoMovto + '';
						}
						
                		NS.storeCuentaContable.load();
                		
						
					}//End function
	
			}//End click
	
		}//End listeners
        		
	});
	/***************************************************************************
	 * CONTROLES SELECCION DE CUENTAS 8========================================D
	 * END
	 */
	
	
	NS.frameReparacion = new Ext.form.FieldSet({
        title: 'Actualización de Cuenta Contable',        
        y: 350,
        layout: 'absolute',
        height: 90,
        width:890,
        padding: 0,
        hidden:true,
        items: [
                			NS.lblCuentaContable,
                			NS.txtCuentaContable,
                			NS.cmbCuentaContable,
                			NS.btnSearchCuentaContable,
                           {
                        	   
                               xtype: 'label',
                               text: 'Id Grupo:',
                               x: 250,
                               y: 1
                           },
                           {
                               xtype: 'label',
                               text: 'Grupo:',
                               x: 315,
                               y: 1
                           },
                           {
                               xtype: 'label',
                               text: 'Id Rubro:',
                               x: 515,
                               y: 1
                           },
                           {
                               xtype: 'label',
                               text: 'Rubro:',
                               x: 580,
                               y: 1
                           },
                           {
                               xtype: 'textfield',
                               id: PF+'txtGrupoBanco',
                               name: PF+'txtGrupoBanco',
                               x: 250,
                               y: 24,
                               width: 60,
                               listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupoBanco',NS.cmbGrupo.getId());
		                        		}
		                        	}
		                        }
                           },
                           NS.cmbGrupo,
                           {
                               xtype: 'textfield',
                               id: PF+'txtRubroBanco',
                               name: PF+'txtRubroBanco',
                               x: 515,
                               y: 24,
                               width: 60,
                               listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtRubroBanco',NS.cmbRubro.getId());
		                        			NS.accionarCmbRubro(comboValor);
		                        		}
		                        	}
		                        }
                           },
                           NS.cmbRubro,
                           {
                               xtype: 'checkbox',
                               id: PF + 'chkContabiliza',
                               name: PF + 'chkContabiliza',
                               checked: false,
                               x: 545,
                               y: 23,
                               boxLabel: 'Contabiliza',
                               hidden:true
                              
                           },
               		    {
               		    	xtype: 'button',
               		    	text: 'Aceptar',
               		    	x: 780,
               		    	y: 0,
               		    	width: 80,
               		    	height: 22,
               		    	listeners: {
               		    		click: {
               		    			fn: function(e) {
                        	   
                        	   			if( NS.txtCuentaContable.getValue() == '')
                        	   			{
                        	   				BFwrk.Util.msgShow('No ha seleccionado la nueva cuenta contable.', 'INFO');
                        	   				return;
                        	   			}
                        	   
                        	   			if( Ext.getCmp(PF+'txtGrupoBanco').getValue() == '')
                        	   			{
                        	   				BFwrk.Util.msgShow('No hay Grupo Asignado.', 'INFO');
                        	   				return;
                        	   			}
                        	   			
                        	   			if( Ext.getCmp(PF+'txtRubroBanco').getValue() == '')
                        	   			{
                        	   				BFwrk.Util.msgShow('No hay Rubro Asignado.', 'INFO');
                        	   				return;
                        	   			}
                        	   			
                        	   			
            							var matrizRegistros = new Array ();
        								var matrizCriterios = new Array ();
        					
        								var regSelec = NS.gridDatos.getSelectionModel().getSelections();
																														
        								if(!confirm('¿Esta seguro de actualizar los movimientos? '))
											return;											
							
        								BFwrk.Util.msgWait('Actualizando...', true);
							
        								for(var i = 0; i < regSelec.length; i ++)
        								{
        									var registro = {};
        									registro.noFolioDet = regSelec[i].get('noFolioDet');        														  
        									matrizRegistros[i] = registro; 
        								}
							
							
        								var criterio = {};
        								criterio.idGrupo = Ext.getCmp(PF+'txtGrupoBanco').getValue();
        								criterio.idRubro = Ext.getCmp(PF+'txtRubroBanco').getValue();
        								criterio.idCuentaContable = Ext.getCmp(PF+'txtCuentaContable').getValue();
        								matrizCriterios[i] = criterio; 
							
        								var jsonStringRegistros = Ext.util.JSON.encode(matrizRegistros);
        								var jsonStringCriterios = Ext.util.JSON.encode(matrizCriterios);
							
        								ExportacionAction.updateMovimientoSET(jsonStringRegistros, jsonStringCriterios, function(result, e){
      			   			
        									if(result.msgUsuario!==null  &&  result.msgUsuario!=='' && result.msgUsuario!= undefined)
        									{
        										BFwrk.Util.msgWait('Finalizado...', false);
        										BFwrk.Util.msgShow(''+result.msgUsuario,'INFO');
        										NS.storeLlenaGrid.load();
					               		    	NS.cmbGrupo.setValue('');
					               		    	NS.cmbRubro.setValue('');
					               		    	NS.txtCuentaContable.setValue('');
					               		    	NS.cmbCuentaContable.setValue('');
					               		    	Ext.getCmp(PF+'txtGrupoBanco').setValue('');
					               		    	Ext.getCmp(PF+'txtRubroBanco').setValue('');
					               		    	NS.frameReparacion.setVisible(false);
					               		    	NS.panelGrid.setDisabled(false);


        									}
        								});                        	   			
                        	   
                        	   			
               		    			}//End function
               		    		}//End click
               		    	}//End listener
               		    },//End Button
               		 {
               		    	xtype: 'button',
               		    	text: 'Cancelar',
               		    	x: 780,
               		    	y: 30,
               		    	width: 80,
               		    	height: 22,
               		    	listeners: {
               		    		click: {
               		    			fn: function(e) {
			               		    	NS.cmbGrupo.setValue('');
			               		    	NS.cmbRubro.setValue('');
			               		    	NS.txtCuentaContable.setValue('');
			               		    	NS.cmbCuentaContable.setValue('');
			               		    	Ext.getCmp(PF+'txtGrupoBanco').setValue('');
			               		    	Ext.getCmp(PF+'txtRubroBanco').setValue('');
			               		    	NS.frameReparacion.setVisible(false);
			               		    	NS.panelGrid.setDisabled(false);
               		    	
               		    			}//End function
               		    		}//End click
               		    	}//End listener
               		    }//End Button

                  ]
	});
	
	//Contenedor global
	NS.Global = new Ext.form.FieldSet
	({
		title: "",
		x: 20,
		y: 5,
		width: 1010,
		height: 490,
		layout: 'absolute',
		items:
		[
		 	NS.panelBusqueda,
		 	NS.panelGrid,
		 	NS.frameReparacion,
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 700,
		 		y: 440,
		 		width: 80,
		 		height: 22,
		 		hidden:true,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Reparar',
		 		x: 900,
		 		y: 360,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.repararRegistros();
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Limpiar',
		 		x: 900,
		 		y: 400,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.limpiaPantalla();
		 					
               		    	
		 				}
		 			}
		 		}
		 	}
		]
	});
			
	//Contenedor principal del formulario
	NS.ExportaPagos = new Ext.FormPanel
	({
		title: '',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'exportaPagos',
		name: PF + 'exportaPagos',
		renderTo: NS.tabContId,
		items:
		[
		 	NS.Global		 	
		]
	});
	
	
	          
	NS.ExportaPagos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});