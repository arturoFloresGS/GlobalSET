Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Egresos.SeguimientoCuentasPorPagar');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	//NS.noEmpresa = apps.SET.noEmpresa;
	NS.fecHoy = apps.SET.FEC_HOY ;//BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
//	NS.tipoBanco = 0;
//	NS.idBanco = 0;
//	NS.aba = 0;
//	NS.descBanco = 0;
//	NS.modificar = false;
	NS.idSecuencia = 0;
	NS.matrizDatos = new Object();
	NS.modificar=false;
	NS.opciones = 'ECA';
	var tc = 0; 
	NS.tipo = '';
	
	////////////////Agregado Prueba////////////////////
	
	NS.storeBanco = new Ext.data.DirectStore({
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
		directFn: ConsultaPropuestasAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen Beneficiarios con ese nombre');
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	//////////////////////////////////////////////////
	

	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		
		paramOrder: ['idEmpresa','idBeneficiario','noDocumento','periodo'],
		directFn: SeguimientoCPAction.llenaGrid,
		fields:
		[
		 	{name: 'noDocumento'}, 
		 	{name: 'descBeneficiario'},
		 	{name: 'divisa'},
		 	{name: 'importe'},
//		 	{name: 'fechaV'},
		 	{name: 'fec_imp'},
		 	{name: 'descFormaPago'}, 
		 	{name: 'estatus'},
//		 	{name: 'cveControl'},
//		 	{name: 'idFormaPago'}, 
		 		
		],
		listeners: {
			load: function (s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando ..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos para la busqueda');
			}
		}
	});
	
	NS.storeUnicoBeneficiarioBloq = new Ext.data.DirectStore({
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
		directFn: ConsultaPropuestasAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiarioBloq, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen el beneficiario con esa clave');
					Ext.getCmp(PF + 'txtIdProveedorBloq').reset();
					Ext.getCmp(PF + 'cmbBeneficiarioBloq').reset();
					return;
				}
				else{
					var reg = NS.storeUnicoBeneficiarioBloq.data.items;
					Ext.getCmp(NS.cmbBeneficiarioBloq.getId()).setValue(reg[0].get('descripcion'));
//					NS.accionarUnicoBeneficiarioBloq(reg[0].get('id'));
					//actualiza grid
					
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	NS.storeBeneficiario = new Ext.data.DirectStore({
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
		directFn: ConsultaPropuestasAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen Beneficiarios con ese nombre');
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	
	NS.storeEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idClave:'0'},
		paramOrder: ['idClave'],
		directFn: GlobalAction.llenarComboEmpresasUsuario,
		fields:
		[
		 	{name: 'id'},
		 	{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresa, msg: "Cargando..."});
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen datos de Empresa');
			}
		}
	});
	
	NS.storeEmpresa.load();
	
	NS.validaCampos = function(){
		 if(Ext.getCmp(PF + 'txtEmpresa').getValue()==""){
			Ext.Msg.alert('SET',"Debe seleccionar una empresa");
			return false;
		}else if(Ext.getCmp(PF + 'txtIdProveedorBloq').getValue()==""){
			Ext.Msg.alert('SET',"Seleccione un proveedor");
			return false;
		}else if(Ext.getCmp(PF + 'txtDocumento').getValue()==""){
			Ext.Msg.alert('SET',"Digite un documento");
			return false;
		}else if(Ext.getCmp(PF + 'txtPeriodo').getValue()==""){
			Ext.Msg.alert('SET',"Digite un periodo");
			return false;
		}
		else{
			return true;
		}
	};
	
	NS.cambiarFecha = function (fecha)
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
	};
	
			NS.buscar = function(){
		
		

			NS.storeLlenaGrid.baseParams.idEmpresa ='' + Ext.getCmp(PF + 'txtEmpresa').getValue();
			NS.storeLlenaGrid.baseParams.idBeneficiario ='' + Ext.getCmp(PF + 'txtIdProveedorBloq').getValue();
			NS.storeLlenaGrid.baseParams.noDocumento ='' + Ext.getCmp(PF + 'txtDocumento').getValue(); 
			NS.storeLlenaGrid.baseParams.periodo ='' + Ext.getCmp(PF + 'txtPeriodo').getValue();
			
			NS.storeLlenaGrid.load();
		
			
			
	};
	
	NS.lblMensaje1 = new Ext.form.Label({
		text: 'Informacion',
		x: 20,
		y: 65		
	});
	

	NS.lblMensaje2 = new Ext.form.Label({
		text: '',
		x: 20,
		y: 65,
		style:'font-size:20px'
	});
	

	
	var venDetalle = new Ext.Window({
		title: '',
		modal: true,
		shadow: true,
		width: 978,
		height: 350,
		x: 200,
		y: 405,
		layout: 'absolute',
		plain: true,
		bodyStyle: 'padding:20px;',
		closeAction: 'hide',
		items:[
		     
		      NS.lblMensaje2,
				

		       	]

	});
	
	NS.cmbBeneficiarioBloq = new Ext.form.ComboBox({
		store: NS.storeBeneficiario,
		name: PF+'cmbBeneficiarioBloq'
		,id: PF+'cmbBeneficiarioBloq'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,pageSize: 10
        ,x: 560
        ,y: 20
        ,width: 300,
		valueField:'id'
		,displayField:'descripcion',
		autocomplete: true
		,emptyText: 'Seleccione beneficiario'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,editable: true
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtIdProveedorBloq',NS.cmbBeneficiarioBloq.getId());
					NS.accionarBeneficiario(combo.getValue());
					
					buscarProveedoresBloqueados();
				}
			},
				beforequery: function(qe){
					NS.parametroBus=qe.combo.getRawValue();
				}
		}
	});
	
	NS.lblPeriodo = new Ext.form.Label({
		text: 'No. Periodo',
		x: 260,
		y: 65		
	});
	
	NS.txtPeriodo = new Ext.form.TextField({
		id: PF + 'txtPeriodo',
		name: PF + 'txtPeriodo',
		x: 260,
		y: 85,
		width: 50,	
 		
	});
	
	
	
	NS.lblDocumento = new Ext.form.Label({
		text: 'No. Documento',
		x: 60,
		y: 65		
	});
	
	NS.txtDocumento = new Ext.form.TextField({
		id: PF + 'txtDocumento',
		name: PF + 'txtDocumento',
		x: 60,
		y: 85,
		width: 150,	

		
	});
	
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 60,
		y: 5		
	});
	
	NS.txtEmpresa = new Ext.form.TextField({
		id: PF + 'txtEmpresa',
		name: PF + 'txtEmpresa',
		x: 60,
		y: 20,
		width: 50,	
		listeners: {
 			change: {
 				
 				fn: function(caja, valor) {
 					
 				if (caja.getValue() !== null && caja.getValue() !== '' & caja.getValue() !== undefined)
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
		 				
				}
 			}
 		}	 	
		
	});
	
	var tpl = new Ext.XTemplate('<tpl for="."><div class="x-combo-list-item" >{id} - {descripcion} </div></tpl>');
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresa,
		id: PF + 'cmbEmpresa',
		name: PF + 'cmbEmpresa',
		x: 120,
		y: 20,
		tpl:tpl,
		width: 300,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				
				fn: function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
					NS.storeChequera.baseParams.idEmpresa=''+NS.cmbEmpresa.getValue();
					NS.storeChequera.load();
