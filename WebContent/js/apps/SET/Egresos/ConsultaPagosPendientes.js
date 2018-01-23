/**
 * Vista Creada por: Luis Alfredo Serrato Montes de oca
 * 10092015
 * Editada por: Yoseline E.C
 * 03.2016
 */
Ext.onReady(function (){
	var NS = Ext.namespace('apps.SET.Egresos.Cupos.ConsultaPagosPendientes');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecHoy = apps.SET.FEC_HOY;
	NS.mascaraB= new Ext.LoadMask(Ext.getBody(), {msg:"Procesando ..."});
	NS.optRangoOrIndividualProv = 1;
	NS.optRangoOrIndividualDoc = 1;
	NS.tipoBusqueda = 'P';
		
	//Funciones iniciales
	verificaComponentesCreados();
	//iniciarCamposFechas();
	
	/*
	 * Declaracion de Variables
	 */
	
	var sociedad;
	var nombreSociedad;
	var acredor;
	var nombreAcredor;
	var importeVencido;
	var importePOrVencer;
	var formaPago;
	var importeSeleccionado;
	var importeSeleccionadoPorVencer;
	var chequera = '';
	var chequeraPagadora = '';
	var idBanco = 0;
	var idBancoPagador = 0;
	var idMatriz;
	var sumaImporte;
	var sumaImportePorVencer;
	var indiceGlobal;
	var matrizDatosGlobal = new Array();
	NS.seleccionArray = new Object();
	NS.elementoSeleccionado = null;
	NS.indiceActual = null;
	NS.importeAcumuladoFloat = 0.0;
	
	NS.importeVencidoResNivelTres = 0.0;
	NS.importeVencidoNivelTres = 0.0;
	
	NS.importePorVencerResNivelTres = 0.0;
	NS.importePorVencerNivelTres = 0.0;
	
	var matrizDatosPagos = new Object();
	var vec = new Array();
	
	/*
	 * Variales para la plantilla
	 */
	var reglaNegocio = 0;
	var fechaConIni = '';
	var fechaConFin = '';
	var fechaPropIni = '';
	var fechaPropFin = '';
	var divisa = 'MN';
	var indicadores = '';
	var numDoc = '';
	var tipoDoc = '';
	var numDocA = '';
	var numDocB = '';
	var equivalePersonaA = '';
	var equivalePersonaB = '';
	var grupoEmpresa = '';
	NS.bSeleccionar= '';
	
	NS.banderaSeleccionado=false;
	NS.sumaNoAcreedorVencido= new Array();
	NS.sumaNoAcreedorPorVencer= new Array();
	NS.noAcreedor = new Array();
	NS.noFactura = new Array();
	NS.contNoFactura=0;
	NS.contador=0;
	NS.importeTotal=0;
	
	NS.banderaVencidosSeleccionados=false;
	NS.dbClicIndxVencidos=0;
	
	var banderaV=false;
	var banderaPV=false;
	
	//variables para cargar excel
	
	NS.radioValor = '';
	NS.rangoDoctos= null;
	NS.rangoProv = null;
	NS.individualProv = false;
	NS.individualDoc = false;
	
	
	/*******************************************************************************
	 * Primer nivel, 
	 * Panel grid busqueda por Empresa
	 * Panel criterios de busqueda
	 * Componentes : Etiquetas, cajas de texto , combos (store de combo)
	 */
	
	NS.unformatNumber = function(num) {
		return num.replace(/(,)/g,''); 
	};
	
	// Declaracion de EtiquetasN1
	NS.plantilla = new Ext.form.Label({
		text: 'Plantilla:',
		x: 10,
		y: 5
	});
	
	NS.lbReglaNegTipo = new Ext.form.Label({
		text: 'Tipo regla negocio: ',
		x: 10,
		y: 35
	});
	
	NS.lblReglaNegocio = new Ext.form.Label({
		text: 'Nombre regla negocio: ',
		x: 10,
		y: 65
	});
	
	NS.lbGrupoEmpresa = new Ext.form.Label({
		text: 'Grupo empresa:',
		width: 70,
		height: 50,
		x: 10,
		y: 90
	});
	
	NS.tipoDocumento = new Ext.form.Label({
		text: 'Clase Documento:',
		width: 110,
		height: 25,
		x: 15,
		y: 125
	});
	
	NS.lblIndicadores = new Ext.form.Label({
		text: 'Indicadores:',
		width: 110,
		height: 25,
		x: 15,
		y: 155

	});
	
	NS.lblFechaRequeridaPago = new Ext.form.Label({
		text: 'Fecha Contable:',
		width: 110,
		height: 25,
		x: 15,
		y: 185
	});
	
	
	NS.lblIdSolicitantePago = new Ext.form.Label({
		text: 'Fecha Prop Pago:',
		width: 110,
		height: 25,
		x: 15,
		y: 215
	});
	
	NS.lbRangoDoc= new Ext.form.Label({
		id:PF + 'lbRangoDoc',
		text: 'Rango: ',
		x: 0,
		y: 15
	});
	
	NS.lbRangoProveedores = new Ext.form.Label({
		text: 'Rango: ',
		x: 0,
		y: 15
	});
	
	
	
	NS.lblMontoTotalPagosPendientes = new Ext.form.Label({
		text: 'MONTO TOTAL DE PAGOS PENDIENTES:',
		width: 210,
		height: 13,
		x: 100,
		y: 270
	});

	//CajasN1
	
	NS.txtTotalVencido = new Ext.form.TextField({
		id: PF + 'txtTotalVencido',
		name: PF + 'txtTotalVencido',
		x: 470,
		y: 270,
		width: 180,
		readOnly: true,
		style: {
			textAlign: 'right',
        }
	});
	
	NS.txtTotalPorVencer = new Ext.form.TextField({
		id: PF + 'txtTotalPorVencer',
		name: PF + 'txtTotalPorVencer',
		x: 655,
		y: 270,
		width: 180,
		readOnly: true,
		style: {
			textAlign: 'right',
        }
	});
	
	NS.txtNoDoc = new Ext.form.TextField({
		id: PF + 'txtNoDoc',
		name: PF + 'txtNoDoc',
		x: 720,
		y: 37,
		width: 100,
	}); //Por borrar en los action.
	
	NS.txtTipoDoc = new Ext.form.TextField({
		id: PF + 'txtTipoDoc',
		name: PF + 'txtTipoDoc',
		x: 130,
		y: 120,
		width: 100,
	});
	
	NS.txtIndicadores = new Ext.form.TextField({
		id: PF + 'txtIndicadores',
		name: PF + 'txtIndicadores',
		x: 130,
        y: 155,
		width: 100,
	});
	
	NS.txtFechaRequeridaPago = new Ext.form.DateField({
		id: PF + 'txtFechaRequeridaPago',
		name: PF + 'txtFechaRequeridaPago',
		format:'d/m/Y',
		x: 130,
		y: 185,
		width: 100
		
	});
	
	NS.txtFechaRequeridaPagoDos = new Ext.form.DateField({
		id: PF + 'txtFechaRequeridaPagoDos',
		name: PF + 'txtFechaRequeridaPagoDos',
		format:'d/m/Y',
		x: 240,
		y: 185,
		width: 100,
	});
	
	
	NS.txtIdSolicitantePago = new Ext.form.DateField({
		id: PF + 'txtIdSolicitantePago',
		name: PF + 'txtIdSolicitantePago',
		format:'d/m/Y',
		x: 130,
		y: 215,
		width: 100,
	});
	
	NS.txtIdSolicitantePagoDos = new Ext.form.DateField({
		id: PF + 'txtIdSolicitantePagoDos',
		name: PF + 'txtIdSolicitantePagoDos',
		format:'d/m/Y',
		x: 240,
		y: 215,
		width: 100,
	});
	
	NS.txtRangoDocA = new Ext.form.TextField({
		id: PF + 'txtRangoDocA',
		name: PF + 'txtRangoDocA',
		x: 35,
		y: 10,
		width: 100,
		maskRe: /[0-9]/,
	});
	
	NS.txtRangoDocB = new Ext.form.TextField({
		id: PF + 'txtRangoDocB',
		name: PF + 'txtRangoDocB',
		x: 145,
		y: 10,
		width: 100,
		maskRe: /[0-9]/,
	});
	
	NS.txtRangoProvA = new Ext.form.TextField({
		id: PF + 'txtRangoProvA',
		name: PF + 'txtRangoProvA',
		/*x: 520,
		y: 120,*/
		x:35,
		y:10,
		width: 100,
		maskRe: /[0-9]/,
	});
	
	NS.txtRangoProvB = new Ext.form.TextField({
		id: PF + 'txtRangoProvB',
		name: PF + 'txtRangoProvB',
		//x: 630,
		//y:120,
		x:145,
		y:10,
		width: 100,
		maskRe: /[0-9]/,
	});
	
	//Radio
	
	NS.radioTipoExcel= new Ext.form.RadioGroup({
		id: PF + 'radioTipoExcel',
		name: PF + 'radioTipoExcel',
		columns: 2, 
        x:310,
        y:260,
        width: 160,
		items: [
	          {boxLabel: 'Proveedor',id: PF + 'optP', name: PF+'radioTipoExcel', inputValue: 'P', checked: true },  
	          {boxLabel: 'Documento',id: PF + 'optD', name: PF+'radioTipoExcel', inputValue: 'D' }
	          //Agregar mas divisas.
	     ]
	});
	
	NS.radioTipoFiltro= new Ext.form.RadioGroup({

		id: PF + 'radioTipoFiltro',
		name: PF + 'radioTipoFiltro',
		columns: 2, 
        x:200,
        y:0,
        width: 200,
		items: [
	          
	          {boxLabel: 'Proveedor',id: PF + 'optProv', name: PF+'radioTipoFiltro', inputValue: 'P',checked: true,
	        		  listeners:{
	        			  check:{
	        				  fn:function(opt, valor)
	        				  {
	        					  if(valor==true){
	        						  if(valor==true){
	        							  NS.tipoBusqueda = 'P'
	        								  NS.cargarPagosPorEmpresa();
	        						  }
	        					  }
	        				  }
	        			  }
	        		  }},
	        		  {boxLabel: 'Interempresa',id: PF + 'optInt', name: PF+'radioTipoFiltro', inputValue: 'I',
	    	        	  listeners:{
	    	        		  check:{
	    	        			  fn:function(opt, valor)
	    	        			  {
	    	        				  if(valor==true){
	    	        					  NS.tipoBusqueda = 'I'
	    	        						  NS.cargarPagosPorEmpresa();
	    	        				  }
	    	        			  }
	    	        		  }
	    	        	  }}
	          //Agregar mas divisas.
	     ]
	});
	
	//CombosN1
	
	/***********
	 * Combo divisas
	 * *****/
	
    NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultaPagosPendientesAction.obtenerDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
				if(records.length==null || records.length <= 0){
					Ext.Msg.alert('SET','No Existen divisas');
				}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
   
    NS.cmbDivisa = new Ext.form.ComboBox({
		store:  NS.storeDivisa ,
		id: PF + 'cmbDivisa',
		name: PF + 'cmbDivisa',
		width: 105,
		x: 80,
		y: 0,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccionar Divisa',
		triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor){
					NS.cargarPagosPorEmpresa();
				}
			},	
		}
	});
	NS.storeDivisa.load();
	
	
	
	/******************
	 * Combo plantilla*
	 ******************/
	 NS.storePlantilla = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultaPagosPendientesAction.obtenerPlantillas, 
		idProperty: 'idPlantilla',
		fields: [
			 {name: 'idPlantilla'},
			 {name: 'nombrePlantilla'}
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePlantilla, msg:"Cargando..."});
				//if(records.length==null || records.length <= 0){}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	
	NS.cmbPlantilla = new Ext.form.ComboBox({
		store: NS.storePlantilla,
		id: PF + 'cmbPlantilla',
		name: PF + 'cmbPlantilla',
		width: 160,
		x: 70,
		y: 0,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idPlantilla',
		displayField: 'nombrePlantilla',
		autocomplete: true,
		emptyText: 'SELECCIONE PLANTILLA',
		triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor){
					NS.limpiarBuscar(false);
					ConsultaPagosPendientesAction.obtenerPlantilla(NS.cmbPlantilla.getValue(), function(resultado, e){
						
						if(resultado.plantilla.nombreRegla== 'S' || resultado.plantilla.nombreRegla== 'L' ){
							NS.cmbReglaNegocioUno.setValue(resultado.plantilla.nombreRegla);
						}else{
							NS.cmbReglaNegocioUno.setValue('S');
						}
						
						if(resultado.plantilla.idReglaNegocio == 0){
							NS.cmbReglaNegocioDos.setValue("");
						}else{
							NS.cmbReglaNegocioDos.setValue(resultado.plantilla.idReglaNegocio);
						}
						
						var itemdivisaAuxiliar = NS.cmbDivisa.getStore().find('idDivisa' , resultado.plantilla.divisa);
						if(itemdivisaAuxiliar!=-1){
							divisa=resultado.plantilla.divisa;
						}else{
							divisa='MN';
						}
					    
						NS.cmbDivisa.setValue(divisa);
						
						if(resultado.plantilla.indicadores == null){
							NS.txtIndicadores.setValue("");
						}else{
							NS.txtIndicadores.setValue(resultado.plantilla.indicadores);
						}
						if(resultado.plantilla.tipoDocumento == null){
							NS.txtTipoDoc.setValue("");
						}else{
							NS.txtTipoDoc.setValue(resultado.plantilla.tipoDocumento);
						}
						
						if(resultado.plantilla.grupoEmpresas == null){
							NS.cmbGrupoEmpresa.setValue("");
						}else{
							NS.cmbGrupoEmpresa.setValue(resultado.plantilla.grupoEmpresas);
							NS.txtIdEmpresa.setValue(resultado.plantilla.grupoEmpresas);
						}
						
						if(resultado.plantilla.fechaContableInicio != null && resultado.plantilla.fechaContableFin != null 
								&& resultado.plantilla.fechaPropuestaPagoInicio != null && resultado.plantilla.fechaPropuestaPagoFin != null ){
							NS.txtFechaRequeridaPago.setValue(resultado.plantilla.fechaContableInicio);
							NS.txtFechaRequeridaPagoDos.setValue(resultado.plantilla.fechaContableFin);
							NS.txtIdSolicitantePago.setValue(resultado.plantilla.fechaPropuestaPagoInicio);
							NS.txtIdSolicitantePagoDos.setValue(resultado.plantilla.fechaPropuestaPagoFin);
						}else{
							iniciarCamposFechas();
						}
						
						NS.storeLlenaGridProveedores.loadData(resultado.proveedores); 
						NS.storeLlenaGridDocumentos.loadData(resultado.documentos);
					    NS.rangoProv=Ext.util.JSON.encode(resultado.proveedores);
        	       		NS.rangoDoctos=Ext.util.JSON.encode(resultado.documentos);
        	       			
					});

				}
			}
			
		}
	});
	//NS.storePlantilla.load();
	
	/**************
	 * Combo tipo regla negocio
	 */
	
	NS.datosComboTipoReglas = [['S', 'SERVICIOS'], ['L', 'LINEAS']];	       		
	                   	 
	NS.storeTipoReglas = new Ext.data.SimpleStore({
		idProperty: 'idTipoRegla',
	    fields: [
	             {name: 'idTipoRegla'},
	             {name: 'tipoRegla'}
	    ]
	});
	NS.storeTipoReglas.loadData(NS.datosComboTipoReglas);
	
	NS.cmbReglaNegocioUno = new Ext.form.ComboBox({
		store: NS.storeTipoReglas,
		id: PF + 'cmbReglaNegocioUno',
		name: PF + 'cmbReglaNegocioUno',
		width: 160,
		x: 130,
		y: 30,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idTipoRegla',
		displayField: 'tipoRegla',
		autocomplete: true,
		emptyText: 'SELECCIONE REGLA',
		triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor){
					NS.storeReglaNegocio.baseParams.tipoRegla = combo.getValue();
					NS.storeReglaNegocio.load();
				}
			}
			
		}
	});
	
	/************
	 * Combo nombre regla de negocio
	 */
	NS.storeReglaNegocio = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{tipoRegla: 'S'},
		root: '',
		paramOrder:['tipoRegla'],
		root: '',
		directFn: ConsultaPagosPendientesAction.llenarComboReglaNegocio, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeReglaNegocio, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen reglas de negocios');
					NS.cmbReglaNegocioDos.setValue('');
				}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
	//NS.cmbReglaNegocioUno.setValue('S');
	NS.storeReglaNegocio.load();
	
	NS.cmbReglaNegocioDos = new Ext.form.ComboBox({
		store: NS.storeReglaNegocio,
		id: PF + 'cmbReglaNegocioDos',
		name: PF + 'cmbReglaNegocioDos',
		width: 190,
		x: 130,
		y: 60,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'SELECCIONE NOMBRE REGLA',
		triggerAction: 'all',
		value: '',
		//listeners:{select:{fn:function(combo, valor){}}}
	});
	
	/********************************************************
	 * 					Combo grupo empresa     			*
	 *******************************************************/
	
	NS.storeGrupoEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		root: '',
		directFn: ConsultaPagosPendientesAction.llenarComboGrupoFlujo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No existen grupos de empresas');
				}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	NS.storeGrupoEmpresa.load();
	
	NS.cmbGrupoEmpresa = new Ext.form.ComboBox({
		store: NS.storeGrupoEmpresa
		,name: PF+'cmbGrupoEmpresa'
		,id: PF+'cmbGrupoEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		, x: 180
		 ,y: 90
        ,width: 165
        ,valueField:'id'
    	,displayField:'descripcion'
		,triggerAction: 'all'
		,autocomplete:true
		,emptyText: 'Seleccione un grupo empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtIdEmpresa',NS.cmbGrupoEmpresa.getId());
				}
			}
		}
	});
	
	NS.txtIdEmpresa = new Ext.form.TextField({			
		id: PF + 'txtIdEmpresa',
		name: PF + 'txtIdEmpresa',
		x: 130,
		y: 90,
		width: 40,
		value: '',
        disabled: false,
        listeners: {
    		change: {
    			fn:function(caja,valor) {
    				BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdEmpresa', NS.cmbGrupoEmpresa.getId());
    			}
    		}
        }
	
	});
	//fin combo grupo empresas

	
	//Grids para cargar excel con rango de proveedores y documentos
	
	
	NS.storeLlenaGridProveedores  = new Ext.data.JsonStore({
		fields: ['de', 'a'],
	   fields : [ {name :'de'},{name :'a'}]
	});
	
	NS.storeLlenaGridIndividualesProveedores = new Ext.data.JsonStore({
		fields: ['de'],
		   fields : [ {name :'de'}]
		});
	
	NS.storeLlenaGridIndividualesDocumentos = new Ext.data.JsonStore({
		fields: ['de'],
		   fields : [ {name :'de'}]
		});
	
	//Grid beneficiarios
	NS.columnaSeleccionProveedor = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	
	NS.columnasProveedores = new Ext.grid.ColumnModel
	([
	  {header: 'De', width: 117, dataIndex: 'de', sortable: true},
	  {header: 'A', width: 117, dataIndex: 'a', sortable: true},
	]);
	
	
	NS.gridProveedores = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridProveedores,
		id: PF + 'gridProveedores',
		name: PF + 'gridProveedores',
		cm: NS.columnasProveedores,
		sm: NS.columnaSeleccionProveedor,
		x: 0,
		y: 40,
		width: 240,
		height: 130,
		stripRows: true,
		columnLines: true,
	});
	
	//Grid beneficiarios
	NS.columnaIndividualesSeleccionProveedor = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	
	NS.columnasIndividualesProveedores = new Ext.grid.ColumnModel
	([
	  {header: 'Proveedor', width: 200, dataIndex: 'de', sortable: true},
	]);
	
	NS.gridIndividualesProveedores = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridIndividualesProveedores,
		id: PF + 'gridIndividualesProveedores',
		name: PF + 'gridIndividualesProveedores',
		cm: NS.columnasIndividualesProveedores,
		sm: NS.columnaIndividualesSeleccionProveedor,
		x: 0,
		y: 5,
		width: 240,
		height: 165,
		stripRows: true,
		columnLines: true,
	});
	
	//Grid documentos
	
	NS.columnaIndividualesSeleccionDocumentos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	
	NS.columnasIndividualesDocumentos = new Ext.grid.ColumnModel
	([
	  {header: 'Documento', width: 200, dataIndex: 'de', sortable: true},
	]);
	
	NS.gridIndividualesDocumentos = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridIndividualesDocumentos,
		id: PF + 'gridIndividualesDocumentos',
		name: PF + 'gridIndividualesDocumentos',
		cm: NS.columnasIndividualesDocumentos,
		sm: NS.columnaIndividualesSeleccionDocumentos,
		x: 0,
		y: 5,
		width: 240,
		height: 165,
		stripRows: true,
		columnLines: true,
	});
	
	NS.storeLlenaGridDocumentos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {tipo:''},
		paramOrder: ['tipo'],
		directFn: ConfiguracionSolicitudPagoAction.llenaGridBeneficiarios,
		fields: [
		 	{name: 'de'},
		 	{name: 'a'},
		],
		listeners: {
			load: function(s, records) {
				
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}	
	});
	
	NS.columnasDocumentos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	
	NS.columnaSeleccionDocumento = new Ext.grid.ColumnModel([
	  {header: 'De', width: 117, dataIndex: 'de', sortable: true},
	  {header: 'A', width: 117, dataIndex: 'a', sortable: true},
	]);
	
	NS.gridDocumentos = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridDocumentos,
		id: PF + 'gridDocumentos',
		name: PF + 'gridDocumentos',
		cm: NS.columnaSeleccionDocumento,
		sm: NS.columnasDocumentos,
		x: 0,
		y: 40,
		width: 240,
		height: 130,
		stripRows: true,
		columnLines: true,
	});
	/*******************************************************************************
	 * Segundo nivel, 
	 * Panel grid busqueda por Proveedor
	 * Componentes : Etiquetas, cajas de texto , Botones fac.
	 */
	
	//labelN2
	NS.lblFechaPropuesta = new Ext.form.Label({
		text: 'FECHA DE PAGO:',
		x: 10,
		y: 450
	});
	
	NS.lblPgoProgramadoMN = new Ext.form.Label({
		text: 'PESOS:'+ '$0.00',
		x: 240,
		y: 475,
	});
	
	NS.lblPgoProgramadoDLS = new Ext.form.Label({
		text: 'DOLARES:' + '$0.00',
		x: 240,
		y: 505,
	});
	
	NS.lblPgoProgramado = new Ext.form.Label({
		text: 'PAGOS PROGRAMADOS' ,
		x: 240,
		y: 445
	});
	
	NS.ImporteAcumulado = new Ext.form.Label({
		text: 'IMPORTE ACUMULADO: ',
		x: 630,
		y: 445
	});
	
	NS.ImporteAcumuladoMN = new Ext.form.Label({
		text: '$0.00',
		x: 750,
		y: 445,
	});
	
	//CajasN2
	NS.txtfechaPropuesta = new Ext.form.DateField({
		id: PF + 'txtfechaPropuesta',
		name: PF + 'txtfechaPropuesta',
		format:'d/m/Y',
		editable: false,
		value: NS.fecHoy,
		minValue: NS.fecHoy,
		x: 120,
		y: 445,
		width: 110,
   		listeners:{
        	select:{
        		fn:function(caja, valor){
        			NS.controlFechas(NS.cambiarFecha(''+valor), 'select');
        			NS.insertarPagoProgramado();
				}
			}
		}
	});
	
	//BOTONN2
	
	NS.botonPropuesta = apps.SET.btnFacultativo(new Ext.Button({
      	 id: 'eppguardarPropuesta',
      	 text: 'Enviar solicitud a pago', 
      	 x: 0,
      	 y: 0,
      	 width: 80,
      	 listeners:{
      		 click:{
      			 fn:function(e){
		   
      				var datosGrid =  NS.storeLlenarGridSolicitudPagos.data.items;
      				var numeroEmpresa = datosGrid[0].get("numeroEmpresa");
      				var arrayAux =  $.map(NS.seleccionArray, function(value, index) {
      					return [value];
      				});
      				
      				if(NS.importeAcumuladoFloat != 0) { 
      					venDetalleUno.disable();
      					Ext.Msg.confirm('SET', '¿Desea realizar la solicitud de pago por el monto indicado? ' + NS.cambiarFormato(NS.importeAcumuladoFloat.toFixed(2), '$')  , function(btn){
      						if(btn == 'yes'){
      							NS.mascaraB.show();
  						   		var jSonString = Ext.util.JSON.encode(jQuery.makeArray(arrayAux));

  						   		ConsultaPagosPendientesAction.crearPropuesta(jSonString, 
  						   			numeroEmpresa, 
  						   				NS.importeAcumuladoFloat, NS.cambiarFecha(''+NS.txtfechaPropuesta.getValue()), divisa, ''+NS.tipoBusqueda,function(resultado, e){                          
  						   			NS.mascaraB.hide();
  						   			
	  						   		if (resultado != null &&
	  										resultado != undefined &&
	  										resultado != '') {
	  									Ext.Msg.alert('SET', "Se ha enviado la solicitud de pago: " + resultado + 
		  											" con un total de: " + 
		  												NS.cambiarFormato(NS.importeAcumuladoFloat.toFixed(2), '$'));
									} else {
										Ext.Msg.alert('SET', "Error al generar la propuesta");
									}
  						   			venDetalleDos.hide();
  						   			venDetalleUno.hide();
  						   			NS.storeLlenarGridPagosTotal.baseParams.reglaNegocio = reglaNegocio;
  						   			NS.storeLlenarGridPagosTotal.baseParams.fechaConIni = fechaConIni;
  						   			NS.storeLlenarGridPagosTotal.baseParams.fechaConFin = fechaConFin;
  						   			NS.storeLlenarGridPagosTotal.baseParams.fechaPropIni = fechaPropIni;
  						   			NS.storeLlenarGridPagosTotal.baseParams.fechaPropFin = fechaPropFin;
  						   			NS.storeLlenarGridPagosTotal.baseParams.divisa = divisa;
  						   			NS.storeLlenarGridPagosTotal.baseParams.indicadores = indicadores;
  						   			NS.storeLlenarGridPagosTotal.baseParams.numDoc = numDoc;
  						   			NS.storeLlenarGridPagosTotal.baseParams.tipoDoc = tipoDoc;
  						   		    NS.storeLlenarGridPagosTotal.baseParams.numDocA = numDocA;
							   		NS.storeLlenarGridPagosTotal.baseParams.numDocB = numDocB;
							   		NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaA = equivalePersonaA;
							   		NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaB = equivalePersonaB;
							   		NS.storeLlenarGridPagosTotal.baseParams.grupoEmpresa = grupoEmpresa;
							   		NS.storeLlenarGridPagosTotal.baseParams.jsonDocumentos=NS.rangoDoctos;
									NS.storeLlenarGridPagosTotal.baseParams.jsonProveedores=NS.rangoProv;
									NS.storeLlenarGridPagosTotal.baseParams.provedoresIndividual = NS.individualProv;
									NS.storeLlenarGridPagosTotal.baseParams.documentosIndividual = NS.individualDoc;
									
	   						   		var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridPagosTotal, msg: "Cargando Pagos ..."});
	              				
  						   			NS.storeLlenarGridPagosTotal.load();
  						   			NS.gridSolicitudPagosTotal.getView().refresh();
  						   		
  								
  						   		});
      						}else{
      							venDetalleUno.enable();
      						}	
      					});
      					
      				}else{
      					
      					Ext.Msg.alert('SET', 'NO SE HAN SELECCIONADO FACTURAS PARA LA PROPUESTA DE PAGO');
					 }
      					 
      			 }
      		 }   
      	 }
	
	}));
	
	
	NS.botonPropuestaS = apps.SET.btnFacultativo(new Ext.Button({
  	 id: 'eppguardarPropuestaS',
  	 text: 'Enviar solicitud a pago (No acumulativa)', 
  	 x: 200,
  	 y: 0,
  	 width: 80,
  	 listeners:{
  		 click:{
  			 fn:function(e){  				 
  				var datosGrid =  NS.storeLlenarGridSolicitudPagos.data.items;
  				var numeroEmpresa = datosGrid[0].get("numeroEmpresa");
  				var arrayAux =  $.map(NS.seleccionArray, function(value, index) {
  					return [value];
  				});
  				if(NS.importeAcumuladoFloat != 0) { 
  					venDetalleUno.disable();
  					Ext.Msg.confirm('SET', '¿Desea realizar la solicitud de pago por el monto indicado? ' + NS.cambiarFormato(NS.importeAcumuladoFloat.toFixed(2), '$')  , function(btn){
  					 if(btn == 'yes'){
  						NS.mascaraB.show();
  						 var jSonString = Ext.util.JSON.encode(jQuery.makeArray(arrayAux));
  						 
  							 ConsultaPagosPendientesAction.crearPropuestaSimple(jSonString, 
  									numeroEmpresa, 
  									 NS.importeAcumuladoFloat, NS.cambiarFecha(''+NS.txtfechaPropuesta.getValue()), divisa, ''+NS.tipoBusqueda,function(resultado, e) {  
  								NS.mascaraB.hide();
  								 
  								 if (resultado != null &&
  										resultado != undefined &&
  										resultado != '') {
  									Ext.Msg.alert('SET', "Se ha enviado la solicitud de pago: " + resultado + 
	  											" con un total de: " + 
	  												NS.cambiarFormato(NS.importeAcumuladoFloat.toFixed(2), '$'));
								} else {
									Ext.Msg.alert('SET', "Error al generar la propuesta");
								}
  								
  			  					 
  								 venDetalleDos.hide();
  								 venDetalleUno.hide();
  								 NS.storeLlenarGridPagosTotal.baseParams.reglaNegocio = reglaNegocio;
  								 NS.storeLlenarGridPagosTotal.baseParams.fechaConIni = fechaConIni;
  								 NS.storeLlenarGridPagosTotal.baseParams.fechaConFin = fechaConFin;
  								 NS.storeLlenarGridPagosTotal.baseParams.fechaPropIni = fechaPropIni;
  								 NS.storeLlenarGridPagosTotal.baseParams.fechaPropFin = fechaPropFin;
  								 NS.storeLlenarGridPagosTotal.baseParams.divisa = divisa;
  								 NS.storeLlenarGridPagosTotal.baseParams.indicadores = indicadores;
  								 NS.storeLlenarGridPagosTotal.baseParams.numDoc = numDoc;
  								 NS.storeLlenarGridPagosTotal.baseParams.tipoDoc = tipoDoc;
  								 NS.storeLlenarGridPagosTotal.baseParams.numDocA = numDocA;
							   	 NS.storeLlenarGridPagosTotal.baseParams.numDocB = numDocB;
							   	 NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaA = equivalePersonaA;
							   	 NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaB = equivalePersonaB;
							   	 NS.storeLlenarGridPagosTotal.baseParams.grupoEmpresa = grupoEmpresa;
							   	 NS.storeLlenarGridPagosTotal.baseParams.jsonDocumentos=NS.rangoDoctos;
								 NS.storeLlenarGridPagosTotal.baseParams.jsonProveedores=NS.rangoProv;
								 NS.storeLlenarGridPagosTotal.baseParams.provedoresIndividual = NS.individualProv;
								 NS.storeLlenarGridPagosTotal.baseParams.documentosIndividual = NS.individualDoc;
								
  								 NS.storeLlenarGridPagosTotal.load();
  								 NS.gridSolicitudPagosTotal.getView().refresh();
  				
  							 });
  					 }else
  						venDetalleUno.enable();
  				 });	 
  				 
  				}else{
  					Ext.Msg.alert('SET', 'NO SE HAN SELECCIONADO FACTURAS PARA LA PROPUESTA DE PAGO');
  				}
  				 
  			 }
  		 }   
  	 }

	}));

	
	/******************************************
	 * Tercer nivel, 
	 * Panel grid busqueda por Factura
	 * Componentes : Etiquetas, cajas de texto ,combos
	 */
	
	//LabelN3
	NS.lblEmpresaInfo = new Ext.form.Label({
		text: 'Empresa:',
		x: 10,
		y: 270
	});
	
	NS.lblEmpresaClave = new Ext.form.Label({
		text: '',
		x: 10,
		y: 287,
		width: 260,
	});
	
	NS.lblAcredorInfo = new Ext.form.Label({
		text: 'ACREDOR:',
		x: 10,
		y: 315
	});
	
	NS.lblAcredorClave = new Ext.form.Label({
		text: '',
		x: 10,
		y: 332,
		width: 260,
	});
	
	/*NS.lblFormaPagoInfo = new Ext.form.Label({
		text: 'FORMA DE PAGO:',
		x: 10,
		y: 360
	});
	
	NS.lblFormaPagoNombre = new Ext.form.Label({
		text: '',
		x: 10,
		y: 377
	});*/
	
	NS.lblImporteTotal = new Ext.form.Label({
		text: 'IMPORTE TOTAL:',
		x: 325,
		y: 430
	});
	
	NS.lblImporteSeleccionado = new Ext.form.Label({
		text: 'IMPORTE SELECCIONADO:',
		x: 10,
		y: 430
	});
	
	//cajasN3
	
	NS.txtImporteTotal = new Ext.form.TextField({
		id: PF + 'txtImporteTotal',
		name: PF + 'txtImporteTotal',
		x: 430,
		y: 423,
		width: 145,
		readOnly: true,
		style: {
			textAlign: 'right',
        }
	});

	NS.txtImporteSeleccionado = new Ext.form.TextField({
		id: PF + 'txtImporteSeleccionado',
		name: PF + 'txtImporteSeleccionado',
		x: 160,
		y: 423,
		width: 145,
		readOnly: true,
		style: {
			textAlign: 'right',
        }
	});

	NS.txtReferenciaBancaria = new Ext.form.TextField({
		id: PF + 'txtReferenciaBancaria',
		name: PF + 'txtReferenciaBancaria',
		x: 0,
		y: 0,
		width: 160,
		maxLength: 30,
		value: '',
			listeners:{
		 		keydown:{
		 			fn:function(caja, e) {
		 				if(!caja.isValid()){
		 					caja.setValue(caja.getValue().substring(0, 30));
		 				}
		 			}
		 		},
		 		keyup:{
		 			fn:function(caja, e) {	 				
		 				if(!caja.isValid()){
		 					caja.setValue(caja.getValue().substring(0, 30));
		 				}
		 			}
		 		},
			}
	});
	
	//comboN3
	
	NS.storeCmbChequerasPagadoras = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {noEmpresa: 2, divisa: '', idBanco: 2, beneficiario: '', acreedor: ''},
		paramOrder: ['noEmpresa', 'divisa', 'idBanco', 'beneficiario', 'acreedor'],
		directFn: ConsultaPagosPendientesAction.llenarComboChequerasPagadoras,
		fields:
		[
		 	{name: 'descripcion'}	
		],
		idProperty: 'descripcion',
		listeners:
		{
			load: function(s, records)
			{
		
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCmbChequerasPagadoras, msg: "Cargando Bancos..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'NO HAY CHQUERAS RELACIONADOS CON LA EMPRESA PAGADORA');
				}
				else{
				}
				
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});

	NS.cmbChquerasPagadoras = new Ext.form.ComboBox({
		store: NS.storeCmbChequerasPagadoras,
		id: PF + 'cmbChquerasPagadoras',
		name: PF + 'cmbChquerasPagadoras',
		width: 160,
		x: 0,
		y: 38,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'descripcion',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'SELECCIONE UNA CHEQUERA',
		disabled: true,
		triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor){
					chequeraPagadora = NS.cmbChquerasPagadoras.getValue();
					
				}
			}
			
		}
	});
	
	NS.storeCmbBancosPagador = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {noEmpresa: 2, divisa: ''},
		paramOrder: ['noEmpresa', 'divisa'],
		directFn: ConsultaPagosPendientesAction.llenarComboBancosPagador,
		fields:
		[
		 	{name: 'id'},
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'id',
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCmbBancosPagador, msg: "Cargando Bancos..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'NO HAY BANCOS RELACIONADOS CON LA EMPRESA PAGADORA');
				}
				else{
				}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	
	var tplB = new Ext.XTemplate('<tpl for="."><div class="x-combo-list-item">{id}-{descripcion}</div></tpl>');
	NS.cmbBancosPagador = new Ext.form.ComboBox({
		store: NS.storeCmbBancosPagador,
		id: PF + 'cmbBancosPagador',
		name: PF + 'cmbBancosPagador',
		width: 160,
		x: 0,
		y: 0,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		tpl:tplB,
		autocomplete: true,
		emptyText: 'SELECCIONE EL BANCO',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor){
					idBancoPagador = NS.cmbBancosPagador.getValue();
					NS.storeCmbChequerasPagadoras.baseParams.noEmpresa = sociedad;
					NS.storeCmbChequerasPagadoras.baseParams.divisa = divisa;
					NS.storeCmbChequerasPagadoras.baseParams.idBanco = idBancoPagador;
					
					NS.storeCmbChequerasPagadoras.baseParams.acreedor = acredor;
					NS.storeCmbChequerasPagadoras.baseParams.beneficiario = beneficiario;
					NS.storeCmbChequerasPagadoras.load();
					NS.cmbChquerasPagadoras.setDisabled(false);
					
				}	
			}
		}
	});

	NS.storeCmbChqueras = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {persona: '', idBanco: 0, tipoBusqueda: NS.tipoBusqueda},
		paramOrder: ['persona', 'idBanco','tipoBusqueda'],
		directFn: ConsultaPagosPendientesAction.llenarComboChequera,
		fields:
		[
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'descripcion',
		listeners:
		{
			load: function(s, records)
			{
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCmbChqueras, msg: "Cargando Chqueras ..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'NO HAY CHQUERAS RELACIONADAS CON EL PROVEEDOR/BANCO');
				}
				else{
				}
				
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	
	NS.cmbChqueras = new Ext.form.ComboBox({
		store: NS.storeCmbChqueras,
		id: PF + 'cmbChqueras',
		name: PF + 'cmbChqueras',
		width: 160,
		x: 0,
		y: 38,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'descripcion',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'SELECCIONE UNA CHEQUERA',
		disabled: true,
		triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor){
					chequera = NS.cmbChqueras.getValue();
				}
			}
			
		}
	});
	
	NS.storeCmbBancos = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {persona: acredor+'', tipoBusqueda: NS.tipoBusqueda},
		paramOrder: ['persona','tipoBusqueda'],
		directFn: ConsultaPagosPendientesAction.llenarComboBanco,
		fields:
		[
		 	{name: 'id'},
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'id',
		listeners:
		{
			load: function(s, records)
			{
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCmbBancos, msg: "Cargando bancos ..."});
				if(records.length == null || records.length <= 0){
				//	Ext.Msg.alert('SET', 'NO HAY BANCOS RELACIONADOS CON EL PROVEEDOR');
				}
				else{
				}
				
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});

	var tpl = new Ext.XTemplate('<tpl for="."><div class="x-combo-list-item">{id}-{descripcion}</div></tpl>');
	NS.cmbBancos = new Ext.form.ComboBox({
		store: NS.storeCmbBancos,
		id: PF + 'cmbBancos',
		name: PF + 'cmbBancos',
		width: 160,
		x: 0,
		y: 0,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		tpl:tpl,
		autocomplete: true,
		emptyText: 'SELECCIONE EL BANCO',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor){
					idBanco = NS.cmbBancos.getValue();
					NS.storeCmbChqueras.baseParams.persona = acredor+'';
					NS.storeCmbChqueras.baseParams.idBanco = idBanco;
					NS.storeCmbChqueras.baseParams.tipoBusqueda = NS.tipoBusqueda;
					NS.storeCmbChqueras.load();
					NS.cmbChqueras.setDisabled(false);
				}	
			}
		}
	});

	/*********
	 * Ventana emergente
	 * VENTANA EMERGENTE PARA INGRESAR EL NOMBRE DE LA PLANTILLA.
	 */
	
	NS.txtNombrePlantilla = new Ext.form.TextField({
		id: PF + 'txtNombrePlantilla',
		name: PF + 'txtNombrePlantilla',
		x: 100,
		y: 60,
		width: 250,
		emptyText: 'NOMBRE PLANTILLA',
		style: {
			textAlign: 'center',
        }
	});
	
	/***************
	 * Store, grid , metodos grid
	 * NIVEL 1 --> pagos por empresa (NS.gridSolicitudPagosTotal, NS.storeLlenarGridPagosTotal)
	 * NIVEL 2 ---> por proveedor
	 * NIVEL 3 ---> por factura 
	 */

	/*******
	 * NIVEL 1
	 * PAGOS POR EMPRESA
	 * GRIDN1
	 */
	
	NS.filterRow = new Ext.ux.grid.FilterRow({
		id: PF + 'filterRow',
		refilterOnStoreUpdate: true
	});
	
	
	NS.filterRowDos = new Ext.ux.grid.FilterRow({
		id: PF + 'filterRowDos',
		refilterOnStoreUpdate: true
	});
	
	NS.seleccionRenglonTotalPagos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasTotalPagos = new Ext.grid.ColumnModel([
       {header: 'SOCIEDAD', width: 100, dataIndex: 'numeroEmpresa' , sortable: true, filter: {testNu: "/^{0}/i"}},	
   	   {header: 'NOMBRE', width: 270, dataIndex: 'nombreEmpresa' ,sortable: true, filter: {testNe: "/^{0}/i"}},                                        	
   	   {header: 'TOTAL VENCIDO', width: 180, dataIndex: 'total' ,sortable: true, renderer: BFwrk.Util.rendererMoney ,align:'right', sortable: true},
   	   {header: 'TOTAL POR VENCER', width: 180, dataIndex: 'porVencer' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right'},
   	   {header: 'TOTAL GLOBAL', width: 180, dataIndex: 'totalGlobal' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right'},
   	]);
	
	NS.storeLlenarGridPagosTotal = new Ext.data.DirectStore({ 
		paramsAsHash: true,
		root: '',
		baseParams: {reglaNegocio: 0, fechaConIni: '', fechaConFin: '', fechaPropIni: '',
			fechaPropFin: '', divisa: 'MN', indicadores: '', numDoc: '', tipoDoc: '' , numDocA:'' , numDocB:'',
			equivalePersonaA:'' , equivalePersonaB:'' , grupoEmpresa:'' , jsonDocumentos: '' , jsonProveedores:'', provedoresIndividual:false, documentosIndividual:false,tipoBusqueda: NS.tipoBusqueda},
		paramOrder: ['reglaNegocio', 'fechaConIni', 'fechaConFin', 'fechaPropIni', 'fechaPropFin',
		             'divisa', 'indicadores', 'numDoc', 'tipoDoc', 'numDocA','numDocB',
				     'equivalePersonaA','equivalePersonaB','grupoEmpresa','jsonDocumentos','jsonProveedores', 'provedoresIndividual', 'documentosIndividual','tipoBusqueda'],
		directFn: ConsultaPagosPendientesAction.retornarLisPagosPendientes,
		fields:
		[
		 	{name: 'numeroEmpresa'},
		 	{name: 'nombreEmpresa'},
		 	{name: 'total'},
		 	{name: 'porVencer'},
		 	{name: 'totalGlobal'}
		 	
		],
		idProperty: 'numeroEmpresa',
		listeners:{
			load: function(s, records){
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existen pagos pendientes.');
				}else{
					NS.llenarTxtToalPagosPendientes();
				}	
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}

	});
	
	NS.gridSolicitudPagosTotal = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridPagosTotal,
		id: PF + 'gridSolicitudPagosTotal',
		x: 80,
		y: 35,
		cm: NS.columnasTotalPagos,
		sm: NS.seleccionRenglonTotalPagos,
		width: 805,
		height: 220,
		border: true,
		stripeRows: true,
		plugins: [NS.filterRowDos],
		listeners:{
			rowClick:{
				fn:function(grid){
					NS.noAcreedor = new Array();
					
					NS.contador=0;
					NS.importeTotal=0;
					NS.importeAcumuladoFloat = 0;
					
					NS.noFactura = new Array();
					NS.contNoFactura=0;

					var seleccion = NS.gridSolicitudPagosTotal.getSelectionModel().getSelected();
					sociedad = seleccion.get('numeroEmpresa');
					nombreSociedad = seleccion.get('nombreEmpresa');
					
					NS.storeLlenarGridSolicitudPagos.baseParams.numeroEmpresa = seleccion.get('numeroEmpresa');
					
					if(NS.cmbReglaNegocioDos.getValue() == '' || NS.cmbReglaNegocioDos.getValue() == undefined || NS.cmbReglaNegocioDos.getValue() == null){
						NS.storeLlenarGridSolicitudPagos.baseParams.reglaNegocio = 0;   
  				   }else{
  					 NS.storeLlenarGridSolicitudPagos.baseParams.reglaNegocio = NS.cmbReglaNegocioDos.getValue(); //qwer
  				   }
					
					NS.storeLlenarGridSolicitudPagos.baseParams.fechaConIni = fechaConIni;
					NS.storeLlenarGridSolicitudPagos.baseParams.fechaConFin = fechaConFin;
					NS.storeLlenarGridSolicitudPagos.baseParams.fechaPropIni = fechaPropIni;
					NS.storeLlenarGridSolicitudPagos.baseParams.fechaPropFin = fechaPropFin;
					NS.storeLlenarGridSolicitudPagos.baseParams.divisa = divisa;
					NS.storeLlenarGridSolicitudPagos.baseParams.indicadores = indicadores;
					NS.storeLlenarGridSolicitudPagos.baseParams.numDoc = numDoc;
					NS.storeLlenarGridSolicitudPagos.baseParams.tipoDoc = tipoDoc;
					NS.storeLlenarGridSolicitudPagos.baseParams.numDocB = numDocB;
					NS.storeLlenarGridSolicitudPagos.baseParams.numDocA = numDocA;
					NS.storeLlenarGridSolicitudPagos.baseParams.equivalePersonaA = equivalePersonaA;
					NS.storeLlenarGridSolicitudPagos.baseParams.equivalePersonaB = equivalePersonaB;
					NS.storeLlenarGridSolicitudPagos.baseParams.grupoEmpresa = grupoEmpresa;
					NS.storeLlenarGridSolicitudPagos.baseParams.jsonDocumentos=NS.rangoDoctos;
                    NS.storeLlenarGridSolicitudPagos.baseParams.jsonProveedores=NS.rangoProv;
                    NS.storeLlenarGridSolicitudPagos.baseParams.provedoresIndividual = NS.individualProv;
        			NS.storeLlenarGridSolicitudPagos.baseParams.documentosIndividual = NS.individualDoc;
        			NS.storeLlenarGridSolicitudPagos.baseParams.tipoBusqueda = NS.tipoBusqueda;
			     		
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridSolicitudPagos, msg: "Cargando Pagos ..."});
					NS.storeLlenarGridSolicitudPagos.load();
					NS.controlFechas(apps.SET.FEC_HOY, 'load');
					NS.ImporteAcumuladoMN.setText( NS.cambiarFormato(NS.importeAcumuladoFloat.toFixed(2), '$'));
					NS.insertarPagoProgramado();
					
					matrizDatosGlobal = new Array();
					matrizDatosPagos = new Object();
					
					NS.seleccionArray = new Object();
					NS.indiceActual = null;
					
					NS.sumaNoAcreedorVencido= new Array();
					NS.sumaNoAcreedorPorVencer= new Array();
					venDetalleUno.enable();
					venDetalleDos.hide();
					venDetalleUno.show();
					
						
				}
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridPagosTotal, msg: "Cargando Pagos ..."});
	NS.storeLlenarGridPagosTotal.load();
	
	/*******
	 * NIVEL 2
	 * PAGOS POR PROVEEDOR
	 * GRIDN2
	 */
	
	NS.seleccionRenglonSolicitudPagos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true,
	});     
	
	NS.checkColumn = new Ext.grid.CheckColumn({
        header: 'Vencido',
        id: 'checkPv',
        width:35,
        dataIndex: 'bandera',
        value:true,
        listeners:{
       	 click:function(column,recordIndex, checked){
       		var seleccion =  NS.gridSolicitudPagos.getSelectionModel().getSelections();
       		
       		if (seleccion[0].get('bandera') == false) {
       			if (seleccion[0].get('vencido') != 0) {
       				NS.importeAcumuladoFloat +=  seleccion[0].get('vencidoP');
    				NS.importeAcumuladoFloat -=  seleccion[0].get('porVencerS');
    				
    				NS.indiceActual = sociedad + "_" + seleccion[0].get('beneficiario');
    				
    				seleccion[0].set('vencidoS',
							seleccion[0].get('vencido'));
					
					seleccion[0].set('vencidoP', 0.0);
					
					seleccion[0].set('bandera', true);
					
					NS.obtenerSeleccionDelNivelTres(
							seleccion[0].get('numeroEmpresa'),
							seleccion[0].get('equivalePersona')+'',
							reglaNegocio,
							fechaConIni,
							fechaConFin,
							fechaPropIni,
							fechaPropFin,
							divisa,
							indicadores,
							numDoc+'',
							tipoDoc+'',
							numDocA+'',
							numDocB+'',
							equivalePersonaA+'',
							equivalePersonaB+'',
							grupoEmpresa+'',
							NS.rangoDoctos,
							NS.rangoProv,
							NS.indiceActual,
							1,
							NS.individualDoc,
							NS.tipoBusqueda
					);
       			} else {
       				Ext.Msg.alert('SET', 'No existen pagos pendientes vencidos.');
       			}				
			} else {
				NS.importeAcumuladoFloat -=  seleccion[0].get('porVencerS');
				NS.importeAcumuladoFloat -=  seleccion[0].get('vencidoS');
				
				NS.indiceActual = sociedad + "_" + seleccion[0].get('beneficiario');
				
				seleccion[0].set('vencidoS', 0.0);
				
				seleccion[0].set('vencidoP',
						seleccion[0].get('vencido'));
				
				seleccion[0].set('bandera', false);
				
				NS.obtenerSeleccionDelNivelTres(
						seleccion[0].get('numeroEmpresa'),
						seleccion[0].get('equivalePersona')+'',
						reglaNegocio,
						fechaConIni,
						fechaConFin,
						fechaPropIni,
						fechaPropFin,
						divisa,
						indicadores,
						numDoc+'',
						tipoDoc+'',
						numDocA+'',
						numDocB+'',
						equivalePersonaA+'',
						equivalePersonaB+'',
						grupoEmpresa+'',
						NS.rangoDoctos,
						NS.rangoProv,
						NS.indiceActual,
						0,
						NS.individualDoc,
						NS.tipoBusqueda
				);
			}
    		
       		NS.ImporteAcumuladoMN.setText(
    				NS.cambiarFormato(
    						NS.importeAcumuladoFloat.toFixed(2), '$'));
    		
		 },
        }
   });
	 
	NS.checkColumnPorVencer = new Ext.grid.CheckColumn({
        header: 'Por Vencer',
        id: 'checkPorVencer',
        width:35,
        dataIndex: 'bPorVencer',
        value:true,
        listeners:{
       	 click:function(column,recordIndex, checked){
       		 var seleccion =  NS.gridSolicitudPagos.getSelectionModel().getSelections();
       		
       		if (seleccion[0].get('bPorVencer') == false) {
       			if (seleccion[0].get('porVencer') != 0) {
       				NS.importeAcumuladoFloat +=  seleccion[0].get('porVencerP');
    				NS.importeAcumuladoFloat -=  seleccion[0].get('vencidoS');
    				
    				NS.indiceActual = sociedad + "_" + seleccion[0].get('beneficiario');
    				
    				seleccion[0].set('porVencerS',
							seleccion[0].get('porVencer'));
					
					seleccion[0].set('porVencerP', 0.0);
					
					seleccion[0].set('bPorVencer', true);
					
					NS.obtenerSeleccionDelNivelTres(
							seleccion[0].get('numeroEmpresa'),
							seleccion[0].get('equivalePersona')+'',
							reglaNegocio,
							fechaConIni,
							fechaConFin,
							fechaPropIni,
							fechaPropFin,
							divisa,
							indicadores,
							numDoc+'',
							tipoDoc+'',
							numDocA+'',
							numDocB+'',
							equivalePersonaA+'',
							equivalePersonaB+'',
							grupoEmpresa+'',
							NS.rangoDoctos,
							NS.rangoProv,
							NS.indiceActual,
							1,
							NS.individualDoc,
							NS.tipoBusqueda
					);
       			} else {
       				Ext.Msg.alert('SET', 'No existen pagos pendientes por vencer.');
       			}				
			} else {
				NS.importeAcumuladoFloat -=  seleccion[0].get('porVencerS');
				NS.importeAcumuladoFloat -=  seleccion[0].get('vencidoS');
				
				NS.indiceActual = sociedad + "_" + seleccion[0].get('beneficiario');
				
				seleccion[0].set('porVencerS', 0.0);
				
				seleccion[0].set('porVencerP',
						seleccion[0].get('porVencer'));
				
				seleccion[0].set('bPorVencer', false);
				
				NS.obtenerSeleccionDelNivelTres(
						seleccion[0].get('numeroEmpresa'),
						seleccion[0].get('equivalePersona')+'',
						reglaNegocio,
						fechaConIni,
						fechaConFin,
						fechaPropIni,
						fechaPropFin,
						divisa,
						indicadores,
						numDoc+'',
						tipoDoc+'',
						numDocA+'',
						numDocB+'',
						equivalePersonaA+'',
						equivalePersonaB+'',
						grupoEmpresa+'',
						NS.rangoDoctos,
						NS.rangoProv,
						NS.indiceActual,
						0,
						NS.individualDoc,
						NS.tipoBusqueda
				);
			}
    		
       		NS.ImporteAcumuladoMN.setText(
    				NS.cambiarFormato(
    						NS.importeAcumuladoFloat.toFixed(2), '$'));
		 },
        }
   });
	
	NS.columnasSolicitudPagosN2 = new Ext.grid.ColumnModel([
   	    NS.checkColumn, 
   	    NS.checkColumnPorVencer ,
   	    {header: 'SOCIEDAD', width: 90, dataIndex: 'numeroEmpresa', sortable: true},
   		{header: 'ACREEDOR', width: 110, dataIndex: 'equivalePersona' ,sortable: true, filter: {testEp:'/{0}/i'}},
   		{header: 'NOMBRE', width: 290, dataIndex: 'beneficiario'  ,sortable: true, filter: {testBe:'/{0}/i'}},
   		{header: 'VENCIDO', width: 140, dataIndex: 'vencido' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right' },
   		{header: 'VENCIDO SELECCIONADO', width: 140, dataIndex: 'vencidoS' ,sortable: true, renderer: BFwrk.Util.rendererMoney,align:'right'},
   		{header: 'VENCIDO PENDIENTE', width: 140, dataIndex: 'vencidoP', sortable: true, renderer: BFwrk.Util.rendererMoney,align:'right'},
   		{header: 'POR VENCER', width: 140, dataIndex: 'porVencer' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right'},
   		{header: 'POR VENCER SELECCIONADO', width: 140, dataIndex: 'porVencerS' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right'},
   		{header: 'POR VENCER PENDIENTE', width: 140, dataIndex: 'porVencerP' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right'},
   		//{header: 'FORMA DE PAGO', width: 170, dataIndex: 'formaPago' ,sortable: true,filter:{testEp:'/{0}/i'}},
   		{header: 'CHEQUERA', width: 170, dataIndex: 'chequera' ,sortable: true,filter: {testEp:'/{0}/i'}},
   		{header: 'REFERENCIA_BANC', width: 170, dataIndex: 'referenciaBanc' ,sortable: true,filter: {testEp:'/{0}/i'}},
   		{header: 'ID_BANCO', width: 70, dataIndex: 'idBanco' ,sortable: true, hidden: true}
   		                                                      
   	]);
	
	NS.storeLlenarGridSolicitudPagos = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {numeroEmpresa: 1, reglaNegocio: 0, fechaConIni: '', fechaConFin: '', fechaPropIni: '',
			fechaPropFin: '', divisa: '', indicadores: '', numDoc: '', tipoDoc: '',
			numDocA: '' ,numDocB: '' ,equivalePersonaA:'',equivalePersonaB:'', tipoDocBusq:'', grupoEmpresa: '' , jsonDocumentos: '' , jsonProveedores:'', provedoresIndividual:false, documentosIndividual:false, tipoBusqueda:NS.tipoBusqueda}, //parametros de busqueda.
		paramOrder: ['numeroEmpresa', 'reglaNegocio', 'fechaConIni', 'fechaConFin', 'fechaPropIni', 'fechaPropFin',
		             'divisa', 'indicadores', 'numDoc', 'tipoDoc','numDocA','numDocB',
		             'equivalePersonaA','equivalePersonaB','grupoEmpresa','jsonDocumentos', 'jsonProveedores', 'provedoresIndividual', 'documentosIndividual','tipoBusqueda'],
		             
		directFn: ConsultaPagosPendientesAction.retornarLisPagosPendientesNivDos,
		fields:
		[		 	
		 	{name: 'numeroEmpresa'},
		 	{name: 'equivalePersona'},
		 	{name: 'beneficiario'},
		 	{name: 'vencido'},
		 	{name: 'vencidoS'},
		 	{name: 'vencidoP'},
		 	{name: 'porVencer'},
		 	{name: 'porVencerS'},
		 	{name: 'porVencerP'},
		 	{name: 'formaPago'},
		 	{name: 'chequera'},
		 	{name: 'idBanco'},
		 	{name: 'referenciaBanc'},
		 	{name: 'bandera'},
		 	{name: 'bPorVencer'},
		 	
		 	
		 	
		 	
		],
		idProperty: 'beneficiario',
		listeners:
		{
			load: function(s, records)
			{
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen pagos pendientes.');
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}

	});

	NS.gridSolicitudPagos = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridSolicitudPagos,
		id: PF + 'gridSolicitudPagos',
		x: 10,
		y: 10,
		cm: NS.columnasSolicitudPagosN2,
		sm: NS.seleccionRenglonSolicitudPagos,
		width: 950,
		height: 400,
		border: false,
		stripeRows: true,
		plugins: [NS.filterRow],
		listeners:{
			dblclick:{
				fn:function(grid){
					var seleccion = NS.gridSolicitudPagos.getSelectionModel().getSelected();
					
					if(seleccion!= undefined){
						venDetalleDos.show();
						acredor = seleccion.get('equivalePersona');
						nombreAcredor = seleccion.get('beneficiario');
						importeVencido = seleccion.get('vencido');
						importePOrVencer = seleccion.get('porVencer');
						formaPago = seleccion.get('formaPago');
						indiceGlobal = seleccion.get('index');
						chequera = '';
						idBanco = 0;
						beneficiario = seleccion.get('beneficiario');
						
						idMatriz = sociedad + "_" + acredor;
						NS.indiceActual = sociedad + "_" + beneficiario;
						
						NS.importeVencidoResNivelTres = seleccion.get('vencidoS');
						NS.importePorVencerResNivelTres = seleccion.get('porVencerS');
						
						NS.importeVencidoNivelTres = NS.importeVencidoResNivelTres;
						NS.importePorVencerNivelTres = NS.importePorVencerResNivelTres;
						
						NS.lblEmpresaClave.setText(sociedad + ' -- ' + nombreSociedad);
						NS.lblAcredorClave.setText(acredor + ' -- ' + seleccion.get('beneficiario'));
						//NS.lblFormaPagoNombre.setText(formaPago);
						NS.txtImporteTotal.setValue(NS.cambiarFormato((importeVencido + importePOrVencer),'$'));
						NS.txtReferenciaBancaria.setValue(seleccion.get('referenciaBanc'));
						chequeraPagadora = "";
						//->guardar datos en mi vector 
						
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.numeroEmpresa = seleccion.get('numeroEmpresa');
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.acredor = seleccion.get('equivalePersona')+'';
						
						if(NS.cmbReglaNegocioDos.getValue() == '' || NS.cmbReglaNegocioDos.getValue() == undefined || NS.cmbReglaNegocioDos.getValue() == null){
							NS.storeLlenarGridSolicitudPagosNivDos.baseParams.reglaNegocio = 0;   
	  				   }else{
	  					 NS.storeLlenarGridSolicitudPagosNivDos.baseParams.reglaNegocio = NS.cmbReglaNegocioDos.getValue(); //qwer
	  				   }
						
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.fechaConIni = fechaConIni;
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.fechaConFin = fechaConFin;
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.fechaPropIni = fechaPropIni;
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.fechaPropFin = fechaPropFin;
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.divisa = divisa;
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.indicadores = indicadores;
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.numDoc = numDoc+'';
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.tipoDoc = tipoDoc+'';
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.numDocA = numDocA+'';
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.numDocB = numDocB+'';
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.equivalePersonaA = equivalePersonaA+'';
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.equivalePersonaB = equivalePersonaB+'';
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.grupoEmpresa = grupoEmpresa+'';
						NS.storeLlenarGridSolicitudPagosNivDos.baseParams.jsonDocumentos=NS.rangoDoctos;
		                NS.storeLlenarGridSolicitudPagosNivDos.baseParams.jsonProveedores=NS.rangoProv;
		                NS.storeLlenarGridSolicitudPagosNivDos.baseParams.documentosIndividual = NS.individualDoc;
		                NS.storeLlenarGridSolicitudPagosNivDos.baseParams.beneficiario = beneficiario + '';
		                NS.storeLlenarGridSolicitudPagosNivDos.baseParams.tipoBusqueda = NS.tipoBusqueda;
		                
		                if (NS.seleccionArray[NS.indiceActual] != null &&
								NS.seleccionArray[NS.indiceActual] != undefined) {
		                	
		                	Ext.MessageBox.show({
		         		       title : 'Información SET',
		         		       msg : 'Seleccionando...',
		         		       width : 300,
		         		       wait : true,
		         		       progress:true,
		         		       waitConfig : {interval:200}
		         	   		});
		                	
		                	NS.elementoSeleccionado = JSON.parse(
		                			JSON.stringify(
		                					NS.seleccionArray[NS.indiceActual]));
		                	
		                	
		                	NS.gridSolicitudPagosNivDos.store.removeAll();
		                	NS.gridSolicitudPagosNivDos.getView().refresh();           	
		                	
		                	
		                	
							var RenglonNivelTres = Ext.data.Record.create(
									NS.storeLlenarGridSolicitudPagosNivDos.fields);
							
							
							
							var vecAuxiliar = NS.seleccionArray[NS.indiceActual];
							
							var vecSeleccion = new Array();
							
							for (var i = 0; i < vecAuxiliar.length; i++) {
								NS.renglon = vecAuxiliar[i];
								NS.newRecord = new RenglonNivelTres();
								NS.storeLlenarGridSolicitudPagosNivDos.add(NS.newRecord);
																
								NS.renglones = NS.storeLlenarGridSolicitudPagosNivDos.data.items;
								
								NS.renglones[i].set('cveControl', NS.renglon['cveControl']);
							 	NS.renglones[i].set('factura', NS.renglon['factura']);
							 	NS.renglones[i].set('numeroDcoumento', NS.renglon['numeroDcoumento']);
							 	NS.renglones[i].set('importeNeto', NS.renglon['importeNeto']);
							 	NS.renglones[i].set('texto', NS.renglon['texto']);
							 	NS.renglones[i].set('divisa', NS.renglon['divisa']);
							 	NS.renglones[i].set('estatus', NS.renglon['estatus']);
							 	NS.renglones[i].set('index', NS.renglon['index']);
							 	NS.renglones[i].set('fechaPropuesta', NS.renglon['fechaPropuesta']);
							 	NS.renglones[i].set('claseDocumento', NS.renglon['claseDocumento']);
							 	NS.renglones[i].set('formaPago', NS.renglon['formaPago']);
							 	NS.renglones[i].set('seleccionado', NS.renglon['seleccionado']);
							 	
							 	if (NS.renglon['seleccionado'] == 'X') {
							 		vecSeleccion[i] = i;
								}

							}
							
//							NS.gridSolicitudPagosNivDos.getSelectionModel().selectRow(0);
//							NS.gridSolicitudPagosNivDos.getSelectionModel().selectRow(1);
							
							NS.gridSolicitudPagosNivDos.getSelectionModel().selectRows(vecSeleccion);
							
							Ext.MessageBox.hide();
						} else {
							
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridSolicitudPagosNivDos, msg: "Cargando Pagos ..."});
							
							NS.storeLlenarGridSolicitudPagosNivDos.load();
							
							var reglaNegocio = 0;
							if(NS.cmbReglaNegocioDos.getValue() == '' || 
									NS.cmbReglaNegocioDos.getValue() == undefined || 
									NS.cmbReglaNegocioDos.getValue() == null){
								reglaNegocio = 0;   
		  				   }else{
		  					 reglaNegocio = NS.cmbReglaNegocioDos.getValue(); //qwer
		  				   }
							
							NS.obtenerSeleccionDelNivelTres(
									seleccion.get('numeroEmpresa'),
									seleccion.get('equivalePersona')+'',
									reglaNegocio,
									fechaConIni,
									fechaConFin,
									fechaPropIni,
									fechaPropFin,
									divisa,
									indicadores,
									numDoc+'',
									tipoDoc+'',
									numDocA+'',
									numDocB+'',
									equivalePersonaA+'',
									equivalePersonaB+'',
									grupoEmpresa+'',
									NS.rangoDoctos,
									NS.rangoProv,
									idMatriz,
									0,
									NS.individualDoc,
									NS.tipoBusqueda
							);
						}
		            	
		            	NS.txtImporteSeleccionado.setValue(
		            			NS.cambiarFormato(
		            					(NS.importeVencidoNivelTres + 
		            							NS.importePorVencerNivelTres).toFixed(2), '$'));
						
						NS.cmbBancos.setValue("");
						NS.cmbChqueras.setValue("");
						NS.cmbBancosPagador.setValue("");
						NS.cmbChquerasPagadoras.setValue("");
						vec = new Array();
						
						if(formaPago == 'VARIOS'){
							Ext.getCmp(PF + 'btnCambiarChequera').setVisible(true);
							//Ext.getCmp(PF + 'PanelContenedorReferenciaBanc').setVisible(true);
							
						}else{
							//Ext.getCmp(PF + 'btnCambiarChequera').setVisible(false);
							//Ext.getCmp(PF + 'PanelContenedorCambioChequera').setVisible(false);
							//Ext.getCmp(PF + 'PanelContenedorCambioChequeraP').setVisible(false);
							//Ext.getCmp(PF + 'PanelContenedorReferenciaBanc').setVisible(false);
						}
						
						
					}else{
						venDetalleDos.hide();
					}
	
				}
			}
		}
	});
	
	/*******
	 * NIVEL 3
	 * PAGOS POR FACTURA
	 * GRIDN3
	 */
	
	NS.seleccionRenglonSolicitudPagosNivDos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false,
		listeners:{
			rowselect: {
				fn:function(r, rowIndex){
					//<-- agregar a la seleccion
					NS.calcularTotal();
					vec[rowIndex] = rowIndex;
					NS.renglones = NS.storeLlenarGridSolicitudPagosNivDos.data.items;
					NS.renglones[rowIndex].set('seleccionado', 'X');

					/*for (var i = 0; NS.renglones.length > i; i++){
						NS.elementoSeleccionado[i] = NS.renglones[i].data;
					}*/
					NS.elementoSeleccionado[NS.renglones[rowIndex].get('index')]['seleccionado'] = 'X';
					
				}
			},
			rowdeselect: {
				fn:function(r, rowIndex){
					NS.renglones = NS.storeLlenarGridSolicitudPagosNivDos.data.items;
					NS.renglones[rowIndex].set('seleccionado', '');

					/*for (var i = 0; NS.renglones.length > i; i++){
						NS.elementoSeleccionado[i] = NS.renglones[i].data;
					}*/
					NS.elementoSeleccionado[NS.renglones[rowIndex].get('index')]['seleccionado'] = '';
					
					NS.calcularTotal();
					
					if(NS.gridSolicitudPagosNivDos.getSelectionModel().getSelections().length < 1){
						vec = new Array();
					}else{
						delete vec[rowIndex];
						//vec.splice(rowIndex, rowIndex);
					}
					
					var posicionAux = NS.storeLlenarGridSolicitudPagosNivDos.data.items[rowIndex].get('factura')+"R";
					if(matrizDatosPagos[posicionAux] != undefined){
						delete matrizDatosPagos[posicionAux];
					}
					NS.banderaVencidosSeleccionados=false;
				}
			}
			
		}
		
	});     
	
	  NS.columnasSolicitudPagos = new Ext.grid.ColumnModel([
	    NS.seleccionRenglonSolicitudPagosNivDos,
	    {header: 'FACTURA', width: 150, dataIndex: 'cveControl', sortable: false},
	    {header: 'INDICE', width: 70, dataIndex: 'index', sortable: false, hidden: true},
	    {header: 'FOLIO_SET', width: 120, dataIndex: 'factura', sortable: false, hidden: true},
	    {header: 'NO_DOC', width: 120, dataIndex: 'numeroDcoumento', sortable: false},
	    {header: 'CLASE', width: 70, dataIndex: 'claseDocumento', sortable: false,},
	    {header: 'IMPORTE', width: 140, dataIndex: 'importeNeto', sortable: false, renderer: BFwrk.Util.rendererMoney ,align:'right'},
	    {header: 'CONCEPTO', width: 360, dataIndex: 'texto', sortable: false},
	    {header: 'VENCIMIENTO', width: 120, dataIndex: 'fechaPropuesta', sortable: false},
		{header: 'DIVISA', width: 70, dataIndex: 'divisa', sortable: false, hidden: true},
	    {header: 'ESTATUS', width: 70, dataIndex: 'estatus', sortable: false,},
	    {header: 'FORMA DE PAGO', width: 100, dataIndex: 'formaPago' ,sortable: true},
	    {header: 'X', width: 18, dataIndex: 'seleccionado', sortable: true, hidden: true}
	]);
	
	NS.storeLlenarGridSolicitudPagosNivDos = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {numeroEmpresa: 1, acredor: 1, reglaNegocio: 0, fechaConIni: '', fechaConFin: '', fechaPropIni: '',
			fechaPropFin: '', divisa: '', indicadores: '', numDoc: '', tipoDoc: '' ,numDocA:'', numDocB:'' ,
			equivalePersonaA:'', equivalePersonaB:'' ,grupoEmpresa:'' , jsonDocumentos: '' , jsonProveedores:'', documentosIndividual:false, beneficiario: '', tipoBusqueda:NS.tipoBusqueda
				},
		paramOrder: ['numeroEmpresa', 'acredor', 'reglaNegocio', 'fechaConIni', 'fechaConFin', 'fechaPropIni', 'fechaPropFin',
		             'divisa', 'indicadores', 'numDoc', 'tipoDoc' ,'numDocA','numDocB',
				     'equivalePersonaA','equivalePersonaB','grupoEmpresa','jsonDocumentos', 'jsonProveedores', 'documentosIndividual', 'beneficiario','tipoBusqueda'],
		directFn: ConsultaPagosPendientesAction.retornarLisPagosPendientesNivTres,
		fields:
		[
		 	{name: 'cveControl'},
		 	{name: 'factura'},
		 	{name: 'numeroDcoumento'},
		 	{name: 'importeNeto'},
		 	{name: 'texto'},
		 	{name: 'divisa'},
		 	{name: 'estatus'},
		 	{name: 'index'},
		 	{name: 'fechaPropuesta'},
		 	{name: 'claseDocumento'},
		 	{name: 'formaPago'},
		 	{name: 'seleccionado'},
		 	
		],
		idProperty: 'factura',
		listeners:
		{
		load: function(s, records)
			{
//				if(matrizDatosGlobal[idMatriz] != null && matrizDatosGlobal[idMatriz] != '' ){
//					NS.gridSolicitudPagosNivDos.getSelectionModel().selectRows(matrizDatosGlobal[idMatriz]);
//				}
//				NS.lblEmpresaClave.setText(sociedad + ' -- ' + nombreSociedad);
//				NS.lblAcredorClave.setText(acredor + ' -- ' + nombreAcredor);
//				//NS.lblFormaPagoNombre.setText(formaPago);
//				NS.txtImporteTotal.setValue(NS.cambiarFormato((importeVencido + importePOrVencer),'$'));
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen pagos pendientes.');
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}

	});
	
	NS.gridSolicitudPagosNivDos = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridSolicitudPagosNivDos,
		id: PF + 'gridSolicitudPagosNivDos',
		x: 10,
		y: 10,
		cm: NS.columnasSolicitudPagos,
		sm: NS.seleccionRenglonSolicitudPagosNivDos,
		width: 950,
		height: 250,
		frame: true,
		border: false,
		stripeRows: true
	});
	
	/************Fin componentes***********/
	
	/*******************************
	 * Funciones
	 */
	
	NS.obtenerSeleccionDelNivelTres = function (
			numeroEmpresa,
			equivalePersona,
			reglaNegocio,
			fechaConIni,
			fechaConFin,
			fechaPropIni,
			fechaPropFin,
			divisa,
			indicadores,
			numDoc,
			tipoDoc,
			numDocA,
			numDocB,
			equivalePersonaA,
			equivalePersonaB,
			grupoEmpresa,
			rangoDoctos,
			rangoProv,
			idMatriz,
			marca,
			documentos,
			tipoBusqueda
	){
		Ext.MessageBox.show({
		       title : 'Información SET',
		       msg : 'Seleccionando...',
		       width : 300,
		       wait : true,
		       progress:true,
		       waitConfig : {interval:200}
	   		});
		ConsultaPagosPendientesAction.retornarJsonPendientesNivTres(
				numeroEmpresa, 
				equivalePersona, 
				reglaNegocio,  
				fechaConIni,  
				fechaConFin,
				fechaPropIni,
				fechaPropFin,
				divisa,  
				indicadores,  
				numDoc,  
				tipoDoc,
				numDocA,  
				numDocB, 
				equivalePersonaA, 
				equivalePersonaB,  
				grupoEmpresa,
				rangoDoctos,
				rangoProv,
				marca,
				documentos,
				tipoBusqueda,
	    function(res ) {
			if (res != null && 
					res != undefined && 
						res != '') {
				NS.seleccionArray[idMatriz] = eval('(' + res + ')');
				NS.elementoSeleccionado = eval('(' + res + ')');
			}
			Ext.MessageBox.hide();
		});
	};
	
	function verificaComponentesCreados(){
		var compt;
		compt = Ext.getCmp(PF + 'txtfechaPropuesta');
		if(compt != null || compt != undefined){
			compt.destroy();
		}
		compt = Ext.getCmp(PF + 'gridSolicitudPagos');
		if(compt != null || compt != undefined){
			compt.destroy();
		}
		compt = Ext.getCmp(PF + 'gridSolicitudPagosNivDos');
		if(compt != null || compt != undefined){
			compt.destroy();
		}
	}
		
	NS.cargarPagosPorEmpresa= function(){
		if(NS.cmbReglaNegocioDos.getValue() == '' || NS.cmbReglaNegocioDos.getValue() == undefined || NS.cmbReglaNegocioDos.getValue() == null){
			 reglaNegocio = 0;   
		   }else{
			 reglaNegocio = NS.cmbReglaNegocioDos.getValue();   
		   }
		   
		   fechaConIni = NS.cambiarFecha(''+NS.txtFechaRequeridaPago.getValue());
		   fechaConFin = NS.cambiarFecha(''+NS.txtFechaRequeridaPagoDos.getValue());
		   fechaPropIni = NS.cambiarFecha(''+NS.txtIdSolicitantePago.getValue());
		   fechaPropFin = NS.cambiarFecha(''+NS.txtIdSolicitantePagoDos.getValue());
		   divisa = NS.cmbDivisa.getValue();
		   if(divisa==null || divisa=='')
			   divisa='MN';
		   indicadores = NS.txtIndicadores.getValue();
		   numDoc = NS.txtNoDoc.getValue()+'';
		   tipoDoc = NS.txtTipoDoc.getValue()+'';
		   numDocA = (NS.txtRangoDocA.getValue()==undefined ? '' : NS.txtRangoDocA.getValue())+'';
		   numDocB = (NS.txtRangoDocB.getValue()==undefined ? '' : NS.txtRangoDocB.getValue())+'';
		   equivalePersonaA = (NS.txtRangoProvA.getValue()==undefined ? '' : NS.txtRangoProvA.getValue())+'';
		   equivalePersonaB = (NS.txtRangoProvB.getValue()==undefined ? '' : NS.txtRangoProvB.getValue())+'';
		   grupoEmpresa = NS.txtIdEmpresa.getValue()+'';
	
		   console.log(reglaNegocio + " " + divisa + " " + indicadores + " " + numDoc + " " + tipoDoc);
		   
			NS.storeLlenarGridPagosTotal.baseParams.reglaNegocio = parseInt(reglaNegocio);
			NS.storeLlenarGridPagosTotal.baseParams.fechaConIni = fechaConIni;
			NS.storeLlenarGridPagosTotal.baseParams.fechaConFin = fechaConFin;
			NS.storeLlenarGridPagosTotal.baseParams.fechaPropIni = fechaPropIni;
			NS.storeLlenarGridPagosTotal.baseParams.fechaPropFin = fechaPropFin;
			NS.storeLlenarGridPagosTotal.baseParams.divisa = divisa;
			NS.storeLlenarGridPagosTotal.baseParams.indicadores = indicadores;
			NS.storeLlenarGridPagosTotal.baseParams.numDoc = numDoc+'';
			NS.storeLlenarGridPagosTotal.baseParams.tipoDoc = tipoDoc+'';
			NS.storeLlenarGridPagosTotal.baseParams.numDocA = numDocA+'';
			NS.storeLlenarGridPagosTotal.baseParams.numDocB = numDocB+'';
			NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaA = equivalePersonaA+'';
			NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaB = equivalePersonaB+'';
			NS.storeLlenarGridPagosTotal.baseParams.grupoEmpresa = grupoEmpresa+'';
			NS.storeLlenarGridPagosTotal.baseParams.jsonDocumentos= NS.rangoDoctos;
			NS.storeLlenarGridPagosTotal.baseParams.jsonProveedores= NS.rangoProv;
			NS.storeLlenarGridPagosTotal.baseParams.provedoresIndividual = NS.individualProv;
			NS.storeLlenarGridPagosTotal.baseParams.documentosIndividual = NS.individualDoc;
			NS.storeLlenarGridPagosTotal.baseParams.tipoBusqueda = NS.tipoBusqueda;
			
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridPagosTotal, msg: "Cargando Pagos ..."});
				
			NS.storeLlenarGridPagosTotal.load();
	}
	
	NS.limpiarBuscar = function(bandera){
		
		if (NS.txtRangoProvB.getValue()!=undefined) {
			NS.txtRangoProvB.setValue('');
		}
		
		if (NS.txtRangoProvA.getValue()!=undefined) {
			NS.txtRangoProvA.setValue('');
		}
		
		if (NS.txtRangoDocB.getValue()!=undefined) {
			NS.txtRangoDocB.setValue('');
		}
		
		if (NS.txtRangoDocA.getValue()!=undefined) {
			NS.txtRangoDocA.setValue('');
		}
		
		NS.cmbGrupoEmpresa.reset();
		NS.txtTipoDoc.setValue('');
		NS.txtIndicadores.setValue('');
		NS.txtIdEmpresa.setValue('');
		NS.cmbReglaNegocioUno.reset();
		NS.cmbReglaNegocioDos.reset();
		NS.txtTotalVencido.setValue('0');
		
		 var reglaNegocio = 0;
		 var fechaConIni = '';
		var fechaConFin = '';
		var fechaPropIni = '';
		var fechaPropFin = '';
		var divisa = 'MN';
		var indicadores = '';
		var numDoc = '';
		var tipoDoc = '';
		var numDocA = '';
		var numDocB = '';
		var equivalePersonaA = '';
		var equivalePersonaB = '';
		var grupoEmpresa = '';
		NS.rangoProv=null;
   		NS.rangoDoctos=null;
   		NS.individualDoc = false;
   		NS.individualProv = false;
		
		NS.banderaSeleccionado=false;
		NS.sumaNoAcreedorVencido= new Array();
		NS.sumaNoAcreedorPorVencer= new Array();
		NS.noAcreedor = new Array();
		NS.contador=0;
		NS.importeTotal=0;
		
		NS.noFactura = new Array();
		NS.contNoFactura=0;
		
		if(bandera){
			NS.cmbPlantilla.reset();
			NS.cmbDivisa.setValue('MN');
			
			NS.storeLlenarGridSolicitudPagosNivDos.removeAll(); //POR FACTURA
		//	NS.gridSolicitudPagosNivDos.getView().refresh();
			NS.storeLlenarGridSolicitudPagos.removeAll(); //POR PROVEEDOR
		//	NS.gridSolicitudPagos.getView().refresh(); 
			NS.storeLlenarGridPagosTotal.removeAll(); //EMPRESA
			
	   		NS.storeLlenaGridProveedores.removeAll();
	   		//NS.gridProveedores.getView().refresh();
	    	 
	   		NS.storeLlenaGridDocumentos.removeAll();
	   		//NS.gridDocumentos.getView().refresh();
	   		
	   		NS.storeLlenaGridIndividualesDocumentos.removeAll();
   			//NS.gridIndividualesDocumentos.getView().refresh();
   			
   			NS.storeLlenaGridIndividualesProveedores.removeAll();
   			//NS.gridIndividualesProveedores.getView().refresh();
			
			//gridSolicitudPagosTotal -->EMPRESA
			//NS.gridSolicitudPagos --> PROVEEDOR
			//NS.gridSolicitudPagosNivDos  POR FACTURA
			NS.storeLlenarGridPagosTotal.baseParams.reglaNegocio = 0;
			NS.storeLlenarGridPagosTotal.baseParams.fechaConIni = '';
			NS.storeLlenarGridPagosTotal.baseParams.fechaConFin = '';
			NS.storeLlenarGridPagosTotal.baseParams.fechaPropIni = '';
			NS.storeLlenarGridPagosTotal.baseParams.fechaPropFin = '';
			NS.storeLlenarGridPagosTotal.baseParams.divisa = 'MN';
			NS.storeLlenarGridPagosTotal.baseParams.indicadores = '';
			NS.storeLlenarGridPagosTotal.baseParams.numDoc = '';
			NS.storeLlenarGridPagosTotal.baseParams.tipoDoc = '';
			NS.storeLlenarGridPagosTotal.baseParams.numDocA = '';
			NS.storeLlenarGridPagosTotal.baseParams.numDocB = '';
			NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaA = '';
			NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaB = '';
			NS.storeLlenarGridPagosTotal.baseParams.grupoEmpresa = '';
			NS.storeLlenarGridPagosTotal.baseParams.jsonDocumentos=NS.rangoDoctos;
			NS.storeLlenarGridPagosTotal.baseParams.jsonProveedores=NS.rangoProv;
			NS.storeLlenarGridPagosTotal.baseParams.provedoresIndividual = NS.individualProv;
			NS.storeLlenarGridPagosTotal.baseParams.documentosIndividual = NS.individualDoc;
			
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridPagosTotal, msg: "Cargando Pagos ..."});
			NS.storeLlenarGridPagosTotal.load();
			
			iniciarCamposFechas();
			venDetalleUno.hide();
			venDetalleDos.hide();
			venNombrePlantilla();
		}
		
	}
	
	NS.desSeleccionar = function(noAcredorN2,tipo){
		
		
		var cadenaEquivaleP="";
		//var seleccion =  NS.storeLlenarGridSolicitudPagos.data.items;
		var pos=0;
		var seleccion = NS.storeLlenarGridSolicitudPagos.data.items;
		if (seleccion.length > 0){
			for(var y = 0; y < seleccion.length; y++){
				if(seleccion[y].get('equivalePersona')==noAcredorN2){
					pos =y;
					y=seleccion.length+1;
				}
			}
		}else{
			return;
		}
		if (seleccion.length > 0){
			
			acredor=noAcredorN2;
			noEmpresa= seleccion[pos].get('numeroEmpresa');
			cadenaEquivaleP=noAcredorN2 ;
			if(NS.cmbReglaNegocioDos.getValue() == '' || NS.cmbReglaNegocioDos.getValue() == undefined || NS.cmbReglaNegocioDos.getValue() == null){
				reglaNegocio = 0;   
			}else{
				   reglaNegocio = NS.cmbReglaNegocioDos.getValue(); //qwer
			}
		 		
				
		ConsultaPagosPendientesAction.obtenerTodosPagosPendientesPorEmpresa( noEmpresa, cadenaEquivaleP, reglaNegocio,  fechaConIni+'',  fechaConFin+'' ,
				 fechaPropIni+'',  fechaPropFin+'',  divisa+'',  indicadores+'',  numDoc+'',  tipoDoc+'',
				 numDocA+'',  numDocB+'', equivalePersona+'', equivalePersona+'' ,  grupoEmpresa+''	, '' ,	
	    function(res ) {	
			if (res != '' && res != null && res != undefined ) {
				if (res.length == null || res.length <= 0){
					
				}else{
					var indiceG=-1;
					var indiceGrid=0;
					var acredor= res[0].equivalePersona;
					vec = new Array();	
					idMatriz = sociedad + "_" + acredor;
					
					if(matrizDatosGlobal[idMatriz] != undefined && matrizDatosGlobal[idMatriz] != null && matrizDatosGlobal[idMatriz] != '')
						vec = matrizDatosGlobal[idMatriz];
					
					for(var m=0 ; m < res.length ; m++){
						indiceG++;
						if(tipo==res[m].estatus){
							bandera=false;
							vec[indiceG] = null;
							matrizDatosGlobal[idMatriz] = vec;
						}	
					}
					
				}
			}
		});
		}
	}
	
	NS.seleccionarTodos = function (tipo){
		//-->
		var seleccion =  NS.storeLlenarGridSolicitudPagos.data.items;
		NS.importeAcumuladoFloat = 0.0;
		
		for (var i = 0; i < seleccion.length; i++) {
			NS.indiceActual = sociedad + "_" + seleccion[i].get('beneficiario');
			
			if (tipo == 1) {
				if (seleccion[i].get('vencido') != 0) {
					seleccion[i].set('vencidoS',
							seleccion[i].get('vencido'));
					
					seleccion[i].set('vencidoP', 0.0);
					
					seleccion[i].set('bandera', true);
					
					NS.importeAcumuladoFloat += seleccion[i].get('vencido');
				}
		
				seleccion[i].set('bPorVencer', false);
				
				seleccion[i].set('porVencerS', 0.0);
				
				seleccion[i].set('porVencerP',
						seleccion[i].get('porVencer'));				
			} else {
				if (seleccion[i].get('porVencer') != 0) {
					seleccion[i].set('porVencerS',
							seleccion[i].get('porVencer'));
					
					seleccion[i].set('porVencerP', 0.0);
					
					seleccion[i].set('bPorVencer', true);
					
					NS.importeAcumuladoFloat += seleccion[i].get('porVencer');
				}
				
				seleccion[i].set('bandera', false);
				
				seleccion[i].set('vencidoS', 0.0);
				
				seleccion[i].set('vencidoP',
						seleccion[i].get('vencido'));
			}
			
			
			
			NS.obtenerSeleccionDelNivelTres(
					seleccion[i].get('numeroEmpresa'),
					seleccion[i].get('equivalePersona')+'',
					reglaNegocio,
					fechaConIni,
					fechaConFin,
					fechaPropIni,
					fechaPropFin,
					divisa,
					indicadores,
					numDoc+'',
					tipoDoc+'',
					numDocA+'',
					numDocB+'',
					equivalePersonaA+'',
					equivalePersonaB+'',
					grupoEmpresa+'',
					NS.rangoDoctos,
					NS.rangoProv,
					NS.indiceActual,
					tipo,
					NS.individualDoc,
					NS.tipoBusqueda
			);
			
		}
		
		NS.ImporteAcumuladoMN.setText(
				NS.cambiarFormato(
						NS.importeAcumuladoFloat.toFixed(2), '$'));
	}
	
	NS.seleccionarVencidosNvlDos = function(noAcredorN2,tipo){
		var cadenaEquivaleP="";
		//var seleccion =  NS.storeLlenarGridSolicitudPagos.data.items;
		var pos=0;
		var seleccion = NS.storeLlenarGridSolicitudPagos.data.items;
		if (seleccion.length > 0){
			for(var y = 0; y < seleccion.length; y++){
				if(seleccion[y].get('equivalePersona')==noAcredorN2){
					pos =y;
					y=seleccion.length+1;
				}
			}
		}else{
			return;
		}
		if (seleccion.length > 0){
			
			sociedad = seleccion[pos].get('numeroEmpresa');
			nombreSociedad = seleccion[pos].get('nombreEmpresa');
			
			nombreAcredor = seleccion[pos].get('beneficiario');
			importeVencido = seleccion[pos].get('vencido');
			importePOrVencer = seleccion[pos].get('porVencer');
			formaPago = seleccion[pos].get('formaPago');
			indiceGlobal = seleccion[pos].get('index');
			chequera = seleccion[pos].get('chequera');
			idBanco = seleccion[pos].get('idBanco');
			
			chequeraPagadora = "";
			noEmpresa= seleccion[pos].get('numeroEmpresa');
			equivalePersona = seleccion[pos].get('equivalePersona');
			numDocA = numDocA;
			numDocB = numDocB;
			grupoEmpresa = '';
			sumaImporte = seleccion[pos].get('vencido');
			sumaImportePorVencer = seleccion[pos].get('porVencer');
			
			if(NS.cmbReglaNegocioDos.getValue() == '' || NS.cmbReglaNegocioDos.getValue() == undefined || NS.cmbReglaNegocioDos.getValue() == null){
				reglaNegocio = 0;   
			}else{
				   reglaNegocio = NS.cmbReglaNegocioDos.getValue(); //qwer
			}
		
			cadenaEquivaleP=noAcredorN2+',';
			var aux=false;
		    nombreSociedad = seleccion[pos].get('nombreEmpresa');
			acredor=noAcredorN2;
			nombreAcredor = seleccion[pos].get('beneficiario');
			importeVencido = seleccion[pos].get('vencido');
			importePOrVencer= seleccion[pos].get('porVencer');
			if(tipo=='VENCIDO' && importeVencido!=0){
				importeSeleccionado = seleccion[pos].get('vencido');
				importeSeleccionadoPorVencer = seleccion[pos].get('porVencerS');
				banderaV=true;
				banderaPV=seleccion[pos].get('bPorVencer');
				aux=true;
			}else if('POR VENCER' && importePOrVencer!=0 ){
				importeSeleccionado = seleccion[pos].get('vencidoS');
				importeSeleccionadoPorVencer = seleccion[pos].get('porVencer');
				banderaV=seleccion[pos].get('bandera');
				banderaPV=true;
				aux=true;
			}
			formaPago = seleccion[pos].get('formaPago');
			chequera = seleccion[pos].get('chequera');
			idBanco = seleccion[pos].get('idBanco');
			
			
			if(aux){
				NS.insertarValor(acredor);
				NS.importeSeleccionado(noAcredorN2,importeSeleccionado,importeSeleccionadoPorVencer);
				cadenaEquivaleP=cadenaEquivaleP.substring(0,cadenaEquivaleP.length-1);
				
				
				//llenar grid
				
				var bandera;
				
				
				//NS.importeTotal=0;
				
				
				ConsultaPagosPendientesAction.obtenerTodosPagosPendientesPorEmpresa( noEmpresa, cadenaEquivaleP, reglaNegocio,  fechaConIni+'',  fechaConFin+'' ,
						 fechaPropIni+'',  fechaPropFin+'',  divisa+'',  indicadores+'',  numDoc+'',  tipoDoc+'',
						 numDocA+'',  numDocB+'', equivalePersona+'', equivalePersona+'' ,  grupoEmpresa+''	, '' ,	
			    function(res ) {	
					if (res != '' && res != null && res != undefined ) {
						if (res.length == null || res.length <= 0){
							
						}else{
							var indiceG=-1;
							var indiceGrid=0;
							var acredor= res[0].equivalePersona;
							vec = new Array();	
							idMatriz = sociedad + "_" + acredor;
							
							if(matrizDatosGlobal[idMatriz] != undefined && matrizDatosGlobal[idMatriz] != null && matrizDatosGlobal[idMatriz] != '')
								vec = matrizDatosGlobal[idMatriz];
							
							for(var m=0 ; m < res.length ; m++){
								indiceG++;
								if(tipo==res[m].estatus){
									bandera=false;
									vec[indiceG] = indiceG;
									matrizDatosGlobal[idMatriz] = vec;
									
									if(res[m].formaPago == 'TRANSFERENCIA'){
		        						 if(res[m].chequera != ''){
		        							 bandera = true;
		        						 }else{
		        							 bandera = false;
		        						 }
			  			
		        					 }else if(res[m].formaPago == 'CHEQUE'){
		        						 if(res[m].chequera == null || chequera == undefined){
		        							 res[m].chequera = '';
		        						 }
		        						 chequeraPagadora = '';
		        						 idBancoPagador = 0;
		        						 bandera = true;
		        					 }
									
									if(bandera){
		        						 var vecDatosPagos = {};
		        						 var posicion = res[m].factura+"R";
	        							 vecDatosPagos.renglonDet = res[m].factura;
	        							 vecDatosPagos.importe = res[m].importeNeto;
	        							 vecDatosPagos.chequera = chequera;
	        							 vecDatosPagos.idBanco = idBanco;
	        							
	        							 vecDatosPagos.chequeraP = chequeraPagadora;
	        							 vecDatosPagos.idBancoP = idBancoPagador;
	        							 vecDatosPagos.referencaBanc = NS.txtReferenciaBancaria.getValue();
	        							 vecDatosPagos.nombreProv = nombreAcredor;
	        							 vecDatosPagos.divisa = res[m].divisa;
	        							 matrizDatosPagos[posicion] = vecDatosPagos;	
	        							 NS.noFactura[NS.contNoFactura]=idMatriz+"_"+posicion+"_"+tipo;
	        							 NS.contNoFactura++;
	        							 chequeraPagadora = "";
		        						 NS.banderaSeleccionado=true;
									}
								}
									
							}
							
						}
					}
				});
			}else{
				//no se encontraron pagos pendientes.
			}
	  }
	}
	
	NS.cerrar = function(){
			
			if(NS.importeAcumuladoFloat != 0){
			 Ext.Msg.confirm( 'SET', 'Confirma que desea cerrar la ventana <br> Se perderan los registros seleccionados.' , function(btn){
				 if(btn=='yes'){
					    venDetalleUno.hide();
					    NS.banderaSeleccionado=false;		
					 	NS.sumaNoAcreedorVencido= new Array();
						NS.sumaNoAcreedorPorVencer= new Array();
						NS.noAcreedor = new Array();
						NS.noFactura = new Array();
						NS.contNoFactura=0;
						NS.contador=0;
						NS.importeTotal=0;
						
						NS.importeAcumuladoFloat = 0;
						NS.seleccionArray = new Object();
						
						NS.storeLlenarGridSolicitudPagosNivDos.removeAll(); //POR FACTURA
						NS.gridSolicitudPagosNivDos.getView().refresh();
						NS.storeLlenarGridSolicitudPagos.removeAll(); //POR PROVEEDOR
						NS.gridSolicitudPagos.getView().refresh(); 
						NS.storeLlenarGridPagosTotal.removeAll(); //EMPRESA
						venDetalleDos.hide();
				   		venDetalleUno.hide();
				   			NS.storeLlenarGridPagosTotal.baseParams.reglaNegocio = reglaNegocio;
				   			NS.storeLlenarGridPagosTotal.baseParams.fechaConIni = fechaConIni;
				   			NS.storeLlenarGridPagosTotal.baseParams.fechaConFin = fechaConFin;
				   			NS.storeLlenarGridPagosTotal.baseParams.fechaPropIni = fechaPropIni;
				   			NS.storeLlenarGridPagosTotal.baseParams.fechaPropFin = fechaPropFin;
				   			NS.storeLlenarGridPagosTotal.baseParams.divisa = divisa;
				   			NS.storeLlenarGridPagosTotal.baseParams.indicadores = indicadores;
				   			NS.storeLlenarGridPagosTotal.baseParams.numDoc = numDoc;
				   			NS.storeLlenarGridPagosTotal.baseParams.tipoDoc = tipoDoc;
				   		    NS.storeLlenarGridPagosTotal.baseParams.numDocA = numDocA;
				   		    NS.storeLlenarGridPagosTotal.baseParams.numDocB = numDocB;
				   	     	NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaA = equivalePersonaA;
				   	     	NS.storeLlenarGridPagosTotal.baseParams.equivalePersonaB = equivalePersonaB;
				   		    NS.storeLlenarGridPagosTotal.baseParams.grupoEmpresa = grupoEmpresa;
				   		    NS.storeLlenarGridPagosTotal.baseParams.jsonDocumentos=NS.rangoDoctos;
				 		    NS.storeLlenarGridPagosTotal.baseParams.jsonProveedores=NS.rangoProv;
				 		    NS.storeLlenarGridPagosTotal.baseParams.provedoresIndividual = NS.individualProv;
							NS.storeLlenarGridPagosTotal.baseParams.documentosIndividual = NS.individualDoc;
							
					   		var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridPagosTotal, msg: "Cargando Pagos ..."});
      				
				   			NS.storeLlenarGridPagosTotal.load();
				   			NS.gridSolicitudPagosTotal.getView().refresh();
				   			
				 }
			 }) 
		 }else{
			 venDetalleUno.hide();
			 NS.banderaSeleccionado=false;
			 venDetalleDos.hide();
			 NS.storeLlenarGridSolicitudPagosNivDos.removeAll(); //POR FACTURA
			NS.gridSolicitudPagosNivDos.getView().refresh();
			NS.storeLlenarGridSolicitudPagos.removeAll(); //POR PROVEEDOR
			NS.gridSolicitudPagos.getView().refresh(); 
		 }
	}
		
	NS.importeSeleccionado = function(nombreAcredor, importeSeleccionado,importeSeleccionadoPorVencer ){
		
		var indice= NS.noAcreedor.indexOf(nombreAcredor);
		 if(indice>=0){
			 NS.sumaNoAcreedorVencido [indice]= importeSeleccionado;
			 NS.sumaNoAcreedorPorVencer [indice]= importeSeleccionadoPorVencer;
		 }else{
			 NS.sumaNoAcreedorVencido [NS.contador]= importeSeleccionado;
			 NS.sumaNoAcreedorPorVencer [NS.contador]= importeSeleccionadoPorVencer;
			 NS.noAcreedor[NS.contador] = nombreAcredor;
			 NS.contador++;
		 }
		 
		var seleccionado = 0;
	
	    for(var i = 0; i < NS.sumaNoAcreedorVencido.length; i++){
	    	seleccionado +=  NS.sumaNoAcreedorVencido[i];
		}
	    
	    for(var i = 0; i < NS.sumaNoAcreedorPorVencer.length; i++){
	    	seleccionado +=  NS.sumaNoAcreedorPorVencer[i];
		}
	    //return importeSeleccionado;
	    
	    NS.ImporteAcumuladoMN.setText( NS.cambiarFormato(seleccionado.toFixed(2), '$'));
	    NS.importeTotal=seleccionado;
	}
	
	/*
	 * pago programado
	 */
	
	NS.insertarPagoProgramado = function (){
		var fecha= NS.txtfechaPropuesta.getRawValue();
		ConsultaPagosPendientesAction.pagosProgramados(fecha, function(res, e) {		
			var imp =res.split("%");
			NS.lblPgoProgramadoMN.setText('PESOS: '+ imp[0]);
			NS.lblPgoProgramadoDLS.setText('DOLARES: '+imp[1]);
        });	
	}
	
	NS.controlFechas = function(fecha, origen){
		ConsultaPagosPendientesAction.controlFechas(fecha, origen, function(resultado, e) {
			NS.txtfechaPropuesta.setValue(resultado);
			if(origen == 'load'){
				NS.txtfechaPropuesta.setMinValue(resultado);
			}	
		});
	}
	
	NS.calcularTotal = function(){
		var regSelec = NS.gridSolicitudPagosNivDos.getSelectionModel().getSelections();
		sumaImporte = 0;
		sumaImportePorVencer = 0;
		
		for ( var i = 0; i < regSelec.length; i++) {
			
			if(regSelec[i].get('estatus') == 'VENCIDO'){
				sumaImporte = sumaImporte + parseFloat(regSelec[i].get('importeNeto'));
			}else{
				sumaImportePorVencer = sumaImportePorVencer + parseFloat(regSelec[i].get('importeNeto'));
			}
				
		}
		
		NS.txtImporteSeleccionado.setValue(NS.cambiarFormato((sumaImporte + sumaImportePorVencer).toFixed(2), '$'));
		
		NS.importeVencidoNivelTres = sumaImporte;
		NS.importePorVencerNivelTres = sumaImportePorVencer;
	}
	
	NS.cambiarFecha = function(fecha){
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		var mes;
		
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
			mes = i+1;
				if(mes<10)
					mes='0'+mes;
			}
		}		
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	}
	
	NS.insertarValor = function(criterio){
		var datosClase = NS.gridSolicitudPagos.getStore().recordType;
		var recordsDatGrid = NS.storeLlenarGridSolicitudPagos.data.items;
		for(i=0;i<recordsDatGrid.length;i++){
			if(recordsDatGrid[i].get('equivalePersona') == criterio){
				NS.storeLlenarGridSolicitudPagos.remove(recordsDatGrid[i]);			
				var datos = new datosClase({
				 	numeroEmpresa: sociedad,
				 	equivalePersona: acredor,
				 	beneficiario: nombreAcredor,
				 	vencido: importeVencido,
				 	vencidoS: importeSeleccionado,
				 	vencidoP: (importeVencido - importeSeleccionado),
				 	porVencer: importePOrVencer, 
				 	porVencerS: importeSeleccionadoPorVencer,
				 	porVencerP: (importePOrVencer - importeSeleccionadoPorVencer),
				 	formaPago: formaPago,
				 	chequera: chequera,
				 	idBanco: idBanco,
				 	referenciaBanc: NS.txtReferenciaBancaria.getValue(),
				 	
				 	/*Agregar los siguientes valores al metodo*/
				 	bandera:banderaV,
				 	bPorVencer:banderaPV,
            	});
				
				NS.storeLlenarGridSolicitudPagos.insert(i, datos);
				NS.gridSolicitudPagos.getView().refresh();
        		break;
			}
		}
	};
	
	function iniciarCamposFechas(){
		ConsultaPagosPendientesAction.obtenerFechasIni(function(resultado, e) {
			NS.txtFechaRequeridaPago.setValue(resultado.fechaContableInicio);
			NS.txtFechaRequeridaPagoDos.setValue(resultado.fechaContableFin);
			NS.txtIdSolicitantePagoDos.setValue(resultado.fechaContableFin);
			NS.txtIdSolicitantePago.setValue(resultado.fechaPropuestaPagoInicio);
			
			NS.cmbDivisa.setValue('MN');
		});
	}

	NS.cambiarFormato = function(num,prefix){
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
	
	NS.llenarTxtToalPagosPendientes = function(){
		datosGrid = NS.storeLlenarGridPagosTotal.data.items;
		var totalVencido = 0;
		var totalPorvencer = 0;
		for(i = 0; i < datosGrid.length; i++ ){
			totalVencido = totalVencido + datosGrid[i].get('total');
			totalPorvencer = totalPorvencer + datosGrid[i].get('porVencer');
		}
		NS.txtTotalPorVencer.setValue(NS.cambiarFormato(totalPorvencer.toFixed(2), '$'));
		NS.txtTotalVencido.setValue(NS.cambiarFormato(totalVencido.toFixed(2), '$' ));
		
	}
	
	/*
	 * Creacion de ventanas
	 */
	
	
	var venNombrePlantilla = new Ext.Window({
		title: 'Ingrese un Nombre Para la Plantilla',
		modal: true,
		shadow: true,
		width: 500,
		height: 200,
		layout: 'absolute',
		bodyStyle: 'padding:10px;',
		closeAction: 'hide',
		items:[NS.txtNombrePlantilla],
		buttons:[
		        {
		        	text: 'Guardar Plantilla',
		        	handler: function () {
	        		   fechaConIni = NS.cambiarFecha(''+NS.txtFechaRequeridaPago.getValue());
					   fechaConFin = NS.cambiarFecha(''+NS.txtFechaRequeridaPagoDos.getValue());
					   fechaPropIni = NS.cambiarFecha(''+NS.txtIdSolicitantePago.getValue());
					   fechaPropFin = NS.cambiarFecha(''+NS.txtIdSolicitantePagoDos.getValue())
       				   var vecDatosPlantilla = {};
       				   if(NS.cmbReglaNegocioDos.getValue() == '' || NS.cmbReglaNegocioDos.getValue() == undefined || NS.cmbReglaNegocioDos.getValue() == null){
       					   vecDatosPlantilla.reglaNegocio = 0;   
     				   }else{
     					  vecDatosPlantilla.reglaNegocio = NS.cmbReglaNegocioDos.getValue();   
     				   }
      				   vecDatosPlantilla.divisa = ''+NS.cmbDivisa.getValue();
      				   vecDatosPlantilla.indicadores = NS.txtIndicadores.getValue();
      				   vecDatosPlantilla.tipoDoc = NS.txtTipoDoc.getValue();
      				   vecDatosPlantilla.numDocA = NS.txtRangoDocA.getValue()+'';
       				   vecDatosPlantilla.numDocB = NS.txtRangoDocB.getValue()+'';
       				   vecDatosPlantilla.equivalePersonaA = NS.txtRangoProvA.getValue()+'';
       				   vecDatosPlantilla.equivalePersonaB = NS.txtRangoProvB.getValue()+'';
       				   vecDatosPlantilla.grupoEmpresa= NS.txtIdEmpresa.getValue()+'';
      				   vecDatosPlantilla.nombrePlantilla = NS.txtNombrePlantilla.getValue();
      				   vecDatosPlantilla.nombreRegla = NS.cmbReglaNegocioUno.getValue();
      				   vecDatosPlantilla.fechaConIni = fechaConIni;
      				   vecDatosPlantilla.fechaConFin = fechaConFin;
      				   vecDatosPlantilla.fechaPropIni = fechaPropIni;
      				   vecDatosPlantilla.fechaPropFin = fechaPropFin;
      				  
      	
      				   var jSonString = Ext.util.JSON.encode(jQuery.makeArray(vecDatosPlantilla));	
      				   
      				   ConsultaPagosPendientesAction.guardarPlantilla(jSonString, NS.rangoDoctos ,NS.rangoProv , function(resultado, e) {
	      					 Ext.Msg.alert('SET', resultado);
	      					 NS.txtNombrePlantilla.setValue('');
	    					 venNombrePlantilla.hide();
	    					 NS.storePlantilla.load();
	    					 NS.cmbPlantilla.setValue('');
      				   });
		        		
		        	}
		        },
		        {
		        	text: 'Cancelar',
		        	handler: function () {
		        		NS.txtNombrePlantilla.setValue('');
		        		venNombrePlantilla.hide();
		        	}
		        }
		]
	
	});
	
	var venDetalleUno = new Ext.Window({
		title: 'PAGOS POR PROVEEDOR',
		//id: PF + 'ventanaNivelDos',
		draggable: false,
		modal: true,
		shadow: true,
		width: 1000,
		height: 600,
		layout: 'absolute',
		plain: true,
		bodyStyle: 'padding:10px;',
		closeAction: 'hide',
		closable:false,
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items:[
		       	NS.gridSolicitudPagos,
		       	NS.txtfechaPropuesta,
		       	NS.lblFechaPropuesta,
		        NS.lblPgoProgramadoMN, 
			   	NS.lblPgoProgramadoDLS, 
			   	NS.lblPgoProgramado,
			   	NS.ImporteAcumuladoMN,
			   	NS.ImporteAcumulado,
		       	],
		buttons:[
		         	{text:'Seleccionar Vencidos',handler:function(){NS.seleccionarTodos(1);} },
		         	{text:'Seleccionar por vencer',handler:function(){NS.seleccionarTodos(2);} },
 		          	NS.botonPropuesta,
		         	NS.botonPropuestaS,
		            {text:'Cerrar',handler:function(){NS.cerrar();} },
		         ]
	});
	
	NS.lblPagoPFactura = new Ext.form.Label({
		text: 'Factura:',
		x: 0,
		y: 5	
	});
	
	NS.lblPagoPImporte = new Ext.form.Label({
		text: 'Importe total:',
		x: 0,
		y: 45	
	});
	
	NS.lblPagoPParcial = new Ext.form.Label({
		text: 'Pago Parcial:',
		x: 0,
		y: 85	
	});
	
	NS.lblPagoPSaldoRestante = new Ext.form.Label({
		text: 'Saldo Restante:',
		x: 0,
		y: 125	
	});
	
	NS.lblPagoPFechaPago = new Ext.form.Label({
		text: 'Fecha Pago:',
		x: 0,
		y: 165	
	});
	
	NS.txtPagoPFactura = new Ext.form.TextField({
		id: PF + 'txtPagoPFactura',
		name: PF + 'txtPagoPFactura',
		x: 80,
		y: 0,
		width: 150,
		readOnly: true
	});
	
	NS.txtPagoPImporte = new Ext.form.TextField({
		id: PF + 'txtPagoPImporte',
		name: PF + 'txtPagoPImporte',
		x: 80,
		y: 40,
		width: 150,
		readOnly: true
	});
	
	NS.txtPagoPParcial = new Ext.form.TextField({
		id: PF + 'txtPagoPParcial',
		name: PF + 'txtPagoPParcial',
		x: 80,
		y: 80,
		width: 150,
		listeners: {
 			change: {
 				fn: function(caja, valor) {				
 					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined){
 						var TipoCambio = Ext.getCmp(PF + 'txtPagoPParcial').setValue(NS.cambiarFormato(Ext.getCmp(PF + 'txtPagoPParcial').getValue()));
 						NS.txtPagoPParcial.setValue(NS.txtPagoPParcial.getValue());
 						var resta = parseFloat(NS.unformatNumber(NS.txtPagoPImporte.getValue())) - parseFloat(NS.unformatNumber(NS.txtPagoPParcial.getValue()));
 						//resta = resta.toFixed(2);
 						NS.txtPagoPSaldoRestante.setValue(NS.cambiarFormato(resta.toFixed(2)));
 					}
 				}
 			}	
		}
	});
	
	NS.txtPagoPSaldoRestante = new Ext.form.TextField({
		id: PF + 'txtPagoPSaldoRestante',
		name: PF + 'txtPagoPSaldoRestante',
		x: 80,
		y: 120,
		width: 150,
		readOnly: true
	});
	
	NS.txtPagoPFechaPago = new Ext.form.DateField({
		id: PF + 'txtPagoPFechaPago',
		name: PF + 'txtPagoPFechaPago',
		format: 'd/m/Y',
		x: 80,
		y: 160,
		width: 150,
		value: apps.SET.FEC_HOY
	});
	
	NS.txtPagoPFolioDet = new Ext.form.TextField({
		id: PF + 'txtPagoPFolioDet',
		name: PF + 'txtPagoPFolioDet',
		x: 0,
		y: 160,
		width: 100,
		hidden: true,
		readOnly: true
	});
	
	NS.txtPagoPFormaPago = new Ext.form.TextField({
		id: PF + 'txtPagoPFormaPago',
		name: PF + 'txtPagoPFormaPago',
		x: 160,
		y: 160,
		width: 100,
		hidden: true,
		readOnly: true
	});
	
	NS.panelPagoParcial = new Ext.form.FieldSet ({
		x: 15,
		y: 15,
		width: 300,
		height: 220,
		layout: 'absolute',
		items: [
		     NS.lblPagoPFactura,
		     NS.lblPagoPImporte,
		     NS.lblPagoPParcial,
		     NS.lblPagoPSaldoRestante,
		     NS.lblPagoPFechaPago,
		     NS.txtPagoPFactura,
		     NS.txtPagoPImporte,
		     NS.txtPagoPParcial,
		     NS.txtPagoPSaldoRestante,
		     NS.txtPagoPFolioDet,
		     NS.txtPagoPFormaPago,
		     NS.txtPagoPFechaPago
		],
	});

	
	var winPagoParcial = new Ext.Window({
		title: 'Pago Parcial de Solicitudes',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 350,
	   	height: 310,
	   	layout: 'absolute',
	   	plain: true,
	   	resizable:false,
	   	draggable:false,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    buttons:[
			      {
			    	text:'Aceptar',
			    	handler:function(){
			    		if(NS.txtPagoPParcial.getValue() != null && NS.txtPagoPParcial.getValue() != '' && NS.txtPagoPParcial.getValue() != undefined){
			    			if(parseFloat(NS.unformatNumber(NS.txtPagoPParcial.getValue())) >= parseFloat(NS.unformatNumber(NS.txtPagoPImporte.getValue()))){
			    				Ext.Msg.alert('SET','El importe a pagar es mayor al de la solicitud');
			    				return;
			    			}	
			    			var fechaPago=NS.cambiarFecha(''+NS.txtPagoPFechaPago.getValue());
			    			ConsultaPagosPendientesAction.ejecutarPagoParcial(fechaPago,parseFloat(NS.unformatNumber(NS.txtPagoPImporte.getValue())) ,parseFloat(NS.unformatNumber(NS.txtPagoPParcial.getValue())) , ''+NS.txtPagoPFolioDet.getValue(), function(resultado, e) {
		      					winPagoParcial.hide();
		      					venDetalleDos.hide();
		      					venDetalleUno.hide();
		      					//venDetalleDos.show();
		      					NS.gridSolicitudPagosNivDos.store.removeAll();
				    			NS.gridSolicitudPagosNivDos.getView().refresh(); 
				    			NS.gridSolicitudPagos.store.removeAll();
				    			NS.gridSolicitudPagos.getView().refresh();
				    			Ext.Msg.alert('SET', resultado);
	      				   });
			    			
			    		}
			    		else
			    			Ext.Msg.alert('SET','Debe ingresar un importe para pago parcial');
			    	}
			      },
			      
			      {
				    	text:'Cancelar',
				    	handler:function(){
				    		NS.limpiarVentanaPagoParcial();
				    		winPagoParcial.hide();
				    	}
				      },
				      
	    ],
	   	items: [
	   	     NS.panelPagoParcial
	   	     ],
   	    listeners:{
   	        	show:{
   	        			fn:function(){
   	        				winPagoParcial.show();
   	        				NS.limpiarVentanaPagoParcial();
   	        				var seleccion = NS.gridSolicitudPagosNivDos.getSelectionModel().getSelections();
   	        				NS.txtPagoPFactura.setValue(seleccion[0].get('cveControl'));	
   	        				NS.txtPagoPImporte.setValue(NS.cambiarFormato(seleccion[0].get('importeNeto')));
   	        				NS.txtPagoPFolioDet.setValue(seleccion[0].get('factura'));
   	        				NS.txtPagoPFormaPago.setValue(seleccion[0].get('formaPago'));
   	        				NS.txtPagoPFechaPago.reset();
   	        				NS.index=seleccion[0].get('index');
   	        				
   	        	    }
   	        	}
   	     }	
		});
	
	NS.limpiarVentanaPagoParcial=function(){
		 NS.txtPagoPFactura.setValue('');
	     NS.txtPagoPImporte.setValue('');
	     NS.txtPagoPParcial.setValue('');
	     NS.txtPagoPSaldoRestante.setValue('');
	     NS.txtPagoPFolioDet.setValue('');
	     NS.txtPagoPFormaPago.setValue('');
	}
	

	NS.txtFechaPago = new Ext.form.DateField({
		id: PF + 'txtFechaPago',
		name: PF + 'txtFechaPago',
		format: 'd/m/Y',
		x: 0,
		y: 0,
		width: 160
	});

	NS.panelFechaPago = new Ext.form.FieldSet ({
		x: 250,
		y: 110,
		title: 'FECHA PAGO',
		width: 200,
		height: 70,
		layout: 'absolute',
		items: [
		     NS.txtFechaPago
		],
	});

	
	/*var winFechaPago = new Ext.Window({
		title: 'Cambio de fecha de pago',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 250,
	   	height: 150,
	   	layout: 'absolute',
	   	plain: true,
	   	resizable:false,
	   	draggable:false,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    buttons:[
			      {
			    	text:'Aceptar',
			    	handler:function(){
			    		if(NS.txtFechaPago.getValue() != null && NS.txtFechaPago.getValue() != '' && NS.txtFechaPago.getValue() != undefined){
			    			var seleccion = NS.gridSolicitudPagosNivDos.getSelectionModel().getSelections();
			    			var claves='';
			    			for(var i=0;i<seleccion.length;i++){
			    				claves+=seleccion[i].get('factura')+',';
			    			}
			    			var fechaPago=NS.cambiarFecha(''+NS.txtFechaPago.getValue());
			    			ConsultaPagosPendientesAction.ejecutarCambioFechaPago(fechaPago,claves, function(resultado, e) {
			    				winFechaPago.hide();
		      					venDetalleDos.hide();
		      					venDetalleUno.hide();
		      					//venDetalleDos.show();
		      					NS.gridSolicitudPagosNivDos.store.removeAll();
				    			NS.gridSolicitudPagosNivDos.getView().refresh(); 
		      				    //var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridSolicitudPagosNivDos, msg: "Cargando Pagos ..."});
				    			//NS.storeLlenarGridSolicitudPagosNivDos.load(); 
				    			NS.gridSolicitudPagos.store.removeAll();
				    			NS.gridSolicitudPagos.getView().refresh();
				    			//NS.storeLlenarGridSolicitudPagos.load();
				    			Ext.Msg.alert('SET', resultado);
	      				   });
			    			
			    		}
			    		else
			    			Ext.Msg.alert('SET','Debe ingresar una fecha de pago');
			    	}
			      },
			      
			      {
				    	text:'Cancelar',
				    	handler:function(){
				    		NS.txtFechaPago.reset();	
				    		winFechaPago.hide();
				    	}
				      },
				      
	    ],
	   	items: [
	   	     //NS.panelFechaPago
	   	     ],
   	    listeners:{
   	        	show:{
   	        			fn:function(){
   	        				NS.txtFechaPago.reset();
   	        				winFechaPago.show();					
   	        	    }
   	        	}
   	     }	
		});*/
	
	
	var winCambioChequeras = new Ext.Window({
		title: 'Cambio de Chequeras de Pago/Beneficarias. Referencia Bancaria. Fecha de Pago.',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 480,
	   	height: 250,
	   	layout: 'absolute',
	   	plain: true,
	   	resizable:false,
	   	draggable:false,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    buttons:[
			      {
			    	text:'Aceptar',
			    	handler:function(){
			    			var seleccion = NS.gridSolicitudPagosNivDos.getSelectionModel().getSelections();
			    			var claves='';
			    			for(var i=0;i<seleccion.length;i++){
			    				claves+=seleccion[i].get('factura')+',';
			    			}
			    			var fechaPago = ''+NS.txtFechaPago.getValue();
			    			if(fechaPago!=undefined && fechaPago!=''){
			    				fechaPago = NS.cambiarFecha(fechaPago);
			    			}
			    			var idBancoBenef = parseInt(NS.cmbBancos.getValue());
			    			if(isNaN(idBancoBenef)){
			    				idBancoBenef=0;
			    			}
			    			var idChequeraBenef = ''+NS.cmbChqueras.getValue();
			    			var idBancoPag = parseInt(NS.cmbBancosPagador.getValue());
			    			if(isNaN(idBancoPag)){
			    				idBancoPag=0;
			    			}
			    			var idChequeraPag = ''+ NS.cmbChquerasPagadoras.getValue();
			    			var refBancaria = ''+NS.txtReferenciaBancaria.getValue();
			    			if(fechaPago=='' && idBancoBenef==0 && idChequeraBenef=='' && idBancoPag==0 && idChequeraPag=='' && refBancaria==''){
			    				NS.limpiarVentanaCambioChequeras();
					    		winCambioChequeras.hide();
			    			}else{
			    				ConsultaPagosPendientesAction.ejecutarCambioDatosMovimiento(fechaPago,claves, idBancoBenef, idChequeraBenef, idBancoPag, idChequeraPag, refBancaria, ''+NS.tipoBusqueda, function(resultado, e) {
				    				venDetalleDos.hide();
			      					venDetalleUno.hide();
			      					winCambioChequeras.hide();
			      					NS.gridSolicitudPagosNivDos.store.removeAll();
					    			NS.gridSolicitudPagosNivDos.getView().refresh(); 
					    			NS.gridSolicitudPagos.store.removeAll();
					    			NS.gridSolicitudPagos.getView().refresh();
					    			Ext.Msg.alert('SET', resultado);
		      				   });
			    			}

				    			

			    	}
			      },
			      
			      {
				    	text:'Cancelar',
				    	handler:function(){
				    		NS.limpiarVentanaCambioChequeras();
				    		winCambioChequeras.hide();
				    	}
				      },
				      
	    ],
	   	items: [
	   	        {
				 xtype: 'fieldset',
				 id: PF + 'PanelContenedorCambioChequera',
				 title: 'CHEQUERA BENEFICIARIA',
				 layout: 'absolute',
				 x: 0,
				 y: 0,
				 width: 198,
				 height: 100,
				 hidden: false,
				 items: [
				        NS.cmbBancos,
				        NS.cmbChqueras
				        ]
			  	},
			  	{
			   	 xtype: 'fieldset',
			   	 id: PF + 'PanelContenedorCambioChequeraP',
			   	 title: 'CHEQUERA PAGADORA',
			   	 layout: 'absolute',
			   	 x: 250,
			   	 y: 0,
			   	 width: 198,
			   	 height: 100,
			   	 hidden: false,
			   	 items: [
			   	         NS.cmbBancosPagador,
			   	         NS.cmbChquerasPagadoras
			   	        ]
			  	},
			  	{
				 xtype: 'fieldset',
				 id: PF + 'PanelContenedorReferenciaBanc',
				 title: 'REFERENCIA BANCARIA',
				 layout: 'absolute',
				 x: 0,
				 y: 110,
				 width: 200,
				 height: 70,
				 hidden: false,
				   items: [
				     NS.txtReferenciaBancaria
				   ]
				       },
				       NS.panelFechaPago
			   	     ],
   	    listeners:{
   	        	show:{
   	        			fn:function(){
   	        				NS.limpiarVentanaCambioChequeras();
   	        				winCambioChequeras.show();					
   	        	    }
   	        	}
   	     }	
		});
	
	NS.limpiarVentanaCambioChequeras=function(){
		 NS.cmbBancos.reset();
		 NS.cmbChqueras.reset();
		 NS.cmbBancosPagador.reset();
		 NS.cmbChquerasPagadoras.reset();
		 NS.txtFechaPago.reset();
		 NS.txtReferenciaBancaria.reset();
	}
	
	var venDetalleDos = new Ext.Window({
		title: 'PAGOS POR FACTURA',
		modal: true,
		draggable: false,
		shadow: true,
		width: 1000,
		height: 530,
		layout: 'absolute',
		plain: true,
		bodyStyle: 'padding:10px;',
		buttonAlign: 'center',
		closeAction: 'hide',
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		listeners: {
			hide:{
				fn:function(e){
					//Ext.getCmp(PF + 'PanelContenedorCambioChequera').setVisible(false);
					//Ext.getCmp(PF + 'PanelContenedorCambioChequeraP').setVisible(false);
					//Ext.getCmp(PF + 'PanelContenedorReferenciaBanc').setVisible(false);
					//NS.txtReferenciaBancaria.setValue("");
 					//NS.cmbChqueras.setDisabled(true);
 					//NS.cmbChquerasPagadoras.setDisabled(true);
 					vec = new Array();
				}
			}
		},
		items:[
		       NS.lblEmpresaInfo,
		       NS.lblEmpresaClave,
		       NS.lblAcredorInfo,
		       NS.lblAcredorClave,
		       //NS.lblFormaPagoInfo,
		      // NS.lblFormaPagoNombre,
		       NS.lblImporteTotal,
		       NS.txtImporteTotal,
		       NS.lblImporteSeleccionado,
		       NS.txtImporteSeleccionado,
		       NS.gridSolicitudPagosNivDos,
		       
		       /*{
		    	 xtype: 'fieldset',
		    	 id: PF + 'PanelContenedorCambioChequera',
		    	 title: 'CHEQUERA BENEFICIARIA',
		    	 layout: 'absolute',
		    	 x: 300,
		    	 y: 270,
		    	 width: 198,
		    	 height: 100,
		    	 hidden: true,
		    	 items: [
		    	        NS.cmbBancos,
		    	        NS.cmbChqueras
		    	        ]
		       },
		       {
			    	 xtype: 'fieldset',
			    	 id: PF + 'PanelContenedorCambioChequeraP',
			    	 title: 'CHEQUERA PAGADORA',
			    	 layout: 'absolute',
			    	 x: 520,
			    	 y: 270,
			    	 width: 198,
			    	 height: 100,
			    	 hidden: true,
			    	 items: [
			    	         NS.cmbBancosPagador,
			    	         NS.cmbChquerasPagadoras
			    	        ]
			       },
				{
		    	 xtype: 'fieldset',
		    	 id: PF + 'PanelContenedorReferenciaBanc',
		    	 title: 'REFERENCIA BANCARIA',
		    	 layout: 'absolute',
		    	 x: 750,
		    	 y: 280,
		    	 width: 200,
		    	 height: 70,
		    	 hidden: true,
		    	 items: [
		    	         NS.txtReferenciaBancaria
		    	         ]
		       }*/
		       ],
		buttons:[
		         {
		        	 text:'Seleccionar Vencidos',
		        	 handler: function(){
		        		 var seleccionModel = NS.storeLlenarGridSolicitudPagosNivDos.data.items;
		        		 var selecciones = NS.gridSolicitudPagosNivDos.getSelectionModel(); 
		        		 console.log(seleccionModel.length);
		        		 if(seleccionModel.length > 0){
		        			 Ext.MessageBox.show({
		        			       title : 'Información SET',
		        			       msg : 'Seleccionando...',
		        			       width : 300,
		        			       wait : true,
		        			       progress:true,
		        			       waitConfig : {interval:200}
		        		   		});
		        			 
		        			 NS.banderaVencidosSeleccionados=true;
		        			 var vecPorVencer = new Array();
		        			 
		        			 for(var i = 0; i < seleccionModel.length; i++){
		        				 if(NS.gridSolicitudPagosNivDos.getSelectionModel().isSelected(i) ||
		        						 seleccionModel[i].get('estatus') == 'VENCIDO'){
		        					 vecPorVencer[i] = i;
		        					 NS.elementoSeleccionado[seleccionModel[i].get('index')]['seleccionado'] = 'X';
		        					 seleccionModel[i].set('seleccionado', 'X');
		        				 }
		        			 }
		        			
		        			 NS.gridSolicitudPagosNivDos.getSelectionModel().selectRows(vecPorVencer);
		        			 
		        			 Ext.MessageBox.hide();
		        		 }else{
		        			 Ext.Msg.alert('SET', 'No hay facturas para seleccionar')
		        		 }
		        		 
		        	 }
		         },
		         {
	 
		        	 id: PF + 'btnCambiarChequera',
		        	 text: 'Modificar Datos',
		        	 x: 200,
		        	 y: 465,
		        	 hidden: true,
		        	 listeners:{
		        		 click:{
		        			 fn:function(e){	 
		        				 var seleccion = NS.gridSolicitudPagosNivDos.getSelectionModel().getSelections();
		        				 if(seleccion.length <= 0){
		        					 Ext.Msg.alert('SET', 'No hay registros seleccionados');
		        				 }else{
		        					 winCambioChequeras.show();
		        					 /*Ext.getCmp(PF + 'PanelContenedorCambioChequera').setVisible(true);
			        				 Ext.getCmp(PF + 'PanelContenedorCambioChequeraP').setVisible(true);*/
			        				 NS.storeCmbBancos.baseParams.tipoBusqueda = NS.tipoBusqueda;
			        				 NS.storeCmbBancos.baseParams.persona = acredor+'';
			        				 NS.storeCmbBancos.load();
			        				 NS.storeCmbBancosPagador.baseParams.noEmpresa = sociedad;
			        				 NS.storeCmbBancosPagador.baseParams.divisa = divisa;
			        				 NS.storeCmbBancosPagador.load();
		        				 } 				 
		        			 }
		        		 }
		        	 }
		         },
		         {
		        	 text: 'Seleccionar y Continuar', 
		        	 x: 350,
		        	 y: 465,
		        	 width: 80,
		        	 listeners:{
		        		 click:{
		        			 fn:function(e){
		        				 
		        				 var bandera;
		        				 var contAuxVencido=0;
		        				 var contAuxPorVencer=0;
		        				 var seleccion = NS.gridSolicitudPagosNivDos.getSelectionModel().getSelections();
		        				 var totalGrid= NS.storeLlenarGridSolicitudPagosNivDos.data.items;
		        				 
		        				 //if(seleccion.length > 0) {
		        					 NS.seleccionArray[NS.indiceActual] = NS.elementoSeleccionado;
		        					 
		        					 
		        					 if(formaPago == 'TRANSFERENCIA'){
		        						 if(chequera != ''){
		        							 bandera = true;
		        						 }else{
		        							 bandera = false;
		        						 }
			  			
		        					 }else if(formaPago == 'CHEQUE'){
		        						 if(chequera == null || chequera == undefined){
		        							 chequera = '';
		        						 }
		        						 NS.txtReferenciaBancaria.setValue("");
		        						 chequeraPagadora = '';
		        						 idBancoPagador = 0;
		        						 bandera = true;
		        					 }
		        					 //if(bandera){
		        						 for(var i = 0; i < seleccion.length; i++){
		        							 var regTemp = NS.seleccionArray[NS.indiceActual][seleccion[i].get('index')];
		        							 regTemp.chequera = chequera;
		        							 regTemp.idBanco = idBanco;
		        							
		        							 regTemp.chequeraP = chequeraPagadora;
		        							 regTemp.idBancoP = idBancoPagador;
		        							 regTemp.referencaBanc = NS.txtReferenciaBancaria.getValue();
		        							 
		        							 if(seleccion[i].get('estatus')=='VENCIDO'){
		        								 contAuxVencido++;
		        							 }
		        							 
		        							 if(seleccion[i].get('estatus')=='POR VENCER'){
		        								 contAuxPorVencer++;
		        							 }
		        						 }
		        						 
		        						 
		        						 var contadorTotalAuxVen=0;
		        						 var contadorTotalAuxPorVen=0;
		        						 
	        							 for(var l=0; l < totalGrid.length ; l++){
	        								 if(totalGrid[l].get('estatus')=='VENCIDO'){
	        									 contadorTotalAuxVen++; 
	        								 }
	        								 if(totalGrid[l].get('estatus')=='POR VENCER'){
	        									 contadorTotalAuxPorVen++;
	        								 }
	        							 }
	        							 
	        							 var gridAuxN3 = NS.storeLlenarGridSolicitudPagos.data.items;
        								 for(var k=0; k < gridAuxN3.length ; k++){
	        								if(gridAuxN3[k].get('equivalePersona')==acredor){
	        									if(contAuxVencido==contadorTotalAuxVen &&contadorTotalAuxVen!=0 ){
			        								gridAuxN3[k].set('bandera', true);	
			        								banderaV=true;
	        									}else{
	        										gridAuxN3[k].set('bandera', false);	
	        										banderaV=false;
	        									}
	        									if(contAuxPorVencer==contadorTotalAuxPorVen && contadorTotalAuxPorVen!=0){ 
	        										gridAuxN3[k].set('bPorVencer', true);
	        										banderaPV=true;
	        									}else{
	        										banderaPV=false;
	        										gridAuxN3[k].set('bPorVencer', false);
	        									}
	        									
	        									gridAuxN3[k].set('vencidoS', NS.importeVencidoNivelTres);
	        									gridAuxN3[k].set('porVencerS', NS.importePorVencerNivelTres);
	        									
	        									gridAuxN3[k].set('vencidoP', 
	        											gridAuxN3[k].get('vencido') - NS.importeVencidoNivelTres.toFixed(2));
	        									gridAuxN3[k].set('porVencerP', 
	        											gridAuxN3[k].get('porVencer') - NS.importePorVencerNivelTres.toFixed(2));
	        									
	        									
	        									break;
	        								}
	        							}
        								 
        								 NS.importeAcumuladoFloat -= NS.importeVencidoResNivelTres;
        								 NS.importeAcumuladoFloat -= NS.importePorVencerResNivelTres;
        								 
        								 NS.importeAcumuladoFloat += NS.importeVencidoNivelTres;
        								 NS.importeAcumuladoFloat += NS.importePorVencerNivelTres;
        								 
        								 NS.ImporteAcumuladoMN.setText(
        											NS.cambiarFormato(
        													NS.importeAcumuladoFloat.toFixed(2), '$'));
	        							 
	        							 //NS.insertarValor(acredor);
		        						 matrizDatosGlobal[idMatriz] = vec;
		        						 vec = new Array();
		        						 //Ext.getCmp(PF + 'PanelContenedorCambioChequera').setVisible(false);
		        						 //Ext.getCmp(PF + 'PanelContenedorCambioChequeraP').setVisible(false);
		        						 //NS.cmbChqueras.setDisabled(true);
		        						 //NS.cmbChquerasPagadoras.setDisabled(true);
		        						 chequeraPagadora = "";
		        						 venDetalleDos.hide();
		        						 NS.banderaSeleccionado=true;
		        						 //YEC 25.02.16
		        						 //NS.importeSeleccionado(acredor,importeSeleccionado,importeSeleccionadoPorVencer);
	        							 
	        							 
		        					// }
		        					 //NS.storeLlenarGridSolicitudPagos.sort([{field:'vencidoS',direction: 'ASC'},{field:'porVencerS',direction: 'ASC'},{field:'beneficiario',direction: 'ASC'} ],'ASC');
//		        				 }else{
//		        					 Ext.Msg.alert('SET', "NO SE HAN SELECCIONADO DETALLES PARA LA PROPUESTA");
//		        				 } 
		        			 }
		        		 }  
		        	 }
		         	},
			         {
			        	 text: 'Pago Parcial',
			        	 x: 500,
			        	 y: 465,
			        	 listeners:{
			        		 click:{
			        			 fn:function(e){
			        				 var seleccion = NS.gridSolicitudPagosNivDos.getSelectionModel().getSelections();
			        				 if(seleccion.length <= 0){
			        					 Ext.Msg.alert('SET', 'No hay registros seleccionados');
			        				 }else if(seleccion.length > 1){
			        					 Ext.Msg.alert('SET', 'Seleccione un solo registro')
			        				 }else{
			        					 winPagoParcial.show();
			        				 }
			        			 }
			        		 }
			        	 }
			         },
			         {
			        	 text: 'Modificar fecha',
			        	 x: 650,
			        	 y: 465,
			        	 hidden: true,
			        	 listeners:{
			        		 click:{
			        			 fn:function(e){
			        				 var seleccion = NS.gridSolicitudPagosNivDos.getSelectionModel().getSelections();
			        				 if(seleccion.length <= 0){
			        					 Ext.Msg.alert('SET', 'No hay registros seleccionados');
			        				 }else{
			        					 winFechaPago.show();
			        				 }
			        			 }
			        		 }
			        	 }
			         }
		         ]
	});		
	
	/************************************************************************************************************/
	/******************************************CREACION DE PANELES*******************************************/
	/************************************************************************************************************/
	NS.PanelCargaExcelProveedores = new Ext.FormPanel({
		fileUpload: true,
        title: '',
        width: 600,
        height: 50,
        x:490,
        y:260,
    	autoScroll: true,
        renderTo: NS.tabContId,
        layout: 'absolute',
        monitorValid: true,
        items:[
			{
			    xtype: 'fileuploadfield',
			    id: PF + 'archivoExcel',
			    emptyText: 'Archivo',
			    name: PF + 'archivoExcel',
			    x:0,
			    y:0,
			    hidden: true,
			    listeners:{
			    	click:{
			    		fn:function(e){
			    			
			    			 
		        	       			
			    		}
			    	}
			    }
			},{
		 		xtype: 'button',
		 		text: 'Cargar Excel',
		 		x: 310,
		 		y: 0,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					 if (Ext.getCmp(PF + 'archivoExcel').getValue() != '' ) {
			 						NS.radioValor = '';
			                    	 var radio = NS.radioTipoExcel.getValue();
			        	       		 if(radio != null && radio != undefined ){
			        	       			NS.radioValor =NS.radioTipoExcel.getValue().getGroupValue();
			        	       			var strParams = 'nombreArchivoLeer=leerExcelRangos&nomParam1=tipo&valParam1=' + 
			        	       			NS.radioValor + '&nomParam2=rango&valParam2=' + 
			        	       			(NS.radioValor=='P' ? 
			        	       					NS.optRangoOrIndividualProv : NS.optRangoOrIndividualDoc) + '&nomParam3=noUsuario&valParam3='+apps.SET.iUserId;
		 								
		 								NS.PanelCargaExcelProveedores.getForm().submit({
		 		       						
		 			                       url: '/SET/jsp/bfrmwrk/gnrators/leerExcel.jsp'+ '?' + strParams,
		 			                       waitMsg: 'Leyendo Registros...',
		 			                       success: function (fp, o) {
		 			                    	  
		 			                       var obj = JSON.parse(o.result.json);
		 			                    	  
		 			                    	 
		 				        	       		 
		 					            	 if(NS.radioValor=='P'){
		 					            		 if (NS.optRangoOrIndividualProv == 2) {
		 					            			NS.storeLlenaGridProveedores.removeAll();
			 			        	       			NS.storeLlenaGridProveedores.loadData(obj);
			 			        	       			NS.gridProveedores.reconfigure(NS.storeLlenaGridProveedores,NS.columnasProveedores);
			 			        	       			
			 			        	       		    NS.rangoProv=Ext.util.JSON.encode(obj);
												} else {
													NS.storeLlenaGridIndividualesProveedores.removeAll();
													NS.storeLlenaGridIndividualesProveedores.loadData(obj);
			 			        	       			NS.gridIndividualesProveedores.reconfigure(NS.storeLlenaGridIndividualesProveedores,NS.columnasIndividualesProveedores);
			 			        	       			
			 			        	       		    NS.individualProv=true;
												}
		 			        	       			
		 			        	       		 }
		 			        	       			
		 			        	       		 
		 			        	       		 if(NS.radioValor=='D'){
		 			        	       			if (NS.optRangoOrIndividualDoc == 2) {
			 			        	       			NS.storeLlenaGridDocumentos.removeAll();
			 			        	       		    NS.rangoDoctos=Ext.util.JSON.encode(obj);
				 			        	       		NS.storeLlenaGridDocumentos.loadData(obj);
			 			        	       			NS.gridDocumentos.reconfigure(NS.storeLlenaGridDocumentos,NS.columnaSeleccionDocumento);
												} else {
													NS.storeLlenaGridIndividualesDocumentos.removeAll();
													NS.storeLlenaGridIndividualesDocumentos.loadData(obj);
			 			        	       			NS.gridIndividualesDocumentos.reconfigure(NS.storeLlenaGridIndividualesDocumentos,NS.columnasIndividualesDocumentos);
			 			        	       			
			 			        	       		    NS.individualDoc=true;
												}
		 			        	       			
	 			        	       			 }
		 			        	       		  
		 			                       },
		 			                       failure: function (fp, o) {
		 			                           Ext.MessageBox.alert('ERROR', 'Ocurrio un error mientras se leeía el archivo');
		 			                       }
		 		                   });
			        				 }else{
			        					 Ext.Msg.alert('SET', 'Seleccione un tipo de rango');
			        					
			        				 }
		 		            		   
		 		               }else{
		 		            	   Ext.Msg.alert('SET', 'Seleccione un archivo');
		 		               }
		 					 
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Limpiar Excel',
		 		x: 380,
		 		y: 0,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					 NS.radioValor = '';
		                    	 var radio = NS.radioTipoExcel.getValue();
		        	       		 if(radio == null || radio == undefined ){
		        	       			Ext.Msg.alert('SET', 'Seleccione un tipo de rango');
		        				 }else{
		        					NS.radioValor =NS.radioTipoExcel.getValue().getGroupValue();
		        					
		        				 }
			        	       		 
				            	 if(NS.radioValor=='P'){
				            		 if (NS.optRangoOrIndividualProv == 2) {
			            			 	NS.storeLlenaGridProveedores.removeAll();
			        	       			NS.gridProveedores.getView().refresh();
			        	       			NS.rangoProv=null;
									 } else {
										NS.storeLlenaGridIndividualesProveedores.removeAll();
 			        	       			NS.gridIndividualesProveedores.getView().refresh();
 			        	       		    NS.individualProv=false;
									 }
				            	 }
		        	       		 if(NS.radioValor=='D'){
		        	       			if (NS.optRangoOrIndividualDoc == 2) {
		        	       				NS.storeLlenaGridDocumentos.removeAll();
			        	       			NS.gridDocumentos.getView().refresh();
			        	       			NS.rangoDoctos=null;
									 } else {
										NS.storeLlenaGridIndividualesDocumentos.removeAll();
 			        	       			NS.gridIndividualesDocumentos.getView().refresh();
 			        	       		    NS.individualDoc=false;
									 }
		        	       		 }
		        	       		
		 				}
		 			}
		 		}
		 	}
		 		
       ]
	});
	
	NS.opIndividualesProveedor = new Ext.form.FieldSet({
		title: 'Proveedores',
		id: PF + 'opIndividualesProveedor',
		layout: 'absolute',
		width: 270,
		height: 200,
		x: 0,
		y: 0,
		items: [
					NS.gridIndividualesProveedores					
		        ]
	});
	
	NS.opRangosProveedor = new Ext.form.FieldSet({
		title: 'Rango de proveedores',
		id: PF + 'opRangosProveedor',
		layout: 'absolute',
		width: 270,
		height: 200,
		x: 0,
		y: 0,
		items: [
					NS.txtRangoProvB,
					NS.txtRangoProvA,
					NS.lbRangoProveedores,
					NS.gridProveedores
					
		        ]
	});
	
	NS.PanelRangoProveedores = new Ext.TabPanel({
		xtype:'tabpanel',
        id: PF+'panelRangoProveedores',
        name: PF+'panelRangoProveedores',
		region: 'center',
		width: 290,
		height: 255,
		x: 360,
		y: 0,
		split: true,
		frame: true,
		autoScroll: true,
		defaults: {labelWidth: 200, autoScroll:true},	// Default config options for child items
		defaultType: 'textfield',
		activeTab:0,
		defaults: {
	        listeners: {
	            activate: function(tab, eOpts) {
	                NS.optRangoOrIndividualProv = (
	                		tab.title == 'Individual' ? 
	                				1 : 2);
	            }
	        }
	    },
		items:[{
		    xtype:'panel',
		    title:'Individual',
		    frame: true,
		    items: [NS.opIndividualesProveedor]
		    
		  },{
		    xtype:'panel',
		    title:'Rangos',
		    frame: true,
		    items: [NS.opRangosProveedor]
		    //html: "<div id='"+PF+"treeMenuDivPerfiles' class='x-panel custom-accordion x-panel-noborder x-tree' style='width:auto;border:1px solid #c3daf9;'>divArbol</div>"
		  }
		]
	});
	
	NS.opRangoDocumentos = new Ext.form.FieldSet({
		title: 'Rango de documentos',
		id: PF + 'PanelRangoDocumentos',
		layout: 'absolute',
		width: 270,
		height: 200,
		x: 0,
		y: 0,
		items: [
					NS.txtRangoDocB,
					NS.txtRangoDocA,
					NS.lbRangoDoc,
					NS.gridDocumentos
		        ]
	});
	
	NS.opIndividualesDocumentos = new Ext.form.FieldSet({
		title: 'Documentos',
		id: PF + 'opIndividualesDocumentos',
		layout: 'absolute',
		width: 270,
		height: 200,
		x: 0,
		y: 0,
		items: [
		        NS.gridIndividualesDocumentos
		        ]
	});
	
	NS.PanelRangoDocumentos = new Ext.TabPanel({
		xtype:'tabpanel',
        id: PF+'panelRangoDocumentos',
        name: PF+'panelRangoDocumentos',
		region: 'center',
		width: 290,
		height: 255,
		x: 660,
		y: 0,
		split: true,
		frame: true,
		autoScroll: true,
		defaults: {labelWidth: 200, autoScroll:true},	// Default config options for child items
		defaultType: 'textfield',
		activeTab:0,
		defaults: {
	        listeners: {
	            activate: function(tab, eOpts) {
	                NS.optRangoOrIndividualDoc = (
	                		tab.title == 'Individual' ? 
	                				1 : 2);
	            }
	        }
	    },
		items:[{
		    xtype:'panel',
		    title:'Individual',
		    frame: true,
		    items: [NS.opIndividualesDocumentos]
		    
		  },{
		    xtype:'panel',
		    title:'Rangos',
		    frame: true,
		    items: [NS.opRangoDocumentos]
		    //html: "<div id='"+PF+"treeMenuDivPerfiles' class='x-panel custom-accordion x-panel-noborder x-tree' style='width:auto;border:1px solid #c3daf9;'>divArbol</div>"
		  }
		]
	});
	
	NS.PanelContenedorCabezera = apps.SET.btnFacultativo(new Ext.form.FieldSet({
		title: 'Criterios de Busqueda',
		id: 'eppfiltros',
		layout: 'absolute',
		width: 978,
		height: 360,
		y: 415,
		buttonAlign: 'center',
		items: [
		        	NS.lblReglaNegocio,
		        	NS.cmbReglaNegocioUno,
		        	NS.cmbReglaNegocioDos,
		        	
		        	NS.lblFechaRequeridaPago,
		        	NS.txtFechaRequeridaPago,
		        	NS.txtFechaRequeridaPagoDos,
		        	
		        	NS.lblIdSolicitantePago,
		        	NS.txtIdSolicitantePago,
		        	NS.txtIdSolicitantePagoDos,
		        	
		        	NS.lblIndicadores,
		        	NS.txtIndicadores,
		        	NS.tipoDocumento,
		        	//NS.plantilla,
		        	NS.txtTipoDoc,
		        	//NS.cmbPlantilla,
		        	
		        	NS.PanelRangoProveedores,
		        	NS.PanelRangoDocumentos,
		        	NS.lbReglaNegTipo,
					
					NS.lbGrupoEmpresa,
					NS.cmbGrupoEmpresa,
					NS.txtIdEmpresa,
					NS.PanelCargaExcelProveedores,
					NS.radioTipoExcel

		],
		buttons:[
		         {text: 'Buscar', handler:function(){NS.cargarPagosPorEmpresa();}},
				 //{text: 'Guardar plantilla',handler:function(){venNombrePlantilla.show();}}, 
    		     {text: 'Limpiar', handler:function(){NS.limpiarBuscar(true);}},    
		         ]
	}));
	
	NS.PanelContenedorCentral = new Ext.form.FieldSet({
		title: 'PAGOS POR EMPRESA',
		id: PF + 'PanelContenedorCentral',
		layout: 'absolute',
		width: 978,
		height: 400,
		x: 0,
		y: 0,
		items: [
		        NS.radioTipoFiltro,
		        NS.gridSolicitudPagosTotal,
		        NS.lblMontoTotalPagosPendientes,
		        NS.txtTotalPorVencer,
		        NS.txtTotalVencido,
	        	NS.cmbDivisa,
		        ]
	});
			
	NS.PanelContenedorGeneral = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorGeneral',
		layout: 'absolute',
		width: 1010,
		height: 800,
		items: [
		        NS.PanelContenedorCabezera,
		        NS.PanelContenedorCentral,
		        ]
	});
	
	NS.ConsultaPagosPendientes = new Ext.form.FormPanel({
		title: 'Propuesta de Pago Manual',
		width: 1010,
		height: 800,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'ConsultaPagosPendientes',
		name: PF + 'ConsultaPagosPendientes',
		renderTo: NS.tabContId,
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items: [
		        	NS.PanelContenedorGeneral
		        ]
	});
	
	NS.ConsultaPagosPendientes.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
});