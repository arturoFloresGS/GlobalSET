Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Seguridad.MantenimientoUsuarios.SeguridadUsuario');
	 
	 // EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	 NS.tabContId = apps.SET.tabContainerId;
	 var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
	 
	 Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	 
	 Ext.QuickTips.init();
	 NS.pBanModificar = false;
	 NS.idUsuarioM = 0;
	 NS.fecHoy = apps.SET.FEC_HOY;
	 
	 //YEC VALIDACION DE CAMPOS
	 var error='';
	 NS.correoValido=false;
	 NS.contrasenaValida=false;
	 NS.componenteValidos=false;
	 NS.banderaOpcion=''; //08-12-2015
	 NS.bOpcion=0;
	
	NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	
	NS.idUsuarioModificado='';
	NS.bchkModificar=0;
	
	/*******************************************
	 * Panel busqueda
	 */
	
	//Combo clave usuario
	
	NS.lbClaveUsuarioB = new Ext.form.Label({
		id: PF+ 'lbClaveUsuarioB',
		text: 'Clave Usuario:',
        x: 0,
        y: 0	
	});
	
	NS.lbNombreB = new Ext.form.Label({
		id: PF+ 'lbNombreB',
		text: 'Nombre:',
        x: 245,
        y: 0	
	});
	
	NS.lbEstatusB = new Ext.form.Label({
		id: PF+ 'lbEstatusB',
		text: 'Estatus: ',
        x: 550,
        y: 0	
	});
	
	NS.storeNombreUsuario = new Ext.data.DirectStore( {
		 paramsAsHash: false
		,root: ''
		,directFn: SeguridadUsuarioAction.obtenerUsuarios
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
					
					// RECARGAR GRID
					NS.storeUsuarios.baseParams.idUsuario = combo.getValue();
					NS.storeUsuarios.baseParams.estatus = '';
					NS.myMask.show();
					NS.storeUsuarios.load();
					
					Ext.getCmp(PF + 'cmbEstatus').reset();				
				}
			}
		}
	});
	
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
		,width :230
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
						NS.myMask.show();
						NS.storeUsuarios.load();
							
						Ext.getCmp(PF + 'cmbEstatus').reset();
				}
			}	
		}
	});
	
	
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
		,x :605
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
					NS.myMask.show();
					NS.storeUsuarios.load();
					Ext.getCmp(PF + 'cmbClaveUsuario').reset();
					Ext.getCmp(PF + 'cmbNomUsuario').reset();
				}
			}
		}
	});
	
	/*************************************************
	 *		COMPONENTES PARA VENTANA EMERGENTE		 *
	 *************************************************/
	
	verificaComponentesCreados();
	
	//Panel 1
	NS.lbNombre = new Ext.form.Label({
		id: PF + 'lbNombre',
		text: 'Nombre:',
		x: 10,
		y : 0
	});
	
	NS.lbApp = new Ext.form.Label({
		id: PF + 'lbApp',
		text: 'Apellido paterno:',
		x: 10,
		y : 30		
	});
	
	NS.lbApm = new Ext.form.Label({
		id: PF + 'lbApm',
		text: 'Apellido Materno:',
		x: 10,
		y : 60		
	});
	
	NS.lbCorreo = new Ext.form.Label({
		id: PF + 'lbCorreo',
		text: 'Correo:',
		x: 10,
		y : 90		
	});
	
	NS.nombre= new Ext.form.TextField({
		id: PF+'nombre',
        name: PF+'nombre',
		x: 95,
		y: 0,
		width: 290,
		maskRe: /[A-Za-z ÑÁÉÍÓÚñáéíóú]/,
		enableKeyEvents:true,
        listeners:{
			blur: {
				fn:function(text){
					text.setValue(text.getValue().toUpperCase());
				}
			}
		}
	});
	
	
	NS.apPaterno= new Ext.form.TextField({
		x: 100,
		y: 30,
		width: 285,
		maskRe: /[A-Za-z ÑÁÉÍÓÚñáéíóú]/,
		id: PF+'apPaterno'
        ,name: PF+'apPaterno'
        ,enableKeyEvents:true
        ,listeners:{
			blur: {
				fn:function(text){
					text.setValue(text.getValue().toUpperCase());
				}
			}
		}
	});
		
	NS.apMaterno = new Ext.form.TextField({
		x: 100,
		y: 60 ,
		width: 285,
		maskRe: /[A-Za-z ÑÁÉÍÓÚñáéíóú]/
		,id: PF+'apMaterno'
        ,name: PF+'apMaterno'
        ,regeX:/[A-Z]|[a-z]/
        ,enableKeyEvents:true
        ,listeners:{
			blur: {
				fn:function(text){
					text.setValue(text.getValue().toUpperCase());
				}
			}
		}
	});
	
	NS.correoElectronico = new Ext.form.TextField({
		id: PF + 'correoElectronico',
		name: PF + 'correoElectronico',
		x: 75,
		y: 90 ,
		width: 310,
		maskRe: /[A-Za-z0-9 @.-_]/
		,id: PF+'correoElectronico'
        ,name: PF+'correoElectronico'
        ,enableKeyEvents:true
     	,listeners:{
     		specialKey:{
 				fn:function(caja, e) {
 					if(e.keyCode==Ext.EventObject.TAB || e.keyCode == 13){
 						NS.validarCorreo(caja.getValue());
							if(NS.correoValido==false){
								Ext.Msg.alert('SET','Correo no valido');
								Ext.getCmp(PF+'correoElectronico').focus();
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
						}
					}
				}
     	}
	});
	
	/***********Panel 2*************/
	
	NS.chkBloqueo= new Ext.form.Checkbox({
		 id:PF+ 'chkBloqueo',
		 name :PF + 'chkBloqueo',
		boxLabel: 'Bloqueo ',
		hidden:true,
		x: 300,
		y : 80	
	});
	
	NS.lbPerfil = new Ext.form.Label({
		id:PF+ 'lbPerfil',
		text: 'Perfil: ',
		x: 10,
		y : 0		
	});
	
	NS.lbEstatus= new Ext.form.Label({
		id: PF + 'lbEstatus',
		text: 'Estatus: ',
		x: 10,
		y : 30
	});
	
	
	NS.lbFechaVen = new Ext.form.Label({
		id: PF + 'lbFechaVen',
		text: 'Fecha vencimiento: ',
		x: 170,
		y : 35		
	});
	
	NS.lbEmpresa = new Ext.form.Label({
		id: PF + 'lbEmpresa',
		text: 'Empresa: ',
		x: 10,
		y : 60		
	});
	
	NS.lbCaja = new Ext.form.Label({
		id: PF + 'lbCaja',
		text: 'Caja: ',
		x: 10,
		y : 90	
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
		,x: 65
		,y: 30
		,width: 100
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
	
	NS.fecVencimiento= new Ext.form.DateField({
		id: PF+'fecVencimiento',
        name: PF+'fecVencimiento',
		x: 270,
		y: 30,
		width: 100,
		minValue : NS.fecHoy, 
		value:NS.fecHoy,
	});
	
	/********Combo empresa ********/
	
	
	NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: GlobalAction.llenarComboEmpresasUsuario,
		fields:
		[
		    {name: 'id'},
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'id',
		listeners:
		{
			load: function(s, records){
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay empresas que mostrar');
				}	
			},
			exception:function(misc) {
				//NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
	
	
	NS.txtEmpresa = new Ext.form.TextField({
		id: PF + 'txtEmpresa',
        name:PF + 'txtEmpresa',
        x: 70,
		y: 60 ,
        width: 50,
        tabIndex: 0,
        value:'0',
        listeners: {
			change: {
				fn: function(caja,valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
							valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresa.getId());
							if(valueCombo=='' || valueCombo ==undefined || valueCombo == null){
								NS.txtEmpresa.setValue('');
							}
					}
                }
			}
    	}
    });

	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresa',
		name: PF + 'cmbEmpresa',
		x: 130,
		y: 60 ,
        width: 245,
		typeAhead: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		triggerAction: 'all',
		value:'',
		visible: false,
		autocomplete:true,
		selectOnFocus:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresa.getId());					
					
				}
			}
		}
	});
	
	/*****Fin combo empresa
	 * 
	 * 
	 */
	
