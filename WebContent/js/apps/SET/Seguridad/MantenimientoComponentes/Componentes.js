Ext.onReady(function() {
    Ext.QuickTips.init();
    var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoComponentes.Componentes');
    // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
    NS.tabContId = apps.SET.tabContainerId;
    //alert('NS.tabContId='+NS.tabContId)
    
    //NS.tabContId = 'BFrmWork.Tab23';
    var PF = apps.SET.tabID + '.';
    // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);

    //Store combo clave componente
    NS.storeComponentes = new Ext.data.DirectStore({
        paramsAsHash: false,
        root: '',
        directFn: SegComponentesAction.obtenerComponentes,
        //llamada a la clase del Action
        idProperty: 'idComponente',
        //identificador del store
        fields: [{
            name: 'idComponente'
        }, {
            name: 'claveComponente'
        }, {
            name: 'descripcion'
        }, {
            name: 'estatus'
        }, {
            name: 'idTipoComponente'
        }, {
            name: 'rutaImagen'
        }, {
            name: 'idComponentePadre'
        }, {
            name: 'claveComponentePadre'
        }],
        listeners: {
            load: function(s, records) {
                var myMask = new Ext.LoadMask(Ext.getBody(), {
                    store: NS.storeComponentes,
                    msg: "Cargando..."
                });
            }
        }
    });

    //Store combo claveTipoComponente
    NS.storeClaveTipoComponente = new Ext.data.DirectStore({
        paramsAsHash: false,
        root: '',
        directFn: SegComponentesAction.obtenerClaveTipoComponente,
        //llamada a la clase del Action
        idProperty: 'idTipoComponente',
        //identificador del store
        fields: [{
            name: 'idTipoComponente'
        }, {
            name: 'claveTipoComponente'
        }, {
            name: 'descripcion'
        }, {
            name: 'estatus'
        }],
        listeners: {
            load: function(s, records) {
                var myMask = new Ext.LoadMask(Ext.getBody(), {
                    store: NS.storeClaveTipoComponente,
                    msg: "Cargando..."
                });
            }
        }
    });

    //datos estatus
    NS.datoEstatus = [['A', 'Activo'], ['I', 'Inactivo'], ['T', 'Todos']
];

    // store status
    NS.storeEstatus = new Ext.data.SimpleStore({
        fields: [{
            name: 'idEstatus'
        }, {
            name: 'nombreEstatus'
        }]
        });

    //datos estatus para el detalle
    NS.datoEstatusDetalle = [['A', 'Activo'], ['I', 'Inactivo']];

    // store status para el detalle
    NS.storeEstatusDetalle = new Ext.data.SimpleStore({
        fields: [{
            name: 'idEstatus'
        }, {
            name: 'nombreEstatus'
        }]
        });

    NS.storeEstatusDetalle.loadData(NS.datoEstatusDetalle);
    NS.storeEstatus.loadData(NS.datoEstatus);
    NS.storeComponentes.load();
    NS.storeClaveTipoComponente.load();

    //combo ClaveComponente
    NS.cmbClaveComponente = new Ext.form.ComboBox({
        store: NS.storeComponentes,
        name: PF + 'cmbComponente',
        id: PF + 'cmbComponente',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 100,
        y: 10,
        width: 130,
        valueField: 'idComponente',
        displayField: 'descripcion',
        autocomplete: true,
        emptyText: 'Seleccione una clave',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {

                    NS.storeGridComponentes.baseParams.idBusqueda = '' + combo.getValue();
                    NS.storeGridComponentes.baseParams.tipo = 'claveComponente';
                    NS.storeGridComponentes.load();

                }
            }
        }
    });

    //combo TipoComponente
    NS.cmbTipoComponente = new Ext.form.ComboBox({
        store: NS.storeClaveTipoComponente,
        name: PF + 'cmbTipoComponente',
        id: PF + 'cmbTipoComponente',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 380,
        y: 10,
        width: 130,
        valueField: 'idTipoComponente',
        displayField: 'descripcion',
        autocomplete: true,
        emptyText: 'Seleccione un tipo',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
                    NS.storeGridComponentes.baseParams.idBusqueda = '' + combo.getValue();
                    NS.storeGridComponentes.baseParams.tipo = 'tipoComponente';
                    NS.storeGridComponentes.load();

                }
            }
        }
    });

    //combo TipoComponente para el detalle
    NS.cmbTipoComponenteDetalle = new Ext.form.ComboBox({
        store: NS.storeClaveTipoComponente,
        name: PF + 'claveTipoComponente',
        id: PF + 'claveTipoComponente',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 120,
        y: 140,
        width: 150,
        valueField: 'idTipoComponente',
        displayField: 'descripcion',
        autocomplete: true,
        emptyText: 'Seleccione un tipo',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
                    var records = NS.gridComponentes.getSelectionModel().getSelections();
                    NS.contenedorPrincipalComponente.baseParams.idTipoComponente = combo.getValue();
                }
            }
        }
    });

    //combo ClaveComponentePadre para el detalle
    NS.cmbClaveComponentePadreDetalle = new Ext.form.ComboBox({
        store: NS.storeComponentes,
        name: PF + 'claveTipoComponentePadre',
        id: PF + 'claveTipoComponentePadre',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 120,
        y: 230,
        width: 150,
        valueField: 'idComponente',
        displayField: 'claveComponentePadre',
        autocomplete: true,
        emptyText: 'Seleccione un tipo',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
                    var records = NS.gridComponentes.getSelectionModel().getSelections();
                    NS.contenedorPrincipalComponente.baseParams.idComponente = combo.getValue();
                    NS.contenedorPrincipalComponente.baseParams.idComponentePadre = NS.storeComponentes.getById(combo.getValue()).get('idComponentePadre');;
                }
            }
        }
    });

    NS.storeGridComponentes = new Ext.data.DirectStore({
        paramsAsHash: false,
        root: '',
        paramOrder: ['idBusqueda', 'tipo'],
        directFn: SegComponentesAction.consultarComponentes,
        idProperty: 'idComponente',
        fields: [{
            name: 'idComponente'
        }, {
            name: 'claveComponente'
        }, {
            name: 'descripcion'
        }, {
            name: 'estatus'
        }, {
            name: 'idTipoComponente'
        }, {
            name: 'rutaImagen'
        }, {
            name: 'idComponentePadre'
        }, {
            name: 'claveComponentePadre'
        }],
        listeners: {
            load: function(s, records) {
                var myMask = new Ext.LoadMask(Ext.getBody(), {
                    store: NS.storeGridComponentes,
                    msg: "Cargando..."
                });
            }
        }
    });
    //Grid componentes
    NS.gridComponentes = new Ext.grid.GridPanel({
	     id: PF+'idMaterGrid'
	    ,name: PF+'idMaterGrid'
	    ,region: 'center'
	    ,defaults:{autoScroll:true}
	    ,frame:true
	    ,height: '100%'  // este es importante para el tamaño maximo en el split
	    ,margins:'3 3 3 0' 
	    ,split: true
        ,stripeRows: true
        ,title: ''
        ,store: NS.storeGridComponentes
	    ,sm: new Ext.grid.RowSelectionModel({
	      singleSelect: true
	      ,listeners: {
	        rowselect: function(sm, row, rec) {
	           NS.detailForm.expand(true);
	           //NS.detailForm.getForm().loadRecord(rec);
	        }
	      }
	    }),
        tbar: [{
            text: 'Nuevo',
            iconCls: 'forma_alta',
            handler: function() {
                Ext.getCmp(PF + 'frameDetalle').setTitle('Nuevo');
                Ext.getCmp(PF + 'claveComponente').setReadOnly(false);
                NS.contenedorPrincipalComponente.getForm().reset();
                NS.contenedorPrincipalComponente.baseParams.accion = 'N';

            }
        }, {
            iconCls: 'forma_cambio',
            text: 'Modificar',

            handler: function() {

                var records = NS.gridComponentes.getSelectionModel().getSelections();
                if (records.length > 0) {

                    Ext.getCmp(PF + 'claveComponente').setReadOnly(true);
                    Ext.getCmp(PF + 'frameDetalle').setTitle('Modificar');
                    Ext.getCmp(PF + 'claveComponente').setValue(records[0].get('claveComponente'));
                    Ext.getCmp(PF + 'descripcion').setValue(records[0].get('descripcion'));
                    Ext.getCmp(PF + 'estatusDetalle').setValue(records[0].get('estatus'));

                    var idTipoComp = NS.storeClaveTipoComponente.getById(records[0].get('idTipoComponente')).get('descripcion');

                    Ext.getCmp(PF + 'claveTipoComponente').setValue(idTipoComp);
                    Ext.getCmp(PF + 'rutaImagen').setValue(records[0].get('rutaImagen'));
                    Ext.getCmp(PF + 'claveTipoComponentePadre').setValue(records[0].get('claveComponentePadre'));

                    NS.contenedorPrincipalComponente.baseParams.idTipoComponente = NS.storeClaveTipoComponente.getById(records[0].get('idTipoComponente')).get('idTipoComponente');
                    NS.contenedorPrincipalComponente.baseParams.idComponente = records[0].get('idComponente');
                    NS.contenedorPrincipalComponente.baseParams.idComponentePadre = records[0].get('idComponentePadre');
                    NS.contenedorPrincipalComponente.baseParams.accion = 'M';

                } else {
                    Ext.Msg.alert('SET', 'Selecciona un registro');
                }
            }

        }, {
            iconCls: 'forma_baja',
            text: 'Eliminar',

            handler: function() {

                var s = NS.gridComponentes.getSelectionModel().getSelections();

                if (s.length > 0) {
                    Ext.Msg.confirm('Confirmaci??n', '??Estas seguro de borrar los registros seleccionados?', function(btn) {

                        if (btn === 'si') {
                            for (var i = 0, r; r = s[i]; i++) {
                                var idComponente = s[i].get('idComponente');
                                NS.storeGridComponentes.remove(r);
                                SegComponentesAction.eliminar(idComponente, function(result, e) {
                                    if (!Ext.encode(result)) {
                                        Ext.Msg.alert('SET', 'No se pudo eliminar el registro');
                                        return;
                                    } else {
                                        Ext.Msg.alert('SET', 'Registro Eliminado');
                                        NS.storeComponentes.remove(idComponente);
                                        NS.storeComponentes.load();
                                        NS.contenedorPrincipalComponente.getForm().reset();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Ext.Msg.alert('SET', 'Selecciona un registro');
                }
            }
        }],
        columns: [{
            id: 'idComponente',
            header: 'Id Componente',
            width: 90,
            sortable: true,
            dataIndex: 'idComponente'
        }, {

            header: 'Clave Componente',
            width: 100,
            sortable: true,
            dataIndex: 'claveComponente'
        }, {
            header: 'Descripcion',
            width: 80,
            dataIndex: 'descripcion'
        }, {

            header: 'Estaus',
            width: 40,
            sortable: true,
            dataIndex: 'estatus'
        }, {

            header: 'Id Tipo Componente',
            width: 100,
            sortable: true,
            dataIndex: 'idTipoComponente'
        }, {
            header: 'Ruta Imagen',
            width: 60,
            sortable: true,
            dataIndex: 'rutaImagen'
        }, {
            header: 'Id Componente Padre',
            width: 100,
            sortable: true,
            dataIndex: 'idComponentePadre'
        }, {
            header: 'Clave Componente Padre',
            width: 100,
            sortable: true,
            dataIndex: 'claveComponentePadre'
        }],
        listeners: {
            dblclick: {
                fn: function(e) {

                    Ext.getCmp(PF + 'claveComponente').setReadOnly(true);

                    Ext.getCmp(PF + 'frameDetalle').setTitle('Modificar');
                    var records = NS.gridComponentes.getSelectionModel().getSelections();
                    Ext.getCmp(PF + 'claveComponente').setValue(records[0].get('claveComponente'));
                    Ext.getCmp(PF + 'descripcion').setValue(records[0].get('descripcion'));
                    Ext.getCmp(PF + 'estatusDetalle').setValue(records[0].get('estatus'));

                    var idTipoComp = NS.storeClaveTipoComponente.getById(records[0].get('idTipoComponente')).get('descripcion');

                    Ext.getCmp(PF + 'claveTipoComponente').setValue(idTipoComp);
                    Ext.getCmp(PF + 'rutaImagen').setValue(records[0].get('rutaImagen'));
                    Ext.getCmp(PF + 'claveTipoComponentePadre').setValue(records[0].get('claveComponentePadre'));

                    NS.contenedorPrincipalComponente.baseParams.idTipoComponente = NS.storeClaveTipoComponente.getById(records[0].get('idTipoComponente')).get('idTipoComponente');
                    NS.contenedorPrincipalComponente.baseParams.idComponente = records[0].get('idComponente');
                    NS.contenedorPrincipalComponente.baseParams.idComponentePadre = records[0].get('idComponentePadre');
                    NS.contenedorPrincipalComponente.baseParams.accion = 'M';

                }
            }
        }
    });

    NS.cmbEstatus = new Ext.form.ComboBox({
        store: NS.storeEstatus,
        name: PF + 'cmbEstaus',
        id: PF + 'cmbEstaus',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 580,
        y: 10,
        width: 80,
        valueField: 'idEstatus',
        displayField: 'nombreEstatus',
        autocomplete: true,
        emptyText: 'Estatus',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
                    NS.storeGridComponentes.baseParams.idBusqueda = '' + combo.getValue();
                    NS.storeGridComponentes.baseParams.tipo = 'estatus';
                    NS.storeGridComponentes.load();

                }
            }
        }
    });

    //Combo Para el estatus de detalle
    NS.cmbEstatusDetalle = new Ext.form.ComboBox({
        store: NS.storeEstatusDetalle,
        name: PF + 'estatusDetalle',
        id: PF + 'estatusDetalle',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 120,
        y: 100,
        width: 110,
        valueField: 'idEstatus',
        displayField: 'nombreEstatus',
        autocomplete: true,
        emptyText: 'Estatus',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
				}
            }
        }
    });
    
   // GET DIV PARENT ID
   //NS.el = Ext.get(NS.tabContId);
   //NS.par = NS.el.parent();
   //NS.parId = NS.par.getAttributeNS('','id');
   //alert('cont Ids='+NS.el.getAttributeNS('','id')+','+NS.parId);
   //NS.el.remove();

    NS.searchForm = new Ext.FormPanel({
		id: PF + 'frameBusqueda',
		name: PF + 'frameBusqueda',
		title: 'Busqueda',
		layout: 'absolute',
		region: 'north',
		split: false,
		frame: true,
		height: 80,
		autoScroll: true,
		bodyStyle: 'padding:5px 5px 0',
		collapsible: true,
		margins:'3 0 3 3',
		cmargins:'3 3 3 3',
		collapsed: false,
		xtype: 'fieldset',
		labelWidth: 120,
		defaults: {autoScroll:true},	// Default config options for child items
		defaultType: 'textfield',
		//bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
		/*style: {
		"margin-left": "10px", // when you add custom margin in IE 6...
		"margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  // you have to adjust for it somewhere else
		},*/
		
		items: [{
			    xtype: 'label',
			    text: 'Componente',
			    x: 10,
			    y: 10
			}, {
			    xtype: 'label',
			    text: 'Tipo Componente',
			    x: 250,
			    y: 10
			}, {
			    xtype: 'label',
			    text: 'Estatus',
			    x: 530,
			    y: 10
			}, NS.cmbClaveComponente, NS.cmbTipoComponente, NS.cmbEstatus
		]
	});

   	NS.detailForm = new Ext.FormPanel({
		id: PF + 'frameDetalle',
		name: PF + 'frameDetalle',
		title: 'Detalle',
		//x: 380,
		//y: 10,
		//width: 340,
		height: 330,
		layout: 'absolute',
		
		region: 'east',
		split: true,
		frame: true,
		width: 350,
		minSize: 200,
		maxSize: 600,
		autoScroll: true,
		bodyStyle: 'padding:5px 5px 0',
		collapsible: true,
		margins:'3 0 3 3',
		cmargins:'3 3 3 3',
		collapsed: false,
		xtype: 'fieldset',
		//labelWidth: 200,
		defaults: {labelWidth: 200, autoScroll:true},	// Default config options for child items
		defaultType: 'textfield',
		//bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
		/*style: {
		"margin-left": "10px", // when you add custom margin in IE 6...
		"margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  // you have to adjust for it somewhere else
		},*/

		buttons: [{
		    text: 'Aceptar',
		    //x: 90,
		    //y: 200,
		    width: 60,
		    formBind: true,
		    handler: function() {
		        if (NS.contenedorPrincipalComponente.getForm().isValid()) {
		            NS.contenedorPrincipalComponente.getForm().submit({
		                params: {
		                    prefijo: PF
		                },
		                method: 'POST',
		                waitTitle: 'Valida',
		                waitMsg: 'Verificando datos...',
		                success: function() {
		                    Ext.Msg.alert('Valida', 'Acci??n Correcta');
		                    NS.contenedorPrincipalComponente.getForm().reset();
		                    NS.storeComponentes.load();
		                    NS.storeGridComponentes.load();
		                    NS.gridComponentes.getView().refresh();
		                },
		                failure: function(form, action) {
		                    if (action.failureType == 'server') {
		                        Ext.Msg.alert('Valida', 'DatosIncorrectos');
		                    } else {
		                        Ext.Msg.alert('??Atenci??n!', 'Fallo de conexi??n con el servidor: ' + action.response.responseText);
		                    }
		                    NS.contenedorPrincipalComponente.getForm().reset();
		                }
		            });
		        }
		    }
		}, {
		    text: 'Cancelar',
		    //x: 180,
		    //y: 200,
		    width: 60,
		    handler: function() {
		        NS.contenedorPrincipalComponente.getForm().reset();
		    }
		}],
        items: [{
                    xtype: 'textfield',
                    x: 120,
                    y: 20,
                    width: 90,
                    id: PF + 'claveComponente',
                    allowBlank: false,
                    listeners: {
                        change: {
                            fn: function(text) {
                                text.setValue(text.getValue().toUpperCase());
                            }
                        }
                    }
                }, {
                    xtype: 'textfield',
                    x: 120,
                    y: 60,
                    id: PF + 'descripcion',
                    allowBlank: false,
                    listeners: {
                        change: {
                            fn: function(text) {
                                text.setValue(text.getValue().toUpperCase());
                            }
                        }
                    }

                }, {
                    xtype: 'textfield',
                    x: 120,
                    y: 190,
                    width: 180,
                    id: PF + 'rutaImagen',
                    allowBlank: false,
                    listeners: {
                        change: {
                            fn: function(text) {
                                text.setValue(text.getValue().toUpperCase());
                            }
                        }
                    }
                }, NS.cmbTipoComponenteDetalle, NS.cmbClaveComponentePadreDetalle, NS.cmbEstatusDetalle, {
                    xtype: 'label',
                    text: 'Clave:',
                    x: 20,
                    y: 20
                }, {
                    xtype: 'label',
                    text: 'Descripcion:',
                    x: 20,
                    y: 60
                }, {
                    xtype: 'label',
                    text: 'Estatus:',
                    x: 20,
                    y: 100
                }, {
                    xtype: 'label',
                    text: 'Tipo Componente:',
                    x: 20,
                    y: 130,
                    width: 70
                }, {
                    xtype: 'label',
                    text: 'Ruta Imagen:',
                    x: 20,
                    y: 190
                }, {
                    xtype: 'label',
                    text: 'Componente Padre:',
                    x: 20,
                    y: 220,
                    width: 60
                }, ]
	}); 
	
	   
    
    /*
    NS.contenedorPrincipalComponente = new Ext.Panel({
        //width: 780,
        height: 500,
        //deferredRender: true,
        x: 250,
        y: 20,
        width: '100%',
        //height: '100%',
        //autoHeight:true,
        autoWidth:true,
        anchor:'100%',
        monitorResize: true,
        monitorWindowResize: true,
        //layout:'fit',
        padding: 10,
        layout: 'absolute',
        //align: 'center',
        renderTo: NS.tabContId,
        
        title: 'Componentes',
        baseParams: {
            idTipoComponente: '',
            idComponente: '',
            idComponentePadre: '',
            accion: ''
        },
        root: '',
        paramOrder: ['idTipoComponente', 'idComponente', 'idComponentePadre', 'accion'],
        api: {
            submit: SegComponentesAction.modificar
        },
        items: [{
            xtype: 'fieldset',
            id: PF + 'frameBusqueda',
            name: PF + 'frameBusqueda',
            title: '',
        	height: 200,
            //width: 340,
            //width: '100%',
            x: 10,
            y: 10,
            layout: 'absolute',
            padding: 10,
            items: [{
                xtype: 'fieldset',
                //title: 'Detalle',
                x: 200,
                y: 10,
                //width: 340,
	        	height: '100%',
            	layout: 'absolute',
                id: PF + 'frameDetalleBus',
                name: PF + 'frameDetalleBus',
                items: [{
	                xtype: 'label',
	                text: 'Componente',
	                x: 10,
	                y: 10
	            }, {
	                xtype: 'label',
	                text: 'Tipo Componente',
	                x: 250,
	                y: 10
	            }, {
	                xtype: 'label',
	                text: 'Estatus',
	                x: 530,
	                y: 10
	            }, NS.cmbClaveComponente, NS.cmbTipoComponente, NS.cmbEstatus
	            ]
	            }]
            }, {
            xtype: 'fieldset',
            id: PF + 'frameDatos',
            name: PF + 'frameDatos',
            title: '',
            x: 10,
            y: 10,
            height: 370,
            padding: 10,
            layout: 'absolute',
            items: [{
                xtype: 'fieldset',
                title: 'Detalle',
                x: 380,
                y: 10,
                width: 340,
                height: 330,
                layout: 'absolute',
                id: PF + 'frameDetalle',
                buttons: [{
                    text: 'Aceptar',
                    //x: 90,
                    //y: 200,
                    width: 60,
                    formBind: true,
                    handler: function() {
                        if (NS.contenedorPrincipalComponente.getForm().isValid()) {
                            NS.contenedorPrincipalComponente.getForm().submit({
                                params: {
                                    prefijo: PF
                                },
                                method: 'POST',
                                waitTitle: 'Valida',
                                waitMsg: 'Verificando datos...',
                                success: function() {
                                    Ext.Msg.alert('Valida', 'Acci??n Correcta');
                                    NS.contenedorPrincipalComponente.getForm().reset();
                                    NS.storeComponentes.load();
                                    NS.storeGridComponentes.load();
                                    NS.gridComponentes.getView().refresh();
                                },
                                failure: function(form, action) {
                                    if (action.failureType == 'server') {
                                        Ext.Msg.alert('Valida', 'DatosIncorrectos');
                                    } else {
                                        Ext.Msg.alert('??Atenci??n!', 'Fallo de conexi??n con el servidor: ' + action.response.responseText);
                                    }
                                    NS.contenedorPrincipalComponente.getForm().reset();
                                }
                            });
                        }
                    }
                }, {
                    text: 'Cancelar',
                    //x: 180,
                    //y: 200,
                    width: 60,
                    handler: function() {
                        NS.contenedorPrincipalComponente.getForm().reset();
                    }
                }],
                items: [{
                    xtype: 'textfield',
                    x: 120,
                    y: 20,
                    width: 90,
                    id: PF + 'claveComponente',
                    allowBlank: false,
                    listeners: {
                        change: {
                            fn: function(text) {
                                text.setValue(text.getValue().toUpperCase());
                            }
                        }
                    }
                }, {
                    xtype: 'textfield',
                    x: 120,
                    y: 60,
                    id: PF + 'descripcion',
                    allowBlank: false,
                    listeners: {
                        change: {
                            fn: function(text) {
                                text.setValue(text.getValue().toUpperCase());
                            }
                        }
                    }

                }, {
                    xtype: 'textfield',
                    x: 120,
                    y: 190,
                    width: 180,
                    id: PF + 'rutaImagen',
                    allowBlank: false,
                    listeners: {
                        change: {
                            fn: function(text) {
                                text.setValue(text.getValue().toUpperCase());
                            }
                        }
                    }
                }, NS.cmbTipoComponenteDetalle, NS.cmbClaveComponentePadreDetalle, NS.cmbEstatusDetalle, {
                    xtype: 'label',
                    text: 'Clave:',
                    x: 20,
                    y: 20
                }, {
                    xtype: 'label',
                    text: 'Descripcion:',
                    x: 20,
                    y: 60
                }, {
                    xtype: 'label',
                    text: 'Estatus:',
                    x: 20,
                    y: 100
                }, {
                    xtype: 'label',
                    text: 'Tipo Componente:',
                    x: 20,
                    y: 130,
                    width: 70
                }, {
                    xtype: 'label',
                    text: 'Ruta Imagen:',
                    x: 20,
                    y: 190
                }, {
                    xtype: 'label',
                    text: 'Componente Padre:',
                    x: 20,
                    y: 220,
                    width: 60
                }, ]
                }, {
                xtype: 'fieldset',
                title: 'Registros',
                x: 10,
                y: 10,
                width: 340,
                height: 330,
                layout: 'absolute',
                id: PF + 'frameResgistros',
                items: [NS.gridComponentes]
                }
			]
         }]

    });
    */
    
	NS.splitPanel = new Ext.Panel({
        renderTo: NS.tabContId,
		id: PF+'idSplitPan',
		name: PF+'idSplitPan',
		plugins: ['fittoparent'],
		layout:'border',
		width: 1400,
		height: 600,
		//layout:'anchor',
      	//height: '100%',
		monitorResize: true,
        listeners: {
            resize: {
                fn: function() {
                	//alert('resize');
				}
            }
        },
		items: [NS.searchForm, NS.gridComponentes, NS.detailForm]
	});        
    
    
   //NS.NS.splitPanel.render(NS.tabContId);
   //NS.contenedorPrincipalComponente.setHeight(510);
   //NS.par.setHeight(NS.contenedorPrincipalComponente.getHeight()+2);

});
