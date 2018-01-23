Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.BancaElectronica.EstadoCuenta');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';

	NS.modificar = false;
	NS.numUsuario=0;
	NS.numEmpresa=0;
	NS.numBanco=0;
	NS.numchequera='';
	NS.fechaV='';
	NS.salvoBC='';
	NS.idBanco=0;
	NS.cargoAbono='';
	var vectorEstadoCuenta = {};
	var matrizEstadoCuenta=new Array();
	NS.idRubro=0;
	NS.idSecuencia=0;
	NS.banco=false;
	NS.chequera=false;
	NS.fecha=false;
	NS.cveConcepto='';
	
	  NS.darFormato=function(num){
	    	return Math.round(num*100)/100;
		};
		
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
		
		NS.unformatNumber=function(num) {
			//return num.replace(/([^0-9\.\-])/g,''*1);
			while(num.indexOf(',')>-1){
				num = num.replace(',','');
			}
			return parseFloat(num);
		};
		

	NS.storeLlenaComboEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {numUsuario: NS.idUsuario},
		paramOrder: ['numUsuario'],
		directFn: CapturaEstadoCuentaAction.llenaComboEmpresas,
		idProperty: 'idTipoEmpresa',
		fields: [
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records) {
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboEmpresas, msg: "Cargando..."});
		}
				
		}
	});
	
	NS.storeLlenaComboTipoBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {numEmpresa: 0},
		paramOrder: ['numEmpresa'],
		directFn: CapturaEstadoCuentaAction.llenaComboTipoBanco,
		idProperty: 'idTipoBanco',
		fields: [
		 	{name: 'idBanco'},
		 	{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records) {
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboTipoBanco, msg: "Cargando..."});
		}
				
		}
	});
	NS.storeLlenaChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {numEmpresa:0,numBanco:0},
		paramOrder: ['numEmpresa','numBanco'],
		directFn: CapturaEstadoCuentaAction.llenaComboChequera,
		idProperty: 'idTipoChequera',
		fields: [
		 	{name: 'idChequera'},
		 	{name: 'descChequera'}
		],
		listeners: {
			load: function(s, records) {
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaChequera, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
				
////					Ext.Msg.alert('SET', 'No existen Bancos dados de alta');
				Ext.Msg.alert('SET', 'No existen Bancos dados de alta');
		}
				
		}
	});
	
	NS.storeLlenaConcepto = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {numBanco:0},
		paramOrder: ['numBanco'],
		directFn: CapturaEstadoCuentaAction.llenaComboConcepto,
		//idProperty: 'idTipoConcepto',
		fields: [
		 	{name: 'conceptoSet'},
		 	{name: 'id_cve_concepto'}
		],
		listeners: {
			load: function(s, records) {
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaConcepto, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen los conceptos de este banco');

//			for(var i=0;i<records.length;i++){
//				console.log(records[i].get("id_cve_concepto"));
//			}
				
		}
				
		}
	});
	
	NS.lblBancoB= new Ext.form.Label({
		text: 'Banco',
		x: 0,
		y: 10

	});
	NS.lblFechaDeposito= new Ext.form.Label({
		text: 'Fecha Deposito',
		x: 0,
		y: 250

	});
	NS.lblChequeraB = new Ext.form.Label({
		text: 'Chequera',
		x: 300,
		y: 10

	});
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 15,
		y: 5
	});
	
	NS.lblFechaB = new Ext.form.Label({
		text: 'Fecha',
		x: 550,
		y: 10
	});
	NS.txtIdBancoB = new Ext.form.TextField({
		id: PF + 'txtIdBancoB',
		name: PF + 'txtIdBancoB',
		x: 0,
		y: 30,
		width: 50,		
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBancoB', NS.cmbTipoBancoB.getId());
					else
						NS.cmbTipoBancoB.reset();
					//BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBanco', NS.cmbTipoBanco.getId());
				}
			}
		}
	});
	NS.txtIdEmpresa = new Ext.form.TextField({
		id: PF + 'txtIdEmpresa',
		name: PF + 'txtIdEmpresa',
		x: 60,
		y: 0,
		width: 50,		
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdEmpresa', NS.cmbEmpresas.getId());
					else
						NS.cmbEmpresas.reset();
					//BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBanco', NS.cmbTipoBanco.getId());
				}
			}
		}
	});

	NS.cmbEmpresas= new Ext.form.ComboBox({
		store: NS.storeLlenaComboEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 120,
		y: 0,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdEmpresa', NS.cmbEmpresas.getId());
					NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	NS.txtFechaB = new Ext.form.DateField({
		id: PF + 'txtFechaB',
		name: PF + 'txtFechaB',
		x: 550,
		y: 30,
		width: 100,
		format: 'd-m-Y',
		value: apps.SET.FEC_HOY
	});
	
	NS.cmbTipoBancoB = new Ext.form.ComboBox({
		store: NS.storeLlenaComboTipoBanco,
		id: PF + 'cmbTipoBancoB',
		name: PF + 'cmbTipoBancoB',
		x: 65,
		y: 30,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Tipo Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdBancoB', NS.cmbTipoBancoB.getId());
					//alert(combo.getValue());
					NS.accionarCmbChequera(combo.getValue());
				}
			}
		}
	});
	NS.cmbChequeraB= new Ext.form.ComboBox({
		store: NS.storeLlenaChequera,
		id: PF + 'cmbChequeraB',
		name: PF + 'cmbChequeraB',
		x: 300,
		y: 30,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idChequera',
		displayField: 'descChequera',
		autocomplete: true,
		emptyText: 'Chequera',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					//alert(combo.getValue());
				}
			}
		}
	});
