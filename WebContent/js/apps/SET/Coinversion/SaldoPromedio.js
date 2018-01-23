/*
@author: Jessica Arelly Cruz Cruz
@since: 31/08/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Coinversion.SaldoPromedio');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	NS.GI_USUARIO = apps.SET.iUserId ;
	
	NS.noEmpresaC = 0;
	NS.dComision = 0;
	NS.dIva = 0;
	NS.dIsr = 0;
	NS.criterio = {};
    NS.matrizCriterios = new Array ();
    NS.matrizCriterios2 = new Array ();
    NS.criterio.optPromedio = 'true';
	NS.criterio.optDeterminada = 'false';
	NS.criterio.interes = 0;
   	NS.criterio.isr = 0;
	
	
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
				if(records.length == 1)
				{
					Ext.getCmp(PF+'txtEmpresa').setValue(records[0].get('id'));
	    			Ext.getCmp(PF+'cmbEmpresa').setValue(records[0].get('descripcion'));
					NS.accionarCmbEmpresa(records[0].get('id'));
				}
			}
		}
	}); 
	
	//combo Empresas coinversoras
	NS.storeCmbEmpresa.load();
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa
		,name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 160
        ,y: 25
        ,width: 306
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
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresa.getId());
					NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	
	NS.storeSoloDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'',
			campoDos:'id_divisa',
			tabla:'cat_divisa',
			condicion:'clasificacion = \'D\'',
			orden:'2'
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		directFn: GlobalAction.llenarComboGeneral, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo CajaVentana
	NS.storeSoloDivisas.load();
	NS.cmbDivisas = new Ext.form.ComboBox({
		store: NS.storeSoloDivisas
		,name: PF+'cmbDivisas'
		,id: PF+'cmbDivisas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 37
        ,y: 65
        ,width: 100
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Divisa'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbDivisas(combo.getValue());
				}
			}
		}
	});
	
	NS.storeValores = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			empresa: 0,
			divisa: ''
		},
		root: '',
		paramOrder:['empresa','divisa'],
		directFn: CoinversionAction.valoresSdoPromyCapitalizacion, 
		idProperty: 'empresa', 
		fields: [
			 {name: 'iva'},
			 {name: 'isr'},
			 {name: 'comision'},
			 {name: 'empresa'}
		],
		listeners: {
			load: function(s, records){
				if(records.length > 0)
				{
					NS.dComision = records[0].get('comision');
					NS.dIva = records[0].get('iva');
					NS.dIsr = records[0].get('isr');
					NS.criterio.dIsr = NS.dIsr;
					NS.criterio.dIva = NS.dIva;
					NS.criterio.dComision = NS.dComision;
		            Ext.getCmp(PF+'txtComision').setValue(BFwrk.Util.formatNumber(''+NS.dComision));
//		            TxtComision.Text = pdComision
	            }
	            else
	            {
	            	Ext.getCmp(PF+'txtComision').setValue(BFwrk.Util.formatNumber(0));
	            	NS.dComision = 0;
					NS.dIva = 0;
					NS.dIsr = 0;
					NS.criterio.dIsr = 0;
					NS.criterio.dIva = 0;
					NS.criterio.dComision = 0;
	            }
			}
		}
	}); 
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({});
	NS.storeCalculaInteres = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			parametros: ''
		},
		root: '',
		paramOrder:['parametros'],
		directFn: CoinversionAction.calcularInteresesPorDevengar, 
		fields: [
			{name: 'tasaDeterminada'},
			{name: 'factor'},
			
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'saldoPromedio'},
			{name: 'interes'},
			{name: 'isr'},
			{name: 'interesDev'},
			{name: 'isrVencido'},
			{name: 'comision'},
			{name: 'iva'},
			{name: 'total'},
			
			{name: 'interesT'},
			{name: 'isrT'},
			{name: 'comisionT'},
			{name: 'interesDevT'},
			{name: 'isrDevT'},
			{name: 'ivaT'},
		],
		listeners: {
			load: function(s, records){
				BFwrk.Util.msgWait('Calculando...', false);
				if(records.length > 0)
				{
					var cont = records.length - 1;
					Ext.getCmp(PF+'txtTasa').setValue(BFwrk.Util.formatNumber(records[cont].get('tasaDeterminada')));
	            	Ext.getCmp(PF+'txtFactor').setValue(BFwrk.Util.formatNumber(records[cont].get('factor')));
	            	Ext.getCmp(PF+'txtInteresT').setValue(BFwrk.Util.formatNumber(records[cont].get('interesT')));
	            	Ext.getCmp(PF+'txtIsrT').setValue(BFwrk.Util.formatNumber(records[cont].get('isrT')));
	            	Ext.getCmp(PF+'txtComisionT').setValue(BFwrk.Util.formatNumber(records[cont].get('comisionT')));	
	            	Ext.getCmp(PF+'txtInteresDevT').setValue(BFwrk.Util.formatNumber(records[cont].get('interesDevT')));
	            	Ext.getCmp(PF+'txtIsrDevT').setValue(BFwrk.Util.formatNumber(records[cont].get('isrDevT')));	
	            	Ext.getCmp(PF+'txtIvaT').setValue(BFwrk.Util.formatNumber(records[cont].get('ivaT')));		
	            }
	            else
	            {
	            	Ext.getCmp(PF+'txtTasa').setValue(BFwrk.Util.formatNumber(0));
	            	Ext.getCmp(PF+'txtFactor').setValue(BFwrk.Util.formatNumber(0));
	            	Ext.getCmp(PF+'txtInteresT').setValue(BFwrk.Util.formatNumber(0));
	            	Ext.getCmp(PF+'txtIsrT').setValue(BFwrk.Util.formatNumber(0));
	            	Ext.getCmp(PF+'txtComisionT').setValue(BFwrk.Util.formatNumber(0));	
	            	Ext.getCmp(PF+'txtInteresDevT').setValue(BFwrk.Util.formatNumber(0));
	            	Ext.getCmp(PF+'txtIsrDevT').setValue(BFwrk.Util.formatNumber(0));	
	            	Ext.getCmp(PF+'txtIvaT').setValue(BFwrk.Util.formatNumber(0));	
	            }
			}
		}
	}); 
	
	NS.gridConsulta  = new Ext.grid.GridPanel({
		store : NS.storeCalculaInteres,
		id: PF+'gridConsulta',
		name: PF+'gridConsulta',
		frame: true,
		//width: 745,
       	height: 200,
		x :0,
		y :222,
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            }	 
		,columns : [ 
		NS.sm,
		{
			header :'No Emp', width :70, dataIndex :'noEmpresa'
		},{
			header :'Empresa', width :200, dataIndex :'nomEmpresa'
		},{
			header :'Saldo Promedio', width :90, dataIndex :'saldoPromedio', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Inter\u00e9s', width :90, dataIndex :'interes', renderer: BFwrk.Util.rendererMoney
		},{
			header :'ISR', width :90, dataIndex :'isr', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Comisi\u00f3n', width :90, dataIndex :'comision', renderer: BFwrk.Util.rendererMoney
		},{
			header :'TOTAL', width :90, dataIndex :'total', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Int Devengado', width :90, dataIndex :'interesDev', renderer: BFwrk.Util.rendererMoney
		},{
			header :'ISR Vencido', width :90, dataIndex :'isrVencido', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Iva', width :90, dataIndex :'iva', renderer: BFwrk.Util.rendererMoney
		}]
		}),
		sm: NS.sm,
		listeners:{
			click:{
				fn:function(grid){
				}
			}
		}
	});
	
	
	NS.storeIntereses = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			empresa: 0,
			iva: NS.dIva,
			divisa: '',
			fecha1: '',
			fecha2: ''
		},
		root: '',
		paramOrder:['empresa','iva','divisa','fecha1','fecha2'],
		directFn: CoinversionAction.buscarIntereses, 
		idProperty: 'empresa', 
		fields: [
			 {name: 'interes'},
			 {name: 'iva'},
			 {name: 'isr'},
			 {name: 'interesDev'},
			 {name: 'isrDev'},
			 {name: 'bCalculo'}
		],
		listeners: {
			load: function(s, records){
				if(records.length > 0)
				{
					Ext.getCmp(PF+'txtInteres').setValue(BFwrk.Util.formatNumber(records[0].get('interes')));
	            	Ext.getCmp(PF+'txtIva').setValue(BFwrk.Util.formatNumber(records[0].get('iva')));
	            	Ext.getCmp(PF+'txtIsr').setValue(BFwrk.Util.formatNumber(records[0].get('isr')));
	            	Ext.getCmp(PF+'txtInteresDev').setValue(BFwrk.Util.formatNumber(records[0].get('interesDev')));
	            	Ext.getCmp(PF+'txtIsrDev').setValue(BFwrk.Util.formatNumber(records[0].get('isrDev')));	
	            	NS.criterio.interes = records[0].get('interes');
	            	NS.criterio.isr = records[0].get('isr');
	            	if(records[0].get('bCalculo') === true)
	            	{
	            		BFwrk.Util.msgShow('No existen datos para calcular los intereses por devengar','WARNING');
	            		return;
	            	}
	            }
	            else
	            {
	            	Ext.getCmp(PF+'txtInteres').setValue(BFwrk.Util.formatNumber(0));
	            	Ext.getCmp(PF+'txtIva').setValue(BFwrk.Util.formatNumber(0));
	            	Ext.getCmp(PF+'txtIsr').setValue(BFwrk.Util.formatNumber(0));
	            	Ext.getCmp(PF+'txtInteresDev').setValue(BFwrk.Util.formatNumber(0));
	            	Ext.getCmp(PF+'txtIsrDev').setValue(BFwrk.Util.formatNumber(0));	
	            	NS.criterio.interes = 0;
	            	NS.criterio.isr = 0;
	            	BFwrk.Util.msgShow('No existen datos para calcular los intereses por devengar','WARNING');
	            	return;
	            }
			}
		}
	}); 
	
	
	NS.storeDivisaVentana = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			condicion:'id_divisa in(\'MN\',\'DLS\')'
		},
		root: '',
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
	
	//combo DivisaVentana
	NS.storeDivisaVentana.load();
	NS.cmbDivisaVentana = new Ext.form.ComboBox({
		store: NS.storeDivisaVentana
		,name: PF+'cmbDivisaVentana'
		,id: PF+'cmbDivisaVentana'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 40
        ,y: 1
        ,width: 140
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione una divisa'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
				}
			}
		}
	});
	
	NS.storeAnioVentana = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
		},
		root: '',
		paramOrder:[],
		directFn: CoinversionAction.llenarCmbAnios, 
		idProperty: 'anio', 
		fields: [
			 {name: 'anio'},
			 {name: 'anio'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo años Ventana
	NS.storeAnioVentana.load();
	NS.cmbAnioVentana = new Ext.form.ComboBox({
		store: NS.storeAnioVentana
		,name: PF+'cmbAnioVentana'
		,id: PF+'cmbAnioVentana'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 40
        ,y: 40
        ,width: 140
		,valueField:'anio'
		,displayField:'anio'
		,autocomplete: true
		,emptyText: 'Seleccione un año'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.storeMesesVentana.baseParams.anio = combo.getValue();
					NS.storeMesesVentana.load();
				}
			}
		}
	});
	
	NS.storeMesesVentana = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			anio: 0
		},
		root: '',
		paramOrder:['anio'],
		directFn: CoinversionAction.llenarCmbMeses, 
		idProperty: 'numMes', 
		fields: [
			 {name: 'numMes'},
			 {name: 'mes'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo años Ventana
	NS.cmbMesesVentana = new Ext.form.ComboBox({
		store: NS.storeMesesVentana
		,name: PF+'cmbMesesVentana'
		,id: PF+'cmbMesesVentana'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 40
        ,y: 80
        ,width: 140
		,valueField:'numMes'
		,displayField:'mes'
		,autocomplete: true
		,emptyText: 'Seleccione un mes'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
				}
			}
		}
	});
	
	NS.accionarCmbEmpresa = function(comboValor)
	{
	    Ext.getCmp(PF+'txtFechaIni').reset();
	    Ext.getCmp(PF+'txtFechaFin').reset();
	    Ext.getCmp(PF+'cmbDivisas').reset();
	    Ext.getCmp(PF+'txtInteresT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIsrT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtComisionT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtInteresDevT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIsrDevT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIvaT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtInteres').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIsr').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtComision').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtInteresDev').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIsrDev').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIva').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtFactor').setValue(BFwrk.Util.formatNumber(0));

		NS.storeValores.baseParams.empresa = comboValor;
		NS.storeIntereses.baseParams.empresa = comboValor;
		NS.noEmpresaC = comboValor;
		NS.criterio.empresa = comboValor;
	}
	
	NS.accionarCmbDivisas = function(comboValor)
	{
		Ext.getCmp(PF+'txtFechaIni').reset();
	    Ext.getCmp(PF+'txtFechaFin').reset();
	    Ext.getCmp(PF+'txtInteresT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIsrT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtComisionT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtInteresDevT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIsrDevT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIvaT').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtInteres').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIsr').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtComision').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtInteresDev').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIsrDev').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtIva').setValue(BFwrk.Util.formatNumber(0));
		Ext.getCmp(PF+'txtFactor').setValue(BFwrk.Util.formatNumber(0));
		
		if(Ext.getCmp(PF+'cmbEmpresa').getValue() === '')
		{
			BFwrk.Util.msgShow('Es necesario seleccionar una empresa concentradora','INFO');
			Ext.getCmp(PF+'cmbDivisas').reset();
			return;
		}
		NS.storeValores.baseParams.divisa = comboValor;
		NS.storeIntereses.baseParams.divisa = comboValor;
		NS.storeValores.load();	
		NS.criterio.divisa = comboValor;
	}
	
	NS.consultaFiliales = function()
	{
		var fechaIni = Ext.getCmp(PF+'txtFechaIni').getValue();
		var fechaFin = Ext.getCmp(PF+'txtFechaFin').getValue();
		var fecha1 = new Date(fechaIni);
		var fecha2 = new Date(fechaFin);
		var dia1 = fecha1.getDate();
		var dia2 = fecha2.getDate();
		var mes1 = fecha1.getMonth() + 1;
		var mes2 = fecha2.getMonth() + 1;
		var anio1 = fecha1.getFullYear();
		var anio2 = fecha2.getFullYear();
		var iDiaMax = '';
		
		Ext.getCmp(PF+'txtTasa').setValue();
	    Ext.getCmp(PF+'txtInteresT').setValue();
	    Ext.getCmp(PF+'txtIsrT').setValue();
	    Ext.getCmp(PF+'txtComisionT').setValue();
	    Ext.getCmp(PF+'txtInteresDevT').setValue();
	    Ext.getCmp(PF+'txtIsrDevT').setValue();
	    Ext.getCmp(PF+'txtIvaT').setValue();
			
		if(fechaIni === '' || fechaFin === '')
		{
			BFwrk.Util.msgShow('Falta una fecha','WARNING');
			return;
		}
		
		if(Ext.getCmp(PF+'optPromedio').getValue(true))
		{
			if(mes1 === mes2 && anio1 === anio2)
			{
				if(dia1 !== 1)
				{
					BFwrk.Util.msgShow('La fecha de inicio debe ser a partir del primer d\u00eda del mes ' +
					'cuando selecciona la opci\u00f3n de Tasa Promedio','WARNING');
					return;
				}
				else
				{
					iDiaMax = (new Date((new Date(anio2, mes2,1))-1)).getDate();
					
					if(dia2 !== iDiaMax)
					{
						BFwrk.Util.msgShow('La fecha final debe ser el \u00faltimo d\u00eda del mes ' +
						'cuando selecciona la opci\u00f3n de Tasa Promedio','WARNING');
						return;
	                }
	            }
            }
            else
            {
            	BFwrk.Util.msgShow('La fecha de inicio y fin deben ser del mismo mes y a\u00f1o ' +
				'cuando selecciona la opci\u00f3n de Tasa Promedio','WARNING');
				return;
            }
	    }
	    
	    BFwrk.Util.msgWait('Calculando...', true);
	    
	    NS.criterio.tasaDeterminada = Ext.getCmp(PF+'txtTasa').getValue();
	    NS.criterio.interesT = Ext.getCmp(PF+'txtInteresT').getValue();
	    NS.criterio.isrT = Ext.getCmp(PF+'txtIsrT').getValue();
	    NS.criterio.comisionT = Ext.getCmp(PF+'txtComisionT').getValue();
	    NS.criterio.interesDevT = Ext.getCmp(PF+'txtInteresDevT').getValue();
	    NS.criterio.isrDevT = Ext.getCmp(PF+'txtIsrDevT').getValue();
	    NS.criterio.ivaT = Ext.getCmp(PF+'txtIvaT').getValue();
		
		NS.matrizCriterios[0] = NS.criterio;
		var jsonStringC = Ext.util.JSON.encode(NS.matrizCriterios);
		
		NS.storeCalculaInteres.baseParams.parametros = jsonStringC;
		NS.storeCalculaInteres.load();
	}
	
	NS.ejecutaCapitalizacion = function()
	{
	    var matrizRegistros = new Array ();
	    var regSelect = NS.gridConsulta.getSelectionModel().getSelections();
		
		for(var i = 0; i < regSelect.length; i = i + 1)
		{
			var registros = {};
			registros.tasaDeterminada = regSelect[i].get('tasaDeterminada');
			registros.factor = regSelect[i].get('factor');
			registros.noEmpresa = regSelect[i].get('noEmpresa');
			registros.nomEmpresa = regSelect[i].get('nomEmpresa');
			registros.saldoPromedio = regSelect[i].get('saldoPromedio');
			registros.interes = regSelect[i].get('interes');
			registros.isr = regSelect[i].get('isr');
			registros.interesDev = regSelect[i].get('interesDev');
			registros.isrVencido = regSelect[i].get('isrVencido');
			registros.comision = regSelect[i].get('comision');
			registros.iva = regSelect[i].get('iva');
			registros.total = regSelect[i].get('total');
			matrizRegistros[i] = registros;
		}
		var jsonStringR = Ext.util.JSON.encode(matrizRegistros);
		NS.matrizCriterios2[0] = NS.criterio;
		var jsonString = Ext.util.JSON.encode(NS.matrizCriterios2);
		
		CoinversionAction.ejecutarCapitalizacion(jsonStringR, jsonString, function(mapResult, e){
			if(mapResult.msgUsuario!==null  &&  mapResult.msgUsuario!=='' && mapResult.msgUsuario!= undefined)
			{
				for(var msg = 0; msg < mapResult.msgUsuario.length; msg++)
				{
					//BFwrk.Util.msgShow(''+mapResult.msgUsuario[msg], 'INFO');
					alert(''+mapResult.msgUsuario[msg]);
				}
			}
		});
	}
	
	NS.contenedorSaldoPromedio = new Ext.FormPanel({
	    title: 'Saldo Promedio y Capitalizaci\u00f3n',
	    width: 802,
	    height: 621,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                height: 580,
	                x: 0,
	                y: 0,
	                layout: 'absolute',
	                width: 790,
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 0,
	                        height: 121,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtEmpresa',
	                                name: PF+'txtEmpresa',
	                                x: 88,
	                                y: 25,
	                                width: 60,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
			                        			NS.accionarCmbEmpresa(comboValue);
			                        		}
			                        	}
			                        }
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Empresa Concentradora:',
	                                x: 1,
	                                y: 21,
	                                width: 93
	                            },
	                            NS.cmbEmpresa,
	                            {
	                                xtype: 'label',
	                                text: 'Divisa:',
	                                x: 1,
	                                y: 71
	                            },
	                            NS.cmbDivisas,
	                            {
	                                xtype: 'label',
	                                text: 'Desde:',
	                                x: 154,
	                                y: 71
	                            },
	                            {
	                                xtype: 'datefield',
	                                format: 'd/m/Y',
	                                id: PF+'txtFechaIni',
	                                name: PF+'txtFechaIni',
	                                emptyText: 'Fecha de inicio',
	                                x: 198,
	                                y: 65,
	                                width: 105,
	                                listeners:{
			                       		change:{
											fn:function(caja,valor) {
												NS.storeIntereses.baseParams.fecha1 = BFwrk.Util.changeDateToString(''+caja.getValue());
												NS.criterio.fecha1 = BFwrk.Util.changeDateToString(''+caja.getValue());
											}
										}
									}
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Hasta:',
	                                x: 318,
	                                y: 71
	                            },
	                            {
	                                xtype: 'datefield',
	                                format: 'd/m/Y',
	                                id: PF+'txtFechaFin',
	                                name: PF+'txtFechaFin',
	                                emptyText: 'Fecha de fin',
	                                x: 362,
	                                y: 65,
	                                width: 105,
	                                listeners:{
			                       		change:{
											fn:function(caja,valor) {
												NS.storeIntereses.baseParams.fecha2 = BFwrk.Util.changeDateToString(''+caja.getValue());
												NS.criterio.fecha2 = BFwrk.Util.changeDateToString(''+caja.getValue());
											}
										}
									}
	                            },
	                            {
	                                xtype: 'fieldset',
	                                title: 'Tipo de tasa para el c\u00e1lculo de intereses',
	                                x: 486,
	                                y: 1,
	                                width: 252,
	                                height: 96,
	                                layout: 'absolute',
	                                items: [
	                                    {
	                                        xtype: 'radio',
	                                        id: PF+'optPromedio',
	                                		name: PF+'optPromedio',
	                                		checked: true,
	                                        x: 0,
	                                        y: 0,
	                                        boxLabel: 'Tasa Promedio',
	                                        listeners:{
				              					check:{
		              			   					fn:function(opt, valor){
		                               					if(valor==true)
			                               				{
			                               					Ext.getCmp(PF+'optDeterminada').setValue(false);
			                               					Ext.getCmp(PF+'txtTasa').setPosition(120,2);
			                               					Ext.getCmp(PF+'txtTasa').reset();
			                               					Ext.getCmp(PF+'txtTasa').setDisabled(true);
			                               					Ext.getCmp(PF+'txtInteresT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtIsrT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtComisionT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtInteresDevT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtIsrDevT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtIvaT').setValue(BFwrk.Util.formatNumber(0));
			                               					NS.criterio.optPromedio = 'true';
			                               					NS.criterio.optDeterminada = 'false';
			                               				}
		                              				}
		                           				}
		                       				}
	                                    },
	                                    {
	                                        xtype: 'radio',
	                                        id: PF+'optDeterminada',
	                                		name: PF+'optDeterminada',
	                                        x: 0,
	                                        y: 30,
	                                        boxLabel: 'Tasa Determinada',
	                                        listeners:{
				              					check:{
		              			   					fn:function(opt, valor){
		                               					if(valor==true)
			                               				{
			                               					Ext.getCmp(PF+'optPromedio').setValue(false);
			                               					Ext.getCmp(PF+'txtTasa').setPosition(120,30);
			                               					Ext.getCmp(PF+'txtTasa').setDisabled(false);
			                               					Ext.getCmp(PF+'txtInteresT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtIsrT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtComisionT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtInteresDevT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtIsrDevT').setValue(BFwrk.Util.formatNumber(0));
			                               					Ext.getCmp(PF+'txtIvaT').setValue(BFwrk.Util.formatNumber(0));
			                               					NS.criterio.optPromedio = 'false';
			                               					NS.criterio.optDeterminada = 'true';
			                               				}
		                              				}
		                           				}
		                       				}
	                                    },
	                                    {
	                                        xtype: 'textfield',
	                                        id: PF+'txtTasa',
	                                		name: PF+'txtTasa',
	                                        emptyText: '0.00',
	                                        disabled: true,
	                                        x: 120,
	                                        y: 2,
	                                        width: 85
	                                    }
	                                ]
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Inversiones',
	                        x: 0,
	                        y: 131,
	                        height: 86,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Intereses:',
	                                x: 3,
	                                y: 8
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'ISR:',
	                                x: 110,
	                                y: 8
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Comisi\u00f3n:',
	                                x: 219,
	                                y: 8
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Factor:',
	                                x: 329,
	                                y: 7
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Inter\u00e9s Devengado:',
	                                x: 439,
	                                y: -1,
	                                width: 90
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Impuestos Vencidos:',
	                                x: 547,
	                                y: 0,
	                                width: 88
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Iva:',
	                                x: 659,
	                                y: 7
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtInteres',
	                                name:PF+'txtInteres',
	                                disabled: true,
	                                x: 0,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtIsr',
	                                name: PF+'txtIsr',
	                                disabled: true,
	                                x: 108,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtComision',
	                                name:PF+'txtComision',
	                                disabled: true,
	                                x: 218,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtFactor',
	                                name:PF+'txtFactor',
	                                disabled: true,
	                                x: 327,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtInteresDev',
	                                name:PF+'txtInteresDev',
	                                disabled: true,
	                                x: 437,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtIsrDev',
	                                name:PF+'txtIsrDev',
	                                disabled: true,
	                                x: 548,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtIva',
	                                name:PF+'txtIva',
	                                disabled: true,
	                                x: 656,
	                                y: 29,
	                                width: 90
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Total',
	                        x: 1,
	                        y: 429,
	                        height: 86,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Intereses:',
	                                x: 7,
	                                y: 4
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'ISR:',
	                                x: 138,
	                                y: 3
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Comisi\u00f3n:',
	                                x: 268,
	                                y: 4
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Inter\u00e9s Devengado:',
	                                x: 399,
	                                y: -2,
	                                width: 90
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Impuestos Vencidos:',
	                                x: 527,
	                                y: -1,
	                                width: 88
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Iva:',
	                                x: 659,
	                                y: 7
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtInteresT',
	                                name:PF+'txtInteresT',
	                                disabled: true,
	                                x: 8,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                
	                                id:PF+'txtIsrT',
	                                name:PF+'txtIsrT',
	                                disabled: true,
	                                x: 137,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtComisionT',
	                                name:PF+'txtComisionT',
	                                disabled: true,
	                                x: 266,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtInteresDevT',
	                                name:PF+'txtInteresDevT',
	                                disabled: true,
	                                x: 396,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtIsrDevT',
	                                name:PF+'txtIsrDevT',
	                                disabled: true,
	                                x: 527,
	                                y: 29,
	                                width: 90
	                            },
	                            {
	                                xtype: 'textfield',
	                                id:PF+'txtIvaT',
	                                name:PF+'txtIvaT',
	                                disabled: true,
	                                x: 655,
	                                y: 29,
	                                width: 90
	                            }
	                        ]
	                    },
							NS.gridConsulta,
	                    {
	                        xtype: 'button',
	                        text: 'Rep. Tasa Promedio',
	                        x: 166,
	                        y: 528,
	                        listeners:{
				           		click:{
				           			fn:function(){
					        			NS.ventanaReporte.show();
			           				}
				           		}
				           	}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Buscar',
	                        x: 297,
	                        y: 527,
	                        width: 70,
	                        listeners:{
		                      	click:{
		                   			fn:function(e){
		                   				var fechaIni = Ext.getCmp(PF+'txtFechaIni').getValue();
		                   				var fechaFin = Ext.getCmp(PF+'txtFechaFin').getValue();
		                   				
		                   				if(fechaIni === '' || fechaFin === '')
		                   				{
		                   					BFwrk.Util.msgShow('Falta una fecha','WARNING');
		                   					return;
		                   				}
		                   				else if(BFwrk.Util.changeDateToString(''+fechaFin) <= BFwrk.Util.changeDateToString(''+fechaIni))
		                   				{
		                   					BFwrk.Util.msgShow('La segunda fecha debe ser mayor a la primera','WARNING');
		                   					return;
		                   				}
		                   				else if(Ext.getCmp(PF+'cmbDivisas').getValue() === '')
		                   				{
		                   					BFwrk.Util.msgShow('Es necesario seleccionar una divisa','WARNING');
		                   					return;
		                   				}
		                   				else if(Ext.getCmp(PF+'cmbEmpresa').getValue() === '')
		                   				{
		                   					BFwrk.Util.msgShow('Es necesario seleccionar una empresa concentradora','INFO');
		                   					return;
		                   				}
		                   				
		                   				NS.storeIntereses.load();
		                       		}
		               			}
		               		}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Calcular',
	                        x: 397,
	                        y: 527,
	                        width: 70,
	                        listeners:{
				           		click:{
				           			fn:function(){
				           				var comision = 0;
				           				comision = Ext.getCmp(PF+'txtComision').getValue();
				           				if(comision === '')
				           				{
				           					BFwrk.Util.msgShow('Es necesario capturar el porcentaje de comisi\u00f3n','WARNING');
				           					Ext.getCmp(PF+'txtComision').setDisabled(false);
		                   					return;
								        }
								        else
								        {
								        	if(comision > 100)
								        	{
								        		BFwrk.Util.msgShow('La comisi\u00f3n debe ser menor a 100','WARNING');
		                   						return;
								        	}
								        	else
								        		NS.consultaFiliales();
								        }
			           				}
				           		}
				           	}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Ejecutar',
	                        x: 498,
	                        y: 528,
	                        width: 70,
	                        listeners:{
				           		click:{
				           			fn:function(){
				           				var records = NS.storeCalculaInteres.data.items;
				           				var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
				           			
				           				if(!confirm('Est\u00e1 a punto de realizar la Capitalizaci\u00f3n, ' + 
													' ¿desea continuar?'))
              								return;
              								
									    if(Ext.getCmp(PF+'cmbEmpresa').getValue() === '' || Ext.getCmp(PF+'txtEmpresa').getValue() === '')
									    {
									    	BFwrk.Util.msgShow('Es necesario seleccionar una empresa concentradora','WARNING');
											return;
									    }
									    
									    if(Ext.getCmp(PF+'txtFechaIni').getValue() === '' || Ext.getCmp(PF+'txtFechaFin').getValue() === '')
									    {
									    	BFwrk.Util.msgShow('Es necesario capturar el rango de fechas','WARNING');
											return;
									    }
									    else if(Ext.getCmp(PF+'txtFechaIni').getValue()
									    	>	Ext.getCmp(PF+'txtFechaFin').getValue())
									    {
									    	BFwrk.Util.msgShow('La primer fecha debe ser menor que la segunda','WARNING');
											return;
									    }
									    
									    if(Ext.getCmp(PF+'txtComision').getValue() === '' || Ext.getCmp(PF+'txtInteres').getValue() === ''
									    	|| Ext.getCmp(PF+'txtIsr').getValue() === '' || Ext.getCmp(PF+'txtFactor').getValue() === ''
									    	|| Ext.getCmp(PF+'cmbDivisas').getValue() === '')
								    	{
								    		BFwrk.Util.msgShow('Datos incompletos!','WARNING');
											return;
								    	}
								    	
								    	if(records.length > 0)
								    	{
								    		if(regSelec.length > 0)
								    		{
									    		for(var i = 0; i < regSelec.length; i = i + 1)
									    		{
									    			if(regSelec[i].get('interesDev') === '')
									    			{
									    				BFwrk.Util.msgShow('La empresa ' + regSelec[i].get('nomEmpresa') +
									    				' No tiene chequera para ejecutar traspasos, '+
									    				' Es necesario darla de alta' ,'WARNING');
														return;
									    			}
									    		}
								    		}
								    		else
								    		{
								    			BFwrk.Util.msgShow('Seleccione al menos un registro','WARNING');
												return;
								    		}
								    	}
								    	else
								    	{
								    		BFwrk.Util.msgShow('No existen datos!','WARNING');
											return;
								    	}
								    	
								    	NS.ejecutaCapitalizacion();
			           				}
				           		}
				           	}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 598,
	                        y: 528,
	                        width: 70,
	                        listeners:{
				           		click:{
				           			fn:function(){
					        			NS.contenedorSaldoPromedio.getForm().reset();
					        			NS.storeCalculaInteres.removeAll();
		              			   		NS.gridConsulta.getView().refresh();
			           				}
				           		}
				           	}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 698,
	                        y: 528,
	                        width: 70,
	                        listeners:{
				           		click:{
				           			fn:function(){
				           				var records = NS.storeCalculaInteres.data.items;
				           				var dias = BFwrk.Util.daysBetweenDates(Ext.getCmp(PF+'txtFechaIni').getValue(),Ext.getCmp(PF+'txtFechaFin').getValue()) + 1
				           				
				           				if(records.length > 0)
				           				{
						        			strParams = '?nomReporte=ReporteSaldoPromedioGrid';
											strParams += '&'+'nomParam1=usuario';
											strParams += '&'+'valParam1='+NS.GI_USUARIO;
											strParams += '&'+'nomParam2=nomEmpresa';
											strParams += '&'+'valParam2='+BFwrk.Util.scanFieldsStore(NS.storeCmbEmpresa, 'id', NS.noEmpresaC, 'descripcion');
											strParams += '&'+'nomParam3=subTitulo';
											strParams += '&'+'valParam3='+'CONSULTA DE SALDO PROMEDIO DEL ' + 
															BFwrk.Util.changeDateToString(''+Ext.getCmp(PF+'txtFechaIni').getValue()) + ' AL ' + 
															BFwrk.Util.changeDateToString(''+Ext.getCmp(PF+'txtFechaFin').getValue()) + ' EN ' + 
															Ext.getCmp(PF+'cmbDivisas').getValue();
											strParams += '&'+'nomParam4=dias';
											strParams += '&'+'valParam4='+ dias;
											window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
								        }
									    else
									    {
									        BFwrk.Util.msgShow('No existen datos para el reporte','WARNING');
											return;
									    }
			           				}
				           		}
				           	}
	                    }
	                ]
	            }
	        ]
	});
	
	NS.ventanaReporte = new Ext.Window({
	    title: 'Reporte Tasa Promedio',
	    width: 254,
	    height: 208,
	    modal:true,
		shadow:true,
		closeAction: 'hide',
		triggerAction: 'all',
		id: PF+'ventanaReporte',
	    name: PF+'ventanaReporte',
	    layout: 'absolute',
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                width: 220,
	                x: 10,
	                y: 10,
	                height: 130,
	                layout: 'absolute',
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Divisa:',
	                        x: 0,
	                        y: 5
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'año:',
	                        x: 0,
	                        y: 45
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Mes:',
	                        x: 0,
	                        y: 85
	                    },
	                   NS.cmbDivisaVentana,
	                   NS.cmbAnioVentana,
	                   NS.cmbMesesVentana
	                ]
	            },
	            {
	                xtype: 'button',
	                text: 'Ejecutar',
	                x: 10,
	                y: 150,
	                width: 60,
	                listeners:{
                      	click:{
                   			fn:function(e){
                   				if(Ext.getCmp(PF+'cmbDivisaVentana').getValue() === '')
                   				{
              					 	BFwrk.Util.msgShow('Debe seleccionar una divisa','INFO');
									return;
							    }
							    
							    if(Ext.getCmp(PF+'cmbAnioVentana').getValue() === '')
                   				{
              					 	BFwrk.Util.msgShow('Debe seleccionar un a\u00f1o','INFO');
									return;
							    }
							    
							    if(Ext.getCmp(PF+'cmbMesesVentana').getValue() === '')
                   				{
              					 	BFwrk.Util.msgShow('Debe seleccionar un mes','INFO');
									return;
							    }
							    
							    var mes = BFwrk.Util.scanFieldsStore(NS.storeMesesVentana, 'numMes', Ext.getCmp(PF+'cmbMesesVentana').getValue(), 'mes')
							    var divisa = Ext.getCmp(PF+'cmbDivisaVentana').getValue();
							    var anio = Ext.getCmp(PF+'cmbAnioVentana').getValue();
							    strParams = '?nomReporte=ReporteTasasInversion';
								strParams += '&'+'nomParam1=titulo';
								strParams += '&'+'valParam1='+'REPORTE DE TASA PROMEDIO DE INVERSION ' + divisa;
								strParams += '&'+'nomParam2=titulo2';
								strParams += '&'+'valParam2='+mes + '  ' + anio;
								strParams += '&'+'nomParam3=divisa';
								strParams += '&'+'valParam3='+divisa;
								strParams += '&'+'nomParam4=nomMes';
								strParams += '&'+'valParam4='+ mes;
								strParams += '&'+'nomParam5=anio';
								strParams += '&'+'valParam5='+anio;
								window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
                       		}
               			}
               		}
	            },
	            {
	                xtype: 'button',
	                text: 'Limpiar',
	                x: 90,
	                y: 150,
	                width: 60,
	                listeners:{
                      	click:{
                   			fn:function(e){
                   				Ext.getCmp(PF+'cmbDivisaVentana').reset();
                   				Ext.getCmp(PF+'cmbAnioVentana').reset();
                   				Ext.getCmp(PF+'cmbMesesVentana').reset();
                       		}
               			}
               		}
	            },
	            {
	                xtype: 'button',
	                text: 'Cerrar',
	                x: 170,
	                y: 150,
	                width: 60,
	                listeners:{
                      	click:{
                   			fn:function(e){
                   				NS.ventanaReporte.hide();
                   				Ext.getCmp(PF+'cmbDivisaVentana').reset();
                   				Ext.getCmp(PF+'cmbAnioVentana').reset();
                   				Ext.getCmp(PF+'cmbMesesVentana').reset();
                       		}
               			}
               		}
	            }
	        ]
	});
	
	NS.contenedorSaldoPromedio.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	staticCheck("div[id*='gridConsulta'] div div[class='x-panel-ml']","div[id*='gridConsulta'] div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
});
