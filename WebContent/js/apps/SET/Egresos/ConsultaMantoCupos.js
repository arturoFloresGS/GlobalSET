/*
 * @author: Victor H. Tello
 * @since: 27/Mar/2012
 */
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Egresos.ConsultaMantoCupos');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.insertar = false;
	NS.esDivision = false;
	NS.cveControl = '';
	
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
	
	//Etiqueta Grupo empresa
	NS.labGpoEmpresa = new Ext.form.Label({
		text: 'Grupo Empresa:',
        x: 10
	});
	//Store Grupo empresas
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
		value: ''
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
	    width: 180,
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
		tabIndex: 3
	});
	//Etiqueta Fecha de Propuesta
	NS.labFecPropuesta = new Ext.form.Label({
		text: 'Fecha de Propuesta:',
		x: 10,
		y: 50
	});
	//Fecha inicial
	NS.txtFechaIni = new Ext.form.DateField({
		id: PF + 'txtFechaIni',
		name: PF + 'txtFechaIni',
		x: 10,
		y: 65,
		width: 110,
		format: 'd/m/Y',
		value : NS.fecIni
	});
	//Fecha Final
	NS.txtFechaFin = new Ext.form.DateField({
		id: PF + 'txtFechaFin',
		name: PF + 'txtFechaFin',
		x: 130,
		y: 65,
		width: 110,
		format: 'd/m/Y',
		value: NS.fecHoy
	});
	//Radio Button Cupos
	NS.optCupos = new Ext.form.RadioGroup({
		id: PF + 'optCupos',
		name: PF + 'optCupos',
		x: 10,
		columns: 3, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Vigentes', name: 'optCupos', inputValue: 0, checked: true},  
	          {boxLabel: 'Todos', name: 'optCupos', inputValue: 1},
	          {boxLabel: 'X Fechas', name: 'optCupos', inputValue: 2}
	     ]
	});
	//Contenedor de Radio Button SI - NO
	NS.contCupos = new Ext.form.FieldSet({
		title: 'Cupos',
		x: 550,
		width: 300,
		height: 60,
		layout: 'absolute',
		items: [
		    NS.optCupos
		]
	});
	//Contenedor de Busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: 'búsqueda',
		width: 985,
	    height: 125,
	    layout: 'absolute',
	    items:[
	        NS.labGpoEmpresa,	NS.cmbGpoEmpresas,
	        NS.labDivision,		NS.cmbDivision,
            NS.labFecPropuesta,	NS.txtFechaIni,		NS.txtFechaFin,
            NS.contCupos,
            {
	        	xtype: 'button',
	        	text: 'Buscar',
	        	x: 870,
	        	y: 15,
	        	width: 80,
	        	height: 22,
	        	listeners: {
	        		click:{
	        			fn:function(e){
	        				NS.buscar();
	        				NS.limpiar(0, 2);
	        				NS.desHabilitar(true, true);
	        			}
	        		}
	        	}
	        }
	    ]
	});
	//Store Pagos donde se muestran los valores seleccionados
	NS.storeCupos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['optCupos', 'grupoEmpresa', 'fecIni', 'fecFin', 'idDivision'],
		baseParams: {},
		directFn:  ConfirmacionCargoCtaAction.buscarCupos, 
		fields: [
		         {name: 'descGrupoFlujo'},
		         {name: 'montoMaximo'},
				 {name: 'cupoManual'},
				 {name: 'cupoAutomatico'},
				 {name: 'descGrupoCupo'},
				 {name: 'fechaPropuesta'},
				 {name: 'idGrupo'},
				 {name: 'fecLimiteSelecc'},
				 {name: 'cveControl'},
				 {name: 'idGrupoFlujo'},
				 {name: 'concepto'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCupos, msg: "Buscando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen registros con estos parametro de busqueda!!');
			}
		}
	}); 
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.selecCupos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	//Columna de Seleccion  
	NS.columCupos = new Ext.grid.ColumnModel([
	    NS.selecCupos,
	    {header: 'Gpo Empresa/División', width: 100, dataIndex: 'descGrupoFlujo', sortable: true},
	    {header: 'Monto máximo de factura', width: 100, dataIndex: 'montoMaximo', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Cupo Manual', width: 100, dataIndex: 'cupoManual', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Cupo Automática', width: 100, dataIndex: 'cupoAutomatico', sortable: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'Grupo de Rubros', width: 100, dataIndex: 'descGrupoCupo', sortable: true},
	    {header: 'Fecha de Pago', width: 100, dataIndex: 'fechaPropuesta', sortable: true},
	    {header: 'Id Grupo', width: 80, dataIndex: 'idGrupo', sortable: true, hidden:true},
	    {header: 'Fec. Límite Selecc.', width: 100, dataIndex: 'fecLimiteSelecc', sortable: true},
	    {header: 'Cve. Control', width: 100, dataIndex: 'cveControl', sortable: true},
	    {header: 'Id Grupo Flujo', width: 60, dataIndex: 'idGrupoFlujo', sortable: true, hidden: true},
	    {header: 'Observaciones', width: 140, dataIndex: 'concepto', sortable: true},
	]);
	//Grid para mostrar los datos seleccionados
	NS.gridCupos = new Ext.grid.GridPanel({
		store: NS.storeCupos,
		id: 'gridCupos',
		x: 5,
		cm: NS.columCupos,
		sm: NS.selecCupos,
		width: 960,
	    height: 200,
	    stripeRows: true,
	    columnLines: true,
	    title: 'Pagos',
		listeners: {
			click: {
				fn:function(grid) {
					var regSelect = NS.gridCupos.getSelectionModel().getSelections();
					NS.desHabilitar(false, false);
					
					for(var i=0; i<regSelect.length; i++) {
	      				Ext.getCmp(PF + 'cmbGpoEmpresasNvo').setValue(regSelect[i].get('descGrupoFlujo'));
	      				Ext.getCmp(PF + 'cmbDivisionNvo').setValue(regSelect[i].get('descGrupoFlujo'));
	      				Ext.getCmp(PF + 'txtFecPago').setValue(cambiarFecha(regSelect[i].get('fechaPropuesta')));
	      				Ext.getCmp(PF + 'txtFecLimitSelec').setValue(cambiarFecha(regSelect[i].get('fecLimiteSelecc')));
	      				Ext.getCmp(PF + 'txtCupoManual').setValue(NS.formatNumber(regSelect[i].get('cupoManual')));
	      				Ext.getCmp(PF + 'txtCupoAuto').setValue(NS.formatNumber(regSelect[i].get('cupoAutomatico')));
	      				Ext.getCmp(PF + 'txtMontoMaxFact').setValue(NS.formatNumber(regSelect[i].get('montoMaximo')));
	      				Ext.getCmp(PF + 'cmbGpoRubros').setValue(regSelect[i].get('descGrupoCupo'));
	      				Ext.getCmp(PF + 'txtConcepto').setValue(regSelect[i].get('concepto'));
	      				NS.cveControl = regSelect[i].get('cveControl');
	      			}
				}
			}
		}
	});
	//Contenedor Grid
	NS.contGrid = new Ext.form.FieldSet({
		y: 135,
		width: 985,
		height: 260,
		layout: 'absolute',
		items: [
		        NS.gridCupos,
		        {
		        	xtype: 'button',
		        	text: 'Nuevo',
		        	x: 870,
		        	y: 210,
		        	width: 80,
		        	height: 22,
		        	listeners: {
		        		click:{
		        			fn:function(e){
		        				NS.limpiar(0, 2);
		        				NS.desHabilitar(false, true);
		        				NS.insertar = true;
		        				NS.nuevo();
		        			}
		        		}
		        	}
		        },{
		        	xtype: 'button',
		        	text: 'Eliminar',
		        	x: 780,
		        	y: 210,
		        	width: 80,
		        	height: 22,
		        	listeners: {
		        		click:{
		        			fn:function(e){
		        				NS.eliminar();
		        				NS.limpiar(0, 2);
		        				NS.desHabilitar(true, true);
		        				NS.buscar();
		        			}
		        		}
		        	}
		        }
		        ]
	});
	//Etiqueta Grupo empresas del boton nuevo
	NS.labGpoEmpresaNvo = new Ext.form.Label({
		text: 'Grupo Empresa:',
        x: 10
	});
	//Store Grupo empresas del boton nuevo
    NS.storeGpoEmpresasNvo = new Ext.data.DirectStore({
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
	NS.storeGpoEmpresasNvo.load();
	
	//Combo Grupo empresas del boton nuevo
	NS.cmbGpoEmpresasNvo = new Ext.form.ComboBox({
		store: NS.storeGpoEmpresasNvo,
		id: PF + 'cmbGpoEmpresasNvo',
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
						NS.agregar();
					}
				}
			}
		}
	});
	//Etiqueta Division
	NS.labDivisionNvo = new Ext.form.Label({
		text: 'Division:',
		x: 360
	});
	//Store Division
	NS.storeDivisionNvo = new Ext.data.DirectStore({
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
			}
		}
	});
	NS.storeDivision.load();
	
	//Combo banco
	NS.cmbDivisionNvo = new Ext.form.ComboBox({
		store: NS.storeDivisionNvo,
		id: PF + 'cmbDivisionNvo',
		name: PF + 'cmbDivisionNvo',
		x: 360,
	    y: 15,
	    width: 200,
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
						NS.agregar();
					}
				}
			}
		}
	});
	//Etiqueta Fecha de Pago
	NS.labFecPago = new Ext.form.Label({
		text: 'Fecha Pago:',
		x: 590
	});
	//Fecha Final
	NS.txtFecPago = new Ext.form.DateField({
		id: PF + 'txtFecPago',
		name: PF + 'txtFecPago',
		x: 590,
		y: 15,
		width: 110,
		format: 'd/m/Y',
		value: NS.fecHoy,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						NS.agregar();
					}
				}
			}
		}
	});
	//Etiqueta Fecha limite de seleccion
	NS.labFecLimitSelec = new Ext.form.Label({
		text: 'Fec. Limite Selección:',
		x: 730
	});
	//Fecha Limite de Seleccion
	NS.txtFecLimitSelec = new Ext.form.DateField({
		id: PF + 'txtFecLimitSelec',
		name: PF + 'txtFecLimitSelec',
		x: 730,
		y: 15,
		width: 130,
		format: 'd/m/Y',
		value : NS.fecHoy,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						NS.agregar();
					}
				}
			}
		}
	});
	//Etiqueta Cupo manual
	NS.labCupoManual = new Ext.form.Label({
		text: 'Cupo Manual:',
		x: 10,
		y: 50
	});
	NS.txtCupoManual = new Ext.form.TextField({
		id: PF + 'txtCupoManual',
		name: PF + 'txtCupoManual',
		x: 10,
		y: 65,
		width: 110,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						Ext.getCmp(PF + 'txtCupoManual').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtCupoManual').getValue()));
						
						if(NS.insertar)
							NS.agregar();
						else
							NS.modificar();
					}
				}
			}
		}
	});
	NS.labCupoAuto = new Ext.form.Label({
		text: 'Cupo Automatico:',
		x: 140,
		y: 50
	});
	NS.txtCupoAuto = new Ext.form.TextField({
		id: PF + 'txtCupoAuto',
		name: PF + 'txtCupoAuto',
		x: 140,
		y: 65,
		width: 110,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						Ext.getCmp(PF + 'txtCupoAuto').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtCupoAuto').getValue()));
						
						if(NS.insertar)
							NS.agregar();
						else
							NS.modificar();
					}
				}
			}
		}
	});
	NS.labMontoMaxFact = new Ext.form.Label({
		text: 'Monto Max. Facturas:',
		x: 270,
		y: 50
	});
	NS.txtMontoMaxFact = new Ext.form.TextField({
		id: PF + 'txtMontoMaxFact',
		name: PF + 'txtMontoMaxFact',
		x: 270,
		y: 65,
		width: 130,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						Ext.getCmp(PF + 'txtMontoMaxFact').setValue(NS.formatNumber(Ext.getCmp(PF + 'txtMontoMaxFact').getValue()));
						
						if(NS.insertar)
							NS.agregar();
						else
							NS.modificar();
					}
				}
			}
		}
	});
	//Etiqueta Grupo rubros
	NS.labGpoRubros = new Ext.form.Label({
		text: 'Grupo Rubros:',
		x: 420,
		y: 50
	});
	//Store GpoRubros
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
		x: 420,
	    y: 65,
	    width: 180,
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
					if(combo.getValue() != null && combo.getValue() != '' && combo.getValue() != undefined)
						NS.agregar();
				}
			}
		}
	});
	//Etiqueta concepto
	NS.labConcepto = new Ext.form.Label({
		text: 'Concepto:',
		x: 620,
		y: 50
	});
	NS.txtConcepto = new Ext.form.TextField({
		id: PF + 'txtConcepto',
		name: PF + 'txtConcepto',
		x: 620,
		y: 65,
		width: 340,
		listeners: {
			change: {
				fn: function(caja, valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						if(NS.insertar)
							NS.agregar();
						else
							NS.modificar();
					}
				}
			}
		}
	});
	//Contenedor de Busqueda
	NS.contNvo = new Ext.form.FieldSet({
		y: 405,
		width: 985,
	    height: 115,
	    layout: 'absolute',
	    items:[
			NS.labGpoEmpresaNvo,	NS.cmbGpoEmpresasNvo,
			NS.labDivisionNvo,		NS.cmbDivisionNvo,
	        NS.labFecPago,			NS.txtFecPago,
	        NS.labFecLimitSelec,	NS.txtFecLimitSelec,
	        NS.labCupoManual,		NS.txtCupoManual,
	        NS.labCupoAuto,			NS.txtCupoAuto,
	        NS.labMontoMaxFact,		NS.txtMontoMaxFact,
	        NS.labGpoRubros,		NS.cmbGpoRubros,
	        NS.labConcepto,			NS.txtConcepto
	    ]
	});
	//Contenedor General
	NS.contGeneral = new Ext.form.FieldSet({
		width: 1010,
	    height: 575,
	    x: 20,
	    y: 5,
	    layout: 'absolute',
	    items:[
			NS.contBusqueda,
			NS.contGrid,
			NS.contNvo,
			{
	        	xtype: 'button',
	        	text: 'Aceptar',
	        	x: 610,
	        	y: 530,
	        	width: 80,
	        	height: 22,
	        	listeners: {
	        		click:{
	        			fn:function(e){
							NS.aceptar();
	        			}
	        		}
	        	}
	        },{
	        	xtype: 'button',
	        	text: 'Cancelar',
	        	x: 700,
	        	y: 530,
	        	width: 80,
	        	height: 22,
	        	listeners: {
	        		click:{
	        			fn:function(e){
	        				NS.limpiar(0, 0);
	        				NS.desHabilitar(true, true);
	        				NS.buscar();
	        			}
	        		}
	        	}
	        },{
			    xtype: 'button',
			    text: 'Imprimir',
			    x: 790,
			    y: 530,
			    width: 80,
			    height: 22
			},{
			    xtype: 'button',
			    text: 'Limpiar',
			    x: 880,
			    y: 530,
			    width: 80,
			    height: 22,
	        	listeners: {
	        		click:{
	        			fn:function(e){
	        				NS.limpiar(1, 2);
	        				NS.desHabilitar(true, true);
	        			}
	        		}
	        	}
			}
		]
	});
	//Funcion para buscar los registros
	NS.buscar = function() {
		var iCupos = Ext.getCmp(PF + 'optCupos').getValue();
		iCupos  = parseInt(iCupos.getGroupValue());
		var fecIni = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
		var fecFin = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
		
		NS.storeCupos.removeAll();
		NS.gridCupos.getView().refresh();
		
		ConfirmacionCargoCtaAction.validarParametros(iCupos, '' + Ext.getCmp(PF + 'cmbGpoEmpresas').getValue(), 
				fecIni, fecFin, '' + Ext.getCmp(PF + 'cmbDivision').getValue(), function(respuesta, e) {
			
			if(respuesta != null && respuesta != undefined && respuesta != '') {
				Ext.Msg.alert('SET', '' + respuesta + '!!');
			}else {
				NS.storeCupos.baseParams.optCupos = iCupos;
				NS.storeCupos.baseParams.grupoEmpresa = '' + Ext.getCmp(PF + 'cmbGpoEmpresas').getValue();
				NS.storeCupos.baseParams.fecIni = fecIni;
				NS.storeCupos.baseParams.fecFin = fecFin;
				NS.storeCupos.baseParams.idDivision = '' + Ext.getCmp(PF + 'cmbDivision').getValue();
				NS.storeCupos.load();
			}
		});
	};
	
	NS.limpiar = function(opcion, opcion2) {
		if(opcion == 1) {
			Ext.getCmp(PF + 'cmbGpoEmpresas').reset();
			Ext.getCmp(PF + 'cmbDivision').reset();
			Ext.getCmp(PF + 'txtFechaIni').setValue(NS.fecHoy);
			Ext.getCmp(PF + 'txtFechaFin').setValue(NS.fecHoy);
		}
		if(opcion2 == 2) {
			NS.storeCupos.removeAll();
			NS.gridCupos.getView().refresh();
		}
		Ext.getCmp(PF + 'cmbGpoEmpresasNvo').reset();
		Ext.getCmp(PF + 'cmbDivisionNvo').reset();
		Ext.getCmp(PF + 'txtFecPago').reset();
		Ext.getCmp(PF + 'txtFecLimitSelec').reset();
		Ext.getCmp(PF + 'txtCupoManual').reset();
		Ext.getCmp(PF + 'txtCupoAuto').reset();
		Ext.getCmp(PF + 'txtMontoMaxFact').reset();
		Ext.getCmp(PF + 'cmbGpoRubros').reset();
		Ext.getCmp(PF + 'txtConcepto').reset();
		NS.insertar = false;
	};
	
	NS.desHabilitar = function(valor, opcion) {
		if(opcion) {
			Ext.getCmp(PF + 'cmbGpoEmpresasNvo').setReadOnly(valor);
			Ext.getCmp(PF + 'cmbDivisionNvo').setReadOnly(valor);
			Ext.getCmp(PF + 'txtFecPago').setReadOnly(valor);
			Ext.getCmp(PF + 'txtFecLimitSelec').setReadOnly(valor);
			Ext.getCmp(PF + 'cmbGpoRubros').setReadOnly(valor);
		}
		Ext.getCmp(PF + 'txtCupoManual').setReadOnly(valor);
		Ext.getCmp(PF + 'txtCupoAuto').setReadOnly(valor);
		Ext.getCmp(PF + 'txtMontoMaxFact').setReadOnly(valor);
		Ext.getCmp(PF + 'txtConcepto').setReadOnly(valor);
	};
	NS.desHabilitar(true, true);
	
	NS.nuevo = function() {
		var datosGrid = NS.gridCupos.getStore().recordType;
		var recordCupos = NS.storeCupos.data.items;
		var i = recordCupos.length;
		
		var datos = new datosGrid({
			descGrupoFlujo: '',
			montoMaximo: '',
			cupoManual: '',
			cupoAutomatico: '',
			descGrupoCupo: '',
			fechaPropuesta: '',
			idGrupo: '',
			fecLimiteSelecc: '',
			cveControl: '',
			idGrupoFlujo: '',
			concepto: ''
      	});
		NS.gridCupos.stopEditing();
  		NS.storeCupos.insert(i, datos);
  		NS.gridCupos.getView().refresh();
	};
	
	NS.agregar = function() {
		var datosGrid = NS.gridCupos.getStore().recordType;
		var recCupos = NS.storeCupos.data.items;
		var i = recCupos.length;
		var idGrupoFlujo = Ext.getCmp(PF + 'cmbGpoEmpresasNvo').getValue();
		var descGrupoFlujo = "";
		var idGrupo = Ext.getCmp(PF + 'cmbGpoRubros').getValue();
		var idDivision = Ext.getCmp(PF + 'cmbDivision').getValue();
		
		if(idGrupoFlujo != '') {
			NS.esDivision = false;
			descGrupoFlujo = NS.storeGpoEmpresasNvo.getById(idGrupoFlujo).get('descGrupoFlujo');
		}else if(idDivision != '') {
			idGrupoFlujo = idDivision; 
			descGrupoFlujo = NS.storeGpoEmpresasNvo.getById(idDivision).get('descDivision');
			NS.esDivision = true;
		}
		
		if(idGrupo != '') 
			idGrupo = NS.storeGpoRubros.getById(idGrupo).get('descGrupoCupo');
		
		NS.storeCupos.remove(recCupos[i-1]);
		
		var datos = new datosGrid({
			descGrupoFlujo: descGrupoFlujo,
			montoMaximo: NS.unformatNumber(Ext.getCmp(PF + 'txtMontoMaxFact').getValue()),
			cupoManual: NS.unformatNumber(Ext.getCmp(PF + 'txtCupoManual').getValue()),
			cupoAutomatico: NS.unformatNumber(Ext.getCmp(PF + 'txtCupoAuto').getValue()),
			descGrupoCupo: idGrupo,
			fechaPropuesta: Ext.getCmp(PF + 'txtFecPago').getValue(),
			idGrupo: Ext.getCmp(PF + 'cmbGpoRubros').getValue(),
			fecLimiteSelecc: Ext.getCmp(PF + 'txtFecLimitSelec').getValue(),
			cveControl: '',
			idGrupoFlujo: idGrupoFlujo,
			concepto: Ext.getCmp(PF + 'txtConcepto').getValue()
      	});
		NS.gridCupos.stopEditing();
  		NS.storeCupos.insert(i-1 , datos);
  		NS.gridCupos.getView().refresh();
	};

	NS.aceptar = function() {
		var datos = NS.storeCupos.data.items;
		var matDatos = new Array();
		var resp = '';
		
		for(var i=0; i<datos.length; i++) {
			var regDatos = {};
			regDatos.descGrupoFlujo = datos[i].get('descGrupoFlujo'),
			regDatos.montoMaximo = datos[i].get('montoMaximo'),
			regDatos.cupoManual = datos[i].get('cupoManual'),
			regDatos.cupoAutomatico = datos[i].get('cupoAutomatico'),
			regDatos.descGrupoCupo = datos[i].get('descGrupoCupo'),
			regDatos.fechaPropuesta = cambiarFecha('' + datos[i].get('fechaPropuesta')),
			regDatos.idGrupo = datos[i].get('idGrupo'),
			regDatos.fecLimiteSelecc = cambiarFecha('' + datos[i].get('fecLimiteSelecc')),
			regDatos.cveControl = datos[i].get('cveControl'),
			regDatos.idGrupoFlujo = datos[i].get('idGrupoFlujo'),
			regDatos.concepto = datos[i].get('concepto')
			
			matDatos[i] = regDatos;
		}
		var jSonString = Ext.util.JSON.encode(matDatos);
		
		ConfirmacionCargoCtaAction.validarParametrosInsert(jSonString, NS.insertar, function(respuesta, e) {
			if(respuesta != null && respuesta != undefined && respuesta != '')
				Ext.Msg.alert('SET', '' + respuesta + '!!');
			else {
				ConfirmacionCargoCtaAction.ejecutarAltaRegistro(jSonString, NS.insertar, NS.esDivision, function(respuesta, e) {
					if(respuesta != null && respuesta != undefined && respuesta != '') {
						Ext.Msg.alert('SET', '' + respuesta.msgUsuario + '!!');
						NS.limpiar(0, 0);
						NS.desHabilitar(true, true);
					}
				});
			}
		});
	};
	
	NS.modificar = function() {
		var regSelec = NS.gridCupos.getSelectionModel().getSelections();
		var datosGrid = NS.gridCupos.getStore().recordType;
		var recCupos = NS.storeCupos.data.items;
		
		for(var i=0; i<recCupos.length; i++) {
			if(recCupos[i].get('cveControl') == NS.cveControl) {
				var datos = new datosGrid({
					descGrupoFlujo: recCupos[i].get('descGrupoFlujo'),
					montoMaximo: NS.unformatNumber(Ext.getCmp(PF + 'txtMontoMaxFact').getValue()),
					cupoManual: NS.unformatNumber(Ext.getCmp(PF + 'txtCupoManual').getValue()),
					cupoAutomatico: NS.unformatNumber(Ext.getCmp(PF + 'txtCupoAuto').getValue()),
					descGrupoCupo: recCupos[i].get('descGrupoCupo'),
					fechaPropuesta: recCupos[i].get('fechaPropuesta'),
					idGrupo: recCupos[i].get('idGrupo'),
					fecLimiteSelecc: recCupos[i].get('fecLimiteSelecc'),
					cveControl: recCupos[i].get('cveControl'),
					idGrupoFlujo: recCupos[i].get('idGrupoFlujo'),
					concepto: Ext.getCmp(PF + 'txtConcepto').getValue()
		      	});
				NS.storeCupos.remove(recCupos[i]);
				NS.gridCupos.stopEditing();
		  		NS.storeCupos.insert(i , datos);
		  		NS.gridCupos.getView().refresh();
			}
		}
	};
	
	NS.eliminar = function() {
		var regEliminar = NS.gridCupos.getSelectionModel().getSelections();
		var matDatos = new Array();
		var resp = '';
		
		for(var i=0; i<regEliminar.length; i++) {
			var regDatos = {};
			regDatos.cveControl = regEliminar[i].get('cveControl'),
			regDatos.idGrupoFlujo = regEliminar[i].get('idGrupoFlujo'),
			matDatos[i] = regDatos;
		}
		var jSonString = Ext.util.JSON.encode(matDatos);
		
		ConfirmacionCargoCtaAction.eliminarRegistro(jSonString, function(respuesta, e) {
			if(respuesta != null && respuesta != undefined && respuesta != '') {
				Ext.Msg.alert('SET', '' + respuesta.msgUsuario + '!!');
				NS.limpiar(0, 2);
				NS.desHabilitar(true, true);
				NS.buscar();
			}
		});
	};
	
	NS.contConsultaMantoCupos = new Ext.FormPanel( {
		title: 'Mantenimiento de Cupos',
	    width: 900,
	    height: 700,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            NS.contGeneral
	    ]
		});
	NS.contConsultaMantoCupos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
