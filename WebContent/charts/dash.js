//@autor Armando Rodriguez
 Ext.onReady(function(){

	var NS = Ext.namespace('apps.SET.Seguridad.AsignarRelaciones.Areas');
	// EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	
	var colorsDivId = Ext.DomHelper.append(NS.tabContId, [{
	 	 tag: 'object'
	 	,classid : 'clsid:D27CDB6E-AE6D-11cf-96B8-444553540000'
 		,id : 'dashboard'
 		,width:'200'
 		,height:'200'
 		,codebase:'http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab'
 		,html: '<param name="movie" value="/SET/chart/dashboard.swf" /><param name="quality" value="high" />'+
 		       '<param name="bgcolor" value="#869ca7" /><param name="allowScriptAccess" value="sameDomain" />'+
 		       '<embed src="dashboard.swf" quality="high" bgcolor="#869ca7" width="200" height="200" name="dashboard" align="middle" '+
 		       'play="true" loop="false" quality="high" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" '+
 		       'pluginspage="http://www.adobe.com/go/getflashplayer"></embed>'
	}]);
	

	AC_FL_RunContent(
			"src", "dashboard",
			"width", "200",
			"height", "200",
			"align", "middle",
			"id", "dashboard",
			"quality", "high",
			"bgcolor", "#869ca7",
			"name", "dashboard",
			"allowScriptAccess","sameDomain",
			"type", "application/x-shockwave-flash",
			"pluginspage", "http://www.adobe.com/go/getflashplayer"
	);
});