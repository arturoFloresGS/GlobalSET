/*
 * @author: Victor H. Tello
 * @since: 10/Oct/2012
 */
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Flujos.PosicionBancaria');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);	
	
	NS.iSelec = 0;
	
	NS.fields = [];
	NS.columns = [];
	NS.numAdd=0;
	NS.newRecord=null;
	NS.storeLlenaGrid = new Ext.data.DirectStore({autoLoad : false});
	NS.storeLlenaGridCash = new Ext.data.DirectStore({autoLoad : false});
	NS.bandera = false;
	NS.varChkSaldos = "false";
	
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
	
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.selCheck = new Ext.grid.CheckboxSelectionModel({		
		singleSelect: true
	});
	
	NS.storedHeads = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		paramOrder: ['iPosicion'],
		baseParams: {iPosicion: 1},
		directFn: PosicionBancariaAction.obtenHeads,
		idProperty: 'tipoPosicion',
		fields:[
		       'tipoPosicion',
		       'columnas',
		       'fields'
		],
		listeners : {
			load:function(s,records){
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos');
				
				var tipoSelec = Ext.getCmp(PF + 'optSel').getValue();
				NS.iSelec = parseInt(tipoSelec.getGroupValue());
				
				NS.fields = Ext.util.JSON.decode(NS.storedHeads.getById(NS.iSelec).get('fields'));
				
				NS.storeLlenaGrid = new Ext.data.DirectStore({
					id: PF + 'storeLlenaGrid',
					autoLoad   : false, 
					paramsAsHash: true,
					autoSave : false,
					root: '',
					paramOrder: ['iPosicion'],
					baseParams: {iPosicion: 1},
					directFn: PosicionBancariaAction.posicionBancaria,
					fields: NS.fields,
					listeners: {
						load: function(s, records) {
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando información..."});
							
							if(records.length == null || records.length <= 0)
								Ext.Msg.alert('SET', 'No existe información para estos parametros');
						}
					}
				});
				NS.columns = new Ext.grid.ColumnModel(Ext.util.JSON.decode(NS.storedHeads.getById(NS.iSelec).get('columnas')));
				
				NS.gridParametros.reconfigure(NS.storeLlenaGrid, NS.columns);
				NS.storeLlenaGrid.baseParams.iPosicion = NS.iSelec;
				NS.storeLlenaGrid.load();
			}
		}
	});
	//NS.storedHeads.load();
	
	NS.gridParametros = new Ext.grid.GridPanel({
		store : NS.storeLlenaGrid,
		id: PF + 'gridParametros',
		name: PF + 'gridParametros',
		//selModel: NS.selCheck,
		columns : [
		           NS.selCheck         
		           ],
		//sm: new Ext.grid.CheckboxSelectionModel({singleSelect: false}),
		header:true,
		//enableDragDrop: false,
		stripeRows :true,
		selCheck: NS.selCheck,
		columnsLine: true,		
		//enableColLock:false,
	    //selModel: new Ext.grid.RowSelectionModel({singleSelect: false}),  
	    height :170,
		width :500,
		y :5
	});
	
	//Radio Button Seleccion por:
	NS.optRadios = new Ext.form.RadioGroup({
		id: PF + 'optSel',
		name: PF + 'optSel',
		x: 5,
		columns: 1, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Bancos/Empresas', name: 'optSel', inputValue: 1, checked: true},  
	          {boxLabel: 'Bancos', name: 'optSel', inputValue: 2},
	          {boxLabel: 'Cuentas', name: 'optSel', inputValue: 3},  
	          {boxLabel: 'Empresas', name: 'optSel', inputValue: 4},
	          {boxLabel: 'Rubros', name: 'optSel', inputValue: 5},  
	          {boxLabel: 'Agrupaciones', name: 'optSel', inputValue: 6}
	          ],
	    listeners: {
			change: {
				fn: function(caja, valor) {
					var tipoSelec = Ext.getCmp(PF + 'optSel').getValue();
					NS.iSelec = parseInt(tipoSelec.getGroupValue());      
					
					NS.storedHeads.baseParams.iPosicion = NS.iSelec;
					NS.storedHeads.load();
				}
			}    
		}
	});
	
	//Contenedor de Radio Button Seleccion por
	NS.contSelect = new Ext.form.FieldSet({
		title: 'Selección por:',
		x: 515,
		width: 160,
		height: 128,
		layout: 'absolute',
		items: [
		    NS.optRadios
		]
	});
	
	//Etiqueta Fecha inicial
	NS.labFechaIni = new Ext.form.Label({
		text: 'Fec. Inicial',
		x: 690,
		y: 5
	});
	//Fecha inicial
	NS.txtFechaIni = new Ext.form.DateField({
		id: PF + 'txtFechaIni',
		name: PF + 'txtFechaIni',
		x: 690,
		y: 20,
		width: 105,
		format: 'd/m/Y',
		value: apps.SET.FEC_HOY
		//value : NS.fecHoy
	});
	
	//Etiqueta Fecha Final
	NS.labFechaFin = new Ext.form.Label({
		text: 'Fec. Final',
		x: 805,
		y: 5
	});
	
	//Fecha Final
	NS.txtFechaFin = new Ext.form.DateField({
		id: PF + 'txtFechaFin',
		name: PF + 'txtFechaFin',
		x: 805,
		y: 20,
		width: 105,
		format: 'd/m/Y',
		value: apps.SET.FEC_HOY
		//value: NS.fecHoy		
	});
	
	//Etiqueta Divisa
	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa',
		x: 690,
		y: 50
	});
	
	//Caja Divisa
	NS.txtDivisa = new Ext.form.TextField({
		id: PF + 'txtDivisa',
        x: 690,
        y: 65,
        width: 50,
        tabIndex: 0,
        listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtDivisa', NS.cmbDivisas.getId());
					}
                }
			}
    	}
    });
	
	//Store Divisas
    NS.storeDivisas = new Ext.data.DirectStore({
    	paramsAsHash: false,
		root: '',
		directFn: PosicionBancariaAction.buscaDivisas, 
		idProperty: 'idDivisa', 
		fields: [
		         {name: 'idDivisa'},
		         {name: 'descDivisa'}
		         ],
		listeners: {
    		load: function(s, records) {
    			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisas, msg: "Cargando..."});
				if(records.length == null || records.length <= 0) Ext.Msg.alert('SET','No Existen divisas');
			}
		}
	}); 
    NS.storeDivisas.load();
    
    //Combo Divisas
	NS.cmbDivisas = new Ext.form.ComboBox({
		store: NS.storeDivisas,
		id: PF + 'cmbDivisas',
		name: PF + 'cmbDivisas',
		x: 745,
        y: 65,
        width: 165,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select: {
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtDivisa', NS.cmbDivisas.getId());
				}
			}
		}
	});
	
	Ext.getCmp(PF + 'txtDivisa').setValue('MN');
	Ext.getCmp(PF + 'cmbDivisas').setValue('PESOS');
	
	NS.tipoReporte = [
	                  	[ '0', 'DIARIO TESORERIA' ],
	                  	[ '1', 'FICHA VALOR EMPRESA' ],
	                  	[ '2', 'FICHA VALOR CUENTA' ]
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
		text: 'Tipo de Reportes',
		x: 690,
		y: 95
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
		x: 690,
        y: 110,
        width: 220,
		valueField: 'idTipoReporte',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un reporte',
		triggerAction: 'all'
	});
	
	NS.chkSaldos = new Ext.form.CheckboxGroup({
	    id: PF + 'chkSaldos',
	    name:PF + 'chkSaldos',
	    x: 515,
	    y: 135,
	    fieldLabel: '',
	    itemCls: 'x-check-group-alt',
	    columns: 1,
	    items: [ 
	         {
	    		xtype:'checkbox',
                boxLabel: 'Saldos Bancarios',
                id: PF + 'chkSaldos',
                listeners: {
                    check: {
                    	fn:function(checkBox, valor) {
                    		if(valor)
                    			NS.varChkSaldos = "true";
                    		else
                    			NS.varChkSaldos = "false";
                        }
	    			}
	    		}
            }
	    ]
	});
	
	//Contenedor de todos los campos de la parte superior de busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: '',
		width: 1030,
		height: 200,
		x: 10,
		layout: 'absolute',
		items: [
		        NS.gridParametros,
	            NS.contSelect,
	            NS.labFechaIni,
	            NS.txtFechaIni,
	            NS.labFechaFin,
	            NS.txtFechaFin,
	            NS.labDivisa,
	            NS.txtDivisa,
	            NS.cmbDivisas,
	            NS.labReportes,
	            NS.cmbTipoReporte,
	            {
				    xtype: 'button',
				    text: 'Buscar',
				    x: 925,
				    y: 5,
				    width: 80,
				    height: 22,
				    listeners: {
						click: {
							fn: function(e) {
								//Ficha Valor por Empresa
								NS.buscarReporte();
							}
						}
					}
				}
		        ]
	});
	
	//Store cargos donde se muestran los valores seleccionados
	NS.storeCargos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['datos', 'parametros'],
		directFn: PosicionBancariaAction.buscaDetalleBE,
		fields: [
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'idGrupo'},
			{name: 'idRubro'},
			{name: 'fecValor'},
			{name: 'fecOperacion'},
			{name: 'importe'},
		    {name: 'referencia'},
		    {name: 'concepto'},
		    {name: 'idTipoMovto'},
		    {name: 'bool'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCargos, msg:"Buscando..."});
				
				if(records.length > 0) {
					
				}
			}
		}
	});
	
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.seleccionCargos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columnasCargos = new Ext.grid.ColumnModel([
	    //NS.seleccionCargos,
	    //{header: 'algo', width: 55, dataIndex: 'indoor', type: 'checkcolumn'},
	    {header: 'Banco', width: 100, dataIndex: 'descBanco'},
	    {header: 'Chequera', width: 100, dataIndex: 'idChequera'},
	    {header: 'Grupo', width: 70, dataIndex: 'idGrupo'},
	    {header: 'Rubro', width: 70, dataIndex: 'idRubro'},
	    {header: 'Fecha Valor', width: 90, dataIndex: 'fecValor'},
	    {header: 'Fecha operación', width: 100, dataIndex: 'fecOperacion'},
	    {header: 'Importe', width: 100, dataIndex: 'importe', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Referencia', width: 100, dataIndex: 'referencia'},
	    {header: 'Concepto', width: 150, dataIndex: 'concepto'},
	    {header: 'Tipo Movto.', width: 80, dataIndex: 'idTipoMovto'}
	]);
	
	//Grid para mostrar los datos seleccionados
	NS.gridCargos = new Ext.grid.GridPanel({
		store: NS.storeCargos,
		id: 'gridCargos',
		x: 20,
		y: 210,
		cm: NS.columnasCargos,
		sm: NS.seleccionCargos,
		width: 1005,
	    height: 275,
	    stripeRows: true,
	    columnLines: true,
	    title: 'DIARIO TESORERIA',
	    hidden: true,
		listeners: {
			click: {
				fn:function(s, records) {
					var regSelec = NS.gridCargos.getSelectionModel().getSelections();
				}
			}
		}
	});
	
	NS.storedHeadsCash = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		paramOrder: ['tipoReporte', 'fecIni', 'fecFin'],
		directFn: PosicionBancariaAction.obtenHeadsCash,
		idProperty: 'tipoPosicion',
		fields:[
		        'nomReporte',
		        'tipoPosicion',
		        'columnas',
		        'fields'
		],
		listeners : {
			load:function(s,records){
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos');
				
				NS.fields = Ext.util.JSON.decode(NS.storedHeadsCash.getById(parseInt(NS.cmbTipoReporte.getValue())).get('fields'));
				
				NS.storeLlenaGridCash = new Ext.data.DirectStore({
					paramsAsHash: false,
					root: "row",
					paramOrder: ['datos', 'parametros'],
					directFn: PosicionBancariaAction.posicionBancariaCash,
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
				var jSonString = NS.obtieneParametroGrid();
				var tipoReporte = NS.cmbTipoReporte.getValue(); 
				var parametros = NS.obtieneParametros(tipoReporte);
				
				NS.storeLlenaGridCash.baseParams.datos = jSonString;
				NS.storeLlenaGridCash.baseParams.parametros = parametros;
				NS.storeLlenaGridCash.load();
				
				NS.columns = new Ext.grid.ColumnModel(Ext.util.JSON.decode(NS.storedHeadsCash.getById(parseInt(NS.cmbTipoReporte.getValue())).get('columnas')));
				var recordsGridDatos = NS.storedHeadsCash.data.items;
				
				NS.gridCash.reconfigure(NS.storeLlenaGridCash, NS.columns);
				NS.gridCash.setTitle(recordsGridDatos[0].get('nomReporte'));		
				
			}
		}
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
		height: 275,
		width: 1005,
		x: 20,
		y: 210,
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
    	}
	});
	
	
	
	//Contenedor del grid de la parte inferior
	NS.contGrid = new Ext.form.FieldSet({
		title: '',
		width: 1030,
		height: 250,
		x: 10,
		y: 260,
		layout: 'absolute',
		items: [
		        NS.gridCargos
		        ]
	});
	
	NS.buscarReporte = function() {
		var jSonString = NS.obtieneParametroGrid();
		var tipoReporte = NS.cmbTipoReporte.getValue(); 
		var parametros = NS.obtieneParametros(tipoReporte);
		
		if(parseInt(tipoReporte) == 0) {
			NS.gridCash.setVisible(false);
			NS.gridCargos.setVisible(true);
			
			NS.storeCargos.baseParams.datos = jSonString;
			NS.storeCargos.baseParams.parametros = parametros;
			NS.storeCargos.load();
		}else {
			NS.gridCargos.setVisible(false);
			NS.gridCash.setVisible(true);
			NS.storedHeadsCash.baseParams.tipoReporte = parseInt(tipoReporte);
			NS.storedHeadsCash.baseParams.fecIni = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
			NS.storedHeadsCash.baseParams.fecFin = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
			NS.storedHeadsCash.load();
		}
	};
	
	NS.obtieneParametroGrid = function() {
		var regSelec = NS.gridParametros.getSelectionModel().getSelections();
		var matRegConf = new Array ();
		var regConf = {};
		var i = 0;
		var bancos = '';
		
		if(regSelec.length == 0) {
			Ext.Msg.alert('SET', 'Seleccione un registro!!');
			return;
		}
		regConf.campo1 = '0';
		regConf.campo2 = '0';
		
		switch (NS.iSelec) {
			case 1:	//Empresas/Bancos
				for(i=0; i<regSelec.length; i++) {
					regConf.campo1 += ',' + regSelec[i].get('col0');
					regConf.campo2 += ',' + regSelec[i].get('col2');
				}
				break;
			case 2:	//Bancos
				for(i=0; i<regSelec.length; i++)
					regConf.campo1 += ',' + regSelec[i].get('col0');
				break;
			case 3:	//Cuentas
				
				regConf.campo1 = "'" + '0' + "'";
				regConf.campo2 = "'" + '0' + "'";
				
				for(i=0; i<regSelec.length; i++) {
					regConf.campo1 += ',' + "'" + regSelec[i].get('col1') + "'";
					regConf.campo2 += ',' + "'" + regSelec[i].get('col3') + "'";
				}
				
				break;
			case 4:	//Empresas
				for(i=0; i<regSelec.length; i++)
					regConf.campo1 += ',' + regSelec[i].get('col0');
				break;
			case 5:
				for(i=0; i<regSelec.length; i++) {
					regConf.campo1 += ',' + regSelec[i].get('col0');
					regConf.campo2 += ',' + regSelec[i].get('col2');
				}
				break;
			case 6:
				for(i=0; i<regSelec.length; i++) {
					regConf.campo1 += ',' + regSelec[i].get('col0');
					regConf.campo2 += ',' + regSelec[i].get('col2');
				}
				break;
		}
		matRegConf[0] = regConf;
		return Ext.util.JSON.encode(matRegConf);
	};
	
	NS.obtieneParametros = function(tipoReporte) {
		var matParam = new Array ();
		var regParam = {};
		
		regParam.fecIni = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
		regParam.fecFin = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
		regParam.selec = NS.iSelec +'';
		regParam.tipoReporte = tipoReporte + '';
		regParam.divisa = Ext.getCmp(PF + 'txtDivisa').getValue();
		regParam.chkSaldos = NS.varChkSaldos+'';
		
		matParam[0] = regParam;
		return Ext.util.JSON.encode(matParam);
	};
	
	NS.contPosicionBancaria = new Ext.FormPanel( {
		title: 'Posición Bancaria',
	    width: 1100,
	    height: 900,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            NS.contBusqueda,
	            NS.gridCargos,
	            NS.gridCash
	            ]
		});
	NS.contPosicionBancaria.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
