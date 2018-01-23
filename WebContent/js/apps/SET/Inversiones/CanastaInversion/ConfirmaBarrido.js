/* 
 * @author: Victor H. Tello
 * @since: 28/Octubre/2014
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Inversiones.CanastaInversion.ConfirmaBarrido');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	NS.autDes = '';
	NS.solicitudes = '';
	
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
		visible: false
	});
	
	//Etiqueta divisa
	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa',
		x: 320
	});
	
	//store combo divisa
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
		},
		root: '',
		paramOrder:[],
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
		x: 480
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
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No Existen bancos con la divisa seleccionada');
			}
		}
	});
	NS.storeBancos.load();
	
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
		visible: false,
		tabIndex: 3
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
		paramOrder: ['idDivisa', 'idBanco'],
		directFn: BarridoInversionAction.obtenerSolicitudesBarrido,
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'noBanco'},
			{name: 'nomBanco'},
			{name: 'idChequera'},
			{name: 'importeTraspaso'},
			{name: 'noEmpresaBenef'},
			{name: 'nomEmpresaBenef'},
			{name: 'noBancoBenef'},
			{name: 'nomBancoBenef'},
			{name: 'idChequeraBenef'},
			{name: 'descDivisa'},
			{name: 'concepto'},
			{name: 'claveUsuario'},
			{name: 'noSolicitud'},
			{name: 'color'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
				var sdoTra = 0;
				
				if(records.length == null || records.length <=0)
					Ext.Msg.alert('SET', 'No existe información con los parametros seleccionados');
				else {
					for(var x=0; x<records.length; x++) {
						sdoTra += parseFloat(records[x].get('importeTraspaso'));
					}
					Ext.getCmp(PF + 'txtSaldoFin').setValue(NS.formatNumber(sdoTra));
				}
			}
		}
	});
	NS.storeDatos.baseParams.idDivisa = 'MN';
	NS.storeDatos.load();
	
	NS.storeGridConsulta = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {criterios: ''},
		paramOrder:['criterios'],
		directFn: TraspasosAction.consultarSolicitudesTraspaso,
		root: '',
		idProperty: 'criterio',
		fields: [
			{name: 'descObservacion'},
			{name: 'nomEmpresa'},
			{name: 'noEmpresa'},
			{name: 'noEmpresaCxp'},
			{name: 'noEmpresaBenef'},
			{name: 'clabeBenef'},
			{name: 'idBanco'},
			{name: 'idBancoBenef'},
			{name: 'secuencia'},
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'fecValor'},
			{name: 'fechaPropuesta'},
			{name: 'sucursalDestino'},
			{name: 'folioBanco'},
			{name: 'referencia'},
			{name: 'idDivisa'},
			{name: 'importe'},
			{name: 'conceptoSet'},
			{name: 'observacion'},
			{name: 'cargoAbono'},
			{name: 'importeCa'},
			{name: 'noFolioDet'},
			{name: 'ejecutado'},
			{name: 'fecAlta'},
			{name: 'bTraspBanco'},
			{name: 'bTraspconta'},
			{name: 'noDocto'},
			{name: 'fecOperacion'},
			{name: 'beneficiario'},
			{name: 'descBancoBenef'},
			{name: 'concepto'},
			{name: 'idChequera'},
			{name: 'idChequeraBenef'},
			{name: 'origenMov'},
			{name: 'bLayoutComerica'},
			{name: 'habilitado'},
			{name: 'idTipoOperacion'},
			{name: 'descUsuarioBital'},
			{name: 'descServicioBital'},
			{name: 'idContratoMass'},
			{name: 'loteEntrada'},
			{name: 'plazaBenef'},
			{name: 'sucursalDestino'},
			{name: 'instFinan'},
			{name: 'noFolioMov'},
			{name: 'usr1'},
			{name: 'usr2'},
			{name: 'color'},
			{name: 'usrUno'},
			{name: 'usrDos'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridConsulta, msg:"Buscando..."});
			}
		}
	}); 
	
	
	//Para indicar el modo de seleccion de los registros en el Grid 
	NS.tipoSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columnaSelec = new Ext.grid.ColumnModel([
	    NS.tipoSeleccion,
	    {header: 'No. Solicitud',width: 50,dataIndex: 'noSolicitud', sortable: true, hidden: true},
	    {header: 'No. Empresa',width: 50,dataIndex: 'noEmpresa', sortable: true, hidden: true},
	    {header: 'Empresa',width: 200,dataIndex: 'nomEmpresa', sortable: true,
	    	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        }},
	    {header: 'No. Banco', width: 50, dataIndex: 'noBanco', sortable: true, hidden: true},
	    {header: 'Banco', width: 110, dataIndex: 'nomBanco', sortable: true,
	    	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        }},
	    {header: 'Chequera', width: 90, dataIndex: 'idChequera', sortable: true,
	    	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        }},
	    {header: 'Importe Traspaso', width: 100, dataIndex: 'importeTraspaso', sortable: true, css: 'text-align:right;', 
        	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return '$ ' + NS.formatNumber(value);
        }},
	    {header: 'No. Empresa Benef',width: 50,dataIndex: 'noEmpresaBenef', sortable: true, hidden: true},
	    {header: 'Empresa Concentradora',width: 200,dataIndex: 'nomEmpresaBenef', sortable: true,
	    	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        }},
	    {header: 'No. Banco Benef', width: 50, dataIndex: 'noBancoBenef', sortable: true, hidden: true},
	    {header: 'Banco Benef', width: 110, dataIndex: 'nomBancoBenef', sortable: true,
	    	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        }},
	    {header: 'Chequera Benef', width: 90, dataIndex: 'idChequeraBenef', sortable: true,
	    	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        }},
	    {header: 'Divisa', width: 60, dataIndex: 'descDivisa', sortable: true,
	    	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        }},
	    {header: 'Concepto', width: 200, dataIndex: 'concepto', sortable: true,
	    	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        }},
	    {header: 'Autoriza', width: 80, dataIndex: 'claveUsuario', sortable: true,
	    	renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        }}
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
	    columnLines: true
	});
	
	NS.labSaldoFin = new Ext.form.Label({
		text: 'Saldo Final',
        x: 10,
        y: 275
	});
    
	NS.txtSaldoFin = new Ext.form.TextField({
		id: PF + 'txtSaldoFin',
		x: 10,
        y: 290,
        width: 100,
        disabled: true
    });
	
	var winLogin = new Ext.Window({
		title: 'SET',
		modal:true,
		shadow:true,
		width: 200,
	   	height:200,
	   	minWidth: 400,
	   	minHeight: 580,
	   	layout: 'fit',
	   	plain:true,
	    bodyStyle:'padding:10px;',
	   	buttonAlign:'center',
	   	closable: false,
	   	items: [
	   	        {
				   	xtype: 'fieldset',
			        title: 'Contraseña',
			        id:PF+'framePendiente',
			        name:PF+'framePendientes',
			       	x: 0,
			        y: 0,
			        width: 80,
			        height: 100,
			        layout: 'absolute',
			        value: '',
			        items: [
			                {
					       		inputType: 'password',
								xtype: 'textfield',
					       		id:PF+'txtPsw',
					       		name:PF+'txtPsw',
					       		x:20,
					       		y:10,
					       		width:100,
					       		hidden:false,
					       		allowBlank: false,
					       		blankText:'Introdusca su Contraseña'
			                }
			                ]
	   	        }
	   	        ],
	   	        buttons: [
					   	{
						 	text: 'Aceptar',
							handler: function(e) {
						   		NS.seleccionaDatos(NS.autDes);
							 }
					     },{
						 	text: 'Cancelar',
							handler: function(e) {
					    	 	winLogin.hide();
							 }
					     }
					     ]
	});
	
	NS.contCuentas = new Ext.form.FieldSet({
		title: '',
        y: 90,
        width: 995,
        height: 335,
        layout: 'absolute',
        items: [
               NS.gridDatos,
               NS.labSaldoFin, 		NS.txtSaldoFin,
               {
            	   xtype: 'button',
            	   text: 'Ejecutar',
                   x: 610,
                   y: 290,
                   width: 80,
                   height: 22,
                   listeners: {
                   		click: {
            	   			fn:function(e) {
            	   				NS.seleccionaDatos('EJE');
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Regresar',
            	   x: 700,
            	   y: 290,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				NS.seleccionaDatos('REG');
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Autorizar',
            	   x: 790,
            	   y: 290,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				NS.autDes = 'AUT';
            	   				Ext.getCmp(PF + 'txtPsw').setValue('');
            	   				winLogin.show();
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Des-Autorizar',
            	   x: 880,
            	   y: 290,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				NS.autDes = 'DES';
            	   				Ext.getCmp(PF + 'txtPsw').setValue('');
            	   				winLogin.show();
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
	
	NS.buscar = function() {
		if(NS.cmbEmpresas.getValue() == '') {
			Ext.Msg.alert('SET', 'Seleccione una empresa');
			return;
		}
		if(NS.cmbDivisa.getValue() == '') {
			Ext.Msg.alert('SET', 'Seleccione una divisa');
			return;
		}
		NS.storeDatos.baseParams.idDivisa = NS.cmbDivisa.getValue();
		NS.storeDatos.baseParams.idBanco = '' + NS.cmbBanco.getValue();
		NS.storeDatos.load();
	};
	
	NS.seleccionaDatos = function(opcion) {
		var selBarrido = NS.gridDatos.getSelectionModel().getSelections();
		var matDatos  = new Array();
		var cadenaJson = '';
		NS.solicitudes = '';
		
		if(selBarrido.length == 0) {
			Ext.Msg.alert('SET', 'Seleccione al menos un registro');
			return;
		}
		
		for(var i=0; i<selBarrido.length; i++) {
			var vec = {};
			vec.noSolicitud = selBarrido[i].get('noSolicitud');
			matDatos[i] = vec;
			NS.solicitudes = NS.solicitudes + selBarrido[i].get('noSolicitud') + ',';
		}
		NS.solicitudes = NS.solicitudes.substring(0, NS.solicitudes.length-1);
		cadenaJson = Ext.util.JSON.encode(matDatos);
		
		if(opcion == 'AUT' || opcion == 'DES')
			NS.confirmaBarrido(cadenaJson, opcion);
		else if(opcion == 'EJE')
			NS.ejecutar(cadenaJson);
		else if(opcion == 'REG')
			NS.regresarBarrido(cadenaJson);
	};
	
	NS.confirmaBarrido = function(cadenaJson, opcion) {
		BarridoInversionAction.confirmaBarrido(cadenaJson, opcion, Ext.getCmp(PF + 'txtPsw').getValue(), function(res, e){
			if(res == 'Contraseña incorrecta') {
				Ext.Msg.alert('SET', res);
				Ext.getCmp(PF + 'txtPsw').setValue('');
				return;
			}
			Ext.Msg.alert('SET', res);
			
			NS.gridDatos.store.removeAll();
			NS.gridDatos.getView().refresh();
			winLogin.hide();
			NS.buscar();
		});
	};
	
	NS.ejecutar = function(cadenaJson) {
		//NS.insertaSolicitudBarrido();
		
		BarridoInversionAction.ejecutarBarridos(cadenaJson, function(res, e){
			Ext.Msg.alert('SET', res);
			NS.gridDatos.store.removeAll();
			NS.gridDatos.getView().refresh();
			
			NS.buscar();
			NS.imprimir();
			NS.imprimeCanastas();
		});
	};
	
	NS.regresarBarrido = function(cadenaJson) {
		BarridoInversionAction.regresarBarrido(cadenaJson, function(res, e){
			Ext.Msg.alert('SET', res);
			
			NS.gridDatos.store.removeAll();
			NS.gridDatos.getView().refresh();
			
			NS.buscar();
		});
	};
	
	NS.insertaSolicitudBarrido = function() {
		var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Registrando traspaso..."});
		myMask.show();
		
		var selBarrido = NS.gridDatos.getSelectionModel().getSelections();
		var matDatos  = new Array();
		
		for(var i=0; i<selBarrido.length; i++) {
			var reg = {};
			reg.empresa = selBarrido[i].get('noEmpresa');
			reg.operacion = 3801;
			reg.chequera = selBarrido[i].get('idChequera');
			reg.banco = selBarrido[i].get('noBanco');
			reg.importe = selBarrido[i].get('importeTraspaso');
			reg.fecha = apps.SET.FEC_HOY;
			reg.usuario = apps.SET.iUserId;
			reg.bancoBenef = selBarrido[i].get('noBancoBenef');
			reg.chequeraBenef = selBarrido[i].get('idChequeraBenef');
			reg.concepto = 'Traspaso para inversion';
			reg.caja = apps.SET.ID_CAJA;
			reg.beneficiario = selBarrido[i].get('nomEmpresaBenef');
			reg.beneficiarion = selBarrido[i].get('nomEmpresa');
			reg.cliente = selBarrido[i].get('noEmpresaBenef');
			reg.regreso = false;
			reg.bandera = false;
			reg.referencia = '';
			reg.ubicacion = 'DESC';
			reg.idGrupoIngreso= 0; 
   			reg.idRubroIngreso= 0; 
   			reg.idGrupoEgreso= 0; 
   			reg.idRubroEgreso= 0; 
   			reg.noDocto = selBarrido[i].get('noSolicitud');
   			matDatos[i] = reg;
		}
		var jsonString2 = Ext.util.JSON.encode(matDatos);
		
		TraspasosAction.insertarSolicitudTraspaso(jsonString2, function(result, e) {
			myMask.hide();
			
			if(result.msgUsuario !== null  &&  result.msgUsuario !== '')
				Ext.Msg.alert('SET', result.msgUsuario);
		});
	};
	
	//Funcion para limpiar los campos
	NS.limpiar = function(){
		NS.cmbEmpresas.reset();
		NS.cmbDivisa.reset();
		NS.cmbBanco.reset();
		NS.storeBancos.removeAll();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
	};
	
	NS.imprimir = function(){
		var nomReporte = 'Barridos';
		var estatus = '';
		var desc = 'realizados del';
		
		var strParams = '?nomReporte=' + nomReporte;
		strParams += '&'+'nomParam1=noEmpresa';
		strParams += '&'+'valParam1=' + NS.cmbEmpresas.getValue();
		strParams += '&'+'nomParam2=pFecIni';
		strParams += '&'+'valParam2=' + apps.SET.FEC_HOY;
		strParams += '&'+'nomParam3=pFecFin';
		strParams += '&'+'valParam3=' + apps.SET.FEC_HOY;
		strParams += '&'+'nomParam4=idDivisa';
		strParams += '&'+'valParam4=' + NS.cmbDivisa.getValue();
		strParams += '&'+'nomParam5=nomReporte';
		strParams += '&'+'valParam5=' + nomReporte;
		strParams += '&'+'nomParam6=estatus';
		strParams += '&'+'valParam6=' + estatus;
		strParams += '&'+'nomParam7=usuario';
		strParams += '&'+'valParam7=' + apps.SET.iUserId;
		strParams += '&'+'nomParam8=desc';
		strParams += '&'+'valParam8=' + desc;
		strParams += '&'+'nomParam9=tipoRep';
		strParams += '&'+'valParam9=' + '1';
		strParams += '&'+'nomParam10=solic';
		strParams += '&'+'valParam10=' + NS.solicitudes;
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		return;
	};
	
	NS.imprimeCanastas = function() {
		var nomReporte = 'CanastasPendientes';
		var strParam = '?nomReporte=' + nomReporte;
		
		strParam += '&'+'nomParam1=pFecIni';
		strParam += '&'+'valParam1=' + '01/01/2015';
		strParam += '&'+'nomParam2=pFecFin';
		strParam += '&'+'valParam2=' + '01/01/2015';
		strParam += '&'+'nomParam3=noConcentradora';
		strParam += '&'+'valParam3=' + NS.cmbEmpresas.getValue();				
		strParam += '&'+'nomParam4=nomReporte';
		strParam += '&'+'valParam4=' + nomReporte;
		strParam += '&'+'nomParam5=solic';
		strParam += '&'+'valParam5=' + NS.solicitudes;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParam);
		return;
	};
	
	//Contenedor principal del formulario
	NS.ConfirmaBarrido = new Ext.FormPanel({
		title: 'Confirma barrido de inversiones',
	    width: 1020,
	    height: 500,
	    frame: true,
	    padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF + 'confirmaBarrido',
	    renderTo: NS.tabContId,
	    items:[
	           NS.contGeneral
	    ]
	});
	NS.ConfirmaBarrido.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});