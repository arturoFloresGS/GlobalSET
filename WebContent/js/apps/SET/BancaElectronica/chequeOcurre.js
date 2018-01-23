Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Egresos.chequeOcurre');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	NS.noEmpresa = 0;
	NS.idBanco = 0;
	NS.idChequera = '';
	NS.record = 0;
	NS.chkAutomatico = false;
	NS.iTieneBanca = 0;
	NS.bAutomatico = false;
	NS.idDivisa = "MN";
	NS.importeTotal = 0;
	NS.optBanamexMasterCheck = false; 
	NS.optSantanderDirecto = false;
	
	NS.chkH2H = false;
	NS.chkDebito = 'false';
	NS.bancoBenef = 0;
	NS.chequeraBenef = '';
	NS.propuesta = '';
	NS.inicializar = function(){
		NS.chkTodasEmpresas = 'true';
		NS.chkSoloLocales= 'false';
		NS.chkConvenioCie= 'false';
		NS.chkConvenioSant = 'false';
		NS.chkH2HSantander = 'true';
		NS.chkMassPayment= 'false';
		NS.optTipoEnvio= 'InterBancario';//Este parametro es un String puede ser= interbancario, mismoBanco, etc.
        NS.optComerica= '';// poner ACH por defualt al visualizar el frame
        NS.optVenc= 'false';
        NS.idBancoEmi=0;
        NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
		NS.GS_ID_EMPRESA = apps.SET.NO_EMPRESA;
		NS.GI_USUARIO = apps.SET.iUserId ;
        NS.idUsuario= NS.GI_USUARIO;
        NS.idEmpresa= NS.GS_ID_EMPRESA;
	}
	NS.inicializar();
	
	//Etiqueta empresa
	NS.labEmpresa = new Ext.form.Label({
		text: 'Empresa:',
        x: 10,
        y: 0
	});
	
//////bandera
    NS.bandera = new Ext.form.TextField({
		id: PF + 'bandera',
        name: PF + 'bandera',
        x: 120,
        y: 60,
        value:2,
        width: 430, 
    	emptyText: '',

	  }); 
	
    NS.cambiarFechaGrid=function(fecha){
		return fecha.substring(0,10);
	}
    
	NS.imprimeReporte = function(archivo){
		var nomEmpresa;
		var registrosSelec = NS.gridDatos.getSelectionModel().getSelections();
		console.log("archivoo "+archivo);
		//if(parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue()) == 0) {    PF+'txtIdGrupo'
		//if(parseInt(Ext.getCmp(PF + 'txtIdGrupo').getValue()) == 0) {
			if(registrosSelec.length == 1)
				nomEmpresa = registrosSelec[0].get('nomEmpresa');
			else
				nomEmpresa = 'VARIAS EMPRESAS';
			console.log(nomEmpresa);
		//else
			//nomEmpresa = NS.storeEmpresas.getById(parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue())).get('nomEmpresa');
			//nomEmpresa = NS.storeGrupo.getById(parseInt(Ext.getCmp(PF + 'txtIdGrupo').getValue())).get('descripcion');

		strParams = '?nomReporte=detArchivoTransf';
		strParams += '&'+'nomParam1=archivo';
		strParams += '&'+'valParam1=' + archivo;
		strParams += '&'+'nomParam2=nomEmpresa';
		strParams += '&'+'valParam2=' + nomEmpresa;
		strParams += '&'+'nomParam3=titulo';
		
		if(NS.optTipoEnvio == 'InterBancario')
			strParams += '&'+'valParam3=' + "DETALLE DEL ARCHIVO DE TRANSFERENCIAS INTERBANCARIAS";
		else
			strParams += '&'+'valParam3=' + "DETALLE DEL ARCHIVO DE TRANSFERENCIAS MISMO BANCO";
		
		strParams += '&'+'nomParam4=fecHoy';
		strParams += '&'+'valParam4=' + apps.SET.FEC_HOY;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		return;
	};
	
	
    //Caja numero de empresa
	NS.txtEmpresa = new Ext.form.TextField({
		id: PF + 'txtEmpresa',
        name:PF + 'txtEmpresa',
		x: 10,
        y: 15,
        width: 50,
        tabIndex: 0,
        listeners:{
			change:{
				fn: function(caja,valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
						
						if(valueCombo != null && valueCombo != undefined && valueCombo != '')
							NS.cambiaBanco(valueCombo);
						else
							NS.cambiaBanco(0);
					}else {
						NS.cambiaBanco(0);
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
					}
                }
			}
    	}
    });
	
	//Store empresas
    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
    		idUsuario: NS.idUsuario
    	},
		paramOrder:['idUsuario'],
		directFn: ChequeOcurreAction.obtenerEmpresas, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene empresas asignadas');
					return;
				}
				//Se agrega la opcion todas las empresas
	 			var recordsStoreEmpresas = NS.storeEmpresas.recordType;	
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '***************TODAS***************'
		       	});
		   		NS.storeEmpresas.insert(0,todas);
			}
		}
	}); 
	NS.storeEmpresas.load();
	
	//Combo empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 65,
        y: 15,
        width: 300,
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
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
					NS.cambiaBanco(combo.getValue());
				}
			}
		}
	});
	
	//Refresca el combo bancos en base a la empresa
	NS.cambiaBanco = function(valueCombo){
		Ext.getCmp(PF + 'txtBanco').reset();
		NS.cmbBanco.reset();
		//NS.cmbChequeras.reset();
		NS.storeBancos.removeAll();
		//NS.storeChequeras.removeAll();
		
		if((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0) {
			if(valueCombo == 0)
				Ext.getCmp(PF + 'txtEmpresa').setValue(valueCombo);
			
			NS.storeBancos.baseParams.noEmpresa = valueCombo;
			NS.storeBancos.load();
			NS.noEmpresa = valueCombo;
		}
	};
	
	//Etiqueta Banco
	NS.labBanco = new Ext.form.Label({
		text: 'Banco:',
		x: 390,
		y: 0
	});
	
	//Caja numero banco 
	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
		name: PF + 'txtBanco',
		x: 390,
		y: 15,
		width: 50,
		tabIndex: 2,
		listeners:{
			change:{
				fn: function(caja,valor){
                    if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') //{
                    	var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
                    
                    if (Ext.getCmp(PF + 'txtBanco').getValue() == "14") {
						NS.optSantander.setVisible(true);
					} else {
						NS.optSantander.setVisible(false);
					}
                }
			}
    	}
	});
	NS.txtEmpresa.setValue(0);
	NS.cmbEmpresas.setValue('***************TODAS***************');
	
	//Store banco
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			noEmpresa: 0
		},
		paramOrder:['noEmpresa'],
		directFn: ChequeOcurreAction.obtenerBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No Existen bancos para esta empresa');
			}
		}
	});
	NS.storeBancos.load();
	
	//Combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancos,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 445,
	    y: 15,
	    width: 185,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 3,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBanco.getId());
					if (Ext.getCmp(PF + 'txtBanco').getValue() == "14") {
						NS.optSantander.setVisible(true);
					} else {
						NS.optSantander.setVisible(false);
					}
					/*if(combo.getValue() != null && combo.getValue() != '' && combo.getValue() != undefined) 
						NS.cambiaChequera(combo.getValue());*/
				}
			}
		}
	});

	//Santander
	NS.optSantanderOpt = new Ext.form.RadioGroup({
		id: PF + 'optSantanderDirecto',
		name: PF + 'optSantanderDirecto',
		columns: 2, 
		x: 0,
		y: 0,
		items: [
	          {boxLabel: 'Normal', name: 'optSantanderDirecto', inputValue: 0, checked: true},  
	          {boxLabel: 'Directo', name: 'optSantanderDirecto', inputValue: 1}
	     ],
	     listeners:{
	    	 change:{
	    		fn:function(caja,valor){
	    			if (this.getValue().inputValue == 0) NS.optSantanderDirecto = false;
	    			else if (this.getValue().inputValue == 1) NS.optSantanderDirecto = true;
				}
	    	 }
	     }
	});
	NS.optSantander = new Ext.form.FieldSet({
		title: "",
		width: 200,
		height: 40,
		y: 0,
		x: 645,
		layout: "absolute",
		hidden: true,
		items: [
		        NS.optSantanderOpt
		]
	});
	
	
	//Contenedor de Busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: 'búsqueda',
		width: 985,
	    height: 80,
	    layout: 'absolute',
	    items:[
	        NS.labEmpresa,
	        NS.txtEmpresa,
	        NS.cmbEmpresas,
	        NS.labBanco,
            NS.txtBanco,
            NS.cmbBanco,
            NS.optSantander,
	        {
	        	xtype: 'button',
	        	text: 'Buscar',
	        	x: 870,
	        	y: 15,
	        	width: 80,
	        	height: 22,
	        	listeners: {
	        		click:{
	        			fn:function(e){
	        				NS.buscar();
	        			}
	        		}
	        	}
	        }
	    ]
	});
	
	//Store Datos donde se muestran los valores seleccionados
