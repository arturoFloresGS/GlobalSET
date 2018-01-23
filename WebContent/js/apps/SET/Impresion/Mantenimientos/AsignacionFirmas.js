/*
 * Autor por YEC
 * 04-01-2016
 */
	Ext.onReady(function(){
	
	var NS = Ext.namespace('apps.SET.impresion.mantenimientos.asignacionFirmas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.idPersonaA="";
	NS.idPersonaB="";
	
	/*****Combos con su caja y su store  BANCO , CUENTA ***/

	/************************************************************************
	 * 							COMBO BANCOS 								*
	 ************************************************************************/
	
	//store bancos
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		root: '',
		directFn: AsignacionFirmasAction.llenarComboBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay bancos disponibles');
				}
			}
		}
	});
	NS.storeBancos.load();

	NS.txtIdBanco = new Ext.form.TextField({
		id: PF+'txtIdBanco',
        name: PF+'txtIdBanco',
        x: 50,
        y: 0,
        width: 50,
        listeners:{
        	change:{
       	         fn:function(caja, valor){
           			var err=1;
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
							var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBanco',NS.cmbBancos.getId());
							if(valueCombo!='' && valueCombo !='undefined' && valueCombo != null)
								err=0;
					}
					
					if(err==1){
						Ext.getCmp(PF + 'txtIdBanco').setValue('');
						NS.cmbBancos.reset();
						NS.storeCuentas.reset();
					}
           			NS.accionarCmbBancos(comboValue);
       	         }
       		}
   		}
    });
	
	
	NS.accionarCmbBancos = function(comboValor){
		NS.storeCuentas.removeAll();
		NS.cmbCuentas.reset();
		NS.storeCuentas.baseParams.idBanco = comboValor+'';	
		NS.storeCuentas.load();
		
		NS.cmbFirmante1.setDisabled(false);
		NS.cmbFirmante2.setDisabled(false);
		NS.cmbFirmante1.reset();
		NS.cmbFirmante2.reset();
		NS.idPersonaA="";
		NS.idPersonaB="";
		NS.txtNombreA.setValue("");
		NS.txtNombreB.setValue("");
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
        ,x: 110
        ,y: 0
        ,width: 170
		,valueField:'id'
		,displayField:'descripcion'
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,autocomplete:true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBancos.getId());
					NS.accionarCmbBancos(combo.getValue());			
				}
			}
		}
	});

	/************************************************************************
	 * 							COMBO CUENTA								*
	 ************************************************************************/
	
	NS.storeCuentas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idBanco: '',
		},
		root: '',
		paramOrder:['idBanco'],
		root: '',
		directFn: AsignacionFirmasAction.llenarComboCuentas, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay cuentas disponibles para el banco');
					NS.cmbCuentas.setDisabled(true);
				}else{
					NS.cmbCuentas.setDisabled(false);
				}
			}
		}
	});
	
	//combo chequeras

	NS.cmbCuentas = new Ext.form.ComboBox({
		store: NS.storeCuentas
		,name: PF+'cmbCuentas'
		,id: PF+'cmbCuentas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 380
        ,y: 0
        ,width: 150
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una cuenta'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,editable:true
		,disabled:true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.cmbFirmante1.reset();
				    NS.storeFirmanteA.removeAll();
					NS.storeFirmanteA.baseParams.idBanco = NS.txtIdBanco.getValue();
					NS.storeFirmanteA.baseParams.cuenta = combo.getValue();
					NS.storeFirmanteA.load();
					
					NS.cmbFirmante2.reset();
				    NS.storeFirmanteB.removeAll();
					NS.storeFirmanteB.baseParams.idBanco = NS.txtIdBanco.getValue();
					NS.storeFirmanteB.baseParams.cuenta = combo.getValue();
					NS.storeFirmanteB.load();
					
					NS.idPersonaA="";
					NS.idPersonaB="";
					NS.txtNombreA.setValue("");
					NS.txtNombreB.setValue("");
	
				}
			}
		}
	});
	
	/*********************************************************************
	*    COMBO FIRMANTE 1
	*********************************************************************/
	NS.storeFirmanteA = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			tipo: 'A', idBanco:'' ,cuenta: ''
		},
		root: '',
		paramOrder:['tipo','idBanco','cuenta'],
		root: '',
		directFn: AsignacionFirmasAction.llenarComboFirmantes, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					NS.cmbFirmante1.setDisabled(true);
				}else{
					NS.cmbFirmante1.setDisabled(false);
				}
			}
		}
	});

	NS.cmbFirmante1 = new Ext.form.ComboBox({
		store: NS.storeFirmanteA
		,name: PF+'cmbFirmante1'
		,id: PF+'cmbFirmante1'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 50
        ,width: 250
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: ''
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,disabled:true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.txtNombreA.setValue(combo.getRawValue());
					NS.idPersonaA=combo.getValue()+'';
				}
			}
		}
	});

	/*********************************************************************
	*    COMBO FIRMANTE 2
	*********************************************************************/
	NS.storeFirmanteB = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			tipo: 'B', idBanco:'' ,cuenta: ''
		},
		root: '',
		paramOrder:['tipo','idBanco','cuenta'],
		root: '',
		directFn: AsignacionFirmasAction.llenarComboFirmantes, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					NS.cmbFirmante2.setDisabled(true);
				}else{
					NS.cmbFirmante2.setDisabled(false);
				}
			}
		}
	});
	
	NS.cmbFirmante2 = new Ext.form.ComboBox({
		store: NS.storeFirmanteB
		,name: PF+'cmbFirmante2'
		,id: PF+'cmbFirmante2'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 310
        ,y: 50
        ,width: 250
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: ''
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,disabled:true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.txtNombreB.setValue(combo.getRawValue());
					NS.idPersonaB=combo.getValue()+'';
				}
			}
		}
		
	});

	/***COMPONENTES DEL FORMULARIO ***/

	NS.lbBanco = new Ext.form.Label({
		text: 'Banco:',
        x: 10,
        y: 	5	
	});

	NS.lbCuenta = new Ext.form.Label({
		text: 'Cuenta:',
        x: 310,
        y: 5		
	});

	NS.lbFirmasA = new Ext.form.Label({
		text: 'Firmas tipo A',
        x: 90,
        y: 10	
	});
	
	NS.lbFirmasB = new Ext.form.Label({
		text: 'Firmas tipo B',
        x: 380,
        y: 10		
	});

	NS.txtNombreA = new Ext.form.TextField({
		id: PF+'txtNombreA',
        name: PF+'txtNombreA',
        x: 10,
        y: 90,
        width: 250,
        disabled:true
    });

    NS.txtNombreB = new Ext.form.TextField({
		id: PF+'txtNombreB',
        name: PF+'txtNombreB',
        x: 310,
        y: 90,
        width: 250,
        disabled:true
    });
    
    /************************************************************
     * Funciones 
     */
  
    NS.ejecutar=function(){
    	if(NS.txtNombreA.getValue()!=''|| NS.txtNombreA.getValue()!=''){
    		AsignacionFirmasAction.updateFirmanteDeterminado(NS.txtIdBanco.getValue(),NS.cmbCuentas.getValue()+'',NS.idPersonaA,NS.idPersonaB, function(resultado, e) {
    			if (resultado != '' && resultado != null && resultado != undefined ) {
    				Ext.Msg.alert('SET', resultado);	
    			}
    		});
    	}else{
    		Ext.Msg.alert('SET', "Debe seleccionar una persona");
    	}
    	
	} 

	NS.panelBuscar = new Ext.form.FieldSet ({
		title: '',
        x:0,
        y: 0,
        width: 610,
        height: 50,
        layout: 'absolute',
		items: [
		    NS.txtIdBanco ,
			NS.cmbBancos ,
			NS.cmbCuentas,
			NS.lbBanco,
			NS.lbCuenta,
			
		]
	});
	
	NS.panelFirmantes = new Ext.form.FieldSet ({
		title: '',
        id: PF+'panelGrid',
        name: PF+'panelGrid',
        x: 0,
        y: 70,
        width: 610,
        height: 180,
        layout: 'absolute',
        items: [
            NS.cmbFirmante1,
			NS.cmbFirmante2,
			NS.txtNombreA,
		    NS.txtNombreB,
		    NS.lbFirmasA,
			NS.lbFirmasB 
         ]
	});
	
	NS.panelComponentes = new Ext.form.FieldSet ({
		title: '',
	    x: 0,
	    y: 0,
	    width: 640,
	    height: 320,
	    layout: 'absolute',
	    buttonAlign: 'center',
	    buttons:[
	      { text:'Ejecutar',handler:function(){ NS.ejecutar(); } },
	      ],
		items: [
		 	NS.panelBuscar,
		 	NS.panelFirmantes
		]
	});
	//panel principal

	NS.contenedorMovimientos =new Ext.FormPanel({
    title: 'Asignacion de firmas',
    width: 1200,
    height: 600,
    padding: 10,
    frame: true,
    autoScroll: true,
    layout: 'absolute',
    renderTo: NS.tabContId,
    items : [
			NS.panelComponentes,
   		 ]   
    });

 	NS.contenedorMovimientos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
});