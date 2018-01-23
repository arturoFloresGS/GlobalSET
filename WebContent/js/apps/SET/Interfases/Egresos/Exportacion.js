Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Interfases.Egresos.Exportacion'); //Nombre del archivo de trabajo
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
	
	//LABEL
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 120,
		y: 0
	});
	
	NS.lblNoRegistros = new Ext.form.Label({
		text: 'No. Registros',
		x: 0,
		y: 270
	});
	
	NS.lblTotalMN = new Ext.form.Label({
		text: 'Total MN',
		x: 200,
		y: 270
	});
	
	NS.lblTotalDLS = new Ext.form.Label({
		text: 'Total DLS',
		x: 400,
		y: 270
	});
	
	NS.lblImporteSolicitado = new Ext.form.Label({
		text: 'Importe Solicitado',
		x: 600,
		y: 270
	});
	
	NS.lblFolioExportacion = new Ext.form.Label({
		text: 'Folio de Exportación',
		x: 800,
		y: 270
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
		x: 0,
		y: 285,
		width: 100
	});
	
	NS.txtTotalMN = new Ext.form.TextField({
		id: PF + 'txtTotalMN',
		name: PF + 'txtTotalMN',
		x: 200,
		y: 285,
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
		x: 400,
		y: 285,
		width: 100
	});
	
	NS.txtImporteSolicitado = new Ext.form.TextField({
		id: PF + 'txtImporteSolicitado',
		name: PF + ' txtImporteSolicitado',
		x: 600,
		y: 285,
		width: 100
	});
	
	NS.txtFolioExportacion = new Ext.form.TextField({
		id: PF + 'txtFolioExportacion',
		name: PF + 'txtFolioExportacion',
		x: 800,
		y: 285,
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
	};
	
	NS.exportaRegistros = function(){
		if(NS.foliosExporta == '') {
			Ext.Msg.alert('SET', 'No existen registros para exportar!!');
			return;
		}
		
		if(Ext.getCmp(PF + 'txtNoEmpresa').getValue() == '') {
			Ext.Msg.alert('SET', 'Seleccione una empresa!!');
			return;
		}
		
		ExportacionAction.exportaRegistros(NS.foliosExporta, parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue()), 
				function(resultado, e){
			
			if (resultado != null && resultado != '' && resultado != undefined)
				Ext.Msg.alert('SET', resultado);
			
			NS.limpiaPantalla();
		});
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
		 	{name: 'nomEmpresa'},
		 	{name: 'noDocto'},
		 	{name: 'noCheque'},
		 	{name: 'idChequera'},
		 	{name: 'fecValor'},
		 	{name: 'importe'},
		 	{name: 'idDivisa'},
		 	{name: 'descDivisa'},
		 	{name: 'formaPago'},
		 	{name: 'descFormaPago'},
		 	{name: 'idBanco'},
		 	{name: 'descBanco'},
		 	{name: 'estatus'},
		 	{name: 'tipoCambio'},
		 	{name: 'noFolioDet'},
		 	{name: 'exporta'},
		 	{name: 'causaRech'},
		 	{name: 'concepto'},
		 	{name: 'idTipoMovto'},
		 	{name: 'idTipoOperacion'},
		 	{name: 'razonSocial'}
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
						
						if (NS.estatus == 1 && records[i].get('causaRech') == '') {
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
		 	{boxLabel: 'Realizadas', name: 'optSeleccion', inputValue: 0, checked: true, 
		 	listeners: {
		 		check: {
		 			fn: function(opt, valor) {
		 				if(valor == true)
		 					NS.estatus = 1;		 				
		 			}
		 		}		 		
		 	}},
		 	{boxLabel: 'Canceladas', name: 'optSeleccion', inputValue: 1,
	 		listeners: {
	 			check: {
	 				fn: function(opt, valor) {
	 					if (valor == true)
	 						NS.estatus = 0;
	 				}
	 			}
	 		}
		 	}	 	
		]
	});
	
	
	//COMPONENTES DE GRID
	//Columna de seleccion
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//Columnas del grid
	NS.columnasGrid = new Ext.grid.ColumnModel ([
	  	{header: 'No. Empresa', width: 60, dataIndex: 'noEmpresa', sortable: true, hidden: true},
	  	{header: 'Empresa', width: 160, dataIndex: 'nomEmpresa', sortable: true},
	  	{header: 'No. Banco', width: 50, dataIndex: 'idBanco', sortable: true, hidden: true},
	  	{header: 'Banco', width: 100, dataIndex: 'descBanco', sortable: true},
	  	{header: 'Chequera', width: 100, dataIndex: 'idChequera', sortable: true},
	  	{header: 'Importe', width: 100, dataIndex: 'importe', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	  	{header: 'Concepto', width: 180, dataIndex: 'concepto', sortable: true},
	  	{header: 'No. Docto', width: 80, dataIndex: 'noDocto', sortable: true},
	  	{header: 'No. Forma Pago', width: 100, dataIndex: 'formaPago', sortable: true, hidden: true},
	  	{header: 'Forma Pago', width: 100, dataIndex: 'descFormaPago', sortable: true},
	  	{header: 'No. Cheque', width: 70, dataIndex: 'noCheque', sortable: true},
	  	{header: 'Id. Divisa', width: 50, dataIndex: 'idDivisa', sortable: true, hidden: true},
	  	{header: 'Divisa', width: 80, dataIndex: 'descDivisa', sortable: true},
	  	{header: 'Fec Valor', width: 100, dataIndex: 'fecValor', sortable: true},
	  	{header: 'Tipo Movimiento', width: 100, dataIndex: 'idTipoMovto', sortable: true},
	  	{header: 'Estatus', width: 80, dataIndex: 'estatus', sortable: true},
	  	{header: 'Proveedor', width: 200, dataIndex: 'razonSocial', sortable: true},
	  	{header: 'Causa Rechazo', width: 200, dataIndex: 'causaRech', sortable: true},
	  	{header: 'Tipo de Cambio', width: 60, dataIndex: 'tipoCambio', sortable: true, hidden:true},
	  	{header: 'Folio SET', width: 60, dataIndex: 'noFolioDet', sortable: true, hidden: true},
	  	{header: 'Exporta', width: 50, dataIndex: 'exporta', sortable: true, hidden: true}
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
		height: 250,
		stripeRows: true,
		columnLines: true,
		viewConfig: {
	        getRowClass: function(record, index){
				if(record.get('causaRech') != '')
		        	return 'row-font-color-red';
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
		    				
		    				NS.storeLlenaGrid.baseParams.estatus = NS.estatus;
		    				NS.storeLlenaGrid.load();
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
		height: 330,
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
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 700,
		 		y: 440,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Ejecutar',
		 		x: 800,
		 		y: 440,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.exportaRegistros();
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Limpiar',
		 		x: 900,
		 		y: 440,
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
		title: 'Exportación de Pagos',
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