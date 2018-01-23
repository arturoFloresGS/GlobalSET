/*
@author: Jessica Arelly Cruz Cruz
@since: 27/09/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Coinversion.ReporteSaldosCoinversion');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	NS.GI_USUARIO = apps.SET.iUserId ;	
	NS.fechaHoy = apps.SET.FEC_HOY;
	
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
					//NS.accionarCmbEmpresa(records[0].get('id'));
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
        ,x: 150
        ,y: 20
        ,width: 210
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
					//NS.accionarCmbEmpresa(combo.getValue());
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
		else if(fechaFin >= BFwrk.Util.changeStringToDate(NS.fechaHoy))
		{
			BFwrk.Util.msgShow('La fecha final no debe ser mayor o igual al dia de hoy','WARNING');
			Ext.getCmp(PF+'txtFechaFin').reset();
			validaDatos = false;
			return;
		}
	    return validaDatos;
	}
	
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
        ,x: 150
        ,y: 77
        ,width: 210
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
					//NS.accionarCmbDivisa(combo.getValue());
				}
			}
		}
	});
	
	NS.contenedorReporteSaldos = new Ext.FormPanel({
	    title: 'Reporte de Saldos de Coinversion',
	    width: 402,
	    height: 283,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                width: 400,
	                height: 250,
	                layout: 'absolute',
	                x: 0,
	                y: 0,
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Empresa:',
	                        x: 25,
	                        y: 27
	                    },
	                    NS.cmbEmpresa,
	                    {
	                        xtype: 'label',
	                        text: 'Divisa:',
	                        x: 25,
	                        y: 84
	                    },
	                    NS.cmbDivisas,
	                    {
	                        xtype: 'textfield',
	                        id: PF+'txtEmpresa',
                            name: PF+'txtEmpresa',
	                        x: 79,
	                        y: 19,
	                        width: 60,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
	                        			//NS.accionarCmbEmpresa(comboValue);
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'uppertextfield',
                            id: PF+'txtDivisa',
                            name: PF+'txtDivisa',
	                        x: 78,
	                        y: 78,
	                        width: 61,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa',NS.cmbDivisas.getId());
	                        			//NS.accionarCmbDivisa(comboValue);
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Desde:',
	                        x: 25,
	                        y: 140
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
                            id: PF+'txtFechaIni',
                            name: PF+'txtFechaIni',
                            value: BFwrk.Util.sumDate(BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY), 'dia', -1),
	                        x: 78,
	                        y: 136,
	                        width: 110
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Hasta:',
	                        x: 200,
	                        y: 140
	                    },
	                    {
	                        xtype: 'datefield',
	                       	format: 'd/m/Y',
                            id: PF+'txtFechaFin',
                            name: PF+'txtFechaFin',
                            value: BFwrk.Util.sumDate(BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY), 'dia', -1),
	                        x: 249,
	                        y: 136,
	                        width: 110
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 99,
	                        y: 196,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										if(NS.validaDatos())
										{
											var fechaIni = Ext.getCmp(PF+'txtFechaIni').getValue();
											var fechaFin = Ext.getCmp(PF+'txtFechaFin').getValue();
											var divisa = Ext.getCmp(PF+'txtDivisa').getValue();
											var empresa = Ext.getCmp(PF+'txtEmpresa').getValue();
											var fecha1 = new  Date(fechaIni);
											var fecha2 = new  Date(fechaFin);
											
											strParams = '?nomReporte=ReporteHistoricoSaldos';
											strParams += '&'+'nomParam1=noEmpresa';
											strParams += '&'+'valParam1='+empresa;
											strParams += '&'+'nomParam2=nombreEmpresa';
											strParams += '&'+'valParam2='+$('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val();
											strParams += '&'+'nomParam3=idDivisa';
											strParams += '&'+'valParam3='+divisa;
											strParams += '&'+'nomParam4=fechaIni';
											strParams += '&'+'valParam4='+ BFwrk.Util.changeDateToString(''+fechaIni);
											strParams += '&'+'nomParam5=fechaFin';
											strParams += '&'+'valParam5='+ BFwrk.Util.changeDateToString(''+fechaFin);
											strParams += '&'+'nomParam6=usuario';
											strParams += '&'+'valParam6='+NS.GI_USUARIO;
											strParams += '&'+'nomParam7=descDivisa';
											strParams += '&'+'valParam7='+ $('input[id*="'+ NS.cmbDivisas.getId() +'"]').val();
											strParams += '&'+'nomParam8=fechaHoy';
											strParams += '&'+'valParam8='+ apps.SET.FEC_HOY;
											strParams += '&'+'nomParam9=fechaInicial';
											strParams += '&'+'valParam9='+ fecha1.getDate() + '/' + BFwrk.Util.getNameMonth(fecha1.getMonth() + 1) + '/' + fecha1.getFullYear();
											strParams += '&'+'nomParam10=fechaFinal';
											strParams += '&'+'valParam10='+fecha2.getDate() + '/' + BFwrk.Util.getNameMonth(fecha2.getMonth() + 1) + '/' + fecha2.getFullYear();
											window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
										}
									}
								}
							}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 199,
	                        y: 197,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										NS.contenedorReporteSaldos.getForm().reset();
									}
								}
							}
	                    }
	                ]
	            }
	        ]
	});
	NS.contenedorReporteSaldos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
