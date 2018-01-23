/*
@author: Jessica Arelly Cruz Cruz
@since: 31/10/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Conciliacion.Cancelacion');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.NOMBRE_HOST = apps.SET.HOST_NAME_LOCAL;


	NS.iIdBanco = 0;
	NS.sIdChequera = '';
	NS.sFecIni = '';
	NS.sFecFin = '';
	NS.sEstatus = '';
	NS.uMontoIni = 0;
	NS.uMontoFin = 0;
	NS.sCargoAbono = '';
	NS.iFormaPago = 0;
	NS.iGrupoIni = 0;
	NS.iGrupoFin = 0;
	NS.noEmpresa = NS.GI_ID_EMPRESA;
	
	//Para evitar el desface de los grids
	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
	NS.inicializaCajas = function(){
		Ext.getCmp(PF+'txtCantCargoBanco').setValue(0);
		Ext.getCmp(PF+'txtCargoBanco').setValue(BFwrk.Util.formatNumber(0.0));
		Ext.getCmp(PF+'txtAbonoBanco').setValue(BFwrk.Util.formatNumber(0.0));
		Ext.getCmp(PF+'txtCantAbonoBanco').setValue(0);
		Ext.getCmp(PF+'txtTotalBanco').setValue(BFwrk.Util.formatNumber(0.0));
		
		Ext.getCmp(PF+'txtCantCargoEmpresa').setValue(0);
		Ext.getCmp(PF+'txtCargoEmpresa').setValue(BFwrk.Util.formatNumber(0.0));
		Ext.getCmp(PF+'txtCantAbonoEmpresa').setValue(0);
		Ext.getCmp(PF+'txtAbonoEmpresa').setValue(BFwrk.Util.formatNumber(0.0));
		Ext.getCmp(PF+'txtTotalEmpresa').setValue(BFwrk.Util.formatNumber(0.0));
	}
	
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
        ,x: 117
        ,y: 0
        ,width: 280
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
//		,value: NS.GI_NOM_EMPRESA
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
        ,x: 238
        ,y: 100
        ,width: 160
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
				    NS.storeCmbChequera.baseParams.opcion = NS.opcionSel();
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
        ,x: 238
        ,y: 100
        ,width: 160
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
		baseParams:{bCancelacion: true},
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
        ,x: 238
        ,y: 100
        ,width: 160
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
        ,x: 238
        ,y: 100
        ,width: 160
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
	
	NS.storeFormaPago = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_forma_pago',
			campoDos:'desc_forma_pago',
			tabla:'cat_forma_pago',
			condicion:'',
			orden:'2'
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		directFn: GlobalAction.llenarComboGeneral, 
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
	
	NS.cmbFormaPago = new Ext.form.ComboBox({
		store: NS.storeFormaPago
		,name: PF+'cmbFormaPago'
		,id: PF+'cmbFormaPago'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 238
        ,y: 100
        ,width: 160
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una forma de pago'
		,triggerAction: 'all'
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.agregarCriterioValor('FORMA DE PAGO', combo.getId(), 'valor');
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
//				     ,[ '6', 'FORMA DE PAGO' ]
				     ,[ '7', 'GRUPO' ]
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
		,x: 238
		,y: 50
		,width:160
		,valueField:'idCriterio'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un criterio'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn:function(combo, value) {
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
					else if(combo.getValue() == 6)
					{
						//FORMA DE PAGO
						Ext.getCmp(PF + 'cmbFormaPago').reset();
						NS.storeFormaPago.load();
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'cmbFormaPago').setDisabled(false);
						Ext.getCmp(PF + 'cmbFormaPago').setVisible(true);
						
						NS.agregarCriterioValor ('FORMA DE PAGO', Ext.getCmp(PF + 'cmbFormaPago').getId(), 'criterio');
					}
					else if(combo.getValue() == 7)
					{
						//GRUPO
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'txtGrupoIni').reset();
						Ext.getCmp(PF + 'txtGrupoFin').reset();
						Ext.getCmp(PF + 'txtGrupoIni').setDisabled(false);
						Ext.getCmp(PF + 'txtGrupoIni').setVisible(true);
						Ext.getCmp(PF + 'txtGrupoFin').setDisabled(false);
						Ext.getCmp(PF + 'txtGrupoFin').setVisible(true);
						
						NS.agregarCriterioValor ('GRUPO', Ext.getCmp(PF + 'txtGrupoIni').getId(), 'criterio');
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
		width: 290,
       	height: 110,
		x :450,
		y :0,
		frame: true,
		title :'',
		columns : [ 
		{
			id :'id', header :'Id', width :10, dataIndex :'id', hidden: true
		},
		{
			header :'Criterio',width :120,dataIndex :'criterio'
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
	
	NS.storeMovimientosBanco = new Ext.data.DirectStore({
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
			iFormaPago: 0,
			iNoEmpresa: 0,
			iGrupoIni: 0,
			iGrupoFin: 0
		},
		root: '',
		paramOrder:['iIdBanco','sIdChequera','sFecIni','sFecFin','sEstatus','uMontoIni','uMontoFin','sCargoAbono','iFormaPago', 'iNoEmpresa','iGrupoIni','iGrupoFin'],
		directFn: ConciliacionBancoSetAction.llenarMovsBancoCancelacion, 
		fields: [
			{name: 'grupo'},
			{name: 'idEstatuscb'},
			{name: 'fecOperacion'},
			{name: 'cargoAbono'},
			{name: 'importe'},
			{name: 'referencia'},
			{name: 'secuencia'},
			{name: 'noCheque'},
			{name: 'concepto'},
			{name: 'noEmpresa'},
			{name: 'idBanco'},
			{name: 'idChequera'},
			{name: 'exportado'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosBanco, msg:"Buscando..."});
				//Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
				//BFwrk.Util.msgWait('Cancelando...', false);
				if(records.length == 0)
					BFwrk.Util.msgShow('No se encontraron movimientos del banco para cancelar','INFO');
				/*else
				{
					var cont = records.length;
					var uImpCargo = 0;
					var iMovCargo = 0;
					var uImpAbono = 0;
					var iMovAbono = 0;
					var uTotal = 0;
					
					for(var i = 0; i < cont; i = i + 1)
					{
						if(records[i].get('cargoAbono') === 'C')
						{
							uImpCargo = uImpCargo + parseFloat(records[i].get('importe'));
							iMovCargo = iMovCargo + 1;
						}
			            else
			            {
			            	uImpAbono =  uImpAbono + parseFloat(records[i].get('importe'));
			            	iMovAbono = iMovAbono + 1;
			            }
		            }
		            
		            uTotal = uImpAbono - uImpCargo;
		            Ext.getCmp(PF+'txtCantCargoBanco').setValue(iMovCargo);
		            Ext.getCmp(PF+'txtCargoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargo)*100)/100));
		            Ext.getCmp(PF+'txtCantAbonoBanco').setValue(iMovAbono);
		            Ext.getCmp(PF+'txtAbonoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbono)*100)/100));
		            Ext.getCmp(PF+'txtTotalBanco').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));
				}*/
			}
		}
	}); 
	
	NS.smBan = new Ext.grid.CheckboxSelectionModel({
		//checkOnly: true
		singleSelect: true,
		listeners:{
			rowselect: {	
				fn:function(r, rowIndex){
					NS.smEmp.unlock();
					NS.marcarGrupoBancos();
					var registrosGrid = NS.gridMovsBanco.getSelectionModel().getSelections();
					var regBancoItem = NS.storeMovimientosBanco.data.items;
					var uImpCargo = 0;
					var iMovCargo = 0;
					var uImpAbono = 0;
					var iMovAbono = 0;
					var uTotal = 0;
									for(var i = 0; i < regBancoItem.length; i++)
									{
										if(regBancoItem[i].get('grupo')==registrosGrid[0].get('grupo')){
											if(regBancoItem[i].get('cargoAbono') == 'C')
											{
												uImpCargo = uImpCargo + parseFloat(regBancoItem[i].get('importe'));
												iMovCargo = iMovCargo + 1;
											}
								            else
								            {
								            	uImpAbono =  uImpAbono + parseFloat(regBancoItem[i].get('importe'));
								            	iMovAbono = iMovAbono + 1;
								            }
							            }
									}
									uTotal = uImpAbono - uImpCargo;
								    Ext.getCmp(PF+'txtCantCargoBanco').setValue(iMovCargo);
								    Ext.getCmp(PF+'txtCargoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargo)*100)/100));
								    Ext.getCmp(PF+'txtCantAbonoBanco').setValue(iMovAbono);
								    Ext.getCmp(PF+'txtAbonoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbono)*100)/100));
								    Ext.getCmp(PF+'txtTotalBanco').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));
								    
					   var registrosGridEmp = NS.gridMovsEmpresa.getSelectionModel().getSelections();		 
					   var uImpCargoEmp = 0;
					   var iMovCargoEmp = 0;
					   var uImpAbonoEmp = 0;
					   var iMovAbonoEmp = 0;
					   var uTotalEmp = 0;
					
					   				for(var i = 0; i < registrosGridEmp.length; i++)
									{
					   					if(registrosGridEmp[i].get('idTipoMovto') == 'C')
										{
					   						uImpCargoEmp = uImpCargoEmp + parseFloat(registrosGridEmp[i].get('importe'));
					   						iMovCargoEmp = iMovCargoEmp + 1;
										}
										else
										{
											uImpAbonoEmp = uImpAbonoEmp + parseFloat(registrosGridEmp[i].get('importe'));
											iMovAbonoEmp = iMovAbonoEmp + 1;
										}
									}  
									uTotalEmp = uImpAbonoEmp - uImpCargoEmp;
									Ext.getCmp(PF+'txtCantCargoEmpresa').setValue(iMovCargoEmp);
									Ext.getCmp(PF+'txtCargoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargoEmp)*100)/100));
									Ext.getCmp(PF+'txtCantAbonoEmpresa').setValue(iMovAbonoEmp);
									Ext.getCmp(PF+'txtAbonoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbonoEmp)*100)/100));
									Ext.getCmp(PF+'txtTotalEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uTotalEmp)*100)/100));
									NS.smEmp.lock();
				}
			},
			rowdeselect: {
				fn:function(r, rowIndex){
					NS.smEmp.unlock();
					NS.smEmp.clearSelections();
					var registrosGrid = NS.gridMovsBanco.getSelectionModel().getSelections();
					var regBancoItem = NS.storeMovimientosBanco.data.items;
					var uImpCargo = 0;
					var iMovCargo = 0;
					var uImpAbono = 0;
					var iMovAbono = 0;
					var uTotal = 0;
									for(var i = 0; i < registrosGrid.length; i++)
									{
											if(registrosGrid[i].get('cargoAbono') == 'C')
											{
												uImpCargo = uImpCargo + parseFloat(registrosGrid[i].get('importe'));
												iMovCargo = iMovCargo + 1;
											}
								            else
								            {
								            	uImpAbono =  uImpAbono + parseFloat(registrosGrid[i].get('importe'));
								            	iMovAbono = iMovAbono + 1;
								            }
							            }
									uTotal = uImpAbono - uImpCargo;
								    Ext.getCmp(PF+'txtCantCargoBanco').setValue(iMovCargo);
								    Ext.getCmp(PF+'txtCargoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargo)*100)/100));
								    Ext.getCmp(PF+'txtCantAbonoBanco').setValue(iMovAbono);
								    Ext.getCmp(PF+'txtAbonoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbono)*100)/100));
								    Ext.getCmp(PF+'txtTotalBanco').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));
								    
				    var registrosGridEmp = NS.gridMovsEmpresa.getSelectionModel().getSelections();		 
					var uImpCargoEmp = 0;
					var iMovCargoEmp = 0;
					var uImpAbonoEmp = 0;
					var iMovAbonoEmp = 0;
					var uTotalEmp = 0;
						for(var i = 0; i < registrosGridEmp.length; i++)
						{
							if(registrosGridEmp[i].get('idTipoMovto') == 'C')
							{
								uImpCargoEmp = uImpCargoEmp + parseFloat(registrosGridEmp[i].get('importe'));
								iMovCargoEmp = iMovCargoEmp + 1;
							}
							else
							{
								 uImpAbonoEmp = uImpAbonoEmp + parseFloat(registrosGridEmp[i].get('importe'));
								 iMovAbonoEmp = iMovAbonoEmp + 1;
							 }
							 }  
									uTotalEmp = uImpAbonoEmp - uImpCargoEmp;
						            Ext.getCmp(PF+'txtCantCargoEmpresa').setValue(iMovCargoEmp);
						            Ext.getCmp(PF+'txtCargoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargoEmp)*100)/100));
						            Ext.getCmp(PF+'txtCantAbonoEmpresa').setValue(iMovAbonoEmp);
						            Ext.getCmp(PF+'txtAbonoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbonoEmp)*100)/100));
						            Ext.getCmp(PF+'txtTotalEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uTotalEmp)*100)/100));
						            NS.smEmp.lock();
				}
			}
		}
	});
	
	NS.gridMovsBanco  = new Ext.grid.GridPanel({
		store : NS.storeMovimientosBanco,
		id: PF+'gridMovsBanco',
		name: PF+'gridMovsBanco',
		width: 380,
       	height: 160,
		x :0,
		y :172,
		frame: true,
		title :'Movimientos del Banco',
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
		columns : [ NS.smBan,
		{
			header :'Grupo',width :50,dataIndex :'grupo'
		},{
			header :'Estatus',width :50,dataIndex :'idEstatuscb'
		},{
			header :'Fecha',width :100,dataIndex :'fecOperacion'
		},{
			header :'C/A',width :30,dataIndex :'cargoAbono'
		},{
			header :'Importe',width :100,dataIndex :'importe', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Referencia',width :160,dataIndex :'referencia'
		},{
			header :'Secuencia',width :80,dataIndex :'secuencia', hidden: true
		},{
			header :'No. Cheque',width :80,dataIndex :'noCheque'
		},{
			header :'Exportado',width :80,dataIndex :'exportado'
		}]
		}),
		sm: NS.smBan,
		listeners:{
			rowclick:{
				fn:function(grid,record,e){
					/*var regBanco = NS.gridMovsBanco.getSelectionModel().getSelections();
					var regEmpresa = NS.gridMovsEmpresa.getSelectionModel().getSelections();
					var regEmpresaItem = NS.storeMovimientosEmpresa.data.items;
					var regBancoItem = NS.storeMovimientosBanco.data.items;
					var array = new Array();
					var array1 = new Array();
		        	var k = 0;
		        	$('div[id*="gridMovsEmpresa"] div[class*="GridBanco"] table tr td div div').each(function(index,el){
					    $(el).addClass('x-grid3-row-checker');
					});
		        	
		        	
			        	$('div[id*="gridMovsBanco"] div[class*="GridBanco"]').removeClass('GridBanco');
			        	NS.smEmp.clearSelections();
			        if(regEmpresa.length < 1 && regBanco.length>1)
			        {
			        	NS.smBan.clearSelections();
						
					}
					
					for(var i = 0; i < regBanco.length; i = i + 1)
					{
						if(regBanco[i].get('exportado') === 'SI')
						{
							BFwrk.Util.msgShow('No se puede desconciliar, este grupo ya fue exportado','WARNING');
							return;
						}
					}
		        	$('div[id*="gridMovsBanco"] div[class*="x-grid3-row-selected"]').addClass('GridBanco');
		        	
		        	for(var j = 0; j < regEmpresaItem.length; j = j + 1)
		        	{
		        		if(NS.smBan.isSelected(record) && (regBancoItem[record].get('grupo') === regEmpresaItem[j].get('grupoPago')))
			        	{	
			        		array[k] = regEmpresaItem[j];
			        		k ++;
			        		NS.smEmp.selectRecords(array);
			        	}
		        	}

		        	
		        	for(var j = 0; j < regBancoItem.length; j = j + 1)
		        	{
		        		if(NS.smBan.isSelected(record) && (regBancoItem[j].get('grupo') === regBancoItem[record].get('grupo')))
			        	{	
			        		array1[k] = regBancoItem[j];
			        		k ++;
			        		NS.smBan.selectRecords(array1);
			        	}
		        	}

		        	
		        	
		        	NS.bloqueoCheck('gridMovsEmpresa');
		        	//NS.smEmp.lock(1);
		        	$('div[id*="gridMovsEmpresa"] div[class*="GridBanco"]').removeClass('GridBanco');
	        		$('div[id*="gridMovsEmpresa"] div[class*="x-grid3-row-selected"]').addClass('GridBanco');*/
				}
			}
		}
	});
	
	NS.marcarGrupoBancos = function() {
		var recordsGridBanco = NS.storeMovimientosBanco.data.items;
		var recordsGridEmpresa = NS.storeMovimientosEmpresa.data.items;
		var regBanco = NS.gridMovsBanco.getSelectionModel().getSelections();
		var matrizBanco = new Array();
		var matrizEmpresa = new Array();
		if(regBanco.length > 0) {
			for(var i=0; i<regBanco.length; i++) {
				for(var x=0; x<recordsGridEmpresa.length; x++) {
					if(regBanco[i].get('grupo') == recordsGridEmpresa[x].get('grupoPago')){
						matrizEmpresa.push(x);
					}
				}
			}
		}
		NS.smEmp.selectRows(matrizEmpresa);
	};
	
	NS.storeMovimientosEmpresa = new Ext.data.DirectStore({
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
			iFormaPago: 0,
			iNoEmpresa: 0,
			iGrupoIni: 0,
			iGrupoFin: 0
		},
		root: '',
		paramOrder:['iIdBanco','sIdChequera','sFecIni','sFecFin','sEstatus','uMontoIni','uMontoFin','sCargoAbono','iFormaPago', 'iNoEmpresa', 'iGrupoIni', 'iGrupoFin'],
		directFn: ConciliacionBancoSetAction.llenarMovsEmpresaCancelacion, 
		fields: [
			{name: 'grupoPago'},
			{name: 'idEstatusCb'},
			{name: 'fecValor'},
			{name: 'idTipoMovto'},
			{name: 'importe'},
			{name: 'referencia'},
			{name: 'noFolioDet'},
			{name: 'noCheque'},
			{name: 'idDivisa'},
			{name: 'bEntregado'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosEmpresa, msg:"Buscando..."});
				//Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
				//BFwrk.Util.msgWait('Cancelando...', false);
				if(records.length == 0)
				{
					BFwrk.Util.msgShow('No se encontraron movimientos de la empresa para cancelar','INFO');
					return;
				}
				/*else
				{
					var cont = records.length;
					var uImpCargo = 0;
					var iMovCargo = 0;
					var uImpAbono = 0;
					var iMovAbono = 0;
					var uTotal = 0;
					
					for(var i = 0; i < cont; i = i + 1)
					{
						if(records[i].get('idTipoMovto') === 'C')
						{
							uImpCargo = uImpCargo + parseFloat(records[i].get('importe'));
							iMovCargo = iMovCargo + 1;
						}
			            else
			            {
			            	uImpAbono =  uImpAbono + parseFloat(records[i].get('importe'));
			            	iMovAbono = iMovAbono + 1;
			            }
		            }
		            
		            uTotal = uImpAbono - uImpCargo;
		            Ext.getCmp(PF+'txtCantCargoEmpresa').setValue(iMovCargo);
		            Ext.getCmp(PF+'txtCargoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargo)*100)/100));
		            Ext.getCmp(PF+'txtCantAbonoEmpresa').setValue(iMovAbono);
		            Ext.getCmp(PF+'txtAbonoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbono)*100)/100));
		            Ext.getCmp(PF+'txtTotalEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));
				}*/
			}
		}
	}); 
	
	NS.smEmp = new Ext.grid.CheckboxSelectionModel({
		//checkOnly: true 
		singleSelect: false,
	});
	
	NS.gridMovsEmpresa  = new Ext.grid.GridPanel({
		store : NS.storeMovimientosEmpresa,
		id: PF+'gridMovsEmpresa',
		name: PF+'gridMovsEmpresa',
		width: 380,
       	height: 160,
		x :388,
		y :172,
		frame: true,
		title :'Movimientos de la Empresa',
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
		columns : [ NS.smEmp,
		{
			header :'Grupo',width :50,dataIndex :'grupoPago'
		},{
			header :'Estatus',width :50,dataIndex :'idEstatusCb'
		},{
			header :'Fecha',width :100,dataIndex :'fecValor'
		},{
			header :'C/A',width :30,dataIndex :'idTipoMovto'
		},{
			header :'Importe',width :100,dataIndex :'importe', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Referencia',width :80,dataIndex :'referencia'
		},{
			header :'Folio Det',width :80,dataIndex :'noFolioDet'
		},{
			header :'No. Cheque',width :80,dataIndex :'noCheque'
		},{
			header :'Divisa',width :80,dataIndex :'idDivisa'
		},{
			header :'Exportado',width :80,dataIndex :'bEntregado'
		}]
		}),
		sm: NS.smEmp,
		listeners:{
			rowclick:{
				fn:function(grid,record,e){
					/*var regEmpresa = NS.gridMovsEmpresa.getSelectionModel().getSelections();
					var regEmpresaItem = NS.storeMovimientosEmpresa.data.items;
					var regBancoItem = NS.storeMovimientosBanco.data.items;
					var array = new Array();
		        	var k = 0;
		        	
		        	$('div[id*="gridMovsBanco"] div[class*="Seleccionado"] table tr td div div').each(function(index,el){
					    $(el).addClass('x-grid3-row-checker');
					});
		        	
		        	if(regEmpresa.length < 1)
		        	{
			        	$('div[id*="gridMovsEmpresa"] div[class*="Seleccionado"]').removeClass('Seleccionado');
						NS.smBan.clearSelections();
					}
		        	
					
					for(var i = 0; i < regEmpresa.length; i = i + 1)
					{
						if(regEmpresa[i].get('bEntregado') === 'SI')
						{
							BFwrk.Util.msgShow('No se puede desconciliar, este grupo ya fue exportado','WARNING');
							return;
						}
					}
		        	$('div[id*="gridMovsEmpresa"] div[class*="x-grid3-row-selected"]').addClass('Seleccionado');
		        	
		        	for(var j = 0; j < regBancoItem.length; j = j + 1)
		        	{
		        		if(NS.smEmp.isSelected(record) && (regBancoItem[j].get('grupo') === regEmpresaItem[record].get('grupoPago')))
			        	{	
			        		array[k] = regBancoItem[j];
			        		k ++;
			        		NS.smBan.selectRecords(array);
			        	}
		        	}
		        	NS.bloqueoCheck('gridMovsBanco');
		        	$('div[id*="gridMovsBanco"] div[class*="Seleccionado"]').removeClass('Seleccionado');
	        		$('div[id*="gridMovsBanco"] div[class*="x-grid3-row-selected"]').addClass('Seleccionado');*/
				}
			}
		}
	});
	
	NS.bloqueoCheck =function(grid){

		//gridMovsEmpresa
		$('div[id*="'+ grid +'"] div[class*="x-grid3-row-selected"] table tr td div div').each(function(index,el){
		    $(el).removeClass('x-grid3-row-checker');
		    $(el).attr('enabled','enabled');
		    $(el).attr('disabled','disabled');
		    
//			$(el).click(function(){
//	   			 return false;
//			});
		});
		$('div[id*="'+ grid +'"] div[class*="x-grid3-row-selected"] table tr td div').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
//	    	$(el).click(function(){
//	   			 return false;
//			});
	   		
		});
		
			$('div[id*="'+ grid +'"] div[class*="x-grid3-row-selected"] table tr td').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
