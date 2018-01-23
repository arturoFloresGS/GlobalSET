/**
 * UTIL FOR BFRAMEWORK
 * @author Armando Rodriguez
 */
(function(){ // INI ANONYMOUS FUNCTION FOR NS CONFIG

var NS = Ext.namespace("BFwrk");

NS.Util = {
	includeFile : function(file, container, opt){

		if(file=='') return;
 
        // GENERATE AN ID FOR THE LOADED FILE TO EVADE TO LOAD MORE THAN ONE TIME
        idfile = file.replace(location.hostname,'');
        idfile = idfile.replace(location.protocol,'');
        idfile = idfile.replace('//"','');
 
        if(document.getElementById(idfile)){ return };
               
        if(typeof opt=='undefined') opt = {};
        if(typeof opt.cache=='undefined') opt.cache = true;
        if(typeof opt.dom=='undefined')  opt.dom = false;
        if(typeof opt.type=='undefined')  opt.type = '';
       
       
        ext = (opt.type!='') ? opt.type : file.substring(file.lastIndexOf('.')+1);
 
        if(!opt.cache){
            var random = new Date().getTime().toString();        
                if(file.indexOf('?')!=-1) file = file+'&'+random;
                else file = file+'?'+random;
        }
       
        if(opt.dom){
    		//alert('container='+container);
    		//alert('document.getElementById(container)='+document.getElementById(container));
    		
            var head = document.getElementById(container);  // .item(0); // 'head'
        }
       
        switch(ext){
                case 'css':
                  if(!opt.dom) 
                        document.write('<link rel="stylesheet" href="'+file+'" id="'+idfile+'" type="text/css"></link>');
                  else{
                    css = document.createElement('link');
                    css.rel  = 'stylesheet';
                    css.href = file;
                        css.type = 'text/css';
                        css.id = idfile;
                        head.appendChild(css);                 
                  }                    
                break;
               
                case 'js':
                 if(!opt.dom){  // HTML
                 		//alert('Inclyendo en write el archivo : ' + file);
                        document.write('<script type="text/javascript" id="'+idfile+'" src="'+file+'"></script>');
                 }
                 else{ // DOM
              		//alert('Inclyendo en dom el archivo : ' + file);
                	 
					try
					{
	                    script = document.createElement('script');
	                    script.src = file;
	                        script.type = 'text/javascript';
	                        script.id = idfile;
	                        head.appendChild(script);
	                        //document.getElementById("contenedor").appendChild(div);

	                       
	                        if(typeof opt.oncomplete!='undefined'){
	                            //Para IE
	                            script.onreadystatechange = function () {if (script.readyState == 'complete') {if(typeof opt.oncomplete == 'function') {eval(opt.oncomplete());}}}
	                            //Para Firefox
	                            script.onload = function () {if(typeof opt.oncomplete == 'function') {opt.oncomplete();}}
	                        }              
	                    	  //Run some code here
	              	}
	              	catch(err){
	              	  //Handle errors here
	              	  alert('Error ['+err+'] cargando el archivo : ' + file);
	              	}                	
                 }
 
                break;
        }
	},

	createDiv : function(id, html, width, height, left, top) {

	   var newdiv = document.createElement('div');
	   newdiv.setAttribute('id', id);
	   
	   if (width) {
	       newdiv.style.width = 300;
	   }
	   
	   if (height) {
	       newdiv.style.height = 300;
	   }
	   
	   if ((left || top) || (left && top)) {
	       newdiv.style.position = "absolute";
	       
	       if (left) {
	           newdiv.style.left = left;
	       }
	       
	       if (top) {
	           newdiv.style.top = top;
	       }
	   }
	   
	   newdiv.style.background = "#00C";
	   newdiv.style.border = "4px solid #000";
	   
	   if (html) {
	       newdiv.innerHTML = html;
	   } else {
	       newdiv.innerHTML = "nothing";
	   }
	   
	   document.body.appendChild(newdiv);
	},	

	existInCombo : function(comboId, fieldName, strData){
		var dataStore = Ext.getCmp(comboId).getStore();
		//alert('dataStore='+dataStore+', strData='+strData);
		return NS.Util.existInDataStore(dataStore, fieldName, strData);
	},
	
	existInDataStore : function(dataStore, fieldName, strData){

		var records = dataStore.data.items;

		//barrido para saber si existe
		for(var j = 0; j< records.length; j++)
			if(records[j].get(fieldName) == strData)
				return j;

		return -1;		
	},
	
	getParams : function(i){
		var qs = document.getElementsByTagName("script")[i].src.match(/\w+=\w+/g), qstring = {},t,i = qs.length;
		while (i--) {
			t = qs[i].split("=");
			qstring[t[0]] = t[1];
		}
		return qstring;
	},
		
    getSessionData : function(){
		/*
        var menu = Util.getPFTop().Menu_ViewHandler;
        return {
            get:function(dnNode){
                return menu.OutDs.get(dnNode);
            },
            set:function(dnNode,data){
                return menu.OutDs.set(dnNode,data);
            }
        }*/
    },
    
   // CUSTOM RENDER FUNCTION
   rendererInteger : function(val){
     if(val > 0){
	 return '<span style="color:blue;align:right;">' + val + '</span>'; 
     }else if(val < 0){
	 return '<span style="color:red;align:right;">' + val + '</span>'; 
     }
     return val;
   },
    
    
   // CUSTOM RENDER FUNCTION
   rendererFloat : function(val){
     if(val > 0){
	 return '<span style="color:black;align:right;">' + Ext.util.Format.number( val, ' ?0,000.00?' )  + '</span>';
     }else if(val < 0){
	 return '<span style="color:red;align:right;">' + Ext.util.Format.number( val, ' ?0,000.00?' )  + '</span>';
     }
     return val;
   },

   // CUSTOM RENDER FUNCTION
   rendererMoney : function(val){ // renderer: BFwrk.Util.rendererMoney
   	 //alert('hola!!');
     if(val > 0){
	 return Ext.util.Format.number( val, ' $?0,000.00?' ) + '</span>';
     }else if(val < 0){
	 return '<span style="color:red;align:right;">' + Ext.util.Format.number( val, ' $?0,000.00?' )  + '</span>';
     }
     return val;
   },

   rendererDecimal : function(val){
	     if(val > 0){
		 return '<span style="color:black;align:right;">' + Ext.util.Format.number( val, ' %?0,000.00000?' )  + '</span>';
	     }else if(val < 0){
		 return '<span style="color:red;align:right;">' + Ext.util.Format.number( val, ' %?0,000.00000?' )  + '</span>';
	     }
	     return val;
   },

   // example of custom renderer function
   rendererPercent : function(val){
     if(val > 0){
	 return '<span style="color:black;" align="center">' + val + '%</span>';
     }else if(val < 0){
	 return '<span style="color:red;" align="center">' + val + '%</span>';
	 }
     return val;
   },
   //by CRGARCIA
   rendererMoneyBlue: function(val){
   	  return '<span style="color:blue;align:right;">' + Ext.util.Format.number( val, ' $?0,000.00?' ) + '</span>';
   }, 
   
	isObjectEmpty : function(ob){
		for(var i in ob){ return false;}
		return true;
	},
	
	enableFromComponents : function(form, enable){
	
		//var formItems = NS.contenedorPrincipal.findById('frmUsuario').getForm().items['items'];
		var formItems = form.items['items'];
		for(i=0; i<formItems.length; i++){
			element = formItems[i];
			if(enable) 
				element.enable();
			else
				element.disable();
		};
	},
	
	enableChildComponents : function(panel, enable){
	
		//alert('enableChildComponents');
		//var componentsArray = panel.findByType('component');
		//alert('componentsArray='+componentsArray);
		
		var formItems = [];
		panel.cascade(function(cmp) {
		  if (cmp.isXType('field')) {
		    formItems.push(cmp);
		    //alert(cmp);
		  }
		});	   

		for(i=0; i<formItems.length; i++){
			element = formItems[i];
			if(enable) 
				element.enable();
			else
				element.disable();
		};
	},
	//by CRGARCIA
	updateTextFieldToCombo : function(textFieldId, comboId){
		var storeItems = Ext.getCmp(comboId).getStore().data.items;
		var valueBox = Ext.getCmp(textFieldId).getValue();
		var cmbValueId = Ext.getCmp(comboId).valueField;
		var cmbDesc = Ext.getCmp(comboId).displayField;
	 	
	 	if(valueBox == '')
	 	return;
	 	 
        for(var i=0;i < storeItems.length;i = i + 1)
        {
			if(storeItems[i].get(cmbValueId)==valueBox)
			{
				Ext.getCmp(comboId).setValue(storeItems[i].get(cmbDesc));
				return storeItems[i].get(cmbValueId);
			}
		}
		Ext.getCmp(comboId).setValue('');
	},
	//by CRGARCIA
	updateComboToTextField : function(textFieldId, comboId){
		Ext.getCmp(textFieldId).setValue(Ext.getCmp(comboId).getValue());
	},
	
	//Formato de un numero a monetario para cajas de texto //by CRGARCIA
	formatNumber : function(num,prefix){
		if(num === null || num === undefined || num === 0)
			return '0.0';
			
		num = num.toString();
		
		if(num.indexOf('.')>-1){
			if(num.substring(num.indexOf('.')).length<3){
				num = num + '0';
			}
		}
		else{
			num = num + '.00';
		}
		prefix = prefix || '';
		var splitStr = num.split('.');
		var splitLeft = splitStr[0];
		var splitRight = splitStr.length > 1 ? '.' + splitStr[1] : '';
		var regx = /(\d+)(\d{3})/;
		while (regx.test(splitLeft)) {
			splitLeft = splitLeft.replace(regx, '$1' + ',' + '$2');
		}
			return prefix + splitLeft + splitRight;
	},
	
	//Quitar formato a las cantidades de las cajas de texto by CRGARCIA
	unformatNumber : function(num) {
		return num.replace(/(,)/g,''); 
	},
	
	//Cambiar Formato de Fecha del dateField by CRGARCIA
	changeDateToString : function(fecha)
	{
		var mesArreglo = new Array(11);
		mesArreglo[0] = "Jan";mesArreglo[1] = "Feb";mesArreglo[2] = "Mar";mesArreglo[3] = "Apr";
		mesArreglo[4] = "May";mesArreglo[5] = "Jun";mesArreglo[6] = "Jul";mesArreglo[7] = "Aug";mesArreglo[8] = "Sep";
		mesArreglo[9] = "Oct";mesArreglo[10] = "Nov";mesArreglo[11] = "Dec";
		var mesDate = fecha.substring(4,7);
		var dia = fecha.substring(8,10);
		var anio = fecha.substring(11,15);
		
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
				var mes=i+1;
				if(mes<10)
				var mes='0'+mes;
			}
		}
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;
	},
	
	/*funcion para obtener un mensaje con barra de espera
	 param sMsg: tiene la leyenda que se desea mostrar
	 param bMostrar: true para mostrar by CRGARCIA
	*/
	msgWait : function(sMsg, bMostrar)
	{
		if(bMostrar)
		{
			Ext.MessageBox.show({
			    title : 'Información SET',
			    msg : ''+sMsg,
			    width : 300,
			    wait : true,
			    progress:true,
			    waitConfig : {interval:200}//,
			    //icon :'lupita'
		        //icon :'forma_alta', //custom class in msg-box.html
		        ///animateTarget : 'mb7'
			});
		}
		else{
			Ext.MessageBox.hide();
		}
	
	},
	//Esta funcion es para mostrar un mensaje al usuario, que son principalmente informativos,
	//que recibe como parametros el mensaje que se motrará y el tipo de mensaje(ERROR, INFO, WARNING) by CRGARCIA
	msgShow : function(sMensaje, sTipoMensaje)
	{
		if(sTipoMensaje === 'ERROR')
		{
			Ext.Msg.show({
				title: 'Información SET',
				msg: sMensaje,
				icon: Ext.MessageBox.ERROR,
				buttons: Ext.MessageBox.OK
			});
		}
		else if(sTipoMensaje === 'INFO')
		{
			Ext.Msg.show({
				title: 'Información SET',
				msg: sMensaje,
				icon: Ext.MessageBox.INFO,
				buttons: Ext.MessageBox.OK
			});
		}
		else if(sTipoMensaje === 'WARNING')
		{
			Ext.Msg.show({
				title: 'Información SET',
				msg: sMensaje,
				icon: Ext.MessageBox.WARNING,
				buttons: Ext.MessageBox.OK
			});
		}
		else if(sTipoMensaje === 'QUESTION')
		{
			Ext.Msg.show({
				title: 'Información SET',
				msg: sMensaje,
				icon: Ext.MessageBox.QUESTION,
				buttons: Ext.MessageBox.OK
			});
		}
	},
	//Este método realiza el barrido sobre un store recibiendo como parametros
	//el nombre de store(storeId), nameFieldCompare(nombre del campo del store), nameFieldtoCompare(nombre del campo obtenido, ej. desc de un combo)
	// namefieldSearch(nombre del campo que se desea retornar, normalmente el id del store) by CRGARCIA
	scanFieldsStore : function(storeId, nameFieldCompare, nameFieldToCompare,nameFieldSearch)
	{
		var records = storeId.data.items;
		for(var i = 0; i < records.length; i = i + 1)
		{
			if(records[i].get(nameFieldCompare) === nameFieldToCompare)
				return records[i].get(nameFieldSearch);
		}
		return "";
	},
	//Este método realiza la suma de fecha, teniendo como parametros la fecha, el valor
	//a sumar('mes', 'dia', 'anio')y la cantidad a sumar(1,2,3, etc...)
	sumDate : function(fechaValor, valorSumar, cantidad)
	{
		var fecha = new Date(fechaValor);
		var fechaRetorno = '';
		var dia = fecha.getDate();
		var mes = fecha.getMonth() + 1;
		var anio = fecha.getFullYear();
		var diasMes = new Date(anio || new Date().getFullYear(), mes, 0).getDate();
		
		if(valorSumar === 'dia') {
			dia = parseInt(dia) + parseInt(cantidad);
			
			if(dia > diasMes) {
				dia = dia - diasMes;
				mes = parseInt(mes) + 1;
			}
		}else if(valorSumar === 'mes') {
			mes = parseInt(mes) + parseInt(cantidad);
		}else if(valorSumar === 'anio') {
			anio = parseInt(anio) + parseInt(cantidad);
		}
		dia = dia + '';
		mes = mes + '';
		anio = anio + '';
		
		if(dia.length === 1) {
			dia = '0' + dia;
		}
		if(mes.length === 1) {
			mes = '0' + mes;
		}
		if(anio.length === 1) {
			anio = '0' + anio;
		}
		
		fechaRetorno = dia + "/" + mes + "/" + anio; 
		
		return fechaRetorno;
	},
	
	sumarDias : function(fechaBase, dias){
		var fechaResultado = new Date();
		
		var tiempoBase = fechaBase.getTime(); 
		
		var diasEnMiliSeg = parseInt(dias)*(1000 * 60 * 60 * 24);
		
		tiempoResultado = tiempoBase + diasEnMiliSeg;
		
		fechaResultado.setTime(tiempoResultado);
		
		return fechaResultado.format('d/m/Y');
	},
	
	//Metodo que resta dos fechas para retornar la diferencia de dias 
	//recibe fecha inicial y fecha final
	//retorna numero entero de dias
	daysBetweenDates : function(fechaIni, fechaFin)
	{
		var fecIni = new Date(fechaIni); 
		var fecFin = new Date(fechaFin); 
		var tiempo = fecFin.getTime() - fecIni.getTime(); 
		dias = Math.floor(tiempo / (1000 * 60 * 60 * 24)); 
		return dias;
	},
	
	//Método para obtener el nombre de un mes, nació para enviar la fecha con nombre 
	//a un reporte, pero puede tener mas utilidades.
	getNameMonth : function(iNumMonth)
	{
		var nameMonth = ''; 
		switch(iNumMonth)
		{
			case 1:
				nameMonth = 'ENERO';
			break;
			case 2:
				nameMonth = 'FEBRERO';
			break;
			case 3:
				nameMonth = 'MARZO';
			break;
			case 4:
				nameMonth = 'ABRIL';
			break;
			case 5:
				nameMonth = 'MAYO';
			break;
			case 6:
				nameMonth = 'JUNIO';
			break;
			case 7:
				nameMonth = 'JULIO';
			break;
			case 8:
				nameMonth = 'AGOSTO';
			break;
			case 9:
				nameMonth = 'SEPTIEMBRE';
			break;
			case 10:
				nameMonth = 'OCTUBRE';
			break;
			case 11:
				nameMonth = 'NOVIEMBRE';
			break;
			case 12:
				nameMonth = 'DICIEMBRE';
			break;
		}
		return nameMonth;
	},
	
	//Metodo que convierte la fecha string (d/m/a) a un formato Date valido para poder comparar con otra fecha
	//por ejemplo la fechaHoy, se obtiene dia, mes y año por separado
	//y se ordena para la nueva fecha que recibe (m/d/a) para convertir a Date
	changeStringToDate : function(fechaString)
	{
		var sDia = fechaString.substring(0,2);
		var sMes = fechaString.substring(3,5);
		var sAnio = fechaString.substring(6,10)
		
		/*var fechaDate = new Date(fechaString);
		var dia = fechaDate.getMonth()+1;
		var mes = fechaDate.getDate();
		var anio = fechaDate.getFullYear();*/
		
		//fechaDate = new Date(mes+'/'+dia+'/'+anio);
		
		var dia = 0;
		var mes = 0;
		var anio = 0;
		dia = sDia;
		mes = sMes;
		anio = sAnio;
		mes = mes - 1;
		fechaDate = new Date (anio, mes, dia, 0,0,0,0);
		return fechaDate;
	},
	
	//Función para obtener el Número de mes apartir de la abreviatura (ej: Ene, Feb, etc.)
	getNumberMonth : function(sMonth)
	{
		var numberMonth = ''; 
		
		if(sMonth.toUpperCase() === 'ENE')
			numberMonth = '01';
		else if(sMonth.toUpperCase() === 'FEB')
			numberMonth = '02';
		else if(sMonth.toUpperCase() === 'MAR')
			numberMonth = '03';
		else if(sMonth.toUpperCase() === 'ABR')
			numberMonth = '04';
		else if(sMonth.toUpperCase() === 'MAY')
			numberMonth = '05';
		else if(sMonth.toUpperCase() === 'JUN')
			numberMonth = '06';
		else if(sMonth.toUpperCase() === 'JUL')
			numberMonth = '07';
		else if(sMonth.toUpperCase() === 'AGO')
			numberMonth = '08';
		else if(sMonth.toUpperCase() === 'SEP')
			numberMonth = '09';
		else if(sMonth.toUpperCase() === 'OCT')
			numberMonth = '10';
		else if(sMonth.toUpperCase() === 'NOV')
			numberMonth = '11';
		else if(sMonth.toUpperCase() === 'DIC')
			numberMonth = '12';
			
		return numberMonth;
	},
	//Esta funcion esta diseñada para recibir de parametros: mes y año, 
	//del cual se requiera saber la cantidad de dias totales que contiene dicho mes ingresado.
	daysInMonth : function(month, year) {
		return new Date(year || new Date().getFullYear(), month, 0).getDate();
	}
};

})() // END ANONYMOUS FUNCTION
