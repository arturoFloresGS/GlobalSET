/* 
 * @author: Eric Medina Serrato
 * @since: 04/Abril/2016
 */
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	var NS = Ext.namespace('apps.SET.Consultas.PosicionBancaria.js');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.fecHoy = apps.SET.FEC_HOY;
	
	NS.optBusqueda = 0;
	NS.jsonBancos = '';
	NS.jsonChequeras = '';
	NS.jsonEmpresas = '';
	NS.jsonOrden = '';
	NS.fElimina = 0;
	NS.fActual = 0;
	NS.datos1 = '';
	NS.datos2 = '';
	NS.datosOrden = '';
	NS.cargaPlantilla = false;
	
	//Para plantillas
	NS.consulta = ''; //Guarda el nombre de la consulta ejecutada.
	NS.orden = 1;
	
	//PARA CONSULTAS DINAMICAS.
	NS.fields = [];
	NS.columns = [];
	NS.tipoReporte = 0;
	NS.storeLlenaGrid = new Ext.data.DirectStore({autoLoad : false});
	NS.storeLlenaGridCash = new Ext.data.DirectStore({autoLoad : false});
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
	
	verificaComponentesCreados();
	
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
	
	NS.storeConsultarCriterioSeleccion = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {idCriterio: 0},
		root: '',
		paramOrder: ['idCriterio'],
		directFn: PosicionBancariaAction.consultarCriterioSeleccion,
		//idProperty: 'idPlan',
		fields: [
			{name: 'idBanco' },
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'descChequera'},
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'orden'}
		],
		listeners: {
			load:{ fn: function(s, records){
				
					var opt = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
					console.log("criterio seleccionado"+opt);
					switch(opt){
						case 1:
							NS.gridSelecciones.reconfigure(NS.storeConsultarCriterioSeleccion, NS.colSelChequeras);
							
							if(NS.cargaPlantilla){
								
								var chequeras = NS.datos2.split(',');
								var vecOrden = NS.datosOrden.split(',');
								var renglones = NS.storeConsultarCriterioSeleccion.data.items;
								
								var contAux = 0;
								for(var i=0; i<renglones.length; i++){
									if(chequeras.indexOf(renglones[i].get('idChequera')) >= 0){
										NS.selSelecciones.selectRow(i,true,false);
										renglones[i].set('orden', vecOrden[i]);
										if(++contAux == chequeras.length){
											break;
										}
									}
								}
								
								NS.cargaPlantilla = false;
								NS.orden = contAux; 
									
								//Realizo la consulta para desplegar el detalle.
								if(NS.consulta == 'MovtoTes'){
									NS.clickMovtoTes();
								}else if(NS.consulta == 'diarioTeso'){
									NS.clickDiarioTeso();
								}else if(NS.consulta == 'fecValorXEmp'){
									NS.clickFecValorXEmp();
								}else if(NS.consulta == 'fecValorXCta'){
									NS.clickFecValorXCta();
								}else if(NS.consulta == 'repSaldos'){
									NS.clickRepSaldos();
								}
									
							}else{
								mascara.hide();
							}
							//NS.gridSelecciones.bindStore(NS.storeConsultarCriterioSeleccion, true);
							break;
						case 2:
							NS.gridSelecciones.reconfigure(NS.storeConsultarCriterioSeleccion, NS.colSelBancosEmpresas);
							
							if(NS.cargaPlantilla){
								
								var bancos = NS.datos1.split(',');
								var empresas = NS.datos2.split(',');
								var vecOrden = NS.datosOrden.split(',');
								var renglones = NS.storeConsultarCriterioSeleccion.data.items;
								
								var contAux = 0;
								for(var i=0; i<renglones.length; i++){
									if(bancos.indexOf(''+renglones[i].get('idBanco')) >= 0
										&& empresas.indexOf(''+renglones[i].get('noEmpresa')) >= 0){
										
										NS.selSelecciones.selectRow(i,true,false);
										renglones[i].set('orden', vecOrden[i]);
										if(++contAux == empresas.length){
											break;
										}
									}
								}
								
								NS.cargaPlantilla = false;
								NS.orden = contAux; 
								
								//Realizo la consulta para desplegar el detalle.
								if(NS.consulta == 'MovtoTes'){
									NS.clickMovtoTes();
								}else if(NS.consulta == 'diarioTeso'){
									NS.clickDiarioTeso();
								}else if(NS.consulta == 'fecValorXEmp'){
									NS.clickFecValorXEmp();
								}else if(NS.consulta == 'fecValorXCta'){
									NS.clickFecValorXCta();
								}else if(NS.consulta == 'repSaldos'){
									NS.clickRepSaldos();
								}
									
							}else{
								mascara.hide();
							}

							//NS.gridSelecciones.bindStore(NS.storeConsultarCriterioSeleccion, true);
							break;
						case 3:
							NS.gridSelecciones.reconfigure(NS.storeConsultarCriterioSeleccion, NS.colSelBancos);
							
							if(NS.cargaPlantilla){
								
								var bancos = NS.datos1.split(',');
								var vecOrden = NS.datosOrden.split(',');
								var renglones = NS.storeConsultarCriterioSeleccion.data.items;
								
								var contAux = 0;
								for(var i=0; i<renglones.length; i++){
									if(bancos.indexOf(''+renglones[i].get('idBanco')) >= 0){
										NS.selSelecciones.selectRow(i,true,false);
										renglones[i].set('orden', vecOrden[i]);
										if(++contAux == bancos.length){
											break;
										}
									}
								}
								
								NS.cargaPlantilla = false;
								NS.orden = contAux; 
								
								//Realizo la consulta para desplegar el detalle.
								if(NS.consulta == 'MovtoTes'){
									NS.clickMovtoTes();
								}else if(NS.consulta == 'diarioTeso'){
									NS.clickDiarioTeso();
								}else if(NS.consulta == 'fecValorXEmp'){
									NS.clickFecValorXEmp();
								}else if(NS.consulta == 'fecValorXCta'){
									NS.clickFecValorXCta();
								}else if(NS.consulta == 'repSaldos'){
									NS.clickRepSaldos();
								}
									
							}else{
								mascara.hide();
							}
							//NS.gridSelecciones.bindStore(NS.storeConsultarCriterioSeleccion, true);
							break;
						case 4:
							NS.gridSelecciones.reconfigure(NS.storeConsultarCriterioSeleccion, NS.colSelEmpresas);
							
							if(NS.cargaPlantilla){
								
								var empresas = NS.datos1.split(',');
								var vecOrden = NS.datosOrden.split(',');
								var renglones = NS.storeConsultarCriterioSeleccion.data.items;
								
								var contAux = 0;
								for(var i=0; i<renglones.length; i++){
									if(empresas.indexOf(''+renglones[i].get('noEmpresa')) >= 0){
										NS.selSelecciones.selectRow(i,true,false);
										renglones[i].set('orden', vecOrden[i]);
										if(++contAux == empresas.length){
											break;
										}
									}
								}
								
								NS.cargaPlantilla = false;
								NS.orden = contAux; 
								
								//Realizo la consulta para desplegar el detalle.
								if(NS.consulta == 'MovtoTes'){
									NS.clickMovtoTes();
								}else if(NS.consulta == 'diarioTeso'){
									NS.clickDiarioTeso();
								}else if(NS.consulta == 'fecValorXEmp'){
									NS.clickFecValorXEmp();
								}else if(NS.consulta == 'fecValorXCta'){
									NS.clickFecValorXCta();
								}else if(NS.consulta == 'repSaldos'){
									NS.clickRepSaldos();
								}
									
							}else{
								mascara.hide();
							}
							//NS.gridSelecciones.bindStore(NS.storeConsultarCriterioSeleccion, true);
							break;	
					}
					
					mascara.hide();
				}
			}
		}
	});
	
 	NS.selSelecciones = new Ext.grid.CheckboxSelectionModel({
 		singleSelect: false,
 		listeners: {
			rowselect:{ fn:
				function(chkSelectionModel, rowIndex, record ){
				
					if(!NS.cargaPlantilla){
						var noOrdenTmp = record.get('orden');
						
						if(noOrdenTmp == '' || noOrdenTmp == null || noOrdenTmp == undefined){
							record.set('orden', NS.orden);
							NS.orden++;
						}
					}
				}
			},
			rowdeselect:{ fn:
				function(chkSelectionModel, rowIndex, record ){
					var ordenTmp = record.get('orden');
					NS.fElimina = ordenTmp;
					//registro deseleccionado se limpia el campo.
					record.set('orden', '');
					
					ordenTmp = '';
					NS.orden--;
					
					if(NS.fElimina > 0){
						
						var sel = NS.gridSelecciones.getSelectionModel().getSelections();
						
	          			for(var i=0; i<sel.length; i++){
	          				ordenTmp = sel[i].get('orden');
	          				
	          				if( ordenTmp != '' && ordenTmp != null && ordenTmp != undefined ){
	          				
	          					if(ordenTmp > NS.fElimina){
	          						sel[i].set('orden', ordenTmp -1);
	          					}
	          				}
	          			}
					} 
				}
			}
 		}//fin listeners
 	});
 	
	NS.colSelChequeras = new Ext.grid.ColumnModel([	
	  	NS.selSelecciones,
	  	{header :'ID BANCO', width :80, sortable :true, dataIndex :'idBanco' },
		{header :'DESC BANCO', width :100, sortable :true, dataIndex :'descBanco' },
		{header :'ID_CHEQUERA', width :200,sortable :true, dataIndex :'idChequera' },
		{header :'DESC CHEQUERA', width :200, sortable :true, dataIndex :'descChequera' },
		{header :'NO EMPRESA', width :80, sortable :true, dataIndex :'noEmpresa' },
		{header :'EMPRESA', width :200, sortable :true, dataIndex :'nomEmpresa' },
		{header :'ORDEN', width :50, sortable :true, dataIndex :'orden' }
	]);
	
	//NS.selSelBancosEmpresas = new Ext.grid.CheckboxSelectionModel({
	
	NS.colSelBancosEmpresas = new Ext.grid.ColumnModel([	
	  	 NS.selSelecciones, 
	  	{header :'ID BANCO', width :80, sortable :true, dataIndex :'idBanco' },
		{header :'DESC BANCO', width :100, sortable :true, dataIndex :'descBanco' },
		{header :'NO EMPRESA', width :80, sortable :true, dataIndex :'noEmpresa' },
		{header :'EMPRESA', width :200, sortable :true, dataIndex :'nomEmpresa' },
		{header :'ORDEN', width :50, sortable :true, dataIndex :'orden' }
	]);
	
	NS.colSelBancos = new Ext.grid.ColumnModel([	
	  	NS.selSelecciones,
	  	{header :'ID BANCO', width :80, sortable :true, dataIndex :'idBanco' },
		{header :'DESC BANCO', width :100, sortable :true, dataIndex :'descBanco' },
		{header :'ORDEN', width :50, sortable :true, dataIndex :'orden' }
	]);
 	
	NS.colSelEmpresas = new Ext.grid.ColumnModel([	
	  	NS.selSelecciones,
		{header :'NO EMPRESA', width :80, sortable :true, dataIndex :'noEmpresa' },
		{header :'EMPRESA', width :200, sortable :true, dataIndex :'nomEmpresa' },
		{header :'ORDEN', width :50, sortable :true, dataIndex :'orden' }
	]);
	
	mascara.show();
	NS.storeConsultarCriterioSeleccion.baseParams.idCriterio = 1;
	NS.storeConsultarCriterioSeleccion.load();
	
	NS.gridSelecciones = new Ext.grid.GridPanel({
        store : NS.storeConsultarCriterioSeleccion,
        viewConfig: {emptyText: ''},
        x:170,
        y:10,
        cm: NS.colSelChequeras,
		sm: NS.selSelecciones,
        width:510,
        height:205,
        frame:true,
        listeners: {
        	click: {
        		fn:function(e) {
        			
        		}
        	}
        }
    });
	
	NS.optCriterioBusqueda = new Ext.form.RadioGroup({
		id: PF + 'optCriterioBusqueda',
		name: PF + 'optCriterioBusqueda',
		x: 0,
		y: 0,
		columns: 1, 
		items: [
	          {boxLabel: 'Cuentas', id: PF + 'optCuentas', name: PF + 'optCriterioBusqueda', inputValue: 1, checked:true },  
	          {boxLabel: 'Banco/Empresas', id: PF + 'optBancoEmpresas', name: PF + 'optCriterioBusqueda', inputValue: 2},
	          {boxLabel: 'Bancos', id: PF + 'optBanco', name: PF + 'optCriterioBusqueda', inputValue: 3},
	          {boxLabel: 'Empresas', id: PF + 'optEmpresas', name: PF + 'optCriterioBusqueda', inputValue: 4}
	     ],
	     listeners:{
				change:{
					fn:function(){
			
						var opcion = NS.optCriterioBusqueda.getValue();
						
						if(opcion == null || opcion == undefined ){
							return;
						}else{
							opcion = NS.optCriterioBusqueda.getValue().getGroupValue();
							
							switch(parseInt(opcion)){
							
								//En estos stores cuando se carguen se debe reconfigurar el grid principal.
								case 1:
									mascara.show();
									NS.orden = 1;
									NS.storeConsultarCriterioSeleccion.baseParams.idCriterio = 1;
									NS.storeConsultarCriterioSeleccion.load();
									break;
								case 2:
									mascara.show();
									NS.orden = 1;
									NS.storeConsultarCriterioSeleccion.baseParams.idCriterio = 2;
									NS.storeConsultarCriterioSeleccion.load();
									break;
								case 3:
									mascara.show();
									NS.orden = 1;
									NS.storeConsultarCriterioSeleccion.baseParams.idCriterio = 3;
									NS.storeConsultarCriterioSeleccion.load();
									break;
								case 4:
									mascara.show();
									NS.orden = 1;
									NS.storeConsultarCriterioSeleccion.baseParams.idCriterio = 4;
									NS.storeConsultarCriterioSeleccion.load();
									break;	
							}
						}
						
					}
				}
			} // End listeners	
	});

	NS.fldCriterioBusqueda = new Ext.form.FieldSet ({
		title: 'Selección por:',
		x: 5,
		y: 5,
		width: 150,		
		height: 150,
		layout: 'absolute',
		items:
			 [
			 	NS.optCriterioBusqueda
			 ]
	});
	
	NS.fldBotonesBusqueda = new Ext.form.FieldSet ({
		title: 'Buscar por:',
		x: 700,
		y: 110,
		width: 110,		
		height: 100,
		layout: 'absolute',
		items:
			 [
				{
					xtype: 'button',
					text: 'Rubro',
					x: 5,
					y: 0,
					width: 80,
					height: 22,
					id: PF+'btnBusquedaRubro',
					name: PF+'btnBusquedaRubro',
					listeners:{
						click:{
							fn:function(e){
								NS.optBusqueda = 1;
								NS.winBusqueda.show();
							}
						}
					}
				},{
					xtype: 'button',
					text: 'Importe',
					x: 5,
					y: 40,
					width: 80,
					height: 22,
					id: PF+'btnBusquedaImporte',
					name: PF+'btnBusquedaImporte',
					listeners:{
						click:{
							fn:function(e){
								NS.optBusqueda = 2;
								NS.winBusqueda.show();
							}
						}
					}
				}	
			 ]
	});
	
	NS.fldBotonesReportes = new Ext.form.FieldSet ({
		title: 'Reporte:',
		x: 820,
		y: 110,
		width: 110,		
		height: 100,
		layout: 'absolute',
		items:
			 [
				{
					xtype: 'button',
					text: 'Saldos',
					x: 5,
					y: 0,
					width: 80,
					height: 22,
					id: PF+'btnRepSaldos',
					name: PF+'btnRepSaldos',
					listeners:{
						click:{
							fn:function(e){
								NS.clickRepSaldos();
							}
						}
					}
				},
				{
					xtype: 'button',
					text: 'Excel',
					x: 5,
					y: 40,
					width: 80,
					height: 22,
					id: PF+'btnRepExcel',
					name: PF+'btnRepExcel',
					listeners:{
						click:{
							fn:function(e){
								
								/*var col = NS.gridDetalle.getColumnModel().getColumnHeader(0);
								alert(col);

								var col2 = NS.gridDetalle.getColumnModel().getColumnHeader(1);
								alert(col2);
								*/
							
								var regs = NS.gridDetalle.store.data.items;
								
								if(regs.length == 0) {
									Ext.Msg.alert('SET','No existen registros para el reporte!!');
									return;
								}
								
								mascara.show();
								
								var jSonString = '';
								
								if(NS.consulta == 'MovtoTes'){
									
									var matriz = new Array();
									
									for(var i=0; i<regs.length; i++){
										var vector = {};
										vector.descBanco = regs[i].get('descBanco');
										vector.idChequera = regs[i].get('idChequera');
										vector.idGrupo = regs[i].get('idGrupo');
										vector.idRubro = regs[i].get('idRubro');
										vector.fechaValor = regs[i].get('fecValor');
										vector.fechaOperacion = regs[i].get('fecOperacion');
										vector.concepto = regs[i].get('concepto');
										vector.referencia = regs[i].get('referencia');
										vector.ingresos = regs[i].get('ingreso'); 
										vector.egresos = regs[i].get('egreso');
										
										matriz[i] = vector;
									}
										
									jSonString = Ext.util.JSON.encode(matriz);
									
									mascara.show(); 
									NS.exportaExcel(jSonString, NS.consulta);
									
								}else if(NS.consulta == 'diarioTeso'){
									
									var matriz = new Array();
									
									for(var i=0; i<regs.length; i++){
										var vector = {};
										vector.descBanco = regs[i].get('descBanco');
										vector.idChequera = regs[i].get('idChequera');
										vector.idGrupo = regs[i].get('idGrupo');
										vector.idRubro = regs[i].get('idRubro');
										vector.fecValor = regs[i].get('fecValor');
										vector.fecOperacion = regs[i].get('fecOperacion');
										vector.importe = regs[i].get('importe');
										vector.referencia = regs[i].get('referencia');
										vector.concepto = regs[i].get('concepto');
										
										matriz[i] = vector;
									}
										
									jSonString = Ext.util.JSON.encode(matriz);
									alert(jSonString);
									mascara.show(); 
									NS.exportaExcel(jSonString, NS.consulta);
									
								}else if(NS.consulta == 'fecValorXEmp'){

									var matriz = new Array();
									
									var columnModel = NS.gridDetalle.getColumnModel();
									
									jSonString ='[';
									
									var band = true;
									
									for(var i=0; i<regs.length; i++){
										
										if(band){
											jSonString += '{'+'"descripcion":"CONCEPTO"';
											for(var x=0; x < NS.columns2.getColumnCount(true)-1; x++ ){
												jSonString += ',' + '"col'+x + '":"' + columnModel.getColumnHeader(x+1) + '"'; 
											}
											jSonString += '}';
											band = false;
										}
										
										jSonString += ',{'+'"descripcion":"'+regs[i].get('descripcion')+'"';
										
										for(var x=0; x < NS.columns2.getColumnCount(true)-1; x++ ){
											jSonString += ',' + '"col'+x + '":"' + regs[i].get('col'+x) + '"'; 
										}
										
										jSonString += '}';
									}
									jSonString += ']';
									mascara.show(); 
									NS.exportaExcel(jSonString, NS.consulta);
									
								}else if(NS.consulta == 'fecValorXCta'){
									
									var matriz = new Array();
									
									var columnModel = NS.gridDetalle.getColumnModel();
									
									jSonString ='[';
									
									var band = true;
									
									for(var i=0; i<regs.length; i++){
										
										if(band){
											jSonString += '{'+'"descripcion":"CONCEPTO"';
											for(var x=0; x < NS.columns2.getColumnCount(true)-1; x++ ){
												jSonString += ',' + '"col'+x + '":"' + columnModel.getColumnHeader(x+1) + '"'; 
											}
											jSonString += '}';
											band = false;
										}
										
										jSonString += ',{'+'"descripcion":"'+regs[i].get('descripcion')+'"';
										
										for(var x=0; x < NS.columns2.getColumnCount(true)-1; x++ ){
											jSonString += ',' + '"col'+x + '":"' + regs[i].get('col'+x) + '"'; 
										}
										
										jSonString += '}';
									}
									jSonString += ']';
									mascara.show(); 
									NS.exportaExcel(jSonString, NS.consulta);
									
								}else if(NS.consulta == 'repSaldos'){
									
									var matriz = new Array();
									
									var columnModel = NS.gridDetalle.getColumnModel();
									
									jSonString ='[';
									
									var band = true;
									
									for(var i=0; i<regs.length; i++){
										
										if(band){
											jSonString += '{'+'"descBanco":"BANCO"';
											jSonString += ','+'"idChequera":"CHEQUERA"';
											jSonString += ','+'"nomEmpresa":"EMPRESA"';
											jSonString += ','+'"idDivisa":"DIVISA"';
											for(var x=0; x < NS.columns2.getColumnCount(true)-4; x++ ){
												jSonString += ',' + '"col'+x + '":"' + columnModel.getColumnHeader(x+4) + '"'; 
											}
											jSonString += '}';
											band = false;
										}
										
										jSonString += ',{'+'"descBanco":"'+regs[i].get('descBanco')+'"';
										jSonString += ','+'"idChequera":"'+regs[i].get('idChequera')+'"';
										jSonString += ','+'"nomEmpresa":"'+regs[i].get('nomEmpresa')+'"';
										jSonString += ','+'"idDivisa":"'+regs[i].get('idDivisa')+'"';
										
										for(var x=0; x < NS.columns2.getColumnCount(true)-4; x++ ){
											jSonString += ',' + '"col'+x + '":"' + regs[i].get('col'+x) + '"'; 
										}
										
										jSonString += '}';
									}
									jSonString += ']';
									mascara.show(); 
									NS.exportaExcel(jSonString, NS.consulta);
									
								}

							}
						}
					}
				}
			 ]
	});
	
	
	NS.storePlantillas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {pantalla: 'PosicionBancaria'},
		root: '',
		paramOrder: ['pantalla'],
		directFn: PosicionBancariaAction.llenarComboPlantillas, 
		idProperty: 'id', 
		fields: [
				 {name: 'id'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen plantillas');
				
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
	
	NS.storePlantillas.load();	
	NS.cmbPlantillas = new Ext.form.ComboBox({
		store: NS.storePlantillas
		,name: PF + 'cmbPlantillas'
		,id: PF + 'cmbPlantillas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 5
        ,y: 35
        ,width: 150
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Plantillas'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					
					//Deseleccionamos cualquier registro que pueda estar seleccionado
					NS.selSelecciones.selectRange(-1,-1);
					
					PosicionBancariaAction.obtenerPlantilla(valor.data.id, function(result, e){
								
						if(e.message=="Unable to connect to the server."){
							Ext.Msg.alert('SET','Error de conexión al servidor');
							mascara.hide();
							return;
						}
								
						if(result.error != '' && result.error != null && result.error != undefined ){
							Ext.Msg.alert('SET',result.error);
							mascara.hide();
							return;
						}else if(result.plantilla != '' && result.plantilla != null && result.plantilla != undefined ){							
							
							
							NS.cargaPlantilla = true;
							
							NS.consulta = result.plantilla.boton;
							Ext.getCmp(PF+'txtPlantilla').setValue(result.plantilla.nombre);
							Ext.getCmp(PF+'fechaIni').setValue(result.plantilla.fechaInicio);
							Ext.getCmp(PF+'fechaFin').setValue(result.plantilla.fechaFin);
							Ext.getCmp(PF+'txtIdDivisa').setValue(result.plantilla.divisa);
							
							NS.datos1 = result.plantilla.datos1;
							NS.datos2 = result.plantilla.datos2;
							NS.datosOrden = result.plantilla.orden;
							
							//Pongo todas las seleccionas como false, ya que si una ya esta seleccionada 
							//y coincide con la que retorne la consulta, no realiza la búsqueda.
							Ext.getCmp(PF + 'optCuentas').setValue(false);
							Ext.getCmp(PF + 'optBancoEmpresas').setValue(false);
							Ext.getCmp(PF + 'optBanco').setValue(false);
							Ext.getCmp(PF + 'optEmpresas').setValue(false);
							
							switch (result.plantilla.seleccion) {
								case 1:
									Ext.getCmp(PF + 'optCuentas').setValue(true);
									Ext.getCmp(PF + 'optBancoEmpresas').setValue(false);
									Ext.getCmp(PF + 'optBanco').setValue(false);
									Ext.getCmp(PF + 'optEmpresas').setValue(false);
									break;
								case 2:
									Ext.getCmp(PF + 'optCuentas').setValue(false);
									Ext.getCmp(PF + 'optBancoEmpresas').setValue(true);
									Ext.getCmp(PF + 'optBanco').setValue(false);
									Ext.getCmp(PF + 'optEmpresas').setValue(false);
									break;
								case 3:
									Ext.getCmp(PF + 'optCuentas').setValue(false);
									Ext.getCmp(PF + 'optBancoEmpresas').setValue(false);
									Ext.getCmp(PF + 'optBanco').setValue(true);
									Ext.getCmp(PF + 'optEmpresas').setValue(false);
									break;
								case 4:
									Ext.getCmp(PF + 'optCuentas').setValue(false);
									Ext.getCmp(PF + 'optBancoEmpresas').setValue(false);
									Ext.getCmp(PF + 'optBanco').setValue(false);
									Ext.getCmp(PF + 'optEmpresas').setValue(true);
									break;
							}
							
						}
					});

				}
			}
		}
	});
	
	NS.fldPlantilla = new Ext.form.FieldSet ({
		title: 'Plantilla',
		x: 415,
		y: 215,
		width: 280,		
		height: 95,
		layout: 'absolute',
		items:
			 [
				{
					  xtype: 'textfield',
					  x: 5,
					  y: 0,
					  width: 150,
					  name: PF+'txtPlantilla',
					  id: PF+'txtPlantilla',
					  listeners: {
						  change: {
							  fn:function(caja) {

							  }
						  }
					  }
				},
				NS.cmbPlantillas,
				{
					xtype: 'button',
					text: 'Guardar',
					x: 170,
					y: 0,
					width: 80,
					height: 22,
					id: PF+'btnGuardarPlantilla',
					name: PF+'btnGuardarPlantilla',
					listeners:{
						click:{
							fn:function(e){
								
								var nomPlantilla = Ext.getCmp(PF+'txtPlantilla').getValue();
								if(nomPlantilla == '' || nomPlantilla == null){
									Ext.Msg.alert('SET','Por favor escriba un nombre para la plantilla.');
									return;
								}
							    
								var itemsPlantillas = NS.storePlantillas.data.items;
								
								for(var i=0; i<itemsPlantillas.length;i++){
									if(itemsPlantillas[i].get('descripcion') == nomPlantilla){
										Ext.Msg.alert('SET','El nombre de la plantilla ingresado ya existe.');
										return;
									}
								}
								
								if(NS.consulta == ''){
									Ext.Msg.alert('SET','Debe ejecutar una consulta antes de guardar la plantilla.');
									return;
								}
								
								mascara.show();
								
								//Obtiene los datos a guardar.
								
								//Selección de busqueda
								var opt = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
								
								var divisa = Ext.getCmp(PF+'txtIdDivisa').getValue();
								if(divisa == ''){
									divisa = 'MN';
								}
								
								var fechaIni = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaIni').getValue());
								var fechaFin = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaFin').getValue());
								
								
								NS.regresaChequerasPlantilla();
								
								PosicionBancariaAction.insertarPlantilla(NS.jsonBancos, NS.jsonChequeras,
																		NS.jsonEmpresas, NS.jsonOrden,
																		nomPlantilla, NS.consulta,
																		fechaIni, fechaFin, opt,
																		divisa, function(result, e){
									
									if(e.message=="Unable to connect to the server."){
										Ext.Msg.alert('SET','Error de conexión al servidor');
										mascara.hide();
										return;
									}
									
									if(result.error != '' && result.error != null && result.error != undefined ){
										Ext.Msg.alert('SET',result.error);
										mascara.hide();
										return;
									}else{
										Ext.Msg.alert('SET',result.msgUsuario);
										//NS.limpiarBusqueda(false);
										//NS.buscarDetallePosicion();
										NS.storePlantillas.load();
										mascara.hide();
										return;
									}
								});
								
							}
						}
					}
				},{
					xtype: 'button',
					text: 'Eliminar',
					x: 170,
					y: 35,
					width: 80,
					height: 22,
					id: PF+'btnEliminarPlantilla',
					name: PF+'btnEliminarPlantilla',
					listeners:{
						click:{
							fn:function(e){
								
								var plantilla = NS.cmbPlantillas.getValue();
								if(plantilla == '' || plantilla == null){
									Ext.Msg.alert('SET','Debe seleccionar una plantilla para eliminar.');
									return;
								}
								
								Ext.Msg.confirm('SET', '¿Está seguro que desea eliminar esta plantilla?', function(btn) {
			          				if(btn == 'yes') {
			          					
			          					mascara.show();
			          					
			          					PosicionBancariaAction.eliminarPlantilla(plantilla, function(result, e){
											
											if(e.message=="Unable to connect to the server."){
												Ext.Msg.alert('SET','Error de conexión al servidor');
												mascara.hide();
												return;
											}
											
											if(result.error != '' && result.error != null && result.error != undefined ){
												Ext.Msg.alert('SET',result.error);
												mascara.hide();
												return;
											}else{
												Ext.Msg.alert('SET',result.msgUsuario);
												NS.storePlantillas.removeAll();
												NS.cmbPlantillas.reset();
												Ext.getCmp(PF+'txtPlantilla').setValue('');
												
												NS.storePlantillas.load();
												
												mascara.hide();
												return;
											}
										});
			          				}
			          				
			          			});
								
							}
						}
					}
				}
			 ]
	});
	
	NS.fldConsultas = new Ext.form.FieldSet ({
		title: 'Consultas',
		x: 700,
		y: 215,
		width: 230,		
		height: 95,
		layout: 'absolute',
		items:
			 [
				{
					xtype: 'button',
					text: 'Movto. Tesorería',
					x: 0,
					y: 0,
					width: 80,
					height: 22,
					id: PF+'btnMovtoTes',
					name: PF+'btnMovtoTes',
					listeners:{
						click:{
							fn:function(e){
								
								NS.clickMovtoTes();
								
							}
						}
					}
				},{
					xtype: 'button',
					text: 'Fecha Valor XEmp',
					x: 0,
					y: 35,
					width: 80,
					height: 22,
					id: PF+'btnFecValorEmp',
					name: PF+'btnFecValorEmp',
					listeners:{
						click:{
							fn:function(e){
								
								NS.clickFecValorXEmp();
							}
						}
					}
				},{
					xtype: 'button',
					text: 'Diario Tesorería',
					x: 110,
					y: 0,
					width: 80,
					height: 22,
					id: PF+'btnDiarioTeso',
					name: PF+'btnDiarioTeso',
					listeners:{
						click:{
							fn:function(e){
								
								NS.clickDiarioTeso();
								
							}
						}
					}
				},{
					xtype: 'button',
					text: 'Fecha Valor X Cta',
					x: 110,
					y: 35,
					width: 80,
					height: 22,
					id: PF+'btnFecValorCta',
					name: PF+'btnFecValorCta',
					listeners:{
						click:{
							fn:function(e){
								NS.clickFecValorXCta();
							}
						}
					}
				}
			 ]
	});
	
	NS.storeMovtoTes = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {jsonBancos: '',
					 jsonChequeras: '',
					 jsonEmpresas: '',
					 fechaIni: '',
					 fechaFin: '',
					 seleccion: 0,
					 divisa: ''},
		root: '',
		paramOrder: ['jsonBancos','jsonChequeras','jsonEmpresas','fechaIni','fechaFin','seleccion','divisa'],
		directFn: PosicionBancariaAction.consultarMovtoTes,
		//idProperty: 'idPlan',
		fields: [
			{name: 'idBanco'},
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'idGrupo'},
			{name: 'idRubro'},
			{name: 'fecValor'},
			{name: 'fecOperacion'},
			{name: 'referencia'},
			{name: 'concepto'},
			{name: 'ingreso'},
			{name: 'egreso'},
			{name: 'color'}
		],
		listeners: {
			load: function(s, records){
				
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET','No existen registros con los criterios de búsqueda.');
				}else{
					NS.gridDetalle.reconfigure(NS.storeMovtoTes, NS.colMovtoTes);
				}					
					
				mascara.hide();
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	});
	
	NS.selMovtoTes = new Ext.grid.CheckboxSelectionModel({
 		singleSelect: true,
 	});
 	
	NS.colMovtoTes = new Ext.grid.ColumnModel([	
	  	NS.selMovtoTes,
	  	{header :'BANCO', width :100, dataIndex :'descBanco',
	  		renderer: function (value, meta, record) {
	  			if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
 	            return value;
 			}
	  	},
		{header :'CHEQUERA', width :200, dataIndex :'idChequera',
	  		renderer: function (value, meta, record) {
	  			if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
 	            return value;
 			}
	  	},
		{header :'GRUPO', width :80, dataIndex :'idGrupo',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
 	            return value;
 			}
	  	},
		{header :'RUBRO', width :80, dataIndex :'idRubro',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
				
 	            return value;
 			}
	  	},
		{header :'FECHA VALOR', width :200, dataIndex :'fecValor',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
 	            return value;
 			}
	  	},
		{header :'FECHA OPERACIÓN', width :200, dataIndex :'fecOperacion',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
 	            return value;
 			}
	  	},
		{header :'REFERENCIA', width :200, dataIndex :'referencia',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
				
 	            return value;
 			}
	  	},
		{header :'CONCEPTO', width :200, dataIndex :'concepto',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
				
 	            return value;
 			}
	  	},
		{header :'INGRESOS', width :200, dataIndex :'ingreso',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
				
				/*
				 * if(((record.get('descBanco') == '' || record.get('descBanco') == null || record.get('descBanco') == undefined)  
					&& (record.get('concepto') == '' || record.get('concepto') == null || record.get('concepto') == undefined)
					&& record.get('ingreso') == '0' && record.get('egreso') == '0')
					|| ((record.get('concepto') == 'Total Ingresos' && record.get('ingreso') == '0')
					|| (record.get('concepto') == 'Total Egresos' && record.get('ingreso') == '0')
					|| (record.get('concepto') == 'Total' && record.get('ingreso') == '0')))
					{
			
				 */
				value = '$ ' + NS.formatNumber(Math.round((value)*100)/100)
				
				if((record.get('descBanco') == '' || record.get('descBanco') == null || record.get('descBanco') == undefined)  
						&& (record.get('concepto') == '' || record.get('concepto') == null || record.get('concepto') == undefined)
						&& record.get('ingreso') == '0' && record.get('egreso') == '0'){

					value = '';
				}
				
				if((record.get('concepto') == 'Total Ingresos' && record.get('ingreso') == '0')
					|| (record.get('concepto') == 'Total Egresos' && record.get('ingreso') == '0')
					|| (record.get('concepto') == 'Total' && record.get('ingreso') == '0')
					){
					value = '';
				}
				
				/*if(value != '' && value != null && value != undefined){
					value = '$ ' + NS.formatNumber(Math.round((value)*100)/100)
				}*/
				
 	            return value;
 			}
	  	},
		{header :'EGRESOS', width :200, dataIndex :'egreso',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
 					meta.attr = 'style="background-color:#8888FF;"';
 				}else if(record.get('color') == '#CCCCCC' ){
 					meta.attr = 'style="background-color:#CCCCCC;"';
 				}
				
				value = '$ ' + NS.formatNumber(Math.round((value)*100)/100)
				
				if((record.get('descBanco') == '' || record.get('descBanco') == null || record.get('descBanco') == undefined)  
						&& (record.get('concepto') == '' || record.get('concepto') == null || record.get('concepto') == undefined)
						&& record.get('ingreso') == '0' && record.get('egreso') == '0'){

					value = '';
				}
				
				/*if((record.get('concepto') == 'Total Ingresos' && record.get('egreso') == '0')
						|| (record.get('concepto') == 'Total Egresos' && record.get('egreso') == '0')
						|| (record.get('concepto') == 'Total' && record.get('egreso') == '0')
						){
						value = '';
					}
				*/
				
				/*if(value != ''){
					value = '$ ' + NS.formatNumber(Math.round((value)*100)/100)
				}*/
				
 	            return value;
 			}
	  	}
	  	
	]);
	
	NS.storeDiarioTes = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {jsonBancos: '',
					 jsonChequeras: '',
					 jsonEmpresas: '',
					 fechaIni: '',
					 fechaFin: '',
					 seleccion: 0,
					 divisa: ''},
		root: '',
		paramOrder: ['jsonBancos','jsonChequeras','jsonEmpresas','fechaIni','fechaFin','seleccion','divisa'],
		directFn: PosicionBancariaAction.consultarDiarioTes,
		//idProperty: 'idPlan',
		fields: [			
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'idGrupo'},
			{name: 'idRubro'},
			{name: 'fecValor'},
			{name: 'fecOperacion'},
			{name: 'referencia'},
			{name: 'concepto'},
			{name: 'importe'},
			{name: 'color'}
		],
		listeners: {
			load: function(s, records){
				
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET','No existen registros con los criterios de búsqueda.');
				}else{
					NS.gridDetalle.reconfigure(NS.storeDiarioTes, NS.colDiarioTes);
				}
					
				mascara.hide();
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	});
	
	NS.colDiarioTes = new Ext.grid.ColumnModel([	
	  	NS.selMovtoTes,
	  	{header :'BANCO', width :100, dataIndex :'descBanco',
	  		renderer: function (value, meta, record) {
	  			if(record.get('color') == '#8888FF' ){
						meta.attr = 'style="background-color:#8888FF;"';
					}else if(record.get('color') == '#CCCCCC' ){
						meta.attr = 'style="background-color:#CCCCCC;"';
					}
		            return value;
				}
	  	},
		{header :'CHEQUERA', width :200, dataIndex :'idChequera',
	  		renderer: function (value, meta, record) {
	  			if(record.get('color') == '#8888FF' ){
						meta.attr = 'style="background-color:#8888FF;"';
					}else if(record.get('color') == '#CCCCCC' ){
						meta.attr = 'style="background-color:#CCCCCC;"';
					}
		            return value;
				}
	  	},
		{header :'GRUPO', width :80, dataIndex :'idGrupo',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
						meta.attr = 'style="background-color:#8888FF;"';
					}else if(record.get('color') == '#CCCCCC' ){
						meta.attr = 'style="background-color:#CCCCCC;"';
					}
		            return value;
				}
	  	},
		{header :'RUBRO', width :80, dataIndex :'idRubro',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
						meta.attr = 'style="background-color:#8888FF;"';
					}else if(record.get('color') == '#CCCCCC' ){
						meta.attr = 'style="background-color:#CCCCCC;"';
					}
				
		            return value;
				}
	  	},
		{header :'FECHA VALOR', width :200, dataIndex :'fecValor',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
						meta.attr = 'style="background-color:#8888FF;"';
					}else if(record.get('color') == '#CCCCCC' ){
						meta.attr = 'style="background-color:#CCCCCC;"';
					}
		            return value;
				}
	  	},
		{header :'FECHA OPERACIÓN', width :200, dataIndex :'fecOperacion',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
						meta.attr = 'style="background-color:#8888FF;"';
					}else if(record.get('color') == '#CCCCCC' ){
						meta.attr = 'style="background-color:#CCCCCC;"';
					}
				
				if(!isNaN(value) && value!= ''){
					value = '$ ' + NS.formatNumber(Math.round((value)*100)/100);
				}
				
		            return value;
				}
	  	},
	  	{header :'IMPORTE', width :200, dataIndex :'importe',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
						meta.attr = 'style="background-color:#8888FF;"';
					}else if(record.get('color') == '#CCCCCC' ){
						meta.attr = 'style="background-color:#CCCCCC;"';
					}
				
				if((record.get('descBanco') == '' || record.get('descBanco') == null || record.get('descBanco') == undefined)  
						&& (record.get('concepto') == '' || record.get('concepto') == null || record.get('concepto') == undefined)
						&& record.get('importe') == '0' && record.get('importe') == '0'){
	
					value = '';
				}
				
				if(value != ''){
					value = '$ ' + NS.formatNumber(Math.round((value)*100)/100)
				}
				
		            return value;
				}
	  	},
		{header :'REFERENCIA', width :200, dataIndex :'referencia',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
						meta.attr = 'style="background-color:#8888FF;"';
					}else if(record.get('color') == '#CCCCCC' ){
						meta.attr = 'style="background-color:#CCCCCC;"';
					}
				
		            return value;
				}
	  	},
		{header :'CONCEPTO', width :200, dataIndex :'concepto',
			renderer: function (value, meta, record) {
				if(record.get('color') == '#8888FF' ){
						meta.attr = 'style="background-color:#8888FF;"';
					}else if(record.get('color') == '#CCCCCC' ){
						meta.attr = 'style="background-color:#CCCCCC;"';
					}
				
		            return value;
				}
	  	}
	]);
	
	
	NS.storeValorXEmp = new Ext.data.DirectStore({
		paramsAsHash: false,
		//root: "row",
		paramOrder: ['jsonBancos', 'jsonChequeras','jsonEmpresas','fechaIni','fechaFin','seleccion','divisa','tipoReporte'],
		directFn: PosicionBancariaAction.posicionXEmp,
		fields:[
		        'nomReporte',
		        'tipoPosicion',
		        'columnas',
		        'fields'
		],
		listeners: {
			load: function(s, records) {
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridCash, msg: "Cargando información..."});
				
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No existe información con estos parametros');
					mascara.hide();
					return;
				}
					
				
				console.log(NS.storeValorXEmp.data.items[0].get('fields'));
				
				NS.fields = Ext.util.JSON.decode(NS.storeValorXEmp.data.items[0].get('fields'));
				
				NS.storeValorXEmp2 = new Ext.data.DirectStore({
					paramsAsHash: false,
					//root: "row",
					paramOrder: ['jsonBancos', 'jsonChequeras','jsonEmpresas','fechaIni','fechaFin','seleccion','divisa','tipoReporte'],
					directFn: PosicionBancariaAction.posicionXEmp2,
					fields: NS.fields,
					listeners: {
						load: function(s, records) {
							//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridCash, msg: "Cargando información..."});
							
							if(records.length == null || records.length <= 0){
								Ext.Msg.alert('SET', 'No existe información con estos parametros');
								mascara.hide();
								return;
							}
								
							//console.log("qwer: " + Ext.util.JSON.decode(NS.storeValorXEmp.data.items[0].get('columnas')));
							
							NS.columns2 = new Ext.grid.ColumnModel(Ext.util.JSON.decode(NS.storeValorXEmp.data.items[0].get('columnas')));
							
							//console.log("clm: " + NS.columns2);
							
							var recordsGridDatos = NS.storeValorXEmp.data.items;
							
							if(NS.tipoReporte == 1 || NS.tipoReporte == 2){
								//
								var iniI = 0;
								var finI = 0;
								var iniE = 0;
								var finE = 0;
								var sumI = 0;
								var sumE = 0;
								var totalI = 0;
								var totalE = 0;
								var saldoIni = 0;
								var sumTotal = 0;
								
								for(var i=0;i<records.length;i++){
									if(records[i].get('descripcion') == 'SALDO INICIAL'){
										iniI = i;
										//records[i].style.backgroundColor  = '#CCCCCC';
									}else if(records[i].get('descripcion') == 'TOTAL INGRESO'){
										finI = i;
										//records[i].style.backgroundColor  = '#CCCCCC';
									}else if(records[i].get('descripcion') == 'TOTAL EGRESO'){
										iniE = i;
										//records[i].style.backgroundColor  = '#CCCCCC';
									}else if(records[i].get('descripcion') == 'SALDO TOTAL'){
										finE = i;
										//records[i].style.backgroundColor  = '#CCCCCC';
									}
								}
								
								//NS.columns2.getColumnCount(true) -> solo las columnas visibles
								//NS.columns2.getColumnCount(false) ->solo las columnas ocultas
								
								//la funcion sum si funciona, pero no me suma los valores porque los toma como cadenas
								//var sumI = NS.storeValorXEmp2.sum('col3', 2,6);
								
								//SUMA LOS TOTALES INGRESO
								for(var x=0; x < NS.columns2.getColumnCount(true) -1; x++ ){
									sumI = 0;
									for(var y = (iniI+1); y < (finI); y++){
										sumI = sumI + (!isNaN(parseFloat(records[y].get('col'+x)))?parseFloat(records[y].get('col'+x)):0);
									}
									records[finI].set('col'+x,sumI);
									//records[finI].set('color',"#CCCCCC");
								}
								
								iniE = finI+1;
								
								//SUMA LOS TOTALES EGRESO
								for(var x=0; x < NS.columns2.getColumnCount(true) -1; x++ ){
									sumE = 0;
									for(var y = (iniE); y < (finE); y++){
										sumE = sumE + (!isNaN(parseFloat(records[y].get('col'+x)))?parseFloat(records[y].get('col'+x)):0);
									}
									records[finE-1].set('col'+x,sumE);
								}
								
								//SALDOS TOTALES
								for(var x=0; x < NS.columns2.getColumnCount(true) -1; x++ ){
									saldoIni = parseFloat(records[iniI].get('col'+x));
									totalI = parseFloat(records[finI].get('col'+x));
									totalE = parseFloat(records[finE-1].get('col'+x));
									sumTotal = (saldoIni + totalI) - totalE;
									records[finE].set('col'+x,sumTotal);
								}

								//CAMBIA EL TIPO DE FORMATO A NUMERICO EN TO_DO EL GRID
								
								if(NS.tipoReporte == 1){
									for(var y=1; y < records.length; y++ ){
										for(var x = 0; x < NS.columns2.getColumnCount(true) -1; x++){
											records[y].set('col'+x, '$ ' + NS.formatNumber(Math.round((records[y].get('col'+x))*100)/100));
										}
									}
								}else{
									for(var y=2; y < records.length; y++ ){
										for(var x = 0; x < NS.columns2.getColumnCount(true) -1; x++){
											records[y].set('col'+x, '$ ' + NS.formatNumber(Math.round((records[y].get('col'+x))*100)/100));
										}
									}
								}
							
								//NS.gridDetalle.reconfigure(NS.storeValorXEmp2, NS.columns);
								NS.gridDetalle.reconfigure(NS.storeValorXEmp2, NS.columns2);
								NS.gridDetalle.setTitle(recordsGridDatos[0].get('nomReporte'));
								
								//Pinta colores
								NS.gridDetalle.getView().getRow(iniI).style.backgroundColor  = '#AAAAFF';
								NS.gridDetalle.getView().getRow(finI).style.backgroundColor  = '#CCCCCC';
								NS.gridDetalle.getView().getRow(finE-1).style.backgroundColor  = '#CCCCCC';
								NS.gridDetalle.getView().getRow(finE).style.backgroundColor  = '#AAAAFF';	
							}else if(NS.tipoReporte == 3){
								
								for(var y=0; y < records.length; y++ ){
									for(var x = 0; x < NS.columns2.getColumnCount(true) -4; x++){
										records[y].set('col'+x, '$ ' + NS.formatNumber(Math.round((records[y].get('col'+x))*100)/100));
									}
								}
								
								NS.gridDetalle.reconfigure(NS.storeValorXEmp2, NS.columns2);
								NS.gridDetalle.setTitle(recordsGridDatos[0].get('nomReporte'));
							}
							
							mascara.hide();
						},
						colores: function (value, meta, record) {
				    		meta.attr = 'style= "#2E64FE"';
				    		return value;
				    	}
					}
				});
				
				//NS.tipoReporte = 1;
				
				NS.regresaChequeras();  
				
				var divisa = Ext.getCmp(PF+'txtIdDivisa').getValue();
				if(divisa == ''){
					divisa = 'MN';
				}
				
				var fechaIni = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaIni').getValue());
				var fechaFin = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaFin').getValue());
				
				if(fechaIni == '' || fechaFin == ''){
					Ext.Msg.alert('SET','Seleccione un rago de fechas válido.');
					return;
				}
				
				var seleccion = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
				
				NS.storeValorXEmp2.baseParams.jsonBancos = NS.jsonBancos;
				NS.storeValorXEmp2.baseParams.jsonChequeras = NS.jsonChequeras;
				NS.storeValorXEmp2.baseParams.jsonEmpresas = NS.jsonEmpresas;
				NS.storeValorXEmp2.baseParams.fechaIni = fechaIni;
				NS.storeValorXEmp2.baseParams.fechaFin = fechaFin;
				NS.storeValorXEmp2.baseParams.seleccion = seleccion;
				NS.storeValorXEmp2.baseParams.divisa = divisa;
				NS.storeValorXEmp2.baseParams.tipoReporte = NS.tipoReporte;
				
				NS.storeValorXEmp2.load();
				
			},
			colores: function (value, meta, record) {
	    		meta.attr = 'style= "#2E64FE"';
	    		return value;
	    	}
		}
	});
	
	/**
	 
	  NS.storeValorXEmp = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: "row",
		paramOrder: ['jsonBancos', 'jsonChequeras','jsonEmpresas','fechaIni','fechaFin','seleccion','divisa','tipoReporte'],
		directFn: PosicionBancariaAction.posicionXEmp,
		fields: NS.fields,
		listeners: {
			load: function(s, records) {
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaGridCash, msg: "Cargando información..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existe información con estos parametros');
				
				
				
			},
			colores: function (value, meta, record) {
	    		meta.attr = 'style= "#2E64FE"';
	    		return value;
	    	}
		}
	});
	 */
	
	NS.gridDetalle = new Ext.grid.GridPanel({
        store : NS.storeMovtoTes,
        viewConfig: {emptyText: '',  markDirty: false},
        x: 5,
        y: 0,
        cm: NS.colMovtoTes,
		sm: NS.selMovtoTes,
        width:930,
        height:280,
        frame:true,
        listeners: {
        	dblclick: {
        		fn:function(e) {

        			var reg = NS.gridDetalle.getSelectionModel().getSelections();
        			
        			if(reg.length > 0){
        				
        				var rubro = reg[0].get('descripcion');
        				var tipoMovto = reg[0].get('tipoMovto');
        				
        				if(rubro != '' && rubro != null && !isNaN(rubro)){
        					
    						var items = NS.gridDetalle.store.data.items;
    						
    						var jSonString = '';
    						
    						if(NS.consulta == 'fecValorXEmp'){
    							
    							var matEmp = new Array();
    							
        						for(var i=0; i<NS.columns2.getColumnCount(true)-1;i++){
        							var vecEmp = {};
        							vecEmp.noEmpresa = items[0].get("col"+i).split(':')[0];
        							matEmp[i] = vecEmp;
        						}
        						
        						jSonString = Ext.util.JSON.encode(matEmp);
        						
    						}else if(NS.consulta == 'fecValorXCta'){
    						
    							var matCtas = new Array();
        						
        						for(var i=0; i<NS.columns2.getColumnCount(true)-1;i++){
        							var vecCtas = {};
        							vecCtas.idChequera = items[1].get("col"+i);
        							matCtas[i] = vecCtas;
        						}
        						
        						jSonString = Ext.util.JSON.encode(matCtas);
        						
    						}
        						
    						
    						
    						var divisa = Ext.getCmp(PF+'txtIdDivisa').getValue();
    						if(divisa == ''){
    							divisa = 'MN';
    						}
    						
    						var fechaIni = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaIni').getValue());
    						var fechaFin = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaFin').getValue());
    						
    						if(fechaIni == '' || fechaFin == ''){
    							Ext.Msg.alert('SET','Seleccione un rago de fechas válido.');
    							return;
    						}
    						
    						NS.storeDetallePosicion2.baseParams.rubro = rubro;
    						
    						if(NS.consulta == 'fecValorXEmp'){
    							NS.storeDetallePosicion2.baseParams.jsonEmpresas = jSonString;
        						NS.storeDetallePosicion2.baseParams.jsonChequeras = '';
    						}else if(NS.consulta == 'fecValorXCta'){
    							NS.storeDetallePosicion2.baseParams.jsonEmpresas = '';
        						NS.storeDetallePosicion2.baseParams.jsonChequeras = jSonString;
    						}        					
    						NS.storeDetallePosicion2.baseParams.divisa = divisa;
    						NS.storeDetallePosicion2.baseParams.fechaIni = fechaIni;
    						NS.storeDetallePosicion2.baseParams.fechaFin = fechaFin;
    						NS.storeDetallePosicion2.baseParams.consulta = NS.consulta;
    						NS.storeDetallePosicion2.baseParams.tipoMovto = tipoMovto;
    						
    						NS.storeDetallePosicion2.load();
    						
    						NS.winModificaRubro.show();
        					
        				}
        				
        			}
        			
        		}
        	}
        }
    });
	
	NS.fldResultados = new Ext.form.FieldSet ({
		title: '',
		x: 00,
		y: 320,
		width: 965,		
		height: 300,
		layout: 'absolute',
		items:
			 [
			  	NS.gridDetalle	
			 ]
	});
	
	NS.storeDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: PosicionBancariaAction.llenarComboDivisa, 
		idProperty: 'idDivisa', 
		fields: [
				 {name: 'idDivisa'},
				 {name: 'descDivisa'}
			 ],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen divisas para el pago');
				else{
					Ext.getCmp(PF + 'txtIdDivisa').setValue('MN');
					NS.cmbDivisa.setValue('PESOS');
				}
					
				
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
	
	NS.storeDivisas.load();	
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisas
		,name: PF + 'cmbDivisa'
		,id: PF + 'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 760
        ,y: 80
        ,width: 150
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Divisa de Pago'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdDivisa', NS.cmbDivisa.getId());
				}
			}
		}
	});
	
	NS.storeRubros = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder: [],
		directFn: PosicionBancariaAction.consultarRubros,
		idProperty: 'idRubro',
		fields: [
			{name: 'idRubro' },
			{name: 'descRubro' }
		],
		listeners: {
			load: function(s, records){
				
			} 
		}
	});
	
	NS.selRubros = new Ext.grid.CheckboxSelectionModel({
 		singleSelect: false,
 	});
 	
	NS.colRubros = new Ext.grid.ColumnModel([	
	  	NS.selRubros,
	  	{header :'ID RUBRO', width :100, sortable :true, dataIndex :'idRubro' },
		{header :'DESC RUBRO', width :200, sortable :true, dataIndex :'descRubro'}
	]);
	
	NS.gridRubros = new Ext.grid.GridPanel({
        store : NS.storeRubros,
        viewConfig: {emptyText: ''},
        x: 5,
        y: 0,
        cm: NS.colRubros,
		sm: NS.selRubros,
        width: 610,
        height: 145,
        frame:true,
        listeners: {
        	click: {
        		fn:function(e) {
        			
        		}
        	}
        }
    });
	
	NS.fldBusquedaRubros = new Ext.form.FieldSet ({
		title: 'Búsqueda por rubro',
		x: 10,
		y: 10,
		width: 650,		
		height: 185,
		layout: 'absolute',
		items:
			 [
			  	NS.gridRubros
			 ]
	});
	
	NS.txtImporteIni = new Ext.form.NumberField({
		id: PF + 'txtImporteIni',
		name: PF + 'txtImporteIni',
		x: 20,
		y: 30,
		width: 150,
    	listeners:{
	 		keyup:{
	 			fn:function(caja, e) {
	 				
	 			}
	 		}
	 		
    	}    		
	});
	
	NS.txtImporteFin = new Ext.form.NumberField({
		id: PF + 'txtImporteFin',
		name: PF + 'txtImporteFin',
		x: 200,
		y: 30,
		width: 150,
    	listeners:{
	 		keyup:{
	 			fn:function(caja, e) {
	 				
	 			}
	 		}
	 		
    	}    		
	});
	
	NS.fldBusquedaImporte = new Ext.form.FieldSet ({
		title: 'Búsqueda por importe',
		x: 10,
		y: 10,
		width: 650,		
		height: 140,
		layout: 'absolute',
		items:
			 [
				{
				    xtype: 'label',
				    text: 'Importe Inicial',
				    x: 20,
				    y: 10
				},
				{
				    xtype: 'label',
				    text: 'Importe Final',
				    x: 200,
				    y: 10
				},
			  	NS.txtImporteIni,
			  	NS.txtImporteFin
			 ]
	});
	
	NS.storeDetallePosicion = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {jsonRubros: '',
					 importeIni: 0,
					 importeFin: 0,
					 jsonBancos: '',
					 jsonChequeras: '',
					 jsonEmpresas: '',
					 divisa: '',
					 fechaIni: '',
					 fechaFin: '',
					 optConsulta: 0,
					 bSinRubros:false
					},
		root: '',
		paramOrder: ['jsonRubros','importeIni','importeFin','jsonBancos','jsonChequeras','jsonEmpresas','divisa','fechaIni','fechaFin','optConsulta','bSinRubros'],
		directFn: PosicionBancariaAction.consultarDetallePosicion,
		//idProperty: 'idRubro',
		fields: [
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'idRubro'},
			{name: 'descRubro'},
			{name: 'importe'},
			{name: 'concepto'},
			{name: 'fecOperacion', type: 'date', dateFormat:'d/m/Y'},
			{name: 'fecValor', type: 'date', dateFormat:'d/m/Y'},
			{name: 'idDivisa'},
			{name: 'idTipoMovto'},
			{name: 'beneficiario'},
			{name: 'descripcion'},
			{name: 'noDocto'},
			{name: 'referencia'},
			{name: 'noFolioDet'}
			
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET','No existen registros para esa búsqueda.');
					mascara.hide();
					return;
				}

				mascara.hide();
				
			} 
		}
	});
	
	NS.selDetallePosicion = new Ext.grid.CheckboxSelectionModel({
 		singleSelect: true,
 		listeners: {
 			rowselect: function(chkSelectionModel, rowIndex, keepExisting, record ){
 				var regsSel = NS.gridDetallePosicion.getSelectionModel().getSelections();
 				var tipoMovto = '';
 				if(regsSel.length > 0 ){
 					Ext.getCmp(PF+'txtReferencia').setValue(regsSel[0].get('referencia'));
 					Ext.getCmp(PF+'fechaReferencia').setValue(regsSel[0].get('fecOperacion'));
 					tipoMovto = regsSel[0].get('idTipoMovto');
 				}
 				
 				NS.storeGrupoRubro.baseParams.tipoMovto = tipoMovto;
 				NS.storeGrupoRubro.load();
 			}
 		}
 	});
 	
	NS.colDetallePosicion = new Ext.grid.ColumnModel([	
	  	NS.selDetallePosicion,
	  	{header :'BANCO', width :100, sortable :true, dataIndex :'descBanco'},
		{header :'CHEQUERA', width :200, sortable :true, dataIndex :'idChequera'},
	  	{header :'RUBRO', width :100, sortable :true, dataIndex :'idRubro'},
	  	{header :'DESC RUBRO', width :100, sortable :true, dataIndex :'descRubro'},
	  	{header :'IMPORTE', width :100, sortable :true, dataIndex :'importe', 
	  		renderer: function (value, meta, record) {
	  			return '$' + NS.formatNumber(Math.round((value)*100)/100 );
	  		}
	  	},
	  	{header :'CONCEPTO', width :100, sortable :true, dataIndex :'concepto'},
	  	{header :'FEC OPERACIÓN', 
	  		width :100, 
	  		xtype: 'datecolumn', 
	  		format: 'd/m/Y', 
	  		sortable :true, 
	  		dataIndex :'fecOperacion',
			editor: {
				xtype : 'datefield',
				//format: 'Y-m-d H:i:s',
				format: 'd/m/Y',
				submitFormat: 'c'
			}
        },
	  	{header :'FEC VALOR', 
        	width :100, 
        	xtype: 'datecolumn', 
	  		format: 'd/m/Y', 
        	sortable :true, 
        	dataIndex :'fecValor',
        	editor: {
				xtype : 'datefield',
				//format: 'Y-m-d H:i:s',
				format: 'd/m/Y',
				submitFormat: 'c'
			}
        },
	  	{header :'ID DIVISA', width :100, sortable :true, dataIndex :'idDivisa'},
	  	{header :'TIPO MOVTO', width :100, sortable :true, dataIndex :'idTipoMovto'},
	  	{header :'BENEFICIARIO', width :100, sortable :true, dataIndex :'beneficiario'},
	  	{header :'REFERENCIA', width :100, sortable :true, dataIndex :'referencia'},
	  	{header :'NO DOCTO', width :100, sortable :true, dataIndex :'noDocto' }
	]);
	
	NS.gridDetallePosicion = new Ext.grid.GridPanel({
        store : NS.storeDetallePosicion,
        viewConfig: {emptyText: 'Sin registros'},
        x: 0,
        y: 0,
        cm: NS.colDetallePosicion,
		sm: NS.selDetallePosicion,
        width: 805,
        height: 210,
        frame:true,
        listeners: {
        	click: {
        		fn:function(e) {
        			
        		}
        	}
        }
    });
	
	/************************************************************************************************/
	/*****				GRID POSICIÓN PARA LA VENTANA MODIFICACIÓN DE RUBROS					*****/
	/************************************************************************************************/
	
	NS.storeDetallePosicion2 = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {rubro:'',
					 jsonChequeras: '',
					 jsonEmpresas: '',
					 divisa: '',
					 fechaIni: '',
					 fechaFin: '',
					 consulta: '',
					 tipoMovto:''
					},
		root: '',
		paramOrder: ['rubro','jsonChequeras','jsonEmpresas','divisa','fechaIni','fechaFin', 'consulta', 'tipoMovto'],
		directFn: PosicionBancariaAction.consultarDetallePosicion2,
		//idProperty: 'idRubro',
		fields: [
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'nomEmpresa'},
			{name: 'noEmpresa'},
			{name: 'idRubro'},
			{name: 'descRubro'},
			{name: 'importe'},
			{name: 'concepto'},
			{name: 'fecOperacion', type: 'date', dateFormat:'d/m/Y'},
			{name: 'fecValor', type: 'date', dateFormat:'d/m/Y'},
			{name: 'idDivisa'},
			{name: 'idTipoMovto'},
			{name: 'beneficiario'},
			{name: 'descripcion'},
			{name: 'noDocto'},
			{name: 'referencia'},
			{name: 'noFolioDet'}
			
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0){
					Ext.Msg.alert('SET','No existen registros con este rubro.');
					return;
				}
					
				
			} 
		}
	});
	
	NS.selDetallePosicion2 = new Ext.grid.CheckboxSelectionModel({
 		singleSelect: true,
 		listeners: {
 			rowselect: function(chkSelectionModel, rowIndex, keepExisting, record ){
 				var regsSel = NS.gridDetallePosicion2.getSelectionModel().getSelections();
 				var tipoMovto = '';
 				if(regsSel.length > 0 ){
 					Ext.getCmp(PF+'txtReferencia2').setValue(regsSel[0].get('referencia'));
 					Ext.getCmp(PF+'fechaReferencia2').setValue(regsSel[0].get('fecOperacion'));
 					tipoMovto = regsSel[0].get('idTipoMovto');
 				}
 				
 				NS.storeGrupoRubro2.baseParams.tipoMovto = tipoMovto;
 				NS.storeGrupoRubro2.load();
 			}
 		}
 	});
 	
	NS.colDetallePosicion2 = new Ext.grid.ColumnModel([	
	  	NS.selDetallePosicion2,
	  	{header :'BANCO', width :100, sortable :true, dataIndex :'descBanco'},
		{header :'CHEQUERA', width :200, sortable :true, dataIndex :'idChequera'},
		{header :'EMPRESA', width :200, sortable :true, dataIndex :'nomEmpresa'},
	  	{header :'RUBRO', width :100, sortable :true, dataIndex :'idRubro'},
	  	{header :'DESC RUBRO', width :100, sortable :true, dataIndex :'descRubro'},
	  	{header :'IMPORTE', width :100, sortable :true, dataIndex :'importe', 
	  		renderer: function (value, meta, record) {
	  			return '$' + NS.formatNumber(Math.round((value)*100)/100 );
	  		}
	  	},
	  	{header :'CONCEPTO', width :100, sortable :true, dataIndex :'concepto'},
	  	{header :'REFERENCIA', width :100, sortable :true, dataIndex :'referencia'},
	  	{header :'FEC OPERACIÓN', 
	  		width :100, 
	  		xtype: 'datecolumn', 
	  		format: 'd/m/Y', 
	  		sortable :true, 
	  		dataIndex :'fecOperacion',
			editor: {
				xtype : 'datefield',
				//format: 'Y-m-d H:i:s',
				format: 'd/m/Y',
				submitFormat: 'c'
			}
        },
	  	{header :'FEC VALOR', 
        	width :100, 
        	xtype: 'datecolumn', 
	  		format: 'd/m/Y', 
        	sortable :true, 
        	dataIndex :'fecValor',
        	editor: {
				xtype : 'datefield',
				//format: 'Y-m-d H:i:s',
				format: 'd/m/Y',
				submitFormat: 'c'
			}
        },
	  	{header :'ID DIVISA', width :100, sortable :true, dataIndex :'idDivisa'},
	  	{header :'TIPO MOVTO', width :100, sortable :true, dataIndex :'idTipoMovto'},
	  	{header :'BENEFICIARIO', width :100, sortable :true, dataIndex :'beneficiario'},
	  	{header :'NO DOCTO', width :100, sortable :true, dataIndex :'noDocto' }
	]);
	
	NS.gridDetallePosicion2 = new Ext.grid.GridPanel({
        store : NS.storeDetallePosicion2,
        viewConfig: {emptyText: 'Sin registros'},
        x: 0,
        y: 0,
        cm: NS.colDetallePosicion2,
		sm: NS.selDetallePosicion2,
        width: 805,
        height: 400,
        frame:true,
        listeners: {
        	click: {
        		fn:function(e) {
        			
        		}
        	}
        }
    });
	
	NS.fldDetallePosicion2 = new Ext.form.FieldSet ({
		title: 'Detalle Posición',
		x: 10,
		y: 10,
		width: 830,		
		height: 440,
		layout: 'absolute',
		items:
			 [
			  	NS.gridDetallePosicion2
			 ]
	});
	
	/************************************************************************************************/
	NS.fldDetallePosicion = new Ext.form.FieldSet ({
		title: 'Detalle Posición',
		x: 10,
		y: 210,
		width: 830,		
		height: 250,
		layout: 'absolute',
		items:
			 [
			  	NS.gridDetallePosicion
			 ]
	});
	
	
	NS.storeGrupoRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { tipoMovto: 'E' },
		root: '',
		paramOrder: ['tipoMovto'],
		directFn: PosicionBancariaAction.obtenerGruposRubros, 
		idProperty: 'idStr', 
		fields: [
				 {name: 'idStr'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existe grupo de rubro del registro seleccionado.');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
	
	//NS.storeCmbRubro.load();	
	NS.cmbGrupoRubro = new Ext.form.ComboBox({
		store: NS.storeGrupoRubro
		,name: PF + 'cmbGrupoRubro'
		,id: PF + 'cmbGrupoRubro'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 200
        ,y: 55
        ,width: 180
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: ''
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGrupoRubro', NS.cmbGrupoRubro.getId());
					
					NS.storeCmbRubro.baseParams.idGrupoRubro = combo.getValue();
					NS.storeCmbRubro.load();
				}
			}
		}
	});
	
	NS.storeCmbRubro = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idGrupoRubro: '' },
		root: '',
		paramOrder: ['idGrupoRubro'],
		directFn: PosicionBancariaAction.llenarComboRubro, 
		idProperty: 'idStr', 
		fields: [
				 {name: 'idStr'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existe rubro del registro seleccionado.');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
	
	//NS.storeCmbRubro.load();	
	NS.cmbRubro = new Ext.form.ComboBox({
		store: NS.storeCmbRubro
		,name: PF + 'cmbRubro'
		,id: PF + 'cmbRubro'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 200
        ,y: 90
        ,width: 180
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: ''
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdRubro', NS.cmbRubro.getId());
				}
			}
		}
	});
	
	NS.fldReclasificacion = new Ext.form.FieldSet ({
		title: 'Reclasificación de Movimientos',
		x: 10,
		y: 470,
		width: 650,		
		height: 150,
		layout: 'absolute',
		items:
			 [
				{
					xtype: 'button',
					text: 'Aceptar',
					x: 450,
					y: 40,
					width: 80,
					height: 22,
					id: PF+'btnAceptarReclas',
					name: PF+'btnAceptarReclas',
					listeners:{
						click:{
							fn:function(e){
								
								if(NS.cmbGrupoRubro.getValue() == ''){
									Ext.Msg.alert('SET','Falta de información: No tiene definido ningún grupo de rubros.');
		    						return;
								}
								
								if(NS.cmbRubro.getValue() == ''){
									Ext.Msg.alert('SET','Falta de información: No tiene definido ningún rubro.');
		    						return;
								}
								
								var regSel = NS.gridDetallePosicion.getSelectionModel().getSelections();
								
								
								if(regSel <= 0){
									Ext.Msg.alert('SET','Es necesario seleccionar un registro.');
		    						return;
								}
								
								Ext.Msg.confirm('Reclasificación de Rubros', '¿Desea realizar esta operación?', function(btn) {
			          				if(btn == 'yes') {
			          					
			          					mascara.show();
			          					
			          					var idGrupo = ''+NS.cmbGrupoRubro.getValue();
			          					var idRubro = ''+NS.cmbRubro.getValue(); 
			          					//var importe = NS.unformatNumber(''+regSel[0].get('importe'));
			          					var importe = regSel[0].get('importe');
			          					//var chkModRef = Ext.getCmp(PF + 'chkModificacion').getValue();
			          					var noFolioDet = ''+regSel[0].get('noFolioDet');
			          					var referencia = ''+Ext.getCmp(PF + 'txtReferencia').getValue();
			          					
			          					PosicionBancariaAction.actualizaRubros(noFolioDet,idGrupo,idRubro,
			          															importe, referencia, false, function(result, e) {
				            				if(e.message=="Unable to connect to the server."){
				            					Ext.Msg.alert('SET','Error de conexión al servidor');
				            					mascara.hide();
				            					return;
				            				}
				            				
				            				if(result.error != '' && result.error != null && result.error != undefined ){
				            					Ext.Msg.alert('SET',result.error);
				            					mascara.hide();
				            					return;
				            				}else{
				            					Ext.Msg.alert('SET',result.msg);
				            					NS.limpiarBusqueda(false);
				            					NS.buscarDetallePosicion();
				            					mascara.hide();
				            					return;
				            				}
				            				
				            			});
				          					
			          				}
			          			});
								
							}
						}
					}
				},/*{
				   	xtype: 'checkbox',
				   	id: PF + 'chkModificacion',
				   	name: PF + 'chkModificacion',
				   	x: 10,
				   	y: 0,
				   	boxLabel: 'Modificación/Referencia/Fecha',
				   	listeners:{
				   		change:{
				   			fn:function(e){
				   				if(Ext.getCmp(PF + 'chkBloqDoc').getValue() == true ){
				   					Ext.getCmp(PF + 'chkBloqDoc').setValue(false);
				   				}
				   			}
				   		}
				   	}
				   },*/
				   {
	                    xtype: 'label',
	                    text: 'Referencia',
	                    x: 10,
	                    y: 25
	                },{
			        	xtype: 'textfield',
			        	x: 75,
			        	y: 25,
			        	width: 160,
			        	name: PF+'txtReferencia',
			        	id: PF+'txtReferencia',
			        	listeners: {
			        		change: {
			        			fn:function(caja) {
			        				
			        			}
			        		}
			        	}
			        },{
	               		xtype:'datefield',
	               		format:'d/m/Y',
	               		id:PF+'fechaReferencia',
	               		name:PF+'fechaReferencia',
	               		//value: apps.SET.FEC_HOY,
	               		x: 245,
	               		y: 25,
	               		width:110,
	               		allowBlank: false,
	               		disabled: true,
	               		listeners:{
	               			change:{
	               				fn:function(caja,valor){
									         		
	               				}
	               			}
	               		}
	               	},{
	                    xtype: 'label',
	                    text: 'Grupo',
	                    x: 10,
	                    y: 55
	                },
	                {
			        	xtype: 'textfield',
			        	x: 75,
			        	y: 55,
			        	width: 110,
			        	name: PF+'txtIdGrupoRubro',
			        	id: PF+'txtIdGrupoRubro',
			        	listeners: {
			        		change: {
			        			fn:function(caja) {
			        				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdGrupoRubro', NS.cmbGrupoRubro.getId());
	                        			
		                        		if(valueCombo == null || valueCombo == undefined || valueCombo == '')
		                        			Ext.getCmp(PF + 'txtIdGrupoRubro').reset();
		                        			
	                				}else
	                					NS.cmbGrupoRubro.reset();
			        			}
			        		}
			        	}
			        },
			        {
	                    xtype: 'label',
	                    text: 'Rubro',
	                    x: 10,
	                    y: 90
	                },
			        NS.cmbGrupoRubro,
			        {
			        	xtype: 'textfield',
			        	x: 75,
			        	y: 90,
			        	width: 110,
			        	name: PF+'txtIdRubro',
			        	id: PF+'txtIdRubro',
			        	listeners: {
			        		change: {
			        			fn:function(caja) {
			        				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdRubro', NS.cmbRubro.getId());
	                        			
		                        		if(valueCombo == null || valueCombo == undefined || valueCombo == '')
		                        			Ext.getCmp(PF + 'txtIdRubro').reset();
		                        			
	                				}else
	                					NS.cmbRubro.reset();
			        			}
			        		}
			        	}
			        },
			        NS.cmbRubro
			 ]
	});
	
	/***** 		PARA LA MODIFICACION DE RUBROS DE BUSQUEDA XEMP Y XCTA.		*****/
	
	NS.storeGrupoRubro2 = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { tipoMovto: 'E' },
		root: '',
		paramOrder: ['tipoMovto'],
		directFn: PosicionBancariaAction.obtenerGruposRubros, 
		idProperty: 'idStr', 
		fields: [
				 {name: 'idStr'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existe grupo de rubro del registro seleccionado.');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
	
	//NS.storeCmbRubro.load();	
	NS.cmbGrupoRubro2 = new Ext.form.ComboBox({
		store: NS.storeGrupoRubro2
		,name: PF + 'cmbGrupoRubro2'
		,id: PF + 'cmbGrupoRubro2'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 200
        ,y: 55
        ,width: 180
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: ''
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdGrupoRubro2', NS.cmbGrupoRubro2.getId());
					
					NS.storeCmbRubro2.baseParams.idGrupoRubro = combo.getValue();
					NS.storeCmbRubro2.load();
				}
			}
		}
	});
	
	NS.storeCmbRubro2 = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idGrupoRubro: '' },
		root: '',
		paramOrder: ['idGrupoRubro'],
		directFn: PosicionBancariaAction.llenarComboRubro, 
		idProperty: 'idStr', 
		fields: [
				 {name: 'idStr'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existe rubro del registro seleccionado.');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
	
	//NS.storeCmbRubro.load();	
	NS.cmbRubro2 = new Ext.form.ComboBox({
		store: NS.storeCmbRubro2
		,name: PF + 'cmbRubro2'
		,id: PF + 'cmbRubro2'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 200
        ,y: 90
        ,width: 180
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: ''
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdRubro2', NS.cmbRubro2.getId());
				}
			}
		}
	});
	
	NS.fldReclasificacion2 = new Ext.form.FieldSet ({
		title: 'Reclasificación de Movimientos',
		x: 10,
		y: 470,
		width: 650,		
		height: 150,
		layout: 'absolute',
		items:
			 [
				{
					xtype: 'button',
					text: 'Aceptar',
					x: 450,
					y: 40,
					width: 80,
					height: 22,
					id: PF+'btnAceptarReclas2',
					name: PF+'btnAceptarReclas2',
					listeners:{
						click:{
							fn:function(e){
								
								if(NS.cmbGrupoRubro2.getValue() == ''){
									Ext.Msg.alert('SET','Falta de información: No tiene definido ningún grupo de rubros.');
		    						return;
								}
								
								if(NS.cmbRubro2.getValue() == ''){
									Ext.Msg.alert('SET','Falta de información: No tiene definido ningún rubro.');
		    						return;
								}
								
								var regSel = NS.gridDetallePosicion2.getSelectionModel().getSelections();
								
								
								if(regSel <= 0){
									Ext.Msg.alert('SET','Es necesario seleccionar un registro.');
		    						return;
								}
								
								Ext.Msg.confirm('Reclasificación de Rubros', '¿Desea realizar esta operación?', function(btn) {
			          				if(btn == 'yes') {
			          					
			          					mascara.show();
			          					
			          					var idGrupo = ''+NS.cmbGrupoRubro2.getValue();
			          					var idRubro = ''+NS.cmbRubro2.getValue(); 
			          					var importe = regSel[0].get('importe');
			          					var noFolioDet = ''+regSel[0].get('noFolioDet');
			          					var referencia = ''+Ext.getCmp(PF + 'txtReferencia2').getValue();
			          					
			          					PosicionBancariaAction.actualizaRubros(noFolioDet,idGrupo,idRubro,
			          															importe, referencia, false, function(result, e) {
				            				if(e.message=="Unable to connect to the server."){
				            					Ext.Msg.alert('SET','Error de conexión al servidor');
				            					mascara.hide();
				            					return;
				            				}
				            				
				            				if(result.error != '' && result.error != null && result.error != undefined ){
				            					Ext.Msg.alert('SET',result.error);
				            					mascara.hide();
				            					return;
				            				}else{
				            					Ext.Msg.alert('SET',result.msg);
				            					NS.limpiarBusqueda2(false);
				            					mascara.hide();
				            					return;
				            				}
				            				
				            			});
				          					
			          				}
			          			});
								
							}
						}
					}
				},
				   {
	                    xtype: 'label',
	                    text: 'Referencia',
	                    x: 10,
	                    y: 25
	                },{
			        	xtype: 'textfield',
			        	x: 75,
			        	y: 25,
			        	width: 160,
			        	name: PF+'txtReferencia2',
			        	id: PF+'txtReferencia2',
			        	listeners: {
			        		change: {
			        			fn:function(caja) {
			        				
			        			}
			        		}
			        	}
			        },{
	               		xtype:'datefield',
	               		format:'d/m/Y',
	               		id:PF+'fechaReferencia2',
	               		name:PF+'fechaReferencia2',
	               		//value: apps.SET.FEC_HOY,
	               		x: 245,
	               		y: 25,
	               		width:110,
	               		allowBlank: false,
	               		disabled: true,
	               		listeners:{
	               			change:{
	               				fn:function(caja,valor){
									         		
	               				}
	               			}
	               		}
	               	},{
	                    xtype: 'label',
	                    text: 'Grupo',
	                    x: 10,
	                    y: 55
	                },
	                {
			        	xtype: 'textfield',
			        	x: 75,
			        	y: 55,
			        	width: 110,
			        	name: PF+'txtIdGrupoRubro2',
			        	id: PF+'txtIdGrupoRubro2',
			        	listeners: {
			        		change: {
			        			fn:function(caja) {
			        				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdGrupoRubro2', NS.cmbGrupoRubro2.getId());
	                        			
		                        		if(valueCombo == null || valueCombo == undefined || valueCombo == '')
		                        			Ext.getCmp(PF + 'txtIdGrupoRubro2').reset();
		                        			
	                				}else
	                					NS.cmbGrupoRubro2.reset();
			        			}
			        		}
			        	}
			        },
			        {
	                    xtype: 'label',
	                    text: 'Rubro',
	                    x: 10,
	                    y: 90
	                },
			        NS.cmbGrupoRubro2,
			        {
			        	xtype: 'textfield',
			        	x: 75,
			        	y: 90,
			        	width: 110,
			        	name: PF+'txtIdRubro2',
			        	id: PF+'txtIdRubro2',
			        	listeners: {
			        		change: {
			        			fn:function(caja) {
			        				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdRubro2', NS.cmbRubro2.getId());
	                        			
		                        		if(valueCombo == null || valueCombo == undefined || valueCombo == '')
		                        			Ext.getCmp(PF + 'txtIdRubro2').reset();
		                        			
	                				}else
	                					NS.cmbRubro2.reset();
			        			}
			        		}
			        	}
			        },
			        NS.cmbRubro2
			 ]
	});
	
	/*****************************************************************************/
	NS.winBusqueda = new Ext.Window({
		title: 'Búsqueda de movimientos'
		,modal:true
		,shadow:true
		,width: 900
	   	,height: 600
	   	//,minWidth: 400
	   	//,minHeight: 400
	   	,layout: 'absolute'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,resizable: false	
	   	,closable: false
	   	,autoScroll: true
	   	,items: [
	   	         NS.fldBusquedaRubros,
	   	         NS.fldBusquedaImporte,
	   	         {
						xtype: 'button',
						text: 'Buscar',
						x: 680,
						y: 20,
						width: 80,
						height: 22,
						id: PF+'btnBuscarMovtos',
						name: PF+'btnBuscarMovtos',
						listeners:{
							click:{
								fn:function(e){
									NS.buscarDetallePosicion();
								}
							}
						}
				},{
					xtype: 'button',
					text: 'Limpiar',
					x: 740,
					y: 480,
					width: 80,
					height: 22,
					id: PF+'btnLimpiarDetPos',
					name: PF+'btnLimpiarDetPos',
					listeners:{
						click:{
							fn:function(e){
								NS.limpiarBusqueda(true);
								if(NS.optBusqueda == 1){
									NS.storeRubros.load();
								}

							}
						}
					}
				},{
					xtype: 'button',
					text: 'Excel',
					x: 740,
					y: 510,
					width: 80,
					height: 22,
					id: PF+'btnExcelPosicion',
					name: PF+'btnExcelPosicion',
					listeners:{
						click:{
							fn:function(e){
								
								var regs = NS.gridDetallePosicion.store.data.items;

								//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
								if(regs.length == 0) {
									Ext.Msg.alert('SET','No existen registros para el reporte!!');
									return;
								}
								
								var matriz = new Array();
								
								for(var i=0; i<regs.length; i++){
									var vector = {};
									vector.descBanco = regs[i].get('descBanco');
									vector.idChequera = regs[i].get('idChequera');
									vector.idRubro = regs[i].get('idRubro');
									vector.descRubro = regs[i].get('descRubro');
									vector.importe = regs[i].get('importe');
									vector.concepto = regs[i].get('concepto');
									vector.referencia = regs[i].get('referencia');
									vector.fecOperacion = NS.cambiarFecha(''+regs[i].get('fecOperacion'));
									vector.fecValor = NS.cambiarFecha(''+regs[i].get('fecValor'));
									vector.idDivisa = regs[i].get('idDivisa');
									vector.idTipoMovto = regs[i].get('idTipoMovto');
									vector.beneficiario = regs[i].get('beneficiario'); 
									vector.noDocto = regs[i].get('noDocto');
									
									matriz[i] = vector;
								}
									
								var jSonString = Ext.util.JSON.encode(matriz);
								
								mascara.show(); 
								NS.exportaExcelPosicion(jSonString);
							}
						}
					}
				},{
					xtype: 'button',
					text: 'Cerrar',
					x: 740,
					y: 540,
					width: 80,
					height: 22,
					id: PF+'btnCerrarBusqueda',
					name: PF+'btnCerrarBusqueda',
					listeners:{
						click:{
							fn:function(e){
								NS.winBusqueda.hide();
							}
						}
					}
				},
				NS.fldDetallePosicion,
				NS.fldReclasificacion
	 	],
	 	listeners:{
	    	 show:{
	    		 fn:function(){
	    			 
	    			 switch(NS.optBusqueda){
	    			 	case 1:
	    			 		NS.fldBusquedaRubros.setVisible(true);
			    			NS.fldBusquedaImporte.setVisible(false);
			    			NS.storeRubros.load();
	    			 		break;
	    			 	case 2:
	    			 		NS.fldBusquedaRubros.setVisible(false);
			    			NS.fldBusquedaImporte.setVisible(true);
	    			 		break;
	    			 }
	    			 
	    		 }
	    	 }
	     } // End listeners	
	});
	
	NS.winModificaRubro = new Ext.Window({
		title: 'Modificación de Rubros'
		,modal:true
		,shadow:true
		,width: 900
	   	,height: 600
	   	//,minWidth: 400
	   	//,minHeight: 400
	   	,layout: 'absolute'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,resizable: false	
	   	,closable: false
	   	,autoScroll: true
	   	,items: [
	   	         {
					xtype: 'button',
					text: 'Limpiar',
					x: 740,
					y: 480,
					width: 80,
					height: 22,
					id: PF+'btnLimpiarDetPos2',
					name: PF+'btnLimpiarDetPos2',
					listeners:{
						click:{
							fn:function(e){
								NS.limpiarBusqueda2(true);
							}
						}
					}
				},{
					xtype: 'button',
					text: 'Excel',
					x: 740,
					y: 510,
					width: 80,
					height: 22,
					id: PF+'btnExcelPosicion2',
					name: PF+'btnExcelPosicion2',
					listeners:{
						click:{
							fn:function(e){
								
								var regs = NS.gridDetallePosicion2.store.data.items;

								//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
								if(regs.length == 0) {
									Ext.Msg.alert('SET','No existen registros para el reporte!!');
									return;
								}
								
								var matriz = new Array();
								
								for(var i=0; i<regs.length; i++){
									var vector = {};
									vector.descBanco = regs[i].get('descBanco');
									vector.idChequera = regs[i].get('idChequera');
									vector.nomEmpresa = regs[i].get('nomEmpresa');
									vector.idRubro = regs[i].get('idRubro');
									vector.descRubro = regs[i].get('descRubro');
									vector.importe = regs[i].get('importe');
									vector.concepto = regs[i].get('concepto');
									vector.referencia = regs[i].get('referencia');
									vector.fecOperacion = NS.cambiarFecha(''+regs[i].get('fecOperacion'));
									vector.fecValor = NS.cambiarFecha(''+regs[i].get('fecValor'));
									vector.idDivisa = regs[i].get('idDivisa');
									vector.idTipoMovto = regs[i].get('idTipoMovto');
									vector.beneficiario = regs[i].get('beneficiario'); 
									vector.noDocto = regs[i].get('noDocto');
									
									matriz[i] = vector;
								}
									
								var jSonString = Ext.util.JSON.encode(matriz);
								
								mascara.show(); 
								NS.exportaExcelPosicion2(jSonString);
							}
						}
					}
				},{
					xtype: 'button',
					text: 'Cerrar',
					x: 740,
					y: 540,
					width: 80,
					height: 22,
					id: PF+'btnCerrarBusqueda2',
					name: PF+'btnCerrarBusqueda2',
					listeners:{
						click:{
							fn:function(e){
								NS.winModificaRubro.hide();
							}
						}
					}
				},
				NS.fldDetallePosicion2,
				NS.fldReclasificacion2
	 	],
	 	listeners:{
	    	 show:{
	    		 fn:function(){
	    		 }
	    	 }
	     } // End listeners	
	});
	
	NS.global = new Ext.form.FieldSet ({
		title: '',
		x: 0,
		y: 5,
		width: 1000,		
		height: 680,
		layout: 'absolute',
		items:
			[
		 		NS.fldCriterioBusqueda,
		 		NS.gridSelecciones,
		 		 {
                    xtype: 'label',
                    text: 'Fec. Inicio',
                    x: 700,
                    y: 10
                },
		 		{
               		xtype:'datefield',
               		format:'d/m/Y',
               		id:PF+'fechaIni',
               		name:PF+'fechaIni',
               		value: apps.SET.FEC_HOY,
               		x: 700,
               		y: 35,
               		width:110,
               		allowBlank: false,
               		blankText:'La fecha inicial es requerida',
               		disabled: false,
               		listeners:{
               			change:{
               				fn:function(caja,valor){
               					//NS.storeMaestro.baseParams.fecIni=cambiarFecha(''+valor);
								         		
               				}
               			}
               		}
               	},
               	{
                    xtype: 'label',
                    text: 'Fec. Fin',
                    x: 850,
                    y: 10
                },
	              {
	           		xtype:'datefield',
	           		format:'d/m/Y',
	           		id:PF+'fechaFin',
	           		name:PF+'fechaFin',
	           		value: apps.SET.FEC_HOY,
	           		x: 850,
	           		y: 35,
	           		width:110,
	           		allowBlank: false,
	           		blankText:'La fecha final es requerida',
	           		disabled: false,
	           		listeners:{
	           			change:{
	           				fn:function(caja,valor){
	           					if(Ext.getCmp(PF+'fechaIni').getValue()>valor){
									Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
									return;
								}
								//NS.storeMaestro.baseParams.fecFin=cambiarFecha(''+valor);
	           				}
	           			}
	           		}
	           	},{
                    xtype: 'label',
                    text: 'Divisa',
                    x: 700,
                    y: 65
                },{
				  	xtype: 'uppertextfield',
                    x: 700,
                    y: 80,
                    width: 40,
                    id: PF+'txtIdDivisa', 
                    name: PF+'txtIdDivisa',
                    value: '',
                    listeners:{
                    	change:{
                    		fn:function(caja){
                				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisa', NS.cmbDivisa.getId());
                        			
	                        		if(valueCombo == null || valueCombo == undefined || valueCombo == '')
	                        			Ext.getCmp(PF + 'txtIdDivisa').reset();
	                        			
                				}else
                					NS.cmbDivisa.reset();
                    		}
                    	}
                    }
			  },
	          NS.cmbDivisa,
	          NS.fldBotonesBusqueda,
	          NS.fldBotonesReportes,
	          NS.fldPlantilla,
	          NS.fldConsultas,
	          NS.fldResultados
		 	]
	});
	
	NS.PosicionBancaria = new Ext.FormPanel ({
		title: 'Consultas Posición Bancaria',
		width: 1300,
		height: 700,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'consultaPosicionBancaria',
		name: PF + 'consultaPosicionBancaria',
		renderTo: NS.tabContId,
		items: [
		 	NS.global
		]
	});
	
	
	/******************		FUNCIONES		*************************/
	NS.cambiarFecha = function (fecha){
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		var mes;
		
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
			mes = i+1;
				if(mes<10)
					mes='0'+mes;
			}
		}		
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	}
	
	/** 						Agregado EMS 28/09/15 										*
	 * CUANDO SE ABRE UNA PESTAÑA Y UNA VENTANA MODAL, SI SE CIERRA LA PESTAÑA				*
	 * LOS OBJETOS CREADOS DE LA VENTANA MODAL NO SE ELIMINAN, SI SE VUELVE ABRIR LA		*
	 * VENTANA MODAL DUPLICA N VECES LOS OBJETOS Y LA VENTANA NO FUNCIONAN LOS COMPONENTES 	**/
	
	function verificaComponentesCreados(){
		
		/**
		 * AGREGAR UN IF A ESTE METODO LOS COMPONENTES QUE SE CREEN EN VENTANAS MODALES 
		 * radios, combos, textos, grids, etc.
		 */
		
		var compt; 
		
		/***** Ventana WinBusqueda *****/
		
		compt = Ext.getCmp(PF+'btnBuscarMovtos');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'btnLimpiarDetPos');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'btnExcelPosicion');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'btnCerrarBusqueda');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		//FIELD BUSQUEDA RUBRO
		compt = NS.gridRubros;
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = NS.txtImporteIni;
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = NS.txtImporteFin;
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = NS.gridDetallePosicion;
		if(compt != null || compt != undefined ){ compt.destroy(); }
		//
		compt = Ext.getCmp(PF+'btnAceptarReclas');
		/*if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF + 'chkModificacion');*/
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'txtReferencia');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'fechaReferencia');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'txtIdGrupoRubro');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'txtIdRubro');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = NS.cmbGrupoRubro;
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = NS.cmbRubro;
		if(compt != null || compt != undefined ){ compt.destroy(); }        
		/************************************************************/
		
		/*****		MODIFICAR RUBROS	*****/
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = NS.gridDetallePosicion2;
		if(compt != null || compt != undefined ){ compt.destroy(); }
		//
		compt = Ext.getCmp(PF+'btnAceptarReclas2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'btnLimpiarDetPos2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'btnExcelPosicion2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'txtReferencia2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'fechaReferencia2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'txtIdGrupoRubro2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = Ext.getCmp(PF+'txtIdRubro2');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = NS.cmbGrupoRubro2;
		if(compt != null || compt != undefined ){ compt.destroy(); }
		compt = NS.cmbRubro2;
		if(compt != null || compt != undefined ){ compt.destroy(); }        
		/************************************************************/
		
	}
	
	//Formato de un numero a monetario
	NS.formatNumber = function(num,prefix){
		num = num.toString();
		if(num.indexOf('.')>-1){
			if(num.substring(num.indexOf('.')).length<3){
				num = num + '0';
			}
		}
		else{
			num = num + '.00';
		}
		prefix = prefix || '';
		var splitStr = num.split('.');
		var splitLeft = splitStr[0];
		var splitRight = splitStr.length > 1 ? '.' + splitStr[1] : '';
		var regx = /(\d+)(\d{3})/;
		while (regx.test(splitLeft)) {
			splitLeft = splitLeft.replace(regx, '$1' + ',' + '$2');
		}
			return prefix + splitLeft + splitRight;
	};
	
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,''); //num.replace(/([^0-9\.\-])/g,''*1);
	};
	
	NS.limpiarBusqueda = function(limpiaTodo){
		
		if(limpiaTodo){
			NS.gridRubros.store.removeAll();
			NS.gridRubros.getView().refresh();
			NS.txtImporteIni.setValue("");
			NS.txtImporteFin.setValue("");
		}
		
		NS.gridDetallePosicion.store.removeAll();
		//NS.gridDetallePosicion.getView().refresh();
		//Ext.getCmp(PF + 'chkModificacion').setValue(false);
		Ext.getCmp(PF+'txtReferencia').setValue("");
		Ext.getCmp(PF+'fechaReferencia').setValue("");
		Ext.getCmp(PF+'txtIdGrupoRubro').setValue("");
		Ext.getCmp(PF+'txtIdRubro').setValue("");
		NS.cmbGrupoRubro.reset();
		NS.cmbRubro.reset();
	};
	
	NS.limpiarBusqueda2 = function(limpiaTodo){

		NS.gridDetallePosicion2.store.removeAll();
		NS.gridDetallePosicion2.getView().refresh();
		NS.gridDetallePosicion2.store.load();
		//Ext.getCmp(PF + 'chkModificacion2').setValue(false);
		Ext.getCmp(PF+'txtReferencia2').setValue("");
		Ext.getCmp(PF+'fechaReferencia2').setValue("");
		Ext.getCmp(PF+'txtIdGrupoRubro2').setValue("");
		Ext.getCmp(PF+'txtIdRubro2').setValue("");
		NS.cmbGrupoRubro2.reset();
		NS.cmbRubro2.reset();
	};
	
	NS.buscarDetallePosicion = function(){
		var jsonBancos = '';
		var jsonChequeras = '';
		var jsonEmpresas = '';
		
		//Obtenemos los datos generales para la busqueda
		var opt = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
		var renSel = NS.gridSelecciones.getSelectionModel().getSelections();
		
		mascara.show();
		console.log("buscarDetallePosicion "+opt);
		switch(opt){
			case 1:
				//Bancos y chequeras
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos un Banco/Chequera.');
					mascara.hide();
					return;
				}
				
				var matBancos = new Array();
				var matCheq = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecBancos = {};
					var vecCheq = {};
					
					vecBancos.idBanco = renSel[i].get('idBanco');
					vecCheq.idChequera = renSel[i].get('idChequera');
					matBancos[i] = vecBancos;
					matCheq[i] = vecCheq;
				}
				
				jsonBancos = Ext.util.JSON.encode(matBancos);
				jsonChequeras = Ext.util.JSON.encode(matCheq);
				
				break;
			case 2:
				//Bancos y empresas
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos un Banco/Empresa.');
					mascara.hide();
					return;
				}
				
				var matBancos = new Array();
				var matEmp = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecBancos = {};
					var vecEmp = {};
					
					vecBancos.idBanco = renSel[i].get('idBanco');
					vecEmp.noEmpresa = renSel[i].get('noEmpresa');
					matBancos[i] = vecBancos;
					matEmp[i] = vecEmp;
				}
				
				jsonBancos = Ext.util.JSON.encode(matBancos);
				jsonEmpresas = Ext.util.JSON.encode(matEmp);
				break;
			case 3:
				//Bancos
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos un Banco.');
					mascara.hide();
					return;
				}
				
				var matBancos = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecBancos = {};
					
					vecBancos.idBanco = renSel[i].get('idBanco');
					matBancos[i] = vecBancos;
				}
				
				jsonBancos = Ext.util.JSON.encode(matBancos);
				break;
			case 4:
				//Empresas
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos una Empresa.');
					mascara.hide();
					return;
				}
				
				var matEmp = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecEmp = {};
					
					vecEmp.noEmpresa = renSel[i].get('noEmpresa');
					matEmp[i] = vecEmp;
				}
				
				jsonEmpresas = Ext.util.JSON.encode(matEmp);
				break;	
		}
		
		var divisa = Ext.getCmp(PF+'txtIdDivisa').getValue();
		if(divisa == ''){
			divisa = 'MN';
		}
		
		var fechaIni = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaIni').getValue());
		var fechaFin = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaFin').getValue());
		console.log("opcion de búsqueda"+NS.optBusqueda);
		switch(NS.optBusqueda){
	 	case 1:
	 		
	 		//Validar que haya seleccionado al menos 1 rubro
	 		var rubros = NS.gridRubros.getSelectionModel().getSelections();
	 		
	 		if(rubros.length <= 0){
	 			Ext.Msg.alert('SET','Para realizar la búsqueda debe seleccionar al menos un rubro.');
	 			mascara.hide();
				return;
	 		}
	 		
	 		var matR = new Array();
	 		
	 		for(var i = 0; i < rubros.length; i++){
	 			var vecR = {}
	 			vecR.idRubro = rubros[i].get('idRubro');
	 			matR[i] = vecR;
	 		}
	 		
	 		var jsonRubros = Ext.util.JSON.encode(matR);
	 		
	 		NS.storeDetallePosicion.baseParams.jsonRubros = jsonRubros; 
	 		NS.storeDetallePosicion.baseParams.importeIni = 0; 
	 		NS.storeDetallePosicion.baseParams.importeFin = 0; 
	 		NS.storeDetallePosicion.baseParams.jsonBancos = jsonBancos; 
	 		NS.storeDetallePosicion.baseParams.jsonChequeras = jsonChequeras; 
	 		NS.storeDetallePosicion.baseParams.jsonEmpresas = jsonEmpresas; 
	 		NS.storeDetallePosicion.baseParams.divisa = divisa; 
	 		NS.storeDetallePosicion.baseParams.fechaIni = fechaIni;
	 		NS.storeDetallePosicion.baseParams.fechaFin = fechaFin;
	 		NS.storeDetallePosicion.baseParams.optConsulta = opt;
	 		NS.storeDetallePosicion.load();
	 		break;
	 	case 2:
	 		
		 	if(Ext.getCmp(PF + 'txtImporteIni').getValue()=='' || Ext.getCmp(PF + 'txtImporteFin').getValue()==''){
		 		Ext.Msg.alert('SET','Para realizar la búsqueda por importe debe ingresar el importe inicial y el importe final.');
		 		mascara.hide();
				return;
		 	}
	 		
	 		NS.storeDetallePosicion.baseParams.jsonRubros = ''; 
	 		NS.storeDetallePosicion.baseParams.importeIni = Ext.getCmp(PF + 'txtImporteIni').getValue(); 
	 		NS.storeDetallePosicion.baseParams.importeFin = Ext.getCmp(PF + 'txtImporteFin').getValue(); 
	 		NS.storeDetallePosicion.baseParams.jsonBancos = jsonBancos; 
	 		NS.storeDetallePosicion.baseParams.jsonChequeras = jsonChequeras; 
	 		NS.storeDetallePosicion.baseParams.jsonEmpresas = jsonEmpresas; 
	 		NS.storeDetallePosicion.baseParams.divisa = divisa; 
	 		NS.storeDetallePosicion.baseParams.fechaIni = fechaIni;
	 		NS.storeDetallePosicion.baseParams.fechaFin = fechaFin;
	 		NS.storeDetallePosicion.baseParams.optConsulta = opt;
	 		NS.storeDetallePosicion.load();
	 		break;
		}
	}
	
	NS.exportaExcel = function(jsonDatos, tipoConsulta) {
		
		PosicionBancariaAction.exportaExcel(jsonDatos, tipoConsulta, function(res, e){
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				mascara.hide();
				return;
			}
			
			if(tipoConsulta == 'MovtoTes'){
				
				if (res != null && res != undefined && res == "") {
					Ext.Msg.alert('SET', "Error al generar el archivo");
				}else {
					strParams = '?nomReporte=excelMovtosTesoreria';
					strParams += '&'+'nomParam1=nomArchivo';
					strParams += '&'+'valParam1='+res;
					window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
				}
				
			}else if(tipoConsulta == 'diarioTeso'){
				
				if (res != null && res != undefined && res == "") {
					Ext.Msg.alert('SET', "Error al generar el archivo");
				}else {
					strParams = '?nomReporte=excelDiarioTesoreria';
					strParams += '&'+'nomParam1=nomArchivo';
					strParams += '&'+'valParam1='+res;
					window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
				}
				
			}else if(tipoConsulta == 'fecValorXEmp'){
				
				if (res != null && res != undefined && res == "") {
					Ext.Msg.alert('SET', "Error al generar el archivo");
				}else {
					strParams = '?nomReporte=excelFecValorXEmp';
					strParams += '&'+'nomParam1=nomArchivo';
					strParams += '&'+'valParam1='+res;
					window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
				}
				
			}else if(tipoConsulta == 'fecValorXCta'){
				
				if (res != null && res != undefined && res == "") {
					Ext.Msg.alert('SET', "Error al generar el archivo");
				}else {
					strParams = '?nomReporte=excelFecValorXCta';
					strParams += '&'+'nomParam1=nomArchivo';
					strParams += '&'+'valParam1='+res;
					window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
				}
				
			}else if(tipoConsulta == 'repSaldos'){
				
				if (res != null && res != undefined && res == "") {
					Ext.Msg.alert('SET', "Error al generar el archivo");
				}else {
					strParams = '?nomReporte=excelReporteSaldos';
					strParams += '&'+'nomParam1=nomArchivo';
					strParams += '&'+'valParam1='+res;
					window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
				}
				
			}
			
			mascara.hide();
		});
	};
	
	NS.exportaExcelPosicion = function(jsonDatos) {
		PosicionBancariaAction.exportaExcelPosicion(jsonDatos, function(res, e){
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				mascara.hide();
				return;
			}
			
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			}else {
				strParams = '?nomReporte=MovimientoPosicionBancaria';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
			mascara.hide();
		});
	};
	
	NS.exportaExcelPosicion2 = function(jsonDatos) {
		PosicionBancariaAction.exportaExcelPosicion2(jsonDatos, function(res, e){
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				mascara.hide();
				return;
			}
			
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			}else {
				strParams = '?nomReporte=MovimientoPosicionBancaria2';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
			mascara.hide();
		});
	};
	
	NS.regresaChequeras = function(){
		
		NS.jsonBancos = '';
		NS.jsonChequeras = '';
		NS.jsonEmpresas = '';
		
		//Obtenemos los datos generales para la busqueda
		var opt = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
		var renSel = NS.gridSelecciones.getSelectionModel().getSelections();
		
		
		switch(opt){
			case 1:
				//Bancos y chequeras
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos un Banco/Chequera.');
					return;
				}
				
				var matBancos = new Array();
				var matCheq = new Array();
				var matEmpresa = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecBancos = {};
					var vecCheq = {};
					var vecEmpresa = {};
					
					vecBancos.idBanco = renSel[i].get('idBanco');
					vecCheq.idChequera = renSel[i].get('idChequera');
					vecEmpresa.noEmpresa = renSel[i].get('noEmpresa');
					matBancos[i] = vecBancos;
					matCheq[i] = vecCheq;
					matEmpresa[i] = vecEmpresa;
				}
				
				NS.jsonBancos = Ext.util.JSON.encode(matBancos);
				NS.jsonChequeras = Ext.util.JSON.encode(matCheq);
				NS.jsonEmpresas = Ext.util.JSON.encode(matEmpresa);
				break;
			case 2:
				//Bancos y empresas
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos un Banco/Empresa.');
					return;
				}
				
				var matBancos = new Array();
				var matEmp = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecBancos = {};
					var vecEmp = {};
					
					vecBancos.idBanco = renSel[i].get('idBanco');
					vecEmp.noEmpresa = renSel[i].get('noEmpresa');
					matBancos[i] = vecBancos;
					matEmp[i] = vecEmp;
				}
				
				NS.jsonBancos = Ext.util.JSON.encode(matBancos);
				NS.jsonEmpresas = Ext.util.JSON.encode(matEmp);
				break;
			case 3:
				//Bancos
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos un Banco.');
					return;
				}
				
				var matBancos = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecBancos = {};
					
					vecBancos.idBanco = renSel[i].get('idBanco');
					matBancos[i] = vecBancos;
				}
				
				NS.jsonBancos = Ext.util.JSON.encode(matBancos);
				break;
			case 4:
				//Empresas
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos una Empresa.');
					return;
				}
				
				var matEmp = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecEmp = {};
					
					vecEmp.noEmpresa = renSel[i].get('noEmpresa');
					matEmp[i] = vecEmp;
				}
				
				NS.jsonEmpresas = Ext.util.JSON.encode(matEmp);
				break;	
		}
	};
	
	NS.regresaChequerasPlantilla = function(){
		
		NS.jsonBancos = '';
		NS.jsonChequeras = '';
		NS.jsonEmpresas = '';
		NS.orden = '';
		
		//Obtenemos los datos generales para la busqueda
		var opt = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
		var renSel = NS.gridSelecciones.getSelectionModel().getSelections();
		
		
		switch(opt){
			case 1:
				//Bancos y chequeras
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos un Banco/Chequera.');
					return;
				}
				
				var matBancos = new Array();
				var matCheq = new Array();
				var matEmpresa = new Array();
				var matOrden = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecBancos = {};
					var vecCheq = {};
					var vecEmpresa = {};
					var vecOrden = {};
					
					vecBancos.idBanco = renSel[i].get('idBanco');
					vecCheq.idChequera = renSel[i].get('idChequera');
					vecEmpresa.noEmpresa = renSel[i].get('noEmpresa');
					vecOrden.orden = renSel[i].get('orden');
					
					matBancos[i] = vecBancos;
					matCheq[i] = vecCheq;
					matEmpresa[i] = vecEmpresa;
					matOrden[i] = vecOrden;
				}
				
				NS.jsonBancos = Ext.util.JSON.encode(matBancos);
				NS.jsonChequeras = Ext.util.JSON.encode(matCheq);
				NS.jsonEmpresas = Ext.util.JSON.encode(matEmpresa);
				NS.jsonOrden = Ext.util.JSON.encode(matOrden);
				
				break;
			case 2:
				//Bancos y empresas
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos un Banco/Empresa.');
					return;
				}
				
				var matBancos = new Array();
				var matEmp = new Array();
				var matOrden = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecBancos = {};
					var vecEmp = {};
					var vecOrden = {};
					
					vecBancos.idBanco = renSel[i].get('idBanco');
					vecEmp.noEmpresa = renSel[i].get('noEmpresa');
					vecOrden.orden = renSel[i].get('orden');
					
					matBancos[i] = vecBancos;
					matEmp[i] = vecEmp;
					matOrden[i] = vecOrden;
				}
				
				NS.jsonBancos = Ext.util.JSON.encode(matBancos);
				NS.jsonEmpresas = Ext.util.JSON.encode(matEmp);
				NS.jsonOrden = Ext.util.JSON.encode(matOrden);
				break;
			case 3:
				//Bancos
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos un Banco.');
					return;
				}
				
				var matBancos = new Array();
				var matOrden = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecBancos = {};
					var vecOrden = {};
					
					vecBancos.idBanco = renSel[i].get('idBanco');
					vecOrden.orden = renSel[i].get('orden');
					
					matBancos[i] = vecBancos;
					matOrden[i] = vecOrden;
				}
				
				NS.jsonBancos = Ext.util.JSON.encode(matBancos);
				NS.jsonOrden = Ext.util.JSON.encode(matOrden);
				break;
			case 4:
				//Empresas
				if(renSel.length <= 0){
					Ext.Msg.alert('SET','Es necesario seleccionar al menos una Empresa.');
					return;
				}
				
				var matEmp = new Array();
				var matOrden = new Array();
				
				for(var i=0; i<renSel.length;i++){
					var vecEmp = {};
					var vecOrden = {};
					
					vecEmp.noEmpresa = renSel[i].get('noEmpresa');
					vecOrden.orden = renSel[i].get('orden');
					
					matEmp[i] = vecEmp;
					matOrden[i] = vecOrden;
				}
				
				NS.jsonEmpresas = Ext.util.JSON.encode(matEmp);
				NS.jsonOrden = Ext.util.JSON.encode(matOrden);
				break;	
		}
		
	};

	/*********************************************************************************/
	
	NS.clickMovtoTes = function(){
		
		var regs = NS.gridSelecciones.getSelectionModel().getSelections();
		
		if(regs.length <= 0){
			Ext.Msg.alert('SET','Es necesario seleccionar algún registro para la búsqueda.');
			return;
		}
		
		mascara.show();
		
		NS.consulta = 'MovtoTes';
		
		//obtiene los bancos, chequeras y empresas seleccionadas en las variables globales.
		NS.regresaChequeras();
		
		var divisa = Ext.getCmp(PF+'txtIdDivisa').getValue();
		if(divisa == ''){
			divisa = 'MN';
		}
		
		var fechaIni = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaIni').getValue());
		var fechaFin = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaFin').getValue());
		
		var seleccion = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
		mascara.show();
		
		NS.storeMovtoTes.baseParams.jsonBancos = NS.jsonBancos;
		NS.storeMovtoTes.baseParams.jsonChequeras = NS.jsonChequeras;
		NS.storeMovtoTes.baseParams.jsonEmpresas = NS.jsonEmpresas;
		NS.storeMovtoTes.baseParams.fechaIni = fechaIni;
		NS.storeMovtoTes.baseParams.fechaFin = fechaFin;
		NS.storeMovtoTes.baseParams.seleccion = seleccion;
		NS.storeMovtoTes.baseParams.divisa = divisa;
		
		NS.storeMovtoTes.load();
		
	};
	
	/****	CLICK DIARIOTESO	*****/
	
	NS.clickDiarioTeso = function(){
		
		var regs = NS.gridSelecciones.getSelectionModel().getSelections();
		
		if(regs.length <= 0){
			Ext.Msg.alert('SET','Es necesario seleccionar algún registro para la búsqueda.');
			return;
		}
		
		mascara.show();
		
		NS.consulta = 'diarioTeso';
		
		//obtiene los bancos, chequeras y empresas seleccionadas en las variables globales.
		NS.regresaChequeras();
		
		var divisa = Ext.getCmp(PF+'txtIdDivisa').getValue();
		if(divisa == ''){
			divisa = 'MN';
		}
		
		var fechaIni = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaIni').getValue());
		var fechaFin = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaFin').getValue());
		
		var seleccion = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
		mascara.show();
		
		NS.storeDiarioTes.baseParams.jsonBancos = NS.jsonBancos;
		NS.storeDiarioTes.baseParams.jsonChequeras = NS.jsonChequeras;
		NS.storeDiarioTes.baseParams.jsonEmpresas = NS.jsonEmpresas;
		NS.storeDiarioTes.baseParams.fechaIni = fechaIni;
		NS.storeDiarioTes.baseParams.fechaFin = fechaFin;
		NS.storeDiarioTes.baseParams.seleccion = seleccion;
		NS.storeDiarioTes.baseParams.divisa = divisa;
		
		NS.storeDiarioTes.load();
		
	};
	
	
	/*****	click fecValorXEmp	*****/
	
	NS.clickFecValorXEmp = function(){
		var regs = NS.gridSelecciones.getSelectionModel().getSelections();
		
		if(regs.length <= 0){
			Ext.Msg.alert('SET','Es necesario seleccionar algún registro para la búsqueda.');
			return;
		}
		
		mascara.show();
		
		NS.consulta = 'fecValorXEmp';
		
		NS.tipoReporte = 1;
		
		NS.regresaChequeras();  
		
		var divisa = Ext.getCmp(PF+'txtIdDivisa').getValue();
		if(divisa == ''){
			divisa = 'MN';
		}
		
		var fechaIni = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaIni').getValue());
		var fechaFin = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaFin').getValue());
		
		if(fechaIni == '' || fechaFin == ''){
			Ext.Msg.alert('SET','Seleccione un rago de fechas válido.');
			return;
		}
		
		var seleccion = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
		var chkSaldos = false;
		
		/*if(fechaIni != NS.fecHoy && fechaFin != NS.fecHoy){
			chkSaldos = false;
		}*/
		
		NS.storeValorXEmp.baseParams.jsonBancos = NS.jsonBancos;
		NS.storeValorXEmp.baseParams.jsonChequeras = NS.jsonChequeras;
		NS.storeValorXEmp.baseParams.jsonEmpresas = NS.jsonEmpresas;
		NS.storeValorXEmp.baseParams.fechaIni = fechaIni;
		NS.storeValorXEmp.baseParams.fechaFin = fechaFin;
		NS.storeValorXEmp.baseParams.seleccion = seleccion;
		NS.storeValorXEmp.baseParams.divisa = divisa;
		NS.storeValorXEmp.baseParams.tipoReporte = NS.tipoReporte;
		
		NS.storeValorXEmp.load();
	};
	
	/*****	clickFecValorXCta	*****/
	
	NS.clickFecValorXCta = function(){
		var regs = NS.gridSelecciones.getSelectionModel().getSelections();
		
		if(regs.length <= 0){
			Ext.Msg.alert('SET','Es necesario seleccionar algún registro para la búsqueda.');
			return;
		}
		
		mascara.show();
		
		NS.consulta = 'fecValorXCta';
		
		NS.tipoReporte = 2;
		
		NS.regresaChequeras();  
		
		var divisa = Ext.getCmp(PF+'txtIdDivisa').getValue();
		if(divisa == ''){
			divisa = 'MN';
		}
		
		var fechaIni = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaIni').getValue());
		var fechaFin = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaFin').getValue());
		
		if(fechaIni == '' || fechaFin == ''){
			Ext.Msg.alert('SET','Seleccione un rago de fechas válido.');
			return;
		}
		
		var seleccion = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;
		
		NS.storeValorXEmp.baseParams.jsonBancos = NS.jsonBancos;
		NS.storeValorXEmp.baseParams.jsonChequeras = NS.jsonChequeras;
		NS.storeValorXEmp.baseParams.jsonEmpresas = NS.jsonEmpresas;
		NS.storeValorXEmp.baseParams.fechaIni = fechaIni;
		NS.storeValorXEmp.baseParams.fechaFin = fechaFin;
		NS.storeValorXEmp.baseParams.seleccion = seleccion;
		NS.storeValorXEmp.baseParams.divisa = divisa;
		NS.storeValorXEmp.baseParams.tipoReporte = NS.tipoReporte;
		
		NS.storeValorXEmp.load();
	};
	
	/*****	clickRepSaldos	*****/
	NS.clickRepSaldos = function(){
		
		var regs = NS.gridSelecciones.getSelectionModel().getSelections();
		
		if(regs.length <= 0){
			Ext.Msg.alert('SET','Es necesario seleccionar algún registro para la búsqueda.');
			return;
		}
		
		mascara.show();
		
		NS.consulta = 'repSaldos';
		
		NS.tipoReporte = 3;
		
		NS.regresaChequeras();  
		
		var divisa = Ext.getCmp(PF+'txtIdDivisa').getValue();
		if(divisa == ''){
			divisa = 'MN';
		}
		
		var fechaIni = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaIni').getValue());
		var fechaFin = NS.cambiarFecha(''+ Ext.getCmp(PF+'fechaFin').getValue());
		
		if(fechaIni == '' || fechaFin == ''){
			Ext.Msg.alert('SET','Seleccione un rago de fechas válido.');
			return;
		}
		
		var seleccion = NS.storeConsultarCriterioSeleccion.baseParams.idCriterio;		
		
		NS.storeValorXEmp.baseParams.jsonBancos = NS.jsonBancos;
		NS.storeValorXEmp.baseParams.jsonChequeras = NS.jsonChequeras;
		NS.storeValorXEmp.baseParams.jsonEmpresas = NS.jsonEmpresas;
		NS.storeValorXEmp.baseParams.fechaIni = fechaIni;
		NS.storeValorXEmp.baseParams.fechaFin = fechaFin;
		NS.storeValorXEmp.baseParams.seleccion = seleccion;
		NS.storeValorXEmp.baseParams.divisa = divisa;
		NS.storeValorXEmp.baseParams.tipoReporte = NS.tipoReporte;
		
		NS.storeValorXEmp.load();
	};
	
	
	/**********************************************************************************/
	
	/**********************		TERMINA FUNCIONES		*************************/
	
	NS.PosicionBancaria.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});