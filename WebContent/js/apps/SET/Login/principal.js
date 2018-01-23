var timeOut;

Ext.onReady(function() {
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET');
	Ext.Ajax.timeout = 200000;

	NS.sUserLogin = sUserLogin;
	NS.iUserId = iUserId;
	NS.APELLIDO_MATERNO = '';
	NS.APELLIDO_PATERNO = '';
	NS.NOMBRE = '';
	NS.NO_EMPRESA = 0;
	NS.NOM_EMPRESA = '';
	NS.ID_CAJA = 0;
	NS.DESC_CAJA = '';
	NS.NO_CUENTA_EMP = 0;
	NS.HOST_NAME_LOCAL = HOST_NAME_LOCAL;
	NS.fecHoy =new Date();
	NS.FEC_HOY = '';
	NS.sCodSession = sCodSession;

	NS.btnFacultativo = function(boton) {
		NS.verificarFacultad(boton);
		return boton;
	};






	NS.verificarFacultad = function(boton) {
		SegPerfilFacultadAction.verificarFacultad(boton.id, function(result, e) {
			//Valida conexión al servidor.
			if(e.message=="Unable to connect to the server."){
				//Ext.Msg.alert('SET',"Error de conexión al validar facultad en componente: " + boton.text);
				Ext.Msg.alert('SET','Error de conexión al validar facultad');
				boton.hide();
				return;
			}

			if(result.facultad){
				boton.show();
			}else if(result.excep != '' && result.excep != null && result.excep != undefined){
				Ext.Msg.alert('SET',result.excep + ' en ' + boton.text);
				boton.hide();
			}
			//Se comenta porque cuando hay error de conexión NO da respuesta con los mensajes, 
			//validado al inicio de la función. 
			/*else if(result.conn != '' && result.conn != null && result.conn != undefined){
				Ext.Msg.alert('SET',result.conn + ' en ' + boton.text);
				boton.hide();
			}else*/ 

		});
		boton.hide();
	};

	if( NS.sUserLogin == '' || NS.sUserLogin == ''){
		Ext.MessageBox.show({
			title : 'Información SET',
			msg : 'Usuario o Contraseña no válidos. Redireccionando...',
			width : 300,
			wait : true,
			progress:true,
			waitConfig : {interval:200}
		});
		var redirect = 'login.jsp'; 
		window.location = redirect;
	}

	GlobalAction.obtenerUsuariosActivos(function(result, e){
		if(result != null && result != undefined && result == true)
		{
			Ext.Msg.alert('Información SET', 'Ya existe una sesión con este usuario');
			var redirect = 'login.jsp'; 
			window.location = redirect;
		}
		else
		{
			GlobalAction.agregarUsuarioActivo(NS.iUserId, NS.sCodSession, function(result, e){
				GlobalAction.obtenerBanderaDatosCargados(function(result, e){
					if(result != null && result != undefined && result == true)
					{
						GlobalAction.obtenerSingleton(function(result, e){
							NS.agregarPropiedadesUsuario(result, true);
						});
						//Cargar datos configura_set
						GlobalAction.llenarDatosConfiguraSet(function(result, e){});
					}
				});
			});
		}
	});

	//Cargar fecha Hoy
	GlobalAction.obtenerFechaHoy(function(result, e){
		NS.FEC_HOY = result;
	});



	NS.agregarPropiedadesUsuario = function(result, bFirst){
		if (result != null) {
			NS.iUserId = parseInt(result.idUsuario);
			NS.APELLIDO_MATERNO = ''+result.materno;
			NS.APELLIDO_PATERNO = ''+result.paterno;
			NS.NOMBRE = ''+result.nombre;
			NS.NO_EMPRESA = parseInt(result.noEmpresa);
			NS.NOM_EMPRESA = ''+result.nomEmpresa;
			NS.ID_CAJA = parseInt(result.idCaja);
			NS.DESC_CAJA = ''+result.descCaja;
			NS.NO_CUENTA_EMP = parseInt(result.noCuentaEmp);
			//Se comento el if y el msg de bienvenido a peticion del usuario
			//if(bFirst)
			//Ext.Msg.alert(' BIENVENIDO ', NS.NOMBRE + '  ' + NS.APELLIDO_PATERNO + '  ' + NS.APELLIDO_MATERNO);
			Ext.getCmp('viewPortNorth').setTitle('Bienvenido, ' + NS.NOMBRE + '  ' + NS.APELLIDO_PATERNO + '  ' + NS.APELLIDO_MATERNO + ' - ' + ' ' + NS.NOM_EMPRESA + ' - ' + ' ' + NS.FEC_HOY);
		}
	};


	NS.permiso= new Ext.form.TextField({

		id: PF + 'permiso',
		name: PF + 'permiso',
		value:'',
		x: 120,
		y: 60,
		width: 430, 
		emptyText: '',

	});

	//Ventana para realizar el cambio de la empresa

	NS.limpiarWinCambioEmp = function()
	{
		NS.iIdEmpresa = 0;
		NS.sNomEmpresa = '';
		NS.iIdCaja = 0;
		NS.sDescCaja = '';
	};
	//store para el combo de empresas
	NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: GlobalAction.llenarComboEmpresasUsuario, 
		idProperty: 'id', 
		fields: [
		         {name: 'id'},
		         {name: 'descripcion'}
		         ],
		         listeners: {
		        	 load: function(s, records){
		        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
		        		 if(records.length === null || records.length <= 0)
		        		 {
		        			 Ext.Msg.alert('SET','No hay empresas asignadas a este usuario');
		        			 return;
		        		 }
		        	 }
		         }
	}); 

	NS.accionarEmpresa = function(valueCombo)
	{
		NS.iIdEmpresa = valueCombo;
		NS.sNomEmpresa = $('input[id*="'+ NS.cmbEmpresa.getId() +'"]').val(); 
	};

	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		name: PF + 'cmbEmpresa',
		id: PF + 'cmbEmpresa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		x: 140,
		y: 10,
		width: 190,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa',NS.cmbEmpresa.getId());
					NS.accionarEmpresa(combo.getValue());
				}
			}
		}
	});


	//store para el combo de cajas
	NS.storeCajas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: GlobalAction.llenarComboCajasUsuario, 
		idProperty: 'id', 
		fields: [
		         {name: 'id'},
		         {name: 'descripcion'}
		         ],
		         listeners: {
		        	 load: function(s, records){
		        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCajas, msg:"Cargando..."});
		        		 if(records.length === null || records.length <= 0)
		        		 {
		        			 Ext.Msg.alert('SET','No hay cajas asignadas a este usuario');
		        			 return;
		        		 }
		        	 }
		         }
	}); 

	NS.accionarCajas = function(valueCombo)
	{
		NS.iIdCaja = valueCombo;
		NS.sDescCaja = $('input[id*="'+ NS.cmbCajas.getId() +'"]').val(); 
	};

	NS.cmbCajas = new Ext.form.ComboBox({
		store: NS.storeCajas,
		name: PF + 'cmbCajas',
		id: PF + 'cmbCajas',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
		x: 140,
		y: 50,
		width: 190,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una caja',
		triggerAction: 'all',
		value: '',
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtNoCaja',NS.cmbCajas.getId());
					NS.accionarCajas(combo.getValue());
				}
			}
		}
	});

	NS.winCambioEmpresa = new Ext.Window({
		title: 'Cambio de Empresa',
		modal:true,
		shadow:true,
		closable: false,
		width: 394,
		height: 204,
		layout: 'absolute',
		id: 'winCambioEmpresa',
		items:[
		       {
		    	   xtype: 'fieldset',
		    	   title: '',
		    	   width: 360,
		    	   height: 120,
		    	   x: 10,
		    	   y: 10,
		    	   layout: 'absolute',
		    	   id: 'fSetPrincipal',
		    	   items: [
		    	           {
		    	        	   xtype: 'label',
		    	        	   text: 'Empresa:',
		    	        	   x: 0,
		    	        	   y: 10
		    	           },
		    	           {
		    	        	   xtype: 'textfield',
		    	        	   x: 70,
		    	        	   y: 10,
		    	        	   width: 60,
		    	        	   name: PF + 'txtNoEmpresa',
		    	        	   id: PF + 'txtNoEmpresa',
		    	        	   listeners: 
		    	        	   {
		    	        		   change:
		    	        		   {
		    	        			   fn: function(box, value)
		    	        			   {
		    	        				   var idCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa',NS.cmbEmpresa.getId());
		    	        				   if(idCombo !== null && idCombo !== '')
		    	        					   NS.accionarEmpresa(idCombo);
		    	        			   }
		    	        		   }
		    	        	   }
		    	           },
		    	           NS.cmbEmpresa,
		    	           {
		    	        	   xtype: 'label',
		    	        	   text: 'Caja:',
		    	        	   x: 0,
		    	        	   y: 50
		    	           },
		    	           {
		    	        	   xtype: 'textfield',
		    	        	   x: 70,
		    	        	   y: 50,
		    	        	   width: 60,
		    	        	   name: PF + 'txtNoCaja',
		    	        	   id: PF + 'txtNoCaja',
		    	        	   listeners: 
		    	        	   {
		    	        		   change:
		    	        		   {
		    	        			   fn: function(box, value)
		    	        			   {
		    	        				   var idCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoCaja',NS.cmbCajas.getId());
		    	        				   if(idCombo !== null && idCombo !== '')
		    	        					   NS.accionarCajas(idCombo);
		    	        			   }
		    	        		   }
		    	        	   }
		    	           },
		    	           NS.cmbCajas
		    	           ]
		       },
		       {
		    	   xtype: 'button',
		    	   text: 'Aceptar',
		    	   x: 200,
		    	   y: 140,
		    	   width: 70,
		    	   id: 'btnAceptar',
		    	   listeners:
		    	   {
		    		   click:
		    		   {
		    			   fn: function(e)
		    			   {
		    				   GlobalAction.cambiarEmpresaCajaUsuario(parseInt(NS.iIdEmpresa), NS.sNomEmpresa, 
		    						   parseInt(NS.iIdCaja), NS.sDescCaja, function(response, e){
		    					   NS.agregarPropiedadesUsuario(response, false);
		    					   NS.winCambioEmpresa.hide();
		    					   BFwrk.Util.msgShow('Ud. ha realizado cambios a su perfil, \npara regresar a los datos iniciales debe cerrar session', 'INFO');
		    				   });
		    			   }
		    		   }
		    	   }
		       },
		       {
		    	   xtype: 'button',
		    	   text: 'Cancelar',
		    	   x: 290,
		    	   y: 140,
		    	   width: 70,
		    	   id: 'btnCancelar',
		    	   listeners:
		    	   {
		    		   click:
		    		   {
		    			   fn: function(e)
		    			   {
		    				   NS.limpiarWinCambioEmp();
		    				   NS.winCambioEmpresa.hide();
		    			   }
		    		   }
		    	   }
		       }
		       ]
	});






	//Termina declaración de la ventana de cambio de empresa

	//session.setAttribute("iUsrActivo", 1);     
	NS.allowDuplicateTabs = false;
	NS.tabTokenDelimiter = ':';
	Ext.History.init();
	for(i=0; i<100; i++)
		Ext.History.add('idBfrmwrkTabPanel' + NS.tabTokenDelimiter + 'idTabWelcome');

	Ext.BLANK_IMAGE_URL = 'ext-3.3.0/resources/images/default/s.gif';
	Ext.QuickTips.init(); 

	// turn on validation errors beside the field globally
	Ext.form.Field.prototype.msgTarget = 'side';

	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

	// poner dominios

	Tree = Ext.tree;
	NS.accordionMenuPanel = new Ext.Panel({
		region: 'west',
		id: 'idAppFrmwrkMnuPanel',
		title: '<font color="#FFFFFF">' + '		MENU' + '</font>',
		layout: 'accordion',
		bodyBorder: true, 
		width: 250,
		collapsible: true,
		autoScroll : true,
		bodyStyle: 'background: #5B5B5E',
		layoutConfig: {animate: true,collapseFirst :true,fill:false}
	//items: modules // agregarlos despues
	});



	NS.panBienvenido = new Ext.Panel({
		title: 'Bienvenido',
		id:PF+'panBienvenido',
		name:PF+'panBienvenido',
		bodyStyle: 'background: #5B5B5E',
		items:[],
		listeners: {
			render: function() {



				//alert("TIENES PAGOS PENDIENTES:\n Mayor Info: Financiamiento-->AlertaVenciemientos");


				NS.limpiar=function(){

					Ext.getCmp(PF+'capital').setValue("");
					Ext.getCmp(PF+'interes').setValue("");
					Ext.getCmp(PF+'iva').setValue("");
					Ext.getCmp(PF+'pagoTotal').setValue("");
					NS.storeMod.removeAll();
					Ext.getCmp(PF+'btnBuscar').setDisabled(false);
				}



				NS.llenarDatos=function(){
					var datos="";
					var matriz=new Array(3);
					parseInt(NS.tam.getValue());
					var vec =[NS.tam.getValue()];

					// alert(NS.tam.getValue());
					for(var i=0;i<NS.tam.getValue();i++){

						NS.valores.setValue(NS.gridMod.getSelectionModel().getSelections()[i].get('fecha'));
						datos+=NS.valores.getValue();

					}
					// alert(NS.valores.getValue());
				};


				NS.formatNumber = function(num,prefix){
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
				};





				NS.agregarGridAval = function(lugar,fecha,empresa,institucion,financiamiento,tipoFin,montoCred,saldoActual,pagoCap,interes,iva,pagoTotal) {
					isColor: true;
				var indice = 0;
				var recordsDatGrid = NS.storeMod.data.items;
				//var tamGrid = indice <= 0 ? (NS.storeMod.data.items).length : indice;
				var tamGrid = lugar;
				var datosClase = NS.gridMod.getStore().recordType;
				var datos = new datosClase({
					fecha:fecha,
					empresa:empresa,
					institucion:institucion,
					financiamiento:financiamiento,
					tipoFin:tipoFin,
					montoCred:montoCred,
					saldoActual:saldoActual,
					pagoCap:pagoCap,
					interes:interes,
					iva:iva,
					pagoTotal:pagoTotal
				});
				NS.gridMod.stopEditing();
				NS.storeMod.insert(tamGrid, datos);
				};




				function cambiarFecha(fecha) {
					var mesArreglo=new Array(11);
					mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
					mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
					mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
					var mesDate=fecha.substring(4,7);
					var dia=fecha.substring(8,10);
					var anio=fecha.substring(11,15);

					for(var i=0;i<12;i=i+1) {
						if(mesArreglo[i]===mesDate) {
							var mes=i+1;
							if(mes<10)
								mes = '0' + mes;
						}
					}
					var fechaString=''+dia+'/'+mes+'/'+anio;
					return fechaString;		
				};


				NS.fecha=function(fecha, dias){
					fecha.setDate(fecha.getDate() + dias);
					return fecha;
				}


				NS.txtFecF = new Ext.form.DateField({
					id: PF+'txtFecF',
					name: PF+'txtFecF',
					format: 'd/m/Y',
					x: 550,
					y: 15,
					disabled :true, 
					width: 150

				});



				NS.tam= new Ext.form.TextField({
					id: PF + 'tam',
					name: PF + 'tam',
					x: 120,
					y: 60,
					value:2,
					width: 430, 
					emptyText: '',

				}); 


				//////bandera
				NS.bandera = new Ext.form.TextField({
					id: PF + 'bandera',
					name: PF + 'bandera',
					x: 120,
					y: 60,
					value:2,
					width: 430, 
					emptyText: '',

				}); 


				NS.txtLinea = new Ext.form.TextField({
					id: PF + 'txtLinea ',
					name: PF + 'txtLinea ',
					x: 120,
					y: 60,
					value:2,
					width: 430, 
					emptyText: '',

				}); 



				NS.valores= new Ext.form.TextField({
					id: PF + 'valores',
					name: PF + 'valores',
					x: 120,
					y: 60,
					value:2,
					width: 430, 
					emptyText: '',

				}); 




				NS.txtElab= new Ext.form.DateField({
					id: PF+'txtElab',
					name: PF+'txtElab',
					format: 'd/m/Y',
					x: 888,
					y: 0,
					disabled :true, 
					width: 150

				});


				var hoy;
				var d = new Date();
				var hoy= NS.fecha(d, +15);
				NS.txtFecF.setValue(hoy);

				var hoy2;
				var d2 = new Date();
				var hoy2= NS.fecha(d2,+0);
				NS.txtElab.setValue(hoy2);




				var lineas = '[{"cod_linea": "1", "nom_linea": "Linea 1"}, {"cod_linea": "2", "nom_linea": "Linea 2"}]';
				var lineasStore = new Ext.data.JsonStore({
					fields: [
					         {type: 'string', name: 'cod_linea'},
					         {type: 'string', name: 'nom_linea'}
					         ]
				});
				lineasStore.loadData(Ext.decode(lineas));





				NS.storeBanco = new Ext.data.DirectStore({
					paramsAsHash: false,
					baseParams:{},
					root: '',
					paramOrder:['usuario'],
					//directFn: FinanciamientoModificacionCAction.llenarCmbLineas, 
					directFn: AlertaVencimientoAction.llenarCmbBanco,
					idProperty: 'id', 
					fields: [
					         {name: 'id'},
					         {name: 'descripcion'}
					         ],
					         listeners: {
					        	 load: function(s, records){
					        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando..."});
					        		 if (records.length == null || records.length <= 0)
					        			 Ext.Msg.alert('SET', 'No tiene empresas asignadas');

					        		 //Se agrega la opcion de todas las empresas
					        		 var recordsStoreBanco = NS.storeBanco.recordType;
					        		 var todas = new recordsStoreBanco({
					        			 id: 0,
					        			 descripcion: '****TODAS****'
					        		 });
					        		 NS.storeBanco.insert(0, todas);
					        	 }
					         }
				}); 

				NS.storeBanco.baseParams.usuario = 2;
				NS.storeBanco.load();






				NS.cmbBanco = new Ext.form.ComboBox({
					store: NS.storeBanco,
					name: PF + 'cmbBanco',
					id: PF + 'cmbBanco',
					x: 70,
					y: 15,
					whidth:90,
					typeAhead: true,
					mode: 'local',
					selecOnFocus: true,
					forceSelection: true,
					valueField: 'id',
					displayField: 'descripcion',
					autocomplete: true,
					emptyText: 'Seleccione una banco',
					triggerAction: 'all',
					value: '****TODAS****',
					visible: false,
					listeners:
					{
						select:
						{
							fn:function(combo, valor)  
							{  

								BFwrk.Util.updateComboToTextField(PF + 'noBanco', NS.cmbBanco.getId());

								var banco=Ext.getCmp(PF + 'noBanco').getValue();
								parseInt(banco);

								if(banco==0){
									NS.storeLinea.baseParams.banco =0;
									NS.storeLinea.load();
									NS.storeCredito.baseParams.banco = 0;
									NS.storeCredito.load();


								}else{
									NS.storeLinea.baseParams.banco = banco;
									NS.storeLinea.load();

									NS.storeCredito.baseParams.banco = banco;
									NS.storeCredito.load();


								}



							}
						}
					}
				});   




				NS.storeLinea= new Ext.data.DirectStore({
					paramsAsHash: false,
					baseParams:{},
					root: '',
					paramOrder:['banco'],
					//directFn: FinanciamientoModificacionCAction.llenarCmbLineas, 
					directFn: AlertaVencimientoAction.llenarCmbLinea,
					idProperty: 'id', 
					fields: [
					         {name: 'id'},
					         {name: 'descripcion'}
					         ],
					         listeners: {
					        	 load: function(s, records){
					        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLinea, msg:"Cargando..."});
					        		 if (records.length == null || records.length <= 0)
					        			 Ext.Msg.alert('SET', 'No tiene Lienas asignadas');

					        		 //Se agrega la opcion de todas las empresas
					        		 var recordsStoreLinea = NS.storeLinea.recordType;
					        		 var todas = new recordsStoreLinea({
					        			 id: 0,
					        			 descripcion: '****TODAS****'
					        		 });
					        		 NS.storeLinea.insert(0, todas);
					        	 }
					         }
				}); 









				NS.cmblinea= new Ext.form.ComboBox({//regresa2
					store: NS.storeLinea, 	//store: NS.storeEmpresasHijo,
					name: PF + 'cmblinea',
					id: PF + 'cmblinea',
					x: 255,
					y: 15,
					width: 150,
					typeAhead: true,
					mode: 'local',
					selecOnFocus: true,
					forceSelection: true,
					valueField: 'id',
					displayField: 'descripcion',
					autocomplete: true,
					emptyText: 'Seleccione una Linea',
					triggerAction: 'all',
					value: '****TODAS****',
					visible: false,
					listeners:{
						select:{
							fn:function(combo, valor) { 
								var linea=valor.get('descripcion');
								NS.txtLinea.setValue(linea)

							}
						}
					}
				});



				NS.storeCredito= new Ext.data.DirectStore({
					paramsAsHash: false,
					baseParams:{},
					root: '',
					paramOrder:['banco'],
					//directFn: FinanciamientoModificacionCAction.llenarCmbLineas, 
					directFn: AlertaVencimientoAction.llenarCmbCredito,
					idProperty: 'id', 
					fields: [
					         {name: 'id'},
					         {name: 'descripcion'}
					         ],
					         listeners: {
					        	 load: function(s, records){
					        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCredito, msg:"Cargando..."});
					        		 if (records.length == null || records.length <= 0)
					        			 Ext.Msg.alert('SET', 'No tiene Lienas asignadas');

					        		 //Se agrega la opcion de todas las empresas
					        		 var recordsStoreCredito= NS.storeCredito.recordType;
					        		 var todas = new recordsStoreCredito({
					        			 id: 0,
					        			 descripcion: '****TODAS****'
					        		 });
					        		 NS.storeCredito.insert(0, todas);
					        	 }
					         }
				}); 



				NS.cmbCredito= new Ext.form.ComboBox({
					store: NS.storeCredito, 
					name: PF + 'cmbCredito',
					id: PF + 'cmbCredito',
					x: 420,
					y: 15,
					width: 90,  
					typeAhead: true,
					mode: 'local',
					selecOnFocus: true,
					forceSelection: true,
					valueField: 'id',
					displayField: 'descripcion',
					autocomplete: true,
					emptyText: 'Seleccione un credito',
					triggerAction: 'all',
					value: '****TODAS****',
					visible: false,
					listeners:{
						select:{
							fn:function(combo, valor) { 
							}
						}
					}			
				});





				/**CODIGO DEL GRID**/

				//store del grid consulta




				NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
					singleSelect:false
				});


				NS.storeMod = new Ext.data.DirectStore({
					paramsAsHash: false,
					baseParams:{	
						banco:0,
						linea:0,
						credito:0,
						feha:"",
						conso: 0},
						root: '',
						paramOrder:['banco','linea','credito','fecha','conso'],
						directFn: AlertaVencimientoAction.llenarGrid, 
						fields: [
						         {name: 'fecha'},
						         {name: 'empresa'},
						         {name: 'institucion'},
						         {name: 'financiamiento'},
						         {name: 'tipoFin'},
						         {name: 'montoCred'},
						         {name: 'saldoActual'},
						         {name: 'pagoCap'},
						         {name: 'interes'},
						         {name: 'iva'},
						         {name: 'pagoTotal'}
						         ],
						         listeners: {
						        	 load: function(s, records) {



						        		 if (records.length<=0) {
						        			// alert("No existen Financiamientos pendientes");
						        		 }else{

						        			 var tam= records.length;
						        			 NS.tam.setValue(tam);
						        			 var totalMontoC= 0.0;
						        			 var  totalSaldoActual = 0.0;
						        			 var totalPagoCap = 0.0;
						        			 var  totalInteres= 0.0;
						        			 var totaliva= 0.0;
						        			 var totalPagoTotal = 0.0;
						        			 var ce=0.0;

						        			 var totalMontoCG= 0.0;
						        			 var  totalSaldoActualG = 0.0;
						        			 var totalPagoCapG = 0.0;
						        			 var  totalInteresG= 0.0;
						        			 var totalivaG= 0.0;
						        			 var totalPagoTotalG = 0.0;



						        			 //alert("recordsTam"+records.length);
						        			 if(records.length==1){
						        				 totalMontoC+=parseFloat(records[0].get('montoCred'));
						        				 totalSaldoActual += parseFloat(records[0].get('saldoActual'));
						        				 totalPagoCap += parseFloat(records[0].get('pagoCap'));
						        				 totalInteres+=parseFloat( records[0].get('interes'));
						        				 totaliva+= parseFloat(records[0].get('iva'));
						        				 totalPagoTotal += parseFloat( records[0].get('pagoTotal'));
						        				 NS.agregarGridAval(records.length,' ALERTA ',' PAGO ',' POR VENCER ',' PRIORIDAD: ','TOTAL DEL DIA',NS.formatNumber(totalMontoC),NS.formatNumber(totalSaldoActual),NS.formatNumber(totalPagoCap),NS.formatNumber(totalInteres),NS.formatNumber(totaliva),NS.formatNumber(totalPagoTotal));
						        				 NS.agregarGridAval(records.length+1,'  ','  ','  ','  ','TOTAL GLOBAL',NS.formatNumber(totalMontoC),NS.formatNumber(totalSaldoActual),NS.formatNumber(totalPagoCap),NS.formatNumber(totalInteres),NS.formatNumber(totaliva),NS.formatNumber(totalPagoTotal));

//						        				 totalCapital = totalCapital + totalCapitalDia
//						        				 totalInteres = totalInteres + totalInteresDia
//						        				 totalRenta = totalRenta + totalRentaDia
//						        				 totalIva = totalIva + totalIvaDia
//						        				 total = total + totalDia

						        				 totalMontoC= 0;
						        				 totalSaldoActual = 0;
						        				 totalPagoCap = 0;
						        				 totalInteres= 0;
						        				 totaliva= 0;
						        				 totalPagoTotal = 0;
						        			 }else{
						        				 //alert("sol"+records.length);

						        				 for (var i = 0; i <records.length-1; i++) {
						        					 totalMontoC= 0;
						        					 totalSaldoActual = 0;
						        					 totalPagoCap = 0;
						        					 totalInteres= 0;
						        					 totaliva= 0;
						        					 totalPagoTotal = 0;


						        					 totalMontoC+=parseFloat(records[i].get('montoCred'));
						        					 totalSaldoActual += parseFloat(records[i].get('saldoActual'));
						        					 totalPagoCap += parseFloat(records[i].get('pagoCap'));
						        					 totalInteres+= parseFloat(records[i].get('interes'));
						        					 totaliva+= parseFloat(records[i].get('iva'));
						        					 totalPagoTotal += parseFloat(records[i].get('pagoTotal'));


						        					 
						        					 totalMontoCG+=totalMontoC ;
						        					 totalSaldoActualG+=totalSaldoActual ;
						        					 totalPagoCapG +=totalPagoCap ;
						        					 totalInteresG+=totalInteres ;
						        					 totalivaG+=totaliva;
						        					 totalPagoTotalG+=totalPagoTotal ;



						        					 for (var j = i+1; j < records.length; j++) {
						        						 /*alert(j)
	        			 								alert("FI"+records[i].get('fecha'))
	        			 								alert("FJ"+records[j].get('fecha'))*/

						        						 if(records[i].get('fecha')==records[j].get('fecha')){

						        							 totalMontoC+=parseFloat(records[j].get('montoCred'));
						        							 totalSaldoActual += parseFloat(records[j].get('saldoActual'));
						        							 totalPagoCap += parseFloat(records[j].get('pagoCap'));
						        							 totalInteres+= parseFloat(records[j].get('interes'));
						        							 totaliva+= parseFloat(records[j].get('iva'));
						        							 totalPagoTotal += parseFloat(records[j].get('pagoTotal'));


								        					 
						        							 
						        							 totalMontoCG+=totalMontoC ;
						        							 totalSaldoActualG+=totalSaldoActual ;
						        							 totalPagoCapG +=totalPagoCap ;
						        							 totalInteresG+=totalInteres ;
						        							 totalivaG+=totaliva;
						        							 totalPagoTotalG+=totalPagoTotal ;
						        							 


						        						 }else{
						        							 //alert("pos::"+j);
						        							 if(ce==1){
						        								 j=j+1;
						        							 }	
						        							 /*	
	        			 								    alert("*j*"+records[j].get('fecha'))
	        				 								alert("I*"+i);
	        			 									alert("j*"+j)
	        				 								alert("FI*"+records[i].get('fecha'))
	        				 								alert("FJ*"+records[j].get('fecha'))
						        							  */



						        							 NS.agregarGridAval(j,'ALERTA',' PAGO',' POR VENCER ',' PRIORIDAD: ','TOTAL DEL DIA',NS.formatNumber(totalMontoC),NS.formatNumber(totalSaldoActual),NS.formatNumber(totalPagoCap),NS.formatNumber(totalInteres),NS.formatNumber(totaliva),NS.formatNumber(totalPagoTotal));
						        							 totalMontoC=0;
						        							 i=j-1;
						        							 //alert("I2"+i);
						        							 ce++;
						        							 i=records.length+2;
						        							 break;
						        						 }

						        					 }


						        				 }


						        				 totalPagoCapG =totalPagoCapG +totalInteresG;
						        				 totalPagoTotalG = totalPagoCapG;
//						        				 NS.agregarGridAval(records.length+1,'  ','  ','  ','  ','TOTAL DEL DIA',0.0,0.0,totalPagoCap,totalInteres,totaliva,totalPagoTotal);					
						        				 NS.agregarGridAval(records.length+1,'  ','  ','  ','  ','TOTAL GLOBAL',NS.formatNumber(totalMontoC),NS.formatNumber(totalSaldoActual),NS.formatNumber(totalPagoCapG),NS.formatNumber(totalInteresG),NS.formatNumber(totalivaG),NS.formatNumber(totalPagoTotalG));



						        				 NS.formatNumber(Ext.getCmp(PF+'capital').setValue(totalPagoCapG));
						        				 NS.formatNumber(Ext.getCmp(PF+'interes').setValue(totalInteresG));
						        				 NS.formatNumber(Ext.getCmp(PF+'iva').setValue(totalivaG));	        	 						
						        				 NS.formatNumber(Ext.getCmp(PF+'pagoTotal').setValue(totalPagoCapG+totalInteresG));





						        				 totalMontoCG= 0;
						        				 totalSaldoActualG = 0;
						        				 totalPagoCapG = 0;
						        				 totalInteresG= 0;
						        				 totalivaG= 0;
						        				 totalPagoTotalG = 0;



						        			 }

						        		 }


						        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod, msg: "Cargando..."});
						        		 Ext.getCmp(PF+'btnBuscar').setDisabled(true);
						        		 if (records.length == null || records.length <= 0){
						        			 //Ext.Msg.alert('SET', 'No existen Datos para esta operacion');
						        			 myMask.hide();
						        		 }
						        	 }

						         }
				});


				NS.columnasMod = new Ext.grid.ColumnModel([
				                                           NS.columnaSeleccion,
				                                           {header: 'Fecha', width: 120, dataIndex: 'fecha', sortable: true},
				                                           {header: 'Empresa', width: 250, dataIndex: 'empresa', sortable: true},
				                                           {header: 'Institucion', width:170 , dataIndex: 'institucion', sortable: true},
				                                           {header: 'Financiamineto', width: 170, dataIndex: 'financiamiento', sortable: true},
				                                           {header: 'Tipo Financiamiento', width:170, dataIndex: 'tipoFin', sortable: true},
				                                           {header: 'Monto Credito', width: 70, dataIndex: 'montoCred', sortable: true},
				                                           {header: 'Saldo actual', width:70, dataIndex: 'saldoActual', sortable: true},
				                                           {header: 'Pago a capital', width: 70, dataIndex: 'pagoCap', sortable: true},
				                                           {header: 'Interes', width:70, dataIndex: 'interes', sortable: true},
				                                           {header: 'Iva', width: 70, dataIndex: 'iva', sortable: true},
				                                           {header: 'Pago Total', width:70 , dataIndex: 'pagoTotal', sortable: true}




				                                           ]);




				NS.gridMod = new Ext.grid.GridPanel({
					store:NS.storeMod,
					id: 'gridMod',
					name: 'gridMod',
					cm: NS.columnasMod,
					width: 1025,
					height: 180,
					x: 0,
					y: 30,
					stripeRows: true,
					columnLines: true,
					cm:NS.columnasMod,
					sm: NS.columnaSeleccion,
					listeners: {
						click: {
							fn: function(grid,valG){	
								var registroSeleccionado = NS.gridMod.getSelectionModel().getSelections();
								//	alert(registroSeleccionado);



								if (registroSeleccionado>=0){	
									Ext.getCmp(PF + 'btnMod').setVisible(true);

									//myMask.show(); 
								}
							}
						}
					}
				});

				/*Find el codigo del grid*/


				NS.storeMod.baseParams.banco=0;
				NS.storeMod.baseParams.linea ='****TODAS****';
				NS.storeMod.baseParams.credito=0;
				NS.storeMod.baseParams.fecha =	NS.txtFecF.getValue();
				NS.storeMod.baseParams.conso=2;


				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod, msg:"Buscando..."});
				NS.storeMod.load();


				NS.imprimir=function(){
					if(NS.storeMod.getCount()<=0){
						Ext.Msg.alert("SET","No existen registros.");
						return;
					}
					else{
						var matriz= new Array();
						for (var i = 0; i < NS.storeMod.data.length; i++) {
							var record=NS.storeMod.getAt(i);
							var registros = {};

							registros.fecha = record.data.fecha;
							registros.empresa=record.data.empresa;
							registros.institucion=record.data.institucion;
							registros.financiamiento=record.data.financiamiento;
							registros.tipoFin=record.data.tipoFin;
							registros.montoCred=record.data.montoCred;
							registros.saldoActual=record.data.saldoActual;
							registros.pagoCap=record.data.pagoCap;
							registros.interes=record.data.interes;
							registros.iva=record.data.iva;
							registros.pagoTotal=record.data.pagoTotal;
							matriz[i] = registros;
						}
						var jsonString= Ext.util.JSON.encode(matriz);
						var parametros='';
						var forma = NS.alertaC.getForm();
						Ext.getCmp('hdJason').setValue(jsonString);
						Ext.getCmp('nomReporte').setValue("ReporteAlerta");
						forma.url = '/SET/jsp/Reportes.jsp';
						forma.standardSubmit = true;
						forma.method = 'GET';
						forma.target = '_blank';
						var el = forma.getEl().dom;
						var target = document.createAttribute("target");
						target.nodeValue = "_blank";
						el.setAttributeNode(target);

						forma.submit({target: '_blank'});

					}
				}





				NS.alertaC = new Ext.form.FormPanel({
					title: 'Alerta Vencimiento',
					width: 1100,
					height: 520,
					padding: 10,
					layout: 'absolute',
					id: PF + 'alertaC',
					name: PF + 'alertaC',
					renderTo: NS.tabContId,
					frame: true,
					autoScroll: true,
					items : [




					         {
					        	 xtype: 'textfield',
					        	 x:0,
					        	 y:0,
					        	 hidden:true,
					        	 name: 'nomReporte',
					        	 id: 'nomReporte',
					        	 value: 'ReporteVencimientos'
					         },{ 
					        	 xtype: 'fieldset',
					        	 title: 'Alerta Vencimimento',
					        	 width: 1050,
					        	 height: 100,
					        	 x: 10,
					        	 y: 10,
					        	 layout: 'absolute',
					        	 id: 'fSetPrincipal',
					        	 items: [
					        	         {
					        	        	 xtype: 'textfield',
					        	        	 x:0,
					        	        	 y:0,
					        	        	 hidden:true,
					        	        	 name:'hdJason',
					        	        	 id:'hdJason',
					        	        	 value: ''
					        	         },{
					        	        	 xtype: 'label',
					        	        	 text: 'Banco:',
					        	        	 x: 1,
					        	        	 y: 0
					        	         },
					        	         {
					        	        	 xtype: 'numberfield',
					        	        	 x: 1,
					        	        	 y: 15,
					        	        	 width: 60,
					        	        	 value:0,
					        	        	 disabled :false, 
					        	        	 name: PF + 'noBanco',
					        	        	 id: PF + 'noBanco',
					        	        	 listeners:
					        	        	 {
					        	        		 change: 
					        	        		 {
					        	        			 fn: function(box, value)
					        	        			 {
					        	        				 if(box!==0){
					        	        					 var bancoI=value.getId('noBanco');	
					        	        					 paseInt(bancoI);
					        	        					 //alert(bancoI);
					        	        					 NS.storeBanco.baseParams.banco =bancoI;
					        	        				 }



					        	        			 }
					        	        		 }
					        	        	 }
					        	         },
					        	         NS.cmbBanco,
					        	         {
					        	        	 xtype: 'label',
					        	        	 text: 'Linea:',
					        	        	 x: 250,
					        	        	 y: 0
					        	         },
					        	         NS.cmblinea,
					        	         {
					        	        	 xtype: 'label',
					        	        	 text: 'No credito:',
					        	        	 x: 420,
					        	        	 y: 0
					        	         },   
					        	         NS.cmbCredito,
					        	         {
					        	        	 xtype: 'label',
					        	        	 text: 'Fecha Final:',
					        	        	 x: 550,
					        	        	 y: 0
					        	         },
					        	         NS.txtFecF,
					        	         {xtype: 'checkbox',
					        	        	 id: PF + 'con',
					        	        	 name: PF + 'con', 
					        	        	 x: 730,
					        	        	 y: 15,
					        	        	 boxLabel: 'Consolidado',

					        	        	 listeners: {
					        	        		 check: {
					        	        			 fn:function(opt, valor) {
					        	        				 if(valor == true) {
					        	        					 var a=1;
					        	        					 parseInt(a)
					        	        					 NS.bandera.setValue(a);


					        	        				 }else {
					        	        					 var b=2;
					        	        					 parseInt(b)
					        	        					 NS.bandera.setValue(b);

					        	        				 }
					        	        			 }
					        	        		 }
					        	        	 }
					        	         },
					        	         {
					        	        	 xtype: 'button',
					        	        	 text: 'Buscar',
					        	        	 x: 860,
					        	        	 y:15,
					        	        	 width: 70,
					        	        	 id: PF + 'btnBuscar',
					        	        	 name: PF + 'btnBuscar',
					        	        	 disabled: false,
					        	        	 listeners:
					        	        	 {
					        	        		 click:
					        	        		 {
					        	        			 fn: function(btn)
					        	        			 {

					        	        				 var banco =Ext.getCmp(PF + 'noBanco').getValue();
					        	        				 parseInt(banco);
					        	        				 var linea2=NS.txtLinea.getValue();
					        	        				 var credito=Ext.getCmp(PF + 'cmbCredito').getValue();
					        	        				 parseInt(credito); 
					        	        				 var fec=NS.txtFecF.getValue();
					        	        				 var conso=NS.bandera.getValue();
					        	        				 parseInt(conso);




					        	        				 if (banco ==0) {

					        	        					 var banco =0;
					        	        					 parseInt(banco);
					        	        					 var linea2="****TODAS****";
					        	        					 var credito=0;
					        	        					 parseInt(credito); 
					        	        					 var fec=NS.txtFecF.getValue();
					        	        					 var conso=NS.bandera.getValue();
					        	        					 parseInt(conso);
					        	        				 } 






					        	        				 NS.storeMod.baseParams.banco=banco;
					        	        				 NS.storeMod.baseParams.linea =linea2;
					        	        				 NS.storeMod.baseParams.credito=credito;
					        	        				 NS.storeMod.baseParams.fecha =	fec;
					        	        				 NS.storeMod.baseParams.conso=conso;


					        	        				 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod, msg:"Buscando..."});
					        	        				 NS.storeMod.load();




					        	        			 }
					        	        		 }
					        	        	 }
					        	         },






					        	         ]/*abajo items*/
					         },
					         { 
					        	 xtype: 'fieldset',
					        	 title: 'Control de pagos de pasivos',
					        	 width: 1050,
					        	 height: 300,
					        	 x: 10,
					        	 y: 110,
					        	 layout: 'absolute',
					        	 id: 'fSetSec',
					        	 items: [
					        	         {
					        	        	 xtype: 'label',
					        	        	 text: 'Elaborado:',
					        	        	 x: 830,
					        	        	 y: 0
					        	         },
					        	         NS.txtElab,
					        	         NS.gridMod,
					        	         {
					        	        	 xtype: 'label',
					        	        	 text: 'PAGO GLOBAL:',
					        	        	 x:0,
					        	        	 y: 230
					        	         },


					        	         {
					        	        	 xtype: 'label',
					        	        	 text: 'Capital:',
					        	        	 x:100,
					        	        	 y: 230
					        	         },
					        	         {
					        	        	 xtype: 'numberfield',
					        	        	 x: 140,
					        	        	 y: 230,
					        	        	 width: 100,
					        	        	 disabled :true, 
					        	        	 name: PF + 'capital',
					        	        	 id: PF + 'capital',
					        	        	 listeners:
					        	        	 {
					        	        		 change: 
					        	        		 {
					        	        			 fn: function(box, value)
					        	        			 {




					        	        			 }
					        	        		 }
					        	        	 }
					        	         },
					        	         {
					        	        	 xtype: 'label',
					        	        	 text: 'Interes:',
					        	        	 x:240,
					        	        	 y: 230
					        	         },
					        	         {
					        	        	 xtype: 'numberfield',
					        	        	 x: 280,
					        	        	 y: 230,
					        	        	 width: 60,
					        	        	 disabled :true, 
					        	        	 name: PF + 'interes',
					        	        	 id: PF + 'interes',
					        	        	 listeners:
					        	        	 {
					        	        		 change: 
					        	        		 {
					        	        			 fn: function(box, value)
					        	        			 {




					        	        			 }
					        	        		 }
					        	        	 }
					        	         },
					        	         {
					        	        	 xtype: 'label',
					        	        	 text: 'IVA:',
					        	        	 x:460,
					        	        	 y: 230
					        	         },
					        	         {
					        	        	 xtype: 'numberfield',
					        	        	 x: 485,
					        	        	 y: 230,
					        	        	 width: 60,
					        	        	 disabled :true, 
					        	        	 name: PF + 'iva',
					        	        	 id: PF + 'iva',
					        	        	 listeners:
					        	        	 {
					        	        		 change: 
					        	        		 {
					        	        			 fn: function(box, value)
					        	        			 {




					        	        			 }
					        	        		 }
					        	        	 }
					        	         },
					        	         {
					        	        	 xtype: 'label',
					        	        	 text: 'PAGO TOTAL:',
					        	        	 x:570,
					        	        	 y: 230
					        	         },
					        	         {
					        	        	 xtype: 'numberfield',
					        	        	 x: 640,
					        	        	 y: 230,
					        	        	 width: 90,
					        	        	 disabled :true, 
					        	        	 name: PF + 'pagoTotal',
					        	        	 id: PF + 'pagoTotal',
					        	        	 listeners:
					        	        	 {
					        	        		 change: 
					        	        		 {
					        	        			 fn: function(box, value)
					        	        			 {




					        	        			 }
					        	        		 }
					        	        	 }
					        	         },


					        	         {
					        	        	 xtype: 'button',
					        	        	 text: 'Exportar Excel',
					        	        	 x:740 ,
					        	        	 y: 220,
					        	        	 width: 70,
					        	        	 id: PF + 'btnExcel',
					        	        	 name: PF + 'btnExcel',
					        	        	 disabled: false,
					        	        	 listeners:
					        	        	 {
					        	        		 click:
					        	        		 {
					        	        			 fn: function(btn)
					        	        			 {

					        	        				 if (Ext.getCmp(PF+'capital').getValue()>0) {
					        	        					 var datos="";
					        	        					 var parametros='';

					        	        					 var matriz= new Array();
					        	        					 for (var i = 0; i < NS.storeMod.data.length; i++) {
					        	        						 var record=NS.storeMod.getAt(i);
					        	        						 var registros = {};

					        	        						 registros.fecha = record.data.fecha;
					        	        						 registros.empresa=record.data.empresa;
					        	        						 registros.institucion=record.data.institucion;
					        	        						 registros.financiamiento=record.data.financiamiento;
					        	        						 registros.tipoFin=record.data.tipoFin;
					        	        						 registros.montoCred=record.data.montoCred;
					        	        						 registros.saldoActual=record.data.saldoActual;
					        	        						 registros.pagoCap=record.data.pagoCap;
					        	        						 registros.interes=record.data.interes;
					        	        						 registros.iva=record.data.iva;
					        	        						 registros.pagoTotal=record.data.pagoTotal;
					        	        						 matriz[i] = registros;
					        	        					 }
					        	        					 var jsonString= Ext.util.JSON.encode(matriz);
					        	        					 AlertaVencimientoAction.exportaExcelPagos(jsonString,function(res){

					        	        						 if (res != null && res != undefined && res == "") {
					        	        							 Ext.Msg.alert('SET', "Error al generar el archivo");
					        	        						 } else {
					        	        							 Ext.Msg.alert('SET', "El archivo se guardo con exito en:"+res);
					        	        							 parametros = '?nomReporte=excelPagosP';
					        	        							 parametros += '&nomParam1=tipo';
					        	        							 parametros += '&valParam1='+ res;
					        	        							 window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+ parametros);
					        	        						 }
					        	        					 });


					        	        				 } else {
					        	        					 Ext.Msg.alert('SET'," No Hay Pagos Por Vencer");
					        	        				 }

					        	        			 }
					        	        		 }
					        	        	 }
					        	         },
					        	         {
					        	        	 xtype: 'button',
					        	        	 text: 'Imprimir',
					        	        	 x:830 ,
					        	        	 y: 220,
					        	        	 width: 70,
					        	        	 id: PF + 'btnImprimir',
					        	        	 name: PF + 'btnImprimir',
					        	        	 disabled: false,
					        	        	 listeners:
					        	        	 {
					        	        		 click:
					        	        		 {
					        	        			 fn: function(btn)
					        	        			 {
					        	        				 NS.imprimir();	
					        	        			 }
					        	        		 }
					        	        	 }
					        	         },

					        	         {
					        	        	 xtype: 'button',
					        	        	 text: 'Limpiar',
					        	        	 x:920 ,
					        	        	 y: 220,
					        	        	 width: 70,
					        	        	 id: PF + 'btnLimpiar',
					        	        	 name: PF + 'btnLimpiar',
					        	        	 disabled: false,
					        	        	 listeners:
					        	        	 {
					        	        		 click:
					        	        		 {
					        	        			 fn: function(btn)
					        	        			 {
					        	        				 NS.limpiar();
					        	        			 }
					        	        		 }
					        	        	 }
					        	         },




					        	         ]
					         }


					         ]
				});




				NS.winPagos = new Ext.Window({
					title: 'Alerta Pagos de pasivos',
					modal: true,
					shadow: true,
					closeAction: 'hide',
					width: 1100,
					height: 520,
					layout: 'absolute',
					plain: true,
					bodyStyle: 'padding:10px;',
					buttonAlign: 'center',
					draggable: true,
					resizable: true,
					autoScroll: true,
					items: [//iniciowin

					        NS.alertaC,

					        ]//finwin
				});






			}
		},
		html: '<img src="img/menu/Bienvenida.gif" style="height:100%;width:100%;" alt="fondo" align="middle" > </img>'	

	});	



	var modules = []; 
	var rootTreeMenu = [];
	var treeMenu = [];  
	var treePanelMenu = [];
	var treeMenuAlertID = [];


	SegMenuAction.listaPermisos(NS.sUserLogin, function(res){


		var c1="Financiamiento";
		var c2="financiamiento";
		var  c3="Financiamientos";
		var c4="financiamientos";

		if (res != null && res != undefined && res == "") {
			//Ext.Msg.alert('SET', "No existen permisos para el reporte");
		}else{
			if(res==c1  || res==c2 || res==c3  || res==c4   ){
				NS.winPagos.show();							
			}


		}
	});


	//Arma el menu de manera dinamica en base a lo que se encuentre asignado al perfil de cada usuario en la base de datos
	SegMenuAction.listaModulos(NS.sUserLogin, function(result, e){
		if(!result){
			Ext.Msg.alert('SET','Error en acceso al menu desde la base de datos.');
			return;
		}

		for(var i=0; i < result.listModulos.length; i++){
			var module = {
					id:'BFRMWRKAPPMODULE-'+result.listModulos[i].idComponente,
					name:'BFRMWRKAPPMODULE-'+result.listModulos[i].idComponente,
					border: false,
					title: '<font color="white">' + result.listModulos[i].etiqueta + '</font>',
					iconCls: result.listModulos[i].ruta_imagen,
					bodyStyle: 'background:#5B5B5E',
					html: "<div id='treeMenuDiv.Mod"+result.listModulos[i].idComponente+"' class='x-panel custom-accordion x-panel-noborder x-tree' style='width:auto;border:1px solid #c3daf9;'></div>",
					listeners: {
						resize: function(pan, adjWidth, adjHeight, rawWidth, rawHeight){
							if(pan==null) return;

							var el = pan.getEl();
							if(el==null) return;

							var childEl = el.first('.x-panel-bwrap');
							if(childEl==null) return;

							childEl = childEl.first('.x-panel-body');
							if(childEl==null) return;

							childEl = childEl.first('.x-panel');
							if(childEl==null) return;

							childEl = childEl.first('.x-panel-bwrap');
							if(childEl==null) return;

							childEl = childEl.first('.x-panel-body');
							if(childEl==null) return;

							childEl.applyStyles('height: 100%');                


						}
					}
			};

			modules[i] = module;

			var objParsSubMenus = {};
			objParsSubMenus.sUserLogin = NS.sUserLogin;
			objParsSubMenus.nModulo = result.listModulos[i].idComponente;

			var jsonParsSubMenus = Ext.util.JSON.encode(objParsSubMenus);







			SegMenuAction.obtenerArbolSubMenus(jsonParsSubMenus, function(result, e){
				if(!result){
					Ext.Msg.alert('SET','Error en acceso al menu desde la base de datos.');
					return;
				}

				if(result==''){
					return;
				}

				var resObject = Ext.util.JSON.decode(result);

				if(BFwrk.Util.isObjectEmpty(resObject.treeMenu)){
					return;
				}

				treeMenu[resObject.nModulo-1] = resObject.treeMenu;

				// ARBOL DE ACORDEON i
				rootTreeMenu[resObject.nModulo-1] = new Ext.tree.AsyncTreeNode({
					text: '<font color:"#A3BD31">' + 'ACORDEON ' + resObject.nModulo + '</font>',
					draggable: false,
					bodyStyle: 'background:#5B5B5E',
					id: 'rootTreeMenu'+resObject.nModulo,
					//***********************
					children: treeMenu[resObject.nModulo-1]   
				});


				// GENERAR LA INICIALIZACION DEL EL ARBOL
				treePanelMenu[resObject.nModulo-1] = new Tree.TreePanel({
					iconCls: 'menu_seguridad'
						,el: 'treeMenuDiv.Mod'+resObject.nModulo 
						,loader: new Ext.tree.TreeLoader()
				,bodyStyle: 'background:#5B5B5E'
					,listeners: {
						expand: function(pan, adjWidth, adjHeight, rawWidth, rawHeight){
							//alert('TreePanel resize: '+'w='+adjWidth+' h='+adjHeight+' - '+'rw='+rawWidth+' rh='+rawHeight);
							var el = pan.getEl();
							if(el==null) return;
							var childEl = el.first();
						}
					}
				,autoScroll:true, frame:false, border:false, lines:true
				,rootVisible: false, iconCls:'nav', root: rootTreeMenu[resObject.nModulo-1]
				});

				treePanelMenu[resObject.nModulo-1].render();
				rootTreeMenu[resObject.nModulo-1].expand();
				treePanelMenu[resObject.nModulo-1].show();


				//************************************
				// AGREGAR CODIGO PARA MANEJO DE LINK   
				//************************************

				treePanelMenu[resObject.nModulo-1].on('click', function(node, event){

					if(!node.isLeaf())
						return;

					var sUrlLnk = node.id.substring(node.id.indexOf('-')+1,node.id.length);
					var sTabIndex = NS.centerTabPanel.items.getCount();
					var sTabID = 'Tab'+node.id.substring(0, node.id.indexOf('-'));

					if(NS.allowDuplicateTabs)
						sTabID = sTabID+'.Ndx'+sTabIndex;

					NS.tabID = 'BFrmWork.'+sTabID;

					if(NS.centerTabPanel.findById(NS.tabID)!=null && !NS.allowDuplicateTabs){
						//alert('Ya existe este Tab');
						NS.centerTabPanel.activate(NS.tabID+'ItemId');
						return;
					}

					// AGREGAR EL TAB
					var tabComp = NS.centerTabPanel.add({
						id: NS.tabID,
						name: NS.tabID,
						itemId: NS.tabID+'ItemId',
						//frame: true,
						title: '<font color:"#A3BD31">' + node.text + '</font>',
						//iconCls: 'tabs',
						iconCls: node.attributes.iconCls,
						layout: 'fit',
						//layout:'absolute',
						closable: true,
						monitorResize: true,
						autoScroll: false,
						listeners: {
							render: function(p) {},
							single: true  // Remove the listener after first invocation
						}
					});

					tabComp.on('resize', function(panel, w, h) {
						var el = panel.getEl();
						if(el==null) return;

						var childEl = el.first('.x-panel-bwrap');
						if(childEl==null) return;

						childEl = childEl.first('.x-panel-body');
						if(childEl==null) return;

						childEl = childEl.first('.x-panel');
						if(childEl==null) return;

						var childPanelId = childEl.getAttributeNS('','id');
						if(childPanelId==null) return;

						Ext.getCmp(childPanelId).setHeight(h);
						Ext.getCmp(childPanelId).setWidth(w);
						Ext.getCmp(childPanelId).doLayout();
					});

					tabComp.show();


					var el = tabComp.getEl();
					if(el==null){
						alert('No fue posible obtener el elemento'); 
						return;
					}
					var childEl = el.first(); 
					if(childEl==null){
						alert('No fue posible obtener el elemento'); 
						return;
					}
					childEl = childEl.first();  
					if(childEl==null){
						alert('No fue posible obtener el elemento'); 
						return;
					}

					childEl.setHeight('100%');
					childEl.setWidth('100%');

					childEl.applyStyles('overflow:false');                

					NS.tabContainerId = childEl.getAttribute('id');

					BFwrk.Util.includeFile(sUrlLnk, NS.tabContainerId, {dom: true});
				}); // treePanelMenu[resObject.nModulo-1].on("click"...
			}); // SegMenuAction.obtenerArbolSubMenus(..
		} // END for(var i=0; i < result.listModulos.length; i++)..

		NS.accordionMenuPanel.add(modules); // agregarle los modulos cargados desde la bd
		NS.accordionMenuPanel.doLayout(); 
	}); // SegMenuAction.listaModulos(NS.sUserLogin, function(result, e)...

	/* Inicio Portal del tesorero
	 GGonzalez
	 */
	

	var empresas = '[{"noEmpresa": "1", "nomEmpresa": "-------------------------------TODAS-------------------------------"},{"noEmpresa": "2", "nomEmpresa": "GLOBAL SET"},{"noEmpresa": "3", "nomEmpresa": "WEBSET"},{"noEmpresa": "3", "nomEmpresa": "NETROSET"}]';
	NS.storeEmpresaP = new Ext.data.JsonStore({
		fields: [
		         {type: 'int', name: 'noEmpresa'},
		         {type: 'string', name: 'nomEmpresa'}
		         ]
	});
	NS.lblGrupoEmpresasP = new Ext.form.Label({
		text : 'Grupo de empresas:',
		x : 50,
		y : 3,
		width : 130
	});

	NS.lblEmpresaP = new Ext.form.Label({
		text : 'Empresa:',
		x : 550,
		y : 3
	});

	NS.txtNoGrupoEmpresasP = new Ext.form.NumberField({
		name : PF + 'txtNoGrupoEmpresasP',
		id : PF + 'txtNoGrupoEmpresasP',
		x : 50,
		y : 20,
		width : 70,
		listeners : {
			change : {
				fn : function() {
					if (Ext.getCmp(PF + 'txtNoGrupoEmpresasP').getValue() !== "") {

						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+ 'txtNoGrupoEmpresasP',NS.cmbGrupoEmpresasP.getId());
						PF + 'txtNoGrupoEmpresasP'.focus();

					} else {

						Ext.getCmp(NS.cmbGrupoEmpresasP.getId())
						.setValue('');
						Ext.getCmp(NS.txtNoEmpresaP.getId())
						.setValue('');
						Ext.getCmp(NS.cmbEmpresaP.getId()).setValue('');
						NS.storeEmpresaP.removeAll();

					}// End if

				}// End fn

			}
	// End change

		}
	// End listeners

	});// NS.txtNoGrupoEmpresas
	NS.cmbEmpresaP = new Ext.form.ComboBox({
		name : PF + 'cmbEmpresaP',
		id : PF + 'cmbEmpresaP',
		store : NS.storeEmpresaP,
		mode : 'local',
		valueField : 'noEmpresa',
		displayField : 'nomEmpresa',
		emptyText : 'Seleccione una empresa',
		triggerAction : 'all',
		x : 620,
		y : 20,
		width : 330,
		listeners : {
			select : {
				fn : function(combo) {
					BFwrk.Util.updateComboToTextField(PF+ 'txtEmpresaP', NS.cmbEmpresaP.getId());
				}
			}

		}
	});
	NS.txtEmpresaP = new Ext.form.NumberField({
		name : PF + 'txtEmpresaP',
		id : PF + 'txtEmpresaP',
		x : 550,
		y : 20,
		width : 60,
		listeners : {
			change : {
				fn : function() {

					if (Ext.getCmp(PF + 'txtNoEmpresaP') .getValue() !== "") {
						// alert(NS.cmbEmpresa.getId());
						var valueCombo = BFwrk.Util .updateTextFieldToCombo(PF + 'txtEmpresaP', NS.cmbEmpresaP.getId());
						PF + 'txtEmpresaP'.focus();

					} else {
						// alert("En ELSE");
						Ext.getCmp(NS.cmbEmpresaP.getId()) .setValue('');

					}// End if

				}// End fn

			}
	// End change
		}
	// End listeners
	});// End NS.txtNoEmpresa


	var grupos = '[{"id_super_grupo": "1", "desc_super_grupo": "CONSOLIDADO"}, {"id_super_grupo": "2", "desc_super_grupo": "COMERCIALIZADORA"}, {"id_super_grupo": "3", "desc_super_grupo": "CONCESIONARIA"}, {"id_super_grupo": "4", "desc_super_grupo": "SERVICIOS"}, {"id_super_grupo": "5", "desc_super_grupo": "INMOBILIARIA"}]';
	NS.storeGrupos = new Ext.data.JsonStore({
		fields: [
		         {type: 'int', name: 'id_super_grupo'},
		         {type: 'string', name: 'desc_super_grupo'}
		         ]
	});
	NS.storeGrupos.loadData(Ext.decode(grupos));

	NS.cmbGrupoEmpresasP = new Ext.form.ComboBox({
		name : PF + 'cmbGrupoEmpresasP',
		id : PF + 'cmbGrupoEmpresasP',
		store :	NS.storeGrupos,
		mode : 'local',
		valueField : 'id_super_grupo',
		displayField : 'desc_super_grupo',
		emptyText : 'Seleccione un grupo',
		triggerAction : 'all',
		x : 130,
		y : 20,
		width : 310,
		listeners : {
			select : {
				fn : function() {
					BFwrk.Util.updateComboToTextField(PF + 'txtNoGrupoEmpresasP',NS.cmbGrupoEmpresasP.getId());
					Ext.getCmp(NS.txtEmpresaP.getId()).setValue('');
					NS.storeEmpresaP.removeAll();
					NS.storeEmpresaP.sort('NO_EMPRESA', 'ASC');
					NS.storeEmpresaP.loadData(Ext.decode(empresas));
				}// end function

			},// end select
			change : {
				fn : function(combo, newValue, oldValue) {

					if (newValue == "") {
						// alert("change de Grupo");
						Ext.getCmp(NS.txtNoGrupoEmpresasP.getId()).setValue('');
						Ext.getCmp(NS.txtEmpresaP.getId()).setValue('');
						Ext.getCmp(NS.cmbEmpresaP.getId()).setValue('');
						NS.storeEmpresaP.removeAll();

					}// end if

				}// end function

			}
			// end change

		}
	// end listeners

	});// NS.cmbGrupoEmpresas


	NS.lblPeriodoP = new Ext.form.Label({
		text : 'Periodo:',
		x : 50,
		y : 50
	});
	NS.lblFechaIniP = new Ext.form.Label({
		text : 'Fecha Inicio:',
		x : 50,
		y : 75
	});
	NS.btnBuscarI = new Ext.Button({
		xtype : 'button',
		id : PF + 'btnBuscarI',
		name : PF + 'btnBuscarI',
		text : 'Ver Indicadores',
		x : 850,
		y : 70,
		width : 80,
		listeners : {
			click : {
				fn : function(e) {
					if(Ext.getCmp(PF + 'cmbGrupoEmpresasP').getValue()==""){
						Ext.Msg.alert("SET","Seleccione un grupo de empresas.");
						return;
					}
					else if(Ext.getCmp(PF + 'cmbEmpresaP').getValue()==""){
						Ext.Msg.alert("SET","Seleccione una empresa.");
						return;
					}
					else
						NS.accionarEmpresaRaiz(NS.cmbEmpresaP.getValue());
				}
			}
		}
	});
	NS.txtFechaIniP = new Ext.form.DateField({
		id : PF + 'txtFechaIniP',
		name : PF + 'txtFechaIniP',
		format : 'd/m/Y',
		x : 130,
		y : 70,
		allowBlank : false,
		width : 100,
		vtype : 'daterange',
		listeners : {
			render : function(datefield) {
				datefield.setValue(NS.fecHoy);
			},
			change : {
				fn : function(caja, valor) {
					var fechaFin = Ext.getCmp(
							PF + 'txtFechaFinP').getValue();
					if (caja.getValue() == null) {
						Ext.getCmp(PF + 'txtFechaIniP')
						.setValue(NS.fecHoy);
						Ext.getCmp(PF + 'txtFechaIniP').focus();
					} else {
						if (fechaFin < caja.getValue()) {
							BFwrk.Util
							.msgShow(
									'La fecha de inicial no puede ser mayor a la final.',
							'ERROR');
							Ext.getCmp(PF + 'txtFechaIniP')
							.focus();
							Ext.getCmp(PF + 'txtFechaIniP')
							.setValue(NS.fecHoy);
							return;
						} 
					}
				}
			},
			blur : function(datefield) {
				if (datefield.getValue() == null) {
					datefield.setValue(NS.fecHoy);
				}

			},
		}
	});
	// fecha fin
	NS.lblFechaFinP = new Ext.form.Label({
		text : 'Fecha Final:',
		x : 300,
		y : 75
	});
	NS.txtFechaFinP = new Ext.form.DateField(
			{
				id : PF + 'txtFechaFinP',
				name : PF + 'txtFechaFinP',
				format : 'd/m/Y',
				x : 370,
				y : 70,
				width : 100,
				altFormats : 'd-m-Y',
				vtype : 'daterange',
				listeners : {
					render : function(datefield) {
						datefield.setValue(NS.fecHoy);
					},
					change : {
						fn : function(caja, valor) {
							var fechaIni = Ext.getCmp(
									PF + 'txtFechaIniP').getValue();
							if (caja.getValue() == null) {
								Ext.getCmp(PF + 'txtFechaFinP')
								.setValue(NS.fecHoy);
								Ext.getCmp(PF + 'txtFechaFinP').focus();
							} else {
								if (fechaIni > caja.getValue()) {
									BFwrk.Util
									.msgShow(
											'La fecha de final no puede ser menor a la inicial.',
									'ERROR');
									Ext.getCmp(PF + 'txtFechaFinP')
									.focus();
									Ext.getCmp(PF + 'txtFechaFinP')
									.setValue(NS.fecHoy);
									return;
								} 
							}
						}
					},
					blur : function(datefield) {
						if (datefield.getValue() == null) {
							datefield.setValue(NS.fecHoy);
						}
					},
				}
			});

	NS.iniciarArbol = function()
	{
		NS.treePanelMenuP = new Ext.tree.TreePanel({
			id : PF + 'treePanelMenuP',
			name : PF + 'treePanelMenuP',
			useArrows: true,
			autoScroll: true,
			animate: true,
			enableDD: true,
			containerScroll: true,
			bodyStyle: 'background: #5B5B5E',
			boxMaxHeight:260,
			height: 260,
			border: false,
			x: 0,
			y: 0,
			loader: new Ext.tree.TreeLoader(),
			listeners: 
			{
				expand: function(pan, adjWidth, adjHeight, rawWidth, rawHeight)
				{
					
				},
				click: function(node)
				{	
					NS.treePanelMenuP.getSelectionModel().clearSelections(true);
					$('.Seleccionado').removeClass('Seleccionado');
					//alert(node.qtipCfg)
					NS.treePanelMenuP.getSelectionModel().select(node);
					node.setCls('Seleccionado');

					var selectedItem = NS.treePanelMenuP.getSelectionModel().getSelectedNode();
					var rutaNodos = '';
					var ruta = '';
					rutaNodos = selectedItem.getPath();

					NS.iIdEmpActual = selectedItem.id;

					if(selectedItem.parentNode === null)
						NS.iIdEmpPadre = 0;
					else
						NS.iIdEmpPadre = selectedItem.parentNode.id;	

					//alert('nodoActual ' + NS.iIdEmpActual);
					//alert('nodoPadre' + NS.iIdEmpPadre);
//					alert('selectedItem.parentNode.id ' + selectedItem.id);

					ruta += rutaNodos.substring(rutaNodos.indexOf('/Empresas/') + 10) 
					//alert('ruta: ' + ruta);	
					NS.sRuta = ruta;
					NS.iIdEmpresaPadre = selectedItem.id;
					
				}
			}
		});
	};


	NS.iniciarArbol();
	NS.accionarEmpresaRaiz = function(valueCombo)
	{


		var response="{id: 1,descripcion: [" +
		"{id:2,text:'1.TASA Y TIPOS DE CAMBIO',singleClickExpand: false,allowDrag: false,children: [" +
		"{id:3,text:'TASAS <img src=img/icons/rojo2.png width=15 height=15 border=0 />',singleClickExpand: false, allowDrag: false, children:" +
		"[{id:4,text:'CETES A 28 DÍAS: 6.96',singleClickExpand: true, allowDrag: false,leaf:true}," +
		"{id:5,text:'LIBOR 3 MESES: 1.32',singleClickExpand: true, allowDrag: false,leaf:true}," +
		"{id:6,text:'TASA INTERBANCARIA: 7.37',singleClickExpand: true, allowDrag: false,leaf:true}," +
		"{id:5,text:'TASA DE INVERSIÓN DE TESORERÍA: 2.08',singleClickExpand: true, allowDrag: false,leaf:true}]}," +
		"{id:7,text:'DIVISAS <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: false, allowDrag: false, children:" +
		"[" +
		"{id:8,text:'DÓLARES <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,children:[" +
		"{id:10,text:'Pactado en tesorería: 17.78',singleClickExpand: true, allowDrag: false,leaf:true}," +
		"{id:11,text:'Fix: 17.96',singleClickExpand: true, allowDrag: false,leaf:true},"+
		"{id:12,text:'Promedio Pactado: 17.68',singleClickExpand: true, allowDrag: false,leaf:true},"+
		"{id:13,text:'Máximo: 17.98',singleClickExpand: true, allowDrag: false,leaf:true},"+
		"{id:14,text:'Mínimo: 17.40',singleClickExpand: true, allowDrag: false,leaf:true}"+
		"]}," +
		"{id:15,text:'EUROS <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,children:[" +
		"{id:16,text:'Pactado en tesorería: 21.40',singleClickExpand: true, allowDrag: false,leaf:true}," +
		"{id:17,text:'Fix: 21.16',singleClickExpand: true, allowDrag: false,leaf:true},"+
		"{id:18,text:'Promedio Pactado: 21.56',singleClickExpand: true, allowDrag: false,leaf:true},"+
		"{id:19,text:'Máximo: 21.02',singleClickExpand: true, allowDrag: false,leaf:true},"+
		"{id:20,text:'Mínimo: 22.10',singleClickExpand: true, allowDrag: false,leaf:true}"+
		"]}," +
		"]}," +
		"]}]}";
		var jSonMenu = Ext.util.JSON.decode(response);
		if(response === null)
		{
			BFwrk.Util.msgShow('No hay una estructura para esta empresa', 'INFO');
			return;
		}

		var arbol = jSonMenu.descripcion;
		NS.indicadores = new Ext.tree.AsyncTreeNode({
			text: 'Indicadores',
			id: PF+'indicadores',
			id: PF+'indicadores',
			expanded:true,
			allowChildren:true,
			children: arbol,
			//disabled:true,
			listeners: 
			{
				click: function(node, event) 
				{

				}
			}
		});
		NS.treePanelMenuP.setRootNode(NS.indicadores);	
		NS.treePanelMenuP.render(PF + 'panelIndicadores');
		NS.indicadores.expand();
		var treeNode = NS.treePanelMenuP.getRootNode();
		treeNode.appendChild({
			id: PF+'c',
			name: PF+'c',
			text: '2. OPERACIÓN BANCARIA',
			singleClickExpand: false,
			allowDrag: false,
			children:
				[
				 {id:5,text:'PAGOS <img src=img/icons/rojo2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					 children:[{id:30,text:'Transferencias por autorizar: 5 de 12 ',singleClickExpand: true, allowDrag: false,leaf:true},
					           {id:31,text:'Enviadas: 2 de 12',singleClickExpand: true, allowDrag: false,leaf:true},
					           {id:31,text:'Por enviar: 3 de 12',singleClickExpand: true, allowDrag: false,leaf:true},
					           {id:32,text:'Confirmadas: 2 de 12',singleClickExpand: true, allowDrag: false,leaf:true}]},
					           {id:33,text:'FONDEO <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:34,text:'Fondeado: 3 de 3',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:35,text:'Pendiente de fondeo: 0 de 3',singleClickExpand: true, allowDrag: false,leaf:true}]
					           },
					           {id:36,text:'DEPÓSITOS <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:37,text:'Identificados: 3 de 6',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:38,text:'No identificados: 3 de 6',singleClickExpand: true, allowDrag: false,leaf:true}]
					           },
					           {id:39,text:'TRASPASOS (Misma empresa) <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:40,text:'Enviados: 10 de 10',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:41,text:'Confirmados: 10 de 10',singleClickExpand: true, allowDrag: false,leaf:true}]
					           },
					           {id:42,text:'TRASPASOS (Interempresas) <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:43,text:'Enviados: 12 de 12',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:44,text:'Confirmados: 12 de 12',singleClickExpand: true, allowDrag: false,leaf:true}]
					           },
					           {id:45,text:'CHEQUES <img src=img/icons/rojo2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:46,text:'Por firmar: 30 de 150',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:47,text:'Impresos no entregados: 10 de 150',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:48,text:'Entregados: 20 de 150',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:49,text:'En tránsito 90 de 150',singleClickExpand: true, allowDrag: false,leaf:true}]
					           },
					           {id:50,text:'IDENTIFICACIÓN DE COMISIONES <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:51,text:'Esperadas: 10 de 20',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:52,text:'Confirmadas: 9 de 20',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:53,text:'No reconocidas: 1 de 20',singleClickExpand: true, allowDrag: false,leaf:true}]
					           },
					           {id:54,text:'CUENTA CORRIENTE<img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:55,text:'Por pagar: 0 de 50',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:56,text:'Enviada: 25 de 50',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:57,text:'Confirmada:25 de 50',singleClickExpand: true, allowDrag: false,leaf:true}]
					           },
					           {id:58,text:'ARCHIVOS DE BANCO <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:59,text:'Enviados: 30 de 30',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:60,text:'Por enviar: 0 de 30',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             ]
					           },
					           {id:74,text:'ESTADOS DE CUENTA RECIBIDOS <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:75,text:'Diarios: 6 de 6',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:76,text:'Mismo día: 10 de 10',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             ]
					           },

					           {id:64,text:'EXCEDENTES EN INVERSIONES ESTABLECIDAS <img src=img/icons/rojo2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:65,text:'Establecidas: 5 de 32',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:66,text:'Confirmadas: 4 de 32',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:700,text:'Por vencer:9 de 32',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             ]
					           },
					           {id:67,text:'PAGO DE DEUDA <img src=img/icons/rojo2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:68,text:'Cubierta: 14 de 56',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:69,text:'No cubierta: 10 de 56',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:70,text:'Por vencer:42 de 56',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             ]
					           },
					           {id:71,text:'DIVISAS <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
					        	   children:[{id:72,text:'Cubierta: 2 de 2',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             {id:73,text:'No cubierta: 0 de 2',singleClickExpand: true, allowDrag: false,leaf:true},
					        	             ]
					           }]
		});
		treeNode.appendChild({
			id: 'c42',
			text: '3. GESTIÓN Y TOMA DE DECISIONES',
			children:
				[
				 {id:78,text:'SALDO PROMEDIO DE CHEQUERAS <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false,
					 children:[{id:79,text:'TOTAL $5,171.66 MN',singleClickExpand: true, allowDrag: false, children:[{
						 id:80,text:'GLOBAL SET $5,171.66 MN <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false, children:[{
							 id:80,text:'BANCOMER',singleClickExpand: true, allowDrag: false,children:[
							                                                                           {id:80,text:'99934343434: $6,804.22 MN',singleClickExpand: true, allowDrag: false, leaf:true},
							                                                                           {id:80,text:'94546565677: $3,539.11 MN',singleClickExpand: true, allowDrag: false, leaf:true},
							                                                                           ]
						 }]}
					 ]},
					 ]},
					 {id:83,text:'SALDO PROMEDIO DE INVERSIONES <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
						 children:[{id:790,text:'TOTAL $620,056,060.00 MN',singleClickExpand: true, allowDrag: false, children:[{
							 id:800,text:'GLOBAL SET $620,056,060.00 MN <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false, children:[                                                                                                                   
							                                                                                                                                                                 {id:801,text:'HSBC',singleClickExpand: true, allowDrag: false,children:[
							                                                                                                                                                                                                                                         {id:802,text:'34567833223: $784,545,787.00 MN',singleClickExpand: true, allowDrag: false, leaf:true},]
							                                                                                                                                                                 },                                                                                                                   
							                                                                                                                                                                 {id:803,text:'SANTANDER',singleClickExpand: true, allowDrag: false,children:[
							                                                                                                                                                                                                                                              {id:804,text:'23234456657: $455,566,333.00 MN',singleClickExpand: true, allowDrag: false, leaf:true},]
							                                                                                                                                                                 }]}
						 ]},
						 ]
					 },
					 {id:86,text:'SALDO PROMEDIO DE DEUDA <img src=img/icons/rojo2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
						 children:[{id:792,text:'TOTAL $6,060.00 MN',singleClickExpand: true, allowDrag: false, children:[{
							 id:805,text:'GLOBAL SET $6,060.00 MN <img src=img/icons/rojo2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false, children:[                                                                                                                   
							                                                                                                                                                        {id:801,text:'HSBC',singleClickExpand: true, allowDrag: false,children:[
							                                                                                                                                                                                                                                {id:806,text:'34567833223: $5,787.00 MN',singleClickExpand: true, allowDrag: false, leaf:true},]},                                                                                                                   
							                                                                                                                                                                                                                                {id:807,text:'SANTANDER',singleClickExpand: true, allowDrag: false,children:[
							                                                                                                                                                                                                                                                                                                             {id:804,text:'23234456657: $6,333.00 MN',singleClickExpand: true, allowDrag: false, leaf:true},]
							                                                                                                                                                                                                                                }]}
						 ]},
						 ]
					 },
					 {id:89,text:'POSICIÓN BANCARIA SALDOS <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false,
						 children:[{id:79,text:'TOTAL $113,560,343.33 MN',singleClickExpand: true, allowDrag: false, children:[{
							 id:80,text:'GLOBAL SET $113,560,343.33 MN <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false, children:[{
								 id:80,text:'BANCOMER',singleClickExpand: true, allowDrag: false,children:[
								                                                                           {id:80,text:'99934343434: $4,585,804.88 MN',singleClickExpand: true, allowDrag: false, leaf:true},
								                                                                           {id:80,text:'94546565677: $108,974,538.45 MN',singleClickExpand: true, allowDrag: false, leaf:true},
								                                                                           ]
							 }]}
						 ]},
						 ]},
						 {id:189,text:'FLUJO DE EFECTIVO <img src="img/icons/verde2.png"  width="15" height="15" border="0"  />',singleClickExpand: true, allowDrag: false,
							 children:[{id:94,text:'Flujo Original vs. Flujo Real <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,
								 children:[{
									 id:80,text:'GLOBAL SET <img src=img/icons/verde2.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false, children:[{
										 id:880,text:'Original: $124,566,887.00',singleClickExpand: true, allowDrag: false,leaf:true},
										 { id:881,text:'Real: $124,566,000.00',singleClickExpand: true, allowDrag: false,leaf:true}
										 ]}
								 ]},
								 {id:95,text:'Flujo Original vs. Flujo Ajustado <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,children:[
{
	id:80,text:'GLOBAL SET <img src=img/icons/verde2.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false, children:[{
		id:882,text:'Original: $124,566,887.00',singleClickExpand: true, allowDrag: false,leaf:true},
		{ id:883,text:'Ajustado: $124,566,333.00',singleClickExpand: true, allowDrag: false,leaf:true}
		]}
]},
{id:96,text:'Flujo Ajustado vs. Flujo Real <img src=img/icons/verde2.png width=15 height=15 border=0 />',singleClickExpand: true, allowDrag: false,children:[
{
	id:884,text:'GLOBAL SET <img src=img/icons/verde2.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false, children:[{
		id:885,text:'Ajustado: $124,566,333.00',singleClickExpand: true, allowDrag: false,leaf:true},
		{ id:886,text:'Real: $124,566,000.00',singleClickExpand: true, allowDrag: false,leaf:true}
		]}
]},]},
{id:97,text:'SALDO DE CUENTA CORRIENTE  <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false,
	children:[{id:123,text:'GLOBAL SET <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false,  
		children:[{id:134,text:'NETROSET <img src="img/icons/verde2.png"  width="15" height="15" border="0"  />',singleClickExpand: true, allowDrag: false,
			children:[{id:138,text:'Coinversión: $944.90',singleClickExpand: true, allowDrag: false,leaf:true},
			          {id:130,text:'Préstamos: $0.00',singleClickExpand: true, allowDrag: false, leaf:true}]},
			          {id:144,text:'WEBSET <img src="img/icons/rojo2.png"  width="15" height="15" border="0"  />',singleClickExpand: true, allowDrag: false,
			        	  children:[{id:158,text:'Coinversión: $0.00',singleClickExpand: true, allowDrag: false,leaf:true},
			        	            {id:161,text:'Préstamos:$1,000.00',singleClickExpand: true, allowDrag: false, leaf:true}]}]
	},
	]},
	{id:102,text:'PARTIDAS EN CONCILIACIÓN <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false,
		children:[{id:103,text:'GLOBAL SET <img src=img/icons/amarillo.png width=17 height=17 border=0 />',singleClickExpand: true, allowDrag: false,  
			children:[{id:124,text:'NETROSET <img src="img/icons/rojo2.png"  width="15" height="15" border="0"  />',singleClickExpand: true, allowDrag: false,
				children:[{id:108,text:'Pendientes de conciliación: 3 de 22',singleClickExpand: true, allowDrag: false,leaf:true},
				          {id:110,text:'$234,677.90',singleClickExpand: true, allowDrag: false, leaf:true}]},
				          {id:104,text:'WEBSET <img src="img/icons/verde2.png"  width="15" height="15" border="0"  />',singleClickExpand: true, allowDrag: false,
				        	  children:[{id:128,text:'Pendientes de conciliación: 0 de 12',singleClickExpand: true, allowDrag: false,leaf:true},
				        	            {id:111,text:'$444,234.90',singleClickExpand: true, allowDrag: false, leaf:true}]}]
		},
		]}
	]

		});
		NS.treePanelMenuP.show();
		
	};
	
	NS.formPortalTesorero = new Ext.form.FormPanel({
		title : 'Portal del Tesorero',
		autowidth : true,
		height : 490,
		padding : 10,
		layout : 'absolute',
		id : PF + 'formPortalTesorero',
		name : PF + 'formPortalTesorero',
		//	renderTo:NS. panPortalTesorero ,
		frame : true,
		autoScroll : true,
		items : [
		         {
		        	 xtype : 'fieldset',
		        	 title : 'Búsqueda',
		        	 id : PF + 'panelEmpresa',
		        	 name : PF + 'panelEmpresa',
		        	 x : 0,
		        	 y : 0,

		        	 autowidth : true,
		        	 height : 135,
		        	 layout : 'absolute',
		        	 items : [NS.lblGrupoEmpresasP,NS.lblEmpresaP,NS.txtNoGrupoEmpresasP, NS.txtEmpresaP,NS.cmbGrupoEmpresasP, NS.cmbEmpresaP,
		        	          NS.lblFechaFinP,NS.txtFechaFinP,NS.lblFechaIniP,NS.txtFechaIniP,NS.lblPeriodoP,NS.btnBuscarI]
		         },
		         {
		        	 xtype : 'fieldset',
		        	 title : 'Indicadores',
		        	 id : PF + 'panelIndicadores',
		        	 name : PF + 'panelIndicadores',
		        	 x : 0,
		        	 y : 140,

		        	 autowidth : true,
		        	 height : 10,
		        	 layout : 'absolute',
		        	 items : []
		         },],
		         buttonAlign: 'center',
		         buttons:[
		                  {
		                	  text: 'Limpiar',
		                	  handler: function(){
		                		  NS.txtNoGrupoEmpresasP.setValue("");
		                		  NS.txtEmpresaP.setValue("");
		                		  NS.cmbEmpresaP.reset();
		                		  NS.cmbGrupoEmpresasP.reset();	
		                		  NS.treePanelMenuP.hide();
		                		  Ext.getCmp(PF + 'txtFechaIniP')
		                		  .setValue(NS.fecHoy);
		                		  Ext.getCmp(PF + 'txtFechaFinP')
		                		  .setValue(NS.fecHoy);}
		                  },{
		                	  text: 'Cerrar',
		                	  handler: function(){
		                		  NS.txtNoGrupoEmpresasP.setValue("");
		                		  NS.txtEmpresaP.setValue("");
		                		  NS.cmbEmpresaP.reset();
		                		  NS.cmbGrupoEmpresasP.reset();
		                		  NS.treePanelMenuP.hide();
		                		  Ext.getCmp(PF + 'txtFechaIniP')
		                		  .setValue(NS.fecHoy);
		                		  Ext.getCmp(PF + 'txtFechaFinP')
		                		  .setValue(NS.fecHoy);
		                		  Ext.getCmp(PF +'panBienvenido').setVisible(true);
		                	  }
		                  },
		                  ]
	});

	NS.panPortalTesorero = new Ext.Panel({
		title: 'Portal del Tesorero',
		id : PF + 'panPortalTesorero',
		name : PF + 'panPortalTesorero',
		bodyStyle: 'background: #5B5B5E',
		autoScroll:true,
		//	height : 1000,
		items:[NS.formPortalTesorero],

	});

	NS.centerTabPanel = new Ext.TabPanel({
		id: 'idBfrmwrkTabPanel',
		name: 'idBfrmwrkTabPanel',
		region:'center',
		//deferredRender:false,
		bufferResize: true,
		activeTab:0,
		enableTabScroll:true,
		autoScroll:false,
		defaults: {autoScroll:true},
		bodyStyle: 'background:#A3BD31',
		activeTab: 0,
		listeners: {
			add: function(panel, component, index) {
			}
		},//Esta es la pestaña de Bienvenida
		items:[
		       NS.panBienvenido,
		    //   NS.panPortalTesorero

		       ]
	});

	var data = [['SPEI',250100],['CHEQUE',150000],['EFECT',75000],['TEF',95000]];
	var data1 = [['BNMX',98],['BCRM',616],['HSBC',380],['STDR',70],['DEUT',900]];
	var store = new Ext.data.ArrayStore({fields:[{name:'FORMASPAGO'},{name:'FORMAS', type:'float'}]});
	var store1 = new Ext.data.ArrayStore({fields:[{name:'framework'},{name:'users', type:'float'}]});

	store.loadData(data);
	store1.loadData(data1); 

	var pieChart = new Ext.chart.PieChart({
		store: store,
		width: 250,
		height: 270,
		store: store,
		dataField: 'FORMAS',
		categoryField: 'FORMASPAGO',
		seriesStyles: {
			colors:['#0E5391','#809708','#FFCA20','#8D0E1B','#878787','#BFD4F0']
		},
		fieldLabel: 'FORMAS DE PAGO',
		extraStyle: {
			animationEnabled:true,
			legend:{
				display: 'bottom',
				padding: 5,
				border:{
					color: '#878787',
					size: 1
				},
				font:{
					size:'9',
					color:'#15428B',
					name: 'Arial'
				}
			},
			dataTip :{
				font:{
					color:'#D6202C'
				}
			}
		}
	});



	var columnChart = new Ext.chart.ColumnChart({
		store: store1,
		//url:'../ext-3.0-rc1/resources/charts.swf',
		xField: 'framework',
		yField: 'users',
		width: 340,
		height: 270,
		seriesStyles: {
			colors:[
			        '#0E5391','#809708',
			        '#FFCA20','#E37703',
			        '#8D0E1B','#878787','#BFD4F0'
			        ] 
		},
		extraStyle: {
			xAxis: new Ext.chart.CategoryAxis({
				title: 'Month'
			}),
			colors:[
			        '#0E5391','#809708',
			        '#FFCA20','#E37703',
			        '#8D0E1B','#878787','#BFD4F0'
			        ],        	
			        animationEnabled: true, 
			        dataTip :{
			        	font:{
			        		color:'#D6202C'
			        	}
			        },
			        font:{
			        	size:'9',
			        	color:'#15428B',
			        	name: 'Arial'
			        }
		}
	});

	NS.panel1 = new Ext.Panel({
		id: 'panel1',
		title: 'Pie',
		bodyStyle: 'background:#E9EFD1',
		forceLayout: true,
		html: ' '
	});
	NS.panel2 = new Ext.Panel({
		id: 'panel2',
		title: 'Barras',
		bodyStyle: 'background:#E9EFD1',
		forceLayout: true,
		html: ' '
	});
	NS.panel3 = new Ext.Panel({
		id: 'panel3',
		title: 'Lineal',
		bodyStyle: 'background:#E9EFD1',
		forceLayout: true,
		html: ' '
	});

