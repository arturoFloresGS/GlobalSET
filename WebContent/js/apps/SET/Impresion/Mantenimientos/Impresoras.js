/*
 * @author: Victor H. Tello
 * @since: 27/Feb/2012
 */
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Impresion.Mantenimientos.Impresoras');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.bNuevo = false;
	
	NS.labImpresora = new Ext.form.Label({
		text: 'No. Impresora:',
		x: 10
	});
	
	NS.txtImpresora = new Ext.form.TextField({
		id: PF + 'txtImpresora',
		name: PF + 'txtImpresora',
		x: 10,
		y: 15,
		width: 100,
		tabIndex: 0
	});
	
	NS.labCajas = new Ext.form.Label({
		text: 'Caja:',
		x: 150
	});
	
	//Store Caja
	NS.storeCajas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ImpresorasAction.llenaComboCajas, 
		idProperty: 'idCaja', 
		fields: [
			 {name: 'idCaja'},
			 {name: 'desCaja'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCajas, msg: "Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen cajas!!');
			}
		}
	});
	NS.storeCajas.load();
	
	//Combo Caja
	NS.cmbCajas = new Ext.form.ComboBox({
		store: NS.storeCajas,
		id: PF + 'cmbCajas',
		x: 150,
	    y: 15,
	    width: 200,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'idCaja',
		displayField:'desCaja',
		autocomplete: true,
		emptyText: 'Seleccione la Caja',
		triggerAction: 'all',
		value: '',
		tabIndex: 1
	});
	
	NS.labCharola = new Ext.form.Label({
		text: 'Charola:',
		x: 390
	});
	
	NS.txtCharola = new Ext.form.TextField({
		id: PF + 'txtCharola',
		name: PF + 'txtCharola',
		x: 390,
		y: 15,
		width: 100,
		tabIndex: 2
	});
	
	NS.storeImpre = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ImpresorasAction.buscarImpresoras,
		fields: [
			{name: 'noImpresora'},
			{name: 'descCaja'},
			{name: 'noCharola'},
			{name: 'cveCaja'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeImpre, msg: "Buscando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen impresoras!!');
			}
		}
	});
	NS.storeImpre.load();
	
	NS.colImpre = new Ext.grid.ColumnModel([
	    {header: 'No. Impresora', width: 100, dataIndex: 'noImpresora', sortable: true},
	    {header: 'Clave Caja', width: 150, dataIndex: 'cveCaja', sortable: true},
	    {header: 'Caja', width: 150, dataIndex: 'descCaja', sortable: true},
	    {header: 'No. Charola', width: 150, dataIndex: 'noCharola', sortable: true}
	]);
	
	NS.gridImpre = new Ext.grid.GridPanel({
		store: NS.storeImpre,
		id: 'gridImpre',
		cm: NS.colImpre,
		width: 960,
	    height: 270,
	    stripeRows: true,
	    columnLines: true,
	    listeners: {
			click: {
				fn:function(grid) {
					var regSelec = NS.gridImpre.getSelectionModel().getSelections();
					
					if(regSelec.length > 0) {
	      				Ext.getCmp(PF + 'txtImpresora').setValue(regSelec[0].get('noImpresora'));
	      				Ext.getCmp(PF + 'cmbCajas').setValue(regSelec[0].get('descCaja'));
	      				Ext.getCmp(PF + 'txtCharola').setValue(regSelec[0].get('noCharola'));
	      			}
					NS.desHabilitarCampos(true);
				}
			}
		}
	});
	NS.contBusqueda = new Ext.form.FieldSet({
		y: 60,
		width: 985,
		height: 300,
		layout: 'absolute',
		items: [
		        NS.gridImpre
		]
	});
	NS.contGeneral = new Ext.form.FieldSet({
		x: 20,
		y: 5,
		width: 1010,
		height: 450,
		layout: 'absolute',
		items: [
		         NS.labImpresora,	NS.txtImpresora,	NS.labCajas,		NS.cmbCajas,
		         NS.labCharola,		NS.txtCharola,		NS.contBusqueda,
		         {
		        	 xtype: 'button',
		        	 text: 'Ejecutar',
		        	 x: 610,
		        	 y: 370,
		        	 width: 80,
		        	 listeners: {
		        	 	click: {
		        	 		fn: function(e) {
		        	 			NS.ejecutar();
		        	 			NS.limpiar();
		        	 			NS.desHabilitarCampos(true);
		        	 		}
		         		}
		         	 }
		         },{
		        	 xtype: 'button',
		        	 text: 'Nuevo',
		        	 x: 700,
		        	 y: 370,
		        	 width: 80,
		        	 listeners: {
		        	 	click: {
		        	 		fn: function(e) {
			        	 		NS.nuevo();
		        	 		}
		         		}
		         	 }
		         },{
		        	 xtype: 'button',
		        	 text: 'Eliminar',
		        	 x: 880,
		        	 y: 370,
		        	 width: 80,
		        	 listeners: {
		        	 	click: {
		        	 		fn: function(e) {
		        	 			NS.eliminar();
		        	 			NS.limpiar();
		        	 			NS.desHabilitarCampos(true);
		        	 		}
		         		}
		         	 }
		         }
		]
	});
	NS.desHabilitarCampos = function(habilita) {
		Ext.getCmp(PF + 'txtImpresora').setReadOnly(habilita);
		Ext.getCmp(PF + 'cmbCajas').setReadOnly(habilita);
		Ext.getCmp(PF + 'txtCharola').setReadOnly(habilita);
	};
	NS.desHabilitarCampos(true);
	
	NS.limpiar = function() {
		Ext.getCmp(PF + 'txtImpresora').setValue('');
		Ext.getCmp(PF + 'cmbCajas').reset();
		Ext.getCmp(PF + 'txtCharola').setValue('');
	};
	
	NS.nuevo = function() {
		NS.desHabilitarCampos(false);
		NS.limpiar();
		NS.bNuevo = true;
	};
	
	NS.eliminar = function() {
		if(Ext.getCmp(PF + 'txtImpresora').getValue() == '') {
			Ext.Msg.alert('SET', 'Seleccione una impresora a borrar');
			return;
		}
		
		Ext.Msg.confirm('SET', '¿Esta seguro de borrar la impresora?',function(btn) {  
			if(btn == 'yes') {
				var regSelec = NS.gridImpre.getSelectionModel.getSelections();
				
				ImpresorasAction.eliminarImpre(parseInt(Ext.getCmp(PF + 'txtImpresora').getValue()), function(res, e) {
					if(res != null && res != undefined && res != '') Ext.Msg.alert('SET', res + '!!');
				});
				NS.gridImpre.store.remove(regSelec[0]);
				NS.gridImpre.getView().refresh();
				NS.limpiar();
			}
		});
	};
	
	NS.ejecutar = function() {
		ImpresorasAction.insertarImpre('' + Ext.getCmp(PF + 'txtImpresora').getValue(), '' + Ext.getCmp(PF + 'cmbCajas').getValue(),
				'' + Ext.getCmp(PF + 'txtCharola').getValue(), function(res, e) {
			
			if(res != null && res != undefined && res != '') Ext.Msg.alert('SET', '' + res.msgUsuario + '!!');
		});
		NS.gridImpre.store.removeAll();
		NS.gridImpre.getView().refresh();
		NS.storeImpre.load();
	};
	
	NS.contImpresoras = new Ext.FormPanel( {
		title: 'Mantenimiento de Impresoras',
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            NS.contGeneral
	    ]
	});
	NS.contImpresoras.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
