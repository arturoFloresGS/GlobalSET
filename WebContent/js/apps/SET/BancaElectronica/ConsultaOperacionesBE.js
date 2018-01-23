/*
 * YEC 
 * 22-12-2015
 */
	Ext.onReady(function(){
	
	var NS = Ext.namespace('apps.SET.BancaElectronica.ConsultaOperacionesBE');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
	
	NS.idEmpresa='0';
	NS.chequera='';
	NS.noBanco='';
	NS.concepto='';
	NS.fecha=NS.fecHoy;
	NS.chequera='';
	NS.tipoMov='I';
	NS.idGrupo='';
	NS.contabiliza='false';
	NS.importe='0';
	NS.folio='';
	NS.referencia='';
	NS.rubro='';
	NS.sucursal='';
	NS.idRubro='';
	NS.texto='';
	NS.sbc='false';
	NS.contabi=' ';
	
	//variables globales de la clase
	NS.agrega=false;
	NS.nuevo=false;
	NS.busca=false;
	optCargo=false;
	optAbono=false;
	generico=true;
	/*
	lb_agrega = False
    lb_selecciono_grid = False
    lb_nuevo = False
    lb_seleccion = False
    lb_busca = False
    Optcargo = False
    Optabono = False
    lbGenerico = True
    lcl_i_renglon = -1*/
	
	/*
	 *  lbReferenciaPorChequera = False
    If gobjVarGlobal.valor_Configura_set(216) = "SI" Then
        lbReferenciaPorChequera = True
    End If
   
	 */
	
	
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
			load: function(s, records){
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

	NS.lbEmpresa = new Ext.form.Label({
		text: 'Empresa:',
        x: 0,
        y: 25	
	});
	
	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
        name:PF + 'txtNoEmpresa',
		x: 0,
        y: 40,
        width: 40,
        listeners: {
			change: {
				fn: function(caja,valor) {
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
						valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
						if(valueCombo != null && valueCombo != undefined && valueCombo != ''){
							NS.idEmpresa=caja.getValue();
						}else{
							caja.setValue('');
							NS.cmbEmpresas.reset();
						}	
					}else {
						caja.setValue(' ');
						NS.cmbEmpresas.reset();
					}
                }
			}
    	}
    });
	
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 55,
        y: 40,
        width: 280,
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
				if(records.length==null || records.length<=0){
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
        x: 0,
        y: 85,
        width: 40,
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
        ,x: 55
        ,y: 85
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
	
	
	/**************Para el panel de busqueda *********/
	
	NS.storeBancosPB = new Ext.data.DirectStore({ //PB->panel busqueda
		paramsAsHash: false,
		baseParams:{noEmpresa:'0'},
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
	NS.storeBancosPB.load();
	
	NS.cmbBancosPB= new Ext.form.ComboBox({
		store: NS.storeBancosPB
		,name: PF+'cmbBancosPB'
		,id: PF+'cmbBancosPB'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 105
        ,y: 0
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
					
					BFwrk.Util.updateComboToTextField(PF+'txtIdBancoPB',NS.cmbBancosPB.getId());
								
				}
			}
		}
	});

	NS.txtIdBancoPB = new Ext.form.TextField({
		id: PF+'txtIdBancoPB',
        name: PF+'txtIdBancoPB',
        x: 50,
        y: 0,
        width: 40,
        listeners:{
        	change:{
       	         fn:function(caja, valor){
					var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBancoPB',NS.cmbBancosPB.getId());
           			
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
		directFn: ConsultaOperacionesBEAction.obtenerConceptos, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','Error, Para el banco seleccionado no existen conceptos');
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
        ,x: 0
        ,y: 230
        ,width: 340
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
		directFn: ConsultaOperacionesBEAction.llenarComboChequeras, 
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
        ,x: 60
        ,y: 120
        ,width: 140
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccionar chequera'
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
	
	/**********Panel busqueda*******/
	NS.cmbChequerasPB = new Ext.form.ComboBox({
		store: NS.storeChequeras
		,name: PF+'cmbChequeras'
		,id: PF+'cmbChequeras'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 320
        ,y: 0
        ,width: 140
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccionar chequera'
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
	
	/*********************************************************************
     *							COMBO GRUPO 						  	 *
	 *********************************************************************/
	
	NS.txtIdGrupo = new Ext.form.TextField({
		name: PF+'txtIdGrupo',
        id: PF+'txtIdGrupo',
		x: 0,
		y: 0,
		width: 50,
		//disabled: true,
		enableKeyEvents:true,
        listeners: {
        	change: {
        		fn:function(caja) {
    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
            			valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdGrupo',NS.cmbGrupo.getId());
            			
            			if(valueCombo != null && valueCombo != undefined && valueCombo != '')
            				NS.idGrupo=valueCombo;
            			else{
            				caja.setValue('' );
            				Ext.getCmp(PF + 'txtIdGrupo').reset();
            			}
    	   			}else
    	   				NS.cmbGrupo.reset();
        		}
        	}
        }
	});
	
	NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		root: '',
		directFn: ConsultaOperacionesBEAction.llenaComboGrupo,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupo, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					
					
				}
			}
		}
	});
	NS.storeGrupo.load();
	
	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo
		,name: PF+'cmbGrupo'
		,id: PF+'cmbGrupo'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 60
        ,y: 0
        ,width: 260
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccionar grupo'
		,triggerAction: 'all'
		,indexTab: 25
		//,disabled: true
		,value: ''
		,visible: false
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idGrupo=combo.getValue();
					NS.txtIdRubroAl.setValue('');
					NS.txtIdGrupo.setValue(combo.getValue());
					NS.cmbRubroAl.reset();
					NS.storeRubro.baseParams.idGrupo=combo.getValue()+'';
					NS.cmbRubroAl.setDisabled(false);
					NS.storeRubro.load();
				}
			}
		}
	});
	
	
	/*********************************************************************
     *							COMBO RUBRO 						  	 *
	 *********************************************************************/
	NS.txtIdRubroAl = new Ext.form.TextField({
		name: PF+'txtIdRubroAl',
        id: PF+'txtIdRubroAl',
		x: 0,
		y: 0,
		width: 50,
		//disabled: true,
		enableKeyEvents:true,
        listeners: {
        	change: {
        		fn:function(caja) {
    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
            			valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdRubroAl',NS.cmbRubroAl.getId());
            			if(valueCombo != null && valueCombo != undefined && valueCombo != ''){
            				NS.idRubro=valueCombo;
            				ConsultaOperacionesBEAction.contabiliza(NS.idRubro+'', function(res, e){
            		   			if (res != null && res != undefined && res == '') {
            		   				Ext.Msg.alert('SET', "No hay datos para generar el archivo");
            		   			}else{
            		   				NS.contabi=res[0].equivaleRP;
            		   				if(res[0].bcontabiliza=='S'){
            		   					NS.chkContabiliza.setValue(true);
            		   				}else{
            		   					NS.chkContabiliza.setValue(false);
            		   				}
            		   			}
            				});
            				
            			}else{
            				Ext.getCmp(PF + 'txtIdRubroAl').reset();
            				NS.idRubro='';
            				caja.setValue('');
            			}
    	   			}else
    	   				NS.cmbRubroAl.reset();
        		}
        	}
        }
	});
	
	NS.storeRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idGrupo:''
		},
		root: '',
		paramOrder:['idGrupo'],
		root: '',
		directFn: ConsultaOperacionesBEAction.llenaComboRubro, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubro, msg:"Cargando..."});
				if(records.length==null || records.length<=0){
					
				}
			}
		}
	}); 
	
	NS.cmbRubroAl = new Ext.form.ComboBox({
		store: NS.storeRubro
		,name: PF+'cmbRubroAl'
		,id: PF+'cmbRubroAl'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		//,disabled: true
      	,x: 60
        ,y: 0
        ,width: 260
        ,indexTab: 28
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccionar rubro'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					
				}
			}
		}
	});
	/**************************FIN COMBOS*************************************/

	/***COMPONENTES DEL FORMULARIO ***/

	NS.lbBancoPB = new Ext.form.Label({
		text: 'Banco:',
        x: 0,
        y: 5		
	});
	
	NS.lbChequeraPB = new Ext.form.Label({
		text: 'Chequera:',
        x: 260,
        y: 5		
	});

	NS.lbBanco = new Ext.form.Label({
		text: 'Banco:',
        x: 0,
        y: 70		
	});

	NS.lbChequera = new Ext.form.Label({
		text: 'Chequera:',
        x: 0,
        y: 125		
	});
	
	NS.lbImporte = new Ext.form.Label({
		text: 'Importe:',
        x: 0,
        y: 155	
	});
	
	NS.lbFolio = new Ext.form.Label({
		text: 'Folio:',
        x: 0,
        y: 185	
	});

	NS.lbConcepto = new Ext.form.Label({
		text: 'Concepto:',
        x: 0,
        y: 210	
	});
	
	NS.lbSucursal = new Ext.form.Label({
		text: 'Sucursal:',
        x: 0,
        y: 335	
	});
	
	NS.lbReferencia = new Ext.form.Label({
		text: 'Referencia:',
        x: 0,
        y: 265	
	});
	
	NS.lbRubro = new Ext.form.Label({
		text: 'Rubro:',
        x: 0,
        y: 300	
	});
	
	
	
	
	
	NS.lbTexto = new Ext.form.Label({
		text: 'Texto:',
        x: 0,
        y: 500	
	});

	NS.lbLinea = new Ext.form.Label({
		xtype: 'label',
    	text: '___________________',
    	x: 0,
    	y:35
	});

	NS.lbFecha = new Ext.form.Label({
		text: 'Fecha:',
        x: 200,
        y: 10		
	});
	
	
	NS.txtSucursal = new Ext.form.TextField({
		name: PF+'txtSucursal',
        id: PF+'txtSucursal',
        x: 60,
        y: 330,
        width: 280,
        enableKeyEvents:true,
        listeners: {
        	change: {
        		fn:function(caja) {
    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
    	   				NS.sucursal=caja.getValue();
    	   			}else
    	   				NS.sucursal='';
        		}
        	}
        }
	});
	
	NS.txtReferencia = new Ext.form.TextField({
		name: PF+'txtReferencia',
        id: PF+'txtReferencia',
        x: 60,
        y: 260,
        width: 280,
        enableKeyEvents:true,
        listeners: {
        	change: {
        		fn:function(caja) {
    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
    	   				NS.referencia=caja.getValue();
    	   			}else
    	   				NS.referencia='';
        		}
        	}
        }
	});
	
	NS.txtRubro = new Ext.form.TextField({
		name: PF+'txtRubro',
        id: PF+'txtRubro',
        x: 60,
        y: 295,
        width: 280,
        enableKeyEvents:true,
        listeners: {
        	change: {
        		fn:function(caja) {
    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
    	   				NS.rubro=caja.getValue();
    	   			}else
    	   				NS.rubro='';
        		}
        	}
        }
	});
	
	
	NS.txtImporte = new Ext.form.NumberField({
		name: PF+'txtImporte',
        id: PF+'txtImporte',
        x: 50,
        y: 150,
        enableKeyEvents:true,
        listeners: {
        	change: {
        		fn:function(caja) {
    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
            			NS.importe=caja.getValue();
    	   			}else
    	   				NS.importe='';
        		}
        	}
        }
	});
	
	NS.txtFolio = new Ext.form.TextField({
		name: PF+'txtFolio',
        id: PF+'txtFolio',
        x: 50,
        y: 180,
        enableKeyEvents:true,
        listeners: {
        	change: {
        		fn:function(caja) {
    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
            			NS.folio=caja.getValue();
    	   			}else
    	   				NS.folio='';
        		}
        	}
        }
	});
	
	NS.txtTexto = new Ext.form.TextArea({
		name: PF+'txtTexto',
        id: PF+'txtTexto',
        x: 50,
        y: 485,
        width: 290,
        height: 45,
        enableKeyEvents:true,
        listeners: {
        	change: {
        		fn:function(caja) {
    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
            			NS.texto=caja.getValue();
    	   			}else
    	   				NS.texto='';
        		}
        	}
        }
	});

	NS.txtFecha = new Ext.form.DateField({
		name: PF+'txtFecha',
        id: PF+'txtFecha',
        x: 240,
        y: 5,
        width: 90,
		value: NS.fecHoy,
        format: 'd/m/Y',
        enableKeyEvents:true,
		listeners:{
	 		change:{
	 			fn:function(caja, e) {	
	 				NS.fecha=cambiarFecha(caja.getValue()+'')+'';
	 			}
	 		}
		}
	});

	NS.chkContabiliza = new Ext.form.Checkbox({
		boxLabel: 'Contabiliza',
        id: PF+'chkContabiliza',
        name: PF+'chkContabiliza',
        value: false,
        x: 200,
    	y:85
	});

	NS.chkSbc = new Ext.form.Checkbox({
		boxLabel: 'Salvo buen cobro',
        name: PF+'chkSbc',
        value: false,
        x: 0,
    	y:48
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
	     ]
	});
	
	/***********************************************************************
	 *  GRID DE BE
	 */
	//Store's
	NS.storeLlenaGrid = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {folioBanco:'',idChequera:''},
		paramOrder: ['folioBanco', 'idChequera'],
		directFn: ConsultaOperacionesBEAction.llenaGrid,
		fields: [
		 	{name: 'secuencia'},
		 	{name: 'cargoAbono'},
		 	{name: 'folioBanco'},
		 	{name: 'importe'},
		 	{name: 'sbc'},
		 	{name: 'referencia'},
		 	{name: 'concepto'},
		 	{name: 'sucursal'},
		 	{name: 'idEstatusTraspaso'},
		 	{name: 'fecValor'},
		    //{name: 'grabados'},
		 	{name: 'observacion'},
		 	{name: 'idRubro'},
		 	{name: 'bTraspBanco'},
		    {name: 'bTraspConta'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Buscando..."});
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No existen ');}
			}
		}	
	});
	NS.storeLlenaGrid.load();
	
	//Fin Store's
	
	//Grid 
	
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	
	NS.columnas = new Ext.grid.ColumnModel
	([
	  NS.columnaSeleccion,
	  {header: 'Secuencia', width: 100, dataIndex: 'secuencia', sortable: true},
	  {header: 'Cargo abono', width: 80, dataIndex: 'cargoAbono', sortable: true},
	  {header: 'Folio Banco', width: 100, dataIndex: 'folioBanco', sortable: true},	
	  {header: 'Importe', width: 100, dataIndex: 'importe', sortable: true},
	  {header: 'S.B.C.', width: 40, dataIndex: 'sbc', sortable: true},
	  {header: 'Referencia', width: 200, dataIndex: 'referencia', sortable: true},
	  {header: 'Concepto', width: 200, dataIndex: 'concepto', sortable: true},
	  {header: 'Sucursal', width: 180, dataIndex: 'sucursal', sortable: true},
	  {header: 'Id Estatus traspaso', width: 100, dataIndex: 'idEstatusTraspaso', sortable: true},
	  {header: 'Fecha', width: 100, dataIndex: 'fecValor', sortable: true},
	 // {header: 'Grabados', width: 20, dataIndex: 'grabados', sortable: true},
	  {header: 'Observacion', width: 100, dataIndex: 'observacion', sortable: true},
	  {header: 'Id Rubro', width: 80, dataIndex: 'idRubro', sortable: true},
	  {header: 'Traspaso Banco', width: 100, dataIndex: 'bTraspBanco', sortable: true},
	  {header: 'Traspaso Conta', width: 100, dataIndex: 'bTraspBanco', sortable: true},
	]);
	
	NS.grid = new Ext.grid.GridPanel
	({
		store: NS.storeLlenaGrid,
		id: PF + 'grid',
		name: PF + 'grid',
		cm: NS.columnas,
		sm: NS.columnaSeleccion,
		x: 0,
		y: 0,
		width: 630,
		height: 350,
		stripRows: true,
		columnLines: true,
	});
	
	/**************Fin del grid **************************/
	//Field set para radios
	
	
	NS.panelTipo = new Ext.form.FieldSet ({
		title: '',
        x: 215,
        y: 115,
        width: 125,
        height: 88,
        layout: 'absolute',
		items: [
		    NS.lbLinea ,
		 	NS.radTipo,
		 	NS.chkSbc
		]
	});
	
	NS.panelGrupo = new Ext.form.FieldSet ({
		title: 'Grupo',
		x: 0,
		y: 355,
		width: 340,
		height: 58,
		layout: 'absolute',
		items: [
			NS.txtIdGrupo,
			NS.cmbGrupo,
		]
	});

	NS.panelRubro = new Ext.form.FieldSet ({
		title: 'Rubro',
		x: 0,
		y: 420 ,
		width: 340,
		height: 58,
		layout: 'absolute',
		items: [
			NS.txtIdRubroAl,
			NS.cmbRubroAl,
		]
	});

	NS.panelComponentes = new Ext.form.FieldSet ({
		title: '',
	    x: 680,
	    y: 0,
	    width: 370,
	    height: 590,
	    layout: 'absolute',
	    buttonAlign: 'center',
		buttons:[
		      { text:'Guardar',handler:function(){NS.guardar(); } },
		      { text:'Limpiar',handler:function(){ /* Limpiar*/} },
		      { text:'Cancelar',handler:function(){ /* Cancelar*/ } },
		],
		items: [
		 	NS.txtNoEmpresa,
            NS.cmbEmpresas,
            NS.lbEmpresa, 
			NS.lbBanco,
			NS.lbChequera,
			NS.lbConcepto,
			NS.lbFecha,
			NS.txtFecha,
			NS.txtFecha,
			NS.cmbBancos,
			NS.txtIdBanco,
	        NS.cmbChequeras,
	        NS.cmbConceptos,
	        NS.chkContabiliza,
			NS.panelTipo ,
			NS.lbImporte,
			NS.lbReferencia,
			NS.lbRubro,
			NS.lbFolio,
			NS.lbSucursal,
			NS.lbTexto,
			NS.txtImporte,
			NS.txtReferencia,
			NS.txtRubro,
			NS.txtFolio,
			NS.txtSucursal,
			NS.txtTexto,
			NS.panelGrupo,
			NS.panelRubro,
			
			
		]
	});
	
	NS.panelBusqueda = new Ext.form.FieldSet ({
		title: 'Busqueda',
        x: 10,
        y: 0,
        width: 660,
        height: 60,
        layout: 'absolute',
		items: [
			NS.cmbBancosPB,
			NS.txtIdBancoPB,
			NS.cmbChequerasPB, 
			NS.lbBancoPB,
			NS.lbChequeraPB,
			{
				xtype: 'button',
		 		text: 'Buscar',
		 		x: 480,
		 		y: 0,
		 		width: 80,
		 		height: 22,
		 		listeners: {
		 			click:{
		 				fn: function(e){
		 					//
		 				}
		 			}
		 		}
			},
		]
	});
	
	NS.panelGrid = new Ext.form.FieldSet ({
		title: 'Banca Electronica',
        x: 10,
        y: 70,
        width: 660,
        height: 430,
        layout: 'absolute',
        buttonAlign: 'center',
		buttons:[
		      { text:'Crear Nuevo',handler:function(){/*Guardar */ } },
		      { text:'Modificar',handler:function(){ /* Limpiar*/} },
		      { text:'Eliminar',handler:function(){ /* Limpiar*/} },
		      { text:'Limpiar',handler:function(){ /* Cancelar*/ } },
		],
		items: [
		        NS.grid
		]
	});

	//panel principal

	NS.contenedorMovimientos =new Ext.FormPanel({
    title: 'Movimientos de Banca Electrónica',
    width: 970,
    height: 500,
    padding: 0,
    frame: true,
    autoScroll: true,
    layout: 'absolute',
    renderTo: NS.tabContId,
    items : [
			NS.panelComponentes,
			NS.panelBusqueda,
			NS.panelGrid
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
    
    NS.guardar=function(){
    	var continuar=1;
    	if(NS.chkContabiliza.checked){
    		if(NS.contabi==' '){
    			NS.chkContabiliza.setDisabled(true);
    			NS.chkContabiliza.setValue(false);
    			 Ext.Msg.confirm( 'SET', 'No es posible contabilizar, ya que falta la equivalencia del rubro. ¿Desea continuar?' , function(btn){
 					if(btn=='yes'){
 						continuar=1;
 					}else{
 						continuar=0;
 					}
    			 })
    		}else{
    			NS.chkContabiliza.setDisabled(false);
    			 Ext.Msg.confirm( 'SET', 'El registro sera enviado al AS-400 . ¿Desea continuar?' , function(btn){
  					if(btn=='yes'){
  						continuar=1;
  					}else{
  						continuar=0;
  					}
    			 })
    		}
    	}
    	
    	if(NS.idGrupo=='' || NS.idRubro==''){
    		continuar=0;
    	}
    	
    	if(NS.fecha==''){
    		continuar=0;
    	}
      	
    	if(continuar==1){
    		var matriz = new Array();
    		var vector = {};
    		vector.fecha=NS.fecha;
    		vector.idEmpresa=NS.idEmpresa;
    		vector.noBanco=NS.noBanco;
    		if(NS.chkContabiliza.checked){NS.contabiliza='true';}else{NS.contabiliza='false';}
    		if(NS.chkSbc.checked){NS.sbc='true';}else{NS.sbc='false';}
    		
    		vector.contabiliza=NS.contabiliza;
    		vector.chequera=NS.chequera;
        	vector.importe=NS.importe;
        	vector.sbc=NS.sbc;
        	vector.texto=NS.texto;
        	vector.sucursal=NS.sucursal;
        	vector.rubro=NS.rubro;
        	vector.referencia=NS.referencia;
        	vector.folio=NS.folio;
        	vector.concepto=NS.concepto;
        	NS.tipoMov='I';
        	var radio= NS.radTipo.getValue();
       		if(radio != null || radio != undefined ){
    			tipoMovimiento=NS.radTipo.getValue().getGroupValue();
    			vector.tipoMov=tipoMovimiento;
    		}
       		vector.idGrupo=NS.idGrupo;
       		vector.idRubro=NS.idRubro;
       		matriz[int] = vector; 
    		var jSon = Ext.util.JSON.encode(matriz);
    	}
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
	   	       	ConsultaOperacionesBEAction.consultaExcel(jSonString, function(res, e){
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
									vectorR.fecValor=res[int].fecValor.substring(0,11);
									vectorR.sucursal=res[int].sucursal;
									vectorR.referencia=res[int].referencia;
									vectorR.idDivisa=res[int].idDivisa;
									vectorR.importe=res[int].importe;
									vectorR.observacion=res[int].observacion;
									vectorR.folioDet=res[int].folioDet;
									vectorR.fecAlta=res[int].fecAlta.substring(0,11);
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
									vectorR.fecValor=res[int].fecValor.substring(0,11);
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
									vectorR.fecAlta=res[int].fecAlta.substring(0,11);
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
   		ConsultaOperacionesBEAction.exportaExcel(jsonCadena, function(res, e){
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