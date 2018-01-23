Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	Ext.apply(Ext.form.VTypes,{
		adult: function(val, field){
			try{
				//alert(val.charCodeAt(val.length - 1));
				return (val.charCodeAt(val.length - 1) >= 65 && val.charCodeAt(val.length - 1) <= 90) || val.charCodeAt(val.length - 1) == 32;
			}catch(e){
				return false;
			}
		},
		adultText: 'You are underage!' //mensaje de error
		
	});
	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoComponentes.AltaComponentes');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;

	var PF = apps.SET.tabID + '.';
	// Generar prefijo para id html
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	
	

	NS.seleccionPantallas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	var textFieldTasas = new Ext.form.TextField({});
	NS.columnasPantallas = new Ext.grid.ColumnModel([
		NS.seleccionPantallas,
		{header: 'No. de componente', width: 120, dataIndex: 'idComponente', sortable: true},
		{header: 'Nombre', width: 80, dataIndex: 'descripcion', sortable: true},
		{header: 'Tipo componente', width: 100, dataIndex: 'idTipoComponente', sortable: true},
		{header: 'Estatus', width: 80, dataIndex: 'estatus', sortable: true},
		{header: 'Componente Padre', width: 120, dataIndex: 'idComponentePadre', sortable: true},
		{header: 'Etiqueta', width: 80, dataIndex: 'etiqueta', sortable: true, editor: textFieldTasas},
		{header: 'URL', width: 80, dataIndex: 'url'}
	]);

	NS.storeLlenaComponetes = new Ext.data.DirectStore({
		paramAsHash: false,
		baseParams: {
			tipoComponente:1
		},
		directFn: SegComponentesAction.llenaComponentes,
		fields: [
			{name: 'idComponente'},
			{name: 'descripcion'},
			{name: 'idTipoComponente'},
			{name: 'estatus'},
			{name: 'idComponentePadre'},
			{name: 'etiqueta', type: 'number'},
			{name: 'url'}
		]
	});

	NS.storeLlenaComponetes.load();

	NS.gridPantallas = new Ext.grid.GridPanel({
		store: NS.storeLlenaComponetes,
		id: 'gridPantallas',
		cm: NS.columnasPantallas,
		sm: NS.seleccionPantallas,
		x: 10,
		y: 280,
		width: 1000,
		height: 200,
		stripeRows: true,
		columnLines: true,
		title: 'Componentes Actuales:'
	});

	NS.cmbTipos = new Ext.form.ComboBox({
		id: PF + 'cmbTipos',
		forceSelection: true,
		store: ['1', '2', '3'],
		emptyText:'Seleccione el tipo',
		triggerAction: 'all',
		editable:false,
		x: 110,
		y: 30,
		width: 150					
	});

	NS.cmbEstatus = new Ext.form.ComboBox({
		id: PF + 'cmbEstatus',
		forceSelection: true,
		store: ['A', 'I', 'X'],
		emptyText:'Seleccione el estatus',
		triggerAction: 'all',
		editable:false,
		x: 50,
		y: 60,
		width: 150					
	});
	
//	NS.BotonFacultativo = function(boton) {
//		NS.verificarFacultad(boton);
//		return boton;
//	};
//	
//	NS.verificarFacultad = function(boton) {
//		CoinversionAction.verificar(boton.id, function(result, e) {
//			if(result){
//				boton.show();
//			}else{
//				boton.hide();
//			}
//		});
//		boton.hide();
//	};
	
	NS.btn = apps.SET.btnFacultativo(new Ext.Button({
		text: 'Reporte',
		id: 'mrs',
		x: 400, 
		y: 0,
		width: 100,
		height: 22,
		listeners:
		{
			click:
			{
				fn: function (e) {
					alert("fd");
					strParams = '?nomReporte=prueba';
					strParams += '&'+'nomParam1=parametroPrueba';
					strParams += '&'+'valParam1=prueba';
					
					//window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
					window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
				}
			}
		}
	}));
	
//	NS.MyBtn = Ext.extend(Ext.Button, {
//		usuario: "x",
//		pantalla: "y",
//		constructor		: function(options){				//Strep 2
//			NS.MyBtn.superclass.constructor.call(this);//Step 3
//			//this.hidden = NS.verificar(this.usuario, this.pantalla);
//		}
//	});
//	
//	NS.btn = new NS.MyBtn({
//		text: 'Reporte',
//		x: 400, 
//		y: 0,
//		width: 100,
//		height: 22,
//		usuario: "x",
//		pantalla: "y",
//		listeners:
//		{
//			click:
//			{
//				fn: function (e) {
//					alert("fd");
//					strParams = '?nomReporte=prueba';
//					strParams += '&'+'nomParam1=parametroPrueba';
//					strParams += '&'+'valParam1=prueba';
//					
//					//window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
//					window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
//				}
//			}
//		}
//	});
	
//	NS.btn = new Ext.Button({
//		text: 'Reporte',
//		x: 400, 
//		y: 0,
//		width: 100,
//		height: 22,
//		listeners:
//		{
//			click:
//			{
//				fn: function (e) {
//					strParams = '?nomReporte=prueba';
//					strParams += '&'+'nomParam1=parametroPrueba';
//					strParams += '&'+'valParam1=prueba';
//					
//					//window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
//					window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
//				}
//			}
//		}
//	});
	
	

	NS.panelPrincipal = new Ext.FormPanel({
		title: 'Alta de componentes',
		width: 1020,
		height: 600,
		frame: true,
		layout: 'absolute',
		renderTo: NS.tabContId,
		items: [
			NS.gridPantallas,
			{
				xtype: 'fieldset',
				title: 'Registro:',
				x: 5,
				y: 0,
				width: Ext.get(NS.tabContId).getWidth()-20,
				height: 275,
				layout: 'absolute',
				items: [
					{
						xtype: 'label',
						x: 0,
						y: 2,
						text: 'Descripcion:'
					},
					{
						xtype: 'textfield',
						vtype:'adult',
						id: PF+'txtDescripcion',
						name: PF+'txtDescripcion',
						x: 65,
						y: 0,
						width: 150
					},
					{
						xtype: 'label',
						text: 'Tipo de componente:',
						x: 0,
						y: 32
					},
					NS.cmbTipos,
					{
						xtype: 'label',
						text: 'Estatus:',
						x: 0,
						y: 62
					},
					NS.cmbEstatus,
					NS.btn
//					{
//						xtype: 'button',
//						text: 'Reporte',
//						x: 400, 
//						y: 0,
//						width: 100,
//						height: 22,
//						listeners:
//						{
//							click:
//							{
//								fn: function (e) {
//									strParams = '?nomReporte=prueba';
//									strParams += '&'+'nomParam1=parametroPrueba';
//									strParams += '&'+'valParam1=prueba';
//									
//									//window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
//									window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
//								}
//							}
//						}
//					}
				]
			}
		]
	});

	NS.panelPrincipal.setSize(
		Ext.get(NS.tabContId).getWidth(), 
		Ext.get(NS.tabContId).getHeight());
	NS.gridPantallas.setWidth(
		NS.panelPrincipal.getWidth()-20);
});
