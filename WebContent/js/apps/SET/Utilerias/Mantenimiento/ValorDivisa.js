Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.ValorDivisa');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecha = NS.fecHoy;
	var fecha=NS.fecha;
	//**************FALTANTES
	/*
	 *HACE FALTA ESTA VALIDACION EN EL BOTON EJECUTAR
	 * If Not gobjSeguridad.ValidaFacultad("ADIVIS", sMensajeFac) Then
	       MsgBox "No tiene facultades para realizar esta operación." & vbCrLf & sMensajeFac, vbExclamation
	       Exit Sub
	   End If
	 */
	
	
	//LABEL
	NS.lblFecha = new Ext.form.Label({
		text: 'Fecha',
		x: 10,
		y : 8		
	});
	
	NS.lblTasa = new Ext.form.Label({
		text: 'Tasas:',
		x: 10,
		y: 0
	});
	
	NS.lblDivisa = new Ext.form.Label({
		text: 'Divisas: ',
		x: 530,
		y: 0
	});
	
	/*NS.lblValorTasa = new Ext.form.Label({
		text: 'Valor',
		x: 300,
		y: 0
	});
	
	
	
	NS.lblValorDivisa = new Ext.form.Label({
		text: 'Valor',
		x: 820,
		y: 0
	});*/
	
	//TEXTFIELD
	NS.txtFecha = new Ext.form.DateField({
	//NS.txtFecha = new Ext.form.TextField({
		id: PF + 'txtFecha',
		name: PF + 'txtFecha',
		x: 60,
		y: 6,
		width: 100,
		format: 'd/m/Y',
		value: NS.fecha,
	});
	
	/*NS.txtValorTasa = new Ext.form.TextField({
		id: PF + 'txtValorTasa',
		name: PF + 'txtValorTasa',
		x: 300,
		y: 15,
		width: 100,
		disabled: true,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{					
					var recordsGridDatos= NS.storeLlenaGridTasas.data.items;
					var registroSeleccionado = NS.gridTasas.getSelectionModel().getSelections();
					var contenido = registroSeleccionado[0].get('valor'); 
							
					var datosClase = NS.gridTasas.getStore().recordType;
					var ejemplo = Ext.getCmp(PF + 'txtValorTasa').getValue();
									
					 var indice;	        			
						for(var inc=0; inc < recordsGridDatos.length ;inc=inc+1)
						{
							if(recordsGridDatos[inc].get('valor') === contenido)
							{
								indice = inc;
								
								NS.storeLlenaGridTasas.remove(recordsGridDatos[indice]);
							}
						}
						
					var datos = new datosClase({			
						idTasa: Ext.getCmp(PF + 'txtIdTasa').getValue(),
						descTasa: Ext.getCmp(PF + 'cmbTasas').getValue(),
						valor: ejemplo
					});
					
					NS.gridTasas.stopEditing(true);
					NS.storeLlenaGridTasas.insert(indice, datos);
			   		NS.gridTasas.getView().refresh();
			   		//NS.gridTasas.startEditing();
				}
			}
		}
	});
	
	NS.txtValorDivisa = new Ext.form.TextField({
		id: PF + 'txtValorDivisa',
		name: PF + 'txtValorDivisa',
		x: 820,
		y: 15,
		width: 100,
		disabled: true,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
		
					var recordsGridDatos= NS.storeLlenaGridDivisas.data.items;
					var registroSeleccionado = NS.gridDivisas.getSelectionModel().getSelections();
					var contenido = registroSeleccionado[0].get('valor'); 
							
					var datosClase = NS.gridDivisas.getStore().recordType;
					var contenidoCaja = Ext.getCmp(PF + 'txtValorDivisa').getValue();
									
					 var indice;	        			
						for(var inc=0; inc < recordsGridDatos.length ;inc=inc+1)
						{
							if(recordsGridDatos[inc].get('valor') === contenido)
							{
								indice = inc;
								
								NS.storeLlenaGridDivisas.remove(recordsGridDatos[indice]);
							}
						}
						
					var datos = new datosClase({			
						idDivisa: Ext.getCmp(PF + 'txtIdDivisa').getValue(),
						descDivisa: Ext.getCmp(PF + 'cmbDivisas').getValue(),
						valor: contenidoCaja
					});
					
					NS.gridDivisas.stopEditing(true);
					NS.storeLlenaGridDivisas.insert(indice, datos);
			   		NS.gridDivisas.getView().refresh();
			   		//NS.gridDivisas.startEditing();*
				}
			}
		}
	});
*/
	//Esta se creo solo para hacer editables los grid y hacer los cambios sobre las celdas
	var textFieldTasas = new Ext.form.TextField({
		xtype: 'number', 
		format: '0.00000',
		enableKeyEvents: true,
		listeners:{
			keydown:{
	 			fn:function(caja, e) {
	 				var temp = caja.getValue();
	 				
	 				 if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				 
	 				/* var res = temp.split(".");
	 				
	 				try{
	 					//alert(res[1]);
	 					if(res[1].length>=5){
	 						caja.setValue(temp);
		 				}
	 				}catch (e) {
	 					//alert("error");
						// TODO: handle exception
					}
	 				var res = temp.split(".");
	 				alert(res.length);
	 				if(res.length==2){
	 					if(res[1].length>=5){
	 						alert(res[1].length);
	 						caja.setValue(temp);
		 				}
	 				} */
	 				 
	 			}
	 		},
	 		keyup:{
	 			
	 			fn:function(caja, e) {	 
	 				var temp = caja.getValue();
	 			
	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				
	 				/*var res = temp.split(".");
	 				
	 				try{
	 					if(res[1].length>=5){
	 						caja.setValue(temp);
		 				}
	 				}catch (e) {
						// TODO: handle exception
					}
	 				
	 				var res = temp.split(".");
	 				alert(res.length);
	 				if(res.length==2){
	 					if(res[1].length>=5){
	 						alert(res[1].length);
	 						caja.setValue(temp);
		 				}
	 				}*/
	 				
	 			}
	 		}
		}
	});
	var textFieldDivisas = new Ext.form.TextField({
		enableKeyEvents: true,
		listeners:{
			keydown:{
	 			fn:function(caja, e) {
	 				var temp = caja.getValue();
	 				
	 				 if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 			}
	 		},
	 		keyup:{
	 			
	 			fn:function(caja, e) {	 
	 				var temp = caja.getValue();
	 			
	 				if(!isNumeric(temp)){
	 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
	 				}
	 				
	 			}
	 		}
		}
		
	});
	
	//FUNCIONES
	//FUNCION QUE HACE LA CONSULTA DE LAS TASAS Y DIVISAS
	NS.buscar = function(){
		
		NS.storeLlenaGridTasas.baseParams.fechaBusqueda = NS.txtFecha.value;
		NS.storeLlenaGridTasas.load();
		
		NS.storeLlenaGridDivisas.baseParams.fechaBusqueda = NS.txtFecha.value;
		NS.storeLlenaGridDivisas.load();	
		
	};
	
	//FUNCION QUE LIMPIA LA PANTALLA
	NS.cancelar = function(tipoCancelacion){
		switch (tipoCancelacion){
		case 1: //Solo limpia lo de las tasas
			/*Ext.getCmp(PF + 'txtIdTasa').setValue('');
			Ext.getCmp(PF + 'txtValorTasa').setValue('');
			Ext.getCmp(PF + 'cmbTasas').reset();*/
			NS.gridTasas.store.removeAll();
			NS.gridTasas.getView().refresh();		
			break;
		case 2: //Solo limpia lo de las divisas
			/*Ext.getCmp(PF + 'txtIdDivisa').setValue('');
			Ext.getCmp(PF + 'txtValorDivisa').setValue('');
			Ext.getCmp(PF + 'cmbDivisas').reset();*/
			NS.gridDivisas.store.removeAll();
			NS.gridDivisas.getView().refresh();
			break;
		case 3: //Limpia toda la pantalla			
			Ext.getCmp(PF + 'txtFecha').setValue(NS.fecHoy);
			/*Ext.getCmp(PF + 'txtIdTasa').setValue('');
			Ext.getCmp(PF + 'txtValorTasa').setValue('');
			Ext.getCmp(PF + 'cmbTasas').reset();
			Ext.getCmp(PF + 'txtIdDivisa').setValue('');
			Ext.getCmp(PF + 'txtValorDivisa').setValue('');
			Ext.getCmp(PF + 'cmbDivisas').reset();*/
			NS.gridTasas.store.removeAll();
			NS.gridTasas.getView().refresh();
			
			NS.buscar();
			break;
		}
				
	};
	
	NS.llenaDatos = function(tipoGrid){
		switch (tipoGrid){
		
			case 1: //Esto es para el grid de Tasas
				var registroSeleccionado = NS.gridTasas.getSelectionModel().getSelections();
				
				if (registroSeleccionado.length <= 0)
					Ext.Msg.alert('SET', 'Se debe seleccionar un registro');
				else
				{
					/*Ext.getCmp(PF + 'txtIdTasa').setValue(registroSeleccionado[0].get('idTasa'));
					Ext.getCmp(PF + 'cmbTasas').setValue(registroSeleccionado[0].get('descTasa'));
					Ext.getCmp(PF + 'txtValorTasa').setValue(registroSeleccionado[0].get('valorTasa'));	*/			
				}			
				break;
			case 2: //Esto es para el grid de Divisas
				var registroSeleccionado = NS.gridDivisas.getSelectionModel().getSelections();
				
				if (registroSeleccionado.length <= 0)
					Ext.Msg.alert('SET', 'Se debe seleccionar un registro');
				else
				{
					/*Ext.getCmp(PF + 'txtIdDivisa').setValue(registroSeleccionado[0].get('idDivisa'));
					Ext.getCmp(PF + 'cmbDivisas').setValue(registroSeleccionado[0].get('descDivisa'));
					Ext.getCmp(PF + 'txtValorDivisa').setValue(registroSeleccionado[0].get('valorDivisa'));*/
				}			
				break;
		}
			
	};
	
	NS.ejecutar = function(){
	
		var matrizTasas = new Array();
		var matrizDivisas = new Array();
		
		var obtieneTasas = NS.gridTasas.store.data.items;
		var obtieneDivisas = NS.gridDivisas.store.data.items;
		
		NS.fecha = Ext.getCmp(PF + 'txtFecha').getValue();
		if (NS.fecha == '')
			NS.fecha = NS.fecHoy;
		
		//Obtiene el grid de las tasas
		for(var t = 0; t < obtieneTasas.length; t++)
		{
			var vectorTasas = {};
			vectorTasas.idTasa = obtieneTasas[t].get('idTasa');
			vectorTasas.descTasa = obtieneTasas[t].get('descTasa');
			vectorTasas.valorTasa = obtieneTasas[t].get('valorTasa');
			//alert(vectorTasas.valorTasa);
			matrizTasas[t] = vectorTasas;
		}	
				
		//Obtiene el grid de las divisas
		for(var d = 0; d < obtieneDivisas.length; d++)
		{
			var vectorDivisas = {};
			vectorDivisas.idDivisa = obtieneDivisas[d].get('idDivisa');
			vectorDivisas.descDivisa = obtieneDivisas[d].get('descDivisa');
			vectorDivisas.valorDivisa = obtieneDivisas[d].get('valorDivisa');
			
			matrizDivisas[d] = vectorDivisas;			
 		}
		
		var jsonTasas = Ext.util.JSON.encode(matrizTasas);
		var jsonDivisas = Ext.util.JSON.encode(matrizDivisas);
		
		//alert(NS.txtFecha.value);
		MantenimientoValorDivisaAction.insertaActualiza(jsonTasas, jsonDivisas, NS.txtFecha.value , function(result, e){
			Ext.Msg.alert('SET', 'Los cambios se guardaron satisfactoriamente');
			
			if (result !== null && result !== '' && result !== undefined)
			{
				Ext.Msg.alert('SET', result);
				NS.buscar();				
			}
		});		
	};
		
	NS.activaGrid = function(){
		Ext.getCmp(PF + 'txtIdDivisa').setDisabled(true);
		Ext.getCmp(PF + 'txtIdTasa').setDisabled(true);
		NS.cmbTasas.setDisabled(true);
		NS.cmbDivisas.setDisabled(true);
		NS.txtValorDivisa.setDisabled(true);
		NS.txtValorTasa.setDisabled(true);
	};
	
	
	//STORE
	NS.storeTasas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: MantenimientoValorDivisaAction.llenaComboTasas,
		idProterty: 'idTasa',
		fields: [
		         {name: 'idTasa'},
		         {name: 'descTasa'}
		],
		listeners:{
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTasas, msg: "Cargando Tasas..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Tasas dadas de alta en el catalogo');
			}
		}	
	});
	NS.storeTasas.load();
	
	NS.storeDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: MantenimientoValorDivisaAction.llenaComboDivisas,
		idProperty: 'idDivisa',
		fields:
		[
		 	{name: 'idDivisa'},
		 	{name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisas, msg: "Cargando Divisas..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Divisas dadas de alta para esta fecha, se coloca estos valores por default');
			}
		}
	});
	NS.storeDivisas.load();
	
	NS.storeLlenaGridTasas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {fechaBusqueda: NS.fecha},
		paramOrder: ['fechaBusqueda'],
		directFn: MantenimientoValorDivisaAction.llenaGridTasas,
		fields: [
		 	{name: 'idTasa'},
		 	{name: 'descTasa'},
		 	{name: 'valorTasa', type: 'number'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridTasas, msg: "Buscando..."});
				if (records.length == null || records.length <= 0){
					NS.cancelar(1);
					Ext.Msg.alert('SET', 'No existen Tasas dadas de alta para esta fecha, se coloca estos valores por default');					
				}
			}
		}		
	});
	NS.storeLlenaGridTasas.load();
	
	NS.storeLlenaGridDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {fechaBusqueda: NS.fecha},
		paramOrder: ['fechaBusqueda'],
		directFn: MantenimientoValorDivisaAction.llenaGridDivisas,
		fields: [
		 	{name: 'idDivisa'},
		 	{name: 'descDivisa'},
		 	{name: 'valorDivisa', type: 'number'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridDivisas, msg: "Buscando..."});
				if (records.length == null || records.length <= 0){
					//NS.cancelar(2);
					Ext.Msg.alert('SET', 'No existen Divisas dadas de alta para esta fecha');					
				}
			}
		}		
	});
	NS.storeLlenaGridDivisas.load();
	
	//COMBOBOX
	NS.cmbTasas = new Ext.form.ComboBox({
		store: NS.storeTasas,
		id: PF + 'cmbTasas',
		name: PF + 'cmbTasas',
		x: 70, 
		y: 15,
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idTasa',
		displayField: 'descTasa',
		autocomplete: true,
		emptyText: 'Tasas',
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled: true,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdTasa', NS.cmbTasas.getId());					
				}
			}
		}
	
	});
	
	
	NS.cmbDivisas = new Ext.form.ComboBox({
		store: NS.storeDivisas,
		id: PF + 'cmbDivisas',
		name: PF + 'cmbDivisas',
		x: 590,
		y: 15,
		width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idDivisa',
		displayField: 'descDivisa',
		autocomplete: true,
		emptyText: 'Divisas',
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled: true,
		listeners: {
			select: {
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdDivisa', NS.cmbDivisas.getId());
				}
			}
		}
	});

	
	//GRID
	//GRID DE TASAS
	NS.columnaSeleccionTasas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	//, editor: textField
	NS.columnasGridTasas = new Ext.grid.ColumnModel
	([
	  {header: 'Clave', width: 70, dataIndex: 'idTasa', sortable: true},
	  {header: 'Tasa', width: 200, dataIndex: 'descTasa', sortable: true},
	  {header: 'Valor', width: 80, dataIndex: 'valorTasa', sortable: true, editor: textFieldTasas, xtype: 'numbercolumn', format: '0.00000',enablekeyevents:true,
			listeners:{
				keydown:{
		 			fn:function(caja, e) {
		 				var temp = caja.getValue();
		 				
		 				 if(!isNumeric(temp)){
		 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
		 				}
		 			}
		 		},
		 		keyup:{
		 			
		 			fn:function(caja, e) {	 
		 				var temp = caja.getValue();
		 			
		 				if(!isNumeric(temp)){
		 					caja.setValue(temp.substring(0, caja.getValue().length - 1));
		 				}
		 				
		 			}
		 		}
			}}	  
	]);
			
	NS.gridTasas = new Ext.grid.EditorGridPanel
	({
		store: NS.storeLlenaGridTasas,
		id: PF + 'gridTasas',
		name: PF + 'gridTasas',
		cm: NS.columnasGridTasas,
		sm: NS.columnaSeleccionTasas,
		x: 0,
		y: 0,
		width: 380,
		height: 250,
		stripRows: true,
		columnLines: true,
		listeners: 
		{
			click:
			{
				fn: function(grid)
				{
					NS.llenaDatos(1);
				}
			}
		}
	});
	
	//GRID DE DIVISAS	
	NS.columnaSeleccionDivisas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasGridDivisas = new Ext.grid.ColumnModel
	([
	  {header: 'Clave', width: 70, dataIndex: 'idDivisa', sortable: true},
	  {header: 'Divisa', width: 200, dataIndex: 'descDivisa', sortable: true},
	  {header: 'Valor', width: 80, dataIndex: 'valorDivisa', sortable: true, editor: textFieldDivisas, xtype: 'numbercolumn', format: '00.00000'}	  
	]);
	
	
	
	NS.gridDivisas = new Ext.grid.EditorGridPanel
	({
		store: NS.storeLlenaGridDivisas,
		id: PF + 'gridDivisas',
		name: PF + 'gridDivisas',
		cm: NS.columnasGridDivisas,
		sm: NS.columnaSeleccionDivisas,
		x: 0,
		y: 0,
		width: 380,
		height: 250,
		stripRows: true,
		columnLines: true,
		listeners: 
		{
			click:
			{
				fn: function(grid)
				{
					NS.llenaDatos(2);
				}
			}
		}
	});
	
	//PANEL
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 0,
		width: 985,
		height: 50,
		layout: 'absolute',
		items:
		[
		 
		 	NS.lblFecha,
		 	NS.txtFecha,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 850,
		 		y: 0,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click:{
		 				fn: function(e){
		 					NS.buscar();
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelGridTasas = new Ext.form.FieldSet({
		title: '',
		x: 10,
		y: 20,
		width: 400,
		height: 250,
		layout: 'absolute',		
		items: 
		[
		 	NS.gridTasas
		]
	
	});
	
	NS.panelGridDivisas = new Ext.form.FieldSet({
		title: '',
		x: 530,
		y: 20,
		width: 400,
		height: 250,
		layout: 'absolute',		
		items: 
		[
		 	NS.gridDivisas
		]
	
	});
	
	NS.panelComponentes = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 60,
		width: 985,
		height: 350,
		layout: 'absolute',
		items:
		[	

		 	NS.lblTasa,
			NS.lblDivisa,
			//NS.lblValorTasa,
			//NS.lblValorDivisa,
			/*{
		 		xtype: 'uppertextfield',
		 		id: PF + 'txtIdTasa',
		 		name: PF + 'txtIdTasa',
		 		x: 10,
		 		y: 15,
		 		width: 50,
		 		disabled: true,
		 		listeners:
				{
					change:
					{
						fn: function(caja, valor)
						{
							if (caja.getValue() !== null && caja.getValue() !== undefined && caja.getValue() !== ''){
								BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdTasa', NS.cmbTasas.getId());
							}
							else
							{								
								Ext.getCmp(PF + 'cmbTasas').reset();
							}
						}
					}
				}		 			
			},
			{
		 		xtype: 'uppertextfield',
		 		id: PF + 'txtIdDivisa',
		 		name: PF + 'txtIdDivisa',
		 		x: 530,
		 		y: 15,
		 		width: 50,
		 		disabled: true,
		 		listeners:
				{
					change:
					{
						fn: function(caja, valor)
						{
							if (caja.getValue() !== null && caja.getValue() !== undefined && caja.getValue() !== ''){
								BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdDivisa', NS.cmbDivisas.getId());
							}
							else
							{
								Ext.getCmp(PF + 'cmbDivisas').reset();								
							}
						}
					}
				}		 			
			},			
			NS.txtValorTasa,
			NS.txtValorDivisa,
			NS.cmbTasas,
			NS.cmbDivisas,*/
			NS.panelGridDivisas,
			NS.panelGridTasas	 			 	
		]
	});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1010,		
		height: 490,
		layout: 'absolute',
		items:
		[				 	
		 	NS.panelBusqueda,	
		 	NS.panelComponentes,
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 660,
		 		y: 430,
		 		width: 80,
		 		height: 22,
		 		hidden: true,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 		
		 				}
		 			}
		 		}		 		
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Ejecutar',
		 		id: PF + 'ejecutar',
		 		name: PF + 'ejecutar',
		 		x: 760,
		 		y: 430,
		 		width: 80,
		 		height: 22,		 		
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					Ext.Msg.confirm('SET', '¿Esta seguro de guardar los registros como se muestran?', function(btn){
		 						if (btn === 'yes'){
		 							NS.ejecutar();
		 						}
		 					});		 				 					
		 				}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Cancelar',
		 		x: 860,
		 		y: 430,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e) {
		 					NS.cancelar(3);
		 				}
		 			}
		 		}
		 	
		 	}
	    ]
	});
	
	NS.valorDivisa = new Ext.FormPanel ({
		title: 'Mantenimiento Valor Divisa',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'valorDivisa',
		name: PF + 'valorDivisa',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	//NS.valorDivisa.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
	
	function isNumeric(num){
        return !isNaN(num);
    }
});
 