//	////////////////////////Nuevo etado de cuenta //////////////////////////////////////////////////

	NS.lblBanco = new Ext.form.Label({
		text: 'Banco',
		x: 0,
		y: 10

	});
	NS.lblChequera = new Ext.form.Label({
		text: 'Chequera',
		x: 300,
		y: 10

	});
	
	NS.lblConcepto = new Ext.form.Label({
		text: 'Concepto',
		x: 0,
		y: 70
	});
	
	NS.lblSucursal = new Ext.form.Label({
		text: 'Sucursal',
		x: 300,
		y: 70
	});
	
	NS.lblFolio = new Ext.form.Label({
		text: 'Folio',
		x: 0,
		y: 140
	});
	
	NS.lblImporte = new Ext.form.Label({
		text: 'Importe',
		x: 300,
		y: 140
	});
	NS.lblReferencia = new Ext.form.Label({
		text: 'Referencia',
		x: 0,
		y: 200
	});
	NS.lblObservaciones = new Ext.form.Label({
		text: 'Observaciones',
		x: 300,
		y: 200
	});
	NS.lblRubro= new Ext.form.Label({
		text: 'Rubro',
		x: 500,
		y: 140
	});

	
	NS.txtIdBanco = new Ext.form.TextField({
		id: PF + 'txtIdBanco',
		name: PF + 'txtIdBanco',
		x: 0,
		y: 30,
		width: 50,		
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBanco', NS.cmbTipoBanco.getId());
					else
						NS.cmbTipoBanco.reset();
				}
			}
		}
	});
	NS.txtFechaDeposito = new Ext.form.DateField({
		id: PF + 'txtFechaDeposito',
		name: PF + 'txtFechaDeposito',
		x: 0,
		y: 270,
		width: 100,
		format: 'd-m-Y',
		value: apps.SET.FEC_HOY
	});
	
	NS.cmbTipoBanco = new Ext.form.ComboBox({
		store: NS.storeLlenaComboTipoBanco,
		id: PF + 'cmbTipoBanco',
		name: PF + 'cmbTipoBanco',
		x: 60,
		y: 30,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Tipo Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdBanco', NS.cmbTipoBanco.getId());		
					NS.accionarCmbChequera(combo.getValue());
					NS.accionarCmbConcepto(combo.getValue());
				}
			}
		}
	});

	NS.cmbConcepto = new Ext.form.ComboBox({
	store: NS.storeLlenaConcepto,
		id: PF + 'cmbConcepto',
		name: PF + 'cmbConcepto',
		x: 0,
		y: 90,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'id_cve_concepto',
		displayField: 'conceptoSet',
		autocomplete: true,
		emptyText: 'Concepto',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					console.log("desc "+combo.getRawValue());
					NS.cveConcepto=combo.getValue();
					if(NS.cveConcepto==null || NS.cveConcepto==''){
						
					}
					NS.accionarCargoBono(combo.getRawValue());
				}
			}
		}
	});
	
	NS.cmbChequera= new Ext.form.ComboBox({
		store: NS.storeLlenaChequera,
		id: PF + 'cmbChequera',
		name: PF + 'cmbChequera',
		x: 300,
		y: 30,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idChequera',
		displayField: 'descChequera',
		autocomplete: true,
		emptyText: 'Chequera',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					NS.accionarSucursal(combo.getValue());
				}
			}
		}
	});
	
	NS.optBono = new Ext.form.RadioGroup({
		id: PF + 'optBono',
		name: PF + 'optBono',
		x: 520,
		y: 30,
		columns: 1,
		items:
		[
		 	{boxLabel: 'Egreso', name: 'optBonoIE', inputValue: 0, checked:true,
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							NS.bono = 'E'
		 						}
		 					}
		 				}
		 			}
		 	},
		 	{boxLabel: 'Ingreso', name: 'optBonoIE', inputValue: 1,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.bono = 'I'
		 					}
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	
	NS.accionarCmbBanco = function(comboValor)
	{
	
		NS.storeLlenaComboTipoBanco.baseParams.numEmpresa=comboValor;
		NS.storeLlenaComboTipoBanco.load();

	}
	NS.accionarCmbChequera = function(comboValor)
	{
		//alert(Ext.getCmp(PF+'txtIdEmpresa').getValue());
		NS.storeLlenaChequera.baseParams.numEmpresa=parseInt(Ext.getCmp(PF+'txtIdEmpresa').getValue());
		//alert(NS.storeLlenaChequera.baseParams.numEmpresa);
		NS.storeLlenaChequera.baseParams.numBanco=comboValor;	
		//alert(NS.storeLlenaChequera.baseParams.numBanco);
		NS.storeLlenaChequera.load();

	}

	NS.accionarCmbConcepto = function(comboValor)
	{
	
		NS.storeLlenaConcepto.baseParams.numBanco=comboValor;
		NS.storeLlenaConcepto.load();
	}
	
	NS.accionarCargoBono = function(comboValor)
	{
		//Ext.Msg.alert('SET', Ext.getCmp(PF + 'txtIdBanco').getValue());
		CapturaEstadoCuentaAction.obtenerCargo(comboValor,parseInt(Ext.getCmp(PF + 'txtIdBanco').getValue()), function(resultado,e){
			if (resultado != '' && resultado != 0 && resultado != undefined){
//				Ext.Msg.alert('SET', resultado);
				if(resultado=='E'){
			//Ext.Msg.alert('b',Ext.getCmp(PF + 'optBono').getValue().getGroupValue());
					Ext.getCmp(PF + 'optBono').setValue(0)[0];
					NS.cargoAbono='E';
				}else{
					if(resultado=='I'){
						//Ext.Msg.alert('b',Ext.getCmp(PF + 'optBono').getValue().getGroupValue());
						Ext.getCmp(PF + 'optBono').setValue(1)[1];
						NS.cargoAbono='I';
						NS.obtenerSBC(parseInt(Ext.getCmp(PF + 'txtIdBanco').getValue()),comboValor);
						
					}
				}
			}else{
				if (resultado == ''){	
					Ext.Msg.alert('SET', resultado);
				}
			}
		});
	}
	NS.obtenerSBC=function(banco,concepto){
		CapturaEstadoCuentaAction.obtenerSalvoBuenCobro(banco,concepto,function(resultado,e){
			if (resultado != '' && resultado != 0 && resultado != undefined){
				if(resultado=='N'){
//					Ext.Msg.alert('sbc', resultado);
					Ext.getCmp(PF+'chkSalvo').setValue(1);
					NS.salvoBC='N';
				}else{
					if(resultado=='S'){
//						Ext.Msg.alert('sbc', resultado);
						Ext.getCmp(PF+'chkSalvo').setValue(0);
						NS.salvoBC='S';
					}else{
						Ext.Msg.alert('SET', resultado);
					}
					
				}
			}else{
				Ext.Msg.alert('SET', 'No se encontro salvo buen cobro');
			}
		});
		
	}
	
	NS.accionarSucursal = function(comboValor)
	{
		//Ext.Msg.alert('SET', Ext.getCmp(PF + 'txtIdBanco').getValue());
		CapturaEstadoCuentaAction.obtenerSucursal(parseInt(Ext.getCmp(PF + 'txtIdBanco').getValue()),comboValor, function(resultado,e){
			if (resultado != '' && resultado != 0 && resultado != undefined){
//				Ext.Msg.alert('SET', resultado);
				Ext.getCmp(PF + 'txtSucursal').setValue(resultado);
			}else{
				if (resultado == ''){	
//					Ext.Msg.alert('SET', resultado);
					Ext.getCmp(PF + 'txtSucursal').setValue("");
				}
			}
		});
	}
	NS.cambiarFecha = function (fecha)
	{
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
	};
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {numEmpresa:0,banco:false,numBanco:0,chequera:false,numChequera:'',fecha:false,fechaV:''},
		paramOrder: ['numEmpresa', 'banco', 'numBanco','chequera','numChequera', 'fecha','fechaV'],
		directFn: CapturaEstadoCuentaAction.llenaGrid,
		fields:
		[	{name: 'cargoAbono'},
		 	{name: 'folioBanco'},
		 	{name: 'importe'},
		 	{name: 'referencia'},
		 	{name: 'conceptoSet'},
		 	{name: 'sucursal'},
		 	{name: 'fecValor'},
		 	{name: 'observacion'},
		 	{name: 'salvoBuenCobro'},
		 	{name: 'idRubro'},
		 	{name: 'idEstatusTrasp'},
		 	{name: 'secuencia'},
		],
		listeners: {
			
			load: function (s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando ..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existen datos para estos parametros de busqueda');
				}
				Ext.getCmp(PF + 'buscar').setDisabled(false);
			}
		}
	});

