Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	var NS = Ext.namespace('apps.SET.Inversiones.Operacion.VencimientoDeInversion');
	var PF = apps.SET.tabID + '.';
	var nomreEmpresa = apps.SET.NOM_EMPRESA;
	NS.tabContId = apps.SET.tabContainerId;
	NS.sHora = '';
	
	NS.smCheck = new Ext.grid.CheckboxSelectionModel({
			singleSelect : true		
	});
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});

	NS.txtNoEmpresa = new Ext.form.TextField({
		x: 10,
	    y: 15,
	    width: 55,
	    tabIndex: 0,
	    name: PF + 'txtNoEmpresa',
	    id: PF + 'txtNoEmpresa',
//	    value: apps.SET.NO_EMPRESA,
	    listeners:{
	    	change:{
	    		fn: function(caja, valor){
	    			var noEmp = BFwrk.Util.updateTextFieldToCombo(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
	    			
	    			if(noEmp == null) {
	    				Ext.getCmp(PF+'txtNoEmpresa').reset();
	    				NS.cmbEmpresa.reset();
	    			}
	    			NS.storeLiqOrdenesInversion.removeAll();
	    			NS.gridLiqOrdenesInversion.getView().refresh();
	    		}
	    	}
	    }
	});
	
	//store de cmbEmpresa
	NS.storeEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: InversionesAction.llenarComboEmpresas, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresa, msg:"Cargando..."});
				
				if(records.length === null || records.length <= 0)
					Ext.Msg.alert('SET','No Existen empresas asignadas a este usuario, consulte a su administrador...');
			}
		}
	}); 
	NS.storeEmpresa.load();
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresa,
		name: PF+'cmbEmpresa',
		id: PF+'cmbEmpresa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 70,
        y: 15,
        width: 280,
		valueField:'noEmpresa',
		displayField:'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
