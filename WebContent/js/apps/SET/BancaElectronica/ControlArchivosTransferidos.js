Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.BancaElectronica.ControlArchivosTransferidos');
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
	
	//Para evitar el desface de los grids
	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
	NS.btnRegenerar = apps.SET.btnFacultativo( 
			new Ext.Button({
				id:  'ecbtnRegenerar',
				name: 'ecbtnRegenerar',
				text: 'Regenerar',
				x: 700,
		 		y: 600,
		 		width: 80,
		 		height: 22,
		 		listeners:
		 		{
		 			click:
		 			{
		 				fn: function (e)
		 				{	
		 					var registroArchivo = NS.gridArchivos.getSelectionModel().getSelections();
		 					var seleccionDetalle = NS.gridDetalle.getSelectionModel().getSelections();
		 					var nomArch = registroArchivo[0].get('nomArch');
		 					if(nomArch.substring(0,4)=='tran' && seleccionDetalle.length<=0){
		 						Ext.Msg.alert('SET','El archivo es H2H, es necesario seleccionar un registro');
		 						return;
		 					}else{
						 		var registroSeleccionadoDetalle = NS.storeLlenaGridDetalle.data.items;
								if (registroSeleccionadoDetalle.length <= 0)
									Ext.Msg.alert('SET', 'No existe información para procesar');
								else{
									Ext.Msg.confirm('SET', '¿Esta seguro de volver a generar el archivo?', function(btn){
										if (btn == 'yes'){
											//Se valida el Bank Boston
											if (registroSeleccionadoDetalle[0].get('idBanco') == 10159){
												ControlArchivosTransferidosAction.configuraSet(227, function(resultado, e){
													if (resultado != '' && resultado != null && resultado != undefined && resultado == "T" && Ext.getCmp(PF + 'optTodas').getValue() == 0){
														Ext.Msg.alert('SET', 'No se pueden cancelar las Transferencias Host to Host BANK BOSTON');
														return;
													}
												});
											}
											  
											ControlArchivosTransferidosAction.configuraSet(200, function(resultado, e){
												if (resultado != '' && resultado != null && resultado != undefined)
													NS.rutaEnvio = resultado;
												else
													Ext.Msg.alert('SET', 'No esta dada de alta la ruta para las Transferencias');
											});
											
											var registroArchivo = NS.gridArchivos.getSelectionModel().getSelections();
											var estatus='';
											if(registroArchivo>0)
												estatus = registroArchivo[0].get('especiales');
											if (estatus == 'ARCHIVO RECIBIDO EN SANTANDER' && !hayErrorEnRegistros(registroSeleccionadoDetalle)) {
												NS.regenerar();
											}
											
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
		ControlArchivosTransferidosAction.validaCriterios(	
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
		//console.log(registroSeleccionadoArchivo[0].get('fecTransferencia'))
		if (registroSeleccionadoArchivo[0].get('fecTransferencia') == NS.fecHoy)
			NS.transmitidasHoy = true;
		else
			NS.transmitidasHoy = false;							
		
		if (NS.bChequeOcurre == "") {
			auxIdBanco=registroSeleccionadoArchivo[0].get('idBanco');
			NS.storeLlenaGridDetalle.baseParams.bChequeOcurre = NS.bChequeOcurre;
		}else{
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
		//Si se selecciona cheques cambia la ruta para dejar los archivos
		if (Ext.getCmp(PF + 'optTodas').getValue == 1){
			if (NS.rutaEnvio.indexOf("chqOcurre") <= 0)
				NS.rutaEnvio = NS.rutaEnvio + "\chqOcurre";
		}		
		
		for(var i = 0; i < registroDetalle.length; i++){
			var vectorGrid = {};
			var vectorSeleccionado = {};
			//console.log("chequera banamex regenerar js"+registroDetalle[i].get('idChequeraBanamex'));
			if (registroDetalle[i].get('idTipoOperacion') == "3800" && registroDetalle[i].get('idBanco') == 2){
				if (registroDetalle[i].get('idBanco') == registroDetalle[i].get('idBancoBenef')){
					if (registroDetalle[i].get('tipoChequeraBenef') == "I"){
						if (registroDetalle[i].get('origenMov') == "INV" || registroDetalle[i].get('origenMov') == "IVC")
							bBanamexInversion = true;
					}						
				}					
			}				
			
			for (var j = 0; j < registroSeleccionado.length; j++){					
				if (registroSeleccionado[j].get('noFolioDet') == registroDetalle[i].get('noFolioDet')){
					cancelar = true;
					vectorSeleccionado.noFolioDet = registroDetalle[i].get('noFolioDet');
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
			vectorGrid.importeTotal = Ext.getCmp(PF + 'txtImporte').getValue();
			vectorGrid.registroTotal = Ext.getCmp(PF + 'txtRegistros').getValue();
			vectorGrid.directorio = NS.rutaEnvio;
			vectorGrid.bBanamexInversion = bBanamexInversion;
			vectorGrid.optCheque = NS.optTodas;
			vectorGrid.noFolioDet  = registroDetalle[i].get('noFolioDet');
			vectorGrid.idTipoOperacion = registroDetalle[i].get('idTipoOperacion');
			vectorGrid.origenMov  = registroDetalle[i].get('origenMov');
			vectorGrid.importe = registroDetalle[i].get('importe');	
			vectorGrid.noEmpresa  = registroDetalle[i].get('noEmpresa');
			vectorGrid.idChequera= registroDetalle[i].get('idChequera');
			vectorGrid.idBanco = registroDetalle[i].get('idBanco');
			vectorGrid.usuario = NS.idUsuario;
			vectorGrid.noDocto = registroDetalle[i].get('noDocto');
			vectorGrid.fecTransferencia = registroArchivo[0].get('fecTransferencia');
			vectorGrid.idServicioBE = registroDetalle[i].get('idServicioBE');
			vectorGrid.idDivisa = registroDetalle[i].get('idDivisa');
			vectorGrid.nomArchivo = psArchivo;

			//if (cancelar == false){
				vectorGrid.descEstatus = registroDetalle[i].get('descEstatus');				
				vectorGrid.noCliente  = registroDetalle[i].get('noCliente');
				vectorGrid.especiales  = registroDetalle[i].get('especiales');
				vectorGrid.fecValor = registroDetalle[i].get('fecValor');
				vectorGrid.beneficiario = registroDetalle[i].get('beneficiario');
				vectorGrid.nombreBancoBenef = registroDetalle[i].get('descBancoBenef');		
				vectorGrid.idChequeraBenef  = registroDetalle[i].get('idChequeraBenef');
				vectorGrid.idChequeraBenefReal  = registroDetalle[i].get('idChequeraBenefReal');
				vectorGrid.concepto  = registroDetalle[i].get('concepto');
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
				vectorGrid.idChequeraBanamex=registroDetalle[i].get('idChequeraBanamex');
				//console.log("chequera banamex "+vectorGrid.idChequeraBanamex);
				vectorGrid.horaRecibo=registroDetalle[i].get('horaRecibo');
				vectorGrid.tipoEnvioLayout= NS.tipoEnvio;
			//}		
			matriz[i] = vectorGrid;
			cancelar = false;
		}			
						
			
		//}		
		var jsonString = Ext.util.JSON.encode(matriz);
		var jsonStringSeleccionado = Ext.util.JSON.encode(matrizSeleccionado);	
													
		ControlArchivosTransferidosAction.regenerar(jsonString,  //registro
													jsonStringSeleccionado,  //registroCancelado 
													contador, //registroTotal
													function(resultado, e){
		if (resultado != '' && resultado != undefined && resultado != null)	
			if(resultado.msgUsuario.substring(31,35)=='tran'){
				Ext.Msg.alert('SET', resultado.msgUsuario);
			}else{
					if(resultado.resExitoLocal == true){
						strParams = '?nomReporte=layouts';
						strParams += '&'+'nomParam1=rutaArchivo';
						strParams += '&'+'valParam1='+resultado.resRutaLocal+resultado.resNombreArchivoLocal;
						strParams += '&'+'nomParam2=nombreArchivo';
						strParams += '&'+'valParam2='+resultado.resNombreArchivoLocal;
						window.open("/SET/jsp/bfrmwrk/gnrators/abretxt.jsp"+strParams);
						Ext.Msg.alert('SET', resultado.msgUsuario);
					}else{
						Ext.Msg.alert('SET', 'No se puede regenerar este archivo');
					}
			}
				NS.gridArchivos.store.removeAll();
				NS.gridArchivos.getView().refresh();
				/*NS.storeLlenaGridArchivos.baseParams.optTodas = NS.bChequeOcurre;
				NS.storeLlenaGridArchivos.baseParams.optCriterios = NS.optCriterios;
				NS.storeLlenaGridArchivos.baseParams.fecArchivo = NS.fechaArchivo;
				NS.storeLlenaGridArchivos.baseParams.fecInicial = NS.fechaIni;
				NS.storeLlenaGridArchivos.baseParams.fecFinal = NS.fecFinal;
				NS.storeLlenaGridArchivos.baseParams.nomArchivo = Ext.getCmp(PF + 'cmbArchivos').getValue();
				NS.storeLlenaGridArchivos.baseParams.noDocto = Ext.getCmp(PF + 'txtNoDocto').getValue();
				NS.mascara.show();*/
				//NS.storeLlenaGridArchivos.load();
				NS.gridDetalle.store.removeAll();
				NS.gridDetalle.getView().refresh();
				//NS.llenaGridDetalle();
		});
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
			nomEmpresa = 'VARIAS EMPRESAS DE GRUPO FORMULA';
		
		strParams = '?nomReporte=detArchivoTransf';
		strParams += '&'+'nomParam1=archivo';
		strParams += '&'+'valParam1=' + archivo;
		strParams += '&'+'nomParam2=nomEmpresa';
		strParams += '&'+'valParam2=' + nomEmpresa;
		strParams += '&'+'nomParam3=titulo';
		strParams += '&'+'valParam3=' + "DETALLE DEL ARCHIVO DE TRANSFERENCIAS";
		
		strParams += '&'+'nomParam4=fecHoy';
		strParams += '&'+'valParam4=' + apps.SET.FEC_HOY;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		return;
	};
	
	/**********FIN FUNCIONES GENERALES ****************/

	/*********Store ************************************/

	NS.storeLlenaComboArchivos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {fecValor: NS.fecha,bChequeOcurre:''},
		paramOrder: ['fecValor', 'bChequeOcurre'],
		directFn: ControlArchivosTransferidosAction.llenaComboArchivos,
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
			},
			exception:function(misc) {
				NS.mascara.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}	
	});	
	
	NS.storeLlenaGridArchivos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {optTodas:'',optCriterios:'',fecArchivo:'',fecInicial:'',fecFinal:'',nomArchivo:'',noDocto:''},
		paramOrder: ['optTodas', 'optCriterios', 'fecArchivo', 'fecInicial', 'fecFinal', 'nomArchivo', 'noDocto'],
		directFn: ControlArchivosTransferidosAction.llenaGridArchivos,
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
				//console.log("registros obtenidos: "+ records.length);
				if (records.length == null || records.length <= 0){					
					Ext.Msg.alert('SET', 'No existen archivos con estos criterios de busqueda');
					NS.gridDetalle.store.removeAll();
					NS.gridDetalle.getView().refresh();				
				}
				NS.mascara.hide();
			},
			exception:function(misc) {
				NS.mascara.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	
	NS.storeLlenaGridDetalle = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {nomArchivo:'',idBanco:0,transmitidasHoy:false, bChequeOcurre:''},
		paramOrder: ['nomArchivo', 'idBanco', 'transmitidasHoy', 'bChequeOcurre'],
		directFn: ControlArchivosTransferidosAction.llenaGridDetalle,
		fields:
		[
		 	{name: 'noDocto'},
		 	{name: 'descEstatus'},
		 	{name: 'fecValor'},
		 	{name: 'descBanco'},
		 	{name: 'idChequera'},
		 	{name: 'descBancoBenef'},
		 	{name: 'idChequeraBenef'},
		 	{name: 'importe'},
		 	{name: 'beneficiario'},
		 	{name: 'plaza'},
		 	{name: 'sucursal'},
		 	{name: 'concepto'},
		 	{name: 'noFolioDet'},
		 	{name: 'noCliente'},
		 	{name: 'bEntregado'},
		 	{name: 'idEstatusMov'},
		 	{name: 'idDivisa'},
		 	{name: 'idBancoBenef'},
		 	{name: 'institucionFinan'},
		 	{name: 'aba'},
		 	{name: 'plazaBenef'},
		 	{name: 'layoutComerica'},
		 	{name: 'fecHoy'},
		 	{name: 'sucOrigen'},
		 	{name: 'hora'},
		 	{name: 'observacion'},
		 	{name: 'idChequeraBenefReal'},
		 	{name: 'descBancoInbursa'},
		 	{name: 'idClabeBenef'},
		 	{name: 'tipoEnvioComerica'},
		 	{name: 'idChequeraBanamex'},
		 	{name: 'idAbaSwift'},
		 	{name: 'abaSwift'},
		 	{name: 'idAbaSwiftInter'},
		 	{name: 'abaInter'},
		 	{name: 'swiftInter'},
		 	{name: 'bancoInter'},
		 	{name: 'idAbaSwiftCorres'},
		 	{name: 'abaCorres'},
		 	{name: 'swiftCorres'},
		 	{name: 'bancoCorres'},
		 	{name: 'idTipoOperacion'},
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'},
		 	{name: 'equivalePersona'},
		 	{name: 'noFactura'},
		 	{name: 'idServicioBE'},
		 	{name: 'idBancoCity'},
		 	{name: 'rfcBenef'},
		 	{name: 'fecValorOriginal'},
		 	{name: 'usuarioBital'},
		 	{name: 'servicioBital'},
		 	{name: 'rfc'},
		 	{name: 'contratoWlink'},
		 	{name: 'especiales'},
		 	{name: 'complemento'},
		 	{name: 'tipoEnvioLayoutCtas'},
		 	{name: 'tipoEnvioLayout'},
		 	{name: 'paisBenef'},
		 	{name: 'direccionBenef'},
		 	{name: 'telefonoBenef'},
		 	{name: 'referenciaBan'},
		 	{name: 'folioDetReferencia'},
		 	{name: 'idBanco'},
		 	{name: 'origenMov'},
		 	{name: 'color'},
		 	{name: 'estatusEnArchivo'},
		 	{name: 'ruta'},
		 	{name: 'horaRecibo'}
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
						if (registroDetalle[i].get('descEstatus') !== 'CANCELADO'){							
							registroTotal += 1;
							importeTotal += registroDetalle[i].get('importe');
						}	
					}
					NS.impOriginal = importeTotal;
					NS.regOriginal = registroTotal;
					Ext.getCmp(PF + 'txtRegistros').setValue(registroTotal);
					Ext.getCmp(PF + 'txtImporte').setValue(NS.formatNumber(importeTotal));
				}
				NS.mascara.hide();
			},
			exception:function(misc) {
				NS.mascara.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
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
		 	{boxLabel: 'Cheque Ocurre', name: 'optSeleccion1', inputValue: 1, width: 500, height: 30, border:1,
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
	
	//GRID DETALLE
	NS.columnaSeleccionDetalle = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	}); 
	
	NS.columnasGridDetalle = new Ext.grid.ColumnModel([	   
	   NS.columnaSeleccionDetalle,
	   {header: 'Factura', width: 70, dataIndex: 'noFactura', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Documento', width: 75, dataIndex: 'noDocto', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Estatus', width: 90, dataIndex: 'descEstatus', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Fec. Valor', width: 80, dataIndex: 'fecValor', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Banco', width: 90, dataIndex: 'descBanco', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Chequera', width: 100, dataIndex: 'idChequera', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Banco Benef.', width: 90, dataIndex: 'descBancoBenef', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Chequera Benef', width: 100, dataIndex: 'idChequeraBenef', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Clabe Benef', width: 140, dataIndex: 'idClabeBenef', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Importe', width: 80, dataIndex: 'importe', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return '$' + NS.formatNumber(value);
		   }
	   },
	   {header: 'Beneficiario', width: 170, dataIndex: 'beneficiario', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Plaza', width: 50, dataIndex: 'plaza', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Sucursal', width: 60, dataIndex: 'sucursal', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Concepto', width: 170, dataIndex: 'concepto', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Folio Det', width: 60, dataIndex: 'noFolioDet', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'No Cliente', width: 60, dataIndex: 'noCliente', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Entregado', width: 70, dataIndex: 'bEntregado', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Estatus Mov', width: 80, dataIndex: 'idEstatusMov', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Divisa', width: 50, dataIndex: 'idDivisa', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Id Banco Benef', width: 60, dataIndex: 'idBancoBenef', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Institucion Finan', width: 60, dataIndex: 'institucionFinan', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Aba', width: 60, dataIndex: 'aba', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Plaza Benef', width: 60, dataIndex: 'plazaBenef', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Layout Comerica', width: 60, dataIndex: 'layoutComerica', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Fecha', width: 100, dataIndex: 'fecHoy', sortable: true, hidden:true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Sucursal Origen', width: 60, dataIndex: 'sucOrigen', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Hora', width: 60, dataIndex: 'hora', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Observación', width: 120, dataIndex: 'observacion', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Chequera Benef Real', width: 60, dataIndex: 'idChequeraBenefReal', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Desc Banco Inbursa', width: 60, dataIndex: 'descBancoInbursa', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Envio Comerica', width: 60, dataIndex: 'tipoEnvioComerica', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Chequera Banamex', width: 60, dataIndex: 'idChequeraBanamex', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Id Aba/Swift', width: 60, dataIndex: 'idAbaSwift', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Desc Aba/Swift', width: 60, dataIndex: 'abaSwift', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Id Aba/Swift Inter', width: 60, dataIndex: 'idAbaSwiftInter', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Desc Aba Inter', width: 60, dataIndex: 'abaInter', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Desc Swift Inter', width: 60, dataIndex: 'swiftInter', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Banco Inter', width: 60, dataIndex: 'bancoInter', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Id Aba/Swift Corres', width: 60, dataIndex: 'idAbaSwiftCorres', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Desc Aba Corres', width: 60, dataIndex: 'abaCorres', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Desc Swift Corres', width: 60, dataIndex: 'swiftCorres', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Banco Corres', width: 60, dataIndex: 'bancoCorres', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Tipo Operacion', width: 90, dataIndex: 'idTipoOperacion', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'No Empresa', width: 75, dataIndex: 'noEmpresa', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Empresa', width: 170, dataIndex: 'nomEmpresa', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Persona', width: 100, dataIndex: 'equivalePersona', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Servicio BE', width: 60, dataIndex: 'idServicioBE', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'City Banco', width: 60, dataIndex: 'idBancoCity', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'RFC Benef', width: 120, dataIndex: 'rfcBenef', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Fec Valor Orig.', width: 100, dataIndex: 'fecValorOriginal', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Desc Usuario Bital', width: 60, dataIndex: 'usuarioBital', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Desc Servicio Bital', width: 60, dataIndex: 'servicioBital', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'RFC Empresa', width: 100, dataIndex: 'rfc', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Contrato', width: 60, dataIndex: 'contratoWlink', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Especiales', width: 60, dataIndex: 'especiales', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Complemento', width: 60, dataIndex: 'complemento', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Tipo Envio Layout Ctas', width: 60, dataIndex: 'tipoEnvioLayoutCtas', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Tipo Envio Layout', width: 60, dataIndex: 'tipoEnvioLayout', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Pais Benef', width: 90, dataIndex: 'paisBenef', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Dir. Benef', width: 60, dataIndex: 'direccionBenef', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Telefono Benef', width: 60, dataIndex: 'telefonoBenef', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Ref. Banco', width: 80, dataIndex: 'referenciaBan', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Folio Det. Referencia', width: 60, dataIndex: 'folioDetReferencia', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Id Banco', width: 50, dataIndex: 'idBanco', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Origen Mov', width: 70, dataIndex: 'origenMov', sortable: true, hidden: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
		   return value;
		   }
	   },
	   {header: 'Estado en el archivo', width: 190, dataIndex: 'estatusEnArchivo', sortable: true, renderer: function (value, meta, record) {meta.attr = 'style=' + record.get('color');
	   return value;
	   }
   }
	]);


	//Grids panel

	NS.gridArchivos = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridArchivos,
		id: PF + 'gridArchivos',
		name: PF + 'gridArchivos',
		cm: NS.columnasGridArchivos,
		sm: NS.columnaSeleccionArchivos,
		width: 980,
		height: 190,
		title:'Archivos',
		//frame:true,
		x:0,
		y:160,
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
	
	NS.gridDetalle = new Ext.grid.GridPanel({
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
						if (regSelec[k].get('descEstatus') != 'CANCELADO'){
							sum += regSelec[k].get('importe');
							registros = registros + 1;
						}
					}
					var importe = (Math.round((sum)*100)/100);
					Ext.getCmp(PF+'txtRegistros').setValue(registros);
					Ext.getCmp(PF+'txtImporte').setValue(NS.formatNumber(importe));
				}
			}
		}
	});

	
	//PANEL	
//	NS.panelArchivos = new Ext.form.FieldSet({
//		title: 'Archivos:',
//		x: 0,
//		y: 160,
//		width: 985,
//		height: 190,
//		layout: 'absolute',
//		autoScroll:true,
//		items:
//		[
//		 	NS.gridArchivos
//		]
//	});
	
	NS.panelCriterios = new Ext.form.FieldSet({
		text: '',
		x: 0,
		y: 35,
		width: 960,
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
		y: 360,
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
		x: 0,
		y: 0,
		width: 1500,		
		height: 700,
		layout: 'absolute',
		items:
		[
		 	NS.panelBusqueda,
		 	//NS.panelArchivos,
		 	NS.gridArchivos,
		 	NS.panelDetalle,
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 800,
		 		y: 600,
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
								//console.log(registroArchivo[0].get('nomArch'));
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
		 		y: 600,
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
		height: 2000,
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