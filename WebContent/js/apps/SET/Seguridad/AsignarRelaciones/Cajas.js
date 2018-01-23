//@autor Cristian Garcia Garcia
 Ext.onReady(function(){

	var NS = Ext.namespace('apps.SET.Seguridad.AsignarRelaciones.Cajas');

	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);

    Ext.QuickTips.init();
    // data store para el combo usuarios
	NS.storeUsuarios = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: SegPersonasAction.obtenerUsuarios,//llamada a la clase del Action
		idProperty: 'claveUsuario',//identificador del store
		fields: [
			{name: 'idUsuario'},
			{name: 'claveUsuario'},
			{name: 'nombre'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUsuarios, msg:"Cargando..."});
			}
		}
	}); 
	// load data combo usuarios
	NS.storeUsuarios.load();//Cargar el store al inicio
	
	NS.cargarGrids = function(usuario){
	
		//alert('usuario='+usuario);

		NS.storeCajasPorAsignar.baseParams.intIdUsuario = usuario;	
		NS.storeCajasPorAsignar.baseParams.bExiste = false;
		NS.storeCajasPorAsignar.load();
		
		NS.storeCajasAsignadas.baseParams.arg1 = usuario;
		NS.storeCajasAsignadas.baseParams.arg2 = true;
		NS.storeCajasAsignadas.load()
	}; 
	
	//Crear combo usuarios
	NS.cmbUsuario = new Ext.form.ComboBox({
		 store: NS.storeUsuarios
		,name: PF+'cmbUsuario'
		,id: PF+'cmbUsuario'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :400
		,y :10
		,width :240
		,valueField:'claveUsuario'
		,displayField:'nombre'
		,autocomplete: true
		,emptyText: 'Seleccione un usuario'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(com, value) {
					//var comboCity = Ext.getCmp('combo-city');        
					//comboCity.clearValue();
					//comboCity.store.filter('cid', combo.getValue());

					Ext.getCmp(PF+'idUsuario').setValue(com.getValue());
					var varIdUsuario = NS.storeUsuarios.getById(com.getValue()).get('idUsuario');
					NS.cargarGrids(varIdUsuario);
					
				}
			}
		}
	});	
	
	
	NS.storeCajasPorAsignar = new Ext.data.DirectStore( {
		pruneModifiedRecords: true,
		paramsAsHash: false,
		baseParams: {
			 intIdUsuario: 2
			,bExiste: false
		},
		root: '',
		paramOrder: ['intIdUsuario', 'bExiste'],
		directFn: SegCajaAction.obtenerCajas,
		idProperty: 'idCaja',
		fields: [
			{name: 'idCaja' },
			{name: 'descCaja'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCajasPorAsignar, msg:"Cargando..."});
			
			}
		}
	});
	// load data
	//storeAreasPorAsignar.load();

	// crear grid para asignar cajas
	NS.gridCajasPorAsignar  = new Ext.grid.GridPanel( {
		name: PF+'gridCajasPorAsignar',
		id: PF+'gridCajasPorAsignar',
		ddGroup: PF+'secondGridDDGroup',
		store: NS.storeCajasPorAsignar,
		columns: [ {
			id: PF+'idCaja',
			name: PF+'idCaja',
			header: 'Id Caja',
			width :50,
			sortable :true,
			dataIndex :'idCaja'
		}, {
			header :'Descripcion',
			width :245,
			dataIndex :'descCaja'
		} ],
		enableDragDrop   : true,
		stripeRows :true,
		height :232,
		width :318,
		x :0,
		y :0,
		title :''
	});

	NS.storeCajasAsignadas = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			 arg1: 1
			,arg2: true
		},
		root: '',
		paramOrder: ['arg1','arg2'],
		directFn: SegCajaAction.obtenerCajas,
		idProperty: 'idCaja',
		fields: [
			{name: 'idCaja' },
			{name: 'descCaja'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCajasAsignadas, msg:"Cargando..."});
			} 
		}
	});
	
	// load data
	//storeAreasAsignadas.load();

	// crea el gride Cajas asignadas
	NS.gridCajasAsignadas  = new Ext.grid.GridPanel( {
		name: PF+'gridCajasAsignadas',
		id: PF+'gridCajasAsignadas',
		ddGroup: PF+'firstGridDDGroup',
		store: NS.storeCajasAsignadas,
		columns : [ {
			id: PF+'idCajaAsig',
			name: PF+'idCajaAsig',
			header :'Id Caja',
			width :50,
			sortable :true,
			dataIndex :'idCaja'
		}, {
			header :'Descripcion',
			width :245,
			dataIndex :'descCaja'
		} ],
		enableDragDrop   : true,
		stripeRows :true,
		height :232,
		width :318,
		x :0,
		y :0,
		title :''
	});
	
