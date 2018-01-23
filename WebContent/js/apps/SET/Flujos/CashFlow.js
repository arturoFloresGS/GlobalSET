/*
 * @author: Victor H. Tello
 * @since: 19/Nov/2013
 */
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Flujos.CashFlow');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.idRubro = '';
	
	NS.iSelec = 0;
	
	NS.fields = [];
	NS.columns = [];
	NS.numAdd=0;
	NS.newRecord=null;
	NS.storeLlenaGridCash = new Ext.data.DirectStore({autoLoad : false});
	NS.bandera = false;
	NS.varChkSaldos = "false";
	NS.fieldName = '';
	
	function cambiarFecha(fecha) {
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		
		for(var i=0;i<12;i=i+1) {
			if(mesArreglo[i]===mesDate) {
			var mes=i+1;
				if(mes<10)
				mes = '0' + mes;
			}
		}
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	}
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
	//Quitar formato a las cantidades
	NS.unformatNumber = function(num) {
		return num.replace(/(,)/g,''); 
	};
	
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa:',
		x: 10
	});
	
	NS.txtEmpresa = new Ext.form.TextField({
		id: PF + 'txtEmpresa',
        name:PF + 'txtEmpresa',
		x: 10,
        y: 15,
        width: 50,
        tabIndex: 0,
        listeners:{
			change:{
				fn: function(caja,valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
					else
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
                }
			}
    	}
    });
	
	NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idUsuario: NS.idUsuario},
		paramOrder: ['idUsuario'],
		directFn: ConfirmacionTransferenciasAction.obtenerEmpresas, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene empresas asignadas');
				}
				//Se agrega la opcion todas las empresas
	 			var recordsStoreEmpresas = NS.storeEmpresas.recordType;	
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '***************TODAS***************'
		       	});
		   		NS.storeEmpresas.insert(0,todas);
			}
		}
	}); 
	NS.storeEmpresas.load();
	
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 65,
        y: 15,
        width: 320,
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
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
				}
			}
		}
	});
	
    NS.lblFechaIni = new Ext.form.Label({
        text: 'Fecha Inicial',
        x: 395
    });
    
    NS.txtFechaIni = new Ext.form.DateField({
		id: PF + 'txtFechaIni',
		name: PF + 'txtFechaIni',
		x: 395,
		y: 15,
		width: 105,
		format: 'd/m/Y',
		value : apps.SET.FEC_HOY,
		hidden: false
	});
    
    NS.dataMes = [	[ '1', 'ENERO' ],
                          [ '2', 'FEBRERO' ],
                          [ '3', 'MARZO' ],
                          [ '4', 'ABRIL' ],
                          [ '5', 'MAYO' ],
                          [ '6', 'JUNIO' ],
                          [ '7', 'JULIO' ],
                          [ '8', 'AGOSTO' ],
                          [ '9', 'SEPTIEMBRE' ],
                          [ '10', 'OCTUBRE' ],
                          [ '11', 'NOVIEMBRE' ],
                          [ '12', 'DICIEMBRE' ]
					  ];
    
	NS.storeMeses = new Ext.data.SimpleStore( {
			idProperty: 'idMes',  		
			fields : [ 
						{name :'idMes'}, 
						{name :'descMes'}
					 ]
	});
	NS.storeMeses.loadData(NS.dataMes);
	
	NS.cmbMesIni = new Ext.form.ComboBox({
		store: NS.storeMeses,
		id: PF + 'cmbMesesIni',
		name: PF + 'cmbMesesIni',
		x: 395,
        y: 15,
        width: 105,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idMes',
		displayField: 'descMes',
		autocomplete: true,
		emptyText: 'Mes inicial',
		triggerAction: 'all',
		value: '',
		hidden: true
	});
    
    NS.lblFechaFin = new Ext.form.Label({             
        text: 'Fecha Final:',
        x: 510
    });
    
    NS.txtFechaFin = new Ext.form.DateField({
		id: PF + 'txtFechaFin',
		name: PF + 'txtFechaFin',
		x: 510,
		y: 15,
		width: 105,
		format: 'd/m/Y',
		value: apps.SET.FEC_HOY,
		hidden: false
	});
    
    NS.cmbMesFin = new Ext.form.ComboBox({
		store: NS.storeMeses,
		id: PF + 'cmbMesesFin',
		name: PF + 'cmbMesesFin',
		x: 510,
        y: 15,
        width: 105,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idMes',
		displayField: 'descMes',
		autocomplete: true,
		emptyText: 'Mes final',
		triggerAction: 'all',
		value: '',
		hidden: true
	});
    
    NS.lblReporte = new Ext.form.Label({                
        text: 'Tipo de Reporte',
        x: 625,
        hidden: true
    });
    
    NS.storeReportesFlujo = new Ext.data.DirectStore({
		paramsAsHash: false,		
		directFn: CashFlowActionImplements.getReportesFlujoService,
		root: '', 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		]		
	}); 
	
	NS.storeReportesFlujo.load();
   
	NS.cmbReporte = new Ext.form.ComboBox({   
	    store: NS.storeReportesFlujo,
	    id: PF + 'cmbReporte',
		mode: 'local',	   
		valueField:'id',
		displayField:'descripcion',
		emptyText: 'Seleccione un reporte',
		triggerAction: 'all',   
	    x: 625,
	    y: 15,
	    width: 250,
	    hidden: true
	});
    
	NS.lblDivisa = new Ext.form.Label({                
        text: 'Divisa',
        x: 625,
        hidden: true
    });
    
    NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,		
		directFn: PosicionBancariaAction.buscaDivisas,
		root: '', 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
		]		
	}); 
	
	NS.storeDivisa.load();
   
	NS.cmbDivisa = new Ext.form.ComboBox({   
	    store: NS.storeDivisa,
	    id: PF + 'cmbDivsa',
		mode: 'local',	   
		valueField:'idDivisa',
		displayField:'descDivisa',
		emptyText: 'Divisa',
		triggerAction: 'all',
	    x: 625,
	    y: 15,
	    width: 120
	});
	
	NS.tipoReporte = [
	                  	[ '0', 'DIARIO' ],
	                  	[ '1', 'SEMANAL' ],
	                  	[ '2', 'MENSUAL' ]
	                  	/*
	                  	[ '3', 'FICHA VALOR GLOBAL' ],
	                  	[ '4', 'MOVIMIENTOS TESORERIA' ],
	                  	[ '5', 'SALDOS POR MONTO' ],
	                  	[ '6', 'REPORTE DE SALDOS' ]
	                  	*/
					  ];
	
	NS.storeTipoReporte = new Ext.data.SimpleStore({
			idProperty: 'idTipoReporte',  		
			fields : [ 
						{name :'idTipoReporte'}, 
						{name :'descripcion'}
					 ]
	});
	NS.storeTipoReporte.loadData(NS.tipoReporte);
	
	NS.labReportes = new Ext.form.Label({
		text: 'Tipo de Reporte',
		x: 755
	});
	
	NS.cmbTipoReporte = new Ext.form.ComboBox({
		store: NS.storeTipoReporte,
		name: PF + 'cmbTipoReporte',
		id: PF + 'cmbTipoReporte',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		x: 755,
	    y: 15,
	    width: 120,
		valueField: 'idTipoReporte',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Tipo de reportes',
		triggerAction: 'all'
	});
	
	//Contenedor de todos los campos de la parte superior de busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: '',
		width: 1030,
		height: 70,
		x: 10,
		layout: 'absolute',
		items: [
		        NS.lblEmpresa,
		        NS.txtEmpresa,
		        NS.cmbEmpresas,
		        NS.lblFechaIni,
		        NS.txtFechaIni,
		        NS.cmbMesIni,
		        NS.lblFechaFin,
		        NS.txtFechaFin,
		        NS.cmbMesFin,
		        NS.lblReporte,
		        NS.cmbReporte,
		        NS.lblDivisa,
		        NS.cmbDivisa,
		        NS.labReportes,
		        NS.cmbTipoReporte,
		        {
				    xtype: 'button',
				    text: 'Buscar',
				    x: 925,
				    width: 80,
				    height: 22,
				    listeners: {
						click: {
							fn: function(e) {
		        				if(NS.cmbDivisa.getValue() == '') {
		        					Ext.Msg.alert('SET', 'Seleccione una divisa para continuar!!');
		        					return;
		        				}
								NS.buscarReporte();
							}
						}
					}
				},{
				    xtype: 'button',
				    text: 'Excel',
				    x: 925,
				    y: 25,
				    width: 80,
				    height: 22,
				    listeners: {
						click: {
							fn: function(e) {
								NS.generarExcel();
							}
						}
					}
				}
		        ]
	});
	
	NS.storedHeadsCash = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		paramOrder: ['noEmpresa', 'fecIni', 'fecFin', 'tipoReporte'],
		directFn: CashFlowNAction.obtenHeadsCash,
		idProperty: 'tipoPosicion',
		fields:[
		        'nomReporte',
		        'tipoReporte',
		        'columnas',
		        'fields'
		],
		listeners : {
			load:function(s,records){
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos');
				
//				NS.fields = Ext.util.JSON.decode(NS.storedHeadsCash.getById(parseInt(NS.cmbReporte.getValue())).get('fields'));
				NS.fields = Ext.util.JSON.decode(NS.storedHeadsCash.getById(parseInt(NS.cmbTipoReporte.getValue())).get('fields'));
				
				NS.storeLlenaGridCash = new Ext.data.DirectStore({
					paramsAsHash: false,
					root: "row",
					paramOrder: ['parametros'],
					directFn: CashFlowNAction.cashFlowDatos,
					fields: NS.fields,
					listeners: {
						load: function(s, records) {
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridCash, msg: "Cargando información..."});
							
							if(records.length == null || records.length <= 0)
								Ext.Msg.alert('SET', 'No existe información con estos parametros');
							
						},
						colores: function (value, meta, record) {
				    		meta.attr = 'style= "#2E64FE"';
				    		return value;
				    	}
					}
				});
				
				var parametros = NS.obtieneParametros();
				
				NS.storeLlenaGridCash.baseParams.parametros = parametros;
				NS.storeLlenaGridCash.load();
				
