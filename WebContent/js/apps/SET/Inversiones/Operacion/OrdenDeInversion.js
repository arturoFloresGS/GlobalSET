Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.Inversiones.Operacion.OrdenDeInversion');
	var PF = apps.SET.tabID+'.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.sHora = "";
	NS.sFecHoy = apps.SET.FEC_HOY;
	NS.tipoChequera = '';
	NS.cveContrato = 0;
	NS.noEmpresaInv = 0;
	NS.isrIgualInt = 'S';
	NS.aplicaISR = 'S';

	NS.folioSeq = 0;
	
	NS.modificar = false;
	
	NS.consultaAnidada = false;
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
	
	NS.smBan = new Ext.grid.CheckboxSelectionModel({
		checkOnly: false,
		singleSelect: false,
		
	});

	//Llamada a GlobalAction para obtener la hora actual
	NS.obtenerHora = function(){
		GlobalAction.obtenerHoraActualFormato12(function(result, e){
			if(result.substring(result.length - 1) === '1')
			{
				NS.sHora = result.substring(0,5) + ' PM';
				return NS.sHora;
				//Ext.getCmp(PF + 'cmbHora').setValue(NS.sHora);
			}
			else
			{
				NS.sHora = result.substring(0,5) + ' AM';
				return NS.sHora;
				//Ext.getCmp(PF + 'cmbHora').setValue(NS.sHora);
			}
		});
	};
	
	NS.obtenerHora();
	
	NS.limpiar = function(){
		NS.OrdenDeInversion.getForm().reset();
		//PF + 'frameContratos'
		//PF + 'frameInversionHoy'
		NS.limpiaCmbContrastos();
		NS.resetCmb();
	};
	//Reinicio de combos del formulario pricipal
	NS.resetCmb = function(){
		NS.cmbNumContrato.reset();
		NS.cmbNomContrato.reset();
		NS.cmbContactos.reset();
		NS.cmbTipoValor.reset();
		NS.cmbPapel.reset();
	};
	
	NS.limpiaCmbContrastos = function() {
		NS.cmbNumContrato.reset();
		NS.cmbNomContrato.reset();
		NS.cmbContactos.reset();
		NS.cmbTipoValor.reset();
		NS.cmbPapel.reset();
		Ext.getCmp(PF + 'cmbHora').setValue('');
		
		Ext.getCmp(PF + 'txtCveContrato').setValue('');
		Ext.getCmp(PF + 'txtNomInstitucion').setValue('');
		Ext.getCmp(PF + 'txtNumContacto').setValue('');
		Ext.getCmp(PF + 'txtTipoValor').setValue('');
		Ext.getCmp(PF + 'txtIdDivisa').setValue('');
	};
	
	NS.txtNoEmpresa = new Ext.form.TextField({
		x: 60,
	    y: 0,
	    width: 55,
	    tabIndex: 0,
	    name: PF + 'txtNoEmpresa',
	    id: PF + 'txtNoEmpresa',
//	    value: apps.SET.NO_EMPRESA,
	    listeners:{
	    	change:{
	    		fn: function(caja, valor){
	    			var noEmp = BFwrk.Util.updateTextFieldToCombo(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
	    			
	    			if(noEmp == null) {
	    				Ext.getCmp(PF+'txtNoEmpresa').reset();
	    				NS.cmbEmpresa.reset();
	    			}else
	    				NS.limpiaCmbContrastos();

	    			NS.storeContrato.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
	    			mascara.show();
	    			NS.storeContrato.load();
	    		}
	    	}
	    }
	});
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
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresa, msg:"Cargando..."});
				
				mascara.hide();
				
				if(records.length === null || records.length <= 0)
					Ext.Msg.alert('SET','No Existen empresas asignadas a este usuario, consulte a su administrador...');
			},
			change: function(combo, valor){
				NS.storeContrato.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
				mascara.show();
				NS.storeContrato.load();
			},
			exception:function(misc) {
				mascara.hide();
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
    	x: 120,
        y: 0,
        width: 300,
		valueField:'noEmpresa',
		displayField:'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
//		value: apps.SET.NOM_EMPRESA,
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
				 	
					NS.limpiaCmbContrastos();
				 	NS.storeContrato.removeAll();
				 	NS.storeContrato.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
					NS.storeContrato.load();
				}
			}
		}
	});
	
	//store del combo cmbContrato
	NS.storeContrato = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bInternas : false,
			noEmpresa: 0
		},
		root: '',
		paramOrder:['bInternas', 'noEmpresa'],
		root: '',
		directFn: InversionesAction.obtenerNumeroContratos, 
		idProperty: 'noCuenta', 
		fields: [
			 {name: 'noCuenta'},
			 {name: 'contratoInstitucion'},
			 {name: 'descCuenta'},
			 {name: 'razonSocial'},
			 {name: 'idDivisa'},
			 {name: 'noContacto1'},
			 {name: 'noContacto2'},
			 {name: 'noContacto3'},
			 {name: 'noInstitucion'},
			 {name: 'plazoInv'},
			 {name: 'formaPago'},
			 {name: 'aplicaISR'},
			 {name: 'isrIgualInt'}
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeContrato, msg:"Cargando..."});
				
				mascara.hide();
				
				if(records.length === null || records.length <= 0)
				{
					mascara.hide();
					Ext.Msg.alert('SET','No Existen contratos con esta empresa');
					return;
				}
				
				NS.consultaAnidada = true;
				
				if(NS.modificar){
					var seleccionados = NS.gridDatos.getSelectionModel().getSelections();
					
					Ext.getCmp(PF + 'txtCveContrato').setValue(seleccionados[0].get('cveContrato'));
					
					NS.accionarNumContrato(Ext.getCmp(PF + 'txtCveContrato').getValue());
				}
			},
			exception:function(misc) {
				mascara.hide();
			}

		}
	});
//	NS.storeContrato.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
//	mascara.show();
//	NS.storeContrato.load();
	NS.accionarNumContrato = function(valueCombo){
		
		if(valueCombo <= 0)
		{
			Ext.getCmp(PF + 'btnSaldoInversion').setDisabled(true);
			return;
		}
		
		mascara.show();
		
		Ext.getCmp(PF + 'btnSaldoInversion').setDisabled(false);
		NS.resetCmb();
		Ext.getCmp(PF + 'txtNomInstitucion').setValue(NS.storeContrato.getById(valueCombo).get('razonSocial'));
		Ext.getCmp(PF + 'cmbHora').setValue(NS.sHora);
	 	Ext.getCmp(PF + 'cmbNomContrato').setValue(NS.storeContrato.getById(valueCombo).get('descCuenta'));
	 	Ext.getCmp(PF + 'cmbNumContrato').setValue(NS.storeContrato.getById(valueCombo).get('noCuenta'));
	 	BFwrk.Util.updateComboToTextField(PF + 'txtCveContrato',NS.cmbNumContrato.getId());
//	 	BFwrk.Util.updateComboToTextField(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
	 	Ext.getCmp(PF + 'txtIdDivisa').setValue(NS.storeContrato.getById(valueCombo).get('idDivisa'));
	 	//Se pasa valor al store y se carga
	 	NS.storeTipoValor.baseParams.sIdDivisa = Ext.getCmp(PF + 'txtIdDivisa').getValue();
	 	NS.storeTipoValor.load();
	 	//Se pasan valores al storeContactos para cargar los datos
//	 	var iCont1 = NS.storeContrato.getById(valueCombo).get('noContacto1');
//	 	var iCont2 = NS.storeContrato.getById(valueCombo).get('noContacto2');
//	 	var iCont3 = NS.storeContrato.getById(valueCombo).get('noContacto3');
//	 	NS.storeContactos.baseParams.iContacto1 = iCont1 !== null && iCont1 !== '' ? parseInt(iCont1) : 0;
//	 	NS.storeContactos.baseParams.iContacto2 = iCont2 !== null && iCont2 !== '' ? parseInt(iCont2) : 0;
//	 	NS.storeContactos.baseParams.iContacto3 = iCont3 !== null && iCont3 !== '' ? parseInt(iCont3) : 0;
//	 	NS.storeContactos.load();
	 	
	}; 
	
	//combo cmbContrato
	NS.cmbNumContrato = new Ext.form.ComboBox({
		store: NS.storeContrato,
		name: PF+'cmbNumContrato',
		id: PF+'cmbNumContrato',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 130,
        y: 15,
        width: 290,
		valueField:'noCuenta',
		displayField:'contratoInstitucion',
		autocomplete: true,
		emptyText: 'Seleccione un Número de contrato',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtCveContrato',NS.cmbNumContrato.getId());
				 	NS.accionarNumContrato(combo.getValue());
				}
			}
		}
	});
	
	//store para el combo cmbTipoValor
	NS.storeTipoValor = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			sIdDivisa : ''
		},
		root: '',
		paramOrder:['sIdDivisa'],
		root: '',
		directFn: InversionesAction.obtenerTipoValor, 
		idProperty: 'idTipoValor', 
		fields: [
			 {name: 'idTipoValor'},
			 {name: 'descTipoValor'},
			 {name: 'bIsr'}
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTipoValor, msg:"Cargando..."});
//				mascara.show();
				if(records.length === null || records.length <= 0)
				{
					mascara.hide();
					Ext.Msg.alert('SET','No Existen datos en cat_tipo_valor consulte con el administrador');
					return;
				}
				Ext.getCmp(PF + 'txtTipoValor').setValue(records[0].get('idTipoValor'));
				BFwrk.Util.updateTextFieldToCombo(PF+'txtTipoValor',NS.cmbTipoValor.getId());
				//Se settean valores al storePapel
			 	NS.storePapel.baseParams.sTipoValor =  Ext.getCmp(PF + 'txtTipoValor').getValue();
			 	NS.storePapel.load();
			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
	
	NS.cmbTipoValor = new Ext.form.ComboBox({
		store: NS.storeTipoValor,
		name: PF+'cmbTipoValor',
		id: PF+'cmbTipoValor',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 565,
        y: 65,
        width: 140,
		valueField:'idTipoValor',
		displayField:'descTipoValor',
		autocomplete: true,
		emptyText: 'Seleccione un tipo valor',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtTipoValor',NS.cmbTipoValor.getId());
				 	//Se settean valores al storePapel
				 	NS.storePapel.baseParams.sTipoValor =  Ext.getCmp(PF + 'txtTipoValor').getValue();
				 	NS.storePapel.load();
				}
			}
		}
	});
	
	//combo que contiene los nombre de los contratos
	NS.cmbNomContrato = new Ext.form.ComboBox({
		store: NS.storeContrato,
		name: PF+'cmbNomContrato',
		id: PF+'cmbNomContrato',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 10,
        y: 65,
        width: 230,
		valueField:'noCuenta',
		displayField:'descCuenta',
		autocomplete: true,
		emptyText: 'Seleccione un contrato',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.accionarNumContrato(combo.getValue());
				}
			}
		}
	});
	
	//store para el combo cmbContactos 
	NS.storeContactos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iContacto1 : 0,
			iContacto2 : 0,
			iContacto3 : 0
		},
		root: '',
		paramOrder:['iContacto1','iContacto2','iContacto3'],
		root: '',
		directFn: InversionesAction.llenarComboContactos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeContactos, msg:"Cargando..."});
				
