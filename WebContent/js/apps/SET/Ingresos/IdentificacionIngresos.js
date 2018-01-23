 /*
@author: Delia Hernández
@since: 11/07/2016
*/
Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Ingresos.IdentificacionIngresos');
	//NS.tabContId = 'contenedorConsultaPropuestas';
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID + '.';
	
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.NOMBRE_HOST = apps.SET.HOST_NAME_LOCAL;
	NS.idUsuario = apps.SET.iUserId;
	//Colorear grid//
	
	NS.recordGridCob = 0;
	NS.recordGridBan = 0;
	
	NS.matCob = new Array();
	NS.mat2Cob = new Array();
	NS.mat3Cob = new Array();
	NS.mat3CoObse = new Array();
	
	NS.matBan = new Array();
	NS.mat2Ban = new Array();
	NS.mat3Ban = new Array();
	NS.mat4Ban = new Array();
	winExcel = '';
	NS.fielExcel = ""
	
	NS.imporCob1 = new Array();
	NS.imporBan1 = new Array();
		
	NS.referenCob1 = new Array();
	NS.referenBan1 = new Array();
		
	NS.referen2Cob1 = new Array();
	NS.observaBan1 = new Array();
	
	NS.Color = 'C';
	
	///// ACTUALIZAR GRID
	NS.tiempoCon = 0
	NS.id = false;
	////////////////
	
	
	NS.iIdBanco = 0;
	NS.sIdChequera = '';
	NS.sFecIni = '';
	NS.sFecFin = '';
	NS.uMontoIni = 0;
	NS.uMontoFin = 0;
	NS.uDiferencia = 0;
	NS.banderaCuentaConta = 0;
	NS.optBusqueda = 2;
	NS.Llenado = '';	
	NS.bandera = 0;
	NS.DiferenciaGlobal = 0;
	
	var jsonStringRegistros = '';
	var jsonStringCriterios = '';
	var textField = new Ext.form.TextField();
	
	//Formato de un numero a monetario
	NS.formatNumber = function(num, prefix){
		num = num.toString();
		if (num.indexOf('.') > -1) {
			if (num.substring(num.indexOf('.')).length < 3) {
				num = num + '0';
			}
		} else {
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
	
	//Quitar formato a las cantidades
	NS.unformatNumber = function(num) {
		return num.replace(/(,)/g, ''); 
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
		directFn: IdentificacionIngresosAction.llenarComboBancos,
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
				    NS.storeCmbChequera.baseParams.opcion = NS.optBusqueda;
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
		directFn: IdentificacionIngresosAction.llenarCmbChequeras,
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
	
	 NS.storeDivisa = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			directFn: IdentificacionIngresosAction.llenarComboDivisa, 
			idProperty: 'idStr', 
			fields: [
					 {name: 'idStr'},
					 {name: 'descripcion'}
				 ],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
					
					if(records.length == null || records.length <= 0)
						Ext.Msg.alert('SET','No existen divisas en el catálogo');
					
					NS.cmbDivisa.setValue('MN');
				},
				exception: function(mist){
					Ext.Msg.alert('SET','Error en conexión al servidor');
					mascara.hide();
				}
			}
	    });
	    
		NS.cmbDivisa = new Ext.form.ComboBox({
			store: NS.storeDivisa
			,name: PF + 'cmbDivisa'
			,id: PF + 'cmbDivisa'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
	      	,x: 780
	        ,y: 30
	        ,width: 150
			,valueField:'idStr'
			,displayField:'descripcion'
			,autocomplete: true
			,emptyText: 'Seleccione una Divisa'
			,triggerAction: 'all'
			,value: ''
			,editable: false
		});
		NS.storeDivisa.load();
		//NS.cmbDivisa.setValue= "PESOS";
		
	NS.dataCriterio = [
					  [ '0', 'BANCO' ]
				     ,[ '1', 'CHEQUERA' ]
				     ,[ '2', 'FECHA' ]
				     ,[ '4', 'MONTOS' ]
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
				    
				    if(combo.getValue() == 0){
						//BANCO
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'cmbBanco').reset();
						NS.storeCmbBanco.load();
						Ext.getCmp(PF + 'cmbBanco').setDisabled(false);
						Ext.getCmp(PF + 'cmbBanco').setVisible(true);
						
						NS.agregarCriterioValor ('BANCO', Ext.getCmp(PF + 'cmbBanco').getId(), 'criterio');
					
				    }else if(combo.getValue() == 1){
						//CHEQUERA
						Ext.getCmp(PF + 'cmbChequera').reset();
						NS.storeCmbChequera.load();
						var indice=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(var i = 0; i < recordsDatGrid.length; i++){
							if(recordsDatGrid[i].get('criterio')=='BANCO')
								indice = i;
						}
						if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='BANCO'){
							NS.ocultarComponentes();
							Ext.getCmp(PF + 'cmbChequera').setDisabled(false);
							Ext.getCmp(PF + 'cmbChequera').setVisible(true);
							
							NS.agregarCriterioValor ('CHEQUERA', Ext.getCmp(PF + 'cmbChequera').getId(), 'criterio');
						
						}else{
							BFwrk.Util.msgShow('Primero debe escoger un Banco','ERROR');
							combo.setDisabled(false);
							return;
						}
						
					}else if(combo.getValue() == 2){
						//FECHA
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'txtFechaIni').reset();
						Ext.getCmp(PF + 'txtFechaFin').reset();
						Ext.getCmp(PF + 'txtFechaIni').setDisabled(false);
						Ext.getCmp(PF + 'txtFechaIni').setVisible(true);
						Ext.getCmp(PF + 'txtFechaFin').setVisible(true);
						
						NS.agregarCriterioValor ('FECHA', Ext.getCmp(PF + 'txtFechaIni').getId(), 'criterio');
					
					}else if(combo.getValue() == 3){
						//DIVISA
						NS.ocultarComponentes();
						Ext.getCmp(PF + 'cmbDivisa').reset();
						NS.storeDivisa.load();
						Ext.getCmp(PF + 'cmbDivisa').setDisabled(false);
						Ext.getCmp(PF + 'cmbDivisa').setVisible(true);
						Ext.getCmp(PF + 'txtIdDivisa').setVisible(true);
						Ext.getCmp(PF + 'txtIdDivisa').setVisible(true);
						NS.agregarCriterioValor ('DIVISA', Ext.getCmp(PF + 'cmbDivisa').getId(), 'criterio');
						
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
					
					for(var i=0;i<records.length;i = i + 1){
						NS.gridDatos.store.remove(records[i]);
						NS.gridDatos.getView().refresh();
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
					}
				}
			}
		}
	
	
	});
	////////////////////////////////////////////COMBOS/////////////////////////////////////////////////////
	
	
//////    COMBO CUANDO BANCO ES MAYOR///
	 NS.storeLlenaComboCuentaContable = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			directFn: IdentificacionIngresosAction.llenaComboCuentaContable, 
			idProperty: 'idCtaContable', 
			fields: [
					 {name: 'idCtaContable'},
					 {name: 'descCtaContable'}
				 ],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboCuentaContable, msg:"Cargando..."});
					
					if(records.length == null || records.length <= 0)
						Ext.Msg.alert('SET','No existen Cuentas en el catálogo');
				},
				exception: function(mist){
					Ext.Msg.alert('SET','Error en conexión al servidor');
					
				}
			}
	    });
	 
	
	
	NS.cmbCuentaContable = new Ext.form.ComboBox({
		store: NS.storeLlenaComboCuentaContable
		,name: PF + 'cmbCuentaContable'
		,id: PF + 'cmbCuentaContable'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 40
        ,y: 75
        ,width: 300
		,valueField:'idCtaContable'
		,displayField:'descCtaContable'
		,autocomplete: true
		,emptyText: 'Seleccione Cuenta Contable'
		,triggerAction: 'all'
		,value: ''
		,editable: false
	});
	
	
//////COMBO CUANDO COBRANZA ES MAYOR///	
	
	 NS.storeLlenaComboCuentaContable2 = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			directFn: IdentificacionIngresosAction.llenaComboCuentaContable2, 
			idProperty: 'idCtaContable', 
			fields: [
					 {name: 'idCtaContable'},
					 {name: 'descCtaContable'}
				 ],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboCuentaContable2, msg:"Cargando..."});
					
					if(records.length == null || records.length <= 0)
						Ext.Msg.alert('SET','No existen Cuentas en el catálogo');
				},
				exception: function(mist){
					Ext.Msg.alert('SET','Error en conexión al servidor');
					//mascara.hide();
				}
			}
	    });
	
	
	
	NS.cmbCuentaContable2 = new Ext.form.ComboBox({
		store: NS.storeLlenaComboCuentaContable2
		,name: PF + 'cmbCuentaContable2'
		,id: PF + 'cmbCuentaContable2'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
   	,x: 40
     ,y: 75
     ,width: 300
		,valueField:'idCtaContable'
		,displayField:'descCtaContable'
		,autocomplete: true
		,emptyText: 'Seleccione Cuenta Contable'
		,triggerAction: 'all'
		,value: ''
		,editable: false
	});
	////////////////////////// COMBO PARA CV //////////////
	
	NS.cmbCuentaContable3 = new Ext.form.ComboBox({
		store:  NS.storeLlenaComboCuentaContable
		,name: PF + 'cmbCuentaContable3'
		,id: PF + 'cmbCuentaContable3'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 40
		,y: 75
		,width: 300
		,valueField:'idCtaContable'
		,displayField:'descCtaContable'
		,autocomplete: true
		,emptyText: 'Seleccione Cuenta Contable'
		,triggerAction: 'all'
		,value: ''
		,editable: false
	});
	
	NS.cmbCuentaContable4 = new Ext.form.ComboBox({
		store:  NS.storeLlenaComboCuentaContable2
		,name: PF + 'cmbCuentaContable4'
		,id: PF + 'cmbCuentaContable4'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 40
		,y: 75
		,width: 300
		,valueField:'idCtaContable'
		,displayField:'descCtaContable'
		,autocomplete: true
		,emptyText: 'Seleccione Cuenta Contable'
		,triggerAction: 'all'
		,value: ''
		,editable: false
	});
	

	////COMBO ORIGEN///
	
	 NS.storeLlenaComboOrigen = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			directFn: IdentificacionIngresosAction.llenaComboOrigen, 
			idProperty: 'soiemp', 
			fields: [
			         {name: 'soiemp'},
					 {name: 'setemp'},
					 {name: 'siscod'},
					 
				 ],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboOrigen, msg:"Cargando..."});
					
					if(records.length == null || records.length <= 0)
						Ext.Msg.alert('SET','No existe Origen en el catálogo');
				},
				exception: function(mist){
					Ext.Msg.alert('SET','Error en conexión al servidor');
					//mascara.hide();
				}
			}
	    });
	 NS.storeLlenaComboOrigen.load();
	

	NS.cmbOrigen = new Ext.form.ComboBox({
		store: NS.storeLlenaComboOrigen
		,name: PF + 'cmbOrigen'
		,id: PF + 'cmbOrigen'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 40
		,y: 115
		,width: 300
		,valueField:'soiemp'
		,displayField:'soiemp'
		,autocomplete: true
		,emptyText: 'Seleccione un Origen'
		,triggerAction: 'all'
		,value: ''
		,editable: false
	});
	
	NS.cmbOrigen2 = new Ext.form.ComboBox({
		store: NS.storeLlenaComboOrigen
		,name: PF + 'cmbOrigen2'
		,id: PF + 'cmbOrigen2'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 40
		,y: 115
		,width: 300
		,valueField:'soiemp'
		,displayField:'soiemp'
		,autocomplete: true
		,emptyText: 'Seleccione un Origen'
		,triggerAction: 'all'
		,value: ''
		,editable: false
	});

