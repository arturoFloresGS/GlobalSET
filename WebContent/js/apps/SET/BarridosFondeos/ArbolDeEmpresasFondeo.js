 Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.PrestamosInterempresas.ArbolDeEmpresas');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	
	NS.iIdEmpresaPadre = 0;
	NS.idArbol=0;
	NS.limpiar = function()
	{
		NS.ArbolDeEmpresas.getForm().reset();
		
		NS.iIdEmpresaRaiz = 0;
		NS.iIdEmpresaPadre = 0;
		NS.iIdEmpresaHijo = 0;
		NS.iIdNvaEmpresaRaiz = 0;
		NS.sRuta = '';
		NS.eRaiz = 0;

		Ext.getCmp(PF + 'btnAgregarNodo').setDisabled(true);
		NS.iIdEmpActual = 0;
		NS.iIdEmpPadre = 0;
		NS.idArbol=0;
	};
	
	NS.ocultar = function()
	{
		Ext.getCmp(PF + 'lblInferiorUno').setVisible(false);
		Ext.getCmp(PF + 'txtEmpresaHija').setVisible(false);
		Ext.getCmp(PF + 'cmbEmpresasHijo').setVisible(false);
		Ext.getCmp(PF + 'cmbTipoOperacion').setVisible(false);
		Ext.getCmp(PF + 'txtEmpresaNvaRaiz').setVisible(false);
		Ext.getCmp(PF + 'cmbNvaRaiz').setVisible(false);
		Ext.getCmp(PF + 'cmbTipoOperacion').setVisible(false);
		Ext.getCmp(PF + 'lblInferiorDos').setVisible(false);
		Ext.getCmp(PF + 'txtMonto').setVisible(false);
		Ext.getCmp(PF + 'txtDesc').setVisible(false);
		Ext.getCmp(PF + 'lblInferiorDesc').setVisible(false);
		
		Ext.getCmp(PF + 'cmbTipoValor').setVisible(false);
		Ext.getCmp(PF + 'lblInferiorValorAltaNodo').setVisible(false);
		Ext.getCmp(PF + 'lblInferiorValor').setVisible(false);

		Ext.getCmp(PF + 'txtDesc').reset();
		Ext.getCmp(PF + 'txtEmpresaHija').reset();
		Ext.getCmp(PF + 'cmbEmpresasHijo').reset();
		Ext.getCmp(PF + 'cmbTipoOperacion').reset();
		Ext.getCmp(PF + 'txtEmpresaNvaRaiz').reset();
		Ext.getCmp(PF + 'cmbNvaRaiz').reset();
		Ext.getCmp(PF + 'txtMonto').reset();
		
		Ext.getCmp(PF + 'cmbTipoValor').reset();
	};
	
	//store del combo cmbEmpresaRaiz
	NS.storeEmpresaRaiz = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bExistentes : true,
		    idArbol : NS.idArbol
		},
		root: '',
		paramOrder:['bExistentes','idArbol'],
		directFn: BarridosFondeosAction.llenarCmbEmpresaRaiz, 
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
				Ext.getCmp(PF + 'txtNoEmpresa').setValue(records[0].get('id'));
				NS.cmbEmpresaRaiz.setValue(records[0].get('descripcion'));
			}
		}
	}); 
	
	//store del combo cmbEmpresaRaiz
	NS.storeArbol = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bExistentes : true
		},
		root: '',
		paramOrder:['bExistentes'],
		directFn: BarridosFondeosAction.llenarCmbArbol, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
			 {name: 'campoUno'}
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
	
	//NS.storeEmpresaRaiz.load();
	//NS.storeEmpresaRaiz.sort('descripcion','A-Z');
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeArbol, msg:"Cargando..."});
	NS.storeArbol.load();
	NS.storeArbol.sort('descripcion','A-Z');
