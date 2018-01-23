Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	
    var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoComponentes.MantenimientoPantallas');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;

	var PF = apps.SET.tabID + '.';
	// Generar prefijo para id html
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	
	NS.idComponente = -1;
	NS.idComponenteMod = -1;
	
	NS.nuevo = false;
	NS.modificar = true;
	// Store para llenar el combo de busqueda cmbPerfiles
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
                    //Ext.getCmp(PF+'cmbCvePerfil').setValue(combo.getValue());
                    //Ext.getCmp(PF+'cmbEstatusPerfil').setValue('');
//                	Ext.getCmp(PF+'pFieldDetalle').setTitle('Detalle');
//					Ext.getCmp(PF+'btnAceptarF').setDisabled(true);
//					NS.pBanModificar = false;
//					// deshabilitar Forma
//					NS.habilitarForma(false);
//        		 	NS.mostrarRegistroEnForma();
        		 	
        		 	//var recPerfil = NS.gridPerfilesRegistros.getSelectionModel().getSelections();
        	    	
        			//if(recPerfil.length > 0) {
                		NS.optTipo.setValue('m');
        				NS.cargarArbol(parseInt(combo.getValue()));//parseInt(recPerfil[0].get('idPerfil')));
        				Ext.get(PF + 'panelRegistro').hide();
        				Ext.get(PF + 'panelRegistroCom').hide();
        				NS.idComponente = -1;
        				
        				NS.nuevo = false;
        				NS.modificar = false;
        			//}
                }
            }
        }
    });
    
    // GENERAR LA INICIALIZACION DEL EL ARBOL
    NS.treePanelMenu = new Ext.tree.TreePanel({
    	iconCls: 'menu_seguridad',
        id: PF + 'treeMenuDivPerfiles',
        //,renderTo: NS.tabContId 
    	region: 'center',
        width: 343,
		minSize: 300,
		maxSize: 300,
        //loader: new Ext.tree.TreeLoader(),
        autoScroll:true, //, frame:false //, border:false //, lines:true
        bodyStyle:'background-color:#5B5B5E;',
        rootVisible: true, //, iconCls:'nav'//, root: NS.rootTreeNodeMenu,
 		listeners: {
    		checkchange: {
 				fn: function(node, event) {
/*******************************************************/
		//var msg = '', selNodes = tree.getChecked();
    	/*var msg = '', selNodes = Ext.getCmp(PF + 'treeMenuDivPerfiles').getChecked();
		Ext.each(selNodes, function(node){
			if(msg.length > 0){
				msg += ', ';
			}
			msg += node.text;
		});
		Ext.Msg.show({
			title: 'Completed Tasks',
			msg: msg.length > 0 ? msg : 'None',
					icon: Ext.Msg.INFO,
					minWidth: 200,
					buttons: Ext.Msg.OK
		});*/
/*******************************************************/
					//alert("hola" + Ext.getCmp(PF + 'treeMenuDivPerfiles').getValue());
 				}
 			},
 			click: function(n) {
 				NS.idComponente = n.attributes.id;
 				if (NS.nuevo) {
 					if (NS.storePadres.getById(NS.idComponente) != undefined) {
						NS.cmbPadres.setValue(NS.idComponente);
					} else {
						Ext.Msg.alert('SET','No se pueden agregar componentes a las pantallas.');
					}
 				} else if (NS.modificar) {
					if (NS.cmbModulo.getValue() != NS.idComponente) {
						NS.cargarInformacion(parseInt(NS.idComponente + ''));
					} else {
						Ext.Msg.alert('SET','Seleccione un componente.');
					}
				}
            }
 		}
         /*
         ,tools:[{
             id:'refresh'
            ,qtip:'Reload Tree'
            ,handler:function() {
                NS.treePanelMenu.getRootNode().reload();
            }
         }]*/
    });
    
    NS.treeVacio = new Ext.tree.AsyncTreeNode({
		//NS.rootTreeNodeMenu = new Ext.tree.CheckTreeNodeUI({
    	
		draggable: false,
		iconCls: 'menu_folder',
		text: 'root',
		expanded: true,
		uiProvider: false,
		children: {}
    });
	NS.treePanelMenu.setRootNode(NS.treeVacio);
    
    //Creacion del arbol para la asignación de modulos a los perfiles
    NS.cargarArbol = function(noPerfil) {
		SegComponentesAction.obtenerArbolModulo(parseInt(noPerfil), function(result, e) {
			if(!result) {
				Ext.Msg.alert('SET','Error en acceso al menu desde la base de datos.');
				return;
			}
			if(result == '') { return; }
			var resObject = Ext.util.JSON.decode(result);
			
			if(BFwrk.Util.isObjectEmpty(resObject.treeMenu)) {
				Ext.Msg.alert('SET','El menu esta vacio.');
				return;
			}
			var treeMenu = resObject.treeMenu;
			
			//ARBOL DE ACORDEON I
			NS.rootTreeNodeMenu = new Ext.tree.AsyncTreeNode({
				//NS.rootTreeNodeMenu = new Ext.tree.CheckTreeNodeUI({
		    	//text: 'Parent ' + resObject.nModulo,
	    		draggable: false,
	    		id: noPerfil,
	    		text: NS.storeModulo.getById(noPerfil).get('etiqueta'),
	    		iconCls: NS.storeModulo.getById(noPerfil).get('rutaImagen'),
	    		expanded: true,
	    		uiProvider: false,
	    		children: treeMenu
	    		
		    });
			NS.treePanelMenu.setRootNode(NS.rootTreeNodeMenu);
		}); // SegMenuAction.obtenerArbolSubMenus(..
	}; // fin NS.cargarArbol()

	NS.splitPermisos = new Ext.Panel({
		id: PF+'idSplitPermisos',
		name: PF+'idSplitPermisos',
		x: 5,
		y: 90,
		width: 345,
		height: 300,
		layout: 'absolute',
		autoScroll:true,
		bodyStyle:'background-color:#5B5B5E; border:1px solid #B5B8C8',
		items: [
		        NS.treePanelMenu
		]
	});
	
	NS.optTipo = new Ext.form.RadioGroup({
		id: PF + 'optTipo',
		name: PF + 'optTipo',
		x: 0,
		y: 0,
		columns: 2, //muestra los radiobuttons en x columnas
		items: [
	          {
					boxLabel: 'Sub-menu', 
					name: 'optTipo', 
					inputValue: 'm',
					checked: true,
					listeners: {
			 			check: {
			 				fn: function(opt, valor) {
			 					//NS.pantalla = false;
			 					if (valor) {
				 					NS.storePadres.baseParams.idComponente = NS.cmbModulo.getValue();
				 					NS.storePadres.load();
	//			 					NS.cmbPadres.setValue(NS.cmbModulo.getValue());
	//			 					NS.cmbPadres.show();
	//			 					Ext.get(PF + 'panelRegistroCom').show();
				 					Ext.get(PF + 'panelRegistroCom').show();
				 					Ext.getCmp(PF + 'lblURL').hide();
				 					Ext.getCmp(PF + 'txtURL').hide();
			 					}
			 				}
			 			}
			 		}
			  },  
	          {
					boxLabel: 'Pantalla', 
					name: 'optTipo', 
					inputValue: 'p',
					listeners: {
			 			check: {
			 				fn: function(opt, valor) {
			 					if (valor) {
				 					//NS.pantalla = true;
				 					NS.storePadres.baseParams.idComponente = NS.cmbModulo.getValue();
				 					NS.storePadres.load();
	//			 					NS.cmbPadres.hide();
				 					Ext.get(PF + 'panelRegistroCom').show();
				 					Ext.getCmp(PF + 'lblURL').show();
				 					Ext.getCmp(PF + 'txtURL').show();
			 					}
			 				}
			 			}
			 		}
	          }
	     ]
	});
	
    NS.storePadres = new Ext.data.DirectStore({
    	paramsAsHash: false,
  		root: '',
		paramOrder:['idComponente'],
		directFn: SegComponentesAction.obtenerComponentesPadre,
		idProperty: 'idComponente',
		fields: [
			{name: 'idComponente'},
			{name: 'etiqueta'},
			{name: 'estatus'}
		],
	    listeners: {
			load: function(s, records){
				if (records.length != null && records.length > 0) {
					if (NS.idComponente != -1 && NS.storePadres.getById(NS.idComponente) != undefined) {
						NS.cmbPadres.setValue(NS.idComponente);
					} else {
						NS.cmbPadres.setValue(NS.cmbModulo.getValue());
						NS.idComponente = NS.cmbModulo.getValue();
					}					 
				}	
			}
		}
    });
    
    // combo de busqueda descripcion
    NS.cmbPadres = new Ext.form.ComboBox({
        store: NS.storePadres,
        name: PF+'cmbPadres',
        id: PF+'cmbPadres',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 70,
        y: 0,
        width: 205,
        valueField: 'idComponente',
        displayField: 'etiqueta',
        autocomplete: true,
        emptyText: 'Seleccione el componente padre',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
                }
            }
        }
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
		title: 'Mantenimiento de pantallas',
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
			NS.splitPermisos,
			{
				xtype: 'fieldset',
				id: PF + 'panelOpciones',
				title: '',
				x: 5,
				y: 400,
				width: 345,
				height: 50,
				layout: 'absolute',
				items: [
				        {
				        	xtype: 'button',
							text: 'Nuevo',
							x: 0, 
							y: 5,
							width: 100,
							height: 22,
							listeners:
							{
								click:
								{
									fn: function (e) {
										if(NS.cmbModulo.getValue()!=''){
											//alert(NS.cmbModulo.getValue());
											NS.nuevo = true;
											NS.modificar = false;
											Ext.get(PF + 'panelRegistro').show();
											Ext.get(PF + 'panelRegistroCom').show();
											NS.storePadres.baseParams.idComponente = NS.cmbModulo.getValue();
						 					NS.storePadres.load();
			//			 					NS.cmbPadres.setValue(NS.cmbModulo.getValue());
			//			 					NS.cmbPadres.show();
			//			 					Ext.get(PF + 'panelRegistroCom').show();
						 					//Ext.get(PF + 'panelRegistroCom').show();
						 					Ext.getCmp(PF + 'lblURL').hide();
						 					Ext.getCmp(PF + 'txtURL').hide();
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
							x: 110, 
							y: 5,
							width: 100,
							height: 22,
							listeners:
							{
								click:
								{
									fn: function (e) {
										if(NS.cmbModulo.getValue()!=''){
											if (NS.idComponente != -1 &&
													NS.cmbModulo.getValue() != NS.idComponente) {
												NS.nuevo = false;
												NS.modificar = true;
												
												NS.cargarInformacion(parseInt(NS.idComponente + ''));
			
												Ext.get(PF + 'panelRegistro').show();
												Ext.get(PF + 'panelRegistroCom').show();
											} else {
												Ext.Msg.alert('SET','Seleccione un componente.');
											}
										}else{
											Ext.Msg.alert('SET','Seleccione un módulo.');
										}
									}
								}
							}
				        },
				        {
				        	xtype: 'button',
							text: 'Eliminar',
							x: 220, 
							y: 5,
							width: 100,
							height: 22,
							listeners:
							{
								click:
								{
									fn: function (e) {
										if(NS.cmbModulo.getValue()!=''){
											if (NS.idComponente != -1 &&
													NS.cmbModulo.getValue() != NS.idComponente) {
												if (!NS.nuevo && !NS.modificar) {
													SegComponentesAction.eliminarComponente(NS.idComponente + '', function(resultado, e) {
														if (resultado != '' && resultado != undefined) {
															if(resultado.estatus){
																Ext.Msg.alert('SET', resultado.msg);
																
																NS.cargarArbol(parseInt(NS.cmbModulo.getValue()));
																
											    				NS.idComponente = -1;
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
												}
											} else {
												Ext.Msg.alert('SET','Seleccione un componente.');
											}
										}else{
											Ext.Msg.alert('SET','Seleccione un módulo.');
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
				x: 360,
				y: 90,
				width: 345,
				height: 360,
				layout: 'absolute',
				items: [
					{
						xtype: 'fieldset',
						title: 'Tipo:',
						x: 0,
						y: 0,
						width: 320,
						height: 60,
						layout: 'absolute',
						items: [
						        NS.optTipo
						]
					},
					{
						xtype: 'fieldset',
						id: PF + 'panelRegistroCom',
						title: '',
						x: 0,
						y: 70,
						width: 320,
						height: 265,
						layout: 'absolute',
						items: [
								{
									xtype: 'label',
									x: 0,
									y: 0,
									text: 'Comp. Padre:'
								},
						        NS.cmbPadres,
						        {
									xtype: 'label',
									x: 0,
									y: 37,
									text: 'Descripcion:'
								},
								{
									xtype: 'textfield',
									id: PF+'txtDescripcion',
									name: PF+'txtDescripcion',
									x: 65,
									y: 35,
									width: 150
								},
								{
									xtype: 'label',
									x: 0,
									y: 72,
									text: 'Etiqueta:'
								},
								{
									xtype: 'textfield',
									id: PF+'txtEtiqueta',
									name: PF+'txtEtiqueta',
									x: 65,
									y: 70,
									width: 150
								},
								{
									xtype: 'label',
									id: PF + 'lblURL',
									x: 0,
									y: 176,
									hidden: true,
									text: 'URL:'
								},
								{
									xtype: 'textfield',
									id: PF+'txtURL',
									name: PF+'txtURL',
									x: 65,
									y: 174,
									hidden: true,
									width: 150
								},
								{
									xtype: 'fieldset',
									title: 'Estatus:',
									x: 0,
									y: 101,
									width: 150,
									height: 60,
									layout: 'absolute',
									items: [
									        NS.optEstatus
									]
								}
						]
					},
					{
						xtype: 'fieldset',
						title: '',
						x: 0,
						y: 290,
						width: 320,
						height: 45,
						layout: 'absolute',
						items: [
							{
								xtype: 'button',
								text: 'Aceptar',
								x: 60, 
								y: 0,
								width: 80,
								height: 22,
								listeners:
								{
									click:
									{
										fn: function (e) {
											var cajas = [
													 	Ext.getCmp(PF + 'txtDescripcion'),
													 	Ext.getCmp(PF + 'txtEtiqueta'),
													 	Ext.getCmp(PF + 'txtURL')
													];
											if (NS.optTipo.getValue().inputValue == 'm') {
												cajas = [
															 	Ext.getCmp(PF + 'txtDescripcion'),
															 	Ext.getCmp(PF + 'txtEtiqueta')
															];
											}
											if (NS.validarDatos(cajas)) {
												datos = NS.obtenerInformacion(cajas);
												if (NS.nuevo) {
													NS.guardar(datos, cajas);
												} else if (NS.modificar) {
													NS.modificarComponente(datos, cajas);
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
								x: 150, 
								y: 0,
								width: 80,
								height: 22,
								listeners:
								{
									click:
									{
										fn: function (e) {
											NS.limpiarCajas([
											 	Ext.getCmp(PF + 'txtDescripcion'),
											 	Ext.getCmp(PF + 'txtURL'),
											 	Ext.getCmp(PF + 'txtEtiqueta')
											]);
											
											Ext.get(PF + 'panelRegistro').hide();
											Ext.get(PF + 'panelRegistroCom').hide();
											NS.idComponente = -1;
											
											NS.nuevo = false;
											NS.modificar = false;
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
	
	NS.limpiarCajas = function(iterable) {
		for (var int = 0; int < iterable.length; int++) {
			iterable[int].setValue("");
		}
		NS.optEstatus.setValue('A');
		NS.optTipo.setValue('m');
	};
	
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
		dto.descripcion = iterable[0].getValue();
		dto.etiqueta = iterable[1].getValue();
		dto.url = iterable[2] != undefined ? iterable[2].getValue() : '';
		dto.estatus = NS.optEstatus.getValue().inputValue;
		dto.idComponentePadre = NS.idComponente;
		dto.idComponente = NS.idComponenteMod;
		
		renglon[0] = dto;
		
		return Ext.util.JSON.encode(renglon);
	};
	
	NS.guardar = function(datos, cajas) {
		SegComponentesAction.guardarComponente(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
					NS.cargarArbol(parseInt(NS.cmbModulo.getValue()));
					Ext.get(PF + 'panelRegistro').hide();
    				Ext.get(PF + 'panelRegistroCom').hide();
    				NS.idComponente = -1;
    				
    				NS.nuevo = false;
    				NS.modificar = false;
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
	
	NS.modificarComponente = function(datos, cajas) {
		SegComponentesAction.modificarComponente(datos, function(resultado, e) {
			if (resultado != '' && resultado != undefined) {
				if(resultado.estatus){
					Ext.Msg.alert('SET', resultado.msg);
					NS.limpiarCajas(cajas);
					NS.cargarArbol(parseInt(NS.cmbModulo.getValue()));
					Ext.get(PF + 'panelRegistro').hide();
    				Ext.get(PF + 'panelRegistroCom').hide();
    				NS.idComponente = -1;
    				
    				NS.nuevo = false;
    				NS.modificar = false;
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
	
	NS.cargarInformacion = function(idComponente) {
		SegComponentesAction.obtenerComponente(idComponente,
				function(resultado, e) {
					if (resultado != '' && resultado != undefined) {
						if(resultado.estatus){
							NS.optTipo.setValue(
									resultado.informacion.idTipoComponente == 0 ? 
											'm' : 'p');
							Ext.getCmp(PF + 'txtDescripcion').setValue(
									resultado.informacion.descripcion);
						 	Ext.getCmp(PF + 'txtEtiqueta').setValue(
						 			resultado.informacion.etiqueta);
						 	Ext.getCmp(PF + 'txtURL').setValue(
						 			resultado.informacion.url);
						 	
						 	NS.optEstatus.setValue(
						 			resultado.informacion.estatus);
						 	
						 	Ext.getCmp(PF + 'txtURL').hide();
						 	Ext.getCmp(PF + 'lblURL').hide();
						 	
						 	if (resultado.informacion.idTipoComponente == 1) {
						 		Ext.getCmp(PF + 'txtURL').show();
							 	Ext.getCmp(PF + 'lblURL').show();
							}
						 	
						 	NS.idComponente = resultado.informacion.idComponentePadre;
						 	NS.idComponenteMod = resultado.informacion.idComponente;
						 	
						 	NS.storePadres.baseParams.idComponente = NS.cmbModulo.getValue();
		 					NS.storePadres.load();
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
				}
		);
		
	};
	
	//Ext.get(PF + 'panelRegistroCom').hide();
	Ext.get(PF + 'panelRegistro').hide();
	NS.panelPrincipal.setSize(
		Ext.get(NS.tabContId).getWidth(), 
		Ext.get(NS.tabContId).getHeight());
});
