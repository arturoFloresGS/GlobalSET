/*
@author: Jessica Arelly Cruz Cruz
@since: 08/11/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.ConciliacionBancoSet.ReporteDetalle');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	NS.nombreEmpresa = apps.SET.NOM_EMPRESA;
	NS.iIdUsuario = apps.SET.iUserId ;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.subtitulo = '';
	NS.dFechaIni = '';
	NS.dFechaFin = '';
	NS.bTodas = false;
	
	NS.checkboxes = new Ext.form.CheckboxGroup({ 
		id: PF+'checkboxes',
		name: PF+'checkboxes', 
		itemCls: 'x-check-group-alt',
		xtype: 'checkboxgroup',
	    columns:2,
	    x: 0,
	    y: 0,
	    items:[  
	    	{boxLabel: 'Movimientos con Aclaraci\u00f3n', name: PF+'chkAclaracion', id: PF+'chkAclaracion'},
            {boxLabel: 'Movimientos Pendientes', name: PF+'chkPendientes', id: PF+'chkPendientes'},
            {boxLabel: 'Conciliados Autom\u00e1ticamente', name: PF+'chkAutomaticos', id: PF+'chkAutomaticos'},
            {boxLabel: 'Ajustes', name: PF+'chkAjustes', id: PF+'chkAjustes'},
            {boxLabel: 'Conciliados Manualmente y Agrupados', name: PF+'chkAgrupados', id: PF+'chkAgrupados'},
            {boxLabel: 'Cargas Iniciales (Registros que no estan en SET)', name: PF+'chkIniciales', id: PF+'chkIniciales'},
            {boxLabel: 'Movimientos Detectados', name: PF+'chkDetectados', id: PF+'chkDetectados'},
            {boxLabel: 'Imprimir todas las opciones anteriores', name: PF+'chkTodas', id: PF+'chkTodas'}
	    ]  
	}); 
	
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:[],
		directFn: GlobalAction.llenarComboEmpresasUsuario,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				/*var recordsEmpresas = NS.storeCmbEmpresa.recordType;	
				var todas = new recordsEmpresas({
			       	id : 0,
			       	descripcion : '----------------------TODAS----------------------'
		       	});
		   		NS.storeCmbEmpresa.insert(0,todas);*/
				Ext.getCmp(PF + 'txtEmpresa').setValue('');
				Ext.getCmp(PF + 'cmbEmpresa').setValue('');
			}
		}
	}); 
	
	//combo
	NS.storeCmbEmpresa.load();
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeCmbEmpresa
		,name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 140
        ,y: 10
        ,width: 280
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
//		,value: NS.nombreEmpresa
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresa.getId());
					NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	
	NS.storeCmbBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{empresa: ''+NS.noEmpresa},
		paramOrder:['empresa'],
		directFn: ConciliacionBancoSetAction.llenarComboBancosEmpresas,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//NS.storeCmbBanco.load();
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeCmbBanco
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 60
        ,width: 160
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	
	NS.selectionModel = new Ext.grid.CheckboxSelectionModel({
		checkOnly: true 
	});
	
	NS.storeGridChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			iBanco: 0,
			sEmpresas: ''+NS.noEmpresa
		},
		root: '',
		paramOrder:['sEmpresas','iBanco'],
		directFn: ConciliacionBancoSetAction.llenarGridChequerasBanco, 
		fields: [
			{name: 'idChequera'},
			{name: 'noEmpresa'},
			{name: 'descPlaza'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGridChequeras, msg:"Buscando..."});
				if(records.length == 0)
				{
					BFwrk.Util.msgShow('No se encontraron chequeras','INFO');
					return;
				}
				//NS.selectionModel.selectRange(0,records.length-1);
			}
		}
	}); 
	
	NS.gridChequeras  = new Ext.grid.GridPanel({
		store : NS.storeGridChequeras,
		id: PF+'gridChequeras',
		name: PF+'gridChequeras',
		title: 'Chequeras',
       	height: 160,
		x :0,
		y :120,
		frame: true,
		cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
		columns : [ NS.selectionModel,
		{
			header :'Chequera',width :120,dataIndex :'idChequera'
		},{
			header :'No Empresa',width :80,dataIndex :'noEmpresa', hidden: true
		},{
			header :'Empresa',width :250,dataIndex :'descPlaza'
		}]}),
		sm: NS.selectionModel,
		listeners:{
			dblclick:{
				fn:function(grid){
					
				}
			}
		}
	});
		
	NS.contenedorReporteDetalle = new Ext.FormPanel({
	    title: 'Reporte Detalle',
	    width: 491,
	    height: 538,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            {
	                xtype: 'fieldset',
	                title: '',
	                x: 0,
	                y: 0,
	                layout: 'absolute',
	                width: 480,
	                height: 500,
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 1,
	                        layout: 'absolute',
	                        width: 458,
	                        height: 113,
	                        items: [
	                            {
	                                xtype: 'label',
	                                text: 'Empresa:',
	                                x: 10,
	                                y: 10
	                            },
	                            {
                                xtype: 'textfield',
                                id: PF+'txtEmpresa',
                                name: PF+'txtEmpresa',
//                                value: NS.noEmpresa,
                                x: 70,
                                y: 10,
                                width: 50,
                                listeners:{
		                        	change:{
		                        		fn:function(caja, valor){
		                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
		                        			NS.accionarCmbEmpresa(comboValue);
		                        		}
		                        	}
		                        }
                            },
	                            NS.cmbEmpresa,
	                            NS.cmbBanco,
	                            {
	                                xtype: 'label',
	                                text: 'Banco:',
	                                x: 10,
	                                y: 40
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Fecha Inicial:',
	                                x: 190,
	                                y: 40
	                            },
	                            {
	                                xtype: 'datefield',
	                                id: PF+'txtFechaIni',
	                                name:PF+'txtFechaIni',
	                                format: 'd/m/Y',
	                                value: apps.SET.FEC_HOY, //'01/' + NS.fecHoy.getMonth() + 1 + '/' + NS.fecHoy.getFullYear(),
	                                x: 190,
	                                y: 60,
	                                width: 110
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Fecha Final:',
	                                x: 321,
	                                y: 40
	                            },
	                            {
	                                xtype: 'datefield',
	                                id: PF+'txtFechaFin',
	                                name:PF+'txtFechaFin',
	                                format: 'd/m/Y',
	                                value: apps.SET.FEC_HOY,
	                                x: 317,
	                                y: 60,
	                                width: 110
	                            }
	                        ]
	                    },
	                    NS.gridChequeras,
	                    
	                    {
	                        xtype: 'fieldset',
	                        title: 'Tipo de Reporte',
	                        x: 0,
	                        y: 291,
	                        layout: 'absolute',
	                        height: 160,
	                        items: [
	                        	NS.checkboxes
	                        ]
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Imprimir',
	                        x: 309,
	                        y: 454,
	                        width: 70,
	                        listeners:{
				           		click:{
				           			fn:function(){
				           				var regSelec=NS.gridChequeras.getSelectionModel().getSelections();
				           				var checks = Ext.getCmp(PF+'checkboxes').getValue();
				           				
				           				if(Ext.getCmp(PF+'cmbEmpresa').getValue() == '')
			           					{
			           						BFwrk.Util.msgShow('Debe seleccionar la empresa','WARNING');
			           						return;
			           					}

				           				if(Ext.getCmp(PF+'txtFechaIni').getValue() > Ext.getCmp(PF+'txtFechaFin').getValue())
			           					{
			           						BFwrk.Util.msgShow('La fecha inicial debe se menor que la fecha final','WARNING');
			           						return;
			           					}
				           				
				           				if(Ext.getCmp(PF+'txtFechaIni').getValue() !== '')
				           					NS.dFechaIni = BFwrk.Util.changeDateToString(''+Ext.getCmp(PF+'txtFechaIni').getValue());
				           				else
				           					NS.dFechaIni = '01/01/1980';
									    
									  	if(Ext.getCmp(PF+'txtFechaFin').getValue() !== '')
				           					NS.dFechaFin = BFwrk.Util.changeDateToString(''+Ext.getCmp(PF+'txtFechaFin').getValue());
				           				else
				           					NS.dFechaFin = BFwrk.Util.sumDate(BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY),'anio',-1);
			           					
			           					if(Ext.getCmp(PF+'cmbBanco').getValue() === '')
			           					{
			           						BFwrk.Util.msgShow('Debe seleccionar un banco','INFO');
			           						return;
			           					}	
			           					
			           					if(regSelec.length <= 0)
				           				{
				           					BFwrk.Util.msgShow('Es necesario seleccionar algun registro','INFO');
				           					return;
				           				}
				           				
		              			   		if(checks == '')
		              			   		{
		              			   			BFwrk.Util.msgShow('Es necesario seleccionar un tipo de reporte', 'INFO');
		              			   			return;
	              			   			}
	              			   			
	              			   			if(Ext.getCmp(PF + 'chkTodas').getValue() === true)
	              			   			{
						        			for(var i = 0; i < checks.length - 1; i ++){
						        				Ext.getCmp(checks[i].getName()).setValue(false);
						        			}
	              			   			}
	              			   			NS.escribeSubtitulo();
	              			   			NS.buscarReporte();
				           			}
			           			}
		           			}
	                    },
	                    {
	                        xtype: 'button',
	                        text: 'Limpiar',
	                        x: 388,
	                        y: 454,
	                        width: 70,
	                        listeners:{
				           		click:{
				           			fn:function(){
				           				NS.contenedorReporteDetalle.getForm().reset();
				           				NS.storeGridChequeras.removeAll();
										NS.gridChequeras.getView().refresh();
				           			}
			           			}
		           			}
	                    }
	                ]
	            }
	        ]
	});
	
	NS.buscarReporte = function()
	{
		var registrosGrid=NS.gridChequeras.getSelectionModel().getSelections();
		for(var j = 0; j < registrosGrid.length; j ++)
		{
			if(Ext.getCmp(PF + 'chkAclaracion').getValue() === true)
			{
				NS.enviaReporte('ReporteMovimientosAclarados','ACLARADO', j);
			}
			
			if(Ext.getCmp(PF + 'chkAgrupados').getValue() === true)
			{
				NS.enviaReporte('ReporteMovimientosAclarados','CONCILIADO MANUAL', j);
			}
			
			if(Ext.getCmp(PF + 'chkAutomaticos').getValue() === true)
			{
				NS.enviaReporte('ReporteConciliadosAutomaticamente','CONCILIADO AUTOMATICO', j);
			}
			
			if(Ext.getCmp(PF + 'chkDetectados').getValue() === true)
			{
				NS.enviaReporte('ReporteMovimientosDetectadosyPendientes','DETECTADOS', j);
			}
			
			if(Ext.getCmp(PF + 'chkPendientes').getValue() === true)
			{
				NS.enviaReporte('ReporteMovimientosDetectadosyPendientes','PENDIENTE POR CONCILIAR', j);
			}
			
			if(Ext.getCmp(PF + 'chkAjustes').getValue() === true)
			{
				NS.enviaReporte('ReporteMovimientosAjustados','MOVIMIENTOS DE AJUSTE', j);
			}
			
			if(Ext.getCmp(PF + 'chkIniciales').getValue() === true)
			{
				NS.enviaReporte('ReporteCargaInicial','CARGA INICIAL', j);
			}
			
			else if(Ext.getCmp(PF + 'chkTodas').getValue() === true)
			{
				NS.enviaReporte('ReporteMovimientosAclarados','ACLARADO', j);
				NS.enviaReporte('ReporteMovimientosAclarados','CONCILIADO MANUAL', j);
				NS.enviaReporte('ReporteConciliadosAutomaticamente','CONCILIADO AUTOMATICO', j);
				NS.enviaReporte('ReporteMovimientosDetectadosyPendientes','DETECTADOS', j);
				NS.enviaReporte('ReporteMovimientosDetectadosyPendientes','PENDIENTE POR CONCILIAR', j);
				NS.enviaReporte('ReporteMovimientosAjustados','MOVIMIENTOS DE AJUSTE', j);
				NS.enviaReporte('ReporteCargaInicial','CARGA INICIAL', j);
			}
		}
	}
	
	NS.enviaReporte = function(nombreReporte, estatus, indice)
	{
		var registrosGrid=NS.gridChequeras.getSelectionModel().getSelections();
		var matrizGrid = new Array ();
//		alert("Índice JC "+ indice);
//		for(var i = 0; i < registrosGrid.length; i ++)
//		{
			var regGrid = {};
			regGrid.idChequera = registrosGrid[indice].get('idChequera');
			regGrid.noEmpresa = registrosGrid[indice].get('noEmpresa');
			regGrid.nomEmpresa = registrosGrid[indice].get('descPlaza');
			matrizGrid[0] = regGrid; 
//		}
		var jsonStringR = Ext.util.JSON.encode(matrizGrid);
		
		strParams = '?nomReporte='+nombreReporte;
		strParams += '&'+'nomParam1=iIdBanco';
		strParams += '&'+'valParam1='+NS.idBanco;
		strParams += '&'+'nomParam2=dFechaIni';
		strParams += '&'+'valParam2='+NS.dFechaIni;
		strParams += '&'+'nomParam3=dFechaFin';
		strParams += '&'+'valParam3='+NS.dFechaFin;
		strParams += '&'+'nomParam4=aDatosGrid';
		strParams += '&'+'valParam4='+jsonStringR;
		strParams += '&'+'nomParam5=iIdUsuario';
		strParams += '&'+'valParam5='+NS.iIdUsuario;
		strParams += '&'+'nomParam6=sDescBanco';
		strParams += '&'+'valParam6='+$('input[id*="'+ NS.cmbBanco.getId() +'"]').val();	
		strParams += '&'+'nomParam7=sSubtitulo';
		strParams += '&'+'valParam7='+NS.subtitulo;	
		strParams += '&'+'nomParam8=nombreEmpresa';
		strParams += '&'+'valParam8='+registrosGrid[indice].get('descPlaza');
		strParams += '&'+'nomParam9=idChequera';
		strParams += '&'+'valParam9='+registrosGrid[indice].get('idChequera');
		strParams += '&'+'nomParam10=estatus';
		strParams += '&'+'valParam10='+estatus.trim();
		
//		alert("La ventana es: "+ strParams);
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp"+strParams);
		return;
	}
	
	NS.escribeSubtitulo = function()
	{
		if(Ext.getCmp(PF + 'txtFechaIni').getValue() !== '')
		{
			//el usuario especifico ambas fechas
			if(Ext.getCmp(PF + 'txtFechaFin').getValue() !== '')
				NS.subtitulo = 'CONCILIACION BANCO SET DEL ' + NS.dFechaIni + ' AL ' + NS.dFechaFin;
			else //el usuario no especifico la fecha final, pero si la fecha inicial
				NS.subtitulo = 'CONCILIACION BANCO SET DESDE EL  ' + NS.dFechaIni;
		}
		else if(Ext.getCmp(PF + 'txtFechaFin').getValue() !== '')
		{
			//el usuario no especifico la fecha inicial, pero si la fecha final
			NS.subtitulo = 'CONCILIACION BANCO SET HASTA EL ' + NS.dFechaFin;
		}
		else
		{
			//el usuario no especifico ninguna fecha
			NS.subtitulo = 'CONCILIACION BANCO SET';
		}
	}
	
	NS.accionarCmbEmpresa = function(comboValor)
	{
		NS.storeCmbBanco.baseParams.empresa = ''+comboValor;
		NS.storeCmbBanco.load();
		NS.storeGridChequeras.baseParams.sEmpresas = ''+comboValor;
		NS.noEmpresa = comboValor;
		NS.storeGridChequeras.removeAll();
		NS.gridChequeras.getView().refresh();
		NS.cmbBanco.reset();
		if(comboValor == 0)
			NS.bTodas = true;
	}
	
	NS.accionarCmbBanco = function(comboValor)
	{
		NS.storeGridChequeras.baseParams.iBanco = comboValor;
		NS.storeGridChequeras.load();
		NS.idBanco = comboValor;
	}
	
	NS.contenedorReporteDetalle.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
