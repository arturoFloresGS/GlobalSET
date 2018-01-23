Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	
	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoComponentes.MantenimientoModulos');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;

	var PF = apps.SET.tabID + '.';
	// Generar prefijo para id html
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	
	NS.bModificar = false;
	NS.idComponente = null;

	NS.seleccionPantallas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});

	NS.columnasPantallas = new Ext.grid.ColumnModel([
		NS.seleccionPantallas,
		{header: 'No. de componente', width: 120, dataIndex: 'idComponente', sortable: true},
		{header: 'Nombre', width: 80, dataIndex: 'descripcion', sortable: true},
		{header: 'Tipo componente', width: 100, dataIndex: 'idTipoComponente', sortable: true},
		{header: 'Estatus', width: 80, dataIndex: 'estatus', sortable: true},
		{header: 'Componente Padre', width: 120, dataIndex: 'idComponentePadre', sortable: true},
		{header: 'Etiqueta', width: 80, dataIndex: 'etiqueta', sortable: true},
		{header: 'Ruta Imagen', width: 80, dataIndex: 'rutaImagen', sortable: true},
		{header: 'URL', width: 80, dataIndex: 'url'}
	]);

	NS.storeLlenaComponetes = new Ext.data.DirectStore({
		paramAsHash: false,
		baseParams: {
			tipoComponente:1
		},
		root: '',
		paramOrder:['tipoComponente'],
		directFn: SegComponentesAction.llenaComponentes,
		fields: [
			{name: 'idComponente'},
			{name: 'descripcion'},
			{name: 'idTipoComponente'},
			{name: 'estatus'},
			{name: 'idComponentePadre'},
			{name: 'etiqueta'},
			{name: 'rutaImagen'},
			{name: 'url'}
		]
	});

	NS.storeLlenaComponetes.load();

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
	
	NS.eliminarBtn = new Ext.Button({
		id: 'smmeliminar',
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
						id = seleccion[0].get('idComponente');
						SegComponentesAction.eliminarModulo(id + '', function(resultado, e) {
							if (resultado != '' && resultado != undefined) {
								if(resultado.estatus){
									Ext.Msg.alert('SET', resultado.msg);
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
    });

	NS.panelPrincipal = new Ext.FormPanel({
		title: 'Alta de componentes',
		width: 1020,
		height: 600,
		frame: true,
		layout: 'absolute',
		renderTo: NS.tabContId,
		items: [
			
			{
				xtype: 'fieldset',
				title: 'Registro:',
				x: 5,
				y: 0,
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
												NS.bModificar = false;
												Ext.get(PF + 'panelOpciones').hide();
												Ext.get(PF + 'panelRegistro').show();
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
														NS.idComponente = seleccion[0].get('idComponente');
														Ext.getCmp(PF + 'txtDescripcion').setValue(seleccion[0].get('descripcion'));
													 	Ext.getCmp(PF + 'txtImagen').setValue(seleccion[0].get('rutaImagen'));
													 	Ext.getCmp(PF + 'txtEtiqueta').setValue(seleccion[0].get('etiqueta'));
													 	NS.optEstatus.setValue(seleccion[0].get('estatus'));
														Ext.get(PF + 'panelOpciones').hide();
														Ext.get(PF + 'panelRegistro').show();
													} else {
														Ext.Msg.alert('SET','Seleciona un registro.');
													}
											}
										}
									}
						        },
						        NS.eliminarBtn
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
								text: 'Descripcion:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtDescripcion',
								name: PF+'txtDescripcion',
								x: 65,
								y: 20,
								width: 150
							},
							{
								xtype: 'label',
								x: 230,
								y: 22,
								text: 'Imagen:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtImagen',
								name: PF+'txtImagen',
								x: 275,
								y: 20,
								width: 150
							},
							{
								xtype: 'label',
								x: 440,
								y: 22,
								text: 'Etiqueta:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtEtiqueta',
								name: PF+'txtEtiqueta',
								x: 490,
								y: 20,
								width: 150
							},
							{
								xtype: 'fieldset',
								title: 'Estatus:',
								x: 655,
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
													 	Ext.getCmp(PF + 'txtImagen'),
													 	Ext.getCmp(PF + 'txtEtiqueta')
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
											 	Ext.getCmp(PF + 'txtImagen'),
											 	Ext.getCmp(PF + 'txtEtiqueta')
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
		dto.imagen = iterable[1].getValue();
		dto.etiqueta = iterable[2].getValue();
		dto.estatus = NS.optEstatus.getValue().inputValue;
		dto.idComponente = NS.idComponente
		
		renglon[0] = dto;
		
		return Ext.util.JSON.encode(renglon);
	};
	
	NS.guardar = function(datos, cajas) {
		SegComponentesAction.guardarModulo(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
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
		SegComponentesAction.modificarModulo(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
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
