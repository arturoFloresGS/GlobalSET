Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.Inversiones.Mantenimiento');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.banco = 0;
	NS.sIsr = 'S';
	NS.INGRESO = "I";
	NS.EGRESO = "E";
	
	//Objeto para agregar los checks al grid
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true,
		singleSelect: true					
	});
	
	NS.habilitarDeshabilitar = function(valor){
		Ext.getCmp(PF + 'frameNuevoModificar').setDisabled(valor);
		Ext.getCmp(PF + 'btnAceptar').setDisabled(valor);
		Ext.getCmp(PF + 'btnEliminar').setDisabled(valor);
		Ext.getCmp(PF + 'btnCancelar').setDisabled(valor);
		
		
		Ext.getCmp(PF+'cmbDivisa').setDisabled(valor);
		Ext.getCmp(PF+'cmbGrupo').setDisabled(valor);
		Ext.getCmp(PF+'cmbRubro').setDisabled(valor);
		Ext.getCmp(PF+'cmbGrupoI').setDisabled(valor);
		Ext.getCmp(PF+'cmbRubroI').setDisabled(valor);
		
		Ext.getCmp(PF+'cmbGrupoInt').setDisabled(valor);
		Ext.getCmp(PF+'cmbRubroInt').setDisabled(valor);
		Ext.getCmp(PF+'cmbGrupoISR').setDisabled(valor);
		Ext.getCmp(PF+'cmbRubroISR').setDisabled(valor);
		
	};
	
	//funcion para limpiar componentes del formulario
	NS.limpiar = function(){
		NS.bNuevo = false;
		NS.bModificar = false;
		NS.gridConsultarValores.store.removeAll();
		NS.gridConsultarValores.getView().refresh();	
	};
	
	
	NS.addToGridValores = function(){
		var obtieneRegistros;
		var datosStoreValores = NS.storeConsultarValores.data.items;
		var datosGridValores = NS.gridConsultarValores.getStore().recordType;
      	var indice = 0;	
		if(NS.bNuevo && !NS.bModificar)
		{
			obtieneRegistros = NS.gridConsultarValores.store.data.items;
			for(var inc=0; inc < datosStoreValores.length;inc=inc+1)
   			{
   				if(datosStoreValores[inc].get('idValor') === NS.sIdValor)
   				{
   					indice=inc;
   					NS.storeConsultarValores.remove(datosStoreValores[indice]);
   				}
   				else if(datosStoreValores[inc].get('idValor') !== NS.sIdValor)
   				{
   					NS.storeConsultarValores.remove(datosStoreValores[indice]);
   				}
   			}
		}
		else if(NS.bModificar && !NS.bNuevo)
		{
			obtieneRegistros = NS.gridConsultarValores.getSelectionModel().getSelections();
			for(var inc = 0; inc < datosStoreValores.length; inc = inc + 1)
   			{
   				if(datosStoreValores[inc].get('idValor') === obtieneRegistros[0].get('idValor'))
   				{
   				    indice = inc;
   				    if(NS.iEntra > 0)
   				    {
						NS.storeConsultarValores.remove(datosStoreValores[indice]);
						break;
   				    }
   				    NS.iEntra = 1;	
   					NS.sIdValor = obtieneRegistros[0].get('idValor');
					NS.sIsr = obtieneRegistros[0].get('isr');
					NS.sDescripcion = obtieneRegistros[0].get('descripcion');
					NS.sIdDivisa = obtieneRegistros[0].get('idDivisa');
   					NS.storeConsultarValores.remove(datosStoreValores[indice]);
   				}
   			}
		}
		//Objeto para agregar datos al gridConsultarContratos
		var datos = new datosGridValores({
			idValor: NS.sIdValor,
			isr: NS.sIsr,
			descripcion: NS.sDescripcion,
			idDivisa: NS.sIdDivisa
		});
		NS.gridConsultarValores.stopEditing();
		NS.storeConsultarValores.insert(indice, datos);
		NS.sm.selectRow(indice);
		NS.gridConsultarValores.getView().refresh();
	};
	//store del combo cmbDivisa
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{condicion : ''},
		root: '',
	    paramOrder:['condicion'],
		directFn: MantenimientoValoresAction.consultarDivisa, 
		idProperty: 'idDiv', 
		fields: [
		     {name: 'idDiv'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
				if(records.length ==null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen divisas');
					return;
				}
			}
		}
	});
	//Llamo al store Divisa
	NS.storeDivisa.load();
	//combo cmbDivisa
	NS.cmbDivisa = new Ext.form.ComboBox({
		store : NS.storeConsultarValores,
		store: NS.storeDivisa,
		name: PF+'cmbDivisa',
		id: PF+'cmbDivisa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 300,
        y: 15,
        width: 140,
		valueField:'idDiv',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo,valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtIdDivisa',NS.cmbDivisa.getId());
				 	//NS.accionarDivisa(combo.getValue());
				}
			}
		}
	});

	NS.storeConsultarValores = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {},
		root: '',
		paramOrder: [''],
		directFn: MantenimientoValoresAction.consultarValores,
		fields: [
			{name: 'idValor'		 },
			{name: 'descripcion'	 },
			{name: 'isr'			 },
			{name: 'idDivisa'		 },
			/*{name: 'idGrupoOrden'    },
			{name: 'idRubroOrden'    },
			{name: 'idGrupoRegreso'  },
			{name: 'idRubroRegreso'  },
			{name: 'idGrupoIntereses'},
			{name: 'idRubroIntereses'},
			{name: 'idGrupoISR'		 },
			{name: 'idRubroISR'      }*/
			
			
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeConsultarValores, msg:"Buscando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información  SET','No existen registros en la base de datos');
				}
			}
		}
	});
	
	NS.gridConsultarValores = new Ext.grid.GridPanel({
       store : NS.storeConsultarValores, 
          cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                 NS.sm,
                 { header :'Tipo De Valor'	, width :100,  sortable :true, dataIndex :'idValor'         , hidden: false, align:'center' },
                 { header :'Descripción'  	, width :250,  sortable :true, dataIndex :'descripcion'     , hidden: false, align:'left'   },
                 { header :'ISR'          	, width :50,   sortable :true, dataIndex :'isr'             , hidden: false, align:'center' },
                 { header :'Divisa'       	, width :110,  sortable :true, dataIndex :'idDivisa'        , hidden: false, align:'center' }, 
                 /*{ header :'Grupo Orden'    , width :110,  sortable :true, dataIndex :'idGrupoOrden'    , hidden: false, align:'center' },
                 { header :'Rubro Orden'    , width :110,  sortable :true, dataIndex :'idRubroOrden'    , hidden: false, align:'center' },
                 { header :'Grupo Regreso'  , width :110,  sortable :true, dataIndex :'idGrupoRegreso'  , hidden: false, align:'center' },
                 { header :'Rubro Regreso'  , width :110,  sortable :true, dataIndex :'idRubroRegreso'  , hidden: false, align:'center' },
                 { header :'Grupo Intereses', width :110,  sortable :true, dataIndex :'idGrupoIntereses', hidden: false, align:'center' },
                 { header :'Rubro Intereses', width :110,  sortable :true, dataIndex :'idRubroIntereses', hidden: false, align:'center' },
                 { header :'Grupo ISR'      , width :110,  sortable :true, dataIndex :'idGrupoISR'      , hidden: false, align:'center' },
                 { header :'Rubro ISR'      , width :110,  sortable :true, dataIndex :'idRubroISR'      , hidden: false, align:'center' }*/
            ]
        })
	,
	sm: NS.sm,
    columnLines: true,
    x:0,
    y:0,
    width:960,
    height:160,
    frame:true,
    listeners:{
    	click:{
    		fn:function(e){
      			var regSelec = NS.gridConsultarValores.getSelectionModel().getSelections();
      		 	if(regSelec.length > 0)
      		 	{
      		 		Ext.getCmp(PF + 'btnModificar').setDisabled(false);
      		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(false);
      		 	}
      		 	else
      		 	{
      		 		Ext.getCmp(PF + 'btnModificar').setDisabled(true);
      		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
      		 		Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
      		 	}
    		}
    	}
    }
 });
 //Caja Reg+istros Pagos
 NS.txtDesc = new Ext.form.TextField({
	     xtype: 'uppertextfield',
	     name: PF + 'txtReferencia',
	     id: PF + 'txtReferencia',
	     enabledKeyEvents: true,
         x: 115,
         y: 20,
         width: 120,
         tabIndex: 0
	});
		
	//*******************************************************************************	
	//INIT:						CONTROLES GRUPOS Y RUBROS
	//*******************************************************************************
	
	
	
	//1.- GRUPO_INVERSION 
	//NS.labGrupo
	//NS.txtGrupo
	//NS.cmbGrupo	
 	//NS.labRubro
 	//NS.txtRubro
 	//NS.cmbRubro
	
 	NS.labGrupo = new Ext.form.Label({
 		text: 'Grupo Salida Inversion:',
		x: 0,
		y: 80,
		hidden:true
 	});
 	   
 	NS.txtGrupo = new Ext.form.TextField({
		id: PF + 'txtGrupo',
        name:PF + 'txtGrupo',
		x: 0,
        y: 100,
        width: 70, 
        tabIndex: 0,
        hidden:true,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
 		
 					funLimpiarPorEventoGrupo();
		
					if(caja.getValue()===''){
						NS.cmbGrupo.reset();
						return; 
					}
					
					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupo',NS.cmbGrupo.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de Grupo no valido.');
						return; 
					}					
						
					NS.accionarGrupo(caja.getValue());
        			
        		}
        	}
        }
	});
 	
 	function funLimpiarPorEventoGrupo(){
 		NS.storeRubro.removeAll();
 		NS.txtRubro.reset();
 		NS.cmbRubro.reset();
 	}
 	
 	NS.accionarGrupo = function(valueCombo){		
 		NS.storeRubro.removeAll();
		NS.storeRubro.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupo').getValue());
		NS.storeRubro.load();
		
	};
	
	NS.accionarGrupo2 = function(valueCombo, valueCombo2){		
 		NS.storeRubro.removeAll();
		NS.storeRubro.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupo').getValue());
		NS.storeRubro.load({
			callback:function( records, operation, success ){
				Ext.getCmp( PF + 'cmbRubro'	    ).setValue( valueCombo2 );			
			}
		});
		
	};
	
	
 	
 	
 	NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
    		idTipoMovto:NS.EGRESO
		},
		paramOrder:['idTipoMovto'],
		directFn: ConfirmacionCargoCtaAction.obtenerGruposPorTipoMovto,
		idProperty: 'idGrupo',    
		fields: [
			 {name: 'idGrupo'},
			 {name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupo, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 	
 	NS.storeGrupo.load();
 
 	NS.cmbGrupo = new Ext.form.ComboBox({
		store: NS.storeGrupo,
		id: PF + 'cmbGrupo',
		name: PF + 'cmbGrupo',
		x: 80,
        y: 100,
        width: 220,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Seleccione un Grupo Egreso',   
		triggerAction: 'all',
		value: '',
		visible: false,	
		hidden:true,
		disabled:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				BFwrk.Util.updateComboToTextField(PF+'txtGrupo',NS.cmbGrupo.getId());
	 				funLimpiarPorEventoGrupo();
	 				NS.accionarGrupo(combo.getValue());
	 				
				}
			}
		}
	});

	//1.- RUBRO_INVERSION
	NS.labRubro = new Ext.form.Label({
 		text: 'Rubro Salida Inversion:',
		x: 350,
		y: 80,
		hidden:true
 	});
 	
 	NS.txtRubro = new Ext.form.TextField({
		id: PF + 'txtRubro',
        name:PF + 'txtRubro',
		x: 350,
        y: 100,
        width: 70,
		hidden:true,
        tabIndex: 0,
        listeners : {
 			change : {
    			fn: function(caja, valor){     
 		
 					if(caja.getValue()===''){
 						NS.cmbRubro.reset();
 						return; 
 					}
		
 					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtRubro',NS.cmbRubro.getId()) === undefined ){
 						caja.reset();
 						Ext.Msg.alert('SET','Id de Rubro no valido.');
 						return; 
 					}					
			
    			}
    		}
 		}
        
	});
 	 	
 	
 	NS.storeRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
		},
		paramOrder:['idGrupo'],
		directFn: ConfirmacionCargoCtaAction.obtenerRubroVta, 
		idProperty: 'idRubro', 
		fields: [
			 {name: 'idRubro'},
			 {name: 'descRubro'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubro, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 
 	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeRubro,
		id: PF + 'cmbRubro',
		name: PF + 'cmbRubro',
		x: 425,
        y: 100,
        width: 215,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Seleccione un Rubro Egreso',
		triggerAction: 'all',
		value: '',
		visible: false,
		hidden:true,
		disabled:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
 		
 					BFwrk.Util.updateComboToTextField(PF+'txtRubro',NS.cmbRubro.getId());
 									
				}
			}
		} 
	});
 	
 	
 	
 	//2.- GRUPO_REGRESO_INVERSION
 	//NS.labGrupoI
 	//NS.txtGrupoI
 	//NS.cmbGrupoI
 	//NS.labRubroI
 	//NS.txtRubroI
 	//NS.cmbRubroI
 	
 	NS.labGrupoI = new Ext.form.Label({
 		text: 'Grupo Regreso Inversion:',
		x: 0,
		y: 130,
		hidden:true
 	});
 	   
 	NS.txtGrupoI = new Ext.form.TextField({
		id: PF + 'txtGrupoI',
        name:PF + 'txtGrupoI',
		hidden:true,
		x: 0,
        y: 150,
        width: 70, 
        tabIndex: 0,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
 		
 					funLimpiarPorEventoGrupoI();
		
					if(caja.getValue()===''){
						NS.cmbGrupoI.reset();
						return; 
					}
					
					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupoI',NS.cmbGrupoI.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de Grupo no valido.');
						return; 
					}					
						
					NS.accionarGrupoI(caja.getValue());
        			
        		}
        	}
        }
	});
 	
 	function funLimpiarPorEventoGrupoI(){
 		NS.storeRubroI.removeAll();
 		NS.txtRubroI.reset();
 		NS.cmbRubroI.reset();
 	}
 	
 	NS.accionarGrupoI = function(valueCombo){		
 		NS.storeRubroI.removeAll();
		NS.storeRubroI.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupoI').getValue());
		NS.storeRubroI.load();
		
	};
	
	NS.accionarGrupoI2 = function(valueCombo, valueCombo2){		
 		NS.storeRubroI.removeAll();
		NS.storeRubroI.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupoI').getValue());
		NS.storeRubroI.load({
			callback:function(records, operation, success){
				Ext.getCmp( PF + 'cmbRubroI'	).setValue( valueCombo2 );			
			}
		});
		
	};
	
	
 	
 	
 	NS.storeGrupoI = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
    		idTipoMovto:NS.INGRESO
		},
		paramOrder:['idTipoMovto'],
		directFn: ConfirmacionCargoCtaAction.obtenerGruposPorTipoMovto, 
		idProperty: 'idGrupo',    
		fields: [
			 {name: 'idGrupo'},
			 {name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoI, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 	
 	NS.storeGrupoI.load();
 
 	NS.cmbGrupoI = new Ext.form.ComboBox({
		store: NS.storeGrupoI,
		id: PF + 'cmbGrupoI',
		name: PF + 'cmbGrupoI',
		x: 80,
        y: 150,
        width: 220,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Seleccione un Grupo Ingreso',   
		triggerAction: 'all',
		value: '',
		visible: false,
		hidden:true,
		disabled:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				BFwrk.Util.updateComboToTextField(PF+'txtGrupoI',NS.cmbGrupoI.getId());
	 				funLimpiarPorEventoGrupoI();
	 				NS.accionarGrupoI(combo.getValue());
	 				
				}
			}
		}
	});
 
 	
	
 	//2.- RUBRO_REGRESO_INVERSION
 	
 	
	NS.labRubroI = new Ext.form.Label({
 		text: 'Rubro Regreso Inversion:',
		x: 350,
		y: 130,
		hidden:true
 	});
 	
 	NS.txtRubroI = new Ext.form.TextField({
		id: PF + 'txtRubroI',
        name:PF + 'txtRubroI',
		x: 350,
        y: 150,
        width: 70, 
        tabIndex: 0,
		hidden:true,
        listeners : {
 			change : {
    			fn: function(caja, valor){     
 		
					if(caja.getValue()===''){
						NS.cmbRubroI.reset();
						return; 
					}
					
					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtRubroI',NS.cmbRubroI.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de Rubro no valido.');
						return; 
					}	
 					
    			}
    		}
 		}
        
	});
 	
 	
 	NS.storeRubroI = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
		},
		paramOrder:['idGrupo'],
		directFn: ConfirmacionCargoCtaAction.obtenerRubroVta, 
		idProperty: 'idRubro', 
		fields: [
			 {name: 'idRubro'},
			 {name: 'descRubro'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubroI, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 
 	NS.cmbRubroI = new Ext.form.ComboBox({
		store: NS.storeRubroI,
		id: PF + 'cmbRubroI',
		name: PF + 'cmbRubroI',
		x: 425,
        y: 150,
        width: 215,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Seleccione un Rubro Ingreso',
		triggerAction: 'all',
		value: '',
		visible: false,
		hidden:true,
		disabled:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
 					BFwrk.Util.updateComboToTextField(PF+'txtRubroI',NS.cmbRubroI.getId());
 									
				}
			}
		} 
	});
 	
 	
 	//3.- GRUPO_INGRESO_INTERES
 	//NS.labGrupoInt
 	//NS.txtGrupoInt
 	//NS.cmbGrupoInt
 	//NS.labRubroInt
 	//NS.txtRubroInt
 	//NS.cmbRubroInt
 	
 	NS.labGrupoInt = new Ext.form.Label({
 		text: 'Grupo Intereses:',
		x: 0,
		y: 180,
		hidden:true
 	});
 	   
 	NS.txtGrupoInt = new Ext.form.TextField({
		id: PF + 'txtGrupoInt',
        name:PF + 'txtGrupoInt',
		x: 0,
        y: 200,
        width: 70, 
        tabIndex: 0,
		hidden:true,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
 		
 					funLimpiarPorEventoGrupoInt();
		
					if(caja.getValue()===''){
						NS.cmbGrupoInt.reset();
						return; 
					}
					
					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupoInt',NS.cmbGrupoInt.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de Grupo no valido.');
						return; 
					}					
						
					NS.accionarGrupoInt(caja.getValue());
        			
        		}
        	}
        }
	});
 	
 	function funLimpiarPorEventoGrupoInt(){
 		NS.storeRubroInt.removeAll();
 		NS.txtRubroInt.reset();
 		NS.cmbRubroInt.reset();
 	}
 	
 	NS.accionarGrupoInt = function(valueCombo){		
 		NS.storeRubroInt.removeAll();
		NS.storeRubroInt.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupoInt').getValue());
		NS.storeRubroInt.load();
		
	};
	
	NS.accionarGrupoInt2 = function(valueCombo, valueCombo2){		
 		NS.storeRubroInt.removeAll();
		NS.storeRubroInt.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupoInt').getValue());
		NS.storeRubroInt.load({
			callback:function(records, operation, success) {
				Ext.getCmp( PF + 'cmbRubroInt'	).setValue( valueCombo2 );				
			}
		});
		
	};
	
	
 	
 	NS.storeGrupoInt = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
    		idTipoMovto:NS.INGRESO
		},
		paramOrder:['idTipoMovto'],
		directFn: ConfirmacionCargoCtaAction.obtenerGruposPorTipoMovto, 
		idProperty: 'idGrupo',    
		fields: [
			 {name: 'idGrupo'},
			 {name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoInt, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 	
 	NS.storeGrupoInt.load();
 
 	NS.cmbGrupoInt = new Ext.form.ComboBox({
		store: NS.storeGrupoInt,
		id: PF + 'cmbGrupoInt',
		name: PF + 'cmbGrupoInt',
		x: 80,
        y: 200,
        width: 220,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Seleccione un Grupo Ingreso',   
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled:true,
		hidden:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				BFwrk.Util.updateComboToTextField(PF+'txtGrupoInt',NS.cmbGrupoInt.getId());
	 				funLimpiarPorEventoGrupoInt();
	 				NS.accionarGrupoInt(combo.getValue());
	 				
				}
			}
		}
	});
 
 	
	
 	//3.- RUBRO_REGRESO_INVERSION
 	
 	
	NS.labRubroInt = new Ext.form.Label({
 		text: 'Rubro Intereses:',
		x: 350,
		y: 180,
		hidden:true,
 	});
 	
 	NS.txtRubroInt = new Ext.form.TextField({
		id: PF + 'txtRubroInt',
        name:PF + 'txtRubroInt',
		x: 350,
        y: 200,
        width: 70, 
		hidden:true,
        tabIndex: 0,
        listeners : {
 			change : {
    			fn: function(caja, valor){     
 		
					if(caja.getValue()===''){
						NS.cmbRubroInt.reset();
						return; 
					}
					
					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtRubroInt',NS.cmbRubroInt.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de Rubro no valido.');
						return; 
					}	
 					
    			}
    		}
 		}
        
	});
 	
 	
 	NS.storeRubroInt = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
		},
		paramOrder:['idGrupo'],
		directFn: ConfirmacionCargoCtaAction.obtenerRubroVta, 
		idProperty: 'idRubro', 
		fields: [
			 {name: 'idRubro'},
			 {name: 'descRubro'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubroInt, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 
 	NS.cmbRubroInt = new Ext.form.ComboBox({
		store: NS.storeRubroInt,
		id: PF + 'cmbRubroInt',
		name: PF + 'cmbRubroInt',
		x: 425,
        y: 200,
        width: 215,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Seleccione un Rubro Ingreso',
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled:true,
		hidden:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
 					BFwrk.Util.updateComboToTextField(PF+'txtRubroInt',NS.cmbRubroInt.getId()); 									
				}
			}
		} 
	});
 	
 	//3.- GRUPO_ISR
 	//NS.labGrupoISR
 	//NS.txtGrupoISR
 	//NS.cmbGrupoISR
 	//NS.labRubroISR
 	//NS.txtRubroISR
 	//NS.cmbRubroISR
 	
 	NS.labGrupoISR = new Ext.form.Label({
 		text: 'Grupo ISR:',
		x: 0,
		y: 230,
		hidden:true,
 	});
 	   
 	NS.txtGrupoISR = new Ext.form.TextField({
		id: PF + 'txtGrupoISR',
        name:PF + 'txtGrupoISR',
		x: 0,
        y: 250,
        width: 70, 
        tabIndex: 0,
		hidden:true,
        listeners : {
        	change : {
        		fn: function(caja, valor){     
 		
 					funLimpiarPorEventoGrupoISR();
		
					if(caja.getValue()===''){
						NS.cmbGrupoISR.reset();
						return; 
					}
					
					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtGrupoISR',NS.cmbGrupoISR.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de Grupo no valido.');
						return; 
					}					
						
					NS.accionarGrupoISR(caja.getValue());
        			
        		}
        	}
        }
	});
 	
 	function funLimpiarPorEventoGrupoISR(){
 		NS.storeRubroISR.removeAll();
 		NS.txtRubroISR.reset();
 		NS.cmbRubroISR.reset();
 	}
 	
 	NS.accionarGrupoISR2 = function(valueCombo, valueCombo2){		
 		NS.storeRubroISR.removeAll();
		NS.storeRubroISR.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupoISR').getValue());
		NS.storeRubroISR.load({			
			callback:function(records, operation, success) {
				Ext.getCmp( PF + 'cmbRubroISR'	).setValue( valueCombo2 );
				
			}
		});	
		
		 
	};
	
	NS.accionarGrupoISR = function(valueCombo){		
 		NS.storeRubroISR.removeAll();
		NS.storeRubroISR.baseParams.idGrupo = parseInt(Ext.getCmp(PF + 'txtGrupoISR').getValue());
		NS.storeRubroISR.load();
		
	};
 	
 	NS.storeGrupoISR = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
    		idTipoMovto:NS.EGRESO
		},
		paramOrder:['idTipoMovto'],
		directFn: ConfirmacionCargoCtaAction.obtenerGruposPorTipoMovto, 
		idProperty: 'idGrupo',    
		fields: [
			 {name: 'idGrupo'},
			 {name: 'descGrupo'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoISR, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 	
 	NS.storeGrupoISR.load();
 
 	NS.cmbGrupoISR = new Ext.form.ComboBox({
		store: NS.storeGrupoISR,
		id: PF + 'cmbGrupoISR',
		name: PF + 'cmbGrupoISR',
		x: 80,
        y: 250,
        width: 220,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idGrupo',
		displayField: 'descGrupo',
		autocomplete: true,
		emptyText: 'Seleccione un Grupo Egreso',   
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled:true,
		hidden:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
	 				BFwrk.Util.updateComboToTextField(PF+'txtGrupoISR',NS.cmbGrupoISR.getId());
	 				funLimpiarPorEventoGrupoISR();
	 				NS.accionarGrupoISR(combo.getValue());
	 				
				}
			}
		}
	});
 
 	
	
 	//3.- RUBRO_ISR
 	
 	
	NS.labRubroISR = new Ext.form.Label({
 		text: 'Rubro ISR:',
		x: 350,
		y: 230,
		hidden:true,
 	});
 	
 	NS.txtRubroISR = new Ext.form.TextField({
		id: PF + 'txtRubroISR',
        name:PF + 'txtRubroISR',
		x: 350,
        y: 250,
        width: 70, 
        tabIndex: 0,
		hidden:true,
        listeners : {
 			change : {
    			fn: function(caja, valor){     
 		
					if(caja.getValue()===''){
						NS.cmbRubroISR.reset();
						return; 
					}
					
					if( BFwrk.Util.updateTextFieldToCombo(PF+'txtRubroISR',NS.cmbRubroISR.getId()) === undefined ){
						caja.reset();
						Ext.Msg.alert('SET','Id de Rubro no valido.');
						return; 
					}	
 					
    			}
    		}
 		}
        
	});
 	
 	
 	NS.storeRubroISR = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {
		},
		paramOrder:['idGrupo'],
		directFn: ConfirmacionCargoCtaAction.obtenerRubroVta, 
		idProperty: 'idRubro', 
		fields: [
			 {name: 'idRubro'},
			 {name: 'descRubro'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeRubroISR, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene Grupos asignados');
				}
					   		
			}
		}
	}); 
 	
 	
 
 	NS.cmbRubroISR = new Ext.form.ComboBox({
		store: NS.storeRubroISR,
		id: PF + 'cmbRubroISR',
		name: PF + 'cmbRubroISR',
		x: 425,
        y: 250,
        width: 215,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'idRubro',
		displayField: 'descRubro',
		autocomplete: true,
		emptyText: 'Seleccione un Rubro Egreso',
		triggerAction: 'all',
		value: '',
		visible: false,
		disabled:true,
		hidden:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
 					BFwrk.Util.updateComboToTextField(PF+'txtRubroISR',NS.cmbRubroISR.getId()); 									
				}
			}
		} 
	});
 	
	//*******************************************************************************	
	//END:						CONTROLES GRUPOS Y RUBROS
	//*******************************************************************************

	
	
	
	NS.MantenimientoDeValores = new Ext.form.FormPanel({
    title: 'Mantenimiento de Valores',
    width: 1080,
    height: 800,    
    layout: 'absolute',
    id: PF + 'MantenimientoDeValores',
    name: PF + 'MantenimientoDeValores',
    renderTo: NS.tabContId,
    frame: true,
    autoScroll: true,
         items : [
            {
                xtype: 'fieldset',
                title: '',
                width: 1010,
                height: 605,
                x: 20,
                y: 5,
                border:false,
                margin:10,
                layout: 'absolute',
                id: 'framePrinManContratos',
                items: [              
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 900,
                        y: 0,
                        width: 80,
                        height: 22,
                        id: 'btnBuscar',
                        listeners: {
                        	click:{
                        		fn: function(e){
            			           NS.storeConsultarValores.load();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Valores',
                        x: 0,
                        y: 30,
                        height: 225,
                        width: 985,
                        layout: 'absolute',
                        id: 'frameValores',
                        items: [
                                 NS.gridConsultarValores ,
                            {
                                xtype: 'button',
                                text: 'Modificar',
                                x: 700,
                                y: 165,
                                width: 80,
                                id: PF + 'btnModificar',
                                disabled: true,
                                listeners:{
                                	click:{
                                		fn: function(e){
                                			
                                			NS.bNuevo = false;
                                			NS.bModificar = true;
                                			
	                                		var regSelec = NS.gridConsultarValores.getSelectionModel().getSelections();
	                                		
	                               			if(regSelec.length <= 0)
	                               				return;
	                               			
	                               			Ext.getCmp(PF + 'txtIdValor').setValue(regSelec[0].get('idValor'));	                               			
	                               			Ext.getCmp(PF + 'txtDescripcion').setValue(regSelec[0].get('descripcion'));
	                               			
	                               			NS.cmbDivisa.setValue( NS.storeDivisa.getById(regSelec[0].get('idDivisa')).get('descripcion'));
	                               			Ext.getCmp(PF + 'txtIdDivisa').setValue(regSelec[0].get('idDivisa'));
	                               			
	                               			Ext.getCmp(PF + 'btnAceptar').setDisabled(false);
	                               			NS.gridConsultarValores.getView().refresh();
	                               		    NS.habilitarDeshabilitar(false); 
   		                                   	if(Ext.getCmp(PF + 'txtIdDivisa').getValue() !== '')
                                    		if(regSelec[0].get('isr') == 'S')
	                               			{
	                               				Ext.getCmp(PF + 'optS').setValue(true);
	                               				Ext.getCmp(PF + 'optN').setValue(false);
	                               			}
	                               			else if(regSelec[0].get('isr') == 'N'){
	                               				Ext.getCmp(PF + 'optN').setValue(true);
	                               				Ext.getCmp(PF + 'optS').setValue(false);
	                               				NS.consultarValores
	                               			}
   		                                    		                                    
   		                                    Ext.getCmp( PF + 'txtGrupo'	    ).setValue( regSelec[0].get('idGrupoOrden'		));
   		                                    Ext.getCmp( PF + 'cmbGrupo'	    ).setValue( regSelec[0].get('idGrupoOrden'		));   		                                    
											Ext.getCmp( PF + 'txtRubro'	    ).setValue( regSelec[0].get('idRubroOrden'		));
											NS.accionarGrupo2( regSelec[0].get('idGrupoOrden'), regSelec[0].get('idRubroOrden' )) ;
																						   										    
   										    Ext.getCmp( PF + 'txtGrupoI'	).setValue( regSelec[0].get('idGrupoRegreso'	));
   										    Ext.getCmp( PF + 'cmbGrupoI'	).setValue( regSelec[0].get('idGrupoRegreso'	));   										    
   										    Ext.getCmp( PF + 'txtRubroI'	).setValue( regSelec[0].get('idRubroRegreso'	));
   										    NS.accionarGrupoI2( regSelec[0].get('idGrupoRegreso'), regSelec[0].get('idRubroRegreso' )) ;
   										        
   										    Ext.getCmp( PF + 'txtGrupoInt'	).setValue( regSelec[0].get('idGrupoIntereses'	));
   										    Ext.getCmp( PF + 'cmbGrupoInt'	).setValue( regSelec[0].get('idGrupoIntereses'	));
   										    Ext.getCmp( PF + 'txtRubroInt'  ).setValue( regSelec[0].get('idRubroIntereses'	));
   										    NS.accionarGrupoInt2( regSelec[0].get('idGrupoIntereses'), regSelec[0].get('idRubroIntereses' )) ;
   										    
   										    Ext.getCmp( PF + 'txtGrupoISR'	).setValue( regSelec[0].get('idGrupoISR'		));
   										    Ext.getCmp( PF + 'cmbGrupoISR'	).setValue( regSelec[0].get('idGrupoISR'		));   										       										    
   										    Ext.getCmp( PF + 'txtRubroISR'	).setValue( regSelec[0].get('idRubroISR'		));   										    
   										    NS.accionarGrupoISR2( regSelec[0].get('idGrupoISR'), regSelec[0].get('idRubroISR' )) ;
   					                	
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'button',
                                text: 'Crear Nuevo',
                                x: 880,
                                y: 165,
                                width: 80,
                                id: 'btnNuevo',
                                listeners:{
                                	click:{
                                		fn: function(e){
                                			NS.habilitarDeshabilitar(false);
                                			NS.bNuevo = true;
                                			NS.bModificar = false;
                                		}
                                	}
                                }
                            },                           
                            {
                                xtype: 'button',
                                text: 'Eliminar',
                                x: 790,
                                y: 165,
                                width: 80,
                                disabled: true,
                                id: PF + 'btnEliminar',
                                name: PF + 'btnEliminar',
                                listeners:{
                                	click:{
                                		fn: function(e){
                                			var regSelec = NS.gridConsultarValores.getSelectionModel().getSelections();
                                			if(regSelec.length <= 0 || regSelec.length > 1) return;	
                                			Ext.Msg.confirm('SET','¿Esta seguro de eliminar este valor?', function(btn) {  
            		           		     		if(btn == 'yes') {
            		           		     	      	MantenimientoValoresAction.consultarValores; 
            		           		     	      	MantenimientoValoresAction.eliminarValores(regSelec[0].get('idValor'), function(result, e){
            		           		     	      		if(result !== null && result !== undefined && result !== '')
				                       					{
				                       						Ext.Msg.alert("Información SET", '' + result.msgUsuario);
				                       						NS.gridConsultarValores.store.removeAll();
															NS.gridConsultarValores.getView().refresh();
															NS.storeConsultarValores.load();
				                       					}
				                       				});
            		           		     		}
                                			});
                                		}
                                	}
                            	}
                            }
                        ]
                    }
                ]          
            },
            {
                xtype: 'fieldset',
                title: 'Registro',
                x: 30,
                y: 285,
                width: 980,
                height: 310,
                layout: 'absolute',
                id: PF + 'frameNuevoModificar',
                disabled:true,
                maskDisabled: false,
                items: [
                    {
                    	xtype: 'uppertextfield',                        
                        x: 10,
                        y: 15,
                        width: 80,
                        name: PF + 'txtIdValor',
                        id: PF + 'txtIdValor',
                        enabledKeyEvents: false,
                        listeners:{
	                        change:{
	                        	fn:function(caja, valor){
	                        		NS.sIdValor = caja.getValue();
	                        	}
	                        }
                        }
                    },              
                    {
                        xtype: 'uppertextfield',
                  	    name: PF + 'txtDescripcion',
                  	    id: PF + 'txtDescripcion',
                  	    enabledKeyEvents: true,
                        x: 115,
                        y: 15,
                        width: 120,
                        tabIndex: 0,
                        listeners:{
	                        change:{
	                        	fn:function(caja, valor){
	                        		NS.sDescripcion= caja.getValue();
	                        	}
	                        }
                        }
                    },
                    {
                        xtype: 'uppertextfield',
                        x: 255,
                        y: 15,
                        width: 40,
                        name: PF + 'txtIdDivisa',
                        id: PF + 'txtIdDivisa',
                        listeners:{
                        	change:{
                        		fn: function(caja, valor){
                    	
                    	
                        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                        	            var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisa',NS.cmbDivisa.getId());
               							if(valueCombo != null && valueCombo != undefined && valueCombo != '')
               								NS.accionarDivisa(valueCombo);	  
               							else
               								Ext.getCmp(PF+'txtIdDivisa').reset();
                        			}else {
                        				NS.cmbDivisa.reset();
                        			}
                        			
                        		
                        			
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Aceptar',
                        x: 690,
                        y: 20,
                        width: 80,
                        id: PF + 'btnAceptar',
                        name: PF + 'btnAceptar',
                        disabled: true,
                        listeners:{
	                        click:{
	                        	fn: function(e){
	                        		var matrizGridValores = new Array();
                           			var arrValores = {};
                           			
                   				    arrValores.sIdValor     	= Ext.getCmp( PF + 'txtIdValor'     ).getValue();
                   				    arrValores.sDescripcion 	= Ext.getCmp( PF + 'txtDescripcion' ).getValue();
                   				    arrValores.sIdDivisa    	= Ext.getCmp( PF + 'txtIdDivisa'    ).getValue();
                   				    arrValores.sIsr         	= NS.sIsr;
									/*arrValores.idGrupoOrden     = Ext.getCmp( PF + 'txtGrupo'	    ).getValue();
									arrValores.idRubroOrden     = Ext.getCmp( PF + 'txtRubro'	    ).getValue();
									arrValores.idGrupoRegreso   = Ext.getCmp( PF + 'txtGrupoI'	    ).getValue();
									arrValores.idRubroRegreso   = Ext.getCmp( PF + 'txtRubroI'	    ).getValue();
									arrValores.idGrupoIntereses = Ext.getCmp( PF + 'txtGrupoInt'	).getValue();
									arrValores.idRubroIntereses = Ext.getCmp( PF + 'txtRubroInt'    ).getValue();
									arrValores.idGrupoISR       = Ext.getCmp( PF + 'txtGrupoISR'	).getValue();
									arrValores.idRubroISR       = Ext.getCmp( PF + 'txtRubroISR'	).getValue();
									*/
                   				    matrizGridValores[0] = arrValores;								    
		                       	    
                   				    var jsonStringValores = Ext.util.JSON.encode(matrizGridValores);
		                       	    
		                       	    MantenimientoValoresAction.insertarModificarValores(NS.bNuevo, NS.bModificar, jsonStringValores, function(result, e){
	                   				
		                       	    	if(result !== null && result !== undefined && result !== '')
	                   					{         						
	                   						Ext.Msg.alert("Información SET", '' + result.msgUsuario);
	                   						
	                   						if(result.terminado == '1'){
	                   						    NS.habilitarDeshabilitar(true);
	                   						    NS.MantenimientoDeValores.getForm().reset();
	                   						    
	                   						}
	                   						NS.storeConsultarValores.load();
	                   					}
		                       	    	
		                       		});  
	                             
	                        	}
	                        }
                        }
                    },        
                    {
                        xtype: 'label',
                        text: 'Tipo Valor:',
                        x: 10,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Descripción:',
                        x: 115,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'Divisa:',
                        x: 255,
                        y: 0
                   },   
                    {
                        xtype: 'button',
                        text: 'Cancelar',
                        x: 780,
                        y: 20,
                        width: 80,
                        height: 22,
                        id: PF + 'btnCancelar',
                        name: PF + 'btnCancelar',
                        disabled: true,
                        listeners:{
                        	click:{
                        		fn:function(e){
                        			
            			        	Ext.getCmp(PF+'txtIdDivisa').reset();
                        			Ext.getCmp(PF+'txtDescripcion').reset();
                        			Ext.getCmp(PF+'txtIdValor').reset();
                        			Ext.getCmp(PF+'cmbDivisa').reset();
            			    		Ext.getCmp(PF + 'btnModificar').setDisabled(true);
            			    		
            			    		Ext.getCmp(PF+'cmbDivisa').reset();
            			    		Ext.getCmp(PF+'txtGrupo').reset();
            			    		Ext.getCmp(PF+'cmbGrupo').reset();
            			    		Ext.getCmp(PF+'txtRubro').reset();
            			    		Ext.getCmp(PF+'cmbRubro').reset();
            			    		Ext.getCmp(PF+'txtGrupoI').reset();
            			    		Ext.getCmp(PF+'cmbGrupoI').reset();
            			    		Ext.getCmp(PF+'txtRubroI').reset();
            			    		Ext.getCmp(PF+'cmbRubroI').reset();
            			    		
            			    		Ext.getCmp(PF+'txtGrupoInt').reset();
            			    		Ext.getCmp(PF+'cmbGrupoInt').reset();
            			    		Ext.getCmp(PF+'txtRubroInt').reset();
            			    		Ext.getCmp(PF+'cmbRubroInt').reset();
            			    		Ext.getCmp(PF+'txtGrupoISR').reset();     			    		
            			    		Ext.getCmp(PF+'cmbGrupoISR').reset();
            			    		Ext.getCmp(PF+'txtRubroISR').reset();
            			    		Ext.getCmp(PF+'cmbRubroISR').reset();
            			    		
            			    		NS.habilitarDeshabilitar(true);
            			    		NS.limpiar();


                        			
                        			
                        		}
                        	}
                        }
                        
                    },
                    {
                        xtype: 'button',
                        text: 'Limpiar',
                        x: 870,
                        y: 20,
                        width: 80,
                        height: 22,
                        id: PF + 'btnLimpiar',
                        name: PF + 'btnLimpiar',
                        listeners:{
                        	click:{
                        		fn:function(e){
            			        	Ext.getCmp(PF+'txtIdDivisa').reset();
                        			Ext.getCmp(PF+'txtDescripcion').reset();
                        			Ext.getCmp(PF+'txtIdValor').reset();
                        			Ext.getCmp(PF+'cmbDivisa').reset();
            			    		Ext.getCmp(PF + 'btnModificar').setDisabled(true);
            			    		
            			    		Ext.getCmp(PF+'cmbDivisa').reset();
            			    		Ext.getCmp(PF+'txtGrupo').reset();
            			    		Ext.getCmp(PF+'cmbGrupo').reset();
            			    		Ext.getCmp(PF+'txtRubro').reset();
            			    		Ext.getCmp(PF+'cmbRubro').reset();
            			    		Ext.getCmp(PF+'txtGrupoI').reset();
            			    		Ext.getCmp(PF+'cmbGrupoI').reset();
            			    		Ext.getCmp(PF+'txtRubroI').reset();
            			    		Ext.getCmp(PF+'cmbRubroI').reset();
            			    		
            			    		Ext.getCmp(PF+'txtGrupoInt').reset();
            			    		Ext.getCmp(PF+'cmbGrupoInt').reset();
            			    		Ext.getCmp(PF+'txtRubroInt').reset();
            			    		Ext.getCmp(PF+'cmbRubroInt').reset();
            			    		Ext.getCmp(PF+'txtGrupoISR').reset();     			    		
            			    		Ext.getCmp(PF+'cmbGrupoISR').reset();
            			    		Ext.getCmp(PF+'txtRubroISR').reset();
            			    		Ext.getCmp(PF+'cmbRubroISR').reset();
            			    		
            			    		NS.habilitarDeshabilitar(true);
            			    		NS.limpiar();
                        		}
                        	}
                        }  
                    },
                    NS.cmbDivisa,
                    NS.labGrupo,
                	NS.txtGrupo,
                	NS.cmbGrupo,
                 	NS.labRubro,
                 	NS.txtRubro,
                 	NS.cmbRubro,
                 	NS.labGrupoI,
                 	NS.txtGrupoI,
                 	NS.cmbGrupoI,
                 	NS.labRubroI,
                 	NS.txtRubroI,
                 	NS.cmbRubroI,
                 	NS.labGrupoInt,
                 	NS.txtGrupoInt,
                 	NS.cmbGrupoInt,
                 	NS.labRubroInt,
                 	NS.txtRubroInt,
                 	NS.cmbRubroInt,
                 	NS.labGrupoISR,
                 	NS.txtGrupoISR,
                 	NS.cmbGrupoISR,
                 	NS.labRubroISR,
                 	NS.txtRubroISR,
                 	NS.cmbRubroISR,
                    {
                        xtype: 'fieldset',
                        title: 'Aplica ISR',
                        width: 140,
                        height: 60,
                        x: 470,
                        y: 0,
                        layout: 'absolute',
                        id: PF + 'framePlaza',
                        items: [
							{
							    xtype: 'radio',
							    boxLabel: 'Si',
							    x: 10,
							    id: PF + 'optS',
							    name: PF + 'optS',
							    checked: true,
							    listeners:{
									check:{
										fn:function(opt, valor){
											if(valor)
											{
												Ext.getCmp(PF+'optN').setValue(false);
												NS.sIsr = 'S';
											}
							   				else{
							   					
							   				}
										}
									}
								}
							},
							{
							    xtype: 'radio',
							    boxLabel: 'No',
							    x: 70,
							    y: 0,
							    id: PF + 'optN',
							    name: PF + 'optN',
							    checked: false,
							    listeners:{
									check:{
										fn:function(opt, valor){
											if(valor)
											{
												Ext.getCmp(PF+'optS').setValue(false);
												NS.sIsr = 'N';
											}
							   				else{
							   					
							   				}
										}
									}
								}
							}
						]
                    }
                ]
            }
        ]
	});
	 NS.MantenimientoDeValores.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
 