//	NS.storeEmpresaRaiz.sort('descripcion','A-Z');

	NS.accionarEmpresaRaiz = function(valueCombo)
	{

		BarridosFondeosAction.obtenerArbolEmpresa(parseInt(valueCombo),function(response, e)
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
		        
		        NS.rootTreeNodeMenu.expand();
		        
		        Ext.getCmp(PF + 'btnAgregarNodo').setDisabled(false);
		        Ext.getCmp(PF + 'btnImprimir').setDisabled(false);		
			}
		);
		//Agregar empresa raiz al combo para un nuevo hijo
		//NS.storeEmpresasHijo.baseParams.iEmpresaRaiz = parseInt(valueCombo);
	};
	
	NS.cmbArbol = new Ext.form.ComboBox({
		store: NS.storeArbol,
		name: PF + 'cmbArbol',
		id: PF + 'cmbArbol',
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
		emptyText: 'Seleccione el árbol',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
				fn:function(combo, valor) 
				{
					NS.ocultar();
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNoArbol',NS.cmbArbol.getId());
				 	//BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa',NS.campoUno);
				 	BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresaRaiz.getId());
				 	var id = combo.getValue();
				 	NS.idArbol = NS.storeArbol.getById(id).get('id');
				 	NS.campoUno = NS.storeArbol.getById(id).get('campoUno');
				    NS.storeEmpresaRaiz.baseParams.idArbol = NS.idArbol;
				    var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresaRaiz, msg:"Cargando..."});
				    NS.storeEmpresaRaiz.load();
				    NS.storeEmpresaRaiz.sort('descripcion','A-Z');
				 	NS.accionarEmpresaRaiz(NS.campoUno);

				}
			}
		}
	});

	NS.cmbEmpresaRaiz = new Ext.form.ComboBox({
		store: NS.storeEmpresaRaiz,
		name: PF + 'cmbEmpresaRaiz',
		id: PF + 'cmbEmpresaRaiz',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 600,
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
					//BFwrk.Util.updateComboToTextField(PF + 'txtNoArbol',NS.cmbEmpresaRaiz.getId());
				 	//BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoArbol', NS.cmbArbol.getId());
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa',NS.cmbEmpresaRaiz.getId());
				 	NS.accionarEmpresaRaiz(combo.getValue());
				 	NS.ocultar();
				 	//NS.iIdEmpresaRaiz = combo.getValue();
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
		directFn: BarridosFondeosAction.llenarCmbEmpresasHijo, 
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
   	 	y: 320,
        width: 220,
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
			bExistentes : false,
			idArbol : NS.idArbol
			
		},
		root: '',
		paramOrder:['bExistentes','idArbol'],
		directFn: BarridosFondeosAction.llenarCmbEmpresaRaiz, 
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
	
	NS.storeTipoOperacion = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bExistentes : false
		},
		root: '',
