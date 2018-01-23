Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Egresos.Cupos.MantenimientoBitacora');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.noEmpresa='';
	NS.noBeneficiario='';
	NS.estatus='';
	NS.Excel= false;
	NS.nombreProv='';
	NS.bandera=false;
	
	NS.myMask1 = new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	
	NS.lbEmpresas = new Ext.form.Label({
		text: 'Empresas: ',
		x: 0,
		y : 8		
	});
	
	NS.lbProveedores = new Ext.form.Label({
		text: 'Proveedores: ',
		x: 270,
		y : 8		
	});

	NS.lbEstatus= new Ext.form.Label({
		text: 'Estatus ',
		x: 690,
		y : 8		
	});
	
	/************ Combo estatus ***************************************
	 * 
	 */
	
	//Datos del combo
	NS.datosCmbEstatus = [['', 'TODOS'],['C', 'CORREGIDO'],	['R', 'RECHAZADO']];	
	
	//store para el combo
	NS.storeCmbEstatus = new Ext.data.SimpleStore({
   		idProperty: 'estatus',
   		fields: [
   					{name: 'estatus'},
   					{name: 'descEstatus'}
   				]
   	});
   	NS.storeCmbEstatus.loadData(NS.datosCmbEstatus);
   	
   	//combo estatus
   
	NS.cmbEstatus= new Ext.form.ComboBox ({
		store: NS.storeCmbEstatus,
		id: PF + 'cmbEstatus ',
		name: PF + 'cmbEstatus ',
		x: 740,
		y: 5,
		width: 100,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'estatus',
		displayField: 'descEstatus',
		autocomplete: true,
		emptyText: 'Seleccionar estatus',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					NS.estatus=combo.getValue();
					if(combo.getValue()=='C'){
						Ext.getCmp(PF + 'btnCorregir').setVisible(false);
					}else{
						Ext.getCmp(PF + 'btnCorregir').setVisible(true);
					}
				}
			}
		}
	});
	
	/***********************Combo empresas ********************************
	 * 
	 *
	 */
	NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		root: '',
		directFn: MantenimientoBitacoraRentasAction.llenaComboEmpresas, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No Existen empresas con pagos rechazados');
				}
				NS.myMask1.hide();
			},
			exception:function(misc) {
				NS.myMask1.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
	NS.myMask1.show();
	NS.storeEmpresas.load();
	
 	NS.cmbEmpresa = new Ext.form.ComboBox({
 		store: NS.storeEmpresas,
		name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,autocomplete: true
        ,x: 110
        ,y: 5
        ,width: 150
		,valueField:'id'
		,displayField:'descripcion'
		,emptyText: 'Seleccionar empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,hidden: false 
		,listeners:{
			select:{
				fn:function(combo, valor) {
					//NS.noEmpresa=combo.getValue();
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
				}
			}
		}
	});
 	
 	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
        name:PF + 'txtNoEmpresa',
		x: 50,
        y: 5,
        width: 50,
        tabIndex: 0,
        listeners: {
			change: {
				fn: function(caja,valor) {
					valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
					if(valueCombo!='' && valueCombo !='undefined' && valueCombo != null)
						NS.noEmpresa=caja.getValue();
					else{
						NS.txtNoEmpresa.setValue('');
						NS.cmbEmpresa.reset();
					}
						
             
			}
    	}
        }
    });
	
	/************Combo proveedores *****************************
	 * 
	 */
 	NS.storeProveedor = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{nombre:'' ,noProv:'0'},
		root: '',
		paramOrder:['nombre' ,'noProv'],
		directFn: MantenimientoBitacoraRentasAction.llenaComboProveedor, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProveedor , msg:"Cargando..."});
				if(records.length==null || records.length<=0){
					//Ext.Msg.alert('SET','No existen proveedores');
					NS.storeProveedor.removeAll();
					NS.cmbProveedor.reset;
					NS.txtProveedor.setValue('');
				}else{
					if(NS.bandera) {
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtProveedor', NS.cmbProveedor.getId());
					}
				}
			},
			exception:function(misc) {
				NS.myMask1.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
 	
 	NS.cmbProveedor = new Ext.form.ComboBox({
 		store: NS.storeProveedor,
		name: PF+'cmbProveedor'
		,id: PF+'cmbProveedor'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,autocomplete: true
        ,x: 400
        ,y: 5
        ,width: 270
		,valueField:'id'
		,displayField:'descripcion'
		,emptyText: 'Seleccionar proveedor'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,hidden: false
		,minLength:4
		,listeners:
			{
				select:{
					fn:function(combo, valor) {
						//NS.noBeneficiario=combo.getValue();
						BFwrk.Util.updateComboToTextField(PF + 'txtProveedor', NS.cmbProveedor.getId());
					}
				},
				beforequery: function(qe){
					NS.nombreProv=qe.combo.getRawValue();
					BFwrk.Util.updateComboToTextField(PF + 'txtProveedor', NS.cmbProveedor.getId());
				},
				valid:function(caja){
					if(NS.nombreProv!='') {
						NS.storeProveedor.baseParams.nombre=NS.nombreProv;
						NS.storeProveedor.baseParams.noProv='0';
    					NS.storeProveedor.load();
	  				}else{
	  					NS.storeProveedor.removeAll();
	  				}
				}
		}
	});
	
 	NS.txtProveedor = new Ext.form.TextField({
		id: PF + 'txtProveedor',
        name:PF + 'txtProveedor',
		x: 340,
        y: 5,
        width: 50,
        tabIndex: 0,
        value:'0',
        listeners: {
			/*change: {
				fn: function(caja,valor) {
					valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtProveedor', NS.cmbProveedor.getId());
					if(valueCombo!='' && valueCombo !='undefined' && valueCombo != null){
						NS.txtProveedor.setValue('');
						NS.storeProveedor.removeAll();
						NS.cmbProveedor.reset();
					}else
						NS.nombreProv=caja.getValue();
                }
			},*/
 			blur:{
 				fn: function(caja,valor) {
	 				if(caja.getValue()!='') {
	 					NS.bandera=true;
	 					NS.storeProveedor.baseParams.nombre='';
						NS.storeProveedor.baseParams.noProv=caja.getValue()+'';
						NS.storeProveedor.load();
						
	  				}
 				}
 			}
    	}
    });
	/*********************** Grid de pagos rechazados ***************************************/
	
	//Store para llenar el grid con los registros de la tabla BITACORA_PAGO_RECHAZADO
	
 	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {noEmpresa:'' , noBeneficiario:'', estatus:''},
		paramOrder: ['noEmpresa','noBeneficiario','estatus'],
		directFn: MantenimientoBitacoraRentasAction.llenaGridBitacoraRentas,
		fields: [
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'},
		 	{name: 'noBeneficiario'},
		 	{name: 'nomBeneficiario'},
		 	{name: 'noDocumento'},
		 	{name: 'noFolioDetalle'},
		 	{name: 'fechaRechazo'},
		 	{name: 'importe'},
		 	{name: 'idTipoGasto'},
		 	{name: 'causaRechazo'},
		 	{name: 'estatus'},
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Buscando..."});
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'Registro vacio');}
				NS.myMask1.hide();
			},
			exception:function(misc) {
				NS.myMask1.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}	
	});
	//NS.storeLlenaGrid.load();
 	
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
	
	//Quitar formato a las cantidades
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,''); //num.replace(/([^0-9\.\-])/g,''*1);
	};
	
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnas = new Ext.grid.ColumnModel
	([
	  NS.columnaSeleccion,
	  {header: 'No.Empresa', width: 0, dataIndex: 'noEmpresa', sortable: true},
	  {header: 'Empresa', width: 250, dataIndex: 'nomEmpresa', sortable: true},
	  {header: 'No.Proveedor', width: 0, dataIndex: 'noBeneficiario', sortable: true},
	  {header: 'Proveedor', width: 250, dataIndex: 'nomBeneficiario', sortable: true},
	  {header: 'Documento', width: 100, dataIndex: 'noDocumento', sortable: true},
	  {header: 'Folio detalle', width: 100, dataIndex: 'noFolioDetalle', sortable: true},
	  {header: 'Fecha rechazo', width: 100, dataIndex: 'fechaRechazo', sortable: true},
	  {header: 'Importe', width: 100, dataIndex: 'importe', sortable: true, css: 'text-align:right;', renderer: function (value,meta,record){return ''+ NS.formatNumber(value)}},
	  {header: 'Tipo', width: 50, dataIndex: 'idTipoGasto', sortable: true},
	  {header: 'Estatus', width: 50, dataIndex: 'estatus', sortable: true},
	  {header: 'Causa rechazo', width: 200, dataIndex: 'causaRechazo', sortable: true},
	]);
			
	NS.grid= new Ext.grid.GridPanel
	({
		store: NS.storeLlenaGrid,
		id: PF + 'grid',
		name: PF + 'grid',
		cm: NS.columnas,
		sm: NS.columnaSeleccion,
		x: 0,
		y: 0,
		width: 950,
		height: 330,
		stripRows: true,
		columnLines: true,
	});
	
	/*********************** Grid de pagos rechazados ***************************************/
	/************* Funcion con las opciones de los botones de la pantalla *************/
	NS.opciones =function (opcion){
		switch (opcion) {
		case 'restaurar':
				NS.cmbEmpresa.reset();
				NS.cmbEstatus.reset();
			 	NS.cmbProveedor.reset();
				/*NS.noEmpresa='';
				NS.noBeneficiario='';
				NS.estatus='';
				NS.storeLlenaGrid.baseParams.noEmpresa=NS.noEmpresa;
				NS.storeLlenaGrid.baseParams.noBeneficiario=NS.noBeneficiario;
				NS.storeLlenaGrid.baseParams.estatus=NS.estatus;
				NS.storeLlenaGrid.load();*/
			break;

		case 'buscar':
			     NS.noBeneficiario =NS.txtProveedor.getValue()+'';
			     NS.noEmpresa = NS.txtNoEmpresa.getValue()+'';
			     NS.bandera=false;
				// if(NS.noEmpresa!= '' || NS.noBeneficiario != '' ){
					NS.storeLlenaGrid.baseParams.noEmpresa=NS.noEmpresa;
					NS.storeLlenaGrid.baseParams.noBeneficiario=NS.noBeneficiario;
					if(NS.noEmpresa== '' && (NS.noBeneficiario == '' || NS.noBeneficiario == '0') && NS.estatus== '')
						NS.storeLlenaGrid.baseParams.estatus='R';
					else
						NS.storeLlenaGrid.baseParams.estatus= NS.estatus;
					NS.myMask1.show();
					NS.storeLlenaGrid.load();
					//NS.opciones('restaurar');
			//	}else{
				//	 Ext.Msg.alert('SET', "Debe seleccionar al menos una empresa y un beneficiario");
				//}
			break;
		
		case 'corregir': //cambia el estatus a C de confirmado del registro 
			 var registroSeleccionado= NS.grid.getSelectionModel().getSelections();
			 var banderaR=0;
			 var c=0;
			 if (registroSeleccionado.length > 0){
				 
				 var matriz = new Array();
					
					for(var i=0;i<registroSeleccionado.length;i++){
						//alert(registroSeleccionado[i].get('estatus'));
						if(registroSeleccionado[i].get('estatus')=='R'){ //condicion para que solo vaya a la bd si se selecciono un rechazado.
							//alert(2);
							var vector = {};
							vector.noEmpresa=registroSeleccionado[c].get('noEmpresa');
							vector.noBeneficiario=registroSeleccionado[c].get('noBeneficiario');
							vector.noDocumento=registroSeleccionado[c].get('noDocumento');
							vector.noFolioDetalle=registroSeleccionado[c].get('noFolioDetalle');
							vector.fechaRechazo=registroSeleccionado[c].get('fechaRechazo');
							vector.importe=NS.unformatNumber(registroSeleccionado[c].get('importe'));
							vector.idTipoGasto=registroSeleccionado[c].get('idTipoGasto');
							vector.estatus=registroSeleccionado[c].get('estatus');
							matriz[i] = vector;
							c=c+1;
							banderaR=banderaR+1;
						}
					}
				if(banderaR!=0){
					 var jSonString = Ext.util.JSON.encode(matriz);
					 MantenimientoBitacoraRentasAction.updateMantenimientoBitacoraRentas(jSonString, function(resultado, e) {
							if (resultado != '' && resultado != null && resultado != undefined) {
								Ext.Msg.alert('SET',resultado);
							}
							NS.opciones('buscar');
							NS.opciones('restaurar');
					});
				}else{
					Ext.Msg.alert('SET', "Los registros seleccionados ya estan corregidos");
				}
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
				 NS.opciones('restaurar');
				 
			 }
			 
			break;
		}
	}
	/**********************Generar el excel *********************************/
 NS.validaDatosExcel = function() {
		
		var registroSeleccionado = NS.grid.getSelectionModel().getSelections();

		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(registroSeleccionado.length == 0) {
			
			registroSeleccionado = NS.storeLlenaGrid.data.items;
			
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		
		if(NS.Excel) {
			
			var matriz = new Array();
			
			for(var i=0;i<registroSeleccionado.length;i++){
				var vector = {};
				vector.noEmpresa=registroSeleccionado[i].get('noEmpresa');
				vector.nomEmpresa=registroSeleccionado[i].get('nomEmpresa');
				vector.noBeneficiario=registroSeleccionado[i].get('noBeneficiario');
				vector.nomBeneficiario=registroSeleccionado[i].get('nomBeneficiario');
				vector.noDocumento=registroSeleccionado[i].get('noDocumento');
				vector.noFolioDetalle=registroSeleccionado[i].get('noFolioDetalle');
				vector.fechaRechazo=registroSeleccionado[i].get('fechaRechazo');
				vector.importe=registroSeleccionado[i].get('importe');
				vector.idTipoGasto=registroSeleccionado[i].get('idTipoGasto');
				vector.estatus=registroSeleccionado[i].get('estatus');
				matriz[i] = vector;
	 		
			}
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		
		NS.exportaExcel(jSonString);
		NS.excel = false;
	
		return;
	};
	
	NS.exportaExcel = function(jsonCadena) {
		MantenimientoBitacoraRentasAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=mantenimientoBitacoraRentas';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
	/****************Fin de generar excel ***********************/
	//PANEL
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 0,
		width:970,
		height: 50,
		layout: 'absolute',
		items:
		[
		 	NS.cmbEmpresa,
		 	NS.cmbProveedor,
		 	NS.cmbEstatus,
		 	NS.lbEmpresas,
			NS.lbProveedores,
			NS.lbEstatus,
			NS.txtNoEmpresa,
			NS.txtProveedor,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 860,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click:{
		 				fn: function(e){
		 					NS.opciones('buscar');
		 				}
		 			}
		 		}
		 	}/*,
		 	
		 	{
	        	xtype: 'button',
	        	text: '...',
	        	x: 630,
	        	y: 5,
	        	width: 20,
	        	height: 22,
	        	id: PF+'btnBusquedaNombre',
	        	name: PF+'btnBusquedaNombre',
	        	hidden: false,
	        	listeners:{
	        		click:{
	        			fn:function(e){
	        				if(NS.nombreProv==='') {
								  Ext.Msg.alert('SET','Es necesario agregar una letra o nombre');
	        				}else{
	        					NS.storeProveedor.baseParams.nombre=NS.nombreProv;
	        					NS.storeProveedor.load();
	        				}
	        			}
	        		}
	        	}
	        }*/
		 	
		]
	});
	
	NS.panelComponentes = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 60,
		width: 970,
		height: 400,
		layout: 'absolute',
		buttonAlign: 'right',
	    buttons:[
	  		{  text:'Excel',  handler:function(){ NS.Excel = true; NS.validaDatosExcel(); }  },
	        {  text:'Corregir', id: PF + 'btnCorregir', handler:function(){ NS.opciones('corregir'); } },
	    ],
		items: [NS.grid]
	});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1005,		
		height: 485,
		layout: 'absolute',
		items:
		[			 	 	
		 	NS.panelBusqueda,
		 	NS.panelComponentes,
		 	
	    ]
	});

	NS.conceptos = new Ext.FormPanel ({
		title: 'Mantenimiento Bitacora de Rentas',
		width: 1005,
		height: 485,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'conceptos',
		name: PF + 'conceptos',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	NS.conceptos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
	
	
});
 