//	    	$(el).click(function(){
//	   			 return false;
//			});
	   		
		});
		
			$('div[id*="'+ grid +'"] div[class*="x-grid3-row-selected"] table tr').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
//	    	$(el).click(function(){
//	   			 return false;
//			});
	   		
		});
			$('div[id*="'+ grid +'"] div[class*="x-grid3-row-selected"] table').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
//	    	$(el).click(function(){
//	   			 return false;
//			});
	   		
		});
			$('div[id*="'+ grid +'"] div[class*="x-grid3-row-selected"]').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
//	    	$(el).click(function(){
//	   			 return false;
//			});
		});
	};
	
	NS.accionarCmbEmpresa = function(comboValor)
	{
		NS.storeCmbChequera.baseParams.empresa = comboValor;
		NS.storeMovimientosBanco.baseParams.iNoEmpresa = comboValor;
		NS.storeCmbBanco.baseParams.empresa = comboValor;
		NS.noEmpresa = comboValor;
		NS.storeDatos.removeAll();
		NS.gridDatos.getView().refresh();
		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}
	
	NS.ocultarComponentes = function()
	{
		Ext.getCmp(PF+'cmbBanco').setVisible(false);
		Ext.getCmp(PF+'cmbChequera').setVisible(false);
		Ext.getCmp(PF+'cmbEstatus').setVisible(false);
		Ext.getCmp(PF+'cmbCargoAbono').setVisible(false);
		Ext.getCmp(PF+'cmbFormaPago').setVisible(false);
		Ext.getCmp(PF+'txtFechaIni').setVisible(false);
		Ext.getCmp(PF+'txtFechaFin').setVisible(false);
		Ext.getCmp(PF+'txtMontoIni').setVisible(false);
		Ext.getCmp(PF+'txtMontoFin').setVisible(false);
		Ext.getCmp(PF+'txtGrupoIni').setVisible(false);
		Ext.getCmp(PF+'txtGrupoFin').setVisible(false);
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
	
	NS.opcionSel = function()
	{
		//valida opcion seleccionada
	    var value = Ext.getCmp(PF + 'movimiento').getValue();
	    var valor = parseInt(value.getGroupValue());
	    return valor;
	}
	
	NS.validarConciliacion = function()
	{
		var bValida = true;
		ConciliacionBancoSetAction.validarConciliacion(NS.iIdBanco, NS.sIdChequera, true, function(mapRet, e){
			if(mapRet.msgUsuario!==null  &&  mapRet.msgUsuario!=='' && mapRet.msgUsuario!= undefined)
			{
				BFwrk.Util.msgShow(''+mapRet.msgUsuario, 'INFO');
				bValida = false;
			}
			else
			{
				bValida = true;
			}
		});
		return bValida;
	}
	
	NS.limpiarParams = function()
	{
		NS.storeMovimientosBanco.baseParams.iIdBanco = 0;
		NS.storeMovimientosBanco.baseParams.sIdChequera = '';
		NS.storeMovimientosBanco.baseParams.sFecIni = '';
		NS.storeMovimientosBanco.baseParams.sFecFin = '';
		NS.storeMovimientosBanco.baseParams.sEstatus = '';
		NS.storeMovimientosBanco.baseParams.uMontoIni = '';
		NS.storeMovimientosBanco.baseParams.uMontoFin = '';
		NS.storeMovimientosBanco.baseParams.sCargoAbono = '';
		NS.storeMovimientosBanco.baseParams.iFormaPago = 0;
		//NS.storeMovimientosBanco.baseParams.iNoEmpresa = NS.GI_ID_EMPRESA;
		NS.storeMovimientosBanco.baseParams.iGrupoIni = 0;
		NS.storeMovimientosBanco.baseParams.iGrupoFin = 0;
		NS.storeMovimientosEmpresa.baseParams.iIdBanco = 0;
		NS.storeMovimientosEmpresa.baseParams.sIdChequera = '';
		NS.storeMovimientosEmpresa.baseParams.sFecIni = '';
		NS.storeMovimientosEmpresa.baseParams.sFecFin = '';
		NS.storeMovimientosEmpresa.baseParams.sEstatus = '';
		NS.storeMovimientosEmpresa.baseParams.uMontoIni = '';
		NS.storeMovimientosEmpresa.baseParams.uMontoFin = '';
		NS.storeMovimientosEmpresa.baseParams.sCargoAbono = '';
		NS.storeMovimientosEmpresa.baseParams.iFormaPago = 0;
		//NS.storeMovimientosEmpresa.baseParams.iNoEmpresa = NS.GI_ID_EMPRESA;
		NS.storeMovimientosEmpresa.baseParams.iGrupoIni = 0;
		NS.storeMovimientosEmpresa.baseParams.iGrupoFin = 0;
	}
	
	 NS.buscar = function(){
		//NS.storeMovimientosBanco.removeAll();
		//NS.gridMovsBanco.getView().refresh();
		//NS.storeMovimientosEmpresa.removeAll();
		//NS.gridMovsEmpresa.getView().refresh();
		
		
	 	NS.limpiarParams();
    	var datosBusqueda = NS.storeDatos.data.items;

   		//Ext.getCmp(PF + 'cmdBuscar').setDisabled(true);
   		for(var i = 0; i < datosBusqueda.length; i = i + 1)
   		{
   			if(datosBusqueda[i].get('criterio') == 'BANCO')
   			{
   				if(datosBusqueda[i].get('id') !== '')
   				{
   					NS.storeMovimientosBanco.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
   					NS.storeMovimientosEmpresa.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
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
					NS.storeMovimientosBanco.baseParams.sIdChequera = datosBusqueda[i].get('id');
					NS.storeMovimientosEmpresa.baseParams.sIdChequera = datosBusqueda[i].get('id');
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
	   				NS.storeMovimientosBanco.baseParams.sFecIni = ini;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
	   				NS.storeMovimientosBanco.baseParams.sFecFin = fin;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
	   				NS.storeMovimientosEmpresa.baseParams.sFecIni = ini;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
	   				NS.storeMovimientosEmpresa.baseParams.sFecFin = fin;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
	   				NS.sFecIni = Ext.getCmp(PF + 'txtFechaIni').getValue().format('d/m/Y');
	   				NS.sFecFin = Ext.getCmp(PF + 'txtFechaFin').getValue().format('d/m/Y');
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
   					NS.storeMovimientosBanco.baseParams.sEstatus = datosBusqueda[i].get('id');
   					NS.storeMovimientosEmpresa.baseParams.sEstatus = datosBusqueda[i].get('id');
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
   					NS.storeMovimientosBanco.baseParams.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
   					NS.storeMovimientosBanco.baseParams.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
   					NS.storeMovimientosEmpresa.baseParams.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
   					NS.storeMovimientosEmpresa.baseParams.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
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
   					NS.storeMovimientosBanco.baseParams.sCargoAbono = datosBusqueda[i].get('id');
   					NS.storeMovimientosEmpresa.baseParams.sCargoAbono = datosBusqueda[i].get('id');
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
   					NS.storeMovimientosBanco.baseParams.iFormaPago = parseInt(datosBusqueda[i].get('id'));
   					NS.storeMovimientosEmpresa.baseParams.iFormaPago = parseInt(datosBusqueda[i].get('id'));
   					NS.iFormaPago = parseInt(datosBusqueda[i].get('id'));
   				}
   				else
   				{
   					BFwrk.Util.msgShow('Debe dar un valor a FORMA DE PAGO', 'INFO');		
		    		return;
   				}
   			}
   			else if(datosBusqueda[i].get('criterio') == 'GRUPO')
   			{
   				var valorGrupo=datosBusqueda[i].get('valor');
   				if(datosBusqueda[i].get('id') !== '')
   				{
   					var ini = '';
   					var fin = '';
   					ini = NS.obtenerValIni(valorGrupo);
   					NS.storeMovimientosBanco.baseParams.iGrupoIni = parseInt(ini.trim());
   					NS.storeMovimientosEmpresa.baseParams.iGrupoIni = parseInt(ini.trim());
   					NS.iGrupoIni = parseInt(ini.trim());
   					if(Ext.getCmp(PF + 'txtGrupoFin').getValue() !== '')
   					{
						fin = NS.obtenerValFin(valorGrupo);
						NS.storeMovimientosBanco.baseParams.iGrupoFin = parseInt(fin.trim());
						NS.iGrupoFin = parseInt(fin.trim());
						NS.storeMovimientosEmpresa.baseParams.iGrupoFin = parseInt(fin.trim());
   					}
   				}
   				else
   				{
   					BFwrk.Util.msgShow('Debe dar un valor a GRUPO', 'INFO');		
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
	
	NS.contenedorCancelacion = new Ext.FormPanel({
	    title: 'Cancelación de Conciliaciones',
	    width: 801,
	    height: 576,
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
	                width: 790,
	                height: 540,
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 5,
	                        width: 768,
	                        height: 160,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Empresa:',
	                                x: 0,
	                                y: 3
	                            },
	                            {
	                                xtype: 'fieldset',
	                                title: 'Selecci\u00f3n',
	                                x: 0,
	                                y: 30,
	                                width: 200,
	                                height: 100,
	                                layout: 'absolute',
	                                items: [
	                                    {
	                                	xtype: 'radiogroup',
				                     	name: PF+'movimiento',
				                     	id: PF+'movimiento',
				                     	columns: 1,
			                     		x: 0,
			                     		y: 0,
								       	items: [
		                                    {
		                                        xtype: 'radio',
		                                        boxLabel: 'Mov. de Banco',
		                                        name: PF+'optMov',
				                                inputValue: 0,
		                                    },
		                                    {
		                                        xtype: 'radio',
		                                        boxLabel: 'Mov. de la Empresa',
		                                        name: PF+'optMov',
				                                inputValue: 1,
		                                    },
		                                    {
		                                        xtype: 'radio',
		                                        boxLabel: 'Mov. de Banco y Empresa',
		                                        name: PF+'optMov',
				                                inputValue: 2,
				                                checked: true
		                                    }
	                                    ]
	                                    }
	                                ]
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Criterio:',
	                                x: 236,
	                                y: 30
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Valor:',
	                                x: 238,
	                                y: 80
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtEmpresa',
	                                name: PF+'txtEmpresa',
//	                                value: NS.GI_ID_EMPRESA,
	                                x: 58,
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
	                            NS.cmbCriterio,
	                            NS.cmbBanco,
	                            NS.cmbChequera,
	                            NS.cmbEstatus,
	                            NS.cmbCargoAbono,
	                            NS.cmbFormaPago,
	                            {
	                                xtype: 'datefield',
	                                id: PF+'txtFechaIni',
	                                name: PF+'txtFechaIni',
	                                format: 'd/m/Y',
	                                hidden: true,
	                                x: 238,
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
	                                x: 340,
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
	                                hidden: true,
	                                x: 238,
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
	                                hidden: true,
	                                x: 340,
	                                y: 100,
	                                width: 100,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var valorUno=BFwrk.Util.formatNumber(Ext.getCmp(PF+'txtMontoIni').getValue());
			                        			Ext.getCmp(PF + 'txtMontoFin').setValue(BFwrk.Util.formatNumber(caja.getValue()));
						        				if(parseInt(valorUno) > parseInt(valor))
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
	                                xtype: 'textfield',
	                                id: PF+'txtGrupoIni',
	                                name: PF+'txtGrupoIni',
	                                hidden: true,
	                                x: 238,
	                                y: 100,
	                                width: 100,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
						        				NS.agregarCriterioValor('GRUPO', caja.getId(), 'valor');
			                        		}
			                        	}
			                        }
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtGrupoFin',
	                                name: PF+'txtGrupoFin',
	                                hidden: true,
	                                x: 340,
	                                y: 100,
	                                width: 100,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var valorUno=Ext.getCmp(PF+'txtGrupoIni').getValue();
						        				if(parseInt(valorUno) > parseInt(valor))
												{
													BFwrk.Util.msgShow('El valor inicial debe ser menor al final', 'ERROR');
													Ext.getCmp(PF+'txtGrupoIni').setValue('');
													Ext.getCmp(PF+'txtGrupoIni').setDisabled(true);
													Ext.getCmp(PF+'txtGrupoFin').setDisabled(true);
													Ext.getCmp(PF+'txtGrupoFin').setValue('');
													return;
												}
						        				NS.agregarCriterioValor('GRUPO', caja.getId(), 'valor');
			                        		}
			                        	}
			                        }
	                            },
	                            {
	                                xtype: 'button',
	                                text: 'Buscar',
	                                id: PF+'cmdBuscar',
	                                name: PF+'cmdBuscar',
	                                x: 667,
	                                y: 115,
	                                width: 70,
   	                                listeners:{
				               			click:{
				           			   		fn:function(e){
					            			   	var indice=0;
					            			   	var indiceFecha=0;
												var recordsDatGrid=NS.storeDatos.data.items;
												for(var i = 0; i < recordsDatGrid.length; i++)
												{
													if(recordsDatGrid[i].get('criterio')=='CHEQUERA')
														indice = i;
												}
												for(var i = 0; i < recordsDatGrid.length; i++)
												{
													if(recordsDatGrid[i].get('criterio')=='FECHA')
														indiceFecha = i;
												}
												
												if(recordsDatGrid.length <= 0){
													BFwrk.Util.msgShow('Debe indicar el banco, la chequera y la fecha en los criterios de b\u00fasqueda','WARNING');
													return;
												}
												
												if(recordsDatGrid[indiceFecha]!=undefined && (recordsDatGrid[indiceFecha].get('criterio'))!='FECHA'){
													BFwrk.Util.msgShow('Debe indicar la fecha en los criterios de b\u00fasqueda','WARNING');
													return;
												}
												else if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='CHEQUERA')
												{
													NS.buscar();
//													NS.storeMovimientosBanco.removeAll();
//													NS.gridMovsBanco.getView().refresh();
//													NS.storeMovimientosEmpresa.removeAll();
//													NS.gridMovsEmpresa.getView().refresh();
													if(NS.opcionSel() == 0)
													{
														if(NS.validarConciliacion()){
															//NS.storeMovimientosEmpresa.removeAll();
															//NS.gridMovsEmpresa.getView().refresh();
															NS.storeMovimientosBanco.baseParams.iNoEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
															var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosBanco, msg:"Buscando..."});
															NS.storeMovimientosBanco.load();
														}else
															return;
													}
													if(NS.opcionSel() == 1)
													{
														if(NS.validarConciliacion()){
															//NS.storeMovimientosBanco.removeAll();
															//NS.gridMovsBanco.getView().refresh();
															NS.storeMovimientosEmpresa.baseParams.iNoEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
															var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosEmpresa, msg:"Buscando..."});
															NS.storeMovimientosEmpresa.load();
														}else
															return;
													}
													if(NS.opcionSel() == 2)
													{
														if(NS.validarConciliacion())
														{
															NS.storeMovimientosEmpresa.baseParams.iNoEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
															var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosEmpresa, msg:"Buscando..."});
															NS.storeMovimientosEmpresa.load();
															NS.storeMovimientosBanco.baseParams.iNoEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
															var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosBanco, msg:"Buscando..."});
															NS.storeMovimientosBanco.load();
														}
														else
															return;
													}
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
	                    NS.gridMovsEmpresa,
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 345,
	                        layout: 'absolute',
	                        width: 768,
	                        height: 173,
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Cargos:',
	                                x: 0,
	                                y: 0
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantCargoBanco',
	                                name: PF+'txtCantCargoBanco',
	                                readOnly: true,
	                                value: 0,
	                                x: 0,
	                                y: 22,
	                                width: 60
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCargoBanco',
	                                name: PF+'txtCargoBanco',
	                                readOnly: true,
	                                value: BFwrk.Util.formatNumber(0.0),
	                                x: 67,
	                                y: 22,
	                                width: 110
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Abonos:',
	                                x: 208,
	                                y: 0
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantAbonoBanco',
	                                name: PF+'txtCantAbonoBanco',
	                                readOnly: true,
	                                value: 0,
	                                width: 60,
	                                x: 186,
	                                y: 22
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtAbonoBanco',
	                                name: PF+'txtAbonoBanco',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 258,
	                                y: 22,
	                                width: 110
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Cargos:',
	                                x: 378,
	                                y: 1
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantCargoEmpresa',
	                                name: PF+'txtCantCargoEmpresa',
	                                readOnly: true,
	                                value: 0,
	                                x: 378,
	                                y: 22,
	                                width: 60
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCargoEmpresa',
	                                name: PF+'txtCargoEmpresa',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 447,
	                                y: 22,
	                                width: 110
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Abonos:',
	                                x: 570,
	                                y: 2
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantAbonoEmpresa',
	                                name: PF+'txtCantAbonoEmpresa',
	                                readOnly: true,
	                                value: 0,
	                                x: 567,
	                                y: 22,
	                                width: 60
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtAbonoEmpresa',
	                                name: PF+'txtAbonoEmpresa',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 636,
	                                y: 22,
	                                width: 110
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Importe Total:',
	                                x: 168,
	                                y: 57
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalBanco',
	                                name: PF+'txtTotalBanco',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 258,
	                                y: 51,
	                                width: 110
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Importe Total:',
	                                x: 547,
	                                y: 58
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalEmpresa',
	                                name: PF+'txtTotalEmpresa',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 636,
	                                y: 53,
	                                width: 110
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 396,
	                                y: 53,
	                                width: 110,
	                                hidden: true
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Nota: Los movimientos se desconcilian por grupo.',
	                                x: 2,
	                                y: 106,
	                                width: 154
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Observaciones:',
	                                x: 169,
	                                y: 91
	                            },
	                            {
	                                xtype: 'textarea',
	                                id: PF+'txtObservacion',
	                                name: PF+'txtObservacion',
	                                anchor: '60%',
	                                height: 40,
	                                width: 150,
	                                x: 168,
	                                y: 110,
	                                emptyText: 'Describa el motivo de desconciliación'
	                            },
	                            {
	                                xtype: 'button',
	                                text: 'Imprimir',
	                                x: 487,
	                                y: 123,
	                                width: 70,
	                                tooltip: 'Bitacora de cancelados',
	                                listeners:{
						           		click:{
						           			fn:function(){
						           				var fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
							        			strParams = '?nomReporte=ReporteCancelaConciliados';
												strParams += '&'+'nomParam1=empresa';
												strParams += '&'+'valParam1='+ NS.noEmpresa;
												strParams += '&'+'nomParam2=nomEmpresa';
												strParams += '&'+'valParam2='+BFwrk.Util.scanFieldsStore(NS.storeCmbEmpresa, 'id', NS.noEmpresa, 'descripcion');
												strParams += '&'+'nomParam3=subtitulo';
												strParams += '&'+'valParam3='+'Cancelacion de Conciliaciones al ' + 
													fecHoy.getDate() + ' DE ' + BFwrk.Util.getNameMonth(fecHoy.getMonth() + 1) + ' DE ' + fecHoy.getFullYear();
												window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
					           				}
						           		}
						           	}
	                            },
	                            {
	                                xtype: 'button',
	                                text: 'Desconciliar',
	                                x: 578,
	                                y: 122,
	                                width: 70,
	                                listeners:{
										click:{
											fn:function(){
												var regSelecBanco=NS.gridMovsBanco.getSelectionModel().getSelections();
												var regSelecEmpresa=NS.gridMovsEmpresa.getSelectionModel().getSelections();
												var matrizBanco = new Array ();
												var matrizEmpresa = new Array ();
	                        					var matrizCriterios = new Array ();
	                        					
	                        					if(regSelecBanco.length <= 0 && regSelecEmpresa.length <= 0)
	                        					{
	                        						BFwrk.Util.msgShow('Es necesario seleccionar algún registro','INFO');
													return;
	                        					}
	                        					else
	                        					{
													if(Ext.getCmp(PF + 'txtObservacion').getValue() === '')
													{
														BFwrk.Util.msgShow('Es necesario especificar por qu\u00e9 se est\u00e1 cancelando la conciliaci\u00f3n','INFO');
														return;
													}
													else
													{
														BFwrk.Util.msgWait('Cancelando...', true);
														for(var i = 0; i < regSelecBanco.length; i ++)
														{
															var banco = {};
															//registros seleccionados del grid banco
															banco.grupo = regSelecBanco[i].get('grupo');
															banco.idEstatuscb = regSelecBanco[i].get('idEstatuscb');
															banco.fecOperacion = regSelecBanco[i].get('fecOperacion');
															banco.cargoAbono = regSelecBanco[i].get('cargoAbono');
															banco.importe = regSelecBanco[i].get('importe');
															banco.referencia = regSelecBanco[i].get('referencia');
															banco.secuencia = regSelecBanco[i].get('secuencia');
															banco.noCheque = regSelecBanco[i].get('noCheque');
															banco.concepto = regSelecBanco[i].get('concepto');
															banco.noEmpresa = regSelecBanco[i].get('noEmpresa');
															banco.idBanco = regSelecBanco[i].get('idBanco');
															banco.idChequera = regSelecBanco[i].get('idChequera');
															banco.exportado = regSelecBanco[i].get('exportado');
															matrizBanco[i] = banco; 
														}
														
														for(var j = 0; j < regSelecEmpresa.length; j ++)
														{
															var empresa = {};
															//registros seleccionados del grid empresa
															empresa.grupo = regSelecEmpresa[j].get('grupoPago');
															empresa.idEstatusCb = regSelecEmpresa[j].get('idEstatusCb');
															empresa.fecValor = regSelecEmpresa[j].get('fecValor');
															empresa.idTipoMovto = regSelecEmpresa[j].get('idTipoMovto');
															empresa.importe = regSelecEmpresa[j].get('importe');
															empresa.referencia = regSelecEmpresa[j].get('referencia');
															empresa.noFolioDet = regSelecEmpresa[j].get('noFolioDet');
															empresa.noCheque = regSelecEmpresa[j].get('noCheque');
															empresa.idDivisa = regSelecEmpresa[j].get('idDivisa');
															empresa.exportado = regSelecEmpresa[j].get('bEntregado');
															matrizEmpresa[j] = empresa;
														}
														
														var criterio = {};
														criterio.observacion = Ext.getCmp(PF + 'txtObservacion').getValue();
														matrizCriterios[0] = criterio; 
														
														var jsonStringBanco = Ext.util.JSON.encode(matrizBanco);
														var jsonStringEmpresa = Ext.util.JSON.encode(matrizEmpresa);
														var jsonStringCriterios = Ext.util.JSON.encode(matrizCriterios);
													
														ConciliacionBancoSetAction.cancelarConciliaciones(jsonStringBanco, jsonStringEmpresa, jsonStringCriterios, function(result, e){
															if(result.msgUsuario!==null  &&  result.msgUsuario!=='' && result.msgUsuario!= undefined)
															{
																//BFwrk.Util.msgWait('Conciliando...', false);
																BFwrk.Util.msgShow('' + result.msgUsuario ,'INFO');
																NS.storeMovimientosEmpresa.load();
																NS.storeMovimientosBanco.load();
																Ext.getCmp(PF + 'txtObservacion').reset();
																NS.inicializaCajas();
															}
														});
													}
												}
											}
										}
									}
	                            },
	                            {
	                                xtype: 'button',
	                                text: 'Limpiar',
	                                x: 668,
	                                y: 122,
	                                width: 70,
	                                listeners:{
										click:{
											fn:function(){
												NS.contenedorCancelacion.getForm().reset();
												NS.storeMovimientosBanco.removeAll();
												NS.gridMovsBanco.getView().refresh();
												NS.storeMovimientosEmpresa.removeAll();
												NS.gridMovsEmpresa.getView().refresh();
												NS.storeDatos.removeAll();
												NS.gridDatos.getView().refresh();
												NS.limpiarParams();
											}
										}
									}
	                            }
	                        ]
	                    }
	                ]
	            }
	        ]
	});
	NS.contenedorCancelacion.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});