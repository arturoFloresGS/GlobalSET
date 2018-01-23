/**
@author: Jessica Arelly Cruz Cruz
@since: 13/05/2011
@Modificado: Eric Medina Serrato
@Fecha: 15/12/2015
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Impresion.Impresion.ImpresionCheques');
	NS.tabContId = apps.SET.tabContainerId;
	//NS.tabContId = 'contenedorcheques';
	var PF = apps.SET.tabID+'.';
	NS.GI_USUARIO = apps.SET.iUserId ;
	//NS.GI_ID_CAJA = apps.SET.ID_CAJA;
	NS.GI_ID_CAJA = 1;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	
	NS.parametroBus = '';
	NS.banco ='0';
	NS.chequera ='';
	NS.empresa ='0';//criterio empresa
	NS.cajaPago ='0';
	NS.lote ='';
	NS.centroCostos ='0';
	NS.solicitudIni ='0';
	NS.solicitudFin ='0';
	NS.fechaIni ='';
	NS.fechaFin ='';
	NS.tipoEgreso ='';
	NS.division ='0';
	NS.importeIni ='0';
	NS.importeFin ='0';
	NS.noDocto ='';
	NS.optSeleccion = true;
	NS.optPendientes = true;
	NS.optPorConfirmar = false;
	NS.optFirmados = false;
	NS.psEquivale ='';
	NS.psNoEmpresa ='0'; // empresa que selecciono al entrar en el combo Empresa
	NS.psbImpreContinua = 'N';
	NS.numImpresora = 1;
	NS.idPersona = 0;
	NS.nomPersona = '';
		
	//criterios extras charola
	NS.bancoCharola = 0;
	NS.folioI = 0;
	NS.folioF = 0;
	
	//NS.chequeNF = false;
	NS.chequeNF = true; //Lo pongo como true ya que se quito el check y se utiliza para validaciones en business
	NS.negociable = false;
	NS.abono = false;
	
	//Variables para la impresión de folio cuando es continua
	NS.fIni = 0;
	NS.fActual = 0;
	NS.fElimina = 0;
	NS.cBanco = false;
	NS.cEmpresa = false;
	NS.cChequera = false;
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
	
	//Agregado por YEC 28.01.16
	NS.idConfiguracion='';
	
	if(NS.cmbBancoCharola)
		NS.cmbBancoCharola.destroy();
	
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
	
	/********Configuracion 2812016******/
	NS.storeConfiguracion = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		directFn: ImpresionAction.llenarComboConfiguracion, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
	}); 

	
	NS.cmbConfiguracion = new Ext.form.ComboBox({
		store: NS.storeConfiguracion
		,name: PF+'cmbConfiguracion'
		,id: PF+'cmbConfiguracion'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x:10
        ,y: 0
        ,width: 65
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: ''
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.idConfiguracion=combo.getValue();
				}
			}
		}
	});
	
	/******Configuracion 2812016******/
	
	//store empresa
	NS.storeEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:[],
		directFn: ImpresionAction.llenarComboEmpresa,
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//combo Empresa
	NS.storeEmpresa.load();
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresa
		,name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 150
        ,y: 0
        ,width: 190
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					//linea cambia combo
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresa.getId());
					if(combo.getValue() != ''){
						NS.storeBanco.baseParams.noEmpresa = parseInt(combo.getValue());
					}else{
						NS.storeBanco.baseParams.noEmpresa = 0;
					}
					
					NS.storeBanco.load();
				}
			}
		}
	});
	
	//store banco charola
	NS.storeBancoCharola = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{impresora : NS.numImpresora,
					banco : 0},
		paramOrder:['impresora','banco'],
		directFn: ImpresionAction.llenarComboBancoImpLaser,
		idProperty: 'noBanco',
		fields: [
			{name: 'noBanco'},
			{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay charolas asignadas a esta impresora');
						return;
				}else{
					NS.cmbBancoCharola.setValue(records[0].get('descBanco'));
					NS.storeFolios.baseParams.banco = records[0].get('noBanco'); 
					NS.storeFolios.load();
					//Se asigno noBanco, ya que no se permite seleccionar impresiones de distintos bancos 
					NS.bancoCharola = records[0].get('noBanco'); 
				}
			}
		}
	});
		
	NS.storeLeyenda = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:['caja'],
		directFn: ImpresionAction.llenarComboLeyenda, 
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
	
	NS.storeLeyenda.load();
	NS.cmbLeyenda = new Ext.form.ComboBox({
		store: NS.storeLeyenda
		,name: PF+'cmbLeyenda'
		,id: PF+'cmbLeyenda'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 370
        ,y: 498
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una leyenda'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.txtLeyenda.setValue(combo.getRawValue());
				}
			}
		}
	});
	
	
	//combos de los criterios
	//store banco
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{noEmpresa : 0},
		root: '',
		paramOrder:['noEmpresa'],
		directFn: ImpresionAction.llenarComboBancos, 
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
	
	//combo banco
	NS.storeBanco.load();
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 100
        ,y: 80
        ,width: 240
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
	
	NS.storeBancoCC = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{idBanco : 0},
		root: '',
		paramOrder:['idBanco'],
		directFn: ImpresionAction.llenarComboBancosCC, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No existe banco seleccionado');
						return;
				}else{
					NS.cmbBancoCharola.setValue(records[0].get('descripcion'));
					NS.storeFolios.baseParams.banco = records[0].get('id'); 
					NS.storeFolios.load();
					//Se asigno noBanco, ya que no se permite seleccionar impresiones de distintos bancos 
					NS.bancoCharola = records[0].get('id'); 
				}
			}
		}
	}); 
	
	//store chequera
	NS.storeChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idBanco: 0,
			noEmpresa: 0
		},
		root: '',
		paramOrder:['idBanco', 'noEmpresa'],
		directFn: ImpresionAction.llenarComboChequera, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	});
	
		//combo chequera
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera
		,name: PF+'cmbChequera'
		,id: PF+'cmbChequera'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 100
        ,y: 80
        ,width: 240
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
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
	
	//store criterio empresa
	NS.storeCriterioEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'no_empresa',
			campoDos:'nom_empresa',
			tabla:'empresa',
			condicion:'',
			orden:''
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
	
	//combo criterio empresa
	NS.storeCriterioEmpresa.load();
	NS.cmbCriterioEmpresa = new Ext.form.ComboBox({
		store: NS.storeCriterioEmpresa
		,name: PF+'cmbCriterioEmpresa'
		,id: PF+'cmbCriterioEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 100
        ,y: 80
        ,width: 240
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbCriterioEmpresa(combo.getValue());
				}
			}
		}
	});
	
	//store Caja de pago
	NS.storeCaja = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_caja',
			campoDos:'desc_caja',
			tabla:'cat_caja',
			condicion:'',
			orden:'desc_caja'
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
	
	//combo caja
	NS.storeCaja.load();
	NS.cmbCaja = new Ext.form.ComboBox({
		store: NS.storeCaja
		,name: PF+'cmbCaja'
		,id: PF+'cmbCaja'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 100
        ,y: 80
        ,width: 240
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una caja de pago'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbCaja(combo.getValue());	
				}
			}
		}
	});
	
	//store proveedor
	NS.storeProveedor = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {texto : ''},
		paramOrder: ['texto'],
		directFn: ImpresionAction.consultarProveedores, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProveedor, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen proveedores con ese nombre');
				}
			}
		}
	}); 
	
	//combo proveedor
	//NS.storeProveedor.load();
	NS.cmbProveedor = new Ext.form.ComboBox({
		store: NS.storeProveedor
		,name: PF+'cmbProveedor'
		,id: PF+'cmbProveedor'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 170
        ,y: 120
        ,width: 190
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un proveedor'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{ fn: function(combo, valor) {
					//linea cambia combo
					BFwrk.Util.updateComboToTextField(PF+'txtProveedor',NS.cmbProveedor.getId());
					//NS.accionarCmbDivisa(combo.getValue());
				}
			},
			beforequery: function(qe){
				NS.parametroBus=qe.combo.getRawValue();
			}
		}
	});
	
	//Este store se carga segun la clave tecleada en la caja de texto
	NS.storeUnicoBeneficiario = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'no_persona',
			campoDos:'',
			tabla:'persona',
			condicion:'',
			orden:'',
			registroUnico:false
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
		root: '',
		directFn: ImpresionAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existe el beneficiario con esa clave');
					Ext.getCmp(PF + 'txtProveedor').reset();
					return;
				}
				else{
					var reg = NS.storeUnicoBeneficiario.data.items;
					Ext.getCmp(NS.cmbProveedor.getId()).setValue(reg[0].get('descripcion'));
					NS.accionarUnicoBeneficiario(reg[0].get('id'));
				}
			}
		}
	}); 
	
	NS.accionarUnicoBeneficiario = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion');
 	}
	
 	NS.accionarBeneficiario = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeProveedor.getById(valueCombo).get('descripcion');
 	}
 	
	//data criterios
	NS.dataCriterio = [
					  [ '0', 'BANCO' ]
				     ,[ '1', 'CHEQUERA' ]
				     //,[ '2', 'EMPRESA' ]
				     //,[ '3', 'CAJA DE PAGO' ]
				     ,[ '4', 'CVE. PROPUESTA' ]
				    //,[ '5', 'CENTRO DE COSTOS' ]
				    //,[ '6', 'SOLICITUDES' ]
				     ,[ '7', 'FECHAS' ]
				     //,[ '8', 'TIPO EGRESO' ]
				     //,[ '9', 'DIVISION' ]    
				     ,[ '10', 'IMPORTE' ]
				     ,[ '11', 'NO. DOCTO.' ]
					 
					  ];
  	NS.storeCriterio = new Ext.data.SimpleStore( {
		idProperty: 'idCriterio',
		fields : [
			{name :'idCriterio'},
			{name :'descripcion'}
	 ]
	});
	NS.storeCriterio.loadData(NS.dataCriterio);
	
	//combo criterios
	NS.cmbCriterio = new Ext.form.ComboBox({
		 store: NS.storeCriterio
		,name: PF+'cmbCriterio'
		,id: PF+'cmbCriterio'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 100
		,y: 40
		,width :240
		,valueField:'idCriterio'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un criterio'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					/*if(Ext.getCmp(PF+'txtEmpresa').getValue() != '')
					{
						NS.storeDivision.baseParams.condicion = 'no_empresa = ' + Ext.getCmp(PF+'txtEmpresa').getValue();
						NS.storeDivision.load();
					}*/
					combo.setDisabled(true);
					var tamGrid=(NS.storeDatos.data.items).length;
					var datosClase = NS.gridDatos.getStore().recordType;
					
					if(combo.getValue() == 0)
					{
						
						Ext.getCmp(PF+'cmbBanco').show();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='BANCO')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó BANCO necesita borralo antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'BANCO',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
						
					}else if(combo.getValue() == 1){
						Ext.getCmp(PF+'cmbChequera').show();
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='BANCO')
								indice=i;
						}
						
						if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='BANCO'){
							
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='CHEQUERA')
									indice=i;
							}
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó CHEQUERA necesita borrala antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'CHEQUERA',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
						}else{
							Ext.Msg.alert('SET','Primero debe escoger un Banco');
							combo.setDisabled(false);
						}
					}else if(combo.getValue() == 2){
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').show();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='EMPRESA')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó EMPRESA necesita borrala antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'EMPRESA',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}else if(combo.getValue() == 3){	
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').show();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='CAJA DE PAGO')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó CAJA DE PAGO necesita borrala antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'CAJA DE PAGO',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}else if(combo.getValue() == 4){
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').show();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='CVE. PROPUESTA')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó CVE. PROPUESTA necesita borrarla antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'CVE. PROPUESTA',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}else if(combo.getValue() == 5){
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').show();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='CENTRO DE COSTOS')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó CENTRO DE COSTOS necesita borralo antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'CENTRO DE COSTOS',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}else if(combo.getValue() == 6){	
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').show();
						Ext.getCmp(PF+'txtSolicitudFin').show();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='SOLICITUDES')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó SOLICITUDES necesita borralo antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'SOLICITUDES',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}else if(combo.getValue() == 7){
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').show();
						Ext.getCmp(PF+'txtFechaFin').show();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='FECHAS')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó FECHAS necesita borralas antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'FECHAS',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}/*else if(combo.getValue() == 8){
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').show();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='TIPO EGRESO')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó TIPO EGRESO necesita borralo antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'TIPO EGRESO',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
						
					}*/
					/*else if(combo.getValue() == 9){
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						Ext.getCmp(PF+'cmbDivision').show();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='DIVISION')
								indice=i;
						}
						
						if(indice>0)
						{
							Ext.Msg.alert('SET','Ya indicó DIVISION necesita borrala antes');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'DIVISION',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}*/
					else if(combo.getValue() == 10){
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').show();
						Ext.getCmp(PF+'txtImporteFinal').show();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').hide();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='IMPORTE')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó IMPORTE necesita borralo antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'IMPORTE',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}else if(combo.getValue() == 11){
						Ext.getCmp(PF+'cmbBanco').hide();
						Ext.getCmp(PF+'cmbChequera').hide();
						Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
						Ext.getCmp(PF+'cmbCaja').hide();
						//Ext.getCmp(PF+'cmbDivision').hide();
						Ext.getCmp(PF+'txtFechaIni').hide();
						Ext.getCmp(PF+'txtFechaFin').hide();
						Ext.getCmp(PF+'txtImporteInicial').hide();
						Ext.getCmp(PF+'txtImporteFinal').hide();
						Ext.getCmp(PF+'txtCentroCosto').hide();
						Ext.getCmp(PF+'txtLote').hide();
						Ext.getCmp(PF+'txtDocto').show();
						Ext.getCmp(PF+'txtSolicitudIni').hide();
						Ext.getCmp(PF+'txtSolicitudFin').hide();
						Ext.getCmp(PF+'txtTipoEgreso').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='NO. DOCTO.')
								indice=i;
						}
						
						if(indice>0){
							Ext.Msg.alert('SET','Ya indicó NO. DOCTO. necesita borralo antes');
							combo.setDisabled(false);
							return;
						}else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'NO. DOCTO.',
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
		width: 275,
       	height: 145,
		x :390,
		y :0,
		frame: true,
		title :'',
		columns : [ 
		{
			id :'criterio',
			header :'Criterio',
			width :120,
			dataIndex :'criterio'
		}, 
		{
			header :'Valor',
			width :135,
			dataIndex :'valor'
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
	
	//store firmas
	NS.storeFirmas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{cuenta : '',
					banco : 0},
		paramOrder:['cuenta', 'banco'],
		directFn: ImpresionAction.consultarFirmasChequera, 
		idProperty: 'noPersona', 
		fields: [
			 {name: 'nombre'},
			 {name: 'ctaNo'},
			 {name: 'tipoFirma'},
			 {name: 'bDeter'},
			 {name: 'noPersona'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					//Ext.Msg.alert('SET','No existe firmas para la chequera ' );
				}
			}
		}
	}); 
	
	//check
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	//store del grid consulta
	NS.storeGridConsulta = new Ext.data.DirectStore({
		paramsAsHash: false,
		//GI_ID_CAJA, LstBusca, optSeleccion(0), Mat_Chequera, _
        //OptPendientes, OptPorConfirmar, OptFirmados, _
        //LG_b_habilita, GI_USUARIO, lmat_division, psEquivale, _
        //psNoEmpresa, psb_impre_continua
		baseParams:{
					banco:'0',
					chequera:'',
					cajaPago:'0',
					//lote:'',
					centroCostos:'0',
					solicitudIni:'0',
					solicitudFin:'0',
					fechaIni:'',
					fechaFin:'',
					//tipoEgreso:'',
					//division:'0',
					importeIni:'0',
					importeFin:'0',
					noDocto:'',
					optSeleccion: true,
					optPendientes: true,
					optPorConfirmar: false,
					optFirmados: false,
					psEquivale: NS.psEquivale,
					psNoEmpresa:'0', // empresa que selecciono al entrar en el combo Empresa
					psbImpreContinua:'',
					idCaja: NS.GI_ID_CAJA,
					cveControl:''
		},
		paramOrder:['banco','chequera','cajaPago',
					'centroCostos','solicitudIni','solicitudFin','fechaIni','fechaFin',
					'importeIni','importeFin','noDocto',
					'optSeleccion','optPendientes','optPorConfirmar','optFirmados','psEquivale',
					'psNoEmpresa','psbImpreContinua', 'idCaja','cveControl'],
		directFn: ImpresionAction.consultarChequesPendientes1,
		root: '',
		//idProperty: 'criterio',
		fields: [
			/*{name: 'noEmpresa'},
			{name: 'importe'},
			{name: 'noCliente'},
			{name: 'beneficiario'},
			{name: 'descBanco'},
			{name: 'concepto'},
			{name: 'noContrarecibo'},
			{name: 'idDivisaOriginal'},
			{name: 'ctaAlias'}*/
			
			{name: 'noEmpresa'},
			{name: 'descCaja'},
			{name: 'noSolicitud'},
			{name: 'importe'},
			{name: 'noCliente'},
			{name: 'beneficiario'},
			{name: 'descBanco'},
			{name: 'ctaAlias'},
			{name: 'descCaja'},
			{name: 'concepto'},
			{name: 'ciaNmbr'},
			{name: 'centroCosto'},
			{name: 'bcoCve'},
			{name: 'tipoEgreso'},
			{name: 'importeOriginal'},
			{name: 'noContrarecibo'},
			{name: 'rfc'},
			{name: 'moneda'},
			{name: 'idContable'},
			{name: 'ctaNo'},
			{name: 'leyendaProte'},
			{name: 'plaza'},
			{name: 'sucursal'},
			{name: 'bProtegido'},
			{name: 'noCuentaS'},
			{name: 'codigoSeguridad'},
			{name: 'cvePlaza'},
			{name: 'idBanco'},
			{name: 'ciaDir'},
			{name: 'ciaDir2'},
			{name: 'ciaRfc'},
			{name: 'idCaja'},
			{name: 'fecCheque'},
			{name: 'fecChequeStr'},
			{name: 'noCheque'},
			{name: 'firmante1'},
			{name: 'firmante2'},
			{name: 'noFirmante1'},
			{name: 'noFirmante2'},
			{name: 'equivaleBenef'},
			{name: 'idGrupo'},
			{name: 'idRubroStr'},
			{name: 'noPedido'},
			{name: 'equivaleEmpresa'},
			{name: 'idDivisaOriginal'},
			{name: 'tipoCambio'},
			{name: 'logoBanco'},
			{name: 'logoEmpresa'},
			{name: 'division'},
			{name: 'idCp'},
			{name: 'dirLogoAlterno'},
			{name: 'tipoImpresion'},
			{name: 'noChequeTmp'},
			{name: 'poHeaders'},
			{name: 'fecValor'},
			{name: 'invoiceType'},
			{name: 'cPeriodo'},
			{name: 'noFolioMov'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridConsulta, msg:"Buscando..."});
				
				//BFwrk.Util.msgWait('Termino de Imprimir', false);

				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay cheques para imprimir!');
					Ext.getCmp(PF+'cmdImprimir').setDisabled(true);
				}else{
					Ext.getCmp(PF+'cmdImprimir').setDisabled(false);
					var totalImporte=0;
          			//NS.sm.selectRange(0,records.length-1);
          			var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
          			
          			for(var k=0;k<regSelec.length;k=k+1){
          				totalImporte=totalImporte+regSelec[k].get('importe');
          			}
					Ext.getCmp(PF+'txtTotalRegistros').setValue(records.length);
					Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));
				}
			}
		}
	}); 
	
	NS.selImpresionCheques = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false,
		listeners:{	
			//selectionchange:{
			/*EMS 14/01/16 - Evento para capturar a que renglon le dieron click 
			para hacer la carga de folio impresión (Automática) cuando sea continua*/
			rowselect:{
       		   	fn:function(selectionModel, rowIndex, record){
       		   		//alert(record.get('importe'));
       		   		
       		   		//Si es impresión continua saca los folios para imprimir los cheques+
					if(NS.psbImpreContinua == 'S'){
						
						var noChequeTmp = record.get('noChequeTmp');
						
						if(noChequeTmp == '' || noChequeTmp == null || noChequeTmp == undefined){
							record.set('noChequeTmp', NS.fActual);
							NS.fActual += 1; 
						}
						
					}
					
					var sum=0;
          			var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
          			for(var k=0;k<regSelec.length;k=k+1)
          			{
          				sum=sum+regSelec[k].get('importe');
          			}

          			Ext.getCmp(PF+'txtTotalRegistros').setValue(regSelec.length);
					Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((sum)*100)/100));
					
   				}
			},
			rowdeselect:{
				fn:function(selectionModel, rowIndex, record){
					//Si es continuo el evento borra el noChequeTmp y hace un corrimiento de folios de los que ya esten. 
					if(NS.psbImpreContinua == 'S'){
						
						var noChequeTmp = record.get('noChequeTmp');
						NS.fElimina = noChequeTmp;
						//registro deseleccionado se limpia el campo.
						record.set('noChequeTmp', '');
						
						noChequeTmp = '';
						NS.fActual -= 1;
						
						if(NS.fElimina > 0){
							
							var sel = NS.gridConsulta.getSelectionModel().getSelections();
							
		          			for(var i=0; i<sel.length; i=i+1){
		          				noChequeTmp = sel[i].get('noChequeTmp');
		          				
		          				if( noChequeTmp != '' && noChequeTmp != null 
		          						&& noChequeTmp != undefined ){
		          				
		          					if(noChequeTmp > NS.fElimina){
		          						sel[i].set('noChequeTmp', noChequeTmp -1);
		          					}
		          				}
		          			}
						} 
						
					}
					
					var sum=0;
          			var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
          			for(var k=0;k<regSelec.length;k=k+1)
          			{
          				sum=sum+regSelec[k].get('importe');
          			}

          			Ext.getCmp(PF+'txtTotalRegistros').setValue(regSelec.length);
					Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((sum)*100)/100));
					
					
   				}//fin function
			}
		}
	
	});

	NS.colImpresionCheques = new Ext.grid.ColumnModel ([
	  	NS.selImpresionCheques,
		{header :'No. Empresa', width :70, sortable :true, dataIndex :'noEmpresa'},
		{header: 'No. Proveedor', width: 100, sortable: true, dataIndex: 'equivaleBenef'}, 
		{header :'Beneficiario',	width :200, sortable :true, dataIndex :'beneficiario'},
		{header :'No. Docto', width :60,	sortable :true, dataIndex :'noContrarecibo'},
		{header :'Importe', width :95, sortable :true, dataIndex :'importe', css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
		{header: 'Divisa', width: 100, sortable: false, dataIndex: 'idDivisaOriginal'},
		{header :'Concepto', width :250, sortable :true, dataIndex :'concepto'},
		{header :'Banco', width :70, sortable :true, dataIndex :'descBanco'},
		{header :'Cuenta', width :100, sortable :true, dataIndex :'ctaAlias'},
		{header :'Firmante 1', width :180, sortable :true, dataIndex :'firmante1'},
		{header :'Firmante 2', width :180, sortable :true, dataIndex :'firmante2'},
		{header :'No Firmante 1', width :100, sortable :true, dataIndex :'noFirmante1'},
		{header :'No Firmante 2', width :100, sortable :true, dataIndex :'noFirmante2'},
		{header :'No. Cheque a Imprimir', width :100, sortable :true, dataIndex :'noCheque'}
	]); 
	
	NS.gridConsulta = new Ext.grid.GridPanel({
		store : NS.storeGridConsulta,
		id : PF + 'gridConsulta'
		,name: PF + 'gridConsulta'
		,cm: NS.colImpresionCheques
      	,sm: NS.selImpresionCheques
      	,columnLines: true
      	,frame: true
		,height :200
		,x :0
		,y :0
		,title :''
	  ,listeners:{	
			click:{
       		   	fn:function(e){
       		   		/*var sum=0;
          			var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
          			for(var k=0;k<regSelec.length;k=k+1)
          			{
          				sum=sum+regSelec[k].get('importe');
          			}

          			Ext.getCmp(PF+'txtTotalRegistros').setValue(regSelec.length);
					Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((sum)*100)/100));
					*/
   				}
			}
			/*rowclick:{ fn:function(grid, rowIndex, e){ NS.gridConsulta.store.data.items[rowIndex] }}*/
		}
	});
	
	NS.accionarCmbBanco = function(comboValor){
		Ext.getCmp(PF+'cmbChequera').reset();
		
		if(Ext.getCmp(PF+'txtEmpresa').getValue() != '')
		
			NS.storeChequera.baseParams.noEmpresa = Ext.getCmp(PF+'txtEmpresa').getValue(); 
		
		else
			NS.storeChequera.baseParams.noEmpresa = 0 ; 
			
			NS.storeChequera.baseParams.idBanco = comboValor;
		
		NS.storeChequera.load();
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
	
	NS.accionarCmbCriterioEmpresa = function(comboValor){
		var empresa = NS.storeCriterioEmpresa.getById(comboValor).get('descripcion');
		var datosClase = NS.gridDatos.getStore().recordType;
		var indice=0;
		var i=0;
		var recordsDatGrid=NS.storeDatos.data.items;
		
		for(i=0;i<recordsDatGrid.length;i++)
		{
			if(recordsDatGrid[i].get('criterio')=='EMPRESA')
			{
				indice=i;
				NS.storeDatos.remove(recordsDatGrid[indice]);
			}
		}
		var datos = new datosClase({
           	criterio: 'EMPRESA',
			valor: empresa
   		});
        NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(indice, datos);
  		NS.gridDatos.getView().refresh();
  		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}
	
	NS.accionarCmbCaja = function(comboValor){
		var caja = NS.storeCaja.getById(comboValor).get('descripcion');
		var datosClase = NS.gridDatos.getStore().recordType;
		var indice=0;
		var i=0;
		var recordsDatGrid=NS.storeDatos.data.items;
		
		for(i=0;i<recordsDatGrid.length;i++)
		{
			if(recordsDatGrid[i].get('criterio')=='CAJA DE PAGO')
			{
				indice=i;
				NS.storeDatos.remove(recordsDatGrid[indice]);
			}
		}
		var datos = new datosClase({
           	criterio: 'CAJA DE PAGO',
			valor: caja
   		});
        NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(indice, datos);
  		NS.gridDatos.getView().refresh();
  		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}
	
	/*NS.accionarCmbDivision = function(comboValor){
		var division = NS.storeDivision.getById(comboValor).get('descripcion');
		var datosClase = NS.gridDatos.getStore().recordType;
		var indice=0;
		var i=0;
		var recordsDatGrid=NS.storeDatos.data.items;
		
		for(i=0;i<recordsDatGrid.length;i++)
		{
			if(recordsDatGrid[i].get('criterio')=='DIVISION')
			{
				indice=i;
				NS.storeDatos.remove(recordsDatGrid[indice]);
			}
		}
		var datos = new datosClase({
           	criterio: 'DIVISION',
			valor: division
   		});
        NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(indice, datos);
  		NS.gridDatos.getView().refresh();
  		Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
	}*/
	
	
	NS.limpiarParams = function()
	{
		NS.storeGridConsulta.baseParams.banco ='0';
		NS.storeGridConsulta.baseParams.chequera ='';
		NS.storeGridConsulta.baseParams.cveControl ='';
		NS.storeGridConsulta.baseParams.cajaPago ='0';
		NS.storeGridConsulta.baseParams.lote ='0';
		NS.storeGridConsulta.baseParams.centroCostos ='0';
		NS.storeGridConsulta.baseParams.solicitudIni ='0';
		NS.storeGridConsulta.baseParams.solicitudFin ='0';
		NS.storeGridConsulta.baseParams.fechaIni ='';
		NS.storeGridConsulta.baseParams.fechaFin ='';
		NS.storeGridConsulta.baseParams.tipoEgreso ='';
		NS.storeGridConsulta.baseParams.division ='0';
		NS.storeGridConsulta.baseParams.importeIni ='0';
		NS.storeGridConsulta.baseParams.importeFin ='0';
		NS.storeGridConsulta.baseParams.noDocto ='';
		//NS.storeGridConsulta.baseParams.optSeleccion = true;
		NS.storeGridConsulta.baseParams.optPendientes = true;
		NS.storeGridConsulta.baseParams.optPorConfirmar = false;
		NS.storeGridConsulta.baseParams.optFirmados = false;
		NS.storeGridConsulta.baseParams.psEquivale ='';
		//NS.storeGridConsulta.baseParams.psNoEmpresa ='0'; 
		NS.storeGridConsulta.baseParams.psbImpreContinua ='';
		
		NS.fIni = 0;
		NS.fActual = 0;
		NS.fElimina = 0;
		NS.cBanco = false;
		NS.cEmpresa = false;
		NS.cChequera = false;
		
	}
	
	NS.buscar = function(){
		NS.storeGridConsulta.removeAll();
		//NS.limpiarParams();

		var recordsDatosGrid=NS.storeDatos.data.items;
		if(recordsDatosGrid.length>0){
			
			for(var i=0;i<recordsDatosGrid.length;i=i+1){
				
				if(recordsDatosGrid[i].get('criterio')==='BANCO'){
					
  					var valorBanco = recordsDatosGrid[i].get('valor');
  					
  					if(valorBanco != ''){
  						
  						var recordsDataBanco = NS.storeBanco.data.items;
  						
  						for(var j=0; j < recordsDataBanco.length;j++){
  							
  							if(recordsDataBanco[j].get('descripcion')===valorBanco){
  								var idBanco=recordsDataBanco[j].get('id');
								NS.storeGridConsulta.baseParams.banco = ''+idBanco;
								NS.banco = ''+idBanco;
								NS.cBanco = true;
								break;
   							}
  						}
  					}else{
  						Ext.Msg.alert('SET','Debe agregar un valor a BANCO');
  						return;
  					}
  					
  				}else if(recordsDatosGrid[i].get('criterio')==='CHEQUERA'){
  					
 					var valorChequera=recordsDatosGrid[i].get('valor');
 					
 					if(valorChequera!=''){
 						NS.storeGridConsulta.baseParams.chequera=''+valorChequera;
 						NS.chequera=''+valorChequera;
 						NS.cChequera = true;
 					}else{
 						Ext.Msg.alert('SET','Debe agregar un valor a la chequera');
 					}
 					
 				/*}else if(recordsDatosGrid[i].get('criterio')==='EMPRESA'){
  					
 					var valorEmpresa=recordsDatosGrid[i].get('valor');
  					
 					if(valorEmpresa!=''){
  						
 						var recordsDataEmpresa=NS.storeCriterioEmpresa.data.items;
  						
 						for(var j=0;j<recordsDataEmpresa.length;j=j+1){
 							
  							if(recordsDataEmpresa[j].get('descripcion')===valorEmpresa){
  								var idEmp=recordsDataEmpresa[j].get('id');
								NS.storeGridConsulta.baseParams.empresa = ''+idEmp;
								NS.empresa = ''+idEmp;
   							}
  						}
 						
  					}else{
  						Ext.Msg.alert('SET','Debe agregar un valor a EMPRESA');
  						return;
  					}
 				*/
  				}else if(recordsDatosGrid[i].get('criterio')==='CAJA DE PAGO'){
  					
  					var valorCaja=recordsDatosGrid[i].get('valor');
  					if(valorCaja!=''){
  						var recordsDataCaja=NS.storeCaja.data.items;
  						for(var j=0;j<recordsDataCaja.length;j=j+1){
  							if(recordsDataCaja[j].get('descripcion')===valorCaja){
  								var idEmp=recordsDataCaja[j].get('id');
								NS.storeGridConsulta.baseParams.cajaPago = ''+idEmp;
								NS.cajaPago = ''+idEmp;
   							}
  						}
  					}else{
  						Ext.Msg.alert('SET','Debe agregar un valor a CAJA DE PAGO');
  						return;
  					}
  					
  				}else if(recordsDatosGrid[i].get('criterio')==='CVE. PROPUESTA'){
  					var valorLote=recordsDatosGrid[i].get('valor');
  					if(valorLote!=''){
						NS.storeGridConsulta.baseParams.cveControl = ''+valorLote;
						NS.cveControl = ''+valorLote;
  					}else{
  						Ext.Msg.alert('SET','Debe agregar un valor a CVE. PROPUESTA');
  						return;
  					}
  				}else if(recordsDatosGrid[i].get('criterio')==='CENTRO DE COSTOS'){
  					
  					var valorCentro=recordsDatosGrid[i].get('valor');
  					
  					if(valorCentro!=''){
						NS.storeGridConsulta.baseParams.centroCostos = ''+valorCentro;
						NS.centroCostos = ''+valorCentro;
  					}else{
  						Ext.Msg.alert('SET','Debe agregar un valor a CENTRO DE COSTOS');
  						return;
  					}
  					
  				}else if(recordsDatosGrid[i].get('criterio')==='SOLICITUDES'){
   					
  					var valorSol=recordsDatosGrid[i].get('valor');
   					
  					if(valorSol!=''){
   						var ini=obtenerValIni(valorSol);
   						var fin=obtenerValFin(valorSol);
   						NS.storeGridConsulta.baseParams.solicitudIni = ''+ini;
						NS.storeGridConsulta.baseParams.solicitudFin = ''+fin;
						NS.solicitudIni = ''+ini;
						NS.solicitudFin = ''+fin;
   					}else{
   						Ext.Msg.alert('SET','Debe agregar un valor a los SOLICITUDES');
   						return;
   					}
   				}else if(recordsDatosGrid[i].get('criterio')==='FECHAS'){
   					var valorFecha=recordsDatosGrid[i].get('valor');
   					if(valorFecha!=''){
   						var ini=obtenerValIni(valorFecha);
   						var fin=obtenerValFin(valorFecha);
   						NS.storeGridConsulta.baseParams.fechaIni=''+ini.replace(/^\s*|\s*$/g,"");
   						NS.storeGridConsulta.baseParams.fechaFin=''+fin.replace(/^\s*|\s*$/g,"");
   						NS.fechaIni=''+ini.replace(/^\s*|\s*$/g,"");
   						NS.fechaFin=''+fin.replace(/^\s*|\s*$/g,"");
   						
   					}else{
   						Ext.Msg.alert('SET','Debe agregar un valor a FECHAS');
   					}
   					
   				/*}else if(recordsDatosGrid[i].get('criterio')==='TIPO EGRESO'){
  					
   					var valorEgreso=recordsDatosGrid[i].get('valor');
  					
   					if(valorEgreso!=''){
						NS.storeGridConsulta.baseParams.tipoEgreso = ''+valorEgreso;
						NS.tipoEgreso = ''+valorEgreso;
  					}else{
  						Ext.Msg.alert('SET','Debe agregar un valor a TIPO EGRESO');
  						return;
  					}
  				}*/
  				/*else if(recordsDatosGrid[i].get('criterio')==='DIVISION')
   				{
   					var ValorDivision=recordsDatosGrid[i].get('valor');
   					if(ValorDivision!='')
   					{
   						var recordsDivision=NS.storeDivision.data.items;
   						for(var j=0;j<recordsDivision.length;j=j+1)
   						{
   							if(recordsDivision[j].get('descripcion')===ValorDivision)
   							{
								var idDivision=recordsDivision[j].get('id');
 								NS.storeGridConsulta.baseParams.division = ''+idDivision;
 								NS.division = ''+idDivision;
 							}
   						}
   					}
   					else
   					{
   						Ext.Msg.alert('SET','Debe agregar un valor a DIVISION');
   						return;
   					}
   					*/
   				}else if(recordsDatosGrid[i].get('criterio')==='IMPORTE'){
   					
   					var valorImporte=recordsDatosGrid[i].get('valor');
   					
   					if(valorImporte!=''){
   						var ini=obtenerValIni(valorImporte);
   						var fin=obtenerValFin(valorImporte);
   						NS.storeGridConsulta.baseParams.importeIni = NS.unformatNumber(''+ini);
						NS.storeGridConsulta.baseParams.importeFin = NS.unformatNumber(''+fin);
						NS.importeIni = NS.unformatNumber(''+ini);
						NS.importeFin = NS.unformatNumber(''+fin);
   					}else{
   						Ext.Msg.alert('SET','Debe agregar un valor al IMPORTE');
   						return;
   					}
   					
   				}else if(recordsDatosGrid[i].get('criterio')==='NO. DOCTO.'){
  					
   					var valorDcto=recordsDatosGrid[i].get('valor');
  					
   					if(valorDcto!=''){
						NS.storeGridConsulta.baseParams.noDocto = ''+valorDcto;
						NS.noDocto = ''+valorDcto;
  					}else{
  						Ext.Msg.alert('SET','Debe agregar un valor a NO. DOCTO.');
  						return;
  					}
  				}
			}
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridConsulta, msg:"Buscando..."});
			NS.storeGridConsulta.sort([{field:'equivaleBenef',direction: 'ASC'}],'ASC');
			NS.storeGridConsulta.load();
			
			//Si tiene los criterios para imprimir en continua, obtengo el ultimo impreso de la chequera
			if(NS.cEmpresa && NS.cBanco && NS.cChequera){
				
				/*ImpresionAction.obtieneUltimoCheqImp(NS.chequera, function(res, e){
					//Ext.getCmp(PF + 'txtCheque').setValue(res + '');
					//NS.ventanaNumCheque.show();
					NS.fIni = res;
					NS.fActual = res;
				});
				*/
				
				ImpresionAction.getUltimoImpCtrlCheque(NS.psNoEmpresa, NS.banco, NS.chequera, function(res, e){
					if(res == '-1'){
						Ext.Msg.alert('SET','La chequera no se encuentra registrada en Control de Cheques');
  						return;
					}else{
						NS.fIni = res;
						NS.fActual = res;
					}
					
				});
				
			}
			
		}
	}
	
	
	NS.txtLeyenda = new Ext.form.TextArea({
		id: PF + 'txtLeyenda',
		name: PF + 'txtLeyenda',
		x: 320,
		y: 525,
		//tabIndex:1,
		width:430,
		height:40
	});
	
	/*****	*****	*****	*****	*****	*****	*****	*****	*****	*****	*****/
	/*****	*****	*****	*****	      FORMULARIO		*****	*****	*****	*****/
	/*****	*****	*****	*****	*****	*****	*****	*****	*****	*****	*****/

	NS.contenedorImpCheques = new Ext.FormPanel({
	    title: 'Impresi&#243;n de Cheques',
	    width: 800,
	    height: 675,
	    padding: 10,
	    frame: true,
	    autoScroll: true,
	    layout: 'absolute',
	    renderTo: NS.tabContId, 
	    items: [
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 0,
                //width: 1010,
                height: 595,
                layout: 'absolute',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'B&#250;squeda',
                        x: 0,
                        y: 0,
                        height: 185,
                        layout: 'absolute',
                        //width: 988,
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa:',
                                x: 30,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Criterio:',
                                x: 30,
                                y: 40
                            },
                            {
                                xtype: 'label',
                                text: 'Valor:',
                                x: 30,
                                y: 80
                            },
                            {
                                xtype: 'label',
                                text: 'Proveedor:',
                                x: 30,
                                y: 120
                            },
                            {
                                xtype: 'numberfield',
                                id: PF+'txtEmpresa',
                                name: PF+'txtEmpresa',
                                x: 100,
                                y: 0,
                                width: 40,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			//linea cambia combo
		                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
		                        			
		                        			if(valor != ''){
		                        				NS.storeBanco.baseParams.noEmpresa = valor;
		                					}else{
		                						NS.storeBanco.baseParams.noEmpresa = 0;
		                					}
		                        			
		                        			NS.storeBanco.load();
		                        		}
		                        	}
		                        }
                            },
                            NS.cmbEmpresa,
                            NS.cmbCriterio,
                            NS.cmbBanco,
                            NS.cmbChequera,
                            NS.cmbCriterioEmpresa,
                            NS.cmbCaja,
                            //NS.cmbDivision,
                            {
                                xtype: 'datefield',
                                id: PF+'txtFechaIni',
                                name: PF+'txtFechaIni',
                                format: 'd/m/Y',
                                x: 100,
                                y: 80,
                                width: 100,
                                hidden: true,
                                //value: apps.SET.FEC_HOY,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;				
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='FECHAS')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
											var valorFecha='';
											if(valor!='')
											{
												valorFecha=cambiarFecha(''+valor);
											}
									    	var datos = new datosClase({
						                			criterio: 'FECHAS',
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
                                x: 220,
                                y: 80,
                                width: 100,
                                hidden: true,
                                format: 'd/m/Y',
                                id: PF+'txtFechaFin',
                                name: PF+'txtFechaFin',
                                //value: apps.SET.FEC_HOY,
                                listeners:{
                                	change:{
                                		fn: function(caja, valor){
                                			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;	
											var fechaUno=Ext.getCmp(PF+'txtFechaIni').getValue();
											var valIni='';
											if(fechaUno!='')
											{
												valIni=cambiarFecha(''+fechaUno);
											}
											if(valIni!='')
											{
												valIni+=' a ';
											}			
											Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='FECHAS')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
											var valFin='';
											if(valor!='')
											{
												valFin=cambiarFecha(''+valor);
											}
											if(fechaUno>valor)
											{
												Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
											}
											else
											{
										    	var datos = new datosClase({
							                			criterio: 'FECHAS',
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
                                xtype: 'numberfield',
                                id: PF+'txtImporteInicial',
                                name: PF+'txtImporteInicial',
                                x: 100,
                                y: 80,
                                width: 100,
                                hidden: true,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;				
											
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='IMPORTE')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
									    	var datos = new datosClase({
						                			criterio: 'IMPORTE',
													valor: NS.formatNumber(valor)
						           			});
						                    NS.gridDatos.stopEditing();
						           			NS.storeDatos.insert(indice, datos);
						           			NS.gridDatos.getView().refresh();
	                        			}
		                        	}
		                        }
                            },
                            {
                                xtype: 'numberfield',
                                id: PF+'txtImporteFinal',
                                name: PF+'txtImporteFinal',
                                x: 220,
                                y: 80,
                                width: 100,
                                hidden: true,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;		
											var valorUno=NS.formatNumber(Ext.getCmp(PF+'txtImporteInicial').getValue());
											if(valorUno!='')
												valorUno+=' a '
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='IMPORTE')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
											if(parseInt(valorUno) > parseInt(valor))
											{
												Ext.Msg.alert('SET','El importe dos debe ser mayor o igual al importe uno');
												Ext.getCmp(PF+'txtImporteInicial').setValue('');
												Ext.getCmp(PF+'txtImporteFinal').setValue('');
												return;
											}
									    	var datos = new datosClase({
						                			criterio: 'IMPORTE',
													valor: valorUno + NS.formatNumber(valor)
						           			});
						                    NS.gridDatos.stopEditing();
						           			NS.storeDatos.insert(indice, datos);
						           			NS.gridDatos.getView().refresh();
						           			
	                   						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
		                        		}
		                        	}
		                        }
                            },
                            {
                                xtype: 'numberfield',
                                id: PF+'txtCentroCosto',
                                name: PF+'txtCentroCosto',
                                x: 100,
                                y: 80,
                                width: 100,
                                hidden: true,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;				
											
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='CENTRO DE COSTOS')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
									    	var datos = new datosClase({
						                			criterio: 'CENTRO DE COSTOS',
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
                                id: PF+'txtLote',
                                name: PF+'txtLote',
                                x: 100,
                                y: 80,
                                width: 240,
                                hidden: true,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;				
											
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='CVE. PROPUESTA')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
									    	var datos = new datosClase({
						                			criterio: 'CVE. PROPUESTA',
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
                                xtype: 'numberfield',
                                id: PF+'txtDocto',
                                name: PF+'txtDocto',
                                x: 100,
                                y: 80,
                                width: 100,
                                hidden: true,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;				
											
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='NO. DOCTO.')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
									    	var datos = new datosClase({
						                			criterio: 'NO. DOCTO.',
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
                                xtype: 'numberfield',
                                id: PF+'txtSolicitudIni',
                                name: PF+'txtSolicitudIni',
                                x: 100,
                                y: 80,
                                width: 100,
                                hidden: true,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;				
											
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='SOLICITUDES')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
									    	var datos = new datosClase({
						                			criterio: 'SOLICITUDES',
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
                                xtype: 'numberfield',
                                id: PF+'txtSolicitudFin',
                                name: PF+'txtSolicitudFin',
                                x: 220,
                                y: 80,
                                width: 100,
                                hidden: true,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;		
											var valorUno=Ext.getCmp(PF+'txtSolicitudIni').getValue();
											if(valorUno!='')
												valorUno+=' a '
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='SOLICITUDES')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
											if(parseInt(valorUno) > parseInt(valor))
											{
												Ext.Msg.alert('SET','El valor dos debe ser mayor al valor uno');
												Ext.getCmp(PF+'txtSolicitudIni').setValue('');
												Ext.getCmp(PF+'txtSolicitudFin').setValue('');
												return;
											}
									    	var datos = new datosClase({
						                			criterio: 'SOLICITUDES',
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
                            {
                                xtype: 'numberfield',
                                id: PF+'txtTipoEgreso',
                                name: PF+'txtTipoEgreso',
                                x: 100,
                                y: 80,
                                width: 100,
                                hidden: true,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var indice=0;
											var i=0;
											var datosClase = NS.gridDatos.getStore().recordType;
											var recordsDatGrid=NS.storeDatos.data.items;				
											
											for(i=0;i<recordsDatGrid.length;i++)
											{
												if(recordsDatGrid[i].get('criterio')=='TIPO EGRESO')
												{
													indice=i;
													NS.storeDatos.remove(recordsDatGrid[indice]);
												}
											}
									    	var datos = new datosClase({
						                			criterio: 'TIPO EGRESO',
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
                                id: PF+'txtProveedor',
                                name: PF+'txtProveedor',
                                x: 100,
                                y: 120,
                                width: 65,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			//linea cambia combo
		                        			/*var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtProveedor',NS.cmbProveedor.getId());
		                        			if(!comboValue && Ext.getCmp(PF+'txtProveedor').getValue()!='') {
		                        				Ext.getCmp(PF+'txtProveedor').setValue('');
		                        			}*/
		                        			
		                        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
		                        				NS.storeUnicoBeneficiario.baseParams.registroUnico=true;
		                        				NS.storeUnicoBeneficiario.baseParams.condicion=''+caja.getValue();
		                        				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando Proveedor..."});
		                        				NS.storeUnicoBeneficiario.load();
		                       				}else {
		                       					NS.cmbProveedor.reset();
		                       					NS.storeUnicoBeneficiario.removeAll();
		                       				}
		                        			
		                        		}
		                        	}
		                        }
                            },
                            NS.cmbProveedor,
                            {
                                xtype: 'button',
                                text: '...',
                                x: 365,
                                y: 120,
                                width: 20,
                                id: PF+'cmdProveedor',
                                name: PF+'cmdProveedor',
                                listeners:{
		                        	click:{
		                        		fn:function(e){
		                        			
		                        			  if(NS.parametroBus==='' || NS.parametroBus.length < 4) {
												  Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
											  }else{
												  NS.storeProveedor.baseParams.texto=NS.parametroBus;
												  var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProveedor, msg:"Cargando Proveedores..."});
				                        		  NS.storeProveedor.load();
											  }
		                        		}
		                        	}
                                }
                            },
                            NS.gridDatos,
                            {
                                xtype: 'fieldset',
                                title: '-',
                                x: 390,
                                y: 140,
                                width: 270,
                                height: 55,
                                hidden: true,
                                layout: 'absolute',
                                items: [{
                                 	xtype: 'radiogroup',
						            columns: 3,
						            items: [
	                                    {
	                                        xtype: 'radio',
	                                        x: 0,
	                                        y: 0,
	                                        boxLabel: 'Pendientes',
	                                        name: PF+'optFirmas',
	                        				inputValue: 0,
	                        				listeners:{
	                        					check:{
                                           			fn:function(opt, valor)
                                           			{
                                           				if(valor==true)
                                           				{
                                           					NS.optPendientes = true,
															NS.optPorConfirmar = false,
															NS.optFirmados = false,
                                           					NS.storeGridConsulta.baseParams.optPendientes = true;
                                           				}
                                           			}
                                           		}
                                           	}
	                                    },
	                                    {
	                                        xtype: 'radio',
	                                        x: 80,
	                                        y: 0,
	                                        boxLabel: 'Por Firmar',
	                                        name: PF+'optFirmas',
	                        				inputValue: 1,
	                        				checked: true,
	                        				listeners:{
	                        					check:{
                                           			fn:function(opt, valor)
                                           			{
                                           				if(valor==true)
                                           				{
                                           					NS.optPendientes = false,
															NS.optPorConfirmar = true,
															NS.optFirmados = false,
                                           					NS.storeGridConsulta.baseParams.optPorConfirmar = true;
                                           				}
                                           			}
                                           		}
                                           	}
	                                    },
	                                    {
	                                        xtype: 'radio',
	                                        x: 160,
	                                        y: 0,
	                                        boxLabel: 'Firmados',
	                                        name: PF+'optFirmas',
	                        				inputValue: 2,
	                        				listeners:{
	                        					check:{
                                           			fn:function(opt, valor)
                                           			{
                                           				if(valor==true)
                                           				{
                                           					NS.optPendientes = false,
															NS.optPorConfirmar = false,
															NS.optFirmados = true,
                                           					NS.storeGridConsulta.baseParams.optFirmados = true;
                                           				}
                                           			}
                                           		}
                                           	}
	                                    }
                                    ]
                                }]
                            },
                            {
                                xtype: 'fieldset',
                                title: '-',
                                x: 670,
                                y: 0,
                                width: 210,
                                height: 55,
                                layout: 'absolute',
                                items: [{
                                 	xtype: 'radiogroup',
						            columns: 2,
						            items: [
	                                    {
	                                        xtype: 'radio',
	                                        x: 0,
	                                        y: 0,
	                                        boxLabel: 'Todos',
	                                        name: PF+'optSeleccion',
	                        				inputValue: 0,
	                        				checked: true,
	                        				listeners:{
	                        					check:{
                                           			fn:function(opt, valor){
                                           				
                                           				if(valor==true){
                                           					NS.storeGridConsulta.baseParams.optSeleccion = true;
                                           					NS.optSeleccion = true;
                                           				}
                                           			}
                                           		}
                                           	}
	                                    },
	                                    {
	                                        xtype: 'radio',
	                                        x: 70,
	                                        y: 0,
	                                        boxLabel: 'Hoy y mañana',
	                                        name: PF+'optSeleccion',
	                        				inputValue: 1,
	                        				listeners:{
	                        					check:{
                                           			fn:function(opt, valor){
                                           				
                                           				if(valor==true){
                                           					NS.storeGridConsulta.baseParams.optSeleccion = false;
                                           					NS.optSeleccion = false;
                                           				}
                                           			}
                                           		}
                                           	}
	                                    }
                                	]
                               	}]
                            },
                            {
                                xtype: 'button',
                                text: 'Buscar',
                                x: 800,
                                y: 110,
                                width: 80,
                                id: PF+'cmdBuscar',
                                name: PF+'cmdBuscar',
                                listeners:{
                                	click:{
                                		fn: function(e){
                                			NS.limpiarParams();
                                			Ext.getCmp(PF+'txtMontoTotal').setValue('0.00');
										    Ext.getCmp(PF+'txtTotalRegistros').setValue(0);
											var recordsP = NS.storeProveedor.data.items;
											
											if(recordsP.length > 0 || Ext.getCmp(PF+'txtProveedor').getValue() != '' ){
										    	NS.storeGridConsulta.baseParams.psEquivale = ''+Ext.getCmp(PF+'txtProveedor').getValue();
										    	NS.psEquivale = ''+Ext.getCmp(PF+'txtProveedor').getValue();
										    }else{ 
										    	NS.storeGridConsulta.baseParams.psEquivale = '';
										    	NS.psEquivale = '';
										    }
										    	
										    var psNoEmpresa = Ext.getCmp(PF+'txtEmpresa').getValue();	
										    if(psNoEmpresa === '' || psNoEmpresa == 0){
										    	NS.storeGridConsulta.baseParams.psNoEmpresa = ''+0;
										    	NS.psNoEmpresa = ''+0;
										    }else{
										    	NS.storeGridConsulta.baseParams.psNoEmpresa = ''+psNoEmpresa;
										    	NS.psNoEmpresa = ''+psNoEmpresa;
										    	NS.cEmpresa = true;
										    }
										    
										    var recordsDatosGrid=NS.storeDatos.data.items;
											if(recordsDatosGrid.length <= 0){
												var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridConsulta, msg:"Buscando..."});
												NS.storeGridConsulta.sort([{field:'equivaleBenef',direction: 'ASC'}],'ASC');
												NS.storeGridConsulta.load();
											}else{
												NS.buscar();
											}
                                		}
                                	}
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Cheques Pendientes',
                        x: 0,
                        y: 190,
                        //width: 988,
                        height: 290,
                        layout: 'absolute',
                        items: [
                        NS.gridConsulta,
                            {
                                xtype: 'label',
                                text: 'Fecha del Cheque:',
                                x: 10,
                                y: 210
                            },
                            {
                                xtype: 'label',
                                text: 'Total de Registros:',
                                x: 130,
                                y: 210
                            },
                            {
                                xtype: 'label',
                                text: 'Monto Total:',
                                x: 250,
                                y: 210
                            },
                            {
                                xtype: 'datefield',
                                x: 10,
                                y: 225,
                                width: 100,
                                format: 'd/m/Y',
                                id: PF+'txtFechaCheque',
                                name: PF+'txtFechaCheque',
                                value: apps.SET.FEC_HOY
                            },
                            {
                                xtype: 'textfield',
                                x: 130,
                                y: 225,
                                width: 100,
                                readOnly: true,
                                id: PF+'txtTotalRegistros',
                                name: PF+'txtTotalRegistros'
                            },
                            {
                                xtype: 'textfield',
                                x: 250,
                                y: 225,
                                width: 100,
                                readOnly: true,
                                id: PF+'txtMontoTotal',
                                name: PF+'txtMontoTotal'
                            }
                        ]
                    },
                    /***********************termina field de consulta*********************************/
                    {
	                    xtype: 'fieldset',
	                    title: 'Impresi&#243;n',
	                    x: 0,
	                    y: 490,
	                    width: 180,
	                    height: 60,
	                    layout: 'absolute',
	                    padding: 0,
	                    items: [{
	                     	xtype: 'radiogroup',
					       	columns: 2,
					       	items: [
	                          {
	                            xtype: 'radio',
	                            x: 0,
	                            y: -10,
	                            boxLabel: 'Continua',
	                            id: PF+'optContinua',
	                            name: PF+'optImpresion',
	              				inputValue: 0,
	              				listeners:{
	              					check:{
                   			   			fn:function(opt, valor){
                               				if(valor==true){
                               					NS.psbImpreContinua = 'S';
                               					if(NS.cEmpresa && NS.cBanco && NS.cChequera){
                               						
                               						//Si hay seleccionados en el grid y no tienen lleno el campo noChequeTmp, llenar.
                               						
                               						var sel = NS.gridConsulta.getSelectionModel().getSelections();
                               						
                               						if(sel.length >0 ){
                               							
                               							NS.fActual = NS.fIni;
                               							
                               							for(var i = 0; i < sel.length; i++){
                               								
                               								var noChequeTmp = sel[i].get('noChequeTmp');
                                    						
                                    						if(noChequeTmp == '' || noChequeTmp == null || noChequeTmp == undefined){
                                    							sel[i].set('noChequeTmp', NS.fActual);
                                    							NS.fActual += 1; 
                                    						}
                               							}
                               							
                               						}
                               						
                               						//Cargamos combo de configuraciones
                               						Ext.getCmp(PF + 'fieldConfiguracion').setDisabled(false);
                               						NS.storeConfiguracion.load();
                            						
                               					}else{
                               						Ext.Msg.alert('SET', "Para imprimir cheques continuos hay que " +
                               								"realizar una busqueda por Empresa, Banco y Chequera");
                               						
                               						NS.psbImpreContinua = 'N';
                               						Ext.getCmp(PF+'optLaser').setValue(true);
                               					}
                               					
                               				}
                               			}
                               		}
                               	}
	                          },
	                          {
	                            xtype: 'radio',
                            	x: 80,
	                            y: -10,
	                            boxLabel: 'Láser',
	                            id: PF+'optLaser',
	                            name: PF+'optImpresion',
	              				inputValue: 1,
	              				checked: true,
	              				listeners:{
	              					check:{
                               			fn:function(opt, valor){
                               				
                               				if(valor==true){
                               					NS.psbImpreContinua = 'N';
                               					NS.fActual = NS.fIni;
                               					
                               					//Quita todos los noChequeTemp que pueda tener el grid.
                               					var sel = NS.gridConsulta.getSelectionModel().getSelections();
                           						
                           						if(sel.length >0 ){
                           							for(var i = 0; i < sel.length; i++){
                                						sel[i].set('noChequeTmp', '');
                           							}
                           						}
                           						
                           						//Quitamos los valores del combo de configuración y ocultamos el field.
                           						NS.storeConfiguracion.removeAll();
                           						Ext.getCmp(PF + 'fieldConfiguracion').setDisabled(true);
                               				}
                               			}
                               		}
                          		}
	                          }
	                         ]
	                     }]
	                 },
	                 /******************COMBO CONFIGURACION********************/
	                 {
		                    xtype: 'fieldset',
		                    id: PF + 'fieldConfiguracion',
		                    title: 'Configuraci&#243;n',
		                    x: 200,
		                    y: 490,
		                    width: 100,
		                    height: 60,
		                    layout: 'absolute',
		                    padding: 0,
		                    items: [
		                            	NS.cmbConfiguracion,
		                            ]
		                 },
		                 /****************** FIN COMBO CONFIGURACION********************/
                   /* {
                        xtype: 'label',
                        text: 'Impresora:',
                        x: 200,
                        y: 500
                    },
                    NS.cmbImpresora,*/
                    {
                        xtype: 'label',
                        text: 'Leyenda:',
                        x: 320,
                        y: 500
                    },
                    NS.txtLeyenda,
                    NS.cmbLeyenda,
                    {
                    	xtype: 'checkbox',
       		        	id: PF + 'chkNegrita',
       		        	name: PF + 'chkNegrita',
       		        	x: 580,
       		        	y: 485,
       		        	boxLabel: 'Negrita',
       		        	listeners:{
       		        		change:{
       		        			fn:function(e){ }
       		        		}
       		        	}
                    },
                    {
                    	xtype: 'checkbox',
       		        	id: PF + 'chkSubrayado',
       		        	name: PF + 'chkSubrayado',
       		        	x: 580,
       		        	y: 505,
       		        	boxLabel: 'Subrayado',
       		        	listeners:{
       		        		change:{
       		        			fn:function(e){ }
       		        		}
       		        	}
                    },
                    {
                        xtype: 'button',
                        text: 'Imprimir',
                        x: 800,
                        y: 500,
                        width: 80,
                        id: PF+'cmdImprimir',
                        name: PF+'cmdImprimir',
                        listeners:{
                        	click:{
                        		fn:function(e){
                    				//var recordsImpresora = NS.storeImpresora.data.items;
                        			var recordsGrid = NS.storeGridConsulta.data.items;
                        			var registros = NS.gridConsulta.getSelectionModel().getSelections();
                        			var matrizRegistros = new Array ();
                        			var matrizCriterios = new Array ();
                       				var empresa = Ext.getCmp(PF+'txtEmpresa').getValue();
                        			var fecha = Ext.getCmp(PF+'txtFechaCheque').getValue();
                        			
                        			if(recordsGrid.length != null || recordsGrid.length > 0){
                        				
                        				if(registros.length != 0){
		                        			
                        					NS.numImpresora = 1;
                        					
                        					//Valida si es impresion continua, en caso afirmativo verifica 
                        					//que todos los registros tengan el folio del cheque a imprimir
                        					
                        					if(NS.psbImpreContinua == 'S'){
                        						
                        						for(var i=0;i<registros.length;i=i+1){
                                      				if(registros[i].get('noChequeTmp') == ''
                                      					|| registros[i].get('noChequeTmp') == null 
                                      					|| registros[i].get('noChequeTmp') == undefined){
                                      					
                                      					Ext.Msg.alert('SET','Error de impresión, '+ 
                                      							'Para impresión continua los registros deben tener folio de impresión.');
                                      					
                                      					return;
                                      				}
                                      				
                                      			}
                        						
                        					}
                        					
	                        				if(registros.length > 1){
		                        				for(var j = 1; j < registros.length; j++){
		                        					if(registros[j-1].get('bcoCve') != registros[j].get('bcoCve')){
		                        						Ext.Msg.alert('SET','Debe seleccionar registros de un mismo banco');
		                        						return;
		                        					}
			                        			}
	                        				}
	                        				
	                        				if(fecha != ''){
	                        					Ext.Msg.confirm('SET','Todos los cheques seleccionados se imprimirán con la fecha ' + cambiarFecha(''+Ext.getCmp(PF+'txtFechaCheque').getValue()) +', ' +
	                        					' ¿Desea continuar?',function(btn){  
										         	
	                        						if(btn=='yes'){
	                        							
	                        							if(NS.psbImpreContinua === 'S'){
	        		                        				if(NS.psNoEmpresa == 0 || NS.banco == 0 || NS.chequera === ''){
	        										        	Ext.Msg.alert('SET','Para la impresión continua debe hacer una busqueda por empresa, banco y chequera');
	        		                        					return;
	        										        }
	        									       	}
	        		                					//ventana de contraseña
	        							      		 	NS.ventanaConfirma.show();
	        							      		 	
	                        						}
	                        						
	                        						if(btn === 'no')
											        	return;
										        });
										        
										        /*if(!confirm('El cheque seleccionado se imprimirá con la fecha capturada, ¿desea continuar?'))
				                					return;*/
				                			}
	                        				

	                       				}else{
	                       					Ext.Msg.alert('SET','Debe seleccionar al menos un registro');
	                       					return;
	                       				}
                        				
                       				}else{
                       					Ext.Msg.alert('SET','No hay cheques para imprimir');
                        				return;
                       				}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 800,
                        y: 540,
                        width: 80,
                        id: PF+'cmdLimpiar',
                        name: PF+'cmdLimpiar',
                        listeners:{
	                  		click:{
	              			   fn:function(e){
	              			   		NS.limpiarTodo();
	              			   }
	           			   }
	       			   }
                    }
                ]
            }
        ]
	});
	
	NS.ventanaConfirma = new Ext.Window({
		title: 'Confirmación de contraseña'
		,modal:true
		,shadow:true
		,width: 230
	   	,height:150
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	   	,closeAction: 'hide'
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,layout: 'absolute'
	   	,closable: false
	   	,items: [
	   		{
                xtype: 'label',
                text: 'Contraseña:',
                x: 10,
                y: 30
            },
   			{
          		inputType: 'password',
				xtype: 'textfield',
               	id:PF+'txtPsw',
               	name:PF+'txtPsw',
               	x:90,
               	y:30,
               	width:100,
               	hidden:false,
               	allowBlank: false,
               	blankText:'Debe introducir su contraseña',
               	listeners:{
               		change:{
               			fn:function(caja,valor){
                  		}
                 	}
               	}
            }
		]
		,buttons:
			[
			   	{
				 	text: 'Aceptar',
					handler: function(e) {
						var psw = Ext.getCmp(PF+'txtPsw').getValue();
						GlobalAction.validarUsuarioAutenticado(NS.GI_USUARIO, psw, function(result, e){
							if(result != null && result != undefined){
								if(result === true){
									NS.ventanaConfirma.hide();
									Ext.getCmp(PF+'txtPsw').reset();
									if(NS.psbImpreContinua === 'N'){	//si es impresion laser muestra ventana de charolas
										var registros = NS.gridConsulta.getSelectionModel().getSelections(); 
										//NS.storeBancoCharola.baseParams.banco = registros[0].get('bcoCve');
										NS.storeBancoCC.baseParams.idBanco = registros[0].get('bcoCve');
										NS.ventanaCharola.show();
									}else {
										var regCheq = NS.gridConsulta.getSelectionModel().getSelections();
										
										if(regCheq.length > 0) {
											var chequera = regCheq[0].get('ctaAlias');
											
											Ext.getCmp(PF+'txtCheque').setValue(regCheq[0].get('noCheque'));
											//se comento porque no daba el no. de cheque real SGE
											/*ImpresionAction.obtieneUltimoCheqImp(chequera, function(res, e){
												Ext.getCmp(PF + 'txtCheque').setValue(res + '');
												NS.ventanaNumCheque.show();
											});*/
											
											ImpresionAction.getUltimoImpCtrlCheque(NS.psNoEmpresa, NS.banco, NS.chequera, function(res, e){
												
												if(res == '-1'){
													Ext.Msg.alert('SET','La chequera no se encuentra registrada en Control de Cheques');
							  						return;
												}else{
													Ext.getCmp(PF + 'txtCheque').setValue(res + '');
													NS.ventanaNumCheque.show();
												}

											});
											
										}
										//NS.ventanaNumCheque.show();
									}
									
								}else{
									Ext.Msg.alert('SET','Contraseña Incorrecta');
									Ext.getCmp(PF+'txtPsw').reset();
									return;
								}
							}
						});
					}
				},
				{
				 	text: 'Cancelar',
					handler: function(e) {
						Ext.getCmp(PF+'txtPsw').reset();
					 	NS.ventanaConfirma.hide();
					 }
		     	}
			]
	});
	
	NS.ventanaNumCheque = new Ext.Window({
		title: 'Siguiente Numero de Cheque'
		,modal:true
		,shadow:true
		,width: 250
	   	,height:150
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	   	,closeAction: 'hide'
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,layout: 'absolute'
	   	,closable: false
	   	,items: [
	   		{
                xtype: 'label',
                text: 'El siguiente número de cheque a imprimir es:',
                x: 10,
                y: 20
            },
   			{
				xtype: 'textfield',
               	id:PF+'txtCheque',
               	name:PF+'txtCheque',
               	x:60,
               	y:50,
               	width:100,
               	hidden:false,
               	allowBlank: false,
               	blankText:'',
               	readOnly: true,
               	listeners:{
               		change:{
               			fn:function(caja,valor){
						
                  		}
                 	}
               	}
            }
		]
		,buttons:
		[
		   	{
			 	text: 'Aceptar',
				handler: function(e) {
					
					var numero = Ext.getCmp(PF+'txtCheque').getValue();
					if (numero == ''){
						Ext.Msg.alert('SET','Número de cheque vacío.');
						Ext.getCmp(PF+'txtCheque').reset();
						return;
					}else{
						mascara.show();
						NS.ventanaNumCheque.hide();
						NS.imprimir();
					}
					
				}
			},
			{
			 	text: 'Cancelar',
				handler: function(e) {
					Ext.getCmp(PF+'txtCheque').reset();
				 	NS.ventanaNumCheque.hide();
				 }
	     	}
		]
	});
	
	
		NS.storeFolios = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			baseParams:{banco : 0,
						 caja : NS.GI_ID_CAJA},
			paramOrder:['banco', 'caja'],
			directFn: ImpresionAction.consultarFoliosPapel,
			idProperty: 'folioInvIni', 
			fields: [
				 {name: 'folioInvIni'},
				 {name: 'folioInvFin'},
				 {name: 'folioUltImpreso'}
			],
			listeners: {
				load: function(s, records){
					if(records.length==null || records.length<=0){
						Ext.Msg.alert('SET','No hay papel asignado para este banco');
						NS.folioI = 0;
						NS.folioF = 0;
						Ext.getCmp(PF+'txtFolioIni').setValue('');
						Ext.getCmp(PF+'txtFolioFin').setValue('');
						return;
					}else{
						
						NS.folioI = 0;
						NS.folioF = 0;
						/*for(var i = 0; i < records.length; i++){
							
							NS.folioI = parseInt(records[i].get('folioUltImpreso')) + 1;
							NS.folioF = records[i].get('folioInvFin');
						}*/
						
						//En caso de que haya más de un papel en banco se toma el último registrado.
						NS.folioI = parseInt(records[0].get('folioUltImpreso')) + 1;
						NS.folioF = records[0].get('folioInvFin');
						
						Ext.getCmp(PF+'txtFolioIni').setValue(NS.folioI);
						Ext.getCmp(PF+'txtFolioFin').setValue(NS.folioF);
					}
				}
			}
		});
	
			//combo Empresa
			//NS.storeBancoCharola.load();
			NS.cmbBancoCharola = new Ext.form.ComboBox({
				store: NS.storeBancoCC
				,name: PF+'cmbBancoCharola'
				,id: PF+'cmbBancoCharola'
				,typeAhead: true
				,mode: 'local'
				,minChars: 0
				,selecOnFocus: true
				,forceSelection: true
		        ,x: 10
		        ,y: 50
		        ,width: 110
				,valueField:'noBanco'
				,displayField:'descBanco'
				//,autocomplete: true
				,emptyText: 'Banco'
				,triggerAction: 'all'
				,value: ''
				,visible: false
				,readOnly: true
				,listeners:{
					/*select:{
						fn: 
						function(combo, valor) {
							NS.bancoCharola = combo.getValue();
							NS.storeFolios.baseParams.banco = combo.getValue();
							NS.storeFolios.load();
						}
					}
					*/
				}
			});
	
	
	NS.ventanaCharola = new Ext.Window({
		title: 'Ubicación del papel'
		,modal:true
		,shadow:true
		,width: 330
	   	,height:180
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	   	,closeAction: 'hide'
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,layout: 'absolute'
	   	,id: PF+'ventanaCharola'
	   	,name: PF+'ventanaCharola'
	   	,closable: false
	   	,items: [
	   		{
                xtype: 'label',
                text: 'Banco',
                x: 20,
                y: 30
            },
            {
                xtype: 'label',
                text: 'Folio Inicial',
                x: 140,
                y: 30
            },
            {
                xtype: 'label',
                text: 'Folio Final',
                x: 230,
                y: 30
            },
            NS.cmbBancoCharola,
   			{
				xtype: 'textfield',
               	id:PF+'txtFolioIni',
               	name:PF+'txtFolioIni',
               	readOnly: true,
               	x:130,
               	y:50,
               	width:80,
               	hidden:false,
               	listeners:{
               		change:{
               			fn:function(caja,valor){
						
                  		}
                 	}
               	}
            },	
            {
				xtype: 'textfield',
               	id:PF+'txtFolioFin',
               	name:PF+'txtFolioFin',
               	readOnly: true,
               	x:220,
               	y:50,
               	width:80,
               	hidden:false,
               	listeners:{
               		change:{
               			fn:function(caja,valor){
						
                  		}
                 	}
               	}
            }
		]
		,buttons:
			[//charolas
			   	{
				 	text: 'Ejecutar',
					handler: function(e) {
						
						if(NS.bancoCharola == 0){
							Ext.Msg.alert('SET','No ha escogido ningun banco para imprimir');
							return;
						}
						//En caso de que se haya modificado los rangos en las cajas de texto.
						if(Ext.getCmp(PF+'txtFolioIni').getValue() != '' && Ext.getCmp(PF+'txtFolioIni').getValue() != 0)
							NS.folioI = Ext.getCmp(PF+'txtFolioIni').getValue(); 
						
						if(Ext.getCmp(PF+'txtFolioFin').getValue() != '' && Ext.getCmp(PF+'txtFolioFin').getValue() != 0)
							NS.folioF = Ext.getCmp(PF+'txtFolioFin').getValue(); 
						
						
						NS.imprimir();
					}
				},
				{
				 	text: 'Cancelar',
					handler: function(e) {
					 	NS.ventanaCharola.hide();
					 	NS.cmbBancoCharola.reset();
					 	Ext.getCmp(PF+'txtFolioIni').reset();
					 	Ext.getCmp(PF+'txtFolioFin').reset();
					 }
		     	}
			]
		,listeners:{
       		show:{
       			fn:function(){
					//NS.storeBancoCharola.baseParams.impresora = NS.numImpresora
					//NS.storeBancoCharola.load();
					NS.storeBancoCC.load();
          		}
         	}
       	}
	
	});
	
	NS.imprimir = function(){
		/*Ext.MessageBox.show({
            title : 'Informaci&oacute;n SET',
            msg : 'Imprimiendo...',
            width : 300,
            wait : true,
            progress:true,
            waitConfig : {interval:200}
		});*/
		
		var registros = NS.gridConsulta.getSelectionModel().getSelections();
		var matrizRegistros = new Array ();
		var matrizCriterios = new Array ();
		//BFwrk.Util.msgWait('Imprimiendo Solicitud', true);
		
		for(var i = 0; i < registros.length; i ++){
			
			var registro = {};
			//registros seleccionados del grid
			registro.bcoCve = registros[i].get('bcoCve');
			registro.descCaja = registros[i].get('descCaja');
			registro.centroCosto = registros[i].get('centroCosto');
			registro.noEmpresa = registros[i].get('noEmpresa');
			registro.descCaja = registros[i].get('descCaja');
			registro.noSolicitud = registros[i].get('noSolicitud');
			registro.importe = registros[i].get('importe');
			registro.noCliente = registros[i].get('noCliente');
			registro.beneficiario = registros[i].get('beneficiario');
			registro.descBanco = registros[i].get('descBanco');
			registro.ctaAlias = registros[i].get('ctaAlias');
			
			registro.concepto = registros[i].get('concepto');
			registro.ciaNmbr = registros[i].get('ciaNmbr');
			registro.tipoEgreso = registros[i].get('tipoEgreso');
			registro.importeOriginal = registros[i].get('importeOriginal');
			registro.noContrarecibo = registros[i].get('noContrarecibo');
			registro.rfc = registros[i].get('rfc');
			registro.moneda = registros[i].get('moneda');
			registro.idContable = registros[i].get('idContable');
			registro.ctaNo = registros[i].get('ctaNo');
			registro.leyendaProte = registros[i].get('leyendaProte');
			registro.plaza = registros[i].get('plaza');
			registro.sucursal = registros[i].get('sucursal');
			registro.bProtegido = registros[i].get('bProtegido');
			registro.noCuentaS = registros[i].get('noCuentaS');
			registro.codigoSeguridad = registros[i].get('codigoSeguridad');
			registro.cvePlaza = registros[i].get('cvePlaza');
			registro.idBanco = registros[i].get('idBanco');
			registro.ciaDir = registros[i].get('ciaDir');
			registro.ciaDir2 = registros[i].get('ciaDir2');
			registro.ciaRfc = registros[i].get('ciaRfc');
			registro.idCaja = registros[i].get('idCaja');
			//registro.fecCheque = registros[i].get('fecCheque'); //registros[i].get('fecCheque');
			registro.fecCheque = registros[i].get('fecChequeStr');
			
			//Se comenta por que toma el que asigna el pagador y dicen que no es el correcto
			if(NS.psbImpreContinua == 'S'){
				registro.noCheque = registros[i].get('noChequeTmp');
			}else{
				registro.noCheque = registros[i].get('noCheque'); //Ext.getCmp(PF+'txtCheque').getValue();
			}
			
			//Si es continua sacar el no_cheque del grid-
			//registro.noCheque = Ext.getCmp(PF+'txtCheque').getValue(); 
			
			registro.firmante1 = registros[i].get('firmante1');
			registro.firmante2 = registros[i].get('firmante2');
			registro.noFirmante1 = registros[i].get('noFirmante1');
			registro.noFirmante2 = registros[i].get('noFirmante2');
			registro.equivaleBenef = registros[i].get('equivaleBenef');
			registro.idGrupo = registros[i].get('idGrupo');
			registro.idRubroStr = registros[i].get('idRubroStr');
			registro.noPedido = registros[i].get('noPedido');
			registro.equivaleEmpresa = registros[i].get('equivaleEmpresa');
			registro.idDivisaOriginal = registros[i].get('idDivisaOriginal');
			registro.tipoCambio = registros[i].get('tipoCambio');
			registro.logoBanco = registros[i].get('logoBanco');
			registro.logoEmpresa = registros[i].get('logoEmpresa');
			registro.division = registros[i].get('division');
			registro.idCp = registros[i].get('idCp');
			registro.dirLogoAlterno = registros[i].get('dirLogoAlterno');
			registro.tipoImpresion = registros[i].get('tipoImpresion');
			
			registro.poHeaders = registros[i].get('poHeaders');
			registro.fecValor = registros[i].get('fecValor');
			registro.invoiceType = registros[i].get('invoiceType');
			registro.cPeriodo = registros[i].get('cPeriodo');
			registro.noFolioMov = registros[i].get('noFolioMov');
			matrizRegistros[i] = registro;
		}
		
		var criterio = {};
		//criterios seleccionados para la busqueda
		criterio.banco = NS.banco;
		criterio.chequera = NS.chequera;
		//criterio.empresa = NS.empresa;//criterio empresa
		criterio.cajaPago = NS.cajaPago;
		//criterio.lote = NS.lote;
		criterio.centroCostos = NS.centroCostos;
		criterio.solicitudIni = NS.solicitudIni;
		criterio.solicitudFin = NS.solicitudFin;
		criterio.fechaIni = NS.fechaIni;
		criterio.fechaFin = NS.fechaFin;
		//criterio.tipoEgreso = NS.tipoEgreso;
		//criterio.division = NS.division;
		criterio.importeIni = NS.importeIni;
		criterio.importeFin = NS.importeFin;
		criterio.noDocto = NS.noDocto;
		criterio.optSeleccion = NS.optSeleccion;
		criterio.optPendientes = NS.optPendientes;
		criterio.optPorConfirmar = NS.optPorConfirmar;
		criterio.optFirmados = NS.optFirmados;
		criterio.psEquivale = NS.psEquivale;
		criterio.psNoEmpresa = NS.psNoEmpresa; // empresa que selecciono al entrar en el combo Empresa
		criterio.psbImpreContinua = NS.psbImpreContinua;
		criterio.numImpresora = NS.numImpresora;
		criterio.idCaja = NS.GI_ID_CAJA;
		criterio.cveControl = NS.cveControl;
		//criterios extras de la ventana charolas
		criterio.bancoCharola = NS.bancoCharola;
		criterio.folioIni = NS.folioI;
		criterio.folioFin = NS.folioF;
		
		criterio.nuevoFormato = NS.chequeNF;
		criterio.negociable = NS.negociable;
		criterio.abono = NS.abono;
		//criterio.fechaCheque = cambiarFecha( '' + Ext.getCmp(PF+'txtFechaCheque').getValue());
		criterio.fechaCheque = cambiarFecha( '' + Ext.getCmp(PF+'txtFechaCheque').getValue()).substring(0,10);
		criterio.idConfiguracion = NS.idConfiguracion; //id de configuración para impresión continua.
		
		matrizCriterios[0] = criterio;
		
		var jsonStringR = Ext.util.JSON.encode(matrizRegistros);
		var jsonStringC = Ext.util.JSON.encode(matrizCriterios);
		
		ImpresionAction.ejecutarImprimirCheques(jsonStringR, jsonStringC, function(result, e){
			
			if(result.error!==null  &&  result.error!=='' && result.error!= undefined){
				//Hay error
				mascara.hide();
				Ext.Msg.alert('SET', result.error);
				return;
			}
			else
			{
				mascara.hide();
				
				if(result.datos!==null  &&  result.datos!=='' && result.datos !== undefined)
				{
					/**	IMPRESION CONTINUA	**/
					if(NS.psbImpreContinua == 'S'){
						/** Variables para la configuracion del cheque **/
						//tl = Tipo Letra, tml = tamano letra, cx = coordenadas X, cy = coordenadas Y
						/****	TIPO LETRA	*****/
						var tlBenef, tlBenef2, tlFecha, tlImporte, tlImporte2, tlImporte3, tlImporte4, tlImporteLetra = ''; 
						var tlNoAcre, tlNomBanco, tlIdChequera, tlIdChequeraBenef, tlNumeroCheque, tlNumeroCheque2 = ''; 
						var tlDivisa, tlDocCompensacion, tlCtaContable, tlConcepto, tlConcepto2 = '';
						var tlLeyenda, tlFecha2, tlEmpresaPag, tlNombreEmpresa, tlNoDocto, tlNoDocto2, tlFactura = '';
						
						/***************************/
						
						/****	TAMAÑO LETRA	*****/
						var tmlBenef, tmlBenef2, tmlFecha, tmlImporte, tmlImporte2, tmlImporte3, tmlImporte4, tmlImporteLetra = ''; 
						var tmlNoAcre, tmlNomBanco, tmlIdChequera, tmlIdChequeraBenef, tmlNumeroCheque, tmlNumeroCheque2 = ''; 
						var tmlDivisa, tmlDocCompensacion, tmlCtaContable, tmlConcepto, tmlConcepto2 = '';
						var tmlLeyenda, tmlFecha2, tmlEmpresaPag, tmlNombreEmpresa, tmlNoDocto, tmlNoDocto2, tmlFactura = '';
						/***************************/
						
						/****	COORDENADAS X	*****/
						var cxBenef, cxBenef2, cxFecha, cxImporte, cxImporte2, cxImporte3, cxImporte4, cxImporteLetra = ''; 
						var cxNoAcre, cxNomBanco, cxIdChequera, cxIdChequeraBenef, cxNumeroCheque, cxNumeroCheque2 = ''; 
						var cxDivisa, cxDocCompensacion, cxCtaContable, cxConcepto, cxConcepto2 = '';
						var cxLeyenda, cxFecha2, cxEmpresaPag, cxNombreEmpresa, cxNoDocto, cxNoDocto2, cxFactura = '';
						/***************************/
						
						/****	COORDENADAS Y	*****/
						var cyBenef, cyBenef2, cyFecha, cyImporte, cyImporte2, cyImporte3, cyImporte4, cyImporteLetra = ''; 
						var cyNoAcre, cyNomBanco, cyIdChequera, cyIdChequeraBenef, cyNumeroCheque, cyNumeroCheque2 = ''; 
						var cyDivisa, cyDocCompensacion, cyCtaContable, cyConcepto, cyConcepto2 = '';
						var cyLeyenda, cyFecha2, cyEmpresaPag, cyNombreEmpresa, cyNoDocto, cyNoDocto2, cyFactura = '';
						/***************************/
												
						//Leyenda
						var impLeyenda = NS.cmbLeyenda.getRawValue();
						
						//configuracion del tamaño de la hoja
						var tamCorto, tamLargo, tamCarta = false;
						
						for(var j =0; j<result.conf.length;j++){
							
							switch(result.conf[j].campo){
								case 'BENEFICIARIO':
									tlBenef = result.conf[j].tipoLetra;
									tmlBenef = result.conf[j].tamanoLetra;
									cxBenef = result.conf[j].coordX;
									cyBenef = result.conf[j].coordY; 
									break;
								case 'BENEFICIARIO 2':
									tlBenef2 = result.conf[j].tipoLetra;
									tmlBenef2 = result.conf[j].tamanoLetra;
									cxBenef2 = result.conf[j].coordX;
									cyBenef2 = result.conf[j].coordY; 
									break;
								case 'FECHA DEL CHEQUE':
									tlFecha = result.conf[j].tipoLetra;
									tmlFecha = result.conf[j].tamanoLetra;
									cxFecha = result.conf[j].coordX;
									cyFecha = result.conf[j].coordY; 
									break;
								case 'FECHA DEL CHEQUE 2':
									tlFecha2 = result.conf[j].tipoLetra;
									tmlFecha2 = result.conf[j].tamanoLetra;
									cxFecha2 = result.conf[j].coordX;
									cyFecha2 = result.conf[j].coordY; 
									break;	
								case 'IMPORTE':
									tlImporte = result.conf[j].tipoLetra;
									tmlImporte = result.conf[j].tamanoLetra;
									cxImporte = result.conf[j].coordX;
									cyImporte = result.conf[j].coordY; 
									break;
								case 'IMPORTE 2':
									tlImporte2 = result.conf[j].tipoLetra;
									tmlImporte2 = result.conf[j].tamanoLetra;
									cxImporte2 = result.conf[j].coordX;
									cyImporte2 = result.conf[j].coordY; 
									break;
								case 'IMPORTE 3':
									tlImporte3 = result.conf[j].tipoLetra;
									tmlImporte3 = result.conf[j].tamanoLetra;
									cxImporte3 = result.conf[j].coordX;
									cyImporte3 = result.conf[j].coordY; 
									break;
								case 'IMPORTE 4':
									tlImporte4 = result.conf[j].tipoLetra;
									tmlImporte4 = result.conf[j].tamanoLetra;
									cxImporte4 = result.conf[j].coordX;
									cyImporte4 = result.conf[j].coordY; 
									break;	
								case 'IMPORTE EN LETRA':
									tlImporteLetra = result.conf[j].tipoLetra;
									tmlImporteLetra = result.conf[j].tamanoLetra;
									cxImporteLetra = result.conf[j].coordX;
									cyImporteLetra = result.conf[j].coordY; 
									break;	
								case 'BANCO PAGADOR':
									tlNomBanco = result.conf[j].tipoLetra;
									tmlNomBanco = result.conf[j].tamanoLetra;
									cxNomBanco = result.conf[j].coordX;
									cyNomBanco = result.conf[j].coordY; 
									break;
								case 'NUMERO ACREEDOR':
									tlNoAcre = result.conf[j].tipoLetra;
									tmlNoAcre = result.conf[j].tamanoLetra;
									cxNoAcre = result.conf[j].coordX;
									cyNoAcre = result.conf[j].coordY; 
									break;
								case 'CHEQUERA BENEFICIARIA':
									tlIdChequeraBenef = result.conf[j].tipoLetra;
									tmlIdChequeraBenef = result.conf[j].tamanoLetra;
									cxIdChequeraBenef = result.conf[j].coordX;
									cyIdChequeraBenef = result.conf[j].coordY; 
									break;	
								case 'CHEQUERA PAGADORA':
									tlIdChequera = result.conf[j].tipoLetra;
									tmlIdChequera = result.conf[j].tamanoLetra;
									cxIdChequera = result.conf[j].coordX;
									cyIdChequera = result.conf[j].coordY; 
									break;
								case 'NUMERO DE CHEQUE':
									tlNumeroCheque = result.conf[j].tipoLetra;
									tmlNumeroCheque = result.conf[j].tamanoLetra;
									cxNumeroCheque = result.conf[j].coordX;
									cyNumeroCheque = result.conf[j].coordY; 
									break;
								case 'NUMERO DE CHEQUE 2':
									tlNumeroCheque2 = result.conf[j].tipoLetra;
									tmlNumeroCheque2 = result.conf[j].tamanoLetra;
									cxNumeroCheque2 = result.conf[j].coordX;
									cyNumeroCheque2 = result.conf[j].coordY; 
									break;	
								case 'DIVISA ORIGINAL':
									tlDivisa = result.conf[j].tipoLetra;
									tmlDivisa = result.conf[j].tamanoLetra;
									cxDivisa = result.conf[j].coordX;
									cyDivisa = result.conf[j].coordY; 
									break;
								case 'DOCUMENTO COMPENSACION':
									tlDocCompensacion = result.conf[j].tipoLetra;
									tmlDocCompensacion = result.conf[j].tamanoLetra;
									cxDocCompensacion = result.conf[j].coordX;
									cyDocCompensacion = result.conf[j].coordY; 
									break;
								case 'CUENTA CONTABLE':
									tlCtaContable = result.conf[j].tipoLetra;
									tmlCtaContable = result.conf[j].tamanoLetra;
									cxCtaContable = result.conf[j].coordX;
									cyCtaContable = result.conf[j].coordY; 
									break;	
								case 'CONCEPTO':
									tlConcepto = result.conf[j].tipoLetra;
									tmlConcepto = result.conf[j].tamanoLetra;
									cxConcepto = result.conf[j].coordX;
									cyConcepto = result.conf[j].coordY; 
									break;	
								case 'CONCEPTO 2':
									tlConcepto2 = result.conf[j].tipoLetra;
									tmlConcepto2 = result.conf[j].tamanoLetra;
									cxConcepto2 = result.conf[j].coordX;
									cyConcepto2 = result.conf[j].coordY; 
									break;		
								case 'LEYENDA':
									tlLeyenda = result.conf[j].tipoLetra;
									tmlLeyenda = result.conf[j].tamanoLetra;
									cxLeyenda = result.conf[j].coordX;
									cyLeyenda = result.conf[j].coordY; 
									break;	
								case 'EMPRESA PAGADORA':
									tlEmpresaPag = result.conf[j].tipoLetra;
									tmlEmpresaPag = result.conf[j].tamanoLetra;
									cxEmpresaPag = result.conf[j].coordX;
									cyEmpresaPag = result.conf[j].coordY; 
									break;	
								case 'NOMBRE EMPRESA':
									tlNombreEmpresa = result.conf[j].tipoLetra;
									tmlNombreEmpresa = result.conf[j].tamanoLetra;
									cxNombreEmpresa = result.conf[j].coordX;
									cyNombreEmpresa = result.conf[j].coordY; 
									break;		
								case 'NUMERO DE DOCUMENTO':
									tlNoDocto = result.conf[j].tipoLetra;
									tmlNoDocto = result.conf[j].tamanoLetra;
									cxNoDocto = result.conf[j].coordX;
									cyNoDocto = result.conf[j].coordY; 
									break;	
								case 'NUMERO DE DOCUMENTO 2':
									tlNoDocto2 = result.conf[j].tipoLetra;
									tmlNoDocto2 = result.conf[j].tamanoLetra;
									cxNoDocto2 = result.conf[j].coordX;
									cyNoDocto2 = result.conf[j].coordY; 
									break;	
								case 'FACTURA':
									tlFactura = result.conf[j].tipoLetra;
									tmlFactura = result.conf[j].tamanoLetra;
									cxFactura = result.conf[j].coordX;
									cyFactura = result.conf[j].coordY; 
									break;	
							}
							
						}
						
						
						var ventana = window.open('', 'Print Panel', 'location=middle,width=200, height=200');
						ventana.document.open();
						ventana.document.write('<!DOCTYPE html>');
						ventana.document.write('<html>');
						ventana.document.write('<head>');
						ventana.document.write('<style type="text/css" media="print">');
						ventana.document.write('	@media all {' );
						ventana.document.write('		.page-break	{ display: none; }' );
						ventana.document.write('	}');
						ventana.document.write('	@media print {');
						ventana.document.write('		.page-break	{ display: block; page-break-before: always; }');
						ventana.document.write('	}');
						
						if(result.tamanoHoja.indexOf('CORTO') > 0 || result.tamanoHoja.indexOf('corto') > 0){
							ventana.document.write('	@page{');
							ventana.document.write('		size: 214.5mm 215mm; '); //ejemplos en: http://www.w3.org/TR/css3-page/#size 
							ventana.document.write('		margin: 0mm 0mm 0mm 0mm; ');
							ventana.document.write('	} ');
							
							ventana.document.write(' body');
							ventana.document.write(' { ');
							//ventana.document.write(' width: 214.5mm;');
							//ventana.document.write(' height: 233mm;');
							ventana.document.write('		 margin: 0px;  ');
							ventana.document.write(' }');
							
						}else if(result.tamanoHoja.indexOf('LARGO') > 0 || result.tamanoHoja.indexOf('largo') > 0 ){
							ventana.document.write('	@page{');
							ventana.document.write('		size: 214.5mm 278.5mm; '); //ejemplos en: http://www.w3.org/TR/css3-page/#size 
							ventana.document.write('		margin: 0mm 0mm 0mm 0mm; ');
							ventana.document.write('	} ');
							
							ventana.document.write(' body');
							ventana.document.write(' { ');
							//ventana.document.write(' width: 214.5mm;');
							//ventana.document.write(' height: 278.5mm;');
							ventana.document.write('		 margin: 0px;  ');
							ventana.document.write(' }');
							
						}else{
							//deja la configuración abierta
						}
						
						/*ventana.document.write('	@page{');
						ventana.document.write('		margin: 0mm 0mm 0mm 0mm; ');
						ventana.document.write('	} ');
						*/
						
						/*ventana.document.write('	body{ ');
						ventana.document.write('		background-color:#FFFFFF;  ');
						//ventana.document.write('		border: solid 0px black ; ');
						ventana.document.write('		 margin: 0px;  ');
						ventana.document.write('	} ');
						 */
						//ventana.document.write('body {');
						//ventana.document.write(' color: purple;');
						//ventana.document.write('background-color: #d8da3d }');
						
						for(var j = 0; j< result.datos.length; j++){
							//Para posición se utilizan centimetros y no pixeles para que no se modifique la impresión con diferentes resoluciones de pantalla.
							//Fuente se utilizan puntos para determinar el tamaño.
							ventana.document.write('#divBenef'+j+'{max-width: 400pt; position:absolute; left:'+cxBenef+'cm; top:'+cyBenef+'cm; font-size:'+tmlBenef+'pt; font-family: '+tlBenef+'; }'); //font-family
							ventana.document.write('#divBenefDos'+j+'{max-width: 300pt; position:absolute; left:'+cxBenef2+'cm; top:'+cyBenef2+'cm; font-size:'+tmlBenef2+'pt; font-family: '+tlBenef2+'; }');
							ventana.document.write('#divFecha'+j+'{max-width: 300pt; position:absolute; left:'+cxFecha+'cm; top:'+cyFecha+'cm; font-size:'+tmlFecha+'pt; font-family: '+tlFecha+'; }');
							ventana.document.write('#divFechaDos'+j+'{max-width: 300pt; position:absolute; left:'+cxFecha2+'cm; top:'+cyFecha2+'cm; font-size:'+tmlFecha2+'pt; font-family: '+tlFecha2+'; }');
							ventana.document.write('#divImporte'+j+'{max-width: 200pt; position:absolute; left:'+cxImporte+'cm; top:'+cyImporte+'cm; font-size:'+tmlImporte+'pt; font-family: '+tlImporte+';}');
							ventana.document.write('#divImporteDos'+j+'{max-width: 200pt; position:absolute; left:'+cxImporte2+'cm; top:'+cyImporte2+'cm; font-size:'+tmlImporte2+'pt; font-family: '+tlImporte2+';}');
							ventana.document.write('#divImporteTres'+j+'{max-width: 200pt; position:absolute; left:'+cxImporte3+'cm; top:'+cyImporte3+'cm; font-size:'+tmlImporte3+'pt; font-family: '+tlImporte3+';}');
							ventana.document.write('#divImporteCuatro'+j+'{max-width: 200pt; position:absolute; left:'+cxImporte4+'cm; top:'+cyImporte4+'cm; font-size:'+tmlImporte4+'pt; font-family: '+tlImporte4+';}');
							ventana.document.write('#divImporteLetra'+j+'{max-width: 600pt; position:absolute; left:'+cxImporteLetra+'cm; top:'+cyImporteLetra+'cm; font-size:'+tmlImporteLetra+'pt; font-family: '+tlImporteLetra+'; }');
							ventana.document.write('#divNomBanco'+j+'{max-width: 600pt; position:absolute; left:'+cxNomBanco+'cm; top:'+cyNomBanco+'cm; font-size:'+tmlNomBanco+'pt; font-family: '+tlNomBanco+'; }');
							
							ventana.document.write('#divNoAcre'+j+'{max-width: 600pt; position:absolute; left:'+cxNoAcre+'cm; top:'+cyNoAcre+'cm; font-size:'+tmlNoAcre+'pt; font-family: '+tlNoAcre+'; }');
							ventana.document.write('#divIdChequera'+j+'{max-width: 600pt; position:absolute; left:'+cxIdChequera+'cm; top:'+cyIdChequera+'cm; font-size:'+tmlIdChequera+'pt; font-family: '+tlIdChequera+'; }');
							ventana.document.write('#divIdChequeraBenef'+j+'{max-width: 600pt; position:absolute; left:'+cxIdChequeraBenef+'cm; top:'+cyIdChequeraBenef+'cm; font-size:'+tmlIdChequeraBenef+'pt; font-family: '+tlIdChequeraBenef+'; }');
							ventana.document.write('#divNumeroCheque'+j+'{max-width: 600pt; position:absolute; left:'+cxNumeroCheque+'cm; top:'+cyNumeroCheque+'cm; font-size:'+tmlNumeroCheque+'pt; font-family: '+tlNumeroCheque+'; }');
							ventana.document.write('#divNumeroChequeDos'+j+'{max-width: 600pt; position:absolute; left:'+cxNumeroCheque2+'cm; top:'+cyNumeroCheque2+'cm; font-size:'+tmlNumeroCheque2+'pt; font-family: '+tlNumeroCheque2+'; }');
							ventana.document.write('#divDivisa'+j+'{max-width: 600pt; position:absolute; left:'+cxDivisa+'cm; top:'+cyDivisa+'cm; font-size:'+tmlDivisa+'pt; font-family: '+tlDivisa+'; }');
							ventana.document.write('#divDocCompensacion'+j+'{max-width: 600pt; position:absolute; left:'+cxDocCompensacion+'cm; top:'+cyDocCompensacion+'cm; font-size:'+tmlDocCompensacion+'pt; font-family: '+tlDocCompensacion+'; }');
							ventana.document.write('#divCtaContable'+j+'{max-width: 600pt; position:absolute; left:'+cxCtaContable+'cm; top:'+cyCtaContable+'cm; font-size:'+tmlCtaContable+'pt; font-family: '+tlCtaContable+'; }');
							ventana.document.write('#divConcepto'+j+'{max-width: 600pt; position:absolute; left:'+cxConcepto+'cm; top:'+cyConcepto+'cm; font-size:'+tmlConcepto+'pt; font-family: '+tlConcepto+'; }');
							ventana.document.write('#divConceptoDos'+j+'{max-width: 600pt; position:absolute; left:'+cxConcepto2+'cm; top:'+cyConcepto2+'cm; font-size:'+tmlConcepto2+'pt; font-family: '+tlConcepto2+'; }');
							ventana.document.write('#divEmpresaPag'+j+'{max-width: 600pt; position:absolute; left:'+cxEmpresaPag+'cm; top:'+cyEmpresaPag+'cm; font-size:'+tmlEmpresaPag+'pt; font-family: '+tlEmpresaPag+'; }');
							ventana.document.write('#divNombreEmpresa'+j+'{max-width: 600pt; position:absolute; left:'+cxNombreEmpresa+'cm; top:'+cyNombreEmpresa+'cm; font-size:'+tmlNombreEmpresa+'pt; font-family: '+tlNombreEmpresa+'; }');
							ventana.document.write('#divNoDocto'+j+'{max-width: 600pt; position:absolute; left:'+cxNoDocto+'cm; top:'+cyNoDocto+'cm; font-size:'+tmlNoDocto+'pt; font-family: '+tlNoDocto+'; }');
							ventana.document.write('#divNoDoctoDos'+j+'{max-width: 600pt; position:absolute; left:'+cxNoDocto2+'cm; top:'+cyNoDocto2+'cm; font-size:'+tmlNoDocto2+'pt; font-family: '+tlNoDocto2+'; }');
							ventana.document.write('#divFactura'+j+'{max-width: 600pt; position:absolute; left:'+cxFactura+'cm; top:'+cyFactura+'cm; font-size:'+tmlFactura+'pt; font-family: '+tlFactura+'; }');
							
							//LEYENDA: Negrita y subrayado
							if(Ext.getCmp(PF+'chkNegrita').getValue() == true && Ext.getCmp(PF+'chkSubrayado').getValue() == true){
								ventana.document.write('#divLeyenda'+j+'{max-width: 350pt; position:absolute; left:'+cxLeyenda+'cm; top:'+(cyLeyenda)+'cm; font-size:'+tmlLeyenda+'pt; font-family: '+tlLeyenda+'; font-weight: bold; text-decoration: underline; }');
							//Solo negrita
							}else if(Ext.getCmp(PF+'chkNegrita').getValue() == true){
								ventana.document.write('#divLeyenda'+j+'{max-width: 350pt; position:absolute; left:'+cxLeyenda+'cm; top:'+(cyLeyenda)+'cm; font-size:'+tmlLeyenda+'pt; font-family: '+tlLeyenda+'; font-weight: bold; }');
							//Solo subrayado
							}else if(Ext.getCmp(PF+'chkSubrayado').getValue() == true){
								ventana.document.write('#divLeyenda'+j+'{max-width: 350pt; position:absolute; left:'+cxLeyenda+'cm; top:'+(cyLeyenda)+'cm; font-size:'+tmlLeyenda+'pt; font-family: '+tlLeyenda+'; text-decoration: underline; }');
							//Formato sin negrita ni subrayado
							}else{
								ventana.document.write('#divLeyenda'+j+'{max-width: 350pt; position:absolute; left:'+cxLeyenda+'cm; top:'+(cyLeyenda)+'cm; font-size:'+tmlLeyenda+'pt; font-family: '+tlLeyenda+'; }');
							}
							
											   
						}
						
						ventana.document.write('.hoja{width:100%; height:100%; position:relative;');
						ventana.document.write('</style>');
						ventana.document.write('</head>');
						ventana.document.write('<body>');
						
						for(var i = 0; i< result.datos.length; i++){
							//ventana:- Crea un nuevo objeto con diferente nombre (objetos dinámicos).
							//var win = window.open('', 'Print Panel', 'location=middle,width=100, height=100');
							if(i!=0)
								ventana.document.write('<div  class="page-break" > ');
							
							ventana.document.write('<div class=\"hoja\">');
							
							ventana.document.write('<BR>');
							ventana.document.write('<div id=\"divBenef'+i+'\">'+result.confLeyendas.beneficiario+' ' + result.datos[i].beneficiario+'</div>');  
							ventana.document.write('<div id=\"divBenefDos'+i+'\">'+result.confLeyendas.beneficiario2+' ' + result.datos[i].beneficiario2+'</div>');
							ventana.document.write('<div id=\"divFecha'+i+'\">'+result.confLeyendas.fechaCheque +' '+result.datos[i].fechaCheque+'</div>');
							ventana.document.write('<div id=\"divFechaDos'+i+'\">'+result.confLeyendas.fechaCheque2 +' '+result.datos[i].fechaCheque2+'</div>');
							/*ventana.document.write('<div id=\"divImporte'+i+'\">'+result.confLeyendas.importe +' '+((result.datos[i].importe > 0 && result.datos[i].importe != '' && result.datos[i].importe != undefined)? NS.formatNumber(Math.round((result.datos[i].importe)*100)/100): "") +'</div>');
							ventana.document.write('<div id=\"divImporte2'+i+'\">'+result.confLeyendas.importe2 +' '+ ((result.datos[i].importe2 > 0 && result.datos[i].importe2 != '' && result.datos[i].importe2 != undefined)? NS.formatNumber(Math.round((result.datos[i].importe2)*100)/100): "") +'</div>');
							ventana.document.write('<div id=\"divImporte3'+i+'\">'+result.confLeyendas.importe3 +' '+ ((result.datos[i].importe3 > 0 && result.datos[i].importe3 != '' && result.datos[i].importe3 != undefined)? NS.formatNumber(Math.round((result.datos[i].importe3)*100)/100): "") +'</div>');
							ventana.document.write('<div id=\"divImporte4'+i+'\">'+result.confLeyendas.importe4 +' '+ ((result.datos[i].importe4 > 0 && result.datos[i].importe4 != '' && result.datos[i].importe4 != undefined)? NS.formatNumber(Math.round((result.datos[i].importe4)*100)/100): "") +'</div>');
							*/
							ventana.document.write('<div id=\"divImporte'+i+'\">'+(result.confLeyendas.importe=='*'?'':result.confLeyendas.importe) +' '+result.datos[i].importe+'</div>');
							ventana.document.write('<div id=\"divImporteDos'+i+'\">'+(result.confLeyendas.importe2=='*'?'':result.confLeyendas.importe2) +' '+result.datos[i].importe2+'</div>');
							ventana.document.write('<div id=\"divImporteTres'+i+'\">'+(result.confLeyendas.importe3=='*'?'':result.confLeyendas.importe3) +' '+result.datos[i].importe3+'</div>');
							ventana.document.write('<div id=\"divImporteCuatro'+i+'\">'+(result.confLeyendas.importe4=='*'?'':result.confLeyendas.importe4) +' '+result.datos[i].importe4+'</div>');
							
							ventana.document.write('<div id=\"divImporteLetra'+i+'\">'+result.confLeyendas.importeLetra +' '+result.datos[i].importeLetra+'</div>');
							ventana.document.write('<div id=\"divNomBanco'+i+'\">'+result.confLeyendas.bancoPagador +' '+result.datos[i].bancoPagador+'</div>');
							ventana.document.write('<div id=\"divNoAcre'+i+'\">'+result.confLeyendas.noAcreedor +' '+result.datos[i].noAcreedor+'</div>');
							ventana.document.write('<div id=\"divIdChequera'+i+'\">'+result.confLeyendas.chequeraPag +' '+result.datos[i].chequeraPagadora+'</div>');
							ventana.document.write('<div id=\"divIdChequeraBenef'+i+'\">'+result.confLeyendas.chequeraBenef +' '+result.datos[i].chequeraBeneficiaria+'</div>');
							ventana.document.write('<div id=\"divNumeroCheque'+i+'\">'+result.confLeyendas.noCheque +' '+((result.datos[i].noCheque > 0 && result.datos[i].noCheque != '' && result.datos[i].noCheque2 != undefined)?result.datos[i].noCheque :"") +'</div>');
							ventana.document.write('<div id=\"divNumeroChequeDos'+i+'\">'+result.confLeyendas.noCheque2 +' '+((result.datos[i].noCheque2 > 0 && result.datos[i].noCheque2 != '' && result.datos[i].noCheque2 != undefined)?result.datos[i].noCheque2 :"") +'</div>');
							ventana.document.write('<div id=\"divDivisa'+i+'\">'+result.confLeyendas.divO +' '+result.datos[i].divisaOriginal+'</div>');
							ventana.document.write('<div id=\"divDocCompensacion'+i+'\">'+result.confLeyendas.docComp +' '+result.datos[i].documentoCompensacion+'</div>');
							ventana.document.write('<div id=\"divCtaContable'+i+'\">'+result.confLeyendas.ctaContable +' '+result.datos[i].cuentaContable+'</div>');
							ventana.document.write('<div id=\"divConcepto'+i+'\">'+result.confLeyendas.concepto +' '+result.datos[i].concepto+'</div>');
							ventana.document.write('<div id=\"divLeyenda'+i+'\">'+impLeyenda+'</div>');
							ventana.document.write('<div id=\"divConceptoDos'+i+'\">'+result.confLeyendas.concepto2 +' '+result.datos[i].concepto2+'</div>');
							ventana.document.write('<div id=\"divEmpresaPag'+i+'\">'+result.confLeyendas.empresaPag +' '+result.datos[i].empresaPagadora+'</div>');
							ventana.document.write('<div id=\"divNombreEmpresa'+i+'\">'+result.confLeyendas.nombreEmpresa+' '+result.datos[i].nombreEmpresa+'</div>');
							ventana.document.write('<div id=\"divNoDocto'+i+'\">'+result.confLeyendas.noDocto +' '+result.datos[i].noDocto+'</div>');
							ventana.document.write('<div id=\"divNoDoctoDos'+i+'\">'+result.confLeyendas.noDocto2 +' '+result.datos[i].noDocto2+'</div>');
							ventana.document.write('<div id=\"divFactura'+i+'\">'+result.confLeyendas.factura +' '+result.datos[i].factura+'</div>');
							
							ventana.document.write('</div>');
							if(i!=0)
								ventana.document.write('</div>');
						}//fin for
						
						ventana.document.write('</body>');
						ventana.document.write('</html>');
						ventana.document.close();
						ventana.print();
						
						if(Ext.isIE){
							window.close();
						}else {
							ventana.close();
						}
						
						if(result.msgUsuario != null && result.msgUsuario != '' && result.msgUsuario != undefined){
							Ext.Msg.alert('SET', result.msgUsuario);
						}
						
						NS.buscar();
					}
					else  // Impresion LASER
					{
						NS.ventanaCharola.hide();
						var resultado = '' + result.datos;
						//alert(result.datos);
						
						/*var win = window.open('', 'Print Panel', 'location=middle,width=100, height=100');
						win.document.open();
                        win.document.write('<!DOCTYPE html>');
                        win.document.write('<html>');
                        win.document.write('<head>');
                        win.document.write('<style type="text/css" media="print">');
                        win.document.write('	@media all {' );
	                    win.document.write('		.page-break	{ display: none; }' );
	                    win.document.write('	}');
	                    win.document.write('	@media print {');
	                    win.document.write('		.page-break	{ display: block; page-break-before: always; }');
	                    win.document.write('	}');
	                    win.document.write('</style>');
                        win.document.write('</head>');
                        win.document.write('<body>');
                        win.document.write(result.datos);
                        //-- NO VAwin.document.write('Prueba de impresion para laserrr');
                        win.document.write('</body>');
                        win.document.write('</html>');
                        win.document.close();
                        win.print();
                        
                        if(Ext.isIE){
							window.close();
						}else {
							win.close();
						}*/
                        
                        Ext.Msg.alert('SET', 'Cheques impresos correctamente');
                        //NS.buscar();
                        
                        NS.storeGridConsulta.baseParams.psEquivale = '';
				    	NS.psEquivale = '';
				    	
				    	/*var psNoEmpresa = Ext.getCmp(PF+'txtEmpresa').getValue();	
				    	NS.storeGridConsulta.baseParams.psNoEmpresa = ''+0;
				    	NS.psNoEmpresa = ''+0;*/
				    
				    	//var recordsDatosGrid=NS.storeDatos.data.items;
				    	//if(recordsDatosGrid.length <= 0){
						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridConsulta, msg:"Buscando..."});
						NS.storeGridConsulta.sort([{field:'equivaleBenef',direction: 'ASC'}],'ASC');
						NS.storeGridConsulta.load();
                        //--
					}
					
				}else{
					Ext.Msg.alert('SET', 'Ocurrió un error en el proceso de impresión.');
					return;
				}

			}
			
		});
	}
	
	NS.imprimirChe = function(){ 
		var registros = NS.gridConsulta.getSelectionModel().getSelections();
		
		for(var i = 0; i < registros.length; i ++) {
			strParams = '?nomReporte=chequeContinua';
			strParams += '&'+'nomParam1=concepto';
			strParams += '&'+'valParam1=' + registros[i].get('concepto');
			strParams += '&'+'nomParam2=importe';
			strParams += '&'+'valParam2=' + registros[i].get('importe');
			strParams += '&'+'nomParam3=noCheque';
			strParams += '&'+'valParam3=' + Ext.getCmp(PF+'txtCheque').getValue();
			strParams += '&'+'nomParam4=lugar';
			strParams += '&'+'valParam4=' + registros[i].get('ciaDir');
			strParams += '&'+'nomParam5=nomBeneficiario';
			strParams += '&'+'valParam5=' + registros[i].get('beneficiario');
			strParams += '&'+'nomParam6=importeLetra';
			strParams += '&'+'valParam6=' + registros[i].get('importeLetra');
			strParams += '&'+'nomParam7=fecCheque';
			//strParams += '&'+'valParam7=' + cambiarFecha(Ext.getCmp(PF+'txtFechaCheque').getValue() + ''); //registros[i].get('fecCheque')
			strParams += '&'+'valParam7=' + cambiarFecha(Ext.getCmp(PF+'txtFechaCheque').getValue() + '').substring(0,10);
			strParams += '&'+'nomParam8=negociable';
			strParams += '&'+'valParam8=' + Ext.getCmp(PF+'chkNegociable').getValue();
			strParams += '&'+'nomParam9=abonoCuenta';
			strParams += '&'+'valParam9=' + Ext.getCmp(PF+'chkAbono').getValue();
			strParams += '&'+'nomParam10=idGrupo';
			strParams += '&'+'valParam10=' + registros[i].get('idGrupo');
			strParams += '&'+'nomParam11=idRubro';
			strParams += '&'+'valParam11=' + registros[i].get('idRubroStr');
			strParams += '&'+'nomParam12=noEmpresa';
			strParams += '&'+'valParam12=' + registros[i].get('noEmpresa');
			strParams += '&'+'nomParam13=ctaAlias';
			strParams += '&'+'valParam13=' + registros[i].get('ctaAlias');
			strParams += '&'+'nomParam14=noDocto';
			strParams += '&'+'valParam14=' + registros[i].get('noContrarecibo');
			strParams += '&'+'nomParam15=noFolioDet';
			strParams += '&'+'valParam15=' + registros[i].get('noSolicitud');
			
			//alert("imprimirChe fecCheque = " + Ext.getCmp(PF+'txtFechaCheque').getValue());
			
			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		}
	}
	
	NS.imprimeCheque = function() {
		var registros = NS.gridConsulta.getSelectionModel().getSelections();
		var matrizRegistros = new Array ();
		var registro = {};
		
		if(registros.length <= 0) {
			Ext.Msg.alert('SET', 'Seleccione un cheque para su impresión!!');
			return;
		}
		registro.concepto = registros[0].get('concepto');
		registro.importe = registros[0].get('importe');
		registro.noCheque = Ext.getCmp(PF+'txtCheque').getValue();
		registro.lugar = registros[0].get('ciaDir');
		registro.nomBeneficiario = registros[0].get('beneficiario');
		registro.importeLetra = registros[0].get('importeLetra');
		//registro.fecCheque = cambiarFecha(Ext.getCmp(PF+'txtFechaCheque').getValue() + '');  //registros[i].get('fecCheque');
		registro.fecCheque = cambiarFecha(Ext.getCmp(PF+'txtFechaCheque').getValue() + '').substring(0,10);
		//registro.negociable = Ext.getCmp(PF+'chkNegociable').getValue();
		//registro.abonoCuenta = Ext.getCmp(PF+'chkAbono').getValue();
		registro.idGrupo = registros[0].get('idGrupo');
		registro.idRubro = registros[0].get('idRubroStr');
		registro.noEmpresa = registros[0].get('noEmpresa');
		registro.ctaAlias = registros[0].get('ctaAlias');
		registro.noDocto = registros[0].get('noContrarecibo');
		registro.noFolioDet = registros[0].get('noSolicitud');
		registro.noChequeAnt = registros[0].get('noCheque');
		registro.nuevoFormato = NS.chequeNF;
		
		matrizRegistros[0] = registro;
		
		var datosGrid = Ext.util.JSON.encode(matrizRegistros);
		
		ImpresionAction.escribirExcel(datosGrid, function(resp, e){
			Ext.Msg.alert('SET', resp);
			
			/*if(resp.substring(0, 5) != 'Error')
				NS.storeGridConsulta.load();
			*/
		});
	};
	
	
	function cambiarFecha(fecha) {
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
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
	
	/*NUEVA*/
	function cambiarFecha1(fecha) {
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		
		var mesDate = fecha.substring(0, 3);
		var dia =     fecha.substring(4,6);
		var anio =    fecha.substring(8, 12);
		
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate) {
				var mes=i+1;
					if(mes<10)
						var mes='0'+mes;
			}
		}
		var fechaString = dia+'/'+mes+'/'+anio;
		return fechaString;
	}
	
	/*function obtenerValIni(valor)
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
	}*/
	
	//Nueva variante del método obtenerValIni: EMS - 16/12/2015
	function obtenerValIni(valor){
		var valIni= valor.split('a');
		return valIni[0].replace(",",""); 
	}
	
	/*
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
		
	}*/
	
	//Nueva variante del método obtenerValFin: EMS - 16/12/2015
	function obtenerValFin(valor){
		var valFin= valor.split('a');
		return valFin[1].replace(",",""); 
	}
	
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
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,'');
	};
	
	
	NS.limpiarTodo = function(){
		NS.limpiarParams();
		
		NS.contenedorImpCheques.getForm().reset();
	   	NS.storeDatos.removeAll();
	   	NS.gridDatos.getView().refresh();
	   	NS.storeGridConsulta.removeAll();
	   	NS.gridConsulta.getView().refresh();
	   	NS.cmbProveedor.reset();
	   	NS.cmbCaja.reset();
	   	Ext.getCmp(PF+'cmbCaja').setValue('');
	   	Ext.getCmp(PF+'cmdImprimir').setDisabled(false);
	   	NS.txtLeyenda.setValue('');
	   	
	   	Ext.getCmp(PF+'txtEmpresa').setValue('');
	   	//NS.cmbEmpresa.clear();
	   	Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
		
	   	Ext.getCmp(PF+'txtFechaIni').hide();
		Ext.getCmp(PF+'txtFechaFin').hide();
		Ext.getCmp(PF+'txtImporteInicial').hide();
		Ext.getCmp(PF+'txtImporteFinal').hide();
		Ext.getCmp(PF+'txtDocto').hide();
	    Ext.getCmp(PF+'txtLote').hide();
	    
		Ext.getCmp(PF+'txtFechaIni').setValue('');
		Ext.getCmp(PF+'txtFechaFin').setValue('');
		Ext.getCmp(PF+'txtImporteInicial').setValue('');
		Ext.getCmp(PF+'txtImporteFinal').setValue('');
		Ext.getCmp(PF+'txtDocto').setValue('');
		Ext.getCmp(PF+'txtLote').setValue('');
		
		NS.cmbBanco.store.removeAll();
		NS.cmbChequera.store.removeAll();
		
		NS.cmbBanco.hide();
		NS.cmbChequera.hide();
		
		NS.storeBanco.baseParams.noEmpresa = 0;
		NS.storeBanco.load();
	   	 
	};
	
	NS.contenedorImpCheques.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	//staticCheck("#gridConsulta div div[class='x-panel-ml']","#gridConsulta div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
});