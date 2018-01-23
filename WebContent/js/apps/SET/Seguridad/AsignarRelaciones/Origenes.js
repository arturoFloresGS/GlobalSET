//@autor Cristian Garcia Garcia
 Ext.onReady(function(){

	var NS = Ext.namespace('apps.SET.Seguridad.AsignarRelaciones.Origenes');

	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);

	Ext.QuickTips.init(); 
	 // data store para el combo usuarios
	NS.storeUsuarios = new Ext.data.DirectStore( {
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
			}
		}
	}); 
	// load data combo usuarios
	NS.storeUsuarios.load();//Cargar el store al inicio

	NS.cargarGrids = function(usuario){

		NS.storeOrigenesPorAsignar.baseParams.intIdUsuario = usuario;					
		NS.storeOrigenesPorAsignar.baseParams.bExiste = false;					
		NS.storeOrigenesPorAsignar.load();
		
		NS.storeOrigenesAsignados.baseParams.arg1 = usuario;
		NS.storeOrigenesAsignados.baseParams.arg2 = true;
		NS.storeOrigenesAsignados.load();
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
				fn:function(combo, value) {

					Ext.getCmp(PF+'idUsuario').setValue(combo.getValue());
					var varIdUsuario= NS.storeUsuarios.getById(combo.getValue()).get('idUsuario');
					// CARGAR AMBOS GRIDS
					NS.cargarGrids(varIdUsuario);
				}
			}
		}
	});	
	
	
	NS.storeOrigenesPorAsignar = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		paramOrder: ['intIdUsuario', 'bExiste'],
		directFn: SegOrigenAction.obtenerOrigen,
		idProperty: 'origen',
		fields: [
			{name: 'origen' },
			{name: 'descOrigen'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	});


	// crear grid para asignar cajas
	NS.gridOrigenesPorAsignar = new Ext.grid.GridPanel( {
		store : NS.storeOrigenesPorAsignar,
		ddGroup: 'secondGridDDGroup',
		columns : [ {
			id :'origen',
			header :'Origen',
			width :50,
			sortable :true,
			dataIndex :'origen'
		}, {
			header :'Descripción Origen',
			width :245,
			dataIndex :'descOrigen'
		} ],
		enableDragDrop   : true,
		stripeRows :true,
		height :232,
		width :318,
		x :0,
		y :0,
		title :''
	});

	NS.storeOrigenesAsignados = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		paramOrder: ['arg1','arg2'],
		directFn: SegOrigenAction.obtenerOrigen,
		idProperty: 'origen',
		fields: [
			{name: 'origen' },
			{name: 'descOrigen'}
		],
		listeners: {
			load: function(s, records){
			} 
		}
	});
	
	// load data
	//storeAreasAsignadas.load();

	// crea el gride Cajas asignadas
	NS.gridOrigenesAsignados = new Ext.grid.GridPanel( {
		store : NS.storeOrigenesAsignados,
		ddGroup : 'firstGridDDGroup',
		columns : [ {
			id :'origen',
			header :'Origen',
			width :50,
			sortable :true,
			dataIndex :'origen'
		}, {
			header :'Descripción Origen',
			width :245,
			dataIndex :'descOrigen'
		} ],
		enableDragDrop   : true,
		stripeRows :true,
		height :232,
		width :318,
		x :0,
		y :0,
		title :''
	});

