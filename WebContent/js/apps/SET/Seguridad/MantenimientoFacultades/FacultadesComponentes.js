Ext.onReady(function(){
	//@autor: Sergio Vaca
 	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoFacultades.FacultadesComponentes');
    // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
    NS.tabContId = apps.SET.tabContainerId;
    var PF = apps.SET.tabID + '.';
    // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
 	 //Store para llenar el combo de busqueda cmbFacultades
  	NS.storeFacultad = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {
			fcId: 0
		},
		root: '',
		paramOrder:['fcId'],
		directFn: SegFacultadComponenteAction.llenarComboFacultades,
		idProperty: 'claveFacultad',
		fields: [
			{name: 'claveFacultad'},
			{name: 'idFacultad' },
			{name: 'descripcion'},
			{name: 'estatus'}
		],
		listeners: {
			load: function(s, records){
				//alert( records.length + " registros cargados.");
			}
		}
	}); 
	// load data
	NS.storeFacultad.load();
	
	//combo busqueda clave 
	NS.cmbCveFacultades = new Ext.form.ComboBox({
		 store: NS.storeFacultad
		,name: PF+'cmbCveFacultades'
		,id: PF+'cmbCveFacultades'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :180
		,y :10
		,width :100
		,valueField: 'claveFacultad'
		,displayField:'claveFacultad'
		,autocomplete: true
		,emptyText: 'Seleccione'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					
					Ext.getCmp(PF+'cmbDescFacultades').setValue(combo.getValue());
					var idFacultadV = NS.storeFacultad.getById(combo.getValue()).get('idFacultad');
					NS.storeComponentesP.baseParams.fcIdFacultadP = idFacultadV;
					NS.storeComponentesP.baseParams.fcBanderaP = false;
					NS.storeComponentesP.load();
					
					NS.storeComponentesA.baseParams.fcIdFacultadA = idFacultadV;
					NS.storeComponentesA.baseParams.fcBanderaA = true;
					NS.storeComponentesA.load();
					
				}
			}
		}
	});
	
	//combo de busqueda descripcion
	var cmbDescFacultades = new Ext.form.ComboBox({
		 store: NS.storeFacultad
		,name: PF+'cmbDescFacultades'
		,id: PF+'cmbDescFacultades'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :400
		,y :10
		,width :240
		,valueField: 'claveFacultad'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una Facultad'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					
					Ext.getCmp(PF+'cmbCveFacultades').setValue(combo.getValue());
					var idFacultadV = NS.storeFacultad.getById(combo.getValue()).get('idFacultad');
					NS.storeComponentesP.baseParams.fcIdFacultadP = idFacultadV;
					NS.storeComponentesP.baseParams.fcBanderaP = false;
					NS.storeComponentesP.load();
					
					NS.storeComponentesA.baseParams.fcIdFacultadA = idFacultadV;
					NS.storeComponentesA.baseParams.fcBanderaA = true;
					NS.storeComponentesA.load();
					
				}
			}
		}
	}); 
  	
  	//Store para el grid de componentes por asignar 'gridComponentesP'	 
	NS.storeComponentesP = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			fcIdFacultadP: 0,
			fcBanderaP: false 
		},
		root: '',
		paramOrder:['fcIdFacultadP','fcBanderaP'],
		directFn: SegFacultadComponenteAction.seleccionarComponentes,
		idProperty: 'idComponente',
		fields: [
			{name: 'idFacultad' },
			{name: 'idComponente' },
			{name: 'clavefacultad' },
			{name: 'claveComponente' },
			{name: 'descripcion' }
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComponentesP, msg:"Cargando..."});
			}
		}
	}); 
	
	//Store para el grid de componentes asignados 'gridComponentesA'
	NS.storeComponentesA = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			fcIdFacultadA: 0,
			fcBanderaA: true 
		},
		root: '',
		paramOrder:['fcIdFacultadA','fcBanderaA'],
		directFn: SegFacultadComponenteAction.seleccionarComponentes,
		idProperty: 'idComponente',
		fields: [
			{name: 'idFacultad' },
			{name: 'idComponente' },
			{name: 'clavefacultad' },
			{name: 'claveComponente' },
			{name: 'descripcion' }
		],
		restful: true,
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeComponentesA, msg:"Cargando..."});
			}
		}
	}); 
	//Grid de componentes por asignar
	NS.gridComponentesP = new Ext.grid.GridPanel( {
		ddGroup: PF+'gridComponentesADDGroup',
		store : NS.storeComponentesP,
		columns : [
			{
				id :'idComponente',
				header :'Id Componente',
				width :80,
				sortable :true,
				dataIndex :'idComponente'
			},{
				id :'claveComponente',
				header :'Clave Componente',
				width :100,
				sortable :true,
				dataIndex :'claveComponente'
			},{
				header :'Descripcion',
				width :245,
				dataIndex :'descripcion'
			}
		],
		stripeRows :true,
		height: 225,
		width :318,
		x :0,
		y :0,
		title :'',
		enableDragDrop: true,
       	stripeRows: true
	});
	
	//Grid de componentes Asignados
	NS.gridComponentesA = new Ext.grid.GridPanel( {
		ddGroup: PF+'gridComponentesPDDGroup',
		store : NS.storeComponentesA,
		columns : [ 
			{
				id :'idComponente',
				header :'Id Componente',
				width :80,
				sortable :true,
				dataIndex :'idComponente'
			},{
				id :'claveComponente',
				header :'Clave Componente',
				width :100,
				sortable :true,
				dataIndex :'claveComponente'
			},{
				header :'Descripcion',
				width :245,
				dataIndex :'descripcion'
			}
		],
		stripeRows :true,
		height: 225,
		width :320,
		x :0,
		y :0,
		title :'',
		enableDragDrop: true,
       	stripeRows: true
	});
	
	//panel para contener todo
	NS.AsignarComponentes = new Ext.FormPanel({
	    title: 'Asignar Componentes',
	    renderTo: NS.tabContId,
	   	width: 782,
    	frame: true,
        height:415,
    	autoScroll: true,
	    padding: 10,
	    layout: 'absolute',
	    items: [
	            {
	                xtype: 'fieldset',
	                title: 'Búsqueda',
	                x: 10,
           			y: 4,
	            	width: 760, // quitar para maximizar al 100%
	                height: 80,
	                layout: 'absolute',
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Clave Facultad:',
	                        x: 50,
	                        y: 10
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Descripcion: ',
	                        width: 70,
	                        x: 300,
	                        y: 10
	                    },
	                    NS.cmbCveFacultades,
	                    cmbDescFacultades
	                ]
	            },
	            {
	                xtype: 'fieldset',
	                title: '',
	                x: 10,
	                y: 90,
	                padding: 10,
	            	width: 760, // quitar para maximizar al 100%
		            height: 290,
	                layout: 'absolute',
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: 'Asignados',
	                        x: 395,
	                        y: 0,
	                        layout: 'absolute',
	                        width: 343,
	                        height: 267,
	                        items: [NS.gridComponentesA]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Por Asignar',
	                        x: 0,
	                        y: 0,
	                        layout: 'absolute',
	                        width: 340,
	                        height: 267,
	                        items: [NS.gridComponentesP]
	                    },
	                    {
	                        xtype: 'button',
	                        text: '>',
	                        x: 355,
	                        y: 60,
	                        width: 30,
	                        listeners: {
	                        	click:{
	                        		//funcion para asignar uno o varios componentes dependiendo de la seleccion
	                        		fn:function(e) {
										var fcClaveCombo = Ext.getCmp(PF+'cmbDescFacultades').getValue();
										if(fcClaveCombo=='' || fcClaveCombo==null)
											Ext.Msg.alert('SET','Seleccione una Facultad');
										else{
											var idFacultadVBA = NS.storeFacultad.getById(Ext.getCmp(PF+'cmbDescFacultades').getValue()).get('idFacultad');
											var records = NS.gridComponentesP.getSelectionModel().getSelections();
											for(var i=0; i<records.length; i=i+1){
												var idComponenteVBA = records[i].get('idComponente');
												var dato = ''+idFacultadVBA+','+idComponenteVBA;
												SegFacultadComponenteAction.asignarUna(dato, function(result, e){
													if(!result.resultado){
														Ext.Msg.alert('SET','Error en la BD');
													}
													else{												
														for(var h=0; h<records.length;h=h+1){
															if(records[h].get('idComponente')==result.idA){
																NS.gridComponentesA.store.add(records[h]);
																NS.gridComponentesP.store.remove(records[h]);
																NS.gridComponentesA.store.sort('idComponente', 'ASC');
															}
														}
													}
												});
											}
						                    NS.gridComponentesP.getView().refresh();
										}
									}
	                        	
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: '&lt;',
	                        x: 355,
	                        y: 100,
	                        width: 30,
	                        listeners: {
	                        	click:{
	                        		//funcion para eliminar uno o varios componentes dependiendo de la seleccion
	                        		fn:function(e) {
										var fcClaveCombo =Ext.getCmp(PF+'cmbDescFacultades').getValue();
										if(fcClaveCombo=='' || fcClaveCombo==null)
											Ext.Msg.alert('SET','Seleccione una Facultad');
										else{
											var idFacultadVBP = NS.storeFacultad.getById(Ext.getCmp(PF+'cmbDescFacultades').getValue()).get('idFacultad');
											var records = NS.gridComponentesA.getSelectionModel().getSelections();
											for(var i=0; i<records.length; i=i+1){
												var idComponenteVBP = records[i].get('idComponente');
												var dato = ''+idFacultadVBP+','+idComponenteVBP;
												SegFacultadComponenteAction.eliminarUna(dato, function(result, e){
													if(!result.resultado){
															Ext.Msg.alert('SET','Error en la BD');
													}
													else{												
														for(var h=0; h<records.length;h=h+1){
															if(records[h].get('idComponente')==result.idA){
																NS.gridComponentesP.store.add(records[h]);
																NS.gridComponentesA.store.remove(records[h]);
																NS.gridComponentesP.store.sort('idComponente', 'ASC');
															}
														}
													}
												});
											}
						                    NS.gridComponentesA.getView().refresh();
										}
									}
	                        	
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: '>>',
	                        x: 355,
	                        y: 140,
	                        width: 30,
	                        listeners:{
								click:{
									fn:function(e) {
										//funcion para asignar todos los componentes
										var fcClaveCombo =Ext.getCmp(PF+'cmbDescFacultades').getValue();
										if(fcClaveCombo=='' || fcClaveCombo==null)
											Ext.Msg.alert('SET','Seleccione una Facultad');
										else{
											var idFacultadVBA = NS.storeFacultad.getById(Ext.getCmp(PF+'cmbDescFacultades').getValue()).get('idFacultad');
											SegFacultadComponenteAction.asignarTodas(idFacultadVBA, function(result, e){
													if(!result){
														Ext.Msg.alert('SET','Error en la BD');
													}
													else{
														var records = NS.storeComponentesP.data.items;
				                        				NS.gridComponentesA.store.add(records);
				                        				NS.gridComponentesP.store.removeAll();
									                    NS.gridComponentesP.getView().refresh();
									                    NS.gridComponentesA.store.sort('idComponente', 'ASC');
													}
												}
											);
										}
									}
								}
							}
	                    },
	                    {
	                        xtype: 'button',
	                        text: '&lt;&lt;',
	                        x: 355,
	                        y: 180,
	                        width: 30,
	                        listeners:{
								click:{
									//funcion para eliminar todos los componentes asignados
									fn:function(e) {
										var fcClaveCombo =Ext.getCmp(PF+'cmbDescFacultades').getValue();
										if(fcClaveCombo=='' || fcClaveCombo==null)
											Ext.Msg.alert('SET','Seleccione una Facultad');
										else{
											var idFacultadVBP = NS.storeFacultad.getById(Ext.getCmp(PF+'cmbDescFacultades').getValue()).get('idFacultad');
											
											SegFacultadComponenteAction.eliminarTodas(idFacultadVBP, function(result, e){
													if(!result){
														Ext.Msg.alert('SET','Error en la BD');
													}
													else{
														var records = NS.storeComponentesA.data.items;
				                        				NS.gridComponentesP.store.add(records);
				                        				NS.gridComponentesA.store.removeAll();
									                    NS.gridComponentesA.getView().refresh();
									                    NS.gridComponentesP.store.sort('idComponente', 'ASC');
													}
												}
											);
										}
									}
								}
							}
	                    }		               
	              ]
	            }
	        ]
	});
	
	//Funcion del drag and drop para eliminar los componentes
	NS.gridComponentesPTargetEl =  NS.gridComponentesP.getView().scroller.dom;
    	NS.gridComponentesPTarget = new Ext.dd.DropTarget(NS.gridComponentesPTargetEl, {
               ddGroup    : PF+'gridComponentesPDDGroup',
               notifyDrop : function(ddSource, e, data){
                       	var records =  ddSource.dragData.selections;
                       	var idFacultadVBP = NS.storeFacultad.getById(Ext.getCmp(PF+'cmbDescFacultades').getValue()).get('idFacultad');
					  	for(var i=0; i<records.length; i=i+1){
							var idComponenteVBP = records[i].get('idComponente');
							var dato = ''+idFacultadVBP+','+idComponenteVBP;
							SegFacultadComponenteAction.eliminarUna(dato, function(result, e){
								if(!result.resultado){
									Ext.Msg.alert('SET','Error en la BD');
								}
								else{
									Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                       				NS.gridComponentesP.store.add(records);
                       				NS.gridComponentesA.getView().refresh();                       
	                   				NS.gridComponentesP.store.sort('idComponente', 'ASC');
								}
							});
						}
	                    return true
               }
       });


       //Funcion del drag and drop para asignar los componentes
       NS.gridComponentesATargetEl = NS.gridComponentesA.getView().scroller.dom;
       	NS.gridComponentesATarget = new Ext.dd.DropTarget(NS.gridComponentesATargetEl, {
               ddGroup    : PF+'gridComponentesADDGroup',
               notifyDrop : function(ddSource, e, data){
                       	var records =  ddSource.dragData.selections;
                       	var idFacultadVBA = NS.storeFacultad.getById(Ext.getCmp(PF+'cmbDescFacultades').getValue()).get('idFacultad');
					  	for(var i=0; i<records.length; i=i+1){
							var idComponenteVBA = records[i].get('idComponente');
							var dato = ''+idFacultadVBA+','+idComponenteVBA;
							SegFacultadComponenteAction.asignarUna(dato, function(result, e){
								if(!result.resultado){
										Ext.Msg.alert('SET','Error en la BD');
								}
								else{
									Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                       				NS.gridComponentesA.store.add(records);
                       				NS.gridComponentesA.getView().refresh();
                      				NS.gridComponentesA.store.sort('idComponente', 'ASC');
								}
							});
						}
                      	return true
               }
       });

	NS.AsignarComponentes.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight()); 

});
	
	


 

