Ext.onReady(function() 
{
	var NS = Ext.namespace('apps.SET.Interfases.CargaPagos'); //Nombre del archivo de trabajo
	NS.tabContId = apps.SET.tabContainerId; //Variable para definir el tamaño del panel principal
	NS.idUsuario = apps.SET.iUserId; //Como global para el id de usuario
	var PF = apps.SET.tabID + '.'; //Se coloca PF para los nombres de los componentes
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API); //para poder tener el enlace a las clases java
	Ext.QuickTips.init(); //Constructor
	NS.iTipoPago = 0;
	NS.fecHoy = apps.SET.FEC_HOY;
	//String obtieneItems = "";
	//int variable = 0;
	
	function cambiarFecha(fecha) {
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		
		for(var i=0;i<12;i=i+1) {
			if(mesArreglo[i]===mesDate) {
			var mes=i+1;
				if(mes<10)
				mes = '0' + mes;
			}
		}
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	}
	
	//Formato de un numero a monetario
	NS.formatNumber = function(num, prefix){
		num = num.toString();
		
		if(num.indexOf('.') > -1){
			if(num.substring(num.indexOf('.')).length < 3){
				num = num + '0';
			}
		}else{
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
	
	NS.unformatNumber=function(num) {
		//return num.replace(/([^0-9\.\-])/g,''*1);
		while(num.indexOf(',')>-1){
			num = num.replace(',','');
		}
		return parseFloat(num);
	};
	
	// *********** Declaracion de etiquetas ***************************************************  
	//Empresa
	NS.lblEmpresa = new Ext.form.Label
	({
		text: 'Empresa',
		x: 10,
		y: 0		
	});
	
	//Etiquetas para total de registros
	NS.lblTotalError = new Ext.form.Label
	({
		text: 'Error',
		x: 5,
		y: 0		
	});
	
	
	NS.lblTotalOk = new Ext.form.Label
	({
		text: 'OK',
		x: 65,
		y: 0
	});
	
	NS.lblTotalRegistros = new Ext.form.Label
	({
		text: 'Total',
		x: 125,
		y: 0
	});
	
	//Label para Total de importe
	NS.lblImporteMN = new Ext.form.Label
	({
		text: 'MN',
		x: 5,
		y: 0
	});
	
	NS.lblImporteDLS = new Ext.form.Label
	({
		text: 'DLS',
		x: 115,
		y: 0
	});
	
	//Etiqueta Descripcion propuesta
	NS.labDescPropuesta = new Ext.form.Label({
		text: 'Descripción:',
		x: 10,
		y: 10
	});
	
	//Etiqueta Fecha Propuesta
	NS.labFecPropuesta = new Ext.form.Label({
		text: 'Fecha de Pago:',
		x: 350,
		y: 10
	});
		
	// *********** Declaracion de Cajas ***************************************************
	//ID Empresa
	NS.txtIdEmpresa = new Ext.form.TextField
	({
		id: PF + 'txtIdEmpresa',
		name: PF + 'txtIdEmpresa',
		x: 10,
		y: 15,
		width: 50,
		tabIndex: 0,
		listeners: {
			change: {
				fn: function(caja, valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdEmpresa', NS.cmbEmpresa.getId());
						
						if(valueCombo == 0)
							Ext.getCmp(PF + 'txtIdEmpresa').setValue('0');
					}else {
						NS.cmbEmpresa.reset();
						Ext.getCmp(PF + 'txtIdEmpresa').setValue('0');
					}
                }
			}
    	}
	});
	//Etiquetas del panelTotales
	NS.txtTotalError = new Ext.form.TextField
	({
		id: PF + 'txtTotalError',
		name: PF + 'txtTotalError',
		x: 5,
		y: 15,
		width: 50,
		tabIndex: 4
	});
	
	NS.txtTotalOk = new Ext.form.TextField
	({
		id: PF + 'txtTotalOk',
		name: PF + 'txtTotalOk',
		x: 65,
		y: 15,
		width: 50,
		tabIndex: 5
	});
	
	NS.txtTotalRegistros = new Ext.form.TextField
	({
		id: PF + 'txtTotalRegistros',
		name: PF + 'txtTotalRegistros',
		x: 125,
		y: 15,
		width: 50,
		tabIndex: 6
	});
	
	//TextField para importes totales
	NS.txtImporteMN = new Ext.form.TextField
	({			
		id: PF + 'txtImporteMN',
		name: PF + 'txtImporteMN',
		x: 5,
		y: 15,
		width: 100
	});
	
	NS.txtImporteDLS = new Ext.form.TextField
	({
		id: PF + 'txtImporteDLS',
		name: PF + 'txtImporteDLS',
		x: 115,
		y: 15,
		width: 100
	});
	
	// *********** Declaracion de RadioButtom ***************************************************
	//RadioButtom para el tipo de envio, por el momento solo coloco SAP
	NS.optTipo = new Ext.form.RadioGroup
	({
		id: PF + 'optTipo',
		name: PF + 'optTipo',
		x: 25,
		y: 0,
		columns: 2,
		items:
		[
		 	{boxLabel: 'SAP', name: 'optSAP', inputValue: 1},
		 	{boxLabel: 'SAE', name: 'optSAP', inputValue: 2, checked: true}
		]
	});
	
	//Caja de descripcion de propuesta
	NS.txtDescPropuesta = new Ext.form.TextField
	({
		id: PF + 'txtDescPropuesta',
		name: PF + 'txtDescPropuesta',
		x: 10,
		y: 25,
		width: 300
	});
	
	//Caja Fecha de la Propuesta
	NS.txtFecPropuesta = new Ext.form.DateField({
		id: PF + 'txtFecPropuesta',
		name: PF + 'txtFecPropuesta',
		x: 350,
		y: 25,
		width: 110,
		format: 'd/m/Y',
		value :apps.SET.FEC_HOY
	});
	
	// *********** Declaracion de Store ***************************************************	
	//storeEmpresas para sacar las empresas
	NS.storeEmpresas = new Ext.data.DirectStore
	({
		paramsAsHash: false,
		root: '',
		baseParams: {idUsuario: NS.idUsuario},
		paramOrder: ['idUsuario'],
		directFn: CargaPagosAction.llenaComboEmpresas,
		idProperty: 'noEmpresa',
		fields:
		[
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'}
		],
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen empresas asignadas al usuario');
				
				/*
				//Se agrega la opcion todas las empresas
	 			var recordsStoreEmpresas = NS.storeEmpresas.recordType;	
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '*************** TODAS ***************'
		       	});
		   		NS.storeEmpresas.insert(0, todas);
		   		*/
			}
		}
	});
	
	NS.storeEmpresas.load();
	

	/**data del store estatus*/
    NS.dataEstatus = [
                       [ 'I', 'IMPORTADO' ],
                       [ 'R', 'RECHAZADO' ],
                       [ 'P', 'PENDIENTE' ]
                      ];
					  
	/**store Estatus*/
	NS.storeEstatus = new Ext.data.SimpleStore( {
			idProperty: 'id',  		
			fields : [ 
						{name :'id'}, 
						{name :'desc'}
					 ]
	});
	NS.storeEstatus.loadData(NS.dataEstatus);
	
	NS.formatoDouble = function(valor, prefix)
	{		
		valor = valor.toString();
		if (valor.indexOf(".") > 0)
		{
			if(valor.substring(valor.indexOf(".")).length < 3)
			{
				valor = valor + '0';
			}
		}
		else
		{
			valor = valor + '.00';
		}
		
		prefix = prefix || '';
		var splitStr = valor.split('.');
		var splitLeft = splitStr[0];
		var splitRight = splitStr.length > 1 ? '.' + splitStr[1] : '';
		var regx = /(\d+)(\d{3})/;
		while (regx.test(splitLeft)) {
			splitLeft = splitLeft.replace(regx, '$1' + ',' + '$2');
		}
			return prefix + splitLeft + splitRight;
	}
	
	//store para sacar la ruta del ERP y llenar el Grid
	NS.storeLlenaGrid = new Ext.data.DirectStore
	({		
		paramAsHash: false,
		root: '',
		paramOrder: ['optTipoValor', 'fecHoy', 'usuarioAlta', 'txtIdEmpresa', 'estatus', 'fecIni', 'fecFin'],
		baseParams: {},
		directFn: CargaPagosAction.llenaGrid,
		idProperty: 'optTipoValor',
		fields:
		[
		 	{name: 'noEmpresa'},
		 	{name: 'noDocto'},
		 	{name: 'secuencia'},
		 	{name: 'fecValor'},
		 	{name: 'fechaAlta'},
		 	{name: 'usuarioAlta'},
		 	{name: 'noBenef'},
		 	{name: 'importe'},
		 	{name: 'idDivisa'},		 	
		 	{name: 'concepto'},
		 	{name: 'idFormaPago'},
		 	{name: 'origen'},
		 	{name: 'idGrupo'},
		 	{name: 'idRubro'},
		 	{name: 'mandante'},
		 	{name: 'profitCenter'},
		 	{name: 'centroCostos'},
		 	{name: 'rfc'},
		 	{name: 'curp'},
		 	{name: 'idCaja'},
		 	{name: 'clabe'},
		 	{name: 'descPropuesta'},
		 	{name: 'chequeraBenef'},
		 	{name: 'idBancoBenef'},
		 	{name: 'nombreBancoBenef'},
		 	{name: 'sucursalBenef'},
		 	{name: 'paisBancoBenef'},
		 	{name: 'cdBancoBenef'},
		 	{name: 'swiftBenef'},
		 	{name: 'iban'},
		 	{name: 'forFurtherCredit'},
		 	{name: 'nombreBancoIntermediario'},
		 	{name: 'aba'},
		 	{name: 'paisBancoIntermediario'},
		 	{name: 'cdBancoIntermediario'},
		 	{name: 'chequeraPagadora'},
		 	{name: 'bancoPagador'},
		 	{name: 'apellidoPaterno'},
		 	{name: 'apellidoMaterno'},
		 	{name: 'nombre'},
		 	{name: 'razonSocial'},
		 	{name: 'mail'},
		 	{name: 'direccionBenef'},
		 	{name: 'cdBenef'},
		 	{name: 'regionBenef'},
		 	{name: 'cpBenef'},
		 	{name: 'paisBenef'},
		 	{name: 'localizacion'},
		 	{name: 'noCheque'},
		 	{name: 'tipoDocumento'},
		 	{name: 'conceptoRechazo'},	
		 	{name: 'tipoPersona'},
		 	{name: 'totalOK'},
		 	{name: 'totalError'},
		 	{name: 'totalRegistros'},
		 	{name: 'totalImporteMN'},
		 	{name: 'totalImporteDLS'},
		 	{name: 'noFactura'},
		 	{name: 'fecFact'},
		 	{name: 'impSolic'},
		 	{name: 'tipoCamb'},
		 	{name: 'cveLeyen'},
		 	{name: 'benefAlt'},
		 	{name: 'idBcoAlt'},
		 	{name: 'idChqAlt'},
		 	{name: 'gpoTesor'},
		 	{name: 'codBloq'},
		 	{name: 'indMayEs'},
		 	{name: 'estatusPropuesta'},
		 	{name: 'causaRech'},
		 	{name: 'fechaImp'}
		],		
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg:"Cargando..."});
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen registros en la busqueda');				
				else
				{
					var obtieneItems = NS.storeLlenaGrid.data.items;				
			 		var variable = obtieneItems.length;
			 		
					Ext.getCmp(PF + 'txtImporteDLS').setValue(NS.formatoDouble(obtieneItems[variable - 1].get('totalImporteDLS')));
					Ext.getCmp(PF + 'txtImporteMN').setValue(NS.formatoDouble(obtieneItems[variable - 1].get('totalImporteMN')));
					Ext.getCmp(PF + 'txtTotalError').setValue(obtieneItems[variable - 1].get('totalError'));
					Ext.getCmp(PF + 'txtTotalOk').setValue(obtieneItems[variable - 1].get('totalOK'));
					Ext.getCmp(PF + 'txtTotalRegistros').setValue(obtieneItems[variable - 1].get('totalRegistros'));
					
				}
			}
		}
		
	});
	
	// *********** Declaracion de Funciones ***************************************************	
	//Funcion para limpiar la pantalla	
	NS.limpiaPantalla = function()
	{
		Ext.getCmp(PF + 'txtIdEmpresa').setValue('');
		Ext.getCmp(PF + 'txtTotalRegistros').reset();
		Ext.getCmp(PF + 'txtTotalOk').reset();
		Ext.getCmp(PF + 'txtTotalError').reset();
		Ext.getCmp(PF + 'txtImporteMN').setValue('0.00');
		Ext.getCmp(PF + 'txtImporteDLS').setValue('0.00');
		NS.cmbEmpresa.reset();
		NS.cmbEstatus.reset();
//		NS.storeEmpresas.removeAll();
		NS.storeLlenaGrid.removeAll();
		NS.gridDatos.getView().refresh();
	};
	
	//Funcion para mandar todos los campos del Grid al Business	
	NS.insertaRegistros = function()
	{
		var matriz = new Array();
		var obtieneGrid = NS.storeLlenaGrid.data.items;		
		
		if (obtieneGrid.length <= 0) {
			Ext.Msg.alert('SET', 'No existen registros para importar');
			return;
		}
		
		for(var i = 0; i < obtieneGrid.length; i++)
		{
			var datos = {};
			datos.noEmpresa = obtieneGrid[i].get('noEmpresa');
			datos.noDocto = obtieneGrid[i].get('noDocto');
			datos.secuencia = obtieneGrid[i].get('secuencia');
			datos.fecValor = obtieneGrid[i].get('fecValor');			
			datos.fechaAlta = NS.fecHoy;
			datos.usuarioAlta = NS.idUsuario;
			datos.noBenef = obtieneGrid[i].get('noBenef');
			datos.importe = obtieneGrid[i].get('importe');
			datos.idDivisa = obtieneGrid[i].get('idDivisa');
			datos.concepto = obtieneGrid[i].get('concepto');
			datos.idFormaPago = obtieneGrid[i].get('idFormaPago');
			datos.origen = obtieneGrid[i].get('origen');
			datos.idGrupo = obtieneGrid[i].get('idGrupo');
			datos.idRubro = obtieneGrid[i].get('idRubro');
			datos.mandante = obtieneGrid[i].get('mandante');
			datos.profitCenter = obtieneGrid[i].get('profitCenter');
			datos.centroCostos = obtieneGrid[i].get('centroCostos');
			datos.rfc = obtieneGrid[i].get('rfc');
			datos.curp = obtieneGrid[i].get('curp');
			datos.idCaja = obtieneGrid[i].get('idCaja');
			datos.clabe = obtieneGrid[i].get('clabe');
			datos.descPropuesta = obtieneGrid[i].get('descPropuesta');
			datos.chequeraBenef = obtieneGrid[i].get('chequeraBenef');
			datos.idBancoBenef = obtieneGrid[i].get('idBancoBenef');
			datos.nombreBancoBenef = obtieneGrid[i].get('nombreBancoBenef');
			datos.sucursalBenef = obtieneGrid[i].get('sucursalBenef');
			datos.paisBancoBenef = obtieneGrid[i].get('cdBancoBenef');
			datos.cdBancoBenef = obtieneGrid[i].get('cdBancoBenef');
			datos.swiftBenef = obtieneGrid[i].get('swiftBenef');
			datos.iban = obtieneGrid[i].get('iban');
			datos.forFurtherCredit = obtieneGrid[i].get('forFurtherCredit');
			datos.nombreBancoIntermediario = obtieneGrid[i].get('nombreBancoIntermediario');
			datos.aba = obtieneGrid[i].get('aba');
			datos.paisBancoIntermediario = obtieneGrid[i].get('paisBancoIntermediario');
			datos.cdBancoIntermediario = obtieneGrid[i].get('cdBancoIntermediario');
			datos.chequeraPagadora = obtieneGrid[i].get('chequeraPagadora');
			datos.bancoPagador = obtieneGrid[i].get('bancoPagador');
			datos.apellidoPaterno = obtieneGrid[i].get('apellidoPaterno');
			datos.apellidoMaterno = obtieneGrid[i].get('apellidoMaterno');
			datos.nombre = obtieneGrid[i].get('nombre');
			datos.razonSocial = obtieneGrid[i].get('razonSocial');
			datos.mail = obtieneGrid[i].get('mail');
			datos.direccionBenef = obtieneGrid[i].get('direccionBenef');
			datos.cdBenef = obtieneGrid[i].get('cdBenef');
			datos.regionBenef = obtieneGrid[i].get('regionBenef');
			datos.cpBenef = obtieneGrid[i].get('cpBenef');
			datos.paisBenef = obtieneGrid[i].get('paisBenef');
			datos.localizacion = obtieneGrid[i].get('localizacion');
			datos.noCheque = obtieneGrid[i].get('noCheque');
			datos.tipoDocumento = obtieneGrid[i].get('tipoDocumento');
			datos.conceptoRechazo = obtieneGrid[i].get('conceptoRechazo');
			datos.tipoPersona = obtieneGrid[i].get('tipoPersona');
			matriz[i] = datos;
		}
		var jsonString = Ext.util.JSON.encode(matriz);
		
		CargaPagosAction.insertaRegistros(jsonString, NS.iTipoPago, Ext.getCmp(PF + 'txtIdEmpresa').getValue(), function(result, e) {
			if (result !== null && result !== '') {
				Ext.Msg.alert('SET', result + '!!');
				NS.limpiaPantalla();
			}
		});
	};
	// *********** Declaracion de Combos ***************************************************	
	//Descripcion de empresa
	NS.cmbEmpresa = new Ext.form.ComboBox
	({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',		
		x: 70,
		y: 15,
		width: 320,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:
		{
			select:
			{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtIdEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}
	});
	
	//Estatus de registro
	NS.cmbEstatus = new Ext.form.ComboBox
	({
		store: NS.storeEstatus,
		id: PF + 'cmbEstatus',
		name: PF + 'cmbEstatus',		
		x: 410,
		y: 15,
		width: 100,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 1,
		valueField: 'id',
		displayField: 'desc',
		autocomplete: true,
		emptyText: 'Seleccione un estatus',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
//					if(combo.getValue() == 'P' || combo.getValue() == 'R') {
//						//150 es el id de la facultad extra para poder importar CXP
//						CargaPagosAction.validaFacultad(150, function(resp, e){
//							if(resp > 0)
//								Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
//							else
//								Ext.Msg.alert('SET', 'No cuenta con la facultad para poder importar las CXP!!');
//						});
//					}else
//						Ext.getCmp(PF + 'btnEjecutar').setDisabled(true);
				}
			}
		}
	});
	
	// *********** Declaracion Y Creacion de Grid ***************************************************
	//Para multiple seleccion en el grid
	NS.seleccion = new Ext.grid.CheckboxSelectionModel
	({
		singleSelect: false
	});

	//Columnas del grid	    	
	NS.columnas = new Ext.grid.ColumnModel
	([	
	  	NS.seleccion,
	  	{header: 'No. Empresa', width: 90, dataIndex: 'noEmpresa', sortable: true},
	  	{header: 'No. Docto', width: 70, dataIndex: 'noDocto', sortable: true},
	  	{header: 'Secuencia', width: 50, dataIndex: 'secuencia', sortable: true, hidden: true},
	  	{header: 'Fec. Valor', width: 70, dataIndex: 'fecValor', sortable: true},
	  	{header: 'Fec. Alta', width: 70, dataIndex: 'fechaAlta', sortable: true},
	  	{header: 'Usuario alta', width: 30, dataIndex: 'usuarioAlta', sortable: true, hidden: true},
	  	{header: 'No. Benef', width: 80, dataIndex: 'noBenef', sortable:true},
	  	{header: 'Importe', width: 90, dataIndex: 'importe', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	  	{header: 'Divisa', width: 40, dataIndex: 'idDivisa', sortable: true},
	  	{header: 'Concepto', width: 200, dataIndex: 'concepto', sortable: true},
	  	{header: 'Forma Pago', width: 80, dataIndex: 'idFormaPago', sortable: true},
	  	{header: 'Origen', width: 40, dataIndex: 'origen', sortable:true, hidden: true},
	  	{header: 'Grupo', width: 30, dataIndex: 'idGrupo', sortable: true, hidden:true},
	  	{header: 'Rubro', width: 30, dataIndex: 'idRubro', sortable: true, hidden:true},
	  	{header: 'Mandante', width: 30, dataIndex: 'mandante', sortable: true, hidden: true},
	  	{header: 'Profit Center', width: 30, dataIndex: 'profitCenter', sortable: true, hidden: true},
	  	{header: 'Centro Costos', width: 30, dataIndex: 'centroCostos', sortable: true, hidden: true},
	  	{header: 'RFC', width: 80, dataIndex: 'rfc', sortable: true, hidden: true},
	  	{header: 'Curp', width: 80, dataIndex: 'curp', sortable: true, hidden: true},
	  	{header: 'Id Caja', width: 30, dataIndex: 'idCaja', sortable: true, hidden: true},
	  	{header: 'Clabe', width: 80, dataIndex: 'clabe', sortable: true, hidden: true},
	  	{header: 'Desc. Propuesta', width: 80, dataIndex: 'descPropuesta', sortable: true, hidden: true},
	  	{header: 'Cheq. Benef', width: 70, dataIndex: 'chequeraBenef', sortable: true, hidden: true},
	  	{header: 'Banco Benef', width: 80, dataIndex: 'idBancoBenef', sortable: true, hidden: true},
	  	{header: 'Nombre Banco', width: 80, dataIndex: 'nombreBancoBenef', sortable: true, hidden: true},
	  	{header: 'Suc Benef', width: 30, dataIndex: 'sucursalBenef', sortable: true, hidden: true},
	  	{header: 'Pais Banco Benef', width: 80, dataIndex: 'paisBancoBenef', sortable: true, hidden: true},
	  	{header: 'Cd Banco Benef', width: 30, dataIndex: 'cdBancoBenef', sortable: true, hidden: true},
	  	{header: 'Swift Benef', width: 60, dataIndex: 'switfBenef', sortable: true, hidden: true},
	  	{header: 'IBAN', width: 80, dataIndex: 'iban', sortable: true, hidden: true},
	  	{header: 'ForFurtherCredir', width: 30, dataIndex: 'forFurtherCredit', sortable: true, hidden: true},
	  	{header: 'Banco Intermediario', width: 30, dataIndex: 'nombreBancoIntermediario', sortable: true, hidden: true},
	  	{header: 'ABA', width: 60, dataIndex: 'aba', sortable: true, hidden: true},
	  	{header: 'Pais Banco Intermediario', width: 30, dataIndex: 'paisBancoIntermediario', sortable: true, hidden: true},	  	
	  	{header: 'Cd Banco Intermediario', width: 30, dataIndex: 'cdBancoIntermediario', sortable: true, hidden:true},
	  	{header: 'Chequera Pagadora', width: 80, dataIndex: 'chequeraPagadora', sortable: true, hidden: true},
	  	{header: 'Banco Pagador', width: 60, dataIndex: 'bancoPagador', sortable: true, hidden: true},
	  	{header: 'Apellido Paterno', width: 30, dataIndex: 'apellidoPaterno', sortable: true, hidden: true},
	  	{header: 'Apellido Materno', width: 30, dataIndex: 'apellidoMaterno', sortable: true, hidden: true},
	  	{header: 'Nombre', width: 30, dataIndex: 'nombre', sortable: true, hidden:true},
	  	{header: 'Razon Social', width: 250, dataIndex: 'razonSocial', sortable: true},
	  	{header: 'E-mail', width: 30, dataIndex: 'mail', sortable: true, hidden: true},
	  	{header: 'Direccion Benef', width: 30, dataIndex: 'direccionBenef', sortable: true, hidden: true},
	  	{header: 'Cd Benef', width: 30, dataIndex: 'cdBenef', sortable: true, hidden: true},
	  	{header: 'Region Benef', width: 30, dataIndex: 'regionBenef', sortable: true, hidden: true},
	  	{header: 'CP Benef', width: 30, dataIndex: 'cpBenef', sortable: true, hidden: true},
	  	{header: 'Pais Benef', width: 30, dataIndex: 'paisBenef', sortable: true, hidden: true},
	  	{header: 'Localizacion', width: 30, dataIndex: 'localizacion', sortable: true, hidden: true},
	  	{header: 'No Cheque', width: 60, dataIndex: 'noCheque', sortable: true, hidden: true},
	  	{header: 'Archivo', width: 80, dataIndex: 'tipoDocumento', sortable: true, hidden: true},
	  	{header: 'Causa Rechazo', width: 250, dataIndex: 'conceptoRechazo', sortable: true},
	  	{header: 'Tipo Persona', width: 30, dataIndex: 'tipoPersona', sortable: true, hidden: true},
	  	{header: 'No. Factura', width: 30, dataIndex: 'noFactura', sortable: true, hidden: true},
	  	{header: 'Fecha Factura', width: 30, dataIndex: 'fecFact', sortable: true, hidden:true},
	  	{header: 'Importe Solicitado', width: 140, dataIndex: 'impSolic', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	  	{header: 'Tipo Cambio', width: 30, dataIndex: 'tipoCamb', sortable: true, hidden: true, renderer: BFwrk.Util.rendererMoney},
	  	{header: 'Clave Leyenda', width: 30, dataIndex: 'cveLeyen', sortable: true, hidden: true},
	  	{header: 'Benef Alt.', width: 30, dataIndex: 'benefAlt', sortable: true, hidden: true},
	  	{header: 'Banco Alt.', width: 30, dataIndex: 'idBcoAlt', sortable: true, hidden: true},
	  	{header: 'Chequera Alt.', width: 30, dataIndex: 'idChqAlt', sortable: true, hidden: true},
	  	{header: 'Grupo Tesoreria', width: 30, dataIndex: 'gpoTesor', sortable: true, hidden: true},
	  	{header: 'Codigo Bloqueo', width: 30, dataIndex: 'codBloq', sortable: true, hidden: true},
	  	{header: 'Estatus', width: 60, dataIndex: 'estatusPropuesta', sortable: true},
	  	{header: 'Fecha Importacion', width: 130, dataIndex: 'fechaImp', sortable: true}
	]);	

	
	//Crea el grid donde asignamos filas y columnas
	NS.gridDatos = new Ext.grid.GridPanel
	({
		store: NS.storeLlenaGrid,
		id: 'gridDatos',
		x:10,
		y: 0,
		cm: NS.columnas,
		sm: NS.seleccion,
		width: 900,
		height: 250,
		stripeRows: true,
		columnLines: true,
		title: 'Pagos',
		listeners:
		{
			click:
			{
				fn: function(grid){
					var datosGrid = NS.gridDatos.getSelectionModel().getSelections();
					var importeMN = 0;
					var importeDLS = 0;
					
					if(datosGrid.length > 0) {
						for(var i=0; i<datosGrid.length; i++) {
							if(datosGrid[i].get('idDivisa') == 'MN')
								importeMN += parseFloat(NS.unformatNumber('' + datosGrid[i].get('importe')));
							else
								importeDLS += parseFloat(NS.unformatNumber('' + datosGrid[i].get('importe')));
						}
						Ext.getCmp(PF + 'txtImporteMN').setValue(NS.formatNumber(importeMN));
						Ext.getCmp(PF + 'txtImporteDLS').setValue(NS.formatNumber(importeDLS));
					}else {
						Ext.getCmp(PF + 'txtImporteMN').setValue('0.00');
						Ext.getCmp(PF + 'txtImporteDLS').setValue('0.00');
					}
				}
			}
		}
	});	
	
	//Funcion para que el usuario arme las propuestas
	NS.agrupar = function() {
		var matriz = new Array();
		var obtieneGrid = NS.gridDatos.getSelectionModel().getSelections();		
		
		if (obtieneGrid.length <= 0) {
			Ext.Msg.alert('SET', 'Seleccione registro para agrupar');
			return;
		}
		
		for(var i = 0; i < obtieneGrid.length; i++) {
			var datos = {};
			datos.noEmpresa = obtieneGrid[i].get('noEmpresa');
			datos.noDocto = obtieneGrid[i].get('noDocto');
			datos.fecValor = obtieneGrid[i].get('fecValor');
			datos.noBenef = obtieneGrid[i].get('noBenef');
			datos.importe = obtieneGrid[i].get('importe');
			datos.idDivisa = obtieneGrid[i].get('idDivisa');
			datos.razonSocial = obtieneGrid[i].get('razonSocial');
			datos.descPropuesta = Ext.getCmp(PF + 'txtDescPropuesta').getValue();
			datos.fecPropuesta = cambiarFecha('' + Ext.getCmp(PF + 'txtFecPropuesta').getValue());
			
			matriz[i] = datos;
		}
		var jsonString = Ext.util.JSON.encode(matriz);
		
		CargaPagosAction.armaPropuesta(jsonString, function(result, e) {
			Ext.getCmp(PF + 'contDatos').setVisible(false);
			
			if (result !== null && result !== '') {
				Ext.Msg.alert('SET', result);
				NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue());
				NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());
				var noEmpresa = Ext.getCmp(PF + 'txtIdEmpresa').getValue();
				NS.storeLlenaGrid.baseParams.optTipoValor = NS.iTipoPago;
				NS.storeLlenaGrid.baseParams.fecHoy = NS.fecHoy;
				NS.storeLlenaGrid.baseParams.usuarioAlta = NS.idUsuario;
				NS.storeLlenaGrid.baseParams.txtIdEmpresa = noEmpresa;
				NS.storeLlenaGrid.baseParams.fecIni = NS.sFecIni;
				NS.storeLlenaGrid.baseParams.fecFin = NS.sFecFin;
				NS.storeLlenaGrid.load();
			}
		});
	};
	
	// *********** Declaracion Panel ***************************************************
	//panel para total de registros
	NS.panelRegistrosTotales = new Ext.form.FieldSet
	({
		title: 'Registros',
		x: 5,
		y: 265,
		width: 200,
		height: 80,
		layout: 'absolute',
		hidden: true,
		items:
		[
		 	NS.lblTotalError,
		 	NS.lblTotalOk,
		 	NS.lblTotalRegistros,
		 	NS.txtTotalError,
		 	NS.txtTotalOk,
		 	NS.txtTotalRegistros
		]		 
	});
		
	//Panel de Tipo de envio
	NS.panelTipoArchivos = new Ext.form.FieldSet
	({		
		title: 'Tipo De Pagos',
		id: PF + 'panelTipoArchivos',
		x: 350,
		y: 200,
		width: 220,
		height: 90,
		layout: 'absolute',
		hidden: true,
		bodyStyle: 'background:#89ACB9',
		items:
		[
		 	NS.optTipo,
		 	{
		 		xtype: 'button',
		 		text: 'Continuar',
		 		x: 5,
		 		y: 28,
		 		width: 80,
		 		height: 22,		 		
		 		listeners:
		 		{
	        		click:
	        		{
	        			fn: function(e)
	        			{
	        				NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue());
	        				NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());
		 					Ext.getCmp(PF + 'panelTipoArchivos').setVisible(false);
		 					var tipoPago = Ext.getCmp(PF + 'optTipo').getValue();
		 					var noEmpresa = Ext.getCmp(PF + 'txtIdEmpresa').getValue();
		 					
		 					if(noEmpresa == '') {
		 						Ext.Msg.alert('SET', 'Seleccione una empresa para continuar!!');
		 						return;
		 					}
		 					if(Ext.getCmp(PF + 'cmbEstatus').getValue() == '') {
		 						Ext.Msg.alert('SET', 'Seleccione un estatus para continuar!!');
		 						return;
		 					}
		 					NS.iTipoPago = parseInt(tipoPago.getGroupValue());		 					
		 					NS.storeLlenaGrid.baseParams.optTipoValor = NS.iTipoPago;
		 					NS.storeLlenaGrid.baseParams.fecHoy = NS.fecHoy;
		 					NS.storeLlenaGrid.baseParams.usuarioAlta = NS.idUsuario;
		 					NS.storeLlenaGrid.baseParams.txtIdEmpresa = noEmpresa;
		 					NS.storeLlenaGrid.baseParams.estatus = Ext.getCmp(PF + 'cmbEstatus').getValue();
		 					NS.storeLlenaGrid.baseParams.fecIni = NS.sFecIni;
		 					NS.storeLlenaGrid.baseParams.fecFin = NS.sFecFin;
		 					NS.storeLlenaGrid.load();
	        			}
	        		}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 90,
		 		y: 28,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function(e)
		 				{
		 					Ext.getCmp(PF + 'panelTipoArchivos').setVisible(false);
		 				}
		 			}
		 		}
		 	}
		]
	});	
	
	//Panel para los montos totales
	NS.panelImportesTotales = new Ext.form.FieldSet
	({		
		title: 'Importes Totales',
		//x: 220,
		x: 5,
		y: 265,
		width: 245,
		height:80,
		layout: 'absolute',
		items:
		[
		 	NS.lblImporteMN,
		 	NS.lblImporteDLS,
		 	NS.txtImporteMN,
		 	NS.txtImporteDLS
		]
	});
	
	//Contenedor para la busqueda
	NS.panelBusqueda = new Ext.form.FieldSet
	({
		title: 'Busqueda',
		x: 10,
		y: 10,
		width: 950,
		height: 80,
		layout: 'absolute',
		items:
		[
		 	NS.lblEmpresa,
		 	NS.txtIdEmpresa,
		 	{
                xtype: 'datefield',
                format: 'd/m/Y',
                x: 520,
                y: 15,
                width: 90,
                name: PF + 'txtFechaInicial',
                id: PF + 'txtFechaInicial',
                value: apps.SET.FEC_HOY,
                listeners : {
                	change : {
                		fn : function(box, val)
                		{
                			//NS.agregarCriterioValor('FECHA VALOR', box.getId(), 'valor');
                		}
                	}
                }
            },
            {
                xtype: 'datefield',
                format: 'd/m/Y',
                x: 620,
                y: 15,
                width: 90,
                name: PF + 'txtFechaFinal',
                id: PF + 'txtFechaFinal',
                value: apps.SET.FEC_HOY,
                listeners : 
                {
                	change : 
                	{
                		fn : function(box, val)
                		{	
                			var fechaIni = Ext.getCmp(PF + 'txtFechaInicial').getValue();
                			if(fechaIni > box.getValue())
                			{
                				BFwrk.Util.msgShow('La fecha inicial no puede ser mayor', 'INFO');
                				return;
                			}
                			//NS.agregarCriterioValor('FECHA VALOR', box.getId(), 'valor');
                		}
                	}
                }
            },
		 	{
	        	xtype: 'button',
	        	text: 'Buscar',
	        	x: 740,
	        	y: 15,
	        	width: 80,
	        	height: 22,
	        	listeners: 
	        	{
	        		click:
	        		{
	        			fn:function(e)
	        			{	
	        				NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue());
	        				NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());  
		 					Ext.getCmp(PF + 'panelTipoArchivos').setVisible(false);
		 					//alert(NS.sFecIni + " <-> "+ NS.sFecFin);
		 					Ext.getCmp(PF + 'panelTipoArchivos').setVisible(false);
		 					var tipoPago = Ext.getCmp(PF + 'optTipo').getValue();
		 					var noEmpresa = Ext.getCmp(PF + 'txtIdEmpresa').getValue();
		 					
		 					if(noEmpresa == '') {
		 						Ext.Msg.alert('SET', 'Seleccione una empresa para continuar!!');
		 						return;
		 					}
		 					if(Ext.getCmp(PF + 'cmbEstatus').getValue() == '') {
		 						Ext.Msg.alert('SET', 'Seleccione un estatus para continuar!!');
		 						return;
		 					}
		 					
		 					if(NS.sFecIni == '' || NS.sFecFin == '') {
		 						Ext.Msg.alert('SET', 'Seleccione un rango de fechas!!');
		 						return;
		 					}
		 					
		 					NS.iTipoPago = parseInt(tipoPago.getGroupValue());		 					
		 					NS.storeLlenaGrid.baseParams.optTipoValor = NS.iTipoPago;
		 					NS.storeLlenaGrid.baseParams.fecHoy = NS.fecHoy;
		 					NS.storeLlenaGrid.baseParams.usuarioAlta = NS.idUsuario;
		 					NS.storeLlenaGrid.baseParams.txtIdEmpresa = noEmpresa;
		 					NS.storeLlenaGrid.baseParams.estatus = Ext.getCmp(PF + 'cmbEstatus').getValue();
		 					NS.storeLlenaGrid.baseParams.fecIni = NS.sFecIni;
		 					NS.storeLlenaGrid.baseParams.fecFin = NS.sFecFin;
		 					NS.storeLlenaGrid.load();
	        			}
	        		}
	        	}
	        },
	        NS.cmbEmpresa,
	        NS.cmbEstatus
		]	
	   
	});	
	
	//Panel que contiene el campo de fecha agrupar y la descripcion de la propuesta
	NS.contDatos = new Ext.form.FieldSet({
		title: 'Datos de Propuesta',
		id: PF + 'contDatos',
		width: 500,
		height: 160,
		x: 200,
		y: 110,
		layout: 'absolute',
		plain: false,
		bodyStyle: 'background:#89ACB9',
		hidden: true,
		items: [
		        NS.labDescPropuesta,
		        NS.txtDescPropuesta,
		        NS.labFecPropuesta,
		        NS.txtFecPropuesta,
		        {
                    xtype: 'button',
                    text: 'Continuar',
                    x: 250,
                    y: 100,
                    width: 80,
                    listeners: {
		        		click: {
		        			fn:function(e) {
		        				NS.agrupar();
		        			}
		        		}
		        	}
		        },{
                    xtype: 'button',
                    text: 'Cancelar',
                    x: 350,
                    y: 100,
                    width: 80,
                    listeners: {
		        		click: {
		        			fn:function(e) {
		        				Ext.getCmp(PF + 'contDatos').setVisible(false);
		        			}
		        		}
		        	}
		        }
		        ]
	});
	
	//Contenedor del Grid
	NS.panelGrid = new Ext.form.FieldSet
	({
		id: PF + 'panelGrid',
		x: 10,
		y: 100,
		width: 950,
		height: 370,
		layout: 'absolute',				
		items:
		[
		 	NS.gridDatos,
		 	NS.panelRegistrosTotales,		 	
		 	NS.panelImportesTotales,
		 	
		 	//Botones Imprimir, Ejecutar, Limpiar, Cerrar
		 	{
		  		xtype: 'button',
				text: 'Imprimir',
				x: 540,
				y: 300,
				width: 80,
				height: 22,
				hidden: true,
				listeners: 
				{
					click:
					{
		  				fn:function(e)
		  				{
				 			//	NS.buscar();
				 		}
				 	}
				 }
			},
			{
				xtype: 'button',
				text: 'Ejecutar',
				id: PF + 'btnEjecutar',
				x: 640,
				y: 300,
				width: 80,
				height: 22,
				hidden: true,
				//disabled: true,
				listeners:
				{
					click:
					{
						fn:function(e)
						{
							//var myMask = Ext.LoadMask(Ext.getBody(), {store: NS.insertaRegistros, msg: "Procesando información..."});
							//if(NS.iTipoPago == 1)
								NS.insertaRegistros();
							//else if(NS.iTipoPago == 2)
							//	Ext.Msg.alert('SET', 'Para la importación de SAE, tienen que seleccionar agrupar!!');
						}
					}
				}
			},
			{
				xtype: 'button',
				text: 'Excel',
				id: PF + 'btnExcel',
				x: 640,
				y: 300,
				width: 80,
				height: 22,
				listeners:
				{
					click:
					{
						fn:function(e)
						{
							NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue());
	        				NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());  
		 					Ext.getCmp(PF + 'panelTipoArchivos').setVisible(false);
		 					//alert(NS.sFecIni + " <-> "+ NS.sFecFin);
		 					Ext.getCmp(PF + 'panelTipoArchivos').setVisible(false);
		 					var tipoPago = Ext.getCmp(PF + 'optTipo').getValue();
		 					var noEmpresa = Ext.getCmp(PF + 'txtIdEmpresa').getValue();
		 					
		 					if(noEmpresa == '') {
		 						Ext.Msg.alert('SET', 'Seleccione una empresa para continuar!!');
		 						return;
		 					}
		 					if(Ext.getCmp(PF + 'cmbEstatus').getValue() == '') {
		 						Ext.Msg.alert('SET', 'Seleccione un estatus para continuar!!');
		 						return;
		 					}
		 					
		 					if(NS.sFecIni == '' || NS.sFecFin == '') {
		 						Ext.Msg.alert('SET', 'Seleccione un rango de fechas!!');
		 						return;
		 					}
		 					
		 					strParams = '?nomReporte=interfaces';
       						strParams += '&'+'nomParam1=tipoValor';
       						strParams += '&'+'valParam1='+NS.iTipoPago;
       						strParams += '&'+'nomParam2=fecHoy';
       						strParams += '&'+'valParam2='+ NS.fecHoy;
       						strParams += '&'+'nomParam3=usuarioAlta';
       						strParams += '&'+'valParam3='+NS.idUsuario;
       						strParams += '&'+'nomParam4=idEmpresa';
       						strParams += '&'+'valParam4='+ noEmpresa;
       						strParams += '&'+'nomParam5=estatus';
       						strParams += '&'+'valParam5='+Ext.getCmp(PF + 'cmbEstatus').getValue();
       						strParams += '&'+'nomParam6=fecIni';
       						strParams += '&'+'valParam6='+ NS.sFecIni;
       						strParams += '&'+'nomParam7=fecFin';
       						strParams += '&'+'valParam7='+NS.sFecFin;
       						window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
						}
					}
				}
			},
			{
				xtype: 'button',
				text: 'Limpiar',
				x: 740,
				y: 300,
				width: 80,
				height: 22,
				listeners:
				{
					click:
					{
						fn: function(e)
						{
							NS.limpiaPantalla();
						}
					}
				}
			},{
		  		xtype: 'button',
				text: 'Agrupar',
				x: 840,
				y: 300,
				width: 80,
				height: 22,
				hidden: true,
				listeners: {
					click: {
		  				fn:function(e) {
							Ext.getCmp(PF + 'txtDescPropuesta').setValue('');
							Ext.getCmp(PF + 'txtFecPropuesta').setValue(apps.SET.FEC_HOY);
							Ext.getCmp(PF + 'contDatos').setVisible(true);
				 		}
				 	}
				 }
			}
			//NS.panelTipoArchivos
		 ]
	});
	
	//Contenedor global
	NS.Global = new Ext.form.FieldSet
	({
		title: "",
		x: 20,
		y: 5,
		width: 1010,
		height: 495,
		layout: 'absolute',
		items:
		[
		 	NS.panelBusqueda,		 	
		 	NS.panelGrid,
		 	NS.panelTipoArchivos,
		 	NS.contDatos
		]
	});
			
	//Contenedor principal del formulario
	NS.ImportaPagos = new Ext.FormPanel
	({
		title: 'Importación de Pagos',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'importacionPagos',
		name: PF + 'importacionPagos',
		renderTo: NS.tabContId,
		items:
		[
		 	NS.Global		 	
		]
	});
	          
	NS.ImportaPagos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});

