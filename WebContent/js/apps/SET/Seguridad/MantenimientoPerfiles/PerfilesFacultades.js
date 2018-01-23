Ext.onReady(function(){
	//@autor: Sergio Vaca
 	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoPerfiles.PerfilesFacultades');
    // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
    NS.tabContId = apps.SET.tabContainerId;
    var PF = apps.SET.tabID + '.';
    // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
 	 //Store para llenar el combo de busqueda cmbFacultades
  	NS.storePerfilFacultad = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			pfId: 0
		},
		root: '',
		paramOrder:['pfId'],
		directFn: SegPerfilFacultadAction.llenarComboPerfiles,
		idProperty: 'idPerfil',
		fields: [
			{name: 'idPerfil' },
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
	NS.storePerfilFacultad.load();
	
	//combo busqueda clave 
	NS.cmbPerfilFacultad = new Ext.form.ComboBox({
		 store: NS.storePerfilFacultad
		,name: PF+'cmbPerfilFacultad'
		,id: PF+'cmbPerfilFacultad'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :160
		,y :10
		,width :100
		,valueField: 'idPerfil'
		,displayField:'descripcionPerfil'
		,autocomplete: true
		,emptyText: 'Seleccione'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					Ext.getCmp(PF+'cmbDescPerfiles').setValue(combo.getValue());
					var idFacultadV = NS.storePerfilFacultad.getById(combo.getValue()).get('idPerfil');
					NS.storeFacultadesP.baseParams.pfIdFacultadP = idFacultadV;
					NS.storeFacultadesP.baseParams.pfBanderaP = false;
					NS.storeFacultadesP.load();
					
					NS.storeFacultadesA.baseParams.pfIdFacultadA = idFacultadV;
					NS.storeFacultadesA.baseParams.pfBanderaA = true;
					NS.storeFacultadesA.load();
					
				}
			}
		}
	});
	
	//combo de busqueda descripcion
	NS.cmbDescPerfiles = new Ext.form.ComboBox({
		 store: NS.storePerfilFacultad
		,name: PF+'cmbDescPerfiles'
		,id: PF+'cmbDescPerfiles'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :400
		,y :10
		,width :240
		,valueField: 'idPerfil'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un Perfil'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					Ext.getCmp(PF+'cmbPerfilFacultad').setValue(combo.getValue());
					var idFacultadV = NS.storePerfilFacultad.getById(combo.getValue()).get('idPerfil');
					NS.storeFacultadesP.baseParams.pfIdFacultadP = idFacultadV;
					NS.storeFacultadesP.baseParams.pfBanderaP = false;
					NS.storeFacultadesP.load();
					
					NS.storeFacultadesA.baseParams.pfIdFacultadA = idFacultadV;
					NS.storeFacultadesA.baseParams.pfBanderaA = true;
					NS.storeFacultadesA.load();
					
				}
			}
		}
	});
	
	//Store para el grid de componentes por asignar 'gridFacultadesP'	 
	NS.storeFacultadesP = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			pfIdFacultadP: 0,
			pfBanderaP: false 
		},
		root: '',
		paramOrder:['pfIdFacultadP','pfBanderaP'],
		directFn: SegPerfilFacultadAction.seleccionarFacultades,
		idProperty: 'idFacultad',
		fields: [
			{name: 'idFacultad' },
			{name: 'idPerfil' },
			{name: 'claveFacultad' },
			{name: 'idPerfil' },
			{name: 'descripcion' }
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFacultadesP, msg:"Cargando..."});
			}
		}
	}); 
	
	//Store para el grid de componentes asignados 'gridFacultadesA'
	NS.storeFacultadesA = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			pfIdFacultadA: 0,
			pfBanderaA: true 
		},
		root: '',
		paramOrder:['pfIdFacultadA','pfBanderaA'],
		directFn: SegPerfilFacultadAction.seleccionarFacultades,
		idProperty: 'idFacultad',
		fields: [
			{name: 'idFacultad' },
			{name: 'idPerfil' },
			{name: 'descripcion' }
		],
		restful: true,
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFacultadesA, msg:"Cargando..."});
			}
		}
	}); 
	//Grid de componentes por asignar
	NS.gridFacultadesP = new Ext.grid.GridPanel( {
		ddGroup: PF+'gridFacultadesADDGroup',
		store : NS.storeFacultadesP,
		columns : [ 
			{
				id :'idFacultad',
				header :'Id Facultad',
				width :80,
				sortable :true,
				dataIndex :'idFacultad'
			},{
				id :'claveFacultad',
				header :'Clave Facultad',
				width :100,
				sortable :true,
				dataIndex :'claveFacultad'
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
		enableDragDrop: true,
       	stripeRows: true
	});
	
	//Grid de componentes Asignados
	NS.gridFacultadesA = new Ext.grid.GridPanel( {
		ddGroup: PF+'gridFacultadesPDDGroup',
		store : NS.storeFacultadesA,
		columns : [
			{
				id :'idFacultad',
				header :'Id Facultad',
				width :80,
				sortable :true,
				dataIndex :'idFacultad'
			},{
				id :'claveFacultad',
				header :'Clave Facultad',
				width :100,
				sortable :true,
				dataIndex :'claveFacultad'
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
	NS.AsignarFacultades = new Ext.FormPanel({
	    title: 'Asignar Facultades',
	    renderTo: NS.tabContId,
	    width: 782,
    	autoScroll: true,
        height: 415,
	    padding: 10,
	    layout: 'absolute',
	    //html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
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
	                        text: 'Clave Perfil:',
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
	                    NS.cmbPerfilFacultad,
	                    NS.cmbDescPerfiles
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
	                        items: [NS.gridFacultadesA]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Por Asignar',
	                        x: 0,
	                        y: 0,
	                        layout: 'absolute',
	                        width: 340,
	                        height: 267,
	                        items: [NS.gridFacultadesP]
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
										var fcClaveCombo = Ext.getCmp(PF+'cmbDescPerfiles').getValue();
										if(fcClaveCombo=='' || fcClaveCombo==null)
											Ext.Msg.alert('SET','Seleccione una Facultad');
										else{
											var idPerfilVBA = NS.storePerfilFacultad.getById(Ext.getCmp(PF+'cmbDescPerfiles').getValue()).get('idPerfil');
											var records = NS.gridFacultadesP.getSelectionModel().getSelections();
											for(var i=0; i<records.length; i=i+1){
												var idFacultadVBA = records[i].get('idFacultad');
												var dato = ''+idPerfilVBA+','+idFacultadVBA;
												SegPerfilFacultadAction.asignarUna(dato, function(result, e){
													if(!result.resultado){
														Ext.Msg.alert('SET','Error en la BD');
													}
													else{
														for(var h=0; h<records.length;h=h+1){
															if(records[h].get('idFacultad')==result.idA){
																NS.gridFacultadesA.store.add(records[h]);
																NS.gridFacultadesP.store.remove(records[h]);
																NS.gridFacultadesA.store.sort('idFacultad', 'ASC');
															}
														}
													}
												});
											}
						                    NS.gridFacultadesP.getView().refresh();
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
										var fcClaveCombo =Ext.getCmp(PF+'cmbDescPerfiles').getValue();
										if(fcClaveCombo=='' || fcClaveCombo==null)
											Ext.Msg.alert('SET','Seleccione una Facultad');
										else{
											var idPerfilVBP = NS.storePerfilFacultad.getById(Ext.getCmp(PF+'cmbDescPerfiles').getValue()).get('idPerfil');
											var records = NS.gridFacultadesA.getSelectionModel().getSelections();
											for(var i=0; i<records.length; i=i+1){
												var idFacultadVBP = records[i].get('idFacultad');
												var dato = ''+idPerfilVBP+','+idFacultadVBP;
												SegPerfilFacultadAction.eliminarUna(dato, function(result, e){
													if(!result.resultado){
															Ext.Msg.alert('SET','Error en la BD');
													}
													else{
														for(var h=0; h<records.length;h=h+1){
															if(records[h].get('idFacultad')==result.idA){
																NS.gridFacultadesP.store.add(records[h]);
																NS.gridFacultadesA.store.remove(records[h]);
																NS.gridFacultadesP.store.sort('idFacultad', 'ASC');
															}
														}
													}
												});
											}
						                    NS.gridFacultadesA.getView().refresh();
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
										var fcClaveCombo =Ext.getCmp(PF+'cmbDescPerfiles').getValue();
										if(fcClaveCombo=='' || fcClaveCombo==null)
											Ext.Msg.alert('SET','Seleccione una Facultad');
										else{
											var idPerfilVBA = NS.storePerfilFacultad.getById(Ext.getCmp(PF+'cmbDescPerfiles').getValue()).get('idPerfil');
											SegPerfilFacultadAction.asignarTodas(idPerfilVBA, function(result, e){
												if(!result){
														Ext.Msg.alert('SET','Error en la BD');
												}
												else{
													var records = NS.storeFacultadesP.data.items;
			                        				NS.gridFacultadesA.store.add(records);
			                        				NS.gridFacultadesP.store.removeAll();
								                    NS.gridFacultadesP.getView().refresh();
								                    NS.gridFacultadesA.store.sort('idFacultad', 'ASC');
												}
											});
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
										var fcClaveCombo =Ext.getCmp(PF+'cmbDescPerfiles').getValue();
										if(fcClaveCombo=='' || fcClaveCombo==null)
											Ext.Msg.alert('SET','Seleccione una Facultad');
										else{
											var idPerfilVBP = NS.storePerfilFacultad.getById(Ext.getCmp(PF+'cmbDescPerfiles').getValue()).get('idPerfil');
											
											SegPerfilFacultadAction.eliminarTodas(idPerfilVBP, function(result, e){
												if(!result){
													Ext.Msg.alert('SET','Error en la BD');
												}
												else{
													var records = NS.storeFacultadesA.data.items;
			                        				NS.gridFacultadesP.store.add(records);
			                        				NS.gridFacultadesA.store.removeAll();
								                    NS.gridFacultadesA.getView().refresh();
								                    NS.gridFacultadesP.store.sort('idFacultad', 'ASC');
												}
											});
											var records = NS.storeFacultadesA.data.items;
	                        				NS.gridFacultadesP.store.add(records);
	                        				NS.gridFacultadesA.store.removeAll();
						                    NS.gridFacultadesA.getView().refresh();
						                    NS.gridFacultadesP.store.sort('idFacultad', 'ASC');
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
	NS.gridFacultadesPTargetEl =  NS.gridFacultadesP.getView().scroller.dom;
       NS.gridFacultadesPTarget = new Ext.dd.DropTarget(NS.gridFacultadesPTargetEl, {
               ddGroup    : PF+'gridFacultadesPDDGroup',
               notifyDrop : function(ddSource, e, data){
                       	var records =  ddSource.dragData.selections;
                       	var idPerfilVBP = NS.storePerfilFacultad.getById(Ext.getCmp(PF+'cmbDescPerfiles').getValue()).get('idPerfil');
					  	for(var i=0; i<records.length; i=i+1){
							var idFacultadVBP = records[i].get('idFacultad');
							var dato = ''+idPerfilVBP+','+idFacultadVBP;
							SegPerfilFacultadAction.eliminarUna(dato, function(result, e){
								if(!result.resultado){
									Ext.Msg.alert('SET','Error en la BD');
								}
								else{
									Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
			                       	NS.gridFacultadesP.store.add(records);
			                       	NS.gridFacultadesA.getView().refresh();                       
	                   				NS.gridFacultadesP.store.sort('idFacultad', 'ASC');								
								}
							});
						}
	                    return true
               }
       });


       //Funcion del drag and drop para asignar los componentes
       NS.gridFacultadesATargetEl = NS.gridFacultadesA.getView().scroller.dom;
       	NS.gridFacultadesATarget = new Ext.dd.DropTarget(NS.gridFacultadesATargetEl, {
               ddGroup    : PF+'gridFacultadesADDGroup',
               notifyDrop : function(ddSource, e, data){
                       	var records =  ddSource.dragData.selections;
                       	var idPerfilVBA = NS.storePerfilFacultad.getById(Ext.getCmp(PF+'cmbDescPerfiles').getValue()).get('idPerfil');
					  	for(var i=0; i<records.length; i=i+1){
							var idFacultadVBA = records[i].get('idFacultad');
							var dato = ''+idPerfilVBA+','+idFacultadVBA;
							SegPerfilFacultadAction.asignarUna(dato, function(result, e){
								if(!result.resultado){
									Ext.Msg.alert('SET','Error en la BD');
								}
								else{
									Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
                       				NS.gridFacultadesA.store.add(records);
                       				NS.gridFacultadesP.getView().refresh();                       
	                   				NS.gridFacultadesA.store.sort('idFacultad', 'ASC');								
								}
							});
						}
	                   	return true
               }
       });

	NS.AsignarFacultades.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());

});
	
	


 

