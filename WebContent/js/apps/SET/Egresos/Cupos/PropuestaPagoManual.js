Ext.onReady(function(){

	Ext.QuickTips.init();	
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Egresos.Cupos.PropuestaPagoManual');
	NS.tabContId = apps.SET.tabContainerId;	
	//NS.tabContId = 'contenedorPropuesta';
	var PF = apps.SET.tabID+'.';
	NS.compraTranfer = false;
	NS.form = 243;
	//  INICIO datos de function openVentana 
	NS.Persona = '';
	NS.piNoEmpresa = ''; 
	NS.giBanco = 0;
	NS.gsChequera = '';
	//  FIN datos de function openVentana	
	NS.regis = 0;
	//NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.GS_DESC_CAJA = apps.SET.DESC_CAJA;
	NS.GI_ID_CAJA = apps.SET.ID_CAJA;
	NS.GI_USUARIO = apps.SET.iUserId;
	NS.GI_NO_EMPRESA = apps.SET.NO_EMPRESA;
	NS.ls_desc_caja = NS.GS_DESC_CAJA;
	NS.pbCodbloqueo = '';
	NS.fechaManana = '';
	NS.psFecPropuesta = '';
	NS.psFolios = '';
	NS.bloqueo = '';
	NS.totalImporte = 0;
	NS.contImporte = 0;
	
	if(NS.cmbBancoWin)
		NS.cmbBancoWin.destroy();
	
	if(NS.cmbChequeraWin)
		NS.cmbChequeraWin.destroy();
	
	PropuestaPagoManualAction.muestraComponentes(NS.form,function(result, e){
		if(result == "SI"){			
			/*
			 * 
			 * 
			 * 
			 * coregir esta parte cambiar el false por true
			 * 
			 * 
			 * 
			 * 
			 */
			Ext.getCmp(PF + 'division').setVisible(false);
			Ext.getCmp(PF + 'cmbDivision').setVisible(false);
			NS.storeDivision.load();
		}
	});
	
	//storeEmpresas
    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idUsuario:NS.GI_USUARIO
		},
		root: '',
		paramOrder:['idUsuario'],
		directFn: CapturaSolicitudesPagoAction.obtenerEmpresas, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No tiene empresas asignadas');
				}
			}
		}
	}); 
	NS.storeEmpresas.load();
	
	//combo Empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas
		,name: PF+'cmbEmpresas'
		,id: PF+'cmbEmpresas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 70
        ,y: 15
        ,width: 300
        ,tabIndex: 1
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: apps.SET.NOM_EMPRESA
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresas.getId());
						NS.accionarEmpresas(combo.getValue());
				}
			}
		}
	});	
	
	
	NS.storeDivision = new Ext.data.DirectStore({     
		paramsAsHash: false,
		root: '',
		baseParams: {
		 usuario: NS.GI_USUARIO
    	},
		paramOrder:['usuario'],
		directFn: PropuestaPagoManualAction.obtenerDivision, 
		idProperty: 'division', 
		fields: [
			 {name: 'division'},
			 {name: 'division'}
			 	],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisaVenta, msg:"Cargando..."});
				if(records.length == null || records.length <= 0){
					if(NS.cmbDivision.isVisible() == true)
						Ext.Msg.alert('SET','No tiene divisiones asignadas');
				}
					   		
			}
		}
	});
	
 
 NS.cmbDivision = new Ext.form.ComboBox({
		store: NS.storeDivision,
		id: PF + 'cmbDivision',
		name: PF + 'cmbDivision',
		x: 10,
        y: 200,
        width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		hidden: true,     
		forceSelection: true,
        tabIndex: 1,
		valueField: 'division',
		displayField: 'division',
		autocomplete: true,
		emptyText: 'Seleccione una Division ',
		triggerAction: 'all',  
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 
				}
			}
		}   
	});
 
 NS.labDivision = new Ext.form.Label({
		id: PF + 'division',
		name: PF + 'division',   
		text: 'Pago a cuenta de: ',
		hidden: true,
		x: 10,
		y: 180,
		visible: false
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
				if(records.length <= 0) Ext.Msg.alert('SET','No tiene empresas asignadas!!');
			/*alert(records.length);
			
				var recordsEmpresas = NS.storeCmbEmpresa.recordType;	
				var todas = new recordsEmpresas({
			       	id : 0,
			       	descripcion : '------------TODAS------------'
		       	});
		   		NS.storeCmbEmpresa.insert(0,todas);
		   		//Ext.getCmp(PF+'txtEmpresa').setValue(records[0].get('id'));
		   		Ext.getCmp(PF+'cmbEmpresasUsuario').setValue('------------TODAS------------');
		   		*/	
			}
		}
	}); 
	
	//combo
	NS.storeCmbEmpresa.load();
	
	NS.cmbEmpresasUsuario = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa
		,name: PF+'cmbEmpresasUsuario'
		,id: PF+'cmbEmpresasUsuario'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 60
        ,y: 16
        ,width: 150
        ,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: NS.GS_DESC_EMPRESA
		,listeners:{
			select:{
			fn:function(combo, valor) {
				BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresasUsuario.getId());
				NS.accionarCmbEmpresasUsuario(combo.getValue());
				}
			}
		}
	});
	
	NS.accionarCmbEmpresasUsuario = function(comboValor)
	{
		NS.storeGrupoEmpresa.removeAll();
		NS.cmbEmpresa.reset();
		NS.GI_NO_EMPRESA = comboValor;
		NS.storeGrupoEmpresa.baseParams.noEmpresa = comboValor;
		NS.storeGrupoEmpresa.load();
	}

	NS.sm = new Ext.grid.CheckboxSelectionModel({
	    singleSelect: false
	    ,header: ''
	    ,checkOnly: true,
	    forceFit: false,
		fixed:true	
	});
	
	//funcion para obtener la fecha de mañana
	NS.obtenerFechaManana = function(){
		PropuestaPagoManualAction.obtenerFechaHoy(function(result, e){
			NS.fechaManana = ''+result;
			Ext.getCmp(PF+'txtFecPago').setValue(NS.fechaManana);
		});
	}
	NS.obtenerFechaManana();
//	PropuestaPagoManualAction.controlProyecto(4, 3, function(result, e){
//		if(result != null && result != undefined)
//		{
//			if(result == true)
//				NS.pbCodbloqueo == true;
//			else 
//				NS.pbCodbloqueo == false;
//		}	
//	});
	
	
	/**data del store criterio*/
    NS.dataCriterio = [ //[ '0', 'FECHAS' ]
						[ '1', 'ESTATUS' ]
//					 	,[ '2', 'BANCO RECEPTOR' ]
						,[ '3', 'CONCEPTO' ]
//						,[ '4', 'LOTE ENTRADA' ]
//						,[ '5', 'CAJA' ]
//						,[ '6', 'BANCO PAGADOR' ]
//						,[ '7', 'DIVISION' ]
						,[ '8', 'CLAVE OPERACION' ]
						,[ '9', 'BLOQUEO' ]
//						,[ '10', 'NUMERO DE PROVEEDOR' ]
//						,[ '11', 'NOMBRE DEL PROVEEDOR' ]
						,[ '12', 'FACTURA' ]
						,[ '13', 'NUMERO DE PEDIDO' ]
//						,[ '14', 'ORIGEN DEL MOVIMIENTO' ]
//						,[ '15', 'RUBRO DEL MOVIMIENTO' ]
//						,[ '16', 'CHEQUERA BENEF.' ]
//						,[ '17', 'CHEQUERA PAGADORA' ]
//						,[ '18', 'NUMERO DE DOCUMENTO' ]
//						,[ '19', 'CLASE DEL DOCUMENTO' ]
						,[ '20', 'MONTOS' ]
					  ];
					  
	/**store criterio*/
	NS.storeCriterio = new Ext.data.SimpleStore( {
			idProperty: 'idCriterio',  		
			fields : [ 
						{name :'idCriterio'}, 
						{name :'descripcion'}
					 ]
	});
	NS.storeCriterio.loadData(NS.dataCriterio);
	
	/**data del store bloqueo*/
    NS.dataBloqueo = [ [ '0', 'SI' ],
					   [ '1', 'NO' ]
					 ];
					  
	NS.storeBloqueo = new Ext.data.SimpleStore( {
			idProperty: 'idBloqueo',  		
			fields : [ 
						{name :'idBloqueo'}, 
						{name :'descripcion'}
					 ]
	});
	
	/**data del store estatus*/
    NS.dataEstatus = [ [ '0', 'CAPTURADO' ],
					   [ '1', 'FILIAL' ],
					   [ '2', 'IMPORTADO' ]
					 ];
					  
	NS.storeEstatus = new Ext.data.SimpleStore( {
			idProperty: 'idEstatus',  		
			fields : [ 
						{name :'idEstatus'}, 
						{name :'descripcion'}
					 ]
	});
	
	//combo bloqueo
	NS.cmbBloqueo = new Ext.form.ComboBox({
		store: NS.storeBloqueo
		,name: PF+'cmbBloqueo'
		,id: PF+'cmbBloqueo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 85
        ,width: 150
		,valueField:'idBloqueo'
		,displayField:'descripcion'
		,autocomplete: true
		,hidden: true
		,emptyText: 'Bloqueo'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					var indice=0;
					var i=0;
					var datosClase = NS.gridDatos.getStore().recordType;
					var recordsDatGrid=NS.storeDatos.data.items;
					var records=NS.storeBloqueo.data.items;
					NS.bloqueo = '';
					//barrido al grid para obtener la descripcion
					for(var j = 0; j< records.length; j++)
					{
						if(records[j].get('idBloqueo') == combo.getValue())
						{
							NS.bloqueo = records[j].get('descripcion');
						}
					}
			    	for(i=0;i<recordsDatGrid.length;i++)
					{
						if(recordsDatGrid[i].get('criterio')==='BLOQUEO')
						{
							indice=i;
							NS.storeDatos.remove(recordsDatGrid[indice]);
						}
					}
						
			    	var datos = new datosClase({
                			criterio: 'BLOQUEO',
							valor: NS.bloqueo
           			});
                    NS.gridDatos.stopEditing();
           			NS.storeDatos.insert(indice, datos);
           			NS.gridDatos.getView().refresh();
					combo.setDisabled(true);
					Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				}
			}
		}
	});
	//combo estatus
	NS.cmbEstatus = new Ext.form.ComboBox({
		store: NS.storeEstatus
		,name: PF+'cmbEstatus'
		,id: PF+'cmbEstatus'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 85
        ,width: 150
		,valueField:'idEstatus'
		,displayField:'descripcion'
		,autocomplete: true
		,hidden: true
		,emptyText: 'Estatus'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					var indice=0;
					var i=0;
					var j=0;
					var datosClase = NS.gridDatos.getStore().recordType;
					var recordsDatGrid=NS.storeDatos.data.items;
				 	NS.estatus = '';
				 	var records=NS.storeEstatus.data.items;
					//barrido al grid para obtener la descripcion
					for(j = 0; j< records.length; j++)
					{
						if(records[j].get('idEstatus') == combo.getValue())
						{
							NS.estatus = records[j].get('descripcion');
						}
					}
					
			    	for(i=0;i<recordsDatGrid.length;i++)
					{
						if(recordsDatGrid[i].get('criterio')=='ESTATUS')
						{
							indice=i;
							NS.storeDatos.remove(recordsDatGrid[indice]);
						}
					}
						
			    	var datos = new datosClase({
                			criterio: 'ESTATUS',
							valor: NS.estatus
            			});
                    NS.gridDatos.stopEditing();
           			NS.storeDatos.insert(indice, datos);
           			NS.gridDatos.getView().refresh();
            			
					combo.setDisabled(true);
					Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				}
			}
		}
	});
	
	 NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			psDivisa:''
		},
		root: '',
		paramOrder:['psDivisa'],
		root: '',
		directFn: PropuestaPagoManualAction.llenarComboDivisa, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen divisas asignadas para la empresa');
				}
			}
		}
	}); 
	
	//metodo que acciona el combo de divisa en el evento click y change
	NS.accionarCmbDivisa = function(comboValue)
	{
		Ext.getCmp(PF+'txtDivisaPago').setValue(comboValue);
		Ext.getCmp(PF+'cmbDivisaPago').setValue(comboValue);
		NS.storeBancoWin.baseParams.idDivisa = comboValue;
		NS.storeChequeraWin.baseParams.idDivisa = comboValue;
		
		var records=NS.storeDivisa.data.items;
		var divisa ='';
		//barrido al grid para obtener la descripcion
		for(var j = 0; j< records.length; j++)
		{
			if(records[j].get('idDivisa') == comboValue)
			{
				divisa = records[j].get('descDivisa');
			}
		}

		var datosClase = NS.gridDatos.getStore().recordType;
		var indice=0;
		var i=0;
		var recordsDatGrid=NS.storeDatos.data.items;
		var tamGrid=(NS.storeDatos.data.items).length;
		for(i=0;i<recordsDatGrid.length;i++)
		{
			if(recordsDatGrid[i].get('criterio')=='DIVISA')
			{
				indice=i;
				//NS.storeDatos.remove(recordsDatGrid[indice]);
			}
		}
		if(indice>0)
		{
			Ext.Msg.alert('SET','Ya indicó DIVISA necesita borralo antes');
			return;
		}
		else
		{
			var datos = new datosClase({
	  			criterio: 'DIVISA',
				valor: divisa
			});
	      	NS.gridDatos.stopEditing();
			NS.storeDatos.insert(tamGrid, datos);
			NS.gridDatos.getView().refresh();
		}
	}
	
	NS.storeDivisa.load();
	//combo divisas
		NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa
		,name: PF+'cmbDivisa'
		,id: PF+'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 60
        ,y: 105
        ,width: 150
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione una divisa'
		,triggerAction: 'all'
