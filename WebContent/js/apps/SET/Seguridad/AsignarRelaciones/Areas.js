//@autor Cristian Garcia Garcia
Ext.onReady(function(){

	var NS = Ext.namespace('apps.SET.Seguridad.AsignarRelaciones.Areas');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
    Ext.QuickTips.init();  
	// make sample array data
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
	// load data
	NS.storeUsuarios.load();//Cargar el store al inicio
	
	NS.cargarGrids = function(usuario){
	
		//alert('usuario='+usuario);
		NS.storeAreasPorAsignar.baseParams.intIdUsuario = usuario;
		NS.storeAreasPorAsignar.baseParams.bExiste = false;
		NS.storeAreasPorAsignar.load();
		
		NS.storeAreasAsignadas.baseParams.arg1 = usuario;
		NS.storeAreasAsignadas.baseParams.arg2 = true;
		NS.storeAreasAsignadas.load()
		
	}; 
	
	
	NS.cmbUsuario = new Ext.form.ComboBox({
		 store: NS.storeUsuarios
		,name: PF+'cmbUsuario'//id y name igaules
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
					NS.cargarGrids(varIdUsuario);
				}
			}
		}
	});	

NS.storeAreasPorAsignar = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			 intIdUsuario: 2
			,bExiste: false
		},
		root: '',
		paramOrder: ['intIdUsuario', 'bExiste'],
		directFn: SegAreaAction.obtenerAreas,
		idProperty: 'idArea',
		fields: [
			{name: 'idArea' },
			{name: 'descArea'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAreasPorAsignar, msg:"Cargando..."});
			}
		}
	});
	// load data
	//storeAreasPorAsignar.load();
	// create the grid
	NS.gridAreasPorAsignar  = new Ext.grid.GridPanel({
		ddGroup: 'secondGridDDGroup',
		store : NS.storeAreasPorAsignar,
		columns : [ {
			id :'idArea',
			header :'Id Area',
			width :50,
			sortable :true,
			dataIndex :'idArea'
		}, {
			header :'Descripcion',
			width :245,
			dataIndex :'descArea'
		} ],
		enableDragDrop   : true,
		stripeRows :true,
		height: 232,
		width :318,
		x :0,
		y :0,
		title :''
	});

	 NS.storeAreasAsignadas = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			 arg1: 1
			,arg2: true
		},
		root: '',
		paramOrder: ['arg1','arg2'],
		directFn: SegAreaAction.obtenerAreas,
		idProperty: 'idArea',
		fields: [
			{name: 'idArea' },
			{name: 'descArea'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAreasAsignadas, msg:"Cargando..."});
			} 
		}
	});
	
	// load data
	//storeAreasAsignadas.load();

	// create the grid
	NS.gridAreasAsignadas  = new Ext.grid.GridPanel({
		ddGroup: 'firstGridDDGroup',
		store : NS.storeAreasAsignadas,
		columns : [ {
			id :'idArea',
			header :'Id Area',
			width :50,
			sortable :true,
			dataIndex :'idArea'
		}, {
			header :'Descripcion',
			width :245,
			dataIndex :'descArea'
		} ],
		enableDragDrop   : true,
		stripeRows :true,
		height: 232,
		width :318,
		x :0,
		y :0,
		title :''
	});


