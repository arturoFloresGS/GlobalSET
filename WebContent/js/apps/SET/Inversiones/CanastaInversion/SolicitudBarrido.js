/* 
 * @author: Victor H. Tello
 * @since: 27/Octubre/2014
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Inversiones.CanastaInversion.SolicitudBarrido');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	NS.tasaNula = false;
	
	var textFieldInv = new Ext.form.TextField();
	var textFieldDias = new Ext.form.TextField();
	var textInteres = new Ext.form.TextField();
	var textISR = new Ext.form.TextField();
	
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
	
	//Etiqueta empresa
	NS.labEmpresa = new Ext.form.Label({
		text: 'Empresa',
        x: 10
	});
    
	//Store empresas
    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: BarridoInversionAction.empresasConcentradoras, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen empresas concentradoras');
			}
		}
	}); 
	NS.storeEmpresas.load();
	
	//Combo empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 10,
        y: 15,
        width: 300,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		visible: false
	});
	
	//Etiqueta divisa
	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa',
		x: 320
	});
	
	//store combo divisa
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
		},
		root: '',
		paramOrder:[],
		directFn: TraspasosAction.llenarComboDivisa,
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
			
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('SET','No hay divisas disponibles');
			}
		}
	});
	NS.storeDivisa.load();
	
	//Combo Divisas (obligatorio)
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa,
		name: PF+'cmbDivisa',
		id: PF+'cmbDivisa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 320,
        y: 15,
        width: 150,
		valueField:'idDivisa',
		displayField:'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccione la divisa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.llenaComboBanco(combo.getValue());
				}
			}
		}
	});
	
	//Refresca el combo bancos en base a la divisa
	NS.llenaComboBanco = function(valueCombo){
		NS.cmbBanco.reset();
		NS.storeBancos.removeAll();
		
		if(valueCombo != null && valueCombo != undefined && valueCombo != '')
			NS.storeBancos.baseParams.idDivisa = valueCombo;
		else
			NS.storeBancos.baseParams.idDivisa = '';
		NS.storeBancos.load();
	};
	
	//Etiqueta Banco
	NS.labBanco = new Ext.form.Label({
		text: 'Banco',
		x: 480
	});
	
	//Store banco
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{idDivisa: 0},
		paramOrder:['idDivisa'],
		directFn: BarridoInversionAction.todosLosBancos, 
		idProperty: 'noBanco', 
		fields: [
			 {name: 'noBanco'},
			 {name: 'nomBanco'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No Existen bancos con la divisa seleccionada');
			}
		}
	});
	NS.storeBancos.load();
	
	//Combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancos,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 480,
	    y: 15,
	    width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'noBanco',
		displayField:'nomBanco',
		autocomplete: true,
		emptyText: 'Seleccione banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 3
	});
	
	//Contenedor de Busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: 'búsqueda',
		width: 995,
	    height: 80,
	    layout: 'absolute',
	    items:[
	        NS.labEmpresa,
	        NS.cmbEmpresas,
	        NS.labBanco,
            NS.cmbBanco,
            NS.labDivisa,
            NS.cmbDivisa,
        	{
	        	xtype: 'button',
	        	text: 'Buscar',
	        	x: 880,
	        	y: 15,
	        	width: 80,
	        	height: 22,
	        	listeners: {
	        		click:{
	        			fn:function(e){
	        				NS.buscar();
	        			}
	        		}
	        	}
	        }
	    ]
	});
	//Store Datos donde se muestran los valores seleccionados
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['idDivisa', 'idBanco'],
		directFn: BarridoInversionAction.obtenerRegistros,
		idProperty: 'idChequera',
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'noBanco'},
			{name: 'nomBanco'},
			{name: 'idChequera'},
			{name: 'descChequera'},
			{name: 'idDivisa'},
			{name: 'saldoFinal'},
			{name: 'saldoTransito'},
			{name: 'saldoBancario'},
			{name: 'saldoInvertir'},
			{name: 'dias'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
				var sdoFin = 0;
				var sdoTra = 0;
				var sdoBan = 0;
				
				if(records.length == null || records.length <=0)
					Ext.Msg.alert('SET', 'No existe información con los parametros seleccionados');
				else {
					for(var x=0; x<records.length; x++) {
						sdoFin += parseFloat(records[x].get('saldoFinal'));
						sdoTra += parseFloat(records[x].get('saldoTransito'));
						sdoBan += parseFloat(records[x].get('saldoBancario'));
					}
					Ext.getCmp(PF + 'txtSaldoFin').setValue(NS.formatNumber(sdoFin));
					Ext.getCmp(PF + 'txtSaldoTransito').setValue(NS.formatNumber(sdoTra));
					Ext.getCmp(PF + 'txtSaldoBancario').setValue(NS.formatNumber(sdoBan));
				}
			}
		}
	});
	
	//Para indicar el modo de seleccion de los registros en el Grid 
	NS.tipoSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columnaSelec = new Ext.grid.ColumnModel([
	    //NS.tipoSeleccion,
	    {header: 'No. Empresa',width: 50,dataIndex: 'noEmpresa', sortable: true, hidden: true},
	    {header: 'Empresa',width: 200,dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'No. Banco', width: 50, dataIndex: 'noBanco', sortable: true, hidden: true},
	    {header: 'Banco', width: 120, dataIndex: 'nomBanco', sortable: true},
	    {header: 'Descripción', width: 125, dataIndex: 'descChequera', sortable: true, hidden: true},
	    {header: 'Chequera', width: 90, dataIndex: 'idChequera', sortable: true},
	    {header: 'Divisa', width: 45, dataIndex: 'idDivisa', sortable: true},
	    {header: 'Saldo total', width: 100, dataIndex: 'saldoBancario', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Saldo Tránsito', width: 100, dataIndex: 'saldoTransito', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Saldo disponible', width: 100, dataIndex: 'saldoFinal', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Monto a invertir', width: 100, editor:textFieldInv, dataIndex: 'saldoInvertir', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Dias', width: 40, editor:textFieldDias, dataIndex: 'dias', sortable: true, css: 'text-align:right;'},
	    {header: 'Tasa est.', width: 40, dataIndex: 'tasa', sortable: true, css: 'text-align:right;'},
	    {header: 'Interes est.', width: 100, dataIndex: 'interes', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney,
	    	listeners:{
        	click:{
        		fn:function(e){
    				NS.calculaInt();
	    		}
        	}
        }},
	    {header: 'ISR est.', width: 80, dataIndex: 'isr', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Ganancia est.', width: 80, dataIndex: 'ganancia', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney}
	]);
	
	NS.storeTasaComision = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: BarridoInversionAction.obtenerTasaComision,
		idProperty: 'dias',
		fields: [
			{name: 'dias'},
			{name: 'tasa'},
			{name: 'comision'}
		]
	});
	NS.storeTasaComision.load();
	
	NS.calculaInt = function() {
		var selec = NS.gridDatos.getSelectionModel().getSelections();
		var records = NS.storeDatos.data.items;
		var datosClase = NS.gridDatos.getStore().recordType;
		var dias = parseInt(selec[0].get('dias'));
		var tasa = 0;
		
		if(NS.tasaNula) {
			NS.tasaNula = false;
			NS.storeTasaComision.load();
		}
		var recTasas = NS.storeTasaComision.data.items;
		
		for(var i=0; i<recTasas.length; i++) {
			if(parseInt(recTasas[i].get('dias')) == dias)
				tasa = recTasas[i].get('tasa');
		}
		if(tasa == 0) {
			NS.tasaNula = true;
			Ext.Msg.alert('SET', 'Ingrese primero la tasa para los dias deseados, esto para realizar los calculos');
			return;
		}
		var varInteres = (((parseFloat(selec[0].get('saldoInvertir')) / 360) * (tasa/100)) * dias);
		var varISR = (parseFloat(selec[0].get('saldoInvertir')) * ((((.60/365) * 360) / 100) / 360) * dias);
		
		var datos = new datosClase({
			noEmpresa: selec[0].get('noEmpresa'),
			nomEmpresa: selec[0].get('nomEmpresa'),
			noBanco: selec[0].get('noBanco'),
			nomBanco: selec[0].get('nomBanco'),
			idChequera: selec[0].get('idChequera'),
			descChequera: selec[0].get('descChequera'),
			idDivisa: selec[0].get('idDivisa'),
			saldoFinal: selec[0].get('saldoFinal'),
			saldoTransito: selec[0].get('saldoTransito'),
			saldoBancario: selec[0].get('saldoBancario'),
			saldoInvertir: selec[0].get('saldoInvertir'),
			dias: selec[0].get('dias'),
			tasa: tasa,
			interes: varInteres,
			isr: varISR,
			ganancia: varInteres - varISR
      	});
		NS.gridDatos.store.remove(selec[0]);
		
		NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(selec[0], datos);
  		NS.gridDatos.getView().refresh();
	};
	
	//Grid para mostrar los datos seleccionados
	NS.gridDatos = new Ext.grid.EditorGridPanel({
		store: NS.storeDatos,
		id: 'gridDatos',
		cm: NS.columnaSelec,
		sm: NS.tipoSeleccion,
		width: 970,
	    height: 265,
	    stripeRows: true,
	    columnLines: true,
		listeners: {
			click: {
				fn:function(grid) {
				}
			}
		}
	});
	
	NS.labSaldoFin = new Ext.form.Label({
		text: 'Saldo Final',
        x: 10,
        y: 275
	});
    
	NS.txtSaldoFin = new Ext.form.TextField({
		id: PF + 'txtSaldoFin',
		x: 10,
        y: 290,
        width: 100,
        disabled: true
    });
	
	NS.labSaldoTransito = new Ext.form.Label({
		text: 'Saldo Transito',
        x: 120,
        y: 275
	});
    
	NS.txtSaldoTransito = new Ext.form.TextField({
		id: PF + 'txtSaldoTransito',
		x: 120,
        y: 290,
        width: 100,
        disabled: true
    });
	NS.labSaldoBancario = new Ext.form.Label({
		text: 'Saldo Bancario',
        x: 230,
        y: 275
	});
    
	NS.txtSaldoBancario = new Ext.form.TextField({
		id: PF + 'txtSaldoBancario',
		x: 230,
        y: 290,
        width: 100,
        disabled: true
    });
	NS.labMontoInvetir = new Ext.form.Label({
		text: 'Monto Invetir',
        x: 340,
        y: 275,
        hidden: true
	});
    
	NS.txtMontoInvetir = new Ext.form.TextField({
		id: PF + 'txtMontoInvetir',
		x: 340,
        y: 290,
        width: 100,
        disabled: true,
        hidden: true
    });
	//Contenedor de Transferencia o Factoraje
	NS.contCuentas = new Ext.form.FieldSet({
		title: '',
        y: 90,
        width: 995,
        height: 335,
        layout: 'absolute',
        items: [
               NS.gridDatos,
               NS.labSaldoFin, 		NS.txtSaldoFin,
               NS.labSaldoTransito, NS.txtSaldoTransito,
               NS.labSaldoBancario, NS.txtSaldoBancario,
               NS.labMontoInvetir,	NS.txtMontoInvetir,
               {
            	   xtype: 'button',
            	   text: 'Ejecutar',
                   x: 790,
                   y: 290,
                   width: 80,
                   height: 22,
                   listeners: {
                   		click: {
            	   			fn:function(e) {
            	   				NS.ejecutar();
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Imprimir',
            	   x: 880,
            	   y: 290,
            	   width: 80,
            	   height: 22,
            	   
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				
            	   			}
               			}
               		}
               }
        ]
    });
	//Contenedor General
	NS.contGeneral = new Ext.form.FieldSet({
		title: '',
		width: 1020,
	    height: 450,
	    x: 20,
	    y: 5,
	    layout: 'absolute',
	    items:[
			NS.contBusqueda,
			NS.contCuentas
		]
	});
	
	NS.buscar = function() {
		if(NS.cmbEmpresas.getValue() == '') {
			Ext.Msg.alert('SET', 'Seleccione una empresa');
			return;
		}
		if(NS.cmbDivisa.getValue() == '') {
			Ext.Msg.alert('SET', 'Seleccione una divisa');
			return;
		}
		NS.storeDatos.baseParams.idDivisa = NS.cmbDivisa.getValue();
		NS.storeDatos.baseParams.idBanco = '' + NS.cmbBanco.getValue();
		NS.storeDatos.load();
	};
	
	NS.ejecutar = function() {
		var selDatos = NS.storeDatos.data.items;
		var matDatos  = new Array();
		var cadenaJson = '';
		
		if(selDatos.length == 0) {
			Ext.Msg.alert('SET', 'Ingrese el importe a invertir en al menos un registro');
			return;
		}
		
		for(var i=0; i<selDatos.length; i++) {
			if(selDatos[i].get('saldoInvertir') != '' && selDatos[i].get('dias') != '' && selDatos[i].get('tasa') != '') {
				var vec = {};
				vec.idChequera = selDatos[i].get('idChequera');
				vec.saldoInvertir = selDatos[i].get('saldoInvertir');
				vec.noDias = selDatos[i].get('dias');
				vec.noEmpresaCon = NS.cmbEmpresas.getValue();
				matDatos[i] = vec;
			}
		}
		cadenaJson = Ext.util.JSON.encode(matDatos);
		
		BarridoInversionAction.insertarBarridos(cadenaJson, function(res, e){
			Ext.Msg.alert('SET', res);
			NS.limpiar();
		});
	};
	
	//Funcion para limpiar los campos
	NS.limpiar = function(){
		NS.cmbBanco.reset();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
	};
	
	//Contenedor principal del formulario
	NS.SolicitudBarrido = new Ext.FormPanel({
		title: 'Solicitud barrido de inversiones',
	    width: 1020,
	    height: 500,
	    frame: true,
	    padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF + 'solicitudBarrido',
	    renderTo: NS.tabContId,
	    items:[
	           NS.contGeneral
	    ]
	});
	NS.SolicitudBarrido.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});