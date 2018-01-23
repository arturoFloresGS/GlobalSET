Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Impresion.Impresion.CartaCheques');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	//NS.noEmpresa = apps.SET.noEmpresa;
	NS.fecHoy = apps.SET.FEC_HOY ;//BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
//	NS.tipoBanco = 0;
//	NS.idBanco = 0;
//	NS.aba = 0;
//	NS.descBanco = 0;
//	NS.modificar = false;
	NS.idSecuencia = 0;
	NS.matrizDatos = new Object();
	NS.modificar=false;
	NS.opciones = 'ECA';
	var tc = 0; 
	NS.tipo = '';
	NS.b=false;

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
	
	
	NS.storeChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idEmpresa:'0',idBanco:'0'},
		paramOrder: ['idEmpresa','idBanco'],
		directFn: CartasChequesAction.llenaChequera,
		fields:   
		[
		 	{name: 'idChequera'},
		 	{name: 'descChequera'}
		],
		listeners: {
			load: function(s, records) {
				
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existen chequeras');
					NS.txtBanco.setValue('');
					NS.cmbBanco.reset();
				}
			}
		}
	});
	
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idEmpresa:'0'},
		paramOrder: ['idEmpresa'],
		directFn: CartasChequesAction.llenaBanco,
		fields:   
		[
		 	{name: 'idBanco'},
		 	{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records) {
				
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existen bancos');
					NS.txtEmpresa.setValue('');
					NS.cmbEmpresa.reset();
				}
			}
		}
	});

	
	
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: ['idEmpresa','idClave','tipo','idBanco','tipoC','op', 'idChequera'],
		directFn: CartasChequesAction.llenaGrid,
		fields:
		[
		 	{name: 'idEmpresa'},
		 	{name: 'descEmpresa'},
		 	{name: 'idProveedor'},
		 	{name: 'descProveedor'}, 
		 	{name: 'fecha'},
		 	{name: 'documento'},
		 	{name: 'divisa'},
		 	{name: 'concepto'},
		 	{name: 'claveP'},
		 	{name: 'importe'},
		 	{name: 'noCheque'},
		 	{name: 'beneficiario'},
		 	{name: 'cuenta'},
		 	{name: 'idBanco'},
		 	{name: 'folio'},
		 	{name: 'op'},
		 	{name: 'idChequera'},
		 	{name: 'emision'}
		 	
		 	
		 	
		],
		listeners: {
			load: function (s, records) {
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos para la busqueda');
			}
		}
	});
	


	
		NS.storeClaveP = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {fechaIni:apps.SET.FEC_HOY,fechaFin:apps.SET.FEC_HOY},
		paramOrder: ['fechaIni','fechaFin'],
		directFn: CartasChequesAction.llenaClave,
		fields:   
		[
		 	{name: 'idClave'},
		 	{name: 'descClave'}
		],
		listeners: {
			load: function(s, records) {
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen clave de propuestas para las fechas');
			}
		}
	});
		
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeClaveP, msg: "Cargando..."});
    NS.storeClaveP.load();
		
		
	
	NS.storeAutorizaciones = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: CartasChequesAction.llenaAutorizaciones,
		fields:
		[
		 	{name: 'idSolicitante'},
		 	{name: 'descSolicitante'}
		],
		listeners: {
			load: function(s, records) {
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de Firmante');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAutorizaciones, msg: "Cargando..."});
	NS.storeAutorizaciones.load();
	
	NS.storeAutorizaciones2 = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: CartasChequesAction.llenaAutorizaciones2,
		fields:
		[
		 	{name: 'idSolicitante'},
		 	{name: 'descSolicitante'}
		],
		listeners: {
			load: function(s, records) {
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de Firmante');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAutorizaciones2, msg: "Cargando..."});
	NS.storeAutorizaciones2.load();
	
	NS.storeSolicitante = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: CartasChequesAction.llenaSolicitante,
		fields:
		[
		 	{name: 'idSolicitante'},
		 	{name: 'descSolicitante'}
		],
		listeners: {
			load: function(s, records) {
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de Solicitante');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeSolicitante, msg: "Cargando..."});
	NS.storeSolicitante.load();
	
	
	NS.storeLlenarProveedor = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'equivale_persona',
			campoDos:'',
			tabla:'persona',
			condicion:'',
			orden:'',
			registroUnico:false
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
		root: '',
		directFn: CartasChequesAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
			 {name: 'campoDos'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarProveedor, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen proveedores con ese nombre');
				}
			}
		}
	});
	
	
	NS.storeEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idClave:'0'},
		paramOrder: ['idClave'],
		directFn: CartasChequesAction.llenaEmpresa,
//		directFn: GlobalAction.llenarComboEmpresasUsuario,
		idProperty: 'idEmpresa',
		fields:
		[
		 	{name: 'idEmpresa'},
		 	{name: 'descEmpresa'}
		],
		listeners: {
			load: function(s, records) {
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de Empresa');
			}
		}
	});
	
