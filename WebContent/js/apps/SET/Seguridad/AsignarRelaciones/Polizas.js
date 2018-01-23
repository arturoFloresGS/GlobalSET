//@autor Edgar Estrada
Ext.onReady(function(){

	var NS = Ext.namespace('apps.SET.Seguridad.AsignarRelaciones.Polizas');

	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);

	Ext.QuickTips.init();

	// data store para el combo usuarios
	NS.storeUsuarios = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: SegPolizasAction.obtenerUsuarios,//llamada a la clase del Action
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
	
		NS.storePolizasPorAsignar.baseParams.intIdUsuario = usuario;	
		NS.storePolizasPorAsignar.baseParams.bExiste = false;
		NS.storePolizasPorAsignar.load();
		
		NS.storePolizasAsignadas.baseParams.arg1 = usuario;
		NS.storePolizasAsignadas.baseParams.arg2 = true;
		NS.storePolizasAsignadas.load();
		
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
					NS.cargarGrids(varIdUsuario);
				}
			}
		}
	});	
	
	NS.storePolizasPorAsignar = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			 intIdUsuario: 2
			,bExiste: false
		},
		root: '',
		paramOrder: ['intIdUsuario', 'bExiste'],
		directFn: SegPolizasAction.obtenerPolizas,
		idProperty: 'idPolizas',
		fields: [
			{name: 'idPoliza' },
			{name: 'nombrePolizas'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePolizasPorAsignar, msg:"Cargando..."});
			
			}
		}
	});

	// crear grid para asignar cajas
	NS.gridPolizasPorAsignar = new Ext.grid.GridPanel( {
		name: PF+'gridPolizasPorAsignar',
		id: PF+'gridPolizasPorAsignar',
		ddGroup: PF+'secondGridDDGroup',
		store : NS.storePolizasPorAsignar,
		columns : [ {
			id :'idPoliza',
			header :'No. Polizas',
			width :50,
			sortable :true,
			dataIndex :'idPoliza'
		}, {
			header :'Nombre de la Poliza',
			width :245,
			dataIndex :'nombrePolizas'
		} ],
		enableDragDrop   : true,
		stripeRows :true,
		height :232,
		width :318,
		x :0,
		y :0,
		title :''
	});

	NS.storePolizasAsignadas = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			 arg1: 1
			,arg2: true
		},
		root: '',
		paramOrder: ['arg1','arg2'],
		directFn: SegPolizasAction.obtenerPolizas,
		idProperty: 'noPoliza',
		fields: [
			{name: 'idPoliza' },
			{name: 'nombrePolizas'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePolizasAsignadas, msg:"Cargando..."});
			} 
		}
	});
	
	// load data
	//storeAreasAsignadas.load();

	// crea el gride Cajas asignadas
	NS.gridPolizasAsignadas = new Ext.grid.GridPanel( {
		name: PF+'gridPolizasAsignadas',
		id: PF+'gridPolizasAsignadas',
		ddGroup: 'firstGridDDGroup',
		store : NS.storePolizasAsignadas,
		columns : [ {
			id :'idPoliza',
			header :'No. Poliza',
			width :50,
			sortable :true,
			dataIndex :'idPoliza'
		}, {
			header :'Nombre de la Poliza',
			width :245,
			dataIndex :'nombrePolizas'
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
    title: 'Asignar Polizas',
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
										Ext.Msg.alert('Información SET', 'El usuario '+Ext.getCmp(PF+'idUsuario').getValue()+' no existe.');
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
                id: PF+'frameNuevo',
                items:[NS.gridPolizasAsignadas]
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
                items:[NS.gridPolizasPorAsignar]
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
										var records = NS.gridPolizasPorAsignar.getSelectionModel().getSelections();
										for(var i=0; i<records.length; i=i+1)
										{
											var idPoliza = records[i].get('idPoliza');
											var dato = ''+noUsuario+','+idPoliza;
											NS.gridPolizasPorAsignar.store.remove(records[i]);
											NS.gridPolizasAsignadas.store.add(records[i]);
											
											NS.gridPolizasAsignadas.store.sort('idPoliza', 'ASC');
											SegPolizasAction.asignarPoliza(dato, function(result, e)
											{
												NS.gridPolizasPorAsignar.getStore().reload();
											
												if(!Ext.encode(result))
													{
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
											}
										);
										}
										NS.gridPolizasPorAsignar.getView().refresh();
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
											var records = NS.gridPolizasAsignadas.getSelectionModel().getSelections();
											
											for(var i=0; i<records.length; i=i+1){
												var idPoliza = records[i].get('idPoliza');
												var dato = ''+noUsuario+','+idPoliza;
												NS.gridPolizasAsignadas.store.remove(records[i]);
												NS.gridPolizasPorAsignar.store.add(records[i]);
												//NS.gridPolizasAsignadas.store.remove(records[i]);
												NS.gridPolizasPorAsignar.store.sort('idPoliza', 'ASC');
												SegPolizasAction.eliminarPolizas(dato, function(result, e){
												//SegPolizasAction.eliminarPolizas(dato, function(result, e){
													NS.gridPolizasAsignadas.getStore().reload();

													if(!Ext.encode(result)){
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
												});
											}
											NS.gridPolizasAsignadas.getView().refresh();
											
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
											SegPolizasAction.asignarPoliza(noUsuario, function(result, e){
												if(!Ext.encode(result)){
													Ext.Msg.alert('SET','Error en la BD');
													return;
												}
											}
											);
											var records = NS.storePolizasPorAsignar.data.items;
											NS.gridPolizasAsignadas.store.add(records);
											NS.gridPolizasPorAsignar.store.removeAll();
											NS.gridPolizasPorAsignar.getView().refresh();
											NS.gridPolizasAsignadas.store.sort('idPoliza', 'ASC');
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
											var noUsuario =''+NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('idUsuario');
											SegPolizasAction.eliminarPolizas(noUsuario, function(result, e){
												if(!Ext.encode(result)){
												Ext.Msg.alert('SET','Error en la BD');
												return;
												}
											}
											);
											var records = NS.storePolizasAsignadas.data.items;
											NS.gridPolizasPorAsignar.store.add(records);
											NS.gridPolizasAsignadas.store.removeAll();
											NS.gridPolizasAsignadas.getView().refresh();
											NS.gridPolizasPorAsignar.store.sort('idPoliza', 'ASC');
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
        var firstGridDropTargetEl =  NS.gridPolizasPorAsignar.getView().scroller.dom;
        var firstGridDropTarget = new Ext.dd.DropTarget(firstGridDropTargetEl, {
                ddGroup    : 'firstGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridPolizasPorAsignar.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var idPoliza=records[i].get('idPoliza');
                        		var claves=''+noUsuario+','+idPoliza;
 								SegEmpresaAction.eliminarEmpresas(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridPolizasPorAsignar.store.sort('idPoliza', 'ASC');
                        return true
                }
        });

        var secondGridDropTargetEl = NS.gridPolizasAsignadas.getView().scroller.dom;
        var secondGridDropTarget = new Ext.dd.DropTarget(secondGridDropTargetEl,{
                ddGroup    : PF+'secondGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridPolizasAsignadas.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var idPoliza=records[i].get('idPoliza');
                        		var claves=''+noUsuario+','+idPoliza;
 								SegPolizasAction.asignarPoliza(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridPolizasAsignadas.store.sort('idPoliza', 'ASC');
                        return true
                }
        });

	NS.contenedorPrincipal.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight()); 

});