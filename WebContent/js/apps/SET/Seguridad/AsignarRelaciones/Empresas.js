//@autor Cristian Garcia Garcia
Ext.onReady(function(){

	var NS = Ext.namespace('apps.SET.Seguridad.AsignarRelaciones.Empresas');

	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);

	Ext.QuickTips.init();
	NS.nombreUsuario='';
	NS.noUsuario='';

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
	
		NS.storeEmpresasPorAsignar.baseParams.intIdUsuario = usuario;	
		NS.storeEmpresasPorAsignar.baseParams.bExiste = false;
		NS.storeEmpresasPorAsignar.load();
		
		NS.storeEmpresasAsignadas.baseParams.arg1 = usuario;
		NS.storeEmpresasAsignadas.baseParams.arg2 = true;
		NS.storeEmpresasAsignadas.load();
		
		
		
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
					NS.nombreUsuario=combo.getRawValue();
					NS.noUsuario=combo.getValue();
					
					Ext.getCmp(PF+'idUsuario').setValue(combo.getValue());
					
					var varIdUsuario= NS.storeUsuarios.getById(combo.getValue()).get('idUsuario');
					NS.cargarGrids(varIdUsuario);
					
					
				}
			}
		}
	});	
	
	NS.storeEmpresasPorAsignar = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			 intIdUsuario: 2
			,bExiste: false
		},
		root: '',
		paramOrder: ['intIdUsuario', 'bExiste'],
		directFn: SegEmpresaAction.obtenerEmpresas,
		idProperty: 'idEmpresas',
		fields: [
			{name: 'noEmpresa' },
			{name: 'nombreEmpresa'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresasPorAsignar, msg:"Cargando..."});
			
			}
		}
	});

	// crear grid para asignar cajas
	NS.gridEmpresasPorAsignar = new Ext.grid.GridPanel( {
		name: PF+'gridEmpresasPorAsignar',
		id: PF+'gridEmpresasPorAsignar',
		ddGroup: PF+'secondGridDDGroup',
		store : NS.storeEmpresasPorAsignar,
		columns : [ {
			id :'noEmpresa',
			header :'No. Empresa',
			width :50,
			sortable :true,
			dataIndex :'noEmpresa'
		}, {
			header :'Nombre de la Empresa',
			width :245,
			dataIndex :'nombreEmpresa'
		} ],
		enableDragDrop   : true,
		stripeRows :true,
		height :232,
		width :318,
		x :0,
		y :0,
		title :''
	});

	NS.storeEmpresasAsignadas = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			 arg1: 1
			,arg2: true
		},
		root: '',
		paramOrder: ['arg1','arg2'],
		directFn: SegEmpresaAction.obtenerEmpresas,
		idProperty: 'noEmpresa',
		fields: [
			{name: 'noEmpresa' },
			{name: 'nombreEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresasAsignadas, msg:"Cargando..."});
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No cuenta con empresas asignadas..');
				}else{
					
				}
				
			} 
		}
	});
	
	// load data
	//storeAreasAsignadas.load();

	// crea el gride Cajas asignadas
	NS.gridEmpresasAsignadas = new Ext.grid.GridPanel( {
		name: PF+'gridEmpresasAsignadas',
		id: PF+'gridEmpresasAsignadas',
		ddGroup: 'firstGridDDGroup',
		store : NS.storeEmpresasAsignadas,
		columns : [ {
			id :'noEmpresa',
			header :'No. Empresa',
			width :50,
			sortable :true,
			dataIndex :'noEmpresa'
		}, {
			header :'Nombre de la Empresa',
			width :245,
			dataIndex :'nombreEmpresa'
		} ],
		enableDragDrop   : true,
		stripeRows :true,
		height :232,
		width :318,
		x :0,
		y :0,
		title :''
	});

	/********************** Generar el excel *********************************/
	
	 NS.validaDatosExcel = function() {		
		var registroSeleccionado = NS.gridEmpresasAsignadas.getSelectionModel().getSelections();
		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(registroSeleccionado.length == 0) {
			registroSeleccionado = NS.storeEmpresasAsignadas.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		var matriz = new Array();	
		for(var i=0;i<registroSeleccionado.length;i++){
			var vector = {};
			vector.noUsuario= NS.noUsuario;
			vector.nombreUsuario= NS.nombreUsuario;
			vector.noEmpresa=registroSeleccionado[i].get('noEmpresa');
			vector.nombreEmpresa=registroSeleccionado[i].get('nombreEmpresa');
			matriz[i] = vector;
		}
		
		var jSonString = Ext.util.JSON.encode(matriz);
		NS.exportaExcel(jSonString);
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		SegEmpresaAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=usuarioEmpresa';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
	
	/****************Fin de generar excel ***********************/


NS.contenedorPrincipal =new Ext.FormPanel({
    width: 782,
    height: 415,
    padding:10,
    frame: true,
    autoScroll: true,
    title: 'Asignar Empresas',
    layout: 'absolute',
    renderTo: NS.tabContId,
    html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
         items : [
				{
					xtype: 'button',
					text: 'Excel',
					x: 650,
					y: 400,
					width: 80,
					listeners:{
						click:{
							fn:function(e) {
								NS.validaDatosExcel();
							}
						}
					}
				},
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
                items:[NS.gridEmpresasAsignadas]
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
                items:[NS.gridEmpresasPorAsignar]
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
										var records = NS.gridEmpresasPorAsignar.getSelectionModel().getSelections();
										for(var i=0; i<records.length; i=i+1)
										{
											var noEmpresa = records[i].get('noEmpresa');
											var dato = ''+noUsuario+','+noEmpresa;
											NS.gridEmpresasAsignadas.store.add(records[i]);
											NS.gridEmpresasPorAsignar.store.remove(records[i]);
											NS.gridEmpresasAsignadas.store.sort('noEmpresa', 'ASC');
											SegEmpresaAction.asignarEmpresas(dato, function(result, e)
											{
												NS.gridEmpresasPorAsignar.getStore().reload();
											
												if(!Ext.encode(result))
													{
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
											}
										);
										}
										NS.gridEmpresasPorAsignar.getView().refresh();
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
											var records = NS.gridEmpresasAsignadas.getSelectionModel().getSelections();
											for(var i=0; i<records.length; i=i+1){
												var noEmpresa = records[i].get('noEmpresa');
												var dato = ''+noUsuario+','+noEmpresa;
												NS.gridEmpresasPorAsignar.store.add(records[i]);
												NS.gridEmpresasAsignadas.store.remove(records[i]);
												NS.gridEmpresasPorAsignar.store.sort('noEmpresa', 'ASC');
												
												SegEmpresaAction.eliminarEmpresas(dato, function(result, e){
													NS.gridEmpresasAsignadas.getStore().reload();

													if(!Ext.encode(result)){
														Ext.Msg.alert('SET','Error en la BD');
														return;
													}
												});
											}
											NS.gridEmpresasAsignadas.getView().refresh();
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
											SegEmpresaAction.asignarEmpresas(noUsuario, function(result, e){
												if(!Ext.encode(result)){
													Ext.Msg.alert('SET','Error en la BD');
													return;
												}
											}
											);
											var records = NS.storeEmpresasPorAsignar.data.items;
											NS.gridEmpresasAsignadas.store.add(records);
											NS.gridEmpresasPorAsignar.store.removeAll();
											NS.gridEmpresasPorAsignar.getView().refresh();
											NS.gridEmpresasAsignadas.store.sort('noEmpresa', 'ASC');
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
											SegEmpresaAction.eliminarEmpresas(noUsuario, function(result, e){
												if(!Ext.encode(result)){
												Ext.Msg.alert('SET','Error en la BD');
												return;
												}
											}
											);
											var records = NS.storeEmpresasAsignadas.data.items;
											NS.gridEmpresasPorAsignar.store.add(records);
											NS.gridEmpresasAsignadas.store.removeAll();
											NS.gridEmpresasAsignadas.getView().refresh();
											NS.gridEmpresasPorAsignar.store.sort('noEmpresa', 'ASC');
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
        var firstGridDropTargetEl =  NS.gridEmpresasPorAsignar.getView().scroller.dom;
        var firstGridDropTarget = new Ext.dd.DropTarget(firstGridDropTargetEl, {
                ddGroup    : 'firstGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridEmpresasPorAsignar.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var noEmpresa=records[i].get('noEmpresa');
                        		var claves=''+noUsuario+','+noEmpresa;
 								SegEmpresaAction.eliminarEmpresas(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridEmpresasPorAsignar.store.sort('noEmpresa', 'ASC');
                        return true
                }
        });

        var secondGridDropTargetEl = NS.gridEmpresasAsignadas.getView().scroller.dom;
        var secondGridDropTarget = new Ext.dd.DropTarget(secondGridDropTargetEl,{
                ddGroup    : PF+'secondGridDDGroup',
                notifyDrop : function(ddSource, e, data){
                        var records =  ddSource.dragData.selections;
                        Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                        NS.gridEmpresasAsignadas.store.add(records);
                        var noUsuario=NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
                        for(var i=0;i<records.length;i++)
                        	{
                        		var noEmpresa=records[i].get('noEmpresa');
                        		var claves=''+noUsuario+','+noEmpresa;
 								SegEmpresaAction.asignarEmpresas(claves, function(result, e){
									if(!Ext.encode(result)){
										Ext.Msg.alert('SET','Error en la BD');
											return;
												}
											}
										);			 
                        	}
                        NS.gridEmpresasAsignadas.store.sort('noEmpresa', 'ASC');
                        return true
                }
        });
        

	NS.contenedorPrincipal.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight()); 

});
