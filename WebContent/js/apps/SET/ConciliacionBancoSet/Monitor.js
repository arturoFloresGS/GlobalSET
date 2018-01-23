Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.ConciliacionBancoSet.Monitor');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';	
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.nombreEmpresa = apps.SET.NOM_EMPRESA;
	NS.iIdUsuario = apps.SET.iUserId ;

	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Esta operación puede tomar algunos minutos. \nCargando..."});
	
	NS.cadenaImprimir = "";

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
				/*var recordsEmpresas = NS.storeCmbEmpresa.recordType;	
				var todas = new recordsEmpresas({
			       	id : 0,
			       	descripcion : 'Seleccione'
		       	});
		   		NS.storeCmbEmpresa.insert(0,todas);*/
				Ext.getCmp(PF + 'txtEmpresa').setValue('');
				Ext.getCmp(PF + 'cmbEmpresa').setValue('');
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
//		,value: NS.nombreEmpresa
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
        ,x: 240
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

	ConciliacionBancoSetAction.consultarHeadersMonitor(127, 17, '01720100000086', function(resp, e){
//	 	alert(resp.ProximaConciliacion);
	 	Ext.getCmp(PF+'txtUlt').setValue(resp.UltimaConciliacion);
	 	Ext.getCmp(PF+'txtPeriodo').setValue(resp.PeriodoConciliacion);
	 	Ext.getCmp(PF+'txtProx').setValue(resp.ProximaConciliacion);
	});
	
	NS.contenedorMonitor = new Ext.FormPanel({
	    title: 'Monitor de Conciliaci\u00f3n Bancaria',
	    width: 811,
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
	                width: 800,
	                height: 650,
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
//                            value: NS.noEmpresa,
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
	                        x: 245,
	                        y: 59
	                    },
	                    NS.cmbChequera,
	                    {
	                        xtype: 'label',
	                        text: 'Fecha:',
	                        x: 393,
	                        y: 59
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        invalidText: 'No es un formato de fecha valido',
	                        id: PF + 'txtFechaIni',
	                        name: PF + 'txtFechaIni',
	                        x: 390,
	                        y: 80,
                            value: apps.SET.FEC_HOY,
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
	                        x: 510,
	                        y: 80,
                            value: apps.SET.FEC_HOY,
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
	                    },{xtype: 'fieldset',
	                    title: '',
	                    x: 628,
	                    y: 0,
	                    title:'Conciliación',
	                    layout: 'absolute',
	                    width: 150,
	                    height: 108,
	                    items: [
		                        {
		                            xtype: 'label',
		                            text: 'Última:',
		                            x: 0,
		                            y: 0
		                        },
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtUlt',
		                            name: PF+'txtUlt',
		                            readOnly: true,
			                        x: 55,
			                        y: 0,
			                        width: 70
			                    },
		                        {
		                            xtype: 'label',
		                            text: 'Periodo:',
		                            x: 0,
		                            y: 25
		                        },
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtPeriodo',
		                            name: PF+'txtPeriodo',
			                        x: 55,
			                        y: 25,
		                            readOnly: true,
			                        width: 70
			                    },
		                        {
		                            xtype: 'label',
		                            text: 'Siguiente:',
		                            x: 0,
		                            y: 50
		                        },
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtProx',
		                            name: PF+'txtProx',
			                        x: 55,
		                            readOnly: true,
			                        y: 50,
			                        width: 70
			                    }
		                        ]
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Buscar',
	                        x: 350,
	                        y: 110,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										var mapa = {};
										
