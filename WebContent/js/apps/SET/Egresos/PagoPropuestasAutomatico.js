/*
 * @author: Victor H. Tello
 * @since: 02/April/2012
 */
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Egresos.PagoPropuestasAutomatico');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.iComponente = 0;
	
	NS.gpoEmpresa = 0;
	NS.grupo = 0;
	NS.idDivision = 0;
	NS.idDivisa = '';
	NS.chkPropuestos = false;
	NS.chkServicio = false;
	NS.chequera = false;
	
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
	//Cambia la fecha cuando biene con formato ejemplo: 2012-01-01 00:00:00.0 a formato 01/01/2012
	function cambiarFechaDMA(fecha) {
		return fecha.substring(8, 10) + '/' + fecha.substring(5, 7) + '/' + fecha.substring(0, 4);
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
	//Etiqueta Grupo empresa
	NS.labGpoEmpresa = new Ext.form.Label({
		text: 'Grupo Empresa:',
        x: 10
	});
	//Store Grupo empresa
    NS.storeGpoEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConfirmacionCargoCtaAction.llenaComboGpoEmpresas, 
		idProperty: 'idGrupoFlujo', 
		fields: [
			 {name: 'idGrupoFlujo'},
			 {name: 'descGrupoFlujo'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGpoEmpresas, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No tiene grupos de empresas asignados');
			}
		}
	}); 
	NS.storeGpoEmpresas.load();
	
	//Combo empresas
	NS.cmbGpoEmpresas = new Ext.form.ComboBox({
		store: NS.storeGpoEmpresas,
		id: PF + 'cmbGpoEmpresas',
		x: 10,
        y: 15,
        width: 320,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idGrupoFlujo',
		displayField: 'descGrupoFlujo',
		autocomplete: true,
		emptyText: 'Seleccione un grupo',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != '') {
						NS.gpoEmpresa = combo.getValue();
						
						if(NS.grupo != 0)
							NS.funCveControl();
					}
				}
			}
		}
	});
	//Etiqueta Division
	NS.labDivision = new Ext.form.Label({
		text: 'Division:',
        x: 350
	});
	//Store Division
	NS.storeDivision = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConfirmacionCargoCtaAction.llenaComboDivision, 
		idProperty: 'idDivision', 
		fields: [
			 {name: 'idDivision'},
			 {name: 'descDivision'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivision, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No tiene divisiones asignadas');
				
				//Se agrega la opcion todas las divisiones
	 			var recStoredDiv = NS.storeDivision.recordType;	
				var todas = new recStoredDiv({
					idDivision: 0,
					descDivision: '*********TODAS*********'
		       	});
		   		NS.storeDivision.insert(0,todas);
			}
		}
	});
	NS.storeDivision.load();
	
	//Combo Division
	NS.cmbDivision = new Ext.form.ComboBox({
		store: NS.storeDivision,
		id: PF + 'cmbDivision',
		name: PF + 'cmbDivision',
		x: 350,
	    y: 15,
	    width: 220,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'idDivision',
		displayField:'descDivision',
		autocomplete: true,
		emptyText: 'Seleccione una división',
		triggerAction: 'all',
		value: '',
		tabIndex: 3,
		listeners: {
			select: {
				fn: function(combo, valor) {
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != '') {
						NS.idDivision = combo.getValue();
						
						if(NS.grupo != 0)
							NS.funCveControl();
					}
				}
			}
		}
	});
	//Etiqueta Grupo Rubro
	NS.labGpoRubro = new Ext.form.Label({
		text: 'Grupo Rubro:',
        x: 10,
        y: 50
	});
	//Caja Grupo Rubro
	NS.txtGpoRubro = new Ext.form.TextField({
		id: PF + 'txtGpoRubro',
		name: PF + 'txtGpoRubro',
		x: 10,
		y: 65,
		width: 50,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var valCombo = Ext.getCmp(PF + 'txtCupoManual').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtCupoManual').getValue()));
						
						if(valCombo != '') {
							NS.grupo = caja.getValue();
							NS.funCveControl();
						}
					}
				}
			}
		}
	});
	//Store Grupo Rubros
	NS.storeGpoRubros = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConfirmacionCargoCtaAction.llenaComboGpoCupo, 
		idProperty: 'idGrupo',
		fields: [
			 {name: 'idGrupo'},
			 {name: 'descGrupoCupo'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGpoRubros, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No tiene grupos de rubros');
			}
		}
	});
	NS.storeGpoRubros.load();
	
	//Combo Grupo rubros
	NS.cmbGpoRubros = new Ext.form.ComboBox({
		store: NS.storeGpoRubros,
		id: PF + 'cmbGpoRubros',
		name: PF + 'cmbGpoRubros',
		x: 65,
	    y: 65,
	    width: 265,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'idGrupo',
		displayField:'descGrupoCupo',
		autocomplete: true,
		emptyText: 'Seleccione un grupo de rubro',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 3,
		listeners: {
			select: {
				fn: function(combo, valor) {
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != '') {
						NS.grupo = combo.getValue();
						
						if(NS.gpoEmpresa != 0 || NS.idDivision != 0)
							NS.funCveControl();
					}
				}
			}
		}
	});
	//Etiqueta Cve control cupos
	NS.labCveControlCupos = new Ext.form.Label({
		text: 'Cve Control Cupos:',
        x: 350,
        y: 50
	});
	//Store Cve Control
	NS.storeCveControl = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['gpoEmpresa', 'grupo', 'idDivision'],
		directFn: ConfirmacionCargoCtaAction.llenaComboCveControl, 
		idProperty: 'cveControl',
		fields: [
			 {name: 'cveControl'},
			 {name: 'descCveControl'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCveControl, msg:"Cargando..."});
			}
		}
	});
	
	//Combo Cve Control
	NS.cmbCveControl = new Ext.form.ComboBox({
		store: NS.storeCveControl,
		id: PF + 'cmbCveControl',
		name: PF + 'cmbCveControl',
		x: 350,
		y: 65,
		width: 220,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'cveControl',
		displayField:'descCveControl',
		autocomplete: true,
		emptyText: 'Seleccione clave control',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 3,
		listeners: {
			select: {
				fn: function(combo, valor) {
					if(combo.getValue() != null && combo.getValue() != '' && combo.getValue() != undefined) {
						NS.buscaCupos(combo.getValue());
					}
				}
			}
		}
	});
	//checkBox proveedores y pagos propuestos
	NS.chkProve = new Ext.form.CheckboxGroup({
	    id: PF + 'chkProve',
	    x: 590,
	    y: 100,
	    itemCls: 'x-check-group-alt',
	    columns: 1,
	    items: [ {
	    		xtype:'checkbox',
                boxLabel: 'Proveedores de Servicios Básicos',
                id: PF + 'chkProvServ',
                listeners: {
                    check: {
                    	fn:function(checkBox,valor) {
                    		if(valor)
                    			NS.chkServicio = true;
                    		else
                    			NS.chkServicio = false;
                        }
	    			}
	    		}
            },{
	    		xtype:'checkbox',
                boxLabel: 'Pagos Propuestos',
                id: PF + 'chkPagos',
                listeners: {
                    check: {
                    	fn:function(checkBox,valor) {
                    		if(valor)
                    			NS.chkPropuestos = true;
                    		else
                    			NS.chkPropuestos = false;
                    		NS.storePagosPen.removeAll();
                			NS.gridPagosPen.getView().refresh();
                			NS.storePagosPro.removeAll();
                			NS.gridPagosPro.getView().refresh();
                        }
	    			}
	    		}
            }
	    ]
	});
	//Etiqueta fecha pago
	NS.labFechaPago = new Ext.form.Label({
		text: 'Fecha Pago:',
        x: 590,
        y: 150
	});
	//Caja FechaPago
	NS.txtFechaPago = new Ext.form.TextField({
		id: PF + 'txtFechaPago',
		name: PF + 'txtFechaPago',
		x: 590,
		y: 165,
		width: 100,
		readOnly: true
	});
	
	//Etiqueta Criterio
	NS.labCriterio = new Ext.form.Label({
		text: 'Criterio:',
        x: 10
	});
	//Criterios de busqueda para el stored
	NS.criterios = 
		[['1', 'FECHAS'],
		 ['2', 'ESTATUS'],
		 ['3', 'MONTOS'],
		 ['4', 'BANCO RECEPTOR'],
		 ['5', 'CONCEPTO'],
		 ['6', 'LOTE ENTRADA'],
		 ['7', 'DIVISION'],
		 ['8', 'FORMA DE PAGO'],
		 ['9', 'DIVISA'],
		 ['10', 'CLAVE OPERACION'],
		 ['11', 'BLOQUEO'],
		 ['12', 'NUMERO DE PROVEEDOR'],
		 ['13', 'NOMBRE PROVEEDOR'],
		 ['14', 'FACTURA'],
		 ['15', 'NUMERO DE PEDIDO'],
		 ['16', 'ORIGEN DEL MOVTO'],
		 ['17', 'RUBRO DEL MOVIMIENTO'],
		 ['18', 'CHEQUERA BENEF.'],
		 ['19', 'NUMERO DE DOCUMENTO'],
		 ['20', 'BANCO PAGADOR'],
		 ['21', 'CHEQUERA PAGADORA'],
		 ['22', 'CLASE DE DOCUMENTO'],
		 ['23', 'CAJA']
		];
	
	// store de criterio
	NS.storeCriterio = new Ext.data.SimpleStore({
		idProperty: 'idCriterio',
		fields: [
		         {name: 'idCriterio'},
		         {name: 'nombre'}
		        ]
		});
	NS.storeCriterio.loadData(NS.criterios);
	//combo Criterios
	NS.cmbCriterios = new Ext.form.ComboBox({
		store: NS.storeCriterio,
		id: PF+'cmbCriterios',
		x: 10,
        y: 15,
        width: 200,
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idCriterio',
		displayField: 'nombre',
		autocomplete: true,
		emptyText: 'Seleccione un criterio',
		triggerAction: 'all',
		value: '',
		hiddenName: 'nombre',
		listeners: {
			select: {
				fn: function(combo, value) {
					if(combo.getValue() != null && combo.getValue() != undefined && combo.getValue() != '')
						NS.valor(combo.getValue());
				}
			}
		}
	});
	//Etiqueta Valor
	NS.labValor = new Ext.form.Label({
		text: 'Valor:',
        x: 10,
        y: 50
	});
	//Store Valor
	NS.storeValor = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['tipoCombo', 'gpoEmpresa', 'idBcoPag'],
		baseParams: {tipoCombo: 0, gpoEmpresa: 0, idBcoPag: 0},
		directFn: ConfirmacionCargoCtaAction.llenaComboValor, 
		idProperty: 'idValor',
		fields: [
			 {name: 'idValor'},
			 {name: 'descValor'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeValor, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No tiene valores cargados');
			}
		}
	});
	//Combo Valor
	NS.cmbValor = new Ext.form.ComboBox({
		store: NS.storeValor,
		id: PF + 'cmbValor',
		x: 10,
	    y: 65,
	    width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idValor',
		displayField: 'descValor',
		autocomplete: true,
		emptyText: 'Seleccione un Valor',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 3,
		listeners: {
			select: {
				fn: function(combo, valor) {
					if(Ext.getCmp(PF + 'cmbCriterios').getValue() == 9) NS.idDivisa = combo.getValue();
					NS.buscaRegStore();
				}
			}
		}
	});
	//Caja Fecha Inicial
	NS.txtFecIni = new Ext.form.DateField({
		id: PF + 'txtFecIni',
		x: 10,
		y: 65,
		width: 95,
		format: 'd/m/Y',
		value: NS.fecHoy,
		listeners: {
			change: {
				fn: function(caja, valor) {
					NS.buscaRegStore();
				}
			}
		}
	});
	//Caja Fecha Final
	NS.txtFecFin = new Ext.form.DateField({
		id: PF + 'txtFecFin',
		x: 115,
		y: 65,
		width: 95,
		format: 'd/m/Y',
		value: NS.fecHoy,
		listeners: {
			change: {
				fn: function(caja, valor) {
					NS.buscaRegStore();
				}
			}
		}
	});
	//Caja Inicial
	NS.txtInicial = new Ext.form.TextField({
		id: PF + 'txtInicial',
		x: 10,
		y: 65,
		width: 95,
		listeners: {
			change: {
				fn: function(caja, valor) {
					NS.buscaRegStore();
				}
			}
		}
	});
	//Caja Final
	NS.txtFinal = new Ext.form.TextField({
		id: PF + 'txtFinal',
		x: 115,
		y: 65,
		width: 95,
		listeners: {
			change: {
				fn: function(caja, valor) {
					NS.buscaRegStore();
				}
			}
		}
	});
	//Caja Valor
	NS.txtValor = new Ext.form.TextField({
		id: PF + 'txtValor',
		x: 10,
		y: 65,
		width: 200,
		listeners: {
			change: {
				fn: function(caja, valor) {
					NS.buscaRegStore();
				}
			}
		}
	});
	//Store CriValor
	NS.storeCriValor = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		//paramOrder: [],
		//baseParams: {},
		//directFn:  ConfirmacionCargoCtaAction.buscarCupos, 
		fields: [
		         {name: 'idCriterio'},
		         {name: 'criterio'},
				 {name: 'idValor'},
				 {name: 'valor'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCriValor, msg: "Buscando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen registros con estos parametro de busqueda!!');
			}
		}
	}); 
	//Columna de Seleccion  
	NS.columGrid = new Ext.grid.ColumnModel([
	    {header: 'idCriterio', width: 150, dataIndex: 'idCriterio', sortable: true, hidden: true},
	    {header: 'Criterio', width: 150, dataIndex: 'criterio', sortable: true},
	    {header: 'idValor', width: 50, dataIndex: 'idValor', sortable: true, hidden: true},
	    {header: 'Valor', width: 120, dataIndex: 'valor', sortable: true}
	]);
	//Grid para mostrar los datos seleccionados
	NS.gridCriValor = new Ext.grid.GridPanel({
		store: NS.storeCriValor,
		id: PF + 'gridCriValor',
		x: 240,
		cm: NS.columGrid,
		width: 290,
	    height: 90,
	    stripeRows: true,
	    columnLines: true,
	    listeners: {
			dblclick: {
				fn: function(grid){
					var regSelect = NS.gridCriValor.getSelectionModel().getSelections();
					
					for(var i=0; i<regSelect.length; i++) {
						if(regSelect[i].get('criterio') == 'DIVISA') NS.idDivisa = '';
						NS.storeCriValor.remove(regSelect[i]);
						NS.gridCriValor.getView().refresh();
					}
				}
			}
		}
	});
	//Contenedor de combos de criterios y valor
	NS.contCriterios = new Ext.form.FieldSet({
		x: 10,
		y: 100,
		width: 560,
		height: 110,
		layout: 'absolute',
		items: [
		        NS.labCriterio,		NS.cmbCriterios,
		        NS.labValor,		NS.cmbValor,
		        NS.txtFecIni,		NS.txtFecFin,
		        NS.txtInicial,		NS.txtFinal,
		        NS.txtValor,
		        NS.gridCriValor
		]
	});
	//Etiqueta Fecha
	NS.labFecha = new Ext.form.Label({
		text: 'Fecha:',
        x: 10
	});
	//Caja Fecha
	NS.txtFecha = new Ext.form.TextField({
		id: PF + 'txtFecha',
		x: 10,
		y: 15,
		width: 90,
		readOnly: true
	});
	//Etiqueta Cupo Automatico
	NS.labCupoAutomatico = new Ext.form.Label({
		text: 'Cupo Automático:',
        x: 120
	});
	//Caja Cupo Automatico
	NS.txtCupoAutomatico = new Ext.form.TextField({
		id: PF + 'txtCupoAutomatico',
		x: 120,
		y: 15,
		width: 100,
		readOnly: true
	});
	//Etiqueta Monto Maximo
	NS.labMontoMaximo = new Ext.form.Label({
		text: 'Monto Maximo:',
        x: 240
	});
	//Caja Monto Maximo
	NS.txtMontoMaximo = new Ext.form.TextField({
		id: PF + 'txtMontoMaximo',
		x: 240,
		y: 15,
		width: 100,
		readOnly: true
	});
	//Etiqueta Monto Maximo
	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa:',
        x: 10,
        y: 45
	});
	//Contenedor de Radio Button SI - NO
	NS.contSelAutomatica = new Ext.form.FieldSet({
		title: 'Selección Automática',
		x: 590,
		width: 370,
		height: 90,
		layout: 'absolute',
		items: [
		        NS.labFecha,			NS.txtFecha,
		        NS.labCupoAutomatico,	NS.txtCupoAutomatico,
		        NS.labMontoMaximo,		NS.txtMontoMaximo,
		        NS.labDivisa
		]
	});
	//Contenedor de Busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: 'búsqueda',
		x: 30,
		width: 985,
	    height: 250,
	    layout: 'absolute',
	    items:[
	        NS.labGpoEmpresa,		NS.cmbGpoEmpresas,
	        NS.labDivision,			NS.cmbDivision,
	        NS.labGpoRubro,			NS.txtGpoRubro,			NS.cmbGpoRubros,
	        NS.labCveControlCupos,	NS.cmbCveControl,
	        NS.chkProve,
	        NS.labFechaPago,		NS.txtFechaPago,
	        NS.contCriterios,
	        NS.contSelAutomatica,
            {
	        	xtype: 'button',
	        	text: 'Buscar',
	        	x: 870,
	        	y: 115,
	        	width: 80,
	        	height: 22,
	        	listeners: {
	        		click:{
	        			fn:function(e){
	        				NS.buscarRegistro();
	        			}
	        		}
	        	}
	        }
	    ]
	});
	//Store Pagos Pendientes
	NS.storePagosPen = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['gpoEmpresa', 'idDivisa', 'grupo', 'cveControl', 'idDivision', 'fecPropuesta', 'chkServicio', 
		             'chkPropuestos', 'bChequera', 'paramsGrid', 'totPropuesto', 'tipoGrid'],
		directFn:  ConfirmacionCargoCtaAction.selectPagosAutomaticos,
		fields: [
		         {name: 'noDocto'},
		         {name: 'nomEmpresa'},
				 {name: 'nomEmpresa'},
				 {name: 'importe'},
				 {name: 'noCliente'},
				 {name: 'noCliente'},
				 {name: 'beneficiario'},
				 {name: 'idRubro'},
				 {name: 'idDivisa'},
				 {name: 'fecValor'},
				 {name: 'importeMn'},
				 {name: 'fecPropuesta'},
				 {name: 'idFormaPago'},
				 {name: 'descFormaPago'},
				 {name: 'fecModif'},
				 {name: 'noFolioDet'},
				 {name: 'idChequeraBenef'},
				 {name: 'noFactura'},
				 {name: 'concepto'},
				 {name: 'idContable'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagosPen, msg: "Buscando..."});
				
				if(records.length != null && records.length > 0) {
					NS.selecGridPen.selectRange(0, records.length);
					NS.sumaPagosPendientes();
				}
			}
		}
	}); 
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.selecGridPen = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	//Columna de Seleccion  
	NS.columGrid = new Ext.grid.ColumnModel([
	    NS.selecGridPen,
	    {header: 'No. Docto', width: 100, dataIndex: 'noDocto', sortable: true},
	    {header: 'Empresa', width: 100, dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'Dias Venc.', width: 100, dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'Importe', width: 100, dataIndex: 'importe', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'No. proveedor', width: 100, dataIndex: 'noCliente', sortable: true, hidden:true},
	    {header: 'No. proveedor', width: 100, dataIndex: 'noCliente', sortable: true},
	    {header: 'Proveedor', width: 100, dataIndex: 'beneficiario', sortable: true},
	    {header: 'Rubro', width: 80, dataIndex: 'idRubro', sortable: true},
	    {header: 'Divisa', width: 100, dataIndex: 'idDivisa', sortable: true},
	    {header: 'Fecha Venc.', width: 100, dataIndex: 'fecValor', sortable: true},
	    {header: 'Importe MN', width: 60, dataIndex: 'importeMn', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Fecha Propuesta', width: 140, dataIndex: 'fecPropuesta', sortable: true},
	    {header: 'Id Forma Pago', width: 140, dataIndex: 'idFormaPago', sortable: true, hidden: true},
	    {header: 'Forma Pago', width: 140, dataIndex: 'descFormaPago', sortable: true},
	    {header: 'Fecha Modif.', width: 140, dataIndex: 'fecModif', sortable: true},
	    {header: 'Folio Det', width: 140, dataIndex: 'noFolioDet', sortable: true},
	    {header: 'Chequera Benef.', width: 140, dataIndex: 'idChequeraBenef', sortable: true},
	    {header: 'Factura', width: 140, dataIndex: 'noFactura', sortable: true},
	    {header: 'Concepto', width: 140, dataIndex: 'concepto', sortable: true},
	    {header: 'Clase Docto.', width: 140, dataIndex: 'idContable', sortable: true}
	]);
	//Grid para mostrar los datos seleccionados
	NS.gridPagosPen = new Ext.grid.GridPanel({
		store: NS.storePagosPen,
		title: 'Pagos Pendientes',
		id: PF + 'gridPagosPen',
		x: 5,
		cm: NS.columGrid,
		sm: NS.selecGridPen,
		width: 470,
	    height: 200,
	    stripeRows: true,
	    columnLines: true,
	    listeners: {
			click: {
				fn: function(grid) {
					NS.sumaPagosPendientes();
				}
			}
		}
	});
	//Store Pagos Propuestos
	NS.storePagosPro = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['gpoEmpresa', 'idDivisa', 'grupo', 'cveControl', 'idDivision', 'fecPropuesta', 'chkServicio', 
		             'chkPropuestos', 'bChequera', 'paramsGrid', 'totPropuesto', 'tipoGrid'],
		directFn:  ConfirmacionCargoCtaAction.selectPagosAutomaticos,
		fields: [
		         {name: 'noDocto'},
		         {name: 'nomEmpresa'},
				 {name: 'nomEmpresa'},
				 {name: 'importe'},
				 {name: 'noCliente'},
				 {name: 'noCliente'},
				 {name: 'beneficiario'},
				 {name: 'idRubro'},
				 {name: 'idDivisa'},
				 {name: 'fecValor'},
				 {name: 'importeMn'},
				 {name: 'fecPropuesta'},
				 {name: 'idFormaPago'},
				 {name: 'descFormaPago'},
				 {name: 'fecModif'},
				 {name: 'noFolioDet'},
				 {name: 'idChequeraBenef'},
				 {name: 'noFactura'},
				 {name: 'concepto'},
				 {name: 'idContable'},
				 {name: 'totalPropuesto'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagosPro, msg: "Buscando..."});
				
				if(records.length != null && records.length > 0) {
					//var regStore = NS.storePagosPro.data.items;
					//Ext.getCmp(PF + 'txtMontoTPro').setValue(NS.formatNumber(regStore[regStore.length - 1].get('totalPropuesto')));
					//NS.storePagosPro.remove(regStore[regStore.length - 1]);
					NS.selecGridPro.selectRange(-1, records.length - 1);
					NS.sumaPagosPropuestos();
				}
			}
		}
	}); 
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.selecGridPro = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	//Columna de Seleccion  
	NS.columGrid = new Ext.grid.ColumnModel([
	    NS.selecGridPro,
	    {header: 'No. Docto', width: 100, dataIndex: 'noDocto', sortable: true},
	    {header: 'Empresa', width: 100, dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'Dias Venc.', width: 100, dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'Importe', width: 100, dataIndex: 'importe', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'No. proveedor', width: 100, dataIndex: 'noCliente', sortable: true, hidden:true},
	    {header: 'No. proveedor', width: 100, dataIndex: 'noCliente', sortable: true},
	    {header: 'Proveedor', width: 100, dataIndex: 'beneficiario', sortable: true},
	    {header: 'Rubro', width: 80, dataIndex: 'idRubro', sortable: true},
	    {header: 'Divisa', width: 100, dataIndex: 'idDivisa', sortable: true},
	    {header: 'Fecha Venc.', width: 100, dataIndex: 'fecValor', sortable: true},
	    {header: 'Importe MN', width: 60, dataIndex: 'importeMn', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Fecha Propuesta', width: 140, dataIndex: 'fecPropuesta', sortable: true},
	    {header: 'Id Forma Pago', width: 140, dataIndex: 'idFormaPago', sortable: true, hidden: true},
	    {header: 'Forma Pago', width: 140, dataIndex: 'descFormaPago', sortable: true},
	    {header: 'Fecha Modif.', width: 140, dataIndex: 'fecModif', sortable: true},
	    {header: 'Folio Det', width: 140, dataIndex: 'noFolioDet', sortable: true},
	    {header: 'Chequera Benef.', width: 140, dataIndex: 'idChequeraBenef', sortable: true},
	    {header: 'Factura', width: 140, dataIndex: 'noFactura', sortable: true},
	    {header: 'Concepto', width: 140, dataIndex: 'concepto', sortable: true},
	    {header: 'Clase Docto.', width: 140, dataIndex: 'idContable', sortable: true}
	]);
	//Grid para mostrar los datos seleccionados
	NS.gridPagosPro = new Ext.grid.GridPanel({
		store: NS.storePagosPro,
		title: 'Pagos Propuestos',
		id: PF + 'gridPagosPro',
		x: 485,
		cm: NS.columGrid,
		sm: NS.selecGridPro,
		width: 470,
	    height: 200,
	    stripeRows: true,
	    columnLines: true,
	    listeners: {
			click: {
				fn: function(grid) {
					NS.sumaPagosPropuestos();
				}
			}
		}
	});
	//Etiqueta Monto total de Pagos pendientes
	NS.labMontoTPagosPen = new Ext.form.Label({
		text: 'T. Pagos Pendienes:',
        x: 10,
        y: 210
	});
	//Caja Monto total de Pagos pendientes
	NS.txtMontoTPagosPen = new Ext.form.TextField({
		id: PF + 'txtMontoTPagosPen',
		name: PF + 'txtMontoTPagosPen',
		x: 10,
		y: 225,
		width: 100,
		readOnly: true
	});
	//Etiqueta cupo manual
	NS.labCupoManual = new Ext.form.Label({
		text: 'Cupo Manual:',
        x: 130,
        y: 210
	});
	//Caja cupo manual
	NS.txtCupoManual = new Ext.form.TextField({
		id: PF + 'txtCupoManual',
		name: PF + 'txtCupoManual',
		x: 130,
		y: 225,
		width: 100,
		readOnly: true
	});
	//Etiqueta diferencia
	NS.labDiferencia = new Ext.form.Label({
		text: 'Diferencia:',
        x: 250,
        y: 210
	});
	//Caja Diferencia
	NS.txtDiferencia = new Ext.form.TextField({
		id: PF + 'txtDiferencia',
		name: PF + 'txtDiferencia',
		x: 250,
		y: 225,
		width: 100,
		readOnly: true
	});
	//Etiqueta agragar a propuestos
	NS.labAgregar = new Ext.form.Label({
		text: 'Agregar:',
        x: 435,
        y: 210
	});
	//Etiqueta Quitar de propuestos
	NS.labQuitar = new Ext.form.Label({
		text: 'Quitar:',
        x: 495,
        y: 210
	});
	//Etiqueta dip para pagar
	NS.labDispPagar = new Ext.form.Label({
		text: 'Disp. Pagar:',
        x: 545,
        y: 210
	});
	//Caja Disp para Pagar
	NS.txtDispPagar = new Ext.form.TextField({
		id: PF + 'txtDispPagar',
		name: PF + 'txtDispPagar',
		x: 545,
		y: 225,
		width: 100,
		readOnly: true
	});
	//Etiqueta monto total prop
	NS.labMontoTProp = new Ext.form.Label({
		text: 'Total Propuesto:',
        x: 665,
        y: 210
	});
	//Caja monto total prop
	NS.txtMontoTPro = new Ext.form.TextField({
		id: PF + 'txtMontoTPro',
		name: PF + 'txtMontoTPro',
		x: 665,
		y: 225,
		width: 100,
		readOnly: true
	});
	//Contenedor Grid Pagos
	NS.contGridPagos = new Ext.form.FieldSet({
		x: 30,
		y: 260,
		width: 985,
		height: 280,
		layout: 'absolute',
		items: [
		        NS.gridPagosPen,
		        NS.gridPagosPro,
		        NS.labMontoTPagosPen,	NS.txtMontoTPagosPen,
		        NS.labCupoManual,		NS.txtCupoManual,
		        NS.labDiferencia,		NS.txtDiferencia,
		        NS.labAgregar,
		        {
		        	xtype: 'button',
		        	text: '>',
		        	x: 435,
		        	y: 225,
		        	width: 30,
		        	height: 22,
		        	listeners: {
		        		click: {
		        			fn:function(e) {
		        				NS.agregar();
		        			}
		        		}
		        	}
		        },
		        {
		        	xtype: 'button',
		        	text: '<',
		        	x: 495,
		        	y: 225,
		        	width: 30,
		        	height: 22,
		        	listeners: {
		        		click: {
		        			fn:function(e) {
		        				NS.quitar();
		        			}
		        		}
		        	}
		        },
		        NS.labQuitar,
		        NS.labDispPagar,		NS.txtDispPagar,
		        NS.labMontoTProp,		NS.txtMontoTPro,
		        
		        {
		        	xtype: 'button',
		        	text: 'Ejecutar',
		        	id: PF + 'ejecutar',
		        	x: 780,
		        	y: 225,
		        	width: 80,
		        	height: 22,
		        	listeners: {
		        		click:{
		        			fn:function(e){
		        				NS.ejecutar();
		        			}
		        		}
		        	}
		        },{
		        	xtype: 'button',
		        	text: 'Limpiar',
		        	x: 870,
		        	y: 225,
		        	width: 80,
		        	height: 22,
		        	listeners: {
		        		click:{
		        			fn:function(e){
		        				NS.limpiar();
		        			}
		        		}
		        	}
		        }
		        ]
	});
	
	NS.funCveControl = function() {
		NS.storeCveControl.baseParams.gpoEmpresa = NS.gpoEmpresa;
		NS.storeCveControl.baseParams.grupo = NS.grupo;
		NS.storeCveControl.baseParams.idDivision = NS.idDivision;
		NS.storeCveControl.load();
	};
	
	NS.valor = function(valorCombo) {
		var i = 0;
		var valores = '';
		var tipoDato = '';
		var recStoredValor = NS.storeValor.recordType;
		
		NS.cmbValor.reset();
		NS.storeValor.removeAll();
		NS.iComponente = 0;
		
		switch (parseInt(valorCombo)) {
			case 1: //FECHAS
				NS.visible(false, true, false, false);
				break;
			case 2: //ESTATUS
				NS.visible(true, false, false, false);
				tipoDato = 'CAPTURADO';
				
				while(i < 3) {
					valores = new recStoredValor({
						idValor: i+1,
						descValor: tipoDato
			       	});
			   		NS.storeValor.insert(i, valores);
			   		tipoDato = 'IMPORTADO';
			   		i++;
			   		if(i == 1) tipoDato = 'FILIAL';
				}
				break;
			case 3: //MONTOS
				NS.visible(false, false, true, false);
				break;
			case 4: //BANCO RECEPTOR
				NS.visible(true, false, false, false);
				NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
				NS.storeValor.load();
				break;
			case 5: //CONCEPTO
				NS.visible(false, false, false, true);
				break;
			case 6: //LOTE ENTRADA
				NS.visible(false, false, false, true);
				break;
			case 7: //DIVISION
				NS.visible(true, false, false, false);
				NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
				NS.storeValor.baseParams.gpoEmpresa = parseInt(Ext.getCmp(PF + 'cmbGpoEmpresas').getValue());
				NS.storeValor.load();
				break;
			case 8: //FORMA DE PAGO
				NS.visible(true, false, false, false);
				NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
				NS.storeValor.load();
				break;
			case 9: //DIVISA
				NS.visible(true, false, false, false);
				NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
				NS.storeValor.load();
				break;
			case 10: //CLAVE OPERACION
				NS.visible(true, false, false, false);
				NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
				NS.storeValor.load();
				break;
			case 11: //BLOQUEO
				NS.visible(true, false, false, false);
				tipoDato = 'NO';
				
				while(i < 2) {
					valores = new recStoredValor({
						idValor: i+1,
						descValor: tipoDato 
			       	});
			   		NS.storeValor.insert(i, valores);
			   		tipoDato = 'SI';
			   		i++;
				}
				break;
			case 12: //NUMERO DE PROVEEDOR
				NS.visible(false, false, true, false);
				break;
			case 13: //NOMBRE PROVEEDOR
				NS.visible(false, false, false, true);
				break;
			case 14: //FACTURA
				NS.visible(false, false, false, true);
				break;
			case 15: //NUMERO DE PEDIDO
				NS.visible(false, false, false, true);
				break;
			case 16: //ORIGEN DEL MOVTO
				NS.visible(true, false, false, false);
				NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
				NS.storeValor.load();
				break;
			case 17: //RUBRO DEL MOVIMIENTO
				NS.visible(true, false, false, false);
				NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
				NS.storeValor.baseParams.gpoEmpresa = parseInt(Ext.getCmp(PF + 'cmbGpoEmpresas').getValue());
				NS.storeValor.load();
				break;
			case 18: //CHEQUERA BENEF.
				NS.visible(true, false, false, false);
				tipoDato = 'NACIONAL';
				
				while(i < 2) {
					valores = new recStoredValor({
						idValor: i+1,
						descValor: tipoDato 
			       	});
			   		NS.storeValor.insert(i, valores);
			   		tipoDato = 'EXTRANJERA';
			   		i++;
				}
				break;
			case 19: //NUMERO DE DOCUMENTO
				NS.visible(false, false, true, false);
				break;
			case 20: //BANCO PAGADOR
				NS.visible(true, false, false, false);
				NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
				NS.storeValor.baseParams.gpoEmpresa = parseInt(Ext.getCmp(PF + 'cmbGpoEmpresas').getValue());
				NS.storeValor.load();
				break;
			case 21: //CHEQUERA PAGADORA
				NS.visible(true, false, false, false);
				var recDatosStore = NS.storeCriValor.data.items;
				var entro = false;
				
				for(i = 0; i < recDatosStore.length; i++) {
					if(recDatosStore[i].get('criterio') == "BANCO PAGADOR") {
						NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
						NS.storeValor.baseParams.gpoEmpresa = parseInt(Ext.getCmp(PF + 'cmbGpoEmpresas').getValue());
						NS.storeValor.baseParams.idBcoPag = parseInt(recDatosStore[i].get('idValor'));
						NS.storeValor.load();
						entro = true;
					}
				}
				if(!entro) Ext.Msg.alert('SET', 'Necesita seleccionar el banco pagador primero!!');
				break;
			case 22: //CLASE DE DOCUMENTO
				NS.visible(false, false, false, true);
				break;
			case 23: //CAJA
				NS.visible(true, false, false, false);
				NS.storeValor.baseParams.tipoCombo = parseInt(valorCombo);
				NS.storeValor.load();
				break;
		}
	};
	
	NS.buscaRegStore = function() {
		var datosStore = NS.storeCriValor.recordType;
		var regStore = NS.storeCriValor.data.items;
		var x = regStore.length;
		var i = 0;
		var encontro = false;
		
		if(Ext.getCmp(PF + 'cmbCriterios').getValue() == '')
			Ext.Msg.alert('SET', 'Seleccione primero un criterio!!');
		else {
			var storeItems = Ext.getCmp(PF + 'cmbCriterios').getStore().data.items;
			var idCriterio = Ext.getCmp(PF + 'cmbCriterios').getValue();
			
			for(i=0; i<storeItems.length; i++) {
				if(storeItems[i].get('idCriterio') == idCriterio) 
					var descCriterio = storeItems[i].get('nombre');
			}
			
			if(NS.iComponente == 1) {  //Saca datos del combo valor
				var storeItemsValor = Ext.getCmp(PF + 'cmbValor').getStore().data.items;
				var idValor = Ext.getCmp(PF + 'cmbValor').getValue();
				
				for(i=0; i<storeItemsValor.length; i++) {
					if(storeItemsValor[i].get('idValor') == idValor) 
						var descValor = storeItemsValor[i].get('descValor');
				}
			}else if(NS.iComponente == 2) { //Saca datos de las cajas fechas
				idValor = 1;
				descValor = Ext.getCmp(PF + 'txtFecIni').getValue();
				descValor = descValor + ',' + Ext.getCmp(PF + 'txtFecFin').getValue();
			}else if(NS.iComponente == 3) { //Saca datos de las cajas inicio final
				idValor = 1;
				descValor = Ext.getCmp(PF + 'txtInicial').getValue();
				descValor = descValor + ',' + Ext.getCmp(PF + 'txtFinal').getValue();
			}else if(NS.iComponente == 4) { //Saca datos de la caja valor
				idValor = 1;
				descValor = Ext.getCmp(PF + 'txtValor').getValue();
			}
			for(i=0; i<regStore.length; i++) {
				if(regStore[i].get('criterio') == descCriterio) {
					NS.modificarGrid(i, regStore, datosStore, idCriterio, descCriterio, idValor, descValor, 1);
					encontro = true;
				}
			}
			if(!encontro) NS.modificarGrid(x, regStore, datosStore, idCriterio, descCriterio, idValor, descValor, 0);
			NS.gridCriValor.getView().refresh();
		}
	};
	
	NS.modificarGrid = function(i, regStore, datosStore, idCriterio, descCriterio, idValor, descValor, modificar) {
		if(modificar == 1) NS.storeCriValor.remove(regStore[i]);
		
		var datos = new datosStore ({
			idCriterio: idCriterio,
			criterio: descCriterio,
			idValor: idValor, 
			valor: descValor
		});
		NS.gridCriValor.stopEditing();
		NS.storeCriValor.insert(i, datos);
	};
	
	NS.visible = function(combo, fechas, iniFin, valor) {
		Ext.getCmp(PF + 'cmbValor').setVisible(combo);
        Ext.getCmp(PF + 'txtFecIni').setVisible(fechas);
        Ext.getCmp(PF + 'txtFecFin').setVisible(fechas);
        Ext.getCmp(PF + 'txtInicial').setVisible(iniFin);
        Ext.getCmp(PF + 'txtFinal').setVisible(iniFin);
        Ext.getCmp(PF + 'txtValor').setVisible(valor);
        
        Ext.getCmp(PF + 'txtFecha').setReadOnly(true);
        Ext.getCmp(PF + 'txtCupoAutomatico').setReadOnly(true);
        Ext.getCmp(PF + 'txtMontoMaximo').setReadOnly(true);
        
        if(combo) NS.iComponente = 1;
		if(fechas) NS.iComponente = 2;
		if(iniFin) NS.iComponente = 3;
		if(valor) NS.iComponente = 4;
	};
	NS.visible(true, false, false, false);
	
	//Store para buscar los cupos
	NS.storeCupos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['gpoEmpresa', 'idDivisa', 'grupo', 'cveControl', 'bCambioMMF', 'idDivision'],
		directFn: ConfirmacionCargoCtaAction.selectCupos, 
		idProperty: 'cveControl',
		fields: [
			 {name: 'idGrupoFlujo'},
			 {name: 'montoMaximo'},
			 {name: 'cupoTotal'},
			 {name: 'cupoAutomatico'},
			 {name: 'fechaPropuesta'},
			 {name: 'cupoManual'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCupos, msg:"Cargando..."});
				
				var regStore = NS.storeCupos.data.items;
				
				for(var i=0; i<regStore.length; i++) {
					Ext.getCmp(PF + 'txtFecha').setValue(cambiarFechaDMA(regStore[i].get('fechaPropuesta')));
			        Ext.getCmp(PF + 'txtCupoAutomatico').setValue(NS.formatNumber(regStore[i].get('cupoAutomatico')));
			        Ext.getCmp(PF + 'txtMontoMaximo').setValue(NS.formatNumber(regStore[i].get('montoMaximo')));
			        Ext.getCmp(PF + 'txtFechaPago').setValue(cambiarFechaDMA(regStore[i].get('fechaPropuesta')));
			        Ext.getCmp(PF + 'txtDispPagar').setValue(NS.formatNumber(regStore[i].get('cupoTotal')));
			        Ext.getCmp(PF + 'txtCupoManual').setValue(NS.formatNumber(regStore[i].get('cupoManual')));   
				}
			}
		}
	});
	
	NS.buscaCupos = function(cveControl) {
		NS.storeCupos.baseParams.gpoEmpresa = NS.gpoEmpresa;
		NS.storeCupos.baseParams.idDivisa = NS.idDivisa;
		NS.storeCupos.baseParams.grupo = NS.grupo;
		NS.storeCupos.baseParams.cveControl = cveControl;
		NS.storeCupos.baseParams.bCambioMMF = false;
		NS.storeCupos.baseParams.idDivision = NS.idDivision;
		NS.storeCupos.load();
	};
	
	NS.buscarRegistro = function() {
		NS.storePagosPen.removeAll();
		NS.storePagosPro.removeAll();
		NS.gridPagosPen.getView().refresh();
		NS.gridPagosPro.getView().refresh();
		
		ConfirmacionCargoCtaAction.validaParametros(NS.gpoEmpresa, NS.idDivisa, NS.grupo, Ext.getCmp(PF + 'cmbCveControl').getValue(), 
				NS.idDivision, Ext.getCmp(PF + 'txtFecha').getValue(), function(resultado, e) {
			
			if(resultado != null && resultado != '')
				Ext.Msg.alert('SET', resultado + '!!');
			else {
				var jSonString = '';
				var totalPropuesto = 0;
				
				if(Ext.getCmp(PF + 'txtMontoTPro').getValue() != '') 
					totalPropuesto = parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtMontoTPro').getValue()));
				
				jSonString = NS.valorGridParam();
				
				NS.storePagosPen.baseParams.gpoEmpresa = NS.gpoEmpresa;
				NS.storePagosPen.baseParams.idDivisa = NS.idDivisa;
				NS.storePagosPen.baseParams.grupo = NS.grupo;
				NS.storePagosPen.baseParams.cveControl = Ext.getCmp(PF + 'cmbCveControl').getValue();
				NS.storePagosPen.baseParams.idDivision = NS.idDivision;
				NS.storePagosPen.baseParams.fecPropuesta = Ext.getCmp(PF + 'txtFecha').getValue();
				NS.storePagosPen.baseParams.chkServicio = NS.chkServicio;
				NS.storePagosPen.baseParams.chkPropuestos = NS.chkPropuestos;
				NS.storePagosPen.baseParams.bChequera = NS.chequera;
				NS.storePagosPen.baseParams.paramsGrid = jSonString;
				NS.storePagosPen.baseParams.totPropuesto = '' + totalPropuesto;
				NS.storePagosPen.baseParams.tipoGrid = 'pendientes';
				NS.storePagosPen.load();
				
				NS.storePagosPro.baseParams.gpoEmpresa = NS.gpoEmpresa;
				NS.storePagosPro.baseParams.idDivisa = NS.idDivisa;
				NS.storePagosPro.baseParams.grupo = NS.grupo;
				NS.storePagosPro.baseParams.cveControl = Ext.getCmp(PF + 'cmbCveControl').getValue();
				NS.storePagosPro.baseParams.idDivision = NS.idDivision;
				NS.storePagosPro.baseParams.fecPropuesta = Ext.getCmp(PF + 'txtFecha').getValue();
				NS.storePagosPro.baseParams.chkServicio = NS.chkServicio;
				NS.storePagosPro.baseParams.chkPropuestos = NS.chkPropuestos;
				NS.storePagosPro.baseParams.bChequera = NS.chequera;
				NS.storePagosPro.baseParams.paramsGrid = jSonString;
				NS.storePagosPro.baseParams.totPropuesto = '' + totalPropuesto;
				NS.storePagosPro.baseParams.tipoGrid = 'propuestos';
				NS.storePagosPro.load();
				
				if(NS.chkPropuestos)
					Ext.getCmp(PF + 'ejecutar').setDisabled(true);
				else
					Ext.getCmp(PF + 'ejecutar').setDisabled(false);
			}
		});
	};
	
	NS.sumaPagosPropuestos = function() {
		var regSelec = NS.gridPagosPro.getSelectionModel().getSelections();
		var totalProp = 0; 
		
		if(NS.idDivisa == '') NS.idDivisa = 'MN'; 
		
		for(var i=0; i<regSelec.length; i++) {
			if(NS.idDivisa == 'MN')
				totalProp = totalProp + regSelec[i].get('importeMn');
			else
				totalProp = totalProp + regSelec[i].get('importe');
		}
		Ext.getCmp(PF + 'txtMontoTPro').setValue(NS.formatNumber(totalProp));
	};
	
	NS.sumaPagosPendientes = function() {
		var regStore = NS.storePagosPen.data.items;
		var totalPen = 0; 
		
		if(NS.idDivisa == '') NS.idDivisa = 'MN'; 
		
		for(var i=0; i<regStore.length; i++) {
			if(NS.idDivisa == 'MN')
				totalPen = totalPen + regStore[i].get('importeMn');
			else
				totalPen = totalPen + regStore[i].get('importe');
		}
		Ext.getCmp(PF + 'txtMontoTPagosPen').setValue(NS.formatNumber(totalPen));
		Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtCupoManual').getValue())) - totalPen));
	};
	
	NS.limpiar = function() {
		NS.storePagosPen.removeAll();
		NS.gridPagosPen.getView().refresh();
		NS.storePagosPro.removeAll();
		NS.gridPagosPro.getView().refresh();
		Ext.getCmp(PF + 'txtMontoTPagosPen').reset();
		Ext.getCmp(PF + 'txtMontoTPro').reset();
		NS.cmbGpoEmpresas.reset();
		NS.cmbGpoRubros.reset();
		NS.cmbCveControl.reset();
		NS.cmbDivision.reset();
		Ext.getCmp(PF + 'chkPagos').setValue(false);
	};
	
	NS.ejecutar = function(){
		var totPropuesto = 0;
		
		if(Ext.getCmp(PF + 'txtMontoTPro').getValue() != '') totPropuesto = NS.unformatNumber(Ext.getCmp(PF + 'txtMontoTPro').getValue());
		
		ConfirmacionCargoCtaAction.validaDatos(parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtDispPagar').getValue())), 
				parseFloat(totPropuesto), function(result, e) {
			
			if(result != '' && result != null && resutl != undefined)
				Ext.Msg.alert('SET', result + '!!');
			else {
				var regSelect = NS.gridPagosPro.getSelectionModel().getSelections();
				var matReg = new Array();
				
				for(var i=0; i<regSelect.length; i++) {
					var regParams = {};
					regParams.noFolioDet = regSelect[i].get('noFolioDet');
					regParams.importeMn = regSelect[i].get('importeMn');
					
					matReg[i] = regParams;
				}
				var jSonString = Ext.util.JSON.encode(matReg);
				
				ConfirmacionCargoCtaAction.ejecutarDatos(jSonString, Ext.getCmp(PF + 'txtFechaPago').getValue(), 
						Ext.getCmp(PF + 'cmbCveControl').getValue(), parseFloat(NS.unformatNumber(Ext.getCmp(PF + 'txtCupoAutomatico').getValue())), 
						parseFloat(totPropuesto), NS.idDivisa, NS.grupo, function(result, e) {
					
					if(result != '' && result != null && resutl != undefined)
						Ext.Msg.alert('SET', result.msgUsuario + '!!');
				});
			}
		});
	};
	
	NS.agregar = function() {
		ConfirmacionCargoCtaAction.validaAgregarQuitar(NS.gpoEmpresa, NS.idDivisa, NS.grupo, Ext.getCmp(PF + 'cmbCveControl').getValue(), 
				NS.idDivision, Ext.getCmp(PF + 'txtFecha').getValue(), true, NS.chkPropuestos, NS.chkServicio, NS.chequera, function(result, e) {
			
			if(result != '' && result != null && resutl != undefined)
				Ext.Msg.alert('SET', result + '!!');
			else {
				var regStorePro = NS.storePagosPro.data.items;
				var totalPro = 0; 
				var cupoTotal = 0;
				var importe = 0;
				var i = 0;
				
				if(NS.idDivisa == '') NS.idDivisa = 'MN'; 
				
				for(i=0; i<regStorePro.length; i++) {
					if(NS.idDivisa == 'MN')
						totalPro = totalPro + regStorePro[i].get('importeMn');
					else
						totalPro = totalPro + regStorePro[i].get('importe');
				}
				if(Ext.getCmp(PF + 'txtDispPagar').getValue() != '') cupoTotal = Ext.getCmp(PF + 'txtDispPagar').getValue();
				
				var regSelec = NS.gridPagosPen.getSelectionModel().getSelections();
				var regStore = NS.storePagosPro.data.items;
				var gridPro = NS.gridPagosPro.getStore().recordType;
				var regStoreLen = regStore.length;
				
				for(i=0; i<regSelec.length; i++) {
					if(regSelec[i].get('idDivisa') == 'MN')
						importe = regSelec[i].get('importeMn');
					else
						importe = regSelec[i].get('importe');
					
					if((totalPro + importe) > cupoTotal) {
						Ext.Msg.alert('SET', 'El cupo total no permite seleccionar este movimiento!!');
						return;
					}
					var datos = new gridPro({
						noDocto: regSelec[i].get('noDocto'),
						nomEmpresa: regSelec[i].get('nomEmpresa'),
						nomEmpresa: regSelec[i].get('nomEmpresa'),
						importe: regSelec[i].get('importe'),
						noCliente: regSelec[i].get('noCliente'),
						noCliente: regSelec[i].get('noCliente'),
						beneficiario: regSelec[i].get('beneficiario'),
						idRubro: regSelec[i].get('idRubro'),
						idDivisa: regSelec[i].get('idDivisa'),
						fecValor: regSelec[i].get('fecValor'),
						importeMn: regSelec[i].get('importeMn'),
						fecPropuesta: regSelec[i].get('fecPropuesta'),
						idFormaPago: regSelec[i].get('idFormaPago'),
						descFormaPago: regSelec[i].get('descFormaPago'),
						fecModif: regSelec[i].get('fecModif'),
						noFolioDet: regSelec[i].get('noFolioDet'),
						idChequeraBenef: regSelec[i].get('idChequeraBenef'),
						noFactura: regSelec[i].get('noFactura'),
						concepto: regSelec[i].get('concepto'),
						idContable: regSelec[i].get('idContable'),
						totalPropuesto: regSelec[i].get('totalPropuesto')
					});
					NS.storePagosPen.remove(regSelec[i]);
					NS.gridPagosPro.stopEditing();
			  		NS.storePagosPro.insert(regStoreLen, datos);
			  		regStoreLen = regStoreLen + 1;
				}
				NS.gridPagosPen.getView().refresh();
		  		NS.gridPagosPro.getView().refresh();
		  		
				var regStorePen = NS.storePagosPen.data.items;
				var regStoreLenPen = regStorePen.length;
				
				NS.selecGridPen.selectRange(0, regStoreLenPen);
				NS.sumaPagosPendientes();
				NS.selecGridPro.selectRange(0, regStoreLen);
				NS.sumaPagosPropuestos();
			}
			
		});
	};
	
	NS.quitar = function() {
		ConfirmacionCargoCtaAction.validaParametros(NS.gpoEmpresa, NS.idDivisa, NS.grupo, Ext.getCmp(PF + 'cmbCveControl').getValue(), 
				NS.idDivision, Ext.getCmp(PF + 'txtFecha').getValue(), function(result, e) {
			
			if(result != '' && result != null && resutl != undefined)
				Ext.Msg.alert('SET', result + '!!');
			else {
				var regSelec = NS.gridPagosPro.getSelectionModel().getSelections();
				var regStore = NS.storePagosPen.data.items;
				var gridPen = NS.gridPagosPen.getStore().recordType;
				var regStoreLen = regStore.length;
				
				for(var i=0; i<regSelec.length; i++) {
					var datos = new gridPen({
						noDocto: regSelec[i].get('noDocto'),
						nomEmpresa: regSelec[i].get('nomEmpresa'),
						nomEmpresa: regSelec[i].get('nomEmpresa'),
						importe: regSelec[i].get('importe'),
						noCliente: regSelec[i].get('noCliente'),
						noCliente: regSelec[i].get('noCliente'),
						beneficiario: regSelec[i].get('beneficiario'),
						idRubro: regSelec[i].get('idRubro'),
						idDivisa: regSelec[i].get('idDivisa'),
						fecValor: regSelec[i].get('fecValor'),
						importeMn: regSelec[i].get('importeMn'),
						fecPropuesta: regSelec[i].get('fecPropuesta'),
						idFormaPago: regSelec[i].get('idFormaPago'),
						descFormaPago: regSelec[i].get('descFormaPago'),
						fecModif: regSelec[i].get('fecModif'),
						noFolioDet: regSelec[i].get('noFolioDet'),
						idChequeraBenef: regSelec[i].get('idChequeraBenef'),
						noFactura: regSelec[i].get('noFactura'),
						concepto: regSelec[i].get('concepto'),
						idContable: regSelec[i].get('idContable'),
						totalPropuesto: regSelec[i].get('totalPropuesto')
					});
					NS.storePagosPro.remove(regSelec[i]);
					NS.gridPagosPen.stopEditing();
			  		NS.storePagosPen.insert(regStoreLen, datos);
			  		regStoreLen = regStoreLen + 1;
				}
				NS.gridPagosPro.getView().refresh();
		  		NS.gridPagosPen.getView().refresh();
		  		
				var regStorePro = NS.storePagosPro.data.items;
				var regStoreLenPro = regStorePro.length;
				
				NS.selecGridPen.selectRange(0, regStoreLen);
				NS.sumaPagosPendientes();
				NS.selecGridPro.selectRange(0, regStoreLenPro);
				NS.sumaPagosPropuestos();
			}
		});
	};
	
	NS.valorGridParam = function() {
		var regGrid = NS.storeCriValor.data.items;
		var matRegEnviar = new Array ();
		var regParams = {};
		var jSonString = '';
		
		regParams.fechas = '';
		regParams.estatus = '';
		regParams.montos = '';
		regParams.bancoRecep = '';
		regParams.concepto = '';
		regParams.loteEntrada = '';
		regParams.division = '';
		regParams.formaPago = '';
		regParams.divisa = '';
		regParams.claveOpe = '';
		regParams.bloqueo = '';
		regParams.numProveedor = '';
		regParams.nomProveedor = '';
		regParams.factura = '';
		regParams.numPedido = '';
		regParams.origenMovto = '';
		regParams.rubroMovto = '';
		regParams.chequeraBenef = '';
		regParams.numDocto = '';
		regParams.bcoPagador = '';
		regParams.chequeraPaga = '';
		regParams.claseDocto = '';
		regParams.caja = '';
		
		for(var i=0; i<regGrid.length; i++) {
			switch (parseInt(regGrid[i].get('idCriterio'))) {
				case 1: //FECHAS
					var fechas = regGrid[i].get('valor');
					var fecIni = cambiarFecha(fechas.substring(0, fechas.indexOf(",")).trim());
					var fecFin = cambiarFecha(fechas.substring(fechas.indexOf(",") + 1).trim());
					
					regParams.fechas = fecIni + ',' + fecFin;
					break;
				case 2: //ESTATUS
					regParams.estatus = regGrid[i].get('valor');
					break;
				case 3: //MONTOS
					regParams.montos = regGrid[i].get('valor');
					break;
				case 4: //BANCO RECEPTOR
					regParams.bancoRecep = regGrid[i].get('idValor');
					break;
				case 5: //CONCEPTO
					regParams.concepto = regGrid[i].get('valor');
					break;
				case 6: //LOTE ENTRADA
					regParams.loteEntrada = regGrid[i].get('idValor');
					break;
				case 7: //DIVISION
					regParams.division = regGrid[i].get('idValor');
					break;
				case 8: //FORMA DE PAGO
					regParams.formaPago = regGrid[i].get('idValor');
					break;
				case 9: //DIVISA
					regParams.divisa = regGrid[i].get('idValor');
					break;
				case 10: //CLAVE OPERACION
					regParams.claveOpe = regGrid[i].get('idValor');
					break;
				case 11: //BLOQUEO
					regParams.bloqueo = regGrid[i].get('valor');
					break;
				case 12: //NUMERO DE PROVEEDOR
					regParams.numProveedor = regGrid[i].get('idValor');
					break;
				case 13: //NOMBRE PROVEEDOR
					regParams.nomProveedor = regGrid[i].get('valor');
					break;
				case 14: //FACTURA
					regParams.factura = regGrid[i].get('valor');
					break;
				case 15: //NUMERO DE PEDIDO
					regParams.numPedido = regGrid[i].get('idValor');
					break;
				case 16: //ORIGEN DEL MOVTO
					regParams.origenMovto = regGrid[i].get('idValor');
					break;
				case 17: //RUBRO DEL MOVIMIENTO
					regParams.rubroMovto = regGrid[i].get('idValor');
					break;
				case 18: //CHEQUERA BENEF.
					regParams.chequeraBenef = regGrid[i].get('valor');
					NS.chequera = true;
					break;
				case 19: //NUMERO DE DOCUMENTO
					regParams.numDocto = regGrid[i].get('valor');
					break;
				case 20: //BANCO PAGADOR
					regParams.bcoPagador = regGrid[i].get('idValor');
					break;
				case 21: //CHEQUERA PAGADORA
					regParams.chequeraPaga = regGrid[i].get('valor');
					break;
				case 22: //CLASE DE DOCUMENTO
					regParams.claseDocto = regGrid[i].get('valor');
					break;
				case 23: //CAJA
					regParams.caja = regGrid[i].get('idValor');
					break;
			}
		}
		matRegEnviar[i] = regParams;
		jSonString = Ext.util.JSON.encode(matRegEnviar);
		
		return jSonString;
	};
	
	NS.contPagoPropuestasAutomatico = new Ext.FormPanel( {
		title: 'Pago de Propuestas Automático',
	    width: 900,
	    height: 700,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            NS.contBusqueda,
	            NS.contGridPagos
	    ]
		});
	NS.contPagoPropuestasAutomatico.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