//		,value: 'PESOS'
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					//linea cambia combo
					BFwrk.Util.updateComboToTextField(PF+'txtDivisa',NS.cmbDivisa.getId());
					NS.accionarCmbDivisa(combo.getValue());
				}
			}
		}
	});
	
	//combo divisas Pago
		NS.cmbDivisaPago = new Ext.form.ComboBox({
		store: NS.storeDivisa
		,name: PF+'cmbDivisaPago'
		,id: PF+'cmbDivisaPago'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 400
        ,y: 225//170
        ,width: 140
        ,disabled: true
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione una divisa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						Ext.getCmp(PF+'txtDivisaPago').setValue(combo.getValue());
				}
			}
		}
	});
	
	//store grupo empresa
	NS.storeGrupoEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noUsuario: NS.GI_USUARIO,
			pagoEmpresa: 1,
			noEmpresa: apps.SET.NO_EMPRESA
		},
		root: '',
		paramOrder:['noUsuario','pagoEmpresa','noEmpresa'],
		root: '',
		directFn: PropuestaPagoManualAction.llenarComboGrupo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen grupos asignados para el usuario');
				}
			}
		}
	}); 
	NS.storeGrupoEmpresa.load();
	
	//combo grupo
		NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeGrupoEmpresa
		,name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 150
        ,width: 200
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un grupo'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						var desc = NS.storeGrupoEmpresa.getById(combo.getValue()).get('descripcion');
						NS.storeEmpresaWin.baseParams.pagoEmpresa = combo.getValue();
						NS.storeEmpresaWin.load();
						NS.propuestas.baseParams.idGrupoEmpresa = combo.getValue();
						NS.propuestas.load();
				}
			}
		}
	});
	
	//store forma pago
	NS.storeFormaPago = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_forma_pago',
			campoDos:'desc_forma_pago',
			tabla:'cat_forma_pago',
			condicion:'id_forma_pago <> 6',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: PropuestaPagoManualAction.llenarCombo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No hay formas de pago disponibles');
				}
				else
				{
				//cargar el primer parametro del grid 
				 	var datosClase = NS.gridDatos.getStore().recordType;
			    	var datos = new datosClase({
		                	criterio: 'FORMA DE PAGO',
							valor: ''
	            	});
                    NS.gridDatos.stopEditing();
            		NS.storeDatos.insert(0, datos);
            		NS.gridDatos.getView().refresh();
				}
			}
		}
	}); 
	NS.storeFormaPago.load();
	//combo grupo
		NS.cmbFormaPago = new Ext.form.ComboBox({
		store: NS.storeFormaPago
		,name: PF+'cmbFormaPago'
		,id: PF+'cmbFormaPago'
		,typeAhead: true
		,loadingText: 'Searching...' 
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10 
        ,y: 60
        ,width: 200
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione forma de pago'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					var forma = NS.storeFormaPago.getById(combo.getValue()).get('descripcion');
					var datosClase = NS.gridDatos.getStore().recordType;
					var indice=0;
					var i=0;
					var recordsDatGrid=NS.storeDatos.data.items;
					if(forma == 'TRANSFERENCIA'){
						Ext.getCmp('actua').enable();
						Ext.getCmp('cambia').enable();
					}   
					
					for(i=0;i<recordsDatGrid.length;i++)
					{
						if(recordsDatGrid[i].get('criterio')=='FORMA DE PAGO')
						{
							indice=i;
							NS.storeDatos.remove(recordsDatGrid[indice]);
						}
					}
					 var datos = new datosClase({
	                	criterio: 'FORMA DE PAGO',
						valor: forma
	            	});
                    NS.gridDatos.stopEditing();   
            		NS.storeDatos.insert(indice, datos);
            		NS.gridDatos.getView().refresh();
				}
			}
		}
	});
	
	NS.propuestas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
  	 	baseParams: {
			idGrupoEmpresa: -1
		},
		paramOrder:['idGrupoEmpresa'],
		directFn: PropuestaPagoManualAction.consultarPropuestasAgregar,
		idProperty: 'cveControl',
		fields: [
			{name: 'cveControl'},
			{name: 'concepto'},
			{name: 'idBanco'},
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.propuestas, msg:"Buscando..."});
				//Ext.MessageBox.hide();
			}
		}
	});
	
	//combo propuetas
		NS.cmbPropuestasAct = new Ext.form.ComboBox({
		store: NS.propuestas
		,name: PF+'cmbPropuestasAct'
		,id: PF+'cmbPropuestasAct'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 630
        ,y: 225
        ,width: 330
		,valueField:'cveControl'
		,displayField:'concepto'
		,autocomplete: true
		,emptyText: 'Nueva Propuesta'
		,triggerAction: 'all'
		,value: ''
		//,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						 
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
		],
		listeners: {
			load: function(s, records){
		
			}
		}
	}); 
	//grid criterios
	NS.gridDatos  = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		id: PF+'gridDatos',
		name: PF+'gridDatos',
		width: 320,
       	height: 135,
		x :220,
		y :5,
		frame: true,
		title :'',
		columns : [ 
		{
			id :'criterio',
			header :'Criterio',
			width :150,
			dataIndex :'criterio'
		}, 
		{
			header :'Valor',
			width :150,
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
	
	//combo criterio
	NS.cmbCriterio = new Ext.form.ComboBox({
		 store: NS.storeCriterio 	
		,name: PF+'cmbCriterio'
		,id: PF+'cmbCriterio'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 10
		,y: 25
		,width :150
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
					
					if(combo.getValue() == 1)
					{
						Ext.getCmp(PF+'cmbEstatus').show();
						Ext.getCmp(PF+'cmbBloqueo').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtCveOperacion').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFactura').hide();
						Ext.getCmp(PF+'txtNoPedido').hide();
						NS.storeEstatus.loadData(NS.dataEstatus);
						Ext.getCmp(PF+'cmbEstatus').setDisabled(false);
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='ESTATUS')
								indice=i;
						}
						
						if(indice>0)
						{
							Ext.Msg.alert('SET','Ya indicó ESTATUS necesita borralo antes');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'ESTATUS',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}
					
					else if(combo.getValue() == 3)
					{
						Ext.getCmp(PF+'txtCveOperacion').hide();
						Ext.getCmp(PF+'txtConcepto').show();
						Ext.getCmp(PF+'txtConcepto').setValue('');
						Ext.getCmp(PF+'cmbBloqueo').hide();
						Ext.getCmp(PF+'cmbEstatus').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtFactura').hide();
						Ext.getCmp(PF+'txtNoPedido').hide();
						
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
							Ext.Msg.alert('SET','Ya indicó CONCEPTO necesita borralo antes');
							combo.setDisabled(false);
							return;
						}
						
	            		else
	            		{
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
					else if(combo.getValue() == 8)
					{
						Ext.getCmp(PF+'txtCveOperacion').show();
						Ext.getCmp(PF+'txtCveOperacion').setValue('');
						Ext.getCmp(PF+'cmbBloqueo').hide();
						Ext.getCmp(PF+'cmbEstatus').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFactura').hide();
						Ext.getCmp(PF+'txtNoPedido').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						
						for(i=0;i<recordsDatGrid.length;i++)
						{	
							if(recordsDatGrid[i].get('criterio')==='CLAVE OPERACION')
								indice=i;
						}
						
						if(indice>0)
						{
							Ext.Msg.alert('SET','Ya indicó CLAVE OPERACION necesita borrala antes');
							combo.setDisabled(false);
							return;
						}
						
	            		else
	            		{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'CLAVE OPERACION',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
	            		}
						
					}
					else if(combo.getValue() == 9)
					{
						Ext.getCmp(PF+'cmbBloqueo').show();
						Ext.getCmp(PF+'cmbEstatus').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtCveOperacion').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFactura').hide();
						Ext.getCmp(PF+'txtNoPedido').hide();
						NS.storeBloqueo.loadData(NS.dataBloqueo);
						Ext.getCmp(PF+'cmbBloqueo').setDisabled(false);
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{	
							if(recordsDatGrid[i].get('criterio')==='BLOQUEO')
								indice=i;
						}
						
						if(indice>0)
						{
							Ext.Msg.alert('SET','Ya indicó BLOQUEO necesita borralo antes');
							combo.setDisabled(false);
							return;
						}
						
	            		else
	            		{
	            			combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'BLOQUEO',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
		            	}
						
					}
					else if(combo.getValue() == 12)
					{
						Ext.getCmp(PF+'txtFactura').show();
						Ext.getCmp(PF+'cmbBloqueo').hide();
						Ext.getCmp(PF+'cmbEstatus').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtCveOperacion').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtNoPedido').hide();
						NS.storeBloqueo.loadData(NS.dataBloqueo);
						Ext.getCmp(PF+'cmbBloqueo').setDisabled(false);
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{	
							if(recordsDatGrid[i].get('criterio')==='FACTURA')
								indice=i;
						}
						
						if(indice>0)
						{
							Ext.Msg.alert('SET','Ya indicó FACTURA necesita borrala antes');
							combo.setDisabled(false);
							return;
						}
						
	            		else
	            		{
	            			combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'FACTURA',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
		            	}
					}
					else if(combo.getValue() == 13)
					{
						Ext.getCmp(PF+'txtNoPedido').show();
            			Ext.getCmp(PF+'txtFactura').hide();
						Ext.getCmp(PF+'cmbBloqueo').hide();
						Ext.getCmp(PF+'cmbEstatus').hide();
						Ext.getCmp(PF+'txtMonto1').hide();
						Ext.getCmp(PF+'txtMonto2').hide();
						Ext.getCmp(PF+'txtCveOperacion').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						NS.storeBloqueo.loadData(NS.dataBloqueo);
						Ext.getCmp(PF+'cmbBloqueo').setDisabled(false);
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{	
							if(recordsDatGrid[i].get('criterio')==='NUMERO DE PEDIDO')
								indice=i;
						}
						
						if(indice>0)
						{
							Ext.Msg.alert('SET','Ya indicó NUMERO DE PEDIDO necesita borralo antes');
							combo.setDisabled(false);
							return;
						}
						
	            		else
	            		{
	            			combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'NUMERO DE PEDIDO',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
		            	}
					}
					else if(combo.getValue()== 20)
					{
						Ext.getCmp(PF+'txtMonto1').setDisabled(false);
						Ext.getCmp(PF+'txtMonto1').setValue('');
						Ext.getCmp(PF+'txtMonto2').setValue('');
						Ext.getCmp(PF+'txtMonto1').show();
						Ext.getCmp(PF+'txtMonto2').show();
						Ext.getCmp(PF+'cmbBloqueo').hide();
						Ext.getCmp(PF+'cmbEstatus').hide();
						Ext.getCmp(PF+'txtCveOperacion').hide();
						Ext.getCmp(PF+'txtConcepto').hide();
						Ext.getCmp(PF+'txtFactura').hide();
						Ext.getCmp(PF+'txtNoPedido').hide();
						
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
							Ext.Msg.alert('SET','Ya indicó MONTOS necesita borralos antes');
							combo.setDisabled(false);
							return;
						}
	            		
	            		else
	            		{	
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
				}
			}	
		}
	});
	
	//store del GridPropuesta
	NS.storeGridPropuesta = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {criterios: NS.criterios},
		paramOrder:['criterios'],
		directFn: PropuestaPagoManualAction.consultarPropuestas,
		root: '',
		totalProperty: 'total', //<-- parametro que se le pasa al pagingToolBar ({2})
		idProperty: 'noFolioDet',
		fields: [
			{name: 'nomEmpresa'},
			{name: 'noEmpresa'},
			{name: 'noFactura'},
			{name: 'fecValor'},
			{name: 'beneficiario'},
			{name: 'importe'},
			{name: 'importeOriginal'},
			{name: 'descEstatus'},
			{name: 'idDivisa'},
			{name: 'descFormaPago'},
			{name: 'descBanco'},
			{name: 'noCliente'},
			{name: 'idChequeraBenef'},
			{name: 'descCveOperacion'},
			{name: 'concepto'},
			{name: 'origenMov'},
			{name: 'idCaja'},
			{name: 'noPedido'},
			{name: 'noDocto'},
			{name: 'division'},
			{name: 'operMayEsp'},
			{name: 'clabe'},
			{name: 'difDias'},
			{name: 'noFolioDet'},
			{name: 'noFolioMov'},
			//AGREGADOS PARA PAGO PARCIAL JRBM
			{name: 'autoriza'},
			{name: 'idLeyenda'},
			{name: 'idBancoBenef'},
			{name: 'idChequera'},
			{name: 'idFormaPago'},
			{name: 'solicita'},
			{name: 'tipoCambio'},
			{name: 'idRubro'},
			{name: 'idGrupoFlujo'},
			{name: 'observacion'},
			{name: 'idBanco'},
			{name: 'confir'},
			{name: 'descGrupo'},
			{name: 'descRubro'}
		],
		listeners: {
			load: function(s, records){
				Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridPropuesta, msg:"Buscando..."});
				var auxg;
				for(i = 0; i < records.length;i++){
					auxg = records[i];
					auxg.set('confir',0);
				}
				$("div [id*='gridPropuesta'] div[class*='x-grid3-header-inner'] td[class*='x-grid3-td-checker'] div").after('<input type="checkbox" name="checkAll" id="checkAll" style="margin-left:2px"/>');
	
				$("#checkAll").click(function(){
					var suma ="0";
					if($("#checkAll").attr('checked')){
						$("div [id*='gridPropuesta'] div [class*='x-grid3-row-table']").addClass('x-grid3-row-selected');
						$('div[class*="x-grid3-col-7"]').each(function(index,element){
						   var  data=$(element).html().split('$')[1].replace(',',''); 
						   data = data.replace(',','');
							suma = Number(suma + (Math.round((data) * 100) / 100));
							$("input[id*='txtTotalPago']").val(suma);
						});
					}
					else{
						$("div [id*='gridPropuesta'] div [class*='x-grid3-row-table']").removeClass('x-grid3-row-selected');
						$("input[id*='txtTotalPago']").val(suma);
					}
					$("input[id*='txtRegistrosSel']").val($("div [id*='gridPropuesta'] div [class*='x-grid3-row-table x-grid3-row-selected']").length);				

												
				});			
				
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Hay Propuestas Pendientes');
				}


				else if(records.length >= 500)
				{
					Ext.Msg.alert('SET','Los registros pueden presentarse incompletos, se requieren criterios de busqueda adicionales');
					return;
				}
