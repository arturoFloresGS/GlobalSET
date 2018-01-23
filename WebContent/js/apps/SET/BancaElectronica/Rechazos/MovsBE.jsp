<link rel="stylesheet" type="text/css" href="/PruebasExtjs/ext-3.3.0/resources/css/ext-all.css" />
<script src="/PruebasExtjs/ext-3.3.0/adapter/ext/ext-base.js"></script>
<script src="/PruebasExtjs/ext-3.3.0/ext-all.js"></script>

<script> 

Ext.onReady(function(){  
 				var simple = new Ext.FormPanel(
 				{
                //title: 'Movs. de Banca Electronica',
                width: 330,
                height: 400,
                padding: 10,
                x: 40,
                y: 10,
                renderTo: 'fielsett',
                layout: 'absolute',
            	items: [ 
    
		            {
		                xtype: 'fieldset',
		                title: 'Empresa',
		                layout: 'absolute',
		                width: 160,
		                height: 50,
		                x: 50,
		                y: 20,
		                items: [
			                    {
			                        xtype: 'radio',
			                        boxLabel: 'Actual',
			                        x: -1,
			                        y: -5
			                    },
			                    {
			                        xtype: 'radio',
			                        boxLabel: 'Todas',
			                        x: 70,
			                        y: -6
			                    }
			                ]
            },
            {
                xtype: 'label',
                text: 'Empresa:',
                width: 50,
                x: 30,
                y: 80
            },
            {
                xtype: 'button',
                text: 'Imprimir',
                x: 40,
                y: 330,
                width: 70
            },
            {
                xtype: 'button',
                text: 'Limpiar',
                x: 120,
                y: 330,
                width: 70
            },
            {
                xtype: 'button',
                text: 'Cerrar',
                x: 200,
                y: 330,
                width: 70
            },
            {
                xtype: 'textfield',
                x: 30,
                y: 100,
                width: 260
            },
            {
                xtype: 'label',
                text: 'Fecha:',
                x: 30,
                y: 140
            },
            {
                xtype: 'datefield',
                x: 30,
                y: 160,
                width: 90
            },
            {
                xtype: 'datefield',
                x: 130,
                y: 160,
                width: 90
            },
            {
                xtype: 'label',
                text: 'Banco:',
                x: 30,
                y: 200
            },
            {
                xtype: 'textfield',
                x: 30,
                y: 220,
                width: 40
            },
            {
                xtype: 'trigger',
                x: 30,
                y: 220,
                width: 260
            },
            {
                xtype: 'label',
                text: 'Chequera:',
                x: 30,
                y: 260
            },
            {
                xtype: 'trigger',
                x: 30,
                y: 280,
                width: 260
            }
       ]
        } );
											
 }); 

  </script>

<div id="fielsett"></div>