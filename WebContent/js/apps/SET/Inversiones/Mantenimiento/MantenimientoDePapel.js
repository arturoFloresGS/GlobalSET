Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.Inversiones.Mantenimiento.MantenimientoDePapel');
	var PF = apps.SET.tabID + '.';
	var papel='';
	NS.tabContId = apps.SET.tabContainerId;
	NS.Bandera='';
	

	//funcion para limpiar
	NS.limpiar = function(){
		NS.gridConsultarPapel.store.removeAll();
		NS.gridConsultarPapel.getView().refresh();
		NS.storeConsultarPapel.load();
	};

	NS.habilitarDeshabilitar = function(valor){
		Ext.getCmp(PF + 'frameNuevoModificar').setDisabled(valor);
		Ext.getCmp(PF + 'btnAceptar').setDisabled(valor);
		Ext.getCmp(PF + 'btnCancelar').setDisabled(valor);

	};
	
	NS.habilitarDesabilitarBotones = function(valor){
    	Ext.getCmp(PF + 'btnModificar').setDisabled(valor);
    	Ext.getCmp(PF + 'btnEliminar').setDisabled(valor);
	};
	
	NS.habilitarDesabilitarCampos = function(valor){
		Ext.getCmp(PF + 'txtPapel').setValue('');
		Ext.getCmp(PF + 'txtIdTipoValor').setValue('');
		Ext.getCmp(PF + 'cmbTipoValor').reset(valor);
		
		Ext.getCmp(PF + 'txtPapel').setDisabled(valor);
		Ext.getCmp(PF + 'txtIdTipoValor').setDisabled(valor);
		Ext.getCmp(PF + 'cmbTipoValor').setDisabled(valor);
	};
	
	NS.habilitarDesabilitarCampos2 = function(valor){
		Ext.getCmp(PF + 'txtPapel').setValue('');
		Ext.getCmp(PF + 'txtIdTipoValor').setValue('');
		Ext.getCmp(PF + 'cmbTipoValor').reset(valor);

		Ext.getCmp(PF + 'txtIdTipoValor').setDisabled(valor);
		Ext.getCmp(PF + 'cmbTipoValor').setDisabled(valor);
	};
		
	//Objeto para agregar los checks al grid
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true,
		singleSelect: true					
	});
	
	//txt par el combo tipo valor
	NS.txtNoEmpresa = new Ext.form.TextField({
		x: 545,
        y: 25,
        width: 80,
        name: PF + 'txtIdTipoValor',
        id: PF + 'txtIdTipoValor',
	    tabIndex: 0,
	    value: apps.SET.id_tipo_valor,
	    listeners:{
	    	change:{
	    		fn: function(caja, valor){
	    			var idTV = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdTipoValor', NS.cmbTipoValor.getId());
	    			if(idTV == null) {
	    				Ext.getCmp(PF+'txtIdTipoValor').reset();
	    				NS.cmbTipoValor.reset();
	    			}
	    		}
	    	}
	    }
	});
	
	//store para el grid principal del grid de papel
	NS.storeConsultarPapel = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	root: '',
		directFn: MantenimientoDePapelAction.consultarPapel,
		fields: [
			{name: 'idPapel'},
			{name: 'idTipoValor'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeConsultarPapel, msg:"Buscando..."});
				if(records.length==null || records.length<=0)
				{
				 Ext.Msg.alert('Información  SET','No existen registros');
				}
		   }  
		}
	}); 

	//Grid Principal
	NS.gridConsultarPapel = new Ext.grid.GridPanel({
			store: NS.storeConsultarPapel,
	        cm: new Ext.grid.ColumnModel({
	            defaults: {
	        	    width: 200,
	                value:true,
	                sortable: true
	            },
	            columns: [
	                NS.sm,
	                 {
						header :'Papel',
						width :280,
						sortable :true,
						dataIndex :'idPapel',
						hidden: false
					},{
						header :'Tipo Valor',
						width :200,
						sortable :true,
						dataIndex :'idTipoValor',
						hidden: false
					},{
						header :'Descripción',
						width :492,
						sortable :true,
						dataIndex :'descripcion',
						hidden: false
					}
	            ]
	        }),
	        sm: NS.sm,
	        columnLines: true,
	        x:0,
	        y:0,
	        width:1005,
	        height:220,
	        frame:true,
	        listeners:{
	        	click:{
	        		fn:function(e){
	          			var regSelec = NS.gridConsultarPapel.getSelectionModel().getSelections();
	          		 	if(regSelec.length > 0)
	          		 	{
	          		 		Ext.getCmp(PF + 'btnModificar').setDisabled(false);
	          		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(false);
	          		 	}
	          		 	else
	          		 	{
	          		 		Ext.getCmp(PF + 'btnModificar').setDisabled(true);
	          		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
	          		 	}
	        		}
	        	}
	        }
	    });
	
	//store del combo cmbTipoValor
	NS.storeTipoValor = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:[''],
		directFn: MantenimientoDePapelAction.llenarComboTipoValor, 
		idProperty: 'idTipoValor', 
		fields: [
			{name: 'idTipoValor'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTipoValor, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existe Tipo de Valor');
				}
			}
		}
	}); 
	
	NS.storeTipoValor.load();

	//combo cmbTipoValor
	NS.cmbTipoValor = new Ext.form.ComboBox({
		store: NS.storeTipoValor,
		name: PF+'cmbTipoValor',
		id: PF+'cmbTipoValor',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 635,
        y: 25,
        width: 370,
		valueField:'idTipoValor',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione Tipo de Valor',
		triggerAction: 'all',
		//value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtIdTipoValor', NS.cmbTipoValor.getId());
				}
			}
		}
	});
	
	NS.MantenimientoDePapeles = new Ext.form.FormPanel({
    title: 'Mantenimiento de Papel',
    width: 1020,
    height: 600,
    padding: 10,
    layout: 'absolute',
    id: PF + 'MantenimientoDePapel',
    name: PF + 'MantenimientoDePapel',
    renderTo: NS.tabContId,
    frame: true,
    autoScroll: true,
         items : [
            {
                xtype: 'fieldset',
                title: '',
                width: 1050,
                height: 495,
                x: 10,
                y: 5,
                layout: 'absolute',
                id: 'fielsetPrinManPapel',
                items: [
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 946,
                        y: 2,
                        width: 80,
                        height: 22,
                        id: 'btnBuscar',
                        listeners: {
                        	click:{
		                       fn: function(e){
		            		   	NS.storeConsultarPapel.load();
		            		   }
                        	}
                       }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 946,
                        y: 450,
                        width: 80,
                        id: PF + 'btnLimpiar',
                        listeners:{
	                        click:{
		                    	fn:function(e){
		                    	NS.gridConsultarPapel.store.removeAll();
		                    	NS.habilitarDeshabilitar(true);
		                    	NS.habilitarDesabilitarBotones(true);
		                    	NS.habilitarDesabilitarCampos(true);
		                    	}
	                        }
                        }
                    },
                    {
                        xtype: 'fieldset',
                        title: '',
                        x: 0,
                        y: 35,
                        height: 275,
                        width: 1028,
                        layout: 'absolute',
                        id: 'fielsetModPapel',
                        items: [
                        	NS.gridConsultarPapel,
                            {
                                xtype: 'button',
                                text: 'Modificar',
                                x: 750,
                                y: 230,
                                width: 80,
                                id: PF + 'btnModificar',
                                disabled: true,
                                listeners:{
                        		click: function(e)
            		 			{
                        			Ext.getCmp(PF + 'btnCancelar').setDisabled(false);
                        			NS.habilitarDeshabilitar(false);
                        			NS.habilitarDesabilitarCampos2(false);
	                        		NS.Bandera = 'M';
            		 				var registroSeleccionado = NS.gridConsultarPapel.getSelectionModel().getSelections();
            		 				if (registroSeleccionado.length <= 0)
            		 					Ext.Msg.alert('SET', 'Debe de elegir un registro para modificar');		 					
            		 				else{
            			 				Ext.getCmp(PF + 'txtPapel').setValue(registroSeleccionado[0].get('idPapel'));
            			 				Ext.getCmp(PF + 'txtIdTipoValor').setValue(registroSeleccionado[0].get('idTipoValor'));
            			 				Ext.getCmp(PF+'cmbTipoValor').setValue(registroSeleccionado[0].get('descripcion'));
            			 				papel=registroSeleccionado[0].get('idPapel');
            			 				
            		 				}
                                }
                        	    }
                            },
                            {
                                xtype: 'button',
                                text: 'Crear Nuevo',
                                x: 920,
                                y: 230,
                                width: 80,
                                id: 'btnNuevo',
                                listeners:{
                                	click:function(e){
		                        		NS.Bandera = 'N';
		                        		NS.habilitarDeshabilitar(false);
		                        		NS.habilitarDesabilitarCampos(false);
                                	}
                                }
                            },
                            {
                                xtype: 'button',
                                text: 'Eliminar',
                                x: 835,
                                y: 230,
                                width: 80,
                                id: PF + 'btnEliminar',
                                name: PF + 'btnEliminar',
                                disabled: true,
                                listeners:{
                                	click:{
                            		fn:function(e){
		                        		var regSelec = NS.gridConsultarPapel.getSelectionModel().getSelections();
		                        		if(regSelec.length == 0){
		                        			Ext.Msg.alert('SET','Debe elegir un registro');
		                        			return;
		                        		} 
                            			Ext.Msg.confirm('SET','¿Esta seguro de eliminar este Papel?', function(btn) {  
        		           		     		if(btn == 'yes') {
	        		           		     		MantenimientoDePapelAction.eliminarPapel(regSelec[0].get('idPapel'), function(res, e){
					                       			if(res !== null && res !== undefined && res !== '')
					                       			{
					                       				Ext.Msg.alert("Información SET", '' + res.msgUsuario);
					                       				NS.limpiar();
							                        	NS.habilitarDeshabilitar(true);
					                       			}
	        		           		     		});
        		           		     		}
                            			});
                                     }
                                   	}
                            	}
                             }
                           ]
                       }
                   ]
            },
            {
                xtype: 'fieldset',
                title: '',
                x: 20,
                y: 335,
                width: 1030,
                height: 120,
                layout: 'absolute',
                id: PF + 'frameNuevoModificar',
                disabled: true,
                maskDisabled: false,
                items: [
                    {
                    	xtype: 'uppertextfield',
                    	maskRe:/\w/,
				        x: 415,
				        y: 25,
				        width: 120,
				        name: PF + 'txtPapel',
				        id: PF + 'txtPapel',
				        maxLength:8 
				    },
				    NS.txtNoEmpresa,
                    {
                        xtype: 'button',
                        text: 'Aceptar',
                        x: 840,
                        y: 75,
                        width: 80,
                        id: PF + 'btnAceptar',
                        name: PF + 'btnAceptar',
                        disabled: true,
                        listeners:{
	                        click:{
			            	fn:function(e){
			            		
			            		if( Ext.getCmp(PF + 'txtPapel').getValue().length > 8 ){
			            			Ext.Msg.alert('SET', 'Longitud de papel debe ser Maximo 8 Caracteres');
	                				return;
			            		}
			            		
		            	 		var matrizPapel= new Array();
	                		    var regGridPapel = NS.gridConsultarPapel.getSelectionModel().getSelections();
	                		    var confir = '';
	                			if(regGridPapel.length == 0 && NS.Bandera != 'N') {
	                				Ext.Msg.alert('SET', 'Debe seleccionar un registro!!');
	                				return;
	                			}else if(regGridPapel.length > 0) {
	                				confir = regGridPapel[0].get('idTipoValor');
	                			}
	                			var arrPapel= {};
	                			arrPapel.idPapel=Ext.getCmp(PF + 'txtPapel').getValue();
	                			arrPapel.idTipoValor=Ext.getCmp(PF + 'txtIdTipoValor').getValue();
	                			arrPapel.idTipoValorOriginal=papel;
	                			matrizPapel[0]=arrPapel;
	                			var jsonStringPapel = Ext.util.JSON.encode(matrizPapel);
	                			
	                			MantenimientoDePapelAction.accionPapel(jsonStringPapel,NS.Bandera,confir,function(res, e) {
	    							if(res != null && res != undefined && res != '') {
	    								Ext.Msg.alert("Información SET", '' + res.msgUsuario);
	    								NS.limpiar();
	    								NS.habilitarDesabilitarCampos(true);
	    								NS.habilitarDeshabilitar(true);
	    							}
    							});
			             	}
	                        }
		               }
                    },
                    {
                        xtype: 'label',
                        text: 'Papel:',
                        x: 415,
                        y: 10
                    },
                    {
                        xtype: 'button',
                        text: 'Cancelar',
                        x: 925,
                        y: 75,
                        width: 80,
                        height: 22,
                        id: PF + 'btnCancelar',
                        name: PF + 'btnCancelar',
                        disabled: true,
                        listeners:{
                        	click:{
		                    	fn:function(e){
			                    	NS.habilitarDeshabilitar(true);
			                    	NS.habilitarDesabilitarBotones(true);
			                    	NS.habilitarDesabilitarCampos(true);
		                    	}
                        	}
                        }
                    },
                    NS.cmbTipoValor,
                    {
                        xtype: 'label',
                        text: 'Tipo Valor:',
                        x: 550,
                        y: 10
                    }
                ]
            }
        ]
	});
	 NS.MantenimientoDePapeles.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});