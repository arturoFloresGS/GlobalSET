//@autor Cristian Garcia Garcia
//22/Feb/2011
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Egresos.CapturaSolicitudesdePago');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.idPersona=0;
	NS.nomPersona='';
	NS.idEmpresa=0;
	NS.numPartida=0;//contador de el numero de partidas
	NS.rubro=0;
	NS.descRubro='';
	NS.importe=0;
	NS.grupo=0;
	NS.idPolizaParametro = 4;
	NS.idGrupoParametro = 0;
	NS.idRubroParametro = 0;
	NS.rubroActual=0;
	NS.depto=0;
	NS.area=0;
	NS.idPlantillaParametro = 0;
	NS.seleccionaPrimero = true;
	
	NS.noDocto=0;
	NS.chkTodos='false';
	NS.chkProvUnico='false';	
	NS.idFormaPago=0;
	NS.fechaFactura='';
	NS.fechaPago='';
	NS.importeOriginal=0;
	NS.idDivOriginal='MN';
	NS.chkContabilizar='false';
	NS.concepto='';
	NS.idDivPago='MN';
	NS.importePago=0;
	NS.optNac='false';
	NS.optExt='false';
	NS.idBancoBenef=0;
	NS.idChequera='';
	NS.clabe=0;
	NS.sucursal=0;
	NS.plaza=0
	NS.idCaja=0;
	NS.chkLeyenda = 'true';
	NS.solicita='';
	NS.autoriza='';
	NS.observaciones='';
	NS.parametroBus='';
	
	NS.noEmpresaTmp = '';
	NS.nomEmpresaTmp = '';
	NS.idBanco=0;
	//NS.tabContId = 'contenedorPruebas';
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	NS.limpiar= function(){
		
		Ext.getCmp(PF+'fechaPago').setValue(apps.SET.FEC_HOY);
		Ext.getCmp(PF+'fechaFactura').setValue(apps.SET.FEC_HOY);
		NS.idUsuario=apps.SET.iUserId;
		NS.idPersona=0;
		NS.nomPersona='';
		NS.idEmpresa=0;
		
		NS.numPartida=0;//contador de el numero de partidas
		NS.rubro=0;
		NS.descRubro='';
		NS.importe=0;
		NS.grupo=0;
		NS.depto=0;
		NS.area=0;
		
		NS.noDocto=0;
		NS.chkTodos='false';
		NS.chkProvUnico='false';	
		NS.idFormaPago=0;
		NS.fechaFactura='';
		NS.fechaPago='';
		NS.importeOriginal=0;
		NS.idDivOriginal='MN';
		NS.chkContabilizar='false';
		NS.concepto='';
		NS.idDivPago='MN';
		NS.importePago=0;
		NS.optNac='false';
		NS.optExt='false';
		NS.idBancoBenef=0;
		NS.idChequera='';
		NS.clabe=0;
		NS.sucursal=0;
		NS.plaza=0
		NS.idCaja=0;
		NS.chkLeyenda = 'true';
		NS.solicita='';
		NS.autoriza='';
		NS.observaciones='';
		
		NS.cmbGrupo.reset(); 
		NS.cmbRubro.reset();
		NS.cmbAreaDet.reset();
		NS.cmbDivision.reset();
//VT	NS.cmbEmpresas.reset();
		
//		NS.cmbDivisaOriginal.reset();
		NS.cmbDivisaOriginal.setValue('PESOS');
//		NS.cmbDivisaPago.reset();
		NS.cmbDivisaPago.setValue('PESOS');
		NS.cmbFormaPago.reset();
		NS.cmbCajaUsuario.reset();
		NS.cmbAreaDestino.reset();
		NS.cmbBeneficiario.reset();
		NS.storeBeneficiario.removeAll();
		NS.cmbBancoBenef.reset();
		NS.cmbChequeras.reset();
		//NS.gridDatos.store.removeAll();
		//NS.gridDatos.getView().refresh();
		NS.CapturaSolicitudesPago.getForm().reset();
		//NS.obtieneSolicitante();
		Ext.getCmp(PF + 'txtEmpresa').setValue(NS.noEmpresaTmp);
		Ext.getCmp(PF + 'cmbEmpresas').setValue(NS.nomEmpresaTmp);
	}
	
	NS.habilitarComponentes = function(valorHabilitar, detalle){
		Ext.getCmp(PF + 'txtEmpresa').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'cmbEmpresas').setDisabled(valorHabilitar);
		Ext.getCmp(PF + 'txtNoDocto').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'txtIdBeneficiario').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'cmbBeneficiario').setDisabled(valorHabilitar);
		Ext.getCmp(PF + 'txtIdFormaPago').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'cmbFormaPago').setDisabled(valorHabilitar);
		Ext.getCmp(PF + 'fechaPago').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'txtImporteOriginal').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'txtIdDivisaOriginal').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'cmbDivisaOriginal').setDisabled(valorHabilitar);
		Ext.getCmp(PF + 'txtConcepto').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'fechaFactura').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'txtIdDivisaPago').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'cmbDivisaPago').setDisabled(valorHabilitar);
		Ext.getCmp(PF + 'txtTipoCambio').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'txtImportePago').setReadOnly(valorHabilitar);
		Ext.getCmp(PF + 'contTransferencia').setDisabled(valorHabilitar);
		
		Ext.getCmp(PF + 'txtImportePartida').setReadOnly(detalle);
		Ext.getCmp(PF + 'btnNuevo').setDisabled(detalle);
		Ext.getCmp(PF + 'txtIdGrupo').setReadOnly(detalle);//txtIdRubroAl
		Ext.getCmp(PF + 'txtIdRubroAl').setReadOnly(detalle);
		Ext.getCmp(PF + 'cmbGrupo').setDisabled(detalle);
		Ext.getCmp(PF + 'txtIdRubro').setReadOnly(detalle);
		Ext.getCmp(PF + 'cmbRubro').setDisabled(detalle);
		Ext.getCmp(PF + 'txtSumPartidas').setReadOnly(true);
		Ext.getCmp(PF + 'txtSumImporte').setReadOnly(true);
		Ext.getCmp(PF + 'txtSolicita').setReadOnly(detalle);
		Ext.getCmp(PF + 'txtAutoriza').setReadOnly(detalle);
		Ext.getCmp(PF + 'txtObservaciones').setReadOnly(detalle);
		Ext.getCmp(PF + 'cmbAreaDestino').setDisabled(detalle);
		Ext.getCmp(PF + 'btnEjecutar').setDisabled(detalle);
		Ext.getCmp(PF + 'txtIdDivision').setReadOnly(detalle);
		Ext.getCmp(PF + 'txtIdAreaDet').setReadOnly(detalle);
		Ext.getCmp(PF + 'btnEliminar').setDisabled(detalle);
		
	};
	
	//Store Datos donde se muestran los valores seleccionados
//	NS.storeDatos = new Ext.data.DirectStore({
//		paramsAsHash: false,
//		root: '',
//		idProperty: 'numPartida',//identificador del store
//		fields: [
//			{name: 'numPartida'},
//			{name: 'rubro'},
//			{name: 'descripcion'},
//			{name: 'importe'},
//			{name: 'departamento'},
//			{name: 'grupo'},
//			{name: 'area'}
//		],
//		listeners: {
//			load: function(s, records){
//		
//			}
//		}
//	}); 
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
	
	
	function cambiarFecha(fecha){
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

	//store  Beneficiario
    NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_banco',
			campoDos:'desc_banco',
			tabla:'cat_banco',
			condicion:'',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarCombo, 
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen bancos en la base de datos');
				}
			}
		}
	}); 
	
	