//		value: apps.SET.NOM_EMPRESA,
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}
	});
	
	//Llamada a GlobalAction para obtener la hora actual
	NS.obtenerHora = function(){
		GlobalAction.obtenerHoraActualFormato12(function(result, e){
			if(result.substring(result.length - 1) === '1')
			{
				NS.sHora = result.substring(0,4) + ' PM';
				return NS.sHora;
			}
			else
			{
				NS.sHora = result.substring(0,4) + ' AM';
				return NS.sHora;
			}
		});
	};
	
	NS.obtenerHora();
	
	//store del combo cmbContactos
	NS.storeContactos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iNoOrden : 0
		},
		root: '',
		paramOrder:['iNoOrden'],
		directFn: InversionesAction.obtenerContactoOrden, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeContactos, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					BFwrk.Util.msgShow('No hay contactos en esta orden', 'INFO');
					return;
				}
				Ext.getCmp(PF + 'cmbContactos').setValue(records[0].get('descripcion'));
			}
		}
	}); 
	
	//combo cmbContactos
	NS.cmbContactos = new Ext.form.ComboBox({
		store: NS.storeContactos,
		name: PF + 'cmbContactos',
		id: PF + 'cmbContactos',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 360,
        y: 15,
        width: 220,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un contacto',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 
				}
			}
		}
	});
	//Declaracion del grid de vencimientos
	NS.storeVencimientoDeInversion = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			sFecha: '',
			bInvInterna: false,
			sSrefMov: '',
			iNoOrden: 0,
			noEmpresa: 0
		},
		root: '',
		paramOrder:['sFecha', 'bInvInterna', 'sSrefMov', 'iNoOrden', 'noEmpresa'],
		directFn: InversionesAction.obtenerVencimientosInversion, 
		//idProperty: 'noOrden', 
		fields: [
			{name: 'sFecValor'},
			{name: 'idBanco'},
			{name: 'descBancoBenef'},
			{name: 'idChequera'},
			{name: 'valorTasa'},
			{name: 'importe'},
			{name: 'interes'},
			{name: 'isr'},
			{name: 'idCveOperacion'},
			{name: 'cveOperacionB'},
			{name: 'cveOperacionC'},
			{name: 'noFolioDet'},
			{name: 'folioDetB'},
			{name: 'folioDetC'},
			{name: 'folioRef'},
			{name: 'idDivisa'},
			{name: 'noCuenta'},
			{name: 'idTipoOperacion'},
			{name: 'tipoOperacionB'},
			{name: 'tipoOperacionC'},
			{name: 'noDocto'},
			{name: 'diasInv'},
			{name: 'noEmpresa'},
			{name: 'totalImporte'},
			{name: 'instFinan'},
			{name: 'nomContacto'},
			{name: 'interesAnt'},
			{name: 'impuestoAnt'},
			{name: 'nota'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLiqOrdenesInversion, msg:"Buscando..."});
				mascara.hide();
				var uImporte = 0;
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No hay inversiones para vencer...');
					return;
				}
				for(var i = 0; i< records.length; i = i + 1)
				{
					uImporte = uImporte + records[i].get('totalImporte');
				}
				//Ext.getCmp(PF + 'txtTotal').setValue(BFwrk.Util.formatNumber(uImporte));
				Ext.getCmp(PF + 'txtTotal').setValue(Ext.util.Format.number(uImporte, '0.0000'));
			}
		}
	});
	
	
	
	NS.gridVencimientoDeInversion = new Ext.grid.GridPanel({
    store : NS.storeVencimientoDeInversion,
    id:'gridVencimientoDeInversion',    
    cm: new Ext.grid.ColumnModel({
        defaults: 
        {
            width: 120,
            value:true,
            sortable: true
        },
           columns: [
            NS.smCheck,
            {
				header :'Empresa',
				width :80,
				sortable :true,
				dataIndex :'noEmpresa',
				hidden: true
			},{
				header :'Número Orden',
				width : 90,
				sortable :true,
				dataIndex :'noDocto'
			},{
				header :'Institución',
				width :130,
				sortable :true,
				dataIndex :'instFinan'
			},{
				header :'Fecha Venc.',
				width :80,
				sortable :true,
				dataIndex :'sFecValor',
				hidden: false
			},{
				header :'Banco',
				width :130,
				sortable :true,
				dataIndex :'descBancoBenef'
			},{
				header :'Chequera',
				width :100,
				sortable :true,
				dataIndex :'idChequera'
			},{
				header :'Tasa',
				width :50,
				sortable :true,
				dataIndex :'valorTasa'
			},{
				header :'Capital',
				width :100,
				sortable :true,
				dataIndex :'importe',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Interes',
				width :100,
				sortable :true,
				dataIndex :'interes',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Impuesto',
				width :80,
				sortable :true,
				dataIndex :'isr',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Total',
				width :100,
				sortable :true,
				dataIndex :'totalImporte',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Divisa',
				width :50,
				sortable :true,
				dataIndex :'idDivisa'
			},{
				header :'Contacto',
				width :200,
				sortable :true,
				dataIndex :'nomContacto'
			},{
				header :'Cve. Operacion A',
				width :100,
				sortable :true,
				dataIndex :'idCveOperacion',
				hidden: true
			},{
				header :'Cve. Operacion B',
				width :100,
				sortable :true,
				dataIndex :'cveOperacionB',
				hidden: true
			},{
				header :'Cve. Operacion C',
				width :100,
				sortable :true,
				dataIndex :'cveOperacionC',
				hidden: true
			},{
				header :'Folio Det A',
				width :100,
				sortable :true,
				dataIndex :'noFolioDet',
				hidden: true
			},{
				header :'Folio Det B',
				width :80,
				sortable :true,
				dataIndex :'folioDetB',
				hidden: true
			},{
				header :'Folio Det C',
				width :80,
				sortable :true,
				dataIndex :'folioDetC',
				hidden: true
			},{
				header :'Folio Ref A',
				width :80,
				sortable :true,
				dataIndex :'folioRef',
				hidden: true
			},{
				header :'Folio Ref B',
				width :80,
				sortable :true,
				dataIndex :'folioRef',
				hidden: true
			},{
				header :'Folio Ref C',
				width :80,
				sortable :true,
				dataIndex :'folioRef',
				hidden: true
			},{
				header :'Interes Ant',
				width :80,
				sortable :true,
				dataIndex :'interesAnt',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Impuesto Ant',
				width :80,
				sortable :true,
				dataIndex :'impuestoAnt',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'No. Cuenta',
				width :80,
				sortable :true,
				dataIndex :'noCuenta',
				hidden: true
			}/*
			,{
				header :'Nota',
				width :80,
				sortable :true,
				dataIndex :'nota',
				hidden : true
			},*/
           ]
        }),
        sm: NS.smCheck,
        columnLines: true,
        x:0,
        y:0,
        width:960,
        height:250,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			var recordsGrid = NS.gridVencimientoDeInversion.getSelectionModel().getSelections();
          			
          			if(recordsGrid.length > 0)
          			{
          				
          				//Ext.getCmp(PF + 'btnContacto').setDisabled(false);
          				NS.storeContactos.baseParams.iNoOrden = parseInt(recordsGrid[0].get('noDocto'));
	          			Ext.getCmp(PF + 'txtCapital').setValue(recordsGrid[0].get('importe'));
	          			Ext.getCmp(PF + 'txtInteres').setValue(recordsGrid[0].get('interes'));
	          			Ext.getCmp(PF + 'txtInteresAnt').setValue(recordsGrid[0].get('interesAnt'));
	          			Ext.getCmp(PF + 'txtImpuesto').setValue(recordsGrid[0].get('isr'));
	          			Ext.getCmp(PF + 'txtImpuestoAnt').setValue(recordsGrid[0].get('impuestoAnt'));
	          			Ext.getCmp(PF + 'txtAreaNotas').setValue(recordsGrid[0].get('nota'));
	          			NS.uTasa = recordsGrid[0].get('valorTasa');
	          			Ext.getCmp(PF + 'btnInteres').setDisabled(false);
          			}
          			else
          			{
          				//Ext.getCmp(PF + 'btnContacto').setDisabled(true);
          				Ext.getCmp(PF + 'btnInteres').setDisabled(true);
	          			Ext.getCmp(PF + 'txtCapital').setValue('0.0');
	          			Ext.getCmp(PF + 'txtInteres').setValue('0.0');
	          			Ext.getCmp(PF + 'txtInteresAnt').setValue('0.0');
	          			Ext.getCmp(PF + 'txtImpuesto').setValue('0.0');
	          			Ext.getCmp(PF + 'txtImpuestoAnt').setValue('0.0');
          			}
        		}
        	}
        }
    });
	
	
	NS.agregarDatosGrid = function(uTasa, uInteres, uImpuesto, uTotal){
		var iIndice = 0;
		var sumTotal = 0;
		var datosStoreGridVencimientos = NS.storeVencimientoDeInversion.data.items;
		var datosGridVencimientos = NS.gridVencimientoDeInversion.getStore().recordType;
		var recordsGridVencimientos = NS.gridVencimientoDeInversion.getSelectionModel().getSelections();
		var datos = new datosGridVencimientos({
			fecValor: recordsGridVencimientos[0].get('sFecValor'),
			idBanco: recordsGridVencimientos[0].get('idBanco'),
			descBancoBenef: recordsGridVencimientos[0].get('descBancoBenef'),
			idChequera: recordsGridVencimientos[0].get('idChequera'),
			valorTasa: uTasa,
			importe: recordsGridVencimientos[0].get('importe'),
			interes: uInteres,
			isr: uImpuesto,
			idCveOperacion: recordsGridVencimientos[0].get('idCveOperacion'),
			cveOperacionB: recordsGridVencimientos[0].get('cveOperacionB'),
			cveOperacionC: recordsGridVencimientos[0].get('cveOperacionC'),
			noFolioDet: recordsGridVencimientos[0].get('noFolioDet'),
			folioDetB: recordsGridVencimientos[0].get('folioDetB'),
			folioDetC: recordsGridVencimientos[0].get('folioDetB'),
			folioRef: recordsGridVencimientos[0].get('folioRef'),
			idDivisa: recordsGridVencimientos[0].get('idDivisa'),
			noCuenta: recordsGridVencimientos[0].get('noCuenta'),
			idTipoOperacion: recordsGridVencimientos[0].get('idTipoOperacion'),
			tipoOperacionB: recordsGridVencimientos[0].get('tipoOperacionB'),
			tipoOperacionC: recordsGridVencimientos[0].get('tipoOperacionC'),
			noDocto: recordsGridVencimientos[0].get('noDocto'),
			diasInv: recordsGridVencimientos[0].get('diasInv'),
			noEmpresa: recordsGridVencimientos[0].get('noEmpresa'),
			totalImporte: uTotal,
			instFinan: recordsGridVencimientos[0].get('instFinan'),
			nomContacto: recordsGridVencimientos[0].get('nomContacto'),
			interesAnt: recordsGridVencimientos[0].get('interesAnt'),
			impuestoAnt: recordsGridVencimientos[0].get('impuestoAnt'),
			nota: recordsGridVencimientos[0].get('nota')
		});
		
		NS.gridVencimientoDeInversion.getSelectionModel().getSelections()[0].set("interes", uInteres);
		NS.gridVencimientoDeInversion.getSelectionModel().getSelections()[0].set("isr", uImpuesto);
		NS.gridVencimientoDeInversion.getSelectionModel().getSelections()[0].set("totalImporte", uTotal);
		
//		for(var i = 0; i < datosStoreGridVencimientos.length ; i++){
//			if(recordsGridVencimientos[0].get('noDocto') === datosStoreGridVencimientos[i].get('noDocto')){
//				iIndice = i;
//				
//				NS.gridVencimientoDeInversion.getSelectionModel().getSelections()[iIndice].set("interes", uInteres);
//				
////				NS.storeVencimientoDeInversion.remove(datosStoreGridVencimientos[iIndice]);
//			}
//		}
		
		//NS.gridVencimientoDeInversion.stopEditing();
		//NS.storeVencimientoDeInversion.insert(iIndice, datos);
		//NS.smCheck.selectRow(iIndice);
		NS.gridVencimientoDeInversion.getView().refresh();
		
		for(var c = 0; c < datosStoreGridVencimientos.length ; c = c + 1)
			sumTotal = sumTotal + parseFloat(datosStoreGridVencimientos[c].get('importe'));
		Ext.getCmp(PF + 'txtTotal').setValue(sumTotal > 0 ? BFwrk.Util.formatNumber(sumTotal) : '0.0');
		
		
		NS.AjusteDeInversion.hide();
	};
	//Inicia Declaración de la ventana de AjusteDeInversión
	
	//Variables utilizadas para la ventana
	NS.VariablesWin =  function(){
		//NS.uTasa = 0;
		NS.iDiasAnual = 0;
		NS.iNoInstitucion = 0;
		NS.iContrato = 0;
		NS.bRealizoCalculo = false;
	};
	NS.VariablesWin();
	
	//Store para obtener los dias_anual
	NS.storeDiasAnual = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			sFechaVenc : '',
			iBanco : 0,
			sChequera : '',
			uTasa : 0,
			iCuenta : 0,
			iNoOrden : 0
		},
		root: '',
		paramOrder:['sFechaVenc','iBanco', 'sChequera', 'uTasa', 'iCuenta', 'iNoOrden'],
		directFn: InversionesAction.obtenerDiasAnual, 
		fields: [
			 {name: 'diasAnual'},
			 {name: 'noInstitucion'},
			 {name: 'contrato'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDiasAnual, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					//BFwrk.Util.msgShow('No se encontraron dias anual', 'INFO');
					return;
				}
				NS.iDiasAnual = records[0].get('diasAnual');
				NS.iNoInstitucion = records[0].get('noInstitucion');
				NS.iContrato = records[0].get('contrato');
			}
		}
	});
	
	//Funcion para llamadar a calcularInteres
	NS.calcularInteres = function(){
		var recordsGridVenc = NS.gridVencimientoDeInversion.getSelectionModel().getSelections();
		var uCapital = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtCapital').getValue());
		var uTasa = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtTasaInteresWin').getValue());
		
		InversionesAction.calcularInteres(parseFloat(uCapital), parseFloat(uTasa), 
											parseInt(recordsGridVenc[0].get('diasInv')), NS.iDiasAnual, function(response, e){
												NS.bRealizoCalculo = true;
												if(response !== null && response !== undefined)
													Ext.getCmp(PF + 'txtInteresWin').setValue(BFwrk.Util.formatNumber(response));
												else
													BFwrk.Util.msgShow('Ocurrio un error al calcular el interes','INFO');
		});
	};
	//Funcion para llamar a calcularImpuesto
	NS.calcularImpuesto = function(){
		var recordsGridVenc = NS.gridVencimientoDeInversion.getSelectionModel().getSelections();
		var uCapital = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtCapital').getValue());
		var uTasa = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtTasaInteresWin').getValue());
		
		InversionesAction.calcularImpuesto(parseFloat(uCapital), parseInt(recordsGridVenc[0].get('diasInv')), NS.iDiasAnual, parseInt(NS.iContrato), 
											parseFloat(recordsGridVenc[0].get('noCuenta')),	parseInt(NS.iNoInstitucion), function(response, e){
												NS.bRealizoCalculo = true;
												if(response !== null && response !== undefined && response > 0)
													Ext.getCmp(PF + 'txtImpuestoWin').setValue(BFwrk.Util.formatNumber(response));
												else
													BFwrk.Util.msgShow('Ocurrio un error al calcular el impuesto','INFO');
													
		});
	};
	
	NS.modificarTasa = function(uTasa, iOrden, sFecha, noEmpresa){
		InversionesAction.modificarTasa(uTasa, iOrden, sFecha, noEmpresa, function(response, e){
			if(response !== null && response !== undefined && response <= 0)
				BFwrk.Util.msgShow('No se pudo modificar la tasa','ERROR');
		});
	};
	
	NS.AjusteDeInversion = new Ext.Window({
	    title: 'Ajuste de  inversiones',
	    modal: true,
	    shadow: true,
	    width: 580,
	    height: 400,
	    padding: 10,
	    layout: 'absolute',
	    autScroll: true,
	    plain: true,
	    id: PF + 'AjusteDeInversiones',
	    name: PF + 'AjusteDeInversiones',
	    closable: false,
	        items : [
	            {
	                xtype: 'fieldset',
	                title: '',
	                height: 320,
	                width: 550,
	                x: 10,
	                y: 10,
	                layout: 'absolute',
	                id: 'fSetAjusteInv',
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Tasa:',
	                        x: 0,
	                        y: 20
	                    },
	                    {
	                        xtype: 'textfield',
	                        x: 110,
	                        y: 20,
	                        name: PF + 'txtTasaInteresWin',
	                        id: PF + 'txtTasaInteresWin',
	                        value: '0.0',
	                        disabled: true
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Interés:',
	                        x: 0,
	                        y: 60
	                    },
	                    {
	                        xtype: 'textfield',
	                        x: 110,
	                        y: 60,
	                        name: PF + 'txtInteresWin',
	                        id: PF + 'txtInteresWin',
	                        value : '0.0',
	                        disabled: true
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Impuesto:',
	                        x: 290,
	                        y: 60
	                    },
	                    {
	                        xtype: 'textfield',
	                        x: 380,
	                        y: 60,
	                        name: PF + 'txtImpuestoWin',
	                        id: PF + 'txtImpuestoWin',
	                        disabled: true
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Interes Calculado:',
	                        x: 0,
	                        y: 100,
	                        hidden:true
	                    },
	                    {
	                        xtype: 'textfield',
	                        x: 110,
	                        y: 100,
	                        name: PF + 'txtInteresCalWin',
	                        id: PF + 'txtInteresCalWin',
	                        disabled: true,
	                        hidden:true
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Impuesto Calculado:',
	                        x: 260,
	                        y: 100,
	                        hidden:true
	                    },
	                    {
	                        xtype: 'textfield',
	                        x: 380,
	                        y: 100,
	                        name: PF + 'txtImpuestoCalWin',
	                        id: PF + 'txtImpuestoCalWin',
	                        value: '0.0',
	                        disabled: true,
	                        hidden:true
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Ajuste Interes:',
	                        x: 0,
	                        y: 100
	                    },
	                    {
	                        xtype: 'textfield',
	                        x: 110,
	                        y: 100,
	                        name: PF + 'txtAjusteInteres',
	                        id: PF + 'txtAjusteInteres',
	                        value: '0.0'
	                        //disabled: true
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Ajuste ISR:',
	                        x: 290,
	                        y: 100
	                    },
	                    {
	                        xtype: 'textfield',
	                        x: 380,
	                        y: 100,
	                        name: PF + 'txtAjusteIsr',
	                        id: PF + 'txtAjusteIsr',
	                        value: '0.0'
	                        //disabled: true
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        width: 120,
	                        height: 40,
	                        x: 250,
	                        y: 10,
	                        layout: 'absolute',
	                        id: 'fSetInteresIsr',
	                        hidden:true,
	                        items: [
	                            {
	                                xtype: 'radio',
	                                boxLabel: 'Interés ',
	                                id: PF + 'optInteres',
	                                name: PF + 'optInteres',
	                               // checked: true,
	                                x: 0,
	                                y: 0,
	                                listeners:{
	                                	check:{
	                                		fn: function(opt, valor){
	                                			if(valor)
	                                			{
	                                				//Ext.getCmp(PF + 'optInteres').setValue(true);
	                                				Ext.getCmp(PF + 'optIsr').setValue(false);
	                                				Ext.getCmp(PF + 'txtImpuestoWin').setDisabled(true);
	                                				Ext.getCmp(PF + 'txtTasaInteresWin').setDisabled(false);
	                                			}
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'radio',
	                                boxLabel: 'ISR',
	                                id: PF + 'optIsr',
	                                name: PF + 'optIsr',
	                                x: 60,
	                                y: 0,
	                                listeners:{
	                                	check:{
	                                		fn: function(opt, valor){
	                                			if(valor)
	                                			{
	                                				Ext.getCmp(PF + 'optInteres').setValue(false);
	                                				Ext.getCmp(PF + 'optIsr').setValue(true);
	                                				Ext.getCmp(PF + 'txtImpuestoWin').setDisabled(false);
	                                				Ext.getCmp(PF + 'txtTasaInteresWin').setDisabled(true);
	                                			}
	                                		}
	                                	}
	                                }
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Calcular',
	                        x: 380,
	                        y: 20,
	                        width: 60,
	                        id: PF + 'btnImpIsr',
	                        name: PF + 'btnImpIsr',
	                        hidden:true,
	                        listeners:{
	                        	click:{
	                        		fn: function(e){
	                        			if(Ext.getCmp(PF + 'optInteres').getValue())
	                        				NS.calcularInteres();
	                        			else if(Ext.getCmp(PF + 'optIsr').getValue())
	                        				NS.calcularImpuesto(); 
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Notas:',
	                        x: 0,
	                        y: 180,
	                        width: 520,
	                        height: 110,
	                        layout: 'absolute',
	                        id: PF +'fSetNotas',
	                        name: PF +'fSetNotas',
	                        items: [
	                            {
	                                xtype: 'textarea',
	                                anchor: '100%',
	                                x: 10,
	                                y: 0,
	                                name: PF + 'txtAreaNotas',
	                                id: PF + 'txtAreaNotas'
	                            }
	                        ]
	                    }
	                ]
	            },
	            {
	                xtype: 'button',
	                text: 'Ejecutar',
	                x: 350,
	                y: 340,
	                width: 80,
	                id: PF + 'btnEjecutarWin',
	                name: PF + 'btnEjecutarWin',
	                listeners:{
	                	click:{
	                		fn: function(e){
	                			var recordsGridVenc = NS.gridVencimientoDeInversion.getSelectionModel().getSelections();
	                			var uAjusteInteres = 0;
	                			var uAjusteImpuesto = 0;
	                			var uA = 0;
	                			var uInteresAnt = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtAjusteInteres').getValue()));
	                			var uInteresAct = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtInteresWin').getValue()));
	                			var uTasaActual = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtTasaInteresWin').getValue()));
	                			var uImpuestoAct = 0;
	                			var uImporteTotal = 0;
	                			var uCapital = parseFloat(recordsGridVenc[0].get('importe'));
	                			
	                		
	                			//Se coloca esta funcion para que modifique el interes y el isr de manera manual y directa no como estaba.
	                			// ya que aqui en caabsa no se necesitara asi como esta originalmente, además de que nadie entiende que hace esto att: victor
	                			//NS.ajustesIntImp(recordsGridVenc);
	                			//return;
	                			
	                			if(uInteresAnt !== uInteresAct) {
	                				
	                				uAjusteInteres = Ext.util.Format.number((uInteresAnt - uInteresAct), '0.00');
	                				
	                				Ext.Msg.show({
									title: 'Información SET',
									msg: 'El ajuste a realizar es por ' + uAjusteInteres + '  ¿es correcto?',
										buttons: {
											yes: true,
											no: true,
											cancel: false
										},
										icon: Ext.MessageBox.QUESTION,
										fn: function(btn) {
											if(btn === 'yes')
											{
												if(NS.uTasa == uTasaActual)
												{
													/*if(!NS.bRealizoCalculo)
													{
														if(Ext.getCmp(PF + 'optInteres').getValue())
														{
															Ext.Msg.show({
															title: 'Información SET',
															msg: 'No se calculo el nuevo interes, ¿deseas recalcularlo?',
																buttons: {
																	yes: true,
																	no: true,
																	cancel: false
																},
																icon: Ext.MessageBox.QUESTION,
																fn: function(btn) {
																	if(btn === 'yes')
												                     	NS.calcularInteres();	
																	else if(btn === 'no')
																		return;
																	else
																		return;
																}
															});
														}
					                        			else if(Ext.getCmp(PF + 'optIsr').getValue())
					                        			{
					                        				Ext.Msg.show({
															title: 'Información SET',
															msg: 'No se calculo el nuevo ISR, ¿deseas recalcularlo?',
																buttons: {
																	yes: true,
																	no: true,
																	cancel: false
																},
																icon: Ext.MessageBox.QUESTION,
																fn: function(btn) {
																	if(btn === 'yes')
												                     	NS.calcularImpuesto();	
																	else if(btn === 'no')
																		valido = false;
																	else
																		valido = false;
																}
															});
					                        			}
													}
													Ext.getCmp(PF + 'txtAreaNotas').setValue('CAMBIO DE TASA DE ' + NS.uTasa + ' A ' + uTasaActual);
													
													NS.modificarTasa(uTasaActual, recordsGridVenc[0].get('noDocto'), 
																		recordsGridVenc[0].get('fecValor').substring(0,10),
																		parseInt(recordsGridVenc[0].get('noEmpresa')));
													*/
													Ext.getCmp(PF + 'txtInteres').setValue((Ext.getCmp(PF + 'txtAjusteInteres').getValue()));
													Ext.getCmp(PF + 'txtImpuesto').setValue((Ext.getCmp(PF + 'txtAjusteIsr').getValue()));
													
													//agregar datos al grid principal
													uInteresAct = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtAjusteInteres').getValue()));
													uImpuestoAct = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtAjusteIsr').getValue()));
													uImporteTotal = uCapital + uInteresAct - uImpuestoAct;
													NS.agregarDatosGrid(uTasaActual, uInteresAct, uImpuestoAct, uImporteTotal);
												}
											}
											else if(btn === 'no')
												return;
											else
												return;
										}
									});
	                			}
	                		}
	                	}
	                }
	            },
	            {
	                xtype: 'button',
	                text: 'Cerrar',
	                x: 460,
	                y: 340,
	                width: 80,
	                id: PF + 'btnCerrarWin',
	                name: PF + 'btnCerrarWin',
	                listeners: {
	                	click:{
	                		fn: function(e){
	                			NS.VariablesWin();
	                			NS.AjusteDeInversion.hide();
	                		}
	                	}
	                }
	            }
	        ]
	});
		
	//Termina Declaración de la ventana de AjusteDeInversión
	
	NS.ajustesIntImp = function() {
		var recordsGridVenc = NS.gridVencimientoDeInversion.getSelectionModel().getSelections();
		var matInv = new Array();
		var arrayInv = {};
		
		arrayInv.noEmpresa = recordsGridVenc[0].get('noEmpresa');
		arrayInv.noOrden = recordsGridVenc[0].get('noDocto');
		arrayInv.noFolioDetInt = recordsGridVenc[0].get('folioDetB');
		arrayInv.noFolioDetIsr = recordsGridVenc[0].get('folioDetC');
		arrayInv.interes = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtAjusteInteres').getValue());
		arrayInv.isr = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtAjusteIsr').getValue());
		arrayInv.nota = Ext.getCmp(PF + 'txtAreaNotas').getValue();
		
		matInv[0] = arrayInv;
		var jsonStrIntImpInv = Ext.util.JSON.encode(matInv);
		
		InversionesAction.modificarIntImpInv(jsonStrIntImpInv, function(response, e){
			if(response !== null && response !== undefined && response != '')
				BFwrk.Util.msgShow('SET', response + '!!');
			
			NS.AjusteDeInversion.hide();
			NS.limpiaCamposImp();
			NS.buscarVenc();
		});
	};
	
	NS.limpiar = function() {
		Ext.getCmp(PF + 'txtTasaInteresWin').setValue('0');
		Ext.getCmp(PF + 'txtInteresWin').setValue('0.00');
		Ext.getCmp(PF + 'txtImpuestoWin').setValue('0.00');
		Ext.getCmp(PF + 'txtInteresCalWin').setValue('0.00');
		Ext.getCmp(PF + 'txtImpuestoCalWin').setValue('0.00');
		Ext.getCmp(PF + 'txtAjusteInteres').setValue('0.00');
		Ext.getCmp(PF + 'txtAjusteIsr').setValue('0.00');
	};
	
	NS.limpiaCamposImp = function() {
		Ext.getCmp(PF + 'txtImpuesto').setValue('0.00');
		Ext.getCmp(PF + 'txtInteresAnt').setValue('0.00');
		Ext.getCmp(PF + 'txtImpuestoAnt').setValue('0.00');
		Ext.getCmp(PF + 'txtInteres').setValue('0.00');
		Ext.getCmp(PF + 'txtCapital').setValue('0.00');
		Ext.getCmp(PF + 'cmbHora').reset();
	};
	
	NS.buscarVenc = function(){
		mascara.show();
		Ext.getCmp(PF + 'cmbHora').setValue(NS.sHora);
		NS.storeVencimientoDeInversion.baseParams.sFecha = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFecha').getValue());
		NS.storeVencimientoDeInversion.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
		NS.storeVencimientoDeInversion.load();
	};

	NS.imprimirReporte = function(noDocumento){
		var recordsGridOrdenes = NS.gridVencimientoDeInversion.getSelectionModel().getSelections();
		var strParams = '';
		strParams = '?nomReporte=ComprobanteVencimiento';
		
		strParams += '&'+'nomParam1=noDocto';
		strParams += '&'+'valParam1=' + noDocumento;
		strParams += '&'+'nomParam2=nomreEmpresa';
		strParams += '&'+'valParam2=' + nomreEmpresa;

		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		return;
	};

	NS.VencimientoDeInversion = new Ext.form.FormPanel({
    title: 'Vencimiento de Inversión',
    width: 1020,
    height: 700,
    padding: 10,
    layout: 'absolute',
    id: PF + 'VencimientoDeInversion',
    name: PF + 'VencimientoDeInversion',
    autoScroll: true,
    renderTo: NS.tabContId,
    frame: true,
       items : [
            {
                xtype: 'fieldset',
                title: '',
                width: 1010,
                height: 490,
                x: 20,
                y: 5,
                layout: 'absolute',
                id: 'fSetVencimientoInversion',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 10,
                        y: 0
                    },
                    NS.txtNoEmpresa,
                    NS.cmbEmpresa,
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 15,
                        width: 320,
                        name: PF + 'txtNomEmpresa',
                        id: PF + 'txtNomEmpresa',
                        value: apps.SET.NOM_EMPRESA,
                        hidden: true
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha:',
                        x: 470,
                        y: 0
                    },
                    {
                        xtype: 'datefield',
                        format: 'd/m/Y',
                        x: 470,
                        y: 15,
                        name: PF + 'txtFecha',
                        id: PF + 'txtFecha',
                        value: apps.SET.FEC_HOY
                    },/*
                    {
                        xtype: 'label',
                        text: 'Contacto:',
                        x: 360,
                        y: 0,
                        hidden:true
                    },
                    	//NS.cmbContactos,
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 585,
                        y: 15,
                        width: 30,
                        id: PF + 'btnContacto',
                        name: PF + 'btnContacto',
                        hidden:true,
                        disabled: true,
                        listeners:{
                        	click:{
                        		fn: function(e){
                        			NS.storeContactos.load();
                        		}
                        	}
                        }
                    },*/
                    {
                        xtype: 'label',
                        text: 'Hora:',
                        x: 600,
                        y: 0
                    },
                    {
                    	xtype: 'textfield',
                        x: 600,
                        y: 15,
                        width: 95,
                        name: PF + 'cmbHora',
                        id: PF + 'cmbHora',
                        readonly:true,
                        listeners:{
                        	change:{
                        		fn: function(e){
                    			NS.obtenerHora();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'label',
                        text: '_________________________________________________________________________________________________________________________________________________________',
                        x: 10,
                        y: 36,
                        width: 800
                    },
                    {
                        xtype: 'label',
                        text: 'Capital:',
                        x: 10,
                        y: 60
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 75,
                        width: 150,
                        name: PF + 'txtCapital',
                        id: PF + 'txtCapital',
                        value: '0.0',
                        readOnly: true
                    },
                    {
                        xtype: 'label',
                        text: 'Interes:',
                        x: 180,
                        y: 60
                    },
                    {
                        xtype: 'textfield',
                        x: 180,
                        y: 75,
                        width: 150,
                        name: PF + 'txtInteres',
                        id: PF +'txtInteres',
                        value: '0.0',
                        readOnly: true
                    },
                    {
                        xtype: 'button',
                        text: 'Ajustes',
                        x: 860,
                        y: 75,
                        width: 80,
                        id: PF + 'btnInteres',
                        name: PF + 'btnInteres',
                        disabled: true,
                        listeners:{
                        	click:{
                        		fn: function(e){
                        			var recordsGridVenc = NS.gridVencimientoDeInversion.getSelectionModel().getSelections();
                        			
                        			NS.limpiar();
                        			
                        			if(recordsGridVenc.length <= 0) {
                        				Ext.Msg.alert('SET', 'Necesita seleccionar un registro para continuar!!');
                        				return;
                        			}
                        			Ext.getCmp(PF + 'txtTasaInteresWin').setValue(NS.uTasa);
                        			Ext.getCmp(PF + 'txtInteresWin').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF + 'txtInteres').getValue()));
                        			Ext.getCmp(PF + 'txtImpuestoWin').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF + 'txtImpuesto').getValue()));
                        			Ext.getCmp(PF + 'txtInteresCalWin').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF + 'txtInteresAnt').getValue()));
                        			Ext.getCmp(PF + 'txtImpuestoCalWin').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF + 'txtImpuestoAnt').getValue()));
                        			Ext.getCmp(PF + 'txtAjusteInteres').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF + 'txtInteresAnt').getValue()));
                        			Ext.getCmp(PF + 'txtAjusteIsr').setValue(BFwrk.Util.formatNumber(Ext.getCmp(PF + 'txtImpuestoAnt').getValue()));
                        			
                        			/*Ext.getCmp(PF + 'txtAjusteInteres').setValue(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtInteres').getValue()))
                        													     - parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtInteresAnt').getValue())));
                        			Ext.getCmp(PF + 'txtAjusteIsr').setValue(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImpuesto').getValue()))
                        													 - parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImpuestoAnt').getValue())));
                        			
                        			*/
                        			//Inicia llamada para diasAnual
                        			NS.storeDiasAnual.baseParams.sFechaVenc = recordsGridVenc[0].get('sFecValor').substring(0,10);
                        			NS.storeDiasAnual.baseParams.iBanco = parseInt(recordsGridVenc[0].get('idBanco'));
                        			NS.storeDiasAnual.baseParams.sChequera = recordsGridVenc[0].get('idChequera');
                        			NS.storeDiasAnual.baseParams.uTasa = parseFloat(recordsGridVenc[0].get('valorTasa'));
                        			NS.storeDiasAnual.baseParams.iCuenta = parseInt(recordsGridVenc[0].get('noCuenta'));
                        			NS.storeDiasAnual.baseParams.iNoOrden = parseInt(recordsGridVenc[0].get('noDocto'));
                        			NS.storeDiasAnual.load();
                        			
                        			NS.AjusteDeInversion.show();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'Impuesto:',
                        x: 520,
                        y: 60
                    },
                    {
                        xtype: 'textfield',
                        x: 520,
                        y: 75,
                        width: 150,
                        name: PF + 'txtImpuesto',
                        id: PF + 'txtImpuesto',
                        value: '0.0',
                        readOnly: true
                    },
                    {
                        xtype: 'label',
                        text: 'Int. Anterior:',
                        x: 350,
                        y: 60,
                        readOnly: true
                    },
                    {
                        xtype: 'textfield',
                        x: 350,
                        y: 75,
                        width: 150,
                        name: PF + 'txtInteresAnt',
                        id: PF + 'txtInteresAnt',
                        value: '0.0',
                        disabled: true
                    },
                    {
                        xtype: 'label',
                        text: 'Imp. Anterior:',
                        x: 690,
                        y: 60
                    },
                    {
                        xtype: 'textfield',
                        x: 690,
                        y: 75,
                        width: 150,
                        name: PF + 'txtImpuestoAnt',
                        id: PF + 'txtImpuestoAnt',
                        value: '0.0',
                        disabled: true
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Vencimiento de inversión',
                        x: 0,
                        y: 120,
                        height: 290,
                        width: 985,
                        items:[
                        	NS.gridVencimientoDeInversion
                        ]
                    },
                    {
                        xtype: 'label',
                        text: 'Total:',
                        x: 10,
                        y: 420
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 435,
                        name: PF + 'txtTotal',
                        id: PF + 'txtTotal',
                        value: '0.0',
                        readOnly: true
                    },{
                        xtype: 'label',
                        text: 'Tasa Reinversión:',
                        x: 160,
                        y: 420
                    },
                    {
                        xtype: 'textfield',
                        x: 160,
                        y: 435,
                        name: PF + 'txtTasaReinversion',
                        id: PF + 'txtTasaReinversion',
                        value: '0.0'//,
                       // readOnly: true
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 750,
                        y: 15,
                        width: 80,
                        id: PF + 'btnBuscar',
                        name: PF + 'btnBuscar',
                        listeners:{
                        	click:{
                        		fn: function(e){
                        			NS.buscarVenc();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Ven. Parcial',
                        x: 700,
                        y: 435,
                        width: 80,
                        hidden: true,
                        id: 'btnVencimiento'
                        //hidden: true
                    },
                    {
                        xtype: 'button',
                        text: 'Aceptar',
                        x: 790,
                        y: 435,
                        width: 80,
                        id: PF + 'btnAceptar',
                        name: PF + 'btnAceptar',
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			var matrizVencimientos = new Array(); 
                        			var recordsGridVencimientos = NS.gridVencimientoDeInversion.getSelectionModel().getSelections();
                        			if(recordsGridVencimientos.length <= 0)
                        			{
                        				BFwrk.Util.msgShow('Debe seleccionar datos para liberar ','WARNING');	
                        				return;
                        			}
                        			
                        			//Llamada para realizar el vencimiento de la inversión
                       				NS.Vencimiento = function(){
                       					var uTasaReinversion = parseFloat(Ext.getCmp(PF + 'txtTasaReinversion').getValue());
                       					var uImporteText = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtCapital').getValue()));
	                        			var uInteresText = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtInteres').getValue()));
	                        			var uImpuestoText = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImpuesto').getValue()));
	                        			var noDocumento = 0;
	                       				
                       					for(var i = 0 ; i < recordsGridVencimientos.length ; i = i + 1)
                       					{
                       						var arrayVenc = {};
	                       						arrayVenc.fecValor = recordsGridVencimientos[i].get('sFecValor');
	                       						arrayVenc.idBanco = recordsGridVencimientos[i].get('idBanco');
	                       						arrayVenc.descBancoBenef = recordsGridVencimientos[i].get('descBancoBenef');
	                       						arrayVenc.idChequera = recordsGridVencimientos[i].get('idChequera');
	                       						arrayVenc.valorTasa = recordsGridVencimientos[i].get('valorTasa');
	                       						arrayVenc.importe = recordsGridVencimientos[i].get('importe');
	                       						arrayVenc.interes = recordsGridVencimientos[i].get('interes');
	                       						arrayVenc.isr = recordsGridVencimientos[i].get('isr');
	                       						arrayVenc.idCveOperacion = recordsGridVencimientos[i].get('idCveOperacion');
	                       						arrayVenc.cveOperacionA = recordsGridVencimientos[i].get('idCveOperacion');
	                       						arrayVenc.cveOperacionB = recordsGridVencimientos[i].get('cveOperacionB');
	                       						arrayVenc.cveOperacionC = recordsGridVencimientos[i].get('cveOperacionC');
	                       						arrayVenc.noFolioDet = recordsGridVencimientos[i].get('noFolioDet');
	                       						arrayVenc.folioDetA = recordsGridVencimientos[i].get('noFolioDet');
	                       						arrayVenc.folioDetB = recordsGridVencimientos[i].get('folioDetB');
	                       						arrayVenc.folioDetC = recordsGridVencimientos[i].get('folioDetC');	
	                       						arrayVenc.folioRef = recordsGridVencimientos[i].get('folioRef');
	                       						arrayVenc.folioRefA = recordsGridVencimientos[i].get('folioRef');
	                       						arrayVenc.folioRefB = recordsGridVencimientos[i].get('folioRef');
	                       						arrayVenc.folioRefC = recordsGridVencimientos[i].get('folioRef');
	                       						arrayVenc.idDivisa = recordsGridVencimientos[i].get('idDivisa');
	                       						arrayVenc.noCuenta = recordsGridVencimientos[i].get('noCuenta');
	                       						arrayVenc.idTipoOperacion = recordsGridVencimientos[i].get('idTipoOperacion');
	                       						arrayVenc.noDocto = recordsGridVencimientos[i].get('noDocto');
	                       						noDocumento = recordsGridVencimientos[i].get('noDocto');
	                       						arrayVenc.diasInv = recordsGridVencimientos[i].get('diasInv');
	                       						arrayVenc.noEmpresa = recordsGridVencimientos[i].get('noEmpresa');
	                       						arrayVenc.totalImporte = recordsGridVencimientos[i].get('totalImporte');
	                       						arrayVenc.instFinan = recordsGridVencimientos[i].get('instFinan');
	                       						arrayVenc.nomContacto = recordsGridVencimientos[i].get('nomContacto');
	                       						arrayVenc.interesAnt = recordsGridVencimientos[i].get('interesAnt');
	                       						arrayVenc.impuestoAnt = recordsGridVencimientos[i].get('impuestoAnt');
	                       						arrayVenc.fecValorOriginal = BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecha').getValue());
	                       					matrizVencimientos[i] = arrayVenc;
                       					}
                       					var jsonStringVenc = Ext.util.JSON.encode(matrizVencimientos);
                       					
                       					mascara.show();
                       					
                       					InversionesAction.ejecutarVencimientoInversion(jsonStringVenc, uImporteText, uInteresText, uImpuestoText,
                       																	false, uTasaReinversion,function(response, e){
                       						mascara.hide();
                       						if(response !== null && response !== '' && response !== undefined)
                       							BFwrk.Util.msgShow('' + response.msgUsuario, 'INFO');
                       						NS.buscarVenc();
                       						NS.VariablesWin();
                       						NS.imprimirReporte(noDocumento);
                       					});
                       				};
                       				
                        			if(recordsGridVencimientos[0].get('sFecValor').substring(0, 10) > apps.SET.FEC_HOY)
                       				{
                       					Ext.Msg.show({
										title: 'Información SET',
										msg: 'La fecha de vencimiento es mayor a la fecha de hoy, ¿desea liberar esta inversión?',
											buttons: {
												yes: true,
												no: true,
												cancel: false
											},
											icon: Ext.MessageBox.QUESTION,
											fn: function(btn) {
												if(btn == 'yes')
													NS.Vencimiento();
												if(btn === 'no')
													return;
											    else
											    	return;
											}
										});
                       				}
                       				else
                       				{
                       					NS.Vencimiento();
                       				}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Imprimir',
                        x: 880,
                        y: 435,
                        width: 80,
                        hidden: true,
                        id: 'btnImprimir'
                    }
                ]
            }
        ]
	});
	NS.VencimientoDeInversion.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	//staticCheck("#gridVencimientoDeInversion div div[class='x-panel-ml']","#gridVencimientoDeInversion div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
});