//				else
//				{
//					var totalImporte=0;
//          			NS.sm.selectRange(0,records.length-1);
//          			var regSelec=NS.gridPropuesta.getSelectionModel().getSelections();
//          			
//          			for(var k=0;k<regSelec.length;k=k+1)
//          			{
//          				totalImporte=totalImporte+regSelec[k].get('importe');
//          			}
//					Ext.getCmp(PF+'txtRegistrosSel').setValue(records.length);
//					Ext.getCmp(PF+'txtTotalPago').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));
//				}
			}
		}
	}); 
	
	//barra de paginacion del grid
	NS.pager = new Ext.PagingToolbar({
			store: NS.storeGridPropuesta, // <--grid and PagingToolbar using same store
			displayInfo: true,
			displayMsg: 'Mostrando {0} - {1} de {2} Propuestas',
			emptyMsg: 'No Hay Propuestas',
			pageSize: 10
		});
		
	NS.cbMem = new Ext.ux.plugins.CheckBoxMemory({
        	idProperty: 'noFolioDet',
        	idValueField: 'importe'
        	
        	});
		
		// crear grid
	NS.gridPropuesta = new Ext.grid.GridPanel( {
		store : NS.storeGridPropuesta
		,id:PF+'gridPropuesta'
		,idProperty:'noFolioDet' 
		,mode: 'local'
		,typeAhead: true
		,plugins: [NS.cbMem]
		,cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            }
		,columns : [ 
				NS.sm,
				{
					header :'Factura',
					width :60,
					sortable :true,
					dataIndex :'noFactura'
				},
				{   header :'NoGrupoFlujo',width :70,sortable :true,dataIndex :'idGrupoFlujo',hidden: true},
				{   header :'NoRubro',width :70,sortable :true,dataIndex :'idRubro',hidden: true},
				{   header :'Grupo',width :120,sortable :true,dataIndex :'descGrupo'},
				{   header :'Rubro',width :120,sortable :true,dataIndex :'descRubro'},
				{
					header :'Empresa',	
					width :200,	
					sortable :true,	
					dataIndex :'nomEmpresa',
					hidden: true
				},
				{
					header :'Beneficiario',
					width :200,
					sortable :true,
					dataIndex :'beneficiario'
				},
				{
					header :'Importe',
					width :95,
					sortable :true,
					dataIndex :'importe',
					css: 'text-align:right;'
					,renderer: BFwrk.Util.rendererMoney
				},
				{
					header :'Concepto',
					width :300,
					sortable :true,
					dataIndex :'concepto'
				},
				{
					header :'NoEmpresa',	
					width :200,	
					sortable :true,
					hidden: true,
					dataIndex :'noEmpresa'		
				},
				{
					header :'Dias Vencidos',
					width :90,
					sortable :true,
					dataIndex :'difDias',
					css: 'text-align:right;'
					
				},
				{
					header :'Fec vencimiento',
					width :100,
					sortable :true,
					dataIndex :'fecValor'
				},
				{
					header :'Importe Original',
					width :90,
					sortable :true,
					dataIndex :'importeOriginal',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney
				},
				{
					header :'Estatus',
					width :80,
					sortable :true,
					dataIndex :'descEstatus'
				},
				{
					header :'Divisa',
					width :50,
					sortable :true,
					dataIndex :'idDivisa'
				},
				{
					header :'Forma Pago',
					width :120,
					sortable :true,
					dataIndex :'descFormaPago'
				},
				{
					header :'Banco Benef',
					width :120,
					sortable :true,
					dataIndex :'descBanco'
				},
				{
					header :'No Proveedor',
					width :120,
					sortable :true,
					dataIndex :'noCliente',
					hidden: true
					
				},
				{
					header :'Chequera Benef',
					width :100,
					sortable :true,
					dataIndex :'idChequeraBenef'
				},
				{
					header :'Clave Operacion',
					width :100,
					sortable :true,
					dataIndex :'descCveOperacion'
				},
				{
					header :'Origen',
					width :50,
					sortable :true,
					dataIndex :'origenMov'
				},
				{
					header :'Caja',
					width :80,
					sortable :true,
					dataIndex :'idCaja',
					hidden: true
				},
				{
					header :'Num Pedido',
					width :80,
					sortable :true,
					dataIndex :'noPedido',
					hidden: true
				},
				{
					header :'No. Docto SAP',
					width :80,
					sortable :true,
					dataIndex :'noDocto',
					hidden: true
				},
				{
					header :'Division',
					width :80,
					sortable :true,
					dataIndex :'division',
					hidden: true
				},
				{
					header :'Operacion',
					width :80,  
					sortable :true,
					dataIndex :'operMayEsp',
					hidden: true
				},
				{
					header :'Clabe',
					width :80,
					sortable :true,
					dataIndex :'clabe',
					hidden: true
				},
				{
					header :'Clase Doc.',
					width :80,
					sortable :true,
					//dataIndex :'clase'
					hidden: true
				},
				{
					header :'No Folio Det.',
					width :80,
					sortable :true,
					dataIndex :'noFolioDet',
					hidden: true
				},
					
				
				//AGREGADOS PARA PAGO PARCIAL JRBM
				{   header :'autoriza',width :100,sortable :true,dataIndex :'autoriza', hidden: true},
				{   header :'idLeyenda',width :100,sortable :true,dataIndex :'idLeyenda', hidden: true},
				{   header :'idBancoBenef',width :100,sortable :true,dataIndex :'idBancoBenef', hidden: true},
				{   header :'idChequera',width :100,sortable :true,dataIndex :'idChequera', hidden: true},
				{   header :'idFormaPago',width :100,sortable :true,dataIndex :'idFormaPago', hidden: true},
				{   header :'solicita',width :100,sortable :true,dataIndex :'solicita', hidden: true},
				{   header :'tipoCambio',width :100,sortable :true,dataIndex :'tipoCambio', hidden: true},
				{   header :'Observacion',width :200,sortable :true,dataIndex :'observacion', hidden: false},
				{   header :'Banco Pagador',width :200,sortable :true,dataIndex :'idBanco', hidden: true},
				{	header :'noFolioMov',width :80,sortable :true,dataIndex :'noFolioMov',hidden: true},
				{	header :'Confir',width :80,sortable :true,dataIndex :'confir',hidden: true}
				
				
	  		]
	  	})
      	,sm: NS.sm
      	,columnLines: true
      	,frame:false
		,height: 200 //135
		,x :10
		,y :0
		,bbar: NS.pager
		,title :'Pagos Pendientes'
	  ,listeners:{	
			click:{
       		   	fn:function(e){	
          		var items = NS.cbMem.getItems();	//arreglo de folios seleccionados
          		
	      			var c=$("div [id*='gridPropuesta'] div [class*='x-grid3-row-table x-grid3-row-selected']").length;	//contador de reg seleccionados
	      			NS.totalImporte = 0.0;
	      			for ( var i in items ){
	      				c++;
        				NS.totalImporte=NS.totalImporte+items[i].get('importe');
				   	}
          			Ext.getCmp(PF+'txtRegistrosSel').setValue(c);
          			var importeAll = $("input[id*='txtTotalPago']").val();
          			
          			//if(importeAll > 0)
          			//	Ext.getCmp(PF+'txtTotalPago').setValue(NS.formatNumber(importeAll));
          			//else
          				Ext.getCmp(PF+'txtTotalPago').setValue(NS.formatNumber(Math.round((NS.totalImporte) * 100) / 100));
					
      				NS.limpiaConta();
					NS.cmbGrupoRubro.reset();
					NS.cmbRubro.reset();
					Ext.getCmp(PF + 'txtGrupoRubro').setValue('');
					Ext.getCmp(PF + 'txtRubro').setValue('');
   				}
			}
		}
	});
/**********************************************grupo rubro*****************************************/
	NS.storeGrupoEgreso = new Ext.data.DirectStore( {
		paramOrder:['idTipoMovto', 'noEmpresa'],
		paramsAsHash: false,
		baseParams:{idTipoMovto:'E', noEmpresa: 0},		
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboGrupo,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		]	
	}); 
 	
 	NS.storeRubroEgreso = new Ext.data.DirectStore( {	
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

 	NS.storeLlenaComboGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConsultaPersonasAction.llenaComboGrupo,
		fields:
		[
		 	{name: 'idGrupo'},
		 	{name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboGrupo, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Grupos dados de alta');
			}
		}
	}); 
	
	
	NS.lblGrupoRubro = new Ext.form.Label({
		text: 'Grupo Rubro',
		x: 10
	});
	
	NS.lblRubro = new Ext.form.Label({
		text: 'Rubro',
		x: 330
	});
	
	NS.txtGrupoRubro = new Ext.form.TextField({
		id: PF + 'txtGrupoRubro',
		name: PF + 'txtGrupoRubro',
		x: 10,
		y: 15,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined) {
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtGrupoRubro', NS.cmbGrupoRubro.getId());
						if(comboValue == null || comboValue == undefined || comboValue == '')
							Ext.getCmp(PF + 'txtGrupoRubro').setValue('');
						else {
							Ext.getCmp(NS.txtRubro.getId()).setValue('');
							Ext.getCmp(NS.cmbRubro.getId()).setValue('');
							NS.storeRubroEgreso.removeAll();
							NS.storeRubroEgreso.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupoRubro').getValue());									
							NS.storeRubroEgreso.load();
						}
					}else {
						NS.cmbGrupoRubro.reset();
						NS.cmbRubro.reset();
						Ext.getCmp(PF + 'txtRubro').setValue('');
						Ext.getCmp(PF + 'txtGrupoRubro').setValue('');
					}
				}
			}
		}
	});
	
	NS.txtRubro = new Ext.form.TextField({
		id: PF + 'txtRubro',
		name: PF + 'txtRubro',
		x: 330,
		y: 15,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if (caja.getValue() !== null && caja.getValue() !== '' && caja.getValue() !== undefined) {
						var comboValue = BFwrk.Util.updateTextFieldToCombo(PF + 'txtRubro', NS.cmbRubro.getId());

						if(comboValue == null || comboValue == undefined || comboValue == '')
							Ext.getCmp(PF + 'txtRubro').setValue('');
						else
							NS.llenaComboGrupoConta(comboValue);
					}else
						NS.cmbRubro.reset();
				}
			}
		}
	});

	NS.cmbGrupoRubro = new Ext.form.ComboBox({
		store: NS.storeGrupoEgreso,
		id: PF + 'cmbGrupoRubro',
		name: PF + 'cmbGrupoRubro',
		x: 65,
		y: 15,		
		width: 250,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtGrupoRubro', NS.cmbGrupoRubro.getId());
					Ext.getCmp(NS.txtRubro.getId()).setValue('');
					Ext.getCmp(NS.cmbRubro.getId()).setValue('');
					NS.storeRubroEgreso.removeAll();
					NS.storeRubroEgreso.baseParams.idGrupo = parseInt(NS.txtGrupoRubro.getValue());									
					NS.storeRubroEgreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtEmpresa').getValue() == '' ? apps.SET.NO_EMPRESA : Ext.getCmp(PF+'txtEmpresa').getValue());
					NS.storeRubroEgreso.load();
				}
			},
			change:{
			    fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtGrupoRubro' ).setValue('');
         				Ext.getCmp(NS.txtRubro.getId()).setValue('');
						Ext.getCmp(NS.cmbRubro.getId()).setValue('');
						NS.storeRubroEgreso.removeAll();       				
         			}else{
         				Ext.getCmp( PF + 'txtRubro' ).forceSelection= true; 
         			}	
		    	}//end function
			}//end change
		}
	});
	
	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeRubroEgreso,
		id: PF + 'cmbRubro',
		name: PF + 'cmbRubro',
		x: 385,
		y: 15,
		width: 250,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtRubro', NS.cmbRubro.getId());
					NS.llenaComboGrupoConta(combo.getValue());
				}
			},
			change:{
				fn:function(combo, newValue, oldValue) {						    		
					if( newValue == '' ){		
						Ext.getCmp( PF + 'txtRubro' ).setValue('');
					}else{
						Ext.getCmp( PF + 'txtRubro' ).forceSelection= true;
					}
				}//end function
			}//end change
		}
	});

	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeLlenaComboGrupo,
		id: PF + 'cmbGrupo',
		name: PF + 'cmbGrupo',
		x: 175,
		y: 275,
		//hidden: true,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo de Flujo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGrupo', NS.cmbGrupo.getId());
				}
			}
		}
	});
	
	
	
	
