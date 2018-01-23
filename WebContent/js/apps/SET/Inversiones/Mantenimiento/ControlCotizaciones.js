
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Inversiones.ControlCotizaciones');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';	
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.nombreEmpresa = apps.SET.NOM_EMPRESA;
	NS.iIdUsuario = apps.SET.iUserId ;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true					
	});
	
	NS.storeCmbIntitucion = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			bExistente : true
		},
		paramOrder:[ ],
		directFn: CotizacionesAction.obtenerInstitucion,
		idProperty: 'idDiv', 
		fields: [
			 {name: 'idDiv'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen instituciones');
					return;
				}
			}
		}
	}); 
	
	NS.storeCmbTipoValor = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			bExistente : true
		},
		paramOrder:[ ],
		directFn: CotizacionesAction.obtenerTipoValor,
		idProperty: 'idDiv', 
		fields: [
			 {name: 'idDiv'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen Tipos Valor');
					return;
				}
			}
		}
	}); 
	
	NS.storeGridContratos = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			idInstitucion: 0
		},
		root: '',
		paramOrder:['idInstitucion'],
		directFn: CotizacionesAction.obtenerContratos,
		fields: [
			{name: 'nomEmpresa'},
			{name: 'contrato'},
			{name: 'mensaje'}
		],
		listeners: {
			load: function(s, records){
				//mascara.hide();
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información  SET','No existen datos con los parametros de búsqueda');
				}
				if (records[0].get('mensaje') != null && records[0].get('mensaje') != '')
					Ext.Msg.alert('Información SET', records[0].get('mensaje'));
			}
		}
	});
	
	NS.cmbIntitucion = new Ext.form.ComboBox({
		store: NS.storeCmbIntitucion
		,name: PF+'cmbIntitucion'
		,id: PF+'cmbIntitucion'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 160
        ,y: 10
        ,width: 350
		,valueField:'idDiv'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Selecciona una institución'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtInstitucion',NS.cmbIntitucion.getId());
					NS.storeGridContratos.baseParams.idInstitucion = parseInt(NS.cmbIntitucion.getValue());
					NS.storeGridContratos.load();
					NS.gridContratos.getView().refresh();
				}
			}
		}
	});
	
	NS.cmbTipoValor = new Ext.form.ComboBox({
		store: NS.storeCmbTipoValor
		,name: PF+'cmbTipoValor'
		,id: PF+'cmbTipoValor'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 160
        ,y: 110
        ,width: 250
		,valueField:'idDiv'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Selecciona un Tipo Valor'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtTipoValor',NS.cmbTipoValor.getId());
					NS.llenaDivisa();
				}
			}
		}
	});
	
	NS.llenaDivisa = function(){
		CotizacionesAction.consultarDivisaTV(NS.cmbTipoValor.getValue(),function(result, e){
			if(result != null && result != '')
			{
				Ext.getCmp(PF + 'txtDivisa').setValue(result);
			}	
    	});
	}
	
	
	NS.gridContratos = new Ext.grid.EditorGridPanel({
        store : NS.storeGridContratos,
        id: 'gridContratos',
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 140,
                value:true,
                sortable: true
            },
            columns: [
                //NS.sm,
                {
					header :'Empresa',
					width :380,
					dataIndex :'nomEmpresa'
				},{
					header :'Contrato',
					width :170,
					dataIndex :'contrato'
				}
			]
        }),
        sm: NS.sm,
        columnLines: true,
        x:20,
        y:180,
        width:550,
        height:170,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
        			
        		}
        	}
        }
    });
	
	
	
	NS.storeCmbIntitucion.load();
	NS.storeCmbTipoValor.load();
	
	NS.contenedorCotizaciones = new Ext.form.FormPanel({
	    title: 'Control de Cotizaciones',
	    width: 900,
	    height: 388,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                x: 0,
	                y: 0,
	                layout: 'absolute',
	                width: 950,
	                height: 100,
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Fecha:',
	                        x: 20,
	                        y: 10
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        invalidText: 'No es un formato de fecha valido',
	                        id: PF + 'txtFechaIni',
	                        name: PF + 'txtFechaIni',
	                        value: NS.fecHoy,
	                        x: 80,
	                        y: 10,
	                        width: 110
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Hora:',
	                        x: 320,
	                        y: 10
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'H:i',
	                        id: PF + 'txtHora',
	                        name: PF + 'txtHora',
	                        value: new Date(),
	                        readOnly: true,
	                        x: 380,
	                        y: 10,
	                        width: 50
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 550,
	                        y: 50,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
				           				NS.strParametros();
									}
								}
							}
	                    }
	                ]
	            },
	            {
	                xtype: 'fieldset',
	                title: '',
	                x: 0,
	                y: 110,
	                layout: 'absolute',
	                width: 950,
	                height: 400,
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Institución:',
	                        x: 20,
	                        y: 10
	                    },
	                    {
                            xtype: 'numberfield',
                            x: 100,
                            y: 10,
                            width: 50,
                            name: PF+'txtInstitucion',
                            id: PF+'txtInstitucion',
                            listeners : {
                            	change : {
                            		fn: function(caja, valor){
                        				Ext.getCmp(PF + 'cmbIntitucion').setValue(Ext.getCmp(PF+'txtInstitucion').getValue());
                        				NS.storeGridContratos.baseParams.idInstitucion = NS.cmbIntitucion.getValue();
                    					NS.storeGridContratos.load();
                    					NS.gridContratos.getView().refresh();
                            		}
                            	}
                            }
                        },
                        NS.cmbIntitucion,
                        {
	                        xtype: 'label',
	                        text: 'Fecha:',
	                        x: 20,
	                        y: 40
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        invalidText: 'No es un formato de fecha valido',
	                        id: PF + 'txtFecha2',
	                        name: PF + 'txtFecha2',
	                        readOnly: true,
	                        value: NS.fecHoy,
	                        x: 100,
	                        y: 40,
	                        width: 110
	                    },
                        {
	                        xtype: 'label',
	                        text: 'Hora:',
	                        x: 20,
	                        y: 70
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'H:i',
	                        id: PF + 'txtHora2',
	                        name: PF + 'txtHora2',
	                        value: new Date(),
	                        readOnly: true,
	                        x: 100,
	                        y: 70,
	                        width: 50
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Tipo Valor:',
	                        x: 20,
	                        y: 110
	                    },
	                    {
                            xtype: 'textfield',
                            x: 100,
                            y: 110,
                            width: 50,
                            name: PF+'txtTipoValor',
                            id: PF+'txtTipoValor',
                            listeners : {
                            	change : {
                            		fn: function(caja, valor){
                        				Ext.getCmp(PF + 'cmbTipoValor').setValue(Ext.getCmp(PF+'txtTipoValor').getValue());
                        				NS.llenaDivisa();
                            		}
                            	}
                            }
                        },
                        NS.cmbTipoValor,
                        {
	                        xtype: 'label',
	                        text: 'Divisa:',
	                        x: 20,
	                        y: 140
	                    },
	                    {
	                        xtype: 'textfield',
	                        id: PF + 'txtDivisa',
	                        name: PF + 'txtDivisa',
	                        readOnly: true,
	                        x: 100,
	                        y: 140,
	                        width: 50
	                    },
	                    NS.gridContratos,
	                    {
	    	                xtype: 'fieldset',
	    	                title: '',
	    	                x: 600,
	    	                y: 0,
	    	                layout: 'absolute',
	    	                width: 320,
	    	                height: 300,
	    	                items: [
	    	                    {
	    	                        xtype: 'label',
	    	                        text: 'Plazo',
	    	                        style: "font-weight:bold",
	    	                        x: 70,
	    	                        y: 10
	    	                    },
	    	                    {
	    	                        xtype: 'label',
	    	                        text: 'Tasa',
	    	                        style: "font-weight:bold",
	    	                        x: 170,
	    	                        y: 10
	    	                    },
	    	                    {
	    	                        xtype: 'label',
	    	                        style: "font-weight:bold",
	    	                        text: '1',
	    	                        x: 80,
	    	                        y: 40
	    	                    },
	    	                    {
	    	                        xtype: 'textfield',
	    	                        id: PF + 'txtTasa1',
	    	                        name: PF + 'txtTasa1',
	    	                        x: 160,
	    	                        y: 38,
	    	                        width: 50
	    	                    },
	    	                    {
	    	                        xtype: 'label',
	    	                        style: "font-weight:bold",
	    	                        text: '7',
	    	                        x: 80,
	    	                        y: 70
	    	                    },
	    	                    {
	    	                        xtype: 'textfield',
	    	                        id: PF + 'txtTasa7',
	    	                        name: PF + 'txtTasa7',
	    	                        x: 160,
	    	                        y: 68,
	    	                        width: 50
	    	                    },
	    	                    {
	    	                        xtype: 'label',
	    	                        style: "font-weight:bold",
	    	                        text: '14',
	    	                        x: 80,
	    	                        y: 100
	    	                    },
	    	                    {
	    	                        xtype: 'textfield',
	    	                        id: PF + 'txtTasa14',
	    	                        name: PF + 'txtTasa14',
	    	                        x: 160,
	    	                        y: 98,
	    	                        width: 50
	    	                    },
	    	                    {
	    	                        xtype: 'label',
	    	                        style: "font-weight:bold",
	    	                        text: '21',
	    	                        x: 80,
	    	                        y: 130
	    	                    },
	    	                    {
	    	                        xtype: 'textfield',
	    	                        id: PF + 'txtTasa21',
	    	                        name: PF + 'txtTasa21',
	    	                        x: 160,
	    	                        y: 128,
	    	                        width: 50
	    	                    },
	    	                    {
	    	                        xtype: 'label',
	    	                        style: "font-weight:bold",
	    	                        text: '28',
	    	                        x: 80,
	    	                        y: 160
	    	                    },
	    	                    {
	    	                        xtype: 'textfield',
	    	                        id: PF + 'txtTasa28',
	    	                        name: PF + 'txtTasa28',
	    	                        x: 160,
	    	                        y: 158,
	    	                        width: 50
	    	                    },
	    	                    {
	    	                    	xtype: 'checkbox',
	                                x: 30,
	                                y: 200,
	                                boxLabel: '',
	                                id: PF+'checkOtraTasa',
	                                name:PF+'checkOtraTasa',
	                                listeners: {
	                                	check:{
	                                		fn: function(check){
	                                			
	                                		}
	                                	}
	                                }
	                            },
	    	                    {
	    	                        xtype: 'textfield',
	    	                        id: PF + 'txtOtroPlazo',
	    	                        name: PF + 'txtOtroPlazo',
	    	                        x: 60,
	    	                        y: 200,
	    	                        width: 50
	    	                    },
	    	                    {
	    	                        xtype: 'textfield',
	    	                        id: PF + 'txtOtraTasa',
	    	                        name: PF + 'txtOtraTasa',
	    	                        x: 160,
	    	                        y: 200,
	    	                        width: 50
	    	                    }
	    	                    
	    	                ]
	    	            },{
	                        xtype: 'button',
	                        text: 'Ejecutar',
	                        x: 750,
	                        y: 320,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										NS.ejecutar();
									}
								}
							}
	                    },{
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 840,
	                        y: 320,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										NS.storeGridContratos.removeAll();
										NS.gridContratos.store.removeAll();
										NS.gridContratos.getView().refresh();
										NS.contenedorCotizaciones.getForm().reset();
									}
								}
							}
	                    }
	                ]
	            }
	        ]
	});
	
	NS.strParametros = function(){

		strParams = '?nomReporte=ReporteCotizaciones';
		strParams += '&'+'noEmpresa='+NS.noEmpresa;
		strParams += '&'+'idUsuario='+NS.iIdUsuario;
		strParams += '&'+'EMPRESA='+NS.nombreEmpresa;
		strParams += '&'+'fecha='+Ext.getCmp(PF + 'txtFechaIni').getValue().format('d/m/Y');
		window.open("/SET/jsp/bfrmwrk/gnrators/RepInversiones.jsp"+strParams);
	}
	
	NS.ejecutar = function (){
		var matTasas = new Array();
		var i = 0;
		
		if (NS.cmbTipoValor.getValue() == '' || NS.cmbTipoValor.getValue() == 0){
			Ext.Msg.alert('Información SET','Debe seleccionar el Tipo Valor');
			return;
		}
		
		if (NS.cmbIntitucion.getValue() == ''){
			Ext.Msg.alert('Información SET','Debe seleccionar una Institución Financiera');
			return;
		}
		
		if (Ext.getCmp(PF + 'txtTasa1').getValue() != ''){
			var regPlazoTasa = {};
			regPlazoTasa.plazo = 1;
			regPlazoTasa.tasa = Ext.getCmp(PF + 'txtTasa1').getValue();
			matTasas[i] = regPlazoTasa;
			i++;
		}
		if (Ext.getCmp(PF + 'txtTasa7').getValue() != ''){
			var regPlazoTasa = {};
			regPlazoTasa.plazo = 7;
			regPlazoTasa.tasa = Ext.getCmp(PF + 'txtTasa7').getValue();
			matTasas[i] = regPlazoTasa;
			i++;
		}
		if (Ext.getCmp(PF + 'txtTasa14').getValue() != ''){
			var regPlazoTasa = {};
			regPlazoTasa.plazo = 14;
			regPlazoTasa.tasa = Ext.getCmp(PF + 'txtTasa14').getValue();
			matTasas[i] = regPlazoTasa;
			i++;
		}
		if (Ext.getCmp(PF + 'txtTasa21').getValue() != ''){
			var regPlazoTasa = {};
			regPlazoTasa.plazo = 21;
			regPlazoTasa.tasa = Ext.getCmp(PF + 'txtTasa21').getValue();
			matTasas[i] = regPlazoTasa;
			i++;
		}
		if (Ext.getCmp(PF + 'txtTasa28').getValue() != ''){
			var regPlazoTasa = {};
			regPlazoTasa.plazo = 28;
			regPlazoTasa.tasa = Ext.getCmp(PF + 'txtTasa28').getValue();
			matTasas[i] = regPlazoTasa;
			i++;
		}
		if (Ext.getCmp(PF+'checkOtraTasa').getValue() == true){
			var regPlazoTasa = {};
			if (Ext.getCmp(PF + 'txtOtroPlazo').getValue() != '' && Ext.getCmp(PF + 'txtOtraTasa').getValue() != ''){
				regPlazoTasa.plazo = parseInt(Ext.getCmp(PF + 'txtOtroPlazo').getValue());
				regPlazoTasa.tasa = Ext.getCmp(PF + 'txtOtraTasa').getValue();
				matTasas[i] = regPlazoTasa;
				i++;
			}
		}
		
		if (i == 0){
			Ext.Msg.alert('Información SET','Debe registrar al menos una tasa');
			return;
		}
		var jsonTasas = Ext.util.JSON.encode(matTasas);
		CotizacionesAction.registrarCotizaciones (jsonTasas, parseInt(NS.cmbIntitucion.getValue()), NS.cmbTipoValor.getValue(),
												Ext.getCmp(PF + "txtFecha2").getValue().format('d/m/Y') + '-' + Ext.getCmp(PF + 'txtHora2').getValue().format('H:i') + '@' + 
												Ext.getCmp(PF + 'txtDivisa').getValue(), NS.iIdUsuario, NS.noEmpresa, function(result, e){
			
													BFwrk.Util.msgWait('Terminando...', false);
													if(result != null && result != '')
													{
														Ext.Msg.alert('Información SET', result);
													}	
										    	});
		
	}
	
	NS.contenedorCotizaciones.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
