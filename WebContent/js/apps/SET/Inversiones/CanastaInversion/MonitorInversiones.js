/* 
 * @author: Victor H. Tello
 * @since: 31/Octubre/2014
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Inversiones.CanastaInversion.MonitorInversiones');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	//Formato de un numero a monetario
	NS.formatNumber = function(num,prefix){
		num = num.toString();
		if(num.indexOf('.')>-1){
			if(num.substring(num.indexOf('.')).length<3){
				num = num + '0';
			}
		}
		else{
			num = num + '.00';
		}
		prefix = prefix || '';
		var splitStr = num.split('.');
		var splitLeft = splitStr[0];
		var splitRight = splitStr.length > 1 ? '.' + splitStr[1] : '';
		var regx = /(\d+)(\d{3})/;
		while (regx.test(splitLeft)) {
			splitLeft = splitLeft.replace(regx, '$1' + ',' + '$2');
		}
			return prefix + splitLeft + splitRight;
	};
	
	//Etiqueta empresa
	NS.labEmpresa = new Ext.form.Label({
		text: 'Empresa',
        x: 10
	});
    
	//Store empresas
    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: BarridoInversionAction.empresasConcentradoras, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen empresas concentradoras');
			}
		}
	}); 
	NS.storeEmpresas.load();
	
	//Combo empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 10,
        y: 15,
        width: 300,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 0
	});
	
	//Etiqueta divisa
	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa',
		x: 320
	});
	
	//store combo divisa
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: TraspasosAction.llenarComboDivisa,
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
			
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('SET','No hay divisas disponibles');
			}
		}
	});
	NS.storeDivisa.load();
	
	//Combo Divisas (obligatorio)
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa,
		name: PF+'cmbDivisa',
		id: PF+'cmbDivisa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 320,
        y: 15,
        width: 150,
		valueField:'idDivisa',
		displayField:'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccione la divisa',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 1,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.llenaComboBanco(combo.getValue());
				}
			}
		}
	});
	
	//Refresca el combo bancos en base a la divisa
	NS.llenaComboBanco = function(valueCombo){
		NS.cmbBanco.reset();
		NS.storeBancos.removeAll();
		
		if(valueCombo != null && valueCombo != undefined && valueCombo != '')
			NS.storeBancos.baseParams.idDivisa = valueCombo;
		else
			NS.storeBancos.baseParams.idDivisa = '';
		NS.storeBancos.load();
	};
	
	//Etiqueta Banco
	NS.labBanco = new Ext.form.Label({
		text: 'Banco',
		x: 480,
		hidden: true
	});
	
	//Store banco
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{idDivisa: 0},
		paramOrder:['idDivisa'],
		directFn: BarridoInversionAction.todosLosBancos, 
		idProperty: 'noBanco',
		fields: [
			 {name: 'noBanco'},
			 {name: 'nomBanco'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				
				//if(records.length == null || records.length <= 0)
					//Ext.Msg.alert('SET','No Existen bancos con la divisa seleccionada');
			}
		}
	});
	
	//Combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancos,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 480,
	    y: 15,
	    width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'noBanco',
		displayField:'nomBanco',
		autocomplete: true,
		emptyText: 'Seleccione banco',
		triggerAction: 'all',
		value: '',
		hidden: true,
		tabIndex: 2
	});
	
	//Contenedor de Busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: 'búsqueda',
		width: 995,
	    height: 80,
	    layout: 'absolute',
	    items:[
	        NS.labEmpresa,
	        NS.cmbEmpresas,
	        NS.labBanco,
            NS.cmbBanco,
            NS.labDivisa,
            NS.cmbDivisa,
        	{
	        	xtype: 'button',
	        	text: 'Buscar',
	        	x: 880,
	        	y: 15,
	        	width: 80,
	        	height: 22,
	        	tabIndex: 3,
	        	listeners: {
	        		click:{
	        			fn:function(e){
	        				NS.buscar();
	        			}
	        		}
	        	}
	        }
	    ]
	});
	//Store Datos donde se muestran los valores seleccionados
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['noEmpresa', 'idDivisa'],
		directFn: BarridoInversionAction.obtenerCanastasInv,
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'noCanasta'},
			{name: 'canasta'},
			{name: 'importe'},
			{name: 'interes'},
			{name: 'isr'},
			{name: 'tasa'},
			{name: 'fechaAlta'},
			{name: 'dias'},
			{name: 'fechaVence'},
			{name: 'folioBanco'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
				
				if(records.length == null || records.length <=0)
					Ext.Msg.alert('SET', 'No existe información con los parametros seleccionados');
				else
					NS.crarNodosArbol();
			}
		}
	});
	
	//Para indicar el modo de seleccion de los registros en el Grid 
	NS.tipoSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columnaSelec = new Ext.grid.ColumnModel([
	    //NS.tipoSeleccion,
	    {header: 'No. Empresa',width: 50,dataIndex: 'noEmpresa', sortable: true, hidden: true},
	    {header: 'Empresa',width: 160,dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'No. Canastas', width: 120, dataIndex: 'noCanasta', sortable: true, hidden: true},
	    {header: 'Canastas', width: 90, dataIndex: 'canasta', sortable: true},
	    {header: 'Monto a invertir', width: 110, dataIndex: 'importe', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Interes', width: 110, dataIndex: 'interes', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'ISR', width: 80, dataIndex: 'isr', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Tasa', width: 80, dataIndex: 'tasa', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Fecha inicio', width: 90, dataIndex: 'fechaAlta', sortable: true},
	    {header: 'Plazo', width: 45, dataIndex: 'dias', sortable: true, css: 'text-align:right;'},
	    {header: 'Fecha vencimiento', width: 100, dataIndex: 'fechaVence', sortable: true},
	    {header: 'Folio banco', width: 90, dataIndex: 'folioBanco', sortable: true}
	]);
	//Grid para mostrar los datos seleccionados
	NS.gridDatos = new Ext.grid.EditorGridPanel({
		store: NS.storeDatos,
		id: 'gridDatos',
		cm: NS.columnaSelec,
		sm: NS.tipoSeleccion,
		width: 970,
	    height: 265,
	    stripeRows: true,
	    columnLines: true,
	    listeners:{
			dblclick:{
				fn:function(grid) {
					var selDatos = NS.gridDatos.getSelectionModel().getSelections();
			   		
					if(selDatos.length > 0) {
						NS.storeDetalle.baseParams.noCanasta = parseInt(selDatos[0].get('noCanasta'));
						NS.storeDetalle.load();
						
						winDetalle.show();
					}
				}
			}
		}
	});
	
	//Contenedor de Transferencia o Factoraje
	NS.contCuentas = new Ext.form.FieldSet({
		title: '',
        y: 90,
        width: 995,
        height: 335,
        layout: 'absolute',
        items: [
               NS.gridDatos,
               {
            	   xtype: 'button',
            	   text: 'Código',
            	   x: 790,
            	   y: 290,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
				            	var selDatos = NS.gridDatos.getSelectionModel().getSelections();
				           		
				           		if(selDatos.length == 0) {
				           			Ext.Msg.alert('SET', 'Seleccione un registro a clasificar con su código');
				           			return;
				           		}				
			           			winCodigo.show();
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Vencer',
            	   x: 880,
            	   y: 290,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
				            	var selDatos = NS.gridDatos.getSelectionModel().getSelections();
				           		
				           		if(selDatos.length == 0) {
				           			Ext.Msg.alert('SET', 'Seleccione un registro a vencer');
				           			return;
				           		}				
			           			winTasaReal.show();
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Imprimir',
            	   x: 700,
            	   y: 290,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
				            	if(NS.cmbEmpresas.getValue() == '') {
				           			Ext.Msg.alert('SET', 'Seleccione una empresa');
				           			return;
				           		}
				           		if(NS.cmbDivisa.getValue() == '') {
				           			Ext.Msg.alert('SET', 'Seleccione una divisa');
				           			return;
				           		}
				            	var nomReporte = 'CanastasVencenHoy';
				            	var strParam = '?nomReporte=' + nomReporte;
				            	strParam += '&'+'nomParam1=pFecIni';
								strParam += '&'+'valParam1=hoy';
								strParam += '&'+'nomParam2=pFecFin';
								strParam += '&'+'valParam2=hoy';
								strParam += '&'+'nomParam3=noConcentradora';
								strParam += '&'+'valParam3=' + NS.cmbEmpresas.getValue();				
								strParam += '&'+'nomParam4=nomReporte';
								strParam += '&'+'valParam4=' + nomReporte;
								
								window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParam);
								return;
            	   			}
               			}
               		}
               }
        ]
    });
	//Contenedor General
	NS.contGeneral = new Ext.form.FieldSet({
		title: '',
		width: 1020,
	    height: 450,
	    x: 20,
	    y: 5,
	    layout: 'absolute',
	    items:[
			NS.contBusqueda,
			NS.contCuentas
		]
	});
	
	NS.lblTasaReal = new Ext.form.Label({
		text: 'Tasa real',
		x: 10,
		y: 20
	});
	
	NS.txtTasaReal = new Ext.form.TextField({
		id: PF + 'txtTasaReal',
		x: 10,
        y: 35,
        width: 125
    });
	
	NS.contTasaReal = new Ext.form.FieldSet({
		layout: 'absolute',
	    items: [
	            NS.lblTasaReal,
	            NS.txtTasaReal,
			{
			    xtype: 'button',
			   	text: 'Aceptar',
			   	id: PF + 'aceptarTasaReal',
			   	x: 10,
				y: 100,
				width: 80,
				height: 22,
				listeners: {
					click:{
						fn:function(e){
	            			if(Ext.getCmp(PF + 'txtTasaReal').getValue() == '') {
	            				Ext.Msg.alert('SET', 'Digite una tasa valida');
	            				return;
	            			}
	            			NS.vencimiento(Ext.getCmp(PF + 'txtTasaReal').getValue());
	            			Ext.getCmp(PF + 'txtTasaReal').setValue('');
	            			winTasaReal.hide();
						}
					}
				}
			},{
        	    xtype: 'button',
        	   	text: 'Cancelar',
        	   	x: 100,
				y: 100,
				width: 80,
				height: 22,
				listeners: {
					click:{
						fn:function(e){
							Ext.getCmp(PF + 'txtTasaReal').setValue('');
							winTasaReal.hide();
						}
					}
				}
            }
	           ]
	});
	
	//ventana para el detalle de las propuestas a pagar
	var winTasaReal = new Ext.Window({
		title: 'Modifica Tasa',
		modal: true,
		shadow: true,
		width: 250,
	   	height: 200,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    closable: false,
	   	items: [
	   	        NS.contTasaReal
	   	        ]
	});//Termina ventana
	
	NS.lblCodigo = new Ext.form.Label({
		text: 'Código bancario',
		x: 10,
		y: 20
	});
	
	NS.txtCodigo = new Ext.form.TextField({
		id: PF + 'txtCodigo',
		x: 10,
        y: 35,
        width: 125
    });
	
	NS.contCodigo = new Ext.form.FieldSet({
		layout: 'absolute',
	    items: [
	            NS.lblCodigo,
	            NS.txtCodigo,
			{
			    xtype: 'button',
			   	text: 'Aceptar',
			   	id: PF + 'aceptarCodigo',
			   	x: 10,
				y: 100,
				width: 80,
				height: 22,
				listeners: {
					click:{
						fn:function(e){
	            			if(Ext.getCmp(PF + 'txtCodigo').getValue() == '') {
	            				Ext.Msg.alert('SET', 'Digite una código de confirmación');
	            				return;
	            			}
	            			NS.clasificacion(Ext.getCmp(PF + 'txtCodigo').getValue());
	            			Ext.getCmp(PF + 'txtCodigo').setValue('');
	            			winCodigo.hide();
						}
					}
				}
			},{
        	    xtype: 'button',
        	   	text: 'Cancelar',
        	   	x: 100,
				y: 100,
				width: 80,
				height: 22,
				listeners: {
					click:{
						fn:function(e){
							Ext.getCmp(PF + 'txtCodigo').setValue('');
							winCodigo.hide();
						}
					}
				}
            }
	           ]
	});
	
	//ventana para el detalle de las propuestas a pagar
	var winCodigo = new Ext.Window({
		title: 'Ingrese código de confirmación',
		modal: true,
		shadow: true,
		width: 250,
	   	height: 200,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    closable: false,
	   	items: [
	   	        NS.contCodigo
	   	        ]
	});//Termina ventana
	

	//******************************************************** Este codigo es para el detalle de las canastas *****************************************//
	
	//Store detalle de las canastas
	NS.storeDetalle = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['noCanasta'],
		directFn: BarridoInversionAction.consultarDetalleCanasta, 
		fields: [
				{name: 'nomEmpresa'},
				{name: 'nomBanco'},
				{name: 'idChequera'},
				{name: 'importe'},
				{name: 'interes'},
				{name: 'isr'},
				{name: 'folioBanco'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDetalle, msg: "Buscando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No hay detalle de canastas');
			}
		}
	});
	
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.selecDeta = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columDeta = new Ext.grid.ColumnModel([
	    //NS.selecDeta,
	    {header :'Empresa', width :180, sortable :true, dataIndex :'nomEmpresa'},
	    {header :'Banco', width :100, sortable :true, dataIndex :'nomBanco'},
	    {header :'Cuenta', width :80, sortable :true, dataIndex :'idChequera'},
	    {header :'Importe', width :100, sortable :true, css: 'text-align:right;', dataIndex :'importe', renderer: BFwrk.Util.rendererMoney},
	    {header :'Interes', width :100, sortable :true, css: 'text-align:right;', dataIndex :'interes', renderer: BFwrk.Util.rendererMoney},
	    {header :'ISR', width :80, sortable :true, css: 'text-align:right;', dataIndex :'isr', renderer: BFwrk.Util.rendererMoney},
	    {header :'Folio banco', width :100, sortable :true, dataIndex :'folioBanco'}
	]);
	
	//Grid para mostrar los datos seleccionados
	NS.gridDetalle = new Ext.grid.GridPanel({
		store: NS.storeDetalle,
		id: 'gridDetalle',
		x: 5,
		y: 5,
		cm: NS.columDeta,
		//sm: NS.selecDeta,
		width: 750,
	    height: 310,
	    stripeRows: true,
	    columnLines: true,
	    title: ''
	});
	
	//Contenedor de detalle de las propuestas
	NS.contDetalle = new Ext.form.FieldSet({
		title: '',
		layout: 'absolute',
	    items: [
	           NS.gridDetalle,
	           {
	        	    xtype: 'button',
	        	   	text: 'Cerrar',
	        	   	x: 650,
					y: 330,
					width: 80,
					height: 22,
					listeners: {
						click:{
							fn:function(e){
								winDetalle.hide();
							}
						}
					}
	           }
	           ]
	});
	
	var winDetalle = new Ext.Window({
		title: 'Detalle de Canastas',
		modal: true,
		shadow: true,
		width: 820,
	   	height: 450,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    closable: false,
	   	items: [
	   	        NS.contDetalle
	   	        ]
	});//Termina ventana

	//**************************************************** Termina el código para el detalle de las canastas *****************************************//	
	
	NS.buscar = function() {
		if(NS.cmbEmpresas.getValue() == '') {
			Ext.Msg.alert('SET', 'Seleccione una empresa');
			return;
		}
		if(NS.cmbDivisa.getValue() == '') {
			Ext.Msg.alert('SET', 'Seleccione una divisa');
			return;
		}
		NS.storeDatos.baseParams.noEmpresa = NS.cmbEmpresas.getValue()+'';
		NS.storeDatos.baseParams.idDivisa = NS.cmbDivisa.getValue();
		NS.storeDatos.load();
	};
	
	NS.vencimiento = function(tasaReal) {
		var selDatos = NS.gridDatos.getSelectionModel().getSelections();
		var matDatos  = new Array();
		var cadenaJson = '';
		
		for(var i=0; i<selDatos.length; i++) {
			var vec = {};
			vec.noCanasta = selDatos[i].get('noCanasta');
			matDatos[i] = vec;
		}
		cadenaJson = Ext.util.JSON.encode(matDatos);
		
		BarridoInversionAction.vencimientoCanastas(cadenaJson, NS.cmbEmpresas.getValue(), tasaReal, function(res, e){
			Ext.Msg.alert('SET', res);
			NS.limpiar();
		});
	};
	
	NS.clasificacion = function(codigo) {
		var selDatos = NS.gridDatos.getSelectionModel().getSelections();
		var matDatos  = new Array();
		var cadenaJson = '';
		
		if(selDatos[0].get('folioBanco') != '') {
			Ext.Msg.confirm('SET', 'Este movimiento ya fue clasificado, desea modificarlo?', function(btn) {
				if(btn == 'yes') {
					BarridoInversionAction.clasificacionCanastas(selDatos[0].get('noCanasta'), NS.cmbEmpresas.getValue(), codigo, function(res, e){
						Ext.Msg.alert('SET', res);
						NS.limpiar();
						NS.buscar();
					});
				}
			});
		}else {
			BarridoInversionAction.clasificacionCanastas(selDatos[0].get('noCanasta'), NS.cmbEmpresas.getValue(), codigo, function(res, e){
				Ext.Msg.alert('SET', res);
				NS.limpiar();
				NS.buscar();
			});
		}
	};
	
	//Funcion para limpiar los campos
	NS.limpiar = function(){
		NS.cmbBanco.reset();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
	};
	
	NS.treePanelMenu = new Ext.tree.TreePanel({
    	iconCls: 'menu_seguridad',
        id: PF + 'treeMenuDivPerfiles',
    	region: 'center',
        width: 350,
		minSize: 200,
		maxSize: 340,
        autoScroll:true,
        bodyStyle:'background-color:#5B5B5E; border:1px solid #B5B8C8'
    });
	
 	NS.crarNodosArbol = function() {
 		BarridoInversionAction.crearNodosArbol(NS.cmbEmpresas.getValue()+'', NS.cmbDivisa.getValue(), function(resp, e){
 			
 			var jSonMenu = Ext.util.JSON.decode(resp);
			
 			NS.treeNode = new Ext.tree.AsyncTreeNode({
 				text: 'Inversiones',
 				id: PF + 'treeNode',
 				expanded:true,
 				allowChildren:true,
 				children: jSonMenu.nodos
 			});
 			
 			NS.treeNode.setText(jSonMenu.raiz);
 			NS.treePanelMenu.setRootNode(NS.treeNode);
 		});
	};
	
	NS.splitPermisos = new Ext.Panel({
		id: PF+'idSplitPermisos',
		plugins: ['fittoparent'],
		layout:'border',
		width: '100%',
		height: '100%',
		monitorResize: true,
		items: [
		        NS.treePanelMenu
		]
	});
	
	NS.perfilesTab = new Ext.TabPanel({
		xtype:'tabpanel',
        id: PF+'pDetallePerfiles',
        region: 'east',
        autoScroll: true,
        frame: true,
        width: 500,
		minSize: 10,
		maxSize: 500,
		split: true,
		collapsible: true,
		collapsed: true,
		height: '100%',
		defaultType: 'textfield',
		monitorResize: true,
		items:[{
		    xtype:'panel',
		    title:'Detalle de inversiones',
		    frame: true,
		    items: [NS.splitPermisos]
		  }
		]
	});
    
	NS.MonitorInversiones = new Ext.FormPanel({
		title: 'Monitor de inversiones',
		id: PF + 'monitorInversiones',
		region: 'center',
		height: '100%',
		split: true,
		frame: true,
	    autoScroll: true,
	    items:[NS.contGeneral]
	});
	
	//panel central
	NS.panelCentral = new Ext.Panel({
        renderTo: NS.tabContId,
		id: PF + 'panelCentral',
		plugins: ['fittoparent'],
		layout:'border',
		width: '100%',
		height: '100%',
		monitorResize: true,
		items: [NS.perfilesTab, NS.MonitorInversiones]
	});
});