//	NS.storeDatos = new Ext.data.DirectStore({
//		paramsAsHash: false,
//		baseParams: {noEmpresa: 0, idBanco: 0},
//		root: '',
//		paramOrder: ['noEmpresa', 'idBanco'],
//		directFn: ChequeOcurreAction.consultarCheques,
//		fields: [
//			 {name: 'noDocto'}, 
//		    {name: 'fecOperacion'}, 
//		    {name: 'beneficiario'}, 
//		    {name: 'importe'}, 
//		    {name: 'idDivisa'}, 
//		    {name: 'descBanco'}, 
//		    {name: 'idChequera'}, 
//		    {name: 'observacion'},
//		    {name: 'noFolioDet'}, 
//		    {name: 'noFolioMov'}, 
//		    {name: 'idBanco'}, 
//		    {name: 'referencia'}, 
//		    {name: 'sucursal'}, 
//		    {name: 'fecValor'}, 
//		    {name: 'origenMov'}, 
//		    {name: 'idBancoBenef'},
//		    {name: 'idChequeraBenef'}, 
//		    {name: 'descBancoBenef'}, 
//		    {name: 'nomEmpresa'}, 
//		    {name: 'noEmpresaBenef'},
//		    {name: 'loteEntrada'},
//		    {name: 'noCliente'},  
//		    {name: 'noEmpresa'}, 
//		    {name: 'concepto'}, 
//		    {name: 'noFactura'},
//		    {name: 'noEmpresa'},  
//		    {name: 'plaza'}, 
//		    {name: 'rfcBenef'}
//		],
//		listeners: {
//			load: function(s, records) {
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
//				NS.record = parseInt(records.length);
//				
//				if(records.length == null || records.length <=0)
//					Ext.Msg.alert('SET', 'No existen registros con estos parametros');
//			}
//		}
//	});
	
	//Store para llenar el grid de envio 
  	NS.storeDatos = new Ext.data.DirectStore({
  		paramsAsHash: false,
  	 	baseParams: {
			chkTodasEmpresas: false,
			chkSoloLocales: false,
			chkConvenioCie: false,
			chkMassPayment: false,
	        optTipoEnvio: 'InterBancario',//Este parametro es un String puede ser: interbancario, mismoBanco, etc.
	        optComerica: '',// poner ACH por defualt al visualizar el frame
	        optVenc: false,
//	        pdMontoIni:'',
//	        pdMontoFin:'',
//	        psFechaValor:'',
//	        psFecValOrig:'',
//	        idBancoRec:'',
	        idBancoEmi:'',
//	        idDivisa:'',
//	        idDivision:'',
	        idUsuario: '' + NS.GI_USUARIO,
	        idEmpresa:'' + NS.GS_ID_EMPRESA,
	      //  cveControl:'',
	        nomina: false,
	        chkConvenioSant: false,
			chkDebito: false
		},
		root: '',
		paramOrder:[
					'chkTodasEmpresas',
					'chkSoloLocales',
					'chkConvenioCie',
					'chkMassPayment',
					'optTipoEnvio',
					'optComerica',
					'optVenc',
//					'pdMontoIni',
//					'pdMontoFin',
//					'psFechaValor',
//					'psFecValOrig',
//					'idBancoRec',
					'idBancoEmi',
//					'idDivisa',
//					'idDivision',
					'idUsuario',
					'idEmpresa',
//					'cveControl',
					'nomina',
					'chkConvenioSant',
					'chkDebito'
			],
		directFn:ChequeOcurreAction.consultaCheques,
		fields: 
		[
			{name : 'noDocto'},
		//	{name : 'idBancoCityStr'},
			{name : 'beneficiario'},
			{name : 'importe'},
			{name : 'idDivisa'},
	//		{name : 'nombreBancoBenef'},
	//		{name : 'idChequeraBenef'},
//			{name : 'idChequeraBenefReal'},
			{name : 'concepto'},
			{name : 'noFolioDet'},
			{name : 'noEmpresa'},
			{name : 'origenMov'},
			{name : 'idChequera'},
	//		{name : 'descBancoBenef'},
	//		{name : 'idBancoBenef'},
	//		{name : 'plazaBenef'},
	//		{name : 'plaza'},
//			{name : 'bLayoutComerica'},
			{name : 'noCliente'},
			{name : 'idBanco'},
//			{name : 'instFinan'},
//			{name : 'sucOrigen'},
//			{name : 'sucDestino'},
			{name : 'division'},
		//	{name : 'especiales'},
	//		{name : 'complemento'},
		//	{name : 'clabeBenef'},
			{name : 'rfcBenef'},
			{name : 'rfc'},
			{name : 'clave'},
			{name : 'horaRecibo'},
			{name : 'idContratoMass'},
			{name : 'tipoEnvioLayout'},
	//		{name : 'nomBancoIntermediario'},
		//	{name : 'aba'},
//		    {name : 'swiftCode'},
//			{name : 'abaIntermediario'},
//			{name : 'swiftIntermediario'},
//			{name : 'abaCorresponsal'},
//			{name : 'swiftCorresponsal'},
//			{name : 'nomBancoCorresponsal'},
//			{name : 'descUsuarioBital'},
//			{name : 'descServicioBital'},
			{name : 'nomEmpresa'},
			{name : 'noFolioMov'},
			{name: 'fecValorStr'},
			{name: 'fecValorOriginalStr'},
			{name:'fecOperacionStr'},
			{name:'noFactura'},
		],
		listeners: {
			load: function(s, records){
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEnvioTransGrid, msg:"Buscando..."});
				NS.record = parseInt(records.length);
				
				if(records.length == null || records.length <=0)
					Ext.Msg.alert('SET', 'No existen registros con estos parametros');
//				if(records.length==null || records.length<=0)
//				{
//					Ext.Msg.alert('Información  SET','No se encontraron datos con los parametros de búsqueda');
//				}
//				else
//				{			   		
//          			var totalImporte=0;
//          			NS.sm.selectRange(0,records.length-1);
//          			var regSelec=NS.gridEnvioTransGrid.getSelectionModel().getSelections();
//          			
//          			for(var k=0;k<regSelec.length;k=k+1)
//          			{
//          				totalImporte=totalImporte+regSelec[k].get('importe');
//          			}
//					Ext.getCmp(PF+'txtTotalReg').setValue(records.length);
//					Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));          			
//				}
			}
		}
	}); 
	
	//Para indicar el modo de seleccion de los registros en el Grid 
	NS.tipoSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	NS.columnaSelec = new Ext.grid.ColumnModel([
	    NS.tipoSeleccion,
	    {header: 'No Docto.', width: 80, dataIndex: 'noDocto', sortable: true}, 
	    {header: 'Fecha Operación', width: 150, dataIndex: 'fecOperacionStr', sortable: true}, 
	    {header: 'Beneficiario', width: 200, dataIndex: 'beneficiario', sortable: true}, 
	    {header: 'Importe', width: 120, dataIndex: 'importe', sortable: true, renderer: BFwrk.Util.rendererMoney}, 
	    {header: 'Divisa', width: 80, dataIndex: 'idDivisa', sortable: true}, 
	    //{header: 'Nombre Banco', width: 120, dataIndex: 'descBanco', sortable: true}, 
	    {header: 'Chequera', width: 150, dataIndex: 'idChequera', sortable: true}, 
	    {header: 'Concepto', width: 150, dataIndex: 'concepto', sortable: true},
	    {header: 'No. Folio Detalle', width: 90, dataIndex: 'noFolioDet', sortable: true}, 
	    {header: 'Folio Movimiento', width: 90, dataIndex: 'noFolioMov', sortable: true}, 
	    {header: 'Banco', width: 80, dataIndex: 'idBanco', sortable: true}, 
	  //  {header: 'Referencia', width: 100, dataIndex: 'referencia', sortable: true}, 
	  //  {header: 'Sucursal', width: 80, dataIndex: 'sucOrigen', sortable: true}, 
	    {header: 'Fecha Valor', width: 150, dataIndex: 'fecValorStr', sortable: true}, 
	    {header: 'Origen Movimiento', width: 150, dataIndex: 'origenMov', sortable: true}, 
//	    {header: 'Banco Beneficiario', width: 80, dataIndex: 'idBancoBenef', sortable: true},
//	    {header: 'Chequera Beneficiaria', width: 150, dataIndex: 'idChequeraBenef', sortable: true}, 
//	    {header: 'Nombre Banco Beneficiario', width: 150, dataIndex: 'descBancoBenef', sortable: true}, 
	    {header: 'Nombre Empresa', width: 150, dataIndex: 'nomEmpresa', sortable: true}, 
//	    {header: 'Nombre Empresa Beneficiaria', width: 150, dataIndex: 'noEmpresaBenef', sortable: true},
//	    {header: 'Lote de Entrada', width: 80, dataIndex: 'loteEntrada', sortable: true},
	    {header: 'No. Cliente', width: 80, dataIndex: 'noCliente', sortable: true},  
	    {header: 'No. Empresa', width: 80, dataIndex: 'noEmpresa', sortable: true}, 
	    {header: 'Concepto', width: 150, dataIndex: 'concepto', sortable: false}, 
	    {header: 'No. Factura', width: 80, dataIndex: 'noFactura', sortable: false},
	    {header: 'No. Empresa', width: 80, dataIndex: 'noEmpresa', sortable: false},  
	  //  {header: 'Plaza', width: 80, dataIndex: 'plaza', sortable: false}, 
	    {header: 'RFC', width: 150, dataIndex: 'rfc', sortable: false}
	]);
	                                    	
	//Columna de Seleccion  