//storeEmpresas
    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idUsuario:NS.idUsuario
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
	
	NS.accionarEmpresas = function(valueCombo){
		//NS.storeDivision.baseParams.condicion='no_empresa ='+valueCombo+'';
		//NS.storeDivision.load();
		NS.storeBancoBenef.baseParams.noEmpresa = parseInt(valueCombo);
		NS.storeChequeras.baseParams.idEmpresa = parseInt(valueCombo);
		//NS.storeChequeras.baseParams.idDivisa = Ext.getCmp(PF + 'txtIdDivisaOriginal').getValue();
		NS.idEmpresa = valueCombo;
	}
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
        ,width: 260
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
					NS.seleccionaEmpresa();
				}
			}
		}
	});	
	//store Tipo cambio
	NS.storeTipoCambio = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'valor',
			campoDos:'id_divisa',
			tabla:'valor_divisa',
			condicion:"id_divisa = 'DLS' and fec_divisa = '" + apps.SET.FEC_HOY + "'",
			orden:''    //   NS.storeBancoBenef.baseParams.condicion=''+valueCombo;
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarCombo, 
		idProperty: 'valor', 
		fields: [
			 {name: 'valor'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen datos en la base de datos');
				}
			}
		}
	});
	
	//store Divisas
	
    NS.storeDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: CapturaSolicitudesPagoAction.obtenerDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
			 {name: 'idDivisaSoin'},
			 {name: 'clasificacion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisas, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen divisas');
				}
			}
		}
	}); 
	NS.storeDivisas.load();
	NS.storeBancos.load();

	NS.accionarDivisaOriginal = function(valueCombo){
		NS.storeBancoBenef.baseParams.idDivisa=''+valueCombo;
		NS.idDivOriginal=''+valueCombo;
	}
	//combo Divisa Original
	NS.cmbDivisaOriginal = new Ext.form.ComboBox({
		store: NS.storeDivisas
		,name: PF+'cmbDivisaOriginal'
		,id: PF+'cmbDivisaOriginal'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 540
        ,y: 115
        ,width: 160
        ,tabIndex: 10
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione divisa original'
		,triggerAction: 'all'
		,value: 'PESOS'
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
				 		BFwrk.Util.updateComboToTextField(PF+'txtIdDivisaOriginal', NS.cmbDivisaOriginal.getId());
						NS.accionarDivisaOriginal(combo.getValue());
						//NS.idDivOriginal=''+valueCombo;
						NS.idDivOriginal=''+combo.getValue()
						NS.storeTipoCambio.load();
				}
			}
		}
	});	
	
	
	NS.accionarDivisaPago = function(valueCombo) {
		NS.idDivPago = valueCombo;
		var importeOri = 1 * (NS.unformatNumber(Ext.getCmp(PF+'txtImporteOriginal').getValue()));
		
		CapturaSolicitudesPagoAction.obtenerCambioDivisa(NS.idDivOriginal, '' + valueCombo, importeOri, function(result, e) {						
			Ext.getCmp(PF + 'txtTipoCambio').setValue(result.tipoCambio);
			Ext.getCmp(PF + 'txtImportePago').setValue(NS.formatNumber(Math.round((result.importePago) * 100) / 100));
		});
	}
	
	//combo Divisa Pago
	NS.cmbDivisaPago = new Ext.form.ComboBox({
		store: NS.storeDivisas
		,name: PF+'cmbDivisaPago'
		,id: PF+'cmbDivisaPago'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 40
        ,y: 0
        ,width: 150
        ,tabIndex: 15
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione divisa pago'
		,triggerAction: 'all'
		,value: 'PESOS'
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						BFwrk.Util.updateComboToTextField(PF+'txtIdDivisaPago', NS.cmbDivisaPago.getId());
						NS.accionarDivisaPago(combo.getValue());
				}
			}
		}
	});	
	
	
    NS.storeFormaPago = new Ext.data.DirectStore({
			paramsAsHash: false,
			/*baseParams:{
				campoUno:'',
				campoDos:'',
				campoTres:''
			},*/
			root: '',
			//paramOrder:['campoUno','campoDos','campoTres'],
			directFn: CapturaSolicitudesPagoAction.obtenerFormaPago, 
			idProperty: 'idFormaPago', 
			fields: [
				 {name: 'idFormaPago'},
				 {name: 'descFormaPago'}
			],
			listeners: {
				load: function(s, records){
					
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFormaPago, msg:"Cargando..."});

				}
			}
		}); 
	NS.storeFormaPago.load();
	
	//Funcion para ser llamada en el select del combo como en el change de la caja de texto
	
	NS.mostrarFormaPago = function(valueCombo){
		NS.cmbBancoBenef.reset();
		NS.cmbChequeras.reset();
		NS.storeBancoBenef.removeAll();
		NS.storeChequeras.removeAll();
		Ext.getCmp(PF + 'txtIdBancoBenef').reset();
		Ext.getCmp(PF + 'txtClabe').reset();
		Ext.getCmp(PF + 'txtSucursal').reset();
		Ext.getCmp(PF + 'txtPlaza').reset();
		
		NS.idFormaPago=parseInt(valueCombo);
		if(valueCombo===1 || valueCombo===2){
			Ext.getCmp(PF+'contCheque').setVisible(true);
			Ext.getCmp(PF+'contTransferencia').setVisible(false);
		}
		else if(valueCombo===3){
			
			var noBeneficiario = Ext.getCmp(PF + 'txtIdBeneficiario').getValue();
			NS.accionarBeneficiarioPlantilla(noBeneficiario);
			NS.storeBancoBenef.load();
			Ext.getCmp(PF+'contTransferencia').setVisible(true);
			Ext.getCmp(PF+'contCheque').setVisible(false);
		}
		else{
			
			Ext.getCmp(PF+'contCheque').setVisible(false);
			Ext.getCmp(PF+'contTransferencia').setVisible(false);
		}
	}
	
	NS.mostrarFormaPagoPlantilla = function(valueCombo){
		//NS.cmbBancoBenef.reset();
		//NS.cmbChequeras.reset();
		//NS.storeChequeras.removeAll();
		//Ext.getCmp(PF + 'txtIdBancoBenef').reset();
		//Ext.getCmp(PF + 'txtClabe').reset();
		//Ext.getCmp(PF + 'txtSucursal').reset();
		//Ext.getCmp(PF + 'txtPlaza').reset();
		
		NS.idFormaPago=parseInt(valueCombo);
		if(valueCombo===1 || valueCombo===2)
		{
			Ext.getCmp(PF+'contCheque').setVisible(true);
			Ext.getCmp(PF+'contTransferencia').setVisible(false);
		}
		else if(valueCombo===3)
		{
			//NS.accionarBeneficiarioPlantilla(Ext.getCmp(PF + 'txtIdBeneficiario').getValue());
			NS.storeBancoBenef.load();
			Ext.getCmp(PF+'contTransferencia').setVisible(true);
			Ext.getCmp(PF+'contCheque').setVisible(false);
		}
		else
		{
			Ext.getCmp(PF+'contCheque').setVisible(false);
			Ext.getCmp(PF+'contTransferencia').setVisible(false);
		}
	}	
	
	//combo Forma Pago
	NS.cmbFormaPago = new Ext.form.ComboBox({
		store: NS.storeFormaPago
		,name: PF+'cmbFormaPago'
		,id: PF+'cmbFormaPago'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 50
        ,y: 115
        ,width: 180
        ,tabIndex: 6
		,valueField:'idFormaPago'
		,displayField:'descFormaPago'
		,autocomplete: true
		,emptyText: 'Seleccione forma de pago'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.seleccionaFormaDePago(combo.getValue());
				}
			}
		}
	});	
	

		//store Caja Usuario
	    NS.storeCajaUsuario = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idUsuario:NS.idUsuario
		},
		root: '',
		paramOrder:['idUsuario'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.obtenerCajaUsuario, 
		idProperty: 'idCaja', 
		fields: [
			 {name: 'idCaja'},
			 {name: 'desCaja'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCajaUsuario, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen cajas para el usuario');
				}
			}
		}
	}); 
	NS.storeCajaUsuario.load();
		//combo Forma Pago
		NS.cmbCajaUsuario = new Ext.form.ComboBox({
		store: NS.storeCajaUsuario
		,name: PF+'cmbCajaUsuario'
		,id: PF+'cmbCajaUsuario'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,width: 170
        ,x: 40
        ,y: 15
		,valueField:'idCaja'
		,displayField:'desCaja'
		,autocomplete: true
		,emptyText: 'Seleccione una caja'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						BFwrk.Util.updateComboToTextField(PF+'txtIdCaja',NS.cmbCajaUsuario.getId());
						NS.idCaja=combo.getValue();
				}
			}
		}
	});

	
	//store Area Destino
	    NS.storeAreaDestino = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_area',
			campoDos:'desc_area',
			tabla:'cat_area',
			condicion:'no_usuario ='+NS.idUsuario+'',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarComboAreaDestino, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAreaDestino, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen areas asignadas para el usuario');
				}
			}
		}
	}); 
	//NS.storeAreaDestino.load();
		//combo Forma Pago
		NS.cmbAreaDestino = new Ext.form.ComboBox({
		store: NS.storeAreaDestino
		,name: PF+'cmbAreaDestino'
		,id: PF+'cmbAreaDestino'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		, hidden: true
        ,x: 650
        ,y: 310
        ,width: 170
        ,indexTab: 34
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione area destino'
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
	
	//store AreaDet
	    NS.storeAreaDet = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_area',
			campoDos:'desc_area',
			tabla:'cat_area',
			condicion:'',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarCombo, 
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAreaDestino, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen areas asignadas para el usuario');
				}
			}
		}
	}); 
	//NS.storeAreaDet.load();
	
	NS.accionarAreaDet = function(valueCombo) {
		var recordsGridDatos= NS.storeDatos.data.items;
		var datosClase = NS.gridDatos.getStore().recordType;
		
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
			NS.area = valueCombo;
		}else {
			NS.area = 0;
			NS.cmbAreaDet.reset();
			Ext.getCmp(PF + 'txtIdAreaDet').reset();
		}
		var indice;
		
		for(var i=0; i<recordsGridDatos.length; i++) {
			if(recordsGridDatos[i].get('numPartida') == NS.numPartida) {
				NS.storeDatos.remove(recordsGridDatos[i]);
				
				var datos = new datosClase({
		          	numPartida: NS.numPartida,
					rubro: NS.rubro,
					descripcion: NS.descRubro,
					importe: NS.importe,
					departamento: NS.depto,
					grupo: NS.grupo,
					area: NS.area
		      	});
				NS.gridDatos.stopEditing();
		  		NS.storeDatos.insert(i, datos);
		  		NS.gridDatos.getView().refresh();
		  		NS.gridDatos.store.sort('numPartida','ASC');
			}
		}
	}
	
	
	//combo Area Det
	NS.cmbAreaDet = new Ext.form.ComboBox({
		store: NS.storeAreaDet
		,name: PF+'cmbAreaDet'
		,id: PF+'cmbAreaDet'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
	   	,x: 85
	    ,y: 215
	    ,width: 170
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione area '
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,disabled:true
		,listeners: {
			select: {
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdAreaDet', NS.cmbAreaDet.getId());
					
					if(combo.getValue() != null && combo.getValue() != '' && combo.getValue() != undefined)
						NS.accionarAreaDet(combo.getValue());
					else
						Ext.getCmp(PF + 'txtIdAreaDet').reset();
				}
			}
		}
	});	
	
	//store Division
	    NS.storeDivision = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_division',
			campoDos:'desc_division',
			tabla:'cat_division',
			condicion:'',
			orden:'desc_division'
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarCombo, 
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivision, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					//Ext.Msg.alert('SET','No Existen areas asignadas para el usuario');
				}
			}
		}
	}); 
	
		//combo Division
		NS.cmbDivision = new Ext.form.ComboBox({
		store: NS.storeDivision
		,name: PF+'cmbDivision'
		,id: PF+'cmbDivision'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
       	,x: 85
        ,y: 165
        ,width: 170
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione  division'
		,triggerAction: 'all'
		,value: ''
		,disabled:true
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
				Ext.getCmp(PF+'txtIdDivision').setValue(combo.getValue());
				NS.depto =Ext.getCmp(PF+'txtIdDivision').getValue();
			 	
				//NS.departamento=valor.getValue();
			  	var recordsGridDatos= NS.storeDatos.data.items;
			 	var datosClase = NS.gridDatos.getStore().recordType;
			 	var indice;
			 	
	  			for(var inc=0; inc<recordsGridDatos.length;inc=inc+1)
	  			{
	  				if(recordsGridDatos[inc].get('numPartida')===NS.numPartida)
	  				{
	  					indice=inc;
	  					NS.storeDatos.remove(recordsGridDatos[indice]);
	  				}
	  			}
					var datos = new datosClase({
	           	numPartida: NS.numPartida,
					rubro: NS.rubro,
					descripcion:NS.descRubro,
					importe:NS.importe,
					departamento:NS.depto,
					grupo:NS.grupo,
					area:NS.area
	       	});
	           			
	        NS.gridDatos.stopEditing();
	   		NS.storeDatos.insert(indice, datos);
	   		NS.gridDatos.getView().refresh();
	   		NS.gridDatos.store.sort('numPartida','ASC');
				}
			}
		}
	});	
	
	//store Grupo
	    NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams:{
		},
		//paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		directFn: CapturaSolicitudesPagoAction.obtenerGrupo, 
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupo, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					//Ext.Msg.alert('SET','No Existen areas asignadas para el usuario');
					
				}
			}
		}
	}); 
	NS.storeGrupo.load();
	
	
	NS.accionarGrupo = function(valueCombo){
		 NS.storeRubro.baseParams.condicion= 'id_grupo='+valueCombo;
		 NS.storeRubro.load(); 
		 NS.grupo=valueCombo;
 		 var recordsGridDatos= NS.storeDatos.data.items;
		 var datosClase = NS.gridDatos.getStore().recordType;
		 var indice;
                   			
		for(var inc=0; inc<recordsGridDatos.length;inc=inc+1)
		{
			if(recordsGridDatos[inc].get('numPartida')===NS.numPartida)
			{
				indice=inc;
				NS.storeDatos.remove(recordsGridDatos[indice]);
			}
		}
		var datos = new datosClase({
	       	numPartida: NS.numPartida,
			rubro: NS.rubro,
			descripcion:NS.descRubro,
			importe:NS.importe,
			departamento:NS.depto,
			grupo:NS.grupo,
			area:NS.area
       	});
                 			
        NS.gridDatos.stopEditing();
   		NS.storeDatos.insert(indice, datos);
   		NS.gridDatos.getView().refresh();
   		NS.gridDatos.store.sort('numPartida','ASC');
	}	

	
	//Este store se carga segun la clave tecleada en la caja de texto
	NS.storeUnicoBeneficiario = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'p.equivale_persona',
			campoDos:'',
			tabla:'persona p join BENEFICIARIOS_SOLICITUD_PAGO s on p.equivale_persona = s.beneficiario',
			condicion:'',
			orden:'',
			poliza:'',
			grupo:'',
			rubro:'',
			registroUnico:false,
			noEmpresa: 0
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','poliza','grupo','rubro','registroUnico','noEmpresa'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarComboBeneficiarioParametrizado, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0) {
					Ext.Msg.alert('SET','No Existen el beneficiario con esa clave');
					Ext.getCmp(PF + 'txtIdBeneficiario').setValue('');
					NS.cmbBeneficiario.reset();
					return;
				}else{
					var reg = NS.storeUnicoBeneficiario.data.items;
					Ext.getCmp(NS.cmbBeneficiario.getId()).setValue(reg[0].get('descripcion'));
					NS.accionarUnicoBeneficiario(reg[0].get('id'));
				}
				//NS.limpiaCamposBenef();
			}
		}
	}); 
	
	//Este combo carga los datos dependiendo la(s) letra(s) que se tecleen en el combo
	//store  Beneficiario 
    NS.storeBeneficiario = new Ext.data.DirectStore({
    //NS.storeBeneficiario = new Ext.ux.data.PagingDirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'p.equivale_persona',
			campoDos:'',
			tabla:'persona p ',
			condicion:'',
			orden:'',
			poliza:'',
			grupo:'',
			rubro:'',
			registroUnico:false,
			noEmpresa:0
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','poliza','grupo','rubro','registroUnico','noEmpresa'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarComboBeneficiarioParametrizado, 
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0) {
					Ext.Msg.alert('SET','No Existen Beneficiarios');
					Ext.getCmp(PF + 'txtIdBeneficiario').setValue('');
					NS.cmbBeneficiario.reset();
				}else {
					
					//Ext.Msg.alert('SET','No Existen Beneficiarios con ese nombre false');
					//NS.cmbBeneficiario.setValue(records[0].get('descripcion'));
					//Ext.getCmp(PF+'txtIdBeneficiario').setValue(records[0].get('id'));
					NS.idPersona=Ext.getCmp(PF+'txtIdBeneficiario').getValue();
					NS.nomPersona=records[0].get('descripcion');
					//NS.accionarUnicoBeneficiario(reg[0].get('id'));
				}
				//NS.limpiaCamposBenef();
			}
		}
	}); 
	
	//Esta funcion es para el storeUnicoBeneficiario
	NS.accionarUnicoBeneficiario = function(valueCombo){
		NS.storeBancoBenef.baseParams.idPersona = parseInt(valueCombo);
		NS.storeChequeras.baseParams.idPersona = parseInt(valueCombo);
		NS.storeChequeras.baseParams.idDivisa = Ext.getCmp(PF + 'txtIdDivisaOriginal').getValue();
	    NS.idPersona=valueCombo;
		NS.nomPersona=NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion');
	
	}
	

	NS.accionarBeneficiario = function(valueCombo){
		
		NS.storeBancoBenef.baseParams.idPersona=parseInt(valueCombo);
		NS.storeChequeras.baseParams.idPersona=parseInt(valueCombo);
		NS.storeChequeras.baseParams.idDivisa = Ext.getCmp(PF + 'txtIdDivisaOriginal').getValue();
	    NS.idPersona=valueCombo;
		//NS.nomPersona=NS.storeBeneficiario.getById(valueCombo).get('descripcion');
	    NS.nomPersona=Ext.getCmp(PF + 'cmbBeneficiario').getValue();
		Ext.getCmp(PF + 'txtIdFormaPago').setValue('');
		NS.cmbFormaPago.reset();
		
		
	}
	
	
	
	NS.accionarBeneficiarioPlantilla = function(valueCombo){
		//NS.storeBancoBenef.baseParams.idPersona=valueCombo;
		
		NS.storeChequeras.baseParams.idPersona=(valueCombo);
		NS.storeChequeras.baseParams.idDivisa = Ext.getCmp(PF + 'txtIdDivisaOriginal').getValue();
	    NS.idPersona=valueCombo;
	    NS.nomPersona=Ext.getCmp(PF + 'cmbBeneficiario').getValue();
		NS.storeBancoBenef.baseParams.nacExt='N';
		NS.storeBancoBenef.baseParams.provUnico='false';
		NS.storeBancoBenef.baseParams.idPersona=(Ext.getCmp(PF+'txtIdBeneficiario').getValue());
		
		NS.storeBancoBenef.baseParams.noEmpresa = 0;
		NS.storeBancoBenef.baseParams.idDivisa='MN';
	
		//NS.mostrarFormaPagoPlantilla(parseInt(Ext.getCmp(PF + 'txtIdFormaPago').getValue()));
		
	}
	
	
	
	//combo Grupo
	NS.cmbBeneficiario = new Ext.form.ComboBox({
		store: NS.storeBeneficiario  
		,name: PF+'cmbBeneficiario'
		,id: PF+'cmbBeneficiario'
		,typeAhead: true
		,mode: 'local'
		//,mode: 'remote'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		//,hideTrigger: true
        ,pageSize: 10
      	,x: 460
        ,y: 15
        ,width: 420
        ,tabIndex: 4
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione beneficiario'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,editable: true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.seleccioneBaneficiario(combo.getValue());
					NS.mostrarFormaPago('');
				}
			},
				beforequery: function(qe){
					NS.parametroBus=qe.combo.getRawValue();
				}
		}
	});
	
	//store  Banco Beneficiario
	    NS.storeBancoBenef = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			nacExt:'N',
			provUnico:'false',
			idPersona:'0',
			noEmpresa:0,
			idDivisa:'MN',
		},
		root: '',
		paramOrder:['nacExt','provUnico','idPersona','noEmpresa','idDivisa'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.obtenerBancoBenef, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoBenef, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existe banco y chequera');
				}
				
			}
		}
	}); 
	
	//Funcion  que puede ser llamada tanto dde la txtIdBancoBenef
	//como del combo cmbBancoBenef
	NS.accionarBancoBenef = function(valueCombo){
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();
		Ext.getCmp(PF + 'txtClabe').reset();
		Ext.getCmp(PF + 'txtPlaza').reset();
		Ext.getCmp(PF + 'txtSucursal').reset();
		NS.storeChequeras.baseParams.idBanco = parseInt(valueCombo);
		NS.storeChequeras.baseParams.idDivisa = Ext.getCmp(PF + 'txtIdDivisaOriginal').getValue();
		NS.storeChequeras.load();
		NS.idBancoBenef = valueCombo;
	}	
	//combo Grupo
	
	NS.cmbBancoBenef = new Ext.form.ComboBox({
		store: NS.storeBancoBenef
		,name: PF+'cmbBancoBeneficiario'
		,id: PF+'cmbBancoBeneficiario'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
    	,x: 145
        ,y: 15
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione banco'
		,triggerAction: 'all'
		,editable:false
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.seleccionaBancoBenef();
					
				}
			}
		}
	});
	
	//store Chequeras
	    NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idPersona:'0',
			idEmpresa:0,
			idBanco:0,
			idDivisa:0,
		},
		root: '',
		paramOrder:['idPersona','idEmpresa','idBanco','idDivisa'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.obtenerChequerasBancoInterlocutor, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
			 {name: 'clabe'},
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});
				
			
				if(records.length==null || records.length<=0) {
					//Ext.Msg.alert('SET','No Existe chequera');
				}else {
					if(records.length == 1) {
						NS.idChequera = '' + records[0].get('id');
						Ext.getCmp(PF + 'cmbChequeras').setValue(records[0].get('descripcion'));
						Ext.getCmp(PF+'txtClabe').setValue(records[0].get('clabe'));
						//NS.buscaSucPlazClabe('' + records[0].get('id'));
					}
					
				}
			}
		}
	}); 
	
	 
		//combo Chequeras
		NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras
		,name: PF+'cmbChequeras'
		,id: PF+'cmbChequeras'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,editable:false
  	    ,x: 340
        ,y: 15
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione chequera'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					//alert(NS.idChequera);
					NS.idChequera = '' + NS.storeChequeras.getById(combo.getValue()).get('id');
					//alert(NS.idChequera);
					Ext.getCmp(PF+'txtClabe').setValue(NS.storeChequeras.getById(combo.getValue()).get('clabe'));
					//Ext.getCmp(PF+'txtClabe').setValue(NS.storeChequeras.getById(combo.getValue()).get('descripcion'));//Ext.getCmp(PF+'txtClabe').setValue(records[0].get('clabe'));
					//NS.buscaSucPlazClabe(NS.idChequera);
				}
			}
		}
	});
	
	NS.buscaSucPlazClabe = function(idChequera) {
		//alert('persona '+NS.idPersona+' empresa '+ NS.idEmpresa+' chequera '+ idChequera);
		CapturaSolicitudesPagoAction.obtenerSucPlazClabe(NS.idPersona, NS.idEmpresa, idChequera, function(result, e){
			if(result!==null) {
				//alert('result'+result)
				//Ext.getCmp(PF+'txtSucursal').setValue(result[0].idSucursal);
				//Ext.getCmp(PF+'txtPlaza').setValue(result[0].idPlaza);
				Ext.getCmp(PF+'txtClabe').setValue(result[0].idClabe);
			}
		});
	};
	
	//store SubCuentas
	    NS.storeRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_rubro',
			campoDos:'desc_rubro',
			tabla:'cat_rubro',
			condicion:'',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.obtenerRubro, 
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubro, msg:"Cargando..."});
				if(NS.seleccionaPrimero){
					//Ext.Msg.alert("set",NS.storeRubro.data.items[0].get('idStr'));
//					if(NS.storeRubro.data.items[0].get('idStr')!=undefined || NS.storeRubro.data.items[0].get('idStr')!=null || NS.storeRubro.data.items[0].get('idStr')!=null){
						NS.cmbRubroAl.setValue(NS.storeRubro.data.items[0].get('idStr'));
						Ext.getCmp(PF + 'txtIdRubroAl').setValue(NS.storeRubro.data.items[0].get('idStr'));
						NS.rubroActual=NS.storeRubro.data.items[0].get('idStr');
//						NS.obtenerGrupoDelRubro();
//					}else{
//						Ext.Msg.alert("SET","No hay Rubros disponibles para este grupo");
//					}
					
				}
			}
		}
	}); 
	  //  NS.storeRubro.load();
	    
	 NS.accionarRubro = function(valueCombo){
	 	 var recordsGridDatos= NS.storeDatos.data.items;
		 var datosClase = NS.gridDatos.getStore().recordType;
		 NS.rubro= valueCombo;
		 NS.descRubro= NS.storeRubro.getById(valueCombo).get('descripcion');
		 var indice;
                    			
		for(var inc=0; inc<recordsGridDatos.length;inc=inc+1)
		{
			if(recordsGridDatos[inc].get('numPartida')===NS.numPartida)
			{
				indice=inc;
				NS.storeDatos.remove(recordsGridDatos[indice]);
			}
		}
		var datos = new datosClase({
           	numPartida: NS.numPartida,
			rubro: NS.rubro,
			descripcion:NS.descRubro,
			importe:NS.importe,
			departamento:NS.depto,
			grupo:NS.grupo,
			area:NS.area
       	});
                  			
        NS.gridDatos.stopEditing();
   		NS.storeDatos.insert(indice, datos);
   		NS.gridDatos.getView().refresh();
   		NS.gridDatos.store.sort('numPartida','ASC');
	 }
	//combo SubCuentas
	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeRubro
		,name: PF+'cmbRubro'
		,id: PF+'cmbRubro'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
   	 	,x: 85
        ,y: 115
		
        ,width: 170
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un rubro'
		,triggerAction: 'all'
		,value: ''
		,disabled:true
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtIdRubro',NS.cmbRubro.getId());
					//NS.accionarRubro(combo.getValue());
				}
			}
		}
	});
	
	NS.limpiaPanelTransfer = function(){
		Ext.getCmp(PF + 'txtIdBancoBenef').reset();
		NS.cmbBancoBenef.reset();
		NS.storeBancoBenef.removeAll();
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();
		Ext.getCmp(PF + 'txtClabe').reset();
		Ext.getCmp(PF + 'txtPlaza').reset();
		Ext.getCmp(PF + 'txtSucursal').reset();
	}
	

	NS.sumaImpPartidas = function(nuevoImpParti) {
		var regStore = NS.storeDatos.data.items;
		var impPartidas = 0;
		var impOri = 0;
		
		for(var i=0; i<regStore.length; i++) {
			impPartidas = impPartidas + parseFloat(regStore[i].get('importe'));
		}
		if(Ext.getCmp(PF + 'txtImporteOriginal').getValue() != '')
			impOri = NS.unformatNumber(Ext.getCmp(PF + 'txtImporteOriginal').getValue());
	
		if((parseFloat(impPartidas) + parseFloat(nuevoImpParti)) > parseFloat(impOri)) {
			Ext.Msg.alert('SET', 'El monto de las partidas supera el improte original');
			return true;
		}
		return false;
	};
	NS.accionarEmpresas(apps.SET.NO_EMPRESA);
	
	
	
	NS.obtenerGrupoDelRubro = function(){
		CapturaSolicitudesPagoAction.obtenerGrupoDelRubro(NS.rubroActual+"", function(result, e){
			if(result.length==null || result.length == 0 ){
				Ext.Msg.alert('SET','No existe un grupo asociado al rubro.');
			}else{
				Ext.getCmp(PF+'txtIdGrupo').setValue(result[0]);
				Ext.getCmp(PF+'txtGrupoAutomatio').setValue(result[1]);
				NS.grupo=result[0];
//				NS.habilitarComponentesPerfilEmpresa();
//				NS.storeBeneficiario.baseParams.condicion=NS.parametroBus;
//    			NS.storeBeneficiario.baseParams.poliza = Ext.getCmp(PF + 'txtIdTipoPoliza').getValue();
//    			NS.storeBeneficiario.baseParams.grupo = result[0];
//    			NS.storeBeneficiario.baseParams.rubro = Ext.getCmp(PF + 'txtIdRubroAl').getValue();
//    			NS.storeBeneficiario.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
//    			NS.storeBeneficiario.load();
//    			NS.parametroBus='';
				
			}
		});
	};
	
	NS.fijaPlantilla = function(){
		CapturaSolicitudesPagoAction.fijaPlantilla(NS.idPlantillaParametro+"", NS.idUsuario+"", function(result, e){
			///alert('respuesta del action es aa '+result.length);
			//alert(result);
			if(result.length==null || result.length == 0 ){
				Ext.Msg.alert('SET','La plantilla no tiene datos');
			}else{
					NS.seleccionaPrimero = false;
					NS.idPolizaParametro=result[6];
					NS.rubroActual = result[8];
					NS.grupo = result[7];
					
					Ext.getCmp(PF+'cmbEmpresas').setValue(result[5]);
					Ext.getCmp(PF+'txtEmpresa').setValue(result[5]);
					//NS.seleccionaEmpresa();
					Ext.getCmp(PF+'cmbTipoPolizas').setValue(result[6]);
					NS.seleccionPoliza();
					Ext.getCmp(PF+'cmbRubroAl').setValue(result[8]);
					NS.seleccionaRubro();
					Ext.getCmp(PF+'cmbRubroAl').setValue(result[2]);
					Ext.getCmp(PF+'cmbBeneficiario').setValue(result[9]);
					NS.seleccioneBaneficiario(parseInt(result[9]));
					Ext.getCmp(PF+'cmbBeneficiario').setValue(result[3]);
					//Ext.getCmp(PF+'cmbFormaPago').setValue(parseInt(result[10]));
					NS.seleccionaFormaDePago(parseInt(result[10]));
					Ext.getCmp(PF+'cmbFormaPago').setValue(result[4]);
					Ext.getCmp(PF+'txtIdFormaPago').setValue(parseInt(result[10]));
					Ext.getCmp(PF+'cmbBancoBeneficiario').setValue(parseInt(result[15]));
					NS.seleccionaBancoBenef();
					Ext.getCmp(PF+'cmbBancoBeneficiario').setValue(parseInt(result[15]));
					Ext.getCmp(PF+'cmbBancoBeneficiario').setValue(result[29]);
					///
					var vectorCajas = [
					                   //cabecera renglo uno
					                   	Ext.getCmp(PF+'txtConcepto'),//texto
						              	//primera columna
						              	Ext.getCmp(PF+'txtTextDec'),//texto cabecera
						              	Ext.getCmp(PF+'txtAsignacion'),// asignacion
						              	Ext.getCmp(PF+'txtTextReferencia'),//refencia
						              	Ext.getCmp(PF+'txtTextCabeceraN'),//referencia de pago
						              	Ext.getCmp(PF+'txtSolicita'),//solicita
						              	//columna dos
						              	Ext.getCmp(PF+'txtSolicitaa'), //centro costos
						              	Ext.getCmp(PF+'txtTextDivisioninf'),//division 
						              	Ext.getCmp(PF+'txtTextOrdeninf'), //Ordeninf   
						              	Ext.getCmp(PF+'txtAutoriza'),  //Autoriza
						              	//observaciones   
						              	Ext.getCmp(PF+'txtObservaciones'), 
					];
					for(var i = 18, j = 0; i <= 28; i++, j++) {
						vectorCajas[j].setValue(result[i]);
	    			}
					///
					Ext.getCmp(PF+'chkLeyenda').setValue(result[13]);
					Ext.getCmp(PF + 'cmbCajaUsuario').setValue(result[14]);
					Ext.getCmp(PF+'txtIdCaja').setValue(result[14]);
					///
					
			}
		});
	};
	
		NS.habilitarComponentesPerfilEmpresa = function(){
		CapturaSolicitudesPagoAction.habilitaComponetesPerfilEmpresa(''+NS.rubroActual, ''+ NS.idPolizaParametro, ''+NS.grupo, function(result, e){
			//CapturaSolicitudesPagoAction.habilitaComponetesPerfilEmpresa(''+NS.idEmpresa, ''+ Ext.getCmp(PF + 'txtIdTipoPoliza').getValue(), ''+Ext.getCmp(PF + 'txtIdGrupo').getValue(), function(result, e){
			var vectorCajas = [
			                   //renglon uno
			              	Ext.getCmp(PF+'txtConcepto'),
			              	Ext.getCmp(PF+'fechaPago'),
			              	Ext.getCmp(PF+'txtConcepto'),//txtIdDivisaPago textfield y combo de divisa
			              	Ext.getCmp(PF+'txtConcepto'),			//Ext.getCmp(PF+'txtTipoCambio'), ANTES
			              	Ext.getCmp(PF+'txtConcepto'),		//Ext.getCmp(PF+'txtImportePago'), ANTES
			              	//primera columna
			              	Ext.getCmp(PF+'txtTextDec'),//texto cabecera
			              	Ext.getCmp(PF+'txtAsignacion'),// asignacion
			              	Ext.getCmp(PF+'txtTextReferencia'),//refencia
			              	Ext.getCmp(PF+'txtTextCabeceraN'),//referencia de pago
			              	Ext.getCmp(PF+'txtSolicita'),//solicita
			              	//columna dos
			              	Ext.getCmp(PF+'txtSolicitaa'), //centro costos
			              	Ext.getCmp(PF+'txtTextDivisioninf'),//division 
			              	Ext.getCmp(PF+'txtTextOrdeninf'), //Ordeninf   
			              	Ext.getCmp(PF+'txtAutoriza'),  //Autoriza
			              	//observaciones   
			              	Ext.getCmp(PF+'txtObservaciones'),
			              	// documento y area destino
			              	Ext.getCmp(PF+'chkNuevopoliza'),
			              	Ext.getCmp(PF+'txtObservaciones')
			];
			var vectorlabel = [
		                    Ext.getCmp(PF+'labelTextoV'),
		                    Ext.getCmp(PF+'labelfechaPagoV'),
			              	Ext.getCmp(PF+'labelTextoV'),
			              	Ext.getCmp(PF+'labelTextoV'),//labelTipoCambioV
			              	Ext.getCmp(PF+'labelTextoV'),//labelImportePagoV
			              	//renglon uno fin
			              	Ext.getCmp(PF+'labelTextoCabeceraV'),//labelAsignacionV
			              	Ext.getCmp(PF+'labelAsignacionV'),
			              	Ext.getCmp(PF+'labelReferenciaV'),
			              	Ext.getCmp(PF+'labelReferenciaPagoV'),
			              	Ext.getCmp(PF+'labelSolicitaV'),
			              	//fin de columna uno
			              	Ext.getCmp(PF+'labelCentroCostosV'),
			              	Ext.getCmp(PF+'labelDivisionV'),
			              	Ext.getCmp(PF+'labelOrdenV'),
			              	Ext.getCmp(PF+'labelAutorizaV'),
			              	//fin de columna dos
			              	Ext.getCmp(PF+'labelObservacionesV'),
			              	//observaciones
			              	Ext.getCmp(PF+'labelObservacionesV'),//labelAreaDestinoV
			              	Ext.getCmp(PF+'labelObservacionesV'), //labelAreaDestinoV
			              	];
			if(result.length==null || result.length<=0)
			{
				Ext.Msg.alert('SET','No tiene componentes asignados');
				
				for(var i = 0; i < vectorlabel.length; i++) {
    				vectorlabel[i].setVisible(false);
    				vectorCajas[i].setVisible(false);
    			}
				
			}else{
				Ext.getCmp(PF+'btnGuardarPlantilla').setDisabled(false);
				for(var i = 0; i < result.length; i++) {
    				vectorlabel[i].setVisible(result[i]);
    				vectorCajas[i].setVisible(result[i]);
    			}
				if(result[15]==false){
					//alert('verdad');
					Ext.getCmp(PF+'chkNuevopoliza').setValue(true);
				}else{
					//alert('false');
					Ext.getCmp(PF+'chkNuevopoliza').setValue(true);
				}
				
				
				NS.storeFormaPago.baseParams.campoUno= ''+NS.idPolizaParametro;
				NS.storeFormaPago.baseParams.campoDos= ''+NS.rubroActual;
				NS.storeFormaPago.baseParams.campoTres= ''+NS.grupo;
				NS.storeFormaPago.load(); 
			}
			
			
		});
	};
	
	NS.reporte = function(noDoctoReal, sImportLetras) {
		var strParams = "";
		
		strParams = '?nomReporte=solicitudPago';
		strParams += '&'+'nomParam1=pNomEmpresa';
		strParams += '&'+'valParam1=' + NS.storeEmpresas.getById(parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue())).get('nomEmpresa');
		strParams += '&'+'nomParam2=fecHoy';
		strParams += '&'+'valParam2=' + apps.SET.FEC_HOY;
		strParams += '&'+'nomParam3=pNoDocto';
		strParams += '&'+'valParam3=' + noDoctoReal;
		strParams += '&'+'nomParam4=pFecha';
		strParams += '&'+'valParam4=' + cambiarFecha('' + Ext.getCmp(PF + 'fechaFactura').getValue());
		strParams += '&'+'nomParam5=pFechaPago';
		strParams += '&'+'valParam5=' + cambiarFecha('' + Ext.getCmp(PF + 'fechaPago').getValue());
		strParams += '&'+'nomParam6=pFormaPago';
		strParams += '&'+'valParam6=' + NS.storeFormaPago.getById(parseInt(Ext.getCmp(PF + 'txtIdFormaPago').getValue())).get('descFormaPago');
		strParams += '&'+'nomParam7=pArea';
		strParams += '&'+'valParam7=' + NS.storeEmpresas.getById(parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue())).get('nomEmpresa');
		strParams += '&'+'nomParam8=pDivisa';
		strParams += '&'+'valParam8=' + NS.storeDivisas.getById(Ext.getCmp(PF + 'txtIdDivisaOriginal').getValue()).get('descDivisa');
		strParams += '&'+'nomParam9=pCompania';
		strParams += '&'+'valParam9=' + NS.storeEmpresas.getById(parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue())).get('nomEmpresa');
		strParams += '&'+'nomParam10=pDireccion';
		strParams += '&'+'valParam10=' + '';
		strParams += '&'+'nomParam11=pDepartamento';
		strParams += '&'+'valParam11=' + '';
		strParams += '&'+'nomParam12=pPuesto';
		strParams += '&'+'valParam12=' + '';
		strParams += '&'+'nomParam13=pBeneficiario';
		strParams += '&'+'valParam13=' + Ext.getCmp(PF + 'txtSolicita').getValue();
		strParams += '&'+'nomParam14=pNumero';
		strParams += '&'+'valParam14=' + apps.SET.iUserId;
		strParams += '&'+'nomParam15=pRazonSocial';
		strParams += '&'+'valParam15=' + NS.idPersona + '     ' + NS.storeBeneficiario.getById(NS.idPersona).get('descripcion');
		strParams += '&'+'nomParam16=pDireccionEmp';
		strParams += '&'+'valParam16=' + '';
		strParams += '&'+'nomParam17=pImporte';
		strParams += '&'+'valParam17=' + sImportLetras;
		strParams += '&'+'nomParam18=pConcepto';
		strParams += '&'+'valParam18=' + Ext.getCmp(PF + 'txtConcepto').getValue();
		strParams += '&'+'nomParam19=pFechaT';
		strParams += '&'+'valParam19=' + '';
		strParams += '&'+'nomParam20=pDia';
		strParams += '&'+'valParam20=' + '';
		strParams += '&'+'nomParam21=pDepartamentoD';
		strParams += '&'+'valParam21=' + '';
		strParams += '&'+'nomParam22=pAutorizadoD';
		strParams += '&'+'valParam22=' + Ext.getCmp(PF + 'txtAutoriza').getValue();
		strParams += '&'+'nomParam23=pComentarios';
		strParams += '&'+'valParam23=' + Ext.getCmp(PF + 'txtObservaciones').getValue();
		strParams += '&'+'nomParam24=pAnalizadoPor';
		strParams += '&'+'valParam24=' + '';
		strParams += '&'+'nomParam25=pRevisadoPor';
		strParams += '&'+'valParam25=' + '';
		strParams += '&'+'nomParam26=pAprovadoPor';
		strParams += '&'+'valParam26=' + '';
		strParams += '&'+'nomParam27=pAutorizadoPor';
		strParams += '&'+'valParam27=' + '';
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		return;
	};
	//stores compenentes nuevos

	//cmbPlantilla
	NS.storePlantilla = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'ID_PLANTILLA',
			campoDos:'NOMBRE_PLANTILLA',
			tabla:'PLANTILLA_SOLICITUD_PAGO',
			condicion:'  Id_Usuario = '+NS.idUsuario ,
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarCombo,  
		idProperty: 'idStr', 
		
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePlantilla, msg:"Cargando..."});
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No Existen plantillas para este usuario.');
					}
				}
			}
	});
	//NS.storePlantilla.load();