//	NS.storeEmpresa.load();
	
	
	NS.optOpciones = new Ext.form.RadioGroup({
		id: PF + 'optOpciones',
		name: PF + 'optOpciones',
		x: 10,
		y: 80,
		columns: 5,
		items:
		[
		 	{boxLabel: 'Carta Emitidas Cheque de Cajas', name: 'optOpcionesSel', inputValue: 0, checked: true,
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							NS.opciones = 'ECA'
		 							Ext.getCmp(PF + 'certificar').setVisible(false);	
		 						}
		 						
		 						
		 						
		 						
		 						
		 						
		 					}
		 				}
		 			}
		 	},
		 	{boxLabel: 'Carta Emitidas Cheque Cert', name: 'optOpcionesSel', inputValue: 1 ,
		 		listeners: {
		 				check: {
		 					fn: function(opt, valor) {
		 						if (valor == true) {
		 							NS.opciones = 'ECE'
		 							Ext.getCmp(PF + 'certificar').setVisible(false);	
		 						}
		 						
		 						
		 						
		 					}
		 					
		 				}
		 			}
		 	},
		 	
		 	{boxLabel: 'Cheque de Cajas', name: 'optOpcionesSel', inputValue: 2,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.opciones = 'CA'
		 						Ext.getCmp(PF + 'certificar').setVisible(false);
		 					}
		 					
		 				}
		 	
		 			}
		 		}
		 	},
		 	{boxLabel: 'Cheque Certificado', name: 'optOpcionesSel', inputValue: 3,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.opciones = 'CE'
		 						Ext.getCmp(PF + 'certificar').setVisible(false);
		 					}
		 					
		 				}
		 				
		 	
		 			}
		 		}
		 	},
		 	{boxLabel: 'Certificar Cheques', name: 'optOpcionesSel', inputValue: 4,
		 		listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if(valor == true) {
		 						NS.opciones = 'CC'
		 						Ext.getCmp(PF + 'certificar').setVisible(true);
		 					}
		 					
		 				}
		 				
		 	
		 			}
		 		}
		 	}
		]
	});
	

	
	
	
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresas',
		x: 160,
		y: 25	
	});
	
	NS.txtEmpresa = new Ext.form.TextField({
		id: PF + 'txtEmpresa',
		name: PF + 'txtEmpresa',
		x: 160,
		y: 40,
		width: 50,	
		listeners: {
 			change: {
 				
 				fn: function(caja, valor) {
 										
						
 					
 				if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined){
 					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId()) === undefined ){
						caja.reset();
						funLimpiarPorEvento();
						Ext.Msg.alert('SET','Id de Empresa no valido.');
						return; 
					}	
 						BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
		 				NS.storeBanco.baseParams.idEmpresa=''+(valor);
		 				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg: "Cargando..."});
		 				NS.storeBanco.load();
		 				NS.txtBanco.setValue("");
						NS.cmbBanco.reset();
 				}
 				}
 			}
 		}	 	
		
	});
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresa,
		id: PF + 'cmbEmpresa',
		name: PF + 'cmbEmpresa',
		x: 220,
		y: 40,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idEmpresa',
		displayField: 'descEmpresa',
		autocomplete: true,
		emptyText: 'Empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg: "Cargando..."});
					NS.storeBanco.baseParams.idEmpresa=''+NS.cmbEmpresa.getValue();
					NS.storeBanco.load();
					
					NS.txtBanco.setValue("");
					NS.cmbBanco.reset();
				}
			}
		}
		
	});
	
	NS.lblChequera = new Ext.form.Label({
		text: 'Chequera',
		x:610,
		y: 25
		
	});
	