//	Este es el contenedor principal
	NS.viewport = new Ext.Viewport({
		layout:'border',
		items:[
		       {
		    	   region:'north',
		    	   contentEl: 'north',
		    	   split:true,
		    	   height: 65,
		    	   minSize: 65,
		    	   maxSize: 65,
		    	   collapsible: true,
		    	   collapsed: false,
		    	   title:'WEBSET',
		    	   margins:'0 0 0 0',
		    	   id: 'viewPortNorth',
		    	   name: 'viewPortNorth',
		    	   layout: 'absolute',
		    	   bodyStyle: 'background:#E9EFD1'
		       },
		       NS.accordionMenuPanel,
		       NS.centerTabPanel,
		       {
		    	   region:'east',
		    	   title: '',
		    	   collapsible: true,
		    	   collapsed: true,
		    	   split:true,
		    	   width: 600,
		    	   minSize: 600,
		    	   maxSize: 600,
		    	   layout:'fit',
		    	   margins:'0 5 0 0',
		    	   items:
		    		   new Ext.TabPanel({
		    			   border:false,
		    			   activeTab:0,
		    			   tabPosition:'bottom',
		    			   bodyStyle: 'background:#5B5B5E',
		    			   items:[NS.panel1, NS.panel2, NS.panel3]
		    		   })
		       },
		       {
		    	   region:'south',
		    	   contentEl: 'south',
		    	   split:true,
		    	   height: 50,
		    	   minSize: 50,
		    	   maxSize: 100,
		    	   collapsible: true,
		    	   collapsed: true,
		    	   title:'Versión 3 Release 5',
		    	   margins:'0 0 0 0',
		    	   id: 'viewPortSouth',
		    	   name: 'viewPortSouth',
		    	   bodyStyle: 'background:#5B5B5E'
		       }
		       ]
	});

