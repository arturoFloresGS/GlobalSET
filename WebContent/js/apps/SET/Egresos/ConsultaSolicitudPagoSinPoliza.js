Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Egresos.ConsultaSolicitudPagoSinPoliza');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.noEmpresa='';
	NS.fechaIni='';
	NS.fechaFin='';
	NS.usuario='';
	NS.banSeguridad=false;
	
	NS.Excel= false;
	
	NS.lbEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 15,
		y : 0		
	});
	
	NS.lbFecIni = new Ext.form.Label({
		text: 'Fecha Inicial',
		x: 320,
		y : 0		
	});
	
	NS.lbFecFin= new Ext.form.Label({
		text: 'Fecha Final',
		x: 450,
		y : 0		
	});

	NS.lbUsuario= apps.SET.btnFacultativo( new Ext.form.Label({
		id:'eclbUsuario',
		text: 'Usuario',
		x: 570,
		y : 0		
	}));
	
	NS.txtFechaIni = new Ext.form.DateField({
		id: PF + 'txtFechaIni',
		name: PF + 'txtFechaIni',
		x: 320,
		y: 20,
		width: 100,
		format: 'd/m/Y',
		value: NS.fecha,
		enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {	
	 				NS.fechaIni=cambiarFecha(caja.getValue()+'')+'';
	 				var fechaFin = Ext.getCmp(PF + 'txtFechaFin').getValue();
        			if(fechaFin < caja.getValue()&& NS.fechaFin!=''){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
        				Ext.getCmp(PF + 'txtFechaFin').setValue('');
        			}
	 			}
	 		}
		}
	});
	
	
	NS.txtFechaFin = new Ext.form.DateField({
		id: PF + 'txtFechaFin',
		name: PF + 'txtFechaFin',
		x: 450,
		y: 20,
		width: 100,
		format: 'd/m/Y',
		value: '',
		enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {
	 				
	 				NS.fechaFin=cambiarFecha(caja.getValue()+'')+'';
	 				var fechaIni = Ext.getCmp(PF + 'txtFechaIni').getValue();
        			if(fechaIni > caja.getValue()&& NS.fechaIni!=''){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
        				Ext.getCmp(PF + 'txtFechaIni').setValue('');
        			}
	 			}
	 		}
		}
	});

	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
		name: PF + 'txtNoEmpresa',
		x: 15,
		y: 20,
		width: 50,
		enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {	
	 				NS.noEmpresa=caja.getValue();
	 				BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbNoEmpresa.getId());
	 			}
	 		}
		}
	});
	/************ Combo usuarios ***************************************
	 * 
	 */
	NS.storeLlenarComboUsuario = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: { idUsuario:''+NS.idUsuario},
		paramOrder: ['idUsuario'],
		directFn: ConsultaSolicitudPagoSinPolizaAction.llenaComboUsuario,
		fields:
		[  
		    {name: 'usuario'},
		    {name: 'nomUsuario'},
		],
		idProperty: 'usuario',
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storeLlenarComboUsuario, msg: "Cargando Datos ..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay usuarios para mostrar');
				}else{
					
					var recordsStoreGruEmp = NS.storeLlenarComboUsuario.recordType;	
					var todas = new recordsStoreGruEmp({
				        usuario : '',
				       	nomUsuario : '***********TODOS***********'
			       	});
					NS.storeLlenarComboUsuario.insert(0,todas);
					
				}
					
			}
		}

	});
	NS.storeLlenarComboUsuario.load();
   	
	NS.cmbUsuario= apps.SET.btnFacultativo( new Ext.form.ComboBox ({
		store: NS.storeLlenarComboUsuario,
		id: 'eccmbUsuarios ',
		name: 'eccmbUsuarios ',
		x: 570,
		y: 20,
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'usuario',
		displayField: 'nomUsuario',
		autocomplete: true,
		emptyText: 'Seleccionar usuario',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn: function(combo, valor) {
					NS.usuario=combo.getValue();
				}
			}
		}
	}));
	
	/***********************Combo empresas ********************************
	 * 
	 *
	 */
	NS.storeLlenarComboEmpresa = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: GlobalAction.llenarComboEmpresasUsuario,
		fields:
		[
		    
		    {name: 'id'},
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'id',
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storeLlenarComboEmpresa, msg: "Cargando Datos ..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay empresas que mostrar');
				}else{
					
					var recordsStoreGruEmp = NS.storeLlenarComboEmpresa.recordType;	
					var todas = new recordsStoreGruEmp({
				       	id : '',
				       	descripcion : '***********TODAS***********'
			       	});
					NS.storeLlenarComboEmpresa.insert(0,todas);
					
				}
					
			}
		}

	});
	NS.storeLlenarComboEmpresa.load();
	
	NS.cmbNoEmpresa = new Ext.form.ComboBox({
		store: NS.storeLlenarComboEmpresa,
		id: PF + 'cmbNoEmpresa',
		name: PF + 'cmbNoEmpresa',
		width: 230,
		x: 70,
		y: 20,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'SELECCIONE EMPRESA',
		triggerAction: 'all',
		listeners:{
			select:{
				fn: function(combo, valor)
				{
					
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbNoEmpresa.getId());
					NS.noEmpresa=combo.getValue();
				}
			}
			
		}
	});
	
	
	/*********************** Grid de pagos rechazados ***************************************/
	
	//Store para llenar el grid con los registros de la tabla Consulta Solicitud Pago Sin Poliza contable
	
 	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {noEmpresa:'' , fechaIni:'', fechaFin:'', usuario:'', usuarioEnCurso:NS.idUsuario+''},
		paramOrder: ['noEmpresa','fechaIni','fechaFin','usuario','usuarioEnCurso'],
		directFn: ConsultaSolicitudPagoSinPolizaAction.llenaGrid,
		fields: [
		 	{name: 'nomEmpresa'},
		 	{name: 'fechaPago'},
		 	{name: 'beneficiario'},
		 	{name: 'concepto'},
		 	{name: 'importe'},
		 	{name: 'noDocumento'},
		 	{name: 'usuario'},
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Buscando..."});
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'Registro vacio');
				}
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
	  {header: 'Empresa', width: 250, dataIndex: 'nomEmpresa', sortable: true},
	  {header: 'Fecha pago', width: 100, dataIndex: 'fechaPago', sortable: true},
	  {header: 'Beneficiario', width: 250, dataIndex: 'beneficiario', sortable: true},
	  {header: 'Concepto', width: 250, dataIndex: 'concepto', sortable: true},
	  {header: 'Monto', width: 100, dataIndex: 'importe', sortable: true, css: 'text-align:right;', renderer: function (value,meta,record){return ''+ NS.formatNumber(value)}},
	  {header: 'No.Documento', width: 100, dataIndex: 'noDocumento', sortable: true},
	  {header: 'Usuario', width: 50, dataIndex: 'usuario', sortable: true}
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
		width: 940,
		height: 310,
		stripRows: true,
		columnLines: true,
		//autoScroll: true,
	});
	
	/*********************** Grid de pagos rechazados ***************************************/
	/************* Funcion con las opciones de los botones de la pantalla *************/
	NS.opciones =function (opcion){
		switch (opcion) {
		case 'buscar':
					if(NS.fechaIni== '/undefined/' || NS.fechaIni== undefined){
						NS.fechaIni='';
					}
					if(NS.fechaFin== '/undefined/' || NS.fechaFin== undefined){
						NS.fechaFin='';
					}
					NS.storeLlenaGrid.baseParams.noEmpresa=NS.noEmpresa+'';
					NS.storeLlenaGrid.baseParams.fechaIni=NS.fechaIni;
					NS.storeLlenaGrid.baseParams.fechaFin=NS.fechaFin;
					NS.storeLlenaGrid.baseParams.usuario=NS.usuario;
					NS.storeLlenaGrid.load();
					
			break;
		}
	}
	
	function cambiarFecha(fecha)
	{
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		var mes;
		
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
			mes = i+1;
				if(mes<10)
					mes='0'+mes;
			}
		}		
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
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
				vector.nomEmpresa=registroSeleccionado[i].get('nomEmpresa');
				vector.fechaPago=registroSeleccionado[i].get('fechaPago');
				vector.beneficiario=registroSeleccionado[i].get('beneficiario');
				vector.concepto=registroSeleccionado[i].get('concepto');
				vector.importe=NS.formatNumber(registroSeleccionado[i].get('importe'));
				vector.noDocumento=registroSeleccionado[i].get('noDocumento');
				vector.usuario=registroSeleccionado[i].get('usuario');
				matriz[i] = vector;
	 		
			}
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		
		NS.exportaExcel(jSonString);
		NS.excel = false;
	
		return;
	};
	
	NS.exportaExcel = function(jsonCadena) {
		ConsultaSolicitudPagoSinPolizaAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=consultaSolicitudPagoSinPoliza';
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
		height: 70,
		layout: 'absolute',
		items:
		[
		 	NS.lbEmpresa,
			NS.lbFecIni,
			NS.lbFecFin,
			NS.lbUsuario,
			NS.txtFechaIni,
			NS.txtFechaFin,
			NS.txtNoEmpresa,
			NS.cmbUsuario,
			NS.cmbNoEmpresa,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 800,
		 		y: 20,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click:{
		 				fn: function(e){
		 					NS.opciones('buscar');
		 				}
		 			}
		 		}
		 	},
		 	
		]
	});
	
	NS.panelComponentes = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 80,
		width: 970,
		height: 400,
		layout: 'absolute',
		buttonAlign: 'right',
		autoScroll: true,
	    buttons:[
	  		{  text:'Excel',  handler:function(){ NS.Excel = true; NS.validaDatosExcel(); }  },
	    ],
		items: [NS.grid ]
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
		title: 'Consulta Solicitud de Pagos (Sin Poliza Contable)',
		width: 1005,
		height: 485,
		frame: true,
		padding: 10,
		//autoScroll: true,
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
 