/* 
 * @author: Victor H. Tello
 * @since: 20/Mayo/2014
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Consultas.ContratosProveedores');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	var sName="";
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	
	NS.idUser = 0;
	NS.idContrato = '';
	
	NS.bandera = '';
	NS.FolioMod = 0;
	
	NS.saldoIni = 0;
	NS.ingresos = 0;
	NS.egresos = 0;
	NS.saldoFin = 0;
	
	
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
	
	//Etiqueta empresa
	NS.labEmpresa = new Ext.form.Label({
		text: 'Empresa',
        x: 10
	});
    
	//Store empresas
    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idUsuario: NS.idUsuario},
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
				/*
				//Se agrega la opcion todas las empresas
	 			var recordsStoreEmpresas = NS.storeEmpresas.recordType;	
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '*************** TODAS ***************'
		       	});
		   		NS.storeEmpresas.insert(0,todas);
		   		Ext.getCmp(PF + 'cmbEmpresas').setValue('*************** TODAS ***************');
		   		*/
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
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.cmbUsuario.reset();
					NS.storeUsuario.removeAll();
					NS.storeUsuario.baseParams.noEmpresa = combo.getValue();
					NS.storeUsuario.load();
					NS.cmbUsuario2.reset();
					NS.storeUsuario2.removeAll();
					NS.storeUsuario2.baseParams.noEmpresa = combo.getValue();
					NS.storeUsuario2.load();
				}
			}
		}
	});
	
	NS.labUsuario = new Ext.form.Label({
		text: 'Proveedor',
        x: 320
	});
	
	NS.storeUsuario = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['noEmpresa'],
		directFn: ConsultasAction.obtenerProveedor, 
		idProperty: 'idUsuario', 
		fields: [
			 {name: 'idUsuario'},
			 {name: 'nomUsuario'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUsuario, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No existen proveedores para esta empresa');
				}
			}
		}
	}); 
	
	NS.storeUsuario2 = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['noEmpresa'],
		directFn: ConsultasAction.obtenerProveedor, 
		idProperty: 'idUsuario', 
		fields: [
			 {name: 'idUsuario'},
			 {name: 'nomUsuario'}
		]
	});
	
	NS.cmbUsuario = new Ext.form.ComboBox({
		store: NS.storeUsuario,
		id: PF + 'cmbUsuario',
		x: 320,
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
					NS.cmbContrato.reset();
					NS.storeContrato.removeAll();
					NS.storeContrato.baseParams.idUsuario = combo.getValue();
					NS.storeContrato.load();
				}
			}
		}
	});
	
	NS.cmbUsuario2 = new Ext.form.ComboBox({
		store: NS.storeUsuario2,
		id: PF + 'cmbUsuario2',
		triggerAction : 'all',
		//typeAhead: true,
		//mode: 'local',
		//selecOnFocus: true,
		//forceSelection: true,
        //tabIndex: 1,
		valueField: 'idUsuario',
		displayField: 'nomUsuario',
		autocomplete: true/*,
		listeners:{
			select:{
				fn:function(combo, valor) {
					var recordsM = NS.gridDatos.getSelectionModel().getSelections();
					var cmbValueId = Ext.getCmp(PF + 'cmbUsuario2').valueField;
					var cmbDesc = Ext.getCmp(PF + 'cmbUsuario2').displayField;
					//recordsM[0]
					Ext.getCmp(PF + 'cmbUsuario2').setValue('');
				}
			}
		}*/
	});
	
	NS.labContrato = new Ext.form.Label({
		text: 'Contrato',
		x: 530
	});
	
	NS.storeContrato = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idUsuario: 0},
		paramOrder:['idUsuario'],
		directFn: ConsultasAction.obtenerContratos,
		idProperty: 'idContrato', 
		fields: [
			 {name: 'idContrato'},
			 {name: 'noContrato'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('SET','No hay contratos disponibles!!');
			}
		}
	});
	
	NS.cmbContrato = new Ext.form.ComboBox({
		store: NS.storeContrato,
		id: PF+'cmbContrato',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 530,
        y: 15,
        width: 170,
		valueField:'idContrato',
		displayField:'noContrato',
		autocomplete: true,
		emptyText: 'Seleccione el contrato',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idContrato = combo.getValue();
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
	        NS.labEmpresa,		NS.cmbEmpresas,
	        NS.labUsuario,		NS.cmbUsuario,
	        NS.labContrato,		NS.cmbContrato,
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
				        	NS.txtMontoOriginal.setValue('');
				    		NS.txtMontoPagado.setValue('');
				    		NS.txtMontoAdeudo.setValue('');		
				            NS.buscarRegistro();
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
		directFn: ConsultasAction.obtenerRegistros,
		fields: [
			{name: 'idContrato'},
			{name: 'razonSocial'},
			{name: 'noContrato2'},
			{name: 'descContrato'},
			{name: 'montoOriginal'},
			{name: 'montoPagado'},
			{name: 'montoAdeudo'},
			{name: 'noPersona'},
			{name: 'fechaInicial'},
			{name: 'fechaFinal'},
			{name: 'noPagos'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
				var ori = 0;
				var pag = 0;
				var ade = 0;
				
				if(records.length == null || records.length <=0) {
					Ext.Msg.alert('SET', 'No existen datos con estos parametros');
				}else {
					for(var k=0; k<records.length; k++) {
      					ori += records[k].get('montoOriginal');
      					pag += records[k].get('montoPagado');
      					ade += records[k].get('montoAdeudo');
          			}
	
					NS.txtMontoOriginal.setValue(NS.formatNumber(ori));
					NS.txtMontoPagado.setValue(NS.formatNumber(pag));
					NS.txtMontoAdeudo.setValue(NS.formatNumber(ade));
					
				}
			}
		}
	});
	//Para indicar el modo de seleccion de los registros en el Grid 
	NS.tipoSeleccion = new Ext.grid.CheckboxSelectionModel ({singleSelect: false});
	
	//Columna de Seleccion  
	NS.columnaSelec = new Ext.grid.ColumnModel([
	    //NS.tipoSeleccion,
	    {header: 'Id Contrato',width: 90,dataIndex: 'idContrato', sortable: true, hidden: true},
	    {header: 'Proveedor',width: 200,dataIndex: 'razonSocial', sortable: true},
	    {header: 'No. Contrato', width: 90, dataIndex: 'noContrato2', sortable: true},
	    {header: 'Desc. Contrato', width: 200, dataIndex: 'descContrato', sortable: true},
	    {header: 'Monto Original', width: 125, dataIndex: 'montoOriginal', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Monto Pagado', width: 125, dataIndex: 'montoPagado', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'Monto Adeudo', width: 125, dataIndex: 'montoAdeudo', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	    {header: 'No. Persona', width: 90, dataIndex: 'noPersona', sortable: true,hidden:true},
	    {header: 'Fecha Inicial', width: 120, dataIndex: 'fechaInicial', sortable: true},
	    {header: 'Fecha Final', width: 120, dataIndex: 'fechaFinal', sortable: true},
	    {header: 'No. Pagos', width: 90, dataIndex: 'noPagos', sortable: true, hidden:true}
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
					
				}
			}
		}
	});
	
	NS.labMontoOriginal = new Ext.form.Label({
		text: 'Monto Original',
        x: 10,
        y: 285
	});
    
	NS.txtMontoOriginal = new Ext.form.TextField({
		id: PF + 'txtMontoOriginal',
        name:PF + 'txtMontoOriginal',
		x: 10,
        y: 300,
        width: 100,
        disabled: true
    });
	
	NS.labMontoPagado = new Ext.form.Label({
		text: 'Monto Pagado',
        x: 120,
        y: 285
	});
    
	NS.txtMontoPagado = new Ext.form.TextField({
		id: PF + 'txtMontoPagado',
        name:PF + 'txtMontoPagado',
		x: 120,
        y: 300,
        width: 100,
        disabled: true
    });
	NS.labMontoAdeudo = new Ext.form.Label({
		text: 'Monto Adeudo',
        x: 230,
        y: 285
	});
    
	NS.txtMontoAdeudo = new Ext.form.TextField({
		id: PF + 'txtMontoAdeudo',
        name:PF + 'txtMontoAdeudo',
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
               NS.labMontoOriginal,
               NS.txtMontoOriginal,
               NS.labMontoPagado,
               NS.txtMontoPagado,
               NS.labMontoAdeudo,
               NS.txtMontoAdeudo,
               //NS.labSaldoFin,
               //NS.txtSaldoFin, 
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
			            	   if(NS.cmbEmpresas.getValue() == ''){
			           			Ext.Msg.alert('SET','Debe elegir una empresa');
			           			return;
			           			}
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
			            	   if(NS.cmbUsuario.getValue() == ''){
			           			Ext.Msg.alert('SET','Debe elegir una empresa y un proveedor');
			           			return;
			           			}
			            	   	NS.bandera= "N";
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
            	   				var registro = 0;
            	   				var recordsEliminar = NS.gridDatos.getSelectionModel().getSelections();
            	   				
            	   				if(recordsEliminar.length>0){
	            	   				Ext.Msg.confirm('confirmación','¿Estas seguro de  eliminar este registro?',function(btn){
	            	   					if(btn === 'yes'){
	            	   						registro = recordsEliminar[0].get('idContrato');
	            	   						ConsultasAction.eliminarContrato(registro, function(res, e){
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
            	   					NS.bandera = "M";
            	   					NS.FolioMod = recordsModif[0].get('idContrato');
            	   					Ext.getCmp(PF + 'txtNoContrato').setValue(recordsModif[0].get('noContrato2'));
            	   					Ext.getCmp(PF + 'txtDescripcion').setValue(recordsModif[0].get('descContrato'));
            	   					Ext.getCmp(PF + 'txtNoPagos').setValue(recordsModif[0].get('noPagos'));
            	   					Ext.getCmp(PF + 'txtMontoOrig').setValue(recordsModif[0].get('montoOriginal'));
            	   					Ext.getCmp(PF + 'txtMontoPag').setValue(recordsModif[0].get('montoPagado'));
            	   					Ext.getCmp(PF + 'txtFecIni').setValue(recordsModif[0].get('fechaInicial'));
            	   					Ext.getCmp(PF + 'txtFecFin').setValue(recordsModif[0].get('fechaFinal'));
                	   				winNuevo.show();
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
	NS.labNoContrato = new Ext.form.Label({
		text: 'No. Contrato'
	});
	
	NS.txtNoContrato = new Ext.form.TextField({
		id: PF + 'txtNoContrato',
        name:PF + 'txtNoContrato',
        y: 15,
        width: 150
    });
	
	NS.labDescripcion = new Ext.form.Label({
		text: 'Descripción',
		x: 180
	});
	
	NS.txtDescripcion = new Ext.form.TextField({
		id: PF + 'txtDescripcion',
        name:PF + 'txtDescripcion',
		x: 180,
        y: 15,
        width: 200
    });
	
	NS.labNoPagos = new Ext.form.Label({
		text: 'No. Pagos',
		x: 400
	});
	
	
	NS.txtNoPagos = new Ext.form.NumberField({
		id: PF + 'txtNoPagos',
        name:PF + 'txtNoPagos',
		x: 400,
        y: 15,
        width: 150
    });
	
	NS.labMontoOrig = new Ext.form.Label({
		text: 'Monto Original',
        y: 50
	});
    
	NS.txtMontoOrig = new Ext.form.TextField({
		id: PF + 'txtMontoOrig',
		name: PF + 'txtMontoOrig',
        y: 65,
        width: 100,
        listeners: {
	   		change: {
				fn : function(caja, value){
					var valor = NS.formatNumber(value.toString());
					caja.setValue(valor);
	   			}
   			}
   		}
    });
	
	NS.labMontoPag = new Ext.form.Label({
		text: 'Monto Pagado',
        x: 120,
        y: 50
	});
    
	NS.txtMontoPag = new Ext.form.TextField({
		id: PF + 'txtMontoPag',
		name: PF + 'txtMontoPag',
		x: 120,
        y: 65,
        width: 100,
        listeners: {
	   		change: {
				fn : function(caja, value){
					var valor = NS.formatNumber(value.toString());
					caja.setValue(valor);
	   			}
   			}
   		}
    });
	
	NS.labFecIni = new Ext.form.Label({
		text: 'Fecha Inicio',
		 x: 240,
	        y: 50
	});
	 
	NS.txtFecIni = new Ext.form.DateField({
		id: PF + 'txtFecIni',
		name: PF + 'txtFecIni',
		x: 240,
        y: 65,
        format: 'd/m/Y',
        value: apps.SET.FEC_HOY
    });
	
	NS.labFecFin = new Ext.form.Label({
		text: 'Fecha Fin',
        x: 360,
        y: 50
	});
	 
	NS.txtFecFin = new Ext.form.DateField({
		id: PF + 'txtFecFin',
		name: PF + 'txtFecFin',
		x: 360,
        y: 65,
        format: 'd/m/Y',
        value: apps.SET.FEC_HOY
    });
	
	
	NS.contNuevo = new Ext.form.FieldSet({
		width: 530,
	    height: 180,
	    layout: 'absolute',
	    items:[
				NS.labNoContrato,
				NS.txtNoContrato,
				NS.labDescripcion,
				NS.txtDescripcion,
				NS.labNoPagos,
				NS.txtNoPagos,
				NS.labMontoOrig,
				NS.txtMontoOrig,
				NS.labMontoPag,
				NS.txtMontoPag,
				NS.labFecIni,
				NS.txtFecIni,
				NS.labFecFin,
				NS.txtFecFin,
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
		var nPag = NS.txtNoPagos.getValue();
		
		reg.bandera = NS.bandera;
		reg.noContrato = NS.txtNoContrato.getValue();
		reg.descripcion = NS.txtDescripcion.getValue();
		reg.montoOrig = NS.unformatNumber(NS.txtMontoOrig.getValue());
		reg.montoPag = NS.unformatNumber(NS.txtMontoPag.getValue());
		reg.persona = NS.cmbUsuario.getValue();
		reg.fecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFecIni').getValue());
		reg.fecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFecFin').getValue());
		reg.noPagos = nPag.toString();
		reg.folio  = NS.FolioMod.toString();
        mat[0] = reg;
        
        var jsonString = Ext.util.JSON.encode(mat);
        
		ConsultasAction.insertaNuevoCP(jsonString, function(res){
			Ext.Msg.alert('SET', res);
			NS.FolioMod = 0;
			if(res == 'Registro exitoso!!' || res == "Registro modificado!") {
				NS.limpiarNuevo();
				winNuevo.hide();
				NS.buscarRegistro();
			}
		});
	};
	
	NS.limpiarNuevo = function() {
		NS.txtNoContrato.setValue('');
		NS.txtDescripcion.setValue('');
		NS.txtNoPagos.setValue('');
		NS.txtMontoOrig.setValue('');
		NS.txtMontoPag.setValue('');
		NS.txtFecIni.setValue(apps.SET.FEC_HOY);
		NS.txtFecIni.setValue(apps.SET.FEC_HOY);
	};
	
	NS.buscarRegistro = function() {
		var reg = {}; 
		var mat = new Array();
		
		if(NS.cmbEmpresas.getValue() == ''){
			Ext.Msg.alert('SET','Debe elegir una empresa');
			return;
		}
		
		reg.empresa = NS.cmbEmpresas.getValue();
		reg.idUsuario = NS.cmbUsuario.getValue();
		reg.contrato = NS.cmbContrato.getValue();
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
	/*	ConsultasAction.validaFacultad(163, function(result, e) {
			if(result == 0)
				Ext.getCmp(PF + 'txtCtasPer').setVisible(false);
			else
				Ext.getCmp(PF + 'txtCtasPer').setVisible(true);
		});*/
	};
	
	NS.habilitaBotones();
	
	//Funcion para limpiar los campos
	NS.limpiar = function(){
		NS.cmbUsuario.reset();
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
		NS.cmbEmpresas.reset();
		NS.cmbContrato.reset();
		NS.txtMontoOriginal.setValue('');
		NS.txtMontoPagado.setValue('');
		NS.txtMontoAdeudo.setValue('');
	};
	
	//Función para la llamada al reporte de pago de propuestas
	NS.imprimir = function(){
		var strParams = '?nomReporte=ContratosProveedores';
		
		strParams += '&'+'nomParam1=empresa';
		strParams += '&'+'valParam1=' + NS.cmbEmpresas.getValue();
		strParams += '&'+'nomParam2=idUsuario';
		strParams += '&'+'valParam2=' + NS.cmbUsuario.getValue();
		strParams += '&'+'nomParam3=contrato';
		strParams += '&'+'valParam3=' + NS.cmbContrato.getValue();
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		return;
	};
	
	//Contenedor principal del formulario
	NS.ContratosProveedores = new Ext.FormPanel({
		title: 'Contratos Proveedores',
	    width: 1020,
	    height: 500,
	    frame: true,
	    padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF + 'contratosProveedores',
	    renderTo: NS.tabContId,
	    items:[
	           NS.contGeneral
	    ]
	});
	NS.ContratosProveedores.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});