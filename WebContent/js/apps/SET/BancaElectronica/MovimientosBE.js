/**
 * @author Jessica Arelly Cruz Cruz
 * @since 01/03/2011
 */

/*
 * Modificado por YEC
 * 08-12-2015
 * Se agrego la funcion de generar excel
 */
	Ext.onReady(function(){
	
	var NS = Ext.namespace('apps.SET.BancaElectronica.MovimientosBE');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
	
	/****YEC 07/12/2015 ***/
	NS.idEmpresa='0';
	NS.chequera='';
	NS.noBanco='';
	NS.concepto='';
	NS.tipo='T';
	NS.movimiento='0';
	NS.fechaIni=NS.fecHoy;
	NS.fechaFin=NS.fecHoy;
	NS.detalle='false'; 
	NS.movTEF='0';
	NS.movDia='0'
	NS.chequera='';
	NS.tipoMov='';
	NS.detalle='false';
	NS.conceptosDesconocidos='false';
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Procesando, por favor espere..."});

	/*****Combos con su caja y su store  EMPRESAS,BANCOS,CONCEPTOS,CHEQUERAS ***/

	/************************************************************************
	 * 							COMBO EMPRESAS
	 ************************************************************************/
		
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
			load: function(s, records)
			{
				var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storeEmpresas, msg: "Cargando Datos ..."});
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No hay empresas que mostrar');
				}else{
					var recordsStoreGruEmp = NS.storeEmpresas.recordType;	
					var todas = new recordsStoreGruEmp({
				       	id : '0',
				       	descripcion : '***********TODAS***********'
			       	});
					NS.storeEmpresas.insert(0,todas);
				}
					
			}
		}
	}); 
	NS.storeEmpresas.load();

	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
        name:PF + 'txtNoEmpresa',
		x: 10,
        y: 15,
        width: 50,
        tabIndex: 0,
 	enableKeyEvents:true,
	listeners:{
		/*keypress:{
 			fn:function(caja, e) {
 				if(e.keyCode ==13){ 
 					NS.idEmpresa=caja.getValue();
					NS.storeBancos.removeAll();
					NS.storeChequeras.removeAll();
					Ext.getCmp(PF+'cmbChequeras').setValue('');
					Ext.getCmp(PF+'txtIdBanco').setValue('');
					Ext.getCmp(PF+'cmbBancos').setValue('');
					NS.storeBancos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
					NS.storeBancos.load();
 				}
 			}
 		},*/
	    blur: {
			fn: function(caja,valor) {
				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
						valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
				else {
					Ext.getCmp(PF + 'txtNoEmpresa').setValue('0');
					NS.cmbEmpresas.reset();
				}
				NS.idEmpresa=caja.getValue();
				if(NS.idEmpresa!= null && NS.idEmpresa!= undefined){
					NS.storeBancos.removeAll();
					NS.storeChequeras.removeAll();
					Ext.getCmp(PF+'cmbChequeras').setValue('');
					Ext.getCmp(PF+'txtIdBanco').setValue('');
					Ext.getCmp(PF+'cmbBancos').setValue('');
					NS.storeBancos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
					NS.storeBancos.load();
				}
            }
		}
	}
    });
	
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 70,
        y: 15,
        width: 310,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'id',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idEmpresa=combo.getValue();
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());					
					NS.storeBancos.removeAll();
					NS.storeChequeras.removeAll();
					Ext.getCmp(PF+'cmbChequeras').setValue('');
					Ext.getCmp(PF+'txtIdBanco').setValue('');
					Ext.getCmp(PF+'cmbBancos').setValue('');
					NS.storeBancos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
					NS.storeBancos.load();
				}
			}
		}
	});
	/************************************************************************
	 * 							COMBO BANCOS 								*
	 ************************************************************************/
	
	
	//store bancos
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{noEmpresa:NS.idEmpresa},
		root: '',
		paramOrder:['noEmpresa'],
		root: '',
		directFn: MovimientosBEAction.llenarComboBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No hay bancos disponibles para la empresa');
				}
				if(records.campoUno=="Error de conexion"){
					Ext.Msg.alert('SET','Error de conexion');
				}
			}
		}
	}); 

	NS.txtIdBanco = new Ext.form.TextField({
		id: PF+'txtIdBanco',
        name: PF+'txtIdBanco',
        x: 10,
        y: 60,
        width: 50,
        listeners:{
        	change:{
       	         fn:function(caja, valor){
					var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBanco',NS.cmbBancos.getId());
           			NS.accionarCmbBancos(comboValue);
           			NS.noBanco=caja.getValue();
       	         }
       		}
   		}
    });
	
	
	NS.accionarCmbBancos = function(comboValor){
		Ext.getCmp(PF+'cmbChequeras').setValue('');
		Ext.getCmp(PF+'cmbConceptos').setValue('');
		NS.storeConceptos.baseParams.noBanco = comboValor;
		NS.storeConceptos.load();
		NS.storeChequeras.baseParams.noBanco = comboValor;		
		NS.storeChequeras.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
		NS.storeChequeras.load();
		NS.descBanco = NS.storeBancos.getById(comboValor).get('descripcion');
	}
	
	//combo bancos
	NS.cmbBancos = new Ext.form.ComboBox({
		store: NS.storeBancos
		,name: PF+'cmbBancos'
		,id: PF+'cmbBancos'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 70
        ,y: 60
        ,width: 140
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.noBanco=combo.getValue();
					BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBancos.getId());
					NS.accionarCmbBancos(combo.getValue());				
				}
			}
		}
	});

	/************************************************************************
	 * 							COMBO CONCEPTOS								*
	 ************************************************************************/
	
	NS.storeConceptos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{noBanco: 0},
		root: '',
		paramOrder:['noBanco'],
		root: '',
		directFn: MovimientosBEAction.obtenerConceptos, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','Para el banco seleccionado no existen conceptos');
				}
				if(records.campoUno=="Error de conexion"){
					Ext.Msg.alert('SET','Error de conexion');
				}
			}
		}
	}); 
	
	// combo conceptos
	NS.cmbConceptos = new Ext.form.ComboBox({
		store: NS.storeConceptos
		,name: PF+'cmbConceptos'
		,id: PF+'cmbConceptos'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 180
        ,width: 370
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un concepto'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						NS.concepto='';
						NS.concepto=combo.getValue();
				}
			}
		}
	});

	/************************************************************************
	 * 							COMBO CHEQUERAS								*
	 ************************************************************************/
	
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noBanco: 0,
			noEmpresa:NS.GI_ID_EMPRESA
		},
		root: '',
		paramOrder:['noBanco','noEmpresa'],
		root: '',
		directFn: MovimientosBEAction.llenarComboChequeras, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay chequeras disponibles para el banco');
				}
			}
		}
	});
	
	//combo chequeras
	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras
		,name: PF+'cmbChequeras'
		,id: PF+'cmbChequeras'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 230
        ,y: 60
        ,width: 150
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						NS.chequera=combo.getValue();
				}
			}
		}
	});

	/***COMPONENTES DEL FORMULARIO ***/

	NS.lbEmpresa = new Ext.form.Label({
		text: 'Empresa:',
        x: 10,
        y: 0	
	});

	NS.lbBanco = new Ext.form.Label({
		text: 'Banco:',
        x: 10,
        y: 40		
	});

	NS.lbChequera = new Ext.form.Label({
		text: 'Chequera:',
        x: 230,
        y: 40		
	});

	NS.lbConcepto = new Ext.form.Label({
		text: 'Concepto:',
        x: 10,
        y: 160	
	});

	NS.lbLinea = new Ext.form.Label({
		xtype: 'label',
    	text: '____________________________________________',
    	x: 0,
    	y:55
	});

	NS.lbFecha = new Ext.form.Label({
		text: 'Fecha:',
        x: 10,
        y: 100		
	});

	//FECHAS INICIAL Y FINAL

	NS.txtFechaIni = new Ext.form.DateField({
		name: PF+'txtFechaIni',
        id: PF+'txtFechaIni',
        x: 60,
        y: 0,
        width: 170,
		value: NS.fecHoy,
        x: 10,
        y: 120,
        width: 95,
        format: 'd/m/Y',
        enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {	
	 				NS.fechaIni=cambiarFecha(caja.getValue()+'')+'';
	 				var fechaFin = Ext.getCmp(PF + 'txtFechaFin').getValue();
        			if(fechaFin < caja.getValue()&& NS.fechaFin!=''){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
        				Ext.getCmp(PF + 'txtFechaFin').setValue('');
        			}
	 			}
	 		}
		}
	});

	NS.txtFechaFin = new Ext.form.DateField({
		format: 'd/m/Y',
        id: PF+'txtFechaFin',
        name: PF+'txtFechaFin',
        value: NS.fecHoy,
        x: 120,
        y: 120,
        width: 95,
        enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {
	 				NS.fechaFin=cambiarFecha(caja.getValue()+'')+'';
	 				var fechaIni = Ext.getCmp(PF + 'txtFechaIni').getValue();
        			if(fechaIni > caja.getValue()&& NS.fechaIni!=''){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
        				Ext.getCmp(PF + 'txtFechaIni').setValue('');
        			}
	 			}
	 		}
	 	}
	});

	NS.chkDetalle = new Ext.form.Checkbox({
		boxLabel: 'Detalle',
        id: PF+'chkDetalle',
        name: PF+'chkDetalle',
        hidden: true,
        value: false,
        /*x: 10,
        y: 340,*/
        x: 0,
    	y:84
	});
	
	NS.radMovimiento = new Ext.form.RadioGroup({
		id: PF + 'optMov',
		name: PF + 'optMov',
		x: 0,
		y:0,
		columns: 1, 
		items: [
	          {boxLabel: 'Capturado',id: PF + 'optCapturado', name: PF+'optMov', inputValue: 0,x:0 ,y:0 ,checked: true},  
	          {boxLabel: 'Banca Electrónica',id: PF + 'optBE', name: PF+'optMov', inputValue: 1 ,x:0,y:20},  
	     ]
	});

	NS.radTipo = new Ext.form.RadioGroup({
		id: PF + 'optTipo',
		name: PF + 'optTipo',
		x: 0,
		y:0,
		columns: 1, 
		items: [
	          {boxLabel: 'Ingreso',id: PF + 'optIngreso', name: PF+'optTipo', inputValue: 'I',x:0 ,y:0},  
	          {boxLabel: 'Egreso',id: PF + 'optEgreso', name: PF+'optTipo', inputValue: 'E' ,x:0,y:20},
	          {boxLabel: 'Todos',id: PF + 'optTodos', name: PF+'optTipo', inputValue: '%' ,x:0 ,y:40,checked: true},  
	          { boxLabel: 'Conceptos Desconocidos',
	            id: PF + 'optConceptosDesconocidos',
	            name: PF+'optTipo',
	            inputValue: 'true' ,
	            x:0,
	            y:60, 
	          	listeners:{
					check:{
						fn:function(opt, valor){
							if(valor==true){
								Ext.getCmp(PF+'chkDetalle').show();
								NS.panelMovimiento.hide();
							}else{
								Ext.getCmp(PF+'chkDetalle').hide();
								NS.panelMovimiento.show();
							}
						}
					}
	      		}
	          }
	     ]
	});

	//Filed set para radios
	NS.panelTipo = new Ext.form.FieldSet ({
		title: 'Tipo',
        x: 160,
        y: 220,
        width: 220,
        height: 140,
        layout: 'absolute',
		items: [
		    NS.lbLinea ,
		 	NS.radTipo,
		 	NS.chkDetalle
		]
	});

	NS.panelMovimiento = new Ext.form.FieldSet ({
		title: 'Movimiento',
        id: PF+'frameMovimiento',
        name: PF+'frameMovimiento',
        x: 10,
        y: 220,
        width: 120,
        height: 110,
        layout: 'absolute',
        items: [
            NS.radMovimiento
		]
	});

	NS.panelComponentes = new Ext.form.FieldSet ({
		title: '',
	    x: 0,
	    y: 0,
	    width: 410,
	    height: 440,
	    layout: 'absolute',
	    buttonAlign: 'center',
		buttons:[
		      { text:'Generar excel',id: PF+'btnExcel',handler:function(){
		    	  Ext.getCmp(PF+'btnExcel').setDisabled(true);
		    	  mascara.show();
		    	  NS.DatosExcel();  
		      } },
		      { text:'Limpiar',handler:function(){ NS.btnLimpiar();} },
		],
		items: [
		 	NS.txtNoEmpresa,
            NS.cmbEmpresas,
            NS.lbEmpresa, 
			NS.lbBanco,
			NS.lbChequera,
			NS.lbConcepto,
			NS.lbFecha,
			NS.txtFechaIni,
			NS.txtFechaFin,
			NS.cmbBancos,
	        NS.cmbChequeras,
	        NS.txtIdBanco,
	        
	        NS.cmbConceptos,
	        //NS.chkDetalle,
			NS.panelTipo ,
			NS.panelMovimiento,
		]
	});

	//panel principal

	NS.contenedorMovimientos =new Ext.FormPanel({
    title: 'Movimientos de Banca Electrónica',
    width: 398,
    height: 479,
    padding: 10,
    frame: true,
    autoScroll: true,
    layout: 'absolute',
    renderTo: NS.tabContId,
    items : [
			NS.panelComponentes,
   		 ]   
    });

    /***Funciones generales **/

    NS.btnLimpiar=function(){
    	NS.contenedorMovimientos.getForm().reset();
		//NS.obtenerFechaHoy();
		NS.empresaActual = true;
		NS.empresaTodas = false;
		NS.movimientoCapturado = true;
		NS.movimientoBanca = false;
		NS.tipoIngreso = false;
		NS.tipoEgreso = false;
		NS.tipoTodos = true;
		NS.tipoConceptos = false;
		NS.tipoMovimientos = false;
		NS.descBanco = '';
		NS.descConcepto = '';
		NS.chkTEF = false;
		NS.chkDia = false;
		NS.chkExcel = false;
		NS.chkDetalle = false;
    }

   /**********************Generar el excel *********************************/
   NS.DatosExcel = function() {
    	if (NS.noBanco=='') {
    		Ext.Msg.alert('SET', "Debe seleccionar un banco");
    		Ext.getCmp(PF+'btnExcel').setDisabled(false);
    		mascara.hide();
    	}else{ //banco no vacio
    		var matriz = new Array();
    		var vector = {};
	    	var radio=null;
    		//Se obtiene el tipo de movimiento E,I y si es de tipo desconocido el valor sera true
    		var conceptosDesconocidos='false';
       		var radio= NS.radTipo.getValue();
       		if(radio == null || radio == undefined ){
       			vector.tipoMov='%';
			}else{
				conceptosDesconocidos=NS.radTipo.getValue().getGroupValue();
				vector.tipoMov=conceptosDesconocidos;
			}
       		
       		if(conceptosDesconocidos!='true'&&(NS.fechaIni=='' || NS.fechaFin=='')){ //Se selecciono conceptos desconocidos
       				Ext.Msg.alert('SET', "Debe seleccionar una fecha de inicio y una fecha final de busqueda.");
       				Ext.getCmp(PF+'btnExcel').setDisabled(false);
       				mascara.hide();
       			
       		}else{//Fin de conceptos desconocidos
				vector.noEmpresa=NS.idEmpresa;
	       		vector.noBanco=NS.noBanco;
	       		vector.chequera=NS.chequera;
	       		vector.fechaIni=NS.fechaIni;
	       		vector.fechaFin=NS.fechaFin;
	       		vector.concepto=NS.concepto;
	       	
	       		//Se obtiene el origen del movimiento BE, o capturado
	       		var radio = NS.radMovimiento.getValue();
	       		if(radio == null || radio == undefined ){
	       			vector.origenMovimiento='0';
				}else{
					vector.origenMovimiento=NS.radMovimiento.getValue().getGroupValue();
				}
	       		if(NS.chkDetalle.checked){NS.detalle='true';}else{NS.detalle='false';}
	       		vector.detalle=NS.detalle;
	       		matriz[0] = vector;
	       		var jSonString = Ext.util.JSON.encode(matriz);
	   	       	MovimientosBEAction.consultaExcel(jSonString, function(res, e){
		   			if (res != null && res != undefined && res == "") {
		   				Ext.Msg.alert('SET', "No hay datos para generar el archivo");
		   				Ext.getCmp(PF+'btnExcel').setDisabled(false);
		   				mascara.hide();
		   			}else{
		   				var matrizR = new Array();
		   				var opcion=res[0].tipoConsulta;
		   				for (var int = 0; int < res.length; int++) {
		   					var vectorR = {};
		   					vectorR.opcion=opcion;
		   					vectorR.noEmpresa=res[int].noEmpresa;
							vectorR.nomEmpresa=res[int].nomEmpresa;
							vectorR.idBanco=res[int].idBanco;
							vectorR.cargoAbono=res[int].cargoAbono;
							vectorR.descBanco=res[int].descBanco;
							vectorR.idChequera=res[int].idChequera;
							switch (opcion) {
								case 'rConceptoConDetalle':
									vectorR.observacion=res[int].observacion;
									vectorR.secuencia=res[int].secuencia;
									if(res[int].fecValor!=null && res[int].fecValor!=""){
										vectorR.fecValor=res[int].fecValor.substring(0,10);
									}else{
										vectorR.fecValor=res[int].fecValor;
									}
									vectorR.sucursal=res[int].sucursal;
									vectorR.referencia=res[int].referencia;
									vectorR.idDivisa=res[int].idDivisa;
									vectorR.importe=res[int].importe;
									vectorR.folioDet=res[int].folioDet;
									if(res[int].fecAlta!=null && res[int].fecAlta!=""){
										vectorR.fecAlta=res[int].fecAlta.substring(0,10);
									}else{
										vectorR.fecAlta=res[int].fecAlta;
									}
									vectorR.bTraspBanco=res[int].bTraspBanco;
									vectorR.bTraspConta=res[int].bTraspConta;
									vectorR.ejecutado=res[int].ejecutado;
									vectorR.importeCa=res[int].importeCa;
									vectorR.conceptoSet=res[int].conceptoSet;
									vectorR.folioBanco=res[int].folioBanco;
									vectorR.descObservacion=res[int].descObservacion;
									vectorR.encabezados='21';
									break;
									
								case 'rConceptoSinDetalle':
									vectorR.concepto=res[int].concepto;
									vectorR.encabezados='7';
									break;
									
								case 'rBE':
									
									vectorR.observacion=res[int].observacion;
									vectorR.secuencia=res[int].secuencia;
									if(res[int].fecValor!=null && res[int].fecValor!=""){
										vectorR.fecValor=res[int].fecValor.substring(0,10);
									}else{
										vectorR.fecValor=res[int].fecValor;
									}
									vectorR.sucursal=res[int].sucursal;
									vectorR.referencia=res[int].referencia;
									vectorR.idDivisa=res[int].idDivisa;
									vectorR.importe=res[int].importe;
									vectorR.folioDet=res[int].folioDet;
									vectorR.bTraspBanco=res[int].bTraspBanco;
									vectorR.folioBanco=res[int].folioBanco;
									vectorR.ejecutado=res[int].ejecutado;
									vectorR.importeCa=res[int].importeCa;
									vectorR.conceptoSet=res[int].conceptoSet;
									vectorR.descObservacion=res[int].descObservacion;
									if(res[int].fecAlta!=null && res[int].fecAlta!=""){
										vectorR.fecAlta=res[int].fecAlta.substring(0,10);
									}else{
										vectorR.fecAlta=res[int].fecAlta;
									}
									vectorR.bTraspConta=res[int].bTraspConta;
									vectorR.encabezados='22';
									break;
							}
			   				matrizR[int] = vectorR;
		   				} 
		   				var jSonStringExcel = Ext.util.JSON.encode(matrizR);
		   				NS.exportaExcel(jSonStringExcel);
		   		   		NS.excel = false;
		   		   		return;
		   			}
		   		});
			}	
    	}//Fin else banco no vacio
   	};
   	
   	NS.exportaExcel = function(jsonCadena) {
   		MovimientosBEAction.exportaExcel(jsonCadena, function(res, e){
   			if (res != null && res != undefined && res == "") {
   				Ext.Msg.alert('SET', "Error al generar el archivo");
   			} else {
   				strParams = '?nomReporte=ConsultaMovsBancaE';
   				strParams += '&'+'nomParam1=nomArchivo';
   				strParams += '&'+'valParam1='+res;
   				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
   			}
   			mascara.hide();
   			Ext.getCmp(PF+'btnExcel').setDisabled(false);
   		});
   		
   	};
   	/****************Fin de generar excel ***********************/
   	
    function cambiarFecha(fecha){	
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";
		mesArreglo[8]="Sep";mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
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
	}
 	NS.contenedorMovimientos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
});