NS.contenedorPrincipalArea = new Ext.FormPanel({
	name: PF+'contenedorPrincipal',
	id: PF+'contenedorPrincipal',
	width: 782,
	height: 415,
	frame: true,
	//padding:10,
	autoScroll: true,
	title: 'Asignar Areas',
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
                id: PF+'frameBusquedaArea',
                plain: false,
        		bodyStyle: 'background:#E9EFD1',
                items: [
                    {
                        xtype: 'uppertextfield', // plugin en util, anterior: 'textfield', 
                        x: 180,
                        y: 10,
                        width: 100,
                        id: PF+'idUsuario',
						listeners: {
							change:{
								fn:function(e) // al salir del campo verificar cambios  
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
                        x: 310,
                        y: 10
                    }
                ]
            },
            {
            	xtype: 'fieldset',
                id: PF+'frameAsignacionArea',
				title: '',
				x: 10,
				y: 90,
	            width: 760, // quitar para maximizar al 100%
				height: 290,
				//padding:10,
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
                id: PF+'frameNuevoArea',
                items:[NS.gridAreasAsignadas ]
            },
            {
                xtype: 'fieldset',
                title: 'Por Asignar',
                x: 0,
                y: 0,
                width: 340,
                height: 268,
                layout: 'absolute',
                id: PF+'frameResgistrosArea',
                items:[NS.gridAreasPorAsignar]
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
										var records = NS.gridAreasPorAsignar.getSelectionModel().getSelections();
										for(var i=0; i<records.length; i=i+1)
										{
											var idArea = records[i].get('idArea');
											var dato = ''+noUsuario+','+idArea;
											NS.gridAreasAsignadas.store.add(records[i]);
											NS.gridAreasPorAsignar.store.remove(records[i]);
											NS.gridAreasAsignadas.store.sort('idArea', 'ASC');

											SegAreaAction.asignarAreas(dato, function(result, e){
												NS.gridAreasPorAsignar.getStore().reload();
											
												if(!Ext.encode(result))
													{
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
											}
										);
										}
										NS.gridAreasPorAsignar.getView().refresh();
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
											var records = NS.gridAreasAsignadas.getSelectionModel().getSelections();
											for(var i=0; i<records.length; i=i+1){
												var idArea = records[i].get('idArea');
												var dato = ''+noUsuario+','+idArea;
												NS.gridAreasPorAsignar.store.add(records[i]);
												NS.gridAreasAsignadas.store.remove(records[i]);
												NS.gridAreasPorAsignar.store.sort('idArea', 'ASC');
												
												SegAreaAction.eliminarAreas(dato, function(result, e){
													NS.gridAreasAsignadas.getStore().reload();
												
													if(!Ext.encode(result)){
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
												});
											}
											NS.gridAreasAsignadas.getView().refresh();
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
											SegAreaAction.asignarAreas(noUsuario, function(result, e){
												if(!Ext.encode(result)){
													Ext.Msg.alert('SET','Error en la BD');
													return;
												}
											}
											);
											var records = NS.storeAreasPorAsignar.data.items;
											NS.gridAreasAsignadas.store.add(records);
											NS.gridAreasPorAsignar.store.removeAll();
											NS.gridAreasPorAsignar.getView().refresh();
											NS.gridAreasAsignadas.store.sort('idArea', 'ASC');
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
											SegAreaAction.eliminarAreas(noUsuario, function(result, e){
												if(!Ext.encode(result)){
												Ext.Msg.alert('SET','Error en la BD');
												return;
												}
											}
											);
											var records = NS.storeAreasAsignadas.data.items;
											NS.gridAreasPorAsignar.store.add(records);
											NS.gridAreasAsignadas.store.removeAll();
											NS.gridAreasAsignadas.getView().refresh();
											NS.gridAreasPorAsignar.store.sort('idArea', 'ASC');
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
    var firstGridDropTargetEl =  NS.gridAreasPorAsignar.getView().scroller.dom;
    var firstGridDropTarget = new Ext.dd.DropTarget(firstGridDropTargetEl, {
            ddGroup    : 'firstGridDDGroup',
            notifyDrop : function(ddSource, e, data){
                    var records =  ddSource.dragData.selections;
                    Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                    NS.gridAreasPorAsignar.store.add(records);
                    var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                    for(var i=0;i<records.length;i++)
                    	{
                    		var idArea=records[i].get('idArea');
                    		var claves=''+noUsuario+','+idArea;
                    		
					SegAreaAction.eliminarAreas(claves, function(result, e){
					if(!Ext.encode(result)){
						Ext.Msg.alert('SET','Error en la BD');
							return;
								}
							}
						);			 
                    	}
                    NS.gridAreasPorAsignar.store.sort('idArea', 'ASC');
                    return true
            }
    });


    var secondGridDropTargetEl = NS.gridAreasAsignadas.getView().scroller.dom;
    var secondGridDropTarget = new Ext.dd.DropTarget(secondGridDropTargetEl,{
            ddGroup    : 'secondGridDDGroup',
            notifyDrop : function(ddSource, e, data){
                    var records =  ddSource.dragData.selections;
                    Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                    NS.gridAreasAsignadas.store.add(records);
                    var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                    for(var i=0;i<records.length;i++)
                    	{
                    		var idArea=records[i].get('idArea');
                    		var claves=''+noUsuario+','+idArea;
                    		
					SegAreaAction.asignarAreas(claves, function(result, e){
					if(!Ext.encode(result)){
						Ext.Msg.alert('SET','Error en la BD');
							return;
								}
							}
						);			 
                    	}
                    NS.gridAreasAsignadas.store.sort('idArea', 'ASC');
                    return true
            }
    });
    
	NS.contenedorPrincipalArea.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight()); 
    
});