//	NS.txtChequera = new Ext.form.TextField({
//		id: PF + 'txtChequera',
//		name: PF + 'txtChequera',
//		x: 500,
//		y: 20,
//		width: 115,	
//		listeners: {
//			select: {
//				
//				fn: function(combo, valor) {
//					BFwrk.Util.updateComboToTextField(PF + 'txtChequera', NS.cmbChequera.getId());
//					
//				}
//			}
//		}
//		
//	});
	
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera,
		id: PF + 'cmbChequera',
		name: PF + 'cmbChequera',
		x: 600,
		y: 40,
		width: 160,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idChequera',
		displayField: 'idChequera',
		autocomplete: true,
		emptyText: 'Chequera',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				
				fn: function(combo, valor) {
//					BFwrk.Util.updateComboToTextField(PF + 'txtChequera', NS.cmbChequera.getId());
					
				}
			}
		}
		
	});
	
	NS.lblBanco = new Ext.form.Label({
		text: 'Banco',
		x: 380,
		y: 25		
	});
	
	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
		name: PF + 'txtBanco',
		x: 380,
		y: 40,
		width: 50,	
		listeners: {
 			change: {
 				
 				fn: function(caja, valor) {
 					
 				if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined){
 					if(NS.txtEmpresa.getValue!=''){
 						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId()) === undefined ){
 							caja.reset();
 							funLimpiarPorEvento();
 							Ext.Msg.alert('SET','Id del banco no valido.');
 							return; 
 						}
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
						NS.storeChequera.baseParams.idEmpresa=''+NS.txtEmpresa.getValue();
						NS.storeChequera.baseParams.idBanco=''+(valor);
						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg: "Cargando..."});
						NS.storeChequera.load();
						NS.cmbChequera.reset();
 					}else{
 						return Ext.Msg.alert('SET',"No se ha seleccionado una empresa"); 
 					}
 				}
 					
				}
 			}
 		}	 	
		
	});
	
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 440,
		y: 40,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idBanco',
		displayField: 'descBanco',
		autocomplete: true,
		emptyText: 'Banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				
				fn: function(combo, valor) {
					if(NS.txtEmpresa.getValue!=''){
					BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBanco.getId());
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg: "Cargando..."});
					NS.storeChequera.baseParams.idEmpresa=''+NS.txtEmpresa.getValue();
					NS.storeChequera.baseParams.idBanco=''+NS.cmbBanco.getValue();
					NS.storeChequera.load();
					NS.cmbChequera.reset();
					}else{
						return Ext.Msg.alert('SET',"No se ha seleccionado una empresa"); 
					}
				}
			}
		}
		
	});
	
	function funLimpiarPorEvento(){
		
		//NS.storeEmpresa.removeAll();
		NS.txtEmpresa.reset();
		NS.cmbEmpresa.reset();
		if(NS.cmbEmpresa=='' && NS.txtEmpresa==''){
			//NS.storeBanco.removeAll();
	        NS.txtBanco.reset();
	        NS.cmbBanco.reset();
		}else if(NS.txtBanco=='' && NS.cmbBanco==''){  
	        //NS.storeChequera.removeAll();
	        NS.cmbChequera.reset();
		}
      
	
	};
	
	NS.lblFechaIni = new Ext.form.Label({
		text: 'Fecha Inicial',
		x: 0,
		y: 3
		
	});
	
	NS.txtFechaInicial = new Ext.form.DateField({
		id: PF + 'txtFechaInicial',
		name: PF + 'txtFechaInicial',
		format:'d/m/Y',
		x: 100,
		y: 0,
		width: 150,
		value: apps.SET.FEC_HOY,
		listeners:{
        	change:{
        		fn:function(caja, valor){
					var valIni='';
						if(valor!=''){
							valIni=NS.cambiarFecha(''+valor);
						}else{
							return Ext.Msg.alert('SET',"Debe seleccionar una fecha inicial"); 
						}
					var fechaDos=NS.txtFechaFinal.getValue();	
					var valFin='';
						if(fechaDos!=''){
							valFin=NS.cambiarFecha(''+fechaDos);
						}else{
								return Ext.Msg.alert('SET',"Debe seleccionar una fecha final"); 
						}
						if(valor>fechaDos){
							Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
						}else{
					   //alert(valIni);
					   //alert(valFin);
					    	NS.storeClaveP.baseParams.fechaIni=''+valIni;
					    	NS.storeClaveP.baseParams.fechaFin=''+valFin;
					    	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeClaveP, msg: "Cargando..."});
					    	NS.storeClaveP.load();
							NS.cmbClaveP.reset();
	        			 
	        		}
					
        		 }
        	}
		}
	});
	
	NS.lblFechaFin = new Ext.form.Label({
		text: 'Fecha Final',
		x: 290,
		y: 3
		
	});
		
	NS.txtFechaFinal = new Ext.form.DateField({
		id: PF + 'txtFechaFinal',
		name: PF + 'txtFechaFinal',
		format:'d/m/Y',
		x: 380,
		y: 0,
		width: 150,
		value: apps.SET.FEC_HOY,
		listeners:{
        	change:{
        		fn:function(caja, valor){
        
					var fechaUno=NS.txtFechaInicial.getValue();
					var valIni='';
					if(fechaUno!=''){
						valIni=NS.cambiarFecha(''+fechaUno);
					}else{
							return Ext.Msg.alert('SET',"Debe seleccionar una fecha inicial"); 
					}

					var valFin='';
					if(valor!=''){
						valFin=NS.cambiarFecha(''+valor);
					}else{
							return Ext.Msg.alert('SET',"Debe seleccionar una fecha inicial"); 
					}
					if(fechaUno>valor){
						Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
					}else{
				   //alert(valIni);
				   //alert(valFin);
				    	NS.storeClaveP.baseParams.fechaIni=''+valIni;
				    	NS.storeClaveP.baseParams.fechaFin=''+valFin;
				    	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeClaveP, msg: "Cargando..."});
				    	NS.storeClaveP.load();
						NS.cmbClaveP.reset();
        			 
        		}
				}
			}
		}
		});
	
	NS.lblClaveP = new Ext.form.Label({
		text: 'Clave Propuesta',
		x: 0,
		y: 25
		
	});
	
