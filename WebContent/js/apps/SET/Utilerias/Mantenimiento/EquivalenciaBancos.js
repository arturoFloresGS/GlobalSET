Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.EquivalenciaBancos');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
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
	
	NS.lblBanco = new Ext.form.Label({
		text: 'Bancos SET',
		x:0,
		y:300
	});
	
	NS.storeUnicoBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_banco',
			campoDos:'desc_banco',
			tabla:'cat_banco',
			condicion:'',
			orden:'',
			registroUnico:false
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
		root: '',
		directFn: EquivalenciaBancosAction.llenarComboBanco, 
		idProperty: 'idBanco', 
		fields: [
			 {name: 'idBanco'},
			 {name: 'descBanco'},
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existe el banco con esa clave');
					Ext.getCmp(PF + 'txtIdBanco').setValue('');
					NS.cmbBanco.reset();
					return;
				}
				else{
					var reg = NS.storeUnicoBanco.data.items;
					Ext.getCmp(NS.cmbBanco.getId()).setValue(reg[0].get('descBanco'));
					NS.accionarUnicoBanco(reg[0].get('idBanco'));
				}
			}
		}
	}); 
	
	NS.accionarUnicoBanco = function(valueCombo){
 		NS.idBanco=valueCombo;
 		NS.descBanco=NS.storeUnicoBanco.getById(valueCombo).get('descBanco');

 	}
	
	NS.txtIdBanco = new Ext.form.TextField({
	         id: PF+'txtIdBanco',
	         name: PF+'txtIdBanco',
	         x: 60,
	         y: 300,
	         width: 50,
	         listeners:{
	         	change:{
	         		fn:function(caja, valor){
	         			
	         			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	         				NS.storeUnicoBanco.baseParams.registroUnico=true;
	         				NS.storeUnicoBanco.baseParams.condicion=''+caja.getValue();
	         				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBanco, msg:"Cargando Datos..."});
	         				NS.storeUnicoBanco.load();
	        				}else {
	        					NS.cmbBanco.reset();
	        					NS.storeUnicoBanco.removeAll();
	        				}
	         		}
	         	}
	         }
	  });
	
	
	/**** Grid de Bancos Extranjeros ****/

	NS.storeLlenaGridBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {bankl:'',banka:'',idBanco:'',descBanco:''},
		paramOrder: ['bankl','banka','idBanco','descBanco'],
		directFn: EquivalenciaBancosAction.llenaGridBancos,
		fields: [
		    {name: 'bankl'},
		 	{name: 'banka'},
		 	{name: 'idBanco'},
		 	{name: 'descBanco'},
		],
		listeners: {
			load: function(s, records) {
				if (records.length == null || records.length <= 0){Ext.Msg.alert('SET', 'No existen bancos');}
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}	
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridBancos, msg: "Buscando..."});
	NS.storeLlenaGridBancos.load();

	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {texto : ''},
		paramOrder: ['texto'],
		directFn: EquivalenciaBancosAction.consultarBancos, 
		idProperty: 'idBanco', 
		fields: [
			 {name: 'idBanco'},
			 {name: 'descBanco'}
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProveedor, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen bancos con ese nombre');
					Ext.getCmp(PF + 'txtIdBanco').setValue('');
					NS.cmbProveedor.reset();
				}
			}
		}
	}); 

	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancos
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 120
        ,y: 300
        ,width: 210
		,valueField:'idBanco'
		,displayField:'descBanco'
		,autocomplete: true
		,emptyText: 'Seleccione un Banco'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{ fn: function(combo, valor) {
					//linea cambia combo
					BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBanco.getId());
				}
			},
			beforequery: function(qe){
				NS.parametroBus=qe.combo.getRawValue();
			}
		}
	});
	
	NS.btnBanco={
        xtype: 'button',
        text: '...',
        x: 335,
        y: 300,
        width: 20,
        id: PF+'btnBanco',
        name: PF+'btnBanco',
        listeners:{
        	click:{
        		fn:function(e){
        			  if(NS.parametroBus==='' || NS.parametroBus.length < 3) {
							  Ext.Msg.alert('SET','Es necesario escribir un mínimo de 3 caracteres para la búsqueda');
							  Ext.getCmp(PF + 'txtIdBanco').setValue('');
							  NS.cmbBanco.reset();
        			  }else{
							  NS.storeBancos.baseParams.texto=NS.parametroBus;
							  var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
							  Ext.getCmp(PF + 'txtIdBanco').setValue('');
							  NS.cmbBanco.reset();
							  NS.storeBancos.load();
						  }
						
        		}
        	}
        }
	}
	
	NS.btnExcel={
	        xtype: 'button',
	        text: 'Excel',
	        x: 580,
	        y: 300,
	        width: 100,
	        id: PF+'btnExcel',
	        name: PF+'btnExcel',
	        listeners:{
	        	click:{
	        		fn:function(e){
	        			parametros='?nomReporte=excelBancosExt';
						window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);	
	        		}
	        	}
	        }
		}

	
	NS.btnRegistrar={
	        xtype: 'button',
	        text: 'Registrar',
	        x: 700,
	        y: 300,
	        width: 100,
	        id: PF+'btnRegistrar',
	        name: PF+'btnRegistrar',
	        listeners:{
	        	click:{
	        		fn:function(e){
	        			var registroSeleccionado = NS.gridBancos.getSelectionModel().getSelections();
	        			if(NS.cmbBanco.getValue()=='' || NS.txtIdBanco.getValue()==''){
	        				return Ext.Msg.alert('SET', 'Se debe elegir un banco');
	        			}
	        			if(registroSeleccionado.length <= 0){
	        				return Ext.Msg.alert('SET', 'Se debe de elegir un registro');
	        			}else{
	        				NS.bankl= NS.gridBancos.getSelectionModel().getSelections()[0].get('bankl');
	        				NS.idBancoGrid = NS.gridBancos.getSelectionModel().getSelections()[0].get('idBanco');
	        				NS.idBancoText = NS.txtIdBanco.getValue();
	        				EquivalenciaBancosAction.actualizaEquivaleBanco(NS.bankl,NS.idBancoGrid,NS.idBancoText,function(resultado, e) {
	        				if(resultado!= '' && resultado!= undefined && resultado!= null){
                   				 
	    	    				 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridBancos, msg: "Cargando Datos..."});
	    	    				 NS.storeLlenaGridBancos.load();
	    	    				 NS.txtIdBanco.setValue('');
	    	    				 NS.cmbBanco.reset();
	    	    				 Ext.Msg.alert('SET', resultado);
	        				}
                    		});
	        			}
	        		}
	        	}
	        }
		}

	NS.filterRow = new Ext.ux.grid.FilterRow({
		id: PF + 'filterRow',
		refilterOnStoreUpdate: true
	});

	NS.columnaSeleccionBancos= new Ext.grid.CheckboxSelectionModel({
		singleSelect: true
	});
	
	
	NS.columnasBancos = new Ext.grid.ColumnModel
	([
	  NS.columnaSeleccionBancos,
	  {header: 'ID Banco SAP', width: 100, dataIndex: 'bankl', sortable: true,filter: {testIdSAP: "/^{0}/i"}},
	  {header: 'Nombre Banco SAP', width: 270, dataIndex: 'banka', sortable: true},
	  {header: 'ID Banco SET', width: 100, dataIndex: 'idBanco', sortable: true},
	  {header: 'Nombre Banco SET', width: 270, dataIndex: 'descBanco', sortable: true},

	]);
			
	NS.gridBancos = new Ext.grid.GridPanel({
		store: NS.storeLlenaGridBancos,
		id: PF + 'gridBancos',
		name: PF + 'gridBancos',
		cm: NS.columnasBancos,
		sm: NS.columnaSeleccionBancos,
		x: 0,
		y: 0,
		width: 790,
		height: 250,
		plugins: [NS.filterRow],
		stripRows: true,
		columnLines: true,
		listeners: {
			click:{
				fn:function(grid){				 
					 //NS.limpiarTodo();
				}
			}
		}
	});
	
	
	/******* Panels ******/
	
	NS.panelComponentes = new Ext.form.FieldSet({
		title: 'Bancos Extranjeros',
		x: 0,
		y: 0,
		width: 1000,
		height: 450,
		layout: 'absolute',
		autoScroll: false,	 
		items:[	
		  NS.gridBancos,
		  NS.lblBanco,
		  NS.txtIdBanco,
		  NS.cmbBanco,
		  NS.btnBanco,
		  NS.btnRegistrar,
		  NS.btnExcel
		]
	});

	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 10,
		y: 0,
		width: 1060,		
		height: 490,
		layout: 'absolute',
		items:[			 	 	
		 	NS.panelComponentes
	    ]
	});
	
	NS.equivalenciaBancos = new Ext.FormPanel ({
		title: 'Bancos Extranjeros',
		width: 1085,
		height: 535,
		frame: true,
		padding: 10,
		autoScroll: false,
		layout: 'absolute',
		id: PF + 'equivalenciaBancos',
		name: PF + 'equivalenciaBancos',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});

	//NS.panelMantenimiento.hide();
	NS.equivalenciaBancos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());	
});