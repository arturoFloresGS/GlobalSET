/*
@author: Jessica Arelly Cruz Cruz
@since: 18/07/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Conciliacion.ConciliacionAutomatica');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	
	NS.labEmpresa = new Ext.form.Label({
		text: 'Empresa',
        x: 10
	});
	
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
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
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCmbEmpresa, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0) {
					Ext.Msg.alert('SET','No tiene empresas asignadas');
				}
				//Se agrega la opcion todas las empresas
	 			var recEmpresas = NS.storeCmbEmpresa.recordType;	
				var todas = new recEmpresas({
					id: 0,
					descripcion: '*************** TODAS ***************'
		       	});
		   		NS.storeCmbEmpresa.insert(0, todas);
			}
		}
	});
	NS.storeCmbEmpresa.load();
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa
		,id: PF + 'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 15
        ,width: 450
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.storeCmbBanco.baseParams.empresa = parseInt(combo.getValue());
					NS.storeCmbBanco.load();
				}
			}
		}
	});
	
	NS.labBanco = new Ext.form.Label({
		text: 'Banco',
        x: 10,
        y: 50
	});
	
	NS.storeCmbBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['empresa'],
		directFn: ConciliacionBancoSetAction.llenarComboBancos,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCmbBanco, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0) {
					Ext.Msg.alert('SET','No tiene bancos asignados');
				}
			}
		}
	}); 
	
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeCmbBanco
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 65
        ,width: 200
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.storeChequeras.baseParams.noEmpresa = parseInt(NS.cmbEmpresa.getValue());
					NS.storeChequeras.baseParams.banco = parseInt(combo.getValue());
					NS.storeChequeras.load();
				}
			}
		}
	});
	
	NS.labFechas = new Ext.form.Label({
		text: 'Fechas',
        x: 250,
        y: 50
	});
	
	NS.txtFechaIni = new Ext.form.DateField({
		id: PF + 'txtFechaIni',
		x: 250,
		y: 65,
		width: 100,
		format: 'd/m/Y',
		value : apps.SET.FEC_HOY
	});
	
	NS.txtFechaFin = new Ext.form.DateField({
		id: PF + 'txtFechaFin',
		x: 360,
		y: 65,
		width: 100,
		format: 'd/m/Y',
		value : apps.SET.FEC_HOY
	});
	
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['noEmpresa','banco'],
		directFn: ConciliacionBancoSetAction.llenarGridChequeras,
		idProperty: 'idChequera',
		fields: [
			{name: 'idChequera'},
			{name: 'noEmpresa'},
			{name: 'descSucursal'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});
				NS.smE.selectRange(0,records.length-1);
				Ext.getCmp(PF+'lblMensaje').setText('Las ' + records.length + ' chequeras seleccionadas seran conciliadas');
				Ext.getCmp(PF+'lblMensaje').setVisible(true);
			}
		}
	}); 
	
	NS.smE = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		singleSelect: false
	});
	
	//grid empresas coinversionistas
	NS.gridChequeras  = new Ext.grid.GridPanel({
		store : NS.storeChequeras,
		id: PF+'gridChequeras',
		name: PF+'gridChequeras',
		title: 'Chequeras',
		frame: true,
		width: 487,
       	height: 320,
		x:10,
		y:100,
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
       	columns : [ NS.smE,
       	         {header :'noEmpresa', width :100, dataIndex :'noEmpresa', hidden: true},
       	         {header :'Empresa', width :280, dataIndex :'descSucursal'},
       	         {header :'Chequera', width :150, dataIndex :'idChequera'}]
			}),
    	viewConfig: {
	        getRowClass: function(record, index){ 
		        
           	}
        },
		sm: NS.smE,
		listeners:{
			click:{
				fn:function(grid){
					var regSelec=NS.gridChequeras.getSelectionModel().getSelections();
					Ext.getCmp(PF + 'lblMensaje').setText('Las ' + regSelec.length + ' chequeras seleccionadas seran conciliadas');
				}
			}
		}
	});
	
	NS.labMensaje = new Ext.form.Label({
		text: '',
		id: PF + 'lblMensaje',
        x: 10,
        y: 430,
        hidden: false
	});
	
	NS.ejecutar = function() {
		var regSelec = NS.gridChequeras.getSelectionModel().getSelections();
		var matrizRegistros = new Array ();
		
		BFwrk.Util.msgWait('Conciliando...', true);
		
		for(var i = 0; i < regSelec.length; i ++) {
			var registro = {};
			registro.idChequera = regSelec[i].get('idChequera');
			registro.noEmpresa = regSelec[i].get('noEmpresa');
			registro.noBanco = NS.cmbBanco.getValue();
			registro.descBanco = NS.storeCmbBanco.getById(parseInt(NS.cmbBanco.getValue())).get('descripcion');
			matrizRegistros[i] = registro; 
		}
		var jsonRegistros = Ext.util.JSON.encode(matrizRegistros);
		
		ConciliacionBancoSetAction.ejecutarConciliacionAutomaticaBS(jsonRegistros, function(result, e) {
 			BFwrk.Util.msgWait('Finalizado...', false);
			Ext.Msg.alert('SET', result);
		});
	};
	
	NS.contenedorConciliacionAtm = new Ext.FormPanel({
	    title: 'Conciliación Automatica Bancaria',
	    width: 519,
	    height: 520,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            {
	                xtype: 'fieldset',
	                x: 10,
	                y: 0,
	                width: 530,
	                height: 480,
	                layout: 'absolute',
	                items: [
	                        NS.labEmpresa,
	                        NS.cmbEmpresa,
	                        NS.labBanco,
	                        NS.cmbBanco,
	                        NS.labFechas,
	                        NS.txtFechaIni,
	                        NS.txtFechaFin, 
	                        NS.gridChequeras,
	                        NS.labMensaje,
	                        {
	                        	xtype: 'button',
		                        text: 'Limpiar',
		                        width: 80,
		                        x: 410,
		                        y: 430, 
		                        listeners:{
									click:{
										fn:function(){
											NS.contenedorConciliacionAtm.getForm().reset();
											NS.storeChequeras.removeAll();
											NS.gridChequeras.getView().refresh();
											Ext.getCmp(PF + 'lblMensaje').hide();
										}
									}
								}
		                    },{
		                        xtype: 'button',
		                        text: 'Ejecutar',
		                        width: 80,
		                        x: 320,
		                        y: 430,
		                        listeners:{
		                        	click:{
		                        		fn:function(){
		                        			NS.ejecutar();
		                        		}
		                        	}
		                        }
		                    }
	                ]
	            }
	        ]
	});
	NS.contenedorConciliacionAtm.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