//store polizas
	
	NS.storeTipoPolizas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'ID_POLIZA',
			campoDos:'NOMBRE_POLIZA',
			tabla:'ZIMP_POLIZAMANUAL',
			condicion:''+NS.idUsuario +'',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarComboPoliza,  
		idProperty: 'idStr', 
		
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTipoPolizas, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen tipos de polizas para este usuario.');
					}
				}
			}
	}); 
	
	///store de grupo nuevo
	
	NS.storeGrupoCaja = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'Id_Grupo',
			campoDos:'Id_Rubro',
			tabla:'cat_rubro',
			condicion:'',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarCombo,  
		idProperty: 'idStr', 
		
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoCaja, msg:"Cargando..."});
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No Existen grupos para el rubro seleccionado.');
				}
			}
		}
	}); 

	//NS.storeGrupo.load();
	//NS.storeTipoPolizas.load();
	//NS.storePlantilla.load();
	//componentes nuevos
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo
		,name: PF+'cmbGrupo'
		,id: PF+'cmbGrupo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 100
        ,y: 265
        ,width: 195
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un grupo'
		,triggerAction: 'all'
		,indexTab: 25
		,disabled: false
		,value: ''
		,visible: false
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
				//NS.storeMaestro.baseParams.idGrupoRubro=combo.getValue();
				BFwrk.Util.updateComboToTextField(PF+'txtIdGrupo',NS.cmbGrupo.getId());
				NS.grupo=combo.getValue();
				NS.storeRubro.baseParams.condicion='id_grupo='+combo.getValue();
				NS.storeRubro.load(); 
				/*NS.storeRubro.baseParams.condicion= 'id_grupo = '+combo.getValue();
				NS.storeRubro.load(); 
				NS.grupo=combo.getValue();
				if(Ext.getCmp(PF + 'cmbRubroAl').getValue() != null && Ext.getCmp(PF + 'cmbRubroAl').getValue() != undefined && Ext.getCmp(PF + 'cmbRubroAl').getValue() != '') {
					NS.habilitarComponentesPerfilEmpresa();
				}*/
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
		,x: 500
        ,y: 265
//		,x: 735
//        ,y: 15
        ,width: 195
        ,indexTab: 28
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un rubro'
		,triggerAction: 'all'
		,value: ''
		,visible: true
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.seleccionaPrimero=true;
					
					//	BFwrk.Util.updateComboToTextField(PF+'txtIdRubroAl',NS.cmbRubroAl.getId());
					
					
					NS.seleccionaRubro();
				}
			}
		}
	});
	
	NS.cmbPlantilla = new Ext.form.ComboBox({
		name: PF+'cmbPlantilla'
		,id: PF+'cmbPlantilla'
		,store: NS.storePlantilla
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 685
        ,y: 15
        ,width: 195
        //,indexTab: 28
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una plantilla'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,hidden: true //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
				//NS.storeMaestro.baseParams.idGrupoRubro=combo.getValue();
				BFwrk.Util.updateComboToTextField(PF+'txtTextPlantilla',NS.cmbPlantilla.getId());
				NS.idPlantillaParametro = Ext.getCmp(PF + 'txtTextPlantilla').getValue();
				NS.fijaPlantilla();
				}
			}
		}
	});
	
		
	NS.cmbTipoPolizas = new Ext.form.ComboBox({
		store: NS.storeTipoPolizas
		,name: PF+'cmbTipoPolizas'
		,id: PF+'cmbTipoPolizas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 430
        ,y: 15
        ,width: 195
		,valueField:'idStr'
		,indexTab: 30
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un tipo de poliza'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.seleccionPoliza();
				}
			},
			///////