//										mapa.noEmpresa = 17;
//										mapa.idEmpresa = 17;
//										mapa.idBanco  = 127;
//										mapa.idBanco2  = 127;
//										mapa.idChequera  = '01720100000086';
//										mapa.sFechaIni  = '07/02/2016';
//										mapa.sFechaFin  = '07/03/2016';
//										mapa.idUsuario = 2;
//										mapa.estatus   = 'MONITOR DE CONCILIACIONES';
										NS.cadenaImprimir = "";
										
										if(Ext.getCmp(PF+'txtEmpresa').getValue() == ''){
						   					BFwrk.Util.msgShow('Debe seleccionar una empresa', 'INFO');
						   					return;
										}else{
//											mapa.noEmpresa = Ext.getCmp(PF+'txtEmpresa').getValue();
											mapa.idEmpresa = Ext.getCmp(PF+'txtEmpresa').getValue();
										}
											
										if(Ext.getCmp(PF+'txtBanco').getValue() == ''){
						   					BFwrk.Util.msgShow('Debe seleccionar un banco', 'INFO');
						   					return;
										}else{
											mapa.idBanco = Ext.getCmp(PF+'txtBanco').getValue();
											mapa.idBanco2 = Ext.getCmp(PF+'txtBanco').getValue();
										}

										if(Ext.getCmp(PF+'cmbChequera').getValue() == ''){
						   					BFwrk.Util.msgShow('Debe seleccionar una chequera', 'INFO');
						   					return;
										}else{
											mapa.idChequera = Ext.getCmp(PF+'cmbChequera').getValue();
										}

										if(Ext.getCmp(PF+'txtFechaIni').getValue() == ''){
						   					BFwrk.Util.msgShow('Debe seleccionar una fecha inicial', 'INFO');
						   					return;
										}else{
											mapa.sFechaIni = Ext.getCmp(PF+'txtFechaIni').getValue().format('d/m/Y');
										}

										if(Ext.getCmp(PF+'txtFechaFin').getValue() == ''){
						   					BFwrk.Util.msgShow('Debe seleccionar una fecha final', 'INFO');
						   					return;
										}else{
											mapa.sFechaFin = Ext.getCmp(PF+'txtFechaFin').getValue().format('d/m/Y');
										}
										
