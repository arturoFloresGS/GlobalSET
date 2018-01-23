Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Utilerias.Mantenimiento.Catalogos');
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';

	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.noEmpresa = apps.SET.noEmpresa;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.storeLlenaGrid=new Ext.data.DirectStore({autoLoad : false, fields: ['id']});
	NS.storeLlenaGrid.singleSort('id', 'ASC');
	NS.titleCat='Catalogo';
	NS.fieldsCat=[];
	NS.columnsCat=[];
	NS.numAdd=0;
	NS.newRecord=null;

	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
	// ---------------- Labels ----------------------------
	NS.lblCatalogos = new Ext.form.Label({
		text: 'Catálogos',
		x:10,
		y:0
	});
	
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa:',
		hidden: true,
        x: 250,
        y: 0	
	});

	NS.lblBanco = new Ext.form.Label({
		text: 'Banco:',
		hidden: true,
        x: 530,
        y: 	0	
	});

	NS.lblChequera = new Ext.form.Label({
		text: 'Chequera:',
		hidden: true,
        x: 780,
        y: 0		
	});
	
	// --------------- Cajas de texto ---------------------
	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
        name:PF + 'txtNoEmpresa',
        hidden: true,
        x: 250,
        y: 15,
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
						Ext.getCmp(PF + 'txtNoEmpresa').setValue('0');
						NS.cmbEmpresas.reset();
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresas.getId());
					}
					NS.storeBancos.removeAll();
					NS.storeChequeras.removeAll();
					Ext.getCmp(PF+'cmbChequeras').setValue('');
					Ext.getCmp(PF+'txtIdBanco').setValue('');
					Ext.getCmp(PF+'cmbBancos').setValue('');
					NS.storeBancos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg: "Cargando bancos..."});
					NS.storeBancos.load();
				}
			}
        }
	});
	
	NS.txtIdBanco = new Ext.form.TextField({
		id: PF+'txtIdBanco',
        name: PF+'txtIdBanco',
        x: 530,
        y: 15,
        hidden: true,
        width: 50,
        listeners:{
        	change:{
       	         fn:function(caja, valor){
 					var err=1;
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != ''){
							var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdBanco',NS.cmbBancos.getId());
				
							if(comboValue!='' && comboValue !='undefined' && comboValue != null){
								err=0;
							}
					}
					
					if(err==1){
						Ext.getCmp(PF + 'txtIdBanco').setValue('');
						NS.cmbBancos.reset();
						NS.cmbChequeras.reset();
					}
					
           			NS.accionarCmbBancos(comboValue);
           			NS.noBanco=caja.getValue();
 				}
 			}
         }
 	});
	
	NS.addRecord = function(e,o){
		if(Ext.isEmpty(NS.cmbCatalogos.getValue())){
			Ext.Msg.alert('SET','Primero debe seleccionar un catálogo');
			return;
		}
		
		if(NS.storeLlenaGrid.getTotalCount()/10 > 1){
			NS.gridPagingBar.moveLast();
		}
		
		var RenglonCatalogo = Ext.data.Record.create(NS.storeLlenaGrid.fields);
		NS.newRecord = new RenglonCatalogo();
		NS.storeLlenaGrid.add(NS.newRecord);
	};
	
	NS.deleteRecord = function(e,o){
		NS.recordToDelete = NS.gridCatalogos.getSelectionModel().getSelections();
		if (NS.recordToDelete <= 0){
			Ext.Msg.alert('SET', 'Debe de seleccionar un registro');
			return;
		}
		if(NS.recordToDelete[0].get(NS.fieldsCat[0].name)==undefined)
			NS.storeLlenaGrid.remove(NS.recordToDelete[0]);
		else{
			var catalogo=NS.cmbCatalogos.getValue().toString();
			var campo=NS.fieldsCat[0].name.toString();
			var valor=NS.recordToDelete[0].get(campo).toString();
			
			if(Ext.isEmpty(NS.fieldsCat[0].type))		
			valor=Ext.util.Format.date(valor,'d/m/Y');
			console.log(NS.recordToDelete[0]);
			Ext.Msg.confirm("SET","Desea eliminar el registro "+valor+"?",function(btn){
				if(btn=='yes')
					MantenimientoCatalogosAction.deleteRecord(catalogo,campo,valor,function(res,e){
					
					if(res<=0){
						Ext.Msg.alert('SET', 'No se pudo eliminar registro '+valor);
					}
					else{
						NS.gridPagingBar.doRefresh();
						NS.storeLlenaGrid.remove(NS.recordToDelete[0]);
						NS.gridCatalogos.getView().refresh();
						
					}
						
					});
			});
		}
	};
	
	NS.save=function(){
		var vector = {};
		var matriz = new Array();
		
		if(NS.cmbCatalogos.getValue()==''){
			Ext.Msg.alert('SET','Primero debe seleccionar un catalogo');
			return;
		}
		var records=NS.storeLlenaGrid.getModifiedRecords();
		var valor;
		if(records.length > 0){
			for(var j=0;j<records.length;j++){
				for(var i=0;i<NS.fieldsCat.length;i++){
					try{
						//if(NS.fieldsCat[0].name.toString()==records[j]){
						
							valor = records[j].get(NS.fieldsCat[i].name);
							console.log(valor);
						if(Ext.isEmpty(NS.fieldsCat[i].type))		
							valor=Ext.util.Format.date(valor,'d/m/Y');
							eval("vector."+NS.fieldsCat[i].name+"='"+valor+"';");					    
					}catch(Exception){
						Ext.Msg.alert('SET','Error en los datos de entrada');
					}
				}
				matriz[j]=vector;
				vector={};
			}
			MantenimientoCatalogosAction.saveRecords(NS.cmbCatalogos.getValue().toString(),Ext.util.JSON.encode(matriz), Ext.util.JSON.encode(NS.fieldsCat), function(r){
				var resultado=Ext.util.JSON.decode(r);
				if(resultado.success)
					NS.storeLlenaGrid.commitChanges();
				Ext.Msg.alert('SET',resultado.message);
			});
		}else{
			Ext.Msg.alert('SET','No hay cambios que salvar en los registros');
		}
		NS.storedHeads.reload();
	};
	
	NS.reporteExcel= function(){
		parametros='?nomReporte=excelCatalogo';
		parametros+='&nomParam1=catalogo';
		parametros+='&valParam1=' + NS.cmbCatalogos.getValue();
		window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
	};
	
	// --------------- Storeds ----------------------------
	NS.storedHeads = new Ext.data.DirectStore({
		paramsAsHash:true,
		root:'',
		baseParams:{},
		paramOrder:['nombreCatalogo','titulos','col'],
		directFn:MantenimientoCatalogosAction.obtenHeads,
		idProperty:'catalogo',
		fields:[
			'catalogo',
			'columns',
			'fields'
		],
		listeners: {
			load:function(s,records){
				if (records.length == null || records.length <= 0) {
					Ext.Msg.alert('SET', 'No existen Catálogos dados de alta');
				}
				
				NS.fieldsCat = 
					Ext.util.JSON.decode(
							NS.storedHeads.getById(
									Ext.getCmp(
											PF + 'cmbCatalogos').getValue()).get('fields'));
				
				NS.nomCat = Ext.getCmp(PF + 'cmbCatalogos').getValue();
				


				NS.storeLlenaGrid = new Ext.data.DirectStore({
					id: PF + 'storedCatalogos',
					autoLoad   : false, 
					paramsAsHash: true,
					autoSave : false,
					root: '',
					baseParams: {nombreCatalogo: NS.nomCat,noEmpresa:"",noBanco:"",idChequera:""},
					paramOrder: ['nombreCatalogo','noEmpresa','noBanco','idChequera'],
					directFn: MantenimientoCatalogosAction.llenaGridCatalogos,
					fields: NS.fieldsCat,
					listeners: {
						load: function(s, records) {
							if(records.length == null || records.length <= 0) {
								Ext.Msg.alert('SET', 'No existe información para estos parametros');
							}
						}
					}
				});
				
				NS.columnsCat = new Ext.grid.ColumnModel(
						Ext.util.JSON.decode(
								NS.storedHeads.getById(
										Ext.getCmp(
												PF + 'cmbCatalogos').getValue()).get('columns')));
				
				NS.titleCat = NS.storeCatalogos.getById(
						NS.cmbCatalogos.getValue()).get('descCatalogo');
				
				NS.gridCatalogos.setTitle(NS.titleCat);
				
				NS.gridCatalogos.reconfigure(NS.storeLlenaGrid, NS.columnsCat);
				NS.gridPagingBar.bindStore(NS.storeLlenaGrid, true);
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando información..."});
				NS.storeLlenaGrid.baseParams.noEmpresa=NS.txtNoEmpresa.getValue();
		    	NS.storeLlenaGrid.baseParams.noBanco=NS.txtIdBanco.getValue();
		    	NS.storeLlenaGrid.baseParams.idChequera=NS.cmbChequeras.getValue();
		    	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGrid, msg: "Cargando información..."});
				NS.storeLlenaGrid.load({params: {start: 0, limit: 10}});
				
			}
		}
	});

	NS.gridPagingBar = new Ext.PagingToolbar({  
	    pageSize: 10,
	    store : NS.storeLlenaGrid,
	    autoHeight:true,
	    autoWidth:true,
	    displayInfo: true,
	    emptyMsg : 'No hay datos para este catálogo'
	});

	NS.gridTopbar = new Ext.Toolbar({  
	    id: 'categoriesGridTopbarId',  
	    items: [
	        {  
	            xtype:'button',  
	            text:'Agregar Nuevo',
	            iconCls:'forma_alta',
	            id: 'new_btn',  
	            handler: NS.addRecord,  
	            type:'button'  
	        },' | ',
	        {  
	            xtype:'button',  
	            text:'Eliminar',
	            iconCls:'forma_baja',
	            id: 'delete_btn',  
	            handler:NS.deleteRecord,  
	            type:'button'  
	        },' | ',
	        {  
	            xtype:'button',  
	            text:'Guardar',  
	            id: 'save_btn',  
	            handler: NS.save,  
	            type:'button'  
	        },' | ',
	        {  
	            xtype:'button',  
	            text:'Excel',  
	            id: 'excel_btn',  
	            handler: NS.reporteExcel,  
	            type:'button'  
	        }
	    ]  
	});

	NS.gridCatalogos = new Ext.grid.EditorGridPanel({
		store : NS.storeLlenaGrid,
		id: PF + 'gridDatos',
		name: PF + 'gridDatos',
		columns: [],
		header:true,
		enableDragDrop: false,
		stripeRows: true,
		columnsLine: true,
		enableColLock: false,
	    selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),  
	    bbar: NS.gridPagingBar,
	    tbar: NS.gridTopbar,
	    height: 300,
		width: 950,
		x: 0,
		y: 0
	});
	
	NS.storeCatalogos = new Ext.data.DirectStore({
    	paramsAsHash: false,
		root: '',
		directFn: MantenimientoCatalogosAction.llenaComboCatalogos,
		idProperty: 'nombreCatalogo',
		fields: [
			'nombreCatalogo',
			'descCatalogo',
			'titulos',
			'col',
			'botones'
		],
		listeners: {
			load: function(s, records){
				if (records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'Error al llenar el combo de catálogos');
			}
		}
    });
	
	var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCatalogos, msg: "Cargando catálogos..."});
	NS.storeCatalogos.load();
	
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
				}else{
					var recordsStoreGruEmp = NS.storeEmpresas.recordType;	
					var todas = new recordsStoreGruEmp({
				       	id : '0',
				       	descripcion : '***********TODAS***********'
			       	});
					NS.storeEmpresas.insert(0,todas);
				}
					
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
				NS.txtIdBanco.setValue('');
				//NS.cmb
			}
		}
	});
	
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{noEmpresa:0},
		root: '',
		paramOrder:['noEmpresa'],
		root: '',
		directFn: MantenimientoCatalogosAction.llenarComboBancos, 
		idProperty: 'id',
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0){
					NS.txtIdBanco.setValue('');
					NS.cmbBancos.reset();
					NS.cmbChequeras.reset();
					Ext.Msg.alert('SET','No hay bancos disponibles para la empresa');
					return;
				}
				
			},
			exception:function(misc) {
				NS.txtIdBanco.setValue('');
				NS.cmbBancos.reset();
				NS.cmbChequeras.reset();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
				return;
				
			}
		}
	});
	
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noBanco: 0,
			noEmpresa:0
		},
		root: '',
		paramOrder:['noBanco','noEmpresa'],
		root: '',
		directFn: MantenimientoCatalogosAction.llenarComboChequeras, 
		idProperty: 'descripcion', 
		fields: [
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No hay chequeras disponibles para el banco');
					NS.cmbChequeras.reset();
				}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
				NS.cmbChequeras.reset();
			}
		}
	});
	
	// ---------------- Combos ----------------------------
	NS.cmbCatalogos = new Ext.form.ComboBox({
        store: NS.storeCatalogos,
        name: PF+'cmbCatalogos',
        id: PF+'cmbCatalogos',
        typeAhead: true,
        mode: 'local',
        minChars: 0,
        selecOnFocus: true,
        forceSelection: true,
        x: 10,
        y: 15,
        width: 200,
        valueField: 'nombreCatalogo',
        displayField: 'descCatalogo',
        autocomplete: true,
        emptyText: 'Seleccione un Catálogo',
        triggerAction: 'all',
        value: '',
        listeners:{
			select: function(combo, record, index) {
				if (combo.getValue()=='cat_ctas_contables_erp') {
					NS.lblEmpresa.setVisible(true);
					NS.lblBanco.setVisible(true);
					NS.lblChequera.setVisible(true);
					NS.txtNoEmpresa.setVisible(true);
					NS.cmbEmpresas.setVisible(true);
					NS.txtIdBanco.setVisible(true);
					NS.cmbBancos.setVisible(true);
					NS.cmbChequeras.setVisible(true);
					Ext.getCmp(PF+'btnBuscar').setVisible(true);
					
					NS.txtNoEmpresa.setValue('0');
					NS.cmbEmpresas.reset();
					NS.txtIdBanco.setValue('');
					NS.cmbBancos.reset();
					NS.cmbChequeras.reset();
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg: "Cargando empresas..."});
					NS.storeEmpresas.load();
				} else {
					NS.lblEmpresa.setVisible(false);
					NS.lblBanco.setVisible(false);
					NS.lblChequera.setVisible(false);
					NS.txtNoEmpresa.setVisible(false);
					NS.cmbEmpresas.setVisible(false);
					NS.txtIdBanco.setVisible(false);
					NS.cmbBancos.setVisible(false);
					NS.cmbChequeras.setVisible(false);
					Ext.getCmp(PF+'btnBuscar').setVisible(false);
				}
				
				NS.botones = JSON.parse(
						NS.storeCatalogos.getById(
								combo.getValue()).get('botones'));
				
				Ext.getCmp('new_btn').setDisabled(NS.botones.crear == 'true' ? false : true);
				Ext.getCmp('delete_btn').setDisabled(NS.botones.eliminar == 'true' ? false : true);
				Ext.getCmp('save_btn').setDisabled(NS.botones.modificar == 'true' ? false : true);
				
				NS.storedHeads.baseParams.nombreCatalogo = combo.getValue();
				NS.storedHeads.baseParams.titulos=NS.storeCatalogos.getById(combo.getValue()).get('titulos');
				NS.storedHeads.baseParams.col=NS.storeCatalogos.getById(combo.getValue()).get('col');
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storedHeads, msg: "Cargando información..."});
				NS.storedHeads.load();
			}
        }
	});
	
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		hidden: true,
		x: 310,
        y: 15,
        width: 210,
		typeAhead: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value:'',
		autocomplete:true,
		selectOnFocus:true,
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
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg: "Cargando bancos..."});
					NS.storeBancos.load();
				}
			}
		}
	});
	
	NS.cmbBancos = new Ext.form.ComboBox({
		store: NS.storeBancos,
		id: PF + 'cmbBancos',
		name: PF + 'cmbBancos',
		hidden: true,
		x: 590,
        y: 15,
        width: 170,
		typeAhead: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		emptyText: 'Seleccione un banco',
		triggerAction: 'all',
		value:'',
		autocomplete:true,
		selectOnFocus:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.noBanco=combo.getValue();
					BFwrk.Util.updateComboToTextField(PF+'txtIdBanco',NS.cmbBancos.getId());
					NS.accionarCmbBancos(combo.getValue());	
					NS.storeChequeras.removeAll();
					NS.cmbChequeras.reset();
				}
			},
			change:{
				fn:function(combo, valor) {
					if(combo.getValue()==''){
						Ext.getCmp(PF + 'txtIdBanco').setValue('');
						NS.cmbBancos.reset();
						NS.storeChequeras.removeAll();
						NS.cmbChequeras.reset();
					}
				}
			}
		}
	});
	
	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras,
		id: PF + 'cmbChequeras',
		name: PF + 'cmbChequeras',
		hidden: true,
		x: 780,
        y: 15,
        width: 170,
		typeAhead: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'descripcion',
		displayField: 'descripcion',
		emptyText: 'Seleccione una chequera',
		triggerAction: 'all',
		value:'',
		autocomplete:true,
		selectOnFocus:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
					NS.chequera=combo.getValue();
				}
			}
		}
	});

	NS.buscar=function(){
    	NS.storeLlenaGrid.removeAll();
    	NS.botones = JSON.parse(
				NS.storeCatalogos.getById(NS.cmbCatalogos.getValue()).get('botones'));
				Ext.getCmp('new_btn').setDisabled(NS.botones.crear == 'true' ? false : true);
				Ext.getCmp('delete_btn').setDisabled(NS.botones.eliminar == 'true' ? false : true);
				Ext.getCmp('save_btn').setDisabled(NS.botones.modificar == 'true' ? false : true);
				
		NS.storedHeads.baseParams.nombreCatalogo = NS.cmbCatalogos.getValue();
		NS.storedHeads.baseParams.titulos=NS.storeCatalogos.getById(NS.cmbCatalogos.getValue()).get('titulos');
		NS.storedHeads.baseParams.col=NS.storeCatalogos.getById(NS.cmbCatalogos.getValue()).get('col');		
		
		var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storedHeads, msg: "Cargando información..."});
		NS.storedHeads.load();
    };

	NS.accionarCmbBancos = function(comboValor){
		Ext.getCmp(PF+'cmbChequeras').reset();
		NS.storeChequeras.baseParams.noBanco = comboValor;		
		NS.storeChequeras.baseParams.noEmpresa = parseInt(Ext.getCmp(PF+'txtNoEmpresa').getValue());
		var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg: "Cargando chequeras..."});
		NS.storeChequeras.load();
		//NS.descBanco = NS.storeBancos.getById(comboValor).get('descripcion');
	};	

	NS.panelPrincipal = new Ext.FormPanel({
		title: 'Catálogos',
		width: 1300,
		height: 600,
		frame: true,
		layout: 'absolute',
		renderTo: NS.tabContId,
		autoScroll: true,
		html: '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="Fondo" align="middle">',
		items: [
			
			{
				xtype: 'fieldset',
				title: '',
				x: 20,
				y: 15,
				width: 1010,
				height: 495,
				layout: 'absolute',
				items: [
				    {
				    	xtype: 'fieldset',
			    		title: 'Busqueda',
			    		x: 0,
			    		y: 0,
			    		width: 985,
			    		height: 120,
			    		layout: 'absolute',
			    		items: [
			    		        NS.lblCatalogos,
			    		        NS.cmbCatalogos,
			    		        NS.lblEmpresa,
			    		        NS.lblBanco,
			    		        NS.lblChequera,
			    		        NS.txtNoEmpresa,
			    		        NS.cmbEmpresas,
			    		        NS.txtIdBanco,
			    		        NS.cmbBancos,
			    		        NS.cmbChequeras,
			    		        {
			    		     		xtype: 'button',
			    		     		text: 'Buscar',
			    		     		id: PF + 'btnBuscar',
			    		     		hidden: true,
			    		     		x: 830,
			    		     		y: 60,
			    		     		width: 80,
			    		     		height: 22,
			    		     		tabIndex:3,
			    		     		listeners: {
			    		     			click:{
			    		     				fn: function(e){
			    		     					NS.buscar();
			    		     				}
			    		     			}
			    		     		}
			    		     	}
			    		]
				    },
				    {
				    	xtype: 'fieldset',
				    	title: '',
			    		x: 0,
			    		y: 130,
			    		width: 985,
			    		height: 340,
			    		layout: 'absolute',
			    		items: [
			    		        NS.gridCatalogos
			    		]
				    }
				]
			}
		]
	});

	NS.panelPrincipal.setSize(
		Ext.get(NS.tabContId).getWidth(), 
		Ext.get(NS.tabContId).getHeight());
});
