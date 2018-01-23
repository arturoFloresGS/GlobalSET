//@autor Cristian Garcia Garcia
 Ext.onReady(function(){

	var NS = Ext.namespace('apps.SET.Seguridad.AsignarRelaciones.Personas');

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

		NS.storePersonasPorAsignar.baseParams.intIdUsuario = usuario;					
		NS.storePersonasPorAsignar.baseParams.bExiste = false;					
		NS.storePersonasPorAsignar.load();
		
		NS.storePersonasAsignadas.baseParams.arg1 = usuario;
		NS.storePersonasAsignadas.baseParams.arg2 = true;
		NS.storePersonasAsignadas.load();
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
					var varIdUsuario = NS.storeUsuarios.getById(combo.getValue()).get('idUsuario');
					NS.cargarGrids(varIdUsuario);
				}
			}
		}
	});	
	
	
	NS.storePersonasPorAsignar = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			intIdUsuario:0,
			bExiste: false
		},
		root: '',
		paramOrder: ['intIdUsuario', 'bExiste'],
		directFn: SegPersonasAction.obtenerPersonas,
		idProperty: 'noPersona',
		fields: [
			{name: 'noPersona' },
			{name: 'razonSocial'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePersonasPorAsignar, msg:"Cargando..."});
			
			}
		}
	});


	// crear grid para asignar Personas
	NS.gridPersonasPorAsignar = new Ext.grid.GridPanel( {
		ddGroup: 'secondGridDDGroup',
		store : NS.storePersonasPorAsignar,
		columns : [ 
		{
			id :'noPersona',
			header :'No. Persona',
			width :80,
			sortable :true,
			dataIndex :'noPersona'
		}, {
			header :'Razón Social',
			width :245,
			sortable: true,
			dataIndex :'razonSocial'
		} ],
			enableDragDrop   : true,
		stripeRows :true,
		height :232,
		width :318,
		x :0,
		y :0,
		title :''
	});

	NS.storePersonasAsignadas = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		paramOrder: ['arg1','arg2'],
		directFn: SegPersonasAction.obtenerPersonas,
		idProperty: 'noPersona',
		fields: [
			{name: 'noPersona' },
			{name: 'razonSocial'}
		],
		listeners: {
			load: function(s, records){
			} 
		}
	});
	

	// crea el gride Personas asignadas
	NS.gridPersonasAsignadas = new Ext.grid.GridPanel( {
		ddGroup          : 'firstGridDDGroup',
		store : NS.storePersonasAsignadas,
		columns : [ 
		{
			id :'noPersona',
			header :'No. Persona',
			width :80,
			sortable :true,
			dataIndex :'noPersona'
		}, {
			header :'Razón Social',
			width :245,
			dataIndex :'razonSocial'
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
    title: 'Asignar Personas',
    layout: 'absolute',
    renderTo: NS.tabContId,
    html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
         items : [
            {
                xtype: 'fieldset',
                title: 'Búsqueda',
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
                        id:PF+'idUsuario',
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
										Ext.getCmp(PF+'cmbUsuario').setRawValue(Ext.getCmp(PF+'idUsuario').getValue());
										
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
                        x:300,
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
		                items:[NS.gridPersonasAsignadas]
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
		                items:[NS.gridPersonasPorAsignar]
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
										var noUsuario = NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
										var records = NS.gridPersonasPorAsignar.getSelectionModel().getSelections();
										for(var i=0; i<records.length; i=i+1)
										{
											var noPersona = records[i].get('noPersona');
											var dato = ''+noUsuario+','+noPersona;
											NS.gridPersonasAsignadas.store.add(records[i]);
											NS.gridPersonasPorAsignar.store.remove(records[i]);
											NS.gridPersonasAsignadas.store.sort('noPersona', 'ASC');

											SegPersonasAction.asignarPersonas(dato, function(result, e){
												NS.gridPersonasPorAsignar.getStore().reload();
												if(!Ext.encode(result))
													{
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
											});
										}
										NS.gridPersonasPorAsignar.getView().refresh();
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
											var noUsuario = NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
											var records = NS.gridPersonasAsignadas.getSelectionModel().getSelections();
											for(var i=0; i<records.length; i=i+1){
												var noPersona = records[i].get('noPersona');
												var dato = ''+noUsuario+','+noPersona;
												NS.gridPersonasPorAsignar.store.add(records[i]);
												NS.gridPersonasAsignadas.store.remove(records[i]);
												NS.gridPersonasPorAsignar.store.sort('noPersona', 'ASC');
												
												SegPersonasAction.eliminarPersonas(dato, function(result, e){
													NS.gridPersonasAsignadas.getStore().reload();
													if(!Ext.encode(result)){
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
												});
											}
											NS.gridPersonasAsignadas.getView().refresh();
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
											var noUsuario = ''+NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
											SegPersonasAction.asignarPersonas(noUsuario, function(result, e){
												if(!Ext.encode(result)){
													Ext.Msg.alert('SET','Error en la BD');
													return;
												}
											}
											);
											var records = NS.storePersonasPorAsignar.data.items;
											NS.gridPersonasAsignadas.store.add(records);
											NS.gridPersonasPorAsignar.store.removeAll();
											NS.gridPersonasPorAsignar.getView().refresh();
											NS.gridPersonasAsignadas.store.sort('noPersona', 'ASC');
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
											var noUsuario = ''+NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
											SegPersonasAction.eliminarPersonas(noUsuario, function(result, e){
												if(!Ext.encode(result)){
												Ext.Msg.alert('SET','Error en la BD');
												return;
												}
											}
											);
											var records = NS.storePersonasAsignadas.data.items;
											NS.gridPersonasPorAsignar.store.add(records);
											NS.gridPersonasAsignadas.store.removeAll();
											NS.gridPersonasAsignadas.getView().refresh();
											NS.gridPersonasPorAsignar.store.sort('noPersona', 'ASC');
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

   
        var firstGridDropTargetEl =  NS.gridPersonasPorAsignar.getView().scroller.dom;
        var firstGridDropTarget = new Ext.dd.DropTarget(firstGridDropTargetEl, {
                ddGroup    : 'firstGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridPersonasPorAsignar.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var noPersona=records[i].get('noPersona');
                        		var claves=''+noUsuario+','+noPersona;
                        		
 								SegPersonasAction.eliminarPersonas(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridPersonasPorAsignar.store.sort('noPersona', 'ASC');
                        return true
                }
        });

 
        var secondGridDropTargetEl = NS.gridPersonasAsignadas.getView().scroller.dom;
        var secondGridDropTarget = new Ext.dd.DropTarget(secondGridDropTargetEl,{
                ddGroup    : 'secondGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridPersonasAsignadas.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var noPersona=records[i].get('noPersona');
                        		var claves=''+noUsuario+','+noPersona;
 								SegPersonasAction.asignarPersonas(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridPersonasAsignadas.store.sort('noPersona', 'ASC');
                        return true
                }
        });

	NS.contenedorPrincipal.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight()); 
        
});

