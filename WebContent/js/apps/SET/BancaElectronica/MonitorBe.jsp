<link rel="stylesheet" type="text/css" href="/PruebasExtjs/ext-3.3.0/resources/css/ext-all.css" />
<script src="/PruebasExtjs/ext-3.3.0/adapter/ext/ext-base.js"></script>
<script src="/PruebasExtjs/ext-3.3.0/ext-all.js"></script>

<script>

		Ext.onReady(function(){  
		
		
				// make sample array data
										
		var sampleData= [
						['12/11/2010', '12', '123456789', '1000','2000','1000','2000']
						];
						
						// create the data store
						var store = new Ext.data.SimpleStore({
						fields: [
						{name: 'fecha'},
						{name: 'banco'}, 
						{name: 'chequera'},
						{name: 'inBanca'},
						{name: 'inSet'},
						{name: 'egBanca'},
						{name: 'egSet'}
						]
						});
						
						// load data
						store.loadData(sampleData);
						
						// create the grid
						var grid = new Ext.grid.GridPanel({ //objeto grid se le asignan los datos del store
						store: store, 
						columns: [
						{header: 'Fecha', width: 100, dataIndex: 'fecha'},//header=encabezado
						{header: 'Banco', width: 100, dataIndex: 'banco'},//dataIndex=valor del store
						{header: 'Chequera', width: 100, dataIndex: 'chequera'},
						{header: 'Ingresos Banca', width: 100, dataIndex: 'inBanca'},
						{header: 'Ingresos SET', width: 100, dataIndex: 'inSet'},
						{header: 'Egresos Banca', width: 100, dataIndex: 'egBanca'},
						{header: 'Egresos SET', width: 100, dataIndex: 'egSet'}
						],
						stripeRows: true,
						height:220,
						width:705
						});
		
		
 				var simple = new Ext.FormPanel( {
    //title: 'Monitor de Banca Electronica',
    width: 764,
    height: 464,
    padding: 10,
    layout: 'absolute',
    renderTo: 'fielsett',
    items : [
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 10,
                width: 740,
                height: 110,
                layout: 'absolute',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 10,
                        y: 0
                    },
                    {
                        xtype: 'textfield',
                        x: 70,
                        y: 0,
                        width: 360
                    },
                    {
                        xtype: 'checkbox',
                        x: 440,
                        y: -1,
                        boxLabel: 'Todas'
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha:',
                        x: 10,
                        y: 30
                    },
                    {
                        xtype: 'datefield',
                        x: 10,
                        y: 50
                    },
                    {
                        xtype: 'datefield',
                        x: 120,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 240,
                        y: 30
                    },
                    {
                        xtype: 'textfield',
                        x: 240,
                        y: 50,
                        width: 50
                    },
                    {
                        xtype: 'trigger',
                        x: 300,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Chequera:',
                        x: 450,
                        y: 30
                    },
                    {
                        xtype: 'trigger',
                        x: 450,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'No. Diferencias:',
                        x: 610,
                        y: 30
                    },
                    {
                        xtype: 'label',
                        text: 'Ingr',
                        x: 590,
                        y: 50
                    },
                    {
                        xtype: 'textfield',
                        x: 620,
                        y: 50,
                        width: 30
                    },
                    {
                        xtype: 'label',
                        text: 'Egr',
                        x: 660,
                        y: 50
                    },
                    {
                        xtype: 'textfield',
                        x: 680,
                        y: 50,
                        width: 30
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 630,
                        y: 0,
                        width: 60
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Monitor de pagos',
                x: 10,
                y: 130,
                width: 740,
                height: 260,
                items: [grid]
            },
            {
                xtype: 'button',
                text: 'Cerrar',
                x: 670,
                y: 400,
                width: 80
            },
            {
                xtype: 'button',
                text: 'Limpiar',
                x: 560,
                y: 400,
                width: 80
            },
            {
                xtype: 'button',
                text: 'Imprimir',
                x: 450,
                y: 400,
                width: 80
            }
        ]
    });
});

  </script>

<div id="fielsett"></div>