//	
	NS.buscar = function(){
//		if(Ext.getCmp(PF + 'txtTipoPersona').getValue()=='P'){
			if (Ext.getCmp(PF + 'cmbTipoBancoB').getValue()=='') {
				Ext.Msg.alert('SET', 'Debe seleccionar un banco');
				Ext.getCmp(PF + 'buscar').setDisabled(false);
//				Ext.Msg.alert('SET', Ext.getCmp(PF+'cmbChequeraB').getValue());
			}else{
				if(Ext.getCmp(PF+'cmbChequeraB').getValue()==''){
					Ext.Msg.alert('SET', 'Debe seleccionar una chequera');
					Ext.getCmp(PF + 'buscar').setDisabled(false);
				}else{
					
					if(Ext.getCmp(PF + 'cmbTipoBancoB').getValue()>0){
						NS.banco=true;
					}
					if(Ext.getCmp(PF+'cmbChequeraB').getValue()>0){
						NS.chequera=true;
					}
					
					if(NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFechaB').getValue())!=''){
						NS.fecha=true;
					}
					console.log(NS.banco+" "+NS.chequera+" "+NS.fecha);
					NS.storeLlenaGrid.baseParams.numEmpresa = parseInt(Ext.getCmp(PF+'txtIdEmpresa').getValue());
					NS.storeLlenaGrid.baseParams.banco=NS.banco;
					NS.storeLlenaGrid.baseParams.numBanco=parseInt(Ext.getCmp(PF + 'txtIdBancoB').getValue());
					NS.storeLlenaGrid.baseParams.chequera=NS.chequera;
					NS.storeLlenaGrid.baseParams.numChequera=Ext.getCmp(PF + 'cmbChequeraB').getValue();
					NS.storeLlenaGrid.baseParams.fecha=NS.fecha;
					NS.storeLlenaGrid.baseParams.fechaV= NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFechaB').getValue());

					NS.storeLlenaGrid.load();	
				
				}

			
			}

	};
	NS.guardar = function(){
			if (Ext.getCmp(PF + 'cmbTipoBanco').getValue()=='') {
				Ext.Msg.alert('SET', 'Debe seleccionar un Banco');
				Ext.getCmp(PF + 'buscar').setDisabled(false);
			}else{
				if(Ext.getCmp(PF + 'cmbChequera').getValue()==''){
					Ext.Msg.alert('SET', 'Debe seleccionar una chequera');
					Ext.getCmp(PF + 'buscar').setDisabled(false);
				}else{
					if(!Ext.getCmp(PF + 'txtSucursal').getValue()==''){
						if(!Ext.getCmp(PF + 'txtImporte').getValue()==''){
//							if(!Ext.getCmp(PF + 'txtRubro').getValue()==''){
								if(!Ext.getCmp(PF + 'txtReferencia').getValue()==''){
									if(!Ext.getCmp(PF + 'txtFolio').getValue()==''){
										
										vectorEstadoCuenta.numEmpresa=parseInt(Ext.getCmp(PF+'txtIdEmpresa').getValue());
										vectorEstadoCuenta.numBanco=Ext.getCmp(PF + 'txtIdBanco').getValue();
										vectorEstadoCuenta.chequera=Ext.getCmp(PF + 'cmbChequera').getValue();
										vectorEstadoCuenta.concepto=Ext.getCmp(PF + 'cmbConcepto').getRawValue();
										vectorEstadoCuenta.sucursal=Ext.getCmp(PF + 'txtSucursal').getValue();
										vectorEstadoCuenta.folio=Ext.getCmp(PF + 'txtFolio').getValue();
										vectorEstadoCuenta.referencia=Ext.getCmp(PF + 'txtReferencia').getValue();
									  if(NS.salvoBC=='' || NS.salvoBC=='S'){
										  vectorEstadoCuenta.salvo='S'
									  }else{
										  vectorEstadoCuenta.salvo='N'
									  }
									  var im=NS.unformatNumber(Ext.getCmp(PF+'txtImporte').getValue());
									 // console.log("importe "+im);
										if(parseFloat(im)<0){
											Ext.Msg.alert("SET","El importe no debe ser negativo");
										}else{
											vectorEstadoCuenta.importe=im;
											//Ext.Msg.alert("SET",vectorEstadoCuenta.importe);
										}
									  
										vectorEstadoCuenta.observaciones=Ext.getCmp(PF + 'txtObservaciones').getValue();
										vectorEstadoCuenta.cargoAbono=NS.cargoAbono;
										//vectorEstadoCuenta.rubro=Ext.getCmp(PF+'txtRubro').getValue();
										vectorEstadoCuenta.rubro=NS.idRubro;
										console.log(NS.cveConcepto);
										vectorEstadoCuenta.cveConcepto=NS.cveConcepto;
										//Ext.Msg.alert(vectorEstadoCuenta.rubro);
										if (NS.modificar){
											vectorEstadoCuenta.tipoOperacion = "MODIFICAR";
											vectorEstadoCuenta.secuencia=NS.idSecuencia;
										}else{
//											Ext.Msg.alert("",'insertar');
											vectorEstadoCuenta.tipoOperacion= "INSERTAR";
										}
											
										matrizEstadoCuenta[0]=vectorEstadoCuenta;
										var jSonStringCuentas = Ext.util.JSON.encode(matrizEstadoCuenta);
//										Ext.Msg.alert("SET",jSonStringCuentas);
										CapturaEstadoCuentaAction.validaCampos(jSonStringCuentas,function(res,e){
											if(res!='' && res!=undefined && res!=0){
												Ext.Msg.alert("SET",res);
											}else{
												var fecDep=NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFechaDeposito').getValue());
												if(res==''){
													CapturaEstadoCuentaAction.guardarNuevoEstado(jSonStringCuentas,fecDep,function(resultado,e){
														if(resultado!='' && resultado!=null && resultado!=undefined){
															Ext.Msg.alert("SET",resultado);
															NS.limpiarCE();
														}
														});
													
												}
											}
										});
										
										
										
									}else{
										Ext.Msg.alert("SET","Debe asignar el folio");
									}
								}else{
									Ext.Msg.alert("SET","Debe asignar una referencia ");
								}
						}else{
							Ext.Msg.alert("SET","Debe asignar el Importe");
						}
					}else{
						Ext.Msg.alert("SET","Debe asignar una Sucursal");
					}
					
					if(Ext.getCmp(PF + 'cmbTipoBancoB').getValue()>0){
						NS.banco=true;
					}
					if(Ext.getCmp(PF+'cmbChequeraB').getValue()>0){
						NS.chequera=true;
					}
								
					if(NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFechaB').getValue())!=''){
						NS.fecha=true;
					}
					
					NS.storeLlenaGrid.baseParams.numEmpresa = NS.noEmpresa;	
					NS.storeLlenaGrid.baseParams.banco=NS.banco;
					NS.storeLlenaGrid.baseParams.numBanco=parseInt(Ext.getCmp(PF + 'txtIdBancoB').getValue());
					NS.storeLlenaGrid.baseParams.chequera=NS.chequera;
					NS.storeLlenaGrid.baseParams.numChequera =Ext.getCmp(PF + 'txtIdBancoB').getValue();
					NS.storeLlenaGrid.baseParams.fecha=NS.fecha;
					NS.storeLlenaGrid.baseParams.fechaV= NS.cambiarFecha(''+Ext.getCmp(PF + 'txtFechaB').getValue());

					NS.storeLlenaGrid.load();	
				}
				

			}
	};
	
	NS.limpiarCE = function(){
		Ext.getCmp(PF + 'txtImporte').setValue('');
		Ext.getCmp(PF + 'txtSucursal').setValue('');
		Ext.getCmp(PF + 'txtReferencia').setValue('');
		Ext.getCmp(PF + 'txtObservaciones').setValue('')
		Ext.getCmp(PF + 'txtFolio').setValue('');
		Ext.getCmp(PF + 'txtIdBanco').setValue('');
		NS.cmbConcepto.reset();
		NS.txtFechaDeposito.reset();
		NS.cmbTipoBanco.reset();
		NS.cmbChequera.reset();
		
	};


