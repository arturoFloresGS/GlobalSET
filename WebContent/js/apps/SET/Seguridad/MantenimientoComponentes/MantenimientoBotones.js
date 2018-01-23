Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	
	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoComponentes.MantenimientoBotones');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;

	var PF = apps.SET.tabID + '.';
	// Generar prefijo para id html
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	
	NS.bModificar = false;
	NS.idComponente = null;
	
	NS.storePantallas = new Ext.data.DirectStore({
    	paramsAsHash: false,
  		root: '',
		paramOrder:['idModulo'],
		directFn: SegComponentesAction.obtenerPantallas,
		idProperty: 'idComponente',
		fields: [
			{name: 'idComponente'},
			{name: 'etiqueta'},
			{name: 'estatus'}
		],
	    listeners: {
			load: function(s, records){
//				if (records.length != null && records.length > 0) {
//					if (NS.idComponente != -1 && NS.storePantallas.getById(NS.idComponente) != undefined) {
//						NS.cmbPadres.setValue(NS.idComponente);
//					} else {
//						NS.cmbPadres.setValue(NS.cmbModulo.getValue());
//						NS.idComponente = NS.cmbModulo.getValue();
//					}					 
//				}	
			}
		}
    });
    
    // combo de busqueda descripcion
    NS.cmbPantallas = new Ext.form.ComboBox({
        store: NS.storePantallas,
        name: PF+'cmbPantallas',
        id: PF+'cmbPantallas',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 475,
		y: 20,
        width: 180,
        valueField: 'idComponente',
        displayField: 'etiqueta',
        autocomplete: true,
        emptyText: 'Seleccione la pantalla',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
                }
            }
        }
    });
	
	NS.storeModulo = new Ext.data.DirectStore({
    	paramsAsHash: false,
        baseParams: {
			tipoComponente:1
		},
		root: '',
		paramOrder:['tipoComponente'],
		directFn: SegComponentesAction.llenaComponentes,
		idProperty: 'idComponente',
		fields: [
			{name: 'idComponente'},
			{name: 'etiqueta'},
			{name: 'estatus'},
			{name: 'rutaImagen'}
		]
    });
    
    //NS.pantalla = true;
    // load data
    NS.storeModulo.load();
    NS.storeModulo.sort('etiqueta', 'ASC');
    
    // combo de busqueda descripcion
    NS.cmbModulo = new Ext.form.ComboBox({
        store: NS.storeModulo,
        name: PF+'cmbModulo',
        id: PF+'cmbModulo',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 55,
        y: 10,
        width: 205,
        valueField: 'idComponente',
        displayField: 'etiqueta',
        autocomplete: true,
        emptyText: 'Seleccione un Módulo',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
                	NS.storeLlenaComponetes.baseParams.idModulo = parseInt(combo.getValue());
                	NS.storeLlenaComponetes.load();
                	
                	NS.storePantallas.baseParams.idModulo = parseInt(combo.getValue());
                	NS.storePantallas.load();
                }
            }
        }
    });

	NS.seleccionPantallas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});

	NS.columnasPantallas = new Ext.grid.ColumnModel([
		NS.seleccionPantallas,
		{header: 'No. de botón', width: 120, dataIndex: 'idBoton', sortable: true},
		{header: 'Descripcion', width: 80, dataIndex: 'descripcion', sortable: true},
		{header: 'Estatus', width: 80, dataIndex: 'estatus', sortable: true},
		{header: 'Id Pantalla', width: 120, dataIndex: 'idComponente', sortable: true},
	]);

	NS.storeLlenaComponetes = new Ext.data.DirectStore({
		paramAsHash: false,
		root: '',
		paramOrder:['idModulo'],
		directFn: SegComponentesAction.llenaBotones,
		fields: [
			{name: 'idBoton'},
			{name: 'descripcion'},
			{name: 'estatus'},
			{name: 'idComponente'},
			{name: 'idModulo'}
		]
	});

	//NS.storeLlenaComponetes.load();

	NS.gridPantallas = new Ext.grid.GridPanel({
		store: NS.storeLlenaComponetes,
		id: 'gridPantallas',
		cm: NS.columnasPantallas,
		sm: NS.seleccionPantallas,
		x: 0,
		y: 0,
		width: 1000,
		height: 200,
		stripeRows: true,
		columnLines: true,
		title: 'Componentes Actuales:'
	});
	
	NS.optEstatus = new Ext.form.RadioGroup({
		id: PF + 'optEstatus',
		name: PF + 'optEstatus',
		x: 0,
		y: 0,
		columns: 2, //muestra los radiobuttons en x columnas
		items: [
	          {boxLabel: 'Activo', name: 'optEstatus', inputValue: 'A', checked: true},  
	          {boxLabel: 'Inactivo', name: 'optEstatus', inputValue: 'I'}
	     ]
	});
	
	

	NS.panelPrincipal = new Ext.FormPanel({
		title: 'Mantenimiento de Botones',
		width: 1020,
		height: 600,
		frame: true,
		layout: 'absolute',
		renderTo: NS.tabContId,
		items: [
			{
				xtype: 'fieldset',
				title: 'Busqueda:',
				x: 5,
				y: 0,
				width: Ext.get(NS.tabContId).getWidth()-20,
				height: 80,
				layout: 'absolute',
				items: [
					{
					    xtype: 'label',
					    text: 'Módulo:',
					    x: 10,
					    y: 13
					}, 
					NS.cmbModulo
				]
			},
			{
				xtype: 'fieldset',
				title: 'Registro:',
				x: 5,
				y: 90,
				width: Ext.get(NS.tabContId).getWidth()-20,
				height: 500,
				layout: 'absolute',
				items: [
				    NS.gridPantallas,
				    {
						xtype: 'fieldset',
						id: PF + 'panelOpciones',
						title: '',
						x: 5,
						y: 210,
						width: Ext.get(NS.tabContId).getWidth()-55,
						height: 50,
						layout: 'absolute',
						items: [
						        {
						        	xtype: 'button',
									text: 'Nuevo',
									x: 600, 
									y: 5,
									width: 100,
									height: 22,
									listeners:
									{
										click:
										{
											fn: function (e) {
												if(NS.cmbModulo.getValue()!=''){
													NS.bModificar = false;
													Ext.get(PF + 'panelOpciones').hide();
													Ext.get(PF + 'panelRegistro').show();
												}else{
													Ext.Msg.alert('SET','Seleccione un módulo.');
												}
											}
										}
									}
						        },
						        {
						        	xtype: 'button',
									text: 'Modificar',
									x: 710, 
									y: 5,
									width: 100,
									height: 22,
									listeners:
									{
										click:
										{
											fn: function (e) {
													seleccion = NS.gridPantallas.getSelectionModel().getSelections();
													if (seleccion.length != 0) {
														NS.bModificar = true;
														NS.idComponente = seleccion[0].get('idBoton');
														Ext.getCmp(PF + 'txtIdBoton').setValue(seleccion[0].get('idBoton'));
														Ext.getCmp(PF + 'txtDescripcion').setValue(seleccion[0].get('descripcion'));
													 	NS.optEstatus.setValue(seleccion[0].get('estatus'));
													 	NS.cmbPantallas.setValue(seleccion[0].get('idComponente'));
														Ext.get(PF + 'panelOpciones').hide();
														Ext.get(PF + 'panelRegistro').show();
													} else {
														Ext.Msg.alert('SET','Seleciona un registro.');
													}
											}
										}
									}
						        },
						        {
						        	xtype: 'button',
									text: 'Eliminar',
									x: 820, 
									y: 5,
									width: 100,
									height: 22,
									listeners:
									{
										click:
										{
											fn: function (e) {
												seleccion = NS.gridPantallas.getSelectionModel().getSelections();
												if (seleccion.length != 0) {
													id = seleccion[0].get('idBoton');
													SegComponentesAction.eliminarBoton(id + '', function(resultado, e) {
														if (resultado != '' && resultado != undefined) {
															if(resultado.estatus){
																Ext.Msg.alert('SET', resultado.msg);
																NS.storeLlenaComponetes.baseParams.idModulo = parseInt(NS.cmbModulo.getValue());
											                	NS.storeLlenaComponetes.load();
															}else{
																Ext.Msg.show({
																	title:'SET',
																	msg:resultado.error,
																	icon:Ext.Msg.ERROR,
																	buttons:Ext.Msg.OK
																});
															}
														}else{
															Ext.Msg.show({
																title:'SET',
																msg:'Error desconocido.',
																icon:Ext.Msg.ERROR,
																buttons:Ext.Msg.OK
															});
														}
													});
												} else {
													Ext.Msg.alert('SET','Seleciona un registro.');
												}
											}
										}
									}
						        }
						]
				    },
				    {
						xtype: 'fieldset',
						id: PF + 'panelRegistro',
						title: '',
						x: 5,
						y: 210,
						width: Ext.get(NS.tabContId).getWidth()-55,
						height: 80,
						layout: 'absolute',
						items: [	
							{
								xtype: 'label',
								x: 0,
								y: 22,
								text: 'Id Botón:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtIdBoton',
								name: PF+'txtIdBoton',
								x: 50,
								y: 20,
								width: 150
							},
							{
								xtype: 'label',
								x: 210,
								y: 22,
								text: 'Descripcion:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtDescripcion',
								name: PF+'txtDescripcion',
								x: 270,
								y: 20,
								width: 150
							},
							{
								xtype: 'label',
								x: 430,
								y: 22,
								text: 'Pantalla:'
							},
							NS.cmbPantallas,
							{
								xtype: 'fieldset',
								title: 'Estatus:',
								x: 660,
								y: -4,
								width: 150,
								height: 60,
								layout: 'absolute',
								items: [
								        NS.optEstatus
								]
							},
							{
								xtype: 'button',
								text: 'Aceptar',
								x: 820, 
								y: 20,
								width: 80,
								height: 22,
								listeners:
								{
									click:
									{
										fn: function (e) {
											var cajas = [
													 	Ext.getCmp(PF + 'txtDescripcion'),
													 	Ext.getCmp(PF + 'txtIdBoton'),
													 	NS.cmbPantallas
													];
											if (NS.validarDatos(cajas)) {
												datos = NS.obtenerInformacion(cajas);
												if (NS.bModificar) {
													NS.modificar(datos, cajas);
												} else {
													NS.guardar(datos, cajas);
												}												
											} else {
												Ext.Msg.alert('SET','Falta información.');
											}												
										}
									}
								}
							},
							{
								xtype: 'button',
								text: 'Cancelar',
								x: 915, 
								y: 20,
								width: 80,
								height: 22,
								listeners:
								{
									click:
									{
										fn: function (e) {
											NS.limpiarCajas([
											 	Ext.getCmp(PF + 'txtDescripcion'),
											 	Ext.getCmp(PF + 'txtIdBoton'),
											 	NS.cmbPantallas,
											]);
											Ext.get(PF + 'panelOpciones').show();
											Ext.get(PF + 'panelRegistro').hide();
										}
									}
								}
							}
						]
				    }
				]
			}
		]
	});
	
	NS.validarDatos = function(iterable) {
		for (var int = 0; int < iterable.length; int++) {
			if (iterable[int].getValue() == ''){
				return false;
			}
		}
		return true;
	};
	NS.limpiarCajas = function(iterable) {
		for (var int = 0; int < iterable.length; int++) {
			iterable[int].setValue("");
		}
		NS.optEstatus.setValue('A');
	};
	
	NS.obtenerInformacion = function(iterable) {
		var renglon = new Array();
		var dto = {};
		dto.descripcion = iterable[0].getValue();
		dto.idPantalla = NS.cmbPantallas.getValue();
		dto.idModulo = NS.cmbModulo.getValue();
		dto.estatus = NS.optEstatus.getValue().inputValue;
		dto.idBoton = iterable[1].getValue();
		
		renglon[0] = dto;
		
		return Ext.util.JSON.encode(renglon);
	};
	
	NS.guardar = function(datos, cajas) {
		SegComponentesAction.guardarBoton(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
					NS.storeLlenaComponetes.baseParams.idModulo = parseInt(NS.cmbModulo.getValue());
                	NS.storeLlenaComponetes.load();
					Ext.get(PF + 'panelOpciones').show();
					Ext.get(PF + 'panelRegistro').hide();
				}else{
					Ext.Msg.show({
						title:'SET',
						msg:resultado.error,
						icon:Ext.Msg.ERROR,
						buttons:Ext.Msg.OK
					});
				}
			}else{
				Ext.Msg.show({
					title:'SET',
					msg:'Error desconocido.',
					icon:Ext.Msg.ERROR,
					buttons:Ext.Msg.OK
				});
			}
		});
	};
	
	NS.modificar = function(datos, cajas) {
		SegComponentesAction.modificarBoton(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
					NS.storeLlenaComponetes.baseParams.idModulo = parseInt(NS.cmbModulo.getValue());
                	NS.storeLlenaComponetes.load();
					Ext.get(PF + 'panelOpciones').show();
					Ext.get(PF + 'panelRegistro').hide();
				}else{
					Ext.Msg.show({
						title:'SET',
						msg:resultado.error,
						icon:Ext.Msg.ERROR,
						buttons:Ext.Msg.OK
					});
				}
			}else{
				Ext.Msg.show({
					title:'SET',
					msg:'Error desconocido.',
					icon:Ext.Msg.ERROR,
					buttons:Ext.Msg.OK
				});
			}
		});
	};
	
	Ext.get(PF + 'panelRegistro').hide();

	NS.panelPrincipal.setSize(
		Ext.get(NS.tabContId).getWidth(), 
		Ext.get(NS.tabContId).getHeight());
	NS.gridPantallas.setWidth(
		NS.panelPrincipal.getWidth()-55);
});
