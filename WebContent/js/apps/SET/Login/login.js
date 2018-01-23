
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Login.login');
	var PF = apps.SET.tabID + '.';
	
	//YEC VALIDACION DE CAMPOS
	 var error='';
	 NS.contrasenaValida=false;
	 
	// Funcion para realizar el submit de la forma
	function submit(){	
		login.getForm().submit({ 
			method:'POST', 
			waitTitle:'Conectando', 
			waitMsg:'Verificando datos...',
		 
		    // Función que se ejecuta (exito o fracaso) cuando el servidor responde.
		    // Cual se ejecuta es determinado por la respuesta
		    // proveniente de login.jsp como se muestra abajo. El servidor responde
		    // realmente con un JSON valido, algo como: response.write "{succes: true}"
		    // o response.write "{succes: false, errors: { reason: 'Identificación incorrecta. Intentelo de nuevo.' }}"
		    // dependiendo en la logica contenida en su servidor.
		    // Si tiene exito, se notifica al usuario con un messagebox de alerta,
		    // y cuando se pulsa "OK", eres redirigido a cualquier pagina
		    // que haya elegido.
		    
		    success:function(){ 
		        var redirect = 'principal.jsp';
		        window.location = redirect;
		    },
		 
		    // Función de fallo, see comment above re: éxito y fallo.
		    // Como puede ver aquí, si la identificación falla, lanza
		    // un mensaje al usuario con los detalles del fallo.
		 
		    failure : function(form, action){ 
		    	Ext.Msg.alert('SET', action.result.errors.Error);
		        login.getForm().reset(); 
		    }  
		}); 
	}
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	//Carga el monitor de tesoreria
	function cargaMonitorTesoreria(){
		login.getForm().submit({
			method:'POST',
			waitTitle:'Conectando',
			waitMsg:'Verificando datos...',
			
			success: function() {
				var redirect = 'monitor.jsp?theme=access';
				window.location = redirect;
			},
		    
		    failure: function(form, action) {
		        if(action.failureType == 'server'){
		            Ext.Msg.alert('SET', 'Identificación incorrecta');
		        }
		        else {
		            Ext.Msg.alert('Atención!', 'Fallo de conexión con el servidor de autenticación: ' + action.response.responseText);
		        }
		        login.getForm().reset();
		    }
		});
	};
	
    // Creamos una variable que contenga nuestro EXT FormPanel.
    // Asignamos varias opciones de configuración.
    var login = new Ext.FormPanel({ 
        //labelWidth: 80,
        //url: 'loginTest.jsp', 
        frame: true, 
        title: 'Acceso al sistema de WEBSET', 
        defaultType: 'textfield',
        monitorValid: true,
        // Atributo especifico para los campos usuario/clave.
        // El atributo "name" defiene el nombre de la variable enviada al servidor. 
        items:[{ 
        		style: 'text-transform: uppercase; text-align: center; border-radius: 5px;',
                fieldLabel: 'Usuario',
                name: 'userLogin', 
                id: 'userLogin',
                allowBlank: false
            },{ 
        		style: 'border-radius: 5px; text-align: center;',
                fieldLabel: 'Contraseña', 
                name: 'passLogin', 
                id: 'passLogin', 
                inputType: 'password', 
                allowBlank: false 
            }],
            // Con el listener permite que con al presion del boton enter se relice el submit de la forma
            listeners:{            
				'render': function(c) {
     			 c.getEl().on('keypress', function(ev) {
     			if(ev.keyCode == 13 || ev.which == 13)
     				if(document.getElementsByTagName("input")[0].value!="" && document.getElementsByTagName("input")[1].value!="")
     	 	 		  submit();  
   				 }, c);
				}
             } ,

        // configs for BasicForm
        api: {
            // The server-side method to call for load() requests
            //load: Profile.getBasicInfo,
            // The server-side must mark the submit handler as a 'formHandler'
            submit: SeguridadAction.loginSubmit
        },
        buttons:
        	[{
            	text: 'Monitor',
            	formBind: true,
            	handler: function() {
            		cargaMonitorTesoreria();
            	}
             },
        	 {
	        	text:'Cambiar Contraseña',
	            // Función que se ejecuta cuando el usuario pulsa el boton.
	            handler:function(){
        		 winCambiaPass.show();
        		 win.setVisible(false);
            }
	        },{
	                text:'Ingresar',
	                formBind: true,
	                // Función que se ejecuta cuando el usuario pulsa el boton.
	                handler:function(){
	        		 submit();  		        			
	                } 
	        }
        ] 
    });
    
    NS.user = new Ext.form.Label({
		text: 'Usuario',
		x: 15,
		y: 7
	});
    
    NS.txtUser = new Ext.form.TextField({
    	xtype: 'textfield',
    	style: 'text-transform: uppercase',
		id: PF + 'txtUser',
		x: 130,
		y: 0,
		width: 100,
		allowBlank: false
	});
    
    NS.pass = new Ext.form.Label({
		text: 'Contraseña',
		x: 15,
		y: 30
	});
    
    NS.txtPass = new Ext.form.TextField({
		id: PF + 'txtPass',
		inputType: 'password',
		x: 130,
		y: 25,
		width: 100,
		allowBlank: false
	});
    
    NS.line = new Ext.form.Label({
		text: '----------------------------------------------------------',
		x: 8,
		y: 50
	});
    
    NS.newPass = new Ext.form.Label({
		text: 'Nueva Contraseña',
		x: 15,
		y: 77
	});
    
    NS.txtNewPass = new Ext.form.TextField({
		id: PF + 'txtNewPass',
		inputType: 'password',
		x: 130,
		y: 70,
		width: 100,
		allowBlank: false,
		enableKeyEvents:true,
    	listeners:{
    		specialKey:{
				fn:function(caja, e) {
					if(e.keyCode==Ext.EventObject.TAB || e.keyCode == 13){
						var cadena=caja.getValue();
						NS.validarContrasena(cadena);
						if(error==''){
							Ext.getCmp(PF+'txtConPass').setValue('');
							Ext.getCmp(PF+'txtConPass').focus();
						}else{
							Ext.Msg.alert('SET',error);
							caja.setValue('');
							caja.focus();
						}
						
					}
				}
			},
	 		
			change:{
				fn:function(caja, e) {
					var cadena=caja.getValue();
					NS.validarContrasena(cadena);
					if(error==''){
						Ext.getCmp(PF+'txtConPass').setValue('');
						Ext.getCmp(PF+'txtConPass').focus();
					}else{
						caja.setValue('');
						Ext.Msg.alert('SET',error);
						caja.focus();
						
					}
	 		 }
		}
			
		}
	});
    
    NS.conPass = new Ext.form.Label({
		text: 'Confirmar Contraseña',
		x: 15,
		y: 100
	});
    
    NS.txtConPass = new Ext.form.TextField({
		id: PF + 'txtConPass',
		inputType: 'password',
		x: 130,
		y: 95,
		width: 100,
		allowBlank: false,
		enableKeyEvents:true,
    	listeners:{
			change: {
				fn:function(text){
					 var contrasena1=Ext.getCmp(PF+'txtNewPass').getValue();
					 var contrasena2=text.getValue();
					 if(contrasena1!=contrasena2){
						 Ext.Msg.alert('SET','Las contraseñas no coinciden');
						 text.setValue('');
						 text.focus();
					 }
				}
			},
		    specialKey:{
				fn:function(caja, e) {
					if(e.keyCode==Ext.EventObject.TAB || e.keyCode == 13){
						var contrasena1=Ext.getCmp(PF+'txtNewPass').getValue();
						 var contrasena2=caja.getValue();
						 if(contrasena1!=contrasena2){
							 Ext.Msg.alert('SET','Las contraseñas no coinciden');
							 caja.setValue('');
							 caja.focus();
						 }
					}
				}
			}	
    	}
	});
    
    NS.panelCambioPass = new Ext.form.FieldSet({
		title: '',
		layout: 'absolute',
		width: 275,
		height: 145,
		items:
		[
		 	NS.user,		NS.pass,
		 	NS.newPass,		NS.conPass,
		 	NS.line,
		 	NS.txtUser,		NS.txtPass,
		 	NS.txtNewPass,	NS.txtConPass
		]
	});
    
    NS.panCambioPass = new Ext.FormPanel({ 
        labelWidth: 80, 
        frame: true,
        width: 250,
		height: 200,
        title: 'Cambio de Contraseña', 
        layout: 'absolute',
        items:[NS.panelCambioPass,
               {
			 		xtype: 'button',
			 		text: 'Aceptar',
			 		id: PF + 'aceptar',
			 		x: 50,
			 		y: 160,
			 		width: 80,
			 		height: 22,
			 		listeners: {
			 			click: {
			 				fn: function(e) {
			 					if(NS.contrasenaValida){
					    			SeguridadAction.cambiarPassword(Ext.getCmp(PF + 'txtUser').getValue(), Ext.getCmp(PF + 'txtPass').getValue(),
					    			Ext.getCmp(PF + 'txtNewPass').getValue(), Ext.getCmp(PF + 'txtConPass').getValue(), function(resp, e) {
					    				
					    				if(resp == 'Contraseña modificada correctamente') {
					    					NS.limpiar();
					    					winCambiaPass.setVisible(false);
					    					win.setVisible(true);
					    				}
					    				Ext.Msg.alert('SET', resp + '!!');
					    		
					    			});
			 					}else{
			 						
			 						
			 						Ext.getCmp(PF + 'txtConPass').setValue('');
			 						Ext.Msg.alert('SET', 'Nueva contraseña no válida!!');
			 						Ext.getCmp(PF + 'txtNewPass').focus();
			 						
			 					}
			 				}
			 			}
			 		}
			 	},
			 	{
			 		xtype: 'button',
			 		text: 'Cancelar',
			 		id: PF + 'cancelar',
			 		x: 150,
			 		y: 160,
			 		width: 80,
			 		height: 22,
			 		listeners: {
			 			click: {
			 				fn: function(e) {
			 					NS.limpiar();
			 					winCambiaPass.setVisible(false);
			 					win.setVisible(true);
			 				}
			 			}
			 		}
			 	}
			 ]
    });
    
    NS.limpiar = function() {
    	NS.txtUser.reset();
    	NS.txtPass.reset();
    	NS.txtNewPass.reset();
    	NS.txtConPass.reset();
    };
    
    NS.validarContrasena=function (cadena){
		//Validar contraseña YEC 25-11-2015
		if (cadena.length>=8){
			var banNumero= false;
			var banMayuscula=false;
			var banCaracterEspecial=false;
			var consecutivo=false;
			var verificaSecuencia=true;
			error='';
			
			for (var i = 0; i < cadena.length; i++) {
				var caracter=cadena[i].charCodeAt(0);
				if((caracter<=90 &&  caracter>=65) )
					banMayuscula=true;
				if(caracter<=57 &&  caracter>=48 )
					banNumero=true;
				if( (caracter<=47 &&  caracter>=33 ) || (caracter<=64 &&  caracter>=58 ) || (caracter<=96 &&  caracter>=91 ) || (caracter<=254 &&  caracter>=123 )){
					banCaracterEspecial=true;
					verificaSecuencia=false;
				}
				if(verificaSecuencia && (caracter!=65 ||caracter!=90 ||caracter!=97 ||caracter!=122 ||caracter!=48 ||caracter!=57 )&& i !=0 && consecutivo==false){
					if(caracter == cadena[i-1].charCodeAt(0)){
						consecutivo=true;
					}					
				}	
			}
			if(banNumero==false)
				error='Se debe incluir al menos un número';
			if(banMayuscula==false)
				error='Se debe incluir al menos una mayúscula';
			if(banCaracterEspecial==false)
				error='Se debe incluir al menos un carácter especial o simbolo';
			if(consecutivo==true)
				error='No debe incluir al secuencias';
		}else{
			error='Contraseña demasiado corta';
		}
		if(banNumero==true && banMayuscula==true && banCaracterEspecial==true && consecutivo == false && cadena.length>=8){
			NS.contrasenaValida=true;
		}else{
			NS.contrasenaValida=false;
		}
	}
    
    var winCambiaPass = new Ext.Window({
        layout: 'fit',
        width: 300,
        height: 245,
        closable: false,
        resizable: false,
        plain: true,
        border: false,
        items: [NS.panCambioPass]
	});
    
    
    // Esto solo crea una ventana para envolver el formulario de registro.
    // El objeto login se pasa a la colección de items.
    var win = new Ext.Window({
        layout:'absolute',
        width:320,
        height:150,
        closable: false,
        resizable: false,
	   	draggable:false,
		shadow: true,
        plain: false,
        border: true,
        items: [login]
	});
	win.show();
});