//	Llena datos de captura empresa
	NS.llenaDatosCE = function(){
		Ext.getCmp(PF+'txtIdBanco').setValue(Ext.getCmp(PF+'txtIdBancoB').getValue());
		Ext.getCmp(PF+'cmbTipoBanco').setValue(Ext.getCmp(PF+'cmbTipoBancoB').getValue());
		Ext.getCmp(PF+'cmbChequera').setValue(Ext.getCmp(PF+'cmbChequeraB').getValue());
		Ext.getCmp(PF+'cmbConcepto').setValue(NS.gridConsulta.getSelectionModel().getSelections()[0].get('conceptoSet'));
		Ext.getCmp(PF+'txtFolio').setValue(NS.gridConsulta.getSelectionModel().getSelections()[0].get('folioBanco'));
		Ext.getCmp(PF+'txtImporte').setValue(BFwrk.Util.formatNumber(NS.gridConsulta.getSelectionModel().getSelections()[0].get('importe')));
		Ext.getCmp(PF+'txtReferencia').setValue(NS.gridConsulta.getSelectionModel().getSelections()[0].get('referencia'));
		Ext.getCmp(PF+'txtObservaciones').setValue(NS.gridConsulta.getSelectionModel().getSelections()[0].get('observacion'));
		Ext.getCmp(PF+'txtSucursal').setValue(NS.gridConsulta.getSelectionModel().getSelections()[0].get('sucursal'));
		NS.idRubro=NS.gridConsulta.getSelectionModel().getSelections()[0].get('idRubro');
		NS.idSecuencia=NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
		if(NS.gridConsulta.getSelectionModel().getSelections()[0].get('salvoBuenCobro')=='' || 
				NS.gridConsulta.getSelectionModel().getSelections()[0].get('salvoBuenCobro')=='S'){
			Ext.getCmp(PF+'chkSalvo').setValue(0);
			NS.salvoBC='S';
		}else{
			Ext.getCmp(PF+'chkSalvo').setValue(1);
			NS.salvoBC='N';
		}
		if(NS.gridConsulta.getSelectionModel().getSelections()[0].get('cargoAbono')=='E'){
			Ext.getCmp(PF+'optBono').setValue(0)[0];
			NS.cargoAbono='E';
		}else{
			Ext.getCmp(PF+'optBono').setValue(1)[1];
			NS.cargoAbono='I';
		}
		
	};

	//GRID CONSULTA
	//Columna de seleccion en el grid
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columnas de grid
	
	NS.columnasGrid = new Ext.grid.ColumnModel([	                                            
	    {header: 'I/E', width: 50,  dataIndex: 'cargoAbono', sortable: true},
	    {header: 'Folio', width: 80, dataIndex: 'folioBanco', sortable: true},
	    {header: 'Importe', width: 100, dataIndex: 'importe', sortable: true},
	    {header: 'Referencia', width: 100, dataIndex: 'referencia', sortable: true},
	    {header: 'Concepto', width: 200, dataIndex: 'conceptoSet', sortable: true},
	    {header: 'Sucursal', width: 80, dataIndex: 'sucursal', sortable: true},
	    {header: 'Fecha', width: 120, dataIndex: 'fecValor', sortable: true},
	    {header: 'Observaciones', width: 120, dataIndex: 'observacion', sortable: true},
	    {header: 'SalvoBC', width: 120, dataIndex: 'salvoBuenCobro', sortable: true,hidden:true},
	    {header: 'Rubro', width: 120, dataIndex: 'idRubro', sortable: true,hidden:true},
	    {header: 'Estatus', width: 120, dataIndex: 'idEstatusTrasp', sortable: true,hidden:true},
	    {header: 'Secuencia', width: 120, dataIndex: 'secuencia', sortable: true,hidden:true},
	]);
	
	NS.gridConsulta = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'gridConsulta',
		name: PF + 'gridConsulta',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 960,
		height: 200,

		stripeRows : true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid)
				{
					
					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
				}
			}
		}
	});
	
	
	