/**********************************************componentes de la vantana*************************************/	
	
	NS.storeEmpresaWin = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noUsuario: NS.GI_USUARIO,
			pagoEmpresa:'', //'\''+NS.GI_NO_EMPRESA+'\'',
			noEmpresa: 0
		},
		root: '',
		paramOrder:['noUsuario','pagoEmpresa','noEmpresa'],
		root: '',
		directFn: PropuestaPagoManualAction.llenarComboGrupo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen empresas asignadas para el usuario');
				}
			}
		}
	}); 
	
	//combo empresas (ventana)
		NS.cmbEmpresaWin = new Ext.form.ComboBox({
		store: NS.storeEmpresaWin
		,name: PF+'cmbEmpresaWin'
		,id: PF+'cmbEmpresaWin'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 110
        ,y: 20
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione la empresa'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,listeners:{
			select:{
				fn:function(combo, valor) {
//						NS.storeBancoWin.baseParams.noEmpresa = combo.getValue();
//						NS.storeBancoWin.load();
				}
			}
		}
	});
	
	//store banco ventana
	NS.storeBancoWin = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idDivisa: 'MN',
			noEmpresa: 0
		},
		root: '',
		paramOrder:['idDivisa','noEmpresa'],
		root: '',
		directFn: PropuestaPagoManualAction.llenarComboBanco, 
		idProperty: 'idBanco', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				NS.regis = records.length;
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No hay bancos disponibles');
				}
			}
		}
	}); 
	
	//combo banco (ventana)   
	
		NS.cmbBancoWin = new Ext.form.ComboBox({
		store: NS.storeBancoWin
		,name: PF+'cmbBancoWin'
		,id: PF+'cmbBancoWin'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 110
        ,y: 70
        ,width: 170
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
						Ext.getCmp(PF+'cmbChequeraWin').reset();
						BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBancoWin.getId());
						/*
						 * paramOrder:['idDivisa','noEmpresa','idBanco','psDivision'],
						 */
						var idDivisa = "";
						
						if(Ext.getCmp(PF + 'cmbDivisaActualiza').getValue() != '')
							idDivisa = Ext.getCmp(PF + 'cmbDivisaActualiza').getValue();
						else
							idDivisa = Ext.getCmp(PF + 'txtDivisaPago').getValue();
						
						NS.storeChequeraWin.removeAll();
						NS.storeChequeraWin.baseParams.idDivisa = idDivisa;
						NS.storeChequeraWin.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
						NS.storeChequeraWin.baseParams.idBanco = combo.getValue();
						NS.storeChequeraWin.baseParams.psDivision = Ext.getCmp(PF + 'cmbDivision').getValue();
						
						if(NS.Persona != '') {
							NS.storeChequeraWin.baseParams.Personas = NS.Persona;
							NS.storeChequeraWin.baseParams.piNoEmpresa = Ext.getCmp(PF + 'cmbDivision').getValue();
						}
						NS.storeChequeraWin.load();
				}
			}
		}
	});
	
	
	NS.storeChequeraWin = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idDivisa: '',
			noEmpresa: 0,//NS.GI_NO_EMPRESA,
			idBanco: 0,
			psDivision: '',
			Persona: '',
			piNoEmpresa: ''
			
		},
		root: '',
		paramOrder:['idDivisa','noEmpresa','idBanco','psDivision','Persona','piNoEmpresa'],
		root: '',
		directFn: PropuestaPagoManualAction.llenarComboChequera, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No hay chequeras disponibles');
				}
			}
		}
	}); 
	
	//combo chequera (ventana)
		NS.cmbChequeraWin = new Ext.form.ComboBox({
		store: NS.storeChequeraWin
		,name: PF+'cmbChequeraWin'
		,id: PF+'cmbChequeraWin'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 110
        ,y: 120
        ,width: 170
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
		
		
		
		NS.storeDivisaActualiza = new Ext.data.DirectStore({     
			paramsAsHash: false,
			root: '',
			baseParams: {
			noEmpresa: NS.GI_NO_EMPRESA,
			division: Ext.getCmp(PF + 'cmbDivision').getValue()
	    	},
			paramOrder:['noEmpresa','division'],
			directFn: PropuestaPagoManualAction.obtenerDivisaAct, 
			idProperty: 'idDivisa', 
			fields: [
				 {name: 'idDivisa'},
				 {name: 'descDivisa'}
				 	],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisaVenta, msg:"Cargando..."});
					if(records.length == null || records.length <= 0)
					{
						Ext.Msg.alert('SET','No tiene divisas asignadas');
					}
						   		
				}
			}
		}); 
		NS.storeDivisaActualiza.load();
	 
	 NS.cmbDivisaActualiza = new Ext.form.ComboBox({
			store: NS.storeDivisaActualiza,
			id: PF + 'cmbDivisaActualiza',
			name: PF + 'cmbDivisaActualiza',
			x: 80,
	        y: 20,
	        width: 200,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			hidden: true,     
			forceSelection: true,
	        tabIndex: 1,
			valueField: 'idDivisa',
			displayField: 'descDivisa',
			autocomplete: true,
			emptyText: 'Seleccione una Divisa ',
			triggerAction: 'all',  
			value: '',
			visible: false,
			listeners:{
				select:{
					fn:function(combo, valor) {
		 				NS.storeDivisaActualiza.getById(combo.getValue());
		 				
		 				NS.storeBancoWin.baseParams.idDivisa = Ext.getCmp(PF + 'cmbDivisaActualiza').getValue();
		 				NS.storeBancoWin.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		 				NS.storeBancoWin.load();
		 				
					}
				}
			}   
		});
	 
	 NS.txtFecPagoParc = new Ext.form.DateField({	 
         id: PF+'txtFecPagoParc',
         name: PF+'txtFecPagoParc',
         format: 'd/m/Y',
         x: 120,
         y: 50
     })
	 
		NS.labDivisa = new Ext.form.Label({
			id: PF + 'div',
	 		name: PF + 'div',   
			text: 'Divisa: ',
	 		hidden: true,
	 		x: 30,
	    	y: 22,
	    	visible: false
	 	});
	 	
	 
		//PAEE
	 function openVentana(){
	 		NS.winActualiza = new Ext.Window({
	 			title: 'Actualizar Chequeras'
	 			,modal:true
	 			,shadow:true
	 			,width: 330
	 			,height:250
	 			,minWidth: 450
	 			,minHeight: 600
	 			,layout: 'absolute'
	 			,plain:true
	 			,bodyStyle: 'background: black;'
	 			,buttonAlign:'center'
	 			,closeAction: 'hide'
	 			,closable: false
	 			,items: [
	 			    
	 			    NS.labDivisa,     
	 			    NS.cmbDivisaActualiza,
	 				{
	 	               xtype: 'label',
	 	               text: 'Pago cuenta de:',
	 	               hidden: true,
	 	               x: 30,
	 	               y: 22
	 	           }
	 	           ,NS.cmbEmpresaWin
	 	           ,{
	 	               xtype: 'label',
	 	               text: 'Banco:',
	 	               x: 30,
	 	               y: 72
	 	           },
	 	           {
	 	               xtype: 'textfield',
	 	               id: PF+'txtIdBanco',
	 	               name: PF+'txtIdBanco',
	 	               x: 67,
	 	               y: 70,
	 	               width: 40,
	 	               listeners:{
	 	                	change:{
	 	               	         fn:function(caja, valor){
	 		        	   			if(caja.getValue != null && caja.getValue != undefined && caja.getValue != '') {
	 	               	         		Ext.getCmp(PF+'cmbChequeraWin').reset();
	 		               				BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBanco',NS.cmbBancoWin.getId());
	 									NS.storeChequeraWin.baseParams.idBanco = parseInt(caja.getValue());
	 									NS.storeChequeraWin.load();
	 		        	   			}
	 	               			}
	 	               		}
	 	               }
	 	           }
	 	           ,NS.cmbBancoWin
	 	           ,{
	 	               xtype: 'label',
	 	               text: 'Chequera:',
	 	               x: 30,
	 	               y: 122
	 	           }
	 	           ,NS.cmbChequeraWin
	 			]
	 			,buttons: [
	 				   	{
	 					 	text: 'Aceptar',
	 						handler: function(e) {
//	 							if(Ext.getCmp(PF+'cmbEmpresaWin').getValue() === '')
//	 							{
//	 								Ext.Msg.alert('SET','Seleccione una empresa');
//	 								return;
//	 							}
	 							if(Ext.getCmp(PF+'cmbBancoWin').getValue() === '')
	 							{
	 								Ext.Msg.alert('SET','Seleccione un banco');
	 								return;
	 							}
	 							if(Ext.getCmp(PF+'cmbChequeraWin').getValue() === '')
	 							{
	 								Ext.Msg.alert('SET','Seleccione una chequera');
	 								return;
	 							}
	 							
	 							var records=NS.storeBancoWin.data.items;
	 							var matriz = new Array ();
	 							var registrosSel = NS.gridPropuesta.getSelectionModel().getSelections();
	 							NS.giBanco = parseInt(Ext.getCmp(PF+'txtIdBanco').getValue());
	 							NS.gsDescBanco = 0;
	 							//barrido al grid para obtener la descripcion
	 							for(j = 0; j< records.length; j++)
	 							{
	 								if(records[j].get('id') == NS.giBanco)
	 								{
	 									NS.gsDescBanco = records[j].get('descripcion');
	 								}
	 							}
	 			            	NS.gsChequera = Ext.getCmp(PF+'cmbChequeraWin').getValue();
	 			            	NS.winActualiza.hide();
	 			            	
	 			            	/*             barrido            */
	 			            	BFwrk.Util.msgWait('Ejecutando...', true);
	 			            	var items = NS.cbMem.getItems();	//arreglo de folios seleccionados
	 			            	//for(var i=0;i<registrosSel.length;i++)
	 			            	for ( var i in items )
	 			            	{
	 			            		var registro = {};
	 		       					registro.folio = NS.psFolios; //registrosSel[i].get('noFolioDet');
	 		       					registro.importe = items[i].get('importe');
	 		       					registro.idCaja = items[i].get('idCaja');
	 		       					registro.fecPropuesta = cambiarFecha(''+NS.psFecPropuesta); //+Ext.getCmp(PF+'txtFecPago').getValue());
	 		       					registro.fecPago = cambiarFecha(''+Ext.getCmp(PF+'txtFecPago').getValue());
	 		       					registro.noDocto = items[i].get('noDocto');
	 		       					registro.formaPago = Ext.getCmp(PF+'cmbFormaPago').getValue();
	 		       					registro.totalPago = NS.unformatNumber(Ext.getCmp(PF+'txtTotalPago').getValue());
	 		       					registro.giBanco = NS.giBanco;
	 		       					registro.gsDescBanco = NS.gsDescBanco;
	 		       					registro.gsChequera = NS.gsChequera;
	 		       					registro.usuario = NS.GI_USUARIO;
	 		       					registro.montoMax = NS.pdMontoMaximo;
	 		       					registro.importeCupo = NS.pdimporteCupo;
	 		       					registro.grupoFlujo = Ext.getCmp(PF+'cmbEmpresa').getValue();
	 		       					registro.divisa = Ext.getCmp(PF+'txtDivisa').getValue();
	 		       					registro.divisaPago = Ext.getCmp(PF+'cmbDivisaPago').getValue();
	 		       					registro.tipoCambio = Ext.getCmp(PF+'txtTipoCambio').getValue();
	 		       					registro.giIdCaja = NS.GI_ID_CAJA //150
	 		       					registro.cmbPropuesta = Ext.getCmp(PF+'cmbPropuestasAct').getValue();
	 		       					matriz[i] = registro; 
	 	       					}
	 	       						var jsonString = Ext.util.JSON.encode(matriz);	
	 								PropuestaPagoManualAction.ejecutarPropuestas(jsonString, function(result, e){
	 									BFwrk.Util.msgWait('Ejecutando...', false);
	 									if(result.msgUsuario!==null  &&  result.msgUsuario!=='' && result.msgUsuario!= undefined)
	 									{
	 										Ext.Msg.alert('SET',''+result.msgUsuario);
	 										NS.storeDatos.removeAll();
	 			              			   	NS.gridDatos.getView().refresh();
	 			              			   	NS.storeGridPropuesta.removeAll();
	 			              			   	NS.gridPropuesta.getView().refresh();
	 			              			   	NS.contenedorPropuesta.getForm().reset();
	 			              			   	NS.obtenerFechaManana();
	 			              			   	Ext.getCmp(PF+'txtIdBanco').setValue('');
	 									 	Ext.getCmp(PF+'cmbEmpresaWin').setValue('');
	 									 	Ext.getCmp(PF+'cmbBancoWin').setValue('');
	 									 	Ext.getCmp(PF+'cmbChequeraWin').setValue('');
	 									 	Ext.getCmp(PF +'cmbDivisaActualiza').setValue('');
	 									}
	 								});
	 								 limpiar();
	 						}
	 				     }
	 				     ,{
	 					 	text: 'Cancelar',
	 						handler: function(e) {
	 							Ext.getCmp(PF+'txtIdBanco').setValue('');
	 						 	Ext.getCmp(PF+'cmbEmpresaWin').setValue('');
	 						 	Ext.getCmp(PF+'cmbBancoWin').setValue('');
	 						 	Ext.getCmp(PF+'cmbChequeraWin').setValue('');
	 						
	 						NS.winActualiza.hide();
	 						 	//NS.winActualiza.close();
	 						 	//delete NS.winActualiza;

	 						 }
	 						 	
	 				     }
	 	     	]
	 		});	
	 		
	 	NS.winActualiza.show(this);
	 	NS.winActualiza.getEl().setStyle('z-index','80000');
	 	
	 	}
	 	//PAEE

	
	//PAEE
