/**
@author: Eric Medina Serrato
@Fecha: 18/12/2015
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Impresion.Mantenimientos.ControlCheques');
	NS.tabContId = apps.SET.tabContainerId;
	//NS.tabContId = 'contenedorcheques';
	var PF = apps.SET.tabID+'.';
	//NS.GI_USUARIO = apps.SET.iUserId;
	NS.tipoOperacion = '';	
	
	//Para evitar el desface de los grids
	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
	//store empresa
	NS.storeEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:[],
		directFn: ControlChequesAction.llenarComboEmpresa,
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//combo Empresa
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando Empresas..."});
	NS.storeEmpresa.load();
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresa
		,name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 60
        ,y: 20
        ,width: 190
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					//linea cambia combo
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresa.getId());
					//NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	
	//store Banco Pago
    NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder:[],
		directFn: ControlChequesAction.llenarComboBanco,
		idProperty: 'id', 
		fields: [
				 {name: 'id'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){

				
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen bancos');
			}
		}
    });

    //var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando Bancos..."});
    NS.storeBanco.load();
    //combo Banco Pago
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco
		,name: PF + 'cmbBanco'
		,id: PF + 'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 60
        ,y: 50
        ,width: 190
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Banco de Pago'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBancoPag.getId());
					
					if(Ext.getCmp(PF + 'txtEmpresa').getValue()!= ''){
						if(combo.getValue() != 0) {
							NS.storeChequera.removeAll();
							NS.cmbChequera.reset();
							NS.storeChequera.baseParams.idBanco = combo.getValue();
							NS.storeChequera.baseParams.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
							//NS.storeChequera.baseParams.idDivisa = NS.sDivisa;
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg:"Cargando Chequeras..."});
							NS.storeChequera.load();
						}
					}else{
						Ext.Msg.alert('SET','Debe seleccionar una empresa.');
						NS.cmbBanco.reset();
					}
					
				}
			}
		}
	});
	
	//store Chequera Pago
    NS.storeChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idBanco: 0, noEmpresa : 0},
		root: '',
		paramOrder:['idBanco', 'noEmpresa'],
		directFn: ControlChequesAction.llenarComboChequera,
		idProperty: 'descripcion', 
		fields: [
				 {name: 'descripcion'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){


				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen chequeras');
			}
		}
    });
    
    //combo Chequera Pagadora
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera
		,name: PF + 'cmbChequera'
		,id: PF + 'cmbChequera'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 70
        ,y: 80
        ,width: 180
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Chequera'
		,triggerAction: 'all'
		,value: ''
	});
	
	/************************************************
	 * 	STORES Y COMBOS PARA EL AREA DE CAPTURA		*
	 ************************************************/
	
	//store empresa
	NS.storeEmpresaC = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:[],
		directFn: ControlChequesAction.llenarComboEmpresa,
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//combo Empresa
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresaC, msg:"Cargando Empresas..."});
	NS.storeEmpresaC.load();
	NS.cmbEmpresaC = new Ext.form.ComboBox({
		store: NS.storeEmpresaC
		,name: PF+'cmbEmpresaC'
		,id: PF+'cmbEmpresaC'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 60
        ,y: 20
        ,width: 190
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					//linea cambia combo
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresaC',NS.cmbEmpresaC.getId());
					//NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	
	//store Banco Pago
    NS.storeBancoC = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder:[],
		directFn: ControlChequesAction.llenarComboBanco,
		idProperty: 'id', 
		fields: [
				 {name: 'id'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoC, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen bancos');
			}
		}
    });

    //var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoC, msg:"Cargando Bancos..."});
    NS.storeBancoC.load();
    //combo Banco Pago 
	NS.cmbBancoC = new Ext.form.ComboBox({
		store: NS.storeBancoC
		,name: PF + 'cmbBancoC'
		,id: PF + 'cmbBancoC'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 60
        ,y: 50
        ,width: 190
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Banco de Pago'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					//BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBancoPag.getId());
					
					if(Ext.getCmp(PF + 'txtEmpresaC').getValue()!= ''){
						if(combo.getValue() != 0) {
							NS.storeChequeraC.removeAll();
							NS.cmbChequeraC.reset();
							NS.storeChequeraC.baseParams.idBanco = combo.getValue();
							NS.storeChequeraC.baseParams.noEmpresa = Ext.getCmp(PF + 'txtEmpresaC').getValue();
							//NS.storeChequera.baseParams.idDivisa = NS.sDivisa;
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraC, msg:"Cargando Chequeras..."});
							NS.storeChequeraC.load();
						}
					}else{
						Ext.Msg.alert('SET','Debe seleccionar una empresa.');
						NS.cmbBancoC.reset();
					}
					
				}
			}
		}
	});
	
	//store Chequera Pago	
    NS.storeChequeraC = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idBanco: 0, noEmpresa : 0},
		root: '',
		paramOrder:['idBanco', 'noEmpresa'],
		directFn: ControlChequesAction.llenarComboChequera,
		idProperty: 'descripcion', 
		fields: [
				 {name: 'descripcion'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){

				
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen chequeras');
			}
		}
    });
    
    //combo Chequera Pagadora
	NS.cmbChequeraC = new Ext.form.ComboBox({
		store: NS.storeChequeraC
		,name: PF + 'cmbChequeraC'
		,id: PF + 'cmbChequeraC'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 70
        ,y: 80
        ,width: 180
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Chequera'
		,triggerAction: 'all'
		,value: ''
	});
	
	/******	GRID CONSULTA	*****/
	
	NS.storeControlCheques = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noEmpresa: 0,
			idBanco: 0,
			idChequera: '',
			folioPapel: false,
			estatusChequeras: false,
			estatusChequerasT: false
		},
		paramOrder:['noEmpresa', 'idBanco', 'idChequera', 'folioPapel','estatusChequeras','estatusChequerasT'],
		root: '',
		directFn: ControlChequesAction.consultarCheques, 
		//idProperty: 'id', 
		fields: [
		     {name: 'idBanco'},
		     {name: 'fechaAlta'},
		     {name: 'folioInvIni'},
		     {name: 'folioInvFin'},
		     {name: 'folioUltImpreso'},
		     {name: 'idChequera'},
		     {name: 'noEmpresa'},
		     {name: 'nomEmpresa'},
		     {name: 'tipoFolio'},
		     {name: 'stock'},
		     {name: 'idControlCheque'},
		     {name: 'estatus'}
			 
		],
		listeners: {
			load: function(s, records){

				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No existen registros con los datos seleccionados.');
				}
			}
		}
	}); 
	
	//solo puede elegir una fila a la vez
 	NS.seleccionControlCheques = new Ext.grid.CheckboxSelectionModel
 	({
 		singleSelect: true
 	});
 	
	//Columnas del grid	    	
 	NS.columnasControlCheques = new Ext.grid.ColumnModel
 	([	
 	  	NS.seleccionControlCheques,
 	  	{	
 			header :'Banco',
 			width :80,
 			sortable :true,
 			dataIndex :'idBanco', 
 		},{
 			header :'Chequera',
 			width :250,
 			sortable :true,
 			dataIndex :'idChequera',
 		},{
 			header :'Folio Inicial',
 			width :80,
 			sortable :true,
 			dataIndex :'folioInvIni',
 		},{
 			header :'Folio Final',
 			width :80,
 			sortable :true,
 			dataIndex :'folioInvFin',
 		},{
 			header :'Folio impreso',
 			width :80,
 			sortable :true,
 			dataIndex :'folioUltImpreso',
 		},{
 			header :'Fecha',
 			width :80,
 			sortable :true,
 			dataIndex :'fechaAlta',
 		},{
 			header :'Empresa',
 			width :80,
 			sortable :true,
 			dataIndex :'nomEmpresa',
 		},{
 			header :'Stock',
 			width :80,
 			sortable :true,
 			dataIndex :'stock',
 		},{
 			header :'Tipo Folio',
 			width :80,
 			sortable :true,
 			dataIndex :'tipoFolio',
 		},{
 			header :'Estatus',
 			width :80,
 			sortable :true,
 			dataIndex :'estatus',
 		}
 		
 	  ]);
 	
	//grid de datos
	 NS.gridControlCheques = new Ext.grid.GridPanel({
       store : NS.storeControlCheques,
       viewConfig: {emptyText: ''},
       x:400,
       y:20,
       cm: NS.columnasControlCheques,
		sm: NS.seleccionControlCheques,
       width:580,
       height:205,
       frame:true,
       listeners: {
       	click: {
       		fn:function(e) {
       			
       			var renSel = NS.gridControlCheques.getSelectionModel().getSelections();
       			
       			if(renSel.length > 0){
       				Ext.getCmp(PF + 'btnModificar').setDisabled(false);
       				Ext.getCmp(PF + 'btnEliminar').setDisabled(false);
       				Ext.getCmp(PF + 'fielCapturaCheque').setDisabled(false);
       				NS.limpiarCaptura();
					NS.cargarDatosAreaCaptura();
					
       			}else{
       				Ext.getCmp(PF + 'btnModificar').setDisabled(true);
       				Ext.getCmp(PF + 'btnEliminar').setDisabled(true);
       			}
       			
       		}
       	}
       }
   });
	 
	 
	 NS.optTipoFolio = new Ext.form.RadioGroup({
		id: PF + 'optTipoFolio',
		name: PF + 'optTipoFolio',
		x: 10,
		y: 180,
		columns: 2, //muestra los radiobuttons en x columnas
		items: [
	          {boxLabel: 'Folio Facsimil', id: PF + 'optFolioPapel', name: 'optTipoFolio', inputValue: 0, checked: true },   
	          {boxLabel: 'Folio cheque', id: PF + 'optFolioCheque', name: 'optTipoFolio', inputValue: 1}
	     ]
	});
	 
	/*****	*****	*****	*****	*****	*****	*****	*****	*****	*****	*****/
	/*****	*****	*****	*****	      FORMULARIO		*****	*****	*****	*****/
	/*****	*****	*****	*****	*****	*****	*****	*****	*****	*****	*****/

	NS.contenedorControlCheques = new Ext.FormPanel({
	    title: 'Control de Cheques',
	    width: 800,
	    height: 450,
	    padding: 10,
	    frame: true,
	    autoScroll: true,
	    layout: 'absolute',
	    renderTo: NS.tabContId, 
	    items: [
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 0,
                width: 1010,
                height: 575,
                layout: 'absolute',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'B&#250;squeda',
                        x: 0,
                        y: 0,

                        height: 215,
                        layout: 'absolute',

                        width: 370,
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa:',
                                x: 10,
                                y: 0
                            },
                            {
                                xtype: 'numberfield',
                                id: PF+'txtEmpresa',
                                name: PF+'txtEmpresa',
                                x: 10,
                                y: 20,
                                width: 40,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			//linea cambia combo
		                        			if(BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId()) == undefined ){
		            							caja.reset();
		            							NS.cmbEmpresa.reset();
		            							Ext.Msg.alert('SET','Id. de Empresa no valido.');
		            							return; 
		            						}else{	
		            							var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
		            						}//NS.accionarCmbDivisa(comboValue);
		                        		}
		                        	}
		                        }
                            },
                            NS.cmbEmpresa,
                            {
                                xtype: 'label',
                                text: 'Banco:',
                                x: 10,
                                y: 50
                            },
                            NS.cmbBanco,
                            {
                                xtype: 'label',
                                text: 'Chequera:',
                                x: 10,
                                y: 80
                            },
                            NS.cmbChequera,
                            {
  							  xtype: 'button',
  							  text: 'Buscar',
  							  x: 170,

  							  y: 155,
  							  width: 80,
  							  height: 22,
  							  id: PF+'btnBuscar',
  							  name: PF+'btnBuscar',
  							  hidden: false,
  							  listeners:{
  								  click:{
  									  fn:function(e){
  										  
  										  NS.buscar();
  									  }
  								  }
  							  }
  						  },{
  	                    	xtype: 'checkbox',
  	       		        	id: PF + 'chkFolioPapel',
  	       		        	name: PF + 'chkFolioPapel',
  	       		        	x: 10,
  	       		        	y: 120,
  	       		        	boxLabel: 'Folio Facsimil'
  	                    },{
  	                    	xtype: 'checkbox',
  	       		        	id: PF + 'chkEstatusChequeras',
  	       		        	name: PF + 'chkFEstatusChequeras',
  	       		        	x: 120,
  	       		        	y: 120,
  	       		        	boxLabel: 'Folios Inactivos'
  	                    },{
  	                    	xtype: 'checkbox',
  	       		        	id: PF + 'chkEstatusChequerasT',
  	       		        	name: PF + 'chkFEstatusChequerasT',
  	       		        	x: 230,
  	       		        	y: 120,
  	       		        	boxLabel: 'Folios Terminados'
  	                    }
                            
                    	]
                    },{
                        xtype: 'fieldset',
                        title: 'Captura',
                        id: PF + 'fielCapturaCheque',
                        x: 0,

                        y: 240,
                        height: 245,
                        layout: 'absolute',
                        width: 350,
                        disabled: true,
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa:',
                                x: 10,
                                y: 0
                            },
                            {	
                                xtype: 'numberfield',
                                id: PF+'txtEmpresaC',
                                name: PF+'txtEmpresaC',
                                x: 10,
                                y: 20,
                                width: 40,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			//linea cambia combo
		                        			if(BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresaC',NS.cmbEmpresaC.getId()) == undefined ){
		            							caja.reset();
		            							NS.cmbEmpresaC.reset();
		            							Ext.Msg.alert('SET','Id de Empresa no valido.');
		            							return; 
		            						}else{	
		            							var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresaC',NS.cmbEmpresaC.getId());
		            						}//NS.accionarCmbDivisa(comboValue);
		                        		}
		                        	}
		                        }
                            },
                            NS.cmbEmpresaC,
                            {
                                xtype: 'label',
                                text: 'Banco:',
                                x: 10,
                                y: 50
                            },
                            NS.cmbBancoC,
                            {
                                xtype: 'label',
                                text: 'Chequera:',
                                x: 10,
                                y: 80
                            },
                            NS.cmbChequeraC,
                            {
                                xtype: 'label',
                                text: 'Folio Inicial:',
                                x: 10,
                                y: 120
                            },{	
                                xtype: 'numberfield',
                                id: PF+'txtFolioIni',
                                name: PF+'txtFolioIni',
                                x: 80,
                                y: 120,
                                width: 70,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			
		                        			if(valor == '' || valor == NaN || valor == null || valor == undefined ){
		                						return;
		                					}
		                        			
		                        			if(Ext.getCmp(PF+'txtFolioFin').getValue() != ''){
		                        				
		                        				if(valor > Ext.getCmp(PF+'txtFolioFin').getValue()){
		                        					Ext.Msg.alert('SET','El valor Folio Inicial debe ser menor a Folio Final.');
		  											return;
		                        				}
		                        				
		                        			}
		                        			
		                        		}
		                        	}
		                        }
                            },{
                                xtype: 'label',
                                text: 'Folio Final:',
                                x: 160,
                                y: 120
                            },{	
                                xtype: 'numberfield',
                                id: PF+'txtFolioFin',
                                name: PF+'txtFolioFin',
                                x: 220,
                                y: 120,
                                width: 70,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			
		                        			if(valor == '' || valor == NaN || valor == null || valor == undefined ){
		                						return;
		                					}
		                        			
		                        			if(Ext.getCmp(PF+'txtFolioIni').getValue() != ''){
		                        				
		                        				if(Ext.getCmp(PF+'txtFolioIni').getValue() > valor){
		                        					Ext.Msg.alert('SET','El valor Folio Final debe ser mayor a Folio Inicial.');
		  											return;
		                        				}
		                        				
		                        			}
		                        		}
		                        	}
		                        }
                            },
                            {
                                xtype: 'label',
                                text: 'Stock:',
                                x: 10,
                                y: 150
                            },{	
                                xtype: 'numberfield',
                                id: PF+'txtStock',
                                name: PF+'txtStock',
                                x: 80,
                                y: 150,
                                width: 70,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			
		                        		}
		                        	}
		                        }
                            },
                            {
                                xtype: 'label',
                                id: PF+'lblFolioImp',
                                name: PF+'lblFolioImp',
                                text: 'Ult. Cheque:',
                                hidden: true,
                                x: 160,
                                y: 150
                            },{	
                                xtype: 'numberfield',
                                id: PF+'txtFolioImp',
                                name: PF+'txtFolioImp',
                                hidden: true,
                                x: 220,
                                y: 150,
                                width: 70,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			
		                        		}
		                        	}
		                        }
                            },
                            NS.optTipoFolio
                            
                    	]
                    }
                ]
            },
            NS.gridControlCheques,
            {
				  xtype: 'button',
				  text: 'Guardar',
				  x: 400,
				  y: 240,
				  width: 80,
				  height: 22,
				  id: PF+'btnEjecutar',
				  name: PF+'btnEjecutar',
				  hidden: false,
				  listeners:{
					  click:{
						  fn:function(e){
							  
							  var opcion = NS.tipoOperacion;
							  
							  switch(opcion){
							  		case 'N':
							  			NS.guardarControlCheque();
							  			//NS.buscar();
							  			//NS.limpiarCaptura();
							  			break;
							  }
						  }
					  }
				  }
			  },
			  {
					  xtype: 'button',
					  text: 'Crear Nuevo',
					  x: 500,
					  y: 240,
					  width: 80,
					  height: 22,
					  id: PF+'btnNuevo',
					  name: PF+'btnNuevo',
					  hidden: false,
					  listeners:{
						  click:{
							  fn:function(e){
								  Ext.getCmp(PF + 'fielCapturaCheque').setDisabled(false);
								  NS.tipoOperacion = 'N';
								  NS.limpiarCaptura();
							  }
						  }
					  }
				  },
				  {
						  xtype: 'button',
						  text: 'Modificar',
						  x: 600,
						  y: 240,
						  width: 80,
						  height: 22,
						  id: PF+'btnModificar',
						  name: PF+'btnModificar',
						  disabled: true,
						  listeners:{
							  click:{
								  fn:function(e){
									  Ext.getCmp(PF + 'fielCapturaCheque').setDisabled(false);
									  
									  Ext.Msg.confirm('SET', 'El registro se modificará con los nuevos valores.', function(btn) {
					          				
						  				  if(btn == 'yes') {
						  					  NS.modificarRegistro();
						  				  }
						  				
				          			  });
									  
									 // NS.limpiarCaptura();
									 // NS.cargarDatosAreaCaptura();
								  }
							  }
						  }
					  },
					  {
							  xtype: 'button',
							  text: 'Eliminar',
							  x: 900,
							  y: 240,
							  width: 80,
							  height: 22,
							  id: PF+'btnEliminar',
							  name: PF+'btnEliminar',
							  hidden: true,
							  disabled: true,
							  listeners:{
								  click:{
									  fn:function(e){
										  Ext.getCmp(PF + 'fielCapturaCheque').setDisabled(false);

										  Ext.Msg.confirm('SET', '¿Desea eliminar el registro seleccionado?', function(btn) {
								  				if(btn == 'yes') {
								  					NS.eliminarRegistro();
						          				}
								  				
						          		  });
									  }
								  }
							  }
						  },
						  {
  							  xtype: 'button',
  							  text: 'Excel',
  							  x: 700,
  							  y: 240,
  							  width: 80,
  							  height: 22,
  							  id: PF+'btnExcel',
  							  name: PF+'btnExcel',
  							  hidden: false,
  							  listeners:{
  								  click:{
  									  fn:function(e){
  										  
  										var renglones = NS.storeControlCheques.data.items;
  										
  										if(renglones.length == 0) {
  											Ext.Msg.alert('SET','No existen registros para el reporte!!');
  											return;
  										}
  										
										var matriz = new Array();
										
										for(var i=0;i<renglones.length;i++){
											var vector = {};
											
											vector.idBanco = renglones[i].get('idBanco');
											vector.idChequera = renglones[i].get('idChequera');
											vector.folioIni = renglones[i].get('folioInvIni');
											vector.folioFin = renglones[i].get('folioInvFin');
											vector.folioActual = renglones[i].get('folioUltImpreso');
											vector.fecha = renglones[i].get('fechaAlta');
											vector.empresa = renglones[i].get('nomEmpresa');
											vector.stock = renglones[i].get('stock');
											vector.tipoFolio = renglones[i].get('tipoFolio');
											vector.estatus = renglones[i].get('estatus');
											matriz[i] = vector;
										}
										
										var jSonString = Ext.util.JSON.encode(matriz);
  										
  										NS.exportaExcel(jSonString);
  										NS.excel = false;
  										
  									  }
  								  }
  							  }
  						  },
  						{
  							  xtype: 'button',
  							  text: 'Limpiar',
  							  x: 800,
  							  y: 240,
  							  width: 80,
  							  height: 22,
  							  id: PF+'btnLimpiar',
  							  name: PF+'btnLimpiar',
  							  hidden: false,
  							  listeners:{
  								  click:{
  									  fn:function(e){
  										  NS.limpiarTodo();
  									  }
  								  }
  							  }
  						  }
        ]
	});
	
	
	
	function cambiarFecha(fecha) {
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
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
	
	/*NUEVA*/
	function cambiarFecha1(fecha) {
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		
		var mesDate = fecha.substring(0, 3);
		var dia =     fecha.substring(4,6);
		var anio =    fecha.substring(8, 12);
		
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate) {
				var mes=i+1;
					if(mes<10)
						var mes='0'+mes;
			}
		}
		var fechaString = dia+'/'+mes+'/'+anio;
		alert(fechaString);
		return fechaString;
	}
	
	
	NS.validarCampos = function(){
		
		/*********	VALIDACIÓN DE CAMPOS	**********/
		
		if(Ext.getCmp(PF+'txtEmpresaC').getValue() == '' || NS.cmbEmpresaC.getValue() == ''){
			Ext.Msg.alert('SET','El campo empresa no puede estar vacío.');
			return false;
		}
		
		if(NS.cmbBancoC.getValue() == ''){
			Ext.Msg.alert('SET','El campo banco no puede estar vacío.');
			return false;
		}
		
		if(NS.cmbChequeraC.getValue() == ''){
			Ext.Msg.alert('SET','El campo chequera no puede estar vacío.');
			return false;
		}
		
		if(Ext.getCmp(PF+'txtFolioIni').getValue() == ''){
			Ext.Msg.alert('SET','El campo folio inicial no puede estar vacío.');
			return false;
		}
		
		if(Ext.getCmp(PF+'txtFolioFin').getValue() == ''){
			Ext.Msg.alert('SET','El campo folio final no puede estar vacío.');
			return false;
		}
		
		if(Ext.getCmp(PF+'txtStock').getValue() == ''){
			Ext.Msg.alert('SET','El campo Stock no puede estar vacío.');
			return false;
		}
		
		var op = NS.optTipoFolio.getValue();
		
		if(op == null || op == undefined){
			Ext.Msg.alert('SET','El campo Stock no puede estar vacío.');
			return false;
		}
		
		return true;
	};
	
	NS.guardarControlCheque = function(){
		
		if(NS.validarCampos()){
			
			/*****************************************************************************/
			
			var matriz = new Array();
			var vec = {};
			
			vec.noEmpresa = Ext.getCmp(PF+'txtEmpresaC').getValue();
			vec.idBanco = NS.cmbBancoC.getValue();
			vec.idChequera = NS.cmbChequeraC.getValue();
			vec.folioIni = Ext.getCmp(PF+'txtFolioIni').getValue();
			vec.folioFin = Ext.getCmp(PF+'txtFolioFin').getValue();
			vec.stock = Ext.getCmp(PF+'txtStock').getValue();
			vec.tipoFolio = NS.optTipoFolio.getValue().getGroupValue();
			matriz[0] = vec;
			
			var jsonControlCheque = Ext.util.JSON.encode(matriz);
			
			ControlChequesAction.guardarControlCheque(jsonControlCheque, function(result, e){
				
				if(result != null && result != undefined && result != '') {
					if(result.error != ''){
						Ext.Msg.alert('SET',''+result.error);
					}else{
						Ext.Msg.alert('SET',''+result.mensaje);
						NS.limpiarCaptura();
						NS.buscar();
					}
				}
			});
			
		}
		
	};
	
	NS.limpiarTodo = function(){
		
		NS.tipoOperacion = '';
		Ext.getCmp(PF+'txtEmpresa').setValue('');
		NS.cmbEmpresa.reset();
		NS.cmbBanco.reset();
		NS.cmbChequera.reset();
		NS.storeChequera.removeAll();
		
		Ext.getCmp(PF+'txtEmpresaC').setValue('');
		NS.cmbEmpresaC.reset();
		NS.cmbBancoC.reset();
		NS.cmbChequeraC.reset();
		NS.storeChequeraC.removeAll();
		Ext.getCmp(PF+'txtFolioIni').setValue('');
		Ext.getCmp(PF+'txtFolioFin').setValue('');
		Ext.getCmp(PF + 'txtFolioImp').setValue('');
		Ext.getCmp(PF+'txtStock').setValue('');
		Ext.getCmp(PF + 'optFolioPapel').setValue(true);
		Ext.getCmp(PF + 'optFolioCheque').setValue(false);
		Ext.getCmp(PF + 'chkFolioPapel').setValue(false);

		Ext.getCmp(PF + 'chkEstatusChequeras').setValue(false);
		Ext.getCmp(PF + 'chkEstatusChequerasT').setValue(false);
		Ext.getCmp(PF + 'fielCapturaCheque').setDisabled(true);
		Ext.getCmp(PF + 'txtFolioImp').setVisible(false);
		Ext.getCmp(PF + 'lblFolioImp').setVisible(false);
		NS.storeControlCheques.baseParams.noEmpresa = 0;
		NS.storeControlCheques.baseParams.idBanco = 0;
		NS.storeControlCheques.baseParams.idChequera = '';
		NS.storeControlCheques.baseParams.folioPapel = false;
		NS.storeControlCheques.baseParams.estatusChequeras = false;
		NS.storeControlCheques.baseParams.estatusChequerasT = false;
		NS.storeControlCheques.removeAll();
		NS.gridControlCheques.getView().refresh();
	};
	
	NS.limpiarCaptura = function(){
		Ext.getCmp(PF+'txtEmpresaC').setValue('');
		NS.cmbEmpresaC.reset();
		NS.cmbBancoC.reset();
		NS.cmbChequeraC.reset();
		NS.storeChequeraC.removeAll();
		Ext.getCmp(PF+'txtFolioIni').setValue('');
		Ext.getCmp(PF+'txtFolioFin').setValue('');
		Ext.getCmp(PF + 'txtFolioImp').setValue('');
		Ext.getCmp(PF+'txtStock').setValue('');
		Ext.getCmp(PF + 'txtFolioImp').setVisible(false);
		Ext.getCmp(PF + 'lblFolioImp').setVisible(false);
		Ext.getCmp(PF + 'optFolioPapel').setValue(true);
		Ext.getCmp(PF + 'optFolioCheque').setValue(false);
	};
	
	NS.buscar = function(){
		
		  NS.storeControlCheques.baseParams.noEmpresa = 0;
		  NS.storeControlCheques.baseParams.idBanco = 0;
		  NS.storeControlCheques.baseParams.idChequera = '';
		
		  if(Ext.getCmp(PF + 'chkFolioPapel').getValue() == true){
			  NS.storeControlCheques.baseParams.folioPapel = true;
		  }else{
			  NS.storeControlCheques.baseParams.folioPapel = false;
		  }
		  
		  if(Ext.getCmp(PF + 'chkEstatusChequeras').getValue() == true){
			  NS.storeControlCheques.baseParams.estatusChequeras = true;
		  }else{
			  NS.storeControlCheques.baseParams.estatusChequeras = false;
		  }
		  
		  if(Ext.getCmp(PF + 'chkEstatusChequerasT').getValue() == true){
			  NS.storeControlCheques.baseParams.estatusChequerasT = true;
		  }else{
			  NS.storeControlCheques.baseParams.estatusChequerasT = false;
		  }
		  
		  if(Ext.getCmp(PF+'txtEmpresa').getValue() != ''){
			  NS.storeControlCheques.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtEmpresa').getValue());
		  }
		  
		  if(NS.cmbBanco.getValue() != ''){
			  if(NS.cmbChequera.getValue() != ''){
				  NS.storeControlCheques.baseParams.idBanco = parseInt(NS.cmbBanco.getValue());
				  NS.storeControlCheques.baseParams.idChequera = NS.cmbChequera.getValue();
			  }else{
				  Ext.Msg.alert('SET','Es necesario seleccionar una chequera');
				  return;
			  }
		  }

		  var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeControlCheques, msg:"Buscando Datos..."});
		  NS.storeControlCheques.load();
		  
	};
	
	NS.cargarDatosAreaCaptura = function(){
		var regSel = NS.gridControlCheques.getSelectionModel().getSelections();
		
		if(regSel.length > 0){
			
			Ext.getCmp(PF+'txtEmpresaC').setValue(regSel[0].get('noEmpresa'));
			NS.cmbEmpresaC.setValue(regSel[0].get('nomEmpresa'));
			NS.cmbBancoC.setValue(regSel[0].get('idBanco'));
			NS.cmbChequeraC.setValue(regSel[0].get('idChequera'));
			Ext.getCmp(PF+'txtFolioIni').setValue(regSel[0].get('folioInvIni'));
			Ext.getCmp(PF+'txtFolioFin').setValue(regSel[0].get('folioInvFin'));
			Ext.getCmp(PF+'txtStock').setValue(regSel[0].get('stock'));

			Ext.getCmp(PF + 'txtFolioImp').setVisible(true);
			Ext.getCmp(PF + 'lblFolioImp').setVisible(true);
			Ext.getCmp(PF+'txtFolioImp').setValue(regSel[0].get('folioUltImpreso'));
			if(regSel[0].get('tipoFolio') == 'P'){
				Ext.getCmp(PF + 'optFolioPapel').setValue(true);
			}else{
				Ext.getCmp(PF + 'optFolioCheque').setValue(true);
			}
			
		}else{
			Ext.Msg.alert('SET','Por favor seleccione un registro.');
		}
		
	};
	
	NS.eliminarRegistro = function(){
		
		var regSel = NS.gridControlCheques.getSelectionModel().getSelections();
		
		if(regSel.length > 0){
			
			var matriz = new Array();
			var vec = {};
			vec.idControlCheque = regSel[0].get('idControlCheque');
			vec.noEmpresa = regSel[0].get('noEmpresa');
			vec.idBanco = regSel[0].get('idBanco');
			vec.idChequera = regSel[0].get('idChequera');
			vec.tipoFolio = regSel[0].get('tipoFolio');
			matriz[0] = vec;
			
			var jsonControlCheque = Ext.util.JSON.encode(matriz);
			
			ControlChequesAction.eliminarControlCheque(jsonControlCheque, function(result, e){
				
				if(result != null && result != undefined && result != '') {
					if(result.error != ''){
						Ext.Msg.alert('SET',''+result.error);
					}else{
						Ext.Msg.alert('SET',''+result.mensaje);
						NS.limpiarCaptura();
						NS.buscar();
					}
				}
			});
			
		}else{
			Ext.Msg.alert('SET','No hay datos seleccionados');
		}
		
	};
	
	NS.modificarRegistro = function(){
		
		var regSel = NS.gridControlCheques.getSelectionModel().getSelections();
		
		if(regSel.length > 0){
			
			if(NS.validarCampos()){
				/******	Datos original que se modificaran	*******/
				var matrizOrig = new Array();
				var vecOrig = {};
				vecOrig.idControlCheque = regSel[0].get('idControlCheque');
				vecOrig.noEmpresa = regSel[0].get('noEmpresa');
				vecOrig.idBanco = regSel[0].get('idBanco');
				vecOrig.idChequera = regSel[0].get('idChequera');
				vecOrig.tipoFolio = regSel[0].get('tipoFolio');
				matrizOrig[0] = vecOrig;
				
				var jsonOrig = Ext.util.JSON.encode(matrizOrig);
				/*****************************************************/
				
				/******	Nuevos Datos a modificar	******/
				var matriz = new Array();
				var vec = {};
				
				vec.noEmpresa = Ext.getCmp(PF+'txtEmpresaC').getValue();
				vec.idBanco = NS.cmbBancoC.getValue();
				vec.idChequera = NS.cmbChequeraC.getValue();
				vec.folioIni = Ext.getCmp(PF+'txtFolioIni').getValue();
				vec.folioFin = Ext.getCmp(PF+'txtFolioFin').getValue();
				vec.folioUltImp = Ext.getCmp(PF+'txtFolioImp').getValue();
				vec.stock = Ext.getCmp(PF+'txtStock').getValue();
				vec.tipoFolio = NS.optTipoFolio.getValue().getGroupValue();
				matriz[0] = vec;
				
				var jsonControlCheque = Ext.util.JSON.encode(matriz);
				/*****************************************************/
				
				ControlChequesAction.modificarControlCheque(jsonOrig, jsonControlCheque, function(result, e){
					
					if(result != null && result != undefined && result != '') {
						if(result.error != ''){
							Ext.Msg.alert('SET',''+result.error);
						}else{
							Ext.Msg.alert('SET',''+result.mensaje);
							NS.limpiarCaptura();
							NS.buscar();
						}
					}
				});
			}
			
		}else{
			Ext.Msg.alert('SET','No hay datos seleccionados');
		}
		
	};
	
	NS.exportaExcel = function(jsonCadena) {
		
		var titulo = '';
		if(Ext.getCmp(PF + 'chkFolioPapel').getValue() == true){
			titulo = 'Control Folios Papel';
		}else{
			titulo = 'Control Folios Cheques';
		}
		
		
		ControlChequesAction.exportaExcel(jsonCadena, titulo, function(res, e){
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=controlCheques';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
		});
	};
	
	NS.contenedorControlCheques.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	//staticCheck("#gridConsulta div div[class='x-panel-ml']","#gridConsulta div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
});