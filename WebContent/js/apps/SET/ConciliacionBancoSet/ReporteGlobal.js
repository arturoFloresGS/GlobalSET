/*
@author: Jessica Arelly Cruz Cruz
@since: 16/11/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.ConciliacionBancoSet.ReporteGlobal');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';	
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.nombreEmpresa = apps.SET.NOM_EMPRESA;
	NS.iIdUsuario = apps.SET.iUserId ;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:[],
		directFn: GlobalAction.llenarComboEmpresasUsuario,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var recordsEmpresas = NS.storeCmbEmpresa.recordType;	
				var todas = new recordsEmpresas({
			       	id : 0,
			       	descripcion : '----------------------TODAS----------------------'
		       	});
		   		NS.storeCmbEmpresa.insert(0,todas);
			}
		}
	}); 
	
	//combo
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
        ,x: 90
        ,y: 30
        ,width: 300
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: NS.nombreEmpresa
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
	
	NS.storeCmbBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{empresa: ''+NS.noEmpresa},
		paramOrder:['empresa'],
		directFn: ConciliacionBancoSetAction.llenarComboBancosEmpresas,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	NS.storeCmbBanco.load();
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeCmbBanco
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 90
        ,y: 80
        ,width: 140
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtBanco',NS.cmbBanco.getId());
					NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	
	NS.storeCmbChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			banco: 0,
			empresa: NS.noEmpresa,
			opcion: 2},
		paramOrder:['banco','empresa','opcion'],
		directFn: ConciliacionBancoSetAction.llenarCmbChequeras,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeCmbChequera
		,name: PF+'cmbChequera'
		,id: PF+'cmbChequera'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 250
        ,y: 80
        ,width: 140
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					
				}
			}
		}
	});
	
	NS.contenedorReporteGlobal = new Ext.FormPanel({
	    title: 'Reporte Global Conciliaci\u00f3n Bancaria',
	    width: 444,
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
	                width: 430,
	                height: 350,
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Empresa:',
	                        x: 20,
	                        y: 10
	                    },
	                    {
                            xtype: 'textfield',
                            id: PF+'txtEmpresa',
                            name: PF+'txtEmpresa',
                            value: NS.noEmpresa,
                            x: 20,
                            y: 30,
                            width: 60,
                              listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
	                        			NS.accionarCmbEmpresa(comboValor);
	                        		}
	                        	}
	                        }
                        },
	                    NS.cmbEmpresa,
	                   
	                    {
	                        xtype: 'label',
	                        text: 'Banco:',
	                        x: 20,
	                        y: 60
	                    },
	                    {
	                        xtype: 'textfield',
	                        id: PF+'txtBanco',
                            name: PF+'txtBanco',
	                        x: 20,
	                        y: 80,
	                        width: 60,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId());
	                        			NS.accionarCmbBanco(comboValor);
	                        		}
	                        	}
	                        }
	                    },
	                    NS.cmbBanco,
	                    {
	                        xtype: 'label',
	                        text: 'Chequera:',
	                        x: 249,
	                        y: 59
	                    },
	                    NS.cmbChequera,
	                    {
	                        xtype: 'label',
	                        text: 'Fecha:',
	                        x: 20,
	                        y: 110
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        invalidText: 'No es un formato de fecha valido',
	                        id: PF + 'txtFechaIni',
	                        name: PF + 'txtFechaIni',
	                        x: 20,
	                        y: 130,
	                        width: 110,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			if(Ext.getCmp(PF + 'txtFechaFin').getValue() !== '')
	                        			{
		                        			if(Ext.getCmp(PF + 'txtFechaFin').getValue() < caja.getValue())
		                        			{
		                        				BFwrk.Util.msgShow('La fecha final debe ser mayor que la inicial','WARNING');
		                        				caja.reset();
												return;
		                        			}
	                        			}
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        invalidText: 'No es un formato de fecha valido',
	                        id: PF + 'txtFechaFin',
	                        name: PF + 'txtFechaFin',
	                        x: 150,
	                        y: 130,
	                        width: 110,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			if(Ext.getCmp(PF + 'txtFechaIni').getValue() > caja.getValue())
	                        			{
	                        				BFwrk.Util.msgShow('La fecha final debe ser mayor que la inicial','WARNING');
	                        				caja.reset();
											return;
	                        			}
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Estatus',
	                        x: 18,
	                        y: 180,
	                        width: 370,
	                        height: 100,
	                        layout: 'absolute',
	                        items: [
	                        {
	                        	xtype: 'radiogroup',
		                     	name: PF+'estatus',
		                     	id: PF+'estatus',
		                     	columns: 2,
	                     		x: 0,
	                     		y: 10,
						       	items: [
		                            {
		                                xtype: 'radio',
		                                name: PF+'optEstatus',
		                                inputValue: 0,
		                                boxLabel: 'Detectado'
		                            },
		                            {
		                                xtype: 'radio',
		                                name: PF+'optEstatus',
		                                inputValue: 1,
		                                boxLabel: 'Pendiente',
		                                checked: true
		                            },
		                            {
		                                xtype: 'radio',
		                                name: PF+'optEstatus',
		                                inputValue: 2,
		                                boxLabel: 'Resumen de Pendientes'
		                            },
		                            {
		                                xtype: 'radio',
		                                name: PF+'optEstatus',
		                                inputValue: 3,
		                                boxLabel: 'Monitor de Conciliaciones'
		                            }
	                            	]
                            	}
	                        ]
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 228,
	                        y: 292,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										if(NS.noEmpresa === 0)
											NS.descEmpresa = 'GLOBAL';
										else
											NS.descEmpresa = $('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val();
									    
									    if(Ext.getCmp(PF + 'txtBanco').getValue() === '')
									    {
									    	NS.banco1 = 0;
									    	NS.banco2 = 30000;
									    }
									    else
									    {
									    	NS.banco1 = Ext.getCmp(PF + 'txtBanco').getValue();
									    	NS.banco2 = NS.banco1;
									    }
									    
									    if(Ext.getCmp(PF + 'cmbChequera').getValue() === '')
									    	NS.idChequera = '%25';
									    else
									    	NS.idChequera = Ext.getCmp(PF + 'cmbChequera').getValue();
									    
									    if(Ext.getCmp(PF+'txtFechaIni').getValue() !== '')
				           					NS.dFechaIni = BFwrk.Util.changeDateToString(''+Ext.getCmp(PF+'txtFechaIni').getValue());
				           				else
				           					NS.dFechaIni = '01/01/1980';
				           					
									    if(Ext.getCmp(PF+'txtFechaFin').getValue() !== '')
				           					NS.dFechaFin = BFwrk.Util.changeDateToString(''+Ext.getCmp(PF+'txtFechaFin').getValue());
				           				else
				           					NS.dFechaFin = BFwrk.Util.sumDate(BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY),'anio',-1);
				           					
				           				NS.buscarReporte();
									}
								}
							}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 317,
	                        y: 291,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										NS.contenedorReporteGlobal.getForm().reset();
									}
								}
							}
	                    }
	                ]
	            }
	        ]
	});
	
	NS.accionarCmbEmpresa = function(comboValor)
	{
		Ext.getCmp(PF+'cmbBanco').reset();
		Ext.getCmp(PF+'txtBanco').reset();
		Ext.getCmp(PF+'cmbChequera').reset();
		NS.storeCmbBanco.baseParams.empresa = ''+comboValor;
		NS.storeCmbBanco.load();
		NS.storeCmbChequera.baseParams.empresa = comboValor;
		NS.noEmpresa = comboValor;
	}
	
	NS.accionarCmbBanco = function(comboValor)
	{
		Ext.getCmp(PF+'cmbChequera').reset();
		NS.storeCmbChequera.baseParams.banco = comboValor;
		NS.storeCmbChequera.load();
	}
	
	NS.opcionSeleccionada = function()
	{
		var value = Ext.getCmp(PF+'estatus').getValue();
	    var valor = value.getGroupValue();
	    return valor;
	}
	
	NS.escribeSubtitulo = function()
	{
		if(Ext.getCmp(PF+'txtFechaFin').getValue() !== '')
			NS.subtitulo = 'CONCILIACION BANCO SET DEL ' + NS.dFechaIni + ' AL ' + NS.dFechaFin;
         else
         	NS.subtitulo = 'CONCILIACION BANCO SET DESDE EL  ' + NS.dFechaIni;
	}
	
	NS.strParametros = function(nombreReporte){
		strParams = '?nomReporte='+nombreReporte;
		strParams += '&'+'nomParam1=noEmpresa';
		strParams += '&'+'valParam1='+NS.noEmpresa;
		strParams += '&'+'nomParam2=bancoIni';
		strParams += '&'+'valParam2='+NS.banco1;
		strParams += '&'+'nomParam3=bancoFin';
		strParams += '&'+'valParam3='+NS.banco2;
		strParams += '&'+'nomParam4=chequera';
		strParams += '&'+'valParam4='+NS.idChequera;
		strParams += '&'+'nomParam5=fechaIni';
		strParams += '&'+'valParam5='+NS.dFechaIni;
		strParams += '&'+'nomParam6=fechaFin';
		strParams += '&'+'valParam6='+NS.dFechaFin;
		strParams += '&'+'nomParam7=idUsuario';
		strParams += '&'+'valParam7='+NS.iIdUsuario;
		strParams += '&'+'nomParam8=estatus';
		strParams += '&'+'valParam8='+NS.estatus;
		strParams += '&'+'nomParam9=descEmpresa';
		strParams += '&'+'valParam9='+NS.descEmpresa;
		strParams += '&'+'nomParam10=subtitulo';
		strParams += '&'+'valParam10='+NS.subtitulo;
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
	}
	
	NS.buscarReporte = function(){
		NS.escribeSubtitulo();
		//detectados
		if(NS.opcionSeleccionada() == 0)
		{
			NS.estatus = 'DETECTADO';
			NS.strParametros('ReporteGlobalDetectadosyPendientes');
		}
		//pendientes
		if(NS.opcionSeleccionada() == 1)
		{
			NS.estatus = 'PENDIENTE';
			NS.strParametros('ReporteGlobalDetectadosyPendientes');
		}
		//resumen de pendientes
		if(NS.opcionSeleccionada() == 2)
		{
			NS.estatus = 'RESUMEN PENDIENTE POR CONCILIAR';
			NS.strParametros('ReporteGlobalPorConciliar');
		}
		//monitor de conciliaciones
		if(NS.opcionSeleccionada() == 3)
		{
			NS.estatus = 'MONITOR DE CONCILIACIONES';
			NS.strParametros('ReporteGlobalMonitor');
		}
	}
	
	NS.contenedorReporteGlobal.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
