/**
 * 
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoComponentes.AsignarBotones');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.';
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	
	
	/************** BOX'S ***************/
	
	NS.txtIdAsignacion = new Ext.form.TextField({
		id: PF + 'txtIdAsignacion',
		name: PF + 'txtIdAsignacion',
		x: 100,
		y: 60,
		width: 250,
		emptyText: 'IDENTIFICADOR',
		style: {
			textAlign: 'center',
        }
	});
	
	
	
	/*************** COMBOS **************/
	
	NS.storeModulo = new Ext.data.DirectStore({
    	paramsAsHash: false,
        baseParams: {
			tipoComponente:1
		},
		root: '',
		paramOrder:['tipoComponente'],
		directFn: SegComponentesAction.llenaComponentes,
		idProperty: 'idComponente',
		fields: [
			{name: 'idComponente'},
			{name: 'etiqueta'},
			{name: 'estatus'},
			{name: 'rutaImagen'}
		]
    });
    
    NS.storeModulo.load();
    NS.storeModulo.sort('etiqueta', 'ASC');
    
    // combo de busqueda descripcion
    NS.cmbModulo = new Ext.form.ComboBox({
        store: NS.storeModulo,
        name: PF+'cmbModulo',
        id: PF+'cmbModulo',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 495,
        y: 10,
        width: 205,
        valueField: 'idComponente',
        displayField: 'etiqueta',
        autocomplete: true,
        emptyText: 'Seleccione un Modulo',
        triggerAction: 'all',
        value: '',
        listeners: {
            select: {
                fn: function(combo, value) {
                }
            }
        }
    });

	
	
	
	NS.storeUsuarios = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: SegComponentesAction.obtenerUsuarios,
		idProperty: 'claveUsuario',
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
	NS.storeUsuarios.load(); 
	
	NS.cmbUsuario = new Ext.form.ComboBox({
		 store: NS.storeUsuarios
		,name: PF+'cmbUsuario'
		,id: PF+'cmbUsuario'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :235
		,y :10
		,width :200
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
					//Ext.Msg.alert('SET', NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario'));
				}
			}
		}
	});
	
	/*************** GRID'S ****************/
	
	
	
	NS.storeBotonesAsignados = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			 idUsuario: 1
			,idModulo: 1
		},
		root: '',
		paramOrder: ['idUsuario', 'idModulo'],
		directFn: SegComponentesAction.obtenerBotonesAsignados,
		idProperty: 'claveComponente',
		fields: [
			{name: 'claveComponente'},
			{name: 'descripcion'},
			
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBotonesAsignados, msg:"Cargando..."});
			
			}
		}
	});
	
	NS.gridBotonesAsignados = new Ext.grid.GridPanel({
		name: PF+'gridBotonesAsignados',
		id: PF+'gridBotonesAsignados',
		store : NS.storeBotonesAsignados,
		columns : [  
		{
			header :'Identificador',
			width :150,
			dataIndex :'claveComponente'
		} ,{
			header :'Descripcion',
			width :150,
			dataIndex :'descripcion'
		}		
		],
		selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
		stripeRows :true,
		height :235,
		width :338,
		x :0,
		y :0,
		title :''
	});
	
	
	NS.storeBotonesPorAsignar = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			 idUsuario: 1
			,idModulo: 1
		},
		root: '',
		paramOrder: ['idUsuario', 'idModulo'],
		directFn: SegComponentesAction.obtenerBotonesSinAsignar,
		idProperty: 'claveComponente',
		fields: [
			{name: 'claveComponente'},
			{name: 'descripcion'},
			{name: 'etiqueta'}
		],
		listeners: {
			load: function(s, records){
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBotonesPorAsignar, msg:"Cargando..."});
			
			}
		}
	});
	
	
	NS.gridBotonesPorAsignar = new Ext.grid.GridPanel( {
		name: PF+'gridBotonesPorAsignar',
		id: PF+'gridBotonesPorAsignar',
		store : NS.storeBotonesPorAsignar,
		columns : [ {
			id :'claveComponente',
			header :'Id Componente ',
			width :50,
			sortable :true,
			dataIndex :'claveComponente'
		}, {
			header :'Descripcion',
			width :150,
			dataIndex :'descripcion'
		} ,{
			header :'Pantalla',
			width :150,
			dataIndex :'etiqueta'
		}		
		],
		selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
		stripeRows :true,
		height :235,
		width :338,
		x :0,
		y :0,
		title :''
	});
	
		
	/*************** PANEL'S **************/
	
	NS.PanelPorAsignar = new Ext.form.FieldSet({
         title: 'Por Asignar',
         x: 0,
	     y: 0,
         width: 360,
         height: 288,
         layout: 'absolute',
         id: PF+'PanelPorAsignar',
         items:[
                NS.gridBotonesPorAsignar
         ]
	});
	
	NS.PanelAsignados = new Ext.form.FieldSet({
        title: 'Asignados',
        x: 600,
	    y: 0,
        width: 360,
        height: 288,
        layout: 'absolute',
        id: PF+'PanelAsignados',
        items:[
               NS.gridBotonesAsignados
        ]
	});
	
	
	
	NS.PanelContenedorBotones = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorBotones',
		layout: 'absolute',
		width: 1000,
		height: 400,
		x: 0,
		y: 110,
		items:[
		       NS.PanelPorAsignar,
		       NS.PanelAsignados,
		       {
		    	   xtype: 'button',
					text: '>',
					x: 440,
					y: 80,
					width: 30,
					listeners: {
						click:{
							fn:function(e){
								var tamano = NS.gridBotonesPorAsignar.getSelectionModel().getSelections(); 
								if(tamano.length > 0){
									
									
									
									SegComponentesAction.asignarBoton(tamano[0].get('claveComponente'),
					        				NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario'), function(resultado, e) {
					        				
					        				if(resultado != '' && resultado != null){
					        					//Ext.Msg.alert('SET', resultado);
					        					
					       					 	NS.storeBotonesPorAsignar.reload();
					       					 	NS.storeBotonesAsignados.reload();
					        					
					        					
					        				}else{
					        					Ext.Msg.alert('SET', 'Ocurrio un error al asignar');
					        				}

									});
									
									
								}else{
									Ext.Msg.alert('SET', 'No hay datos seleccionados');
								}
							}
						}
					}
		    	   
		       },
		       {
		    	   xtype: 'button',
					text: '<',
					x: 440,
					y: 120,
					width: 30,
					listeners: {
						click:{
							fn:function(e){
								var tamano = NS.gridBotonesAsignados.getSelectionModel().getSelections(); 
								if(tamano.length > 0){
									SegComponentesAction.desAsignarBoton(tamano[0].get('claveComponente'),NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario')+'',function(resultado, e) {
					        				
										NS.storeBotonesPorAsignar.reload();
			       					 	NS.storeBotonesAsignados.reload();
					        			
					        		});
								}else{
									Ext.Msg.alert('SET', 'No hay datos seleccionados');
								}
							}
						}
					}
		    	   
		       },
		       {
		    	   xtype: 'button',
					text: '<<',
					x: 440,
					y: 160,
					width: 30,
					listeners: {
						click:{
							fn:function(e){
								var tamano = NS.storeBotonesAsignados.data.items; 
								if(tamano.length > 0){
									SegComponentesAction.desAsignarTodos(NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario'),
											NS.cmbModulo.getValue(), function(resultado, e) {
										
			        					 
										NS.storeBotonesPorAsignar.reload();
			       					 	NS.storeBotonesAsignados.reload();
					        			
					        		});
								}else{
									Ext.Msg.alert('SET', 'No hay datos disponibles');
								}
							}
						}
					}
		    	   
		       },
		       {
		    	   xtype: 'button',
					text: '>>',
					x: 440,
					y: 200,
					width: 30,
					listeners: {
						click:{
							fn:function(e){
								var tamano = NS.storeBotonesPorAsignar.data.items; 
								if(tamano.length > 0){
								 	var matrizDatos = new Array();
									for(var i = 0; i < tamano.length; i++){
										vecDatos = {};
										vecDatos.idBoton = tamano[i].get('claveComponente');
										vecDatos.idUsuario = NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
										matrizDatos[i] = vecDatos; 
									}
									var jSonString = Ext.util.JSON.encode(matrizDatos);
									
									SegComponentesAction.asignarTodos(jSonString, function(resultado, e) {
										NS.storeBotonesPorAsignar.reload();
			       					 	NS.storeBotonesAsignados.reload();
					        		});
									
								}else{
									Ext.Msg.alert('SET', 'No hay datos disponibles');
								}
							}
						}
					}
		    	   
		       }
		       
		      ]
	});
	
	
	NS.PanelBusqueda = new Ext.form.FieldSet({
        title: 'Busqueda',
        width: 990,
        height: 80,
        x:0,
        y:0,
    	autoScroll: true,
        renderTo: NS.tabContId,
        layout: 'absolute',
        items: [
           {      
            	xtype: 'uppertextfield',
                x: 75,
                y: 10,
                width: 100,
                id: PF+'idUsuario',
				listeners: {
					change:{
						//funcion para asignar uno o varios componentes dependiendo de la seleccion
						fn:function(e) 
						{
							var index = BFwrk.Util.existInCombo(PF+'cmbUsuario', 'claveUsuario', Ext.getCmp(PF+'idUsuario').getValue());
							if (index == -1) {
								Ext.Msg.alert('Información SET', 'El usuario '+Ext.getCmp(PF+'idUsuario').getValue()+' no existe.');
							} else {
								// ASINGAR TANTO EL VALOR COMO LA DESC PARA QUE SE "POSICIONE" EL INDICE DEL COMBO EN EL LUGAR CORECTO
								Ext.getCmp(PF+'cmbUsuario').setValue(Ext.getCmp(PF+'idUsuario').getValue());
								Ext.getCmp(PF+'cmbUsuario').setRawValue(NS.storeUsuarios.getById(Ext.getCmp(PF+'idUsuario').getValue()).get('nombre'));										
							}
							
						}
					}
				}
            },        
            NS.cmbUsuario,
            {
                xtype: 'label',
                text: 'Clave Usuario:',
                x: 0,
                y: 10,
                width: 120
            },
            {
                xtype: 'label',
                text: 'Usuario:',
                x: 190,
                y: 10
            },
            {
                xtype: 'label',
                text: 'Modulo:',
                x: 450,
                y: 10
            },
            NS.cmbModulo,
            {
            	xtype: 'button',
				text: 'Buscar',
				x: 715,
				y: 10,
				width: 60,
	        	 listeners:{
	        		 click:{
	        			 fn:function(e){
	        				 
	        				 if(NS.cmbModulo.getValue() != '' && NS.cmbUsuario.getValue() != ''){
	        					 NS.storeBotonesPorAsignar.baseParams.idUsuario = NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
	        					 NS.storeBotonesPorAsignar.baseParams.idModulo = NS.cmbModulo.getValue();
	        					 NS.storeBotonesPorAsignar.load();
	        					 NS.storeBotonesAsignados.baseParams.idUsuario = NS.storeUsuarios.getById(Ext.getCmp(PF+'cmbUsuario').getValue()).get('idUsuario');
	        					 NS.storeBotonesAsignados.baseParams.idModulo = NS.cmbModulo.getValue();
	        					 NS.storeBotonesAsignados.load();
	        				 }else{
	        					 Ext.Msg.alert('SET', 'Debe seleccionar un modulo y un usuario');
	        				 }
	        				
	        			 }
	        		 }
	        	 }
            }
                
                
        ]
	})
	
	
	
	
	NS.PanelContenedorGeneral = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorGeneral',
		layout: 'absolute',
		width: 1030,
		height: 560,
		x: 0,
		y: 0,
		items: [
		        NS.PanelBusqueda,
		        NS.PanelContenedorBotones
		        ]
	});
	
	
	NS.AsignarBotones = new Ext.form.FormPanel({
		title: 'ASIGNAR BOTONES',
		width: 1010,
		height: 800,
		frame: true,	
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'AsignarBotones',
		name: PF + 'AsignarBotones',
		renderTo: NS.tabContId,
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items: [
		        	NS.PanelContenedorGeneral
		        ]
	});
	
	
	NS.AsignarBotones.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	

	
	
});