//					NS.txtChequera.setValue("");
					NS.cmbChequera.reset();
				}
			}
		}
		
	});
	
	NS.accionarBeneficiarioPagoCruzado = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeBeneficiarioPagoCruzado.getById(valueCombo).get('descripcion');
 	}
	
	
	NS.seleccionMovimiento = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.columnasMovimiento = new Ext.grid.ColumnModel([
	                                                  
				{header: 'No. Documento ', width: 80,  dataIndex: 'noDocumento', sortable: true},                          
	            {header: 'Beneficiario', width: 500,  dataIndex: 'descBeneficiario', sortable: true},
	            {header: 'Divisa', width: 50,  dataIndex: 'divisa', sortable: true},
	            {header: 'Importe ', width: 150,  dataIndex: 'importe', sortable: true},
//	            {header: 'Fecha Valor ', width: 80,  dataIndex: 'fechaV', sortable: true},
				{header: 'Fecha Importacion ', width: 80,  dataIndex: 'fec_imp', sortable: true},     
	            {header: 'Estatus ', width: 80,  dataIndex: 'estatus', sortable: true},
//	            {header: 'Estatus ', width: 80,  dataIndex: 'IdEstatusMov', sortable: true},
//	            {header: 'Cve. Control', width: 80,  dataIndex: 'cveControl', sortable: true},
//	            {header: 'Id. Forma Pago ', width: 80,  dataIndex: 'idFormaPago', sortable: true},
//	            
				]);
	           
	NS.gridMovimiento = new Ext.grid.GridPanel({
		store: NS.storeLlenaGrid,
		id: PF + 'gridMovimiento',
		x: 0,
		y: 5,
		cm: NS.columnasMovimiento,
		sm: NS.seleccionMovimiento,
		lockColumns: 3, 
		width: 960,
		height: 140,
		border: false,
		stripeRows: true,
		listeners:{
			rowdblClick:{
				
				fn:function(grid){
					NS.idEmpresa=Ext.getCmp(PF + 'txtEmpresa').getValue();
					NS.idBeneficiario=Ext.getCmp(PF + 'txtIdProveedorBloq').getValue();
					NS.noDocumento=Ext.getCmp(PF + 'txtDocumento').getValue();
					NS.periodo= Ext.getCmp(PF + 'txtPeriodo').getValue();
						
		
					
					var registroSeleccionado = NS.gridMovimiento.getSelectionModel().getSelections();
					
					if (registroSeleccionado[0].get('estatus')=='I') {
						NS.fecha = registroSeleccionado[0].get('fec_imp');
						NS.mensajeI = "Movimiento importado el dia "+NS.fecha;
						
					}else if (registroSeleccionado[0].get('estatus')=='R') {
						NS.mensajeI = "Movimiento rechazado en importacion el dia  "+NS.fecha;
						
						
					}else if (registroSeleccionado[0].get('estatus')=='F') {
						NS.mensajeI = "Movimiento rechazado en importacion el dia "+NS.fecha;
						
					}else if (registroSeleccionado[0].get('estatus')=='P') {
						NS.mensajeI = "El movimiento se encuentra pendiente en importacion "+NS.fecha;
						
					} else if (registroSeleccionado[0].get('estatus')=='C') {
						NS.mensajeI = "Movimiento cancelado en importacion "+NS.fecha;
						
					} 
					
					SeguimientoCPAction.cadenaDeInformacion(''+ NS.idEmpresa, ''+ NS.idBeneficiario ,
							''+NS.noDocumento, ''+NS.periodo, function(resultado, e){
						
						if (resultado != null && resultado != '' && resultado != undefined)
						{
							NS.mensajeI+="\r "+resultado;
						}else{
							NS.mensajeI="No se pudieron obtener los datos";
						}
						NS.lblMensaje2.setText(NS.mensajeI.toUpperCase());	
						
					});
					
					

//					NS.lblMensaje2.setText(NS.mensajeI);
					venDetalle.show();
					 	
				}
			}
		}
    
	});
	
	NS.PanelMovimiento = new Ext.form.FieldSet({
		title: 'Elije un Movimiento',
		id: PF + 'PanelMovimiento',
		layout: 'absolute',
		width: 978,
		height: 350,
		x: 0,
		y: 200,
		items:[
		       NS.gridMovimiento ,
		],
		
	
	});
	
	NS.PanelContenedorCriteriosBusqueda = new Ext.form.FieldSet({
		title: 'BUSQUEDA',
		id: PF + 'PanelContenedorCriteriosBusqueda',
		layout: 'absolute',
		width: 978,
		height: 175,
		x: 0,
		y: 0,
		items:[
		       NS.lblEmpresa,
		       NS.txtEmpresa,
		       NS.cmbEmpresa,
		       NS.lblDocumento,
		       NS.txtDocumento,
		       NS.lblPeriodo,
		       NS.txtPeriodo,
		       {
					  xtype: 'label',
					  text: 'Proveedor',
					  x: 450,
					  y: 0
				  },
				  {
					  xtype: 'textfield',
					  x: 450,
					  y: 20,
					  width: 90,
					  name: PF+'txtIdProveedorBloq',
					  id: PF+'txtIdProveedorBloq',
					  hidden: false, //true
					  listeners: {
						  change: {
							  fn:function(caja) {
								  if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
									  NS.storeUnicoBeneficiarioBloq.baseParams.registroUnico=true;
									  NS.storeUnicoBeneficiarioBloq.baseParams.condicion=''+caja.getValue();
									  NS.storeUnicoBeneficiarioBloq.load();
									  
									  //
//									  buscarProveedoresBloqueados();
									  
								  }else {
									  NS.cmbBeneficiarioBloq.reset();
									  NS.storeUnicoBeneficiarioBloq.removeAll();
								  }	
							  }
						  }
					  }
				  },
				  {
					  xtype: 'button',
					  text: '...',
					  x: 870,
					  y: 20,
					  width: 20,
					  height: 22,
					  id: PF+'btnComboBenefBloq',
					  name: PF+'btnComboBenefBloq',
					  hidden: false,
					  listeners:{
						  click:{
							  fn:function(e){
								  if(NS.parametroBus==='' || (isNaN(NS.parametroBus) && NS.parametroBus.length < 4)) {
									  Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
								  }else{
									  NS.storeBeneficiario.baseParams.condicion=NS.parametroBus;
									  NS.storeBeneficiario.load();
								  }
							  }
						  }
					  }
				  },
				  {
					  xtype: 'button',
					  text: 'Buscar',
					  x: 400,
					  y: 70,
					  width: 80,
					  height: 22,
					  id: PF+'buscar',
					  name: PF+'buscar',
					  hidden: false,
					  listeners:{
						  click:{
							  fn:function(e){
								
								  if (NS.validaCampos()==true) {
									  NS.buscar();
								  }
							  }
						  }
					  }
				  },
				  NS.cmbBeneficiarioBloq,

		],
		
	
	});
	
	
	
	NS.PanelContenedorGeneral = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorGeneral',
		layout: 'absolute',
		width: 1010,
		height: 850,
		items: [
		        NS.PanelContenedorCriteriosBusqueda,
		        NS.PanelMovimiento,
//		        NS.PanelContenedorCheques,
//		        NS.PanelContenedorCartas
		]
	});
	
	NS.Global = new Ext.form.FormPanel({
		title: 'Seguimiento de Cuentas Por Pagar',
		width: 1010,
		height: 1000,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'global',
		name: PF + 'global',
		renderTo: NS.tabContId,
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items: [
		        	NS.PanelContenedorGeneral
		        	
		        ]
	});
	
	NS.Global.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	

	
	
});