function openVentana2(){
	
		NS.winActualiza = new Ext.Window({
			
			title: 'Pago Parcial'
			,modal:true
			,shadow:true
			,width: 300
			,height:200
			,minWidth: 450
			,minHeight: 600
			,layout: 'absolute'
			,plain:true
			,bodyStyle: 'background: black;'
			,buttonAlign:'center'
			,closeAction: 'hide',
			closable: false,
			listeners: {
        		show: {
        			fn:function(t) {
			
			Ext.getCmp(PF+'txtImporteParcial').setValue('');
			Ext.getCmp(PF+'txtFecPagoParc').setValue(NS.fechaManana);
			
				
			
			var items = NS.cbMem.getItems();	//arreglo de folios seleccionados
        	
        	
        		for ( var i in items )
        		{        		
        		
        			Ext.getCmp(PF+'txtTo').setValue(NS.formatNumber(items[i].get('importe')));
        							
					
				}
        		
//        		Ext.getCmp(PF+'txtFecPagoParc').toFront();
        			}
        		}
        	}			
			,items: [
			    
			    NS.labDivisa,     
			    NS.cmbDivisaActualiza,
				{
	               xtype: 'label',
	               text: 'Pago cuenta de:',
	               hidden: true,
	               x: 30,
	               y: 52
	           }
	           ,{
	               xtype: 'label',
	               text: 'Fecha de Pago:',
	               x: 30,
	               y: 52
	           },
	          NS.txtFecPagoParc
	           ,{
	               xtype: 'label',
	               text: 'Importe Parcial:',
	               x: 30,
	               y: 92
	           },
	           {
	               xtype: 'textfield',
	               id: PF+'txtImporteParcial',
	               name: PF+'txtImporteParcial',
	               x: 120,
	               y: 90,
	               width: 100,
	               listeners:{
	                	change:{
	               	         fn:function(caja, valor){

		        	   			}
	               			}
	               		}
	               }
	               ,{
		               xtype: 'label',
		               text: 'Pago Total:',
		               x: 30,
		               y: 12
		               
		           },
		           {
		               xtype: 'textfield',
		               id: PF+'txtTo',
		               name: PF+'txtTo',
		               x: 120,
		               y: 10,
		               width: 100,
		               disabled:true,
		               listeners:{
		                	change:{
		               	         fn:function(caja, valor){

			        	   			}
		               			}
		               		}
		               }
			]
			,buttons: [
				   		{
				   			text: 'Aceptar',
				   			handler: function(e) {
				   			
				   			var fecPagoParcial = '';
				   			fecPagoParcial = Ext.getCmp(PF+'txtFecPagoParc').getValue(); 
				   			
				   		
					   		if(fecPagoParcial == '')
							{
									Ext.Msg.alert('SET','Seleccione una fecha.');
									return;
							}
					   		
					   		var importeParcial = '';
				   			importeParcial = Ext.getCmp(PF+'txtImporteParcial').getValue();
				   			
						
					   		if(importeParcial == '')
							{
									Ext.Msg.alert('SET','No a dado de alta un importe.');
									return;
							}
				   		
					   		if(parseInt(Ext.getCmp(PF+'txtImporteParcial').getValue()) <= 0)
							{
									Ext.Msg.alert('SET','Importe Invalido.');
									return;
							}
					   		
					   		if(parseInt(NS.unformatNumber(Ext.getCmp(PF+'txtTo').getValue())) <= parseInt(Ext.getCmp(PF+'txtImporteParcial').getValue()))
							{
									Ext.Msg.alert('SET','El importe parcial debe ser menor al importe original');
									return;
							}
				   		
					   		var matriz = new Array();
					   		var reg = {};
					   		
					   		var items = NS.gridPropuesta.getSelectionModel().getSelections();					   		
					   		var i = 0;
					   		
					   		/*if (items[i].get('noEmpresa') == "PAGO PARCIALIZADO" )
					   		{
					   			Ext.Msg.alert('SET','El pago no se puede parcializar.');
					   			return;					   								   			
					   		}*/
					   		
					   		if( items[i].get('idChequera') == "" )
					   		{
					   			
					   			reg.idChequera=''
					   		}
					   		else{
					   			
					   			reg.idChequera=items[i].get('idChequera');
					   		}
					   		
					   		if( items[i].get('idChequeraBenef') == "" )
					   		{
					   			reg.chequera='';
					   		
					   		}
					   		else{
					   			reg.chequera=items[i].get('idChequeraBenef');					   			
					   		}
					   		
					   		
					   		
					   		reg.idEmpresa=items[i].get('noEmpresa');					   		 		
					   		reg.autoriza = items[i].get('autoriza');
					   		reg.chkLeyenda=items[i].get('idLeyenda');
					   		reg.clabe=items[i].get('clabe');
					   		reg.concepto=items[i].get('concepto');
					   		reg.idBancoBenef=items[i].get('idBancoBenef');
					   		reg.idCaja=items[i].get('idCaja');
					   		reg.origenMov=items[i].get('origenMov');
					   		
					   		reg.idDivPago=items[i].get('idDivisa');
					   		reg.idDivOriginal=items[i].get('idDivisa');
					   		reg.idFormaPago=items[i].get('idFormaPago');
					   		reg.noDocto =items[i].get('noDocto');
					   		reg.solicita=items[i].get('solicita');
					   		reg.tipoCambio=items[i].get('tipoCambio');
					   		//reg.idRubro=items[i].get('idRubro'); //se cambia el grupo y rubro fijo para obligar al usuario a poner la cuenta contable
					   		//reg.idGrupo=items[i].get('idGrupoFlujo');
					   		reg.idRubro='79991';
					   		reg.idGrupo='79990';
					   		reg.idPersona=items[i].get('noCliente');
					   		reg.nomPersona=items[i].get('beneficiario');
					   		reg.noFactura=items[i].get('noFactura');
					   		reg.noFolioDet=items[i].get('noFolioDet');
					   		reg.noFolioMov=items[i].get('noFolioMov');
					   		reg.optNac='true';
							reg.optExt='false';
							reg.sucursal = 0;
							reg.plaza = 0;
							reg.observaciones = 'PAGO PARCIAL';						
							reg.chkContabilizar='false' ;
							reg.chkTodos='false' ;
							reg.chkProvUnico='true';
							reg.idDepto=0;
							reg.idArea=0;
					   		reg.numPartida=1;//PENDIENT
					   		reg.idUsuario = 2;
					   		reg.descRubro='SN';
					   		reg.idBanco=items[i].get('idBanco');
					   		reg.importeOriginal = items[i].get('importeOriginal');							 
							reg.importePago = NS.unformatNumber(Ext.getCmp(PF + 'txtImporteParcial').getValue());
							reg.fechaFactura =items[i].get('fecValor');
							reg.sumImporte = NS.unformatNumber(Ext.getCmp(PF+'txtTo').getValue());
							reg.fechaPago = cambiarFecha('' + Ext.getCmp(PF + 'txtFecPagoParc').getValue());
							
							
					   		
							matriz[0] = reg;
							var jsonString = Ext.util.JSON.encode(matriz);
							
							var matrizGrid= new Array();
							var regGrid={};
							
							regGrid.partida=2;
							regGrid.rubro=items[i].get('idRubro');
							regGrid.descRubro='';
							regGrid.importe=NS.unformatNumber(Ext.getCmp(PF + 'txtImporteParcial').getValue());
							regGrid.departamento=0;
							regGrid.grupo=items[i].get('idGrupoFlujo');
							regGrid.area=0;
							
							matrizGrid[i]=regGrid;							
							var jsonStringGrid = Ext.util.JSON.encode(matrizGrid);
							
												   		
					   		NS.winActualiza.hide();			            
					   		BFwrk.Util.msgWait('Ejecutando...', true);
			            
			            /*&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&CAPTURA SOLICITUD DE PAGO &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*/
						
			            
					   		PropuestaPagoManualAction.ejecutarSolicitud(jsonString,jsonStringGrid, function(result, e){
			            	
							if(result.msgUsuario !== null && result.msgUsuario !== '') {
								
								BFwrk.Util.msgWait('Ejecutando...', false);
								Ext.Msg.alert('Información SET', '' + result.msgUsuario);
								
								
								if(result.terminado == '1') {
									
									PropuestaPagoManualAction.letrasCantidad('MN', NS.unformatNumber(importeParcial), function(res, e) {
										
										BFwrk.Util.msgWait('Ejecutando...', false);										
										NS.reporte(('' + result.msgUsuario).substring((''+result.msgUsuario).indexOf(':') + 1, result.msgUsuario.length),
													"$ " + importeParcial + " *** " + res + " *** ");
										
									});
									
								}
										   		NS.storeGridPropuesta.removeAll();
						              			NS.gridPropuesta.getView().refresh();
						              			NS.criterios.grupo = Ext.getCmp(PF+'cmbEmpresa').getValue();
						            			NS.criterios.empresa = NS.GI_NO_EMPRESA;
						            			NS.criterios.usuario = NS.GI_USUARIO;
						            			NS.criterios.tipoChequeNE = false;
						            			NS.criterios.psDivision = '';
						            			NS.matriz[0] = NS.criterios;
						            			var jsonString = Ext.util.JSON.encode(NS.matriz);
						            			
						            			NS.storeGridPropuesta.baseParams.criterios = jsonString;
						            			NS.storeGridPropuesta.load();
							}
						});
					   		
					   		
						}
				     }
				     ,{
					 	text: 'Cancelar',
						handler: function(e) {
				    	 
				    	 	Ext.getCmp(PF+'txtTo').setValue('');
							Ext.getCmp(PF+'txtImporteParcial').setValue('');
							Ext.getCmp(PF+'txtFecPagoParc').setValue(NS.fechaManana);

						
						NS.winActualiza.hide();
						 	//NS.winActualiza.close();
						 	//delete NS.winActualiza;

						 }
						 	
				     }
	     	]
		});	
		
	NS.winActualiza.show(this);
	NS.winActualiza.getEl().setStyle('z-index','80000');
	
	}
	//PAEE
	
	//*************************************************   Esto es para la parte contable  ****************************************************//
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
		baseParams: {idTipoMovto: 'E'},
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
					datosConta.idGrupo = Ext.getCmp(PF + 'txtGrupoRubro').getValue();
					datosConta.idRubro = Ext.getCmp(PF + 'txtRubro').getValue();
					datosConta.noEmpresa = Ext.getCmp(PF+'txtEmpresa').getValue();
					mdatosConta[0] = datosConta;
					
					var jsonDatos = Ext.util.JSON.encode(mdatosConta);
					
					PropuestaPagoManualAction.buscaCtaContable(jsonDatos, function(resp, e) {
						if(resp.length > 15)
							Ext.Msg.alert('SET', resp);
						else if(resp != '') {
							Ext.getCmp(PF + 'txtCtaConta').setValue(resp);
							Ext.getCmp(PF + 'ejecutarConta').setDisabled(false);
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
		title: 'Cuenta contable',
		width: 720,
	    height: 80,
	    x: 10,
	    y: 60,
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
        Ext.getCmp(PF + 'ejecutarConta').setDisabled(true);
	};
	
	NS.llenaComboGrupoConta = function(comboValue) {
		var idSubGrupo = NS.storeRubroEgreso.getById(parseInt(comboValue)).get('idSubGrupo');
		
		NS.limpiaConta();
		
		NS.storeGrupoConta.baseParams.idSubGrupo = idSubGrupo;
		NS.storeGrupoConta.load();
	};
	
//******************************************************** Este codigo es para el detalle de las facturas de CXC *****************************************//
	
	NS.lblDepto = new Ext.form.Label({
		text: 'Departamento',
		x: 10,
		y: 150
	});
	
	NS.lblProyecto = new Ext.form.Label({
		text: 'Proyecto',
	    x: 200,
	    y: 150
	});
	
	NS.lblCCosto = new Ext.form.Label({
		text: 'Centro de Costo',
	    x: 390,
	    y: 150
	});
	
	NS.storeDepto = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['noEmpresa', 'tipoCombo'],
		directFn: PropuestaPagoManualAction.llenarComboDepCCProy, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDepto, msg:"Cargando..."});
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
		,x: 10
		,y: 165
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Departamentos'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					
				}
			}
		}
	});
	
	NS.storeProyect = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['noEmpresa', 'tipoCombo'],
		directFn: PropuestaPagoManualAction.llenarComboDepCCProy, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProyect, msg:"Cargando..."});
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
		,x: 200
        ,y: 165
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
		root: '',
		paramOrder:['noEmpresa', 'tipoCombo'],
		directFn: PropuestaPagoManualAction.llenarComboDepCCProy, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCCosto, msg:"Cargando..."});
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
        ,x: 390
        ,y: 165
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
	
	
	//Contenedor de configuración contable
	NS.contContable = new Ext.form.FieldSet({
		layout: 'absolute',
	    items: [
			NS.lblGrupoRubro,
			NS.lblRubro,
			NS.txtGrupoRubro,
			NS.txtRubro,
			NS.cmbGrupoRubro,
			NS.cmbRubro,
			NS.contConta,
			NS.lblDepto,
			NS.lblProyecto,
			NS.lblCCosto,
			NS.cmbDepto,
			NS.cmbProyect,
			NS.cmbCCosto,
			{
			    xtype: 'button',
			   	text: 'Ejecutar',
			   	id: PF + 'ejecutarConta',
			   	x: 560,
				y: 200,
				width: 80,
				height: 22,
				listeners: {
					click:{
						fn:function(e){
							var mdatos = new Array();
							var records = NS.gridPropuesta.getSelectionModel().getSelections();
							
							var gru = Ext.getCmp(PF + 'txtGrupoRubro').getValue();
							var rub = Ext.getCmp(PF + 'txtRubro').getValue();
							var auxg;
							var auxr;
							
							for(i = 0; i < records.length;i++){
								auxg = records[i];
								auxg.set('idGrupoFlujo',gru);
								auxr = records[i];
								auxr.set('idRubro',rub);
								auxg = records[i];
								auxg.set('confir',1);
							}
							for(j = 0; j < records.length;j++){
								var datos = {};
								datos.noFolioDet = records[j].get('noFolioDet');
								datos.noFolioMov = records[j].get('noFolioMov');
								datos.idGrupoFlujo = records[j].get('idGrupoFlujo');
								datos.idRubro = records[j].get('idRubro');
								datos.idSubGrupo = NS.cmbGrupoConta.getValue();
								datos.idSubSubGrupo = NS.cmbSubGrupo.getValue();
								datos.idRubroC = NS.cmbSubSubGrupo.getValue();
								datos.ctaCargo = Ext.getCmp(PF + 'txtCtaConta').getValue();
								datos.departamento = NS.cmbDepto.getValue();
								datos.proyecto = NS.cmbProyect.getValue();
								datos.cCosto = NS.cmbCCosto.getValue();
								mdatos[j] = datos;
							}
							var jsonModif = Ext.util.JSON.encode(mdatos);
							ConsultaPropuestasAction.insertarFlujoMovtos(jsonModif, function(res, e){
								if (res != null && res != undefined && res != ''){
									Ext.Msg.alert('Información SET','' + res.msgUsuario);
									
									NS.txtGrupoRubro.reset();
									NS.txtRubro.reset();
									NS.cmbGrupoRubro.reset();
									NS.cmbRubro.reset();
									NS.cmbDepto.reset();
									NS.cmbProyect.reset();
									NS.cmbCCosto.reset();
									
									NS.limpiaConta();
									NS.buscarFacturas();
									
									winContable.hide();
									
								}
							});
						}
					}
				}
			},{
        	    xtype: 'button',
        	   	text: 'Cancelar',
        	   	x: 650,
				y: 200,
				width: 80,
				height: 22,
				listeners: {
					click:{
						fn:function(e){
							NS.limpiaConta();
							winContable.hide();
						}
					}
				}
            }
	           ]
	});
	//ventana para el detalle de las propuestas a pagar
	var winContable = new Ext.Window({
		title: 'Configuración contable',
		modal: true,
		shadow: true,
		width: 800,
	   	height: 300,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    closable: false,
	   	items: [
	   	        NS.contContable
	   	        ]
	});//Termina ventana
	
	NS.lblImporteModif = new Ext.form.Label({
		text: 'Nuevo Importe',
		x: 10,
		y: 20
	});
	
	NS.txtImporteModif = new Ext.form.TextField({
		id: PF + 'txtImporteModif',
		x: 10,
        y: 35,
        width: 125
    });
	
	//Contenedor de configuración contable
	NS.contImporte = new Ext.form.FieldSet({
		layout: 'absolute',
	    items: [
	            NS.lblImporteModif,
	            NS.txtImporteModif,
			{
			    xtype: 'button',
			   	text: 'Aceptar',
			   	id: PF + 'aceptarImporte',
			   	x: 10,
				y: 100,
				width: 80,
				height: 22,
				listeners: {
					click:{
						fn:function(e){
	            			var records = NS.gridPropuesta.getSelectionModel().getSelections();
	            			
			            	PropuestaPagoManualAction.modificaImporteApagar(parseInt(records[0].get('noFolioDet')), 
			            								parseFloat(Ext.getCmp(PF + 'txtImporteModif').getValue('')), function(res, e){
								Ext.Msg.alert('SET', res);
								NS.buscarFacturas();
							});
	            			Ext.getCmp(PF + 'txtImporteModif').setValue('');
	            			winImporte.hide();
						}
					}
				}
			},{
        	    xtype: 'button',
        	   	text: 'Cancelar',
        	   	x: 100,
				y: 100,
				width: 80,
				height: 22,
				listeners: {
					click:{
						fn:function(e){
							Ext.getCmp(PF + 'txtImporteModif').setValue('');
							winImporte.hide();
						}
					}
				}
            }
	           ]
	});
	
	//ventana para el detalle de las propuestas a pagar
	var winImporte = new Ext.Window({
		title: 'Modifica Importe',
		modal: true,
		shadow: true,
		width: 250,
	   	height: 200,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    closable: false,
	   	items: [
	   	        NS.contImporte
	   	        ]
	});//Termina ventana