//	//PANEL
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: 'Busqueda',
		x: 0,
		y: 50,
		width: 985,
		height: 160,
		layout: 'absolute',
		items: 
		[
		 	
		NS.lblBancoB,
		NS.lblChequeraB,
		NS.cmbTipoBancoB,
		NS.cmbChequeraB,
		NS.lblFechaB,	 	
	 	NS.txtFechaB,	
	 	NS.txtIdBancoB,
	 	//NS.storeLlenaComboTipoBanco.load(),
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		id: PF + 'buscar',
		 		name: PF + 'buscar',
		 		x: 700,
		 		y: 30,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {	
		 					if(Ext.getCmp(PF+'txtIdEmpresa').getValue()==''){
		 						Ext.Msg.alert("SET","Debe seleccionar una Empresa");
		 						
		 					}else{
		 						if(Ext.getCmp(PF+'txtIdBancoB').getValue()==''){
		 							Ext.Msg.alert("SET","Debe seleccionar un Banco");
		 						}else{
		 							if(Ext.getCmp(PF+'cmbChequeraB').getValue()==''){
		 								Ext.Msg.alert("SET","Debe seleccionar una Chequera");
		 							}else{
		 								NS.buscar();
		 							}
		 						}
		 					}
		 						
		 				}
		 			}
		 		}
		 	},	
		]
	});
	
	
	
	
	NS.panelBotonesMCE = new Ext.form.FieldSet({
		id: PF + 'panelBotonesMCE',
		name: PF + 'panelBotonesMCE',
		x: 0,
		y: 370,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Modificar',
		 		x: 680,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.modificar=true;
		 					//NS.modificarCuentas == false;
//		 					if (NS.validaVaciosIB()) {
		 						NS.guardar();
//							}			
		 				}
		 			}
		 		}
		 	},

		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 			
		 		{
		 			click:
		 			{
		 				
		 				fn: function(e)
		 				{
		 					//NS.limpiarCE();
		 					NS.panelCamposCE.setVisible(false);
		 					NS.panelBotonesCE.setVisible(false);
		 					NS.panelBotonesMCE.setVisible(false);
		 					NS.panelEmpresa.setVisible(true);
		 					NS.panelBusqueda.setVisible(true);
		 					NS.buscar();
		 					NS.panelGrid.setVisible(true);
		 				}
		 			}
		 		}
		 	}
		]
	});
	

	NS.panelBotonesCE = new Ext.form.FieldSet({
		id: PF + 'panelBotonesCE',
		name: PF + 'panelBotonesCE',
		x: 0,
		y: 374,
		width: 985,
		height: 55,
		layout: 'absolute',
		items:
		[
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 680,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 						NS.guardar();
			
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Regresar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				
		 				fn: function(e)
		 				{
		 					NS.panelCamposCE.setVisible(false);
		 					NS.panelBotonesCE.setVisible(false);
		 					NS.panelEmpresa.setVisible(true);
		 					NS.panelBotonesMCE.setVisible(false);
		 					NS.panelBusqueda.setVisible(true);
		 					NS.buscar();
		 					NS.panelGrid.setVisible(true);
		 					
			 					
		 				}
		 			}
		 		}
		 	}
		]
	});
	