////////////////////// para nuevo folio cuando cobranza es mayor /////////////////
/*	NS.storeNuevoFolioC = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: IdentificacionIngresosAction.nuevoFolioCob, 
		idProperty: 'nvoFolioCob', 
		fields: [
		         {name: 'nvoFolioCob'}
				 
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeNuevoFolioC, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existe Origen en el catálogo');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				//mascara.hide();
			}
		}
    });
	NS.storeNuevoFolioC.load();


	NS.cmbNuevoFolioC = new Ext.form.ComboBox({
	store: NS.storeNuevoFolioC
	,name: PF + 'cmbNuevoFolioC'
	,id: PF + 'cmbNuevoFolioC'
	,typeAhead: true
	,mode: 'local'
	,minChars: 0
	,selecOnFocus: true
	,forceSelection: true
	, x: 250
	,y:5
	,width: 60
	,valueField:'nvoFolioCob'
	,displayField:'nvoFolioCob'
	,autocomplete: true
	,emptyText: 'Folio'
	,triggerAction: 'all'
	,value: ''
	,editable: false
});
	
	
//////////////////////para nuevo folio cuando movimiento es mayor /////////////////
	NS.storeNuevoFolioM = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: IdentificacionIngresosAction.nuevoFolioMov, 
		idProperty: 'nvoFolioMov', 
		fields: [
		         {name: 'nvoFolioMov'}
				 
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeNuevoFolioM, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existe Nuevo folio en el catálogo');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				//mascara.hide();
			}
		}
    });
	NS.storeNuevoFolioM.load();


	NS.cmbNuevoFolioM = new Ext.form.ComboBox({
	store: NS.storeNuevoFolioM
	,name: PF + 'cmbNuevoFolioM'
	,id: PF + 'cmbNuevoFolioM'
	,typeAhead: true
	,mode: 'local'
	,minChars: 0
	,selecOnFocus: true
	,forceSelection: true
	, x: 250
	,y:5
	,width: 60
	,valueField:'nvoFolioMov'
	,displayField:'nvoFolioMov'
	,autocomplete: true
	,emptyText: 'Folio'
	,triggerAction: 'all'
	,value: ''
	,editable: false
});	*////////////////////////////////////////////////////////
///////////////////// FUNCION PARA PINTAR GRID   ////////////
/*	NS.Mayor = function() {	  
	
		if(NS.recordGridCob>=NS.recordGridBan)
		     				{
		    	 			for(var k=0; k<NS.recordGridCob;k++)
		    	 						{
		    	 						for(var o = 0; o <NS.recordGridBan; o++)
		    	 							{
		    	 							
		    	 							if( NS.matCob[k]==NS.matBan[o])
		    	 								{
		    	 								NS.imporCob1[k]=NS.matCob[k];
		    	 								NS.imporBan1[o]=NS.matBan[o];  
		    	 								}
		    	 							if( NS.mat3Cob[k]==NS.mat3Ban[o])
	    	 									{
		    	 								NS.referenCob1[k]=NS.mat3Cob[k];
		    	 								NS.referenBan1[o]=NS.mat3Ban[o];
		    	 								}
		    	 							if( NS.mat3CoObse[k]==NS.mat4Ban[o])
    	 										{
		    	 								NS.referen2Cob1[k]=NS.mat3CoObse[k];
		    	 								NS.observaBan1[o]=NS.mat4Ban[o];
    	 										}
		    	 							
		    	 					
		    	 				
			    	 			 }
		     				 }
		    	 			
		    	 			}
		if(NS.recordGridBan > NS.recordGridCob)
			{
			for(var o = 0; o <NS.recordGridBan; o++)
						{
							for(var k=0; k<NS.recordGridCob;k++)
							{
							
							if( NS.matCob[k]==NS.matBan[o])
								{
								NS.imporCob1[k]=NS.matCob[k];
								NS.imporBan1[o]=NS.matBan[o];  
								}
							if( NS.mat3Cob[k]==NS.mat3Ban[o])
								{
								NS.referenCob1[k]=NS.mat3Cob[k];
								NS.referenBan1[o]=NS.mat3Ban[o];     
								}
							if( NS.mat3CoObse[k]==NS.mat4Ban[o])
								{
								NS.referen2Cob1[k]=NS.mat3CoObse[k];
								NS.observaBan1[o]=NS.mat4Ban[o];
								}
					
				
 			 }
			 }
			
			}
		     
		 
	}
*/
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	
	NS.smBan = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false,
		listeners: {
 			rowselect: function(chkSelectionModel, rowIndex, keepExisting, record ){
 				var records = NS.gridMovsCobranza.getSelectionModel().getSelections();
				
				Ext.getCmp(PF + 'txtRegistros').setValue(records.length);
				
				var monto = 0;
				
				for(var i=0; i<records.length; i++){
					monto += records[i].get('importe');
					NS.colorCobranza = records[i].get('color');
				}
				
				Ext.getCmp(PF + 'txtMonto').setValue(NS.formatNumber(monto));
				
				//Direrencia
				var monto2 = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtMonto2').getValue()));
				
				if(isNaN(monto2))
					monto2 = 0;
				
				if(monto>monto2)
				    Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto-monto2)*100)/100));
				else
					Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto2-monto)*100)/100));			
 				
 			},
		rowdeselect: function(chkSelectionModel, rowIndex, keepExisting, record ){
				var records = NS.gridMovsCobranza.getSelectionModel().getSelections();
			
			Ext.getCmp(PF + 'txtRegistros').setValue(records.length);
			
			var monto = 0;
			
			for(var i=0; i<records.length; i++){
				monto += records[i].get('importe');
				NS.colorCobranza = records[i].get('color');
			}
			
			Ext.getCmp(PF + 'txtMonto').setValue(NS.formatNumber(monto));
			
			//Direrencia
			var monto2 = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtMonto2').getValue()));
			
			if(isNaN(monto2))
				monto2 = 0;
			
			if(monto>monto2)
			    Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto-monto2)*100)/100));
			else
				Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto2-monto)*100)/100));			
				
			}
		}
	});
	
	NS.storeMovimientosCobranza = new Ext.data.DirectStore({		
		paramsAsHash: false,
		baseParams:{			
			iIdBanco: 0,
			sIdChequera: '',
			sFecIni: '',
			sFecFin: '',
			uMontoIni: '',
			uMontoFin: '',
			iNoEmpresa: 0,
			idDivisa: '',
			concepto: '',
			
		},
		root: '',
		paramOrder:['iIdBanco','sIdChequera','sFecIni','sFecFin','uMontoIni','uMontoFin','iNoEmpresa','idDivisa'],
		directFn: IdentificacionIngresosAction.llenarMovsEmpresa, 		
		fields: [		    
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'noDocto2'},
			{name: 'noFactura2'},
			{name: 'fecFactura'},
			{name: 'fecValor'},
			{name: 'noClienteS'},
			{name: 'importeSol'},
			{name: 'idDivisa'},
			{name: 'concepto'},
			{name: 'referencia'},
			{name: 'estatus'},
			{name: 'folioDetMov'},
			{name: 'importe', type: 'float'},
			{name: 'folioCob'},
			{name: 'secuencia'},
			{name: 'razonSoc'},
		    {name: 'color',sortType: 'asText'}	     
		],	
		listeners: {
			
			load: function(s, records2){
				if(records2.length == 0)
					BFwrk.Util.msgShow('No se encontraron movimientos de cobranza para conciliar','INFO');
				/*if(NS.id==true )
				setTimeout("actualizarGrid()",NS.tiempoCon);
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosCobranza, msg:"Buscando..."});*/
				
			}
		},		
	});
	
	
	///empieza grid de banco
	NS.gridMovsCobranza  = new Ext.grid.GridPanel({
		store: NS.storeMovimientosCobranza,
		id: PF+'gridMovsCobranza',
		name: PF+'gridMovsCobranza',
		width: 460,
       	height: 250,
		x :480,
		y :0,
		frame: true,
			viewConfig:
			{ 
		        stripeRows: false, 
		        getRowClass: function(record, index,store,meta) 
		        {
		        	for(var k=0; k<NS.recordGridCob; k++)
	    			{
					
					if( record.get('color') == 'A'  )
						{
						return  'row-font-color-blue';
						}
					if( record.get('color') == 'B'  )
						{
						
						return  'row-font-color-green';
						
						}
	    		}
		        }
		 	},
		  
		title :'Movimientos de Cobranza',
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
		columns : 
			[ NS.smBan,
			 	{
					header :'Cliente',
					width :120,
					dataIndex :'razonSoc'
				},{
					header :'No. Factura',
					css: 'text-align:center;',
					width :80,
					dataIndex :'noFactura2'
				},{	
					header :'Importe', 
					css: 'text-align:right;',
					width :80,
					dataIndex :'importe', 
				 	renderer: BFwrk.Util.rendererMoney
				 },{
					 header :'Divisa',
					 css: 'text-align:center;',
					 width: 50,
					 dataIndex :'idDivisa'
				},{
					header :'Fecha Factura',
					css: 'text-align:center;',
					width :120,
					dataIndex :'fecFactura'
				},{
					header :'No Docto',
					css: 'text-align:center;',
					width :65,
					dataIndex :'noDocto2'
				},{
					header :'Fecha Valor',
					css: 'text-align:center;',
					width :120,
					dataIndex :'fecValor'
				},{
					 header :'Referencia',
					 css: 'text-align:right;',
					 width :160,
					 dataIndex :'referencia'
				},{
					header :'Concepto',
					css: 'text-align:center;',
					width :80,
					dataIndex :'concepto'
				},{
					header :'No. cliente',
					css: 'text-align:center;',
					width :80,
					dataIndex :'noClienteS'
				},{
					header :'Folio Cobranza',
					css: 'text-align:right;',
			 		width :95,
			 		dataIndex :'folioCob', 
			 	},{
					header :'Nombre Empresa',
					width :120,
					dataIndex :'nomEmpresa'
				},{
					header :'No Empresa',
					width :120,
					dataIndex :'noEmpresa'
				},{
					 header :'Secuencia',
					 width: 80,
					 dataIndex :'secuencia'
				},{
					 header :'Estatus',
					 width: 80,
					 dataIndex :'estatus'
				},{
					 header :'FolioDetMov',
					 width: 80,
					 dataIndex :'folioDetMov'
				},{
					 header :'Importe Solic',
					 width: 80,
					 hidden: true,
					 dataIndex :'importeSol'
				},{
					 header :'Color',
					 width: 80,
					 hidden: true,
					 dataIndex :'color'
				}
				
			]
		}),
		sm: NS.smBan,
		listeners:{
			rowclick:{
				fn:function(grid,record,e){
					
					var records = NS.gridMovsCobranza.getSelectionModel().getSelections();
					
					Ext.getCmp(PF + 'txtRegistros').setValue(records.length);
					
					var monto = 0;
					
					for(var i=0; i<records.length; i++){
						monto += records[i].get('importe');
						NS.colorCobranza = records[i].get('color');
					}
					
					Ext.getCmp(PF + 'txtMonto').setValue(NS.formatNumber(monto));
					
					//Direrencia
					var monto2 = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtMonto2').getValue()));
					
					if(isNaN(monto2))
						monto2 = 0;
					
					if(monto>monto2)
					    Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto-monto2)*100)/100));
					else
						Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto2-monto)*100)/100));					
				}
			}
				
		}
	});
	
	
	NS.smCobranza = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true,
		listeners: {
 			rowselect: function(chkSelectionModel, rowIndex, keepExisting, record ){
 				var records = NS.gridMovsBanco.getSelectionModel().getSelections();
				
				Ext.getCmp(PF + 'txtRegistros2').setValue(records.length);
				
				var monto2 = 0;
				
				for(var i=0; i<records.length; i++){
					monto2 += records[i].get('importe');
					NS.colorBanco = records[i].get('color');
				}
				
				Ext.getCmp(PF + 'txtMonto2').setValue(NS.formatNumber(monto2));
				
				var monto = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtMonto').getValue()));
				
				if(isNaN(monto))
					monto = 0;
				
				if(monto>monto2)
				    Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto-monto2)*100)/100));
				else
					Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto2-monto)*100)/100));	
				;			
 				
 			},
		rowdeselect: function(chkSelectionModel, rowIndex, keepExisting, record ){
			var records = NS.gridMovsBanco.getSelectionModel().getSelections();
			
			Ext.getCmp(PF + 'txtRegistros2').setValue(records.length);
			
			var monto2 = 0;
			
			for(var i=0; i<records.length; i++){
				monto2 += records[i].get('importe');
				NS.colorBanco = records[i].get('color');
			}
			
			Ext.getCmp(PF + 'txtMonto2').setValue(NS.formatNumber(monto2));
			
			var monto = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtMonto').getValue()));
			
			if(isNaN(monto))
				monto = 0;
			
			if(monto>monto2)
			    Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto-monto2)*100)/100));
			else
				Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto2-monto)*100)/100));	
			}
		}
	});
	
	NS.storeMovimientosBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdBanco: 0,
			noCliente: '',
			sIdChequera: '',
			sFecIni: '',
			sFecFin: '',
			uMontoIni: '',
			uMontoFin: '',
			iNoEmpresa: NS.GI_ID_EMPRESA,
			idDivisa : ''
				
		},
		root: '',
		paramOrder:['iIdBanco','sIdChequera','sFecIni','sFecFin','uMontoIni','uMontoFin','iNoEmpresa', 'idDivisa'],
		directFn: IdentificacionIngresosAction.llenarMovsBancos,
		
		fields: [
		         
				{name: 'nomEmpresa'},					
				{name: 'noEmpresa'},
				{name: 'idBanco'},
				{name: 'descBanco'},
				{name: 'noCuenta'},
				{name: 'importe', sortType: 'asFloat'},
				{name: 'fecOperacion'},
				{name: 'fecValor'},
				{name: 'idDivisa'},
				{name: 'referencia'},
				{name: 'concepto'},
				{name: 'observacion'},
				{name: 'descripcion'},
				{name: 'idTipoOperacion'}, 
				{name: 'noFolioDet'},
				{name: 'noFolioMov'}, 
				{name: 'idChequera'},
				{name: 'noDocto'}, 
				{name: 'idFormaPago'}, 
				{name: 'idCaja'},
				{name: 'idEstatusMov'},
				{name: 'noCliente'},
				{name: 'idBancoBenef'},
				{name: 'idRubro'}, 
				{name: 'division'},
				{name: 'color',sortType: 'asText'},		
				],	
		listeners: {
			load: function(s, records ){
				if(records.length == 0){
					BFwrk.Util.msgShow('No se encontraron movimientos en banco.','INFO');
					Ext.getCmp(PF + 'btnAnticipo').setDisabled(true);
				} else {
					Ext.getCmp(PF + 'btnAnticipo').setDisabled(false);
				}
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosBanco, msg:"Buscando..."});
			}
		},
		
	}); 
	
	NS.gridMovsBanco  = new Ext.grid.GridPanel({
		store : NS.storeMovimientosBanco,
		id: PF+'gridMovsBanco',
		name: PF+'gridMovsBanco',
		width: 460,
       	height: 250,
		x :0,
		y :0,
		frame: true,
		viewConfig: { 
	        stripeRows: false, 
	        getRowClass: function(record, index) {
        		for (var k = 0; k < NS.recordGridBan; k++) {
					if (record.get('color') == 'A') {
						return  'row-font-color-blue';
					}
					if (record.get('color') == 'B') {
						return  'row-font-color-green';
					}
	    		}
        
	        }
	    },
		title :'Movimientos de Banco',
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
		columns : 
			[ 
			 	NS.smCobranza,
		       {
					header :'Importe',
					width :70,
					dataIndex :'importe', 
					css: 'text-align:right;', 
					renderer: BFwrk.Util.rendererMoney
				},{
			 		header :'Divisa',
			 		css: 'text-align:center;',
			 		width: 50,
			 		dataIndex :'idDivisa'
		        },{
					header :'Fecha Valor',
					css: 'text-align:center;',
					width :80,
					dataIndex :'fecValor'
				},{
					header :'Referencia',
					css: 'text-align:center;',
					width :120,
					dataIndex :'referencia'
				},{
					header :'Observación',
					css: 'text-align:center;',
					width :150,
					dataIndex :'observacion'
				},{
					header :'Id Banco',
					css: 'text-align:center;',
					width :65,
					dataIndex :'idBanco'
				},{
					header :'Id Chequera',
					css: 'text-align:center;',
					width :150,
					dataIndex :'idChequera'
				},{
					header :'Concepto',
					css: 'text-align:center;',
					width :150,
					dataIndex :'concepto'
				},{
					header :'No Folio Det',
					css: 'text-align:center;',
					width :80,
					dataIndex :'noFolioDet'
				},{
					header :'Tipo Operacion',
					css: 'text-align:center;',
					width :100,
					dataIndex :'idTipoOperacion'
				},{
					header :'No. Cuenta',
					css: 'text-align:center;',
					width :80,
					dataIndex :'noCuenta'
				},{
					header :'Documento',
					css: 'text-align:right;',
					width :110,
					dataIndex :'noDocto'
				},{
					header :'No cliente',
					width :80,
					hidden: true,
					dataIndex :'noCliente'
				},{
					header :'No Empresa',
					width :120,
					hidden: true,
					dataIndex :'noEmpresa'
				},{
					header :'Nombre Empresa',
					width :120,
					hidden: true,
					dataIndex :'nomEmpresa'
				},{
					 header :'Desc. Banco',
					 width: 80,
					 hidden: true,
					 dataIndex :'descBanco'
				},{
					 header :'Fec. Operacion',
					 width: 80,
					 hidden: true,
					 dataIndex :'fecOperacion'
				},{
					 header :'Descripcion',
					 width: 80,
					 hidden: true,
					 dataIndex :'descripcion'
				},{
					 header :'No. FolioMov',
					 width: 80,
					 hidden: true,
					 dataIndex :'noFolioMov'
				},{
					 header :'Forma Pago',
					 width: 80,
					 hidden: true,
					 dataIndex :'idFormaPago'
				},{
					 header :'Caja',
					 width: 80,
					 hidden: true,
					 dataIndex :'idCaja'
				},{
					 header :'Estatus Mov',
					 width: 80,
					 hidden: true,
					 dataIndex :'idEstatusMov'
				},{
					 header :'idBancoBenef',
					 width: 80,
					 hidden: true,
					 dataIndex :'idBancoBenef'
				},{
					 header :'Rubro',
					 width: 80,
					 hidden: true,
					 dataIndex :'idRubro'
				},{
					 header :'Division',
					 width: 80,
					 hidden: true,
					 dataIndex :'division'
				},{
					 header :'Color',
					 width: 80,
					 hidden: true,
					 dataIndex :'color'
				}
			],
			
		}),
		
		sm: NS.smCobranza,
		listeners:{
			rowclick:{
				fn:function(grid, record, e){
					
					var records = NS.gridMovsBanco.getSelectionModel().getSelections();
					
					Ext.getCmp(PF + 'txtRegistros2').setValue(records.length);
					
					var monto2 = 0;
					
					for(var i=0; i<records.length; i++){
						monto2 += records[i].get('importe');
						NS.colorBanco = records[i].get('color');
					}
					
					Ext.getCmp(PF + 'txtMonto2').setValue(NS.formatNumber(monto2));
					
					var monto = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtMonto').getValue()));
					
					if(isNaN(monto))
						monto = 0;
					
					if(monto>monto2)
					    Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto-monto2)*100)/100));
					else
						Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(Math.round(parseFloat(monto2-monto)*100)/100));	
					
						
									
				}
			}
			},
			
	});
	
	
	NS.accionarCmbEmpresa = function(comboValor){
		NS.storeMovimientosBanco.removeAll();		
		NS.storeCmbChequera.baseParams.empresa = comboValor;
		NS.storeMovimientosBanco.baseParams.iNoEmpresa = comboValor;
		NS.storeCmbBanco.baseParams.empresa = comboValor;
		NS.storeDatos.removeAll();
		NS.gridDatos.getView().refresh();
		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}
	
	
	NS.ocultarComponentes = function(){
		Ext.getCmp(PF+'cmbBanco').setVisible(false);
		Ext.getCmp(PF+'cmbChequera').setVisible(false);
		Ext.getCmp(PF+'txtIdDivisa').setVisible(false);
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
		
		for(i = 0; i < recordsDatGrid.length; i = i + 1){
			if(recordsDatGrid[i].get('criterio') == sValor){
				indice = i;
				sValorAnterior = recordsDatGrid[i].get('valor');
			}
		}
		
		if(sTipoAgregado == 'criterio'){
			
			if(recordsDatGrid.length > 0){
				
				if((indice > 0 || (recordsDatGrid[0].get('criterio') == sValor)) && sValorAnterior !== ''){
					
					BFwrk.Util.msgShow('Ya indicó el criterio, necesita borrarlo antes','WARNING');
					Ext.getCmp(oIdElemento).setDisabled(true);
					return;
				}
				else
					NS.agregarValorGridDatos(sValor, oIdElemento, 0, sTipoAgregado);
			}else
				NS.agregarValorGridDatos(sValor, oIdElemento, 0, sTipoAgregado);
		}
		else if(sTipoAgregado == 'valor'){	
			NS.storeDatos.remove(recordsDatGrid[indice]);
			NS.agregarValorGridDatos(sValor, oIdElemento, indice, sTipoAgregado, sValorAnterior);
		}
	};
	
	NS.agregarValorGridDatos = function(sValor, oIdElemento, indice, sTipoAgregado, sValorAnterior){
		var valorCombo = Ext.getCmp(oIdElemento).getValue();
		var tamGrid = indice <= 0 ? (NS.storeDatos.data.items).length : indice;
		var datosClase = NS.gridDatos.getStore().recordType;
		
		if(sTipoAgregado == 'valor'){
			
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
		
	
	NS.buscar = function() {
		var banco = false;
	   	var chequera = false;
		var datosBusqueda = NS.storeDatos.data.items;
		
		
		/*if(datosBusqueda.length <= 0) {
			BFwrk.Util.msgShow('Debe indicar al menos el banco y la chequera en los criterios de búsqueda','WARNING');
			return;
		}
		*/
		NS.gridMovsCobranza.store.removeAll();
		NS.gridMovsBanco.store.removeAll();
		for(var i = 0; i < datosBusqueda.length; i++) {
			if(datosBusqueda[i].get('criterio') == 'BANCO') banco = true;
			if(datosBusqueda[i].get('criterio') == 'CHEQUERA') chequera = true;
			if(datosBusqueda[i].get('id') == '') {
				BFwrk.Util.msgShow('Debe indicar el valor de ' + datosBusqueda[i].get('criterio'),'WARNING');
				return;
			}
		}
		
	
		
		//Antes de asignar parametros se borran.
		NS.storeMovimientosCobranza.baseParams.iIdBanco = 0;
		NS.storeMovimientosBanco.baseParams.iIdBanco = 0;
		NS.storeMovimientosCobranza.baseParams.sIdChequera = '';
		NS.storeMovimientosBanco.baseParams.sIdChequera = '';
		NS.storeMovimientosCobranza.baseParams.sFecIni = '';
		NS.storeMovimientosCobranza.baseParams.sFecFin = '';
		NS.storeMovimientosBanco.baseParams.sFecIni = '';
		NS.storeMovimientosBanco.baseParams.sFecFin = '';
		NS.storeMovimientosCobranza.baseParams.uMontoIni = '';
		NS.storeMovimientosCobranza.baseParams.uMontoFin = '';
		NS.storeMovimientosBanco.baseParams.uMontoIni = '';
		NS.storeMovimientosBanco.baseParams.uMontoFin = '';
		NS.storeMovimientosCobranza.baseParams.idDivisa = '';
		NS.storeMovimientosBanco.baseParams.idDivisa = '';
		
		if (Ext.getCmp(PF + 'cmbDivisa').getValue() == "") {
			BFwrk.Util.msgShow('Debe seleccionar una divisa para la busqueda.','WARNING');
			return;
		} else {			
			NS.storeMovimientosCobranza.baseParams.idDivisa = Ext.getCmp(PF + 'cmbDivisa').getValue();
			NS.storeMovimientosBanco.baseParams.idDivisa = Ext.getCmp(PF + 'cmbDivisa').getValue();
			NS.iIdBanco = parseInt(Ext.getCmp(PF + 'cmbDivisa').getValue());
		}
		
		for(var i = 0; i < datosBusqueda.length; i = i + 1) {
   			if(datosBusqueda[i].get('criterio') == 'BANCO') {
				NS.storeMovimientosCobranza.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
				NS.storeMovimientosBanco.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
				NS.iIdBanco = parseInt(datosBusqueda[i].get('id'));
				
   			}else if(datosBusqueda[i].get('criterio') == 'CHEQUERA') {
   				NS.storeMovimientosCobranza.baseParams.sIdChequera = datosBusqueda[i].get('id');
				NS.storeMovimientosBanco.baseParams.sIdChequera = datosBusqueda[i].get('id');
				NS.sIdChequera = datosBusqueda[i].get('id');
				
   			}else if(datosBusqueda[i].get('criterio') == 'FECHA') {
   				var valorFechas = datosBusqueda[i].get('valor');
   				var ini = NS.obtenerValIni(valorFechas);
				var fin = NS.obtenerValFin(valorFechas);
   				NS.storeMovimientosCobranza.baseParams.sFecIni = ini;
   				NS.storeMovimientosCobranza.baseParams.sFecFin = fin;
   				NS.storeMovimientosBanco.baseParams.sFecIni = ini;
   				NS.storeMovimientosBanco.baseParams.sFecFin = fin;
   				NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
   				NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFin').getValue());

   			}else if(datosBusqueda[i].get('criterio') == 'MONTOS') {
   				var valorImporte = datosBusqueda[i].get('valor');
   				var ini = NS.obtenerValIni(valorImporte);
				var fin = NS.obtenerValFin(valorImporte);
				NS.storeMovimientosCobranza.baseParams.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
				NS.storeMovimientosCobranza.baseParams.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
				NS.storeMovimientosBanco.baseParams.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
				NS.storeMovimientosBanco.baseParams.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
				NS.uMontoIni = BFwrk.Util.unformatNumber(''+ini);
				NS.uMontoFin = BFwrk.Util.unformatNumber(''+fin);
   			}else if(datosBusqueda[i].get('criterio') == 'DIVISA') {
				NS.storeMovimientosCobranza.baseParams.idDivisa = datosBusqueda[i].get('id');
				NS.storeMovimientosBanco.baseParams.idDivisa = datosBusqueda[i].get('id');
				NS.iIdBanco = parseInt(datosBusqueda[i].get('id'));
   			}
   		}
		
		if(NS.optBusqueda == 0) {
			
			
			//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosBanco, msg:"Buscando..."});
			
			//Ext.getCmp(PF + 'btnConVir').setDisabled(true);
			Ext.getCmp(PF + 'btnParcializar').setDisabled(true);
			Ext.getCmp(PF + 'btnConfirmar').setDisabled(true);
			NS.storeMovimientosBanco.load();
			
		}else if(NS.optBusqueda == 1) {
			
			//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosCobranza, msg:"Buscando..."});
			
			//Ext.getCmp(PF + 'btnConVir').setDisabled(true);
			Ext.getCmp(PF + 'btnParcializar').setDisabled(true);
			Ext.getCmp(PF + 'btnConfirmar').setDisabled(true);
			NS.storeMovimientosCobranza.baseParams.iNoEmpresa = parseInt(NS.txtEmpresa.getValue());
			NS.storeMovimientosCobranza.load();
			
		}else if(NS.optBusqueda == 2) {
			
			
			
			//Ext.getCmp(PF + 'btnConVir').setDisabled(false);
			Ext.getCmp(PF + 'btnParcializar').setDisabled(false);
			Ext.getCmp(PF + 'btnConfirmar').setDisabled(false);
			NS.storeMovimientosCobranza.baseParams.iNoEmpresa = parseInt(NS.txtEmpresa.getValue());
			NS.storeMovimientosCobranza.load();
			NS.storeMovimientosBanco.load();
			//NS.storeMovimientosCobranza.load();
		}
		
		//Ext.getCmp(PF + 'btnConVirB&C').setDisabled(false);
		//Ext.getCmp(PF + 'btnActPantalla').setDisabled(false);
		//Ext.getCmp(PF + 'btnExcel').setDisabled(false);
		
    };
	
    NS.obtenerValIni = function(valor){
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
    
	NS.obtenerValFin = function(valor){
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
//***************************Ventana Conciliacion Virtual SBanco_ mayor cobranza******************************************
	
	NS.winConciliacionVirtual = new Ext.Window({
		title: 'Conciliación Virtual',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 423,
	   	height: 450,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    draggable: false,
	    resizable: false,
	   	items: [
	   	        {
					xtype: 'fieldset',
					title: '',
					width: 421,
					height: 428,
					x: 1,
					y: 1,
					layout: 'absolute',
					id: PF + 'fieldGeneral',
					items: [
					        {
										   	xtype: 'fieldset',
										    title: 'Datos Conciliación',
										    width: 370,
										    height: 110,
										    x: 8,
										    y: 5,
										    layout: 'absolute',
										    id: PF + 'fieldDatosConcilia',
										    items: [
													{
													    xtype: 'label',
													    text: 'No. Empresa',
													    id: PF + 'lbNoEmpresa',
													    x: 10,
													    y: 5
													},{
													    xtype: 'label',
													    text: '',
													    id: PF + 'NoEmpresalb',
													    x: 250,
													    y: 5,
													    
													},{
													    xtype: 'label',
													    text: 'Folio Movimiento',
													    id: PF + 'lbFolioMov',
													    x: 10,
													    y: 25
													},{
													    xtype: 'label',
													    text: '',
													    id: PF + 'FolioMovlb',
													    x: 250,
													    y: 25
													},{
													    xtype: 'label',
													    text: 'Folio Cobranza',
													    id: PF + 'lbFolioCob',
													    x: 10,
													    y:45
													},
													
													
													{
													    xtype: 'label',
													    text: '',
													    id: PF + 'FolioCoblb',
													    x: 250,
													    y:45
													}
										            
										            
										            ]
										},
										{
										   	xtype: 'fieldset',
										    title: 'Nuevo Registro',
										    width: 370,
										    height: 210,
										    x: 8,
										    y: 120,
										    layout: 'absolute',
										    id: PF + 'fieldNvoRegistro',
										    items: [
										           
										          
														{
														    xtype: 'label',
														    text: '',
														    id: PF + 'lbFolioNvo',
														    x: 10,
														    y: 5
														},
														
														//NS.cmbNuevoFolioC
														/*{
														    xtype: 'label',
														    text: '',
														    id: PF + 'FolioNvolb',
														    x: 70,
														    y: 5
														}*/{
														    xtype: 'label',
														    text: 'Importe',
														    id: PF + 'lbImporte',
														    x: 10,
														    y: 30,
														    
														},{
														    xtype: 'label',
														    text: '',
														    id: PF + 'Importelb',
														    x: 250,
														    y: 30,
														    
														},{
														    xtype: 'label',
														    text: 'Cuenta Contable',
														    id: PF + 'lbCuentaConta',
														    x: 10,
														    y: 60
														},
														
														{
														    xtype: 'label',
														    text: 'Origen',
														    id: PF + 'lbOrigen',
														    x: 10,
														    y: 100
														},
														{
														    xtype: 'label',
														    text: 'Causa',
														    id: PF + 'lbCausa',
														    x: 10,
														    y: 140
														},{
												        	xtype: 'textfield',
												        	x: 40,
												        	y: 155,
												        	width: 300,
												        	name: PF+'txtCausa',
												        	id: PF+'txtCausa',
												        	hidden: false,
												        	disabled: false,
												        	listeners: {
												        		
												        				}
														 },
										            
														NS.cmbCuentaContable4,
														 NS.cmbOrigen
														 
										            ]
										},{
					                        xtype: 'button',
					                        id: PF+'cmdCerrarConVirtual',
					                        name: PF+'cmdCerrarConVirtual',
					                        text: 'Cerrar',
					                        x: 200,
					                        y: 340,
					                        width: 80,
					                        listeners:{
						               			click:{
						           			   		fn:function(e){
						           			   		NS.winConciliacionVirtual.hide();
						           			   		}
						        			   	}
						    			   	}
					                    },{
					                        xtype: 'button',
					                        id: PF+'cmdAceptarConVirtual',
					                        name: PF+'cmdAceptarConVirtual',
					                        text: 'Aceptar',
					                        x: 115,
					                        y: 340,
					                        width: 80,
					                        listeners:{
						               			click:{
						           			   		fn:function(e){
						           			   			
						           			   			
						           			   			
						           			   			
						           			   			//////////////////////////////////////////////////////////////////

						           			   			
							           			   		var regsBanco = NS.gridMovsCobranza.getSelectionModel().getSelections();
								        				var regsCobranza = NS.gridMovsBanco.getSelectionModel().getSelections();
								        				var causa = (Ext.getCmp(PF+'txtCausa').getValue());
								        				var origen = (Ext.getCmp(PF+'cmbOrigen').getValue());
								        				var cuenta = (Ext.getCmp(PF+'cmbCuentaContable4').getValue());

						        						var matBan = new Array();
			        		          					
			        		          					for(var i=0; i<regsBanco.length;i++){
					                						var vec = {};
					            							vec.importe = regsBanco[i].get('importe');
					            							vec.idDivisa = regsBanco[i].get('idDivisa');
					            							vec.referencia = regsBanco[i].get('referencia');
					            							vec.noEmpresa = regsBanco[i].get('noEmpresa');
					            							vec.nomEmpresa = regsBanco[i].get('nomEmpresa');
					            							vec.noDocto2 = regsBanco[i].get('noDocto2');
					            							vec.noFactura2 = regsBanco[i].get('noFactura2');
					            							vec.fecFactura = regsBanco[i].get('fecFactura');
					            							vec.fecValor = regsBanco[i].get('fecValor');
					            							vec.noCliente = regsBanco[i].get('noCliente');
					            							vec.concepto = regsBanco[i].get('concepto');
					            							vec.folioCob = regsBanco[i].get('folioCob');
					            							vec.secuencia = regsBanco[i].get('secuencia');
					            							vec.razonSoc = regsBanco[i].get('razonSoc');
					            							vec.status = regsBanco[i].get('estatus');
					            							vec.folioDetMov = regsBanco[i].get('folioDetMov');
					            							vec.importeSol = regsBanco[i].get('importeSol');
					            							
					            							
					            							matBan[i] = vec;
					                					}
					            							
					            						var jsonBan = Ext.util.JSON.encode(matBan);
					            						/******************************************/
					            						
					            						/*** 	JSON PARA BANCO	***/
					            						var matCob = new Array();
			        		          					
			        		          					for(var i=0; i<regsCobranza.length;i++){
					                						var vec = {};
					                					
					                						vec.nomEmpresa = regsCobranza[i].get('nomEmpresa');
					                						vec.noEmpresa = regsCobranza[i].get('noEmpresa');
					            							vec.idBanco = regsCobranza[i].get('idBanco');
					            							vec.descBanco = regsCobranza[i].get('descBanco');
					            							vec.noCuenta= regsCobranza[i].get('noCuenta');
					            							vec.importe = regsCobranza[i].get('importe');
					            							vec.fecOperacion = regsCobranza[i].get('fecOperacion');
					            							vec.fecValor = regsCobranza[i].get('fecValor');
					            							vec.idDivisa = regsCobranza[i].get('idDivisa');
					            							vec.referencia = regsCobranza[i].get('referencia');
					            							vec.concepto= regsCobranza[i].get('concepto');
					            							vec.observacion = regsCobranza[i].get('observacion');
					            							vec.descripcion = regsCobranza[i].get('descripcion');
					            							vec.idTipoOperacion= regsCobranza[i].get('idTipoOperacion');
					            							vec.noFolioDet = regsCobranza[i].get('noFolioDet');
					            							vec.noCliente = regsCobranza[i].get('noCliente');
					            							vec.noDocto = regsCobranza[i].get('noDocto');
					            							vec.idChequera = regsCobranza[i].get('idChequera');
					            							vec.idEstatusMov = regsCobranza[i].get('idEstatusMov');
					            							vec.noFolioMov = regsCobranza[i].get('noFolioMov');
					            							vec.idFormaPago = regsCobranza[i].get('idFormaPago'); 
					            							vec.idCaja = regsCobranza[i].get('idCaja');
					            							vec.idBancoBenef = regsCobranza[i].get('idBancoBenef');
					            							vec.idRubro = regsCobranza[i].get('idRubro'); 
					            							vec.division = regsCobranza[i].get('division');
					            							
					            							
					            							
					            							matCob[i] = vec;
					                					}
					            							
					            						var jsonCob = Ext.util.JSON.encode(matCob);
					            						/******************************************/
							        					
							        					IdentificacionIngresosAction.conciliaVirtualCobranza(jsonBan, jsonCob, ''+causa, ''+origen, ''+cuenta, function(resultado, e) {
							        						
							        						if(e.message=="Unable to connect to the server."){
							        							Ext.Msg.alert('SET','Error de conexión al servidor');
							        							return;
							        						}
							        						
								        					if (resultado.error != '') {
								        						Ext.Msg.alert('SET', resultado.error );
								        						NS.buscar();

								        						NS.winConciliacionVirtual.hide();
								        						return;
								        					}else{
								        						Ext.Msg.alert('SET', resultado.mensaje );
								        						NS.buscar();
								        						NS.winConciliacionVirtual.hide();
								        						return;
								        					}
							        						
							        					});
								        				
								        				
							        				
							        				
						           			   		
						           			   		}
						        			   	}
						    			   	}
					                    },	
						            ]
						}
	   	        
	   	        
	   	        
	   	        ],
	   	     listeners:{

	        	show:{
	        		
	        		fn:function(){
	        			
	        			
   	        			
	        			
	        			var monto = NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
	        			var monto2 = NS.unformatNumber(Ext.getCmp(PF+'txtMonto2').getValue());
	        			var regsBanco = NS.gridMovsCobranza.getSelectionModel().getSelections();
    				    var regsCobranza = NS.gridMovsBanco.getSelectionModel().getSelections();
    				    var diferencia = (Math.round(parseFloat(monto-monto2)*100)/100);
    				
    				
    				Ext.getCmp(PF + 'NoEmpresalb').setText(regsCobranza[0].get('noEmpresa'));
    				Ext.getCmp(PF+'FolioMovlb').setText(regsCobranza[0].get('noFolioDet'));
	        		Ext.getCmp(PF+'FolioCoblb').setText(regsBanco[0].get('folioCob'));
	        		Ext.getCmp(PF + 'lbFolioNvo').setText('Folio Cobranza:');
	        		Ext.getCmp(PF + 'Importelb').setText(diferencia);
	        		
	        			
	        		
	        		}
	        	},
   	        	hide:{
   	        		fn:function(){
   	        		
   	        			Ext.getCmp(PF + 'cmbCuentaContable4').setValue('');
   	        			Ext.getCmp(PF + 'cmbOrigen').setValue('');
   	        			Ext.getCmp(PF + 'txtCausa').setValue('');
   	        			Ext.getCmp(PF + 'cmbCuentaContable4').reset();
   	        			Ext.getCmp(PF + 'cmbOrigen').reset();
	        			
	        		}
	        
	   	}
	   	        	
	   	     }      	
		});
	
	
	//***************************Ventana Conciliacion Virtual mayor banco*****************************************
	
	NS.winConciliacionVirtual2 = new Ext.Window({
		title: 'Conciliación Virtual Banco',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 423,
	   	height: 450,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    draggable: false,
	    resizable: false,
	   	items: [
	   	        
						{
						   	xtype: 'fieldset',
						    title: '',
						    width: 421,
						    height: 428,
						    x: 1,
						    y: 1,
						    layout: 'absolute',
						    id: PF + 'fieldGeneral',
						    items: [
										{
										   	xtype: 'fieldset',
										    title: 'Datos Conciliación2',
										    width: 370,
										    height: 110,
										    x: 8,
										    y: 5,
										    layout: 'absolute',
										    id: PF + 'fieldDatosConcilia',
										    items: [
													{
													    xtype: 'label',
													    text: 'No. Empresa',
													    id: PF + 'lbNoEmpresa2',
													    x: 10,
													    y: 5
													},{
													    xtype: 'label',
													    text: '',
													    id: PF + 'NoEmpresalb2',
													    x: 250,
													    y: 5,
													    
													},{
													    xtype: 'label',
													    text: 'Folio Movimiento',
													    id: PF + 'lbFolioMov2',
													    x: 10,
													    y: 25
													},{
													    xtype: 'label',
													    text: '',
													    id: PF + 'FolioMovlb2',
													    x: 250,
													    y: 25
													},{
													    xtype: 'label',
													    text: 'Folio Cobranza',
													    id: PF + 'lbFolioCob2',
													    x: 10,
													    y:45
													},{
													    xtype: 'label',
													    text: '',
													    id: PF + 'FolioCoblb2',
													    x: 250,
													    y:45
													}
										            
										            
										            ]
										},
										{
										   	xtype: 'fieldset',
										    title: 'Nuevo Registro',
										    width: 370,
										    height: 210,
										    x: 8,
										    y: 120,
										    layout: 'absolute',
										    id: PF + 'fieldNvoRegistro2',
										    items: [
										           
										            	
														{
														    xtype: 'label',
														    text: '',
														    id: PF + 'lbFolioNvo2',
														    x: 10,
														    y: 5
														},
														//NS.cmbNuevoFolioM
														
														/*{
														    xtype: 'label',
														    text: '',
														    id: PF + 'FolioNvolb2',
														    x: 10,
														    y: 5
														}*/{
														    xtype: 'label',
														    text: 'Importe',
														    id: PF + 'lbImporte2',
														    x: 10,
														    y: 30,
														    
														},{
														    xtype: 'label',
														    text: '',
														    id: PF + 'Importelb2',
														    x: 250,
														    y: 30,
														    
														},{
														    xtype: 'label',
														    text: 'Cuenta Contable',
														    id: PF + 'lbCuentaConta2',
														    x: 10,
														    y: 60
														},
														
														{
														    xtype: 'label',
														    text: 'Origen',
														    id: PF + 'lbOrigen2',
														    x: 10,
														    y: 100
														},
														{
														    xtype: 'label',
														    text: 'Causa',
														    id: PF + 'lbCausa2',
														    x: 10,
														    y: 140
														},{
												        	xtype: 'textfield',
												        	x: 40,
												        	y: 155,
												        	width: 300,
												        	name: PF+'txtCausa2',
												        	id: PF+'txtCausa2',
												        	hidden: false,
												        	disabled: false,
												        	listeners: {
												        		
												        				}
														 },
										            
														NS.cmbCuentaContable3,
														 NS.cmbOrigen2
														 
										            ]
										},{
					                        xtype: 'button',
					                        id: PF+'cmdCerrarConVirtual2',
					                        name: PF+'cmdCerrarConVirtual2',
					                        text: 'Cerrar',
					                        x: 200,
					                        y: 340,
					                        width: 80,
					                        listeners:{
						               			click:{
						           			   		fn:function(e){
						           			   		NS.winConciliacionVirtual2.hide();
						           			   		
						           			   		
						           			   		}
						        			   	}
						    			   	}
					                    },{
					                        xtype: 'button',
					                        id: PF+'cmdAceptarConVirtual2',
					                        name: PF+'cmdAceptarConVirtual2',
					                        text: 'Aceptar',
					                        x: 115,
					                        y: 340,
					                        width: 80,
					                        listeners:{
						               			click:{
						           			   		fn:function(e){
						           			   			
						           			   			//////////////////////////////////////////////////////////////////

						           			   			
							           			   		var regsBanco = NS.gridMovsCobranza.getSelectionModel().getSelections();
								        				var regsCobranza = NS.gridMovsBanco.getSelectionModel().getSelections();
								        				var causa = (Ext.getCmp(PF+'txtCausa2').getValue());
								        				var origen = (Ext.getCmp(PF+'cmbOrigen2').getValue());
								        				var cuenta = (Ext.getCmp(PF+'cmbCuentaContable3').getValue());

						        						var matBan = new Array();
			        		          					
			        		          					for(var i=0; i<regsBanco.length;i++){
					                						var vec = {};
					            							
					            							vec.importe = regsBanco[i].get('importe');
					            							vec.idDivisa = regsBanco[i].get('idDivisa');
					            							vec.referencia = regsBanco[i].get('referencia');
					            							vec.noEmpresa = regsBanco[i].get('noEmpresa');
					            							vec.nomEmpresa = regsBanco[i].get('nomEmpresa');
					            							vec.noDocto2 = regsBanco[i].get('noDocto2');
					            							vec.noFactura2 = regsBanco[i].get('noFactura2');
					            							vec.fecFactura = regsBanco[i].get('fecFactura');
					            							vec.fecValor = regsBanco[i].get('fecValor');
					            							vec.noCliente = regsBanco[i].get('noCliente');
					            							vec.concepto = regsBanco[i].get('concepto');
					            							vec.folioCob = regsBanco[i].get('folioCob');
					            							vec.secuencia = regsBanco[i].get('secuencia');
					            							vec.razonSoc = regsBanco[i].get('razonSoc');
					            							vec.status = regsBanco[i].get('estatus');
					            							vec.folioDetMov = regsBanco[i].get('folioDetMov');
					            							vec.importeSol = regsBanco[i].get('importeSol');
					            							
					            							
					            							matBan[i] = vec;
					                					}
					            							
					            						var jsonBan = Ext.util.JSON.encode(matBan);
					            						/******************************************/
					            						
					            						/*** 	JSON PARA COBRANZA	***/
					            						var matCob = new Array();
			        		          					
			        		          					for(var i=0; i<regsCobranza.length;i++){
					                						var vec = {};
					                					
					                						vec.nomEmpresa = regsCobranza[i].get('nomEmpresa');
					                						vec.noEmpresa = regsCobranza[i].get('noEmpresa');
					            							vec.idBanco = regsCobranza[i].get('idBanco');
					            							vec.descBanco = regsCobranza[i].get('descBanco');
					            							vec.noCuenta= regsCobranza[i].get('noCuenta');
					            							vec.importe = regsCobranza[i].get('importe');
					            							vec.fecOperacion = regsCobranza[i].get('fecOperacion');
					            							vec.fecValor = regsCobranza[i].get('fecValor');
					            							vec.idDivisa = regsCobranza[i].get('idDivisa');
					            							vec.referencia = regsCobranza[i].get('referencia');
					            							vec.concepto= regsCobranza[i].get('concepto');
					            							vec.observacion = regsCobranza[i].get('observacion');
					            							vec.descripcion = regsCobranza[i].get('descripcion');
					            							vec.idTipoOperacion= regsCobranza[i].get('idTipoOperacion');
					            							vec.noFolioDet = regsCobranza[i].get('noFolioDet');
					            							vec.noCliente = regsCobranza[i].get('noCliente');
					            							vec.noDocto = regsCobranza[i].get('noDocto');
					            							vec.idChequera = regsCobranza[i].get('idChequera');
					            							vec.idEstatusMov = regsCobranza[i].get('idEstatusMov');
					            							vec.noFolioMov = regsCobranza[i].get('noFolioMov');
					            							vec.idFormaPago = regsCobranza[i].get('idFormaPago'); 
					            							vec.idCaja = regsCobranza[i].get('idCaja');
					            							vec.idBancoBenef = regsCobranza[i].get('idBancoBenef');
					            							vec.idRubro = regsCobranza[i].get('idRubro'); 
					            							vec.division = regsCobranza[i].get('division');
					            							
					            							matCob[i] = vec;
					                					}
					            							
					            						var jsonCob = Ext.util.JSON.encode(matCob);
					            						/******************************************/
							        					
							        					IdentificacionIngresosAction.conciliaVirtualBanco(jsonBan, jsonCob, ''+causa, ''+origen, ''+cuenta, function(resultado, e) {
							        						
							        						if(e.message=="Unable to connect to the server."){
							        							Ext.Msg.alert('SET','Error de conexión al servidor');
							        							return;
							        						}
							        						
								        					if (resultado.error != '') {
								        						Ext.Msg.alert('SET', resultado.error );
								        						NS.buscar();

								        						NS.winConciliacionVirtual2.hide();
								        						return;
								        					}else{
								        						Ext.Msg.alert('SET', resultado.mensaje );
								        						NS.buscar();
								        						NS.winConciliacionVirtual2.hide();
								        						return;
								        					}
							        						
							        					});
								        				
								        				
							        				
							        				
						           			   		
						           			   		}
						        			   	}
						    			   	}
					                    },	
						            ]
						}
	   	        
	   	        
	   	        
	   	        ],
	   	     listeners:{

	        	show:{
	        		
	        		fn:function(){
	        			
	        			
   	        			
	        			
	        			var monto = NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
	        			var monto2 = NS.unformatNumber(Ext.getCmp(PF+'txtMonto2').getValue());
	        			var regsBanco = NS.gridMovsCobranza.getSelectionModel().getSelections();
    				    var regsCobranza = NS.gridMovsBanco.getSelectionModel().getSelections();
    				    var diferencia = (Math.round(parseFloat(monto2-monto)*100)/100);
    				
    				
    				Ext.getCmp(PF + 'NoEmpresalb2').setText(regsCobranza[0].get('noEmpresa'));
    				Ext.getCmp(PF+'FolioMovlb2').setText(regsCobranza[0].get('noFolioDet'));
	        		Ext.getCmp(PF+'FolioCoblb2').setText(regsBanco[0].get('folioCob'));
	        		Ext.getCmp(PF + 'lbFolioNvo2').setText('Folio Banco:');
	        		Ext.getCmp(PF + 'Importelb2').setText(diferencia);
	        				
	        		
	        		}
	        	},
   	        	hide:{
   	        		fn:function(){
   	        		
   	        			Ext.getCmp(PF + 'cmbCuentaContable3').setValue('');
   	        			Ext.getCmp(PF + 'cmbOrigen2').setValue('');
   	        			Ext.getCmp(PF + 'txtCausa2').setValue('');
   	        			Ext.getCmp(PF + 'cmbCuentaContable3').reset();
   	        			Ext.getCmp(PF + 'cmbOrigen2').reset();
   	        			
   	        			
   	        			
	        			
	        		}
	        
	   	}
	   	        	
	   	     }      	
		});
	
	
	
	//***************************Ventana Conciliacion Virtual solo de Banco ******************************************
	

	NS.winConciliacionVirtualBancoC = new Ext.Window({
		title: 'Conciliación Virtual Banco',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 423,
	   	height: 410,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    draggable: false,
	    resizable: false,
	   	items: [
	   	        
						{
						   	xtype: 'fieldset',
						    title: '',
						    width: 421,
						    height: 408,
						    x: 1,
						    y: 1,
						    layout: 'absolute',
						    id: PF + 'fieldGeneral2',
						    items: [
										{
										   	xtype: 'fieldset',
										    title: 'Datos Generales',
										    width: 370,
										    height: 90,
										    x: 8,
										    y: 5,
										    layout: 'absolute',
										    id: PF + 'fieldDatosGeneralesSBanco',
										    items: [
													{
													    xtype: 'label',
													    text: 'Empresa',
													    id: PF + 'lbEmpresaSBanco',
													    x: 10,
													    y: 5
													},{
											        	xtype: 'textfield',
											        	x: 40,
											        	y: 20,
											        	width: 300,
											        	name: PF+'txtEmpresaSBanco',
											        	id: PF+'txtEmpresaSBanco',
											        	hidden: false,
											        	disabled: true,
											        	listeners: {
											        		
											        				}
													 }
										            
										            ]
										},
										{
										   	xtype: 'fieldset',
										    title: 'Nuevo Registro',
										    width: 370,
										    height: 210,
										    x: 8,
										    y: 100,
										    layout: 'absolute',
										    id: PF + 'fieldNvoRegistroSBanco',
										    items: [
										           
														{
														    xtype: 'label',
														    text: 'Importe',
														    id: PF + 'lbImporteSBanco',
														    x: 10,
														    y: 20
														},{
														    xtype: 'label',
														    text: '',
														    id: PF + 'ImporteSBanco',
														    x: 90,
														    y: 20
														},{
														    xtype: 'label',
														    text: 'Cuenta Contable',
														    id: PF + 'lbCuentaContaSBanco',
														    x: 10,
														    y: 60
														},{
														    xtype: 'label',
														    text: 'Causa',
														    id: PF + 'lbCausaSBanco',
														    x: 10,
														    y: 95
														},{
												        	xtype: 'textfield',
												        	x: 40,
												        	y: 110,
												        	width: 300,
												        	name: PF+'txtCausaSBanco',
												        	id: PF+'txtCausaSBanco',
												        	hidden: false,
												        	disabled: false,
												        	listeners: {
												        		
												        				}
														 },
										            
														 NS.cmbCuentaContable
										            ]
										},{
					                        xtype: 'button',
					                        id: PF+'cmdCerrarConVirtualSBanco',
					                        name: PF+'cmdCerrarConVirtualSBanco',
					                        text: 'Cerrar',
					                        x: 200,
					                        y: 325,
					                        width: 80,
					                        listeners:{
						               			click:{
						           			   		fn:function(e){
						           			   		NS.winConciliacionVirtualBancoC.hide();
						           			   		
						           			   		}
						        			   	}
						    			   	}
					                    },{
					                        xtype: 'button',
					                        id: PF+'cmdAceptarConVirtualSBanco',
					                        name: PF+'cmdAceptarConVirtualSBanco',
					                        text: 'Aceptar',
					                        x: 115,
					                        y: 325,
					                        width: 80,
					                        listeners:{
						               			click:{
						           			   		fn:function(e){

						           			   			

						    	   	        			var regsCobranza = NS.gridMovsBanco.getSelectionModel().getSelections();
						    	   	        			var causa = (Ext.getCmp(PF+'txtCausaSBanco').getValue());
						    	   	        			var cuenta =(Ext.getCmp(PF+'cmbCuentaContable').getValue());
						        						
					            						var matCob = new Array();
							          					
							          					for(var i=0; i<regsCobranza.length;i++){
					                						var vec = {};
					                						
					                						vec.nomEmpresa = regsCobranza[i].get('nomEmpresa');
					                						vec.noEmpresa = regsCobranza[i].get('noEmpresa');
					            							vec.idBanco = regsCobranza[i].get('idBanco');
					            							vec.descBanco = regsCobranza[i].get('descBanco');
					            							vec.noCuenta= regsCobranza[i].get('noCuenta');
					            							vec.importe = regsCobranza[i].get('importe');
					            							vec.fecOperacion = regsCobranza[i].get('fecOperacion');
					            							vec.fecValor = regsCobranza[i].get('fecValor');
					            							vec.idDivisa = regsCobranza[i].get('idDivisa');
					            							vec.referencia = regsCobranza[i].get('referencia');
					            							vec.concepto= regsCobranza[i].get('concepto');
					            							vec.observacion = regsCobranza[i].get('observacion');
					            							vec.descripcion = regsCobranza[i].get('descripcion');
					            							vec.idTipoOperacion= regsCobranza[i].get('idTipoOperacion');
					            							vec.noFolioDet = regsCobranza[i].get('noFolioDet');
					            							vec.noCliente = regsCobranza[i].get('noCliente');
					            							vec.noDocto = regsCobranza[i].get('noDocto');
					            							vec.idChequera = regsCobranza[i].get('idChequera');
					            							vec.idEstatusMov = regsCobranza[i].get('idEstatusMov');
					            							vec.noFolioMov = regsCobranza[i].get('noFolioMov');
					            							vec.idFormaPago = regsCobranza[i].get('idFormaPago'); 
					            							vec.idCaja = regsCobranza[i].get('idCaja');
					            							vec.idBancoBenef = regsCobranza[i].get('idBancoBenef');
					            							vec.idRubro = regsCobranza[i].get('idRubro'); 
					            							vec.division = regsCobranza[i].get('division');
					            							
					                						
					            							matCob[i] = vec;
					                					}
					            							
					            						var jsonCob = Ext.util.JSON.encode(matCob);
					            						/******************************************/
							        					
							        					IdentificacionIngresosAction.concilVirSBanco( jsonCob, ''+causa, ''+cuenta,function(resultado, e) {
							        						
							        						if(e.message=="Unable to connect to the server."){
							        							Ext.Msg.alert('SET','Error de conexión al servidor');
							        							return;
							        						}
							        						
								        					if (resultado.error != '') {
								        						Ext.Msg.alert('SET', resultado.error );
								        						return;
								        					}else{
								        						Ext.Msg.alert('SET', resultado.mensaje );
								        						NS.buscar();
								        						NS.winConciliacionVirtualBancoC.hide();
								        						
								  
								        						
								        						return;
								        					}
							        						
							        					});
							        					
							        			
						        					
						           			   		}
						        			   	}
						    			   	}
					                    },	
						            ]
						
							}
	   	        
	   	        ],
	   	     listeners:{

	        	show:{
	        		
	        		fn:function(){
	        			
	        			
	        			var    regsBanco = NS.gridMovsBanco.getSelectionModel().getSelections();
   	        			
    					Ext.getCmp(PF + 'ImporteSBanco').setText(regsBanco[0].get('importe'));
    					Ext.getCmp(PF + 'txtEmpresaSBanco').setValue(regsBanco[0].get('nomEmpresa'));
	        		
	        		}
	        	},
   	        	hide:{
   	        		fn:function(){
   	        			Ext.getCmp(PF + 'txtCausaSBanco').setValue('');
   	        			Ext.getCmp(PF + 'cmbCuentaContable').setValue('');
   	        			Ext.getCmp(PF + 'cmbCuentaContable').reset();
    					Ext.getCmp(PF + 'txtDiferencia').setValue('');
    					Ext.getCmp(PF + 'txtMonto').setValue('');
    					Ext.getCmp(PF + 'txtMonto2').setValue('');
    					Ext.getCmp(PF + 'txtRegistros').setValue('');
    					Ext.getCmp(PF + 'txtRegistros2').setValue('');
	        			
	        		}
	        
	   	}
	   	        	
	   	     }      	
		});
	
	
	
	
	
	
	
//***************************Ventana Conciliacion Virtual solo de Cobranza ******************************************
	

	NS.winConciliacionVirtualSCobranza = new Ext.Window({
		title: 'Conciliación Virtual Cobranza ',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 423,
	   	height: 410,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    draggable: false,
	    resizable: false,
	   	items: [
	   	        
						{
						   	xtype: 'fieldset',
						    title: '',
						    width: 421,
						    height: 408,
						    x: 1,
						    y: 1,
						    layout: 'absolute',
						    id: PF + 'fieldGeneral3',
						    items: [
										{
										   	xtype: 'fieldset',
										    title: 'Datos Generales',
										    width: 370,
										    height: 90,
										    x: 8,
										    y: 5,
										    layout: 'absolute',
										    id: PF + 'fieldDatosGeneralesSCobranza',
										    items: [
													{
													    xtype: 'label',
													    text: 'Empresa',
													    id: PF + 'lbEmpresaSCobranza',
													    x: 10,
													    y: 5
													},{
											        	xtype: 'textfield',
											        	x: 60,
											        	y: 5,
											        	width: 280,
											        	name: PF+'txtEmpresaSCobranza',
											        	id: PF+'txtEmpresaSCobranza',
											        	hidden: false,
											        	disabled: true,
											        	listeners: {
											        		
											        	}
													 },{
														    xtype: 'label',
														    text: 'Cliente',
														    id: PF + 'lbClienteSCobranza',
														    x: 10,
														    y: 30
														},{
												        	xtype: 'textfield',
												        	x: 60,
												        	y: 30,
												        	width: 280,
												        	name: PF+'txtClienteSCobranza',
												        	id: PF+'txtClienteSCobranza',
												        	hidden: false,
												        	disabled: true,
												        	listeners: {
												        		
												        	}
														 }
										            
										            ]
										},
										{
										   	xtype: 'fieldset',
										    title: 'Nuevo Registro',
										    width: 370,
										    height: 210,
										    x: 8,
										    y: 100,
										    layout: 'absolute',
										    id: PF + 'fieldNvoRegistroSCobranza',
										    items: [
										            
										            {
														    xtype: 'label',
														    text: 'Importe',
														    id: PF + 'lbImporteSCobranza',
														    x: 10,
														    y: 20
														},{
														    xtype: 'label',
														    text: '',
														    id: PF + 'ImporteSCobranza',
														    x: 90,
														    y: 20
														},{

														    xtype: 'label',
														    text: 'Cuenta Contable',
														    id: PF + 'lbCuentaContaSCobranza',
														    x: 10,
														    y: 60
														},{
														    xtype: 'label',
														    text: 'Causa',
														    id: PF + 'lbCausaSCobranza',
														    x: 10,
														    y: 95
														},{
												        	xtype: 'textfield',
												        	x: 40,
												        	y: 110,
												        	width: 300,
												        	name: PF+'txtCausaSCobranza',
												        	id: PF+'txtCausaSCobranza',
												        	hidden: false,
												        	disabled: false,
												        	listeners: {
												        		change: {
												        			fn:function(caja) {
												        				
												        			}
												        		}
												        	}
														 },
										            
														 NS.cmbCuentaContable2
										            ]
										},{
					                        xtype: 'button',
					                        id: PF+'cmdCerrarConVirtualSCobranza',
					                        name: PF+'cmdCerrarConVirtualSCobranza',
					                        text: 'Cerrar',
					                        x: 200,
					                        y: 325,
					                        width: 80,
					                        listeners:{
						               			click:{
						           			   		fn:function(e){
						           			   		NS.winConciliacionVirtualSCobranza.hide();
						           			   		
						           			   		}
						        			   	}
						    			   	}
					                    },{
					                        xtype: 'button',
					                        id: PF+'cmdAceptarConVirtualSCobranza',
					                        name: PF+'cmdAceptarConVirtualSCobranza',
					                        text: 'Aceptar',
					                        x: 115,
					                        y: 325,
					                        width: 80,
					                        listeners:{
						               			click:{
						               				fn:function(e){
						               					
							        					
						               					var regsBanco = NS.gridMovsCobranza.getSelectionModel().getSelections();
						    	   	        			var causa = (Ext.getCmp(PF+'txtCausaSCobranza').getValue());
						    	   	        			var cuenta =(Ext.getCmp(PF+'cmbCuentaContable2').getValue());
						        						
						    	   	        			var matBan = new Array();
							          					
							          					for(var i=0; i<regsBanco.length;i++){
					                						var vec = {};
					                						
					                						vec.importe = regsBanco[i].get('importe');
					            							vec.idDivisa = regsBanco[i].get('idDivisa');
					            							vec.referencia = regsBanco[i].get('referencia');
					            							vec.noEmpresa = regsBanco[i].get('noEmpresa');
					            							vec.nomEmpresa = regsBanco[i].get('nomEmpresa');
					            							vec.noDocto2 = regsBanco[i].get('noDocto2');
					            							vec.noFactura2 = regsBanco[i].get('noFactura2');
					            							vec.fecFactura = regsBanco[i].get('fecFactura');
					            							vec.fecValor = regsBanco[i].get('fecValor');
					            							vec.noCliente = regsBanco[i].get('noCliente');
					            							vec.concepto = regsBanco[i].get('concepto');
					            							vec.folioCob = regsBanco[i].get('folioCob');
					            							vec.secuencia = regsBanco[i].get('secuencia');
					            							vec.razonSoc = regsBanco[i].get('razonSoc');
					            							vec.status = regsBanco[i].get('estatus');
					            							vec.folioDetMov = regsBanco[i].get('folioDetMov');
					            							vec.importeSol = regsBanco[i].get('importeSol');
					            							
					            							matBan[i] = vec;
					                					}
					            							
					            						var jsonBan = Ext.util.JSON.encode(matBan);
					            						
							        					IdentificacionIngresosAction.concilVirSCobranza( jsonBan, ''+causa, ''+cuenta,function(resultado, e) {
							        						
							        						if(e.message=="Unable to connect to the server."){
							        							Ext.Msg.alert('SET','Error de conexión al servidor');
							        							return;
							        						}
							        						
								        					if (resultado.error != '') {
								        						Ext.Msg.alert('SET', resultado.error );
								        						return;
								        					}else{
								        						Ext.Msg.alert('SET', resultado.mensaje );
								        						NS.buscar();
								        						NS.winConciliacionVirtualSCobranza.hide();
								        						
								  
								        						
								        						return;
								        					}
							        						
							        					});
							        					
							        			
						        					
						           			   		
						               				
						               				}
						    			   	}
					                    }
					                    }	
						            ]
						}
	   	        
	   	        
	   	        
	   	        ],listeners:{
	   	        	show:{
	   	        		
	   	        		fn:function(){
	   	        			var regsCobranza = NS.gridMovsCobranza.getSelectionModel().getSelections();
	   	        			
	    					Ext.getCmp(PF + 'ImporteSCobranza').setText(regsCobranza[0].get('importe'));
	    					Ext.getCmp(PF + 'txtEmpresaSCobranza').setValue(regsCobranza[0].get('nomEmpresa'));
	    					Ext.getCmp(PF + 'txtClienteSCobranza').setValue(regsCobranza[0].get('razonSoc'));
	    					
	    					}
	   	        },
   	        	hide:{
   	        		fn:function(){
   	        			Ext.getCmp(PF + 'txtCausaSCobranza').setValue('');
   	        			Ext.getCmp(PF + 'cmbCuentaContable2').setValue('');
   	        			Ext.getCmp(PF + 'cmbCuentaContable2').reset();
    					Ext.getCmp(PF + 'txtDiferencia').setValue('');
    					Ext.getCmp(PF + 'txtMonto').setValue('');
    					Ext.getCmp(PF + 'txtMonto2').setValue('');
    					Ext.getCmp(PF + 'txtRegistros').setValue('');
    					Ext.getCmp(PF + 'txtRegistros2').setValue('');
	        			
	        		}
	        
	   	}	        		
	   	        	
	   	        				
	   	        			

	   	        	
	   	        }      	
		});
	
	
	
	
////****************************************ventana de actualizacion**************

	NS.winActualización = new Ext.Window({
		title: 'Configuración de tiempo de actualización',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 360,
	   	height: 155,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    draggable: false,
	    resizable: false,
	   	items: [
	   	        {
	   	        	xtype: 'label',
				    text: 'Indique tiempo de actualización, no mayor a 180 segundos',
					id: PF + 'lbleyenda',
					x: 35,
					y: 10
				},{
		        	xtype: 'textfield',
		        	x: 100,
		        	y: 45,
		        	width: 50,
		        	name: PF+'txtTiempo',
		        	id: PF+'txtTiempo',
		        	hidden: false,
		        	disabled: false,
		        	listeners: {
		        		
		        				}
				 },{
		   	        	xtype: 'label',
					    text: 'segundos',
						id: PF + 'lbseg',
						x: 175,
						y: 45
					},{
                        xtype: 'button',
                        id: PF+'cmdCerrarActualización',
                        name: PF+'cmdCerrarActualización',
                        text: 'Cerrar',
                        x: 220,
                        y: 90,
                        width: 80,
                        listeners:{
	               			click:{
	           			   		fn:function(e){
	           			   		NS.winActualización.hide();
	           			   		}
	        			   	}
	    			   	}
                    },{
                        xtype: 'button',
                        id: PF+'cmdAceptarActualización',
                        name: PF+'cmdAceptarActualización',
                        text: 'Aceptar',
                        x: 50,
                        y: 90,
                        width: 80,
                        listeners:{
	               			click:{
	           			   		fn:function(e){
	           			   					var tiempo = NS.unformatNumber(Ext.getCmp(PF+'txtTiempo').getValue());
	           			   				
	           			   				NS.id= true;
	           			   					if (tiempo>=0 && tiempo<=180)
	           			   						{
		           			   						NS.tiempoCon=0;
					           			   			NS.tiempoCon=(tiempo*1000)
					           			   			
					           			   			actualizarGrid = function() {
					           			   				NS.buscar();
					           			   			}
					           			   			
					           			   		if(NS.id==true )
							           			   		{	
							           			   		setTimeout("actualizarGrid()",NS.tiempoCon);
							           			   			NS.winActualización.hide();
			           			   						}
	           			   						}
	           			   					if (tiempo<0){
			           			 					BFwrk.Util.msgShow('Error al elegir tiempo ','ERROR');
				           			 				Ext.getCmp(PF + 'txtTiempo').setValue('');
			           			 					return;
	           			   					}
	           			   					if (tiempo>180){
			           			 					BFwrk.Util.msgShow('Tiempo máximo de acutalización es de 180 seg. ','ERROR');
				           			 				Ext.getCmp(PF + 'txtTiempo').setValue('');
			           			 					return;
	           			   					}
	           			   			}
                    						
	               			     }
	    			   			 }
                    },{
                        xtype: 'button',
                        id: PF+'cmdPararActualización',
                        name: PF+'cmdPararActualización',
                        text: 'Detener',
                        x: 135,
                        y: 90,
                        width: 80,
                        listeners:{
	               			click:{
	           			   		fn:function(e){	
	           			   	NS.id= false;	
	           			   	NS.winActualización.hide();
	           			   		return;
	           			   		}
	        			   	}
	    			   	}
                    },
					
	   	        
	   	        
	   	        ],
	   	        listeners:{
	   	        	hide:{
	   	        		fn:function(){
	   	   	        		
			   	        			Ext.getCmp(PF + 'txtTiempo').setValue('');
			   	        			Ext.getCmp(PF + 'txtTiempo').reset();
	   	        					}
   	        		
	        			}
	   	        	}  
	});
	   	        
	//**********************************************************************************************************
	NS.winParcializar = new Ext.Window({
			title: 'Parcializar Ingreso',
			modal: true,
			shadow: true,
			//closable: false,
			closeAction: 'hide',
			width: 520,
		   	height: 350,
		   	layout: 'absolute',
		   	plain: true,
		    bodyStyle: 'padding:10px;',
		    buttonAlign: 'center',
		    draggable: false,
		    resizable: false,
		   	items: [
		   	        {
		   	        	xtype: 'fieldset',
		                title: 'Cliente',
		                width: 480,
		                height: 100,
		                x: 10,
		                y: 10,
		                layout: 'absolute',
		                id: PF + 'fieldClientePar',
		                items: [
								{
								    xtype: 'label',
								    text: '',
								    id: PF + 'lblCliente',
								    x: 10,
								    y: 10
								}
						]
		   	        },{
                        xtype: 'label',
                        text: 'Importe Cobranza:',
                        x: 20,
                        y: 130
                    },{
			        	xtype: 'textfield',
			        	x: 140,
			        	y: 130,
			        	width: 150,
			        	name: PF+'txtImporteCobranza',
			        	id: PF+'txtImporteCobranza',
			        	hidden: false,
			        	disabled: true,
			        	listeners: {
			        		change: {
			        			fn:function(caja) {
			        				
			        			}
			        		}
			        	}
			        },{
                        xtype: 'label',
                        text: 'Importe Banco:',
                        x: 20,
                        y: 170
                    },{
			        	xtype: 'textfield',
			        	x: 140,
			        	y: 170,
			        	width: 150,
			        	name: PF+'txtImporteBanco',
			        	id: PF+'txtImporteBanco',
			        	hidden: false,
			        	disabled: false,
			        	listeners: {
			        		blur: {
			        			fn:function(caja) {
			        				var monto = NS.unformatNumber(Ext.getCmp(PF+'txtImporteCobranza').getValue());
			   	        			var monto2 = NS.unformatNumber(Ext.getCmp(PF+'txtImporteBanco').getValue());
			        				//alert(parseFloat(NS.unformatNumber(Ext.getCmp(PF+'txtMonto2').getValue())) +">" + parseFloat(monto2));

			   	        			if(parseFloat(NS.unformatNumber(Ext.getCmp(PF+'txtMonto2').getValue())) < parseFloat(monto2)){
	        							Ext.Msg.alert('SET','El importe capturado es mayor al importe original del deposito.');
	        							Ext.getCmp(PF+'txtImporteBanco').setValue(Ext.getCmp(PF+'txtMonto2').getValue());
	        							return;
			   	        			}
			   	        			
			   	        			if(parseFloat(NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue())) < parseFloat(monto2)){
	        							Ext.Msg.alert('SET','El importe capturado es mayor al importe de la factura.');
	        							Ext.getCmp(PF+'txtImporteBanco').setValue(Ext.getCmp(PF+'txtMonto2').getValue());
	        							return;
			   	        			}
			        				/*Ext.getCmp(PF + 'lblCliente').setText(regsBanco[0].get('razonSoc'))
			        				
			   	        			Ext.getCmp(PF+'txtImporteBanco').setValue(monto);
			   	        			Ext.getCmp(PF+'txtImporteCobranza').setValue(Ext.getCmp(PF+'txtMonto').getValue());*/
			   	        			
			   	        			
			   	        			var diferencia = (Math.round(parseFloat(monto-monto2)*100)/100);
			   	        			/*var band=0;
			   	        			if(diferencia > 0){
			   	        				Ext.getCmp(PF + 'lblParcializar').setText('Importe Restante de Cobranza:')
			   	        				band = 1;
			   	        			}*/
			   	        			if(diferencia<0){
			   	        				diferencia = (Math.round(parseFloat(monto2-monto)*100)/100);
			   	        				Ext.getCmp(PF + 'lblParcializar').setText('Importe Restante de Banco:')
			   	        				band = 2;
			   	        			}
			   	        			//NS.bandera= band;
			   						Ext.getCmp(PF+'txtParcializar').setValue(NS.formatNumber(diferencia));
			        			}
			        		}
			        	}
			        },{
                        xtype: 'label',
                        text: '______________________________________________________________________',
                        x: 20,
                        y: 200
                    },{
                        xtype: 'label',
                        text: 'Importe Restante de la factura:',
                        id: PF + 'lblParcializar',
                        x: 20,
                        y: 230
                    },{
			        	xtype: 'textfield',
			        	x: 180,
			        	y: 230,
			        	width: 150,
			        	name: PF+'txtParcializar',
			        	id: PF+'txtParcializar',
			        	hidden: false,
			        	disabled: true,
			        	listeners: {
			        		change: {
			        			fn:function(caja) {
			        				
			        			}
			        		}
			        	}
			        },{
                        xtype: 'button',
                        id: PF+'cmdAceptarParcializar',
                        name: PF+'cmdAceptarParcializar',
                        text: 'Aceptar',
                        x: 100,
                        y: 280,
                        width: 80,
                        listeners:{
	               			click:{
	           			   		fn:function(e){	           			   			
		           			   		var regsBanco = NS.gridMovsCobranza.getSelectionModel().getSelections();
			        				var regsCobranza = NS.gridMovsBanco.getSelectionModel().getSelections();
			        				var diferencia = NS.unformatNumber(Ext.getCmp(PF+'txtParcializar').getValue());
			        				
			        				if(diferencia > 0 ){
			        					//if(NS.bandera == 1){				        					
				        					var matBan = new Array();
				          					
				          					for(var i=0; i<regsBanco.length;i++){
		                						var vec = {};
		                						
		                						vec.importe = regsBanco[i].get('importe');
		            							vec.idDivisa = regsBanco[i].get('idDivisa');
		            							vec.referencia = regsBanco[i].get('referencia');
		            							vec.noEmpresa = regsBanco[i].get('noEmpresa');
		            							vec.nomEmpresa = regsBanco[i].get('nomEmpresa');
		            							vec.noDocto2 = regsBanco[i].get('noDocto2');
		            							vec.noFactura2 = regsBanco[i].get('noFactura2');
		            							vec.fecFactura = regsBanco[i].get('fecFactura');
		            							vec.fecValor = regsBanco[i].get('fecValor');
		            							vec.noCliente = regsBanco[i].get('noCliente');
		            							vec.concepto = regsBanco[i].get('concepto');
		            							vec.folioCob = regsBanco[i].get('folioCob');
		            							vec.secuencia = regsBanco[i].get('secuencia');
		            							vec.razonSoc = regsBanco[i].get('razonSoc');
		            							vec.status = regsBanco[i].get('estatus');
		            							vec.folioDetMov = regsBanco[i].get('folioDetMov');
		            							vec.importeSol = regsBanco[i].get('importeSol');
		            							
		            							matBan[i] = vec;
		                					}
		            							
		            						var jsonBan = Ext.util.JSON.encode(matBan);
		            						//NS.folioCobranza = vec.folioCob;
				        					IdentificacionIngresosAction.parcializarBancos(jsonBan, ''+diferencia, function(resultado, e) {
				        						
				        						if(e.message=="Unable to connect to the server."){
				        							Ext.Msg.alert('SET','Error de conexión al servidor');
				        							return;
				        						}
				        						
					        					if (resultado.error != '') {
					        						Ext.Msg.alert('SET', resultado.error );
					        						return;
					        					} else {
					        						Ext.Msg.alert('SET', resultado.mensaje );
					        						NS.buscar();
					        						NS.winParcializar.hide();
					        						return;
					        					}
				        						
				        					});
				        					
				        				
			        					//}
			        					/*if(NS.bandera == 2){

			        						
		            						var matCob = new Array();
				          					
				          					for(var i=0; i<regsCobranza.length;i++){
		                						var vec = {};
		                						
		                						vec.nomEmpresa = regsCobranza[i].get('nomEmpresa');
		                						vec.noEmpresa = regsCobranza[i].get('noEmpresa');
		            							vec.idBanco = regsCobranza[i].get('idBanco');
		            							vec.descBanco = regsCobranza[i].get('descBanco');
		            							vec.noCuenta= regsCobranza[i].get('noCuenta');
		            							vec.importe = regsCobranza[i].get('importe');
		            							vec.fecOperacion = regsCobranza[i].get('fecOperacion');
		            							vec.fecValor = regsCobranza[i].get('fecValor');
		            							vec.idDivisa = regsCobranza[i].get('idDivisa');
		            							vec.referencia = regsCobranza[i].get('referencia');
		            							vec.concepto= regsCobranza[i].get('concepto');
		            							vec.observacion = regsCobranza[i].get('observacion');
		            							vec.descripcion = regsCobranza[i].get('descripcion');
		            							vec.idTipoOperacion= regsCobranza[i].get('idTipoOperacion');
		            							vec.noFolioDet = regsCobranza[i].get('noFolioDet');
		            							vec.noCliente = regsCobranza[i].get('noCliente');
		            							vec.noDocto = regsCobranza[i].get('noDocto');
		            							vec.idChequera = regsCobranza[i].get('idChequera');
		            							vec.idEstatusMov = regsCobranza[i].get('idEstatusMov');
		            							vec.noFolioMov = regsCobranza[i].get('noFolioMov');
		            							vec.idFormaPago = regsCobranza[i].get('idFormaPago'); 
		            							vec.idCaja = regsCobranza[i].get('idCaja');
		            							vec.idBancoBenef = regsCobranza[i].get('idBancoBenef');
		            							vec.idRubro = regsCobranza[i].get('idRubro'); 
		            							vec.division = regsCobranza[i].get('division');
		            							
		                						
		            							matCob[i] = vec;
		                					}
		            							
		            						var jsonCob = Ext.util.JSON.encode(matCob);
		            						
				        					IdentificacionIngresosAction.parcializarBancos2( jsonCob, ''+diferencia, function(resultado, e) {
				        						
				        						if(e.message=="Unable to connect to the server."){
				        							Ext.Msg.alert('SET','Error de conexión al servidor');
				        							return;
				        						}
				        						
					        					if (resultado.error != '') {
					        						Ext.Msg.alert('SET', resultado.error );
					        						return;
					        					}else{
					        						Ext.Msg.alert('SET', resultado.mensaje );
					        						NS.buscar();
					        						NS.winParcializar.hide();
					        						
					  
					        						
					        						return;
					        					}
				        						
				        					});
				        					
				        			
			        					}*/
			        					
			        					
			        					
			        				}else if(diferencia < 0){
			        					
			        					Ext.Msg.alert('SET','guardando bancos ');
			        					
			        					//IdentificacionIngresosAction.parcializarBancos(jsonBan, jsonCob, ''+diferencia, function(resultado, e) {
			        						
			        						if(e.message=="Unable to connect to the server."){
			        							Ext.Msg.alert('SET','Error de conexión al servidor');
			        							return;
			        						}
			        						
				        					if (resultado.error != '') {
				        						Ext.Msg.alert('SET', resultado.error );
				        						return;
				        					} else {
				        						Ext.Msg.alert('SET', resultado.mensaje );
				        						NS.buscar();
				        						NS.winParcializar.hide();
				        						return;
				        					}	        					
			        				} else {
			        					Ext.Msg.alert('SET', 'Para parcializar la diferencia debe ser diferente 0.');
			        					return;
			        				}
	           			   		}
	        			   	}
	    			   	}
                    },{
                        xtype: 'button',
                        id: PF+'cmdCerrarParcializar',
                        name: PF+'cmdCerrarParcializar',
                        text: 'Cerrar',
                        x: 200,
                        y: 280,
                        width: 80,
                        listeners:{
	               			click:{
	           			   		fn:function(e){
	           			   			NS.winParcializar.hide();
	           			   		}
	        			   	}
	    			   	}
                    },
			        
		   	        ],
		   	        listeners:{
		   	        	show:{		   	        		
		   	        		fn:function(){       			
		   	        			
		   	        			var monto = NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
		   	        			var monto2 = NS.unformatNumber(Ext.getCmp(PF+'txtMonto2').getValue());
		   	        			
		   	        			
		   	        			var regsBanco = NS.gridMovsCobranza.getSelectionModel().getSelections();
		        				var regsCobranza = NS.gridMovsBanco.getSelectionModel().getSelections();
		        				
		        				
		        				
		        				//CLIENTE
		        				Ext.getCmp(PF + 'lblCliente').setText(regsBanco[0].get('razonSoc'));
		        				
		   	        			Ext.getCmp(PF+'txtImporteBanco').setValue(Ext.getCmp(PF+'txtMonto2').getValue());
		   	        			Ext.getCmp(PF+'txtImporteCobranza').setValue(Ext.getCmp(PF+'txtMonto').getValue());
		   	        			
		   	        			
		   	        			var diferencia = (Math.round(parseFloat(monto-monto2)*100)/100);
		   	        			var band=0;
		   	        			if(diferencia>0){
		   	        				Ext.getCmp(PF + 'lblParcializar').setText('Importe Restante de Cobranza:')
		   	        				band = 1;
		   	        			}
		   	        			if(diferencia<0){
		   	        				diferencia = (Math.round(parseFloat(monto2-monto)*100)/100);
		   	        				Ext.getCmp(PF + 'lblParcializar').setText('Importe Restante de Banco:')
		   	        				band = 2;
		   	        			}
		   	        			NS.bandera= band;
		   						Ext.getCmp(PF+'txtParcializar').setValue(NS.formatNumber(diferencia));
		   						
		   	        		}
		   	        	}
		   	        }	
			});

	    //FIN VENTANA CATÁLOGO COLORES.
	
/***********************************************************************************************************/
	NS.smClientes = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	NS.storeClientes = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noCliente: ''	
		},
		root: '',
		paramOrder:['noCliente'],
		directFn: IdentificacionIngresosAction.obtenerClientes,
		
		fields: [		         
				{name: 'equivalePersona'},					
				{name: 'razonSocial'}	
				],	
		listeners: {
			load: function(s, records ){
				if(records.length == 0){
					BFwrk.Util.msgShow('No se encontraron clientes.','INFO');
				}
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeClientes, msg:"Buscando..."});
			}
		},
		
	}); 
	
	NS.gridClientes  = new Ext.grid.GridPanel({
		store : NS.storeClientes,
		id: PF + 'gridClientes',
		name: PF + 'gridClientes',
		width: 440,
       	height: 150,
		x :0,
		y :50,
		frame: true,
		title :'Clientes',
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
		columns : 
			[ 
			 	NS.smClientes,
		       {
					header :'Clave',
					width :100,
			 		css: 'text-align:center;',
					dataIndex :'equivalePersona'
				},{
			 		header :'Nombre',
			 		width: 280,
			 		dataIndex :'razonSocial'
		        }
			],
			
		}),		
		sm: NS.smClientes			
	});
	
	NS.winAnticipo = new Ext.Window({
		title: 'Enviar Anticipo',
		modal: true,
		shadow: true,
		closeAction: 'hide',
		width: 480,
	   	height: 320,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    draggable: false,
	    resizable: false,
	   	items: [{
			   		xtype: 'label',
					text: 'Importe',
	                id: PF + 'datosDeposito',
	                name: PF + 'datosDeposito',
					x: 10,
					y: 10
	   			},{
	   	        	xtype: 'fieldset',
	                title: '',
	                width: 445,
	                height: 220,
	                x: 10,
	                y: 40,
	                layout: 'absolute',
	                id: PF + 'fieldClienteAnticipo',
	                items: [{
                				xtype: 'label',
								text: 'Clave o Nombre del cliente: ',
								x: 10,
								y: 0
							},{
		                		xtype: 'textfield',
								text: '',
								x: 10,
								y: 20,
					        	width: 300,
					        	name: PF+'txtCliente',
					        	id: PF+'txtCliente'
							},{
		                		xtype: 'button',
								text: 'Buscar',
								x: 330,
								y: 20,
								width: 80,
			                    listeners:{
			               			click:{
			           			   		fn:function(e){	
			           			   			var clienteBusqueda = Ext.getCmp(PF + 'txtCliente').getValue();
			           			   			
			           			   			if (clienteBusqueda != "") {
			           			   				NS.storeClientes.baseParams.noCliente = clienteBusqueda;
			           			   				NS.storeClientes.load();
			           			   			} else {
			           							BFwrk.Util.msgShow('Debe agregar datos al campo de búsqueda.','INFO');
			           			   			}
			           			   		}
			        			   	}
			    			   	}
							},NS.gridClientes]
	   	        },{
                    xtype: 'button',
                    id: PF + 'cmdAceptarAnticipo',
                    name: PF + 'cmdAceptarAnticipo',
                    text: 'Aceptar',
                    x: 240,
                    y: 262,
                    width: 80,
                    listeners:{
               			click:{
           			   		fn:function(e){	
           			   			var folioDet = NS.depositoSeleccionado[0].get("noFolioDet");
           			   			var cliente = NS.gridClientes.getSelectionModel().getSelections()[0].get("equivalePersona");
           			   			var importe = parseFloat(NS.depositoSeleccionado[0].get("importe"));
	        					var divisa = NS.depositoSeleccionado[0].get("idDivisa");
	        					var noEmpresa = parseInt(NS.txtEmpresa.getValue());
	        					
	           			 		IdentificacionIngresosAction.enviarAnticipo(folioDet, cliente, importe, noEmpresa, divisa, function(res, e){
	           			    			if (res != null && res != undefined && res != "") {
		               			   			NS.winAnticipo.hide();
		               			   			NS.gridClientes.store.removeAll();
		               			   			Ext.getCmp(PF+'txtCliente').setValue("");
		           							NS.buscar();
		           							BFwrk.Util.msgShow(res, 'INFO');
	           			    			} else {
		           							BFwrk.Util.msgShow('Hubo algun error en la ejecución.', 'INFO');
	           			    			}
	           			    	});
           			   		}
        			   	}
    			   	}
                },{
                    xtype: 'button',
                    id: PF + 'cmdCerrarAnticipo',
                    name: PF + 'cmdCerrarAnticipo',
                    text: 'Cerrar',
                    x: 340,
                    y: 262,
                    width: 80,
                    listeners: {
               			click: {
           			   		fn: function(e) {
           			   			NS.winAnticipo.hide();
           			   			NS.gridClientes.store.removeAll();
           			   			Ext.getCmp(PF+'txtCliente').setValue("");
           			   		}
        			   	}
    			   	}
                }],
	   	        listeners: {
	   	        	show: {
	   	        		fn: function() {
	   	        			Ext.getCmp(PF + 'datosDeposito').setText("Importe: " + NS.depositoSeleccionado[0].get("importe")
	   	        					+ " Moneda: " + NS.depositoSeleccionado[0].get("idDivisa")
	   	        					+ " Banco: " + NS.depositoSeleccionado[0].get("descBanco")
	   	        					+ " Cuenta: " + NS.depositoSeleccionado[0].get("idChequera")
	   	        					+ " Concepto: " + NS.depositoSeleccionado[0].get("observacion"));
	   	        		}
	   	        	}
	   	        }	
		});
