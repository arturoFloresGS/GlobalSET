Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	
	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoComponentes.ConfiguracionesSET');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;

	var PF = apps.SET.tabID + '.';
	// Generar prefijo para id html
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	
	NS.bModificar = false;
	NS.nConfiguracion = null;
	
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
		dto.indice = iterable[0].getValue();
		dto.valor = iterable[1].getValue();
		dto.descripcion = iterable[2].getValue();
		renglon[0] = dto;
		
		return Ext.util.JSON.encode(renglon);
	};
	
	NS.limpiarCajas = function(iterable) {
		for (var int = 0; int < iterable.length; int++) {
			iterable[int].setValue("");
		}
	};
	
	NS.guardar = function(datos, cajas) {
		SegConfiguracionesSETAction.guardarConfiguracion(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaConfiguraciones, msg:"Cargando Datos..."});
					NS.storeLlenaConfiguraciones.load();
					Ext.get(PF + 'panelOpciones').show();
					Ext.get(PF + 'panelRegistroConfiguraciones').hide();
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
		SegConfiguracionesSETAction.modificarConfiguracion(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaConfiguraciones, msg:"Cargando Datos..."});
					NS.storeLlenaConfiguraciones.load();
					Ext.get(PF + 'panelOpciones').show();
					Ext.get(PF + 'panelRegistroConfiguraciones').hide();
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
					seleccion = NS.gridConfiguraciones.getSelectionModel().getSelections();
					if (seleccion.length != 0) {
						id = seleccion[0].get('indice');
						SegConfiguracionesSETAction.eliminarConfiguracion(id + '', function(resultado, e) {
							if (resultado != '' && resultado != undefined) {
								if(resultado.estatus){
									Ext.Msg.alert('SET', resultado.msg);
									var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaConfiguraciones, msg:"Cargando Datos..."});
									NS.storeLlenaConfiguraciones.load(); 
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
	NS.seleccionConfiguraciones = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	NS.columnasConfiguraciones = new Ext.grid.ColumnModel([
	   NS.seleccionConfiguraciones,
       //{header: 'ID Empresa', width: 120, dataIndex: 'idEmpresa', sortable: true},
	   {header: 'Indice', width: 120, dataIndex: 'indice', sortable: true},
	   {header: 'Valor', width: 120, dataIndex: 'valor', sortable: true},
	   {header: 'Descripcion', width: 300, dataIndex: 'descripcion', sortable: true}
	]);
	
	NS.storeLlenaConfiguraciones = new Ext.data.DirectStore({
		paramAsHash: false,
		root: '',
		directFn: SegConfiguracionesSETAction.llenaConfiguraciones,
		fields: [
			{name: 'indice'},
			{name: 'valor'},
			{name: 'descripcion'}
		]
	});

	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaConfiguraciones, msg:"Cargando Datos..."});
	NS.storeLlenaConfiguraciones.load(); 
	
	NS.gridConfiguraciones = new Ext.grid.GridPanel({
		store: NS.storeLlenaConfiguraciones,
		id: 'gridConfiguraciones',
		cm: NS.columnasConfiguraciones,
		sm: NS.seleccionConfiguraciones,
		x: 0,
		y: 0,
		width: 1000,
		height: 257,
		stripeRows: true,
		columnLines: true,
		title: 'Configuraciones Actuales:'
	});
	
	
	NS.panelPrincipal = new Ext.FormPanel({
		title: 'Alta de Configuraciones',
		width: 1020,
		height: 600,
		frame: true,
		layout: 'absolute',
		renderTo: NS.tabContId,
		items: [
			{
				xtype: 'fieldset',
				title: 'Configuraciones del SET:',
				x: 5,
				y: 0,
				width: Ext.get(NS.tabContId).getWidth()-20,
				height: 468,
				layout: 'absolute',
				items: [
				    NS.gridConfiguraciones,
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
												Ext.get(PF + 'panelRegistroConfiguraciones').show();
												Ext.getCmp(PF + 'txtIndice').setDisabled(false);
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
													seleccion = NS.gridConfiguraciones.getSelectionModel().getSelections();
													if (seleccion.length != 0) {
														NS.bModificar = true;
														NS.nConfiguracion = seleccion[0].get('indice');
													 	Ext.getCmp(PF + 'txtIndice').setValue(seleccion[0].get('indice'));
														Ext.getCmp(PF+'txtIndice').setDisabled(true);
													 	Ext.getCmp(PF + 'txtValor').setValue(seleccion[0].get('valor'));
													 	Ext.getCmp(PF + 'txtDescripcion').setValue(seleccion[0].get('descripcion'));
														Ext.get(PF + 'panelOpciones').hide();
														Ext.get(PF + 'panelRegistroConfiguraciones').show();
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
						id: PF + 'panelRegistroConfiguraciones',
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
								text: 'Indice:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtIndice',
								name: PF+'txtIndice',
								x: 90,
								y: 12,
								width: 250
							},
							{
								xtype: 'label',
								x: 390,
								y: 14,
								text: 'Valor:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtValor',
								name: PF+'txtValor',
								x: 480,
								y: 12,
								width: 250
							},
							{
								xtype: 'label',
								x: 0,
								y: 49,
								text: 'Descripción:'
							},
							{
								xtype: 'textfield',
								id: PF+'txtDescripcion',
								name: PF+'txtDescripcion',
								x: 90,
								y: 47,
								width: 640
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
											        Ext.getCmp(PF + 'txtIndice'),
											        Ext.getCmp(PF + 'txtValor'),
											        Ext.getCmp(PF + 'txtDescripcion')
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
												Ext.getCmp(PF + 'txtIndice'),
												Ext.getCmp(PF + 'txtValor'),
												Ext.getCmp(PF + 'txtDescripcion')
											]);
											Ext.get(PF + 'panelOpciones').show();
											Ext.get(PF + 'panelRegistroConfiguraciones').hide();
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
	
	Ext.get(PF + 'panelRegistroConfiguraciones').hide();	
	NS.panelPrincipal.setSize(
		Ext.get(NS.tabContId).getWidth(), 
		Ext.get(NS.tabContId).getHeight());
	NS.gridConfiguraciones.setWidth(
		NS.panelPrincipal.getWidth()-55);
});
