Ext.onReady(function() {
	//@autor: Sergio Vaca
    var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoPerfiles.Perfiles');
    // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
    NS.tabContId = apps.SET.tabContainerId;
    var PF = apps.SET.tabID + '.';
    // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
    
    NS.pBanModificar = false;
    NS.idPerfilM = 0;
    
    // Store para llenar el combo de busqueda cmbPerfiles
    NS.storePerfilCombo = new Ext.data.DirectStore({
        paramsAsHash: false,
        baseParams: {
            pfId: -1
        },
        root: '',
        paramOrder: ['pfId'],
        directFn: SegPerfilAction.llenarComboPerfiles,
        ////idProperty: 'clavePerfil',
        fields: [{
            name: 'idPerfil'
        }, {
            name: 'descripcion'
        }, {
            name: 'estatus'
        }],
        listeners: {
            load: function(s, records) {
                var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storePerfilGrid, msg:"Cargando..."});
            }
        }
    });

    // load data
    NS.storePerfilCombo.load();
    NS.storePerfilCombo.sort('descripcion', 'ASC');

    // estatus
    NS.dataEstatusPerfil = [['T', 'Todos'], ['A', 'Activo'], ['I', 'Inactivo']];

    NS.dataEstatusPerfilNuevo = [['A', 'Activo'], ['I', 'Inactivo']];
    // store estatus
    NS.storeEstatusPerfil = new Ext.data.SimpleStore({
        idProperty: 'id',
        // identificador del store
        fields: [{
            name: 'id'
        },{
            name: 'descripcion'
        }]
    });

    // store estatus para el combo del formulario
    NS.storeEstatusPerfilNuevo = new Ext.data.SimpleStore({
        idProperty: 'id',
        // identificador del store
        fields: [{
            name: 'id'
        }, {
            name: 'descripcion'
        }]
        });

    // carga de datos a los stores
    NS.storeEstatusPerfil.loadData(NS.dataEstatusPerfil);
    NS.storeEstatusPerfilNuevo.loadData(NS.dataEstatusPerfilNuevo);

    // combo fisico estatus
    NS.cmbEstatusPerfil = new Ext.form.ComboBox({
        store: NS.storeEstatusPerfil,
        name: PF+'cmbEstatusPerfil',
        id: PF+'cmbEstatusPerfil',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 340,
        y: 10,
        width: 90,
        valueField: 'id',
        displayField: 'descripcion',
        autocomplete: true,
        emptyText: 'Seleccione',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                // evento click
                fn: function(combo, value) {
                   //Ext.getCmp(PF+'cmbCvePerfil').setValue('');
	               //Ext.getCmp(PF+'cmbDescPerfil').setValue('');
                }
            }
        }
    });
    // Combo para modificar y crear nuevo
    NS.cmbEstatusPerfilNuevo = new Ext.form.ComboBox({
        store: NS.storeEstatusPerfilNuevo,
        name: PF+'cmbEstatusPerfilNuevo',
        id: PF+'cmbEstatusPerfilNuevo',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 120,
        y: 120,
        width: 100,
        valueField: 'id',
        displayField: 'descripcion',
        autocomplete: true,
        emptyText: 'Seleccione',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                // evento click
                fn: function(combo, value) {}
            }
        }
    });
    
    // combo de busqueda descripcion
    NS.cmbDescPerfil = new Ext.form.ComboBox({
        store: NS.storePerfilCombo,
        name: PF+'cmbDescPerfil',
        id: PF+'cmbDescPerfil',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 55,
        y: 10,
        width: 205,
        valueField: 'idPerfil',
        displayField: 'descripcion',
        autocomplete: true,
        emptyText: 'Seleccione un Perfil',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
                    //Ext.getCmp(PF+'cmbCvePerfil').setValue(combo.getValue());
                    //Ext.getCmp(PF+'cmbEstatusPerfil').setValue('');
                }
            }
        }
    });
    
	NS.habilitarForma = function(habilitar){
		Ext.getCmp(PF+'btnAceptarF').setDisabled(!habilitar);
		// habilitar y deshabilitar Forma
		BFwrk.Util.enableChildComponents(Ext.getCmp(PF+'pFieldDetalle'), habilitar);
	};
    
	NS.mostrarRegistroEnForma = function(){
			// OBTENER LOS REGISTROS SELECCIONADOS
		
            var records = NS.gridPerfilesRegistros.getSelectionModel().getSelections();

			// SI HAY REGISTROS SELECCIONADOS, UTILIZAR EL PRIMERO (UNICO) PARA MOSTRARLO EN LAFORMA			
			if(records.length>0){

                NS.idPerfilM = records[0].get('idPerfil');
                //var clavePerfilG = records[0].get('clavePerfil');
                var descripcionG = records[0].get('descripcion');
                var estatusG = records[0].get('estatus');

                Ext.getCmp(PF+'descripcionF').setValue(descripcionG);
                Ext.getCmp(PF+'cmbEstatusPerfilNuevo').setValue(estatusG);

				return true;
			}else{
				return false;
			}
	};
	
	NS.actualizarRegistroDesdeForma = function(){
			// OBTENER LOS REGISTROS SELECCIONADOS
			var records = NS.gridPerfilesRegistros.getSelectionModel().getSelections();

			// SI HAY REGISTROS SELECCIONADOS, ACTUALIZAR ESTE DESDE LOS DATOS DE LA FORMA			
			if(records.length>0){
				records[0].set('descripcion', Ext.getCmp(PF+'descripcionF').getValue());
				records[0].set('estatus', Ext.getCmp(PF+'cmbEstatusPerfilNuevo').getValue());
				
				records[0].commit();

				return true;
			}else{
				return false;
			}
	};

	NS.actualizarComboDesdeForma = function(){
	
			// OBTENER LOS REGISTROS SELECCIONADOS
			var nIdx = NS.storePerfilCombo.findExact('idPerfil', NS.idPerfilM);
			var record = NS.storePerfilCombo.getAt(nIdx);

			// ACTUALIZAR ESTE DESDE LOS DATOS DE LA FORMA			
			if(record != undefined && record != null){
				record.set('descripcion', Ext.getCmp(PF+'descripcionF').getValue());
				record.set('estatus', Ext.getCmp(PF+'cmbEstatusPerfilNuevo').getValue());
				
				record.commit();

				return true;
			}else{
				return false;
			}
	};
	
 	NS.nuevoRegistro = function(){
		Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
      	NS.perfilesFormulario.getForm().reset();
		NS.idPerfilM = 0;
		
		NS.habilitarForma(true);
		Ext.getCmp(PF+'descripcionF').focus(); 
    	NS.pBanModificar = false;
	};
	
	
 	NS.modificarRegistro = function(){
		Ext.getCmp(PF+'pFieldDetalle').setTitle('Modificar');
        NS.perfilesFormulario.getForm().reset();
    	
		NS.mostrarRegistroEnForma();
		NS.habilitarForma(true);

		Ext.getCmp(PF+'descripcionF').focus(); 
		NS.pBanModificar = true;
	};
	
 	NS.borrarRegistro = function(){
	        
       	var records = NS.gridPerfilesRegistros.getSelectionModel().getSelections();
		if(records.length>0){
			Ext.Msg.confirm('confirmación','¿Estas seguro de borrar los registros seleccionados?',function(btn){
				if(btn==='yes'){
			    	var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Eliminando..."});
					myMask.show();
					//Ext.getCmp(PF+'clavePerfilF').setReadOnly(false);
		        	NS.perfilesFormulario.getForm().reset();
		        	var i=0;
		        	for(i=0; i<records.length; i=i+1){
		        		var record = records[i];
		        		var banEliminar = false;
						var pIdPerfilE = records[i].get('idPerfil');
						SegPerfilAction.eliminar(pIdPerfilE, function(result, e){
							if(!result.resultado){
								banEliminar = false;
								Ext.Msg.alert('SET','No se puede eliminar el Perfil por referencia');
								myMask.hide();
							}
							else{
								banEliminar = true;
							}
							if(banEliminar){
								var recordsFor = NS.gridPerfilesRegistros.getSelectionModel().getSelections();
								var pIdPerfilA = result.id;
								var datosP = NS.storePerfilCombo.data.items;
								for(var j=0; j<recordsFor.length; j=j+1){
									if(recordsFor[j].get('idPerfil')==pIdPerfilA){
										NS.gridPerfilesRegistros.store.remove(recordsFor[j]);
										for(var h=0; h<datosP.length; h=h+1){
											if(datosP[h].get('idPerfil')==pIdPerfilA)
												NS.storePerfilCombo.remove(datosP[h]);
										}
										NS.storePerfilCombo.sort('descripcion','ASC');
										NS.gridPerfilesRegistros.store.sort('idPerfil', 'ASC');
										Ext.Msg.alert('SET','Registro Eliminado');
										NS.gridPerfilesRegistros.getView().refresh();
										myMask.hide();
									}
								}
							}
						});
					}
					NS.gridPerfilesRegistros.getView().refresh();
					NS.perfilesFormulario.getForm().reset();
				}
			});
		}
		else{
				Ext.Msg.alert('SET','Seleccione un Registro');
		}	
	};
    
    // Store para llenar el grid
    NS.storePerfilGrid = new Ext.data.DirectStore({
        paramsAsHash: false,
        baseParams: {
            pIdPerfil: -1,
            pEstatusPerfil: 'T'
        },
        root: '',
        paramOrder: ['pIdPerfil', 'pEstatusPerfil'],
        directFn: SegPerfilAction.consultar,
        //idProperty: 'clavePerfil',
        idProperty: 'idPerfil',
        fields: [{
            name: 'idPerfil'
        }, {
            name: 'descripcion'
        }, {
            name: 'estatus'
        }],
        listeners: {
            load: function(s, records) {
                var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storePerfilGrid, msg:"Cargando..."});
            }
        }
    });

	// add to the toolbar grid
	NS.barraGrid = new Ext.Toolbar({
	    items:[{
	        text: 'Nuevo',
	        iconCls:'forma_alta',
	        handler: function() {
	        	NS.nuevoRegistro();
	        }
	    },' | ',{
	    	text:'Modificar',
	    	iconCls:'forma_cambio',
	    	handler: function(){
	    		NS.modificarRegistro();
	    	}
	    },' | ',{
	        text: 'Eliminar',
	        iconCls:'forma_baja',
	        handler: function() {
   	        	NS.borrarRegistro();
	        }
	    }, '->']
	});


	NS.searchForm = new Ext.Panel({
	    xtype: 'fieldset',
	    title: 'Búsqueda',
	    x: 10,
	    y: 4,
	    layout: 'absolute',
	    width: 760,
	    height: 80,
		region: 'north',
		split: false,
		frame: true,
		height: 80,
		autoScroll: false,
		bodyStyle: 'padding:5px 5px 0',
		collapsible: true,
		margins:'3 0 3 3',
		cmargins:'3 3 3 3',
		collapsed: false,
		labelWidth: 120,
		defaults: {autoScroll:true},	// Default config options for child items
		defaultType: 'textfield',
	    items: [{
	        xtype: 'label',
	        text: 'Perfil:',
	        x: 10,
	        y: 13
	    }, {
	        xtype: 'label',
	        text: 'Estatus:',
	        x: 280,
	        y: 13
	    }, NS.cmbDescPerfil, NS.cmbEstatusPerfil, {
	        xtype: 'button',
	        text: 'Buscar',
	        x: 660,
	        y: 10,
	        width: 75,
	        listeners: {
	            click: {
	                // OJO: REVISAR BIEN  ******************
	                fn: function() {
	
	                    if (Ext.getCmp(PF + 'cmbDescPerfil').getValue() == null || Ext.getCmp(PF + 'cmbDescPerfil').getValue() == '')
	                        var pIdPerfilB = -1;
	                    else
	                        var pIdPerfilB = Ext.getCmp(PF + 'cmbDescPerfil').getValue();
	
	                    if (Ext.getCmp(PF + 'cmbEstatusPerfil') == null || Ext.getCmp(PF + 'cmbEstatusPerfil') == '')
	                        var pEstatusB = 'T';
	                    else
	                        var pEstatusB = Ext.getCmp(PF + 'cmbEstatusPerfil').getValue();
	
	                    NS.storePerfilGrid.baseParams.pIdPerfil = pIdPerfilB;
	                    NS.storePerfilGrid.baseParams.pEstatusPerfil = pEstatusB;
	                    NS.storePerfilGrid.load();
	                    NS.gridPerfilesRegistros.store.sort('idPerfil', 'ASC');
	                }
	            }
	        }
	    }]
    });
    
    // grid de Registros
    NS.gridPerfilesRegistros = new Ext.grid.GridPanel({
	     id: PF+'idMaterGrid'
	    ,name: PF+'idMaterGrid'
	    ,region: 'west'
	    ,defaults:{autoScroll:true}
	    ,frame:true
	    ,margins:'3 3 3 0' 
		,width: 340
		,minSize: 200
		,maxSize: 340
	    ,split: true
	    ,collapsible: true
        ,stripeRows: true
        ,title: ''
        ,store: NS.storePerfilGrid
        ,tbar: [NS.barraGrid]
        ,columns: [{
            id: 'idPerfilGrid',
            header: 'Id Perfil',
            width: 80,
            sortable: true,
            dataIndex: 'idPerfil'
        },  {
            header: 'Descripción',
            width: 180,
            dataIndex: 'descripcion'
        }, {
            header: 'Estatus',
            width: 60,
            sortable: true,
            dataIndex: 'estatus'
        }],
        listeners: {
        	click:{
        		fn:function(e){
        			Ext.getCmp(PF+'pFieldDetalle').setTitle('Detalle');
					Ext.getCmp(PF+'btnAceptarF').setDisabled(true);
					NS.pBanModificar = false;
					// deshabilitar Forma
					NS.habilitarForma(false);
        		 	NS.mostrarRegistroEnForma();
        		 	
        		 	var recPerfil = NS.gridPerfilesRegistros.getSelectionModel().getSelections();
        	    	
        			if(recPerfil.length > 0) {
        				NS.cargarArbol(parseInt(recPerfil[0].get('idPerfil')));
        			}
        		}
        	},
        
            dblclick: {
                fn: function(grid) {
                	NS.modificarRegistro();
                }

            }
        }
    });
    
    // GENERAR LA INICIALIZACION DEL EL ARBOL
    NS.treePanelMenu = new Ext.ux.tree.CheckTreePanel({
    	iconCls: 'menu_seguridad',
        id: PF + 'treeMenuDivPerfiles',
        //,renderTo: NS.tabContId 
    	region: 'center',
        width: 350,
		minSize: 200,
		maxSize: 340,
        //loader: new Ext.tree.TreeLoader(),
        autoScroll:true, //, frame:false //, border:false //, lines:true
        bodyStyle:'background-color:#5B5B5E; border:1px solid #B5B8C8',
        rootVisible: false, //, iconCls:'nav'//, root: NS.rootTreeNodeMenu,
 		listeners: {
    		checkchange: {
 				fn: function(node, event) {
/*******************************************************/
		
/*******************************************************/
					//alert("hola" + Ext.getCmp(PF + 'treeMenuDivPerfiles').getValue());
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
    
    //Creacion del arbol para la asignación de modulos a los perfiles
    NS.cargarArbol = function(noPerfil) {
		SegComponentesAction.obtenerArbolComponentes(parseInt(noPerfil), function(result, e) {
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
		    	text: 'Parent ' + resObject.nModulo,
	    		draggable: false,
	    		id: 'rootTreeComp' + resObject.nModulo,
	    		text: 'root',
	    		expanded: true,
	    		uiProvider: false,
	    		children: treeMenu
		    });
			NS.treePanelMenu.setRootNode(NS.rootTreeNodeMenu);
		}); // SegMenuAction.obtenerArbolSubMenus(..
	}; // fin NS.cargarArbol()

    
   	NS.perfilesFormulario = new Ext.FormPanel({
        id: PF+'pFieldDetalle',
        name: PF+'pFieldDetalle',
        //xtype: 'fieldset',
        xtype:"form",
        title: 'Nuevo',
        layout: 'absolute',
        width: 350,
        height: 300,
        monitorValid: true,
        api: {
            submit: SegPerfilAction.insertarModificar
        },
        paramOrder: ['idPerfilF'],
		//frame: true,
		autoScroll: true,
		bodyStyle: 'padding:5px 5px 0',
		margins:'3 0 3 3',
		cmargins:'3 3 3 3',
		defaults: {labelWidth: 200, autoScroll:true},	// Default config options for child items
		defaultType: 'textfield',
		//html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items: [{
		             xtype: 'label',
		             text: 'Descripción:',
		             x: 10,
		             y: 84
		         }, {
		             xtype: 'label',
		             text: 'Estatus:',
		             x: 10,
		             y: 124
		         },  {
		             xtype: 'textfield',
		             name: PF+'descripcionF',
		             id: PF+'descripcionF',
		             maxLength: 100,
		             x: 120,
		             y: 80,
		             allowBlank: false,
		             listeners: {
		                 change: {
		                     fn: function(text) {
		                         text.setValue(text.getValue().toUpperCase());
		                     }
		                 }
		             }
		         }, NS.cmbEstatusPerfilNuevo, 
		         {
		             xtype: 'button',
		             text: 'Aceptar',
		             name: PF+'btnAceptarF',
		             id: PF+'btnAceptarF',
		             formBind: true,
		             x: 75,
		             y: 180,
		             width: 75,
		             handler: function() {
		                 NS.perfilesFormulario.getForm().submit({
		                     params: {
		                         idPerfilF: NS.idPerfilM,
		                         banderaF: NS.pBanModificar,
		                         prefijo: PF
		                     },
		                     method: 'POST',
		                     waitTitle: 'Conectando',
		                     waitMsg: 'Verificando datos...',
		                     success: function() {
		                         var records = NS.storePerfilGrid.data.items;
		                         if (!NS.pBanModificar) { // ALTA
		                             var aux = 0;
		                             if (records.length > 0) {
		                                 for (var i = 0; i < records.length; i = i + 1) {
		                                     if (aux < records[i].get('idPerfil'))
		                                         aux = records[i].get('idPerfil');
		                                 }
		                                 aux = aux + 1;
		                                 var perfilClase = NS.gridPerfilesRegistros.getStore().recordType;
		                                 var perfilDato = new perfilClase({
		                                     //clavePerfil: Ext.getCmp(PF+'clavePerfilF').getValue(),
		                                     idPerfil: aux,
		                                     descripcion: Ext.getCmp(PF+'descripcionF').getValue(),
		                                     estatus: Ext.getCmp(PF+'cmbEstatusPerfilNuevo').getValue()==='' ? 'I' : Ext.getCmp(PF+'cmbEstatusPerfilNuevo').getValue()
		                                     });
		                                 NS.gridPerfilesRegistros.stopEditing();
		                                 NS.storePerfilCombo.insert(0, perfilDato);
		                                 NS.storePerfilCombo.sort('descripcion', 'ASC');
		                                 NS.storePerfilGrid.insert(0, perfilDato);
		                                 NS.gridPerfilesRegistros.store.sort('idPerfil', 'ASC');
		                                 NS.gridPerfilesRegistros.getView().refresh();
		                             } else {
		                                 NS.storePerfilCombo.load();
		                                 NS.storePerfilCombo.sort('descripcion', 'ASC');
		                             }
		                             Ext.Msg.alert('SET', 'Registro Insertado');
		                         } else { // MOD
		                         
		                         	NS.actualizarRegistroDesdeForma();
									NS.actualizarComboDesdeForma();
		                         	
		                         
		                         	/*
		                             for (var i = 0; i < records.length; i = i + 1) {
		                                 if (NS.idPerfilM == records[i].get('idPerfil')) {
		                                     var record = i;
		                                 }
		                             }
		                             var index;
		                             var datosP = NS.storePerfilCombo.data.items;
		                             for (var i = 0; i < datosP.length; i = i + 1) {
		                                 if (datosP[i].get('idPerfil') == records[record].get('idPerfil'))
		                                     index = i;
		                             }
		                             NS.storePerfilGrid.remove(records[record]);
		                             NS.storePerfilCombo.remove(datosP[index]);
		                             var perfilClase = NS.gridPerfilesRegistros.getStore().recordType;
		                             var perfilDato = new perfilClase({
		                                 idPerfil: NS.idPerfilM,
		                                 //clavePerfil: Ext.getCmp(PF+'clavePerfilF').getValue(),
		                                 descripcion: Ext.getCmp(PF+'descripcionF').getValue(),
		                                 estatus: Ext.getCmp(PF+'cmbEstatusPerfilNuevo').getValue()
		                                 });
		                             NS.gridPerfilesRegistros.stopEditing();
		                             NS.storePerfilCombo.insert(0, perfilDato);
		                             NS.storePerfilCombo.sort('descripcion', 'ASC');
		                             NS.storePerfilGrid.insert(0, perfilDato);
		                             NS.gridPerfilesRegistros.store.sort('idPerfil', 'ASC');
		                             NS.gridPerfilesRegistros.getView().refresh();
		                             */
		                             
		                             
		                             Ext.Msg.alert('SET', 'Registro Modificado');
		                             NS.pBanModificar = false;
		                             NS.idPerfilM = 0;
		                         }
		                         Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
		                         //Ext.getCmp(PF+'clavePerfilF').setReadOnly(false);
		                         NS.perfilesFormulario.getForm().reset();
		                     },
		                     failure: function(form, action) {
		                         if (action.failureType == 'server') {
		                             if (NS.pBanModificar) {
		                                 Ext.Msg.alert('SET', 'No se puede actualizar el registro por alguna referencia');
		                                 NS.pBanModificar = false;
		                             } else
		                                 Ext.Msg.alert('SET', 'Error al insertar el registro');
		
		                         } else {
		                             Ext.Msg.alert('SET', 'Fallo de conexión con el servidor');
		                         }
		                         NS.perfilesFormulario.getForm().reset();
		                         Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
		                         //Ext.getCmp(PF+'clavePerfilF').setReadOnly(false);
		                     }
		                 });
		             }
		         }, {
		             xtype: 'button',
		             text: 'Cancelar',
		             name: PF+'btnCancelarF',
		             id: PF+'btnCancelarF',
		             formBind: true,
		             x: 160,
		             y: 180,
		             width: 75,
		             handler: function() {
		                 Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
		                 //Ext.getCmp(PF+'clavePerfilF').setReadOnly(false);
		                 NS.perfilesFormulario.getForm().reset();
		                 NS.pBanModificar = false;
		                 NS.idPerfilM = 0;
		             }
		         }]
    });
   	
   	//Radios para opciones de Facultades
	/*NS.optFacultades = new Ext.form.RadioGroup({
		id: PF + 'optFacultades',
		name: PF + 'optFacultades',
		x: 30,
		y: 10,
		columns: 2,
		items: [
		        {boxLabel: 'HABILITAR', name: 'optFacultades', inputValue: 0, checked: true},
		        {boxLabel: 'DESHABILITAR', name: 'optFacultades', inputValue: 1},  
		        {boxLabel: 'MOSTRAR', name: 'optFacultades', inputValue: 3},
		        {boxLabel: 'OCULTAR', name: 'optFacultades', inputValue: 2}
		        ]
	});*/
	
	NS.contBotones = new Ext.form.FieldSet({
		title: 'Guardar cambios de facultades',
		x: 40,
		y: 100,
		width: 220,
		height: 70,
		layout: 'absolute',
		items: [
		    {
	    	    xtype: 'button',
		    	text: 'Aceptar',
		    	x: 10,
		    	y: 5,
		    	width: 80,
		    	listeners: {
		    		click: {
				  		fn: function() {
				        	var msg = '', selNodes = Ext.getCmp(PF + 'treeMenuDivPerfiles').getChecked();
				    		Ext.each(selNodes, function(node){
				    			if(msg.length > 0)
				    				msg += ' ';
				    			msg += node.id;
				    		});
				    		/*Ext.Msg.show({
				    			title: 'id Checked Componentes',
				    			msg: msg.length > 0 ? msg : 'None',
				    					icon: Ext.Msg.INFO,
				    					minWidth: 200,
				    					buttons: Ext.Msg.OK
				    		});*/
				    		var recPerfil = NS.gridPerfilesRegistros.getSelectionModel().getSelections();
				    		
				    		SegComponentesAction.actualizarComponentesPerfil(parseInt(recPerfil[0].get('idPerfil')), msg, function(result, e) {
				    			if(result) {
				    				Ext.Msg.alert('SET','Se han actualizado los Componentes del Perfil.');
				    			} else {
				    				Ext.Msg.alert('SET','Ha ocurrido un erroral actualizar los componentes, compruebe la Bitacora.');
				    			}
				    		});
	        			}
	        		}
	        	}
	        },{
	        	xtype: 'button',
		    	text: 'Cancelar',
		    	x: 110,
		    	y: 5,
		    	width: 80,
		    	listeners: {
		    		click: {
				  		fn: function() {
				  			Ext.getCmp(PF+'pFieldDetalle').setTitle('Detalle');
							Ext.getCmp(PF+'btnAceptarF').setDisabled(true);
							NS.pBanModificar = false;
							// deshabilitar Forma
							NS.habilitarForma(false);
		        		 	NS.mostrarRegistroEnForma();
		        		 	
		        		 	var recPerfil = NS.gridPerfilesRegistros.getSelectionModel().getSelections();
		        	    	
		        			if(recPerfil.length > 0) {
		        				NS.cargarArbol(parseInt(recPerfil[0].get('idPerfil')));
		        			}
		                }
			  		}
	        	}
	        }
		]
	});
	
	NS.facultadesPanel = new Ext.Panel({
		xtype: 'fieldset',
	    title: 'Facultades',
	    x: 10,
	    y: 4,
	    layout: 'absolute',
        width: 300,
		minSize: 300,
		maxSize: 400,
		region:'east',
		split: false,
		//frame: true,
		autoScroll: false,
		bodyStyle: 'padding:5px 5px 0',
		collapsible: true,
		margins:'3 0 3 3',
		cmargins:'3 3 3 3',
		collapsed: false,
		labelWidth: 120,
		defaults: {autoScroll:true},	// Default config options for child items
		defaultType: 'textfield',
		items: [
		       NS.contBotones
		       ]
	});
	
	NS.splitPermisos = new Ext.Panel({
		id: PF+'idSplitPermisos',
		name: PF+'idSplitPermisos',
		plugins: ['fittoparent'],
		layout:'border',
		width: '100%',
		height: '100%',
		monitorResize: true,
		items: [
		        NS.treePanelMenu, 
		        NS.facultadesPanel/*,
		        {
		        	xtype: 'button',
			    	text: 'Aceptar',
			    	x: 220,
			    	y: 380,
			    	width: 85,
			    	listeners: {
			    		click: {
					  		fn: function() {
		        	var msg = '', selNodes = Ext.getCmp(PF + 'treeMenuDivPerfiles').getChecked();
		    		Ext.each(selNodes, function(node){
		    			if(msg.length > 0){
		    				msg += ' ';
		    			}
		    			msg += node.id;
		    		});*/
		    		/*Ext.Msg.show({
		    			title: 'id Checked Componentes',
		    			msg: msg.length > 0 ? msg : 'None',
		    					icon: Ext.Msg.INFO,
		    					minWidth: 200,
		    					buttons: Ext.Msg.OK
		    		});*/
        		 	/*var recPerfil = NS.gridPerfilesRegistros.getSelectionModel().getSelections();

			    		SegComponentesAction.actualizarComponentesPerfil(parseInt(recPerfil[0].get('idPerfil')), msg, function(result, e) {
			    			if(result) {
			    				Ext.Msg.alert('SET','Se han actualizado los Componentes del Perfil.');
			    			} else {
			    				Ext.Msg.alert('SET','Ha ocurrido un erroral actualizar los componentes, compruebe la Bitacora.');
			    			}
		    				});
		        		}
		        	}
		        	}
		        },{
		        	xtype: 'button',
			    	text: 'Cancelar',
			    	x: 315,
			    	y: 380,
			    	width: 85,
			    	listeners: {
			    		click: {
					  		fn: function() {
		        				//alert("Cancelar");
			                }
				  		}
		        	}
		        }*/
		]
	});
	
	NS.perfilesTab = new Ext.TabPanel({
		xtype:'tabpanel',
        id: PF+'pDetallePerfiles',
        name: PF+'pDetallePerfiles',
		region: 'center',
	    height: '100%',
		split: true,
		frame: true,
		autoScroll: true,
		defaults: {labelWidth: 200, autoScroll:true},	// Default config options for child items
		defaultType: 'textfield',
		activeTab:0,
		items:[{
		    xtype:'panel',
		    title:'Detalle',
		    frame: true,
		    items: [NS.perfilesFormulario]
		    
		  },{
		    xtype:'panel',
		    title:'Asignación de Facultades',
		    frame: true,
		    items: [NS.splitPermisos]
		    //html: "<div id='"+PF+"treeMenuDivPerfiles' class='x-panel custom-accordion x-panel-noborder x-tree' style='width:auto;border:1px solid #c3daf9;'>divArbol</div>"
		  }
		]
	});
    
	NS.splitPanel = new Ext.Panel({
        renderTo: NS.tabContId,
		id: PF+'idSplitPan',
		name: PF+'idSplitPan',
		plugins: ['fittoparent'],
		layout:'border',
		width: '100%',
		height: '100%',
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
		items: [NS.searchForm, NS.gridPerfilesRegistros, NS.perfilesTab]
	});        

	NS.habilitarForma(false);
	NS.cargarArbol(-1);
	//NS.perfilesFormulario.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