//	//////////////////////////panel campos ESTADO DE CUENTA//////////////////////////////
	NS.panelCamposCE = new Ext.form.FieldSet({
		id: PF + 'panelCamposCE',
		name: PF + 'panelCamposCE',
		title: 'NUEVO REGISTRO DE ESTADO DE CUENTA',
		x: 0,
		y: 0, 
		width: 985,
		height: 370,
		layout: 'absolute',
		items:
		[
		 NS.txtIdBanco,
		 NS.lblBanco,
		 NS.lblChequera,
		 NS.lblConcepto,
		 NS.lblFolio,
		 NS.lblReferencia,
		 NS.txtFechaDeposito,
		 NS.lblFechaDeposito,
		 NS.lblObservaciones,
//		 NS.lblRubro,
		 {
				xtype: 'uppertextfield',
				id: PF + 'txtSucursal',
				name: PF + 'txtSucursal',
				x: 300,
				y: 90,
				width: 200,
				listeners: {
					change: {
						fn: function(caja, valor){
//							if (caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
								//BFwrk.Util.updateTextFieldToCombo(PF + 'txtTipoPersona', NS.cmbTipoPersona.getId());
						}
					}
				}	 	
			 },
			 {
			 		xtype: 'checkbox',
			 		id: PF + 'chkSalvo',
			 		name: PF + 'chkSalvo',
			 		x: 550,
			 		y: 85,
			 		boxLabel: 'Salvo Buen Cobro',
			 		listeners: {
			 			check: {
			 				fn: function(checkBox, valor) {
			 						
			 				}
			 			}
			 		}
			 	},
			 	NS.optBono,
			 {
					xtype: 'uppertextfield',
					id: PF + 'txtFolio',
					name: PF + 'txtFolio',
					x: 0,
					y: 155,
					width: 200,
					listeners: {
						change: {
							fn: function(caja, valor){
								//if (caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
									//BFwrk.Util.updateTextFieldToCombo(PF + 'txtTipoPersona', NS.cmbTipoPersona.getId());
							}
						}
					}	 	
				 },
				 {
						xtype: 'uppertextfield',
						id: PF + 'txtImporte',
						name: PF + 'txtImporte',
						x: 300,
						y: 155,
						width: 200,
						listeners: {
							change: {
								fn: function(caja, valor){
									//if (caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
										//BFwrk.Util.updateTextFieldToCombo(PF + 'txtTipoPersona', NS.cmbTipoPersona.getId());
								}
							}
						}	 	
					 },
				 
		 NS.lblSucursal,
		 {
				xtype: 'uppertextfield',
				id: PF + 'txtReferencia',
				name: PF + 'txtReferencia',
				x: 0,
				y: 220,
				width: 200,
				listeners: {
					change: {
						fn: function(caja, valor){
							//if (caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
								//BFwrk.Util.updateTextFieldToCombo(PF + 'txtTipoPersona', NS.cmbTipoPersona.getId());
						}
					}
				}	 	
			 },
			 {
					xtype: 'uppertextfield',
					id: PF + 'txtObservaciones',
					name: PF + 'txtObservaciones',
					x: 300,
					y: 220,
					width: 200,
					listeners: {
						change: {
							fn: function(caja, valor){
								//if (caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
									//BFwrk.Util.updateTextFieldToCombo(PF + 'txtTipoPersona', NS.cmbTipoPersona.getId());
							}
						}
					}	 	
				 },

		 NS.lblImporte,
		 NS.cmbTipoBanco,
		 NS.cmbChequera,
		 NS.cmbConcepto,
		 NS.storeLlenaComboTipoBanco.load(),
		]	
	});	
	
	
	NS.panelGrid = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 180,
		width: 985,
		height: 250,
		layout: 'absolute',
		items:
		[
		 	NS.gridConsulta, 	
		 	{
		 		xtype: 'button',
		 		text: 'Consultar',
		 		x: 650,
		 		y: 205,
		 		id: PF + 'consultar',
		 		name: PF + 'consultar',
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 						var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
			 					if (registroSeleccionado.length <= 0)
			 						Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			 					else
			 					{
			 						NS.panelBusqueda.setVisible(false);
			 						NS.panelGrid.setVisible(false);
			 						NS.panelEmpresa.setVisible(false);
			 						NS.panelBotonesCE.setVisible(false);
			 						NS.panelCamposCE.setVisible(true);
			 						NS.panelBotonesMCE.setVisible(true);
			 						NS.llenaDatosCE();

		 					}

		 					}	
		 				}
		 			}
		 		
		 	},
