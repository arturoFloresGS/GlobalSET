 Ext.onReady(function(){


	var NS = Ext.namespace('apps.SET.Seguridad.AsignarRelaciones.Areas');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	 
	Ext.Direct.addProvider(  Ext.setapp.REMOTING_API  );
	 /*var win = new Ext.Window({  
	     //title: '',  
	     width: 500,  
	     height:350,  
	     //minimizable: true,  
	     //maximizable: true,  
	     html: '<iframe src="http://localhost:8080/SET/charts/dashboard.html" style="width:100%;height:100%;border:none;"></iframe>'  
	 });*/  
   
	 new Ext.Panel({
	     renderTo: NS.tabContId,
	     html: '<iframe src="http://localhost:8080/SET/charts/dashboard.html" style="width:100%;height:100%;border:none;"></iframe>'  
	 }); 
 
	 /*
	 var win2 = new Ext.Window({  
	     title: 'Saldo Historico de Chequeras',  
	     width: 500,  
	     height:350,  
	     //minimizable: true,  
	     //maximizable: true,  
	     html: '<iframe src="http://localhost:8080/SET/charts/dashboard.html" style="width:100%;height:100%;border:none;"></iframe>'  
	 });*/  
	  
	 //win.show();  
	  //win2.show();
});		