//	*******************************************************************

//	Handle this change event in order to restore the UI to the appropriate history state
	Ext.History.on('change', function(token){
		if(token){
			var parts = token.split(NS.tabTokenDelimiter);
			var tabPanel = Ext.getCmp(parts[0]);
			var tabId = parts[1];

			if(tabPanel!=null){
				tabPanel.show();
				tabPanel.setActiveTab(tabId);
			}
		}else{
			// This is the initial default state.  Necessary if you navigate starting from the
			// page without any existing history token params and go back to the start state.
			tp.setActiveTab(0);
			tp.getItem(0).setActiveTab(0);
		}
	});

	cerrarSession = function(tipoMsg) {
		if(tipoMsg)
			alert('Lo sentimos su sesión a expirado, tiene que volver a registrarse en el sistema');

		GlobalAction.quitarUsuarioActivo(function(result, e) {
			Ext.Msg.alert('SET', 'Sesión Terminada');
			var redirect = 'login.jsp';
			window.location = redirect;
		});
	};

	cambioEmpresa = function() {
		NS.storeCajas.load();
		NS.storeEmpresas.load();
		NS.winCambioEmpresa.show();
	};

	function getAttention(window) {
		window.focus(); 
		var i = 0;
		var show = ['Evite cierre de sesión', window.document.title];
		var focusTimer = setInterval(function() {
			if (window.closed) {
				clearInterval(focusTimer);
				return;
			}
			window.document.title = show[i++ % 2] ;
		}, 1000);

		window.document.onmousemove = function() { 
			clearInterval(focusTimer); 
			window.document.title = show[1] ;
			window.document.onmousemove = null ;
		} 
	}



	alerta = function() {
		//llamamos la ventana emergente
		getAttention(window);
		//Se le manda una alerta al usuario avisando que se cerrara su sesión en 2 min esto en milisegundos
		timeOut = window.setTimeout("cerrarSession(true)", 120000);
		Ext.Msg.alert('SET', 'La sesión expirara por inactividad en 2 minutos, para evitarlo de un click en cualquier opción del menu');
	};
}); // END  Ext.onReady(function()... 

//Funcion que establece el tiempo con el que va a contar el usuario de inactividad,
//cuando se llegue a ese tiempo preguntara que si desea continuar en el sistema o salir de el.
function startTime() {
	//Establecemos el tiempo de 15 min para el cierre de la sesión esto es en milisegundos 900000
	timeOut = window.setTimeout("alerta()", 32400000);
	//timeOut = window.setTimeout('window.location = "login.jsp";',20000);
};

//Si se detecta un movimiento dentro del sistema se detiene el tiempo y se comienza de nuevo.
function stopAndStartTime() {
	window.clearTimeout(timeOut);
	startTime();
};

//Al cargarse la página comienza a correr el tiempo establesido een la funcion de startTime 
window.onload = startTime();