//		 	//boton para crear nueva captura de estado de cuenta
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 750,
		 		y: 205,
		 		id: PF + 'crearN',
		 		name: PF + 'crearN',
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					if(Ext.getCmp(PF+'txtIdEmpresa').getValue()==''){
		 						Ext.Msg.alert('SET','Debe seleccionar una Empresa');
		 					}else{
		 						NS.modificar = false;
								NS.panelEmpresa.setVisible(false);
	 							NS.panelBusqueda.setVisible(false);
	 							NS.panelGrid.setVisible(false);
	 							NS.panelBotonesMCE.setVisible(false);
	 							NS.limpiarCE();
	 							NS.panelCamposCE.setVisible(true);
	 							NS.panelBotonesCE.setVisible(true);	
		 					}
		 				}
		 			}
		 		}
		 	},
							
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 850,
		 		y: 205,
		 		id: PF + 'eliminar',
		 		name: PF + 'eliminar',
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 					NS.modificar = false;
		 					var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
//		 					
		 					if (registroSeleccionado.length <= 0)
		 						Ext.Msg.alert('SET', 'Se debe de elegir un registro, para eliminar');
		 					else
		 					{
		 						Ext.Msg.confirm("SET","¿Desea borrar los datos seleccionados?",function(res){
		 							if(res=='yes'){
		 								if(NS.gridConsulta.getSelectionModel().getSelections()[0].get('idEstatusTrasp')=='N'){
		 									
		 									var secuencia=NS.gridConsulta.getSelectionModel().getSelections()[0].get('secuencia');
		 									CapturaEstadoCuentaAction.eliminarCapturaEstado(parseInt(Ext.getCmp(PF+'txtIdEmpresa').getValue()),secuencia,function(resultado,e){
		 										if(resultado!=''|| resultado!=undefined){
		 											Ext.Msg.alert("SET",resultado);
		 											NS.panelBusqueda.setVisible(true);
		 						 					NS.buscar();
		 						 					NS.panelGrid.setVisible(true);
		 										}else{
		 											Ext.Msg.alert("SET",resultado);
		 										}
		 									});
		 								}else{
		 									
		 									Ext.Msg.alert("SET","El Movimiento Traspasado no se puede eliminar");
		 								}
		 							}else{
		 								if(res=='no'){
		 									Ext.Msg.alert("SET","El registro no pudo ser eliminado");
		 								}
		 							}
		 						},this);
		 								 					
	 					}
		 				}
		 			}
		 		}
		 	}
		]
	});


	NS.panelEmpresa = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 0,
		width: 985,
		height: 50,
		layout: 'absolute',
		items:
		[
		 	NS.lblEmpresa,
		 	NS.txtIdEmpresa,
			NS.cmbEmpresas,
			NS.storeLlenaComboEmpresas.load(),
	]});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1010,
		height: 520,
		layout: 'absolute',
		items:
		[
		 	NS.panelEmpresa,
		 	NS.panelBusqueda,
		 	NS.panelGrid,
		 	
				NS.panelCamposCE.setVisible(false),
				NS.panelBotonesCE.setVisible(false),
				NS.panelBotonesMCE.setVisible(false),

	]});
	
	
	NS.capturaEstadoCuenta = new Ext.FormPanel ({
		title: 'Captura de Estado de Cuenta',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'capturaEstadoCuenta',
		name: PF + 'capturaEstadoCuenta',
		renderTo: NS.tabContId,
		items: [
		 	NS.global,
		]
	});


});