NS.contenedorPrincipal =new Ext.FormPanel({
    width: 782,
    height: 415,
    padding:10,
    frame: true,
    autoScroll: true,
    title: 'Asignar Origenes',
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
                items: [
                    {
                       xtype: 'uppertextfield',
                       x: 180,
                        y: 10,
                        width: 100,
                        id: PF+'idUsuario',
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
                        x: 50,
                        y: 10,
                        width: 120
                    },
                    {
                        xtype: 'label',
                        text: 'Usuario',
                        x: 300,
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
		                id:PF+'frameNuevo',
		                items:[NS.gridOrigenesAsignados]
		            },
		            {
		                xtype: 'fieldset',
		                title: 'Por Asignar',
		                x: 0,
		                y: 0,
		                width: 340,
		                height: 268,
		                layout: 'absolute',
		                id: 'frameResgistros',
		                items:[NS.gridOrigenesPorAsignar]
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
										var records = NS.gridOrigenesPorAsignar.getSelectionModel().getSelections();
										for(var i=0; i<records.length; i=i+1)
										{
											var idOrigen = records[i].get('origen');
											var dato = ''+noUsuario+','+idOrigen;
											NS.gridOrigenesAsignados.store.add(records[i]);
											NS.gridOrigenesPorAsignar.store.remove(records[i]);
											NS.gridOrigenesAsignados.store.sort('origen', 'ASC');

											SegOrigenAction.asignarOrigenes(dato, function(result, e){
												NS.gridOrigenesPorAsignar.getStore().reload();

												if(!Ext.encode(result))
													{
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
											});
										}
										NS.gridOrigenesPorAsignar.getView().refresh();
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
											var records = NS.gridOrigenesAsignados.getSelectionModel().getSelections();
											for(var i=0; i<records.length; i=i+1){
												var idOrigen = records[i].get('origen');
												var dato = ''+noUsuario+','+idOrigen;
												NS.gridOrigenesPorAsignar.store.add(records[i]);
												NS.gridOrigenesAsignados.store.remove(records[i]);
												NS.gridOrigenesPorAsignar.store.sort('origen', 'ASC');
												
												SegOrigenAction.eliminarOrigenes(dato, function(result, e){
													NS.gridOrigenesAsignados.getStore().reload();
													
													if(!Ext.encode(result)){
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
												});
											}
											NS.gridOrigenesAsignados.getView().refresh();
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
										//funcion para asignar todos los Origenes
										var claveUsuario =Ext.getCmp(PF+'cmbUsuario').getValue();
										if(claveUsuario=='' || claveUsuario==null)
											Ext.Msg.alert('SET','Seleccione un usuario');
										else{
											var noUsuario = ''+NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('idUsuario');
											SegOrigenAction.asignarOrigenes(noUsuario, function(result, e){
												if(!Ext.encode(result)){
													Ext.Msg.alert('SET','Error en la BD');
													return;
												}
											}
											);
											var records = NS.storeOrigenesPorAsignar.data.items;
											NS.gridOrigenesAsignados.store.add(records);
											NS.gridOrigenesPorAsignar.store.removeAll();
											NS.gridOrigenesPorAsignar.getView().refresh();
											NS.gridOrigenesAsignados.store.sort('origen', 'ASC');
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
									//funcion para eliminar todos los origenes asignados
									fn:function(e) {
										var claveUsuario =Ext.getCmp(PF+'cmbUsuario').getValue();
										if(claveUsuario=='' || claveUsuario==null)
										Ext.Msg.alert('SET','Seleccione un usuario');
										else{
											var noUsuario = ''+NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('idUsuario');
											SegOrigenAction.eliminarOrigenes(noUsuario, function(result, e){
												if(!Ext.encode(result)){
												Ext.Msg.alert('SET','Error en la BD');
												return;
												}
											}
											);
											var records = NS.storeOrigenesAsignados.data.items;
											NS.gridOrigenesPorAsignar.store.add(records);
											NS.gridOrigenesAsignados.store.removeAll();
											NS.gridOrigenesAsignados.getView().refresh();
											NS.gridOrigenesPorAsignar.store.sort('origen', 'ASC');
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

   
        var firstGridDropTargetEl =  NS.gridOrigenesPorAsignar.getView().scroller.dom;
        var firstGridDropTarget = new Ext.dd.DropTarget(firstGridDropTargetEl, {
                ddGroup    : 'firstGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridOrigenesPorAsignar.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var idOrigen=records[i].get('origen');
                        		var claves=''+noUsuario+','+idOrigen;
                        		
 								SegOrigenAction.eliminarOrigenes(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridOrigenesPorAsignar.store.sort('origen', 'ASC');
                        return true
                }
        });


      
        var secondGridDropTargetEl = NS.gridOrigenesAsignados.getView().scroller.dom;
        var secondGridDropTarget = new Ext.dd.DropTarget(secondGridDropTargetEl,{
                ddGroup    : 'secondGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridOrigenesAsignados.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var idOrigen=records[i].get('origen');
                        		var claves=''+noUsuario+','+idOrigen;
                        		
 								SegOrigenAction.asignarOrigenes(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridOrigenesAsignados.store.sort('origen', 'ASC');
                        return true
                }
        });

	NS.contenedorPrincipal.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight()); 
	    
});

 


