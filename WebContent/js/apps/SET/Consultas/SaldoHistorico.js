Ext.onReady(function(){
 	//@autor Sergio Vaca
	var NS = Ext.namespace('apps.SET.Consultas.SaldoHistorico');
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
 	
 	//store fecha_hoy
 	NS.storeFechaHoy = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		directFn: ConsultasAction.obtenerFechaAyer,
		idProperty: 'fechaAyer',
		fields: [
			{name: 'fechaAyer'}
		],
		listeners: {
			load: function(s, records){
				//console.log(records.length);
				if(records.length>0){
			
					//Ext.getCmp(PF+'shFecha').setValue(records[0].get('fechaAyer'));
				}
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFechaHoy, msg:"Cargando..."});
			}
		}
	}); 
	//carga de Datos
	NS.storeFechaHoy.load();
	
	NS.limpiar = function(){
		NS.cmbEmpresa.reset();
		NS.cmbBanco.reset();			
		NS.cmbChequera.reset();
		NS.gridDetalleAbono.store.removeAll();
		NS.gridDetalleCargo.store.removeAll();
		NS.gridDetalleAbono.getView().refresh();
		NS.gridDetalleCargo.getView().refresh();
   		Ext.getCmp(PF+'txtRegistros').setValue('0');
		Ext.getCmp(PF+'txtTotal').setValue('0.0');
		Ext.getCmp(PF+'saldoInicialText').setValue('0.0');
		Ext.getCmp(PF+'cargosText').setValue('0.0');
		Ext.getCmp(PF+'abonosText').setValue('0.0');
		Ext.getCmp(PF+'saldoFinalText').setValue('0.0');
		Ext.getCmp(PF+'cargoAbonoLabel').setText('');
		Ext.getCmp(PF+'shFecha').reset();
		Ext.getCmp(PF + 'txtNoEmpresa').setValue('');
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
				NS.storeBancoCombo.baseParams.idEmpresa=NS.GI_ID_EMPRESA;
				NS.storeBancoCombo.sort('descripcion','ASC');
				NS.storeBancoCombo.load();
			}
		}
	}); 
	//carga de datos
	NS.storeComboEmpresa.load();
	
	//Combo del banco
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
        y: 25,
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
					NS.GI_ID_EMPRESA = combo.getValue();
					NS.cmbBanco.setValue('');
					NS.cmbChequera.setValue('');
					NS.storeBancoCombo.removeAll();
					NS.storeChequeraCombo.removeAll();
					Ext.getCmp(PF+'saldoInicialText').setValue('');
					Ext.getCmp(PF+'cargosText').setValue('');
					Ext.getCmp(PF+'abonosText').setValue('');
					Ext.getCmp(PF+'saldoFinalText').setValue('');
					Ext.getCmp(PF+'cargoAbonoLabel').setText('');
					//Carga del store
					NS.storeBancoCombo.baseParams.idEmpresa=combo.getValue();
					NS.storeBancoCombo.sort('descripcion','ASC');
					NS.storeBancoCombo.load();
					//crar
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}
	});
	
 	//store combo Banco
 	NS.storeBancoCombo = new Ext.data.DirectStore( {
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
	
	//Combo del banco
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
					NS.storeChequeraCombo.removeAll();
					NS.cmbChequera.setValue('');
					NS.storeChequeraCombo.baseParams.idEmpresa = NS.GI_ID_EMPRESA;
					NS.storeChequeraCombo.baseParams.idBanco = combo.getValue();
					NS.storeChequeraCombo.sort('idChequera','ASC');
					NS.storeChequeraCombo.load();
				}
			}
		}
	});
	
	//store combo Chequera
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
	//Chequera
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
		
					NS.gridDetalleAbono.store.removeAll();
					NS.gridDetalleCargo.store.removeAll();
					NS.gridDetalleAbono.getView().refresh();
					NS.gridDetalleCargo.getView().refresh();
					
					if(Ext.getCmp(PF+'shFecha').getValue()===''){
						if(NS.OptDetalle){
							Ext.Msg.alert('SET','¡Es necesario introducir una fecha!');
							NS.cmbBanco.setValue('');
							NS.cmbChequera.setValue('');
							NS.storeBancoCombo.removeAll();
							NS.storeChequeraCombo.removeAll();
							Ext.getCmp(PF+'saldoInicialText').setValue('');
							Ext.getCmp(PF+'cargosText').setValue('');
							Ext.getCmp(PF+'abonosText').setValue('');
							Ext.getCmp(PF+'saldoFinalText').setValue('');
							Ext.getCmp(PF+'cargoAbonoLabel').setText('');
							Ext.getCmp(PF+'shFecha').reset();
							NS.storeBancoCombo.baseParams.idEmpresa = parseInt(NS.GI_ID_EMPRESA);
							NS.storeBancoCombo.sort('descripcion','ASC');
							NS.storeBancoCombo.load();
							return;
						}
					}
					if(NS.OptDetalle){
						var records = NS.storeFechaHoy.data.items;
									
						if(NS.cambiarFechaAMD(Ext.getCmp(PF+'shFecha').getValue())>NS.cambiarFechaFormatoAMD(records[0].get('fechaAyer'))){
							Ext.Msg.alert('SET','Fecha no Valida');
							NS.cmbBanco.setValue('');
							NS.cmbChequera.setValue('');
							NS.storeBancoCombo.removeAll();
							NS.storeChequeraCombo.removeAll();
							Ext.getCmp(PF+'saldoInicialText').setValue('');
							Ext.getCmp(PF+'cargosText').setValue('');
							Ext.getCmp(PF+'abonosText').setValue('');
							Ext.getCmp(PF+'saldoFinalText').setValue('');
							Ext.getCmp(PF+'cargoAbonoLabel').setText('');
                        	Ext.getCmp(PF+'shFecha').setValue(records[0].get('fechaAyer'));
                        	NS.storeBancoCombo.baseParams.idEmpresa = parseInt(NS.GI_ID_EMPRESA);
							NS.storeBancoCombo.sort('descripcion','ASC');
							NS.storeBancoCombo.load();
                        	return;
						}
					}
						NS.storeDatosSaldos.baseParams.noEmpresa = NS.GI_ID_EMPRESA;
						NS.storeDatosSaldos.baseParams.idBanco = Ext.getCmp(PF+'cmbBanco').getValue(); 
						NS.storeDatosSaldos.baseParams.descBanco = ''+NS.storeBancoCombo.getById(Ext.getCmp(PF+'cmbBanco').getValue()).get('descripcion');
						NS.storeDatosSaldos.baseParams.idChequera =''+combo.getValue();
						NS.storeDatosSaldos.baseParams.fecValor = Ext.getCmp(PF+'shFecha').getValue();
						NS.storeDatosSaldos.load();
					} 
				}
			}
	});
	
	//Dato de los saldos
	NS.storeDatosSaldos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			noEmpresa: -1,
			idBanco: -1,
			descBanco: '',
			idChequera: '',
			fecValor: ''
		},
		root: '',
		paramOrder:['noEmpresa', 'idBanco', 'descBanco', 'idChequera', 'fecValor'],
		directFn: ConsultasAction.seleccionarDatosChequeraHistorico,
		idProperty: 'saldoInicial',
		fields: [
			{name: 'saldoInicial'},
			{name: 'saldoFinal'},
			{name: 'cargo'},
			{name: 'abono'}, 
			{name: 'cargoSbc'},
			{name: 'abonoSbc'}
			
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatosSaldos, msg:"Cargando..."});
				//var records = NS.storeDatosSaldos.data.items;
				if(records.length>0){
					Ext.getCmp(PF+'saldoInicialText').setValue(NS.formatNumber(records[0].get('saldoInicial')));
					Ext.getCmp(PF+'cargosText').setValue(NS.formatNumber(records[0].get('cargo')));
					//Ext.Msg.alert("cargo "+Ext.getCmp(PF+'cargosText').getValue());
					Ext.getCmp(PF+'abonosText').setValue(NS.formatNumber(records[0].get('abono')));
					Ext.getCmp(PF+'saldoFinalText').setValue(NS.formatNumber(records[0].get('saldoFinal')));
				}
				else{
					Ext.Msg.alert('SET','El Archivo Histórico no Contiene la información Requerida');
					NS.cmbBanco.setValue('');
					NS.cmbChequera.setValue('');
					NS.storeChequeraCombo.removeAll();
					NS.cmbChequera.setValue('');
					Ext.getCmp(PF+'saldoInicialText').setValue('');
					Ext.getCmp(PF+'cargosText').setValue('');
					Ext.getCmp(PF+'abonosText').setValue('');
					Ext.getCmp(PF+'saldoFinalText').setValue('');
					Ext.getCmp(PF+'cargoAbonoLabel').setText('');
				}
			}
		}
	});
	
	// Store del Grid Abono
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
		directFn: ConsultasAction.seleccionarDatosGridAbonos,
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
  					Ext.getCmp(PF+'txtTotal').setValue(NS.formatNumber(NS.darFormato(total)));
  				}
			}
		}
	});
	
	// Store del Grid Cargo
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
		directFn: ConsultasAction.seleccionarDatosGridCargos,
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
  					Ext.getCmp(PF+'txtTotal').setValue(NS.formatNumber(NS.darFormato(total)));
  				}
			}
		}
	});
	
	//Grid de datos
	NS.gridDetalleAbono = new Ext.grid.GridPanel({
		store : NS.storeGridAbono,
		height : 220,
		width :380,
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
				header: 'Persona',
				width: 180,
				sortable: true,
				dataIndex:'beneficiario'
			},{
				header: 'Folio',
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
	
	//Grid de datos
	NS.gridDetalleCargo = new Ext.grid.GridPanel({
		store : NS.storeGridCargo,
		height : 220,
		width :380,
		x :5,
		y :20,
		title :'',
		hidden:true,
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
				header: 'Persona',
				width: 180,
				sortable: true,
				dataIndex:'beneficiario'
			},{
				header: 'Folio',
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
	
	NS.txtNoBancoImprimir = new Ext.form.TextField({
		id: PF + 'txtNoBancoImprimir',
		name: PF + 'txtNoBancoImprimir',
		x: 0,
		y: 65,
		width: 55,
		disabled: true		
	});
	
	NS.txtBancoImprimir = new Ext.form.TextField({
		id: PF + 'txtBancoImprimir',
		name: PF + 'txtBancoImprimir',
		x: 75,
		y: 65,
		width: 300,
		disabled: true
	});
	
	NS.lblChequera = new Ext.form.Label({
		text: 'Chequera:',
		x: 0,
		y: 110		
	});
	
	NS.txtChequeraImprimir = new Ext.form.TextField({
		id: PF + 'txtChequeraImprimir',
		name: PF + 'txtChequeraImprimir',
		x: 75,
		y: 110,
		width: 300,
		disabled: true
	});
	
	NS.optTipoReporte = new Ext.form.RadioGroup ({
		id: PF + 'optTipoReporte',
		name: PF + 'optTipoReporte',
		x: 0,
		y: 0,
		columns: 1, //muestra los radiobuttons en dos columnas
		items: [
		        {boxLabel: 'Actual', name: 'optActualTipoReporte', inputValue: 0, checked: true,
		        	listeners: {
		        			check: {
		        				fn: function(opt, valor) {
		        					if (valor == true) {
		        						Ext.getCmp(PF+'lblDesde').setVisible(false);
		        						Ext.getCmp(PF+'txtFechaDesde').setVisible(false);
		        						Ext.getCmp(PF+'lblHasta').setVisible(false);
		        						Ext.getCmp(PF+'txtFechaHasta').setVisible(false);
		        					}
		        				}
				
		        			}
		        	}
		        },  
		        {boxLabel: 'Entre Fechas', name: 'optActualTipoReporte', inputValue: 1,
		        	listeners: {
		        			check: {
		        				fn: function(opt, valor) {
		        					if (valor == true) {
		        						Ext.getCmp(PF+'lblDesde').setVisible(true);
		        						Ext.getCmp(PF+'txtFechaDesde').setVisible(true);
		        						Ext.getCmp(PF+'lblHasta').setVisible(true);
		        						Ext.getCmp(PF+'txtFechaHasta').setVisible(true);
		        					}
		        				}
				
		        			}
		        	}
		        }
	    ]
	});
	
	NS.panelRadioButtonTipoReporte = new Ext.form.FieldSet ({
		title: 'Tipo Reporte',
		x: 0,
		y: 140,
		width: 375,
		height: 150,
		layout: 'absolute',
		items: [
		     NS.optTipoReporte,
		     {
                 xtype: 'label',
                 text: 'Desde:',
                 name: PF+'lblDesde',
                 id: PF+'lblDesde',
                 hidden: true,
                 x: 0,
                 y: 60
             },
             {
                 xtype: 'datefield',
                 format: 'd/m/Y',
                 name: PF+'txtFechaDesde',
                 id: PF+'txtFechaDesde',
                 hidden: true,
                 x: 65,
                 y: 55,
                 width: 100,
                 value: apps.SET.FEC_HOY
             },
             {
                 xtype: 'label',
                 text: 'Hasta:',
                 name: PF+'lblHasta',
                 id: PF+'lblHasta',
                 hidden: true,
                 x: 180,
                 y:60
             },
             {
                 xtype: 'datefield',
                 format: 'd/m/Y',
                 name: PF+'txtFechaHasta',
                 id: PF+'txtFechaHasta',
                 hidden: true,
                 x: 225,
                 y: 55,
                 width: 100,
                 value: apps.SET.FEC_HOY,
                 listeners:{
            			change:{
            				fn:function(caja,valor){
            					if(Ext.getCmp(PF+'txtFechaDesde').getValue()>valor)
								{
									Ext.Msg.alert('Información SET','La fecha inicial debe ser menor a la final');
									return;
								}							
            				}
            			}
            		}
             }
		     
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
		     NS.lblEmpresaImprimir,
		     NS.txtNoEmpresaImprimir,
		     NS.txtEmpresaImprimir,
		     NS.lblBancoImprimir,
		     NS.txtNoBancoImprimir,
		     NS.txtBancoImprimir,
		     NS.lblChequera,
		     NS.txtChequeraImprimir,
		     NS.panelRadioButtonTipoReporte
		],
	});
	
	var winImprimir = new Ext.Window({
		title: 'Reporte Historico de Saldos de Chequeras',
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
			    	    NS.idEmpresa = NS.txtNoEmpresaImprimir.getValue();
			    	    NS.nomEmpresa = NS.txtEmpresaImprimir.getValue();
			    	    NS.idBanco = NS.cmbBanco.getValue(); //NS.txtBancoImprimir.getValue();
			    	    NS.idChequera = NS.txtChequeraImprimir.getValue();
			    	    NS.fechaDesde = Ext.getCmp(PF + 'txtFechaDesde').getValue().format('d/m/Y');
			    	    NS.fechaHasta = Ext.getCmp(PF + 'txtFechaHasta').getValue().format('d/m/Y');
       					NS.txtBancoImprimir.setValue(NS.cmbBanco.getRawValue());

			    	    var strParams = '';
			    		if(NS.estatusTipoReporte == 0)
			    		{
			    			strParams = '?nomReporte=ReporteSaldoHistoricoDeChequerasA';
			    		}
			    		else if(NS.estatusTipoReporte == 1)
			    		{
			    			strParams = '?nomReporte=ReporteSaldoHistoricoDeChequerasF';
			    		}
			    		strParams += '&'+'nomParam1=noEmpresa';
			    		strParams += '&'+'valParam1='+NS.idEmpresa;
			    		strParams += '&'+'nomParam2=nomEmpresa';
			    		strParams += '&'+'valParam2='+NS.nomEmpresa;
			    		strParams += '&'+'nomParam3=idBanco';
			    		strParams += '&'+'valParam3='+NS.idBanco;
			    		strParams += '&'+'nomParam4=idChequera';
			    		strParams += '&'+'valParam4='+NS.idChequera;
			    		strParams += '&'+'nomParam5=txtFechaDesde';
			    		strParams += '&'+'valParam5='+NS.fechaDesde;
			    		strParams += '&'+'nomParam6=txtFechaHasta';
			    		strParams += '&'+'valParam6='+NS.fechaHasta;
			    		strParams += '&'+'nomParam7=banco';
			    		strParams += '&'+'valParam7='+NS.cmbBanco.getRawValue();
			    		strParams += '&'+'nomParam8=fechaHoy';
			    		strParams += '&'+'valParam8='+apps.SET.FEC_HOY;
			    		
			    		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
			    		return;
			    	}
			      },
			      {
				    	text:'Limpiar',
				    	handler:function(){
				    		NS.limpiarVentanaImprimir();
				    	}
				      },
				      
	    ],
	   	items: [
	   	     NS.panelImprimir
	   	     ],
   	        	listeners:{
   	        		show:{
   	        			fn:function(){
   	        				if(NS.cmbEmpresa.getValue()=='' || Ext.getCmp(PF + 'txtNoEmpresa').getValue()=='' || NS.cmbBanco.getValue()=='' || NS.cmbChequera.getValue()==''){
   	        					Ext.Msg.alert('SET','Debe seleccionar una empresa, un banco y una chequera');
   	        					winImprimir.hide();
   	        				}else{
   	        					winImprimir.show();
   	        					NS.limpiarVentanaImprimir();
   	        					NS.txtNoEmpresaImprimir.setValue(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
   	        					NS.txtEmpresaImprimir.setValue(NS.cmbEmpresa.getRawValue());
   	        					NS.txtNoBancoImprimir.setValue(NS.cmbBanco.getValue());
   	        					NS.txtBancoImprimir.setValue(NS.cmbBanco.getRawValue());
   	        					NS.txtChequeraImprimir.setValue(NS.cmbChequera.getRawValue());
   	        				}
   	        			}
   	        		}
   	        	}	
		});
	

	NS.limpiarVentanaImprimir=function(){
		 Ext.getCmp(PF+'txtFechaDesde').setValue(apps.SET.FEC_HOY);
		 Ext.getCmp(PF+'txtFechaHasta').setValue(apps.SET.FEC_HOY);
	     NS.optTipoReporte.reset();
	 }
	
	
	//Formulario principal
 	NS.SaldoHistoricoFormulario =  new Ext.FormPanel({
    	title: 'Saldo Histórico de Chequeras',
    	renderTo: NS.tabContId,
	    width: 692,
	    height: 538,
    	frame: true,
    	autoScroll: true,
	    padding: 0,
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
                        width: 130,
                        //hidden:true,
                        format: 'd/m/y',
                        id: PF+'shFecha',
                        name:PF+'shFecha',
                        editable: false,
                        listeners:{
                        	change:{
                        		fn:function(caja,fecha){
                        			if(NS.cambiarFechaAMD(fecha.getValue())>=NS.cambiarFechaFormatoAMD(records[0].get('fechaAyer')))
                        			{
                        				Ext.Msg.alert('SET','Fecha no Valida');
                        				fecha.setValue(records[0].get('fechaAyer'));
                        				return;
                        			}
                        			NS.cmbChequera.setValue('');
									NS.storeBancoCombo.removeAll();
									NS.storeChequeraCombo.removeAll();
									Ext.getCmp(PF+'saldoInicialText').setValue('');
									Ext.getCmp(PF+'cargosText').setValue('');
									Ext.getCmp(PF+'abonosText').setValue('');
									Ext.getCmp(PF+'saldoFinalText').setValue('');
									Ext.getCmp(PF+'cargoAbonoLabel').setText('');
									NS.storeBancoCombo.baseParams.idEmpresa = parseInt(NS.GI_ID_EMPRESA);
									NS.storeBancoCombo.sort('descripcion','ASC');
									NS.storeBancoCombo.load();
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
                        y: 10,
                        width: 60
                    },
                    {
                    	xtype: 'numberfield',
                    	x: 0,
				        y: 25,
						width: 70,
						name: PF + 'txtNoEmpresa',
						id: PF + 'txtNoEmpresa',
						listeners: 
						{
							change:
							{
								fn: function(box, value)
								{
									NS.cmbBanco.setValue('');
									NS.cmbChequera.setValue('');
									NS.storeBancoCombo.removeAll();
									NS.storeChequeraCombo.removeAll();
									Ext.getCmp(PF+'saldoInicialText').setValue('');
									Ext.getCmp(PF+'cargosText').setValue('');
									Ext.getCmp(PF+'abonosText').setValue('');
									Ext.getCmp(PF+'saldoFinalText').setValue('');
									Ext.getCmp(PF+'cargoAbonoLabel').setText('');
				
									//crar
									if(box.getValue() != null && box.getValue() != undefined && box.getValue() != '')
              							var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
           							if(valueCombo != null && valueCombo != undefined && valueCombo != '')
           							{
   										//Carga del store
   										NS.GI_ID_EMPRESA = valueCombo;
										NS.cmbEmpresa.setValue(valueCombo);
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
                        text: 'Fecha',
                        //hidden:true,
                        x: 40,
                        y: 65
                    },
                    {
                        xtype: 'label',
                        text: 'Banco',
                        x: 385,
                        y: 25,
                        width: 70
                    },
                    {
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
                title: 'Saldos',
                x: 10,
                y: 150,
                layout: 'absolute',
                width: 250,
                height: 310,
                items: [
                    {
                    	id:PF+'saldoInicialText',
                    	name:PF+'saldoInicialText',
                        xtype: 'textfield',
                        align:'rigth',
                        readOnly: true,
                        x: 90,
                        y: 40,
                        listeners:{
                        	focus:{
                        		fn:function(){
                        			NS.gridDetalleAbono.store.removeAll();
                        			NS.gridDetalleCargo.store.removeAll();
	                        		Ext.getCmp(PF+'txtRegistros').setValue('');
                        			Ext.getCmp(PF+'txtTotal').setValue('');
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('');
                        		}
                        	}
                        }
                    },
                    {
                    	id:PF+'cargosText',
                    	name:PF+'cargosText',
                    	align:'rigth',
                    	readOnly: true,
                        xtype: 'textfield',
                        x: 90,
                        y: 80,
                        listeners:{
                        	focus:{
                        		fn:function(txt){
                        			if(txt.getValue()===''){
                        				return;
                        			}
                        			if(txt.getValue()==='0.00'){
                        				Ext.Msg.alert('SET','¡No Existen Detalle de Cargos!');
                        			}
                        			else{
                        				NS.storeGridCargo.baseParams.fecha= Ext.getCmp(PF+'shFecha').getValue(); 
                        				NS.storeGridCargo.baseParams.idEmpresa = NS.GI_ID_EMPRESA;
                        				NS.storeGridCargo.baseParams.idBanco= Ext.getCmp(PF+'cmbBanco').getValue();
                        				NS.storeGridCargo.baseParams.idChequera = ''+Ext.getCmp(PF+'cmbChequera').getValue();
                        				NS.storeGridCargo.load();
                        				NS.storeGridCargo.sort('noDocto','ASC');
                        				NS.gridDetalleCargo.show();
                        				NS.gridDetalleAbono.hide();
                        				NS.gridDetalleCargo.getView().refresh();
                        				Ext.getCmp(PF+'txtTotal').focus();
                        			}
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('Cargos');
                        		}
                        	}
                        }
                    },
                    {	
                  		id:PF+'abonosText',
                    	name:PF+'abonosText',
                    	readOnly: true,
                    	align:'rigth',
                        xtype: 'textfield',
                        x: 90,
                        y: 120,
                        listeners:{
                        	focus:{
                        		fn:function(txt){
                        			if(txt.getValue()===''){
                        				return;
                        			}
                        			if(txt.getValue()==='0.00'){
                        				Ext.Msg.alert('SET','¡No Existen Detalle de Abonos!');
                        			}
                        			else{
                        				NS.storeGridAbono.baseParams.fecha= Ext.getCmp(PF+'shFecha').getValue(); 
                        				NS.storeGridAbono.baseParams.idEmpresa= NS.GI_ID_EMPRESA;
                        				NS.storeGridAbono.baseParams.idBanco= Ext.getCmp(PF+'cmbBanco').getValue();
                        				NS.storeGridAbono.baseParams.idChequera = ''+Ext.getCmp(PF+'cmbChequera').getValue();
                        				NS.storeGridAbono.load();
                        				NS.storeGridAbono.sort('noDocto','ASC');
                        				NS.gridDetalleAbono.show();
                        				NS.gridDetalleCargo.hide();
                        				NS.gridDetalleAbono.getView().refresh();
                        				Ext.getCmp(PF+'txtTotal').focus();
                        			}
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('Abonos');
                        		}
                        	}
                        }
                        
                    },
                    {
                    	id:PF+'saldoFinalText',
                    	name:PF+'saldoFinalText',
                    	readOnly: true,
                        xtype: 'textfield',
                        x: 90,
                        y: 160,
                        listeners:{
                        	focus:{
                        		fn:function(){
                        			NS.gridDetalleAbono.store.removeAll();
                        			NS.gridDetalleCargo.store.removeAll();
	                        		Ext.getCmp(PF+'txtRegistros').setValue('');
                        			Ext.getCmp(PF+'txtTotal').setValue('');
                        			Ext.getCmp(PF+'cargoAbonoLabel').setText('');
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'Saldo Inicial',
                        x: 0,
                        y: 45
                    },
                    {
                        xtype: 'label',
                        text: 'Egresos',
                        x: 0,
                        y: 85
                    },
                    {
                        xtype: 'label',
                        text: 'Ingresos',
                        x: 0,
                        y: 120
                    },
                    {
                        xtype: 'label',
                        text: 'Saldo Disponible',
                        x: 0,
                        y: 165
                    },
                    {
                        xtype: 'label',
                        text: '____________________________________',
                        x: 0,
                        y: 140
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Detalle',
                x: 270,
                y: 150,
                layout: 'absolute',
                width: 410,
                height: 310,
                items:[{
                        xtype: 'label',
                        text: '',
                        id:PF+'cargoAbonoLabel',
                        name:PF+'cargoAbonoLabel',
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
                    NS.gridDetalleCargo
                ]
            },{
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
               	text:'Reconstruir',
					id: PF+'btnReconstruir',
					name: PF+'btnReconstruir',
					width:100,
					x:420,
					y:475,
					hidden: true,
					listeners:{
						click:{
							fn:function(btn){
                   				NS.reconstruir();
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
                  }
        ]
    }); 
 	
NS.reconstruir = function(){
 		
 		var ntEmpresa = parseInt( Ext.getCmp(PF + 'txtNoEmpresa').getValue() ) ; 
 		var ntBanco   = parseInt( Ext.getCmp(PF+'cmbBanco').getValue() );
 	    var strChequera = Ext.getCmp( PF+'cmbChequera').getValue();
 	    var strFecha = Ext.getCmp( PF+'shFecha').getValue();
 	    //strFechaNS = NS.cambiarFechaAMD(strFecha);
 	    
 	    
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
		
		ConsultasAction.reconstruyeChequeraHistoricaAction(ntEmpresa, ntBanco, strChequera,strFecha, function(res, e){			
			Ext.Msg.alert('SET',res);
			
			NS.gridDetalleAbono.store.removeAll();
			NS.gridDetalleCargo.store.removeAll();
			NS.gridDetalleAbono.getView().refresh();
			NS.gridDetalleCargo.getView().refresh();
			
			NS.storeDatosSaldos.baseParams.noEmpresa = NS.GI_ID_EMPRESA;
			NS.storeDatosSaldos.baseParams.idBanco = Ext.getCmp(PF+'cmbBanco').getValue(); 
			NS.storeDatosSaldos.baseParams.descBanco = ''+ NS.storeBancoCombo.getById(Ext.getCmp(PF+'cmbBanco').getValue()).get('descripcion');
			NS.storeDatosSaldos.baseParams.idChequera =''+Ext.getCmp(PF+'cmbChequera').getValue();
			NS.storeDatosSaldos.baseParams.fecValor = Ext.getCmp(PF+'shFecha').getValue();
			NS.storeDatosSaldos.load();
			
			
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
		return num.replace(/([^0-9\.\-])/g,''*1);
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