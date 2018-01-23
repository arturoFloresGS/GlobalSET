//@autor Cristian Garcia Garcia
//18/Marzo/2011
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.BancaElectronica.EnvioTransferenciasBe');
	//NS.tabContId = 'contEnvioTranferencias';
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	NS.tabContId = apps.SET.tabContainerId;
	NS.cveBanco=0;//Esta varibale es para obtener el id del banco y mostrar componentes
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
        NS.pdMontoIni=0;
        NS.pdMontoFin=0;
        NS.psFechaValor='';
        NS.psFecValOrig='';
        NS.idBancoRec=0;
        NS.idBancoEmi=0;
        NS.idDivisa=0;
        NS.idDivision=0;
        NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
		NS.GS_ID_EMPRESA = apps.SET.NO_EMPRESA;
		NS.GI_USUARIO = apps.SET.iUserId ;
        NS.idUsuario= NS.GI_USUARIO;
        NS.idEmpresa= NS.GS_ID_EMPRESA;
	}
	NS.inicializar();
	
	NS.limpiar = function(){
		
		NS.cmbCriterio.reset();
		NS.cmbDivisa.reset();
		NS.cmbDivision.reset();
		NS.cmbBancoReceptor.reset();
		NS.cmbAgrupa.reset();
		
		Ext.getCmp(PF+'fechaValor').setValue("");
		Ext.getCmp(PF+'monto1').setValue("");
		Ext.getCmp(PF+'monto2').setValue("");
		Ext.getCmp(PF+'fecValorOriginal').setValue("");
		Ext.getCmp(PF + 'chkH2HSantander').setVisible(false);
		Ext.getCmp(PF + 'chkNomina').setVisible(false);
		Ext.getCmp(PF + 'chkNomina').setValue(false);
		
		NS.gridDatos.store.removeAll();
		NS.gridEnvioTransGrid.store.removeAll();
		NS.gridEnvioTransGrid.getView().refresh();
		NS.gridDatos.getView().refresh();
		NS.EnvioTransferencias.getForm().reset();
		Ext.getCmp(PF + 'txtEmpresa').setValue('');
		NS.cmbGrupo.reset();
		Ext.getCmp(PF+'cmbGrupo').setValue('')
		
		Ext.getCmp(PF + 'contEnvioBancos').setVisible(false);
	}
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({
				
	});
	
	NS.inicializarValores = function(){
		
	}
	
	NS.cambiarFecha = function(fecha)
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
    
    
//////bandera
    NS.banderaBBVA = new Ext.form.TextField({
		id: PF + 'banderaBBVA',
        name: PF + 'banderaBBVA',
        x: 120,
        y: 60,
        value:0,
        width: 430, 
    	emptyText: '',

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
	
	NS.cambiarFechaGrid=function(fecha){
		return fecha.substring(0,10);
	}
	
	//Función para la llamada al reporte de envio de transferencias
	
	NS.abrirTxt = function(archivo) {
		strParams = '?nomReporte=layouts';
		strParams += '&'+'nomParam1=rutaArchivo';
		strParams += '&'+'valParam1='+archivo.resRutaLocal + archivo.resNombreArchivoLocal;
		strParams += '&'+'nomParam2=nombreArchivo';
		strParams += '&'+'valParam2='+archivo.resNombreArchivoLocal;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/abretxt.jsp"+strParams);
		return;
	}
	
	NS.imprimeReporte = function(archivo){
		var nomEmpresa;
		var registrosSelec = NS.gridEnvioTransGrid.getSelectionModel().getSelections();
		
		//if(parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue()) == 0) {    PF+'txtIdGrupo'
		if(parseInt(Ext.getCmp(PF + 'txtIdGrupo').getValue()) == 0) {
			if(registrosSelec.length == 1)
				nomEmpresa = registrosSelec[0].get('nomEmpresa');
			else
				nomEmpresa = 'VARIAS EMPRESAS DEL GRUPO';
		}else
			//nomEmpresa = NS.storeEmpresas.getById(parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue())).get('nomEmpresa');
			nomEmpresa = NS.storeGrupo.getById(parseInt(Ext.getCmp(PF + 'txtIdGrupo').getValue())).get('descripcion');
		
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
	
	NS.ejecutar = function() {
		Ext.MessageBox.show({
		       title : 'Información SET',
		       msg : 'Generando Documentos, espere por favor...',
		       width : 300,
		       wait : true,
		       progress:true,
		       waitConfig : {interval:200}//,
		       //icon :'lupita'
	           //icon :'forma_alta', //custom class in msg-box.html
	           ///animateTarget : 'mb7'
	   		});

		var matrizGrid = new Array ();
		var matrizCriterios = new Array();  
		var registrosSelec = NS.gridEnvioTransGrid.getSelectionModel().getSelections();
		var optValorBNMX = 0;
		var optValorHSBC = 0;
		var optValorBBVA = 0;
		
		
		  if(NS.idBancoEmi == 2) {
			var optEnvioBNMX = Ext.getCmp(PF + 'optEnvioBNMX').getValue();
			optValorBNMX = parseInt(optEnvioBNMX.getGroupValue());
			//Ext.Msg.alert("optEnvioBNMX",optEnvioBNMX);
		}else if(NS.idBancoEmi == 21) {
			var envioHSBC = Ext.getCmp(PF + 'optEnvioHSBC').getValue();
			optValorHSBC = parseInt(envioHSBC.getGroupValue());
		}else if(NS.idBancoEmi == 12) {
				var optEnvioBBVA= Ext.getCmp(PF + 'optEnvioBBVA').getValue();
				optEnvioBBVA = parseInt(optEnvioBBVA.getGroupValue());
				NS.banderaBBVA.setValue(parseInt(optEnvioBBVA));
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
		
		var criterios= {};
		criterios.chkTodasEmpresas = NS.chkTodasEmpresas;
		criterios.chkSoloLocales = NS.chkSoloLocales;
		criterios.chkConvenioCie = NS.chkConvenioCie;
		criterios.chkConvenioSant = NS.chkConvenioSant;
		criterios.chkDebito = NS.chkDebito;
		
		if (NS.optTipoEnvioH2H.getValue() != null && NS.optTipoEnvioH2H.getValue().getGroupValue() == 0) {
			criterios.chkH2HSantander = true;			
			NS.chkH2HSantander = true;
		} else {
			criterios.chkH2HSantander = false;
			NS.chkH2HSantander = false;
		}
		criterios.chkMassPayment = NS.chkMassPayment;
		criterios.chkH2H = Ext.getCmp(PF + 'chkH2H').getValue();
		criterios.optEnvioBNMX = optValorBNMX;
		criterios.optEnvioHSBC = optValorHSBC;
		criterios.optEnvioBBVA = optValorBBVA;
		criterios.optTipoEnvio = NS.optTipoEnvio;
		criterios.optComerica = NS.optComerica;
		criterios.optVenc = NS.optVenc;
		criterios.pdMontoIni = NS.pdMontoIni;
		criterios.pdMontoFin = NS.pdMontoFin;
		criterios.psFechaValor = NS.psFechaValor;
		criterios.psFecValOrig = NS.psFecValOrig;
		criterios.idBancoRec = NS.idBancoRec;
		criterios.idBancoEmi = NS.idBancoEmi; 
		criterios.idDivisa = NS.idDivisa;
		criterios.idDivision = NS.idDivision;
		criterios.idUsuario = NS.idUsuario;
		criterios.idEmpresa = NS.idEmpresa;
		criterios.nomina = Ext.getCmp(PF + 'chkNomina').getValue();
	
		matrizCriterios[0] = criterios;
		
		
		
		var jsonString = Ext.util.JSON.encode(matrizGrid);
		var jsonString2 = Ext.util.JSON.encode(matrizCriterios);
	
		EnvioTransferenciasAction.ejecutarEnvioTrans(jsonString, jsonString2,NS.bandera.getValue(),NS.banderaBBVA.getValue(), function(result, e) {
			if(result != null && result != undefined && result != '') {
				//Ext.Msg.alert('SET', "entro a envioTrans");
				//Descomentar una vez que ya se cuente con el servidor de archivos.
				//Ext.Msg.alert('SET', '' + result.msgUsuario);
				Ext.getCmp(PF + 'optEnvioHSBC').setVisible(false);
				Ext.getCmp(PF + 'optEnvioBNMX').setVisible(false);
				Ext.getCmp(PF + 'contEnvioBancos').setVisible(false);

				Ext.Msg.alert('SET', '' + result.msgUsuario);
				if(result.msgUsuario=='Error el registro no cuenta con clabe'){
					winTipoEnvio.hide();
					NS.buscar
				}
				if(result.resExitoLocal == true){
					if(NS.idBancoEmi == 14 && NS.chkH2HSantander==true){
						NS.imprimeReporte(result.resNombreArchivoLocal);
						NS.buscar();
						winTipoEnvio.hide();
						return;
					}
					NS.abrirTxt(result);					
					NS.imprimeReporte(result.resNombreArchivoLocal);
				}
				NS.buscar();
			} else {
				Ext.Msg.alert('SET', 'Error desconocido.');
				NS.buscar();
			}
		});
	};
	
	//Etiqueta empresa
	NS.labEmpresa = new Ext.form.Label({
		text: 'Grupo Empresa:',
        x: 10,
        y: 0
	});
	
	NS.labPropuesta = new Ext.form.Label({
		text: 'Propuesta:',
        x: 10,
        y: 40
	});
	
	
    //Caja numero de empresa
	NS.txtEmpresa = new Ext.form.TextField({
		id: PF + 'txtEmpresa',
        name:PF + 'txtEmpresa',
        hidden: true,
		x: 10,
        y: 15,
        width: 50,
        tabIndex: 0,
        listeners: {
			change: {
				fn: function(caja,valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
					else {
						Ext.getCmp(PF + 'txtEmpresa').setValue(0);
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
		directFn: EnvioTransferenciasAction.obtenerEmpresas, 
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
	
	NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'Id_Grupo_Flujo',
			campoDos:'Desc_Grupo_Flujo',
			tabla:'Cat_Grupo_Flujo',
			condicion:'',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: EnvioTransferenciasAction.llenarCombo, 
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
					Ext.Msg.alert('SET','No Existen grupos');
				} 
				NS.storeGrupo.sort('id', 'ASC');
				/*else{
					//Se agrega la opcion todos los grupos
		 			var recordsStoreGrupo = NS.storeGrupo.recordType;	
					var todas = new recordsStoreGrupo({
						id: 0,
						descripcion: '***************TODAS***************'
			       	});
			   		NS.storeGrupo.insert(0,todas);
				}*/
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	

    
	//Combo empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
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
		hidden: true,
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
				}
			}
		}
	});
	NS.storeGrupo.load();
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo
		,name: PF+'cmbGrupo'
		,id: PF+'cmbGrupo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	//,x: 45
        //,y: 165
        //,width: 195
		,x: 70
        ,y: 15
        ,width: 320
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un grupo'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,hidden: false //true
		,tabIndex: 13
		,listeners:{
			select:{
				fn:function(combo, valor) {
				//NS.storeMaestro.baseParams.idGrupoRubro=combo.getValue();
				BFwrk.Util.updateComboToTextField(PF+'txtIdGrupo',NS.cmbGrupo.getId());
				}
			}
		}
	});
	
	
	//Datos para el store de criterio
	NS.datosCriterio= [
			//['0', 'BANCO EMISOR'],
			['1', 'MONTOS'],
			['2', 'BANCO RECEPTOR'],
			['3', 'FECHA VALOR'],
			['4', 'FEC. VALOR ORIGINAL'],
			['5', 'DIVISA'],
			['6', 'DIVISION']
					];
		
		// store de criterio
	 NS.storeCriterio = new Ext.data.SimpleStore({
		idProperty: 'idCriterio',
		fields: [
					{name: 'idCriterio'},
					{name: 'nombre'}
				]
		});
	NS.storeCriterio.loadData(NS.datosCriterio)
	
	
	//Store Datos donde se muestran los valores seleccionados
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
	//Grid para mostrar los datos seleccionados
	NS.gridDatos = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		columns : [ {
			id :'criterio',
			header :'Criterio',
			width :138,
			dataIndex :'criterio'
		}, {
			header :'Valor',
			width :139,
			dataIndex :'valor'
		} ],
		width: 280,
       	height: 105,
		title :'',
		listeners:{
			dblclick:{
				fn:function(grid){
					var records = NS.gridDatos.getSelectionModel().getSelections();
					for(var i=0;i<records.length;i++)
					{
						if(records[i].get('criterio')==='BANCO EMISOR' || records[i].get('criterio')==='DIVISA')
							return;
						NS.gridDatos.store.remove(records[i]);
						NS.gridDatos.getView().refresh();
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
					}
				}
			}
		}
	});
	
	//combo Criterio
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
        ,y: 135
        ,width: 180
		,valueField:'idCriterio'
		,displayField:'nombre'
		,autocomplete: true
		,emptyText: 'Seleccione un criterio'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					var tamGrid=(NS.storeDatos.data.items).length;
				
				    combo.setDisabled(true);
				    var datosClase = NS.gridDatos.getStore().recordType;
					/*if(combo.getValue()==0)
					{
						combo.setDisabled(false);
					}*/
					if(combo.getValue()==1)
					{
						Ext.getCmp(PF+'monto1').setDisabled(false);
						Ext.getCmp(PF+'monto2').setDisabled(false);
						Ext.getCmp(PF+'monto1').setVisible(true);
						Ext.getCmp(PF+'monto2').setVisible(true);
						
						Ext.getCmp(PF+'cmbBancoReceptor').setVisible(false);
						Ext.getCmp(PF+'fechaValor').setVisible(false);
						Ext.getCmp(PF+'fecValorOriginal').setVisible(false);
						Ext.getCmp(PF+'cmbDivisa').setVisible(false);
						Ext.getCmp(PF+'cmbDivision').setVisible(false);
						
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
							Ext.Msg.alert('información SET','Ya indicó los montos necesita borralos antes');
							combo.setDisabled(false);
							return;
						}
						else{
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
					else if(combo.getValue()==2)
					{
						Ext.getCmp(PF+'cmbBancoReceptor').setDisabled(false);
						Ext.getCmp(PF+'cmbBancoReceptor').setVisible(true);
					
						Ext.getCmp(PF+'monto1').setVisible(false);
						Ext.getCmp(PF+'monto2').setVisible(false);
						Ext.getCmp(PF+'fechaValor').setVisible(false);
						Ext.getCmp(PF+'fecValorOriginal').setVisible(false);
						Ext.getCmp(PF+'cmbDivisa').setVisible(false);
						Ext.getCmp(PF+'cmbDivision').setVisible(false);
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='BANCO RECEPTOR')
								indice=i;
						}
						if(indice>0)
						{
							Ext.Msg.alert('información SET','Ya indicó el banco receptor necesita borralo antes');
							combo.setDisabled(false);
							return;
						}
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'BANCO RECEPTOR',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}	
					}
					else if(combo.getValue()==3)
					{
						Ext.getCmp(PF+'fechaValor').setDisabled(false);
						Ext.getCmp(PF+'fechaValor').setVisible(true);
						
						Ext.getCmp(PF+'monto1').setVisible(false);
						Ext.getCmp(PF+'monto2').setVisible(false);
						Ext.getCmp(PF+'cmbBancoReceptor').setVisible(false);
						Ext.getCmp(PF+'fecValorOriginal').setVisible(false);
						Ext.getCmp(PF+'cmbDivisa').setVisible(false);
						Ext.getCmp(PF+'cmbDivision').setVisible(false);
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='FECHA VALOR')
								indice=i;
						}
						if(indice>0)
						{
							Ext.Msg.alert('información SET','Ya indicó la fecha valor necesita borrala antes');
							combo.setDisabled(false);
							return;
						}
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'FECHA VALOR',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}	
								
					}
					else if(combo.getValue()==4)
					{
						Ext.getCmp(PF+'fecValorOriginal').setDisabled(false);
						Ext.getCmp(PF+'fecValorOriginal').setVisible(true);
						
						Ext.getCmp(PF+'monto1').setVisible(false);
						Ext.getCmp(PF+'monto2').setVisible(false);
						Ext.getCmp(PF+'cmbBancoReceptor').setVisible(false);
						Ext.getCmp(PF+'fechaValor').setVisible(false);
						Ext.getCmp(PF+'cmbDivisa').setVisible(false);
						Ext.getCmp(PF+'cmbDivision').setVisible(false);
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='FEC. VALOR ORIGINAL')
								indice=i;
						}
						if(indice>0)
						{
							Ext.Msg.alert('información SET','Ya indicó la fecha original necesita borrala antes');
							combo.setDisabled(false);
							return;
						}
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'FEC. VALOR ORIGINAL',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}	
					}
					else if(combo.getValue()==5)
					{
						Ext.getCmp(PF+'cmbDivisa').setDisabled(false);
						Ext.getCmp(PF+'cmbDivisa').setVisible(true);
						
						Ext.getCmp(PF+'fecValorOriginal').setDisabled(false);
						Ext.getCmp(PF+'fecValorOriginal').setVisible(false);
						Ext.getCmp(PF+'monto1').setVisible(false);
						Ext.getCmp(PF+'monto2').setVisible(false);
						Ext.getCmp(PF+'cmbBancoReceptor').setVisible(false);
						Ext.getCmp(PF+'fechaValor').setVisible(false);
						Ext.getCmp(PF+'cmbDivision').setVisible(false);
						
					 	var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++){
							if(recordsDatGrid[i].get('criterio')=='DIVISA')
								indice=i;
						}
						if(indice>0)
						{
							//Ext.Msg.alert('información SET','Ya indicó la divisa necesita borrala antes');
							combo.setDisabled(false);
							return;
						}
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'DIVISA',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}	
					else if(combo.getValue()==6)
					{
						NS.storeDivision.load();
						
						Ext.getCmp(PF+'cmbDivision').setDisabled(false);
						Ext.getCmp(PF+'cmbDivision').setVisible(true);						
						
						Ext.getCmp(PF+'cmbBancoReceptor').setDisabled(true);
						Ext.getCmp(PF+'cmbBancoReceptor').setVisible(false);
						Ext.getCmp(PF+'monto1').setVisible(false);
						Ext.getCmp(PF+'monto2').setVisible(false);
						Ext.getCmp(PF+'fechaValor').setVisible(false);
						Ext.getCmp(PF+'fecValorOriginal').setVisible(false);
						Ext.getCmp(PF+'cmbDivisa').setVisible(false);
						
						
					 	var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='DIVISION')
								indice=i;
						}
						if(indice>0)
						{
							Ext.Msg.alert('información SET','Ya indicó la división necesita borrala antes');
							combo.setDisabled(false);
							return;
						}
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'DIVISION',
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
	});	//Termina cmbCriterio
	
	//Store BancoEmisor
	  NS.storeBancoEmisor = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idUsuario: NS.GI_USUARIO
		},
		root: '',
		paramOrder:['idUsuario'],
		directFn: EnvioTransferenciasAction.obtenerBancoEmisor, 
		idProperty: 'id', 
		fields: [
			{name: 'id'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoEmisor, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No existe banco emisor');
					
				//cargar el primer parametro del grid banco emisor
			 	var datosClase = NS.gridDatos.getStore().recordType;
		    	var datos = new datosClase({
	                	criterio: 'BANCO EMISOR',
						valor: ''
	            	});
                    NS.gridDatos.stopEditing();
            		NS.storeDatos.insert(0, datos);
            		NS.gridDatos.getView().refresh();
            		
        		var datos1 = new datosClase({
	                	criterio: 'DIVISA',
						valor: 'PESOS'
	            	});
                    NS.gridDatos.stopEditing();
            		NS.storeDatos.insert(1, datos1);
            		NS.gridDatos.getView().refresh();
        		
			}
		}
	}); 
	NS.storeBancoEmisor.load();
	
			//combo Banco Emisor
	NS.cmbBancoEmisor = new Ext.form.ComboBox({
		store: NS.storeBancoEmisor
		,name: PF+'cmbBancoEmisor'
		,id: PF+'cmbBancoEmisor'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 95
        ,width: 250
        ,tabIndex: 3
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione banco emisor'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					NS.cveBanco = combo.getValue(); 
					if(NS.cveBanco != 12)//Bancomer
					{
						Ext.getCmp(PF+'chkConvenio').setVisible(false);
						NS.storeEnvioTransGrid.baseParams.chkConvenioCie= false;
					} else if(NS.optTipoEnvio == 'Mismo' ) {
						Ext.getCmp(PF+'chkConvenio').setVisible(true);
						Ext.getCmp(PF+'chkConvenio').setValue(false);
						//NS.storeEnvioTransGrid.baseParams.chkConvenioCie= true;
					}
										
					if(NS.cveBanco==1260)//Comerica  Bank
					{
						Ext.getCmp(PF+'contComerica').setVisible(true);
						NS.storeEnvioTransGrid.baseParams.optComerica = 'ACH';
					}
					else
					{
						Ext.getCmp(PF+'contComerica').setVisible(false);
						NS.storeEnvioTransGrid.baseParams.optComerica = '';
					}
					if(NS.cveBanco==135)//Nacional Financiera
					{
						Ext.getCmp(PF+'contVenc').setVisible(true);
						NS.storeEnvioTransGrid.baseParams.optVenc = true;
					}
					else
					{
						Ext.getCmp(PF+'contVenc').setVisible(false);
						NS.storeEnvioTransGrid.baseParams.optVenc = false;
					}
					
					if(NS.cveBanco != 14) {//Santander
						//Ext.getCmp(PF + 'chkH2HSantander').setVisible(false);
						Ext.getCmp(PF + 'chkNomina').setVisible(false);
						Ext.getCmp(PF + 'chkNomina').setValue(false);
						Ext.getCmp(PF + 'chkConvenioSant').setVisible(false);
						Ext.getCmp(PF + 'chkConvenioSant').setValue(false);
						Ext.getCmp(PF + 'chkDebito').setVisible(false);
						Ext.getCmp(PF + 'chkDebito').setValue(false);
//						NS.storeEnvioTransGrid.baseParams.chkH2HSantander = false;
					} else {
						if(NS.optTipoEnvio == 'Mismo' ) {
							Ext.getCmp(PF + 'chkConvenioSant').setVisible(true);
							Ext.getCmp(PF + 'chkConvenioSant').setValue(false);
							Ext.getCmp(PF + 'chkDebito').setVisible(true);
							Ext.getCmp(PF + 'chkDebito').setValue(false);
						}
						//Ext.getCmp(PF + 'chkH2HSantander').setVisible(false);
						Ext.getCmp(PF + 'chkNomina').setVisible(true);
						/*if(NS.optTipoEnvio == 'Mismo' || NS.optTipoEnvio == 'InterBancario'){
							NS.chkH2HSantander = 'true';
                            if (NS.optTipoEnvio == 'Mismo') {                            	
                                Ext.getCmp(PF + 'chkH2HSantander').setVisible(true);
                                Ext.getCmp(PF+'chkH2HSantander').setValue(true);
                            } else if (NS.optTipoEnvio == 'InterBancario')  {
                                Ext.getCmp(PF + 'chkH2HSantander').setVisible(true);
                                Ext.getCmp(PF+'chkH2HSantander').setValue(true);
                            }
						}  */
					}
						
					var datosClase = NS.gridDatos.getStore().recordType;
					var recordsDatGrid=NS.storeDatos.data.items;
					
						var banco=NS.storeBancoEmisor.getById(combo.getValue()).get('descripcion');
					 	
					 	NS.storeDatos.remove(recordsDatGrid[0]);
					 	
				    	var datos = new datosClase({
	                			criterio: 'BANCO EMISOR',
								valor: banco
	            			});
                       	NS.gridDatos.stopEditing();
            			NS.storeDatos.insert(0, datos);
            			NS.gridDatos.getView().refresh();
				}
			}
		}
	});	//Termina cmbBancoEmisor

		//Store propuesta
	  	NS.storePropuesta = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: EnvioTransferenciasAction.llenaComboCveControl, 
		idProperty: 'idStr', 
		fields: [
			{name: 'idStr'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePropuesta, msg:"Cargando..."});
				
				if(records.length==null || records.length<=0)
					{
					 	Ext.Msg.alert('Información SET','No existen claves de propuestas');
						return;
					}

			}
		}
	}); 
		

		NS.cmbPropuesta = new Ext.form.ComboBox({
			store: NS.storePropuesta,  
			name: PF+'cmbPropuesta'
			,id: PF+'cmbPropuesta'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
	        ,x: 10
	        ,y: 55
	        ,width: 250
	        ,tabIndex: 3
	        ,editable:false
			,valueField:'idStr'
			,displayField:'descripcion'
			,hidden: true
			,autocomplete: true
			,emptyText: 'Seleccione una propuesta'
			,triggerAction: 'all'
			,value: ''
			,listeners:{
				select:{
					fn:function(combo, value) {
						if(combo.getValue() != null && combo.getValue() != '' && combo.getValue() != undefined)
							NS.propuesta=''+combo.getValue()
						
					}
				}
			}
		});	//Termina cmbBancoEmisor
		NS.txtCveControl = new Ext.form.TextField({
			id: PF + 'txtCveControl',
			name: PF + 'txtCveControl',
			x: 10,
	        y: 55,
			width: 145,maxLength: 20,
			enableKeyEvents:true,
			listeners:{
				keypress:{
		 			fn:function(caja, e) {
		 				if(e.keyCode ==18){ 
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
		 		
			}
			
		});
		//NS.storePropuesta.load();
		
		
		//Store banco receptor
	  	NS.storeBancoReceptor = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_banco',
			campoDos:'desc_banco',
			tabla:'cat_banco',
			condicion:'',
			orden:'2'
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		directFn: EnvioTransferenciasAction.llenarComboGral, 
		idProperty: 'id', 
		fields: [
			{name: 'id'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoReceptor, msg:"Cargando..."});
					
					
			}
		}
	}); 
	
	NS.storeBancoReceptor.load();
	
			//combo Bancos
		NS.cmbBancoReceptor = new Ext.form.ComboBox({
		store: NS.storeBancoReceptor
		,name: PF+'cmbBancoReceptor'
		,id: PF+'cmbBancoReceptor'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 210
        ,y: 135
        ,width: 180
        ,tabIndex: 3
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione banco receptor'
		,triggerAction: 'all'
		,value: ''
		,hidden: false
		,disabled: true
		,listeners:{
			select:{
				fn:function(combo, value) {
					var indice=0;
					var i=0;
					var datosClase = NS.gridDatos.getStore().recordType;
					var recordsDatGrid=NS.storeDatos.data.items;
					
						var banco=NS.storeBancoReceptor.getById(combo.getValue()).get('descripcion');
					 	
				    	for(i=0;i<recordsDatGrid.length;i++)
							{
								if(recordsDatGrid[i].get('criterio')=='BANCO RECEPTOR')
								{
									indice=i;
									NS.storeDatos.remove(recordsDatGrid[indice]);
								}
							}
							
				    	var datos = new datosClase({
	                			criterio: 'BANCO RECEPTOR',
								valor: banco
	            			});
	                   	NS.gridDatos.stopEditing();
	           			NS.storeDatos.insert(indice, datos);
	           			NS.gridDatos.getView().refresh();
	           			
						combo.setDisabled(true);
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				}
			}
		}
	});	//Termina cmbBancoReceptor
	
	
	//Store divisa
	  	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: EnvioTransferenciasAction.llenarComboDivisa, 
		idProperty: 'idDivisa', 
		fields: [
			{name: 'idDivisa'},
			{name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
				
				if(records.length==null || records.length<=0)
					{
					 	Ext.Msg.alert('Información SET','No existen divisas');
						return;
					}

			}
		}
	}); 
	
	NS.storeDivisa.load();
	
		//combo Bancos
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa
		,name: PF+'cmbDivisa'
		,id: PF+'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 210
        ,y: 135
        ,width: 180
        ,tabIndex: 3
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione divisa'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,listeners:{
			select:{
				fn:function(combo, value) {
				
					var indice=0;
					var i=0;
					var datosClase = NS.gridDatos.getStore().recordType;
					var recordsDatGrid=NS.storeDatos.data.items;
					
						var divisa=NS.storeDivisa.getById(combo.getValue()).get('descDivisa');
					 	
				    	for(i=0;i<recordsDatGrid.length;i++)
							{
								if(recordsDatGrid[i].get('criterio')=='DIVISA')
								{
									indice=i;
									NS.storeDatos.remove(recordsDatGrid[indice]);
								}
							}
							
				    	var datos = new datosClase({
	                			criterio: 'DIVISA',
								valor: divisa
	            			});
	                   	NS.gridDatos.stopEditing();
	           			NS.storeDatos.insert(indice, datos);
	           			NS.gridDatos.getView().refresh();
	           			
						combo.setDisabled(true);
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				}
			}
		}
	});	//Termina cmbDivisa
	
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
		directFn: EnvioTransferenciasAction.llenarComboGral, 
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
					Ext.Msg.alert('SET','No Existen divisiones');
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
       	,x: 210
        ,y: 135
        ,width: 180
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
				
					var indice=0;
					var i=0;
					var datosClase = NS.gridDatos.getStore().recordType;
					var recordsDatGrid=NS.storeDatos.data.items;
					
					var division=NS.storeDivision.getById(combo.getValue()).get('descripcion');
				 	
			    	for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='DIVISION')
							{
								indice=i;
								NS.storeDatos.remove(recordsDatGrid[indice]);
							}
						}
						
			    	var datos = new datosClase({
                			criterio: 'DIVISION',
							valor: division
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

	//combo para agrupamientos
		NS.cmbAgrupa = new Ext.form.ComboBox({
		store: NS.storeEnvioTransGrid
		,name: PF+'cmbAgrupa'
		,id: PF+'cmbAgrupa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
       	,x: 10
        ,y: 470
        ,width: 180
		,valueField:'clave'
		,displayField:'clave'
		,autocomplete: true
		,emptyText: 'Agrupa'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
				
					var recordsDatGrid=NS.storeEnvioTransGrid.data.items;
					
					var clave=NS.storeEnvioTransGrid.getById(combo.getValue()).get('clave');
				 	
			    	for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('clave')==clave)
							{
								//alert(recordsDatGrid[i].get('noDocto'));
							}
						}
				}
			}
		}
	});
	
	//Store para llenar el grid de envio de transferencias
  	NS.storeEnvioTransGrid = new Ext.data.DirectStore({
  		paramsAsHash: false,
  	 	baseParams: {
			chkTodasEmpresas: false,
			chkSoloLocales: false,
			chkConvenioCie: false,
			chkMassPayment: false,
	        optTipoEnvio: 'InterBancario',//Este parametro es un String puede ser: interbancario, mismoBanco, etc.
	        optComerica: '',// poner ACH por defualt al visualizar el frame
	        optVenc: false,
	        pdMontoIni:'',
	        pdMontoFin:'',
	        psFechaValor:'',
	        psFecValOrig:'',
	        idBancoRec:'',
	        idBancoEmi:'',
	        idDivisa:'',
	        idDivision:'',
	        idUsuario: '' + NS.GI_USUARIO,
	        idEmpresa:'' + NS.GS_ID_EMPRESA,
	        cveControl:'',
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
					'pdMontoIni',
					'pdMontoFin',
					'psFechaValor',
					'psFecValOrig',
					'idBancoRec',
					'idBancoEmi',
					'idDivisa',
					'idDivision',
					'idUsuario',
					'idEmpresa',
					'cveControl',
					'nomina',
					'chkConvenioSant',
					'chkDebito'
			],
		directFn: EnvioTransferenciasAction.consultarPagos,
		fields: 
		[
			{name : 'noDocto'},
			//{name : 'fecOperacion'},
			{name : 'idBancoCityStr'},
			{name : 'beneficiario'},
			{name : 'importe'},
			{name : 'idDivisa'},
			{name : 'nombreBancoBenef'},
			{name : 'idChequeraBenef'},
			{name : 'idChequeraBenefReal'},
			{name : 'concepto'},
			{name : 'noFolioDet'},
			{name : 'noEmpresa'},
			{name : 'origenMov'},
			{name : 'idChequera'},
			//{name : 'fecValor'},
			{name : 'descBancoBenef'},
			{name : 'idBancoBenef'},
			{name : 'plazaBenef'},
			{name : 'plaza'},
			{name : 'bLayoutComerica'},
			{name : 'noCliente'},
			{name : 'idBanco'},
			{name : 'instFinan'},
			{name : 'sucOrigen'},
			{name : 'sucDestino'},
			{name : 'division'},
			{name : 'especiales'},
			{name : 'complemento'},
			{name : 'clabeBenef'},
			{name : 'rfcBenef'},
			{name : 'rfc'},
			{name : 'clave'},
			{name : 'horaRecibo'},
			{name : 'idContratoMass'},
			{name : 'tipoEnvioLayout'},
			{name : 'nomBancoIntermediario'},
			{name : 'aba'},
			{name : 'swiftCode'},
			{name : 'abaIntermediario'},
			{name : 'swiftIntermediario'},
			{name : 'abaCorresponsal'},
			{name : 'swiftCorresponsal'},
			{name : 'nomBancoCorresponsal'},
			{name : 'descUsuarioBital'},
			{name : 'descServicioBital'},
			{name : 'nomEmpresa'},
			{name : 'noFolioMov'},
			{name: 'fecValorStr'},
			{name: 'fecValorOriginalStr'},
			{name:'fecOperacionStr'},
		],
		listeners: {
			load: function(s, records){
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEnvioTransGrid, msg:"Buscando..."});
				
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información  SET','No se encontraron datos con los parametros de búsqueda');
				}
				else
				{			   		
          			var totalImporte=0;
          			NS.sm.selectRange(0,records.length-1);
          			var regSelec=NS.gridEnvioTransGrid.getSelectionModel().getSelections();
          			
          			for(var k=0;k<regSelec.length;k=k+1)
          			{
          				totalImporte=totalImporte+regSelec[k].get('importe');
          			}
					Ext.getCmp(PF+'txtTotalReg').setValue(records.length);
					Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));          			
				}
			}
		}
	}); 
	
		NS.gridEnvioTransGrid = new Ext.grid.GridPanel({
		id: PF+'gridEnvioTransGrid',
		name: PF+'gridEnvioTransGrid',
        store : NS.storeEnvioTransGrid,
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm,
               	{header: 'Factura', width: 50, dataIndex: 'noDocto'},
               	{header: 'Empresa', width: 180, dataIndex: 'nomEmpresa'},
				{header: 'Beneficiario', width: 160, dataIndex: 'beneficiario'},
				{header: 'Importe', width: 100, dataIndex: 'importe', css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
				{
					header: 'Divisa', 
					width: 50, 
					dataIndex: 'idDivisa'},
				{
					header: 'Banco Benef', 
					width: 120, 
					dataIndex: 'descBancoBenef'
				},
				{
					header: 'Chequera Benef', 
					width: 100, 
					dataIndex: 'idChequeraBenef'
				},{
					header: 'Clabe Benef', 
					width: 120, 
					dataIndex: 'clabeBenef'
				},
				{
					header: 'Concepto', 
					width: 200, 
					dataIndex: 'concepto'
				},
				{
					header: 'Fec Operacion', 
					width: 90, 
					dataIndex: 'fecOperacionStr',
					format: 'd/m/Y',
					editor: {
			            xtype : 'datefield',
			            //format: 'Y-m-d H:i:s',
			            format: 'd/m/Y',
			            submitFormat: 'c'
			        },
				},
				                 
				{
					header: 'Folio', 
					width: 50, 
					dataIndex: 'noFolioDet'
				},
				{
					header: 'Empresa', 
					width: 50, 
					dataIndex: 'noEmpresa', hidden: true},
				{
					header: 'Origen Mov', 
					width: 40, 
					dataIndex: 'origenMov', hidden: true
				},
				{
					header: 'Division', 
					width: 50, 
					dataIndex: 'division', hidden: true
				},
				{
					header: 'No Proveedor', 
					width: 80, 
					dataIndex: 'noCliente'
				},
				{	
					header: 'Especiales', 
					width: 100, 
					dataIndex: 'especiales', hidden: true
				},
				{
					header: 'Complemento', 
					width: 100, 
					dataIndex: 'complemento', hidden: true
				},
				{
					header: 'Sucursal Origen', 
					width: 100, 
					dataIndex: 'sucOrigen', hidden: true
				},
				{
					header: 'RFC Benef.', 
					width: 100, 
					dataIndex: 'rfcBenef', hidden: true
				},
				{
					header: 'RFC.', 
					width: 100, 
					dataIndex: 'rfc', hidden: true
				}
            ]
        }),
        sm: NS.sm,
        columnLines: true,
        x:0,
        y:0,
        width:960,
        height:200,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			var sum=0;
          			var regSelec=NS.gridEnvioTransGrid.getSelectionModel().getSelections();
          			for(var k=0;k<regSelec.length;k=k+1)
          			{
          				sum=sum+regSelec[k].get('importe');
          			}
          		 
          			Ext.getCmp(PF+'txtTotalReg').setValue(regSelec.length);
					Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((sum)*100)/100));   
        		}
        	}
        }
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
		
	//win h2h
		
		var winTipoEnvio = new Ext.Window({
			title: 'Tipo de envio',
			modal: true,
			shadow: true,
			//closable: false,
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
		            		 NS.ejecutar();
		            	 }
				     }					      
		    ],
		   	items: [NS.optTipoEnvioH2H],
	   	    listeners:{
	   	        	show:{
	   	        		fn:function(){
	   	        			winTipoEnvio.show();	   	        				
	   	        	    }
	   	        	}
	   	     }	
		});
		
		//combo para agrupamientos
		NS.cmbAgrupa = new Ext.form.ComboBox({
		store: NS.storeEnvioTransGrid
		,name: PF+'cmbAgrupa'
		,id: PF+'cmbAgrupa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
       	,x: 10
        ,y: 450
        ,width: 180
		,valueField:'clave'
		,displayField:'clave'
		,autocomplete: true
		,hidden: true
		,emptyText: '**Agrupa**'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
				
					var recordsDatGrid=NS.storeEnvioTransGrid.data.items;
					
					var clave=combo.getValue();
				 	
			    	for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('clave')!=clave)
							{
								//NS.gridEnvioTransGrid.store.sort('noDocto', 'ASC');
								//NS.gridEnvioTransGrid.getView().refresh();
							}
						}
				}
			}
		}
	});
			
	//Funcion Buscar
	NS.buscar = function(){
		
		var recordsDatosGrid=NS.storeDatos.data.items;
		
		if(recordsDatosGrid.length <= 0)
		{
			Ext.Msg.alert('Información SET','Debe agregar datos para la búsqueda');
			return;
		}
		//Ext.Msg.alert("SET","entro a buscar");
		if(Ext.getCmp( PF+'txtIdGrupo').getValue() == null || 
				Ext.getCmp( PF+'txtIdGrupo').getValue() == '' || 
				Ext.getCmp( PF+'txtIdGrupo').getValue() == undefined){
			Ext.Msg.alert('Información SET','Debe agregar un grupo de empresas para la búsqueda');
			return;
		}
		
		if(Ext.getCmp( PF+'txtCveControl').getValue() == null || 
				Ext.getCmp( PF+'txtCveControl').getValue().trim() == '' ||
				Ext.getCmp( PF+'txtCveControl').getValue() == undefined){
			NS.storeEnvioTransGrid.baseParams.cveControl = '0';
		}else{
			NS.storeEnvioTransGrid.baseParams.cveControl = ''+Ext.getCmp( PF+'txtCveControl').getValue();
		}
		
		for(var i=0;i<recordsDatosGrid.length;i=i+1){
			if(recordsDatosGrid[i].get('criterio')==='BANCO EMISOR'){
				var valorBancoE=recordsDatosGrid[i].get('valor');
				if(valorBancoE!=''){
					var recordsDataBancoE=NS.storeBancoEmisor.data.items;
					for(var j=0;j<recordsDataBancoE.length;j=j+1)
					{
						if(recordsDataBancoE[j].get('descripcion')===valorBancoE)
						{
							var idBancoE=recordsDataBancoE[j].get('id');
							 NS.storeEnvioTransGrid.baseParams.idBancoEmi = ''+idBancoE;
							 NS.idBancoEmi=idBancoE;
						}
					}
				}
				else{
					Ext.Msg.alert('Información SET','Debe agregar un valor a banco emisor');
					return;
				}
			}
			else if(recordsDatosGrid[i].get('criterio')==='BANCO RECEPTOR')
			{
				var valorBancoR=recordsDatosGrid[i].get('valor');
				if(valorBancoR!='')
				{
					var recordsDataBancoR=NS.storeBancoReceptor.data.items;
					for(var j=0;j<recordsDataBancoR.length;j=j+1)
					{
						if(recordsDataBancoR[j].get('descripcion')===valorBancoR)
						{
							var idBancoR=recordsDataBancoR[j].get('id');
							NS.storeEnvioTransGrid.baseParams.idBancoRec = ''+idBancoR;
							NS.idBancoRec = idBancoR;
							}
					}
				}
				else{
					Ext.Msg.alert('Información SET','Debe agregar un valor a banco receptor');
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
					NS.storeEnvioTransGrid.baseParams.pdMontoIni = NS.unformatNumber(''+ini);
					NS.storeEnvioTransGrid.baseParams.pdMontoFin = NS.unformatNumber(''+fin);
	 				NS.pdMontoIni = ini;
	 				NS.pdMontoFin = fin;
				}
				else{
					Ext.Msg.alert('Información SET','Debe agregar un valor a montos');
					return;
				}
			}
			else if(recordsDatosGrid[i].get('criterio')==='FECHA VALOR')
			{
				var valorFecha=recordsDatosGrid[i].get('valor');
				if(valorFecha!=''){
					NS.storeEnvioTransGrid.baseParams.psFechaValor = ''+valorFecha;
						NS.psFechaValor = valorFecha;
				}
				else{
					Ext.Msg.alert('Información SET','Debe agregar un valor a la fecha');
					return;
				}
			}
			else if(recordsDatosGrid[i].get('criterio')==='FEC. VALOR ORIGINAL')
			{
				var valorFecha=recordsDatosGrid[i].get('valor');
				if(valorFecha!=''){
					NS.storeEnvioTransGrid.baseParams.psFecValOrig = ''+valorFecha;
					NS.psFecValOrig = valorFecha;
				}
				else{
					Ext.Msg.alert('Información SET','Debe agregar un valor a la fecha');
					return;
				}
			}
			else if(recordsDatosGrid[i].get('criterio')==='DIVISA')
			{
				var valorDivisa=recordsDatosGrid[i].get('valor');
				if(valorDivisa!='')
				{
					var recordsDataDivisa=NS.storeDivisa.data.items;
					for(var j=0;j<recordsDataDivisa.length;j=j+1)
					{
						if(recordsDataDivisa[j].get('descDivisa')===valorDivisa)
						{
							var idDivisa=recordsDataDivisa[j].get('idDivisa');
							NS.storeEnvioTransGrid.baseParams.idDivisa = ''+idDivisa;
							NS.idDivisa = idDivisa;
						}
					}
					if(NS.idDivisa === 'DLS' && NS.idBancoEmi == 12 ){
						Ext.getCmp(PF+'opcionInterbancario').setVisible(false);
						//Ext.getCmp(PF+'opcionInternecional').setValue(true);
					}else{
						Ext.getCmp(PF+'opcionInterbancario').setVisible(true);
						//Ext.getCmp(PF+'opcionInternecional').setValue(false);
					}
					
				}
				else{
					Ext.Msg.alert('Información SET','Debe agregar un valor a la divisa');
					return;
				}
			}
		}
		
		//NS.storeEnvioTransGrid.baseParams.idEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		NS.storeEnvioTransGrid.baseParams.idEmpresa = parseInt(Ext.getCmp( PF+'txtIdGrupo').getValue()) != null &&
				Ext.getCmp( PF+'txtIdGrupo').getValue() != ''? parseInt(Ext.getCmp( PF+'txtIdGrupo').getValue()):0;
				NS.storeEnvioTransGrid.baseParams.nomina = Ext.getCmp(PF + 'chkNomina').getValue()
		NS.storeEnvioTransGrid.load();
		//NS.gridImportaBanca.getView().refresh();
		
	}
	
	NS.funActualizaCheq = function() {
		var recSelect = NS.gridEnvioTransGrid.getSelectionModel().getSelections();
		
		EnvioTransferenciasAction.actualizaMovtoCheqBenef(recSelect[0].get('noDocto'), parseInt(recSelect[0].get('noEmpresa')), 
				parseInt(recSelect[0].get('noCliente')), parseInt(recSelect[0].get('noFolioMov')), parseInt(NS.bancoBenef), 
				NS.chequeraBenef, function(res, e) {
			Ext.Msg.alert('SET', res);
			NS.winActualizaCheq.hide();
			NS.buscar();
		});
	};
	
	NS.funEliminar = function() {
		
	};
	/*Radio Button Digitem, TEF (Transferencia Electronica de Fondos), Citibank WorldLink,
	Mass Payment Citibank, Flat File Citibank (Solo DLS), Pay Link Citibank (Solo MN),
	Citibank ACH Credit */
	NS.optEnvioBNMX = new Ext.form.RadioGroup({
		id: PF + 'optEnvioBNMX',
		name: PF + 'optEnvioBNMX',
		x: 10,
		columns: 1, //muestra los radiobuttons en una columna
		items: [
	          {boxLabel: 'Bancanet', name: 'optBNM', inputValue: 0, disabled:false},  
	          {boxLabel: 'TEF (Transferencia Electronica de Fondos)', name: 'optBNM', inputValue: 1,disabled:true },
	          {boxLabel: 'Citibank WorldLink', name: 'optBNM', inputValue: 2, disabled: true},  
	          {boxLabel: 'Mass Payment Citibank', name: 'optBNM', hidden: true, inputValue: 3, disabled: true},
	          {boxLabel: 'Flat File Citibank (Solo DLS)', name: 'optBNM', inputValue: 4, disabled: true},  
	          {boxLabel: 'Pay Link Citibank (Solo MN)', name: 'optBNM', inputValue: 5, disabled: false,checked: true},
	          {boxLabel: 'Citibank ACH Credit', name: 'optBNM', inputValue: 6, disabled: true},
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
	
	
	NS.optEnvioBBVA = new Ext.form.RadioGroup({
		id: PF + 'optEnvioBBVA',
		name: PF + 'optEnvioBBVA',
		x: 10,
		columns: 1, //muestra los radiobuttons en una columna
		items: [
	      
	          {boxLabel: 'H2H Bancomer (Net cash)', name: 'optBBVA', inputValue: 1, disabled: false},
	          {boxLabel: 'Tradicional', name: 'optBBVA', inputValue: 2, disabled: false}
	     ]
	});

	
	
	
	//Radio Button Conexion empresarial y host to host HSBC
	NS.optEnvioHSBC = new Ext.form.RadioGroup({
		id: PF + 'optEnvioHSBC',
		name: PF + 'optEnvioHSBC',
		x: 60,
		y: 30,
		columns: 1, //muestra los radiobuttons en una columna
		items: [
	          {boxLabel: 'Conexion Empresarial', name: 'optHSBC', inputValue: 0, checked: true}/*,  
	          {boxLabel: 'Host to Host', name: 'optHSBC', inputValue: 1}*/
	     ]
	});
	
	NS.contEnvioBancos = new Ext.form.FieldSet({
		title: 'Tipos de Envio Bancos',
		id: PF + 'contEnvioBancos',
		width: 300,
		height: 240,
		x: 350,
		y: 120,
		layout: 'absolute',
		plain: false,
		bodyStyle: 'background:#89ACB9',
		hidden: true,
		items: [
		        
			NS.optEnvioBNMX,
			NS.optEnvioBBVA,
		    NS.optEnvioHSBC,
		        
		        {
                    xtype: 'button',
                    text: 'Continuar',
                    x: 50,
                    y: 170,
                    width: 80,
                    listeners: {
		        		click: {
		        			fn:function(e) {
		        				NS.ejecutar();
		        			}
		        		}
		        	}
		        },{
                    xtype: 'button',
                    text: 'Cancelar',
                    x: 150,
                    y: 170,
                    width: 80,
                    listeners: {
		        		click: {
		        			fn:function(e) {
		        				Ext.getCmp(PF + 'contEnvioBancos').setVisible(false);
		        			}
		        		}
		        	}
		        }
		        ]
	});
	
	NS.labProveedor = new Ext.form.Label ({
		text: 'Proveedor',
		x: 10
	});
	
	NS.txtProveedor = new Ext.form.TextField({
		id: PF + 'txtProveedor',
        name:PF + 'txtProveedor',
		x: 80,
        width: 195,
        readOnly: true
    });
	
	NS.txtIdGrupo = new Ext.form.TextField({
		name: PF+'txtIdGrupo',
        id: PF+'txtIdGrupo',
       // x: 10,
       // y: 165,
        x: 10,
        y: 15,
        width: 50,
        hidden: false, //true
        listeners: {
        	change: {
        		fn:function(caja) {
        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
            			BFwrk.Util.updateTextFieldToCombo(PF+'txtIdGrupo',NS.cmbGrupo.getId());
            			if (caja.getValue() == 0)
            				Ext.getCmp(PF+'chkTodos').setValue(true);
            			else if (NS.cmbGrupo.getValue() == '')
            				Ext.getCmp(PF+'txtIdGrupo').setValue('');
    	   			}else
    	   				NS.cmbGrupo.reset();
        		}
        	}
        }
	});
    

	NS.labDivisa = new Ext.form.Label ({
		text: 'Divisa',
		x: 10,
		y: 35
	});
	
	NS.txtDivisa = new Ext.form.TextField({
		id: PF + 'txtDivisa',
        name:PF + 'txtDivisa',
		x: 80,
		y: 35,
        width: 40,
        readOnly: true
    });
	
	NS.labBanco = new Ext.form.Label ({
		text: 'Banco',
		x: 10,
		y: 70
		
	});
	
	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
        name:PF + 'txtBanco',
		x: 80,
		y: 70,
        width: 150,
        readOnly: true
    });
	
	NS.labChequera = new Ext.form.Label ({
		text: 'Chequera',
		x: 10,
		y: 105
	});
	
	NS.txtChequera = new Ext.form.TextField({
		id: PF + 'txtChequera',
        name:PF + 'txtChequera',
		x: 80,
		y: 105,
        width: 150,
        readOnly: true
    });
	
	NS.labClabe = new Ext.form.Label ({
		text: 'Clabe',
		x: 10,
		y: 140
	});
	
	NS.txtClabe = new Ext.form.TextField({
		id: PF + 'txtClabe',
        name:PF + 'txtClabe',
		x: 80,
		y: 140,
        width: 195,
        readOnly: true
    });
	
	NS.labNvoBanco = new Ext.form.Label ({
		text: 'Banco',
		x: 10
	});
	
	NS.txtNvoBanco = new Ext.form.TextField({
		id: PF + 'txtNvoBanco',
        name:PF + 'txtNvoBanco',
		x: 80,
        width: 40,
        tabIndex: 0
    });
	
	NS.storeNvoBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['noPersona'],
		directFn: EnvioTransferenciasAction.obtenerBancosBenef, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeNvoBanco, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No tiene bancos asignados el proveedor');
			}
		}
	}); 
	
	NS.cmbNvoBanco = new Ext.form.ComboBox({
		store: NS.storeNvoBanco,
		id: PF + 'cmbNvoBanco',
		x: 125,
        width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'id',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un banco',
		triggerAction: 'all',
		value: '',
		minChars: 0,
		listeners: {
			select: {
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtNvoBanco', NS.cmbNvoBanco.getId());
					NS.bancoBenef = combo.getValue();
					
					NS.storeNvaChequera.baseParams.idBanco = combo.getValue();
					NS.storeNvaChequera.load();
				}
			}
		}
	});
	
	NS.labNvaChequera = new Ext.form.Label ({
		text: 'Chequera',
		x: 10,
		y: 35
	});
	
	NS.storeNvaChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['noPersona', 'idBanco'],
		directFn: EnvioTransferenciasAction.obtenerChequerasBenef, 
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeNvaChequera, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No tiene chequeras asignadas el proveedor');
			}
		}
	}); 
	
	NS.cmbNvaChequera = new Ext.form.ComboBox({
		store: NS.storeNvaChequera,
		id: PF + 'cmbNvaChequera',
		name: PF + 'cmbNvaChequera',
		x: 80,
        y: 35,
        width: 195,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 2,
		valueField: 'idStr',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una chequera',
		triggerAction: 'all',
		value: '',
		visible: false,
		minChars: 0,
		listeners: {
			select: {
				fn:function(combo, valor) {
					NS.chequeraBenef = combo.getValue();
				}
			}
		}
	});
	
	NS.contDatosActuales = new Ext.form.FieldSet({
		title: 'Datos Actuales',
		id: PF + 'contDatosActuales',
		width: 300,
		height: 200,
		x: 10,
		y: 10,
		layout: 'absolute',
		plain: false,
		items: [
		        NS.labProveedor,	NS.txtProveedor,
		        NS.labDivisa,		NS.txtDivisa,
			    NS.labBanco,		NS.txtBanco,
		        NS.labChequera,		NS.txtChequera,
		        NS.labClabe,		NS.txtClabe
		        ]
	});
	
	NS.contCheqProveedor = new Ext.form.FieldSet({
		title: 'Chequeras del Proveedor',
		id: PF + 'contCheqProveedor',
		width: 300,
		height: 100,
		y: 220,
		x: 10,
		layout: 'absolute',
		plain: false,
		items: [
		        NS.labNvoBanco,		NS.txtNvoBanco,		NS.cmbNvoBanco,
		        NS.labNvaChequera,	NS.cmbNvaChequera
		        ]
	});
	
	NS.limpiaCampos = function() {
		Ext.getCmp(PF + 'txtProveedor').setValue('');
		Ext.getCmp(PF + 'txtDivisa').setValue('');
		Ext.getCmp(PF + 'txtBanco').setValue('');
		Ext.getCmp(PF + 'txtChequera').setValue('');
		Ext.getCmp(PF + 'txtClabe').setValue('');
		
		Ext.getCmp(PF + 'txtNvoBanco').setValue('');
		NS.storeNvoBanco.removeAll();
		NS.cmbNvoBanco.reset();
		NS.storeNvaChequera.removeAll();
		NS.cmbNvaChequera.reset();
	};
	
	NS.winActualizaCheq = new Ext.Window({
    	title: 'Actualiza Chequera',
		modal: true,
		shadow: true,
		width: 350,
	   	height: 400,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    autoScroll: true,
	    hidden: true,
	    id: PF + 'winActualizaCheq',
	    name: PF + 'winActualizaCheq',
	    closable: false,
	    items : [
	             	NS.contDatosActuales,
	                NS.contCheqProveedor,
	                {
	                      xtype: 'button',
	                      text: 'Aceptar',
	                      x: 100,
	                      y: 330,
	                      width: 80,
	                      tabIndex: 3,
	                      listeners: {
	  		        		click: {
	  		        			fn:function(e) {
	                				NS.funActualizaCheq();
	  		        			}
	  		        		}
	  		        	}
	  		        },{
	                      xtype: 'button',
	                      text: 'Cancelar',
	                      x: 200,
	                      y: 330,
	                      width: 80,
	                      tabIndex: 4,
	                      listeners: {
	  		        		click: {
	  		        			fn:function(e) {
	  		        				NS.winActualizaCheq.hide();
	  		        			}
	  		        		}
	  		        	}
	  		        }
	             ]
    });
    
		NS.EnvioTransferencias = new Ext.form.FormPanel({
		    title: 'Envio de Transferencias BE.',
		    width: 810,
		    height: 656,
    		frame: true,
		    autoScroll: true,
		    padding: 10,
		    layout: 'absolute',
		    id: PF+'contEnvioTranferencias',
		    name: PF+'contEnvioTranferencias',
		    renderTo: NS.tabContId,
		        items : [
		            {
		                xtype: 'fieldset',
		                title: '',
		                width: 1010,
		                height: 490,
		                x: 20,
		                y: 5,
		                layout: 'absolute',
		                id: 'cont',
		                items: [
		                        NS.cmbAgrupa,
		                    
		                    {
		                        xtype: 'fieldset',
		                        title: '',
		                        x: 0,
		                        y: 205,
		                        width: 985,
		                        height: 260,
		                        layout: 'absolute',
		                        id: 'contGrid',
		                        items: [
		                        NS.gridEnvioTransGrid,
		                            {
		                                xtype: 'textfield',
		                                x: 0,
		                                y: 215,
		                                width: 70,
		                                id: PF+'txtTotalReg',
		                                id: PF+'txtTotalReg',
		                                readOnly: true
		                            },
		                            {
		                                xtype: 'textfield',
		                                x: 90,
		                                y: 215,
		                                width: 120,
		                                name: PF+'txtMontoTotal',
		                                id: PF+'txtMontoTotal',
		                                readOnly: true
		                            },
		                            {
		                                xtype: 'label',
		                                text: 'Monto Total:',
		                                x: 90,
		                                y: 200
		                            },
		                            {
		                                xtype: 'label',
		                                text: 'T. Registros:',
		                                x: 0,
		                                y: 200
		                            },
		                            {
				                        xtype: 'button',
				                        text: 'Ejecutar',
				                        x: 767,
		                                y: 215,
				                        width: 80,
				                        id: PF+'btnEjecutar',
				                        name: PF+'btnEjecutar',
				                        listeners: {
			                        		click: {
						                        fn:function(e){
						                        	
				                    				var registrosSelec = NS.gridEnvioTransGrid.getSelectionModel().getSelections();
								                	
				                    				if (registrosSelec.length == 0) {
					                					Ext.Msg.alert('Información SET','Es necesario seleccionar algun registro');
					                					return;
					                				}
				                    				//Ext.Msg.alert("set",NS.idBancoEmi);
					                				if (NS.idBancoEmi == 2 || NS.idBancoEmi == 1026) {
					                					Ext.getCmp(PF + 'contEnvioBancos').setVisible(true);
					                					Ext.getCmp(PF + 'contEnvioBancos').setTitle('Tipos de Envio Banamex');
					                					Ext.getCmp(PF + 'optEnvioHSBC').setVisible(false);
					                					Ext.getCmp(PF + 'optEnvioBNMX').setVisible(true);
					                					Ext.getCmp(PF + 'optEnvioBBVA').setVisible(false);
					                				} else if (NS.idBancoEmi == 21) {
					                					Ext.getCmp(PF + 'contEnvioBancos').setVisible(true);
					                					Ext.getCmp(PF + 'contEnvioBancos').setTitle('Tipos de Envio HSBC');
					                					Ext.getCmp(PF + 'optEnvioBNMX').setVisible(false);
					                					Ext.getCmp(PF + 'optEnvioHSBC').setVisible(true);
					                					Ext.getCmp(PF + 'optEnvioBBVA').setVisible(false);
					                				} else if (NS.idBancoEmi == 12) {
					                					Ext.getCmp(PF + 'contEnvioBancos').setVisible(true);
					                					Ext.getCmp(PF + 'contEnvioBancos').setTitle('Tipos de Envio Bancomer');
					                					Ext.getCmp(PF + 'optEnvioBBVA').setVisible(true);
					                					Ext.getCmp(PF + 'optEnvioBNMX').setVisible(false);
					                					Ext.getCmp(PF + 'optEnvioHSBC').setVisible(false);
					                				} else if (NS.idBancoEmi == 14 && NS.chkDebito == 'false'){//&& NS.optTipoEnvio == "Interbancario" && NS.optTipoEnvio == "Mismo"
					                					winTipoEnvio.show();
					                				} else
					                					NS.ejecutar();
					                			}
				                    		}
				                    	}
				                    },
				                    {
				                        xtype: 'button',
				                        text: 'Limpiar',
				                        x: 667,
		                                y: 215,
				                        width: 80,
				                        id: PF+'btnLimpiar',
				                        listeners:{
			                        			click:{
						                        	fn:function(e){
						                        	NS.limpiar();
						                        	var datosClase = NS.gridDatos.getStore().recordType;
													var recordsDatGrid=NS.storeDatos.data.items;
													
													//cargar el primer parametro del grid banco emisor
												 	var datosClase = NS.gridDatos.getStore().recordType;
											    	var datos = new datosClase({
										                	criterio: 'BANCO EMISOR',
															valor: ''
										            	});
									                    NS.gridDatos.stopEditing();
									            		NS.storeDatos.insert(0, datos);
									            		NS.gridDatos.getView().refresh();
									            		NS.cmbEmpresas.setValue('');
						                        }
						                    }
						                }
				                    },
		                            {
		                                xtype: 'button',
		                                text: 'Actualiza Chequera',
		                                x: 767,
		                                y: 215,
		                                id: PF + 'btnActualizaChe',
		                                hidden: true,
		                                listeners:{
	                        				click:{
				                        		fn:function(e){
		                            				var recSelect = NS.gridEnvioTransGrid.getSelectionModel().getSelections();
		                            				
		                            				if(recSelect.length == 0) {
		                            					Ext.Msg.alert('SET', 'Debe seleccionar un registro!!');
		                            					return;
		                            				}
		                            				NS.winActualizaCheq.show();
		                            				
		                            				NS.limpiaCampos();
		                            				
		                            				Ext.getCmp(PF + 'txtProveedor').setValue(recSelect[0].get('beneficiario'));
		                            				Ext.getCmp(PF + 'txtDivisa').setValue(recSelect[0].get('idDivisa'));
		                            				Ext.getCmp(PF + 'txtBanco').setValue(recSelect[0].get('descBancoBenef'));
		                            				Ext.getCmp(PF + 'txtChequera').setValue(recSelect[0].get('idChequeraBenef'));
		                            				Ext.getCmp(PF + 'txtClabe').setValue(recSelect[0].get('clabeBenef'));
		                            				
		                            				NS.storeNvoBanco.baseParams.noPersona = parseInt(recSelect[0].get('noCliente'));
		                            				NS.storeNvaChequera.baseParams.noPersona = parseInt(recSelect[0].get('noCliente'));
		                            				NS.storeNvoBanco.load();
		                            				
			                        			}
				                        	}
				                        }
		                            },
		                            {
		                                xtype: 'button',
		                                text: 'Eliminar',
		                                x: 880,
		                                y: 215,
		                                width: 80,
		                                hidden: true,
		                                id: PF + 'btnEliminar',
		                                listeners:{
	                        				click:{
				                        		fn:function(e){
		                            				NS.funEliminar();
			                        			}
				                        	}
				                        }
		                            }
		                        ]
		                    },
		                    {
		                        xtype: 'fieldset',
		                        title: 'Búsqueda',
		                        x: 0,
		                        y: 0,
		                        layout: 'absolute',
		                        width: 985,
		                        height: 195,
		                        id: 'conBusqueda',
		                        items: [
		                            {
		                                xtype: 'checkbox',
		                                x: 890,
		                                y: 60,
		                                boxLabel: 'H2H',
		                                id: PF + 'chkH2H',
		                                name: PF + 'chkH2H',
		                                hidden: true
		                            },
		                            NS.labEmpresa,
		                            NS.labPropuesta,
		                            NS.txtEmpresa,
		                            NS.cmbEmpresas,
		                            NS.cmbGrupo,
		                            NS.txtIdGrupo,
		                           	NS.cmbBancoEmisor,
		                           	NS.txtCveControl,
		                            NS.cmbCriterio,
		                            NS.cmbDivision,
		                            {    xtype: 'checkbox',
		                        		x: 530,
		                        		y: 15,
		                                //width: 320
		                                boxLabel: 'Nomina',
		                                tabIndex: 1,
		                                id: PF + 'chkNomina',
		                                name: PF + 'chkNomina',
		                                hidden: true
		                            },
			                        {xtype: 'checkbox',
			                        	x: 400,
			                        	y: 15,
			                            //width: 320
			                            boxLabel: 'Todos los grupos',
			                            tabIndex: 1,
			                            id: PF + 'chkTodos',
			                            name: PF + 'chkTodos',
	                                    listeners:{
	                                   		check:{
	                                   			fn:function(opt, valor)
	                                   			{
	                                   				if(valor==true)
	                                   				{
	                                   					NS.chkTodasEmpresas = 'true';
	                                   					NS.cmbGrupo.setDisabled(true);
	                                   					NS.cmbGrupo.setValue("**********Todos los Grupos de Empresa**********");
	                                   					Ext.getCmp(PF+'txtIdGrupo').setValue('0');
	                                   					Ext.getCmp(PF+'txtIdGrupo').setDisabled(true);
													}
													else{ 
														NS.chkTodasEmpresas = 'false';
	                                   					NS.cmbGrupo.setDisabled(false);
	                                   					Ext.getCmp(PF+'txtIdGrupo').setDisabled(false);
	                                   					NS.cmbGrupo.setValue('');
	                                   					Ext.getCmp(PF+'txtIdGrupo').setValue('');
	                                   				}
	                                   			}
	                                   		}
	                                   	}
		                            },
		                            
		                            
		                            {
		                                xtype: 'button',
		                                text: 'Buscar',
		                                x: 880,
		                                y: 15,
		                                width: 80,
		                                height: 22,
		                                id: PF+'btnBuscar',
		                                name: PF+'btnBuscar',
		                                listeners:{
	                        				click:{
				                        		fn:function(e){
				                        			NS.buscar();
			                        			}
				                        	}
				                        }
		                            },		                           
		                            {
		                                xtype: 'label',
		                                text: 'Banco emisor:',
		                                x: 10,
		                                y: 80
		                            },
		                            {
		                                xtype: 'label',
		                                text: 'Criterio:',
		                                x: 10,
		                                y: 120
		                            },
		                            /*{
		                                xtype: 'textfield',
		                                x: 60,
		                                y: 0,
		                                width: 210,
		                                tabIndex: 0,
		                                value:NS.GS_DESC_EMPRESA,
		                                name: PF+'txtEmpresa',
		                                id: PF+'txtEmpresa',
		                                readOnly : true
		                            },
		                            {
		                                xtype: 'label',
		                                text: 'Empresa:',
		                                x: 0,
		                                y: 0
		                            },*/
	                            	{
				                   	   xtype: 'numberfield',
				                   	   id:PF+'monto1',
				                   	   name:PF+'monto1',
				                       x: 210,
				                       y: 135,
				                       width: 80,
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
													
													//Ext.getCmp('cmbCriterio').setDisabled(false);
													
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
															valor: NS.formatNumber(valor)
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
				                   		id:PF+'monto2',
				                   		name:PF+'monto2',
				                   		x:300,
				                   		y:135,
				                   		width: 80,
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
													var recordsDatGrid = NS.storeDatos.data.items;		
													var valorUno = NS.formatNumber(Ext.getCmp(PF+'monto1').getValue());
													if(valorUno!='')
														valorUno+=' a '
													//Ext.getCmp('cmbCriterio').setDisabled(false);
													
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
															valor: valorUno+NS.formatNumber(valor)
								           			});
								                    NS.gridDatos.stopEditing();
								           			NS.storeDatos.insert(indice, datos);
								           			NS.gridDatos.getView().refresh();
								           			
				                   					if(valor!='')
				                   					{
				                   						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				                   						Ext.getCmp(PF+'monto1').setDisabled(true);
				                   						caja.setDisabled(true);
				                   					}
				                   				}
				                   			}
				                   		}
				                   	},
				                   	NS.cmbBancoReceptor,
				                   	NS.cmbDivisa,
			                   	 	{
				                   		xtype:'datefield',
				                   		format:'d/m/Y',
				                   		id:PF+'fechaValor',
				                   		name:PF+'fechaValor',
			                   		 	x: 210,
		                                y: 135,
		                                width: 100,
				                   		hidden:true,
				                   		allowBlank: false,
				                   		blankText:'La fecha es requerida',
				                   		listeners:{
				                   			change:{
				                   				fn:function(caja,valor){
				                   					var indice=0;
													var i=0;
													var datosClase = NS.gridDatos.getStore().recordType;
													var recordsDatGrid=NS.storeDatos.data.items;				
													for(i=0;i<recordsDatGrid.length;i++)
													{
														if(recordsDatGrid[i].get('criterio')=='FECHA VALOR')
														{
															indice=i;
															NS.storeDatos.remove(recordsDatGrid[indice]);
														}
													}
													var valorFecha='';
													if(valor!='')
													{
														valorFecha=NS.cambiarFecha(''+valor);
													}
											    	var datos = new datosClase({
								                			criterio: 'FECHA VALOR',
															valor: valorFecha
								           			});
								                    NS.gridDatos.stopEditing();
								           			NS.storeDatos.insert(indice, datos);
								           			NS.gridDatos.getView().refresh();
								           			
								           			caja.setDisabled(true);
								           			Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				                   				}
				                   			}
				                   		}
				                   	},{
				                   		xtype:'datefield',
				                   		format:'d/m/Y',
				                   		id:PF+'fecValorOriginal',
				                   		name:PF+'fecValorOriginal',
			                   		 	x: 210,
		                                y: 135,
		                                width: 100,
				                   		hidden:true,
				                   		allowBlank: false,
				                   		blankText:'La fecha es requerida',
				                   		listeners:{
				                   			change:{
				                   				fn:function(caja,valor){
				                   					var indice=0;
													var i=0;
													var datosClase = NS.gridDatos.getStore().recordType;
													var recordsDatGrid=NS.storeDatos.data.items;				
													for(i=0;i<recordsDatGrid.length;i++)
													{
														if(recordsDatGrid[i].get('criterio')=='FEC. VALOR ORIGINAL')
														{
															indice=i;
															NS.storeDatos.remove(recordsDatGrid[indice]);
														}
													}
													var valorFecha='';
													if(valor!='')
													{
														valorFecha=NS.cambiarFecha(''+valor);
													}
											    	var datos = new datosClase({
								                			criterio: 'FEC. VALOR ORIGINAL',
															valor: valorFecha
								           			});
								                    NS.gridDatos.stopEditing();
								           			NS.storeDatos.insert(indice, datos);
								           			NS.gridDatos.getView().refresh();
								           			
								           			caja.setDisabled(true);
								           			Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				                   				}
				                   			}
				                   		}
				                   	},
		                            {
		                                xtype: 'label',
		                                text: 'Valor:',
		                                x: 210,
		                                y: 120
		                            },
		                            {
		                                xtype: 'checkbox',
		                                x: 530,
		                                y: 0,
		                                boxLabel: 'Convenio CIE',
		                                name: PF+'chkConvenio',
		                                id: PF+'chkConvenio',
		                                hidden: true,
	                                 	listeners:{
                                       		check:{
                                       			fn:function(opt, valor)
                                       			{
                                       				if(valor==true)
                                       				{
                                       					NS.storeEnvioTransGrid.baseParams.chkConvenioCie= true;
                                       					NS.chkConvenioCie= 'true';
                                       				}
                                       				else
                                       				{
                                       					NS.storeEnvioTransGrid.baseParams.chkConvenioCie= false;
                                       					NS.chkConvenioCie= 'false';
                                       				}
                                       			}
                                       		}
                                       	}
		                            },
		                            {
		                                xtype: 'checkbox',
		                                x: 530,
		                                y: 0,
		                                boxLabel: 'Convenio Santander',
		                                name: PF+'chkConvenioSant',
		                                id: PF+'chkConvenioSant',
		                                hidden: true,
	                                 	listeners: {
                                       		check: {
                                       			fn:function(opt, valor) {
                                       				if(valor == true) {
                                       					NS.storeEnvioTransGrid.baseParams.chkConvenioSant = true;
                                       					NS.chkConvenioSant = 'true';
                                       				} else {
                                       					NS.storeEnvioTransGrid.baseParams.chkConvenioSant= false;
                                       					NS.chkConvenioSant = 'false';
                                       				}
                                       			}
                                       		}
                                       	}
		                            },
		                            {
		                                xtype: 'checkbox',
		                                x: 680,
		                                y: 0,
		                                boxLabel: 'Pago de tarjeta de debito',
		                                name: PF+'chkDebito',
		                                id: PF+'chkDebito',
		                                hidden: true,
	                                 	listeners: {
                                       		check: {
                                       			fn:function(opt, valor) {
                                       				if(valor == true) {
                                       					NS.storeEnvioTransGrid.baseParams.chkDebito = true;
                                       					NS.chkDebito = 'true';
                                       				} else {
                                       					NS.storeEnvioTransGrid.baseParams.chkDebito= false;
                                       					NS.chkDebito = 'false';
                                       				}
                                       			}
                                       		}
                                       	}
		                            },
		                            {
		                                xtype: 'checkbox',
		                                x: 530,
		                                y: 0,
		                                boxLabel: 'H2H Santander',
		                                name: PF+'chkH2HSantander',
		                                id: PF+'chkH2HSantander',
		                                hidden: true,
		                                checked: true,
	                                 	listeners: {
                                       		check: {
                                       			fn:function(opt, valor) {
                                       				if(valor == true) {
//                                       					NS.storeEnvioTransGrid.baseParams.chkH2HSantander = true;
                                       					NS.chkH2HSantander = 'true';
                                       				}else {
//                                       					NS.storeEnvioTransGrid.baseParams.chkH2HSantander = false;
                                       					NS.chkH2HSantander = 'false';
                                       				}
                                       			}
                                       		}
                                       	}
		                            },
		                            {
		                                xtype: 'checkbox',
		                                x: 420,
		                                y: 0,
		                                boxLabel: 'Solo locales',
		                                name: 'chkLocales',
		                                tabIndex: 2,
		                                id: 'chkLocales',
		                                name: PF+'',
		                                hidden: true,
	                                 	listeners:{
                                       		check:{
                                       			fn:function(opt, valor)
                                       			{
                                       				if(valor==true)
                                       				{
                                       					NS.storeEnvioTransGrid.baseParams.chkSoloLocales = true;
                                       					NS.chkSoloLocales = 'true';
                                       				}
                                       				else
                                       				{
                                       					NS.storeEnvioTransGrid.baseParams.chkSoloLocales = false;
                                       					NS.chkSoloLocales = 'false';
                                       				}
                                       			}
                                       		}
                                       	}
		                            },
		                            {
		                                xtype: 'checkbox',
		                                x: 635,
		                                y: 0,
		                                boxLabel: 'Mass Payment',
		                                id: PF+'chkMammP',
		                                name: PF+'chkMammP',
		                                hidden: true,
	                                 	listeners:{
                                       		check:{
                                       			fn:function(opt, valor)
                                       			{
                                       				if(valor==true)
                                       				{
                                       					NS.storeEnvioTransGrid.baseParams.chkMassPayment = true;
                                       					NS.chkMassPayment = 'true';
                                       				//	Ext.Msg.alert("set","entro a massPay");
                                       				}
                                       				else
                                       				{
                                       					NS.storeEnvioTransGrid.baseParams.chkMassPayment = false;
                                       					NS.chkMassPayment = 'false';
                                       				}
                                       			}
                                       		}
                                       	}
		                            },
                                    {
				                        xtype: 'fieldset',
		                                x: 720,
		                                y: 30,
		                                width: 125,
		                                height: 110,
		                                layout: 'absolute',
		                                id: PF+'contInterba',
				                        name: PF+'contInterba',
				                        items: [new Ext.form.RadioGroup(
				                        			{
				                                    vertical: true,
				                                    id:PF+'optInterBan',
				                                    name:PF+'optInterBan',
				                                    columns:1,									                           
				                                    items: [
				                                            {
				                                             boxLabel: 'Interbancario', 
				                                             name:PF+'id-1',
				                                             id: PF+'opcionInterbancario',
				                                             inputValue: 1,
				                                             checked:true,
				                                             listeners:{
					                                           		check:{
					                                           			fn:function(opt, valor)
					                                           			{
					                                           				if(valor==true)
					                                           				{
					                                           					NS.storeEnvioTransGrid.baseParams.optTipoEnvio = 'Interbancario';
					                                           					NS.optTipoEnvio = 'Interbancario';
																			//Ext.Msg.alert("set","interbancario");
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setVisible(false);
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setValue(false);
				                                    							Ext.getCmp(PF + 'chkDebito').setVisible(false);
				                                    							Ext.getCmp(PF + 'chkDebito').setValue(false);
					                                           					if(NS.cveBanco == 14){
						                                           						/*Ext.getCmp(PF+'chkH2HSantander').setVisible(true);
						                                           						NS.chkH2HSantander = 'true';
						                                           						Ext.getCmp(PF+'chkH2HSantander').setValue(true);*/
					                                           					}
						                                           				else {
						                                           					Ext.getCmp(PF+'chkConvenio').setVisible(false);
						                                           					Ext.getCmp(PF+'chkConvenio').setValue(false);
					                                           						NS.storeEnvioTransGrid.baseParams.chkConvenioCie= false;
							                                       					NS.chkConvenioCie= 'false';
							                                       					NS.chkH2HSantander = 'false';
							                                       					
					                                           					}
					                                           					NS.buscar();
					                                           				}
					                                           				
					                                           			}
					                                           		}
					                                           	}
				                                            },{
				                                             boxLabel: 'Mismo Banco',
				                                             name: PF+'id-1',
				                                             inputValue: 2,
				                                             listeners:{
				                                           		check:{
				                                           			fn:function(opt, valor)
				                                           			{
				                                           				if(valor==true)
				                                           				{
				                                           					NS.storeEnvioTransGrid.baseParams.optTipoEnvio = 'Mismo';
				                                           					NS.optTipoEnvio = 'Mismo';

				                                           					if(NS.cveBanco==12)
				                                           						Ext.getCmp(PF+'chkConvenio').setVisible(true);
				                                           						
				                                           					else if(NS.cveBanco == 14){
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setVisible(true);
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setValue(false);
				                                    							Ext.getCmp(PF + 'chkDebito').setVisible(true);
				                                    							Ext.getCmp(PF + 'chkDebito').setValue(false);
					                                           						/*Ext.getCmp(PF+'chkH2HSantander').setVisible(true);
					                                           						NS.chkH2HSantander = 'true';
					                                           						Ext.getCmp(PF+'chkH2HSantander').setValue(true);*/
				                                           					}
					                                           				else {
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setVisible(false);
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setValue(false);
				                                    							Ext.getCmp(PF + 'chkDebito').setVisible(false);
				                                    							Ext.getCmp(PF + 'chkDebito').setValue(false);
				                                           						NS.storeEnvioTransGrid.baseParams.chkConvenioCie= false;
						                                       					NS.chkConvenioCie= 'false';
						                                       					NS.chkH2HSantander = 'false';
				                                           					}
				                                           					NS.buscar();
				                                           				}
				                                           				/*else{
				                                           					NS.storeEnvioTransGrid.baseParams.chkConvenioCie= false;
					                                       					NS.chkConvenioCie= 'false';
				                                           					Ext.getCmp(PF+'chkConvenio').setVisible(false);
				                                           					Ext.getCmp(PF+'chkConvenio').setValue(false);
				                                           					Ext.getCmp(PF+'chkH2HSantander').setVisible(false);
				                                           					Ext.getCmp(PF+'chkH2HSantander').setValue(false);*/
				                                           				
				                                           			}
				                                           		}
				                                           	}
				                                            },{
					                                             boxLabel: 'Internacional', 
					                                             name: PF+'id-1',
					                                             id: PF+'opcionInternecional',
					                                             inputValue: 3, 
					                                             hidden:false,
					                                             listeners:{
					                                           		check:{
					                                           			fn:function(opt, valor)
					                                           			{
					                                           				if(valor==true)
					                                           				{
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setVisible(false);
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setValue(false);
					                                           					NS.storeEnvioTransGrid.baseParams.optTipoEnvio = 'Internacional';
					                                           					NS.optTipoEnvio = 'Internacional';
					                                           					NS.storeEnvioTransGrid.baseParams.chkConvenioCie= false;
						                                       					NS.chkConvenioCie= 'false';
					                                           					Ext.getCmp(PF+'chkConvenio').setVisible(false);
					                                           					Ext.getCmp(PF+'chkConvenio').setValue(false);
					                                           					Ext.getCmp(PF+'chkH2HSantander').setVisible(false);
					                                           					Ext.getCmp(PF+'chkH2HSantander').setValue(false);
					                                           				}
					                                           			}
					                                           		}
					                                           	}
				                                            },{
					                                             boxLabel: 'SPEUA', 
					                                             name: PF+'id-1',
					                                             inputValue: 4, 
					                                             listeners:{
					                                           		check:{
					                                           			fn:function(opt, valor)
					                                           			{
					                                           				if(valor==true)
					                                           				{
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setVisible(false);
				                                    							Ext.getCmp(PF + 'chkConvenioSant').setValue(false);
					                                           					NS.storeEnvioTransGrid.baseParams.optTipoEnvio = 'SPEUA';
					                                           					NS.optTipoEnvio = 'SPEUA';
					                                           					NS.storeEnvioTransGrid.baseParams.chkConvenioCie= false;
						                                       					NS.chkConvenioCie= 'false';
					                                           					Ext.getCmp(PF+'chkConvenio').setVisible(false);
					                                           					Ext.getCmp(PF+'chkConvenio').setValue(false);
					                                           					Ext.getCmp(PF+'chkH2HSantander').setVisible(false);
					                                           					Ext.getCmp(PF+'chkH2HSantander').setValue(false);
					                                           				}
					                                           			}
					                                           		}
					                                           	}
				                                            }
				                                        	
				                                    ]    
				                            })
				                        ]
				                    },
		                            {
		                                xtype: 'fieldset',
		                                x: 650,
		                                y: 40,
		                                width: 75,
		                                height: 60,
		                                layout: 'absolute',
		                                id: PF+'contComerica',
		                                name: PF+'contComerica',
		                                hidden: true,
		                                items: [
		                                    new Ext.form.RadioGroup(
				                        			{
				                                    vertical: true,
				                                    id:PF+'optComerica',
				                                    name:PF+'optComerica',	
				                                    columns:1,								                           
				                                    items: [
				                                            {
				                                             boxLabel: 'ACH', 
				                                             name:PF+'id-2', 
				                                             inputValue: 1,
				                                             checked:true,
				                                             listeners:{
				                                           		check:{
				                                           			fn:function(opt, valor)
				                                           			{
				                                           				if(valor==true)
				                                           				{
				                                           					NS.storeEnvioTransGrid.baseParams.optComerica = 'ACH';
				                                           					NS.optComerica = 'ACH';
				                                           				}
				                                           			}
				                                           		}
				                                           	}
				                                            },{
					                                             boxLabel: 'WRD',
					                                             name: PF+'id-2',
					                                             inputValue: 2,
					                                             listeners:{
					                                           		check:{
					                                           			fn:function(opt, valor)
					                                           			{
					                                           				if(valor==true)
					                                           				{
					                                           					NS.storeEnvioTransGrid.baseParams.optComerica = 'WRD';
					                                           					NS.optComerica = 'WRD';
					                                           				}
					                                           			}
					                                           		}
					                                           	}
				                                            }
				                                    ]    
				                            })
		                                ]
		                            },
		                            {
		                                xtype: 'fieldset',
		                                title: '',
		                                width: 75,
		                                height: 35,
		                                x: 650,
		                                y: 110,
		                                layout: 'absolute',
		                                id: PF+'contVenc',
		                                name: PF+'contVenc',
		                                hidden: true,
		                                items: [
		                                	{
				                                xtype: 'checkbox',
				                                x: 290,
				                                y: 0,
				                                boxLabel: 'Todas las empresas',
				                                tabIndex: 1,
				                                id: PF+'optVenc',
				                                name: PF+'optVenc',
				                                hidden: true,
		                                        listeners:{
		                                       		check:{
		                                       			fn:function(opt, valor)
		                                       			{
		                                       				if(valor==true)
		                                       				{
                                           						NS.storeEnvioTransGrid.baseParams.optVenc = true;
                                           						NS.optVenc = 'true';
															}
															else{ 
															   	NS.storeEnvioTransGrid.baseParams.optVenc = false;
															   	NS.optVenc = 'false';
		                                       				}
		                                       			}
		                                       		}
		                                       	}
		                                  	}
		                                ]
		                            },{
				                        xtype: 'container',
				                        padding: 0, 
				                        layout: 'absolute',
				                        x: 420,
				                        y: 40,
				                        width: 280,
				                        height: 105,
				                        items: [
				                               NS.gridDatos
				                        ]
				                    }
		                        ]
		                    },
		                    
		                    NS.contEnvioBancos
		                ]
		            }
		        ]
		});
		
 	NS.EnvioTransferencias.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	//staticCheck("div[id*='gridEnvioTransGrid'] div div[class='x-panel-ml']","div[id*='gridEnvioTransGrid'] div div[class='x-panel-ml']",8,".x-grid3-scroller",false);	
});