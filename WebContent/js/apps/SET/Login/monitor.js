var timeOut;

Ext.onReady(function() {
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Login.Monitor');
	Ext.Ajax.timeout = 200000;
	Ext.chart.Chart.CHART_URL = '/SET/ext-3.3.0/resources/charts.swf';

	NS.sUserLogin = sUserLogin;
	NS.iUserId = iUserId;
	NS.APELLIDO_MATERNO = '';
	NS.APELLIDO_PATERNO = '';
	NS.NOMBRE = '';
	NS.NO_EMPRESA = 0;
	NS.NOM_EMPRESA = '';
	NS.ID_CAJA = 0;
	NS.DESC_CAJA = '';
	NS.NO_CUENTA_EMP = 0;
	NS.HOST_NAME_LOCAL = HOST_NAME_LOCAL;

	NS.FEC_HOY = '';
	NS.sCodSession = sCodSession;
	NS.tabContId = apps.SET.tabContainerId;
	
	var Tree = Ext.tree;
	
	var alto =screen.height;
	alto = (alto -(alto* 0.37))/2;
	
	NS.divisa='MN';
	NS.tipoGrafica='';
	NS.titulo;
	NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Procesando ..."});
	
	verificaComponentesCreados();
	
	/*******************************
	 * COMPONENTES
	 */

	NS.lblIndicadores = new Ext.form.Label({
		text: 'Indicadores Bancarios:',
		style: {
			  color: 'white', 
		}
	});
	
	NS.radioDivisa = new Ext.form.RadioGroup({
		id: PF + 'radioDivisa',
		name: PF + 'radioDivisa',
		columns: 1, 
		items: [
	          {
		           boxLabel: 'PESOS',
		           id: PF + 'optPesos', 
		           name: PF+'radioDivisa', 
		           inputValue: 0, 
		           checked: true ,
		           listeners: {
			 			check: {
			 				fn: function(opt, valor) {
			 					if (valor == true){	
			 						NS.divisa = "MN";
			 						NS.cargarTodo();
			 					}	 					
			 				}
			 			}
			 		}	
	           },  
	          {
	        	   boxLabel: 'DOLARES',
	        	   id: PF + 'optDolares',
	        	   name: PF+'radioDivisa',
	        	   inputValue: 1,
	        	   listeners: {
			 			check: {
			 				fn: function(opt, valor) {
			 					if (valor == true){		 	
			 						NS.divisa = "DLS";
			 						NS.cargarTodo();
			 					}	 					
			 				}
			 			}
			 		}
	          }
	          //Agregar mas divisas.
	     ]
	});
	
	/*************************************
		FUNCIONES
	 **************************************/

	function getAttention(window) {
		window.focus(); 
		var i = 0;
		var show = ['Evite cierre de sesión', window.document.title];
		var focusTimer = setInterval(function() {
			if (window.closed) {
				clearInterval(focusTimer);
				return;
			}
			window.document.title = show[i++ % 2] ;
		}, 1000);
	
		window.document.onmousemove = function() { 
			clearInterval(focusTimer); 
			window.document.title = show[1] ;
			window.document.onmousemove = null ;
		} 
	}
	
	cerrarSession = function(tipoMsg) {
		if(tipoMsg)
			alert('Lo sentimos su sesión a expirado, tiene que volver a registrarse en el sistema');
		
		GlobalAction.quitarUsuarioActivo(function(result, e) {
			Ext.Msg.alert('SET', 'Sesión Terminada');
			var redirect = 'login.jsp';
			window.location = redirect;
		});
	};
	
	alerta = function() {
		//llamamos la ventana emergente
		getAttention(window);
		//Se le manda una alerta al usuario avisando que se cerrara su sesión en 2 min esto en milisegundos
		timeOut = window.setTimeout("cerrarSession(true)", 120000);
		Ext.Msg.alert('SET', 'La sesión expirara por inactividad en 2 minutos, para evitarlo de un click en cualquier opción del menu');
	};
	
	function verificaComponentesCreados(){
		var compt; 	
		
		compt = Ext.getCmp(PF + 'graficaBarras');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'graficaPastel');
		if(compt != null || compt != undefined ){ compt.destroy(); }
	}
	
	/*****************
	 * INDICADORES BANCARIOS
	 */
	
	NS.indicadoresBancarios = function() {
		MonitorAction.consultaIndicadoresBancarios(function(result, e) {
			if(!result) {
				//Ext.Msg.alert('SET','Error en acceso al menu desde la base de datos.');
				NS.lblIndicadores.setText('Error al acceder a la base de datos');
			}else if(result=='' || result == undefined || result == null)
				NS.lblIndicadores.setText('No se encontraron indicadores bancarios');
			else 
				NS.lblIndicadores.setText(result);
		}); 
	};
	
	/******************************************************
	 * GRAFICA
	 * FUNCIONES
	 */
	
	NS.graficar = function(tipoGr) {
		NS.tipoGrafica=tipoGr;
		if(tipoGr == 'CAJA'){
			NS.storeGraficaBarras.baseParams.tipoGrafica=NS.tipoGrafica;
			NS.storeGraficaBarras.baseParams.divisa=NS.divisa;
			NS.storeGraficaBarras.load();
			NS.panelGraficaPastel.hide();
			NS.panelGraficaBarras.show();
		}else{
			NS.storeGraficaPastel.baseParams.tipoGrafica=NS.tipoGrafica;
			NS.storeGraficaPastel.baseParams.divisa=NS.divisa;
			NS.storeGraficaPastel.load();
			NS.panelGraficaBarras.hide();
			NS.panelGraficaPastel.show();
		}
	};
	
	NS.storeGraficaBarras = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {tipoGrafica: '' ,divisa: ''},
		paramOrder: ['tipoGrafica', 'divisa'],
		directFn: MonitorAction.consultaGrafica,
		fields: [
		    {name: 'descripcion'}, 
		 	{name: 'total'}, 
		],
		listeners: {
			load: function(s, records) {
				NS.myMask.hide();
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existen datos para graficar');
					NS.winGrafica.hide();
				}else{
					NS.winGrafica.show();
				}
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
				NS.winGrafica.hide();
			}
		}
	});
	
	NS.storeGraficaPastel = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {tipoGrafica: '' ,divisa: ''},
		paramOrder: ['tipoGrafica', 'divisa'],
		directFn: MonitorAction.consultaGrafica,
		
		fields: [
		    {name: 'descripcion'}, 
		 	{name: 'total'}, 
		],
		listeners: {
			load: function(s, records) {
				NS.myMask.hide();
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existen datos para graficar');
					NS.winGrafica.hide();
				}else{
					NS.winGrafica.show();
				}
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
				NS.winGrafica.hide();
			}
		}	
	});
	
	NS.panelGraficaBarras = new Ext.Panel({
        height: '90%',
        id: PF + 'graficaBarras',
        hidden:true,
         items: {
            store: NS.storeGraficaBarras,
        	xtype: 'stackedbarchart',
            yField:'descripcion',
            xField:'total',
        }
    });
	
	NS.panelGraficaPastel = new Ext.Panel({
       // title: 'Grafica :' +NS.titulo,
        height: '90%',
        id: PF + 'graficaPastel',
        hidden:true,
         items: {
            store: NS.storeGraficaPastel,
        	xtype: 'piechart',
            dataField: 'total',
            categoryField: 'descripcion',
            extraStyle:{
                legend:{
                    display: 'bottom',
                    padding: 5,
                    font:{
                        family: 'Tahoma',
                        size: 13
                    }
                }
            }
        }
    });
	
	/****************Carga de arboles ****************************/
	
	NS.cargarArbol= function(opt) {
		MonitorAction.obtenerArbol(NS.divisa,opt , function(result, e) {
			if(!result) {
				Ext.Msg.alert('SET','Error en acceso al menu desde la base de datos.');
				NS.myMask.hide();
				return;
			}
			if(result == '') {
				NS.myMask.hide();
				return;
			}
			
			var jsonArbol = Ext.util.JSON.decode(result);
			
			NS.rootTreeNode = new Ext.tree.AsyncTreeNode({
				text: 'Total',
	    		draggable: false,
	    		id: 'source',
	    		expanded: true,
	    		uiProvider: false,
	    		children: jsonArbol
	    		
		    });
			
			if(opt=='CAJA')
				 NS.panelArbolCaja.setRootNode(NS.rootTreeNode);
			
			if(opt=='INVERSION')
				NS.panelArbolInversion.setRootNode(NS.rootTreeNode);
			
			if(opt=='DEUDAS')
				NS.panelArbolDeudas.setRootNode(NS.rootTreeNode);
			
			if(opt=='CONCILIACIONES')
				NS.panelArbolConciliaciones.setRootNode(NS.rootTreeNode);
			
			if(opt=='COMPARATIVO')
				NS.panelArbolComparativo.setRootNode(NS.rootTreeNode);
			 NS.myMask.hide();
		}); 
		
	};
	
	NS.cargarTodo = function(){
		NS.cargarArbol('CAJA');
	    NS.myMask.show();
	    NS.cargarArbol('INVERSION');
	    NS.myMask.show();
	    NS.cargarArbol('DEUDAS');
	    NS.myMask.show();
	    NS.cargarArbol('CONCILIACIONES');
	    NS.myMask.show();
	    NS.cargarArbol('COMPARATIVO');
	    NS.indicadoresBancarios();
	}
	
	NS.cargarTodo();
	
	/***************fin carga de arboles********************/
	

	/**********************************************************************
	Contenedor
	**********************************************************************/
	
	/***********Arboles */
    NS.panelArbolCaja = new Ext.tree.TreePanel({
    	iconCls: 'menu_seguridad',
        id: PF + 'panelArbolCaja',
        height: alto,
        autoScroll:true, //, frame:false //, border:false //, lines:true
        bodyStyle:'background-color:#5B5B5E;',
        rootVisible: false,
		loader: new Tree.TreeLoader(),
		lines: true,
		selModel: new Ext.tree.MultiSelectionModel(),
	        containerScroll: false
	 });
    
    var raizArbolCaja = new Tree.AsyncTreeNode({
        text: 'Total',
        draggable:false,
        id:'source',
        children: {}
    });

    NS.panelArbolCaja.setRootNode(raizArbolCaja);
    
    NS.panelArbolInversion = new Ext.tree.TreePanel({
    	bufferResize: true,
    	iconCls: 'menu_seguridad',
        id: PF + 'panelArbolInversion',
        height: alto,
        autoScroll:true, //, frame:false //, border:false //, lines:true
        bodyStyle:'background-color:#5B5B5E;',
        rootVisible: false,
		loader: new Tree.TreeLoader(),
		lines: true,
		selModel: new Ext.tree.MultiSelectionModel(),
	        containerScroll: false
	 });
    
   var raizArbolInversion = new Tree.AsyncTreeNode({
        text: 'Total',
        draggable:false,
        id:'source',
        children: {}
    });

    NS.panelArbolInversion.setRootNode(raizArbolInversion);
    
    NS.panelArbolDeudas = new Ext.tree.TreePanel({
    	bufferResize: true,
    	iconCls: 'menu_seguridad',
        id: PF + 'panelArbolDeudas',
        height: alto,
        autoScroll:true, //, frame:false //, border:false //, lines:true
        bodyStyle:'background-color:#5B5B5E;',
        rootVisible: false,
		loader: new Tree.TreeLoader(),
		lines: true,
		selModel: new Ext.tree.MultiSelectionModel(),
	        containerScroll: false
	 });
    
    var raizArbolDeudas = new Tree.AsyncTreeNode({
        text: 'Total',
        draggable:false,
        id:'source',
        children: {}
    });

    NS.panelArbolDeudas.setRootNode(raizArbolDeudas);
    
    NS.panelArbolConciliaciones = new Ext.tree.TreePanel({
    	bufferResize: true,
    	iconCls: 'menu_seguridad',
        id: PF + 'panelArbolConciliaciones',
        height: alto,
        autoScroll:true, //, frame:false //, border:false //, lines:true
        bodyStyle:'background-color:#5B5B5E;',
        rootVisible: false,
		loader: new Tree.TreeLoader(),
		lines: true,
		selModel: new Ext.tree.MultiSelectionModel(),
	        containerScroll: false
	 });
    
    var raizArbolConciliaciones = new Tree.AsyncTreeNode({
        text: 'Total',
        draggable:false,
        id:'source',
        children: {}
    });

    NS.panelArbolConciliaciones.setRootNode(raizArbolConciliaciones);
    
    NS.panelArbolComparativo = new Ext.tree.TreePanel({
    	bufferResize: true,
    	iconCls: 'menu_seguridad',
        id: PF + 'panelGridComparativo',
        height: alto,
        autoScroll:true, //, frame:false //, border:false //, lines:true
        bodyStyle:'background-color:#5B5B5E;',
        rootVisible: false,
		loader: new Tree.TreeLoader(),
		lines: true,
		selModel: new Ext.tree.MultiSelectionModel(),
	        containerScroll: false
	 });
    
    var raizArbolComparativo = new Tree.AsyncTreeNode({
        text: 'Total',
        draggable:false,
        id:'source',
        children: {}
    });

    NS.panelArbolComparativo.setRootNode(raizArbolComparativo);
    
    /*******Grids**************/
    
    
    NS.storeGridDiversos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {divisa:NS.divisa},
		paramOrder: ['divisa'],
		directFn: MonitorAction.consultaGridDiversos,
		fields: [
		    {name: 'descripcion'},
		 	{name: 'total'},
		],
		listeners: {
			load: function(s, records) {
				NS.myMask.hide();
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No existen movimientos');}
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}	
	})
    NS.myMask.show();
    NS.storeGridDiversos.load();
    
	NS.columnaSeleccion= new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	NS.columnas= new Ext.grid.ColumnModel([
	  {header: 'Diversos', width: 120, dataIndex: 'descripcion', sortable: true },
	  {header: ' ', width: 100, dataIndex: 'total', sortable: false }
	]);
			
	 NS.panelGridDiversos = new Ext.grid.GridPanel({
		store: NS.storeGridDiversos,
		id: PF + 'gridDiversos',
		name: PF + 'gridDiversos',
		cm: NS.columnas,
		sm: NS.columnaSeleccion,
		height: alto,
		stripRows: true,
		columnLines: true,
		autoScroll: true,
		bodyStyle:'background-color:#5B5B5E;'
	});
    
	/***************Sub-paneles de panel central***/
	
	NS.panelPcaja = new Ext.Panel({
		id: 'panelPcaja',
		title: 'Posición de caja',
		layout: 'fit',
		columnWidth : .33,
	    autoScroll: false,
	    bufferResize: true,
	    collapsible: true,
		collapsed: false,
		margins:'0 0 0 0',
		bodyStyle: 'background:#A3BD31',
		items: [
		        NS.panelArbolCaja
		]
	});
	
	NS.panelPInversion = new Ext.Panel({
		id: 'panelPInversion',
		title: 'Posición de inversion',
		layout: 'fit',
		autoScroll: true,  
		columnWidth : .34,
		bufferResize: true,
		collapsible: true,
		collapsed: false,
		margins:'0 0 0 0',
		bodyStyle: 'background:#A3BD31',
		items: [
		        //NS.tree     
		        NS.panelArbolInversion
		]
	});
	
	NS.panelPDeuda = new Ext.Panel({
		id: 'panelPDeuda',
		title: 'Posición de deuda',
		layout: 'fit',
		columnWidth : .33,
		autoScroll: true,
		bufferResize: true,
		collapsible: true,
		collapsed: false,
		margins:'0 0 0 0',
		bodyStyle: 'background:#A3BD31',
		items: [
		        NS.panelArbolDeudas    
		]
	});
	
	
	NS.panelConciliaciones = new Ext.Panel({
		id: 'panelConciliaciones',
		title: 'Conciliaciones',
		layout: 'fit',
		columnWidth : .33,
		
		autoScroll: true,
		bufferResize: true,
		collapsible: true,
		collapsed: false,
		margins:'0 0 0 0',
		bodyStyle: 'background:#A3BD31',
		items: [
		        NS.panelArbolConciliaciones
		]
	});
	
	
	NS.panelComparativo = new Ext.Panel({
		id: 'panelComparativo',
		title: 'Comparativo',
		layout: 'fit',
		columnWidth : .34,
		bufferResize: true,
		autoScroll: true,
		collapsible: true,
		collapsed: false,
		margins:'0 0 0 0',
		bodyStyle: 'background:#A3BD31',
		items: [
		        NS.panelArbolComparativo      
		]
	});
	
	
	NS.panelDiversos = new Ext.Panel({
		id: 'panelDiversos',
		title: 'Diversos',
		layout: 'fit',
		collapsible: true,
		collapsed: false,
		margins:'0 0 0 0',
		bufferResize: true,
		columnWidth : .33,
		autoScroll: true,
		bodyStyle: 'background:#A3BD31',
		items: [
		        NS.panelGridDiversos    
		]
	});
	
	/*******************************************
	 * Panel central
	 */
	
	NS.panelAuxiliarA = new Ext.Panel({
		layout: 'column',
		layoutConfig:{columns:3},
		bufferResize: true,
		bodyStyle: 'background:#A3BD31',
		anchor: '100% 50%',
		items:[
		       NS.panelPcaja ,
		       NS.panelPInversion,
		       NS.panelPDeuda,
		       ]
	});
	
	
	NS.panelAuxiliarB = new Ext.Panel({
		layout: 'column',
		layoutConfig:{columns:3},
		bufferResize: true,
		bodyStyle: 'background:#A3BD31',
		anchor: '100% 50%',
		items:[
		       NS.panelConciliaciones,
		   	   NS.panelComparativo,
		   	   NS.panelDiversos ,
		       ]
	});
	
	NS.panelCentral = new Ext.Panel({
		id: 'centerTabPanel',
		name: 'centerTabPanel',
		title: 'Monitor de Tesoreria Corporativa',
		region:'center',
		layout: 'anchor',
		bufferResize: true,
		bodyStyle: 'background:#A3BD31',
		anchor: '100% 100%',
		items:[
		       NS.panelAuxiliarA,
		       NS.panelAuxiliarB
		       ]
	});
	
	/***********Panel east ****************************/
	NS.panelBotonesGrafica = new Ext.form.FieldSet({
		id: 'panelGrafica',
		title: 'Grafica',
		y:90,
		width:130,
		height: 150,
		layout: 'absolute',
		items: [
		        //Botones de graficas
		        {
		        	xtype: 'button',
		        	text: 'Posición caja',
					x: 15,
					y: 0,
					width: 80,
					height: 22,
					tabIndex:2,
					listeners: {
						click:{
							fn: function(e){
								NS.graficar('CAJA');
								NS.winGrafica.setTitle('Gráfica de posición de caja');
							}
						}
					}
				},
				{
		        	xtype: 'button',
		        	text: 'Posición Inversión',
					x: 10,
					y: 30,
					width: 80,
					height: 22,
					tabIndex:2,
					listeners: {
						click:{
							fn: function(e){
								NS.graficar('INVERSION');
								NS.winGrafica.setTitle('Gráfica de posición de inversión');
							}
						}
					}
				},
				{
		        	xtype: 'button',
		        	text: 'Posición deuda',
					x: 15,
					y: 60,
					width: 80,
					height: 22,
					tabIndex:2,
					listeners: {
						click:{
							fn: function(e){
								NS.graficar('DEUDA');
								NS.winGrafica.setTitle('Gráfica de posición de deuda.');
							}
						}
					}
				},
				{
		        	xtype: 'button',
		        	text: 'Conciliación',
					x: 15,
					y: 90,
					width: 80,
					height: 22,
					tabIndex:2,
					listeners: {
						click:{
							fn: function(e){
								NS.graficar('CONCILIACION');
								NS.winGrafica.setTitle('Gráfica de conciliación');
							}
						}
					}
				}
		]
	});
	
	NS.panelDivisa = new Ext.form.FieldSet({
		id: 'panelDivisa',
		title: 'Divisa',
		layout: 'column',
		cls:'center-align',
		y:0,
		width:100,
		height: 80,
		items: [
		        NS.radioDivisa  
		]
	});
	
	NS.panelOpcionesAux = new Ext.form.FieldSet({
		id: 'panelOpcionesAux',
		title: '',
		bodyStyle: 'background:#E9EFD1',
		//layout: 'absolute',
		width:150,
		height: 500,
		items: [
			NS.panelDivisa, 
			NS.panelBotonesGrafica  
		]
	});

	NS.panelOpciones = new Ext.Panel({
		id: 'panelOpciones',
		title: 'Opciones',
		layout: 'fit',
		bodyStyle: 'background:#E9EFD1',
		items: [
		      NS.panelOpcionesAux 
		]
	});


	//Este es el contenedor principal
	NS.viewport = new Ext.Viewport({
		layout:'border',
		items:[
			{
				region:'north',
				contentEl: 'north',
				split:true,
				height: 65,
				minSize: 65,
				maxSize: 65,
				collapsible: false,
				collapsed: false,
				title:'WEBSET',
				margins:'0 0 0 0',
				id: 'viewPortNorth',
				name: 'viewPortNorth',
				layout: 'absolute',
				bodyStyle: 'background:#E9EFD1'
			},
			
			NS.panelCentral,
			{
				region:'east',
				title: '',
				collapsible: true,
				collapsed: false,
				split:true,
				width: 200,
				minSize: 200,
				maxSize: 200,
				layout:'fit',
				margins:'0 5 0 0',
				items:[
				       NS.panelOpciones
				]
			},
			{
				region:'south',
				contentEl: 'south',
				split:true,
				height: 50,
				minSize: 30,
				maxSize: 50,
				collapsible: true,
				collapsed: true,
				title:'Indicadores',
				margins:'0 0 0 0',
				id: 'viewPortSouth',
				name: 'viewPortSouth',
				bodyStyle: 'background:#5B5B5E',
				items:[ NS.lblIndicadores]
			}
		]
	});
	

	/***********************
	 * VENTANA PARA GRAFICAR
	 */
	
	NS.winGrafica = new Ext.Window({
		title: 'Gráfica',
		modal: true,
		shadow: true,
		closeAction: 'hide',
		width: 500,
	   	height: 500,
	   	layout: 'fit',
	   	plain: true,
	   	resizable:true,
	   	draggable:true,
	   	closable:true,
	    bodyStyle: 'padding:10px;',
	   	items: [
		   	    NS.panelGraficaPastel,
		   		NS.panelGraficaBarras 
	   	        ],
	     listeners:{
	    	hide:{fn:function(){
	    	}},
			 show:{fn:function(){ 
			 }}
	     } 
	});
}); // END  Ext.onReady(function()... 

// Funcion que establece el tiempo con el que va a contar el usuario de inactividad,
// cuando se llegue a ese tiempo preguntara que si desea continuar en el sistema o salir de el.
function startTime() {
	//Establecemos el tiempo de 15 min para el cierre de la sesión esto es en milisegundos 900000
	timeOut = window.setTimeout("alerta()", 32400000);
	//timeOut = window.setTimeout('window.location = "login.jsp";',20000);
};

//Si se detecta un movimiento dentro del sistema se detiene el tiempo y se comienza de nuevo.
function stopAndStartTime() {
	window.clearTimeout(timeOut);
	startTime();
};

//Al cargarse la página comienza a correr el tiempo establesido een la funcion de startTime 
window.onload = startTime();