//				NS.columns = new Ext.grid.ColumnModel(Ext.util.JSON.decode(NS.storedHeadsCash.getById(parseInt(NS.cmbReporte.getValue())).get('columnas')));
				NS.columns = new Ext.grid.ColumnModel(Ext.util.JSON.decode(NS.storedHeadsCash.getById(parseInt(NS.cmbTipoReporte.getValue())).get('columnas')));
				var recordsGridDatos = NS.storedHeadsCash.data.items;
				
				NS.gridCash.reconfigure(NS.storeLlenaGridCash, NS.columns);
				NS.gridCash.setTitle(recordsGridDatos[0].get('nomReporte'));
			}
		}
	});
	
	NS.selCheck = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.gridCash = new Ext.grid.GridPanel({
		store : NS.storeLlenaGridCash,
		id: PF + 'gridCash',
		name: PF + 'gridCash',
		selModel: NS.selCheck,
		columns : [],
		sm: new Ext.grid.CheckboxSelectionModel({singleSelect: false}),
		header:true,
		stripeRows :true,
		columnsLine: true,
		height: 390,
		width: 1030,
		x: 10,
		y: 80,
		viewConfig: {
	        getRowClass: function(record, index){ 
		        if(record.get('descripcion') == 'SALDO INICIAL' || record.get('descripcion') == 'SALDO TOTAL')
		        	return 'row-font-color-blue';
		        if(record.get('descripcion') == 'TOTAL INGRESOS' || record.get('descripcion') == 'TOTAL EGRESOS')
		        	return 'row-font-color-red';
           	}
        },
        colores: function (value, meta, record) {
    		meta.attr = 'style= "#2E64FE"';
    		return value;
    	},
    	listeners:{
    		cellclick: function(iView, iCellEl, iColIdx, iRecord, iRowEl, iRowIdx, iEvent) {
    			Ext.Msg.confirm('SET', '¿Desea visualizar el detalle?', function(btn) {
					if(btn == 'yes') {
						var regSelec = NS.gridCash.getSelectionModel().getSelections();
						NS.fieldName = NS.gridCash.getColumnModel().getDataIndex(iColIdx);
						
						if(regSelec.length == 0 || regSelec[0].get('idRubro') == null || regSelec[0].get('idRubro') == '') {
							Ext.Msg.alert('SET', 'Seleccione un rubro valido!!');
							return;
						}
						NS.llenaGridDetalle(regSelec);
						
		        		winDetalle.show();
		        		
		        		NS.storeGrupoRubro.removeAll();
						NS.storeRubro.removeAll();
						NS.cmbGrupoRubro.reset();
						NS.cmbRubro.reset();
						
					}
				});
    		}
    	}
	});
	
	NS.llenaGridDetalle = function(regSelec) {
		var parametros = NS.parametrosReporte();
		NS.idRubro = regSelec[0].get('idRubro');
		NS.storeDetalleFlujo.baseParams.parametros = parametros;
		NS.storeDetalleFlujo.baseParams.idRubro = NS.idRubro;
		NS.storeDetalleFlujo.load();
	};
	
	//******************************************************** Este codigo es para el detalle del Flujo por concepto *****************************************//
	NS.storeDetalleFlujo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['parametros', 'idRubro'],
		directFn: CashFlowNAction.consultarDetalleFlujo, 
		idProperty: 'noFolioDet',
		fields: [
				{name: 'nomEmpresa'},
				{name: 'concepto'},
				{name: 'importe'},
				{name: 'fecValor'},
				{name: 'descRubro'},
				{name: 'idTipoMovto'},
				{name: 'noFolioDet'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDetalleFlujo, msg: "Buscando..."});
				
				if(records.length == null || records.length <= 0) {
					Ext.Msg.alert('SET', 'No existe detalle alguno!!');
					return;
				}
				var ini = 0;
				
				for(var k=0; k<records.length; k++) ini += records[k].get('importe');
				
				NS.txtTotal.setValue(NS.formatNumber(ini));
				
			}
		}
	});
	
	//Columna de Seleccion  
	NS.columDeta = new Ext.grid.ColumnModel([
	    {header :'Empresa', width :200, align: 'left', css: 'text-align:left;', sortable :true, dataIndex :'nomEmpresa'},
	    {header :'Concepto', width :150, align: 'left', css: 'text-align:left;', sortable :true, dataIndex :'concepto'},
	    {header :'Importe', width :100, align: 'left', css: 'text-align:right;', sortable :true, dataIndex :'importe', renderer: BFwrk.Util.rendererMoney},
	    {header :'Fecha', width :100, align: 'left', css: 'text-align:left;', sortable :true, dataIndex :'fecValor'},
	    {header :'Rubro', width :105, align: 'left', css: 'text-align:left;', sortable :true, dataIndex :'descRubro'},
	    {header :'I/E', width :105, align: 'left', css: 'text-align:left;', sortable :true, dataIndex :'idTipoMovto', hidden: true},
	    {header :'Folio Set', width :100, align: 'left', css: 'text-align:right;', sortable :true, dataIndex :'noFolioDet', hidden: true}
	]);
	
	//Grid para mostrar los datos seleccionados
	NS.gridDetaCash = new Ext.grid.GridPanel({
		store: NS.storeDetalleFlujo,
		id: 'gridDetaCash',
		x: 5,
		y: 5,
		cm: NS.columDeta,
		width: 730,
	    height: 310,
	    stripeRows: true,
	    columnLines: true,
	    title: '',
	    listeners: {
			click: {
				fn: function(e) {
					var regSelec = NS.gridDetaCash.getSelectionModel().getSelections();
					
					if(regSelec.length > 0) {
						Ext.getCmp(NS.cmbGrupoRubro.getId()).setValue('');
						
						NS.storeGrupoRubro.removeAll();
						NS.storeRubro.removeAll();
						NS.cmbGrupoRubro.reset();
						NS.cmbRubro.reset();
						NS.storeGrupoRubro.baseParams.idTipoMovto = regSelec[0].get('idTipoMovto');
						NS.storeGrupoRubro.load();
					}
				}
			}
		}
	});
	
	NS.lblTotal = new Ext.form.Label({
		text: 'Monto Total',
		x: 10,
		y: 320
	});
	
	NS.txtTotal = new Ext.form.TextField({
		id: PF + 'txtTotal',
        name:PF + 'txtTotal',
		x: 10,
        y: 335,
        width: 100,
        disabled: true
    });
	
	NS.lblInteres = new Ext.form.Label({
		text: 'Interés Fideicomiso',
		x: 10,
		y: 360
	});
	
	NS.txtInteres = new Ext.form.TextField({
		id: PF + 'txtInteres',
        name:PF + 'txtInteres',
		x: 10,
        y: 375,
        width: 100,
        value: 0,
        disabled: false
    });
	
	NS.labGrupoRubro = new Ext.form.Label({
		text: 'Grupo Rubro',
		x: 120,
		y: 320
	});
	
	NS.labRubro = new Ext.form.Label({
		text: 'Rubro',
		x: 330,
		y: 320
	});
	
	NS.storeGrupoRubro = new Ext.data.DirectStore( {
		paramOrder:['idTipoMovto'],
		paramsAsHash: false,
		baseParams:{idTipoMovto: ''},
		root: '',
		directFn: CashFlowNAction.comboGrupoRubro,
		idProperty: 'idGrupo',
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoRubro, msg: "Cargando..."});
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'Error al cargar los grupos');
			}
		}
	});
 	
 	NS.storeRubro = new Ext.data.DirectStore( {	
 		root: '',
 		paramOrder:['idGrupo'],
		paramsAsHash: false,
		baseParams:{idGrupo:0},
		directFn: CashFlowNAction.comboRubro,
		idProperty: 'idRubro',  		
		fields: [
			{name: 'idRubro' },
			{name: 'descRubro'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubro, msg: "Cargando..."});
				
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'Error al cargar los rubros');
			}
		}			
 	});
 	
	NS.cmbGrupoRubro = new Ext.form.ComboBox({
		store: NS.storeGrupoRubro,
		id: PF + 'cmbGrupoRubro',
		name: PF + 'cmbGrupoRubro',
		x: 120,
		y: 335,		
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Grupo rubro',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					Ext.getCmp(NS.cmbRubro.getId()).setValue('');
					
					NS.storeRubro.removeAll();
					NS.storeRubro.baseParams.idGrupo = parseInt(combo.getValue());
					NS.storeRubro.load();
				}
			}
		}
	});
	
	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeRubro,
		id: PF + 'cmbRubro',
		name: PF + 'cmbRubro',
		x: 330,
		y: 335,
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Rubro',
		triggerAction: 'all',
		value: ''
	});
	
	//Contenedor de detalle de las propuestas
	NS.contDetalle = new Ext.form.FieldSet({
		title: 'Detalle de movimientos',
		layout: 'absolute',
	    items: [
	           NS.gridDetaCash,
	           NS.lblTotal,
	           NS.txtTotal,
	           NS.lblInteres,
	           NS.txtInteres,
	           NS.labGrupoRubro,
	           NS.labRubro,
	           NS.cmbGrupoRubro,
	           NS.cmbRubro,
	           {
                    xtype: 'button',
                    text: 'Aceptar',
                    x: 560,
                    y: 330,
                    width: 80,
                    id: PF + 'btnAceptar',
                    listeners: {
	              	 	click: {
	                   		fn:function(e) {
		                   		NS.reclasificaMovtos();
	          				}
		                }
                   	}
		        },{
                    xtype: 'button',
                    text: 'Imprimir',
                    x: 650,
                    y: 330,
                    width: 80,
                    id: PF + 'btnImprimir',
                    listeners: {
			         	click: {
		               		fn:function(e) {
		                   		NS.imprimirReporte();
		          			}
		        		}
		        	}
		        },
	            {
	        	    xtype: 'button',
	        	   	text: 'Cerrar',
	        	   	x: 650,
					y: 360,
					width: 80,
					height: 22,
					listeners: {
						click:{
							fn:function(e){
		        				NS.buscarReporte();
								winDetalle.hide();
							}
						}
					}
	           }
	           ]
	});
	//ventana para el detalle de las propuestas a pagar
	var winDetalle = new Ext.Window({
		title: 'Detalle de agrupación de rubros',
		modal: true,
		shadow: true,
		width: 800,
	   	height: 500,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    closable: false,
	   	items: [
	   	        NS.contDetalle
	   	        ]
	});//Termina ventana

	
	//Función para la llamada al reporte
	NS.imprimirReporte = function(){
		var regGrid = NS.storeDetalleFlujo.data.items;
		if(regGrid.length > 0) {
			var strParams = "";
			strParams = '?nomReporte=reporteDetalleFlujo';
			strParams += '&'+'nomParam1=noEmpresa';
			strParams += '&'+'valParam1=' + Ext.getCmp(PF + 'txtEmpresa').getValue();
			strParams += '&'+'nomParam2=fecIni';
			strParams += '&'+'valParam2=' + Ext.getCmp(PF + 'txtFechaIni').getValue();
			strParams += '&'+'nomParam3=fecFin';
			strParams += '&'+'valParam3=' + Ext.getCmp(PF + 'txtFechaFin').getValue();
			strParams += '&'+'nomParam4=tipoReporte';
			strParams += '&'+'valParam4=' + NS.cmbTipoReporte.getValue();
			strParams += '&'+'nomParam5=idDivisa';
			strParams += '&'+'valParam5=' +  NS.cmbDivisa.getValue();
			strParams += '&'+'nomParam6=idRubro';
			strParams += '&'+'valParam6=' + NS.idRubro;
			strParams += '&'+'nomParam7=dataIndex';
			strParams += '&'+'valParam7=' + NS.fieldName;
			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		}else
			Ext.Msg.alert('SET', 'No existe información para imprimir!!');
		return;
	};
	
	NS.reclasificaMovtos = function() {
		var regSelecDeta = NS.gridDetaCash.getSelectionModel().getSelections();
		var regSelec = NS.gridCash.getSelectionModel().getSelections();
		
		if(regSelecDeta.length < 0) {
			Ext.Msg.alert('SET', 'Seleccione un registro a reclasificar!!');
			return;
		}
		Ext.MessageBox.show({
	       title: 'SET',
	       msg: 'Reclasificando, espere por favor...',
	       width: 200,
	       wait: true,
	       progress: true,
	       waitConfig: {interval:200}
	   	});
		CashFlowNAction.reclasificaMovtos(parseInt(regSelecDeta[0].get('noFolioDet')), parseInt(NS.cmbGrupoRubro.getValue()), 
										  parseInt(NS.cmbRubro.getValue()),parseInt(Ext.getCmp(PF + 'txtInteres').getValue()), 
										  regSelecDeta[0].get('concepto'),regSelecDeta[0].get('fecValor'),function(res) {
			Ext.Msg.alert('SET', res);
			NS.cmbGrupoRubro.setValue('');
			NS.cmbRubro.setValue('');
			Ext.getCmp(PF + 'txtInteres').setValue('');
			
			NS.llenaGridDetalle(regSelec);
		});
	};
	
	//**************************************************** Termina el código para el detalle de las propuestas *****************************************//	
	
	
	
	NS.buscarReporte = function() {
		NS.storedHeadsCash.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		NS.storedHeadsCash.baseParams.tipoReporte = parseInt(NS.cmbTipoReporte.getValue());
//		NS.storedHeadsCash.baseParams.fecIni = parseInt(NS.cmbMesIni.getValue());
//		NS.storedHeadsCash.baseParams.fecFin = parseInt(NS.cmbMesFin.getValue());
		NS.storedHeadsCash.baseParams.fecIni = Ext.getCmp(PF + 'txtFechaIni').getValue();
		NS.storedHeadsCash.baseParams.fecFin = Ext.getCmp(PF + 'txtFechaFin').getValue();
		NS.storedHeadsCash.load();
	};
	
	NS.generarExcel = function() {
		CashFlowNAction.generaExcel(NS.obtieneParametros(), function(resp, e){
			Ext.Msg.alert('SET', resp);
		});
	};
	
	NS.obtieneParametros = function() {
		var matParam = new Array ();
		var regParam = {};
		
        regParam.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
//      regParam.fecIni = parseInt(NS.cmbMesIni.getValue());
//		regParam.fecFin = parseInt(NS.cmbMesFin.getValue());
        regParam.fecIni = Ext.getCmp(PF + 'txtFechaIni').getValue();
        regParam.fecFin = Ext.getCmp(PF + 'txtFechaFin').getValue();
//		regParam.tipoReporte = parseInt(NS.cmbReporte.getValue());
		regParam.tipoReporte = NS.cmbTipoReporte.getValue();
		regParam.idDivisa = NS.cmbDivisa.getValue();
		

		matParam[0] = regParam;
		return Ext.util.JSON.encode(matParam);
	};
	
	
	NS.parametrosReporte = function() {
		var matParam = new Array ();
		var regParam = {};
		
		regParam.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
		regParam.fecIni = Ext.getCmp(PF + 'txtFechaIni').getValue();
	    regParam.fecFin = Ext.getCmp(PF + 'txtFechaFin').getValue();
		regParam.tipoReporte = NS.cmbTipoReporte.getValue();
		regParam.idDivisa = NS.cmbDivisa.getValue();
		regParam.dataIndex = NS.fieldName;
		
		matParam[0] = regParam;
		return Ext.util.JSON.encode(matParam);
	};
	
	NS.contCashFlow = new Ext.FormPanel( {
		title: 'Cash Flow',
	    width: 1100,
	    height: 900,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            NS.contBusqueda,
	            NS.gridCash
	            ]
		});
	NS.contCashFlow.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
