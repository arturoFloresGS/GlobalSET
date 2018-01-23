Ext.onReady(function(){

	//@autor: Sergio Vaca
 	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoComponentes.TipoComponentes');
    // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
    NS.tabContId = apps.SET.tabContainerId;
    var PF = apps.SET.tabID + '.';
    // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);

	//alert('Apps.SET.sUserLogin='+Apps.SET.sUserLogin);
    
 	NS.pBanModificar = false;
 	NS.idTipoComponenteM = 0;
 	//Store para llenar el combo de busqueda cmbFacultades
  	NS.storeCatTipoComponenteCombo = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			pfId: -1
		},
		root: '',
		paramOrder:['pfId'],
		directFn: SegCatTipoComponenteAction.llenarComboCatTipoComponentes,
		idProperty: 'idTipoComponente',
		fields: [
			{name: 'idTipoComponente' },
			{name: 'descripcion'},
			{name: 'estatus'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCatTipoComponenteCombo, msg:"Cargando..."});
			}
		}
	}); 
	
	// load data
	NS.storeCatTipoComponenteCombo.load();
	NS.storeCatTipoComponenteCombo.sort('descripcion','ASC');
	
	// estatus
	NS.dataEstatusCatTipoComponente= [
						['T', 'Todos' ],
						['A', 'Activo' ],
					  	['I', 'Inactivo']
	];

	NS.dataEstatusCatTipoComponenteNuevo = [
						['A', 'Activo' ],
					  	['I', 'Inactivo']
	];
	// store estatus
	NS.storeEstatusCatTipoComponente = new Ext.data.SimpleStore( {
		idProperty: 'id', //identificador del store
		fields : [
			{name :'id'},
			{name :'descripcion'}
		]
	});
	
	// store estatus para el combo del formulario
	NS.storeEstatusCatTipoComponenteNuevo = new Ext.data.SimpleStore( {
		idProperty: 'id', //identificador del store
		fields : [
			{name :'id'},
			{name :'descripcion'}
		]
	});

	//carga de datos a los stores
	NS.storeEstatusCatTipoComponente.loadData(NS.dataEstatusCatTipoComponente);
	NS.storeEstatusCatTipoComponenteNuevo.loadData(NS.dataEstatusCatTipoComponenteNuevo);

	//combo fisico estatus
	NS.cmbEstatusTipoComponente = new Ext.form.ComboBox({
		store: NS.storeEstatusCatTipoComponente
		,name: PF+'cmbEstatusTipoComponente'
		,id: PF+'cmbEstatusTipoComponente'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :550
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
					Ext.getCmp(PF+'cmbCveCatTipoComponente').setValue('');
					Ext.getCmp(PF+'cmbDescCatTipoComponente').setValue('');
				}
			}
		}
	});
	//Combo para modificar y crear nuevo
	NS.cmbEstatusTipoComponenteNuevo = new Ext.form.ComboBox({
		store: NS.storeEstatusCatTipoComponenteNuevo
		,name: PF+'cmbEstatusTipoComponenteNuevo'
		,id: PF+'cmbEstatusTipoComponenteNuevo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :100
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
	NS.cmbCveCatTipoComponente = new Ext.form.ComboBox({
		 store: NS.storeCatTipoComponenteCombo
		,name: PF+'cmbCveCatTipoComponente'
		,id: PF+'cmbCveCatTipoComponente'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :50
		,y :10
		,width :130
		,valueField: 'idTipoComponente'
		,displayField:'idTipoComponente'
		,autocomplete: true
		,emptyText: 'Seleccione'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					Ext.getCmp(PF+'cmbDescCatTipoComponente').setValue(combo.getValue());
					Ext.getCmp(PF+'cmbEstatusTipoComponente').setValue('');
				}
			}
		}
	});
	
	//combo de busqueda descripcion
	NS.cmbDescCatTipoComponente = new Ext.form.ComboBox({
		 store: NS.storeCatTipoComponenteCombo
		,name: PF+'cmbDescCatTipoComponente'
		,id: PF+'cmbDescCatTipoComponente'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :275
		,y :10
		,width :205
		,valueField: 'idTipoComponente'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione Tipo Componente'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					Ext.getCmp(PF+'cmbCveCatTipoComponente').setValue(combo.getValue());
					Ext.getCmp(PF+'cmbEstatusTipoComponente').setValue('');
				}
			}
		}
	}); 
 	
 	//Store para llenar el grid
  	 NS.storeCatTipoComponenteGrid = new Ext.data.DirectStore( {
  	 	paramsAsHash: false,
		baseParams: {
			pIdCatTipoComponente: -1,
			pEstatusCatTipoComponente: 'T'
		},
		root: '',
		paramOrder:['pIdCatTipoComponente','pEstatusCatTipoComponente'],
		directFn: SegCatTipoComponenteAction.consultar,
		idProperty: 'idTipoComponente',
		fields: [
			{name: 'idTipoComponente' },
			{name: 'descripcion'},
			{name: 'estatus'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCatTipoComponenteGrid, msg:"Cargando..."});
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

 	
 	//grid de Registros
 	NS.gridTipoComponenteRegistros = new Ext.grid.GridPanel({
		store : NS.storeCatTipoComponenteGrid,
		height: 250,
        width: 428,
        x: 0,
        y: 0,
		title :'',
		tbar: [NS.barraGrid],
		columns : [ 
			{
				id :'idTipoComponenteGrid',
				header :'Id',
				width :90,
				sortable :true,
				dataIndex :'idTipoComponente'
			}/*,{
				id :'claveTipoComponenteGrid',
				header :'Clave',
				width :100,
				sortable :true,
				dataIndex :'claveTipoComponente'
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
        			//alert('click!');
        			
					Ext.getCmp(PF+'pFieldDetalle').setTitle('Detalle');
					Ext.getCmp(PF+'btnAceptarF').setDisabled(true);
					NS.pBanModificar = false;

					// deshabilitar Forma
					NS.habilitarForma(false);

        		 	NS.mostrarRegistroEnForma();
        		}
        	},
			dblclick:{
				fn:function(grid){
					NS.modificarRegistro();
				}
				
			}
		}
	});
 	
 	// **********
 	
 	NS.limpiarForma = function(){
		NS.catTipoComponenteFormulario.getForm().reset();
	};

	NS.habilitarForma = function(habilitar){
	
		Ext.getCmp(PF+'btnAceptarF').setDisabled(!habilitar);

		// habilitar y deshabilitar Forma
		BFwrk.Util.enableChildComponents(Ext.getCmp(PF+'pFieldDetalle'), habilitar);
		//BFwrk.Util.enableChildComponents(NS.catTipoComponenteFormulario.findById(PF+'pFieldDetalle'), habilitar);
		
	};

	NS.mostrarRegistroEnForma = function(){
			// OBTENER LOS REGISTROS SELECCIONADOS
		
			var records = NS.gridTipoComponenteRegistros.getSelectionModel().getSelections();

			// SI HAY REGISTROS SELECCIONADOS, UTILIZAR EL PRIMERO (UNICO) PARA MOSTRARLO EN LAFORMA			
			if(records.length>0){
				NS.idTipoComponenteM = records[0].get('idTipoComponente');
				var descripcionG = records[0].get('descripcion');
				var estatusG = records[0].get('estatus');

				Ext.getCmp(PF+'descripcionF').setValue(descripcionG);
				Ext.getCmp(PF+'cmbEstatusTipoComponenteNuevo').setValue(estatusG);

				return true;
			}else{
				return false;
			}
	};
 	
	NS.actualizarRegistroDesdeForma = function(){
			// OBTENER LOS REGISTROS SELECCIONADOS
			var records = NS.gridTipoComponenteRegistros.getSelectionModel().getSelections();

			// SI HAY REGISTROS SELECCIONADOS, ACTUALIZAR ESTE DESDE LOS DATOS DE LA FORMA			
			if(records.length>0){
				//NS.idTipoComponenteM = records[0].get('idTipoComponente');
				records[0].set('descripcion', Ext.getCmp(PF+'descripcionF').getValue());
				records[0].set('estatus', Ext.getCmp(PF+'cmbEstatusTipoComponenteNuevo').getValue());
				
				records[0].commit();

				return true;
			}else{
				return false;
			}
	};

	NS.actualizarComboDesdeForma = function(){
	
			// OBTENER LOS REGISTROS SELECCIONADOS
			var nIdx = NS.storeCatTipoComponenteCombo.findExact('idTipoComponente', NS.idTipoComponenteM);
			var record = NS.storeCatTipoComponenteCombo.getAt(nIdx);

			// ACTUALIZAR ESTE DESDE LOS DATOS DE LA FORMA			
			if(record != undefined && record != null){
				//NS.idTipoComponenteM = records[0].get('idTipoComponente');
				record.set('descripcion', Ext.getCmp(PF+'descripcionF').getValue());
				record.set('estatus', Ext.getCmp(PF+'cmbEstatusTipoComponenteNuevo').getValue());
				
				record.commit();

				return true;
			}else{
				return false;
			}
	};

 	
 	NS.modificarRegistro = function(){
		Ext.getCmp(PF+'pFieldDetalle').setTitle('Modificar');
    	NS.catTipoComponenteFormulario.getForm().reset();
    	
		NS.mostrarRegistroEnForma();
		NS.habilitarForma(true);

		Ext.getCmp(PF+'descripcionF').focus(); 
		NS.pBanModificar = true;
	};
 	
 	NS.nuevoRegistro = function(){
		Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
    	NS.catTipoComponenteFormulario.getForm().reset();
		NS.idTipoComponenteM = 0;
		
		NS.habilitarForma(true);
		Ext.getCmp(PF+'descripcionF').focus(); 
    	NS.pBanModificar = false;
	};
	
	NS.borrarRegistro = function() {
    	var records = NS.gridTipoComponenteRegistros.getSelectionModel().getSelections();
	    if(records.length>0){
	       	Ext.Msg.confirm('confirmación','¿Estas seguro de borrar los registros seleccionados?', function(btn){
				if(btn==='yes'){
		        	var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Eliminando..."});
					myMask.show();
		        	NS.catTipoComponenteFormulario.getForm().reset();
		        	var i=0;
		        	
		        	// RECORRER TODOS LOS REGISTRO SELECCIONADOS
		        	for(i=0; i<records.length; i=i+1){
		        		var record = records[i];
						var pIdCatTipoComponenteE = record.get('idTipoComponente');
						
						// LLAMAR A METODO EN JAVA PARA  ELIMINAR EL REGISTRO
						SegCatTipoComponenteAction.eliminar(pIdCatTipoComponenteE, function(result, e){
						
							if(!result.resultado){
								Ext.Msg.alert('SET','No se puede eliminar el CatTipoComponente por referencia');
								myMask.hide();
							}
							else{
								var pIdCatTipoComponenteA = result.id;

								NS.borrarRegistroDeGrid(pIdCatTipoComponenteA);
								NS.borrarRegistroDeCombos(pIdCatTipoComponenteA);
								
								NS.gridTipoComponenteRegistros.getView().refresh();
								myMask.hide();
							}
						});
					}
					NS.gridTipoComponenteRegistros.getView().refresh();
					NS.catTipoComponenteFormulario.getForm().reset();
				}
			});
		}
		else{
			Ext.Msg.alert('SET','Seleccione un Registro');
		}
	};	

	NS.borrarRegistroDeGrid = function(idTipocomponente) {
		var recordsFor = NS.gridTipoComponenteRegistros.getSelectionModel().getSelections();
		
		for(var j=0; j<recordsFor.length; j=j+1){
			if(recordsFor[j].get('idTipoComponente')==idTipocomponente){
				// ELIMINAR REGISTRO DE GRID
				NS.gridTipoComponenteRegistros.store.remove(recordsFor[j]);
				NS.gridTipoComponenteRegistros.store.sort('idTipoComponente', 'ASC');
				
				
				Ext.Msg.alert('SET','Registro Eliminado');
			}
		}
	};
	
	NS.borrarRegistroDeCombos = function(idTipocomponente) {
		var datosP = NS.storeCatTipoComponenteCombo.data.items;
	
		// ELIMINAR REGISTRO DE COMBOS
		for(var h=0; h<datosP.length; h=h+1){
			if(datosP[h].get('idTipoComponente')==idTipocomponente){
				NS.storeCatTipoComponenteCombo.remove(datosP[h]);
			}
		}
		
		NS.storeCatTipoComponenteCombo.sort('Descripcion', 'ASC');
 	};
 	
 	//***********	
 	
 	
	NS.catTipoComponenteFormulario = new Ext.FormPanel({
		id: PF+'frmTipoComponente',
		name: PF+'frmTipoComponente',
	    //title: 'Tipo Componentes',
	    layout: 'absolute',
        x: 470,
        y: 89,
        width: 300,
        height: 290,
	    padding: 10,
		monitorValid: true,
        api: {
			submit: SegCatTipoComponenteAction.insertarModificar
		},
		paramOrder: ['idTipoComponenteF'],
        items: [
			{
	                id: PF+'pFieldDetalle',
	                name: PF+'pFieldDetalle',
	                xtype: 'fieldset',
	                title: 'Nuevo',
	                layout: 'absolute'
	                ,x: 10
	                ,y: 10
	                ,height: 250
	                ,items:[
	                	/*{
	                        xtype: 'label',
	                        text: 'Clave:',
	                        x: 10,
	                        y: 44
	                    },*/{
	                        xtype: 'label',
	                        text: 'Descripcion:',
	                        x: 10,
	                        y: 84
	                    },{
	                        xtype: 'label',
	                        text: 'Estatus:',
	                        x: 10,
	                        y: 124
	                    }/*,{ 
							xtype: 'textfield',
							name: PF+'claveTipoComponenteF', 
							id:PF+'claveTipoComponenteF',
							maxLength: 10,
							blankText : 'Este campo es requerido',
							maxLengthText: 'Este campo solo puede Contener 10 letras',
							x: 120,
	                        y: 40, 
							allowBlank:	false,
							listeners:{
								change: {
									fn:function(text){
										text.setValue(text.getValue().toUpperCase());
									}
								}
							} 
						}*/,{ 
							xtype: 'textfield',
							name: PF+'descripcionF', 
							id: PF+'descripcionF',
							blankText : 'Este campo es requerido', 
							maxLength: 100,
							maxLengthText: 'Este Campo Solo puede Contener 100',
							x: 100,
	                        y: 80, 
							allowBlank:	false,
							listeners:{
								change: {
									fn:function(text){
										text.setValue(text.getValue().toUpperCase());
									}
								}
							} 
						},
						NS.cmbEstatusTipoComponenteNuevo,
						{ 
							xtype: 'button',
							text: 'Aceptar', 
							name:PF+'btnAceptarF',
							id:PF+'btnAceptarF',
							formBind: true, 
							x:75,
							y:180,
							width: 75,
							handler: function(){ 
								NS.catTipoComponenteFormulario.getForm().submit({ 
									params: {
										idTipoComponenteF:NS.idTipoComponenteM,
										banderaF: NS.pBanModificar,
										prefijo: PF
									},
									method: 'POST', 
									waitTitle: 'Conectando', 
									waitMsg: 'Verificando datos...', 
									success:
										function(){
											NS.habilitarForma(false); // deshabilitar Forma
											var records = NS.storeCatTipoComponenteGrid.data.items;
											
											if(!NS.pBanModificar){ // ALTA
														
												var aux=0;
												if(records.length>0){
													for(var i = 0; i<records.length; i = i + 1){
														if(aux<records[i].get('idTipoComponente'))
															aux = records[i].get('idTipoComponente');
													}
													aux = aux+1;
													var TipoComponenteClase = NS.gridTipoComponenteRegistros.getStore().recordType;
									            	var TipoComponenteDato = new TipoComponenteClase({
										                //claveTipoComponente: Ext.getCmp(PF+'claveTipoComponenteF').getValue(),
														idTipoComponente: aux,
														descripcion: Ext.getCmp(PF+'descripcionF').getValue(),
														estatus: Ext.getCmp(PF+'cmbEstatusTipoComponenteNuevo').getValue()==='' ? 'I' : Ext.getCmp(PF+'cmbEstatusTipoComponenteNuevo').getValue()
										            });
										            NS.gridTipoComponenteRegistros.stopEditing();
									            	NS.storeCatTipoComponenteGrid.insert(0, TipoComponenteDato);
									            	NS.storeCatTipoComponenteCombo.insert(0, TipoComponenteDato);
									            	NS.gridTipoComponenteRegistros.store.sort('idTipoComponente', 'ASC');
									            	NS.storeCatTipoComponenteCombo.sort('descripcion', 'ASC');
									        
									            	NS.gridTipoComponenteRegistros.getView().refresh();
									            }
									            else{
									            	NS.storeCatTipoComponenteCombo.load();
									            	NS.storeCatTipoComponenteCombo.sort('descripcion','ASC');
									            }
									            Ext.Msg.alert('SET','Registro Insertado');
											}
											else{ // MOD
											
												NS.actualizarRegistroDesdeForma();
												NS.actualizarComboDesdeForma();
												NS.gridTipoComponenteRegistros.getView().refresh();
												Ext.Msg.alert('SET','Registro Actualizado');
											}
											Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
											//Ext.getCmp(PF+'claveTipoComponenteF').setReadOnly(false);
											NS.catTipoComponenteFormulario.getForm().reset();
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
												Ext.Msg.alert('SET', 'Completa los campos');
											}
											Ext.getCmp(PF+'pFieldDetalle').setTitle('Nuevo');
											//Ext.getCmp(PF+'claveTipoComponenteF').setReadOnly(false);
											NS.catTipoComponenteFormulario.getForm().reset();
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
								//Ext.getCmp(PF+'claveTipoComponenteF').setReadOnly(false);
							 	NS.catTipoComponenteFormulario.getForm().reset();
							 	NS.pBanModificar = false;
								NS.idTipoComponenteM = 0;
							}
						}
					]
	            }        
        ]
	});
	
	
	NS.contenedorPrincipal = new Ext.Panel({
		id: PF+'contenedorPrincipal',
		name: PF+'contenedorPrincipal',
	    title: 'Tipo Componentes',
	    renderTo: NS.tabContId,
	    width: 782,
    	frame: true,
        height: 415,
    	autoScroll: true,
	    layout: 'absolute',
	    padding: 10,
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
	                        text: 'Id:',
	                        x: 5,
                			y: 13
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Descripción:',
	                        x: 190,
               				y: 13
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Estatus:',
	                        x: 493,
                			y: 13
	                    },
	                    NS.cmbCveCatTipoComponente,
	                    NS.cmbDescCatTipoComponente,
	                    NS.cmbEstatusTipoComponente,
	                    {
	                        xtype: 'button',
	                        text: 'Buscar',
	                        x: 660,
	                        y: 10,
	                        width: 75,
	                        listeners:{
	                        	click:{
									fn:function(){
										if(Ext.getCmp(PF+'cmbCveCatTipoComponente').getValue()==null ||
											Ext.getCmp(PF+'cmbCveCatTipoComponente').getValue()==''){
											var pIdCatTipoComponenteB = -1;
											if( Ext.getCmp(PF+'cmbEstatusTipoComponente')==null ||
												Ext.getCmp(PF+'cmbEstatusTipoComponente')=='' ){
												var pEstatusB = 'T';
											}
											else
												var pEstatusB = Ext.getCmp(PF+'cmbEstatusTipoComponente').getValue();
										}
										else{
											var datos = NS.storeCatTipoComponenteCombo.data.items;
											var data= Ext.getCmp(PF+'cmbCveCatTipoComponente').getValue();
											var pIdCatTipoComponenteB = data; 
											var pEstatusB = Ext.getCmp(PF+'cmbEstatusTipoComponente').getValue();
										}
										NS.storeCatTipoComponenteGrid.baseParams.pIdCatTipoComponente = pIdCatTipoComponenteB;
										NS.storeCatTipoComponenteGrid.baseParams.pEstatusCatTipoComponente = pEstatusB;
										NS.storeCatTipoComponenteGrid.load();
										NS.gridTipoComponenteRegistros.store.sort('idTipoComponente', 'ASC');
									}	                        	
	                        	}
	                        }
	                    }
	                ]
	            },
	            {
	                xtype: 'fieldset',
	                title: 'Registros',
	                x: 10,
		            y: 89,
		            padding: 10,
		            height: 290,
	                layout: 'absolute',
	                width: 450,
	                items:[NS.gridTipoComponenteRegistros]
	            }, NS.catTipoComponenteFormulario
	        ]
	});

	NS.habilitarForma(false);
	NS.contenedorPrincipal.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight()); 
	
});


