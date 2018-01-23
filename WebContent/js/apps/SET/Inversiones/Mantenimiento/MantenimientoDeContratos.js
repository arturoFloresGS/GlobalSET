Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.Inversiones.Mantenimiento');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.banco = 0;
	NS.isrIgualInt = 'N';
	NS.aplicaISR = 'N';
	
	//Variables para agregar datos a los grid de banChe y contactos (VgBC)
	NS.limpiarVgBC = function()
	{
		NS.iIdBanco = 0;
		NS.sDescBanco = '';
		NS.sIdChequera = '';
		NS.iNoContacto = 0;
		NS.sNomContacto = 0; 
	};
	
	
	//Variables para agregar datos al gridConsultarContratos (VgCC)
	NS.limpiarVgCC = function(){
		
		NS.iNumContrato = '';
		NS.sDescripcion = '';
		NS.sIdDivisa = '';
		NS.iPlazoInv = '360';
		NS.iInstitucion = 0;
		NS.cuentaContable = 0; 
		NS.socGl = '';
		NS.subCuenta = '';
		NS.arrayContactos = new Array();
		NS.arrayContactos[0] = 0;
		NS.arrayContactos[1] = 0;
		NS.arrayContactos[2] = 0;
		NS.sIsrBisiesto = 'N';
		NS.sReferencia = '';
		NS.sTasaAlterna = 'NO';
		NS.iValorSalida = 3;
		NS.iNoCuenta = 0;
		
		//Variable para obtener los datos originales del gridConsultarContratos
		// la solo la primera vez
		NS.iEntra = 0;
		
		//Matriz y array para agregar datos para eliminar de bancosChe (delMatArr)
		NS.matrizBanCheDel = new Array();
		NS.arrBanCheDel = {};
		
		
		//banderas para determinar si se modifica o es registro nuevo
		NS.bNuevo = false;
		NS.bModificar = false;
	};
	
	NS.limpiarVgCC();
		//(VgBC)
	NS.limpiarVgBC();
	
	//Objeto para agregar los checks al grid
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true,
		singleSelect: true					
	});
	
	//funcion para limpiar componentes del formulario
	NS.limpiarMod = function(){
		NS.bNuevo = false;
		NS.bModificar = false;
		Ext.getCmp(PF + 'txtNoContrato').setValue('');
		Ext.getCmp(PF + 'txtIdBanco').setValue('');
		Ext.getCmp(PF+'cmbBanco').reset();
		Ext.getCmp(PF + 'txtIdDivisa').setValue('');
		Ext.getCmp(PF+'cmbDivisa').reset();
		Ext.getCmp(PF + 'txtNoContacto').setValue('');
		Ext.getCmp(PF+'cmbContactos').reset();
		Ext.getCmp(PF + 'cmbChequera').reset();
		Ext.getCmp(PF + 'txtIdInstitucion').setValue('');
		Ext.getCmp(PF+'cmbInstitucion').reset();
		Ext.getCmp(PF + 'txtDescripcion').setValue('');
		Ext.getCmp(PF + 'txtReferencia').setValue('');
		Ext.getCmp(PF + 'txtSociedadGl').setValue ('');
		Ext.getCmp(PF + 'txtSubcuenta').setValue('');
		
		NS.txtCuentaContable.setValue('');
		
		NS.gridBancosChe.store.removeAll();
		NS.gridBancosChe.getView().refresh();
		NS.gridContactos.store.removeAll();
		NS.gridContactos.getView().refresh();
	};
	
	
	
	//funcion para limpiar componentes del formulario
	NS.limpiar = function(){
	
		NS.bNuevo = false;
		NS.bModificar = false;
		//(VgCC)
		NS.limpiarVgCC();
		//(VgBC)
		NS.limpiarVgBC();
		//NS.MantenimientoDeContratos.getForm().reset();
		NS.gridConsultarContratos.store.removeAll();
		NS.gridConsultarContratos.getView().refresh();
		NS.gridBancosChe.store.removeAll();
		NS.gridBancosChe.getView().refresh();
		NS.gridContactos.store.removeAll();
		NS.gridContactos.getView().refresh();
		
		
	};

	NS.habilitarDeshabilitar = function(valor){
		Ext.getCmp(PF + 'frameNuevoModificar').setDisabled(valor);
		Ext.getCmp(PF + 'btnAceptar').setDisabled(valor);
	};
	
	NS.addToGridContratos = function(){
		var obtieneRegistros;
		var datosStoreContratos = NS.storeConsultarContratos.data.items;
		var datosGridContratos = NS.gridConsultarContratos.getStore().recordType;
      	var indice = 0;
   		
		if(NS.bNuevo && !NS.bModificar)
		{
			obtieneRegistros = NS.gridConsultarContratos.store.data.items;
			for(var inc=0; inc < datosStoreContratos.length;inc=inc+1)
   			{
   				if(datosStoreContratos[inc].get('contratoInstitucion') === NS.iNumContrato)
   				{
   					indice=inc;
   					NS.storeConsultarContratos.remove(datosStoreContratos[indice]);
   				}
   				else if(datosStoreContratos[inc].get('contratoInstitucion') !== NS.iNumContrato)
   				{
   					NS.storeConsultarContratos.remove(datosStoreContratos[indice]);
   				}
   			}
		}
		else if(NS.bModificar && !NS.bNuevo)
		{
			obtieneRegistros = NS.gridConsultarContratos.getSelectionModel().getSelections();
			for(var inc = 0; inc < datosStoreContratos.length; inc = inc + 1)
   			{
   				if(datosStoreContratos[inc].get('contratoInstitucion') === obtieneRegistros[0].get('contratoInstitucion'))
   				{
   				    indice = inc;
   				    if(NS.iEntra > 0)
   				    {
						NS.storeConsultarContratos.remove(datosStoreContratos[indice]);
						break;
   				    }
   				    NS.iEntra = 1;	
   					NS.sIdDivisa = obtieneRegistros[0].get('idDivisa');
					NS.iNoCuenta = obtieneRegistros[0].get('noCuenta');
					NS.iPlazoInv = obtieneRegistros[0].get('plazoInv');
					NS.sDescripcion = obtieneRegistros[0].get('descCuenta');
					NS.iInstitucion = obtieneRegistros[0].get('noInstitucion');
					NS.iNumContrato = obtieneRegistros[0].get('contratoInstitucion');
					NS.sIsrBisiesto = obtieneRegistros[0].get('bIsrBisiesto');
					NS.sReferencia = obtieneRegistros[0].get('personaAutoriza');
					NS.sTasaAlterna = obtieneRegistros[0].get('condicionAlt');
					NS.iValorSalida = obtieneRegistros[0].get('valorSalida');
					NS.cuentaContable = obtieneRegistros[0].get('noContacto3');
					NS.aplicaISR = obtieneRegistros[0].get('aplicaISR');
					NS.isrIgualInt = obtieneRegistros[0].get('isrIgualInt');
					NS.socGl = obtieneRegistros[0].get('socGl');
					NS.subCuenta = obtieneRegistros[0].get('subCuenta');
					
					
   					NS.storeConsultarContratos.remove(datosStoreContratos[indice]);
   				}
   			}
		}

		//Objeto para agregar datos al gridConsultarContratos
		var datos = new datosGridContratos({
	       	idDivisa: NS.sIdDivisa,
			noCuenta: NS.iNoCuenta,
			plazoInv: NS.iPlazoInv,
			descCuenta: NS.sDescripcion,
			noInstitucion: NS.iInstitucion,
			noContacto1: NS.arrayContactos[0],
			noContacto2: NS.arrayContactos[1],
			noContacto3: NS.cuentaContable,
			contratoInstitucion: NS.iNumContrato,
			descEstatus:'',
			bIsrBisiesto: NS.sIsrBisiesto,
			personaAutoriza: NS.sReferencia,
			condicionAlt: NS.sTasaAlterna,
			valorSalida:NS.iValorSalida,
			aplicaISR: NS.aplicaISR,
			isrIgualInt:NS.isrIgualInt,
			socGl:NS.socGl,
			subCuenta:NS.subCuenta,
		});
		NS.gridConsultarContratos.stopEditing();
		NS.storeConsultarContratos.insert(indice, datos);
		NS.sm.selectRow(indice);
		NS.gridConsultarContratos.getView().refresh();
	};
	
	NS.txtNoEmpresa = new Ext.form.TextField({
		x: 10,
	    y: 15,
	    width: 55,
	    tabIndex: 0,
	    name: PF + 'txtNoEmpresa',
	    id: PF + 'txtNoEmpresa',
	    value: apps.SET.NO_EMPRESA,
	    listeners:{
	    	change:{
	    		fn: function(caja, valor){
	    			var noEmp = BFwrk.Util.updateTextFieldToCombo(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
	    			
	    			if(noEmp == null) {
	    				Ext.getCmp(PF+'txtNoEmpresa').reset();
	    				NS.cmbEmpresa.reset();
	    			}
	    		}
	    	}
	    }
	});
	
	NS.txtCuentaContable = new Ext.form.TextField({
        xtype: 'textfield',
        x: 10,
        y: 150,
        width: 120,
        name: PF + 'txtCuentaContable',
        id: PF + 'txtNoCuentaContable',
        maxLength: 10,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				if(e.keyCode ==10){ 
	 					NS.buscar();
	 				}
	 			}
	 		},
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 10));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 10));
	 				}
	 				
	 			}
	 		},
            change:{
            	fn:function(caja, valor){	  
            		NS.cuentaContable = caja.getValue();
            		NS.addToGridContratos();
            	}
            
            }
		}
    });
	
	
	/*NS.txtSociedadGl = new Ext.form.TextField({
        x: 10,
        y: 150,
        width: 120,
        name: PF + 'txtSociedadGl',
        id: PF + 'txtSociedadGl',
        enabledKeyEvents: true,
        listeners:{
            change:{
            	fn:function(caja, valor){	  
            		NS.socGl = caja.getValue();
            		NS.addToGridContratos();
            	}
            }
        }
    });
	
	NS.txtSubCuenta = new Ext.form.TextField({
        x: 10,
        y: 150,
        width: 120,
        name: PF + 'txtSubcuenta',
        id: PF + 'txtSubcuenta',
        enabledKeyEvents: true,
        listeners:{
            change:{
            	fn:function(caja, valor){	  
            		NS.subCuenta = caja.getValue();
            		NS.addToGridContratos();
            	}
            }
        }
    });*/
	
	//store de cmbEmpresa
	NS.storeEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: InversionesAction.llenarComboEmpresas, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresa, msg:"Cargando..."});
				
				if(records.length === null || records.length <= 0)
					Ext.Msg.alert('SET','No Existen empresas asignadas a este usuario, consulte a su administrador...');
			}
		}
	}); 
	NS.storeEmpresa.load();
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresa,
		name: PF+'cmbEmpresa',
		id: PF+'cmbEmpresa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 70,
        y: 15,
        width: 230,
		valueField:'noEmpresa',
		displayField:'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: apps.SET.NOM_EMPRESA,
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}
	});
	
	//store del combo cmbDivisa
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			condicion : ''
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
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen divisas');
					return;
				}
			}
		}
	}); 
	
	NS.storeDivisa.load();
	
	NS.accionarDivisa = function(valueCombo){
		NS.cmbBanco.reset();
		NS.cmbChequera.reset();
		Ext.getCmp(PF + 'txtIdBanco').reset();
		//Agregar valor de la divisa para mostrar bancos
		NS.storeBanco.baseParams.sIdDivisa = valueCombo;
		NS.storeBanco.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
		NS.storeChequera.baseParams.sIdDivisa = valueCombo;
		NS.storeBanco.load();
	    //Valores para agregar al gridConsultarContratos
		NS.sIdDivisa = valueCombo;
		NS.addToGridContratos();
	};
	
	//combo cmbDivisa
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa,
		name: PF+'cmbDivisa',
		id: PF+'cmbDivisa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 300,
        y: 15,
        width: 140,
		valueField:'idDivisa',
		displayField:'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtIdDivisa',NS.cmbDivisa.getId());
				 	NS.accionarDivisa(combo.getValue());
				}
			}
		}
	});
	
	//store del combo cmbInstitucion
	NS.storeInstitucion = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: InversionesAction.llenarComboInstitucion, 
		idProperty: 'idCasaCambio', 
		fields: [
			 {name: 'idCasaCambio'},
			 {name: 'descCasaCambio'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeInstitucion, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen personas');
					return;
				}
			}
		}
	}); 
		
		NS.storeInstitucion.load();
	
	NS.accionarInstitucion = function(valueCombo){
		Ext.getCmp(PF + 'txtNoContacto').setValue('');
		NS.cmbContactos.reset();
		//NS.storeContactos.baseParams.iInstitucion = valueCombo;
		//NS.storeContactos.load();
		//Parametro para el llenado de las chequeras
		NS.storeChequera.baseParams.iInstitucion = valueCombo;
		//Se agrega valor a la variable y se llama para modificar al griContratos
		NS.iInstitucion = valueCombo;
		NS.addToGridContratos();
	};
	
	//combo cmbInstitucion
	NS.cmbInstitucion = new Ext.form.ComboBox({
		store: NS.storeInstitucion,
		name: PF+'cmbInstitucion',
		id: PF+'cmbInstitucion',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 815,
        y: 15,
        width: 140,
		valueField:'idCasaCambio',
		displayField:'descCasaCambio',
		autocomplete: true,
		emptyText: 'Seleccione institución',
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtIdInstitucion',NS.cmbInstitucion.getId());
				 	NS.accionarInstitucion(combo.getValue());
				}
			}
		}
	});
	
	//store del combo cmbBanco
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iInstitucion : 0,
			sIdDivisa : '',
			noEmpresa: 0
		},
		root: '',
		paramOrder:['iInstitucion', 'sIdDivisa', 'noEmpresa'],
		directFn: InversionesAction.llenarCombosBancosDep, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0) {
					Ext.Msg.alert('SET','No Existen bancos');
					return;
				}
				
				if(NS.banco != 0)
					Ext.getCmp(PF + 'cmbBanco').setValue(NS.storeBanco.getById(NS.banco).get('descripcion'));
				NS.banco = 0;
			}
		}
	}); 
	
	NS.accionarBanco = function(valueCombo){
		Ext.getCmp(PF + 'cmbChequera').reset();
		NS.storeChequera.removeAll();
		NS.storeChequera.baseParams.iIdBanco = valueCombo;
		NS.storeChequera.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue()); 
		NS.storeChequera.baseParams.sIdDivisa = Ext.getCmp(PF + 'txtIdDivisa').getValue();
		NS.storeChequera.load();
	};
	
	//combo cmbBanco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco,
		name: PF+'cmbBanco',
		id: PF+'cmbBanco',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 405,
        y: 65,
        width: 170,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBanco.getId());
				 	NS.accionarBanco(combo.getValue());
				}
			}
		}
	});
	
	//store del combo cmbChequera
	NS.storeChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdBanco :0,
			iInstitucion : 0,
			sIdDivisa: '',
			noEmpresa: 0
		},
		paramOrder:['iIdBanco','iInstitucion','sIdDivisa','noEmpresa'],
		root: '',
		directFn: InversionesAction.obtenerChequeras, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No hay chequeras para este banco');
					return;
				}
			}
		}
	}); 
	
	NS.accionarChequera = function(valueCombo){
		NS.sIdChequera = valueCombo;
	};
	
	//combo cmbChequera
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera,
		name: PF+'cmbChequera',
		id: PF+'cmbChequera',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 595,
        y: 65,
        width: 150,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una chequera',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	//BFwrk.Util.updateComboToTextField(PF+'txtNoContacto',NS.cmbContactos.getId());
				 	NS.accionarChequera(combo.getValue());
				}
			}
		}
	});

	NS.storeContactos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iInstitucion : 0
		},
		root: '',
		paramOrder:['iInstitucion'],
		root: '',
		directFn: InversionesAction.consultarContactosInstitucion, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeContactos, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No hay contactos asociados con la institución');
				}
			}
		}
	}); 
	
	
	//combo cmbContactos
	NS.cmbContactos = new Ext.form.ComboBox({
		store: NS.storeContactos,
		name: PF+'cmbContactos',
		id: PF+'cmbContactos',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 95,
        y: 65,
        width: 180,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un contacto',
		triggerAction: 'all',
		disabled: false,
		maskDisabled: false,
		hidden:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNoContacto',NS.cmbContactos.getId());
				}
			}
		}
	});
	
	//store para el grid principal del grid de contratos
	NS.storeConsultarContratos = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {noEmpresa: 0},
		root: '',
		paramOrder: ['noEmpresa'],
		directFn: InversionesAction.consultarContratos,
		fields: [
			{name: 'idDivisa'},
			{name: 'noCuenta'},
			{name: 'plazoInv'},
			{name: 'descCuenta'},
			{name: 'noInstitucion'},
			{name: 'noContacto1'},
			{name: 'noContacto2'},
			{name: 'noContacto3'},
			{name: 'contratoInstitucion'},
			{name: 'descEstatus'},
			{name: 'bIsrBisiesto'},
			{name: 'personaAutoriza'},
			{name: 'condicionAlt'},
			{name: 'valorSalida'},
			{name: 'aplicaISR'},
			{name: 'isrIgualInt'},
			{name: 'socGl'},
			{name: 'subCuenta'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeConsultarContratos, msg:"Buscando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información  SET','No existen datos con los parametros de búsqueda');
				}
			}
		}
	}); 
	
	NS.gridConsultarContratos = new Ext.grid.GridPanel({		
        store : NS.storeConsultarContratos,
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm,
                 {
					header :'Núm Contrato',
					width :100,
					sortable :true,
					dataIndex :'contratoInstitucion',
					hidden: false
				},{
					header :'Descripción',
					width :150,
					sortable :true,
					dataIndex :'descCuenta'
				},{
					header :'Divisa',
					width :50,
					sortable :true,
					dataIndex :'idDivisa',
					hidden: false
				},{
					header :'Cálculo Int.',
					width :60,
					sortable :true,
					dataIndex :'plazoInv',
					value: '360'
				},{
					header :'Institución',
					width :70,
					sortable :true,
					dataIndex :'noInstitucion'
				},{
					header :'Contacto 1',
					width :70,
					sortable :true,
					dataIndex :'noContacto1',
					hidden:true
				},{
					header :'Contacto 2',
					width :70,
					sortable :true,
					dataIndex :'noContacto2',
					hidden:true
				},{
					header :'Cta Contable',
					width :90,
					sortable :true,
					dataIndex :'noContacto3'					
				},{
				
					header :'Sociedad Gl',
					width :90,
					sortable :true,
					dataIndex :'socGl'					
				},{
					
			
					header :'Subcuenta',
					width :90,
					sortable :true,
					dataIndex :'subCuenta'					
				},{
					header :'ISR Bisiesto',
					width :80,
					sortable :true,
					dataIndex :'bIsrBisiesto'
				},{
					header :'Referencia',
					width :120,
					sortable :true,
					dataIndex :'personaAutoriza'
				},{
					header :'Tasa Alterna',
					width :80,
					sortable :true,
					dataIndex :'condicionAlt'
				},{
					header :'No cuenta',
					width :90,
					sortable :true,
					dataIndex :'noCuenta',
					hidden: true
				},{
					header :'Estatus',
					width :80,
					sortable :true,
					dataIndex :'descEstatus', 
					hidden: true
				},
				{
					header :'Forma Cargo',
					width :80,
					sortable :true,
					dataIndex :'valorSalida', 
					hidden: false
				},
				{
					header :'Aplica ISR',
					width :80,
					sortable :true,
					dataIndex :'aplicaISR', 
					hidden: false
				},
				{
					header :'ISR Igual a intereses',
					width :80,
					sortable :true,
					dataIndex :'isrIgualInt', 
					hidden: false
				}
            ]
        }),
        sm: NS.sm,
        columnLines: true,
        x:0,
        y:0,
        width:960,
        height:160,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
        			
        			
          			var importeTraspaso = 0; 
          			var regSelec = NS.gridConsultarContratos.getSelectionModel().getSelections();
          		 	if(regSelec.length > 0)
          		 	{
          		 		Ext.getCmp(PF + 'btnModificar').setDisabled(false);
          		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(false);
          		 	}
          		 	else
          		 	{
          		 		Ext.getCmp(PF + 'btnModificar').setDisabled(true);
          		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
          		 	}
        		}
        	}
        }
    });
    
    
   	//Esta es la estructura para el grid donde se mostraran  los contactos
	NS.storeGridContactos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'numContacto',//identificador del store
		fields: [
			{name: 'numContacto'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
		
			}
		}
	});
	
	//Grid para mostrar los contactos
	NS.gridContactos = new Ext.grid.GridPanel({
	hidden:true,
	store : NS.storeGridContactos,
	columns : [ {
				 
				header :'Contacto',
				width :60,
				dataIndex :'numContacto'
			}, {
				header :'Descripción',
				width :220,
				dataIndex :'descripcion'
			}
		],
		width: 300,
       	height: 100,
		title :'',
		listeners:{
			dblclick:{
				fn:function(grid){
					var recordsEliminar = NS.gridContactos.getSelectionModel().getSelections();
					var datosStoreContactos = NS.gridContactos.store.data.items;
					 Ext.Msg.confirm('Información SET','¿Esta seguro de eliminar este contacto?',function(btn){  
           		     if(btn === 'yes')		
           		   		{
           		   			for(var i = 0;i < datosStoreContactos.length;i = i + 1)
							{
								if(recordsEliminar[0].get('numContacto') === datosStoreContactos[i].get('numContacto'))
								{
									NS.arrayContactos[i] = 0;
	                   				NS.gridContactos.store.remove(recordsEliminar[0]);
									NS.gridContactos.getView().refresh();
									NS.addToGridContratos();
								}
							}
           		   		}
           		   	});
					
				}
			}
		}
	});
	
	//Esta es la estructura para el grid donde se mostraran los bancos y chequeras
	NS.storeGridBancosChe = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'idBanco',//identificador del store
		fields: [
			{name: 'idBanco'},
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'm'}
		],
		listeners: {
			load: function(s, records){
		
			}
		}
	});
	
	//Grid para mostrar los Bancos y chequeras
	NS.gridBancosChe = new Ext.grid.GridPanel({
	store : NS.storeGridBancosChe,
	columns : [ 
			{
				 
				header :'No. Banco',
				width :80,
				dataIndex :'idBanco'
			},{
				header :'Desc Banco',
				width :180,
				dataIndex :'descBanco'
			},{
				header :'Chequera',
				width :140,
				dataIndex :'idChequera'
			},{
				header :'m',
				width :70,
				dataIndex :'m',
				hidden: true
			}
		],
		width: 400,
       	height: 100,
		title :'',
		listeners:{
			dblclick:{
				fn:function(grid){
					var recordsEliminar= NS.gridBancosChe.getSelectionModel().getSelections();
					var c = NS.matrizBanCheDel.length === null ? 0 : NS.matrizBanCheDel.length;
					 Ext.Msg.confirm('Información SET','¿Esta seguro de eliminar este banco y chequera?',function(btn){  
           		     if(btn === 'yes')		
           		   		{
           		   			for(var i = 0;i < recordsEliminar.length;i = i + 1)
							{
								NS.arrBanCheDel = {};
                   					NS.arrBanCheDel.idBanco = recordsEliminar[i].get('idBanco');
                   					NS.arrBanCheDel.descBanco = recordsEliminar[i].get('descBanco');
                   					NS.arrBanCheDel.idChequera = recordsEliminar[i].get('idChequera');
                   					NS.arrBanCheDel.m = 'E';
                   				NS.matrizBanCheDel[c] = NS.arrBanCheDel;
                   				NS.gridBancosChe.store.remove(recordsEliminar[i]);
								NS.gridBancosChe.getView().refresh();
							}
           		   		}
           		   	});
				}
			}
		}
	});
	
	
	
	
	
	
	
	
	
	
	
	NS.optRadios = new Ext.form.RadioGroup({
		id: PF + 'Radios',
		name: PF + 'Radios',
		x: 25,
		y: 0,	
		items: [
	          {boxLabel: 'Trasferencia', name: 'opt', inputValue: 3, checked: true, id:'T' },  
	          {boxLabel: 'Cargo en Cuenta', name: 'opt', inputValue: 5, id:'C'},  
	     ]
 						     
	});


    NS.ContFormaPago = new Ext.form.FieldSet({
		layout: 'absolute',
		title: '',
		width: 300,
	    height: 50,
	    x: 10,
        y: 65,    
		items: [
		        NS.optRadios
		       ]
		        
	});
	
	
	
	
	NS.MantenimientoDeContratos = new Ext.form.FormPanel({
    title: 'Mantenimiento de Contratos',
    width: 1020,
    height: 600,
    padding: 10,
    layout: 'absolute',
    id: PF + 'MantenimientoDeContratos',
    name: PF + 'MantenimientoDeContratos',
    renderTo: NS.tabContId,
    frame: true,
    autoScroll: true,
         items : [
            {
                xtype: 'fieldset',
                title: '',
                width: 1020,
                height: 650,
                x: 20,
                y: 5,
                layout: 'absolute',
                id: 'framePrinManContratos',
                items: [
                    NS.txtNoEmpresa,
                    NS.cmbEmpresa,
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 15,
                        width: 280,
                        hidden: true,
                        name: PF + 'txtNomEmpresa',
                        id: PF + 'txtNomEmpresa',
                        value: apps.SET.NOM_EMPRESA,
                        readOnly: true
                    },
                    {
                        xtype: 'textfield',
                        x: 310,
                        y: 15,
                        width: 220,
                        name: PF + 'txtNomUsuario',
                        id: PF + 'txtNomUsuario',
                        value: apps.SET.NOMBRE+'  '+apps.SET.APELLIDO_PATERNO+'  '+apps.SET.APELLIDO_MATERNO,
                        readOnly: true
                    },
                    {
                        xtype: 'textfield',
                        x: 550,
                        y: 15,
                        width: 120,
                        name: PF + 'txtFecha',
                        id: PF + 'txtFecha',
                        value: apps.SET.FEC_HOY,
                        readOnly: true
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 880,
                        y: 15,
                        width: 80,
                        height: 22,
                        id: 'btnBuscar',
                        listeners: {
                        	click:{
                        		fn: function(e){
			                		//NS.MantenimientoDeContratos.getForm().reset();//se comentan porque no permiten consultar otra empresa mas que la 5
                    				NS.storeConsultarContratos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
                        			NS.storeConsultarContratos.load();
                        			//NS.limpiarMod();	//se comentan porque no permiten consultar otra empresa mas que la 5
			                		Ext.getCmp(PF + 'frameNuevoModificar').setDisabled(true);
			                		Ext.getCmp(PF + 'frameContratos').setDisabled(false);
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 10,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Usuario:',
                        x: 310,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha:',
                        x: 550,
                        y: 0
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 880,
                        y: 595,
                        width: 80,
                        id: PF + 'btnLimpiar',
                        listeners:{
	                        click:{
	                        	fn: function(e){
	                        		NS.limpiar();
	                        		NS.limpiarMod();
			                		Ext.getCmp(PF + 'frameNuevoModificar').setDisabled(true);
	                        		NS.MantenimientoDeContratos.getForm().reset();
	                        		Ext.getCmp(PF + 'frameContratos').setDisabled(false);
	                        	}
	                        }
                        }
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Contratos',
                        x: 0,
                        y: 45,
                        height: 225,
                        width: 985,
                        layout: 'absolute',
                        id: PF + 'frameContratos',
                        items: [
                        	NS.gridConsultarContratos,
                            {
                                xtype: 'button',
                                text: 'Modificar',
                                x: 700,
                                y: 165,
                                width: 80,
                                id: PF + 'btnModificar',
                                disabled: true,
                                listeners:{
                                	click:{
                                		fn: function(e){
                                			
                                			NS.limpiarMod();
                                			
                                			NS.bNuevo = false;
                                			NS.bModificar = true;
	                                		var regSelec = NS.gridConsultarContratos.getSelectionModel().getSelections();
	                               			NS.banco = 0;
	                               			
	                               			if(regSelec.length <= 0)
	                               				return;
	                               			
	                               			NS.txtCuentaContable.setValue(regSelec[0].get('noContacto3'));
	                               			Ext.getCmp(PF + 'txtNoContrato').setValue(regSelec[0].get('contratoInstitucion'));
	                               			Ext.getCmp(PF + 'txtReferencia').setValue(regSelec[0].get('personaAutoriza'));
	                               			Ext.getCmp(PF + 'txtIdDivisa').setValue(regSelec[0].get('idDivisa'));
	                               			Ext.getCmp(PF + 'txtSociedadGl').setValue(regSelec[0].get('socGl'));
	                               			Ext.getCmp(PF + 'txtSubcuenta').setValue(regSelec[0].get('subCuenta'));
	                               			
	                               			
	                               			if(Ext.getCmp(PF + 'txtIdDivisa').getValue() !== '')
	                               				BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisa',NS.cmbDivisa.getId());
	                               				
	                               			Ext.getCmp(PF + 'txtDescripcion').setValue(regSelec[0].get('descCuenta'));
	                               			Ext.getCmp(PF + 'txtIdInstitucion').setValue(regSelec[0].get('noInstitucion'));
	                               			//Se llama a accionarInstitucion para que se cargue el combo de contactos
	                               			if(Ext.getCmp(PF + 'txtIdInstitucion').getValue() != null 
	                               				&& Ext.getCmp(PF + 'txtIdInstitucion').getValue() != '')
	                               					NS.accionarInstitucion(parseInt(Ext.getCmp(PF + 'txtIdInstitucion').getValue()));
	                               			
	                               			if(Ext.getCmp(PF + 'txtIdInstitucion').getValue() !== '')
	                               				BFwrk.Util.updateTextFieldToCombo(PF+'txtIdInstitucion',NS.cmbInstitucion.getId());
	                               			
	                               			if(regSelec[0].get('plazoInv') == '360')
	                               			{
	                               				Ext.getCmp(PF + 'opt360').setValue(true);
	                               				Ext.getCmp(PF + 'opt365').setValue(false);
	                               			}
	                               			else if(regSelec[0].get('plazoInv') == '365'){
	                               				Ext.getCmp(PF + 'opt365').setValue(true);
	                               				Ext.getCmp(PF + 'opt360').setValue(false);
	                               			}
	                               			
	                               			
	                               			if(regSelec[0].get('bIsrBisiesto') == 'S') 
	                               				Ext.getCmp(PF + 'chkBisiesto').setValue(true);
	                               			else
	                               				Ext.getCmp(PF + 'chkBisiesto').setValue(false);
	                               			/*if(regSelec[0].get('condicionAlt') == 'SI')
	                               				Ext.getCmp(PF + 'chkAlternativo').setValue(true);
	                               			else
	                               				Ext.getCmp(PF + 'chkAlternativo').setValue(false);
	                               				*/
	                               			
	                               			if( regSelec[0].get('valorSalida') === 5 )
	                               				Ext.getCmp(PF + 'Radios').onSetValue('C', true )
	                               			else                               			
	                               				Ext.getCmp(PF + 'Radios').onSetValue( 'T', true )
	                               			
	                               			
	                               			if(regSelec[0].get('aplicaISR') == 'S') 
	                               				Ext.getCmp(PF + 'chkSinISR').setValue(false);
	                               			else
	                               				Ext.getCmp(PF + 'chkSinISR').setValue(true);
	                               			
	                               			if(regSelec[0].get('isrIgualInt') == 'S') 
	                               				Ext.getCmp(PF + 'chkIsrIgualInt').setValue(true);
	                               			else
	                               				Ext.getCmp(PF + 'chkIsrIgualInt').setValue(false);
	                               			
	                               		 	NS.gridContactos.store.removeAll();
						            		NS.gridContactos.getView().refresh();
	                               			//Llamada para llenar el grid de contactos 
	                               			InversionesAction.obtenerContactos(regSelec[0].get('noContacto1'), regSelec[0].get('noContacto2'),
	                               				regSelec[0].get('noContacto3'), function(result, e){
		                               			for(var cont = 0; cont < result.length; cont = cont + 1)
			                        		 	{
												 	var datosClase = NS.gridContactos.getStore().recordType;
							          				var datos = new datosClase({
									                	numContacto: result[cont].id,
														descripcion: result[cont].descripcion
								            		});
								                 		
								                 	if(result[cont].id)	
								                 	{
									                 	NS.arrayContactos[cont] = result[cont].id;
								                       	NS.gridContactos.stopEditing();
									            		NS.storeGridContactos.insert(cont, datos);
									            		NS.gridContactos.getView().refresh();
									            		Ext.getCmp(PF + 'txtNoContacto').setValue(result[0].id);
									            		Ext.getCmp(PF+'cmbContactos').setValue(result[0].descripcion);
								            		}
								            		//NS.gridContactos.store.sort('numContacto','ASC');
							            		}
							            		NS.addToGridContratos();
	                               			});
	                               			
	                               			//Llamada para llenar el grid de gridBancosChe
	                               			InversionesAction.llenarGridBancosContrato(regSelec[0].get('noCuenta'), parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue()), function(result, e){
		                               			for(var c = 0; c < result.length; c = c + 1)
			                        		 	{
												 	var datosClase = NS.gridBancosChe.getStore().recordType;
							          				var datos = new datosClase({
									                	idBanco: result[c].idBanco,
														descBanco: result[c].descBanco,
														idChequera: result[c].idChequera,
														m: 'X'
								            		});
								            		
							                       	NS.gridBancosChe.stopEditing();
								            		NS.storeGridBancosChe.insert(c, datos);
								            		NS.gridBancosChe.getView().refresh();
								            		Ext.getCmp(PF + 'txtIdBanco').setValue(result[0].idBanco);
								            		Ext.getCmp(PF+'cmbBanco').setValue(result[0].descBanco);
								            		NS.banco = result[0].idBanco;
							            			NS.accionarBanco(result[0].idBanco);
							            			Ext.getCmp(PF + 'cmbChequera').setValue(result[0].idChequera);
							            			
							            		}
	                               			});
	                               			//Agregar valor de la divisa para mostrar bancos
	                               			NS.storeBanco.baseParams.sIdDivisa = regSelec[0].get('idDivisa');
	                               			NS.storeBanco.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
	                               			NS.storeBanco.load();
	                               			
	                               			NS.habilitarDeshabilitar(false);
	                               			NS.addToGridContratos();
	                               			
	                               			Ext.getCmp(PF + 'frameContratos').setDisabled(true);
	                               			
	                               			
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'button',
                                text: 'Crear Nuevo',
                                x: 790,
                                y: 165,
                                width: 80,
                                id: 'btnNuevo',
                                listeners:{
                                	click:{
                                		fn: function(e){
			                            
                                			InversionesAction.consultaCajas(function(res){
		                        				if (res != null && res != undefined && res == "") {
		                        						Ext.Msg.alert('SET', "Error en el configura_set");
		                        					} else {
		                        						Ext.getCmp(PF + 'cta').setVisible(false);
		                        						Ext.getCmp(PF + 'sociedad').setVisible(false);
		                        						Ext.getCmp(PF + 'sub').setVisible(false);
		                        						Ext.getCmp(PF + 'txtSociedadGl').setVisible(false);
		                        						Ext.getCmp(PF + 'txtSubcuenta').setVisible(false);
		                        						Ext.getCmp(PF + 'txtNoCuentaContable').setVisible(false);
		                        					}
		                        				});
                           			
                                			
                                			
                                			Ext.getCmp(PF + 'btnModificar').setDisabled(true);
			                  		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
			                  		 		NS.gridConsultarContratos.store.removeAll();
			                  				NS.gridConsultarContratos.getView().refresh();
                                			NS.limpiarMod();
                                			NS.habilitarDeshabilitar(false);
                                			NS.bNuevo = true;
                                			NS.bModificar = false;
                                			
                                		
                                			
                                			
                                			
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'button',
                                text: 'Eliminar',
                                x: 880,
                                y: 165,
                                width: 80,
                                id: PF + 'btnEliminar',
                                name: PF + 'btnEliminar',
                                disabled:true,
                                listeners:{
                                	click:{
                                		fn: function(e){
                                			var regSelec = NS.gridConsultarContratos.getSelectionModel().getSelections();
                                			if(regSelec.length <= 0 || regSelec.length > 1) return;
                                			
                                			Ext.Msg.confirm('SET','¿Esta seguro de eliminar este contrato?', function(btn) {  
            		           		     		if(btn == 'yes') {
            		           		     			InversionesAction.eliminarContratos(parseInt(regSelec[0].get('noCuenta')), parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue()), function(result, e){
				                       					if(result !== null && result !== undefined && result !== '')
				                       					{
				                       						Ext.Msg.alert("Información SET", '' + result.msgUsuario);
				                       						NS.gridConsultarContratos.store.removeAll();
															NS.gridConsultarContratos.getView().refresh();
															NS.storeConsultarContratos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
															NS.storeConsultarContratos.load();
						                        			NS.limpiar();
						                        			NS.habilitarDeshabilitar(true);
				                       					}
				                       				});
            		           		     		}
                                			});
                                		}
                                	}
                            	}
                            }
                            
                            
                        ]
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: '',
                x: 30,
                y: 295,
                width: 1000,
                height: 295,
                layout: 'absolute',
                id: PF + 'frameNuevoModificar',
                disabled:true,
                maskDisabled: true,
                items: [
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 15,
                        width: 80,
                        name: PF + 'txtNoContrato',
                        id: PF + 'txtNoContrato',
                        enabledKeyEvents: true,
                        listeners:{
	                        change:{
	                        	fn:function(caja, valor){
	                        		NS.iNumContrato = caja.getValue();
	                        		NS.addToGridContratos();
	                        	}
	                        }
                        }
                    },  
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 200,
                        width: 120,
                        name: PF + 'txtSociedadGl',
                        id: PF + 'txtSociedadGl',
                        maxLength: 6,
                		enableKeyEvents:true,
                		listeners:{
                			keypress:{
                	 			fn:function(caja, e) {
                	 				if(e.keyCode ==6){ 
                	 					NS.buscar();
                	 				}
                	 			}
                	 		},
                	 		keydown:{
                	 			fn:function(caja, e) {
                	 				if(!caja.isValid()){
                	 					caja.setValue(caja.getValue().substring(0, 6));
                	 				}
                	 			}
                	 		},
                	 		keyup:{
                	 			fn:function(caja, e) {	 				
                	 				if(!caja.isValid()){
                	 					caja.setValue(caja.getValue().substring(0, 6));
                	 				}
                	 				
                	 			}
                	 		},
                	 		
                		 change:{
	                        	fn:function(caja, valor){
	                        		NS.socGl = caja.getValue();
	                        		NS.addToGridContratos();
	                        	}
	                        }
           
                		}
            
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 250,
                        width: 120,
                        name: PF + 'txtSubcuenta',
                        id: PF + 'txtSubcuenta',
                        maxLength: 20,
                		enableKeyEvents:true,
                		listeners:{
                			keypress:{
                	 			fn:function(caja, e) {
                	 				if(e.keyCode ==20){ 
                	 					NS.buscar();
                	 				}
                	 			}
                	 		},
                	 		keydown:{
                	 			fn:function(caja, e) {
                	 				if(!caja.isValid()){
                	 					caja.setValue(caja.getValue().substring(0, 20));
                	 				}
                	 			}
                	 		},
                	 		keyup:{
                	 			fn:function(caja, e) {	 				
                	 				if(!caja.isValid()){
                	 					caja.setValue(caja.getValue().substring(0, 20));
                	 				}
                	 				
                	 			}
                	 		},
	                        change:{
	                        	fn:function(caja, valor){
	                        		NS.subCuenta = caja.getValue();
	                        		NS.addToGridContratos();
	                        	}
	                        }
                        }
                    },
                    {
                        xtype: 'uppertextfield',
                        x: 115,
                        y: 15,
                        width: 120,
                        name: PF + 'txtReferencia',
                        id: PF + 'txtReferencia',
                        listeners:{
                        	change:{
                        		fn: function(caja, valor){
                        			NS.sReferencia = caja.getValue();
                        			NS.addToGridContratos();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'uppertextfield',
                        x: 255,
                        y: 15,
                        width: 40,
                        name: PF + 'txtIdDivisa',
                        id: PF + 'txtIdDivisa',
                        listeners:{
                        	change:{
                        		fn: function(caja, valor){
                        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
               							var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisa',NS.cmbDivisa.getId());
               							
               							if(valueCombo != null && valueCombo != undefined && valueCombo != '')
               								NS.accionarDivisa(valueCombo);
               							else
               								Ext.getCmp(PF+'txtIdDivisa').reset();
                        			}else {
                        				NS.cmbDivisa.reset();
                        			}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'uppertextfield',
                        x: 460,
                        y: 15,
                        width: 260,
                        id: PF + 'txtDescripcion',
                        name: PF + 'txtDescripcion',
                        listeners:{
	                        change:{
	                        	fn: function(caja, valor){
	                        		NS.sDescripcion = caja.getValue();
	                        		NS.addToGridContratos();
	                        	}
	                        }
                        }
                    },
                    {
                       xtype: 'textfield',
                       x: 740,
                       y: 15,
                       width: 70,
                       name: PF + 'txtIdInstitucion',
                       id: PF + 'txtIdInstitucion',
                       listeners:{
                        change:{
                        	fn:function(caja, valor){
                        		if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
              						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdInstitucion',NS.cmbInstitucion.getId());
              						
	              					if(valueCombo != null && valueCombo != undefined && valueCombo != '')
	              						NS.accionarInstitucion(valueCombo);
	              					else
	              						Ext.getCmp(PF+'txtIdInstitucion').reset();
                        		}else {
                        			NS.cmbInstitucion.reset();
                        		}
                        	}
                        }
                       }
                   	},
                   	NS.ContFormaPago,
                    {
                        xtype: 'textfield',
                        hidden:true,
                        x: 10,
                        y: 65,
                        width: 80,
                        id: PF + 'txtNoContacto',
                        name : PF + 'txtNoContacto',
                        listeners:{
                        	change:{
                        		fn: function(e){
                        				BFwrk.Util.updateTextFieldToCombo(PF+'txtNoContacto',NS.cmbContactos.getId());
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'textfield',
                        x: 350,
                        y: 65,
                        width: 50,
                        id: PF + 'txtIdBanco',
                        name: PF + 'txtIdBanco',
                        listeners:{
                        	change:{
                        		fn: function(caja, valor){
                    				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBanco', NS.cmbBanco.getId());
                    				
                    					if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
                    						NS.accionarBanco(valueCombo);
                    					}else {
                    						Ext.getCmp(PF + 'txtIdBanco').reset();
                    						NS.accionarBanco(0);
                    					}
                    				}else {
                    					NS.cmbBanco.reset();
                    					NS.accionarBanco(0);
                    				}
                        		}
                        	}
                        }
                    },
                    NS.cmbContactos,
                    NS.cmbBanco,
                    NS.cmbChequera,
                    {
                        xtype: 'label',
                        text: 'Chequera:',
                        x: 595,
                        y: 50
                    },
                    NS.txtCuentaContable,
                    //NS.socGl,
                    //NS.subCuenta,
                    
                    {
                    	
                        xtype: 'button',
                        
                        text: 'Aceptar',
                        x: 780,
                        y: 220,
                        width: 80,
                        id: PF + 'btnAceptar',
                        name: PF + 'btnAceptar',
                        disabled: true,
                        listeners:{
	                        click:{
	                        	fn: function(e){
	                        		//Inician datos del gridConsultarContratos
	                        		var matrizGridContratos = new Array();
                           			var regGridContratos;
                           			
                           			var idFormaCargo = Ext.getCmp(PF + 'Radios').getValue().getGroupValue( );                           			
                           			
	                       			if(NS.bNuevo && !NS.bModificar)
	                       			{
                           				regGridContratos = NS.gridConsultarContratos.store.data.items;
	                       			}
	                       			
	                       			
	                       			else if(NS.bModificar && !NS.bNuevo)
	                       			{
                           				regGridContratos = NS.gridConsultarContratos.getSelectionModel().getSelections();
	                       			}
	                       			
	                       			for(var i = 0; i < regGridContratos.length; i = i + 1)
	                       			{
	                       				//Inicia Validar Datos
	                       				if(regGridContratos[i].get('idDivisa') === ''
											|| regGridContratos[i].get('noCuenta') === '0'
											|| regGridContratos[i].get('plazoInv') === '0'
											|| regGridContratos[i].get('descCuenta') === ''
											|| regGridContratos[i].get('noInstitucion') === '0'
											|| (regGridContratos[i].get('noContacto1') === '0'
												&& regGridContratos[i].get('noContacto2') === '0'
												&& regGridContratos[i].get('noContacto3') === '0')
											|| regGridContratos[i].get('contratoInstitucion') === '0'
											|| regGridContratos[i].get('personaAutorizada') === ''
											//|| regGridContratos[i].get('socGl') === ''
	                       					//|| regGridContratos[i].get('subCuenta') === ''	
	                       						)
	                       				{
	                       					Ext.Msg.show({
													title: 'Información SET',
													msg: 'Datos Incompletos, favor de verificar',
													icon: Ext.MessageBox.INFO,
													buttons: Ext.MessageBox.OK
												});
	                       					return;
	                       				}
                           			//termina validar datos
	                       				var arrContratos = {};
	                       					arrContratos.idDivisa = regGridContratos[i].get('idDivisa');
											arrContratos.noCuenta = regGridContratos[i].get('noCuenta');
											arrContratos.plazoInv = regGridContratos[i].get('plazoInv');
											arrContratos.descCuenta = regGridContratos[i].get('descCuenta');
											arrContratos.noInstitucion = regGridContratos[i].get('noInstitucion');
											arrContratos.noContacto1 = regGridContratos[i].get('noContacto1');
											arrContratos.noContacto2 = regGridContratos[i].get('noContacto2');
											arrContratos.noContacto3 = regGridContratos[i].get('noContacto3');
											arrContratos.contratoInstitucion = regGridContratos[i].get('contratoInstitucion');
											arrContratos.descEstatus = regGridContratos[i].get('descEstatus');
											arrContratos.bIsrBisiesto = regGridContratos[i].get('bIsrBisiesto');
											arrContratos.personaAutoriza = regGridContratos[i].get('personaAutoriza');
											arrContratos.condicionAlt = regGridContratos[i].get('condicionAlt');
											arrContratos.aplicaISR = regGridContratos[i].get('aplicaISR');
											arrContratos.isrIgualInt = regGridContratos[i].get('isrIgualInt');
											arrContratos.socGl = regGridContratos[i].get('socGl');
											arrContratos.subCuenta = regGridContratos[i].get('subCuenta');
											arrContratos.idFormaCargo = idFormaCargo;
											
										    matrizGridContratos[i] = arrContratos;
	                       			}
	                       			var jsonStringContratos = Ext.util.JSON.encode(matrizGridContratos);
	                       			
	                       			//Inician datos del gridbanChe
	                       			var matrizBanChe = new Array();
	                       			var regGridBanChe = NS.gridBancosChe.store.data.items;
	                       			
                       				//Inicia Validar Datos
                       				
                       				if(regGridBanChe.length <= 0)
                       				{
                       					BFwrk.Util.msgShow('Datos Incompletos, falta banco y/o chequera','INFO');
                       					return;
                       				}
                           			//termina validar datos
	                       			for(var c = 0 ; c < regGridBanChe.length; c = c + 1)
	                       			{
	                       				var arrBanChe = {};
	                       					arrBanChe.idBanco = regGridBanChe[c].get('idBanco');
	                       					arrBanChe.descBanco = regGridBanChe[c].get('descBanco');
	                       					arrBanChe.idChequera = regGridBanChe[c].get('idChequera');
	                       					arrBanChe.m = regGridBanChe[c].get('m');
	                       				    matrizBanChe[c] = arrBanChe;
	                       			}
	                       			//Agregar eliminados
	                       			var z = matrizBanChe.length;
	                       			for(var r = 0; r < NS.matrizBanCheDel.length; r = r + 1)
	                       			{
	                       				matrizBanChe[z] = NS.matrizBanCheDel[r];
	                       				z = z + 1;
	                       			}
	                       			var jsonStringBanChe = Ext.util.JSON.encode(matrizBanChe);
	                       			
	                       			InversionesAction.insertarModificarContratos(NS.bNuevo, NS.bModificar,
	                       				jsonStringContratos, jsonStringBanChe, parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue()),  function(result, e){
	                       			       // Ext.Msg.alert('Aceptar','Ok');
	                       					if(result !== null && result !== undefined && result !== '')
	                       					{
	                       						Ext.Msg.alert("Información SET", '' + result.msgUsuario);
	                       						NS.limpiar();
	                       						NS.limpiarMod();
	                       						Ext.getCmp(PF + 'frameNuevoModificar').setDisabled(true);
	                       						//NS.MantenimientoDeContratos.reset();
	                       					}
	                       				});
	                       			
	                       			Ext.getCmp(PF + 'frameContratos').setDisabled(false);
	                        	}
	                        }
                        }
                    },
                    {
                        xtype: 'checkbox',
                        id:PF+'chkBisiesto',
                        name:PF+'chkBisiesto',
                        x: 800,
                        y: 65,
                        boxLabel: 'Aplica Año Bisiesto',
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        			if(valor==true)
	                     			 	NS.sIsrBisiesto = 'S'
                        			else
                        				NS.sIsrBisiesto = 'N'
                        			
                        			NS.addToGridContratos();
                        		}
                        	}
                        }
                    },
                    /*---{
                        xtype: 'checkbox',
                        id:PF+'chkAlternativo',
                        name:PF+'chkAlternativo',
                        x: 800,
                        y: 65,
                        boxLabel: 'ISR alternativo',
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        			if(valor==true)
                        				NS.sTasaAlterna = 'SI';
	                     			else
	                     				NS.sTasaAlterna = 'NO';		
	                     			NS.addToGridContratos();
                        		}
                        	}
                        }
                    },---*/
                    {
                        xtype: 'checkbox',
                        id:PF+'chkSinISR',
                        name:PF+'chkSinISR',
                        x: 800,
                        y: 80,
                        boxLabel: 'Sin ISR',
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        			if(valor==true){
                        				NS.aplicaISR = 'N';
	                     				NS.isrIgualInt = 'N';		
                       				Ext.getCmp(PF + 'chkIsrIgualInt').setValue(false);
                       			}else
	                     				NS.aplicaISR = 'S';		
	                     			NS.addToGridContratos();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'checkbox',
                        id:PF+'chkIsrIgualInt',
                        name:PF+'chkIsrIgualInt',
                        x: 800,
                        y: 95,
                        boxLabel: 'ISR igual a Intereses',
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        			if(valor==true){
                        				NS.isrIgualInt = 'S';
                        				Ext.getCmp(PF + 'chkSinISR').setValue(false);
                        				NS.aplicaISR = 'S';		
                        			}else
	                     				NS.isrIgualInt = 'N';		
	                     			NS.addToGridContratos();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'Núm. Contrato:',
                        x: 10,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        id: PF + 'cta',
					    name: PF + 'cta',
                        text: 'Cta. Contable:',
                        x: 10,
                        y: 130
                    },
                    {
                        xtype: 'label',
                        id: PF + 'sociedad',
					    name: PF + 'sociedad',
                        text: 'Sociedad Gl:',
                        x: 10,
                        y: 180
                    },
                    {
                        xtype: 'label',
                        id: PF + 'sub',
					    name: PF + 'sub',
                        text: 'Subcuenta:',
                        x: 10,
                        y: 230
                    },
                    {
                        xtype: 'label',
                        text: 'Referencia:',
                        x: 115,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Descripción:',
                        x: 460,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Contactos:',
                        x: 10,
                        y: 50, 
                        hidden:true
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 350,
                        y: 50
                    },
                    {
                        xtype: 'button',
                        text: 'Cancelar',
                        x: 870,
                        y: 220,
                        width: 80,
                        height: 22,
                        id: PF + 'btnCancelar',
                        name: PF + 'btnCancelar',
                        listeners:{
                        	click:{
                        		fn:function(e){
                        			NS.bNuevo = false;
                        			NS.bModificar = false;
                        			NS.limpiar();
	                        		NS.limpiarMod();
		                		Ext.getCmp(PF + 'frameNuevoModificar').setDisabled(true);
		                		Ext.getCmp(PF + 'frameContratos').setDisabled(false);
                        		}
                        	}
                        }
                        
                    },
                    NS.cmbDivisa,
                    NS.cmbInstitucion,
                    {
                        xtype: 'fieldset',
                        title: 'Cálculo Interés',
                        width: 140,
                        height: 80,
                        x: 810,
                        y: 125,
                        layout: 'absolute',
                        id: PF + 'framePlaza',
                        items: [
							{
							    xtype: 'radio',
							    boxLabel: '360 días',
							    x: 10,
							    id: PF + 'opt360',
							    name: PF + 'opt360',
							    checked: true,
							    listeners:{
									check:{
										fn:function(opt, valor){
											if(valor)
											{
												Ext.getCmp(PF+'opt365').setValue(false);
												NS.iPlazoInv = '360';
												NS.addToGridContratos();
											}
							   				else{
							   					
							   				}
										}
									}
								}
							},
							{
							    xtype: 'radio',
							    boxLabel: '365 días',
							    x: 10,
							    y: 20,
							    id: PF + 'opt365',
							    name: PF + 'opt365',
							    checked: false,
							    listeners:{
									check:{
										fn:function(opt, valor){
											if(valor)
											{
												Ext.getCmp(PF+'opt360').setValue(false);
												NS.iPlazoInv = '365';
												NS.addToGridContratos();
											}
							   				else{
							   					
							   				}
										}
									}
								}
							}
						]
                    },
                    {
                        xtype: 'button',
                        text: 'V',
                        x: 750,
                        y: 65,
                        width: 30,
                        id: PF + 'btnBajarBanco',
                        name: PF + 'btnBajarBanco',
                        disabled: false,
                        listeners:{
                        	click:{
                        		fn: function(e){
                    				if(Ext.getCmp(PF + 'cmbBanco').getValue() != '' && Ext.getCmp(PF + 'cmbChequera').getValue() != '') {
	                        		    NS.iIdBanco = parseInt(Ext.getCmp(PF + 'txtIdBanco').getValue());
	                        		    NS.sDescBanco = NS.storeBanco.getById(parseInt(Ext.getCmp(PF + 'txtIdBanco').getValue())).get('descripcion');
	                        		    NS.sIdChequera = Ext.getCmp(PF + 'cmbChequera').getValue();
	                        		    var datGridBanChe = NS.storeGridBancosChe.data.items;
	                        		    
	                        			for(var i = 0; i < datGridBanChe.length; i = i + 1)
	                        			{
											if(datGridBanChe[i].get('idBanco') == NS.iIdBanco && datGridBanChe[i].get('idChequera') == NS.sIdChequera)  
												return;          
										}   
	                        		    
	                        			var datosGridBanco = NS.gridBancosChe.getStore().recordType;
				          				var datos = new datosGridBanco({
						                	idBanco: NS.iIdBanco,
											descBanco: NS.sDescBanco,
											idChequera: NS.sIdChequera,
											m: 'N'
					            		});
					            		
				                       	NS.gridBancosChe.stopEditing();
					            		NS.storeGridBancosChe.insert(datGridBanChe.length, datos);
					            		NS.gridBancosChe.getView().refresh();
                    				}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        x: 280,
                        text: 'V',
                        y: 65,
                        width: 30,
                        hidden:true,
                        id: PF + 'btnBajarContacto',
                        name: PF + 'btnBajarContacto',
                        disabled: false,
                        listeners:{
                        	click:{
                        		fn: function(e){
                    				if(Ext.getCmp(PF + 'cmbContactos').getValue() != '') {
	                    				NS.iNoContacto = Ext.getCmp(PF + 'cmbContactos').getValue();
										NS.sNomContacto = NS.storeContactos.getById(Ext.getCmp(PF + 'cmbContactos').getValue()).get('descripcion');
										var indice = 0;
	                        			var datStoreContactos = NS.storeGridContactos.data.items;
	                        		 	indice = datStoreContactos.length;
	                        			for(var i = 0; i < datStoreContactos.length; i = i + 1)
	                        			{
											if(datStoreContactos[i].get('numContacto') == NS.iNoContacto)  
												return; 
										}        
										 
	                        			var datosContactos = NS.gridContactos.getStore().recordType;
				          				var datos = new datosContactos({
						                	numContacto: NS.iNoContacto,
											descripcion: NS.sNomContacto
					            		});
					                 			
				                       	NS.gridContactos.stopEditing();
					            		NS.storeGridContactos.insert(datStoreContactos.length, datos);
					            		NS.arrayContactos[indice] = datStoreContactos[i].get('numContacto');
					            		NS.gridContactos.getView().refresh();
					            		NS.addToGridContratos();
                    				}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'fieldset',
                        hidden:true,
                        title: '',
                        width: 310,
                        height: 110,
                        x: 10,
                        y: 100,
                        id: PF + 'frameContactos',
                        items:[
                        	NS.gridContactos
                        ]
                        
                    },
                    {
                        xtype: 'fieldset',
                        title: '',
                        width: 430,
                        height: 110,
                        x: 350,
                        y: 100,
                        id: PF + 'frameBanChe',
                        items:[
                        	NS.gridBancosChe
                        ]
                    },
                    {
                        xtype: 'label',
                        text: 'Divisa:',
                        x: 255,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Institución:',
                        x: 780,
                        y: 0
                    }
                ]
            }
        ]
	});
	

	 NS.MantenimientoDeContratos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	 //var obj = $("div[id*='MantenimientoDeContratos'] div div[class='x-panel-ml']")[1];
	 //staticCheck(obj,obj,8,".x-grid3-scroller",false);
});