//		paramOrder:['bExistentes'],
		directFn: BarridosFondeosAction.llenarCmbTipoOperacion, 
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
					Ext.Msg.alert('SET','No existen tipos de operación');
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
   	 	y: 320,
        width: 240,
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

	NS.cmbTipoOperacion = new Ext.form.ComboBox({
		store: NS.storeTipoOperacion,
		name: PF + 'cmbTipoOperacion',
		id: PF + 'cmbTipoOperacion',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 540,
   	 	y: 320,
        width: 220,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione tipo operación',
		triggerAction: 'all',
		value: '',
		hidden: true,
	});

	NS.datosTipoValor = [['T', 'T'],['%', '%'],	['NECESIDAD', 'NECESIDAD']];	
	
	NS.storeTipoValor = new Ext.data.SimpleStore({
   		idProperty: 'id',
   		fields: [
   					{name: 'id'},
   					{name: 'descripcion'}
   				]
   	});
   	NS.storeTipoValor.loadData(NS.datosTipoValor);

	
	NS.cmbTipoValor = new Ext.form.ComboBox({
		store: NS.storeTipoValor,
		name: PF + 'cmbTipoValor',
		id: PF + 'cmbTipoValor',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 220,
   	 	y: 360,
        width: 220,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione tipo valor',
		triggerAction: 'all',
		value: '',
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, valor){
					var monto = combo.getValue();
					if (monto == '%')
						{
						Ext.getCmp(PF + 'lblInferiorValor').setVisible(true);
						Ext.getCmp(PF + 'txtMonto').setVisible(true);
						}
					else
						{
						Ext.getCmp(PF + 'lblInferiorValor').setVisible(false);
						Ext.getCmp(PF + 'txtMonto').setVisible(false);
						}
				
				}
			}
		}
	});

	NS.obtenerReporte = function()
	{
		var id = NS.cmbArbol.getValue();
	 	NS.noEmpresaReporte = NS.storeArbol.getById(id).get('campoUno');
		var strParams = '';
		strParams = '?nomReporte=ReporteArbolEstructura';	
		
		strParams += '&'+'noEmpresa=' + NS.noEmpresaReporte;
		strParams += '&'+'idUsuario=' + NS.idUsuario;
		strParams += '&EMPRESA=' + NS.cmbArbol.getRawValue();
		
		window.open("/SET/jsp/Reportes.jsp" + strParams);
	};
	
	NS.validarDatos = function ()
	{
		
	};
	
	NS.ArbolDeEmpresas = new Ext.form.FormPanel({
    title: 'Árbol de Empresas Fondeos',
    width: 1000,
    height: 100,
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
	                title: 'Nivel 1',
	                width: 1050,
	                height: 530,
	                x: 10,
	                y: 10,
	                //autoScroll: true,
	                layout: 'absolute',
	                id: 'fSetPrincipal',
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Árbol de Fondeo',
	                        x: 0,
	                        y: 0
	                    },
	                    {
	                        xtype: 'numberfield',
	                        x: 80,
	                        y: 0,
	                        width: 60,
	                        name: PF + 'txtNoArbol',
	                        id: PF + 'txtNoArbol',
	                        listeners:
                            {
                            	change: 
                            	{
                            		fn: function(box, value)
                            		{
                            			var valueCombo = '';
                            			if(box.getValue() !== '') {
                            				valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoArbol', NS.cmbArbol.getId());
                            			} if(valueCombo !== null && valueCombo !== '' && valueCombo !== undefined){
											//NS.iIdEmpresaRaiz = valueCombo;
											NS.campoUno = NS.storeArbol.getById(valueCombo).get('campoUno');
										 	BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoArbol', NS.cmbEmpresaRaiz.getId());
										 	//Ext.getCmp(PF + 'txtNoEmpresa').setValue(NS.campoUno);
										 	NS.idArbol = NS.storeArbol.getById(valueCombo).get('id');
										 	NS.storeEmpresaRaiz.baseParams.idArbol = NS.idArbol;
										 	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresaRaiz, msg:"Cargando..."});
										    NS.storeEmpresaRaiz.load();
										    NS.storeEmpresaRaiz.sort('descripcion','A-Z');
										 	NS.accionarEmpresaRaiz(NS.campoUno);
                            			}else{
											BFwrk.Util.msgShow('No existe el árbol [' + box.getValue() +']', 'INFO');
											Ext.getCmp(PF + 'txtNoArbol').setValue('');
                            			}
                            			NS.ocultar();
										
                            		}
                            	}
                            }
	                    },
	                    NS.cmbArbol,
	                    {
	                        xtype: 'label',
	                        text: 'Empresa raíz:',
	                        x: 450,
	                        y: 0
	                    },
	                    {
	                        xtype: 'numberfield',
	                        x: 520,
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
                            			if(box.getValue() !== ''){
                            				valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresaRaiz.getId());
                            			}

                            			if(valueCombo !== null && valueCombo !== '' && valueCombo !== undefined){
											NS.idArbol = valueCombo;
										 	BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbArbol.getId());
										 	Ext.getCmp(PF + 'txtNoArbol').setValue(valueCombo);
										 	NS.accionarEmpresaRaiz(NS.idArbol);
                            			}else
											BFwrk.Util.msgShow('No existe la empresa ' + box.getValue(), 'INFO');
										
                            		}
                            	}
                            }
	                    },
	                    NS.cmbEmpresaRaiz,
	                    {
	                        xtype: 'fieldset',
	                        title: 'Nivel 2',
	                        x: 0,
	                        y: 60,
	                        height: 500,
	                        layout: 'absolute',
	                        id: PF + 'fSetEmpresas',
	                        name: PF + 'fSetEmpresas',
	                        items: [
		                        {
		                        	xtype: 'fieldset',
			                        title: 'Nivel 3',
			                        height: 20,
			                        width: 850,
			                        layout: 'absolute',
			                        id: PF + 'fSetArbol',
			                        name: PF + 'fSetArbol',
			                        //autoScroll: true,
			                        //id: 'fSetArbol',
			                        //name: 'fSetArbol',
			                        items: [
			                        
			                        ]
			                     },
	                            {
	                                xtype: 'label',
	                                text: 'Nuevo:',
	                                x: 100,
	                                y: 320,
	                                id: PF + 'lblInferiorUno',
	                                hidden: true
	                            },
	                            {
	                                xtype: 'numberfield',
	                                x: 150,
	                                y: 320,
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
	                                y: 320,
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
				 								else{
				 									BFwrk.Util.msgShow('No existe la empresa ' + box.getValue(), 'INFO');
				 									Ext.getCmp(PF + 'txtEmpresaNvaRaiz').reset();
				 								}
				 									
	                                		}
	                                	}
	                                }
	                            },
	                                NS.cmbEmpresasHijo,
	                                NS.cmbNvaRaiz,
	                                NS.cmbTipoOperacion,
                                {
	                                xtype: 'label',
	                                text: 'Tipo operación:',
	                                x: 460,
	                                y: 320,
	                                id: PF + 'lblInferiorDos',
	                                hidden: true
	                            },
                                {
	                                xtype: 'label',
	                                text: 'Descripción:',
	                                x: 500,
	                                y: 320,
	                                id: PF + 'lblInferiorDesc',
	                                hidden: true
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 600,
	                                y: 320,
	                                width: 140,
	                                name: PF + 'txtDesc',
	                                id: PF + 'txtDesc',
	                                hidden: true,
	                            },{
	                                xtype: 'label',
	                                text: 'Tipo valor:',
	                                x: 150,
	                                y: 360,
	                                id: PF + 'lblInferiorValorAltaNodo',
	                                hidden: true
	                            },
	                            NS.cmbTipoValor,
	                            {
	                                xtype: 'label',
	                                text: 'Valor:',
	                                x: 460,
	                                y: 360,
	                                id: PF + 'lblInferiorValor',
	                                hidden: true
	                            },
	                            {
	                                xtype: 'numberfield',
	                                x: 540,
	                                y: 360,
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
	                        x: 900,
	                        y: 340,
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
	                        x: 900,
	                        y: 290,
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
	                        			
	                        			if(NS.cmbEmpresasHijo.isVisible()){
	                        				if(NS.sRuta === ''){
												BFwrk.Util.msgShow('Es necesario seleccionar la Empresa Padre', 'INFO');
												return;
											}else if(NS.iIdEmpresaHijo <= 0){
	                        					BFwrk.Util.msgShow('Debe seleccionar una empresa hija', 'INFO');
	                        					return;
	                        				}else if(Ext.getCmp(PF + 'cmbTipoOperacion').getValue() === ''){
	                        					BFwrk.Util.msgShow('Es necesario seleccionar un tipo de operación', 'INFO');
	                        					return;
	                        				}else if(Ext.getCmp(PF + 'cmbTipoValor').getValue() === ''){
	                        					BFwrk.Util.msgShow('Es necesario seleccionar un tipo de valor', 'INFO');
	                        					return;
	                        				}else if(Ext.getCmp(PF + 'txtMonto').isVisible() && Ext.getCmp(PF + 'txtMonto').getValue() == ''){
	                        					BFwrk.Util.msgShow('Debe agregar un monto', 'INFO');
	                        					return;
	                        				}else if(Ext.getCmp(PF + 'cmbTipoValor').getValue() == '%'
	                        						&& Ext.getCmp(PF + 'txtMonto').getValue() > 100){
	                        					BFwrk.Util.msgShow('El valor del monto no puede ser mayor a 100', 'INFO');
	                        					return;
	                        				}else if(NS.iIdEmpresaPadre == NS.iIdEmpresaHijo){
	                        					BFwrk.Util.msgShow('La empresa no puede ser hija de si misma', 'INFO');
	                        					return;
	                        				}
	                        				uMonto = Ext.getCmp(PF + 'txtMonto').getValue();
	                        				BarridosFondeosAction.agregarNodosArbol(NS.cmbEmpresasHijo.isVisible(), NS.sRuta, parseInt(NS.idArbol),
	                        												parseInt(NS.iIdEmpresaHijo), parseFloat(uMonto), '', 
	                        												Ext.getCmp(PF + 'cmbTipoValor').getValue(), 
	                        												Ext.getCmp(PF + 'cmbTipoOperacion').getValue(), NS.iIdEmpresaPadre,function(response, e)
	                        				{
	                        					if(response !== null && response !== '' && response != undefined)
	                        					{
	                        						BFwrk.Util.msgShow('' + response, 'INFO');
	                        						NS.iIdEmpresaPadre = 0;
	                        						NS.accionarEmpresaHijo(NS.iIdEmpresaHijo, Ext.getCmp(PF + 'txtMonto').getValue());
	                        						NS.ocultar();
	                        					}
	                        				});
	                        			}
	                        			else if(NS.cmbNvaRaiz.isVisible())
	                        			{
	                        				BarridosFondeosAction.agregarNodosArbol(NS.cmbEmpresasHijo.isVisible(), NS.sRuta, parseInt(NS.iIdNvaEmpresaRaiz),
	                        												0, parseFloat(uMonto), Ext.getCmp(PF + 'txtDesc').getValue(), '', 0, NS.iIdEmpresaPadre,function(response, e)
	                        				{
	                        					if(response !== null && response !== '')
	                        					{
	                        						BFwrk.Util.msgShow('' + response, 'INFO');
	                        						if(response.indexOf('correct') > 0)
	                        						{
	                        							//NS.storeEmpresaRaiz.load();
														//NS.storeEmpresaRaiz.sort('descripcion','A-Z');
														//NS.accionarEmpresaRaiz(NS.iIdNvaEmpresaRaiz);
														//NS.iIdEmpresaRaiz = NS.iIdNvaEmpresaRaiz;
	                        							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeArbol, msg:"Cargando..."});
	                        							NS.storeArbol.load();
														NS.storeArbol.sort('descripcion','A-Z');
													 	Ext.getCmp(PF + 'txtNoArbol').setValue('');
													 	Ext.getCmp(PF + 'txtNoEmpresa').setValue('');
													 	NS.cmbArbol.reset();
													 	NS.cmbEmpresaRaiz.reset();
													 	//BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoArbol', NS.cmbArbol.getId());
													 	//BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresaRaiz.getId());
													 	NS.ocultar();
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
	                        text: 'Nuevo Árbol',
	                        x: 900,
	                        y: 240,
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
	                        			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeNvaRaiz, msg:"Cargando..."});
	                        			NS.storeNvaRaiz.load();
	                        			NS.storeNvaRaiz.sort('descripcion','A-Z');
	                        			NS.ocultar();
	                        			NS.cmbNvaRaiz.setVisible(true);
	                        			Ext.getCmp(PF + 'txtEmpresaNvaRaiz').setVisible(true);
	                        			Ext.getCmp(PF + 'lblInferiorUno').setVisible(true);
	                        			Ext.getCmp(PF + 'lblInferiorUno').setText('Nuevo');
										Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
										Ext.getCmp(PF + 'txtDesc').setVisible(true);
										Ext.getCmp(PF + 'lblInferiorDesc').setVisible(true);
										Ext.getCmp(PF + 'btnAgregarNodo').setDisabled(true);
										Ext.getCmp(PF + 'btnImprimir').setDisabled(true);
	                        			//Ext.getCmp(PF + 'fSetArbol').removeAll();
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Agregar Nodo',
	                        x: 900,
	                        y: 190,
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

	                        			var noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
	                        			if (noEmpresa == "") noEmpresa = 0;
	                        			NS.storeEmpresasHijo.baseParams.iEmpresaRaiz = noEmpresa;
	                        			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresasHijo, msg:"Cargando..."});
	                        			NS.storeEmpresasHijo.load();
	                        			NS.storeEmpresasHijo.sort('descripcion','A-Z');
	                        			NS.storeTipoOperacion.load();
	                        			NS.storeTipoOperacion.sort('descripcion','A-Z');
	                        			NS.ocultar();
	                        			Ext.getCmp(PF + 'lblInferiorUno').setVisible(true);
	                        			Ext.getCmp(PF + 'lblInferiorUno').setText('Nuevo:');
										Ext.getCmp(PF + 'txtEmpresaHija').setVisible(true);
										Ext.getCmp(PF + 'cmbEmpresasHijo').setVisible(true);
										Ext.getCmp(PF + 'cmbTipoOperacion').setVisible(true);
										Ext.getCmp(PF + 'lblInferiorDos').setVisible(true);
									//	Ext.getCmp(PF + 'txtMonto').setVisible(true);
										Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
										Ext.getCmp(PF + 'lblInferiorValor').setVisible(false);
										Ext.getCmp(PF + 'txtMonto').setVisible(false);
										Ext.getCmp(PF + 'cmbTipoValor').setVisible(true);
										Ext.getCmp(PF + 'lblInferiorValorAltaNodo').setVisible(true);
									//	Ext.getCmp(PF + 'lblInferiorValor').setVisible(true);
										Ext.getCmp(PF + 'txtDesc').setVisible(false);
										Ext.getCmp(PF + 'lblInferiorDesc').setVisible(false);

	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Eliminar',
	                        x: 900,
	                        y: 140,
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
	                        			else if(NS.iIdEmpActual <= 0 || NS.iIdEmpPadre <= 0 || NS.idArbol <= 0)
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
														BarridosFondeosAction.eliminarNodosArbol(parseInt(NS.idArbol), parseInt(NS.iIdEmpActual), 
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
	                        x: 900,
	                        y: 90,
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
	        useArrows: true,
	        autoScroll: true,
	        animate: true,
	        enableDD: true,
	        containerScroll: true,
	        bodyStyle: 'background: #5B5B5E',
	        boxMaxHeight:260,
	        height: 260,
	        border: false,
	        x: 0,
	        y: 0,
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
//					alert('selectedItem.parentNode.id ' + selectedItem.id);
					
					ruta += rutaNodos.substring(rutaNodos.indexOf('/Empresas/') + 10) 
					//alert('ruta: ' + ruta);	
					NS.sRuta = ruta;
					NS.iIdEmpresaPadre = selectedItem.id;
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