//				mascara.show();
				
				if(records.length === null || records.length <= 0)
				{
					//Ext.Msg.alert('SET','No hay contactos para este contrato');
					Ext.getCmp(PF + 'txtNumContacto').setValue('');
					return;
				}
				//Se settean los primeros valores del store para mostrar los cambios en los combos
				Ext.getCmp(PF + 'txtNumContacto').setValue(records[0].get('id'));
				BFwrk.Util.updateTextFieldToCombo(PF+'txtNumContacto',NS.cmbContactos.getId());
			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
	
	NS.cmbContactos = new Ext.form.ComboBox({
		store: NS.storeContactos,
		name: PF+'cmbContactos',
		id: PF+'cmbContactos',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 575,
        y: 65,
        width: 160,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un contacto',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		hidden:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNumContacto',NS.cmbContactos.getId());
				 
				}
			}
		}
	});
	
	//store para el combo cmbPapel
	NS.storePapel = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			sTipoValor : ''
		},
		root: '',
		paramOrder:['sTipoValor'],
		root: '',
		directFn: InversionesAction.obtenerPapel, 
		idProperty: 'idPapel', 
		fields: [
			 {name: 'idPapel'},
			 {name: 'idTipoValor'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePapel, msg:"Cargando..."});
				
//				mascara.show();
				
				if(records.length === null || records.length <= 0)
				{
					mascara.hide();
					Ext.Msg.alert('SET','No existen valores en la tabla cat_papel, consulte con su administrador...');
					return;
				}
				Ext.getCmp(PF + 'cmbPapel').setValue(records[0].get('idPapel'));
				
			 	NS.storeCmbBancoInv.baseParams.cveContrato = Ext.getCmp(PF + 'txtCveContrato').getValue();
			 	NS.storeCmbBancoInv.baseParams.idEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();;
			 	NS.storeCmbBancoInv.baseParams.tipoChequera = "I";
			 	NS.storeCmbBancoInv.load();

			 	NS.storeCmbBancoReg.baseParams.cveContrato = Ext.getCmp(PF + 'txtCveContrato').getValue();
			 	NS.storeCmbBancoReg.baseParams.idEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
			 	NS.storeCmbBancoReg.load();
			 	
			 	NS.storeCmbBancoInstitucion.baseParams.cveContrato = Ext.getCmp(PF + 'txtCveContrato').getValue();;
			 	NS.storeCmbBancoInstitucion.baseParams.idEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();;
			 	
			 	var formaPago = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'formaPago');
				NS.aplicaISR = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'aplicaISR');
				NS.isrIgualInt = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'isrIgualInt');
			 	
			 	
			 	Ext.getCmp(PF + 'hdFormaPago').setValue(formaPago);

			 	NS.storeCmbBancoInstitucion.removeAll();
				NS.storeCmbChequeraInst.removeAll();
				NS.cmbBancoInstitucion.reset();
				NS.cmbChequeraInst.reset();

				Ext.getCmp(PF+ 'txtBancoInstitucion').setValue('');
				
			 	if(formaPago == 3){
			 		NS.storeCmbBancoInstitucion.load();
			 	}
			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
	//este era un combo pero se cambio a texfiel para que no tengan opcion de cambiar el valor
	NS.cmbPapel = new Ext.form.TextField({
		//store: NS.storePapel,
		name: PF+'cmbPapel',
		id: PF+'cmbPapel',
		selecOnFocus: true,
		forceSelection: true,
    	x: 100,
        y: 115,
        width: 120,
        readOnly: true,
		//typeAhead: true,
		//mode: 'local',
		//minChars: 0,
		//valueField:'idTipoValor',
		//displayField:'idPapel',
		//autocomplete: true,
		//emptyText: 'Seleccione un papel',
		//triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			change:{
				fn: function(combo, valor){
					
				}
			}
		}
	});
	
	////*******Inicia ventana de consulta de posicion de invertir, se declaran stores y combos********/////
	//para esta ventana(CPI)
	//Inicia (***)declaración de stores para grids de la ventana ConsultaDePosicionInvertir(WinCons)
	//Termina(***)
	
	//Declaracion de la ventana de Consulta posicion a invertir
	
	//Termina(CPI)
	NS.validarAccionIntImp = function(){
		if(Ext.getCmp(PF+ 'txtImporteInvertir').getValue() === '' || Ext.getCmp(PF+ 'txtImporteInvertir').getValue() === '0')
			return false;
		if(Ext.getCmp(PF + 'txtTasa').getValue() === '' || Ext.getCmp(PF + 'txtTasa').getValue() === '0')
			return false;
		if(Ext.getCmp(PF + 'txtPlazo').getValue() === '' || Ext.getCmp(PF + 'txtPlazo').getValue() === '0')
			return false;
		return true;	
	};
	
	NS.validarDatos = function(){
	    var sIsr = BFwrk.Util.scanFieldsStore(NS.storeTipoValor, 'descTipoValor',Ext.getCmp(PF + 'cmbTipoValor').getValue(),'isr');
	   
		if(Ext.getCmp(PF+ 'txtImporteInvertir').getValue() === '' || Ext.getCmp(PF+ 'txtImporteInvertir').getValue() === 0)
		{
			BFwrk.Util.msgShow('!Falta indicar importe¡','INFO');
			return false;
		}
		if(Ext.getCmp(PF + 'txtTasa').getValue() === '' || Ext.getCmp(PF + 'txtTasa').getValue() === 0)
		{
			BFwrk.Util.msgShow('!Falta indicar la tasa¡','INFO');
			return false;
		}
		if(Ext.getCmp(PF + 'txtPlazo').getValue() === '' || Ext.getCmp(PF + 'txtPlazo').getValue() === 0)
		{
			BFwrk.Util.msgShow('!Falta indicar el plazo¡','INFO');
			return false;
		}
		if((sIsr === '1') && (Ext.getCmp(PF + 'txtImpuesto').getValue() === '' || Ext.getCmp(PF + 'txtImpuesto').getValue() === 0))
		{
			BFwrk.Util.msgShow('!Falta indicar importe¡','INFO');
			return false;
		}
		if(Ext.getCmp(PF + 'txtFechaVencimiento').getValue() === '')
		{
			BFwrk.Util.msgShow('!Falta indicar la fecha de vencimiento¡','INFO');
			return false;
		}
		if(Ext.getCmp(PF + 'txtInteres').getValue() === '' || Ext.getCmp(PF + 'txtInteres').getValue() === 0)
		{
			BFwrk.Util.msgShow('!Falta indicar el interés¡','INFO');
			return false;
		}
		if((sIsr === '1') && (Ext.getCmp(PF + 'txtImpuestoEsperado').getValue() === '' || Ext.getCmp(PF + 'txtImpuestoEsperado').getValue() === 0))
		{
			BFwrk.Util.msgShow('!Falta indicar el impuesto esperado¡','INFO');
			return false;
		}
		if(Ext.getCmp(PF + 'txtNeto').getValue() === '' || Ext.getCmp(PF + 'txtNeto').getValue() === 0)
		{
			BFwrk.Util.msgShow('!Falta indicar el neteo¡','INFO');
			return false;
		}
		if(Ext.getCmp(PF + 'cmbHora').getValue() === '')
		{
			BFwrk.Util.msgShow('!Falta indicar la hora¡','INFO');
			return false;
		}
		return true;
	};
	
	
	//Inicia fomulario principal de Orden de inversión
	NS.accionarInteresImpuesto = function() {
		var fechaVencimiento;
		
		//validar datos
		if(Ext.getCmp(PF + 'cmbNumContrato').getValue() <= 0) {
			BFwrk.Util.msgShow('Tiene que seleccionar un contrato de la empresa','INFO');
			return;
		}
//		fechaVencimiento = BFwrk.Util.sumDate(Ext.getCmp(PF + 'txtFechaInversion').getValue(), 'dia', Ext.getCmp(PF + 'txtPlazo').getValue());
		fechaVencimiento = BFwrk.Util.sumarDias(new Date(Ext.getCmp(PF + 'txtFechaInversion').getValue()) , 
				parseInt(Ext.getCmp(PF + 'txtPlazo').getValue()));
		
		NS.obtenerInteresesImp = function(){//Esta funcion se realizo asi por la Asincronia que existe entre las llamadas al servidor
			var uCapital = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImporteInvertir').getValue());
   			var uTasa = Ext.getCmp(PF + 'txtTasa').getValue();
   			var iPlazo = Ext.getCmp(PF + 'txtPlazo').getValue();
   			var iDiasAnual = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'plazoInv');
   			var iCuenta = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'noCuenta');
   			var iInstitucion = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'noInstitucion');
   			var sContrato = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'contratoInstitucion');
   			var cisr = Ext.getCmp(PF + 'txtTipoValor').getValue();
   			//var bIsr = BFwrk.Util.scanFieldsStore(NS.storeTipoValor, 'descTipoValor',Ext.getCmp(PF + 'cmbTipoValor').getValue(),'bIsr');
   			
   			Ext.getCmp(PF + 'txtFechaVencimiento').setValue(fechaVencimiento);
   			//Llamada para calcular el interes, impuesto, impuesto esperado.
   			InversionesAction.calcularInteresImpuesto(parseFloat(uCapital), parseFloat(uTasa), parseInt(iPlazo), parseInt(iDiasAnual),
   												      parseInt(iCuenta), parseInt(iInstitucion), sContrato,cisr, function(result, e){	
     				Ext.getCmp(PF + 'txtInteres').setValue(BFwrk.Util.formatNumber(result.interes));
     				Ext.getCmp(PF + 'txtImpuesto').setValue(BFwrk.Util.formatNumber(result.impuesto));
     				Ext.getCmp(PF + 'txtImpuestoEsperado').setValue(BFwrk.Util.formatNumber(result.impuestoEsperado));
     				
     				Ext.getCmp(PF + 'txtNeto').setValue(BFwrk.Util.formatNumber(result.neto));
     				
     				if(NS.aplicaISR == 'N'){
     	 				Ext.getCmp(PF +'txtImpuesto').setValue(0.0);
     	 				Ext.getCmp(PF + 'txtImpuestoEsperado').setValue(0.0);
     	   				Ext.getCmp(PF +'txtNeto').setValue(BFwrk.Util.formatNumber(parseFloat(uCapital) + parseFloat(result.interes)));
     				}
     				
     	   			if(NS.isrIgualInt == 'S'){
     	   				Ext.getCmp(PF +'txtNeto').setValue(BFwrk.Util.formatNumber(uCapital));
     	 				Ext.getCmp(PF +'txtImpuestoEsperado').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF+ 'txtInteres').getValue()));
     	 				Ext.getCmp(PF +'txtImpuesto').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF +'txtTasa').getValue()));
     	   			}
   			});
   			
		};
		
		//Llamada para validar fecha inhabil
		InversionesAction.verificarFechaInhabil(fechaVencimiento, function(res, e){
			if(res !== null && res !== undefined && res !== '' && res === true)
			{
				Ext.Msg.show({
				title: 'Información SET',
				msg: '¿La fecha de vencimiento es un día inhábil, desea continuar?',
					buttons: {
						yes: true,
						no: true,
						cancel: false
					},
					icon: Ext.MessageBox.QUESTION,
					fn: function(btn) {
						if(btn === 'yes')
						{
							NS.obtenerInteresesImp();
						}
						else if(btn === 'no')
						{	
							return;
						}
						else
						{
							return;
						}
					}
				});
			}
			else
			{
				NS.obtenerInteresesImp();
			}
		});
	};
	
	
	NS.storeCmbBancoInv = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{cveContrato: NS.cveContrato,
					idEmpresa: NS.noEmpresaInv,
					tipoChequera: ''+ NS.tipoChequera
					},
		paramOrder:['cveContrato', 'idEmpresa', 'tipoChequera'],
		directFn: InversionesAction.obtenerBancoOrdenInversion,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length === null || records.length <= 0){
					mascara.hide();
					Ext.Msg.alert('SET','No Existen bancos asociados');
					return;
				}
				Ext.getCmp(PF + 'cmbBancoInv').setValue(records[0].get('id'));
				var valueCombo = BFwrk.Util.updateComboToTextField(PF+'txtBancoInv',NS.cmbBancoInv.getId());

				NS.storeCmbChequeraInv.removeAll();
				NS.cmbChequeraInv.reset();

			 	NS.llenaChequerasInv();
				
			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