//	NS.txtClaveP = new Ext.form.TextField({
//		id: PF + 'txtClaveP',
//		name: PF + 'txtClaveP',
//		x: 0,
//		y: 20,
//		width: 100,	
//		listeners: {
//			select: {
//				
//				fn: function(combo, valor) {
//					BFwrk.Util.updateComboToTextField(PF + 'txtClaveP', NS.cmbClaveP.getId());
//	 				NS.storeEmpresa.baseParams.idClave=''+(valor);
//					NS.storeEmpresa.load();
//					
//				}
//			}
//		}
//		
//	});
	
	NS.cmbClaveP = new Ext.form.ComboBox({
		store: NS.storeClaveP,
		id: PF + 'cmbClaveP',
		name: PF + 'cmbClaveP',
		x: 0,
		y: 40,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idClave',
		displayField: 'idClave',
		autocomplete: true,
		emptyText: 'Clave Propuesta',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
//					BFwrk.Util.updateComboToTextField(PF + 'txtClaveP', NS.cmbClaveP.getId());
//					alert(NS.cmbClaveP.getValue());
					NS.storeEmpresa.baseParams.idClave=''+NS.cmbClaveP.getValue();
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresa, msg: "Cargando..."});
					NS.storeEmpresa.load();
					NS.txtEmpresa.setValue("");
					NS.cmbEmpresa.reset();
				}
			}
		}
		
	});
	
	
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
	
	NS.lblDocumento = new Ext.form.Label({
		text: 'Documento',
		x: 720,
		y: 25
		
	});
	
	NS.txtDocumento = new Ext.form.TextField({
		id: PF + 'txtDocumento',
		name: PF + 'txtDocumento',
		x: 720,
		y: 40,
		width: 150,		
		
	});
	
	NS.lblIdentificacion = new Ext.form.Label({
		text: 'Identificacion',
		x: 300,
		y: 310
	});
	

	
	NS.txtIdentificacion = new Ext.form.TextField({
		id: PF + 'txtIdentificacion',
		name: PF + 'txtIdentificacion',
		x: 380,
		y: 310,
		width: 150,	
		readOnly: true,
		
	});
	
	
	
	NS.lblSolicitante = new Ext.form.Label({
		text: 'Nombre Solicitante',
		x: 0,
		y: 310
		
	});
	
	NS.cmbSolicitante = new Ext.form.ComboBox({
		store: NS.storeSolicitante,
		id: PF + 'cmbSolicitante',
		name: PF + 'cmbSolicitante',
		x: 100,
		y: 310,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSolicitante',
		displayField: 'descSolicitante',
		autocomplete: true,
		emptyText: 'Solicitante',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdentificacion', NS.cmbSolicitante.getId());
					
				}
			}
		}
		
	});
	
	NS.lblAutorizaciones = new Ext.form.Label({
		text: 'Autorizaciones',
		x: 560,
		y: 310
	});
	
	NS.cmbAutorizaciones = new Ext.form.ComboBox({
		store: NS.storeAutorizaciones,
		id: PF + 'cmbAutorizaciones',
		name: PF + 'cmbAutorizaciones',
		x: 650,
		y: 310,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSolicitante',
		displayField: 'descSolicitante',
		autocomplete: true,
		emptyText: 'Autorizacion',
		triggerAction: 'all',
		value: '',
		visible: false
		
	});
	
	NS.cmbAutorizaciones2 = new Ext.form.ComboBox({
		store: NS.storeAutorizaciones2,
		id: PF + 'cmbAutorizaciones2',
		name: PF + 'cmbAutorizaciones2',
		x: 835,
		y: 310,
		width: 150,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idSolicitante',
		displayField: 'descSolicitante',
		autocomplete: true,
		emptyText: 'Autorizacion',
		triggerAction: 'all',
		value: '',
		visible: false,
		
	});
	
