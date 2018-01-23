
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Inversiones.ReportesOperativos');
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
		                   		['1', 'Inversiones establecidas hoy'],
		                   		['2', 'Vencimiento de inversiones'],
//		                   		['3', 'Posición de inversiones establecidas'],
//		                   		['4', 'Saldos de inversión']
		                   	 ];
	                   	 
   	NS.storeTipoReporte = new Ext.data.SimpleStore({
	  		idProperty: 'id',
	  		fields: [
	  					{name: 'id'},
	  					{name: 'descripcion'}
	  				]
  	});
  	NS.storeTipoReporte.loadData(NS.datosCmbTipoReporte);
	
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
        ,y: 130
        ,width: 220
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Selecciona un tipo de reporte'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					if(NS.cmbTipoReporte.getValue() == '1'){
						Ext.getCmp(PF + 'txtFechaIni').setValue(NS.fecHoy);
						Ext.getCmp(PF + 'txtFechaFin').setValue(NS.fecHoy);
						Ext.getCmp(PF + 'txtFechaIni').setDisabled(true);
						Ext.getCmp(PF + 'txtFechaFin').setDisabled(true);
					}else{
						Ext.getCmp(PF + 'txtFechaIni').setDisabled(false);
						Ext.getCmp(PF + 'txtFechaFin').setDisabled(false);
					}
				}
			}
		}
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
        ,y: 50
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
	                        text: 'Divisa:',
	                        x: 20,
	                        y: 50
	                    },
	                    {
                            xtype: 'textfield',
                            id: PF+'txtDivisa',
                            name: PF+'txtDivisa',
                            value: '',
                            x: 80,
                            y: 50,
                            width: 60,
                              listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			//NS.cmbDivisa.setValue(valor);
	                        			var valorTexto = "";
	                        			valorTexto = "" + valor;
	                        			Ext.getCmp(PF+'txtDivisa').setValue(valorTexto.toUpperCase());
	                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa',NS.cmbDivisa.getId());
	                        			//NS.accionarCmbEmpresa(comboValor);
	                        		}
	                        	}
	                        }
                        },
	                    NS.cmbDivisa,
	                    {
	                        xtype: 'label',
	                        text: 'Fecha Valor:',
	                        x: 20,
	                        y: 90
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        invalidText: 'No es un formato de fecha valido',
	                        id: PF + 'txtFechaIni',
	                        name: PF + 'txtFechaIni',
	                        value: NS.fecHoy,
	                        x: 100,
	                        y: 90,
	                        width: 110,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			if (valor > Ext.getCmp(PF + 'txtFechaFin').getValue()){
	                        				Ext.Msg.alert('SET', 'La fecha inicial debe ser menor o igual a la fecha final');
	                        				caja.reset();
											return;
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
	                        value: NS.fecHoy,
	                        x: 240,
	                        y: 90,
	                        width: 110,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			if (valor < Ext.getCmp(PF + 'txtFechaIni').getValue()){
	                        				Ext.Msg.alert('SET', 'La fecha final debe ser mayor o igual a la fecha inicial');
	                        				caja.reset();
											return;
	                        			}
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Reporte:',
	                        x: 20,
	                        y: 130
	                    },
	                    NS.cmbTipoReporte,
	                    {
	                        xtype: 'button',
	                        text: 'Excel',
	                        x: 650,
	                        y: 170,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										NS.generarExcel();
									}
								}
							}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 740,
	                        y: 170,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										NS.strParametros();
									}
								}
							}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 830,
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

		var nomReporte = "";
		var valorCombo = NS.cmbTipoReporte.getValue();
		
		if (valorCombo == '1')
			nomReporte = 'ReporteInversiones';
		else if (valorCombo == '2')
			nomReporte = 'ReporteVencimiento';
		else if (valorCombo == '3')
			nomReporte = 'ReportePosicionInv';
		else if (valorCombo == '4')
			nomReporte = 'ReporteSaldosInv';
		else{
			Ext.Msg.alert('SET', 'Debe seleccionar un tipo de reporte');
			return;
		}
		
		
		strParams = '?nomReporte=' + nomReporte;
		strParams += '&'+'noEmpresa='+Ext.getCmp(PF+'txtEmpresa').getValue();
		strParams += '&'+'idUsuario='+NS.iIdUsuario;
		strParams += '&'+'EMPRESA='+NS.cmbEmpresa.getRawValue();
		strParams += '&'+'fecha='+Ext.getCmp(PF + 'txtFechaIni').getValue().format('d/m/Y');
		strParams += '&'+'fechaFin='+Ext.getCmp(PF + 'txtFechaFin').getValue().format('d/m/Y');
		strParams += '&'+'idDivisa='+Ext.getCmp(PF + 'txtDivisa').getValue();
		strParams += '&'+'divisa='+NS.cmbDivisa.getRawValue();
		window.open("/SET/jsp/bfrmwrk/gnrators/RepInversiones.jsp"+strParams);
	}
	
	NS.generarExcel = function(){	
		var nomReporte = "";
		var valorCombo = NS.cmbTipoReporte.getValue();
		
		if (valorCombo == '1')
			nomReporte = 'ExcelInversiones';
		else if (valorCombo == '2')
			nomReporte = 'ExcelVencimiento';
		else if (valorCombo == '3')
			nomReporte = 'ExcelPosicionInv';
		else if (valorCombo == '4')
			nomReporte = 'ExcelSaldosInv';
		else{
			Ext.Msg.alert('SET', 'Debe seleccionar un tipo de reporte');
			return;
		}

		
		parametros='?nomReporte='+nomReporte;
		parametros+='&nomParam1=noEmpresa';
		parametros+='&valParam1=' + Ext.getCmp(PF+'txtEmpresa').getValue();
		parametros+='&nomParam2=idUsuario';
		parametros+='&valParam2=' + NS.iIdUsuario;
		parametros+='&nomParam3=fecha';
		parametros+='&valParam3=' + Ext.getCmp(PF + 'txtFechaIni').getValue().format('d/m/Y');
		parametros+='&nomParam4=fechaFin';
		parametros+='&valParam4=' + Ext.getCmp(PF + 'txtFechaFin').getValue().format('d/m/Y');
		parametros+='&nomParam5=idDivisa';
		parametros+='&valParam5=' + Ext.getCmp(PF + 'txtDivisa').getValue();
		parametros+='&nomParam4=EMPRESA';
		parametros+='&valParam4=' + +NS.cmbEmpresa.getRawValue();
		
		window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
	 
	}
	
	NS.contenedorReporte.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
