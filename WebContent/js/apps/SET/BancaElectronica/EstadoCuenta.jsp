<link rel="stylesheet" type="text/css" href="/PruebasExtjs/ext-3.3.0/resources/css/ext-all.css" />
<script src="/PruebasExtjs/ext-3.3.0/adapter/ext/ext-base.js"></script>
<script src="/PruebasExtjs/ext-3.3.0/ext-all.js"></script>

<script>
Ext.onReady(function(){  

	// make sample array data
										
				var sampleData= [
								['E', '1', 5000000, '0000', 'PAGOS', '12', '12/11/2010', 'OK']
								];
						
						// create the data store
						var store = new Ext.data.SimpleStore({
						fields: [
						{name: 'ie'},
						{name: 'folio'},
						{name: 'importe', type: 'int'},
						{name: 'ref'},
						{name: 'concepto'},
						{name: 'sucursal'},
						{name: 'fecha'},
						{name: 'obs'}
						]
						});
						
						// load data
						store.loadData(sampleData);
						
						// create the grid
						var grid = new Ext.grid.GridPanel({ //objeto grid se le asignan los datos del store
						store: store, 
						columns: [
						{header: 'I/E', width: 100, dataIndex: 'ie'},
						{header: 'Folio', width: 100, dataIndex: 'folio'},
						{header: 'Importe', width: 100, dataIndex: 'importe'},
						{header: 'Referencia', width: 100, dataIndex: 'ref'},
						{header: 'Concepto', width: 100, dataIndex: 'concepto'},
						{header: 'Sucursal', width: 100, dataIndex: 'sucursal'},
						{header: 'Fecha', width: 100, dataIndex: 'fecha'},
						{header: 'Observaciones', width: 100, dataIndex: 'obs'}
						],
						stripeRows: true,
						height:90,
						width:550,
						x: 10
						});

 				var simple = new Ext.FormPanel(
 				 {
    title: 'Captura de Estado de Cuenta',
    width: 663,
    height: 580,
    padding: 10,
    layout: 'absolute',
    renderTo: 'fielsett',
    items : [
            {
                xtype: 'fieldset',
                title: 'Busqueda',
                layout: 'absolute',
                x: 10,
                y: 50,
                width: 640,
                height: 90,
                items: [
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 0,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Fecha:',
                        x: 390,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Chequera:',
                        x: 210,
                        y: 0
                    },
                    {
                        xtype: 'trigger',
                        x: 0,
                        y: 20,
                        width: 180
                    },
                    {
                        xtype: 'timefield',
                        x: 210,
                        y: 20,
                        width: 160
                    },
                    {
                        xtype: 'textfield',
                        x: 390,
                        y: 20,
                        width: 100
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 520,
                        y: 20,
                        width: 80
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Banca Electronica',
                width: 640,
                x: 10,
                y: 150,
                height: 160,
                layout: 'absolute',
                items: [
                		grid,
                    {
                        xtype: 'button',
                        text: 'Modificar',
                        x: 330,
                        y: 90,
                        width: 80
                    },
                    {
                        xtype: 'button',
                        text: 'Eliminar',
                        x: 530,
                        y: 90,
                        width: 80
                    },
                    {
                        xtype: 'button',
                        text: 'Crear Nuevo',
                        x: 430,
                        y: 90,
                        width: 80
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: '',
                width: 640,
                height: 180,
                x: 10,
                y: 330,
                layout: 'absolute',
                items: [
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 0,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Concepto:',
                        x: 0,
                        y: 40
                    },
                    {
                        xtype: 'label',
                        text: 'Chequera:',
                        x: 0,
                        y: 80
                    },
                    {
                        xtype: 'label',
                        text: 'Referencia:',
                        x: 0,
                        y: 120
                    },
                    {
                        xtype: 'trigger',
                        x: 70,
                        y: 0
                    },
                    {
                        xtype: 'trigger',
                        x: 70,
                        y: 40
                    },
                    {
                        xtype: 'trigger',
                        x: 70,
                        y: 80
                    },
                    {
                        xtype: 'textfield',
                        x: 70,
                        y: 120
                    },
                    {
                        xtype: 'label',
                        text: 'Sucursal:',
                        x: 260,
                        y: 80
                    },
                    {
                        xtype: 'label',
                        text: 'Folio:',
                        x: 260,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Importe:',
                        x: 260,
                        y: 120
                    },
                    {
                        xtype: 'numberfield',
                        x: 320,
                        y: 0,
                        width: 80
                    },
                    {
                        xtype: 'textfield',
                        x: 320,
                        y: 80,
                        width: 80
                    },
                    {
                        xtype: 'numberfield',
                        x: 320,
                        y: 120,
                        width: 80
                    },
                    {
                        xtype: 'radio',
                        boxLabel: 'Egreso',
                        x: 260,
                        y: 40
                    },
                    {
                        xtype: 'radio',
                        boxLabel: 'Ingreso',
                        x: 330,
                        y: 40
                    },
                    {
                        xtype: 'label',
                        text: 'Observaciones:',
                        x: 450,
                        y: -2
                    },
                    {
                        xtype: 'textarea',
                        anchor: '100%',
                        x: 450,
                        y: 20,
                        height: 80
                    },
                    {
                        xtype: 'button',
                        text: 'Aceptar',
                        x: 460,
                        y: 120,
                        width: 70
                    },
                    {
                        xtype: 'button',
                        text: 'Cancelar',
                        x: 540,
                        y: 120,
                        width: 70
                    }
                ]
            },
            {
                xtype: 'label',
                text: 'Empresa:',
                x: 20,
                y: 20
            },
            {
                xtype: 'textfield',
                x: 80,
                y: 20,
                width: 400
            },
            {
                xtype: 'button',
                text: 'Limpiar',
                x: 480,
                y: 520,
                width: 70
            },
            {
                xtype: 'button',
                text: 'Cerrar',
                x: 560,
                y: 520,
                width: 70
            }
        ]
        });
});

  </script>

<div id="fielsett"></div>