/********Combo Perfil ********/
	
	NS.storePerfil = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: SeguridadUsuarioAction.comboPerfilesUsuario,
		fields:
		[
		    
		    {name: 'id'},
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'id',
		listeners:
		{
			load: function(s, records){
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay perfiles que mostrar');
				}	
			},
			exception:function(misc) {
				//NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
	
	
	NS.txtPerfil = new Ext.form.TextField({
		id: PF + 'txtPerfil',
        name:PF + 'txtPerfil',
        x: 70,
		y: 0 ,
        width: 50,
        tabIndex: 0,
        value:'0',
        listeners: {
			change: {
				fn: function(caja,valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
							valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtPerfil', NS.cmbPerfil.getId());
							if(valueCombo=='' || valueCombo ==undefined || valueCombo == null){
								NS.txtPerfil.setValue('');
							}
					}
                }
			}
    	}
    });

	NS.cmbPerfil = new Ext.form.ComboBox({
		store: NS.storePerfil,
		id: PF + 'cmbPerfil',
		name: PF + 'cmbPerfil',
		x: 130,
		y: 0 ,
        width: 245,
		typeAhead: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		triggerAction: 'all',
		value:'',
		visible: false,
		autocomplete:true,
		selectOnFocus:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtPerfil', NS.cmbPerfil.getId());					
					
				}
			}
		}
	});
	
	/*****Fin combo PERFIL
	 * 
	 * 
	 */
	
	/*
	 * Combo caja
	 */
	
	NS.storeCaja = new Ext.data.DirectStore( {
		 paramsAsHash: false
		,root: ''
		,directFn: SeguridadUsuarioAction.obtenerTodasCajas
		,idProperty: 'id'  		//identificador del store
		,fields: [
			 {name: 'id' }
			,{name: 'descripcion'}
		]
		,listeners: {
			load: function(s, records){
				//Ext.MessageBox.alert("Informacion",""+  records.length + " registros cargados.");
			}
		}
	}); 
	
	//combo fisico Empresa
	NS.cmbCaja = new Ext.form.ComboBox({
		store: NS.storeCaja 	//llamada al store
		,id: PF+'cmbCaja'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,width :150
		,valueField: 'id'
		,displayField: 'descripcion'
		,autocomplete: true
		,emptyText: 'Caja'
		,triggerAction: 'all'
		,value: ''
		,x: 130
		,y: 90
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtCaja',NS.cmbCaja.getId());					
					
				}
			}
		}
	});
	
	NS.txtCaja  = new Ext.form.TextField({
		id: PF + 'txtCaja',
		name: PF + 'txtCaja',
		x: 70,
		y: 90 ,
		width: 50,
		listeners: {
			change: {
				fn: function(caja,valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
							valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtCaja', NS.cmbCaja.getId());
							if(valueCombo=='' || valueCombo ==undefined || valueCombo == null){
								NS.txtCaja.setValue('');
							}
					}
                }
			}
    	}
	});
	
	/****Fin combo caja***/
	
	/*********** Contraseña ************/
	
	NS.chkModificar= new Ext.form.Checkbox({
		id:PF+ 'chkModificar',
		name :PF + 'chkModificar',
		boxLabel: 'Contraseña ',
		hidden:true,
		x: 300,
		y : 95
		,listeners:{
			check: {
				fn:function(cja,check){
					if(check){
						winEditarSF.setSize(450,520);
		    			NS.panelContrasenia.show();
		    			winEditarSF.setPosition(475,100);
					}else{
						winEditarSF.setSize(450,400);
		    			NS.panelContrasenia.hide();
		    			winEditarSF.setPosition(475,100);
						//bandera para modificar
						//ocultar el panel
						
						
					}
				}
			}
		} 
	});
	
	NS.lbClave = new Ext.form.Label({
		id: PF+'lbClave',
		text: 'Clave: ',
		x: 10,
		y : 0		
	});
	
	NS.lbContrasenia = new Ext.form.Label({
		id: PF+ 'Contrasenia',
		text: 'Contraseña: ',
		x: 10,
		y :30		
	});
	
	NS.lbConfirmContra= new Ext.form.Label({
		id: PF + 'lbConfirmContra',
		text: 'Confirma contraseña: ',
		x: 10,
		y : 60
	});
	
	NS.clave = new Ext.form.TextField({
		x: 90,
		y: 0,
		width: 120
		,id: PF+'clave'
        ,name: PF+'clave'
        ,maxLength: 10
        ,enableKeyEvents:true
        ,listeners:{
			change: {
				fn:function(text){
					text.setValue(text.getValue().toUpperCase());
				}
			},
			keydown:{
				fn:function(caja, e) {
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 10));
					}
				}
			},
			keyup:{
				fn:function(caja, e) {	 				
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 10));
					}
					
				}
			}
		} 
	
	});
	
	NS.contrasena = new Ext.form.TextField({
		inputType: 'password',
		x: 90,
		y: 30,
		width: 300,
		autocomplete: false,
   		/*style: {
   			autocomplete: 'off',
        },*/
        id: PF+'contrasena'
        ,name: PF+'contrasena'
        //,blankText : 'Este campo es requerido'
        //,allowBlank: false
        ,enableKeyEvents:true
		,listeners:{
			specialKey:{
				fn:function(caja, e) {
					if(e.keyCode==Ext.EventObject.TAB || e.keyCode == 13){
						var cadena=caja.getValue();
						if(NS.bchkModificar==1)
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
	 		
			blur:{fn:function(caja, e) {
				var cadena=caja.getValue();
				if(NS.bchkModificar==1)
					NS.validarContrasena(cadena);
				if(error==''){
					Ext.getCmp(PF+'contrasena2').setValue('');
					Ext.getCmp(PF+'contrasena2').focus();
				}else{
					caja.setValue('');
					Ext.Msg.alert('SET',error);
					//caja.focus();
					
				}
				
	 		}}
		}
	});
	
	NS.contrasena2= new Ext.form.TextField({
		inputType: 'password',
		x: 120,
		y: 60,
		width: 270,
		autocomplete: false,
		id: PF+'contrasena2',
	    name: PF+'contrasena2',
	    blankText : 'Escriba nuevamente la contreseña',
	    enableKeyEvents:true,
		listeners:{
			blur: {
				fn:function(text){
					 if(NS.bchkModificar==1){
						 var contrasena1=Ext.getCmp(PF+'contrasena').getValue();
						 var contrasena2=text.getValue();
						 if(contrasena1!=contrasena2){
							 Ext.Msg.alert('SET','Las contraseñas no coinciden');
						 }
					 }
				}
			}
		}
	
	});
	
	
	/*********************************************
	 * 			Fin de ventana emergente         *
	 *********************************************/
	
	//GRID DE CONSULTA
	//Columna de seleccion en el grid
	
	NS.storeUsuarios = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idUsuario:'',estatus:''},
		paramOrder: ['idUsuario', 'estatus'],
		directFn: SeguridadUsuarioAction.obtenerUsuariosGrid,
		idProperty: 'idUsuario',
		fields: [
		    {name: 'idUsuario'},
		 	{name: 'cveUsuario'},
		 	{name: 'nombreU'},
		 	{name: 'paterno'},
		 	{name: 'materno'},
		 	{name: 'psw'},
		 	{name: 'estatus'},
		 	{name: 'intentos'},
		 	{name: 'mail'},
		 	{name: 'acceso'},
		 	{name: 'vencimiento'},
		 	{name: 'nomCaja'},
		 	{name: 'nomEmpresa'},
		 	{name: 'perfil'},
		 	{name: 'noEmpresa'},
		 	{name: 'idPerfil'},
		 	{name: 'idCaja'},
		 	
		],
		listeners: {
			load: function(s, records) {
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No se encontraron usuarios.');}
				NS.myMask.hide();
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}	
	})
	//NS.myMask.show();
	//NS.storeUsuarios.load();
	
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: true
	});

	//Columnas del grid
	NS.columnasGrid = new Ext.grid.ColumnModel ([
	    NS.columnaSeleccion,
	  	//{header: 'Usuario', width: 100, dataIndex: 'idUsuario', sortable: true},
	    {header: 'Perfil', width: 200, dataIndex: 'perfil', sortable: true},
	  	{header: 'Clave', width: 100, dataIndex: 'cveUsuario', sortable: true},
	  	{header: 'Nombre', width: 100, dataIndex: 'nombreU', sortable: true},
	  	{header: 'Apellido paterno', width: 100, dataIndex: 'paterno', sortable: true},
	  	{header: 'Apellido materno', width: 100, dataIndex: 'materno', sortable: true},
	  	{header: 'Estatus', width: 70, dataIndex: 'estatus', sortable: true},
	  	{header: 'Intentos', width: 70, dataIndex: 'intentos', sortable: true},
	  	{header: 'Email', width: 170, dataIndex: 'mail', sortable: true},
	  	{header: 'Fec. Acceso', width: 100, dataIndex: 'acceso', sortable: true},
	  	{header: 'Fec. Vencimiento', width: 100, dataIndex: 'vencimiento', sortable: true},
	  	{header: 'Caja', width: 100, dataIndex: 'nomCaja', sortable: true},
	  	{header: 'Empresa', width: 300, dataIndex: 'nomEmpresa', sortable: true},
	]); 
	
	//Se agregan componentes (columnas) a grid
	
	NS.gridUsuarios= new Ext.grid.GridPanel ({
		store: NS.storeUsuarios,
		id: PF + 'gridDatos',
		name: PF + 'gridDatos',
		cm: NS.columnasGrid,
		sm: NS.columnaSeleccion,
		width: 1000,		
		height: 340,
		
		stripeRows: true,
		columnLines: true,
	});
	
	//fin de  grid
	
   	
   	// Funciones y store's
	
	NS.buscar = function(){};
	
	NS.opciones=function(op){
		
		switch (op) {
		case 1:
	
			NS.banderaOpcion='nuevo';
 	   		 NS.pBanModificar = false;
			 NS.idUsuarioM = 0;
			 Ext.getCmp(PF+ 'chkBloqueo').setVisible(false);
			 Ext.getCmp(PF+'clave').setReadOnly(false);
			 
			 NS.chkModificar.setVisible(false);
			 NS.bOpcion=1;
			 NS.bchkModificar=1;
			 
			 winEditarSF.setSize(450,520);
	 		 NS.panelContrasenia.show();
	 		 winEditarSF.setPosition(475,100);
			 winEditarSF.setTitle("Nuevo registro");
			 winEditarSF.show();
			 
			 //NS.fecVencimiento.setMinValue(fecHoy);
			break;

		case 2:
			 NS.bchkModificar=0;
			 NS.chkModificar.setValue(false);
			
			 var registroSeleccionado= NS.gridUsuarios.getSelectionModel().getSelections();
			 if (registroSeleccionado.length > 0){
				 
				 winEditarSF.setSize(450,400);
		 		 NS.panelContrasenia.hide();
		 		 winEditarSF.setPosition(475,100);
				 winEditarSF.setTitle("Editar registro");
				 winEditarSF.show();
			    
				 
				 //banderaEleccion=1;
				    NS.idUsuarioModificado = registroSeleccionado[0].get('idUsuario');
					NS.clave.setValue(registroSeleccionado[0].get('cveUsuario'));
					NS.nombre.setValue(registroSeleccionado[0].get('nombreU'));
					NS.apPaterno.setValue(registroSeleccionado[0].get('paterno'));
					NS.apMaterno.setValue(registroSeleccionado[0].get('materno'));
					var estatus= registroSeleccionado[0].get('estatus');
					if(estatus=='A')
						NS.cmbEstatusNuevo.setValue('A');
					else 
						NS.cmbEstatusNuevo.setValue('I');
					NS.correoElectronico.setValue(registroSeleccionado[0].get('mail'));
					NS.fecVencimiento.setValue(registroSeleccionado[0].get('vencimiento'));
					NS.cmbCaja.setValue(registroSeleccionado[0].get('nomCaja'));
					NS.txtCaja.setValue(registroSeleccionado[0].get('idCaja'));
					NS.txtEmpresa.setValue(registroSeleccionado[0].get('noEmpresa'));
					NS.cmbEmpresa.setValue(registroSeleccionado[0].get('nomEmpresa'));
					NS.cmbPerfil.setValue(registroSeleccionado[0].get('perfil'));
					NS.txtPerfil.setValue(registroSeleccionado[0].get('idPerfil'));
					Ext.getCmp(PF+'clave').setReadOnly(true);
			    	NS.pBanModificar = true;
					Ext.getCmp(PF+ 'chkBloqueo').show();
					NS.chkModificar.setVisible(true);
					NS.chkModificar.show();
					
					var intentos = registroSeleccionado[0].get('intentos'); //YEC 23/11/2015
					if(intentos==0){ //YEC
						Ext.getCmp(PF+'chkBloqueo').setValue(false);
					}else{
						Ext.getCmp(PF+'chkBloqueo').setValue(true);
					} // FIN YEC 23/11/2015
				
					//fin YEC
					
					NS.bOpcion=2;
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
			 }
			break;
		case 3:
			NS.banderaOpcion='';
			var records= NS.gridUsuarios.getSelectionModel().getSelections();
			 if (records.length > 0){
				 Ext.Msg.confirm( 'SET', 'Confirma que desea eliminar el registro' , function(btn){
						if(btn=='yes'){
							NS.eliminar();
						}
					 }) 
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
			 }
			break;
		}
	};
	
	NS.cancelar=function(){
		 winEditarSF.hide();
		 NS.limpiarTodo();
	}
	
	NS.aceptar=function(){
		NS.validarComponentes(); //YEC
		if( NS.componenteValidos==true){ //YEC 
			if(NS.bOpcion==1)
				NS.nuevoRegistro();
			if(NS.bOpcion==2)
				NS.editarRegistro();
			NS.limpiarTodo();
		} else{ //ERROR DATOS NO VALIDOS
				Ext.Msg.alert('SET','Error '+ error); //MENSAJE DE ERROR
		} //FIN DE COMPONENTES NO VALIDOS YEC
	}
	
	NS.eliminar=function(){
		var records= NS.gridUsuarios.getSelectionModel().getSelections();
		var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Eliminando..."});
		myMask.show();
		for(i=0; i<records.length; i++){
			var record = records[i];
			var varId = records[i].get('idUsuario');
			SeguridadUsuarioAction.eliminar(varId, function(result, e){
				if (result != '' && result != null && result != undefined ) {
					myMask.hide();
					Ext.Msg.alert('SET', result);
					NS.limpiarTodo();	
				}
			});
		}
	}
	
	NS.nuevoRegistro=function(){
		NS.bOpcion=0;
		var vector = {};
		var matriz = new Array();
		vector.banderaF='false';
		vector.idUsuarioF='0';
		vector.clave=NS.clave.getValue();
		vector.nombre=NS.nombre.getValue();
	    vector.apPaterno=NS.apPaterno.getValue();
	    vector.apMaterno=NS.apMaterno.getValue();
		vector.contrasena=NS.contrasena.getValue();
		vector.cmbEstatusNuevo=NS.cmbEstatusNuevo.getValue();
		vector.correoElectronico=NS.correoElectronico.getValue();
		vector.fecVencimiento=NS.fecVencimiento.getRawValue();
		vector.noEmpresa=NS.txtEmpresa.getValue();
		vector.idCaja=NS.txtCaja.getValue(); //NS.cmbCaja , NS.txtCaja
		vector.idPerfil= NS.txtPerfil.getValue();
		vector.intentos='false';
		matriz[0]=vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		SeguridadUsuarioAction.insertarModificarB(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined ) {
				winEditarSF.hide();
				Ext.Msg.alert('SET', resultado);
				NS.limpiarTodo();
			}
				
		});
	}
	
	NS.editarRegistro=function(){
		var vector = {};
		var matriz = new Array();
		vector.banderaF='true';
		vector.idUsuarioF=NS.idUsuarioModificado;
		vector.clave=NS.clave.getValue();
		vector.nombre=NS.nombre.getValue();
	    vector.apPaterno=NS.apPaterno.getValue();
	    vector.apMaterno=NS.apMaterno.getValue();
		vector.contrasena=NS.contrasena.getValue();
		vector.cmbEstatusNuevo=NS.cmbEstatusNuevo.getValue();
		vector.correoElectronico=NS.correoElectronico.getValue();
	    vector.fecVencimiento=NS.fecVencimiento.getRawValue()+'';
		vector.noEmpresa=NS.txtEmpresa.getValue();
		vector.idCaja=NS.txtCaja.getValue(); //NS.cmbCaja , NS.txtCaja
		vector.perfil= NS.txtPerfil.getValue();
		vector.idPerfil= NS.txtPerfil.getValue();
		var banderaintentos= NS.chkBloqueo.getValue();
		
		vector.intentos=''+banderaintentos;
		matriz[0]=vector;
		var jSonString = Ext.util.JSON.encode(matriz);
		
		SeguridadUsuarioAction.insertarModificarB(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined ) {
				winEditarSF.hide();
				Ext.Msg.alert('SET', resultado);
				NS.limpiarTodo();
			}
		});
	}
	
	NS.limpiarTodo=function(){

		NS.storeUsuarios.removeAll();
		NS.storeNombreUsuario.removeAll();
		
		NS.cmbEmpresa.reset();
		NS.cmbCaja.reset();
		NS.cmbPerfil.reset();
		NS.cmbEstatusNuevo.reset();
		NS.cmbClaveUsuario.reset();
		NS.cmbNomUsuario.reset();
		NS.cmbEstatus.reset();
		banderaEleccion=0;
		
		NS.contrasena.setValue('');
		NS.contrasena2.setValue('');
		NS.nombre.setValue('');
		NS.apPaterno.setValue('');
		NS.apMaterno.setValue('');
		NS.correoElectronico.setValue('');
		NS.clave.setValue('');
		NS.txtPerfil.setValue('');
		NS.fecVencimiento.setValue(NS.fecHoy);	
		NS.txtEmpresa.setValue('');
		NS.chkBloqueo.setValue(false); 
		NS.chkBloqueo.setVisible(false);
		NS.txtCaja.setValue('');
		
		//NS.gridUsuarios.refresh();
		
		NS.chkModificar.setValue(false); 
		NS.chkModificar.setVisible(false);
		NS.bchkModificar=0;
		
		NS.storeNombreUsuario.load();
	
		winEditarSF.hide();
	}
	
	/************
	 * Funciones de usuarios
	 */
	
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
	
	NS.validarComponentes=function(){
		error='';
		var clave =Ext.getCmp(PF+'clave').getValue();
		var nombre =Ext.getCmp(PF+'nombre').getValue();
		var app =Ext.getCmp(PF+'apPaterno').getValue();
		var apm =Ext.getCmp(PF+'apMaterno').getValue();
		var correo =Ext.getCmp(PF+'correoElectronico').getValue();
		var perfil= NS.txtPerfil.getValue();
		var empresa= NS.txtEmpresa.getValue();
		var caja= NS.txtCaja.getValue();
		
		if(clave!='' && nombre != '' && app != '' && apm != '' && perfil != '' && empresa!='' && caja!=''){
			
				NS.validarCorreo(correo);
				if(NS.correoValido==true){
					 NS.componenteValidos=true;
				}else{
					 error='Correo no valido.';
					 NS.componenteValidos=false;
				}
				
				if(NS.bchkModificar == 1 && NS.componenteValidos) {
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
	
	//ventana emergente
	function verificaComponentesCreados(){
		var compt; 	
		/*** Ventana nuevo/editar***/
		
		//Panel  1
		
		compt = Ext.getCmp(PF + 'lbClave');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbNombre');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbApp');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbApm');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbCorreo');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'clave');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'nombre');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'apPaterno');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'apMaterno');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'correoElectronico');
		if(compt != null || compt != undefined ){ compt.destroy(); }

		
		//Panel 2
		
		compt = Ext.getCmp(PF + 'chkBloqueo');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'lbPerfil');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbEstatus');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbFechaVen');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbEmpresa');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbCaja');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtPerfil');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'cmbPerfil');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtEstatus');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'fecVencimiento');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtEmpresa');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'cmbEmpresa');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtCaja');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'cmbCaja');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		//Panel 3
		
		compt = Ext.getCmp(PF + 'lbContrasenia');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbConfirmContra');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'contrasena');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'contrasena2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'chkModificar');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		/**************************/
	}
	
	/********************** Generar el excel *********************************/
	
	 NS.validaDatosExcel = function() {		
		var registroSeleccionado = NS.gridUsuarios.getSelectionModel().getSelections();
		
		if(registroSeleccionado.length == 0) {
			registroSeleccionado = NS.storeUsuarios.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		var matriz = new Array();	
		for(var i=0;i<registroSeleccionado.length;i++){
			var vector = {};
			vector.idUsuario=registroSeleccionado[i].get('idUsuario');
			vector.cveUsuario=registroSeleccionado[i].get('cveUsuario');
			vector.nombreU=registroSeleccionado[i].get('nombreU');
			vector.paterno=registroSeleccionado[i].get('paterno');
			vector.materno=registroSeleccionado[i].get('materno');
			vector.estatus=registroSeleccionado[i].get('estatus');
			vector.intentos=registroSeleccionado[i].get('intentos');
			vector.mail=registroSeleccionado[i].get('mail');
			vector.acceso=registroSeleccionado[i].get('acceso');
			vector.vencimiento=registroSeleccionado[i].get('vencimiento');
			vector.noEmpresa=registroSeleccionado[i].get('noEmpresa');
			vector.nomEmpresa=registroSeleccionado[i].get('nomEmpresa');
			vector.idCaja=registroSeleccionado[i].get('idCaja');
			vector.nomCaja=registroSeleccionado[i].get('nomCaja');
			vector.idPerfil=registroSeleccionado[i].get('idPerfil');
			vector.perfil=registroSeleccionado[i].get('perfil');
			matriz[i] = vector;
		}
		var jSonString = Ext.util.JSON.encode(matriz);
		NS.exportaExcel(jSonString);
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		SeguridadUsuarioAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=excelUsuariosS';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
	
	/****************Fin de generar excel ***********************/

	
	//panel
	NS.panelBusqueda = new Ext.form.FieldSet ({
		title: 'Búsqueda',
		x: 250,
		y: 0,
		width: 780,
		height: 60,
		layout: 'absolute',
		items: [
				NS.cmbClaveUsuario,
				NS.cmbNomUsuario,
				NS.cmbEstatus,
				NS.lbClaveUsuarioB, 
				NS.lbEstatusB,
				NS.lbNombreB,
		 	/*{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 280,
		 		y: 0,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click:{
		 				fn: function(e){
		 					NS.buscar();
		 				}
		 			}
		 		}
		 	},*/
		]
	});
	
	
	NS.panelGrid = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 70,
		width: 1040,
		height: 400,
		layout: 'absolute',
		buttonAlign:'center',
		buttons:[
			      {
				    text:'Crear Nuevo',
				    handler:function(){
				    	NS.opciones(1);
				    }
				  },
				  {
					 text:'Modificar',
					 handler:function(){
						 NS.opciones(2);
					 }
			       },
			       {
			    	  text:'Eliminar',
					  handler:function(){
						  NS.opciones(3);
					  }
					},
					{
			    	  text:'Excel',
					  handler:function(){
						  NS.validaDatosExcel();
					}
			       },
				 ],
		items: [
		 	NS.gridUsuarios,		 		 	
		]
	
	});
	
	 NS.global = new Ext.form.FieldSet ({
			title: '',
			x: 0,
			y: 5,
			width: 1200 ,		
			height: 530,
			layout: 'absolute',
			items:
			[
			 	NS.panelBusqueda,
			 	NS.panelGrid,
		    ]
		});
	 NS.conceptos = new Ext.FormPanel ({
			title: 'Usuarios',
			width: 1300,
			height: 800,
			frame: true,
			padding: 10,
			autoScroll: false,
			layout: 'absolute',
			id: PF + 'conceptos',
			name: PF + 'conceptos',
			renderTo: NS.tabContId,
			items: [
			 	NS.global
			]
		});
	 	 
	 NS.panelContacto1 = new Ext.form.FieldSet ({
			title: 'Datos personales',
			x: 15,
			y: 0,
			width: 400,
			height: 150,
			layout: 'absolute',
			items: [
					
					NS.lbNombre,
					NS.lbApp,
					NS.lbApm,
					NS.lbCorreo,
					NS.nombre,
					NS.apPaterno,
					NS.apMaterno,
					NS.correoElectronico,
			]
		});
	 
	 
	 NS.panelContacto2= new Ext.form.FieldSet ({
			title: 'Datos usuario',
			x: 15,
			y: 155,
			width: 400,
			height: 150,
			layout: 'absolute',
			items: [
					NS.lbPerfil,
					NS.lbEstatus,
					NS.lbFechaVen,
					NS.lbEmpresa,
					NS.lbCaja,
					NS.txtPerfil,
					NS.cmbPerfil,
					NS.cmbEstatusNuevo,
					NS.fecVencimiento,	
					NS.txtEmpresa,
					NS.cmbEmpresa,
					NS.cmbCaja ,
					NS.txtCaja,
					NS.chkBloqueo,
					NS.chkModificar,
			],
		});
	 
	 NS.panelContrasenia= new Ext.form.FieldSet ({
			title: 'Datos cuenta',
			x: 15,
			y: 310,
			width: 400,
			height: 120,
			layout: 'absolute',
			items: [
					NS.lbClave,
					NS.clave,
					NS.lbContrasenia,
					NS.lbConfirmContra,
					NS.contrasena,
					NS.contrasena2,
					
			]
		});
	 
		var winEditarSF = new Ext.Window({
			title: 'Mantenimiento usuarios',
			modal: true,
			shadow: true,
			closeAction: 'hide',
			width: 450,
		   	height: 520,
		   	layout: 'absolute',
		   	plain: true,
		   	resizable:false,
		   	draggable:false,
		   	closable:false,
		    bodyStyle: 'padding:0px;',
		    buttonAlign: 'center',
		    buttons:[
		      {
		    	text:'Aceptar',
		    	handler:function(){
		    		NS.aceptar();
		    	}
		      },
		      
		      {
			    	text:'Cancelar',
			    	handler:function(){
			    		NS.cancelar();
			    	}
			      },
			      
		    ],
		   	items: [
		   	        	NS.panelContacto1,
		   	        	NS.panelContacto2,
		   	        	NS.panelContrasenia
		   	        ],
		     listeners:{
		    	 show:{
		    		 fn:function(){
		    			 NS.storeEmpresas.load();
		    			 NS.storePerfil.load();
		    			 NS.storeCaja.load();
		    		 }
		    	 }, 
		    	  hide:{
		    		  fn:function(){
		    			  
		    			  
		    		  }
		    	  }
		     } 
		
		});
	 NS.conceptos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	 
	 
});
 