NS.contenedorPrincipalCaja =new Ext.FormPanel({
	name: PF+'contenedorPrincipal',
	id: PF+'contenedorPrincipal',
    width: 782,
    height: 415,
    frame: true,
    padding:10,
    autoScroll: true,
    title: 'Asignar Cajas',
    layout: 'absolute',
    renderTo: NS.tabContId,
    html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
         items : [
            {
                xtype: 'fieldset',
                title: 'Búsqueda',
	            width: 760, // quitar para maximizar al 100%
                height: 80,
                x: 10,
                y: 4,
                layout: 'absolute',
                id: PF+'frameBusqueda',
				name: PF+'frameBusqueda',
                items: [
                    {
                        xtype: 'uppertextfield', // plugin en util, anterior: 'textfield', 
                        x: 180,
                        y: 10,
                        width: 100,
                        id: PF+'idUsuario',
						name: PF+'idUsuario',
						listeners: {
							change:{
								//funcion para asignar uno o varios componentes dependiendo de la seleccion
								fn:function(e) 
								{
									var index = BFwrk.Util.existInCombo(PF+'cmbUsuario', 'claveUsuario', Ext.getCmp(PF+'idUsuario').getValue());
									if(index == -1){
										alert('El usuario '+Ext.getCmp(PF+'idUsuario').getValue()+' no existe.');
									}
									else{
										// ASINGAR TANTO EL VALOR COMO LA DESC PARA QUE SE "POSICIONE" EL INDICE DEL COMBO EN EL LUGAR CORECTO
										
										Ext.getCmp(PF+'cmbUsuario').setValue(Ext.getCmp(PF+'idUsuario').getValue());
										Ext.getCmp(PF+'cmbUsuario').setRawValue(NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('nombre'));
										
										// OBTENER EL ID DEL DATASTORE
										var varIdUsuario = NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('idUsuario');
										
										// CARGAR AMBOS GRIDS
										NS.cargarGrids(varIdUsuario);
										
									}
									
								}
							}
						}
                        
                        
                    },
                   [NS.cmbUsuario],
                    {
                        xtype: 'label',
                        text: 'Clave Usuario',
                        x: 60,
                        y: 10,
                        width: 120
                    },
                    {
                        xtype: 'label',
                        text: 'Usuario',
                        x: 310,
                        y: 10
                    }
                ]
            },
            {
            	xtype: 'fieldset',
				title: '',
				x: 10,
				y: 90,
				padding:10,
	            width: 760, // quitar para maximizar al 100%
				height: 290,
				layout: 'absolute',
				items: [
				{
                xtype: 'fieldset',
                title: 'Asignadas',
                x: 390,
		        y: 0,
                width: 340,
                height: 268,
                layout: 'absolute',
                id: PF+'frameNuevo',
				name: PF+'frameNuevo',
                items:[NS.gridCajasAsignadas ]
            },
            {
                xtype: 'fieldset',
                title: 'Por Asignar',
                x: 0,
		        y: 0,
                width: 340,
                height: 268,
                layout: 'absolute',
                id: PF+'frameResgistros',
                items:[NS.gridCajasPorAsignar ]
                
            },
             {
						
							xtype: 'button',
							text: '>',
							x: 350,
							y: 80,
							width: 30,
							listeners: {
								click:{
								//funcion para asignar uno o varios componentes dependiendo de la seleccion
								fn:function(e) 
								{
									var claveUsuario = Ext.getCmp(PF+'cmbUsuario').getValue();
									if(claveUsuario=='' || claveUsuario==null)
									Ext.Msg.alert('SET','Seleccione un usuario');
									else
									{
										var noUsuario = NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('idUsuario');
										var records = NS.gridCajasPorAsignar.getSelectionModel().getSelections();
										
										NS.gridCajasPorAsignar.getSelectionModel().clearSelections();
										
										for(var i=0; i<records.length; i=i+1)
										{
											var idCaja = records[i].get('idCaja');
											var dato = ''+noUsuario+','+idCaja;
												
											SegCajaAction.asignarCajas(dato, function(result, e){
												NS.gridCajasPorAsignar.getStore().reload();

												if(!Ext.encode(result))
													{
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
											}
										);
										}
										
										NS.gridCajasAsignadas.store.add(records);
										//NS.gridCajasPorAsignar.store.remove(records); // ojo: bug falla el refresh
										NS.gridCajasAsignadas.store.sort('idCaja', 'ASC');
										
									}
								}
								
								}
							}
						},
						{
							xtype: 'button',
							text: '<',
							x: 350,
							y: 120,
							width: 30,
							listeners: {
								click:{
									//funcion para eliminar uno o varios componentes dependiendo de la seleccion
									fn:function(e) {
										var claveUsuario =Ext.getCmp(PF+'cmbUsuario').getValue();
										if(claveUsuario=='' || claveUsuario==null)
										Ext.Msg.alert('SET','Seleccione un usuario');
										else{
											var noUsuario = NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('idUsuario');
											var records = NS.gridCajasAsignadas.getSelectionModel().getSelections();
											for(var i=0; i<records.length; i=i+1){
												var idCaja = records[i].get('idCaja');
												var dato = ''+noUsuario+','+idCaja;
												NS.gridCajasPorAsignar.store.add(records[i]);
												NS.gridCajasAsignadas.store.remove(records[i]);
												NS.gridCajasPorAsignar.store.sort('idCaja', 'ASC');
												
												SegCajaAction.eliminarCajas(dato, function(result, e){
													NS.gridCajasAsignadas.getStore().reload();
												
													if(!Ext.encode(result)){
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
												});
											}
											NS.gridCajasAsignadas.getView().refresh();
										}
									}
								
								}
							}
						},
						{
							xtype: 'button',
							text: '>>',
							x: 350,
							y: 160,
							width: 30,
							listeners:{
								click:{
									fn:function(e) {
										//funcion para asignar todos los componentes
										var claveUsuario =Ext.getCmp(PF+'cmbUsuario').getValue();
										if(claveUsuario=='' || claveUsuario==null)
											Ext.Msg.alert('SET','Seleccione un usuario');
										else{
											var noUsuario = ''+NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('idUsuario');
											SegCajaAction.asignarCajas(noUsuario, function(result, e){
												if(!Ext.encode(result)){
													Ext.Msg.alert('SET','Error en la BD');
													return;
												}
											}
											);
											var records = NS.storeCajasPorAsignar.data.items;
											NS.gridCajasAsignadas.store.add(records);
											NS.gridCajasPorAsignar.store.removeAll();
											NS.gridCajasPorAsignar.getView().refresh();
											NS.gridCajasAsignadas.store.sort('idCaja', 'ASC');
										}
									}
								}
							}
						},
						{
							xtype: 'button',
							text: '<<',
							x: 350,
							y: 200,
							width: 30,
							listeners:{
								click:{
									//funcion para eliminar todos los componentes asignados
									fn:function(e) {
										var claveUsuario =Ext.getCmp(PF+'cmbUsuario').getValue();
										if(claveUsuario=='' || claveUsuario==null)
										Ext.Msg.alert('SET','Seleccione un usuario');
										else{
											var noUsuario = ''+NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('idUsuario');
											SegCajaAction.eliminarCajas(noUsuario, function(result, e){
												if(!Ext.encode(result)){
												Ext.Msg.alert('SET','Error en la BD');
												return;
												}
											}
											);
											var records = NS.storeCajasAsignadas.data.items;
											NS.gridCajasPorAsignar.store.add(records);
											NS.gridCajasAsignadas.store.removeAll();
											NS.gridCajasAsignadas.getView().refresh();
											NS.gridCajasPorAsignar.store.sort('idCaja', 'ASC');
										}
									}
								}
							}
					}

		       	]
		    }
        ]
       
    
});


//Funciones para los eventos del grid
  
        var firstGridDropTargetEl =  NS.gridCajasPorAsignar.getView().scroller.dom;
        var firstGridDropTarget = new Ext.dd.DropTarget(firstGridDropTargetEl, {
                ddGroup    : PF+'firstGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridCajasPorAsignar.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var idCaja=records[i].get('idCaja');
                        		var claves=''+noUsuario+','+idCaja;
                        		
 								SegCajaAction.eliminarCajas(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridCajasPorAsignar.store.sort('idCaja', 'ASC');
                        return true;
                }
        });

 
        var secondGridDropTargetEl = NS.gridCajasAsignadas.getView().scroller.dom;
        var secondGridDropTarget = new Ext.dd.DropTarget(secondGridDropTargetEl,{
                ddGroup    : PF+'secondGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridCajasAsignadas.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var idCaja=records[i].get('idCaja');
                        		var claves=''+noUsuario+','+idCaja;
                        		
 								SegCajaAction.asignarCajas(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridCajasAsignadas.store.sort('idCaja', 'ASC');
                        return true;
                }
        });

	NS.contenedorPrincipalCaja.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight()); 

});

 

