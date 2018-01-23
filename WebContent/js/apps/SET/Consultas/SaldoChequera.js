Ext.onReady(function(){
 	//@autor Sergio Vaca //Modificaciones by GGC (crar) 06/10/2011
	var NS = Ext.namespace('apps.SET.Consultas.SaldoChequera');
    // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
    NS.tabContId = apps.SET.tabContainerId;

    var PF = apps.SET.tabID + '.';
    // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);

    //Para recibir del modulo global
    NS.GI_ID_USUARIO = apps.SET.iUserId;
    NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
 	NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
 	//Para el modulo global
 	
 	NS.OptDetalle = true;
 	NS.banderaP = false; 
 	NS.myMask=null;
 	
 	
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
 	
 	
   // CUSTOM RENDER FUNCTION
   var myRendererMoney  = function(val){ // renderer: BFwrk.Util.rendererMoney
     if(val > 0)
     {
		 return '<span style="color:black;align:right;">$' + Ext.util.Format.number( val, ' $?0,000.00?' ) + '</span>';
     }
     else if(val < 0)
     {
		 return '<span style="color:red;align:right;">$' + Ext.util.Format.number( val, ' $?0,000.00?' )  + '</span>';
     }
     return val;
   };
 	
 	NS.storeFechaHoy = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		directFn: ConsultasAction.obtenerFechaHoy,
		idProperty: 'fechaHoy',
		fields: [
			{name: 'fechaHoy'}
		],
		listeners: {
			load: function(s, records){
				if(records.length>0){
					Ext.getCmp(PF+'shFecha').setValue(records[0].get('fechaHoy'));
				}
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFechaHoy, msg:"Cargando..."});
			}
		}
	}); 

 	NS.storeFechaHoy.load();
	
	NS.limpiar = function(){
		NS.banderaP=false;
		
		NS.cmbEmpresa.reset();
		NS.cmbBanco.reset();			
		NS.cmbChequera.reset();
		Ext.getCmp(PF + 'txtNoEmpresa').reset();
		Ext.getCmp(PF+'saldoInicialText').setValue('0.0');
		Ext.getCmp(PF+'cargosText').setValue('0.0');
		Ext.getCmp(PF+'abonosText').setValue('0.0');
		Ext.getCmp(PF+'saldoFinalText').setValue('0.0');
		Ext.getCmp(PF+'dChequera').setValue('0.0');
		Ext.getCmp(PF+'abonoSBC').setValue('0.0');
		Ext.getCmp(PF+'pRealizar').setValue('0.0');
		NS.gridDetalleAbono.store.removeAll();
		NS.gridDetalleCargo.store.removeAll();
		NS.gridDetalleAbonoSBC.store.removeAll();
		NS.gridDetalleCheques.store.removeAll();
		NS.gridDetalleAbono.getView().refresh();
		NS.gridDetalleCargo.getView().refresh();
		NS.gridDetalleCheques.getView().refresh();
		NS.gridDetalleAbonoSBC.getView().refresh();
		Ext.getCmp(PF+'txtRegistros').setValue('0');
		Ext.getCmp(PF+'txtTotal').setValue('0.0');
		Ext.getCmp(PF+'cargoAbonoLabel').setText('');
		Ext.getCmp(PF+'saldoIniBancoText').setValue('0.0');
       	Ext.getCmp(PF+'saldoFinBancoText').setValue('0.0');
		//Ext.getCmp(PF+'shFecha').setValue('');
		//Ext.getCmp(PF+'lActualizacion').setText('');
	}
	

	NS.storeComboEmpresa = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		directFn: ConsultasAction.llenarComboEmpresa,
		idProperty: 'idEmpresa',
		fields: [
			{name: 'idEmpresa'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComboEmpresa, msg:"Cargando..."});
				if(records.length<=0){
					Ext.Msg.alert('SET','El usuario no tiene empresas asignadas');
					return;
				}
				//NS.cmbEmpresa.setValue(NS.GI_ID_EMPRESA); 
				NS.storeBancoCombo.baseParams.idEmpresa=NS.GI_ID_EMPRESA;
				NS.storeBancoCombo.sort('descripcion','ASC');
				NS.storeBancoCombo.load();
			}
		}
	}); 

	NS.storeComboEmpresa.load();
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeComboEmpresa,
		name: PF+'cmbEmpresa',
		id: PF+'cmbEmpresa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		x: 80,
        y: 45,
		width: 260,
		valueField:'idEmpresa',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{ //evento click
				fn:function(combo, value) {
					NS.cmbBanco.setValue('');
					NS.cmbChequera.setValue('');
					NS.storeBancoCombo.removeAll();
					NS.storeChequeraCombo.removeAll();
					// MAFJ 10 marzo 2014
					// se agrego para limpiar casillas cuando se cambia de empresa
					NS.gridDetalleAbono.store.removeAll();
					NS.gridDetalleCargo.store.removeAll();
        			NS.gridDetalleAbonoSBC.store.removeAll();
        			NS.gridDetalleCheques.store.removeAll();
        			//----------------
					NS.gridDetalleAbono.getView().refresh();
					NS.gridDetalleCargo.getView().refresh();
        			NS.gridDetalleAbonoSBC.getView().refresh();
					NS.gridDetalleCheques.getView().refresh();
					// fin MAFJ
					Ext.getCmp(PF+'saldoInicialText').setValue('');
					Ext.getCmp(PF+'cargosText').setValue('');
					Ext.getCmp(PF+'abonosText').setValue('');
					Ext.getCmp(PF+'saldoFinalText').setValue('');
					Ext.getCmp(PF+'cargoAbonoLabel').setText('');
					Ext.getCmp(PF+'saldoIniBancoText').setValue('');
                   	Ext.getCmp(PF+'saldoFinBancoText').setValue('');
				 	
                    // MAFJ 10 marzo 2014
					// se agrego para limpiar casillas cuando se cambia de empresa
                   	Ext.getCmp(PF+'pRealizar').setValue('');
					Ext.getCmp(PF+'dChequera').setValue('');
					Ext.getCmp(PF+'abonoSBC').setValue('');
					Ext.getCmp(PF+'txtRegistros').setValue('');
					Ext.getCmp(PF+'txtTotal').setValue('');
					// fin MAFJ
					
					NS.GI_ID_EMPRESA = combo.getValue();
					//Carga del store
					NS.storeBancoCombo.baseParams.idEmpresa = parseInt(combo.getValue());
					NS.storeBancoCombo.load();
					NS.storeBancoCombo.sort('descripcion','ASC');
					//crar
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}
	});
	
	
	
	NS.storeBancoCombo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:
			{
				idEmpresa: 0
			},
		paramOrder:
			[
				'idEmpresa'
			],
		directFn: ConsultasAction.llenarComboBanco,
		idProperty: 'id',
		fields: [
			{name: 'id'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoCombo, msg:"Cargando..."});
			}
		}
	}); 
	
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancoCombo,
		name: PF+'cmbBanco',
		id: PF+'cmbBanco',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		x: 440,
        y: 20,
		width: 190,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione Banco',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{ //evento click
				fn:function(combo, value) {
					Ext.getCmp(PF+'lActualizacion').hide();
					NS.storeChequeraCombo.removeAll();
					NS.cmbChequera.setValue('');
					NS.storeChequeraCombo.baseParams.idEmpresa = NS.GI_ID_EMPRESA;
					NS.storeChequeraCombo.baseParams.idBanco = combo.getValue();
					NS.storeChequeraCombo.sort('idChequera','ASC');
					NS.storeChequeraCombo.load();
					Ext.getCmp(PF+'saldoInicialText').setValue('');
					Ext.getCmp(PF+'cargosText').setValue('');
					Ext.getCmp(PF+'abonosText').setValue('');
					Ext.getCmp(PF+'saldoFinalText').setValue('');
					Ext.getCmp(PF+'cargoAbonoLabel').setText('');
					Ext.getCmp(PF+'abonoSBC').setValue('');
                   	Ext.getCmp(PF+'dChequera').setValue('');
                   	Ext.getCmp(PF+'pRealizar').setValue('');
                   	Ext.getCmp(PF+'saldoFinBancoText').setValue('');
                	Ext.getCmp(PF+'saldoIniBancoText').setValue('');
                   
				}
			}
		}
	});
	
 	NS.storeChequeraCombo = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			idEmpresa: NS.GI_ID_EMPRESA,
			idBanco: -1
		},
		root: '',
		paramOrder:['idBanco','idEmpresa'],
		directFn: ConsultasAction.llenarComboChequera,
		idProperty: 'idChequera',
		fields: [
			{name: 'idChequera'},
			{name: 'descripcion'},
			{name: 'idBanco'},
			{name: 'saldo'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraCombo, msg:"Cargando..."});
			}
		}
	});

 	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequeraCombo,
		name: PF+'cmbChequera',
		id: PF+'cmbChequera',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		x: 440,
        y: 60,
		width: 190,
		valueField:'idChequera',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione Chequera',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{ //evento click
				fn:function(combo, value) {
					Ext.getCmp(PF+'txtRegistros').setValue('0');
					Ext.getCmp(PF+'txtTotal').setValue('0.00');
					NS.gridDetalleAbono.store.removeAll();
					NS.gridDetalleCargo.store.removeAll();
					NS.gridDetalleCheques.store.removeAll();
					NS.gridDetalleAbonoSBC.store.removeAll();
					NS.gridDetalleAbono.getView().refresh();
					NS.gridDetalleCargo.getView().refresh();
					NS.gridDetalleCheques.getView().refresh();
					NS.gridDetalleAbonoSBC.getView().refresh();
					Ext.getCmp(PF+'lActualizacion').hide();
					NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
					NS.myMask.show();
					
					//Parametros para storeDatosSaldos
					NS.storeDatosSaldos.baseParams.noEmpresa = NS.GI_ID_EMPRESA;
					NS.storeDatosSaldos.baseParams.idBanco = Ext.getCmp(PF+'cmbBanco').getValue(); 
					NS.storeDatosSaldos.baseParams.descBanco = ''+NS.storeBancoCombo.getById(Ext.getCmp(PF+'cmbBanco').getValue()).get('descripcion');
					NS.storeDatosSaldos.baseParams.idChequera =''+combo.getValue();
					//Carga o Ejecuta storeDatosSaldos
					NS.storeDatosSaldos.load();
					
					//Parametros para storeSaldoIniBanco
					NS.storeSaldoIniBanco.baseParams.noEmpresa = NS.GI_ID_EMPRESA;
					NS.storeSaldoIniBanco.baseParams.idBanco = Ext.getCmp(PF+'cmbBanco').getValue(); 
					NS.storeSaldoIniBanco.baseParams.descBanco = ''+NS.storeBancoCombo.getById(Ext.getCmp(PF+'cmbBanco').getValue()).get('descripcion');
					NS.storeSaldoIniBanco.baseParams.idChequera =''+combo.getValue();
					//Carga o Ejecuta storeSaldoIniBanco
					NS.storeSaldoIniBanco.load();
					
					//Parametros para storeDatosPago
					NS.storeDatosPago.baseParams.noEmpresa = NS.GI_ID_EMPRESA;
					NS.storeDatosPago.baseParams.idBanco = Ext.getCmp(PF+'cmbBanco').getValue(); 
					NS.storeDatosPago.baseParams.fecValor = Ext.getCmp(PF+'shFecha').getValue();
					NS.storeDatosPago.baseParams.idChequera =''+combo.getValue();
					//Carga o Ejecuta storeDatosPago
					NS.storeDatosPago.load();
					
					//Parametros para storeFechaActualizacion
					NS.storeFechaActualizacion.baseParams.noEmpresa = NS.GI_ID_EMPRESA;
					NS.storeFechaActualizacion.baseParams.idBanco = Ext.getCmp(PF+'cmbBanco').getValue(); 
					NS.storeFechaActualizacion.baseParams.idChequera =''+combo.getValue();
					//Carga o Ejecuta storeFechaActualizacion
					NS.storeFechaActualizacion.load();
					
				}
			}
		}
	});
	
	NS.storeFechaActualizacion = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			noEmpresa: -1,
			idBanco: -1,
			idChequera: ''
		},
		root: '',
		paramOrder:['noEmpresa', 'idBanco', 'idChequera'],
		directFn: ConsultasAction.seleccionarFechaActualizacion,
		idProperty: 'fechaActualizacion',
		fields: [
			{name: 'fechaActualizacion'}
		],
		listeners: {
			load: function(s, records){
				//Checolast
				if(records.length>0){
					Ext.getCmp(PF+'lActualizacion').setText('Ultima Actualización de B.E:'+records[0].get('fechaActualizacion'));
					Ext.getCmp(PF+'lActualizacion').show();
				}
			}
		}
	});
	
	
	NS.storeSaldoIniBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			noEmpresa: -1,
			idBanco: -1,
			descBanco: '',
			idChequera: ''
		},
		root: '',
		paramOrder:['noEmpresa', 'idBanco', 'descBanco', 'idChequera'],
		directFn: ConsultasAction.obtenerSaldoInicialBanco,  
		//sobre cat_cta_banco
		idProperty: 'saldoInicial',
		fields: [
			{name: 'saldoIniBanco'}
			
		],
		listeners: {
			load: function(s, records){
				if(records.length>0){
					
					Ext.getCmp(PF+'saldoIniBancoText').setValue(NS.formatNumber(records[0].get('saldoIniBanco').toFixed(2)));
				
					//Ext.Msg.alert("saldos",records[0].get('saldoIniBanco')+" "+records[0].get('saldoFinBanco'));
				}
				else{
					
				}
			}
		}
	});
	
	NS.storeDatosSaldos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			noEmpresa: -1,
			idBanco: -1,
			descBanco: '',
			idChequera: ''
		},
		root: '',
		paramOrder:['noEmpresa', 'idBanco', 'descBanco', 'idChequera'],
		directFn: ConsultasAction.seleccionarDatosChequera,  
		//sobre cat_cta_banco
		idProperty: 'saldoInicial',
		fields: [
			{name: 'saldoInicial'},
			{name: 'saldoFinal'},
			{name: 'cargo'},
			{name: 'abono'}, 
			{name: 'cargoSbc'},
			{name: 'abonoSbc'},
			{name: 'saldoFinBanco'},
			
			
		],
		listeners: {
			load: function(s, records){
				if(records.length>0){
					Ext.getCmp(PF+'saldoInicialText').setValue(NS.formatNumber(records[0].get('saldoInicial').toFixed(2)));
					Ext.getCmp(PF+'cargosText').setValue(NS.formatNumber(records[0].get('cargo').toFixed(2)));
					Ext.getCmp(PF+'abonosText').setValue(NS.formatNumber(records[0].get('abono').toFixed(2)));
					Ext.getCmp(PF+'saldoFinalText').setValue(NS.formatNumber(records[0].get('saldoFinal').toFixed(2)));
					Ext.getCmp(PF+'abonoSBC').setValue(NS.formatNumber(records[0].get('abonoSbc').toFixed(2)));
					Ext.getCmp(PF+'saldoFinBancoText').setValue(NS.formatNumber(records[0].get('saldoFinBanco').toFixed(2)));
					//Ext.Msg.alert("saldos",records[0].get('saldoIniBanco')+" "+records[0].get('saldoFinBanco'));
				}
				else{
					Ext.Msg.alert('SET','No existe información'); 
					NS.cmbBanco.setValue('');
					NS.cmbChequera.setValue('');
					NS.storeChequeraCombo.removeAll();
					NS.cmbChequera.setValue('');
					Ext.getCmp(PF+'saldoInicialText').setValue('');
					Ext.getCmp(PF+'cargosText').setValue('');
					Ext.getCmp(PF+'abonosText').setValue('');
					Ext.getCmp(PF+'saldoFinalText').setValue('');
					Ext.getCmp(PF+'cargoAbonoLabel').setValue('');
					Ext.getCmp(PF+'abonoSBC').setValue('');
                   	Ext.getCmp(PF+'dChequera').setValue('');
                   	Ext.getCmp(PF+'pRealizar').setValue('');
                   	Ext.getCmp(PF+'saldoIniBancoText').setValue('');
                   	Ext.getCmp(PF+'saldoFinBancoText').setValue('');
				}
			}
		}
	});
	
	
	
	NS.storeDatosPago = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			noEmpresa: -1,
			idBanco: -1,
			idChequera: '',
			fecValor: ''
		},
		root: '',
		paramOrder:['noEmpresa', 'idBanco', 'idChequera', 'fecValor'],
		directFn: ConsultasAction.seleccionarImporteTotalMovimiento,
		idProperty: 'cheques',
		fields: [
			{name: 'cheques'}
		],
		listeners: {
			load: function(s, records){
			if(records.length>0){
					Ext.getCmp(PF+'pRealizar').setValue(NS.formatNumber(records[0].get('cheques').toFixed(2)));
					var tfinal = NS.unformatNumber(Ext.getCmp(PF+'saldoFinalText').getValue());
					var pagos = NS.unformatNumber(Ext.getCmp(PF+'pRealizar').getValue());
					var disponible = tfinal + pagos;
				
       			    Ext.getCmp(PF+'dChequera').setValue(NS.formatNumber(disponible.toFixed(2))); 
					NS.myMask.hide();
				}
			}
		}
	});
	
	// Store storeGridAbono
	NS.storeGridAbono = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			fecha: '',
			idEmpresa: '',
			idBanco: -1,
			idChequera: ''
		},
		root: '',
		paramOrder:['fecha', 'idEmpresa', 'idBanco', 'idChequera'],
		directFn: ConsultasAction.seleccionarMvtosAbonosChequera,
		//idProperty: 'noDocto',
		fields: [
			{name: 'noDocto'},
			{name: 'concepto'},
			{name: 'importe'},
			{name: 'beneficiario'}, 
			{name: 'noFolioDet'},
			{name: 'descFormaPago'},
			{name: 'observacion'}
			
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridAbono, msg:"Cargando..."});
				if(records.length>0){
                	Ext.getCmp(PF+'txtRegistros').setValue(records.length);
  					var total=0;
  					for(var i=0; i<records.length; i = i+1){
  						total = total + records[i].get('importe');
  					}
  					Ext.getCmp(PF+'txtTotal').setValue(NS.formatNumber(NS.darFormato(total).toFixed(2)));
  				}
  				else{
  					Ext.Msg.alert('SET','¡No Existe Detalle de Igresos!');
  				}
			}
		}
	});
	
	// Store storeGridCargo
	NS.storeGridCargo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			fecha: '',
			idEmpresa: '',
			idBanco: -1,
			idChequera: ''
		},
		root: '',
		paramOrder:['fecha', 'idEmpresa', 'idBanco', 'idChequera'],
		directFn: ConsultasAction.seleccionarMvtosCargosChequera,
		//idProperty: 'noDocto',
		fields: [
			{name: 'noDocto'},
			{name: 'concepto'},
			{name: 'importe'},
			{name: 'beneficiario'}, 
			{name: 'noFolioDet'},
			{name: 'descFormaPago'},
			{name: 'observacion'}
			
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridCargo, msg:"Cargando..."});
				if(records.length>0){
                	Ext.getCmp(PF+'txtRegistros').setValue(records.length);
  					var total=0;
  					for(var i=0; i<records.length; i = i+1){
  						total = total + records[i].get('importe');
  					}
  					Ext.getCmp(PF+'txtTotal').setValue(NS.formatNumber(NS.darFormato(total).toFixed(2)));
  				}
  				else{
  					Ext.Msg.alert('SET','¡No Existe Detalle de Cargos!');
  				}
			}
		}
	});
	
	// Store storeGridAbonoSBC
	NS.storeGridAbonoSBC = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			idEmpresa: '',
			idBanco: -1,
			idChequera: ''
		},
		root: '',
		paramOrder:['idEmpresa', 'idBanco', 'idChequera'],
		directFn: ConsultasAction.seleccionarAbonoSBC,
		idProperty: 'noDocto',
		fields: [
			{name: 'noDocto'},
			{name: 'concepto'},
			{name: 'importe'},
			{name: 'beneficiario'}, 
			{name: 'noFolioDet'}
		],
		listeners: {
			load: function(s, records){
				if(records.length>0){
					Ext.getCmp(PF+'txtRegistros').setValue(records.length);
  				}
  				else{
  					Ext.Msg.alert('SET','¡No Existe Detalle de Abonos!');
  				}
			}
		}
	});
	
	// Store storeSumaAbonoSBC 
	NS.storeSumaAbonoSBC = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			fecha: '',
			idEmpresa: '',
			idBanco: -1,
			idChequera: ''
		},
		root: '',
		paramOrder:['fecha', 'idEmpresa', 'idBanco', 'idChequera'],
		directFn: ConsultasAction.seleccionarMvtosCargosChequera,
		idProperty: 'cheques',
		fields: [
			{name: 'cheques'}
		],
		listeners: {
			load: function(s, records){
				if(records.length>0){
  					Ext.getCmp(PF+'txtTotal').setValue(NS.formatNumber(NS.darFormato(records[0].get('cheques').toFixed(2))));
  				}
  				else{
  					Ext.Msg.alert('SET','¡No Existe Detalle de Cargos!');
  				}
			}
		}
	});

	
	// Store storeChequesPorEnt
	NS.storeChequerasPorEnt = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			fecha: '',
			idEmpresa: '',
			idBanco: -1,
			idChequera: ''
		},
		root: '',
		paramOrder:['fecha', 'idEmpresa', 'idBanco', 'idChequera'],
		directFn: ConsultasAction.seleccionarPorEnt,
		//idProperty: 'noDocto',
		fields: [
			{name: 'noDocto'},
			{name: 'concepto'},
			{name: 'importe'},
			{name: 'beneficiario'}, 
			{name: 'noFolioDet'},
			{name: 'descFormaPago'},
			{name: 'observacion'}
			
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridAbono, msg:"Cargando..."});
				if(records.length>0){
                	Ext.getCmp(PF+'txtRegistros').setValue(records.length);
  					var total=0;
  					for(var i=0; i<records.length; i = i+1){
  						total = total + records[i].get('importe');
  					}
  					Ext.getCmp(PF+'txtTotal').setValue(NS.formatNumber(NS.darFormato(total).toFixed(2)));
  				}
  				else{
  					Ext.Msg.alert('SET','¡No hay Cheques en Tránsito!');
  				}
			}
		}
	});
	
	
	//Grid de datos abono
	NS.gridDetalleAbono = new Ext.grid.GridPanel({
		store : NS.storeGridAbono,
		height : 220,
		width :690,
		x :5,
		y :20,
		title :'',
		columns : [ 
			{
				id :'noDoctoGrid',
				header :'Docto.',
				width :80,
				sortable :true,
				dataIndex :'noDocto'
			},{
				id :'conceptoGrid',
				header :'Descripcion',
				width :200,
				sortable :true,
				dataIndex :'concepto'
			},{
				header :'Importe',
				align:'right',
				width :90,
				sortable :true,
				dataIndex:'importe',
				stripeRows: true,
				css: 'text-align:right;',
				renderer: function(dato){
					return NS.formatNumber(dato);
				}
			},{
				header: 'Beneficiario',
				width: 180,
				sortable: true,
				dataIndex:'beneficiario'
			},{
				header: 'Folio Det',
				width: 80,
				sortable: true,
				dataIndex:'noFolioDet'
			},{
				header: 'Forma de Pago',
				width: 120,
				sortable: true,
				dataIndex:'descFormaPago'
			},{
				header: 'Observaciones',
				width: 120,
				sortable: true,
				dataIndex:'observacion',
				hidden: true
			}
		]
	});
	
	//Grid de datos Cargo
	NS.gridDetalleCargo = new Ext.grid.GridPanel({
		store : NS.storeGridCargo,
		height : 220,
		width :690,
		x :5,
		y :20,
		title :'',
		hidden: false,
		columns : [ 
			{
				id :'noDoctoGrid',
				header :'Docto.',
				width :80,
				sortable :true,
				dataIndex :'noDocto'
			},{
				id :'conceptoGrid',
				header :'Descripcion',
				width :200,
				sortable :true,
				dataIndex :'concepto'
			},{
				header :'Importe',
				align:'right',
				width :90,
				sortable :true,
				dataIndex :'importe',
				css: 'text-align:right;',
				renderer: function(dato){
					return NS.formatNumber(dato);
				}
			},{
				header: 'Beneficiario',
				width: 180,
				sortable: true,
				dataIndex:'beneficiario'
			},{
				header: 'Folio Det',
				width: 80,
				sortable: true,
				dataIndex:'noFolioDet'
			},{
				header: 'Forma de Pago',
				width: 120,
				sortable: true,
				dataIndex:'descFormaPago'
			},{
				header: 'Observaciones',
				width: 120,
				sortable: true,
				dataIndex:'observacion',
				hidden: true
			}
		]
	});
	
	//Grid de datos AbonoSBC
	NS.gridDetalleAbonoSBC = new Ext.grid.GridPanel({
		store : NS.storeGridAbonoSBC,
		height : 220,
		width :690,
		x :5,
		y :20,
		title :'',
		hidden: false,
		columns : [ 
			{
				id :'noDoctoGrid',
				header :'Docto.',
				width :80,
				sortable :true,
				dataIndex :'noDocto'
			},{
				id :'conceptoGrid',
				header :'Descripcion',
				width :150,
				sortable :true,
				dataIndex :'concepto'
			},{
				header :'Importe',
				align:'right',
				width :90,
				sortable :true,
				dataIndex :'importe',
				css: 'text-align:right;',
				renderer: function(dato){
					return NS.formatNumber(dato);
				}
			},{
				header: 'Beneficiario',
				width: 180,
				sortable: true,
				dataIndex:'beneficiario'
			},{
				header: 'Folio Det',
				width: 80,
				sortable: true,
				dataIndex:'noFolioDet'
			},{
				header: 'Forma de Pago',
				width: 120,
				sortable: true,
				dataIndex:'descFormaPago'
			}
		]
	});
	

	//Grid PRUEBAS CHEQUES TRANSITO
	NS.gridDetalleCheques = new Ext.grid.GridPanel({
		store : NS.storeChequerasPorEnt,
		height : 220,
		width :690,
		x :5,
		y :20,
		title :'',
		hidden: false,
		columns : [ 
			{
				id :'noDoctoGrid',
				header :'Docto.',
				width :80,
				sortable :true,
				dataIndex :'noDocto'
			},{
				id :'conceptoGrid',
				header :'Descripcion',
				width :150,
				sortable :true,
				dataIndex :'concepto'
			},{
				header :'Importe',
				align:'right',
				width :90,
				sortable :true,
				dataIndex :'importe',
				css: 'text-align:right;',
				renderer: function(dato){
					return NS.formatNumber(dato);
				}
			},{
				header: 'Beneficiario',
				width: 180,
				sortable: true,
				dataIndex:'beneficiario'
			},{
				header: 'Folio Det',
				width: 80,
				sortable: true,
				dataIndex:'noFolioDet'
			},{
				header: 'Forma de Pago',
				width: 120,
				sortable: true,
				dataIndex:'descFormaPago'
			}
		]
	});
	

	NS.lblEmpresaImprimir = new Ext.form.Label({
		text: 'Empresa:',
		x: 0,
		y: 0	
	});
	
	NS.txtNoEmpresaImprimir = new Ext.form.TextField({
		id: PF + 'txtNoEmpresaImprimir',
		name: PF + 'txtEmpresaImprimir',
		x: 0,
		y: 15,
		width: 50,
		disabled: true
	});
	
	NS.txtEmpresaImprimir = new Ext.form.TextField({
		id: PF + 'txtEmpresaImprimir',
		name: PF + 'txtEmpresaImprimir',
		x: 75,
		y: 15,
		width: 300,
		disabled: true
	});
	
	NS.lblBancoImprimir = new Ext.form.Label({
		text: 'Banco:',
		x: 0,
		y: 50		
	});
	
	NS.storeBancoImprimir = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:
			{
				idEmpresa: 0
			},
		paramOrder:
			[
				'idEmpresa'
			],
		directFn: ConsultasAction.llenarComboBanco,
		idProperty: 'id',
		fields: [
			{name: 'id'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoCombo, msg:"Cargando..."});
			}
		}
	}); 
	
	NS.txtBancoImprimir = new Ext.form.TextField({
		id: PF + 'txtBancoImprimir',
		name: PF + 'txtBancoImprimir',
		x: 0,
		y: 65,
		width: 55,
		listeners: {
 			change: {
 				fn: function(caja, valor) {				
 				if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined){
 						if( BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoImprimir',NS.cmbBancoImprimir.getId()) == undefined ){
 							caja.reset();
 							Ext.Msg.alert('SET','Id del banco no valido.');
 							return; 
 						}
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtBancoImprimir', NS.cmbBancoImprimir.getId());
						NS.storeChequeraImprimir.baseParams.idEmpresa=parseInt(Ext.getCmp(PF + 'txtNoEmpresaImprimir').getValue());
						NS.storeChequeraImprimir.baseParams.idBanco= parseInt(caja.getValue());
						NS.storeChequeraImprimir.load();
						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraImprimir, msg: "Cargando..."});
						NS.storeChequeraImprimir.load();
 				}
 					
				}
 			}
 		}		
	});
	
	NS.cmbBancoImprimir = new Ext.form.ComboBox({
		store: NS.storeBancoImprimir,
		id: PF + 'cmbBancoImprimir',
		name: PF + 'cmbBancoImprimir',
		x: 75,
		y: 65,
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione Banco',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtBancoImprimir', NS.cmbBancoImprimir.getId());
					NS.storeChequeraImprimir.baseParams.idEmpresa=parseInt(Ext.getCmp(PF + 'txtNoEmpresaImprimir').getValue());
					NS.storeChequeraImprimir.baseParams.idBanco= parseInt(combo.getValue());
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraImprimir, msg: "Cargando..."});
					NS.storeChequeraImprimir.load();
				}
			}
		}
		
	});
	
	NS.lblChequera = new Ext.form.Label({
		text: 'Chequera:',
		x: 0,
		y: 110		
	});
	
	NS.storeChequeraImprimir = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			idEmpresa: 0,
			idBanco: 0
		},
		root: '',
		paramOrder:['idBanco','idEmpresa'],
		directFn: ConsultasAction.llenarComboChequera,
		idProperty: 'idChequera',
		fields: [
			{name: 'idChequera'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	});

	
	NS.cmbChequeraImprimir = new Ext.form.ComboBox({
		store: NS.storeChequeraImprimir,
		name: PF+'cmbChequera',
		id: PF+'cmbChequeraImprimir',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		x: 75,
        y: 110,
		width: 180,
		valueField:'idChequera',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione Chequera',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{ //evento click
				fn:function(combo, value) {
					
				}
			}
		}
	});
	
	NS.lblTipoChequera = new Ext.form.Label({
		text: 'Tipo Chequera:',
		x: 0,
		y: 150		
	}); 
	
	NS.storeTipoChequerasImprimir = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: MantenimientoChequerasAction.obtieneTipoChequeras, 
		idProperty: 'idString', 
		fields: [
			 {name: 'idString'},
			 {name: 'descChequera'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTipoChequerasImprimir, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No tiene tipos de chequeras dadas de alta');
				
				//Se agrega la opcion todas a storeGrupoEmpresas
	 			/*var recordsStoreGruEmp = NS.storeTipoChequerasImprimir.recordType;	
				var todas = new recordsStoreGruEmp({
					idString : 'T',
					descChequera : '***TODAS***'
		       	});
				NS.storeTipoChequerasImprimir.insert('T',todas);*/
			}
		}
	}); 
	
	NS.cmbTipoChequerasImprimir = new Ext.form.ComboBox ({
		store: NS.storeTipoChequerasImprimir,
		id: PF + 'cmbTipoChequerasImprimir',
		name: PF + 'cmbTipoChequerasImprimir',
		x: 75,
		y: 150,
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		tabIndex: 10,
		valueField: 'idString',
		displayField: 'descChequera',
		autocomplete: true,
		emptyText: 'Tipo Chequera',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtIdTipoChequerasNuevo', NS.cmbTipoChequerasImprimir.getId());
				}
			}
		}
	});	
	
	NS.optTipoReporte = new Ext.form.RadioGroup ({
		id: PF + 'optTipoReporte',
		name: PF + 'optTipoReporte',
		x: 0,
		y: 0,
		columns: 2, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Reporte de Saldos', name: 'optActualTipoReporte', inputValue: 0, checked: true},  
	          {boxLabel: 'Reporte con desglose', name: 'optActualTipoReporte', inputValue: 1, hidden: true}
	    ]
	});
	
	NS.panelRadioButtonTipoReporte = new Ext.form.FieldSet ({
		title: 'Tipo Reporte',
		x: 0,
		y: 200,
		width: 375,
		height: 80,
		layout: 'absolute',
		items: [
		     NS.optTipoReporte
		]
	});
	
	NS.optTipoEmpresa = new Ext.form.RadioGroup ({
		id: PF + 'optTipoEmpresa',
		name: PF + 'optTipoEmpresa',
		x: 0,
		y: 0,
		columns: 2, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Actual', name: 'optActualEmpresa', inputValue: 0, checked: true},  
	          {boxLabel: 'Todas', name: 'optActualEmpresa', inputValue: 1}
	    ]
	});
	
	NS.panelRadioButtonTipoEmpresa = new Ext.form.FieldSet ({
		title: 'Empresa',
		x: 0,
		y: 290,
		width: 375,
		height: 80,
		layout: 'absolute',
		items: [
		     NS.optTipoEmpresa
		]
	});
	
	NS.panelImprimir = new Ext.form.FieldSet ({
		title: 'Criterios',
		x: 15,
		y: 0,
		width: 400,
		height: 420,
		layout: 'absolute',
		items: [
		     NS.txtEmpresaImprimir,
		     NS.txtNoEmpresaImprimir,
		     NS.lblEmpresaImprimir,
		     NS.lblBancoImprimir,
		     NS.txtBancoImprimir,
		     NS.cmbBancoImprimir,
		     NS.lblChequera,
		     NS.cmbChequeraImprimir,
		     NS.lblTipoChequera,
		     NS.cmbTipoChequerasImprimir,
		     NS.panelRadioButtonTipoReporte,
		     NS.panelRadioButtonTipoEmpresa
		],
	});
	
  
	var winImprimir = new Ext.Window({
		title: 'Reporte de Saldos de Chequeras',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 450,
	   	height: 500,
	   	layout: 'absolute',
	   	plain: true,
	   	resizable:false,
	   	draggable:false,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    buttons:[
			      {
			    	text:'Imprimir',
			    	handler:function(){
			    		var valueTR = Ext.getCmp(PF+'optTipoReporte').getValue();
			    		NS.estatusTipoReporte = valueTR.getGroupValue();  
			    	    var valueTE = Ext.getCmp(PF+'optTipoEmpresa').getValue();
			    		NS.estatusTipoEmpresa = valueTE.getGroupValue();
			    	    NS.idEmpresa = NS.txtNoEmpresaImprimir.getValue();
			    	    
			    	    if(NS.estatusTipoEmpresa==0){
			    	    	NS.nomEmpresa = NS.txtEmpresaImprimir.getValue();
			    	    	NS.empresas = NS.txtNoEmpresaImprimir.getValue();
			    	    	if(Ext.getCmp(PF+'txtBancoImprimir').getValue() == ''){
			    	    		NS.idBancoInf = 0;
				    			NS.idBancoSup = 32000;
			    	    	}else{
			    	    		NS.idBancoInf = NS.txtBancoImprimir.getValue();
				    	    	NS.idBancoSup = NS.txtBancoImprimir.getValue();
			    	    	}
			    	    	
			    	    	if(NS.cmbChequeraImprimir.getValue() == ''){
			    	    		NS.idChequera = '%25';
			    	    	}else{
			    	    		NS.idChequera = NS.cmbChequeraImprimir.getValue();
			    	    	}	
			    	    }else if(NS.estatusTipoEmpresa==1){
			    	    	NS.nomEmpresa='Todas las Empresas';
			    	    	NS.empresas='';
			    	    	if(Ext.getCmp(PF+'txtBancoImprimir').getValue() == ''){
			    	    		NS.idBancoInf = 0;
				    			NS.idBancoSup = 32000;
			    	    	}else{
			    	    		NS.idBancoInf = NS.txtBancoImprimir.getValue();
				    	    	NS.idBancoSup = NS.txtBancoImprimir.getValue();
			    	    	}
			    	    	if(NS.cmbChequeraImprimir.getValue() == ''){
			    	    		NS.idChequera = '%25';
			    	    	}else{
			    	    		NS.idChequera = NS.cmbChequeraImprimir.getValue();
			    	    	}	
			    	    }
			    	    NS.tipoChequera = NS.cmbTipoChequerasImprimir.getValue();
			    	    var strParams = '';
			    		if(NS.estatusTipoReporte == 0)
			    		{
			    			strParams = '?nomReporte=ReporteSaldosDeChequerasS';
			    		}
			    		else if(NS.estatusTipoReporte == 1)
			    		{
			    			strParams = '?nomReporte=ReporteSaldoDeChequerasD';
			    		}
			    		strParams += '&'+'nomParam1=idEmpresa';
			    		strParams += '&'+'valParam1='+NS.idEmpresa;
			    		strParams += '&'+'nomParam2=nomEmpresa';
			    		strParams += '&'+'valParam2='+NS.nomEmpresa;
			    		strParams += '&'+'nomParam3=idBancoInf';
			    		strParams += '&'+'valParam3='+NS.idBancoInf;
			    		strParams += '&'+'nomParam4=idBancoSup';
			    		strParams += '&'+'valParam4='+NS.idBancoSup;
			    		strParams += '&'+'nomParam5=idChequera';
			    		strParams += '&'+'valParam5='+NS.idChequera;
			    		strParams += '&'+'nomParam6=tipoChequera';
			    		strParams += '&'+'valParam6='+NS.tipoChequera;
			    		strParams += '&'+'nomParam7=estatusTipoEmpresa';
			    		strParams += '&'+'valParam7='+NS.estatusTipoEmpresa;
			    		strParams += '&'+'nomParam8=usuario';
			    		strParams += '&'+'valParam8='+NS.GI_ID_USUARIO;
			    		strParams += '&'+'nomParam9=empresas';
			    		strParams += '&'+'valParam9='+NS.empresas;
			    		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
			    		return;
			    	}
			      },
			      {
				    	text:'Excel',
				    	handler:function(){
				    		var valueTR = Ext.getCmp(PF+'optTipoReporte').getValue();
				    		NS.estatusTipoReporte = valueTR.getGroupValue();  
				    	    var valueTE = Ext.getCmp(PF+'optTipoEmpresa').getValue();
				    		NS.estatusTipoEmpresa = valueTE.getGroupValue();
				    	    NS.idEmpresa = NS.txtNoEmpresaImprimir.getValue();
				    	    
				    	    if(NS.estatusTipoEmpresa==0){
				    	    	NS.nomEmpresa = NS.txtEmpresaImprimir.getValue();
				    	    	NS.empresas = NS.txtNoEmpresaImprimir.getValue();
				    	    	if(Ext.getCmp(PF+'txtBancoImprimir').getValue() == ''){
				    	    		NS.idBancoInf = 0;
					    			NS.idBancoSup = 32000;
				    	    	}else{
				    	    		NS.idBancoInf = NS.txtBancoImprimir.getValue();
					    	    	NS.idBancoSup = NS.txtBancoImprimir.getValue();
				    	    	}
				    	    	
				    	    	if(NS.cmbChequeraImprimir.getValue() == ''){
				    	    		NS.idChequera = '%25';
				    	    	}else{
				    	    		NS.idChequera = NS.cmbChequeraImprimir.getValue();
				    	    	}	
				    	    }else if(NS.estatusTipoEmpresa==1){
				    	    	NS.nomEmpresa='Todas las Empresas';
				    	    	NS.empresas='';
				    	    	if(Ext.getCmp(PF+'txtBancoImprimir').getValue() == ''){
				    	    		NS.idBancoInf = 0;
					    			NS.idBancoSup = 32000;
				    	    	}else{
				    	    		NS.idBancoInf = NS.txtBancoImprimir.getValue();
					    	    	NS.idBancoSup = NS.txtBancoImprimir.getValue();
				    	    	}
				    	    	
				    	    	if(NS.cmbChequeraImprimir.getValue() == ''){
				    	    		NS.idChequera = '%25';
				    	    	}else{
				    	    		NS.idChequera = NS.cmbChequeraImprimir.getValue();
				    	    	}	
				    	    }
				    	    NS.tipoChequera = NS.cmbTipoChequerasImprimir.getValue();
				    	    var strParams = '';
				    		if(NS.estatusTipoReporte == 0)
				    		{
				    			strParams = '?nomReporte=ExcelSaldosDeChequerasS';
				    		}
				    		else if(NS.estatusTipoReporte == 1)
				    		{
				    			strParams = '?nomReporte=ExcelSaldoDeChequerasD';
				    		}
				    		strParams += '&'+'nomParam1=idEmpresa';
				    		strParams += '&'+'valParam1='+NS.idEmpresa;
				    		strParams += '&'+'nomParam2=nomEmpresa';
				    		strParams += '&'+'valParam2='+NS.nomEmpresa;
				    		strParams += '&'+'nomParam3=idBancoInf';
				    		strParams += '&'+'valParam3='+NS.idBancoInf;
				    		strParams += '&'+'nomParam4=idBancoSup';
				    		strParams += '&'+'valParam4='+NS.idBancoSup;
				    		strParams += '&'+'nomParam5=idChequera';
				    		strParams += '&'+'valParam5='+NS.idChequera;
				    		strParams += '&'+'nomParam6=tipoChequera';
				    		strParams += '&'+'valParam6='+NS.tipoChequera;
				    		strParams += '&'+'nomParam7=estatusTipoEmpresa';
				    		strParams += '&'+'valParam7='+NS.estatusTipoEmpresa;
				    		strParams += '&'+'nomParam8=usuario';
				    		strParams += '&'+'valParam8='+NS.GI_ID_USUARIO;
				    		strParams += '&'+'nomParam9=empresas';
				    		strParams += '&'+'valParam9='+NS.empresas;
				    		window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
				    		return;
				    	}
				      },
			      {
				    	text:'Limpiar',
				    	handler:function(){
				    		NS.limpiarBotonVentanaImprimir();
				    	}
				      },
				      
	    ],
	   	items: [
	   	     NS.panelImprimir
	   	     ],
   	        	listeners:{
   	        		show:{
   	        			fn:function(){
   	        				if(NS.cmbEmpresa.getValue()=='' || Ext.getCmp(PF + 'txtNoEmpresa').getValue()==''){
   	        					Ext.Msg.alert('SET','No ha seleccionado una empresa valida');
   	        					winImprimir.hide();
   	        				}else{
   	        					winImprimir.show();
   	        					NS.limpiarVentanaImprimir();
   	        					NS.txtNoEmpresaImprimir.setValue(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
   	        					NS.txtEmpresaImprimir.setValue(NS.cmbEmpresa.getRawValue());
   	        					NS.storeBancoImprimir.baseParams.idEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresaImprimir').getValue());
								NS.storeBancoImprimir.sort('descripcion','ASC');
								var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoImprimir, msg:"Cargando..."});
   	        					NS.storeBancoImprimir.load();
   	        					NS.storeTipoChequerasImprimir.load();
   	        				}
   	        			}
   	        		}
   	        	}	
		});
	
	NS.limpiarVentanaImprimir=function(){
		 NS.txtEmpresaImprimir.setValue('');
	     NS.txtNoEmpresaImprimir.setValue('');
	     NS.txtBancoImprimir.setValue('');
	     NS.cmbBancoImprimir.reset();
	     NS.cmbChequeraImprimir.reset();
	     NS.cmbTipoChequerasImprimir.reset();
	     NS.optTipoReporte.reset();
	     NS.optTipoEmpresa.reset();
	     NS.storeBancoImprimir.removeAll();
	     NS.storeChequeraImprimir.removeAll();
	     NS.storeTipoChequerasImprimir.removeAll();
	}
	
	NS.limpiarBotonVentanaImprimir=function(){
	     NS.txtBancoImprimir.setValue('');
	     NS.cmbBancoImprimir.reset();
	     NS.cmbChequeraImprimir.reset();
	     NS.cmbTipoChequerasImprimir.reset();
	     NS.optTipoReporte.reset();
	     NS.optTipoEmpresa.reset();
	}
	
	//Formulario principal
 	NS.SaldoHistoricoFormulario =  new Ext.FormPanel({
    	title: 'Saldos de Chequeras',
    	renderTo: NS.tabContId,
	    width: 692,
	    height: 538,
    	frame: true,
	    padding: 0,
    	autoScroll: true,
	    layout: 'absolute',
        items:[
            {
                xtype: 'fieldset',
                title: 'Búsqueda',
                x: 10,
                y: 10,
                layout: 'absolute',
                width: 670,
                height: 130,
                items: [
                    {
                        xtype: 'datefield',
                        emptyText:'Seleccion Fecha',
                        x: 80,
                        y: 60,
                        width: 150,
                        hidden:true,
                        format: 'd/m/Y',
                        id: PF+'shFecha',
                        name:PF+'shFecha',
                        editable: false,
                        listeners:{
                        	select:{
                        		fn:function(fecha){
                        			var records = NS.storeFechaHoy.data.items;
                        			if(NS.cambiarFechaAMD(fecha.getValue())>=NS.cambiarFechaFormatoAMD(records[0].get('fechaHoy'))){
                        				Ext.Msg.alert('SET','¡Fecha NO Valida!');
                        				fecha.setValue(records[0].get('fechaHoy'));
                        				return;
                        			}
                        			NS.cmbBanco.setValue('');
									NS.cmbChequera.setValue('');
									NS.storeChequeraCombo.removeAll();
									Ext.getCmp(PF+'saldoInicialText').setValue('');
									Ext.getCmp(PF+'cargosText').setValue('');
									Ext.getCmp(PF+'abonosText').setValue('');
									Ext.getCmp(PF+'saldoFinalText').setValue('');
									Ext.getCmp(PF+'cargoAbonoLabel').setText('');
                        		}
                        	}
                        }
                    },
                    NS.cmbEmpresa,
                    NS.cmbBanco,
                    NS.cmbChequera,
                    {
                        xtype: 'label',
                        text: 'Empresa',
                        x: 0,
                        y: 30,
                        width: 60
                    },
                    {
                    	xtype: 'numberfield',
                    	x: 0,
				        y: 45,
						width: 70,
						name: PF + 'txtNoEmpresa',
						id: PF + 'txtNoEmpresa',
						listeners: 
						{
							change:
							{
								fn: function(box, value)
								{
									Ext.getCmp(PF+'lActualizacion').hide();
									NS.cmbBanco.setValue('');
									NS.cmbChequera.setValue('');
									NS.storeBancoCombo.removeAll();
									NS.storeChequeraCombo.removeAll();
				                    // MAFJ 10 marzo 2014
									// se agrego para limpiar casillas cuando se cambia de empresa
									NS.gridDetalleAbono.store.removeAll();
									NS.gridDetalleCargo.store.removeAll();
				        			NS.gridDetalleAbonoSBC.store.removeAll();
				        			NS.gridDetalleCheques.store.removeAll();
									NS.gridDetalleAbono.getView().refresh();
									NS.gridDetalleCargo.getView().refresh();
				        			NS.gridDetalleAbonoSBC.getView().refresh();
									NS.gridDetalleCheques.getView().refresh();
				        			// fin MAFJ
									Ext.getCmp(PF+'saldoInicialText').setValue('');
									Ext.getCmp(PF+'cargosText').setValue('');
									Ext.getCmp(PF+'abonosText').setValue('');
									Ext.getCmp(PF+'saldoFinalText').setValue('');
									Ext.getCmp(PF+'cargoAbonoLabel').setText('');
				                    // MAFJ 10 marzo 2014
									// se agrego para limpiar casillas cuando se cambia de empresa
									Ext.getCmp(PF+'pRealizar').setValue('');
									Ext.getCmp(PF+'dChequera').setValue('');
									Ext.getCmp(PF+'abonoSBC').setValue('');
									Ext.getCmp(PF+'txtRegistros').setValue('');
									Ext.getCmp(PF+'txtTotal').setValue('');
									// fin MAFJ

									//crar
									if(box.getValue() != null && box.getValue() != undefined && box.getValue() != '')
              							var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
           							if(valueCombo != null && valueCombo != undefined && valueCombo != '')
           							{
           								//Carga del store
										NS.GI_ID_EMPRESA = valueCombo;
										NS.storeBancoCombo.baseParams.idEmpresa = parseInt(valueCombo);
										NS.storeBancoCombo.sort('descripcion','ASC');
										NS.storeBancoCombo.load();
           							}
                               		else
                               		{
                               			NS.GI_ID_EMPRESA = 0;
                               		}
									//crar
								}
							}
						}
                    },
                    {
                        xtype: 'label',
                        text: '',
                        id:PF+'lActualizacion',
                        name:PF+'lActualizacion',
                        hidden: true,
                        autoWidth:true,
                        x: 0,
                        y: 80,
                        width: 60
                    },{
                        xtype: 'label',
                        text: 'Fecha',
                        hidden:true,
                        x: 0,
                        y: 65
                    },{
                        xtype: 'label',
                        text: 'Banco',
                        x: 385,
                        y: 25,
                        width: 70
                    },{
                        xtype: 'label',
                        text: 'Chequera',
                        x: 385,
                        y: 65,
                        width: 60
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Saldos del Banco',
                x: 700,
                y: 10,
                layout: 'absolute',
                width: 250,
                height: 130,
                items: [
                 
                    {
                    	name: PF + 'saldoIniBancoText',
						id: PF + 'saldoIniBancoText',
                        xtype: 'textfield',
                        align:'rigth',
                        renderer: myRendererMoney,
                        readOnly: true,
                    	x: 100,
				        y: 15,
						width: 120,
						
						listeners: 
						{
							focus:
							{
								fn: function(box, value)
								{
								}
							}
						}
                    },
                    {
                    	name: PF + 'saldoFinBancoText',
						id: PF + 'saldoFinBancoText',
                        xtype: 'textfield',
                        align:'rigth',
                        renderer: myRendererMoney,
                        readOnly: true,
                    	x: 100,
				        y: 55,
						width: 120,
						listeners: 
						{
							focus:
							{
								fn: function(box, value)
								{
									
								}
							}
						}
                    },

                    {
                        xtype: 'label',
                        text: 'Saldo Inicial Banco',
                        x: 0,
                        y: 18,
                        width: 100
                    },{
                        xtype: 'label',
                        text: 'Saldo Final Banco',
                        x: 0,
                        y: 58,
                        width: 100
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Saldos',
                x: 10,
                y: 150,
                layout: 'absolute',
                width: 250,
                height: 310,
                items: [
                    {
                    //-Definicion Saldo Inicial
                    	id:PF+'saldoInicialText',
                    	name:PF+'saldoInicialText',
                        xtype: 'textfield',
                        align:'rigth',
                        renderer: myRendererMoney,
                        readOnly: true,
                        x: 100,
                        y: 0,
                        width: 120,
                        listeners:{
                        	focus:{
                        		fn:function(){
                        			NS.gridDetalleAbono.store.removeAll();
                        			NS.gridDetalleCargo.store.removeAll();
                        			NS.gridDetalleAbonoSBC.store.removeAll();
                        			NS.gridDetalleCheques.store.removeAll();
                        			Ext.getCmp(PF+'txtRegistros').setValue('');
                        			Ext.getCmp(PF+'txtTotal').setValue('');
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('');
                        		}
                        	}
                        }
                    },
                    //-Definicion cargosText
                    {
                    	id:PF+'cargosText',
                    	name:PF+'cargosText',
                    	align:'rigth',
                    	readOnly: true,
                        xtype: 'textfield',
                        x: 100,
                        y: 40,
                        width: 120,
                        listeners:{
                        	focus:{
                        		fn:function(txt){
                        			if(txt.getValue()===''){
                        				return;
                        			}
                        			if(txt.getValue()===0){
                        				Ext.Msg.alert('SET','¡No Existe Detalle de Cargos!');
                        			}
                        			else{
                        				NS.storeGridCargo.baseParams.fecha= Ext.getCmp(PF+'shFecha').getValue(); 
                        				NS.storeGridCargo.baseParams.idEmpresa= NS.GI_ID_EMPRESA;
                        				NS.storeGridCargo.baseParams.idBanco= Ext.getCmp(PF+'cmbBanco').getValue();
                        				NS.storeGridCargo.baseParams.idChequera = ''+Ext.getCmp(PF+'cmbChequera').getValue();
                        				NS.storeGridCargo.load();
                        				NS.storeGridCargo.sort('descripcion','ASC');
                        				if(!NS.banderaP){
	                        				NS.gridDetalleCargo.show();
	                        				NS.gridDetalleAbono.hide();
	                        				NS.gridDetalleAbonoSBC.hide();
	                        				NS.gridDetalleCheques.hide();
                        				}
                        				NS.gridDetalleCargo.getView().refresh();
                        				Ext.getCmp(PF+'txtTotal').focus();
                        			}
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('Cargos');
                        			Ext.getCmp(PF+'txtRegistros').setValue('0');
                  					Ext.getCmp(PF+'txtTotal').setValue('0.00');
                        		}
                        	}
                        }
                    },
                    //-Definicion abonosText
                    {	
                  		id:PF+'abonosText',
                    	name:PF+'abonosText',
                    	readOnly: true,
                    	align:'rigth',
                        xtype: 'textfield',
                        x: 100,
                        y: 80,
                        width: 120,
                        listeners:{
                        	focus:{
                        		fn:function(txt){
                        			if(txt.getValue()===''){
                        				return;
                        			}
                        			if(txt.getValue()===0){
                        				Ext.Msg.alert('SET','¡No Existe Detalle de Abonos!');
                        			}
                        			else{
                        				NS.storeGridAbono.baseParams.fecha= Ext.getCmp(PF+'shFecha').getValue(); 
                        				NS.storeGridAbono.baseParams.idEmpresa= NS.GI_ID_EMPRESA;
                        				NS.storeGridAbono.baseParams.idBanco= Ext.getCmp(PF+'cmbBanco').getValue();
                        				NS.storeGridAbono.baseParams.idChequera = ''+Ext.getCmp(PF+'cmbChequera').getValue();
                        				NS.storeGridAbono.load();
                        				NS.storeGridAbono.sort('descripcion','ASC');
                        				if(!NS.banderaP){
	                        				NS.gridDetalleAbono.show();
	                        				NS.gridDetalleCargo.hide();
	                        				NS.gridDetalleAbonoSBC.hide();
	                        				NS.gridDetalleCheques.hide();
                        				}
                        				NS.gridDetalleAbono.getView().refresh();
                        				Ext.getCmp(PF+'txtTotal').focus();
                        			}
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('Abonos');
                        			Ext.getCmp(PF+'txtRegistros').setValue('0');
                  					Ext.getCmp(PF+'txtTotal').setValue('0.00');
                        		}
                        	}
                        }
                        
                    },
                    //-Definicion Saldo Disponible
                    {
                    	id:PF+'saldoFinalText',
                    	name:PF+'saldoFinalText',
                    	readOnly: true,
                        xtype: 'textfield',
                        x: 100,
                        y: 120,
                        width: 120,
                        listeners:{
                        	focus:{
                        		fn:function(){
                        			NS.gridDetalleAbono.store.removeAll();
                        			NS.gridDetalleCargo.store.removeAll();
                        			NS.gridDetalleAbonoSBC.store.removeAll();
                    				NS.gridDetalleCheques.store.removeAll();
                        			Ext.getCmp(PF+'txtRegistros').setValue('');
                        			Ext.getCmp(PF+'txtTotal').setValue('');
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('');
                        		}
                        	}
                        }
                    },
                    //-Definicion Cheques en Tránsito PRUEBAS MAFJ 11 MAR 2014
                    {	
                    	id:PF+'pRealizar',
                    	name:PF+'pRealizar',
                    	readOnly: true,
                        xtype: 'textfield',
                        hidden: false,
                        x: 100,
                        y: 160,
                        width: 120,
                    	
                        listeners:{
                        	focus:{
                        		fn:function(txt){
                        			if(txt.getValue()===''){
                        				return;
                        			}
                        			if(txt.getValue()===0){
                        				Ext.Msg.alert('SET','¡No Existe Detalle de Cheques en Tránsito!');
                        			}
                        			else{
                        				NS.storeChequerasPorEnt.baseParams.fecha= Ext.getCmp(PF+'shFecha').getValue(); 
                        				NS.storeChequerasPorEnt.baseParams.idEmpresa= NS.GI_ID_EMPRESA;
                        				NS.storeChequerasPorEnt.baseParams.idBanco= Ext.getCmp(PF+'cmbBanco').getValue();
                        				NS.storeChequerasPorEnt.baseParams.idChequera = ''+Ext.getCmp(PF+'cmbChequera').getValue();
                        				NS.storeChequerasPorEnt.load();
                        				//
                        				if(!NS.banderaP){
	                        				NS.gridDetalleCheques.show();
	                        				NS.gridDetalleAbono.hide();
	                        				NS.gridDetalleCargo.hide();
	                        				NS.gridDetalleAbonoSBC.hide();
                        				}
                        				NS.storeChequerasPorEnt.sort('descripcion','ASC');
                        				NS.gridDetalleCheques.getView().refresh();
                        				Ext.getCmp(PF+'txtTotal').focus();
                        			}
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('Cheques en Tránsito');
                        			Ext.getCmp(PF+'txtRegistros').setValue('0');
                  					Ext.getCmp(PF+'txtTotal').setValue('0.00');
                    			}
                        	}
                        }
                        
                    },
                    //-Definicion Saldo en Banco
                   	{
                    	id:PF+'dChequera',
                    	name:PF+'dChequera',
                        renderer: myRendererMoney,
                    	readOnly: true,
                    	hidden: false,
                        xtype: 'textfield',
                        x: 100,
                        y: 200,
                        width: 120,
                        listeners:{
                        	focus:{
                        		fn:function(text){	
                        			NS.gridDetalleAbono.store.removeAll();
                        			NS.gridDetalleCargo.store.removeAll();
                        			NS.gridDetalleAbonoSBC.store.removeAll();
                    				NS.gridDetalleCheques.store.removeAll();
                        			Ext.getCmp(PF+'txtRegistros').setValue('');
                        			Ext.getCmp(PF+'txtTotal').setValue('');
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('');
                        		}
                        	}
                        }
                   	},{
                    	id:PF+'abonoSBC',
                    	name:PF+'abonoSBC',
                    	readOnly: true,
                    	hidden: false,
                    	xtype: 'textfield',
                        x: 100,
                        y: 240,
                        width: 120,
                        listeners:{
                        	focus:{
                        		fn:function(text){
                        			Ext.getCmp(PF+'txtRegistros').setValue('');
                        			Ext.getCmp(PF+'txtTotal').setValue('');
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText(''); //MAFJ 11 MAR 2014 NO DEBE MOSTRASE LA ETIQUETA
                        			if(text.getValue()===''){
                        				return;
                        			}
                       				NS.storeGridAbonoSBC.baseParams.idEmpresa= NS.GI_ID_EMPRESA;
                       				NS.storeGridAbonoSBC.baseParams.idBanco= Ext.getCmp(PF+'cmbBanco').getValue();
                       				NS.storeGridAbonoSBC.baseParams.idChequera = ''+Ext.getCmp(PF+'cmbChequera').getValue();
                       				NS.storeGridAbonoSBC.load();
                       				if(!NS.banderaP){
                        				NS.gridDetalleAbono.hide();
	                        			NS.gridDetalleCargo.hide();
                        				NS.gridDetalleAbonoSBC.show();
                        				NS.gridDetalleCheques.hide();
                       				}
                       				NS.storeGridAbonoSBC.sort('descripcion','ASC');
                       				NS.gridDetalleAbonoSBC.getView().refresh();
                       				Ext.getCmp(PF+'txtTotal').focus();

                       				if(text.getValue()==='0.00'){
                            			Ext.getCmp(PF+'cargoAbonoLabel').setText('AbonosSBC');
                        				Ext.Msg.alert('SET','¡No Existe Detalle de AbonosSBC!');
                        				return;
                        			}
                        		}
                        	}
                        }
                   	},{
                        xtype: 'label',
                        text: 'Saldo Inicial',
                        x: 0,
                        y: 5
                    },{
                        xtype: 'label',
                        text: 'Egresos',
                        x: 0,
                        y: 40
                    },{
                        xtype: 'label',
                        text: 'Ingresos',
                        x: 0,
                        y: 80
                    },{
                        xtype: 'label',
                        text: '____________________________________',
                        x: 0,
                        y: 100
                    },{
                        xtype: 'label',
                        text: 'Saldo Disponible',
                        x: 0,
                        y: 125
                    },{
                        xtype: 'label',
                        text: 'Cheques en Tránsito',
                        hidden: false,
                        x: 0,
                        y: 165,
                        width: 105
                    },{
                        xtype: 'label',
                        text: '____________________________________',
                        hidden: false,
                        x: 0,
                        y: 180
                    },{
                        xtype: 'label',
                        text: 'Saldo en Banco',
                        hidden: false,
                        x: 0,
                        y: 205,
                        width: 105
                    },{
                        xtype: 'label',
                        text: 'Abono SBC',
                        hidden: false,
                        x: 0,
                        y: 245
                    }
                ]
            },
            //Frame Detalle
            {
                xtype: 'fieldset',
                title: 'Detalle',
                x: 270,
                y: 150,
                layout: 'absolute',
                width: 680,
                height: 310,
                items:[{
                        xtype: 'label',
                        text: '',
                        id:PF+'cargoAbonoLabel',
                        name:PF+'cargoAbonoLabel',
                        autoWidth:true,
                        x: 0,
                        y: 0
                    },{
                    	xtype: 'label',
                        text: 'Total de Registros:',
                        id:PF+'lRegistro',
                        name:PF+'lRegistro',
                        x: 0,
                        y: 248
                    },{
						xtype: 'numberfield',
						id: PF+'txtRegistros',
						name: PF+'txtRegistros',
						readOnly: true,
						x:120,
						y:248,
						width: 80									                    
                    },{
                    	xtype: 'label',
                        text: 'Total:',
                        id:PF+'lImporte',
                        name:PF+'lImporte',
                        x: 220,
                        y: 248
                    },{
						xtype: 'textfield',
						id: PF+'txtTotal',
						name: PF+'txtTotal',
						readOnly: true,
						x:260,
						y:248,
						width: 110									                    
                    },
                    NS.gridDetalleAbono,
                    NS.gridDetalleCargo,
                    NS.gridDetalleAbonoSBC,
                    NS.gridDetalleCheques
                ]
            },
            //Boton limpiar
            {
                    	xtype: 'button',
                    	text:'Limpiar',
						id: PF+'btnLimpiar',
						name: PF+'btnLimpiar',
						width:100,
						x:540,
						y:475,
						listeners:{
							click:{
								fn:function(btn){
                        			NS.limpiar();
								}
							}
						}
                    },
                    {
                    	xtype: 'button',
                    	text:'Imprimir',
						id: PF+'btnImprimir',
						name: PF+'btnImprimir',
						width:100,
						x:420,
						y:475,
						listeners:{
							click:{
								fn:function(btn){
									winImprimir.show();
								}
							}
						}
                    },
                    {
                    	xtype: 'button',
                    	text:'Reconstruir',
						id: PF+'btnReconstruir',
						name: PF+'btnReconstruir',
						hidden: true,   //MAFJ 03 MAR 2014 PENDIANTE HASTA DEFINIR RECONSTRUCCION DE SALDOS
						width:100,
						x:420,
						y:475,
						listeners:{
							click:{
								fn:function(btn){
                        			NS.reconstruir();
								}
							}
						}
                    }
        ]
    }); 
 	
 	
 	
 	NS.reconstruir = function(){
 		
 		var ntEmpresa = parseInt( Ext.getCmp(PF + 'txtNoEmpresa').getValue() ) ; 
 		var ntBanco   = parseInt( Ext.getCmp(PF+'cmbBanco').getValue() );
 	    var strChequera = Ext.getCmp( PF+'cmbChequera').getValue();
 	    
		if( isNaN(ntEmpresa) )
		{
			Ext.Msg.alert( "SET" , "No ha seleccionado una Empresa." );
			return;			
		}
		
		if( isNaN(ntBanco) )
		{
			Ext.Msg.alert( "SET" , "No ha seleccionado un banco." );
			return; 			
		}
		
		if( strChequera == "" )
		{
			Ext.Msg.alert( "SET" , "No ha seleccionado una chequera." );
			return; 			
		}
 	  		
 		ConsultasAction.reconstruyeChequeraAction(ntEmpresa, ntBanco, strChequera, function(res, e){			
				Ext.Msg.alert('SET',res);
				
				NS.gridDetalleAbono.store.removeAll();
				NS.gridDetalleCargo.store.removeAll();
				NS.gridDetalleCheques.store.removeAll();
				NS.gridDetalleAbonoSBC.store.removeAll();
				NS.gridDetalleAbono.getView().refresh();
				NS.gridDetalleCargo.getView().refresh();
				NS.gridDetalleCheques.getView().refresh();
				NS.gridDetalleAbonoSBC.getView().refresh();
				Ext.getCmp(PF+'lActualizacion').hide();
				Ext.getCmp(PF+'txtRegistros').setValue('0');
				Ext.getCmp(PF+'txtTotal').setValue('0.00');
				NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
				NS.myMask.show();								
				NS.storeDatosSaldos.baseParams.noEmpresa = NS.GI_ID_EMPRESA;
				
				NS.storeDatosSaldos.baseParams.idBanco = Ext.getCmp(PF+'cmbBanco').getValue(); 
				NS.storeDatosSaldos.baseParams.descBanco = ''+NS.storeBancoCombo.getById(Ext.getCmp(PF+'cmbBanco').getValue()).get('descripcion');
				NS.storeDatosSaldos.baseParams.idChequera =''+Ext.getCmp(PF+'cmbChequera').getValue();
				NS.storeDatosSaldos.load();
				NS.myMask.hide();

		});
 		
 	}
    
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
	
	NS.cambiarFechaFormatoAMD=function(fecha){
		var dia = fecha.substring(0,2);
		var mes = fecha.substring(3,5);
		var anio = fecha.substring(6);
		return ''+anio+'/'+mes+'/'+dia;
	};
	
	NS.cambiarFechaAMD=function(fecha){
		fecha= fecha.toString();
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		for(var i=0;i<12;i=i+1){
			if(mesArreglo[i]===mesDate)
			{
				var mes=i+1;
				if(mes<10){
					mes='0'+mes;
				}
			}
		}
		return ''+anio+'/'+mes+'/'+dia;
	};
	
	NS.SaldoHistoricoFormulario.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
});


