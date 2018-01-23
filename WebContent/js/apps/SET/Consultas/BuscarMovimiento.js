Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	var NS = Ext.namespace('apps.SET.Consultas.BuscarMovimiento');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	
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
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		singleSelect : true		
	});
	
	NS.sm2 = new Ext.grid.CheckboxSelectionModel({
		singleSelect : true		
	});
	
	NS.inicializarVariables = function()
	{
		NS.parametroBus = '';
		NS.iNoEmpresa = 0;
		NS.iNoProveedor = 0;
		NS.sEstatusPantallas = '';
	};
	NS.inicializarVariables();
	
	NS.limpiar = function()
	{
		NS.inicializarVariables();
		NS.BuscarMovimiento.getForm().reset();
		//NS.storeEmpresas.removeAll();
		NS.cmbEmpresas.reset();
		NS.storeProveedor.removeAll();
		NS.cmbProveedor.reset();
		NS.storeConsultaMovimiento.removeAll();
		NS.gridConsultaMovimiento.getView().refresh();
		NS.gridConsultaZimpfact.getView().refresh();
		NS.gridConsultaMovimiento.setVisible(false);
		NS.gridConsultaZimpfact.setVisible(true);
	};
	
	//Store para el combo de empresas
	NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: GlobalAction.llenarComboEmpresasUsuario, 
		idProperty: 'id', 
		fields: [
			{name: 'id'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('Información SET','No existen empresas asignadas al usuario');
					
			}
		}
	}); 
	
	NS.storeEmpresas.load();
	
	//combo para mostrar empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		name: PF + 'cmbEmpresas',
		id: PF + 'cmbEmpresas',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 80,
        y: 20,
        width: 210,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.iNoEmpresa = combo.getValue();
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
					
					if(NS.iNoEmpresa != 0){
						Ext.getCmp(PF + 'txtProveedor').setValue('');
						NS.cmbProveedor.reset();
						Ext.getCmp(PF + 'txtProveedor').setDisabled(false);
						Ext.getCmp(PF + 'cmbProveedor').setDisabled(false);
					}else {
						Ext.getCmp(PF + 'txtProveedor').setValue('');
						NS.cmbProveedor.reset();
						Ext.getCmp(PF + 'txtProveedor').setDisabled(false);
						Ext.getCmp(PF + 'cmbProveedor').setDisabled(false);
					}
				}
			}
		}
	});	//Termina cmbEmpresas
	
	/*//store  Proveedor 
    NS.storeProveedor = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			prefijoProv : '',
			iNoProveedor : 0,
			noEmpresa: 0
		},
		root: '',
		paramOrder:['prefijoProv', 'iNoProveedor', 'noEmpresa'],
		root: '',
		directFn: ConsultasAction.llenarComboProveedores, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProveedor, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.getCmp(PF + 'cmbProveedor').setValue('');
					Ext.getCmp(PF + 'txtNoProveedor').setValue('');
					Ext.Msg.alert('SET','No Existen Proveedores con ese nombre');
					return;
				}
				NS.iNoProveedor = parseInt(records[0].get('id'));
				Ext.getCmp(PF + 'cmbProveedor').setValue(records[0].get('descripcion'));
				NS.parametroBus = '';
			}
		}
	}); 
	
	//Este combo carga los datos dependiendo la(s) letra(s) que se tecleen en el combo y se da clik en el boton
	//combo para el Proveedor
	NS.cmbProveedor = new Ext.form.ComboBox({
		store: NS.storeProveedor,
		name: PF+'cmbProveedor',
		id: PF+'cmbProveedor',
		typeAhead: true,
		mode: 'local',
		mode: 'remote',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		hideTrigger: true,
        pageSize: 10,
      	x: 80,
        y: 70,
        width: 190,
        tabIndex: 4,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Escriba prefijo...',
		triggerAction: 'all',
		value: '',
		disabled: true,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	 BFwrk.Util.updateComboToTextField(PF + 'txtNoProveedor', NS.cmbProveedor.getId());
				 	 NS.iNoProveedor = parseInt(combo.getValue());
				}
			},
			beforequery: function(qe){
				if(Ext.getCmp(PF + 'txtNoEmpresa').getValue() == '') {
					NS.storeProveedor.removeAll();
					NS.cmbProveedor.reset();
					Ext.Msg.alert('SET', 'Seleccione primero la empresa');
				}else {
					NS.parametroBus = qe.combo.getRawValue();
					NS.storeProveedor.baseParams.prefijoProv = NS.parametroBus;
					NS.storeProveedor.baseParams.iNoProveedor = 0;
					NS.storeProveedor.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
				}
			}
		}
	});*/
	
	//store proveedor
	NS.storeProveedor = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {texto : ''},
		paramOrder: ['texto'],
		directFn: ConsultasAction.consultarProveedores, 
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProveedor, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen proveedores con ese nombre');
				}
			}
		}
	}); 
	
	//combo proveedor
	//NS.storeProveedor.load();
	NS.cmbProveedor = new Ext.form.ComboBox({
		store: NS.storeProveedor
		,name: PF+'cmbProveedor'
		,id: PF+'cmbProveedor'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 80
        ,y: 70
        ,width: 190
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un proveedor'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{ fn: function(combo, valor) {
					//linea cambia combo
					BFwrk.Util.updateComboToTextField(PF+'txtProveedor',NS.cmbProveedor.getId());
					//NS.accionarCmbDivisa(combo.getValue());
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
		directFn: ConsultasAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existe el beneficiario con esa clave');
					Ext.getCmp(PF + 'txtProveedor').reset();
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
 	
	
	//Store para llenar el grid de movimiento
  	 NS.storeConsultaMovimiento = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			iIdEmpresa : 0,
			sNoDocto : '',
			sPeriodo : '',
			sIdProveedor : '',
			sNoFactura : ''
		},
		root: '',
		paramOrder:[
					'iIdEmpresa',
					'sNoDocto',
					'sPeriodo',
					'sIdProveedor',
					'sNoFactura'
			],
		directFn: ConsultasAction.realizarBusquedaMovto,
		fields: 
		[
			{name : 'idEstatusMov'},
			{name : 'idFormaPago'},
			{name : 'descFormaPago'},
			{name : 'importe'},
			{name : 'noDocto'},
			{name : 'fecOperacion'},
			{name : 'idDivisa'},
			{name : 'fecImprime'},
			{name : 'fecValor'},
			{name : 'fecConfTrans'},
			{name : 'fecReimprime'},
			{name : 'beneficiario'},
			{name : 'grupoPago'},
			{name : 'poHeaders'},
			{name : 'invoiceType'},
			{name : 'codBloqueo'},
			{name : 'cPeriodo'},
			{name : 'fecPropuesta'},
			{name : 'cveControl'},
			{name : 'descCaja'},
			{name : 'noCliente'},
			//Terminan campos de consulta de Solicitudes
			{name : 'noDocSap'},
			{name : 'fecFact'},
			{name : 'estatusCompensa'},
			{name : 'fechaImp'},
			{name : 'estatus'},
			{name : 'causaRech'},
			{name : 'secuencia'},
			//{name : 'importe'},
			//{name : 'idDivisa'},
			{name : 'formaPago'},
			{name : 'noBenef'},
			{name : 'nombre'},
			{name : 'bZimpFact'}
			//Terminan campos de zimpfact
		],
		listeners: {
			load: function(s, records){
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeConsultaMovimiento, msg:"Buscando..."});
		 
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información  SET','No se encontraron datos con los parametros de búsqueda');
					return;
				}
				//Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
				if(records[0].get('bZimpFact') == true)
				{
					Ext.getCmp(PF + 'gridConsultaZimpfact').setVisible(true);
					Ext.getCmp(PF + 'gridConsultaMovimiento').setVisible(false);
				}
				else
				{
					Ext.getCmp(PF + 'gridConsultaMovimiento').setVisible(true);
					Ext.getCmp(PF + 'gridConsultaZimpfact').setVisible(false);
				}
			}
		}
	}); 
  	 
  	var winInfo = new Ext.Window({
		title: 'Seguimiento de movimiento',
		modal:true,
		shadow:true,
		width: 520,
	   	height: 250,
	   	minWidth: 200,
	   	minHeight: 200,
	   	layout: 'absolute',
	   	plain:true,
	    bodyStyle:'padding:1px;',
	    autoScroll: true,
	    frame: true,
	    closeAction: 'hide',
	    //closable: false,
	   	items: 
		   	[
		   		{
	                xtype: 'container',
	                width: 500,
	                height: 200,
	                items: 
	                [
	                    {
	                        xtype: 'textarea',
	                        width:500,
	                		height: 200,
	                		readOnly: true,
	                		name: PF + 'lblAreaInfo',
	                		id : PF + 'lblAreaInfo'
	                    },
	                    
	                ]
            	}
		   	]
	});
  	
	var winInfoPant = new Ext.Window({
		title: 'Seguimiento de movimiento',
		modal:true,
		shadow:true,
		width: 520,
	   	height: 250,
	   	minWidth: 200,
	   	minHeight: 200,
	   	layout: 'absolute',
	   	plain:true,
	    bodyStyle:'padding:1px;',
	    autoScroll: true,
	    frame: true,
	    closeAction: 'hide',
	    //closable: false,
	   	items: 
		   	[
		   		{
	                xtype: 'container',
	                width: 500,
	                height: 200,
	                items: 
	                [
	                    {
	                        xtype: 'textarea',
	                        width:500,
	                		height: 200,
	                		readOnly: true,
	                		name: PF + 'lblAreaInfoPant',
	                		id : PF + 'lblAreaInfoPant'
	                    },
	                    
	                ]
            	}
		   	]
	});
	
	//Grid principal donde se mostraran los registros de movimiento
	NS.gridConsultaMovimiento = new Ext.grid.GridPanel({
		id: PF + 'gridConsultaMovimiento',
		name: PF + 'gridConsultaMovimiento',
        store : NS.storeConsultaMovimiento,
        cm: new Ext.grid.ColumnModel({
        	defaults: 
	        	{
		            width: 120,
		            value:true,
		            sortable: true
	       		},
            columns: [
                NS.sm,
               	{
               		header: 'No. Docto.', 
               		width: 90, 
               		dataIndex: 'noDocto'
              	},
				{
					header: 'Beneficiario', 
					width: 200, 
					dataIndex: 'beneficiario'
				},
				{
					header: 'Divisa', 
					width: 50, 
					dataIndex: 'idDivisa'
				},
				{
					header: 'Importe', 
					width: 100, 
					dataIndex: 'importe',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney
				},
				{
					header: 'Fecha Valor', 
					width: 100, 
					dataIndex: 'fecValor'
				},
				{
					header: 'Fecha Operaci\u00f3n', 
					width: 100, 
					dataIndex: 'fecOperacion'
				},
				{
					header: 'Forma Pago', 
					width: 120, 
					dataIndex: 'descFormaPago'
				},
				{
					header: 'Caja', 
					width: 100, 
					dataIndex: 'descCaja'
				},
				{
					header: 'No. Cliente', 
					width: 80, 
					dataIndex: 'noCliente',
					hidden:true
				},
				{
					header: 'Invoice type', 
					width: 100, 
					dataIndex: 'invoiceType',
					hidden: true
				}
            ]
        })
        ,viewConfig: 
            	{
			        getRowClass: function(record, rowIndex)
			        {
				              
			        }
		        },
        sm: NS.sm,
        columnLines: true,
        x:0,
        y:0,
        width: 1000,
        height: 245,
        frame:true,
        hidden : true,
        listeners:{
        	dblclick:{
        		fn:function(e){
          			var iNoEmpresa = 0;
			    	var sNoDocto = '';
			    	var sPeriodo = '';
			    	var sProveedor = '';
			    	var sNoFactura = '';
			    	var sFecOperacion = '';
			    	var sEstatus = '';
			    	var sCausa = '';
			    	var iFormaPago = 0;
			    	var iNoBenef = 0;
			    	var sEstatusComp = '';
			    	var bZimpFact = false;

					if(NS.gridConsultaMovimiento.isVisible()){
                		var records = NS.gridConsultaMovimiento.getSelectionModel().getSelections();
                		iNoEmpresa = parseInt(NS.iNoEmpresa);
    					sNoDocto = '' + records[0].get('noDocto');
    					sPeriodo = '' + Ext.getCmp(PF + 'txtPeriodo').getValue();
    					sProveedor = '' + records[0].get('noCliente');
    					sNoFactura = Ext.getCmp(PF + 'txtNoFactura').getValue();
    					sFecOperacion = records[0].get('fecOperacion').substring(0, 10);
                	}else if(NS.gridConsultaZimpfact.isVisible()){
                		var records = NS.gridConsultaZimpfact.getSelectionModel().getSelections();
                		iNoEmpresa = parseInt(NS.iNoEmpresa);
    					sNoDocto = records[0].get('noDocSap');
    					sPeriodo = '' + Ext.getCmp(PF + 'txtPeriodo').getValue();
    					sProveedor = '' + records[0].get('noBenef');
    					sNoFactura = '' + Ext.getCmp(PF + 'txtNoFactura').getValue();
    					sFecOperacion = '' + records[0].get('fechaImp').substring(0, 10);
    					sEstatus = '' + records[0].get('estatus');
    					sCausa = '' + records[0].get('causaRech');
    					iFormaPago = parseInt(records[0].get('formaPago'));
    					iNoBenef = 0;
    					sEstatusComp = '' + records[0].get('estatusCompensa').trim();
    					bZimpFact = true;
                	}
					
					ConsultasAction.realizarSeguimientoMovto(iNoEmpresa, sNoDocto, sPeriodo, sProveedor, sNoFactura,
																sFecOperacion,sEstatus, sCausa, iFormaPago, iNoBenef,
															    sEstatusComp, bZimpFact, function(response, e)
						{
							if(response !== null && response !== undefined && response !== '')
							{
								Ext.getCmp(PF + 'lblAreaInfo').setValue('');
								//NS.sEstatusPantallas = response.substring(response.indexOf('@') + 1);
								Ext.getCmp(PF + 'lblAreaInfo').setValue('' + response.substring(0, response.indexOf('@')));
								winInfo.show();
							}
						}
					);
        		}
        	}
        }
    });
    
    //Grid principal donde se mostraran los registros de zimp_fact
    //Se crean dos grids diferentes por el tipo de columnas que arroja la consulta
    //y el comportamiento de Ext con columnas dinamicas
	NS.gridConsultaZimpfact = new Ext.grid.GridPanel({
		id: PF + 'gridConsultaZimpfact',
		name: PF + 'gridConsultaZimpfact',
        store : NS.storeConsultaMovimiento,
        cm: new Ext.grid.ColumnModel({
        	defaults: 
	        	{
		            width: 120,
		            value:true,
		            sortable: true
	       		},
            columns: [
                NS.sm2,
               	{
               		header: 'No. Docto.', 
               		width: 90, 
               		dataIndex: 'noDocSap'
              	},
				{
					header: 'Beneficiario', 
					width: 200, 
					dataIndex: 'nombre'
				},
				{
					header: 'Divisa', 
					width: 60, 
					dataIndex: 'idDivisa'
				},
				{
					header: 'Importe', 
					width: 120, 
					dataIndex: 'importe',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney
				},
				{
					header: 'Estatus', 
					width: 60, 
					dataIndex: 'estatus'
				},
				{
					header: 'Secuencia', 
					width: 70, 
					dataIndex: 'secuencia'
				},
				{
					header: 'Fecha Exp.', 
					width: 90, 
					dataIndex: 'fechaImp'
				},
				{
					header: 'Causa Rechazo', 
					width: 170, 
					dataIndex: 'causaRech'
				},
				{
					header: 'Estatus Compensa', 
					width: 50, 
					dataIndex: 'estatusCompensa',
					hidden: true
				},
				{
					header: 'No. Beneficiario', 
					width: 100, 
					dataIndex: 'noBenef',
					hidden: true
				},
				{
					header: 'Forma Pago', 
					width: 100, 
					dataIndex: 'formaPago'
				}
            ]
        })
        ,viewConfig: 
            	{
			        getRowClass: function(record, rowIndex)
			        {
				               
			        }
		        },
        sm: NS.sm2,
        columnLines: true,
        x:0,
        y:0,
        width: 1000,
        height: 245,
        frame:true,
        hidden : false,
        listeners:{
        	dblclick:{
        		fn:function(e){
          			var iNoEmpresa = 0;
			    	var sNoDocto = '';
			    	var sPeriodo = '';
			    	var sProveedor = '';
			    	var sNoFactura = '';
			    	var sFecOperacion = '';
			    	var sEstatus = '';
			    	var sCausa = '';
			    	var iFormaPago = 0;
			    	var iNoBenef = 0;
			    	var sEstatusComp = '';
			    	var bZimpFact = true;
			    	
					var records = NS.gridConsultaZimpfact.getSelectionModel().getSelections();
					
					if(records.length == 0) {
						Ext.Msg.alert('SET', 'No existen registros para consultar');
						return;
					}
					
					iNoEmpresa = parseInt(NS.iNoEmpresa);
					sNoDocto = records[0].get('noDocSap');
					sPeriodo = '' + Ext.getCmp(PF + 'txtPeriodo').getValue();
					sProveedor = '' + records[0].get('noBenef');
					sNoFactura = '' + Ext.getCmp(PF + 'txtNoFactura').getValue();
					sFecOperacion = '' + records[0].get('fechaImp').substring(0, 10);
					sEstatus = '' + records[0].get('estatus');
					sCausa = '' + records[0].get('causaRech');
					iFormaPago = parseInt(records[0].get('formaPago'));
					iNoBenef = 0;
					sEstatusComp = '' + records[0].get('estatusCompensa').trim();
					
					ConsultasAction.realizarSeguimientoMovto(iNoEmpresa, sNoDocto, sPeriodo, sProveedor, sNoFactura,
																sFecOperacion,sEstatus, sCausa, iFormaPago, iNoBenef,
															    sEstatusComp, bZimpFact, function(response, e)
						{
							if(response !== null && response !== undefined && response !== '')
							{
								Ext.getCmp(PF + 'lblAreaInfo').setValue('');
								//NS.sEstatusPantallas = response.substring(response.indexOf('@') + 1);
								Ext.getCmp(PF + 'lblAreaInfo').setValue('' + response.substring(0, response.indexOf('@')));
							    winInfo.show();
							}
						}
					);
        		}
        	}
        }
    });
    
    NS.consultarMovto = function()
    {
    	var iNoEmpresa = 0;
    	var sNoDocto = '';
    	var sPeriodo = '';
    	var sProveedor = '';
    	var sNoFactura = '';
		
		iNoEmpresa = parseInt(NS.iNoEmpresa);
		sNoDocto = '' + Ext.getCmp(PF + 'txtNoDocto').getValue();
		sPeriodo = '' + Ext.getCmp(PF + 'txtPeriodo').getValue();
		sProveedor = '' + Ext.getCmp(PF + 'txtProveedor').getValue();
		sNoFactura = Ext.getCmp(PF + 'txtNoFactura').getValue();
		
		if(iNoEmpresa == 0 || sPeriodo == '')
		{
			BFwrk.Util.msgShow('Capture los criterios de b\u00fasqueda', 'INFO');
			Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
			return;
		}
		
		NS.storeConsultaMovimiento.baseParams.iIdEmpresa = iNoEmpresa;
		NS.storeConsultaMovimiento.baseParams.sNoDocto = sNoDocto;
		NS.storeConsultaMovimiento.baseParams.sPeriodo = sPeriodo;
		NS.storeConsultaMovimiento.baseParams.sIdProveedor = sProveedor.trim();
		NS.storeConsultaMovimiento.baseParams.sNoFactura = sNoFactura;
		var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeConsultaMovimiento, msg:"Buscando..."});
		NS.storeConsultaMovimiento.load();
    };
	
	
	NS.BuscarMovimiento = new Ext.form.FormPanel({
    title: 'Seguimiento De Movimiento',
    width: 632,
    height: 477,
    padding: 10,
    layout: 'absolute',
    id: PF + 'BuscarMovimiento',
    name: PF + 'BuscarMovimiento',
    renderTo: NS.tabContId,
    autoScroll: true,
    frame: true,
    	items : [
            {
                xtype: 'fieldset',
                title: '',
                width: 1050,
                height: 480,
                x: 10,
                y: 10,
                layout: 'absolute',
                id: 'fSetPrincipal',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Búsqueda',
                        height: 130,
                        layout: 'absolute',
                        width: 1025,
                        x: 0,
                        y: 0,
                        id: 'fSetBusqueda',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa',
                                x: 0,
                                y: -2
                            },
                            {
                                xtype: 'numberfield',
                                x: 0,
                                y: 20,
                                width: 70,
                                name: PF + 'txtNoEmpresa',
                                id: PF + 'txtNoEmpresa',
                                listeners : 
                                {
                                	change :
                                	{
                                		fn : function(caja, value)
                                		{
                                			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
                       							var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtNoEmpresa', NS.cmbEmpresas.getId());
                   							if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
	                                			NS.iNoEmpresa = valueCombo;
	                   							Ext.getCmp(PF + 'txtProveedor').setValue('');
	                   							NS.cmbProveedor.reset();
	                   							Ext.getCmp(PF + 'txtProveedor').setDisabled(false);
	                   							Ext.getCmp(PF + 'cmbProveedor').setDisabled(false);
                   							}else {
	                                			NS.iNoEmpresa = 0;
	                                			NS.cmbEmpresas.reset();
	                                			Ext.getCmp(PF + 'txtProveedor').setValue('');
	                    						NS.cmbProveedor.reset();
	                    						Ext.getCmp(PF + 'txtProveedor').setDisabled(true);
	                    						Ext.getCmp(PF + 'cmbProveedor').setDisabled(true);
                   							}
                                		}
                                	}
                                }
                            },
                          		NS.cmbEmpresas,
                            {
                                xtype: 'label',
                                text: 'No. Documento',
                                x: 310,
                                y: 0
                            },
                            {
                                xtype: 'textfield',
                                x: 310,
                                y: 20,
                                width: 110,
                                name: PF + 'txtNoDocto',
                                id: PF + 'txtNoDocto'
                            },
                            {
                                xtype: 'label',
                                text: 'Periodo',
                                x: 440,
                                y: 0
                            },
                            {
                                xtype: 'numberfield',
                                x: 440,
                                y: 20,
                                width: 110,
                                name: PF + 'txtPeriodo',
                                id: PF + 'txtPeriodo',
                                value : apps.SET.FEC_HOY.substring(6,10)
                            },
                            {
                                xtype: 'label',
                                text: 'Proveedor',
                                x: 0,
                                y: 50
                            },
                            /*{
                                xtype: 'numberfield',
                                x: 0,
                                y: 70,
                                width: 70,
                                name: PF + 'txtNoProveedor',
                                id: PF + 'txtNoProveedor',
                                disabled: true,
                                listeners:
                                {
                                	change:
                                	{
                                		fn : function(box, value)
                                		{
                                			if(box.getValue() != 0)
                                			{
                                				NS.storeProveedor.baseParams.prefijoProv = '';
	                        					NS.storeProveedor.baseParams.iNoProveedor = parseInt(box.getValue());
	                        					NS.storeProveedor.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
	                        					NS.storeProveedor.load();
                                			}
                                		}
                                	}
                                }
                            },*/
                            {
                                xtype: 'textfield',
                                id: PF+'txtProveedor',
                                name: PF+'txtProveedor',
                                x: 0,
                                y: 70,
                                width: 70,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			//linea cambia combo
		                        			/*var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtProveedor',NS.cmbProveedor.getId());
		                        			if(!comboValue && Ext.getCmp(PF+'txtProveedor').getValue()!='') {
		                        				Ext.getCmp(PF+'txtProveedor').setValue('');
		                        			}*/
		                        			
		                        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
		                        				NS.storeUnicoBeneficiario.baseParams.registroUnico=true;
		                        				NS.storeUnicoBeneficiario.baseParams.condicion=''+caja.getValue();
		                        				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando Proveedor..."});
		                        				NS.storeUnicoBeneficiario.load();
		                       				}else {
		                       					NS.cmbProveedor.reset();
		                       					NS.storeUnicoBeneficiario.removeAll();
		                       				}
		                        			
		                        		}
		                        	}
		                        }
                            },
                            NS.cmbProveedor,
                            {
                                xtype: 'button',
                                text: '...',
                                x: 275,
                                y: 70,
                                width: 20,
                                id: PF+'cmdProveedor',
                                name: PF+'cmdProveedor',
                                listeners:{
		                        	click:{
		                        		fn:function(e){
		                        			
		                        			  if(NS.parametroBus==='' || NS.parametroBus.length < 4) {
												  Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
											  }else{
												  NS.storeProveedor.baseParams.texto=NS.parametroBus;
												  var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProveedor, msg:"Cargando Proveedores..."});
				                        		  NS.storeProveedor.load();
											  }
		                        		}
		                        	}
                                }
                            },
                            //NS.cmbProveedor,
                            /*{
                                xtype: 'button',
                                text: '',
                                x: 270,
                                y: 70,
                                width: 20,
                                id: PF + 'btnComboPro',
                                icon : 'img/icons/lupita.png',
                                hidden: true,
                                listeners:{
	                        	click:{
	                        		fn:function(e)
	                        			{
	                        			    var valTxtEmp = Ext.getCmp(PF + 'txtNoProveedor').getValue(); 
	                        				
	                        			    if(NS.parametroBus === '' && valTxtEmp == '')
	                        				{
	                        					BFwrk.Util.msgShow('Es necesario agregar una(s) letra(s) o nombre y despues pulsar el boton','INFO');
	                        				}
	                        				else
	                        				{
	                        					NS.storeProveedor.baseParams.prefijoProv = NS.parametroBus;
	                        					NS.storeProveedor.baseParams.iNoProveedor = 0;
	                        					NS.storeProveedor.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
	                        					NS.storeProveedor.load();
	                        					NS.parametroBus = '';
	                        				}
	                       				}
	                   				}
			               		}
                            },*/
                            {
                                xtype: 'label',
                                text: 'No. Factura',
                                hidden: true,
                                x: 310,
                                y: 50
                            },
                            {
                                xtype: 'textfield',
                                x: 310,
                                y: 70,
                                width: 110,
                                hidden:true,
                                name: PF + 'txtNoFactura',
                                id: PF + 'txtNoFactura'
                            },
                            {
                                xtype: 'button',
                                text: 'Buscar',
                                x: 450,
                                y: 70,
                                width: 90,
                                id: PF + 'btnBuscar',
                                name : PF + 'btnBuscar',
                                listeners : 
                                {
                                	click : 
                                	{
                                		fn : function (btn)
                                		{
                                			NS.consultarMovto();
                                			//btn.setDisabled(true);
                                		}
                                	}
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Movimientos',
                        x: 0,
                        y: 140,
                        width: 1025,
                        height: 280,
                        id: 'fSetMovtos',
                        items : [
                        	NS.gridConsultaMovimiento,
                        	NS.gridConsultaZimpfact
                        ]
                    },
                    /*{
                        xtype: 'button',
                        text: 'Continuar',
                        x: 290,
                        y: 440,
                        width: 80,
                        id: 'btnContinuar'
                    },*/
                    {
                        xtype: 'button',
                        text: 'Pantallas',
                        x: 840,
                        y: 430,
                        width: 80,
                        id: PF + 'btnPantallas',
                        name: PF + 'btnPantallas',
                        listeners:
                        {
                        	click :
                        	{
                        		fn: function(e)
                        		{
                        		if(NS.sEstatusPantallas==''){
                        			var iNoEmpresa = 0;
                			    	var sNoDocto = '';
                			    	var sPeriodo = '';
                			    	var sProveedor = '';
                			    	var sNoFactura = '';
                			    	var sFecOperacion = '';
                			    	var sEstatus = '';
                			    	var sCausa = '';
                			    	var iFormaPago = 0;
                			    	var iNoBenef = 0;
                			    	var sEstatusComp = '';
                			    	var bZimpFact = false;
                			    	
                					if(NS.gridConsultaMovimiento.isVisible()){
                                		var records = NS.gridConsultaMovimiento.getSelectionModel().getSelections();
                                		iNoEmpresa = parseInt(NS.iNoEmpresa);
                    					sNoDocto = '' + records[0].get('noDocto');
                    					sPeriodo = '' + Ext.getCmp(PF + 'txtPeriodo').getValue();
                    					sProveedor = '' + records[0].get('noCliente');
                    					sNoFactura = Ext.getCmp(PF + 'txtNoFactura').getValue();
                    					sFecOperacion = records[0].get('fecOperacion').substring(0, 10);
                                	}else if(NS.gridConsultaZimpfact.isVisible()){
                                		var records = NS.gridConsultaZimpfact.getSelectionModel().getSelections();
                                		iNoEmpresa = parseInt(NS.iNoEmpresa);
                    					sNoDocto = records[0].get('noDocSap');
                    					sPeriodo = '' + Ext.getCmp(PF + 'txtPeriodo').getValue();
                    					sProveedor = '' + records[0].get('noBenef');
                    					sNoFactura = '' + Ext.getCmp(PF + 'txtNoFactura').getValue();
                    					sFecOperacion = '' + records[0].get('fechaImp').substring(0, 10);
                    					sEstatus = '' + records[0].get('estatus');
                    					sCausa = '' + records[0].get('causaRech');
                    					iFormaPago = parseInt(records[0].get('formaPago'));
                    					iNoBenef = 0;
                    					sEstatusComp = '' + records[0].get('estatusCompensa').trim();
                    					bZimpFact = true;
                                	}
                					if(records.length == 0) {
                						Ext.Msg.alert('SET', 'No existen registros para consultar');
                						return;
                					}
                					ConsultasAction.realizarSeguimientoMovto(iNoEmpresa, sNoDocto, sPeriodo, sProveedor, sNoFactura,
                																sFecOperacion,sEstatus, sCausa, iFormaPago, iNoBenef,
                															    sEstatusComp, bZimpFact, function(response, e)
                						{
                							if(response !== null && response !== undefined && response !== '')
                							{
                								NS.sEstatusPantallas = response.substring(response.indexOf('@') + 1);
                								ConsultasAction.seguimientoPantallas(NS.sEstatusPantallas, function(response, e)
                										{
                											if(response !== null && response !== undefined && response !== '')
                											{
                												Ext.getCmp(PF + 'lblAreaInfoPant').setValue('');
                												Ext.getCmp(PF + 'lblAreaInfoPant').setValue('' + response);
                												winInfoPant.show();
                												NS.sEstatusPantallas='';
                											}
                										}
                									);
                							}
                						}
                					);
                        		}
                        			//var seleccionZimpfact = NS.gridConsultaZimpfact.getSelectionModel().getSelections();
                        			//var sEstatusPantallas=''+seleccionZimpfact[0].get('estatus');		
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 930,
                        y: 430,
                        width: 80,
                        id: PF + 'btnLimpiar',
                        name: PF + 'btnLimpiar',
                        listeners: 
                        {
                        	click:
                        	{
                        		fn: function(e)
                        		{
                        			NS.limpiar();
                        		}
                        	}
                        }
                    }
                ]
            }
        ]
	});
	NS.BuscarMovimiento.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	//staticCheck("div[id*='gridConsultaMovimiento']","div[id*='gridConsultaMovimiento']",8,".x-grid3-scroller",false);
	
});