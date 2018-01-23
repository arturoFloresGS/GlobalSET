Ext.onReady(function()
{
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.PrestamosInterempresas.FlujoNeto');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	
	NS.limpiar = function()
	{
		NS.iIdEmpresa = 0;
		NS.FlujoNeto.getForm().reset();
		NS.storeFlujoNeto.removeAll();
		NS.gridFlujoNeto.getView().refresh();
		Ext.getCmp(PF + 'txtFecha').setValue(apps.SET.FEC_HOY);
	};
	
	NS.smCheck = new Ext.grid.CheckboxSelectionModel({
				checkOnly: true,
				singleSelect: true		
	});
	
	NS.storeFlujoNeto = new Ext.data.DirectStore({
	paramsAsHash: false,
	baseParams:
		{
			iIdEmpresa: 0,
			sIdDivisa: '',
			iSector: 0,
			sFecValor: '',
			bCheckSector: false
		},
		paramOrder:
		[
			'iIdEmpresa','sIdDivisa','iSector','sFecValor','bCheckSector'
		],
		root: '',
		directFn: PrestamosInterempresasAction.llenarGridFlujoNeto, 
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'saldoInicialNeto'},
			{name: 'si'},
			{name: 'ibm'},
			{name: 'efm'},
			{name: 'totalmn'},
			{name: 'pcm'},
			{name: 'sc'},
			{name: 'saldoC'},
			{name: 'sdoFinal'}
		],
		listeners: 
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCalculoDeInteres, msg:"Buscando..."});
				var uSdoIni = 0;
				var uIngre = 0;
				var uEgre = 0;
			    var uSdoCoin = 0;
			    				
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No hay datos...');
					return;
				}
				
				for(var i = 0; i < records.length; i = i + 1)
				{
					uSdoIni = uSdoIni + records[i].get('si');
					uIngre = uIngre + records[i].get('ibm');
					uEgre = uEgre + records[i].get('efm');
					uSdoCoin = uSdoCoin + records[i].get('totalmn');
				}
				Ext.getCmp(PF + 'txtSaldoInicial').setValue(BFwrk.Util.formatNumber(uSdoIni));
				Ext.getCmp(PF + 'txtIngresos').setValue(BFwrk.Util.formatNumber(uIngre));
				Ext.getCmp(PF + 'txtEgresos').setValue(BFwrk.Util.formatNumber(uEgre));
				Ext.getCmp(PF + 'txtSaldoCoinversion').setValue(BFwrk.Util.formatNumber(uSdoCoin));
				
				Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
			}
		}
	});
	
	NS.gridFlujoNeto = new Ext.grid.GridPanel({
    store : NS.storeFlujoNeto,
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
				header :'Saldo Inicial Coinvers\u00f3n Neto',
				width :100,
				sortable :true,
				dataIndex :'saldoInicialNeto',
				renderer: BFwrk.Util.rendererMoney
			},
			{
				header :'Saldo Inicial Coinversi\u00f3n',
				width :100,
				sortable :true,
				dataIndex :'si',
				renderer: BFwrk.Util.rendererMoney
			},
			{
				header :'Ingresos',
				width :100,
				sortable :true,
				dataIndex :'ibm',
				renderer: BFwrk.Util.rendererMoney
			},
			{
				header :'Egresos',
				width :100,
				sortable :true,
				dataIndex :'efm',
				renderer: BFwrk.Util.rendererMoney
			},
			{
				header :'Saldo Coinversi\u00f3n',//Poner divisa seleccionada
				width :100,
				sortable :true,
				dataIndex :'totalmn',
				renderer: BFwrk.Util.rendererMoney
			},
			{
				header :'Pago Cr\u00e9dito',
				width :100,
				sortable :true,
				dataIndex :'pcm',
				renderer: BFwrk.Util.rendererMoney
			},
			{
				header :'Solicitud de Cr\u00e9dito',
				width :100,
				sortable :true,
				dataIndex :'sc',
				renderer: BFwrk.Util.rendererMoney
			},
			{
				header :'Saldo de Cr\u00e9dito',
				width :100,
				sortable :true,
				dataIndex :'saldoC',
				renderer: BFwrk.Util.rendererMoney
			},
			{
				header :'Saldo Final Coinversi\u00f3n Neto',
				width :100,
				sortable :true,
				dataIndex :'sdoFinal',
				renderer: BFwrk.Util.rendererMoney
			}
           ]
        }),
        sm: NS.smCheck,
        columnLines: true,
        x:0,
        y:0,
        width:760,
        height:160,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			
        		}
        	}
        }
    });
	
	NS.storeEmpresa = new Ext.data.DirectStore({
	paramsAsHash: false,
	directFn: PrestamosInterempresasAction.llenarCmbEmpCon, 
	idProperty: 'id', 
	fields: [      
		 {name: 'id'},
		 {name: 'descripcion'}      
	],
	listeners: 
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresa, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existe empresa concentradora');
					return;
				}
			}
		}
	}); 
	
	NS.storeEmpresa.load();
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresa,
		name: PF + 'cmbEmpresa',
		id: PF + 'cmbEmpresa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 130,
        y: 0,
        width: 220,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn:function(combo, valor) 
				{
				 	 BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
				 	 NS.iIdEmpresa = combo.getValue();
				}
			}
		}
	});
	
	NS.storeDivisa = new Ext.data.DirectStore({
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
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen divisas');
					return;
				}
			}
		}
	}); 
	
	NS.storeDivisa.load();
	
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa,
		name: PF + 'cmbDivisa',
		id: PF + 'cmbDivisa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 430,
        y: 0,
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
	
	
	NS.storeSectores = new Ext.data.DirectStore({
	paramsAsHash: false,
	directFn: PrestamosInterempresasAction.llenarCmbSectores, 
	idProperty: 'id', 
	fields: [      
		 {name: 'id'},
		 {name: 'descripcion'}      
	],
	listeners: 
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeSectores, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen sectores');
					return;
				}
				var recordsStoreSectores = NS.storeSectores.recordType;	
				var todas = new recordsStoreSectores({
			       	id : 0,
			       	descripcion : '-------------TODAS-------------'
		       	});
		   		NS.storeSectores.insert(0,todas);
			}
		}
	}); 
	
	NS.storeSectores.load();
	
	NS.cmbSectores = new Ext.form.ComboBox({
		store: NS.storeSectores,
		name: PF + 'cmbSectores',
		id: PF + 'cmbSectores',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 80,
        y: 40,
        width: 200,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una sector',
		triggerAction: 'all',
		value: '',
		disabled: true,
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
		if(NS.iIdEmpresa <= 0)
		{
			BFwrk.Util.msgShow('Debe seleccionar una empresa','INFO');
			return false;
		}
		else if(NS.cmbDivisa.getValue() === '')
		{
			BFwrk.Util.msgShow('Debe seleccionar una divisa','INFO');
			return false;
		}
		return true;
	};
	
	NS.obtenerReporte = function()
	{
		var strParams = '';
		var records = NS.storeFlujoNeto.data.items;
		
		if(records.length <= 0)
		{
			BFwrk.Util.msgShow('No hay registros para imprimir','INFO');
			return;
		}
		
		if((Ext.getCmp(PF + 'checkSector').getValue()) === true)
			strParams = '?nomReporte=ReporteFlujoNetoSectores';
		else
			strParams = '?nomReporte=ReporteFlujoNeto';	
		
		strParams += '&'+'nomParam1=iIdEmpresa';
		strParams += '&'+'valParam1=' + Ext.getCmp(PF + 'cmbEmpresa').getValue();
		strParams += '&'+'nomParam2=sIdDivisa';
		strParams += '&'+'valParam2=' + Ext.getCmp(PF + 'cmbDivisa').getValue();
		strParams += '&'+'nomParam3=iSector';
		strParams += '&'+'valParam3=' + (NS.cmbSectores.getValue() !== null 
															&& NS.cmbSectores.getValue() !== '' 
															? NS.cmbSectores.getValue() 
															: 0);
		strParams += '&'+'nomParam4=sFecValor';
		strParams += '&'+'valParam4=' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecha').getValue());
		strParams += '&'+'nomParam5=bCheckSector';
		strParams += '&'+'valParam5=' + Ext.getCmp(PF + 'checkSector').getValue();
		strParams += '&'+'nomParam6=sNomEmpresa';
		strParams += '&'+'valParam6=' + $('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val();
		strParams += '&'+'nomParam7=sDescDivisa';
		strParams += '&'+'valParam7=' + $('input[id*="'+ NS.cmbDivisa.getId() +'"]').val();
		strParams += '&'+'nomParam8=sFecHoy';
		strParams += '&'+'valParam8=' + apps.SET.FEC_HOY;
	 
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
	};
	
	NS.FlujoNeto = new Ext.form.FormPanel({
    title: 'Consulta de Flujo Neto',
    height: 496,
    padding: 10,
    layout: 'absolute',
    id: PF + 'FlujoNeto',
    renderTo: NS.tabContId,
    autoScroll: true,
    frame: true,
        items : [
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 10,
                width: 815,
                height: 450,
                layout: 'absolute',
                id: 'fSetPrincipal',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Concentradora',
                        height: 100,
                        width: 790,
                        layout: 'absolute',
                        id: 'fSetConcentradora',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa:'
                            },
                            {
                                xtype: 'textfield',
                                x: 60,
                                y: 0,
                                width: 60,
                                name: PF + 'txtNoEmpresa',
                                id: PF + 'txtNoEmpresa',
                                listeners:
                                {
                                	click:
                                	{
                                		fn: function(box, value)
                                		{
                                			var valueId = 0; 
                                			if(box.getValue() !== null || box.getValue() !== '')
                                				valueId = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
                                			if(valueId !== null && valueId !== '')
                                				NS.iIdEmpresa = valueId;
                                			else
                                				NS.iIdEmpresa = 0;
                                		}
                                	}
                                }
                            },
                                NS.cmbEmpresa,
                            {
                                xtype: 'label',
                                text: 'Divisa:',
                                x: 380,
                                y: -1
                            },
                                NS.cmbDivisa,
                            {
                                xtype: 'label',
                                text: 'Sector:',
                                x: 0,
                                y: 40
                            },
                            {
                                xtype: 'checkbox',
                                name: PF + 'checkSector',
                                id: PF + 'checkSector',
                                x: 60,
                                y: 40,
                                listeners: 
                                {
                                	check:
                                	{
                                		fn: function(checkBox)
                                		{
                                			if(checkBox.getValue())
                                				Ext.getCmp(PF + 'cmbSectores').setDisabled(false);
                                			else
												Ext.getCmp(PF + 'cmbSectores').setDisabled(true);   
											
											Ext.getCmp(PF + 'cmbSectores').reset();                             			
                                		}
                                	}
                                }
                            },
                                NS.cmbSectores,
                            {
                                xtype: 'label',
                                text: 'Fecha:',
                                x: 380,
                                y: 40
                            },
                            {
                                xtype: 'datefield',
                                format: 'd/m/Y',
                                x: 430,
                                y: 40,
                                width: 110,
                                id: PF + 'txtFecha',
                                name: PF + 'txtFecha'
                            },
                            {
                                xtype: 'button',
                                text: 'Buscar',
                                x: 670,
                                y: 40,
                                width: 90,
                                id: PF + 'btnBuscar',
                                name: PF + 'btnBuscar',
                                listeners:
                                {
                                	click:
                                	{
                                		fn: function(btn)
                                		{			
                                			if(!NS.validarDatos())
                                				return;
                                			
                                			this.setDisabled(true);
                                			NS.storeFlujoNeto.baseParams.iIdEmpresa = parseInt(NS.cmbEmpresa.getValue());
                                			NS.storeFlujoNeto.baseParams.sIdDivisa = NS.cmbDivisa.getValue();
                                			NS.storeFlujoNeto.baseParams.iSector = parseInt(NS.cmbSectores.getValue() !== null 
                                																&& NS.cmbSectores.getValue() !== '' 
                                																? NS.cmbSectores.getValue() 
                                																: 0);
                                			NS.storeFlujoNeto.baseParams.sFecValor = BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecha').getValue());
                                			NS.storeFlujoNeto.baseParams.bCheckSector = Ext.getCmp(PF + 'checkSector').getValue();
                                			NS.storeFlujoNeto.load();
                                		}
                                	}
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Empresa',
                        x: 0,
                        y: 110,
                        width: 790,
                        height: 280,
                        layout: 'absolute',
                        id: 'fSetGrid',
                        items: [
                        	NS.gridFlujoNeto,
                            {
                                xtype: 'fieldset',
                                title: 'Totales',
                                x: 0,
                                y: 165,
                                width: 765,
                                height: 80,
                                layout: 'absolute',
                                id: 'fSetTotales',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Saldo Inicial',
                                        x: 50,
                                        y: 0
                                    },
                                    {
                                        xtype: 'textfield',
                                        x: 10,
                                        y: 20,
                                        width: 160,
                                        value: '0.0',
                                        readOnly: true,
                                        name: PF + 'txtSaldoInicial',
                                        id: PF + 'txtSaldoInicial'
                                    },
                                    {
                                        xtype: 'label',
                                        text: 'Ingresos',
                                        x: 250,
                                        y: 0
                                    },
                                    {
                                        xtype: 'textfield',
                                        x: 200,
                                        y: 20,
                                        width: 160,
                                        value: '0.0',
                                        readOnly: true,
                                        name: PF + 'txtIngresos',
                                        id: PF + 'txtIngresos'
                                    },
                                    {
                                        xtype: 'label',
                                        text: 'Egresos',
                                        x: 450,
                                        y: 0
                                    },
                                    {
                                        xtype: 'textfield',
                                        x: 390,
                                        y: 20,
                                        width: 160,
                                        value: '0.0',
                                        readOnly: true,
                                        name: PF + 'txtEgresos',
                                        id: PF + 'txtEgresos'
                                    },
                                    {
                                        xtype: 'label',
                                        text: 'Saldo Coinversion DLS',
                                        x: 600,
                                        y: 0
                                    },
                                    {
                                        xtype: 'textfield',
                                        x: 580,
                                        y: 20,
                                        width: 160,
                                        value: '0.0',
                                        readOnly: true,
                                        name: PF + 'txtSaldoCoinversion',
                                        id: PF + 'txtSaldoCoinversion'
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Pago de Crédito',
                        x: 350,
                        y: 400,
                        width: 100,
                        hidden: true,
                        id: 'btnPagoCredito'
                    },
                    {
                        xtype: 'button',
                        text: 'Solicitud de Crédito',
                        x: 460,
                        y: 400,
                        width: 100,
                        hidden: true,
                        id: PF + 'btnSolCredito'
                    },
                    {
                        xtype: 'button',
                        text: 'Imprimimr',
                        x: 570,
                        y: 400,
                        width: 100,
                        id: PF + 'btnImprimir',
                        listeners:
                        {
                        	click:
                        	{
                        		fn: function(btn)
                        		{
                        			NS.obtenerReporte();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 680,
                        y: 400,
                        width: 100,
                        id: PF + 'btnLimpiar',
                        name: PF + 'btnLimpiar',
                        listeners: 
                        {
                        	click: 
                        	{
                        		fn: function(btn)
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
	NS.FlujoNeto.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	NS.limpiar();
});
