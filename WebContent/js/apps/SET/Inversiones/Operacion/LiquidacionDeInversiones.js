Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	var NS = Ext.namespace('apps.SET.Inversiones.Operacion.LiquidacionOrdenesInversion');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.nomreUsuario = apps.SET.sUserLogin;
	NS.noOrdenes = "";
	NS.todas = false;
	
	NS.idUsuario = apps.SET.iUserId;
	var sName = "";
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
	
	//Objeto para agregar los checks al grid
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true,
		singleSelect: true				
	});
	
	NS.limpiar = function(){
		NS.iNumCta = 0;
		NS.uImporte = 0;
		NS.iIndice = 0;
		NS.bInterna = false;
		NS.fecAlta = '';
		NS.sIdDivisa = '';
	};
	
	NS.limpiarVentana = function(){
		NS.cmbBancoRegreso.reset();
		NS.cmbChequerasRegreso.reset();
		NS.cmbBancoLiquida.reset();
		NS.cmbChequerasLiquida.reset();
		NS.cmbBancosCargo.reset();
		NS.cmbChequeraCargo.reset();
		
		Ext.getCmp(PF + 'txtIdBancoRegreso').reset();
		Ext.getCmp(PF + 'txtIdBancoLiquida').reset();
		Ext.getCmp(PF + 'txtNoContrato').reset();
		Ext.getCmp(PF + 'txtDescContrato').reset();
		Ext.getCmp(PF + 'txtIdBancoCargo').reset();
		Ext.getCmp(PF + 'txtSaldoDisponible').reset();
		Ext.getCmp(PF + 'txtImporteCargo').reset();
		Ext.getCmp(PF + 'txtTotalCargos').reset();
		
		NS.storeGridBancosCargo.removeAll();
		NS.gridBancosCargo.getView().refresh();		
    };
	
	NS.limpiar();
	
	NS.txtNoEmpresa = new Ext.form.TextField({
		x: 60,
	    y: 0,
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
	    			
	    			Ext.getCmp(PF + 'chkTodas').setValue(false);
	    			NS.todas = false;
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
    			mascara.hide();

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
    	x: 120,
        y: 0,
        width: 300,
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
				 	NS.storeLiqOrdenesInversion.removeAll();
	    			NS.gridLiqOrdenesInversion.getView().refresh();
				}
			}
		}
	});
	
	NS.smCheck = new Ext.grid.CheckboxSelectionModel({
				singleSelect : false		
	});
	
	NS.storeLiqOrdenesInversion = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {iAvisoInv: 0, noEmpresa: apps.SET.NO_EMPRESA, iUserId: NS.idUsuario},
		root: '',
		paramOrder:['iAvisoInv', 'noEmpresa', 'iUserId'],
		directFn: InversionesAction.consultarLiquidaOrdenInversion, 
		//idProperty: 'noOrden', 
		fields: [
			{name: 'noOrden'},
			{name: 'nombreCorto'},
			{name: 'plazo'},
			{name: 'importe'},
			{name: 'tasa'},
			{name: 'interes'},
			{name: 'isr'},
			{name: 'noContacto'},
			{name: 'sFecAlta'},
			{name: 'sFecVenc'},
			{name: 'descTipoValor'},
			{name: 'idPapel'},
			{name: 'noCuenta'},
			{name: 'noFolioDet'},
			{name: 'idTipoOperacion'},
			{name: 'idBanco'},
			{name: 'idChequera'},
			{name: 'idBancoReg'},
			{name: 'idChequeraReg'},
			{name: 'idBancoInst'},
			{name: 'idChequeraInst'},
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'neto'},
			{name: 'idDivisa'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLiqOrdenesInversion, msg:"Buscando..."});
				mascara.hide();
				var uImporte = 0;
				var uImporteDls = 0;
				
				if(records.length === null || records.length <= 0)
				{
					//Ext.getCmp(PF + 'cmdGrafica').setDisabled(true);
					Ext.Msg.alert('SET','No existen ordenes de inversión para confirmar...');
					return;
				}
				else
				{
					for(var i = 0; i< records.length; i = i + 1)
					{
						if (records[i].get('idDivisa') == 'DLS')
							uImporteDls = uImporteDls + records[i].get('importe');
						else
							uImporte = uImporte + records[i].get('importe');
					}
					Ext.getCmp(PF + 'txtTotal').setValue(BFwrk.Util.formatNumber(uImporte));
					Ext.getCmp(PF + 'txtTotalDls').setValue(BFwrk.Util.formatNumber(uImporteDls));
					
					//Graficas
		            sName = apps.SET.NO_EMPRESA;
		            //Ext.getCmp(PF + 'cmdGrafica').setDisabled(false);
				}
			}
		}
	});
	
	
	NS.gridLiqOrdenesInversion = new Ext.grid.GridPanel({
    store : NS.storeLiqOrdenesInversion,
    id:'gridLiqOrdenesInversion',  
    cm: new Ext.grid.ColumnModel({
        defaults: {
            width: 120,
            value:true,
            sortable: true
        },
           columns: [
	           NS.smCheck,
	             {
					header :'Núm Orden',
					width :80,
					sortable :true,
					dataIndex :'noOrden'
				},{
					header :'Institución',
					width :130,
					sortable :true,
					dataIndex :'nombreCorto'
				},{
					header :'Plazo',
					width :50,
					sortable :true,
					dataIndex :'plazo'
				},{
					header :'Importe',
					width :90,
					sortable :true,
					dataIndex :'importe',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney
				},{
					header :'Tasa',
					width :70,
					sortable :true,
					dataIndex :'tasa'
				},{
					header :'Interés',
					width :90,
					sortable :true,
					dataIndex :'interes',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney
				},{
					header :'Impuesto',
					width :90,
					sortable :true,
					dataIndex :'isr',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney
				},{
					header :'Neto',
					width :90,
					sortable :true,
					dataIndex :'neto',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney
				},{
					header :'Contacto',
					width :70,
					sortable :true,
					dataIndex :'noContacto',
					hidden: true
				},{
					header :'Fecha Orden',
					width :100,
					sortable :true,
					dataIndex :'sFecAlta'
				},{
					header :'Fecha Vencimiento',
					width :100,
					sortable :true,
					dataIndex :'sFecVenc'
				},{
					header :'Descripción',
					width :80,
					sortable :true,
					dataIndex :'descTipoValor'
				},{
					header :'Tipo Papel',
					width :80,
					sortable :true,
					dataIndex :'idPapel'
				},{
					header :'Cuenta',
					width :80,
					sortable :true,
					dataIndex :'noCuenta',
					hidden : true
				},{
					header :'No Folio Det',
					width :80,
					sortable :true,
					dataIndex :'noFolioDet',
					hidden : true
				},{
					header :'idBanco',
					width :80,
					sortable :true,
					dataIndex :'idBanco',
					hidden : false
				},{
					header :'idChequera',
					width :80,
					sortable :true,
					dataIndex :'idChequera',
					hidden : false
				},{
					header :'idBancoReg',
					width :80,
					sortable :true,
					dataIndex :'idBancoReg',
					hidden : true
				},{
					header :'idChequeraReg',
					width :80,
					sortable :true,
					dataIndex :'idChequeraReg',
					hidden : true
				},{
					header :'idBancoInst',
					width :80,
					sortable :true,
					dataIndex :'idBancoInst',
					hidden : true
				},{
					header :'idChequeraInst',
					width :80,
					sortable :true,
					dataIndex :'idChequeraInst',
					hidden : true
				},{
					header :'idTipoOperacion',
					width :80,
					sortable :true,
					dataIndex :'idTipoOperacion',
					hidden : true
				},{
					header :'noEmpresa',
					width :80,
					sortable :true,
					dataIndex :'noEmpresa',
					hidden : true
				},{
					header :'nomEmpresa',
					width :80,
					sortable :true,
					dataIndex :'nomEmpresa',
					hidden : true
				},{
					header :'idDivisa',
					width :80,
					sortable :true,
					dataIndex :'idDivisa',
					hidden : true
				}
           ]
        }),
        sm: NS.smCheck,
        columnLines: true,
        x:0,
        y:0,
        width:960,
        height:345,
        frame:true,
        listeners:{
        	click:{
        		
//        		fn:function(e){
//          			var ordenSelec = NS.gridLiqOrdenesInversion.getSelectionModel().getSelections();
//          			if(ordenSelec.length > 0)
//          			{
//          				NS.iNumCta = ordenSelec[0].get('noCuenta');
//          				NS.uImporte = ordenSelec[0].get('importe'); 
//          				NS.fecAlta = ordenSelec[0].get('fecAlta');
//          				NS.obtenerContratosLiq();//Se carga la funcion que obtiene los contratos
//          			}
//        		}
        	}
        }
    });
    
    //Inician funciones, stores, combos, para la ventana NS.winLiquidacionOrdenesInv

    //store del combo cmbBancoRegreso
	NS.storeBancoRegreso = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdEmpresa : 0,
			sIdDivisa : ''
		},
		root: '',
		paramOrder:['iIdEmpresa', 'sIdDivisa'],
		root: '',
		directFn: InversionesAction.obtenerBancosRegresoInversion, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records) {
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoRegreso, msg:"Cargando..."});
				
				mascara.hide();
				
				if(records.length == null || records.length <= 0) {
					BFwrk.Util.msgShow('No Existen bancos de regreso para la inversión', 'INFO');
					return;
				}
				NS.accionarBancoRegreso(records[0].get('id'));
			}
		}
	}); 
	
	/*Llamada para obtener un valor de configuraSet, por ahora no se manejan inversiones internas
	GlobalAction.obtenerValorConfiguraSet(parseInt(268), function(response, e){
		if(response !== null && response !== undefined && response !== 'SI')
		{
			NS.storeBancoRegreso.baseParams.sIdDivisa = NS.sIdDivisa;
			NS.storeBancoRegreso.load();
		}
		else
		{
			NS.bInterna = true;
			If pbInversionInterna Then
           	 is_IdEmpresa = frmMonitorInversion.msfDatos.TextMatrix(frmMonitorInversion.msfDatos.row, COL_EMPRESA)
	        Else 'La llamada a este formulario se queda pendiente de migrar
	            is_IdEmpresa = CStr(GI_ID_EMPRESA)
	            txtEmpresa.Text = GS_DESC_EMPRESA
	        End If
		}
	});*/

	//combo cmbBancoReg
	NS.cmbBancoRegreso = new Ext.form.ComboBox({
		store: NS.storeBancoRegreso,
		name: PF + 'cmbBancoRegreso',
		id: PF + 'cmbBancoRegreso',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 60,
        y: 20,
        width: 150,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Banco Regreso',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtIdBancoRegreso',NS.cmbBancoRegreso.getId());
				 	NS.accionarBancoRegreso(combo.getValue());
				}
			}
		}
	});
	
	//store del combo cmbChequerasRegreso
	NS.storeChequeraRegreso = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:
			{
				iBanco : 0,
				iInst : 0,
				sIdDivisa: '',
				noEmpresa: apps.SET.NO_EMPRESA
			},
		root: '',
		paramOrder:['iBanco', 'iInst','sIdDivisa', 'noEmpresa'],
		root: '',
		directFn: InversionesAction.obtenerChequeras, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
			 {name: 'idBanco'},
			 {name: 'tipoChequera'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraRegreso, msg:"Cargando..."});
				
				mascara.hide();
				
				if(records.length === null || records.length <= 0)
				{
					BFwrk.Util.msgShow('No Existen chequeras de regreso', 'INFO');
					return;
				}
				/*for(var i=0; i<records.length; i++) {
					if(records[i].get('tipoChequera') == 'I') {
						Ext.getCmp(PF + 'txtIdBancoRegreso').setValue(records[i].get('id'));
						Ext.getCmp(PF + 'cmbBancoRegreso').setValue(NS.storeBancoRegreso.getById(records[i].get('idBanco')).get('descripcion'));
						Ext.getCmp(PF + 'cmbChequerasRegreso').setValue(records[i].get('descripcion'));
						break;
					}
				}*/
			}
		}
	}); 
	
	//combo para las chequeras de regreso de inversión
	NS.cmbChequerasRegreso = new Ext.form.ComboBox({
		store: NS.storeChequeraRegreso,
		name: PF + 'cmbChequerasRegreso',
		id: PF + 'cmbChequerasRegreso',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 220,
        y: 20,
        width: 150,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Chequera Regreso',
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
	
	NS.accionarBancoRegreso = function(valueCombo){
		NS.cmbChequerasRegreso.reset();
		NS.storeChequeraRegreso.baseParams.iBanco = valueCombo;
		NS.storeChequeraRegreso.baseParams.sIdDivisa = NS.sIdDivisa;
		NS.storeChequeraRegreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
		NS.storeChequeraRegreso.load();
	};
	
	//store de contratos, el cual sirve para lleanar varios campos en la ventana
	NS.storeContratosLiq = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdEmpresa : 0,
			iNumCta : 0
		},
		root: '',
		paramOrder:['iIdEmpresa', 'iNumCta'],
		root: '',
		directFn: InversionesAction.obtenerContratosLiq, 
		idProperty: 'idBancoDep',
		fields: [
			 {name: 'descCuenta'},
			 {name: 'noCuenta'},
			 {name: 'idDivisa'},
			 {name: 'idBancoDep'},
			 {name: 'idChequeraDep'},
			 {name: 'descBanco'},
			 {name: 'idChequeraDos'},
			 {name: 'noLinea'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeContratosLiq, msg:"Cargando..."});
				
				mascara.hide();
				
				if(records.length === null || records.length <= 0)
				{
					BFwrk.Util.msgShow('No hay contratos', 'INFO');
					return;
				}
				else{
					Ext.getCmp(PF + 'txtNoContrato').setValue(records[0].get('noCuenta'));
					Ext.getCmp(PF + 'txtDescContrato').setValue(records[0].get('descCuenta'));
					Ext.getCmp(PF + 'txtIdDivisa').setValue(records[0].get('idDivisa'));
					Ext.getCmp(PF + 'txtImporte').setValue(BFwrk.Util.formatNumber(NS.uImporte));
					Ext.getCmp(PF + 'txtImporteCargo').setValue(BFwrk.Util.formatNumber(NS.uImporte));
					if(records[0].get('idChequeraDos') !== '')
						Ext.getCmp(PF + 'chkCuentaPropia').setValue(true);
					NS.sIdDivisa = records[0].get('idDivisa');
				}
			}
		}
	});
	
    NS.obtenerContratosLiq = function(){
    	//Llamada para obtener un valor de configuraSet
		GlobalAction.obtenerValorConfiguraSet(parseInt(268), function(response, e){
			if(response !== null && response !== undefined && response !== 'SI')
			{
				//Se setea cero para que el sistema tome la que esta asigada al usuario
				//para que posteriormente si se realiza la funcionalidad de empresas internas sea seteada aqui la empresa
				NS.storeContratosLiq.baseParams.iIdEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
				NS.storeContratosLiq.baseParams.iNumCta = NS.iNumCta;
				NS.storeContratosLiq.load();
			}
			else
			{
				/*
				@cuando se requiera se cambiaran lo parametros de la empresa con el frm adicional
				@de frmMonitorInversion
				If pbInversionInterna Then 
		            is_IdEmpresa = frmMonitorInversion.msfDatos.TextMatrix(frmMonitorInversion.msfDatos.row, COL_EMPRESA)
		        Else
		            is_IdEmpresa = CStr(GI_ID_EMPRESA)
		            txtEmpresa.Text = GS_DESC_EMPRESA
		        End If
		        If pbInversionInterna Then
		            tNumcta = frmMonitorInversion.msfDatos.TextMatrix(frmMonitorInversion.msfDatos.row, COL_NO_CUENTA)
		            txtImporte.MaxLength = 20
		            txtImporte.Text = frmMonitorInversion.msfDatos.TextMatrix(frmMonitorInversion.msfDatos.row, COL_IMPORTE_REAL)
		        Else
		            tNumcta = msfOrdenesInv.TextMatrix(1, 9)
		            txtImporte.MaxLength = 20
		            txtImporte.Text = msfOrdenesInv.TextMatrix(1, 3)
		        End If*/
			}
		});
    };
    
    //combo cmbBancoLiquida
	NS.cmbBancoLiquida = new Ext.form.ComboBox({
		store: NS.storeContratosLiq,
		name: PF + 'cmbBancoLiquida',
		id: PF + 'cmbBancoLiquida',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 60,
        y: 70,
        width: 150,
		valueField:'idBancoDep',
		displayField:'descBanco',
		autocomplete: true,
		emptyText: 'Banco Liquidación',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtIdBancoLiquida',NS.cmbBancoLiquida.getId());
				 	NS.accionarBancoLiquida(combo.getValue());
				}
			}
		}
	});
	
	//store del combo cmbChequeraLiquida
	NS.storeChequeraLiquida = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iBanco : 0,
			iCuenta : 0,
			bInterna : false
		},
		root: '',
		paramOrder:['iBanco', 'iCuenta','bInterna'],
		root: '',
		directFn: InversionesAction.obtenerChequerasLiq, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraLiquida, msg:"Cargando..."});
				
				mascara.hide();
				
				if(records.length === null || records.length <= 0)
				{
					BFwrk.Util.msgShow('No Existen chequeras para liquidar', 'INFO');
					return;
				}
			}
		}
	}); 
	
	//combo de las chequeras para liquidar
	NS.cmbChequerasLiquida = new Ext.form.ComboBox({
		store: NS.storeChequeraLiquida,
		name: PF + 'cmbChequerasLiquida',
		id: PF + 'cmbChequerasLiquida',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 220,
        y: 70,
        width: 150,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Chequera Liquidación',
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
	
	NS.accionarBancoLiquida = function(valueCombo){
    	NS.cmbChequerasLiquida.reset();
    	NS.storeChequeraLiquida.baseParams.iBanco = valueCombo;
    	NS.storeChequeraLiquida.baseParams.iCuenta = NS.iNumCta;
    	NS.storeChequeraLiquida.load();
    };
    
	//store del combo cmbBancosCargo
	NS.storeBancosCargo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iEmpresa : 0,
			sDivisa : 0
		},
		root: '',
		paramOrder:['iEmpresa', 'sDivisa'],
		root: '',
		directFn: InversionesAction.obtenerBancosCargo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
    			mascara.hide();
				
				if(records.length === null || records.length <= 0)
				{
					BFwrk.Util.msgShow('No Existen bancos cargo', 'INFO');
					return;
				}
			}
		}
	}); 
	
	//combo de los banco en cargo
	NS.cmbBancosCargo = new Ext.form.ComboBox({
		store: NS.storeBancosCargo,
		name: PF + 'cmbBancosCargo',
		id: PF + 'cmbBancosCargo',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 60,
        y: 20,
        width: 150,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Banco Cargo',
		triggerAction: 'all',
		value: '',
		disabled: true,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtIdBancoCargo',NS.cmbBancosCargo.getId());
				 	NS.accionarBancosCargo(combo.getValue());
				}
			}
		}
	});
	
	NS.accionarBancosCargo = function(valueCombo){
		NS.cmbChequeraCargo.reset();
		NS.storeChequeraCargo.baseParams.iBanco = valueCombo;
		NS.storeChequeraCargo.baseParams.sDivisa = Ext.getCmp(PF + 'txtIdDivisa').getValue();
		NS.storeChequeraCargo.baseParams.iEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
		NS.storeChequeraCargo.load();
	};
	
	//store del combo cmbChequeraCargo
	NS.storeChequeraCargo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iEmpresa : 0,
			iBanco   : 0,
			sDivisa  : 0
		},
		root: '',
		paramOrder:['iEmpresa', 'iBanco' ,'sDivisa'],
		root: '',
		directFn: InversionesAction.obtenerChequerasCargo, 
		idProperty: 'idChequera', 
		fields: [
			 {name: 'idChequera'},
			 {name: 'saldoFinal'},
			 {name: 'idDivisa'},
			 {name: 'descChequera'},
			 {name: 'idBanco'},
			 {name: 'tipoChequera'}
		],
		listeners: {
			load: function(s, records){
//				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraCargo, msg:"Cargando..."});
				
    			mascara.hide();
				
				var entro = false;
				
				if(records.length === null || records.length <= 0) {
					BFwrk.Util.msgShow('No Existen chequeras para cargo', 'INFO');
					return;
				}
				for(var i=0; i<records.length; i++) {
					if(records[i].get('tipoChequera') == 'C') {
						Ext.getCmp(PF + 'txtIdBancoCargo').setValue(records[i].get('idBanco'));
						Ext.getCmp(PF + 'cmbBancosCargo').setValue(NS.storeBancosCargo.getById(parseInt(records[i].get('idBanco'))).get('descripcion'));
						Ext.getCmp(PF + 'cmbChequeraCargo').setValue(records[i].get('idChequera'));
						entro = true;
						break;
					}
				}
				if(!entro) {
					Ext.getCmp(PF + 'txtIdBancoCargo').setValue(records[0].get('idBanco'));
					Ext.getCmp(PF + 'cmbBancosCargo').setValue(NS.storeBancosCargo.getById(parseInt(records[0].get('idBanco'))).get('descripcion'));
					Ext.getCmp(PF + 'cmbChequeraCargo').setValue(records[0].get('idChequera'));
				}
				//Combo de banco y chequera de Liquidación
				Ext.getCmp(PF + 'txtIdBancoLiquida').setValue(Ext.getCmp(PF + 'txtIdBancoCargo').getValue());
				Ext.getCmp(PF + 'cmbBancoLiquida').setValue(NS.storeContratosLiq.getById(parseInt(Ext.getCmp(PF + 'txtIdBancoLiquida').getValue())).get('descBanco'));
				NS.accionarBancoLiquida(parseInt(Ext.getCmp(PF + 'txtIdBancoLiquida').getValue()));
				Ext.getCmp(PF + 'cmbChequerasLiquida').setValue(Ext.getCmp(PF + 'cmbChequeraCargo').getValue());
				
				//Combo de banco y chequera de Regreso
				Ext.getCmp(PF + 'txtIdBancoRegreso').setValue(Ext.getCmp(PF + 'txtIdBancoCargo').getValue());
				Ext.getCmp(PF + 'cmbBancoRegreso').setValue(NS.storeBancoRegreso.getById(parseInt(Ext.getCmp(PF + 'txtIdBancoRegreso').getValue())).get('descripcion'));
				NS.accionarBancoRegreso(parseInt(Ext.getCmp(PF + 'txtIdBancoRegreso').getValue()));
				Ext.getCmp(PF + 'cmbChequerasRegreso').setValue(Ext.getCmp(PF + 'cmbChequeraCargo').getValue());
				
				Ext.getCmp(PF + 'txtSaldoDisponible').setValue(BFwrk.Util.formatNumber(BFwrk.Util.scanFieldsStore(
					NS.storeChequeraCargo, 'idChequera',Ext.getCmp(PF + 'cmbChequeraCargo').getValue(),'saldoFinal')
				));
				//clave, desc, che, importe, indice
				NS.agregarDatosGrid(Ext.getCmp(PF + 'txtIdBancoCargo').getValue(), 
						NS.storeBancosCargo.getById(parseInt(Ext.getCmp(PF + 'txtIdBancoCargo').getValue())).get('descripcion'),
						Ext.getCmp(PF + 'cmbChequeraCargo').getValue(), Ext.getCmp(PF + 'txtImporteCargo').getValue(), NS.iIndice);
			}
		}
	}); 
	
	//combo de las chequeras para cargo
	NS.cmbChequeraCargo = new Ext.form.ComboBox({
		store: NS.storeChequeraCargo,
		name: PF + 'cmbChequeraCargo',
		id: PF + 'cmbChequeraCargo',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 220,
        y: 20,
        width: 150,
		valueField:'idChequera',
		displayField:'idChequera',
		autocomplete: true,
		emptyText: 'Chequera Cargo',
		triggerAction: 'all',
		value: '',
		disabled: true,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					Ext.getCmp(PF + 'txtSaldoDisponible').setValue(BFwrk.Util.formatNumber(BFwrk.Util.scanFieldsStore(
						NS.storeChequeraCargo, 'idChequera',Ext.getCmp(PF + 'cmbChequeraCargo').getValue(),'saldoFinal')
					));
					//clave, desc, che, importe, indice
					NS.agregarDatosGrid(Ext.getCmp(PF + 'txtIdBancoCargo').getValue(), 
							NS.storeBancosCargo.getById(parseInt(Ext.getCmp(PF + 'txtIdBancoCargo').getValue())).get('descripcion'),
							Ext.getCmp(PF + 'cmbChequeraCargo').getValue(), Ext.getCmp(PF + 'txtImporteCargo').getValue(), NS.iIndice);
				}
			}
		}
	});
	
	//Declaración de la estructura del grid para mostrar los bancos a los que se hará el cargo
	NS.storeGridBancosCargo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'numero',
		fields: [
			{name: 'numero'},
			{name: 'clave'},
			{name: 'descripcion'},
			{name: 'chequera'},
			{name: 'importe'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	});
	
	NS.agregarDatosGrid = function(clave, desc, che, importe, indice){
		var sumImporte = 0;
		var datosStoreGridBancosCargo = NS.storeGridBancosCargo.data.items;
		var datosGridBancosCargo = NS.gridBancosCargo.getStore().recordType;
		var datos = new datosGridBancosCargo({
			numero: indice,
			clave: clave,
			descripcion: desc,
			chequera: che,
			importe: importe
		});
		
		for(var i = 0; i < datosStoreGridBancosCargo.length ; i = i + 1)
		{
			if(NS.iIndice === datosStoreGridBancosCargo[i].get('numero')) 
				NS.storeGridBancosCargo.remove(datosStoreGridBancosCargo[indice]);
		}
		
		NS.gridBancosCargo.stopEditing();
		NS.storeGridBancosCargo.insert(indice, datos);
		NS.sm.selectRow(indice);
		NS.iIndice = indice;
		NS.gridBancosCargo.getView().refresh();
		
		//Deshabilitar botones al insertar un nuevo registro
		Ext.getCmp(PF + 'btnAceptarWin').setDisabled(false);
		Ext.getCmp(PF + 'btnEliminarWin').setDisabled(false);
						
		for(var c = 0; c < datosStoreGridBancosCargo.length ; c = c + 1)
			sumImporte = sumImporte + parseFloat(BFwrk.Util.unformatNumber(datosStoreGridBancosCargo[c].get('importe')));
		Ext.getCmp(PF + 'txtTotalCargos').setValue(sumImporte > 0 ? BFwrk.Util.formatNumber(sumImporte) : '0.0');
	};
	
	//Grid para mostrar los bancos y chequeras de cargo
	NS.gridBancosCargo = new Ext.grid.GridPanel({
	store : NS.storeGridBancosCargo,
	cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
			columns : [
					NS.sm,
					{
						header :'Número',
						width :50,
						dataIndex :'numero',
						hidden: true
					},{
						header :'Clave',
						width :50,
						dataIndex :'clave'
					},{
						header :'Descripción',
						width :100,
						dataIndex :'descripcion'
					},{
						header: 'Chequera',
						width : 100,
						dataIndex: 'chequera' 
					},{
						header: 'Importe',
						width: 100,
						dataIndex: 'importe',
						render: BFwrk.Util.rendererMoney
					}
				]
			}),
		sm: NS.sm,
        columnLines: true,
		width: 590,
       	height: 85,
		x :0,
		y :0,
		title :'',
		listeners:{
			dblclick:{
				fn:function(grid){
					
				}
			},
			click:{
				fn: function(e){
					var recordsGrid = NS.gridBancosCargo.getSelectionModel().getSelections();
					if(recordsGrid.length > 0)
					{
						NS.iIndice = recordsGrid[0].get('numero');
						Ext.getCmp(PF + 'btnAceptarWin').setDisabled(false);
						Ext.getCmp(PF + 'btnEliminarWin').setDisabled(false);
					}
					else
					{
						Ext.getCmp(PF + 'btnAceptarWin').setDisabled(true);
						Ext.getCmp(PF + 'btnEliminarWin').setDisabled(true);
					}	
				}
			}
		}
	});
	
	//Declaración de la funcion para imprimir el reporte
	NS.imprimirReporte = function(){
		var recordsGridOrdenes = NS.gridLiqOrdenesInversion.getSelectionModel().getSelections();
		var strParams = '';
	
		strParams = '?nomReporte=ReporteLiquidacionDeInversiones';
		strParams += '&'+'nomParam1=noDocto';
		strParams += '&'+'valParam1=' + NS.noOrdenes;
		strParams += '&'+'nomParam2=nomreUsuario';
		strParams += '&'+'valParam2=' + NS.nomreUsuario;
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		
		NS.noOrdenes = '';
		return;
	};
	//Termina declaración imprimir
	
	NS.funCrearNuevo = function() {
		//Ext.getCmp(PF + 'txtImporteCargo').getValue();
		
		NS.storeBancosCargo.baseParams.iEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
		NS.storeBancosCargo.baseParams.sDivisa = Ext.getCmp(PF + 'txtIdDivisa').getValue();
		NS.storeBancosCargo.load();
		NS.cmbBancosCargo.setDisabled(false);
		NS.cmbChequeraCargo.setDisabled(false);
		Ext.getCmp(PF + 'txtIdBancoCargo').setDisabled(false);
		NS.iIndice = NS.gridBancosCargo.getSelectionModel().getSelections().length;
		//agregar el indice para un nuevo registro
		NS.agregarDatosGrid('', '', '', Ext.getCmp(PF + 'txtImporteCargo').getValue(), NS.iIndice);
		NS.cmbBancosCargo.reset();
		NS.cmbChequeraCargo.reset();
		Ext.getCmp(PF + 'txtIdBancoCargo').reset();
		var empre = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
	};
	
	
	
