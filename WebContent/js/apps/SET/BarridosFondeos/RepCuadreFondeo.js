
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.BarridosFondeos.ReporteBarridosFondeos');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';	
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.nombreEmpresa = apps.SET.NOM_EMPRESA;
	NS.iIdUsuario = apps.SET.iUserId ;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			bExistente : true,
			tipoOperacion : 'F'
		},
		paramOrder:['bExistente', 'tipoOperacion'],
		directFn: BarridosFondeosAction.obtenerRaizArbolFA,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen empresas raíz');
					return;
				}
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
		,emptyText: 'Seleccione un árbol'
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
	
	NS.storeCmbEmpresa.load();
	
	NS.contenedorReporte = new Ext.form.FormPanel({
	    title: 'Reporte de Cuadre de Fondeo',
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
	                height: 200,
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Árbol:',
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
	                        text: 'Fecha:',
	                        x: 20,
	                        y: 80
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        invalidText: 'No es un formato de fecha valido',
	                        id: PF + 'txtFechaIni',
	                        name: PF + 'txtFechaIni',
	                        x: 80,
	                        y: 80,
	                        width: 110/*,
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
	                        }*/
	                    },
	                    /*{
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        invalidText: 'No es una fecha válida',
	                        id: PF + 'txtFechaFin',
	                        name: PF + 'txtFechaFin',
	                        x: 230,
	                        y: 80,
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
	                    },*/
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 550,
	                        y: 120,
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
	                        text: 'Excel',
	                        x: 630,
	                        y: 120,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										if(NS.noEmpresa === 0)
											NS.descEmpresa = 'GLOBAL';
										else
											NS.descEmpresa = $('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val();
									    
									    
				           				NS.generarExcel();
									}
								}
							}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 710,
	                        y: 120,
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
		/*if (NS.cmbEmpresa.getValue() == 0)
			NS.generarExcel();
		else{*/
			strParams = '?nomReporte=ReporteCuadreFondeo';
			strParams += '&'+'noEmpresa='+Ext.getCmp(PF+'txtEmpresa').getValue();
			strParams += '&'+'idUsuario='+NS.iIdUsuario;
			strParams += '&'+'EMPRESA='+NS.cmbEmpresa.getRawValue();
			strParams += '&'+'fecha='+Ext.getCmp(PF + 'txtFechaIni').getValue().format('d/m/Y');
			window.open("/SET/jsp/Reportes.jsp"+strParams);
		//}
	}
	
	NS.generarExcel = function(){	
		parametros='?nomReporte=excelCuadreFondeo';
		parametros+='&nomParam1=noEmpresa';
		parametros+='&valParam1=' + Ext.getCmp(PF+'txtEmpresa').getValue();
		parametros+='&nomParam2=idUsuario';
		parametros+='&valParam2=' + NS.iIdUsuario;
		parametros+='&nomParam3=fecha';
		parametros+='&valParam3=' + Ext.getCmp(PF + 'txtFechaIni').getValue().format('d/m/Y');
		parametros+='&nomParam4=EMPRESA';
		parametros+='&valParam4=' + +NS.cmbEmpresa.getRawValue();
		
		window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
	 
	}
	
	NS.contenedorReporte.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
