/*
@author: Jessica Arelly Cruz Cruz
@since: 05/08/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Coinversion.BarridoAtmSaldos');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	NS.GI_USUARIO = apps.SET.iUserId ;
	NS.GI_ID_CAJA = apps.SET.ID_CAJA;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	
	NS.psBancos = '';
	NS.mostrarFondeo = false;
	NS.nomEmpresa = '';
	NS.noConcentradora = 0;

	NS.idTipoSaldo=0;
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
        ,x: 118
        ,y: 4
        ,width: 261
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
	
	
//Tipo Saldo Alejandra velazquez
	
	NS.cmbTipoSaldo = new Ext.form.ComboBox({
		store: NS.storeTipoSaldo
		,name: PF+'cmbTipoSaldo'
		,id: PF+'cmbTipoSaldo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 58
        ,y: 150
        ,width: 212
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Tipo de Saldo'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					var valor=BFwrk.Util.updateComboToTextField( PF+'cmbTipoSaldo',NS.cmbTipoSaldo.getId());
					//NS.accionarcmbTipoSaldo(combo.getValue());
					NS.idTipoSaldo=combo.getValue();
					console.log("id tipo saldo "+combo.getValue());
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
        ,x: 118
        ,y: 38
        ,width: 150
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
	
	//store banco
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			concentradora: false,
			empresa: 0,
			divisa: '' 
		},
		root: '',
		paramOrder:['concentradora','empresa','divisa'],
		directFn: CoinversionAction.llenarComboBancosConcentradora, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					BFwrk.Util.msgShow('No hay bancos disponibles', 'INFO');
					Ext.getCmp(PF+'txtBanco').setValue('');
					Ext.getCmp(PF+'cmbBanco').setValue('');
					return;
				}
			}
		}
	}); 
	
	//combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 118
        ,y: 73
        ,width: 150
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
					 BFwrk.Util.updateComboToTextField(PF+'txtBanco',NS.cmbBanco.getId());
					 NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	
	//store chequera
	NS.storeChequera = new Ext.data.DirectStore({
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
	
	//combo divisa
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera
		,name: PF+'cmbChequera'
		,id: PF+'cmbChequera'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 57
        ,y: 108
        ,width: 212
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una Chequera'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.idChequera = combo.getValue();
				}
			}
		}
	});
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({});
	
	//stores del grid de consulta
	NS.storeConsulta = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{concentradora : 0,
					divisa: '',
					banco: 0},
		paramOrder:['concentradora','divisa','banco'],
		directFn: CoinversionAction.llenarGridBarridoChequeras,
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'idDivisa'},
			{name: 'idBanco'},
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'saldoChequera'},
			{name: 'saldoFinal'},
			{name: 'saldoMinimo'},
			{name: 'diferencia'},
			{name: 'secuencia'},
			{name: 'fecValor'},
			{name: 'sobregiro'},
			{name: 'montoSobregiro'},
			{name: 'noLinea'},
			{name: 'saldoCredito'},
			{name: 'traspaso'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					BFwrk.Util.msgShow('No hay Chequeras Importadas para el Barrido', 'INFO');
					return;
				}
				else
				{
					var totalImporte = 0;
					for(var k=0;k<records.length;k=k+1)
          			{
          				totalImporte=totalImporte+records[k].get('diferencia');
          			}
					Ext.getCmp(PF+'txtTotalReg').setValue(records.length);
					Ext.getCmp(PF+'txtTotalBarrido').setValue(BFwrk.Util.formatNumber(Math.round((totalImporte)*100)/100));
				}
			}
		}
	}); 
	
	//grid Consulta
	NS.gridConsulta  = new Ext.grid.GridPanel({
		store : NS.storeConsulta,
		id: PF+'gridConsultaBarridoATM',
		name: PF+'gridConsultaBarridoATM',
		frame: true,
		width: 615,
       	height: 170,
		x :0,
		y :0,
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
			header :'Nombre Empresa', width :160, dataIndex :'nomEmpresa'
		},{
			header :'Chequera', width :90, dataIndex :'idChequera'
		},{
			header :'Banco', width :90, dataIndex :'descBanco'
		},{
			header :'Fecha Chequera', width :90, dataIndex :'fecValor'
		},{
			header :'Saldo Chequera Importado', width :135, dataIndex :'saldoChequera', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Diferencia', width :135, dataIndex :'diferencia', renderer: BFwrk.Util.rendererMoney, hidden: true
		},{
			header :'Saldo Minimo', width :100, dataIndex :'saldoMinimo', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Traspaso', width :100, dataIndex :'traspaso', renderer: BFwrk.Util.rendererMoney
		},{
			header :'id Banco', width :100, dataIndex :'idBanco', hidden: true
		},{
			header :'secuencia', width :100, dataIndex :'secuencia', hidden: true
		},{
			header :'sobregiro', width :100, dataIndex :'sobregiro', hidden: true
		},{
			header :'monto sobregiro', width :100, dataIndex :'montoSobregiro', renderer: BFwrk.Util.rendererMoney
		},{
			header :'Credito', width :100, dataIndex :'saldoCredito', renderer: BFwrk.Util.rendererMoney
		}]
		}),
		sm: NS.sm,
		listeners:{
			click:{
				fn:function(grid){
					var suma = 0;
          			var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
          			for(var k = 0; k < regSelec.length; k = k+1)
          			{
          				suma = suma + regSelec[k].get('diferencia');
          			}
          		 
          			Ext.getCmp(PF+'txtTotalRegSel').setValue(regSelec.length);
					Ext.getCmp(PF+'txtTotalBarridoSel').setValue(BFwrk.Util.formatNumber(Math.round((suma)*100)/100));  
				}
			}
		}
	});
	
	//funciones
	NS.accionarCmbEmpresa = function(comboValor){
		NS.storeBanco.baseParams.empresa = comboValor;
		NS.storeChequera.baseParams.empresa = comboValor;
		if(Ext.getCmp(PF+'txtEmpresa').getValue != '')
			NS.storeBanco.baseParams.concentradora = true;
		NS.storeConsulta.baseParams.concentradora = comboValor;
		NS.noConcentradora = comboValor;
		NS.nomEmpresa = BFwrk.Util.scanFieldsStore(NS.storeCmbEmpresa, 'id', comboValor, 'descripcion');
		
	}
	
	NS.accionarCmbDivisa = function(comboValor){
		NS.storeBanco.baseParams.divisa = comboValor;
		NS.storeBanco.load();
		NS.storeConsulta.baseParams.divisa = comboValor;
		NS.idDivisa = comboValor;
	}
	
	NS.accionarCmbBanco = function(comboValor){
		NS.storeChequera.baseParams.banco = comboValor;
	 	NS.storeChequera.load();
	 	NS.noBanco = comboValor;
	}
	
	NS.mostrarCheck = function(){
		GlobalAction.obtenerValorConfiguraSet(252, function(valor, e){
	              			   			
			if(valor!==null  &&  valor!= undefined)
			{
				if(valor === 'SI')
				{
					Ext.getCmp(PF+'chkBarrerSobregiro').show();
					Ext.getCmp(PF+'chkFondear').show();
					NS.mostrarFondeo = true;
				}
				else
				{
					Ext.getCmp(PF+'chkBarrerSobregiro').hide();
					Ext.getCmp(PF+'chkFondear').hide();
					NS.mostrarFondeo = false;
				}
			}
		});
	}
	NS.mostrarCheck();
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
	NS.bancosSeleccionados = function(){
		NS.psBancos = '';
	//	if(Ext.getCmp(PF+'chkBanamex').getValue() == true)
	//		NS.psBancos = NS.psBancos + 'BANAMEX' + ',';
			
		if(Ext.getCmp(PF+'chkBancomer').getValue() == true)
			NS.psBancos = NS.psBancos + 'BANCOMER' + ',';
			
	//	if(Ext.getCmp(PF+'chkCitibank').getValue() == true)
	//		NS.psBancos = NS.psBancos + 'CITIBANK' + ',';
			
	//	if(Ext.getCmp(PF+'chkComerica').getValue() == true)
	//		NS.psBancos = NS.psBancos + 'COMERICA' + ',';
			
		if(Ext.getCmp(PF+'chkSantander').getValue() == true)
			NS.psBancos = NS.psBancos + 'SANTANDER' + ',';
		
	//	if(Ext.getCmp(PF+'chkAzteca').getValue() == true)
   //		NS.psBancos = NS.psBancos + 'AZTECA' + ',';
			
		NS.psBancos = NS.psBancos.substring(0, NS.psBancos.length - 1);
		return NS.psBancos;
	}
	
	NS.checkboxes = new Ext.form.CheckboxGroup({ 
		id: PF+'checkboxes',
		name: PF+'checkboxes', 
		itemCls: 'x-check-group-alt',
		xtype: 'checkboxgroup',
	    columns:2,
	    x: 10,
	    y: 10,
	    items:[  
	    	//{boxLabel: 'Banamex', name: PF+'chkBanamex', id: PF+'chkBanamex'},
            {boxLabel: 'Bancomer', name: PF+'chkBancomer', id: PF+'chkBancomer'},
            {boxLabel: 'Santander', name: PF+'chkSantander', id: PF+'chkSantander'},
            //{boxLabel: 'Citibank', name: PF+'chkCitibank', id: PF+'chkCitibank'},
            //{boxLabel: 'Azteca', name: PF+'chkAzteca', id: PF+'chkAzteca'},
            //{boxLabel: 'Comerica', name: PF+'chkComerica', id: PF+'chkComerica'}
	    ]  
	}); 
	
	NS.ejecutarBarrido = function()
	{
		var registrosGrid=NS.gridConsulta.getSelectionModel().getSelections();
		var matrizGrid = new Array ();
		var matrizCriterios = new Array ();
		
		for(var i = 0; i < registrosGrid.length; i = i + 1)
		{
			var regGrid = {};
			
			regGrid.noEmpresa = registrosGrid[i].get('noEmpresa');
			regGrid.nomEmpresa = registrosGrid[i].get('nomEmpresa');
			regGrid.idDivisa = registrosGrid[i].get('idDivisa');
			regGrid.idBanco = registrosGrid[i].get('idBanco');
			regGrid.descBanco = registrosGrid[i].get('descBanco');
			regGrid.idChequera = registrosGrid[i].get('idChequera');
			regGrid.saldoChequera = registrosGrid[i].get('saldoChequera');
			regGrid.saldoFinal = registrosGrid[i].get('saldoFinal');
			regGrid.saldoMinimo = registrosGrid[i].get('saldoMinimo');
			regGrid.diferencia = registrosGrid[i].get('diferencia');
			regGrid.secuencia = registrosGrid[i].get('secuencia');
			regGrid.fecValor = registrosGrid[i].get('fecValor');
			regGrid.sobregiro = registrosGrid[i].get('sobregiro');
			regGrid.montoSobregiro = registrosGrid[i].get('montoSobregiro');
			regGrid.noLinea = registrosGrid[i].get('noLinea');
			regGrid.saldoCredito = registrosGrid[i].get('saldoCredito');
			regGrid.traspaso = registrosGrid[i].get('traspaso');
			regGrid.idSaldo = NS.idTipoSaldo;
			matrizGrid[i] = regGrid; 
		}
		
		var criterio = {};
		criterio.noEmpresa = NS.noConcentradora;
		criterio.noBanco = NS.noBanco;
		criterio.opcFondeo = Ext.getCmp(PF+'chkFondear').getValue();
		criterio.opcSobregiro = Ext.getCmp(PF+'chkBarrerSobregiro').getValue();
		criterio.idDivisa = NS.idDivisa;
		criterio.idChequera = NS.idChequera;
		criterio.nomEmpresa = NS.nomEmpresa;
		
		matrizCriterios[0] = criterio;
		
		var jsonStringR = Ext.util.JSON.encode(matrizGrid);	
		var jsonStringC = Ext.util.JSON.encode(matrizCriterios);	
		CoinversionAction.ejecutarBarrido(jsonStringR, jsonStringC, function(mapResult, e){
			if(mapResult.msgUsuario!==null  &&  mapResult.msgUsuario!=='' && mapResult.msgUsuario!= undefined)
			{
				for(var msg = 0; msg < mapResult.msgUsuario.length; msg++)
				{
					BFwrk.Util.msgShow(''+mapResult.msgUsuario[msg], 'INFO');
					//alert(''+mapResult.msgUsuario[msg]);
				}
			}
			NS.storeConsulta.removeAll();
   			NS.gridConsulta.getView().refresh();
   			NS.contenedorBarridoAtmSaldos.getForm().reset();
		});
	}
	
	NS.contenedorBarridoAtmSaldos = new Ext.FormPanel({
	    title: 'Barrido Automatico por Saldos',
	    width: 682,
	    height: 547,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                width: 660,

	                height: 550,
	                layout: 'absolute',
	                x: 10,
	                y: 0,
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: 'Concentradora',
	                        width: 638,

	                        height: 220,
	                        layout: 'absolute',
	                        x: 0,
	                        y: 0,
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Empresa:',
	                                x: 1,
	                                y: 7
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtEmpresa',
	                                name: PF+'txtEmpresa',
	                                x: 56,
	                                y: 4,
	                                width: 52,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
			                        			NS.accionarCmbEmpresa(comboValor);
			                        		
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbEmpresa,
	                            
	                            {
	                                xtype: 'label',
	                                text: 'Divisa:',
	                                x: 3,
	                                y: 42
	                            },
	                            {
	                                xtype: 'uppertextfield',
	                                id: PF+'txtDivisa',
	                                name: PF+'txtDivisa',
	                                x: 56,
	                                y: 38,
	                                width: 52,
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
	                                text: 'Banco:',
	                                x: 2,
	                                y: 74
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtBanco',
	                                name: PF+'txtBanco',
	                                x: 56,
	                                y: 73,
	                                width: 52,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var comboValor = BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId());
			                        			NS.accionarCmbBanco(comboValor);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbBanco,
	                           
	                            {
	                                xtype: 'label',
	                                text: 'Chequera:',
	                                x: 0,
	                                y: 111
	                            },
	                            NS.cmbChequera,
	                            {
	                                xtype: 'label',
	                                text: 'Tipo Saldo:',
	                                name: PF+'tiposaldo',
	                        		id: PF+'tiposaldo',
	                                x: 0,
	                                y: 150
	                            },
	                            NS.cmbTipoSaldo,
	                        
	                            
	                            {
	                                xtype: 'fieldset',
	                                title: 'Bancos a Importar',
	                                width: 224,
	                                height: 120,
	                                x: 392,
	                                y: -3,
	                                layout: 'absolute',
	                                items: [
	                                	NS.checkboxes
	                                ]
	                            },
	                            {
	                                xtype: 'button',
	                                text: 'Buscar',
	                                x: 537,

	                                y: 141,
	                                width: 70,
	                                listeners:{
				                  		click:{
				              			   	fn:function(e){
				              			   	
				              			   		Ext.getCmp(PF+'txtTotalReg').setValue(0);
				              			   		Ext.getCmp(PF+'txtTotalRegSel').setValue(0);
				              			   		Ext.getCmp(PF+'txtTotalBarrido').setValue(0);
				              			   		Ext.getCmp(PF+'txtTotalBarridoSel').setValue(0);
				              			   		
				              			   
				              			   		
				              			   		var checks = Ext.getCmp(PF+'checkboxes').getValue();
				              			   		if(checks == '')
				              			   		{
				              			   			BFwrk.Util.msgShow('Es necesario seleccionar algun banco', 'INFO');
				              			   			return;
			              			   			}
				              			   		
				              			   		if(Ext.getCmp(PF+'txtEmpresa').getValue() === '')
				              			   		{
				              			   			BFwrk.Util.msgShow('Debe eligir una empresa Concentradora', 'INFO');
				              			   			return;
				              			   		}
				              			   		
				              			   		if(Ext.getCmp(PF+'txtDivisa').getValue() === '')
				              			   		{
				              			   			BFwrk.Util.msgShow('Debe eligir una Divisa', 'INFO');
				              			   			return;
				              			   		}
				              			   		
				              			   		
				              			  	GlobalAction.obtenerValorConfiguraSet(1, function(valor, e){
	                            		    	if(valor!==null  &&  valor!= undefined)
	                            				{
	                            					if(valor === 'DALTON')
	                            					{	
	                            					  if(Ext.getCmp(PF+'cmbTipoSaldo').getValue()=='')
	                            						{
	                            						  BFwrk.Util.msgShow('Es necesario seleccionar el tipo de saldo', 'INFO');
	                            					        return;
	                            						}
	                            					  else
	                            						  {
	                            						  NS.bancosSeleccionados();
	      				              			   		
		      											    NS.storeConsulta.baseParams.banco = NS.psBancos;
		      											    NS.storeConsulta.load();
	                            						  }
	                            					}
	                            					else
	                            						{
	                            						NS.bancosSeleccionados();
	    				              			   		
	    											    NS.storeConsulta.baseParams.banco = NS.psBancos;
	    											    NS.storeConsulta.load();
	                            						}
	                         
	                            				}
	                            		    	else
	                            		    		{
	                            		    		NS.bancosSeleccionados();
					              			   		
												    NS.storeConsulta.baseParams.banco = NS.psBancos;
												    NS.storeConsulta.load();
	                            		    		
	                            		    		}
	                            				});
				              			   		
				              			   		
				              			   	}
		              				   	}
		              			   	}
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Empresa',
	                        x: 0,

	                        y: 230,
	                        height: 247,
	                        layout: 'absolute',
	                        items: [
	                        NS.gridConsulta,
	                            {
	                                xtype: 'label',
	                                text: 'Total Registros:',
	                                x: 0,
	                                y: 180,
	                                width: 68
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalReg',
	                                name: PF+'txtTotalReg',
	                                x: 60,
	                                y: 184,
	                                width: 50
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Registros Seleccionados:',
	                                x: 130,
	                                y: 179,
	                                width: 87
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalRegSel',
	                                name: PF+'txtTotalRegSel',
	                                x: 210,
	                                y: 182,
	                                width: 50
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Total Barrido:',
	                                x: 280,
	                                y: 179,
	                                width: 61
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Barridos Seleccionados:',
	                                x: 430,
	                                y: 177,
	                                width: 110
	                            },
	                            {
	                                xtype: 'textfield',
                                 	id: PF+'txtTotalBarrido',
	                                name: PF+'txtTotalBarrido',
	                                x: 325,
	                                y: 181,
	                                width: 100
	                            },
	                            {
	                                xtype: 'textfield',
	                                id: PF+'txtTotalBarridoSel',
	                                name: PF+'txtTotalBarridoSel',
	                                x: 515,
	                                y: 180,
	                                width: 100
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'checkbox',
	                        id: PF+'chkBarrerSobregiro',
	                        name: PF+'chkBarrerSobregiro',
	                        boxLabel: 'Barrer la linea de Sobregiro, en saldos de Chequera negativos',
	                        x: 3,
	                        y: 448,
	                        width: 180,
	                        hidden: true,
	                        value: false
	                    },
	                    {
	                        xtype: 'checkbox',
	                        id: PF+'chkFondear',
	                        name: PF+'chkFondear',
	                        boxLabel: 'Tambien Fondear',
	                        x: 217,
	                        y: 449,
	                        hidden: true,
	                        value: false
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Ejecutar',
	                        x: 397,

	                        y: 500,
	                        width: 70,
	                        listeners:{
				           		click:{
				           			fn:function(){
				           				var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
				           				var recordsGrid=NS.storeConsulta.data.items;
				           				
				           				if(Ext.getCmp(PF+'txtEmpresa').getValue() === '')
				           				{
				           					BFwrk.Util.msgShow('Falta la concentradora','INFO');
				           					return;
				           				}
				           				
				           				if(Ext.getCmp(PF+'txtBanco').getValue() === '')
				           				{
				           					BFwrk.Util.msgShow('Falta el banco','INFO');
				           					return;
				           				}
				           				
				           				if(Ext.getCmp(PF+'cmbChequera').getValue() === '')
				           				{
				           					BFwrk.Util.msgShow('Falta la chequera','INFO');
				           					return;
				           				}
				           				
				           				if(regSelec.length <= 0)
				           				{
				           					BFwrk.Util.msgShow('Es necesario seleccionar algun registro','INFO');
				           					return;
				           				}
				           				
										if(recordsGrid.length <= 0)
										{
									    	BFwrk.Util.msgShow('Faltan datos','INFO');
				           					return;
								    	}
								    	
								    	if(Ext.getCmp(PF+'chkFondear').getValue() == false && NS.mostrarFondeo == true)
								    	{
								    		Ext.Msg.confirm('Información SET',
								    		'Se le recuerda, que como no ha seleccionado ' +
								    		'la opcion de: Tambien Fondear, cualquier saldo negativo ' + 
								    		'que se encuentre en la lista, sera ignorado, y solo seran ' + 
								    		'barridos los saldos positivos, ¿Desea Continuar?',function(btn)
								    		{
								    			if(btn === 'no')
											        	return;
								    		});
								    	}
								    	
								    	NS.ejecutarBarrido();
			           				}
				           		}
				           	}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Importar',
	                        x: 477,

	                        y: 500,
	                        width: 70,
	                         listeners:{
				           		click:{
				           			fn:function(){
					        			var checks = Ext.getCmp(PF+'checkboxes').getValue();
		              			   		if(checks == '')
		              			   		{
		              			   			BFwrk.Util.msgShow('Es necesario seleccionar algun banco', 'INFO');
		              			   			return;
	              			   			}
	              			   			
	              			   			CoinversionAction.importarBancos(NS.bancosSeleccionados(), function(mapResult, e){
	              			   			
											if(mapResult.msgUsuario!==null  &&  mapResult.msgUsuario!=='' && mapResult.msgUsuario!= undefined)
											{
												for(var msg = 0; msg < mapResult.msgUsuario.length; msg++)
												{
													BFwrk.Util.msgShow(''+mapResult.msgUsuario[msg], 'INFO');
													//alert(''+mapResult.msgUsuario[msg]);
												}
											}
										});
			           				}
				           		}
				           	}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 559,

	                        y: 500,
	                        width: 70,
	                        listeners:{
				           		click:{
				           			fn:function(){
				           				var checks = Ext.getCmp(PF+'checkboxes').getValue();
					        			for(var i=0; i<checks.length; i=i+1){
					        				Ext.getCmp(checks[i].getName()).setValue(false);
					        			}
					        			NS.contenedorBarridoAtmSaldos.getForm().reset();
					        			NS.storeConsulta.removeAll();
		              			   		NS.gridConsulta.getView().refresh();
			           				}
				           		}
				           	}
	                    }
	                ]
	            }
	        ]
	});
	NS.contenedorBarridoAtmSaldos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	staticCheck("div[id*='gridConsulta'] div div[class='x-panel-ml']","div[id*='gridConsulta'] div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
});
