Ext.onReady(function(){

	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.PrestamosInterempresas.ReporteDeSolicitudes');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	
	NS.limpiar = function()
	{
		NS.sIdDivisa = '';
		NS.ReporteDeSolicitudes.getForm().reset();
	};
	
	NS.storeEmpresa = new Ext.data.DirectStore({
	paramsAsHash: false,
	directFn: GlobalAction.llenarComboEmpresasUsuario, 
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
					Ext.Msg.alert('SET','No existen empresas asignadas a este usuario');
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
   	 	x: 60,
        y: 10,
        width: 270,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
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
   	 	x: 130,
        y: 50,
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
				 	 BFwrk.Util.updateComboToTextField(PF + 'txtIdDivisa', NS.cmbDivisa.getId());
				 	 NS.sIdDivisa = combo.getValue();
				}
			}
		}
	});
	
	NS.validarDatos = function()
	{
		if((NS.cmbEmpresa.getValue() <= 0) && (Ext.getCmp(PF + 'optActual').getValue()))
		{
			BFwrk.Util.msgShow("Debe seleccionar una empresa","INFO");
			return false;
		}
		else if(NS.cmbDivisa.getValue() === '' || $('input[id*="'+ NS.cmbDivisa.getId() +'"]').val() === '')
		{
			BFwrk.Util.msgShow("Debe seleccionar una divisa","INFO");
			return false;
		}
		return true;
	};
	
	NS.obtenerReporte = function()
	{
		var strParams = '';
		
		strParams = '?nomReporte=ReporteSolicitudesDeCredito';	
		
		strParams += '&'+'nomParam1=noEmpresa';
		strParams += '&'+'valParam1=' + Ext.getCmp(PF + 'cmbEmpresa').getValue();
		strParams += '&'+'nomParam2=sNomEmpresa';
		
		if(Ext.getCmp(PF + 'optActual').getValue())
			strParams += '&'+'valParam2=' + $('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val();
		else
			strParams += '&'+'valParam2=' + 'REPORTE GLOBAL';			
		strParams += '&'+'nomParam3=idDivisa';
		strParams += '&'+'valParam3=' + NS.sIdDivisa;
		strParams += '&'+'nomParam4=sFecHoy';
		strParams += '&'+'valParam4=' + apps.SET.FEC_HOY;
		strParams += '&'+'nomParam5=optActual';
		strParams += '&'+'valParam5=' + Ext.getCmp(PF + 'optActual').getValue();
		strParams += '&'+'nomParam6=optCredito';
		strParams += '&'+'valParam6=' + Ext.getCmp(PF + 'optCredito').getValue();
		strParams += '&'+'nomParam7=sDescDivisa';
		strParams += '&'+'valParam7=' + $('input[id*="'+ NS.cmbDivisa.getId() +'"]').val();
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
	};
	
	NS.ReporteDeSolicitudes = new Ext.form.FormPanel({
    title: 'Reportes de Crédito',
    width: 422,
    height: 357,
    padding: 10,
    layout: 'absolute',
    id: PF + 'ReporteDeSolicitudes',
    autoScroll: true,
    frame: true,
    renderTo: NS.tabContId,
        items : [
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 10,
                height: 310,
                layout: 'absolute',
                width: 400,
                id: 'fSetPrincipal',
                items: [
                    {
                        xtype: 'fieldset',
                        title: '',
                        x: 0,
                        y: 0,
                        layout: 'absolute',
                        height: 250,
                        width: 370,
                        id: 'fSetParams',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa:',
                                x: 0,
                                y: 10
                            },
                            NS.cmbEmpresa,
                            {
                                xtype: 'label',
                                text: 'Divisa:',
                                x: 0,
                                y: 50,
                                width: 40
                            },
                             NS.cmbDivisa,
                            {
                                xtype: 'uppertextfield',
                                x: 60,
                                y: 50,
                                width: 60,
                                name: PF + 'txtIdDivisa',
                                id: PF + 'txtIdDivisa',
                                listeners:
                                {
                                	change:
                                	{
                                		fn: function(box, value)
                                		{
                                			var sIdDivisa = '';
                                			if(box.getValue() !== null && box.getValue() !== '')
												sIdDivisa = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdDivisa', NS.cmbDivisa.getId());
											if(sIdDivisa !== undefined && sIdDivisa !== null && sIdDivisa !== '')
												NS.sIdDivisa = sIdDivisa;
											else
												NS.sIdDivisa = '';                                			
												
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'fieldset',
                                title: 'Empresa',
                                x: 60,
                                y: 80,
                                height: 70,
                                layout: 'absolute',
                                width: 270,
                                id: 'fSetEmpresa',
                                items: [
                                    {
                                        xtype: 'radio',
                                        boxLabel: 'Actual',
                                        name: 'optActual',
                                        x: 30,
                                        y:0,
                                        id: PF + 'optActual',
                                        name: PF + 'optActual',
                                        checked: true,
                                        listeners:
                                        {
                                        	check:
                                        	{
                                        		fn: function(opt, value)
                                        		{
                                        			if(value)
                                        			{
                                        				this.setValue(true);
                                        				Ext.getCmp(PF + 'optTodas').setValue(false);
                                        			}
                                        		}
                                        	}
                                        }
                                    },
                                    {
                                        xtype: 'radio',
                                        boxLabel: 'Todas',
                                        x: 140,
                                        y: 0,
                                        name: PF + 'optTodas',
                                        id: PF + 'optTodas',
                                        listeners:
                                        {
                                        	check:
                                        	{
                                        		fn: function(opt, value)
                                        		{
                                        			if(value)
                                        			{
                                        				this.setValue(true);
                                        				Ext.getCmp(PF + 'optActual').setValue(false);
                                        			}
                                        		}
                                        	}
                                        }
                                    }
                                ]
                            },
                            {
                                xtype: 'fieldset',
                                title: 'Tipo',
                                x: 60,
                                y: 150,
                                height: 70,
                                layout: 'absolute',
                                width: 270,
                                id: 'fSetTipo',
                                items: [
                                    {
                                        xtype: 'radio',
                                        boxLabel: 'Crédito',
                                        name: 'optCredito',
                                        x: 30,
                                        y: 0,
                                        id: PF + 'optCredito',
                                        name: PF + 'optCredito',
                                        checked: true,
                                        listeners:
                                        {
                                        	check:
                                        	{
                                        		fn: function(opt, value)
                                        		{
                                        			if(value)
                                        			{
                                        				this.setValue(true);
                                        				Ext.getCmp(PF + 'optPagoCredito').setValue(false);
                                        			}
                                        		}
                                        	}
                                        }
                                    },
                                    {
                                        xtype: 'radio',
                                        boxLabel: 'Pago de Crédito',
                                        x: 140,
                                        y: 0,
                                        name: PF + 'optPagoCredito',
                                        id: PF + 'optPagoCredito',
                                        listeners:
                                        {
                                        	check:
                                        	{
                                        		fn: function(opt, value)
                                        		{
                                        			if(value)
                                        			{
                                        				this.setValue(true);
                                        				Ext.getCmp(PF + 'optCredito').setValue(false);
                                        			}
                                        		}
                                        	}
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Ejecutar',
                        x: 160,
                        y: 260,
                        width: 90,
                        id: PF + 'btnEjecutar',
                        listeners:
                        {
                        	click:
                        	{
                        		fn: function(btn)
                        		{
                        			if(NS.validarDatos())
                        				NS.obtenerReporte();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 260,
                        y: 260,
                        width: 90,
                        id: PF + 'btnLimpiar',
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
	NS.ReporteDeSolicitudes.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	NS.limpiar();
});