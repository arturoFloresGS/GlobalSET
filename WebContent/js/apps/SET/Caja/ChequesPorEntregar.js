/*
@author: Jessica Arelly Cruz Cruz
@since: 26/07/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Caja.ChequesPorEntregar');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	NS.GI_USUARIO = apps.SET.iUserId;
	NS.GI_ID_CAJA = apps.SET.ID_CAJA;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.fechaHoy = apps.SET.FEC_HOY;
	
	NS.fechaIni = '';
	NS.fechaFin = '';
	NS.empresaSup = NS.GI_ID_EMPRESA;
	NS.empresaInf = NS.GI_ID_EMPRESA;
	NS.cajaInf = 0;
	NS.cajaSup = 30000;
	NS.bancoInf = 0;
	NS.bancoSup = 32000;
	NS.chequera = '%25';
	NS.divisa = '';
	NS.nomEmpresa = NS.GI_NOM_EMPRESA;
	NS.subtitulo = '';
	NS.descDivisa = '';
//	if(NS.cmbEmpresaVentana)
//		NS.cmbEmpresaVentana.destroy();
	
		//store empresa
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{usuario : NS.GI_USUARIO},
		paramOrder:['usuario'],
		directFn: CajaAction.llenarComboEmpresas,
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				//Se agrega la opcion todas las empresas
	 			var recStoreEmpresas = NS.storeCmbEmpresa.recordType;	
				var todas = new recStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '*************** TODAS ***************'
		       	});
		   		NS.storeCmbEmpresa.insert(0,todas);
			}
		}
	}); 
	
	
	//combo Empresas coinversoras
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
        ,width: 340
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: NS.GI_NOM_EMPRESA
		,visible: false
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
	
	//store banco
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			empresa: NS.GI_ID_EMPRESA 
		},
		root: '',
		paramOrder:['empresa'],
		directFn: CajaAction.llenarComboBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					BFwrk.Util.msgShow('No hay bancos asignados a la empresa seleccionada', 'INFO');
					return;
				}
			}
		}
	}); 
	
	//combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 56
        ,y: 105
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	
	//store divisa
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			cnt: 2 
		},
		root: '',
		paramOrder:['cnt'],
		directFn: CajaAction.llenarComboDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo divisa
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa
		,name: PF+'cmbDivisa'
		,id: PF+'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 56
        ,y: 105
        ,width: 170
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione una Divisa'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbDivisa(combo.getValue());
				}
			}
		}
	});
	
	//store chequera
	NS.storeChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			banco: 0,
			empresa: 0
		},
		root: '',
		paramOrder:['banco','empresa'],
		directFn: CajaAction.llenarComboChequeras, 
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
	
	//combo divisa
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera
		,name: PF+'cmbChequera'
		,id: PF+'cmbChequera'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 56
        ,y: 105
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una Chequera'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbChequera(combo.getValue());
				}
			}
		}
	});
	
	NS.dataCriterio = [
					  [ '0', 'BANCO' ]
				     ,[ '1', 'CHEQUERA' ]
				     ,[ '2', 'CONCEPTO' ]
				     ,[ '3', 'DIVISA' ]
				     ,[ '4', 'FECHA' ]
				     ,[ '5', 'MONTOS' ]
				     ,[ '6', 'BENEFICIARIO' ]
				     ,[ '7', 'No. CHEQUE' ]
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
		,x: 58
		,y: 55
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
					combo.setDisabled(true);
					var tamGrid=(NS.storeDatos.data.items).length;
					var datosClase = NS.gridDatos.getStore().recordType;
					if(combo.getValue() == 0)
					{
						Ext.getCmp(PF+'cmbBanco').show();
						NS.storeBanco.load();
						Ext.getCmp(PF+'cmbDivisa').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFecha1').hide();
						Ext.getCmp(PF+'txtFecha2').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtBenef').hide();
						Ext.getCmp(PF+'txtCheque1').hide();
						Ext.getCmp(PF+'txtCheque2').hide();
					
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='BANCO')
								indice=i;
						}
						
						if(indice>0)
						{
							BFwrk.Util.msgShow('Ya indicó el criterio BANCO','WARNING');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'BANCO',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
						
					}
					if(combo.getValue() == 1)
					{
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbDivisa').hide();
						Ext.getCmp(PF+'cmbChequera').show();
						NS.storeChequera.load();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFecha1').hide();
						Ext.getCmp(PF+'txtFecha2').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtBenef').hide();
						Ext.getCmp(PF+'txtCheque1').hide();
						Ext.getCmp(PF+'txtCheque2').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='BANCO')
								indice=i;
						}
						if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='BANCO')
						{
							for(i=0;i<recordsDatGrid.length;i++)
							{
								if(recordsDatGrid[i].get('criterio')=='CHEQUERA')
									indice=i;
							}
							
							if(indice>0)
							{
								BFwrk.Util.msgShow('Ya indicó el criterio CHEQUERA','WARNING');
								combo.setDisabled(false);
								return;
							}
							
							else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'CHEQUERA',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
						}
						else
						{
							BFwrk.Util.msgShow('Primero debe escoger un Banco','ERROR');
							combo.setDisabled(false);
						}
					}
					if(combo.getValue() == 2)
					{
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbDivisa').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'txtConcepto').show();
						Ext.getCmp(PF+'txtFecha1').hide();
						Ext.getCmp(PF+'txtFecha2').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtBenef').hide();
						Ext.getCmp(PF+'txtCheque1').hide();
						Ext.getCmp(PF+'txtCheque2').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='CONCEPTO')
								indice=i;
						}
						
						if(indice>0)
						{
							BFwrk.Util.msgShow('Ya indicó el criterio CONCEPTO','WARNING');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'CONCEPTO',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}
					if(combo.getValue() == 3)
					{
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbDivisa').show();
						NS.storeDivisa.load();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFecha1').hide();
						Ext.getCmp(PF+'txtFecha2').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtBenef').hide();
						Ext.getCmp(PF+'txtCheque1').hide();
						Ext.getCmp(PF+'txtCheque2').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='DIVISA')
								indice=i;
						}
						
						if(indice>0)
						{
							BFwrk.Util.msgShow('Ya indicó el criterio DIVISA','WARNING');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'DIVISA',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}	
					if(combo.getValue() == 4)
					{
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbDivisa').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFecha1').show();
						Ext.getCmp(PF+'txtFecha2').show();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtBenef').hide();
						Ext.getCmp(PF+'txtCheque1').hide();
						Ext.getCmp(PF+'txtCheque2').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='FECHA')
								indice=i;
						}
						
						if(indice>0)
						{
							BFwrk.Util.msgShow('Ya indicó el criterio FECHA','WARNING');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'FECHA',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}
					if(combo.getValue() == 5)
					{
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbDivisa').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFecha1').hide();
						Ext.getCmp(PF+'txtFecha2').hide();
						Ext.getCmp(PF+'txtMonto1').show();
						Ext.getCmp(PF+'txtMonto2').show();
						Ext.getCmp(PF+'txtBenef').hide();
						Ext.getCmp(PF+'txtCheque1').hide();
						Ext.getCmp(PF+'txtCheque2').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='MONTOS')
								indice=i;
						}
						
						if(indice>0)
						{
							BFwrk.Util.msgShow('Ya indicó el criterio MONTOS','WARNING');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'MONTOS',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}
					if(combo.getValue() == 6)
					{
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbDivisa').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFecha1').hide();
						Ext.getCmp(PF+'txtFecha2').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtBenef').show();
						Ext.getCmp(PF+'txtCheque1').hide();
						Ext.getCmp(PF+'txtCheque2').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='BENEFICIARIO')
								indice=i;
						}
						
						if(indice>0)
						{
							BFwrk.Util.msgShow('Ya indicó el criterio BENEFICIARIO','WARNING');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'BENEFICIARIO',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}
					if(combo.getValue() == 7)
					{
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbDivisa').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFecha1').hide();
						Ext.getCmp(PF+'txtFecha2').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtBenef').hide();
						Ext.getCmp(PF+'txtCheque1').show();
						Ext.getCmp(PF+'txtCheque2').show();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='No. CHEQUE')
								indice=i;
						}
						
						if(indice>0)
						{
							BFwrk.Util.msgShow('Ya indicó el criterio No. CHEQUE','WARNING');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'No. CHEQUE',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}
				}
			}
		}
	});
	
	//store del grid criterios
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'criterio',//identificador del store
		fields: [
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
		x :235,
		y :35,
		frame: true,
		title :'',
		columns : [ 
		{
			id :'criterio',header :'Criterio',width :120,dataIndex :'criterio'
		}, 
		{
			header :'Valor',width :135,dataIndex :'valor'
		} ],
		listeners:{
			dblclick:{
				fn:function(grid){
					var records = NS.gridDatos.getSelectionModel().getSelections();
					for(var i=0;i<records.length;i++)
					{
						NS.gridDatos.store.remove(records[i]);
						NS.gridDatos.getView().refresh();
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
					}
				}
			}
		}
	});
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true,
	});
	
	//store del grid de consulta
	NS.storeGridConsulta = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{empresa : ''+NS.GI_ID_EMPRESA
					,caja: ''+NS.GI_ID_CAJA
					,banco: '0'
					,divisa: ''
					,chequera: ''
					,concepto:''
					,fechaIni: ''
					,fechaFin: ''
					,montoIni: '0'
					,montoFin: '0'
					,beneficiario: ''
					,noChequeIni: '0'
					,noChequeFin: '0'
					},
		paramOrder:['empresa','caja','banco','divisa','chequera','concepto','fechaIni',
					'fechaFin','montoIni','montoFin','beneficiario','noChequeIni','noChequeFin'],
		directFn: CajaAction.llenarGridConsultaCheques,
		//idProperty: 'noEmpresa',
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'importe'},
			{name: 'idDivisa'},
			{name: 'noCheque'},
			{name: 'beneficiario'},
			{name: 'fecValor'},
			{name: 'concepto'},
			{name: 'noFolioDet'},
			{name: 'idTipoOperacion'},
			{name: 'folioSeg'},
			{name: 'idEstatusCheq'},
			{name: 'idEstatusMov'},
			{name: 'idBanco'},
			{name: 'cveControl'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridConsulta, msg:"Cargando..."});
				Ext.getCmp(PF+'btnBuscar').setDisabled(false);
				if(records.length==null || records.length<=0)
				{
					BFwrk.Util.msgShow('No se encontraron resultados', 'INFO');
					return;
				}
				else
				{	
					NS.sm.selectRange(0,records.length-1);
          			for(var i = 0; i < records.length; i = i + 1)
          			{
          				if(records[i].get('idTipoOperacion') === 3000 && records[i].get('folioSeg') === 0)
          				{
          					NS.sm.deselectRow(i);
         					//NS.sm.lock(i);
       					}
          					
          			}
          			NS.sumarTotales(records);
				}
				
				NS.bloqueoCheck();
			}
		}
	}); 
	//grid consulta
	NS.gridConsulta  = new Ext.grid.GridPanel({
		store : NS.storeGridConsulta,
		id: PF+'gridConsulta',
		name: PF+'gridConsulta',
		frame: true,
		width: 595,
       	height: 170,
		x :0,
		y :0,
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
       	columns: [ NS.sm,
			{
				header :'No. Empresa', width :80, dataIndex :'noEmpresa', hidden: true
			},{
				header :'Empresa', width :180, dataIndex :'nomEmpresa'
			},{
				header :'Banco', width :100, dataIndex :'descBanco' 
			},{
				header :'Chequera', width :100, dataIndex :'idChequera'
			},{
				header :'Importe', width :100, dataIndex :'importe', renderer: BFwrk.Util.rendererMoney
			},{
				header :'Divisa', width :80, dataIndex :'idDivisa'
			},{
				header :'No. Cheque', width :80, dataIndex :'noCheque'
			},{
				header :'Beneficiario', width :180, dataIndex :'beneficiario'
			},{
				header :'Fecha', width :100, dataIndex :'fecValor'
			},{
				header :'Concepto', width :180, dataIndex :'concepto'
			},{
				header :'Folio', width :70, dataIndex :'noFolioDet'
			},{
				header :'Tipo Operacion', width :100, dataIndex :'idTipoOperacion', hidden: true
			},{
				header :'Fondeado', width :100, dataIndex :'folioSeg', hidden: true
			},{
				header :'Estatus', width :100, dataIndex :'idEstatusCheq'
			},{
				header :'ID. Estatus', width :100, dataIndex :'idEstatusMov', hidden: true
			}]
			}),
    	viewConfig: {
	        getRowClass: function(record, index){ 
		        if(record.get('idTipoOperacion') === 3000 && record.get('folioSeg') === 0)
	            {
	                return 'row-font-color-red';
	            }
//	            if(index==1)
//	            {
//	                return 'row-font-color-red';
//	            }
           	}
        },
		sm: NS.sm,
		listeners:{
			click:{
				fn:function(grid){
					var regSelec = NS.gridConsulta.getSelectionModel().getSelections();
					
					for(var i = 0; i < regSelec.length; i = i + 1) {
						if(regSelec[i].get('idTipoOperacion') == 3000 && regSelec[i].get('folioSeg') == 0)
          					NS.sm.deselectRow(i);
					}
					NS.sumarTotales(regSelec);
					NS.bloqueoCheck();
				}
			}
		}
	});
	
	NS.bloqueoCheck =function(){

		$('.row-font-color-red table tr td div div').each(function(index,el){
		    $(el).removeClass('x-grid3-row-checker');
		    $(el).attr('enabled','enabled');
		    $(el).attr('disabled','disabled');
		    $(el).click(function(){
		   		 $(el).removeClass('x-grid3-row-selected');
				});
			$(el).click(function(){
	   			 return false;
			});
		});
		$('.row-font-color-red table tr td div').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
	    	$(el).click(function(){
	   			 return false;
			});
	   		
		});
		
			$('.row-font-color-red table tr td').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
	    	$(el).click(function(){
	   			 return false;
			});
	   		
		});
		
			$('.row-font-color-red table tr').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
	    	$(el).click(function(){
	   			 return false;
			});
	   		
		});
				$('.row-font-color-red table').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
	    	$(el).click(function(){
	   			 return false;
			});
	   		
		});
					$('.row-font-color-red').each(function(index,el){
	    	$(el).removeClass('x-grid3-row-checker');
	    	$(el).attr('enabled','enabled');
	    	$(el).attr('disabled','disabled');
	    	$(el).click(function(){
	   			 return false;
			});
	   		
		});
	
	};
	
	//*****componentes de la ventana de reportes
	//combo Empresas coinversoras
	NS.cmbEmpresaVentana = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa
		,name: PF+'cmbEmpresaVentana'
		,id: PF+'cmbEmpresaVentana'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 86
        ,y: 6
        ,width: 252
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: NS.GI_NOM_EMPRESA
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbEmpresaVentana(combo.getValue());
				}
			}
		}
	});
	
	//store banco
	NS.storeBancoVentana = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			empresa: NS.GI_ID_EMPRESA 
		},
		root: '',
		paramOrder:['empresa'],
		directFn: CajaAction.llenarComboBancosVentana, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					BFwrk.Util.msgShow('No hay bancos asignados a la empresa seleccionada', 'INFO');
					return;
				}
			}
		}
	}); 
	
	//combo banco
	NS.storeBancoVentana.load();
	NS.cmbBancoVentana = new Ext.form.ComboBox({
		store: NS.storeBancoVentana
		,name: PF+'cmbBancoVentana'
		,id: PF+'cmbBancoVentana'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 137
        ,y: 48
        ,width: 200
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,hidden: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtBanco',NS.cmbBancoVentana.getId());
					NS.accionarCmbBancoVentana(combo.getValue());
				}
			}
		}
	});
	
	//store caja
	NS.storeCajaVentana = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_caja',
			campoDos:'desc_caja',
			tabla:'cat_caja',
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
	
	//combo CajaVentana
	NS.storeCajaVentana.load();
	NS.cmbCajaVentana = new Ext.form.ComboBox({
		store: NS.storeCajaVentana
		,name: PF+'cmbCajaVentana'
		,id: PF+'cmbCajaVentana'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 138
        ,y: 128
        ,width: 200
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una caja'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtCaja',NS.cmbCajaVentana.getId());
					//NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	
	//store divisa
	NS.storeDivisaVentana = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			condicion:'muestra_divisa = \'S\''
		},
		root: '',
		paramOrder:['condicion'],
		directFn: GlobalAction.llenarComboDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo DivisaVentana
	NS.storeDivisaVentana.load();
	NS.cmbDivisaVentana = new Ext.form.ComboBox({
		store: NS.storeDivisaVentana
		,name: PF+'cmbDivisaVentana'
		,id: PF+'cmbDivisaVentana'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 138
        ,y: 168
        ,width: 200
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione una divisa'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtDivisa',NS.cmbDivisaVentana.getId());
					//NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	
	//store chequeras
	NS.storeChequerasVentana = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			banco:'',
			empresa:NS.GI_ID_EMPRESA 
		},
		root: '',
		paramOrder:['banco','empresa'],
		directFn: CajaAction.llenarChequerasVentana, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					BFwrk.Util.msgShow('No hay chequeras asignadas al banco seleccionado', 'INFO');
					return;
				}
			}
		}
	}); 
	
	//combo DivisaVentana
	NS.cmbChequerasVentana = new Ext.form.ComboBox({
		store: NS.storeChequerasVentana
		,name: PF+'cmbChequerasVentana'
		,id: PF+'cmbChequerasVentana'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 87
        ,y: 88
        ,width: 250
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					//NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});

	NS.sumarTotales = function(records){
		var mn = 0;
		var dls = 0;
		var totalImporteMN=0;
		var totalImporteDLS=0;
		for(var k = 0;k < records.length;k = k + 1)
		{ 
			if ((records[k].get('idTipoOperacion') !== 3000 && records[k].get('folioSeg') === 0)||
				(records[k].get('idTipoOperacion') === 3000 && records[k].get('folioSeg') !== 0)||
				(records[k].get('idTipoOperacion') !== 3000 && records[k].get('folioSeg') !== 0))
				if(records[k].get('idDivisa') === 'MN')
				{
					totalImporteMN=totalImporteMN+records[k].get('importe');
					mn = mn + 1;
				}
				else if(regSelec[k].get('idDivisa') === 'DLS')
				{
					totalImporteDLS=totalImporteDLS+records[k].get('importe');
					dls = dls + 1;
				}
		}
		Ext.getCmp(PF+'txtRegistrosMN').setValue(mn);
		Ext.getCmp(PF+'txtTotalMN').setValue(BFwrk.Util.formatNumber(Math.round((totalImporteMN)*100)/100));
		Ext.getCmp(PF+'txtRegistrosDLS').setValue(dls);
		Ext.getCmp(PF+'txtTotalDLS').setValue(BFwrk.Util.formatNumber(Math.round((totalImporteDLS)*100)/100));
	}
	
	NS.accionarCmbEmpresa = function(comboValor){
		NS.storeBanco.baseParams.empresa = comboValor;
		NS.storeChequera.baseParams.empresa = comboValor;
		Ext.getCmp(PF+'chkTodas').setValue(false);
	}
	
	NS.accionarCmbEmpresaVentana = function(comboValor){
		NS.storeBancoVentana.baseParams.empresa = comboValor;
		NS.storeBancoVentana.load();
		NS.storeChequerasVentana.baseParams.empresa = comboValor;
		NS.empresaSup = comboValor;
    	NS.empresaInf = comboValor;
		NS.nomEmpresa = NS.storeCmbEmpresa.getById(comboValor).get('nomEmpresa');
	}
	
	NS.accionarCmbBancoVentana = function(comboValor){
		Ext.getCmp(PF+'cmbChequerasVentana').reset();
		NS.storeChequerasVentana.baseParams.banco = comboValor;
		NS.storeChequerasVentana.load();
	}
	
	NS.accionarCmbBanco = function(comboValor){
		Ext.getCmp(PF+'cmbChequera').reset();
		NS.storeChequera.baseParams.banco = comboValor;
		var banco = NS.storeBanco.getById(comboValor).get('descripcion');
		var datosClase = NS.gridDatos.getStore().recordType;
		var indice=0;
		var i=0;
		var recordsDatGrid=NS.storeDatos.data.items;
		
		for(i=0;i<recordsDatGrid.length;i++)
		{
			if(recordsDatGrid[i].get('criterio')=='BANCO')
			{
				indice=i;
				NS.storeDatos.remove(recordsDatGrid[indice]);
			}
		}
		var datos = new datosClase({
           	criterio: 'BANCO',
			valor: banco
   		});
        NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(indice, datos);
  		NS.gridDatos.getView().refresh();
  		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}
	
	NS.accionarCmbChequera = function(comboValor){
		var chequera = NS.storeChequera.getById(comboValor).get('descripcion');
		var datosClase = NS.gridDatos.getStore().recordType;
		var indice=0;
		var i=0;
		var recordsDatGrid=NS.storeDatos.data.items;
		
		for(i=0;i<recordsDatGrid.length;i++)
		{
			if(recordsDatGrid[i].get('criterio')=='CHEQUERA')
			{
				indice=i;
				NS.storeDatos.remove(recordsDatGrid[indice]);
			}
		}
		var datos = new datosClase({
           	criterio: 'CHEQUERA',
			valor: chequera
   		});
        NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(indice, datos);
  		NS.gridDatos.getView().refresh();
  		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}
	
	NS.accionarCmbDivisa = function(comboValor){
		var divisa = NS.storeDivisa.getById(comboValor).get('descDivisa');
		var datosClase = NS.gridDatos.getStore().recordType;
		var indice=0;
		var i=0;
		var recordsDatGrid=NS.storeDatos.data.items;
		
		for(i=0;i<recordsDatGrid.length;i++)
		{
			if(recordsDatGrid[i].get('criterio')=='DIVISA')
			{
				indice=i;
				NS.storeDatos.remove(recordsDatGrid[indice]);
			}
		}
		var datos = new datosClase({
           	criterio: 'DIVISA',
			valor: divisa
   		});
        NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(indice, datos);
  		NS.gridDatos.getView().refresh();
  		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}
	
	NS.limpiarParams = function(){
		NS.storeGridConsulta.baseParams.banco = '0';
		NS.storeGridConsulta.baseParams.divisa = '';
		NS.storeGridConsulta.baseParams.chequera = '';
		NS.storeGridConsulta.baseParams.concepto = '';
		NS.storeGridConsulta.baseParams.fechaIni = '';
		NS.storeGridConsulta.baseParams.fechaFin = '';
		NS.storeGridConsulta.baseParams.montoIni = '0';
		NS.storeGridConsulta.baseParams.montoFin = '0';
		NS.storeGridConsulta.baseParams.beneficiario = '';
		NS.storeGridConsulta.baseParams.noChequeIni = '0';
		NS.storeGridConsulta.baseParams.noChequeFin = '0';
	}
	
	NS.limpiarForm = function(){
		NS.contenedorChequesxEntregar.getForm().reset();
	   	NS.storeDatos.removeAll();
	   	NS.gridDatos.getView().refresh();
	   	NS.storeGridConsulta.removeAll();
	   	NS.gridConsulta.getView().refresh();
	   	NS.limpiarParams();
	}
	
	NS.buscar = function(){
		NS.storeGridConsulta.removeAll();
		NS.limpiarParams();
		var recordsDatosGrid=NS.storeDatos.data.items;
		if(recordsDatosGrid.length>0)
		{
			for(var i=0;i<recordsDatosGrid.length;i=i+1)
			{
				if(recordsDatosGrid[i].get('criterio')==='BANCO')
  				{
  					var valorBanco=recordsDatosGrid[i].get('valor');
  					if(valorBanco!='')
  					{
  						var recordsDataBanco=NS.storeBanco.data.items;
  						for(var j=0;j<recordsDataBanco.length;j=j+1)
  						{
  							if(recordsDataBanco[j].get('descripcion')===valorBanco)
  							{
  								var idBanco=recordsDataBanco[j].get('id');
								NS.storeGridConsulta.baseParams.banco = ''+idBanco;
   							}
  						}
  					}
  					else{
  						BFwrk.Util.msgShow('Debe agregar un valor a BANCO','WARNING');
  						return;
  					}
  				}
  				else if(recordsDatosGrid[i].get('criterio')==='CHEQUERA')
 				{
 					var valorChequera=recordsDatosGrid[i].get('valor');
 					if(valorChequera!='')
 					{
 						NS.storeGridConsulta.baseParams.chequera=''+valorChequera;
 					}
 					else{
 						BFwrk.Util.msgShow('Debe agregar un valor a CHEQUERA','WARNING');
 						return;
 					}
 				}
 				else if(recordsDatosGrid[i].get('criterio')==='DIVISA')
  				{
  					var valorDivisa=recordsDatosGrid[i].get('valor');
  					if(valorDivisa!='')
  					{
  						var recordsDataDivisa=NS.storeDivisa.data.items;
  						for(var j=0;j<recordsDataDivisa.length;j=j+1)
  						{
  							if(recordsDataDivisa[j].get('descDivisa')===valorDivisa)
  							{
  								var idDivisa=recordsDataDivisa[j].get('idDivisa');
								NS.storeGridConsulta.baseParams.divisa = ''+idDivisa;
   							}
  						}
  					}
  					else{
  						BFwrk.Util.msgShow('Debe agregar un valor a DIVISA','WARNING');
  						return;
  					}
  				}
  				else if(recordsDatosGrid[i].get('criterio')==='CONCEPTO')
  				{
  					var valorConcepto=recordsDatosGrid[i].get('valor');
  					if(valorConcepto!='')
  					{
						NS.storeGridConsulta.baseParams.concepto = ''+valorConcepto;
  					}
  					else{
  						BFwrk.Util.msgShow('Debe agregar un valor a CONCEPTO','WARNING');
  						return;
  					}
  				}
  				else if(recordsDatosGrid[i].get('criterio')==='BENEFICIARIO')
  				{
  					var valorBenef=recordsDatosGrid[i].get('valor');
  					if(valorBenef!='')
  					{
						NS.storeGridConsulta.baseParams.beneficiario = ''+valorBenef;
  					}
  					else{
  						BFwrk.Util.msgShow('Debe agregar un valor a BENEFICIARIO','WARNING');
  						return;
  					}
  				}
  				else if(recordsDatosGrid[i].get('criterio')==='No. CHEQUE')
   				{
   					var valorNoCheque=recordsDatosGrid[i].get('valor');
   					if(valorNoCheque!='')
   					{
   						var ini=obtenerValIni(valorNoCheque);
   						var fin=obtenerValFin(valorNoCheque);
   						NS.storeGridConsulta.baseParams.noChequeIni = ''+ini;
						NS.storeGridConsulta.baseParams.noChequeFin = ''+fin;
   					}
   					else
   					{
   						BFwrk.Util.msgShow('Debe agregar un valor a No. CHEQUE','WARNING');
   						return;
   					}
   				}
   				else if(recordsDatosGrid[i].get('criterio')==='FECHA')
   				{
   					var valorFecha=recordsDatosGrid[i].get('valor');
   					if(valorFecha!=''){
   						var ini=obtenerValIni(valorFecha);
   						NS.storeGridConsulta.baseParams.fechaIni=''+ini.replace(/^\s*|\s*$/g,"");
   						if(Ext.getCmp(PF+'txtFecha2').getValue() !== '')
   						{
	   						var fin=obtenerValFin(valorFecha);
	   						NS.storeGridConsulta.baseParams.fechaFin=''+fin.replace(/^\s*|\s*$/g,"");
   						}
   						
   					}
   					else{
   						BFwrk.Util.msgShow('Debe agregar un valor a FECHA','WARNING');
   						return;
   					}
   				}
   				else if(recordsDatosGrid[i].get('criterio')==='MONTOS')
   				{
   					var valorImporte=recordsDatosGrid[i].get('valor');
   					if(valorImporte!='')
   					{
   						var ini=obtenerValIni(valorImporte);
   						var fin=obtenerValFin(valorImporte);
   						NS.storeGridConsulta.baseParams.montoIni = BFwrk.Util.unformatNumber(''+ini);
						NS.storeGridConsulta.baseParams.montoFin = BFwrk.Util.unformatNumber(''+fin);
   					}
   					else
   					{
   						BFwrk.Util.msgShow('Debe agregar un valor a MONTOS','WARNING');
   						return;
   					}
   				}
			}
			NS.storeGridConsulta.baseParams.empresa = '' + Ext.getCmp(PF+'txtEmpresa').getValue() == '' ? 0 : '' + Ext.getCmp(PF+'txtEmpresa').getValue();
			NS.storeGridConsulta.load();
		}
	}
	
	NS.ejecutarEntrega = function(){
		BFwrk.Util.msgWait('Ejecutando entrega...', true);
		var registrosGrid = NS.gridConsulta.getSelectionModel().getSelections();
		var matrizGrid = new Array ();
		
		for(var i=0; i < registrosGrid.length; i++) {
			var regGrid = {};
			regGrid.noEmpresa = registrosGrid[i].get('noEmpresa');
			regGrid.nomEmpresa = registrosGrid[i].get('nomEmpresa');
			regGrid.descBanco = registrosGrid[i].get('descBanco');
			regGrid.idChequera = registrosGrid[i].get('idChequera');
			regGrid.importe = registrosGrid[i].get('importe');
			regGrid.idDivisa = registrosGrid[i].get('idDivisa');
			regGrid.noCheque = registrosGrid[i].get('noCheque');
			regGrid.beneficiario = registrosGrid[i].get('beneficiario');
			regGrid.fecValor = registrosGrid[i].get('fecValor');
			regGrid.concepto = registrosGrid[i].get('concepto');
			regGrid.noFolioDet = registrosGrid[i].get('noFolioDet');
			regGrid.idTipoOperacion = registrosGrid[i].get('idTipoOperacion');
			regGrid.idEstatusMov = registrosGrid[i].get('idEstatusMov');
			regGrid.idBanco = registrosGrid[i].get('idBanco');
			regGrid.cveControl = registrosGrid[i].get('cveControl');
			matrizGrid[i] = regGrid; 
		}
		var jsonString = Ext.util.JSON.encode(matrizGrid);
		CajaAction.ejecutaEntregaCheques(jsonString, apps.SET.FEC_HOY, NS.GI_USUARIO, function(mapRetorno, e){
		
			BFwrk.Util.msgWait('Terminado...', false);
		
			if(mapRetorno.msgUsuario!==null  &&  mapRetorno.msgUsuario!=='' && mapRetorno.msgUsuario!= undefined)
			{
				//for(var msg = 0; msg < mapRetorno.msgUsuario.length; msg++)
				//{
					//BFwrk.Util.msgShow(''+mapRetorno.msgUsuario[msg], 'INFO');
					Ext.Msg.alert('SET', '' + mapRetorno.msgUsuario);
				//}
				var msg = 'El movimiento con No. folio';
				if(mapRetorno.msgUsuario.indexOf(msg) < 0) NS.limpiarForm();
			}
		});
	}
	
	function obtenerValIni(valor)
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
	
	function obtenerValFin(valor)
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
	
	NS.inicializaParams = function(){
		NS.fechaIni = '';
		NS.fechaFin = '';
		NS.empresaSup = NS.GI_ID_EMPRESA;
		NS.empresaInf = NS.GI_ID_EMPRESA;
		NS.cajaInf = 0;
		NS.cajaSup = 30000;
		NS.bancoInf = 0;
		NS.bancoSup = 32000;
		NS.chequera = '%25';
		NS.divisa = '';
		NS.nomEmpresa = NS.GI_NOM_EMPRESA;
		NS.subtitulo = '';
		NS.descDivisa = '';
		//Ext.getCmp.get(PF+'cmbCajaVentana').setValue('');
	}
	
	NS.escribeSubtitulo = function(texto){
		if(NS.fechaIni == NS.fechaFin)
		{
			NS.subtitulo = texto + ' al '+ NS.fechaIni + ' en ' + NS.descDivisa;
		}
		else
		{
			NS.subtitulo = texto + ' del ' + NS.fechaIni + ' al ' + NS.fechaFin + ' en ' + NS.descDivisa;
		}
	}
	
	NS.strParametros = function(nombreReporte){
		strParams = '?nomReporte='+nombreReporte;
		strParams += '&'+'nomParam1=empresaInf';
		strParams += '&'+'valParam1='+NS.empresaInf;
		strParams += '&'+'nomParam2=empresaSup';
		strParams += '&'+'valParam2='+NS.empresaSup;
		strParams += '&'+'nomParam3=bancoInf';
		strParams += '&'+'valParam3='+NS.bancoInf;
		strParams += '&'+'nomParam4=bancoSup';
		strParams += '&'+'valParam4='+NS.bancoSup;
		strParams += '&'+'nomParam5=chequera';
		strParams += '&'+'valParam5='+NS.chequera;
		strParams += '&'+'nomParam6=fechaInf';
		strParams += '&'+'valParam6='+NS.fechaIni;
		strParams += '&'+'nomParam7=fechaSup';
		strParams += '&'+'valParam7='+NS.fechaFin;
		strParams += '&'+'nomParam8=fechaHoy';
		strParams += '&'+'valParam8='+NS.fechaHoy;
		strParams += '&'+'nomParam9=cajaInf';
		strParams += '&'+'valParam9='+NS.cajaInf;
		strParams += '&'+'nomParam10=cajaSup';
		strParams += '&'+'valParam10='+NS.cajaSup;
		strParams += '&'+'nomParam11=divisa';
		strParams += '&'+'valParam11='+NS.divisa;
		strParams += '&'+'nomParam12=nomEmpresa';
		strParams += '&'+'valParam12='+NS.nomEmpresa;
		strParams += '&'+'nomParam13=subtitulo';
		strParams += '&'+'valParam13='+NS.subtitulo;
	}
	
	NS.buscarReporte = function(){
		//cheques por entregar
		if(Ext.getCmp(PF+'optPorEntregar').getValue() == true)
		{	
			NS.escribeSubtitulo('Cheques por Entregar');
			NS.strParametros('reporteChequesPorEntregar');
		}
		//cheques entregados no historico ordenado por banco
		else if(NS.fechaIni == NS.fechaHoy && NS.fechaFin == NS.fechaHoy && Ext.getCmp(PF+'optBanco').getValue() == true)
		{
			NS.escribeSubtitulo('Cheques Entregados');
			NS.strParametros('reporteChequesEntregados');
		}
		//cheques entregados historico ordenado por banco
		else if((NS.fechaIni != NS.fechaHoy || NS.fechaFin != NS.fechaHoy) && Ext.getCmp(PF+'optBanco').getValue() == true)
		{
			NS.escribeSubtitulo('Cheques Entregados Historicos');
			NS.strParametros('reporteHistoricoChequesEntregados');
		}
		//cheques entregados no historico ordenado por caja
		else if(NS.fechaIni == NS.fechaHoy && NS.fechaFin == NS.fechaHoy && Ext.getCmp(PF+'optCaja').getValue() == true)
		{
			NS.escribeSubtitulo('Cheques Entregados');
			NS.strParametros('reporteChequesEntregadosPorCaja');
		}
		//cheques entregados historico ordenado por caja
		else if((NS.fechaIni != NS.fechaHoy || NS.fechaFin != NS.fechaHoy) && Ext.getCmp(PF+'optCaja').getValue() == true)
		{
			NS.escribeSubtitulo('Cheques Entregados Historicos');
			NS.strParametros('reporteHistoricoChequesEntregadosPorCaja');
		}
    	window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
	}
	
	NS.contenedorChequesxEntregar = new Ext.FormPanel({
	    title: 'Cheques y Efectivo por Entregar',
	    width: 662,
	    height: 568,
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
	                width: 640,
	                height: 520,
	                layout: 'absolute',
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: 'B&#250;squeda',
	                        x: 0,
	                        y: 0,
	                        layout: 'absolute',
	                        height: 183,
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Empresa:',
	                                x: 0,
	                                y: 0
	                            },
	                            NS.cmbEmpresa,
	                            {
			                        xtype: 'textfield',
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
			                    },
	                            {
	                                xtype: 'checkbox',
	                                id:PF+'chkTodas',
	                                name:PF+'chkTodas',
	                                x: 466,
	                                y: 0,
	                                boxLabel: 'Todas las Empresas',
	                                checked: false,
	                                hidden: true,
	                                listeners:{
	                                	check:{
                                  			fn:function(box)
                                  			{
                                  				if(box.getValue()== true)
                                   				{
	                                				NS.storeGridConsulta.baseParams.empresa = '0';
                                				}
                                				else
                                				{
                                					NS.storeGridConsulta.baseParams.empresa = ''+Ext.getCmp(PF+'txtEmpresa').getValue();
                                				}
	                                		}
                                		}
	                                }
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Criterio:',
	                                x: 0,
	                                y: 58
	                            },
	                            NS.cmbCriterio,
	                            {
	                                xtype: 'label',
	                                text: 'Valor:',
	                                x: 0,
	                                y: 106
	                            },
	                            NS.cmbBanco,
	                            {
			                        xtype: 'textfield',
			                        id: PF+'txtConcepto',
			                        name: PF+'txtConcepto',
			                        hidden: true,
			                        x: 56,
			                        y: 105,
			                        width: 170,
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='CONCEPTO')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
										    	var datos = new datosClase({
							                			criterio: 'CONCEPTO',
														valor: valor
							           			});
							           			Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
			                        		}
			                        	}
			                        }
			                    },
			                    {
			                        xtype: 'datefield',
			                        id: PF+'txtFecha1',
			                        name: PF+'txtFecha1',
			                        format: 'd/m/Y',
			                        hidden: true,
			                        x: 56,
			                        y: 105,
			                        width: 80,
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='FECHA')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
												var valorFecha='';
												if(valor!='')
												{
													valorFecha=BFwrk.Util.changeDateToString(''+valor);
												}
										    	var datos = new datosClase({
							                			criterio: 'FECHA',
														valor: valorFecha
							           			});
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
			                        		}
			                        	}
			                        }
			                    },
			                    {
			                        xtype: 'datefield',
			                        id: PF+'txtFecha2',
			                        name: PF+'txtFecha2',
			                        format: 'd/m/Y',
			                        hidden: true,
			                        x: 146,
			                        y: 105,
			                        width: 80,
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;	
												var fechaUno=Ext.getCmp(PF+'txtFecha1').getValue();
												var valIni='';
												if(fechaUno!='')
												{
													valIni=BFwrk.Util.changeDateToString(''+fechaUno);
												}
												if(valIni!='')
												{
													valIni+=' a ';
												}			
												Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='FECHA')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
												var valFin='';
												if(valor!='')
												{
													valFin=BFwrk.Util.changeDateToString(''+valor);
												}
												if(fechaUno>valor)
												{
													BFwrk.Util.msgShow('La fecha inicial debe ser menor a la final', 'ERROR');
												}
												else
												{
											    	var datos = new datosClase({
								                			criterio: 'FECHA',
															valor: valIni + valFin
								           			});
								                    NS.gridDatos.stopEditing();
								           			NS.storeDatos.insert(indice, datos);
								           			NS.gridDatos.getView().refresh();
			                   					}
			                        		}
			                        	}
			                        }
			                    },
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtMonto1',
			                        name: PF+'txtMonto1',
			                        hidden: true,
			                        x: 56,
			                        y: 105,
			                        width: 80,
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='MONTOS')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
										    	var datos = new datosClase({
							                			criterio: 'MONTOS',
														valor: BFwrk.Util.formatNumber(valor)
							           			});
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
			                        		}
			                        	}
			                        }
			                    },
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtMonto2',
			                        name: PF+'txtMonto2',
			                        hidden: true,
			                        x: 146,
			                        y: 105,
			                        width: 80,
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;		
												var valorUno=BFwrk.Util.formatNumber(Ext.getCmp(PF+'txtMonto1').getValue());
												if(valorUno!='')
													valorUno+=' a '
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='MONTOS')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
												if(parseInt(valorUno) > parseInt(valor))
												{
													BFwrk.Util.msgShow('El importe dos debe ser mayor o igual al importe uno', 'ERROR');
													Ext.getCmp(PF+'txtMonto1').setValue('');
													Ext.getCmp(PF+'txtMonto2').setValue('');
													return;
												}
										    	var datos = new datosClase({
							                			criterio: 'MONTOS',
														valor: valorUno + BFwrk.Util.formatNumber(valor)
							           			});
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
							           			
		                   						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
			                        		}
			                        	}
			                        }
			                    },
			                    NS.cmbDivisa,
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtBenef',
			                        name: PF+'txtBenef',
			                        hidden: true,
			                        x: 56,
			                        y: 105,
			                        width: 170,
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='BENEFICIARIO')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
										    	var datos = new datosClase({
							                			criterio: 'BENEFICIARIO',
														valor: valor
							           			});
							           			Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
			                        		}
			                        	}
			                        }
			                    },
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtCheque1',
			                        name: PF+'txtCheque1',
			                        hidden: true,
			                        x: 56,
			                        y: 105,
			                        width: 80,
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='No. CHEQUE')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
										    	var datos = new datosClase({
							                			criterio: 'No. CHEQUE',
														valor: valor
							           			});
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
			                        		}
			                        	}
			                        }
			                    },
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtCheque2',
			                        name: PF+'txtCheque2',
			                        hidden: true,
			                        x: 146,
			                        y: 105,
			                        width: 80,
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;		
												var valorUno=(Ext.getCmp(PF+'txtCheque1').getValue());
												if(valorUno!='')
													valorUno+=' a '
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='No. CHEQUE')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
												if(parseInt(valorUno) > parseInt(valor))
												{
													BFwrk.Util.msgShow('El n&#250;mero de cheque dos debe ser mayor o igual al n&#250;mero de cheque uno', 'ERROR');
													Ext.getCmp(PF+'txtCheque1').setValue('');
													Ext.getCmp(PF+'txtCheque2').setValue('');
													return;
												}
										    	var datos = new datosClase({
							                			criterio: 'No. CHEQUE',
														valor: valorUno + valor
							           			});
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
		                   						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
			                        		}
			                        	}
			                        }
			                    },
			                    NS.cmbChequera,
	                            NS.gridDatos,
	                            {
	                                xtype: 'button',
	                                text: 'Buscar',
	                                id: PF+'btnBuscar',
	                                name: PF+'btnBuscar',
	                                x: 535,
	                                y: 115,
	                                width: 59,
	                                listeners:{
	                                	click:{
	                                		fn: function(e){
	                                			//Ext.getCmp(PF+'btnBuscar').setDisabled(true);
	                                			NS.limpiarParams();
	                                			Ext.getCmp(PF+'txtRegistrosMN').setValue(0);
												Ext.getCmp(PF+'txtTotalMN').setValue(BFwrk.Util.formatNumber(Math.round((0)*100)/100));
												Ext.getCmp(PF+'txtRegistrosDLS').setValue(0);
												Ext.getCmp(PF+'txtTotalDLS').setValue(BFwrk.Util.formatNumber(Math.round((0)*100)/100));
											    	
											    var recordsDatosGrid=NS.storeDatos.data.items;
												if(recordsDatosGrid.length <= 0)
												{
													NS.storeGridConsulta.baseParams.empresa = '' + Ext.getCmp(PF+'txtEmpresa').getValue() == '' ? 0 : '' + Ext.getCmp(PF+'txtEmpresa').getValue();
											    	NS.storeGridConsulta.load();
										    	}
											    else 
											    	NS.buscar();
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'checkbox',
	                                boxLabel: 'Por Rango',
	                                x: 59,
	                                y: 31
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Cheques',
	                        x: 0,
	                        y: 193,
	                        layout: 'absolute',
	                        height: 268,
	                        items: [
	                        	NS.gridConsulta,
	                            {
	                                xtype: 'label',
	                                text: 'Total de Registros MN:',
	                                x: 157,
	                                y: 189
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Monto Total MN:',
	                                x: 378,
	                                y: 189
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Total de Registros DLS:',
	                                x: 156,
	                                y: 217
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Monto Total DLS:',
	                                x: 381,
	                                y: 219
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtRegistrosMN',
	                                name: PF+'txtRegistrosMN',
	                                width: 70,
	                                x: 287,
	                                y: 182
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalMN',
	                                iname: PF+'txtTotalMN',
	                                x: 487,
	                                y: 181,
	                                width: 100
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtRegistrosDLS',
	                                name: PF+'txtRegistrosDLS',
	                                x: 287,
	                                y: 211,
	                                width: 70
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalDLS',
	                                name: PF+'txtTotalDLS',
	                                x: 487,
	                                y: 211,
	                                width: 100
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Registros marcados en color rojo pendientes de fondeo',
	                                x: 2,
	                                y: 190,
	                                width: 133
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Ejecutar',
	                        x: 369,
	                        y: 470,
	                        width: 66,
	                        listeners:{
		                  		click:{
		              			   	fn:function(e){
		              			   		var recordsGrid = NS.storeGridConsulta.data.items;
                        				var registros = NS.gridConsulta.getSelectionModel().getSelections();
                        				
                        				if(recordsGrid.length != null || recordsGrid.length > 0)
                        				{
	                        				if(registros.length != 0)
	                        				{
											        NS.ejecutarEntrega();
	                        				}
	                        				else
	                        				{
	                        					BFwrk.Util.msgShow('Es necesario seleccionar alg&#250;n registro', 'INFO');
                        						return;
	                        				}
                        				}
                        				else
                        				{
                        					BFwrk.Util.msgShow('No hay cheques para entregar', 'INFO');
                        					return;
                        				}
		              			   	}
	              			   	}
            			   	}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 459,
	                        y: 469,
	                        width: 67,
                         	id: PF+'cmdImprimir',
	                        name: PF+'cmdImprimir',
	                        listeners:{
		                  		click:{
		              			   fn:function(e){
		              			   		NS.ventanaReportes.show();
		              			   	}
		           			   	}
	       			   		}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 551,
	                        y: 468,
	                        width: 66,
	                        id: PF+'cmdLimpiar',
	                        name: PF+'cmdLimpiar',
	                        listeners:{
		                  		click:{
		              			   fn:function(e){
		              			   		NS.limpiarForm();
		              			   }
		           			   }
		       			   	}
	                    }
	                ]
	            }
	        ]
    });
    
    /**formulario de reportes**/
   NS.formReportes = new Ext.FormPanel({
	    width: 425,
	    height: 432,
	    frame: true,
	    layout: 'absolute',
	    id: PF+'formReportes',
	    name: PF+'formReportes',
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                layout: 'absolute',
	                width: 390,
	                height: 350,
	                x: 10,
	                y: 10,
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Empresa:',
	                        x: 18,
	                        y: 9
	                    },
	                    NS.cmbEmpresaVentana,
	                    {
	                        xtype: 'label',
	                        text: 'Banco:',
	                        x: 19,
	                        y: 48
	                    },
	                    NS.cmbBancoVentana,
	                    {
	                        xtype: 'label',
	                        text: 'Chequera:',
	                        x: 18,
	                        y: 87
	                    },
	                    {
	                        xtype: 'textfield',
	                        id: PF+'txtBanco',
	                        name: PF+'txtBanco',
	                        x: 87,
	                        y: 48,
	                        width: 43,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBancoVentana.getId());
	                        			NS.accionarCmbBancoVentana(comboValor);
	                        		}
                        		}
                       		}
	                    },
	                    NS.cmbChequerasVentana,
	                    {
	                        xtype: 'label',
	                        text: 'Caja:',
	                        x: 20,
	                        y: 126
	                    },
	                    {
	                        xtype: 'textfield',
	                        id: PF+'txtCaja',
	                        name: PF+'txtCaja',
	                        x: 88,
	                        y: 128,
	                        width: 41,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtCaja',NS.cmbCajaVentana.getId());
	                        			//NS.accionarCmbCajaVentana(comboValor);
	                        		}
                        		}
                       		}
	                    },
	                    NS.cmbCajaVentana,
	                    {
	                        xtype: 'label',
	                        text: 'Divisa:',
	                        x: 20,
	                        y: 168
	                    },
	                    {
	                        xtype: 'uppertextfield',
	                        id: PF+'txtDivisa',
	                        name: PF+'txtDivisa',
	                        x: 88,
	                        y: 168,
	                        width: 42,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa',NS.cmbDivisaVentana.getId());
	                        			//NS.accionarCmbDivisaVentana(comboValor);
	                        		}
                        		}
                       		}
	                    },
	                    NS.cmbDivisaVentana,
	                    {
	                        xtype: 'label',
	                        text: 'Desde:',
	                        x: 20,
	                        y: 208
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        name: PF+'txtFechaDesde',
                            id: PF+'txtFechaDesde',
	                        x: 87,
	                        y: 208,
	                        width: 103
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Hasta:',
	                        x: 195,
	                        y: 207
	                    },
	                    {
	                        xtype: 'datefield',
	                        format: 'd/m/Y',
	                        name: PF+'txtFechaHasta',
	                        id: PF+'txtFechaHasta',
	                        x: 235,
	                        y: 208,
	                        width: 103
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Cheques',
	                        x: 0,
	                        y: 242,
	                        layout: 'absolute',
	                        height: 77,
	                        width: 106,
	                        items: [
	                            {
	                                xtype: 'radio',
	                                boxLabel: 'Entregados',
	                                id: PF+'optEntregado',
	                                name: PF+'optEntregado',
	                                x: 0,
	                                y: 0,
	                                listeners:{
		              					check:{
              			   					fn:function(opt, valor){
                               					if(valor==true)
	                               				{
	                               					Ext.getCmp(PF+'optPorEntregar').setValue(false);
	                               				}
                              				}
                           				}
                       				}
	                            },
	                            {
	                                xtype: 'radio',
	                                boxLabel: 'Por Entregar',
	                                id: PF+'optPorEntregar',
	                                name: PF+'optPorEntregar',
	                                checked: true,
	                                x: 0,
	                                y: 18,
	                                listeners:{
		              					check:{
              			   					fn:function(opt, valor){
                               					if(valor==true)
	                               				{
	                               					Ext.getCmp(PF+'optEntregado').setValue(false);
	                               				}
                              				}
                           				}
                       				}
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Empresa',
	                        x: 117,
	                        y: 242,
	                        layout: 'absolute',
	                        width: 106,
	                        height: 77,
	                        hidden: true,
	                        items: [
	                            {
	                                xtype: 'radio',
	                                boxLabel: 'Actual',
	                                id: PF+'optActual',
	                                name: PF+'optActual',
	                                checked: true,
	                                x: 0,
	                                y: 0,
	                                listeners:{
		              					check:{
              			   					fn:function(opt, valor){
                               					if(valor==true)
	                               				{
	                               					Ext.getCmp(PF+'optTodas').setValue(false);
	                               				}
                              				}
                           				}
                       				}
	                            },
	                            {
	                                xtype: 'radio',
	                                boxLabel: 'Todas',
	                                id: PF+'optTodas',
	                                name: PF+'optTodas',
	                                x: 0,
	                                y: 18,
	                                listeners:{
		              					check:{
              			   					fn:function(opt, valor){
                               					if(valor==true)
	                               				{
	                               					Ext.getCmp(PF+'optActual').setValue(false);
	                               					NS.nomEmpresa = 'REPORTE GLOBAL';
	                               				}
                              				}
                           				}
                       				}
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Criterio',
	                        width: 129,
	                        height: 77,
	                        x: 231,
	                        y: 242,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'radio',
	                                boxLabel: 'Caja',
	                                id: PF+'optCaja',
	                                name: PF+'optCaja',
	                                x: 0,
	                                y: 0,
	                                listeners:{
		              					check:{
              			   					fn:function(opt, valor){
                               					if(valor==true)
	                               				{
	                               					Ext.getCmp(PF+'optBanco').setValue(false);
	                               				}
                              				}
                           				}
                       				}
	                            },
	                            {
	                                xtype: 'radio',
	                                boxLabel: 'Banco y Chequera',
	                                id: PF+'optBanco',
	                                name: PF+'optBanco',
	                                checked: true,
	                                x: 0,
	                                y: 18,
	                                listeners:{
		              					check:{
              			   					fn:function(opt, valor){
                               					if(valor==true)
	                               				{
	                               					Ext.getCmp(PF+'optCaja').setValue(false);
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
	                xtype: 'button',
	                text: 'Imprimir',
	                x: 110,
	                y: 370,
	                width: 70,
	                listeners:{
	                	click:{
	                		fn:function(e){
	                			if(Ext.getCmp(PF+'txtDivisa').getValue() == '')
	                			{
	                				BFwrk.Util.msgShow('Debe seleccionar una divisa...','WARNING');
	                				return;
	                			}
	                			else
	                			{
	                				NS.divisa = Ext.getCmp(PF+'txtDivisa').getValue();
	                				NS.descDivisa = NS.storeDivisaVentana.getById(NS.divisa).get('descDivisa');
	                			}
							    
						    	if(Ext.getCmp(PF+'txtFechaDesde').getValue() !== '')
						     	{
						     		NS.fechaIni = Ext.getCmp(PF+'txtFechaDesde').getValue();
						     		NS.fechaFin = Ext.getCmp(PF+'txtFechaHasta').getValue();
						     		if(NS.fechaIni > NS.fechaFin)
						     		{
						     			BFwrk.Util.msgShow('La segunda fecha debe ser mayor que la primera','WARNING');
						     			return;
						     		}
						     		else
						     		{
						     			NS.fechaIni = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF+'txtFechaDesde').getValue());
						     			NS.fechaFin = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF+'txtFechaHasta').getValue());
						     		}
						     	}
						     	else
						     	{
						     		Ext.Msg.show({
										title: 'Información SET',
										msg: 'Se utilizará por default la fecha de hoy, presione Aceptar para Imprimir o Cancelar Para capturar otra fecha',
										buttons: {
											ok: true,
											no: false,
											cancel: true
										},
										icon: Ext.MessageBox.WARNING,
										fn: function(btn) {
											if(btn === 'ok')
											{
												NS.fechaIni = BFwrk.Util.changeDateToString(''+ NS.fechaHoy);
												NS.fechaFin = NS.fechaIni;
												Ext.getCmp(PF+'txtFechaDesde').setValue(NS.fechaHoy);
												Ext.getCmp(PF+'txtFechaHasta').setValue(NS.fechaHoy);
											}
											else
												return;
										}
									});
						     	}
							    /*
							    if(Ext.getCmp(PF+'optActual').getValue() == true)
							    {
							    	NS.empresaSup = NS.GI_ID_EMPRESA;
							    	NS.empresaInf = NS.GI_ID_EMPRESA;
							    	NS.nomEmpresa = NS.storeCmbEmpresa.getById(NS.empresaSup).get('nomEmpresa');
							    }
							    else
							    {
							    	NS.empresaInf = 0;
							    	NS.empresaSup = 30000;
							    	NS.nomEmpresa = 'REPORTE GLOBAL';
							    }*/
						    	
						    	if(Ext.getCmp(PF+'cmbEmpresaVentana').getValue() == '' || (Ext.getCmp(PF+'cmbEmpresaVentana').getValue() + '') == '0') {
						    		NS.empresaInf = 0;
							    	NS.empresaSup = 30000;
							    	NS.nomEmpresa = 'REPORTE GLOBAL';
						    	}else {
						    		NS.nomEmpresa = NS.storeCmbEmpresa.getById(NS.empresaSup).get('nomEmpresa');
						    	}
							    
							    if(Ext.getCmp(PF+'txtCaja').getValue() === '')
							    {
							    	NS.cajaInf = 0;
							    	NS.cajaSup = 30000;
							    }
							    else
							    {
							    	NS.cajaInf = Ext.getCmp(PF+'txtCaja').getValue();
							    	NS.cajaSup = Ext.getCmp(PF+'txtCaja').getValue();
							    }
							    
							    if(Ext.getCmp(PF+'txtBanco').getValue() === '')
							    {
							    	NS.bancoInf = 0;
							    	NS.bancoSup = 32000;
							    }
							    else 
							    {
							    	NS.bancoInf = Ext.getCmp(PF+'txtBanco').getValue();
							    	NS.bancoSup = Ext.getCmp(PF+'txtBanco').getValue();
							    }
							    
							    if(Ext.getCmp(PF+'cmbChequerasVentana').getValue() === '')
							    {
							    	NS.chequera = '%25';
							    }    
							    else
							    {
							    	NS.chequera = Ext.getCmp(PF+'cmbChequerasVentana').getValue();
							    }
							    
							    NS.buscarReporte();
	                		}
	                	}
	                }
	            },
	            {
	                xtype: 'button',
	                text: 'Limpiar',
	                x: 200,
	                y: 370,
	                width: 70,
	                listeners:{
                      	click:{
                   			fn:function(e){
                   				NS.formReportes.getForm().reset();
                   				NS.cmbCajaVentana.reset();
                   				NS.inicializaParams();
                   			}
               			}
           			}
	            },
	            {
	                xtype: 'button',
	                text: 'Cerrar',
	                x: 290,
	                y: 370,
	                width: 70,
	                listeners:{
                      	click:{
                   			fn:function(e){
                   				NS.ventanaReportes.hide();
                       		}
               			}
               		}
	            }
	        ]
	});
	
	NS.ventanaReportes = new Ext.Window({
	    title: 'Cheques Por Entregar',  
	    width: 430,
	    height: 440, 
	    modal:true,
		shadow:true,
		closeAction: 'hide',
		triggerAction: 'all',
	    layout: 'absolute',
	    id: PF+'ventanaReportes',
	    name: PF+'ventanaReportes',
	    items:NS.formReportes //le asignamos el formulario solamente  
	});  
    
    NS.contenedorChequesxEntregar.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
    //staticCheck("div [id*='gridConsulta'] div","div [id*='gridConsulta']",8,".x-grid3-scroller",false);
    
});
