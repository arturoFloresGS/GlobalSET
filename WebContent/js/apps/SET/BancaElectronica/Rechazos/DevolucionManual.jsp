<link rel="stylesheet" type="text/css" href="/PruebasExtjs/ext-3.3.0/resources/css/ext-all.css" />
<script src="/PruebasExtjs/ext-3.3.0/adapter/ext/ext-base.js"></script>
<script src="/PruebasExtjs/ext-3.3.0/ext-all.js"></script>

<script> 

Ext.onReady(function(){  

// make sample array data
										
				var sampleData= [
								[116, 'COSTCO', '123456789', 5000000, '12/11/2010', 'PAGOS', 'PAGOS SET', '12', '0000']
								];
						
						// create the data store
						var store = new Ext.data.SimpleStore({
						fields: [
						{name: 'no', type: 'int'},
						{name: 'nombre'}, //default string
						{name: 'chequera'},
						{name: 'importe', type: 'int'},
						{name: 'fecha'},
						{name: 'concepto'},
						{name: 'conceptoset'},
						{name: 'folio'},
						{name: 'ref'}
						]
						});
						
						// load data
						store.loadData(sampleData);
						
						// create the grid
						var grid = new Ext.grid.GridPanel({ //objeto grid se le asignan los datos del store
						store: store, 
						columns: [
						{id:'no',header: 'No Empresa', width: 100, sortable: true, dataIndex: 'no'},
						{header: 'Nombre Empresa', width: 100, dataIndex: 'nombre'},
						{header: 'Chequera', width: 100, dataIndex: 'chequera'},
						{header: 'Importe', width: 100, dataIndex: 'importe'},
						{header: 'Fecha', width: 100, dataIndex: 'fecha'},
						{header: 'Concepto', width: 100, dataIndex: 'concepto'},
						{header: 'Concepto SET', width: 100, dataIndex: 'conceptoset'},
						{header: 'Folio', width: 100, dataIndex: 'folio'},
						{header: 'Referencia Egreso', width: 100, dataIndex: 'ref'}
						],
						stripeRows: true,
						height:150,
						width:550,
						x: 10
						});

 				var simple = new Ext.FormPanel(
 				{
			    //title: 'Devolucion Manual de Pagos',
			    width: 633,
			    height: 486,
			    padding: 10,
			    layout: 'absolute',
			  	renderTo: 'fielsett',
			  	x: 20,
			  	y: 20,
			  	items : [
            {
                xtype: 'fieldset',
                title: 'Busqueda',
                x: 10,
                y: 10,
                layout: 'absolute',
                width: 610,
                height: 120,
                items: [
                    {
                        xtype: 'label',
                        text: 'Criterio:',
                        x: 0,
                        y: 10
                    },
                    {
                        xtype: 'label',
                        text: 'Valor:',
                        x: 0,
                        y: 60
                    },
                    {
                        xtype: 'trigger',
                        x: 60,
                        y: 10
                    },
                    {
                        xtype: 'textfield',
                        x: 270,
                        y: 10
                    },
                    {
                        xtype: 'trigger',
                        x: 60,
                        y: 60
                    },
                    {
                        xtype: 'label',
                        text: 'Empresa: ',
                        x: 210,
                        y: 10
                    },
                    {
                        xtype: 'checkbox',
                        boxLabel: 'Todas',
                        x: 420,
                        y: 10
                    },
                    {
                        xtype: 'textarea',
                        anchor: '80%',
                        x: 210,
                        y: 40,
                        height: 40
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 490,
                        y: 50,
                        width: 70
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Depositos Banca Electronica',
                layout: 'absolute',
                x: 10,
                y: 150,
                width: 610,
                height: 230,
                items: [
                    {
                        xtype: 'label',
                        text: 'Total de Registros:',
                        x: 240,
                        y: 160
                    },
                    {
                        xtype: 'numberfield',
                        x: 360,
                        y: 160,
                        width: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Monto Total:',
                        x: 410,
                        y: 160
                    },
                    {
                        xtype: 'numberfield',
                        x: 500,
                        y: 160,
                        width: 50
                    },
                    grid
                ]
            },
            {
                xtype: 'button',
                text: 'Cerrar',
                x: 510,
                y: 400,
                width: 70
            },
            {
                xtype: 'button',
                text: 'Limpiar',
                x: 420,
                y: 400,
                width: 70
            },
            {
                xtype: 'button',
                text: 'Ejecutar',
                x: 330,
                y: 400,
                width: 70
            }
        ]
       
   
} );
											
 				}); 
 				

  </script>

<div id="fielsett"></div>