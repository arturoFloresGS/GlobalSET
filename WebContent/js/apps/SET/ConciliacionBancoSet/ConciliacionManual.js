/*
@author: Jessica Arelly Cruz Cruz
@since: 12/09/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Conciliacion.ConciliacionManual');
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
	NS.uDiferencia = 0;
	NS.grupo = '';
	NS.contabi = '';
	NS.totalBanco = 0;
	NS.totalSET = 0;
	
	NS.importeBco = 0;
	NS.importeEmp = 0;
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
	
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
        ,x: 217
        ,y: 115
        ,width: 178
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
        ,x: 217
        ,y: 115
        ,width: 178
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
        ,x: 217
        ,y: 115
        ,width: 178
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
        ,x: 217
        ,y: 115
        ,width: 178
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
        ,x: 217
        ,y: 115
        ,width: 178
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
	
	NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_grupo',
			campoDos:'desc_grupo',
			tabla:'cat_grupo',
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
	
	NS.storeGrupo.load();
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo
		,name: PF+'cmbGrupo'
		,id: PF+'cmbGrupo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 77
        ,y: 24
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un grupo'
		,triggerAction: 'all'
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtGrupoBanco',NS.cmbGrupo.getId());
					Ext.getCmp(PF + 'cmbRubro').reset();
					Ext.getCmp(PF+'txtRubroBanco').reset();
					NS.storeRubro.baseParams.condicion = 'id_grupo = \'' + combo.getValue() + '\'';
					NS.storeRubro.load();
				}
			}
		}
	});
	
	NS.storeRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_rubro',
			campoDos:'desc_rubro',
			tabla:'cat_rubro',
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
	
	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeRubro
		,name: PF+'cmbRubro'
		,id: PF+'cmbRubro'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 356
        ,y: 24
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un rubro'
		,triggerAction: 'all'
	    ,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtRubroBanco',NS.cmbRubro.getId());
					NS.accionarCmbRubro(combo.getValue());
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
				     ,[ '6', 'FORMA DE PAGO' ]
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
		,x: 218
		,y: 65
		,width:178
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
       	height: 130,
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
	
	NS.smBan = new Ext.grid.CheckboxSelectionModel({
		//checkOnly: false,
		singleSelect: false,
		listeners:{
			rowselect: {
				fn:function(r, rowIndex){
					var registrosGrid = NS.gridMovsBanco.getSelectionModel().getSelections();
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
					    var totalBanco = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtTotalBanco').getValue());
			            var totalEmpresa = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtTotalEmpresa').getValue());
			            Ext.getCmp(PF + 'txtDiferencia').setValue(BFwrk.Util.formatNumber(Math.round((totalBanco - totalEmpresa) * 100) / 100));
				}
			},
			rowdeselect: {
				fn:function(r, rowIndex){
					var registrosGrid = NS.gridMovsBanco.getSelectionModel().getSelections();
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
											iMovCargo = parseInt(Ext.getCmp(PF+'txtCantCargoBanco').getValue()) - 1;
										}
							            else
							            {
							            	uImpAbono = uImpAbono + parseFloat(registrosGrid[i].get('importe'));
							            	iMovAbono = parseInt(Ext.getCmp(PF+'txtCantAbonoBanco').getValue()) - 1;
							            }
						            }
						uTotal = uImpAbono - uImpCargo;
					    Ext.getCmp(PF+'txtCantCargoBanco').setValue(iMovCargo);
					    Ext.getCmp(PF+'txtCargoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargo)*100)/100));
					    Ext.getCmp(PF+'txtCantAbonoBanco').setValue(iMovAbono);
					    Ext.getCmp(PF+'txtAbonoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbono)*100)/100));
					    Ext.getCmp(PF+'txtTotalBanco').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));
					    var totalBanco = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtTotalBanco').getValue());
			            var totalEmpresa = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtTotalEmpresa').getValue());
			            Ext.getCmp(PF + 'txtDiferencia').setValue(BFwrk.Util.formatNumber(Math.round((totalBanco - totalEmpresa) * 100) / 100));
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
			iNoEmpresa: 0
		},
		root: '',
		paramOrder:['iIdBanco','sIdChequera','sFecIni','sFecFin','sEstatus','uMontoIni','uMontoFin','sCargoAbono','iFormaPago', 'iNoEmpresa'],
		directFn: ConciliacionBancoSetAction.llenarMovsBanco, 
		fields: [
			{name: 'idEstatuscb'},
			{name: 'sFecOperacion'},
			{name: 'cargoAbono'},
			{name: 'importe'},
			{name: 'referencia'},
			{name: 'secuencia'},
			{name: 'noCheque'},
			{name: 'concepto'},
			{name: 'noEmpresa'},
			{name: 'idBanco'},
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosBanco, msg:"Buscando..."});
				
				if(records.length == 0) {
					BFwrk.Util.msgShow('No se encontraron movimientos del banco para conciliar','INFO');
		            mascara.hide();
					return;
				}
				/*var cont = records.length;
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
	            Ext.getCmp(PF+'txtTotalBanco').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));*/
	            mascara.hide();
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
		y :181,
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
			header :'Estatus',width :50,dataIndex :'idEstatuscb'
		},{
			header :'Fecha',width :100,dataIndex :'sFecOperacion', sortable: false
		},{
			header :'C/A',width :30,dataIndex :'cargoAbono'
		},{
			header :'Importe',width :100,dataIndex :'importe', renderer: BFwrk.Util.rendererMoney, align:'right'
		},{
			header :'Referencia',width :160,dataIndex :'referencia'
		},{
			header :'Secuencia',width :80,dataIndex :'secuencia', hidden: true
		},{
			header :'No. Cheque',width :80,dataIndex :'noCheque'
		},{
			header :'Desc. Banco',width :160,dataIndex :'concepto'
		},{
			header :'No. Empresa',width :80,dataIndex :'noEmpresa', hidden: true
		},{
			header :'No. Banco',width :80,dataIndex :'idBanco', hidden: true
		},{
			header :'Chequera',width :100,dataIndex :'idChequera', hidden: true
		}]
		}),
		sm: NS.smBan,
		listeners:{
			click:{
				fn: function(){
					//NS.calculaDiferencia('BANCO', NS.gridMovsBanco.getSelectionModel().getSelections());
				}
			}
		}
	});
	
	NS.calculaDiferencia = function(tipoMovto, movtos) {
		var sumImporte = 0;
		
		for(var i=0; i<movtos.length; i++) {
			if(movtos[i].get('idEstatuscb') == 'C' || movtos[i].get('idEstatuscb') == 'A' || movtos[i].get('idEstatuscb') == 'M') {
				BFwrk.Util.msgShow('Este registro ya fue conciliado: ' + movtos[i].get('importe'),'INFO');
				return;
			}else
				sumImporte += movtos[i].get('importe');
		}
		if(tipoMovto == 'BANCO') NS.importeBco = sumImporte;
		else NS.importeEmp = sumImporte;
		
		Ext.getCmp(PF + 'txtDiferencia').setValue(BFwrk.Util.formatNumber(Math.round((NS.importeBco - NS.importeEmp) * 100) / 100));
	};
	
	NS.smEmp = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true,
		checkOnly: false ,
		listeners:{
			rowselect: {
				fn:function(r, rowIndex){
					var registrosGrid = NS.gridMovsEmpresa.getSelectionModel().getSelections();
					var uImpCargo = 0;
					var iMovCargo = 0;
					var uImpAbono = 0;
					var iMovAbono = 0;
					var uTotal = 0;
					
					for(var i = 0; i < registrosGrid.length; i++)
					{
						if(registrosGrid[i].get('idTipoMovto') == 'C')
						{
							uImpCargo = uImpCargo + parseFloat(registrosGrid[i].get('importe'));
							iMovCargo = iMovCargo + 1;
						}
			            else
			            {
			            	uImpAbono = uImpAbono + parseFloat(registrosGrid[i].get('importe'));
			            	iMovAbono = iMovAbono + 1;
			            }
		            }
		            
		            uTotal = uImpAbono - uImpCargo;
		            Ext.getCmp(PF+'txtCantCargoEmpresa').setValue(iMovCargo);
		            Ext.getCmp(PF+'txtCargoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargo)*100)/100));
		            Ext.getCmp(PF+'txtCantAbonoEmpresa').setValue(iMovAbono);
		            Ext.getCmp(PF+'txtAbonoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbono)*100)/100));
		            Ext.getCmp(PF+'txtTotalEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));
		            var totalBanco = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtTotalBanco').getValue());
		            var totalEmpresa = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtTotalEmpresa').getValue());
		            Ext.getCmp(PF + 'txtDiferencia').setValue(BFwrk.Util.formatNumber(Math.round((totalBanco - totalEmpresa) * 100) / 100));
				}
			},
			rowdeselect: {
				fn:function(r, rowIndex){
					var registrosGrid = NS.gridMovsEmpresa.getSelectionModel().getSelections();
					var uImpCargo = 0;
					var iMovCargo = 0;
					var uImpAbono = 0;
					var iMovAbono = 0;
					var uTotal = 0;
					
					for(var i = 0; i < registrosGrid.length; i++)
					{
						if(registrosGrid[i].get('idTipoMovto') == 'C')
						{
							uImpCargo = uImpCargo + parseFloat(registrosGrid[i].get('importe'));
							iMovCargo = parseInt(Ext.getCmp(PF+'txtCantCargoEmpresa').getValue()) - 1;
						}
			            else
			            {
			            	uImpAbono = uImpAbono + parseFloat(registrosGrid[i].get('importe'));
			            	iMovAbono = parseInt(Ext.getCmp(PF+'txtCantAbonoEmpresa').getValue()) - 1;
			            }
		            }
		            
		            uTotal = uImpAbono - uImpCargo;
		            Ext.getCmp(PF+'txtCantCargoEmpresa').setValue(iMovCargo);
		            Ext.getCmp(PF+'txtCargoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargo)*100)/100));
		            Ext.getCmp(PF+'txtCantAbonoEmpresa').setValue(iMovAbono);
		            Ext.getCmp(PF+'txtAbonoEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbono)*100)/100));
		            Ext.getCmp(PF+'txtTotalEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));
		            var totalBanco = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtTotalBanco').getValue());
		            var totalEmpresa = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtTotalEmpresa').getValue());
		            Ext.getCmp(PF + 'txtDiferencia').setValue(BFwrk.Util.formatNumber(Math.round((totalBanco - totalEmpresa) * 100) / 100));
				}
			}
		}
	});
	
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
			iNoEmpresa: 0
		},
		root: '',
		paramOrder:['iIdBanco','sIdChequera','sFecIni','sFecFin','sEstatus','uMontoIni','uMontoFin','sCargoAbono','iFormaPago', 'iNoEmpresa'],
		directFn: ConciliacionBancoSetAction.llenarMovsEmpresa, 
		fields: [
			{name: 'idEstatusCb'},
			{name: 'fecValorStr'},
			{name: 'idTipoMovto'},
			{name: 'importe'},
			{name: 'referencia'},
			{name: 'noFolioDet'},
			{name: 'noCheque'},
			{name: 'noCuenta'},
			{name: 'idDivisa'},
			{name: 'noDocto'},
			{name: 'concepto'},
			{name: 'noFactura'},
			{name: 'beneficiario'},
			{name: 'tablaOrigen'},
			{name: 'idEstatusMov'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosEmpresa, msg:"Buscando..."});
				
				if(records.length == 0) {
					BFwrk.Util.msgShow('No se encontraron movimientos de la empresa para conciliar','INFO');
		            mascara.hide();
					return;
				}
				/*var cont = records.length;
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
	            Ext.getCmp(PF+'txtTotalEmpresa').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));*/
	            mascara.hide();
			}
		}
	}); 
	
	NS.gridMovsEmpresa  = new Ext.grid.GridPanel({
		store : NS.storeMovimientosEmpresa,
		id: PF+'gridMovsEmpresa',
		name: PF+'gridMovsEmpresa',
		width: 380,
       	height: 160,
		x :388,
		y :181,
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
			header :'Estatus',width :50,dataIndex :'idEstatusCb'
		},{
			header :'Fecha',width :100,dataIndex :'fecValorStr', sortable: false
		},{
			header :'C/A',width :30,dataIndex :'idTipoMovto'
		},{
			header :'Importe',width :100,dataIndex :'importe', renderer: BFwrk.Util.rendererMoney, align:'right'
		},{
			header :'Referencia',width :80,dataIndex :'referencia'
		},{
			header :'Folio Det',width :80,dataIndex :'noFolioDet', hidden: true
		},{
			header :'No. Cheque',width :80,dataIndex :'noCheque'
		},{
			header :'No. Cuenta',width :140,dataIndex :'noCuenta', hidden: true
		},{
			header :'Divisa',width :80,dataIndex :'idDivisa', hidden: true
		},{
			header :'No. Documento',width :100,dataIndex :'noDocto'
		},{
			header :'Concepto',width :100,dataIndex :'concepto'
		},{
			header :'Factura',width :100,dataIndex :'noFactura'
		},{
			header :'Beneficiario',width :150,dataIndex :'beneficiario'
		},{
			header :'Tabla',width :60,dataIndex :'tablaOrigen'
		},{
			header :'Id Estatus Mov',width :100,dataIndex :'idEstatusMov'
		}]
		}),
		sm: NS.smEmp,
		listeners:{
			click:{
				fn:function(grid, record, e){
					//NS.calculaDiferencia('EMPRESA', NS.gridMovsEmpresa.getSelectionModel().getSelections());
				}
			}
		}
	});
	
	NS.accionarCmbEmpresa = function(comboValor)
	{
		NS.storeCmbChequera.baseParams.empresa = comboValor;
		NS.storeMovimientosBanco.baseParams.iNoEmpresa = comboValor;
		NS.storeCmbBanco.baseParams.empresa = comboValor;
		NS.storeDatos.removeAll();
		NS.gridDatos.getView().refresh();
		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}
	
	NS.accionarCmbRubro = function(comboValor)
	{
		var rubro = ''+ comboValor;
		ConciliacionBancoSetAction.consultarContabiliza(rubro, function(mapRet, e){
	              			   			
			if(mapRet.contabi!==null && mapRet.contabi!= undefined)
			{
				NS.contabi = mapRet.contabi;
			}
			if(mapRet.chkContabiliza!==null && mapRet.chkContabiliza!= undefined)
			{
				if(mapRet.chkContabiliza == 1)
					Ext.getCmp(PF + 'chkContabiliza').setValue(true);
				else
					Ext.getCmp(PF + 'chkContabiliza').setValue(false);
			}
		});
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
					Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
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
	
	NS.inicializaCajas = function()
	{
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
		
		Ext.getCmp(PF+'txtAclaracion').setValue('');
		Ext.getCmp(PF+'txtDiferencia').setValue(BFwrk.Util.formatNumber(0.0));
	}
	
	
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
	
	NS.opcionSel = function() {
		//valida opcion seleccionada
	    var value = Ext.getCmp(PF + 'movimiento').getValue();
	    var valor = parseInt(value.getGroupValue());
	    return valor;
	};
	
	NS.buscar = function() {
		var paramBusqueda = NS.storeDatos.data.items;
		var jSonString;
		var parametros = new Array ();
    	var validaConci = false;
    	var datos = {};
    	
		for(var i=0; i<paramBusqueda.length; i++) {
			datos = {};
			datos.id = paramBusqueda[i].get('id');
			datos.criterio = paramBusqueda[i].get('criterio');
			parametros[i] = datos;
		}
		jSonString = Ext.util.JSON.encode(parametros);	
		
		ConciliacionBancoSetAction.validaDatosBusqueda(jSonString, function(resp, e){
			if(resp != '') {
				Ext.Msg.alert('SET', resp);
				mascara.hide();
				return;
			}
			NS.asignaParametros(paramBusqueda);
			validaConci = NS.validarConciliacion();
			
//			NS.storeMovimientosBanco.removeAll();
//			NS.gridMovsBanco.getView().refresh();
//			NS.storeMovimientosEmpresa.removeAll();
//			NS.gridMovsEmpresa.getView().refresh();

			if(NS.opcionSel() == 0 && validaConci){
				//NS.storeMovimientosEmpresa.removeAll();
				//NS.gridMovsEmpresa.getView().refresh();
				NS.storeMovimientosBanco.load();
			}
			
			if(NS.opcionSel() == 1 && validaConci){
				//NS.storeMovimientosBanco.removeAll();
				//NS.gridMovsBanco.getView().refresh();
				NS.storeMovimientosEmpresa.load();
			}
			
			if(NS.opcionSel() == 2 && validaConci) {
				NS.storeMovimientosEmpresa.load();
				NS.storeMovimientosBanco.load();
			}
		});
		mascara.show();
	};
	
	NS.asignaParametros = function(datosBusqueda) {
		NS.storeMovimientosBanco.baseParams.iIdBanco = 0;
		NS.storeMovimientosBanco.baseParams.sIdChequera = '';
		NS.storeMovimientosBanco.baseParams.sFecIni = '';
		NS.storeMovimientosBanco.baseParams.sFecFin = '';
		NS.storeMovimientosBanco.baseParams.sEstatus = '';
		NS.storeMovimientosBanco.baseParams.uMontoIni = '';
		NS.storeMovimientosBanco.baseParams.uMontoFin = '';
		NS.storeMovimientosBanco.baseParams.sCargoAbono = '';
		NS.storeMovimientosBanco.baseParams.iFormaPago = 0;
		NS.storeMovimientosBanco.baseParams.iNoEmpresa = 0;
		
		NS.storeMovimientosEmpresa.baseParams.iIdBanco= 0;
		NS.storeMovimientosEmpresa.baseParams.sIdChequera= '';
		NS.storeMovimientosEmpresa.baseParams.sFecIni = '';
		NS.storeMovimientosEmpresa.baseParams.sFecFin = '';
		NS.storeMovimientosEmpresa.baseParams.sEstatus = '';
		NS.storeMovimientosEmpresa.baseParams.uMontoIni = '';
		NS.storeMovimientosEmpresa.baseParams.uMontoFin = '';
		NS.storeMovimientosEmpresa.baseParams.sCargoAbono = '';
		NS.storeMovimientosEmpresa.baseParams.iFormaPago = 0;
		NS.storeMovimientosEmpresa.baseParams.iNoEmpresa = 0;
		
		for(var i = 0; i < datosBusqueda.length; i = i + 1) {
			if(datosBusqueda[i].get('criterio') == 'BANCO') {
				NS.storeMovimientosBanco.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
				NS.storeMovimientosEmpresa.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
				NS.iIdBanco = parseInt(datosBusqueda[i].get('id'));
   			}
			if(datosBusqueda[i].get('criterio') == 'CHEQUERA') {
   				NS.storeMovimientosBanco.baseParams.sIdChequera = datosBusqueda[i].get('id');
				NS.storeMovimientosEmpresa.baseParams.sIdChequera = datosBusqueda[i].get('id');
				NS.sIdChequera = datosBusqueda[i].get('id');    				
   			}
			if(datosBusqueda[i].get('criterio') == 'FECHA') {
   				var valorFechas=datosBusqueda[i].get('valor');
				var ini=NS.obtenerValIni(valorFechas);
				var fin=NS.obtenerValFin(valorFechas);
   				NS.storeMovimientosBanco.baseParams.sFecIni = ini;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
   				NS.storeMovimientosBanco.baseParams.sFecFin = fin;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
   				NS.storeMovimientosEmpresa.baseParams.sFecIni = ini;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
   				NS.storeMovimientosEmpresa.baseParams.sFecFin = fin;//BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
   				NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
   				NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
   			}
			if(datosBusqueda[i].get('criterio') == 'ESTATUS') {
				NS.storeMovimientosBanco.baseParams.sEstatus = datosBusqueda[i].get('id');
				NS.storeMovimientosEmpresa.baseParams.sEstatus = datosBusqueda[i].get('id');
				NS.sEstatus = datosBusqueda[i].get('id');
   			}
			if(datosBusqueda[i].get('criterio') == 'MONTOS') {
   				var valorImporte=datosBusqueda[i].get('valor');
				var ini=NS.obtenerValIni(valorImporte);
				var fin=NS.obtenerValFin(valorImporte);
				NS.storeMovimientosBanco.baseParams.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
				NS.storeMovimientosBanco.baseParams.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
				NS.storeMovimientosEmpresa.baseParams.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
				NS.storeMovimientosEmpresa.baseParams.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
				NS.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
				NS.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
   			}
			if(datosBusqueda[i].get('criterio') == 'CARGO/ABONO') {
				NS.storeMovimientosBanco.baseParams.sCargoAbono = datosBusqueda[i].get('id');
				NS.storeMovimientosEmpresa.baseParams.sCargoAbono = datosBusqueda[i].get('id');
				NS.sCargoAbono = datosBusqueda[i].get('id');
   			}
			if(datosBusqueda[i].get('criterio') == 'FORMA DE PAGO') {
				NS.storeMovimientosBanco.baseParams.iFormaPago = parseInt(datosBusqueda[i].get('id'));
				NS.storeMovimientosEmpresa.baseParams.iFormaPago = parseInt(datosBusqueda[i].get('id'));
				NS.iFormaPago = parseInt(datosBusqueda[i].get('id'));
   			}
   		}
    	NS.storeMovimientosBanco.baseParams.iNoEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		NS.storeMovimientosEmpresa.baseParams.iNoEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
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
		
	};
	
	NS.validarConciliacion = function() {
		var bValida = true;
		ConciliacionBancoSetAction.validarConciliacion(NS.iIdBanco, NS.sIdChequera, false, function(mapRet, e){
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
	};
	
	NS.ejecutar = function() {
		var regSelecBanco = NS.gridMovsBanco.getSelectionModel().getSelections();
		var regSelecEmpresa = NS.gridMovsEmpresa.getSelectionModel().getSelections();
		var bAjuste = false;
		var matrizBanco = new Array ();
		var matrizEmpresa = new Array ();
		var matrizCriterios = new Array ();
		var tipoMovtoBco = '';
		var tipoMovtoEmp = '';
		var idEstatusMov = '';
		
		ConciliacionBancoSetAction.validaDatos(parseInt(regSelecBanco.length), parseInt(regSelecEmpresa.length), 
				parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtDiferencia').getValue())),
				Ext.getCmp(PF + 'txtAclaracion').getValue(), function(result, e) {
			
			if(result != null && result != '' && result != undefined) {
				Ext.Msg.alert('SET', result);
				return;
			}
			BFwrk.Util.msgWait('Conciliando...', true);
		    
		    //registros seleccionados del grid banco
		    for(var i = 0; i < regSelecBanco.length; i ++) {
				var banco = {};
				banco.idEstatuscb = regSelecBanco[i].get('idEstatuscb');
				banco.fecOperacion = regSelecBanco[i].get('sFecOperacion');
				banco.cargoAbono = regSelecBanco[i].get('cargoAbono');
				banco.importe = regSelecBanco[i].get('importe');
				banco.referencia = regSelecBanco[i].get('referencia');
				banco.secuencia = regSelecBanco[i].get('secuencia');
				banco.noCheque = regSelecBanco[i].get('noCheque');
				banco.concepto = regSelecBanco[i].get('concepto');
				banco.noEmpresa = regSelecBanco[i].get('noEmpresa');
				banco.idBanco = regSelecBanco[i].get('idBanco');
				banco.idChequera = regSelecBanco[i].get('idChequera');
				matrizBanco[i] = banco;
				
				if(i==0) 
					tipoMovtoBco = regSelecBanco[i].get('cargoAbono');
				else if(tipoMovtoBco != regSelecBanco[i].get('cargoAbono')) {
						Ext.Msg.alert('SET', 'Debe seleccionar movimientos del mismo tipo, cargo o abono');
						return;
				}
			}
		    
		    //registros seleccionados del grid empresa
			for(var j = 0; j < regSelecEmpresa.length; j ++) {
				var empresa = {};
				empresa.idEstatusCb = regSelecEmpresa[j].get('idEstatusCb');
				empresa.fecValor = regSelecEmpresa[j].get('fecValorStr');
				empresa.idTipoMovto = regSelecEmpresa[j].get('idTipoMovto');
				empresa.importe = regSelecEmpresa[j].get('importe');
				empresa.referencia = regSelecEmpresa[j].get('referencia');
				empresa.noFolioDet = regSelecEmpresa[j].get('noFolioDet');
				empresa.noCheque = regSelecEmpresa[j].get('noCheque');
				empresa.noCuenta = regSelecEmpresa[j].get('noCuenta');
				empresa.idDivisa = regSelecEmpresa[j].get('idDivisa');
				empresa.noDocto = regSelecEmpresa[j].get('noDocto');
				empresa.concepto = regSelecEmpresa[j].get('concepto');
				empresa.noFactura = regSelecEmpresa[j].get('noFactura');
				empresa.beneficiario = regSelecEmpresa[j].get('beneficiario');
				empresa.tablaOrigen = regSelecEmpresa[j].get('tablaOrigen');
				empresa.idEstatusMov = regSelecEmpresa[j].get('idEstatusMov');
				matrizEmpresa[j] = empresa;
				
				if(regSelecEmpresa[j].get('idEstatusMov')=='T' || regSelecEmpresa[j].get('idEstatusMov')=='P'){
					Ext.Msg.alert('SET', 'Hay movimientos de la empresa Pendientes no se pueden conciliar');
					return;
				}
				
				if(j==0)
					tipoMovtoEmp = regSelecEmpresa[j].get('idTipoMovto');
				else if(tipoMovtoEmp != regSelecEmpresa[j].get('idTipoMovto')) {
						Ext.Msg.alert('SET', 'Debe seleccionar movimientos del mismo tipo, cargo o abono');
						return;
				}
			}
			
			if(regSelecBanco.length > 0 && regSelecEmpresa.length > 0) {
				if(tipoMovtoBco != tipoMovtoEmp) {
					Ext.Msg.alert('SET', 'Los movimientos seleccionados del banco y los de la empresa deben de ser del mismo tipo, cargo o abono');
					return;
				}
			}
			
			var criterio = {};
			criterio.idBanco = NS.iIdBanco;
			criterio.idChequera = NS.sIdChequera;
			criterio.aclaracion = Ext.getCmp(PF + 'txtAclaracion').getValue();
			criterio.bAjuste = bAjuste;
			criterio.diferencia = ''+BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtDiferencia').getValue());
			criterio.sCargoAbono = NS.sCargoAbono;
			matrizCriterios[i] = criterio; 
			
			var jsonStringBanco = Ext.util.JSON.encode(matrizBanco);
			var jsonStringEmpresa = Ext.util.JSON.encode(matrizEmpresa);
			var jsonStringCriterios = Ext.util.JSON.encode(matrizCriterios);
			
			ConciliacionBancoSetAction.ejecutarConciliacionManualBS(jsonStringBanco, jsonStringEmpresa, jsonStringCriterios, function(result, e) {
				if(result.msgUsuario != null  &&  result.msgUsuario != '' && result.msgUsuario != undefined) {
					for(var msg = 0; msg < result.msgUsuario.length; msg++) {
						BFwrk.Util.msgWait('Conciliando...', false);
						Ext.Msg.alert('SET',''+result.msgUsuario[msg]);
						NS.inicializaCajas();
					}
					NS.storeMovimientosEmpresa.load();
					NS.storeMovimientosBanco.load();
				}
			});
		});
	};
	
	NS.contenedorConciliacionManual = new Ext.FormPanel({
	    title: 'Conciliacion Manual Bancaria',
	    width: 803,
	    height: 606,
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
	                height: 580,
	                width: 790,
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 0,
	                        width: 768,
	                        height: 176,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Empresa:',
	                                x: 0,
	                                y: 0
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
	                            {
	                                xtype: 'fieldset',
	                                title: 'Seleccion',
	                                x: 1,
	                                y: 46,
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
	                                x: 219,
	                                y: 42
	                            },
	                            NS.cmbCriterio,
	                            {
	                                xtype: 'label',
	                                text: 'Valor:',
	                                x: 220,
	                                y: 97
	                            },
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
	                                x: 217,
	                                y: 115,
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
	                                x: 320,
	                                y: 115,
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
	                                x: 217,
	                                y: 115,
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
	                                x: 320,
	                                y: 115,
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
	                                xtype: 'button',
	                                id: PF+'cmdBuscar',
	                                name: PF+'cmdBuscar',
	                                text: 'Buscar',
	                                x: 669,
	                                y: 132,
	                                width: 70,
	                                listeners:{
				               			click:{
				           			   		fn:function(e){
				           			   			NS.inicializaCajas();
				           			   			NS.buscar();
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
	                        x: 1,
	                        y: 352,
	                        layout: 'absolute',
	                        height: 200,
	                        padding: 0,
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Id Grupo:',
	                                hidden: true,
	                                x: 0,
	                                y: 1
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Grupo:',
	                                hidden: true,
	                                x: 87,
	                                y: 1
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Id Rubro:',
	                                hidden: true,
	                                x: 277,
	                                y: 1
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Rubro:',
	                                hidden: true,
	                                x: 358,
	                                y: 1
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtGrupoBanco',
	                                name: PF+'txtGrupoBanco',
	                                hidden: true,
	                                x: 0,
	                                y: 24,
	                                width: 60,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupoBanco',NS.cmbGrupo.getId());
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbGrupo,
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtRubroBanco',
	                                name: PF+'txtRubroBanco',
	                                hidden: true,
	                                x: 276,
	                                y: 24,
	                                width: 60,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtRubroBanco',NS.cmbRubro.getId());
			                        			NS.accionarCmbRubro(comboValor);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbRubro,
	                            {
	                                xtype: 'checkbox',
	                                id: PF + 'chkContabiliza',
	                                name: PF + 'chkContabiliza',
	                                checked: false,
	                                hidden: true,
	                                x: 545,
	                                y: 23,
	                                boxLabel: 'Contabiliza'
	                            },
	                            {
	                                xtype: 'button',
	                                text: 'Crear Movimiento',
	                                x: 640,
	                                y: 24,
	                                width: 70,
	                                hidden: true,
	                                listeners:{
										click:{
											fn:function(){
												var matrizRegistros = new Array ();
	                        					var matrizCriterios = new Array ();
	                        					var regSelec=NS.gridMovsBanco.getSelectionModel().getSelections();
											
												if(regSelec.length <= 0)
												{
													BFwrk.Util.msgShow('Debe seleccionar al menos un registro del banco', 'INFO');
													return;
												}
												
												if(Ext.getCmp(PF + 'txtGrupoBanco').getValue() === '' || Ext.getCmp(PF + 'cmbGrupo').getValue() === ''
													|| Ext.getCmp(PF + 'txtRubroBanco').getValue() === '' || Ext.getCmp(PF + 'txtRubroBanco').getValue() === '')
												{
													BFwrk.Util.msgShow('Debe seleccionar Grupo y Rubro para crear el movimiento en SET', 'INFO');
													return;
												}
												
								    			 Ext.Msg.confirm( 'SET', '¿Esta seguro de crear los movimientos seleccionados de bancos en SET? ' , function(btn){
								  					if(btn=='yes'){

								  					}else{
								  						return;
								  					}
								     			 });
//												if(!confirm('¿Esta seguro de crear los movimientos seleccionados de bancos en SET? '))
//              										return;
												
												//valida si se contabiliza
												if(Ext.getCmp(PF + 'chkContabiliza').getValue() === true)
												{
													if(NS.contabi === '')
													{
														Ext.getCmp(PF + 'chkContabiliza').setValue(false);
//														if(!confirm('No se puede contabilizar, falta la equivalencia del rubro, ¿desea continuar? '))
//              												return;
										    			 Ext.Msg.confirm( 'SET', 'No se puede contabilizar, falta la equivalencia del rubro, ¿desea continuar? ' , function(btn){
											  					if(btn=='yes'){

											  					}else{
											  						return;
											  					}
											     			 });
													}
												}
												
												BFwrk.Util.msgWait('Creando Movimiento...', true);
												for(var i = 0; i < regSelec.length; i ++)
												{
													var registro = {};
													//registros seleccionados del grid bancos
													registro.idEstatuscb = regSelec[i].get('idEstatuscb');
													registro.fecOperacion = regSelec[i].get('sFecOperacion');
													registro.cargoAbono = regSelec[i].get('cargoAbono');
													registro.importe = regSelec[i].get('importe');
													registro.referencia = regSelec[i].get('referencia');
													registro.secuencia = regSelec[i].get('secuencia');
													registro.noCheque = regSelec[i].get('noCheque');
													registro.concepto = regSelec[i].get('concepto');
													registro.noEmpresa = regSelec[i].get('noEmpresa');
													registro.idBanco = regSelec[i].get('idBanco');
													registro.idChequera = regSelec[i].get('idChequera');
													matrizRegistros[i] = registro; 
												}
												
												if(Ext.getCmp(PF+'chkContabiliza').getValue() == 1)
													var chk = 'true';
												else
													var chk = 'false';
												
												var criterio = {};
												criterio.idBanco = NS.iIdBanco;
												criterio.idChequera = NS.sIdChequera;
												criterio.idGrupo = Ext.getCmp(PF+'txtGrupoBanco').getValue();
												criterio.idRubro = Ext.getCmp(PF+'txtRubroBanco').getValue();
												criterio.contabiliza = chk;
												criterio.descRubro = $('input[id*="'+ NS.cmbRubro.getId() +'"]').val();
												criterio.nomComputadora = ''+NS.NOMBRE_HOST;
												matrizCriterios[i] = criterio; 
												
												var jsonStringRegistros = Ext.util.JSON.encode(matrizRegistros);
												var jsonStringCriterios = Ext.util.JSON.encode(matrizCriterios);
												
												ConciliacionBancoSetAction.crearMovimientoSET(jsonStringRegistros, jsonStringCriterios, function(result, e){
			              			   			
													if(result.msgUsuario!==null  &&  result.msgUsuario!=='' && result.msgUsuario!= undefined)
													{
														BFwrk.Util.msgWait('Finalizado...', false);
														BFwrk.Util.msgShow(''+result.msgUsuario,'INFO');
														NS.storeMovimientosEmpresa.load();
														NS.storeMovimientosBanco.load();
													}
												});
											}
										}
									}
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Cargos:',
	                                x: 38,
	                                y: 63
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Abonos:',
	                                x: 216,
	                                y: 62
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Cargos:',
	                                x: 418,
	                                y: 62
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Abonos:',
	                                x: 615,
	                                y: 60
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantCargoBanco',
	                                name: PF+'txtCantCargoBanco',
	                                readOnly: true,
	                                value: 0,
	                                x: 0,
	                                y: 82,
	                                width: 50
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCargoBanco',
	                                name: PF+'txtCargoBanco',
	                                readOnly: true,
	                                style: 'text-align: right;',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                x: 60,
	                                y: 82,
	                                width: 110
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantAbonoBanco',
	                                name: PF+'txtCantAbonoBanco',
	                                readOnly: true,
	                                value: 0,
	                                x: 180,
	                                y: 82,
	                                width: 51
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtAbonoBanco',
	                                name: PF+'txtAbonoBanco',
	                                style: 'text-align: right;',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 240,
	                                y: 82,
	                                width: 110
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantCargoEmpresa',
	                                name: PF+'txtCantCargoEmpresa',
	                                readOnly: true,
	                                value: 0,
	                                x: 377,
	                                y: 82,
	                                width: 50
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCargoEmpresa',
	                                name: PF+'txtCargoEmpresa',
	                                style: 'text-align: right;',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 437,
	                                y: 82,
	                                width: 110
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantAbonoEmpresa',
	                                name: PF+'txtCantAbonoEmpresa',
	                                readOnly: true,
	                                value: 0,
	                                x: 576,
	                                y: 82,
	                                width: 50
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtAbonoEmpresa',
	                                name: PF+'txtAbonoEmpresa',
	                                style: 'text-align: right;',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 635,
	                                y: 82,
	                                width: 110
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Importe Total:',
	                                x: 156,
	                                y: 117
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Importe Total:',
	                                x: 548,
	                                y: 116
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalBanco',
	                                name: PF+'txtTotalBanco',
	                                style: 'text-align: right;',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 240,
	                                y: 112,
	                                width: 110
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalEmpresa',
	                                name: PF+'txtTotalEmpresa',
	                                style: 'text-align: right;',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 635,
	                                y: 112,
	                                width: 110
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Aclaracion:',
	                                x: 2,
	                                y: 150
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Diferencia:',
	                                x: 378,
	                                y: 151
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtAclaracion',
	                                name: PF+'txtAclaracion',
	                                x: 66,
	                                y: 148,
	                                width: 284
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtDiferencia',
	                                name: PF+'txtDiferencia',
	                                style: 'text-align: right;',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 436,
	                                y: 148,
	                                width: 110
	                            },
	                            {
	                                xtype: 'button',
	                                text: 'Ejecutar',
	                                x: 587,
	                                y: 150,
	                                width: 70,
	                                listeners:{
										click:{
											fn:function(){
												NS.ejecutar();
											}
										}
									}
	                            },{
	                                xtype: 'button',
	                                text: 'Limpiar',
	                                x: 674,
	                                y: 150,
	                                width: 70,
	                                listeners:{
										click:{
											fn:function(){
												NS.contenedorConciliacionManual.getForm().reset();
												NS.storeMovimientosBanco.removeAll();
												NS.gridMovsBanco.getView().refresh();
												NS.storeMovimientosEmpresa.removeAll();
												NS.gridMovsEmpresa.getView().refresh();
												NS.storeDatos.removeAll();
												NS.gridDatos.getView().refresh();
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
	NS.contenedorConciliacionManual.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});