//			change: {
//				fn:function(combo, valor) {
//					BFwrk.Util.updateComboToTextField(PF+'txtIdTipoPoliza',NS.cmbTipoPolizas.getId());
//					NS.idPolizaParametro = parseInt(NS.cmbTipoPolizas.getValue());
//					
//					if(Ext.getCmp(PF + 'cmbRubroAl').getValue() != null && Ext.getCmp(PF + 'cmbRubroAl').getValue() != undefined && Ext.getCmp(PF + 'cmbRubroAl').getValue() != '') {
//						NS.habilitarComponentesPerfilEmpresa();
//						NS.storeRubro.baseParams.condicion='Id_rubro in (select id_grupo from ASIGNACION_POLIZA_GRUPO where id_poliza = '+NS.idPolizaParametro+')';
//						NS.storeRubro.load(); 
//						
//					}
//
//					NS.storeRubro.baseParams.condicion='Id_rubro in (select id_grupo from ASIGNACION_POLIZA_GRUPO where id_poliza = '+NS.idPolizaParametro+')';
//					NS.storeRubro.load();
//        		}
//        	}
			
			
			////
		}
	});
	//////////////////compponentes ventana modal////////////////////////////////
	NS.lblEmpresaInfo = new Ext.form.Label({
		text: 'EMPRESA:',
		x: 10,
		y: 10
	});
	NS.lblProveedorInfo = new Ext.form.Label({
		text: 'PROVEEDOR',
		x: 10,
		y: 40
	});
	NS.lblFechaPagoInfo = new Ext.form.Label({
		text: 'FECHA DE PAGO',
		x: 10,
		y: 70
	});
	NS.lblImporteInfo = new Ext.form.Label({
		text: 'IMPORTE',
		x: 10,
		y: 100
	});
	
	NS.lblFormaPagoInfo = new Ext.form.Label({
		text: 'FORMA DE PAGO',
		x: 410,
		y: 10
	});
	NS.lblDivisaInfo = new Ext.form.Label({
		text: 'DIVISA',
		x: 410,
		y: 40
	});
	NS.lblConceptoInfo = new Ext.form.Label({
		text: 'CONCEPTO',
		x: 410,
		y: 70
	});
	NS.lblDocumentoInfo = new Ext.form.Label({
		text: 'DOCUMENTO',
		x: 10,
		y: 130
	});
	
	NS.txtImporteInfo = new Ext.form.TextField({
		id: PF + 'txtImporteInfo',
		name: PF + 'txtImporteInfo',
		x: 160,
		y: 100,
		width: 145,
	});
	NS.txtFormaPagoInfo = new Ext.form.TextField({
		id: PF + 'txtFormaPagoInfo',
		name: PF + 'txtFormaPagoInfo',
		disabled: true,
		x: 520,
		y: 10,
		width: 160,
	});
	NS.txtDivisaInfo = new Ext.form.TextField({
		id: PF + 'txtDivisaInfo',
		name: PF + 'txtDivisaInfo',
		disabled: true,
		x: 520,
		y: 40,
		width: 160,
	});
	NS.txtConceptoInfo = new Ext.form.TextField({
		id: PF + 'txtConceptoInfo',
		name: PF + 'txtConceptoInfo',
		disabled: true,
		x: 520,
		y: 70,
		width: 160,
	});
	NS.txtEmpresaInfo = new Ext.form.TextField({
		id: PF + 'txtEmpresaInfo',
		name: PF + 'txtEmpresaInfo',
		x: 160,
		y: 10,
		width: 45,
	});
	NS.txtDocumentoInfo = new Ext.form.TextField({
		id: PF + 'txtDocumentoInfo',
		name: PF + 'txtDocumentoInfo',
		x: 160,
		y: 130,
		width: 145,
	});
	NS.txtFechaPagoInfo = new Ext.form.TextField({
		id: PF + 'txtFechaPagoInfo',
		name: PF + 'txtFechaPagoInfo',
		x: 160,
		y: 70,
		width: 145,
	});
	NS.txtProveedorInfo = new Ext.form.TextField({
		id: PF + 'txtProveedorInfo',
		name: PF + 'txtProveedorInfo',
		x: 160,
		y: 40,
		width: 45,
	});
	NS.cmbEmpresasInfo = new Ext.form.ComboBox({
		id: PF + 'cmbEmpresasInfo',
		name: PF + 'cmbEmpresasInfo',
		width: 170,
		x: 210,
		y: 10,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: 'SELECCIONE UNA EMPRESA',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor){
			
				}	
			}
		}
	});
	
	NS.cmbProveeodrInfo = new Ext.form.ComboBox({
		id: PF + 'cmbProveeodrInfo',
		name: PF + 'cmbProveeodrInfo',
		width: 170,
		x: 210,
		y: 40,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: 'SELECCIONE UN PROVEEDOR',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor){
			
				}	
			}
		}
	});
	NS.cmbCentroCostosInfo = new Ext.form.ComboBox({
		id: PF + 'cmbCentroCostosInfo',
		name: PF + 'cmbCentroCostosInfo',
		width: 100,
		x: 490,
		y: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		autocomplete: true,
		emptyText: 'CENTRO DE COSTOS',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor){
			
				}	
			}
		}
	});
	
	/////////////// fin de los componentes de la ventana modal///////////////////////////
	///////////////// inicio componentes de la venta plantilla  ////////////////////////
	NS.lblPlantillaInfo = new Ext.form.Label({
		text: 'Plantilla',
		x: 10,
		y: 10
	});
	NS.txtNombrePlantillaInfo = new Ext.form.TextField({
		id: PF + 'txtNombrePlantillaInfo',
		name: PF + 'txtNombrePlantillaInfo',
		x: 10,
		y: 30,
		width: 150,
		maxLength: 25,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				if(e.keyCode ==23){ 
	 					NS.buscar();
	 				}
	 			}
	 		},
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 23));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 23));
	 				}
	 				
	 			}
	 		},
	 		
		}
		
	});
	/////////////// inicio componentes de la venta plantilla  ////////////////////////
	///////////////// inicio ventana plantilla	///////////////////////////
	var winPlantilla = new Ext.Window({
		title: 'Plantilla',
		modal: true,
		shadow: true,
		width: 260,
		height: 260,
		layout: 'absolute',
		plain: true,
		bodyStyle: 'padding:10px;',
		buttonAlign: 'center',
		closeAction: 'hide',
	   	items: [
				{
					 xtype: 'fieldset',
					 id: PF + 'PanelContenedorPlantilla',
					 title: 'Guardar plantilla',
					 layout: 'absolute',
					 x: 10,
					 y: 10,
					 width: 240,
					 height: 250,
					 items: [
					         	{
		                              xtype: 'button',
		                              text: 'Guardar',
		                              x: 10,
		                              y: 130,
		                              width: 90,
		                              height: 22,
		                              id: PF+'btnGuardarPlantillaInfo',
		                              name: PF+'btnGuardarPlantillaInfo',
		                              listeners:{
		                              	click:{
		                              		fn:function(e){
		                              			Ext.MessageBox.show({
		                             		       title : 'Información SET',
		                             		       msg : 'Procesando informacion, espere por favor...',
		                             		       width : 300,
		                             		       wait : true,
		                             		       progress:true,
		                             		       waitConfig : {interval:200}//,
		                             		       //icon :'lupita'
		                             	           //icon :'forma_alta', //custom class in msg-box.html
		                             	           ///animateTarget : 'mb7'
		                             	   		});
		                              			Ext.getCmp(PF + 'btnGuardarPlantillaInfo').setDisabled(true);//Ext.getCmp(PF + 'txtClabe')
//		                              	
		                                			var regPlantilla = {};
		                                			
		                                			var matrizPlantilla = new Array ();
		                                			regPlantilla.nomPlantilla=Ext.getCmp(PF + 'txtNombrePlantillaInfo').getValue();
		                                			regPlantilla.idEmpresa=NS.idEmpresa;
		                                			regPlantilla.idPersona=NS.idPersona;
		                                			regPlantilla.idUsuario=NS.idUsuario;
		                                			regPlantilla.tipoPoliza=Ext.getCmp(PF + 'txtIdTipoPoliza').getValue();
		                                			regPlantilla.idGrupo=Ext.getCmp(PF + 'txtIdGrupo').getValue();
		                                			regPlantilla.idRubro=Ext.getCmp(PF + 'txtIdRubroAl').getValue();
		                                			regPlantilla.idBeneficiario=Ext.getCmp(PF + 'txtIdBeneficiario').getValue();
		                                			regPlantilla.idDivOriginal=NS.idDivOriginal ;
		                                			regPlantilla.concepto=Ext.getCmp(PF + 'txtConcepto').getValue();
		                                			regPlantilla.observaciones=Ext.getCmp(PF + 'txtObservaciones').getValue();
		                                			regPlantilla.descripcion=Ext.getCmp(PF + 'txtTextDec').getValue();
		                                			regPlantilla.asignacion=Ext.getCmp(PF + 'txtAsignacion').getValue();
		                                			regPlantilla.idFormaPago=NS.idFormaPago;
		                                			regPlantilla.fechaFactura=cambiarFecha('' + Ext.getCmp(PF + 'fechaFactura').getValue());
		        									regPlantilla.fechaPago = cambiarFecha('' + Ext.getCmp(PF + 'fechaPago').getValue());
		                                			regPlantilla.referencia=Ext.getCmp(PF + 'txtTextReferencia').getValue();
		                                			regPlantilla.cabecera=Ext.getCmp(PF + 'txtTextCabeceraN').getValue();
		                                			regPlantilla.autoriza=Ext.getCmp(PF + 'txtAutoriza').getValue();
		                                			regPlantilla.solicita=Ext.getCmp(PF + 'txtSolicita').getValue();
		                                			regPlantilla.importePago = NS.unformatNumber(Ext.getCmp(PF + 'txtImportePago').getValue());
		                                			regPlantilla.importeOriginal = NS.unformatNumber(Ext.getCmp(PF + 'txtImporteOriginal').getValue());
		                                			regPlantilla.idDivPago=NS.idDivPago ;
		                                			regPlantilla.tipoCambio = Ext.getCmp(PF + 'txtTipoCambio').getValue();
		                                			regPlantilla.tranferenciaBancoNac = Ext.getCmp(PF + 'optNac').getValue();
		                                			regPlantilla.polizaContable = Ext.getCmp(PF + 'chkNuevopoliza').getValue();
		                                			regPlantilla.leyenda = Ext.getCmp(PF+'chkLeyenda').getValue();
		                                			regPlantilla.bancoBeneficiario = Ext.getCmp(PF + 'txtIdBancoBenef').getValue();
		                                			regPlantilla.chequeraBeneficiario = Ext.getCmp(PF + 'cmbChequeras').getValue();
		                                			regPlantilla.centroCostos=Ext.getCmp(PF + 'txtSolicitaa').getValue();
		                                			regPlantilla.division = Ext.getCmp(PF + 'txtTextDivisioninf').getValue();//
		                                			regPlantilla.clabe = Ext.getCmp(PF + 'txtClabe').getValue();
		                                			regPlantilla.sucursal = Ext.getCmp(PF + 'txtSucursal').getValue();
		                                			regPlantilla.plaza = Ext.getCmp(PF + 'txtPlaza').getValue();
		                                			regPlantilla.orden = Ext.getCmp(PF+'txtTextOrdeninf').getValue(); //Ordeninf
		                                			
		                                			regPlantilla.cajaCheque = Ext.getCmp(PF + 'cmbCajaUsuario').getValue();
		                                			matrizPlantilla[0] = regPlantilla; 
		        									var jsonString = Ext.util.JSON.encode(matrizPlantilla);	
		        									//Ext.Msg.alert('Información SET', jsonString );
		        									CapturaSolicitudesPagoAction.registrarPlantilla(jsonString, function(result, e){
		        										if(result.msgUsuario !== null && result.msgUsuario !== ''){ 
		        											Ext.Msg.alert('Información SET', '' + result.msgUsuario);
		        											NS.storePlantilla.reload();
		        										}
		        										Ext.getCmp(PF + 'btnGuardarPlantillaInfo').setDisabled(false);//Ext.getCmp(PF + 'txtClabe')
		        										
		        									});
		        									//
		        									
		        	                        			
		        	                        			var vectorCombos =[
		        	                          								Ext.getCmp(PF+'cmbTipoPolizas'),
		        	                          								//Ext.getCmp(PF+'cmbGrupo'),//
		        	                          								Ext.getCmp(PF+'txtGrupoAutomatio'),
		        	                          								// cabecera renglon dos  
		        	                          								Ext.getCmp(PF+'cmbRubroAl'),
		        	                          								Ext.getCmp(PF+'cmbBeneficiario'),
		        	                          								Ext.getCmp(PF+'cmbFormaPago'),
		        	                          								Ext.getCmp(PF+'cmbEmpresas'),//cmbFormaPagoExt.getCmp(PF+'txtIdFormaPago'),//
		        	                          								Ext.getCmp(PF + 'cmbBeneficiario')
		        	                          				                   ];
		        	                           			
		        	                           			
		        	                           			for(var  j = 0; j <= 6; j++) {
		        	                           				vectorCombos[j].setValue('');
		        	                           			}
		        	                           			var vectorCajas = [
		        	                       				                   //cabecera renglo uno
		        	                       				                Ext.getCmp(PF + 'txtIdBancoBenef'),
		        	                       				                Ext.getCmp(PF + 'txtClabe'),
		        	                       				                Ext.getCmp(PF + 'txtSucursal'),
		        	                       				                Ext.getCmp(PF + 'txtPlaza'),   
		        	                       				                   
		        	                       				                Ext.getCmp(PF + 'txtIdBeneficiario'),
		        	                       				                Ext.getCmp(PF+'txtEmpresa'),//cmbEmpresas
		        	                       			                    Ext.getCmp(PF+'txtIdTipoPoliza'),
		        	                       				                Ext.getCmp(PF+'txtIdGrupo'),
		        	                       				                // cabecera renglon dos  
		        	                       				                Ext.getCmp(PF+'txtIdRubroAl'),
		        	                       				                Ext.getCmp(PF+'txtIdBeneficiario'),//
		        	                       				                //cabecera renglon tres
		        	                       				                Ext.getCmp(PF+'txtIdFormaPago'),
		        	                       				               // Ext.getCmp(PF+'txtIdDivisaOriginal'),
		        	                       				                Ext.getCmp(PF+'chkNuevopoliza'),
		        	                       				                //renglon uno
		        	                       				              	Ext.getCmp(PF+'txtConcepto'),//texto
		        	                       				              	//Ext.getCmp(PF+'fechaPago'),
		        	                       				              	//Ext.getCmp(PF+'panelDivisaPagoV'),//txtIdDivisaPago textfield y combo de divisa
		        	                       				              	Ext.getCmp(PF+'txtTipoCambio'),
		        	                       				              	Ext.getCmp(PF+'txtImportePago'),
		        	                       				              	
		        	                       				              	//primera columna
		        	                       				              	Ext.getCmp(PF+'txtTextDec'),//texto cabecera
		        	                       				              	Ext.getCmp(PF+'txtAsignacion'),// asignacion
		        	                       				              	Ext.getCmp(PF+'txtTextReferencia'),//refencia
		        	                       				              	Ext.getCmp(PF+'txtTextCabeceraN'),//referencia de pago
		        	                       				              	Ext.getCmp(PF+'txtSolicita'),//solicita
		        	                       				              	//columna dos
		        	                       				              	Ext.getCmp(PF+'txtSolicitaa'), //centro costos
		        	                       				              	Ext.getCmp(PF+'txtTextDivisioninf'),//division 
		        	                       				              	Ext.getCmp(PF+'txtTextOrdeninf'), //Ordeninf   
		        	                       				              	Ext.getCmp(PF+'txtAutoriza'),  //Autoriza
		        	                       				              	//observaciones   
		        	                       				              	Ext.getCmp(PF+'txtObservaciones'),
		        	                       				              	// documento y area destino
		        	                       				              	//Ext.getCmp(PF+'panelDocumentoV'),
		        	                       				              	Ext.getCmp(PF+'cmbAreaDestino'),
		        	                       				              	Ext.getCmp(PF + 'txtImporteOriginal'),
		        	                       				              	Ext.getCmp(PF + 'cmbCajaUsuario'),//
		        	                       				              	Ext.getCmp(PF + 'txtIdCaja')
		        	                       				];
		        	                           			//Ext.getCmp(PF+'txtImporteOriginal').setValue('');
		        	                       				for(var  j = 0; j <= 28; j++) {
		        	                       					console.log(j);
		        	                       					vectorCajas[j].setValue('');
		        	                           			}
		        	                       			
		        	                        			var vectorCajas = [
		        	                    				                   //cabecera renglo uno
		        	                    				                Ext.getCmp(PF+'txtEmpresa'),//cmbEmpresas
		        	                    			                    Ext.getCmp(PF+'txtIdTipoPoliza'),
		        	                    				                Ext.getCmp(PF+'txtIdGrupo'),
		        	                    				                // cabecera renglon dos  
		        	                    				                Ext.getCmp(PF+'txtIdRubroAl'),
		        	                    				                Ext.getCmp(PF+'txtIdBeneficiario'),//
		        	                    				                //cabecera renglon tres
		        	                    				                Ext.getCmp(PF+'txtIdFormaPago'),
		        	                    				               // Ext.getCmp(PF+'txtIdDivisaOriginal'),
		        	                    				                Ext.getCmp(PF+'chkNuevopoliza'),
		        	                    				                //renglon uno
		        	                    				              	Ext.getCmp(PF+'txtConcepto'),//texto
		        	                    				              	//Ext.getCmp(PF+'fechaPago'),
		        	                    				              	//Ext.getCmp(PF+'panelDivisaPagoV'),//txtIdDivisaPago textfield y combo de divisa
		        	                    				              	Ext.getCmp(PF+'txtTipoCambio'),
		        	                    				              	Ext.getCmp(PF+'txtImportePago'),
		        	                    				              	
		        	                    				              	//primera columna
		        	                    				              	Ext.getCmp(PF+'txtTextDec'),//texto cabecera
		        	                    				              	Ext.getCmp(PF+'txtAsignacion'),// asignacion
		        	                    				              	Ext.getCmp(PF+'txtTextReferencia'),//refencia
		        	                    				              	Ext.getCmp(PF+'txtTextCabeceraN'),//referencia de pago
		        	                    				              	Ext.getCmp(PF+'txtSolicita'),//solicita
		        	                    				              	//columna dos
		        	                    				              	Ext.getCmp(PF+'txtSolicitaa'), //centro costos
		        	                    				              	Ext.getCmp(PF+'txtTextDivisioninf'),//division 
		        	                    				              	Ext.getCmp(PF+'txtTextOrdeninf'), //Ordeninf   
		        	                    				              	Ext.getCmp(PF+'txtAutoriza'),  //Autoriza
		        	                    				              	//observaciones   
		        	                    				              	Ext.getCmp(PF+'txtObservaciones'),
		        	                    				              	// documento y area destino
		        	                    				              	Ext.getCmp(PF+'txtObservaciones'),
		        	                    				              	Ext.getCmp(PF+'cmbAreaDestino')
		        	                    				];
		        	                        			Ext.getCmp(PF+'txtNombrePlantillaInfo').setValue('');
		        	                        			Ext.getCmp(PF + 'txtImporteOriginal').setValue('');
		        	                        			NS.cmbPlantilla.setValue('');
		        	                        			NS.mostrarFormaPago();
		        	                    				for(var  j = 0; j <= 21; j++) {
		        	                    					vectorCajas[j].setValue('');
		        	                        			}
		        	                    				NS.mostrarFormaPago();
		        	                    				NS.mostrarFormaPago(parseInt(Ext.getCmp(PF + 'txtIdFormaPago').getValue()));
		        									//
		        	                    				
		                              			}
		                              		}
		                              }
		                          },
		                          NS.lblPlantillaInfo,
		                          NS.txtNombrePlantillaInfo,
		                          {
		                              xtype: 'button',
		                              text: 'Cancelar',
		                              x: 122,
		                              y: 130,
		                              width: 90,
		                              height: 22,
		                              id: PF+'btnCancelarPlantillaInfo',
		                              name: PF+'btnCancelarPlantillaInfo',
		                              listeners:{
		                              	click:{
		                              		fn:function(e){
		                              			winPlantilla.hide();
		                              			}
		                              		}
		                              }
		                          }
					        ]
				  }
	   	        ]
	  
	});
	
