Ext.onReady(function (){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	
	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoComponentes.MantenimientoCatalogosComp');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;

	var PF = apps.SET.tabID + '.';
	// Generar prefijo para id html
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	
	NS.bModificar = false;
	NS.nCatalogo = null;
	
	NS.validarDatos = function(iterable) {
		for (var int = 0; int < iterable.length; int++) {
			if (iterable[int].getValue() == ''){
				return false;
			}
		}
		return true;
	};
	
	NS.obtenerInformacion = function(iterable) {
		var renglon = new Array();
		var dto = {};
		dto.nombreCatalogo = iterable[0].getValue();
		dto.descCatalogo = iterable[1].getValue();
		dto.tituloColumnas = iterable[2].getValue();
		dto.campos = iterable[3].getValue();
		dto.botones = iterable[4].getValue();
		renglon[0] = dto;
		
		return Ext.util.JSON.encode(renglon);
	};
	
	NS.limpiarCajas = function(iterable) {
		for (var int = 0; int < iterable.length; int++) {
			iterable[int].setValue("");
		}
	};
	
	NS.guardar = function(datos, cajas) {
		SegMantenimientoCatalogosAction.guardarCatalogo(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaCatalogos, msg:"Cargando Datos..."});
					NS.storeLlenaCatalogos.load();
					Ext.get(PF + 'panelOpciones').show();
					Ext.get(PF + 'panelRegistroCatalogo').hide();
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
		SegMantenimientoCatalogosAction.modificarCatalogo(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaCatalogos, msg:"Cargando Datos..."});
					NS.storeLlenaCatalogos.load();
					Ext.get(PF + 'panelOpciones').show();
					Ext.get(PF + 'panelRegistroCatalogo').hide();
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
	
	NS.eliminarBtn = new Ext.Button({
		id: 'btnEliminar',
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
					seleccion = NS.gridCatalogos.getSelectionModel().getSelections();
					if (seleccion.length != 0) {
						id = seleccion[0].get('nombreCatalogo');
						SegMantenimientoCatalogosAction.eliminarCatalogo(id + '', function(resultado, e) {
							if (resultado != '' && resultado != undefined) {
								if(resultado.estatus){
									Ext.Msg.alert('SET', resultado.msg);
									var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaCatalogos, msg:"Cargando Datos..."});
									NS.storeLlenaCatalogos.load(); 
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
	
	NS.seleccionCatalogos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	NS.columnasCatalogos = new Ext.grid.ColumnModel([
	   NS.seleccionCatalogos,
       //{header: 'ID Empresa', width: 120, dataIndex: 'idEmpresa', sortable: true},
	   {header: 'Nombre Catálogo', width: 150, dataIndex: 'nombreCatalogo', sortable: true},
	   {header: 'Descripción Catálogo', width: 150, dataIndex: 'descCatalogo', sortable: true},
	   {header: 'Titulo Columnas', width: 200, dataIndex: 'tituloColumnas', sortable: true},
	   {header: 'Campos', width: 200, dataIndex: 'campos', sortable: true},
	   {header: 'Botones', width: 200, dataIndex: 'botones', sortable: true}
	]);
	
	NS.storeLlenaCatalogos = new Ext.data.DirectStore({
		paramAsHash: false,
		root: '',
		directFn: SegMantenimientoCatalogosAction.llenaCatalogos,
		fields: [
			{name: 'nombreCatalogo'},
			{name: 'descCatalogo'},
			{name: 'tituloColumnas'},
			{name: 'campos'},
			{name: 'botones'}
		]
	});

	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaCatalogos, msg:"Cargando Datos..."});
	NS.storeLlenaCatalogos.load(); 
	
	NS.gridCatalogos = new Ext.grid.GridPanel({
		store: NS.storeLlenaCatalogos,
		id: 'gridPantallas',
		cm: NS.columnasCatalogos,
		sm: NS.seleccionCatalogos,
		x: 0,
		y: 0,
		width: 1000,
		height: 257,
		stripeRows: true,
		columnLines: true,
		title: 'Catálogos Actuales:'
	});
	
	NS.panelPrincipal = new Ext.FormPanel({
		title: 'Alta de Catálogos',
		width: 1020,
		height: 600,
		frame: true,
		layout: 'absolute',
		renderTo: NS.tabContId,
		items: [
			
			{
				xtype: 'fieldset',
				title: 'Catálogos:',
				x: 5,
				y: 0,
				width: Ext.get(NS.tabContId).getWidth()-20,
				height: 468,
				layout: 'absolute',
				items: [
				    NS.gridCatalogos,
				    {
						xtype: 'fieldset',
						id: PF + 'panelOpciones',
						title: 'Opciones:',
						x: 5,
						y: 265,
						width: Ext.get(NS.tabContId).getWidth()-55,
						height: 80,
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
												Ext.get(PF + 'panelRegistroCatalogo').show();
												Ext.getCmp(PF + 'txtNombreCatalogo').setDisabled(false);
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
													seleccion = NS.gridCatalogos.getSelectionModel().getSelections();
													if (seleccion.length != 0) {
														NS.bModificar = true;
														NS.nCatalogo = seleccion[0].get('nombreCatalogo');
													 	Ext.getCmp(PF + 'txtNombreCatalogo').setValue(seleccion[0].get('nombreCatalogo'));
														Ext.getCmp(PF+'txtNombreCatalogo').setDisabled(true);
													 	Ext.getCmp(PF + 'txtDescCatalogo').setValue(seleccion[0].get('descCatalogo'));
													 	Ext.getCmp(PF + 'txtTituloColumnas').setValue(seleccion[0].get('tituloColumnas'));
													 	Ext.getCmp(PF + 'txtCampos').setValue(seleccion[0].get('campos'));
													 	Ext.getCmp(PF + 'txtBotones').setValue(seleccion[0].get('botones'));
														Ext.get(PF + 'panelOpciones').hide();
														Ext.get(PF + 'panelRegistroCatalogo').show();
													} else {
														Ext.Msg.alert('SET','Seleciona un registro.');
													}
											}
										}
									}
						        }
						        ,NS.eliminarBtn
						]
				    },
				    {
						xtype: 'fieldset',
						id: PF + 'panelRegistroCatalogo',
						title: 'Registro de Datos',
						x: 5,
						y: 265,
						width: Ext.get(NS.tabContId).getWidth()-55,
						height: 168,
						layout: 'absolute',
						items: [	
							{
								xtype: 'label',
								x: 0,
								y: 14,
								text: 'NombreCatalogo:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtNombreCatalogo',
								name: PF+'txtNombreCatalogo',
								x: 90,
								y: 12,
								width: 250
							},
							{
								xtype: 'label',
								x: 350,
								y: 14,
								text: 'Descripción del Catálogo:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtDescCatalogo',
								name: PF+'txtDescCatalogo',
								x: 480,
								y: 12,
								width: 250
							},
							{
								xtype: 'label',
								x: 0,
								y: 49,
								text: 'Titulo Columnas:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtTituloColumnas',
								name: PF+'txtTituloColumnas',
								x: 90,
								y: 47,
								width: 640
							},
							{
								xtype: 'label',
								x: 0,
								y: 77,
								text: 'Campos:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtCampos',
								name: PF+'txtCampos',
								x: 90,
								y: 79,
								width: 640
							},
							{
								xtype: 'label',
								x: 0,
								y: 107,
								text: 'Botones:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtBotones',
								name: PF+'txtBotones',
								x: 90,
								y: 109,
								width: 640
							}
							,
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
													 	Ext.getCmp(PF + 'txtNombreCatalogo'),
													 	Ext.getCmp(PF + 'txtDescCatalogo'),
													 	Ext.getCmp(PF + 'txtTituloColumnas'),
													 	Ext.getCmp(PF + 'txtCampos'),
													 	Ext.getCmp(PF + 'txtBotones')
	 	
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
												Ext.getCmp(PF + 'txtNombreCatalogo'),
												Ext.getCmp(PF + 'txtDescCatalogo'),
												Ext.getCmp(PF + 'txtTituloColumnas'),
												Ext.getCmp(PF + 'txtCampos'),
												Ext.getCmp(PF + 'txtBotones')
											]);
											Ext.get(PF + 'panelOpciones').show();
											Ext.get(PF + 'panelRegistroCatalogo').hide();
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
	
	Ext.get(PF + 'panelRegistroCatalogo').hide();	
	NS.panelPrincipal.setSize(
		Ext.get(NS.tabContId).getWidth(), 
		Ext.get(NS.tabContId).getHeight());
	NS.gridCatalogos.setWidth(
		NS.panelPrincipal.getWidth()-55);
});