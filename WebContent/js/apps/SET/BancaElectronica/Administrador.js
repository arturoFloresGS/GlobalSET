Ext.onReady(function(){  
	//@autor Sergio Vaca
	var NS = Ext.namespace('apps.SET.BancaElectronica.Administrador');
    // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
    NS.tabContId = apps.SET.tabContainerId;
    var PF = apps.SET.tabID + '.';
    // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
    
    NS.ban = false;
    NS.pbAutomatico = false;
    NS.lbReferenciaNumerica = false;
    NS.liLongitudReferencia = -1;
    NS.lbReferenciaPorChequera = false;
    NS.lsRutaRegreso ='';
    NS.lsRutaRegresoNAFIN='';
    NS.lsRutaRegresoMASSPAYMENT='';
    NS.GI_ID_USUARIO = apps.SET.iUserId;
    NS.chkMT940 = false;
    NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
    
    NS.storeConfiguraSet = new  Ext.data.DirectStore({
        paramsAsHash: false,
        baseParams: {
            indice: -1
        },
        root: '',
        paramOrder: ['indice'],
        directFn: BancaElectronicaAction.consultarConfiguraSet,
        idProperty: 'valorConfiguraSet',
        fields: [
        	{name: 'id'},
			{name: 'idA'},
			{name: 'resultado'},
			{name: 'valorConfiguraSet'}
        ],
        listeners: {
        	load:{
        		fn:function(){
        		}
        	}
        }
    });
    //store configura set
    NS.storeConfiguraSetTodos = new  Ext.data.DirectStore({
        paramsAsHash: false,
        root: '',
        directFn: BancaElectronicaAction.consultarConfiguraSetTodos,
        idProperty: 'id',
        fields: [
        	{name: 'id'},
			{name: 'idA'},
			{name: 'resultado'},
			{name: 'valorConfiguraSet'}
        ],
        listeners: {
        	load:{
        		fn:function(){
        		}
        	}
        }
    });
    
    //carga de store
    NS.storeConfiguraSetTodos.load();
    
    NS.storeLevantarRefEnc = new Ext.data.DirectStore({
        paramsAsHash: false,
        root: '',
        directFn: BancaElectronicaAction.levantarRefEnc,
        idProperty: 'idBanco',
        fields: [
        	{name: 'idBanco'},
			{name: 'noEmpresa'},
			{name: 'noDigitos'},
			{name: 'campoVerificador'},
			{name: 'baseCalculo'},
			{name: 'numAlfa'},
			{name: 'repara1'},
			{name: 'repara2'}
        ],
        listeners: {
        	load:function(){
        		//carga de store
    			NS.storeLoad.load();		
        	}
        }
    });
    //Carga store
    NS.storeLevantarRefEnc.load();
    
    //store para bancos activos
    NS.storeLoad = new  Ext.data.DirectStore({
        paramsAsHash: false,
        root: '',
        directFn: BancaElectronicaAction.seleccionarBancosActivos,
        idProperty: 'idBanco',
        fields: [
                 {name: 'idBanco'},
                 {name: 'descripcion'}
                 ],
        listeners: {
            load: function(s, records) {
            	NS.pbAutomatico = false;
               	
            	if(records.length > 0) {
            		NS.lsRutaRegreso = retornaValorCS(201);
               		
            		if(NS.lsRutaRegreso == '') {
            			if(!NS.pbAutomatico) {
            				Ext.Msg.alert('SET','No se encontró la ruta para el regreso en configura_set(201)');
        				}
        				return;
               		}
               		NS.lsRutaRegresoNAFIN = retornaValorCS(199);
    				NS.lsRutaRegresoMASSPAYMENT = retornaValorCS(197);
               		var boxes = Ext.getCmp(PF+'boxBancos').getValue();
               		
               		for(var j=0; j<records.length; j++){
	               		switch(records[j].get('idBanco')) {
	               			case 2:		//BANAMEX
	               				Ext.getCmp(PF + 'BANAMEX').setVisible(true);
	               				break;
	               			case 12:	//BANCOMER
	               				Ext.getCmp(PF + 'BANCOMER').setVisible(true);
	               				break;
	               			case 14:	//SANTANDER
	               				Ext.getCmp(PF + 'SANTANDER').setVisible(true);
	               				Ext.getCmp(PF + 'H2HSantander').setVisible(false);
	               				break;
	               			case 21:	//HSBC
	               				Ext.getCmp(PF + 'HSBC').setVisible(true);
	               				break;
	               			case 44:	//INVERLAT
	               				Ext.getCmp(PF + 'INVERLAT').setVisible(true);
	               				break;
	               			case 72:	//BANORTE
	               				Ext.getCmp(PF + 'BANORTE').setVisible(true);
	               				break;
	               			case 135:	//NAFIN
	               				Ext.getCmp(PF + 'NAFIN').setVisible(true);
	               				break;
	               			case 1260:	//COMERICA
	               				Ext.getCmp(PF + 'COMERICA').setVisible(true);
	               				break;
	               			case 1026:	//CITIBANK N A
	               				Ext.getCmp(PF + 'CITIBANK').setVisible(true);
	               				break;
	               		}
	               	}
	               	boxes = Ext.getCmp(PF+'boxBancos').getValue();
	               	
	               	for(i=0; i<boxes.length; i=i+1){
	               		Ext.getCmp(boxes[i].getName()).setValue(false);
	               	}
	               	var datos = NS.storeLevantarRefEnc.data.items;
	               	if(datos.length>0){
				        NS.liLongitudReferencia = datos[0].get('noDigitos');
				        if(datos[0].get('numAlfa') == 'N'){
				            NS.lbReferenciaNumerica = true;
				        }
				        else{
				            NS.lbReferenciaNumerica = false;
				        }
				    }        
				    else{
				        if(!NS.pbAutomatico){
				            Ext.Msg.alert('SET','No se encontró la longitud de la referencia');
				        }
				        NS.liLongitudReferencia = 0;
				    }
				    NS.lbReferenciaPorChequera = false;
			    	if(retornaValorCS(216) == 'SI'){
			        	NS.lbReferenciaPorChequera = true;
			    	}
               	}
            }
        }
    });
    
    NS.desmarcarTodo = function()
    {       	
    	var checks = Ext.getCmp(PF+'boxBancos').getValue();
    	alert(checks.length);
		for(var i=0; i<checks.length; i=i+1)
		{
			if (retornarNombre(checks[i].getName()) != 'MT940')
			{
				Ext.getCmp(checks[i].getName()).setValue(false);
			}
		}
    };
    
	//Combos
    NS.boxBancos = new Ext.form.CheckboxGroup({
	    id:PF+'boxBancos',
	    name:PF+'boxBancos',
	    xtype: 'checkboxgroup',
	    fieldLabel: '',
	    itemCls: 'x-check-group-alt',
	    x: 30,
	    y: 10,
	    columns: 2,
	    items:[ {
	            xtype:'checkbox',
	            boxLabel:'BANAMEX',
	            id:PF+'BANAMEX',
	            name:PF+'BANAMEX',
	            hidden:true
	        },{
                xtype:'checkbox',
                boxLabel:'BANCOMER',
                id:PF+'BANCOMER',
                name:PF+'BANCOMER',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'SANTANDER',
                id:PF+'SANTANDER',
                name:PF+'SANTANDER',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'HSBC',
                id:PF+'HSBC',
                name:PF+'HSBC',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'INVERLAT',
                id:PF+'INVERLAT',
                name:PF+'INVERLAT',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'BANORTE',
                id:PF+'BANORTE',
                name:PF+'BANORTE',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'NAFIN',
                id:PF+'NAFIN',
                name:PF+'NAFIN',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'COMERICA',
                id:PF+'COMERICA',
                name:PF+'COMERICA',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'INBURSA',
                id:PF+'INBURSA',
                name:PF+'INBURSA',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'CITIBANK',
                id:PF+'CITIBANK',
                name:PF+'CITIBANK',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'IXE',
                id:PF+'IXE',
                name:PF+'IXE',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'AMEX',
                id:PF+'AMEX',
                name:PF+'AMEX',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'Check Plus',
                id:PF+'CheckPlus',
                name:PF+'CheckPlus',
                hidden:true
            },{
                xtype:'checkbox',
                boxLabel:'AZTECA',
                id:PF+'AZTECA',
                name:PF+'AZTECA',
                hidden:true
			},{
                xtype:'checkbox',
                boxLabel:'H2H Santander',
                id:PF+'H2HSantander',
                hidden:true
			},{
				xtype: 'checkbox',
				boxLabel: 'MT940',
				id: PF + 'MT940',
				name: PF + 'MT940',
				hidden: true,
				listeners:
				{
					check:
					{
						fn: function()
						{		
							if (Ext.getCmp(PF + 'MT940').getValue())
							{
								NS.chkMT940 = true;
								NS.desmarcarTodo();
							}
							else
							{
								NS.chkMT940 = false;
							}							
						}
					}
				}		
				
			}
	    ]
	});
    
    
	NS.administradorBE = new Ext.FormPanel({
        title: 'Administrador de Archivos de BE',
        width: 400,
        height: 300,
        x:10,
        y:10,
    	frame: true,
    	autoScroll: true,
        renderTo: NS.tabContId,
        layout: 'absolute',
        monitorValid: true,
       	items: [{
      	    xtype: 'fieldset',
            title: 'Procesa Archivos',
            width: 380,
            height: 220,
            x: 10,
            y: 0,
            layout: 'absolute',
            items: [NS.boxBancos,
            {
            	xtype: 'textarea',
            	x:10,
            	y:10,
				width: 340,
				height: 160,
				id:PF+'txtAreaBusca',
				name:PF+'txtAreaBusca',
				readOnly: true,
				hidden: true
			}]
       	},{
	        xtype: 'button',
	        text: 'Ejecutar',
	        x: 240,
	        y: 240,
	        width: 70,
	        handler: function(result) {
	       		if (NS.chkMT940 == true)
	       		{		       			
	       			NS.desmarcarTodo();       			
	
	       			BancaElectronicaAction.lecturaMT940(NS.fechaHoy, function(result, e)
	       			{
	       				if(result == null  &&  result == '' && result != undefined)
	       				{
	       					Ext.Msg.alert('SET', 'No existe información para cargar');
	       				}
	       				else
	       				{
	       					Ext.getCmp(PF+'txtAreaBusca').show();
	       					Ext.getCmp(PF + 'txtAreaBusca').setValue(result);
	       					Ext.Msg.alert('SET', 'información procesada');
	       					
	       					var checks = Ext.getCmp(PF + 'boxBancos').getValue();
	       					for(var i=0; i<checks.length; i=i+1)
	       					{
		        				Ext.getCmp(checks[i].getName()).setValue(false);
	       					}
	       					//Llamada para la confirmacion en automatico
	       		       		BancaElectronicaAction.confirmacionAutomatica(NS.fecHoy, function(resultado, e){
	       		       			if (resultado < 0 && resultado == undefined && resultado == null){
	       		       				alert(resultado + " resultado regresado");
	       		       				Ext.Msg.alert('SET', 'Ocurrio un problema en la confirmación');
	       		       			}
	       		       		});
	       				}
	       			});
	       		}else {
	       			Ext.getCmp(PF + 'MT940').setValue(false);
	       			
	       			NS.administradorBE.getForm().submit({
	       				params: {
	                            idUsuarioF: NS.GI_ID_USUARIO,
	                            referenciaF: NS.lbReferenciaPorChequera,
	                            longitudF: NS.liLongitudReferencia,
	                            clavesF: claves(),
	                            referenciaN: NS.lbReferenciaNumerica
	                        }, 
	                       
	                        method: 'POST',
	                        waitTitle: 'Conectando',
	                        waitMsg: 'Realizando las operaciones...',
	                        success: function(result,ac) {
	                        	if(ac.result.mensajes!==''){                        		
		                        	Ext.getCmp(PF+'txtAreaBusca').show();
		                        	Ext.getCmp(PF+'txtAreaBusca').setValue(ac.result.mensajes+
		                        									ac.result.mensajesUsuario);
			                        var checks = Ext.getCmp(PF+'boxBancos').getValue();
				        			for(var i=0; i<checks.length; i=i+1){
				        				Ext.getCmp(checks[i].getName()).setValue(false);
				        			}
				        			
				        			//Llamada para la confirmacion en automatico
				    	       		BancaElectronicaAction.confirmacionAutomatica(NS.fecHoy, function(resultado, e){
				    	       			if (resultado < 0 && resultado == undefined && resultado == null){
				    	       				alert("4");
				    	       				alert(resultado + " resultado regresado");
				    	       				Ext.Msg.alert('SET', 'Ocurrio un problema en la confirmación');
				    	       			}
				    	       		});
				        		}else{
				        			Ext.Msg.alert('SET','Seleccione un banco');
				        		}
	                        },
	                        failure: function(form, action) {
	                        	alert(failure);
	                            Ext.Msg.alert('SET','¡Ocurrio un error en el proceso!');
	                            var checks = Ext.getCmp(PF+'boxBancos').getValue();
			        			for(var i=0; i<checks.length; i=i+1){
			        				Ext.getCmp(checks[i].getName()).setValue(false);
			        			}
	                        }
	               });
	       		}
	       		/*
	       		//Llamada para la confirmacion en automatico
	       		BancaElectronicaAction.confirmacionAutomatica(NS.fecHoy, function(resultado, e){
	       			if (resultado < 0 && resultado == undefined && resultado == null){
	       				alert(resultado + " resultado regresado");
	       				Ext.Msg.alert('SET', 'Ocurrio un problema en la confirmación');
	       			}
	       		});*/
       		}
       	},{
           xtype: 'button',
           text: 'Limpiar',
           x: 320,
           y: 240,
           width: 70,
           listeners:{
           		click:{
           			fn:function(){
           				var checks = Ext.getCmp(PF+'boxBancos').getValue();
	        			for(var i=0; i<checks.length; i=i+1){
	        				Ext.getCmp(checks[i].getName()).setValue(false);
	        			}
	        			Ext.getCmp(PF+'txtAreaBusca').hide();
	        			Ext.getCmp(PF+'txtAreaBusca').setValue('');
           			}
           		}
           }
       },
       {
    	   xtype: 'button',
    	   text: 'borrar',
    	   x: 0,
    	   y: 240,
    	   width: 50,
    	   hidden: true,
    	   listeners:
    	   {
    	   		click: {
    	   			fn: function()
    	   			{
    	   				BancaElectronicaAction.eliminaArchivos(function(result, e){
    	   					if (result != 0 && result != '' && result != undefined)
    	   						Ext.Msg.alert('SET', result);
    	   				});
    	   			}
       			}
    	   }
       }
       ],
       api: {
            submit: BancaElectronicaAction.leerLayoutProcedimiento
       }
 	});
 	
	
 	function retornaValorCS(num){
 		var configuraSet = NS.storeConfiguraSetTodos.data.items;
 		var valor;
 		for(var i=0; i<configuraSet.length; i=i+1){
 			if(configuraSet[i].get('id')==num){
 				valor=configuraSet[i].get('valorConfiguraSet');
 				break;
 			}
 		}
 		return valor;
 	}
 
 	function claves(){
 		var ids='';
 		var checks = Ext.getCmp(PF+'boxBancos').getValue();
 		var bancos = NS.storeLoad.data.items;
 		for(var i=0; i<checks.length; i=i+1){
 			for(var j=0; j<bancos.length; j=j+1){
 				if(retornarNombre(checks[i].getName())==bancos[j].get('descripcion')){
 					ids=ids+bancos[j].get('idBanco')+',';
 				}
 			}
	    }
	    return ids;
 	}	
 	function retornarNombre(nombre){
 		while(nombre.indexOf('.')>0){
 			if(nombre.substring(nombre.indexOf('.')-1,nombre.indexOf('.')+2)!=='S.A'){
 				nombre = nombre.substring(nombre.indexOf('.')+1);
 			}
 			else{
 				break;
 			}
 		}
 		return nombre;
 	}	
 	
 	NS.administradorBE.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});