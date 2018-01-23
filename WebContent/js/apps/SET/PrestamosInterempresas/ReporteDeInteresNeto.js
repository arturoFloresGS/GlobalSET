Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.PrestamosInterempresas.ReporteDeInteresesNeto');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	
	NS.limpiar = function()
	{
		NS.ReporteDeInteresesNeto.getForm().reset();
		NS.idEmpresa = 0;
	};
	
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
   	 	x: 170,
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
				 	 BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
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
   	 	x: 100,
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
				 	 
				}
			}
		}
	});
	
	//cmbAnios
	NS.storeAnios = new Ext.data.DirectStore({
	paramsAsHash: false,
	directFn: PrestamosInterempresasAction.llenarCmbAnios, 
	idProperty: 'id', 
	fields: [      
		 {name: 'id'}
	],
	listeners: 
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAnios, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen datos para obtener años');
					return;
				}
			}
		}
	}); 
	
	NS.storeAnios.load();
	
	NS.cmbAnios = new Ext.form.ComboBox({
		store: NS.storeAnios,
		name: PF + 'cmbAnios',
		id: PF + 'cmbAnios',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 100,
        y: 90,
        width: 90,
		valueField:'id',
		displayField:'id',
		autocomplete: true,
		emptyText: 'Seleccione un año',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor) 
				{
					NS.cmbMes.reset();
					NS.storeMes.baseParams.iAnio = parseInt(combo.getValue());
				 	NS.storeMes.load();
				}
			}
		}
	});
	
	//cmbMes
	NS.storeMes = new Ext.data.DirectStore({
	paramsAsHash: false,
	baseParams:
	{
		iAnio:0
	},
	paramOrder:
	[
		'iAnio'
	],
	directFn: PrestamosInterempresasAction.llenarCmbMes, 
	idProperty: 'id', 
	fields: [      
		 {name: 'id'},
		 {name: 'descripcion'}      
	],
	listeners: 
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMes, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen los meses');
					return;
				}
			}
		}
	}); 
	
	
	NS.cmbMes = new Ext.form.ComboBox({
		store: NS.storeMes,
		name: PF + 'cmbMes',
		id: PF + 'cmbMes',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 100,
        y: 130,
        width: 170, 
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un mes',
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
	
	NS.reporteInteresNeto = function()
	{
		var strParams = '';
		strParams = '?nomReporte=ReporteInteresNeto';
		
		strParams += '&'+'nomParam1=noEmpresa';
		strParams += '&'+'valParam1=' + Ext.getCmp(PF + 'cmbEmpresa').getValue();
		strParams += '&'+'nomParam2=idDivisa';
		strParams += '&'+'valParam2=' + Ext.getCmp(PF + 'cmbDivisa').getValue();
		strParams += '&'+'nomParam3=anio';
		strParams += '&'+'valParam3=' + Ext.getCmp(PF + 'cmbAnios').getValue();
		strParams += '&'+'nomParam4=mes';
		strParams += '&'+'valParam4=' + Ext.getCmp(PF + 'cmbMes').getValue();
		strParams += '&'+'nomParam5=tasaPres';
		strParams += '&'+'valParam5=' + '0.0';
		strParams += '&'+'nomParam6=tasaCoin';
		strParams += '&'+'valParam6=' + '0.0';
		strParams += '&'+'nomParam7=nomEmpCoin';
		strParams += '&'+'valParam7=' + $('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val();
		strParams += '&'+'nomParam8=periodo';
		strParams += '&'+'valParam8=' + $('input[id*="'+ NS.cmbMes.getId() +'"]').val() + ' DEL ' + NS.cmbAnios.getValue();
		strParams += '&'+'nomParam9=fecHoy';
		strParams += '&'+'valParam9=' + apps.SET.FEC_HOY;
		strParams += '&'+'nomParam10=descDivisa';
		strParams += '&'+'valParam10=' + $('input[id*="'+ NS.cmbDivisa.getId() +'"]').val();
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		
	};
	
	NS.validarDatos = function()
	{
		if(NS.cmbEmpresa.getValue() <= 0)
		{
			BFwrk.Util.msgShow('Debe seleccionar una empresa','INFO');
			return false;
		}
		else if(NS.cmbDivisa.getValue() === '')
		{
			BFwrk.Util.msgShow('Debe seleccionar una divisa','INFO');
			return false;
		}
		else if(NS.cmbAnios.getValue() <= 0)
		{
			BFwrk.Util.msgShow('Debe seleccionar un año','INFO');
			return false;
		}
		else if(NS.cmbMes.getValue() <= 0)
		{
			BFwrk.Util.msgShow('Debe seleccionar un mes','INFO');
			return false;
		}
		return true;
	};
	
	NS.ReporteDeInteresesNeto = new Ext.form.FormPanel({
    title: 'Reporte de Intereses Netos',
    width: 521,
    height: 287,
    padding: 10,
    layout: 'absolute',
    id: PF + 'ReporteDeInteresesNetos',
    renderTo: NS.tabContId,
    autoScroll: true,
    frame: true,
        items : [
            {
                xtype: 'fieldset',
                title: '',
                height: 240,
                x: 10,
                y: 10,
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
                                text: 'Concentradora:',
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
                                			if(valueId !== null && valueId !== '')
                                				NS.idEmpresa = valueId;
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
                                NS.cmbDivisa,
                            {
                                xtype: 'label',
                                text: 'año:',
                                x: 0,
                                y: 90
                            },
                                NS.cmbAnios,
                            {
                                xtype: 'label',
                                text: 'Mes:',
                                x: 0,
                                y: 130
                            },
                                NS.cmbMes
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Ejecutar',
                        x: 280,
                        y: 190,
                        height: 22,
                        width: 80,
                        id:PF + 'btnEjecutar',
                        listeners:
                        {
                        	click:
                        	{
                        		fn:function(e)
                        		{
                        			if(NS.validarDatos())
                        				NS.reporteInteresNeto();
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
                        listeners:
                        {
                        	click:
                        	{
                        		fn:function(e)
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
	NS.ReporteDeInteresesNeto.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	NS.limpiar();
});