//	NS.columnaSelec = new Ext.grid.ColumnModel([
//	    NS.tipoSeleccion,
//	    {header: 'No Docto.', width: 80, dataIndex: 'noDocto', sortable: true}, 
//	    {header: 'Fecha Operación', width: 150, dataIndex: 'fecOperacion', sortable: true}, 
//	    {header: 'Beneficiario', width: 200, dataIndex: 'beneficiario', sortable: true}, 
//	    {header: 'Importe', width: 120, dataIndex: 'importe', sortable: true, renderer: BFwrk.Util.rendererMoney}, 
//	    {header: 'Divisa', width: 80, dataIndex: 'idDivisa', sortable: true}, 
//	    {header: 'Nombre Banco', width: 120, dataIndex: 'descBanco', sortable: true}, 
//	    {header: 'Chequera', width: 150, dataIndex: 'idChequera', sortable: true}, 
//	    {header: 'Concepto', width: 150, dataIndex: 'observacion', sortable: true},
//	    {header: 'No. Folio Detalle', width: 90, dataIndex: 'noFolioDet', sortable: true}, 
//	    {header: 'Folio Movimiento', width: 90, dataIndex: 'noFolioMov', sortable: true}, 
//	    {header: 'Banco', width: 80, dataIndex: 'idBanco', sortable: true}, 
//	    {header: 'Referencia', width: 100, dataIndex: 'referencia', sortable: true}, 
//	    {header: 'Sucursal', width: 80, dataIndex: 'sucursal', sortable: true}, 
//	    {header: 'Fecha Valor', width: 150, dataIndex: 'fecValor', sortable: true}, 
//	    {header: 'Origen Movimiento', width: 150, dataIndex: 'origenMov', sortable: true}, 
//	    {header: 'Banco Beneficiario', width: 80, dataIndex: 'idBancoBenef', sortable: true},
//	    {header: 'Chequera Beneficiaria', width: 150, dataIndex: 'idChequeraBenef', sortable: true}, 
//	    {header: 'Nombre Banco Beneficiario', width: 150, dataIndex: 'descBancoBenef', sortable: true}, 
//	    {header: 'Nombre Empresa', width: 150, dataIndex: 'nomEmpresa', sortable: true}, 
//	    {header: 'Nombre Empresa Beneficiaria', width: 150, dataIndex: 'noEmpresaBenef', sortable: true},
//	    {header: 'Lote de Entrada', width: 80, dataIndex: 'loteEntrada', sortable: true},
//	    {header: 'No. Cliente', width: 80, dataIndex: 'noCliente', sortable: true},  
//	    {header: 'No. Empresa', width: 80, dataIndex: 'noEmpresa', sortable: true}, 
//	    {header: 'Concepto', width: 150, dataIndex: 'concepto', sortable: false}, 
//	    {header: 'No. Factura', width: 80, dataIndex: 'noFactura', sortable: false},
//	    {header: 'No. Empresa', width: 80, dataIndex: 'noEmpresa', sortable: false},  
//	    {header: 'Plaza', width: 80, dataIndex: 'plaza', sortable: false}, 
//	    {header: 'RFC', width: 150, dataIndex: 'rfcBenef', sortable: false}
//	]);
//	
	//Grid para mostrar los datos seleccionados
	NS.gridDatos = new Ext.grid.GridPanel({
		store: NS.storeDatos,
		id: 'gridDatos',
		cm: NS.columnaSelec,
		sm: NS.tipoSeleccion,
		width: 960,
	    height: 245,
	    stripeRows: true,
	    columnLines: true,
		listeners: {
			click: {
				fn:function(grid) {
					var records = NS.gridDatos.getSelectionModel().getSelections();
					NS.importeTotal = 0;
					for(i = 0; i < records.length; i++){
						NS.importeTotal = parseInt(NS.importeTotal) + parseInt(records[i].get('importe'));
					}
					Ext.getCmp(PF + "txtImporteTotal").setValue(NS.importeTotal + "");
					NS.txtSeleccionados.setValue(i + '');
				}
			}
		}
	});

	//checkBox para seleccionar todos
	NS.chkTodos = new Ext.form.CheckboxGroup({
	    id: PF + 'chkTodos',
	    name:PF + 'chkTodos',
	    x: 10,
	    y: 270,
	    fieldLabel: '',
	    itemCls: 'x-check-group-alt',
	    columns: 2,
	    items: [ {
	    		xtype:'checkbox',
                boxLabel: 'Marcar Todos',
                id: PF + 'chkAuto',
                name: PF + 'chkAuto',
                listeners: {
                    check: {
                    	fn:function(checkBox,valor) {
                    		if(valor) {
                    			NS.marcarTodo();
                    			NS.chkAutomatico = true;
                    		}else{
                 				NS.tipoSeleccion.selectRange(-1, -1);
                    			NS.chkAutomatico = false;
                    		}
                    		var records = NS.gridDatos.getSelectionModel().getSelections();
        					NS.importeTotal = 0;
        					for(i = 0; i < records.length; i++){
        						NS.importeTotal = parseInt(NS.importeTotal) + parseInt(records[i].get('importe'));
        					}
        					Ext.getCmp(PF + "txtImporteTotal").setValue(NS.importeTotal + "");
        					NS.txtSeleccionados.setValue(i + '');
                        }
	    			}
	    		}
            },{
	    		xtype:'checkbox',
                boxLabel: 'Marcar Todos',
                id: PF + 'a',
                name: PF + 'a',
                disabled:true,
                hidden:true,
                checked:true
            }
	    ]
	});

	//Etiqueta seleccionados
	NS.labRegistros = new Ext.form.Label({
		text: 'Total de registros:',
        x: 300,
        y: 270
	});
	
	// seleccionados
	NS.txtSeleccionados = new Ext.form.TextField({
		id: (PF + "txtSeleccionados"),
		name: (PF + "txtSeleccionados"),
		disabled: true,
		value: '0',
		width: 60,
        x: 390,
        y: 265
	});
	
	//Etiqueta total importe
	NS.labImporteTotal = new Ext.form.Label({
		text: 'Importe total:  $',
        x: 460,
        y: 270  
	});

	
	// total importe
	NS.txtImporteTotal = new Ext.form.TextField({
		id: (PF + "txtImporteTotal"),
		name: (PF + "txtImporteTotal"),
		renderer: BFwrk.Util.rendererMoney,
		disabled: true,
		value: '0.00',
		width: 80,
        x: 540,
        y: 265
	});
	
	//Contenedor de Transferencia o Factoraje
	NS.contGridBusqueda = new Ext.form.FieldSet({
		title: '',
        y: 85,
        width: 985,
        height: 330,
        layout: 'absolute',
        items: [
                NS.chkTodos,
                NS.gridDatos,
                NS.labRegistros, 
            	NS.txtSeleccionados,
            	NS.labImporteTotal,
            	NS.txtImporteTotal,
               {
            	   xtype: 'button',
            	   text: 'Ejecutar',
            	   x: 700,
            	   y: 270,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				var regSeleccionados = NS.gridDatos.getSelectionModel().getSelections();
            	   				if(regSeleccionados.length > 0) {
            	   					if (NS.txtBanco.getValue() == 2){
	                					winTipoEnvio.show();
            	   					} else {
           	   						NS.ejecutar(regSeleccionados, NS.optSantanderOpt);
            	   					

            	   					}
            	   				}else
            	   					Ext.Msg.alert('Información SET', 'Debe seleccionar al menos un registro.');

            	   					
   							}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Limpiar',
                   x: 790,
                   y: 270,
                   width: 80,
                   height: 22,
                   listeners: {
                   		click: {
            	   			fn:function(e) {
            	   				NS.limpiar();
            	   			}
               			}
               		}
               }
        ]
    });

	NS.optTipoEnvioH2H = new Ext.form.RadioGroup ({
		id: PF + 'gOptTipoEnvio',
		name: PF + 'gOptTipoEnvio',
		x: 5,
		y: 5,
		columns: 1, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Host to host', name: 'optTipoEnvioH2H', inputValue: 0, checked: true},  
	          {boxLabel: 'Normal', name: 'optTipoEnvioH2H', inputValue: 1}
	    ]
	});
	NS.optEnvioBNMX = new Ext.form.RadioGroup({
		id: PF + 'optEnvioBNMX',
		name: PF + 'optEnvioBNMX',
		x: 10,
		columns: 1, //muestra los radiobuttons en una columna
		items: [
	          {boxLabel: 'Bancanet', name: 'optBNM', inputValue: 0, disabled:false},  
	          //{boxLabel: 'TEF (Transferencia Electronica de Fondos)', name: 'optBNM', inputValue: 1,disabled:true },
	         // {boxLabel: 'Citibank WorldLink', name: 'optBNM', inputValue: 2, disabled: true},  
	         //// {boxLabel: 'Mass Payment Citibank', name: 'optBNM', hidden: true, inputValue: 3, disabled: true},
	        //  {boxLabel: 'Flat File Citibank (Solo DLS)', name: 'optBNM', inputValue: 4, disabled: true},  
	          {boxLabel: 'Pay Link Citibank (Solo MN)', name: 'optBNM', inputValue: 5, disabled: false,checked: true},
	       //   {boxLabel: 'Citibank ACH Credit', name: 'optBNM', inputValue: 6, disabled: true},
	          {xtype: 'checkbox',
					id: PF + 'h2h',
					name: PF + 'h2h', 
					x: 850,
					y: 122,
					boxLabel: 'H2H AFRD',
					
					listeners: {
                 		check: {
                 			fn:function(opt, valor) {
                 				if(valor == true) {
                 					var a=1;
                 					parseInt(a)
                 					NS.bandera.setValue(a);
             

                 				}else {
                 					var b=2;
                 					parseInt(b)
                 					NS.bandera.setValue(b);
                 					
                 				}
                 			}
                 		}
                 	}
				},  
	     ]
	});
	
	var winTipoEnvio = new Ext.Window({
		title: 'Tipo de envio',
		modal: true,
		shadow: true,
		closeAction: 'hide',
		width: 200,
	   	height: 160,
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
	            		 var regSeleccionados = NS.gridDatos.getSelectionModel().getSelections();
//	            		 NS.ejecutar(regSeleccionados, NS.optSantanderOpt);
	            		 NS.ejecutar(regSeleccionados,  NS.optSantanderOpt);

	            	 }
			     }					      
	    ],
	   	items: [NS.optEnvioBNMX],
   	    listeners:{
   	        	show:{
   	        		fn:function(){
   	        			winTipoEnvio.show();	   	        				
   	        	    }
   	        	}
   	     }	
	});
	
	//Contenedor General
	NS.contGeneral = new Ext.form.FieldSet({
		title: '',
		width: 1010,
	    height: 440,
	    x: 20,
	    y: 5,
	    layout: 'absolute',
	    items:[
			NS.contBusqueda,
			NS.contGridBusqueda
		]
	});
	
	//Funcion para buscar los registros
