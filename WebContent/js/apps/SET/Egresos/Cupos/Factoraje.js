/**
 * Vista Creada por: Luis Alfredo Serrato Montes de oca
 * 30/11/2015
 */

Ext.onReady(function (){
	
	var NS = Ext.namespace('apps.SET.Egresos.Cupos.Factoraje');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	//Para evitar el desface de los grids
	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
	
	NS.parametroBus = '';
	                                                                                                                                      
	/*
	 * LABEL'S
	 */

	NS.noEmpresaLbl = new Ext.form.Label({
		text: 'No.de Empresa:',
		x: 0,
		y: 7
	});
	
	NS.proveedorLbl = new Ext.form.Label({
		text: 'Proveedor:',
		x: 300,
		y: 7
	});
	
	NS.fechasLbl = new Ext.form.Label({
		text: 'Rango de Fechas:',
		x: 725,
		y: 7
	});
	
	
	NS.factorajeLbl = new Ext.form.Label({
		text: 'Factoraje:',
		x: 0,
		y: 7
	});
	
	NS.fechaFactorajeLbl = new Ext.form.Label({
		text: 'Fecha de vencimiento:',
		x: 0,
		y: 40
	});
	
	/*
	 * TEXTBOX'S
	 */
	
	NS.noEmpresasTxt = new Ext.form.TextField({
		id: PF + 'noEmpresasTxt',
		//name: PF + 'noEmpresasTxt',
		x: 0,
		y: 25,
		width: 50,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					BFwrk.Util.updateTextFieldToCombo(PF + 'noEmpresasTxt', PF + 'cmbNoEmpresa');
				}
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
		directFn: FactorajeAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existe el beneficiario con esa clave');
					//Ext.getCmp(PF + 'proveedorTxt').reset();
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
	
	NS.proveedorTxt = new Ext.form.TextField({
		id: PF + 'proveedorTxt',
		//name: PF + 'proveedorTxt',
		x: 300,
		y: 25,
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
	
	
	/*NS.proveedorBusquedaTxt = new Ext.form.TextField({
		id: PF + 'proveedorBusquedaTxt',
		x: 600,
		y: 25,
		width: 45,
		listeners:
		{
			change:
			{
				fn: function(caja, valor)
				{
					if(valor.length >= 4){
						
					}else{
						Ext.Msg.alert('SET', 'Debe ingresar minimo 4 letras');
					}
				}
			}
		}
	});*/
		
		
		
		
	
	/*
	 * DATEFIELD'S
	 */
	
	NS.fechaInicialDtf = new Ext.form.DateField({
		id: PF + 'fechaInicialDtf',
		name: PF + 'fechaInicialDtf',
		format:'d/m/Y',
		x: 725,
		y: 25,
		width: 100,
		value: apps.SET.FEC_HOY
	});
	
	NS.fechaFinalDtf = new Ext.form.DateField({
		id: PF + 'fechaFinalDtf',
		name: PF + 'fechaFinalDtf',
		format:'d/m/Y',
		x: 840,
		y: 25,
		width: 100,
		value: apps.SET.FEC_HOY
		
	}); 
	
	
	NS.fechaFactDtf = new Ext.form.DateField({
		id: PF + 'fechaFactDtf',
		name: PF + 'fechaFactDtf',
		format:'d/m/Y',
		x: 110,
		y: 40,
		width: 140,
		value: apps.SET.FEC_HOY
	}); 
	
	
	
	/*
	 * FUNCIONE'S
	 */
	
	
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
	
	
	/*
	 * COMBO'S
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
				
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay empresas que mostrar');
				}else{
					
					var recordsStoreGruEmp = NS.storeLlenarComboEmpresa.recordType;	
					var todas = new recordsStoreGruEmp({
				       	id : 0,
				       	descripcion : '***********TODAS***********'
			       	});
					NS.storeLlenarComboEmpresa.insert(0,todas);
					
				}
					
			}
		}

	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storeLlenarComboEmpresa, msg: "Cargando Empresas ..."});
	NS.storeLlenarComboEmpresa.load();
	
	NS.cmbNoEmpresa = new Ext.form.ComboBox({
		store: NS.storeLlenarComboEmpresa,
		id: PF + 'cmbNoEmpresa',
		name: PF + 'cmbNoEmpresa',
		width: 210,
		x: 65,
		y: 25,
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
					BFwrk.Util.updateComboToTextField(PF + 'noEmpresasTxt', NS.cmbNoEmpresa.getId());
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
		directFn: ConsultaPropuestasAction.llenarComboBeneficiario, 
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
				}
			}
		}
	});
	
	
	//combo Beneficiario
	NS.cmbProveedor = new Ext.form.ComboBox({
		store: NS.storeLlenarProveedor,
		id: PF + 'cmbProveedor',
		name: PF + 'cmbProveedor',
		width: 210,
		x: 375,
		y: 25,
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
					//NS.accionarBeneficiario(combo.getValue());
				}
			},
				beforequery: function(qe){
					NS.parametroBus=qe.combo.getRawValue();
				}
		}
	});
	
	

	NS.storeLlenarIntermediario = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: FactorajeAction.obtenerIntermediarios,
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
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay empresas que mostrar');
				}else{
					
				}
					
			}
		}

	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storeLlenarIntermediario, msg: "Cargando Intermediarios ..."});
	NS.storeLlenarIntermediario.load();
	
	NS.cmbFactoraje = new Ext.form.ComboBox({
		store: NS.storeLlenarIntermediario,
		id: PF + 'cmbFactoraje',
		name: PF + 'cmbFactoraje',
		width: 200,
		x: 50,
		y: 7,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'SELECCIONE FACTORAJE',
		triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor){
					

				}
			}
			
		}
	});
	
	
	
	/*
	 * GRID'S
	 */
	
	NS.storeLlenarGridatosFactoraje = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		baseParams: {empresa: 0, proveedor: 0, fechaIni: '', fechaFin: ''},
		paramOrder: ['empresa', 'proveedor', 'fechaIni', 'fechaFin'],
		directFn: FactorajeAction.obtenerListaFactoraje,
		fields:
		[
		    {name: 'factura'},
		    {name: 'nombreEmpresa'},
		 	{name: 'beneficiario'},
		 	{name: 'numeroDcoumento'},
		 	{name: 'texto'},
		 	{name: 'total'},
		 	{name: 'formaPago'},
		 	{name: 'bancoP'},
		 	{name: 'chequera'},
		 	{name: 'referenciaBanc'},
		 	{name: 'estatus'},
		 	{name: 'fecValor'},
		 	{name: 'fecCont'},
		 	{name: 'fecOperacion'},
		 	{name: 'cveControl'},
		 	{name: 'noFact'}
		 	
		],
		listeners:
		{
			load: function(s, records)
			{
				
				
				
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay datos que mostrar');
				}else{
										
		 			
				}
					
			}
		}

	});

	
	NS.seleccionGrid = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});  
	
	
	NS.columnasDatosFact = new Ext.grid.ColumnModel([
        NS.seleccionGrid,
        {header: 'FOLIO_SET', width: 100, dataIndex: 'factura' ,sortable: true, hidden: true},
	    {header: 'EMPRESA', width: 100, dataIndex: 'nombreEmpresa' ,sortable: true},	
	 	{header: 'BENEFICIARIO', width: 100, dataIndex: 'beneficiario' ,sortable: true},                                        	
	 	{header: 'DOCUMENTO', width: 120, dataIndex: 'numeroDcoumento' ,sortable: true},
	 	{header: 'CONCEPTO', width: 270, dataIndex: 'texto' ,sortable: true},
	 	{header: 'IMPORTE', width: 100, dataIndex: 'total' ,sortable: true, renderer: BFwrk.Util.rendererMoney, align:'right'},
	 	{header: 'FORMA DE PAGO', width: 150, dataIndex: 'formaPago' ,sortable: true},                                      	
	 	{header: 'BANCO PAGO', width: 120, dataIndex: 'bancoP' ,sortable: true},
	 	{header: 'CUENTA PAGO', width: 180, dataIndex: 'chequera' ,sortable: true},
	 	{header: 'BANCO BENEFICIARIO', width: 100, dataIndex: 'referenciaBanc' ,sortable: true},
	 	{header: 'CUENTA BENEFICIARIA', width: 150, dataIndex: 'estatus' ,sortable: true},
	 	{header: 'FECHA_VALOR', width: 100, dataIndex: 'fecValor' , hidden: true},
	 	{header: 'FECHA_CONTABLE', width: 100, dataIndex: 'fecCont', hidden: true},
	 	{header: 'FECHA_DOCUMENTO', width: 100, dataIndex: 'fecOperacion' ,hidden: true},
	 	{header: 'PROPUESTA', width: 100, dataIndex: 'cveControl' ,hidden: true},
	 	{header: 'FACTURA', width: 100, dataIndex: 'noFact' , hidden: true}
	 	
	]);
	
	NS.gridDatosFact = new Ext.grid.GridPanel({
		store: NS.storeLlenarGridatosFactoraje,
		id: PF + 'gridDatosFact',
		x: 0,
		y: 0,
		cm: NS.columnasDatosFact,
		sm: NS.seleccionGrid,
		width: 960,
		height: 250,
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
	 * PANEL'S
	 */
	
	
	NS.PanelContenedorPie = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorPie',
		layout: 'absolute',
		width: 978,
		height: 120,
		x: 0,
		y: 435,
		items:[
		       NS.factorajeLbl,
		       NS.fechaFactorajeLbl,
		       NS.cmbFactoraje,
		       NS.fechaFactDtf
		      
		],
		buttons:[
		         {
		        	 text: 'Contabilizar',
		        	 handler: function(){
		        		 NS.datos = NS.gridDatosFact.getSelectionModel().getSelections();
		        		 if(NS.datos.length > 0){
		        			if(NS.cmbFactoraje.getValue() != ''){ 
		        				if(NS.fechaFactDtf.getValue() != ''){
				        			 //NS.folios = '( ';
		        					var matriz = new Array();
				        			 for(var i = 0; i < NS.datos.length; i ++){
				        				 var vectorGrid = {};
				        				// NS.folios = NS.folios + NS.datos[i].get('factura');
				        				 vectorGrid.folio = NS.datos[i].get('factura');
				        				 vectorGrid.cveControl = NS.datos[i].get('cveControl');
				        				 matriz[i] = vectorGrid;
				        			 }
				        			 
				        			 var jsonString = Ext.util.JSON.encode(matriz);
				        			 Ext.MessageBox.show({
									       title : 'Información SET',
									       msg : 'Compensando Facturas, espere por favor...',
									       width : 300,
									       wait : true,
									       progress:true,
									       waitConfig : {interval:200}
								   		});
				        			 NS.hide_messagee();
				        			 FactorajeAction.enviarDatos(jsonString , NS.cmbFactoraje.getValue(), NS.cambiarFecha('' + NS.fechaFactDtf.getValue()) 
				        					 ,function(result, e){
				        				//Ext.Msg.alert('SET', resultado);
				        				//////////////////
				        				 Ext.MessageBox.hide();
		                					if(result!==null && result!==undefined && result!==''){
		                						
		                						if(result.estatus==true){
		                							if(result.excel !== ''){
		                								parametros='?nomReporte=excelMensajePagoPropuesta';
		                								parametros+='&nomParam1=nombre';
		                								parametros+='&valParam1=' + result.excel;
		                								window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
		                								Ext.Msg.alert('Información SET', 'Consulte el archivo Excel para ver el detalle de los pagos.', function(btn){
				                						    if (btn == 'ok'){
				       				        				 	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridatosFactoraje, msg: "Cargando Datos ..."});
				       				        				 	NS.storeLlenarGridatosFactoraje.reload();
				                						    }
				                						});
		                								
		                							}else{
		                								Ext.Msg.alert('Información SET', 'No se pudo generar la respuesta.', function(btn){
				                						    if (btn == 'ok'){
				       				        				 	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridatosFactoraje, msg: "Cargando Datos ..."});
				       				        				 	NS.storeLlenarGridatosFactoraje.reload();
				                						    }
				                						});

		                							}
		                							
		                						}else{
		                							Ext.Msg.alert('Información SET', ''+result.msgError, function(btn){
			                						    if (btn == 'ok'){
			       				        				 	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridatosFactoraje, msg: "Cargando Datos ..."});
			       				        				 	NS.storeLlenarGridatosFactoraje.reload();
			                						    }
			                						});
		                						}
		                					

		                					}

				        				//////////////////
				        				 
				        				 
				        				
				        				 
				        			 });
		        				}else{
		        					Ext.Msg.alert('SET', "Debe seleccionar una fecha factoraje");
		        				}	 
		        		 	}else{
		        		 		Ext.Msg.alert('SET', "Debe seleccionar una empresa factoraje");
		        		 	}	 
		        		 }else{
		        			 Ext.Msg.alert('SET', "No hay renglones seleccionados");
		        		 }
		        	 }
		         },
		         
		         {
		        	 text: 'Cancelar',
		        	 handler: function(){
		        		 NS.cmbNoEmpresa.setValue('');
		        		 NS.noEmpresasTxt.setValue('');
			  		     NS.proveedorTxt.setValue('');
			  		     NS.cmbProveedor.setValue('');
			  		     NS.fechaInicialDtf.setValue(apps.SET.FEC_HOY);
			  		     NS.fechaFinalDtf.setValue(apps.SET.FEC_HOY);
		        	 }
		        	 
		         }
		         
		]
	});
		
	
	NS.PanelContenedorDatosConsulta = new Ext.form.FieldSet({
		title: 'DATOS CONSULTA',
		id: PF + 'PanelContenedorDatosConsulta',
		layout: 'absolute',
		width: 978,
		height: 300,
		x: 0,
		y: 130,
		items:[
		       NS.gridDatosFact
		]
		
	});
		
	
	NS.PanelContenedorCabezera = new Ext.form.FieldSet({
		title: 'Criterios de Busqueda',
		layout: 'absolute',
		width: 978,
		height: 120,
		items:[
		       NS.noEmpresaLbl,
		       NS.noEmpresasTxt,
		       NS.cmbNoEmpresa,
		       NS.proveedorLbl,
		       NS.proveedorTxt,
		       NS.cmbProveedor,
		       NS.fechasLbl,
		       NS.fechaInicialDtf,
		       NS.fechaFinalDtf,
//		       NS.proveedorBusquedaTxt,
		       {
		    	   xtype: 'button',
		    	   text: '...',
		    	   x: 600,
		   		   y: 25,
		   		   width: 20,
		    	   handler: function(){
		    		   	if(NS.parametroBus==='' || (isNaN(NS.parametroBus) && NS.parametroBus.length < 4)) {
               				Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
	               		} else{
	               			NS.storeLlenarProveedor.baseParams.condicion=NS.parametroBus;
	               			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarProveedor, msg:"Cargando Proveedores..."});
	               			NS.storeLlenarProveedor.load();
	               		}
		    	   }
		       }
		       
		       
		],
		buttons:[
		         {
		        	 text: 'Buscar',
		        	 handler: function(){
		        		 
		        		 console.log(NS.cmbNoEmpresa.getValue() + " " + NS.cmbProveedor.getValue() + " " +
		        				 NS.fechaInicialDtf.getValue() + " " + NS.fechaFinalDtf.getValue());
		        		 
		        		 if(NS.cmbNoEmpresa.getValue() != ''){
		        			 NS.storeLlenarGridatosFactoraje.baseParams.empresa = NS.cmbNoEmpresa.getValue(); 
		        		 }else{
		        			 NS.storeLlenarGridatosFactoraje.baseParams.empresa = 0;
		        		 }
		        		 if(NS.cmbProveedor.getValue() != '' && NS.proveedorTxt.getValue() != ''){
		        			 //NS.storeLlenarGridatosFactoraje.baseParams.proveedor = NS.cmbProveedor.getValue();
		        			 NS.storeLlenarGridatosFactoraje.baseParams.proveedor=parseInt(NS.proveedorTxt.getValue());
		        		 }else{
		        			 NS.storeLlenarGridatosFactoraje.baseParams.proveedor = 0;
		        		 }
		        		 if(NS.fechaInicialDtf.getValue() != ''){
		        			 NS.storeLlenarGridatosFactoraje.baseParams.fechaIni = NS.cambiarFecha('' + NS.fechaInicialDtf.getValue()); 
		        		 }else{
		        			 NS.storeLlenarGridatosFactoraje.baseParams.fechaIni = '';
		        		 }
		        		 if(NS.fechaFinalDtf.getValue() != ''){
		        			 NS.storeLlenarGridatosFactoraje.baseParams.fechaFin = NS.cambiarFecha('' + NS.fechaFinalDtf.getValue());
		        		 }else{
		        			 NS.storeLlenarGridatosFactoraje.baseParams.fechaFin = '';
		        		 }
		        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarGridatosFactoraje, msg: "Cargando Datos ..."});
		        		 NS.storeLlenarGridatosFactoraje.load();
		        	 }
		         }
		]
	});
	

	
	NS.PanelContenedorGeneral = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorGeneral',
		layout: 'absolute',
		width: 1010,
		height: 690,
		items: [
		        NS.PanelContenedorCabezera,
		        NS.PanelContenedorDatosConsulta,
		        NS.PanelContenedorPie
		]
	});
	
	NS.Factoraje = new Ext.form.FormPanel({
		title: 'Factoraje',
		width: 1010,
		height: 800,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'ConsultaPagosPendientes',
		name: PF + 'ConsultaPagosPendientes',
		renderTo: NS.tabContId,
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items: [
		        	NS.PanelContenedorGeneral
		        ]
	});
	
	NS.hide_messagee =function() {
	    Ext.defer(function() {
	    	Ext.MessageBox.hide();
	    }, 180000);
	}
	
	NS.Factoraje.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
});