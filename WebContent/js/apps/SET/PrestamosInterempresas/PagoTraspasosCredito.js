Ext.onReady(function(){
	
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.PrestamosInterempresas.PagoDeTraspasosDeCredito');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	
	NS.resetComponentes = function()
	{
		//Ext.getCmp(PF + 'txtNoEmpresaD').reset();
		//Ext.getCmp(PF + 'cmbEmpresaD').reset();
		Ext.getCmp(PF + 'txtNoBancoD').reset();
		Ext.getCmp(PF + 'cmbBancoD').reset();
		Ext.getCmp(PF + 'cmbChequeraD').reset();
		Ext.getCmp(PF + 'txtSaldoDisponibleD').reset();
		Ext.getCmp(PF + 'txtNvoSaldoDispDe').reset();
		Ext.getCmp(PF + 'txtNoEmpresaA').reset();
		Ext.getCmp(PF + 'cmbEmpresaA').reset();
		Ext.getCmp(PF + 'txtNoBancoA').reset();
		Ext.getCmp(PF + 'cmbBancoA').reset();
		Ext.getCmp(PF + 'cmbChequeraA').reset();
		Ext.getCmp(PF + 'txtSaldoDisponibleA').reset();
		Ext.getCmp(PF + 'txtNvoSaldoDispA').reset();
		Ext.getCmp(PF + 'txtMonto').reset();
		Ext.getCmp(PF + 'txtConcepto').reset();
		Ext.getCmp(PF + 'txtSaldoCoinversion').reset();
		
		//Variables utilizadas para el saldo final
		NS.iIdBancoD = 0;
		//NS.iIdEmpresaD = 0;
		NS.sIdCheqD = '';
		NS.iIdBancoA = 0;
		NS.iIdEmpresaA = 0;
		NS.sIdCheqA = '';
	};
	
	NS.limpiar = function()
	{
		NS.PagoDeTraspasoDeCredito.getForm().reset();
		Ext.getCmp(PF + 'txtConcepto').setValue('TRASPASO PAGO CREDITO');
		Ext.getCmp(PF + 'txtNomEmpresa').setValue(apps.SET.NOM_EMPRESA);
		
		//Variables utilizadas para el saldo final
		NS.iIdBancoD = 0;
		NS.iIdEmpresaD = 0;
		NS.sIdCheqD = '';
		NS.iIdBancoA = 0;
		NS.iIdEmpresaA = 0;
		NS.sIdCheqA = '';
	};
	
	//store para el combo de empresas hijo 
	NS.storeEmpArbol = new Ext.data.DirectStore({
		paramsAsHash: false,
		directFn: PrestamosInterempresasAction.llenarCmbEmpresasArbol, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpArbol, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existe empresas hijo');
					return;
				}
			}
		}
	}); 
	
	NS.storeEmpArbol.load();
	NS.storeEmpArbol.sort('ASC');
	
	NS.accionarEmpresaD = function(valueCombo)
	{
		NS.storeBancosD.baseParams.iIdEmpresa = parseInt(valueCombo);
		NS.storeBancosD.load();
		NS.storeEmpresaDestino.load();
		NS.storeChequeraD.baseParams.iIdEmpresa = parseInt(valueCombo);
		
		NS.iIdEmpresaD = valueCombo;
		
		NS.resetComponentes();
	};
	
	NS.cmbEmpresaD = new Ext.form.ComboBox({
		store: NS.storeEmpArbol,
		name: PF + 'cmbEmpresaD',
		id: PF + 'cmbEmpresaD',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 130,
        y: 10,
        width: 220,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor) 
				{
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresaD',NS.cmbEmpresaD.getId());
				 	NS.accionarEmpresaD(combo.getValue());
				}
			}
		}
	});
		
	
    //store para el combo de bancosD
	NS.storeBancosD = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdEmpresa : 0,
			iIdBancoD :0,
			sIdCheD : ''
		},
		root: '',
		paramOrder:['iIdEmpresa','iIdBancoD','sIdCheD'],
		directFn: PrestamosInterempresasAction.llenarCmbBancosEmp, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancosD, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen bancos asignados a esta empresa');
					return;
				}
			}
		}
	}); 
	
	NS.accionarBancoD = function(valueCombo)
	{
		NS.storeChequeraD.baseParams.iIdBancoD = parseInt(valueCombo);
		NS.storeChequeraD.load();
		NS.storeBancosA.baseParams.iIdBancoD = parseInt(valueCombo);
		NS.storeChequeraA.baseParams.iIdBancoD = parseInt(valueCombo);
		
		NS.iIdBancoD = valueCombo;
		
		Ext.getCmp(PF + 'cmbChequeraD').reset();
		Ext.getCmp(PF + 'txtSaldoDisponibleD').reset();
		Ext.getCmp(PF + 'txtNvoSaldoDispDe').reset();
	 
		NS.sIdCheqD = '';
	};
	
	NS.cmbBancoD = new Ext.form.ComboBox({
		store: NS.storeBancosD,
		name: PF + 'cmbBancoD',
		id: PF + 'cmbBancoD',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 130,
        y: 50,
        width: 170,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un banco',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor) 
				{
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNoBancoD',NS.cmbBancoD.getId());
				 	NS.accionarBancoD(combo.getValue());
				}
			}
		}
	});
	
    //store para el combo de Empresa Destino
	NS.storeEmpresaDestino = new Ext.data.DirectStore({
		paramsAsHash: false,
		directFn: PrestamosInterempresasAction.llenarCmbEmpCon, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresaDestino, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen empresas');
					return;
				}
			}
		}
	}); 
	
	NS.accionarEmpresaA = function(valueCombo)
	{
		NS.storeBancosA.baseParams.iIdEmpresa = parseInt(valueCombo);
		NS.storeBancosA.load();
		NS.storeChequeraA.baseParams.iIdEmpresa = parseInt(valueCombo);
		
		NS.iIdEmpresaA = valueCombo;
		
		Ext.getCmp(PF + 'txtNoBancoA').reset();
		Ext.getCmp(PF + 'cmbBancoA').reset();
		Ext.getCmp(PF + 'cmbChequeraA').reset();
		Ext.getCmp(PF + 'txtSaldoDisponibleA').reset();
		Ext.getCmp(PF + 'txtNvoSaldoDispA').reset();
		Ext.getCmp(PF + 'txtSaldoCoinversion').reset();
		
		NS.iIdBancoA = 0;
		NS.sIdCheqA = '';
	};
	
	NS.cmbEmpresaA = new Ext.form.ComboBox({
		store: NS.storeEmpresaDestino,
		name: PF + 'cmbEmpresaA',
		id: PF + 'cmbEmpresaA',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 130,
        y: 10,
        width: 220,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un empresa',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresaA',NS.cmbEmpresaA.getId());
				 	NS.accionarEmpresaA(combo.getValue());
				}
			}
		}
	});
	
	//store para el combo chequera origen
	NS.storeChequeraD = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdEmpresa : 0,
			iIdBancoD : 0,
			sIdCheqD : '',
			iIdBancoA: 0
		},
		root: '',
		paramOrder:['iIdEmpresa','iIdBancoD','sIdCheqD','iIdBancoA'],
		directFn: PrestamosInterempresasAction.llenarCmbCheqOrigenDes, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraD, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen chequeras para este banco');
					return;
				}
			}
		}
	}); 
	
	NS.accionarChequeraD = function(valueCombo)
	{
		NS.storeBancosA.baseParams.sIdCheD = valueCombo;
		NS.storeChequeraA.baseParams.sIdCheqD = valueCombo;
		
		//obtener saldo final
		PrestamosInterempresasAction.obtenerSaldoFinal(parseInt(NS.iIdEmpresaD), parseInt(NS.iIdBancoD), valueCombo, function(response, e)
		{
			if(response !== null)
			{
				Ext.getCmp(PF + 'txtSaldoDisponibleD').setValue(BFwrk.Util.formatNumber(response));
				Ext.getCmp(PF + 'txtNvoSaldoDispDe').setValue(BFwrk.Util.formatNumber(response 
																- parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtMonto').getValue()))));
			}
		});
		
		NS.sIdCheqD = valueCombo;
	};
	
	NS.cmbChequeraD = new Ext.form.ComboBox({
		store: NS.storeChequeraD,
		name: PF + 'cmbChequeraD',
		id: PF + 'cmbChequeraD',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 60,
        y: 90,
        width: 180,
		valueField:'descripcion',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una chequera',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	NS.accionarChequeraD(combo.getValue());
				}
			}
		}
	});
	
	//Objetos para datos destino
	
	//store para el combo de bancosA
	NS.storeBancosA = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdEmpresa :0,
			iIdBancoD :0,
			sIdCheD : ''
		},
		root: '',
		paramOrder:['iIdEmpresa','iIdBancoD','sIdCheD'],
		directFn: PrestamosInterempresasAction.llenarCmbBancosEmp, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancosA, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen bancos asignados a esta empresa');
					return;
				}
			}
		}
	}); 
	
	NS.accionarBancoA = function(valueCombo)
	{
		NS.storeChequeraA.baseParams.iIdBancoA = parseInt(valueCombo);
		NS.storeChequeraA.load();
		
		NS.iIdBancoA = valueCombo;
		
		Ext.getCmp(PF + 'cmbChequeraA').reset();
		Ext.getCmp(PF + 'txtSaldoDisponibleA').reset();
		Ext.getCmp(PF + 'txtNvoSaldoDispA').reset();
		Ext.getCmp(PF + 'txtSaldoCoinversion').reset();
		
		NS.sIdCheqA = '';
	};
	
	NS.cmbBancoA = new Ext.form.ComboBox({
		store: NS.storeBancosA,
		name: PF + 'cmbBancoA',
		id: PF + 'cmbBancoA',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 130,
        y: 50,
        width: 170,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un banco',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor) 
				{
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNoBancoA',NS.cmbBancoA.getId());
				 	NS.accionarBancoA(combo.getValue());
				}
			}
		}
	});
	
	//store para el combo chequera destino
	NS.storeChequeraA = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iIdEmpresa : 0,
			iIdBancoD : 0,
			sIdCheqD : '',
			iIdBancoA: 0
		},
		root: '',
		paramOrder:['iIdEmpresa','iIdBancoD','sIdCheqD','iIdBancoA'],
		directFn: PrestamosInterempresasAction.llenarCmbCheqOrigenDes, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraA, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen chequeras para este banco');
					return;
				}
			}
		}
	}); 
	
	NS.accionarChequeraA = function(valueCombo)
	{
		PrestamosInterempresasAction.obtenerSaldoFinal(parseInt(NS.iIdEmpresaA), parseInt(NS.iIdBancoA), valueCombo, function(response, e)
		{
			if(response !== null)
			{
				Ext.getCmp(PF + 'txtSaldoDisponibleA').setValue(BFwrk.Util.formatNumber(response));
				Ext.getCmp(PF + 'txtNvoSaldoDispA').setValue(BFwrk.Util.formatNumber(response 
																+ parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtMonto').getValue()))));
			
				//Llamada para obtener saldo de coinversion
				PrestamosInterempresasAction.obtenerSaldoFinalYCoinversion(parseInt(NS.iIdEmpresaD), parseInt(NS.iIdEmpresaA), 
																			parseInt(NS.iIdBancoA),valueCombo, function(response, e)
				{
					if(response !== null)
					{
						Ext.getCmp(PF + 'txtSaldoCoinversion').setValue(BFwrk.Util.formatNumber(response.saldoCoinversion));
					}
				});
			}
		});
		NS.sIdCheqA = valueCombo;
	};
	
	NS.cmbChequeraA = new Ext.form.ComboBox({
		store: NS.storeChequeraA,
		name: PF + 'cmbChequeraA',
		id: PF + 'cmbChequeraA',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 60,
        y: 90,
        width: 180,
		valueField:'descripcion',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una chequera',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	NS.accionarChequeraA(combo.getValue());
				}
			}
		}
	});
	
	NS.validarDatos = function()
	{
		var validaDatos = true;
		var uMonto = 0;
		var uSaldoFinD = 0;
		uMonto = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtMonto').getValue());
		uSaldoFinD = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtSaldoDisponibleD').getValue());
		
		if(NS.iIdEmpresaD <= 0 || NS.iIdEmpresaA <= 0)
		{
			BFwrk.Util.msgShow('Debe seleccionar una empresa','INFO');
			validaDatos = false;
			
		}
		else if(NS.iIdBancoD <= 0)
		{
			BFwrk.Util.msgShow('Debe seleccionar un banco origen','INFO');
			validaDatos = false;
			return validaDatos;
		}
		else if(NS.iIdBancoA <= 0)
		{
			BFwrk.Util.msgShow('Debe seleccionar un banco destino','INFO');
			validaDatos = false;
			return validaDatos;
		}
		else if(NS.sIdCheqD == '')
		{
			BFwrk.Util.msgShow('Debe seleccionar chequera origen','INFO');
			validaDatos = false;
			return validaDatos;
		}
		else if(NS.sIdCheqA == '')
		{
			BFwrk.Util.msgShow('Debe seleccionar chequera destino','INFO');
			validaDatos = false;
			return validaDatos;
		}
		else if(uMonto <= 0)
		{
			BFwrk.Util.msgShow('Escriba el monto a traspasar','INFO');
			validaDatos = false;
			return validaDatos;
		}
		else if((NS.iIdEmpresaD == NS.iIdEmpresaA) && (NS.iIdBancoD == NS.iIdBancoA) && (NS.sIdCheqD == NS.sIdCheqA))
		{
			BFwrk.Util.msgShow('No puede transferir a la misma chequera','INFO');
			validaDatos = false;
			return validaDatos;
		}
		else if(uSaldoFinD < 0)
		{
			BFwrk.Util.msgShow('No tiene saldo suficiente en la chequera','INFO');
			validaDatos = false;
			return validaDatos;
		}
		else if(Ext.getCmp(PF + 'txtConcepto').getValue() == '')
		{
			BFwrk.Util.msgShow('Debe agregar un concepto','INFO');
			validaDatos = false;
			return validaDatos;
		}
		return validaDatos;
	};
	
	NS.realizarPagoCredito = function(iEmpOrigen, iEmpDestino, iBanOrigen, iBanDestino, 
								 sCheqOrigen, sCheqDestino, uMonto, sConcepto)
	{
		PrestamosInterempresasAction.realizarPagoTraspCred(parseInt(iEmpOrigen), parseInt(iEmpDestino), parseInt(iBanOrigen), 
														  parseInt(iBanDestino),sCheqOrigen, sCheqDestino, 
														  parseFloat(uMonto), sConcepto,function(response, e)
		{
			if(response !== null)
			{
				BFwrk.Util.msgShow('' + response.msgUsuario, 'INFO');
				NS.limpiar();
			}
		});
	};
	
	NS.PagoDeTraspasoDeCredito = new Ext.form.FormPanel({
    title: 'Traspaso de pago de credito',
    width: 822,
    height: 468,
    padding: 10,
    layout: 'absolute',
    renderTo: NS.tabContId,
    autoScroll: true,
    frame: true,
    id: PF + 'PagoDeTraspasoDeCredito',
        items : [
            {
                xtype: 'fieldset',
                title: '',
                width: 800,
                height: 420,
                x: 10,
                y: 10,
                layout: 'absolute',
                id: 'fSetPrincipal',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa',
                        x: 0,
                        y: 0
                    },
                    {
                        xtype: 'textfield',
                        x: 60,
                        y: 0,
                        width: 320,
                        name: PF + 'txtNomEmpresa',
                        id: PF + 'txtNomEmpresa'
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Traspaso de:',
                        x: 0,
                        y: 30,
                        width: 380,
                        height: 270,
                        layout: 'absolute',
                        id: PF + 'fSetTrasD',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa',
                                x: 0,
                                y: 10
                            },
                            {
                                xtype: 'textfield',
                                x: 60,
                                y: 10,
                                width: 60,
                                name: PF + 'txtNoEmpresaD',
                                id: PF + 'txtNoEmpresaD',
                                listeners: 
                                {
                                	change:
                                	{
                                		fn: function(box, value)
                                		{
                                			var valueCombo = '';
                                 			if(box.getValue() !== '')
                                				valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresaD',NS.cmbEmpresaD.getId());
                                			if(valueCombo !== '')
                                				NS.accionarEmpresaD(valueCombo);
                                		}
                                	}
                                }
                            },
                                NS.cmbEmpresaD,
                            {
                                xtype: 'label',
                                text: 'Banco',
                                x: 0,
                                y: 50
                            },
                            {
                                xtype: 'textfield',
                                x: 60,
                                y: 50,
                                width: 60,
                                name: PF + 'txtNoBancoD',
                                id: PF + 'txtNoBancoD',
                                listeners: 
                                {
                                	change:
                                	{
                                		fn: function(box, value)
                                		{
                                			var valueCombo = '';
                                 			if(box.getValue() !== '')
                                				valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoBancoD',NS.cmbBancoD.getId());
                                			if(valueCombo !== '')
                                				NS.accionarBancoD(valueCombo);
                                		}
                                	}
                                }
                            },
                                NS.cmbBancoD,
                            {
                                xtype: 'label',
                                text: 'Chequera',
                                x: 0,
                                y: 90,
                                width: 50
                            },
                                NS.cmbChequeraD,
                            {
                                xtype: 'label',
                                text: 'Saldo disponible',
                                x: 0,
                                y: 130
                            },
                            {
                                xtype: 'textfield',
                                x: 110,
                                y: 130,
                                readOnly: true,
                                name: PF + 'txtSaldoDisponibleD',
                                id: PF + 'txtSaldoDisponibleD'
                            },
                            {
                                xtype: 'label',
                                text: 'Nuevo Saldo Disp.',
                                x: 0,
                                y: 170
                            },
                            {
                                xtype: 'textfield',
                                x: 110,
                                y: 170,
                                readOnly: true,
                                name: PF + 'txtNvoSaldoDispDe',
                                id: PF + 'txtNvoSaldoDispDe'
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Traspaso a:',
                        x: 390,
                        y: 30,
                        width: 380,
                        height: 270,
                        layout: 'absolute',
                        id: 'fSetTrasA',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa',
                                x: 0,
                                y: 10
                            },
                            {
                                xtype: 'textfield',
                                x: 60,
                                y: 10,
                                width: 60,
                                name: PF + 'txtNoEmpresaA',
                                id: PF + 'txtNoEmpresaA',
                                listeners: 
                                {
                                	change:
                                	{
                                		fn: function(box, value)
                                		{
                                			var valueCombo = '';
                                 			if(box.getValue() !== '')
                                				valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresaA',NS.cmbEmpresaA.getId());
                                			if(valueCombo !== '')
                                				NS.accionarEmpresaA(valueCombo);
                                		}
                                	}
                                }
                            },
                                NS.cmbEmpresaA,
                            {
                                xtype: 'label',
                                text: 'Banco',
                                x: 0,
                                y: 50
                            },
                            {
                                xtype: 'textfield',
                                x: 60,
                                y: 50,
                                width: 60,
                                name: PF + 'txtNoBancoA',
                                id: PF + 'txtNoBancoA',
                                listeners: 
                                {
                                	change:
                                	{
                                		fn: function(box, value)
                                		{
                                			var valueCombo = '';
                                 			if(box.getValue() !== '')
                                				valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoBancoA',NS.cmbBancoA.getId());
                                			if(valueCombo !== '')
                                				NS.accionarBancoA(valueCombo);
                                		}
                                	}
                                }
                            },
                                NS.cmbBancoA,
                            {
                                xtype: 'label',
                                text: 'Chequera',
                                x: 0,
                                y: 90
                            },
                            NS.cmbChequeraA,
                            {
                                xtype: 'label',
                                text: 'Saldo disponible',
                                x: 0,
                                y: 130
                            },
                            {
                                xtype: 'textfield',
                                x: 110,
                                y: 130,
                                readOnly: true,
                                name: PF + 'txtSaldoDisponibleA',
                                id: PF + 'txtSaldoDisponibleA'
                            },
                            {
                                xtype: 'label',
                                text: 'Nuevo Saldo Disp.',
                                x: 0,
                                y: 170
                            },
                            {
                                xtype: 'textfield',
                                x: 110,
                                y: 170,
                                readOnly: true,
                                name: PF + 'txtNvoSaldoDispA',
                                id: PF + 'txtNvoSaldoDispA'
                            },
                            {
                                xtype: 'label',
                                text: 'Saldo Coinversion',
                                x: 0,
                                y: 210
                            },
                            {
                                xtype: 'textfield',
                                x: 110,
                                y: 210,
                                readOnly: true,
                                id: PF + 'txtSaldoCoinversion',
                                name: PF + 'txtSaldoCoinversion'
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: '',
                        x: 0,
                        y: 310,
                        width: 770,
                        height: 50,
                        layout: 'absolute',
                        id: 'fSetInferior',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Monto',
                                x: 60,
                                y: 0
                            },
                            {
                                xtype: 'textfield',
                                x: 110,
                                y: 0,
                                name: PF + 'txtMonto',
                                id: PF + 'txtMonto',
                                value: '0.0',
                                listeners:
                                {
                                	change:
                                	{
                                		fn: function(box, value)
                                		{
                                			var fSaldoNvoDispD = 0;
                                			var fSaldoDispD = 0;
                                			var fSaldoNvoDispA = 0;
                                			var fSaldoDispA = 0;
                                			var fMonto = 0;
                                			
                                			fSaldoDispD = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtSaldoDisponibleD').getValue()));
                                			fSaldoNvoDispD = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtNvoSaldoDispDe').getValue()));
                                			fMonto = parseFloat(BFwrk.Util.unformatNumber(box.getValue()));
                                			
                                			this.setValue(BFwrk.Util.formatNumber(fMonto));
                                			if(fSaldoNvoDispD > 0)
                                			{
                                				Ext.getCmp(PF + 'txtNvoSaldoDispDe').setValue(BFwrk.Util.formatNumber(fSaldoDispD - fMonto));
                                			}
                                			
                                			fSaldoDispA = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtSaldoDisponibleA').getValue()));
                                			fSaldoNvoDispA = parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtNvoSaldoDispA').getValue()));
                                			
                                			if(fSaldoDispA > 0)
                                			{
                                				Ext.getCmp(PF + 'txtNvoSaldoDispA').setValue(BFwrk.Util.formatNumber(fSaldoDispA + fMonto));
                                			}
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'label',
                                text: 'Concepto',
                                x: 390,
                                y: 0
                            },
                            {
                                xtype: 'uppertextfield',
                                x: 460,
                                y: 0,
                                width: 260,
                                name: PF + 'txtConcepto',
                                id: PF + 'txtConcepto',
                                value: 'TRASPASO PAGO CREDITO'
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Ejecutar',
                        x: 550,
                        y: 370,
                        width: 90,
                        id: PF + 'cmbEjecutar',
                        listeners:
                        {
                        	click:
                        	{
                        		fn: function(e)
                        		{
                        			var txtMonto = '';
                        			var txtConcepto = '';
                        			
                        			txtMonto = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtMonto').getValue());
                        			txtConcepto = Ext.getCmp(PF + 'txtConcepto').getValue();
                        			
                        			NS.realizarPagoCredito(NS.iIdEmpresaD, NS.iIdEmpresaA, NS.iIdBancoD,
                        							  NS.iIdBancoA, NS.sIdCheqD, NS.sIdCheqA, txtMonto, txtConcepto);
                        			 
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 660,
                        y: 370,
                        width: 90,
                        id: 'cmbLimpiar',
                        listeners:
                        {
                        	click:
                        	{
                        		fn: function(e)
                        		{
                        			NS.limpiar();
                        		}
                        	}
                        }
                    }
                ]
            }
        ]
	});
	NS.PagoDeTraspasoDeCredito.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	NS.limpiar();
});