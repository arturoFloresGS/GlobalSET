/* 
 * @author: Victor H. Tello
 * @since: 20/Enero/2015
 */
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Inversiones.CanastaInversion.TasaComision');
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	var textFieldDias = new Ext.form.TextField();
	var textFieldTasa = new Ext.form.TextField();
	
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
	
	NS.labComision = new Ext.form.Label({
		text: 'Comision mensual',
        x: 5,
        y: 355
	});
    
	NS.txtComision = new Ext.form.TextField({
		id: PF + 'txtComision',
		x: 5,
        y: 370,
        width: 100
    });
	
	//Store Datos donde se muestran los valores seleccionados
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: BarridoInversionAction.obtenerTasaComision,
		idProperty: 'dias',
		fields: [
			{name: 'dias'},
			{name: 'tasa'},
			{name: 'comision'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDatos, msg:"Cargando..."});
				
				if(records.length == null || records.length <=0)
					Ext.Msg.alert('SET', 'No existe información actualmente');
				
				if(parseInt(records[0].get('dias')) == 0)
					Ext.getCmp(PF + 'txtComision').setValue(records[0].get('comision'));
			}
		}
	});
	NS.storeDatos.load();
	
	//Para indicar el modo de seleccion de los registros en el Grid 
	NS.tipoSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	
	//Columna de Seleccion  
	NS.columnaSelec = new Ext.grid.ColumnModel([
	    //NS.tipoSeleccion,
	    {header: 'Dias', width: 130, editor:textFieldDias, dataIndex: 'dias', sortable: true, css: 'text-align:right;'},
	    {header: 'Tasa', width: 130, editor:textFieldTasa, dataIndex: 'tasa', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererDecimal}
	]);
	
	//Grid para mostrar los datos seleccionados
	NS.gridDatos = new Ext.grid.EditorGridPanel({
		store: NS.storeDatos,
		id: 'gridDatos',
		cm: NS.columnaSelec,
		sm: NS.tipoSeleccion,
		width: 350,
	    height: 320,
	    y: 30,
	    stripeRows: true,
	    columnLines: true,
		listeners: {
			click: {
				fn:function(grid) {
				}
			}
		}
	});
	
	NS.contCuentas = new Ext.form.FieldSet({
		title: '',
        y: 5,
        width: 375,
        height: 415,
        layout: 'absolute',
        items: [
               NS.gridDatos,
               NS.labComision,
               NS.txtComision,
               {
            	   xtype: 'button',
            	   text: 'Nuevo',
                   x: 270,
                   width: 80,
                   height: 22,
                   listeners: {
                   		click: {
            	   			fn:function(e) {
            	   				NS.nuevo();
               				}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Guardar',
                   x: 180,
                   y: 370,
                   width: 80,
                   height: 22,
                   listeners: {
                   		click: {
            	   			fn:function(e) {
            	   				NS.guardar();
            	   			}
               			}
               		}
               },{
            	   xtype: 'button',
            	   text: 'Imprimir',
            	   x: 270,
            	   y: 370,
            	   width: 80,
            	   height: 22,
            	   
            	   listeners: {
            	   		click: {
            	   			fn:function(e) {
            	   				
            	   			}
               			}
               		}
               }
        ]
    });
	
	//Contenedor General
	NS.contGeneral = new Ext.form.FieldSet({
		title: '',
		width: 400,
	    height: 450,
	    x: 300,
	    y: 5,
	    layout: 'absolute',
	    items:[
			NS.contCuentas
		]
	});
	
	NS.nuevo = function() {
		var records = NS.storeDatos.data.items;
		var datosClase = NS.gridDatos.getStore().recordType;
		
		var datos = new datosClase({
			dias: '',
			tasa: ''
      	});
		NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(0, datos);
  		NS.gridDatos.getView().refresh();
	};
	
	NS.guardar = function() {
		var selDatos = NS.storeDatos.data.items;
		var matDatos  = new Array();
		var cadenaJson = '';
		
		for(var i=0; i<selDatos.length; i++) {
			if(selDatos[i].get('dias') != '' && selDatos[i].get('tasa') != '') {
				var vec = {};
				vec.dias = selDatos[i].get('dias');
				vec.tasa = selDatos[i].get('tasa');
				matDatos[i] = vec;
			}
		}
		cadenaJson = Ext.util.JSON.encode(matDatos);
		
		BarridoInversionAction.insertarTasaComision(cadenaJson, Ext.getCmp(PF + 'txtComision').getValue(), function(res, e) {
			Ext.Msg.alert('SET', res);
			NS.limpiar();
		});
	};
	
	//Funcion para limpiar los campos
	NS.limpiar = function(){
		NS.gridDatos.store.removeAll();
		NS.gridDatos.getView().refresh();
		NS.storeDatos.load();
	};
	
	//Contenedor principal del formulario
	NS.TasaComision = new Ext.FormPanel({
		title: 'Almacenamiento de tasas bancarias',
	    width: 1020,
	    height: 500,
	    frame: true,
	    padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF + 'tasaComision',
	    renderTo: NS.tabContId,
	    items:[
	           NS.contGeneral
	    ]
	});
	NS.TasaComision.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});