/* 
 * @author: Victor H. Tello
 * @since: 09/Ene/2012
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Egresos.confirmación.Transferencias');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	NS.noEmpresa = 0;
	NS.idBanco = 0;
	NS.idChequera = '';
	NS.record = 0;
	NS.chkAutomatico = false;
	NS.iTieneBanca = 0;
	NS.bAutomatico = false;
	NS.idDivisa = "MN";
	
	//Etiqueta empresa
	NS.labEmpresa = new Ext.form.Label({
		text: 'Empresa:',
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
					nomEmpresa: '***************TODAS***************'
		       	});
		   		NS.storeEmpresas.insert(0,todas);
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
		text: 'Banco:',
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
				fn: function(caja,valor){
                    if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                    	var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
                    	NS.cambiaChequera(valueCombo);
                    }else
                    	NS.cambiaChequera('');
                }
			}
    	}
	});
	NS.txtEmpresa.setValue(0);
	NS.cmbEmpresas.setValue('***************TODAS***************');
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
					Ext.Msg.alert('SET','No Existen bancos para esta empresa');
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
					if(combo.getValue() != null && combo.getValue() != '' && combo.getValue() != undefined)
						NS.cambiaChequera(combo.getValue());
				}
			}
		}
	});
	//Refresca el combo de chequeras segun el banco y la empresa seleccionados
	NS.cambiaChequera = function(valueCombo) {
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();
		
		if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
			NS.storeChequeras.baseParams.idDivisa = NS.idDivisa;
			NS.storeChequeras.baseParams.idBanco = valueCombo;
			NS.storeChequeras.baseParams.noEmpresa = NS.noEmpresa;
			NS.storeChequeras.load();
			NS.idBanco = valueCombo;
    	}else {
    		Ext.getCmp(PF + 'txtBanco').reset();
    		NS.cmbBanco.reset();
    	}
	};
	//Etiqueta chequera
	NS.labChequera = new Ext.form.Label({
		text: 'Chequera:',
		x: 300,
		y: 50
	});
	
	//divisa busqueda
	NS.optDivisas = new Ext.form.RadioGroup({
		id: PF + 'optDivisa',
		name: PF + 'optDivisa',
		x: 25,
		y: 0,
		columns: 3, 
		items: [
	          {boxLabel: 'MN', name: 'optDivisa', inputValue: "MN", checked: true},  
	          {boxLabel: 'DLS', name: 'optDivisa', inputValue: "DLS"},  
	          {boxLabel: 'EUR', name: 'optDivisa', inputValue: "EUR"}
	     ],
	     listeners:{
	    	 change:{
	    		 fn:function(caja,valor){
	    			NS.idDivisa = this.getValue().inputValue;
	    			NS.storeChequeras.baseParams.idDivisa = NS.idDivisa;
	    			NS.storeChequeras.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBanco').getValue());
	    			NS.storeChequeras.baseParams.noEmpresa = NS.noEmpresa;
	    			NS.storeChequeras.load();
				}
	    	 }
	     }
	});
	
	NS.divisa = new Ext.form.FieldSet({
		title: 'Divisa',
		x: 700,
		y: 40,
		width: 255,
		height: 55,
		layout: 'absolute',
		items: [
		    NS.optDivisas
		]
	});
	
	//storeChequeras en base al banco seleccionado
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{idBanco: 0, noEmpresa: 0, idDivisa: 'MN'},
		paramOrder: ['idBanco', 'noEmpresa', 'idDivisa'],
		directFn: ConfirmacionTransferenciasAction.llenaComboChequera,
		idProperty: 'id',
		fields: [
		         {name: 'id'},
		         {name: 'id'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen chequeras para este banco con divisa: ' + NS.idDivisa);
			}
		}
	});
	//Combo Chequeras en base al banco
	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras,
		id: PF + 'cmbChequeras',
		name: PF + 'cmbChequeras',
		x: 300,
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
	//Radio Button SI - NO
	NS.optRadios = new Ext.form.RadioGroup({
		id: PF + 'opthayBanca',
		name: PF + 'opthayBanca',
		x: 25,
		y: 0,
		columns: 2, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Si', name: 'optSi', inputValue: 0, checked: true},  
	          {boxLabel: 'No', name: 'optSi', inputValue: 1}
	     ]
	});
	//Contenedor de Radio Button SI - NO
	NS.contBanca = new Ext.form.FieldSet({
		title: 'Tiene Banca Electrónica',
		x: 520,
		y: 40,
		width: 170,
		height: 55,
		layout: 'absolute',
		items: [
		    NS.optRadios
		]
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
            NS.contBanca,
            NS.divisa,
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
		baseParams: {noEmpresa: 0, idBanco: 0, idChequera: '', hayBanca: '', idUsuario: NS.idUsuario, idDivisa: ''},
		root: '',
		paramOrder: ['noEmpresa', 'idBanco', 'idChequera', 'hayBanca', 'idUsuario', 'idDivisa'],
		directFn: ConfirmacionTransferenciasAction.consultarMovimiento,
		fields: [
			{name: 'noEmpresa'},
			{name: 'fecValor'},
			{name: 'bancoPago'},
			{name: 'idChequera'},
			{name: 'importe'},
			{name: 'descBancoBenef'},
			{name: 'idChequeraBenef'},
			{name: 'idDivisa'},
			{name: 'descFormaPago'},
			{name: 'beneficiario'},
			{name: 'referencia'},
			{name: 'idBanco'},
			{name: 'idBancoBenef'},
			{name: 'idFormaPago'},
			{name: 'idCveOperacion'},
			{name: 'tipoCambio'},
			{name: 'idEstatusMov'},
			{name: 'descEstatus'},
			{name: 'noDocto'},
			{name: 'loteEntrada'},
			{name: 'folioRef'},
			{name: 'noCuenta'},
			{name: 'origenMov'},
			{name: 'agrupa1'},
			{name: 'agrupa2'},
			{name: 'idRubro'},
			{name: 'agrupa3'},
			{name: 'noFolioDet'},
			{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
				NS.record = parseInt(records.length);
				
				if(records.length == null || records.length <=0)
					Ext.Msg.alert('SET', 'No existen registros con estos parametros');
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
	    {header: 'No Empresa',width: 150,dataIndex: 'noEmpresa', sortable: true, hidden: true},
	    {header: 'Empresa',width: 150,dataIndex: 'nomEmpresa', sortable: true},
	    {header: 'No. Referencia', width: 110, dataIndex: 'referencia', sortable: true},
	    {header: 'Fecha',width: 80,dataIndex: 'fecValor', sortable: true},
	    {header: 'Banco Pag.', width: 110, dataIndex: 'bancoPago', sortable: true},
	    {header: 'Chequera Pag.', width: 100, dataIndex: 'idChequera', sortable: true},
	    {header: 'Importe', width: 90, dataIndex: 'importe', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Banco Benef.', width: 110, dataIndex: 'descBancoBenef', sortable: true},
	    {header: 'Chequera Benef.',	width: 130, dataIndex: 'idChequeraBenef', sortable: true},
	    {header: 'Divisa', width: 60, dataIndex: 'idDivisa', sortable: true},
	    {header: 'Forma Pago', width: 60, dataIndex: 'descFormaPago', sortable: true, hidden: true },
	    {header: 'Beneficiario', width: 150, dataIndex: 'beneficiario', sortable: true},
	    {header: 'ID Banco Pag', width: 50, dataIndex: 'idBanco', sortable: true, hidden: true},
	    {header: 'ID Banco Benef', width: 50, dataIndex: 'idBancoBenef', sortable: true, hidden: true},
	    {header: 'ID Forma Pago', width: 50, dataIndex: 'idFormaPago', sortable: true, hidden: true},
	    {header: 'ID Cve Operacion', width: 50, dataIndex: 'idCveOperacion', sortable: true, hidden: true},
	    {header: 'Tipo Cambio', width: 50, dataIndex: 'tipoCambio', sortable: true, hidden: true, renderer: BFwrk.Util.rendererMoney},
	    {header: 'ID Estatus', width: 50, dataIndex: 'idEstatusMov', sortable: true, hidden: true},
	    {header: 'Estatus', width: 90, dataIndex: 'descEstatus', sortable: true},
	    {header: 'No Documento', width: 110, dataIndex: 'noDocto', sortable: true},
	    {header: 'Lote Entrada', width: 80, dataIndex: 'loteEntrada', sortable: true},
	    {header: 'Folio Ref', width: 50, dataIndex: 'folioRef', sortable: true, hidden: true},
	    {header: 'No Cuenta', width: 50, dataIndex: 'noCuenta', sortable: true, hidden: true},
	    {header: 'Origen Mov', width: 50, dataIndex: 'origenMov', sortable: true, hidden: true},
	    {header: 'Agrupa1', width: 50, dataIndex: 'agrupa1', sortable: true, hidden: true},
	    {header: 'Agrupa2', width: 50, dataIndex: 'agrupa2', sortable: true, hidden: true},
	    {header: 'ID Rubro', width: 50, dataIndex: 'idRubro', sortable: true, hidden: true},
	    {header: 'Región', width: 50, dataIndex: 'agrupa3', sortable: true, hidden: true},
	    {header: 'Folio Det', width: 50, dataIndex: 'noFolioDet', sortable: true, hidden: true}
	]);
	//Grid para mostrar los datos seleccionados
	NS.gridDatos = new Ext.grid.GridPanel({
		store: NS.storeDatos,
		id: 'gridDatos',
		cm: NS.columnaSelec,
		sm: NS.tipoSeleccion,
		width: 960,
	    height: 245,
	    stripeRows: true,
	    columnLines: true
		/*listeners: {
			dblclick: {
				fn:function(grid) {
					var recordsEliminar = NS.gridDatos.getSelectionModel().getSelections();
					Ext.Msg.confirm('Información SET','¿Esta seguro de eliminar '+recordsEliminar.length+' detalle(s)?',function(btn) {  
						if(btn === 'yes') {
							for(var i=0; i<recordsEliminar.length; i++) {
								NS.gridDatos.store.remove(recordsEliminar[i]);
								NS.gridDatos.getView().refresh();
							}
						}
					});
				}
			}
		}*/
	});
	//Etiqueta referencia
	NS.labReferencia = new Ext.form.Label({
		text: 'Referencia:',
		x: 10,
		y: 255
	});
	//Caja de Referencia
	NS.txtReferencia = new Ext.form.TextField({
		id: PF + 'txtReferencia',
		name: PF + 'txtReferencia',
		x: 10,
		y: 270,
		width: 130,
		tabIndex: 5,
		listeners:{
   			change:{
   				fn:function(caja,valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						NS.agregaReferencia();
				}
			}
		}
	});
	//checkBox para confirmación automatica
	NS.chkAutom = new Ext.form.CheckboxGroup({
	    id: PF + 'chkAutom',
	    name:PF + 'chkAutom',
	    x: 170,
	    y: 270,
	    fieldLabel: '',
	    itemCls: 'x-check-group-alt',
	    columns: 2,
	    items: [ {
	    		xtype:'checkbox',
                boxLabel: 'confirmación Automatica',
                id: PF + 'chkAuto',
                name: PF + 'chkAuto',
                listeners: {
                    check: {
                    	fn:function(checkBox,valor) {
                    		if(valor) {
                    			NS.marcarTodo();
                    			NS.chkAutomatico = true;
                    		}else{
                 				NS.tipoSeleccion.selectRange(-1, -1);
                    			NS.chkAutomatico = false;
                    		}
                        }
	    			}
	    		}
            },{
	    		xtype:'checkbox',
                boxLabel: 'confirmación Automatica',
                id: PF + 'a',
                name: PF + 'a',
                disabled:true,
                hidden:true,
                checked:true
            }
	    ]
	});
	//Contenedor de Transferencia o Factoraje
	NS.contTransferencias = new Ext.form.FieldSet({
		title: 'Transferencia o Factoraje',
        y: 135,
        width: 985,
        height: 330,
        layout: 'absolute',
        items: [
               NS.gridDatos,
               NS.labReferencia,
               NS.txtReferencia,
               NS.chkAutom,
               {
            	   xtype: 'button',
            	   text: 'Ejecutar',
            	   x: 700,
            	   y: 270,
            	   width: 80,
            	   height: 22,
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				var regSeleccionados = NS.gridDatos.getSelectionModel().getSelections();
            	   				
            	   				if(NS.chkAutomatico && regSeleccionados.length > 0 && !NS.bAutomatico) {
            	   					Ext.Msg.confirm('Información SET', 'Solo se confirmarón los movimientos anteriores a hoy ¿desea continuar?', function(btn) {
            	   						if(btn == 'yes') {
            	   							NS.ejecutar(regSeleccionados);
            	   						}	
            	   					});
            	   				}else
            	   					NS.ejecutar(regSeleccionados);
   							}
               			}
               		}
               },{
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
			NS.contTransferencias
		]
	});
	//Funcion para buscar los registros
	NS.buscar = function(){
		var tieneBanca = Ext.getCmp(PF + 'opthayBanca').getValue();
		NS.iTieneBanca = parseInt(tieneBanca.getGroupValue());
		NS.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
		
		if(NS.noEmpresa == "" || NS.noEmpresa == 0)
			NS.noEmpresa = 0;
		
		if(Ext.getCmp(PF + 'txtBanco').getValue() == "")
			NS.idBanco = 0;
		
		NS.storeDatos.baseParams.noEmpresa = parseInt(NS.noEmpresa);
		NS.storeDatos.baseParams.idBanco = parseInt(NS.idBanco);
		NS.storeDatos.baseParams.idChequera = Ext.getCmp(PF + 'cmbChequeras').getValue();
		NS.storeDatos.baseParams.hayBanca = NS.iTieneBanca;
		NS.storeDatos.baseParams.idDivisa = NS.idDivisa;
		NS.storeDatos.load();
	};
	
	//Funcion del checkBox confirmacion automatica
	NS.marcarTodo = function() {
		if(NS.record > 0)
			NS.tipoSeleccion.selectRange(-1, NS.record - 1);
	};
	
	//Funcion que agrega la referencia al grid cuando se acaba de digitar 
	NS.agregaReferencia = function() {
		var recordsAgregar = NS.gridDatos.getSelectionModel().getSelections();
		var datosClase = NS.gridDatos.getStore().recordType;
		var recordsGridDatos = NS.storeDatos.data.items;
		var referencia = Ext.getCmp(PF + 'txtReferencia').getValue();
		
		if(recordsAgregar.length > 0) {
			for(var i = 0; i < recordsAgregar.length; i++) {
				if(recordsAgregar[i].get('referencia') == '') {
					var datos = new datosClase({
						noEmpresa: recordsAgregar[i].get('noEmpresa'),
						nomEmpresa: recordsAgregar[i].get('nomEmpresa'),
						fecValor: recordsAgregar[i].get('fecValor'),
						bancoPago: recordsAgregar[i].get('bancoPago'),
						idChequera: recordsAgregar[i].get('idChequera'),
						importe: recordsAgregar[i].get('importe'),
						descBancoBenef: recordsAgregar[i].get('descBancoBenef'),
						idChequeraBenef: recordsAgregar[i].get('idChequeraBenef'),
						idDivisa: recordsAgregar[i].get('idDivisa'),
						descFormaPago: recordsAgregar[i].get('descFormaPago'),
						beneficiario: recordsAgregar[i].get('beneficiario'),
						referencia: referencia,
						idBanco: recordsAgregar[i].get('idBanco'),
						idBancoBenef: recordsAgregar[i].get('idBancoBenef'),
						idFormaPago: recordsAgregar[i].get('idFormaPago'),
						idCveOperacion: recordsAgregar[i].get('idCveOperacion'),
						tipoCambio: recordsAgregar[i].get('tipoCambio'),
						idEstatusMov: recordsAgregar[i].get('idEstatusMov'),
						descEstatus: recordsAgregar[i].get('descEstatus'),
						noDocto: recordsAgregar[i].get('noDocto'),
						loteEntrada: recordsAgregar[i].get('loteEntrada'),
						folioRef: recordsAgregar[i].get('folioRef'),
						noCuenta: recordsAgregar[i].get('noCuenta'),
						origenMov: recordsAgregar[i].get('origenMov'),
						agrupa1: recordsAgregar[i].get('agrupa1'),
						agrupa2: recordsAgregar[i].get('agrupa2'),
						idRubro: recordsAgregar[i].get('idRubro'),
						agrupa3: recordsAgregar[i].get('agrupa3'),
						noFolioDet: recordsAgregar[i].get('noFolioDet')
					});
					NS.storeDatos.remove(recordsAgregar[i]);
					NS.gridDatos.stopEditing();
					NS.storeDatos.insert(i, datos);
					NS.gridDatos.getView().refresh();
					Ext.getCmp(PF + 'txtReferencia').reset();
				}
			}
			NS.tipoSeleccion.selectRange(0, i-1);
		}
	};
	
	//Funcion del boton ejecutar
	NS.ejecutar = function(regSeleccionados) {
		var matRegConfirmar = new Array ();
		
		for(var i=0; i<regSeleccionados.length; i++) {
			var regConfirmar = {};
			regConfirmar.folioRef = regSeleccionados[i].get('folioRef');
			regConfirmar.fecValor = regSeleccionados[i].get('fecValor');
			regConfirmar.noEmpresa = regSeleccionados[i].get('noEmpresa');
			regConfirmar.idBanco = regSeleccionados[i].get('idBanco');
			regConfirmar.idChequera = regSeleccionados[i].get('idChequera');
			regConfirmar.importe = regSeleccionados[i].get('importe');
			regConfirmar.noFolioDet = regSeleccionados[i].get('noFolioDet');
			regConfirmar.referencia = regSeleccionados[i].get('referencia'); 
			
			matRegConfirmar[i] = regConfirmar;
		}
		var jSonString = Ext.util.JSON.encode(matRegConfirmar);
		ConfirmacionTransferenciasAction.ejecutarConfirmacion(jSonString, NS.bAutomatico, NS.chkAutomatico, NS.iTieneBanca, function(res, e) {
			if(res != null && res != undefined && res != '') {
				Ext.Msg.alert('SET', '' + res.msgUsuario + '!!');
				Ext.getCmp(PF + 'chkAuto').setValue(false);
				NS.buscar();
			}
		});
	};
	
	//Funcion para limpiar los campos
	NS.limpiar = function(){
		NS.cmbEmpresas.reset();
		Ext.getCmp(PF + 'txtBanco').setValue('');
		NS.cmbBanco.reset();
		NS.storeBancos.removeAll();
		NS.storeBancos.baseParams.noEmpresa = 0;
		NS.storeBancos.load();
		Ext.getCmp(PF + 'txtReferencia').setValue('');
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
		NS.ConfirmacionTransferencias.getForm().reset();
		Ext.getCmp(PF + 'txtEmpresa').setValue('0');
	};
	
	//Funcion imprimir
	NS.imprimir = function(){
		
	};
	
	//Contenedor principal del formulario
	NS.ConfirmacionTransferencias = new Ext.FormPanel({
		title: 'confirmación de Transferencias',
	    width: 1020,
	    height: 500,
	    frame: true,
	    padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF + 'confirmacionTransferencias',
	    name: PF+ 'confirmacionTransferencias',
	    renderTo: NS.tabContId,
	    items:[
	           NS.contGeneral
	    ]
	});
	NS.ConfirmacionTransferencias.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});