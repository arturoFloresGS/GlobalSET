<html>
<head>
   <title>TEST</title>
   <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
  	<%@include file="/header.jsp"%>
</head>

<body>
<script>
/*
Ext.onReady(function(){

	var NS = Ext.namespace('apps.SET.test');
	
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	//NS.tabContId = apps.SET.tabContainerId;
	NS.tabContId = 'contenedorPrincipal';
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);

	new Ext.form.TextField({  
	    name: 'prueba'  
	});  
	
	var MyField = new Ext.form.TextField({  
	    name: 'my_field'  
	    ,flex: 1  
	});  
	
	//Ext.Msg.alert('Hola!', 'hola');
  
	new Ext.form.CompositeField({  
	    fieldLabel: 'My field'
		,renderTo: NS.tabContId
	    ,items: [  
	        MyField  
	        ,{  
	             xtype: 'button'  
	            ,text: 'Buscar'  
	            ,handler: function() {  
	                // Show a menu or selection dialog, then set the user's  
	                // selected value with:
				    var win = new Ext.Window({
				        //layout:'fit',
				        width:300,
				        height:150,
				        closable: false,
				        resizable: false,
				        plain: true,
				        border: false//,
				        //items: [login]
					});
					win.show();
	                
	                  
	                //MyField.setValue(value);  
	            }  
	        }  
	    ]  
	});  
	
});
*/
Ext.onReady(function(){


	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoUsuarios.Test');
	
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	
	Ext.QuickTips.init();
			
	Ext.Direct.addProvider(  Ext.setapp.REMOTING_API  );

		/**llena el combo nombres*/
	NS.directStore = new Ext.data.DirectStore( {
		 paramsAsHash: false
		,root: ''
		,directFn: SegUsuarioAction.obtenerUsuarios
		,idProperty: 'ID'  		//identificador del store
		,fields: [
			 {name: 'ID' }
			,{name: 'PROVEEDOR'}
			,{name: 'ROWNUM'}
		]
		,listeners: {
			load: function(s, records){
				//Ext.MessageBox.alert("Informacion",""+  records.length + " registros cargados.");
			}
		}
	}); 
	// load data se carga al principio el combo
	NS.directStore.load();

    var ds = new Ext.data.Store({
        proxy: new Ext.data.ScriptTagProxy({
            url: 'http://extjs.com/forum/topics-remote.php'
        }),
        reader: new Ext.data.JsonReader({
            root: 'topics',
            totalProperty: 'totalCount',
            id: 'post_id'
        }, [
            {name: 'title', mapping: 'topic_title'},
            {name: 'topicId', mapping: 'topic_id'},
            {name: 'author', mapping: 'author'},
            {name: 'lastPost', mapping: 'post_time', type: 'date', dateFormat: 'timestamp'},
            {name: 'excerpt', mapping: 'post_text'}
        ])
    });

    // Custom rendering Template
    var resultTpl = new Ext.XTemplate(
        '<tpl for="."><div class="search-item">',
            '<h3><span>{lastPost:date("M j, Y")}<br />by {author}</span>{title}</h3>',
            '{excerpt}',
        '</div></tpl>'
    );
    
    var search = new Ext.form.ComboBox({
        store: ds,
        displayField: 'title',
        typeAhead: false,
        loadingText: 'Searching...',
        width:  570,
        pageSize: 10,
        //hideTrigger: true,
        tpl: resultTpl,
        applyTo: 'search',
        itemSelector: 'div.search-item',
        bbar: new Ext.PagingToolbar({
           pageSize: 5,
           store: ds,
           //plugins: filters,
           displayInfo: true
        }),
        onSelect: function(record){ // override default onSelect to do redirect
            //window.location =
                //String.format('http://extjs.com/forum/showthread.php?t={0}&p={1}', record.data.topicId, record.id);
        }
    });

});

//-- The box wrap markup embedded instead of using Element.boxWrap()

document.write('<div style="width:600px;">');
document.write('<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>');
document.write('<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc">');
document.write('<h3 style="margin-bottom:5px;">Buscar</h3>');
document.write('<input type="text" size="40" name="search" id="search" />');
document.write('<div style="padding-top:4px;">');
document.write('Se requieren minimo 4 caractres para la busqueda.');
document.write('</div>');
document.write('</div></div></div>');
document.write('<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>');


</script>

</body>
</html>