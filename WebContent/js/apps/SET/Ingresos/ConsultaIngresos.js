/*
@author: Jessica Arelly Cruz Cruz
@since: 12/09/2011
*/
Ext.onReady(function(){
	
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Conciliacion.ConsultaIngresos');
	
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.NOMBRE_HOST = apps.SET.HOST_NAME_LOCAL;
	NS.idUsuario = apps.SET.iUserId;
	
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
	
	NS.parametroBus = '';
	NS.iNoCliente = '';
	NS.ietu = false;
	NS.iva = false;
	NS.concepto = '';
	
	var jsonStringRegistros = '';
	var jsonStringCriterios = '';
	var textField = new Ext.form.TextField();
	
	//Limpiar ventana facturas
	NS.limpiar= function(){
		NS.cmbDepto.setValue('');
		NS.cmbProyect.setValue('');
		NS.cmbCCosto.setValue('');
        NS.cmbCliente.setValue('');
        NS.txtImpIng.setValue('');
        NS.txtFactCXC.setValue('');
        NS.txtDif.setValue('');
	};
	
	//Formato de un numero a monetario
	NS.formatNumber = function(num,prefix){
		num = num.toString();
		if(num.indexOf('.')>-1){
			if(num.substring(num.indexOf('.')).length<3){
				num = num + '0';
			}
		}
		else{
			num = num + '.00';
		}
		prefix = prefix || '';
		var splitStr = num.split('.');
		var splitLeft = splitStr[0];
		var splitRight = splitStr.length > 1 ? '.' + splitStr[1] : '';
		var regx = /(\d+)(\d{3})/;
		while (regx.test(splitLeft)) {
			splitLeft = splitLeft.replace(regx, '$1' + ',' + '$2');
		}
			return prefix + splitLeft + splitRight;
	};
	//Quitar formato a las cantidades
	NS.unformatNumber = function(num) {
		return num.replace(/(,)/g,''); 
	};
	
	
	//******************************************************************************************
	//                                CONTROLES DE BUSQUEDA
	//******************************************************************************************
	
	
    
       
        	
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa:',
	    x: 0,
	    y: 0
	});
    
    NS.txtEmpresa = new Ext.form.TextField({
        id: PF+'txtEmpresa',
        name: PF+'txtEmpresa',
        value: NS.GI_ID_EMPRESA,
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
			}
		}
	}); 
	
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
		,value: NS.GI_NOM_EMPRESA
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
	
	//******************************************************************************************
	//                                CONTROLES DE BUSQUEDA
	//******************************************************************************************

	
	
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
	
	
	NS.storeCuentaContable = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
					noEmprea: 0,
					idGrupo: '',
					idRubro: ''
					},
		paramOrder:['noEmpresa', 'idGrupo', 'idRubro'],
		directFn: ConciliacionBancoSetAction.getCuentaContable,
		fields: [
			 {name: 'cuentaContable'}
		],
		listeners: {
			load: function(s, records){
						
						Ext.getCmp( PF + 'txtCuentaContable').setValue('');
						
						if(records.length == 0)
							Ext.getCmp( PF + 'txtCuentaContable').setValue('Sin Cuenta');
						else
							Ext.getCmp( PF + 'txtCuentaContable').setValue(records[0].get('cuentaContable'));
														
							
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
        ,y: 100
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
        ,y: 100
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
        ,y: 100
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
        ,y: 100
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
        ,y: 100
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
	
	NS.dataIdentificado = [
						  [ '1', 'SI' ]
					     ,[ '2', 'NO' ]
						  ];
	  	NS.storeIdentificado = new Ext.data.SimpleStore( {
			idProperty: 'id',  		
			fields : [ 
				{name :'id'}, 
				{name :'descripcion'}
		 	]
		});
		NS.storeIdentificado.loadData(NS.dataIdentificado);
		
		NS.cmbIdentificado = new Ext.form.ComboBox({
			store: NS.storeIdentificado
			,name: PF+'cmbIdentificado'
			,id: PF+'cmbIdentificado'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
	        ,x: 217
	        ,y: 100
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
						NS.agregarCriterioValor('IDENTIFICADOS', combo.getId(), 'valor');
					}
				}
			}
		});
		
	NS.storeGrupo = new Ext.data.DirectStore( {
		paramOrder:['idTipoMovto', 'noEmpresa'],
		paramsAsHash: false,
		baseParams:{idTipoMovto:"I", noEmpresa:5},
		root: '',
		directFn: TraspasosAction.llenarComboGrupoVX,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		]	
	});
	/*
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
	*/
	
	
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo
		,name: PF+'cmbGrupo'
		,id: PF+'cmbGrupo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 65
        ,y: 15
        ,width: 180
		,valueField:'idGrupo'
		,displayField:'descGrupo'
		,autocomplete: true
		,emptyText: 'Seleccione un grupo'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtGrupoBanco',NS.cmbGrupo.getId());
					Ext.getCmp(PF + 'cmbRubro').reset();
					Ext.getCmp(PF+'txtRubroBanco').reset();
					
				//	NS.storeRubro.baseParams.condicion = 'id_grupo = \'' + combo.getValue() + '\'';
				//	NS.storeRubro.load();
					
					NS.storeRubro.removeAll();
					NS.storeRubro.baseParams.idGrupo = parseInt(combo.getValue());									
					NS.storeRubro.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtEmpresa').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF+'txtEmpresa').getValue());
					NS.storeRubro.load();
				}
			}
		}
	});
	/*
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
			 {name: 'descripcion'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	*/
	NS.storeRubro = new Ext.data.DirectStore( {	
 		root: '',
 		paramOrder:['idGrupo'],	            
 			paramsAsHash: false,
 			baseParams:{
 				idGrupo:0,
 				noEmpresa:0
 				},
 			directFn: ConsultaPropuestasAction.llenarComboRubros,
 			idProperty: 'idRubro',  		
 			fields: [
 				{name: 'idRubro' },
 				{name: 'descRubro'},
 				{name: 'idSubGrupo'}
 			],
 			listeners: {
 				load: function(s, records) {
 					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubroEgreso, msg: "Cargando..."});
 					if (records.length == null || records.length <= 0)
 						Ext.Msg.alert('SET', 'No existen Rubros en este grupo');
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
        ,x: 341
        ,y: 15
        ,width: 180
		,valueField:'idRubro'
		,displayField:'descRubro'
		,autocomplete: true
		,emptyText: 'Seleccione un rubro'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.llenaComboGrupoConta(combo.getValue());
					
					BFwrk.Util.updateComboToTextField(PF+'txtRubroBanco',NS.cmbRubro.getId());
					
					/*NS.accionarCmbRubro(combo.getValue());
					NS.storeCuentaContable.removeAll();
					NS.storeCuentaContable.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtEmpresa').getValue());
					NS.storeCuentaContable.baseParams.idGrupo = Ext.getCmp(PF + 'txtGrupoBanco').getValue();
				    NS.storeCuentaContable.baseParams.idRubro = Ext.getCmp(PF + 'txtRubroBanco').getValue();
					NS.storeCuentaContable.load();*/
				}
			}
		}
	});
	
	NS.dataCriterio = [
					  [ '0', 'BANCO' ]
				     ,[ '1', 'CHEQUERA' ]
				     ,[ '2', 'FECHA' ]
				     //,[ '3', 'ESTATUS' ]
				     ,[ '4', 'MONTOS' ]
				     //,[ '5', 'CARGO/ABONO' ]
				     //,[ '6', 'FORMA DE PAGO' ]
				      ,['7', 'IDENTIFICADOS']
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
		,y: 55
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
					else if(combo.getValue() == 7)
					{
						//NO IDENTIFICADOS
						Ext.getCmp(PF + 'cmbIdentificado').reset();
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'cmbIdentificado').setDisabled(false);
						Ext.getCmp(PF + 'cmbIdentificado').setVisible(true);
						
						NS.agregarCriterioValor ('IDENTIFICADOS', Ext.getCmp(PF + 'cmbIdentificado').getId(), 'criterio');
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
						NS.storeMovimientosEmpresa.baseParams.identificados = 0;
					}
				}
			}
		}
	
	
	});
	
	NS.smBan = new Ext.grid.CheckboxSelectionModel({
		checkOnly: false,
		singleSelect: false
		
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
			iNoEmpresa: NS.GI_ID_EMPRESA
		},
		root: '',
		paramOrder:['iIdBanco','sIdChequera','sFecIni','sFecFin','sEstatus','uMontoIni','uMontoFin','sCargoAbono','iFormaPago', 'iNoEmpresa'],
		directFn: ConciliacionBancoSetAction.llenarMovsBanco, 
		fields: [
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
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosBanco, msg:"Buscando..."});
				
				if(records.length == 0)
					BFwrk.Util.msgShow('No se encontraron movimientos del banco para conciliar','INFO');
				else
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
		            //Ext.getCmp(PF+'txtCantCargoBanco').setValue(iMovCargo);
		            Ext.getCmp(PF+'txtCargoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpCargo)*100)/100));
		            Ext.getCmp(PF+'txtCantAbonoBanco').setValue(iMovAbono);
		            Ext.getCmp(PF+'txtAbonoBanco').setValue(BFwrk.Util.formatNumber(Math.round((uImpAbono)*100)/100));
		            Ext.getCmp(PF+'txtTotalBanco').setValue(BFwrk.Util.formatNumber(Math.round((uTotal)*100)/100));
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
		y :181,
		frame: true,
		hidden:true,
		
		title :'Movimientos del Banco',
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
                
            },
		columns : [ NS.smBan,
		{
			header :'Estatus',width :50,dataIndex :'idGrupo'
		},            
		{
			header :'Estatus',width :50,dataIndex :'descGrupo'
		},
		{
			header :'Estatus',width :50,dataIndex :'idRubro'
		},
		{
			header :'Estatus',width :50,dataIndex :'descRubro'
		},
		{
			header :'Estatus',width :50,dataIndex :'idEstatuscb'
		},{
			header :'Fecha',width :100,dataIndex :'fecOperacion'
		},{
			header :'C/A',width :30,dataIndex :'cargoAbono', hidden: true
		},{
			header :'Importe', css: 'text-align:right;', width :100,dataIndex :'importe', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Referencia',width :160,dataIndex :'referencia'
		},{
			header :'Secuencia',width :80,dataIndex :'secuencia', hidden: true
		},{
			header :'No. Cheque',width :80,dataIndex :'noCheque', hidden: true
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
			rowclick:{
				fn:function(grid,record,e){
					var records = NS.storeMovimientosBanco.data.items;
					var id = NS.gridMovsBanco.getGridEl().id;
					var activado = NS.smBan.isSelected(record);
					NS.calculaDiferencia('banco', records, activado, record);
				},
				click:{
					fn: function(){
						return false;
						}
					}
			}
		}
	});
	
	//NS.gridMovsBanco.getSelectionModel().getSelection();
	
	NS.calculaDiferencia = function(movimiento, records, activado, k)
	{
		if(records.length <= 0)
			NS.uDiferencia = 0;
		if(movimiento === 'banco')
		{	
			if(activado === false)
			{
                if(records[k].get('cargoAbono') === 'C')
                	NS.uDiferencia = NS.uDiferencia - records[k].get('importe');
                else
                	NS.uDiferencia = NS.uDiferencia + records[k].get('importe');
			}
			else
			{
		        if(records[k].get('idEstatuscb') === 'C' || records[k].get('idEstatuscb') === 'A' || records[k].get('idEstatuscb') === 'M')
	                BFwrk.Util.msgShow('Este registro ya fue conciliado','INFO');
	            else
	            {
	                if(records[k].get('cargoAbono') === 'C')
	                	NS.uDiferencia = NS.uDiferencia + records[k].get('importe');
	                else
	                	NS.uDiferencia = NS.uDiferencia - records[k].get('importe');
	            }
	        }
		}
		if(movimiento === 'empresa')
		{
			if(activado === false)
			{
                if(records[k].get('idEstatuscb') === 'C' || records[k].get('idEstatuscb') === 'A' || records[k].get('idEstatuscb') === 'M')
                	NS.uDiferencia = NS.uDiferencia + records[k].get('importe');
                else
                	NS.uDiferencia = NS.uDiferencia - records[k].get('importe');
			}
			else
			{
		        if(records[k].get('idEstatuscb') === 'C' || records[k].get('idEstatuscb') === 'A' || records[k].get('idEstatuscb') === 'M')
	                BFwrk.Util.msgShow('Este registro ya fue conciliado','INFO');
	            else
	            {
	                if(records[k].get('idTipoMovto') === 'A')
	                	NS.uDiferencia = NS.uDiferencia + records[k].get('importe');
	                else
	                	NS.uDiferencia = NS.uDiferencia - records[k].get('importe');
	            }
	        }
		}
		Ext.getCmp(PF + 'txtDiferencia').setValue(BFwrk.Util.formatNumber(Math.round((NS.uDiferencia)*100)/100));
	}
	
	NS.smEmp = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true,
		checkOnly: false,
		singleSelect: true
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
			iNoEmpresa: NS.GI_ID_EMPRESA,
			iUserId: NS.idUsuario,
			identificados: 0
		},
		root: '',
		paramOrder:['iIdBanco','sIdChequera','sFecIni','sFecFin','sEstatus','uMontoIni','uMontoFin','sCargoAbono','iFormaPago', 'iNoEmpresa', 'iUserId', 'identificados'],
		directFn: ConciliacionBancoSetAction.llenarMovsEmpresaVIngresos,
		fields: [
			{name: 'idGrupo'},
			{name: 'descGrupo'},
			{name: 'idRubro'},
			{name: 'descRubro'},
			{name: 'idEstatusCb'},
			{name: 'fecValor'},
			{name: 'idTipoMovto'},
			{name: 'importe'},
			{name: 'idTipoOperacion'},
			{name: 'referencia'},
			{name: 'noFolioDet'},
			{name: 'noCheque'},
			{name: 'noCuenta'},
			{name: 'idDivisa'},
			{name: 'noDocto'},
			{name: 'concepto'},			
			{name: 'tablaOrigen'},
			{name: 'idEstatusMov'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosEmpresa, msg:"Buscando..."});
				
				if(records.length == 0)
				{
					//Ext.getCmp('panel1').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					//Ext.getCmp('panel2').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					//Ext.getCmp('panel3').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					BFwrk.Util.msgShow('No se encontraron movimientos.','INFO');
					Ext.getCmp(PF + 'cmdGrafica').setDisabled(true);
				}
				else
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
		            
		            //Graficas
		            Ext.getCmp(PF + 'cmdGrafica').setDisabled(false);
				}
			}
		}
	}); 
	
	NS.gridMovsEmpresa  = new Ext.grid.GridPanel({
		store : NS.storeMovimientosEmpresa,
		id: PF+'gridMovsEmpresa',
		name: PF+'gridMovsEmpresa',
		width: 975,
       	height: 190,
		x :0,
		y :155,
		frame: true,
		viewConfig: {
		  	// CONFIGURA LAS OPCIONES DE COLORES DE REGISTROS EN BASE A VALORES DETERMINADOS
		  	// EN SU COLUMNA CONCEPTO
		  	
	    	getRowClass: function(record, index) {
	        	var c = record.get('concepto');
	        	var posicion = c.indexOf('/'); 
	        	if ( posicion < 0) {
	            	return 'row-font-color-red';
	        	} 
	        				            	
		 	}//end getRowClass
		 	
		  },//end viewConfig
		title :'Movimientos de la Empresa',
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
		columns : [ NS.smEmp,
		            {
			header :'Id Grupo',width :50,dataIndex :'idGrupo', hidden:true
		},
		{
			header :'Grupo',width:150, dataIndex :'descGrupo',
		},
		{
			header :'Id Rubro',width:50, dataIndex :'idRubro',hidden:true
		},
		{
			header :'Rubro',width:150, dataIndex :'descRubro'
		},
		{
			header :'Estatus',width :50,dataIndex :'idEstatusCb', hidden:true
		},{
			header :'Fecha',width :80,dataIndex :'fecValor'
		},{
			header :'C/A',width :30,dataIndex :'idTipoMovto', hidden: true
		},{
			header :'Importe',width :100,dataIndex :'importe', css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Tipo de Operacion',width :30,dataIndex :'idTipoOperacion',hidden:true
		},{
			header :'Concepto',width :260,dataIndex :'concepto'
		},{
			header :'Referencia',width :120,dataIndex :'referencia'
		},{
			header :'Folio Det',width :80,dataIndex :'noFolioDet', hidden: true
		},{
			header :'No. Cheque',width :80,dataIndex :'noCheque', hidden: true
		},{
			header :'No. Cuenta',width :140,dataIndex :'noCuenta', hidden: true 
		},{
			header :'Divisa',width :60,dataIndex :'idDivisa' 
		},{
			header :'No. Documento',width :100,dataIndex :'noDocto', hidden: true
		},{
			header :'Tabla',width :60,dataIndex :'tablaOrigen', hidden: true
		},{
			header :'Id Estatus Mov',width :100,dataIndex :'idEstatusMov', hidden: true
		}]
		}),
		sm: NS.smEmp,
		listeners:{
			rowclick:{
				fn:function(grid, record, e){
					var records = NS.storeMovimientosEmpresa.data.items;
					var id = NS.gridMovsEmpresa.getGridEl().id;
					var activado = NS.smEmp.isSelected(record);
					NS.calculaDiferencia('empresa', records, activado, record);
					NS.limpiaConta();
				}
			}
		}
	});
	
	//*******************************************Esto es para la parte contable *******************************
	NS.labGrupo = new Ext.form.Label({
		text: 'Grupo'
	});
	
	NS.labSubGrupo = new Ext.form.Label({
		text: 'Sub Grupo',
        x: 190
	});
	
	NS.labSubSubGrupo = new Ext.form.Label({
		text: 'Sub Sub Grupo',
        x: 380
	});
	
	NS.labCtaConta = new Ext.form.Label({
		text: 'Cta. Contable',
        x: 570
	});
	
	NS.storeGrupoConta = new Ext.data.DirectStore( {
		paramOrder: ['idTipoMovto', 'idSubGrupo'],
		paramsAsHash: false,
		baseParams: {idTipoMovto: 'I'},
		root: '',
		directFn: PropuestaPagoManualAction.llenaComboGrupoConta,
		idProperty: 'idSubGrupo',  		
		fields: [
			{name: 'idSubGrupo' },
			{name: 'descSubGrupo'}
		]
	});
	
	NS.cmbGrupoConta = new Ext.form.ComboBox({
		store: NS.storeGrupoConta,
		id: PF + 'cmbGrupoConta',
		y: 15,		
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSubGrupo',
		displayField: 'descSubGrupo',
		autocomplete: true,
		emptyText: 'Grupo',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					NS.cmbSubSubGrupo.reset();
					NS.storeSubSubGrupo.removeAll();
					NS.txtCtaConta.setValue('');
		
					NS.cmbSubGrupo.reset();
					NS.storeSubGrupo.removeAll();
					NS.storeSubGrupo.baseParams.idSubGrupo = parseInt(combo.getValue());									
					NS.storeSubGrupo.load();
				}
			}
		}
	});
	
	NS.storeSubGrupo = new Ext.data.DirectStore( {
		paramOrder:['idSubGrupo'],
		paramsAsHash: false,
		root: '',
		directFn: PropuestaPagoManualAction.llenaComboSubGrupo,
		idProperty: 'idSubSubGrupo',  		
		fields: [
			{name: 'idSubSubGrupo'},
			{name: 'descSubSubGrupo'}
		]	
	});
	
	NS.cmbSubGrupo = new Ext.form.ComboBox({
		store: NS.storeSubGrupo,
		id: PF + 'cmbSubGrupo',
		x: 190,
		y: 15,
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSubSubGrupo',
		displayField: 'descSubSubGrupo',
		autocomplete: true,
		emptyText: 'Sub Grupo',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					NS.txtCtaConta.setValue('');
					NS.cmbSubSubGrupo.reset();
					NS.storeSubSubGrupo.removeAll();
					NS.storeSubSubGrupo.baseParams.idRubroC = parseInt(combo.getValue());									
					NS.storeSubSubGrupo.load();
				}
			}
		}
	});
	
	NS.storeSubSubGrupo = new Ext.data.DirectStore( {
		paramOrder:['idRubroC'],
		paramsAsHash: false,
		root: '',
		directFn: PropuestaPagoManualAction.llenaComboSubSubGrupo,
		idProperty: 'idRubroC',  		
		fields: [
			{name: 'idRubroC' },
			{name: 'descRubroC'}
		]	
	}); 
 	
	NS.cmbSubSubGrupo = new Ext.form.ComboBox({
		store: NS.storeSubSubGrupo,
		id: PF + 'cmbSubSubGrupo',
		x: 380,
		y: 15,
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idRubroC',
		displayField: 'descRubroC',
		autocomplete: true,
		emptyText: 'Sub Sub Grupo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					var mdatosConta = new Array();
					var datosConta = {};
					
					Ext.getCmp(PF + 'txtCtaConta').setValue('');
					
					datosConta.idSubGrupo = NS.cmbGrupoConta.getValue();
					datosConta.idSubSubGrupo = NS.cmbSubGrupo.getValue();
					datosConta.idRubroC = combo.getValue();
					datosConta.idGrupo = Ext.getCmp(PF + 'txtGrupoBanco').getValue();
					datosConta.idRubro = Ext.getCmp(PF + 'txtRubroBanco').getValue();
					datosConta.noEmpresa = Ext.getCmp(PF+'txtEmpresa').getValue();
					mdatosConta[0] = datosConta;
					
					var jsonDatos = Ext.util.JSON.encode(mdatosConta);
					
					PropuestaPagoManualAction.buscaCtaContable(jsonDatos, function(resp, e) {
						if(resp.length > 15)
							Ext.Msg.alert('SET', resp);
						else {
							Ext.getCmp(PF + 'txtCtaConta').setValue(resp);
//							Ext.getCmp(PF + 'Modificar').setDisabled(false);
						}
					});
				}
			}
		}
	});
	
	NS.txtCtaConta = new Ext.form.TextField({
		id: PF + 'txtCtaConta',
		x: 570,
        y: 15,
        width: 125,
        readOnly:true
    });
	
	NS.contConta = new Ext.form.FieldSet({
		title: 'Configuración Contable',
		width: 975,
	    height: 75,
	    x: 1,
	    y: 470,
	    layout: 'absolute',
	    items:[
	        NS.labGrupo,
	        NS.labSubGrupo,
	        NS.labSubSubGrupo,
	        NS.cmbGrupoConta,
	        NS.cmbSubGrupo,
	        NS.cmbSubSubGrupo,
	        NS.labCtaConta,
	        NS.txtCtaConta
	    ]
	});
	
	NS.limpiaConta = function() {
		NS.storeGrupoConta.removeAll();
		NS.storeSubGrupo.removeAll();
		NS.storeSubSubGrupo.removeAll();
		
		NS.cmbGrupoConta.reset();
        NS.cmbSubGrupo.reset();
        NS.cmbSubSubGrupo.reset();
        NS.txtCtaConta.setValue('');
//        Ext.getCmp(PF + 'Modificar').setDisabled(true);
	};
	
	NS.llenaComboGrupoConta = function(comboValue) {
		var idSubGrupo = NS.storeRubro.getById(parseInt(comboValue)).get('idSubGrupo');
		
		NS.limpiaConta();
		
		NS.storeGrupoConta.baseParams.idSubGrupo = idSubGrupo;
		NS.storeGrupoConta.load();
	};
	
	////*******************************************Termina la parte contable *******************************
	
	NS.accionarCmbEmpresa = function(comboValor)
	{
		NS.storeMovimientosEmpresa.removeAll();
		NS.storeMovimientosEmpresa.baseParams.identificados = 0;
		NS.storeCmbChequera.baseParams.empresa = comboValor;
		NS.storeMovimientosEmpresa.baseParams.iNoEmpresa = comboValor;
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
		Ext.getCmp(PF+'cmbIdentificado').setVisible(false);
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
					BFwrk.Util.msgShow('Ya indicó el criterio, necesita borrarlo antes','WARNING');
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
	
	NS.inicializaCajas = function()
	{
		//Ext.getCmp(PF+'txtCantCargoBanco').setValue(0);
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
	
	NS.opcionSel = function()
	{
		//valida opcion seleccionada
	    var value = Ext.getCmp(PF + 'movimiento').getValue();
	    var valor = parseInt(value.getGroupValue());
	    return valor;
	}
	
	NS.buscar = function() {
		var banco = false;
	   	var chequera = false;
		var datosBusqueda = NS.storeDatos.data.items;
		
		if(datosBusqueda.length <= 0) {
			BFwrk.Util.msgShow('Debe indicar al menos el banco y la chequera en los criterios de búsqueda','WARNING');
			return;
		}
		
		for(var i = 0; i < datosBusqueda.length; i++) {
			if(datosBusqueda[i].get('criterio') == 'BANCO') banco = true;
			if(datosBusqueda[i].get('criterio') == 'CHEQUERA') chequera = true;
			if(datosBusqueda[i].get('id') == '') {
				BFwrk.Util.msgShow('Debe indicar el valor de ' + datosBusqueda[i].get('criterio'),'WARNING');
				return;
			}
		}
		
		if (!banco || !chequera) {
			BFwrk.Util.msgShow('Debe indicar el banco y la chequera en los criterios de búsqueda','WARNING');
			return;
		}
		NS.storeGrupo.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		NS.storeGrupo.load(); 
		
		for(var i = 0; i < datosBusqueda.length; i = i + 1) {
   			if(datosBusqueda[i].get('criterio') == 'BANCO') {
				NS.storeMovimientosBanco.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
				NS.storeMovimientosEmpresa.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
				NS.iIdBanco = parseInt(datosBusqueda[i].get('id'));
				
   			}else if(datosBusqueda[i].get('criterio') == 'CHEQUERA') {
   				NS.storeMovimientosBanco.baseParams.sIdChequera = datosBusqueda[i].get('id');
				NS.storeMovimientosEmpresa.baseParams.sIdChequera = datosBusqueda[i].get('id');
				NS.sIdChequera = datosBusqueda[i].get('id');
				
   			}else if(datosBusqueda[i].get('criterio') == 'FECHA') {
   				var valorFechas = datosBusqueda[i].get('valor');
   				var ini = NS.obtenerValIni(valorFechas);
				var fin = NS.obtenerValFin(valorFechas);
   				NS.storeMovimientosBanco.baseParams.sFecIni = ini;
   				NS.storeMovimientosBanco.baseParams.sFecFin = fin;
   				NS.storeMovimientosEmpresa.baseParams.sFecIni = ini;
   				NS.storeMovimientosEmpresa.baseParams.sFecFin = fin;
   				NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
   				NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
   				
   			}else if(datosBusqueda[i].get('criterio') == 'ESTATUS') {
   				NS.storeMovimientosBanco.baseParams.sEstatus = datosBusqueda[i].get('id');
				NS.storeMovimientosEmpresa.baseParams.sEstatus = datosBusqueda[i].get('id');
				NS.sEstatus = datosBusqueda[i].get('id');
				
   			}else if(datosBusqueda[i].get('criterio') == 'MONTOS') {
   				var valorImporte = datosBusqueda[i].get('valor');
   				var ini = NS.obtenerValIni(valorImporte);
				var fin = NS.obtenerValFin(valorImporte);
				NS.storeMovimientosBanco.baseParams.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
				NS.storeMovimientosBanco.baseParams.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
				NS.storeMovimientosEmpresa.baseParams.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
				NS.storeMovimientosEmpresa.baseParams.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
				NS.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
				NS.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
				
   			}else if(datosBusqueda[i].get('criterio') == 'CARGO/ABONO') {
   				NS.storeMovimientosBanco.baseParams.sCargoAbono = datosBusqueda[i].get('id');
				NS.storeMovimientosEmpresa.baseParams.sCargoAbono = datosBusqueda[i].get('id');
				NS.sCargoAbono = datosBusqueda[i].get('id');
				
   			}else if(datosBusqueda[i].get('criterio') == 'FORMA DE PAGO') {
   				NS.storeMovimientosBanco.baseParams.iFormaPago = parseInt(datosBusqueda[i].get('id'));
				NS.storeMovimientosEmpresa.baseParams.iFormaPago = parseInt(datosBusqueda[i].get('id'));
				NS.iFormaPago = parseInt(datosBusqueda[i].get('id'));
   			}else if(datosBusqueda[i].get('criterio') == 'IDENTIFICADOS') {
   				NS.storeMovimientosEmpresa.baseParams.identificados = parseInt(datosBusqueda[i].get('id'));
   			}
   		}
		
   		if(NS.opcionSel() == 0) {
			if(NS.validarConciliacion())
				NS.storeMovimientosBanco.load();
			else
				return;
		}else if(NS.opcionSel() == 1) {
			NS.storeMovimientosEmpresa.load();
			return;
		}else if(NS.opcionSel() == 2) {
			if(NS.validarConciliacion()) {
				NS.storeMovimientosEmpresa.load();
				NS.storeMovimientosBanco.load();
			}else
				return;
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
	
	NS.validarConciliacion = function()
	{
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
	}
	
	//*********************************************************************************************************
	
	//******************************************************** Este codigo es para el detalle de las facturas de CXC *****************************************//
	
	NS.lblDepto = new Ext.form.Label({
		text: 'Departamento'
	});
	
	NS.lblProyecto = new Ext.form.Label({
		text: 'Proyecto',
	    x: 240
	});
	
	NS.lblCCosto = new Ext.form.Label({
		text: 'Centro de Costo',
	    x: 480
	});
	
	NS.storeDepto = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noEmpresa: 0
		},
		root: '',
		paramOrder:['noEmpresa'],
		directFn: ConciliacionBancoSetAction.llenarComboDeptos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDepto, msg:"Cargando..."});
				/*if(records.length==null || records.length<=0)
					{
						Ext.Msg.alert('SET','No Existen formas de pago');
					}*/
			}
		}
	});
	
	NS.cmbDepto = new Ext.form.ComboBox({
		store: NS.storeDepto
		,name: PF+'cmbDepto'
		,id: PF+'cmbDepto'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,y: 15
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Departamentos'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn:function(combo, valor) {
				}
			}
		}
	});
	
	NS.storeProyect = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_proyecto',
			campoDos:'desc_proyecto',
			tabla:'cat_proyectos',
			condicion: '',
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
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProyect, msg:"Cargando..."});
				/*if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existe proyecto');
				}*/
			}
		}
	}); 
	
	NS.cmbProyect = new Ext.form.ComboBox({
		store: NS.storeProyect
		,name: PF+'cmbProyect'
		,id: PF+'cmbProyect'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 240
        ,y: 15
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Proyectos'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					
				}
			}
		}
	});
	
	NS.storeCCosto = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'centro_costo',
			campoDos:'desc_centro_costo',
			tabla:'cat_centro_costo',
			condicion: '',
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
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCCosto, msg:"Cargando..."});
				/*if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existe proyecto');
				}*/
			}
		}
	}); 
	
	NS.cmbCCosto = new Ext.form.ComboBox({
		store: NS.storeCCosto
		,name: PF+'cmbCCosto'
		,id: PF+'cmbCCosto'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 480
        ,y: 15
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Centro de Costo'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					
				}
			}
		}
	});
	
	NS.lblCliente = new Ext.form.Label({
		text: 'Cliente',
		y: 42
	});
    
	NS.storeCliente = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{prefijoProv: '', iNoProveedor: 0,	noEmpresa: 0},
		root: '',
		paramOrder:['prefijoProv', 'iNoProveedor', 'noEmpresa'],
		root: '',
		directFn: ConsultasAction.llenarComboProveedores, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCliente, msg:"Cargando..."});
				
				if(records.length==null || records.length<=0)
				{
					Ext.getCmp(PF + 'cmbProveedor').setValue('');
					Ext.Msg.alert('SET','No Existen Clientes con ese Nombre');
					return;
				}
				NS.iNoCliente = records[0].get('id');
				Ext.getCmp(PF + 'cmbCliente').setValue(records[0].get('descripcion'));
				NS.parametroBus = '';
			}
		}
	}); 
	
	//Este combo carga los datos dependiendo la(s) letra(s) que se tecleen en el combo y se da clik en el boton
	//combo para el Proveedor
	NS.cmbCliente = new Ext.form.ComboBox({
		store: NS.storeCliente,
		id: PF+'cmbCliente',
		typeAhead: true,
		mode: 'local',
		mode: 'remote',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		hideTrigger: true,
        pageSize: 10,
      	y: 57,
        width: 300,
        tabIndex: 4,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Escriba prefijo...',
		triggerAction: 'all',
		value: '',
		disabled: false,
		listeners:{
			beforequery: function(qe){
				if(Ext.getCmp(PF+'txtEmpresa').getValue() == '') {
					NS.storeCliente.removeAll();
					NS.cmbCliente.reset();
					Ext.Msg.alert('SET', 'Seleccione primero la empresa');
				}else {
					NS.parametroBus = qe.combo.getRawValue();
					NS.storeCliente.baseParams.prefijoProv = NS.parametroBus;
					NS.storeCliente.baseParams.iNoProveedor = 0;
					NS.storeCliente.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtEmpresa').getValue());
					NS.storeCliente.load();
				}
			},
			select:{
				fn:function(combo, valor) {
				 	 NS.iNoCliente = combo.getValue();
				 	 NS.buscaFacturas();
				}
			}
		}
	});
	
	//Store detalle de las facturas de cxc
	NS.storeFacCXC = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['noCliente', 'noEmpresa'],
		directFn: ConciliacionBancoSetAction.consultarFacturasCXC, 
		fields: [
				{name: 'noFactura'},
				{name: 'importe'},
				{name: 'importeA'},
				{name: 'idDivisa'},
				{name: 'descDivisa'},
				{name: 'fecFact'},
				{name: 'concepto'},
				{name: 'noBenef'},
				{name: 'razonSocial'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFacCXC, msg: "Buscando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen facturas de este cliente!!');
			}
		}
	});
	
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.selecFacCXC = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columFacCXC = new Ext.grid.ColumnModel([
	    NS.selecFacCXC,
	    {header :'Factura', width :90, sortable :true, dataIndex :'noFactura'},
	    {header :'Importe', width :100, css: 'text-align:right;', sortable :true, dataIndex :'importe', renderer: BFwrk.Util.rendererMoney},
	    {header :'Importe a Aplicar', css: 'text-align:right;', width :100, sortable :true,editor:textField, dataIndex :'importeA', renderer: BFwrk.Util.rendererMoney},
	    {header :'Divisa', width :77, sortable :true, dataIndex :'descDivisa'},
	    {header :'Fecha', width :80, sortable :true, dataIndex :'fecFact'},
	    {header :'Concepto', width :150, sortable :true, dataIndex :'concepto'},
	    {header :'Cliente', width :150, sortable :true, dataIndex :'razonSocial'}
	]);
	
	//Grid para mostrar los datos seleccionados
	NS.gridFacCXC = new Ext.grid.EditorGridPanel({
		store: NS.storeFacCXC,
		id: 'gridDetaProp',
		y: 90,
		cm: NS.columFacCXC,
		sm: NS.selecFacCXC,
		width: 690,
	    height: 270,
	    stripeRows: true,
	    columnLines: true,
	    title: '',
	    listeners: {
			click: {
				fn:function(s, records) {
					var montoBco = 0;
					var montoFac = 0;
					var regSelec = NS.gridFacCXC.getSelectionModel().getSelections();
					
					montoBco = NS.unformatNumber(Ext.getCmp(PF + 'txtImpIng').getValue());
					
					for(var i=0; i<regSelec.length; i++) {
						montoFac += parseFloat(regSelec[i].get('importeA'));
					}
					Ext.getCmp(PF + 'txtFactCXC').setValue(NS.formatNumber(montoFac));
					Ext.getCmp(PF + 'txtDif').setValue(NS.formatNumber(montoBco - montoFac));
				}
			}
		}
	});
	
	NS.labImpIng = new Ext.form.Label({
		text: 'Ingreso Banco',
        x: 10,
        y: 370
	});
    
	NS.txtImpIng = new Ext.form.TextField({
		id: PF + 'txtImpIng',
		x: 10,
        y: 385,
        width: 120,
        tabIndex: 0,
        disabled: true
    });
	NS.labFactCXC = new Ext.form.Label({
		text: 'Facturas CXC',
        x: 180,
        y: 370
	});
    
	NS.txtFactCXC = new Ext.form.TextField({
		id: PF + 'txtFactCXC',
		x: 180,
        y: 385,
        width: 120,
        tabIndex: 0,
        disabled: true
    });
	NS.labDiferencia = new Ext.form.Label({
		text: 'Diferencia',
        x: 350,
        y: 370
	});
    
	NS.txtDif = new Ext.form.TextField({
		id: PF + 'txtDif',
		x: 350,
        y: 385,
        width: 120,
        tabIndex: 0,
        disabled: true
    });
	
	
	//Contenedor de detalle de las propuestas
	NS.contFacCXC = new Ext.form.FieldSet({
		title: 'Facturas',
		layout: 'absolute',
	    items: [
				{
				    xtype: 'label',
				    text: 'IETU:',
				    x: 390,
				    y: 57
				},{   
				    xtype: 'checkbox',
				    name: PF + 'chkIETU',    
				    id: 'chkIETU',
				    x: 420,
				    y: 52,
				    listeners:{
						check:{
							fn:function(checkBox,valor){
								if(valor)
									NS.ietu = true;
								else
									NS.ietu = false;
							}
						}
					}
				},{
				    xtype: 'label',
				    text: 'IVA:',
				    x: 480,
				    y: 57
				},{   
				    xtype: 'checkbox',
				    name: PF + 'chkIVA',    
				    id: 'chkIVA',
				    x: 505,
				    y: 52,
				    listeners:{
						check:{
							fn:function(checkBox,valor){
								if(valor)
									NS.iva = true;
								else
									NS.iva = false;
							}
						}
					}
				},
				NS.lblDepto,
				NS.lblProyecto,
				NS.lblCCosto,
				NS.cmbDepto,
				NS.cmbProyect,
				NS.cmbCCosto,
				NS.lblCliente, 
	            NS.cmbCliente, 
	            NS.gridFacCXC,
	            NS.labImpIng,
	            NS.txtImpIng,
	            NS.labFactCXC,
	            NS.txtFactCXC,
	            NS.labDiferencia,
	            NS.txtDif,
	           {
	        	    xtype: 'button',
	        	   	text: 'Aceptar',
	        	   	x: 490,
	        	   	y: 385,
					width: 80,
					height: 22,
					listeners: {
						click:{
							fn:function(e){
								NS.aceptar();
							}
						}
					}
	           },{
	        	    xtype: 'button',
	        	   	text: 'Cerrar',
	        	   	x: 580,
	        	   	y: 385,
					width: 80,
					height: 22,
					listeners: {
						click:{
							fn:function(e){
	        	   				NS.limpiar();
	        	   				winFacCXC.hide();
							}
						}
					}
	           }
	           ]
	});
	
	NS.buscaFacturas = function() {
		NS.storeFacCXC.removeAll();
		NS.gridFacCXC.getView().refresh();
		
		NS.storeFacCXC.baseParams.noCliente = NS.iNoCliente;
		NS.storeFacCXC.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtEmpresa').getValue());
		NS.storeFacCXC.load();
	};
	
	NS.aceptar = function() {
		var records = NS.gridFacCXC.getSelectionModel().getSelections();
		var recIng = NS.gridMovsEmpresa.getSelectionModel().getSelections();
		var matVec = new Array();
		var cliente = '';
		var facturas = '';
		var conceptos = '';
		var clienteTmp = '';
		var direrencia = '';
		var aplicaDif = '';
		
		/*if(parseInt(NS.unformatNumber(Ext.getCmp(PF + 'txtDif').getValue())) != 0) {
			Ext.Msg.alert('SET', 'La diferencia tiene que ser cero!!');
			return;
		}*/
		
		montoBco = NS.unformatNumber(Ext.getCmp(PF + 'txtImpIng').getValue());
		
		if(!confirm('¿Esta seguro de actualizar los movimientos? '))
			return;
	
		BFwrk.Util.msgWait('Actualizando Movimiento...', true);
		
		for(var i=0; i<records.length; i++) {
			var vec = {};
			vec.noFactura = records[i].get('noFactura');
			vec.importe = records[i].get('importe');
			vec.importeA = records[i].get('importeA');
			vec.noCliente = records[i].get('noBenef');
			vec.noEmpresa = Ext.getCmp(PF+'txtEmpresa').getValue();
			vec.noFolioDet = recIng[0].get('noFolioDet');
			vec.idDivisa = recIng[0].get('idDivisa');
			vec.idTipoOperacion = recIng[0].get('idTipoOperacion');
			vec.depto = Ext.getCmp(PF+'cmbDepto').getValue() + '';
			vec.proyec = Ext.getCmp(PF+'cmbProyect').getValue() + '';
			vec.cCosto = Ext.getCmp(PF+'cmbCCosto').getValue() + '';
			vec.ctaContable = Ext.getCmp(PF + 'txtCtaConta').getValue();
			matVec[i] = vec;
			
			if(montoBco < parseFloat(records[i].get('importeA'))) {
				Ext.Msg.alert('SET', 'El monto de la factura es mayor al del ingreso, ingrese el monto aplicar!!');
				return;
			}
			/*
			if(records[i].get('noBenef') != clienteTmp) {
				cliente += records[i].get('razonSocial') + ',';
				clienteTmp = records[i].get('noBenef');
			}
			*/
			facturas += records[i].get('noFactura').trim() + ',';
			
			if(i == 0) {
				cliente += records[i].get('razonSocial') + ',';
				conceptos += records[i].get('concepto').trim() + ',';
			}
		}
		NS.concepto = cliente.substring(0, cliente.length - 1) + '/' + facturas.substring(0, facturas.length - 1) + '/' + conceptos.substring(0, conceptos.length - 1);
		
		var cadenaJson = Ext.util.JSON.encode(matVec);
		
		ConciliacionBancoSetAction.clasificaIngresos(cadenaJson, NS.ietu, NS.iva, NS.concepto, jsonStringRegistros, jsonStringCriterios, function(res,e){
			Ext.Msg.alert('SET', res);
			NS.limpiaAceptar();
			NS.limpiar();
			winFacCXC.hide();
		});
	};
	
	//ventana para de las facturas CXC
	var winFacCXC = new Ext.Window({
		title: 'Facturas del Cliente',
		modal: true,
		shadow: true,
		width: 750,
	   	height: 510,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    closable: false,
	   	items: [
	   	        NS.contFacCXC
	   	        ]
	});//Termina ventana
	
	NS.actualizarMov = function() {
		var matrizRegistros = new Array ();
		var matrizCriterios = new Array ();
		
		var regSelec = NS.gridMovsEmpresa.getSelectionModel().getSelections();
	
		if(regSelec.length <= 0)
		{
			BFwrk.Util.msgShow('Debe seleccionar al menos un registro.', 'INFO');
			return 0;
		}
		
		if(Ext.getCmp(PF + 'txtGrupoBanco').getValue() === '' || Ext.getCmp(PF + 'cmbGrupo').getValue() === ''
			|| Ext.getCmp(PF + 'txtRubroBanco').getValue() === '' || Ext.getCmp(PF + 'txtRubroBanco').getValue() === '')
		{
			BFwrk.Util.msgShow('Debe seleccionar Grupo y Rubro para actualizar el movimiento en SET', 'INFO');
			return 0;
		}
		
		if(Ext.getCmp( PF + 'txtCtaConta').getValue() == '') {
			BFwrk.Util.msgShow('Debe seleccionar los parametros de la configuración contable', 'INFO');
			return 0;
		}
		
		for(var i = 0; i < regSelec.length; i ++)
		{
			var registro = {};
			registro.idChequera = regSelec[i].get('noFolioDet');
			matrizRegistros[i] = registro; 
		}
		
		
		var criterio = {};
		criterio.idGrupo = Ext.getCmp(PF+'txtGrupoBanco').getValue();
		criterio.idRubro = Ext.getCmp(PF+'txtRubroBanco').getValue();
		criterio.descRubro = $('input[id*="'+ NS.cmbRubro.getId() +'"]').val();
		criterio.nomComputadora = ''+NS.NOMBRE_HOST;
		
		criterio.idSubGrupo = NS.cmbGrupoConta.getValue();
		criterio.idSubSubGrupo = NS.cmbSubGrupo.getValue();
		criterio.idRubroC = NS.cmbSubSubGrupo.getValue();
		criterio.ctaCargo = Ext.getCmp( PF + 'txtCtaConta').getValue();
		
		criterio.cliente = Ext.getCmp(PF+'txtCliente').getValue();
		criterio.factura = Ext.getCmp(PF+'txtFactura').getValue();
		criterio.concepto = Ext.getCmp(PF+'txtConcepto').getValue();
		
		matrizCriterios[i] = criterio;
		
		jsonStringRegistros = Ext.util.JSON.encode(matrizRegistros);
		jsonStringCriterios = Ext.util.JSON.encode(matrizCriterios);
		
		/*ConciliacionBancoSetAction.updateMovimientoSETVIngresos(jsonStringRegistros, jsonStringCriterios, function(result, e){
 			
			if(result.msgUsuario!==null  &&  result.msgUsuario!=='' && result.msgUsuario!= undefined)
			{
				BFwrk.Util.msgWait('Finalizado...', false);
				BFwrk.Util.msgShow(''+result.msgUsuario,'INFO');
				NS.storeMovimientosEmpresa.load();
				Ext.getCmp(PF+'txtGrupoBanco').setValue('');
				Ext.getCmp(PF+'txtRubroBanco').setValue('');

				Ext.getCmp(PF+'cmbGrupo').setValue('');
				Ext.getCmp(PF+'cmbRubro').setValue('');
				Ext.getCmp( PF + 'txtCuentaContable').setValue('');
				
				Ext.getCmp(PF + 'txtCliente').setValue('');
				Ext.getCmp(PF + 'txtFactura').setValue('');
				Ext.getCmp(PF + 'txtConcepto').setValue('');
				
				Ext.getCmp(PF + 'txtCliente').setDisabled( false );
				Ext.getCmp(PF + 'txtFactura').setDisabled( false );
				Ext.getCmp(PF + 'txtConcepto').setDisabled( false );
				
				Ext.getCmp(PF + 'chkActive').setValue(0);
				
				NS.limpiaConta();
			}
		});
		*/
	};
	
	NS.limpiaAceptar = function() {
		BFwrk.Util.msgWait('Finalizado...', false);
		NS.storeMovimientosEmpresa.load();
		Ext.getCmp(PF+'txtGrupoBanco').setValue('');
		Ext.getCmp(PF+'txtRubroBanco').setValue('');
		
		Ext.getCmp(PF+'cmbGrupo').setValue('');
		Ext.getCmp(PF+'cmbRubro').setValue('');
		Ext.getCmp( PF + 'txtCuentaContable').setValue('');
		/*
		Ext.getCmp(PF + 'txtCliente').setValue('');
		Ext.getCmp(PF + 'txtFactura').setValue('');
		Ext.getCmp(PF + 'txtConcepto').setValue('');
		
		Ext.getCmp(PF + 'txtCliente').setDisabled( false );
		Ext.getCmp(PF + 'txtFactura').setDisabled( false );
		Ext.getCmp(PF + 'txtConcepto').setDisabled( false );
		*/
		//Ext.getCmp(PF + 'chkActive').setValue(0);
		
		NS.limpiaConta();
	};
	
	//**************************************************** Termina el código para el detalle de las facturas de CXC *****************************************//	

	//**********************************************************************************************************
	
	NS.contenedorConciliacionManual = new Ext.FormPanel({
	    title: 'Consulta de Depositos',
	    width: 803,
	    height: 700,
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
	                width: 1000,
	                items: [
	                        NS.contConta,
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 0,
	                        width: 975,
	                        height: 150,
	                        layout: 'absolute',
	                        items: [
	                            NS.lblEmpresa,
	                            NS.txtEmpresa,
	                            NS.cmbEmpresa,
	                            {
	                                xtype: 'fieldset',
	                                title: 'Seleccion',
	                                x: 1,
	                                y: 46,
	                                width: 200,
	                                height: 100,
	                                layout: 'absolute',
	                                hidden:true,
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
				                                inputValue: 0
		                                    },
		                                    {
		                                        xtype: 'radio',
		                                        boxLabel: 'Mov. de la Empresa',
		                                        name: PF+'optMov',
				                                inputValue: 1,
				                                checked: true
		                                    },
		                                    {
		                                        xtype: 'radio',
		                                        boxLabel: 'Mov. de Banco y Empresa',
		                                        name: PF+'optMov',
				                                inputValue: 2
				                                
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
	                                y: 85
	                            },
	                            NS.cmbBanco,
	                            NS.cmbChequera,
	                            NS.cmbEstatus,
	                            NS.cmbCargoAbono,
	                            NS.cmbFormaPago,
	                            NS.cmbIdentificado,
	                            {
	                                xtype: 'datefield',
	                                id: PF+'txtFechaIni',
	                                name: PF+'txtFechaIni',
	                                format: 'd/m/Y',
	                                hidden: true,
	                                x: 217,
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
	                                x: 320,
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
	                                x: 217,
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
	                                x: 320,
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
	                                xtype: 'button',
	                                id: PF+'cmdGrafica',
	                                name: PF+'cmdGrafica',
	                                text: 'Grafica',
	                                disabled:true,
	                                x: 870,
	                                y: 0,
	                                width: 80,
	                                listeners:{
				               			click:{
				           			   		fn:function(e){
				                            	var sName = NS.GI_ID_EMPRESA+""+NS.iIdBanco+NS.sIdChequera;
				            		            sName += (NS.iFormaPago>0) ? NS.iFormaPago : "";
				            					sName += (NS.sEstatus!=null && NS.sEstatus!="0") ? NS.sEstatus : "";
				            					sName += (NS.uMontoIni!=null && NS.uMontoIni!="0") ? NS.uMontoIni.substring(0, NS.uMontoIni.indexOf(".")) : "";
				            					sName += (NS.uMontoFin!=null && NS.uMontoFin!="0") ? NS.uMontoFin.trim().substring(0, NS.uMontoFin.indexOf(".")-1) : "";
				            					
				            					if(NS.sFecIni!="") {
				            						NS.sFecIni = NS.sFecIni.trim();
				            						NS.sFecFin = NS.sFecFin.trim();
				            						
				            						var sAux1 = NS.sFecIni.replace("/", "");
				            						sAux1 = sAux1.replace("/", "");
				            						var sAux2 = NS.sFecFin.replace("/", "");
				            						sAux2 = sAux2.replace("/", "");
				            						sName += sAux1 + sAux2;
				            					}
				            					
				            					Ext.getCmp('panel3').show();
				            					Ext.getCmp('panel2').show();
				            					Ext.getCmp('panel1').show();
				            					Ext.getCmp('panel1').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'ConsultaDepositos'+sName+'PC.jpg" border="0"/>');
				            					Ext.getCmp('panel2').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'ConsultaDepositos'+sName+'BG.jpg" border="0"/>');
				            					Ext.getCmp('panel3').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'ConsultaDepositos'+sName+'LG.jpg" border="0"/>');
				            					Ext.Msg.alert('SET', 'Graficas listas');
				           			   		}
				        			   	}
				    			   	}
	                            },
	                            {
	                                xtype: 'button',
	                                id: PF+'cmdBuscar',
	                                name: PF+'cmdBuscar',
	                                text: 'Buscar',
	                                x: 870,
	                                y: 30,
	                                width: 80,
	                                listeners:{
				               			click:{
				           			   		fn:function(e){
				           			   			//NS.inicializaCajas()
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
	                        height: 110,
	                        padding: 0,
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Grupo:'
	                            },{
	                                xtype: 'label',
	                                text: 'Rubro:',
	                                x: 277
	                            },{
	                                xtype: 'textfield',
	                                id: PF+'txtGrupoBanco',
	                                y: 15,
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
	                                x: 276,
	                                y: 15,
	                                width: 60,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtRubroBanco',NS.cmbRubro.getId());
			                        			
			                        			if(comboValor == null || comboValor == undefined || comboValor == '')
			            							Ext.getCmp(PF + 'txtRubroBanco').setValue('');
			            						else {
				                        			NS.accionarCmbRubro(comboValor);
				                        			NS.llenaComboGrupoConta(comboValor);
			            						}
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
	                                x: 545,
	                                y: 23,
	                                boxLabel: 'Contabiliza',
	                                hidden:true
	                               
	                            },{
	                            	xtype: 'button',
	                                text: 'Facturas',
	                                x: 740,
	                                width: 80,
//	                                hidden:true,
	                                listeners: {
										click: {
											fn:function(){
	                            				var records = NS.gridMovsEmpresa.getSelectionModel().getSelections();
	                            				
	                            				if(records == 0) {
	                            					Ext.Msg.alert('SET', 'Debe seleccionar un registro!!');
	                            					return;
	                            				}
	                            				if(NS.actualizarMov() == 0) return;

	                            				
	                            				
	                            				winFacCXC.show();
	                            				NS.iNoCliente = '';
	                            				NS.buscaFacturas();
	                            				
	                            				Ext.getCmp(PF + 'txtImpIng').setValue(NS.formatNumber(records[0].get('importe')));
	                            				
	                            				NS.storeDepto.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtEmpresa').getValue());
	                            				NS.storeDepto.load();
	                            				
	                            				NS.storeProyect.baseParams.condicion='no_empresa = \'' + Ext.getCmp(PF+'txtEmpresa').getValue() + '\'';
	                            				NS.storeProyect.load();

	                            				NS.storeCCosto.baseParams.condicion='no_empresa = \'' + Ext.getCmp(PF+'txtEmpresa').getValue() + '\'';
	                            				NS.storeCCosto.load();
	                            			}
	                            		}
	                            	}
	                            },{
	                                xtype: 'button',
	                                text: 'Actualizar Movimientos',
	                                x: 830,
	                                width: 80,
	                                listeners:{
										click:{
											fn:function(){
	                            	
												var matrizRegistros = new Array ();
	                        					var matrizCriterios = new Array ();
	                        					
	                        					var regSelec = NS.gridMovsEmpresa.getSelectionModel().getSelections();
											
												if(regSelec.length <= 0)
												{
													BFwrk.Util.msgShow('Debe seleccionar al menos un registro.', 'INFO');
													return;
												}
												
												if(Ext.getCmp(PF + 'txtGrupoBanco').getValue() === '' || Ext.getCmp(PF + 'cmbGrupo').getValue() === ''
													|| Ext.getCmp(PF + 'txtRubroBanco').getValue() === '' || Ext.getCmp(PF + 'txtRubroBanco').getValue() === '')
												{
													BFwrk.Util.msgShow('Debe seleccionar Grupo y Rubro para actualizar el movimiento en SET', 'INFO');
													return;
												}
												
												if(Ext.getCmp( PF + 'txtCtaConta').getValue() == '') {
													BFwrk.Util.msgShow('Debe seleccionar los parametros de la configuración contable', 'INFO');
													return;
												}
												if(!confirm('¿Esta seguro de actualizar los movimientos? '))
              										return;
											
												
												
												BFwrk.Util.msgWait('Actualizando Movimiento...', true);
												
												for(var i = 0; i < regSelec.length; i ++)
												{
													var registro = {};
													registro.idChequera = regSelec[i].get('noFolioDet');
													matrizRegistros[i] = registro; 
												}
												
												
												var criterio = {};
												criterio.idGrupo = Ext.getCmp(PF+'txtGrupoBanco').getValue();
												criterio.idRubro = Ext.getCmp(PF+'txtRubroBanco').getValue();
												criterio.descRubro = $('input[id*="'+ NS.cmbRubro.getId() +'"]').val();
												criterio.nomComputadora = ''+NS.NOMBRE_HOST;
												
												criterio.idSubGrupo = NS.cmbGrupoConta.getValue();
												criterio.idSubSubGrupo = NS.cmbSubGrupo.getValue();
												criterio.idRubroC = NS.cmbSubSubGrupo.getValue();
												criterio.ctaCargo = Ext.getCmp( PF + 'txtCtaConta').getValue();
												
												criterio.cliente = Ext.getCmp(PF+'txtCliente').getValue();
												criterio.factura = Ext.getCmp(PF+'txtFactura').getValue();
												criterio.concepto = Ext.getCmp(PF+'txtConcepto').getValue();
												
												matrizCriterios[i] = criterio;
												
												var jsonStringRegistros = Ext.util.JSON.encode(matrizRegistros);
												var jsonStringCriterios = Ext.util.JSON.encode(matrizCriterios);
												
												ConciliacionBancoSetAction.updateMovimientoSETVIngresos(jsonStringRegistros, jsonStringCriterios, function(result, e){
			              			   			
													if(result.msgUsuario!==null  &&  result.msgUsuario!=='' && result.msgUsuario!= undefined)
													{
														BFwrk.Util.msgWait('Finalizado...', false);
														BFwrk.Util.msgShow(''+result.msgUsuario,'INFO');
														NS.storeMovimientosEmpresa.load();
														Ext.getCmp(PF+'txtGrupoBanco').setValue('');
														Ext.getCmp(PF+'txtRubroBanco').setValue('');

														Ext.getCmp(PF+'cmbGrupo').setValue('');
														Ext.getCmp(PF+'cmbRubro').setValue('');
														Ext.getCmp( PF + 'txtCuentaContable').setValue('');
														
														Ext.getCmp(PF + 'txtCliente').setValue('');
		                            					Ext.getCmp(PF + 'txtFactura').setValue('');
		                            					Ext.getCmp(PF + 'txtConcepto').setValue('');
														
														Ext.getCmp(PF + 'txtCliente').setDisabled( false );
		                            					Ext.getCmp(PF + 'txtFactura').setDisabled( false );
		                            					Ext.getCmp(PF + 'txtConcepto').setDisabled( false );
		                            					
		                            					Ext.getCmp(PF + 'chkActive').setValue(0);
		                            					
		                            					NS.limpiaConta();
													}
												});
											}
										}
									}
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Cuenta Contable:',
	                                y: 50
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCuentaContable',
	                                name: PF+'txtCuentaContable',
	                                readOnly: true,	                                
	                                y: 65,
	                                width: 100,
	                                disabled:true
	                            },
	                            //****************************************************************************	                            
	                            //                         CAMPOS PARA CONTABILIZAR
	                            //****************************************************************************
	                            {
	                                xtype: 'label',
	                                text: 'Activa:',
	                                x: 130,
	                                y: 70
	                            },
	                            {   
	                                xtype: 'checkbox',
	                                name: PF + 'chkActive',    
	                                id: 'chkActive',
	                                x: 170,
		                            y: 65,
		                            listeners:{
	                            		check:{
	                            			fn:function(){
	                            				var me  = this;
	                            				var res = me.getValue();
	                            				
	                            				
	                            				if( res == true)
	                            				{
	                            					
	                            					Ext.getCmp(PF + 'lblCliente').setDisabled( false );	                            					
	                            					Ext.getCmp(PF + 'txtCliente').setDisabled( false );
	                            					Ext.getCmp(PF + 'lblFactura').setDisabled( false );
	                            					Ext.getCmp(PF + 'txtFactura').setDisabled( false );
	                            					Ext.getCmp(PF + 'lblConcepto').setDisabled( false );
	                            					Ext.getCmp(PF + 'txtConcepto').setDisabled( false );
	                            					
	                            					
	                            					
	                            				}else{
	                            					
	                            					Ext.getCmp(PF + 'lblCliente').setDisabled( true );
	                            					Ext.getCmp(PF + 'txtCliente').setDisabled( true );
	                            					Ext.getCmp(PF + 'lblFactura').setDisabled( true );
	                            					Ext.getCmp(PF + 'txtFactura').setDisabled( true );
	                            					Ext.getCmp(PF + 'lblConcepto').setDisabled( true );
	                            					Ext.getCmp(PF + 'txtConcepto').setDisabled( true );
	                            					
	                            				}
	                            					
	                            			}
	                            		}//End check
	                            	}//listeners
		                            	
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Cliente:',
	                                id: PF + 'lblCliente',
	                                name: PF + 'lblCliente',
	                                x: 210,
	                                y: 50,
	                                disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCliente',
	                                name: PF+'txtCliente',	                                	                                
	                                x: 210,
	                                y: 65,
	                                width: 190,
	                                disabled:true
	                                
	                            },	                            
	                            {
	                                xtype: 'label',
	                                text: 'Factura:',
	                                id: PF+'lblFactura',
	                                name: PF+'lblFactura',
	                                x: 420,
	                                y: 50,
	                                disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtFactura',
	                                name: PF+'txtFactura',
	                                x: 420,
	                                y: 65,
	                                width: 100,
	                                disabled:true
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Concepto:',
	                                name: PF+ 'lblConcepto',
	                                id: PF+ 'lblConcepto',
	                                x: 540,
	                                y: 50,
	                                disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtConcepto',
	                                name: PF+'txtConcepto',	                                                         
	                                x: 540,
	                                y: 65,
	                                width: 190,
	                                disabled:true
	                            },
	                            
	                            //****************************************************************************	                            
	                            //                         CAMPOS PARA CONTABILIZAR
	                            //****************************************************************************
	                            
	                            {
	                                xtype: 'label',
	                                text: 'Abonos:',
	                                x: 0,
	                                y: 300
	                                ,disabled:true
	                            },
	                            
	                            {
	                                xtype: 'label',
	                                text: 'Cargos:',
	                                x: 418,
	                                y: 300
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Abonos:',
	                                x: 615,
	                                y: 300
	                                ,disabled:true
	                            },
	                            
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCargoBanco',
	                                name: PF+'txtCargoBanco',
	                                readOnly: true,
	                                value: BFwrk.Util.formatNumber(0.0),
	                                x: 60,
	                                y: 300,
	                                width: 110
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantAbonoBanco',
	                                name: PF+'txtCantAbonoBanco',
	                                readOnly: true,
	                                value: 0,
	                                x: 180,
	                                y: 300,
	                                width: 51
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtAbonoBanco',
	                                name: PF+'txtAbonoBanco',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 240,
	                                y: 300,
	                                width: 110
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantCargoEmpresa',
	                                name: PF+'txtCantCargoEmpresa',
	                                readOnly: true,
	                                value: 0,
	                                x: 377,
	                                y: 300,
	                                width: 50
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCargoEmpresa',
	                                name: PF+'txtCargoEmpresa',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 437,
	                                y: 300,
	                                width: 110
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtCantAbonoEmpresa',
	                                name: PF+'txtCantAbonoEmpresa',
	                                readOnly: true,
	                                value: 0,
	                                x: 576,
	                                y: 300,
	                                width: 50
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtAbonoEmpresa',
	                                name: PF+'txtAbonoEmpresa',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 635,
	                                y: 300,
	                                width: 110
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Importe Total:',
	                                x: 156,
	                                y: 117
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Importe Total:',
	                                x: 548,
	                                y: 116
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalBanco',
	                                name: PF+'txtTotalBanco',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 240,
	                                y: 112,
	                                width: 110
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalEmpresa',
	                                name: PF+'txtTotalEmpresa',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 635,
	                                y: 112,
	                                width: 110
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Aclaracion:',
	                                x: 2,
	                                y: 150
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Diferencia:',
	                                x: 378,
	                                y: 151
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtAclaracion',
	                                name: PF+'txtAclaracion',
	                                x: 66,
	                                y: 148,
	                                width: 284
	                                ,disabled:true
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtDiferencia',
	                                name: PF+'txtDiferencia',
	                                value: BFwrk.Util.formatNumber(0.0),
	                                readOnly: true,
	                                x: 436,
	                                y: 148,
	                                width: 110,
	                                disabled:true
	                            }
	                            
	                        ]
	                    }
	                ]
	            }
	        ]
	});
	NS.contenedorConciliacionManual.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});