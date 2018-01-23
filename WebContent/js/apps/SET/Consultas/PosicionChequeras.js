/* 
 * @author: Victor H. Tello
 * @since: 19/Octubre/2012
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Consultas.PosicionChequeras');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	var sName="";
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	NS.noEmpresa = 0;
	NS.idBanco = 0;
	NS.idChequera = '';
	NS.idDivisa = '';
	NS.record = 0;
	NS.chkAutomatico = false;
	NS.iTieneBanca = 0;
	NS.bAutomatico = false;
	
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
        x: 10,
        y: 0
	});
    //Caja numero de empresa
	NS.txtEmpresa = new Ext.form.TextField({
		id: PF + 'txtEmpresa',
        name:PF + 'txtEmpresa',
		x: 10,
        y: 15,
        width: 50,
        tabIndex: 0,
        listeners:{
			change:{
				fn: function(caja,valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
						
						if(valueCombo != null && valueCombo != undefined && valueCombo != '')
							NS.cambiaBanco(valueCombo);
						else
							NS.cambiaBanco(0);
					}else {
						NS.cambiaBanco(0);
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
					}
                }
			}
    	}
    });
	//Store empresas
    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
    		idUsuario: NS.idUsuario
		},
		paramOrder:['idUsuario'],
		directFn: ConfirmacionTransferenciasAction.obtenerEmpresas, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene empresas asignadas');
				}
				//Se agrega la opcion todas las empresas
	 			var recordsStoreEmpresas = NS.storeEmpresas.recordType;	
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '*************** TODAS ***************'
		       	});
		   		NS.storeEmpresas.insert(0,todas);
		   		Ext.getCmp(PF + 'txtEmpresa').setValue('0');
		   		Ext.getCmp(PF + 'cmbEmpresas').setValue('*************** TODAS ***************');
			}
		}
	}); 
	NS.storeEmpresas.load();
	
	//Combo empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 65,
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
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
					NS.cambiaBanco(combo.getValue());
				}
			}
		}
	});
	//Refresca el combo bancos en base a la empresa
	NS.cambiaBanco = function(valueCombo){
		Ext.getCmp(PF + 'txtBanco').reset();
		NS.cmbBanco.reset();
		NS.cmbChequeras.reset();
		NS.storeBancos.removeAll();
		NS.storeChequeras.removeAll();
		
		if((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0) {
			if(valueCombo == 0)
				Ext.getCmp(PF + 'txtEmpresa').setValue(valueCombo);
			
			NS.storeBancos.baseParams.noEmpresa = valueCombo;
			NS.storeBancos.load();
			NS.noEmpresa = valueCombo;
		}
	};
	//Etiqueta Banco
	NS.labBanco = new Ext.form.Label({
		text: 'Banco',
		x: 10,
		y: 50
	});
	//Caja numero banco 
	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
		name: PF + 'txtBanco',
		x: 10,
		y: 65,
		width: 50,
		tabIndex: 2,
		listeners:{
			change:{
				fn: function(caja, valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
						
						if(valueCombo != null && valueCombo != undefined && valueCombo != '')
							NS.cambiaChequera(valueCombo);
						else
							NS.cambiaChequera(0);
					}else {
						NS.cambiaChequera(0);
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
					}
                }
			}
    	}
	});
	//Store banco
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			noEmpresa: 0
		},
		paramOrder:['noEmpresa'],
		directFn: ConfirmacionTransferenciasAction.llenaComboBanco, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No Existen bancos para la empresa');
				
				//Se agrega la opcion todos los bancos
	 			var recordsStoreBancos = NS.storeBancos.recordType;	
				var todos = new recordsStoreBancos({
					id: 0,
					descripcion: '****** TODOS ******'
		       	});
				NS.storeBancos.insert(0, todos);
		   		Ext.getCmp(PF + 'txtBanco').setValue('0');
		   		Ext.getCmp(PF + 'cmbBanco').setValue('****** TODOS ******');
			}
		}
	});
	NS.storeBancos.load();
	
	//Combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancos,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 65,
	    y: 65,
	    width: 185,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 3,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBanco.getId());
					//if(combo.getValue() != null && combo.getValue() != '' && combo.getValue() != undefined)
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
			if(valueCombo == 0)
				Ext.getCmp(PF + 'txtBanco').setValue(valueCombo);
			
			NS.storeChequeras.baseParams.idBanco = valueCombo;
			NS.storeChequeras.baseParams.noEmpresa = NS.noEmpresa;
			NS.storeChequeras.load();
			NS.idBanco = valueCombo;
    	}
	};
	//Etiqueta chequera
	NS.labChequera = new Ext.form.Label({
		text: 'Chequera',
		x: 265,
		y: 50
	});
	//Etiqueta divisa
	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa',
		x: 455,
		y: 50
	});
	//storeChequeras en base al banco seleccionado
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{idBanco: 0, noEmpresa: 0, idDivisa: ''},
		paramOrder: ['idBanco', 'noEmpresa', 'idDivisa'],
		directFn: ConsultasAction.llenaComboChequera,
		idProperty: 'id',
		fields: [
		         {name: 'id'},
		         {name: 'id'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0) {
					if(NS.idBanco != 0) Ext.Msg.alert('SET', 'No existen chequeras para este banco');
				}
			}
		}
	});
	//Combo Chequeras en base al banco
	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras,
		id: PF + 'cmbChequeras',
		name: PF + 'cmbChequeras',
		x: 265,
		y: 65,
		width: 170,
		tabIndex: 4,
		typeAhead: true,
		selecOnFocus: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'id',
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
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No hay divisas disponibles');
				}
			}
		}
	});
	NS.storeDivisa.load();
	NS.accionarCmbDivisa = function(comboValue)
	{
		/*Ext.getCmp(PF+'txtTipoCambio').setValue('1.0000');
		Ext.getCmp(PF+'txtIdDivisaPago').setValue(comboValue);
		Ext.getCmp(PF+'cmbDivisaPago').setValue(comboValue);*/
	}
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
        x: 505,
        y: 65,
        width: 140,
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
					BFwrk.Util.updateComboToTextField(PF+'txtIdDivisa',NS.cmbDivisa.getId());
					//NS.accionarCmbDivisa(combo.getValue());
					NS.idDivisa = combo.getValue();
				}
			}
		}
	});
	//Contenedor de Busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: 'búsqueda',
		width: 985,
	    height: 130,
	    layout: 'absolute',
	    items:[
	        NS.labEmpresa,
	        NS.txtEmpresa,
	        NS.cmbEmpresas,
	        NS.labBanco,
            NS.txtBanco,
            NS.cmbBanco,
            NS.labChequera,
            NS.cmbChequeras,
            NS.labDivisa,
            NS.cmbDivisa,
        	{
                xtype: 'uppertextfield',
                id: PF+'txtIdDivisa',
                name: PF+'txtIdDivisa',
                x: 455,
                y: 65,
                width: 40,
                listeners:{
                	change:{
                		fn:function(caja, valor){
                			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisa',NS.cmbDivisas.getId());
                			NS.accionarCmbDivisa(comboValue);
                		}
                	}
                }
            },
            {
	        	xtype: 'button',
	        	id: PF+'cmdGrafica1',
                name: PF+'cmdGrafica1',
	        	text: 'Grafica saldo/banco',
	        	disabled: true,
	        	x: 633,
	        	y: 15,
	        	width: 80,
	        	height: 22,
	        	listeners: {
	        		click:{
	        			fn:function(e){
				        	Ext.getCmp('panel3').show();
							Ext.getCmp('panel2').show();
							Ext.getCmp('panel1').show();
				        	Ext.getCmp('panel1').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'PosicionChequerasSaldoBanco'+sName+'PC.jpg" border="0"/>');//back.png" border="0"/>');
							Ext.getCmp('panel2').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'PosicionChequerasSaldoBanco'+sName+'BG.jpg" border="0"/>');
							Ext.getCmp('panel3').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'PosicionChequerasSaldoBanco'+sName+'LG.jpg" border="0"/>');
							Ext.Msg.alert('SET', 'Graficas listas');
	        			}
	        		}
	        	}
	        },
	        {
	        	xtype: 'button',
	        	id: PF+'cmdGrafica2',
                name: PF+'cmdGrafica2',
	        	text: 'Grafica saldo/empresa',
	        	disabled: true,
	        	x: 750,
	        	y: 15,
	        	width: 80,
	        	height: 22,
	        	listeners: {
	        		click:{
	        			fn:function(e){
				        	Ext.getCmp('panel3').show();
							Ext.getCmp('panel2').show();
							Ext.getCmp('panel1').show();
							Ext.getCmp('panel1').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'PosicionChequerasSaldoEmpresa'+sName+'PC.jpg" border="0"/>');//back.png" border="0"/>');
							Ext.getCmp('panel2').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'PosicionChequerasSaldoEmpresa'+sName+'BG.jpg" border="0"/>');
							Ext.getCmp('panel3').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'PosicionChequerasSaldoEmpresa'+sName+'LG.jpg" border="0"/>');
							Ext.Msg.alert('SET', 'Graficas listas');
	        			}
	        		}
	        	}
	        },
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
	        },{
	        	xtype: 'button',
	        	text: 'Importar Saldos',
	        	id: PF + 'txtImpSdo',
	        	x: 880,
	        	y: 50,
	        	width: 80,
	        	height: 22,
	        	hidden: true,
	        	listeners: {
	        		click:{
	        			fn:function(e){
	        				NS.importaSaldos();
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
		baseParams: {noEmpresa: 0, idBanco: 0, idChequera: '', idUsuario: NS.idUsuario, idDivisa: ''},
		root: '',
		paramOrder: ['noEmpresa', 'idBanco', 'idChequera', 'idUsuario', 'idDivisa'],
		directFn: ConsultasAction.consultarSaldosCuentas,
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'idBanco'},
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'descChequera'},
			{name: 'idDivisa'},
			{name: 'saldoInicial'},
			{name: 'cargo'},
			{name: 'abono'},
			{name: 'saldoFinal'},
			{name: 'idUsuario'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
				NS.record = parseInt(records.length);
				var ini = 0;
				var ing = 0;
				var car = 0;
				var fin = 0;
				
				if(records.length == null || records.length <=0) {
					Ext.getCmp(PF + 'cmdGrafica1').setDisabled(true);
					Ext.getCmp(PF + 'cmdGrafica2').setDisabled(true);
		        	Ext.Msg.alert('SET', 'No existen datos con estos parametros');
				}else {
					//Graficas
					Ext.getCmp(PF + 'cmdGrafica1').setDisabled(false);
					Ext.getCmp(PF + 'cmdGrafica2').setDisabled(false);
					sName = NS.noEmpresa + "" + NS.idBanco + NS.idChequera;
					
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
	NS.tipoSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columnaSelec = new Ext.grid.ColumnModel([
	    //NS.tipoSeleccion,
	    {header: 'No. Empresa',width: 50,dataIndex: 'noEmpresa', sortable: true, hidden: true},
	    {header: 'Empresa',width: 200,dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'No. Banco', width: 50, dataIndex: 'idBanco', sortable: true, hidden: true},
	    {header: 'Banco', width: 120, dataIndex: 'descBanco', sortable: true},
	    {header: 'Descripción', width: 125, dataIndex: 'descChequera', sortable: true},
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
	    height: 245,
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
        y: 255
	});
    
	NS.txtSaldoIni = new Ext.form.TextField({
		id: PF + 'txtSaldoIni',
        name:PF + 'txtSaldoIni',
		x: 10,
        y: 270,
        width: 100,
        disabled: true
    });
	
	NS.labIngresos = new Ext.form.Label({
		text: 'Ingresos',
        x: 120,
        y: 255
	});
    
	NS.txtIngresos = new Ext.form.TextField({
		id: PF + 'txtIngresos',
        name:PF + 'txtIngresos',
		x: 120,
        y: 270,
        width: 100,
        disabled: true
    });
	NS.labEgresos = new Ext.form.Label({
		text: 'Egresos',
        x: 230,
        y: 255
	});
    
	NS.txtEgresos = new Ext.form.TextField({
		id: PF + 'txtEgresos',
        name:PF + 'txtEgresos',
		x: 230,
        y: 270,
        width: 100,
        disabled: true
    });
	NS.labSaldoFin = new Ext.form.Label({
		text: 'Saldo Final',
        x: 340,
        y: 255
	});
    
	NS.txtSaldoFin = new Ext.form.TextField({
		id: PF + 'txtSaldoFin',
        name:PF + 'txtSaldoFin',
		x: 340,
        y: 270,
        width: 100,
        disabled: true
    });
	//Contenedor de Transferencia o Factoraje
	NS.contCuentas = new Ext.form.FieldSet({
		title: 'Cuentas',
        y: 135,
        width: 985,
        height: 330,
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
                   y: 270,
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
            	   y: 270,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				NS.imprimir();
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
	    height: 490,
	    x: 20,
	    y: 5,
	    layout: 'absolute',
	    items:[
			NS.contBusqueda,
			NS.contCuentas
		]
	});
	//Funcion para buscar los registros
	NS.buscar = function(){
		NS.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
		NS.idBanco = Ext.getCmp(PF + 'txtBanco').getValue();
		
		if(NS.noEmpresa == "" || NS.noEmpresa == 0)
			NS.noEmpresa = 0;
		
		if(NS.idBanco == "" || NS.idBanco == 0)
			NS.idBanco = 0;
		
		if(NS.idDivisa==""){
			Ext.Msg.alert('SET','Debe indicar la divisa.');
		}
		else {
			NS.storeDatos.baseParams.noEmpresa = parseInt(NS.noEmpresa);
			NS.storeDatos.baseParams.idBanco = parseInt(NS.idBanco);
			NS.storeDatos.baseParams.idChequera = Ext.getCmp(PF + 'cmbChequeras').getValue();
			NS.storeDatos.baseParams.idDivisa = NS.idDivisa;
			NS.storeDatos.load();
		}
	};
	
	//Funcion para importar los saldos (provisional)
	NS.importaSaldos = function() {
		Ext.Msg.confirm('SET', '¿Desea actualizar los saldos de las chequeras contenidas en el archivo excel?', function(btn) {
			if(btn == 'yes') {
				ConsultasAction.importaSaldos(function(res, e) {
					Ext.Msg.alert('SET', res + '!!');
				});
			}
		});
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
		ConsultasAction.validaFacultad(136, function(result, e) {
			if(result == 0)
				Ext.getCmp(PF + 'txtImpSdo').setVisible(false);
			else
				Ext.getCmp(PF + 'txtImpSdo').setVisible(true);
		});
		
		ConsultasAction.validaFacultad(163, function(result, e) {
			if(result == 0)
				Ext.getCmp(PF + 'txtCtasPer').setVisible(false);
			else
				Ext.getCmp(PF + 'txtCtasPer').setVisible(true);
		});
	}
	
	NS.habilitaBotones();
	
	//Funcion para limpiar los campos
	NS.limpiar = function(){
		NS.cmbEmpresas.reset();
		Ext.getCmp(PF + 'txtBanco').setValue('');
		NS.cmbBanco.reset();
		NS.storeBancos.removeAll();
		NS.storeBancos.baseParams.noEmpresa = 0;
		NS.storeBancos.load();
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
		Ext.getCmp(PF + 'txtEmpresa').setValue('0');
	};
	
	//Función para la llamada al reporte de pago de propuestas
	NS.imprimir = function(){
		var strParams = '?nomReporte=PosicionChequeras';
		
		strParams += '&'+'nomParam1=noEmpresa';
		strParams += '&'+'valParam1=' + ('' + Ext.getCmp(PF + 'txtEmpresa').getValue());
		strParams += '&'+'nomParam2=idBanco';
		strParams += '&'+'valParam2=' + ('' + Ext.getCmp(PF + 'txtBanco').getValue());
		strParams += '&'+'nomParam3=idChequera';
		strParams += '&'+'valParam3=' + ('' + Ext.getCmp(PF + 'cmbChequeras').getValue());
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
	NS.PosicionChequeras = new Ext.FormPanel({
		title: 'Posición de Chequeras',
	    width: 1020,
	    height: 500,
	    frame: true,
	    padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF + 'posicionChequeras',
	    renderTo: NS.tabContId,
	    items:[
	           NS.contGeneral
	    ]
	});
	NS.PosicionChequeras.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});