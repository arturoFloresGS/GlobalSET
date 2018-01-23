 Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.PrestamosInterempresas.ArbolDeEmpresas');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	
	NS.limpiar = function()
	{
		NS.ArbolDeEmpresas.getForm().reset();
		
		NS.iIdEmpresaRaiz = 0;
		NS.iIdEmpresaHijo = 0;
		NS.iIdNvaEmpresaRaiz = 0;
		NS.sRuta = '';
		
		NS.iIdEmpActual = 0;
		NS.iIdEmpPadre = 0;
	};
	
	NS.ocultar = function()
	{
		Ext.getCmp(PF + 'lblInferiorUno').setVisible(false);
		Ext.getCmp(PF + 'txtEmpresaHija').setVisible(false);
		Ext.getCmp(PF + 'cmbEmpresasHijo').setVisible(false);
		Ext.getCmp(PF + 'txtEmpresaNvaRaiz').setVisible(false);
		Ext.getCmp(PF + 'cmbNvaRaiz').setVisible(false);
		Ext.getCmp(PF + 'lblInferiorDos').setVisible(false);
		Ext.getCmp(PF + 'txtMonto').setVisible(false);
		
		Ext.getCmp(PF + 'txtEmpresaHija').reset();
		Ext.getCmp(PF + 'cmbEmpresasHijo').reset();
		Ext.getCmp(PF + 'txtEmpresaNvaRaiz').reset();
		Ext.getCmp(PF + 'cmbNvaRaiz').reset();
		Ext.getCmp(PF + 'txtMonto').reset();
	};
	
	//store del combo cmbEmpresaRaiz
	NS.storeEmpresaRaiz = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bExistentes : true
		},
		root: '',
		paramOrder:['bExistentes'],
		directFn: PrestamosInterempresasAction.llenarCmbEmpresaRaiz, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresaRaiz, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen empresas raiz');
					return;
				}
			}
		}
	}); 
	
	NS.storeEmpresaRaiz.load();
	NS.storeEmpresaRaiz.sort('descripcion','A-Z');
	
	NS.accionarEmpresaRaiz = function(valueCombo)
	{
		PrestamosInterempresasAction.obtenerArbolEmpresa(parseInt(valueCombo),function(response, e)
			{
			 	var jSonMenu = Ext.util.JSON.decode(response);
				
				if(response === null)
				{
					BFwrk.Util.msgShow('No hay una estructura para esta empresa', 'INFO');
					return;
				}
				
				var arbol = jSonMenu.descripcion;
			    NS.rootTreeNodeMenu = new Ext.tree.AsyncTreeNode({
			           text: 'Empresas',
			           //draggable: false,
			           //id: PF + 'rootTreeComp' + jSonMenu.id,
			           //id: jSonMenu.id,
			           id: 'Empresas',
			           expanded:true,
			           allowChildren:true,
			           children: arbol,
			           //disabled:true,
			           listeners: 
			           {
			           		click: function(node, event) 
			           		{
				        	 
				       		}
				       }
				});
			
		        NS.treePanelMenu.setRootNode(NS.rootTreeNodeMenu);	
		        NS.treePanelMenu.render(PF + 'fSetArbol');	
		        
		        Ext.getCmp(PF + 'btnAgregarNodo').setDisabled(false);
		        Ext.getCmp(PF + 'btnImprimir').setDisabled(false);		
			}
		);
		//Agregar empresa raiz al combo para un nuevo hijo
		//NS.storeEmpresasHijo.baseParams.iEmpresaRaiz = parseInt(valueCombo);
	};
	
	NS.cmbEmpresaRaiz = new Ext.form.ComboBox({
		store: NS.storeEmpresaRaiz,
		name: PF + 'cmbEmpresaRaiz',
		id: PF + 'cmbEmpresaRaiz',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 150,
        y: 0,
        width: 240,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione empresa raiz',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn:function(combo, valor) 
				{
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa',NS.cmbEmpresaRaiz.getId());
				 	NS.accionarEmpresaRaiz(combo.getValue());
				 	NS.iIdEmpresaRaiz = combo.getValue();
				}
			}
		}
	});
	
	//Funcion para agregar el nuevo nodo al arbol
	NS.accionarEmpresaHijo = function(valueCombo, sMonto)
	{
		var newNode =  new Ext.tree.TreeNode({
			id : valueCombo,
            text : valueCombo + ':' + $('input[id*="'+ NS.cmbEmpresasHijo.getId() +'"]').val() + ':' + sMonto,
            leaf : true,
            cls: 'nvoAgregado',
            allowChildren : true,
            allowDrag: false
     	});
     	
   		var selectedItem = NS.treePanelMenu.getSelectionModel().getSelectedNode();
   		var nuevoNodo = selectedItem.getPath().split("rootTreeComp")[1]+ "/" +newNode.id;
   		var newRute="";
   		var len = nuevoNodo.split("/").length;
   		for(var i = 0; i < len; i = i + 1)
   		{
           	if(nuevoNodo.split("xnode").length > 1)			
           		if(i == 1 )
               		continue;
               	newRute = newRute +nuevoNodo.split("/")[i] +  "-";                            
        }    
        newRute = newRute.substr(0,newRute.length -1);
		newNode.qtipCfg =newRute;
		//alert(selectedItem.qtipCfg)
   		if(selectedItem.isLeaf()) 
   		{
			selectedItem.leaf = false;
			selectedItem.appendChild([newNode]);
         } else {
            selectedItem.insertBefore(newNode, selectedItem.nextSibling);            
         }
        $('.Seleccionado').removeClass('Seleccionado');                              
   		NS.treePanelMenu.getSelectionModel().unselect(selectedItem,true);
	};
	
	//Store para combo de empresas hijo
	NS.storeEmpresasHijo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iEmpresaRaiz : 0
		},
		root: '',
		paramOrder:['iEmpresaRaiz'],
		directFn: PrestamosInterempresasAction.llenarCmbEmpresasHijo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresasHijo, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen empresas hijo');
					return;
				}
			}
		}
	}); 
	
	NS.cmbEmpresasHijo = new Ext.form.ComboBox({
		store: NS.storeEmpresasHijo,
		name: PF + 'cmbEmpresasHijo',
		id: PF + 'cmbEmpresasHijo',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 220,
        y: 280,
        width: 210,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione empresa hijo',
		triggerAction: 'all',
		value: '',
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF + 'txtEmpresaHija',NS.cmbEmpresasHijo.getId());
				 	NS.iIdEmpresaHijo = combo.getValue();
				 	//NS.accionarEmpresaHijo(NS.iIdEmpresaHijo);
				}
			}
		}
	});
	
	
	NS.accionarNvaRaiz = function(valueCombo)
	{
	
	};
	
	//Store para combo de una nueva raiz
	NS.storeNvaRaiz = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bExistentes : false
		},
		root: '',
		paramOrder:['bExistentes'],
		directFn: PrestamosInterempresasAction.llenarCmbEmpresaRaiz, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeNvaRaiz, msg:"Cargando..."});
				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen empresas para nueva raiz');
					return;
				}
			}
		}
	}); 
	
	NS.cmbNvaRaiz = new Ext.form.ComboBox({
		store: NS.storeNvaRaiz,
		name: PF + 'cmbNvaRaiz',
		id: PF + 'cmbNvaRaiz',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 220,
        y: 280,
        width: 220,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione empresa raiz',
		triggerAction: 'all',
		value: '',
		hidden: true,
		listeners:
		{
			select:
			{
				fn:function(combo, valor) 
				{
				 	BFwrk.Util.updateComboToTextField(PF + 'txtEmpresaNvaRaiz',NS.cmbNvaRaiz.getId());
				 	NS.iIdNvaEmpresaRaiz = combo.getValue();
				}
			}
		}
	});
	
	NS.obtenerReporte = function()
	{
		var strParams = '';
		
		strParams = '?nomReporte=ReporteArbolEmpPresInter';	
		
		strParams += '&'+'nomParam1=sFecHoy';
		strParams += '&'+'valParam1=' + apps.SET.FEC_HOY;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
	};
	
	NS.validarDatos = function ()
	{
		
	};
	
	NS.ArbolDeEmpresas = new Ext.form.FormPanel({
    title: 'Arbol de Empresas',
    width: 859,
    height: 467,
    padding: 10,
    layout: 'absolute',
    id: PF + 'ArbolDeEmpresas',
    name: PF + 'ArbolDeEmpresas',
    renderTo: NS.tabContId,
    frame: true,
    autoScroll: true,
       items : [
	            {
	                xtype: 'fieldset',
	                title: '',
	                width: 840,
	                height: 420,
	                x: 10,
	                y: 10,
	                layout: 'absolute',
	                id: 'fSetPrincipal',
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Empresa raíz:',
	                        x: 0,
	                        y: 0
	                    },
	                    {
	                        xtype: 'numberfield',
	                        x: 80,
	                        y: 0,
	                        width: 60,
	                        name: PF + 'txtNoEmpresa',
	                        id: PF + 'txtNoEmpresa',
	                        listeners:
                            {
                            	change: 
                            	{
                            		fn: function(box, value)
                            		{
                            			var valueCombo = '';
                            			if(box.getValue() !== '')
                            				valueCombo = BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresaRaiz.getId());
                            			alert('valueCombo ' + valueCombo);
                            			if(valueCombo !== null && valueCombo !== '' && valueCombo !== undefined)
											NS.iIdEmpresaRaiz = valueCombo;
										else
											BFwrk.Util.msgShow('No existe la empresa ' + box.getValue(), 'INFO');
										
                            		}
                            	}
                            }
	                    },
	                    NS.cmbEmpresaRaiz,
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 30,
	                        height: 330,
	                        layout: 'absolute',
	                        id: PF + 'fSetEmpresas',
	                        name: PF + 'fSetEmpresas',
	                        items: [
		                        {
		                        	xtype: 'fieldset',
			                        title: '',
			                        height: 20,
			                        layout: 'absolute',
			                        id: PF + 'fSetArbol',
			                        name: PF + 'fSetArbol',
			                        //id: 'fSetArbol',
			                        //name: 'fSetArbol',
			                        items: [
			                        
			                        ]
			                     },
	                            {
	                                xtype: 'label',
	                                text: 'Nuevo:',
	                                x: 100,
	                                y: 280,
	                                id: PF + 'lblInferiorUno',
	                                hidden: true
	                            },
	                            {
	                                xtype: 'numberfield',
	                                x: 150,
	                                y: 280,
	                                width: 60,
	                                name: PF + 'txtEmpresaHija',
	                                id: PF + 'txtEmpresaHija',
	                                hidden: true,
	                                listeners:
	                                {
	                                	change: 
	                                	{
	                                		fn: function(box, value)
	                                		{
	                                			var valueCombo = '';
	                                			if(box.getValue() !== '')
                                					valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresaHija',NS.cmbEmpresasHijo.getId());
	                                			if(valueCombo !== null && valueCombo !== '' && valueCombo !== undefined)
				 									NS.iIdEmpresaHijo = valueCombo;
				 								else
				 									BFwrk.Util.msgShow('No existe la empresa ' + box.getValue(), 'INFO');
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 150,
	                                y: 280,
	                                width: 60,
	                                name: PF + 'txtEmpresaNvaRaiz',
	                                id: PF + 'txtEmpresaNvaRaiz',
	                                hidden: true,
	                                listeners:
	                                {
	                                	change: 
	                                	{
	                                		fn: function(box, value)
	                                		{
	                                			var valueCombo = '';
	                                			if(box.getValue() !== '')
                                					valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresaNvaRaiz',NS.cmbNvaRaiz.getId());
	                                			if(valueCombo !== null && valueCombo !== '' && valueCombo !== undefined)
				 									NS.iIdNvaEmpresaRaiz = valueCombo;
				 								else
				 									BFwrk.Util.msgShow('No existe la empresa ' + box.getValue(), 'INFO');
				 									
	                                		}
	                                	}
	                                }
	                            },
	                                NS.cmbEmpresasHijo,
	                                NS.cmbNvaRaiz,
	                            {
	                                xtype: 'label',
	                                text: 'Monto:',
	                                x: 460,
	                                y: 280,
	                                id: PF + 'lblInferiorDos',
	                                hidden: true
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 510,
	                                y: 280,
	                                width: 140,
	                                name: PF + 'txtMonto',
	                                id: PF + 'txtMonto',
	                                hidden: true,
	                                listeners: 
	                                {
	                                	change:
	                                	{
	                                		fn: function(box, value)
	                                		{
	                                			box.setValue(BFwrk.Util.formatNumber(box.getValue()));
	                                		}
	                                	}
	                                }
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 150,
	                        y: 370,
	                        width: 90,
	                        id: PF + 'btnLimpiar',
	                        name: PF + 'btnLimpiar',
	                        listeners:
	                        {
	                        	click:
	                        	{
	                        		fn: function(btn)
	                        		{
	                        			NS.ocultar();
	                        			NS.limpiar();
	                        			NS.treePanelMenu.destroy();
	                        			NS.iniciarArbol();
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Ejecutar',
	                        x: 260,
	                        y: 370,
	                        width: 90,
	                        id: PF + 'btnEjecutar',
	                        name: PF + 'btnEjecutar',
	                        disabled: true,
	                        listeners:{
	                        	click:{
	                        		fn: function(e){
	                        			//var  lol = $('.nvoAgregado')
	                        			//alert(lol)
	                        			var uMonto = 0;
	                        			if(NS.cmbEmpresasHijo.isVisible())
	                        			{
	                        				if(NS.sRuta === '')
											{
												BFwrk.Util.msgShow('Es necesario seleccionar la Empresa Padre', 'INFO');
												return;
											}
	                        				else if(NS.iIdEmpresaHijo <= 0)
	                        				{
	                        					BFwrk.Util.msgShow('Debe seleccionar una empresa hija', 'INFO');
	                        					return;
	                        				}
	                        				else if(Ext.getCmp(PF + 'txtMonto').getValue() === '')
	                        				{
	                        					BFwrk.Util.msgShow('Debe agregar un monto', 'INFO');
	                        					return;
	                        				}
	                        				
	                        				uMonto = BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtMonto').getValue());
	                        				   
	                        				PrestamosInterempresasAction.agregarNodosArbol(NS.cmbEmpresasHijo.isVisible(), NS.sRuta, parseInt(NS.iIdEmpresaRaiz),
	                        												parseInt(NS.iIdEmpresaHijo), parseFloat(uMonto),function(response, e)
	                        				{
	                        					if(response !== null && response !== '')
	                        					{
	                        						BFwrk.Util.msgShow('' + response, 'INFO');
	                        						NS.accionarEmpresaHijo(NS.iIdEmpresaHijo, Ext.getCmp(PF + 'txtMonto').getValue());
	                        					}
	                        				});
	                        			}
	                        			else if(NS.cmbNvaRaiz.isVisible())
	                        			{
	                        				   
	                        				PrestamosInterempresasAction.agregarNodosArbol(NS.cmbEmpresasHijo.isVisible(), NS.sRuta, parseInt(NS.iIdNvaEmpresaRaiz),
	                        												0, parseFloat(uMonto),function(response, e)
	                        				{
	                        					if(response !== null && response !== '')
	                        					{
	                        						BFwrk.Util.msgShow('' + response, 'INFO');
	                        						if(response.indexOf('correct') > 0)
	                        						{
	                        							NS.storeEmpresaRaiz.load();
														NS.storeEmpresaRaiz.sort('descripcion','A-Z');
														NS.accionarEmpresaRaiz(NS.iIdNvaEmpresaRaiz);
	                        						}
	                        					}
	                        				});
	                        			}
	                        			
	                        			//Validar que no se kiera meter mas de un padre
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Nuevo árbol',
	                        x: 370,
	                        y: 370,
	                        width: 90,
	                        id: PF + 'btnNuevoArbol',
	                        listeners:
	                        {
	                        	click:
	                        	{
	                        		fn: function(btn)
	                        		{
	                        			//Destruir y crear nuevamente el arbol
	                        			NS.treePanelMenu.destroy();
	                        			NS.iniciarArbol();
	                        			
	                        			NS.storeNvaRaiz.load();
	                        			NS.storeNvaRaiz.sort('descripcion','A-Z');
	                        			NS.ocultar();
	                        			NS.cmbNvaRaiz.setVisible(true);
	                        			Ext.getCmp(PF + 'txtEmpresaNvaRaiz').setVisible(true);
	                        			Ext.getCmp(PF + 'lblInferiorUno').setVisible(true);
	                        			Ext.getCmp(PF + 'lblInferiorUno').setText('Nuevo');
										Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
	                        			//Ext.getCmp(PF + 'fSetArbol').removeAll();
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Agregar Nodo',
	                        x: 480,
	                        y: 370,
	                        width: 90,
	                        id: PF + 'btnAgregarNodo',
	                        name: PF + 'btnAgregarNodo',
	                        disabled: true,
	                        listeners:
	                        {
	                        	click:
	                        	{
	                        		fn: function(e)
	                        		{ 
	                        			noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa');
	                        			if (noEmpresa = "") noEmpresa = 0
	                        			NS.storeEmpresasHijo.baseParams.iEmpresaRaiz = Ext.getCmp(PF + 'txtEmpresaNvaRaiz')
	                        			NS.storeEmpresasHijo.load();
	                        			NS.storeEmpresasHijo.sort('descripcion','A-Z');
	                        			NS.ocultar();
	                        			Ext.getCmp(PF + 'lblInferiorUno').setVisible(true);
	                        			Ext.getCmp(PF + 'lblInferiorUno').setText('Nuevo');
										Ext.getCmp(PF + 'txtEmpresaHija').setVisible(true);
										Ext.getCmp(PF + 'cmbEmpresasHijo').setVisible(true);
										Ext.getCmp(PF + 'lblInferiorDos').setVisible(true);
										Ext.getCmp(PF + 'txtMonto').setVisible(true);
										Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
										
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Eliminar',
	                        x: 590,
	                        y: 370,
	                        width: 90,
	                        id: 'btnEliminar',
	                        listeners :
	                        {
	                        	click:
	                        	{
	                        		fn:function(e){
	                        			
	                        			if(NS.iIdEmpPadre === 'Empresas' || NS.iIdEmpPadre === 0)
	                        			{
	                        				BFwrk.Util.msgShow('No se puede eliminar la empresa Raiz', 'INFO');
	                        				return;
	                        			}
	                        			else if(NS.iIdEmpActual <= 0 || NS.iIdEmpPadre <= 0 || NS.iIdEmpresaRaiz <= 0)
	                        			{
	                        				BFwrk.Util.msgShow('Debe seleccionar una empresa para eliminar', 'INFO');
	                        				return;
	                        			}
	                        			
	                        			Ext.Msg.show({
										title: 'Información SET',
										msg: '¿Desea Eliminar la empresa ' + NS.iIdEmpActual + ' del arbol?',
										buttons: 
											{
												ok: true,
												no: false,
												cancel: true
											},
										icon: Ext.MessageBox.WARNING,
										fn: 
											function(btn) 
											{
													if(btn === 'ok')
													{
														PrestamosInterempresasAction.eliminarNodosArbol(parseInt(NS.iIdEmpresaRaiz), parseInt(NS.iIdEmpActual), 
                        																	parseInt(NS.iIdEmpPadre),function(response, e)
				                        				{
				                        					if(response !== null && response !== '')
				                        					{
				                        						BFwrk.Util.msgShow('' + response, 'INFO');
				                        						if(response.indexOf('no') < 0)
				                        						{
				                        							var selectedItem = NS.treePanelMenu.getSelectionModel().getSelectedNode();
	                       			  								selectedItem.remove(true);
				                        						}
				                        					}
				                        				});
													}
													else
														return;
											}
										});
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 700,
	                        y: 370,
	                        width: 90,
	                        id: PF + 'btnImprimir',
	                        name: PF + 'btnImprimir',
	                        disabled: true,
	                        listeners:
	                        {
	                        	click:
	                        	{
	                        		fn: function(btn)
	                        		{
	                        			NS.obtenerReporte();
	                        		}
	                        	}
	                        }
	                    }
	                ]
	            }
        	]
	});
 
 	NS.iniciarArbol = function()
 	{
 		NS.treePanelMenu = new Ext.tree.TreePanel({
	    //NS.treePanelMenu = new Ext.ux.tree.CheckTreePanel({
	        useArrows: true,
	        autoScroll: true,
	        animate: true,
	        enableDD: true,
	        containerScroll: true,
		    //rootVisible: false,
	        border: false,
	        x: 0,
	        y: 0,
	        //auto create TreeLoader
	        //dataUrl: 'get-nodes.php',
			loader: new Ext.tree.TreeLoader(),
			listeners: 
			{
	           expand: function(pan, adjWidth, adjHeight, rawWidth, rawHeight)
	           {
		            /*
		            alert('TreePanel resize: '+'w='+adjWidth+' h='+adjHeight+' - '+'rw='+rawWidth+' rh='+rawHeight);
		            var el = pan.getEl();
		            alert('Elemento ' + el);
					if(el==null) return;
		                 var childEl = el.first();
		            */
	        	},
	        	click: function(node)
	        	{	
	   				NS.treePanelMenu.getSelectionModel().clearSelections(true);
	   				$('.Seleccionado').removeClass('Seleccionado');
	   				//alert(node.qtipCfg)
		        	NS.treePanelMenu.getSelectionModel().select(node);
		        	node.setCls('Seleccionado');
		        	
		        	var selectedItem = NS.treePanelMenu.getSelectionModel().getSelectedNode();
		        	var rutaNodos = '';
					var ruta = '';
					rutaNodos = selectedItem.getPath();
					
					NS.iIdEmpActual = selectedItem.id;
					
					if(selectedItem.parentNode === null)
						NS.iIdEmpPadre = 0;
					else
						NS.iIdEmpPadre = selectedItem.parentNode.id;	
					
					//alert('nodoActual ' + NS.iIdEmpActual);
					//alert('nodoPadre' + NS.iIdEmpPadre);
					//alert('rutaTotal ' + rutaNodos);
					
					ruta += rutaNodos.substring(rutaNodos.indexOf('/Empresas/') + 10) 
					//alert('ruta: ' + ruta);	
					NS.sRuta = ruta;
					/*if((rutaNodos.indexOf('xnode')) > 0)
					{
						//Para conocer el por que de los índices, ver el getPath('...xnode...').
						ruta = rutaNodos.substring(1,rutaNodos.indexOf('xnode-') - 1);
						ruta += rutaNodos.substring(rutaNodos.indexOf('xnode-') + 9) 
						alert('ruta: ' + ruta);	
						NS.sRuta = ruta;
					}Se hacia esto para sacar la ruta, cuando desde la estructura del json en java, no se le asinaba id 
					al padre y por default ponia un xnode-566*/
	        	}
	        }
	    });
 	};
    
    NS.limpiar();
    NS.iniciarArbol();
	NS.ArbolDeEmpresas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});