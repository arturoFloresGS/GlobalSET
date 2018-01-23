//@autor Cristian Garcia Garcia
//22/Feb/2011
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Egresos.prueba');
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
	NS.parametroBus='';
	
	NS.noEmpresaTmp = '';
	NS.nomEmpresaTmp = '';
	
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
		//NS.idEmpresa=0;
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
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
		NS.CapturaSolicitudesPago.getForm().reset();
		NS.obtieneSolicitante();
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
		Ext.getCmp(PF + 'txtIdGrupo').setReadOnly(detalle);
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
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'numPartida',//identificador del store
		fields: [
			{name: 'numPartida'},
			{name: 'rubro'},
			{name: 'descripcion'},
			{name: 'importe'},
			{name: 'departamento'},
			{name: 'grupo'},
			{name: 'area'}
		],
		listeners: {
			load: function(s, records){
		
			}
		}
	}); 
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
		
	
	//Grid para mostrar los datos seleccionados
		NS.gridDatos  = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		columns : [ {
				 
				header :'Partida',
				width :60,
				dataIndex :'numPartida'
			}, {
				header :'Rubro',
				width :75,
				dataIndex :'rubro'
			},{
				 
				header :'Descripcion',
				width :150,
				dataIndex :'descripcion'
			},{
				header :'Importe',
				width :100,
				dataIndex :'importe',
				renderer: BFwrk.Util.rendererMoney
			},{
			 
				header :'Departamento',
				width :87,
				dataIndex :'departamento'
			}, {
				header :'Grupo',
				width :75,
				dataIndex :'grupo'
			},{
				 
				header :'Area',
				width :50,
				dataIndex :'area'
			}
		],
		width: 600,
       	height: 150,
		x :350,
		y :15,
		title :'',
		listeners:{
			dblclick:{
				fn:function(grid) {
					NS.eliminarPartida();
				}
			}
		}
	});

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
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
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
	
	NS.storeBancos.load();

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
		NS.storeDivision.baseParams.condicion='no_empresa ='+valueCombo+'';
		NS.storeDivision.load();
		NS.storeBancoBenef.baseParams.noEmpresa = valueCombo;
		NS.storeChequeras.baseParams.idEmpresa = valueCombo;
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
						NS.storeBeneficiario.removeAll();
						NS.cmbBeneficiario.reset();
						Ext.getCmp(PF + 'txtIdBeneficiario').setValue('');
						NS.noEmpresaTmp = combo.getValue();
						NS.nomEmpresaTmp = NS.storeEmpresas.getById(combo.getValue()).get('nomEmpresa');
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
        ,y: 65
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
        ,x: 430
        ,y: 115
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
	
	
	//store Forma Pago
	    NS.storeFormaPago = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: CapturaSolicitudesPagoAction.obtenerFormaPago, 
		idProperty: 'idFormaPago', 
		fields: [
			 {name: 'idFormaPago'},
			 {name: 'descFormaPago'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFormaPago, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen formas de pago');
				}
			}
		}
	}); 
	NS.storeFormaPago.load();
	
	//Funcion para ser llamada en el select del combo como en el change de la caja de texto
	
	NS.mostrarFormaPago = function(valueCombo){
		NS.cmbBancoBenef.reset();
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();
		Ext.getCmp(PF + 'txtIdBancoBenef').reset();
		Ext.getCmp(PF + 'txtClabe').reset();
		Ext.getCmp(PF + 'txtSucursal').reset();
		Ext.getCmp(PF + 'txtPlaza').reset();
		
		NS.idFormaPago=parseInt(valueCombo);
		if(valueCombo===1 || valueCombo===2)
		{
			Ext.getCmp(PF+'contCheque').setVisible(true);
			Ext.getCmp(PF+'contTransferencia').setVisible(false);
		}
		else if(valueCombo===3)
		{
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
        ,y: 65
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
						BFwrk.Util.updateComboToTextField(PF+'txtIdFormaPago', NS.cmbFormaPago.getId());
						NS.mostrarFormaPago(parseInt(Ext.getCmp(PF + 'txtIdFormaPago').getValue()));
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
	NS.storeAreaDestino.load();
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
        ,x: 730
        ,y: 515
        ,width: 170
		,valueField:'id'
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
	NS.storeAreaDet.load();
	
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
		,valueField:'id'
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
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
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
	;
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
		,valueField:'id'
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
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_grupo',
			campoDos:'desc_grupo',
			tabla:'cat_grupo',
			//condicion:'id_grupo in(220000,440000,800000)',
			condicion:'id_grupo in (6000) ',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarCombo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
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
	//combo Grupo
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo
		,name: PF+'cmbGrupo'
		,id: PF+'cmbGrupo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 85
        ,y: 65
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione cuenta'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,disabled:true
		,listeners:{
			select:{
				fn:function(combo, valor) {
						BFwrk.Util.updateComboToTextField(PF+'txtIdGrupo',NS.cmbGrupo.getId());
						NS.accionarGrupo(combo.getValue());
				}
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
			registroUnico:false,
			noEmpresa: 0
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico','noEmpresa'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarComboBeneficiario, 
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
				NS.limpiaCamposBenef();
			}
		}
	}); 
	
	//Este combo carga los datos dependiendo la(s) letra(s) que se tecleen en el combo
	//store  Beneficiario 
    NS.storeBeneficiario = new Ext.data.DirectStore({
    //NS.storeBeneficiario = new Ext.ux.data.PagingDirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'no_persona',
			campoDos:'',
			tabla:'persona',
			condicion:'',
			orden:'',
			registroUnico:false,
			noEmpresa:0
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico','noEmpresa'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0) {
					Ext.Msg.alert('SET','No Existen Beneficiarios con ese nombre');
					Ext.getCmp(PF + 'txtIdBeneficiario').setValue('');
					NS.cmbBeneficiario.reset();
				}else {
					NS.cmbBeneficiario.setValue(records[0].get('descripcion'));
					Ext.getCmp(PF+'txtIdBeneficiario').setValue(records[0].get('id'));
					NS.idPersona=Ext.getCmp(PF+'txtIdBeneficiario').getValue();
					NS.nomPersona=records[0].get('descripcion');
					//NS.accionarUnicoBeneficiario(reg[0].get('id'));
				}
				NS.limpiaCamposBenef();
			}
		}
	}); 
	
	//NS.storeBeneficiario.load();

	/*	
	NS.sTabla  = "(select equivale_persona ID"; 
    NS.sTabla += "    ,rtrim(p.razon_social) + rTRIM(NOMBRE) + ' ' + rTRIM(PATERNO) + ' ' + rTRIM(MATERNO) PROVEEDOR"; 
    NS.sTabla += "    ,NUM = ROW_NUMBER() OVER (ORDER BY equivale_persona ASC)";
 	NS.sTabla += " from persona p";
 	NS.sTabla += " where p.id_tipo_persona = 'P'";
	NS.sTabla += "  and ((p.razon_social like 'E%' or p.paterno like 'E%' or p.materno like 'E%' or p.nombre like 'E%')";
    NS.sTabla += "  or (p.equivale_persona like 'E%'))";
	NS.sTabla += ") PER";
	
	
    NS.storeBeneficiario = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'PER.ID',
			campoDos:'PER.PROVEEDOR',
			tabla: NS.sTabla,
			condicion: '', //'NUM  > 10 AND NUM <= 20',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.llenarCombo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					//Ext.Msg.alert('SET','No Existen areas asignadas para el usuario');
				}
			}
		}
	});
	 
	NS.storeBeneficiario.load();
	*/
	//Esta funcion es para el storeUnicoBeneficiario
	NS.accionarUnicoBeneficiario = function(valueCombo){
		NS.storeBancoBenef.baseParams.idPersona = valueCombo;
		NS.storeChequeras.baseParams.idPersona = valueCombo;
	    NS.idPersona=valueCombo;
		NS.nomPersona=NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion');
		
		/*
		if((NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion')) != null
			&& (NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion')).length > 25)
		{
			Ext.getCmp(PF+'txtSolicita').setValue(NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion').substring(0,25));
		}
		else
		{
			if(NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion') == null)
				Ext.getCmp(PF+'txtSolicita').setValue('Datos Incompletos');
			else
		 		Ext.getCmp(PF+'txtSolicita').setValue(NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion'));
		}
		*/
	}
	
	NS.obtieneSolicitante = function() {
		CapturaSolicitudesPagoAction.obtenerSolicitante(function(result, e) {
			Ext.getCmp(PF + 'txtSolicita').setValue(result);
		});
	};
	NS.obtieneSolicitante();
	
	NS.accionarBeneficiario = function(valueCombo){
		NS.storeBancoBenef.baseParams.idPersona=valueCombo;
		NS.storeChequeras.baseParams.idPersona=valueCombo;
	    NS.idPersona=valueCombo;
		NS.nomPersona=NS.storeBeneficiario.getById(valueCombo).get('descripcion');
		Ext.getCmp(PF + 'txtIdFormaPago').setValue('');
		NS.cmbFormaPago.reset();
		
		/*if((NS.storeBeneficiario.getById(valueCombo).get('descripcion')).length>25)
			Ext.getCmp(PF+'txtSolicita').setValue(NS.storeBeneficiario.getById(valueCombo).get('descripcion').substring(0,25));
		else
		 	Ext.getCmp(PF+'txtSolicita').setValue(NS.storeBeneficiario.getById(valueCombo).get('descripcion'));
		*/
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
      	,x: 565
        ,y: 15
        ,width: 180
        ,tabIndex: 4
		,valueField:'id'
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
				 	 BFwrk.Util.updateComboToTextField(PF+'txtIdBeneficiario',NS.cmbBeneficiario.getId());
					 NS.accionarBeneficiario(combo.getValue());
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
			idPersona:0,
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
		NS.storeChequeras.baseParams.idBanco = valueCombo;
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
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
			 			 BFwrk.Util.updateComboToTextField(PF+'txtIdBancoBenef',NS.cmbBancoBenef.getId());
			 			 NS.accionarBancoBenef(combo.getValue());
				}
			}
		}
	});
	
	//store Chequeras
	    NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idPersona:0,
			idEmpresa:0,
			idBanco:0,
		},
		root: '',
		paramOrder:['idPersona','idEmpresa','idBanco'],
		root: '',
		directFn: CapturaSolicitudesPagoAction.obtenerChequeras, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});
				
				if(records.length==null || records.length<=0) {
					//Ext.Msg.alert('SET','No Existe chequera');
				}else {
					if(records.length == 1) {
						Ext.getCmp(PF + 'cmbChequeras').setValue(records[0].get('descripcion'));
						NS.buscaSucPlazClabe('' + records[0].get('descripcion'));
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
  	    ,x: 340
        ,y: 15
        ,width: 170
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione chequera'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idChequera = '' + NS.storeChequeras.getById(combo.getValue()).get('descripcion');
					NS.buscaSucPlazClabe(NS.idChequera);
				}
			}
		}
	});
	
	NS.buscaSucPlazClabe = function(idChequera) {
		CapturaSolicitudesPagoAction.obtenerSucPlazClabe(NS.idPersona, NS.idEmpresa, idChequera, function(result, e){
			if(result!==null) {
				Ext.getCmp(PF+'txtSucursal').setValue(result[0].idSucursal);
				Ext.getCmp(PF+'txtPlaza').setValue(result[0].idPlaza);
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
		directFn: CapturaSolicitudesPagoAction.llenarCombo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubro, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					//Ext.Msg.alert('SET','No Existe chequera');
				}
			}
		}
	}); 
	
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
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione subcuenta'
		,triggerAction: 'all'
		,value: ''
		,disabled:true
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtIdRubro',NS.cmbRubro.getId());
					NS.accionarRubro(combo.getValue());
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
	
	NS.eliminarPartida = function() {
		var recordsEliminar = NS.gridDatos.getSelectionModel().getSelections();
		var datosClase = NS.gridDatos.getStore().recordType;
		var recordsGridDatos = NS.storeDatos.data.items;
		var x = 1;
		var rubro = "";
		var descripcion = "";
		var importe = 0;
		var departamento = "";
		var grupo = 0;
		var area = 0;
		var sumImporte = 0;
		var totImporte = 0;
		
		if(recordsEliminar.length > 0) {
			Ext.Msg.confirm('Información SET','¿Esta seguro de eliminar '+recordsEliminar.length+' detalle(s)?',function(btn) {
				if(btn == 'yes') {
					for(var i = 0; i < recordsEliminar.length; i++) {
						NS.gridDatos.store.remove(recordsEliminar[i]);
						NS.gridDatos.getView().refresh();
						NS.numPartida = NS.numPartida - 1;
					}
					
					for(var i = 0; i < recordsGridDatos.length; i++) {
						rubro = recordsGridDatos[i].get('rubro');
						descripcion = recordsGridDatos[i].get('descripcion');
						importe = recordsGridDatos[i].get('importe');
						departamento = recordsGridDatos[i].get('departamento');
						grupo = recordsGridDatos[i].get('grupo');
						area = recordsGridDatos[i].get('area');
						sumImporte = parseFloat(recordsGridDatos[i].get('importe'));
						totImporte = totImporte + sumImporte;
						
						NS.gridDatos.store.remove(recordsGridDatos[i]);
						
						var datos = new datosClase({
							numPartida: x,
							rubro: rubro,
							descripcion: descripcion,
							importe: importe,
							departamento: departamento,
							grupo: grupo,
							area: area
						});
						NS.gridDatos.stopEditing();
						NS.storeDatos.insert(i, datos);
						NS.gridDatos.getView().refresh();
						x++;
					}
					Ext.getCmp(PF + 'txtSumPartidas').setValue(NS.numPartida);
					Ext.getCmp(PF + 'txtSumImporte').setValue(NS.formatNumber(totImporte));
				}
			});
		}
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
		
		//alert(parseFloat(impOri));
		//alert(parseFloat(impPartidas));
		//alert(parseFloat(nuevoImpParti));
		if((parseFloat(impPartidas) + parseFloat(nuevoImpParti)) > parseFloat(impOri)) {
			Ext.Msg.alert('SET', 'El monto de las partidas supera el improte original');
			return true;
		}
		return false;
	};
	NS.accionarEmpresas(apps.SET.NO_EMPRESA);
	
	NS.limpiaCamposBenef = function() {
		Ext.getCmp(PF + 'txtIdFormaPago').setValue('');
		NS.cmbFormaPago.reset();
		NS.limpiaPanelTransfer();
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
                height: 600,
                x: 20,
                y: 5,
                layout: 'absolute',
                items: [
                    {
                    	
                    	xtype: 'fieldset',
                    	title: 'Captura',
                        width: 985,
                        height: 213,
                        layout: 'absolute',
                        items: [
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
                    				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                        				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresas.getId());
	                    				
	                        			if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
	                        				NS.noEmpresaTmp = caja.getValue();
	                						NS.nomEmpresaTmp = NS.storeEmpresas.getById(parseInt(caja.getValue())).get('nomEmpresa');
	                						
	                        				NS.accionarEmpresas(valueCombo);
	                        			}else
	                        				Ext.getCmp(PF + 'txtEmpresa').reset();
                    				}else
	                    				NS.cmbEmpresas.reset();
                        		}
                        	}
                        }
                    },
                    {
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
                    },
                    {
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
                    },
                   NS.cmbEmpresas,
                   NS.cmbBeneficiario,
                    /*{
                        xtype: 'combo',
                        x: 130,
                        y: 70,
                        width: 190,
                        id: 'cmbOrigenProv'
                    },*/
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
                        x: 500,
                        y: 15,
                        width: 60,
                        tabIndex: 3,
                        id: PF+'txtIdBeneficiario',
                        name: PF+'txtIdBeneficiario',
                        listeners:{
                        	change:{
                        		fn:function(caja, valor){
                        			//var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBeneficiario',NS.cmbBeneficiario.getId());
                        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                       					NS.storeUnicoBeneficiario.baseParams.registroUnico=true;
                        				NS.storeUnicoBeneficiario.baseParams.condicion=''+caja.getValue();
                        				NS.storeUnicoBeneficiario.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
                        				NS.storeUnicoBeneficiario.load();//Cuando se carga se pasan los parametros del id del benef
                       				}else
                       					NS.cmbBeneficiario.reset();
                        			NS.limpiaCamposBenef();
                        		}
                        	}
                        }
                    }
                    ,
                    {
                        xtype: 'uppertextfield',
                        x: 500,
                        y: 65,
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
                        x: 390,
                        y: 65,
                        width: 90,
                        tabIndex: 8,
                        id: PF+'txtImporteOriginal',
                        name:PF+'txtImporteOriginal',
                        listeners:{
                        	change:{
                        		fn:function(caja,valor){
                        			Ext.getCmp(PF+'txtImporteOriginal').setValue(NS.formatNumber(caja.getValue()));
                        			Ext.getCmp(PF+'txtImportePago').setValue(NS.formatNumber(caja.getValue()));
                        			Ext.getCmp(PF+'txtImportePartida').setValue(NS.formatNumber(caja.getValue()));
                        		}
                        	}
                        }
                    },
                    {
                        xtype:'datefield',
                   		format:'d/m/Y',
                        x: 260,
                        y: 65,
                        width: 100,
                        tabIndex: 13,
                        id: PF+'fechaFactura',
                        name: PF+'fechaFactura',
                        value: apps.SET.FEC_HOY
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 65,
                        width: 35,
                        tabIndex: 5,
                        id: PF+'txtIdFormaPago',
                        name:PF+'txtIdFormaPago',
                        listeners:{
                        	change:{
                        		fn:function(caja, valor){
                    				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdFormaPago', NS.cmbFormaPago.getId());
                    				NS.mostrarFormaPago(valueCombo);
                    				
                    				if(caja.getValue() == null || caja.getValue() == undefined || caja.getValue() == '')
                    					NS.cmbFormaPago.reset();
                    				if(valueCombo == null || valueCombo == undefined || valueCombo == '')
                    					Ext.getCmp(PF + 'txtIdFormaPago').reset();
                        		}
                        	}
                        }
                    },
                    
                      NS.cmbFormaPago,
                      NS.cmbDivisaOriginal,
                    {
                        xtype: 'textfield',
                        x: 700,
                        y: 115,
                        width: 120,
                        tabIndex: 17,
                        id: PF+'txtImportePago',
                        name: PF+'txtImportePago',
                        readOnly: true
                    },
                    {
                        xtype:'datefield',
                   		format:'d/m/Y',
                        x: 260,
                        y: 115,
                        width: 100,
                        tabIndex: 7,
                        id: PF+'fechaPago',
                        name: PF+'fechaPago',
                        value: apps.SET.FEC_HOY
                    },
                    {
                        xtype: 'uppertextfield',
                        x: 10,
                        y: 115,
                        width: 220,
                        tabIndex: 12,
                        id: PF+'txtConcepto',
                        name: PF+'txtConcepto'
                    },
                    {
                        xtype: 'uppertextfield',
                        x: 390,
                        y: 115,
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
                        NS.cmbDivisaPago,
                    {
                        xtype: 'textfield',
                        x: 600,
                        y: 115,
                        width: 80,
                        tabIndex: 16,
                        id: PF+'txtTipoCambio',
                        name: PF+'txtTipoCambio', 
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
                        name: PF+'btnBuscar',
                        listeners:{
                        	click:{
                        		fn:function(e){
                        		if(Ext.getCmp(PF+'txtNoDocto').getValue()!=="")
                        		{
                        			/*Esta parte es exclusiva de costco por el momento no se integra 09/02/2011
                        			var indice=1;
						            CapturaSolicitudesPagoAction.consultarConfiguraSet(indice, function(resultado,e){
						            	var configuraSet=resultado;
						            	if(configuraSet==='COSTCO')
						            	{
						            		if(NS.idPersona==0)
						            		{
						            			Ext.Msg.alert('Información SET','Debe seleccionar un beneficiario ');
						            			return;
						            		}
						            		//If cmbOrigenProv.Text <> "NOMINA" Then
							                  If gobjSQL.FunSQLExistefactura(Trim$(txtNoDocto), cmbEmpresa.ItemData(cmbEmpresa.ListIndex), id_benef) Then
							                    If Trim(gobjVarGlobal.valor_Configura_set(1)) <> "COSTCO" Then
							                        MsgBox "La factura:" & Trim$(txtNoDocto) & " ya existe en el erp", vbInformation, "SET"
							                    Else
							                        MsgBox "La factura:" & Trim$(txtNoDocto) & " ya existe en el AS/400", vbInformation, "SET"
							                    End If
							                      txtNoDocto.Text = ""
							                  End If
							               
							                //End If
						            	}
						            });*/          			
                        			CapturaSolicitudesPagoAction.consultarMovimientos(Ext.getCmp(PF+'txtNoDocto').getValue(),NS.idEmpresa,''+NS.idPersona,function(result,e){
                        				var recordsBenef=NS.storeBeneficiario.data.items;
                        				var recordsDivisas=NS.storeDivisas.data.items;
                        				var recordsFormaPago=NS.storeFormaPago.data.items;
                        				var recordsBancos=NS.storeBancos.data.items;
                        				var recordsCajaUsuario=NS.storeCajaUsuario.data.items;
                        				
                        				if(result[0]==null)
                        				{
                        					Ext.Msg.alert('Información SET','No existe la solicitud');
                        					return;
                        				}
                   						Ext.getCmp(PF+'cmbBeneficiario').setValue(result[0].beneficiario);
                   						Ext.getCmp(PF+'txtIdBeneficiario').setValue(result[0].noCliente);
                   							
                        				for(var j=0;j<recordsDivisas.length;j=j+1)
                        				{
                        					if(recordsDivisas[j].get('idDivisa') == result[0].idDivisa)
                        					{
                        						Ext.getCmp(PF+'txtIdDivisaPago').setValue(recordsDivisas[j].get('idDivisa'));
                        						Ext.getCmp(PF+'cmbDivisaPago').setValue(recordsDivisas[j].get('descDivisa'));	
                        					}
                        					if(recordsDivisas[j].get('idDivisa') == result[0].idDivisaOriginal)
                       						{
                       							Ext.getCmp(PF+'txtIdDivisaOriginal').setValue(recordsDivisas[j].get('idDivisa'));
                       							Ext.getCmp(PF+'cmbDivisaOriginal').setValue(recordsDivisas[j].get('descDivisa'));
                       						}
                        				}
                        				for(var k=0;k<recordsFormaPago.length;k=k+1)
                        				{
                        					if(recordsFormaPago[k].get('idFormaPago') == result[0].idFormaPago)
                        					{
                        						Ext.getCmp(PF+'cmbFormaPago').setValue(recordsFormaPago[k].get('descFormaPago'));
                        						Ext.getCmp(PF+'txtIdFormaPago').setValue(recordsFormaPago[k].get('idFormaPago'));	
                        					}
                        				}
                        				//Validar los id forma de pago Para setear los valores
                        				if(result[0].idFormaPago==3){
                        					Ext.getCmp(PF+'contCheque').setVisible(false);
											Ext.getCmp(PF+'contTransferencia').setVisible(true);
											for(var ii=0; ii<recordsBancos.length;ii=ii+1)
											{
												if(recordsBancos[ii].get('id')==result[0].idBancoBenef)
												{
													Ext.getCmp(PF+'txtIdBancoBenef').setValue(result[0].idBancoBenef);
													Ext.getCmp(PF+'cmbBancoBeneficiario').setValue(recordsBancos[ii].get('descripcion'));
													Ext.getCmp(PF+'cmbChequeras').setValue(result[0].idChequeraBenef);
													Ext.getCmp(PF+'txtClabe').setValue(result[0].clabe);
													Ext.getCmp(PF+'txtSucursal').setValue(result[0].sucursal);
													Ext.getCmp(PF+'txtPlaza').setValue(result[0].plaza);
													if(result[0].nacExt==='N')
													{
														Ext.getCmp(PF+'optNac').setValue(true);
														Ext.getCmp(PF+'optExt').setValue(false);
													}else if(result[0].nacExt==='E'){
														Ext.getCmp(PF+'optNac').setValue(false);
														Ext.getCmp(PF+'optExt').setValue(true);
													}
													
												}
												
											}
                        				}
                        				else if(result[0].idFormaPago==1){
                        					Ext.getCmp(PF+'contCheque').setVisible(true);
											Ext.getCmp(PF+'contTransferencia').setVisible(false);
											for(var ij=0; ij<recordsCajaUsuario.length;ij=ij+1)
											{
												if(recordsCajaUsuario[ij].get('idCaja')==result[0].idCaja)
												{
													Ext.getCmp(PF+'txtIdCaja').setValue(result[0].idCaja);
													Ext.getCmp(PF+'cmbCajaUsuario').setValue(recordsCajaUsuario[ij].get('descCaja'));
													if(result[0].idLeyenda==1){
														Ext.getCmp(PF+'chkLeyenda').setValue(true);
													}
													else{
														Ext.getCmp(PF+'chkLeyenda').setValue(false);
													}
													
												}
												
											}
                        				}
                        				else if(result[0].idFormaPago==2){
                        					Ext.getCmp(PF+'contCheque').setVisible(true);
											Ext.getCmp(PF+'contTransferencia').setVisible(false);
											for(var ik=0; ik<recordsCajaUsuario.length;ik=ik+1)
											{
												if(recordsCajaUsuario[ik].get('idCaja')==result[0].idCaja)
												{
													Ext.getCmp(PF+'txtIdCaja').setValue(result[0].idCaja);
													Ext.getCmp(PF+'cmbCajaUsuario').setValue(recordsCajaUsuario[ik].get('descCaja'));
													if(result[0].idLeyenda==1){
														Ext.getCmp(PF+'chkLeyenda').setValue(true);
													}
													else{
														Ext.getCmp(PF+'chkLeyenda').setValue(false);
													}
													
												}
                        					}
                        				}
                        				else{
                        					Ext.getCmp(PF+'contCheque').setVisible(false);
											Ext.getCmp(PF+'contTransferencia').setVisible(false);
                        				}
										var fecValor=result[0].fecOperacion;
										var fecOperacion=result[0].fecValor;
										Ext.getCmp(PF+'fechaFactura').setValue(fecOperacion.substring(0,10));
										Ext.getCmp(PF+'fechaPago').setValue(fecValor.substring(0,10));
										Ext.getCmp(PF+'txtImporteOriginal').setValue(NS.formatNumber(result[0].importeOriginal));
										Ext.getCmp(PF+'txtImportePago').setValue(NS.formatNumber(result[0].importe));//mult importeOriginal* tipo de cambio
										Ext.getCmp(PF+'txtConcepto').setValue(result[0].concepto);
										Ext.getCmp(PF+'txtTipoCambio').setValue(result[0].tipoCambio);
										Ext.getCmp(PF+'txtSolicita').setValue(result[0].solicita);
										Ext.getCmp(PF+'txtAutoriza').setValue(result[0].autoriza);
										Ext.getCmp(PF+'txtObservaciones').setValue(result[0].observacion);
										///Ext.getCmp(PF+'txtSumImporte').setValue(NS.formatNumber(result[0].importeOriginal));
										
										NS.gridDatos.store.removeAll();
										NS.gridDatos.getView().refresh();
										
										CapturaSolicitudesPagoAction.obtenerDetalleMovimiento(parseInt(result[0].noFolioDet), function(res, e){
											var impParti = 0;
											
		                        		 	for(var cont=0; cont<res.length; cont=cont+1)
		                        		 	{
											 	var datosClase = NS.gridDatos.getStore().recordType;
						          				var datos = new datosClase({
								                	numPartida: res[cont].noPartida,
													rubro: res[cont].idRubro,
													descripcion: res[cont].descRubro,
													importe: res[cont].importe,
													departamento: res[cont].division,
													grupo: res[cont].idGrupo,
													area: res[cont].idArea
							            		});
						          				impParti = impParti + parseFloat(res[cont].importe);
						                       	NS.gridDatos.stopEditing();
							            		NS.storeDatos.insert(cont, datos);
							            		NS.gridDatos.getView().refresh();
							            		NS.gridDatos.store.sort('numPartida','ASC');
						            		}
						            		Ext.getCmp(PF+'txtSumPartidas').setValue(res[res.length-1].noPartida);
						            		Ext.getCmp(PF+'txtSumImporte').setValue(NS.formatNumber(impParti));
						            		
						            		if(impParti == parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtImporteOriginal').getValue())))
												NS.habilitarComponentes(true, true);
											else
												NS.habilitarComponentes(true, false);
										});
                        			});
                        		}
                        		else{
                        			Ext.Msg.alert('Información SET','Es necesario agregar número de documento');
                        		}
                        	  }
                        	}
                        }
                    }
                    ,{
	                        xtype: 'button',
	                        text: '...',
	                        x: 750,
	                        y: 15,
	                        width: 20,
	                        height: 22,
	                        id: PF+'btnComboBenef',
	                        name: PF+'btnComboBenef',
	                        listeners:{
	                        	click:{
	                        		fn:function(e){
	                        		if(NS.parametroBus==='')
	                        		{
	                        			Ext.Msg.alert('Información SET','Es necesario agregar una letra o nombre');
	                        		}
	                        		else{
	                        			NS.storeBeneficiario.baseParams.condicion=NS.parametroBus;
	                        			NS.storeBeneficiario.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
	                        			NS.storeBeneficiario.load();
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
                    /*{
                        xtype: 'label',
                        text: 'No. Documento:',
                        x: 500,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Origen del Beneficiario',
                        x: 130,
                        y: 50
                    },*/
                    {
                        xtype: 'label',
                        text: 'Beneficiario:',
                        x: 500,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Forma Pago:',
                        x: 10,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha factura:',
                        x: 260,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Importe Original:',
                        x: 390,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Divisa Original:',
                         x: 500,
  						 y: 50
                    },
                    {
                        xtype: 'checkbox',
                        x: 725,
                        y: 65,
                        tabIndex: 11,
                        boxLabel: 'Contabilizar',
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
                        text: 'Concepto:',
                        x: 10,
                        y: 100
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha Pago:',
                        x: 260,
                        y: 100
                    },
                    {
                        xtype: 'label',
                        text: 'Divisa de Pago:',
                        x: 390,
                        y: 100
                    },
                    { 
                        xtype: 'label',
                        text: 'Tipo Cambio:',
                        x: 600,
                        y: 100
                    },
                    {
                        xtype: 'label',
                        text: 'Importe de Pago:',
                        x: 700,
                        y: 100
                    },
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
												if(valor==true)
                                   				{
													NS.limpiaPanelTransfer();
													NS.storeBancoBenef.baseParams.nacExt='N';
                                   					NS.storeBancoBenef.load();
                                   					NS.optNac='true';
                                   				}
                                   				else
                                   				{
                                   					NS.optNac='false';
                                   				}
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
											}
											if(valor==true)
                               				{
												NS.limpiaPanelTransfer();
												
                               					NS.storeBancoBenef.baseParams.nacExt='E';
                               					NS.storeBancoBenef.load();
                               					NS.optExt='true';
                               				}
                               				else{
                               					NS.optExt='false';
                               				}
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
											if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
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
                                name: PF+'txtSucursal'
                            },
                            {
                                xtype: 'textfield',
                                x: 770,
                                y: 15,
                                width: 40,
                                id: PF+'txtPlaza',
                                name: PF+'txtPlaza'
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
                                x: 690,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Plaza:',
                                x: 770,
                                y: 0
                            }
                        ]
                    },//contenedor de transferencia
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
                    
                    
   //vt                 
                    
                    ]
            }
                    
                    
                    
                    ,
                    {
                        xtype: 'container',
                        x: 20,
                        y: 515,
                        width: 300,
                        height: 65,
                        layout: 'absolute',
                        items: [
                            {
                                xtype: 'textarea',
                                width: 295,
                                height: 60,
                                id: PF+'txtObservaciones',
                                name: PF+'txtObservaciones'
                            }
                        ]
                    },
                    {
                        xtype: 'label',
                        text: 'Observaciones:',
                        x: 20,
                        y: 500
                    },
                    {
                        xtype: 'textfield',
                        x: 350,
                        y: 515,
                        width: 170,
                        maxLengthText: 25,
                        id: PF+'txtSolicita',
                        name: PF+'txtSolicita',
                        disabled:true
                    },
                    {
                        xtype: 'uppertextfield',
                        x: 540,
                        y: 515,
                        width: 170,
                        maxLengthText: 25,
                        id: PF+'txtAutoriza',
                        name: PF+'txtAutoriza'
                    },
                    {
                        xtype: 'label',
                        text: 'Solicita:',
                        x: 350,
                        y: 500
                    },
                    {
                        xtype: 'label',
                        text: 'Autoriza:',
                        x: 540,
                        y: 500
                    },
                        NS.cmbAreaDestino
                    ,
                    {
                        xtype: 'label',
                        text: 'Area Destino:',
                        x: 730,
                        y: 500
                    },
                    {
                        xtype: 'button',
                        text: 'Ejecutar',
                        x: 790,
                        y: 550,
                        width: 80,
                        id: PF+'btnEjecutar',
                        name: PF+'btnEjecutar',
                        listeners:{
                    	
                        	click:{
                    	
                        		fn:function(e){
                    	           
                        			var matriz = new Array (); 
                        			var matrizGrid= new Array();
                        			var reg = {};
                        			var recordsGrid = NS.gridDatos.store.data.items;
                        			
                        			reg.idEmpresa=NS.idEmpresa;
                        			reg.idPersona=NS.idPersona;
									reg.nomPersona=NS.nomPersona;
                        			reg.numPartida=NS.numPartida;//contador de el numero de partidas
									//reg.idRubro=NS.rubro;//se cambio por uno fijo por el momento
                        			reg.idRubro="79991";//id de rubro de egreso no identificado
                        			reg.descRubro=NS.descRubro;
									//reg.idGrupo=NS.grupo;//se cambio por uno fijo por el momento
                        			reg.idGrupo="79990";//id de grupo egreso no identificado
									reg.idDepto=NS.depto;
									reg.idArea=NS.area;
									reg.idUsuario=NS.idUsuario;
									reg.noDocto = "0"; //Ext.getCmp(PF + 'txtNoDocto').getValue();
									reg.chkTodos=NS.chkTodos ;
									reg.chkProvUnico=NS.chkProvUnico ;	
									reg.idFormaPago=NS.idFormaPago;
									reg.fechaFactura = cambiarFecha('' + Ext.getCmp(PF + 'fechaFactura').getValue());
									reg.fechaPago = cambiarFecha('' + Ext.getCmp(PF + 'fechaPago').getValue());
									reg.importeOriginal = NS.unformatNumber(Ext.getCmp(PF + 'txtImporteOriginal').getValue());
									reg.idDivOriginal=NS.idDivOriginal ;
									reg.chkContabilizar=NS.chkContabilizar ;
									reg.concepto = Ext.getCmp(PF + 'txtConcepto').getValue();
									reg.idDivPago=NS.idDivPago ;
									reg.tipoCambio = Ext.getCmp(PF + 'txtTipoCambio').getValue();
									reg.importePago = NS.unformatNumber(Ext.getCmp(PF + 'txtImportePago').getValue());
									reg.optNac=NS.optNac;
									reg.optExt=NS.optExt;
									reg.idBancoBenef=NS.idBancoBenef ;
									reg.idChequera = Ext.getCmp(PF + 'cmbChequeras').getValue();
									reg.clabe = Ext.getCmp(PF + 'txtClabe').getValue();
									reg.sucursal = Ext.getCmp(PF + 'txtSucursal').getValue();
									reg.plaza = Ext.getCmp(PF + 'txtPlaza').getValue();
									reg.idCaja=NS.idCaja ;
									reg.chkLeyenda=NS.chkLeyenda ;
									reg.observaciones = Ext.getCmp(PF + 'txtObservaciones').getValue();
									reg.solicita = Ext.getCmp(PF + 'txtSolicita').getValue();
									reg.autoriza = Ext.getCmp(PF + 'txtAutoriza').getValue();
									reg.sumImporte = Ext.getCmp(PF + 'txtSumImporte').getValue();
									matriz[0] = reg; 
									
									var jsonString = Ext.util.JSON.encode(matriz);	
									
									for(var i=0; i<recordsGrid.length; i++) {
										var regGrid={};
										regGrid.partida=recordsGrid[i].get('numPartida');
										regGrid.rubro=recordsGrid[i].get('rubro');
										regGrid.descRubro=recordsGrid[i].get('descripcion');
										regGrid.importe=recordsGrid[i].get('importe');
										regGrid.departamento=recordsGrid[i].get('departamento');
										regGrid.grupo=recordsGrid[i].get('grupo');
										regGrid.area=recordsGrid[i].get('area');
										matrizGrid[i]=regGrid;
									}
									
									var jsonStringGrid = Ext.util.JSON.encode(matrizGrid);
									CapturaSolicitudesPagoAction.ejecutarSolicitud(jsonString,jsonStringGrid, function(result, e){
										if(result.msgUsuario !== null && result.msgUsuario !== '') {
											Ext.Msg.alert('Información SET', '' + result.msgUsuario);
											
											if(result.terminado == '1') {
												CapturaSolicitudesPagoAction.letrasCantidad(NS.idDivPago, NS.unformatNumber(Ext.getCmp(PF + 'txtImporteOriginal').getValue()), function(res, e) {
													NS.reporte(('' + result.msgUsuario).substring((''+result.msgUsuario).indexOf(':') + 1, result.msgUsuario.length),
																"$ " + Ext.getCmp(PF + 'txtImporteOriginal').getValue() + " *** " + res + " *** ");
													NS.limpiar();
												});
												//NS.limpiar();
											}
										}
									});
									
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 880,
                        y: 550,
                        width: 80,
                        id: PF+'btnLimpiar',
                        name: PF+'btnLimpiar',
                        listeners:{
                        click:{
                        		fn:function(e){
						 			NS.limpiar();
						 			NS.habilitarComponentes(false);
                        		}
                        	}
                        }
                    }
                        
            
            ,
            {
                xtype: 'fieldset',
                title: 'Partidas',
                y: 215,
                width: 985,
                height: 280,
                layout: 'absolute',
                items: [
                   /* {
                        xtype: 'textfield',
                        x: 80,
                        y: 20,
                        width: 40,
                        id: 'txtPartida'
                    },*/
                    {
                        xtype: 'textfield',
                        x: 85,
                        y: 15,
                        width: 170,
                        disabled:true,
                        id: PF+'txtImportePartida',
                        name: PF+'txtImportePartida',
                        listeners:{
                        	change:{
                        		fn:function(caja, valor){
                        			NS.importe=caja.getValue();
                        			var recordsGridDatos= NS.storeDatos.data.items;
								 	var datosClase = NS.gridDatos.getStore().recordType;
								 	var indice;
								 	
								 	if(NS.sumaImpPartidas(NS.importe)) {
								 		Ext.getCmp(PF + 'txtImportePartida').setValue('');
								 		return;
								 	}

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
				            		var sumImporte=0;
				            		var recordsPos=0;
			            			for(var i=0; i<recordsGridDatos.length;i=i+1)
			              			{
			              				recordsPos = parseFloat(recordsGridDatos[i].get('importe'));
	              						sumImporte = sumImporte + recordsPos;	
			              			}
			            			Ext.getCmp(PF+'txtSumImporte').setValue(NS.formatNumber(sumImporte));	
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 65,
                        width: 70,
                        id: PF+'txtIdGrupo',
                        name: PF+'txtIdGrupo',
                        disabled:true,
                        listeners:{
                        	change:{
                        		fn:function(caja, valor){
                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdGrupo',NS.cmbGrupo.getId());
                        			NS.accionarGrupo(valueCombo);	
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 115,
                        width: 70,
                        id: PF+'txtIdRubro',
                        name: PF+'txtIdRubro',
                        disabled:true,
                        listeners:{
                        	change:{
                        		fn:function(caja, valor){
                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdRubro',NS.cmbRubro.getId());
                        			NS.accionarRubro(valueCombo);
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 165,
                        width: 70,
                        id: PF+'txtIdDivision',
                        name: PF+'txtIdDivision'
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 215,
                        width: 70,
                        id: PF+'txtIdAreaDet',
                        name: PF+'txtIdAreaDet',
                        listeners: {
                        	change: {
                        		fn:function(caja, valor) {
	                        		var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdAreaDet', NS.cmbAreaDet.getId());
	                        		NS.accionarAreaDet(valueCombo);
                        		}
                        	}
                        }
                    },
                    
                      NS.cmbGrupo,
                   	  NS.cmbRubro,
                      NS.cmbDivision,
                      NS.cmbAreaDet,
                      NS.gridDatos,
                    {
                        xtype: 'button',
                        text: 'Crear Nuevo',
                        x: 10,
                        y: 15,
                        id: PF+'btnNuevo',
                        name: PF+'btnNuevo',
                        listeners:{
                        	click:{
                        		fn: function(e){
                        		
                        		//NS.cmbGrupo.setDisabled(false); //se comentaron para que no se pueda ingresar por el momento
								//NS.cmbRubro.setDisabled(false);
								NS.cmbAreaDet.setDisabled(false);
								NS.cmbDivision.setDisabled(false);
								Ext.getCmp(PF + 'txtImportePartida').setDisabled(false);
                        		Ext.getCmp(PF+'txtImportePartida').setValue("");
                        		
                        		if(NS.numPartida >= 0)
                        		{
									NS.rubro=79991;
									NS.descRubro='Egresos no Identificados';
									NS.importe=0;
									NS.grupo=79990;
									NS.depto=0;
									NS.area=0;  
									NS.cmbGrupo.reset(); 
									NS.cmbRubro.reset();
									NS.cmbAreaDet.reset();
									NS.cmbDivision.reset();
									Ext.getCmp(PF+'txtIdGrupo').setValue(""); 
									Ext.getCmp(PF+'txtIdRubro').setValue(""); 
									Ext.getCmp(PF+'txtIdAreaDet').setValue(""); 
									Ext.getCmp(PF+'txtIdDivision').setValue("");               			
                        		}
                        		
                        		NS.numPartida=NS.numPartida+1;
                        		var indice=-1;
                        			var datosClase = NS.gridDatos.getStore().recordType;
                        			var datos = new datosClase({
					                	numPartida: NS.numPartida,
										rubro: '',
										descripcion:'',
										importe:'',
										departamento:'',
										grupo:'',
										area:''
					            	});
                        			
			                        NS.gridDatos.stopEditing();
				            		NS.storeDatos.insert(indice=indice+1, datos);
				            		NS.gridDatos.getView().refresh();
				            		NS.gridDatos.store.sort('numPartida','ASC');
				            		Ext.getCmp(PF+'txtSumPartidas').setValue(NS.numPartida);
                        		}
                        	}
                        }
                    },
                    /*{
                        xtype: 'label',
                        text: 'Partida',
                        x: 80,
                        y: 0
                    },*/
                    {
                        xtype: 'label',
                        text: 'Importe:',
                        x: 130,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Cuenta Mayor:',
                        x: 10,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Sub Cuenta:',
                        x: 10,
                        y: 100
                    },
                    {
                        xtype: 'label',
                        text: 'Departamento:',
                        x: 10,
                        y: 150
                    },
                    {
                        xtype: 'label',
                        text: 'Location:',
                        x: 10,
                        y: 200
                    },
                    {
                        //xtype: 'numberfield',
                        xtype: 'textfield',
                        x: 430,
                        y: 185,
                        id: PF+'txtSumImporte',
                        name: PF+'txtSumImporte',
                        readOnly: true
                    },
                    {
                        xtype: 'textfield',
                        x: 350,
                        y: 185,
                        width: 50,
                        id: PF+'txtSumPartidas',
                        name:PF+'txtSumPartidas',
                        readOnly: true
                    },
                    {
                        xtype: 'label',
                        text: 'Partidas:',
                        x: 350,
                        y: 170
                    },
                    {
                        xtype: 'label',
                        text: 'Importe Acumulado:',
                        x: 430,
                        y: 170
                    },
                    {
                        xtype: 'button',
                        text: 'Eliminar',
                        x: 870,
                        y: 175,
                        width: 80,
                        height: 22,
                        id: PF + 'btnEliminar',
                        name: PF + 'btnEliminar',
                        listeners:{
                        	click:{
                        		fn:function(e){
                    				NS.eliminarPartida();
                    			}
                        	}
                        }
                    }
                
                    
                    ]
                
                
                
            }

            ]
    }
            
            
         ]
	});
	NS.CapturaSolicitudesPago.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
