 Ext.onReady(function(){
	
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.PrestamosInterempresas.ArbolDeEmpresas');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	
	NS.iIdEmpresaPadre = 0;
	
	NS.limpiar = function()
	{
	
		 
		 
		NS.ArbolDeEmpresas.getForm().reset();
		
		NS.iIdEmpresaRaiz = 0;
		NS.iIdEmpresaPadre = 0;
		NS.iIdEmpresaHijo = 0;
		NS.iIdNvaEmpresaRaiz = 0;
		NS.sRuta = '';


		Ext.getCmp(PF + 'btnAgregarNodo').setDisabled(true),
		Ext.getCmp(PF + 'btnModificar').setDisabled(true),
		
		NS.iIdEmpActual = 0;
		NS.iIdEmpPadre = 0;
	};
	
	NS.ocultar = function(){
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
		Ext.getCmp(PF + 'txtVersion').setVisible(false);
		Ext.getCmp(PF + 'txtVersionE').setVisible(false);
		Ext.getCmp(PF + 'txtVersionM').setVisible(false);
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
		Ext.getCmp(PF + 'cmbTipoOperacion').reset();
		Ext.getCmp(PF + 'txtMonto').reset();
		
		Ext.getCmp(PF + 'cmbTipoValor').reset();

	};
	
	//store del combo cmbEmpresaRaiz
//angel 
	NS.storeEmpresaRaiz = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bExistentes : true,
		    idArbol : NS.idArbol
		},
		root: '',
		paramOrder:['bExistentes','idArbol'],
		directFn: BarridosFondeosAction.llenarCmbArbolHijosInterempresas, //angel rev
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
					Ext.Msg.alert("ALERT"+records.get('id'));
					NS.txtNoEmpresa.setValue("");
					
					 
					return;
				}
				Ext.getCmp(PF + 'txtNoEmpresa').setValue(records[0].get('id'));
				NS.cmbEmpresaRaiz.setValue(records[0].get('descripcion'));
			}
		}
	}); 
	

	
	
	//Store para hacer una consulta de puras empresas hijas y agregarselo a un combo invisible
	
	
	
	
	
	//store del combo cmbArbol una caja que tiene datos para pegar en el combo
	NS.storeArbol = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bExistentes : true
		},
		root: '',
		paramOrder:['bExistentes'],
		directFn: BarridosFondeosAction.llenarCmbArbolInterempresas, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeArbol, msg:"Cargando..."});
				
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
	NS.storeArbol.load();
	NS.storeArbol.sort('descripcion','A-Z');
	
	NS.accionarEmpresaRaiz = function(valueCombo)
	{
		BarridosFondeosAction.obtenerArbolEmpresaIn(parseInt(valueCombo),function(response, e)
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
	
<!--########################### Descomentar inicio ##########################################################-->	
  
  NS.cmbArbol = new Ext.form.ComboBox({ 
		 store: NS.storeArbol,
		name: PF + 'cmbArbol',
		id: PF + 'cmbArbol',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 250,
        y: 0,
        width: 300,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione la empresa raiz',
		triggerAction: 'all',
		value: '',
		listeners: 
		{
			select:
			{
				fn:function(combo, valor) 
				{
				
 					
				 	BFwrk.Util.updateComboToTextField(PF + 'txtNoArbol',NS.cmbArbol.getId());
				 	NS.accionarEmpresaRaiz(combo.getValue());
			        
				 	NS.iIdEmpresaRaiz = combo.getValue();
				 	NS.storeEmpresaRaiz.baseParams.idArbol = combo.getValue();
				 	NS.storeEmpresaRaiz.load();
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
   	 	x: 250,
        y: 30,
        width: 300,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione la  empresa hija',
		triggerAction: 'all',
		value: '',
		listeners:
		{
			select:
			{
			
				fn:function(combo, valor)  
				{
					var hijos='';
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa',NS.cmbEmpresaRaiz.getId());
					var sDesc = valor.get('id') + ":" + valor.get('descripcion');
					NS.txtVersion.setValue(sDesc);
					

				 	NS.accionarEmpresaHijo(combo.getValue());
				 	NS.iIdEmpresaHijo = combo.getValue();
				 	NS.storeEmpresaHijo.baseParams.idArbol = combo.getValue();
				 	NS.storeEmpresaHijo.load();
					
				}
			}
		}
	});   
	
	<!--########################### Descomentar Fin ##########################################################-->	

	<!-- %%%%%%%%%%%%%%%%%%%% CODIGO QUE HICE INICIO %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%-->
	
	      NS.txtVersion = new Ext.form.TextField({
	 	store: NS.storeEmpresaRaiz,
		id: PF + 'txtVersion',
        name: PF + 'txtVersion',
        x: 120,
        y: 60,
        width: 430, 
    	emptyText: 'Empresa  seleccionada',

	  }); 
	    
 
	      NS.txtVersionE = new Ext.form.TextField({
   	   		  id: PF + 'txtVersionE',
	          name: PF + 'txtVersionE',
	          x: 20,
	          y: 440,
	          width: 250,  
	          hidden:false,
	          emptyText: 'Empresa ',
           });
	      

	      NS.txtVersionM = new Ext.form.TextField({
   	   		  id: PF + 'txtVersionE',
	          name: PF + 'txtVersionE',
	          x: 350,
	          y: 440,
	          width: 250,  
	          hidden:true,
	          emptyText: 'monto  ',
           });
	      
	      
	      
	      
	    
          	 
	 /*	NS.obtenerReporte = function()
		{
			var strParams = '';
			
			strParams = '?nomReporte=ReporteArbolEstructura';	
			
			strParams += '&'+'noEmpresa=' + Ext.getCmp(PF + 'txtNoArbol').getValue();
			strParams += '&'+'idUsuario=' + NS.idUsuario;
			strParams += '&EMPRESA=' + NS.cmbArbol.getRawValue();
			 
			window.open("/SET/jsp/Reportes.jsp" + strParams);
		};
	   */
	<!-- %%%%%%%%%%%%%%%%%%%% CODIGO QUE HICE FIN  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%-->
	  
		  		  	//Funcion para agregar el nuevo nodo al arbol 
	NS.accionarEmpresaHijo = function(valueCombo, sMonto)
	{ 
		alert ("a"+valueCombo.getId());
		alert(NS.cmbEmpresasHijo.getId());
		 NS.txtVersionM.setValue(sMonto);
		var newNode =  new Ext.tree.TreeNode({
			id : valueCombo,
			 //regresa
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
   		if(selectedItem.isLeaf()) 
   		{
			selectedItem.leaf = false;
			selectedItem.appendChild([newNode]);
			var sDesc = (selectedItem.text).substring(0, 3);
			sDesc = sDesc + (selectedItem.text).substring((selectedItem.text).indexOf(":"), (selectedItem.text).lastIndexOf(":"));

			NS.txtVersion.setValue(sDesc);

         } else {//regresa
            selectedItem.insertBefore(newNode, selectedItem.nextSibling);            
    		var sDesc = (selectedItem.text).substring(0, 3);
    		sDesc = sDesc + (selectedItem.text).substring((selectedItem.text).indexOf(":"), (selectedItem.text).lastIndexOf(":"));

    		NS.txtVersion.setValue(sDesc);

         }
        $('.Seleccionado').removeClass('Seleccionado');                              
   		NS.treePanelMenu.getSelectionModel().unselect(selectedItem,true);
   		
		var sDesc = (selectedItem.text).substring(0, 3);
		sDesc = sDesc + (selectedItem.text).substring((selectedItem.text).indexOf(":"), (selectedItem.text).lastIndexOf(":"));

		NS.txtVersion.setValue(sDesc);

	};
	
 	 //angelStore para combo de empresas hijo
	 NS.storeEmpresasHijo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iEmpresaRaiz : 0
		},
		root: '',
		paramOrder:['iEmpresaRaiz'],
		directFn: BarridosFondeosAction.llenarCmbEmpresasHijoIn, 
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
	
	 NS.cmbEmpresasHijo = new Ext.form.ComboBox({//regresa2
	 	store: NS.storeEmpresasHijo,
		name: PF + 'cmbEmpresasHijo',
		id: PF + 'cmbEmpresasHijo',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
   	 	x: 220,
   	 	y: 440,
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
				 	NS.accionarEmpresaHijo(NS.iIdEmpresaHijo);
				}
			}
		}
	});
	
	
	 NS.accionarNvaRaiz = function(valueCombo)
	{
	
	}; 
	//angel  
 	 //Store para combo de una nueva raiz 
	 NS.storeNvaRaiz = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bExistentes : false
		},
		root: '',
		paramOrder:['bExistentes'],
		directFn: BarridosFondeosAction.llenarCmbEmpresaRaizInterempresas, 
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
	 //angeltipo
	
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
	
	//angel
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
   	 	y: 440,
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
   	 	y: 228,
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
   	 	y: 255,
        width: 220,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione tipo valor',
		triggerAction: 'all',
		value: '',
		hidden: true,
	});
   	 
   	<!--Fin del codigo de hoy _Israel -->
   	 
   	
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
   	 	y: 255,
        width: 220,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione tipo valor',
		triggerAction: 'all',
		value: '',
		 hidden: true,
	});
  
  	NS.obtenerReporte = function()
	{
		var strParams = ''; 
		
		strParams = '?nomReporte=ReporteArbolEstructura';	
		
		strParams += '&'+'noEmpresa=' + Ext.getCmp(PF + 'txtNoArbol').getValue();
		strParams += '&'+'idUsuario=' +NS.idUsuario;
		strParams += '&EMPRESA=' + NS.cmbArbol.getRawValue();
		
		
		window.open("/SET/jsp/Reportes.jsp" + strParams);
	};
	
	NS.validarDatos = function ()
	{
		
	};
	
	NS.ArbolDeEmpresas = new Ext.form.FormPanel({
    
	title: 'Arbol de Empresas',
    width: 859,
    height: 900,
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
	                title: '1',
	                width: 1050,
	                height: 655,
	                x: 10,
	                y: 10,
	                layout: 'absolute',
	                 id: 'fSetPrincipal',
	                items: [
	                	{
	                        xtype: 'label',
	                         text: 'Empresa Raíz',
	                        x: 1,
	                        y: 0
	                    },
	                    { 
	                        xtype: 'numberfield',
	                        x: 85,
	                        y: 0,
	                        width: 120,
	                        name: PF + 'txtNoArbol',
	                        id: PF + 'txtNoArbol',
	                        listeners:
                            {
                            	change: 
                            	{
                            		fn: function(box, value)
                            		{
                            		
                            			var valueCombo = '';
                            			if(box.getValue() !== ''){
                            				valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoArbol', NS.cmbArbol.getId());
                            			}if(valueCombo !== null && valueCombo !== '' && valueCombo !== undefined){
						 					NS.iIdEmpresaRaiz = valueCombo;
										 	BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoArbol', NS.cmbEmpresaRaiz.getId());
										 	Ext.getCmp(PF + 'txtNoEmpresa').setValue(valueCombo);
										 	NS.accionarEmpresaRaiz(NS.iIdEmpresaRaiz);//regresa
                            			}else
											BFwrk.Util.msgShow('No existe la empresa raiz [' + box.getValue() +']', 'INFO');
                            			
                            			NS.ocultar();
	                        			NS.limpiar();
	                        			NS.treePanelMenu.destroy();
	                        			 NS.iniciarArbol();
										
                            		}
                            	}
                            }
	                    },	            
	                     
	                     
	                	  
	                	  
	                	  NS.cmbArbol,
	                	  
   
	                    {
	                        xtype: 'label',
	                        text: 'Empresa Buscar:',
	                        x: 1,
	                        y: 30
	                    }, 
	                    
	              
	                    {
	                        xtype: 'numberfield',
	                        x: 85,
	                        y: 30,
	                        width: 120,
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
											NS.iIdEmpresaRaiz = valueCombo;
										 	BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbArbol.getId());
										 	Ext.getCmp(PF + 'txtNoArbol').setValue(valueCombo);
										 	NS.accionarEmpresaRaiz(NS.iIdEmpresaRaiz);
                            			}else
											BFwrk.Util.msgShow('No existe la empresa ' + box.getValue(), 'INFO');
										
                            		}
                            	}
                            }
	                     },

		                    {
		                        xtype: 'label',
		                        text: 'Nuevo monto:',
		                        x: 300,
		                        y: 440,
		                        id: PF + 'lMonto',
		                        hidden: true,
		                        
		                    },	            
		                     
	                     
	                     NS.cmbEmpresaRaiz,
	                    
	                    
	                    {
	                        xtype: 'label',
	                        text: 'Empresa Seleccionada:',
	                        x: 0,
	                        y: 60,
	                        
	                    },	            
	                     
	
	                     
	                    
	                     NS.txtVersion,//8
	                     NS.txtVersionE,
	                  
	             
	                
	                    
	                    
	                    { 
	                        xtype: 'fieldset',
	                        title: 'ddd',
	                        x: 0,
	                        y: 90,
	                        height: 500,
	                        layout: 'absolute',
	                        id: PF + 'fSetEmpresas',
	                        name: PF + 'fSetEmpresas',
	                        items: [ 
		                        { 
		                        	xtype: 'fieldset',
			                        title: 'angel3',
			                        height: 20,
			                        width:850,
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
	                                y: 440,
	                                id: PF + 'lblInferiorUno',
	                                hidden: true
	                            },
	                            {
	                                xtype: 'numberfield',
	                                x: 150,
	                                y: 440,
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
	                                y: 440,
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
	                                			alert("aquia");
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
	                                NS.cmbTipoOperacion,
                                {
	                                xtype: 'label',
	                                text: 'Tipo operación:',
	                                x: 460,
	                                y: 228,
	                                id: PF + 'lblInferiorDos',
	                                hidden: true
	                            },
                                {
	                                xtype: 'label',
	                                text: 'Descripción:',
	                                x: 460,
	                                y: 228,
	                                id: PF + 'lblInferiorDesc',
	                                hidden: true
	                            },
	                            { 
	                                xtype: 'textfield',
	                                x: 540,
	                                y: 228,
	                                value:'Arbol de empresas',
	                                width: 140,
	                                name: PF + 'txtDesc',
	                                id: PF + 'txtDesc',
	                                hidden: true,
	                            },{
	                                xtype: 'label',
	                                text: 'Tipo valor:',
	                                x: 100,
	                                y: 255,
	                                id: PF + 'lblInferiorValorAltaNodo',
	                                hidden: true
	                            },
	                            NS.cmbTipoValor,
	                            {
	                                xtype: 'label',
	                                text: 'monto:',
	                                x: 460,
	                                y: 440,
	                                id: PF + 'lblInferiorValor',
	                                hidden: true
	                            },
	                            
	                            
	                            {
	                                xtype: 'numberfield',
	                                x: 540,
	                                y: 440,
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
	                            },{
	                            	
	                            	
	                            	  
	  	                                xtype: 'numberfield',
	  	                                x: 440,
	  	                                y: 324,
	  	                                width: 140,
	  	                                name: PF + 'txtMonto1',
	  	                                id: PF + 'txtMonto1',
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
	                        y: 250,
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
	                        y: 200,
	                        width: 90,
	                        id: PF + 'btnEjecutar',
	                        name: PF + 'btnEjecutar',
	                         disabled: true,
	                        listeners:{
	                        	click:{
	                        		  fn: function(e){
	                        			//var  lol = $('.nvoAgregado')
	                        	 		//alert(lol)
	                        	 		var uMonto = 1; 
	                        	 		//
	                        			if(NS.cmbEmpresasHijo.isVisible()){
	                            			uMonto = Ext.getCmp(PF + 'txtMonto').getValue();
	                          				 NS.sRuta=Ext.getCmp(PF + 'txtNoArbol').getValue();
	                    				 	//alert(NS.sRuta+"Angel Ruta padre");
	                    				 	 //alert(Ext.getCmp(PF + 'cmbEmpresaRaiz').getValue());
	                    				 //	 alert(Ext.getCmp(PF + 'txtNoArbol').getValue());
	                    					var auxIdArbol=Ext.getCmp(PF + 'txtNoArbol').getValue();
												
												
												if(NS.sRuta === ''){
													BFwrk.Util.msgShow('Es necesario seleccionar la Empresa Padre', 'INFO');
													return;
												}else if(NS.iIdEmpresaHijo <= 0){
		                        					BFwrk.Util.msgShow('Debe seleccionar una empresa hija', 'INFO');
		                        					return;
		                        				}else /*if(Ext.getCmp(PF + 'cmbTipoOperacion').getValue() === ''){
		                        					BFwrk.Util.msgShow('Es necesario seleccionar un tipo de operación', 'INFO');
		                        					return;
		                        				}else if(Ext.getCmp(PF + 'cmbTipoValor').getValue() === ''){
		                        					BFwrk.Util.msgShow('Es necesario seleccionar un tipo de valor', 'INFO');
		                        					return;
		                        				}else */if(Ext.getCmp(PF + 'txtMonto').isVisible() && Ext.getCmp(PF + 'txtMonto').getValue() <-1 ){
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
									        
                       					//alert("6fuera del if");
                            			 uMonto = Ext.getCmp(PF + 'txtMonto').getValue();
                          				 NS.sRuta=Ext.getCmp(PF + 'txtNoArbol').getValue();
                    			var auxIdArbol=Ext.getCmp(PF + 'txtNoArbol').getValue();
                    					var auxMonto=0.0;
   
                    						BarridosFondeosAction.agregarNodosArbolIn(
                 							NS.cmbEmpresasHijo.isVisible(),
       									 '' + NS.sRuta,
       									 parseInt(auxIdArbol),
       	                        				
       									 parseInt(NS.iIdEmpresaHijo),
       									 parseFloat(uMonto),
       									 "arbol", 
       	                        					//Ext.getCmp(PF + 'cmbTipoValor').getValue(), 
       	                        			1,		//parseInt(Ext.getCmp(PF + 'cmbTipoOperacion').getValue()),
       									 parseInt(NS.iIdEmpresaPadre),
                 							
								        		/*NS.cmbEmpresasHijo.isVisible(),//trae los datos de las empresas hijas para hacerlas padres
												NS.sRuta,
												parseInt(NS.iIdEmpresaHijo),
												parseInt(NS.idArbol),//es una variable de arriba que trae un monto
												parseFloat(uMonto), 
												"arbolAngel",
												//Ext.getCmp(PF + 'txtDesc').getValue(),//es una caja de texto que sra llenada con datos en la interfazangelR 
											 	//"a", //recibe una cadena
								 				1,//le mando un cero
								 				parseInt(NS.iIdEmpresaPadre),//es una variable con un id del padre */
												    				function (response, e){
									    					if(response !== null && response !== '' && response != undefined){
									    						
									    						NS.ocultar();
									    						NS.storeArbol.load();
                    											NS.storeNvaRaiz.load();
																NS.storeEmpresaRaiz.load();
																NS.iniciarArbol();
																
									    						BFwrk.Util.msgShow('' + response, 'INFO');
									    						NS.iIdEmpresaPadre = 0; 
									    						NS.accionarEmpresaHijo(NS.iIdEmpresaHijo, Ext.getCmp(PF + 'txtMonto').getValue());
									    					//	NS.accionarEmpresaHijo(NS.iIdNvaEmpresaRaiz);//la variable NS.AcionarEmRaiz tiene el id o valor del NS.cmbNvaRaiz
																
									    						var selectedItem = NS.treePanelMenu.getSelectionModel().getSelectedNode();
                       			  								selectedItem.load();
									    						
									    					}
								        		}
								        		);
								                        				 
                 					
	                        				 
	                        									}else  
							                        				if(NS.cmbNvaRaiz.isVisible())	{
							                        					//funcion 2 de ejecutar por si no se llena en nodo se llena en padre
							                        					BarridosFondeosAction.agregarNodosArbolIn(
							                        						NS.cmbEmpresasHijo.isVisible(),//trae los datos de las empresas hijas para hacerlas padres
							                        						NS.sRuta, //rutaA
	                        														parseInt(NS.iIdNvaEmpresaRaiz),//cadenaA
	                        														0,
			                        												parseFloat(uMonto),//es una variable de arriba que trae un monto
			                        												//Ext.getCmp(PF + 'txtDesc').getValue(),//es una caja de texto que sra llenada con datos en la interfazangelR 
			                        												'',//recibe una cadena
			                        								 				0,//le mando un cero
			                        												NS.iIdEmpresaPadre,//es una variable con un id del padre 
			                        												
			                        												
			                        												function(response, e){
											                        					if(response !== null && response !== ''){
											                        						  
											                        						BFwrk.Util.msgShow('' + response, 'INFO');
											                        						if(response.indexOf('correct') > 0){
											                        							NS.storeArbol.load();//cargo el primer combo
																								NS.storeArbol.sort('descripcion','A-Z');
																							
																								Ext.getCmp(PF + 'txtNoArbol').setValue( NS.iIdNvaEmpresaRaiz);//a la primera caja de la interfaz que es la del arbol  le pongo la clave de una variable que tiene el id del padre
																							 	Ext.getCmp(PF + 'txtNoEmpresa').setValue( NS.iIdNvaEmpresaRaiz);//a la segunda caja de la interfaz que es la del las hijas  le pongo la clave de una variable que tiene el id del padre
																							 	
																							 	//BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoArbol', NS.cmbArbol.getId());//le actualizo ala caja el ID que tiene el combo arbol  que tiene el id del arbol padre
																							 	BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresaRaiz.getId());//le actualizo ala caja el ID que tiene el ID de las empresas hijas 
																							 	
																							 	NS.accionarEmpresaRaiz(NS.iIdNvaEmpresaRaiz);//la variable NS.AcionarEmRaiz tiene el id o valor del NS.cmbNvaRaiz
																								NS.iIdEmpresaRaiz = NS.cmbNvaRaiz.getValue();//dos variable con el mismo nombre tienen el mismo valor
																							 	
																								NS.storeEmpresaRaiz.baseParams.idArbol = NS.iIdEmpresaRaiz;
																								
																							   NS.storeEmpresaRaiz.load();
																							   var valor=NS.cmbNvaRaiz.getValue();
																							alert(valor);
																							 			 	
																							 	NS.ocultar();
																							 	
											                        						}
											                        					}
											                        				});
	                        													
	                        											}
													                        		}
													                        	}
													                        }
													                    },
	                    {
	                        xtype: 'button',
	                        text: 'Nuevo árbol',
	                        x: 900,
	                        y: 100,
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
	                        			//NS.iniciarArbol();//solo es una funcion para iniciar el panel con las empresas	
	                        			//NS.ocultar();
	                        			
	                        			 
	                        			
	                        			/*NS.storeArbol.load();
	                        			NS.storeArbol.sort('descripcion','A-Z');
	                        			NS.iniciarArbol();
	                        			NS.storeEmpresaRaiz.load();
	                        			NS.storeEmpresaRaiz.sort('descripcion','A-Z');*/
	                        			 
	                        			NS.storeArbol.load();
	                        		            	NS.storeNvaRaiz.load();//llena este store con las empresas que no son padres y que no tienen hijos
	                        	
	                            			//pone que se vea en lapantalla el combo con las nuevas raices que esta lleno
	                        			           NS.cmbNvaRaiz.setVisible(true);
	                        				//de NS.cmbNvaRaiz que tiene cargado datos de NS.storeNvaRaiz hace una consulta con los hijos.
		                        		
	                        			
	                        			Ext.getCmp(PF + 'txtEmpresaNvaRaiz').setVisible(true);//botonR
	                        			Ext.getCmp(PF + 'lblInferiorUno').setVisible(true);
	                        			Ext.getCmp(PF + 'lblInferiorUno').setText('Nuevo');
										Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
										//Ext.getCmp(PF + 'txtDesc').setVisible(true	);
										//Ext.getCmp(PF + 'lblInferiorDesc').setVisible(true);
										
	                        			Ext.getCmp(PF + 'fSetArbol').removeAll();
	                        			 //angelf
	                        		
	                			        
	                        			
	                        		}
	                        	}
	                        }
	                    },{
	                    	
	                    	
	                        xtype: 'button',
	                        text: 'Modificar',
	                        x: 900,
	                        y: 400, 
	                        width: 90,
	                        id: PF + 'btnModificar', 
	                        disabled: true,
	                        listeners:
	                        {
	                        	click:
	                        	{ 
	                        		fn: function(btn)
	                        		{ 


  	                        			 var a=NS.txtVersion.getValue();
  	                        			 Ext.getCmp(PF + 'lMonto').setVisible(true);			
  	                                     Ext.getCmp(PF + 'txtVersionE').setVisible(true);
  	                                   Ext.getCmp(PF + 'txtMonto1').setVisible(true);	
  	      	                    	             
  	                                     Ext.getCmp(PF + 'btnMontoN').setVisible(true);	
  	                    	 
  	                    
  	          						
	    	                        	

	                        			
	                        		}
	                        	}
	                        }
	                    	
	                    	
	                    	
	                    },{
	                        xtype: 'button',
	                        text: 'Actualizar Monto',
	                        x: 900,
	                        y: 440,
	                        width: 90,
	                        id: PF + 'btnMontoN',
	                        name: PF + 'btnMontoN',
	                        disabled: false,
	                       
	                        listeners:
	                        {
	                         	click:
	                        	{
	                        		fn: function(e)
	                        		{	
	                        		 
	                        			
	                        		var idRaiz= Ext.getCmp(PF + 'txtNoArbol').getValue();	
  	          						var idEmpresa =Ext.getCmp(PF + 'txtNoEmpresa').getValue();	
  	          						var monto= 	Ext.getCmp(PF + 'txtMonto1').getValue()
	                        			
  	          						parseInt(monto);
  	          					parseInt(idEmpresa);
  	          				parseInt(idRaiz);
  	          						
	                        			alert("Monto\n"+monto);

                        			alert("Empresa\n"+idEmpresa);

                        			alert("idRaiz\n"+idRaiz);
  	          						
			    						//var selectedItem = NS.treePanelMenu.getSelectionModel().getSelectedNode();
	                        			//BarridosFondeosAction.agregarNodosArbolIn
	                        			
  	                        			alert("La empresa a modificar es:\nNombre:  "+NS.txtVersion.getValue());

	                        			
	                        			
	                        	BarridosFondeosAction.actualizarMonto(monto, idEmpresa, idRaiz);
	                        			
	                        	NS.accionarEmpresaRaiz(idRaiz);
	        			        
	        				 	NS.iIdEmpresaRaiz = idRaiz;
	        				 	NS.storeEmpresaRaiz.baseParams.idArbol = idRaiz;
	        				 	NS.storeEmpresaRaiz.load();
	                        			
	                        		}
	                        	}
	                        }
	                    
	                    	
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Agregar Nodo',
	                        x: 900,
	                        y: 150,
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
	                        			
	                        		
	                        		
	                        			 NS.storeEmpresasHijo.load();
	                        			 var noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
	                        			if (noEmpresa == "") noEmpresa = 0;
	                        			NS.storeEmpresasHijo.baseParams.iEmpresaRaiz = noEmpresa;
	                        			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresasHijo, msg:"Cargando..."});
	                        			NS.storeEmpresasHijo.load();
	                        			NS.storeEmpresasHijo.sort('descripcion','A-Z');
	                        				//llena este store con las empresas que no son padres y que no tienen hijos
	                        	
	                            			//pone que se vea en lapantalla el combo con las nuevas raices que esta lleno
	                        			           
	                        				//de NS.cmbNvaRaiz que tiene cargado datos de NS.storeNvaRaiz hace una consulta con los hijos.
		                        		
  	                        			
 	                        			        Ext.getCmp(PF + 'lblInferiorUno').setVisible(true);
 	                        			        Ext.getCmp(PF + 'lblInferiorUno').setText('Nuevo');
 
	       										
	       										Ext.getCmp(PF + 'txtEmpresaHija').setVisible(true);
	       										Ext.getCmp(PF + 'cmbEmpresasHijo').setVisible(true);
	       										//Ext.getCmp(PF + 'txtDesc').setVisible(true	);
	    										//Ext.getCmp(PF + 'lblInferiorDesc').setVisible(true);
	    										           
	    										Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
										//Ext.getCmp(PF + 'txtDesc').setVisible(false	);
									//	Ext.getCmp(PF + 'lblInferiorDesc').setVisible(true);
										
	                        			//Ext.getCmp(PF + 'fSetArbol').removeAll();
	    									
	    										Ext.getCmp(PF + 'lblInferiorValor').setVisible(true);
	    										Ext.getCmp(PF + 'txtMonto').setVisible(true);
	    								
	    	                        	NS.iniciarArbol();
	    	                        	NS.treePanelMenu.setRootNode(NS.rootTreeNodeMenu);	
	    	            		        NS.treePanelMenu.render(PF + 'fSetArbol');	
	    	            		        
	    	            		        NS.rootTreeNodeMenu.expand();
	    	            		        
	    	                        	/*Ext.getCmp(PF + 'lblInferiorUno').setVisible(true);
	                        			Ext.getCmp(PF + 'lblInferiorUno').setText('Nuevo');
										Ext.getCmp(PF + 'txtEmpresaHija').setVisible(true);
										Ext.getCmp(PF + 'cmbEmpresasHijo').setVisible(true);
										Ext.getCmp(PF + 'cmbTipoOperacion').setVisible(true);
										Ext.getCmp(PF + 'lblInferiorDos').setVisible(true);
										Ext.getCmp(PF + 'txtMonto').setVisible(true);
										Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);

										Ext.getCmp(PF + 'cmbTipoValor').setVisible(true);
										Ext.getCmp(PF + 'lblInferiorValorAltaNodo').setVisible(true);
										Ext.getCmp(PF + 'lblInferiorValor').setVisible(true);
										Ext.getCmp(PF + 'txtDesc').setVisible(false);
										Ext.getCmp(PF + 'lblInferiorDesc').setVisible(false);*/

	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Eliminar',
	                        x: 900,
	                        y: 350,
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
														BarridosFondeosAction.eliminarNodosArbolIn(parseInt(NS.iIdEmpresaRaiz), parseInt(NS.iIdEmpActual), 
                        																	parseInt(NS.iIdEmpPadre),function(response, e)
				                        				{
				                        					if(response !== null && response !== '')
				                        					{
				                        						BFwrk.Util.msgShow('' + response, 'INFO');
				                        						if(response.indexOf('no') < 0)
				                        						{
				                        							var selectedItem = NS.treePanelMenu.getSelectionModel().getSelectedNode();
	                       			  								selectedItem.remove(true);
	                       			  								NS.txtVersion.setValue='';
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
	                        y: 300,
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
	      
	        animate: true,
	        enableDD: true,
	        containerScroll: true,
	        bodyStyle: 'background: #5B5B5E',
	        border: false,
	        x: 100,
	        y: 10,
			loader: new Ext.tree.TreeLoader(),
			  autoScroll: true,
			listeners: 
			{
	           expand: function(pan, adjWidth, adjHeight, rawWidth, rawHeight)
	           {
 		            alert('TreePanel resize: '+'w='+adjWidth+' h='+adjHeight+' - '+'rw='+rawWidth+' rh='+rawHeight);
		            var el = pan.getEl();
		            alert('Elemento ' + el);
					if(el==null) return;
		                 var childEl = el.first();
	        	},
	        	click: function(node)
	        	{	
	   				NS.treePanelMenu.getSelectionModel().clearSelections(true);
	   				$('.Seleccionado').removeClass('Seleccionado');
	   				//alert(node.qtipCfg)
		        	NS.treePanelMenu.getSelectionModel().select(node);
		        	node.setCls('Seleccionado');
		        	
		        	NS.treePanelMenu.getSelectionModel().select(node);
		        	var selectedItem = NS.treePanelMenu.getSelectionModel().getSelectedNode();
		        	
		        	var rutaNodos = '';
					var ruta = '';
					rutaNodos = selectedItem.getPath();
					
					NS.iIdEmpActual = selectedItem.id;
					
					if(selectedItem.parentNode === null)
						NS.iIdEmpPadre = 0;
					
					else
						NS.iIdEmpPadre = selectedItem.parentNode.id;	

if(NS.iIdEmpPadre!=''){
	
	Ext.getCmp(PF + 'btnModificar').setDisabled(false);
	
}
					
					ruta += rutaNodos.substring(rutaNodos.indexOf('/Empresas/') + 20) 
 					NS.sRuta = ruta;
					NS.iIdEmpresaPadre = selectedItem.id;
					
					var sDesc = (selectedItem.text).substring(0, 3);
					sDesc += (selectedItem.text).substring((selectedItem.text).indexOf(":"), (selectedItem.text).lastIndexOf(":"));
					
				
					NS.txtVersion.setValue(sDesc);
					NS.txtVersionE.setValue(sDesc);
									
				    
					/*
					 * 					if((rutaNodos.indexOf('xnode')) < 0)
					{
				 		//Para conocer el por que de los índices, ver el getPath('...xnode...').
						ruta = rutaNodos.substring(1,rutaNodos.indexOf('xnode-') - 1);
						ruta += rutaNodos.substring(rutaNodos.indexOf('xnode-') + 9) 
						alert('ruta: ' + ruta+"angel 1");	//ruta
						NS.sRuta = ruta;
						alert('ruta: ' + ruta+"angel 2");	//ruta
						
					}/*Se hacia esto para sacar la ruta, cuando desde la estructura del json en java, no se le asinaba id 
					al padre y por default ponia un xnode-566
					alert('ruta: ' + ruta);

					 * 
					 * */
	        	}
	        }
	    });
 	};
    
    NS.limpiar();
    NS.iniciarArbol();
	NS.ArbolDeEmpresas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});