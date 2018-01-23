Ext.onReady(function(){

	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.PrestamosInterempresas.CalculoDeInteres');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;

	NS.smCheck = new Ext.grid.CheckboxSelectionModel({
				checkOnly: true		
	});
	
	NS.limpiar = function()
	{
		NS.CalculoDeInteres.getForm().reset();
		NS.storeCalculoDeInteres.removeAll();
		NS.gridCalculoDeInteres.getView().refresh();
	};
	
	NS.storeCalculoDeInteres = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:
		{
			sFecIni: '',
			sFecFin: '',
			iPlazo: 0,
			uTasa: 0,
			sDivisa: ''
		},
		paramOrder:
		[
			'sFecIni','sFecFin','iPlazo','uTasa','sDivisa'
		],
		root: '',
		directFn: PrestamosInterempresasAction.obtenerConsultaInteresPresNoDoc, 
		fields: [
			{name: 'iva'},
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'noCuentaEmp'},
			{name: 'noLineaEmp'},
			{name: 'idDivisaSoin'},
			{name: 'idDivisa'},
			{name: 'saldoPromedio'},
			{name: 'interesesPorPagar'},
			{name: 'noEmpBenef'},
			{name: 'nomEmpBenef'},
			{name: 'cheqBenef'},
			{name: 'bancoBenef'},
			{name: 'nomBancoBenef'},
			{name: 'cheqOrigen'},
			{name: 'bancoOrigen'}
		],
		listeners: 
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCalculoDeInteres, msg:"Buscando..."});
				var uImporte = 0;
				console.log();
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No hay datos...');
					return;
				}
				Ext.getCmp(PF + 'txtTotalRegistros').setValue(records.length);
			}
		}
	});
	
	NS.gridCalculoDeInteres = new Ext.grid.GridPanel({
    store : NS.storeCalculoDeInteres,
    cm: new Ext.grid.ColumnModel({
        defaults: {
            width: 120,
            value:true,
            sortable: true
        },
           columns: [
               NS.smCheck,
            {
				header :'Empresa',
				width :70,
				sortable :true,
				dataIndex :'noEmpresa'
			},{
				header :'Nom. Empresa',
				width :130,
				sortable :true,
				dataIndex :'nomEmpresa'
			},{
				header :'Saldo Promedio',
				width :100,
				sortable :true,
				dataIndex :'saldoPromedio',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Intereses Por Pagar',
				width :90,
				sortable :true,
				dataIndex :'interesesPorPagar'
			},
			{
				header :'Iva',
				width :70,
				sortable :true,
				dataIndex :'iva',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Empresa Benef',
				width :50,
				sortable :true,
				dataIndex :'noEmpBenef'
			},{
				header :'Nom Empresa Benef',
				width :100,
				sortable :true,
				dataIndex :'nomEmpBenef'
			},{
				header :'Chequera Benef',
				width :90,
				sortable :true,
				dataIndex :'cheqBenef'
			},{
				header :'Banco benef',
				width :80,
				sortable :true,
				dataIndex :'bancoBenef'
			},{
				header :'Nom Banco Benef',
				width :80,
				sortable :true,
				dataIndex :'nomBancoBenef'
			},{
				header :'Divisa',
				width :50,
				sortable :true,
				dataIndex :'idDivisa'
			},{
				header :'Chequera Origen',
				width :80,
				sortable :true,
				dataIndex :'cheqOrigen'
			},{
				header :'Banco Origen',
				width :80,
				sortable :true,
				dataIndex :'bancoOrigen'
			},{
				header :'Cuenta Emp',
				width :70,
				sortable :true,
				dataIndex :'noCuentaEmp',
				hidden: true
			},{
				header :'No LineaEmp',
				width :70,
				sortable :true,
				dataIndex :'noLineaEmp',
				hidden: true
			},{
				header :'Divisa Soin',
				width :50,
				sortable :true,
				dataIndex :'idDivisaSoin',
				hidden: true
			}
           ]
        }),
        sm: NS.smCheck,
        columnLines: true,
        x:0,
        y:0,
        width:700,
        height:230,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			
        		}
        	}
        }
    });
	
	NS.storeDivisas = new Ext.data.DirectStore({
	paramsAsHash: false,
	baseParams:
	{
		sCond:''
	},
	paramOrder:
	[
		'sCond'
	],
	directFn: GlobalAction.llenarComboDivisas, 
	idProperty: 'idDivisa', 
	fields: [      
		 {name: 'idDivisa'},
		 {name: 'descDivisa'}      
	],
	listeners: 
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisas, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen divisas');
					return;
				}
			}
		}
	}); 
	
	NS.storeDivisas.load();
	
	NS.cmbDivisas = new Ext.form.ComboBox({
		store: NS.storeDivisas,
		name: PF + 'cmbDivisas',
		id: PF + 'cmbDivisas',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 430,
        y: 20,
        width: 170,
		valueField:'idDivisa',
		displayField:'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor) 
				{
				 	 
				}
			}
		}
	});
	
	NS.validarDatos = function()
	{
		var validaDatos = false;
		if(Ext.getCmp(PF + 'txtFecIni').getValue() === '')
		{
			BFwrk.Util.msgShow('Debe indicar la fecha inicial del rango a considerar','INFO');
			return;
		}
		else if(Ext.getCmp(PF + 'txtFecFin').getValue() === '')
		{
			BFwrk.Util.msgShow('Debe indicar la fecha final del rango a considerar','INFO');
			return;
		}
		else if(Ext.getCmp(PF + 'txtFecIni').getValue() > Ext.getCmp(PF + 'txtFecFin').getValue())
		{
			BFwrk.Util.msgShow('La fecha inicial debe ser menor o igual a la fecha final','INFO');
			return;
		}
		else if(Ext.getCmp(PF + 'txtFecFin').getValue() > BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY))
		{
			BFwrk.Util.msgShow('La fecha final del Rango a considerar debe ser menor a la fecha de hoy','INFO');
			return;
		}
		else if(Ext.getCmp(PF + 'txtTasa').getValue() === '')
		{
			BFwrk.Util.msgShow('Debe indicar la tasa a aplicar para el calculo del interes...','INFO');
			return;
		}
		else if(Ext.getCmp(PF + 'cmbDivisas').getValue() === '')
		{
			BFwrk.Util.msgShow('Debe seleccionar una divisa','INFO');
			return;
		}
		validaDatos = true;
		
		return validaDatos;
		
	};
	
	//Inician datos para mostrar en la ventana
	NS.storePeriodos = new Ext.data.DirectStore({
	paramsAsHash: false,
	root: '',
	directFn: PrestamosInterempresasAction.llenarCmbPeriodos,
	idProperty: 'descripcion',
	fields: [
		 {name: 'descripcion'}
	],
	listeners: 
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePeriodos, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen periodos de presatamo');
					return;
				}
			}
		}
	}); 
	
	
	NS.cmbPeriodos = new Ext.form.ComboBox({
	store: NS.storePeriodos,
	name: PF + 'cmbPeriodos',
	id: PF + 'cmbPeriodos',
	typeAhead: true,
	mode: 'local',
	minChars: 0,
	selecOnFocus: true,
	forceSelection: true,
 	x: 120,
    y: 20,
    width: 200,
	valueField: 'descripcion',
	displayField: 'descripcion',
	autocomplete: true,
	emptyText: 'Seleccione un periodo',
	value: '',
	listeners:{
		select:
			{
			fn:function(combo, valor) 
				{
				 	 
				}
			}
		}
	});
	
	//Ventana para el reporte
	NS.winInforme = new Ext.Window({
    title: 'Informe de Intereses Para Préstamos no Documentados',
    width: 352,
    height: 171,
    layout: 'absolute',
    closable: false,
    modal: true,
       items : 
       [
	        {
	            xtype: 'label',
	            text: 'Periodo del informe',
	            width: 100,
	            x: 10,
	            y: 20
	        },
	        	NS.cmbPeriodos,
	        {
	            xtype: 'button',
	            text: 'Ejecutar',
	            x: 90,
	            y: 80,
	            width: 70,
	            listeners:
	            {
	            	click:
	            	{
	            		fn:function()
	            		{
	            			if(Ext.getCmp(PF + 'cmbPeriodos').getValue() === '')
							{
								BFwrk.Util.msgShow('Debe seleccionar un periodo','INFO');
								return;
							}
	            			NS.reportePresNoDoc();
	            		}
	            	}
	            }
	        },
	        {
	            xtype: 'button',
	            text: 'Limpiar',
	            x: 170,
	            y: 80,
	            width: 70,
	            listeners:
	            {
	            	click:
	            	{
	            		fn:function()
	            		{
	            			Ext.getCmp(PF + 'cmbPeriodos').reset();
	            		}
	            	}
	            }
	            
	        },
	        {
	            xtype: 'button',
	            text: 'Cerrar',
	            x: 250,
	            y: 80,
	            width: 70,
	            id: PF + '',
	            listeners: 
	            {
	            	click:
	            	{
	            		fn: function(e)
	            		{
	            			Ext.getCmp(PF + 'cmbPeriodos').reset();
	            			NS.winInforme.hide();
	            		}
	            	}
	            }
	        }
       	]
	});
	
	//funcion para el reporte
	NS.reportePresNoDoc = function()
	{
		var sPeriodo = Ext.getCmp(PF + 'cmbPeriodos').getValue();
		var strParams = '';
		strParams = '?nomReporte=ReportePrestamosNoDocumentados';
		
		strParams += '&'+'nomParam1=sFecIni';
		strParams += '&'+'valParam1=' + sPeriodo.substring(0,2) + '/' + BFwrk.Util.getNumberMonth(sPeriodo.substring(3,6)) + '/' + sPeriodo.substring(7,11);
		strParams += '&'+'nomParam2=sFecFin';
		strParams += '&'+'valParam2=' + sPeriodo.substring(14,16) + '/' + BFwrk.Util.getNumberMonth(sPeriodo.substring(17,20)) + '/' + sPeriodo.substring(21,25);
		strParams += '&'+'nomParam3=sDivisa';
		strParams += '&'+'valParam3=' + sPeriodo.substring(sPeriodo.length - 3); 
		strParams += '&'+'nomParam4=sFechaHoy';
		strParams += '&'+'valParam4=' + apps.SET.FEC_HOY;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		Ext.getCmp(PF + 'cmbPeriodos').reset();
	};
	
	NS.CalculoDeInteres = new Ext.form.FormPanel({
    title: 'Interes para Prestamos no Documentados',
    width: 772,
    height: 507,
    padding: 10,
    layout: 'absolute',
    id: PF + 'CalculoDeInteres',
    autoScroll: true,
    renderTo: NS.tabContId,
    frame: true,
        items : [
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 10,
                height: 460,
                width: 750,
                layout: 'absolute',
                id: 'fSetPrincipal',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'búsqueda',
                        height: 87,
                        layout: 'absolute',
                        width: 728,
                        id: 'fSetBusqueda',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Periodo de Cálculo'
                            },
                            {
                                xtype: 'datefield',
                                format: 'd/m/Y',
                                allowBlank: false,
                                blankText: 'La fecha inicial es requerida',
                                x: 0,
                                y: 20,
                                width: 110,
                                name: PF + 'txtFecIni',
                                id: PF + 'txtFecIni'
                            },
                            {
                                xtype: 'datefield',
                                format: 'd/m/Y',
                                allowBlank: false,
                                blankText: 'La fecha final es requerida',
                                x: 120,
                                y: 20,
                                width: 110,
                                name: PF + 'txtFecFin',
                                id: PF + 'txtFecFin',
                                listeners:
                                {
                                	change:
                                	{
                                		fn: function(box, value)
                                		{
                                			if(Ext.getCmp(PF + 'txtFecFin').getValue() !== ''
                                				&& box.getValue() !== '')
                                			{
                                				Ext.getCmp(PF + 'txtPlazo').setValue(BFwrk.Util.daysBetweenDates(Ext.getCmp(PF + 'txtFecIni').getValue(), 
                                														Ext.getCmp(PF + 'txtFecFin').getValue()) + 1);
                                			}
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'label',
                                text: 'Plazo',
                                x: 260,
                                y: 0
                            },
                            {
                                xtype: 'numberfield',
                                x: 260,
                                y: 20,
                                width: 60,
                                readOnly: true,
                                name: PF + 'txtPlazo',
                                id: PF + 'txtPlazo'
                            },
                            {
                                xtype: 'label',
                                text: 'Tasa',
                                x: 350,
                                y: 0
                            },
                            {
                                xtype: 'textfield',
                                x: 350,
                                y: 20,
                                width: 60,
                                name: PF + 'txtTasa',
                                id: PF + 'txtTasa'
                            },
                            {
                                xtype: 'label',
                                text: 'Divisa',
                                x: 430,
                                y: 0
                            },
                            NS.cmbDivisas,
                            {
                                xtype: 'button',
                                text: 'Buscar',
                                x: 620,
                                y: 20,
                                width: 70,
                                id: PF + 'cmbBuscar',
                                listeners:
                                {
                                	click:
                                	{
                                		fn: function(e)
                                		{
                                			if(NS.validarDatos())
                                			{
                                				NS.storeCalculoDeInteres.baseParams.sFecIni = BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecIni').getValue());
												NS.storeCalculoDeInteres.baseParams.sFecFin = BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecFin').getValue());
												NS.storeCalculoDeInteres.baseParams.iPlazo = parseInt(Ext.getCmp(PF + 'txtPlazo').getValue());
												NS.storeCalculoDeInteres.baseParams.uTasa = parseFloat(Ext.getCmp(PF + 'txtTasa').getValue());
												NS.storeCalculoDeInteres.baseParams.sDivisa = Ext.getCmp(PF + 'cmbDivisas').getValue();
												NS.storeCalculoDeInteres.load();
                                			}
                                		}
                                	}
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: '',
                        layout: 'absolute',
                        height: 290,
                        x: 0,
                        y: 100,
                        id: 'fSetGrid',
                        items: [
                        	NS.gridCalculoDeInteres,
                            {
                                xtype: 'textfield',
                                x: 300,
                                y: 240,
                                width: 100,
                                name: PF + 'txtTotalRegistros',
                                id: PF + 'txtTotalRegistros'
                            },
                            {
                                xtype: 'label',
                                text: 'Total de Registros',
                                x: 190,
                                y: 240
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Ejecutar',
                        x: 430,
                        y: 410,
                        width: 80,
                        id: PF + 'cmbEjecutar',
                        listeners:
                        {
                        	click:
                        	{
                        		fn: function(e)
                        		{
                        			var regSelecGrid = NS.gridCalculoDeInteres.getSelectionModel().getSelections();
                        			var matReg = new Array();
                        			var sFecIni = '';
                        			var sFecFin = '';
                        			var uValorTasa = 0;
                        			var sIdDivisa = '';
                        			
                        			if(regSelecGrid.length <= 0)
                        			{
                        				BFwrk.Util.msgShow('Debe selecionar los registros para procesar','INFO');
                        				return;
                        			}
                        			
                        			for(var i = 0; i < regSelecGrid.length; i = i + 1)
                        			{
                        				arrayReg = {};
                        				
                        				if((regSelecGrid[i].get('cheqBenef') === null || regSelecGrid[i].get('cheqBenef') === '') 
                        						|| (regSelecGrid[i].get('cheqOrigen') === null || regSelecGrid[i].get('cheqOrigen') === ''))
	                        			{
	                        				BFwrk.Util.msgShow('Algunos de los movimientos involucrados no tienen chequera de traspasos en esta divisa, por favor verifique e intente nuevamente','INFO');
	                        				return;
	                        			}
	                        			
	                        			if(regSelecGrid[i].get('iva') === null || regSelecGrid[i].get('iva') === '' || parseFloat(regSelecGrid[i].get('iva')) <= 0)
	                        			{
	                        				BFwrk.Util.msgShow('Algunos de los movimientos involucrados no tienen tasa de iva, por favor verifique e intente nuevamente','INFO');
	                        				return;
	                        			}
	                        			
                        				arrayReg.iva = regSelecGrid[i].get('iva');
                        				arrayReg.noEmpresa = regSelecGrid[i].get('noEmpresa');
                        				arrayReg.nomEmpresa = regSelecGrid[i].get('nomEmpresa');
                        				arrayReg.noCuentaEmp = regSelecGrid[i].get('noCuentaEmp');
                        				arrayReg.noLineaEmp = regSelecGrid[i].get('noLineaEmp');
                        				arrayReg.idDivisaSoin = regSelecGrid[i].get('idDivisaSoin');
                        				arrayReg.idDivisa = regSelecGrid[i].get('idDivisa');
                        				arrayReg.saldoPromedio = regSelecGrid[i].get('saldoPromedio');
                        				arrayReg.interesesPorPagar = regSelecGrid[i].get('interesesPorPagar');
                        				arrayReg.noEmpBenef = regSelecGrid[i].get('noEmpBenef');
                        				arrayReg.nomEmpBenef = regSelecGrid[i].get('nomEmpBenef');
                        				arrayReg.cheqBenef = regSelecGrid[i].get('cheqBenef');
                        				arrayReg.bancoBenef = regSelecGrid[i].get('bancoBenef');
                        				arrayReg.nomBancoBenef = regSelecGrid[i].get('nomBancoBenef');
                        				arrayReg.cheqOrigen = regSelecGrid[i].get('cheqOrigen');
                        				arrayReg.bancoOrigen = regSelecGrid[i].get('bancoOrigen');
                        				
                        				matReg[i] = arrayReg;
                        			}
                        			var jSonDatGrid = Ext.util.JSON.encode(matReg);
                        			
                        			sFecIni = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF + 'txtFecIni').getValue());
                        			sFecFin = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF + 'txtFecFin').getValue());
                        			uValorTasa = Ext.getCmp(PF + 'txtTasa').getValue();
									sIdDivisa = Ext.getCmp(PF + 'cmbDivisas').getValue();  
									return;//kitar
									PrestamosInterempresasAction.ejecutarCalculoInteres(jSonDatGrid, sFecIni, sFecFin, 
																	parseFloat(uValorTasa), sIdDivisa, function(response, e)
									{
										if(response !== null && response !== '')
										{
											BFWrk.Util.msgShow('' + response[0],'INFO');
											NS.limpiar();
										}
									});									            			
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Imprimir',
                        x: 530,
                        y: 410,
                        width: 80,
                        id: PF + 'cmbImprimir',
                        listeners:
                        {
                        	click:
                        	{
                        		fn: function(e)
                        		{
                        			NS.storePeriodos.load();
                        			NS.winInforme.show();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 630,
                        y: 410,
                        width: 80,
                        id: 'cmbLimpiar',
                        listeners:
                        {
                        	click:
                        	{
                        		fn: function(e)
                        		{
                        			NS.limpiar();
                        		}
                        	}
                        }
                    }
                ]
            }
        ]
	});
    NS.CalculoDeInteres.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
