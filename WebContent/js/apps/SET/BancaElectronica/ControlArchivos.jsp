<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="/Tutorial/ext-3.3.0/resources/css/ext-all.css" />
<script src="/Tutorial/ext-3.3.0/adapter/ext/ext-base.js"></script>
<script src="/Tutorial/ext-3.3.0/ext-all.js"></script>

<title>Getting Started with Extjs</title>

<script type="text/javascript">
Ext.onReady(function(){

// make sample array data
var dataArchivos= [
['BANAMEX', 'BNX04', '04/09/10', '07/09/10',150600.00,10,'ALBERTO CUESTA','MIGUEL SILVA',2],
['BANCOMER', 'BNC05', '08/09/10', '11/09/10',130600.00,12,'MIGUEL SILVA','MIGUEL SILVA',3],
['HSBC', 'BIT07', '05/09/10', '08/09/10',140800.00,8,'ARMANDO DIAZ','ALBERTO CUESTA',4]

];

var dataDetalle= [
['BANAMEX', 'BNX04', '04/09/10', '07/09/10',150600.00,10,'ALBERTO CUESTA','MIGUEL SILVA',2],
['BANCOMER', 'BNC05', '08/09/10', '11/09/10',130600.00,12,'MIGUEL SILVA','MIGUEL SILVA',3],
['HSBC', 'BIT07', '05/09/10', '08/09/10',140800.00,8,'ARMANDO DIAZ','ALBERTO CUESTA',4]

];

// create the data store
var storeArchivos = new Ext.data.SimpleStore({
fields: [
{name: 'banco'},
{name: 'nombreArchivo'},
{name: 'fecTrans'},
{name: 'fecRetrans'},
{name: 'importe', type: 'double'},
{name: 'registros', type: 'int'},
{name: 'usuarioAlta'},
{name: 'usuarioModifica'},
{name: 'idBanco', type: 'int'}
]
});

var storeDetalle = new Ext.data.SimpleStore({
fields: [
{name: 'noDocto'},
{name: 'estatus'},
{name: 'fecValor'},
{name: 'banco'},
{name: 'chequera'},
{name: 'bancoBenef'},
{name: 'chequeraBenef'},
{name: 'importe', type: 'double'},
{name: 'beneficiario'},
{name: 'plaza'},
{name: 'sucursal'},
{name: 'concepto'},
{name: 'noFolioDet'},
{name: 'noCliente'},
{name: 'entregado'},
{name: 'estatusMov'},
{name: 'noBeneficiario'},
{name: 'noFactura'}
]
});

// load data
storeArchivos.loadData(dataArchivos);


storeDetalle.loadData(dataDetalle);

// create the grid
var gridArchivos = new Ext.grid.GridPanel({
store: storeArchivos,
columns: [
{header: 'Banco', width: 30, sortable: true, dataIndex: 'banco'},
{header: 'Nombre Archivo', width: 100, dataIndex: 'nombreArchivo'},
{header: 'Fecha Trans', width: 110, dataIndex: 'fecTrans'},
{header: 'Fecha Retrans', width: 50, dataIndex: 'fecRetrans'},
{header: 'Importe', width: 100, dataIndex: 'importe'},
{header: 'Registros', width: 100, dataIndex: 'registros'},
{header: 'Usuario Alta', width: 100, dataIndex: 'usuarioAlta'},
{header: 'Usuario Modifica', width: 100, dataIndex: 'usuarioModifica'},
{header: 'Id Banco', width: 100, dataIndex: 'idBanco'}
],
stripeRows: true,
height:100,
width:290,
x:0,
y:0,
title:''
});


var gridDetalle = new Ext.grid.GridPanel({
store: storeDetalle,
columns: [
{header: 'No Docto.', width: 30, sortable: true, dataIndex: 'noDocto'},
{header: 'Estatus', width: 100, dataIndex: 'estatus'},
{header: 'Fecha Valor', width: 110, dataIndex: 'fecValor'},
{header: 'Banco', width: 50, dataIndex: 'banco'},
{header: 'Chequera', width: 100, dataIndex: 'chequera'},
{header: 'Banco Benef', width: 100, dataIndex: 'bancoBenef'},
{header: 'Importe', width: 100, dataIndex: 'importe'},
{header: 'Beneficiario', width: 100, dataIndex: 'beneficiario'},
{header: 'Plaza', width: 100, dataIndex: 'plaza'},
{header: 'Sucursal', width: 100, dataIndex: 'sucursal'},
{header: 'Concepto', width: 100, dataIndex: 'concepto'},
{header: 'No Folio Det', width: 100, dataIndex: 'noFolioDet'},
{header: 'No Cliente', width: 100, dataIndex: 'noCliente'},
{header: 'Entregado', width: 100, dataIndex: 'entregado'},
{header: 'Estatus Mov', width: 100, dataIndex: 'estatusMov'},
{header: 'No Beneficiario', width: 100, dataIndex: 'noBeneficiario'},
{header: 'No Factura', width: 100, dataIndex: 'noFactura'}
],
stripeRows: true,
height:185,
width:735,
x:0,
y:10,
title:''
});



var contenedorPrincipal =new Ext.FormPanel({
title: '',
width: 777,
height: 543,
padding: 10,
layout: 'absolute',
renderTo:'contenedorPrincipal',
items :[
{
xtype: 'fieldset',
title: 'Búsqueda',
x: 10,
y: 0,
width: 760,
height: 170,
layout: 'absolute',
id: 'bntBusqueda',
items: [
{
xtype: 'fieldset',
title: '',
x: 0,
y: 40,
width: 350,
height: 90,
layout: 'absolute',
items: [
{
xtype: 'radio',
fieldLabel: 'Label',
boxLabel: 'Fecha',
anchor: '100%',
x: 0,
y: 0,
id: 'optFecha'
},
{
xtype: 'radio',
fieldLabel: 'Label',
boxLabel: 'Archivo',
anchor: '100%',
x: 80,
y: -2,
id: 'optArchivo'
},
{
xtype: 'radio',
fieldLabel: 'Label',
boxLabel: 'Número de Documento',
anchor: '100%',
x: 160,
y: -1,
id: 'optNumDoc'
},
{
xtype: 'label',
text: 'Valor:',
x: 30,
y: 35
},
{
xtype: 'textfield',
x: 80,
y: 35,
width: 100,
id: 'valorUno'
},
{
xtype: 'textfield',
x: 200,
y: 35,
width: 120,
id: 'valorDos'
}
]
},
{
xtype: 'fieldset',
title: 'Archivos',
x: 360,
y: 0,
width: 310,
height: 130,
id: 'archivos',
items:[
gridArchivos
]
},
{
xtype: 'button',
text: 'Búsqueda',
x: 680,
y: 50
},
{
xtype: 'radio',
x: 90,
y: 0,
boxLabel: 'Todos',
id: 'optTodos'
},
{
xtype: 'radio',
x: 170,
y: 0,
boxLabel: 'Cheque Ocurre',
id: 'optChequeOcurre'
}
]
},
{
xtype: 'fieldset',
title: 'Detalle',
x: 10,
y: 180,
width: 760,
height: 290,
layout: 'absolute',
id: 'detalle',
items: [gridDetalle,
{
xtype: 'textfield',
x: 210,
y: 230,
id: 'txtImporte'
},
{
xtype: 'textfield',
x: 60,
y: 230,
width: 60,
id: 'txtRegistros'
},
{
xtype: 'label',
text: 'Importe:',
x: 150,
y: 230
},
{
xtype: 'label',
text: 'Registros:',
x: 0,
y: 230
}
]
},
{
xtype: 'button',
text: 'Ejecutar',
x: 470,
y: 480,
width: 60,
id: 'btnEjecutar'
},
{
xtype: 'button',
text: 'Limpiar',
x: 550,
y: 480,
width: 60,
id: 'btnLimpiar'
},
{
xtype: 'button',
text: 'Imprimir',
x: 630,
y: 480,
width: 60,
id: 'btnImprimir'
},
{
xtype: 'button',
text: 'Cerrar',
x: 710,
y: 480,
width: 60,
height: 22,
id: 'btnCerrar'
}
]
});



});
</script>
</head>
<body>
<div id="contenedorPrincipal"
	style="height: 30%; width: 500; border: 0px solid blue;"></div>

</body>
</html>

