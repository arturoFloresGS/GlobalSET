/*
@author: Jessica Arelly Cruz Cruz
@since: 27/06/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Impresion.Impresion.ImpresionCheques');
	NS.tabContId = apps.SET.tabContainerId;	
	//NS.tabContId = 'contenedorArchivoSeguridad'; 
	var PF = apps.SET.tabID+'.';
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;	
	NS.optGenerar = true;
	NS.empresa = NS.GI_ID_EMPRESA;
	NS.banco = '';
	NS.chequera='';
	NS.fechaIni='';
	NS.fechaFin='';
	NS.importeIni = '';
	NS.importeFin = '';
	NS.todas = false;
	NS.chkH2H = false;
	
	//store empresa
	NS.storeEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ImpresionAction.llenarComboEmpresas, 
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
	
	//combo emopresa
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
        ,x: 105
        ,y: 0
        ,width: 200
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una Empresa'
		,triggerAction: 'all'
		,value: apps.SET.NOM_EMPRESA
		,hidden: true
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresa.getId());
					NS.accionarCmbEmpresa();
				}
			}
		}
	});
	
	NS.txtEmpresaUsuario = new Ext.form.TextField({
		id: PF + 'txtEmpresaUsuario',
		name: PF + 'txtEmpresaUsuario',
		x: 105,
        y: 0,
        width: 200,
        value: apps.SET.NOM_EMPRESA
	});
	
	//store banco
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'distinct id_banco',
			campoDos:'desc_banco',
			tabla:'cat_banco',
			condicion:'b_protegido = \'S\'',
			orden:'desc_banco'
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		directFn: GlobalAction.llenarComboGeneral, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				//cargar el primer parametro del grid 
			 	var datosClase = NS.gridDatos.getStore().recordType;
		    	var datos = new datosClase({
	                	criterio: 'BANCO',
						valor: ''
            	});
               	NS.gridDatos.stopEditing();
           		NS.storeDatos.insert(0, datos);
           		NS.gridDatos.getView().refresh();
			}
		}
	});
	
	//combo banco
	NS.storeBanco.load();
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 50
        ,y: 10
        ,width: 150
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,hidden: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	
	//store chequera
	NS.storeChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			banco: 0,
			empresa: NS.GI_ID_EMPRESA
		},
		root: '',
		paramOrder:['banco','empresa'],
		directFn: ImpresionAction.llenarComboChequeras, 
		idProperty: 'id', 
		fields: [
			 {name: 'idChequera'},
			 {name: 'descChequera'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	});
	
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera
		,name: PF+'cmbChequera'
		,id: PF+'cmbChequera'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 280
        ,y: 10
        ,width: 180
		,valueField:'idChequera'
		,displayField:'descChequera'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,value: ''
		,hidden: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbChequera(combo.getValue());
				}
			}
		}
	});
	
	//data criterios
	NS.dataCriterio = [
					  [ '0', 'FECHAS' ]
				     ,[ '1', 'MONTOS' ]
					  ];
  	NS.storeCriterio = new Ext.data.SimpleStore( {
		idProperty: 'idCriterio',  		
		fields : [ 
			{name :'idCriterio'}, 
			{name :'descripcion'}
	 ]
	});
	NS.storeCriterio.loadData(NS.dataCriterio);
	
	//store del grid criterios
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'criterio',
		fields: [
			{name: 'criterio'},
			{name: 'valor'}
		]
	}); 
	
	//combo criterios
	NS.cmbCriterio = new Ext.form.ComboBox({
		 store: NS.storeCriterio 	
		,name: PF+'cmbCriterio'
		,id: PF+'cmbCriterio'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 50
		,y: 55
		,width:150
		,valueField:'idCriterio'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un criterio'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn:function(combo, value) {
					combo.setDisabled(true);
					var tamGrid=(NS.storeDatos.data.items).length;
					var datosClase = NS.gridDatos.getStore().recordType;
					
					if(combo.getValue() == 0)
					{
						Ext.getCmp(PF+'txtFecha1').show();
						Ext.getCmp(PF+'txtFecha2').show();
						Ext.getCmp(PF+'txtImporte1').hide();
						Ext.getCmp(PF+'txtImporte2').hide();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='FECHAS')
								indice=i;
						}
						
						if(indice>0)
						{
							Ext.Msg.alert('SET','Ya indicó el criterio FECHAS');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'FECHAS',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}
					if(combo.getValue() == 1)
					{
						Ext.getCmp(PF+'txtFecha1').hide();
						Ext.getCmp(PF+'txtFecha2').hide();
						Ext.getCmp(PF+'txtImporte1').show();
						Ext.getCmp(PF+'txtImporte2').show();
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='MONTOS')
								indice=i;
						}
						
						if(indice>0)
						{
							Ext.Msg.alert('SET','Ya indicó el criterio MONTOS');
							combo.setDisabled(false);
							return;
						}
						
						else{
							combo.setDisabled(true);
							var datos = new datosClase({
			                	criterio: 'MONTOS',
								valor: ''
			            	});
	                        NS.gridDatos.stopEditing();
		            		NS.storeDatos.insert(tamGrid, datos);
		            		NS.gridDatos.getView().refresh();
						}
					}
				}
			}
		}
	});
	
	//grid criterios
	NS.gridDatos  = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		id: PF+'gridDatos',
		name: PF+'gridDatos',
		width: 260,
       	height: 105,
		x :500,
		y :0,
		frame: true,
		title :'',
		columns : [ 
		{
			id :'criterio',
			header :'Criterio',
			width :100,
			dataIndex :'criterio'
		}, 
		{
			header :'Valor',
			width :130,
			dataIndex :'valor'
		} ],
		listeners:{
			dblclick:{
				fn:function(grid){
					var records = NS.gridDatos.getSelectionModel().getSelections();
					for(var i=0;i<records.length;i++)
					{
						NS.gridDatos.store.remove(records[i]);
						NS.gridDatos.getView().refresh();
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
					}
				}
			}
		}
	});
	
	
	
	//check
	/*NS.sm = new Ext.grid.CheckboxSelectionModel({
				
	});
	*/
	NS.sm = new Ext.grid.CheckboxSelectionModel
	({
		singleSelect: false
	});
	//store del grid consulta
	NS.storeGridConsulta = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {fechaIni: '',
					fechaFin: '',
					importeIni:'0',
					importeFin:'0',
					banco: '0',
					chequera: '',
					empresa: '0',
					optGenerar: true,
					chkTodas: false
					},
		paramOrder:['fechaIni','fechaFin','importeIni','importeFin','banco','chequera','empresa','optGenerar','chkTodas'],
		directFn: ImpresionAction.consultarDocumentosCheques,
		root: '',
		idProperty: 'criterio',
		fields: [
			{name: 'idBanco'},
			{name: 'idChequera'},
			{name: 'noCheque'},
			{name: 'importe'},
			{name: 'beneficiario'},
			{name: 'fecImprime'},
			{name: 'fecPropuestaStr'},
			{name: 'noFolioDet'},
			{name: 'descBanco'},
			{name: 'concepto'},
			{name: 'plaza'},
			{name: 'poHeaders'},
			{name: 'noEmpresa'},
			{name: 'noDocto'},
			{name: 'idDivisa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridConsulta, msg:"Buscando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No existen datos!');
				}
				else
				{
					var totalImporte=0;
          			//NS.sm.selectRange(0,records.length-1);
          		//	var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
          			
          			for(var k=0;k<records.length;k=k+1)
          			{
          				totalImporte=totalImporte+records[k].get('importe');
          			}
					Ext.getCmp(PF+'txtTotalRegistros').setValue(records.length);
					Ext.getCmp(PF+'txtMontoTotal').setValue(BFwrk.Util.formatNumber(Math.round((totalImporte)*100)/100));
				}
			}
		}
	}); 
	
	NS.gridConsulta = new Ext.grid.GridPanel( {
		store : NS.storeGridConsulta,
		id:'gridConsultaSeguridad',
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
        },	 
		columns : [ 
				NS.sm,
				{header :'Banco', width :100, sortable :true, dataIndex :'descBanco'},
				{header :'No. Empresa', width :100, sortable :true, dataIndex :'noEmpresa', hidden: true}, 
				{
					header :'Poliza Comp.', width :100, sortable :true, dataIndex :'poHeaders', hidden: false 
				},{
					header :'Chequera',	width :130, sortable :true, dataIndex :'idChequera'
				},{
					header :'No. Cheque', width :100, sortable :true, dataIndex :'noCheque'
				},{
					header :'Beneficiario', width :180, sortable :true, dataIndex :'beneficiario'		
				},{
					header :'Importe',	width :100, sortable :true, dataIndex :'importe', css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney		
				},{
					header :'Fecha Cheque', width :120, sortable :true, dataIndex :'fecImprime'		
				},{
					header :'Folio', width :100, sortable :true, dataIndex :'noFolioDet', hidden: true
				},{
					header :'No banco',	width :100, sortable :true, dataIndex :'idBanco', hidden: true
				},{
					header :'Concepto', width :100, sortable :true, dataIndex :'concepto', hidden: true
				},{
					header :'Plaza', width :100, sortable :true, dataIndex :'plaza', hidden: true
				},{
					header :'Documento', width :100, sortable :true, dataIndex :'noDocto', hidden: true
				},{
					header :'Fec. Propuesta', width :100, sortable :true, dataIndex :'fecPropuestaStr', hidden: true
				},{
					header :'Divisa', width :100, sortable :true, dataIndex :'idDivisa', hidden: true
				}
	  		]
	  	})
      	,sm: NS.sm
      	,columnLines: true
      	,x :0
		,y :0		
		,width:760
        ,height:175
        ,frame: true
	  	,listeners:{	
				click:{
	       		   	fn:function(e){	
	       		   		var sum=0;
	          			var regSelec=NS.gridConsulta.getSelectionModel().getSelections();
	          			for(var k=0;k<regSelec.length;k=k+1)
	          			{
	          				sum=sum+regSelec[k].get('importe');
	          			}
	          		 
	          			Ext.getCmp(PF+'txtTotalRegistros').setValue(regSelec.length);
						Ext.getCmp(PF+'txtMontoTotal').setValue(BFwrk.Util.formatNumber(Math.round((sum)*100)/100));
	   				}
				}
			}
		});
	
	/*funciones*/
	
	NS.accionarCmbEmpresa = function(){
		NS.empresa = ''+Ext.getCmp(PF+'txtEmpresa').getValue();	
		NS.storeChequera.baseParams.empresa = parseInt(NS.empresa);
	}
	
	NS.accionarCmbBanco = function(comboValor){
		NS.storeChequera.baseParams.banco = comboValor;
		NS.storeChequera.load();
		var banco = NS.storeBanco.getById(comboValor).get('descripcion');
		var datosClase = NS.gridDatos.getStore().recordType;
		var indice=0;
		var i=0;
		var recordsDatGrid=NS.storeDatos.data.items;
		
		for(i=0;i<recordsDatGrid.length;i++)
		{
			if(recordsDatGrid[i].get('criterio')=='BANCO')
			{
				indice=i;
				NS.storeDatos.remove(recordsDatGrid[indice]);
			}
		}
		var datos = new datosClase({
           	criterio: 'BANCO',
			valor: banco
   		});
        NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(indice, datos);
  		NS.gridDatos.getView().refresh();
	}
	
	NS.accionarCmbChequera = function(comboValor){
		var datosClase = NS.gridDatos.getStore().recordType;
		var indice=0;
		var i=0;
		var recordsDatGrid=NS.storeDatos.data.items;
		
		for(i=0;i<recordsDatGrid.length;i++)
		{
			if(recordsDatGrid[i].get('criterio')=='CHEQUERA')
			{
				indice=i;
				NS.storeDatos.remove(recordsDatGrid[indice]);
			}
		}
		var datos = new datosClase({
           	criterio: 'CHEQUERA',
			valor: comboValor
   		});
        NS.gridDatos.stopEditing();
  		NS.storeDatos.insert(indice, datos);
  		NS.gridDatos.getView().refresh();
	}
	
	NS.limpiarParams = function()
	{
		NS.storeGridConsulta.baseParams.banco ='0';
		NS.storeGridConsulta.baseParams.chequera ='';
		NS.storeGridConsulta.baseParams.empresa ='0';
		NS.storeGridConsulta.baseParams.fechaIni ='';
		NS.storeGridConsulta.baseParams.fechaFin ='';
		NS.storeGridConsulta.baseParams.importeIni ='0';
		NS.storeGridConsulta.baseParams.importeFin ='0';
		//NS.storeGridConsulta.baseParams.optGenerar = 'true';
	}
	
	NS.buscar = function(){
		NS.storeGridConsulta.removeAll();
		NS.limpiarParams();
		var recordsDatosGrid=NS.storeDatos.data.items;
		if(recordsDatosGrid.length>0)
		{
			for(var i=0;i<recordsDatosGrid.length;i=i+1)
			{
				if(recordsDatosGrid[i].get('criterio')==='BANCO')
  				{
  					var valorBanco=recordsDatosGrid[i].get('valor');
  					if(valorBanco!='')
  					{
  						var recordsDataBanco=NS.storeBanco.data.items;
  						for(var j=0;j<recordsDataBanco.length;j=j+1)
  						{
  							if(recordsDataBanco[j].get('descripcion')===valorBanco)
  							{
  								var idBanco=recordsDataBanco[j].get('id');
								NS.storeGridConsulta.baseParams.banco = ''+idBanco;
								NS.banco = ''+idBanco;
   							}
  						}
  					}
  					else{
  						Ext.Msg.alert('SET','Debe agregar un valor a BANCO');
  						return;
  					}
  				}
  				else if(recordsDatosGrid[i].get('criterio')==='CHEQUERA')
 				{
 					var valorChequera=recordsDatosGrid[i].get('valor');
 					if(valorChequera!='')
 					{
 						NS.storeGridConsulta.baseParams.chequera=''+valorChequera;
 						NS.chequera=''+valorChequera;
 					}
 					else{
 						Ext.Msg.alert('SET','Debe agregar un valor a la chequera');
 					}
 				}
   				else if(recordsDatosGrid[i].get('criterio')==='FECHAS')
   				{
   					var valorFecha=recordsDatosGrid[i].get('valor');
   					if(valorFecha!=''){
   						var ini=obtenerValIni(valorFecha);
   						var fin=obtenerValFin(valorFecha);
   						NS.storeGridConsulta.baseParams.fechaIni=''+ini.replace(/^\s*|\s*$/g,"");
   						NS.storeGridConsulta.baseParams.fechaFin=''+fin.replace(/^\s*|\s*$/g,"");
   						NS.fechaIni=''+ini.replace(/^\s*|\s*$/g,"");
   						NS.fechaFin=''+fin.replace(/^\s*|\s*$/g,"");
   						
   					}
   					else{
   						Ext.Msg.alert('SET','Debe agregar un valor a FECHAS');
   						return;
   					}
   				}
   				else if(recordsDatosGrid[i].get('criterio')==='MONTOS')
   				{
   					var valorImporte=recordsDatosGrid[i].get('valor');
   					if(valorImporte!='')
   					{
   						var ini=obtenerValIni(valorImporte);
   						var fin=obtenerValFin(valorImporte);
   						NS.storeGridConsulta.baseParams.importeIni = BFwrk.Util.unformatNumber(''+ini);
						NS.storeGridConsulta.baseParams.importeFin = BFwrk.Util.unformatNumber(''+fin);
						NS.importeIni = BFwrk.Util.unformatNumber(''+ini);
						NS.importeFin = BFwrk.Util.unformatNumber(''+fin);
   					}
   					else
   					{
   						Ext.Msg.alert('SET','Debe agregar un valor a MONTOS');
   						return;
   					}
   				}
			}
			NS.storeGridConsulta.baseParams.empresa = ''+NS.empresa;	
			NS.storeGridConsulta.baseParams.optGenerar = NS.optGenerar;
			NS.storeGridConsulta.load();
		}
	}
	
	NS.enviarRegistros = function(){
		
		Ext.MessageBox.show({
		       title : 'Información SET',
		       msg : 'Generando Documentos, espere por favor...',
		       width : 300,
		       wait : true,
		       progress:true,
		       waitConfig : {interval:200}//,
	   		});
		
		var registros = NS.gridConsulta.getSelectionModel().getSelections();
		var matrizRegistros = new Array ();
		
		for(var i = 0; i < registros.length; i ++)
		{
			var registro = {};
			//registros seleccionados del grid
			registro.descBanco = registros[i].get('descBanco');
			registro.idChequera = registros[i].get('idChequera');
			registro.noCheque = registros[i].get('noCheque');
			registro.beneficiario = registros[i].get('beneficiario');
			registro.importe = registros[i].get('importe');
			registro.fecImprime = registros[i].get('fecImprime');
			registro.fecPropuestaStr = registros[i].get('fecPropuestaStr');
			registro.noFolioDet = registros[i].get('noFolioDet');
			registro.idBanco = registros[i].get('idBanco');
			registro.concepto = registros[i].get('concepto');
			registro.plaza = registros[i].get('plaza');
			registro.divisa = registros[i].get('divisa');
			registro.poHeaders = registros[i].get('poHeaders');
			registro.noEmpresa = registros[i].get('noEmpresa');
			registro.documento = registros[i].get('noDocto');
			matrizRegistros[i] = registro; 
		}
		
		var jsonStringRegistros = Ext.util.JSON.encode(matrizRegistros);
		//alert(jsonStringRegistros);
		ImpresionAction.ejecutarProteccionCheque(jsonStringRegistros, NS.chkH2H, NS.optGenerar, function(result, e){
			if(result != null && result != undefined && result != '') {
				if(result.msgUsuario!==null  &&  result.msgUsuario!=='' && result.msgUsuario!= undefined){
					Ext.Msg.alert('SET',''+result.msgUsuario);
					NS.storeGridConsulta.load();
					
					if(result.resExitoLocal == true){
						NS.abrirTxt(result);
						//NS.imprimeReporte(result.resNombreArchivoLocal);
					}else{
						Ext.Msg.alert('SET', 'Error desconocido.');
					}
					
				}else{
					Ext.Msg.alert('SET', 'Error desconocido..');
				}
				
			}else{
				Ext.Msg.alert('SET', 'Error desconocido...');
			}
		});
	}
	
	NS.abrirTxt = function(archivo) {
		strParams = '?nomReporte=layouts';
		strParams += '&'+'nomParam1=rutaArchivo';
		strParams += '&'+'valParam1='+archivo.resRutaLocal + archivo.resNombreArchivoLocal;
		strParams += '&'+'nomParam2=nombreArchivo';
		strParams += '&'+'valParam2='+archivo.resNombreArchivoLocal;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/abretxt.jsp"+strParams);
		return;
	}
	/*formulario*/
	NS.contenedorArchivoSeguridad = new Ext.FormPanel({
	    title: 'Generaci&#243;n de Archivos de Seguridad',
	    width: 845,
	    height: 567,
	    padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    frame: true,
	    renderTo: NS.tabContId,
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                width: 820,
	                height: 530,
	                x: 10,
	                y: 0,
	                layout: 'absolute',
	                items: [
	                    {
	                        xtype: 'label',
	                        text: 'Empresa:',
	                        x: 0,
	                        y: 5
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Usuario:',
	                        x: 375,
	                        y: 5
	                    },
	                    {
	                        xtype: 'label',
	                        text: 'Fecha:',
	                        x: 635,
	                        y: 5
	                    },
	                    //NS.cmbEmpresa,
	                    NS.txtEmpresaUsuario,
	                    {
	                        xtype: 'textfield',
	                        x: 50,
	                        y: 0,
	                        width: 40,
	                        name: PF+'txtEmpresa',
	                        id: PF+'txtEmpresa',
	                        value: apps.SET.NO_EMPRESA,
	                        listeners:{
	                        	change:{
	                        		fn:function(caja, valor){
	                        			//linea cambia combo
	                        			BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
	                        			NS.accionarCmbEmpresa();
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'checkbox',
	                        boxLabel: 'Todas',
	                        x: 320,
	                        y: 0,
	                        id: PF+'chkTodas',
	                        name: PF+'chkTodas',
	                        listeners:{
	                           	check:{
	                               	fn: function(box){
	                               		if(box.getValue()== true)
	                           			{
	                           				NS.storeGridConsulta.baseParams.chkTodas = true;
				 					 		NS.todas = true;
				 					 	}
						 				else
						 				{
						 					NS.storeGridConsulta.baseParams.chkTodas = false;
						 					NS.todas = false;
						 				}
	                        		}
	                        	}
	                        }
	                    },
	                    {
	                        xtype: 'textfield',
	                        x: 425,
	                        y: 0,
	                        width: 180,
	                        name: 'txtUsuario',
	                        value: apps.SET.NOMBRE+' '+apps.SET.APELLIDO_PATERNO+' '+apps.SET.APELLIDO_MATERNO,
	                        readOnly: true
	                    },
	                    {
	                        xtype: 'datefield',
	                        x: 680,
	                        y: 0,
	                        width: 110,
	                        name: 'txtFecha',
	                        format: 'd/m/Y',
	                        value: apps.SET.FEC_HOY,
	                        readOnly: true
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'B&#250;squeda',
	                        x: 0,
	                        y: 40,
	                        width: 790,
	                        height: 170,
	                        padding: 0,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Banco:',
	                                x: 0,
	                                y: 10
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Criterio:',
	                                x: 0,
	                                y: 55
	                            },
	                            NS.cmbBanco,
	                            NS.cmbCriterio,
	                            NS.cmbChequera,
	                            {
	                                xtype: 'label',
	                                text: 'Chequera:',
	                                x: 220,
	                                y: 10
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Valor:',
	                                x: 220,
	                                y: 55
	                            },
	                            {
	                                xtype: 'datefield',
	                                x: 250,
	                                y: 55,
	                                width: 100,
	                                name: PF+'txtFecha1',
	                                id: PF+'txtFecha1',
	                                format: 'd/m/Y',
	                                hidden: true,
	                                listeners:{
		                        		change:{
		                        			fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='FECHAS')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
												var valorFecha='';
												if(valor!='')
												{
													valorFecha=BFwrk.Util.changeDateToString(''+valor);
												}
										    	var datos = new datosClase({
							                			criterio: 'FECHAS',
														valor: valorFecha
							           			});
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
			                        		}
			                        	}
			                        }
	                            },
	                            {
	                                xtype: 'datefield',
	                                x: 360,
	                                y: 55,
	                                width: 100,
	                                name: PF+'txtFecha2',
	                                id: PF+'txtFecha2',
	                                format: 'd/m/Y',
	                                hidden: true,
	                                listeners:{
	                                	change:{
	                                		fn: function(caja, valor){
	                                			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;	
												var fechaUno=Ext.getCmp(PF+'txtFecha1').getValue();
												var valIni='';
												if(fechaUno!='')
												{
													valIni=BFwrk.Util.changeDateToString(''+fechaUno);
												}
												if(valIni!='')
												{
													valIni+=' a ';
												}			
												Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='FECHAS')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
												var valFin='';
												if(valor!='')
												{
													valFin=BFwrk.Util.changeDateToString(''+valor);
												}
												if(fechaUno>valor)
												{
													Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
													return;
												}
												else
												{
											    	var datos = new datosClase({
								                			criterio: 'FECHAS',
															valor: valIni + valFin
								           			});
								                    NS.gridDatos.stopEditing();
								           			NS.storeDatos.insert(indice, datos);
								           			NS.gridDatos.getView().refresh();
			                   					}
	                                		}
	                                	}
	                                }
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 250,
	                                y: 55,
	                                width: 100,
	                                name: PF+'txtImporte1',
	                                id: PF+'txtImporte1',
	                                hidden: true,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;				
												
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='MONTOS')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
										    	var datos = new datosClase({
							                			criterio: 'MONTOS',
														valor: BFwrk.Util.formatNumber(valor)
							           			});
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
		                        			}
			                        	}
			                        }
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 360,
	                                y: 55,
	                                width: 100,
	                                name: PF+'txtImporte2',
	                                id: PF+'txtImporte2',
	                                hidden: true,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			var indice=0;
												var i=0;
												var datosClase = NS.gridDatos.getStore().recordType;
												var recordsDatGrid=NS.storeDatos.data.items;		
												var valorUno=BFwrk.Util.formatNumber(Ext.getCmp(PF+'txtImporte1').getValue());
												if(valorUno!='')
													valorUno+=' a '
												for(i=0;i<recordsDatGrid.length;i++)
												{
													if(recordsDatGrid[i].get('criterio')=='MONTOS')
													{
														indice=i;
														NS.storeDatos.remove(recordsDatGrid[indice]);
													}
												}
												if(parseInt(valorUno) > parseInt(valor))
												{
													Ext.Msg.alert('SET','El importe dos debe ser mayor o igual al importe uno');
													Ext.getCmp(PF+'txtImporte1').setValue('');
													Ext.getCmp(PF+'txtImporte2').setValue('');
													return;
												}
										    	var datos = new datosClase({
							                			criterio: 'MONTOS',
														valor: valorUno + BFwrk.Util.formatNumber(valor)
							           			});
							                    NS.gridDatos.stopEditing();
							           			NS.storeDatos.insert(indice, datos);
							           			NS.gridDatos.getView().refresh();
							           			
		                   						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.gridDatos,
	                            {
	                                xtype: 'button',
	                                text: 'Buscar',
	                                width: 70,
	                                x: 690,
	                                y: 110,
	                                id: 'btnBuscar',
	                                listeners:{
	                                	click:{
	                                		fn: function(e){
	                                			Ext.getCmp(PF+ 'btnEjecutar').setDisabled(false);
	                                			NS.buscar();
	                                		}
                                		}
                               		}
	                            },
	                            {
                                xtype: 'fieldset',
                                title: '-',
                                x: 0,
                                y: 80,
                                width: 210,
                                height: 55,
                                layout: 'absolute',
                                items: [
	                                {
	                                 	xtype: 'radiogroup',
							            columns: 2,
							            items: [
		                                    {
		                                        xtype: 'radio',
		                                        x: 0,
		                                        y: 0,
		                                        boxLabel: 'Por Generar',
		                                        name: PF+'optGenerar',
		                        				inputValue: 0,
		                        				checked: true,
		                        				listeners:{
		                        					check:{
	                                           			fn:function(opt, valor)
	                                           			{
	                                           				if(valor==true)
	                                           				{
	                                           					NS.optGenerar = true;
	                                           					Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
	                                           				}
	                                           			}
	                                           		}
	                                           	}
		                                    },
		                                    {
		                                        xtype: 'radio',
		                                        x: 70,
		                                        y: 0,
		                                        boxLabel: 'Generados',
		                                        name: PF+'optGenerar',
		                        				inputValue: 1,
		                        				listeners:{
		                        					check:{
	                                           			fn:function(opt, valor)
	                                           			{
	                                           				if(valor==true)
	                                           				{
	                                           					NS.optGenerar = false;
	                                           					Ext.getCmp(PF + 'btnEjecutar').setDisabled(true);
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
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Registros Disponibles',
	                        x: 0,
	                        y: 220,
	                        height: 250,
	                        layout: 'absolute',
	                        width: 790,
	                        items: [
	                        	NS.gridConsulta,
	                            {
	                                xtype: 'label',
	                                text: 'Total de Registros Seleccionados:',
	                                x: 270,
	                                y: 190
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 460,
	                                y: 190,
	                                width: 60,
	                                readOnly: true,
	                                name: PF+'txtTotalRegistros',
	                                id: PF+'txtTotalRegistros'
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Monto Total:',
	                                x: 560,
	                                y: 190
	                            },
	                            {
	                                xtype: 'textfield',
	                                x: 640,
	                                y: 190,
	                                width: 100,
	                                readOnly: true,
	                                name: PF+'txtMontoTotal',
	                                id: PF+'txtMontoTotal'
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Ejecutar',
	                        x: 630,
	                        y: 480,
	                        width: 70,
	                        id: PF+ 'btnEjecutar',
	                        name: PF+ 'btnEjecutar',
	                        disabled: true,
	                        listeners:{
		                  		click:{
		              			    fn:function(e){
		              			   		var registros = NS.gridConsulta.getSelectionModel().getSelections();
		              			   		if(registros.length != 0)
	                        			{
		              			    		NS.enviarRegistros();
	              			    		}
	              			    		else
	                       				{
	                       					Ext.Msg.alert('SET','Debe seleccionar al menos un registro');
	                       					return;
	                       				}
		              			    }
              				    }
              			    }
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 720,
	                        y: 480,
	                        width: 70,
	                        id: 'btnLimpiar',
	                        listeners:{
		                  		click:{
		              			    fn:function(e){
		              			    Ext.getCmp(PF+ 'btnEjecutar').setDisabled(true);
		              			   	NS.contenedorArchivoSeguridad.getForm().reset();
		              			   	NS.storeDatos.removeAll();
		              			   	NS.gridDatos.getView().refresh();
		              			   	NS.storeGridConsulta.removeAll();
		              			   	NS.gridConsulta.getView().refresh();
		              			   	//cargar el primer parametro del grid 
								 	var datosClase = NS.gridDatos.getStore().recordType;
							    	var datos = new datosClase({
						                	criterio: 'BANCO',
											valor: ''
					            	});
					               	NS.gridDatos.stopEditing();
					           		NS.storeDatos.insert(0, datos);
					           		NS.gridDatos.getView().refresh();
		              			   }
		           			   }
		       			   	}
	                    }, {
	                        xtype: 'checkbox',
	                        boxLabel: 'H2H',
	                        x: 300,
	                        y: 480,	
	                        width: 70,
	                        id: PF + 'chkH2H',
	                        name: PF + 'chkH2H',
	                        listeners:
	                        {
	                    	check:
                           	{
                               	fn: function(box)
                               	{
                               		if(box.getValue()== true)
                           			{
                           				NS.chkH2H = true;				 					 		
			 					 	}
					 				else
					 				{
					 					NS.chkH2H = false;						 					
					 				}
                               	}
                        	}
                        }
                    }
	                ]
	            }
	        ]
    });
    function obtenerValIni(valor)
	{
		var i=0;
		var valIni='';
		while(valor.charAt(i)!='a')
		{
			valIni+=valor.charAt(i);
			i++;
		}
		valIni.replace(",","");
		return valIni;
	}
	
	function obtenerValFin(valor)
	{
		var i=0;
		var valFin='';
		while(i<valor.length)
		{
			if(valor.charAt(i)=='a')
			{
				valFin=valor.substring(i+1,valor.length);
			}
			i++;
		}
		valFin.replace(",","");
		return valFin;
		
	}
    
    NS.contenedorArchivoSeguridad.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
    staticCheck("#gridConsultaSeguridad div div[class='x-panel-ml']","#gridConsultaSeguridad div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
});
