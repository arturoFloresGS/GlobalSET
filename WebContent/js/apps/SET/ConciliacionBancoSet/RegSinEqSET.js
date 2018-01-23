/*
@author: Jessica Arelly Cruz Cruz
@since: 08/11/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.ConciliacionBancoSet.RegSinEqSET');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	NS.GI_USUARIO = apps.SET.iUserId ;
	NS.GI_ID_CAJA = apps.SET.ID_CAJA;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	
	NS.iIdBanco = 0;
	NS.sIdChequera = '';
	NS.sFecIni = '';
	NS.sFecFin = '';
	NS.sEstatus = '';
	NS.uMontoIni = 0;
	NS.uMontoFin = 0;
	NS.sCargoAbono = '';
	NS.iFormaPago = 0;
	NS.noEmpresa = NS.GI_ID_EMPRESA;
	
	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
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
        ,x: 115
        ,y: 0
        ,width: 270
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		//,value: NS.GI_NOM_EMPRESA
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
		baseParams:{empresa: NS.GI_ID_EMPRESA},
		paramOrder:['empresa'],
		directFn: ConciliacionBancoSetAction.llenarComboBancos,
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
	
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeCmbBanco
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 50
        ,y: 100
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.agregarCriterioValor('BANCO', combo.getId(), 'valor');
					NS.storeCmbChequera.baseParams.banco = combo.getValue();
				}
			}
		}
	});
	
	NS.storeCmbChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			banco: 0,
			empresa: NS.GI_ID_EMPRESA,
			opcion: 0},
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
        ,x: 50
        ,y: 100
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.agregarCriterioValor('CHEQUERA', combo.getId(), 'valor');
				}
			}
		}
	});
	
	NS.storeCmbEstatus = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{bCancelacion: false},
		paramOrder:['bCancelacion'],
		directFn: ConciliacionBancoSetAction.consultarEstatus,
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
	
	NS.cmbEstatus = new Ext.form.ComboBox({
		store: NS.storeCmbEstatus
		,name: PF+'cmbEstatus'
		,id: PF+'cmbEstatus'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 50
        ,y: 100
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un estatus'
		,triggerAction: 'all'
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.agregarCriterioValor('ESTATUS', combo.getId(), 'valor');
				}
			}
		}
	});
	
	NS.dataCargoAbono = [
					  [ 'E', 'CARGO' ]
				     ,[ 'I', 'ABONO' ]
					  ];
  	NS.storeCargoAbono = new Ext.data.SimpleStore( {
		idProperty: 'id',  		
		fields : [ 
			{name :'id'}, 
			{name :'descripcion'}
	 	]
	});
	NS.storeCargoAbono.loadData(NS.dataCargoAbono);
	
	NS.cmbCargoAbono = new Ext.form.ComboBox({
		store: NS.storeCargoAbono
		,name: PF+'cmbCargoAbono'
		,id: PF+'cmbCargoAbono'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 50
        ,y: 100
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una opcion'
		,triggerAction: 'all'
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.agregarCriterioValor('CARGO/ABONO', combo.getId(), 'valor');
				}
			}
		}
	});
	
	NS.dataCriterio = [
					  [ '0', 'BANCO' ]
				     ,[ '1', 'CHEQUERA' ]
				     ,[ '2', 'FECHA' ]
				     ,[ '3', 'ESTATUS' ]
				     ,[ '4', 'MONTOS' ]
				     ,[ '5', 'CARGO/ABONO' ]
					  ];
  	NS.storeCriterio = new Ext.data.SimpleStore( {
		idProperty: 'idCriterio',  		
		fields : [ 
			{name :'idCriterio'}, 
			{name :'descripcion'}
	 	]
	});
	NS.storeCriterio.loadData(NS.dataCriterio);
	
	NS.cmbCriterio = new Ext.form.ComboBox({
		 store: NS.storeCriterio 	
		,name: PF+'cmbCriterio'
		,id: PF+'cmbCriterio'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 50
		,y: 60
		,width:170
		,valueField:'idCriterio'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un criterio'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn:function(combo, value) {
					if(Ext.getCmp(PF + 'cmbEmpresa').getValue() == ''){
						BFwrk.Util.msgShow('Debe seleccionar una empresa','WARNING');
						Ext.getCmp(PF + 'cmbCriterio').setValue('');
						return;
					}
					combo.setDisabled(true);
				    
				    if(combo.getValue() == 0)
					{
						//BANCO
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'cmbBanco').reset();
						NS.storeCmbBanco.load();
						Ext.getCmp(PF + 'cmbBanco').setDisabled(false);
						Ext.getCmp(PF + 'cmbBanco').setVisible(true);
						
						NS.agregarCriterioValor ('BANCO', Ext.getCmp(PF + 'cmbBanco').getId(), 'criterio');
					}
					else if(combo.getValue() == 1)
					{
						//CHEQUERA
						Ext.getCmp(PF + 'cmbChequera').reset();
						NS.storeCmbChequera.load();
						var indice=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(var i = 0; i < recordsDatGrid.length; i++)
						{
							if(recordsDatGrid[i].get('criterio')=='BANCO')
								indice = i;
						}
						if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='BANCO')
						{
							NS.ocultarComponentes();
							Ext.getCmp(PF + 'cmbChequera').setDisabled(false);
							Ext.getCmp(PF + 'cmbChequera').setVisible(true);
							
							NS.agregarCriterioValor ('CHEQUERA', Ext.getCmp(PF + 'cmbChequera').getId(), 'criterio');
						}
						else
						{
							BFwrk.Util.msgShow('Primero debe escoger un Banco','ERROR');
							combo.setDisabled(false);
							return;
						}
					}
					else if(combo.getValue() == 2)
					{
						//FECHA
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'txtFechaIni').reset();
						Ext.getCmp(PF + 'txtFechaFin').reset();
						Ext.getCmp(PF + 'txtFechaIni').setDisabled(false);
						Ext.getCmp(PF + 'txtFechaIni').setVisible(true);
						Ext.getCmp(PF + 'txtFechaFin').setDisabled(false);
						Ext.getCmp(PF + 'txtFechaFin').setVisible(true);
						
						NS.agregarCriterioValor ('FECHA', Ext.getCmp(PF + 'txtFechaIni').getId(), 'criterio');
					}
					else if(combo.getValue() == 3)
					{
						//ESTATUS
						Ext.getCmp(PF + 'cmbEstatus').reset();
						NS.storeCmbEstatus.load();
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'cmbEstatus').setDisabled(false);
						Ext.getCmp(PF + 'cmbEstatus').setVisible(true);
						
						NS.agregarCriterioValor ('ESTATUS', Ext.getCmp(PF + 'cmbEstatus').getId(), 'criterio');
					}
					else if(combo.getValue() == 4)
					{
						//MONTOS
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'txtMontoIni').reset();
						Ext.getCmp(PF + 'txtMontoFin').reset();
						Ext.getCmp(PF + 'txtMontoIni').setDisabled(false);
						Ext.getCmp(PF + 'txtMontoIni').setVisible(true);
						Ext.getCmp(PF + 'txtMontoFin').setDisabled(false);
						Ext.getCmp(PF + 'txtMontoFin').setVisible(true);
						
						NS.agregarCriterioValor ('MONTOS', Ext.getCmp(PF + 'txtMontoIni').getId(), 'criterio');
					}
					else if(combo.getValue() == 5)
					{
						//CARGO/ABONO
						Ext.getCmp(PF + 'cmbCargoAbono').reset();
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'cmbCargoAbono').setDisabled(false);
						Ext.getCmp(PF + 'cmbCargoAbono').setVisible(true);
						
						NS.agregarCriterioValor ('CARGO/ABONO', Ext.getCmp(PF + 'cmbCargoAbono').getId(), 'criterio');
					}
				}
			}
		}
	});
	
	//store del grid criterios
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'criterio',
		fields: [
			{name: 'id'},
			{name: 'criterio'},
			{name: 'valor'}
		]
	}); 
	
	//grid criterios
	NS.gridDatos  = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		id: PF+'gridDatos',
		name: PF+'gridDatos',
		width: 270,
       	height: 110,
		x :400,
		y :0,
		frame: true,
		title :'',
		columns : [ 
		{
			id :'id', header :'Id', width :10, dataIndex :'id', hidden: true
		},
		{
			header :'Criterio',width :115,dataIndex :'criterio'
		}, 
		{
			header :'Valor',width :135,dataIndex :'valor'
		} ],
		listeners:{
			dblclick:{
				fn:function(grid){
					var records = NS.gridDatos.getSelectionModel().getSelections();
					for(var i=0;i<records.length;i = i + 1)
					{
						NS.gridDatos.store.remove(records[i]);
						NS.gridDatos.getView().refresh();
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
					}
				}
			}
		}
	});
	
	NS.storeGridBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdBanco: 0,
			sIdChequera: '',
			sFecIni: '',
			sFecFin: '',
			sEstatus: '',
			uMontoIni: '',
			uMontoFin: '',
			sCargoAbono: '',
			iNoEmpresa: NS.GI_ID_EMPRESA
		},
		root: '',
		paramOrder:['iIdBanco','sIdChequera','sFecIni','sFecFin','sEstatus','uMontoIni','uMontoFin','sCargoAbono', 'iNoEmpresa'],
		directFn: ConciliacionBancoSetAction.llenarMovsDuplicados, 
		fields: [
			{name: 'idEstatuscb'},
			{name: 'fecOperacion'},
			{name: 'cargoAbono'},
			{name: 'importe'},
			{name: 'referencia'},
			{name: 'noCheque'},
			{name: 'concepto'},
			{name: 'secuencia'}
		],
		listeners: {
			load: function(s, records){
				BFwrk.Util.msgWait('Eliminando...', false);
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridBanco, msg:"Buscando..."});
				Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
				if(records.length == 0)
				{
					BFwrk.Util.msgShow('No se encontraron movimientos','INFO');
					return;
				}
			}
		}
	}); 
	
	NS.selectionModel = new Ext.grid.CheckboxSelectionModel({
		checkOnly: true 
	});
	
	NS.gridMovsBanco  = new Ext.grid.GridPanel({
		store : NS.storeGridBanco,
		id: PF+'gridMovsBanco',
		name: PF+'gridMovsBanco',
		title: 'Movimientos del Banco',
		width: 700,
       	height: 200,
		x :0,
		y :170,
		frame: true,
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
		columns : [ NS.selectionModel,
		{
			header :'Estatus',width :50,dataIndex :'idEstatuscb'
		},{
			header :'Fecha',width :100,dataIndex :'fecOperacion'
		},{
			header :'C/A',width :30,dataIndex :'cargoAbono'
		},{
			header :'Importe',width :100,dataIndex :'importe', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Referencia',width :80,dataIndex :'referencia'
		},{
			header :'No. Cheque',width :80,dataIndex :'noCheque'
		},{
			header :'Concepto',width :180,dataIndex :'concepto'
		},{
			header :'Secuencia',width :80,dataIndex :'secuencia', hidden: true
		}]}),
		sm: NS.selectionModel,
		listeners:{
			dblclick:{
				fn:function(grid){
					
				}
			}
		}
	});
	
	NS.contenedorMovsBanco = new Ext.FormPanel({
    title: 'Movimientos Bancarios sin Equivalencia en SET',
    width: 700,
    height: 477,
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
                height: 440,
                width: 725,
                items: [
                    {
                        xtype: 'fieldset',
                        title: '',
                        x: 0,
                        y: 0,
                        width: 700,
                        height: 160,
                        layout: 'absolute',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa:',
                                x: 0,
                                y: 5
                            },
                            {
                                xtype: 'textfield',
                                id: PF+'txtEmpresa',
                                name: PF+'txtEmpresa',
//                                value: NS.GI_ID_EMPRESA,
                                x: 55,
                                y: 0,
                                width: 50,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
		                        			NS.accionarCmbEmpresa(comboValue);
		                        		}
		                        	}
		                        }
                            },
                            NS.cmbEmpresa,
                            {
                                xtype: 'fieldset',
                                title: 'Selecci\u00f3n',
                                x: 0,
                                y: 50,
                                width: 120,
                                height: 70,
                                hidden:true,
                                layout: 'absolute',
                                items: [
                                    {
                                        xtype: 'radio',
                                        x: 0,
                                        y: 0,
                                        checked: true,
                                        boxLabel: 'Movimientos del Banco'
                                    }
                                ]
                            },
                            {
                                xtype: 'label',
                                text: 'Criterio:',
                                x: 0,
                                y: 60
                            },
                            {
                                xtype: 'label',
                                text: 'Valor:',
                                x: 0,
                                y: 100
                            },
                            NS.cmbCriterio,
                            NS.cmbBanco,
                            NS.cmbChequera,
                            NS.cmbEstatus,
                            NS.cmbCargoAbono,
                            {
                                xtype: 'datefield',
                                id: PF+'txtFechaIni',
                                name: PF+'txtFechaIni',
                                format: 'd/m/Y',
                                hidden: true,
                                x: 50,
                                y: 100,
                                width: 100,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			NS.agregarCriterioValor('FECHA', caja.getId(), 'valor');
		                        		}
		                        	}
		                        }
                            },
                            {
                                xtype: 'datefield',
                                id: PF+'txtFechaFin',
                                name: PF+'txtFechaFin',
                                format: 'd/m/Y',
                                hidden: true,
                                x: 160,
                                y: 100,
                                width: 100,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var fechaIni = Ext.getCmp(PF + 'txtFechaIni').getValue();
                                			if(fechaIni > caja.getValue())
                                			{
                                				BFwrk.Util.msgShow('La fecha inicial no puede ser mayor', 'ERROR');
                                				Ext.getCmp(PF + 'txtFechaIni').setDisabled(true);
                                				Ext.getCmp(PF + 'txtFechaFin').setDisabled(true);
                                				return;
                                			}
                                			NS.agregarCriterioValor('FECHA', caja.getId(), 'valor');
		                        		}
		                        	}
		                        }
                            },
                            {
                                xtype: 'textfield',
                                id: PF+'txtMontoIni',
                                name: PF+'txtMontoIni',
                                maskRe: /[0-9.]/,
                                hidden: true,
                                x: 50,
                                y: 100,
                                width: 100,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			Ext.getCmp(PF + 'txtMontoIni').setValue(BFwrk.Util.formatNumber(caja.getValue()));
					        				NS.agregarCriterioValor('MONTOS', caja.getId(), 'valor');
		                        		}
		                        	}
		                        }
                            },
                            {
                                xtype: 'textfield',
                                id: PF+'txtMontoFin',
                                name: PF+'txtMontoFin',
                                maskRe: /[0-9.]/,
                                hidden: true,
                                x: 160,
                                y: 100,
                                width: 100,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var valorUno=BFwrk.Util.formatNumber(Ext.getCmp(PF+'txtMontoIni').getValue());
		                        			Ext.getCmp(PF + 'txtMontoFin').setValue(BFwrk.Util.formatNumber(caja.getValue()));
					        				if(parseFloat(valorUno) > parseFloat(valor))
											{
												BFwrk.Util.msgShow('El importe dos debe ser mayor que el importe uno', 'ERROR');
												Ext.getCmp(PF+'txtMontoIni').setValue('');
												Ext.getCmp(PF+'txtMontoIni').setDisabled(true);
												Ext.getCmp(PF+'txtMontoFin').setDisabled(true);
												Ext.getCmp(PF+'txtMontoFin').setValue('');
												return;
											}
					        				NS.agregarCriterioValor('MONTOS', caja.getId(), 'valor');
		                        		}
		                        	}
		                        }
                            },
                            {
                                xtype: 'button',
                                text: 'Buscar',
                                id: PF+'cmdBuscar',
	                                name: PF+'cmdBuscar',
                                x: 600,
                                y: 115,
                                width: 70,
                                listeners:{
			               			click:{
			           			   		fn:function(e){
				            			   	var indice=0;
				            			   	var indiceBanco = -1;
											var recordsDatGrid=NS.storeDatos.data.items;
											
											if(Ext.getCmp(PF + 'cmbEmpresa').getValue() == ''){
												BFwrk.Util.msgShow('Debe seleccionar una empresa','WARNING');
												return;
											}

											
											for(var i = 0; i < recordsDatGrid.length; i++)
											{
												if(recordsDatGrid[i].get('criterio')=='CHEQUERA')
													indice = i;
												else if(recordsDatGrid[i].get('criterio')=='BANCO')
													indiceBanco = i;
											}
											
											if(indiceBanco < 0){
												BFwrk.Util.msgShow('Debe indicar el banco en los criterios de b\u00fasqueda','WARNING');
												return;
											}
											
											if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='CHEQUERA')
											{
												NS.buscar();
												var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridBanco, msg:"Buscando..."});
												NS.storeGridBanco.load();
											}
											else
											{
												BFwrk.Util.msgShow('Debe indicar la chequera en los criterios de b\u00fasqueda','WARNING');
												return;
											}
			           			   		}
			        			   	}
			    			   	}
                            },
                            
                            NS.gridDatos
                        ]
                    },
                    NS.gridMovsBanco,
                    {
                        xtype: 'button',
                        text: 'Imprimir',
                        x: 620,
                        y: 385,
                        width: 70,
                        tooltip: 'Movimientos Duplidados Bancarios',
                        listeners:{
			           		click:{
			           			fn:function(){
			           				var fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
				        			strParams = '?nomReporte=ReporteMovsDuplicados';
									strParams += '&'+'nomParam1=empresa';
									strParams += '&'+'valParam1='+ NS.noEmpresa;
									strParams += '&'+'nomParam2=nomEmpresa';
									strParams += '&'+'valParam2='+BFwrk.Util.scanFieldsStore(NS.storeCmbEmpresa, 'id', NS.noEmpresa, 'descripcion');
									strParams += '&'+'nomParam3=subtitulo';
									strParams += '&'+'valParam3='+'Conciliacion Banco SET al ' + 
										fecHoy.getDate() + ' DE ' + BFwrk.Util.getNameMonth(fecHoy.getMonth() + 1) + ' DE ' + fecHoy.getFullYear();;
									window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
		           				}
			           		}
			           	}
                    },
                    {
                        xtype: 'button',
                        text: 'Ejecutar',
                        x: 540,
                        y: 385,
                        width: 70,
                        listeners:{
							click:{
								fn:function(){
										var regSelecBanco=NS.gridMovsBanco.getSelectionModel().getSelections();
										var matrizBanco = new Array ();
                      					var matrizCriterios = new Array ();
                      					
                      					if(regSelecBanco.length <= 0)
                      					{
                      						BFwrk.Util.msgShow('Es necesario seleccionar alg\u00fan registro','INFO');
											return;
                      					}
                      					else
                      					{
										if(Ext.getCmp(PF + 'txtObservacion').getValue() === '')
										{
											BFwrk.Util.msgShow('Es necesario especificar el motivo por el cual se esta eliminando el registro','INFO');
											return;
										}
										else
										{
											BFwrk.Util.msgWait('Eliminando...', true);
											for(var i = 0; i < regSelecBanco.length; i ++)
											{
												var banco = {};
												banco.idEstatuscb = regSelecBanco[i].get('idEstatuscb');
												banco.fecOperacion = regSelecBanco[i].get('fecOperacion');
												banco.cargoAbono = regSelecBanco[i].get('cargoAbono');
												banco.importe = regSelecBanco[i].get('importe');
												banco.referencia = regSelecBanco[i].get('referencia');
												banco.secuencia = regSelecBanco[i].get('secuencia');
												banco.noCheque = regSelecBanco[i].get('noCheque');
												banco.concepto = regSelecBanco[i].get('concepto');
												matrizBanco[i] = banco;
											}
											
											var criterio = {};
											criterio.observacion = Ext.getCmp(PF + 'txtObservacion').getValue();
											criterio.noEmpresa = NS.noEmpresa;
											matrizCriterios[0] = criterio; 
											
											var jsonStringBanco = Ext.util.JSON.encode(matrizBanco);
											var jsonStringCriterios = Ext.util.JSON.encode(matrizCriterios);
										
											ConciliacionBancoSetAction.ejecutarMovsDuplicados(jsonStringBanco, jsonStringCriterios, function(result, e){
												if(result.msgUsuario!==null  &&  result.msgUsuario!=='' && result.msgUsuario!= undefined)
												{
													BFwrk.Util.msgShow('' + result.msgUsuario ,'INFO');
													Ext.getCmp(PF + 'txtObservacion').reset();
													NS.storeGridBanco.load();
												}
											});
										}
									}
								}
							}
						}
                    },
                    {
                        xtype: 'label',
                        text: 'Observaciones:',
                        x: 4,
                        y: 378
                    },
                    {
                        xtype: 'textarea',
                        anchor: '60%',
                        id: PF + 'txtObservacion',
                        name: PF + 'txtObservacion',
                        x: 100,
                        y: 377,
                        height: 40,
                        width: 150,
                        emptyText: 'Describa el motivo de cancelación'
                    }
                ]
            }
           ]
	});
	
	NS.accionarCmbEmpresa = function(comboValor)
	{
		NS.storeCmbChequera.baseParams.empresa = comboValor;
		NS.storeGridBanco.baseParams.iNoEmpresa = comboValor;
		NS.storeCmbBanco.baseParams.empresa = comboValor;
		NS.noEmpresa = comboValor;
		NS.storeDatos.removeAll();
		NS.gridDatos.getView().refresh();
		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}
	
	NS.agregarCriterioValor = function(sValor, oIdElemento, sTipoAgregado){
		var indice = 0;
		var i = 0;
		var recordsDatGrid = NS.storeDatos.data.items;
		var sValorAnterior = '';
		
		for(i = 0; i < recordsDatGrid.length; i = i + 1)
		{
			if(recordsDatGrid[i].get('criterio') == sValor)
			{
				indice = i;
				sValorAnterior = recordsDatGrid[i].get('valor');
			}
		}
		
		if(sTipoAgregado == 'criterio')
		{
			if(recordsDatGrid.length > 0)
			{
				if((indice > 0 || (recordsDatGrid[0].get('criterio') == sValor)) && sValorAnterior !== '')
				{
					BFwrk.Util.msgShow('Ya indicó el criterio, necesita borralo antes','WARNING');
					Ext.getCmp(oIdElemento).setDisabled(true);
					return;
				}
				else
					NS.agregarValorGridDatos(sValor, oIdElemento, 0, sTipoAgregado);
			}
			else
				NS.agregarValorGridDatos(sValor, oIdElemento, 0, sTipoAgregado);
		}
		else if(sTipoAgregado == 'valor')
		{	
			NS.storeDatos.remove(recordsDatGrid[indice]);
			NS.agregarValorGridDatos(sValor, oIdElemento, indice, sTipoAgregado, sValorAnterior);
		}
	};
	
	NS.agregarValorGridDatos = function(sValor, oIdElemento, indice, sTipoAgregado, sValorAnterior){
		var valorCombo = Ext.getCmp(oIdElemento).getValue();
		var tamGrid = indice <= 0 ? (NS.storeDatos.data.items).length : indice;
		var datosClase = NS.gridDatos.getStore().recordType;
		
		if(sTipoAgregado == 'valor')
		{
			Ext.getCmp(PF + 'cmbCriterio').setDisabled(false);
			Ext.getCmp(oIdElemento).setDisabled(true);
		}
			var datos = new datosClase({
				id: valorCombo,
               	criterio: sValor,
				valor: (sValorAnterior !== undefined && sValorAnterior !== '' ? sValorAnterior + ' a ' : '')   + (sTipoAgregado == 'criterio' ? '' : $('input[id*="'+ oIdElemento +'"]').val())//sCadenaValor
           	});
            NS.gridDatos.stopEditing();
       		NS.storeDatos.insert(tamGrid, datos);
       		NS.gridDatos.getView().refresh();
	};
	
	NS.ocultarComponentes = function()
	{
		Ext.getCmp(PF+'cmbBanco').setVisible(false);
		Ext.getCmp(PF+'cmbChequera').setVisible(false);
		Ext.getCmp(PF+'cmbEstatus').setVisible(false);
		Ext.getCmp(PF+'cmbCargoAbono').setVisible(false);
		Ext.getCmp(PF+'txtFechaIni').setVisible(false);
		Ext.getCmp(PF+'txtFechaFin').setVisible(false);
		Ext.getCmp(PF+'txtMontoIni').setVisible(false);
		Ext.getCmp(PF+'txtMontoFin').setVisible(false);
	}
	
	NS.buscar = function(){
	 	NS.limpiarParams();
    	var datosBusqueda = NS.storeDatos.data.items;

   		Ext.getCmp(PF + 'cmdBuscar').setDisabled(true);
   		for(var i = 0; i < datosBusqueda.length; i = i + 1)
   		{
   			if(datosBusqueda[i].get('criterio') == 'BANCO')
   			{
   				if(datosBusqueda[i].get('id') !== '')
   				{
   					NS.storeGridBanco.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
   					NS.iIdBanco = parseInt(datosBusqueda[i].get('id'));
   				}
   				else
   				{
   					BFwrk.Util.msgShow('Debe dar un valor a BANCO', 'INFO');
   					return;
   				}
   			}
   			else if(datosBusqueda[i].get('criterio') == 'CHEQUERA')
   			{
   				if(datosBusqueda[i].get('id') !== '')
   				{
					NS.storeGridBanco.baseParams.sIdChequera = datosBusqueda[i].get('id');
					NS.sIdChequera = datosBusqueda[i].get('id');    				
   				}
   				else
   				{
   					BFwrk.Util.msgShow('Debe dar un valor a CHEQUERA', 'INFO');
   					return;
   				}
   			}
   			else if(datosBusqueda[i].get('criterio') == 'FECHA')
   			{
   				var valorFechas=datosBusqueda[i].get('valor');
   				if(datosBusqueda[i].get('id') !== '')
   				{
   					var ini=NS.obtenerValIni(valorFechas);
					var fin=NS.obtenerValFin(valorFechas);
	   				NS.storeGridBanco.baseParams.sFecIni = ini;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
	   				NS.storeGridBanco.baseParams.sFecFin = fin;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
	   				NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
	   				NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
   				}
   				else
   				{
   					BFwrk.Util.msgShow('Debe dar un valor a FECHAS', 'INFO');
   					return;
   				}
   				
   			}
   			else if(datosBusqueda[i].get('criterio') == 'ESTATUS')
   			{
   				if(datosBusqueda[i].get('id') !== '')
   				{
   					NS.storeGridBanco.baseParams.sEstatus = datosBusqueda[i].get('id');
   					NS.sEstatus = datosBusqueda[i].get('id');
   				}
   				else
   				{
   					BFwrk.Util.msgShow('Debe dar un valor a ESTATUS', 'INFO');
   					return;
   				}
   			}
   			else if(datosBusqueda[i].get('criterio') == 'MONTOS')
   			{
   				var valorImporte=datosBusqueda[i].get('valor');
   				if(datosBusqueda[i].get('id') !== '')
   				{
   					var ini=NS.obtenerValIni(valorImporte);
					var fin=NS.obtenerValFin(valorImporte);
   					NS.storeGridBanco.baseParams.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
   					NS.storeGridBanco.baseParams.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
   					NS.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
   					NS.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
   				}
   				else
   				{
   					BFwrk.Util.msgShow('Debe dar un valor a MONTOS', 'INFO');
   					return;
   				}
   			}
   			else if(datosBusqueda[i].get('criterio') == 'CARGO/ABONO')
   			{
   				if(datosBusqueda[i].get('id') !== '')
   				{
   					NS.storeGridBanco.baseParams.sCargoAbono = datosBusqueda[i].get('id');
   					NS.sCargoAbono = datosBusqueda[i].get('id');
   				}
   				else
   				{
   					BFwrk.Util.msgShow('Debe dar un valor a CARGO/ABONO', 'INFO');
   					return;
   				}
   			}
   			else if(datosBusqueda[i].get('criterio') == 'FORMA DE PAGO')
   			{
   				if(datosBusqueda[i].get('id') !== '')
   				{
   					NS.storeGridBanco.baseParams.iFormaPago = parseInt(datosBusqueda[i].get('id'));
   					NS.iFormaPago = parseInt(datosBusqueda[i].get('id'));
   				}
   				else
   				{
   					BFwrk.Util.msgShow('Debe dar un valor a FORMA DE PAGO', 'INFO');		
		    		return;
   				}
   			}
   		}
    };
    
    NS.obtenerValIni = function(valor)
	{
		var i=0;
		var valIni='';
		while(valor.charAt(i)!='a')
		{
			valIni+=valor.charAt(i);
			i++;
		}
		valIni.replace(",","");
		return valIni;
	}
	
	NS.obtenerValFin = function(valor)
	{
		var i=0;
		var valFin='';
		while(i<valor.length)
		{
			if(valor.charAt(i)=='a')
			{
				valFin=valor.substring(i+1,valor.length);
			}
			i++;
		}
		valFin.replace(",","");
		return valFin;
		
	}
	
	NS.limpiarParams = function()
	{
		NS.storeGridBanco.baseParams.iIdBanco= 0;
		NS.storeGridBanco.baseParams.sIdChequera= '';
		NS.storeGridBanco.baseParams.sFecIni= '';
		NS.storeGridBanco.baseParams.sFecFin= '';
		NS.storeGridBanco.baseParams.sEstatus= '';
		NS.storeGridBanco.baseParams.uMontoIni= '';
		NS.storeGridBanco.baseParams.uMontoFin= '';
		NS.storeGridBanco.baseParams.sCargoAbono= '';
		//NS.storeGridBanco.baseParams.iNoEmpresa= NS.GI_ID_EMPRESA;
	}

	NS.contenedorMovsBanco.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