//	NS.lblFecha = new Ext.form.Label({
//		text: 'Fecha Carta',
//		x: 260,
//		y: 290
//		
//	});
//	
//	NS.txtFecha = new Ext.form.DateField({
//		id: PF + 'txtFecha',
//		name: PF + 'txtFecha',
//		x: 340,
//		y: 290,
//		width: 80,
//		format: 'd-m-Y'		
//	});
//		
//	Ext.getCmp(PF + 'txtFecha').setValue(apps.SET.FEC_HOY);
//	
	
	NS.lblMotivo = new Ext.form.Label({
		text: 'Motivo Cancelacion',
		x: 0,
		y: 0
		
	});
	
		NS.txtMotivo = new Ext.form.TextField({
		id: PF + 'txtMotivo',
		name: PF + 'txtMotivo',
		x: 0,
		y: 10,
		width: 290,		
		
	});
		
	NS.lblFechaImp = new Ext.form.Label({
		text: 'Fecha de Impresión',
		x: 0,
		y: 350
			
	});
		
	NS.txtFechaImp = new Ext.form.DateField({
			id: PF + 'txtFechaImp',
			name: PF + 'txtFechaImp',
			format:'d/m/Y',
			x: 100,
			y: 350,
			width: 150,
			value: apps.SET.FEC_HOY,
			listeners:{
	        	change:{
	        		fn:function(caja, valor){
	        			NS.b=true;
						NS.valImp='';
							if(valor!=''){
								NS.valImp=NS.cambiarFecha(''+valor);
							}else{
								return Ext.Msg.alert('SET',"Debe seleccionar una fecha de impresión"); 
							}
	        		 }
	        	}
			}
		});

		
		NS.limpiar = function(){
			NS.storeLlenaGrid.removeAll();
			NS.gridConsulta.getView().refresh();
			Ext.getCmp(PF + 'cancelarc').setVisible(false);
//			Ext.getCmp(PF + 'reimprimir').setVisible(false);
			NS.txtEmpresa.setValue("");
			NS.cmbEmpresa.reset();
//			NS.txtChequera.setValue("");
			NS.cmbChequera.reset();
//			NS.txtClaveP.setValue("");
			NS.cmbClaveP.reset();
			NS.cmbSolicitante.reset();
			NS.txtIdentificacion.setValue("");
			NS.cmbAutorizaciones.reset();
			NS.cmbAutorizaciones2.reset();
			NS.txtBanco.setValue("");
			NS.cmbBanco.reset();
			NS.txtMotivo.setValue("");
			NS.txtFechaInicial.setValue(apps.SET.FEC_HOY);
			NS.txtFechaFinal.setValue(apps.SET.FEC_HOY);
			NS.txtFechaImp.setValue(apps.SET.FEC_HOY);
			NS.panelCancelacion.setVisible(false);	
			Ext.getCmp(PF + 'certificar').setVisible(false);
			Ext.getCmp(PF + 'visualizar').setVisible(false);
			Ext.getCmp(PF + 'cerrar').setVisible(false);
			Ext.getCmp(PF + 'cancelarc').setVisible(false);
			//NS.b=false;
//			Ext.getCmp(PF + 'reimprimir').setVisible(false);
		};
		
	
	
	NS.visualizar = function(){
		
		if (NS.opciones == 'ECE') {
			
				NS.movC=NS.txtMotivo.getValue();
				NS.tipo = 'CCERT';
			
		} else if (NS.opciones == 'ECA') {
		
				NS.movC=NS.txtMotivo.getValue();
				NS.tipo = 'CCHEQ';
			
		}else if (NS.opciones == 'CA') {
			NS.tipo = 'ACHEQ';
		}else if (NS.opciones == 'CE') {
			 NS.tipo = 'ACERT';
		}
		
		if (NS.identificador=='aceptar') {
			identificador='ace';
		} else{
			identificador='can';
		}
		
		
		
		var registroSeleccionado = NS.gridConsulta.getSelectionModel().getSelections();
			if (registroSeleccionado.length <= 0)
				Ext.Msg.alert('SET', 'Se debe de elegir un registro');
			else{	
				//if (registroSeleccionado.length <= 20){
				NS.idEmpresa= NS.gridConsulta.getSelectionModel().getSelections()[0].get('idEmpresa');
				NS.descEmpresa= NS.gridConsulta.getSelectionModel().getSelections()[0].get('descEmpresa');
				NS.idProveedor = NS.gridConsulta.getSelectionModel().getSelections()[0].get('idProveedor');
				NS.descProveedor = NS.gridConsulta.getSelectionModel().getSelections()[0].get('descProveedor');
				NS.claveP= NS.gridConsulta.getSelectionModel().getSelections()[0].get('claveP');
				NS.documento = NS.gridConsulta.getSelectionModel().getSelections()[0].get('documento');
				NS.importe = NS.gridConsulta.getSelectionModel().getSelections()[0].get('importe'); 
//				NS.tipo = NS.gridConsulta.getSelectionModel().getSelections()[0].get('tipo');
//				NS.idProveedor = NS.gridConsulta.getSelectionModel().getSelections()[0].get('importe');
				NS.idBanco= NS.txtBanco.getValue();
				NS.cuenta = NS.gridConsulta.getSelectionModel().getSelections()[0].get('cuenta');
				NS.folio = NS.gridConsulta.getSelectionModel().getSelections()[0].get('folio');
				NS.dif = ''+identificador;
				NS.idEmision= NS.gridConsulta.getSelectionModel().getSelections()[0].get('emision');
				
				CartasChequesAction.obtieneDatos(''+ NS.idEmpresa, ''+ NS.idBanco ,NS.tipo, function(resultado, e){
					
					
					
					
					if (resultado != null && resultado != '' && resultado != undefined)
					{
						
						var jsonDatos = '[{"solicitante":"' + NS.cmbSolicitante.getRawValue() + '","identificacion":"' + NS.txtIdentificacion.getValue() + 
						'","autorizacion1":"' + NS.cmbAutorizaciones.getRawValue() + 
					 	'","autorizacion11":"' + NS.cmbAutorizaciones.getValue() + '","autorizacion22":"' + NS.cmbAutorizaciones2.getValue()+
					 	'","autorizacion2":"' + NS.cmbAutorizaciones2.getRawValue() + '","claveP":"' + resultado[0].claveP + '","b1":"' + resultado[0].b1 + 
					 	'","b2":"' + resultado[0].b2 + '","b3":"' + resultado[0].b3 + '","b4":"' + resultado[0].b4 + 
					 	'","c1":"' + resultado[0].c1 + '","c2":"' + resultado[0].c2 + '","c3":"' + resultado[0].c3 + '","c4":"' + resultado[0].c4 + 
					 	'","c5":"' + resultado[0].c5 + '","c6":"' + resultado[0].c6 + '","proveedor":"' + NS.descProveedor + '","empresa":"' + NS.descEmpresa +
					 	'","calle":"' + resultado[0].calle + '","colonia":"' + resultado[0].colonia + '","ciudad":"' + resultado[0].ciudad + '","cuenta":"' + NS.cuenta +
					 	'","cp":"' + resultado[0].cp + '","tipo":"' + NS.tipo + '","banco":"' + NS.txtBanco.getValue() + '","estado":"' + resultado[0].estado + '","emision":"' + NS.idEmision +
					 	'","motivo":"' + NS.movC + '","lugarFecha":"' + resultado[0].lugarFecha + '" }]' ;
						
						NS.vecBeneficiarios = new Array ();
						for (var i = 0; i < registroSeleccionado.length; i++) {
							NS.beneficiario = {};
							NS.beneficiario.importe = registroSeleccionado[i].get('importe');
							NS.beneficiario.beneficiario = registroSeleccionado[i].get('beneficiario');
							NS.beneficiario.folio = registroSeleccionado[i].get('folio');
							NS.beneficiario.noCheque = registroSeleccionado[i].get('noCheque');
							NS.vecBeneficiarios[i] = NS.beneficiario;
						}
						
						var beneficiarios = Ext.util.JSON.encode(NS.vecBeneficiarios);
						if(NS.b==false){
							NS.valImp=apps.SET.FEC_HOY;
							NS.b=true;
						}
						CartasChequesAction.generaPDF(jsonDatos, beneficiarios,''+NS.dif,NS.tipo,NS.valImp, function(resultado, e){
							if (resultado != null && resultado != '' && resultado != undefined)
							{
								
								strParams = '?nomReporte=cartas';
								strParams += '&'+'nomParam1=rutaArchivo';
								strParams += '&'+'valParam1='+resultado;
								strParams += '&'+'nomParam2=nombreArchivo';
								strParams += '&'+'valParam2=carta';
								window.open("/SET/jsp/bfrmwrk/gnrators/abretxt.jsp"+strParams);
							}else{
								Ext.Msg.alert('SET', 'No se pudo generar la carta');
							}
							
						});
						
				
					}
					else
						Ext.Msg.alert('SET', 'No se pudo generar la carta');
				});
					NS.storeLlenaGrid.removeAll();
					NS.gridConsulta.getView().refresh();
				/*}else{
					Ext.Msg.alert('SET', 'Seleccionar maximo 20 beneficiarios');
				}*/					
			}
	};
	
	NS.validaCamposS = function(){
		 if(Ext.getCmp(PF + 'txtIdentificacion').getValue()==""){
			Ext.Msg.alert('SET',"Debe seleccionar un solicitante");
			return false;
		}
		 //Quitar la validacion para la autorizacion.
		 /*else if(Ext.getCmp(PF + 'cmbAutorizaciones').getValue()==""){
			Ext.Msg.alert('SET',"Seleccione la primera autorización");
			return false;
		}else if(Ext.getCmp(PF + 'cmbAutorizaciones2').getValue()==""){
			Ext.Msg.alert('SET',"Seleccione la segunda autorización");
			return false;
		}*/
		else{
			return true;
		}
	};

	
	NS.validaCampos = function(){
		 if(Ext.getCmp(PF+'txtFechaInicial').getValue()=="" || Ext.getCmp(PF+'txtFechaFinal').getValue()==""){
			Ext.Msg.alert('SET',"Debe seleccionar una fecha inicial y una fecha final para inicar la busqueda"); 
			return false;
		 }else if(Ext.getCmp(PF + 'cmbClaveP').getValue()==""){
			Ext.Msg.alert('SET',"Debe seleccionar la clave propuesta");
			return false;
		}else if(Ext.getCmp(PF + 'txtEmpresa').getValue()==""){
			Ext.Msg.alert('SET',"Seleccione la empresa");
			return false;
		}else if(Ext.getCmp(PF + 'txtBanco').getValue()==""){
				Ext.Msg.alert('SET',"Seleccione el banco");
				return false;
		}else if(Ext.getCmp(PF + 'cmbChequera').getValue()==""){
			Ext.Msg.alert('SET',"Seleccione la chequera");
			return false;
		}else{
			return true;
		}
	};
	
	NS.buscar = function(){
		
		if (NS.opciones =='ECA' || NS.opciones =='ECE') {
				Ext.getCmp(PF + 'visualizar').setVisible(false);
				Ext.getCmp(PF + 'cerrar').setVisible(false);
				Ext.getCmp(PF + 'cancelarc').setVisible(true);
				NS.panelCancelacion.setVisible(false);
				
		} else {
				Ext.getCmp(PF + 'cancelarc').setVisible(false);
				Ext.getCmp(PF + 'visualizar').setVisible(true);
				Ext.getCmp(PF + 'cerrar').setVisible(false);
				NS.panelCancelacion.setVisible(false);
		}
			
		if (NS.opciones == 'ECE') {
			NS.tipo = 'ACERT';
			tc = 102;
			NS.op='ECE';
		} else if (NS.opciones == 'ECA') {
			NS.tipo = 'ACHEQ';
			tc = 103;
			NS.op='ECA';
		}else if (NS.opciones == 'CA') {
			tc = 103;
			NS.tipo = 'ACHEQ';
			NS.op='CA';
		}else if (NS.opciones == 'CE') {
			 tc = 102;
			 NS.tipo = 'ACERT';
			 NS.op='CE';
		}else if(NS.opciones == 'CC'){
			 tc ='';
		     NS.tipo = '';
			 NS.op='CC'
		}
			NS.storeLlenaGrid.baseParams.idEmpresa ='' + Ext.getCmp(PF + 'txtEmpresa').getValue();
			NS.storeLlenaGrid.baseParams.idChequera ='' + Ext.getCmp(PF + 'cmbChequera').getValue();
			NS.storeLlenaGrid.baseParams.idBanco ='' + Ext.getCmp(PF + 'txtBanco').getValue(); 
			NS.storeLlenaGrid.baseParams.idClave ='' + Ext.getCmp(PF + 'cmbClaveP').getValue();
			NS.storeLlenaGrid.baseParams.tipo = ''+ tc;	
			NS.storeLlenaGrid.baseParams.tipoC = ''+ NS.tipo;
			NS.storeLlenaGrid.baseParams.op = ''+ NS.op;
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando ..."});
			NS.storeLlenaGrid.load();
			
	};
	
		NS.certificar = function(){
			var selecciones=NS.gridConsulta.getSelectionModel().getSelections();
			var mat = new Array();
			if(selecciones.length > 0){
    			for(var i = 0; i < selecciones.length; i++ ){
      				 var vecTempP ={};
       				 vecTempP.folio = selecciones[i].get('folio');
       				 mat[i] = vecTempP;				
      			}
    			var jsonFolio= Ext.util.JSON.encode(mat);
    			NS.storeLlenaGrid.baseParams.jsonFolio ='' + jsonFolio;
    			
    			CartasChequesAction.certificarCheque(jsonFolio, function(resultado, e) {
       			 if(resultado!= '' && resultado!= undefined && resultado!= null){
    				   Ext.Msg.alert('SET', resultado);
       			 }
       			 NS.limpiar();
    			});  
    			
			}else{
				Ext.Msg.alert('SET','Debe seleccionar un registro');
			}

        	
		
	};
	
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGrid = new Ext.grid.ColumnModel([
	            NS.columnaSeleccion,
	            {header: 'Id Empresa', width: 100,  dataIndex: 'idEmpresa', sortable: true, hidden:true},
	            {header: 'Empresa', width: 250,  dataIndex: 'descEmpresa', sortable: true},
	            {header: 'No. Proveedor ', width: 80,  dataIndex: 'idProveedor', sortable: true},
	            {header: 'Proveedor ', width: 250,  dataIndex: 'descProveedor', sortable: true},
	            {header: 'Fecha de Pago ', width: 85,  dataIndex: 'fecha', sortable: true},
	            {header: 'Documento', width:80 , dataIndex: 'documento', sortable: true},
	            {header: 'Divisa', width:40 , dataIndex: 'divisa', sortable: true},
	            {header: 'Concepto', width: 150 , dataIndex: 'concepto', sortable: true},
	            {header: 'Clave de Propuesta', width:120 , dataIndex: 'claveP', sortable: true},
	            {header: 'Importe', width:60 , dataIndex: 'importe', sortable: true,renderer: BFwrk.Util.rendererMoney ,align:'right'},
	            {header: 'No. Cheque', width:60 , dataIndex: 'noCheque', sortable: true},
	            {header: 'Tipo', width:60 , dataIndex: 'tipo', sortable: true, hidden:true},
	            {header: 'Beneficiario', width:60 , dataIndex: 'beneficiario', sortable: true, hidden:true},
	            {header: 'No. Cuenta', width:60 , dataIndex: 'cuenta', sortable: true},
	            {header: 'No. Folio', width:60 , dataIndex: 'folio', sortable: true, hidden:true},
	            {header: 'Id Emision', width:60 , dataIndex: 'emision', sortable: true, hidden:true},
	             ]); 
	
	
	NS.gridConsulta = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'gridConsulta',
		name: PF + 'gridConsulta',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 960,
		height: 140,
		stripeRows : true,
		columnLines: true,
		
	});
	
	
	NS.panelGrid = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 110,
		width: 985,
		height: 180,
		layout: 'absolute',
		items:
		[	
		 	NS.gridConsulta,
		]
	});
	
	
	NS.panelCancelacion = new Ext.form.FieldSet({
		title: 'Motivo Cancelacion',
		x: 340,
		y: 360,
		width: 320,
		height: 100,
		layout: 'absolute',
		items:
		[	
//		 	NS.lblMotivo,
		 	NS.txtMotivo,
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir Cancelacion',
		 		id: PF + 'cancelar',
		 		name: PF + 'cancelar',
		 		x: 85,
		 		y: 40,
		 		width: 150,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					if (NS.validaCamposS()==true) {
		 						NS.identificador='cancelar';
			 					NS.visualizar();
			 					NS.panelCancelacion.setVisible(false);
			 					NS.storeLlenaGrid.removeAll();
			 					NS.gridConsulta.getView().refresh();
			 					NS.txtMotivo.setValue('');
		 						
		 					}
		 					
		 					
		 					
		 				}
		 			}
		 		}
		 	},
		]
	});
	
	
	NS.exportaExcel = function(jsonCadena) {
		CartasChequesAction.exportaExcel(jsonCadena, function(res, e){
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}	
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=excelCartaCheques';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
	
	NS.contenedor = new Ext.form.FieldSet ({
		title: 'Cartas Cheques',
		x: 20,
		y: 5,
		width: 1010,
		height: 600,
		layout: 'absolute',
		items:
		[
		 	NS.panelGrid,
			NS.lblEmpresa,
			NS.txtEmpresa,
			NS.cmbEmpresa,
			NS.lblChequera,
//			NS.txtChequera,
			NS.cmbChequera,
			NS.lblClaveP,
//			NS.txtClaveP,
			NS.cmbClaveP,
//			NS.lblDocumento,
//			NS.txtDocumento,
			NS.lblSolicitante,
			NS.cmbSolicitante,
			NS.lblIdentificacion,
			NS.txtIdentificacion,
			NS.lblAutorizaciones,
			NS.cmbAutorizaciones,
			NS.cmbAutorizaciones2,
//			NS.lblFecha,
//			NS.txtFecha,
			NS.panelCancelacion.setVisible(false),
			NS.lblBanco,
			NS.txtBanco,
			NS.cmbBanco,
			NS.lblFechaIni,
			NS.lblFechaFin,
			NS.lblFechaImp,
			NS.txtFechaInicial,
			NS.txtFechaFinal,
			NS.txtFechaImp,
			{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		id: PF + 'buscar',
		 		name: PF + 'buscar',
		 		x: 800,
		 		y: 40,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					if (NS.validaCampos()==true) {
		 						    NS.buscar();
		 					}
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		id: PF + 'visualizar',
		 		name: PF + 'visualizar',
		 		x: 835,
		 		y: 340,
		 		width: 80,
		 		hidden:true,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					if (NS.validaCamposS()==true) {
		 						NS.identificador='aceptar';
			 					NS.visualizar();
			 					
		 					}
		 					
		 				}
		 			}
		 		}
		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cerrar',
		 		id: PF + 'cerrar',
		 		name: PF + 'cerrar',
		 		x: 835,
		 		y: 340,
		 		width: 80,
		 		hidden:true,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					
		 				}
		 			}
		 		}
		 	},
