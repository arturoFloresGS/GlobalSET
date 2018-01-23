Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.CofiguracionSolicitudPago');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var banderaEleccion=0; 
	
	NS.idPoliza='';
	NS.idGrupo='';
	NS.idRubro='';
	var bandera=0;
	var jsonStringGlob=null;
	NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	
	//checkBox
	
	//Se desactiva el campo a peticion del usuario 25-ene-2016
	NS.chkAreaDestino= new Ext.form.Checkbox({
		id:PF+ 'chkAreaDestino',
		name :PF + 'chkAreaDestino',
		boxLabel: 'Área destino ',
		x: 0,
		y : 0,
		hidden:true
	});
	
	NS.chkAsignacion = new Ext.form.Checkbox({
		id:PF+ 'chkAsignacion',
		name :PF + 'chkAsignacion',
		 boxLabel: 'Asignación ',
		x: 0,
		y : 0		
	});
	
	NS.chkCentroCostos = new Ext.form.Checkbox({
		id:PF+ 'chkCentroCostos ',
		name :PF + 'chkCentroCostos',
		boxLabel: 'Centro costos ',
		x: 0,
		y : 20			
	});
	
	//Se desactiva el campo a peticion del usuario 25-ene-2016
	NS.chkDivisaPago= new Ext.form.Checkbox({
		id:PF+ 'chkDivisaPago',
		name :PF + 'chkDivisaPago',
		 boxLabel: 'Divisa pago ',
		 x: 0,
		 y : 40	,
		 hidden:true
	});
	
	NS.chkDivision = new Ext.form.Checkbox({
		id:PF+ 'chkDivision',
		name :PF + 'chkDivision',
		boxLabel: 'Division',
		x: 0,
		y : 40		
	});
	
	NS.chkFechaPago = new Ext.form.Checkbox({
		id:PF+ 'chkFechaPago',
		name :PF + 'chkFechaPago',
		 boxLabel: 'Fecha pago ',
		x: 0,
		y : 60	
	});
	
	NS.chkObservaciones = new Ext.form.Checkbox({
		id:PF+ 'chkObservaciones',
		name :PF + 'chkObservaciones',
		boxLabel: 'Observaciones ',
		x: 0,
		y : 80	
	});
	
	//
	
	NS.chkOrden = new Ext.form.Checkbox({
		id:PF+ 'chkOrden',
		name :PF + 'chkOrden',
		boxLabel: 'Orden ',
		x: 120,
		y : 0		
	});
	
	NS.chkPagoSinPoliza = new Ext.form.Checkbox({
		id:PF+ 'chkPagoSinPoliza',
		name :PF + 'chkPagoSinPoliza',
		 boxLabel: 'Pago sin poliza',
		x: 120,
		y : 20		
	});
	
	NS.chkReferencia = new Ext.form.Checkbox({
		id:PF+ 'chkReferencia',
		name :PF + 'chkReferencia',
		 boxLabel: 'Referencia ',
		x: 120,
		y : 40		
	});
	
	NS.chkReferenciaPago = new Ext.form.Checkbox({
		id:PF+ 'chkReferenciaPago',
		name :PF + 'chkReferenciaPago',
		 boxLabel: 'Referencia pago ',
		x: 120,
		y : 60		
	});

	NS.chkSociedadGL  = new Ext.form.Checkbox({
		id:PF+ 'chkSociedadGL',
		name :PF + 'chkSociedadGL',
		boxLabel: 'Sociedad GL ',
		x: 120,
		y : 80		
	});
	
	//

	
	
	NS.chkTexto = new Ext.form.Checkbox({
		id:PF+ 'chkTexto',
		name :PF + 'chkTexto',
		 boxLabel: 'Texto ',
		x: 240,
		y : 0		
	});

	NS.chkTextoCabecera = new Ext.form.Checkbox({
		id:PF+ 'chkTextoCabecera',
		name :PF + 'chkTextoCabecera',
		 boxLabel: 'Texto cabecera ',
		x: 240,
		y : 20		
	});
	
	NS.chkBancoInterlocutor = new Ext.form.Checkbox({
		id:PF+ 'chkBancoInterlocutor',
		name :PF + 'chkBancoInterlocutor',
		 boxLabel: 'Banco Interlocutor ',
		x: 240,
		y : 40		
	});
	
	/******Vias de pago ********/
	NS.chkCargoCuenta = new Ext.form.Checkbox({
		id:PF+ 'chkCargoCuenta',
		name :PF + 'chkCargoCuenta',
		boxLabel: 'Cargo en cuenta ',
		x: 0,
		y : 0	
	});
	
	NS.chkCheque = new Ext.form.Checkbox({
		id:PF+ 'chkCheque',
		name :PF + 'chkCheque',
		boxLabel: 'Cheque ',
		x: 110,
		y : 0		
	});
	
	NS.chkChequeCaja = new Ext.form.Checkbox({
		id:PF+ 'chkChequeCaja',
		name :PF + 'chkChequeCaja',
		boxLabel: 'Cheque caja',
		x: 175,
		y : 0		
	});
	
	NS.chkTransferencia = new Ext.form.Checkbox({
		id:PF+ 'chkTransferencia',
		name :PF + 'chkTransferencia',
		 boxLabel: 'Transferencia',
		x: 265,
		y : 0	
	});
	
	/***Vias de pago ****/
	
	NS.lbClaseDoc = new Ext.form.Label({
		text: 'Clase documento: ',
		x: 0,
		y : 115	
	});
	
	NS.txtClaseDoc = new Ext.form.TextField({
		id: PF + 'txtClaseDoc',
		name: PF + 'txtClaseDoc',
		x: 100,
		y: 115 ,
		width: 100,
		maxLength: 40,
		enableKeyEvents:true,
		listeners:{
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 40));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 40));
	 				}
	 				
	 			}
	 		}
		}
	});

	NS.lbTransaccion = new Ext.form.Label({
		text: 'Transaccion',
		x: 215,
		y : 115		
	});
	
	NS.txtTransaccion = new Ext.form.TextField({
		id: PF + 'txtTransaccion',
		name: PF + 'txtTransaccion',
		x: 275,
		y: 115,
		width: 100,
		maxLength: 40,
		enableKeyEvents:true,
		listeners:{
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 40));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 40));
	 				}
	 				
	 			}
	 		}
		}
	});
	
	NS.lbBeneficiarios = new Ext.form.Label({
		text: 'Cargar Beneficiarios: ',
		x: 0,
		y : 205		
	});
	
	//TEXFIELD
	
	NS.txtIdTipoPoliza = new Ext.form.TextField({
		id: PF + 'txtIdTipoPoliza',
		name: PF + 'txtIdTipoPoliza',
		x: 0,
		y: 0,
		width: 50,
		enableKeyEvents:true,
		listeners:{
			change: {
	    		fn:function(caja) {
		   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	        			valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdTipoPoliza',NS.cmbTipoPolizas.getId());
	        			if(valueCombo != null && valueCombo != undefined && valueCombo != ''){
	        				NS.idPoliza=valueCombo;
	    					NS.txtIdRubroAl.setValue('');
	    					NS.txtIdGrupo.setValue('');
	    					NS.storeRubro.baseParams.idPoliza= valueCombo+'';
	    					NS.txtDescGrupo.setValue('');
	    					NS.cmbRubroAl.setDisabled(false);
	    					NS.txtIdRubroAl.setDisabled(false);
	    					NS.storeRubro.load();
	    					NS.panelPolizasVigentes.show();
            			}else{
	        				NS.btnLimpiar()
	        				NS.cmbRubroAl.setDisabled(true);
	    					NS.txtIdRubroAl.setDisabled(true);
	    					Ext.getCmp(PF + 'txtIdTipoPoliza').reset();
	        			}
	        				
		   			}else
		   				NS.cmbTipoPolizas.reset();
	    		}
	    	}
		}
	});
	
	NS.txtIdGrupo = new Ext.form.TextField({
		name: PF+'txtIdGrupo',
        id: PF+'txtIdGrupo',
		x: 0,
		y: 0,
		width: 50,
		disabled: true,
	});
	
	NS.txtDescGrupo = new Ext.form.TextField({
		name: PF+'txtDescGrupo',
        id: PF+'txtDescGrupo',
        x: 60,
        y: 0,
        width: 170,
		disabled: true,
	});
	
	NS.txtIdRubroAl = new Ext.form.TextField({
		name: PF+'txtIdRubroAl',
        id: PF+'txtIdRubroAl',
		x: 0,
		y: 0,
		width: 50,
		disabled: true,
		enableKeyEvents:true,
        listeners: {
        	change: {
        		fn:function(caja) {
    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
            			valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdRubroAl',NS.cmbRubroAl.getId());
            			if(valueCombo != null && valueCombo != undefined && valueCombo != ''){
            				NS.idRubro=valueCombo;
        					NS.txtIdGrupo.setValue('');
        					//NS.txtIdRubroAl.setValue(combo.getValue());
        					NS.txtDescGrupo.setValue('');
        					NS.storeGrupo.baseParams.idRubro=valueCombo+'';
        					//NS.cmbRubroAl.setDisabled(false);
        					NS.storeGrupo.load();
        					NS.panelChks.hide();
        					NS.panelGridBeneficiarios.hide();
            			}else{
            				Ext.getCmp(PF + 'txtIdRubroAl').reset();
            				Ext.getCmp(PF+'btnNuevo').setVisible(false);
            				Ext.getCmp(PF+'btnModificar').setVisible(false);
            				Ext.getCmp(PF+'btnEliminar').setVisible(false);
            				Ext.getCmp(PF+'btnConsultar').setVisible(false);
            				NS.idGrupo='';
            				NS.idRubro='';
            				NS.txtIdRubroAl.setValue('');
            				NS.txtIdGrupo.setValue('');
            				NS.txtDescGrupo.setValue('');
            			}
    	   			}else
    	   				NS.cmbRubroAl.reset();
        		}
        	}
        }
	});
	
	// fin textfield
	
	
	//Combo polizas
	NS.storeRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idPoliza:''
		},
		root: '',
		paramOrder:['idPoliza'],
		root: '',
		directFn: ConfiguracionSolicitudPagoAction.llenaComboRubro, 
		idProperty: 'idRubro', 
		fields: [
			 {name: 'idRubro'},
			 {name: 'descRubro'},
		],
		listeners: {
			load: function(s, records){
				var myMaskAux = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubro, msg:"Cargando..."});
				if(records.length==null || records.length<=0){
					
				}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
	
	NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idRubro:''
		},
		root: '',
		paramOrder:['idRubro'],
		root: '',
		directFn: ConfiguracionSolicitudPagoAction.llenaComboGrupo,
		idProperty: 'idGrupo', 
		fields: [
			 {name: 'idGrupo'},
			 {name: 'descGrupo'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					
					
				}else{
					Ext.getCmp(PF+'txtIdGrupo').setValue(records[0].data.idGrupo);
					Ext.getCmp(PF+'txtDescGrupo').setValue(records[0].data.descGrupo);
					
					NS.idGrupo=Ext.getCmp(PF+'txtIdGrupo').getValue();
					NS.existe();
					
				}
				NS.myMask.hide();
			},
			exception:function(misc) {
		    	NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
	
	NS.storeTipoPolizas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		root: '',
		directFn: ConfiguracionSolicitudPagoAction.llenaComboPoliza,  
		idProperty: 'idPoliza', 
		
		fields: [
			 {name: 'idPoliza'},
			 {name: 'descPoliza'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No Existen tipos de polizas para este usuario.');
				}
				NS.myMask.hide();
			},
			exception:function(misc) {
		    	NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
	NS.myMask.show();
	NS.storeTipoPolizas.load();
	
	NS.cmbTipoPolizas = new Ext.form.ComboBox({
		store: NS.storeTipoPolizas
		,name: PF+'cmbTipoPolizas'
		,id: PF+'cmbTipoPolizas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 60
        ,y: 0
        ,width: 170
		,valueField:'idPoliza'
		,displayField:'descPoliza'
		,autocomplete: true
		,emptyText: 'Seleccionar Poliza'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.btnLimpiar();
					NS.idPoliza=combo.getValue();
					NS.txtIdRubroAl.setValue('');
					NS.txtIdGrupo.setValue('');
					NS.txtIdTipoPoliza.setValue(combo.getValue());
					NS.storeRubro.baseParams.idPoliza=combo.getValue()+'';
					NS.txtDescGrupo.setValue('');
					NS.cmbRubroAl.setDisabled(false);
					NS.txtIdRubroAl.setDisabled(false);
					NS.storeRubro.load();
					//NS.storeLlenaGridPolizas.load();
			    	NS.panelPolizasVigentes.show();
				}
			}
		}
	});
	
	NS.cmbRubroAl = new Ext.form.ComboBox({
		store: NS.storeRubro
		,name: PF+'cmbRubroAl'
		,id: PF+'cmbRubroAl'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,disabled: true
      	,x: 60
        ,y: 0
        ,width: 170
        ,indexTab: 28
		,valueField:'idRubro'
		,displayField:'descRubro'
		,autocomplete: true
		,emptyText: 'Seleccionar rubro'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idRubro=combo.getValue();
					NS.txtIdGrupo.setValue('');
					NS.txtIdRubroAl.setValue(combo.getValue());
					NS.txtDescGrupo.setValue('');
					NS.storeGrupo.baseParams.idRubro=combo.getValue()+'';
					NS.cmbRubroAl.setDisabled(false);
					NS.storeGrupo.load();
					NS.panelChks.hide();
					NS.panelGridBeneficiarios.hide();
					
					
					
				}
			}
		}
	});
	
	
	//Funciones
	/****************Grid para polizas vigentes ****************/
	NS.storeLlenaGridPolizas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: ConfiguracionSolicitudPagoAction.llenaGridPolizas,
		fields: [
		 	{name: 'idPoliza'},
		 	{name: 'descPoliza'},
		 	{name: 'idGrupo'},
		 	{name: 'descGrupo'},
		 	{name: 'idRubro'},
		 	{name: 'descRubro'},
		 	
		 	{name: 'texto'},
		 	{name: 'textoCabecera'},
		 	{name: 'asignacion'},
		 	{name: 'referencia'},
		 	{name: 'referenciaPago'},
		 	{name: 'division'},
		 	{name: 'centroCostos'},
		 	{name: 'orden'},
		 	{name: 'fechaPago'},
		 	{name: 'observaciones'},
		 	{name: 'divisaPago'},
		 	{name: 'areaDestino'},
		 	{name: 'claseDoc'},
		 	{name: 'pagoSinPoliza'},
		 	{name: 'sociedadGL'},
		 	{name: 'transaccion'},
		 	{name: 'chequeCaja'},
		 	{name: 'cheque'},
		 	{name: 'transferencia'},
		 	{name: 'cargoCuenta'},
		 	{name: 'bancoInterlocutor'}
		],
		listeners: {
			load: function(s, records) {
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No existen polizas vigentes');}
				NS.myMask.hide();
			}
		},
		exception:function(misc) {
	    	NS.myMask.hide();
			Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
		}
	});
	NS.myMask.show();
	 NS.storeLlenaGridPolizas.load();
	//----------------------------------------
	
	NS.columnaSeleccionPolizas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	
	NS.columnasPolizas = new Ext.grid.ColumnModel
	([
	 // NS.columnaSeleccionPolizas,
	  {header: 'Poliza', width: 50, dataIndex: 'idPoliza', sortable: true},
	  {header: 'Desc.Poliza', width: 240, dataIndex: 'descPoliza', sortable: true},
	  {header: 'Grupo', width: 50, dataIndex: 'idGrupo', sortable: true}	,
	  {header: 'Desc. Grupo', width: 240, dataIndex: 'descGrupo', sortable: true},
	  {header: 'Rubro', width: 50, dataIndex: 'idRubro', sortable: true},
	  {header: 'Desc. Rubro', width: 240, dataIndex: 'descRubro', sortable: true}	,
	]);
	
	NS.gridPolizas = new Ext.grid.GridPanel
	({
		store: NS.storeLlenaGridPolizas,
		id: PF + 'gridPolizas',
		name: PF + 'gridPolizas',
		cm: NS.columnasPolizas,
		sm: NS.columnaSeleccionPolizas,
		x: 0,
		y: 0,
		width: 900,
		height: 380,
		stripRows: false,
		columnLines: true,
		autoScroll: true,
	});
	/****************** polizas vigentes fin  ****************/
	NS.storeLlenaGridBeneficiarios = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idPoliza:'',idGrupo:'',idRubro:'',beneficiarios:''},
		paramOrder: ['idPoliza','idGrupo','idRubro','beneficiarios'],
		directFn: ConfiguracionSolicitudPagoAction.llenaGridBeneficiarios,
		fields: [
		 	{name: 'idBeneficiario'},
		 	{name: 'nombreBeneficiario'},
		],
		listeners: {
			load: function(s, records) {
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No existen beneficiarios asociados');}
				NS.myMask.hide();
			},
			exception:function(misc) {
		    	NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}	
	});
	
	NS.storeTipo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		root: '',
		directFn: ConfiguracionSolicitudPagoAction.llenaComboPoliza,  
		idProperty: 'idPoliza', 
		
		fields: [
			 {name: 'idPoliza'},
			 {name: 'descPoliza'},
		],
		listeners: {
			load: function(s, records){
				var myMaskAux = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTipo, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen tipos de polizas para este usuario.');
					}
				}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
	}); 
	
	NS.existe=function(){
		var vector = {};
		var matriz = new Array();
		vector.idPoliza=NS.idPoliza;
		vector.idGrupo=NS.idGrupo;
		vector.idRubro=NS.idRubro
		matriz[0]=vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		ConfiguracionSolicitudPagoAction.existeConfiguracionSolicitudPago(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined ) {
				if(resultado == 'Existe'){
					Ext.getCmp(PF+'btnModificar').setVisible(true);
					Ext.getCmp(PF+'btnEliminar').setVisible(true);
					Ext.getCmp(PF+'btnNuevo').setVisible(false);
				}else{
					Ext.getCmp(PF + 'btnNuevo' ).setVisible(true);
					Ext.getCmp(PF+'btnModificar').setVisible(false);
					Ext.getCmp(PF+'btnEliminar').setVisible(false);
				}
				NS.panelPolizasVigentes.hide();
				Ext.getCmp(PF+'btnConsultar').setVisible(true);
			}
				
		});
	}
	
	NS.btnLimpiar=function(){
		NS.panelChks.hide();
		NS.panelGridBeneficiarios.hide();
		Ext.getCmp(PF+'btnNuevo').setVisible(false);
		Ext.getCmp(PF+'btnModificar').setVisible(false);
		Ext.getCmp(PF+'btnEliminar').setVisible(false);
		Ext.getCmp(PF+'btnConsultar').setVisible(false);
		NS.idPoliza='';
		NS.idGrupo='';
		NS.idRubro='';
		NS.txtIdRubroAl.setValue('');
		NS.txtIdGrupo.setValue('');
		NS.txtIdTipoPoliza.setValue('');
		NS.txtClaseDoc.setValue('');
		NS.txtTransaccion.setValue('');
		NS.cmbRubroAl.reset();
		NS.txtDescGrupo.setValue('');
		NS.cmbRubroAl.setDisabled(true);
		NS.txtIdRubroAl.setDisabled(true);
		NS.txtDescGrupo.setDisabled(true);
		NS.cmbTipoPolizas.setDisabled(false);
		NS.chkAreaDestino.setValue(false);
		NS.chkAsignacion.setValue(false);
		NS.chkCentroCostos.setValue(false);
		NS.chkDivisaPago.setValue(false);
		NS.chkDivision.setValue(false);
		NS.chkFechaPago.setValue(false); 
		NS.chkObservaciones.setValue(false);
		NS.chkOrden.setValue(false);
		NS.chkPagoSinPoliza.setValue(false);
		NS.chkReferencia.setValue(false);
		NS.chkReferenciaPago.setValue(false);
		NS.chkSociedadGL.setValue(false);
		NS.chkTexto.setValue(false);
		NS.chkTextoCabecera.setValue(false);
		NS.chkChequeCaja.setValue(false);
		NS.chkCheque.setValue(false);
		NS.chkTransferencia.setValue(false);
		NS.chkCargoCuenta.setValue(false);
		NS.chkBancoInterlocutor.setValue(false);
		jsonStringGlob=null;
		jsonExiste=false;
		NS.storeLlenaGridBeneficiarios.removeAll();
		NS.gridBeneficiarios.getView().refresh();
	}
	
	NS.accionarEleccion=function(op){
		var jsonExiste=false;
		var vector = {};
		var matriz = new Array();
		vector.idPoliza=NS.idPoliza;
		vector.idGrupo=NS.idGrupo;
		vector.idRubro=NS.idRubro;
		vector.claseDoc=NS.txtClaseDoc.getValue();
		vector.transaccion=NS.txtTransaccion.getValue();
		if(NS.chkAreaDestino.checked){vector.areaDestino='SI';}else{vector.areaDestino='NO';}
		if(NS.chkAsignacion.checked){vector.asignacion='SI';}else{vector.asignacion='NO';}
		if(NS.chkCentroCostos.checked){vector.centroCostos='SI';}else{vector.centroCostos='NO';}
		if(NS.chkDivisaPago.checked){vector.divisaPago='SI';}else{vector.divisaPago='NO';}
		if(NS.chkDivision.checked){vector.division='SI';}else{vector.division='NO';}
		if(NS.chkFechaPago.checked){vector.fechaPago='SI';}else{vector.fechaPago='NO';}
		if(NS.chkObservaciones.checked){vector.observaciones='SI';}else{vector.observaciones='NO';}
		if(NS.chkOrden.checked){vector.orden='SI';}else{vector.orden='NO';}
		if(NS.chkPagoSinPoliza.checked){vector.pagoSinPoliza='SI';}else{vector.pagoSinPoliza='NO';}
		if(NS.chkReferencia.checked){vector.referencia='SI';}else{vector.referencia='NO';}
		if(NS.chkReferenciaPago.checked){vector.referenciaPago='SI';}else{vector.referenciaPago='NO';}
		if(NS.chkSociedadGL.checked){vector.sociedadGL='SI';}else{vector.sociedadGL='NO';}
		if(NS.chkTexto.checked){vector.texto='SI';}else{vector.texto='NO';}
		if(NS.chkTextoCabecera.checked){vector.textoCabecera='SI';}else{vector.textoCabecera='NO';}
		if(NS.chkChequeCaja.checked){vector.chequeCaja='SI';}else{vector.chequeCaja='NO';}
		if(NS.chkCheque.checked){vector.cheque='SI';}else{vector.cheque='NO';}
		if(NS.chkTransferencia.checked){vector.transferencia='SI';}else{vector.transferencia='NO';}
		if(NS.chkCargoCuenta.checked){vector.cargoCuenta='SI';}else{vector.cargoCuenta='NO';}
		if(NS.chkBancoInterlocutor.checked){vector.bancoInterlocutor='SI';}else{vector.bancoInterlocutor='NO';}

		if(jsonStringGlob!=null){jsonExiste=true;}
		matriz[0]=vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		switch (op) {
		case 1:
			if(NS.txtClaseDoc.getValue()!='' && NS.txtTransaccion.getValue() != ''){
				ConfiguracionSolicitudPagoAction.insertaConfiguracionSolicitudPago(jSonString, function(resultado, e) {
					
					if (resultado != '' && resultado != null && resultado != undefined ) {
						if(resultado == 'Exito'){
							if(jsonExiste){NS.accionarEleccion(5)}else{Ext.Msg.alert('SET', "Exito al guardar la configuración pero no se guardaron beneficiarios");}
							NS.btnLimpiar();
							NS.cmbTipoPolizas.reset();
							NS.storeTipoPolizas.load();
							Ext.getCmp(PF+'btnConsultar').setVisible(true);
						}else{
							Ext.Msg.alert('SET', resultado);
						}		
					}
						
				});
			}else{
				Ext.Msg.alert('SET', "Error: Campos requeridos vacios. ");
			}
		break;
		//editar
		case 2:
			
			ConfiguracionSolicitudPagoAction.updateConfiguracionSolicitudPago(jSonString, function(resultado, e) {
				if (resultado != '' && resultado != null && resultado != undefined ) {
					if(resultado == 'Exito'){
						if(jsonExiste){NS.accionarEleccion(5)}
						NS.btnLimpiar();
						NS.cmbTipoPolizas.reset();
						NS.storeTipoPolizas.load();
						Ext.getCmp(PF+'btnConsultar').setVisible(true);
					}else{
						Ext.Msg.alert('SET', resultado);
					}			
				}
					
			});
		break;
		//eliminar
		case 3:
			ConfiguracionSolicitudPagoAction.deleteConfiguracionSolicitudPago(jSonString, function(resultado, e) {
				if (resultado != '' && resultado != null && resultado != undefined ) {
					if(resultado == 'Exito'){
						NS.btnLimpiar();
						NS.cmbTipoPolizas.reset();
						NS.storePolizas.load();
						Ext.getCmp(PF+'btnConsultar').setVisible(true);
					}
					Ext.Msg.alert('SET', resultado);
				}		
			});
			break;
			//consultar
		case 4:
			ConfiguracionSolicitudPagoAction.consultarConfiguracionSolicitudPago(jSonString, function(resultado, e) {
				if(resultado.areaDestino=='SI'){NS.chkAreaDestino.setValue(true);}else{NS.chkAreaDestino.setValue(false);}
				if(resultado.asignacion=='SI'){NS.chkAsignacion.setValue(true);}else{NS.chkAsignacion.setValue(false);}
				if(resultado.centroCostos=='SI'){NS.chkCentroCostos.setValue(true);}else{NS.chkCentroCostos.setValue(false);}
				if(resultado.divisaPago=='SI'){NS.chkDivisaPago.setValue(true);}else{NS.chkDivisaPago.setValue(false);}
				if(resultado.division=='SI'){NS.chkDivision.setValue(true);}else{NS.chkDivision.setValue(false);}
				if(resultado.fechaPago=='SI'){NS.chkFechaPago.setValue(true);}else{NS.chkFechaPago.setValue(false);}
				if(resultado.observaciones=='SI'){NS.chkObservaciones.setValue(true);}else{NS.chkObservaciones.setValue(false);}
				if(resultado.orden=='SI'){NS.chkOrden.setValue(true);}else{NS.chkOrden.setValue(false);}
				if(resultado.pagoSinPoliza=='SI'){NS.chkPagoSinPoliza.setValue(true);}else{NS.chkPagoSinPoliza.setValue(false);}
				if(resultado.referencia=='SI'){NS.chkReferencia.setValue(true);}else{NS.chkReferencia.setValue(false);}
				if(resultado.referenciaPago=='SI'){NS.chkReferenciaPago.setValue(true);}else{NS.chkReferenciaPago.setValue(false);}
				if(resultado.sociedadGL=='SI'){NS.chkSociedadGL.setValue(true);}else{NS.chkSociedadGL.setValue(false);}
				if(resultado.texto=='SI'){NS.chkTexto.setValue(true);}else{NS.chkTexto.setValue(false);}
				if(resultado.textoCabecera=='SI'){NS.chkTextoCabecera.setValue(true);}else{NS.chkTextoCabecera.setValue(false);}
				if(resultado.chequeCaja=='SI'){NS.chkChequeCaja.setValue(true);}else{NS.chkChequeCaja.setValue(false);}
				if(resultado.cheque=='SI'){NS.chkCheque.setValue(true);}else{NS.chkCheque.setValue(false);}
				if(resultado.transferencia=='SI'){NS.chkTransferencia.setValue(true);}else{NS.chkTransferencia.setValue(false);}
				if(resultado.cargoCuenta=='SI'){NS.chkCargoCuenta.setValue(true);}else{NS.chkCargoCuenta.setValue(false);}
				if(resultado.bancoInterlocutor=='SI'){NS.chkBancoInterlocutor.setValue(true);}else{NS.chkBancoInterlocutor.setValue(false);}
				NS.txtClaseDoc.setValue(resultado.claseDoc);
				NS.txtTransaccion.setValue(resultado.transaccion);
				NS.storeLlenaGridBeneficiarios.baseParams.idPoliza=NS.idPoliza;
				NS.storeLlenaGridBeneficiarios.baseParams.idGrupo=NS.idGrupo;
				NS.storeLlenaGridBeneficiarios.baseParams.idRubro=NS.idRubro;
				NS.storeLlenaGridBeneficiarios.baseParams.beneficiarios='';
				NS.myMask.show();
				NS.storeLlenaGridBeneficiarios.load();	
			});
			break;
		case 5: // Eliminar beneficiarios y volver a agregar
			ConfiguracionSolicitudPagoAction.guardarBeneficiarios(jsonStringGlob,jSonString, function(resultado, e) {
				if (resultado != '' && resultado != null && resultado != undefined ) {
					Ext.Msg.alert('SET', resultado);
				}	
			});
			break;
		case 6: //Eliminar desde el grid
			ConfiguracionSolicitudPagoAction.deleteConfiguracionSolicitudPago(jSonString, function(resultado, e) {
				if (resultado != '' && resultado != null && resultado != undefined ) {
					if(resultado == 'Exito'){
						NS.btnLimpiar();
						NS.storeLlenaGridPolizas.removeAll();
		    			NS.gridPolizas.getView().refresh();
		    			NS.storeLlenaGridPolizas.load();
		   	    	   NS.panelPolizasVigentes.show();
		   	    	   NS.panelChks.hide();
		   	  		  NS.panelGridBeneficiarios.hide();
					}
					Ext.Msg.alert('SET', resultado);
				}		
			});
			break;
		}
	}
	
	NS.eleccion=function(op){
		NS.panelChks.show();
		NS.cmbRubroAl.setDisabled(true);
		NS.txtDescGrupo.setDisabled(true);
		NS.cmbTipoPolizas.setDisabled(true);
		
		switch (op) {
		case 1: //Nuevo
			NS.panelPolizasVigentes.hide();
			bandera=1;
			break;
		case 2://Editar
			NS.txtIdTipoPoliza.setDisabled(true);
			NS.panelPolizasVigentes.hide();
			NS.accionarEleccion(4);
			bandera=2;
			NS.panelGridBeneficiarios.show();
			break;
		case 3: //Eliminar
			Ext.Msg.confirm( 'SET', 'Confirma que desea eliminar el registro' , function(btn){
				if(btn=='yes'){
					NS.accionarEleccion(3);
				}else{
					NS.btnLimpiar();
				}
			}) 
			break;
		case 4:
	    	  NS.panelChks.hide();
	  		  NS.panelGridBeneficiarios.hide();
	  		  Ext.Msg.confirm( 'SET', 'Confirma que desea eliminar el registro' , function(btn){
				if(btn=='yes'){
					NS.accionarEleccion(6);
				}
	  		  }) 
			break;
		}
	};
	
	//Se agrega el excel 09-feb-2016
	
	/**********************Generar el excel *********************************/
	 NS.validaDatosExcel = function() {		
		var registroSeleccionado = NS.gridPolizas.getSelectionModel().getSelections();
		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(registroSeleccionado.length == 0) {
			registroSeleccionado = NS.storeLlenaGridPolizas.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		
		if(NS.Excel) {
			
			var matriz = new Array();
			
			for(var i=0;i<registroSeleccionado.length;i++){
				var vector = {};
				vector.idPoliza=registroSeleccionado[i].get('idPoliza');
				vector.descPoliza=registroSeleccionado[i].get('descPoliza');
				vector.idGrupo=registroSeleccionado[i].get('idGrupo');
				vector.descGrupo=registroSeleccionado[i].get('descGrupo');
				vector.idRubro=registroSeleccionado[i].get('idRubro');
				vector.descRubro=registroSeleccionado[i].get('descRubro');
				vector.texto=registroSeleccionado[i].get('texto');
				vector.textoCabecera=registroSeleccionado[i].get('textoCabecera');
				vector.asignacion=registroSeleccionado[i].get('asignacion');
				vector.referencia=registroSeleccionado[i].get('referencia');
				vector.referenciaPago=registroSeleccionado[i].get('referenciaPago');
				vector.division=registroSeleccionado[i].get('division');
				vector.centroCostos=registroSeleccionado[i].get('centroCostos');
				vector.orden=registroSeleccionado[i].get('orden');
				vector.fechaPago=registroSeleccionado[i].get('fechaPago');
				vector.observaciones=registroSeleccionado[i].get('observaciones');
				vector.divisaPago=registroSeleccionado[i].get('divisaPago');
				vector.areaDestino=registroSeleccionado[i].get('areaDestino');
				vector.claseDoc=registroSeleccionado[i].get('claseDoc');
				vector.pagoSinPoliza=registroSeleccionado[i].get('pagoSinPoliza');
				vector.sociedadGL=registroSeleccionado[i].get('sociedadGL');
				vector.transaccion=registroSeleccionado[i].get('transaccion');
				vector.chequeCaja=registroSeleccionado[i].get('chequeCaja');
				vector.cheque=registroSeleccionado[i].get('cheque');
				vector.transferencia=registroSeleccionado[i].get('transferencia');
				vector.cargoCuenta=registroSeleccionado[i].get('cargoCuenta');
				vector.bancoInterlocutor=registroSeleccionado[i].get('bancoInterlocutor');
				
				matriz[i] = vector;
	 		
			}
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		NS.exportaExcel(jSonString);
		NS.excel = false;
	
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		ConfiguracionSolicitudPagoAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=parametrosSolicitudPago';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
		/****************Fin de generar excel ***********************/
	
	//Grid beneficiarios
	NS.columnaSeleccionBeneficiarios = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	
	NS.columnasBeneficiarios = new Ext.grid.ColumnModel
	([
	  //NS.columnaSeleccionBeneficiarios,
	  {header: 'Id', width: 50, dataIndex: 'idBeneficiario', sortable: true},
	  {header: 'Beneficiario', width: 250, dataIndex: 'nombreBeneficiario', sortable: true},
	]);
			
	NS.gridBeneficiarios = new Ext.grid.GridPanel
	({
		store: NS.storeLlenaGridBeneficiarios,
		id: PF + 'gridBeneficiarios',
		name: PF + 'gridBeneficiarios',
		cm: NS.columnasBeneficiarios,
		sm: NS.columnaSeleccionBeneficiarios,
		x: 0,
		y: 0,
		width: 380,
		height: 300,
		stripRows: true,
		columnLines: true,
	});
	
	//panel
	NS.PanelCargaExcel = new Ext.FormPanel({
		fileUpload: true,
        title: '',
        width: 350,
        height: 40,
        x:0,
        y:230,
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
			    			NS.panelGridBeneficiarios.hide();
     						NS.storeLlenaGridBeneficiarios.removeAll();
     						NS.gridBeneficiarios.getView().refresh()
     						jsonStringGlob=null;
			    		}
			    	}
			    }
			},{
		 		xtype: 'button',
		 		text: 'Cargar Excel',
		 		x: 260,
		 		y: 0,
		 		width: 70,
		 		listeners:{
		 			click:{
		 				fn:function(e) {
		 					
		 					 if (Ext.getCmp(PF + 'archivoExcel').getValue() != '' ) {
		 		            	  
		 		            		   var strParams = 'nombreArchivoLeer=leerExcelBeneficiarios';
		 								
		 								NS.PanelCargaExcel.getForm().submit({
		 		       						
		 			                       url: '/SET/jsp/bfrmwrk/gnrators/leerExcel.jsp'+ '?' + strParams,
		 			                       waitMsg: 'Leyendo Registros...',
		 			                       success: function (fp, o) {
		 			                    	  
		 			                    	  var obj = JSON.parse(o.result.json);
		 			                    	  
		 			                    	  var matriz = new Array();
		 			                    	  var beneficiarios='';
		 			                    	  for(var i=0; i<obj.length; i++){
		 			                    		  var reg = {};
		 			                    		  reg.beneficiario = obj[i].Beneficiarios;
		 			                    		  beneficiarios=beneficiarios+obj[i].Beneficiarios+', ';
		 			                    		  matriz[i] = reg;
		 			                    	   }
		 			                    	  
		 			                    	  jsonStringGlob = Ext.util.JSON.encode(matriz);
		 			                    	  ConfiguracionSolicitudPagoAction.existenBeneficiarios(jsonStringGlob, function(resultado, e) {
		 			            				if (resultado != '' && resultado != null && resultado != undefined ) {
		 			            					if(resultado=='Exito al cargar el archivo'){
		 			            						NS.panelGridBeneficiarios.show();
		 			            						NS.storeLlenaGridBeneficiarios.baseParams.beneficiarios=beneficiarios;
		 			            						NS.myMask.show();
		 			            						NS.storeLlenaGridBeneficiarios.load();
		 			            					}else{
		 			            						
		 			            						NS.panelGridBeneficiarios.hide();
		 			            						NS.storeLlenaGridBeneficiarios.removeAll();
		 			            						NS.gridBeneficiarios.getView().refresh()
		 			            						Ext.Msg.alert('SET', resultado);
		 			            						jsonStringGlob=null;
		 			            					}
		 			            					
		 			            				}		
		 			                    	  });
		 			                       },
		 			                       failure: function (fp, o) {
		 			                           Ext.MessageBox.alert('ERROR', 'Ocurrio un error mientras se leeía el archivo');
		 			                       }
		 		                   });
		 		               }else{
		 		            	   Ext.Msg.alert('SET', 'Seleccione un archivo');
		 		               }
		 					 
		 				}
		 			}
		 		}
		 	}
       ]
	});
	
	//FIN EXCEL
	
	NS.panelPoliza = new Ext.form.FieldSet ({
		title: 'Poliza',
		x: 0,
		y: 0,
		width: 260,
		height: 70,
		layout: 'absolute',
		items: [
			NS.txtIdTipoPoliza,
			NS.cmbTipoPolizas,
		]
	});
	
	NS.panelGrupo = new Ext.form.FieldSet ({
		title: 'Grupo',
		x: 540,
		y: 0,
		width: 260,
		height: 70,
		layout: 'absolute',
		items: [
			NS.txtIdGrupo,
			NS.txtDescGrupo,
		]
	});
	
	NS.panelRubro = new Ext.form.FieldSet ({
		title: 'Rubro',
		x: 270,
		y: 0 ,
		width: 260,
		height: 70,
		layout: 'absolute',
		items: [
			NS.txtIdRubroAl,
			NS.cmbRubroAl,
		]
	});
	
	/*******************Panel p *********************/
	NS.panelPolizasVigentes = new Ext.form.FieldSet ({
		title: 'Polizas vigentes',
		x: 0,
		y: 140 ,
		width: 825,
		height: 300,
		//layout: 'absolute',
		 
		//collapsible: true,
		buttonAlign: 'center',
	    buttons:[
	     
	      { 
	    	  text:'Modificar',
	    	  id:PF + 'btnModificar2',
	    	  handler:function(){
	    		  var registroSeleccionado= NS.gridPolizas.getSelectionModel().getSelections();
	    		  if (registroSeleccionado.length > 0){
	    			  NS.panelPolizasVigentes.hide();
	    			  NS.idPoliza= registroSeleccionado[0].get('idPoliza');
	    			  NS.idGrupo= registroSeleccionado[0].get('idGrupo');
	    			  NS.idRubro= registroSeleccionado[0].get('idRubro');
	    			  NS.txtIdTipoPoliza.setValue(registroSeleccionado[0].get('idPoliza'));
	  				  NS.txtIdGrupo.setValue(registroSeleccionado[0].get('idGrupo')); 
	  				  NS.txtDescGrupo.setValue(registroSeleccionado[0].get('descGrupo')); 
	  				  NS.txtIdRubroAl.setValue(registroSeleccionado[0].get('idRubro'))
	  				  NS.cmbTipoPolizas.setValue(registroSeleccionado[0].get('descPoliza')); 
	  				  NS.cmbRubroAl.setValue(registroSeleccionado[0].get('descRubro')); 
	    			  NS.eleccion(2);    			  
	    		  }else{
					  Ext.Msg.alert('SET', 'Debe seleccionar un registro');
				  }
	    		 
	    	  } },
	      { 
	    		  text:'Eliminar',
	    		  id:PF + 'btnEliminar3',
	    		  handler:function(){
	    			  var registroSeleccionado= NS.gridPolizas.getSelectionModel().getSelections();
		    		  if (registroSeleccionado.length > 0){
		    			  NS.idPoliza= registroSeleccionado[0].get('idPoliza');
		    			  NS.idGrupo= registroSeleccionado[0].get('idGrupo');
		    			  NS.idRubro= registroSeleccionado[0].get('idRubro');
		    			  NS.eleccion(4);
		    		  }else{
						  Ext.Msg.alert('SET', 'Debe seleccionar un registro');
					  }
	    			  
	    		  } },
	    		  { text:'Cerrar',handler:function(){
	    			  NS.panelPolizasVigentes.hide();
	    			  NS.btnLimpiar();
	    			  NS.cmbTipoPolizas.reset();
	    			  Ext.getCmp(PF+'btnConsultar').setVisible(true);
	    			
	    		  } },
	    		  {text:'Excel',handler:function(){NS.Excel = true; NS.validaDatosExcel();}},
	      
	    ],
	   	items: [
	   	        	NS.gridPolizas
	   	        ],
	});
	
	
	/*************** fin panel EXTRA *******************/
	
	NS.panelBusqueda = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 0,
		width: 825,
		height: 130,
		layout: 'absolute',
		buttonAlign: 'center',
	    buttons:[
	      { text:'Crear Nuevo',id:PF + 'btnNuevo',handler:function(){ NS.eleccion(1); } },
	      { text:'Modificar',id:PF + 'btnModificar',handler:function(){ NS.eleccion(2); } },
	      { text:'Eliminar',id:PF + 'btnEliminar',handler:function(){ NS.eleccion(3); } },
	      { text:'Consultar Polizas Vigentes ',id:PF + 'btnConsultar',handler:function(){ 
	    	  NS.btnLimpiar();
	    	  NS.cmbTipoPolizas.reset();
	    	  NS.myMask.show();
	    	  NS.storeLlenaGridPolizas.load();
	    	  NS.panelPolizasVigentes.show();
	    	  NS.panelChks.hide();
	  		  NS.panelGridBeneficiarios.hide();
	  		  Ext.getCmp(PF+'btnConsultar').setVisible(false);
	      } },
	      ],
		items: [
		        NS.panelPoliza,
		        NS.panelGrupo,
		        NS.panelRubro
		]
	});
	
	NS.panelGridBeneficiarios = new Ext.form.FieldSet ({
		title: 'Beneficiarios asociados a la configuracion: ',
		x: 410,
		y: 140,
		width: 400,
		height: 330,
		layout: 'absolute',
		buttonAlign: 'center',
		items: [
		        NS.gridBeneficiarios
		]
	});	
	NS.panelGridBeneficiarios.hide();
	
	NS.panelViasPago = new Ext.form.FieldSet ({
		title: 'Vias de pago',
		x: 0,
		y : 145	,
		width: 375,
		height: 60,
		layout: 'absolute',
		items: [
		 	NS.chkChequeCaja,
			NS.chkCheque,
			NS.chkTransferencia,
			NS.chkCargoCuenta,
		]
	});
	
	NS.panelChks = new Ext.form.FieldSet ({
		title: 'Opciones',
		x: 0,
		y: 140,
		width: 400,
		height: 330,
		layout: 'absolute',
		buttonAlign: 'center',
	    buttons:[
	      { text:'Guardar',handler:function(){ NS.accionarEleccion(bandera); } },
	      { text:'Cancelar',handler:function(){ 
	    	  NS.btnLimpiar();
	    	  NS.cmbTipoPolizas.reset();
	    	  NS.myMask.show();
	    	  NS.storeTipoPolizas.load(); 
	    	 // Ext.getCmp(PF+'btnConsultar').setVisible(true);
	    	  NS.panelPolizasVigentes.show();
	      } },
	      ],
		items: [
			NS.chkAreaDestino,
			NS.chkAsignacion,
			NS.chkCentroCostos,
			NS.chkDivisaPago,
			NS.chkDivision,
			NS.chkFechaPago,
			NS.chkObservaciones,
			NS.chkOrden,
			NS.chkPagoSinPoliza,
			NS.chkReferencia,
			NS.chkReferenciaPago,
			NS.chkSociedadGL,
			NS.chkTexto,
			NS.chkTextoCabecera,
			NS.chkBancoInterlocutor,
			NS.lbClaseDoc,
			NS.txtClaseDoc,
			NS.lbTransaccion, 
			NS.txtTransaccion,
			NS.lbBeneficiarios,
			NS.PanelCargaExcel,
			NS.panelViasPago
		]
	});
	NS.panelChks.hide();
	
	 NS.global = new Ext.form.FieldSet ({
			title: '',
			x: 0,
			y: 5,
			width: 850,		
			height: 530,
			layout: 'absolute',
			items:
			[
			 	NS.panelBusqueda,
			 	NS.panelGridBeneficiarios,
			 	NS.panelChks,
			 	NS.panelPolizasVigentes
		    ]
		});
	 
	 NS.conceptos = new Ext.FormPanel ({
			title: 'Configuracion Solicitud Pago',
			width: 1300,
			height: 800,
			frame: true,
			padding: 10,
			autoScroll: false,
			layout: 'absolute',
			id: PF + 'conceptos',
			name: PF + 'conceptos',
			renderTo: NS.tabContId,
			items: [
			 	NS.global
			]
		});
	 
	 NS.btnLimpiar();
	 
	 NS.conceptos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
 