/////////////////////////////////////////////////////////////////////////////////////	
    //Inicia ventana de liquidación de ordenes NS.winLiquidacionOrdenesInv
    NS.winLiquidacionOrdenesInv = new Ext.Window({
    	title: 'Confirmación de Inversiones',
		modal:true,
		shadow:true,
		width: 720,
	   	height: 600,
	   	layout: 'absolute',
	   	plain:true,
	    bodyStyle:'padding:10px;',
	    autoScroll: true,
	    id: PF + 'winConsultaPosInve',
	    name: PF + 'winConsultaPosInve',
	    closable: false,
	    items : [
	    	{
                xtype: 'fieldset',
                title: '',
                width: 660,
                height: 550,
                x: 20,
                y: 5,
                layout: 'absolute',
                id: 'framePrinLiquidacionOrden',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 10,
                        y: 5
                    },
                    {
                        xtype: 'textfield',
                        x: 60,
                        y: 0,
                        width: 270,
                        name: PF + 'txtNomEmpresaW',
                        id: PF + 'txtNomEmpresaW',
                        //value: apps.SET.NOM_EMPRESA,
                        readOnly: true
                        //hidden: true
                    },
                    {
                        xtype: 'label',
                        text: 'Contrato:',
                        x: 0,
                        y: 30
                    },
                    {
                        xtype: 'textfield',
                        x: 80,
                        y: 30,
                        width: 50,
                        name: PF + 'txtNoContrato',
                        id: PF +  'txtNoContrato',
                        readOnly: true
                    },
                    {
                        xtype: 'textfield',
                        x: 140,
                        y: 30,
                        width: 210,
                        name: PF + 'txtDescContrato',
                        id: PF + 'txtDescContrato',
                        readOnly: true
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Regreso de Inversión',
                        x: 0,
                        y: 235,
                        width: 400,
                        height: 80,
                        layout: 'absolute',
                        id: 'fsetRegresoInversion',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Banco:',
                                x: 0,
                                y: 0
                            },
                            {
                                xtype: 'numberfield',
                                x: 0,
                                y: 20,
                                width: 50,
                                name: PF + 'txtIdBancoRegreso',
                                id: PF + 'txtIdBancoRegreso',
                                listeners: {
                                	change:{
                                		fn: function(e){
                                			var idCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBancoRegreso',NS.cmbBancoRegreso.getId());
                                			if(idCombo !== null && idCombo !== '')
				 								NS.accionarBancoRegreso(idCombo);
                                		}
                                	}
                                }
                            },
                              NS.cmbBancoRegreso,
                            {
                                xtype: 'label',
                                text: 'Chequera:',
                                x: 220,
                                y: 0
                            },
                            NS.cmbChequerasRegreso
                        ]
                    },
                    {
                        xtype: 'label',
                        text: 'Divisa:',
                        x: 410,
                        y: 250
                    },
                    {
                        xtype: 'textfield',
                        x: 460,
                        y: 245,
                        width: 50,
                        name: PF + 'txtIdDivisa',
                        id: PF + 'txtIdDivisa',
                        readOnly: true
                    },
                    {
                        xtype: 'label',
                        text: 'Importe:',
                        x: 410,
                        y: 285
                    },
                    {
                        xtype: 'textfield',
                        x: 460,
                        y: 280,
                        width: 140,
                        name: PF + 'txtImporte',
                        id: PF + 'txtImporte',
                        readOnly: true
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha liq.:',
                        x: 410,
                        y: 310,
                        id: PF + 'lblFechaAlta',
                        name: PF + 'lblFechaAlta'
                    },
                    {
                        xtype: 'fieldset',
                        title: '',
                        x: 0,
                        y: 60,
                        height: 170,
                        layout: 'absolute',
                        id: 'fSetLiquidaEnBanco',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Liquida en Banco:',
                                x: 0,
                                y: 50
                            },
                            {
                                xtype: 'textfield',
                                x: 0,
                                y: 70,
                                width: 50,
                                name: PF + 'txtIdBancoLiquida',
                                id: PF + 'txtIdBancoLiquida',
                                listeners: {
                                	click:{
                                		fn: function(e){
                                			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBancoLiquida',NS.cmbBancoLiquida.getId());
                                			if(valueCombo !== null && valueCombo !== '')
				 								NS.accionarBancoLiquida(valueCombo);
                                		}
                                	}
                                }
                            },
                            	NS.cmbBancoLiquida,
                            {
                                xtype: 'label',
                                text: 'Chequera:',
                                x: 220,
                                y: 50
                            },
                            	NS.cmbChequerasLiquida,
                            {
                                xtype: 'checkbox',
                                x: 515,
                                y: 20,
                                boxLabel: 'Cuenta Propia',
                                id: PF + 'chkCuentaPropia',
                                name: PF + 'chkCuentaPropia',
                                disabled: true
                            },
                            {
                                xtype: 'label',
                                text: 'Cargo a Banco:',
                                x: 0,
                                y: 0
                            },
                            {
                                xtype: 'textfield',
                                x: 0,
                                y: 20,
                                width: 50,
                                name: PF + 'txtIdBancoCargo',
                                id: PF + 'txtIdBancoCargo',
                                disabled: true,
                                listeners: {
                                	change:{
                                		fn: function(caja, valor){
                                			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdBancoCargo',NS.cmbBancosCargo.getId());
                                			if(valueCombo !== null && valueCombo !== '')
                                				NS.accionarBancosCargo(valueCombo);
                                		}
                                	}
                                }
                            },
                                NS.cmbBancosCargo,
                            {
                                xtype: 'label',
                                text: 'Chequera:',
                                x: 220,
                                y: 0
                            },
                             NS.cmbChequeraCargo,
                            {
                                xtype: 'label',
                                text: 'Saldo Disponible:',
                                x: 385,
                                y: 50
                            },
                            {
                                xtype: 'textfield',
                                x: 385,
                                y: 70,
                                width: 120,
                                name: PF + 'txtSaldoDisponible',
                                id: PF + 'txtSaldoDisponible',
                                readOnly: true,
                                listeners: {
                                	change:{
                                		fn: function(caja, valor){
                                		
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'label',
                                text: 'Importe Cargo:',
                                x: 385,
                                y: 0
                            },
                            {
                                xtype: 'textfield',
                                x: 385,
                                y: 20,
                                width: 120,
                                name: PF + 'txtImporteCargo',
                                id: PF + 'txtImporteCargo',
                                readOnly: true,
                                listeners: {
                                	change:{
                                		fn: function(caja, valor){
                                			Ext.getCmp(PF + 'txtImporteCargo').setValue(BFwrk.Util.formatNumber(caja.getValue()));
                                			NS.agregarDatosGrid(Ext.getCmp(PF + 'cmbBancosCargo').getValue(), 
												BFwrk.Util.scanFieldsStore(NS.storeBancosCargo, 'id',Ext.getCmp(PF + 'cmbBancosCargo').getValue(),'descripcion'),
												Ext.getCmp(PF + 'cmbChequeraCargo').getValue(), Ext.getCmp(PF + 'txtImporteCargo').getValue(), NS.iIndice);
                                		}
                                	}
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Cargo a Bancos',
                        x: 0,
                        y: 320,
                        height: 150,
                        layout: 'absolute',
                        id: 'fSetCargoaBancos',
                        items: [
                        	NS.gridBancosCargo,
                            {
                                xtype: 'label',
                                text: 'Total cargos:',
                                x: 380,
                                y: 93
                            },
                            {
                                xtype: 'textfield',
                                x: 460,
                                y: 93,
                                name: PF + 'txtTotalCargos',
                                id: PF + 'txtTotalCargos',
                                readOnly: true
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Aceptar',
                        x: 220,
                        y: 480,
                        width: 80,
                        id: PF + 'btnAceptarWin',
                        name: PF + 'btnAceptarWin',
                        disabled: true,
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			var recStorGridBancC = NS.storeGridBancosCargo.data.items;
                        			var uImporteGrid = 0;
                        			var matDatos = new Array();
                        			
                        			if(NS.validarRegreso() === false)
                        				return;
                        			
                        			for(var i=0; i<recStorGridBancC.length; i++) {
                        				var arrayDatos = {};
                        				arrayDatos.importeGrid = BFwrk.Util.unformatNumber(recStorGridBancC[i].get('importe'));
                        				arrayDatos.clave = recStorGridBancC[i].get('clave');
                        				arrayDatos.chequera = recStorGridBancC[i].get('chequera');
                        				matDatos[i] = arrayDatos;
                        			}
                        			var jSonString = Ext.util.JSON.encode(matDatos);
                        			
                        			InversionesAction.validaDatosLiqInv(Ext.getCmp(PF + 'chkCuentaPropia').getValue(), jSonString, 
                        					NS.uImporte, Ext.getCmp(PF + 'txtIdBancoCargo').getValue(),
                        					Ext.getCmp(PF + 'cmbChequeraCargo').getValue(), function(response, e) {
                        				
                        				if(response != null && response != undefined && response != '') {
                        					BFwrk.Util.msgShow(response, 'INFO');
                        					return;
                        				}
                        			});
                        			/*
                        			if(Ext.getCmp(PF + 'chkCuentaPropia').getValue())
                        			{
                        				for(var i = 0; i < recordsStoreGridBancosCargo.length; i = i + 1)
                        				{
                        					uImporteGrid = parseFloat(uImporteGrid) + parseFloat(BFwrk.Util.unformatNumber(recordsStoreGridBancosCargo[i].get('importe')));
                        				    if(recordsStoreGridBancosCargo[i].get('clave') === '' 
                        					   || recordsStoreGridBancosCargo[i].get('chequera') === '')
                       					    {
                       					   		BFwrk.Util.msgShow('Datos incompletos, verifique por favor...','INFO');
                       					   		return;
                       					    }
                        				}
                        				
                        				if(NS.uImporte !== uImporteGrid)
                        				{
                        					BFwrk.Util.msgShow('Los importes a cargar son diferentes al importe de la inversión...','WARNING');
                       					   	return;
                        				}
                        			}
                        			else
                        			{
                        				if(Ext.getCmp(PF + 'cmbBancosCargo').getValue() === '')
                        				{
                        					BFwrk.Util.msgShow('Debe selecionar un banco de cargo...','WARNING');
                       					   	return;
                        				}
                        				if(Ext.getCmp(PF + 'cmbChequeraCargo').getValue() === '')
                        				{
                        					BFwrk.Util.msgShow('Debe selecionar una chequera de cargo...','WARNING');
                       					   	return;
                        				}
                        				
                        			}
                        			*/
                        			
                        			if(Ext.getCmp(PF + 'cmbChequerasLiquida').getValue() !== Ext.getCmp(PF + 'cmbChequerasRegreso').getValue()) {
								    	Ext.Msg.confirm('SET', 'La chequera de Regreso de Inversión es distinta a la de Liquidación, ' + 
								    		'Esto generará los traspasos necesarios para que el dinero ' + 
								    		'llegue a la cuenta seleccionada ¿Desea continuar?', function(btn){
								    		
								    		if(btn == 'yes') {
								    			chequerasDiferentesDos = true;
								    			NS.aplicaLiquidacionInv();
								    		}else
												return;
								    	});
								    }else {
								    	NS.aplicaLiquidacionInv();
								    }
                        			/*
                        			//Inician datos del grid principal
                        			var recordsGridLiqInv = NS.gridLiqOrdenesInversion.getSelectionModel().getSelections();
                        			var matrizLiqInv = new Array();
                        			var arrayLiqInv = {};
                        	
                        			arrayLiqInv.noOrden = recordsGridLiqInv[0].get('noOrden');
                        			arrayLiqInv.nombreCorto = recordsGridLiqInv[0].get('nombreCorto');
                        			arrayLiqInv.plazo = recordsGridLiqInv[0].get('plazo');
                        			arrayLiqInv.importe = recordsGridLiqInv[0].get('importe');
                        			arrayLiqInv.tasa = recordsGridLiqInv[0].get('tasa');
                        			arrayLiqInv.interes = recordsGridLiqInv[0].get('interes');
                        			arrayLiqInv.isr = recordsGridLiqInv[0].get('isr');
                        			arrayLiqInv.noContacto = recordsGridLiqInv[0].get('noContacto');
                        			arrayLiqInv.fecAlta = recordsGridLiqInv[0].get('fecAlta').substring(0,10);
                        			arrayLiqInv.fecVenc = recordsGridLiqInv[0].get('fecVenc');
                        			arrayLiqInv.descTipoValor = recordsGridLiqInv[0].get('descTipoValor');
                        			arrayLiqInv.idPapel = recordsGridLiqInv[0].get('idPapel');
                        			arrayLiqInv.noCuenta = recordsGridLiqInv[0].get('noCuenta');
                        			arrayLiqInv.noFolioDet = recordsGridLiqInv[0].get('noFolioDet');
                        			arrayLiqInv.idTipoOperacion = recordsGridLiqInv[0].get('idTipoOperacion');
                        			arrayLiqInv.idBanco = recordsGridLiqInv[0].get('idBanco');
                        			arrayLiqInv.idChequera = recordsGridLiqInv[0].get('idChequera');
                        			
                        			matrizLiqInv[0] = arrayLiqInv;
                        			var jsonOrdenesInversion = Ext.util.JSON.encode(matrizLiqInv);
                        			//Terminan datos del grid principal
                        			
                        			//Inician datos BancosCargo
                        			var recordsGridBancosCargo = NS.storeGridBancosCargo.data.items;
                        			var matrizBancosCargo = new Array();
                        			for(var c = 0; c < recordsGridBancosCargo.length; c = c + 1)
                        			{
                        				var arrayBancosCargo = {};
	                        				arrayBancosCargo.idBancoCargo = recordsGridBancosCargo[c].get('clave');
	                        				arrayBancosCargo.idChequeraCargo = recordsGridBancosCargo[c].get('chequera');
	                        				arrayBancosCargo.importe = recordsGridBancosCargo[c].get('importe');
                        				matrizBancosCargo[c] = arrayBancosCargo;
                        			}
                        			var jsonBancosCargo = Ext.util.JSON.encode(matrizBancosCargo);
                        			//Terminan datos GridBancos
                        			
                        			//Inician datos de la ventana
                        			var chequerasDiferentesUno = false;
                        			var chequerasDiferentesDos = false;
                        			var arrayDatosWin = {};
                        			var matrizDatosWin = new Array();
                        			
                        			if(Ext.getCmp(PF + 'cmbChequerasLiquida').getValue() !== Ext.getCmp(PF + 'cmbChequeraCargo').getValue())
                        				chequerasDiferentesUno = true;
                        			
                        			if(Ext.getCmp(PF + 'cmbChequerasLiquida').getValue() !== Ext.getCmp(PF + 'cmbChequerasRegreso').getValue())
								    {
								    	Ext.Msg.confirm('SET', 'La chequera de Regreso de Inversión es distinta a la de Liquidación, ' + 
								    		'Esto generará los traspasos necesarios para que el dinero ' + 
								    		'llegue a la cuenta seleccionada ¿Desea continuar?', , function(btn){
								    		
								    		if (btn === 'yes')
								    			chequerasDiferentesDos = true;
											else
												chequerasDiferentesDos = false;
								    	});
								    }
                        			
                        			arrayDatosWin.idEmpresa = apps.SET.NO_EMPRESA;
                        			arrayDatosWin.bInterna = NS.bInterna;//Este parametro se deja falso, ya que por el momento no se agrega funcionalidad de empresas internas
                        			arrayDatosWin.bCuentaPropia = Ext.getCmp(PF + 'chkCuentaPropia').getValue();
                        			arrayDatosWin.idBancoRegreso = Ext.getCmp(PF + 'cmbBancoRegreso').getValue();
                        			arrayDatosWin.idChequeraRegreso = Ext.getCmp(PF + 'cmbChequerasRegreso').getValue();
                        			arrayDatosWin.idBancoLiquida = Ext.getCmp(PF + 'cmbBancoLiquida').getValue();
                        			arrayDatosWin.idChequeraLiquida = Ext.getCmp(PF + 'cmbChequerasLiquida').getValue();
                        			arrayDatosWin.idDivisa = Ext.getCmp(PF + 'txtIdDivisa').getValue();
                        			arrayDatosWin.fechaAlta = NS.fecAlta.substring(0,10);
                        			arrayDatosWin.importe = NS.uImporte;
                        			arrayDatosWin.nomEmpresa = apps.SET.NOM_EMPRESA;
                        			arrayDatosWin.chequerasDiferentesUno = chequerasDiferentesUno;
                        			arrayDatosWin.chequerasDiferentesDos = chequerasDiferentesDos;
                        			matrizDatosWin[0] = arrayDatosWin;
                        			var jsonDatosWin = Ext.util.JSON.encode(matrizDatosWin);
                        			
                        			//Terminan datos de la ventana
                        			InversionesAction.liquidarInversiones(jsonOrdenesInversion, jsonBancosCargo, jsonDatosWin, function(response, e){
                        				if(response !== null && response !== undefined) {
                        					BFwrk.Util.msgShow(''+ response.msgUsuario, 'INFO');
                        					NS.imprimirReporte();
                        				}else {
											BFwrk.Util.msgShow('Ocurrio un error en el proceso'+ response.msgUsuario, 'ERROR');                        				
                        				}
                        				NS.storeLiqOrdenesInversion.baseParam.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
        								NS.storeLiqOrdenesInversion.load();
                        				NS.limpiar();	
                        				NS.limpiarVentana();
                        				NS.winLiquidacionOrdenesInv.hide();
                        			}); */
                        			Ext.getCmp(PF + 'txtNomEmpresa').hidden(true);
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Crear Nuevo',
                        x: 320,
                        y: 480,
                        width: 80,
                        id: PF + 'btnNuevoWin',
                        name: PF + 'btnNuevoWin',
                        hidden: true,
                        listeners:{
                        	click:{
                        		fn: function(e){
                        			NS.funCrearNuevo();
               						
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Eliminar',
                        x: 420,
                        y: 480,
                        width: 80,
                        id: PF + 'btnEliminarWin',
                        name: PF + 'btnEliminarWin',
                        disabled: true,
                        listeners:{
                        	click:{
                        		fn: function(e){
                        			Ext.Msg.show({
									title: 'Información SET',
									msg: '¿Desea eliminar este importe...?',
										buttons: {
											yes: true,
											no: true,
											cancel: false
										},
										icon: Ext.MessageBox.WARNING,
										fn: function(btn) {
											if(btn === 'yes')
											{
												var datosStoreGridBancosCargo = NS.storeGridBancosCargo.data.items;
                        						NS.storeGridBancosCargo.remove(datosStoreGridBancosCargo[NS.iIndice]);
                        						NS.iIndice = datosStoreGridBancosCargo.length;
                        						NS.cmbBancosCargo.reset();
			                        			NS.cmbChequeraCargo.reset();
			                        			Ext.getCmp(PF + 'txtIdBancoCargo').reset();
                        						NS.cmbBancosCargo.setDisabled(true);
			                        			NS.cmbChequeraCargo.setDisabled(true);
			                        			Ext.getCmp(PF + 'txtIdBancoCargo').setDisabled(false);
			                        			
											}
											else if(btn === 'no')
											{	
												return;
											}
											else
											{
												return;
											}
										}
									});
                        		}
                        	}
                        }
                    },{
                        xtype: 'button',
                        text: 'Cancelar',
                        x: 520,
                        y: 480,
                        width: 80,
                        id: PF + 'btnCancelarWin',
                        name: PF + 'btnCancelarWin',
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			NS.limpiarVentana();
                        			NS.limpiar();
                        			NS.winLiquidacionOrdenesInv.hide();
                        		}
                        	}
                        }
                    }
                ]
            }
	    ]
    });
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    NS.aplicaLiquidacionInv = function() {
    	//Inician datos del grid principal
		var recordsGridLiqInv = NS.gridLiqOrdenesInversion.getSelectionModel().getSelections();
		var matrizLiqInv = new Array();
		var arrayLiqInv = {};

		arrayLiqInv.noOrden = recordsGridLiqInv[0].get('noOrden');
		arrayLiqInv.nombreCorto = recordsGridLiqInv[0].get('nombreCorto');
		arrayLiqInv.plazo = recordsGridLiqInv[0].get('plazo');
		arrayLiqInv.importe = recordsGridLiqInv[0].get('importe');
		arrayLiqInv.tasa = recordsGridLiqInv[0].get('tasa');
		arrayLiqInv.interes = recordsGridLiqInv[0].get('interes');
		arrayLiqInv.isr = recordsGridLiqInv[0].get('isr');
		arrayLiqInv.noContacto = recordsGridLiqInv[0].get('noContacto');
		arrayLiqInv.fecAlta = recordsGridLiqInv[0].get('sFecAlta').substring(0,10);
		arrayLiqInv.fecVenc = recordsGridLiqInv[0].get('sFecVenc').substring(0,10);
		arrayLiqInv.descTipoValor = recordsGridLiqInv[0].get('descTipoValor');
		arrayLiqInv.idPapel = recordsGridLiqInv[0].get('idPapel');
		arrayLiqInv.noCuenta = recordsGridLiqInv[0].get('noCuenta');
		arrayLiqInv.noFolioDet = recordsGridLiqInv[0].get('noFolioDet');
		arrayLiqInv.idTipoOperacion = recordsGridLiqInv[0].get('idTipoOperacion');
		arrayLiqInv.idBanco = recordsGridLiqInv[0].get('idBanco');
		arrayLiqInv.idChequera = recordsGridLiqInv[0].get('idChequera');
		
		arrayLiqInv.idBancoLiquida = recordsGridLiqInv[0].get('idBanco');
		arrayLiqInv.idChequeraLiquida = recordsGridLiqInv[0].get('idChequera');

		matrizLiqInv[0] = arrayLiqInv;
		var jsonOrdenesInversion = Ext.util.JSON.encode(matrizLiqInv);
		//Terminan datos del grid principal
		
		//Inician datos BancosCargo
//		var recordsGridBancosCargo = NS.storeGridBancosCargo.data.items;
		var matrizBancosCargo = new Array();
//		for(var c = 0; c < recordsGridBancosCargo.length; c = c + 1)
//		{
			var arrayBancosCargo = {};
			arrayBancosCargo.idBancoCargo = recordsGridLiqInv[0].get('idBanco');
			arrayBancosCargo.idChequeraCargo = recordsGridLiqInv[0].get('idChequera');
			arrayBancosCargo.importe = recordsGridLiqInv[0].get('importe');
			matrizBancosCargo[0] = arrayBancosCargo;
//		}
//			alert("idBancoCargo ["+ arrayBancosCargo.idBancoCargo +"] ["+ recordsGridLiqInv[0].get('idBanco') +"]");
		var jsonBancosCargo = Ext.util.JSON.encode(matrizBancosCargo);
		//Terminan datos GridBancos
		
		//Inician datos de la ventana
		var chequerasDiferentesUno = false;
		var chequerasDiferentesDos = false;
		var arrayDatosWin = {};
		var matrizDatosWin = new Array();
		
		if(Ext.getCmp(PF + 'cmbChequerasLiquida').getValue() !== Ext.getCmp(PF + 'cmbChequeraCargo').getValue())
			chequerasDiferentesUno = true;
		/*
		if(Ext.getCmp(PF + 'cmbChequerasLiquida').getValue() !== Ext.getCmp(PF + 'cmbChequerasRegreso').getValue())
	    {
	    	Ext.Msg.confirm('SET', 'La chequera de Regreso de Inversión es distinta a la de Liquidación, ' + 
	    		'Esto generará los traspasos necesarios para que el dinero ' + 
	    		'llegue a la cuenta seleccionada ¿Desea continuar?', , function(btn){
	    		
	    		if (btn === 'yes')
	    			chequerasDiferentesDos = true;
				else
					chequerasDiferentesDos = false;
	    	});
	    }
		*/
		arrayDatosWin.idEmpresa = recordsGridLiqInv[0].get('noEmpresa');;
		arrayDatosWin.bInterna = NS.bInterna;//Este parametro se deja falso, ya que por el momento no se agrega funcionalidad de empresas internas
		arrayDatosWin.bCuentaPropia = false;
		arrayDatosWin.idBancoRegreso = recordsGridLiqInv[0].get('idBancoReg');
		arrayDatosWin.idChequeraRegreso = recordsGridLiqInv[0].get('idChequeraReg');
		arrayDatosWin.idBancoInv = recordsGridLiqInv[0].get('idBancoInst');
		arrayDatosWin.idChequeraInv = recordsGridLiqInv[0].get('idChequeraInst');
		arrayDatosWin.idDivisa = recordsGridLiqInv[0].get('idDivisa');
		arrayDatosWin.fechaAlta = recordsGridLiqInv[0].get('sFecAlta').substring(0,10);
		arrayDatosWin.importe =  recordsGridLiqInv[0].get('importe');
		arrayDatosWin.nomEmpresa = recordsGridLiqInv[0].get('nomEmpresa');
		arrayDatosWin.tipoPago = 5;
		arrayDatosWin.idBancoLiquida = recordsGridLiqInv[0].get('idBanco');
		arrayDatosWin.idChequeraLiquida = recordsGridLiqInv[0].get('idChequera');
//		arrayDatosWin.chequerasDiferentesUno = chequerasDiferentesUno;
//		arrayDatosWin.chequerasDiferentesDos = chequerasDiferentesDos;
		
//		if(Ext.getCmp(PF + 'cmbChequerasLiquida').getValue() == Ext.getCmp(PF + 'cmbChequeraCargo').getValue())
//		arrayDatosWin.tipoPago = 5;
		
		matrizDatosWin[0] = arrayDatosWin;
		var jsonDatosWin = Ext.util.JSON.encode(matrizDatosWin);
		
		//Terminan datos de la ventana
		
		mascara.show();
		InversionesAction.liquidarInversiones(jsonOrdenesInversion, jsonBancosCargo, jsonDatosWin, function(response, e){
			if(response !== null && response !== undefined) {
				mascara.hide();
				BFwrk.Util.msgShow(''+ response.msgUsuario, 'INFO');
				if(response.codError == '0')
					NS.imprimirReporte();
			}else {
				mascara.hide();
				BFwrk.Util.msgShow('Ocurrio un error en el proceso'+ response.msgUsuario, 'ERROR');                        				
			}
			mascara.show();
			NS.storeLiqOrdenesInversion.load();
		});
    }
    
    /* Liquida Múltiple (I) */
    
    NS.aplicaLiquidacionInvMultiple = function() {
    	//Inician datos del grid principal
		var recordsGridLiqInv = NS.gridLiqOrdenesInversion.getSelectionModel().getSelections();
		var matrizDatos = new Array();
		var arrayDatos = {};
		
		if (recordsGridLiqInv.length < 1){
			BFwrk.Util.msgShow('No ha seleccionado ninguna inversión', 'WARNING');
			return;
		}

		for(var i = 0; i < recordsGridLiqInv.length; i++){
			arrayDatos = {};
			
			NS.noOrdenes += recordsGridLiqInv[i].get('noOrden')
			
			if(i < recordsGridLiqInv.length - 1)
				NS.noOrdenes += ',';
			
			arrayDatos.noOrden = recordsGridLiqInv[i].get('noOrden');
			arrayDatos.nombreCorto = recordsGridLiqInv[i].get('nombreCorto');
			arrayDatos.plazo = recordsGridLiqInv[i].get('plazo');
			arrayDatos.importe = recordsGridLiqInv[i].get('importe');
			arrayDatos.tasa = recordsGridLiqInv[i].get('tasa');
			arrayDatos.interes = recordsGridLiqInv[i].get('interes');
			arrayDatos.isr = recordsGridLiqInv[i].get('isr');
			arrayDatos.noContacto = recordsGridLiqInv[i].get('noContacto');
			arrayDatos.fecAlta = recordsGridLiqInv[i].get('sFecAlta').substring(0,10);
			arrayDatos.fecVenc = recordsGridLiqInv[i].get('sFecVenc').substring(0,10);
			arrayDatos.descTipoValor = recordsGridLiqInv[i].get('descTipoValor');
			arrayDatos.idPapel = recordsGridLiqInv[i].get('idPapel');
			arrayDatos.noCuenta = recordsGridLiqInv[i].get('noCuenta');
			arrayDatos.noFolioDet = recordsGridLiqInv[i].get('noFolioDet');
			arrayDatos.idTipoOperacion = recordsGridLiqInv[i].get('idTipoOperacion');
			arrayDatos.idBanco = recordsGridLiqInv[i].get('idBanco');
			arrayDatos.idChequera = recordsGridLiqInv[i].get('idChequera');
			
			arrayDatos.idBancoLiquida = recordsGridLiqInv[i].get('idBanco');
			arrayDatos.idChequeraLiquida = recordsGridLiqInv[i].get('idChequera');
	
			var matrizBancosCargo = new Array();
			var arrayBancosCargo = {};
			arrayDatos.idBancoCargo = recordsGridLiqInv[i].get('idBanco');
			arrayDatos.idChequeraCargo = recordsGridLiqInv[i].get('idChequera');
			arrayDatos.importe = recordsGridLiqInv[i].get('importe');
	
			var chequerasDiferentesUno = false;
			var chequerasDiferentesDos = false;
			
			if(Ext.getCmp(PF + 'cmbChequerasLiquida').getValue() !== Ext.getCmp(PF + 'cmbChequeraCargo').getValue())
				chequerasDiferentesUno = true;
	
			arrayDatos.idEmpresa = recordsGridLiqInv[i].get('noEmpresa');;
			arrayDatos.bInterna = NS.bInterna;//Este parametro se deja falso, ya que por el momento no se agrega funcionalidad de empresas internas
			arrayDatos.bCuentaPropia = false;
			arrayDatos.idBancoRegreso = recordsGridLiqInv[i].get('idBancoReg');
			arrayDatos.idChequeraRegreso = recordsGridLiqInv[i].get('idChequeraReg');
			arrayDatos.idBancoInv = recordsGridLiqInv[i].get('idBancoInst');
			arrayDatos.idChequeraInv = recordsGridLiqInv[i].get('idChequeraInst');
			arrayDatos.idDivisa = recordsGridLiqInv[i].get('idDivisa');
			arrayDatos.fechaAlta = recordsGridLiqInv[i].get('sFecAlta').substring(0,10);
			arrayDatos.importe =  recordsGridLiqInv[i].get('importe');
			arrayDatos.nomEmpresa = recordsGridLiqInv[i].get('nomEmpresa');
			arrayDatos.tipoPago = 5;
			arrayDatos.idBancoLiquida = recordsGridLiqInv[i].get('idBanco');
			arrayDatos.idChequeraLiquida = recordsGridLiqInv[i].get('idChequera');
	
			matrizDatos[i] = arrayDatos;
		}
		
		var jsonOrdenesInversion = Ext.util.JSON.encode(matrizDatos);
		//Terminan datos de la ventana
		mascara.show();
		InversionesAction.liquidarInversionesMultiple(jsonOrdenesInversion, function(response, e){
			if(response !== null && response !== undefined) {
				mascara.hide();
				BFwrk.Util.msgShow(''+ response.msgUsuario, 'INFO');
 //				if(response.codError == '0')
//					NS.imprimirReporte();
			}else {
				mascara.hide();
				BFwrk.Util.msgShow('Ocurrio un error en el proceso'+ response.msgUsuario, 'ERROR');                        				
			}
			mascara.show();
			NS.storeLiqOrdenesInversion.load();
		});
    }

    
    /* Liquida Múltiple (T) */
    
    
    
    NS.imprimirReporteFinal = function(){

		var nomReporte = "ReporteLiquidacionInv";
		var tipoInversion = "N";
		if (NS.txtNoEmpresa.getValue() === '' && Ext.getCmp(PF+'chkTodas').getValue() == false){
			BFwrk.Util.msgShow('Debe selecionar una empresa','WARNING');
			return;
		}

		strParams = '?nomReporte=' + nomReporte;
		strParams += '&'+'noEmpresa='+NS.txtNoEmpresa.getValue();
		strParams += '&'+'idUsuario='+NS.idUsuario;
		strParams += '&'+'usuario='+NS.nomreUsuario;
		strParams += '&'+'EMPRESA='+NS.cmbEmpresa.getRawValue();
		strParams += '&'+'tipoInversion='+tipoInversion;
		window.open("/SET/jsp/bfrmwrk/gnrators/RepInversiones.jsp"+strParams);
	}
	
	//Funcion para realizar validaciones de banco de regreso
	NS.validarRegreso = function(){
		var valido = true;
		if(Ext.getCmp(PF + 'txtIdBancoRegreso').getValue() === '')
		{
			 if(confirm('No ha seleccionado un banco para el regreso, ¿Esta seguro de Aplicar movimiento?'))
			 	valido = true;
			 else
			 	valido = false;
					
			
			/*Ext.Msg.show({
			title: 'Información SET',
			msg: 'No ha seleccionado un banco para el regreso, ¿Esta seguro de Aplicar movimiento?',
				buttons: {
					yes: true,
					no: true,
					cancel: false
				},
				icon: Ext.MessageBox.WARNING,
				fn: function(btn) {
					if(btn === 'yes')
                     	valido = true;
					else if(btn === 'no')
						valido = false;
					else
						valido = false;
				}
			});*/
		}
		
		if(Ext.getCmp(PF + 'txtIdBancoRegreso').getValue() !== '' && Ext.getCmp(PF + 'cmbChequerasRegreso').getValue() === '')
		{
	    	BFwrk.Util.msgShow('Debe selecionar una chequera de Regreso...','WARNING');
	    	valido = false;
	    	return valido;	
	    }
	    	
	    if(Ext.getCmp(PF + 'cmbChequeraCargo').getValue() === Ext.getCmp(PF + 'cmbChequerasLiquida').getValue())
		{
			if(confirm('¿está generando un movimiento en su misma cuenta.. desea continuar?'))
				valido = true;
			else
				valido = false;
			/*Ext.Msg.show({
			title: 'Información SET',
			msg: '¿está generando un movimiento en su misma cuenta.. desea continuar?',
				buttons: {
					yes: true,
					no: true,
					cancel: false
				},
				icon: Ext.MessageBox.QUESTION,
				fn: function(btn) {
					if(btn === 'yes')
                     	valido = true;
					else if(btn === 'no')
						valido = false;
					else
						valido = false;
				}
			});*/
		}
	    return valido;
	};
	
	//Inicia formulario principal LiquidacionDeInversiones.js
	NS.LiquidacionOrdenesInversion = new Ext.form.FormPanel({
	    title: 'Confirmación de Inversiones',
	    width: 859,
	    height: 547,
	    padding: 10,
	    layout: 'absolute',
	    id: PF + 'LiquidacionDeInversiones',
	    name: PF + 'LiquidacionDeInversiones',
	    renderTo: NS.tabContId,
	    frame: true,
	    autoScroll: true,
	    items : [
            {
                xtype: 'fieldset',
                title: '',
                width: 1010,
                height: 490,
                x: 10,
                y: 10,
                layout: 'absolute',
                id: PF + 'framePrinLiquidacionOrdenes',
                name: PF + 'framePrinLiquidacionOrdenes',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa :',
                        x: 10,
                        y: 5
                    },
                    NS.txtNoEmpresa,
                    NS.cmbEmpresa,
                    {
                        xtype: 'checkbox',
                        id:PF+'chkTodas',
                        name:PF+'chkTodas',
                        x: 450,
                        y: 0,
                        boxLabel: 'Todas',
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        			if(valor==true){
                        				NS.todas=true;
                        				NS.cmbEmpresa.reset();
                        				NS.txtNoEmpresa.setValue('');
                       			}else
                       				NS.todas=true;
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'textfield',
                        x: 60,
                        y: 0,
                        width: 320,
                        name: PF +'txtNomEmpresa',
                        id: PF +'txtNomEmpresa',
                        value: apps.SET.NOM_EMPRESA,
                        hidden: true
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 880,
                        y: 0,
                        width: 100,
                        id: PF + 'btnBuscar',
                        name: PF + 'btnBuscar',
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			if(NS.todas == true || Ext.getCmp(PF + 'txtNoEmpresa').getValue() != ''){
	                        			NS.storeLiqOrdenesInversion.baseParams.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
	                        			mascara.show();
	                        			NS.storeLiqOrdenesInversion.load();
                        			}else{
    									Ext.Msg.alert('SET', 'Debe seleccionar una empresa');
                        			}
                        			
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Confirmación de inversiones',
                        x: 0,
                        y: 40,
                        height: 380,
                        width: 985,
                        id: PF +'frameLiquidacionInv',
                        name: PF +'frameLiquidacionInv',
                        items: [
                        	NS.gridLiqOrdenesInversion
                        ]
                    },
                    {
                        xtype: 'label',
                        text: 'Total MN:',
                        x: 10,
                        y: 430
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 445,
                        width: 230,
                        name: PF + 'txtTotal',
                        id: PF + 'txtTotal',
                        readOnly: true
                    },
                    {
                        xtype: 'label',
                        text: 'Total Dólares:',
                        x: 280,
                        y: 430
                    },
                    {
                        xtype: 'textfield',
                        x: 280,
                        y: 445,
                        width: 230,
                        name: PF + 'txtTotalDls',
                        id: PF + 'txtTotalDls',
                        readOnly: true
                    },
                    {
                        xtype: 'button',
                        text: 'Aceptar',
                        x: 880,
                        y: 445,
                        width: 80,
                        id: 'btnAceptar',
                        name: PF + 'btnAceptar',
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			NS.aplicaLiquidacionInvMultiple();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Eliminar',
                        x: 790,
                        y: 445,
                        width: 80,
                        id: PF + 'btnEliminar',
                        name: PF + 'btnEliminar',
                        hidden: false,
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			var regGridSelec = NS.gridLiqOrdenesInversion.getSelectionModel().getSelections();
                        			var iOrden = 0;
                        			
                        			if(regGridSelec.length <= 0)
                        			{
                        				BFwrk.Util.msgShow('Debe selecionar una orden para eliminar...','WARNING');
                        				return;
                        			}
                        			iOrden = regGridSelec[0].get('noOrden');
                        			Ext.Msg.show({
									title: 'Información SET',
									msg: '¿Desea cancelar esta Orden de Inversión? ',
										buttons: {
											yes: true,
											no: true,
											cancel: false
										},
										icon: Ext.MessageBox.QUESTION,
										fn: function(btn) {
											if(btn === 'yes')
											{
												InversionesAction.cancelarOrdenesInversion(parseInt(iOrden), function(response, e){
													if(response !== null && response !== undefined && response > 0)
														BFwrk.Util.msgShow('La orden de inversion ha sido cancelada','INFO');
													else
														BFwrk.Util.msgShow('Error al cancelar la orden de inversion','ERROR');	
													mascara.show();
				                        			NS.storeLiqOrdenesInversion.baseParams.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
													NS.storeLiqOrdenesInversion.load();
    												NS.gridLiqOrdenesInversion.getView().refresh();
												});
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
                    },
                    {
                        xtype: 'button',
                        text: 'Imprimir',
                        x: 700,
                        y: 445,
                        width: 80,
                        id: 'btnImprimir',
                        hidden:false,
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			NS.imprimirReporteFinal();
                        		}
                        	}
                        }
                    }
                ]
            }
        ]
	});
	NS.LiquidacionOrdenesInversion.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	//  staticCheck("#gridLiqOrdenesInversion div div[class='x-panel-ml']","#gridLiqOrdenesInversion div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
});