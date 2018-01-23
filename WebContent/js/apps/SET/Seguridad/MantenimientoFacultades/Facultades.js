Ext.onReady(function(){
	//@autor: Sergio Vaca
 	 var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoFacultades.Facultades');
    // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
    NS.tabContId = apps.SET.tabContainerId;
    var PF = apps.SET.tabID + '.';
    // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
    
 	NS.pBanModificar = false;
 	NS.idFacultadM = 0;
 	//Store para llenar el combo de busqueda cmbFacultades
  	NS.storeFacultadCombo = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			pfId: -1
		},
		root: '',
		paramOrder:['pfId'],
		directFn: SegFacultadAction.llenarComboFacultades,
		idProperty: 'idFacultad',
		fields: [
			{name: 'idFacultad' },
			{name: 'descripcion'},
			{name: 'estatus'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFacultadCombo, msg:"Cargando..."});
			}
		}
	}); 
	
	// load data
	NS.storeFacultadCombo.load();
	NS.storeFacultadCombo.sort('descripcion','ASC');
	
	// estatus
	NS.dataEstatusFacultad= [
						['T', 'Todos' ],
						['A', 'Activo' ],
					  	['I', 'Inactivo']
	];

	NS.dataEstatusFacultadNuevo = [
						['A', 'Activo' ],
					  	['I', 'Inactivo']
	];
	// store estatus
	NS.storeEstatusFacultad = new Ext.data.SimpleStore( {
		idProperty: 'id', //identificador del store
		fields : [
			{name :'id'},
			{name :'descripcion'}
		]
	});
	
	// store estatus para el combo del formulario
	NS.storeEstatusFacultadNuevo = new Ext.data.SimpleStore( {
		idProperty: 'id', //identificador del store
		fields : [
			{name :'id'},
			{name :'descripcion'}
		]
	});

	//carga de datos a los stores
	NS.storeEstatusFacultad.loadData(NS.dataEstatusFacultad);
	NS.storeEstatusFacultadNuevo.loadData(NS.dataEstatusFacultadNuevo);

	//combo fisico estatus
	NS.cmbEstatusFacultad = new Ext.form.ComboBox({
		store: NS.storeEstatusFacultad
		,name: PF+'cmbEstatusFacultad'
		,id: PF+'cmbEstatusFacultad'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :555
		,y :10
		,width :90
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{ //evento click
				fn:function(combo, value) {
					Ext.getCmp(PF+'cmbCveFacultad').setValue('');
					Ext.getCmp(PF+'cmbDescFacultad').setValue('');
				}
			}
		}
	});
	//Combo para modificar y crear nuevo
	NS.cmbEstatusFacultadNuevo = new Ext.form.ComboBox({
		store: NS.storeEstatusFacultadNuevo
		,name: PF+'cmbEstatusFacultadNuevo'
		,id: PF+'cmbEstatusFacultadNuevo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :120
		,width :100
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{ //evento click
				fn:function(combo, value) {
				}
			}
		}
	});
	
	
	//combo busqueda clave 
	NS.cmbCveFacultad = new Ext.form.ComboBox({
		 store: NS.storeFacultadCombo
		,name: PF+'cmbCveFacultad'
		,id: PF+'cmbCveFacultad'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :95
		,y :10
		,width :100
		,valueField: 'idFacultad'
		,displayField:'idFacultad'
		,autocomplete: true
		,emptyText: 'Seleccione'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					Ext.getCmp(PF+'cmbDescFacultad').setValue(combo.getValue());
					Ext.getCmp(PF+'cmbEstatusFacultad').setValue('');
				}
			}
		}
	});
	
	//combo de busqueda descripcion
	NS.cmbDescFacultad = new Ext.form.ComboBox({
		 store: NS.storeFacultadCombo
		,name: PF+'cmbDescFacultad'
		,id: PF+'cmbDescFacultad'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :285
		,y :10
		,width :205
		,valueField: 'idFacultad'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un Facultad'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					Ext.getCmp(PF+'cmbCveFacultad').setValue(combo.getValue());
					Ext.getCmp(PF+'cmbEstatusFacultad').setValue('');
				}
			}
		}
	}); 
 	
 	//Store para llenar el grid
  	 NS.storeFacultadGrid = new Ext.data.DirectStore( {
  	 	paramsAsHash: false,
		baseParams: {
			pIdFacultad: -1,
			pEstatusFacultad: 'T'
		},
		root: '',
		paramOrder:['pIdFacultad','pEstatusFacultad'],
		directFn: SegFacultadAction.consultar,
		idProperty: 'idFacultad',
		fields: [
			{name: 'idFacultad' },
			{name: 'descripcion'},
			{name: 'estatus'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFacultadGrid, msg:"Cargando..."});
			}
		}
	}); 
	
	// add to the toolbar grid
	NS.barraGrid = new Ext.Toolbar({
	    items:[{
	        text: 'Nuevo',
	        iconCls:'forma_alta',
	        handler: function() {
	        	Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
	        	NS.habilitarForma(true);
	        	NS.facultadesFormulario.getForm().reset();
	        	NS.pBanModificar = false;
 				NS.idFacultadM = 0;
	        }
	    },' | ',{
	    	text:'Modificar',
	    	iconCls:'forma_cambio',
	    	handler: function(){
	    		var records = NS.gridFacultadesRegistros.getSelectionModel().getSelections();
	    		if(records.length>0){
	        		NS.habilitarForma(true);
	    		
		    		Ext.getCmp(PF+'pFieldDetalle').setTitle('Modificar');
	        		NS.facultadesFormulario.getForm().reset();
	        		NS.pBanModificar = true;
					NS.idFacultadM = records[0].get('idFacultad');
					//var claveFacultadG = records[0].get('claveFacultad');
					var descripcionG = records[0].get('descripcion');
					var estatusG = records[0].get('estatus');
					//Ext.getCmp(PF+'claveFacultadF').setValue(claveFacultadG);
					//Ext.getCmp(PF+'claveFacultadF').setReadOnly(true);
					Ext.getCmp(PF+'descripcionF').setValue(descripcionG);
					Ext.getCmp(PF+'cmbEstatusFacultadNuevo').setValue(estatusG);
				}
				else{
					Ext.Msg.alert('SET','Seleccione un Registro');
				}
	    	}
	    },' | ',{
	        text: 'Eliminar',
	        iconCls:'forma_baja',
	        handler: function() {
	        	var records = NS.gridFacultadesRegistros.getSelectionModel().getSelections();
	        	if(records.length>0){
	        		Ext.Msg.confirm('confirmación','¿Estas seguro de borrar los registros seleccionados?',function(btn){
						if(btn==='yes'){
				        	var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Eliminando..."});
							myMask.show();
				        	NS.facultadesFormulario.getForm().reset();
				        	var i=0;
				        	for(i=0; i<records.length; i=i+1){
				        		var record = records[i];
				        		var banEliminar = false;
								var pIdFacultadE = records[i].get('idFacultad');
								SegFacultadAction.eliminar(pIdFacultadE, function(result, e){
									if(!result.resultado){
										banEliminar = false;
										Ext.Msg.alert('SET','No se puede eliminar el Facultad por referencia');
										myMask.hide();
									}
									else{
										banEliminar = true;
									}
									if(banEliminar){
										var recordsFor = NS.gridFacultadesRegistros.getSelectionModel().getSelections();
										var pIdFacultadA = result.id;
										var datosP = NS.storeFacultadCombo.data.items;
										for(var j=0; j<recordsFor.length; j=j+1){
											if(recordsFor[j].get('idFacultad')==pIdFacultadA){
												NS.gridFacultadesRegistros.store.remove(recordsFor[j]);
												for(var h=0; h<datosP.length; h=h+1){
													if(datosP[h].get('idFacultad')==pIdFacultadA)
														NS.storeFacultadCombo.remove(datosP[h]);
												}
												NS.gridFacultadesRegistros.store.sort('idFacultad', 'ASC');
												NS.storeFacultadCombo.sort('descripcion','ASC');
												Ext.Msg.alert('SET','Registro Eliminado');
												NS.gridFacultadesRegistros.getView().refresh();
												myMask.hide();
											}
										}
									}
								});
							}
							NS.gridFacultadesRegistros.getView().refresh();
							NS.facultadesFormulario.getForm().reset();
						}
					});
				}
				else{
					Ext.Msg.alert('SET','Seleccione un Registro');
				}
	        }
	    }, '->']
	});

 	
 	//grid de Registros
 	NS.gridFacultadesRegistros = new Ext.grid.GridPanel({
		store : NS.storeFacultadGrid,
		height: 250,
        width: 428,
        x: 0,
        y: 0,
		title :'',
		tbar:  [NS.barraGrid],
		columns : [ 
			{
				id :'idFacultadGrid',
				header :'Id Facultad',
				width :80,
				sortable :true,
				dataIndex :'idFacultad'
			}/*,{
				id :'claveFacultadGrid',
				header :'Clave Facultad',
				width :100,
				sortable :true,
				dataIndex :'claveFacultad'
			}*/,{
				header :'Descripción',
				width :180,
				dataIndex :'descripcion'
			},{
				header: 'Estatus',
				width: 60,
				sortable: true,
				dataIndex:'estatus'
			}
		],
		listeners:{
        	click:{
        		fn:function(e){
					Ext.getCmp(PF+'pFieldDetalle').setTitle('Detalle');

					// deshabilitar Forma
					NS.habilitarForma(false);
        		 	NS.mostrarRegistroEnForma();
					NS.pBanModificar = false;
        		}
        	},
		
			dblclick:{
				fn:function(grid){
					Ext.getCmp(PF+'pFieldDetalle').setTitle('Modificar');
	        		NS.facultadesFormulario.getForm().reset();
	        		NS.pBanModificar = true;
					NS.habilitarForma(true);
	        		var records = NS.gridFacultadesRegistros.getSelectionModel().getSelections();
 					NS.idFacultadM = records[0].get('idFacultad');
 					var descripcionG = records[0].get('descripcion');
 					var estatusG = records[0].get('estatus');
 					Ext.getCmp(PF+'descripcionF').setValue(descripcionG);
 					Ext.getCmp(PF+'cmbEstatusFacultadNuevo').setValue(estatusG);
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
			// OBTENER LOS REGISTROS SELECCIONADOS.
      		var records = NS.gridFacultadesRegistros.getSelectionModel().getSelections();

			// SI HAY REGISTROS SELECCIONADOS, UTILIZAR EL PRIMERO (UNICO) PARA MOSTRARLO EN LA FORMA.
			if(records.length>0){
				NS.idFacultadM = records[0].get('idFacultad');
				var descripcionG = records[0].get('descripcion');
				var estatusG = records[0].get('estatus');

				Ext.getCmp(PF+'descripcionF').setValue(descripcionG);
				Ext.getCmp(PF+'cmbEstatusFacultadNuevo').setValue(estatusG);

				return true;
			}else{
				return false;
			}
	};
 	
	NS.actualizarRegistroDesdeForma = function(){
			// OBTENER LOS REGISTROS SELECCIONADOS
			var records = NS.gridFacultadesRegistros.getSelectionModel().getSelections();

			// SI HAY REGISTROS SELECCIONADOS, ACTUALIZAR ESTE DESDE LOS DATOS DE LA FORMA			
			if(records.length>0){
				records[0].set('descripcion', Ext.getCmp(PF+'descripcionF').getValue());
				records[0].set('estatus', Ext.getCmp(PF+'cmbEstatusFacultadNuevo').getValue());
				
				records[0].commit();

				return true;
			}else{
				return false;
			}
	};

	NS.actualizarComboDesdeForma = function(){
			// OBTENER LOS REGISTROS SELECCIONADOS
			var nIdx = NS.storeFacultadCombo.findExact('idFacultad', NS.idFacultadM);
			var record = NS.storeFacultadCombo.getAt(nIdx);
			
			// ACTUALIZAR ESTE DESDE LOS DATOS DE LA FORMA			
			if(record != undefined && record != null){
				record.set('descripcion', Ext.getCmp(PF+'descripcionF').getValue());
				record.set('estatus', Ext.getCmp(PF+'cmbEstatusFacultadNuevo').getValue());
				
				record.commit();

				return true;
			}else{
				return false;
			}
	};

    // *******************
	NS.facultadesFormulario = new Ext.FormPanel({
	    title: 'Facultades',
	    renderTo: NS.tabContId,
    	frame: true,
	    width: 782,
        height: 415,
	    layout: 'absolute',
    	autoScroll: true,
	    padding: 10,
		monitorValid: true, 
        items: [
	            {
	                xtype: 'fieldset',
	                title: 'Búsqueda',
	                x: 10,
            		y: 4,
	                layout: 'absolute',
	                width: 760,
	                height: 80,
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Id Facultad:',
	                        x: 0,
                			y: 13
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Descripción:',
	                        x: 205,
	                        y: 13
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Estatus:',
	                        x: 500,
	                        y: 13
	                    },
	                    NS.cmbCveFacultad,
	                    NS.cmbDescFacultad,
	                    NS.cmbEstatusFacultad,
	                    {
	                        xtype: 'button',
	                        text: 'Buscar',
	                        x: 660,
	                        y: 10,
	                        width: 75,
	                        listeners:{
	                        	click:{
									fn:function(){
										if(Ext.getCmp(PF+'cmbCveFacultad').getValue()==null ||
											Ext.getCmp(PF+'cmbCveFacultad').getValue()==''){
											var pIdFacultadB = -1;
											if( Ext.getCmp(PF+'cmbEstatusFacultad')==null ||
												Ext.getCmp(PF+'cmbEstatusFacultad')=='' ){
												var pEstatusB = 'T';
											}
											else
												var pEstatusB = Ext.getCmp(PF+'cmbEstatusFacultad').getValue();
										}
										else{	
											var datos = NS.storeFacultadCombo.data.items;
											var data= Ext.getCmp(PF+'cmbCveFacultad').getValue();
											var pIdFacultadB = data; 
											var pEstatusB = Ext.getCmp(PF+'cmbEstatusFacultad').getValue();
										}
										NS.storeFacultadGrid.baseParams.pIdFacultad = pIdFacultadB;
										NS.storeFacultadGrid.baseParams.pEstatusFacultad = pEstatusB;
										NS.storeFacultadGrid.load();
										NS.gridFacultadesRegistros.store.sort('idFacultad', 'ASC');
									}	                        	
	                        	}
	                        }
	                    }
	                ]
	            },{
	                xtype: 'fieldset',
	                title: 'Registros',
	                x: 10,
		            y: 89,
		            padding: 10,
		            height: 290,
	                layout: 'absolute',
	                width: 450,
	                items:[NS.gridFacultadesRegistros]
	            },{
	                id: PF+'pFieldDetalle',
	                name: PF+'pFieldDetalle',
	                xtype: 'fieldset',
	                title: 'Nuevo',
	                x: 470,
		            y: 89,
		            width: 300,
		            height: 290,
	                layout: 'absolute',
	                items:[
	                	/*{
	                        xtype: 'label',
	                        text: 'Clave Facultad:',
	                        x: 10,
	                        y: 40
	                    },*/{
	                        xtype: 'label',
	                        text: 'Descripción:',
	                        x: 10,
	                        y: 80
	                    },{
	                        xtype: 'label',
	                        text: 'Estatus:',
	                        x: 10,
	                        y: 120
	                    },{ 
							xtype: 'textfield',
							name: PF+'descripcionF', 
							id: PF+'descripcionF',
							blankText : 'Este campo es requerido', 
							maxLength: 100,
							maxLengthText: 'Este Campo Solo puede Contener 100',
							x: 120,
	                        y: 80, 
							allowBlank:	false,
							listeners:{
								change: {
									fn:function(text){
										//text.setValue(text.getValue().toUpperCase());
									}
								}
							} 
						},
						NS.cmbEstatusFacultadNuevo,
						{ 
							xtype: 'button',
							name:PF+'btnAceptarF',
							id:PF+'btnAceptarF',
							text: 'Aceptar', 
							//formBind: true, 
							x:75,
							y:180,
							width: 75,
							handler: function(){ 
								NS.facultadesFormulario.getForm().submit({ 
									params: {
										idFacultadF:NS.idFacultadM,
										banderaF: NS.pBanModificar,
										prefijo: PF
									},
									method: 'POST', 
									waitTitle: 'Conectando', 
									waitMsg: 'Verificando datos...', 
									success:
										function(){
											var records = NS.storeFacultadGrid.data.items;
											if(!NS.pBanModificar){ // ALTA
												var aux=0;
												if(records.length>0){
													for(var i = 0; i<records.length; i = i + 1){
														if(aux<records[i].get('idFacultad'))
															aux = records[i].get('idFacultad');
													}
													aux = aux+1;
													var facultadClase = NS.gridFacultadesRegistros.getStore().recordType;
									            	var facultadDato = new facultadClase({
										                //claveFacultad: Ext.getCmp(PF+'claveFacultadF').getValue(),
														idFacultad: aux,
														descripcion: Ext.getCmp(PF+'descripcionF').getValue(),
														estatus: Ext.getCmp(PF+'cmbEstatusFacultadNuevo').getValue()==='' ? 'I' : Ext.getCmp(PF+'cmbEstatusFacultadNuevo').getValue() 
										            });
										            NS.gridFacultadesRegistros.stopEditing();
									            	NS.storeFacultadGrid.insert(0, facultadDato);
									            	NS.storeFacultadCombo.insert(0, facultadDato);
									            	NS.gridFacultadesRegistros.store.sort('idFacultad', 'ASC');
									            	NS.storeFacultadCombo.sort('descripcion', 'ASC');
									            	NS.gridFacultadesRegistros.getView().refresh();
									            }
									            else{
										            NS.storeFacultadCombo.load();
													NS.storeFacultadCombo.sort('descripcion','ASC');
												}
									            Ext.Msg.alert('SET','Registro Insertado');
											}
											else{ // MOD
												NS.actualizarRegistroDesdeForma();
												NS.actualizarComboDesdeForma();
												NS.gridTipoComponenteRegistros.getView().refresh();
												Ext.Msg.alert('SET','Registro Modificado');
												
												NS.pBanModificar = false;
												NS.idFacultadM = 0;
											}
											NS.facultadesFormulario.getForm().reset();
											Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
								        	NS.habilitarForma(false);
											
											//Ext.getCmp(PF+'claveFacultadF').setReadOnly(false); 
										},
									failure:
										function(form, action){ 
											if(action.failureType == 'server'){
												if(NS.pBanModificar){ 
													Ext.Msg.alert('SET', 'No se puede actualizar el registro por alguna referencia');
													NS.pBanModificar = false;
												}
												else
													Ext.Msg.alert('SET', 'Error al insertar el registro');
												 
											}
											else{ 
												Ext.Msg.alert('SET', 'Fallo de conexión con el servidor'); 
											} 
											NS.facultadesFormulario.getForm().reset();
											Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
											//Ext.getCmp(PF+'claveFacultadF').setReadOnly(false);  
										} 
								}); 
							} 
						},{
							xtype: 'button',
							text: 'Cancelar', 
							name:PF+'btnCancelarF',
							id:PF+'btnCancelarF',
							formBind: true, 
							x:160,
							y:180,
							width: 75,
							handler: function(){
								Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
								//Ext.getCmp(PF+'claveFacultadF').setReadOnly(false);
							 	NS.facultadesFormulario.getForm().reset();
							 	NS.pBanModificar = false;
								NS.idFacultadM = 0;
							}
						}
					]
	            }
	        ],
	        api: {
				submit: SegFacultadAction.insertarModificar
			},
			paramOrder: ['idFacultadF']
	});
	
	NS.habilitarForma(false);
	NS.facultadesFormulario.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight()); 
	
});