//		 	{
//		 		xtype: 'button',
//		 		text: 'Reimprimir',
//		 		id: PF + 'reimprimir',
//		 		name: PF + 'reimprimir',
//		 		x: 760,
//		 		y: 340,
//		 		width: 80,
//		 		height: 22,
//		 		hidden:true,
//		 		listeners: {
//		 			click: {
//		 				
//		 				fn: function(e)
//		 				{
//		 					
//		 					if (NS.validaCamposS()==true) {
//		 						NS.identificador='reImprimir';
//			 					NS.visualizar();
//			 					NS.storeLlenaGrid.removeAll();
//			 					NS.gridConsulta.getView().refresh();
//		 						
//		 					}
//		 					
//		 				}
//		 			}
//		 		}
//		 	},
		 	
		 	{
		 		xtype: 'button',
		 		text: 'Cancelacion',
		 		id: PF + 'cancelarc',
		 		name: PF + 'cancelarc',
		 		x: 835,
		 		y: 340,
		 		width: 80,
		 		height: 22,
		 		hidden:true,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					NS.panelCancelacion.setVisible(true);	
		 					Ext.getCmp(PF + 'cancelarc').setVisible(false);
//		 					Ext.getCmp(PF + 'reimprimir').setVisible(false);
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Limpiar',
		 		id: PF + 'limpiar',
		 		name: PF + 'limpiar',
		 		x: 835,
		 		y: 380,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					
		 					NS.limpiar();
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Certificar',
		 		id: PF + 'certificar',
		 		name: PF + 'certificar',
		 		x: 835,
		 		y: 460,
		 		hidden:true,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					
		 					NS.certificar();
		 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Excel',
		 		id: PF + 'excel',
		 		name: PF + 'excel',
		 		x: 835,
		 		y: 420,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				
		 				fn: function(e)
		 				{
		 					var matriz = new Array();
		 					var datosGrid = NS.storeLlenaGrid.data.items;
		 					for(var i=0;i<datosGrid.length;i++){
		 						var vector = {};
		 						
								vector.idEmpresa = datosGrid[i].get('idEmpresa');
		 						vector.descEmpresa = datosGrid[i].get('descEmpresa');
		 						vector.idProveedor = datosGrid[i].get('idProveedor');
		 						vector.descProveedor = datosGrid[i].get('descProveedor');
		 						vector.fecha = datosGrid[i].get('fecha');
		 						vector.documento = datosGrid[i].get('documento');
		 						vector.divisa = datosGrid[i].get('divisa');
		 						vector.concepto = datosGrid[i].get('concepto');
		 						vector.claveP = datosGrid[i].get('claveP');
		 						vector.importe = datosGrid[i].get('importe');
		 						vector.noCheque = datosGrid[i].get('noCheque');
		 						vector.emision = datosGrid[i].get('emision');			
		 						matriz[i] = vector;	
		 					}
		 					
		 					var jSonString = Ext.util.JSON.encode(matriz);
		 					NS.exportaExcel(jSonString);	
		 				}
		 			}
		 		}
		 	},
//		 	 {
//		    	   xtype: 'button',
//		    	   text: '...',
//		    	   x: 470,
//		   		   y: 20,
//		   		   width: 20,
//		    	   handler: function(){
//		    		   	if(NS.parametroBus==='' || (isNaN(NS.parametroBus) && NS.parametroBus.length < 4)) {
//            				Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
//	               		} else{
//	               			NS.storeLlenarProveedor.baseParams.condicion=NS.parametroBus;
//	               			NS.storeLlenarProveedor.load();
//	               		}
//		    	   }
//		       },
		 	NS.optOpciones,
		 ]
	});
	
	
	
	
	NS.Global = new Ext.FormPanel ({
		title: 'Cartas Cheques',
		width: 1300,
		height: 606,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'global',
		name: PF + 'global',
		renderTo: NS.tabContId,
		items: [
		 	
		NS.contenedor,
		
		 
		]
	});
	NS.Global.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	

	

	
});