/////////////// fin ventana plantilla	///////////////////////////
	//ventana para ver el documento  
	var winDetalle = new Ext.Window({
		title: 'Movimientos',
		modal: true,
		shadow: true,
		width: 800,
		height: 300,
		layout: 'absolute',
		plain: true,
		bodyStyle: 'padding:10px;',
		buttonAlign: 'center',
		closeAction: 'hide',
	   	items: [
				{
					 xtype: 'fieldset',
					 id: PF + 'PanelContenedor',
					 title: 'Imprimir Comprobante de solicitud de pago',
					 layout: 'absolute',
					 x: 10,
					 y: 10,
					 width: 750,
					 height: 250,
					 items: [
					         	NS.lblEmpresaInfo,
					         	NS.lblProveedorInfo,
					         	NS.lblFechaPagoInfo,
					         	NS.lblImporteInfo,
					         	NS.lblDocumentoInfo,
					         	NS.txtEmpresaInfo,
					         	NS.txtImporteInfo,
					         	NS.txtFechaPagoInfo,
					         	NS.txtDocumentoInfo,
					         	NS.txtProveedorInfo,
					         	NS.cmbEmpresasInfo,
					         	NS.cmbProveeodrInfo,
					         	NS.lblFormaPagoInfo,
					         	NS.lblDivisaInfo,
					         	NS.lblConceptoInfo,
					         	NS.txtFormaPagoInfo,
					         	NS.txtConceptoInfo,
					         	NS.txtDivisaInfo,
					         	{
		                              xtype: 'button',
		                              text: 'Buscar',
		                              x: 600,
		                              y: 130,
		                              width: 100,
		                              height: 22,
		                              id: PF+'btnConsultarDocumentoinfo',
		                              name: PF+'btnConsultarDocumentoinfo',
		                              listeners:{
		                              	click:{
		                              		fn:function(e){
		                              			alert("Esta listo para programar.");
		                              			}
		                              		}
		                              }
		                          }
					        ]
				  }
	   	        ]
	  
	});
	
	//Contenedor de detalle de las propuestas
	NS.contDetalle = new Ext.form.FieldSet({
		title: 'Imprimir',
		layout: 'absolute',
	  
	});
	//fin de componentes nuevos