//	NS.storeCmbBancoInv.load();
	NS.cmbBancoInv = new Ext.form.ComboBox({
		store: NS.storeCmbBancoInv
		,name: PF+'cmbBancoInv'
		,id: PF+'cmbBancoInv'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 80
        ,y: 25
        ,width: 140
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtBancoInv',NS.cmbBancoInv.getId());
					NS.llenaChequerasInv();
				}
			}
		}
	});

	NS.storeCmbBancoReg = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{cveContrato: NS.cveContrato,
					idEmpresa: NS.noEmpresaInv,
					tipoChequera: 'R'
					},
		paramOrder:['cveContrato', 'idEmpresa', 'tipoChequera'],
		directFn: InversionesAction.obtenerBancoOrdenInversion,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length === null || records.length <= 0){
					mascara.hide();
					Ext.Msg.alert('SET','No Existen bancos asociados');
					return;
				}
				Ext.getCmp(PF + 'cmbBancoReg').setValue(records[0].get('id'));
				var valueCombo = BFwrk.Util.updateComboToTextField(PF+'txtBancoReg',NS.cmbBancoReg.getId());
				NS.llenaChequerasReg();
			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
//	NS.storeCmbBancoInv.load();
	NS.cmbBancoReg = new Ext.form.ComboBox({
		store: NS.storeCmbBancoReg
		,name: PF+'cmbBancoReg'
		,id: PF+'cmbBancoReg'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 350
        ,y: 25
        ,width: 140
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtBancoReg',NS.cmbBancoReg.getId());
					NS.llenaChequerasReg();
//					NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});

	NS.storeCmbBancoInstitucion = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{cveContrato: NS.cveContrato,
					idEmpresa: NS.noEmpresaInv
					},
		paramOrder:['idEmpresa','cveContrato'],
		directFn: InversionesAction.obtenerBancoOrdenIinstitucion,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length === null || records.length <= 0){
					mascara.hide();
					Ext.Msg.alert('SET','No Existen bancos asociados');
					return;
				}
				Ext.getCmp(PF + 'cmbBancoInstitucion').setValue(records[0].get('id'));
				var valueCombo = BFwrk.Util.updateComboToTextField(PF+'txtBancoInstitucion',NS.cmbBancoInstitucion.getId());
			 	var formaPago = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'formaPago');

			 	if(formaPago == 3){
			 		NS.llenaChequerasInst();
			 	}

			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
