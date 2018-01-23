 Ext.onReady(function(){
	 var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoUsuarios.Usuarios');
	 
	 // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	 NS.tabContId = apps.SET.tabContainerId;
	 var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	 
	 Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	 
	 Ext.QuickTips.init();
	 NS.pBanModificar = false;
	 NS.idUsuarioM = 0;
	 
	 //YEC VALIDACION DE CAMPOS
	 var error='';
	 NS.correoValido=false;
	 NS.contrasenaValida=false;
	 NS.componenteValidos=false;
	 NS.banderaOpcion=''; //08-12-2015
	 
	 // estatus
	 NS.dataEstatus = [  [ 'A', 'Activo' ]
					   ,[ 'I', 'Inactivo' ] 
					   ,[ 'T', 'Todos' ]
					  ];
	
	// store estatus
	NS.storeEstatus = new Ext.data.SimpleStore( {
			idProperty: 'id'  		//identificador del store
		   ,fields : [ 
						{name :'id'} 
					   ,{name :'descripcion'}
					 ]
		});
		NS.storeEstatus.loadData(NS.dataEstatus);
		
	//combo fisico estatus
	NS.cmbEstatus = new Ext.form.ComboBox({
		 store: NS.storeEstatus 	//llamada al store
		,name: PF+'cmbEstatus'
		,id: PF+'cmbEstatus'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :505
		,y :0
		,width :130
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Estatus'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
					// RECARGAR GRID
					NS.storeUsuarios.baseParams.idUsuario = -1;
					NS.storeUsuarios.baseParams.estatus = combo.getValue();
					NS.storeUsuarios.load();
					Ext.getCmp(PF + 'cmbClaveUsuario').reset();
					Ext.getCmp(PF + 'cmbNomUsuario').reset();
					NS.limpiarFormaUsuario();
					NS.habilitarFormaUsuario(false);
				}
			}
		}
	});
	
	// estatus Nuevo
	NS.dataEstatusNuevo = [  [ 'A', 'Activo' ]
							   ,[ 'I', 'Inactivo' ] 
							  ];
	
	// store estatus
	NS.storeEstatusNuevo = new Ext.data.SimpleStore( {
			idProperty: 'id',  		//identificador del store
			fields : [ 
						{name :'id'} 
					   ,{name :'descripcion'}
					 ]
	});
	NS.storeEstatusNuevo.loadData(NS.dataEstatusNuevo);
	
	//combo fisico estatusNuevo
	NS.cmbEstatusNuevo = new Ext.form.ComboBox({
		fieldLabel : 'Estatus'
		,width: 150
		,store: NS.storeEstatusNuevo 	//llamada al store
		,name: PF+'cmbEstatusNuevo'
		,id: PF+'cmbEstatusNuevo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Estatus'
		,triggerAction: 'all'
		,value: ''
		
	});
	
	NS.storeEmpresa = new Ext.data.DirectStore( {
		 paramsAsHash: false
		,root: ''
		,directFn: SegEmpresaAction.obtenerTodasEmpresas
		,idProperty: 'noEmpresa'  		//identificador del store
		,fields: [
			 {name: 'noEmpresa' }
			,{name: 'nomEmpresa'}
		]
		,listeners: {
			load: function(s, records){
				//Ext.MessageBox.alert("Informacion",""+  records.length + " registros cargados.");
			}
		}
	}); 
	NS.storeEmpresa.load();
	
	//combo fisico Empresa
	NS.cmbEmpresa = new Ext.form.ComboBox({
		fieldLabel : 'Empresa',
		store: NS.storeEmpresa 	//llamada al store
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,width :150
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Empresa'
		,triggerAction: 'all'
		,value: ''
	});
	
	NS.storeCaja = new Ext.data.DirectStore( {
		 paramsAsHash: false
		,root: ''
		,directFn: SegCajaAction.obtenerTodasCajas
		,idProperty: 'idCaja'  		//identificador del store
		,fields: [
			 {name: 'idCaja' }
			,{name: 'descCaja'}
		]
		,listeners: {
			load: function(s, records){
				//Ext.MessageBox.alert("Informacion",""+  records.length + " registros cargados.");
			}
		}
	}); 
	NS.storeCaja.load();
	
	//combo fisico Empresa
	NS.cmbCaja = new Ext.form.ComboBox({
		fieldLabel : 'Caja',
		 store: NS.storeCaja 	//llamada al store
		,id: PF+'cmbCaja'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,width :150
		,valueField:'idCaja'
		,displayField:'descCaja'
		,autocomplete: true
		,emptyText: 'Caja'
		,triggerAction: 'all'
		,value: ''
	});
	
		/**llena el combo nombres*/
	NS.storeNombreUsuario = new Ext.data.DirectStore( {
		 paramsAsHash: false
		,root: ''
		,directFn: SegUsuarioAction.obtenerUsuarios
		,idProperty: 'claveUsuario'  		//identificador del store
		,fields: [
			 {name: 'idUsuario' }
			,{name: 'nombre'}
			,{name: 'claveUsuario'}
			,{name: 'perfil'}
		]
		,listeners: {
			load: function(s, records){
				//Ext.MessageBox.alert("Informacion",""+  records.length + " registros cargados.");
			}
		}
	}); 
	// load data se carga al principio el combo
	NS.storeNombreUsuario.load();
	
	//combo fisico nombres
	NS.cmbNomUsuario = new Ext.form.ComboBox({
		 store: NS.storeNombreUsuario 	//llamada al store
		,name: PF+'cmbNomUsuario'
		,id: PF+'cmbNomUsuario'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :300
		,y :0
		,width :130
		,valueField:'idUsuario'
		,displayField:'nombre'
		,autocomplete: true
		,emptyText: 'Usuario'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
						
						NS.cmbClaveUsuario.setValue(combo.getValue());	
						    // RECARGAR GRID
							NS.storeUsuarios.baseParams.idUsuario = combo.getValue();
							NS.storeUsuarios.baseParams.estatus = '';
							
							NS.storeUsuarios.load();
							
							Ext.getCmp(PF + 'cmbEstatus').reset();
							NS.limpiarFormaUsuario();
							NS.habilitarFormaUsuario(false);
				}
			}	
		}
	});
	
	//combo fisico claves
	NS.cmbClaveUsuario = new Ext.form.ComboBox({
		 store: NS.storeNombreUsuario 	//llamada al store
		,name: PF+'cmbClaveUsuario'
		,id: PF+'cmbClaveUsuario'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :95
		,y :0
		,width :130
		,valueField:'idUsuario'
		,displayField:'claveUsuario'
		,autocomplete: true
		,emptyText: 'Clave Usuario'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
					
					NS.cmbNomUsuario.setValue(combo.getValue());		
					// RECARGAR GRID
					NS.storeUsuarios.baseParams.idUsuario = combo.getValue();
					NS.storeUsuarios.baseParams.estatus = '';
					
					NS.storeUsuarios.load();
					
					Ext.getCmp(PF + 'cmbEstatus').reset();
					NS.limpiarFormaUsuario();				
					NS.habilitarFormaUsuario(false);
				}
			}
		}
	});
	
	NS.limpiarGridPerfiles = function(){
		NS.perfilesUsuarioStore.baseParams.idUsuario = 0;
		NS.perfilesUsuarioStore.load();
	}


	NS.limpiarFormaUsuario = function(){
		NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().reset();
		NS.limpiarGridPerfiles();
	}
	NS.habilitarFormaUsuario = function(habilitar) {
		BFwrk.Util.enableChildComponents(NS.contenedorPrincipal.findById(PF+'frmUsuario'), habilitar);
		
		if(habilitar)
			NS.gridPerfilesUsuario.enable();
		else
			NS.gridPerfilesUsuario.disable();
	}
	NS.mostrarUsuarioEnForma = function(){
			// OBTENER LOS REGISTROS SELECCIONADOS
			var records = NS.gridUsuarios.getSelectionModel().getSelections();

			// SI HAY REGISTROS SELECCIONADOS, UTILIZAR EL PRIMERO (UNICO) PARA MOSTRARLO EN LAFORMA			
			if(records.length>0){
				NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().reset();
				NS.idUsuarioM = records[0].get('idUsuario');
				var claveuser = records[0].get('cveUsuario');
				var nomuser = records[0].get('nombreU');
				var apuser = records[0].get('paterno');
				var amuser = records[0].get('materno');
				var pswuser = records[0].get('psw');
				var estatususer = records[0].get('estatus');
				var mailuser = records[0].get('mail');
				var fecuser = records[0].get('vencimiento');
				var descCaja = records[0].get('descCaja');
				var nomEmpresa = records[0].get('nomEmpresa');
				var intentos= records[0].get('intentos'); //YEC
				Ext.getCmp(PF+'clave').setValue(claveuser);
				Ext.getCmp(PF+'nombre').setValue(nomuser);
				Ext.getCmp(PF+'apPaterno').setValue(apuser);
				Ext.getCmp(PF+'apMaterno').setValue(amuser);
//				Ext.getCmp(PF+'contrasena').setValue(pswuser);
				Ext.getCmp(PF+'contrasena').setValue('');
				Ext.getCmp(PF+'cmbEstatusNuevo').setValue(estatususer);
				Ext.getCmp(PF+'correoElectronico').setValue(mailuser);
				Ext.getCmp(PF+'fecVencimiento').setValue(fecuser);
				Ext.getCmp(PF+'cmbEmpresa').setValue(nomEmpresa);
				Ext.getCmp(PF+'cmbCaja').setValue(descCaja);
				if(intentos==0){ //YEC
					Ext.getCmp(PF+'chkBloqueo').setValue(false);
				}else{
					Ext.getCmp(PF+'chkBloqueo').setValue(true);
				}
				
				//fin YEC
				// CARGAR LOS DATOS DE LOS PERFIILES DEL SERVIDOR
				NS.perfilesUsuarioStore.baseParams.idUsuario = NS.idUsuarioM;
				NS.perfilesUsuarioStore.load();
				
				return true;
			}else{
				return false;
			}
	};

	NS.actualizarFormaEnRegistro = function(){
		// OBTENER LOS REGISTROS SELECCIONADOS
		var records = NS.gridUsuarios.getSelectionModel().getSelections();
	
		// SI HAY REGISTROS SELECCIONADOS, UTILIZAR EL PRIMERO (UNICO) PARA MOSTRARLO EN LAFORMA			
		if(records.length>0){
			//NS.idUsuarioM = records[0].get('idUsuario');
			
			var claveuser = Ext.getCmp(PF+'clave').getValue();
			var nomuser = Ext.getCmp(PF+'nombre').getValue();
			var apuser = Ext.getCmp(PF+'apPaterno').getValue();
			var amuser = Ext.getCmp(PF+'apMaterno').getValue();
			var pswuser = Ext.getCmp(PF+'contrasena').getValue();
			var estatususer = Ext.getCmp(PF+'cmbEstatusNuevo').getValue();
			var mailuser = Ext.getCmp(PF+'correoElectronico').getValue();
			var fecuser = Ext.getCmp(PF+'fecVencimiento').getValue();
			
			var intentos = Ext.getCmp(PF+'chkBloqueo').getValue(); //YEC 23/11/2015
			if(intentos==true){ 
				records[0].set('intentos',3); 
			}else{  
				records[0].set('intentos',0);  
			}  // FIN YEC 23/11/2015
	
			records[0].set('cveUsuario', claveuser);
			records[0].set('nombreU', nomuser);
			records[0].set('paterno', apuser);
			records[0].set('materno', amuser);
			records[0].set('psw', pswuser);
			records[0].set('estatus', estatususer);
			records[0].set('mail', mailuser);
			records[0].set('vencimiento', fecuser);
			return true;
		}else{
			return false;
		}
	};

	
	NS.campos = [
			 {name: 'idUsuario', mapping : 'idUsuario' }
			,{name: 'cveUsuario', mapping : 'cveUsuario' }
			,{name: 'nombreU', mapping: 'nombreU'}
			,{name: 'paterno', mapping: 'paterno'}
			,{name: 'materno', mapping: 'materno'}
			,{name: 'psw', mapping: 'psw'}
			,{name: 'estatus', mapping: 'estatus'}
			,{name: 'intentos', mapping: 'intentos'}
			,{name: 'mail', mapping: 'mail'}
			,{name: 'acceso', mapping: 'acceso'}
			,{name: 'vencimiento', mapping: 'vencimiento'}
			,{name: 'descCaja', mapping: 'nomCaja'}
			,{name: 'nomEmpresa', mapping: 'nomEmpresa'}
			];
			
			//store del grid
	NS.storeUsuarios = new Ext.data.DirectStore( {
		 paramsAsHash: false
		,root: ''
		,paramOrder: ['idUsuario', 'estatus']
		,directFn: SegUsuarioAction.obtenerUsuariosGrid
		,idProperty: 'idUsuario'
		,fields: NS.campos
		,listeners: {
			load: function(s, records){
			}
		}
	});
	
	NS.ocultaPassword = function() {
   			return '**********';
	};
			
	NS.validarContrasena=function (cadena){
		//Validar contraseña YEC 23-11-2015
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
				error='No debe incluir secuencias';
		}else{
			error='Contraseña demasiado corta';
		}
		if(banNumero==true && banMayuscula==true && banCaracterEspecial==true && consecutivo == false && cadena.length>=8){
			NS.contrasenaValida=true;
		}else{
			NS.contrasenaValida=false;
		}
	}
	NS.validarCorreo= function (cadena){
		var banArroba=false;
		var banPunto=false
		for (var i = 0; i < cadena.length; i++) {
			if(cadena[i].charCodeAt(0)==64){
				banArroba=true;
			}
			if(cadena[i].charCodeAt(0)==46){
				banPunto=true;
			}
		}
		if(banPunto==true && banArroba==true){
			NS.correoValido=true;
		}else{
			NS.correoValido=false;
		}
	
			
	}
	
	NS.validarTexto=function(cadena){
		var banNumero= true;
		var banCaracterEspecial=true;
		var cadenaAux='';
		for (var i = 0; i < cadena.length; i++) {
			var caracter=cadena[i].charCodeAt(0);
			if(caracter<=57 &&  caracter>=48 )
				banNumero=false;
			if(  (caracter<=47 &&  caracter>=32 ) || (caracter<=64 &&  caracter>=58 ) || (caracter<=96 &&  caracter>=91 ) || (caracter<=254 &&  caracter>=123 )){
				//PARA PERMITIR ciertos caracteres : espacio en blanco ,ñ y Ñ
				banCaracterEspecial=false;
				switch (caracter) {
				case 241:
					banCaracterEspecial=true;
					break;
				case 32:
					banCaracterEspecial=true;
					break;
				case 209:
					banCaracterEspecial=true;
					break;
				}
				
			}
			if(banNumero && banCaracterEspecial)
				cadenaAux=cadenaAux+cadena[i].charAt(0);
		}
		return cadenaAux;
	}
	
	//var contrasenaA =Ext.getCmp(PF+'contrasena').getValue();
	//var contrasenaB =Ext.getCmp(PF+'contrasena2').getValue();
	NS.validarComponentes=function(){
		error='';
		var clave =Ext.getCmp(PF+'clave').getValue();
		var nombre =Ext.getCmp(PF+'nombre').getValue();
		var app =Ext.getCmp(PF+'apPaterno').getValue();
		var apm =Ext.getCmp(PF+'apMaterno').getValue();
		var correo =Ext.getCmp(PF+'correoElectronico').getValue();
		
		if(clave!='' && nombre != '' && app != '' && apm != ''){
			
				NS.validarCorreo(correo);
				if(NS.correoValido==true){
					 NS.componenteValidos=true;
				}else{
					error='Correo no valido.';
					 NS.componenteValidos=false;
				}
				if(NS.banderaOpcion == 'nuevo') {
					var contrasenaA =Ext.getCmp(PF+'contrasena').getValue();
					var contrasenaB =Ext.getCmp(PF+'contrasena2').getValue();
					if(contrasenaA!= '' && contrasenaB!=''){
						if(contrasenaA==contrasenaB &&  NS.componenteValidos==true){
							NS.componenteValidos=true;
						}else{
							error='Las contraseñas no coinciden.';
							NS.componenteValidos=false;
						}
					}else{
						error='Campos requeridos en blanco.';
						NS.componenteValidos=false;
					}
				}
			
		}else{
			error= 'Campos requeridos en blanco.';
			 NS.componenteValidos=false;
		}
	}
	// create the grid
	NS.gridUsuarios = new Ext.grid.GridPanel( {
		ddGroup: 'gridDDGroup'
		,store : NS.storeUsuarios
		,stripeRows :true
		,autoScroll: true
		,height :420
		,x :0
		,y :0
		,title :''
        ,listeners:{
        	click:{
        		fn:function(e){
        			
        			if(NS.banderaOpcion!='nuevo'){
						Ext.getCmp(PF+'frameDetalle').setTitle('Detalle');
						Ext.getCmp(PF+'aceptar').setDisabled(true);
						NS.habilitarFormaUsuario(false);
	        		 	NS.mostrarUsuarioEnForma();
	        		 	NS.pBanModificar = false;
        			}
        		}
        	}
        }
		,tbar:[
		       {
		    	   text: 'Nuevo',
		    	   iconCls:'forma_alta',
		    	   handler: function() {
		    		   NS.banderaOpcion='nuevo';
		    	   		Ext.getCmp(PF+'frameDetalle').setTitle('Nuevo');
						Ext.getCmp(PF+'clave').setReadOnly(false);
						Ext.getCmp(PF+'aceptar').setDisabled(false);
						NS.limpiarFormaUsuario();
						NS.habilitarFormaUsuario(true);
						NS.pBanModificar = false;
						NS.idUsuarioM = 0;
						Ext.getCmp(PF+ 'chkBloqueo').setVisible(false);
					}
				},' | ',{
					text:'Modificar',
					iconCls:'forma_cambio',
					handler: function() {	
						if(!NS.mostrarUsuarioEnForma()) {
							Ext.Msg.alert('SET','Seleccione un Registro');
							return;
						}
								NS.banderaOpcion='modificar';
								Ext.getCmp(PF+'frameDetalle').setTitle('Modificar');
								Ext.getCmp(PF+'clave').setReadOnly(true);
								Ext.getCmp(PF+'clave').setDisabled(true); //YEC 
								Ext.getCmp(PF+'aceptar').setDisabled(false);
								Ext.getCmp(PF+ 'chkBloqueo').show();
								NS.pBanModificar = true;
								NS.habilitarFormaUsuario(true);
								
								
	
							}
						}
						,' | '
						,{
							text: 'Eliminar',
							iconCls:'forma_baja',
							handler: function() {
								NS.banderaOpcion='';
							     Ext.Msg.confirm('SET','¿Esta seguro de eliminar?',function(btn){  
							         if(btn === 'yes'){  
								var records = NS.gridUsuarios.getSelectionModel().getSelections();
								if(records.length>0){
									var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Eliminando..."});
									myMask.show();
									Ext.getCmp(PF+'clave').setReadOnly(false);
									//Ext.getCmp(PF+'idPerfil').setReadOnly(false);
									NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().reset();
									var i=0;
									
									for(i=0; i<records.length; i=i+1){
										var record = records[i];
										var banEliminar = false;
										var varId = records[i].get('idUsuario');
										SegUsuarioAction.eliminar(varId, function(result, e){
											if(!result.resultado){
												banEliminar = false;
												Ext.Msg.alert('SET','No se puede eliminar el registro por referencia');
												myMask.hide();
												}
											else{
												banEliminar = true;
												}
											if(banEliminar){
												var recordsFor = NS.gridUsuarios.getSelectionModel().getSelections();
												var pIdUsuarioA = result.id;
												var datoStore = NS.storeNombreUsuario.data.items;
												
												for(var j=0; j<recordsFor.length; j=j+1){
													if(recordsFor[j].get('idUsuario')==pIdUsuarioA){
														NS.gridUsuarios.store.remove(recordsFor[j]);
														for(var h=0; h<datoStore.length; h=h+1){
															if(datoStore[h].get('id_usuario')==pIdUsuarioA)
																NS.storeNombreUsuario.remove(datoStore[h]);
															}
														NS.gridUsuarios.store.sort('idUsuario', 'ASC');
														Ext.Msg.alert('SET','Registro Eliminado');
														NS.gridUsuarios.getView().refresh();
														myMask.hide();
														}
													}
												}
											});
										}
									NS.gridUsuarios.getView().refresh();
									NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().reset();
									}
								else{
									Ext.Msg.alert('SET','Seleccione un Registro');
									}
									 }
									
							     });
								}
							}, '->'],
		columns : [ 
		{
			id :'idUsuario',
			header :'Usuario',
			width :60,
			sortable :true,
			dataIndex :'idUsuario'
		},
		{
			header :'Cve Usuario',
			width :80,
			sortable :true,
			dataIndex :'cveUsuario'
		},
		{
			header :'Nombre',
			width :100,
			sortable :true,
			dataIndex :'nombreU'
		}, {
			
			header :'Ap Paterno',
			width :80,
			sortable :true,
			dataIndex :'paterno'
		}, {
			
			header :'Ap Materno',
			width :80,
			sortable :true,
			dataIndex :'materno'
		}
		,{
			header :'Contrasena',
			width :80,
			sortable :true,
			dataIndex :'psw',
			renderer: NS.ocultaPassword
		}
		,{
			header :'Estatus'
			,width :55
			,sortable :true
			,dataIndex :'estatus'
		}
		,{
			header :'Intentos'
			,width :65
			,sortable :true
			,dataIndex :'intentos'
		}
		,{
			header :'E mail'
			,width :150
			,sortable :true
			,dataIndex :'mail'
		} 
		,{
			header :'F Acceso'
			,width :80
			,sortable :true
			,dataIndex :'acceso'
		} 
		,{
			header :'F Vencimiento'
			,width :80
			,sortable :true
			,dataIndex :'vencimiento'
		}
		,{
			header :'Empresa'
			,width :150
			,sortable :true
			,dataIndex :'nomEmpresa'
		}
		,{
			header :'Caja'
			,width :80
			,sortable :true
			,dataIndex :'descCaja'
		}
	  ]
	});
	
	
	//************ GRID DE PERFILES DEL USUARIO ************
	
		//Store para llenar el grid que muestra la consulta de movimientos
  	 NS.perfilesUsuarioStore = new Ext.data.DirectStore({
  	 	paramsAsHash: false
  	 	,baseParams: {
			idUsuario: '1'
		}	
		,root: ''
		,paramOrder:['idUsuario']
		,directFn: SegUsuarioPerfilAction.obtenerPerfilesUsuario
		,idProperty: 'ID_PERFIL'  		//identificador del store
		,fields: [
			 {name: 'ID_PERFIL' }
			,{name: 'SELECCIONADO' }
			,{name: 'DESCRIPCION'}
			,{name: 'ESTATUS'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeImportaBancaGrid, msg:"Cargando..."});
				//alert('GridPerfiles.load');
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información SET','No existen Datos');
					return;
				}else {
					for(var i=0; i<records.length; i++) {
						if(records[i].get('SELECCIONADO') == 1) {
							NS.sm.selectRange(0, i);
							return;
						}
					}
				}
			}
		}
	}); 
	
	//check
	/*NS.sm = new Ext.grid.SmartCheckboxSelectionModel({
		dataIndex :'SELECCIONADO'
//		,alwaysSelectOnCheck: true
		,singleSelect: true
	});*/
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		dataIndex :'SELECCIONADO',
		alwaysSelectOnCheck: true,
		singleSelect: true
	});
	
	//grid de datos
	 NS.gridPerfilesUsuario = new Ext.grid.GridPanel({
	 	title: 'Perfiles',
        columnLines: true,
        //width: 250,
        height:320,
        autoScroll: true,
        //frame:true,
        store : NS.perfilesUsuarioStore,
        sm: NS.sm,
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
               NS.sm
               ,{
					header :'Id',
					width :30,
					sortable :true,
					dataIndex :'ID_PERFIL'
				},{
					header :'Perfil',
					width :200,
					sortable :true,
					dataIndex :'DESCRIPCION'
				},{
					
					header :'Estatus',
					width :60,
					sortable :true,
					dataIndex :'ESTATUS'
				}
            ]
        })
    });

	//********  FIN DE  GRID DE PEFILES ************
	 
	 NS.contenedorPrincipal =new Ext.Panel( {
		 //width: 1100,
		 height: 595,
		 frame: true,
		//autoScroll: true,
		autowidth:true,
		 title: 'Mantenimiento de Usuarios',
		 layout: 'absolute',
		 renderTo: NS.tabContId,
		 monitorValid: true,
		 html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		 items: [
		         {
		        	 xtype: 'fieldset'
	                ,title: 'Búsqueda'
	            	,width: 760 // quitar para maximizar al 100%
	                ,height: 60
	                ,x: 10
	                ,y: 4
	                ,layout: 'absolute'
	                ,id: 'frameBusqueda'
	                ,items: [   
	                      
	                    	 NS.cmbClaveUsuario
	                   		,NS.cmbNomUsuario
	                    	,NS.cmbEstatus
	                   , {
	                        xtype: 'label'
	                        ,text: 'Clave Usuario:'
	                        ,x: 0
	                        ,y: 0
	                    }
	                    ,{
	                        xtype: 'label'
	                        ,text: 'Nombre:'
	                        ,x: 245
	                        ,y: 0
	                    }
	                    ,{
	                        xtype: 'label'
	                        ,text: 'Estatus:'
	                        ,x: 450
	                        ,y: 0
	                    }
	                ]
	            },{ //Finaliza Contenedor de Busqueda
	            	//NS.contenedorPrincipal =new Ext.FormPanel({
	            	xtype: 'form'
	                ,title: ''
	                ,x: 10
	                ,y: 70
	                //,autoScroll: true
	            	//,width: 760 // quitar para maximizar al 100%
	                ,height: 420
	                ,layout: 'absolute'
	                ,id: PF+'frmUsuario'
	                ,name: PF+'frmUsuario'
			     	,api: {submit: SegUsuarioAction.insertarModificar} // LLAMADA A JAVA
	                ,items: [
	                       {
			                xtype: 'fieldset'
			                ,title: 'Detalle'
			                ,x: 620
			                ,y: 0
			                ,width: 400
			                ,height: 420
			                ,layout: 'absolute'
			                ,id: PF+'frameDetalle'
			                ,name: PF+'frameDetalle'
			 
			               ,autoScroll: true
			                ,buttons:[
								{ 
									text:'Aceptar' 
									,name: PF+'aceptar'
									,id: PF+'aceptar'
									//,x: 100
			                        //,y: 400
			                        ,disabled:true
									,formBind: true 
									// Función que se ejecuta cuando el usuario pulsa el botón. 
									,handler:function(){ 
										var listaPerfiles = new Array();
										NS.validarComponentes(); //YEC
										if(NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().isValid()&& NS.componenteValidos==true){ //YEC 
											// costruir lista de perfiles para el parametro del submit
											var records = NS.gridPerfilesUsuario.getSelectionModel().getSelections();
											if (records.length > 0) {
												if (NS.cmbEstatusNuevo.getValue() != '') {
													if (NS.cmbEmpresa.getValue() != '') {
														if (NS.cmbCaja.getValue() != '') {
															listaPerfiles[0] = new Object();
															listaPerfiles[0].idPerfil = records[0].get('ID_PERFIL');
															
															var jsonListaPerfiles = Ext.util.JSON.encode(listaPerfiles);
															
															NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().submit({ 
																params: {
																	idUsuarioF: NS.idUsuarioM
																	,banderaF: NS.pBanModificar
																	,prefijo: PF
																	,listaPerfiles : jsonListaPerfiles 
																	
																}
																,method: 'POST' 
																,waitTitle: 'Conectando' 
																,waitMsg: 'Verificando datos...' 
																,success: function(){
																	var records = NS.storeUsuarios.data.items; 
																	if(!NS.pBanModificar)		//insertar
																	{
																		
																		if(Ext.getCmp(PF+'contrasena').getValue() == Ext.getCmp(PF+'contrasena2').getValue()){ //YEC
																			
																			var aux=0;
																			if(records.length>0){
																				for(var i = 0; i<records.length; i = i + 1){
																					if(aux<records[i].get('idUsuario'))
																						aux = records[i].get('idUsuario');
																				}
																				aux = aux+1;
																				var userClase = NS.gridUsuarios.getStore().recordType;
																            	var userDato = new userClase({
																	                idUsuario: aux
																					,cveUsuario: Ext.getCmp(PF+'clave').getValue()
																					,nombreU: Ext.getCmp(PF+'nombre').getValue()
																					,paterno: Ext.getCmp(PF+'apPaterno').getValue()
																					,materno: Ext.getCmp(PF+'apMaterno').getValue()
																					,psw: Ext.getCmp(PF+'contrasena').getValue()
																					,estatus: Ext.getCmp(PF+'cmbEstatusNuevo').getValue()
																					,mail: Ext.getCmp(PF+'correoElectronico').getValue()
																					,vencimiento: Ext.getCmp(PF+'fecVencimiento').getValue()
																					,noEmpresa: Ext.getCmp(PF+'cmbEmpresa').getValue()
																					,idCaja: Ext.getCmp(PF+'cmbCaja').getValue()
																					
																					//,noperfil: Ext.getCmp(PF+'idPerfil').getValue()
																					//,descripcionPerfil: Ext.getCmp(PF+'cmbPerfil').getValue()
																	            });
																	            NS.gridUsuarios.stopEditing();
																	            NS.storeNombreUsuario.insert(0, userDato);
																            	NS.storeNombreUsuario.sort('nombreU', 'ASC');
																            	NS.storeUsuarios.insert(0, userDato);
																            	NS.gridUsuarios.store.sort('idUsuario', 'ASC');
																            	NS.gridUsuarios.getView().refresh();
																            }
																            else{
																	            NS.storeNombreUsuario.load();
																				NS.storeNombreUsuario.sort('idUsuario','ASC');
																			}
																		
																            Ext.Msg.alert('SET','Registro Insertado');
																            NS.banderaOpcion='';
																		
																	  }else{ // MENSAJE ERROR
																		  Ext.Msg.alert('SET','Las contraseñas no coinciden'); //MSG CONTRA
																	  } // FIN COMPARACION DE CONTRASEÑAS YEC
																}else			//modificar
																	{
																		
																		NS.actualizarFormaEnRegistro();
																		
																		/*
																		for(var i = 0; i<records.length; i = i + 1)
																			if(NS.idUsuarioM==records[i].get('idUsuario')){
																				var record = i;
																			}
																		
																		var index;
																		var datosUsuario =NS.storeNombreUsuario.data.items;
																		for(var i=0; i<datosUsuario.length; i=i+1){
																			if(datosUsuario[i].get('idUsuario')==records[record].get('idUsuario'))
																				index=i;
																		}
																		alert('aceptar 5');	
																		NS.storeUsuarios.remove(records[record]);
																		NS.storeNombreUsuario.remove(datosUsuario[index]);
																		var userClase = NS.gridUsuarios.getStore().recordType;
																		var userDato = new userClase({
																			idUsuario: NS.idUsuarioM
																			,cveUsuario: Ext.getCmp(PF+'clave').getValue()
																			,nombreU: Ext.getCmp(PF+'nombre').getValue()
																			,paterno: Ext.getCmp(PF+'apPaterno').getValue()
																			,materno: Ext.getCmp(PF+'apMaterno').getValue()
																			,psw: Ext.getCmp(PF+'contrasena').getValue()
																			,estatus: Ext.getCmp(PF+'cmbEstatusNuevo').getValue()
																			,mail: Ext.getCmp(PF+'correoElectronico').getValue()
																			,vencimiento: Ext.getCmp(PF+'fecVencimiento').getValue()
																			//,noperfil: Ext.getCmp(PF+'idPerfil').getValue()
																			//,descripcionPerfil: Ext.getCmp(PF+'cmbPerfil').getValue()
																        });
																        NS.gridUsuarios.stopEditing();
																        NS.storeNombreUsuario.insert(0, userDato);
															            NS.storeNombreUsuario.sort('nombreU', 'ASC');
															            NS.storeUsuarios.insert(0, userDato);
															            NS.gridUsuarios.store.sort('idUsuario', 'ASC');
															            NS.gridUsuarios.getView().refresh();
																		Ext.Msg.alert('SET','Registro Modificado');
																		NS.pBanModificar = false;
																		NS.idUsuarioM = 0;
															  			// RECARGAR DATOS ACTUALES DE LA BD Y DEAHBILITAR CAPTURA
																		NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().reset();
								        		 						NS.mostrarUsuarioEnForma();
											               				NS.habilitarFormaUsuario(false);
																		alert('aceptar 6');	
											               				*/
											               				
											               				// RECARGAR DATOS ACTUALES DE LA BD Y DEAHBILITAR CAPTURA
																		//NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().reset();
											               				NS.habilitarFormaUsuario(false);
											               				
																	}
																	NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().reset();
																},
																failure:function(form, action){ 
																	if(action.failureType == 'server'){ 
																		if(NS.pBanModificar){ 
																			Ext.Msg.alert('SET', 'No se puede actualizar el registro por alguna referencia');
																			NS.pBanModificar = false;
																		}
																		else
																			Ext.Msg.alert('SET', 'Error al insertar el registro'); 
																	}
																	else{ 
																		Ext.Msg.alert(	'SET', 'Fallo de conexión con el servidor de autenticación'); 
																	} 
																	NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().reset(); 
																	NS.storeNombreUsuario.load();
																} 
															}); //cierra submit
														} else {
															Ext.Msg.alert('SET','Selecciona una caja.');
														}
													} else {
														Ext.Msg.alert('SET','Selecciona una empresa.');
													}
												} else {
													Ext.Msg.alert('SET','Selecciona un estatus.');
												}
											} else {
												Ext.Msg.alert('SET','Selecciona un perfil.');
											}
							  			} else{ //ERROR DATOS NO VALIDOS
							  				Ext.Msg.alert('SET','Error '+ error); //MENSAJE DE ERROR
							  			} //FIN DE COMPONENTES NO VALIDOS YEC
							  			
							  			
							 		}
							 		
							}
							,{
							text:'Cancelar' 
							//,y: 400
							//,x: 160
							,formBind: true,
									handler: function(){
			               				NS.contenedorPrincipal.findById(PF+'frmUsuario').getForm().reset();
        		 						NS.mostrarUsuarioEnForma();
			               				NS.habilitarFormaUsuario(false);
			               				
			               				// limpiar grid de perfiles
			               				 Ext.getCmp(PF+'clave').setValue('');
										Ext.getCmp(PF+'nombre').setValue('');
										Ext.getCmp(PF+'apPaterno').setValue('');
										 Ext.getCmp(PF+'apMaterno').setValue('');
										 Ext.getCmp(PF+'contrasena').setValue('');
										 Ext.getCmp(PF+'cmbEstatusNuevo').setValue('');
										Ext.getCmp(PF+'correoElectronico').setValue('');
										Ext.getCmp(PF+'fecVencimiento').setValue('');
										Ext.getCmp(PF+'cmbEmpresa').setValue('');
										Ext.getCmp(PF+'cmbCaja').setValue('');
										 NS.banderaOpcion='';
			               				// 
										NS.perfilesUsuarioStore.removeAll();
										NS.gridPerfilesUsuario.refresh();
										
			            			}
 
							}
							] //cierra buttons
							,items:
								{
								  	xtype: "tabpanel",
					                defaultType: 'textfield',
					                deferredRender: false,
					               
		                            activeTab:0,
  		                            items:[
										{
											xtype: 'panel'
											,title: 'Perfiles'
											,items : [NS.gridPerfilesUsuario]
										},
		                            	{
				                        	xtype: 'panel'
						                	,title: 'Usuario'
						                	//,autoScroll: true
									    	,items : [        {
									         	 xtype : "fieldset"
									          	,title : "Datos del Usuario"
									          	,height:365
									          
          							            ,items: [
														{
														    xtype: 'checkbox',
														    id:PF+ 'chkBloqueo',
															name :PF + 'chkBloqueo',
															boxLabel: 'Bloqueo: ',
															hidden:true,
																
														},
									                    {
									                    	xtype: 'uppertextfield'
									                        ,fieldLabel : 'Clave Usuario'
									                        ,width: 150
									                        ,id: PF+'clave'
									                        ,name: PF+'clave'
									                        ,blankText : 'Este campo es requerido'
									                        ,allowBlank: false
									                        ,listeners:{
																change: {
																	fn:function(text){
																		if(text.getValue().length > 10){
																			Ext.Msg.alert('SET','La longitud de la clave no debe ser mayor a 10 dígitos.');
																			text.setValue('');
																			return;
																		}
																		
																		text.setValue(text.getValue().toUpperCase());
																	}
																}
															} 
									                    }
									                    
									                    ,{
									                        xtype: 'textfield'
									                        ,fieldLabel : 'Nombre'
									                        ,width: 150
									                        ,id: PF+'nombre'
									                        ,name: PF+'nombre'
									                        ,blankText : 'Este campo es requerido'
									                        ,allowBlank:	false
									                        ,enableKeyEvents:true
									                        ,listeners:{
																change: {
																	fn:function(text){
																		text.setValue(text.getValue().toUpperCase());
																	}
																},
																keyup:{
														 			fn:function(caja, e) {
														 				var texto=NS.validarTexto(caja.getValue());
														 				caja.setValue(texto);
														 			}
																}
															}
									                    }
									                    ,{
									                        xtype: 'textfield'
									                        ,fieldLabel : 'Apellido Paterno'
									                        ,width: 150
									                        ,id: PF+'apPaterno'
									                        ,name: PF+'apPaterno'
									                        ,blankText : 'Este campo es requerido'
									                        ,allowBlank: false
									                        ,enableKeyEvents:true
									                        ,listeners:{
																change: {
																	fn:function(text){
																		text.setValue(text.getValue().toUpperCase());
																	}
																},
																keyup:{
														 			fn:function(caja, e) {
														 				var texto=NS.validarTexto(caja.getValue());
														 				caja.setValue(texto);
														 			}
																}
									                    
															}
									                    }
									                    ,{
									                        xtype: 'textfield'
									                        ,fieldLabel : 'Apellido Materno'
									                        ,width: 150
									                        ,id: PF+'apMaterno'
									                        ,name: PF+'apMaterno'
									                        ,blankText : 'Este campo es requerido'
									                        ,allowBlank: false
									                        ,regeX:/[A-Z]|[a-z]/
									                        ,enableKeyEvents:true
									                        ,listeners:{
																change: {
																	fn:function(text){
																		text.setValue(text.getValue().toUpperCase());
																	}
																},
																keyup:{
														 			fn:function(caja, e) {
														 				var texto=NS.validarTexto(caja.getValue());
														 				caja.setValue(texto);
														 			}
																}
											                    
															}
									                    }
									                    ,NS.cmbEstatusNuevo
									                    ,{
									                        xtype: 'textfield'
									                        ,fieldLabel : 'Correo Electronico'
									                        ,width: 150
									                        ,id: PF+'correoElectronico'
									                        ,name: PF+'correoElectronico'
									                        ,blankText : 'Este campo es requerido'
									                        ,allowBlank:	false
									                        ,enableKeyEvents:true
								                        	,listeners:{
								                        		specialKey:{
									                				fn:function(caja, e) {
									                					if(e.keyCode==Ext.EventObject.TAB || e.keyCode == 13){
									                						NS.validarCorreo(caja.getValue());
																			if(NS.correoValido==false){
																				Ext.Msg.alert('SET','Correo no valido');
																				Ext.getCmp(PF+'correoElectronico').focus();
																			}else{
																				Ext.getCmp(PF+'contrasena').focus();
																			}	
									                					}
									                				}
									                			},
																change: {
																	fn:function(text){
																		NS.validarCorreo(text.getValue());
																		if(NS.correoValido==false){
																			Ext.Msg.alert('SET','Correo no valido');
																			Ext.getCmp(PF+'correoElectronico').focus();
																		}else{
																			Ext.getCmp(PF+'contrasena').focus();
																		}
																	}
																}
								                        	}
									                    }
									                    ,{
									                        inputType: 'password'
									                        ,xtype: 'textfield'
									                        ,fieldLabel : 'Contraseña'
									                        ,width: 150
									                        ,id: PF+'contrasena'
									                        ,name: PF+'contrasena'
									                        //,blankText : 'Este campo es requerido'
									                        //,allowBlank: false
									                        ,enableKeyEvents:true
									                		,listeners:{
									                			specialKey:{
									                				fn:function(caja, e) {
									                					if(e.keyCode==Ext.EventObject.TAB || e.keyCode == 13){
									                						var cadena=caja.getValue();
									                						NS.validarContrasena(cadena);
									                						if(error==''){
									                							Ext.getCmp(PF+'contrasena2').setValue('');
								                								Ext.getCmp(PF+'contrasena2').focus();
								                							}else{
								                								Ext.Msg.alert('SET',error);
								                								caja.setValue('');
								                								caja.focus();
								                							}
								                							
									                					}
									                				}
									                			},
									                	 		
									                			change:{fn:function(caja, e) {
									                				var cadena=caja.getValue();
							                						NS.validarContrasena(cadena);
						                							if(error==''){
						                								Ext.getCmp(PF+'contrasena2').setValue('');
						                								Ext.getCmp(PF+'contrasena2').focus();
						                							}else{
						                								caja.setValue('');
						                								Ext.Msg.alert('SET',error);
						                								caja.focus();
						                								
						                							}
						                							
									                	 		}}
									                		}
									                    }
									                    ,{
									                        inputType: 'password'
									                        ,xtype: 'textfield'
									                        ,fieldLabel : 'Verificar contraseña'
									                        ,width: 150
									                        ,id: PF+'contrasena2'
									                        ,name: PF+'contrasena2'
									                        ,blankText : 'Escriba nuevamente la contreseña'
									                        //,allowBlank: false
									                        ,enableKeyEvents:true
								                        	,listeners:{
																change: {
																	fn:function(text){
																		 var contrasena1=Ext.getCmp(PF+'contrasena').getValue();
																		 var contrasena2=text.getValue();
																		 if(contrasena1!=contrasena2){
																			 Ext.Msg.alert('SET','Las contraseñas no coinciden');
																			 text.setValue('');
																			 text.focus();
																		 }
																	}
																}
															}
									                    }
									                    
									                    
									                    ,{
									                         xtype: 'datefield'
									                        ,fieldLabel : 'Fecha Vencimiento'
									                        ,width: 100
									                        ,format: 'd/m/Y'
									                        ,id: PF+'fecVencimiento'
									                        ,name: PF+'fecVencimiento'
									                        ,blankText : 'Este campo es requerido'
									                        ,allowBlank:	false
									                        
									                    },
									                    
									                    NS.cmbEmpresa, 
									                    NS.cmbCaja ,
									                    
									                    
									    		]
									    								                  
									    	}]
									    								            
					            		}
					            			
			                        ]
			                    }

				            }
					            
			            ,{
			                xtype: 'fieldset'
			                ,title: 'Registros'
			                ,x: 0
			                ,y: 0
			                ,width: 600
			                ,height: 420
			               // ,autoScroll:true
			                ,layout: 'absolute'
			                ,id: 'frameResgistros'
			                ,items:[NS.gridUsuarios]
			            }
			         ]
			        }
	        ]
			});
			
	NS.habilitarFormaUsuario();
	NS.contenedorPrincipal.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
	
});