/***********************************************************************************************************/
	NS.dateIni = new Ext.form.DateField({
		x: 0,
		y: 20,
		width: 120,
		editable: false,
		hidden: false,
		listeners:{
	    	change:{
	    		fn:function(caja, valor){
	    			var fechaFin = NS.dateFin.getValue();
	    			if(fechaFin < caja.getValue() && fechaFin != '' && caja.getValue() != '')
	    			{
	    				BFwrk.Util.msgShow('La fecha inicial no puede ser mayor', 'ERROR');
	    				NS.dateFin.setValue('');
	    				return;
	    			}		
	    		}
	    	}
	    }
	});
	NS.dateFin = new Ext.form.DateField({
		x: 150,
		y: 20,
		width: 120,
		editable: false,
		hidden: false,
		listeners:{
	    	change:{
	    		fn:function(caja, valor){
	    			var fechaIni = NS.dateIni.getValue();
	    			if(fechaIni > caja.getValue() && fechaIni != '' && caja.getValue() != '')
	    			{
	    				BFwrk.Util.msgShow('La fecha inicial no puede ser mayor', 'ERROR');
	    				NS.dateFin.setValue('');
	    				return;
	    			} else {
	    				Ext.getCmp( PF + 'cmdAceptarExcel').setDisabled(false);
	    			}
	    		}
	    	}
	    }
	});
	NS.fielExcel = new Ext.form.FieldSet({
		title: '',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 360,
	   	height: 220,
	   	layout: 'absolute',
	   	plain: true,
	    //bodyStyle: 'padding:10px;',
	   // buttonAlign: 'center',
	    draggable: false,
	    resizable: false,
		items: [
				{
				    xtype: 'label',
				    text: 'Fecha inicio:',
				    x: 0,
				    y: 0
				},{
				    xtype: 'label',
				    text: 'Fecha fin:',
				    x: 150,
				    y: 0
				},NS.dateIni,
				NS.dateFin,{
					 xtype: 'fieldset',
                     title: '',
                     x: 0,
                     y: 46,
                     width: 300,
                     height: 90,
                     layout: 'absolute',
                     items: [
                             {
                            	xtype: 'radiogroup',
							 	name: PF + 'optReporte',
							 	id: PF + 'optReporte',
							 	columns: 3,
							 	hidden: false,
								x: 0,
								y: 0,
							   	items: [
							        {
							            xtype: 'radio',
							            boxLabel: 'Facturas pendientes.',
							         	name: 'optReporte',
							            inputValue: 0,
							            checked: true	                        
							        },
							        {
							            xtype: 'radio',
							            boxLabel: 'Depositos',
							         	name: 'optReporte',
							            inputValue: 1
							        },
							        {
							            xtype: 'radio',
							            boxLabel: 'Conciliados',
							         	name: 'optReporte',
							            inputValue: 2
							        }
							    ]}
                 ]
		}]
	});
