<link rel="stylesheet" type="text/css" href="/PruebasExtjs/ext-3.3.0/resources/css/ext-all.css" />
<script src="/PruebasExtjs/ext-3.3.0/adapter/ext/ext-base.js"></script>
<script src="/PruebasExtjs/ext-3.3.0/ext-all.js"></script>

<script>

Ext.onReady(function(){  

		// make sample array data
										
				var sampleData= [
								['123456789', '0000', 'MN', '1', 'XXXX', 1000,
								 '12/11/2010', '13/11/2010','14/11/2010','15/11/2010','16/11/2010',
								 '1', 'archivo.txt']
								];
						
						// create the data store
						var store = new Ext.data.SimpleStore({
						fields: [
						{name: 'chequera'},
						{name: 'ref'},
						{name: 'divisa'},
						{name: 'estatus'},
						{name: 'beneficio'},
						{name: 'importe', type: 'int'},
						{name: 'envio'},
						{name: 'alta'},
						{name: 'expiracion'},
						{name: 'mant'},
						{name: 'pago'},
						{name: 'estatus2'},
						{name: 'archivo'}
						]
						});
						
						// load data
						store.loadData(sampleData);
						
						// create the grid
						var grid = new Ext.grid.GridPanel({ //objeto grid se le asignan los datos del store
						store: store, 
						columns: [
						{header: 'Chequera', width: 100, dataIndex: 'chequera'},
						{header: 'Referencia', width: 100, dataIndex: 'ref'},
						{header: 'Divisa', width: 100, dataIndex: 'divisa'},
						{header: 'Estatus', width: 100, dataIndex: 'estatus'},
						{header: 'Beneficio', width: 100, dataIndex: 'beneficio'},
						{header: 'Importe', width: 100, dataIndex: 'importe'},
						{header: 'Fecha de Envio', width: 100, dataIndex: 'envio'},
						{header: 'Fecha de Alta', width: 100, dataIndex: 'alta'},
						{header: 'Fecha de Expiracion', width: 100, dataIndex: 'expiracion'},
						{header: 'Fecha de Mantenimiento', width: 100, dataIndex: 'mant'},
						{header: 'Fecha de Pago', width: 100, dataIndex: 'pago'},
						{header: 'Estatus', width: 100, dataIndex: 'estatus2'},
						{header: 'Nombre de Archivo', width: 100, dataIndex: 'archivo'}
						],
						stripeRows: true,
						height:160,
						width:600,
						x: 10
						});

 				var simple = new Ext.FormPanel(
 				 {
    //title: 'Regreso Cheque Ocurre',
    width: 655,
    height: 329,
    padding: 10,
    layout: 'absolute',
    renderTo: 'fielsett',
    items : [
            {
                xtype: 'label',
                text: 'Banco:',
                x: 20,
                y: 10
            },
            {
                xtype: 'trigger',
                x: 20,
                y: 30,
                width: 150
            },
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 60,
                width: 630,
                height: 190,
                items: [
                	grid
               		 ]
            },
            {
                xtype: 'button',
                text: 'Ejecutar',
                x: 260,
                y: 260,
                width: 80
            },
            {
                xtype: 'button',
                text: 'Importar',
                x: 360,
                y: 260,
                width: 80
            },
            {
                xtype: 'button',
                text: 'Limpiar',
                x: 460,
                y: 260,
                width: 80
            },
            {
                xtype: 'button',
                text: 'Cerrar',
                x: 560,
                y: 260,
                width: 80
            }
            ]
     });
});

  </script>

<div id="fielsett"></div>