//	NS.buscar = function(){
//		if (NS.cmbBanco.getValue() != "") {
//			NS.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
//			NS.idBanco = Ext.getCmp(PF + 'txtBanco').getValue();
//			
//			if(NS.noEmpresa == "" || NS.noEmpresa == 0)
//				NS.noEmpresa = 0;
//			
//			NS.storeDatos.baseParams.noEmpresa = parseInt(NS.noEmpresa);
//			NS.storeDatos.baseParams.idBanco = parseInt(NS.idBanco);
//			NS.storeDatos.load();
//		} else {
//			Ext.Msg.alert('Información SET', 'Debe seleccionar un banco');
//		}
//	};
	
	NS.buscar = function(){
		
//		var recordsDatosGrid=NS.storeDatos.data.items;
//		
//		if(recordsDatosGrid.length <= 0)
//		{
//			Ext.Msg.alert('Información SET','Debe agregar datos para la búsqueda');
//			return;
//		}
		if (NS.cmbBanco.getValue() != "") {
			NS.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
			NS.idBanco = Ext.getCmp(PF + 'txtBanco').getValue();
			
			if(NS.noEmpresa == "" || NS.noEmpresa == 0)
				NS.noEmpresa = 0;
			
			NS.storeDatos.baseParams.idEmpresa = parseInt(NS.noEmpresa);
	//		NS.storeDatos.baseParams.idBanco = parseInt(NS.idBanco);
			NS.storeDatos.baseParams.idBancoEmi = ''+NS.idBanco;
			NS.idBancoEmi=NS.idBanco;
			
			NS.storeDatos.load();
		} else {
			Ext.Msg.alert('Información SET', 'Debe seleccionar un banco');
		}
		
//		
//		NS.storeEnvioTransGrid.baseParams.idEmpresa = parseInt(Ext.getCmp( PF+'txtIdGrupo').getValue()) != null &&
//				Ext.getCmp( PF+'txtIdGrupo').getValue() != ''? parseInt(Ext.getCmp( PF+'txtIdGrupo').getValue()):0;
//				NS.storeEnvioTransGrid.baseParams.nomina = Ext.getCmp(PF + 'chkNomina').getValue()
//		NS.storeEnvioTransGrid.load();

		
	}
	
	//Funcion del checkBox confirmacion automatica
	NS.marcarTodo = function() {
		if(NS.record > 0)
			NS.tipoSeleccion.selectRange(-1, NS.record - 1);
	};
	
	
	//Funcion del boton ejecutar
	NS.ejecutar = function(registrosSelec, tipoPago) {
//		var matRegConfirmar = new Array ();
//		
//		for(var i=0; i<regSeleccionados.length; i++) {
//			var regConfirmar = {};
//			regConfirmar.folioRef = regSeleccionados[i].get('folioRef');
//			regConfirmar.fecValor = regSeleccionados[i].get('fecValor');
//			regConfirmar.noEmpresa = regSeleccionados[i].get('noEmpresa');
//			regConfirmar.idBanco = regSeleccionados[i].get('idBanco');
//			regConfirmar.noCliente = regSeleccionados[i].get('noCliente');
//			regConfirmar.idChequera = regSeleccionados[i].get('idChequera');
//			regConfirmar.idChequeraBenef = regSeleccionados[i].get('idChequeraBenef');
//			regConfirmar.importe = regSeleccionados[i].get('importe');
//			regConfirmar.noFolioDet = regSeleccionados[i].get('noFolioDet');
//			regConfirmar.referencia = regSeleccionados[i].get('referencia'); 
//			regConfirmar.beneficiario = regSeleccionados[i].get('beneficiario'); 
//			regConfirmar.rfcBenef = regSeleccionados[i].get('rfcBenef'); 
//			regConfirmar.concepto = regSeleccionados[i].get('concepto'); 
//			regConfirmar.sucursal = regSeleccionados[i].get('sucursal'); 
//			regConfirmar.plaza = regSeleccionados[i].get('plaza'); 
//			regConfirmar.noDocto = regSeleccionados[i].get('noDocto'); 
//			regConfirmar.idBancoBenef = regSeleccionados[i].get('idBancoBenef'); 
//			matRegConfirmar[i] = regConfirmar;
//		}
		var matrizGrid = new Array ();
		var matrizCriterios = new Array();
	//	var registrosSelec = NS.gridEnvioTransGrid.getSelectionModel().getSelections();
		var optValorBNMX = 0;
		var optValorHSBC = 0;

		if(NS.idBancoEmi == 2) {
			var optEnvioBNMX = Ext.getCmp(PF + 'optEnvioBNMX').getValue();
			optValorBNMX = parseInt(optEnvioBNMX.getGroupValue());
			
		}else if(NS.idBancoEmi == 21) {
			var envioHSBC = Ext.getCmp(PF + 'optEnvioHSBC').getValue();
			optValorHSBC = parseInt(envioHSBC.getGroupValue());
		}
		
		for(var i=0; i<registrosSelec.length; i++) {
			var registroGrid = {};
			registroGrid.noDocto = registrosSelec[i].get('noDocto');
			registroGrid.nomEmpresa = registrosSelec[i].get('nomEmpresa');
			registroGrid.fecOperacion = NS.cambiarFechaGrid(""+registrosSelec[i].get('fecOperacionStr'));
			registroGrid.idBancoCityStr = registrosSelec[i].get('idBancoCityStr');
			registroGrid.beneficiario = registrosSelec[i].get('beneficiario');
			registroGrid.importe = registrosSelec[i].get('importe');
			registroGrid.idDivisa = registrosSelec[i].get('idDivisa');
			registroGrid.nombreBancoBenef = registrosSelec[i].get('nombreBancoBenef');
			registroGrid.idChequeraBenef = registrosSelec[i].get('idChequeraBenef');
			registroGrid.idChequeraBenefReal = registrosSelec[i].get('idChequeraBenefReal');
			registroGrid.concepto = registrosSelec[i].get('concepto');
			registroGrid.noFolioDet = registrosSelec[i].get('noFolioDet');
			registroGrid.noEmpresa = registrosSelec[i].get('noEmpresa');
			registroGrid.origenMov = registrosSelec[i].get('origenMov');
			registroGrid.division = registrosSelec[i].get('division');
			registroGrid.noCliente = registrosSelec[i].get('noCliente');
			registroGrid.especiales = registrosSelec[i].get('especiales');
			registroGrid.complemento = registrosSelec[i].get('complemento');
			registroGrid.clave = registrosSelec[i].get('clave');
			registroGrid.idBancoBenef = registrosSelec[i].get('idBancoBenef');
			registroGrid.plazaBenef = registrosSelec[i].get('plazaBenef');
			registroGrid.plaza = registrosSelec[i].get('plaza');
			registroGrid.sucDestino = registrosSelec[i].get('sucDestino');
			registroGrid.clabeBenef = registrosSelec[i].get('clabeBenef');
			registroGrid.bLayoutComerica = registrosSelec[i].get('bLayoutComerica');
			registroGrid.idChequera = registrosSelec[i].get('idChequera');
			registroGrid.fecValor = registrosSelec[i].get('fecValorStr');
			registroGrid.idBanco = registrosSelec[i].get('idBanco');
			registroGrid.instFinan = registrosSelec[i].get('instFinan');
			registroGrid.sucOrigen = registrosSelec[i].get('sucOrigen');
			registroGrid.rfcBenef = registrosSelec[i].get('rfcBenef');
			registroGrid.rfc = registrosSelec[i].get('rfc');
			registroGrid.horaRecibo = registrosSelec[i].get('horaRecibo');
			registroGrid.idContratoMass = registrosSelec[i].get('idContratoMass');
			registroGrid.tipoEnvioLayout = registrosSelec[i].get('tipoEnvioLayout');
			registroGrid.nomBancoIntermediario = registrosSelec[i].get('nomBancoIntermediario');
			registroGrid.descBancoBenef = registrosSelec[i].get('descBancoBenef');
			registroGrid.aba = registrosSelec[i].get('aba');
			registroGrid.swiftCode = registrosSelec[i].get('swiftCode');
			registroGrid.abaIntermediario = registrosSelec[i].get('abaIntermediario');
			registroGrid.swiftIntermediario = registrosSelec[i].get('swiftIntermediario');
			registroGrid.abaCorresponsal = registrosSelec[i].get('abaCorresponsal');
			registroGrid.swiftCorresponsal = registrosSelec[i].get('swiftCorresponsal');
			registroGrid.nomBancoCorresponsal = registrosSelec[i].get('nomBancoCorresponsal');
			registroGrid.descUsuarioBital = registrosSelec[i].get('descUsuarioBital');
			registroGrid.descServicioBital = registrosSelec[i].get('descServicioBital');
			registroGrid.idBancoEmi=NS.idBancoEmi; 
			
			matrizGrid[i] = registroGrid;
		}
		var jSonString = Ext.util.JSON.encode(matrizGrid);
		var criterios= {};
//		criterios.chkTodasEmpresas = NS.chkTodasEmpresas;
//		criterios.chkSoloLocales = NS.chkSoloLocales;
//		criterios.chkConvenioCie = NS.chkConvenioCie;
//		criterios.chkConvenioSant = NS.chkConvenioSant;
//		criterios.chkDebito = NS.chkDebito;
		if (NS.optTipoEnvioH2H.getValue() != null && NS.optTipoEnvioH2H.getValue().getGroupValue() == 0) {
			criterios.chkH2HSantander = true;			
			NS.chkH2HSantander = true;
		} else {
			criterios.chkH2HSantander = false;
			NS.chkH2HSantander = false;
		}
		//criterios.chkMassPayment = NS.chkMassPayment;
		criterios.chkH2H = Ext.getCmp(PF + 'h2h').getValue();
		criterios.optEnvioBNMX = optValorBNMX;
		criterios.optEnvioHSBC = optValorHSBC;
		//criterios.optTipoEnvio = NS.optTipoEnvio;
		criterios.optComerica = NS.optComerica;
		criterios.optVenc = NS.optVenc;
//		criterios.pdMontoIni = NS.pdMontoIni;
//		criterios.pdMontoFin = NS.pdMontoFin;
//		criterios.psFechaValor = NS.psFechaValor;
//		criterios.psFecValOrig = NS.psFecValOrig;
//		criterios.idBancoRec = NS.idBancoRec;
		criterios.idBancoEmi = NS.idBancoEmi; 
//		criterios.idDivisa = NS.idDivisa;
//		criterios.idDivision = NS.idDivision;
		criterios.idUsuario = NS.idUsuario;
		criterios.idEmpresa = NS.idEmpresa;
	//	criterios.nomina = Ext.getCmp(PF + 'chkNomina').getValue();
		matrizCriterios[0] = criterios;
		
		var jsonString = Ext.util.JSON.encode(matrizGrid);
		var jsonString2 = Ext.util.JSON.encode(matrizCriterios);
		
//		var jSonString = Ext.util.JSON.encode(matRegConfirmar);
//		var idBanco = parseInt(NS.txtBanco.getValue());
		var tipoPagoSelec = tipoPago.getValue().inputValue;
		var chkSantanderH2H = false;
		var chkBanamexH2H = false;
//		if (NS.optTipoEnvioH2H.getValue().getGroupValue() == 0) chkSantanderH2H = true;	
		//if (NS.optTipoEnvioH2H.getValue().getGroupValue() == 0) chkBanamexH2H = true;		
//		ChequeOcurreAction.ejecutar(jSonString, tipoPagoSelec, idBanco, chkBanamexH2H,  function(res, e) {
		ChequeOcurreAction.ejecutarCheques(jsonString, jsonString2, NS.bandera.getValue(),  function(res, e) {
		if(res != null && res != undefined && res != '') {
				if(res.resNombreArchivoLocal!=""||res.resNombreArchivoLocal!=null){
					console.log("respuesta de archivo "+res.resNombreArchivoLocal);
					NS.imprimeReporte(res.resNombreArchivoLocal);
					winTipoEnvio.hide();
					NS.limpiar();
					console.log();
					//Ext.Msg.alert('Información SET',res.msgUsuario);
					if( NS.LI_BANCO != 30&&res.directo=='si'){
    					strParams = '?nomReporte=layouts';
    					strParams += '&'+'nomParam1=rutaArchivo';
    					strParams += '&'+'valParam1='+res.resRutaLocal+'\\'+res.resNombreArchivoLocal;
    					strParams += '&'+'nomParam2=nombreArchivo';
    					strParams += '&'+'valParam2='+res.resNombreArchivoLocal;
    					window.open("/SET/jsp/bfrmwrk/gnrators/abretxt.jsp"+strParams);
  			   		}
				}
			}
		});
	};
	
	//Funcion para limpiar los campos
	NS.limpiar = function(){
		NS.cmbEmpresas.reset();
		Ext.getCmp(PF + 'txtBanco').setValue('');
		NS.cmbBanco.reset();
		NS.storeBancos.removeAll();
		NS.storeBancos.baseParams.noEmpresa = 0;
		NS.storeBancos.load();
		//Ext.getCmp(PF + 'txtReferencia').setValue('');
		//NS.cmbChequeras.reset();
		//NS.storeChequeras.removeAll();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
		NS.ConfirmacionTransferencias.getForm().reset();
		Ext.getCmp(PF + 'txtEmpresa').setValue('0');
		Ext.getCmp(PF + "txtImporteTotal").setValue('0.00');
		NS.txtSeleccionados.setValue('0');
	};
		
	//Contenedor principal del formulario
	NS.ConfirmacionTransferencias = new Ext.FormPanel({
		title: 'Envio de Cheque Ocurre.',
	    width: 1020,
	    height: 500,
	    frame: true,
	    padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF + 'envioChequeOcurre',
	    name: PF+ 'envioChequeOcurre',
	    renderTo: NS.tabContId,
	    items:[
	           NS.contGeneral
	    ]
	});
	NS.ConfirmacionTransferencias.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});