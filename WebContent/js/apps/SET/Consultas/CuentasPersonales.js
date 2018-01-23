/* 
 * @author: Victor H. Tello
 * @since: 15/Mayo/2014
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Consultas.CuentasPersonales');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	var sName="";
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	NS.idUser = 0;
	NS.idBanco = 0;
	NS.idChequera = '';
	NS.idDivisa = '';
	NS.banco = '';
	NS.chequera = '';
	
	NS.saldoIni = 0;
	NS.ingresos = 0;
	NS.egresos = 0;
	NS.saldoFin = 0;
	NS.bandera = '';
	
	NS.unformatNumber = function(num) {
		return num.replace(/(,)/g,''); 
	};
	
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
	
	NS.labUsuario = new Ext.form.Label({
		text: 'Usuario',
        x: 10
	});
	
	NS.storeUsuario = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultasAction.obtenerUsuarios, 
		idProperty: 'idUsuario', 
		fields: [
			 {name: 'idUsuario'},
			 {name: 'nomUsuario'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUsuario, msg:"Cargando..."});
			}
		}
	}); 
	NS.storeUsuario.load();
	
	NS.cmbUsuario = new Ext.form.ComboBox({
		store: NS.storeUsuario,
		id: PF + 'cmbUsuario',
		x: 10,
        y: 15,
        width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idUsuario',
		displayField: 'nomUsuario',
		autocomplete: true,
		emptyText: 'Seleccione un Usuario',
		triggerAction: 'all',
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idUser = combo.getValue();
				}
			}
		}
	});
	
	//Etiqueta divisa
	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa',
		x: 220
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
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('SET','No hay divisas disponibles');
				
				//Se agrega la opcion todos los bancos
	 			var recDivisa = NS.storeDivisa.recordType;	
				var todos = new recDivisa({
					idDivisa: 0,
					descDivisa: '****** TODAS ******'
		       	});
				NS.storeDivisa.insert(0, todos);
//		   		Ext.getCmp(PF + 'cmbDivisa').setValue('****** TODAS ******');
			}
		}
	});
	NS.storeDivisa.load();
	
	//Combo Divisas (obligatorio)
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa,
		id: PF+'cmbDivisa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 220,
        y: 15,
        width: 170,
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
					NS.cambiaBanco(combo.getValue());
					NS.idDivisa = combo.getValue();
				}
			}
		}
	});
	
	//Refresca el combo bancos en base a la empresa
	NS.cambiaBanco = function(valueCombo){
		NS.cmbBanco.reset();
		NS.cmbChequeras.reset();
		NS.storeBancos.removeAll();
		NS.storeChequeras.removeAll();
		
		if((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0) {
			NS.storeBancos.baseParams.idUsuario = NS.idUser;
			NS.storeBancos.baseParams.idDivisa = valueCombo;
			NS.storeBancos.load();
		}
	};
	//Etiqueta Banco
	NS.labBanco = new Ext.form.Label({
		text: 'Banco',
		x: 400
	});
	
	//Store banco
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['idUsuario', 'idDivisa'],
		directFn: ConsultasAction.obtenerBancos, 
		idProperty: 'idBanco', 
		fields: [
			 {name: 'idBanco'},
			 {name: 'descBanco'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen bancos!!');
				
				//Se agrega la opcion todos los bancos
	 			var recordsStoreBancos = NS.storeBancos.recordType;	
				var todos = new recordsStoreBancos({
					idBanco: 0,
					descBanco: '****** TODOS ******'
		       	});
				NS.storeBancos.insert(0, todos);
			}
		}
	});
	
	//Combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancos,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 400,
	    y: 15,
	    width: 170,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'idBanco',
		displayField:'descBanco',
		autocomplete: true,
		emptyText: 'Seleccione banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 3,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.cambiaChequera(combo.getValue());
				}
			}
		}
	});
	//Refresca el combo de chequeras segun el banco y la empresa seleccionados
	NS.cambiaChequera = function(valueCombo) {
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();
		
		if((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0) {
			NS.storeChequeras.baseParams.idBanco = valueCombo;
			NS.storeChequeras.baseParams.idUsuario = NS.idUser;
			NS.storeChequeras.baseParams.idDivisa = NS.idDivisa;
			NS.storeChequeras.load();
			NS.idBanco = valueCombo;
    	}
	};
	
	
	
	//Etiqueta chequera
	NS.labChequera = new Ext.form.Label({
		text: 'Chequera',
		x: 580
	});
	
	//storeChequeras en base al banco seleccionado
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder: ['idBanco', 'idUsuario', 'idDivisa'],
		directFn: ConsultasAction.obternerChequeras,
		idProperty: 'idChequera',
		fields: [
		         {name: 'idChequera'},
		         {name: 'idChequera'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					if(NS.idBanco != 0) Ext.Msg.alert('SET', 'No existen chequeras para este banco');
				/*
				//Se agrega la opcion todos los bancos
	 			var recCheque = NS.storeChequeras.recordType;	
				var todos = new recCheque({
					idChequera: 0,
					idChequera: '****** TODAS ******'
		       	});
				NS.storeChequeras.insert(0, todos);
		   		Ext.getCmp(PF + 'cmbChequeras').setValue('****** TODAS ******');
		   		*/
			}
		}
	});
	
	//Combo Chequeras en base al banco
	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras,
		id: PF + 'cmbChequeras',
		x: 580,
		y: 15,
		width: 170,
		tabIndex: 4,
		typeAhead: true,
		selecOnFocus: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'idChequera',
		displayField: 'idChequera',
		emptyText: 'Seleccione una chequera',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn:function(combo, valor){
					NS.idChequera = combo.getValue();
				}
			}
		}
	});
	
	//Contenedor de Busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: 'búsqueda',
		width: 985,
	    height: 80,
	    layout: 'absolute',
	    items:[
	        NS.labUsuario,
	        NS.cmbUsuario,
	        NS.labBanco,
            NS.cmbBanco,
            NS.labChequera,
            NS.cmbChequeras,
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
	        			fn:function(e)	{
	        				var idUsu = NS.cmbUsuario.getValue();
	        				
	        				if(idUsu == '') {
	        					Ext.Msg.alert('SET','Seleccione un usuario valido!!');
								return;
	        				}
	        				
	        				ConsultasAction.obtenerFacultad(idUsu, function(resu, e) {
								if(resu == 0){
									Ext.Msg.alert('SET','Usted no cuenta con la facultad para consultar cuentas personales!!');
									return;
								}
							}
	        				);
	        	
				        	NS.txtSaldoIni.setValue('');
				    		NS.txtIngresos.setValue('');
				    		NS.txtEgresos.setValue('');
				            NS.txtSaldoFin.setValue('');			
				            NS.buscarRegistro();
	        			}
	        		}
	        	}
	        },{
	        	xtype: 'button',
	        	text: 'Ctas. personales',
	        	id: PF + 'txtCtasPer',
	        	x: 775,
	        	y: 50,
	        	width: 80,
	        	height: 22,
	        	hidden: true,
	        	listeners: {
	        		click:{
	        			fn:function(e){
	        				NS.importaCtasPersonales();
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
		paramOrder: ['datos'],
		directFn: ConsultasAction.obtenerRegistrosCP,
		fields: [
			{name: 'idUsuario'},
			{name: 'nomUsuario'},
			{name: 'idBanco'},
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'descChequera'},
			{name: 'idDivisa'},
			{name: 'saldoInicial'},
			{name: 'cargo'},
			{name: 'abono'},
			{name: 'saldoFinal'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
				var ini = 0;
				var ing = 0;
				var car = 0;
				var fin = 0;
				
				if(records.length == null || records.length <=0) {
					Ext.Msg.alert('SET', 'No existen datos con estos parametros');
				}else {
					for(var k=0; k<records.length; k++) {
      					ini += records[k].get('saldoInicial');
      					ing += records[k].get('abono');
      					car += records[k].get('cargo');
      					fin += records[k].get('saldoFinal');
          			}
					NS.txtSaldoIni.setValue(NS.formatNumber(ini));
					NS.txtIngresos.setValue(NS.formatNumber(ing));
					NS.txtEgresos.setValue(NS.formatNumber(car));
					NS.txtSaldoFin.setValue(NS.formatNumber(fin));
					
				}
			}
		}
	});
	//Para indicar el modo de seleccion de los registros en el Grid 
	NS.tipoSeleccion = new Ext.grid.CheckboxSelectionModel ({singleSelect: false});
	
	//Columna de Seleccion  
	NS.columnaSelec = new Ext.grid.ColumnModel([
	    //NS.tipoSeleccion,
	    {header: 'No. Usuario',width: 50,dataIndex: 'idUsuario', sortable: true, hidden: true},
	    {header: 'Beneficiario',width: 200,dataIndex: 'nomUsuario', sortable: true},
	    {header: 'No. Banco', width: 50, dataIndex: 'idBanco', sortable: true, hidden: true},
	    {header: 'Banco', width: 120, dataIndex: 'descBanco', sortable: true},
	    {header: 'Descripción', width: 125, dataIndex: 'descChequera', sortable: true, hidden: true},
	    {header: 'Chequera', width: 90, dataIndex: 'idChequera', sortable: true},
	    {header: 'Divisa', width: 45, dataIndex: 'idDivisa', sortable: true},
	    {header: 'Saldo Inicial', width: 90, dataIndex: 'saldoInicial', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Ingresos', width: 90, dataIndex: 'abono', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Egresos', width: 90, dataIndex: 'cargo', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Saldo Final', width: 90, dataIndex: 'saldoFinal', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney}
	]);
	//Grid para mostrar los datos seleccionados	
	NS.gridDatos = new Ext.grid.GridPanel({
		store: NS.storeDatos,
		id: 'gridDatos',
		cm: NS.columnaSelec,
		//sm: NS.tipoSeleccion,
		width: 960,
	    height: 280,
	    stripeRows: true,
	    columnLines: true,
		listeners: {
			dblclick: {
				fn:function(grid) {
					var recordsEliminar = NS.gridDatos.getSelectionModel().getSelections();
				}
			}
		}
	});
	
	NS.labSaldoIni = new Ext.form.Label({
		text: 'Saldo inicial',
        x: 10,
        y: 285
	});
    
	NS.txtSaldoIni = new Ext.form.TextField({
		id: PF + 'txtSaldoIni',
        name:PF + 'txtSaldoIni',
		x: 10,
        y: 300,
        width: 100,
        disabled: true
    });
	
	NS.labIngresos = new Ext.form.Label({
		text: 'Ingresos',
        x: 120,
        y: 285
	});
    
	NS.txtIngresos = new Ext.form.TextField({
		id: PF + 'txtIngresos',
        name:PF + 'txtIngresos',
		x: 120,
        y: 300,
        width: 100,
        disabled: true
    });
	NS.labEgresos = new Ext.form.Label({
		text: 'Egresos',
        x: 230,
        y: 285
	});
    
	NS.txtEgresos = new Ext.form.TextField({
		id: PF + 'txtEgresos',
        name:PF + 'txtEgresos',
		x: 230,
        y: 300,
        width: 100,
        disabled: true
    });
	NS.labSaldoFin = new Ext.form.Label({
		text: 'Saldo Final',
        x: 340,
        y: 285
	});
    
	NS.txtSaldoFin = new Ext.form.TextField({
		id: PF + 'txtSaldoFin',
        name:PF + 'txtSaldoFin',
		x: 340,
        y: 300,
        width: 100,
        disabled: true
    });
	//Contenedor de Transferencia o Factoraje
	NS.contCuentas = new Ext.form.FieldSet({
		title: 'Cuentas',
        y: 90,
        width: 985,
        height: 360,
        layout: 'absolute',
        items: [
               NS.gridDatos,
               NS.labSaldoIni,
               NS.txtSaldoIni,
               NS.labIngresos,
               NS.txtIngresos,
               NS.labEgresos,
               NS.txtEgresos,
               NS.labSaldoFin,
               NS.txtSaldoFin, 
               {
            	   xtype: 'button',
            	   text: 'Limpiar',
                   x: 790,
                   y: 300,
                   width: 80,
                   height: 22,
                   listeners: {
                   		click: {
            	   			fn:function(e) {
            	   				NS.limpiar();
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Imprimir',
            	   x: 880,
            	   y: 300,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				NS.imprimir();
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Nuevo',
            	   x: 700,
            	   y: 300,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
			            	   	if(NS.cmbUsuario.getValue() == '') {
				           			Ext.Msg.alert('SET', 'Seleccione un usuario valido!!');
				           			return;
				           		}
			            	   	
			            	   	if(NS.cmbDivisa.getValue() == '' || NS.cmbDivisa.getValue() == '0') {
				           			Ext.Msg.alert('SET', 'Seleccione una divisa valida!!');
				           			return;
				           		}
        	   					NS.bandera = "N";
            	   				NS.limpiarNuevo();
            	   				NS.storeNvoBancos.load();
            	   				winNuevo.show();
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Eliminar',
            	   x: 600,
            	   y: 300,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				var banco = 0;
            	   				var chequera = '';
            	   				var recordsEliminar = NS.gridDatos.getSelectionModel().getSelections();
            	   				
            	   				if(recordsEliminar.length>0){
	            	   				Ext.Msg.confirm('confirmación','¿Estas seguro de  eliminar este registro?',function(btn){
	            	   					if(btn === 'yes'){
	            	   						chequera = recordsEliminar[0].get('idChequera');
	            	   						banco = recordsEliminar[0].get('idBanco');
	            	   						ConsultasAction.eliminarCP(banco,chequera, function(res, e){
	        											if(res !== null && res !== undefined && res !== '')
	        											{
	        												Ext.Msg.alert('SET','Registro Eliminado Correctamente');
	        												NS.buscarRegistro();
	        											}
	        										}
	        									);
	            	   					}else{
	            	   						return;
	            	   					}	
	            	   				});
               					}else{
               						Ext.Msg.alert('SET','Debe seleccionar un registro');
               					}
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Modificar',
            	   x: 500,
            	   y: 300,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {	
            	   				var recordsModif = NS.gridDatos.getSelectionModel().getSelections();        	   				
            	   				if(recordsModif.length>0){
            	   					if(NS.cmbDivisa.getValue() !== ''){
	            	   					NS.bandera = "M";
	            	   					NS.banco = recordsModif[0].get('idBanco');
	            	   					NS.chequera = recordsModif[0].get('idChequera');
	            	   					NS.storeNvoBancos.load();
	            	   					
	            	   					//Ext.getCmp(PF + 'cmbNvoBanco').setValue(recordsModif[0].get('idBanco'));
	            	   					Ext.getCmp(PF + 'txtChequera').setValue(recordsModif[0].get('idChequera'));
	            	   					Ext.getCmp(PF + 'txtClabe').setValue(recordsModif[0].get('descChequera'));
	            	   					Ext.getCmp(PF + 'txtNvoSaldoIni').setValue(recordsModif[0].get('saldoInicial'));
	            	   					Ext.getCmp(PF + 'txtNvoIngresos').setValue(recordsModif[0].get('abono'));
	            	   					Ext.getCmp(PF + 'txtNvoEgresos').setValue(recordsModif[0].get('cargo'));
	            	   					Ext.getCmp(PF + 'txtNvoSaldoFin').setValue(recordsModif[0].get('saldoFinal'));
	            	   					NS.saldoIni = recordsModif[0].get('saldoInicial');
	            	   					NS.ingresos = recordsModif[0].get('abono');
	            	   					NS.egresos = recordsModif[0].get('cargo');
	            	   					NS.saldoFin = recordsModif[0].get('saldoFinal');
	                	   				winNuevo.show();
            	   					}else{
            	   						Ext.Msg.alert('SET','Debe elegir usuario y divisa para poder modificar');
            	   						return;
            	   					}
               					}else{
               						Ext.Msg.alert('SET','Debe seleccionar un registro');
               					}
            	   			}
               			}
               		}
               }
        ]
    });
	//Contenedor General
	NS.contGeneral = new Ext.form.FieldSet({
		title: '',
		width: 1010,
	    height: 480,
	    x: 20,
	    y: 5,
	    layout: 'absolute',
	    items:[
			NS.contBusqueda,
			NS.contCuentas
		]
	});
	
	//******************************************  Para dar de alta una nueva chequera **************************************////
	NS.labNvoBanco = new Ext.form.Label({
		text: 'Banco'
	});
	
	//Store banco
	NS.storeNvoBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultasAction.obtenerNvoBancos, 
		idProperty: 'idBanco', 
		fields: [
			 {name: 'idBanco'},
			 {name: 'descBanco'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeNvoBancos, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen bancos!!');
			}
		}
	});
	
	//Combo banco
	NS.cmbNvoBanco = new Ext.form.ComboBox({
		store: NS.storeNvoBancos,
		id: PF + 'cmbNvoBanco',
		name: PF + 'cmbNvoBanco',
		y: 15,
	    width: 170,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'idBanco',
		displayField:'descBanco',
		autocomplete: true,
		emptyText: 'Seleccione banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 3,
		listeners:{
			select:{
				fn:function(combo, valor) {
				}
			}
		}
	});
	
	NS.labChequera = new Ext.form.Label({
		text: 'Chequera',
		x: 180
	});
	
	NS.txtChequera = new Ext.form.TextField({
		id: PF + 'txtChequera',
        name:PF + 'txtChequera',
		x: 180,
        y: 15,
        width: 150
    });
	
	NS.labClabe = new Ext.form.Label({
		text: 'Descripción cta.',
		x: 340
	});
	
	NS.txtClabe = new Ext.form.TextField({
		id: PF + 'txtClabe',
        name:PF + 'txtClabe',
		x: 340,
        y: 15,
        width: 150,
        style:'text-transform: uppercase'
    });
	
	NS.labNvoSaldoIni = new Ext.form.Label({
		text: 'Saldo inicial',
        y: 50
	});
    
	NS.txtNvoSaldoIni = new Ext.form.TextField({
		id: PF + 'txtNvoSaldoIni',
        y: 65,
        width: 100,
        listeners:{
			change:{
				fn: function(caja,valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						NS.saldoIni = caja.getValue();
						Ext.getCmp(PF + 'txtNvoSaldoFin').setValue(NS.formatNumber(parseFloat(NS.saldoIni) + parseFloat(NS.ingresos) - parseFloat(NS.egresos)));
						caja.setValue(NS.formatNumber(caja.getValue()));
					}
                }
			}
    	}
    });
	
	NS.labNvoIngresos = new Ext.form.Label({
		text: 'Ingresos',
        x: 120,
        y: 50
	});
    
	NS.txtNvoIngresos = new Ext.form.TextField({
		id: PF + 'txtNvoIngresos',
		x: 120,
        y: 65,
        width: 100,
        listeners:{
			change:{
				fn: function(caja,valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						NS.ingresos = caja.getValue();
						Ext.getCmp(PF + 'txtNvoSaldoFin').setValue(NS.formatNumber(parseFloat(NS.saldoIni) + parseFloat(NS.ingresos) - parseFloat(NS.egresos)));
						caja.setValue(NS.formatNumber(caja.getValue()));
					}
                }
			}
    	}
    });
	
	NS.labNvoEgresos = new Ext.form.Label({
		text: 'Egresos',
        x: 240,
        y: 50
	});
    
	NS.txtNvoEgresos = new Ext.form.TextField({
		id: PF + 'txtNvoEgresos',
		x: 240,
        y: 65,
        width: 100,
        listeners:{
			change:{
				fn: function(caja,valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						NS.egresos = caja.getValue();
						Ext.getCmp(PF + 'txtNvoSaldoFin').setValue(NS.formatNumber(parseFloat(NS.saldoIni) + parseFloat(NS.ingresos) - parseFloat(NS.egresos)));
						caja.setValue(NS.formatNumber(caja.getValue()));
					}
                }
			}
    	}
    });
	
	NS.labNvoSaldoFin = new Ext.form.Label({
		text: 'Saldo Final',
        x: 360,
        y: 50
	});
    
	NS.txtNvoSaldoFin = new Ext.form.TextField({
		id: PF + 'txtNvoSaldoFin',
		x: 360,
        y: 65,
        width: 100,
        disabled: true
    });
	
	NS.contNuevo = new Ext.form.FieldSet({
		width: 530,
	    height: 180,
	    layout: 'absolute',
	    items:[
	           NS.labNvoBanco,		NS.cmbNvoBanco,		NS.labChequera,
	           NS.txtChequera,		NS.labClabe,		NS.txtClabe,
	           NS.labNvoSaldoIni,	NS.txtNvoSaldoIni,	NS.labNvoIngresos,
	           NS.txtNvoIngresos,	NS.labNvoEgresos,	NS.txtNvoEgresos,
	           NS.labNvoSaldoFin,	NS.txtNvoSaldoFin,
	   	        {
            	   xtype: 'button',
            	   text: 'Aceptar',
            	   x: 320,
            	   y: 100,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
	        	   				NS.insertarNuevo();
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Cancelar',
            	   x: 410,
            	   y: 100,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				NS.limpiarNuevo();
            	   				winNuevo.hide();
            	   			}
               			}
               		}
	               }
		]
	});
	var winNuevo = new Ext.Window({
		title: 'Alta de nuevo registro',
		modal: true,
		shadow: true,
		width: 550,
	   	height: 200,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    closable: false,
	   	items: [
	   	        NS.contNuevo
	   	        ]
	});
	
	NS.insertarNuevo = function() {
		var reg = {}; 
		var mat = new Array();
		
		reg.idUsuario = NS.cmbUsuario.getValue();
		reg.idDivisa = NS.cmbDivisa.getValue();
		reg.idBanco = NS.cmbNvoBanco.getValue();
		reg.idChequera = NS.txtChequera.getValue();
		reg.clabe = NS.txtClabe.getValue();
		reg.saldoIni = NS.unformatNumber(NS.txtNvoSaldoIni.getValue());
		reg.saldoIng = NS.unformatNumber(NS.txtNvoIngresos.getValue());
		reg.saldoEgr = NS.unformatNumber(NS.txtNvoEgresos.getValue());
		reg.saldoFin = NS.unformatNumber(NS.txtNvoSaldoFin.getValue());
		reg.bandera = NS.bandera;
		reg.bancoMod = NS.banco;
		reg.chequeraMod = NS.chequera;
        mat[0] = reg;
		
        var jsonString = Ext.util.JSON.encode(mat);
        
		ConsultasAction.insertaNuevoCP(jsonString, function(res){
			Ext.Msg.alert('SET', res);
			
			if(res == 'Registro exitoso!!' || res == 'Registro modificado!') {
				NS.limpiarNuevo();
				winNuevo.hide();
				NS.buscarRegistro();
			}
		});
	};
	
	NS.limpiarNuevo = function() {
		NS.cmbNvoBanco.reset();
		NS.storeNvoBancos.removeAll();
		NS.txtChequera.setValue('');
		NS.txtClabe.setValue('');
		NS.txtNvoSaldoIni.setValue('');
		NS.txtNvoIngresos.setValue('');
		NS.txtNvoEgresos.setValue('');
        NS.txtNvoSaldoFin.setValue('');
	};
	
	NS.buscarRegistro = function() {
		var reg = {}; 
		var mat = new Array();
		
		if(NS.cmbUsuario.getValue() == '') {
			Ext.Msg.alert('SET', 'Seleccione un usuario valido!!');
			return;
		}
		reg.idUsuario = NS.cmbUsuario.getValue();
		reg.idDivisa = NS.cmbDivisa.getValue();
		reg.idBanco = NS.cmbBanco.getValue();
		reg.idChequera = NS.cmbChequeras.getValue();
		mat[0] = reg;
		
        var jsonString = Ext.util.JSON.encode(mat);
        
        NS.storeDatos.baseParams.datos = jsonString;
        NS.storeDatos.load();
	};
	
	//Funcion para importar los saldos (provisional)
	NS.importaCtasPersonales = function() {
		Ext.Msg.confirm('SET', '¿Desea cargar las cuentas personales del Excel?', function(btn) {
			if(btn == 'yes') {
				ConsultasAction.importaCtasPersonales(function(res, e) {
					Ext.Msg.alert('SET', res + '!!');
				});
			}
		});
	};
	
	NS.habilitaBotones = function() {
		ConsultasAction.validaFacultad(163, function(result, e) {
			if(result == 0)
				Ext.getCmp(PF + 'txtCtasPer').setVisible(false);
			else
				Ext.getCmp(PF + 'txtCtasPer').setVisible(true);
		});
	};
	
	NS.habilitaBotones();
	
	//Funcion para limpiar los campos
	NS.limpiar = function(){
		NS.cmbUsuario.reset();
		NS.cmbBanco.reset();
		NS.storeBancos.removeAll();
		NS.cmbDivisa.reset();
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
		NS.txtSaldoIni.setValue('');
		NS.txtIngresos.setValue('');
		NS.txtEgresos.setValue('');
        NS.txtSaldoFin.setValue('');	
	};
	
	//Función para la llamada al reporte de pago de propuestas
	NS.imprimir = function(){
		var strParams = '?nomReporte=CuentasPersonales';
		
		strParams += '&'+'nomParam1=idUsuario';
		strParams += '&'+'valParam1=' + NS.cmbUsuario.getValue();
		strParams += '&'+'nomParam2=idBanco';
		strParams += '&'+'valParam2=' + NS.cmbBanco.getValue();
		strParams += '&'+'nomParam3=idChequera';
		strParams += '&'+'valParam3=' + NS.cmbChequeras.getValue();
		strParams += '&'+'nomParam4=idDivisa';
		strParams += '&'+'valParam4=' + NS.idDivisa;
		strParams += '&'+'nomParam5=fecHoy';
		strParams += '&'+'valParam5=' + apps.SET.FEC_HOY;
		strParams += '&'+'nomParam6=noUsuario';
		strParams += '&'+'valParam6=' + NS.idUsuario;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		return;
	};
	
	//Contenedor principal del formulario
	NS.CuentasPersonales = new Ext.FormPanel({
		title: 'Cuentas Personales',
	    width: 1020,
	    height: 500,
	    frame: true,
	    padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF + 'cuentasPersonales',
	    renderTo: NS.tabContId,
	    items:[
	           NS.contGeneral
	    ]
	});
	NS.CuentasPersonales.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});