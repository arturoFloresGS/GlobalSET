/**
 * @author Jessica Arelly Cruz Cruz
 * @since 01/03/2011
 */
	Ext.onReady(function(){
	Ext.QuickTips.init();	
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.BancaElectronica.MovsBancaE');	
	NS.tabContId = apps.SET.tabContainerId;	
	//NS.tabContId = 'contenedorMovimientos';		
	var PF = apps.SET.tabID+'.';	
	
	/*  declaracion de variables   */
	NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.empresaActual = true;
	NS.empresaTodas = false;
	NS.movimientoCapturado = true;
	NS.movimientoBanca = false;
	NS.tipoIngreso = false;
	NS.tipoEgreso = false;
	NS.tipoTodos = true;
	NS.tipoConceptos = false;
	NS.tipoMovimientos = false;
	NS.descBanco = '';
	NS.descConcepto = '';
	NS.chkTEF = false;
	NS.chkDia = false;
	NS.chkExcel = false;
	NS.chkDetalle = false;
	NS.opcion1 = false;
	
	/************************************************************************
	 * 							COMBO EMPRESAS
	 ************************************************************************/
		
    //Caja numero de empresa
	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
        name:PF + 'txtNoEmpresa',
		x: 10,
        y: 15,
        width: 50,
        tabIndex: 0,
        listeners: {
			change: {
				fn: function(caja,valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
					else {
						Ext.getCmp(PF + 'txtNoEmpresa').setValue(0);
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
					}
                }
			}
    	}
    });
	
	NS.storeEmpresas = new Ext.data.DirectStore({
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

				var recordsStoreEmpresas = NS.storeEmpresas.recordType;	
				var todas = new recordsStoreEmpresas({
					id: 0,
					descripcion: '***************TODAS***************'
				});
				NS.storeEmpresas.insert(0,todas);
				
				
			}
		}
	}); 
	
	NS.storeEmpresas.load();
	/*
	*/
	
	
	//Combo empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 70,
        y: 15,
        width: 310,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'id',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());					
					NS.storeBancos.removeAll();
					NS.storeChequeras.removeAll();
					Ext.getCmp(PF+'cmbChequeras').setValue('');
					Ext.getCmp(PF+'txtIdBanco').setValue('');
					Ext.getCmp(PF+'cmbBancos').setValue('');
					
					NS.storeBancos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
					NS.storeBancos.load();
				}
			}
		}
	});
	/************************************************************************
	 * 							COMBO EMPRESAS
	 ************************************************************************/
	
	
	//store bancos
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noEmpresa:NS.GI_ID_EMPRESA
		},
		root: '',
		paramOrder:['noEmpresa'],
		root: '',
		directFn: MovimientosBancaEAction.llenarComboBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No hay bancos disponibles para la empresa');
				}
			}
		}
	}); 
	
	NS.storeBancos.load();
	
	NS.accionarCmbBancos = function(comboValor)
	{
		Ext.getCmp(PF+'cmbChequeras').setValue('');
		Ext.getCmp(PF+'cmbConceptos').setValue('');
		//Ext.getCmp(PF+'txtIdBanco').setValue(comboValor);
		NS.storeConceptos.baseParams.noBanco = comboValor;
		NS.storeConceptos.load();
		
		NS.storeChequeras.baseParams.noBanco = comboValor;		
		 				
		NS.storeChequeras.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
		NS.storeChequeras.load();
		NS.descBanco = NS.storeBancos.getById(comboValor).get('descripcion');
		
		
		//vsFlagConfSet = gobjVarGlobal.valor_Configura_set(352)
 				//If cmbBanco.Text = "BANCOMER" And vsFlagConfSet = "SI" Then
		if(NS.descBanco === 'BANCOMER')
		{
			//Ext.getCmp(PF+'chkMovtoDia').show();
	        Ext.getCmp(PF+'chkMovtoTEF').hide();
        }
	    else if(NS.descBanco === 'BANAMEX')
	    {
	       	Ext.getCmp(PF+'chkMovtoDia').hide();
        	Ext.getCmp(PF+'chkMovtoTEF').show();
        }
	    else
	    {
	        Ext.getCmp(PF+'chkMovtoDia').hide();
	        Ext.getCmp(PF+'chkMovtoTEF').hide();
	    }	
	}
	
	//combo bancos
	NS.cmbBancos = new Ext.form.ComboBox({
		store: NS.storeBancos
		,name: PF+'cmbBancos'
		,id: PF+'cmbBancos'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 70
        ,y: 60
        ,width: 140
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBancos.getId());
					NS.accionarCmbBancos(combo.getValue());				
				}
			}
		}
	});
	
	//store conceptos
	NS.storeConceptos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noBanco: 0
		},
		root: '',
		paramOrder:['noBanco'],
		root: '',
		directFn: MovimientosBancaEAction.obtenerConceptos, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','Error, no existen los conceptos de este banco en cat_banco_concepto '+
					'y la bandera de banca Electrónica está en \'S\'');
				}
			}
		}
	}); 
	
	// combo conceptos
	NS.cmbConceptos = new Ext.form.ComboBox({
		store: NS.storeConceptos
		,name: PF+'cmbConceptos'
		,id: PF+'cmbConceptos'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 180
        ,width: 370
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un concepto'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						NS.descConcepto = NS.storeConceptos.getById(combo.getValue()).get('descripcion');
				}
			}
		}
	});
	
	//store chequeras
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noBanco: 0,
			noEmpresa:NS.GI_ID_EMPRESA
		},
		root: '',
		paramOrder:['noBanco','noEmpresa'],
		root: '',
		directFn: MovimientosBancaEAction.llenarComboChequeras, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					//Ext.Msg.alert('SET','No hay chequeras disponibles para el banco');
				}
			}
		}
	}); 
	
	//combo chequeras
	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras
		,name: PF+'cmbChequeras'
		,id: PF+'cmbChequeras'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 230
        ,y: 60
        ,width: 150
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						
				}
			}
		}
	});

	/* formulario */
	NS.contenedorMovimientos =new Ext.FormPanel({
    title: 'Movimientos de Banca Electrónica',
    width: 398,
    height: 479,
    padding: 10,
    frame: true,
    autoScroll: true,
    layout: 'absolute',
    renderTo: NS.tabContId,
    items : [
    		{
             xtype: 'fieldset',
             title: '',
             x: 0,
             y: 0,
             width: 410,
             height: 460,
             layout: 'absolute',
             items: [
                     NS.txtNoEmpresa,
                     NS.cmbEmpresas,
	            {
	                xtype: 'label',
	                text: 'Empresa:',
	                x: 10,
	                y: 0
	            },
	            {
	                xtype: 'textfield',
	                id: PF+'txtEmpresa',
	                name: PF+'txtEmpresa',
	                value: NS.GS_DESC_EMPRESA,
	                x: 70,
	                y: 10,
	                width: 310,
	                hidden:true
	            },
	            {
	                xtype: 'label',
	                text: 'Banco:',
	                x: 10,
	                y: 40
	            },
	            {
	                xtype: 'label',
	                text: 'Chequera:',
	                x: 230,
	                y: 40
	            },
	            {
	                xtype: 'textfield',
	                id: PF+'txtIdBanco',
	                name: PF+'txtIdBanco',
	                x: 10,
	                y: 60,
	                width: 50,
	                listeners:{
	                	change:{
	               	         fn:function(caja, valor){
								var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBanco',NS.cmbBancos.getId());
	                   			NS.accionarCmbBancos(comboValue);
	               	         }
	               		   }
	           			 }
	            }
	            ,NS.cmbBancos
	            ,NS.cmbChequeras
	            ,{
	                xtype: 'label',
	                text: 'Fecha:',
	                x: 10,
	                y: 100
	            },
	            {
	                xtype: 'datefield',
	                format: 'd/m/Y',
	                id: PF+'txtFechaIni',
	                name: PF+'txtFechaIni',
	                value: apps.SET.FEC_HOY,
	                x: 10,
	                y: 120,
	                width: 95
	            },
	            {
	                xtype: 'datefield',
	                format: 'd/m/Y',
	                id: PF+'txtFechaFin',
	                name: PF+'txtFechaFin',
	                value: apps.SET.FEC_HOY,
	                x: 120,
	                y: 120,
	                width: 95
	            },
	            {
	                xtype: 'fieldset',
	                title: 'Empresa',
	                x: 230,
	                y: 100,
	                width: 150,
	                height: 60,
	                layout: 'absolute',
	                hidden:true,
	                items: [
	                	{
				            xtype: 'radiogroup',
				            columns: 2,
				            items: [
				                    {
				                        xtype: 'radio',
				                        name: PF+'optEmpresa',
		                        		inputValue: 0,
				                        x: 0,
				                        y: 0,
				                        boxLabel: 'Actual',
				                        checked:true,
				                        listeners:{
											check:{
												fn:function(opt, valor)
												{
													if(valor==true){
													NS.empresaActual = true;
													NS.empresaTodas = false;
													}
												}
											}
										}
				                    },
				                    {
				                        xtype: 'radio',
				                        name: PF+'optEmpresa',
		                        		inputValue: 1,
				                        x: 70,
				                        y: 0,
				                        boxLabel: 'Todas',
				                        listeners:{
											check:{
												fn:function(opt, valor)
												{
													if(valor==true){
													NS.empresaActual = false;
													NS.empresaTodas = true;
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
	                xtype: 'label',
	                text: 'Concepto:',
	                x: 10,
	                y: 160
	            },
	            NS.cmbConceptos
	            ,{
	                xtype: 'fieldset',
	                title: 'Movimiento',
	                id: PF+'frameMovimiento',
	                name: PF+'frameMovimiento',
	                x: 10,
	                y: 220,
	                width: 120,
	                height: 110,
	                layout: 'absolute',
	                items: [
	                	{
	                 		xtype: 'radiogroup',
				            columns: 1,
				            items: [
				                    {
				                        xtype: 'radio',
				                        name: PF+'optMov',
		                        		inputValue: 0,
				                        x: 0,
				                        y: 0,
				                        boxLabel: 'Capturado',
				                        checked: true,
				                        listeners:{
											check:{
												fn:function(opt, valor)
												{
													if(valor==true){
													NS.movimientoCapturado = true;
													NS.movimientoBanca = false;
													NS.opcion1 = true;
													}
												}
											}
										}
				                    },
				                    {
				                        xtype: 'radio',
				                        name: PF+'optMov',
		                        		inputValue: 1,
				                        boxLabel: 'Banca Electrónica',
				                        x: 0,
				                        y: 30,
				                        listeners:{
											check:{
												fn:function(opt, valor)
												{
													if(valor==true){
													NS.movimientoCapturado = false;
													NS.movimientoBanca = true;
													NS.opcion1 = true;
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
	                xtype: 'fieldset',
	                title: 'Tipo',
	                x: 160,
	                y: 220,
	                width: 220,
	                height: 160,
	                layout: 'absolute',
	                items: [
			                {
					            xtype: 'radiogroup',
					            columns: 1,
					            items: [
					                    {
					                        xtype: 'radio',
					                        boxLabel: 'Ingreso',
					                        name: PF+'optTipo',
		                        			inputValue: 0,
					                        x: 0,
					                        y: 0,
					                        listeners:{
												check:{
													fn:function(opt, valor)
													{
														if(valor==true){
															NS.tipoIngreso = true;
															NS.tipoEgreso = false;
															NS.tipoTodos = false;
															NS.tipoConceptos = false;
															NS.tipoMovimientos = false;
															
															Ext.getCmp(PF+'frameMovimiento').setDisabled(false);
														    Ext.getCmp(PF+'chkDetalle').hide();
														    NS.chkDetalle = false;
														    /*
														    if(NS.descBanco === 'BANAMEX')
														        Ext.getCmp(PF+'chkMovtoTEF').show();
														    else if (NS.descBanco === 'BANCOMER')
														    	Ext.getCmp(PF+'chkMovtoDia').show();
														    else
														    {
														        Ext.getCmp(PF+'chkMovtoDia').hide();
														        Ext.getCmp(PF+'chkMovtoTEF').hide();
													        }*/
														}
													}
												}
											}
					                    },
					                    {
					                        xtype: 'radio',
					                        boxLabel: 'Egreso',
					                        name: PF+'optTipo',
		                        			inputValue: 1,
					                        x: 0,
					                        y: 20,
					                        listeners:{
												check:{
													fn:function(opt, valor)
													{
														if(valor==true){
															NS.tipoIngreso = false;
															NS.tipoEgreso = true;
															NS.tipoTodos = false;
															NS.tipoConceptos = false;
															NS.tipoMovimientos = false;
															Ext.getCmp(PF+'frameMovimiento').setDisabled(false);
															Ext.getCmp(PF+'chkDetalle').hide();
														    NS.chkDetalle = false;
														    /*
															if(NS.descBanco === 'BANAMEX')
														        Ext.getCmp(PF+'chkMovtoTEF').show();
														    else if (NS.descBanco === 'BANCOMER')
														    	Ext.getCmp(PF+'chkMovtoDia').show();
														    else
														    {
														        Ext.getCmp(PF+'chkMovtoDia').hide();
														        Ext.getCmp(PF+'chkMovtoTEF').hide();
													        }*/
														}
													}
												}
											}
					                    },
					                    {
					                        xtype: 'radio',
					                        boxLabel: 'Todos',
					                        name: PF+'optTipo',
		                        			inputValue: 2,
					                        x: 0,
					                        y: 40,
					                        checked: true,
					                        listeners:{
												check:{
													fn:function(opt, valor)
													{
														if(valor==true){
															NS.tipoIngreso = false;
															NS.tipoEgreso = false;
															NS.tipoTodos = true;
															NS.tipoConceptos = false;
															NS.tipoMovimientos = false;
															Ext.getCmp(PF+'frameMovimiento').setDisabled(false);
															Ext.getCmp(PF+'chkDetalle').hide();
														    NS.chkDetalle = false;
														    /*
															if(NS.descBanco === 'BANAMEX')
														        Ext.getCmp(PF+'chkMovtoTEF').show();
														    else if (NS.descBanco === 'BANCOMER')
														    	Ext.getCmp(PF+'chkMovtoDia').show();
														    else
														    {
														        Ext.getCmp(PF+'chkMovtoDia').hide();
														        Ext.getCmp(PF+'chkMovtoTEF').hide();
													        }*/
														}
													}
												}
											}
					                    },
					                    {
					                    	xtype: 'label',
					                    	text: '____________________________________________',
					                    	x: 0,
					                    	y:40
					                    },
					                    {
					                        xtype: 'radio',
					                        boxLabel: 'Conceptos Desconocidos',
					                        name: PF+'optTipo',
		                        			inputValue: 3,
					                        x: 0,
					                        y: 60,
					                        hidden: true,
					                        listeners:{
												check:{
													fn:function(opt, valor)
													{
														if(valor==true){
															NS.tipoIngreso = false;
															NS.tipoEgreso = false;
															NS.tipoTodos = false;
															NS.tipoConceptos = true;
															NS.tipoMovimientos = false;
															NS.movimientoBanca = true;
															//Ext.getCmp(PF+'optMov').getInputValue().checked(true);
															Ext.getCmp(PF+'frameMovimiento').setDisabled(true);
														    Ext.getCmp(PF+'chkDetalle').show();
														    NS.chkDetalle = false;
														    
														    Ext.getCmp(PF+'chkMovtoDia').hide();
														    Ext.getCmp(PF+'chkMovtoTEF').hide();
														}
													}
												}
											}
					                    },
					                    {
					                        xtype: 'radio',
					                        boxLabel: 'Movimientos Avanzados Autómata',
					                        name: PF+'optTipo',
		                        			inputValue: 4,
					                        x: 0,
					                        y: 80,
					                        hidden: true,
					                        listeners:{
												check:{
													fn:function(opt, valor)
													{
														if(valor==true){
															NS.tipoIngreso = false;
															NS.tipoEgreso = false;
															NS.tipoTodos = false;
															NS.tipoConceptos = false;
															NS.tipoMovimientos = true;
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
	                xtype: 'checkbox',
	                boxLabel: 'Detalle',
	                id: PF+'chkDetalle',
	                name: PF+'chkDetalle',
	                hidden: true,
	                value: false,
	                x: 10,
	                y: 340,
	                listeners:{
						check:{
							fn:function(opt, valor)
							{
								if(valor==true){
									NS.chkDetalle = true;
									NS.chkDia = false
									NS.chkTEF = false;
									NS.chkExcel = false
								}
								else NS.chkDetalle = false;
							}
						}
					}
	            },
	            {
	                xtype: 'checkbox',
	                boxLabel: 'Exportar a Excel',
	                id: PF+'chkExcel',
	                name: PF+'chkExcel',
	                value: false,
	                hidden:true,
	                x: 10,
	                y: 360,
	                listeners:{
						check:{
							fn:function(opt, valor)
							{
								if(valor==true){
									NS.chkDetalle = false;
									NS.chkDia = false
									NS.chkTEF = false;
									NS.chkExcel = true
								}
								else NS.chkExcel = false;
							}
						}
					}
	            },
	            {
	                xtype: 'checkbox',
	                boxLabel: 'Incluir movimientos TEF',
	                id: PF+'chkMovtoTEF',
	                name: PF+'chkMovtoTEF',
	                hidden: true,
	                value: false,
	                x: 10,
	                y: 380,
	                listeners:{
						check:{
							fn:function(opt, valor)
							{
								if(valor==true){
									NS.chkTEF = true;
									NS.chkDia = false
								}
								else NS.chkTEF = false;
							}
						}
					}
	            },
	            {
	                xtype: 'checkbox',
	                boxLabel: 'Incluir movimientos del dia',
	                id: PF+'chkMovtoDia',
	                name: PF+'chkMovtoDia',
	                hidden: true,
	                x: 10,
	                y: 400,
	                value: false,
	                listeners:{
						check:{
							fn:function(opt, valor)
							{
								if(valor==true){
									NS.chkDia = true;
									NS.chkTEF = false;
								}
								else NS.chkDia = false
							}
						}
					}
	            },
	            {
	                xtype: 'button',
	                text: 'Limpiar',
	                x: 260,
	                y: 410,
	                width: 80,
	                listeners:{
	       				click:{
	       					fn:function(e){
	                			NS.contenedorMovimientos.getForm().reset();
	                			//NS.obtenerFechaHoy();
	                			NS.empresaActual = true;
								NS.empresaTodas = false;
								NS.movimientoCapturado = true;
								NS.movimientoBanca = false;
								NS.tipoIngreso = false;
								NS.tipoEgreso = false;
								NS.tipoTodos = true;
								NS.tipoConceptos = false;
								NS.tipoMovimientos = false;
								NS.descBanco = '';
								NS.descConcepto = '';
								NS.chkTEF = false;
								NS.chkDia = false;
								NS.chkExcel = false;
								NS.chkDetalle = false;
	               			}
	           			}
	       			}
	            },
	            {
	                xtype: 'button',
	                text: 'Imprimir',
	                x: 160,
	                y: 410,
	                width: 80,
	                listeners:{
	       				click:{
	       					fn:function(e){
									
	       						NS.lbanco_sup = 0;
							    NS.lbanco_inf = 0;
							    NS.psConcepto = '';
							    NS.l_valor = '';
							    NS.empresainf = 0;
							    NS.empresasup = 0;
							    NS.dateinf = '';
							    NS.datesup = '';
							    NS.origenMov = 0;
							    NS.tipoMov = '';
							    NS.psEmpresa = '';
							   
							    NS.pb_movtoTEF = false;
							    NS.pb_movtoDia = false;
							    NS.pb_movotoAutomata = false;
							    
							    
							    if(Ext.getCmp(PF+'txtNoEmpresa').getValue() == '')
							    	Ext.getCmp(PF+'txtNoEmpresa').setValue('0');
							    
							    //if(NS.empresaActual == true)
							    if(parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue()) != 0)
							    {
							    	var nameEmpresa = Ext.getCmp(PF+'cmbEmpresas').getValue();
							    	var nameLiteral =  NS.storeEmpresas.getById(nameEmpresa).get("descripcion");
							    	
							    	NS.empresainf =parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
							    	NS.empresasup =parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
							    	NS.psEmpresa = nameLiteral;
							    	//NS.empresainf = NS.GI_ID_EMPRESA;
							        //NS.empresasup = NS.GI_ID_EMPRESA;
							    }
							    else
							    {
							    	
							        NS.psEmpresa = 'REPORTE GLOBAL';
							        NS.empresainf = 0;
							        NS.empresasup = 30000;
						    	}
						    	
						    	if(NS.movimientoCapturado == true)
							        NS.origenMov = 0;
							    else
							        NS.origenMov = 1;
							        
						       	if(NS.tipoIngreso == true)
						       		NS.tipoMov = 'I';
							            
							    if(NS.tipoEgreso == true)
							        NS.tipoMov = 'E';
							    
							    if(NS.tipoTodos == true)
							        NS.tipoMov = '%25';
							    
						        if(Ext.getCmp(PF+'cmbChequeras').getValue() === '')
						        {
							        NS.l_valor = '%25';
						        }
							    else
							    {
							        NS.l_valor = Ext.getCmp(PF+'cmbChequeras').getValue();
						        }
							    
						        if(parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue()) != 0) {
		       						if(Ext.getCmp(PF+'txtIdBanco').getValue() == '')
		              				{
		              					Ext.Msg.alert('SET','Debe seleccionar un banco');
		              					return;
		              				}
		              				else
		              				{
		              					NS.lbanco_inf = parseInt(Ext.getCmp(PF+'txtIdBanco').getValue());
								        NS.lbanco_sup = parseInt(Ext.getCmp(PF+'txtIdBanco').getValue());
							        }
						        }else {
						        	NS.lbanco_inf = 0;
							        NS.lbanco_sup = 3000;
						        }
	        					if(NS.tipoConceptos == false)
	        					{
		              				if(Ext.getCmp(PF+'txtFechaIni').getValue() == '')
		              				{
		              					Ext.Msg.alert('SET','Debe seleccionar una fecha');
		              					return;
		              				}
		              				if(Ext.getCmp(PF+'txtFechaFin').getValue() == '')
		              				{
		              					Ext.Msg.alert('SET','Debe seleccionar una fecha final');
		              					return;
		              				}
		              				
		              				//Valida si se van a mostrar los movimientos TEF o los del dia.
		              				if(NS.chkTEF = false && Ext.getCmp(PF+'chkMovtoTEF').isVisible())
							            NS.pb_movtoTEF = true;
							        else
							            NS.pb_movtoTEF = false;
							        
							        if(NS.chkDia = false && Ext.getCmp(PF+'chkMovtoDia').isVisible())
							            NS.pb_movtoDia = true;
							        else
							            NS.pb_movtoDia = false;
							        
							        if(NS.tipoMovimientos == true)
							        {
							            NS.pb_movotoAutomata = true;
							            NS.tipoMov = '%25';
						            }
							        else
							            NS.pb_movotoAutomata = false;
	              				}
	              				
	              				if(Ext.getCmp(PF+'txtFechaIni').getValue() != '')
	              				{
							        NS.dateinf = cambiarFecha(''+Ext.getCmp(PF+'txtFechaIni').getValue());
							        NS.datesup = cambiarFecha(''+Ext.getCmp(PF+'txtFechaFin').getValue());
						        }
							    else
							    {
							        NS.dateinf = '01/01/1990';
							        NS.datesup = '01/01/2190';
							    }
							    
							    if(Ext.getCmp(PF+'cmbConceptos').getValue() == '')
							    {
							        NS.psConcepto = '%25';
							        NS.opcion1 = true;
						        }
							    else
							        NS.psConcepto = NS.descConcepto;
							        
	    
							    if(NS.tipoConceptos == true)
							    {
							    	var strParams = '?nomReporte=reporteconcepto11';
									strParams += '&'+'nomParam1=piEmpresaInf';
									strParams += '&'+'valParam1='+NS.empresainf;
									strParams += '&'+'nomParam2=piEmpresaSup';
									strParams += '&'+'valParam2='+NS.empresasup;
									strParams += '&'+'nomParam3=piBancoInf';
									strParams += '&'+'valParam3='+NS.lbanco_inf;
									strParams += '&'+'nomParam4=piBancoSup';
									strParams += '&'+'valParam4='+NS.lbanco_sup;
									strParams += '&'+'nomParam5=psChequera';
									strParams += '&'+'valParam5='+NS.l_valor;
									strParams += '&'+'nomParam6=pdFechaInf';
									strParams += '&'+'valParam6='+NS.dateinf;
									strParams += '&'+'nomParam7=pdFechaSup';
									strParams += '&'+'valParam7='+NS.datesup;
									strParams += '&'+'nomParam8=pbDetalle';
									strParams += '&'+'valParam8='+NS.chkDetalle;
									strParams += '&'+'nomParam9=titulo';
									strParams += '&'+'valParam9='+NS.psEmpresa;
									
									window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
									return;
	
							    }else
							    {
							    	
							    	var strParams2 = '?nomReporte=reportebanca1';
									strParams2 += '&'+'nomParam1=piEmpresaInf';
									strParams2 += '&'+'valParam1='+NS.empresainf;
									strParams2 += '&'+'nomParam2=piEmpresaSup';
									strParams2 += '&'+'valParam2='+NS.empresasup;
									strParams2 += '&'+'nomParam3=piBancoInf';
									strParams2 += '&'+'valParam3='+NS.lbanco_inf;
									strParams2 += '&'+'nomParam4=piBancoSup';
									strParams2 += '&'+'valParam4='+NS.lbanco_sup;
									strParams2 += '&'+'nomParam5=psChequera';
									strParams2 += '&'+'valParam5='+NS.l_valor;
									strParams2 += '&'+'nomParam6=pdFechaInf';
									strParams2 += '&'+'valParam6='+NS.dateinf;
									strParams2 += '&'+'nomParam7=pdFechaSup';
									strParams2 += '&'+'valParam7='+NS.datesup;
									strParams2 += '&'+'nomParam8=pbMov';
									strParams2 += '&'+'valParam8='+NS.movimientoCapturado;
									strParams2 += '&'+'nomParam9=psTipoMov';
									strParams2 += '&'+'valParam9='+NS.tipoMov;
									strParams2 += '&'+'nomParam10=pvOrigenMov';
									strParams2 += '&'+'valParam10='+NS.origenMov;
									strParams2 += '&'+'nomParam11=psConcepto';
									strParams2 += '&'+'valParam11='+NS.psConcepto;
									strParams2 += '&'+'nomParam12=pbOpcion1';
									strParams2 += '&'+'valParam12='+NS.opcion1;
									strParams2 += '&'+'nomParam13=psMovtosTEF';
									strParams2 += '&'+'valParam13='+NS.pb_movtoTEF;
									strParams2 += '&'+'nomParam14=psMovtosDia';
									strParams2 += '&'+'valParam14='+NS.pb_movtoDia;
									strParams2 += '&'+'nomParam15=psMovtoAutomata';
									strParams2 += '&'+'valParam15='+NS.pb_movotoAutomata;
									strParams2 += '&'+'nomParam16=titulo';
									strParams2 += '&'+'valParam16='+NS.psEmpresa;
									
									window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams2);
									
							    }
								}
	       					}
	  					}
					}
	        ]
	        }
        ]
        
    });
    
    //funciones//
    function cambiarFecha(fecha)
	{	
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";
		mesArreglo[8]="Sep";mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
			var mes=i+1;
			if(mes<10)
				var mes='0'+mes;
			}
		}
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;
	}
	
 	NS.contenedorMovimientos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
});

  