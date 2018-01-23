/*
@author: Jessica Arelly Cruz Cruz
@since: 20/09/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Coinversion.EstadoCuenta');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	NS.GI_USUARIO = apps.SET.iUserId ;	
	NS.fechaHoy = apps.SET.FEC_HOY;
	
	//store empresa
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{mantenimiento : false},
		paramOrder:['mantenimiento'],
		directFn: GlobalAction.llenarComboEmpresasConcentradoras,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length == 1)
				{
					Ext.getCmp(PF+'txtEmpresa').setValue(records[0].get('id'));
	    			Ext.getCmp(PF+'cmbEmpresa').setValue(records[0].get('descripcion'));
					NS.accionarCmbEmpresa(records[0].get('id'));
				}
			}
		}
	}); 
	
	//combo Empresas coinversoras
	NS.storeCmbEmpresa.load();
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa
		,name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 67
        ,y: 22
        ,width: 410
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresa.getId());
					NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	
	NS.storeDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			condicion: 'id_divisa_soin is not null'
		},
		root: '',
		paramOrder:['condicion'],
		directFn: GlobalAction.llenarComboDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo CajaVentana
	NS.storeDivisas.load();
	NS.cmbDivisas = new Ext.form.ComboBox({
		store: NS.storeDivisas
		,name: PF+'cmbDivisas'
		,id: PF+'cmbDivisas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 68
        ,y: 75
        ,width: 170
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione una divisa'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtDivisa',NS.cmbDivisas.getId());
					NS.accionarCmbDivisa(combo.getValue());
				}
			}
		}
	});
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({});
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			empresa: 0,
			divisa: ''
		},
		root: '',
		paramOrder:['empresa', 'divisa'],
		directFn: CoinversionAction.llenarGridEmpresasCoinv, 
		fields: [
			
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg:"Cargando..."});
				NS.sm.selectRange(0,records.length-1);
				if(records.length > 0)
					Ext.getCmp(PF+'cmdImprimir').setDisabled(false);
			}
		}
	}); 
	
	NS.gridConsulta  = new Ext.grid.GridPanel({
		store : NS.storeLlenaGrid,
		id: PF+'gridConsulta',
		name: PF+'gridConsulta',
		frame: true,
		title: 'Empresas',
		//width: 745,
       	height: 210,
		x :10,
		y :160,
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            }	 
		,columns : [ 
		NS.sm,
		{
			header :'N\u00famero', width :70, dataIndex :'noEmpresa'
		},{
			header :'Empresa', width :400, dataIndex :'nomEmpresa'
		}]
		}),
		sm: NS.sm,
		listeners:{
			click:{
				fn:function(grid){
					
				}
			}
		}
	});
	
	NS.accionarCmbEmpresa = function(comboValue)
	{
		NS.storeLlenaGrid.baseParams.empresa = comboValue;
	}
	
	NS.accionarCmbDivisa = function(comboValue)
	{
		NS.storeLlenaGrid.baseParams.divisa = comboValue;
	}
	
	NS.validaDatos = function()
	{
		var validaDatos = true;
		var fechaIni = Ext.getCmp(PF+'txtFechaIni').getValue();
		var fechaFin = Ext.getCmp(PF+'txtFechaFin').getValue();
		
    	if(Ext.getCmp(PF+'txtEmpresa').getValue() === '')
    	{
    		BFwrk.Util.msgShow('Debe seleccionar una Empresa Concentradora','INFO');
	        validaDatos = false;
	        return;
       	}
       	else if(Ext.getCmp(PF+'txtDivisa').getValue() === '')
       	{
	        BFwrk.Util.msgShow('Debe seleccionar una Divisa','INFO');
	        validaDatos = false;
	        return;
        }
		else if(fechaIni === '' || fechaFin === '')
		{
			BFwrk.Util.msgShow('Falta una fecha','WARNING');
			validaDatos = false;
			return;
		}
		else if(fechaFin < fechaIni)
		{
			BFwrk.Util.msgShow('La fecha inicial debe ser menor a la fecha final','WARNING');
			validaDatos = false;
			return;
		}
		else if(fechaFin > BFwrk.Util.changeStringToDate(NS.fechaHoy))
		{
			BFwrk.Util.msgShow('La fecha final debe ser menor a hoy','WARNING');
			validaDatos = false;
			return;
		}
	    return validaDatos;
	}
	
	NS.contenedorEdoCta = new Ext.FormPanel({
	    title: 'Estado de Cuenta',
	    width: 562,
	    height: 470,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                width: 550,
	                height: 430,
	                layout: 'absolute',
	                x: 10,
	                y: 0,
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: 'B\u00fasqueda',
	                        width: 510,
	                        height: 148,
	                        layout: 'absolute',
	                        x: 10,
	                        y: 0,
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Empresa Controladora:',
	                                x: 0,
	                                y: 5
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtEmpresa',
	                                name: PF+'txtEmpresa',
	                                x: 0,
	                                y: 22,
	                                width: 58,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
			                        			NS.accionarCmbEmpresa(comboValue);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbEmpresa,
	                            
	                            {
	                                xtype: 'label',
	                                text: 'Divisa:',
	                                x: 1,
	                                y: 53
	                            },
	                            {
	                                xtype: 'uppertextfield',
	                                id: PF+'txtDivisa',
	                                name: PF+'txtDivisa',
	                                x: 0,
	                                y: 75,
	                                width: 57,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa',NS.cmbDivisas.getId());
			                        			NS.accionarCmbDivisa(comboValue);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbDivisas,
	                            
	                            {
	                                xtype: 'label',
	                                text: 'Periodo:',
	                                x: 248,
	                                y: 53
	                            },
	                            {
	                                xtype: 'datefield',
	                                format: 'd/m/Y',
	                                id: PF+'txtFechaIni',
	                                name: PF+'txtFechaIni',
	                                x: 248,
	                                y: 75,
	                                width: 110
	                            },
	                            {
	                                xtype: 'datefield',
	                                format: 'd/m/Y',
	                                id: PF+'txtFechaFin',
	                                name: PF+'txtFechaFin',
	                                x: 367,
	                                y: 75,
	                                width: 110
	                            }
	                        ]
	                    },
	                    NS.gridConsulta,
	                    {
	                        xtype: 'button',
	                        text: 'Buscar',
	                        x: 368,
	                        y: 379,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										if(NS.validaDatos())
											NS.storeLlenaGrid.load();
									}
								}
							}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        id: PF+'cmdImprimir',
                            name: PF+'cmdImprimir',
                            disabled: true,
	                        x: 449,
	                        y: 379,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
										var fechaIni = Ext.getCmp(PF+'txtFechaIni').getValue();
										var fechaFin = Ext.getCmp(PF+'txtFechaFin').getValue();
										var divisa = Ext.getCmp(PF+'txtDivisa').getValue();
										var empresa = Ext.getCmp(PF+'txtEmpresa').getValue();
		
										if(regSelec.length <= 0)
										{
											BFwrk.Util.msgShow('Es necesario seleccionar alg\u00fan registro', 'INFO');
											return;
										}
										
										if(fechaIni === '' || fechaFin === '')
										{
											BFwrk.Util.msgShow('Debe seleccionar un periodo','INFO');
											return;
										}
										
										var registrosGrid=NS.gridConsulta.getSelectionModel().getSelections();
										var matrizGrid = new Array ();
										
										var datosGrid = '';
										
										for(var i = 0; i < registrosGrid.length; i = i + 1)
										{
											matrizGrid = new Array ();
											var regGrid = {};
											regGrid.noEmpresa = registrosGrid[i].get('noEmpresa');
											regGrid.nomEmpresa = registrosGrid[i].get('nomEmpresa');
											matrizGrid[0] = regGrid; 
											
											datosGrid +=  Ext.util.JSON.encode(matrizGrid);
										}
										var jsonStringR = Ext.util.JSON.encode(matrizGrid);	
										
										strParams = '?nomReporte=ReporteEstadoCuenta';
										strParams += '&'+'nomParam1=noEmpresaConcentradora';
										strParams += '&'+'valParam1='+empresa;
										strParams += '&'+'nomParam2=nombreEmpresaConcentradora';
										strParams += '&'+'valParam2='+$('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val();
										strParams += '&'+'nomParam3=divisa';
										strParams += '&'+'valParam3='+divisa;
										strParams += '&'+'nomParam4=fechaIni';
										strParams += '&'+'valParam4='+ BFwrk.Util.changeDateToString(''+fechaIni);
										strParams += '&'+'nomParam5=fechaFin';
										strParams += '&'+'valParam5='+ BFwrk.Util.changeDateToString(''+fechaFin);
										strParams += '&'+'nomParam6=usuario';
										strParams += '&'+'valParam6='+NS.GI_USUARIO;
										strParams += '&'+'nomParam7=descDivisa';
										strParams += '&'+'valParam7='+ $('input[id*="'+ NS.cmbDivisas.getId() +'"]').val();
										//strParams += '&'+'nomParam8=datosGrid';
										//strParams += '&'+'valParam8='+ datosGrid; //jsonStringR;
										window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
									}
								}
							}
	                    }
	                ]
	            }
	        ]
	});
	NS.contenedorEdoCta.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