/********************************************formulario******************************************************/	
	NS.contenedorPropuesta =new Ext.FormPanel({
	    title: 'Propuesta Pago Manual',
	    width: 1020,
	    height: 900,//638,
	    padding: 10,
    	frame: true,
    	autoScroll: true,
	    layout: 'absolute',
	    renderTo: NS.tabContId,
	    items: [
	            {
	            	xtype: 'fieldset',
	                title: '',
	                x: 20,
	                y: 5,
	                layout: 'absolute',
	                width: 1010,
	                height: 620,
	                items: [
	                    {
	                    	
	                    	
	                xtype: 'fieldset',
	                title: 'B&#250;squeda',
	                layout: 'absolute',
	                height: 260,
	                width: 985,
	                items: [
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
	                        value: apps.SET.NO_EMPRESA,
	                        x: 10,
	                        y: 16,
	                        width: 45,
	                        listeners: {
	                        	change: {
	                        		fn:function(caja, valor) {
	                    				if(caja.getValue != null && caja.getValue != undefined && caja.getValue != '') {
		                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresasUsuario.getId());
		                        			NS.accionarCmbEmpresasUsuario(comboValor);
	                    				}//else {
	                    					//Ext.getCmp(PF + 'txtEmpresa').setValue('0');
	                    			   		//Ext.getCmp(PF + 'cmbEmpresasUsuario').setValue('------------TODAS------------');
	                    				//}
	                        		}
	                        	}
	                        }
	                    },
	                    NS.cmbEmpresasUsuario,
	                    NS.labDivision,
	                    NS.cmbDivision,
	                    {
	                        xtype: 'label',
	                        text: 'Forma de Pago:',
	                        x: 10,
	                        y: 45
	                    },
	                    NS.cmbFormaPago,
	                    {
	                        xtype: 'label',
	                        text: 'Divisa:',
	                        x: 10,
	                        y: 90
	                    },
	                    {
	                        xtype: 'uppertextfield',
	                        id: PF+'txtDivisa',
	                        name: PF+'txtDivisa',
	                        //value: 'MN',
	                        x: 10,
	                        y: 105,
	                        width: 45,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
		                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa',NS.cmbDivisa.getId());
		                        			
		                        			if(comboValue != null && comboValue != undefined && comboValue != '')
		                        				NS.accionarCmbDivisa(comboValue);
		                        			else
		                        				Ext.getCmp(PF + 'txtDivisa').reset();
	                        			}else {
	                        				Ext.getCmp(PF + 'txtDivisa').reset();
	                        				NS.cmbDivisa.reset();
	                        			}
	                        		}
	                        	}
	                        }
	                    },
	                    NS.cmbDivisa,
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 250,
	                        y: 20,
	                        width: 600,
	                        height: 190,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Criterio:',
	                                x: 10,
	                                y: 10
	                            },
	                            NS.cmbCriterio,
	                            {
	                                xtype: 'label',
	                                text: 'Valor:',
	                                x: 10,
	                                y: 70,
	                                width: 150
	                            }
	                            ,NS.cmbBloqueo
	                            ,NS.cmbEstatus
	                            ,NS.gridDatos
	                            ,{
			                   	   xtype: 'numberfield',
			                   	   id:PF+'txtMonto1',
			                   	   name:PF+'txtMonto1',
			                       x: 10,
			                       y: 85,
			                       width: 70,
			                       hidden: true,
			                       allowBlank: false,
			                       blankText:'El monto uno es requerido',
			                       listeners:{
			                       		change:{
											fn:function(caja,valor) {
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
			                   		xtype:'numberfield',
			                   		id:PF+'txtMonto2',
			                   		name:PF+'txtMonto2',
			                   		x:90,
			                   		y:85,
			                   		width: 70,
			                   		hidden: true,
			                   		allowBlank: false,
			                   		blankText:'El monto dos es requerido',
			                   		listeners:{			                   		
			                   			change:{
			                   				fn:function(caja, valor)
			                   				{
			                   					var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;		
												var valorUno=Ext.getCmp(PF+'txtMonto1').getValue();
												if(valorUno!='')
												{
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
														Ext.Msg.alert('SET','El monto dos debe ser mayor o igual al monto uno');
														Ext.getCmp(PF+'txtMonto1').setValue('');
														Ext.getCmp(PF+'txtMonto2').setValue('');
														return;
													}
											    	var datos = new datosClase({
								                			criterio: 'MONTOS',
															valor: valorUno + valor
								           			});
								                    NS.gridDatos.stopEditing();
								           			NS.storeDatos.insert(indice, datos);
								           			NS.gridDatos.getView().refresh();
								           			
				                   					if(valor!='')
				                   					{
				                   						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				                   						Ext.getCmp(PF+'txtMonto1').setDisabled(true);
				                   					}
			                   					}
			                   				}
			                   			}
			                   		}
			                   	},
			                   	{
			                   	   xtype: 'textfield',
			                   	   id:PF+'txtCveOperacion',
			                   	   name:PF+'txtCveOperacion',
			                       x: 10,
			                       y: 85,
			                       width: 140,
			                       hidden: true,
			                       allowBlank: false,
			                       blankText:'La clave es requerida',
			                       listeners:{
			                       		change:{
											fn:function(caja,valor) {
												var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='CLAVE OPERACION')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
										    	var datos = new datosClase({
							                			criterio: 'CLAVE OPERACION',
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
			                   	   id:PF+'txtConcepto',
			                   	   name:PF+'txtConcepto',
			                       x: 10,
			                       y: 85,
			                       width: 140,
			                       hidden: true,
			                       allowBlank: false,
			                       blankText:'El concepto es requerido',
			                        listeners:{
			                       		change:{
											fn:function(caja,valor) {
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
			                   	   xtype: 'textfield',
			                   	   id:PF+'txtFactura',
			                   	   name:PF+'txtFactura',
			                       x: 10,
			                       y: 85,
			                       width: 140,
			                       hidden: true,
			                       allowBlank: false,
			                       blankText:'La factura es requerida',
			                        listeners:{
			                       		change:{
											fn:function(caja,valor) {
												var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='FACTURA')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
										    	var datos = new datosClase({
							                			criterio: 'FACTURA',
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
			                   	   id:PF+'txtNoPedido',
			                   	   name:PF+'txtNoPedido',
			                       x: 10,
			                       y: 85,
			                       width: 140,
			                       hidden: true,
			                       allowBlank: false,
			                       blankText:'El numero de pedido es requerido',
			                        listeners:{
			                       		change:{
											fn:function(caja,valor) {
												var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='NUMERO DE PEDIDO')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
										    	var datos = new datosClase({
							                			criterio: 'NUMERO DE PEDIDO',
														valor: valor
							           			});
							           			Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
											}
			                       		}
			                       	}
			                   	}
	                        ]
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Grupo Empresa:',
	                        x: 10,
	                        y: 135
	                    },
	                    NS.cmbEmpresa,
	                    {
	                        xtype: 'button',
	                        text: 'Buscar',
	                        id: PF + 'cmdBuscar',
	                        name: PF + 'cmdBuscar',
	                        x: 880,
	                        y: 15,
	                        width: 80,
	                        listeners:{
                        		click:{
                        			fn:function(e){
                        				alert("JCArlitos 0");
										NS.buscarFacturas();
                        				alert("JCArlitos 1");
										NS.limpiaConta();
                        				alert("JCArlitos 2");
										NS.cmbGrupoRubro.reset();
                        				alert("JCArlitos 3");
										NS.cmbRubro.reset();
                        				alert("JCArlitos 4");
										Ext.getCmp(PF + 'txtGrupoRubro').setValue('');
                        				alert("JCArlitos 5");
										Ext.getCmp(PF + 'txtRubro').setValue('');
                        				alert("JCArlitos 6");
										
			           					 // openVentana();
      								//NS.winActualiza.hide();
//comento victor para saber que hace									staticCheck("div [id*='gridPropuesta'] div","div [id*='gridPropuesta']",2,".x-grid3-scroller",false);
                        			}
                        			
                       			}
                       			
                       			
                  			}
	                    }
	                ]
	            },
	            {
	                xtype: 'fieldset',
	                title: 'Egresos',
	                y: 263,
	                layout: 'absolute',
	                height: 335,
	                width: 985,
	                items: [
	                	NS.gridPropuesta,
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        hidden : true,
	                        x: 0,
	                        y: 110,
	                        layout: 'absolute',
	                        height: 140,
	                        items:[]},
			                    {
			                        xtype: 'label',
			                        text: 'Registros Sel:',
			                        x: 10,
			                        y: 210//150
			                    },
			                    {
			                        xtype: 'label',
			                        text: 'Beneficiario para grupo:',
			                        x: 100,
			                        y: 210//150
			                    },
			                    {
			                        xtype: 'combo',
			                        x: 100,
			                        y: 225,//170,
			                        width: 230,
			                        disabled: true
			                    },
			                    {
			                        xtype: 'label',
			                        text: 'Divisa de Pago:',
			                        x: 350,
			                        y: 210//150
			                    },
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtDivisaPago',
			                        name: PF+'txtDivisaPago',
			                        disabled: true,
			                        x: 350,
			                        y: 225,//170,
			                        width: 45
			                    },
			                    NS.cmbDivisaPago,
			                    {
			                        xtype: 'label',
			                        text: 'T. Cambio:',
			                        x: 555,
			                        y: 210//150
			                    },
			                    {
			                        xtype: 'numberfield',
			                        name: PF+'txtTipoCambio',
			                        id: PF+'txtTipoCambio',
			                        x: 555,
			                        y: 225,//170,
			                        width: 60,
			                        readOnly:true,
			                        value: 1.00,
			                        disabled: true
			                    },
			                    {
			                        xtype: 'label',
			                        text: 'Seleccione una propuesta para agregar pagos:',
			                        x: 630,
			                        y: 210//200
			                    },
			                    NS.cmbPropuestasAct
			                    ,{
			                        xtype: 'checkbox',
			                        boxLabel: 'Factoraje',
			                        disabled: true,
			                        hidden: true,
			                        x: 130,
			                        y: 270//200
			                    },
			                    {
			                        xtype: 'label',
			                        text: 'Fecha de Pago:',
			                        x: 10,
			                        y: 260//240
			                    },
			                    {
			                        xtype: 'datefield',
			                        id: PF+'txtFecPago',
			                        name: PF+'txtFecPago',
			                        format: 'd/m/Y',
			                        x: 10,
			                        y: 275//240
			                    },
/*			                    NS.lblGrupoRubro,
			        		 	NS.lblRubro,
			        		 	NS.txtGrupoRubro,
			        		 	NS.txtRubro,
			        		 	NS.cmbGrupoRubro,
			        		 	NS.cmbRubro,
*/			                    {
			                        xtype: 'button',
			                        text: 'Modifica Imp.',
			                        id: PF + 'ModificaImp',
			                        x: 520,
			                        y: 275,
			                        width: 80,
			                        hidden: false,
			                        listeners:{
                        				click:{
                        					fn:function(e){
						                    	var records = NS.gridPropuesta.getSelectionModel().getSelections();
												
						                    	if(records.length == 0) {
						                    		Ext.Msg.alert('SET','Debe seleccionar un registro!!');
						                    		return;
						                    	}
						                    	if(records.length > 1) {
					                    			Ext.Msg.alert('SET', 'Solo puede modificar un solo registro a la vez');
						            				return;
						            			}
						                    	if(records[0].get('origenMov') != 'SET') {
						            				Ext.Msg.alert('SET', 'No puede modificar el importe de una solicitud enviada por SAE');
						            				return;
						            			}
					                    		winImporte.show();
			                    		 	}
			                          	}
			                       }
			                    },{
			                        xtype: 'button',
			                        text: 'Clasificación',
			                        id: PF + 'Modificar',
			                        x: 610,
			                        y: 275,
			                        width: 80,
			                        hidden: false,
			                        listeners:{
                        				click:{
                        					fn:function(e){
						                    	var records = NS.gridPropuesta.getSelectionModel().getSelections();
												
						                    	if(records.length > 0) {
													NS.storeDepto.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
													NS.storeDepto.baseParams.tipoCombo = 1;
													NS.storeDepto.load();
													
													NS.storeCCosto.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
													NS.storeCCosto.baseParams.tipoCombo = 2;
													NS.storeCCosto.load();
													
													NS.storeProyect.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
													NS.storeProyect.baseParams.tipoCombo = 3;
													NS.storeProyect.load();
													
													NS.txtGrupoRubro.reset();
													NS.txtRubro.reset();
													NS.cmbGrupoRubro.reset();
													NS.cmbRubro.reset();
													NS.cmbDepto.reset();
													NS.cmbProyect.reset();
													NS.cmbCCosto.reset();
													
													NS.limpiaConta();
													
													winContable.show();
												}else 
													Ext.Msg.alert('SET','Debe seleccionar un registro!!');
			                    		 	}
			                          	}
			                       }
			                    },
//			                    NS.contConta,
			                    {
			                        xtype: 'button',
			                        text: 'Compra de Transfer',
			                        disabled: false,
			                        x: 450,
			                        y: 275,//240
			                        hidden: true,
			                        listeners:{
                        				click:{
                        					fn:function(e){
			                    				NS.compraTranfer = true;
			                    				btn.click();
			                    				NS.compraTranfer = false;
			                    		 }
			                          }
			                       }
			                    },
			                    {
			                        xtype: 'button',
			                        id: 'actua',  
			                        name: 'actua',
			                        text: 'Actualiza Chequera',
			                        disabled: true,
			                        x: 560,
			                        y: 275,
			                        hidden: true,
			                        listeners:{
                        				click:{    
                        					fn:function(e){
			                    	
			                    				var i = 0;
			                    				var psFolios = '';
												var piNumEmpresa = 0;
												var psBeneficiario = '';
												var records = NS.gridPropuesta.getSelectionModel().getSelections();
												
												if(records.length > 0){
													if(records[i].get('descFormaPago') != 'TRANSFERENCIA'){
														Ext.Msg.alert('SET','Solo se le puede cambiar la chequera a la forma de pago transferencia');
													}
													for(i = 0; i < records.length;i++){
														psFolios = psFolios + records[i].get('noFolioDet') + ',';
													    piNumEmpresa = piNumEmpresa + records[i].get('noEmpresa') + ',';
													    if(psBeneficiario == '')
													    	psBeneficiario = records[i].get('noCliente'); 
													    else {
													    	if(records[i].get('noCliente') != psBeneficiario){
													    		Ext.Msg.alert('SET','El Beneficiario debe ser el mismo en todos los movimientos seleccionados para poder cambiar su chequera');   
													    	}
													    }
													}
												}
												
												if(psFolios != '')
												{
													NS.giBanco = 0;
											        NS.gsChequera = '';
											        NS.Persona = psBeneficiario;
											        NS.piNoEmpresa = 0;
											        
											        NS.storeBancoWin.removeAll();
											        NS.storeBancoWin.baseParams.idDivisa = Ext.getCmp(PF + 'cmbDivisa').getValue();
									 				NS.storeBancoWin.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
											        NS.storeBancoWin.load();
											        
											        openVentana();
											        
											        if(NS.giBanco != 0 && NS.gsChequera != ''){
											        	psFolios = psFolios.substring(0,psFolios.length);
											        	
											        	/*PropuestaPagoManual.UpdateBancosCheqsBenef(NS.giBanco, NS.gsChequera, psFolios,
											        			function(result, e){
											        		if(result != 0)
											        			Ext.Msg.alert('SET','Chequeras actualizadas');
											        		else
											        			Ext.Msg.alert('SET','Las Chequeras no se actualizaron correctamente');
											        	});*/
											        	
											        	NS.buscarFacturas();
											        }
												}
												else
													Ext.Msg.alert('SET','No hay registros seleccionados');
											}
			                           }
			                       }
			                    },
			                    {
			                        xtype: 'button',
			                        id: 'cambia',
			                        name: 'cambia',
			                        text: 'Cambio de Chequera',
			                        disabled: true,
			                        hidden: true,
			                        height: 22,
			                        x: 670,
			                        y: 275,//240
			                        listeners:{
                        				click:{
                        					fn:function(e){
			                    	
			                    				var i = 0;
			                    				var lbencontrado = false;
			                    				var piNumEmpresa = 0;
			                    				var records = NS.gridPropuesta.getSelectionModel().getSelections();
		                    	    
		                    	    
			                    				if(records.length > 0){
			                    					if(records[i].get('descFormaPago') == 'TRANSFERENCIA'){
			                    						for(i = 0; i < records.length;i++){
			                    							piNumEmpresa = records[i].get('noEmpresa');
			                    							lbencontrado = true;
			                    						}
											
			                    						NS.psForma = "CambiaChequerasBenef";
			                    						NS.piNoEmpresa = piNumEmpresa;
			                    						openVentana();
			                    						
			                    						if(NS.giBanco == 0){
			                    							Ext.Msg.confirm('SET', 'está a punto de cambiar las chequeras del beneficiario a unas que corresponden al banco: ' + records[0].get('descBanco') + ' Desea Continuar?' , 
			                    									function(e){
			                    	            						if(e == 'yes'){
			                    	            							var matData = new Array();
	                    	            								    
	                    	            								    for(var i=0;i<records.length;i=i+1){
	                    	            								    	var regGridFondeo = {};
	                    	            								    	
	                    	            								    	regGridFondeo.noEmpresa	= regGridFondeo[i],get('noEmpresa');
	                    	            								    	regGridFondeo.Divisa = regGridFondeo[i],get('idDivisa');
	                    	            								    	regGridFondeo.formaPago = regGridFondeo[i],get('descFormaPago');
	                    	            								    	regGridFondeo.NoFolioDet = regGridFondeo[i],get('NoFolioDet');
	                    	            								    	
	                    	            								    	matData[i] = regGridFondeo; 
	                    	            							        }
	                    	            								    var jsonGridFondeo = Ext.util.JSON.encode(matData);
			                    	            							PropuestaPagoManualAction.CambiaChequerasMonedaPago(NS.giBanco, jsonGridFondeo, function(res, e){
			                    	            								if (res == ''){
			                    	            									Ext.Msg.alert('SET','Chequeras Actualizadas!');
			                    	            									NS.buscarFacturas();
			                    	            								}
			                    	            							});
			                    	            						}
			                    	            						else
			                    	            							Ext.Msg.alert('SET','No hay registros seleccionados!');    
			                    	            				
			                    							});
			                    						}
			                    	            
			                    					}
			                    					
			                    				}
			                    				else
		                    						Ext.Msg.alert('SET','No hay registros seleccionados!');
			                    		 }
			                          }
			                       }
			                    },
			                    {
			                        xtype: 'button',
			                        text: 'Pago Parcial',
			                        disabled: false,
			                        hidden: false,
			                        x: 790,
			                        y: 275,   
			                        width: 80,
			                        listeners:{
			                    		click:{
			                    			fn:function(e){
			                    				
			                    				var pbEncontrado = false;
			                    				var pd_pago = 0;  
			                    				var i = 0;
			                    				var records = NS.gridPropuesta.getSelectionModel().getSelections();
			                    				
			                    				var registros = Ext.getCmp(PF + 'txtRegistrosSel').getValue();
			                    				
			                    				if(parseInt(registros) > 0){
			                    					pbEncontrado = true;
			                    					if(parseInt(registros) > 1){
			                    						Ext.Msg.alert('SET','Seleccione un solo registro!');	
			                    						return;
			                    					}
			                    				}
			                    				else {
			                    					Ext.Msg.alert('SET','No hay registros seleccionados!');
			                    					return;
			                    				}
			                    				
			                    				PropuestaPagoManualAction.ConfiguraSet(254,records[0].get('noCliente'),function(res, e){
			                    					if(res != '')
			                    						Ext.Msg.alert('SET', + res);
			                    				});
			                    				
			                    				var items = NS.gridPropuesta.getSelectionModel().getSelections();					   		
										   		var x = 0;
										   		
										   		
										   		var varObservacion = items[x].get('observacion') + '';
										   		/*if ( varObservacion == "PAGO PARCIAL" )
										   		{
										   			Ext.Msg.alert('SET','El pago no se puede parcializar.');
										   			return;					   								   			
//										   		}*/
										   		openVentana2();
										   		
			                    			}
			                    			
			                    		}
			                    
			                    	}
			                    },{
			                        xtype: 'button',
			                        id: PF + 'btn',
			                        name: PF + 'btn',
			                        text: 'Ejecutar',
			                        x: 700,
			                        y: 275,
			                        width: 80,
			                        listeners:{
                        				click:{
                        					fn:function(e){
						                        var matriz = new Array (); 
						                        NS.pdMontoMaximo = 0;
												NS.pdimporteCupo = 0;
												NS.psFolios = '';
												var psTmpEmpresa = 0;
												NS.psFecPropuesta = '';
												var pdPagoEfectivo = 0.0;
												var ldTotal = 0.0;
												var giBanco;
										        var gsChequera;
										        var totalPago = NS.unformatNumber(Ext.getCmp(PF+'txtTotalPago').getValue());
										        var formaPago = Ext.getCmp(PF+'cmbFormaPago').getValue();
										        var items = NS.cbMem.getItems();	//arreglo de folios seleccionados
				                				var items_aux = NS.gridPropuesta.getSelectionModel().getSelections();
				                				
										        if(items_aux.length == 0) {
										        	Ext.Msg.alert('SET','Es necesario seleccionar algun registro');
				                					return;
				                				}
										        
										        // Se comenta hasta que se libera la parte de flujo y posicion
										        for(i=0;i<items_aux.length;i++){
										        	if((parseInt(items_aux[i].get('confir')) == 0 && parseInt(items_aux[i].get('idGrupoFlujo')) == 79990 && parseInt(items_aux[i].get('idRubro')) == 79991) ||
										        			(parseInt(items_aux[i].get('idGrupoFlujo')) == 79990 && parseInt(items_aux[i].get('idRubro')) == 79991)){
				                						Ext.Msg.alert('SET','Debe clasificar todos los movimientos con un Grupo y Rubro validos!!');
				                						return;
				                					}
				                				}
								            	
										        var bandParcial = false;
										        var noDoctoParcial = '';
										        var noParciales = 0;
										        
			                					var bandParcializado = false;			                					
			                					var noDoctoParcializado = '';
			                					
			                					var noDoctoTmp = '';
			                					
			                					for (var i in items) {
				                					
				                					NS.storeBancoWin.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
													NS.storeChequeraWin.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
				                					
													var fecha = items[i].get('fecValor');//registrosSel[i].get('fecValor');
				                					var anio = '';
				                					
				                					noDoctoTmp = items[i].get( 'noDocto' );
				                					
				                					if( items[i].get( 'observacion' ) == 'PAGO PARCIAL' )
				                					{				                						
				                						bandParcial = true;
				                						noDoctoParcial = noDoctoTmp;
				                						noParciales = noParciales + 1;
				                					}
				                						
				                					if( items[i].get( 'observacion' ) == 'PAGO PARCIALIZADO' )
				                					{
				                						bandParcializado = true;
				                						noDoctoParcializado = noDoctoTmp;
				                					}
				                					
				                					/*
				                					 * se comento porque no permite hacer dos pagos de la misma parcializacion
				                					 * if( bandParcial && bandParcializado ){
				                						if( noDoctoParcial == noDoctoParcializado ){
				                							Ext.Msg.alert('SET', 'La propuesta contiene pagos parciales del mismo documento, operacion incongruente.' );
				                							return;
				                						}
				                					}
				                					
				                					if( ( noDoctoParcial == noDoctoTmp ) && ( noParciales > 1 ) )
				                					{
				                						Ext.Msg.alert('SET', 'La propuesta contiene pagos parciales del mismo documento, operacion incongruente.' );
			                							return;
				                					}*/
					                				
				                					if(items[i].get('fecValor').substring(5,6) ==='/')
				                						anio = items[i].get('fecValor').substring(6,10);
				                					else
				                						anio = items[i].get('fecValor').substring(5,9);
				                					var anioHoy = NS.fechaManana.substring(0,4);

				                					if(parseInt(anio) < parseInt(anioHoy))
				                					{
//				                						Ext.Msg.confirm('SET','El documento '+
//				                						registrosSel[i].get('noDocto') + 
//				                						' es de un ejercicio anterior: ' +
//				                						anio+ ', ¿desea continuar?',function(btn){  
//													         if(btn === 'no'){
//													         	return;
//													         }
//												         });
				                						if(!confirm('El documento '+ items[i].get('noDocto') + 
					                						' es de un ejercicio anterior: ' + anio+ ', ¿desea continuar?'))
				                							return;
				                					}
				                					NS.psFolios = NS.psFolios + items[i].get('noFolioDet') + ",";
										            NS.pdimporteCupo = NS.pdimporteCupo + items[i].get('importe');
										            
												 	if (NS.pdMontoMaximo == 0)
														NS.pdMontoMaximo = NS.pdimporteCupo;
													else
													{
										                if(NS.pdMontoMaximo < NS.pdimporteCupo)
										                    NS.pdMontoMaximo = NS.pdimporteCupo;
													}
													
													
													
										            if (psTmpEmpresa == 0)
										                psTmpEmpresa = items[i].get('noEmpresa');
										            else
										            {
										                if (psTmpEmpresa != items[i].get('noEmpresa'))
										                {
										                    Ext.Msg.alert("SET", "Los movimientos seleccionados deben ser de la misma empresa");
										                    return;
										                }
										            }
									            }//termina for
									            //cargar el combo banco de la ventana
				                				NS.storeBancoWin.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
												NS.storeChequeraWin.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
			                					
				                				NS.storeBancoWin.load();
										            NS.psFolios = NS.psFolios.substring(0, NS.psFolios.length - 1);
										            if (Ext.getCmp(PF+'cmbPropuestasAct').getValue()==='')
										            {
										                if (Ext.getCmp(PF+'txtFecPago').getValue() !== '')
										                {
//										                    Ext.Msg.confirm("SET","Los movimientos seleccionados seran actualizados con " + 
//										                    "la fecha de pago capturada, ¿desea continuar?",function(btn){  
//														         if(btn === 'no')
//														         	return;
//														         else
//														         	psFecPropuesta = Ext.getCmp(PF+'txtFecPago').getValue();
//													         });
										                   	if(confirm("Los movimientos seleccionados serán actualizados con " + 
										                              "la fecha de pago capturada, ¿desea continuar?"))
														         	NS.psFecPropuesta = Ext.getCmp(PF+'txtFecPago').getValue();
													         else return;
										                }
										                else{
										                	Ext.Msg.alert('SET','Introduzca una fecha de pago');
										                	return;
										                	}
										            }
										            
										            if (formaPago === 2)
										            {
											            pdPagoEfectivo = totalPago;
											            ldTotal = totalPago;
										            }
											        else
											            ldTotal = totalPago;

										            if(NS.compraTranfer == true){
										            	NS.CompraTran();
										            }   
										              
										            if (Ext.getCmp(PF+'cmbPropuestasAct').getValue()==='')
										            {
											            NS.giBanco = 0;
											            NS.gsChequera = '';
											            NS.gsDescBanco = '';
											            //NS.storeEmpresaWin.load();
											            //NS.winActualiza.show;
											            //NS.storeBancoWin.load();
											            //NS.storeChequeraWin.load();
											            		
											            openVentana();
											            	//NS.winActualiza.show(this);
										            }else {
										            	var clavePro = Ext.getCmp(PF+'cmbPropuestasAct').getValue();
										            	
										            	Ext.Msg.confirm('SET', 'Los movimientos seleccionados seran agregados a la propuesta ' + clavePro + ', Desea Continuar?' , 
		                    							function(e) {
										            		if(e == 'yes') {
										            			PropuestaPagoManualAction.agregarPropuestas(parseInt(Ext.getCmp(PF + 'cmbEmpresa').getValue()), clavePro,
										            					NS.psFolios, function(res, e){
										            				Ext.Msg.alert('SET', res);
										            				NS.buscarFacturas();
										            				NS.cmbPropuestasAct.reset();
										            				NS.propuestas.removeAll();
										            				NS.propuestas.baseParams.idGrupoEmpresa = parseInt(Ext.getCmp(PF + 'cmbEmpresa').getValue());
										    						NS.propuestas.load();
										            			});
		                    	            				}
										            	});
										            }
				                			}
				                		}
	                				}
			                    }
			                    ,
			                    {
			                        xtype: 'numberfield',
			                        id: PF+'txtRegistrosSel',
			                        name: PF+'txtRegistrosSel',
			                        x: 10,
			                        y: 225,//170,
			                        width: 70,
			                        readOnly:true
			                    }
//	                    	]
//                    	}
	                ]
	            },
	            {
	                xtype: 'label',
	                text: '$ Tot. Pagos Pend.:',
	                x: 130,
	                y: 545,
	            },
	            {
	                xtype: 'textfield',
	                name: PF+'txtMontoTotal',
	                id: PF+'txtMontoTotal',
	                readOnly: true,
	                x: 130,
	                y: 560,
	                width: 100
	            },
	            {
	                xtype: 'label',
	                text: 'Total del Pago:',
	                x: 240,
	                y: 545,//580
	            },
	            {
	                xtype: 'textfield',
	                id: PF+'txtTotalPago',
	                name: PF+'txtTotalPago',
	                readOnly: true,
	                x: 240,
	                y: 560,
	                width: 100
	            },
	            {
	                xtype: 'button',
	                text: 'Limpiar',
	                x: 890,
	                y: 560,
	                width: 80,
	                listeners:{
                  		click:{
              			   fn:function(e){
              			  	limpiar();
              			   }
           			   }
       			   }
	            },
	            {
	                xtype: 'button',
	                text: 'Cerrar',
	                hidden: true,
	                x: 560,
	                y: 635,
	                width: 60
	            }
	        ]
	            }
	            ]
	});
	
	NS.buscarFacturas = function() {
		Ext.getCmp(PF + 'cmdBuscar').setDisabled(true);
		
		if(Ext.getCmp(PF+'cmbEmpresa').getValue() == '') {
			Ext.Msg.alert('SET','Seleccione un grupo de empresa');
			Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
			return;
		}
		
		//empieza seleccion de criterios para la busqueda
		NS.criterios = {};
		NS.matriz = new Array();
		var recordsDatosGrid=NS.storeDatos.data.items;
		
		if(recordsDatosGrid.length>0)
			{
				if(NS.bloqueo == '')
					NS.criterios.bloqueo = 'NO';
				for(var i=0;i<recordsDatosGrid.length;i=i+1)
			{
				if(recordsDatosGrid[i].get('criterio')==='ESTATUS')
				{
					var valorEstatus=recordsDatosGrid[i].get('valor');
					if(valorEstatus!='')
					{
						NS.criterios.estatus = valorEstatus;
					}
					else
					{
						Ext.Msg.alert('SET','Debe agregar un valor a ESTATUS');
						Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
						return;
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='CONCEPTO')
				{
					var valorConcepto=recordsDatosGrid[i].get('valor');
					if(valorConcepto!='')
					{
						NS.criterios.concepto = valorConcepto;
					}
					else
					{
						Ext.Msg.alert('SET','Debe agregar un valor a CONCEPTO');
						Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
						return;
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='CLAVE OPERACION')
				{
					var valorCveO=recordsDatosGrid[i].get('valor');
					if(valorCveO!='')
					{
						NS.criterios.cveOperacion = valorCveO;
					}
					else
					{
						Ext.Msg.alert('SET','Debe agregar un valor a CLAVE OPERACION');
						Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
						return;
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='BLOQUEO')
				{
					var valorBloqueo=recordsDatosGrid[i].get('valor');
					if(valorBloqueo!='')
					{
						NS.criterios.bloqueo = valorBloqueo;
					}
					else
					{
						Ext.Msg.alert('SET','Debe agregar un valor a BLOQUEO');
						Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
						return;
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='FACTURA')
				{
					var valorFactura=recordsDatosGrid[i].get('valor');
					if(valorFactura!='')
					{
						NS.criterios.factura = valorFactura;
					}
					else
					{
						Ext.Msg.alert('SET','Debe agregar un valor a FACTURA');
						Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
						return;
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='NUMERO DE PEDIDO')
				{
					var valorPedido=recordsDatosGrid[i].get('valor');
					if(valorPedido!='')
					{
						NS.criterios.noPedido = valorPedido;
					}
					else
					{
						Ext.Msg.alert('SET','Debe agregar un valor a NUMERO DE PEDIDO');
						Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
						return;
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='MONTOS')
				{
					var valorMontos=recordsDatosGrid[i].get('valor');
					if(valorMontos!='')
					{
						var ini=obtenerValIni(valorMontos);
						var fin=obtenerValFin(valorMontos);
						NS.criterios.monto1 = NS.unformatNumber(''+ini);
						NS.criterios.monto2 = NS.unformatNumber(''+fin);
					}
					else
					{
						Ext.Msg.alert('SET','Debe agregar un valor a los MONTOS');
						Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
						return;
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='FORMA DE PAGO')
				{
					var FormaPago=recordsDatosGrid[i].get('valor');
					if(FormaPago!='')
					{
						var recordsFormaPago=NS.storeFormaPago.data.items;
						for(var j=0;j<recordsFormaPago.length;j=j+1)
						{
							if(recordsFormaPago[j].get('descripcion')===FormaPago)
							{
								var idFormaPago=recordsFormaPago[j].get('id');
								NS.criterios.formaPago = idFormaPago;
							}
						}
						
					}
					else
					{
						Ext.Msg.alert('SET','Debe agregar un valor a FORMA DE PAGO');
						Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
						return;
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='DIVISA')
				{
					var divisa=recordsDatosGrid[i].get('valor');
					if(divisa!='')
					{
						var recordsDivisa=NS.storeDivisa.data.items;
						for(var j=0;j<recordsDivisa.length;j=j+1)
						{
							if(recordsDivisa[j].get('descDivisa')===divisa)
							{
								var idDivisa=recordsDivisa[j].get('idDivisa');
								NS.criterios.divisa = idDivisa;
							}
						}
					}
					else
					{
						Ext.Msg.alert('SET','Debe agregar un valor a DIVISA');
						Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
						return;
					}
				}
			}//for
			//VT
			alert("JCRE 0");
			NS.storeGrupoEgreso.baseParams.noEmpresa = parseInt(NS.GI_NO_EMPRESA);
			alert("JCRE 1");
			NS.storeGrupoEgreso.load();
			alert("JCRE 2");
				
			Ext.getCmp(PF + 'txtRegistrosSel').setValue('');
			alert("JCRE 3");
			Ext.getCmp(PF + 'txtTotalPago').setValue('');
			alert("JCRE 4");
			NS.storeGridPropuesta.removeAll();
			alert("JCRE 5");
			NS.gridPropuesta.getView().refresh();
			alert("JCRE 6");
				
			NS.criterios.grupo = Ext.getCmp(PF+'cmbEmpresa').getValue();
			alert("JCRE 7");
			NS.criterios.empresa = NS.GI_NO_EMPRESA;
			alert("JCRE 8");
			NS.criterios.usuario = NS.GI_USUARIO;
			alert("JCRE 9");
			NS.criterios.tipoChequeNE = false;
			alert("JCRE 10");
			NS.criterios.psDivision = '';
			alert("JCRE 11");
			NS.matriz[0] = NS.criterios;
			alert("JCRE 12");
			var jsonString = Ext.util.JSON.encode(NS.matriz);
			alert("JCRE 13");
			
			//enviar parametros
			NS.storeGridPropuesta.baseParams.criterios = jsonString;
			alert("JCRE 14");
			NS.storeGridPropuesta.load();
			alert("JCRE 15");
			//hay un bug con la siguiente linea ya que no hace la llamada al action
			//se omitio  {params: {start: 0, limit: 10}}
//			NS.storeGridPropuesta.load({params: {start: 0, limit: 10}});// cargar el grid
			//importe total de las propuestas
			
			alert("JCRE 16");
			PropuestaPagoManualAction.sumarImportePropuestas(jsonString, function(result, e){
				alert("JCRE 17");
				Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((result) * 100) / 100));
			});
		}
		else
		{
			Ext.Msg.alert('SET','Seleccione al menos un criterio de búsqueda');
			Ext.getCmp(PF + 'cmdBuscar').setDisabled(false);
		}
	};
	
	
	NS.CompraTran = function(){
		PropuestaPagoManualAction.CompraTransf(NS.regis, function(result, e){
			if(result != ''){
				
				Ext.Msg.alert('set',result);				
				Ext.getCmp(PF + 'cmbDivisaActualiza').setVisible(true);  
				Ext.getCmp(PF + 'div').setVisible(true);
			}
			else 
				alert('sin problemas');
			
			//Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((result) * 100) / 100));
		});	
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
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,''); //num.replace(/([^0-9\.\-])/g,''*1);
	};
	
	function cambiarFecha(fecha)
	{
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
	function limpiar(){
	 	NS.contenedorPropuesta.getForm().reset();
        NS.storeDatos.removeAll();
        NS.gridDatos.getView().refresh();
        NS.storeGridPropuesta.removeAll();
        NS.gridPropuesta.getView().refresh();
        NS.obtenerFechaManana();             			 
        //cargar el primer parametro del grid 
		var datosClase = NS.gridDatos.getStore().recordType;
		var datos = new datosClase({
				       	criterio: 'FORMA DE PAGO',
						valor: ''
	        	});
	   NS.gridDatos.stopEditing();
	   NS.storeDatos.insert(0, datos);
	   NS.gridDatos.getView().refresh();
	   //NS.ventana();
       //NS.winActualiza.hide();
	   Ext.getCmp(PF + 'ejecutarConta').setDisabled(true);
	}
	NS.contenedorPropuesta.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());

///victor	staticCheck("div [id*='gridPropuesta'] div","div [id*='gridPropuesta']",2,".x-grid3-scroller",false);
   
});