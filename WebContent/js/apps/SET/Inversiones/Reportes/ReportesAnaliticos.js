
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Inversiones.ReportesInversiones');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';	
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.nombreEmpresa = apps.SET.NOM_EMPRESA;
	NS.iIdUsuario = apps.SET.iUserId ;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var fechaHoyStr = '' + apps.SET.FEC_HOY;
	
	NS.anioActual = parseInt(fechaHoyStr.substring(6,10));

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
	
	NS.storeCmbInstitucion = new Ext.data.DirectStore({
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
	
	NS.storeCmbDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			bExistente : true
		},
		paramOrder:[ ],
		directFn: InversionesRepAction.obtenerDivisa,
		idProperty: 'idDiv', 
		fields: [
			 {name: 'idDiv'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen divisas');
					return;
				}
			}
		}
	});
	
	NS.datosCmbTipoReporte = [
		                   		['1', 'Inversiones'],
		                   		['2', 'Comparativo de Tasas'],
		                   		['3', 'Gráfica de Inversiones']
		                   	 ];
	                   	 
   	NS.storeTipoReporte = new Ext.data.SimpleStore({
	  		idProperty: 'id',
	  		fields: [
	  					{name: 'id'},
	  					{name: 'descripcion'}
	  				]
  	});
  	NS.storeTipoReporte.loadData(NS.datosCmbTipoReporte);
  	
  	NS.datosCmbMes = [
                   		['1', 'Enero'],
                   		['2', 'Febrero'],
                   		['3', 'Marzo'],
                   		['4', 'Abril'],
                   		['5', 'Mayo'],
                   		['6', 'Junio'],
                   		['7', 'Julio'],
                   		['8', 'Agosto'],
                   		['9', 'Septiembre'],
                   		['10', 'Octubre'],
                   		['11', 'Noviembre'],
                   		['12', 'Diciembre']
                   	 ];
	                   	 
 	NS.storeMes = new Ext.data.SimpleStore({
	  		idProperty: 'id',
	  		fields: [
	  					{name: 'id'},
	  					{name: 'descripcion'}
	  				]
	});
	NS.storeMes.loadData(NS.datosCmbMes);
	
	//combo
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa
		,name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 160
        ,y: 10
        ,width: 350
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
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
	
	NS.cmbInstitucion = new Ext.form.ComboBox({
		store: NS.storeCmbInstitucion
		,name: PF+'cmbInstitucion'
		,id: PF+'cmbInstitucion'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 160
        ,y: 40
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
					BFwrk.Util.updateComboToTextField(PF+'txtInstitucion',NS.cmbInstitucion.getId());
				}
			}
		}
	});
	
	NS.cmbTipoReporte = new Ext.form.ComboBox({
		store: NS.storeTipoReporte
		,name: PF+'cmbTipoReporte'
		,id: PF+'cmbTipoReporte'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 80
        ,y: 70
        ,width: 200
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Selecciona un Tipo de reporte'
		,triggerAction: 'all'
		,value: ''
	});
	
	NS.cmbMes = new Ext.form.ComboBox({
		store: NS.storeMes
		,name: PF+'cmbMes'
		,id: PF+'cmbMes'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 80
        ,y: 100
        ,width: 100
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Mes'
		,triggerAction: 'all'
		,value: ''
	});
	
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeCmbDivisa
		,name: PF+'cmbDivisa'
		,id: PF+'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 160
        ,y: 130
        ,width: 200
		,valueField:'idDiv'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Selecciona una divisa'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtDivisa',NS.cmbDivisa.getId());
				}
			}
		}
	});
	
	NS.storeCmbEmpresa.load();
	NS.storeCmbInstitucion.load();
	NS.storeCmbDivisa.load();
	
	NS.contenedorReporte = new Ext.form.FormPanel({
	    title: 'Reportes de Inversiones',
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
	                height: 220,
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
                            value: '',
                            x: 80,
                            y: 10,
                            width: 60,
                              listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			NS.cmbEmpresa.setValue(valor);
	                        			//var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
	                        			//NS.accionarCmbEmpresa(comboValor);
	                        		}
	                        	}
	                        }
                        },
	                    NS.cmbEmpresa,
	                    {
	                        xtype: 'label',
	                        text: 'Institución:',
	                        x: 20,
	                        y: 40
	                    },
	                    {
                            xtype: 'textfield',
                            id: PF+'txtInstitucion',
                            name: PF+'txtInstitucion',
                            value: '',
                            x: 80,
                            y: 40,
                            width: 60,
                              listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			NS.cmbEmpresa.setValue(valor);
	                        			//var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
	                        			//NS.accionarCmbEmpresa(comboValor);
	                        		}
	                        	}
	                        }
                        },
	                    NS.cmbInstitucion,
	                    {
	                        xtype: 'label',
	                        text: 'Reporte:',
	                        x: 20,
	                        y: 70
	                    },
	                    NS.cmbTipoReporte,
	                    {
	                        xtype: 'label',
	                        text: 'Mes:',
	                        x: 20,
	                        y: 100
	                    },
	                    NS.cmbMes,
	                    {
	                        xtype: 'label',
	                        text: 'Año:',
	                        x: 250,
	                        y: 102
	                    },
	                    {
                            xtype: 'numberfield',
                            id: PF+'txtAnio',
                            name: PF+'txtAnio',
                            value: NS.anioActual,
                            minValue: 2000,
                            maxValue: NS.anioActual,
                            x: 280,
                            y: 100,
                            width: 60,
                              listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			if (valor > NS.anioActual){
	                        				Ext.Msg.alert('SET', 'El año no debe ser mayor a ' + NS.anioActual);
	                        				return false;
	                        			}else if(valor < 2000){
	                        				Ext.Msg.alert('SET', 'El año no debe ser menor a 2000');
	                        			}
	                        			
	                        		}
	                        	}
	                        }
                        },
	                    {
	                        xtype: 'label',
	                        text: 'Divisa:',
	                        x: 20,
	                        y: 130
	                    },
	                    {
                            xtype: 'textfield',
                            id: PF+'txtDivisa',
                            name: PF+'txtDivisa',
                            value: '',
                            x: 80,
                            y: 130,
                            width: 60,
                              listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			NS.cmbDivisa.setValue(valor);
	                        			//var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
	                        			//NS.accionarCmbEmpresa(comboValor);
	                        		}
	                        	}
	                        }
                        },
	                    NS.cmbDivisa,
	                    {
	                        xtype: 'button',
	                        text: 'Generar',
	                        x: 650,
	                        y: 170,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										
										
										if(NS.noEmpresa === 0)
											NS.descEmpresa = 'GLOBAL';
										else
											NS.descEmpresa = $('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val();
									    
				           				NS.strParametros();
									}
								}
							}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 740,
	                        y: 170,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										NS.contenedorReporte.getForm().reset();
									}
								}
							}
	                    }
	                ]
	            }
	        ]
	});
	
	NS.strParametros = function(){
		
		
		if (NS.cmbEmpresa.getValue() == 0)
			NS.generarExcel();
		else{
			
		
			
			strParams = '?nomReporte=ReporteBarridosFondeosFec';
			strParams += '&'+'noEmpresa='+Ext.getCmp(PF+'txtEmpresa').getValue();
			strParams += '&'+'idUsuario='+NS.iIdUsuario;
			strParams += '&'+'EMPRESA='+NS.cmbEmpresa.getRawValue();
			strParams += '&'+'fecha='+fechaHoyStr;
			window.open("/SET/jsp/Reportes.jsp"+strParams);
		}
	}
	
	NS.generarExcel = function(){	
		parametros='?nomReporte=excelBarridosFondeos';
		parametros+='&nomParam1=noEmpresa';
		parametros+='&valParam1=' + Ext.getCmp(PF+'txtEmpresa').getValue();
		parametros+='&nomParam2=idUsuario';
		parametros+='&valParam2=' + NS.iIdUsuario;
		parametros+='&nomParam3=fecha';
		parametros+='&valParam3=' + fechaHoyStr;
		parametros+='&nomParam4=EMPRESA';
		parametros+='&valParam4=' + +NS.cmbEmpresa.getRawValue();
		
		window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
	 
	}
	
	NS.contenedorReporte.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