//										alert(mapa);
										mascara.show();
										var jsonStringCriterios = Ext.util.JSON.encode(mapa);
										ConciliacionBancoSetAction.obtenerReporteGlobalMonitor(mapa,function(res, e){
										 	for(var key in res){
//										 		alert(res[key] +  " " + key);
										 		NS.cadenaImprimir += key +"="+ res[key] + "|";
										 	}
											
											/*Movimientos al día SET*/
										 	Ext.getCmp(PF+'txtCantCargoMovDiaSET').setValue(res.cantMovAlDiaCargoSET);
//										 	BFwrk.Util.formatNumber(Math.round((res.impMovAlDiaCargoSET)*100)/100)
										 	Ext.getCmp(PF+'txtImpCargoMovDiaSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impMovAlDiaCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoMovDiaSET').setValue(res.cantMovAlDiaAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoMovDiaSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impMovAlDiaAbonoSET)*100)/100));

										 	
										 	/*Movimientos al día Banco*/
										 	Ext.getCmp(PF+'txtCantCargoMovDiaBANCO').setValue(res.cantMovAlDiaCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoMovDiaBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impMovAlDiaCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoMovDiaBANCO').setValue(res.cantMovAlDiaAbonoBANCO);
										 	Ext.getCmp(PF+'txtImpAbonoMovDiaBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impMovAlDiaAbonoBANCO)*100)/100));

										 	/*Movimientos Conciliados automáticamente SET*/
										 	Ext.getCmp(PF+'txtCantCargoConsAutomSET').setValue(res.cantConsAutomCargoSET);
										 	Ext.getCmp(PF+'txtImpCargoConsAutomSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impConsAutomCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsAutomSET').setValue(res.cantConsAutomAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoConsAutomSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impConsAutomAbonoSET)*100)/100));

										 	/*Movimientos Agrupados y Manual SET*/
										 	Ext.getCmp(PF+'txtCantCargoConsManAgrupSET').setValue(res.cantManAgrupCargoSET);
										 	Ext.getCmp(PF+'txtImpCargoConsManAgrupSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impManAgrupCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsManAgrupSET').setValue(res.cantManAgrupAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoConsManAgrupSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impManAgrupAbonoSET)*100)/100));

										 	/*Movimientos Conciliados automáticamente BANCO*/
										 	Ext.getCmp(PF+'txtCantCargoConsAutomBANCO').setValue(res.cantConsAutomCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoConsAutomBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impConsAutomCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsAutomBANCO').setValue(res.cantConsAutomAbonoBANCO);
										 	Ext.getCmp(PF+'txtImpAbonoConsAutomBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impConsAutomAbonoBANCO)*100)/100));

										 	/*Movimientos Agrupados y Manual BANCO*/
										 	Ext.getCmp(PF+'txtCantCargoConsManAgrupBANCO').setValue(res.cantManAgrupCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoConsManAgrupBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impManAgrupCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsManAgrupBANCO').setValue(res.cantManAgrupAbonoBANCO);
										 	Ext.getCmp(PF+'txtImpAbonoConsManAgrupBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impManAgrupAbonoBANCO)*100)/100));
										
										 	/*Movimientos Detectados SET*/
										 	Ext.getCmp(PF+'txtCantCargoConsDetectSET').setValue(res.cantDetectadosCargoSET);
										 	Ext.getCmp(PF+'txtImpCargoConsDetectSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impDetectadosCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsDetectSET').setValue(res.cantDetectadosAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoConsDetectSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impDetectadosAbonoSET)*100)/100));

										 	/*Movimientos Aclarados SET*/
										 	Ext.getCmp(PF+'txtCantCargoConsAclaSET').setValue(res.cantAclaradosCargoSET);
										 	Ext.getCmp(PF+'txtImpCargoConsAclaSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impAclaradosCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsAclaSET').setValue(res.cantAclaradosAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoConsAclaSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impAclaradosAbonoSET)*100)/100));
										 	
										 	/*Movimientos Detectados BANCO*/
										 	Ext.getCmp(PF+'txtCantCargoConsDetectBANCO').setValue(res.cantDetectadosCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoConsDetectBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impDetectadosCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsDetectBANCO').setValue(res.cantDetectadosAbonoBANCO);
										 	Ext.getCmp(PF+'txtImpAbonoConsDetectBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impDetectadosAbonoBANCO)*100)/100));

										 	/*Movimientos Aclarados BANCO*/
										 	Ext.getCmp(PF+'txtCantCargoConsAclaBANCO').setValue(res.cantAclaradosCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoConsAclaBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impAclaradosCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsAclaBANCO').setValue(res.cantAclaradosAbonoBANCO);
										 	Ext.getCmp(PF+'txtImpAbonoConsAclaBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impAclaradosAbonoBANCO)*100)/100));

										 	/*Por conciliar SET*/
										 	Ext.getCmp(PF+'txtCantCargoConsPorConsSET').setValue(res.cantPorConcCargoSET);
										 	Ext.getCmp(PF+'txtImpCargoConsPorConsSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impPorConcCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsPorConsSET').setValue(res.cantPorConcAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoConsPorConsSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impPorConcAbonoSET)*100)/100));

										 	/*Por conciliar BANCO*/
										 	Ext.getCmp(PF+'txtCantCargoConsPorConsBANCO').setValue(res.cantPorConcCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoConsPorConsBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impPorConcCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoConsPorConsBANCO').setValue(res.cantPorConcAbonoBANCO); 
										 	Ext.getCmp(PF+'txtImpAbonoConsPorConsBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impPorConcAbonoBANCO)*100)/100));

										 	/*Pendientes SET*/
										 	Ext.getCmp(PF+'txtCantCargoMovPendSET').setValue(res.cantPendientesCargoSET);
										 	Ext.getCmp(PF+'txtImpCargoMovPendSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impPendientesCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoMovPendSET').setValue(res.cantPendientesAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoMovPendSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impPendientesAbonoSET)*100)/100));

										 	/*Pendientes BANCO*/
										 	Ext.getCmp(PF+'txtCantCargoMovPendBANCO').setValue(res.cantPendientesCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoMovPendBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impPendientesCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoMovPendBANCO').setValue(res.cantPendientesAbonoBANCO);
										 	Ext.getCmp(PF+'txtImpAbonoMovPendBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impPendientesAbonoBANCO)*100)/100));
										 	
										 	/*TOTAL SET*/
										 	Ext.getCmp(PF+'txtCantCargoTotalSET').setValue(res.cantTotalCargoSET);
										 	Ext.getCmp(PF+'txtImpCargoTotalSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impTotalCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoTotalSET').setValue(res.cantTotalAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoTotalSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impTotalAbonoSET)*100)/100));

										 	/*TOTAL BANCO*/
										 	Ext.getCmp(PF+'txtCantCargoTotalBANCO').setValue(res.cantTotalCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoTotalBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impTotalCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoTotalBANCO').setValue(res.cantTotalAbonoBANCO);
										 	Ext.getCmp(PF+'txtImpAbonoTotalBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impTotalAbonoBANCO)*100)/100));

										 	/*Ajuste SET*/
										 	Ext.getCmp(PF+'txtCantCargoAjusteSET').setValue(res.cantAjusteCargoSET);
										 	Ext.getCmp(PF+'txtImpCargoAjusteSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impAjusteCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoAjusteSET').setValue(res.cantAjusteAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoAjusteSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impAjusteAbonoSET)*100)/100));

										 	/*Ajuste BANCO*/
										 	Ext.getCmp(PF+'txtCantCargoAjusteBANCO').setValue(res.cantAjusteCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoAjusteBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impAjusteCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoAjusteBANCO').setValue(res.cantAjusteAbonoBANCO);
										 	Ext.getCmp(PF+'txtImpAbonoAjusteBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impAjusteAbonoBANCO)*100)/100));

										 	/*GRAN TOTAL SET*/
										 	Ext.getCmp(PF+'txtCantCargoGranTotalSET').setValue(res.cantGranTotalCargoSET);
										 	Ext.getCmp(PF+'txtImpCargoGranTotalSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impGranTotalCargoSET)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoGranTotalSET').setValue(res.cantGranTotalAbonoSET);
										 	Ext.getCmp(PF+'txtImpAbonoGranTotalSET').setValue(BFwrk.Util.formatNumber(Math.round((res.impGranTotalAbonoSET)*100)/100));

										 	/*GRAN TOTAL BANCO*/
										 	Ext.getCmp(PF+'txtCantCargoGranTotalBANCO').setValue(res.cantGranTotalCargoBANCO);
										 	Ext.getCmp(PF+'txtImpCargoGranTotalBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impGranTotalCargoBANCO)*100)/100));
										 	Ext.getCmp(PF+'txtCantAbonoGranTotalBANCO').setValue(res.cantGranTotalAbonoBANCO);
										 	Ext.getCmp(PF+'txtImpAbonoGranTotalBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.impGranTotalAbonoBANCO)*100)/100));
										 	
										 	Ext.getCmp(PF+'txtSaldoInicialSET').setValue(BFwrk.Util.formatNumber(Math.round((res.salodInicialSET)*100)/100));
										 	Ext.getCmp(PF+'txtSaldoInicialBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.salodInicialBANCO)*100)/100));

										 	Ext.getCmp(PF+'txtImpTotalSET').setValue(BFwrk.Util.formatNumber(Math.round((res.importeTotalSET)*100)/100));
										 	Ext.getCmp(PF+'txtImpTotalBANCO').setValue(BFwrk.Util.formatNumber(Math.round((res.importeTotalBANCO)*100)/100));

										 	mascara.hide();
										});
									}
								}
							}
	                    },
                        {
                            xtype: 'label',
                            text: 'Mov. al día:',
                            x: 5,
                            y: 182
                        },
                        {
                            xtype: 'label',
                            text: 'Cons. Autom.:',
                            x: 5,
                            y: 207
                        },
                        {
                            xtype: 'label',
                            text: 'Cons. Man. y Agrup.:',
                            x: 5,
                            y: 232
                        },
                        {
                            xtype: 'label',
                            text: 'Detectados:',
                            x: 5,
                            y: 257
                        },
                        {
                            xtype: 'label',
                            text: 'Aclarados:',
                            x: 5,
                            y: 282
                        },
                        {
                            xtype: 'label',
                            text: 'Por conciliar:',
                            x: 5,
                            y: 307
                        },
                        {
                            xtype: 'label',
                            text: 'Mov. pendientes:',
                            x: 5,
                            y: 332
                        },
                        {
                            xtype: 'label',
                            text: 'TOTAL:',
                            x: 5,
                            y: 357
                        },
                        {
                            xtype: 'label',
                            text: 'AJUSTE:',
                            x: 5,
                            y: 382
                        },
                        {
                            xtype: 'label',
                            text: 'GRAN TOTAL:',
                            x: 5,
                            y: 407
                        },
	                    {
	                    	xtype: 'fieldset',
		                    title: '',
		                    x: 110,
		                    y: 132,
		                    title:'SET',
		                    layout: 'absolute',
		                    width: 329,
		                    height: 303,
		                    items: [
									{
			                            xtype: 'label',
			                            text: 'Cargo',
			                            x: 0,
			                            y: 0
			                        },{
			                            xtype: 'label',
			                            text: 'Abono',
			                            x: 170,
			                            y: 0
			                        },
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoMovDiaSET',
									    name: PF+'txtCantCargoMovDiaSET',
									    x: 0,
									    y: 20,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoMovDiaSET',
									    name: PF+'txtImpCargoMovDiaSET',
									    x: 45,
									    y: 20,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoMovDiaSET',
									    name: PF+'txtCantAbonoMovDiaSET',
									    x: 155,
									    y: 20,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoMovDiaSET',
									    name: PF+'txtImpAbonoMovDiaSET',
									    x: 198,
									    y: 20,
									    readOnly: true,
									    width: 103
									},
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsAutomSET',
									    name: PF+'txtCantCargoConsAutomSET',
									    x: 0,
									    y: 45,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsAutomSET',
									    name: PF+'txtImpCargoConsAutomSET',
									    x: 45,
									    y: 45,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsAutomSET',
									    name: PF+'txtCantAbonoConsAutomSET',
									    x: 155,
									    y: 45,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsAutomSET',
									    name: PF+'txtImpAbonoConsAutomSET',
									    x: 198,
									    y: 45,
									    readOnly: true,
									    width: 103
									},
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsManAgrupSET',
									    name: PF+'txtCantCargoConsManAgrupSET',
									    x: 0,
									    y: 70,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsManAgrupSET',
									    name: PF+'txtImpCargoConsManAgrupSET',
									    x: 45,
									    y: 70,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsManAgrupSET',
									    name: PF+'txtCantAbonoConsManAgrupSET',
									    x: 155,
									    y: 70,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsManAgrupSET',
									    name: PF+'txtImpAbonoConsManAgrupSET',
									    x: 198,
									    y: 70,
									    readOnly: true,
									    width: 103
									},
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsDetectSET',
									    name: PF+'txtCantCargoConsDetectSET',
									    x: 0,
									    y: 95,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsDetectSET',
									    name: PF+'txtImpCargoConsDetectSET',
									    x: 45,
									    y: 95,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsDetectSET',
									    name: PF+'txtCantAbonoConsDetectSET',
									    x: 155,
									    y: 95,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsDetectSET',
									    name: PF+'txtImpAbonoConsDetectSET',
									    x: 198,
									    y: 95,
									    readOnly: true,
									    width: 103
									}/**/,
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsAclaSET',
									    name: PF+'txtCantCargoConsAclaSET',
									    x: 0,
									    y: 120,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsAclaSET',
									    name: PF+'txtImpCargoConsAclaSET',
									    x: 45,
									    y: 120,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsAclaSET',
									    name: PF+'txtCantAbonoConsAclaSET',
									    x: 155,
									    y: 120,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsAclaSET',
									    name: PF+'txtImpAbonoConsAclaSET',
									    x: 198,
									    y: 120,
									    readOnly: true,
									    width: 103
									}/**/,
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsPorConsSET',
									    name: PF+'txtCantCargoConsPorConsSET',
									    x: 0,
									    y: 145,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsPorConsSET',
									    name: PF+'txtImpCargoConsPorConsSET',
									    x: 45,
									    y: 145,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsPorConsSET',
									    name: PF+'txtCantAbonoConsPorConsSET',
									    x: 155,
									    y: 145,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsPorConsSET',
									    name: PF+'txtImpAbonoConsPorConsSET',
									    x: 198,
									    y: 145,
									    readOnly: true,
									    width: 103
									}/**/,
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoMovPendSET',
									    name: PF+'txtCantCargoMovPendSET',
									    x: 0,
									    y: 170,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoMovPendSET',
									    name: PF+'txtImpCargoMovPendSET',
									    x: 45,
									    y: 170,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoMovPendSET',
									    name: PF+'txtCantAbonoMovPendSET',
									    x: 155,
									    y: 170,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoMovPendSET',
									    name: PF+'txtImpAbonoMovPendSET',
									    x: 198,
									    y: 170,
									    readOnly: true,
									    width: 103
									}/**/,
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoTotalSET',
									    name: PF+'txtCantCargoTotalSET',
									    x: 0,
									    y: 195,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoTotalSET',
									    name: PF+'txtImpCargoTotalSET',
									    x: 45,
									    y: 195,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoTotalSET',
									    name: PF+'txtCantAbonoTotalSET',
									    x: 155,
									    y: 195,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoTotalSET',
									    name: PF+'txtImpAbonoTotalSET',
									    x: 198,
									    y: 195,
									    readOnly: true,
									    width: 103
									}/**/,
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoAjusteSET',
									    name: PF+'txtCantCargoAjusteSET',
									    x: 0,
									    y: 220,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoAjusteSET',
									    name: PF+'txtImpCargoAjusteSET',
									    x: 45,
									    y: 220,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoAjusteSET',
									    name: PF+'txtCantAbonoAjusteSET',
									    x: 155,
									    y: 220,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoAjusteSET',
									    name: PF+'txtImpAbonoAjusteSET',
									    x: 198,
									    y: 220,
									    readOnly: true,
									    width: 103
									}/**/,
			                        {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoGranTotalSET',
									    name: PF+'txtCantCargoGranTotalSET',
									    x: 0,
									    y: 245,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoGranTotalSET',
									    name: PF+'txtImpCargoGranTotalSET',
									    x: 45,
									    y: 245,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoGranTotalSET',
									    name: PF+'txtCantAbonoGranTotalSET',
									    x: 155,
									    y: 245,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoGranTotalSET',
									    name: PF+'txtImpAbonoGranTotalSET',
									    x: 198,
									    y: 245,
									    readOnly: true,
									    width: 103
									}
			                        ]
		                },
	                    {
	                    	xtype: 'fieldset',
		                    title: '',
		                    x: 448,
		                    y: 132,
		                    title:'BANCO',
		                    layout: 'absolute',
		                    width: 329,
		                    height: 303,
		                    items: [
									{
                                        xtype: 'label',
                                        text: 'Cargo',
                                        x: 0,
                                        y: 0
                                    },{
                                        xtype: 'label',
                                        text: 'Abono',
                                        x: 170,
                                        y: 0
                                    },
                                    {
									    xtype: 'textfield',
									    id: PF+'txtCantCargoMovDiaBANCO',
									    name: PF+'txtCantCargoMovDiaBANCO',
									    x: 0,
									    y: 20,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoMovDiaBANCO',
									    name: PF+'txtImpCargoMovDiaBANCO',
									    x: 45,
									    y: 20,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoMovDiaBANCO',
									    name: PF+'txtCantAbonoMovDiaBANCO',
									    x: 155,
									    y: 20,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoMovDiaBANCO',
									    name: PF+'txtImpAbonoMovDiaBANCO',
									    x: 198,
									    y: 20,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsAutomBANCO',
									    name: PF+'txtCantCargoConsAutomBANCO',
									    x: 0,
									    y: 45,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsAutomBANCO',
									    name: PF+'txtImpCargoConsAutomBANCO',
									    x: 45,
									    y: 45,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsAutomBANCO',
									    name: PF+'txtCantAbonoConsAutomBANCO',
									    x: 155,
									    y: 45,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsAutomBANCO',
									    name: PF+'txtImpAbonoConsAutomBANCO',
									    x: 198,
									    y: 45,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsManAgrupBANCO',
									    name: PF+'txtCantCargoConsManAgrupBANCO',
									    x: 0,
									    y: 70,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsManAgrupBANCO',
									    name: PF+'txtImpCargoConsManAgrupBANCO',
									    x: 45,
									    y: 70,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsManAgrupBANCO',
									    name: PF+'txtCantAbonoConsManAgrupBANCO',
									    x: 155,
									    y: 70,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsManAgrupBANCO',
									    name: PF+'txtImpAbonoConsManAgrupBANCO',
									    x: 198,
									    y: 70,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsDetectBANCO',
									    name: PF+'txtCantCargoConsDetectBANCO',
									    x: 0,
									    y: 95,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsDetectBANCO',
									    name: PF+'txtImpCargoConsDetectBANCO',
									    x: 45,
									    y: 95,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsDetectBANCO',
									    name: PF+'txtCantAbonoConsDetectBANCO',
									    x: 155,
									    y: 95,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsDetectBANCO',
									    name: PF+'txtImpAbonoConsDetectBANCO',
									    x: 198,
									    y: 95,
									    readOnly: true,
									    width: 103
									}/**/,
									{
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsAclaBANCO',
									    name: PF+'txtCantCargoConsAclaBANCO',
									    x: 0,
									    y: 120,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsAclaBANCO',
									    name: PF+'txtImpCargoConsAclaBANCO',
									    x: 45,
									    y: 120,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsAclaBANCO',
									    name: PF+'txtCantAbonoConsAclaBANCO',
									    x: 155,
									    y: 120,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsAclaBANCO',
									    name: PF+'txtImpAbonoConsAclaBANCO',
									    x: 198,
									    y: 120,
									    readOnly: true,
									    width: 103
									}/**/,
									{
									    xtype: 'textfield',
									    id: PF+'txtCantCargoConsPorConsBANCO',
									    name: PF+'txtCantCargoConsPorConsBANCO',
									    x: 0,
									    y: 145,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoConsPorConsBANCO',
									    name: PF+'txtImpCargoConsPorConsBANCO',
									    x: 45,
									    y: 145,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoConsPorConsBANCO',
									    name: PF+'txtCantAbonoConsPorConsBANCO',
									    x: 155,
									    y: 145,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoConsPorConsBANCO',
									    name: PF+'txtImpAbonoConsPorConsBANCO',
									    x: 198,
									    y: 145,
									    readOnly: true,
									    width: 103
									}/**/,
									{
									    xtype: 'textfield',
									    id: PF+'txtCantCargoMovPendBANCO',
									    name: PF+'txtCantCargoMovPendBANCO',
									    x: 0,
									    y: 170,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoMovPendBANCO',
									    name: PF+'txtImpCargoMovPendBANCO',
									    x: 45,
									    y: 170,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoMovPendBANCO',
									    name: PF+'txtCantAbonoMovPendBANCO',
									    x: 155,
									    y: 170,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoMovPendBANCO',
									    name: PF+'txtImpAbonoMovPendBANCO',
									    x: 198,
									    y: 170,
									    readOnly: true,
									    width: 103
									}/**/,
									{
									    xtype: 'textfield',
									    id: PF+'txtCantCargoTotalBANCO',
									    name: PF+'txtCantCargoTotalBANCO',
									    x: 0,
									    y: 195,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoTotalBANCO',
									    name: PF+'txtImpCargoTotalBANCO',
									    x: 45,
									    y: 195,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoTotalBANCO',
									    name: PF+'txtCantAbonoTotalBANCO',
									    x: 155,
									    y: 195,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoTotalBANCO',
									    name: PF+'txtImpAbonoTotalBANCO',
									    x: 198,
									    y: 195,
									    readOnly: true,
									    width: 103
									}/**/,
									{
									    xtype: 'textfield',
									    id: PF+'txtCantCargoAjusteBANCO',
									    name: PF+'txtCantCargoAjusteBANCO',
									    x: 0,
									    y: 220,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoAjusteBANCO',
									    name: PF+'txtImpCargoAjusteBANCO',
									    x: 45,
									    y: 220,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoAjusteBANCO',
									    name: PF+'txtCantAbonoAjusteBANCO',
									    x: 155,
									    y: 220,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoAjusteBANCO',
									    name: PF+'txtImpAbonoAjusteBANCO',
									    x: 198,
									    y: 220,
									    readOnly: true,
									    width: 103
									}/**/,
									{
									    xtype: 'textfield',
									    id: PF+'txtCantCargoGranTotalBANCO',
									    name: PF+'txtCantCargoGranTotalBANCO',
									    x: 0,
									    y: 245,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpCargoGranTotalBANCO',
									    name: PF+'txtImpCargoGranTotalBANCO',
									    x: 45,
									    y: 245,
									    readOnly: true,
									    width: 103
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtCantAbonoGranTotalBANCO',
									    name: PF+'txtCantAbonoGranTotalBANCO',
									    x: 155,
									    y: 245,
									    readOnly: true,
									    width: 40
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtImpAbonoGranTotalBANCO',
									    name: PF+'txtImpAbonoGranTotalBANCO',
									    x: 198,
									    y: 245,
									    readOnly: true,
									    width: 103
									}
			                        ]
		                }, 
		                {
	                        xtype: 'label',
	                        text: 'Saldo Inicial SET:',
	                        x: 125,
	                        y: 442
	                    },
						{
						    xtype: 'textfield',
						    id: PF+'txtSaldoInicialSET',
						    name: PF+'txtSaldoInicialSET',
						    x: 125,
						    y: 467,
						    readOnly: true,
						    width: 103
						}, 
		                {
	                        xtype: 'label',
	                        text: 'Importe Total:',
	                        x: 235,
	                        y: 467
	                    },
						{
						    xtype: 'textfield',
						    id: PF+'txtImpTotalSET',
						    name: PF+'txtImpTotalSET',
						    x: 310,
						    y: 467,
						    readOnly: true,
						    width: 103
						}, 
		                {
	                        xtype: 'label',
	                        text: 'Saldo Inicial BANCO:',
	                        x: 460,
	                        y: 442
	                    },
						{
						    xtype: 'textfield',
						    id: PF+'txtSaldoInicialBANCO',
						    name: PF+'txtSaldoInicialBANCO',
						    x: 460,
						    y: 467,
						    readOnly: true,
						    width: 103
						}, 
		                {
	                        xtype: 'label',
	                        text: 'Importe Total:',
	                        x: 580,
	                        y: 467
	                    },
						{
						    xtype: 'textfield',
						    id: PF+'txtImpTotalBANCO',
						    name: PF+'txtImpTotalBANCO',
						    x: 655,
						    y: 467,
						    readOnly: true,
						    width: 103
						},
	                    {
	        	            xtype: 'textfield',
	        	            x: 0,
                            y: 0,
                            hidden:true,
	        	            name: 'nomReporte',
	        	            id: 'nomReporte',
	        	            value: 'MonitorConciliacion'
	        	        },
	                    {
	        	            xtype: 'textfield',
	        	            x: 0,
                            y: 0,
                            hidden:true,
	        	            name: 'nomEmpresa',
	        	            id: 'nomEmpresa',
	        	            value: ''
	        	        },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 308,
	                        y: 497,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										if(NS.cadenaImprimir == ""){
						   					BFwrk.Util.msgShow('Debe consultar antes de imprimir', 'INFO');
						   					return;
										}else{
											
//											strParams = '?nomReporte=MonitorConciliacion';
//											strParams += '&'+'datosReporte='+NS.cadenaImprimir+'&nombreEmpresa='+Ext.getCmp(PF+'cmbEmpresa').getValue();
//											window.open("/SET/jsp/bfrmwrk/gnrators/genJasperMonitorPDF.jsp"+strParams);
											
									    	var forma = NS.contenedorMonitor.getForm();
									    	Ext.getCmp('nomReporte').setValue("MonitorConciliacion");
									    	Ext.getCmp('nomEmpresa').setValue(Ext.getCmp(PF+'cmbEmpresa').getRawValue());
											forma.url = '/SET/jsp/bfrmwrk/gnrators/genJasperMonitorPDF.jsp';
										    forma.standardSubmit = true;
										    forma.method = 'POST';
										    forma.target = '_blank';
										    var el = forma.getEl().dom;
										    var target = document.createAttribute("target");
										    target.nodeValue = "_blank";
										    el.setAttributeNode(target);
										    forma.submit({target: '_blank'});
										};

										}
									}
							}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 397,
	                        y: 497,
	                        width: 70,
	                        listeners:{
								click:{
									fn:function(){
										NS.contenedorMonitor.getForm().reset();
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
	
	NS.contenedorMonitor.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
