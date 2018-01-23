/*
@author: Jessica Arelly Cruz Cruz
@since: 29/08/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Coinversion.SolicitudFondeo');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	NS.GI_USUARIO = apps.SET.iUserId ;
	NS.GI_ID_CAJA = apps.SET.ID_CAJA;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	
	NS.nomEmpresaConcen = '';
	NS.coinversora = 0;
	NS.noEmpresa = 0;
	NS.divisa = '';
	NS.idBancoDe = 0;
	NS.idBancoA = 0;
	NS.idChequeraDe = '';
	NS.idChequeraA = '';
	NS.nomEmpresaDe='';
	NS.Monto = 0;
	NS.confSet = false;
	NS.salCoinv = 0;
	
	//store tipos_saldo
	
	NS.storeTipoSaldo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
		},
		root: '',
		paramOrder:[],
		directFn: CoinversionAction.llenarTipoSaldo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					BFwrk.Util.msgShow('No hay tipos de saldos disponibles', 'INFO');
				//	Ext.getCmp(PF+'txtBanco').setValue('');
					//Ext.getCmp(PF+'cmbBanco').setValue('');
					return;
				}
				
				//console.log("tipo de saldo "+records[0].get('descripcion'));
			}
		}
	}); 
	
	//NS.storeTipoSaldo.load();
	NS.cmbTipoSaldo = new Ext.form.ComboBox({
		store: NS.storeTipoSaldo
		,name: PF+'cmbTipoSaldo'
		,id: PF+'cmbTipoSaldo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 430
        , y: 45
        ,width: 212
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Tipo de Saldo'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					var valor=BFwrk.Util.updateComboToTextField( PF+'cmbTipoSaldo',NS.cmbTipoSaldo.getId());
					//NS.accionarCmbEmpresa(combo.getValue());
					NS.idTipoSaldo=combo.getValue();
					console.log("id tipo saldo "+combo.getValue());
				}
			}
		}
	});
	
	
	//store empresa
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{mantenimiento : false},
		paramOrder:['mantenimiento'],
		directFn: GlobalAction.llenarComboEmpresasConcentradoras,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//combo Empresas concentradoras
	NS.storeCmbEmpresa.load();
	NS.cmbEmpresaConsen = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa
		,name: PF+'cmbEmpresaConsen'
		,id: PF+'cmbEmpresaConsen'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 243
        ,y: 0
        ,width: 380
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresaConsen.getId());
					NS.accionarCmbEmpresaConcen(combo.getValue());
				}
			}
		}
	});
	
	//store divisas
	NS.storeCmbDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{condicion : 'id_divisa_soin is not null'},
		paramOrder:['condicion'],
		directFn: GlobalAction.llenarComboDivisas,
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//combo divisas
	NS.storeCmbDivisa.load();
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeCmbDivisa
		,name: PF+'cmbDivisa'
		,id: PF+'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 157
        ,y: 4
        ,width: 160
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione una divisa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtDivisa',NS.cmbDivisa.getId());
					NS.accionarCmbDivisa(combo.getValue());
				}
			}
		}
	});
	
	//store banco DE
	NS.storeCmbBancoDe = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{concentradora: true,
					empresa: 0,
					divisa: ''},
		paramOrder:['concentradora','empresa','divisa'],
		directFn: CoinversionAction.llenarComboBancosConcentradora,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//combo banco De
	NS.cmbBancoDe = new Ext.form.ComboBox({
		store: NS.storeCmbBancoDe
		,name: PF+'cmbBancoDe'
		,id: PF+'cmbBancoDe'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 117
        ,y: 50
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtBancoDe',NS.cmbBancoDe.getId());
					NS.accionarCmbBancoDe(combo.getValue());
				}
			}
		}
	});
	
	//store chequera DE
	NS.storeChequeraDe = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			pagadora: false,
			banco: 0,
			empresa: 0
		},
		root: '',
		paramOrder:['pagadora','banco','empresa'],
		directFn: CoinversionAction.llenarComboChequerasBarrido, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo chequera DE
	NS.cmbChequeraDe = new Ext.form.ComboBox({
		store: NS.storeChequeraDe
		,name: PF+'cmbChequeraDe'
		,id: PF+'cmbChequeraDe'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 117
        ,y: 92
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbChequeraDe(combo.getValue());
				}
			}
		}
	});
	
	//store empresa coinversionista
	NS.storeCmbEmpresaCoinv = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{empresa: 0,
					divisa: ''},
		paramOrder:['empresa','divisa'],
		directFn: CoinversionAction.llenarComboEmpresasCoinv,
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//combo Empresas coinversionista
	NS.cmbEmpresaCoinv = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresaCoinv
		,name: PF+'cmbEmpresaCoinv'
		,id: PF+'cmbEmpresaCoinv'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 117
        ,y: 10
        ,width: 230
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresaCoinv',NS.cmbEmpresaCoinv.getId());
					NS.accionarCmbEmpresaCoinv(combo.getValue());
				}
			}
		}
	});
	
	//store banco A
	NS.storeCmbBancoA = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{empresa: 0,
					divisa: ''},
		paramOrder:['empresa','divisa'],
		directFn: CoinversionAction.llenarCmbBancosSolFondeo2,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//combo banco A
	NS.cmbBancoA = new Ext.form.ComboBox({
		store: NS.storeCmbBancoA
		,name: PF+'cmbBancoA'
		,id: PF+'cmbBancoA'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 117
        ,y: 50
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtBancoA',NS.cmbBancoA.getId());
					NS.accionarCmbBancoA(combo.getValue());
				}
			}
		}
	});
	
	//store chequera A
	NS.storeChequeraA = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			banco: 0,
			empresa: 0
			},
		root: '',
		paramOrder:['banco','empresa'],
		directFn: CoinversionAction.llenarCmbChequeras, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo divisa A
	NS.cmbChequeraA = new Ext.form.ComboBox({
		store: NS.storeChequeraA
		,name: PF+'cmbChequeraA'
		,id: PF+'cmbChequeraA'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 117
        ,y: 92
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbChequeraA(combo.getValue());
				}
			}
		}
	});
	
	NS.accionarCmbEmpresaConcen = function(comboValor)
	{
		NS.nomEmpresaConcen = BFwrk.Util.scanFieldsStore(NS.storeCmbEmpresa, 'id', comboValor, 'descripcion');
		NS.storeCmbEmpresaCoinv.baseParams.empresa = comboValor;
		NS.storeCmbBancoDe.baseParams.empresa = comboValor;
		NS.storeChequeraDe.baseParams.empresa = comboValor;
		Ext.getCmp(PF+'txtNoEmpresaConcen').setValue(comboValor);
		Ext.getCmp(PF+'txtEmpresaConcen').setValue(NS.nomEmpresaConcen);
		NS.coinversora = comboValor;
	}
	
	NS.accionarCmbDivisa = function(comboValor)
	{
		Ext.getCmp(PF+'cmbEmpresaCoinv').reset();
		Ext.getCmp(PF+'txtEmpresaCoinv').reset();
		Ext.getCmp(PF+'cmbBancoDe').reset();
		Ext.getCmp(PF+'txtBancoDe').reset();
		Ext.getCmp(PF+'cmbChequeraDe').reset();
		Ext.getCmp(PF+'txtSaldoIniDe').reset();
		Ext.getCmp(PF+'txtSaldoFinDe').reset();
		Ext.getCmp(PF+'cmbBancoA').reset();
		Ext.getCmp(PF+'txtBancoA').reset();
		Ext.getCmp(PF+'cmbChequeraA').reset();
		Ext.getCmp(PF+'txtSaldoIniA').reset();
		Ext.getCmp(PF+'txtSaldoFinA').reset();
		NS.storeCmbEmpresaCoinv.baseParams.divisa = comboValor;
		NS.storeCmbEmpresaCoinv.load();
		NS.storeCmbBancoDe.baseParams.divisa = comboValor;
		NS.storeCmbBancoDe.load();
		NS.storeCmbBancoA.baseParams.divisa = comboValor;
		NS.divisa = comboValor;
	}
	//agregue un if para validar que el monto no sea vacio o 0
	NS.validarTxtMonto = function(valor)
	{
		if(Ext.getCmp(PF+'txtMonto').getValue()==='0' || Ext.getCmp(PF+'txtMonto').getValue()===''){
			Ext.getCmp(PF+'cmbEmpresaCoinv').reset();
			Ext.getCmp(PF+'txtEmpresaCoinv').reset();
			Ext.getCmp(PF+'cmbBancoDe').reset();
			Ext.getCmp(PF+'txtBancoDe').reset();
			Ext.getCmp(PF+'cmbChequeraDe').reset();
			Ext.getCmp(PF+'txtSaldoIniDe').reset();
			Ext.getCmp(PF+'txtSaldoFinDe').reset();
			Ext.getCmp(PF+'cmbBancoA').reset();
			Ext.getCmp(PF+'txtBancoA').reset();
			Ext.getCmp(PF+'cmbChequeraA').reset();
			Ext.getCmp(PF+'txtSaldoIniA').reset();
			Ext.getCmp(PF+'txtSaldoFinA').reset();
			Ext.getCmp(PF+'txtSaldoCoinv').reset();
			Ext.getCmp(PF+'txtMonto').reset();
		}else{
			if(Ext.getCmp(PF+'cmbChequeraA').getValue() !== '' && Ext.getCmp(PF+'cmbBancoA').getValue() !== '')
			{
				var saldoIni = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtSaldoIniA').getValue());
				Ext.getCmp(PF+'txtSaldoFinA').setValue(BFwrk.Util.formatNumber(Math.round((parseFloat(saldoIni) + parseFloat(valor))*100)/100))
	    	}
	    	if(Ext.getCmp(PF+'cmbChequeraDe').getValue() !== '' && Ext.getCmp(PF+'cmbBancoDe').getValue() !== '')
	    	{
	    		var saldoIni = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtSaldoIniDe').getValue());
				Ext.getCmp(PF+'txtSaldoFinDe').setValue(BFwrk.Util.formatNumber(Math.round((parseFloat(saldoIni) - parseFloat(valor))*100)/100))
	    	}
	    	NS.Monto = valor;
	    	Ext.getCmp(PF+'txtMonto').setValue(BFwrk.Util.formatNumber(valor));
	    }
	}
	
	NS.accionarCmbBancoDe = function(comboValor)
	{
		Ext.getCmp(PF+'cmbChequeraDe').reset();
		Ext.getCmp(PF+'txtSaldoIniDe').reset();
		Ext.getCmp(PF+'txtSaldoFinDe').reset();
		NS.storeChequeraDe.baseParams.banco = comboValor;
		NS.storeChequeraDe.load();
		NS.idBancoDe = comboValor;
	}
	
	NS.accionarCmbChequeraDe = function(comboValor)
	{
		var items = NS.storeChequeraDe.data.items;
		
		Ext.getCmp(PF+'txtSaldoIniDe').reset();
		Ext.getCmp(PF+'txtSaldoFinDe').reset();
		
		if(items.length > 0 && NS.idBancoDe > 0)
		{
		    //obtiene el saldo final e inicial de la chequera
		    CoinversionAction.consultarSaldoFinal(NS.coinversora, NS.idBancoDe, NS.divisa, comboValor, function(result, e)
			{
				if(result !== null && result !== undefined && result.length !== 0)
				{
					var saldoIni = result[0] + result[1];
					Ext.getCmp(PF+'txtSaldoIniDe').setValue(BFwrk.Util.formatNumber(Math.round((saldoIni)*100)/100)); 
					Ext.getCmp(PF+'txtSaldoFinDe').setValue(BFwrk.Util.formatNumber(Math.round((saldoIni - NS.Monto)*100)/100));
				}
				else
				{
					Ext.getCmp(PF+'txtSaldoIniDe').setValue(BFwrk.Util.formatNumber(0));
					Ext.getCmp(PF+'txtSaldoFinDe').setValue(BFwrk.Util.formatNumber(0));
				}
			});
		}
		NS.idChequeraDe = comboValor;
	}
	
	NS.accionarCmbEmpresaCoinv = function(comboValor)
	{
		Ext.getCmp(PF+'cmbBancoA').reset();
		Ext.getCmp(PF+'txtBancoA').reset();
		Ext.getCmp(PF+'cmbChequeraA').reset();
		Ext.getCmp(PF+'txtSaldoIniA').reset();
		Ext.getCmp(PF+'txtSaldoFinA').reset();
		Ext.getCmp(PF+'txtSaldoCoinv').reset();
		NS.storeCmbBancoA.baseParams.empresa = comboValor;
		NS.storeCmbBancoA.load();
		NS.storeChequeraA.baseParams.empresa = comboValor;
		NS.noEmpresa = comboValor;
	}
	
	NS.accionarCmbBancoA = function(comboValor)
	{
		Ext.getCmp(PF+'cmbChequeraA').reset();
		Ext.getCmp(PF+'txtSaldoIniA').reset();
		Ext.getCmp(PF+'txtSaldoFinA').reset();
		Ext.getCmp(PF+'txtSaldoCoinv').reset();
		NS.storeChequeraA.baseParams.banco = comboValor;
		NS.storeChequeraA.load();
		NS.idBancoA = comboValor;
	}
	
	NS.accionarCmbChequeraA = function(comboValor)
	{
		var items = NS.storeChequeraDe.data.items;
		
		Ext.getCmp(PF+'txtSaldoIniA').reset();
		Ext.getCmp(PF+'txtSaldoFinA').reset();
		Ext.getCmp(PF+'txtSaldoCoinv').reset();
		
		if(items.length > 0 && NS.idBancoA > 0)
		{
		    //obtiene el saldo final e inicial de la chequera
		    CoinversionAction.consultarSaldoFinal2(NS.idBancoA, comboValor, NS.noEmpresa, NS.coinversora, NS.divisa, function(result, e)
			{
				
				if(result !== null && result !== undefined && result.length !== 0)
				{
					var saldoIni = result[0] + result[2];
					Ext.getCmp(PF+'txtSaldoIniA').setValue(BFwrk.Util.formatNumber(Math.round((saldoIni)*100)/100));
					Ext.getCmp(PF+'txtSaldoFinA').setValue(BFwrk.Util.formatNumber(Math.round((parseFloat(saldoIni) + parseFloat(NS.Monto))*100)/100));
					Ext.getCmp(PF+'txtSaldoCoinv').setValue(BFwrk.Util.formatNumber(Math.round(((result[1])*100)/100)));
					NS.salCoinv = result[1];
				}
				else
				{
					Ext.getCmp(PF+'txtSaldoIniA').setValue(BFwrk.Util.formatNumber(0));
					Ext.getCmp(PF+'txtSaldoFinA').setValue(BFwrk.Util.formatNumber(0));
					Ext.getCmp(PF+'txtSaldoCoinv').setValue(BFwrk.Util.formatNumber(0));
					NS.salCoinv = 0;
				}
			});
		}
		NS.idChequeraA = comboValor;
	}
	
	NS.consultarConfSet = function()
	{
		GlobalAction.obtenerValorConfiguraSet(264, function(valor, e){
			if(valor!==null  &&  valor!= undefined)
			{
				if(valor !== 'SI')
					NS.confSet = true;
				else
					NS.confSet = false;
			}
		});
	}
	NS.consultarConfSet();
	
	NS.validaDatos = function()
	{
		var validaDatos = true;
		
		if(Ext.getCmp(PF+'cmbTipoSaldo').getValue()=='')
        {
			GlobalAction.obtenerValorConfiguraSet(1, function(valor, e)
				{
		    	if(valor!==null  &&  valor!= undefined)
				{
					if(valor === 'DALTON')
					{	
						 BFwrk.Util.msgShow('Selecciones el tipo de saldo','INFO');
					        validaDatos = false;
					        return;
					}
				}
				});
		  }
		
		else if(Ext.getCmp(PF+'txtEmpresa').getValue() === '')
    	{
    		BFwrk.Util.msgShow('Debe seleccionar una Empresa Concentradora','INFO');
	        validaDatos = false;
	        return;
       	}
       	else if(Ext.getCmp(PF+'txtDivisa').getValue() === '')
       	{
	        BFwrk.Util.msgShow('Debe seleccionar una Divisa','INFO');
	        validaDatos = false;
	        return;
        }
        else if(Ext.getCmp(PF+'txtNoEmpresaConcen').getValue() === '')
        {
	        BFwrk.Util.msgShow('Debe seleccionar una Empresa origen','INFO');
	        validaDatos = false;
	        return;
        }
        else if(Ext.getCmp(PF+'txtMonto').getValue() === '')
        {
	        BFwrk.Util.msgShow('Escriba el monto a traspasar','INFO');
	        validaDatos = false;
	        return;
        }
        else if(Ext.getCmp(PF+'cmbBancoDe').getValue() === '')
        {
	        BFwrk.Util.msgShow('Debe seleccionar un Banco origen','INFO');
	        validaDatos = false;
	        return;
        }
        else if(Ext.getCmp(PF+'cmbChequeraDe').getValue() === '')
        {
	        BFwrk.Util.msgShow('Debe seleccionar una Chequera origen','INFO');
	        validaDatos = false;
	        return;
        }
        else if(Ext.getCmp(PF+'txtEmpresaCoinv').getValue() === '')
        {
	        BFwrk.Util.msgShow('Debe seleccionar una Empresa destino','INFO');
	        validaDatos = false;
	        return;
        }
        else if(Ext.getCmp(PF+'cmbBancoA').getValue() === '')
        {
	        BFwrk.Util.msgShow('Debe seleccionar un Banco destino','INFO');
	        validaDatos = false;
	        return;
        }
        else if(Ext.getCmp(PF+'cmbChequeraA').getValue() === '')
        {
	        BFwrk.Util.msgShow('Debe seleccionar una Chequera destino','INFO');
	        validaDatos = false;
	        return;
        }
        else if(Ext.getCmp(PF+'txtMonto').getValue() !== '')
        {
        	var monto = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
        	if(parseFloat(monto) < 0)
        	{
		        BFwrk.Util.msgShow('No es posible un traspaso negativo','INFO');
		        validaDatos = false;
		        return;
	        }
        }
        else if(Ext.getCmp(PF+'cmbBancoDe').getValue() === Ext.getCmp(PF+'cmbBancoA').getValue() &&
        		Ext.getCmp(PF+'cmbChequeraDe').getValue() === Ext.getCmp(PF+'cmbChequeraA').getValue())
        {
	        BFwrk.Util.msgShow('No se puede transferir a la misma chequera','INFO');
	        validaDatos = false;
	        return;
        }
         else if(Ext.getCmp(PF+'txtConcepto').getValue() === '')
        {
	        BFwrk.Util.msgShow('Escriba un concepto','INFO');
	        validaDatos = false;
	        return;
        }
         
        	 
	    return validaDatos;
	}
	
	NS.mostrarTipoSaldo = function(){
		GlobalAction.obtenerValorConfiguraSet(1, function(valor, e){
			
	              			   			
			if(valor!==null  &&  valor!= undefined)
			{
				if(valor === 'DALTON')
				{				
					Ext.getCmp(PF+'cmbTipoSaldo').show();
					Ext.getCmp(PF+'tiposaldo').show();
					NS.storeTipoSaldo.load();
					//Ext.getCmp(PF+'chkFondear').show();
					//NS.mostrarFondeo = true;
				}
				else
				{
					Ext.getCmp(PF+'cmbTipoSaldo').hide();
					Ext.getCmp(PF+'tiposaldo').hide();
					//Ext.getCmp(PF+'chkFondear').hide();
					//NS.mostrarFondeo = false;
				}
			}
		});
	}
	NS.mostrarTipoSaldo();
	
	
	NS.contenedorSolFondeo = new Ext.FormPanel({
	    title: 'Solicitud de Fondeo',
	    width: 792,
	    height: 528,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                x: 0,
	                y: 0,
	                width: 770,
	                height: 480,
	                layout: 'absolute',
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: 'Concentradora',
	                        x: 0,
	                        y: -2,
	                        height: 60,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtEmpresa',
	                                name: PF+'txtEmpresa',
	                                x: 138,
	                                y: 1,
	                                width: 77,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresaConsen.getId());
			                        			NS.accionarCmbEmpresaConcen(comboValor);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbEmpresaConsen
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 64,
	                        height: 92,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Divisa:',
	                                x: 54,
	                                y: 7
	                            },
	                            {
	                                xtype: 'uppertextfield',
	                                x: 106,
	                                y: 3,
	                                width: 42,
	                                id: PF+'txtDivisa',
	                                name: PF+'txtDivisa',
	                                x: 106,
	                                y: 4,
	                                width: 42,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa',NS.cmbDivisa.getId());
			                        			NS.accionarCmbDivisa(comboValor);
			                        		}
			                        	}
			                        }
	                            },
	                           NS.cmbDivisa,
	                            {
	                                xtype: 'label',
	                                text: 'Monto:',
	                                x: 54,
	                                y: 46
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtMonto',
	                                name: PF+'txtMonto',
	                                emptyText: 'Escriba el monto del traspaso',
	                                value: '',
	                                x: 107,
	                                y: 44,
	                                width: 211,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			NS.validarTxtMonto(caja.getValue());
			                        		}
			                        	}
			                        }
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Concepto:',
	                                x: 364,
	                                y: 7
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtConcepto',
	                                name: PF+'txtConcepto',
	                                emptyText: 'Escriba un concepto',
	                                value: 'RETIRO',
	                                x: 435,
	                                y: 4,
	                                width: 200
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Tipo Saldo:',
	                                name: PF+'tiposaldo',
	                        		id: PF+'tiposaldo',
	                                x: 364,
	                                y: 50
	                            },
	                            NS.cmbTipoSaldo
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Traspaso de:',
	                        x: 1,
	                        y: 163,
	                        width: 370,
	                        height: 270,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Empresa:',
	                                x: 1,
	                                y: 10
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+ 'txtNoEmpresaConcen',
	                                name: PF+ 'txtNoEmpresaConcen',
	                                x: 56,
	                                y: 11,
	                                width: 50
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+ 'txtEmpresaConcen',
	                                name: PF+ 'txtEmpresaConcen',
	                                x: 117,
	                                y: 10,
	                                width: 230
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Banco:',
	                                x: 2,
	                                y: 54
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtBancoDe',
	                                name: PF+'txtBancoDe',
	                                x: 56,
	                                y: 51,
	                                width: 51,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoDe',NS.cmbBancoDe.getId());
			                        			NS.accionarCmbBancoDe(comboValor);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbBancoDe,
	                            {
	                                xtype: 'label',
	                                text: 'Chequera:',
	                                x: 1,
	                                y: 93
	                            },
	                            NS.cmbChequeraDe,
	                            {
	                                xtype: 'label',
	                                text: 'Saldo Disponible:',
	                                x: 1,
	                                y: 136,
	                                width: 94
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtSaldoIniDe',
	                                name: PF+'txtSaldoIniDe',
	                                readOnly: true,
	                                x: 117,
	                                y: 132,
	                                width: 180
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtSaldoFinDe',
	                                name: PF+'txtSaldoFinDe',
	                                readOnly: true,
	                                x: 117,
	                                y: 171,
	                                width: 180
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Nuevo Saldo Disp:',
	                                x: 1,
	                                y: 175,
	                                width: 101
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Traspaso a:',
	                        x: 377,
	                        y: 163,
	                        height: 270,
	                        width: 370,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Empresa:',
	                                x: 0,
	                                y: 12
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtEmpresaCoinv',
	                                name: PF+'txtEmpresaCoinv',
	                                x: 56,
	                                y: 11,
	                                width: 50,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresaCoinv',NS.cmbEmpresaCoinv.getId());
			                        			NS.accionarCmbEmpresaCoinv(comboValor);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbEmpresaCoinv,
	                            {
	                                xtype: 'label',
	                                text: 'Banco:',
	                                x: 3,
	                                y: 52
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtBancoA',
	                                name: PF+'txtBancoA',
	                                x: 59,
	                                y: 51,
	                                width: 50,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtBancoA',NS.cmbBancoA.getId());
			                        			NS.accionarCmbBancoA(comboValor);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbBancoA,
	                            {
	                                xtype: 'label',
	                                text: 'Chequera:',
	                                x: 1,
	                                y: 96
	                            },
	                            NS.cmbChequeraA,
	                            {
	                                xtype: 'label',
	                                text: 'Saldo Disponible:',
	                                x: 0,
	                                y: 137
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtSaldoIniA',
	                                name: PF+'txtSaldoIniA',
	                                readOnly: true,
	                                x: 118,
	                                y: 132,
	                                width: 180
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Nuevo Saldo Disp:',
	                                x: 0,
	                                y: 175
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtSaldoFinA',
	                                name: PF+'txtSaldoFinA',
	                                readOnly: true,
	                                x: 117,
	                                y: 172,
	                                width: 180
	                            },
	                            {
			                        xtype: 'label',
			                        text: 'Saldo Coinversion:',
			                        x: 0,
			                        y: 215
			                    },
			                    {
			                        xtype: 'textfield',
			                        id: PF+'txtSaldoCoinv',
			                        name: PF+'txtSaldoCoinv',
			                        readOnly: true,
			                        x: 117,
			                        y: 212,
			                        width: 180
			                    }
	                        ]
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Ejecutar',
	                        x: 558,
	                        y: 436,
	                        width: 70,
	                        listeners:{
	                        	click:{
	                        		fn:function(caja, valor){
	                        			var saldoFinC = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtSaldoFinDe').getValue());
	                        			var saldoCoinv = BFwrk.Util.unformatNumber(Ext.getCmp(PF+'txtSaldoCoinv').getValue());
	                        			var matrizCriterios = new Array ();
	                        			
	                        		
	                        			if(parseFloat(saldoFinC) < 0)
	                        			{
	                        				if(!confirm('No tiene saldo suficiente en la chequera, ' + 
													' ¿desea continuar?'))
               								return;
										}
	                        			
										
										if(NS.confSet === true)
										{
											GlobalAction.obtenerValorConfiguraSet(1, function(valor, e){
												
			              			   			
												if(valor!==null  &&  valor!= undefined)
												{
													if(valor != 'DALTON')
													{				
														if(parseFloat(NS.Monto) > parseFloat(saldoCoinv))
														{
															BFwrk.Util.msgShow('La empresa no tiene saldo suficiente en coinversion, ' +
																			'este movimiento no puede realizarse','INFO');
															return;
														}
													}
												}
											});
											
										}
										if(NS.validaDatos() == true)
										{
											var criterio = {};
											criterio.empresaOrigen = NS.coinversora;
											criterio.nomEmpresaOrigen = Ext.getCmp(PF+'txtEmpresaConcen').getValue();
											criterio.bancoOrigen = NS.idBancoDe;
											criterio.chequeraOrigen = NS.idChequeraDe;
											criterio.empresaDestino = NS.noEmpresa;
											criterio.nomEmpresaDestino = BFwrk.Util.scanFieldsStore(NS.storeCmbEmpresaCoinv, 'noEmpresa', NS.noEmpresaDe, 'nomEmpresa');
											criterio.bancoDestino = NS.idBancoA;
											criterio.chequeraDestino = NS.idChequeraA;
											criterio.divisa = NS.divisa;
											criterio.monto = NS.Monto;
											criterio.concepto = Ext.getCmp(PF+'txtConcepto').getValue()
											criterio.idSaldo = NS.idTipoSaldo;
											
											matrizCriterios[0] = criterio;
											var jsonStringC = Ext.util.JSON.encode(matrizCriterios);
											
											CoinversionAction.ejecutarSolicitudFondeo(jsonStringC, function(mapResult, e){
												if(mapResult.msgUsuario!==null  &&  mapResult.msgUsuario!=='' && mapResult.msgUsuario!==undefined)
												{
													BFwrk.Util.msgShow(''+mapResult.msgUsuario,'INFO');
													NS.contenedorSolFondeo.getForm().reset();
												}
											});
										}
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 645,
	                        y: 436,
	                        width: 70,
	                        listeners:{
	                        	click:{
	                        		fn:function(caja, valor){
	                        			NS.contenedorSolFondeo.getForm().reset();
	                        		}
	                        	}
	                        }
	                    }
	                ]
	            }
	        ]
	});
	NS.contenedorSolFondeo.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
		