NS.CapturaSolicitudesPago = new Ext.FormPanel({
    title: 'Captura Solicitudes de Pago',
    width: 1020,
    height: 700,
    frame: true,
    //padding: 10,
    autoScroll: true,
    layout: 'absolute',
    id: PF + 'capturaSolicitudesPago',
    name: PF+ 'capturaSolicitudesPago',
    renderTo: NS.tabContId,
    // html : '<img src="img/formas/Fondo_Form.png">',
    items :[
            {     	
                xtype: 'fieldset',
                title: '',
                width: 1010,
                height: 530,
                x: 20,
                y: 5,
                layout: 'absolute',
                items: [
                    {
                    	
                    	xtype: 'fieldset',
                    	title: 'Captura',
                        width: 985,
                        height: 505,
                        layout: 'absolute',
                        items: [
                            {
                            	xtype: 'textfield',
                                //x: 650,
                                //y: 15,
                            	x: 10,
                                y: 265,
                                width: 70,
                                name: PF+'txtIdGrupo',
                                id: PF+'txtIdGrupo',
                                indexTab: 24,
                                hidden: false, //true
                                //disabled: true,
                                readOnly: true,
                                listeners: {
                                	change: {
                                		fn:function(caja) {
                            	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
        	                        			valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdGrupo',NS.cmbGrupo.getId());
        	                        			if(valueCombo == null && valueCombo == undefined && valueCombo == ''){
        	                        				Ext.getCmp(PF + 'txtIdGrupo').reset();
        	                        			}
                            	   			}else
                            	   				NS.cmbGrupo.reset();
                            	   			NS.habilitarComponentesPerfilEmpresa(); //reserva
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'label',
                                text: 'Grupo:',
                               // x: 650,
                               // y: 1,
                                x: 10,
                                y: 250,
                                hidden:false //true
                                
                            },
                            {
                                xtype: 'label',
                                text: 'Rubro:',
                                //x: 10,
                                //y: 50,
                                x: 400,
                                y: 250,
                                hidden: false //true
                                
                            },

                            {
                                xtype: 'textfield',//centroCostos
                                x: 90,
                                y: 15,
                                width: 150,
                                indexTab: 32,
                                id: PF+'txtGrupoAutomatio',  
                                name: PF+'txtGrupoAutomatio',
                                hidden:true,
                                //readonly: true,
                                readOnly: true,
                                //disabled: true
                                
                            },
                            {
                            	xtype: 'textfield',
                               // x: 10,
                                //y: 15,
                            	x: 400,
                                y: 265,
                                width: 80,
                                name: PF+'txtIdRubroAl',
                                id: PF+'txtIdRubroAl',
                                indexTab: 27,
                                hidden: false, //true
                                listeners: {
                                	change: {
                                		fn:function(caja) {
                                			if(NS.existeEnStored(NS.storeRubro, caja.getValue())){
	                                			NS.cmbRubroAl.setValue(caja.getValue());
	                                			NS.seleccionaPrimero=true;
	                        					NS.seleccionaRubro();
                                			}else{
                                				NS.cmbRubroAl.reset();
                                				Ext.getCmp(PF + 'txtIdRubroAl').reset();
                                				Ext.getCmp(PF + 'txtIdGrupo').reset();
                                				Ext.getCmp(PF+'txtGrupoAutomatio').reset();
                                				Ext.getCmp(PF + 'txtIdBeneficiario').reset();
                                				NS.cmbBeneficiario.reset();
                                				NS.storeBeneficiario.removeAll();
                                				NS.cmbFormaPago.reset();
                                				Ext.getCmp(PF + 'txtIdFormaPago').reset();
                                				Ext.getCmp(PF + 'txtIdDivisaOriginal').reset();
                                				NS.cmbDivisaOriginal.reset();
                                				NS.storeFormaPago.removeAll();
                                				NS.rubroActual= 0; 
                                				NS.grupo= 0;
                                				NS.habilitarComponentesPerfilEmpresa();
                                				NS.mostrarFormaPago();
                                			}
                            	   			/*if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
        	                        			valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdRubroAl',NS.cmbRubroAl.getId());
        	                        			
        	                        			if((valueCombo != null && valueCombo != undefined && valueCombo != ''))
        	                        				Ext.getCmp(PF + 'txtIdRubroAl').reset();
                            	   			}else
                            	   				NS.cmbRubroAl.reset();     
                            	   				*/
                                		}
                                	}
                                }
                            }, 
                    NS.cmbRubroAl,
                    
                    //
                    {
                        xtype: 'label',
                        text: 'Tipo de poliza:',
                        x: 350,
                        hidden: true, //true
                        y: 0
                    },
                    {
                    	xtype: 'textfield',
                        x: 350,
                        y: 15,
                        width: 60,
                        indexTab: 29,
                        name: PF+'txtIdTipoPoliza',
                        id: PF+'txtIdTipoPoliza',
                        hidden: true, //true
                        listeners: {
                        	change: {
                        		fn:function(caja) {
                        			if(NS.existeEnStored(NS.storeTipoPolizas,caja.getValue())){
	                        			NS.seleccionaPrimero = true;
	                        			NS.cmbTipoPolizas.setValue(caja.getValue());
	                        			NS.seleccionPoliza();
                        			}else{
                        				NS.cmbTipoPolizas.reset();
                        				Ext.getCmp(PF + 'txtIdTipoPoliza').reset();
                        				NS.cmbRubroAl.reset();
                        				Ext.getCmp(PF + 'txtIdRubroAl').reset();
                        				NS.storeRubro.removeAll();
                        				Ext.getCmp(PF + 'txtIdGrupo').reset();
                        				Ext.getCmp(PF+'txtGrupoAutomatio').reset();
                        				Ext.getCmp(PF + 'txtIdBeneficiario').reset();
                        				NS.cmbBeneficiario.reset();
                        				NS.storeBeneficiario.removeAll();
                        				NS.cmbFormaPago.reset();
                        				Ext.getCmp(PF + 'txtIdFormaPago').reset();
                        				Ext.getCmp(PF + 'txtIdDivisaOriginal').reset();
                        				NS.cmbDivisaOriginal.reset();
                        				NS.storeFormaPago.removeAll();
                        				NS.rubroActual= 0;
                        				NS.idPolizaParametro=0; 
                        				NS.grupo= 0;
                        				NS.habilitarComponentesPerfilEmpresa();
                        				NS.mostrarFormaPago();
                        			}
                    	   			/*if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	                        			valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdTipoPoliza',NS.cmbTipoPolizas.getId());
	                        			
	                        			if(!(valueCombo != null && valueCombo != undefined && valueCombo != ''))
	                        				
	                        				Ext.getCmp(PF + 'txtIdTipoPoliza').reset();
                    	   			}else
                    	   				NS.cmbTipoPolizas.reset();
                    	   			*/
                    	   			
                        		}
                        	}
                        }
                        
                    },
                    NS.cmbTipoPolizas,
                    NS.cmbPlantilla,
                    {
                        xtype: 'label',
                        text: 'Texto de cabecera:',
                        name: PF+'labelTextoCabeceraV',
                        id: PF+'labelTextoCabeceraV',
                        hidden: true,
                        x: 10,
                        y: 235
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 255,
                        width: 200,
                        indexTab: 31,
                        id: PF+'txtTextDec',
                        name:PF+'txtTextDec',//texto cabecera
                        hidden:true,
                        maxLength: 25,
  	              	    enableKeyEvents:true,
  	              		listeners:{
  	              			keypress:{
  	              	 			fn:function(caja, e) {
  	              	 				if(e.keyCode ==23){ 
  	              	 					NS.buscar();
  	              	 				}
  	              	 			}
  	              	 		},
  	              	 		keydown:{
  	              	 			fn:function(caja, e) {
  	              	 				if(!caja.isValid()){
  	              	 					caja.setValue(caja.getValue().substring(0, 25));
  	              	 				}
  	              	 			}
  	              	 		},
  	              	 		keyup:{
  	              	 			fn:function(caja, e) {	 				
  	              	 				if(!caja.isValid()){
  	              	 					caja.setValue(caja.getValue().substring(0, 25));
  	              	 				}
  	              	 				
  	              	 			}
  	              	 		},
  	              	 		
  	              		}
                    }
                    ,
                    {
                        xtype: 'label',
                        text: 'Asignacion:',
                        name: PF+'labelAsignacionV',
                        id: PF+'labelAsignacionV',
                        x: 10,
                        hidden: true, //true
                        y: 285
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 300,
                        width: 200,
                        indexTab: 32,
                        id: PF+'txtAsignacion',
                        name:PF+'txtAsignacion',
                        hidden:true,
                        maxLength: 18,
                		enableKeyEvents:true,
                		listeners:{
                			keypress:{
                	 			fn:function(caja, e) {
                	 				if(e.keyCode ==16){ 
                	 					NS.buscar();
                	 				}
                	 			}
                	 		},
                	 		keydown:{
                	 			fn:function(caja, e) {
                	 				if(!caja.isValid()){
                	 					caja.setValue(caja.getValue().substring(0, 18));
                	 				}
                	 			}
                	 		},
                	 		keyup:{
                	 			fn:function(caja, e) {	 				
                	 				if(!caja.isValid()){
                	 					caja.setValue(caja.getValue().substring(0, 18));
                	 				}
                	 				
                	 			}
                	 		},
                	 		
                		}
                  }, 
                  {
                      xtype: 'textfield',//centroCostos
                      x: 250,
                      y: 255,
                      width: 100,
                      indexTab: 32,
                      id: PF+'txtSolicitaa',  
                      name: PF+'txtSolicitaa',
                      hidden:true,
                      maxLength: 6,
	              	  enableKeyEvents:true,
	              		listeners:{
	              			keypress:{
	              	 			fn:function(caja, e) {
	              	 				if(e.keyCode ==4){ 
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
	              	 		
	              		}
                      
                  },
                  {
                      xtype: 'textfield',
                      x: 10,
                      y: 440,
                      width: 100,
                      indexTab: 32,
                      //maxLengthText: 15,
                      id: PF+'txtSolicita',    // ANTES SOLICITA AHORA SOCIEDAD GL 
                      name: PF+'txtSolicita',
                      
                      hidden:true,
                      maxLength: 6,
	              	  enableKeyEvents:true,
	              		listeners:{
	              			keypress:{
	              	 			fn:function(caja, e) {
	              	 				if(e.keyCode ==4){ 
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
	              	 		
	              		}
                  },
                  
                  {
                      xtype: 'uppertextfield',
                      x: 250,
                      y: 440,
                      width: 100,
                      indexTab: 33,
                      //maxLengthText: 15,
                      //hidden: true,
                      id: PF+'txtAutoriza',  
                      name: PF+'txtAutoriza',
                      hidden:true,
                      maxLength: 4,
	              	  enableKeyEvents:true,
	              		listeners:{
	              			keypress:{
	              	 			fn:function(caja, e) {
	              	 				if(e.keyCode ==2){ 
	              	 					NS.buscar();
	              	 				}
	              	 			}
	              	 		},
	              	 		keydown:{
	              	 			fn:function(caja, e) {
	              	 				if(!caja.isValid()){
	              	 					caja.setValue(caja.getValue().substring(0, 4));
	              	 				}
	              	 			}
	              	 		},
	              	 		keyup:{
	              	 			fn:function(caja, e) {	 				
	              	 				if(!caja.isValid()){
	              	 					caja.setValue(caja.getValue().substring(0, 4));
	              	 				}
	              	 				
	              	 			}
	              	 		},
	              	 		
	              		}
                  },
                  {
                      xtype: 'label',
                      text: 'Sociedad Gl:',
                      name: PF+'labelSolicitaV',
                      id: PF+'labelSolicitaV',
                      hidden: true,
                      x: 10,
                      y: 425
                  },
                  {
                      xtype: 'label',
                      text: 'Centro de Costos:',
                      name: PF+'labelCentroCostosV',
                      id: PF+'labelCentroCostosV',
                      hidden: true,
                      x: 250,
                      y: 240
                  },
                  {
                      xtype: 'label',
                      text: 'División:',
                      name: PF+'labelDivisionV',
                      id: PF+'labelDivisionV',
                      hidden: true,
                      x: 250,
                      y: 285
                  },
                  {
                      xtype: 'label',
                      text: 'Orden:',
                      name: PF+'labelOrdenV',
                      id: PF+'labelOrdenV',
                      hidden: true,
                      x: 250,
                      y: 330
                  },
                  {
                      xtype: 'label',
                      text: 'Banco Interlocutor:',
                      name: PF+'labelAutorizaV',
                      id: PF+'labelAutorizaV',
                      hidden: true,
                      x: 250,
                      y: 425
                  },

                  NS.cmbAreaDestino
                  ,
                  {
	                  xtype: 'label',
	                  text: 'Area Destino:',
	                  name: PF+'labelAreaDestinoV',
                      id: PF+'labelAreaDestinoV',
                      hidden: true,
	                  x: 650,
	                  y: 285
	              },
	              {
                      xtype: 'label',
                      text: 'Referencia de pago:',
                      name: PF+'labelReferenciaPagoV',
                      id: PF+'labelReferenciaPagoV',
                      hidden: true,
                      x: 10,
                      y: 330
                  },
                  {
                 	 xtype: 'textfield',
                      x: 10,
                      y: 345,
                      width: 200,
                      indexTab: 36,
                      id: PF+'txtTextCabeceraN',  //refe pago
                      name:PF+'txtTextCabeceraN',
                      hidden:true,
                      maxLength: 30,
	              	  enableKeyEvents:true,
	              		listeners:{
	              			keypress:{
	              	 			fn:function(caja, e) {
	              	 				if(e.keyCode ==28){ 
	              	 					NS.buscar();
	              	 				}
	              	 			}
	              	 		},
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
                    
                 },
                 {
                 	 xtype: 'textfield',
             	 		x: 250,
             	 		y: 300,
                      width: 100,
                      //indexTab: 36,
                      id: PF+'txtTextDivisioninf',  
                      name:PF+'txtTextDivisioninf',
                      hidden:true,
                      maxLength: 4,
	              	  enableKeyEvents:true,
	              		listeners:{
	              			keypress:{
	              	 			fn:function(caja, e) {
	              	 				if(e.keyCode ==2){ 
	              	 					NS.buscar();
	              	 				}
	              	 			}
	              	 		},
	              	 		keydown:{
	              	 			fn:function(caja, e) {
	              	 				if(!caja.isValid()){
	              	 					caja.setValue(caja.getValue().substring(0, 4));
	              	 				}
	              	 			}
	              	 		},
	              	 		keyup:{
	              	 			fn:function(caja, e) {	 				
	              	 				if(!caja.isValid()){
	              	 					caja.setValue(caja.getValue().substring(0, 4));
	              	 				}
	              	 				
	              	 			}
	              	 		},
	              	 		
	              		}
                 },
                 {
                 	 xtype: 'textfield',
             	 		x: 250,
             	 		y: 345,
                      width: 100,
                      //indexTab: 36,
                      id: PF+'txtTextOrdeninf',  
                      name:PF+'txtTextOrdeninf',                     
                      hidden:true,
                      maxLength: 9,
	              	  enableKeyEvents:true,
	              		listeners:{
	              			keypress:{
	              	 			fn:function(caja, e) {
	              	 				if(e.keyCode ==7){ 
	              	 					NS.buscar();
	              	 				}
	              	 			}
	              	 		},
	              	 		keydown:{
	              	 			fn:function(caja, e) {
	              	 				if(!caja.isValid()){
	              	 					caja.setValue(caja.getValue().substring(0, 9));
	              	 				}
	              	 			}
	              	 		},
	              	 		keyup:{
	              	 			fn:function(caja, e) {	 				
	              	 				if(!caja.isValid()){
	              	 					caja.setValue(caja.getValue().substring(0, 9));
	              	 				}
	              	 				
	              	 			}
	              	 		},
	              	 		
	              		}
                 },
                 {
                     xtype: 'label',
                     text: 'Referencia:',
                     name: PF+'labelReferenciaV',
                     id: PF+'labelReferenciaV',
                     x: 10,
                     hidden: true,
                     y: 380
                 },
                 {
                     xtype: 'button',
                     text: 'Ejecutar',
                     x: 870,
                     y: 395,
                     width: 90,
                     height: 22,
                     id: PF+'btnGenerarDocumentoP',
                     hidden: false,
                     name: PF+'btnGenerarDocumentoP',
                     listeners:{
                     	click:{
                     		fn:function(e){
                     			Ext.MessageBox.show({
                     		       title : 'Información SET',
                     		       msg : 'Generando Documento, espere por favor...',
                     		       width : 300,
                     		       wait : true,
                     		       progress:true,
                     		       waitConfig : {interval:200}//,
                     		       
                     	   		});
                     			NS.controlFechas(apps.SET.FEC_HOY, 'load');
                     			//matriz cabecera
                     			var cabecera = {};
                     			//alert(cabecera.idEmpresa=Ext.getCmp(PF + 'txtEmpresa').getValue());
                     			//alert(Ext.getCmp(PF + 'chkNuevopoliza').getValue()+" este es el id de empresa");
	                   			var matrizCabecera = new Array ();
	                   			cabecera.idEmpresa=Ext.getCmp(PF + 'txtEmpresa').getValue();
	                   			//cabecera.idEmpresa=NS.idEmpresa;
	                   			//cabecera.tipoPoliza=Ext.getCmp(PF + 'txtIdTipoPoliza').getValue();
	                   			cabecera.tipoPoliza=0;
	                   			cabecera.idGrupo=Ext.getCmp(PF + 'txtIdGrupo').getValue();
	                   			cabecera.idRubro=Ext.getCmp(PF + 'txtIdRubroAl').getValue();
	                   			cabecera.idPersona=NS.idPersona;
	                   			cabecera.idFormaPago=NS.idFormaPago;
	                   			cabecera.fechaFactura = cambiarFecha('' + Ext.getCmp(PF + 'fechaFactura').getValue());
	                   			cabecera.importeOriginal = NS.unformatNumber(Ext.getCmp(PF + 'txtImporteOriginal').getValue());
	                   			cabecera.idDivOriginal=NS.idDivOriginal;
	                   			cabecera.polizaContable = Ext.getCmp(PF + 'chkNuevopoliza').getValue();
	                   			//transferencias
	                   			cabecera.optNac=NS.optNac;
	                   			cabecera.optExt=NS.optExt;
	               //    			cabecera.idBancoBenef=NS.idBancoBenef ;   
	                   			
//	                   			if(NS.idFormaPago==3){
//	                   				//cabecera.idChequera = '' + NS.storeChequeras.getById(NS.cmbChequeras.getValue()).get('id');
//	                   				cabecera.idChequera = '' + NS.idChequera;
//		                   			cabecera.clabe = Ext.getCmp(PF + 'txtClabe').getValue();
//		                   			cabecera.sucursal = Ext.getCmp(PF + 'txtSucursal').getValue();
//		                   			cabecera.plaza = Ext.getCmp(PF + 'txtPlaza').getValue();
//	                   			}else{
//	                   				cabecera.idChequera = 0;
//		                   			cabecera.clabe = 0;
//		                   			cabecera.sucursal = 0;
//		                   			cabecera.plaza = 0;
//	                   			}
//	                   			
									//cheque
	                   			cabecera.idCaja=NS.idCaja ;
	                   			cabecera.leyenda = NS.chkLeyenda;
	                   			cabecera.idUsuario=NS.idUsuario;
	                   			cabecera.nomPersona = Ext.getCmp(PF + 'cmbBeneficiario').getRawValue();
//	                   			
//	                   			cabecera.idChequeraP=0;
//	                   			cabecera.idBancoP=0;
	                   			matrizCabecera[0] = cabecera;
	                   			//var jsonStringCabecera = Ext.util.JSON.encode(matrizCabecera);	
								var jsonStringCabecera ="";
								
									CapturaSolicitudesPagoAction.obtieneCheqPagadora(parseInt(cabecera.idEmpresa),cabecera.idDivOriginal,parseInt(cabecera.idFormaPago), function(result, e){
										if(result!== null && result!== ''){
											//Ext.Msg.alert('chequera', '' + result.idChequera);
											
											if(NS.idFormaPago==3){
												//Ext.Msg.alert("set","pago igual a 3");
				                   				//cabecera.idChequera = '' + NS.storeChequeras.getById(NS.cmbChequeras.getValue()).get('id');
				                   				cabecera.idChequera = '' + NS.idChequera;
				                   				cabecera.idBancoBenef = '' + NS.idBancoBenef;
				                   				cabecera.idChequeraP=result.idChequera;
												cabecera.idBancoP=result.idBanco;
					                   			cabecera.clabe = Ext.getCmp(PF + 'txtClabe').getValue();
					                   			cabecera.sucursal = Ext.getCmp(PF + 'txtSucursal').getValue();
					                   			cabecera.plaza = Ext.getCmp(PF + 'txtPlaza').getValue();
					                   			matrizCabecera.concat(cabecera);
											}else{
				                   				//Ext.Msg.alert("set","otro pago");
				                   				cabecera.idChequera=0;
				                   				cabecera.idBancoBenef=NS.idBancoBenef ;
				                   				cabecera.idChequeraP=result.idChequera;
												cabecera.idBancoP=result.idBanco;
												cabecera.clabe = 0;
					                   			cabecera.sucursal = 0;
					                   			cabecera.plaza = 0;
												matrizCabecera.concat(cabecera);
				                   				
				                   			}	
										}
										jsonStringCabecera = Ext.util.JSON.encode(matrizCabecera);	
										//Ext.Msg.alert("array",jsonStringCabecera);
										//matriz parametros
		                     			var parametros = {};
		                   			var matrizParametros = new Array ();
		                   			parametros.i0=Ext.getCmp(PF + 'txtIdRubroAl').getValue();
		                   			parametros.i1=Ext.getCmp(PF + 'txtIdTipoPoliza').getValue();
		                   			parametros.i2=Ext.getCmp(PF + 'txtIdGrupo').getValue();
		                   			
		                   			matrizParametros[0] = parametros; 
										var jsonStringParam = Ext.util.JSON.encode(matrizParametros);	
										//Ext.Msg.alert("SET",jsonStringParam);
		                   			
		                     			var regPlantilla = {};
		                   			var matrizPlantilla = new Array ();
		                   			regPlantilla.i0=Ext.getCmp(PF + 'txtConcepto').getValue();//texto
		                   			regPlantilla.i1 = cambiarFecha('' + Ext.getCmp(PF + 'fechaPago').getValue());
		                   			regPlantilla.i2=Ext.getCmp(PF + 'txtIdDivisaPago').getValue();
		                   			regPlantilla.i3='1';
		                   			//regPlantilla.i4=Ext.getCmp(PF + 'txtImportePago').getValue();
		                   			regPlantilla.i4=NS.unformatNumber(Ext.getCmp(PF + 'txtImporteOriginal').getValue());
		                   			regPlantilla.i5=Ext.getCmp(PF + 'txtTextDec').getValue();
		                   			regPlantilla.i6=Ext.getCmp(PF + 'txtAsignacion').getValue();
		                   			regPlantilla.i7=Ext.getCmp(PF + 'txtTextReferencia').getValue();
		                   			regPlantilla.i8=Ext.getCmp(PF + 'txtTextCabeceraN').getValue();
		                   			regPlantilla.i9=Ext.getCmp(PF + 'txtSolicita').getValue();
		                   			
		                   			regPlantilla.i10=Ext.getCmp(PF + 'txtSolicitaa').getValue();
		                   			regPlantilla.i11=Ext.getCmp(PF + 'txtTextDivisioninf').getValue();
		                   			regPlantilla.i12=Ext.getCmp(PF + 'txtTextOrdeninf').getValue();
		                   			regPlantilla.i13=Ext.getCmp(PF + 'txtAutoriza').getValue();
		                   			regPlantilla.i14=Ext.getCmp(PF + 'txtObservaciones').getValue();
		                   			regPlantilla.i15='1';
		                   			regPlantilla.i16='0';
		                   			
		                   			matrizPlantilla[0] = regPlantilla; 
									var jsonString = Ext.util.JSON.encode(matrizPlantilla);	
										//Ext.Msg.alert('Información SET', jsonStringParam );
									CapturaSolicitudesPagoAction.generarDocumento(jsonString, jsonStringParam, jsonStringCabecera, function(result, e){
										if(result.msgUsuario !== null && result.msgUsuario !== ''){										Ext.Msg.alert('Información SET', '' + result.msgUsuario);
										} 
										
									});
										
								});
//									
//									//matriz parametros
//	                     			var parametros = {};
//	                   			var matrizParametros = new Array ();
//	                   			parametros.i0=Ext.getCmp(PF + 'txtIdRubroAl').getValue();
//	                   			parametros.i1=Ext.getCmp(PF + 'txtIdTipoPoliza').getValue();
//	                   			parametros.i2=Ext.getCmp(PF + 'txtIdGrupo').getValue();
//	                   			
//	                   			matrizParametros[0] = parametros; 
//									var jsonStringParam = Ext.util.JSON.encode(matrizParametros);	
//									//Ext.Msg.alert("SET",jsonStringParam);
//	                   			
//	                     			var regPlantilla = {};
//	                   			var matrizPlantilla = new Array ();
//	                   			regPlantilla.i0=Ext.getCmp(PF + 'txtConcepto').getValue();//texto
//	                   			regPlantilla.i1 = cambiarFecha('' + Ext.getCmp(PF + 'fechaPago').getValue());
//	                   			regPlantilla.i2=Ext.getCmp(PF + 'txtIdDivisaPago').getValue();
//	                   			regPlantilla.i3='1';
//	                   			//regPlantilla.i4=Ext.getCmp(PF + 'txtImportePago').getValue();
//	                   			regPlantilla.i4=NS.unformatNumber(Ext.getCmp(PF + 'txtImporteOriginal').getValue());
//	                   			regPlantilla.i5=Ext.getCmp(PF + 'txtTextDec').getValue();
//	                   			regPlantilla.i6=Ext.getCmp(PF + 'txtAsignacion').getValue();
//	                   			regPlantilla.i7=Ext.getCmp(PF + 'txtTextReferencia').getValue();
//	                   			regPlantilla.i8=Ext.getCmp(PF + 'txtTextCabeceraN').getValue();
//	                   			regPlantilla.i9=Ext.getCmp(PF + 'txtSolicita').getValue();
//	                   			
//	                   			regPlantilla.i10=Ext.getCmp(PF + 'txtSolicitaa').getValue();
//	                   			regPlantilla.i11=Ext.getCmp(PF + 'txtTextDivisioninf').getValue();
//	                   			regPlantilla.i12=Ext.getCmp(PF + 'txtTextOrdeninf').getValue();
//	                   			regPlantilla.i13=Ext.getCmp(PF + 'txtAutoriza').getValue();
//	                   			regPlantilla.i14=Ext.getCmp(PF + 'txtObservaciones').getValue();
//	                   			regPlantilla.i15='1';
//	                   			regPlantilla.i16='0';
//	                   			
//	                   			matrizPlantilla[0] = regPlantilla; 
//								var jsonString = Ext.util.JSON.encode(matrizPlantilla);	
//									//Ext.Msg.alert('Información SET', jsonStringParam );
//								CapturaSolicitudesPagoAction.generarDocumento(jsonString, jsonStringParam, jsonStringCabecera, function(result, e){
//									if(result.msgUsuario !== null && result.msgUsuario !== ''){										Ext.Msg.alert('Información SET', '' + result.msgUsuario);
//									} 
//								});
	             			}
	             		}
                     }
                 },
	              
                 {
                	 xtype: 'textfield',
                     x: 10,
                     y: 395,
                     width: 160,
                     indexTab: 37,
                     id: PF+'txtTextReferencia',
                     name:PF+'txtTexReferencia',//refencia
                     hidden:true,
                     maxLength: 16,
             		enableKeyEvents:true,
             		listeners:{
             			keypress:{
             	 			fn:function(caja, e) {
             	 				if(e.keyCode ==14){ 
             	 					NS.buscar();
             	 				}
             	 			}
             	 		},
             	 		keydown:{
             	 			fn:function(caja, e) {
             	 				if(!caja.isValid()){
             	 					caja.setValue(caja.getValue().substring(0, 16));
             	 				}
             	 			}
             	 		},
             	 		keyup:{
             	 			fn:function(caja, e) {	 				
             	 				if(!caja.isValid()){
             	 					caja.setValue(caja.getValue().substring(0, 16));
             	 				}
             	 				
             	 			}
             	 		},
             	 		
             		}
                },
                {
               	 xtype: 'textfield',
                    x: 640,
                    y: 15,
                    width: 30,
                    //indexTab: 37,
                    id: PF+'txtTextPlantilla',
                    name:PF+'txtTextPlantilla',
                    hidden:true,
                    listeners:{
                      	change:{
                      		fn: function(caja,valor) {
                      			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                    				NS.storePlantilla.baseParams.condicion=' Id_Usuario = '+NS.idUsuario+' and ID_PLANTILLA = '+caja.getValue();
                    				NS.storePlantilla.load();
                   				}else
                   					NS.cmbPlantilla.reset();
                      			
                      		}
                      	}
                  }
               },
	              {
                      xtype: 'container',
                      x: 530,
                      y: 400,
                      width: 300,
                      height: 65,
                      layout: 'absolute',
                      items: [
                          {
                              xtype: 'textarea',
                              width: 295,
                              height: 60,
                              id: PF+'txtObservaciones', //alberto1
                              name: PF+'txtObservaciones',
                              hidden: true,
                              maxLength: 50,
                       		enableKeyEvents:true,
                       		listeners:{
                       			keypress:{
                       	 			fn:function(caja, e) {
                       	 				if(e.keyCode ==48){ 
                       	 					NS.buscar();
                       	 				}
                       	 			}
                       	 		},
                       	 		keydown:{
                       	 			fn:function(caja, e) {
                       	 				if(!caja.isValid()){
                       	 					caja.setValue(caja.getValue().substring(0, 50));
                       	 				}
                       	 			}
                       	 		},
                       	 		keyup:{
                       	 			fn:function(caja, e) {	 				
                       	 				if(!caja.isValid()){
                       	 					caja.setValue(caja.getValue().substring(0, 50));
                       	 				}
                       	 				
                       	 			}
                       	 		},
                       	 		
                       		}
                          }
                      ]
                  },
                  

                  {
                      xtype: 'label',
                      text: 'Observaciones:',
                      name: PF+'labelObservacionesV',
                      id: PF+'labelObservacionesV',
                      hidden: true,
                      x: 530,
                      y: 385
                  }
                  //inicio documento
                  ,{
                      xtype: 'fieldset',
                  	title: 'Documento',
                      width: 230,
                      height: 130,
                      name: PF+'panelDocumentoV',
                      id: PF+'panelDocumentoV',
                      hidden: true,
                      x: 390,
                      y: 230,
                      layout: 'absolute',
                      items: [
                          {
                          	 xtype: 'textfield',
                               x: 10,
                               y: 60,
                               width: 100,
                               id: PF+'txtFolion',
                               name:PF+'txtFolion',
                               hidden:false
                          },
                          {
                              xtype: 'button',
                              text: 'Generar No. Documento',
                              x: 10,
                              y: 10,
                              width: 180,
                              height: 22,
                              id: PF+'btnGenerarDocumento',
                              hidden: false,
                              name: PF+'btnGenerarDocumento',
                              listeners:{
                              	click:{
                              		fn:function(e){
                              			//matriz cabecera
	                      			}
                          		}
                              }
                          },
                          {
                              xtype: 'button',
                              text: 'Consultar',
                              x: 10,
                              y: 40,
                              width: 100,
                              height: 22,
                              id: PF+'btnConsultarDocumento',
                              hidden: true,
                              name: PF+'btnConsultarDocumento',
                              listeners:{
                              	click:{
                              		fn:function(e){
                              			}
                              		}
                              }
                          }
                          ]
                      },
                    ///fin de codigo nuevo
                      {
                          xtype: 'textfield',
                          x: 10,
                          y: 15,
                          width: 50,
                          tabIndex: 0,
                          id: PF+'txtEmpresa',
                          name:PF+'txtEmpresa',
                          value: apps.SET.NO_EMPRESA,
                      	listeners:{
                          	change:{
                          		fn: function(caja,valor) {
                          			if(NS.existeEnStored(NS.storeEmpresas, caja.getValue())){
                          				NS.cmbEmpresas.setValue(caja.getValue());
                          				NS.seleccionaEmpresa();
                          			}else{
                          				Ext.getCmp(PF + 'txtEmpresa').reset();
                          				NS.cmbEmpresas.reset();
                          			}
                      				/*if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                          				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresas.getId());
  	                    				
  	                        			if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
  	                        				NS.noEmpresaTmp = caja.getValue();
  	                						NS.nomEmpresaTmp = NS.storeEmpresas.getById(parseInt(caja.getValue())).get('nomEmpresa');
  	                						
  	                        				NS.accionarEmpresas(valueCombo);
  	                        			}else
  	                        				Ext.getCmp(PF + 'txtEmpresa').reset();
                      				}else
  	                    				NS.cmbEmpresas.reset();*/
                          		}
                          	}
                          }
                      },
                    
                   NS.cmbEmpresas,
                   NS.cmbBeneficiario,
                   {
                       xtype: 'checkbox',
                       id:PF+'chkNuevopoliza',
                       name:PF+'chkNuevopoliza',
                       x: 740,
                       y: 110,
                       boxLabel: 'Pago Sin Poliza Contable',
                       hidden: true,
                       checked: true,
                       readOnly: true,
                       disabled: true,
                       listeners:{
                       	check:{
                       		fn:function(checkBox,valor)
                       		{
                       			
                       		}
                       	}
                       }
                   },
                   {
                        xtype: 'textfield',
                        x: 500,
                        y: 15,
                        width: 80,
                        tabIndex: 2,
                        id: PF+'txtNoDocto',
                        name:PF+'txtNoDocto',
                        hidden:true
                    },
                    {
                        xtype: 'textfield',
                        x: 340,
                        y: 15,
                        width: 100,
                        tabIndex: 3,
                        id: PF+'txtIdBeneficiario',
                        name: PF+'txtIdBeneficiario',
                        listeners:{
                        	change:{
                        		fn:function(caja, valor){
                        			Ext.Msg.alert('m',caja.getValue());
                        			if(NS.existeEnStored(NS.storeBeneficiario, caja.getValue())){
                        				NS.cmbBeneficiario.setValue(caja.getValue());
                            			NS.seleccioneBaneficiario(parseInt(caja.getValue()));
                    					NS.mostrarFormaPago('');
                        			}else{
                        				Ext.getCmp(PF + 'txtIdBeneficiario').reset();
                        				NS.cmbBeneficiario.reset();
                        				NS.cmbFormaPago.reset();
                        				Ext.getCmp(PF + 'txtIdFormaPago').reset();
                        				Ext.getCmp(PF + 'txtIdDivisaOriginal').reset();
                        				NS.cmbDivisaOriginal.reset();
                        				NS.mostrarFormaPago();
                        			}
                        		}
                        	}
                        }
                    }
                    ,
                    {
                        xtype: 'uppertextfield',
                        x: 500,
                        y: 115,
                        width: 35,
                        tabIndex: 9,
                        id: PF+'txtIdDivisaOriginal',
                        name: PF+'txtIdDivisaOriginal',
                        value: 'MN',
                        listeners:{
                        	change:{
                        		fn:function(caja){
                    				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisaOriginal', NS.cmbDivisaOriginal.getId());
	                        			
		                        		if(valueCombo != null && valueCombo != undefined && valueCombo != '')
		                        			NS.accionarDivisaOriginal(valueCombo);
		                        		else
		                        			Ext.getCmp(PF + 'txtIdDivisaOriginal').reset();
                    				}else
                    					NS.cmbDivisaOriginal.reset();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'textfield',
                        //x: 390,
                        x: 290,
                        y: 115,
                        width: 90,
                        tabIndex: 8,
                       // regex: /^[0-9]{1,15}(\.[0-9]{1,2})?$/,
                        id: PF+'txtImporteOriginal',
                        name:PF+'txtImporteOriginal',
                        listeners:{
                        	change:{
                        		fn:function(caja,valor){
                        			 //if (!/^([0-9])+.{1|0}[0-9]{0|2}$/.test(caja.getValue())){
                        			if ((caja.getValue().match('^[0-9]{1,15}(\.[0-9]{1,2})?$'))==null){
                        				 Ext.getCmp(PF+'txtImporteOriginal').setValue('');
                        				 Ext.Msg.alert('Información SET', 'El valor del importe tiene que ser numerico, o tener una longitud de 15 caracteres con dos decimales.');
                        			 }else{
                        				 Ext.getCmp(PF+'txtImporteOriginal').setValue(NS.formatNumber(caja.getValue()));
                        				 Ext.getCmp(PF+'txtImportePago').setValue(NS.formatNumber(caja.getValue()));
                        				// Ext.getCmp(PF+'txtImportePartida').setValue(NS.formatNumber(caja.getValue()));
                        				 
                        			 }
                        			
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha factura:',
                        hidden: false,
                        x: 820,
                        y: 100
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha pago:',
                        hidden: false,
                        x: 710,
                        y: 100
                    },
                    {
                        xtype:'datefield',
                   		format:'d/m/Y',
                        x: 820,
                        y: 115,
                        width: 100,
                        tabIndex: 7,
                        hidden: false,
                        id: PF+'fechaFactura',
                        name: PF+'fechaFactura',
                        value: apps.SET.FEC_HOY,
                        
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 115,
                        width: 35,
                        tabIndex: 5,
                        id: PF+'txtIdFormaPago',
                        name:PF+'txtIdFormaPago',
                        listeners:{
                        	change:{
                        		fn:function(caja, valor){
                        			if(NS.existeEnStored(NS.storeFormaPago, caja.getValue())){
                        				NS.cmbFormaPago.setValue(caja.getValue());
                            			NS.seleccionaFormaDePago(caja.getValue());
                        			}else{
                        				NS.cmbFormaPago.reset();
                        				Ext.getCmp(PF + 'txtIdFormaPago').reset();
                        				NS.mostrarFormaPago();
                        			}
                        			
                    				/*var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdFormaPago', NS.cmbFormaPago.getId());
                    				NS.mostrarFormaPago(valueCombo);
                    				
                    				if(caja.getValue() == null || caja.getValue() == undefined || caja.getValue() == ''){
                    					NS.cmbFormaPago.reset();
                    				}
                    					
                    				if(valueCombo == null || valueCombo == undefined || valueCombo == ''){
                    					Ext.getCmp(PF + 'txtIdFormaPago').reset();
                    				}*/
                        		}
                        	}
                        }
                    },
                    
                      NS.cmbFormaPago,
                      NS.cmbDivisaOriginal,
                    {
                        xtype: 'textfield',
                        x: 700,
                        y: 200,
                        width: 120,
                        tabIndex: 17,
                        id: PF+'txtImportePago',
                        name: PF+'txtImportePago',
                        hidden: true,
                        readOnly: true
                    },
                    {
                        xtype:'datefield',
                   		format:'d/m/Y',
                        x: 710,
                        y: 115,
                        width: 100,
                        tabIndex: 13,
                        id: PF+'fechaPago',
                        name: PF+'fechaPago',
                        hidden: false,
                        value: apps.SET.FEC_HOY,
                        //editable: false,

                        listeners:{
                      	change:{
                      		fn:function(caja, valor){
                      			var aux = cambiarFecha('' + caja.getValue());
                      			
                      			if(apps.SET.FEC_HOY > aux){
                      				Ext.Msg.alert('SET', 'La fecha de pago no puede ser menor a la facha actual.!!');
                      				Ext.getCmp(PF + 'fechaPago').setValue(apps.SET.FEC_HOY);
                      			}
                      				
              				}
              			},
                    
                    	select:{
                    		fn:function(caja, valor){
                    			NS.controlFechas(cambiarFecha(''+valor), 'select');
                    			
            				}
            				}
                        }

                        
                    },
                    {
                        xtype: 'label',
                        text: 'Concepto:',
                        hidden: false,
                        x: 400,
                        y: 190
                    }
                    ,
                    {
                        xtype: 'uppertextfield',
                        x: 400,
                        y: 210,
                        width: 420,
                        tabIndex: 12,
                        id: PF+'txtConcepto',
                        name: PF+'txtConcepto',
                        hidden: false,
                        maxLength: 50/*,
                		enableKeyEvents:true,
                		listeners:{
                			keypress:{
                	 			fn:function(caja, e) {
                	 				if(e.keyCode ==48){ 
                	 					NS.buscar();
                	 				}
                	 			}
                	 		},
                	 		keydown:{
                	 			fn:function(caja, e) {
                	 				if(!caja.isValid()){
                	 					caja.setValue(caja.getValue().substring(0, 50));
                	 				}
                	 			}
                	 		},
                	 		keyup:{
                	 			fn:function(caja, e) {	 				
                	 				if(!caja.isValid()){
                	 					caja.setValue(caja.getValue().substring(0, 50));
                	 				}
                	 				
                	 			}
                	 		},
                	 		
                		}*/
                    },
                    ///
//                    {
//                        xtype: 'label',
//                        text: 'Grupo:',
//                        hidden: false,
//                        x: 0,
//                        y: 250
//                    }
//                    ,
                    NS.cmbGrupo
                    ,
//                    {
//                        xtype: 'label',
//                        text: 'Rubro:',
//                        hidden: false,
//                        x: 200,
//                        y: 250
//                    }
//                    ,
                    {
                        xtype: 'fieldset',
                    	title: '',
                        width: 210,
                        height: 50,
                        name: PF+'panelDivisaPagoV',
                        id: PF+'panelDivisaPagoV',
                        x: 380,
                        y: 190,
                        border: false,
                        hidden: true,
                        layout: 'absolute',
                        items: [
                            
                            	{
                                    xtype: 'uppertextfield',
                                    x: 0, // x: 390,
                                    y: 0, //y: 200,
                                    width: 35,
                                    tabIndex: 14,
                                    id: PF+'txtIdDivisaPago',
                                    name:PF+'txtIdDivisaPago',
                                    value: 'MN',
                                    listeners:{
                                    	change:{
                                    		fn:function(caja, valor) {
            	                    			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
            	                    				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisaPago', NS.cmbDivisaPago.getId());
            	                    				
            	                    				if(valueCombo != null && valueCombo != undefined && valueCombo != '')
            	                    					NS.accionarDivisaPago(valueCombo);
            	                    				else
            	                    					Ext.getCmp(PF + 'txtIdDivisaPago').reset();
            	                    			}else {
            	                    				NS.cmbDivisaPago.reset();
            	                    				Ext.getCmp(PF + 'txtTipoCambio').setValue('');
            	                    				Ext.getCmp(PF + 'txtImportePago').setValue(Ext.getCmp(PF+'txtImporteOriginal').getValue());
            	                    			}
                                    		}
                                    	}
                                    }
                                },
                                NS.cmbDivisaPago
                            	
                            
                            ]
                    },
                    
                    ///
                    
                    {
                        xtype: 'textfield',
                        x: 600,
                        y: 200,
                        width: 80,
                        tabIndex: 16,
                        id: PF+'txtTipoCambio',
                        name: PF+'txtTipoCambio', 
                        hidden: true,
                       	readOnly: true,
                       	value: '1'
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 780,
                        y: 15,
                        width: 80,
                        height: 22,
                        id: PF+'btnBuscar',
                        hidden: true,
                        name: PF+'btnBuscar',
                        listeners:{
                        	click:{
                        		fn:function(e){
                        		
                        	  }
                        	}
                        }
                    }
                    ,{
	                        xtype: 'button',
	                        text: 'Buscar',
	                        x: 890,
	                        y: 15,
	                        width: 60,
	                        height: 22,
	                        id: PF+'btnComboBenef',
	                        name: PF+'btnComboBenef',
	                        listeners:{
	                        	click:{
	                        		fn:function(e){
	                        		if(NS.parametroBus===''){
	                        			//Ext.Msg.alert('Información SET','Es necesario agregar una letra o nombre');
	                        			NS.storeBeneficiario.baseParams.condicion=NS.parametroBus;
	                        			NS.storeBeneficiario.baseParams.poliza = Ext.getCmp(PF + 'txtIdTipoPoliza').getValue();
	                        			NS.storeBeneficiario.baseParams.grupo = Ext.getCmp(PF + 'txtIdGrupo').getValue();
	                        			NS.storeBeneficiario.baseParams.rubro = Ext.getCmp(PF + 'txtIdRubroAl').getValue();
	                        			NS.storeBeneficiario.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
	                        			NS.storeBeneficiario.load();
	                        			NS.parametroBus='';
	                        		}else{
	                        			NS.storeBeneficiario.baseParams.condicion=NS.parametroBus;
	                        			NS.storeBeneficiario.baseParams.poliza = Ext.getCmp(PF + 'txtIdTipoPoliza').getValue();
	                        			NS.storeBeneficiario.baseParams.grupo = Ext.getCmp(PF + 'txtIdGrupo').getValue();
	                        			NS.storeBeneficiario.baseParams.rubro = Ext.getCmp(PF + 'txtIdRubroAl').getValue();
	                        			NS.storeBeneficiario.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
	                        			NS.storeBeneficiario.load();
	                        			NS.parametroBus='';
	                        		}
	                       		}
	                   		}
	               		}
               		},{
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 10,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Beneficiario:',
                        x: 340,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Plantilla:',
                        x: 640,
                        y: 50,
                        hidden: true
                    },
                    {
                        xtype: 'label',
                        text: 'Forma Pago:',
                        x: 10,
                        y: 100
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha factura:',
                        hidden: true,
                        x: 260,
                        y: 100
                    },
                    {
                        xtype: 'label',
                        //text: 'Importe Original:',
                        text: 'Importe del pago:',
                        x: 290,
                        //x: 390,
                        y: 100
                    },
                    {
                        xtype: 'label',
                        text: 'Divisa:',
                         x: 500,
  						 y: 100
                    },
                    {
                        xtype: 'checkbox',
                        x: 725,
                        y: 15,
                        tabIndex: 11,
                        boxLabel: 'Contabilizar',
                        hidden: true,
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        			if(valor==true)
                        			{
	                     				NS.chkContabilizar='true';
                        			}
                        			else{
                        				NS.chkContabilizar='false';
                        			}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'Texto:',
                        id: PF+'labelTextoV',
                        name:PF+'labelTextoV',
                        hidden: true,
                        x: 10,
                        y: 185
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha Pago:',
                        id: PF+'labelfechaPagoV',
                        name:PF+'labelfechaPagoV',
                        hidden: true,
                        x: 260,
                        y: 185
                    },
                    {
                        xtype: 'label',
                        text: 'Divisa de Pago:',
                        id: PF+'labelDivisaPagoV',
                        name:PF+'labelDivisaPagoV',
                        hidden: true,
                        x: 390,
                        y: 185
                    },
                    { 
                        xtype: 'label',
                        text: 'Tipo Cambio:',
                        id: PF+'labelTipoCambioV',
                        name:PF+'labelTipoCambioV',
                        hidden: true,
                        x: 600,
                        y: 185
                    },
                    {
                        xtype: 'label',
                        text: 'Importe de Pago:',
                        id: PF+'labelImportePagoV',
                        name:PF+'labelImportePagoV',
                        hidden: true,
                        x: 700,
                        y: 185
                    },
                    //contenedor de tranferencia inicia
                    {
                        xtype: 'container',
                        x: 10,
                        y: 140,
                        width: 800,
                        height: 50,
                        hidden: true,
                        layout: 'absolute',
                        id: PF+'contTransferencia',
                        name:PF+'contTransferencia',
                        items: [
                       		 {
								xtype: 'radio',
								boxLabel: 'Nac',
								id: PF+'optNac',
								name: PF+'optNac',
								value: 0,
								checked: true,
								x: 0,
								y: 15,
								listeners:{
									check:{
										fn:function(opt, valor){
												
												if(valor)
												{
													Ext.getCmp(PF+'optExt').setValue(false);
												}
//												if(valor==true)
//                                   				{
//													NS.limpiaPanelTransfer();
//													
//													NS.storeBancoBenef.baseParams.noEmpresa = Ext.getCmp(PF+'cmbBancoBeneficiario').getValue();
//													NS.storeBancoBenef.baseParams.nacExt='N';
//                                   					NS.storeBancoBenef.load();
//                                   					NS.optNac='true';
//                                   				}
//                                   				else
//                                   				{
//                                   					
//                                   					NS.storeBancoBenef.baseParams.noEmpresa = Ext.getCmp(PF+'cmbBancoBeneficiario').getValue();
//													NS.storeBancoBenef.load();
//                                   					NS.optNac='false';
//                                   				}
											}
										}
									}
								},
								{
								xtype: 'radio',
								boxLabel: 'Ext',
								id: PF+'optExt',
								name: PF+'optExt',
								x: 40,
								y: 15,
								value: 1,
								listeners:{
									check:{
									fn:function(opt, valor){
											
											if(valor)
											{
												Ext.getCmp(PF+'optNac').setValue(false);
												NS.storeBancoBenef.baseParams.nacExt='E';
												NS.storeBancoBenef.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'cmbBancoBeneficiario').getValue());
												NS.storeBancoBenef.load();
											}
//											if(valor==true)
//                               				{
//												NS.limpiaPanelTransfer();
//												
//                               					NS.storeBancoBenef.baseParams.nacExt='E';
//                               					NS.storeBancoBenef.baseParams.noEmpresa = Ext.getCmp(PF+'cmbBancoBeneficiario').getValue();
//                               					NS.storeBancoBenef.load();
//                               					NS.optExt='true';
//                               				}
//                               				else{
//                               					NS.optExt='false';
//                               					
//                               				}
										}
									}
								}
							},
                            {
	                            xtype: 'textfield',
	                            x: 100,
	                            y: 15,
	                            width: 40,
	                            id: PF+'txtIdBancoBenef',
	                            name: PF+'txtIdBancoBenef',
	                            listeners:{
	                            	change:{
	                            		fn:function(caja, valor) {
	                            			if(NS.existeEnStored(NS.storeBancoBenef,caja.getValue())){
	                            				NS.cmbBancoBenef.setValue(caja.getValue());
		                            			NS.seleccionaBancoBenef();
		                
	                            			}else{
	                            				Ext.getCmp(PF + 'txtIdBancoBenef').reset();
	                            				NS.cmbBancoBenef.reset();
												NS.cmbChequeras.reset();
												NS.storeChequeras.removeAll();
												Ext.getCmp(PF + 'txtClabe').reset();
												Ext.getCmp(PF + 'txtPlaza').reset();
												Ext.getCmp(PF + 'txtSucursal').reset();
	                            			}
	                            			
	                            			/*if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
		                            			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBancoBenef',NS.cmbBancoBenef.getId());
		                            			
		                            			if(valueCombo != null && valueCombo != undefined && valueCombo != '')
		                            				NS.accionarBancoBenef(valueCombo);
		                            			else
		                            				Ext.getCmp(PF + 'txtIdBancoBenef').reset();
											}else {
												NS.cmbBancoBenef.reset();
												NS.cmbChequeras.reset();
												NS.storeChequeras.removeAll();
												Ext.getCmp(PF + 'txtClabe').reset();
												Ext.getCmp(PF + 'txtPlaza').reset();
												Ext.getCmp(PF + 'txtSucursal').reset();
											}
											*/
	                            		}
	                            	}
	                            }
                            },
                                NS.cmbBancoBenef,                             
                              	NS.cmbChequeras,
                            {
                                xtype: 'textfield',
                                x: 530,
                                y: 15,
                                width: 140,
                                id: PF+'txtClabe',
                                name: PF+'txtClabe',
                                readOnly: true
                            },
                            {
                                xtype: 'textfield',
                                x: 690,
                                y: 15,
                                width: 60,
                                id: PF+'txtSucursal',
                                name: PF+'txtSucursal',
                                hidden:true,
                                readOnly: true
                            },
                            {
                                xtype: 'textfield',
                                x: 770,
                                y: 15,
                                width: 40,
                                id: PF+'txtPlaza',
                                name: PF+'txtPlaza',
                                hidden:true,
                                readOnly: true
                            },
                            {
                                xtype: 'label',
                                text: 'Banco Beneficiario:',
                                x: 100,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Chequera:',
                                x: 340,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Clabe:',
                                x: 530,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Sucursal:',
                                hidden:true,
                                x: 690,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Plaza:',
                                hidden:true,
                                x: 770,
                                y: 0
                            }
                        ]
                    },
                    //termina contenedor de transferencia
                    //inicia contenedor cheque
                    {
                        xtype: 'container',
                        x: 10,
                        y: 140,
                        width: 800,
                        height: 50,
                        hidden:true,
                        layout: 'absolute',
                        id: PF+'contCheque',
                        name: PF+'contCheque',
                        items: [
                            {
                                xtype: 'textfield',
                                width: 31,
                                x: 0,
                                y: 15,
                                id: PF+'txtIdCaja',
                                name: PF+'txtIdCaja',
                                listeners :{
                                	change:{
                                		fn:function(caja, valor){
                                			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdCaja',NS.cmbCajaUsuario.getId());
                                			NS.idCaja=valueCombo;
                                		}
                                	}
                                }
                                
                            },
                            NS.cmbCajaUsuario,
                            {
                                xtype: 'label',
                                text: 'Imprimir en:',
                                x: 0,
                                y: 0
                            },
                            {
                                xtype: 'checkbox',
                                boxLabel: 'Leyenda',
                                x: 230,
                                y: 15,
                                id: PF+'chkLeyenda',
                                name: PF+'chkLeyenda',
                                checked: true,
                              	listeners:{
		                        	check:{
		                        		fn:function(checkBox,valor)
		                        		{
		                        			if(valor==true)
		                        			{
			                     				NS.chkLeyenda='true';
		                        			}
		                        			else{
		                        				NS.chkLeyenda='false';
		                        			}
		                        		}
		                        	}
		                        }
                            }
                        ]
                    }
                    //termina contenedor cheque
                    
                    ]
            }
                    
                    
                    
                    ,
                    
                    {
                        xtype: 'button',
                        text: 'Ejecutar',
                        x: 880,
                        y: 430,
                        width: 80,
                        hidden: true,
                        id: PF+'btnEjecutar',
                        name: PF+'btnEjecutar',
                        listeners:{
                    	
                        	click:{
                    	
                        		fn:function(e){
                        			
                        			
                    	          
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 880,
                        y: 390,
                        width: 90,
                        id: PF+'btnLimpiar',
                        name: PF+'btnLimpiar',
                        listeners:{
                        click:{
                        		fn:function(e){
						 			
                        			NS.seleccionaPrimero=true;
                        			var vectorCombos =[
                       								Ext.getCmp(PF+'cmbTipoPolizas'),
                       								//Ext.getCmp(PF+'cmbGrupo'),//txtGrupoAutomatio
                       								Ext.getCmp(PF+'txtGrupoAutomatio'),//
                       								// cabecera renglon dos  
                       								Ext.getCmp(PF+'cmbRubroAl'),
                       								Ext.getCmp(PF+'cmbBeneficiario'),
                       								Ext.getCmp(PF+'cmbFormaPago'),
                       								Ext.getCmp(PF+'cmbEmpresas'),//cmbFormaPagoExt.getCmp(PF+'txtIdFormaPago'),//
                       								Ext.getCmp(PF + 'cmbBeneficiario')
                       				                   ];
                        			
                        			
                        			for(var  j = 0; j <= 6; j++) {
                        				vectorCombos[j].setValue('');
                        			}
                        			var vectorCajas = [
                    				                   //cabecera renglo uno
                    				                Ext.getCmp(PF + 'txtIdBancoBenef'),
                    				                Ext.getCmp(PF + 'txtClabe'),
                    				                Ext.getCmp(PF + 'txtSucursal'),
                    				                Ext.getCmp(PF + 'txtPlaza'),   
                    				                   
                    				                Ext.getCmp(PF + 'txtIdBeneficiario'),
                    				                Ext.getCmp(PF+'txtEmpresa'),//cmbEmpresas
                    			                    Ext.getCmp(PF+'txtIdTipoPoliza'),
                    				                Ext.getCmp(PF+'txtIdGrupo'),
                    				                // cabecera renglon dos  
                    				                Ext.getCmp(PF+'txtIdRubroAl'),
                    				                Ext.getCmp(PF+'txtIdBeneficiario'),//
                    				                //cabecera renglon tres
                    				                Ext.getCmp(PF+'txtIdFormaPago'),
                    				               // Ext.getCmp(PF+'txtIdDivisaOriginal'),
                    				                Ext.getCmp(PF+'chkNuevopoliza'),
                    				                //renglon uno
                    				              	Ext.getCmp(PF+'txtConcepto'),//texto
                    				              	//Ext.getCmp(PF+'fechaPago'),
                    				              	//Ext.getCmp(PF+'panelDivisaPagoV'),//txtIdDivisaPago textfield y combo de divisa
                    				              	Ext.getCmp(PF+'txtTipoCambio'),
                    				              	Ext.getCmp(PF+'txtImportePago'),
                    				              	
                    				              	//primera columna
                    				              	Ext.getCmp(PF+'txtTextDec'),//texto cabecera
                    				              	Ext.getCmp(PF+'txtAsignacion'),// asignacion
                    				              	Ext.getCmp(PF+'txtTextReferencia'),//refencia
                    				              	Ext.getCmp(PF+'txtTextCabeceraN'),//referencia de pago
                    				              	Ext.getCmp(PF+'txtSolicita'),//solicita
                    				              	//columna dos
                    				              	Ext.getCmp(PF+'txtSolicitaa'), //centro costos
                    				              	Ext.getCmp(PF+'txtTextDivisioninf'),//division 
                    				              	Ext.getCmp(PF+'txtTextOrdeninf'), //Ordeninf   
                    				              	Ext.getCmp(PF+'txtAutoriza'),  //Autoriza
                    				              	//observaciones   
                    				              	Ext.getCmp(PF+'txtObservaciones'),
                    				              	// documento y area destino
                    				              	//Ext.getCmp(PF+'panelDocumentoV'),
                    				              	Ext.getCmp(PF+'cmbAreaDestino'),
                    				              	Ext.getCmp(PF + 'txtImporteOriginal'),
                    				              	Ext.getCmp(PF + 'cmbCajaUsuario'),//
                    				              	Ext.getCmp(PF + 'txtIdCaja')
                    				];
                        			//Ext.getCmp(PF+'txtImporteOriginal').setValue('');
                    				for(var  j = 0; j <= 28; j++) {
                    					vectorCajas[j].setValue('');
                        			}
                    				NS.rubroActual= 0;
                    				NS.idPolizaParametro=0; 
                    				NS.grupo= 0;
                    				//NS.habilitarComponentesPerfilEmpresa();
                    				NS.mostrarFormaPago();
						 		//	NS.habilitarComponentes(false);
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Guardar Plantilla',
                        x: 880,
                        y: 360,
                        width: 90,
                        id: PF+'btnGuardarPlantilla',
                        name: PF+'btnGuardarPlantilla',
                        disabled:true,
                        listeners:{
                        click:{
                        		fn:function(e){
						 			//NS.limpiar();
                        			//Ext.Msg.alert('Información SET', 'En proceso');
                        			winPlantilla.show();
						 		//	NS.habilitarComponentes(false);
                        		}
                        	}
                        }
                    },   
                    {
                        xtype: 'button',
                        text: 'Imprimir',
                        x: 880,
                        y: 325,
                        hidden: true,
                        width: 90,
                        id: PF+'btnImprimirA',
                        name: PF+'btnImprimirA',
                        listeners:{
                        click:{
                        		fn:function(e){
						 			//NS.limpiar();
                        			//Ext.Msg.alert('Información SET', 'En proceso');
                        			winDetalle.show();
						 		//	NS.habilitarComponentes(false);
                        		}
                        	}
                        }
                    }   
            
                   , {
                        xtype: 'checkbox',
                        id:PF+'chkTodos',
                        name:PF+'chkTodos',
                        x: 390,
                        y: 0,
                        boxLabel: 'Todos',
                        hidden: true,
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        			if(valor==true)
                        			{
	                     			 	NS.chkTodos='true'
                        			}
                        			else
                        			{
                        				NS.chkTodos='false'
                        			}
                        		}
                        	}
                        }
                    }
                   
                    ,{
                        xtype: 'checkbox',
                        id:PF+'chkProvUnico',
                        name:PF+'chkProvUnico',
                        x: 390,
                        y: 15,
                        hidden: true,
                        boxLabel: 'Proveedor &Uacute;nico',
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        			if(valor==true)
                        			{
	                     				NS.storeBancoBenef.baseParams.provUnico='true';
	                     				NS.chkProvUnico='true';
                        			}
                        			else{
                        				NS.chkProvUnico='false';
                        			}
                        		}
                        	}
                        }
                    }      
                    

            ]
    }
            
            
         ]
	});
	NS.seleccionaFormaDePago = function (formaPago){
		if(Ext.getCmp(PF + 'txtIdBeneficiario').getValue() != ''){
			//BFwrk.Util.updateComboToTextField(PF+'txtIdFormaPago', NS.cmbFormaPago.getId());
			Ext.getCmp(PF + 'txtIdFormaPago').setValue(formaPago);
			NS.mostrarFormaPago(parseInt(formaPago));
		}else{
			Ext.getCmp(PF + 'txtIdFormaPago').reset();
			NS.cmbFormaPago.reset();
			Ext.Msg.alert('Información SET', 'Seleccione un Beneficiario.');
		}
		NS.controlFechas(apps.SET.FEC_HOY, 'load');
	};
	
	NS.seleccionaRubro = function(){
		BFwrk.Util.updateComboToTextField(PF+'txtIdRubroAl',NS.cmbRubroAl.getId());
		NS.rubroActual=NS.cmbRubroAl.getValue();
		//NS.obtenerGrupoDelRubro();
		//NS.controlFechas(apps.SET.FEC_HOY, 'load');
	};
	
	NS.seleccioneBaneficiario = function(noBeneficiario){
		 BFwrk.Util.updateComboToTextField(PF+'txtIdBeneficiario',NS.cmbBeneficiario.getId());
		 NS.accionarBeneficiario(noBeneficiario);//NS.cmbBeneficiario.getValue());
		 NS.controlFechas(apps.SET.FEC_HOY, 'load');
	};
	
	NS.seleccionaEmpresa  = function(){
		BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresas.getId());
		NS.accionarEmpresas(NS.cmbEmpresas.getValue());
		NS.storeBeneficiario.removeAll();
		NS.cmbBeneficiario.reset();
		Ext.getCmp(PF + 'txtIdBeneficiario').setValue('');
		NS.noEmpresaTmp = NS.cmbEmpresas.getValue();
		NS.nomEmpresaTmp = NS.storeEmpresas.getById(NS.cmbEmpresas.getValue()).get('nomEmpresa');
		NS.controlFechas(apps.SET.FEC_HOY, 'load');
		
	};
	
	NS.seleccionPoliza = function(){
		BFwrk.Util.updateComboToTextField(PF+'txtIdTipoPoliza',NS.cmbTipoPolizas.getId());
		NS.idPolizaParametro = parseInt(NS.cmbTipoPolizas.getValue());
		if(Ext.getCmp(PF + 'cmbRubroAl').getValue() != null && 
				Ext.getCmp(PF + 'cmbRubroAl').getValue() != undefined && 
				Ext.getCmp(PF + 'cmbRubroAl').getValue() != '') {
			NS.habilitarComponentesPerfilEmpresa();
			NS.storeRubro.baseParams.condicion='Id_rubro in (select id_grupo from ASIGNACION_POLIZA_GRUPO where id_poliza = '+NS.idPolizaParametro+')';
			NS.storeRubro.load(); 
			
		}

		NS.storeRubro.baseParams.condicion='Id_rubro in (select id_grupo from ASIGNACION_POLIZA_GRUPO where id_poliza = '+NS.idPolizaParametro+')';
		NS.storeRubro.load(); 
		NS.controlFechas(apps.SET.FEC_HOY, 'load');
	};
	
	NS.seleccionaBancoBenef = function(){
		 BFwrk.Util.updateComboToTextField(PF+'txtIdBancoBenef',NS.cmbBancoBenef.getId());
		 NS.accionarBancoBenef(NS.cmbBancoBenef.getValue());
		 NS.controlFechas(apps.SET.FEC_HOY, 'load');
	};
	
	NS.existeEnStored = function(store, id){
		return store.getById(parseInt(id)) != undefined && store.getById(parseInt(id)) != null;
	};
	
	NS.controlFechas = function(fecha, origen){
		
		CapturaSolicitudesPagoAction.controlFechas(fecha, origen, function(resultado, e) {
			Ext.getCmp(PF + 'fechaPago').setValue(resultado);
			if(origen == 'load'){
				Ext.getCmp(PF + 'fechaPago').setMinValue(resultado);
			}	
		});
		
	}
	
	
	NS.CapturaSolicitudesPago.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
