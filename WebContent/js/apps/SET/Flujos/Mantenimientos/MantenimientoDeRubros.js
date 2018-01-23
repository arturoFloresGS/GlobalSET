Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.Flujos.Mantenimientos.MantenimientoDeRubros');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.codigo = "I";
	
	NS.limpiar = function(){
		Ext.getCmp(PF + 'txtIdRubro').setValue("");
		Ext.getCmp(PF + 'txtRubro').setValue("");
		Ext.getCmp(PF + 'optSel').reset();
		Ext.getCmp(PF + 'fielsetAltaRubro').setDisabled(true);
	};
	
	NS.recargar = function(){
		NS.gridGrupoRubro.store.removeAll();
		NS.gridGrupoRubro.getView().refresh();
		NS.storeConsultarRubro.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtNoGrupo').getValue());
		NS.storeConsultarRubro.load();
	};

	NS.txtNoGrupo = new Ext.form.TextField({
		x: 473,
        y: 25,
        width: 80,
        name: PF + 'txtNoGrupo',
        id: PF + 'txtNoGrupo',
	    tabIndex: 0,
	    value: apps.SET.id_grupo,
	    listeners:{
	    	change:{
	    		fn: function(caja, valor){
	    			var idNG = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoGrupo',NS.cmbGrupo.getId());
	    			if(idNG == null) {
	    				Ext.getCmp(PF + 'txtNoGrupo').reset();
	    				NS.cmbGrupo.reset();
	    				Ext.getCmp(PF + 'btnBuscar').setDisabled(true);
	    			}else{Ext.getCmp(PF + 'btnBuscar').setDisabled(false);}
	    		}
	    	}
	    }
	});
	
	NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: MantenimientoDeRubrosAction.llenarComboGrupo, 
		idProperty: 'id', 
		fields: [
			{name: 'idGrupo'},
			{name: 'desripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupo, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen Grupos');
				}
			}
		}
	}); 
	
	NS.storeGrupo.load();
	
	//combo cmbGrupo
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo,
		name: PF + 'cmbGrupo',
		id: PF + 'cmbGrupo',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 563,
        y: 25,
        width: 370,
		valueField:'idGrupo',
		displayField:'desripcion',
		autocomplete: true,
		emptyText: 'Seleccione Grupo',
		triggerAction: 'all',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function() {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNoGrupo', NS.cmbGrupo.getId());   
				 	Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
				}
			}
		}
	});
	
	//Radio Button Seleccion por:
	NS.optRadios = new Ext.form.RadioGroup({
		id: PF + 'optSel',
		name: PF + 'optSel',
		x: 700,
	    y: 25,
		columns: 2, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Ingreso', name: 'optSel', inputValue: 1, checked:true,
	        	  listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true){		 					
		 						NS.codigo = "I";	
		 					}	 					
		 				}
		 			}
		 		}
	          },  
	          {boxLabel: 'Egreso', name: 'optSel', inputValue: 2,
	        	  listeners: {
		 			check: {
		 				fn: function(opt, valor) {
		 					if (valor == true){		 					
		 						NS.codigo = "E";	
		 					}	 					
		 				}
		 			}
		 		}
	          }
	          ]
	});
	
	//Objeto para agregar los checks al grid
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true					
	});
	
	//store para el grid principal de grupo rubro
	NS.storeConsultarRubro = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	root: '',
  	 	baseParams:{
			idGrupo: 'idGrupo'
		},
		paramOrder:['idGrupo'],
		idProperty: 'idG',
		directFn: MantenimientoDeRubrosAction.consultarRubro,
		fields: [
			{name: 'idGrupo'},
			{name: 'idRubro'},
			{name: 'descRubro'},
			{name: 'ingresoEgreso'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeConsultarRubro, msg:"Buscando..."});
				if(records.length==null || records.length<=0)
				{
				 Ext.Msg.alert('Información  SET','No existen registros');
				}
		   }  
		}
	}); 
	
	
	//Grid Grupo Rubro
	NS.gridGrupoRubro = new Ext.grid.GridPanel({
		 store: NS.storeConsultarRubro,
	        cm: new Ext.grid.ColumnModel({
	            defaults: {
	        	    width: 200,
	                value:true,
	                sortable: true
	            },
	            columns: [
	               NS.sm,
	                {
						header : 'Grupo',
						width : 130,
						sortable : true,
						dataIndex : 'idGrupo',
						hidden: false
					},
					{
						header : 'Rubro',
						width : 130,
						sortable : true,
						dataIndex : 'idRubro',
						hidden: false
					},
					{
						header : 'Descripción Rubro',
						width : 365,
						sortable : true,
						dataIndex : 'descRubro',
						hidden: false
					},
					{
						header : 'Ingreso/Egreso',
						width : 345,
						sortable : true,
						dataIndex : 'ingresoEgreso',
						hidden: false
					}
	            ]
	        }),
	        sm: NS.sm,
	        columnLines: true,
	        x:0,
	        y:0,
	        width:1005,
	        height:160,
	        frame:true,
	        listeners:{
	        	click:{
	        		fn:function(e){
	          			var regSelec = NS.gridGrupoRubro.getSelectionModel().getSelections();
	          		 	if(regSelec.length > 0)
	          		 	{
	          		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(false);
	          		 	}
	          		 	else
	          		 	{
	          		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
	          		 	}
	        		}
	        	}
	        }
	    });
	
	
	//------------------------------------------------------------------------
	NS.MantenimientoDeRubros = new Ext.form.FormPanel({
		title: 'Mantenimiento de Rubros',
		width: 1100,
		height: 600,
		padding: 10,
		layout: 'absolute',
        id: PF + 'MantenimientoDeRubros',
	    name: PF + 'MantenimientoRubros',
	    renderTo: NS.tabContId,
	    frame: true,
	    autoScroll: true,
			items : [
				{
				    xtype: 'fieldset',
				    title: '',
				    width: 1045,
				    height: 495,
				    x: 10,
				    y: 5,
				    layout: 'absolute',
				    id: 'fielsetPrinManRubro',
				    items: [
							{
							    xtype: 'label',
							    text: 'No Grupo:',
							    x: 473,
							    y: 5
							},
				            NS.txtNoGrupo,
				            {
							    xtype: 'label',
							    text: 'Descripción:',
							    x: 563,
							    y: 5
							},
				            NS.cmbGrupo,
				            {
		                        xtype: 'button',
		                        text: 'Buscar',
		                        x: 943,
		                        y: 25,
		                        width: 80,
		                        height: 22,
		                        id: PF + 'btnBuscar',
		                        name: PF + 'btnBuscar',
		                        disabled: true,
		                        listeners: {
		                        	click:{
				                       fn: function(e){
								  			NS.recargar();
										    Ext.getCmp(PF + 'btnNuevo').setDisabled(false);
				            		   }
		                        	}
		                       }
		                    },
		                    {
		    				    xtype: 'fieldset',
		    				    title: 'Rubros',
		    	                height: 230,
		    				    x: 0,
		    				    y: 60,
		    				    layout: 'absolute',
		    				    name: PF + 'fielsetGridGrupoRubro',
		    				    id: PF + 'fielsetGridGrupoRubro',
		    				    items: [
		    				            NS.gridGrupoRubro,
									{
									    xtype: 'button',
									    text: 'Crear Nuevo',
									    x: 828,
									    y: 170,
									    width: 80,
									    height: 22,
									    id: PF + 'btnNuevo',
									    name: PF + 'btnNuevo',
									    disabled: true,
									    listeners: {
				                        	click:{
						                       fn: function(e){
		    				            	   	Ext.getCmp(PF + 'fielsetAltaRubro').setDisabled(false);
						            		   }
				                        	}
				                       }
									},
									{
									    xtype: 'button',
									    text: 'Eliminar',
									    x: 920,
									    y: 170,
									    width: 80,
									    height: 22,
									    id: PF + 'btnEliminar',
									    name: PF + 'btnEliminar',
									    disabled: true,
									    listeners:{
											click:{
												fn:function(e){
													var regSelec = NS.gridGrupoRubro.getSelectionModel().getSelections();
					                        		if(regSelec.length == 0){
					                        			Ext.Msg.alert('SET','Debe elegir un registro');
					                        			return;
					                        		} 
			                            			Ext.Msg.confirm('SET','¿Esta seguro de eliminar este registro?', function(btn) {  
			        		           		     		if(btn == 'yes') {
			        		           		     			MantenimientoDeRubrosAction.eliminarRubro(regSelec[0].get('idGrupo'),regSelec[0].get('idRubro'), function(res, e){
								                       			if(res !== null && res !== undefined && res !== '')
								                       			{
								                       				Ext.Msg.alert("Información SET", '' + res.msgUsuario);
								                       				NS.recargar();
								                       				Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
								                       			}
				        		           		     		});
			        		           		     		}
			                            			});
												}
											}
										}
									}
		    				           ]
		    				},
		    				{
		    				    xtype: 'fieldset',
		    				    title: 'Alta de Rubro',
		    	                height: 135,
		    				    x: 0,
		    				    y: 300,
		    				    layout: 'absolute',
		    				    name: PF + 'fielsetAltaRubro',
		    				    id: PF + 'fielsetAltaRubro',
		    				    disabled: true,
		    				    items: [
									{
										xtype: 'numberfield',
									    x: 50,
									    y: 25,
									    width: 60,
									    name: PF + 'txtIdRubro',
									    id: PF + 'txtIdRubro'
									},
									{
										xtype: 'textfield',
									    x: 130,
									    y: 25,
									    width: 350,
									    name: PF + 'txtRubro',
									    id: PF + 'txtRubro'
									},
									{
									    xtype: 'label',
									    text: 'Rubro Contrario:',
									    x: 515,
									    y: 25
									},
									{
										xtype: 'textfield',
									    x: 610,
									    y: 25,
									    width: 60,
									    name: PF + 'txtRubrContrario',
									    id: PF + 'txtRubrContrario',
									    disabled:true
									},
									NS.optRadios,
									{
									    xtype: 'button',
									    text: 'Cancelar',
									    x: 828,
									    y: 75,
									    width: 80,
									    height: 22,
									    id: PF + 'btnCancelar',
									    name: PF + 'btnCancelar',
									    listeners:{
											click:{
												fn:function(e){
													NS.limpiar();
												}
										
											}
										}
									},
									{
									    xtype: 'button',
									    text: 'Aceptar',
									    x: 920,
									    y: 75,
									    width: 80,
									    height: 22,
									    id: PF + 'btnAceptar',
									    name: PF + 'btnAceptar',
									    listeners:{
					                        click:{
							            	fn:function(e){
												var matrizRubro= new Array();
					                			var arrRubro= {};
					                			arrRubro.idGrupo=Ext.getCmp(PF + 'txtNoGrupo').getValue();
					                			arrRubro.idRubro=Ext.getCmp(PF + 'txtIdRubro').getValue();
					                			arrRubro.rubro=Ext.getCmp(PF + 'txtRubro').getValue();
					                			arrRubro.ingesoEgreso=NS.codigo;
					                			matrizRubro[0]=arrRubro;
					                			var jsonStringRubro = Ext.util.JSON.encode(matrizRubro);
					                			MantenimientoDeRubrosAction.accionRubro(jsonStringRubro,function(res, e) {
					    							if(res != null && res != undefined && res != '') {
					    								if(res.msgUsuario == 'Datos Registrados'){
					    									Ext.Msg.alert("Información SET", '' + res.msgUsuario);
						    								NS.limpiar();
						    								NS.recargar();
					    								}
					    								Ext.Msg.alert("Información SET", '' + res.msgUsuario);
					    							}
				    							});
							             	}
					                        }
						               }
									}
		    				           ]
		    				},
		    				{
		                        xtype: 'button',
		                        text: 'Imprimir',
		                        x: 850,
		                        y: 450,
		                        width: 80,
		                        height: 22,
		                        id: PF + 'btnImprimir',
		                        name: PF + 'btnImprimir',
		    				    hidden:true
		                    },
		                    {
		                        xtype: 'button',
		                        text: 'Limpiar',
		                        x: 943,
		                        y: 450,
		                        width: 80,
		                        height: 22,
		                        id: PF + 'btnLimpiar',
		                        name: PF + 'btnLimpiar',
		                        listeners:{
		                    		click:{
		                    			fn:function(){
					                    	Ext.getCmp(PF + 'txtNoGrupo').setValue('');
					                    	Ext.getCmp(PF + 'cmbGrupo').reset();
					                    	Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
					                    	Ext.getCmp(PF + 'btnNuevo').setDisabled(true);
					                    	Ext.getCmp(PF + 'btnBuscar').setDisabled(true);
					                    	NS.limpiar();
					                    	NS.gridGrupoRubro.store.removeAll();
		                    			}
		                    		}
		                    	}
		                    }
				           ]
				}
		    ]
	});
	NS.MantenimientoDeRubros.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});