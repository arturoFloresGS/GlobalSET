/**
 * 
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.BancaElectronica.ControlArchivosTransferidosA');
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	NS.fecha = NS.fecHoy;
	NS.optTodas = 0;
	NS.optCriterios = 0;
	NS.bChequeOcurre = "";
	NS.transmitidasHoy = false;
	NS.rutaEnvio = "";	
	NS.impOriginal = 0;
	NS.regOriginal = 0;
	NS.usr1 = '';
	NS.usr2 = '';
	NS.fechaIni= NS.fecHoy;
	NS.fechaFin= NS.fecHoy;
	NS.fechaArchivo=NS.fecha;
	NS.mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	
	NS.btnRegenerar = apps.SET.btnFacultativo( 
			new Ext.Button({
				id:  'ecbtnRegenerarA',
				name: 'ecbtnRegenerarA',
				text: 'Regenerar',
				x: 700,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function (e)
		 				{	
					 		var registroSeleccionadoDetalle = NS.storeLlenaGridDetalle.data.items;
							if (registroSeleccionadoDetalle.length <= 0)
								Ext.Msg.alert('SET', 'No existe información para procesar');
							else{
								Ext.Msg.confirm('SET', '¿Esta seguro de volver a generar el archivo?', function(btn){
									if (btn == 'yes'){
										
										  
										ControlArchivosTransferidosAAction.armaCadenaConeccion(200, function(resultado, e){
											if (resultado != '' && resultado != null && resultado != undefined)
												NS.rutaEnvio = resultado;
											else
												Ext.Msg.alert('SET', 'No esta dada de alta la ruta para las Transferencias');
										});
										
										/*var registroArchivo = NS.gridArchivos.getSelectionModel().getSelections();
										var estatus='';
										if(registroArchivo>0)
											estatus = registroArchivo[0].get('especiales');
										if (estatus == 'ARCHIVO RECIBIDO EN SANTANDER' && !hayErrorEnRegistros(registroSeleccionadoDetalle)) {
											NS.regenerar();
										}
										*/
										NS.regenerar();
										/*else if (NS.gridDetalle.getSelectionModel().getSelections().length!=0) {
											NS.regenerar();
										} else {
											//Ext.Msg.alert('SET', 'Seleccione un registro.');
										}*/
									}
								});
							}
		 				}
		 			}
		 		}
		    })
	);

	/************FUNCIONES GENERALES *******************/
	function hayErrorEnRegistros(registroSeleccionadoDetalle) {
		for ( var i = 0; i < registroSeleccionadoDetalle.length; i++) {
			var r = registroSeleccionadoDetalle[i];
			if (r.get("estatusEnArchivo") != 'TRANSACCION EXITOSA') {
				return true;
			}
		}
		return false;
	}

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

	//Cambiar formato fecha
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

	//FUNCIONES
	NS.validaCriterios = function(){
		ControlArchivosTransferidosAAction.validaCriterios(	
															NS.optTodas, //int todas
															NS.optCriterios, //int optCriterios
															NS.fechaArchivo, //String fechaArchivo
															NS.fechaIni, //String fechaInicial
															NS.fechaFin, //String fechaFinal
															Ext.getCmp(PF + 'cmbArchivos').getValue(), //String nomArchivo
															Ext.getCmp(PF + 'txtNoDocto').getValue(), //String noDocto
															function(resultado, e){ //resultado es String
			
			NS.mascara.show();
			NS.storeLlenaGridArchivos.removeAll(); //GRID PRINCIPAL
			NS.gridArchivos.getView().refresh();
			NS.storeLlenaGridDetalle.removeAll(); //GRID DETALLE
			NS.gridDetalle.getView().refresh(); 
																
			if (resultado !== '' && resultado !== undefined && resultado !== null){
				Ext.Msg.alert('SET', resultado);
				NS.mascara.hide();
			}else {				
				NS.storeLlenaGridArchivos.baseParams.optTodas = NS.bChequeOcurre;
				NS.storeLlenaGridArchivos.baseParams.optCriterios = NS.optCriterios;
				NS.storeLlenaGridArchivos.baseParams.fecArchivo = NS.fechaArchivo;
				NS.storeLlenaGridArchivos.baseParams.fecInicial = NS.fechaIni;
				NS.storeLlenaGridArchivos.baseParams.fecFinal = NS.fechaFin;
				NS.storeLlenaGridArchivos.baseParams.nomArchivo = Ext.getCmp(PF + 'cmbArchivos').getValue();
				NS.storeLlenaGridArchivos.baseParams.noDocto = Ext.getCmp(PF + 'txtNoDocto').getValue();
				NS.storeLlenaGridArchivos.load();
			}
		});
	};
	
	NS.limpiaPantalla = function(){
		NS.optTodas = 0;
		NS.optCriterios = 0;
		Ext.getCmp(PF + 'optTodas').setValue(0);
		Ext.getCmp(PF + 'optCriterios').setValue(0);
		Ext.getCmp(PF + 'txtFechaInicial').setDisabled(false);
		Ext.getCmp(PF + 'txtFechaFinal').setDisabled(false);
		Ext.getCmp(PF + 'txtFechaArchivo').setDisabled(true);
		Ext.getCmp(PF + 'txtNoDocto').setDisabled(true);
		Ext.getCmp(PF + 'txtFechaInicial').setVisible(true);
		Ext.getCmp(PF + 'txtFechaFinal').setVisible(true);
		Ext.getCmp(PF + 'txtFechaArchivo').setVisible(false);
		Ext.getCmp(PF + 'txtNoDocto').setVisible(false);	
		Ext.getCmp(PF + 'txtFechaInicial').setValue(NS.fecHoy);
		Ext.getCmp(PF + 'txtFechaFinal').setValue(NS.fecHoy);
		Ext.getCmp(PF + 'txtFechaArchivo').setValue(NS.fecHoy);	 						
		Ext.getCmp(PF + 'txtNoDocto').setValue('');
		Ext.getCmp(PF + 'txtRegistros').setValue('');
		Ext.getCmp(PF + 'txtImporte').setValue('');

		NS.cmbArchivos.reset();
		NS.cmbArchivos.setVisible(false);		 						
		NS.cmbArchivos.setDisabled(true);
		NS.gridArchivos.store.removeAll();
		NS.gridArchivos.getView().refresh();
		NS.gridDetalle.store.removeAll();
		NS.gridDetalle.getView().refresh();

		//
		NS.fechaIni='';
		NS.fechaFin='';
		NS.fechaArchivo=NS.fecha;
		NS.impOriginal = 0;
		NS.regOriginal = 0;
	};
		
	NS.llenaGridDetalle = function(){
		var auxIdBanco=0;
		var registroSeleccionadoArchivo = NS.gridArchivos.getSelectionModel().getSelections();		
		if (registroSeleccionadoArchivo[0].get('fecOperacion') == NS.fecHoy)
			NS.transmitidasHoy = true;
		else
			NS.transmitidasHoy = false;							
		
		if (NS.bChequeOcurre == "") {
			auxIdBanco=registroSeleccionadoArchivo[0].get('idBanco');
			NS.storeLlenaGridDetalle.baseParams.bChequeOcurre = NS.bChequeOcurre;
		}		
							
		NS.storeLlenaGridDetalle.baseParams.idBanco = auxIdBanco;
		NS.storeLlenaGridDetalle.baseParams.transmitidasHoy = NS.transmitidasHoy;
		NS.storeLlenaGridDetalle.baseParams.nomArchivo = registroSeleccionadoArchivo[0].get('nomArch');
		NS.mascara.show();
		NS.storeLlenaGridDetalle.load();
	};
	
	NS.regenerar = function(){
		var matriz = new Array();
		var matrizSeleccionado = new Array();
		var psArchivo = "";
		var mismoBanco = false;
		var bBanamexInversion = false;
		var registroDetalle = NS.storeLlenaGridDetalle.data.items;
		var registroSeleccionado = NS.gridDetalle.getSelectionModel().getSelections();
		var registroArchivo = NS.gridArchivos.getSelectionModel().getSelections();
		var contador = 0;
		psArchivo = registroArchivo[0].get('nomArch') + ".txt";
		var cancelar = false;	
		
		for(var i = 0; i < registroDetalle.length; i++){
			var vectorGrid = {};
			var vectorSeleccionado = {};
			
			if (registroDetalle[i].get('idTipoOperacion') == "3800" && registroDetalle[i].get('idBanco') == 2){
				if (registroDetalle[i].get('idBanco') == registroDetalle[i].get('idBancoBenef')){
					if (registroDetalle[i].get('tipoChequeraBenef') == "I"){
						if (registroDetalle[i].get('origenMov') == "INV" || registroDetalle[i].get('origenMov') == "IVC")
							bBanamexInversion = true;
					}						
				}					
			}				
			
			for (var j = 0; j < registroSeleccionado.length; j++){					
				if (registroSeleccionado[j].get('poHeaders') == registroDetalle[i].get('poHeaders') &&
						registroSeleccionado[j].get('idBanco') == registroDetalle[i].get('idBanco') &&
						registroSeleccionado[j].get('idChequera') == registroDetalle[i].get('idChequera') &&
						registroSeleccionado[j].get('noEmpresa') == registroDetalle[i].get('noEmpresa') &&
						registroSeleccionado[j].get('fecOperacion') == registroDetalle[i].get('fecOperacion') 
					){
					cancelar = true;
					//vectorSeleccionado.noFolioDet = registroDetalle[i].get('noFolioDet');
					vectorSeleccionado.poHeaders = registroDetalle[i].get('poHeaders');
					vectorSeleccionado.idBanco = registroDetalle[i].get('idBanco');
					vectorSeleccionado.idChequera = registroDetalle[i].get('idChequera');
					vectorSeleccionado.noEmpresa = registroDetalle[i].get('noEmpresa');
					vectorSeleccionado.fecOperacion = registroDetalle[i].get('fecOperacion');
					vectorSeleccionado.idTipoOperacion = registroDetalle[i].get('idTipoOperacion');
					vectorSeleccionado.idBancoBenef = registroDetalle[i].get('idBancoBenef');
					vectorSeleccionado.importe = registroDetalle[i].get('importe');
					vectorSeleccionado.nomArchivo = psArchivo;
					matrizSeleccionado[contador] = vectorSeleccionado;
					contador ++;
				}else if (registroSeleccionado.length != registroDetalle.length){
					cancelar = false;
				}
		    }
			//else{				
				//vectorGrid.columnaSeleccion =  NS.columnaSeleccionDetalle;

    		vectorGrid.fecHoy = NS.fecHoy;
			vectorGrid.usuario = NS.idUsuario;
			vectorGrid.nomArchivo = psArchivo;
			vectorGrid.importeTotal = Ext.getCmp(PF + 'txtImporte').getValue();
			vectorGrid.registroTotal = Ext.getCmp(PF + 'txtRegistros').getValue();
			vectorGrid.directorio = NS.rutaEnvio;
			vectorGrid.bBanamexInversion = bBanamexInversion;
			vectorGrid.optCheque = NS.optTodas;
			vectorGrid.tipoEnvioLayout= NS.tipoEnvio;
			vectorGrid.fecTransferencia = registroArchivo[0].get('fecTransferencia');
			
			//nueva seleccion//
			
			vectorGrid.idServicioBe = registroDetalle[i].get('idServicioBe');
			vectorGrid.poHeaders = registroDetalle[i].get('poHeaders');
			vectorGrid.noDocto = registroDetalle[i].get('noDocto');
			vectorGrid.nomEmpresa = registroDetalle[i].get('nomEmpresa');
			vectorGrid.fecOperacion = NS.cambiarFechaGrid(""+registroDetalle[i].get('fecOperacion'));
			vectorGrid.beneficiario = registroDetalle[i].get('beneficiario');
			vectorGrid.importe = registroDetalle[i].get('importe');
			vectorGrid.idDivisa = registroDetalle[i].get('idDivisa');
			vectorGrid.nombreBancoBenef = registroDetalle[i].get('nombreBancoBenef');
			vectorGrid.idChequeraBenef = registroDetalle[i].get('idChequeraBenef');
			vectorGrid.idChequeraBenefReal = registroDetalle[i].get('idChequeraBenefReal');
			vectorGrid.concepto = registroDetalle[i].get('concepto');
			vectorGrid.noFolioDet = registroDetalle[i].get('noFolioDet');
			vectorGrid.noEmpresa = registroDetalle[i].get('noEmpresa');
			vectorGrid.origenMov = registroDetalle[i].get('origenMov');
			vectorGrid.division = registroDetalle[i].get('division');
			vectorGrid.noCliente = registroDetalle[i].get('noCliente');
			vectorGrid.especiales = registroDetalle[i].get('especiales');
			vectorGrid.complemento = registroDetalle[i].get('complemento');
			vectorGrid.clave = registroDetalle[i].get('clave');
			vectorGrid.idBancoBenef = registroDetalle[i].get('idBancoBenef');
			vectorGrid.plazaBenef = registroDetalle[i].get('plazaBenef');
			vectorGrid.plaza = registroDetalle[i].get('plaza');
			vectorGrid.sucDestino = registroDetalle[i].get('sucDestino');
			vectorGrid.clabeBenef = registroDetalle[i].get('clabeBenef');
			vectorGrid.bLayoutComerica = registroDetalle[i].get('bLayoutComerica');
			vectorGrid.idChequera = registroDetalle[i].get('idChequera');
			vectorGrid.fecValor = registroDetalle[i].get('fecValor');
			vectorGrid.idBanco = registroDetalle[i].get('idBanco');
			vectorGrid.instFinan = registroDetalle[i].get('instFinan');
			vectorGrid.sucOrigen = registroDetalle[i].get('sucOrigen');
			vectorGrid.rfcBenef = registroDetalle[i].get('rfcBenef');
			vectorGrid.rfc = registroDetalle[i].get('rfc');
			vectorGrid.horaRecibo = registroDetalle[i].get('horaRecibo');
			vectorGrid.idContratoMass = registroDetalle[i].get('idContratoMass');
			vectorGrid.tipoEnvioLayout = registroDetalle[i].get('tipoEnvioLayout');
			vectorGrid.nomBancoIntermediario = registroDetalle[i].get('nomBancoIntermediario');
			vectorGrid.descBancoBenef = registroDetalle[i].get('descBancoBenef');
			vectorGrid.aba = registroDetalle[i].get('aba');
			vectorGrid.swiftCode = registroDetalle[i].get('swiftCode');
			vectorGrid.abaIntermediario = registroDetalle[i].get('abaIntermediario');
			vectorGrid.swiftIntermediario = registroDetalle[i].get('swiftIntermediario');
			vectorGrid.abaCorresponsal = registroDetalle[i].get('abaCorresponsal');
			vectorGrid.swiftCorresponsal = registroDetalle[i].get('swiftCorresponsal');
			vectorGrid.nomBancoCorresponsal = registroDetalle[i].get('nomBancoCorresponsal');
			vectorGrid.descUsuarioBital = registroDetalle[i].get('descUsuarioBital');
			vectorGrid.descServicioBital = registroDetalle[i].get('descServicioBital');
			vectorGrid.direccionBenef = registroDetalle[i].get('direccionBenef');
			matriz[i] = vectorGrid;
			cancelar = false;
			//fin de nueva seleccion//
			
			/*vectorGrid.noFolioDet  = registroDetalle[i].get('noFolioDet');
			vectorGrid.idTipoOperacion = registroDetalle[i].get('idTipoOperacion');
			vectorGrid.origenMov  = registroDetalle[i].get('origenMov');
			vectorGrid.importe = registroDetalle[i].get('importe');	
			vectorGrid.noEmpresa  = registroDetalle[i].get('noEmpresa');
			vectorGrid.idChequera= registroDetalle[i].get('idChequera');
			vectorGrid.idBanco = registroDetalle[i].get('idBanco');
			vectorGrid.noDocto = registroDetalle[i].get('noDocto');
			vectorGrid.idServicioBE = registroDetalle[i].get('idServicioBE');
			vectorGrid.idDivisa = registroDetalle[i].get('idDivisa');
			vectorGrid.descEstatus = registroDetalle[i].get('descEstatus');				
			vectorGrid.noCliente  = registroDetalle[i].get('noCliente');
			vectorGrid.especiales  = registroDetalle[i].get('especiales');
			vectorGrid.fecValor = registroDetalle[i].get('fecValor');
			vectorGrid.beneficiario = registroDetalle[i].get('beneficiario');
			vectorGrid.nombreBancoBenef = registroDetalle[i].get('descBancoBenef');		
			vectorGrid.idChequeraBenef  = registroDetalle[i].get('idChequeraBenef');
			vectorGrid.idChequeraBenefReal  = registroDetalle[i].get('idChequeraBenefReal');
			vectorGrid.concepto  = registroDetalle[i].		get('concepto');
			vectorGrid.complemento  = registroDetalle[i].get('complemento');			
			vectorGrid.plazaBenef  = registroDetalle[i].get('plazaBenef');
			vectorGrid.plaza  = registroDetalle[i].get('plaza');
			vectorGrid.sucDestino  = registroDetalle[i].get('sucursal');
			vectorGrid.clabeBenef  = registroDetalle[i].get('idClabeBenef');
			vectorGrid.bLayoutComerica  = registroDetalle[i].get('layoutComerica');
			vectorGrid.idChequera  = registroDetalle[i].get('idChequera');
			vectorGrid.instFinan = registroDetalle[i].get('institucionFinan');
			vectorGrid.sucOrigen = registroDetalle[i].get('sucOrigen');
			vectorGrid.rfcBenef = registroDetalle[i].get('rfcBenef');
			vectorGrid.horaRecibo = registroDetalle[i].get('hora');				
			vectorGrid.tipoEnvioLayoutCtas = registroDetalle[i].get('tipoEnvioLayoutCtas') == "" || registroDetalle[i].get('tipoEnvioLayoutCtas') == null ? 0 : registroDetalle[i].get('tipoEnvioLayoutCtas');				
			vectorGrid.tipoEnvioLayout = registroDetalle[i].get('tipoEnvioLayout');				
			vectorGrid.nomBancoIntermediario = registroDetalle[i].get('bancoInter');
			vectorGrid.descBancoBenef = registroDetalle[i].get('descBancoBenef');
			vectorGrid.aba = registroDetalle[i].get('aba');			
			vectorGrid.swiftCode = registroDetalle[i].get('abaSwift');
			vectorGrid.abaIntermediario = registroDetalle[i].get('abaInter');
			vectorGrid.swiftIntermediario = registroDetalle[i].get('swiftInter');
			vectorGrid.abaCorresponsal = registroDetalle[i].get('abaCorres');
			vectorGrid.swiftCorresponsal = registroDetalle[i].get('swiftCorres');
			vectorGrid.nomBancoCorresponsal = registroDetalle[i].get('bancoCorres');
			vectorGrid.descUsuarioBital = registroDetalle[i].get('usuarioBital');
			vectorGrid.descServicioBital = registroDetalle[i].get('servicioBital');
			vectorGrid.idBancoBenef=registroDetalle[i].get('idBancoBenef');
			*/
				
			
		}			
	
		var jsonString = Ext.util.JSON.encode(matriz);
		var jsonStringSeleccionado = Ext.util.JSON.encode(matrizSeleccionado);	
													
		ControlArchivosTransferidosAAction.regenerar(jsonString,  //registro
													jsonStringSeleccionado,  //registroCancelado 
													contador, //registroTotal
													function(resultado, e){
		if (resultado != '' && resultado != undefined && resultado != null)
			
			Ext.Msg.alert('Información SET', ''+resultado.msgUsuario, function(btn){
			    if (btn == 'ok'){
			
					if(resultado.resExitoLocal == true){
						strParams = '?nomReporte=layouts';
						strParams += '&'+'nomParam1=rutaArchivo';
						strParams += '&'+'valParam1='+resultado.resRutaLocal;
						strParams += '&'+'nomParam2=nombreArchivo';
						strParams += '&'+'valParam2='+resultado.resNombreArchivoLocal;
						window.open("/SET/jsp/bfrmwrk/gnrators/abretxt.jsp"+strParams);
						Ext.Msg.alert('SET', resultado.msgUsuario);
					}else{
						Ext.Msg.alert('SET', 'Error al generar el archivo.');
					}
			    }
			});
				
		});

		NS.gridArchivos.store.removeAll();
		NS.gridArchivos.getView().refresh();
		NS.storeLlenaGridArchivos.baseParams.optTodas = NS.bChequeOcurre;
		NS.storeLlenaGridArchivos.baseParams.optCriterios = NS.optCriterios;
		NS.storeLlenaGridArchivos.baseParams.fecArchivo = NS.fechaArchivo;
		NS.storeLlenaGridArchivos.baseParams.fecInicial = NS.fechaIni;
		NS.storeLlenaGridArchivos.baseParams.fecFinal = NS.fecFinal;
		NS.storeLlenaGridArchivos.baseParams.nomArchivo = Ext.getCmp(PF + 'cmbArchivos').getValue();
		NS.storeLlenaGridArchivos.baseParams.noDocto = Ext.getCmp(PF + 'txtNoDocto').getValue();
		NS.mascara.show();
		NS.storeLlenaGridArchivos.load();
		NS.gridDetalle.store.removeAll();
		NS.gridDetalle.getView().refresh();
		NS.llenaGridDetalle();
	};	

	//Imprimir aun en pruebas
	//Funcion para la llamada al reporte de envio de transferencias
	NS.imprimeReporte = function(archivo){
		var nomEmpresa;
		var registrosSelec = NS.gridDetalle.
			getSelectionModel().getSelections();
		
		if(registrosSelec.length == 1)
			nomEmpresa = registrosSelec[0].get('nomEmpresa');
		else
			nomEmpresa = 'VARIAS EMPRESAS DEL GRUPO SALINAS';
		
		strParams = '?nomReporte=detArchivoTransf2';
		strParams += '&'+'nomParam1=archivo';
		strParams += '&'+'valParam1=' + archivo + ".txt";
		strParams += '&'+'nomParam2=nomEmpresa';
		strParams += '&'+'valParam2=' + nomEmpresa;
		strParams += '&'+'nomParam3=titulo';
		strParams += '&'+'valParam3=' + "DETALLE DEL ARCHIVO DE TRANSFERENCIAS";
		
		strParams += '&'+'nomParam4=fecHoy';
		strParams += '&'+'valParam4=' + apps.SET.FEC_HOY;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		return;
	};
	
	NS.cambiarFechaGrid=function(fecha){
		return fecha.substring(0,10);
	}
	
	/**********FIN FUNCIONES GENERALES ****************/

	/*********Store ************************************/

	NS.storeLlenaComboArchivos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {fecValor: NS.fecha,bChequeOcurre:''},
		paramOrder: ['fecValor', 'bChequeOcurre'],
		directFn: ControlArchivosTransferidosAAction.llenaComboArchivos,
		idProperty: 'nomArch',
		fields: [
		 	{name: 'nomArch'},
		 	{name: 'nomArch'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboArchivos, msg: "Cargando Archivos.."});
				if (records.length == null || records.length <= 0){
					NS.cmbArchivos.reset();
					Ext.Msg.alert('SET', 'No existen archivos en esta fecha');
										
				}
				NS.mascara.hide();
			}
		}	
	});	
	
	NS.storeLlenaGridArchivos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {optTodas:'',optCriterios:'',fecArchivo:'',fecInicial:'',fecFinal:'',nomArchivo:'',noDocto:''},
		paramOrder: ['optTodas', 'optCriterios', 'fecArchivo', 'fecInicial', 'fecFinal', 'nomArchivo', 'noDocto'],
		directFn: ControlArchivosTransferidosAAction.llenaGridArchivos,
		fields:
		[
		 	{name: 'descBanco'},
		 	{name: 'nomArch'},
		 	{name: 'fecTransferencia'},
		 	{name: 'fecRetransferencia'},
		 	{name: 'importe'},
		 	{name: 'registros'},
		 	{name: 'noUsuarioAlta'},
		 	{name: 'noUsuarioModif'},
		 	{name: 'idBanco'},
		 	{name: 'nomUsrAlta'},
		 	{name: 'nomUsrModif'},
		 	{name: 'especiales'},
		 	{name: 'color'},
		 	{name: 'ruta'}
		],
		listeners:
		{
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridArchivos, msg: "Cargando Archivos.."});
				if (records.length == null || records.length <= 0){					
					Ext.Msg.alert('SET', 'No existen archivos con estos criterios de busqueda');
					NS.gridDetalle.store.removeAll();
					NS.gridDetalle.getView().refresh();				
				}
				NS.mascara.hide();
			}
		}
	});
	
	NS.storeLlenaGridDetalle = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {nomArchivo:'',idBanco:0,transmitidasHoy:false, bChequeOcurre:''},
		paramOrder: ['nomArchivo', 'idBanco', 'transmitidasHoy', 'bChequeOcurre'],
		directFn: ControlArchivosTransferidosAAction.llenaGridDetalle,
		fields: 
			[
				{name : 'noDocto'},
				{name : 'fecOperacion'},
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
				{name : 'fecValor'},
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
				{name : 'direccionBenef'},
				{name : 'nomBancoCorresponsal'},
				{name : 'descUsuarioBital'},
				{name : 'descServicioBital'},
				{name : 'nomEmpresa'},
				{name : 'noFolioMov'},
				{name : 'poHeaders'},
				{name : 'idServicioBe'}
				
			],
		listeners:{
			load: function(s, recordsDetalle) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridDetalle, msg: "Cargando Detalle.."});
				Ext.getCmp(PF + 'txtRegistros').setValue('0');
				Ext.getCmp(PF + 'txtImporte').setValue('0.00');
				
				if (recordsDetalle.length == null || recordsDetalle.length <= 0){					
					Ext.Msg.alert('SET', 'No existe detalle para el archivo seleccionado');
					NS.gridDetalle.store.removeAll();
					NS.gridDetalle.getView().refresh();
				}else{				
					var registroDetalle = NS.storeLlenaGridDetalle.data.items;
					var registroTotal = 0;
					var importeTotal = 0;
					
					for (var i = 0; i < registroDetalle.length; i++){
						//if (registroDetalle[i].get('descEstatus') !== 'CANCELADO'){							
							registroTotal += 1;
							importeTotal += registroDetalle[i].get('importe');
						//}	
					}
					NS.impOriginal = importeTotal;
					NS.regOriginal = registroTotal;
					Ext.getCmp(PF + 'txtRegistros').setValue(registroTotal);
					Ext.getCmp(PF + 'txtImporte').setValue(NS.formatNumber(importeTotal));
				}
				NS.mascara.hide();
			}
		}
	});

	/***********FIN Store *****************************/
	
	//LABEL
	NS.lblValor = new Ext.form.Label({
		text: 'Valor:',
		x: 0,
		y: 40
	});
	
	NS.lblRegistro = new Ext.form.Label({
		text: 'Registros:',
		x: 0,
		y: 220
	});
	
	NS.lblImporte = new Ext.form.Label({
		text: 'Importe:',
		x: 200,
		y: 220
	});
	
	//TEXTFIELD
	NS.txtFechaArchivo = new Ext.form.DateField({
		id: PF + 'txtFechaArchivo',
		name: PF + 'txtFechaArchivo',
		x: 40,
		y: 35,
		width: 100,		
		format: 'd/m/Y',
		value: NS.fecHoy,
		hidden: true,
		listeners: {
			change: {
				fn: function(caja,e) {
					NS.fechaArchivo=cambiarFecha(caja.getValue()+'')+'';
					NS.fecha = NS.fechaArchivo;
					NS.storeLlenaComboArchivos.baseParams.fecValor = NS.fechaArchivo;
					NS.storeLlenaComboArchivos.baseParams.bChequeOcurre = NS.bChequeOcurre;
					NS.storeLlenaComboArchivos.load();
				}
			}
		}
	});
	
	NS.txtFechaInicial = new Ext.form.DateField({
		id: PF + 'txtFechaInicial',
		name: PF + 'txtFechaInicial',
		x: 40,
		y: 35,
		width: 100,
		format: 'd/m/Y',
		hidden: false,
		value: NS.fecha,
		enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {	
	 				NS.fechaIni=cambiarFecha(caja.getValue()+'')+'';
	 				var fechaFin = Ext.getCmp(PF + 'txtFechaFinal').getValue();
        			if(fechaFin < caja.getValue()&& NS.fechaFin!=''){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
        				Ext.getCmp(PF + 'txtFechaFinal').setValue('');
        			}
	 			}
	 		}
		}
	});
	
	NS.txtFechaFinal = new Ext.form.DateField({
		id: PF + 'txtFechaFinal',
		name: PF + 'txtFechaFinal',
		x: 160,
		y: 35,
		width: 100,
		format: 'd/m/Y',
		value: NS.fecha,
		hidden: false,
		enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {
	 				NS.fechaFin=cambiarFecha(caja.getValue()+'')+'';
	 				var fechaIni = Ext.getCmp(PF + 'txtFechaInicial').getValue();
        			if(fechaIni > caja.getValue()&& NS.fechaIni!=''){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
        				Ext.getCmp(PF + 'txtFechaInicial').setValue('');
        			}
	 			}
	 		}
		}
	});
	
	NS.txtNoDocto = new Ext.form.TextField({
		id: PF + 'txtNoDocto',
		name: PF + 'txtNoDocto',
		x: 40,
		y: 35,
		width: 200,
		hidden: true
	});
	
	NS.txtRegistros = new Ext.form.TextField({
		id: PF + 'txtRegistros',
		name: PF + 'txtRegistros',
		x: 55,
		y: 215,
		width: 100		
	});
	
	NS.txtImporte = new Ext.form.TextField({
		id: PF + 'txtImporte',
		name: PF + 'txtImporte',
		x: 250,
		y: 215,
		width: 100,
	});
	
	//COMBOBOX
	NS.cmbArchivos = new Ext.form.ComboBox({
		store: NS.storeLlenaComboArchivos,
		id: PF + 'cmbArchivos',
		name: PF + 'cmbArchivos',
		x: 180,
		y: 35,
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'nomArch',
		displayField: 'nomArch',
		autocomplete: true,
		emptyText: 'Seleccione Archivo',
		triggerAction: 'all',
		value: '',
		visible: false,
		hidden: true,
		listeners: {
			select: {
				fn: function(combo, valor) {
					
				}
			}
		}		
	});
	//RADIOBUTTON
	NS.optRadioTodas = new Ext.form.RadioGroup({
		id: PF + 'optTodas',
		name: PF + 'optTodas',
		x: 10,
		y: 0,
		width: 200,
		height: 30,
		columns: 2,
		items:
		[
		 	{boxLabel: 'Todas', name: 'optSeleccion1', inputValue: 0, checked: true, width: 80, height: 30,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true){
		 						NS.optTodas = 0;
		 						NS.bChequeOcurre = "";
		 					}
		 					NS.gridArchivos.store.removeAll();
							NS.gridArchivos.getView().refresh();
							NS.gridDetalle.store.removeAll();
							NS.gridDetalle.getView().refresh();
		 				}
		 			}
		 		}
		 	},
		 	{boxLabel: 'Cheque Ocurre', name: 'optSeleccion1', inputValue: 1, width: 150, height: 30,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					//alert("linea 676"+valor);
		 					if (valor == true)
		 					{
		 						NS.optTodas = 1;
		 						NS.bChequeOcurre = "S";
		 					}
		 					NS.gridArchivos.store.removeAll();
							NS.gridArchivos.getView().refresh();
							NS.gridDetalle.store.removeAll();
							NS.gridDetalle.getView().refresh();
		 				}
		 			}
		 		}
		 	}
		]
	}); 
	
	NS.optRadioCriterios = new Ext.form.RadioGroup({
		id: PF + 'optCriterios',
		name: PF + 'optCriterios',
		x: 0,
		y: 0,
		width: 370, 
		columns: 3,
		items:
		[
		 	{boxLabel: 'Fecha', name: 'optSeleccion2', inputValue: 0, checked: true, width: 50, height: 30,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor ==  true) {
		 						NS.optCriterios = 0;
		 						Ext.getCmp(PF + 'txtFechaInicial').setDisabled(false);
		 						Ext.getCmp(PF + 'txtFechaFinal').setDisabled(false);
		 						Ext.getCmp(PF + 'txtFechaArchivo').setDisabled(true);
		 						NS.cmbArchivos.setDisabled(true);		 						
		 						Ext.getCmp(PF + 'txtNoDocto').setDisabled(true);
		 						
		 						Ext.getCmp(PF + 'txtFechaInicial').setVisible(true);
		 						Ext.getCmp(PF + 'txtFechaFinal').setVisible(true);
		 						Ext.getCmp(PF + 'txtFechaArchivo').setVisible(false);
		 						NS.cmbArchivos.setVisible(false);		 						
		 						Ext.getCmp(PF + 'txtNoDocto').setVisible(false);
		 						
		 						NS.gridArchivos.store.removeAll();
		 						NS.gridArchivos.getView().refresh();
		 						NS.gridDetalle.store.removeAll();
		 						NS.gridDetalle.getView().refresh();
		 						
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{boxLabel: 'Archivo', name: 'optSeleccion2', inputValue: 1, width: 50, height: 30,		 		
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.optCriterios = 1;
		 						
		 						//NS.fecha = NS.fecValor;
		 						
		 						if (NS.optTodas == 1)
		 							NS.storeLlenaComboArchivos.baseParams.bChequeOcurre = "S";
		 						else
		 							NS.storeLlenaComboArchivos.baseParams.bChequeOcurre = "";
		 						
		 						
		 						NS.storeLlenaComboArchivos.baseParams.fecValor = NS.fecha;
		 						NS.storeLlenaComboArchivos.load();
		 						
		 						Ext.getCmp(PF + 'txtFechaArchivo').setDisabled(false);		 						
		 						NS.cmbArchivos.setDisabled(false);		 						 						
		 						Ext.getCmp(PF + 'txtFechaInicial').setDisabled(true);		 						
		 						Ext.getCmp(PF + 'txtFechaFinal').setDisabled(true);
		 						Ext.getCmp(PF + 'txtNoDocto').setDisabled(true);
		 						
		 						Ext.getCmp(PF + 'txtFechaArchivo').setVisible(true);
		 						NS.cmbArchivos.setVisible(true);
		 						Ext.getCmp(PF + 'txtFechaInicial').setVisible(false);
		 						Ext.getCmp(PF + 'txtFechaFinal').setVisible(false);
		 						Ext.getCmp(PF + 'txtNoDocto').setVisible(false);
		 						
		 						NS.gridArchivos.store.removeAll();
		 						NS.gridArchivos.getView().refresh();
		 						NS.gridDetalle.store.removeAll();
		 						NS.gridDetalle.getView().refresh();
		 					}
		 				}
		 			}
		 		}
		 	},
		 	{boxLabel: 'Número de Docto.', name: 'optSeleccion2', inputValue: 2, width: 280, height: 30,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true) {
		 						NS.optCriterios = 2;
		 						
		 						Ext.getCmp(PF + 'txtNoDocto').setDisabled(false);
		 						Ext.getCmp(PF + 'txtFechaInicial').setDisabled(true);
		 						Ext.getCmp(PF + 'txtFechaFinal').setDisabled(true);
		 						Ext.getCmp(PF + 'txtFechaArchivo').setDisabled(true);
		 						NS.cmbArchivos.setDisabled(true);
		 						
		 						Ext.getCmp(PF + 'txtNoDocto').setVisible(true);
		 						Ext.getCmp(PF + 'txtFechaInicial').setVisible(false);
		 						Ext.getCmp(PF + 'txtFechaFinal').setVisible(false);
		 						Ext.getCmp(PF + 'txtFechaArchivo').setVisible(false);
		 						NS.cmbArchivos.setVisible(false);	
		 						
		 						NS.gridArchivos.store.removeAll();
		 						NS.gridArchivos.getView().refresh();
		 						NS.gridDetalle.store.removeAll();
		 						NS.gridDetalle.getView().refresh();
		 					}
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	//GRID ARCHIVOS
	NS.columnaSeleccionArchivos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	}); 
	
	NS.columnasGridArchivos = new Ext.grid.ColumnModel([                                              
	   {header: 'Banco', width: 90, dataIndex: 'descBanco', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');return value;}},
	   {header: 'Archivo', width: 90, dataIndex: 'nomArch', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }},
	   {header: 'Fec. Transfer', width: 100, dataIndex: 'fecTransferencia', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }},
	   {header: 'Fec. Retrans.', width: 100, dataIndex: 'fecRetransferencia', sortable: true, hidden:true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }},
	   {header: 'Importe', width: 90, dataIndex: 'importe', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return '$' + NS.formatNumber(value);
	   }},
	   {header: 'Registros', width: 60, dataIndex: 'registros', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }},
	   {header: 'Usuario Alta', width: 80, dataIndex: 'noUsuarioAlta', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }},
	   {header: 'Usuario Modif.', width: 80, dataIndex: 'noUsuarioModif', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }},
	   {header: 'Usr. Alta', width: 150, dataIndex: 'nomUsrAlta', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }},
	   {header: 'Usr. Modif.', width: 150, dataIndex: 'nomUsrModif', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }},
	   {header: 'Id Banco', width: 60, dataIndex: 'idBanco', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }},
	   {header: 'Estatus', width: 250, dataIndex: 'especiales', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }}
	  
	   
	]);

	
	//Grids panel

	NS.gridArchivos = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridArchivos,
		id: PF + 'gridArchivos',
		name: PF + 'gridArchivos',
		cm: NS.columnasGridArchivos,
		sm: NS.columnaSeleccionArchivos,
		width: 500,
		height: 90,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {					
					var registroSeleccionadoArchivo = NS.gridArchivos.getSelectionModel().getSelections();
					if (registroSeleccionadoArchivo.length <= 0){
						Ext.Msg.alert('SET', 'No existen registros de Archivos');
						NS.gridDetalle.store.removeAll();
						NS.gridDetalle.getView().refresh();
					}else{
						NS.llenaGridDetalle();
					}					
				}
			}
		}
	});
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		
	});
	
	NS.gridDetalle = new Ext.grid.GridPanel({
		id: PF+'gridDetalle',
		name: PF+'gridDetalle',
        store : NS.storeLlenaGridDetalle,
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm,
               	{header: 'Poliza Compensacion', width: 50, dataIndex: 'noDocto'},
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
					dataIndex: 'fecOperacion'
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
				},
				{
					header: 'poliza de compensacion.', 
					width: 100, 
					dataIndex: 'poHeaders', hidden: true
				},
				{
					header: 'Servicio',
					width: 100,
					dataIndex: 'idServicioBe',
					hidden: true
				},
				{
					header: 'Direccion Benef.', 
					width: 100, 
					dataIndex: 'direccionBenef', hidden: true
				}
            ]
        }),
        sm: NS.sm,
        columnLines: true,
        x:0,
        y:0,
        width: 950,
		height: 200,
		//stripeRows: true,
		//columnLines: true,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			/*var sum=0;
          			var regSelec=NS.gridEnvioTransGrid.getSelectionModel().getSelections();
          			for(var k=0;k<regSelec.length;k=k+1)
          			{
          				sum=sum+regSelec[k].get('importe');
          			}
          		 
          			Ext.getCmp(PF+'txtTotalReg').setValue(regSelec.length);
					Ext.getCmp(PF+'txtMontoTotal').setValue(NS.formatNumber(Math.round((sum)*100)/100));
					*/
        			/*var sum = 0;
					var registros = 0;
					var regSelec = NS.gridDetalle.getSelectionModel().getSelections();
					
					for(var k=0; k<regSelec.length; k++)
					{
						//if (regSelec[k].get('descEstatus') !== 'CANCELADO'){
							sum += regSelec[k].get('importe');
							registros = registros + 1;
						//}
					}
					var importe = NS.impOriginal - (Math.round((sum)*100)/100);
					Ext.getCmp(PF+'txtRegistros').setValue(NS.regOriginal - registros);
					Ext.getCmp(PF+'txtImporte').setValue(NS.formatNumber(importe));*/
        		}
        	}
        }
    });
	
	/*NS.gridDetalle = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridDetalle,
		id: PF + 'gridDetalle',
		name: PF + 'gridDetalle',
		cm: NS.columnasGridDetalle,
		sm: NS.columnaSeleccionDetalle,
		width: 950,
		height: 200,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					var sum = 0;
					var registros = 0;
					var regSelec = NS.gridDetalle.getSelectionModel().getSelections();
					
					for(var k=0; k<regSelec.length; k++)
					{
						if (regSelec[k].get('descEstatus') !== 'CANCELADO'){
							sum += regSelec[k].get('importe');
							registros = registros + 1;
						}
					}
					var importe = NS.impOriginal - (Math.round((sum)*100)/100);
					Ext.getCmp(PF+'txtRegistros').setValue(NS.regOriginal - registros);
					Ext.getCmp(PF+'txtImporte').setValue(NS.formatNumber(importe));
				}
			}
		}
	});
*/
	
	//PANEL	
	NS.panelArchivos = new Ext.form.FieldSet({
		title: 'Archivos:',
		x: 430,
		y: 0,
		width: 530,
		height: 125,
		layout: 'absolute',
		items:
		[
		 	NS.gridArchivos
		]
	});
	
	NS.panelCriterios = new Ext.form.FieldSet({
		text: '',
		x: 0,
		y: 35,
		width: 410,
		height: 85,
		layout: 'absolute',
		items:
		[
		 	NS.optRadioCriterios,
		 	NS.lblValor,
		 	NS.txtFechaArchivo,
		 	NS.txtFechaInicial,
		 	NS.txtFechaFinal,
		 	NS.txtNoDocto, 
		 	NS.cmbArchivos
		]
	});
	
	
	NS.panelBusqueda = new Ext.form.FieldSet({
		text: 'Busqueda',
		x: 0,
		y: 0,
		width: 985,
		height: 150,
		layout: 'absolute',
		items:
		[
		 	NS.optRadioTodas,
		 	NS.panelArchivos,
		 	NS.panelCriterios,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 330,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e){
		 					NS.validaCriterios();
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelDetalle = new Ext.form.FieldSet({
		text: '',
		x: 0,
		y: 160,
		width: 985,
		height: 270,
		layout: 'absolute',
		items:
		[
		 	NS.gridDetalle,
		 	NS.lblRegistro,
		 	NS.lblImporte,
		 	NS.txtRegistros,
		 	NS.txtImporte
		]
	});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1010,		
		height: 490,
		layout: 'absolute',
		items:
		[
		 	NS.panelBusqueda,
		 	NS.panelDetalle,
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 800,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function (e)
		 				{	
					 		var registroSeleccionadoDetalle = NS.storeLlenaGridDetalle.data.items;
							if (registroSeleccionadoDetalle.length <= 0)
								Ext.Msg.alert('SET', 'No existe información para procesar');
							else{
								
								var registroArchivo = NS.gridArchivos.getSelectionModel().getSelections();
								var nomArch = registroArchivo[0].get('nomArch');
								NS.imprimeReporte(nomArch);
							}
		 				}
		 			}
		 		}
		 	},
		 	NS.btnRegenerar,
		 	{
		 		xtype: 'button',
		 		text: 'Limpiar',
		 		x: 900,
		 		y: 445,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function (e) {
		 					NS.limpiaPantalla();
		 				}
		 			}
		 		}
		 	}
	    ]
	});
	
	NS.controlArchivosTransferidos = new Ext.FormPanel ({
		title: 'Control De Archivos Transferidos',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'controlArchivosTransferidos',
		name: PF + 'controlArchivosTransferidos',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});	
	
	NS.controlArchivosTransferidos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});