/**
 * Luis Alfredo Serrato Montes de Oca
 */

Ext.onReady(function (){
	
	var NS = Ext.namespace('apps.SET.Impresion.Impresion.AplicacionDescuentosPropuestas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var vec = new Array();
	
	
	/*
	 * VARIABLE'S
	 */
	
	NS.totalPPago = 0;
	NS.totalPPagoProveedor = 0;
	NS.totalDesc = 0;
	NS.parametroBus = '';
	NS.importeSelectDescuento = 0;
	NS.cveControlPago = '';
	NS.cveControlDescuento = '';
	NS.folioDet = '';
	NS.vec = new Array();
	NS.vecTemp = new Array();
	NS.matDatos = new Object();
	//NS.clavesN = new Array();
	NS.proveedorSeleccionado = '';
	NS.indexProv = 0;
	NS.textoProv = '';
	NS.equivalePe = '';
	NS.noSumar = false;
	//NS.cont = 0;
	
	/*
	 * LABEL�S
	 */
	
	NS.empresaLbl = new Ext.form.Label({
		text: 'Grupo Empresas:',
		x:10,
		y:0
	});
	
	NS.proveedoresLbl = new Ext.form.Label({
		text: 'Proveedores:',
		x:310,
		y:0
	});
	
	NS.divisaLbl = new Ext.form.Label({
		text: 'Divisa:',
		x:680,
		y:0
	});
	
	NS.fechasLbl = new Ext.form.Label({
		text: 'Rango de Fechas:',
		x:10,
		y:50
	});
	
	NS.totalPPagosLbl = new Ext.form.Label({
		text: 'Total de Pago:',
		x:690,
		y:210
	});
	
	NS.totalPDescuentosProveedorLbl = new Ext.form.Label({
		text: 'Total por Proveedor:',
		x:665,
		y:515
	});
	
	NS.totalPDescuentosProveedoresLbl = new Ext.form.Label({
		text: 'Total por Proveedores:',
		x:655,
		y:545
	});
	
	/*
	 * TEXTBOX�S
	 */
	
	NS.txtIdEmpresa = new Ext.form.TextField({			
		id: PF + 'txtIdEmpresa',
		name: PF + 'txtIdEmpresa',
		x: 10,
		y: 18,
		width: 40,
		value: '',
        disabled: false,
        tabIndex: 1,
        listeners: {
    		change: {
    			fn:function(caja,valor) {
    				if(valor != null && valor != '' && valor != undefined){
    					BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdEmpresa', NS.cmbGrpEmpresa.getId());
    				}else{
    					NS.cmbGrpEmpresa.setValue('');
    				}
    				
    			}
    		}
        }
	
	});
	
	NS.proveedorTxt = new Ext.form.TextField({
		id: PF + 'proveedorTxt',
		name: PF + 'proveedorTxt',
		x: 310,
		y: 18,
		width: 65,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					BFwrk.Util.updateTextFieldToCombo(PF + 'proveedorTxt', PF + 'cmbProveedor');
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
        				NS.storeUnicoBeneficiario.baseParams.registroUnico=true;
        				NS.storeUnicoBeneficiario.baseParams.condicion=''+caja.getValue();
        				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando Datos..."});
        				NS.storeUnicoBeneficiario.load();
       				}else {
       					NS.cmbProveedor.reset();
       					NS.storeUnicoBeneficiario.removeAll();
       				}
				}
			}
		}
	});
	
	

	NS.totalPPagosTxt = new Ext.form.TextField({
		id: PF + 'totalPPagosTxt',
		name: PF + 'totalPPagosTxt',
		x: 765,
		y: 210,
		width: 180,
		readOnly: true,
		style: {
			textAlign: 'right',
        }
	});
	
	NS.totalPDescuentosProveedorTxt = new Ext.form.TextField({
		id: PF + 'totalPDescuentosProveedorTxt',
		name: PF + 'totalPDescuentosProveedorTxt',
		x: 765,
		y: 515,
		width: 180,
		readOnly: true,
		style: {
			textAlign: 'right',
        }
	});
	
	NS.totalPDescuentosProveedoresTxt = new Ext.form.TextField({
		id: PF + 'totalPDescuentosProveedoresTxt',
		name: PF + 'totalPDescuentosProveedoresTxt',
		x: 765,
		y: 545,
		width: 180,
		readOnly: true,
		style: {
			textAlign: 'right',
        }
	});
	
	
	/*
	 * DATEFIELD�S
	 */
	
	NS.txtFechaBusquedaIni = new Ext.form.DateField({
		id: PF + 'txtFechaBusquedaIni',
		name: PF + 'txtFechaBusquedaIni',
		format:'d/m/Y',
		x: 10,
		y: 68,
		width: 100
	});
	
	NS.txtFechaBusquedaFin = new Ext.form.DateField({
		id: PF + 'txtFechaBusquedaFin',
		name: PF + 'txtFechaBusquedaFin',
		format:'d/m/Y',
		x: 130,
		y: 68,
		width: 100
		
	});
	
	
	
	/*
	 * FUNCIONE�S
	 */
	//Quitar formato a las cantidades
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,'').replace('$',''); //num.replace(/([^0-9\.\-])/g,''*1);
	};
	
	NS.generarExcelHeader = function(){
		var seleccionPagos = NS.gridDatosPagos.getSelectionModel().getSelections();
		var seleccionDescuentos = NS.gridDatosDescuentos.getSelectionModel().getSelections();
		if(seleccionPagos.length > 0) {
			if(seleccionDescuentos.length>0){
				
			var cveControl = '';
			for(var k=0;k<seleccionPagos.length;k++){
				cveControl = cveControl +"'"+ seleccionPagos[k].get('cveControl')+"',";
			}
			
			var cveControlDesc='';
			for(var i=0;i<seleccionDescuentos.length;i++){
				cveControlDesc = cveControlDesc +"'"+ seleccionDescuentos[i].get('cveControl')+"',";
			}
			
			parametros='?nomReporte=excelHeader';
			parametros+='&nomParam1=claveP';
			parametros+='&valParam1=' + cveControl;
			parametros+='&nomParam2=claveD';
			parametros+='&valParam2=' + cveControlDesc;
			window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
		}else{
			Ext.Msg.alert('SET','Seleccionar una propuesta de descuentos')	
		}
		}else{
			Ext.Msg.alert('SET','Seleccionar una propuesta de pago.');
			return;
		}
	}
	
	NS.generarExcelDetalle = function(){
		var seleccionPagos = NS.gridDatosPagos.getSelectionModel().getSelections();
		var seleccionDescuentos = NS.gridDatosDescuentos.getSelectionModel().getSelections();
		if(seleccionPagos.length > 0) {
			if(seleccionDescuentos.length>0){
				
			var cveControl = '';
			for(var k=0;k<seleccionPagos.length;k=k+1){
				cveControl = cveControl +"'"+ seleccionPagos[k].get('cveControl')+"',";
			}
			
			var cveControlDesc='';
			for(var i=0;i<seleccionDescuentos.length;i++){
				cveControlDesc = cveControlDesc +"'"+ seleccionDescuentos[i].get('cveControl')+"',";
			}
			
			parametros='?nomReporte=excelDetalle';
			parametros+='&nomParam1=claveP';
			parametros+='&valParam1=' + cveControl;
			parametros+='&nomParam2=claveD';
			parametros+='&valParam2=' + cveControlDesc;
			window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
		}else{
			Ext.Msg.alert('SET','Seleccionar una propuesta de descuentos')	
		}
		}else{
			Ext.Msg.alert('SET','Seleccionar una propuesta de pago.');
			return;
		}
	}
	
	
	NS.calcularDescuentos = function(){
		var renglones = NS.gridDatosDetallesDescuentos.getSelectionModel().getSelections();
		var totalDescuentos = 0
		
		for(var i = 0; i < renglones.length ; i++){
			totalDescuentos = (totalDescuentos + renglones[i].get('importeNeto'));
		}
		return totalDescuentos.toFixed(2);
		
	}
	
	NS.cambiarFecha = function(fecha){
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
			if(mesArreglo[i] === mesDate)
			{
			mes = i+1;
				if(mes<10)
					mes='0'+mes;
			}
		}		
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	}
	
	NS.cambiarFormato = function(num,prefix){
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
	

	/*
	 * COMBO�S
	 */
	
	NS.storeLlenarComboGrpEmpresa = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {idUsuario: ''+NS.idUsuario},
		paramOrder: ['idUsuario'],
		root: '',
		directFn: AplicacionDescuentosPropuestasAction.llenarComboGrpEmpresas,
		fields:
		[
		    
		    {name: 'idStr'},
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'idStr',
		listeners:
		{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storeLlenarComboGrpEmpresa, msg: "Cargando Datos ..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay grupos de empresas que mostrar');
				}else{
					
				}
					
			}
		}

	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarProveedor, msg:"Cargando Datos..."});
	NS.storeLlenarComboGrpEmpresa.load();
	
	NS.cmbGrpEmpresa = new Ext.form.ComboBox({
		store: NS.storeLlenarComboGrpEmpresa,
		id: PF + 'cmbGrpEmpresa',
		name: PF + 'cmbGrpEmpresa',
		width: 210,
		x: 60,
		y: 18,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idStr',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'SELECCIONE EMPRESA',
		triggerAction: 'all',
		listeners:{
			select:{
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF + 'txtIdEmpresa', NS.cmbGrpEmpresa.getId());
				}
			}
			
	
		}
	});
	
	
	NS.storeLlenarProveedor = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'equivale_persona',
			campoDos:'',
			tabla:'persona',
			condicion:'',
			orden:'',
			registroUnico:false
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
		root: '',
		directFn: AplicacionDescuentosPropuestasAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen proveedores con ese nombre');
					Ext.getCmp(PF + 'proveedorTxt').reset();
					NS.cmbProveedor.reset();
				}
			}
		}
	});
	
	
	NS.cmbProveedor = new Ext.form.ComboBox({
		store: NS.storeLlenarProveedor,
		id: PF + 'cmbProveedor',
		name: PF + 'cmbProveedor',
		width: 210,
		x: 390,
		y: 18,
		typeAhead: true
		,mode: 'local'
		//,mode: 'remote'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		//,hideTrigger: true
        ,pageSize: 10,
        valueField: 'id',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'SELECCIONE PROVEEDOR',
		triggerAction: 'all'
		,value: ''
		,visible: false
		,editable: true
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'proveedorTxt', NS.cmbProveedor.getId());
				}
			},
			beforequery: function(qe){
				NS.parametroBus=qe.combo.getRawValue();
			}
		}
	});
	
	//Este store se carga segun la clave tecleada en la caja de texto
	NS.storeUnicoBeneficiario = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'equivale_persona',
			campoDos:'',
			tabla:'persona',
			condicion:'',
			orden:'',
			registroUnico:false
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
		root: '',
		directFn: AplicacionDescuentosPropuestasAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existe el beneficiario con esa clave');
					Ext.getCmp(PF + 'proveedorTxt').reset();
					NS.cmbProveedor.reset();
					return;
				}
				else{
					var reg = NS.storeUnicoBeneficiario.data.items;
					Ext.getCmp(NS.cmbProveedor.getId()).setValue(reg[0].get('descripcion'));
					NS.accionarUnicoBeneficiario(reg[0].get('id'));
				}
			}
		}
	}); 
	
	NS.accionarUnicoBeneficiario = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion');
 	}
	
 	NS.accionarBeneficiario = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeProveedor.getById(valueCombo).get('descripcion');
 	}
	
	
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultaPagosPendientesAction.obtenerDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
			 {name: 'idDivisaSoin'},
			 {name: 'clasificacion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
				if(records.length==null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen divisas');
				}
			}
		}
	}); 
    
	NS.storeDivisa.load();
	
	NS.cmbDivisa = new Ext.form.ComboBox({
		store:  NS.storeDivisa ,
		id: PF + 'cmbDivisa',
		name: PF + 'cmbDivisa',
		width: 105,
		x: 680,
		y: 18,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'DIVISA',
		triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor){
					

				}
			}
			
		}
	});
	
	/*
	 * GRID�S
	 */
	
	NS.storeLlenarGridPagos = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {json: ''},
		paramOrder: ['json'],
		root: '',
		directFn: AplicacionDescuentosPropuestasAction.obtenerPagos,
		fields:
		[
		 	{name: 'cveControl'},
		 	{name: 'beneficiario'},
		    {name: 'texto'},
		    {name: 'total'},
		 	{name: 'fechaPropuesta'}
		],
		listeners:
		{
			load: function(s, records)
			{
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay datos que mostrar');
				}
				
			}
		}
	});
	
	NS.seleccionGridPagos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false,
		listeners:{
			rowselect: {
				fn:function(r, rowIndex){
					var matP = new Array();
					var seleccionesP = NS.gridDatosPagos.getSelectionModel().getSelections(); 
           		 	if(seleccionesP.length > 0){
           			 for(var i = 0; i < seleccionesP.length; i++ ){
           					 var vecTempP ={};
            				 vecTempP.cveControlPago = seleccionesP[i].get('cveControl');
            				 matP[i] = vecTempP;
            				
           			}
					}
           		 	var jsonClaves = Ext.util.JSON.encode(matP);
					var registrosGrid = NS.gridDatosPagos.getSelectionModel().getSelections();
					var totalP=0;
          		 	 for(var i = 0; i < registrosGrid.length; i++){
          		 		totalP+=registrosGrid[i].get('total');
          		 		
          		    }
          		 	var totalF=Math.round((totalP)*100)/100;
          		 	NS.totalPPagosTxt.setValue(NS.cambiarFormato(totalF, '$'));
          		 	//NS.totalPDescuentosProveedoresTxt.setValue(NS.cambiarFormato(Math.round((totalP)*100)/100), '$');
					NS.storeLlenarGridDescuentos.baseParams.cveControl = '' + jsonClaves;
	        		if(NS.cmbGrpEmpresa.getValue()=='' && NS.txtIdEmpresa.getValue()==''){
							return Ext.Msg.alert('SET', 'Debe seleccionar un Grupo Empresas');
					}else{
						NS.storeLlenarGridDescuentos.baseParams.noEmpresa=NS.txtIdEmpresa.getValue();
					} 
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridDescuentos, msg: "Cargando Datos ..."});
					NS.storeLlenarGridDescuentos.load();
						
				}
			},
			rowdeselect: {
				fn:function(r, rowIndex){
					var matP = new Array();
					var seleccionesP = NS.gridDatosPagos.getSelectionModel().getSelections(); 
           		 	if(seleccionesP.length > 0){
           		 	for(var i = 0; i < seleccionesP.length; i++ ){
          					 var vecTempP ={};
           				 vecTempP.cveControlPago = seleccionesP[i].get('cveControl');
           				 matP[i] = vecTempP;
          			}
					}
           		 	var jsonClaves = Ext.util.JSON.encode(matP);
					var registrosGrid = NS.gridDatosPagos.getSelectionModel().getSelections();
					var totalP=0;
          		 	 for(var i = 0; i < registrosGrid.length; i++){
          		 		totalP+=registrosGrid[i].get('total');
          		 		
          		    }
          		 	//NS.totalPPagosTxt.setValue(NS.cambiarFormato(totalP, '$'));
          		 	var totalF=Math.round((totalP)*100)/100;
          		 	NS.totalPPagosTxt.setValue(NS.cambiarFormato(totalF, '$'));
          		 	//NS.totalPPagosTxt.setValue(NS.cambiarFormato(Math.round((totalP)*100)/100), '$');
					NS.storeLlenarGridDescuentos.baseParams.cveControl = '' + jsonClaves;
					NS.storeLlenarGridDescuentos.baseParams.texto='si';
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridDescuentos, msg: "Cargando Datos ..."});
					NS.storeLlenarGridDescuentos.load();
				}
			}
		}
	});
	
	NS.columnasDatosPagos = new Ext.grid.ColumnModel([
	NS.seleccionGridPagos,
	  {header: 'CVE. PROPUESTA', width: 120, dataIndex:'cveControl', align:'left',filter: {testNumPago: "/^{0}/i"}},
	  {header: 'GRUPO EMPRESA', width: 200, dataIndex: 'beneficiario', align:'left'},
	  {header: 'CONCEPTO', width: 120, dataIndex: 'texto', align:'left'},	
	  {header: 'IMPORTE', width: 120, dataIndex: 'total', renderer: BFwrk.Util.rendererMoney ,align:'right',filter: {testImporte: "/^{0}/i"}},
	  {header: 'FECHA PAGO', width: 120, dataIndex: 'fechaPropuesta', align:'center',filter: {testFechaPago: "/^{0}/i"}}
    ]);
	
	NS.gridDatosPagos = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridPagos,
		id: PF + 'gridDatosPagos',
		title: 'Propuestas Facturas',
		x: 480,
		y: 5,
		cm: NS.columnasDatosPagos,
		sm: NS.seleccionGridPagos,
		lockColumns: 3, 
		width: 470,
		height: 200,
		frame: true,
		border: false,
		stripeRows : true,
		columnLines: true,
		listeners:{
			rowClick:{
				fn:function(grid){
		
				}
			}
		}
	});
	
	
	NS.storeLlenarGridDetallePagos = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {cveControl: '', cveControlD: ''},
		paramOrder: ['cveControl', 'cveControlD'],
		root: '',
		directFn: AplicacionDescuentosPropuestasAction.obtenerDetallePagos,
		fields:
		[
		 	//{name: 'noFact'},
		 	{name: 'equivalePersona'},
		 	{name: 'nombreEmpresa'},
		 	{name: 'beneficiario'},
		    {name: 'importeNeto'},
		    {name: 'importeOriginal'},
		    {name: 'numeroEmpresa'},
		    //{name: 'nomEmpresa'}
//		    {name: 'texto'},
//		 	{name: 'formaPago'},
//		 	{name: 'fecValor'},
//		 	{name: 'fechaPropuesta'},
//		 	{name: 'estatus'}
		],
		listeners:
		{
			load: function(s, records)
			{

				var totalP=0; 
      		 	 for(var i = 0; i < records.length; i++){
      		 		totalP+=records[i].get('importeNeto');
      		 		
      		    }
      		 	var totalF=Math.round((totalP)*100)/100;
      		 	//NS.totalPDescuentosProveedoresTxt.setValue(NS.cambiarFormato(totalF), '$');
      		 	//NS.totalPDescuentosProveedoresTxt.setValue('$'+totalF);	
      		 	NS.totalPDescuentosProveedoresTxt.setValue('$'+NS.cambiarFormato(totalF),'$');
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay datos que mostrar');
				}
				
			}
		}
	});

	NS.seleccionGridDetallesPagos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true,
		listeners:{
			beforerowselect:{
				fn:function(r, rowIndex, keepExisting, record){
					/*if(NS.totalDesc < 0){
						Ext.Msg.alert('SET', 'El importe del proveedor no debe quedar negativo');
						return false;
					}*/
				}
			},
			rowselect: {
				fn:function(r, rowIndex){
					NS.indexProv = rowIndex;
					NS.textoProv = NS.gridDatosDetallesPagos.getSelectionModel().getSelected().get('beneficiario');
					NS.equivalePe = NS.gridDatosDetallesPagos.getSelectionModel().getSelected().get('equivalePersona');
				}
			}
		}
		
	});
	
	
	NS.columnasDetallesDatosPagos = new Ext.grid.ColumnModel([
	                                                                                        
	  {header: 'EQUIVALE PERSONA', width: 150, dataIndex: 'equivalePersona',align:'right'},
	  {header: 'PROVEEDOR', width: 250, dataIndex: 'beneficiario', align:'left',filter: {testProveedor: "/^{0}/i"}},
	  {header: 'IMPORTE', width: 150, dataIndex: 'importeNeto', renderer: BFwrk.Util.rendererMoney ,align:'right',filter: {testImporte: "/^{0}/i"}},
	  {header: 'IMPORTE ORIGINAL', width: 150, dataIndex: 'importeOriginal', renderer: BFwrk.Util.rendererMoney ,align:'right', hidden: true},
	  {header: 'NUMERO DE EMPRESA', width: 150, dataIndex: 'numeroEmpresa',align:'right'},
	  {header: 'NOMBRE EMPRESA', width: 120, dataIndex:'nombreEmpresa', align:'left'}
	 
    ]);
	
	NS.gridDatosDetallesPagos = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridDetallePagos,
		id: PF + 'gridDatosDetallesPagos',
		title: 'Detalles de Facturas',
		x: 480,
		y: 275,
		cm: NS.columnasDetallesDatosPagos,
		sm: NS.seleccionGridDetallesPagos,
		lockColumns: 3, 
		width: 470,
		height: 230,
		frame: true,
		border: false,
		stripeRows: true,
		listeners:{
			rowdblClick:{
				fn:function(grid){
					
				}
			},
			rowClick:{
				fn:function(grid){
					var selecciones = NS.gridDatosDetallesDescuentos.getSelectionModel().getSelections(); 
					if(selecciones.length > 0){
						NS.vecTemp[NS.proveedorSeleccionado] = NS.vec;
						for(var i = 0; i < selecciones.length; i++ ){
							var vecTempD = {};
							vecTempD.folioDet = selecciones[i].get('noFact');
							NS.matDatos['' + selecciones[i].get('noFact')] = vecTempD;
							console.log(NS.matDatos);
							
						}
					}else{
						delete NS.vecTemp[NS.proveedorSeleccionado];
						//Ext.Msg.alert('SET', 'No se han seleccionado descuentos');
					}	    	 
					NS.proveedorSeleccionado = '' + grid.getSelectionModel().getSelected().get('equivalePersona');
					NS.totalPPagoProveedor = grid.getSelectionModel().getSelected().get('importeOriginal');
					NS.totalPDescuentosProveedorTxt.setValue(NS.cambiarFormato(NS.totalPPagoProveedor, '$'));
					NS.vec = new Array();
					NS.storeLlenarGridDetalleDescuentos.baseParams.cveControl = NS.jsonD;
					NS.storeLlenarGridDetalleDescuentos.baseParams.ePersona = grid.getSelectionModel().getSelected().get('equivalePersona');
					NS.storeLlenarGridDetalleDescuentos.baseParams.numeroEmpresa = '' + grid.getSelectionModel().getSelected().get('numeroEmpresa');
					//NS.noSumar = true;
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridDetalleDescuentos, msg: "Cargando Datos ..."});
					NS.storeLlenarGridDetalleDescuentos.load();
					
				}
			}
		}
      
	});
	
	
	NS.storeLlenarGridDescuentos = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {cveControl: '', noEmpresa: ''},
		paramOrder: ['cveControl','noEmpresa'],
		root: '',
		directFn: AplicacionDescuentosPropuestasAction.obtenerDescuentos,
		fields:
		[
		 	{name: 'cveControl'},
		 	{name: 'beneficiario'},
		    {name: 'texto'},
		    {name: 'total'},
		 	{name: 'fechaPropuesta'}
		],
		listeners:
		{
			load: function(s, records)
			{
				if(records.length == null || records.length <= 0){
					NS.storeLlenarGridDescuentos.removeAll();
	    			NS.gridDatosDescuentos.getView().refresh;
				}
				//var selecciones = NS.gridDatosPagos.getSelectionModel().getSelections();
				/*if(selecciones.length > 0){
					NS.storeLlenarGridDescuentos.removeAll();
	    			NS.gridDatosDescuentos.getView().refresh;
					for(var i = 0; i<selecciones.length;i++){
						NS.clavesN[NS.cont]=selecciones[i].get('cveControl');
						NS.cont++;
					}
				}else{
					NS.storeLlenarGridDescuentos.removeAll();
	    			NS.gridDatosDescuentos.getView().refresh;
	    			if(records == null || records.length == 0 ){
	    				Ext.Msg.alert('SET', 'No hay descuentos para ese proveedorl '+records.length);
	    			}else{
	    				Ext.Msg.alert('SET', 'si hay descuentos para ese proveedorr '+records.length);
	    			}
				//}}
	    		*/
				}
		}
	});

	NS.seleccionGridDescuentos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false,
		listeners:{
			rowselect: {
				fn:function(r, rowIndex){
				
				}
			},
			rowdeselect: {
				fn:function(r, rowIndex){
				
				}
			}
		}
	});
	
	
	NS.columnasDatosDescuentos = new Ext.grid.ColumnModel([
	 NS.seleccionGridDescuentos,                                                   
	  {header: 'CVE. PROPUESTA', width: 120, dataIndex:'cveControl', align:'left',filter: {testNumPago: "/^{0}/i"}},
	  {header: 'GRUPO_EMPRESA', width: 200, dataIndex: 'beneficiario', align:'left'},
	  {header: 'CONCEPTO', width: 120, dataIndex: 'texto', align:'left'},	
	  {header: 'IMPORTE', width: 120, dataIndex: 'total', renderer: BFwrk.Util.rendererMoney ,align:'right',filter: {testImporte: "/^{0}/i"}},
	  {header: 'FECHA_PAGO', width: 120, dataIndex: 'fechaPropuesta', align:'center',filter: {testFechaPago: "/^{0}/i"}}
    ]);
	
	NS.gridDatosDescuentos = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridDescuentos,
		id: PF + 'gridDatosDescuentos',
		title: 'Propuestas Descuentos',
		x: 0,
		y: 5,
		cm: NS.columnasDatosDescuentos,
		sm: NS.seleccionGridDescuentos,
		lockColumns: 3, 
		width: 470,
		height: 200,
		frame: true,
		border: false,
		stripeRows: true,
		columnLines: true,
		listeners:{
			rowClick:{
				fn:function(grid){
					
					//NS.cveControlDescuento = grid.getSelectionModel().getSelected().get('cveControl'); 
				}
			}
		}
      
	});
	

	
	NS.storeLlenarGridDetalleDescuentos = new Ext.data.DirectStore({
		paramsAsHash: true,
		baseParams: {cveControl: '', ePersona: '', numeroEmpresa: ''},
		paramOrder: ['cveControl', 'ePersona','numeroEmpresa'],
		root: '',
		directFn: AplicacionDescuentosPropuestasAction.obtenerDescuentosDet,
		fields:
		[
		 	{name: 'noFact'},
		 	{name: 'nombreEmpresa'},
		 	{name: 'cveControl'},
		 	{name: 'beneficiario'},
		    {name: 'importeNeto'},
		    {name: 'texto'},
		 	{name: 'formaPago'},
		 	{name: 'fecValor'},
		 	{name: 'fechaPropuesta'}
		],
		listeners:
		{
			load: function(s, records)
			{
				
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay detalles de descuentos que mostrar');
				}else{
					//if(NS.noSumar){
						if(NS.vecTemp[NS.proveedorSeleccionado] != '' && NS.vecTemp[NS.proveedorSeleccionado] != null && NS.vecTemp[NS.proveedorSeleccionado] != undefined){
							NS.gridDatosDetallesDescuentos.getSelectionModel().selectRows(NS.vecTemp[NS.proveedorSeleccionado]);
						}
					/*NS.noSumar=false;
					}else{
					NS.noSumar=false;
					}*/
					
				}
				
			}
		}
	});

	NS.seleccionGridDetallesDescuentos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false,
		listeners:{
			rowselect: {
				fn:function(r, rowIndex,record){
					NS.vec[''+rowIndex] = rowIndex;
					NS.totalDesc = 0;
					NS.totalDesc = (NS.totalPPagoProveedor - (NS.calcularDescuentos()));
					NS.totalPDescuentosProveedorTxt.setValue(NS.cambiarFormato(NS.totalDesc.toFixed(2), '$'));
					//Aqui se fija el importe
					NS.gridDatosDetallesPagos.getSelectionModel().getSelections()[0].set('importeNeto',(NS.totalDesc.toFixed(2)));
					var totalP=0; 
					var registrosGrid = NS.gridDatosDetallesPagos.store.data.items;
	      		 	 for(var i = 0; i < registrosGrid.length; i++){
	      		 		totalP=totalP+parseFloat(registrosGrid[i].get('importeNeto'));	
	      		    }
	      		 	var totalF=Math.round((totalP)*100)/100;
	      		 	NS.totalPDescuentosProveedoresTxt.setValue(NS.cambiarFormato(totalF,'$'));
	      		 	
				}
			},
			rowdeselect: {
				fn:function(selectionModel, rowIndex, record){
	
					if(NS.gridDatosDetallesDescuentos.getSelectionModel().getSelections().length < 1){
						NS.vec = new Array();
					}else{
						delete NS.vec[''+rowIndex];
						NS.vec.splice(rowIndex, rowIndex);
					}
					
					if(NS.matDatos['' + NS.storeLlenarGridDetalleDescuentos.data.items[rowIndex].get('noFact')] != undefined){
						delete NS.matDatos['' + NS.storeLlenarGridDetalleDescuentos.data.items[rowIndex].get('noFact')];
					}
					NS.totalDesc = (parseFloat(NS.totalPPagoProveedor) - parseFloat(NS.calcularDescuentos()));
					NS.totalPDescuentosProveedorTxt.setValue(NS.cambiarFormato(NS.totalDesc.toFixed(2), '$'));
					//Aqui se fija el importe
					NS.gridDatosDetallesPagos.getSelectionModel().getSelections()[0].set('importeNeto',(NS.totalDesc.toFixed(2)));		
					
					var totalP=0; 
					var registrosGrid = NS.gridDatosDetallesPagos.store.data.items;
	      		 	 for(var i = 0; i < registrosGrid.length; i++){
	      		 		totalP=totalP+parseFloat(registrosGrid[i].get('importeNeto'));	
	      		    }
	      		 	var totalF=Math.round((totalP)*100)/100;
	      		 	NS.totalPDescuentosProveedoresTxt.setValue(NS.cambiarFormato(totalF,'$'));
	      		 	//NS.totalPDescuentosProveedoresTxt.setValue(NS.cambiarFormato(Math.round((totalP)*100)/100), '$');
				}
			}
		}
	});
	
	
	NS.columnasDetallesDatosDescuentos = new Ext.grid.ColumnModel([
	  NS.seleccionGridDetallesDescuentos,                                                               
	  {header: 'FOLIO_SET', width: 120, dataIndex:'noFact', align:'center', hidden: true},
	  {header: 'E_PERSONA', width: 120, dataIndex:'nombreEmpresa', align:'center', hidden: true},
	  {header: 'CVE. PROPUESTA', width: 120, dataIndex: 'cveControl', align:'left'},
	  {header: 'PROVEEDOR', width: 200, dataIndex: 'beneficiario', align:'left'},
	  {header: 'IMPORTE', width: 120, dataIndex: 'importeNeto', renderer: BFwrk.Util.rendererMoney ,align:'right',filter:{testImporte: "/^{0}/i"}},
	  {header: 'CONCEPTO', width: 200, dataIndex: 'texto', align:'left'},
	  {header: 'FORMA_PAGO', width: 120, dataIndex: 'formaPago', align:'left'},
	  {header: 'FECHA_PAGO', width: 120, dataIndex: 'fechaPropuesta', align:'center',filter:{testFechaPago: "/^{0}/i"}},
	  {header: 'FECHA_VENCIMIENTO', width: 120, dataIndex: 'fecValor', align:'center',filter:{testFechaVen: "/^{0}/i"}}
	  
	  
	  
	  
    ]);
	
	NS.gridDatosDetallesDescuentos = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridDetalleDescuentos,
		id: PF + 'gridDatosDetallesDescuentos',
		title: 'Detalles de Descuentos',
		x: 0,
		y: 275,
		cm: NS.columnasDetallesDatosDescuentos,
		sm: NS.seleccionGridDetallesDescuentos,
		lockColumns: 3, 
		width: 470,
		height: 230,
		frame: true,
		border: false,
		stripeRows: true,
		listeners:{
			rowClick:{
				fn:function(grid){
				
										
				}
			}
		}
      
	});

	
	
	/*
	 * PANEL�S
	 */
	
	
	
	 NS.PanelContenedorCentral = new Ext.form.FieldSet({
	    	id: PF + 'PanelContenedorCentral',
	    	layout: 'absolute',
	    	width: 978,
	    	height: 630,
	    	x: 0,
	    	y: 190,
	    	items:[
	    	       NS.gridDatosPagos,
	    	       NS.gridDatosDetallesPagos,
	    	       NS.gridDatosDescuentos,
	    	       NS.gridDatosDetallesDescuentos,
	    	       NS.totalPPagosTxt,
	    	       NS.totalPPagosLbl,
	    	       NS.totalPDescuentosProveedorTxt,
	    	       NS.totalPDescuentosProveedorLbl,
	    	       NS.totalPDescuentosProveedoresLbl,
	    	       NS.totalPDescuentosProveedoresTxt,
	    	       {
	    	    	   xtype: 'button',
	    	    	   id: PF + 'btnExcel',
	    	    	   text: 'Detalle Propuestas',
	    	    	   width: 110,
	    	    	   x: 410,
	    	    	   y: 235,
	    	    	   handler: function(){
	    	    		   NS.generarExcelHeader();
	    	    		   NS.generarExcelDetalle();    	    		   
	    	    	   }
	    	       },
	    	       {
	    	    	   xtype: 'button',
	    	    	   id: PF + 'btnRetorarDescuento',
	    	    	   text: '<',
	    	    	   width: 30,
	    	    	   x: 500,
	    	    	   y: 235,
	    	    	   hidden: true,
	    	    	   handler: function(){
	    	    		   
	    	    		   var regSelect = NS.gridDatosDetallesPagos.getSelectionModel().getSelected();
	    	    		   var datosClase = NS.gridDatosDetallesPagos.getStore().recordType;
	    	    		   var recordsDatGrid = NS.storeLlenarGridDetalleDescuentos.data.items;
	    	    		   
	    	    		   var datos = new datosClase({
	    	    			   	noFact: regSelect.get('noFact'),
	    	    			 	nombreEmpresa: regSelect.get('nombreEmpresa'),
	    	    			 	beneficiario: regSelect.get('beneficiario'),
	    	    			    importeNeto: regSelect.get('importeNeto'),
	    	    			    texto: regSelect.get('texto'),
	    	    			 	formaPago: regSelect.get('formaPago'),
	    	    			 	fecValor: regSelect.get('fecValor'),
	    	    			 	fechaPropuesta: regSelect.get('fechaPropuesta')
	    	    		   });
	    	    		   
	    	    		   NS.storeLlenarGridDetallePagos.remove(regSelect);
	    	    		   NS.gridDatosDetallesPagos.getView().refresh;
	    	    		   
	    	    		   NS.storeLlenarGridDetalleDescuentos.insert(recordsDatGrid.length, datos);
	    	    		   NS.gridDatosDetallesDescuentos.getView().refresh;
	    	    		   
	    	    		   //delete NS.vecTemp[''+regSelect.get('noFact')];
	    	    		   delete NS.matDatos[''+regSelect.get('noFact')];
	    	    		   Ext.getCmp(PF + 'btnRetorarDescuento').setDisabled(true);
	    	    		   //console.log(NS.vecTemp);
	    	    		   console.log(NS.matDatos);
	    	    	   }
	    	       }
	    	       
            ],
            buttons:[
                     {
                    	 text: 'Aceptar',
                    	 handler: function(){
                    		 
                    		 var registrosGrid = NS.gridDatosDetallesPagos.store.data.items;
                    		 for(var i = 0; i < registrosGrid.length; i++){
                    			 NS.importes=parseFloat(NS.gridDatosDetallesPagos.store.data.items[i].get("importeNeto"));
                    			 if(NS.importes<0){
                    				 return Ext.Msg.alert('SET','Hay pagos con importe negativo');
                    			 }
                    		 }
                    		 var selecciones = NS.gridDatosDetallesDescuentos.getSelectionModel().getSelections(); 
                    		 if(selecciones.length > 0){
                    			 for(var i = 0; i < selecciones.length; i++ ){
                    				 var vecTempD = {};
                    				 vecTempD.folioDet = selecciones[i].get('noFact');	
                    				 NS.matDatos['' + selecciones[i].get('noFact')] = vecTempD;
         						}
         					}

                    		 var arrayAux =  $.map(NS.matDatos, function(value, index) {
                    			 return [value];
                    		 });
                    		 var json = Ext.util.JSON.encode(jQuery.makeArray(arrayAux));
                    		 
                    		 console.log(json);
                    		 
                    		 
    		        	
                    		var mat = new Array();
                    		var selecciones = NS.gridDatosPagos.getSelectionModel().getSelections(); 
                    		if(selecciones.length > 0){
                    			for(var i = 0; i < selecciones.length; i++ ){
                      				 var vecTempP ={};
                       				 vecTempP.cveControlPago = selecciones[i].get('cveControl');
                       				 mat[i] = vecTempP;
                       				
                      			}
             				}
                        	var jsonClaves = Ext.util.JSON.encode(mat);
                    		AplicacionDescuentosPropuestasAction.aplicarDescuentoSimple(json, NS.cveControlPago, NS.jsonD,jsonClaves, function(resultado, e) {
                    			 if(resultado!= '' && resultado!= undefined && resultado!= null){
	    	    				   Ext.Msg.alert('SET', resultado);
                    			 }
		    	    			   NS.storeLlenarGridPagos.removeAll();
		    	    			   NS.gridDatosPagos.getView().refresh;
		    	    			   
		    	    			   NS.storeLlenarGridDescuentos.removeAll();
		    	    			   NS.gridDatosDescuentos.getView().refresh;
		    	    			   
		    	    			   NS.storeLlenarGridDetalleDescuentos.removeAll();
		    	    			   NS.gridDatosDetallesDescuentos.getView().refresh;
		    	    			   
		    	    			   NS.storeLlenarGridDetallePagos.removeAll();
		    	    			   NS.gridDatosDetallesPagos.getView().refresh
	 	    						

		    	    			   NS.totalPPagosTxt.setValue('');
		    	    			   NS.totalPDescuentosProveedorTxt.setValue('');
		    	    			   NS.totalPDescuentosProveedoresTxt.setValue('');
		    	    			   NS.totalPPago = 0;
		    	    			   NS.totalPPagoProveedor = 0;
		    	    			   NS.totalDesc = 0;
		    	    			   NS.parametroBus = '';
		    	    			   NS.importeSelectDescuento = 0;
		    	    			   NS.cveControlPago = '';
		    	    			   NS.cveControlDescuento = '';
		    	    			   NS.folioDet = '';
		    	    			   NS.vec = new Array();
		    	    			   NS.vecTemp = new Array();
		    	    			   NS.matDatos = new Object();
		    	    			   NS.proveedorSeleccionado = '';
		    	    			   NS.indexProv = 0;
		    	    			   NS.textoProv = '';
		    	    			   NS.equivalePe = '';
		    	    			   
		    	    			   NS.totalPPagosTxt.setValue('');	
		    	    			   NS.totalPDescuentosProveedorTxt.setValue('');
		    	    			   NS.clavesN = new Array();
		    	    			   NS.cont = 0;
		    	    			   
		    	    			   NS.txtIdEmpresa.setValue('');
		    	    			   NS.proveedorTxt.setValue('');
		    	    			   NS.txtFechaBusquedaIni.setValue('');
		    	    			   NS.txtFechaBusquedaFin.setValue('');
		    	    			   NS.cmbGrpEmpresa.reset();
		    	    			   NS.cmbProveedor.reset();
		    	    			   NS.cmbDivisa.reset();
                    		 });                   			 
                    	 }
                     },
                     {
                    	 text:'Cancelar',
                    	 handler: function(){
                    			//NS.vecTemp = new Object();
                    			NS.matDatos = new Object();
                    			
                    			NS.storeLlenarGridPagos.removeAll();
		    	    			NS.gridDatosPagos.getView().refresh;
 	    						
 	    	    			   	NS.storeLlenarGridDetalleDescuentos.removeAll();
 	    						NS.gridDatosDetallesDescuentos.getView().refresh;
 	    						
 	    						NS.storeLlenarGridDescuentos.removeAll();
 	    						NS.gridDatosDescuentos.getView().refresh;
 	    						
 	    						NS.storeLlenarGridDetallePagos.removeAll();
 	    						NS.gridDatosDetallesPagos.getView().refresh;
 	    	    			   	
 	    	    			   	
 	    						NS.totalPPagosTxt.setValue('');
 	    						NS.totalPDescuentosProveedorTxt.setValue('');
 	    						NS.totalPDescuentosProveedoresTxt.setValue('');
 	    						NS.totalPPago = 0;
 	    						NS.totalPPagoProveedor = 0;
 	    						NS.totalDesc = 0;
 	    						NS.parametroBus = '';
 	    						NS.importeSelectDescuento = 0;
 	    						NS.cveControlPago = '';
 	    						NS.cveControlDescuento = '';
 	    						NS.folioDet = '';
 	    						NS.vec = new Array();
 	    						NS.vecTemp = new Array();
 	    						NS.matDatos = new Object();
 	    						NS.proveedorSeleccionado = '';
 	    						NS.indexProv = 0;
 	    						NS.textoProv = '';
 	    						NS.equivalePe = '';
	    	    			   

 	    						NS.txtIdEmpresa.setValue('');
 	    						NS.proveedorTxt.setValue('');
 	    						NS.txtFechaBusquedaIni.setValue('');
 	    						NS.txtFechaBusquedaFin.setValue('');
 	    						NS.cmbGrpEmpresa.reset();
 	    						NS.cmbProveedor.reset();
 	    						NS.cmbDivisa.reset();
                    	 }
                     }
            ]
	 });
	
	NS.PanelContenedorCriteriosBusqueda = new Ext.form.FieldSet({
		title: 'BUSQUEDA',
		id: PF + 'PanelContenedorCriteriosBusqueda',
		layout: 'absolute',
		width: 978,
		height: 165,
		x: 0,
		y: 0,
		items:[
		       NS.empresaLbl,
		       NS.txtIdEmpresa,
		       NS.cmbGrpEmpresa,
		       NS.proveedoresLbl,
		       NS.proveedorTxt,
		       NS.cmbProveedor,
		       NS.divisaLbl,
		       NS.cmbDivisa,
		       NS.fechasLbl,
		       NS.txtFechaBusquedaIni,
		       NS.txtFechaBusquedaFin,
		       {
		    	   xtype: 'button',
		    	   text: '...',
		    	   x: 610,
		   		   y: 18,
		   		   width: 20,
		    	   handler: function(){
		    		   	if(NS.parametroBus==='' || (isNaN(NS.parametroBus) && NS.parametroBus.length < 4)) {
               				Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
	               		} else{
	               			NS.storeLlenarProveedor.baseParams.condicion=NS.parametroBus;
	               			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarProveedor, msg:"Cargando..."});
	               			NS.storeLlenarProveedor.load();
	               		}
		    	   }
		       }
		],
		buttons:[
		         {
		        	 text: 'Limpiar',
		        	 handler: function(){
		        		NS.txtIdEmpresa.setValue('');
  						NS.proveedorTxt.setValue('');
  						NS.txtFechaBusquedaIni.setValue('');
  						NS.txtFechaBusquedaFin.setValue('');
  						NS.cmbGrpEmpresa.reset();
  						NS.cmbProveedor.reset();
  						NS.cmbDivisa.reset();
		        		
		        	 }
		         },
		         {
		        	 text: 'Aplicacion Automatica',
		        	 handler: function(){
		        		 var tamDesc = NS.gridDatosDescuentos.getSelectionModel().getSelections().length;
		        		 if(tamDesc > 0){
		        			
		        			 var mat = new Array();
		        			 var selecciones = NS.gridDatosPagos.getSelectionModel().getSelections(); 
                    		 if(selecciones.length > 0){
                    			 for(var i = 0; i < selecciones.length; i++ ){
                      				 //if(NS.cont>0){
                      				//if(NS.clavesN.indexOf(seleccionesP[i].get('cveControl'))<=-1){
                      				 var vecTempP ={};
                       				 vecTempP.cveControlPago = selecciones[i].get('cveControl');
                       				 mat[i] = vecTempP;
                       				 //}
                       				 //}else{
                      					//var vecTempP ={};
                      					//vecTempP.cveControlPago = seleccionesP[i].get('cveControl');
                      					//matP[i] = vecTempP;
                      					// }
                      			}
         					}
                    		var json = Ext.util.JSON.encode(mat);
                    		
                    		//NS.cveControlDescuento = grid.getSelectionModel().getSelected().get('cveControl');
        					var matD = new Array();
        					var seleccionesD = NS.gridDatosDescuentos.getSelectionModel().getSelections()
                   		 	if(seleccionesD.length > 0){
                   			 for(var i = 0; i < seleccionesD.length; i++ ){
                   					 var vecTempD ={};
                    				 vecTempD.cveControlDesc = seleccionesD[i].get('cveControl');
                    				 matD[i] = vecTempD;
                    				
                   			}
        					}
        					NS.jsonD = Ext.util.JSON.encode(matD);
        			
        					
		        		 	NS.storeLlenarGridDetallePagos.baseParams.cveControl = json;
		        		 	NS.storeLlenarGridDetallePagos.baseParams.cveControlD = NS.jsonD;
		        		 	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridDetallePagos, msg: "Cargando Datos ..."});
		        		 	NS.storeLlenarGridDetallePagos.load();
		        		 	
		        		 	NS.storeLlenarGridDetalleDescuentos.removeAll();
	  	    			   	NS.gridDatosDetallesDescuentos.getView().refresh;
	  	    			    NS.totalPDescuentosProveedorTxt.setValue('');
	  	    			    NS.totalPDescuentosProveedoresTxt.setValue('');
	  	    			    NS.vec = new Array();
	  	    			    NS.vecTemp = new Array();
	  	    			    NS.matDatos = new Object();
   	    			   
		        		 }else{
		        			 Ext.Msg.alert('SET', 'Debe seleccionar un descuento');
		        		 }
		        	 }
		         },
		         {
		        	 text: 'Buscar',
		        	 handler: function(){
		        		 //NS.storeLlenarGridPagos.clearFilter();
		        		 if(NS.cmbGrpEmpresa.getValue()=='' && NS.txtIdEmpresa.getValue()==''){
							return Ext.Msg.alert('SET', 'Debe seleccionar un Grupo Empresas');
						 }
		        		 var json = '[{';
		        		 if(NS.cmbGrpEmpresa.getValue() != ''){
		        			 json = json + '"empresa":"' + NS.txtIdEmpresa.getValue() + '",';
		        		 }else{
		        			 json = json + '"empresa":"",'
		        		 }if(NS.cmbProveedor.getValue() != ''){
		        			 json = json + '"ePersona":"' + NS.cmbProveedor.getValue() + '",';
		        		 }else{
		        			 json = json + '"ePersona":"",';
		        		 }if(NS.cmbDivisa.getValue() != ''){
		        			 json = json + '"divisa":"' + NS.cmbDivisa.getValue() + '",';
		        		 }else{
		        			 json = json + '"divisa":"",';
		        		 }if(NS.txtFechaBusquedaIni.getValue() != '' && NS.txtFechaBusquedaFin.getValue() != ''
		        			 	&& NS.txtFechaBusquedaIni.getValue() != undefined && NS.txtFechaBusquedaFin.getValue() != undefined){
		        			 
		        			 json = json + '"fIni":"' + NS.cambiarFecha('' + NS.txtFechaBusquedaIni.getValue()) + '","fFin":"' + NS.cambiarFecha('' + NS.txtFechaBusquedaFin.getValue()) + '"' ;
		        		 }else{
		        			 json = json + '"fIni":"", "fFin":""';
		        		 }
		        		 
		        		 json = json + '}]';
		        		 
		        		 NS.storeLlenarGridPagos.baseParams.json = json;
		        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridPagos, msg: "Cargando Datos ..."});
		        
		        		 NS.storeLlenarGridPagos.load();
		        		 NS.storeLlenarGridDescuentos.removeAll();
  	    			   	 NS.storeLlenarGridDetalleDescuentos.removeAll();
  	    			   	 NS.storeLlenarGridDetallePagos.removeAll();
  	    			   	 
  	    			   	 NS.gridDatosDescuentos.getView().refresh;
  	    			   	 NS.gridDatosDetallesDescuentos.getView().refresh;
  	    			   	 NS.gridDatosDetallesPagos.getView().refresh;
  	    			   	 NS.totalPDescuentosProveedorTxt.setValue('');
  	    			   	 NS.totalPDescuentosProveedoresTxt.setValue('');
  	    			   	 NS.totalPPagosTxt.setValue('');
		        	 }
		         }
	    
	    ]
	
	});
	
	NS.PanelContenedorGeneral = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorGeneral',
		layout: 'absolute',
		width: 1010,
		height: 850,
		items: [
		        NS.PanelContenedorCriteriosBusqueda,
		        NS.PanelContenedorCentral
		]
	});
	
	
	NS.ADPropuestas = new Ext.form.FormPanel({
		title: 'Aplicación de Descuentos en Propuestas',
		width: 1010,
		height: 800,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'ADPropuestas',
		name: PF + 'ADPropuestas',
		renderTo: NS.tabContId,
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items: [
		        	NS.PanelContenedorGeneral
		        	
		        ]
	});
	
	NS.ADPropuestas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});