//	NS.storeCmbBancoInv.load();
	NS.cmbBancoInstitucion = new Ext.form.ComboBox({
		store: NS.storeCmbBancoInstitucion
		,name: PF+'cmbBancoInstitucion'
		,id: PF+'cmbBancoInstitucion'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 610
        ,y: 25
        ,width: 140
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtBancoInstitucion',NS.cmbBancoInstitucion.getId());
					NS.llenaChequerasInst();
//					NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});

	/*Chequeuras (I)*/
	
	NS.storeCmbChequeraInv = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
				banco: 0,
				empresa: NS.noEmpresa,
				tipo: NS.cveContrato
			},
		paramOrder:['banco','empresa','tipo'],
		directFn: InversionesAction.obtenerChequeraOrdenInversion,
		idProperty: 'descripcion', 
		fields: [
//			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length === null || records.length <= 0){
					mascara.hide();
					Ext.Msg.alert('SET','No Existen chequeras asociadas');
					return;
				}

				Ext.getCmp(PF + 'cmbChequeraInv').setValue(records[0].get('descripcion'));
				mascara.hide();
			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
	
	NS.cmbChequeraInv = new Ext.form.ComboBox({
		store: NS.storeCmbChequeraInv
		,name: PF+'cmbChequeraInv'
		,id: PF+'cmbChequeraInv'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 0
        ,y: 80
        ,width: 140
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					
				}
			}
		}
	});

	NS.llenaChequerasInv = function(){
		NS.storeCmbChequeraInv.baseParams.banco =  Ext.getCmp(PF + 'txtBancoInv').getValue();
		NS.storeCmbChequeraInv.baseParams.empresa =  Ext.getCmp(PF + 'txtNoEmpresa').getValue();
		NS.storeCmbChequeraInv.baseParams.tipo =  Ext.getCmp(PF + 'txtCveContrato').getValue();
		
//		alert(NS.storeCmbChequeraInv.baseParams.banco +", "+ NS.storeCmbChequeraInv.baseParams.empresa);
		
		NS.storeCmbChequeraInv.load();
	};
	/* Chequera REG */
	
	NS.storeCmbChequeraReg = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
				banco: 0,
				empresa: NS.noEmpresa,
				tipo: NS.cveContrato
			},
		paramOrder:['banco','empresa','tipo'],
		directFn: InversionesAction.obtenerChequeraOrdenInversion,
		idProperty: 'descripcion', 
		fields: [
//			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length === null || records.length <= 0){
					mascara.hide();
					Ext.Msg.alert('SET','No Existen chequeras asociados');
					return;
				}
				Ext.getCmp(PF + 'cmbChequeraReg').setValue(records[0].get('descripcion'));
				mascara.hide();
			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
	
	NS.cmbChequeraReg = new Ext.form.ComboBox({
		store: NS.storeCmbChequeraReg
		,name: PF+'cmbChequeraReg'
		,id: PF+'cmbChequeraReg'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 270
        ,y: 80
        ,width: 140,
		valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					
				}
			}
		}
	});

	NS.llenaChequerasReg = function(){
		NS.storeCmbChequeraReg.baseParams.banco =  Ext.getCmp(PF + 'txtBancoReg').getValue();
		NS.storeCmbChequeraReg.baseParams.empresa =  Ext.getCmp(PF + 'txtNoEmpresa').getValue();
		NS.storeCmbChequeraReg.baseParams.tipo =  Ext.getCmp(PF + 'txtCveContrato').getValue();
		
//		alert(NS.storeCmbChequeraInv.baseParams.banco +", "+ NS.storeCmbChequeraInv.baseParams.empresa);
		
		NS.storeCmbChequeraReg.load();
	};
	
	/* Chequera Inst */
	
	NS.storeCmbChequeraInst = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
				banco: 0,
				empresa: NS.noEmpresa,
				cuenta: 0
			},
		paramOrder:['banco','empresa','cuenta'],
		directFn: InversionesAction.obtenerChequeraOrdenInstitucion,
		idProperty: 'descripcion', 
		fields: [
//			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length === null || records.length <= 0){
					mascara.hide();
					Ext.Msg.alert('SET','No Existen chequeras asociados');
					return;
				}
				Ext.getCmp(PF + 'cmbChequeraInst').setValue(records[0].get('descripcion'));
			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
	
	NS.cmbChequeraInst = new Ext.form.ComboBox({
		store: NS.storeCmbChequeraInst
		,name: PF+'cmbChequeraInst'
		,id: PF+'cmbChequeraInst'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 540
        ,y: 80
        ,width: 140
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					
				}
			}
		}
	});

	NS.llenaChequerasInst = function(){
		NS.storeCmbChequeraInst.baseParams.banco =  Ext.getCmp(PF + 'txtBancoInstitucion').getValue();
		NS.storeCmbChequeraInst.baseParams.empresa =  Ext.getCmp(PF + 'txtNoEmpresa').getValue();
		NS.storeCmbChequeraInst.baseParams.cuenta =  Ext.getCmp(PF + 'txtCveContrato').getValue();
		
//		alert(NS.storeCmbChequeraInv.baseParams.banco +", "+ NS.storeCmbChequeraInv.baseParams.empresa);
		
		NS.storeCmbChequeraInst.load();
	};	
	/*Chequeras (T)*/

	/* GRID (I) */
	//store del grid criterios
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:[],
		directFn: InversionesAction.consultarOrdenesInvPendientes,
		idProperty: 'id', 
		fields: [
			 {name: 'folioSeq'},
			 {name: 'hora'},
			 {name: 'contraparte'},
			 {name: 'importe'},
			 {name: 'instrumento'},
			 {name: 'emisionDe'},
			 {name: 'diasInvertir'},
			 {name: 'sFechaValor'},
			 {name: 'tasa'},
			 {name: 'origen'},
			 {name: 'estatus'},
			 {name: 'contrato'},
			 {name: 'plazo'},
			 {name: 'garantia'},
			 {name: 'idPapel'},
			 {name: 'idTipoValor'},
			 {name: 'cveContrato'},
			 {name: 'noContacto'},
			 {name: 'sFecVenc'},
			 {name: 'minuto'},
			 {name: 'impuestoEsperado'},
			 {name: 'noInstitucion'},
			 {name: 'plazoInv'},
			 {name: 'factorImpuesto'},
			 {name: 'interes'},
			 {name: 'moneda'},
			 {name: 'institucion'},
			 {name: ''},
			 {name: 'rubro'},
			 {name: 'noEmpresa'},
			 {name: 'idBanco'},
			 {name: 'idChequera'},
			 {name: 'idBancoReg'},
			 {name: 'idChequeraReg'},
			 {name: 'idBancoInst'},
			 {name: 'idChequeraInst'},
			 {name: 'grupo'},
			 {name: 'neto'}
		],
		listeners: {
			load: function(s, records){
				if(records.length === null || records.length <= 0){
					mascara.hide();
					Ext.Msg.alert('SET','No existen registros pendientes');
					return;
				}
				Ext.getCmp(PF + 'cmbChequeraInst').setValue(records[0].get('id'));
				mascara.hide();
				NS.limpiar();
			},
			exception:function(misc) {
				mascara.hide();
			}
		}
	}); 
	
	//grid datos
	NS.gridDatos  = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		id: PF+'gridDatos',
		name: PF+'gridDatos',
		width: 1000,
       	height: 260,
		x :0,
		y :580,
		frame: true,
		title :'',
		columns : [ NS.smBan,
		{
			id :'id', header :'Id', width :10, dataIndex :'id', hidden: true
		},
		{
			header :'No. Empresa',width :70,dataIndex :'noEmpresa'
		},
		{
			header :'Folio',width :40,dataIndex :'folioSeq', hidden: true
		}, 
		{
			header :'Hora',width :35,dataIndex :'hora'
		}, 
		{
			header :'Minuto',width :40,dataIndex :'minuto'
		}, 
		{
			header :'Contraparte',width :150,dataIndex :'contraparte', hidden: true
		}, 
		{
			header :'Monto',width :80,dataIndex :'importe', css: 'text-align:right;', renderer:BFwrk.Util.rendererMoney
		}, 
		{
			header :'Instrumento',width :106,dataIndex :'instrumento', hidden: true
		}, 
		{
			header :'De',width :53,dataIndex :'emisionDe', hidden: true
		}, 
		{
			header :'Días a Invertir',width :60,dataIndex :'diasInvertir'
		}, 
		{
			header :'Fecha Valor',width :70,dataIndex :'sFechaValor'
		}, 
		{
			header :'Tasa',width :40,dataIndex :'tasa', css: 'text-align:right;'
		}, 
		{
			header :'Origen',width :69,dataIndex :'origen', hidden:true
		}, 
		{
			header :'Estatus',width :40,dataIndex :'estatus'
		}, 
		{
			header :'No. Contrato',width :80,dataIndex :'contrato'
		}, 
		{
			header :'Plazo',width :60,dataIndex :'plazo', hidden:true
		}, 
		{
			header :'Garantía',width :60,dataIndex :'garantia'
		}, 
		{
			header :'Tipo Papel',width :80,dataIndex :'idPapel'
		}, 
		{
			header :'Tipo Valor',width :60,dataIndex :'idTipoValor'
		}, 
		{
			header :'Cve. Contrato',width :90,dataIndex :'cveContrato'
		}, 
		{
			header :'Contacto',width :87,dataIndex :'noContacto', hidden:true
		}, 
		{
			header :'Fecha de Vencimiento',width :110,dataIndex :'sFecVenc'
		}, 
		{
			header :'Impuesto Esperado',width :80,dataIndex :'impuestoEsperado', css: 'text-align:right;', renderer:BFwrk.Util.rendererMoney
		}, 
		{
			header :'No. Institución',width :90,dataIndex :'noInstitucion'
		}, 
		{
			header :'Plazo Inv.',width :84,dataIndex :'plazo', hidden:true
		}, 
		{
			header :'Factor Impuesto',width :100,dataIndex :'factorImpuesto', css: 'text-align:right;', renderer:BFwrk.Util.rendererMoney
		}, 
		{
			header :'Interés',width :70,dataIndex :'interes', css: 'text-align:right;', renderer:BFwrk.Util.rendererMoney
		},
		{
			header :'Neto',width :70,dataIndex :'neto', css: 'text-align:right;', renderer:BFwrk.Util.rendererMoney
		},
		{
			header :'Divisa',width :50,dataIndex :'moneda'
		}, 
		{
			header :'Institución',width :170,dataIndex :'institucion'
		}, 
		{
			header :'Grupo',width :45,dataIndex :'grupo'
		}, 
		{
			header :'Rubro',width :40,dataIndex :'rubro'
		}, 
		{
			header :'Id banco inversion',width :60,dataIndex :'idBanco'
		}, 
		{
			header :'Chequera de inversión',width :60,dataIndex :'idChequera'
		}, 
		{
			header :'Id regresa banco',width :60,dataIndex :'idBancoReg'
		}, 
		{
			header :'Chequera regreso',width :60,dataIndex :'idChequeraReg'
		}, 
		{
			header :'BancoBenef',width :60,dataIndex :'idBancoInst'
		}, 
		{
			header :'Chequera Benef',width :60,dataIndex :'idChequeraInst'
		}, 
		],
		sm: NS.smBan,
		listeners:{
			/*dblclick:{
				fn:function(grid){
					var records = NS.gridDatos.getSelectionModel().getSelections();
					for(var i=0;i<records.length;i = i + 1)
					{
						NS.gridDatos.store.remove(records[i]);
						NS.gridDatos.getView().refresh();
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
					}
				}
			},*/
			click:function(grid){
				var records = NS.gridDatos.getSelectionModel().getSelections();
				
				if(records.length == 1){
					Ext.getCmp(PF + 'btnModificar').setDisabled(false);
				}else{
					Ext.getCmp(PF + 'btnModificar').setDisabled(true);
					Ext.getCmp(PF + 'btnAplicaModif').hide();
					Ext.getCmp(PF + 'btnAgregar').show();
				}
			}
		}
	});

	NS.storeDatos.load();
	/* GRID (T) */
	
	
	NS.subeDatos = function(){
		var seleccionados = NS.gridDatos.getSelectionModel().getSelections();

		NS.folioSeq = seleccionados[0].get('folioSeq');
		
		NS.modificar = true;
		

		Ext.getCmp(PF + 'btnAplicaModif').show();
		Ext.getCmp(PF + 'btnAgregar').hide();

		Ext.getCmp(PF + 'txtNoEmpresa').setValue(seleccionados[0].get('noEmpresa'));
		
		BFwrk.Util.updateTextFieldToCombo(PF+'txtNoEmpresa',PF + 'cmbEmpresa' );
		
		

//		BFwrk.Util.updateComboToTextField(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
	 	
		NS.limpiaCmbContrastos();
	 	NS.storeContrato.removeAll();
	 	NS.storeContrato.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
		NS.storeContrato.load();
		

		Ext.getCmp(PF + 'txtImporteInvertir').setValue(seleccionados[0].get('importe'));
		Ext.getCmp(PF + 'txtTasa').setValue(seleccionados[0].get('tasa'));
		Ext.getCmp(PF + 'txtPlazo').setValue(seleccionados[0].get('diasInvertir'));
//		Ext.getCmp(PF + 'txtImporteInvertir').setValue("importe");

	}
	
   	NS.OrdenDeInversion = new Ext.form.FormPanel({
    title: 'Orden de Inversión',
    width: 1020,
    height: 700,
    padding: 10,
    layout: 'absolute',
    id: PF + 'Orden de Inversion',
    name: PF + 'Orden de Inversion',
    renderTo : NS.tabContId,
    frame: true,
    autoScroll: true,
         items : [
                  {
	                xtype: 'fieldset',
	                title: '',
	                width: 1010,
	                height: 930,
	                x: 20,
	                y: 5,
	                layout: 'absolute',
	                id: 'framePrinOrdenInversion',
	                items: [
	                    {
                            xtype: 'textfield',
                            x: 60,
                            y: 0,
                            name: 'txtPF',
                            width: 320,
                            id: 'txtPF',
                            value: PF,
                       		readOnly: true,
                       		hidden: true
	                    },
	                    {
                            xtype: 'textfield',
                            x: 60,
                            y: 0,
                            name: PF + 'txtInstucion',
                            width: 320,
                            id: PF + 'txtInstucion',
                            value: PF,
                       		readOnly: true,
                       		hidden: true
	                    },
	                    {
                            xtype: 'textfield',
                            x: 60,
                            y: 0,
                            name: PF + 'hdFormaPago',
                            width: 320,
                            id: PF + 'hdFormaPago',
                            value: PF,
                       		readOnly: true,
                       		hidden: true
	                    },
	                    {
                            xtype: 'textfield',
                            x: 60,
                            y: 0,
                            name: PF + 'hdPlazoInv',
                            width: 320,
                            id: PF + 'hdPlazoInv',
                            value: PF,
                       		readOnly: true,
                       		hidden: true
	                    },
                        {
                            xtype: 'label',
                            text: 'Empresa:',
                            x: 10,
                            y: 5
                        },
                        NS.txtNoEmpresa,
                        NS.cmbEmpresa,
                        {
                            xtype: 'textfield',
                            x: 60,
                            y: 0,
                            name: 'txtNomEmpresa',
                            width: 320,
                            id: PF + 'txtNomEmpresa',
                            value: apps.SET.NOM_EMPRESA,
                       		readOnly: true,
                       		hidden: true
                        },
                        {
	                        xtype: 'fieldset',
	                        title: 'Contratos',
	                        x: 0,
	                        y: 40,
	                        height: 180,
	                        layout: 'absolute',
	                        width: 985,
	                        id: PF + 'frameContratos',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Clave Contrato:',
	                                x: 10,
	                                y: 0
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 10,
	                                y: 15,
	                                width: 100,
	                                name: PF + 'txtCveContrato',
	                                id: PF + 'txtCveContrato',
	                        	    listeners:{
	                        	    	change:{
	                        	    		fn: function(caja, valor){
	                        	    			BFwrk.Util.updateTextFieldToCombo(PF+'txtCveContrato',PF + 'cmbNumContrato' );
//	                        	    			Ext.getCmp(PF + 'txtCveContrato').setValue(valor);
	                        	    			NS.accionarNumContrato(Ext.getCmp(PF + 'cmbNumContrato').getValue());
	                        	    		}
	                        	    	}
	                        	    }
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Núm Contrato:',
	                                x: 130,
	                                y: 0
	                            },
	                            NS.cmbNumContrato,
	                            {
	                                xtype: 'label',
	                                text: 'Descripción:',
	                                x: 10,
	                                y: 50
	                            },
	                           	NS.cmbNomContrato,
	                            {
	                                xtype: 'textfield',
	                                x: 270,
	                                y: 65,
	                                width: 210,
	                                name: PF + 'txtNomInstitucion',
	                                id: PF + 'txtNomInstitucion',
	                                readOnly: true
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 510,
	                                y: 65,
	                                width: 60,
	                                name: PF + 'txtNumContacto',
	                                id: PF + 'txtNumContacto',
	                                hidden:true,
	                                listeners:{
			                        	change:{
			                        		fn: function(caja, valor){
			                        			if(caja.getValue() !== null && caja.getValue() !== undefined && caja.getValue() !== '')
			               							var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtNumContacto',NS.cmbContactos.getId());
			               						//if(valueCombo != null && valueCombo != undefined && valueCombo != '')
			               							//NS.accionarDivisa(valueCombo);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbContactos,
	                            {
	                                xtype: 'textfield',
	                                x: 510,
	                                y: 65,
	                                width: 50,
	                                id: PF + 'txtTipoValor',
	                                name: PF + 'txtTipoValor',
	                                listeners:{
			                        	change:{
			                        		fn: function(caja, valor){
			                        			if(caja.getValue() !== null && caja.getValue() !== undefined && caja.getValue() !== '')
			               							var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtTipoValor',NS.cmbTipoValor.getId());
			               						//if(valueCombo != null && valueCombo != undefined && valueCombo != '')
			               							//NS.accionarDivisa(valueCombo);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbTipoValor,
	                            {
	                                xtype: 'textfield',
	                                x: 10,
	                                y: 115,
	                                width: 60,
	                                id: PF + 'txtIdDivisa',
	                                name: PF + 'txtIdDivisa',
	                                readOnly: true
	                            },
	                            NS.cmbPapel,
	                            {
	                                xtype: 'textfield',
	                                x: 250,
	                                y: 115,
	                                width: 90,
	                               /* minValue: '0:00',
								    maxValue: '23:00',
								    increment: 01,*/
	                                name: PF + 'cmbHora',
	                                id: PF + 'cmbHora',
	                                readonly:true,
	                                listeners:{
	                                	change:{
	                                		fn: function(e){
	                            				NS.obtenerHora();
	                                		}
	                                	}
	                                }
	                            },
	                            {
			                        xtype: 'checkbox',
			                        x: 370,
			                        y: 110,
			                        boxLabel: 'Garantia',
			                        name: PF + 'chkGarantia',
			                        id: PF + 'chkGarantia',
			                        hidden: false,
			                        listeners:{
			                        	check:{
			                        		fn: function(check, valor){
			                        			
			                        		}
			                        	}
			                        }
			                    },
	                            /*{
	                                xtype: 'combo',
	                                x: 480,
	                                y: 120,
	                                width: 60,
	                                name: 'cmbMinuto',
	                                id: 'cmbMinuto',
	                                hidden: true
	                            },
	                            {
	                                xtype: 'combo',
	                                x: 540,
	                                y: 120,
	                                width: 60,
	                                name: 'cmbPMAM',
	                                id: 'cmbPMAM',
	                                hidden: true
	                            },*/
	                            {
	                                xtype: 'label',
	                                text: 'Institución:',
	                                x: 270,
	                                y: 50
	                            },
	                            /*{
	                                xtype: 'label',
	                                text: 'Contacto:',
	                                x: 510,
	                                y: 50
	                            },*/
	                            {
	                                xtype: 'label',
	                                text: 'Tipo Valor:',
	                                x: 510,
	                                y: 50
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Divisa:',
	                                x: 10,
	                                y: 100
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Hora:',
	                                x: 250,
	                                y: 100
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Papel:',
	                                x: 100,
	                                y: 100
	                            }
	                        ]
	                    },
	                    {
                        xtype: 'fieldset',
                        title: 'Datos bancarios',
                        x: 0,
                        y: 225,
                        height: 150,
                        layout: 'absolute',
                        width: 985,
                        id: PF + 'frameBancarios',
                        items: [

	                        {
	                            xtype: 'label',
	                            text: 'Banco de inversión:',
	                            x: 0,
	                            y: 0
	                        },
		                    {
		                        xtype: 'textfield',
		                        id: PF+'txtBancoInv',
	                            name: PF+'txtBancoInv',
	                            readOnly:true,
		                        x: 0,
		                        y: 25,
		                        width: 60,
		                        listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoInv',NS.cmbBancoInv.getId());
	//	                        			NS.accionarCmbBanco(comboValor);
	//	                        			NS.llenaChequerasInv();
		                        		}
		                        	}
		                        }
		                    },
		                    NS.cmbBancoInv,
	                        {
	                            xtype: 'label',
	                            text: 'Banco regreso inversión:',
	                            x: 270,
	                            y: 0
	                        },
		                    {
		                        xtype: 'textfield',
		                        id: PF+'txtBancoReg',
	                            name: PF+'txtBancoReg',
	                            readOnly:true,
		                        x: 270,
		                        y: 25,
		                        width: 60,
		                        listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoInv',NS.cmbBancoReg.getId());
		                        			NS.accionarCmbBanco(comboValor);
		                        		}
		                        	}
		                        }
		                    },
		                    NS.cmbBancoReg,
	                        {
	                            xtype: 'label',
	                            text: 'Banco de institución bancaria:',
	                            x: 540,
	                            y: 0
	                        },
		                    {
		                        xtype: 'textfield',
		                        id: PF+'txtBancoInstitucion',
	                            name: PF+'txtBancoInstitucion',
	                            readOnly:true,
		                        x: 540,
		                        y: 25,
		                        width: 60,
		                        listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoInstitucion',NS.cmbBancoInstitucion.getId());
	//	                        			NS.accionarCmbBanco(comboValor);
		                        		}
		                        	}
		                        }
		                    },
	                        NS.cmbBancoInstitucion,
	                        {
	                            xtype: 'label',
	                            text: 'Chequera:',
	                            x: 0,
	                            y: 50
	                        },
	                        NS.cmbChequeraInv,
	                        {
	                            xtype: 'label',
	                            text: 'Chequera:',
	                            x: 270,
	                            y: 50
	                        },
	                        NS.cmbChequeraReg,
	                        {
	                            xtype: 'label',
	                            text: 'Chequera:',
	                            x: 540,
	                            y: 50
	                        },
	                        NS.cmbChequeraInst
	                    ]},
	                    {
	                        xtype: 'fieldset',
	                        title: 'Captura de Inversiones',
	                        y: 385,
	                        width: 985,
	                        height: 150,
	                        layout: 'absolute',
	                        id:  PF + 'frameInversionHoy',
	                        items: [
	                            {
	                                xtype: 'textfield',
	                                x: 10,
	                                y: 15,
	                                width: 150,
	                                name: PF + 'txtImporteInvertir',
	                                id: PF + 'txtImporteInvertir',
	                                listeners:{
	                                	change:{
	                                		fn: function(caja, valor){
	                                			Ext.getCmp(PF + 'txtImporteInvertir').setValue(BFwrk.Util.formatNumber(caja.getValue()));
	                                			if(NS.validarAccionIntImp())
	                                				NS.accionarInteresImpuesto();
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 200,
	                                y: 15,
	                                width: 60,
	                                name: PF + 'txtTasa',
	                                id: PF + 'txtTasa',
	                                listeners:{
	                                	change:{
	                                		fn: function(caja, valor){
	                                			Ext.getCmp(PF + 'txtTasa').setValue(caja.getValue());
	                                			if(NS.validarAccionIntImp())
	                                				NS.accionarInteresImpuesto();
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'numberfield',
	                                x: 300,
	                                y: 15,
	                                width: 60,
	                                name: PF + 'txtPlazo',
	                                id: PF + 'txtPlazo',
	                                listeners: {
	                                	change:{
	                                		fn: function(caja, valor){
	                                			if(NS.validarAccionIntImp())
	                                				NS.accionarInteresImpuesto();
	                                			
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 400,
	                                y: 15,
	                                width: 160,
	                                name: PF + 'txtImpuesto',
	                                id: PF + 'txtImpuesto',
	                                readOnly : true
	                            },
	                            {
	                                xtype: 'label',
	                                text: '__________________________________________________________________________________________________________________________________________________',
	                                x: 10,
	                                y: 35,
	                                width: 180
	                            },
	                            {
	                                xtype: 'datefield',
	                                format:'d/m/Y',
	                                renderer:Ext.util.Format.dateRenderer('d-m-Y'), 
	                                x: 600,
	                                y: 15,
	                                width: 110,
	                                name: PF + 'txtFechaInversion',
	                                id: PF + 'txtFechaInversion',
	                                value: apps.SET.FEC_HOY,
	                                readOnly : false,
	                                listeners: {
	                            		change: {
	                            			fn: function(caja, valor){
	                            				 var fechaAlta = Ext.getCmp(PF + 'txtFechaInversion').getValue().format('d/m/Y');
	                            				 var fechaValida = true;
	                            				 
	                            				 InversionesAction.verificarFechaInhabil(fechaAlta, function(res, e){
	                            						if(res !== null && res !== undefined && res !== '' && res === true){
	                            							Ext.Msg.alert('SET','La fecha ingresada es inhábil');

	                            							Ext.getCmp(PF + 'txtFechaInversion').setValue(apps.SET.FEC_HOY);
	                            							fechaValida = false;
	                            							
	                            							return;
	                            						}
	                            				 });
	                            				 InversionesAction.verificarFechaMayor(fechaAlta, apps.SET.FEC_HOY, function(res, e){
	                            						if(res !== null && res !== undefined && res !== '' && res === true){
	                            							Ext.Msg.alert('SET','La no puede ser mayor al día de opreación');
	                            							Ext.getCmp(PF + 'txtFechaInversion').setValue(apps.SET.FEC_HOY);
	                            							fechaValida = false;
	                            							return;
	                            						}
	                            				 });

	                                			if(fechaValida && NS.validarAccionIntImp())
	                                				NS.accionarInteresImpuesto();
	                            			}
	                            		},select: {
	                            			fn: function(caja, valor){
	                            				 var fechaAlta = Ext.getCmp(PF + 'txtFechaInversion').getValue().format('d/m/Y');
	                            				 var fechaValida = true;
	                            				 
	                            				 InversionesAction.verificarFechaInhabil(fechaAlta, function(res, e){
	                            						if(res !== null && res !== undefined && res !== '' && res === true){
	                            							Ext.Msg.alert('SET','La fecha ingresada es inhábil');

	                            							Ext.getCmp(PF + 'txtFechaInversion').setValue(apps.SET.FEC_HOY);
	                            							fechaValida = false;
	                            							
	                            							return;
	                            						}
	                            				 });
	                            				 InversionesAction.verificarFechaMayor(fechaAlta, apps.SET.FEC_HOY, function(res, e){
	                            						if(res !== null && res !== undefined && res !== '' && res === true){
	                            							Ext.Msg.alert('SET','La no puede ser mayor al día de opreación');
	                            							Ext.getCmp(PF + 'txtFechaInversion').setValue(apps.SET.FEC_HOY);
	                            							fechaValida = false;
	                            							return;
	                            						}
	                            				 });

	                                			if(fechaValida && NS.validarAccionIntImp())
	                                				NS.accionarInteresImpuesto();
	                            			}
	                            		}
	                            	}
	                            },
	                            {
	                                xtype: 'textfield',
	                                //format:'d/m/Y',
	                                //renderer:Ext.util.Format.dateRenderer('d-m-Y'),
	                                x: 750,
	                                y: 15,
	                                width: 130,
	                                name: PF + 'txtFechaVencimiento',
	                                id: PF + 'txtFechaVencimiento',
	                                readOnly : true
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 10,
	                                y: 75,
	                                width: 150,
	                                name: PF + 'txtInteres',
	                                id: PF + 'txtInteres',
	                                readOnly : true
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 200,
	                                y: 75,
	                                width: 160,
	                                name: PF + 'txtImpuestoEsperado',
	                                id: PF + 'txtImpuestoEsperado',
	                                readOnly : true
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Importe a Invertir:',
	                                x: 10,
	                                y: 0
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Tasa:',
	                                x: 200,
	                                y: 0
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Plazo:',
	                                x: 300,
	                                y: 0
	                            },
	                            {
	                                xtype: 'label',
	                                text: ' Factor Impuesto:',
	                                x: 400,
	                                y: 0
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Fecha de Inversión:',
	                                x: 600,
	                                y: 0
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Fecha de Vencimiento:',
	                                x: 750,
	                                y: 0
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Interes:',
	                                x: 10,
	                                y: 60
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Impuesto Esperado:',
	                                x: 200,
	                                y: 60
	                            },
	                            {
	                                xtype: 'button',
	                                text: 'Saldo de Inversión...',
	                                x: 750,
	                                y: 75,
	                                width: 80,
	                                id: PF + 'btnSaldoInversion',
	                                name: PF + 'btnSaldoInversion',
	                                disabled: true,
	                                hidden: true,
	                                listeners:{
	                                	click:{
	                                		fn: function(e){
	                                		    Ext.getCmp(PF + 'txtIdEmpresa').setValue(apps.SET.NO_EMPRESA);
	                                		    BFwrk.Util.updateTextFieldToCombo(PF+'txtIdEmpresa',NS.cmbEmpresaCon.getId());
	                                			NS.winConsultaPosInve.show();
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Neto:',
	                                x: 400,
	                                y: 60
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 400,
	                                y: 75,
	                                width: 160,
	                                name: PF + 'txtNeto',
	                                id: PF + 'txtNeto',
	                                readOnly : true
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Agregar',
	                        x: 790,
	                        y: 550,
	                        width: 80,
	                        id: PF + 'btnAgregar',
	                        name: PF + 'btnAgregar',
	                        disabled:false,
	                        listeners:{
	                        	click:{
	                        		fn: function(e){
	                           			var iInstitucion = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'noInstitucion');
	                           			var plazoInv = BFwrk.Util.scanFieldsStore(NS.storeContrato, 'noCuenta',Ext.getCmp(PF + 'cmbNumContrato').getValue(),'plazoInv');
	                           			Ext.getCmp(PF + 'txtInstucion').setValue(iInstitucion);
	                           			Ext.getCmp(PF + 'hdPlazoInv').setValue(plazoInv);	                           			
	                           			
	                           			if(Ext.getCmp(PF + 'txtTasa').getValue() > 99 || Ext.getCmp(PF + 'txtTasa').getValue() <= 0){
	                        				BFwrk.Util.msgShow('El valor de la tasa debe ser menor a 100...','INFO');
	                        				return;
	                        			}
	                        			
	                        			if(Ext.getCmp(PF + 'cmbTipoValor').getValue() === 0){
	                        				BFwrk.Util.msgShow('Necesita establecer el tipo de valor...','INFO');
	                        				return;
	                        			}
	                        			
	                        			if(NS.validarDatos() === false)
                        					return;

	                        			NS.OrdenDeInversion.getForm().submit({ 
	                        	    	    method:'POST', 
	                        	    	    waitTitle:'Conectando', 
	                        	    	    waitMsg:'cargando...',

	                        	    	    success:function(form, action){
	                    	    	    		Ext.MessageBox.alert('SET', "Se agregó de manera correcta");
	                    	    	    		mascara.show();
	                    	    	    		NS.storeDatos.load();
	                    	    	    		return;
	                        	    	    },

	                        	    	    failure : function(form, action){
	                        	    	    	Ext.MessageBox.alert('SET', "Ocurrió un error"); 
	                        	    	    }  
	                        	    	}); 
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Ejecutar Modificación',
	                        x: 790,
	                        y: 550,
	                        width: 80,
	                        id: PF + 'btnAplicaModif',
	                        name: PF + 'btnAplicaModif',
	                        hidden:true,
	                        listeners:{
	                        	click:{
	                        		fn: function(e){
	                        			
//	                        			if(Ext.getCmp(PF + 'txtFechaVencimiento').getValue() === '')
//	                        			{
//	                        				BFwrk.Util.msgShow('!Falta indicar la fecha de vencimiento¡','INFO');
//	                        				return false;
//	                        			}
//	                        			if(Ext.getCmp(PF + 'txtInteres').getValue() === '' || Ext.getCmp(PF + 'txtInteres').getValue() === 0)
//	                        			{
//	                        				BFwrk.Util.msgShow('!Falta indicar el interés¡','INFO');
//	                        				return false;
//	                        			}
//	                        			if((sIsr === '1') && (Ext.getCmp(PF + 'txtImpuestoEsperado').getValue() === '' || Ext.getCmp(PF + 'txtImpuestoEsperado').getValue() === 0))
//	                        			{
//	                        				BFwrk.Util.msgShow('!Falta indicar el impuesto esperado¡','INFO');
//	                        				return false;
//	                        			}
//	                        			if(Ext.getCmp(PF + 'txtNeto').getValue() === '' || Ext.getCmp(PF + 'txtNeto').getValue() === 0)

	                        			var factorImpuesto = Ext.getCmp(PF+ 'txtImpuesto').getValue();
	                        			var fechaVencimiento = Ext.getCmp(PF + 'txtFechaVencimiento').getValue();
	                        			var fechaAlta = Ext.getCmp(PF + 'txtFechaInversion').getValue().format('d/m/Y');
	                        			var interes = Ext.getCmp(PF + 'txtInteres').getValue();
	                        			var impuestoEsperado = Ext.getCmp(PF + 'txtImpuestoEsperado').getValue();
	                        			var neto = Ext.getCmp(PF + 'txtNeto').getValue();
	                        			
	                        			if(factorImpuesto == '' ||
	                        					fechaVencimiento == '' ||
	                        					interes == '' ||
	                        					interes == 0,
	                        					impuestoEsperado == '' ||
	                        					neto == ''){
	                        				BFwrk.Util.msgShow('Es necesario modificar Importe a invertir, tasa o plazo','INFO');
	                        				return;
	                        			}
	                        			
	                        			var arrayDatos = {};
	                        			
//	                        			dtoOrd.setFolioSeq(funciones.convertirCadenaInteger(gDatos.get(0).get("folioSeq").toString()));
//
//	                        			dtoOrd.setImporte(funciones.convertirCadenaDouble(gDatos.get(0).get("importe").toString()));
//	                        			dtoOrd.setTasa(funciones.convertirCadenaDouble(gDatos.get(0).get("tasa").toString()));
//	                        			dtoOrd.setFactorImpuesto(funciones.convertirCadenaDouble(gDatos.get(0).get("impuesto").toString()));
//	                        			dtoOrd.setInteres(funciones.convertirCadenaDouble(gDatos.get(0).get("interes").toString()));
//	                        			dtoOrd.setImpuestoEsperado(funciones.convertirCadenaDouble(gDatos.get(0).get("impuestoEsperado").toString()));
//	                        			dtoOrd.setFecVenc(funciones.ponerFechaDate(gDatos.get(0).get("fechaVence").toString()));

	                        			
	                        			arrayDatos.folioSeq = NS.folioSeq;
	                        			arrayDatos.importe = Ext.getCmp(PF+ 'txtImporteInvertir').getValue();
	                        			arrayDatos.tasa = Ext.getCmp(PF+ 'txtTasa').getValue();
	                        			arrayDatos.impuestoEsperado = impuestoEsperado;
	                        			arrayDatos.interes = interes;
	                        			arrayDatos.impuesto = factorImpuesto;
	                        			arrayDatos.factorImpuesto = factorImpuesto;
	                        			arrayDatos.fechaVence = fechaVencimiento;
	                        			arrayDatos.fechaAlta = fechaAlta;

	                        			var matrizDatos = new Array();
	                        			
	                        			matrizDatos[0] = arrayDatos;
	                        			var jsonDatos = Ext.util.JSON.encode(matrizDatos);

	                        			InversionesAction.actualizarTmpInvSipo(jsonDatos, function(result, e){
							        		BFwrk.Util.msgShow('' + result.msgUsuario,'INFO');
											Ext.getCmp(PF + 'btnModificar').setDisabled(true);
											Ext.getCmp(PF + 'btnAplicaModif').hide();
											Ext.getCmp(PF + 'btnAgregar').show();
							        		NS.storeDatos.load();
							        	});
	                        			return;
	                        		}
	                        	}
	                        }
	                    },
	                    NS.gridDatos,
	                    {
	                        xtype: 'button',
	                        text: 'Aceptar',
	                        x: 590,
	                        y: 855,
	                        width: 80,
	                        id:PF + 'btnAceptar',
	                        name: PF + 'btnAceptar',
	                        listeners:{
	                        	click:{
	                        		fn: function(e){
	                        			
	                        			 var matriz= new Array();
				             			    for (var i = 0; i < NS.storeDatos.data.length; i++) {
				             			     var record=NS.storeDatos.getAt(i);
				             			     var registros = {};
					             			     
				             			    registros.id= record.data.id;
				             			   registros.noEmpresa= record.data.noEmpresa;
				             			   registros.folioSeq= record.data.folioSeq;
				             			   registros.hora= record.data.hora;
				             			   registros.minuto= record.data.minuto;
				             			   registros.contraparte= record.data.contraparte;
				             			   registros.importe= record.data.importe;
				             			   registros.instrumento= record.data.instrumento;
				             			   registros.emisionDe= record.data.emisionDe;
				             			   registros.diasInvertir= record.data.diasInvertir;
				             			   registros.sFechaValor= record.data.sFechaValor;
				             			   registros.tasa= record.data.tasa;
				             			   registros.origen= record.data.origen;
				             			   registros.estatus= record.data.estatus;
				             			   registros.contrato= record.data.contrato;
				             			   registros.plazo= record.data.plazo;
				             			   registros.garantia= record.data.garantia;
				             			   registros.idPapel= record.data.idPapel;
				             			   registros.idTipoValor= record.data.idTipoValor;
				             			   registros.cveContrato= record.data.cveContrato;
				             			   registros.noContacto= record.data.noContacto;
				             			   registros.sFecVenc= record.data.sFecVenc;
				             			   registros.impuestoEsperado= record.data.impuestoEsperado;
				             			   registros.noInstitucion= record.data.noInstitucion;
				             			   registros.plazo= record.data.plazo;
				             			   registros.factorImpuesto= record.data.factorImpuesto;
				             			   registros.interes= record.data.interes;
				             			   registros.neto= record.data.neto;
				             			   registros.moneda= record.data.moneda;
				             			   registros.institucion= record.data.institucion;
				             			   registros.grupo= record.data.grupo;
				             			   registros.rubro= record.data.rubro;
				             			   registros.idBanco= record.data.idBanco;
				             			   registros.idChequera= record.data.idChequera;
				             			   registros.idBancoReg= record.data.idBancoReg;
				             			   registros.idChequeraReg= record.data.idChequeraReg;
				             			   registros.idBancoInst= record.data.idBancoInst;
				             			   registros.idChequeraInst= record.data.idChequeraInst;
					             			   matriz[i] = registros;
					             			 }
				             			     var jsonString= Ext.util.JSON.encode(matriz);
	                        			
	                        		//	alert(jsonString);
	                        		//	alert(NS.gridDatos.getSelectionModel().getSelections()+"2");
	                        			var seleccionados= NS.gridDatos.getSelectionModel().getSelections();
	                        			var cadena = "";
	                					for(var i = 0; i < seleccionados.length; i = i + 1){
	                						cadena += seleccionados[i].get('folioSeq') + "|";
	                					}
	                        			mascara.show();
	                        			//alert(cadena);
	                					InversionesAction.insertarOrdenesInversion(cadena, function(result, e){
							        		BFwrk.Util.msgShow('' + result.msgUsuario,'INFO');
							        		NS.storeDatos.load();
							        	});
	                        			return;
	                        			
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Modificar',
	                        x: 680,
	                        y: 855,
	                        width: 80,
	                        id: PF + 'btnModificar',
	                        name: PF + 'btnModificar',
	                        disabled:true,
	                        listeners:{
	                        	click:{
	                        		fn: function(e){
	                        			NS.subeDatos();
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Eliminar',
	                        x: 770,
	                        y: 855,
	                        width: 80,
	                        id: PF + 'btnEliminar',
	                        name: PF + 'btnEliminar',
	                        listeners:{
	                        	click:{
	                        		fn: function(e){
	                        			var seleccionados = NS.gridDatos.getSelectionModel().getSelections();
	                        			var cadena = "";
	                					for(var i = 0; i < seleccionados.length; i = i + 1){
	                						cadena += seleccionados[i].get('folioSeq') + "|";
	                					}
	                        			mascara.show();
	                					InversionesAction.eliminarTmpInvSipo(cadena, function(result, e){
							        		BFwrk.Util.msgShow('' + result.msgUsuario,'INFO');
							        		NS.storeDatos.load();
							        	});
	                        			return;
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 860,
	                        y: 855,
	                        width: 80,
	                        id: PF + 'btnLimpiar',
	                        name: PF + 'btnLimpiar',
	                        listeners:{
	                        	click:{
	                        		fn: function(e){
	                        			NS.limpiar();
	                        		}
	                        	}
	                        }
	                    }
	                ]
	            }
	        ],
	        api: {
	            submit: InversionesAction.insertarTmpInvSipo
	        }
	});
	 NS.OrdenDeInversion.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});