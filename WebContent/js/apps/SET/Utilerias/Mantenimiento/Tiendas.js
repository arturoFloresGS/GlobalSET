Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.Tiendas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var banderaEleccion=0;
	var motivoEliminar="";
	NS.idPersona='';
	NS.nomPersona='';
	verificaComponentesCreados();
	NS.nombre='';
	NS.bandera=false;
	
	NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	
	
	/*************Agregados el 090216********/
	NS.lbRazonSocial = new Ext.form.Label({
		id: PF + 'lbRazonSocial',
		text: 'Razon social: ',
		x: 10,
		y : 130	
	});
	
	NS.txtRazonSocial = new Ext.form.TextField({
		id: PF + 'txtRazonSocial',
		name: PF + 'txtRazonSocial',
		x: 90,
		y: 130,
		maxLength: 80,
		width: 230,
		enableKeyEvents:true,
		listeners:{
			keydown:{
				fn:function(caja, e) {
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 80));
					}
				}
		    },
		    keyup:{
				fn:function(caja, e) {	 				
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 80));
					}
					
				}
		    }
		}

	});

	/*****Combo empresa************/
	NS.lbNoEmpresa = new Ext.form.Label({
		text: 'Empresa: ',
		x: 10,
		y : 90		
	});
	
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
				}/*else{
					var recordsStoreGruEmp = NS.storeEmpresas.recordType;	
					var todas = new recordsStoreGruEmp({
				       	id : '0',
				       	descripcion : '***********TODAS***********'
			       	});
					NS.storeEmpresas.insert(0,todas);
				}*/
				NS.myMask.hide();
					
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
	
	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
        name:PF + 'txtNoEmpresa',
		x: 70,
        y: 90,
        width: 50,
        tabIndex: 0,
        value:'0',
        listeners: {
			change: {
				fn: function(caja,valor) {
					var err=1;
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
							valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
							if(valueCombo!='' && valueCombo !='undefined' && valueCombo != null)
								err=0;
					}
					
					if(err==1){
						//Ext.getCmp(PF + 'txtNoEmpresa').setValue('0');
						NS.cmbEmpresas.reset();
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
					}
                }
			}
    	}
    });
	
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 120,
        y: 90,
        width: 210,
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
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());					
				}
			}
		}
	});
	/*******Agregados el 090216 ********/
	
	NS.lbNoAcredor = new Ext.form.Label({
		text: 'No.Acredor: ',
		x: 10,
		y : 8		
	});
	
	NS.txtNoAcredor = new Ext.form.TextField({
		id: PF + 'txtNoAcredor',
		name: PF + 'txtNoAcredor',
		x: 100,
		y: 5,
		tabIndex:1,
		maxLength: 15,
		width: 60,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				if(e.keyCode ==13){ 
	 				//	NS.buscar();
	 					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	 						NS.myMask.show();
	 						NS.storeBeneficiario.baseParams.ePersona=''+caja.getValue();
		  					NS.storeBeneficiario.baseParams.nombre='';
		  					NS.bandera=true;
		  					NS.storeBeneficiario.load();
	       				}else {
	       					NS.cmbBeneficiario.reset();
	       					NNS.storeBeneficiario.removeAll();
	       				}
	 				}
	 			}
	 		},
	 		keydown:{
	 			fn:function(caja, e) {
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 15));
	 				}
	 			}
	 		},
	 		keyup:{
	 			fn:function(caja, e) {	 				
	 				if(!caja.isValid()){
	 					caja.setValue(caja.getValue().substring(0, 15));
	 				}
	 				
	 			}
	 		},
	 		change:{
	 			fn:function(caja, e) {	
	 				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
        				NS.storeBeneficiario.baseParams.nombre='';
        				NS.storeBeneficiario.baseParams.ePersona=''+caja.getValue();
        				NS.myMask.show();
        				NS.bandera=true;
        				NS.storeBeneficiario.load();
       				}else {
       					NS.cmbBeneficiario.reset();
       					NS.storeBeneficiario.removeAll();
       				}
	 			}
	 		}
		}
	});
	
	NS.lbTienda = new Ext.form.Label({
		id: PF + 'lbTienda',
		text: 'Tienda: ',
		x: 10,
		y : 8		
	});
	
	NS.txtTienda = new Ext.form.NumberField({
		id: PF + 'txtTienda',
		name: PF + 'txtTienda',
		width: 70,
		x: 90,
		y: 5,
		maxLength: 6,
		tabIndex:6,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				var temp = caja.getValue();
	 				if(e.keyCode ==13){ 
	 					NS.txtNoAcredor_editar.focus(true);
	 				} 
	 			}
	 		},
		    keydown:{
				fn:function(caja, e) {
					 var temp=""+caja.getValue();
					if(!caja.isValid()){
						caja.setValue(parseInt(temp.substring(0, 6)));
					}
				}
		    },
		    keyup:{
				fn:function(caja, e) {	 
					 var temp=""+caja.getValue();
					if(!caja.isValid()){
						caja.setValue(parseInt(temp.substring(0, 6)));
					}
					
				}
		    }
		}
	});
	
	NS.lbNoAcredor_editar = new Ext.form.Label({
		id: PF + 'lbNoAcredor_editar',
		text: 'No.Acredor: ',
		x: 180,
		y : 8		
	});
	
	NS.txtNoAcredor_editar = new Ext.form.NumberField({
		id: PF + 'txtNoAcredor_editar',
		name: PF + 'txtNoAcredor_editar',
		x: 260,
		y: 8 ,
		maxLength: 15,
		width: 60,
		tabIndex:7,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				var temp = caja.getValue();
	 				if(e.keyCode ==13){ 
	 					NS.txtNombre.focus(true);
	 				}
	 			}
	 		},
		    keydown:{
				fn:function(caja, e) {
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 15));
					}
				}
		    },
		    keyup:{
				fn:function(caja, e) {	 				
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 15));
					}
					
				}
		    }
		}
	});
	
	NS.lbNombre = new Ext.form.Label({
		id: PF + 'lbNombre',
		text: 'Nombre: ',
		x: 10,
		y : 50	
	});
	
	NS.txtNombre = new Ext.form.TextField({
		id: PF + 'txtNombre',
		name: PF + 'txtNombre',
		x: 90,
		y: 50,
		maxLength: 60,
		width: 230,
		tabIndex:8,
		enableKeyEvents:true,
		listeners:{
			keypress:{
	 			fn:function(caja, e) {
	 				var temp = caja.getValue();
	 				if(e.keyCode ==13){ 
	 					NS.txtMotivo.focus(true);
	 				}
	 			}
	 		},
		    keydown:{
				fn:function(caja, e) {
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 60));
					}
				}
		    },
		    keyup:{
				fn:function(caja, e) {	 				
					if(!caja.isValid()){
						caja.setValue(caja.getValue().substring(0, 60));
					}
					
				}
		    }
		}

	});
	
	NS.lbMotivo = new Ext.form.Label({
		id: PF + 'lbMotivo',
		text: 'Motivo: ',
		x: 10,
		y : 170,
		hidden:true,
		
	});
	
	NS.txtMotivo = new Ext.form.TextField({
		id: PF + 'txtMotivo',
		name: PF + 'txtMotivo',
		x: 90,
		y: 170 ,
		hidden:true,
		tabIndex:9,
		width: 230,
	});
	
	//Funciones
	
	 NS.storeBeneficiario = new Ext.data.DirectStore({
			paramsAsHash: false,
			baseParams:{
				nombre:'',
				ePersona:'',
			},
			root: '',
			paramOrder:['nombre','ePersona'],
			root: '',
			directFn: MantenimientoTiendasAction.llenarComboBeneficiario, 
			idProperty: 'id', 
			fields: [
				 {name: 'id'},
				 {name: 'descripcion'},
			],
			listeners: {
				load: function(s, records){
					if(records.length==null || records.length<=0){
						NS.cmbBeneficiario.setValue("");
						NS.txtNoAcredor.setValue("");
					}else{
						if(NS.bandera)
							NS.cmbBeneficiario.setValue(records[0].data.descripcion+'');
					}
					NS.myMask.hide();
					NS.bandera=false;
				},
				exception:function(misc) {
					NS.myMask.hide();
					Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
				}
			}
		}); 
	 
	 	NS.cmbBeneficiario = new Ext.form.ComboBox({
			store: NS.storeBeneficiario
			,name: PF+'cmbBeneficiario'
			,id: PF+'cmbBeneficiario'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
			,pageSize: 10
	        ,x: 180
	        ,y: 5
	        ,width: 175
			,valueField:'id'
			,displayField:'descripcion'
			,autocomplete: true
			,emptyText: 'Seleccione beneficiario'
			,triggerAction: 'all'
			,value: ''
			,visible: false
			,editable: true
			,hidden: false //true
			,minLength:4
			,listeners:{
				select:{
					fn:function(combo, valor) {
						BFwrk.Util.updateComboToTextField(PF+'txtNoAcredor',NS.cmbBeneficiario.getId());
					}
				},
				beforequery: function(qe){
					NS.nombre=qe.combo.getRawValue();
				},
				valid:function(caja){
					if(NS.nombre!='' && !NS.bandera) {
						NS.myMask.show();
	  					NS.storeBeneficiario.baseParams.nombre=NS.nombre;
	  					NS.storeBeneficiario.baseParams.ePersona='';
	  					NS.storeBeneficiario.load();
	  				}else{
	  					NS.storeBeneficiario.removeAll();
	  				}
				}
			}
		});
	 	
	 	//FIN
	 	
	NS.buscar = function(){
		NS.myMask.show();
		NS.storeLlenaGridTiendas.baseParams.noAcreedor = NS.txtNoAcredor.getValue();
		NS.storeLlenaGridTiendas.load();
		NS.txtNoAcredor.focus(true);
	};
	
	NS.comparar=function(){
		var registroSeleccionado= NS.gridTiendas.getSelectionModel().getSelections();
		 if (registroSeleccionado.length > 0){
			var rowAcreedor=NS.gridTiendas.store.data.items[0].get('noAcreedor');
			var datoBuscado=NS.txtNoAcredor.getValue();
			if(rowAcreedor!=datoBuscado && datoBuscado!=''){
				Ext.Msg.alert('SET', 'No existe el número de acreedor');
				NS.txtNoAcredor.setValue('');
			}
		 }
	};
	
	NS.opciones=function(op){
		switch (op) {
		case 1:
			 NS.limpiarTodo();
			 winEditarTienda.show();
			 winEditarTienda.setTitle("Nuevo registro");
			 winEditarTienda.setHeight(220);
			 banderaEleccion=0;
			 NS.txtTienda.enable();
			 NS.myMask.show();
			NS.storeEmpresas.load();
			break;

		case 2:
			 var registroSeleccionado= NS.gridTiendas.getSelectionModel().getSelections();
			 if (registroSeleccionado.length > 0){
				 NS.myMask.show();
				 NS.storeEmpresas.load()
				 winEditarTienda.show();
				 winEditarTienda.setTitle("Editar registro");
				 NS.lbMotivo.setVisible(true);
				 NS.txtMotivo.setVisible(true);
				 winEditarTienda.setHeight(260);
				 NS.txtTienda.disable();
				 banderaEleccion=1;
				 NS.txtTienda.setValue(registroSeleccionado[0].get('ccid'));
				 NS.txtNombre.setValue(registroSeleccionado[0].get('descSucursal'));
				 NS.txtNoAcredor_editar.setValue(registroSeleccionado[0].get('noAcreedor'));
				 NS.cmbEmpresas.setValue(registroSeleccionado[0].get('nomEmpresa'));
				 NS.txtRazonSocial.setValue(registroSeleccionado[0].get('razonSocial'));
				 NS.txtNoEmpresa.setValue(registroSeleccionado[0].get('noEmpresa'));
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
				 NS.limpiarTodo();
				 NS.buscar();
			 }
			break;
		case 3:
			var registroSeleccionado= NS.gridTiendas.getSelectionModel().getSelections();
			 if (registroSeleccionado.length > 0){
				 Ext.MessageBox.prompt( 'SET', 'Escriba el motivo por el cual eliminara el registro: ' , function(btn,text){
					if(btn=='ok'){
						if(text!='' && text!= null){
							NS.eliminar(text,registroSeleccionado[0].get('ccid'));
						}else{
							 Ext.Msg.alert('SET', "El motivo no puede estar vacio");
						}
						
					}else{
						NS.cancelar();
					}
				 }) ;
			 }else{
				 Ext.Msg.alert('SET', "Debe seleccionar un registro");
				 NS.limpiarTodo();
				 NS.buscar();
			 }
			break;
		}
	};
	
	NS.limpiarTodo=function(){
		NS.txtTienda.setValue("");
		NS.txtNoAcredor_editar.setValue("");
		NS.txtNombre.setValue("");
		NS.txtNoAcredor.setValue("");
		NS.txtMotivo.setValue("");
		NS.lbMotivo.setVisible(false);
		NS.txtMotivo.setVisible(false);
		NS.storeLlenaGridTiendas.removeAll();
		NS.gridTiendas.getView().refresh();
		banderaEleccion=0;
		NS.txtNoEmpresa.setValue("");
		NS.txtRazonSocial.setValue("");
		NS.cmbEmpresas.reset();
	}
	
	NS.cancelar=function(){
		 NS.limpiarTodo();
		 NS.buscar();
		 winEditarTienda.hide();
		 NS.lbMotivo.setVisible(false);
		 NS.txtMotivo.setVisible(false);
		 winEditarTienda.setHeight(220);
	}
	
	NS.aceptar=function(){
		if (banderaEleccion==0){
			NS.nuevoRegistro();
			winEditarTienda.hide();
		}else{
			NS.editarRegistro();
		}
	}
	
	NS.eliminar=function(text,ccid){
		var registroSeleccionado= NS.gridTiendas.getSelectionModel().getSelections();
		var vectorTienda = {};
		var matrizTienda = new Array();
		vectorTienda.ccid=ccid;
		vectorTienda.motivo="D:"+text;
		vectorTienda.fechaMod=NS.fecHoy;
		vectorTienda.descSucursal=registroSeleccionado[0].get('descSucursal');
		vectorTienda.noAcreedor=registroSeleccionado[0].get('noAcreedor');
		vectorTienda.razonSocial=registroSeleccionado[0].get('razonSocial');
		vectorTienda.noEmpresa=registroSeleccionado[0].get('noEmpresa');
		
		matrizTienda[0]=vectorTienda;
		
		var jSonString = Ext.util.JSON.encode(matrizTienda);
		MantenimientoTiendasAction.deleteMantenimientoTiendas(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined) {
				if(resultado=='Exito'){
					NS.limpiarTodo();
					NS.buscar();
					winEditarTienda.hide();
				}else{
					winEditarTienda.show();
				}
				Ext.Msg.alert('SET',resultado);
			}
		});	
	}
	
	NS.nuevoRegistro=function(){
		var vectorTienda = {};
		var matrizTienda = new Array();
		vectorTienda.ccid=NS.txtTienda.getValue();
		vectorTienda.descSucursal=NS.txtNombre.getValue();
		vectorTienda.noAcreedor=NS.txtNoAcredor_editar.getValue();
		vectorTienda.fecAlta=NS.fecHoy;
		vectorTienda.razonSocial=NS.txtRazonSocial.getValue();
		vectorTienda.noEmpresa=NS.txtNoEmpresa.getValue();
		matrizTienda[0]=vectorTienda;
		var jSonString = Ext.util.JSON.encode(matrizTienda);
		MantenimientoTiendasAction.insertaMantenimientoTiendas(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined ) {
				if(resultado=='Exito'){
					NS.limpiarTodo();
					NS.buscar();
					winEditarTienda.hide();
				}else{
					winEditarTienda.show();
				}
				Ext.Msg.alert('SET',resultado);
			}	
		});
	}
	
	NS.editarRegistro=function(){
		var vectorTienda = {};
		var matrizTienda = new Array();
		vectorTienda.ccid=NS.txtTienda.getValue();
		vectorTienda.descSucursal=NS.txtNombre.getValue();
		vectorTienda.noAcreedor=NS.txtNoAcredor_editar.getValue();
		vectorTienda.motivo="U:"+NS.txtMotivo.getValue();
		vectorTienda.fechaMod=NS.fecHoy;
		vectorTienda.razonSocial=NS.txtRazonSocial.getValue();
		vectorTienda.noEmpresa=NS.txtNoEmpresa.getValue();
		matrizTienda[0]=vectorTienda;
		var jSonString = Ext.util.JSON.encode(matrizTienda);
		MantenimientoTiendasAction.updateMantenimientoTiendas(jSonString, function(resultado, e) {
			if (resultado != '' && resultado != null && resultado != undefined) {
				if(resultado=='Exito'){
					NS.limpiarTodo();
					NS.buscar();
					winEditarTienda.hide();
				}else{
					winEditarTienda.show();
				}
				Ext.Msg.alert('SET',resultado);
			}
		});
	}
	
	function verificaComponentesCreados(){
		var compt; 	
		/*** Ventana nuevo/editar***/
		compt = Ext.getCmp(PF + 'lbTienda');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtTienda');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbNoAcredor_editar');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtNoAcredor_editar');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbNombre');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtNombre');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'lbMotivo');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'txtMotivo');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtNoEmpresa');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'cmbEmpresas');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbNoEmpresa');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtRazonSocial');
		if(compt != null || compt != undefined ){ compt.destroy(); }
			
		compt = Ext.getCmp(PF + 'lbRazonSocial');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		/**************************/
	}
	
	//Se agrega el excel 09-feb-2016
	
	/**********************Generar el excel *********************************/
	 NS.validaDatosExcel = function() {		
		var registroSeleccionado = NS.gridTiendas.getSelectionModel().getSelections();
		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(registroSeleccionado.length == 0) {
			registroSeleccionado = NS.storeLlenaGridTiendas.data.items;
			if(registroSeleccionado.length == 0) {
				Ext.Msg.alert('SET','No hay registros para el reporte !!');
				return;
			}
		}
		
		if(NS.Excel) {
			
			var matriz = new Array();
			
			for(var i=0;i<registroSeleccionado.length;i++){
				var vectorTienda = {};
				vectorTienda.ccid=registroSeleccionado[i].get('ccid')
				vectorTienda.descSucursal=registroSeleccionado[i].get('descSucursal')
				vectorTienda.noAcreedor=registroSeleccionado[i].get('noAcreedor')
				vectorTienda.nomEmpresa=registroSeleccionado[i].get('nomEmpresa')
				vectorTienda.razonSocial=registroSeleccionado[i].get('razonSocial')
				vectorTienda.noEmpresa=registroSeleccionado[i].get('noEmpresa')
				matriz[i] = vectorTienda;
	 		
			}
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		NS.exportaExcel(jSonString);
		NS.excel = false;
	
		return;
	};
		
	NS.exportaExcel = function(jsonCadena) {
		MantenimientoTiendasAction.exportaExcel(jsonCadena, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=excelTiendas';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
		/****************Fin de generar excel ***********************/

	//Fin funciones
	
	
	//Store's
	NS.storeLlenaGridTiendas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {noAcreedor:''},
		paramOrder: ['noAcreedor'],
		directFn: MantenimientoTiendasAction.llenaGridTiendas,
		fields: [
		 	{name: 'ccid'},
		 	{name: 'descSucursal'},
		 	{name: 'noAcreedor'},
		 	{name: 'razonSocial'},
		 	{name: 'noEmpresa'},
		 	{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records) {
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existen Tiendas');
				}else{
					if (NS.txtNoAcredor.getValue()!=''||NS.txtNoAcredor.getValue() != null){
						NS.comparar();
					}
				}
				NS.txtNoAcredor.focus(true);
				NS.myMask.hide();
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}

		}	
	});
	NS.myMask.show();
	NS.storeLlenaGridTiendas.load();
	
	//Fin Store's
	
	//Grid Tiendas
	
	NS.columnaSeleccionTiendas = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	
	NS.columnasTiendas = new Ext.grid.ColumnModel
	([
	  NS.columnaSeleccionTiendas,
	  {header: 'Tienda', width: 250, dataIndex: 'ccid', sortable: true},
	  {header: 'Nombre', width: 410, dataIndex: 'descSucursal', sortable: true},
	  {header: 'Numero Acredor', width: 290, dataIndex: 'noAcreedor', sortable: true}	,
	  {header: 'Razon social', width: 290, dataIndex: 'razonSocial', sortable: true},
	  {header: 'No.Empresa', width: 290, dataIndex: 'noEmpresa', sortable: true},	
	  {header: 'Empresa', width: 290, dataIndex: 'nomEmpresa', sortable: true}	
	]);
			
	NS.gridTiendas = new Ext.grid.GridPanel
	({
		store: NS.storeLlenaGridTiendas,
		id: PF + 'gridTiendas',
		name: PF + 'gridTiendas',
		cm: NS.columnasTiendas,
		sm: NS.columnaSeleccionTiendas,
		x: 0,
		y: 0,
		width: 970,
		height: 350,
		stripRows: true,
		columnLines: true,
	});
	
	//PANEL
	NS.panelBusqueda = new Ext.form.FieldSet({
		title: '',
		x: 400,
		y: 0,
		width:500,
		height: 50,
		layout: 'absolute',
		items:
		[
		 	NS.lbNoAcredor,
			NS.txtNoAcredor,
			NS.cmbBeneficiario,
		 	{
		 		xtype: 'button',
		 		text: 'Buscar',
		 		x: 380,
		 		y: 5,
		 		width: 80,
		 		height: 22,
		 		tabIndex:2,
		 		listeners: {
		 			click:{
		 				fn: function(e){
		 					NS.buscar();
		 					NS.txtNoAcredor.focus(true);
		 					
		 				}
		 			}
		 		}
		 	}
		]
	});
	
	NS.panelComponentes = new Ext.form.FieldSet({
		title: '',
		x: 0,
		y: 60,
		width: 990,
		height: 420,
		layout: 'absolute',
		buttonAlign:'center',
		buttons:[
			      {text:'Nuevo',handler:function(){NS.opciones(1);}},
			      {text:'Editar',handler:function(){NS.opciones(2);}},
			      {text:'Eliminar',handler:function(){NS.opciones(3);}},
			      {text:'Excel',handler:function(){NS.Excel = true; NS.validaDatosExcel();}},
				 ],
		items:
		[	
			NS.gridTiendas,	
		]
	});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 20,
		y: 5,
		width: 1005,		
		height: 485,
		layout: 'absolute',
		items:
		[			 	 	
		 	NS.panelBusqueda,
		 	NS.panelComponentes,
		 	
	    ]
	});

	NS.conceptos = new Ext.FormPanel ({
		title: 'Tiendas',
		width: 1005,
		height: 485,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'conceptos',
		name: PF + 'conceptos',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	//Ventana emergente para editar registro o nuevo registro
	var winEditarTienda = new Ext.Window({
		title: 'Tiendas',
		modal: true,
		shadow: true,
		closeAction: 'hide',
		width: 350,
	   	height: 220,
	   	layout: 'absolute',
	   	plain: true,
	   	resizable:false,
	   	draggable:false,
	   	closable:false,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    buttons:[
	            {text:'Aceptar',handler:function(){NS.aceptar();}},
	            {text:'Cancelar',handler:function(){NS.cancelar();}},
	    ],
	   	items: [
				NS.lbTienda,
				NS.txtTienda,
				NS.lbNombre,
				NS.txtNombre,
				NS.lbNoAcredor_editar,
				NS.txtNoAcredor_editar,
				NS.lbMotivo,
				NS.txtMotivo,
				NS.txtNoEmpresa,
				NS.cmbEmpresas,
				NS.lbNoEmpresa,
				NS.txtRazonSocial,
				NS.lbRazonSocial
	   	        ],
	     listeners:{
	    	  hide:{fn:function(){NS.buscar(); }}
	     } 
	});
	NS.conceptos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});
 