//Ventana Excel 
	var winExcel = new Ext.Window({
		width: 360,
	   	height: 250,
		title: 'Reportes',
		modal:true,
		shadow:true,
		minWidth: 400,
		minHeight: 580,
		layout: 'fit',
		plain:true,
	    draggable: false,
	    resizable: false,
		bodyStyle:'padding:10px;',
		buttonAlign:'center',
		closable: false,
	   	items: [ NS.fielExcel ],
		buttons: [{
				    text: 'Aceptar',
					   handler:function(e){
									var myMask = new Ext.LoadMask(Ext.getBody(), { msg:"Obteniendo reporte..."});
				
				    			var fechaIni = NS.dateIni.getValue();
				    			var fechaFin = NS.dateFin.getValue();
				    			var empresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
				    			var reporte = Ext.getCmp(PF + 'optReporte').getValue().getGroupValue();
				    			switch (reporte) {
				        			case "0":
				        		   		NS.exportaExcel(empresa, 0, fechaIni, fechaFin);
				        				break;
				        			case "1":
				        		   		NS.exportaExcel(empresa, 1, fechaIni, fechaFin);
				        				break;
				        			case "2":
				        		   		NS.exportaExcel(empresa, 2, fechaIni, fechaFin);
				        				break;
				    			}
				   	}
				},{
				    text: 'Cerrar',
				    handler: function(e){
				    	NS.dateIni.setValue("");
				    	NS.dateFin.setValue("");
						winExcel.hide();
				    }}]
		});
	
	NS.exportaExcel = function(empresa, caso, fecIni, fecFin) {
		IdentificacionIngresosAction.exportaExcel(empresa, caso, fecIni, fecFin, function(res, e){
   			if (res != null && res != undefined) {
   				if (res == "") {
   	   				Ext.Msg.alert('SET', "No hay datos para generar el archivo.");
   				} else {
					parametros='?nomReporte=excelConciliaciones';
					parametros+='&nomParam1=nombre';
					parametros+='&valParam1=' + res;
					window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
	   			}	
   			} else {   				
   				Ext.Msg.alert('SET', "Error al generar el archivo.");
   			}
				Ext.getCmp(PF+'cmdAceptarExcel').setDisabled(false);
   		});
   		
   	};	
	
	/**********************************************************************************/
	NS.contenedorGlobal = new Ext.FormPanel({
	    title: 'Conciliación Banca Cobranza',
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
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 0,
	                        width: 975,
	                        height: 170,
	                        layout: 'absolute',
	                        items: [
	                            NS.lblEmpresa,
	                            NS.txtEmpresa,
	                            NS.cmbEmpresa,
	                            {
	                                xtype: 'fieldset',
	                                title: 'Selección',
	                                x: 1,
	                                y: 46,
	                                width: 200,
	                                height: 100,
	                                layout: 'absolute',
	                                items: [
	                                {
	                                	xtype: 'radiogroup',
				                     	name: PF + 'movimiento',
				                     	id: PF + 'movimiento',
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
				                                inputValue: 1
		                                    },
		                                    {
		                                        xtype: 'radio',
		                                        boxLabel: 'Mov. de Banco y Empresa',
		                                        name: PF+'optMov',
				                                inputValue: 2,
				                                checked: true
		                                    }
	                                    ],
	                                    listeners:{
	                        				change:{
	                        					fn:function(){
	                        			
	                        						var opcion = Ext.getCmp(PF + 'movimiento').getValue();
	                        						
	                        						if(opcion == null || opcion == undefined ){
	                        							return;
	                        						}else{
	                        							opcion = Ext.getCmp(PF + 'movimiento').getValue().getGroupValue();
	                        							
	                        							switch(parseInt(opcion)){
	                        							
	                        								case 0:
	                        									NS.optBusqueda = 0;
	                        									break;
	                        								case 1:
	                        									NS.optBusqueda = 1;
	                        									break;
	                        								case 2:
	                        									NS.optBusqueda = 2;
	                        									break;
	                        							}
	                        						}
	                        					}//End function
	                        				} //End change
	                        			} // End listeners
	                                    },
	                                    
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
	                            NS.cmbDivisa,
	                            {
								  	xtype: 'uppertextfield',
			                        x: 215,
			                        y: 100,
			                        width: 40,
			                        id: PF+'txtIdDivisa', 
			                        name:PF+'txtIdDivisa',
			                        value: '',
			                        hidden: true,
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor) {
				                    			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
				                    				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisa', NS.cmbDivisa.getId());
				                    			}else {
				                    				NS.cmbDivisa.reset();
				                    			}
			                        		}
			                        	}
			                        }
							  },{
	                                xtype: 'datefield',
	                                id: PF+'txtFechaIni',
	                                name: PF+'txtFechaIni',
	                                format: 'd/m/Y',
	                                hidden: true,
	                                x: 217,
	                                y: 100,
	                                width: 100,
	            		        	editable: false,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var fechaFin = Ext.getCmp(PF + 'txtFechaFin');
			                        			fechaFin.setDisabled(false);
			                        			var fechaFin = fechaFin.getValue();
	                                			if(fechaFin < caja.getValue() && fechaFin != '' && caja.getValue() != '')
	                                			{
	                                				BFwrk.Util.msgShow('La fecha inicial no puede ser mayor', 'ERROR');
		                	   	        			Ext.getCmp( PF+'txtFechaFin').setValue('');
	                                				return;
	                                			}
	                	   	        			Ext.getCmp( PF+'txtFechaFin').setValue('');	
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
	            		        	editable: false,
	            		        	disabled: true,
	                                width: 100,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var fechaIni = Ext.getCmp(PF + 'txtFechaIni').getValue();
	                                			if(fechaIni > caja.getValue() && fechaIni != '' && caja.getValue() != '')
	                                			{
	                                				BFwrk.Util.msgShow('La fecha inicial no puede ser mayor', 'ERROR');
	                                				Ext.getCmp(PF + 'txtFechaIni').setDisabled(true);
		                	   	        			Ext.getCmp( PF+'txtFechaFin').setValue('');
	                                				Ext.getCmp(PF + 'cmdAceptarExcel').setDisabled(true);
	                                				return;
	                                			} else {
	                             
	                                				Ext.getCmp(PF + 'txtFechaFin').setDisabled(true);
				                        			NS.agregarCriterioValor('FECHA', Ext.getCmp(PF + 'txtFechaIni').getId(), 'valor');
	                                				NS.agregarCriterioValor('FECHA', caja.getId(), 'valor');
	                                				Ext.getCmp(PF+'cmdAceptarExcel').setDisabled(false);
	                                			}
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
						        				if(parseInt(valorUno) > parseInt(valor)){
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
	                            },{
	                                xtype: 'button',
	                                id: PF+'cmdBuscar',
	                                name: PF+'cmdBuscar',
	                                text: 'Buscar',
	                                x: 860,
	                                y: 80,
	                                width: 80,
	                                listeners:{
				               			click:{
				           			   		fn:function(e){
				           						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMovimientosCobranza, msg:"Buscando..."});

					            			   	NS.buscar();
				           			   		}
				        			   	}
				    			   	}
	                            },{
	                                xtype: 'label',
	                                text: 'Divisa:',
	                                x: 780,
	                                y: 15
	                            },
	                            NS.cmbDivisa,
	                            NS.gridDatos
	                        ]
	                    },
	                    {
                            xtype: 'fieldset',
                            title: '',
                            x: 0,
                            y: 181,
                            width: 975,
                            height: 320,
                            layout: 'absolute',
                            items: [
									NS.gridMovsCobranza,
									NS.gridMovsBanco,
									
									{
		                                xtype: 'label',
		                                text: 'Registros:',
		                                x: 50,
		                                y: 260
		                            },{
		                                xtype: 'label',
		                                text: 'Registros:',
		                                x: 520,
		                                y: 260
		                            },{
							        	xtype: 'textfield',
							        	x: 580,
							        	y: 260,
							        	width: 50,
							        	name: PF+'txtRegistros',
							        	id: PF+'txtRegistros',
							        	hidden: false,
							        	disabled: true,
							        	listeners: {
							        		change: {
							        			fn:function(caja) {
							        				/*if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
							        				}else {
							        				}	
							        				*/
							        			}
							        		}
							        	}
							        },{
							        	xtype: 'textfield',
							        	x: 110,
							        	y: 260,
							        	width: 50,
							        	name: PF+'txtRegistros2',
							        	id: PF+'txtRegistros2',
							        	hidden: false,
							        	disabled: true,
							        	listeners: {
							        		change: {
							        			fn:function(caja) {
							        				/*if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
							        				}else {
							        				}	
							        				*/
							        			}
							        		}
							        	}
							        },{
		                                xtype: 'label',
		                                text: 'Monto:',
		                                x: 180,
		                                y: 260
		                            },{
		                                xtype: 'label',
		                                text: 'Monto:',
		                                x: 645,
		                                y: 260
		                            },{
							        	xtype: 'textfield',
							        	x: 690,
							        	y: 260,
							        	width: 120,
							        	name: PF+'txtMonto',
							        	id: PF+'txtMonto',
							        	hidden: false,
							        	disabled: true,
							        	listeners: {
							        		change: {
							        			fn:function(caja) {
							        				/*if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
							        				}else {
							        				}	
							        				*/
							        			}
							        		}
							        	}
							        },{
							        	xtype: 'textfield',
							        	x: 230,
							        	y: 260,
							        	width: 120,
							        	name: PF+'txtMonto2',
							        	id: PF+'txtMonto2',
							        	hidden: false,
							        	disabled: true,
							        	listeners: {
							        		change: {
							        			fn:function(caja) {
							        				/*if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
							        				}else {
							        				}	
							        				*/
							        			}
							        		}
							        	}
							        },
                                    ] //fin fieldset grids
	                    },{
                            xtype: 'label',
                            text: 'Diferencia:',
                            x: 20,
                            y: 520
                        },{
				        	xtype: 'textfield',
				        	x: 80,
				        	y: 520,
				        	width: 100,
				        	name: PF+'txtDiferencia',
				        	id: PF+'txtDiferencia',
				        	hidden: false,
				        	disabled: true
				        },{
				        	xtype: 'button',
				        	text: 'Reportes',
				        	x: 770,
				        	y: 90,
				        	width: 80,
				        	height: 22,
				        	id: PF+'btnExcel',
				        	name: PF+'btnExcel',
				        	hidden: false,
				        	disabled: false,
				        	listeners:{
				        		click:{
				        			fn:function(e){
				        				winExcel.show();
				        			}
				        		}
				        	}
				        },{
				        	xtype: 'button',
				        	text: 'Mandar a anticipo',
				        	x: 200,
				        	y: 520,
				        	width: 80,
				        	height: 22,
				        	id: PF + 'btnAnticipo',
				        	name: PF + 'btnAnticipo',
				        	hidden: false,
				        	disabled: true,
				        	listeners: {
				        		click: {
				        			fn: function(e) {
				        				NS.depositoSeleccionado = NS.gridMovsBanco.getSelectionModel().getSelections();
				        				if (NS.depositoSeleccionado.length > 0) {
				        					NS.winAnticipo.show(NS.depositoSeleccionado[0]);
				        				} else {
				   	        				Ext.Msg.alert('SET','Debe seleccionar un deposito para enviarlo a anticipo.');
				        				}				        				
				        			}
				        		}
				        	}				        			
				        },{				        			
				        	xtype: 'button',
				        	text: 'Parcializar factura',
				        	x: 700,
				        	y: 520,
				        	width: 80,
				        	height: 22,
				        	id: PF+'btnParcializar',
				        	name: PF+'btnParcializar',
				        	hidden: false,
				        	disabled:true,
				        	listeners:{
				        		click:{
				        			fn:function(e){
				   	        			var monto = NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
				   	        			var monto2 = NS.unformatNumber(Ext.getCmp(PF+'txtMonto2').getValue());
				   	        			
				   	        			if(isNaN(monto) || monto <= 0){
				   	        				Ext.Msg.alert('SET','Error al cargar el monto de la factura cobranza.');
				   	        				NS.winParcializar.hide();
				        					return;
				   	        			}
				   							
				   	        			if(isNaN(monto2) || monto2 <= 0){
				   	        				Ext.Msg.alert('SET','Error al cargar el monto de bancos.');
				   	        				NS.winParcializar.hide();
				        					return;
				   	        			}
				   	        			
				   	        			/*if (parseInt(monto2) > parseInt(monto)) {
				   	        				Ext.Msg.alert('SET','Para parcializar una factura, el importe debe ser mayor al deposito.');
				   	        				NS.winParcializar.hide();
				        					return;
				   	        			}*/
				   	        			
				   	        			var regsCobranza1 = NS.gridMovsCobranza.getSelectionModel().getSelections();
				        				var regsBanco1 = NS.gridMovsBanco.getSelectionModel().getSelections();
				        				
				        				if(regsBanco1.length > 1){
				        					Ext.Msg.alert('SET','Para parcializar se debe seleccionar solo un registro de bancos.');
				        					NS.winParcializar.hide();
				        					return;
				        				}
				        				
				        				if(regsCobranza1.length > 1){
				        					Ext.Msg.alert('SET','Para parcializar se debe seleccionar solo un registro de cobranza.');
				        					NS.winParcializar.hide();
				        					return;
				        				}				        				
				        				NS.winParcializar.show();
				        			}
				        		}
				        	}
				        },			        
				        {
				        	xtype: 'button',
				        	text: 'Confirmar',
				        	x: 805,
				        	y: 520,
				        	width: 80,
				        	height: 22,
				        	id: PF+'btnConfirmar',
				        	name: PF+'btnConfirmar',
				        	hidden: false,
				        	disabled:true,
				        	listeners:{
				        		click:{
				        			fn:function(e){
				        				
				        				var regsBanco = NS.gridMovsCobranza.getSelectionModel().getSelections();
				        				var regsCobranza = NS.gridMovsBanco.getSelectionModel().getSelections();
				        				
				        				if(regsBanco.length <= 0){
				        					Ext.Msg.alert('SET','Debe seleccionar al menos un registro de cobranza ');
				        					return;
				        				}
				        				
				        				var tmpMonto = Ext.getCmp(PF + 'txtMonto').getValue();
				        				
				        				if(regsCobranza.length <= 0){
				        					Ext.Msg.alert('SET','Debe seleccionar al menos un registro de banco ');
				        					return;
				        				}
				        				
				        				/*if (NS.colorCobranza!='A' && NS.colorBanco!='A'){
				        					Ext.Msg.alert('SET','Para conciliar debe de ser azul ');
				        					return;
				        				}*/
				        				var tmpMonto2 = Ext.getCmp(PF + 'txtMonto2').getValue();
				        				var fechaSplit = (regsCobranza[0].get("fecValor")).split("/");
				        				var fecDepOk=fechaSplit[1]+"/"+fechaSplit[0]+"/"+fechaSplit[2];

				        				var fechaDeposito = new Date(fecDepOk);
				        				//var fechaDeposito = new Date(.split('/'));
				        				for(var k = 0; regsBanco.length > k; k++) {
					        				var fechaSplit2 = (regsBanco[k].get("fecFactura")).split("/");
					        				var fecFactOk=fechaSplit2[1]+"/"+fechaSplit2[0]+"/"+fechaSplit2[2];
					        				var fechaFactura = new Date(fecFactOk);
				        					if (fechaFactura > fechaDeposito){
				        						Ext.Msg.alert('SET','>>>>Una o varias de las facturas seleccionadas tienen fecha mayor al deposito.\n Seleccione solo las de fecha menor.');
				        						return;
				        					}
				        				}
				        				
				        				
				        				Ext.Msg.confirm('SET', '¿Esta seguro de confirmar </br>' + regsCobranza.length 
				        						+' Depósito(s) ('+tmpMonto2+') a '+  regsBanco.length
				        						+' Movimiento(s) de Cobranza por ('+tmpMonto+')?', function(btn){
				        					
				        					if (btn === 'yes'){	
						        				var diferencia = NS.unformatNumber(Ext.getCmp(PF + 'txtDiferencia').getValue());

						        				var tmpMonto = parseFloat(Ext.getCmp(PF + 'txtMonto').getValue().replace(/,/g, ""));
						        				var tmpMonto2 = parseFloat(Ext.getCmp(PF + 'txtMonto2').getValue().replace(/,/g, ""));
						        				
						        				if (tmpMonto2 >= tmpMonto)  {
						        					/*** 	JSON PARA BANCOS	***/
						        					var matBan = new Array();
		        		          					
		        		          					for(var i=0; i<regsBanco.length;i++){
				                						var vec = {};
				                						
				                						vec.importe = regsBanco[i].get('importe');
				            							vec.idDivisa = regsBanco[i].get('idDivisa');
				            							vec.referencia = regsBanco[i].get('referencia');
				            							vec.noEmpresa = regsBanco[i].get('noEmpresa');
				            							vec.nomEmpresa = regsBanco[i].get('nomEmpresa');
				            							vec.noDocto2 = regsBanco[i].get('noDocto2');
				            							vec.noFactura2 = regsBanco[i].get('noFactura2');
				            							vec.fecFactura = regsBanco[i].get('fecFactura');
				            							vec.fecValor = regsBanco[i].get('fecValor');
				            							vec.noCliente = regsBanco[i].get('noCliente');
				            							vec.concepto = regsBanco[i].get('concepto');
				            							vec.folioCob = regsBanco[i].get('folioCob');
				            							vec.secuencia = regsBanco[i].get('secuencia');
				            							vec.razonSoc = regsBanco[i].get('razonSoc');
				            							vec.status = regsBanco[i].get('estatus');
				            							vec.folioDetMov = regsBanco[i].get('folioDetMov');
				            							vec.importeSol = regsBanco[i].get('importeSol');
				            							
				            							
				            							
				            							matBan[i] = vec;
				                					}
				            							
				            						var jsonBan = Ext.util.JSON.encode(matBan);
				            						/******************************************/
				            						
				            						/*** 	JSON PARA COBRANZA	***/
				            						var matCob = new Array();
		        		          					
		        		          					for(var i=0; i<regsCobranza.length;i++){
				                						var vec = {};
				                					
				                						vec.nomEmpresa = regsCobranza[i].get('nomEmpresa');
				                						vec.noEmpresa = regsCobranza[i].get('noEmpresa');
				            							vec.idBanco = regsCobranza[i].get('idBanco');
				            							vec.descBanco = regsCobranza[i].get('descBanco');
				            							vec.noCuenta= regsCobranza[i].get('noCuenta');
				            							vec.importe = regsCobranza[i].get('importe');
				            							vec.fecOperacion = regsCobranza[i].get('fecOperacion');
				            							vec.fecValor = regsCobranza[i].get('fecValor');
				            							vec.idDivisa = regsCobranza[i].get('idDivisa');
				            							vec.referencia = regsCobranza[i].get('referencia');
				            							vec.concepto= regsCobranza[i].get('concepto');
				            							vec.observacion = regsCobranza[i].get('observacion');
				            							vec.descripcion = regsCobranza[i].get('descripcion');
				            							vec.idTipoOperacion= regsCobranza[i].get('idTipoOperacion');
				            							vec.noFolioDet = regsCobranza[i].get('noFolioDet');
				            							vec.noCliente = regsCobranza[i].get('noCliente');
				            							vec.noDocto = regsCobranza[i].get('noDocto');
				            							vec.idChequera = regsCobranza[i].get('idChequera');
				            							vec.idEstatusMov = regsCobranza[i].get('idEstatusMov');
				            							vec.noFolioMov = regsCobranza[i].get('noFolioMov');
				            							vec.idFormaPago = regsCobranza[i].get('idFormaPago'); 
				            							vec.idCaja = regsCobranza[i].get('idCaja');
				            							vec.idBancoBenef = regsCobranza[i].get('idBancoBenef');
				            							vec.idRubro = regsCobranza[i].get('idRubro'); 
				            							vec.division = regsCobranza[i].get('division');
				            							
				            							matCob[i] = vec;
				                					}
				            							
				            						var jsonCob = Ext.util.JSON.encode(matCob);
				            						/******************************************/
						        					
						        					IdentificacionIngresosAction.conciliaBancosEmpresa(jsonBan, jsonCob, function(resultado, e) {
						        						
						        						if(e.message=="Unable to connect to the server."){
						        							Ext.Msg.alert('SET','Error de conexión al servidor');
						        							return;
						        						}
						        						
							        					if (resultado.error != '') {
							        						Ext.Msg.alert('SET', resultado.error );
							        						return;
							        					}else{
							        						Ext.Msg.alert('SET', resultado.mensaje );
							        						NS.buscar();
							        						return;
							        					}
						        						
						        					});
						        				}else{
						        					Ext.Msg.alert('SET','El monto total de facturas debe de ser menor al del deposito seleccionado.');
						        					return;
						        				}
						        				
				        					}
				        					
				        				}); //fin confirm
				        				
				        				
				        				
				        			}
				        		}
				        	}
				        },{
				        	xtype: 'button',
				        	text: 'Limpiar',
				        	x: 890,
				        	y: 520,
				        	width: 80,
				        	height: 22,
				        	id: PF+'btnLimpiar',
				        	name: PF+'btnLimpiar',
				        	hidden: false,
				        	listeners:{
				        		click:{
				        			fn:function(e){
				    					NS.gridDatos.store.removeAll();
				    					NS.gridMovsCobranza.store.removeAll();
				    					NS.gridMovsBanco.store.removeAll();
				    					NS.cmbCriterio.setValue('');
				    					NS.cmbCriterio.setDisabled(false);
				    					NS.cmbBanco.setVisible(false);
				    					NS.cmbChequera.setVisible(false);
				    					NS.cmbDivisa.setValue('');

				    					Ext.getCmp(PF + 'txtFechaFin').setVisible(false);						
				    					Ext.getCmp(PF + 'txtFechaIni').setVisible(false);
				    					Ext.getCmp(PF + 'txtMontoIni').setVisible(false);
										Ext.getCmp(PF + 'txtIdDivisa').setVisible(false);
				    					Ext.getCmp(PF + 'txtMontoFin').setVisible(false);
				    					Ext.getCmp(PF + 'txtDiferencia').setValue('');
				    					Ext.getCmp(PF + 'txtMonto').setValue('');
				    					Ext.getCmp(PF + 'txtMonto2').setValue('');
				    					Ext.getCmp(PF + 'txtRegistros').setValue('');
				    					Ext.getCmp(PF + 'txtRegistros2').setValue('');
				   	        			
				    					//Ext.getCmp(PF + 'btnActPantalla').setDisabled(true);
				    					Ext.getCmp(PF + 'btnConfirmar').setDisabled(true);
				    					//Ext.getCmp(PF + 'btnConVir').setDisabled(true);
				    					//Ext.getCmp(PF + 'btnConVirB&C').setDisabled(true);
				    					Ext.getCmp(PF + 'btnParcializar').setDisabled(true);
				    					Ext.getCmp(PF + 'btnAnticipo').setDisabled(true);
				    					//Ext.getCmp(PF + 'btnExcel').setDisabled(true);
				        			}
				        		}
				        	}
				        },
				        
	                   
	                ] //fin fieldset global
	            }
	        ] //fin formpanel
	});	
	NS.contenedorGlobal.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});