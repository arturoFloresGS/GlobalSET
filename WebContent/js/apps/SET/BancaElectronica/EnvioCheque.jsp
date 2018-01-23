<link rel="stylesheet" type="text/css" href="/PruebasExtjs/ext-3.3.0/resources/css/ext-all.css" />
<script src="/PruebasExtjs/ext-3.3.0/adapter/ext/ext-base.js"></script>
<script src="/PruebasExtjs/ext-3.3.0/ext-all.js"></script>

<script>

		Ext.onReady(function(){  
 				
 				
 							// make sample array data
										
				var sampleData= [
								['1', '1', 'XX', '10000', 'MN', 'Bancomer', '0000', 'XX', '1', '1', '12', 'XX',
								 '01', '16/11/2010', 'XX', 'XX', 'XX', 'XX', 'XX', 'XX', 'XX']
								];
						
						// create the data store
						var store = new Ext.data.SimpleStore({
						fields: [
						{name: 'docto'},
						{name: 'operacion'},
						{name: 'benef'},
						{name: 'importe'},
						{name: 'divisa'},
						{name: 'banco'},
						{name: 'chequera'},
						{name: 'concepto'},
						{name: 'foliodet'},
						{name: 'foliomov'},
						{name: 'idbanco'},
						{name: 'ref'},
						{name: 'sucursal'},
						{name: 'fvalor'},
						{name: 'origen'},
						{name: 'bancoB'},
						{name: 'idbancoB'},
						{name: 'idchequeraB'},
						{name: 'empresa'},
						{name: 'lEntrada'},
						{name: 'empresaB'}
						]
						});
						
						// load data
						store.loadData(sampleData);
						
						// create the grid
						var grid = new Ext.grid.GridPanel({ //objeto grid se le asignan los datos del store
						store: store, 
						columns: [
						{header: 'No Docto', width: 100, dataIndex: 'docto'},
						{header: 'Fec Operacion', width: 100, dataIndex: 'operacion'},
						{header: 'Beneficiario', width: 100, dataIndex: 'benef'},
						{header: 'Importe', width: 100, dataIndex: 'importe'},
						{header: 'Divisa', width: 100, dataIndex: 'divisa'},
						{header: 'Banco', width: 100, dataIndex: 'banco'},
						{header: 'Chequera', width: 100, dataIndex: 'chequera'},
						{header: 'Concepto', width: 100, dataIndex: 'concepto'},
						{header: 'No Folio Det', width: 100, dataIndex: 'foliodet'},
						{header: 'No Folio Mov', width: 100, dataIndex: 'foliomov'},
						{header: 'Id Banco', width: 100, dataIndex: 'idbanco'},
						{header: 'Referencia', width: 100, dataIndex: 'ref'},
						{header: 'Sucursal', width: 100, dataIndex: 'sucursal'},
						{header: 'Fecha Valor', width: 100, dataIndex: 'fvalor'},
						{header: 'Origen Mov', width: 100, dataIndex: 'origen'},
						{header: 'Banco Benef', width: 100, dataIndex: 'bancoB'},
						{header: 'Id Banco Benef', width: 100, dataIndex: 'idbancoB'},
						{header: 'Id Chquera Benef', width: 100, dataIndex: 'idchequeraB'},
						{header: 'Empresa', width: 100, dataIndex: 'empresa'},
						{header: 'Lote Entrada', width: 100, dataIndex: 'lEntrada'},
						{header: 'Empresa Benef', width: 100, dataIndex: 'empresaB'}
						],
						stripeRows: true,
						height:140,
						width:630,
						x: 10
						});
 				
 				
 				
 		var simple = new Ext.FormPanel( {		
 				
    //title: 'Envio Cheque Ocurre',
    width: 683,
    height: 534,
    padding: 10,
    layout: 'absolute',
    renderTo: 'fielsett',
    items : [
            {
                xtype: 'fieldset',
                title: 'Busqueda',
                width: 660,
                height: 110,
                x: 10,
                y: 10,
                layout: 'absolute',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 70,
                        y: 0
                    },
                    {
                        xtype: 'textfield',
                        x: 140,
                        y: 0,
                        width: 350
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 70,
                        y: 40
                    },
                    {
                        xtype: 'trigger',
                        x: 140,
                        y: 40,
                        width: 190
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Empresa',
                        width: 150,
                        height: 50,
                        x: 340,
                        y: 20,
                        layout: 'absolute',
                        items: [
                            {
                                xtype: 'radio',
                                boxLabel: 'Actual',
                                x: 0,
                                y: -7
                            },
                            {
                                xtype: 'radio',
                                boxLabel: 'Todas',
                                x: 60,
                                y: -7
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 510,
                        y: 40,
                        width: 80
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: '',
                height: 40,
                x: 10,
                y: 140,
                layout: 'absolute',
                width: 230,
                items: [
                    {
                        xtype: 'radio',
                        boxLabel: 'Marcar Todo',
                        x: 0,
                        y: 0
                    },
                    {
                        xtype: 'radio',
                        boxLabel: 'Desmarcar Todo',
                        x: 100,
                        y: 0
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 190,
                width: 660,
                height: 190,
                items: [
                		grid
               		 ]
            },
            {
                xtype: 'label',
                text: 'Total de Registros:',
                x: 290,
                y: 400
            },
            {
                xtype: 'label',
                text: 'Monto Total:',
                x: 470,
                y: 400
            },
            {
                xtype: 'numberfield',
                x: 410,
                y: 400,
                width: 50
            },
            {
                xtype: 'numberfield',
                x: 560,
                y: 400,
                width: 110
            },
            {
                xtype: 'button',
                text: 'Ejecutar',
                x: 410,
                y: 460,
                width: 80
            },
            {
                xtype: 'button',
                text: 'Limpiar',
                x: 500,
                y: 460,
                width: 80
            },
            {
                xtype: 'button',
                text: 'Cerrar',
                x: 590,
                y: 460,
                width: 80
            }
        ]
        });
});

  </script>

<div id="fielsett"></div>