Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.PrestamosInterempresas.EstadoDeCuentaCredito');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	
	NS.limpiar = function()
	{
		NS.EstadoDeCuentaDeCredito.getForm().reset();
		NS.iIdEmpresa = 0;
		NS.sIdDivisa = '';
	};
	
	NS.storeEmpresa = new Ext.data.DirectStore({
	paramsAsHash: false,
	directFn: PrestamosInterempresasAction.llenarCmbArbolEmpresas, 
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
					Ext.Msg.alert('SET','No existen empresas hijas');
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
   	 	x: 170,
        y: 10,
        width: 270,
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
   	 	x: 170,
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
		if(NS.iIdEmpresa === 0)
		{
			BFwrk.Util.msgShow('Debe seleccionar una empresa', 'INFO');
			return false;
		}
		else if(NS.sIdDivisa === '')
		{
			BFwrk.Util.msgShow('Debe seleccionar una divisa', 'INFO');
			return false;
		}
		else if(Ext.getCmp(PF + 'txtFecIni').getValue() === '')
		{
			BFwrk.Util.msgShow('Debe agregar una fecha inicial', 'INFO');
			return false;
		}
		else if(Ext.getCmp(PF + 'txtFecFin').getValue() === '')
		{
			BFwrk.Util.msgShow('Debe agregar una fecha final', 'INFO');
			return false;
		}
		else if(Ext.getCmp(PF + 'txtFecFin').getValue() < Ext.getCmp(PF + 'txtFecIni').getValue())
		{
			BFwrk.Util.msgShow('La fecha inicial debe ser menor a la final', 'INFO');
			return false;
		}
		return true;
	};
	
	NS.reporteEstadoDeCuenta = function()
	{
		var strParams = '';
		
		if(Ext.getCmp(PF + 'optConcentrado').getValue())
			strParams = '?nomReporte=ReporteEstadoDeCuentaDeCredito';
		else
			strParams = '?nomReporte=ReporteEstadoDeCuentaDeCreditoDet';	
		
		strParams += '&'+'nomParam1=iIdEmpresa';
		strParams += '&'+'valParam1=' + NS.iIdEmpresa;
		strParams += '&'+'nomParam2=sIdDivisa';
		strParams += '&'+'valParam2=' + NS.sIdDivisa;
		strParams += '&'+'nomParam3=sFecIni';
		strParams += '&'+'valParam3=' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecIni').getValue());
		strParams += '&'+'nomParam4=sFecFin';
		strParams += '&'+'valParam4=' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecFin').getValue());
		strParams += '&'+'nomParam5=sNomEmpresa';
		strParams += '&'+'valParam5=' + $('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val();
		strParams += '&'+'nomParam6=sDescDivisa';
		strParams += '&'+'valParam6=' +  $('input[id*="'+ NS.cmbDivisa.getId() +'"]').val();
		strParams += '&'+'nomParam7=sPeriodo';
		strParams += '&'+'valParam7=' + 'DEL ' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecIni').getValue()) + ' AL ' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecFin').getValue());
		strParams += '&'+'nomParam8=sConcentrado';
		strParams += '&'+'valParam8=' + Ext.getCmp(PF + 'optConcentrado').getValue();
		strParams += '&'+'nomParam9=sNoEmpresa';
		strParams += '&'+'valParam9=' + NS.iIdEmpresa;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		
	};
	
	NS.EstadoDeCuentaDeCredito = new Ext.form.FormPanel({
    title: 'Estado de Cuenta de Crédito',
    width: 521,
    height: 286,
    padding: 10,
    layout: 'absolute',
    id: PF + 'EstadoDeCuentaDeCredito',
    renderTo: NS.tabContId,
    autoScroll: true,
    frame: true,
        items : [
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 10,
                height: 240,
                layout: 'absolute',
                width: 500,
                id: 'fSetPrincipal',
                items: [
                    {
                        xtype: 'fieldset',
                        title: '',
                        x: 0,
                        y: 0,
                        layout: 'absolute',
                        height: 180,
                        width: 470,
                        id: 'fSetParams',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa:',
                                x: 0,
                                y: 10
                            },
                            {
                                xtype: 'textfield',
                                x: 100,
                                y: 10,
                                width: 60,
                                name: PF + 'txtNoEmpresa',
                                id: PF + 'txtNoEmpresa',
                                listeners:
                                {
                                	change:
                                	{
                                		fn: function(box, value)
                                		{
                                			var valueId = 0; 
                                			if(box.getValue() !== null || box.getValue() !== '')
                                				valueId = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
                                			if(valueId !== null && valueId !== '' && valueId !== undefined)
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
                                x: 0,
                                y: 50,
                                width: 40
                            },
                            {
                                xtype: 'uppertextfield',
                                width: 60,
                                x: 100,
                                y: 50,
                                name: PF + 'txtIdDivisa',
                                id: PF + 'txtIdDivisa',
                                listeners:
                                {
                                	change:
                                	{
                                		fn: function(box, value)
                                		{
                                			var valueId = 0; 
                                			if(box.getValue() !== null || box.getValue() !== '')
                                				valueId = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdDivisa', NS.cmbDivisa.getId());
                                			if(valueId !== null && valueId !== '' && valueId !== undefined)
                                				NS.sIdDivisa = valueId;
                                			else
                                				NS.sIdDivisa = '';
                                		}
                                	}
                                }
                            },
                                NS.cmbDivisa,
                            {
                                xtype: 'label',
                                text: 'Desde:',
                                x: 0,
                                y: 90
                            },
                            {
                                xtype: 'label',
                                text: 'Hasta:',
                                x: 0,
                                y: 130
                            },
                            {
                                xtype: 'datefield',
                                format: 'd/m/Y',
                                x: 100,
                                y: 90,
                                width: 110,
                                name: PF + 'txtFecIni',
                                id: PF + 'txtFecIni',
                                value: apps.SET.FEC_HOY
                            },
                            {
                                xtype: 'datefield',
                                format: 'd/m/Y',
                                x: 100,
                                y: 130,
                                width: 110,
                                name: PF + 'txtFecFin',
                                id: PF + 'txtFecFin',
                                value: apps.SET.FEC_HOY
                            },
                            {
                                xtype: 'label',
                                x: 320,
                                y: 105,
                                text: 'Detallado'
                            },
                            {
                                xtype: 'radio',
                                width: 60,
                                x: 300,
                                y: 100,
                                name: PF + 'optDetallado',
                                id: PF + 'optDetallado',
                                listeners:
                                {
                                	check:
                                	{
                                		fn: function(opt, value)
                                		{
                                			if(value)
                                			{
                                				Ext.getCmp(PF + 'optConcentrado').setValue(false);
                                				Ext.getCmp(PF + 'optDetallado').setValue(true);
                                			}
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'label',
                                x: 320,
                                y: 135,
                                text: 'Concentrado'
                            },
                            {
                                xtype: 'radio',
                                width: 60,
                                x: 300,
                                y: 130,
                                name: PF + 'optConcentrado',
                                id: PF + 'optConcentrado',
                                checked: true,
                                listeners:
                                {
                                	check:
                                	{
                                		fn: function(opt, value)
                                		{
                                			if(value)
                                			{
                                				Ext.getCmp(PF + 'optConcentrado').setValue(true);
                                				Ext.getCmp(PF + 'optDetallado').setValue(false);
                                			}
                                		}
                                	}
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Ejecutar',
                        x: 280,
                        y: 190,
                        height: 22,
                        width: 80,
                        id: 'btnEjecutar',
                        listeners:
                        {
                        	click:
                        	{
                        		fn: function(e)
                        		{
                        			if(NS.validarDatos())
                        		    	NS.reporteEstadoDeCuenta();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 380,
                        y: 190,
                        width: 80,
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
	NS.EstadoDeCuentaDeCredito.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	NS.limpiar();
});