Ext.onReady(function()
{
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.Catalogos');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.noEmpresa;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.catalogo = "";
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	//LABELS
	NS.lblCatalogos = new Ext.form.Label({
		text: 'Catalogos',
		x: 0,
		y: 0
	});
	
	//FUNCIONES
		
	function makeStructure(catalogo) {

		MantenimientoCatalogosAction.getStructureReportService(catalogo,
												   function(result, e){  										
				                           				this.estructura = result;				                           			  										
							             		   });    						                                      
		
		alert("cabecera: " + eval(this.estructura));   	
		return eval(this.estructura);
	}
	
	//STORE
	NS.storeComboCatalogos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: MantenimientoCatalogosAction.llenaComboCatalogos,
		idProperty: 'nombreCatalogo',
		fields: [
		         'nombreCatalogo',
		         'descCatalogo'
		],
		listeners: {
			load: function(s, records){
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen Catálogos dados de alta');
			}
		}		
	});
	NS.storeComboCatalogos.load();
	
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {},
		paramOrder: [],
		directFn: MantenimientoCatalogosAction.getBodyReportService,		
		fields: makeStructure(),
		listeners:{
			load: function(e, result){}
		}
		 	
		
	});
	
	/*NS.creaGrid = function(catalogo){
		var columnasDinamicas = new Array();
		
		storeLlenaGrid.baseParams.descCatalogo = catalogo;																													
		storeLlenaGrid.load();		
	};
*/
	
	//COMBOBOX
	NS.cmbCatalogos = new Ext.form.ComboBox({
		store: NS.storeComboCatalogos,
		id: PF + 'cmbCatalogos',
		name: PF + 'cmbCatalogos',
		x: 0,
		y: 15,
		width: 250,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'nombreCatalogo',
		displayField: 'descCatalogo',
		autocomplete: true,
		emptyText: 'Seleccione un Catálogo',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select:
			{
				fn: function(combo, valor)
				{					
					NS.catalogo = Ext.getCmp(PF + 'cmbCatalogos').getValue();
					NS.creaGrid(NS.catalogo);
				}
			}
		}	
	});
	
	//GRID 
	NS.gridDatos = new Ext.grid.GridPanel({
		store : storeLlenaGrid,
		width :900,
		height : 300,		
		x: 0,
		y: 0,
		title :'',
		columns: makeStructure()		
	});	
	
	
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: 'Busqueda',
		x: 0,
		y: 0,
		width: 985,
		height: 75,
		layout: 'absolute',
		items: [
		        NS.lblCatalogos,		        
		        NS.cmbCatalogos
		]
	});
	
	NS.panelGrid = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 80,
		width: 985,
		height: 330,
		layout: 'absolute',
		items: [
		              NS.gridDatos
		]
	});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1010,		
		height: 490,
		layout: 'absolute',
		items:
		[
		 	NS.panelBusqueda,
		 	NS.panelGrid,
		 	{
		 		xtype: 'button',
		 		text: 'Aceptar',
		 		x: 600,
		 		y: 430,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{
		 		
		 				}
		 			}
		 		}		 		
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Crear Nuevo',
		 		x: 700,
		 		y: 430,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Eliminar',
		 		x: 800,
		 		y: 430,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{}
		 			}
		 		}
		 	},
		 	{
		 		xtype: 'button',
		 		text: 'Imprimir',
		 		x: 900,
		 		y: 430,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click: {
		 				fn: function(e)
		 				{}
		 			}
		 		}
		 	}
	    ]
	});

	NS.catalogos = new Ext.FormPanel ({
		title: 'Mantenimiento De Catalogos',
		width: 1300,
		height: 706,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'catalogos',
		name: PF + 'catalogos',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	NS.catalogos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});